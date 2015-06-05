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

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
/**
 * Mmowgli2CACError.java
 * Created on June 1, 2015
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@Theme("mmowgli2")
public class Mmowgli2CACError extends UI
{
  private static final long serialVersionUID = 4258310524821294859L;

  @Override
  protected void init(VaadinRequest request)
  {
    final VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    setContent(layout);
    Page.getCurrent().setTitle("Mmowgli Authentication Error");
    layout.addComponent(new Label("A Common Access Card (CAC) is required for entry into this game."));
    layout.addComponent(new Label("You may try reloading this page after insertion of a CAC into an attached reader."));
    
    new Thread(new Runnable() {
      @Override
      public void run()
      {
        Mmowgli2CACError.this.access(new Runnable() {
          public void run()
          {
            System.out.println("No CAC -- killing session");
            getSession().close();
          }
        });
      }
    }).start();
  } 
}
