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

package edu.nps.moves.mmowgli.modules.cards;

import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardMarking;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * CardMarkingManager.java
 * Created on Jan 25, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@HibernateSessionThreadLocalConstructor
public class CardMarkingManager
{
  private static CardMarking superinteresting, scenariofail, commonknowledge, hidden, nochildren;
  
  public static CardMarking getSuperInterestingMarking()
  {
    if(superinteresting == null)
      superinteresting=getMarking(CardMarking.SUPER_INTERESTING_LABEL);
    return superinteresting;
  }
  public static CardMarking getNoChildrenMarking()
  {
    if(nochildren == null)
    	nochildren = getMarking(CardMarking.NOCHILDREN_LABEL);
    return nochildren;
  }
  public static CardMarking getScenarioFailMarking()
  {
    if(scenariofail == null)
      scenariofail = getMarking(CardMarking.SCENARIO_FAIL_LABEL);
    return scenariofail;
  }
  public static CardMarking getCommonKnowledgeMarking()
  {
    if(commonknowledge == null)
      commonknowledge = getMarking(CardMarking.COMMON_KNOWLEDGE_LABEL);
    return commonknowledge;
  }
  public static CardMarking getHiddenMarking()
  {
    if(hidden == null)
      hidden = getMarking(CardMarking.HIDDEN_LABEL);
    return hidden;
  }
    
  @SuppressWarnings("unchecked")
  private static CardMarking getMarking(String label)
  {
    List<CardMarking> types = (List<CardMarking>)
                                  HSess.get().createCriteria(CardMarking.class).
                                  add(Restrictions.eq("label", label)).
                                  list();
    if(types != null && types.size()>0)
      return types.get(0);
    return null;
  }

  public static boolean isHidden(Card c)
  {
    return _commonIs(c,getHiddenMarking());
  }
  
  public static boolean isSuperInteresting(Card c)
  {
    return _commonIs(c,getSuperInterestingMarking());
  }
  
  public static boolean isNoChildren(Card c)
  {
    return _commonIs(c, getNoChildrenMarking());
  }
  
  public static boolean isScenarioFail(Card c)
  {
    return _commonIs(c, getScenarioFailMarking());
  }
  
  public static boolean isCommonKnowledge(Card c)
  {
    return _commonIs(c, getCommonKnowledgeMarking());
  }
  
  private static boolean _commonIs(Card c, CardMarking cm)
  {
    Set<CardMarking> marks = c.getMarking();  // allows more than one, but we're only using one
    for(CardMarking mark : marks)
      if(mark.getId() == cm.getId())
        return true;
    return false;       
    
  }
  public static boolean isHiddenMarking(CardMarking cm)
  {
    return (getHiddenMarking().getId() == cm.getId());
  }
  
  public static boolean isSuperInterestingMarking(CardMarking cm)
  {
    return (getSuperInterestingMarking().getId() == cm.getId());
  }
}
