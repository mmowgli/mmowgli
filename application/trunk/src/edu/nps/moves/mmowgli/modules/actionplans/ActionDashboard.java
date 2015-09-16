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

package edu.nps.moves.mmowgli.modules.actionplans;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import static edu.nps.moves.mmowgli.MmowgliEvent.HOWTOWINACTIONCLICK;

import java.io.Serializable;
import java.util.Set;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanUpdates;
import edu.nps.moves.mmowgli.utility.IDNativeButton;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * ActionDashboard.java
 * Created on Jan 18, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionDashboard extends VerticalLayout implements MmowgliComponent, WantsActionPlanUpdates, View
{
  private static final long serialVersionUID = 8653983135113761237L;

  private static String howWinAction_tt = "Strategy guidance video";
    
  private ActionDashboardTabPanel actionPlansTab,myPlansTab,needAuthorsTab;
  private Button currentTabButton;
  private NativeButton actionPlansTabButt, myPlansTabButt, needAuthorsTabButt;
  private ActionDashboardTabPanel currentTabPanel;
  private IDNativeButton howToWinActionButt;
  
  //private User me;
  //private Set<ActionPlan> invitedSet;
  
  @HibernateSessionThreadLocalConstructor
  public ActionDashboard()
  {
    User me = Mmowgli2UI.getGlobals().getUserTL();

    actionPlansTab     = new ActionDashboardTabActionPlans();
    myPlansTab         = new ActionDashboardTabMyPlans(me);
    needAuthorsTab     = new ActionDashboardTabNeedAuthors();
    actionPlansTabButt = new NativeButton();
    myPlansTabButt     = new NativeButton();
    needAuthorsTabButt = new NativeButton();
    howToWinActionButt = new IDNativeButton(null,HOWTOWINACTIONCLICK);
    
    howToWinActionButt.setStyleName("m-howToWinAction");
    currentTabButton = actionPlansTabButt;
    currentTabPanel  = actionPlansTab;    
  }
  
  @Override
  public void initGui()
  {
    throw new UnsupportedOperationException("");
  }

  public void initGuiTL()
  {
    setSizeUndefined();
    setWidth(APPLICATION_SCREEN_WIDTH);
//    setHeight("855px"); //ACTIONDASHBOARD_H);
    
    Label sp;
    addComponent(sp=new Label());
    sp.setHeight("10px");
    
    HorizontalLayout titleHL = new HorizontalLayout();
    titleHL.setWidth("95%");
    addComponent(titleHL);
   
    titleHL.addComponent(sp=new Label());
    sp.setWidth("20px");
    Component titleC;
    titleHL.addComponent(titleC=Mmowgli2UI.getGlobals().getMediaLocator().getActionDashboardTitle());
    titleHL.setComponentAlignment(titleC, Alignment.MIDDLE_LEFT);
    
    titleHL.addComponent(sp=new Label());
    sp.setWidth("1px");
    titleHL.setExpandRatio(sp, 1.0f);
    
    titleHL.addComponent(howToWinActionButt);
    howToWinActionButt.setDescription(howWinAction_tt);
       
    AbsoluteLayout absL = new AbsoluteLayout();    
    addComponent(absL);
    
    absL.setWidth(APPLICATION_SCREEN_WIDTH);
    absL.setHeight(ACTIONDASHBOARD_H);
    
    MediaLocator medLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    
    AbsoluteLayout mainAbsLay = new AbsoluteLayout(); // offset it from master
    mainAbsLay.setWidth(APPLICATION_SCREEN_WIDTH);
    mainAbsLay.setHeight(ACTIONDASHBOARD_H);
    absL.addComponent(mainAbsLay,ACTIONDASHBOARD_OFFSET_POS);

    // Now the background     
    Embedded backgroundImage = new Embedded(null,medLoc.getActionDashboardPlanBackground());
    backgroundImage.setWidth(ACTIONDASHBOARD_W);
    backgroundImage.setHeight(ACTIONDASHBOARD_H);
    mainAbsLay.addComponent(backgroundImage,"top:0px;left:0px");

    HorizontalLayout tabsHL = new HorizontalLayout();
    tabsHL.setStyleName("m-actionDashboardBlackTabs");
    tabsHL.setSpacing(false);
    
    tabsHL.addComponent(sp = new Label());
    sp.setWidth("12px");
    
    TabClickHandler  tabHndlr = new TabClickHandler();
    actionPlansTabButt.setStyleName("m-actionDashboardActionPlansTab");
    actionPlansTabButt.addClickListener(tabHndlr);
    actionPlansTabButt.setId(ACTION_DASHBOARD_ACTION_PLANS_TAB);
    tabsHL.addComponent(actionPlansTabButt);
    
    tabsHL.addComponent(sp=new Label());
    sp.setWidth("1px");
        
    myPlansTabButt.setStyleName("m-actionDashboardMyPlansTab");
    myPlansTabButt.addClickListener(tabHndlr);
    myPlansTabButt.setId(ACTION_DASHBOARD_MY_ACTION_PLANS_TAB);
    tabsHL.addComponent(myPlansTabButt);
    myPlansTabButt.addStyleName("m-transparent-background"); // initially
    
    tabsHL.addComponent(sp=new Label());
    sp.setWidth("1px");
    
    needAuthorsTabButt.setStyleName("m-actionDashboardNeedAuthorsTab");
    needAuthorsTabButt.addClickListener(tabHndlr);
    needAuthorsTabButt.setId(ACTION_DASHBOARD_NEED_AUTHORS_TAB);
    tabsHL.addComponent(needAuthorsTabButt);
    needAuthorsTabButt.addStyleName("m-transparent-background"); // initially
    
    absL.addComponent(tabsHL,"left:7px;top:8px");
    
    // stack the pages
    absL.addComponent(actionPlansTab,ACTIONDASHBOARD_TABCONTENT_POS);
    actionPlansTab.initGuiTL();
    
    absL.addComponent(myPlansTab, ACTIONDASHBOARD_TABCONTENT_POS);
    myPlansTab.initGuiTL();
    myPlansTab.setVisible(false);
    
    absL.addComponent(needAuthorsTab, ACTIONDASHBOARD_TABCONTENT_POS);
    needAuthorsTab.initGuiTL();
    needAuthorsTab.setVisible(false);
    
    User me = Mmowgli2UI.getGlobals().getUserTL();
    Set<ActionPlan> invitedSet = me.getActionPlansInvited();
    if(invitedSet != null && (invitedSet.size())>0) {
      Notification note = new Notification(
          "<center>You're invited to an Action Plan!</center>",
          "<center> Look for the \"you're invited to join!\" notice.<br/>"+
          "First, check out the plan.  Then, if you want to join,<br/>"+
          "click the link to become an author."+
          "</center>",Type.HUMANIZED_MESSAGE,true); // allow html

      note.setPosition(Position.MIDDLE_CENTER);
      note.setDelayMsec(5000);// 5 secs
      note.show(Page.getCurrent());
    }     
  }
  
  @SuppressWarnings("serial")
  class TabClickHandler implements ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      Button b = event.getButton();
      if(b == currentTabButton)
        return;
      
      HSess.init();
      currentTabButton.addStyleName("m-transparent-background");
      currentTabPanel.setVisible(false);
      currentTabButton = b;
      
      if(b == actionPlansTabButt) {
        actionPlansTabButt.removeStyleName("m-transparent-background");
        actionPlansTab.setVisible(true);
        currentTabPanel = actionPlansTab;
      }
      else if(b == myPlansTabButt) {
        myPlansTabButt.removeStyleName("m-transparent-background");
        myPlansTab.setVisible(true);
        currentTabPanel = myPlansTab;
      }
      else if(b == needAuthorsTabButt) {
        needAuthorsTabButt.removeStyleName("m-transparent-background");
        currentTabPanel = needAuthorsTab;
        needAuthorsTab.setVisible(true);      
      }
      HSess.close();
    }
  }

  @Override
  public boolean actionPlanUpdatedOobTL(Serializable apId)
  {
    boolean retn = actionPlansTab.actionPlanUpdatedOobTL(apId);
    if(myPlansTab.actionPlanUpdatedOobTL(apId))
      retn = true;        
    if(needAuthorsTab.actionPlanUpdatedOobTL(apId))
      retn = true;
    return retn;
 }

  /* View interface */
  @Override
  public void enter(ViewChangeEvent event)
  {
    Object sessKey = HSess.checkInit();
    initGuiTL(); 
    HSess.checkClose(sessKey);
  }
}
