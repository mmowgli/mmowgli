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

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

/**
 * MmowgliMobileUIProvider.java Created on Jan 30, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliMobileUIProvider extends UIProvider
{
  private static final long serialVersionUID = -8081133333677248591L;

  @Override
  public Class<? extends UI> getUIClass(UIClassSelectionEvent event)
  {
    String userAgent = event.getRequest().getHeader("user-agent").toLowerCase();
    // todo test for screen siz
    if (userAgent.contains("webkit") ||
        userAgent.contains("firefox") ||
        userAgent.contains("msie 1") ||
        userAgent.contains("trident/7")) {
      return MmowgliMobileUI.class;
    }
    else {
      return MmowgliMobileFallbackUI.class;
    }

  }
}
