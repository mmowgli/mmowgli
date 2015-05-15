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

package edu.nps.moves.mmowgli.modules.actionplans;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.utility.MmowgliLinkInserter;

/**
 * TextAreaLabelUnion.java
 * Created on Jun 27, 2012
 * Modified on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
class TextAreaLabelUnion extends AbsoluteLayout implements LayoutClickListener
{
  private static final long serialVersionUID = 1L;
  
  TextArea ta;
  Label lab;
  Panel labPan;
  FocusListener fLis;
  int TOPZ = 1, BOTTOMZ = 0;
  boolean readOnly=true;
  VerticalLayout vl;
  
  TextAreaLabelUnion(TextArea taParam, Label labParam, FocusListener fLis)
  {
    this(taParam,labParam,fLis,null);
  }
  
  TextAreaLabelUnion(TextArea taParam, Label labParam, FocusListener fLis, String componentStyle)
  {
    this.fLis = fLis;

    if(taParam != null)
      ta = taParam;
    else
      ta = new TextArea();
    if(labParam != null)
      lab = labParam;
    else {
      lab = new HtmlLabel();
    }
    vl = new VerticalLayout();
    
    if(componentStyle == null)
      componentStyle = "m-actionplan-theplan-fields";
    addStyleToComponents(componentStyle); 
  }
  
  public void addStyleToComponents(String s)
  {
    vl.addStyleName(s); // need this for grey background
    ta.addStyleName(s);
    lab.addStyleName(s);
  }
  
  public void initGui()
  {   
    addComponent(ta,"top:0px;left:0px"); 
    ta.setWidth(getWidth(),getWidthUnits());
    ta.setHeight(getHeight(),getHeightUnits());

    labPan = new Panel();
    labPan.setStyleName(Reindeer.PANEL_LIGHT);
    labPan.setContent(vl);    
    vl.setMargin(false);
    lab.setWidth(getWidth(),getWidthUnits());
    lab.setHeight(getHeight(),getHeightUnits());
    vl.addComponent(lab);    
    vl.addLayoutClickListener(this);
    addComponent(labPan,"top:0px;left:0px");
    labPan.setWidth(getWidth(),getWidthUnits());
    labPan.setHeight(getHeight(),getHeightUnits());
  }
  
  @Override
  public void layoutClick(LayoutClickEvent event)
  {
    if(isRo())
      return;
    
    textAreaTop();
    if(fLis!= null)
      fLis.focus(new FocusEvent(ta));  // fake out the listener //todo properly
    
    ta.focus();
  }
  
  public void setLabelValueTL(String txt)
  {
    String escapedTxt = insertBRs(txt);
    lab.setValue(MmowgliLinkInserter.insertLinksTL(escapedTxt,null));     
  }
  
  public void setLabelValueOobTL(String txt)
  {
    String escapedTxt = insertBRs(txt);
    lab.setValue(MmowgliLinkInserter.insertLinksOob(escapedTxt, null, HSess.get()));
  }
  
  public void setValueTL(String txt)
  {
    ta.setValue(txt);
    setLabelValueTL(txt);
  }
  
  public void setValueOobTL(String txt)
  {
    ta.setValue(txt);
    setLabelValueOobTL(txt);
  }
  public String getValue()
  {
    return ta.getValue().toString();
  }
  
  public void textAreaTop()
  {
    setZZ(ta);
  }
  
  public void labelTop()
  {
    setZZ(lab);
  }
  
  private void setZZ(Component newtop)
  {
    if(newtop == ta) {
      getPosition(ta).setZIndex(TOPZ);
      getPosition(labPan).setZIndex(BOTTOMZ);
    }
    else {
      getPosition(ta).setZIndex(BOTTOMZ);
      getPosition(labPan).setZIndex(TOPZ);       
    }
  }
  
  public boolean isRo()
  {
    return readOnly;
  }
  
  public void setRo(boolean wh)
  {
    readOnly = wh;
    ta.setReadOnly(wh);
  }
  
  private String RNESC = "<br t='rn'/>";
  private String NESC  = "<br t='n'/>";
  private String RESC  = "<br t='r'/>";

  private String insertBRs(String s)
  {
    String ret = s.replace("\r\n", RNESC);
    ret = ret.replace("\n",NESC);
    return ret.replace("\r",RESC);
  }
}