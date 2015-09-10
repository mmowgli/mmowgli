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

package edu.nps.moves.mmowgli.cache;

import static edu.nps.moves.mmowgli.MmowgliConstants.MCACHE_LOGS;

import java.util.*;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.db.GameEvent;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.messaging.MMessage;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * MCacheGameEventHelper.java created on Feb 9, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class MCacheGameEventHelper
{
  private List<GameEvent> gameEvents;
  public static int GAMEEVENTCAPACITY = 1000; // number cached, not all returned in a hunk to client
  private static int myLogLevel = MCACHE_LOGS;

  // package-local
  MCacheGameEventHelper(Session sess)
  {
    gameEvents = Collections.synchronizedList(new Vector<GameEvent>(GAMEEVENTCAPACITY + 1));
    _rebuildEvents(sess);
  }
  
  private void _rebuildEvents(Session sess)
  {
    synchronized(gameEvents) {
      List<GameEvent> events = eventsQuery(sess);
      gameEvents.clear();
      for(GameEvent ev : events)
        gameEvents.add(ev);
    }
  }
  
  @SuppressWarnings("unchecked")
  private List<GameEvent> eventsQuery(Session sess)
  {
    // Attempt to speed up query
    Long num =  (Long)sess.createCriteria(GameEvent.class)
        //.setProjection(Projections.rowCount()).uniqueResult();
        .setProjection(Projections.max("id")).uniqueResult();

    List<GameEvent> evs = null;
    if(num != null) {
      long lowlimit = Math.max(0L, num.longValue()-GAMEEVENTCAPACITY);
      evs = (List<GameEvent>) sess.createCriteria(GameEvent.class).
                             add(Restrictions.gt("id", lowlimit)).
                             addOrder(Order.desc("dateTime")).list();
    }
    else
      evs = new ArrayList<GameEvent>();

    // Old version
//    List<GameEvent> evs = (List<GameEvent>) sess.createCriteria(GameEvent.class).
//    addOrder(Order.desc("dateTime")).
//    setMaxResults(GAMEEVENTCAPACITY).list();
    return evs;
  }

  void putGameEvent(GameEvent ge)
  {
    synchronized(gameEvents) {
      gameEvents.add(0, ge);
      int i;
      while((i=gameEvents.size()) > GAMEEVENTCAPACITY)
        gameEvents.remove(i-1);
    }
  }

  void newGameEventTL(char messageType, String message)
  {
    Long id = MMessage.MMParse(messageType,message).id;
    GameEvent ev = GameEvent.getTL(id); 

    // Here's the check for receiving notification that an event has been created, but it ain't in the db yet.
    if(ev == null) {
      ev = new GameEvent(GameEvent.EventType.UNSPECIFIED,"/ event not yet in database");
      ev.setId(id);
      updateGameEventWhenPossible(ev);
      return;
    } 
    
    synchronized(gameEvents) {
      gameEvents.add(0, ev);
      int i;
      while((i=gameEvents.size()) > GAMEEVENTCAPACITY)
        gameEvents.remove(i-1);
    }
    
  }
  
  public GameEvent getGameEventWhenPossible(Long id)
  {
     GameEvent ev = new GameEvent();
     ev.setId(id);
     updateGameEventWhenPossible(ev,true);
     
     return ev;
  }
  
  private void updateGameEventWhenPossible(final GameEvent evorig)
  {
    updateGameEventWhenPossible(evorig,false);
  }
  
  private void updateGameEventWhenPossible(final GameEvent evorig, boolean wait)
  {
    Thread thr = new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        for(int i=0; i<10; i++) { // try for 10 seconds
          try{Thread.sleep(1000l);}catch(InterruptedException ex){}
          HSess.init();

          GameEvent ge = (GameEvent)HSess.get().get(GameEvent.class, evorig.getId());
          if(ge != null) {
            if(i>0)
              MSysOut.println(myLogLevel,"(MCacheManager)Delayed fetch of GameEvent from db, got it on try "+i);
            evorig.clone(ge); // get its data
            HSess.close();
            return;
          }
          HSess.close();
        }
        System.err.println("ERROR: Couldn't get game event "+evorig.getId()+" in 10 seconds");// give up
      }
    });
    thr.setPriority(Thread.NORM_PRIORITY);
    thr.setDaemon(true);
    thr.setName("GameEventDbGetter");
    thr.start();
    
    if(wait){
      try {
        thr.join();
      }
      catch(InterruptedException ex) {

      }
    }
  }
  GameEvent[] getRecentGameEvents()
  {
    synchronized(gameEvents) {
      return gameEvents.toArray(new GameEvent[0]);
    }
  }

  GameEvent[] getNextGameEvents(Integer lastIndexGotten, Long lastIdGotten, int numToReturn)
  {
    int indexToGet = -1;
    if(lastIndexGotten == null)
      indexToGet = 0;
    else
      indexToGet = calcStartingPlace(lastIndexGotten, lastIdGotten);

    if(indexToGet != -1) {
      int numToGet = Math.min(gameEvents.size()-indexToGet, numToReturn);
      GameEvent[] arr = new GameEvent[numToGet];
      return gameEvents.subList(indexToGet, indexToGet + numToGet).toArray(arr);
    }
    else
      return new GameEvent[0];
  }

  /*
   * For the calling code in EventMonitorPanel, this is not really needed, since when an event comes in OOB, the vLay count of components, which serves as
   * the event index, gets updated automatically, so we stay in sync.
   */
  private int calcStartingPlace(int lastIndex,long lastId)
  {
    // Normally, we return lastIndex+1, but if we were updated, don't want to return what we already returned
    int maxIdx = gameEvents.size()-1;

    if(lastIndex >= maxIdx)
      return -1;

    int idx=lastIndex;
    while(idx < gameEvents.size()) {
      GameEvent ge = gameEvents.get(idx);
      if(ge.getId() == lastId) {
        idx++;
        break;
      }
      idx++;
    }
    if(idx >= maxIdx)
      return -1;
    return idx;
  }


}
