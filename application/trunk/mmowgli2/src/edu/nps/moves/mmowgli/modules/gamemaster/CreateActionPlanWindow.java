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

package edu.nps.moves.mmowgli.modules.gamemaster;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;

import edu.nps.moves.mmowgli.modules.gamemaster.CreateActionPlanPanel.CreateActionPlanLayout;

/**
 * CreateActionPlanWindow.java
 * Created on Aug 25, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CreateActionPlanWindow extends Window implements ClickListener
{
  private static final long serialVersionUID = 5886052066514140705L;
  
  public CreateActionPlanWindow()
  {
    this(null,null);
  }
  public CreateActionPlanWindow(Object apId, Object rootCardId)
  {
    super("Create Action Plan");

    CreateActionPlanLayout lay = new CreateActionPlanLayout(apId,rootCardId,this);
    setContent(lay);
    lay.setSizeUndefined();
    lay.initGui();
    
    setModal(false);
  }
  
  @Override
  public void buttonClick(ClickEvent event)
  {
    this.close();
  }
}
