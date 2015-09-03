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

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * CardTypeManager.java
 * Created on Jan 25, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardTypeManager
{
  private static CardType resource, risk, expand, counter, adapt, explore;
  
  public static boolean isIdeaCard(CardType ct)
  {
    return ct.isIdeaCard();
  }
  
  public static CardType getPositiveIdeaCardTypeTL()
  {
    return getPositiveIdeaCardType(HSess.get());
  }
  
  public static CardType getPositiveIdeaCardType(Session sess)
  {
    if(resource == null)
      resource = CardType.getPositiveIdeaCardType(sess);
    return resource;
  }
  
  public static void updatePositiveIdeaCardType(CardType ct)
  {
    resource = ct;
  }
     
  public static CardType getNegativeIdeaCardTypeTL()
  {
    return getNegativeIdeaCardType(HSess.get());
  }
  
  public static CardType getNegativeIdeaCardType(Session sess)
  {
    if(risk == null)
      risk=CardType.getNegativeIdeaCardType(sess); //getType("Worst Strategy");
    return risk;
  }
  
  public static void updateNegativeIdeaCardType(CardType ct)
  {
    risk = ct;
  }
 
  @Deprecated
  public static boolean isDefendType(CardType ct)
  {
    return ct.isNegativeIdeaCard();
  }
  
  @Deprecated 
  public static CardType getExpandType()
  {
    if(expand == null)
      expand=getDescendantOrdinal(1);
    return expand;
  }
  @Deprecated 
  public static CardType getExpandTypeTL()
  {
    if(expand == null)
      expand=getDescendantOrdinalTL(1);
    return expand;
  }
  @Deprecated
  public static boolean isExpandType(CardType ct)
  {
    return ct.getId() == getExpandType().getId();
  }
    
  @Deprecated  
  public static CardType getCounterType()
  {
    if(counter == null)
      counter=getDescendantOrdinal(2); //getType("Counter");
    return counter;
  }
  @Deprecated
  public static CardType getCounterTypeTL()
  {
    if(counter == null)
      counter=getDescendantOrdinalTL(2); //getType("Counter");
    return counter;
  }   
  @Deprecated 
  public static boolean isCounterType(CardType ct)
  {
    return ct.getId() == getCounterType().getId();
  }
  
  @Deprecated  
  public static CardType getAdaptType()
  {
    if(adapt == null)
      adapt=getDescendantOrdinal(3);
    return adapt;
  }
  @Deprecated  
  public static CardType getAdaptTypeTL()
  {
    if(adapt == null)
      adapt=getDescendantOrdinalTL(3);
    return adapt;
  }
  @Deprecated
  public static boolean isAdaptType(CardType ct)
  {
    return ct.getId() == getAdaptType().getId();
  }  
  
  
  
  @Deprecated  
  public static CardType getExploreType()
  {
    if(explore == null)
      explore=getDescendantOrdinal(4);
    return explore;
  }
  @Deprecated  
  public static CardType getExploreTypeTL()
  {
    if(explore == null)
      explore=getDescendantOrdinalTL(4);
    return explore;
  }
  @Deprecated
  public static boolean isExploreType(CardType ct)
  {
    return ct.getId() == getExploreType().getId();
  }
  
  
  
  public static boolean isDescendantType(CardType ct, int i)
  {
    return ct.getDescendantOrdinal() == i;
  }
  
  public static void updateDescendantType(CardType ct, int i)
  {
    switch(i) {
    case 1:
      expand = ct;
      break;
    case 2:
      counter = ct;
      break;
    case 3:
      adapt = ct;
      break;
    case 4:
      explore = ct;
      break;
    default:
      System.err.println("Bogus index in CardTypeManager.updateDescendantType("+i+")");
    }
  }
  @SuppressWarnings("unchecked")
  public static CardType getDescendantOrdinal(int i)
  {
    Session sess = HSess.getSessionFactory().openSession();      // no leaked sessions
    List<CardType> types = (List<CardType>)
                                  sess.createCriteria(CardType.class).
                                  add(Restrictions.eq("descendantOrdinal",i)).
                                  list();
    assert types.size()==1 : "CardType table error, descendantOrdinal: "+i;
    
    sess.close();
    return types.get(0);
  }
  @SuppressWarnings("unchecked")
  public static CardType getDescendantOrdinalTL(int i)
  {
    List<CardType> types = (List<CardType>)
                                  HSess.get().createCriteria(CardType.class).
                                  add(Restrictions.eq("descendantOrdinal",i)).
                                  list();
    assert types.size()==1 : "CardType table error, descendantOrdinal: "+i;
    return types.get(0);
  }

  @SuppressWarnings("unchecked")
  public static List<CardType> getDefinedPositiveIdeaCardsTL()
  {
    return (List<CardType>) HSess.get().createCriteria(CardType.class)
                                .add(Restrictions.eq("cardClass", CardType.CardClass.POSITIVEIDEA))
                                .list();
  }
  
  @SuppressWarnings("unchecked")
  public static List<CardType> getDefinedNegativeIdeaCardsTL()
  {
    return (List<CardType>) HSess.get().createCriteria(CardType.class)
                                .add(Restrictions.eq("cardClass", CardType.CardClass.NEGATIVEIDEA))
                                .list();
  }

  public static String getBackgroundColorStyle(CardType ct)
  {
    return CardStyler.getCardBaseColor(ct);// new way
  }
  
  public static String getColorStyle(CardType ct)
  {
    String sty = ct.getCssColorStyle();
    if(sty == null)
      sty = "m-lightgray";
    return sty;
  }
  
  public static String getColorStyle_light(CardType ct)
  {
    String sty = ct.getCssLightColorStyle();
    if(sty == null)
      sty = "m-lightergray";
    return sty;
  }
   
  public static String getCardSubmitDebugId(CardType ct)
  {
    long cid = ct.getId();
    if(cid == getPositiveIdeaCardTypeTL().getId())
      return GOOD_IDEA_CARD_SUBMIT;
    else if(cid == getNegativeIdeaCardTypeTL().getId())
      return BAD_IDEA_CARD_SUBMIT;
    else if (cid == getDescendantOrdinal(1).getId())
      return EXPAND_CARD_SUBMIT;
    else if (cid == getDescendantOrdinal(2).getId())
      return COUNTER_CARD_SUBMIT;
    else if(cid == getDescendantOrdinal(3).getId())
      return ADAPT_CARD_SUBMIT;
    else if(cid == getDescendantOrdinal(4).getId())
      return EXPLORE_CARD_SUBMIT;
    else {
      System.err.println("Bogus card type passed to CardTypeManager.getCardSubmitDebugId(), id = "+cid);
      return GOOD_IDEA_CARD_SUBMIT;
    }
  }

  public static String getCardContentDebugId(CardType ct)
  {
    long cid = ct.getId();
    if(cid == getPositiveIdeaCardTypeTL().getId())
      return GOOD_IDEA_CARD_TEXTBOX;
    else if(cid == getNegativeIdeaCardTypeTL().getId())
      return BAD_IDEA_CARD_TEXTBOX;
    else if (cid == getDescendantOrdinal(1).getId())
      return EXPAND_CARD_TEXTBOX;
    else if (cid == getDescendantOrdinal(2).getId())
      return COUNTER_CARD_TEXTBOX;
    else if(cid == getDescendantOrdinal(3).getId())
      return ADAPT_CARD_TEXTBOX;
    else if(cid == getDescendantOrdinal(4).getId()) //getExploreType().getId())
      return EXPLORE_CARD_TEXTBOX;
    else {
      System.err.println("Bogus card type passed to CardTypeManager.getCardContentDebugId(), id = "+cid);
      return GOOD_IDEA_CARD_TEXTBOX;
    }
  }

  public static String getCardCreateClickDebugId(CardType ct)
  {
    long cid = ct.getId();
    if(cid == getPositiveIdeaCardTypeTL().getId())
      return GOOD_IDEA_CARD_OPEN_TEXT;
    else if(cid == getNegativeIdeaCardTypeTL().getId())
      return BAD_IDEA_CARD_OPEN_TEXT;
    else if (cid == getDescendantOrdinal(1).getId()) //getExpandType().getId())
      return EXPAND_CARD_OPEN_TEXT;
    else if (cid == getDescendantOrdinal(2).getId()) //getCounterType().getId())
      return COUNTER_CARD_OPEN_TEXT;
    else if(cid == getDescendantOrdinal(3).getId()) //getAdaptType().getId())
      return ADAPT_CARD_OPEN_TEXT;
    else if(cid == getDescendantOrdinal(4).getId()) //getExploreType().getId())
      return EXPLORE_CARD_OPEN_TEXT;
    else {
      System.err.println("Bogus card type passed to CardTypeManager.getCardCreateClickDebugId(), id = "+cid);
      return GOOD_IDEA_CARD_OPEN_TEXT;
    }
  }

  public static void updateCardType(CardType obj)
  {
    if(obj == null) {
      System.err.println("Null cardtype to CardTypeManager.updateCardType");
      return;
    }
    if(isExpandType(obj))
      expand = obj;
    else if(isCounterType(obj))
      counter = obj;
    else if(isAdaptType(obj))
      adapt = obj;
    else if(isExploreType(obj))
      explore = obj;
 
    else if(obj.isPositiveIdeaCard())
      resource = obj;
    else if(obj.isNegativeIdeaCard())
      risk = obj;
    
    else
      System.err.println("Unrecognized card type in CardTypeManager");
  }

}
