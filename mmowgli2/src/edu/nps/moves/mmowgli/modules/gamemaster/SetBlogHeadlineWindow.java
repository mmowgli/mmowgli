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

package edu.nps.moves.mmowgli.modules.gamemaster;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.hbnutil.HbnContainer.EntityItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.db.MessageUrl;

/**
 * SetBlogHeadlineWindow.java
 * Created on Apr 11, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class SetBlogHeadlineWindow extends Window implements ItemClickListener
{
  private static final long serialVersionUID = -6407565039131287931L;
  private TextField textTF, toolTipTF, urlTF;
  private Label infoLab,  textLab, toolTipLab, urlLab;
  private Table table;
  private Button cancelButt;
  private Button okButt;
  private CheckBox nullCheckBox;
  
  public SetBlogHeadlineWindow()
  {
    super("Edit Blog Headline");
    cancelButt = new Button("Cancel");
    okButt = new Button("Update");
    nullCheckBox = new CheckBox("Do not show blog headline");
    nullCheckBox.setImmediate(true);
    setModal(true);
  }
  
  public void setOkListener(ClickListener lis)
  {
    okButt.addClickListener(lis); 
  }
  
  public void setCancelListener(ClickListener lis)
  {
    cancelButt.addClickListener(lis);
  }
  
  @Override
  public void attach()
  {
    Panel p = new Panel();
    setContent(p);
    p.setSizeFull();
    
    VerticalLayout layout = new VerticalLayout();
    layout.addStyleName("m-blogheadline");
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setSizeFull();
    p.setContent(layout);
    
    layout.addComponent(infoLab=new Label("Game masters can communicate with players throughout the game.  Add a new headling, tooltip and link here."));
    
    layout.addComponent(textLab=new Label("Enter headline:"));
    textTF = new TextField();
    textTF.setInputPrompt("Enter new headline or choose from previous ones below");
    textTF.setWidth("100%");
    textTF.addStyleName("m-blogtextfield");
    layout.addComponent(textTF);
    
    layout.addComponent(toolTipLab=new Label("Enter headline tooltip:"));
    toolTipTF = new TextField();
    toolTipTF.setWidth("100%");
    layout.addComponent(toolTipTF);
    
    layout.addComponent(urlLab=new Label("Enter blog entry url:"));
    urlTF = new TextField();
    urlTF.setWidth("100%");
    layout.addComponent(urlTF);
    
    table = new Table("Previous headlines");
    table.setSizeFull();
    table.setImmediate(true);
    table.setColumnExpandRatio("date",1);
    table.setColumnExpandRatio("text",1);
    table.setColumnExpandRatio("tooltip",1);
    table.setColumnExpandRatio("url", 1);
    table.setSelectable(true);
    table.setMultiSelect(true); // return whole pojo
    table.addItemClickListener(this);
    table.setContainerDataSource(MessageUrl.getContainer());
    layout.addComponent(table);
    
    layout.addComponent(nullCheckBox);
    nullCheckBox.addValueChangeListener(new CBListener());
    HorizontalLayout buttHl = new HorizontalLayout();
    buttHl.setSpacing(true);
    buttHl.addComponent(cancelButt);
    buttHl.addComponent(okButt);
    layout.addComponent(buttHl);
    layout.setComponentAlignment(buttHl, Alignment.TOP_RIGHT);
    layout.setExpandRatio(table, 1.0f); // gets all
    setWidth("675px");
    setHeight("455px");
  }
  
  @SuppressWarnings("rawtypes")
  public void itemClick(ItemClickEvent event)
  {
    EntityItem item = (EntityItem) event.getItem();
    MessageUrl mu = (MessageUrl) ((EntityItem) item).getPojo(); 
    textTF.setValue(mu.getText());
    toolTipTF.setValue(mu.getTooltip());
    urlTF.setValue(mu.getUrl());
  }

  public boolean getNullHeadline()
  {
    return nullCheckBox.getValue();
  }
  
  public String getTextEntry()
  {
    Object obj = textTF.getValue();
    return obj==null?"":obj.toString();
  }
  public String getUrlEntry()
  {
    Object obj = urlTF.getValue();
    return obj==null?"":obj.toString();
  }
  public String getToolTipEntry()
  {
    Object obj = toolTipTF.getValue();
    return obj==null?"":obj.toString();
    
  }
  private void setEnabledFromCheckBox()
  {
    boolean wh = nullCheckBox.getValue();
    textTF.setEnabled(!wh);
    toolTipTF.setEnabled(!wh);
    urlTF.setEnabled(!wh);
//test    table.setEnabled(!wh);
    infoLab.setEnabled(!wh);
    textLab.setEnabled(!wh);
    toolTipLab.setEnabled(!wh);
    urlLab.setEnabled(!wh);
  }
  
  @SuppressWarnings("serial")
  class CBListener implements ValueChangeListener
  {
    @Override
    public void valueChange(final ValueChangeEvent event)
    {
       setEnabledFromCheckBox();
    }
  }
}
