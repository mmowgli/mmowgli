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

package edu.nps.moves.mmowgli;

import java.util.Collection;

import com.vaadin.server.*;
import com.vaadin.ui.UI;

import edu.nps.moves.mmowgli.CACManager.CACData;

/**
 * Mmowgli2UIProvider.java Created on Apr 28, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Mmowgli2UIProvider extends DefaultUIProvider
{
  private static final long serialVersionUID = -3986937743749539633L;

  @Override
  public Class<? extends UI> getUIClass(UIClassSelectionEvent event)
  {
    VaadinService serv = event.getService();
    VaadinSession vsess;
    
    CACData data = (CACData)event.getRequest().getAttribute(CACData.class.getName());  // put in place by Mmowgli2VaadinServlet.sessionInit()
    if(data != null) // why null sometimes?
      if(!CACManager.canProceed(data)) { 
        return Mmowgli2CACError.class;
      }
    
    try {
      vsess = serv.findVaadinSession(event.getRequest());
    }
    catch (SessionExpiredException ex) {
      return Mmowgli2UILogin.class;
    }
    catch (ServiceException sex) {
      return Mmowgli2UIError.class;
    }

    Collection<UI> uis = vsess.getUIs();

    int count = uis.size();

    if (count == 0)
      return Mmowgli2UILogin.class;

    MmowgliSessionGlobals globs = vsess.getAttribute(MmowgliSessionGlobals.class);
    
    // if globs != null, just means servlet has been hit, shouldn't be here
    // if globs ! initted, means Mmowgli2UILogin has not finished init ... send error
    // if glob ! loggedIn, means Mmowgli2UILogin in the sequence of login screens ... send error
    // else, send Subsequent UI, which is UI for 2nd and further browser windows/tabs in same user session
    
    if (globs != null && globs.initted & globs.isLoggedIn()) {
      for (UI ui : uis) {
        if (ui instanceof Mmowgli2UILogin) {
          // so we've got an initted UI, and the user is logged in
          return Mmowgli2UISubsequent.class;
        }
      }
    }
    return Mmowgli2UIError.class; // puts up "incomplete login" verbage
  }
}
