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

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.event.MouseEvents;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.Avatar;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * AvatarPanel.java
 * Created on Mar 21, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AvatarPanel extends Panel implements MmowgliComponent
{
  private static final long serialVersionUID = -4886630541532249910L;
  private String caption;
  private Object[] avIdArr;
  private Integer selectedIdx;
  private HorizontalLayout imgLay;
  
  @HibernateSessionThreadLocalConstructor
  public AvatarPanel(String caption)
  {
  }
  @Override
  public void initGui()
  {
    setCaption(caption);
    //setScrollable(true);

    imgLay = new HorizontalLayout();
    setContent(imgLay);
  //  imgLay.setHeight("105px");
    imgLay.setSpacing(true);
    
    @SuppressWarnings("unchecked")
    HbnContainer<Avatar> contr = (HbnContainer<Avatar>)HSess.getContainer(Avatar.class);
    Collection<?> lis = contr.getItemIds();
    avIdArr = new Object[lis.size()];

    int idx = 0;
    MediaLocator loc = Mmowgli2UI.getGlobals().mediaLocator();
    
    for(Object id : lis) {
      avIdArr[idx++] = id;

      Avatar a = Avatar.getTL(id);
      Image em = new Image(null, loc.locate(a.getMedia()));
      em.setWidth("95px");
      em.setHeight("95px");
      em.addClickListener(new ImageClicked());
      em.addStyleName("m-greyborder5"); //m-orangeborder5
      imgLay.addComponent(em);
    }
    
  }
  
  public Object getSelectedAvatarId()
  {
    if(selectedIdx != null)
      return avIdArr[selectedIdx];
    return null;
  }
  
  public void setSelectedAvatarIdx(int idx)
  {
    setSelectedAvatarId(avIdArr[idx]);
  }
  
  public void setSelectedAvatarId(Object selId)
  {
    for(int i=0;i<avIdArr.length;i++) {
      Object aId = avIdArr[i];
    
      if(aId.equals(selId)) {       
        if(selectedIdx != null) {
          Image oldSel = (Image)imgLay.getComponent(selectedIdx);
          oldSel.removeStyleName("m-orangeborder5");
          oldSel.addStyleName("m-greyborder5");
        }
        Image em = (Image)imgLay.getComponent(i);
        em.removeStyleName("m-greyborder5");
        em.addStyleName("m-orangeborder5");
        selectedIdx = i;        
      }
    }
  }
  
  
  @SuppressWarnings("serial")
  class ImageClicked implements MouseEvents.ClickListener
  {
    @Override
    @MmowgliCodeEntry
    public void click(com.vaadin.event.MouseEvents.ClickEvent event)
    {
      Image emb = (Image)event.getSource();
      for(int x=0;x<avIdArr.length;x++)
        if(imgLay.getComponent(x) == emb) {
          setSelectedAvatarId(avIdArr[x]);
        }
    }    
  }
}
