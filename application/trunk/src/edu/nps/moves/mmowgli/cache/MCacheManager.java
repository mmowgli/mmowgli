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

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.util.*;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.cache.MCacheActionPlanHelper.QuickActionPlan;
import edu.nps.moves.mmowgli.cache.MCacheUserHelper.QuickUser;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DB;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.messaging.InterTomcatIO.JmsReceiver;
import edu.nps.moves.mmowgli.messaging.MMessage;
import edu.nps.moves.mmowgli.messaging.MMessagePacket;
import edu.nps.moves.mmowgli.modules.cards.CardDbHelper;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * MCacheManager.java
 * Created on May 18, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MCacheManager implements JmsReceiver
{
  private MCacheGameEventHelper  gameEventHelper;
  private MCacheUserHelper       userHelper;
  private MCacheActionPlanHelper actionPlanHelper;
  
  private SortedMap<Long,Card> allNegativeIdeaCardsCurrentMove;
  private SortedMap<Long,Card> unhiddenNegativeIdeaCardsCurrentMove;
  private SortedMap<Long,Card> allNegativeIdeaCards;
  private SortedMap<Long,Card> unhiddenNegativeIdeaCardsAll;

  private SortedMap<Long,Card> allPositiveIdeaCardsCurrentMove;
  private SortedMap<Long,Card> unhiddenPositiveIdeaCardsCurrentMove;
  private SortedMap<Long,Card> allPositiveIdeaCards;
  private SortedMap<Long,Card> unhiddenPositiveIdeaCardsAll;

  private CardType negativeTypeCurrentMove;
  private CardType positiveTypeCurrentMove;

  private static int myLogLevel = MCACHE_LOGS;
  
  private MSuperActiveCacheManager supActMgr;

  private static MCacheManager me;
  public static MCacheManager instance()
  {
    if(me == null)
      me = new MCacheManager();
    return me;
  }

  private MCacheManager()
  {
    MSysOut.println(myLogLevel,"Enter MCacheManager constructor");
    try {
      Session sess = HSess.getSessionFactory().openSession();
      gameEventHelper = new MCacheGameEventHelper(sess);
      userHelper = new MCacheUserHelper(sess);
      actionPlanHelper = new MCacheActionPlanHelper(sess);
      
      supActMgr = new MSuperActiveCacheManager();

      negativeTypeCurrentMove = CardType.getNegativeIdeaCardType(sess);
      positiveTypeCurrentMove = CardType.getPositiveIdeaCardType(sess);

      allNegativeIdeaCardsCurrentMove = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));
      allPositiveIdeaCardsCurrentMove = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));

      allNegativeIdeaCards = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));
      allPositiveIdeaCards = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));

      unhiddenNegativeIdeaCardsCurrentMove = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));
      unhiddenPositiveIdeaCardsCurrentMove = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));

      unhiddenNegativeIdeaCardsAll = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));
      unhiddenPositiveIdeaCardsAll = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));

      _rebuildCards(sess);

      supActMgr.rebuild(sess);

      sess.close();

      MSysOut.println(myLogLevel,"Exit MCacheManager constructor");
    }
    catch (Throwable t) {
      System.err.println("Exception in MCacheManager: "+t.getClass().getSimpleName()+" "+t.getLocalizedMessage());
      t.printStackTrace();
    }
  }

  private List<Card> positiveCardsCurrentMoveOnly(Session sess)
  {
    return CardDbHelper.getCardsCurrentMoveOnly(sess,positiveTypeCurrentMove);
  }

  private List<Card> negativeCardsCurrentMoveOnly(Session sess)
  {
    return CardDbHelper.getCardsCurrentMoveOnly(sess,negativeTypeCurrentMove);
  }

  // Feb 2015 Before leaving this method, make sure that the referenced object can be read from the db. It's easy to out race Hibernate it seems
  public boolean handleIncomingDatabaseMessage(MMessagePacket packet)
  {
    MSysOut.println(myLogLevel,"MCacheManager.handleIncomingDatabaseMessageTL(), type = "+packet.msgType);
    switch (packet.msgType) {
      case NEW_CARD:
        dbNewCard(packet.msgType,packet.msg);
        break;
      case UPDATED_CARD:
        dbUpdatedCard(packet.msgType,packet.msg);
        break;
      case GAMEEVENT:
        dbNewGameEvent(packet.msgType,packet.msg);
        break;
    //case NEW_USER:
      case UPDATED_USER:
        userHelper.dbUpdatedUser(packet.msgType,packet.msg);
        break;
      case DELETED_USER:
        userHelper.dbDeleteUser(packet.msgType,packet.msg);
        break;
      case UPDATED_ACTIONPLAN:
        actionPlanHelper.dbUpdatedActionPlan(packet.msgType,packet.msg);
        break;
      case NEW_ACTIONPLAN:
        actionPlanHelper.dbNewActionPlan(packet.msgType,packet.msg);
      default:
    }
    return false;  
  }
  
  @Override
  public boolean handleIncomingTomcatMessageTL(MMessagePacket packet)
  {
    MSysOut.println(myLogLevel,"MCacheManager.handleIncomingTomcatMessageTL(), type = "+packet.msgType);
    switch (packet.msgType) {
      case NEW_CARD:
        externallyNewCardTL(packet.msgType,packet.msg);
        break;
      case UPDATED_CARD:
        externallyUpdatedCardTL(packet.msgType,packet.msg);
        break;
      case GAMEEVENT:
        externallyNewGameEventTL(packet.msgType,packet.msg);
        break;
    //case NEW_USER:    // will be incomplete
      case UPDATED_USER:
        userHelper.externallyNewOrUpdatedUserTL(packet.msgType,packet.msg);
        break;
      case DELETED_USER:
        userHelper.externallyDeletedUser(packet.msgType,packet.msg);
        break;
      case UPDATED_ACTIONPLAN:
        actionPlanHelper.externallyUpdatedActionPlanTL(packet.msgType,packet.msg);
        break;
      case NEW_ACTIONPLAN:
        actionPlanHelper.externallyNewActionPlanTL(packet.msgType,packet.msg);
      default:
    }
    return false;
  }
  
  // From another cluster node
  private void externallyUpdatedCardTL(char messageType, String message)
  {
    dbUpdatedCard(messageType,message); // no difference
  }
  
  // From the database listener, local to this cluster node
  private void dbUpdatedCard(char messageType, String message)
  {
    Object key = HSess.checkInit();
    MMessage msg = MMessage.MMParse(messageType, message);
    long id = msg.id;
    long revision = msg.version;
    MSysOut.println(MCACHE_LOGS,"MCacheManger.reportUpdatedCardTL() handling card "+id+" revision "+revision);  
    Card c = DB.getRetry(Card.class, id, revision, HSess.get());
    if (c == null) {
      MSysOut.println(ERROR_LOGS, "MCacheManger.reportUpdatedCardTL() card with id "+id+" can't be read from db.");
      HSess.checkClose(key);
      return;
    }
    newOrUpdatedCardTL_common(c); 
    MSysOut.println(MCACHE_LOGS,"MCacheManger.reportUpdatedCardTL() given to card caches "+c.toString2());
    HSess.checkClose(key);
  }
  
  // From another cluster node
  private void externallyNewCardTL(char messageType, String message)
  {
    dbNewCard(messageType, message); // no difference
  }
  
  // From the database listener, local to this cluster node
  private void dbNewCard(char messageType, String message)
  {
    Object key = HSess.checkInit();
    long id = MMessage.MMParse(messageType, message).id;
    MSysOut.println(MCACHE_LOGS,"MCacheManger.reportNewCardTL() handling card "+id);
    Card c = DB.getRetry(Card.class, id, null, HSess.get());
    if(c == null) {
      MSysOut.println(ERROR_LOGS, "MCacheManger.reportNewCardTL() card with id "+id+" can't be read from db.");
      HSess.checkClose(key);
      return;
    }
    newOrUpdatedCardTL_common(c);
    MSysOut.println(MCACHE_LOGS,"MCacheManger.reportNewCardTL() given to card caches "+c.toString2()); // causes lazy init error due to followons...why, I dont know
    HSess.checkClose(key);
  }

  private void newOrUpdatedCardTL_common(Card c)
  {
    CardType ct = c.getCardType();
    long cardTypeId = ct.getId();

    if(cardTypeId == negativeTypeCurrentMove.getId())
      newOrUpdatedCurrentMoveNegativeCard(c);

    else if(cardTypeId == positiveTypeCurrentMove.getId())
      newOrUpdatedCurrentMovePositiveCard(c);

    if(ct.getCardClass() == CardType.CardClass.POSITIVEIDEA)
      newOrUpdatedAllMovesPositiveCard(c);

    else if(ct.getCardClass() == CardType.CardClass.NEGATIVEIDEA)
      newOrUpdatedAllMovesNegativeCard(c);

    // all cards are checked for turning a chain into superactive
    supActMgr.newCard(c,HSess.get());
  }

  private void newOrUpdatedAllMovesPositiveCard(Card c)
  {
    synchronized(allPositiveIdeaCards) {
      allPositiveIdeaCards.put(c.getId(),c);
      synchronized(unhiddenPositiveIdeaCardsAll) {
        if(!c.isHidden())
          unhiddenPositiveIdeaCardsAll.put(c.getId(),c);
        else
          unhiddenPositiveIdeaCardsAll.remove(c.getId());
      }
    }
  }

  private void newOrUpdatedCurrentMovePositiveCard(Card c)
  {
    synchronized(allPositiveIdeaCardsCurrentMove) {
      allPositiveIdeaCardsCurrentMove.put(c.getId(),c);
      synchronized(unhiddenPositiveIdeaCardsCurrentMove) {
        if(!c.isHidden())
          unhiddenPositiveIdeaCardsCurrentMove.put(c.getId(),c);
        else
          unhiddenPositiveIdeaCardsCurrentMove.remove(c.getId());
      }
    }
  }

  private void newOrUpdatedAllMovesNegativeCard(Card c)
  {
    synchronized(allNegativeIdeaCards) {
      allNegativeIdeaCards.put(c.getId(), c);
      synchronized(unhiddenNegativeIdeaCardsAll) {
        if(!c.isHidden())
          unhiddenNegativeIdeaCardsAll.put(c.getId(), c);
        else
          unhiddenNegativeIdeaCardsAll.remove(c.getId());
      }
    }
  }

  private void newOrUpdatedCurrentMoveNegativeCard(Card c)
  {
    synchronized(allNegativeIdeaCardsCurrentMove) {
      allNegativeIdeaCardsCurrentMove.put(c.getId(), c);
      synchronized(unhiddenNegativeIdeaCardsCurrentMove) {
        if(!c.isHidden())
          unhiddenNegativeIdeaCardsCurrentMove.put(c.getId(), c);
        else
          unhiddenNegativeIdeaCardsCurrentMove.remove(c.getId());
      }
    }
  }

  class ReverseIdComparator implements Comparator<Long>
  {
    @Override
    public int compare(Long arg0, Long arg1)
    {
      return (int)(arg1 - arg0);   // highest first
    }
  }
   
  /******* GameEvents ***********/
  private void putGameEvent(GameEvent ge)
  {
    gameEventHelper.putGameEvent(ge);
  }
  
  private void dbNewGameEvent(char messageType, String message)
  {
    Object key = HSess.checkInit();
    externallyNewGameEventTL(messageType,message);
    HSess.checkClose(key);
  }
  
  private void externallyNewGameEventTL(char messageType, String message)
  {
    gameEventHelper.newGameEventTL(messageType, message);
  }
    
  public GameEvent[] getRecentGameEvents()
  {
    return gameEventHelper.getRecentGameEvents();
  }

  public GameEvent[] getNextGameEvents(Integer lastIndexGotten, Long lastIdGotten, int numToReturn)
  {
    return gameEventHelper.getNextGameEvents(lastIndexGotten, lastIdGotten, numToReturn);
  }

  /********** Users ***********/
  public void putUser(User u)  // from db listener
  {
    userHelper.putUser(u);
  }
  
  public void removeUser(User u)
  {
    userHelper.removeUser(u);
  }
  
  public List<QuickActionPlan> getQuickActionPlanList()
  {
    return actionPlanHelper.getQuickActionPlanList();
  }
  
  // Only used by add authordialog; list won't include guest account(s) or banished accounts
  public List<QuickUser> getUsersQuickList()
  {
    return userHelper.getUsersQuickList();
  }

  // Used by UserAdminPanel
  public List<QuickUser> getUsersQuickFullList()
  {
    return userHelper.getUsersQuickFullList();
  }

  public void putObject(Object obj)
  {
    if(obj.getClass().equals(GameEvent.class))
      putGameEvent((GameEvent)obj);
    
    else if(obj.getClass().equals(User.class))
      putUser((User)obj);
  }

  public  MCacheData<Card> getIdeaCards(CardType ct, Card c, int start, Integer count)
  {
    return getIdeaCards(ct,c,start,count,false);
  }

  public Collection<Card> getAllPositiveIdeaCards()
  {
    Vector<Card> v;
    synchronized(allPositiveIdeaCards) {
      v = new Vector<Card>(allPositiveIdeaCards.values());
    }
    return v;
  }

  public Collection<Card> getPositiveIdeaCardsCurrentMove()
  {
    Vector<Card> v;
    synchronized(allPositiveIdeaCardsCurrentMove) {
      v = new Vector<Card>(allPositiveIdeaCardsCurrentMove.values());
    }
    return v;
  }

  public Collection<Card> getAllPositiveUnhiddenIdeaCards()
  {
    Vector<Card> v;
    synchronized(unhiddenPositiveIdeaCardsAll) {
      v = new Vector<Card>(unhiddenPositiveIdeaCardsAll.values());
    }
    return v;
  }

  public Collection<Card> getPositiveUnhiddenIdeaCardsCurrentMove()
  {
    Vector<Card> v;
    synchronized(unhiddenPositiveIdeaCardsCurrentMove) {
      v = new Vector<Card>(unhiddenPositiveIdeaCardsCurrentMove.values());
    }
    return v;
  }

  public Collection<Card> getAllNegativeIdeaCards()
  {
    Vector<Card> v;
    synchronized(allNegativeIdeaCards) {
      v = new Vector<Card>(allNegativeIdeaCards.values());
    }
    return v;
  }

  public Collection<Card> getNegativeIdeaCardsCurrentMove()
  {
    Vector<Card> v;
    synchronized(allNegativeIdeaCardsCurrentMove) {
      v = new Vector<Card>(allNegativeIdeaCardsCurrentMove.values());
    }
    return v;
  }

  public Collection<Card> getAllNegativeUnhiddenIdeaCards()
  {
    Vector<Card> v;
    synchronized(unhiddenNegativeIdeaCardsAll) {
      v = new Vector<Card>(unhiddenNegativeIdeaCardsAll.values());
    }
    return v;
  }

  public Collection<Card> getNegativeUnhiddenIdeaCardsCurrentMove()
  {
    Vector<Card> v;
    synchronized(unhiddenNegativeIdeaCardsCurrentMove) {
      v = new Vector<Card>(unhiddenNegativeIdeaCardsCurrentMove.values());
    }
    return v;
  }
  
  public  MCacheData<Card> getIdeaCards(CardType ct, Card c, int start, Integer count, boolean unhiddenOnly)
  {
    if(ct.getId() == positiveTypeCurrentMove.getId())
      return getResourceCards(c,start,count,unhiddenOnly);
    else if (ct.getId() == negativeTypeCurrentMove.getId())
      return getRiskCards(c,start,count,unhiddenOnly);
    else
      throw new RuntimeException("Only risk and resource cards available through this interface");
  }

  private  MCacheData<Card> getResourceCards(Card updateFirst, int start, Integer count, boolean unhiddenOnly)
  {
    if(unhiddenOnly)
      return getIdeaCard(unhiddenPositiveIdeaCardsCurrentMove,updateFirst,start,count);
    return getIdeaCard(allPositiveIdeaCardsCurrentMove, updateFirst, start, count);
  }

  private  MCacheData<Card> getRiskCards(Card updateFirst, int start, Integer count, boolean unhiddenOnly)
  {
    if(unhiddenOnly)
      return getIdeaCard(unhiddenNegativeIdeaCardsCurrentMove,updateFirst,start,count);
    return getIdeaCard(allNegativeIdeaCardsCurrentMove, updateFirst, start, count);
  }

  private MCacheData<Card> getIdeaCard(SortedMap<Long, Card> map, Card updateFirst, int start, Integer count)
  {
    synchronized (map) {
      if (updateFirst != null)
        map.put(updateFirst.getId(), updateFirst);

      Card[] carrbig = new Card[0];
      carrbig = map.values().toArray(carrbig);

      int ender = carrbig.length - 1;
      if (count != null)
        ender = start + count;
      Card[] carrsmall = Arrays.copyOfRange(carrbig, start, ender); // ok to return out of sync block
      return new MCacheData<Card>(carrsmall,start,carrbig.length);
    }
  }

  private void _rebuildCards(Session sess)
  {
    synchronized (allNegativeIdeaCardsCurrentMove) {
      synchronized (unhiddenNegativeIdeaCardsCurrentMove) {
        unhiddenNegativeIdeaCardsCurrentMove.clear();
        List<Card> risks = negativeCardsCurrentMoveOnly(sess); //negativeCardsCurrentMoveQuery(sess);
        for (Card c : risks) {
          allNegativeIdeaCardsCurrentMove.put(c.getId(), c);
          if (!c.isHidden())
            unhiddenNegativeIdeaCardsCurrentMove.put(c.getId(), c);
        }
      }
    }

    synchronized (allPositiveIdeaCardsCurrentMove) {
      synchronized (unhiddenPositiveIdeaCardsCurrentMove) {
        unhiddenPositiveIdeaCardsCurrentMove.clear();
        List<Card> resources = positiveCardsCurrentMoveOnly(sess); //positiveCardsCurrentMoveQuery(sess);
        for (Card c : resources) {
          allPositiveIdeaCardsCurrentMove.put(c.getId(), c);
          if (!c.isHidden())
            unhiddenPositiveIdeaCardsCurrentMove.put(c.getId(), c);
        }
      }
    }

    /* all-moves maps: */
    synchronized (allPositiveIdeaCards) {
      synchronized (unhiddenPositiveIdeaCardsAll) {
        unhiddenPositiveIdeaCardsAll.clear();
        List<Card>posLis = CardDbHelper.getCardsByCardClass(sess, CardType.CardClass.POSITIVEIDEA);
        for(Card c : posLis) {
          allPositiveIdeaCards.put(c.getId(), c);
          if(!c.isHidden())
            unhiddenPositiveIdeaCardsAll.put(c.getId(), c);
        }
      }
    }
    synchronized (allNegativeIdeaCards) {
      synchronized (unhiddenNegativeIdeaCardsAll) {
        unhiddenNegativeIdeaCardsAll.clear();
        List<Card>negLis = CardDbHelper.getCardsByCardClass(sess, CardType.CardClass.NEGATIVEIDEA);
        for(Card c : negLis) {
          allNegativeIdeaCards.put(c.getId(), c);
          if(!c.isHidden())
            unhiddenNegativeIdeaCardsAll.put(c.getId(), c);
        }
      }
    }
  }

  public List<Card> getSuperActiveChainRoots()
  {
    return supActMgr.getSuperInterestingRoots();
  }
  
  public static class MCacheData<T>
  {
    public T[] data;  // returned piece of data
    public int start; // index of cached data item which is at index 0 in this array
    public int total; // total size of cached data

    public MCacheData(T[] data, int start, int total)
    {
      this.data = data;
      this.start = start;
      this.total = total;
    }
  }

  public static class UserIdNamePair
  {
    public long id;
    public String name;
    public UserIdNamePair(long id, String uname)
    {
      this.id = id;
      this.name = uname;
    }
  }

  public static SortedSet<Card> makeSortedCardSet()
  {
    return new TreeSet<Card>(new Comparator<Card>()
    {
      @Override
      public int compare(Card arg0, Card arg1)
      {
        return (int)(arg0.getId()-arg1.getId());
      }
    });
  }

  public static SortedMap<Long, Card> makeSortedCardMap()
  {
    return new TreeMap<Long, Card>(new Comparator<Long>()
    {
      @Override
      public int compare(Long arg0, Long arg1)
      {
        return (int) (arg1 - arg0); // highest first
      }
    });
  }
}
