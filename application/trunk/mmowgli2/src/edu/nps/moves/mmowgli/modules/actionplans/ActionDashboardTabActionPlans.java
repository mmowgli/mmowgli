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

import static edu.nps.moves.mmowgli.MmowgliEvent.ACTIONPLANREQUESTCLICK;

import java.io.Serializable;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanUpdates;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanContainers.QuickAllPlansInThisMove;
import edu.nps.moves.mmowgli.utility.IDButton;


/**
 * ActionDashboardTabActionPlans.java Created on Mar 2, 2011
 * Updated 19 Mar, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionDashboardTabActionPlans extends ActionDashboardTabPanel implements WantsActionPlanUpdates
{
  private static final long serialVersionUID = -2159016498621759089L;
  
  private ActionPlanTable table;
  private VerticalLayout flowLay;
  
  @HibernateSessionThreadLocalConstructor
  public ActionDashboardTabActionPlans()
  {
    super();
  }

  @Override
  public void initGui()
  {
    throw new UnsupportedOperationException("");
  }

  public void initGuiTL()
  {
    AbstractLayout leftLay = getLeftLayout();

    flowLay = new VerticalLayout();
    flowLay.setWidth("100%");
    leftLay.addComponent(flowLay); //, "top:0px;left:0px");
    flowLay.addStyleName("m-padding15");
    flowLay.setSpacing(true);

    Label titleLab = new Label("All Plans");
    flowLay.addComponent(titleLab);
    flowLay.setComponentAlignment(titleLab, Alignment.TOP_LEFT);
    titleLab.addStyleName("m-actionplan-mission-title-text");

    Label contentLab = new Label("The Action Plans tab displays a list of all action plans in the game.");
    flowLay.addComponent(contentLab);
    flowLay.setComponentAlignment(contentLab, Alignment.TOP_LEFT);
    flowLay.addStyleName("m-actionplan-mission-content-text");
    
    Button requestActionPlanButt = new IDButton("Action Plan Request",ACTIONPLANREQUESTCLICK);
    requestActionPlanButt.setStyleName(BaseTheme.BUTTON_LINK);
    requestActionPlanButt.setDescription("Open a page where you can submit a request to create an action plan");
    flowLay.addComponent(requestActionPlanButt);

    AbsoluteLayout rightLay = getRightLayout();
    flowLay = new VerticalLayout();
    rightLay.addComponent(flowLay, "top:0px;left:0px");
    flowLay.setSpacing(true);
    flowLay.setStyleName("m-actionplan-plan-rightside"); // set the style name so the css's below can use it (e.g.: .m-actionplan-plan-rightside
                                                         // .m-actionplan-plan-headling { blah:blah;} )

    loadTableTL();
  }

  
  private void loadTableTL()
  {
    if(table != null)
      flowLay.removeComponent(table);

    table = new ActionPlanTable(new QuickAllPlansInThisMove(Game.getTL(),Mmowgli2UI.getGlobals().getUserTL()));

    flowLay.addComponent(table);
    flowLay.setWidth("669px");
    table.setWidth("100%");
    table.setHeight("715px"); //"680px");   
  }

  @Override
  public boolean actionPlanUpdatedOobTL(Serializable apId)
  {
    // don't need to refresh here...excessive
   // loadTable(sessMgr);
   // return true;
    return false;
  }

}
