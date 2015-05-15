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

package edu.nps.moves.mmowgli.modules.registrationlogin;

import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.GameLinks;

/**
 * RegistrationPageSurvey.java Created on Dec 15, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RegistrationPageSurvey extends RegistrationPageAgreement
{
  private static final long serialVersionUID = -207571125353979178L;

  public RegistrationPageSurvey(ClickListener listener)
  {
    super(listener);
  }

  @Override
  protected String getTitle()
  {
    return "Postgame Optional Survey Request";
  }

  @Override
  protected String getLabelText()
  {
    Game g = Game.getTL();
    GameLinks gl = GameLinks.getTL();
    // Hack
    if(gl.getFixesLink().toLowerCase().contains("armyscitech") || gl.getGlossaryLink().toLowerCase().contains("armyscitech"))
      return "<p>Thanks for playing the exercise!</p><p>We are interested in your opinions.  This is optional.</p>";
    else {
      String handle = g.getGameHandle();
      if(handle != null && handle.length()>0)
        handle = handle.toUpperCase();
      else
        handle = "MMOWGLI";
      return "<p>Thanks for playing the "+handle+" game!</p><p>We are interested in your opinions.  This is optional.</p>";
    }
  }

  @Override
  protected String getReadUrlTL()
  {
    return GameLinks.getTL().getSurveyConsentLink();
  }

  @Override
  protected String getReadLabel()
  {
    return "<i>Consent to Participate in Anonymous Survey</i>";
  } 
}
