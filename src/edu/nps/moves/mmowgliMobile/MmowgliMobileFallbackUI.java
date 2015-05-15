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

package edu.nps.moves.mmowgliMobile;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import edu.nps.moves.mmowgli.components.HtmlLabel;

/**
 * MmowgliMobileFallbackUI.java
 * Created on Jan 30, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Theme("mmowglimobile")
public class MmowgliMobileFallbackUI extends UI
{
  private static final long serialVersionUID = -3852574001871621481L;
  
  private static final String MSG = "<h1>Ooops...</h1> <p>You accessed MobileMmowgli "
          + "with a browser that is not supported. "
          + "MobileMmowgli is "
          + "meant to be used with modern WebKit based mobile browsers, "
          + "e.g. with iPhone or modern Android devices. Currently those "
          + "cover a huge majority of actively used mobile browsers. "
          + "Support will be extended as other mobile browsers develop "
          + "and gain popularity. Testing ought to work with desktop "
          + "Safari or Chrome as well.";

  @Override
  protected void init(VaadinRequest request)
  {
      Label label = new HtmlLabel(MSG);
      
      VerticalLayout content = new VerticalLayout();
      content.setMargin(true);
      content.addComponent(label);
      setContent(content);
  }
}