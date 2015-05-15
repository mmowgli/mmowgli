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

import com.vaadin.ui.*;

/**
 * WebContentDisplayer.java
 * Created on Jan 8, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class WebContentDisplayer extends Window
{
  private static final long serialVersionUID = 1L;
  //private ApplicationEntryPoint app;
  public WebContentDisplayer(String text)
  {
   // this.app = app;
    initGui(text);
  }
  
  public void show(Component parent, String width, String height, String title)
  {
    this.setCaption(title);
    this.setHeight(height);
    this.setWidth(width);
    UI.getCurrent().addWindow(this);
    this.center();
  }
  
  private void initGui(String text)
  {
    VerticalLayout vl;
    setContent(vl=new VerticalLayout());
    vl.setHeight("100%");
    StringBuffer sb = new StringBuffer();
    boolean needHTML = false;
    if(!text.toLowerCase().startsWith("<html>")) {
      sb.append("<html><body>");
      needHTML=true;
    }
    sb.append(text);  
    if(needHTML)
      sb.append("</body></html>");
    
    Label lab = new HtmlLabel(sb.toString());
    lab.setWidth("100%");
    lab.setHeight("100%");
    vl.addComponent(lab);
//    StreamResource sr = new StreamResource(new MyStringResource(sb.toString()),"dummy",app);
//    sr.setMIMEType("text/html");
//    Embedded emb = new Embedded(null,sr);
//    emb.setType(Embedded.TYPE_BROWSER);
//    emb.setWidth("100%");
//    emb.setHeight("100%");
//    vl.addComponent(emb);
    
  }
//  class MyStringResource implements StreamResource.StreamSource
//  {
//    private static final long serialVersionUID = 1L;
//    String s;
//    public MyStringResource(String s)
//    {
//      this.s = s;
//    }
//
//    @Override
//    public InputStream getStream()
//    {
//      return new StringBufferInputStream(s);
//    }    
//  }
}
