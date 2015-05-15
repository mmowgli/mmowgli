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

package edu.nps.moves.mmowgli.modules.userprofile;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.io.Serializable;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.messaging.WantsMessageUpdates;
import edu.nps.moves.mmowgli.messaging.WantsUserUpdates;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * UserProfilePageStyled.java
 * Created on Mar 14, 2011
 * Updated on Mar 13, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserProfilePage3 extends AbsoluteLayout implements MmowgliComponent, WantsUserUpdates, WantsMessageUpdates, View
{
  private static final long serialVersionUID = 1400555506850006813L;
  
  private NativeButton myIdeasButt,myActionPlansButt, myBuddiesButt, myMailButt;
  private UserProfileTabPanel myIdeasPanel, myActionsPanel, myBuddiesPanel;
  private UserProfileMyMailPanel myMailPanel;
  private UserProfile3Top topPan;
  
  Button currentTabButton;
  UserProfileTabPanel currentTabPanel;
  private boolean itsSomebodyElse = false;

  @HibernateSessionThreadLocalConstructor
  public UserProfilePage3(Object uid)
  {
    User u = User.getTL(uid);
    User me = Mmowgli2UI.getGlobals().getUserTL();
    itsSomebodyElse = (u.getId() != me.getId());

    myIdeasPanel       = new UserProfileMyIdeasPanel2(uid);   
    myActionsPanel     = new UserProfileMyActionsPanel(uid);
    myBuddiesPanel     = new UserProfileMyBuddiesPanel(uid);
    myMailPanel        = new UserProfileMyMailPanel(uid);
    
    myIdeasButt       = new NativeButton();
    myActionPlansButt = new NativeButton();
    myBuddiesButt     = new NativeButton();
    myMailButt        = new NativeButton();
    
    currentTabButton = myIdeasButt;
    currentTabPanel  = myIdeasPanel;
    topPan = new UserProfile3Top(uid);
  }
  
  @Override
  public void initGui()
  {
  }
  public void initGuiTL()
  {
    setWidth(APPLICATION_SCREEN_WIDTH);
    setHeight("1215px"); //"1000px");
      
    Label sp;
    this.addComponent(sp = new Label());
    sp.setHeight("25px");

    addComponent(topPan,"top:5px;left:22px"); //33px");
    topPan.initGui();
    AbsoluteLayout bottomPan = new AbsoluteLayout();
    addComponent(bottomPan,"top:375;left:23px");
    bottomPan.setWidth("969px");
    bottomPan.setHeight("841px");
    
    NewTabClickHandler  tabHndlr = new NewTabClickHandler();
       
    // Set different art if it's "me" we're looking at or anyother
    if(!itsSomebodyElse) {
      //tabs.setStyleName("m-userProfile2BlackTabs"); //978w 831h has names     
      bottomPan.addStyleName("m-userprofile3bottom");
      myIdeasButt.setStyleName("m-userProfile3MyIdeasTab");
      myActionPlansButt.setStyleName("m-userProfile3MyActionPlansTab");
      myBuddiesButt.setStyleName("m-userProfile3MyBuddiesTab");
      myMailButt.setStyleName("m-userProfile3MyMailTab");
    }
    else {
      //tabs.setStyleName("m-userProfile2BlackTabs_other");      
      bottomPan.addStyleName("m-userprofile3HisBottom");
      myIdeasButt.setStyleName("m-userProfile3HisIdeasTab");
      myActionPlansButt.setStyleName("m-userProfile3HisActionPlansTab");
      myBuddiesButt.setStyleName("m-userProfile3HisBuddiesTab");
    }
    
    HorizontalLayout buttons = new HorizontalLayout();
    buttons.setSpacing(false);
    myIdeasButt.addClickListener(tabHndlr);
    buttons.addComponent(myIdeasButt);
    
    myActionPlansButt.addClickListener(tabHndlr);
    buttons.addComponent(myActionPlansButt);
    myActionPlansButt.addStyleName("m-transparent-background");  // un selected
    
    myBuddiesButt.addClickListener(tabHndlr);
    buttons.addComponent(myBuddiesButt);
    myBuddiesButt.addStyleName("m-transparent-background");  // un selected
    
    if(!itsSomebodyElse) {
      myMailButt.addClickListener(tabHndlr);
      buttons.addComponent(myMailButt);
      myMailButt.addStyleName("m-transparent-background"); // un selected
    }
    
    bottomPan.addComponent(buttons, "top:0px;left:0px");
    
    // stack the pages
    String panPosition = "top:70px;left:0px";

    bottomPan.addComponent(myIdeasPanel,panPosition);
    myIdeasPanel.initGui();
    
    bottomPan.addComponent(myActionsPanel, panPosition);
    myActionsPanel.initGui();
    myActionsPanel.setVisible(false);
    
    bottomPan.addComponent(myBuddiesPanel, panPosition);
    myBuddiesPanel.initGui();
    myBuddiesPanel.setVisible(false);
    
    bottomPan.addComponent(myMailPanel, panPosition);
    myMailPanel.initGui();
    myMailPanel.setVisible(false);
  }
  
  @SuppressWarnings("serial")
  class NewTabClickHandler implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      Button b = event.getButton();
      if (b == currentTabButton)
        return;
      currentTabButton.addStyleName("m-transparent-background");
      currentTabPanel.setVisible(false);
      currentTabButton = b;

      if (b == myIdeasButt) {
        myIdeasButt.removeStyleName("m-transparent-background");
        currentTabPanel = myIdeasPanel;
        myIdeasPanel.setVisible(true);
      }
      else if (b == myActionPlansButt) {
        myActionPlansButt.removeStyleName("m-transparent-background");
        currentTabPanel = myActionsPanel;
        myActionsPanel.setVisible(true);
      }
      else if (b == myBuddiesButt) {
        myBuddiesButt.removeStyleName("m-transparent-background");
        currentTabPanel = myBuddiesPanel;
        myBuddiesPanel.setVisible(true);
      }
      else if (b == myMailButt) {
        myMailButt.removeStyleName("m-transparent-background");
        currentTabPanel = myMailPanel;
        myMailPanel.setVisible(true);
      }
    }
  }

  @Override
  public boolean userUpdated_oobTL(Object uId)
  {
    MSysOut.println(USER_UPDATE_LOGS, getClass().getSimpleName()+".userUpdated_oobTL("+uId+")");
    return topPan.userUpdated_oobTL(uId);
  }

  /**
   * Here's where we get notice that a message came in. See if it's for us.  The controller knows
   * that the only messages which are applicable are those which are to the logged in user, so we
   * won't be getting anything but those.
   */
  @Override
  public boolean messageCreated_oobTL(Serializable uId)
  {
    return myMailPanel.messageCreated_oobTL(uId);    
  }

  /* View interface */
  @Override
  public void enter(ViewChangeEvent event)
  {
    Object key = HSess.checkInit();
    initGuiTL();
    HSess.checkClose(key);
  }
}
