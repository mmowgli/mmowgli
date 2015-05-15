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

import java.io.Serializable;
import java.text.SimpleDateFormat;

import com.vaadin.ui.AbstractOrderedLayout;

import edu.nps.moves.mmowgliMobile.data.*;

/**
 * MessageRenderer.java
 * Created on Feb 24, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class EntryRenderer2
{
  abstract public void setMessage(FullEntryView2 mView, ListEntry msg, ListView2 messageList, AbstractOrderedLayout layout);
 
  protected SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy hh:mm");
  protected Serializable getPojoId(ListEntry ent)
  {
    if(ent instanceof CardListEntry)
      return ((CardListEntry)ent).getCard().getId();
    if(ent instanceof UserListEntry)
      return ((UserListEntry)ent).getUser().getId();
    if(ent instanceof ActionPlanListEntry)
      return ((ActionPlanListEntry)ent).getActionPlan().getId();
    return null;     
  }
}
