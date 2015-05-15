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

package edu.nps.moves.mmowgliMobile.ui;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.db.GameLinks;
import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * NotAllowedPopover.java created on Oct 28, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class NotAllowedPopover extends Popover
{
  private static final long serialVersionUID = -503141823070900404L;

  @SuppressWarnings("serial")
  public NotAllowedPopover()
  {
    setWidth("75%");

    VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    layout.setSpacing(true);
    setHeight("150px");

    Label lbl;
    layout.addComponent(lbl = new Label("This mmowgli game does not allow guest access.  Thank you for your interest."));

    layout.setExpandRatio(lbl, 1.0f);
    Button closeButt;
    layout.addComponent(closeButt = new Button("Close"));

    NavigationView nv = new NavigationView(layout);
    nv.setCaption("Access Not Allowed");
    setContent(nv);
    this.center();

    closeButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        GameLinks links = GameLinks.getTL();
        HSess.close();

        UI.getCurrent().getPage().setLocation(links.getThanksForInterestLink());
        getSession().close();
      }
    });
  }
}
