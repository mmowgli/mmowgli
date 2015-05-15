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

package edu.nps.moves.mmowgli.modules.cards;

import static edu.nps.moves.mmowgli.MmowgliConstants.CALLTOACTION_VIDEO_H;
import static edu.nps.moves.mmowgli.MmowgliConstants.CALLTOACTION_VIDEO_W;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * HowToPlayCardsPopup.java Created on Feb 26, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class HowToPlayCardsPopup extends MmowgliDialog implements ClickListener
{
  private static final long serialVersionUID = -4806292137082005538L;

  @HibernateSessionThreadLocalConstructor
  public HowToPlayCardsPopup()
  {
    super(null);
    super.initGui();

    setModal(true);
    setListener(this);

    setTitleString("How to Play");
    Media m = getMedia();
    Component comp = new Label("Not found");

    if (m.getType() == Media.MediaType.VIDEO) {
      /*
       * Quicktime qt = new Quicktime(null,res); qt.setWidth("94%"); qt.setHeight("340px"); //"100%"); qt.setScale(Scale.Aspect); qt.setAutoplay(true); comp =
       * qt;
       */
    }
    else if (m.getType() == Media.MediaType.YOUTUBE) {

      try {
        Flash ytp = new Flash();
        ytp.setSource(new ExternalResource("https://www.youtube.com/v/" + m.getUrl()));
        ytp.setParameter("allowFullScreen", "true");
        ytp.setParameter("showRelated", "false");
        ytp.setWidth(CALLTOACTION_VIDEO_W);
        ytp.setHeight(CALLTOACTION_VIDEO_H);
        comp = ytp;
      }
      catch (Exception ex) {
        System.err.println("Exception instantiating YouTubPlayer: " + ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage());
      }
    }
    contentVLayout.addComponent(comp);
    contentVLayout.setComponentAlignment(comp, Alignment.MIDDLE_CENTER);
  }

  protected void addLowerComponent(Component c)
  {
    contentVLayout.addComponent(c);
    contentVLayout.setComponentAlignment(c, Alignment.MIDDLE_CENTER);
  }

  protected Media getMedia()
  {
    return Mmowgli2UI.getGlobals().mediaLocator().getHowToPlayCardsVideoMediaTL();
  }

  @Override
  public Long getUserId()
  {
    return null;
  }

  @Override
  public void setUser(User u)
  {
  }

  @Override
  public void buttonClick(ClickEvent event)
  {
    UI.getCurrent().removeWindow(this);
  }
}
