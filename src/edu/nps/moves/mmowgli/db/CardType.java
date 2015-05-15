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

package edu.nps.moves.mmowgli.db;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.hibernate.DB;
import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 * 
 * @version $Id$
 * @since $Date$
 * @copyright Copyright (C) 2011
 */

@Entity
public class CardType implements Serializable
{
  private static final long serialVersionUID = -1252876642638588384L;

  static public final int EXPAND_CARD_TYPE = 1;
  static public final int COUNTER_CARD_TYPE = 2;
  static public final int ADAPT_CARD_TYPE = 3;
  static public final int EXPLORE_CARD_TYPE = 4;
  
  static public enum DescendantCardType {
    EXPAND, COUNTER, ADAPT, EXPLORE;
    public String description()
    {
      switch(this) {
      case EXPAND: return "Descendent EXPAND card";
      case COUNTER: return "Descendant COUNTER card";
      case ADAPT: return "Descendant ADAPT card";
      case EXPLORE: return "Descendant EXPLORE card";
    }
    throw new AssertionError("Unknown card type number: " + this);      
    }
  }
  
  static public enum CardClass {
    POSITIVEIDEA,NEGATIVEIDEA,DESCENDANT;
    public String description()
    {
      switch(this) {
        case POSITIVEIDEA: return "Innovate, resource, best, etc.";
        case NEGATIVEIDEA: return "Defend, risk, protect, worst";
        case DESCENDANT: return "Card played on any other card";
      }
      throw new AssertionError("Unknown card class: " + this);
    }
  };
  
  long id;      /* Primary key, auto-generated. */
  String title;
  String titleAlternate;
  boolean isIdeaCard;  // aka "initiating"
  String prompt;
  String summaryHeader;
  CardClass cardClass = CardClass.DESCENDANT; // default
  Integer descendantOrdinal; // can be null
  String cssColorStyle;
  String cssLightColorStyle;
  
  // The constructors are not used (so far) in mmowgli; it is assumed the database entries are statically set.
  // If used, there needs to be parameters for cardClass and descendantOrder.
  public CardType()
  {
  }
  
  public CardType(String title, String titleAlternate, boolean isIdeaCard, String prompt, String summaryHeader)
  {
    this.title = title;
    this.titleAlternate = titleAlternate;
    this.isIdeaCard = isIdeaCard;
    this.prompt = prompt;
    this.summaryHeader = summaryHeader;
  }
  
  public CardType(String title, String titleAlternate, boolean isIdeaCard, String prompt)
  {
    this(title,titleAlternate,isIdeaCard,prompt,"");
  }
  
  public static CardType getTL(Object id)
  {
    return DB.getTL(CardType.class, id);
  }
  
  public static List<CardType> getIdeaCards(Session sess)
  {
    List<CardType> lis = DB.getMultiple(CardType.class, sess, Restrictions.eq("ideaCard", true));
    
    assert lis.size()==2: "Two idea card types must be defined in the database";
    // put in order, pos then neg
    if(lis.get(0).cardClass == CardClass.POSITIVEIDEA)
      ;
    else {
      CardType ct = lis.get(0);
      lis.set(0, lis.get(1));
      lis.set(1, ct);
    }
    return lis;    
  }

  public static CardType getPositiveIdeaCardTypeTL()
  {
    return getPositiveIdeaCardType(HSess.get());
  }
  
  public static CardType getPositiveIdeaCardType(Session sess)
  {
    return getPositiveIdeaCardType(Game.get(sess).getCurrentMove());
  }
  
  // Following used by MmowgliMobile
  public static CardType getExpandType(Session sess)
  {
    return getExpandType(Game.get(sess).getCurrentMove());
  }
  public static CardType getCounterType(Session sess)
  {
    return getCounterType(Game.get(sess).getCurrentMove());
  }
  public static CardType getAdaptType(Session sess)
  {
    return getAdaptType(Game.get(sess).getCurrentMove());
  }
  public static CardType getExploreType(Session sess)
  {
    return getExploreType(Game.get(sess).getCurrentMove());
  }
    
  public static CardType getExpandTypeTL()
  {
    return getExpandType(Game.getTL().getCurrentMove());
  }
  public static CardType getExpandType(Move m)
  {
    return _getChildType(m,EXPAND_CARD_TYPE);    
  } 
  public static CardType getCounterTypeTL()
  {
    return getCounterType(Game.getTL().getCurrentMove());
  }
  public static CardType getCounterType(Move m)
  {
    return _getChildType(m,COUNTER_CARD_TYPE);    
  } 
  public static CardType getAdaptTypeTL()
  {
    return getAdaptType(Game.getTL().getCurrentMove());
  }
  public static CardType getAdaptType(Move m)
  {
    return _getChildType(m,ADAPT_CARD_TYPE);    
  } 
  public static CardType getExploreTypeTL()
  {
    return getExploreType(Game.getTL().getCurrentMove());
  }
  public static CardType getExploreType(Move m)
  {
    return _getChildType(m,EXPLORE_CARD_TYPE);    
  }
  
  private static CardType _getChildType(Move m, int typ)
  {
    Set<CardType> typs = m.getCurrentMovePhase().getAllowedCards();
    for(CardType ct : typs)
      if(!ct.isIdeaCard && ct.getDescendantOrdinal() == typ)
        return ct;
    return null;    
    
  }
  public static void setNegativeIdeaCardTypeTL(Move currentMove, CardType newNegativeCt)
  {
    MovePhase phase = currentMove.getCurrentMovePhase();
    setNegativeIdeaCardTypeTL(phase,newNegativeCt);
  }
  
  public static void setNegativeIdeaCardTypeAllPhasesTL(Move mov, CardType ct)
  {
    List<MovePhase> lis = mov.getMovePhases();
    for(MovePhase mp : lis) {
      mp = MovePhase.mergeTL(mp);
      setNegativeIdeaCardTypeTL(mp,ct);
    }
  }
  
  public static void setNegativeIdeaCardTypeTL(MovePhase phase, CardType newNegativeCt)
  {
    HashSet<CardType> typs = new HashSet<CardType>(phase.getAllowedCards());
    HashSet<CardType> set = new HashSet<CardType>();
    
    for(CardType ct : typs) {
      if(!ct.isNegativeIdeaCard())
        set.add(ct);
    }
    set.add(newNegativeCt);
    phase.setAllowedCards(set);
    MovePhase.updateTL(phase);
  }
  
  public static void setPositiveIdeaCardTypeTL(Move currentMove, CardType newPositiveCt)
  {
    setPositiveIdeaCardTypeTL(currentMove.getCurrentMovePhase(), newPositiveCt);
  }
  
  public static void setPositiveIdeaCardTypeAllPhasesTL(Move mov, CardType newPositiveCt)
  {
    List<MovePhase> lis = mov.getMovePhases();
    for(MovePhase mp : lis) {
      mp = MovePhase.mergeTL(mp);
      setPositiveIdeaCardTypeTL(mp,newPositiveCt);
    }
  }
  
  public static void setPositiveIdeaCardTypeTL(MovePhase phase, CardType newPositiveCt)
  {
    HashSet<CardType> typs = new HashSet<CardType>(phase.getAllowedCards());
    HashSet<CardType> set = new HashSet<CardType>();
    
    for(CardType ct : typs) {
      if(!ct.isPositiveIdeaCard())
        set.add(ct);
    }
    set.add(newPositiveCt);
    phase.setAllowedCards(set);
    MovePhase.updateTL(phase);
  }
  
  public static CardType getPositiveIdeaCardType(Move m)
  {
    Set<CardType> typs = m.getCurrentMovePhase().getAllowedCards();
    for(CardType ct : typs)
      if(ct.isPositiveIdeaCard())
        return ct;
    return null;
    
  }

  public static CardType getNegativeIdeaCardTypeTL()
  {
    return getNegativeIdeaCardType(HSess.get());
  }
  
 public static CardType getNegativeIdeaCardType(Session sess)
  {
//    Disjunction disj = Restrictions.disjunction();
//    disj.add(Restrictions.eq("title", "Worst Strategy"));
//    disj.add(Restrictions.eq("titleAlternate", "Worst Strategy"));
//    disj.add(Restrictions.eq("title", "Defend"));
//    disj.add(Restrictions.eq("titleAlternate", "Risk"));
 /*   
    List<CardType> lis =  (List<CardType>) sess.createCriteria(CardType.class).
                              add(Restrictions.eq("cardClass", CardClass.NEGATIVEIDEA)).
                              // add(disj).
                              list();
    return lis.get(0);
*/    
//    Set<CardType> typs = Game.get(sess).currentMove.getCurrentMovePhase().getAllowedCards();
//    for(CardType ct : typs)
//      if(ct.isNegativeIdeaCard())
//        return ct;
//    return null;
     return getNegativeIdeaCardType(Game.get(sess).getCurrentMove());
  }
  
  public static CardType getNegativeIdeaCardType(Move m)
  {
    Set<CardType> typs = m.getCurrentMovePhase().getAllowedCards();
    for(CardType ct : typs)
      if(ct.isNegativeIdeaCard())
        return ct;
    return null;    
  }
  
  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof CardType && ((CardType)obj).getTitle().equals(getTitle());
  }

  public static CardType mergeTL(CardType ct)
  {
    return DB.mergeTL(ct);
  }

  public static void updateTL(CardType ct)
  {
    DB.updateTL(ct);
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  public long getId()
  {
    return id;
  }

  public void setId(long card_pk)
  {
    this.id = card_pk;
  }

  @Basic
  public String getSummaryHeader()
  {
    return summaryHeader;
  }

  public void setSummaryHeader(String summaryHeader)
  {
    this.summaryHeader = summaryHeader;
  }
//  private Boolean isInnovate;
//  private Boolean isDefend;

  //This is bogus.  Need to redefine the 2 top level types as "positive root" and "negative root", separate from the text....done
  @Transient
  public boolean isPositiveIdeaCard()
  {
    return getCardClass() == CardClass.POSITIVEIDEA;
//    if(isInnovate == null)
//      isInnovate = (summaryHeader.equalsIgnoreCase("innovate") || summaryHeader.equalsIgnoreCase("resource") || summaryHeader.equalsIgnoreCase("disrupt") || summaryHeader.toLowerCase().contains("best") ||
//                            title.equalsIgnoreCase("innovate") ||         title.equalsIgnoreCase("resource") ||         title.equalsIgnoreCase("disrupt") ||         title.toLowerCase().contains("best"));
//    return isInnovate;
  }
  @Transient
  public boolean isNegativeIdeaCard()
  {
    return getCardClass() == CardClass.NEGATIVEIDEA;
//    if(isDefend == null)
//      isDefend = (summaryHeader.equalsIgnoreCase("defend") || summaryHeader.equalsIgnoreCase("risk") || summaryHeader.equalsIgnoreCase("protect") || summaryHeader.toLowerCase().contains("worst") ||
//                          title.equalsIgnoreCase("defend") ||         title.equalsIgnoreCase("risk") ||         title.equalsIgnoreCase("protect") ||         title.toLowerCase().contains("worst"));
//    return isDefend;
  }
  
  @Basic
  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  @Basic
  public String getTitleAlternate()
  {
    return titleAlternate;
  }

  public void setTitleAlternate(String titleAlternate)
  {
    this.titleAlternate = titleAlternate;
  }
  
  @Basic
  public boolean isIdeaCard()
  {
    return isIdeaCard;
  }

  public void setIdeaCard(boolean isIdeaCard)
  {
    this.isIdeaCard = isIdeaCard;
  }

  @Basic
  public String getPrompt()
  {
    return prompt;
  }

  public void setPrompt(String prompt)
  {
    this.prompt = prompt;
  }
  
  @Basic
  public CardClass getCardClass()
  {
    return cardClass;
  }

  public void setCardClass(CardClass cardClass)
  {
    this.cardClass = cardClass;
  }
  
  @Basic
  @Column(nullable = true)
  public Integer getDescendantOrdinal()
  {
    return descendantOrdinal;
  }

  public void setDescendantOrdinal(Integer descendantOrdinal)
  {
    this.descendantOrdinal = descendantOrdinal;
  }

  @Basic
  public String getCssColorStyle()
  {
    return cssColorStyle;
  }

  /**
   * @param cssColorStyle the cssColorStyle to set
   */
  public void setCssColorStyle(String cssColorStyle)
  {
    this.cssColorStyle = cssColorStyle;
  }

  @Basic
  public String getCssLightColorStyle()
  {
    return cssLightColorStyle;
  }

  public void setCssLightColorStyle(String cssLightColorStyle)
  {
    this.cssLightColorStyle = cssLightColorStyle;
  }

  @SuppressWarnings("unchecked")
  public static List<CardType> getDefinedDescendantsByType(Session sess, int descTyp)
  {
    return (List<CardType>) sess.createCriteria(CardType.class).
        add(Restrictions.eq("ideaCard", false)).
        add(Restrictions.eq("descendantOrdinal", descTyp)).
        list();
  } 
  @SuppressWarnings("unchecked")
  public static List<CardType> getDefinedIdeaCardsByClass(Session sess, CardClass cls)
  {
    return (List<CardType>) sess.createCriteria(CardType.class).
        add(Restrictions.eq("cardClass", cls)).
        list();
  }
  public static List<CardType> getDefinedPositiveTypesTL()
  {
    return getDefinedIdeaCardsByClass(HSess.get(),CardClass.POSITIVEIDEA);
  }
  public static List<CardType> getDefinedNegativeTypesTL()
  {
    return getDefinedIdeaCardsByClass(HSess.get(),CardClass.NEGATIVEIDEA);
  }
  
  public static List<CardType> getDefinedExpandTypesTL()
  {
    return getDefinedDescendantsByType(HSess.get(),EXPAND_CARD_TYPE);
  }
  public static List<CardType> getDefinedExpandTypes(Session sess)
  {
    return getDefinedDescendantsByType(sess,EXPAND_CARD_TYPE);
  }
  public static List<CardType> getDefinedCounterTypesTL()
  {
    return getDefinedDescendantsByType(HSess.get(),COUNTER_CARD_TYPE);
  }
  public static List<CardType> getDefinedCounterTypes(Session sess)
  {
    return getDefinedDescendantsByType(sess,COUNTER_CARD_TYPE);
  }
  public static List<CardType> getDefinedAdaptTypesTL()
  {
    return getDefinedDescendantsByType(HSess.get(),ADAPT_CARD_TYPE);
  }
  public static List<CardType> getDefinedAdaptTypes(Session sess)
  {
    return getDefinedDescendantsByType(sess,ADAPT_CARD_TYPE);
  }
  public static List<CardType> getDefinedExploreTypesTL()
  {
    return getDefinedDescendantsByType(HSess.get(),EXPLORE_CARD_TYPE);
  }
  public static List<CardType> getDefinedExploreTypes(Session sess)
  {
    return getDefinedDescendantsByType(sess,EXPLORE_CARD_TYPE);
  }

  public static void setExpandCardTypeTL(Move m, CardType ct)
  {
    setChildCardTypeTL(m,ct,EXPAND_CARD_TYPE);
  }
  public static void setExpandCardTypeAllPhasesTL(Move m, CardType ct)
  {
    setChildCardTypeAllPhasesTL(m,ct,EXPAND_CARD_TYPE);
  }
  public static void setCounterTLCardType(Move m, CardType ct)
  {
    setChildCardTypeTL(m,ct,COUNTER_CARD_TYPE);
  }
  public static void setCounterCardTypeAllPhasesTL(Move m, CardType ct)
  {
    setChildCardTypeAllPhasesTL(m,ct,COUNTER_CARD_TYPE);
  }
  public static void setAdaptCardTypeTL(Move m, CardType ct)
  {
    setChildCardTypeTL(m,ct,ADAPT_CARD_TYPE);
  }
  public static void setAdaptCardTypeAllPhasesTL(Move m, CardType ct)
  {
    setChildCardTypeAllPhasesTL(m,ct,ADAPT_CARD_TYPE);
  }
  public static void setExploreCardTypeTL(Move m, CardType ct)
  {
    setChildCardTypeTL(m,ct,EXPLORE_CARD_TYPE);
  }
  public static void setExploreCardTypeAllPhasesTL(Move m, CardType ct)
  {
    setChildCardTypeAllPhasesTL(m,ct,EXPLORE_CARD_TYPE);    
  }
  private static void setChildCardTypeTL(Move m, CardType newCt, int ordinal)
  {
    MovePhase phase = m.getCurrentMovePhase();
    setChildCardTypeTL(phase, newCt, ordinal);
  }
  private static void setChildCardTypeAllPhasesTL(Move m, CardType newCt, int ordinal)
  {
    List<MovePhase> lis = m.getMovePhases();
    for(MovePhase mp : lis)
      setChildCardTypeTL(mp,newCt,ordinal);
  }
  private static void setChildCardTypeTL(MovePhase phase, CardType newCt, int ordinal)
  {
    Set<CardType> typs = phase.getAllowedCards();
    // Build a list of existing type(s) to be replaced
    HashSet<CardType> set = new HashSet<CardType>();
    for(CardType ct : typs)
      if(!ct.isIdeaCard() && ct.getDescendantOrdinal() == ordinal)
        set.add(ct);
    // Remove them
    for(CardType ct : set){
      typs.remove(ct);
    }
    // Insert new one
    typs.add(newCt);
    phase.setAllowedCards(typs);
    MovePhase.updateTL(phase);   
  }
}
