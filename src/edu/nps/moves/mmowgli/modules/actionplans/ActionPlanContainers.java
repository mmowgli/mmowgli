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
  along with Mmowgli, in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.modules.actionplans;

import static edu.nps.moves.mmowgli.cache.MCacheActionPlanHelper.QuickActionPlan.QUICKAP_ID;

import java.io.Serializable;
import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.cache.MCacheActionPlanHelper.QuickActionPlan;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * ActionPlanContainers.java created on Feb 24, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class ActionPlanContainers
{
  public static class QuickAllPlansInThisMove extends BeanContainer<Long,QuickActionPlan>
  {
    private static final long serialVersionUID = 1L;

    public QuickAllPlansInThisMove(Game g, User me)
    {
      super(QuickActionPlan.class);
      this.setBeanIdProperty(QUICKAP_ID);
      addAll(AppMaster.instance().getMcache().getQuickActionPlanList());
      if(me.isAdministrator() || g.isShowPriorMovesActionPlans())
        ;
      else
        addFilter(new Compare.Equal(QuickActionPlan.QUICKAP_CREATEDINMOVE, g.getCurrentMove().getNumber()));
      
      if(!me.isAdministrator())
        addFilter(new Compare.Equal(QuickActionPlan.QUICKAP_HIDDEN, false));
     }   
  }
  
  @SuppressWarnings({ "serial", "unchecked" })
  public static class HelpWantedContainer<T> extends HbnContainer<T>
  {
    public HelpWantedContainer()
    {
      this(HSess.getSessionFactory());
    }
    public HelpWantedContainer(SessionFactory fact)
    {
      super((Class<T>)ActionPlan.class, fact);
    }
    @Override
    protected Criteria getBaseCriteriaTL()
    {
      Criteria crit = super.getBaseCriteriaTL();  // gets all plans
      crit.add(Restrictions.isNotNull("helpWanted"));
      
      User me = Mmowgli2UI.getGlobals().getUserTL(); 
      ActionPlan.adjustCriteriaToOmitActionPlansTL(crit, me);
      return crit;
    }
  }
  
  @SuppressWarnings({ "serial", "rawtypes", "unchecked" })
  public static class ImFollowingContainer2 extends BeanItemContainer
  {
    public ImFollowingContainer2(Class type, Collection collection) throws IllegalArgumentException
    {
      super(ActionPlan.class, collection);
    }
  }
  @SuppressWarnings({ "serial", "unchecked" })
  public static class MyActionPlanContainer<T> extends HbnContainer<T>
  {
    public MyActionPlanContainer()
    {
      this(HSess.getSessionFactory());
    }
    public MyActionPlanContainer(SessionFactory fact)
    {
      super((Class<T>)ActionPlan.class, fact );
    }
    @Override
    protected Criteria getBaseCriteriaTL()
    {
      Serializable uid = Mmowgli2UI.getGlobals().getUserID();
      Criteria crit = super.getBaseCriteriaTL();           // gets all cards
      crit = crit.createCriteria("authors").             // sub criteria
      add(Restrictions.idEq(uid));// written by me
      
      User me = User.getTL(uid);
      ActionPlan.adjustCriteriaToOmitActionPlansTL(crit, me);
      
      return crit;
    }
  }
  
  @SuppressWarnings({ "serial", "unchecked" })
  public static class NeedsAuthorsContainer<T> extends HbnContainer<T>
  {
    public NeedsAuthorsContainer()
    {
      this(HSess.getSessionFactory());
    }
    public NeedsAuthorsContainer(SessionFactory fact)
    {
      super((Class<T>)ActionPlan.class, fact);
    }
    @Override
    protected Criteria getBaseCriteriaTL()
    {
      Criteria crit = super.getBaseCriteriaTL();  // gets all plans
      crit.add(Restrictions.isEmpty("authors"));// nobody there
      crit.add(Restrictions.isEmpty("invitees"));  // Any invitations have be declined
      
      User me = Mmowgli2UI.getGlobals().getUserTL();
      ActionPlan.adjustCriteriaToOmitActionPlansTL(crit, me);

      return crit;
    }
  }

  @SuppressWarnings({ "serial", "unchecked" })
  class AllPlansInThisMove<T> extends HbnContainer<T>
  {
    public AllPlansInThisMove()
    {
      this(HSess.getSessionFactory());
    }

    public AllPlansInThisMove(SessionFactory fact)
    {
      super((Class<T>) ActionPlan.class, fact);
    }

    @Override
    protected Criteria getBaseCriteriaTL()
    {
      Criteria crit = super.getBaseCriteriaTL();
      User me = Mmowgli2UI.getGlobals().getUserTL();
      ActionPlan.adjustCriteriaToOmitActionPlansTL(crit, me);
      return crit;
    }
  }

}
