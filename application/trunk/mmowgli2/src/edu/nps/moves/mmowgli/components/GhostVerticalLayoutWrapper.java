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

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * GhostVerticalLayoutWrapper.java
 * Created on Apr 27, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class GhostVerticalLayoutWrapper extends VerticalLayout
{
  private static final long serialVersionUID = -3756759885386494152L;
  VerticalLayout head,foot,middle;
  public static String WIDTH = "261px";
  public static String HEADER_HEIGHT = "20px";
  public static String FOOTER_HEIGHT = "20px";
  public static String CONTENT_WIDTH = "206px"; //261px and 25px and 30px padding on each side
  
  public GhostVerticalLayoutWrapper()
  {
    // A fixed width container with background, header and footer
    setSpacing(false);
    this.setMargin(false);
    setWidth(WIDTH);
    
    head=new VerticalLayout();
    head.addStyleName("m-ghostBoxHead");
    head.setSpacing(false);
    head.setMargin(false);
    head.setWidth(WIDTH);
    head.setHeight(HEADER_HEIGHT);
    addComponent(head);
    
    middle = new VerticalLayout();
    middle.setSpacing(false);
    middle.setMargin(false);
    middle.addStyleName("m-ghostBoxMiddle");
    middle.setSizeUndefined();
    middle.setWidth(WIDTH);
    addComponent(middle);
    
    foot = new VerticalLayout();
    foot.setSpacing(false);
    foot.setMargin(false);
    foot.addStyleName("m-ghostBoxFoot");
    foot.setWidth(WIDTH);
    foot.setHeight(FOOTER_HEIGHT);
    addComponent(foot);
  }
 
  public void ghost_setContent(AbstractLayout lay)
  {
    middle.addComponent(lay);
    // won't work? lay.addStyleName("m-ghostBoxContent");
    lay.setWidth(CONTENT_WIDTH);
  }
  public AbstractLayout ghost_getContent()
  {
    return (AbstractLayout)middle.getComponent(0);
  }
}
