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

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.VerticalLayout;

import edu.nps.moves.mmowgli.components.GhostVerticalLayoutWrapper;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanUpdates;

/**
 * ActionDashboardTabPanel.java
 * Created on Mar 2, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class ActionDashboardTabPanel extends AbsoluteLayout implements MmowgliComponent, WantsActionPlanUpdates
{
  private static final long serialVersionUID = 5708282349412097419L;
  
  private AbsoluteLayout leftAbsLay;
  private AbsoluteLayout rightAbsLay;
  private GhostVerticalLayoutWrapper wrapper;
  private VerticalLayout leftVertLay;
  public ActionDashboardTabPanel()
  {
    setWidth(ACTIONDASHBOARD_TABCONTENT_W);
    setHeight(ACTIONDASHBOARD_TABCONTENT_H);
    
    leftAbsLay = new AbsoluteLayout();
    leftAbsLay.setWidth(ACTIONDASHBOARD_TABCONTENT_LEFT_W);
    leftAbsLay.setHeight(ACTIONDASHBOARD_TABCONTENT_LEFT_H);
    leftAbsLay.addComponent(wrapper=new GhostVerticalLayoutWrapper(), "left:0px;right:0px");

    leftVertLay = new VerticalLayout();
    wrapper.ghost_setContent(leftVertLay);
       
    rightAbsLay = new AbsoluteLayout();
    rightAbsLay.setWidth("669px"); // this needs about 10 more px //ACTIONDASHBOARD_TABCONTENT_RIGHT_W);
    rightAbsLay.setHeight(ACTIONDASHBOARD_TABCONTENT_RIGHT_H);
    
    addComponent(leftAbsLay,ACTIONDASHBOARD_TABCONTENT_LEFT_POS);
    addComponent(rightAbsLay,ACTIONDASHBOARD_TABCONTENT_RIGHT_POS);
  }
  
  public AbstractLayout getLeftLayout()
  {
    return leftVertLay; //leftAbsLay;
  }
  public AbsoluteLayout getRightLayout()
  {
    return rightAbsLay;
  }
  
  abstract public void initGuiTL();  // temp until MmowgliComponent is changed
}