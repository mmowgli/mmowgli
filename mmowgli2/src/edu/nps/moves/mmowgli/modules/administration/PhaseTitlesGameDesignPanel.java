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

import edu.nps.moves.mmowgli.db.MovePhase;

/**
 * HeaderFooterGameDesignPanel.java
 * Created on Mar 28, 2013
 * Updated on Mar 12, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class PhaseTitlesGameDesignPanel extends AbstractGameBuilderPanel implements MovePhaseChangeListener
{
  private static final long serialVersionUID = 7466151010695755468L;

  //private MovePhase activePhase;
  
  public PhaseTitlesGameDesignPanel(MovePhase phase, AuxiliaryChangeListener auxLis, GameDesignGlobals globs)
  {
    super(false,globs);
    //activePhase = phase;
    addEditLine("1 Browser window title", "MovePhase.windowTitle", phase, phase.getId(),"WindowTitle")   .auxListener = auxLis;
    addEditLine("2 Card play prompt", "MovePhase.playACardTitle", phase, phase.getId(), "PlayACardTitle").auxListener = auxLis;
  }
   
   @Override
  Embedded getImage()
  {
    return null;
  }

  @Override
  public void movePhaseChanged(MovePhase newPhase)
  {
    //activePhase = newPhase;
    okToUpdateDbFlag = false; 
    changeMovePhase(newPhase); 
    okToUpdateDbFlag = true; 
  }
  
  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 120; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 80; // default = 240
  }
}
