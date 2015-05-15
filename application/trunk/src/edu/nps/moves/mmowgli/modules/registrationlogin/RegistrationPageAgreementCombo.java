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
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.GameLinks;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
import static edu.nps.moves.mmowgli.MmowgliConstants.NEWUSER_CREATION_LOGS;

/**
 * LoginPopup.java Created on Dec 15, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RegistrationPageAgreementCombo extends MmowgliDialog
{
  private static final long serialVersionUID = 4978360585517885841L;
  
  private boolean rejected = true;  // so cancel button works
  
  public RegistrationPageAgreementCombo(Button.ClickListener listener)
  {
    super(listener);
    super.initGui();

    setTitleString("User Agreement");

    contentVLayout.setSpacing(true);

    Label lab = new Label("I confirm my willingness to meet game requirements:");
    lab.addStyleName(topLabelStyle);
    contentVLayout.addComponent(lab);
    
    // First
    contentVLayout.addComponent(lab = new Label());
    lab.setHeight("5px"); // space
    
    contentVLayout.addComponent(lab = new HtmlLabel("First, I confirm that I am at least 18 years of age, I have been informed of risks<br/>and benefits, and I consent to participate."));
    lab.addStyleName(labelStyle);    
    
    HorizontalLayout hlayout = new HorizontalLayout();
    contentVLayout.addComponent(hlayout);
    hlayout.setSpacing(true);
    hlayout.setWidth("100%");
    hlayout.addStyleName(labelStyle);
    
    // First read
    hlayout.addComponent(lab = new HtmlLabel("&nbsp;&nbsp;"));
    lab.setHeight("10px");
    GameLinks gl = GameLinks.getTL();
    Link readLink = new Link("Read",new ExternalResource(gl.getInformedConsentLink())); //REGISTRATIONCONSENTURL));
    readLink.setTargetName("_agreements");
    readLink.setTargetBorder(BorderStyle.DEFAULT);
    readLink.setDescription("Opens in new window/tab");
    hlayout.addComponent(readLink);
    readLink.setSizeUndefined();
    
    lab = new HtmlLabel("<i>Informed Consent to Participate in Research</i>");
    lab.setSizeUndefined();
    hlayout.addComponent(lab);
   
    hlayout.setSizeUndefined();
   
    // Second
    contentVLayout.addComponent(lab = new Label());
    lab.setHeight("5px"); // space
    
    lab = new HtmlLabel("Second, I understand that <b style='color:red;'>no classified or sensitive information can be<br/>posted</b> to the game since participation is open.  Violation of this policy may<br/>lead to serious consequences.");
    lab.addStyleName(labelStyle);
    contentVLayout.addComponent(lab);
    
    hlayout = new HorizontalLayout();
    contentVLayout.addComponent(hlayout);
    hlayout.setSpacing(true);
    hlayout.setWidth("100%");
    hlayout.addStyleName(labelStyle);
    
    // Second read
    hlayout.addComponent(lab = new HtmlLabel("&nbsp;&nbsp;"));

    readLink = new Link("Read",new ExternalResource(gl.getUserAgreementLink()));
    readLink.setTargetName("_agreements");
    readLink.setTargetBorder(BorderStyle.DEFAULT);
    readLink.setDescription("Opens in new window/tab");
    hlayout.addComponent(readLink);
    readLink.setSizeUndefined();
    
    lab = new HtmlLabel("<i>Department of Defense Social Media User Agreement</i>");
    lab.setSizeUndefined();
    hlayout.addComponent(lab);
   
    hlayout.setSizeUndefined();
    
    // Third
    contentVLayout.addComponent(lab = new Label());
    lab.setHeight("5px"); // space
    
    lab = new HtmlLabel("Third, the official language of the MMOWGLI game is English.  Other languages<br/>are not supported in order to ensure that player postings are appropriate.");
    lab.addStyleName(labelStyle);
    contentVLayout.addComponent(lab);
    
    contentVLayout.addComponent(lab=new Label());
    lab.setHeight("15px");
  
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth("100%");
    contentVLayout.addComponent(hl);
    
    hl.addComponent(lab=new Label());
    lab.setWidth("20px");
    
    NativeButton rejectButt = new NativeButton();
    hl.addComponent(rejectButt);
    rejectButt.setStyleName("m-rejectNoThanksButton");   //new way
    rejectButt.addClickListener(new RejectListener());
    
    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);
    
    NativeButton continueButt = new NativeButton();
    hl.addComponent(continueButt);
    continueButt.setStyleName("m-acceptAndContinueButton");  // new way
    continueButt.addClickListener(new MyContinueListener());
    
    continueButt.setClickShortcut(KeyCode.ENTER);
  }
  
  // Used to center the dialog
  public int getUsualWidth()
  {
    return 580; // px
  }
  
  @SuppressWarnings("serial")
  class RejectListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      MSysOut.println(NEWUSER_CREATION_LOGS,"Reject user agreement clicked");

      rejected = true;
      listener.buttonClick(event);
    }
  }

  @SuppressWarnings("serial")
  class MyContinueListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      MSysOut.println(NEWUSER_CREATION_LOGS,"Accept user agreement clicked");

      rejected = false;
      listener.buttonClick(event); // back up the chain
    }
  }

  private Long u;
  /**
   * @return the userID or null if cancelled
   */
  public Long getUserId()
  {
    return u;
  }

  // used by parent class to set u == null when cancel is hit
  public void setUser(User usr)
  {
    u = (usr==null?null:usr.getId());  
  }

  public boolean getRejected()
  {
    return rejected;
  }
}
