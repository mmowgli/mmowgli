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

package edu.nps.moves.mmowgli.utility;

import java.util.Vector;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.NativeButton;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliEvent;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 *
 * @version	$Id$
 * @since $Date$
 * @copyright	Copyright (C) 2011
 */

public class IDNativeButton extends NativeButton implements IDButtonIF, ClickListener
{
  private static final long serialVersionUID = 4087507984175224153L;
  boolean attached=false;
  MmowgliEvent mEv;
  Object param;
  public IDNativeButton(String label, MmowgliEvent mEv, Object param)
  {
    super(label);
    this.mEv = mEv;
    this.param = param;
  }
  
   public IDNativeButton(String label, MmowgliEvent mEv)
  {
    this(label,mEv,null);
  }
  
  @Override
  public MmowgliEvent getEvent()
  {
    return mEv;
  }
  
  @Override
  public Object getParam()
  {
    return param;
  }
  
  @Override
  public void setParam(Object param)
  {
    this.param = param;
  }

  @Override
  public void setEvent(MmowgliEvent mEv)
  {
	  this.mEv = mEv;  
  }
 
  private boolean locallyEnabled = true;
  public void enableAction(boolean yn)
  {
     locallyEnabled = yn;
  }  
  
  @Override
  public void attach()
  {
    attached=true;
    super.attach();
    super.addClickListener((ClickListener)this);
    for(ClickListener cLis : lis)
      super.addClickListener(cLis);
  }

  @Override
  public void buttonClick(ClickEvent event)
  {
    if(locallyEnabled)
      Mmowgli2UI.getGlobals().getController().buttonClick(event);
  }

  Vector<ClickListener> lis = new Vector<ClickListener>();
  @Override
  public void addListener(ClickListener listener)
  {
    if(!attached)
      lis.add(listener);
    else
      super.addClickListener(listener);
  }
  
  public void addVIPListener(ClickListener lis)
  {
    super.addClickListener(lis);
  }
}