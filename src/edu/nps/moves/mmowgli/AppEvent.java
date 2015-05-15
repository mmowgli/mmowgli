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

import java.util.EventObject;

import com.vaadin.ui.Component;

/**
 * ApplicationEvent.java
 * Created on Dec 13, 2010
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AppEvent extends EventObject
{
  private static final long serialVersionUID = -7925391085409877332L;
  
  private MmowgliEvent mEv;
  private Object data;
  
  public AppEvent (MmowgliEvent mEv, Component source, Object data)
  {
    super(source);
    this.mEv = mEv;
    this.data = data;
  }
  public AppEvent(String uriFragment) throws IllegalArgumentException
  {
    super(new Object());
    parseFragment(uriFragment);
  }
  public MmowgliEvent getEvent()
  {
    return mEv;
  }
  
  public Object getData()
  {
    return data;
  }
  
  public Component getSource()
  {
    return (Component)super.getSource();
  }
  
  public String getFragmentString()
  {
    return "" + mEv.ordinal()+"_"+(getData()==null?"":getData().toString());
  }
  
  public void parseFragment(String s) throws IllegalArgumentException
  {
    if(s == null)
      throw new IllegalArgumentException("null fragment");
    
    s = s.trim();
    if(s.startsWith("!"))
      s = s.substring(1);
    
    if(s.indexOf('_') == -1) {
      try {
        int ord = Integer.parseInt(s);
        mEv = MmowgliEvent.values[ord];  //todo what about legal int, but no event by that number?
        data = null;
      }
      catch(NumberFormatException nex) {
        throw new IllegalArgumentException("unrecognized fragment");
      }
    }
    else {
      try {
        String[] sa = s.split("_");
        if(sa.length <= 0) 
          throw new IllegalArgumentException("unrecognized fragment");
        
        int ord = Integer.parseInt(sa[0]);
        if(sa.length <= 1)   {
          mEv = MmowgliEvent.values[ord];
          data = "";
        }
        else {
          int dat = Integer.parseInt(sa[1]);
          mEv = MmowgliEvent.values[ord];
          data = ""+dat;
        }
      }
      catch(NumberFormatException numex) {
        throw new IllegalArgumentException("unrecognized fragment");
      }
    }
  }
}
