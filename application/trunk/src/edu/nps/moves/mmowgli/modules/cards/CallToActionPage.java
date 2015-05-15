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

import static edu.nps.moves.mmowgli.MmowgliConstants.CALLTOACTION_HOR_OFFSET_STR;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliEvent;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.components.VideoWithRightTextPanel;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.utility.IDNativeButton;

/**
 * CallToAction.java Created on Jan 12, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class CallToActionPage extends HorizontalLayout implements MmowgliComponent, View
{
  private static final long serialVersionUID = 8057113786593755301L;

  private VideoWithRightTextPanel vidPan;

  private boolean mockupOnly = false;
  
  public CallToActionPage()
  {
    this(false);
  }
  
  @HibernateSessionThreadLocalConstructor
  public CallToActionPage(boolean mockupOnly)
  {
    this.mockupOnly = mockupOnly;
    initGui();
  }
  
  public void initGui()
  {
    Label spacer = new Label();
    spacer.setWidth(CALLTOACTION_HOR_OFFSET_STR);
    addComponent(spacer);
    VerticalLayout mainVl = new VerticalLayout();
    addComponent(mainVl);
    mainVl.setSpacing(true);
    mainVl.setWidth("100%");

    MovePhase phase = MovePhase.getCurrentMovePhaseTL();
    String sum = phase.getCallToActionBriefingSummary();
    String tx = phase.getCallToActionBriefingText();
    Media v = phase.getCallToActionBriefingVideo();

    Embedded headerImg = new Embedded(null, Mmowgli2UI.getGlobals().mediaLocator().getCallToActionBang());
    headerImg.setDescription("Review motivation and purpose of this game");
    
    NativeButton needButt = new NativeButton();
    needButt.setStyleName("m-weNeedYourHelpButton");

    vidPan = new VideoWithRightTextPanel(v, headerImg, sum, tx, needButt); // needImg);
    vidPan.initGui();
    mainVl.addComponent(vidPan); 
    
    String playCardString = Game.getTL().getCurrentMove().getCurrentMovePhase().getPlayACardTitle();
    NativeButton butt;
    if(!mockupOnly)
      butt = new IDNativeButton(playCardString, MmowgliEvent.PLAYIDEACLICK);
    else
      butt = new NativeButton(playCardString);  // no listener
    butt.addStyleName("borderless");
    butt.addStyleName("m-calltoaction-playprompt");
    butt.setDescription("View existing cards and play new ones");
    mainVl.addComponent(butt);
    mainVl.setComponentAlignment(butt, Alignment.MIDDLE_CENTER);
  }
  /*
   * View interface
   */
  @Override
  public void enter(ViewChangeEvent event)
  {
    // initGui();   
  }
}
