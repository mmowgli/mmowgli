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

package edu.nps.moves.mmowgli.modules.administration;

import com.vaadin.ui.Embedded;
import com.vaadin.ui.TextArea;

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * HeaderFooterGameDesignPanel.java
 * Created on Mar 28, 2013
 * Updated 13 Mar, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: BooleansGameDesignPanel.java 3268 2014-01-11 01:47:37Z tdnorbra $
 */
public class BooleansGameDesignPanel extends AbstractGameBuilderPanel
{
  private static final long serialVersionUID = 6176067174697054429L;
  
  private static final long dbObjId = 1L;

  @HibernateSessionThreadLocalConstructor
  public BooleansGameDesignPanel(GameDesignGlobals globs)
  {
    super(false,globs);
    Game g = Game.getTL(dbObjId);
    TextArea ta = (TextArea)addEditLine("Game title", "Game.title", g, dbObjId,"Title",null,String.class,"Used in game reports").ta;
    ta.setRows(1);
    addSeparator();
//@formatter:off
    addEditBoolean("Set entire game read-only",                    "Game.readonly",                  g, dbObjId, "Readonly");
    addEditBoolean("Require email confirmation",                   "Game.emailConfirmation",         g, dbObjId, "EmailConfirmation");
    addEditBoolean("Show 2nd login permission page",               "Game.secondLoginPermissionPage", g, dbObjId, "SecondLoginPermissionPage");
    addSeparator();
    addEditBoolean("Set all cards read-only",                      "Game.cardsReadonly",             g, dbObjId, "CardsReadonly");
    addEditBoolean("Set top cards read-only",                      "Game.topCardsReadonly",          g, dbObjId, "TopCardsReadonly");
    addEditBoolean("Show cards from prior rounds",                 "Game.showPriorMovesCards",       g, dbObjId, "ShowPriorMovesCards");
    addEditBoolean("Allow play on cards from prior rounds",        "Game.playOnPriorMovesCards",     g, dbObjId, "PlayOnPriorMovesCards");
    addSeparator();
    addEditBoolean("Enable action plans for this game",            "Game.isActionPlansEnabled",      g, dbObjId, "ActionPlansEnabled", "For specific game requirements where Action Plans are desired");
    addEditBoolean("Show action plans from prior rounds",          "Game.showPriorMovesActionPlans", g, dbObjId, "ShowPriorMovesActionPlans");
    addEditBoolean("Allow edits on action plans from prior rounds","Game.editPriorMovesActionPlans", g, dbObjId, "EditPriorMovesActionPlans");
//@formatter:on
  }

  @Override
  Embedded getImage()
  {
    return null;
  }

  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 250; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 0; // default = 240
  }
}
