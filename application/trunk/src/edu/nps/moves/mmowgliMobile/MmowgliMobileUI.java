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

package edu.nps.moves.mmowgliMobile;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
import edu.nps.moves.mmowgliMobile.ui.GameDataCategoriesView2;
import edu.nps.moves.mmowgliMobile.ui.NotAllowedPopover;
import static edu.nps.moves.mmowgli.MmowgliConstants.*;

@SuppressWarnings("serial")
@Theme("mmowglimobile")
@Title("mmowgli")
@Widgetset("edu.nps.moves.mmowgliMobile.gwt.MmowgliMobileWidgetSet")

public class MmowgliMobileUI extends UI
{
  @Override
  protected void init(VaadinRequest request)
  {
    MSysOut.println(MOBILE_LOGS,"MM mmowgli mobile UI init v2");
    setContent(new NavigationManager(new GameDataCategoriesView2()));
    
    HSess.init();
    Game g = Game.get(HSess.get());
    HSess.close();
    
    if(g.getCurrentMove().getCurrentMovePhase().isLoginAllowGuests())
      ;
    else {
      NotAllowedPopover pop = new NotAllowedPopover();
      addWindow(pop);
      pop.center();
    }
  } 
}
