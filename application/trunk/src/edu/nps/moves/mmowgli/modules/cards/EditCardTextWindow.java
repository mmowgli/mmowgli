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

import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

/**
 * EditCardTextWindow.java
 * Created on May 3, 2010
 * Updated on 12 Mar, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class EditCardTextWindow extends Window
{
  private static final long serialVersionUID = -8217172020066011334L;

  private Button saveButt,cancelButt;

  private TextArea ta;
  private int characterLimit = 140;
  public EditCardTextWindow()
  {
    this(null);
  }
  public EditCardTextWindow(String text)
  {
    this(text,null);
  }
  public EditCardTextWindow(String text, Integer charLim)
  {
    super("Edit card text");

    if(charLim != null)
      characterLimit = charLim;
    
    setModal(true);
    VerticalLayout layout = new VerticalLayout();
    setContent(layout);
    layout.setMargin(true);
    layout.setSpacing(true);
    ta = new TextArea();
    ta.setRows(10);
    ta.setColumns(50);
    if(text != null) {
      ta.setValue(text);
      ta.selectAll();
    }
    else
      ta.setInputPrompt("Type card text here");
    layout.addComponent(ta);

    HorizontalLayout buttHL = new HorizontalLayout();
    buttHL.setSpacing(true);

    WindowCloser closeListener = new WindowCloser(this);
    cancelButt = new Button("Cancel", closeListener);
    buttHL.addComponent(cancelButt);

    saveButt = new Button("Save", closeListener);
    saveButt.addClickListener(closeListener);
    buttHL.addComponent(saveButt);
    
    layout.addComponent(buttHL);
    
    layout.setSizeUndefined(); // does a "pack"
    UI.getCurrent().addWindow(this);
    ta.focus();
  }
  
  public String results = null;
  
  @SuppressWarnings("serial")
  private class WindowCloser implements Button.ClickListener
  {
    Window w;
    WindowCloser(Window w)
    {
      this.w = w;
    }
    public void buttonClick(ClickEvent event)
    {     
      if(event.getButton() == saveButt) {
        results = ta.getValue().toString();
        if(results.length()>characterLimit) {
          new Notification(
              "Entry too long.",
              "The limit is "+characterLimit+".<br/>You entered "+results.length()+" characters.",
              Notification.Type.ERROR_MESSAGE,true).show(Page.getCurrent());
          return;
        }
      }
      else
        results = null;
      UI.getCurrent().removeWindow(w);
    }
  }
}
