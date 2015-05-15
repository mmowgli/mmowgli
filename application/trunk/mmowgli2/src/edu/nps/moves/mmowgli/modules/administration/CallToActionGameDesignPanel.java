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

import com.vaadin.server.ClassResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.TextArea;

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
public class CallToActionGameDesignPanel extends AbstractGameBuilderPanel implements MovePhaseChangeListener
{
  private static final long serialVersionUID = -5151119059574019023L;

  public CallToActionGameDesignPanel(MovePhase phase, AuxiliaryChangeListener auxLis, GameDesignGlobals globs)
  {
    super(false,globs);
    
    addEditComponent("1 Video", "MovePhase.callToActionBriefingVideo",new VideoChangerComponent(phase,"setCallToActionBriefingVideo",phase.getCallToActionBriefingVideo(),globs)).auxListener = auxLis;
    addEditLine("2 Summary", "MovePhase.callToActionBriefingSummary", phase, phase.getId(), "CallToActionBriefingSummary").auxListener = auxLis;
    EditLine edLine = addEditLine("3 Text ","MovePhase.callToActionBriefingText",phase, phase.getId(), "CallToActionBriefingText");
    TextArea ta = (TextArea)edLine.ta;
    ta.setRows(12);
    edLine.auxListener = auxLis;
  }
  
  @Override
  Embedded getImage()
  {
    ClassResource cr = new ClassResource("/edu/nps/moves/mmowgli/modules/administration/call2action.png");
    Embedded e = new Embedded(null,cr);
    return e;
  }  

  @Override
  public void movePhaseChanged(MovePhase newPhase)
  {
    okToUpdateDbFlag = false; 
    changeMovePhase(newPhase);
    // todo do something with vidComp
    okToUpdateDbFlag = true; 
  }

}
