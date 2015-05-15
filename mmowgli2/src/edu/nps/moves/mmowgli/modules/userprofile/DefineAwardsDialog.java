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

package edu.nps.moves.mmowgli.modules.userprofile;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.AwardType;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.modules.userprofile.EditAwardTypeDialog.EditAwardResultListener;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * HistoryDialog.java Created on Apr 5, 2012
 * Updated on Mar 13 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class DefineAwardsDialog extends Window
{
  private static final long serialVersionUID = 5301099341257441994L;
  
  private GridLayout gridLayout;
  
  @HibernateSessionThreadLocalConstructor
  public DefineAwardsDialog()
  {
    setCaption("Define Player Award Types");
    setModal(true);
    setSizeUndefined();
    setWidth("700px");
    setHeight("400px");
    
    VerticalLayout vLay = new VerticalLayout();
    vLay.setMargin(true);
    vLay.setSpacing(true);
    vLay.setSizeFull();
    setContent(vLay);
    
    vLay.addComponent(new HtmlLabel("<b>This dialog is not yet functional</b>"));
    
    Panel p = new Panel();
    p.setWidth("99%");
    p.setHeight("100%");
    vLay.addComponent(p);
    vLay.setExpandRatio(p, 1.0f);
    
    gridLayout = new GridLayout();
    gridLayout.addStyleName("m-headgrid");
    gridLayout.setWidth("100%");
    p.setContent(gridLayout);
    fillPanelTL();
    
    HorizontalLayout buttPan = new HorizontalLayout();
    buttPan.setWidth("100%");
    buttPan.setSpacing(true);
    NativeButton addButt = new NativeButton("Add new type", new AddListener());
    NativeButton delButt = new NativeButton("Delete type", new DelListener());
    NativeButton saveButt = new NativeButton("Save", new SaveListener());
    NativeButton cancelButt = new NativeButton("Cancel", new CancelListener());
    buttPan.addComponent(addButt);
    buttPan.addComponent(delButt);
    
    Label lab;
    buttPan.addComponent(lab = new Label());
    buttPan.setExpandRatio(lab, 1.0f);
    buttPan.addComponent(cancelButt);
    buttPan.addComponent(saveButt);
    vLay.addComponent(buttPan);  
    
    //temp
    saveButt.setEnabled(false);
    delButt.setEnabled(false);
  }  
  
  private ArrayList<AwardType> gridList;
  private void fillPanelTL()
  {
    @SuppressWarnings("unchecked")
    List<AwardType> typs = (List<AwardType>)HSess.get().createCriteria(AwardType.class).list();
    gridList = new ArrayList<AwardType>(typs.size());
    gridList.addAll(typs);
    gridLayout.removeAllComponents();
    gridLayout.setRows(typs.size()+1);
    gridLayout.setColumns(3);
    gridLayout.setSpacing(true);
    gridLayout.setColumnExpandRatio(1, 0.5f);
    gridLayout.setColumnExpandRatio(2, 0.5f);

    gridLayout.addComponent(new HtmlLabel("<b>Icon</b>"));
    gridLayout.addComponent(new HtmlLabel("<b>Name</b>"));
    gridLayout.addComponent(new HtmlLabel("<b>Description</b>"));

    MediaLocator mediaLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    for(AwardType at: typs) {
      Embedded emb = new Embedded(null,mediaLoc.locate(at.getIcon55x55()));
      emb.addStyleName("m-greyborder3");
      gridLayout.addComponent(emb);
      TextArea tf;
      gridLayout.addComponent(tf=makeTa(at.getName()));
      gridLayout.setComponentAlignment(tf, Alignment.MIDDLE_LEFT);     
      gridLayout.addComponent(tf=makeTa(at.getDescription()));
      gridLayout.setComponentAlignment(tf, Alignment.MIDDLE_LEFT);
    }
  }
  
  private TextArea makeTa(String val)
  {
    TextArea tf = new TextArea();
    tf.setRows(2);
    tf.setValue(val);
    tf.setWidth("100%");
    return tf;
  }
  
  @SuppressWarnings("serial")
  class CancelListener implements ClickListener
  {  
    @Override
    public void buttonClick(ClickEvent event)
    {
      UI.getCurrent().removeWindow(DefineAwardsDialog.this);        
    }
  }
  
  @SuppressWarnings("serial")
  class AddListener implements ClickListener
  {  
    @Override
    public void buttonClick(ClickEvent event)
    {
      EditAwardTypeDialog.show(null, new EditAwardResultListener()
      {
        @Override
        public void doneTL(AwardType at)
        {
          if(at != null) {
            fillPanelTL();
          }
        }        
      });
    }
  }
  
  @SuppressWarnings("serial")
  class DelListener implements ClickListener
  {  
    @Override
    public void buttonClick(ClickEvent event)
    {
    }
  }
  
  @SuppressWarnings("serial")
  class SaveListener implements ClickListener
  {  
    @Override
    public void buttonClick(ClickEvent event)
    {
 /*     User u = DBGet.getUserFresh(uId);
      HashSet<Award> awSet = new HashSet<Award>();
      Set<Award>dbset = u.getAwards();
      for(Award a : dbset) {
        awSet.add(a);
      }
      HashSet<Award> newSet = new HashSet<Award>();
      
      for (int i = 0;i<gridLayout.getRows(); i++) {
        CheckBox cb = (CheckBox)gridLayout.getComponent(0, i);
        addRemoveAward(awSet,newSet,gridList.get(i),u,(Boolean)cb.getValue());
      }
      u.setAwards(newSet);
      Sess.sessUpdate(u);
 */     
      UI.getCurrent().removeWindow(DefineAwardsDialog.this);        
    }
  }
}
