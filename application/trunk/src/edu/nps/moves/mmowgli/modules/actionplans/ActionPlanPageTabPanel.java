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

import java.util.Iterator;

import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.components.GhostVerticalLayoutWrapper;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanUpdates;
/**
 * IdeaDashboardTabPanel.java
 * Created on Feb 8, 2011
 * Modified on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class ActionPlanPageTabPanel extends HorizontalLayout implements MmowgliComponent,WantsActionPlanUpdates
{
  private static final long serialVersionUID = -8385877114662231713L;
  
  protected Object apId;
  protected boolean isMockup;
  protected boolean isReadOnly;
  
  private VerticalLayout leftVertLay;
  private VerticalLayout rightVertLay;
    
  public ActionPlanPageTabPanel(Object apId, boolean isMockup, boolean isReadOnly)
  {
    this.apId = apId;
    this.isMockup = isMockup;
    this.isReadOnly = isReadOnly;
    
    setSpacing(true);
    setMargin(false);
    
    setWidth("970px");
    setHeight("750px");
    
    VerticalLayout leftWrapper = new VerticalLayout();
    addComponent(leftWrapper);
    leftWrapper.setSpacing(false);
    leftWrapper.setMargin(false);
    leftWrapper.setWidth("261px"); 

    Label sp;
    leftWrapper.addComponent(sp = new Label());
    sp.setHeight("15px");
    
    GhostVerticalLayoutWrapper gWrap = new GhostVerticalLayoutWrapper();
    leftWrapper.addComponent(gWrap);
    leftVertLay = new VerticalLayout();
    gWrap.ghost_setContent(leftVertLay);

    leftWrapper.addComponent(sp = new Label());
    sp.setHeight("1px");
    leftWrapper.setExpandRatio(sp, 1.0f);
    
    VerticalLayout rightWrapper = new VerticalLayout();
    addComponent(rightWrapper);
    rightWrapper.setSpacing(false);
    rightWrapper.setMargin(false);
    rightWrapper.setWidth("100%");
    rightWrapper.setHeight("690px");
    
    rightVertLay = new VerticalLayout();
    rightWrapper.addComponent(rightVertLay);
    rightVertLay.setWidth("690px");
    rightVertLay.setHeight("695px");
  }
  
  public VerticalLayout getLeftLayout()
  {
    return leftVertLay;
  }
  public VerticalLayout getRightLayout()
  {
    return rightVertLay;
  }

  abstract public void setICanEdit(boolean yn);
  
  /* a utility routing used by images and video tabs */
  protected MediaPanel findMed(Component c)
  {
    if(c instanceof MediaPanel)
      return (MediaPanel)c;
    if(! (c instanceof ComponentContainer))
      return null;
    
    Iterator<Component> itr = ((ComponentContainer)c).iterator();
    while(itr.hasNext()) {
      Object o;
      if((o=itr.next()) instanceof MediaPanel)
        return (MediaPanel) o;
      if(o instanceof ComponentContainer) {
        MediaPanel mp = findMed((ComponentContainer)o);
        if(mp != null)
          return mp;
      }
    }
    return null;
  }
  
  /* used by images and video tabs */
  protected boolean mediaUpdatedOobTL(ComponentContainer cont, Object medId)
  {
    Iterator<Component> itr = cont.iterator();
    while (itr.hasNext()) {
      MediaPanel imp = findMed(itr.next());
      if (imp !=null && imp.getMedia().getId() == (Long) medId) {
        imp.mediaUpdatedOobTL();
        return true;
      }
    }
    return false;
  }
  
  protected String nullOrString(Object o)
  {
    if(o == null)
      return null;
    return o.toString();
  }

  protected void setValueIfNonNull(AbstractTextField comp, String s)
  {
    if(s != null)
      comp.setValue(s);
  }
  
  protected void sendStartEditMessage(String msg)
  {
    /* Have seen event flurries... disable until tracked down
    if(app.isAlive()) {
      ApplicationMaster master = app.globs().applicationMaster();
      master.sendLocalMessage(ACTIONPLAN_EDIT_BEGIN, "" + apId + MMESSAGE_DELIM + msg);
    }
    */
  }
  
  protected String getDisplayedName(Media m)
  {
    String url = m.getUrl();
    int i;
    switch(m.getType())
    {
      case IMAGE:  //0       
      case VIDEO:  //1
      case AVATARIMAGE: // 2
        if((i=url.lastIndexOf("/")) != -1)
          url = url.substring(i+1);
        return url;
      case YOUTUBE: // 3
        return "Youtube ID: "+url;
      default:
        return "";
    }
  }
  

 }
