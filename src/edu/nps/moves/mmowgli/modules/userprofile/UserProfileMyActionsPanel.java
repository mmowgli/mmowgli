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

import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.ui.Label;

import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanTable;

/**
 * UserProfileMyActionsPanel.java
 * Created on Mar 15, 2011
 * Updated on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserProfileMyActionsPanel extends UserProfileTabPanel
{
  private static final long serialVersionUID = 6213829028886384848L;

  @HibernateSessionThreadLocalConstructor
  public UserProfileMyActionsPanel(Object uid)
  {
    super(uid);
  }

  @Override
  public void initGui()
  {
    super.initGui();
    Game g = Game.getTL();
    if(g.isActionPlansEnabled()) {    
      String name = userIsMe?"you are":userName+" is";
      getLeftLabel().setValue("Here are Action Plans "+name+" currently co-authoring.");
    }
    else {
      getLeftLabel().setValue("This feature is not used in this exercise.");
    }
    
    if(g.isActionPlansEnabled()) {
      Label sp;
      getRightLayout().addComponent(sp = new Label());
      sp.setHeight("20px");
      showMyActionPlans();
    }
  }
  
  private void showMyActionPlans()
  {
    ActionPlanTable tab = new ActionPlanTable(null);
    tab.initFromDataSource(new MyActionsContainer<ActionPlan>());
    
    // put table in place
    getRightLayout().addComponent(tab); 
    getRightLayout().setWidth("669px");
    tab.setWidth("100%");
    tab.setHeight("720px");
    tab.addStyleName("m-greyborder");
  }
  
  @SuppressWarnings({ "serial", "unchecked" })
  class MyActionsContainer<T> extends HbnContainer<T>
  {
    public MyActionsContainer()
    {
      this(HSess.getSessionFactory());
    }    
    public MyActionsContainer(SessionFactory fact)
    {
      super((Class<T>) ActionPlan.class,fact);
    }
    
    @Override
    protected Criteria getBaseCriteriaTL()
    {
      User me = User.getTL(uid);
      
      Criteria crit = super.getBaseCriteriaTL();
      
      Set<ActionPlan> imAuthor = me.getActionPlansAuthored();
      if(imAuthor != null && imAuthor.size()>0) {
        Disjunction dis = Restrictions.disjunction();   // "or"
        for(ActionPlan ap : imAuthor)
          dis.add(Restrictions.idEq(ap.getId()));
        crit.add(dis);
        ActionPlan.adjustCriteriaToOmitActionPlansTL(crit, me);
      }
      else
        crit.add(Restrictions.idEq(-1L)); // will never pass, so we get an empty set
      return crit;
    }
  }
}
