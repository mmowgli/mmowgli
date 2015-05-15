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

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.Toolbar;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * MmowgliFooter.java
 * Created on Feb 26, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliFooter2 extends Toolbar
{
  private static final long serialVersionUID = 3065915569244954235L;
  private NavigationManager navmgr;
  
  @SuppressWarnings("serial")
  public MmowgliFooter2()
  {
    Button homeButton = new Button(FontAwesome.HOME);
    homeButton.setStyleName("no-decoration");
    addComponent(homeButton);

    homeButton.addClickListener(new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event) {
        navmgr.navigateTo(new GameDataCategoriesView2());
      }
    });
  }
  
  public void setNavigationManager(NavigationManager mgr)
  {
    navmgr = mgr;
  }
}

