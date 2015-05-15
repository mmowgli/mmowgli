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

package edu.nps.moves.mmowgli.modules.cards;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.hibernate.HSessionAccessor;
import edu.nps.moves.mmowgli.db.*;

/**
 * CardDbHelper.java created on Feb 10, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

//todo try extract all the Session-related stuff and put into HSessionAccessor
public class CardDbHelper
{
  @SuppressWarnings("unchecked")
  public static List<Card> getCardsByCardClass(Session sess, CardType.CardClass cls)
  {
    Criteria crit = sess.createCriteria(Card.class).
        add(Restrictions.eq("factCard", false)).
        addOrder(Order.desc("creationDate"));
    crit = crit.createCriteria("cardType")
        .add(Restrictions.eq("cardClass",cls));
    return (List<Card>)crit.list();
  }
  
  public static List<Card> getAllCards(Session sess)
  {
    return HSessionAccessor.getMultiple(Card.class, sess, (Criterion[])null);
  }
  
  @SuppressWarnings("unchecked")
  public static List<Card> getCardsCurrentMoveOnly(Session sess, CardType typ)
  {
    Move mov = Game.get(sess).getCurrentMove();
    List<Card> cards = (List<Card>) sess.createCriteria(Card.class).
    add(Restrictions.eq("cardType", typ)).
    add(Restrictions.eq("factCard", false)).
    createAlias("createdInMove","MOVE").
    add(Restrictions.eq("MOVE.number", mov.getNumber())).
    addOrder(Order.desc("creationDate")).list();
    return cards;
  }

}
