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

import java.util.*;
import java.util.Calendar;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * ManageAwardsDialog.java Created on Apr 5, 2012
 * Modified on 13 Mar 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ManageAwardsDialog extends Window
{
  private static final long serialVersionUID = -9025756247856875487L;
  
  private Object uId;
  private GridLayout gridLayout;
  
  @HibernateSessionThreadLocalConstructor
  public ManageAwardsDialog(Object uId)
  {
    this.uId = uId;
    User u = User.getTL(uId);
    
    setCaption("Manage Awards for "+u.getUserName());
    setModal(true);
    setSizeUndefined();
    setWidth("625px");
    setHeight("400px");
    
    VerticalLayout vLay = new VerticalLayout();
    vLay.setMargin(true);
    vLay.setSpacing(true);
    vLay.setSizeFull();
    setContent(vLay);
    
    Panel p = new Panel("Award Assignments -- a check applies the award to player "+u.getUserName());
    p.setWidth("99%");
    p.setHeight("99%");
    vLay.addComponent(p);
    vLay.setExpandRatio(p, 1.0f);
    
    gridLayout = new GridLayout();
    gridLayout.addStyleName("m-headgrid");
    gridLayout.setWidth("100%");
    p.setContent(gridLayout);
    fillPanelTL(u);  //@HibernateUserRead
    
    HorizontalLayout buttPan = new HorizontalLayout();
    buttPan.setWidth("100%");
    buttPan.setSpacing(true);
    NativeButton defineButt = new NativeButton("Define Award Types",new DefineListener());
    NativeButton saveButt = new NativeButton("Save", new SaveListener());
    NativeButton cancelButt = new NativeButton("Cancel", new CancelListener());
    
    buttPan.addComponent(defineButt);
    Label lab;
    buttPan.addComponent(lab = new Label());
    buttPan.setExpandRatio(lab, 1.0f);
    buttPan.addComponent(cancelButt);
    buttPan.addComponent(saveButt);
    vLay.addComponent(buttPan);
  }

  private ArrayList<AwardType> gridList;
  @HibernateUserRead
  private void fillPanelTL(User u)
  {
    @SuppressWarnings("unchecked")
    List<AwardType> typs = (List<AwardType>)HSess.get().createCriteria(AwardType.class).list();
    gridList = new ArrayList<AwardType>(typs.size());
    gridList.addAll(typs);
    gridLayout.removeAllComponents();
    gridLayout.setRows(typs.size());
    gridLayout.setColumns(4);
    gridLayout.setSpacing(true);
    gridLayout.setColumnExpandRatio(2, 0.5f);
    gridLayout.setColumnExpandRatio(3, 0.5f);

    Set<Award> uAwards = u.getAwards();
    MediaLocator mediaLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    for(AwardType at: typs) {
      CheckBox cb;
      boolean checked = hasBeenAwarded(uAwards,at);
      gridLayout.addComponent(cb=new CheckBox());
      cb.setValue(checked);
      gridLayout.setComponentAlignment(cb, Alignment.MIDDLE_CENTER);
      Embedded emb = new Embedded(null,mediaLoc.locate(at.getIcon55x55()));
      emb.addStyleName("m-greyborder3");
      gridLayout.addComponent(emb);
      Label lab;
      gridLayout.addComponent(lab=new Label(at.getName()));
      gridLayout.setComponentAlignment(lab, Alignment.MIDDLE_LEFT);     
      gridLayout.addComponent(lab=new Label(at.getDescription()));
      gridLayout.setComponentAlignment(lab, Alignment.MIDDLE_LEFT);  
    }
  }
  
  private boolean hasBeenAwarded(Set<Award> uAwards, AwardType at)
  {
    for(Award a : uAwards) {
      if(a.getAwardType().getId() == at.getId())
        return true;
    }
    return false;
  }
  
  @SuppressWarnings("serial")
  class CancelListener implements ClickListener
  {  
    @Override
    public void buttonClick(ClickEvent event)
    {
      UI.getCurrent().removeWindow(ManageAwardsDialog.this);
    }
  }
  
  @SuppressWarnings("serial")
  class SaveListener implements ClickListener
  {
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    @HibernateUpdate
    @HibernateUserUpdate
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      User u = User.getTL(uId);
      HashSet<Award> awSet = new HashSet<Award>();
      Set<Award>dbset = u.getAwards();
      for(Award a : dbset) {
        awSet.add(a);
      }
      HashSet<Award> newSet = new HashSet<Award>();
      
      for (int i = 0;i<gridLayout.getRows(); i++) {
        CheckBox cb = (CheckBox)gridLayout.getComponent(0, i);
        addRemoveAwardTL(awSet,newSet,gridList.get(i),u,(Boolean)cb.getValue());
      }
      u.setAwards(newSet);
      User.updateTL(u);
      
      UI.getCurrent().removeWindow(ManageAwardsDialog.this);
      HSess.close();
    }

    private void addRemoveAwardTL(Set<Award> oldSet, Set<Award> newSet, AwardType at, User u, boolean add)
    {
      User me = Mmowgli2UI.getGlobals().getUserTL();

      boolean handled = false;
      
      while (true) {
        boolean inList = false;
        for (Award a : oldSet) {
          if (a.getAwardType().getId() == at.getId()) {
            inList = true;
            if (add) {
              oldSet.remove(a);
              newSet.add(a);
            }
            else { // remove
              oldSet.remove(a);
              Award.deleteTL(a);
            }
            handled = true;
            break; // out of for
          }
        }
        if (!inList)          
          break;
        inList = false;
      }
      
      if(add && !handled) {
        Award aw = new Award();
        aw.setAwardType(at);
        aw.setAwardedBy(me);
        aw.setAwardedTo(u);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        aw.setTimeAwarded(cal);
        aw.setMove(Game.getTL().getCurrentMove());
        aw.setStoryUrl(""); // todo
        Award.saveTL(aw);
        
        newSet.add(aw);       
      }
    }
  }  

  @SuppressWarnings("serial")
  class DefineCloseListener implements CloseListener
  {
    @Override
    public void windowClose(CloseEvent e)
    {
      // refresh here
    }    
  }
  
  @SuppressWarnings("serial")
  class DefineListener implements ClickListener
  {  
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      
      DefineAwardsDialog dad = new DefineAwardsDialog();
      dad.addCloseListener(new DefineCloseListener());
      UI.getCurrent().addWindow(dad);
      dad.center();
      
      HSess.close();
    }
  }
}
