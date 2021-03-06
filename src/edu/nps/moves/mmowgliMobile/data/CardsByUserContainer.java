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

package edu.nps.moves.mmowgliMobile.data;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
/**
 * CardsByTypeContainer.java
 * Created on Feb 11, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardsByUserContainer<T> extends HbnContainer<T>
{
  private static final long serialVersionUID = 958255595195171755L;
  private User user;
  
  @SuppressWarnings("unchecked")
  public CardsByUserContainer(User user)
  {
    super((Class<T>)Card.class,HSess.getSessionFactory());
    this.user=user;
  }
  
  @Override
  protected Criteria getBaseCriteriaTL()
  {
    Criteria crit = super.getBaseCriteriaTL();   // gets all cards
    crit.add(Restrictions.eq("author",user));
    crit.addOrder(Order.desc("creationDate"));   // newest first
    
 //   if(me.isGameMaster() || me.isAdministrator())
 //     ;
 //   else
      crit.add(Restrictions.eq("hidden", false));
    
//    Card.adjustCriteriaToOmitCards(crit, me);

    return crit;

  }  
}
