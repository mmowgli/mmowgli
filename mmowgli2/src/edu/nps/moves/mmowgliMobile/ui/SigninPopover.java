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

package edu.nps.moves.mmowgliMobile.ui;

import org.hibernate.Session;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.VHibPii;

/**
 * SigninPopover.java
 * Created on Feb 28, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class SigninPopover extends Popover implements Button.ClickListener
{
  private static final long serialVersionUID = 7790319233765441770L;
  TextField nameTF;
  PasswordField pwTF;
  Button submitButt;
  Label errorLab;
  
  public SigninPopover()
  {
    setWidth("300px");
    setHeight("350px");
    setClosable(false);  // else it can be closed by clicking anywhere outside it

    VerticalLayout layout = new VerticalLayout();
    layout.setSpacing(true);
    layout.setMargin(true);
    layout.addComponent(nameTF = new TextField("Player name"));
    nameTF.setWidth("100%");
    layout.addComponent(pwTF = new PasswordField("Password"));
    pwTF.setWidth("100%");
    
    Label lab;
    layout.addComponent(lab = new Label());
    lab.setHeight("25px");

    layout.addComponent(submitButt = new Button("Sign in",new SubmitListener()));
    submitButt.setSizeUndefined();
    
    layout.addComponent(errorLab = new HtmlLabel(""));
    layout.setComponentAlignment(errorLab, Alignment.TOP_CENTER);
 // Decorate with navigation view
    NavigationView content = new NavigationView(layout);
    content.setCaption("Sign in");
    setContent(content);
    // Have a close button
    /*
    Button close = new Button(null, this);
    close.setIcon(new ThemeResource("../runo/icons/64/cancel.png"));
    content.setRightComponent(close); 
*/
  }
  @SuppressWarnings("serial")
  class SubmitListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      errorLab.setVisible(false);
       String error = processName();
       if(error == null) {
         error = processPW();
         if(error ==  null) {
           error =  tryLogin();
           if(error == null) {
             close();   // removes the popup and lets the user proceed
           }
         }
       }
       errorLab.setValue(error);
       errorLab.setVisible(true);
       return;
    }   
  }
  private String enteredName;
  private String processName()
  {
    enteredName = nameTF.getValue().trim();
    if(enteredName == null || enteredName.length()<=0)
      return "Please enter your player name.";
    return null;
  }
  private String enteredPW;
  private String processPW()
  {
    enteredPW = pwTF.getValue().trim();
    if(enteredPW == null || enteredPW.length()<=0)
      return "Please enter your player password.";
    return null;
  }
  
  private String errorStr = "Sign in unsucessful.  Retry or visit http://mmowgli.nps.edu/blah on a large screen device to register.";
  
  private String tryLogin()
  {
    Session sess = VHibPii.getASession();
    User u = User.getUserWithUserName(enteredName, sess);
    if(u != null) {
      StrongPasswordEncryptor pwEncryptor = new StrongPasswordEncryptor();
      try {
        UserPii upii = VHibPii.getUserPii(u.getId(),sess,true); 
        if(pwEncryptor.checkPassword(enteredPW,upii.getPassword())) {
          if(!u.isAccountDisabled())
            return null; // success
        }
      }
      catch(Throwable t) {}
    }
    return errorStr;
  }
    
  public void buttonClick(ClickEvent event)
  {
    getUI().getSession().close();    // Close the VaadinServiceSession

    // Invalidate underlying session instead if login info is stored there
    // VaadinService.getCurrentRequest().getWrappedSession().invalidate();
    getUI().getPage().setLocation("http://nps.edu");    // Redirect to avoid keeping the removed UI open in the browser
  }
}
