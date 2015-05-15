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

import static edu.nps.moves.mmowgli.MmowgliConstants.PORTALTARGETWINDOWNAME;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.modules.cards.HowToPlayCardsPopup;

/**
 * HowToWinActionPopup.java Created on Feb 26, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class HowToWinActionPopup extends HowToPlayCardsPopup
{
  private static final long serialVersionUID = 9054620543272655877L;
  
  @HibernateSessionThreadLocalConstructor
  public HowToWinActionPopup(String title)
  {
    super();
    setTitleString(title);
    
    VerticalLayout vl = new VerticalLayout();
    vl.setMargin(true);
    
    Link l = new Link("Learn more",new ExternalResource("http://portal.mmowgli.nps.edu/instructions"));
    l.setTargetName(PORTALTARGETWINDOWNAME);
    l.setTargetBorder(BorderStyle.DEFAULT);
    l.addStyleName("m-learn-more-link");
    vl.addComponent(l);
    vl.setComponentAlignment(l,Alignment.MIDDLE_CENTER);
    addLowerComponent(vl);
  }
  
  protected Media getMedia()
  {
    return Mmowgli2UI.getGlobals().getMediaLocator().getHowToWinActionMediaTL();    
  }
}
