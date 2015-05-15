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

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.NativeButton;

/**
 * ToggleLinkButton.java
 * Created on Aug 17, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class ToggleLinkButton extends NativeButton implements ClickListener
{
  private static final long serialVersionUID = 873592948496343086L;

  public static String STYLE = "m-link-button";
  
  private ClickListener onListener, offListener;
  private String onText="on", offText="off";
  private String onTT=null,offTT=null;
  
  boolean on = true;
  
  public ToggleLinkButton(String onText, String offText)
  {
    this(onText,offText,STYLE);
  }
  
  public ToggleLinkButton(String onText, String offText, String style)
  {
    super(onText);
    this.onText = onText;
    this.offText = offText;

    setStyleName(BaseTheme.BUTTON_LINK);
    addStyleName("borderless");
    addStyleName(style==null?STYLE:style);
    setDescription(onText);  // I believe if you set this to null here, you can never thereafter set a tt
    addClickListener((ClickListener)this);
    setImmediate(true);
  }
  
  /* Called when the button is clicked while in the "on" state.  On return from
   * the listener, the button is moved to the "off" state;
   */
  public void addOnListener(ClickListener lis)
  {
    onListener = lis;
  }
  
  /* Called when the button is clicked while in the "off" state.  On return from
   * the listener, the button is moved to the "on" state;
   */

  public void addOffListener(ClickListener lis)
  {
    offListener = lis;
  }
  
  public void setInitialState(boolean on)
  {
    this.on = on;
    if(on) {
      setCaption(onText);
      setDescription(onTT);
    }
    else {
     setCaption(offText);
     setDescription(offTT);
    }
  }
  
  public void setToolTips(String onTT, String offTT)
  {
    this.onTT = onTT;
    this.offTT = offTT;
    setInitialState(on);
  }
  
  @Override
  public void buttonClick(ClickEvent event)
  {
    if(on) {
      setCaption(offText);
      setDescription(offTT);
      if(onListener != null)
        onListener.buttonClick(event);
    }
    else {
      setCaption(onText);
      setDescription(onTT);
      if(offListener != null)
        offListener.buttonClick(event);
    }
    on = !on;
  }
}
