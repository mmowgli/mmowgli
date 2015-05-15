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

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.components.BoundAffiliationCombo;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.Affiliation;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

import static edu.nps.moves.mmowgli.MmowgliConstants.NEWUSER_CREATION_LOGS;

/**
 * RegistrationPagePopupFirst.java
 * Created on Nov 29, 2010
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RegistrationPagePopupSecond extends MmowgliDialog
{
  private static final long serialVersionUID = -7938276229102923102L;
  
  private FormLayout formLay;
  private TextField locTf;
  private BoundAffiliationCombo affilCombo;
  private Long localUserId;
  
  private String warning = "These fields are optional.  Please be careful that the combination of<br/>player ID, affiliation and location "+
  "do not reveal your actual identity.";
  public RegistrationPagePopupSecond(Button.ClickListener listener, Long uId)
  {
    super(listener);
    super.initGui();
    localUserId = uId;
    setTitleString("Tell us about you");
 
    contentVLayout.setSpacing(true);
    Label sp;
    contentVLayout.addComponent(sp = new Label());
    sp.setHeight("20px");

    Label header = new HtmlLabel("<center>Affiliation category and location are optional and are displayed to other game players.</center>"); // and help you</center>");
    header.addStyleName("m-dialog-label-noindent");
    contentVLayout.addComponent(header);
    contentVLayout.setComponentAlignment(header, Alignment.TOP_CENTER);

    HorizontalLayout horL = new HorizontalLayout();
    horL.setSpacing(false);
    horL.setWidth("100%");
    contentVLayout.addComponent(horL);

    horL.addComponent(sp = new Label());
    sp.setWidth("20px");
       
    // Use an actual form widget here for data binding and error display.
    formLay = new FormLayout();
    formLay.addStyleName("m-login-form");  // to allow styling contents (v-textfield)
    formLay.setSizeUndefined();

    horL.addComponent(formLay);
    horL.setExpandRatio(formLay, 1.0f);
    
    formLay.addComponent(affilCombo = new BoundAffiliationCombo("Affiliation:"));
    affilCombo.setValue(affilCombo.getItemIds().toArray()[0]);  // Tried to get this to be editable....needs more work
  
    formLay.addComponent(locTf = new TextField("Location:"));
    locTf.setColumns(31);
    locTf.setInputPrompt("optional");
    locTf.addStyleName("m-noleftmargin");
    
    Label lab;
    contentVLayout.addComponent(lab = new HtmlLabel(warning));
    lab.addStyleName(labelStyle);
    
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth("100%");
    contentVLayout.addComponent(hl);

    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);
    
    NativeButton continueButt = new NativeButton(null);
    continueButt.setStyleName("m-continueButton");
    hl.addComponent(continueButt);
    continueButt.addClickListener(new JoinListener());   
    continueButt.setClickShortcut(KeyCode.ENTER);
    
    hl.addComponent(lab = new Label());
    lab.setWidth("20px"); // don't run off the end
  }
  
  @SuppressWarnings("serial")
  class JoinListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      //String location = checkValue(locTf);
      // Don't want a notification anymore; also, the checkValue method will now return "unspecified" if null
//      if(location == null || location.length()<=0) {
//        app.getMainWindow().showNotification("Login not complete.","Please enter at least an approximate location from where you are playing.",Notification.TYPE_ERROR_MESSAGE);
//        return;
//      }
      User _usr = User.getTL(localUserId);
      _usr.setLocation(checkValue(locTf));
      Affiliation afl = (Affiliation)affilCombo.getValue();
      String aflStr = afl.getAffiliation();
      if(aflStr.equalsIgnoreCase("optional") || aflStr.equalsIgnoreCase("required"))
        aflStr = "";
      _usr.setAffiliation(aflStr);
      User.updateTL(_usr);
      HSess.close();
      MSysOut.println(NEWUSER_CREATION_LOGS,"Affiliation dialog continue button clicked, user "+_usr.getUserName());
      listener.buttonClick(event); // up the chain
    }
  }

  private String checkValue(TextField tf)
  {
    Object o = tf.getValue();
    boolean empty = (o == null) || (o.toString().length()<=0);
    if(o.toString().equals("optional"))
      empty = true;
    return empty?"":o.toString();
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
