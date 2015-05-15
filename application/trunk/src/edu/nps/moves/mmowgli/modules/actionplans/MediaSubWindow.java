/*
  Copyright (C) 2010-2015 Modeling Virtual Environments and Simulation
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

package edu.nps.moves.mmowgli.modules.actionplans;

import org.vaadin.alump.scaleimage.ScaleImage;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.server.Resource;
import com.vaadin.ui.*;

/**
 * MediaSubWindow.java
 * Created on Apr 24, 2015
 * 
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MediaSubWindow extends Window 
{
  private static final long serialVersionUID = 7902776872895421109L;
  private ScaleImage image;
  
  public MediaSubWindow(Resource res)
  {
    setCaption("Action Plan Image");
    setModal(true);
    setWidth("640px");
    setHeight("480px");
    
    TabSheet tabs = new TabSheet();
    tabs.setSizeFull();
    setContent(tabs);
      
    Panel pan = new Panel();
    tabs.addTab(pan,"Fit Window");
    pan.setSizeFull();
      
    VerticalLayout layout = new VerticalLayout();
    pan.setContent(layout);
    layout.setSizeFull();
    layout.addStyleName("m-background-lightgrey");
    layout.setMargin(false);
      
    image = new ScaleImage();
    image.setSizeFull();
    image.setSource(res);
    
    layout.addComponent(image);
    layout.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
    layout.setExpandRatio(image, 1.0f);
    
    
    tabs.addTab(buildNestedImage(res),"Actual Size");
  }

  private Component buildNestedImage(Resource res)
  {
    VerticalLayout content = new MVerticalLayout().withMargin(false).withFullHeight().withFullWidth();
    content.addStyleName("m-background-lightgrey");

    Label sp;
    content.addComponent(sp = new Label());
    content.setExpandRatio(sp, 0.5f);
    
    Component comp = buildNestedHL(res);
    content.addComponent(comp);
    content.setComponentAlignment(comp, Alignment.MIDDLE_CENTER);
    content.addComponent(sp = new Label());
    content.setExpandRatio(sp, 0.5f); 
    return content;
  }
  
  private Component buildNestedHL(Resource res)
  {
    HorizontalLayout hl = new MHorizontalLayout().withMargin(false);
    Label sp;
    hl.addComponent(sp = new Label());
    hl.setExpandRatio(sp, 0.5f);
    
    Component comp = buildCenter(res);
    hl.addComponent(comp);

    hl.addComponent(sp = new Label());
    hl.setExpandRatio(sp, 0.5f);
    return hl;   
  }
  
  private Component buildCenter(Resource res)
  {
    Image img = new Image();
    img.setSource(res);
    return img;
  }  
 }
