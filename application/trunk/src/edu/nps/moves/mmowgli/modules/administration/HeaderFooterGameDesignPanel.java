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

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.GameLinks;
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
public class HeaderFooterGameDesignPanel extends AbstractGameBuilderPanel
{
  private static final long serialVersionUID = -4772309985926200842L;

  @HibernateSessionThreadLocalConstructor
  public HeaderFooterGameDesignPanel(GameDesignGlobals globs)
  {
    super(false,globs);
    setWidth("100%");

    GameLinks links = GameLinks.getTL();
//@formatter:off
    addEditLine("1 Game Blog Link",  "GameLinks.blogLink",     links, links.getId(), "BlogLink");
    addEditLine("2 Learn More Link", "GameLinks.learnMoreLink",links, links.getId(), "LearnMoreLink");
    addSeparator();
    addEditLine("3 About Link",      "GameLinks.aboutLink",    links, links.getId(), "AboutLink");
    addEditLine("4 Credits Link",    "GameLinks.creditsLink",  links, links.getId(), "CreditsLink");
    addEditLine("5 FAQs Link",       "GameLinks.faqLink",      links, links.getId(), "FaqLink");
    addEditLine("6 Fixes Link",      "GameLinks.fixesLink",    links, links.getId(), "FixesLink");
    addEditLine("7 Glossary Link",   "GameLinks.glossaryLink", links, links.getId(), "GlossaryLink");
    addEditLine("8 Terms Link",      "GameLinks.termsLink",    links, links.getId(), "TermsLink");
    addEditLine("9 Trouble Link",    "GameLinks.troubleLink",  links, links.getId(), "TroubleLink");
    addEditLine("10 Videos Link",    "GameLinks.videosLink",   links, links.getId(), "VideosLink");
//@formatter:on
    addSeparator();
    addEditBoolean("11 Show FOUO branding","Game.showFouo", Game.getTL(), 1L, "ShowFouo");
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
    return super.getColumn1PixelWidth() + 60; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 90; // default = 240
  }
}
