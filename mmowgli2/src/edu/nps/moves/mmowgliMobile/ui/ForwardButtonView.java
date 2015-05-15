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

import com.vaadin.addon.touchkit.ui.*;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.ui.Component;

/**
 * ForwardButtonView.java created on Oct 23, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@SuppressWarnings("serial")
public abstract class ForwardButtonView extends NavigationView
{
  private ForwardButton fwdButt;
  public ForwardButtonView()
  {
    getNavigationBar().setRightComponent(fwdButt=new ForwardButton());
  }
  
  @Override
  protected void onBecomingVisible()
  {
    super.onBecomingVisible();
    Component next = getNavigationManager().getNextComponent();
    if(next == null)
      fwdButt.setVisible(false);
    else {
      fwdButt.setVisible(true);
      fwdButt.setCaption(" "+next.getCaption());
    }
  }
  
  class ForwardButton extends NavigationButton implements NavigationButtonClickListener
  {
    public ForwardButton()
    {
      addClickListener(this);
    }

    @Override
    public void buttonClick(NavigationButtonClickEvent event)
    {
      NavigationManager navMgr = ForwardButtonView.this.getNavigationManager();
      Component c = navMgr.getNextComponent();
      if(c != null)
        navMgr.navigateTo(c);      
    }
  }
}
