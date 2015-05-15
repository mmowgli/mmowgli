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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.db.*;

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
public class WelcomeScreenGameDesignPanel extends AbstractGameBuilderPanel implements MovePhaseChangeListener
{
  private static final long serialVersionUID = -3079923547334639204L;
  
  private EditLine orientationVideoLine;
  
  public WelcomeScreenGameDesignPanel(MovePhase phase, AuxiliaryChangeListener auxLis, GameDesignGlobals globs)
  {
    super(false,globs);

    addEditComponent("1 Video", "MovePhase.orientationVideo",new VideoChangerComponent(phase,"setOrientationVideo",phase.getOrientationVideo(),globs)).auxListener = auxLis;
    addEditLine("2 Text", "MovePhase.orientationCallToActionText", phase, phase.getId(), "OrientationCallToActionText").auxListener = auxLis;
    EditLine edLine = addEditLine("3 Headline","MovePhase.orientationHeadline",phase, phase.getId(), "OrientationHeadline");
    TextArea ta = (TextArea)edLine.ta;
    ta.setRows(12); // bump up from default of 2
    edLine.auxListener = auxLis;
    addEditLine("3 Summary", "MovePhase.orientationSummary", phase, phase.getId(), "OrientationSummary").auxListener=auxLis;
  }
  
  class MyMoveListener implements MoveListener
  {
    @Override
    public void setMove(Move m)
    {
      Media med =  m.getCurrentMovePhase().getOrientationVideo();
      orientationVideoLine.objId = med.getId();
      ((TextArea)orientationVideoLine.ta).setValue(med.getUrl());
    }   
  }

  @Override
  Embedded getImage()
  {
    ClassResource cr = new ClassResource("/edu/nps/moves/mmowgli/modules/administration/welcomeshot.png");
    Embedded e = new Embedded(null,cr);
    return e;
  }

  @Override
  protected void testButtonClickedTL(ClickEvent ev)
  {
    Game.updateTL(Game.getTL(1L));  // cause page title to be redrawn
    AppEvent evt = new AppEvent(MmowgliEvent.GAMEADMIN_SHOW_WELCOME_MOCKUP, this, null);
    Mmowgli2UI.getGlobals().getController().miscEventTL(evt);
  }

  @Override
  public void movePhaseChanged(MovePhase newPhase)
  {
    okToUpdateDbFlag = false; 
    changeMovePhase(newPhase); 
    okToUpdateDbFlag = true; 
  }
}
