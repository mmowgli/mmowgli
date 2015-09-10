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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.pii.EmailPii;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
/*
 * Program:      MMOWGLI
 * Filename:     PasswordResetPopup.java
 * Author(s):    Terry Norbraten
 *               http://www.nps.edu and http://www.movesinstitute.org
 * Created on:   Created on Jan 23, 2014 11:10:11 AM
 * Description:  Popup to initiate a forgot password reset process
 */
import edu.nps.moves.mmowgli.markers.HibernateUpdate;
import edu.nps.moves.mmowgli.markers.HibernateUserUpdate;

/**
 * Allow a registered user to reset their forgotten password
 * 
 * @author <a href="mailto:tdnorbra@nps.edu?subject=edu.nps.moves.mmowgli.modules.registrationLogin.PasswordResetPopupListener">Terry Norbraten, NPS MOVES</a>
 * @version $Id: PasswordResetPopup.java 3305 2014-02-01 00:02:34Z tdnorbra $
 */
public class PasswordResetPopup extends Window implements Button.ClickListener
{
  private static final long serialVersionUID = 353792651604998559L;
  private TextField userIDTf, emailTf;

  @SuppressWarnings("serial")
  public PasswordResetPopup(Button.ClickListener listener, String uname)
  {
    setCaption("Password Reset");
    VerticalLayout vLay = new VerticalLayout();
    vLay.setSpacing(true);
    vLay.setMargin(true);
    setContent(vLay);
    
    vLay.addComponent(new HtmlLabel("<center>Please fill in your player name and/or email address<br/>to initiate a password reset.</center>"));
    // Use an actual form widget here for data binding and error display.
    FormLayout formLay = new FormLayout();
    formLay.setSizeUndefined();
    formLay.setSpacing(true);
    formLay.addStyleName("m-login-form"); // to allow styling contents (v-textfield)
    vLay.addComponent(formLay);
    vLay.setComponentAlignment(formLay, Alignment.TOP_CENTER);

    formLay.addComponent(userIDTf = new TextField("Player name:"));
    userIDTf.addStyleName("m-dialog-textfield");
    userIDTf.setWidth("85%");
    userIDTf.setTabIndex(100);

    // Help out a little here
    userIDTf.setValue(uname==null?"":uname);
    formLay.addComponent(emailTf = new TextField("Email:"));
    emailTf.addStyleName("m-dialog-textfield");
    emailTf.setWidth("85%");
    emailTf.setTabIndex(101);

    HorizontalLayout hl = new HorizontalLayout();
    hl.setSpacing(true);
    hl.setMargin(false);
    hl.setWidth("100%");
    vLay.addComponent(hl);

    Label lab;
    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);
    Button cancelButt = new Button("Cancel");
    hl.addComponent(cancelButt);
    cancelButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        getUI().setScrollTop(0);
        getUI().removeWindow(PasswordResetPopup.this);
      }
    });
    cancelButt.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);

    Button continueButt = new Button("Continue");
    hl.addComponent(continueButt);
    continueButt.addClickListener(PasswordResetPopup.this);
    continueButt.setClickShortcut(ShortcutAction.KeyCode.ENTER);

    emailTf.focus();
  }

  // Lots of stuff borrowed from RegistrationPagePopupFirst
  @Override
  public void buttonClick(Button.ClickEvent event)
  {
    HSess.init();
    handleNameAndOrEmailTL_1(event);
    HSess.close();
  }

  private void handleNameAndOrEmailTL_1(Button.ClickEvent event)
  {
    String uname = userIDTf.getValue().trim();
    String email = emailTf.getValue().trim();
    if(uname.isEmpty() && email.isEmpty()) {
      errorOut("Specify player name and/or email address.");
      return;
    }
    Session piiSess = VHibPii.getASession();
    // First, if user entered gname, no matter if an email was entered, use it.  There can be multiple users sharing
    // an email, but not multiple email per user

    if(!uname.isEmpty())
      proceedWithNameTL_2a(uname,piiSess);
    else
      proceedWithEnteredEmailTL_2b(piiSess,null);

    piiSess.close();
  }
  
  private void proceedWithNameTL_2a(String uname, Session piiSess)
  {
    User usr = User.getUserWithUserNameTL(uname);
    if(usr != null) {
       UserPii uPii = VHibPii.getUserPii(usr.getId(), piiSess, false);
       List<EmailPii> ePii = uPii.getEmailAddresses(); 
       finalConfirmationCheckTL_4(ePii.get(0).getAddress(),usr);  //  @HibernateUserUpdate
    }
    else
      proceedWithEnteredEmailTL_2b(piiSess,"No player found."); //  @HibernateUserUpdate
  }   
  
  private void proceedWithEnteredEmailTL_2b(Session piiSess, String errorStr)
  {
    String email = emailTf.getValue().trim();
    if(email == null || email.length()<=0) {
      errorOut(errorStr != null? errorStr:"No email specified.");
      return;
    }
    checkValidEmailTL_3(email,piiSess); //  @HibernateUserUpdate
  }
  
  private void checkValidEmailTL_3(String email, Session piiSess)
  {
    // Email address has ampersand

    EmailValidator v = new EmailValidator("");
    if (email == null || !v.isValid(email)) {
      errorOut("Invalid email address.");
      return;
    }

    // Check that is in DB
    if (RegistrationPagePopupFirst.checkEmail(email,piiSess)) {
      errorOut("Email address not found."); // in database for user: " + user.getUserName() + ".");
      return;
    }
    
    ArrayList<User> aLis =  UserPii.getUserFromEmailTL(email, piiSess);
    if(aLis.isEmpty()) {
      errorOut("Player not associated with email "+email);
      return;    
    }
      
    // Check for disabled account only if there's one user.  Gets too complicated otherwise, and the unlikely chance that
    // a multi-user accounted situation exists where one or all are disabled will still not pass login.

    // Check user account status
    if(aLis.size() == 1 && aLis.get(0).isAccountDisabled()) {
      errorOut("This account has been disabled.");
      return;
    }
    // otherwise, proceed
    finalConfirmationCheckTL_4(email, aLis);  //  @HibernateUserUpdate
  }
  
  //Check user email confirmation status
  private void finalConfirmationCheckTL_4(String email, User u)
  {
    ArrayList<User> aLis = new ArrayList<User>();
    aLis.add(u);
    finalConfirmationCheckTL_4(email,aLis); //  @HibernateUserUpdate
  }

  @HibernateUpdate
  @HibernateUserUpdate
  private void finalConfirmationCheckTL_4(String email, ArrayList<User> aLis)
  {
    ArrayList<User> confirmedUsers = new ArrayList<User>(aLis); // working list
    Game g = Game.getTL();
    boolean confirmationOn = g.isEmailConfirmation();
    if (confirmationOn) {
      Iterator<User> itr = aLis.iterator();
      while (itr.hasNext()) {
        User u = itr.next();
        if (!u.isEmailConfirmed()) {
          confirmedUsers.remove(u);
        }
      }
    }
    if (confirmedUsers.isEmpty()) {
      errorOut("This email address has not yet been confirmed.");
      return;
    }

    // did not fail confirm check; if confirmation off, make sure they can get in in the future or questions will arise
    if (!confirmationOn) {
      Iterator<User> itr = confirmedUsers.iterator();
      while (itr.hasNext()) {
        User us = itr.next();

        us.setEmailConfirmed(true);
        User.updateTL(us);
      }
    }
    
    makeResetAnnounceDialogTL_5(email,confirmedUsers);
  }

  private void makeResetAnnounceDialogTL_5(String email, ArrayList<User>aLis)
  {
    UI myUI = getUI();
    myUI.removeWindow(PasswordResetPopup.this);

    final Window resetAnnounceDialog = new Window("Password Reset Announcement");
    resetAnnounceDialog.setModal(true);
    resetAnnounceDialog.setClosable(false);
    VerticalLayout vLay = new VerticalLayout();
    resetAnnounceDialog.setContent(vLay);
    vLay.setMargin(true);
    vLay.setSpacing(true);
    vLay.setSizeUndefined();
    vLay.setWidth("400px");

    Label message = new HtmlLabel("An email has been sent to <b>" + email + "</b>.");
    vLay.addComponent(message);

    message = new Label("Follow the link in the message to confirm your password reset request to enable login to your mmowgli player account.");
    vLay.addComponent(message);

    message = new Label("Please be advised that you will only have three hours to complete this process, after which time "
                      + "you will have to re-initiate a new password reset process from the game login page.");
    vLay.addComponent(message);

    message = new HtmlLabel("Now, press <b>Homepage -- Return to login</b> after receiving a reset request confirmation email.");
    vLay.addComponent(message);

    @SuppressWarnings("serial")
    Button laterButt = new Button("Homepage -- Return to login", new Button.ClickListener()
    {
      @Override
      public void buttonClick(Button.ClickEvent event)
      {
        HSess.init();
        Mmowgli2UI.getAppUI().quitAndGoTo(GameLinks.getTL().getGameHomeUrl());
        HSess.close();
      }
    });
    vLay.addComponent(laterButt);

    @SuppressWarnings("serial")
    Button troubleButt = new Button("Send trouble report", new Button.ClickListener()
    {
      @Override
      public void buttonClick(Button.ClickEvent event)
      {
        HSess.init();
        Mmowgli2UI.getAppUI().quitAndGoTo(GameLinks.getTL().getTroubleLink());
        HSess.close();
      }
    });
    vLay.addComponent(troubleButt);

    myUI.addWindow(resetAnnounceDialog);
    resetAnnounceDialog.center();

    // This process generates unique uId for th3 reset process that will
    // need to be confirmed once the user receives a confirmation email and
    // click on the link containing the uId
    Iterator<User> itr = aLis.iterator();
    // sends email to all user accounts (which are at the same email address)
    // if a game name was entered, only that account receives the email
    while(itr.hasNext()) {
      User usr = itr.next();
      PasswordReset pr = new PasswordReset(usr);
      PasswordReset.saveTL(pr);

      String confirmUrl = buildConfirmUrl(pr);
      AppMaster.instance().getMailManager().sendPasswordResetEmailTL(email, usr.getUserName(), confirmUrl);
    }
  }

  private String buildConfirmUrl(PasswordReset pr)
  {
    StringBuilder sb = new StringBuilder();
    String gameUrl = AppMaster.instance().getAppUrl().toExternalForm();
    sb.append(gameUrl);
    if (!gameUrl.endsWith("/")) {
      sb.append('/');
    }
    sb.append("password?uid=");
    sb.append(pr.getResetCode());

    return sb.toString();
  }

  private void errorOut(String s)
  {
    Notification.show("Could not process password reset", s, Notification.Type.ERROR_MESSAGE);
  }

}
