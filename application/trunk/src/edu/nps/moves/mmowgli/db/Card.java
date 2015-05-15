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

import static edu.nps.moves.mmowgli.MmowgliConstants.CARD_UPDATE_LOGS;
import static edu.nps.moves.mmowgli.hibernate.DbUtils.len255;
//import static edu.nps.moves.mmowgli.hibernate.DbUtils.forceUpdateEvent;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.annotations.*;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.hibernate.DB;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * Card.java

 * This is a database table, listing all cards played
 * 
 * Modified on Dec 16, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@SuppressWarnings("deprecation")
@Entity
@Indexed(index="mmowgli")
public class Card implements Serializable
{
  private static final long serialVersionUID = 6735230788451972828L;
  public static String[] CARD_SEARCH_FIELDS = {"id","text"};  // must be annotated for hibernate search
  
  public static int TEXT_FIELD_LENGTH = 255;
  public static int AUTHOR_FIELD_LENGTH = 255;
  
//@formatter:off
  
  long        id;            // Primary key, auto-generated.
  String      text;          // added by user
  boolean     isFactCard=false;    // shown at random after card play
  
  CardType    cardType;      //
  Card        parentCard;        // 
  SortedSet<Card>   followOns = Collections.synchronizedSortedSet(new TreeSet<Card>());
  User        author;        // Author of the card
  String      authorName = "author-name";    // "Denormalized" for performance; gotten from author
  Date        creationDate;  // when made
  Move        createdInMove;
  
  Set<CardMarking> marking = new HashSet<CardMarking>();       // optional marking by gamemasters, e.g., positive, negative, superinteresting
  boolean     hidden = false;  // duplicate of the CardMarking hidden, but used for Hibernate querying -- must be kept in sync
  
  Long        revision = 0L;   // used internally by hibernate for optimistic locking, but not here
//@formatter:on
  
  public Card()
  {
    setCreationDate(new Date());
  }
  
  public Card(String text, CardType type, Date creation)
  {
    setText(text);
    setCardType(type);
    setCreationDate(creation);   
  }
  
  public static HbnContainer<Card> getContainer()
  {
    return DB.getContainer(Card.class);
  }

  public static Card getTL(Object id)
  {
    return DB.getTL(Card.class, id);
  }
  
  public static Card getRevisionTL(Object id, long revision)
  {
    return DB.getRevisionTL(Card.class, id, revision);
  }

  public static Card get(Object id, Session sess)
  {
    return DB.get(Card.class, id, sess);
  }
  
  public static Card merge(Card c, Session sess)
  {
    return DB.merge(c, sess);
  }
  
  public static Card mergeTL(Card c)
  {
    return DB.mergeTL(c);
  }
 
  public static void updateTL(Card c)
  {
    c.incrementRevision();
    DB.updateTL(c);
    MSysOut.println(CARD_UPDATE_LOGS,"Card.updateTL() back from sess.update card "+c.getId()+" with text: "+c.getText()+" hidden = "+c.isHidden());
  }
 
  public static void saveTL(Card c)
  {
    DB.saveTL(c);;
    MSysOut.println(CARD_UPDATE_LOGS,"Card.saveTL() back from sess.save card "+c.getId()+" with text: "+c.getText());
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    if(cardType != null)
      sb.append(cardType.getTitle());
    else
      sb.append("unknown type");
    sb.append(" / ");
    sb.append(text);
    User author = this.getAuthor();
    if(author != null) {
      sb.append(" / author:");
      sb.append(author.getUserName());
    }
    Card parent = this.getParentCard();
    if(parent != null) {
      sb.append(" / parent card id:");
      sb.append(parent.getId());
    }
    sb.append(" / this id:");
    sb.append(id);
    return sb.toString();
  }
  
  public String toString2()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("id = ");
    sb.append(getId());
    sb.append(" text = ");
    sb.append(getText());
    sb.append(" revision = ");
    sb.append(getRevision());
    sb.append(" hidden = ");
    sb.append(isHidden());
    // temp sb.append("# children = ");
    // temp sb.append(getFollowOns()==null?"0":getFollowOns().size());
    return sb.toString();
  }
  
  @Id
  @DocumentId
  @GeneratedValue(strategy = GenerationType.AUTO)
  //@Column(nullable = false)
  @Field(analyze=Analyze.NO) //index=Index.UN_TOKENIZED)
  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }
  
  // This screws up our code@Version
  @Basic
  public Long getRevision()
  {
    return revision;
  }

  public void setRevision(Long revision)
  {
    this.revision = revision;
  }

  public Long incrementRevision()
  {
    setRevision(revision+1);
    return getRevision();
  }
  
  // This field duplicates CardMarking.hidden and must be kept in sync
  @Basic
  public boolean isHidden()
  {
    return hidden;
  }

  public void setHidden(boolean hidden)
  {
    this.hidden = hidden;
  }
  
  @Basic
  public boolean isFactCard()
  {
    return isFactCard;
  }

  public void setFactCard(boolean isFactCard)
  {
    this.isFactCard = isFactCard;
  }

  @ManyToOne
  @IndexedEmbedded
  public User getAuthor()
  {
    return author;
  }

  public void setAuthor(User author)
  {
    this.author = author;
    this.authorName = author.getUserName();
  }

  @Basic
  public String getAuthorName()
  {
    return authorName;
  }
  
  public void setAuthorName(String s)
  {
    authorName = len255(s);
  }
  
  @ManyToOne
  public Move getCreatedInMove()
  {
    return createdInMove;
  }

  public void setCreatedInMove(Move createdInMove)
  {
    this.createdInMove = createdInMove;
  }

  @Basic
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getText()
  {
    return text;
  }

  public void setText(String cardText)
  {
    this.text = len255(cardText);
  }

  // many cards can have the same type
  @ManyToOne
  public CardType getCardType()
  {
    return cardType;
  }

  public void setCardType(CardType cardType)
  {
    this.cardType = cardType;
  }

  // many cards can have the same markings
  @ManyToMany
  public Set<CardMarking> getMarking()
  {
    return marking;
  }
  
  public void setMarking(Set<CardMarking> marking)
  {
    this.marking = marking;
  }
  
  // many cards can have the same parent card
  @ManyToOne
  public Card getParentCard()
  {
    return parentCard;
  }

  public void setParentCard(Card parentCard)
  {
    this.parentCard = parentCard;
  }

  @Basic
  public Date getCreationDate()
  {
    return creationDate;
  }

  public void setCreationDate(Date creationDate)
  {
    this.creationDate = creationDate;
  }
  
  // This card can have many follow-on cards, but each follow-on has only one "parent"
  @OneToMany
  @JoinTable(name="Card_FollowOnCards",
        joinColumns = @JoinColumn(name="card_id"),
        inverseJoinColumns = @JoinColumn(name="follow_on_card_id")
    )
  @Sort(type=SortType.COMPARATOR, comparator=DateDescComparator.class)
  //@SortComparator(value=DateDescComparator.class) // hib 4 bug ?
  public SortedSet<Card> getFollowOns()
  {
    return followOns;
  }
  
  public void setFollowOns(SortedSet<Card> followOns)
  {
    this.followOns = followOns;
  }
  
  public static class DateDescComparator implements Comparator<Card>
  {
    @Override
    public int compare(Card c0, Card c1)
    {
      long l0 = c0.getCreationDate().getTime();
      long l1 = c1.getCreationDate().getTime();
      if(l1==l0)
        return 0;
      if(l1>l0)
        return +1;
      return -1;
      //return (int)(l1-l0); //(l0-l1);  // rounding err
    }
  }
  
//  public static Criteria adjustCriteriaToOmitCards(Criteria crit, User me)
//  {
//    // 2 conflicting requirements:
//    // 1: if guest and we're in prep phase, don't allow viewing of current moves cards;
//    // 2: if game doesn't allow prior card viewing, don't show old moves cards;
//
//    // since the combination of the 2 would potentially prohibit viewing all cards, only use one at at time.
//    // since the guest case is the special and most unfrequent one one, check for it first
//    Move thisMove = Move.getCurrentMove();
//    boolean canSeeCurrent = !MovePhase.isGuestAndIsPreparePhase(me);
//    boolean canSeePast = me.isAdministrator() || Game.get().isShowPriorMovesCards();
//
//    if (!canSeeCurrent && !canSeePast) {
//      crit.add(Restrictions.eq("factCard", true)); // effectively hides everything since we don't do fact cards
//    }
//    else {
//      if (!canSeeCurrent) {
//        crit.createAlias("createdInMove", "MOVE").add(Restrictions.ne("MOVE.number", thisMove.getNumber()));
//      }
//      if (!canSeePast) {
//        crit.createAlias("createdInMove", "MOVE").add(Restrictions.eq("MOVE.number", thisMove.getNumber()));
//      }
//    }
//    return crit;
//  }
  
  public static Criteria adjustCriteriaToOmitCardsTL(Criteria crit, User me)
  {
    // 2 conflicting requirements:
    // 1: if guest and we're in prep phase, don't allow viewing of current moves cards;
    // 2: if game doesn't allow prior card viewing, don't show old moves cards;

    // since the combination of the 2 would potentially prohibit viewing all cards, only use one at at time.
    // since the guest case is the special and most unfrequent one one, check for it first
    Move thisMove = Move.getCurrentMoveTL();
    boolean canSeeCurrent = true; // not used anymore !MovePhase.isGuestAndIsPreparePhaseTL(me);
    boolean canSeePast = me.isAdministrator() || Game.getTL().isShowPriorMovesCards();

    if (!canSeeCurrent && !canSeePast) {
      crit.add(Restrictions.eq("factCard", true)); // effectively hides everything since we don't do fact cards
    }
    else {
      if (!canSeeCurrent) {
        crit.createAlias("createdInMove", "MOVE").add(Restrictions.ne("MOVE.number", thisMove.getNumber()));
      }
      if (!canSeePast) {
        crit.createAlias("createdInMove", "MOVE").add(Restrictions.eq("MOVE.number", thisMove.getNumber()));
      }
    }
    return crit;
  }
  
  public static boolean canSeeCardTL(Card card, User me)
  {
    return canSeeCard_oob(card,me,HSess.get());
  }
//  public static boolean canSeeCard(Card card, User me)
//  {
//    return canSeeCard_oob(card,me,VHib.getVHSession());
//  }
  public static boolean canSeeCard_oobTL(Card card, User me)
  {
    return canSeeCard_oob(card,me,HSess.get());
  }
  public static boolean canSeeCard_oob(Card card, User me, Session sess)
  {
    //card = (Card)sess.merge(card); too expensive, and not needed if card.hidden bit is used instead of marking array
    Move thisMove = Move.getCurrentMove(sess);
    MovePhase thisPhase = thisMove.currentMovePhase;
    int thisMoveNum = thisMove.getNumber();
    int cardMoveNum = card.getCreatedInMove().getNumber();

    boolean isHidden = card.isHidden();    // boolean isHidden = CardMarkingManager.isHidden(card);  too expensive, use card hidden bit instead
    //boolean canSeeCurrent = !MovePhase.isGuestAndIsPreparePhase(me) || (thisMoveNum == 1);  // last clause added 6 Sep 2013
    boolean canSeeCurrent =  me.isAdministrator() ||
                             me.isGameMaster() ||
                             (( thisMoveNum == 1 || ! thisPhase.isPreparePhase()) && !isHidden);
    if(me.isViewOnly() && isHidden) // never let guests see hidden cards
      canSeeCurrent = false;
    
    boolean canSeePast = me.isAdministrator() || (Game.get(sess).isShowPriorMovesCards() && !isHidden);
    
    if(cardMoveNum == thisMoveNum && canSeeCurrent)
      return true;
    if(cardMoveNum != thisMoveNum && canSeePast)
      return true;
    
    return false;
  }

}
