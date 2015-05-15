/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.modules.registrationlogin;

import static edu.nps.moves.mmowgli.MmowgliConstants.LOGIN_CONTINUE_BUTTON;
import static edu.nps.moves.mmowgli.MmowgliConstants.USER_NAME_TEXTBOX;
import static edu.nps.moves.mmowgli.MmowgliConstants.USER_PASSWORD_TEXTBOX;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.db.pii.EmailPii;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.*;
/**
 * LoginPopup.java Created on Dec 15, 2010
 * Updated Mar 6, 2014 Vaadin 7
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: LoginPopup.java 3308 2014-02-03 20:15:16Z tdnorbra $
 */
public class LoginPopup extends MmowgliDialog
{
  private static final long serialVersionUID = -5011993698392685409L;

  Label header;

  private TextField userIDTf;
  private PasswordField passwordTf;
  private NativeButton continueButt, pwResetButt;

  Long userID; // what gets returned
  
  @HibernateSessionThreadLocalConstructor
  public LoginPopup(Button.ClickListener listener)
  {
    this(listener,false);
  }

  public LoginPopup(Button.ClickListener listener, boolean guest)
  {
    super(listener);
    super.initGui();
    if(guest) {
      @SuppressWarnings("unchecked")
      List<User> lis = (List<User>)HSess.get().createCriteria(User.class).
                       add(Restrictions.eq("viewOnly", true)).
                       add(Restrictions.eq("accountDisabled", false)).list();
      if(lis.size()>0) {
        for(User u : lis) {
          if(u.getUserName().toLowerCase().equals("guest")) {
            userID = u.getId();
            return;
          }
        }
      }
      // If here, the guest logon is enabled, but no userID named guest is marked "viewOnly", continue and let
      // caller realize what happened
    }
    setTitleString("Sign in please.");

    contentVLayout.setSpacing(true);
    Label lab = new Label();
    lab.setHeight("20px");
    contentVLayout.addComponent(lab);
        
    VerticalLayout lay = new VerticalLayout();   
    contentVLayout.addComponent(lay);
    contentVLayout.setComponentAlignment(lay, Alignment.TOP_CENTER);

    lay.addComponent(lab=new Label("Player name:"));
    lab.addStyleName("m-dialog-label");
    
    lay.addComponent(userIDTf = new TextField());
    userIDTf.setColumns(35);
    userIDTf.setTabIndex(100);
    userIDTf.setId(USER_NAME_TEXTBOX);
    userIDTf.addStyleName("m-dialog-entryfield");
    
    lay.addComponent(lab = new Label());
    lab.setHeight("15px");
    
    lay.addComponent(lab=new Label("Password:"));
    lab.addStyleName("m-dialog-label");
    
    lay.addComponent(passwordTf = new PasswordField());
    passwordTf.setColumns(35);
    passwordTf.setTabIndex(101);
    passwordTf.setId(USER_PASSWORD_TEXTBOX);  
    passwordTf.addStyleName("m-dialog-entryfield");

    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth("100%");
    contentVLayout.addComponent(hl);

    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);

    continueButt = new NativeButton();
    continueButt.setId(LOGIN_CONTINUE_BUTTON);
    hl.addComponent(continueButt);
    Mmowgli2UI.getGlobals().mediaLocator().decorateDialogContinueButton(continueButt);

    continueButt.addClickListener(new MyContinueListener());
    continueButt.setClickShortcut(KeyCode.ENTER);

    // Password reset
    HorizontalLayout h2 = new HorizontalLayout();
    h2.setWidth("100%");
    contentVLayout.addComponent(h2);

    h2.addComponent(lab = new Label());
    h2.setExpandRatio(lab, 01.0f);
    pwResetButt = new NativeButton("Forgot password or player name?");
    pwResetButt.addStyleName("m-signin-forgotButton");
    h2.addComponent(pwResetButt);

    pwResetButt.addClickListener(new MyForgotLoginInfoListener());

    userIDTf.focus();
  }

  // Used to center the dialog
  public int getUsualWidth()
  {
    return 580; // px
  }

  @SuppressWarnings("serial")
  class MyContinueListener implements Button.ClickListener
  {
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      
      String uname = userIDTf.getValue().toString();
      User luser = User.getUserWithUserNameTL(uname);
      if(luser == null) {
        errorOut("No registered player with that name / ID");
        HSess.close();
        return;
      }

      String enteredPassword = passwordTf.getValue().toString();

      StrongPasswordEncryptor pwEncryptor = new StrongPasswordEncryptor();
      try {
        UserPii upii = VHibPii.getUserPii(luser.getId());

        if(!pwEncryptor.checkPassword(enteredPassword,upii.getPassword())) { //userID.getPassword())) {
          errorOut("Password does not match.  Try again.");
          passwordTf.focus();
          passwordTf.selectAll();
          HSess.close();
          return;
        }
      }
      catch(Throwable t) {
        errorOut("Password error. Try again.");
        passwordTf.focus();
        passwordTf.selectAll();
        HSess.close();
        return;
      }

      if(luser.isAccountDisabled()) {
        errorOut("This account has been disabled.");
        HSess.close();
        return;
      }

      Game g = Game.getTL();
      MovePhase mp = g.getCurrentMove().getCurrentMovePhase();

      if(g.isEmailConfirmation() && !luser.isEmailConfirmed()) {
        errorOut("This email address has not been confirmed.");
        HSess.close();
        return;
      }
      else {
        // did not fail confirm check; if confirmation off, make sure they can get in in the future or questions will arise
        luser.setEmailConfirmed(true);
        userID = luser.getId();
        User.updateTL(luser);
      }
      /* replaced with clause below it
      if(!g.isLoginAllowAll()) {
        String errorMsg = checkLoginPermissions(g,userID);
        if(errorMsg != null) {
          errorOut(errorMsg);
          return;
        }
      }
      */
      loginPermissions: {
        if (!mp.isLoginAllowAll()) {
          if (mp.isLoginAllowRegisteredUsers())
            break loginPermissions;
          if (luser.isAdministrator() && mp.isLoginAllowGameAdmins())
            break loginPermissions;
          if (luser.isGameMaster() && mp.isLoginAllowGameMasters())
            break loginPermissions;
          if (luser.isDesigner() && mp.isLoginAllowGameDesigners())
            break loginPermissions;
          if (luser.isViewOnly() && mp.isLoginAllowGuests())
            break loginPermissions;
          if(mp.isLoginAllowVIPList() && isOnList(luser))
            break loginPermissions;

          // ok, not allowing everybody in and didn't match any special cases
          errorOut("<center><br/>Sorry.  Logins are currently restricted.  If you think<br/>you should have permission, please "+
          "click<br/>\"Trouble signing in?\" to send us a Trouble Report.</center>");
          HSess.close();
          return;
        }
      }
      
      HSess.close();
      listener.buttonClick(event); // back up the chain
    }

    private boolean isOnList(User u)
    {
      EmailPii ePii = VHibPii.getUserPiiEmail(u.getId());
      return Vips.isVipOrVipDomain(ePii.getAddress());     
    }
    
    private void errorOut(String s)
    {
      new Notification("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Could not log in.", s, Notification.Type.ERROR_MESSAGE,true).show(Page.getCurrent());
    }
  }

  @SuppressWarnings("serial")
  class MyForgotLoginInfoListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {      
      String uname = userIDTf.getValue().toString();

      UI ui = Mmowgli2UI.getGlobals().getFirstUI();
      ui.removeWindow(LoginPopup.this);
      PasswordResetPopup pwp = new PasswordResetPopup(listener, uname);
      ui.addWindow(pwp);
      pwp.center();        
    }
  }

  @SuppressWarnings("serial")
  class MyOldForgotPasswordListener implements Button.ClickListener
  {
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateRead
    @HibernateClosed
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      String uname = userIDTf.getValue().toString();
      User luser = User.getUserWithUserNameTL(uname);

      if (luser == null) {
        errorOut("No registered player with that name / ID");
        HSess.close();
        return;
      }

      // This is necessary to receive an email to activate your registration
      Mmowgli2UI.getGlobals().setUserID(luser);

      // Lots of stuff borrowed from RegistrationPageBase
      if (event.getButton() == pwResetButt) {
        UI ui = Mmowgli2UI.getGlobals().getFirstUI();
        ui.removeWindow(LoginPopup.this);

        PasswordResetPopup pwp = new PasswordResetPopup(listener, luser.getUserName());
        ui.addWindow(pwp);
        pwp.center();        
      }
      HSess.close();
    }

    private void errorOut(String s)
    {
      Notification.show("Could not initiate password reset", s, Notification.Type.ERROR_MESSAGE);
    }
  }

  @Override
  protected void cancelClickedTL(ClickEvent event)
  {
    userID = null;
    super.cancelClickedTL(event);
  }

  /**
   * @return the userID or null if canceled
   */
  @Override
  public Long getUserId()
  {
    return userID;
  }

  // used by parent class when cancel is hit
  @Override
  public void setUser(User u)
  {
    userID = (u==null?null:u.getId());
  }
}
