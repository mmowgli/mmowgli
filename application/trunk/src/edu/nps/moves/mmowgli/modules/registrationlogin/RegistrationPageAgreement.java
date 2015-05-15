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
import edu.nps.moves.mmowgli.db.User;
/**
 * LoginPopup.java Created on Dec 15, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
abstract public class RegistrationPageAgreement extends MmowgliDialog
{
  private static final long serialVersionUID = 4978360585517885841L;
  
  private boolean rejected = true;  // so cancel button works

  abstract protected String getTitle();
  abstract protected String getLabelText();
  abstract protected String getReadUrlTL();
  abstract protected String getReadLabel();
  
  public RegistrationPageAgreement(Button.ClickListener listener)
  {
    super(listener);
    super.initGui();

    setTitleString(getTitle()); //"User Agreement 1");

    contentVLayout.setSpacing(true);
    Label lab = new HtmlLabel(getLabelText()); //"First, please confirm your willingness to meet game requirements.  I also confirm that I am at least 18 years of age.");
    lab.addStyleName(topLabelStyle);
    contentVLayout.addComponent(lab);
    
    HorizontalLayout hlayout = new HorizontalLayout();
    contentVLayout.addComponent(hlayout);
    hlayout.setSpacing(true);
    hlayout.setWidth("100%");
    hlayout.addStyleName(labelStyle);
    
    String readUrl = getReadUrlTL();
    if(readUrl != null) {
      Link readLink = new Link("Read",new ExternalResource(getReadUrlTL())); //REGISTRATIONCONSENTURL));
      readLink.setTargetName("_agreements");
      readLink.setTargetBorder(BorderStyle.DEFAULT);
      readLink.setDescription("Opens in new window/tab");
      hlayout.addComponent(readLink);
      readLink.setSizeUndefined();
      hlayout.setComponentAlignment(readLink, Alignment.MIDDLE_LEFT);
    }
    
    lab = new HtmlLabel(getReadLabel()); //"<i>Consent to Participate in Anonymous Survey</i>");
    lab.setSizeUndefined();
    hlayout.addComponent(lab);
    hlayout.setSizeUndefined();
    hlayout.setComponentAlignment(lab, Alignment.TOP_LEFT);
   
    contentVLayout.addComponent(lab=new Label());
    lab.setHeight("15px");
  
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth("100%");
    contentVLayout.addComponent(hl);
    
    NativeButton rejectButt = new NativeButton();
    hl.addComponent(rejectButt);
    rejectButt.setStyleName("m-rejectNoThanksButton");
   // Mmowgli2UI.getGlobals().mediaLocator().decorateDialogRejectButton(rejectButt);    
    rejectButt.addClickListener(new RejectListener());
    
    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);
    
    NativeButton continueButt = new NativeButton();
    hl.addComponent(continueButt);
    //Mmowgli2UI.getGlobals().mediaLocator().decorateDialogAcceptAndContinueButton(continueButt);
    continueButt.setStyleName("m-acceptAndContinueButton");
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

  // used by parent class when cancel is hit
  public void setUser(User usr)
  {
    u = (usr==null?null:usr.getId());  
  }

  public boolean getRejected()
  {
    return rejected;
  }
}
