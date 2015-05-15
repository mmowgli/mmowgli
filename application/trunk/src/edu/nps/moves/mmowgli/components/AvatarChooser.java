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

import java.util.Collection;

import com.vaadin.event.MouseEvents;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.Avatar;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * AvatarChooser.java
 * Created on Mar 19, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AvatarChooser extends Window implements MmowgliComponent
{
  private static final long serialVersionUID = 7927514202747533448L;
  
  private Object[] avIdArr;
  private Object selectedId = null;
  private HorizontalLayout imgLay;
  private Object initSelectedID;
  private HorizontalLayout butts;
  private static String defaultCaption = "Choose an Avatar";
  
  public AvatarChooser()
  {
    this(null);
  }
  
  public AvatarChooser(Object selectedId)
  {
    this(selectedId, defaultCaption);
  }
  
  @HibernateSessionThreadLocalConstructor
  public AvatarChooser(Object selectedId, String caption)
  {
    super(caption);

    this.initSelectedID = selectedId;
    
    setWidth("745px");
    setHeight("225px");
    setResizable(true);
    setClosable(false);
  }
  
  @SuppressWarnings("serial")
  @Override
  public void initGui()
  {
    VerticalLayout mainLayout = new VerticalLayout();
    mainLayout.setSizeFull();
    mainLayout.setMargin(true); //test
    mainLayout.setSpacing(true);
    MediaLocator medLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    Label sp;  
    setContent(mainLayout);

    Panel p = new Panel(imgLay=new HorizontalLayout());
    imgLay.setSpacing(true);
    
    p.setWidth("100%");
    mainLayout.addComponent(p);
    
    Collection<?> lis = Avatar.getContainer().getItemIds();
    avIdArr = new Object[lis.size()];

    int idx = 0;
    
    for(Object id : lis) {
      avIdArr[idx++] = id;
      if(initSelectedID == null)
        initSelectedID = id; // sets first one
      Avatar a = Avatar.getTL(id);
      Image em = new Image(null, medLoc.locate(a.getMedia()));
      em.setWidth("95px");
      em.setHeight("95px");
      em.addClickListener(new ImageClicked());

      if(id.equals(initSelectedID)) {
        em.addStyleName("m-orangeborder5");
        lastSel = em;
      }
      else
        em.addStyleName("m-greyborder5");
      imgLay.addComponent(em);
    }
    
    butts = new HorizontalLayout();
    butts.setWidth("100%");
    butts.setSpacing(true);
    mainLayout.addComponent(butts);


    butts.addComponent(sp = new Label());
    sp.setWidth("1px");
    butts.setExpandRatio(sp, 1.0f);
    
    NativeButton cancelButt = new NativeButton();
    medLoc.decorateCancelButton(cancelButt);
    butts.addComponent(cancelButt);
    
    NativeButton selectButt = new NativeButton();
    medLoc.decorateSelectButton(selectButt);
    butts.addComponent(selectButt);
    
    butts.addComponent(sp = new Label(""));
    sp.setWidth("20px");
    
    mainLayout.addComponent(sp = new Label(""));
    sp.setHeight("1px");
    mainLayout.setExpandRatio(sp, 1.0f);;
    
    cancelButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      public void buttonClick(ClickEvent event)
      {
        cancelClick();
      }
    });
    selectButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      public void buttonClick(ClickEvent event)
      {
        selectClick();
      }
    });
  }
  
  private Image lastSel = null;
  
  @SuppressWarnings("serial")
  class ImageClicked implements MouseEvents.ClickListener
  {
    @Override
    @MmowgliCodeEntry
    public void click(com.vaadin.event.MouseEvents.ClickEvent event)
    {
      Image emb = (Image)event.getSource();
      int idx = 0;

      for(int x=0;x<avIdArr.length;x++)
        if(imgLay.getComponent(x) == emb) {
          idx = x;
          break;
        }
      
      if(lastSel != null) {
        lastSel.removeStyleName("m-orangeborder5");
        lastSel.addStyleName("m-greyborder5");
      }
      
      emb.removeStyleName("m-greyborder5");
      emb.addStyleName("m-orangeborder5");
      lastSel = emb;
      selectedId = avIdArr[idx];
    }    
  }
  
  private void selectClick()
  {
    UI.getCurrent().removeWindow(this);
  }
  
  private void cancelClick()
  {
    selectedId = null;
    UI.getCurrent().removeWindow(this);
  }
  
  public Object getSelectedAvatarId()
  {
    return selectedId;
  }
  
  public void showButtonPanel(boolean yn)
  {
    butts.setVisible(yn);
  }
}
