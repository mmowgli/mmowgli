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

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.data.hbnutil.HbnContainer.EntityItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

import edu.nps.moves.mmowgli.components.UserTable;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
;

/**
 * UserProfileMyBuddiesPanel.java
 * Created on Mar 15, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserProfileMyBuddiesPanel extends UserProfileTabPanel implements ItemClickListener
{
  private static final long serialVersionUID = 6712398487478286813L;

  @HibernateSessionThreadLocalConstructor
  public UserProfileMyBuddiesPanel(Object uid)
  {
    super(uid);
  }

  @Override
  public void initGui()
  {
    super.initGui();
    String name = userIsMe?"you are":userName+" is";
    
    getLeftLabel().setValue(
       "Here are other players "+name+" currently following.  To follow a player, visit their profile and click 'Follow this player'.");
    
    Label sp;
    getRightLayout().addComponent(sp = new Label());
    sp.setHeight("20px");
       
    showMyBuddiesTL();
  }
 
  private void showMyBuddiesTL()
  {
    UserTable tab = UserTable.makeBuddyTableTL();
    tab.initFromDataSource(new MyBuddiesContainer<Object>());
    tab.addItemClickListener((ItemClickListener)this);
    
    // put table in place
    getRightLayout().setWidth("669px");
    getRightLayout().addComponent(tab);
    tab.setWidth("100%");
    tab.setHeight("720px");
    tab.addStyleName("m-greyborder");
    getRightLayout().setExpandRatio(tab, 1.0f);
   }

  @Override
  public void itemClick(ItemClickEvent event)
  {
    // This is handled by the UserTable
    // if(event.isDoubleClick()) {
    // EntityItem it = (EntityItem)event.getItem();
    // User u = (User)it.getPojo();
    // app.globs().controller().miscEvent(new
    // ApplicationEvent(SHOWUSERPROFILECLICK, this, u.getId()));
    // }
  }

  class MyColumnCustomizer implements Table.ColumnGenerator
  {
    private static final long serialVersionUID = 1938821794468835620L;

    @Override
    public Component generateCell(Table table, Object itemId, Object columnId)
    {
      @SuppressWarnings("rawtypes")
      EntityItem ei = (EntityItem)table.getItem(itemId);
      User u = (User)ei.getPojo();
      
      if("genemail".equals(columnId)) {
        List<String> sLis = VHibPii.getUserPiiEmails(u.getId());
        if(sLis != null && sLis.size()<=0)
          sLis = null;
        return new Label(sLis==null?"":sLis.get(0));
      }     
      return new Label("Program error in UserProfileMyIdeasPanel.java");
    }   
  }
    
  @SuppressWarnings({ "serial", "unchecked" })
  class MyBuddiesContainer<T> extends HbnContainer<T>
  {
    public MyBuddiesContainer()
    {
      this(HSess.getSessionFactory());
    }
    public MyBuddiesContainer(SessionFactory fact)
    {
      super((Class<T>) User.class,fact);
    }
   
    @Override
    protected Criteria getBaseCriteriaTL()
    {
      User me = User.getTL(uid);
      
      Criteria crit = super.getBaseCriteriaTL();
      crit.add(Restrictions.not(Restrictions.idEq(me.getId())));  
      
      Set<User> imFollowing = me.getImFollowing();
      if(imFollowing != null && imFollowing.size()>0) {
        Disjunction dis = Restrictions.disjunction();
        for(User u : imFollowing)
          dis.add(Restrictions.idEq(u.getId()));
        crit.add(dis);
      }
      else
        crit.add(Restrictions.isNull("userName")); // will never be empty, so we get an empty set
      return crit;
    }
  }  
}
