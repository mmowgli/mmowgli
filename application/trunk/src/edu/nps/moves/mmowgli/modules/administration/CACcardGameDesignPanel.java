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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Embedded;

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * CACcardGameDesignPanel.java
 * Created on May 29, 2015
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CACcardGameDesignPanel extends AbstractGameBuilderPanel
{
  private static final long serialVersionUID = 8189771164860435872L;
  private static final long dbObjId = 1L;

  private CheckBox enforceCB;
  private CheckBox requireRegCB;
  
  @SuppressWarnings("serial")
  @HibernateSessionThreadLocalConstructor
  public CACcardGameDesignPanel(GameDesignGlobals globs)
  {
    super(false,globs);
    Game g = Game.getTL(dbObjId);

//@formatter:off
    EditLine reqLine     = addEditBoolean("Require CAC presence for registration",          "Game.requireCACregistration",     g, dbObjId, "RequireCACregistration");
    EditLine enforceLine = addEditBoolean("Enforce CAC data (email, name) in registration", "Game.enforceCACdataRegistration", g, dbObjId, "EnforceCACdataRegistration");
    addSeparator();
    addEditBoolean("Require CAC presence for login", "Game.requireCAClogin", g, dbObjId, "RequireCAClogin");
    addEditBoolean("Use CAC data for quick login",   "Game.useCAClogin",     g, dbObjId, "UseCAClogin");
//@formatter:on
    
    // if cac data is enforced in registration, the card must be first be required, so force the other to true
    // conversely, if the cac is not required, the data can't be enforced
    enforceCB = (CheckBox)enforceLine.ta;
    requireRegCB = (CheckBox)reqLine.ta;
    
    enforceCB.addValueChangeListener(new ValueChangeListener() {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        if(enforceCB.getValue()) {
          requireRegCB.setValue(true);          
        }        
      }     
    });
    
    requireRegCB.addValueChangeListener(new ValueChangeListener() {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        if(!requireRegCB.getValue()) {
          enforceCB.setValue(false);          
        }        
      }           
    });
  }

  @Override
  Embedded getImage()
  {
    return null;
  }

  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 250; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 40; // default = 240
  }
}