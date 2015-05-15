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

package edu.nps.moves.mmowgli.modules.cards;

import java.util.List;

import com.vaadin.ui.Label;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * ActionPlanPageTabImages.java
 * Created on Feb 8, 2011
 * Updated 26 Mar, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class IdeaDashboardTabDefend extends IdeaDashboardTabPanel
{
  private static final long serialVersionUID = 3579313668272173564L;
  
  private boolean initted = false;
  
  @HibernateSessionThreadLocalConstructor
  public IdeaDashboardTabDefend()
  {
    super();
  }
  
  @Override
  public void initGui()
  {
    String defendCardName = CardType.getNegativeIdeaCardTypeTL().getTitle();
    Label leftLabel = new Label(
        "This is a list of all "+defendCardName+" cards that have been played.");
    getLeftLayout().addComponent(leftLabel, "top:0px;left:0px"); 
  }
  
  @Override
  public List<Card> getCardList()
  {
    return null;
  }

  @Override
  boolean confirmCard(Card c)
  {
    return false;
  }

  @Override
  public void setVisible(boolean visible)
  {
    super.setVisible(visible);
    if(visible)
      if(!initted) {
        buildCardClassTable(CardTypeManager.getNegativeIdeaCardTypeTL()); // getting class instead of type since the root idea cards can change move to move
        initted=true;
      }
  }
}
