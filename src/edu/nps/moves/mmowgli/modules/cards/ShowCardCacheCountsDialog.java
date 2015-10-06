/*
  Copyright (C) 2010-2015 Modeling Virtual Environments and Simulation
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
  along with Mmowgli, in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.modules.cards;

import java.util.Map;

import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.cache.MCacheManager;

/**
 * InstallImageDialog.java created on Feb 26, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class ShowCardCacheCountsDialog extends Window
{
  private static final long serialVersionUID = -5306188191479283113L;

  public static void show()
  {
	ShowCardCacheCountsDialog dialog = new ShowCardCacheCountsDialog();
    dialog.center();
    UI.getCurrent().addWindow(dialog);
  }
   
  private ShowCardCacheCountsDialog()
  {
    setCaption("Show Card Cache Sizes");
    setModal(true);
    setWidth("350px");
    FormLayout fl = new FormLayout();
    setContent(fl);
    fl.setMargin(true);
    fl.setSpacing(true);
    
    MCacheManager mgr = MCacheManager.instance();
    Object[][] caches = mgr.getCaches();
    Label lab;
    
    for(int i=0;i<caches[0].length;i++) {
      if(!(caches[0][i] instanceof String))
    	  continue;
      if(!(caches[1][i] instanceof Map))
    	  continue;
      fl.addComponent(lab = new Label(""+((Map<?,?>)caches[1][i]).size()));
      lab.setCaption(caches[0][i].toString());
    }  
  }  
}
