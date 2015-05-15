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

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

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
public class ActionPlansGameDesignPanel extends AbstractGameBuilderPanel
{
  private static final long serialVersionUID = -4769171145429094345L;

  @HibernateSessionThreadLocalConstructor
  public ActionPlansGameDesignPanel(GameDesignGlobals globs)
  {
    super(false,globs);
    Game game = Game.getTL(1L);
    String thePlanTxt = game.getDefaultActionPlanThePlanText();
    String talkTxt = game.getDefaultActionPlanTalkText();
    String imagesTxt = game.getDefaultActionPlanImagesText();
    String videosTxt = game.getDefaultActionPlanVideosText();
    String mapTxt = game.getDefaultActionPlanMapText();
    
    TextArea ta;
    ta = (TextArea)addEditLine("1 \"The Plan\" Tab Instructions", "Game.defaultActionPlanThePlanText", game, game.getId(), "DefaultActionPlanThePlanText").ta;
    ta.setValue(thePlanTxt);
    ta.setRows(5);
    ta = (TextArea)addEditLine("2 \"Talk it Over\" Tab Instructions", "Game.defaultActionPlanTalkText", game, game.getId(), "DefaultActionPlanTalkText").ta;
    ta.setValue(talkTxt);
    ta.setRows(5);
    ta = (TextArea)addEditLine("3 Images Tab Instructions", "Game.defaultActionPlanImagesText", game, game.getId(), "DefaultActionPlanImagesText").ta;
    ta.setValue(imagesTxt);
    ta.setRows(5);
    ta = (TextArea)addEditLine("4 Videos Tab Instructions", "Game.defaultActionPlanVideosText", game, game.getId(), "DefaultActionPlanVideosText").ta;
    ta.setValue(videosTxt);
    ta.setRows(5);
    ta = (TextArea)addEditLine("5 Map Tab Instructions", "Game.defaultActionPlanMapText", game, game.getId(), "DefaultActionPlanMapText").ta;
    ta.setValue(mapTxt);
    ta.setRows(5);

  }
  @Override
  Embedded getImage()
  {
    ClassResource cr = new ClassResource("/edu/nps/moves/mmowgli/modules/administration/actionplantexts.png");
    Embedded e = new Embedded(null,cr);
    return e;
  }  
/*  
  @Override
  protected void testButtonClicked(ClickEvent ev)
  {
    // I'll try the first 10:
    for(long lon = 1; lon<=10;lon++) {
      ActionPlan ap = ActionPlan.get(lon);
      if(ap != null) {
        ApplicationEvent evt = new ApplicationEvent(GAMEADMIN_SHOW_ACTIONPLAN_MOCKUP, this, lon);
        ((ApplicationEntryPoint)getApplication()).globs().controller().miscEvent(evt);
        return;
      }
    }  
  }
*/
  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 150; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 30; // default = 240
  }
}
