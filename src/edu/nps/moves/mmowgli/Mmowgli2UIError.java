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

package edu.nps.moves.mmowgli;

import java.util.UUID;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.nps.moves.mmowgli.markers.HasUUID;
/**
 * Mmowgli2UILogin.java
 * Created on Apr 28, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@Theme("mmowgli2")
public class Mmowgli2UIError extends UI implements HasUUID
{
  private static final long serialVersionUID = 9069779406128535862L;
  private UUID uuid;
  @SuppressWarnings("serial")
  @Override
  protected void init(VaadinRequest request)
  {
    uuid = UUID.randomUUID();
    
    final VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    setContent(layout);
    Page.getCurrent().setTitle("Mmowgli Login Error");
    layout.addComponent(new Label("Whoops!  It looks like you are opening a second mmowgli window or tab without completing your log-in in the first."));
    //layout.addComponent(new Label("You appear to have an incomplete log-in session pending in another tab or window."));
    layout.addComponent(new Label("If this is the case, close this tab and continue with your log-in."));
    layout.addComponent(new Label("If that window or tab is no longer available, begin a new Mmowgli session by clicking the following button."));
    //layout.addComponent(new Label("Any previously entered information will be discarded."));
    
    Button button = new Button("Begin new Mmowgli session");
    button.addClickListener(new Button.ClickListener() {
      public void buttonClick(ClickEvent event)
      {
        getPage().setLocation(getPage().getLocation());
        getSession().close();
      }
    });
    layout.addComponent(button);
  }
  
  public String getUI_UUID()
  {
    return uuid.toString();
  } 
}
