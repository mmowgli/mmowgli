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

package edu.nps.moves.mmowgli.cache;

import java.util.*;
import java.util.Map.Entry;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.modules.cards.CardTypeManager;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
import static edu.nps.moves.mmowgli.MmowgliConstants.*;

/**
 * MSuperActiveCacheManager.java
 * Created on Aug 9, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
class MSuperActiveCacheManager
{
  private  TreeMap<Long,Card> cardMap = new TreeMap<Long,Card>();

  public List<Card> getSuperInterestingRoots()
  {
    ArrayList<Card> aLis = new ArrayList<Card>();
    Iterator<Entry<Long,Card>> itr = cardMap.entrySet().iterator();
    
    while(itr.hasNext())
      aLis.add(itr.next().getValue());
    
    return aLis;
  }
  
  @SuppressWarnings("unchecked")
  public void rebuild(Session sess)
  {
    MSysOut.println(MCACHE_LOGS,"Building super-active list");
    cardMap.clear();
    List<Card> lis = (List<Card>)sess.createCriteria(Card.class).list();
    for(Card c : lis) {
     // System.out.println("&&&& "+c.getId());
      if(CardTypeManager.isIdeaCard(c.getCardType()))
        continue; // will never be since we check backwards
      
      newCard(c,sess);
    }
    MSysOut.println(MCACHE_LOGS,"Finished building super-active list");
  }
  
  public void newCard(Card c, Session sess)
  {
    if ((c = qualifies(c,sess)) != null)
      cardMap.put(c.getId(), c);
    return;
  }

  private class TallyPkt {HashSet<Long> authors=new HashSet<Long>(); int numFourCardLevs=0;}

  private Card qualifies(Card c, Session sess)
  {
    TallyPkt pkt = new TallyPkt();
    
    Card tmp = null;
    while((tmp=c.getParentCard())!= null)
      c = tmp;
    
    c = Card.merge(c, sess);
    checkOneRoot(c,pkt,sess);
    
    if (isSupAct(pkt))
      return c;
    
    return null;
  }
  
  private boolean isSupAct(TallyPkt pkt)
  {
    if(pkt.numFourCardLevs >= 2)
      if(pkt.authors.size() >= 2)
        return true;
    return false;
  }
  
  private void checkOneRoot(Card c, TallyPkt pkt, Session sess)
  {
    ArrayList<Card>mergedChildren = new ArrayList<Card>();
    for(Card child : c.getFollowOns()) {
      child = Card.merge(child,sess);
      pkt.authors.add(child.getAuthor().getId());
      mergedChildren.add(child);
    }
    if(c.getFollowOns().size() >= 4)
      pkt.numFourCardLevs++;
    
    // We don't need to check further if we pass
    if(isSupAct(pkt))
      return;
    
    for(Card child : mergedChildren) {
      checkOneRoot(child,pkt,sess);
    }
  }
}
