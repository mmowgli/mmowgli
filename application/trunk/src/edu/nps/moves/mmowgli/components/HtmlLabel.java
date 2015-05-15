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

package edu.nps.moves.mmowgli.components;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

/**
 * HtmlLabel.java
 * Created on May 17, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class HtmlLabel extends Label
{
  private static final long serialVersionUID = -3865144993138255689L;

  public HtmlLabel()
  {
    super();
    setContentMode(ContentMode.HTML);
  }

  public HtmlLabel(String content)
  {
    super(content);
    setContentMode(ContentMode.HTML);
  }

  @SuppressWarnings("rawtypes")
  public HtmlLabel(Property contentSource)
  {
    super(contentSource);
    setContentMode(ContentMode.HTML);
  }

  public HtmlLabel(String content, ContentMode mode)
  {
    super(content, mode);
    setContentMode(ContentMode.HTML); // override since the is HTML label
  }

  @SuppressWarnings("rawtypes")
  public HtmlLabel(Property contentSource, ContentMode mode)
  {
    super(contentSource, mode);
    setContentMode(ContentMode.HTML); // override since the is HTML label
  }
  
  public HtmlLabel(String content, String cssStyle)
  {
    this(content);
    addStyleName(cssStyle);
  }
}
