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
  along with Mmowgli, in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
 */

package edu.nps.moves.mmowgli.utility;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;

/**
 * BackArrowFontIcon.java created on Mar 19, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class BackArrowFontIcon implements FontIcon
{
  private static final long serialVersionUID = 2264120643738215441L;
  
  public static final String fontFamily = "FontAwesome";
  public static final int codepoint = FontAwesome.PLAY.getCodepoint();

  // The following is the only part of this which we use, since we're not adding this as a font icon.
  @Override
  public String getHtml()
  {
    //return "<span class=\"v-icon fa-rotate-180\" style=\"font-family: " + fontFamily + ";\">&#x" + Integer.toHexString(codepoint) + ";</span>";
    return "<i class=\"fa fa-play fa-rotate-180\"/>";
  }

  @Override
  public String getFontFamily()
  {
    return fontFamily;
  }

  @Override
  public int getCodepoint()
  {
    return codepoint;
  }

  @Override
  public String getMIMEType()
  {
    throw new UnsupportedOperationException(getClass().getSimpleName() + " should not be used where a MIME type is needed.");
  }
}
