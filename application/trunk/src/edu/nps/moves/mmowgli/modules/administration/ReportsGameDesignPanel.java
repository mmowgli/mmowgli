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

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;

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
public class ReportsGameDesignPanel extends AbstractGameBuilderPanel
{
  private static final long serialVersionUID = -5845831597615764415L;

  @HibernateSessionThreadLocalConstructor
  @SuppressWarnings("serial")
  public ReportsGameDesignPanel(GameDesignGlobals globs)
  {
    super(false,globs);
    Game g = Game.getTL();
    long period = g.getReportIntervalMinutes();
    
    TextArea ta;
    
    ta = addEditLine("1 Game Reports publishing interval (minutes)", "Game.reportIntervalMinutes");
    boolean lastRO = ta.isReadOnly();
    ta.setReadOnly(false);
    ta.setValue(""+period);
    ta.setRows(1); 
    ta.setReadOnly(lastRO);
    ta.addValueChangeListener(new Property.ValueChangeListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void valueChange(ValueChangeEvent event)
      {
        //System.out.println("msid valueChange");
        HSess.init();
        try {
          String val = event.getProperty().getValue().toString();
          long lg = Long.parseLong(val);
          if(lg < 0)
            throw new Exception();
          
          MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
          Game gm = Game.getTL(1L);
          gm.setReportIntervalMinutes(lg);
          Game.updateTL();
          GameEventLogger.logGameDesignChangeTL("Report interval", ""+""+lg, globs.getUserID());
         
          // Wake it up
          AppMaster.instance().pokeReportGenerator();
        }
        catch (Exception ex) {
          new Notification("Parameter error", "<html>Check for proper positive integer format.</br>New value not committed.",Notification.Type.WARNING_MESSAGE,true).show(Page.getCurrent());
        }
        HSess.close();
      }
    });
    addEditBoolean("2 Indicate PDF reports available","Game.pdfAvailable",g, 1L, "PdfAvailable");
    addEditBoolean("2 Show hidden cards","Game.reportsShowHiddenCards",g, 1L, "ReportsShowHiddenCards");
  }
   
  @Override
  Embedded getImage()
  {
    return null;
  }

  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 240; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 60; // default = 240
  }

  @Override
  protected String getTextButtonText()
  {
    return "Save";  // just used to switch focus and cause propertyListener to be hit
  }
}
