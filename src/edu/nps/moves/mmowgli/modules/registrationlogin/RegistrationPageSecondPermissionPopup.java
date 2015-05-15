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

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * LoginPopup.java Created on Dec 15, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RegistrationPageSecondPermissionPopup extends MmowgliDialog
{
  private static final long serialVersionUID = 4978360585517885841L;
  
  private boolean rejected = true;  // so cancel button works
  
  @HibernateSessionThreadLocalConstructor
  public RegistrationPageSecondPermissionPopup(Button.ClickListener listener)
  {
    super(listener);
    super.initGui();
    
    Game g = Game.getTL();
    
    setTitleString(g.getSecondLoginPermissionPageTitle());

    contentVLayout.setSpacing(true);
    Label lab = new HtmlLabel(g.getSecondLoginPermissionPageText());
    lab.setWidth("82%");
    lab.addStyleName(labelStyle);
    contentVLayout.addComponent(lab);

    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth("98%");
    contentVLayout.addComponent(hl);
    
    hl.addComponent(lab=new Label());
    lab.setWidth("20px");
    
    NativeButton rejectButt = new NativeButton();
    hl.addComponent(rejectButt);
    rejectButt.setStyleName("m-rejectNoThanksButton");
    rejectButt.addClickListener(new RejectListener());
    
    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);
    
    NativeButton continueButt = new NativeButton();
    hl.addComponent(continueButt);
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
      MSysOut.println(NEWUSER_CREATION_LOGS,"Reject 2nd login permission dialog clicked");

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
      MSysOut.println(NEWUSER_CREATION_LOGS,"Accept/continue 2nd login permission dialog clicked");
      
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
