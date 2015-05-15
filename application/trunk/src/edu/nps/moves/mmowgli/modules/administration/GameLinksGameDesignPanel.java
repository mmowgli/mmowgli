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

import edu.nps.moves.mmowgli.db.GameLinks;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * GameLinksDesignPanel.java
 * Created on Nov 27, 2013
 * Updated on Mar 12, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class GameLinksGameDesignPanel extends AbstractGameBuilderPanel
{
  private static final long serialVersionUID = 3048712939632579777L;

  @HibernateSessionThreadLocalConstructor
  public GameLinksGameDesignPanel(GameDesignGlobals globs)
  {
    super(false,globs);
    setWidth("100%");

    GameLinks links = GameLinks.getTL();
//@formatter:off    
    ((TextArea)addEditLine("1 Action plan request link",  "GameLinks.actionPlanRequestLink", links, links.getId(), "ActionPlanRequestLink").ta).setRows(1);
    ((TextArea)addEditLine("2 FOUO link",                 "GameLinks.fouoLink",              links, links.getId(), "FouoLink").ta)             .setRows(1);
    ((TextArea)addEditLine("3 Game email sender",         "GameLinks.gameFromEmail",         links, links.getId(), "GameFromEmail").ta)        .setRows(1);
    ((TextArea)addEditLine("4 Game-full link",            "GameLinks.gameFullLink",          links, links.getId(), "GameFullLink").ta)         .setRows(1);
    ((TextArea)addEditLine("5 Game home URL",             "GameLinks.gameHomeUrl",           links, links.getId(), "GameHomeUrl").ta)          .setRows(1);
    TextArea ta = ((TextArea)addEditLine("6 How-to-play link",          "GameLinks.howToPlayLink",         links, links.getId(), "HowToPlayLink").ta);
    ta.setRows(1);
    ta.setInputPrompt("An empty entry signifies default behavior");
    ta.setNullRepresentation("");
    ta.setNullSettingAllowed(true);
    ((TextArea)addEditLine("7 Improve your score link",   "GameLinks.improveScoreLink",      links, links.getId(), "ImproveScoreLink").ta)     .setRows(1);
    ((TextArea)addEditLine("8 Informed consent link",     "GameLinks.informedConsentLink",   links, links.getId(), "InformedConsentLink").ta)  .setRows(2);
    ((TextArea)addEditLine("9 Map link",                  "GameLinks.mmowgliMapLink",        links, links.getId(), "MmowgliMapLink").ta)       .setRows(4);
    ((TextArea)addEditLine("10 Survey consent link",      "GameLinks.surveyConsentLink",     links, links.getId(), "SurveyConsentLink").ta)    .setRows(2);
    ((TextArea)addEditLine("11 Thanks for interest link", "GameLinks.thanksForInterestLink", links, links.getId(), "ThanksForInterestLink").ta).setRows(1);
    ((TextArea)addEditLine("12 Thanks for playing link",  "GameLinks.thanksForPlayingLink",  links, links.getId(), "ThanksForPlayingLink").ta) .setRows(1);
    ((TextArea)addEditLine("13 Trouble mail-to address",  "GameLinks.troubleMailto",         links, links.getId(), "TroubleMailto").ta)        .setRows(2);
    ((TextArea)addEditLine("14 User agreement link",      "GameLinks.userAgreementLink",     links, links.getId(), "UserAgreementLink").ta)    .setRows(1);
//@formatter:off
  }
  
  @Override
  public void initGui()
  {
    super.initGui();
  }
  
  @Override
  Embedded getImage()
  {
    return null;
  }
  
  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 110; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 40; // default = 240
  }
}
