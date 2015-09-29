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

import static edu.nps.moves.mmowgli.MmowgliConstants.NEWUSER_CREATION_LOGS;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.CACManager.CACData;
import edu.nps.moves.mmowgli.components.AvatarPanel;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.utility.FocusHack;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * RegistrationPagePopupFirst.java Created on Nov 29, 2010
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RegistrationPagePopupFirstB extends MmowgliDialog
{
  private static final long serialVersionUID = 1631805613131285670L;
  
  public TextField firstNameTf, lastNameTf;
  private FormLayout formLay;

  private AvatarPanel chooser;
  private NativeButton continueButt;
  
  private Long localUserId = null;  // what gets returned
  
  @HibernateSessionThreadLocalConstructor
  public RegistrationPagePopupFirstB(ClickListener listener, Long uId)
  {
    super(listener);
    super.initGui();
    localUserId = uId;
    
    setTitleString("The following is not revealed to other players.",true); //smaller

    contentVLayout.setSpacing(true);

    Label lab;
    contentVLayout.addComponent(lab = new Label());
    lab.setHeight("10px");


     // Use an actual form widget here for data binding and error display.
    formLay = new FormLayout();
    formLay.setSizeUndefined();
    formLay.addStyleName("m-login-form");  // to allow styling contents (v-textfield)
    contentVLayout.addComponent(formLay);
    contentVLayout.setComponentAlignment(formLay, Alignment.TOP_CENTER);
    
    formLay.addComponent(firstNameTf = new TextField("First name *"));
    firstNameTf.setColumns(27); // sets width
    firstNameTf.setInputPrompt("optional");
    // firstNameTf.setRequired(true);
    // firstNameTf.setRequiredError("We really need a location.");

    formLay.addComponent(lastNameTf = new TextField("Last name *"));
    lastNameTf.setColumns(27); // sets width
    lastNameTf.setInputPrompt("optional");
    // lastNameTf(true);
    // lastNameTf("We really need a location.");
    
    contentVLayout.addComponent(lab = new Label("* private information (encrypted in database)"));
    lab.addStyleName("m-dialog-text");
    lab.setWidth(null);  // makes it undefined so it's not 100%
    contentVLayout.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

    contentVLayout.addComponent(lab = new Label());
    lab.setHeight("5px");
    
    HorizontalLayout hl;
    contentVLayout.addComponent(hl=new HorizontalLayout());
    hl.setMargin(false);

    hl.addComponent(lab=new Label());
    lab.setWidth("50px");
    hl.addComponent(lab = new Label("Choose an avatar image:"));
    lab.addStyleName("m-dialog-text"); //"m-dialog-label");

    chooser = new AvatarPanel(null); // no initselected
    chooser.setWidth("500px"); //"470px"); // doesn't work well w/ relative width 470=min for displaying 4 across of size below
  //  chooser.setHeight("130px"); // 125 enough for mac to show complete image plus bottom scrollbar, IE 7 will ALWAYS show vert scroller
    // todo, check commented-out line on windows...works well on new macs.
    chooser.initGui();
    contentVLayout.addComponent(chooser);
    contentVLayout.setComponentAlignment(chooser, Alignment.TOP_CENTER);
    chooser.setSelectedAvatarIdx(0); // choose the first one just so something is chosen

    hl = new HorizontalLayout();
    hl.setWidth("100%");
    contentVLayout.addComponent(hl);

    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);

    continueButt = new NativeButton(null);
    continueButt.setStyleName("m-continueButton");
    //NativeButton continueButt = new NativeButton();
    hl.addComponent(continueButt);
    //app.globs().mediaLocator().decorateDialogContinueButton(continueButt);
    continueButt.addClickListener(new MyContinueListener());
    continueButt.setClickShortcut(KeyCode.ENTER);

    hl.addComponent(lab = new Label());
    lab.setWidth("15px");
    
    
    // if this is a cac-based registration, initialize the tf's with the cac card.
    // then, if the cac values are require to be used, mark the tf's as read-only
    CACData cData = Mmowgli2UI.getGlobals().getCACInfo();
    if(CACManager.isCacPresent(cData)) {
      Game g = Game.getTL();
      boolean force = g.isEnforceCACdataRegistration();
    
      String s;
      if((s = CACManager.getFirstName(cData))!=null) {
        firstNameTf.setValue(s);
        firstNameTf.setReadOnly(force);
      }
      if((s = CACManager.getLastName(cData))!=null) {
        lastNameTf.setValue(s);
        lastNameTf.setReadOnly(force);
      }
      /*
      if((s = CACManager.getEmail(cData))!=null) {
        emailTf.setValue(s);
        emailTf.setReadOnly(force);
      }
      */
    }
    
    firstNameTf.focus();  // should do it
    FocusHack.focus(firstNameTf);  // this does
  }
  
  @SuppressWarnings("serial")
  class MyContinueListener implements Button.ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    @HibernateUpdate
    @HibernateUserUpdate
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      MSysOut.println(NEWUSER_CREATION_LOGS,"Real name, avatar, dialog continue clicked");
      
      // 7. Something entered for first and last name
      String fName = firstNameTf.getValue().toString().trim();
      String lName = lastNameTf.getValue().toString().trim();
/*      if(fName.length() <= 0 || lName.length() <= 0) {
        errorOut("Real name fields must both be entered");
        return;
      }
 */
      UserPii uPii = VHibPii.getUserPii(localUserId);
      uPii.setUserObjectId(localUserId);
      uPii.setRealFirstName(fName);
      uPii.setRealLastName(lName);
      //String hashedPassword = new StrongPasswordEncryptor().encryptPassword(pw);
     // uPii.setPassword(hashedPassword);
      VHibPii.update(uPii);

      User _usr = User.getTL(localUserId);
      _usr.setAvatar(Avatar.getTL(chooser.getSelectedAvatarId()));
      User.updateTL(_usr);

      VHibPii.markInGame(_usr);
      HSess.close();
      
      MSysOut.println(NEWUSER_CREATION_LOGS,"Real name, avatar dialog SUCCEEDED: new user "+ _usr.getUserName()+"updated in database");
      continueButt.setEnabled(true);
      
      listener.buttonClick(event);
    }
  }
  
  @Override
  public Long getUserId()
  {
    return localUserId;
  }

  @Override
  public void setUser(User u)
  {
    localUserId = (u==null?null:u.getId());
  }

  // Used to center the dialog
  public int getUsualWidth()
  {
    return 585;
  }
}
