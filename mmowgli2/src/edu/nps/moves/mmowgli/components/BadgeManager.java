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

package edu.nps.moves.mmowgli.components;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import static edu.nps.moves.mmowgli.db.Badge.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.messaging.MMessage;
import edu.nps.moves.mmowgli.messaging.MMessagePacket;
import edu.nps.moves.mmowgli.modules.cards.CardMarkingManager;
import edu.nps.moves.mmowgli.modules.cards.CardTypeManager;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * BadgeManager.java
 * Created on Oct 7, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class BadgeManager implements Runnable
{
  AppMaster master;

  private Thread thread;
  private long SLEEPPERIOD_MS = 1000;
  private long FIRSTRUNDELAY_MS = 5000;

  private final long LEADERBOARD_CHECK_INTERVAL_MS = 5*60*1000;  // 5 minutes
  private long lastLeaderboardCheck = 0;
  private final int leadGroupLen = 50; // top 50
  private final long leadUserCountTrigger =  100;  // have to have at least this many users before we start thinking of leaders

  private boolean firstRunComplete = false;

  public BadgeManager(AppMaster master)
  {
    this.master = master;
    queue = new LinkedBlockingQueue<Pkt>(); // no limit
    thread = new Thread(this,"BadgeManagerThread");
    thread.setPriority(Thread.NORM_PRIORITY);
    thread.setDaemon(true);
    thread.start();
  }
  
  public void messageReceivedTL(MMessagePacket pkt)
  {
    switch (pkt.msgType) {
    case NEW_CARD :
    case UPDATED_CARD:
    case NEW_ACTIONPLAN:
    case UPDATED_ACTIONPLAN:
    case UPDATED_USER:
      enQ(pkt.msgType,pkt.msg);
      break;

    default:
    }    
  }
  
  private void enQ(char msgTyp, String msg)
  {
    long id;
    if(msgTyp == UPDATED_CARD  || msgTyp == UPDATED_USER) {
      String[] sa = msg.split(MMessage.MMESSAGE_DELIM);
      id = Long.parseLong(sa[0]);
    }
    else
      id = Long.parseLong(msg);   // db key
    try {
      queue.put(new Pkt(msgTyp,id));
    }
    catch(InterruptedException ie) {
      System.err.println("Error in BadgeManager.queue.put()...should never have to wait: "+ie.getLocalizedMessage());
    }
  }

  class Pkt {
    public char msgType;
    public long id;
    Pkt(char msgType, long id)
    {
      this.msgType = msgType;
      this.id = id;
    }
  }

  private LinkedBlockingQueue<Pkt> queue;

  private boolean killed = false;
  public void kill() {
      killed = true;
      thread.interrupt();
  }

  // Badge processing loop
  @Override
  public void run()
  {
    if(!firstRunComplete) {   // not used, since run is not reentered
      updateAllBadges();  // does its own hibtlsession
      firstRunComplete = true;
    }

    while(true) {
      try {
        Pkt pkt = queue.take();       // block here
        HSess.init();
        
        checkBadgeThreeTL(); // get checked every time

        switch(pkt.msgType) {
        case NEW_CARD:
        case UPDATED_CARD:
          Card c = Card.getTL(pkt.id);
          checkBadgeOneTL(c);  // one of each root card type
          checkBadgeFourTL(c); // marked superinteresting
          checkBadgeTwoTL(c.getAuthor()); // one of everytype
          break;
        case NEW_ACTIONPLAN:
        case UPDATED_ACTIONPLAN:
          ActionPlan ap = ActionPlan.getTL(pkt.id);
          checkBadgeSixTL(ap);  // ap author
          break;
        case UPDATED_USER:
          User u = User.getTL(pkt.id);
          checkBadgeFiveTL(u); // user fav list

          //todo: badge 8, logged in each day
          break;
        }
        checkLeaderBoardTL();          //top 50 of leader board
        
        HSess.close();

        Thread.sleep(SLEEPPERIOD_MS);
      }
      catch(InterruptedException ie) {
          if (killed)
            return;
      }
    }
  }

  /* Give the user Badge #7 if they've been in the top 50 */
  /* but only check every so often, to keep db accesses down */
  @SuppressWarnings("unchecked")
  private boolean checkLeaderBoardTL()
  {
    boolean ret = false;
    Long now = System.currentTimeMillis();
    if(now > (lastLeaderboardCheck+LEADERBOARD_CHECK_INTERVAL_MS)) {
      lastLeaderboardCheck = now;
      MSysOut.println(BADGEMANAGER_LOGS,"leaderboard badge check started: "+now);
      
      Session sess = HSess.get();
      // Got to have at least 100 reg. users non-gm
      Long num =  (Long)sess.createCriteria(User.class)
      .add(Restrictions.eq("gameMaster", false))
      .add(Restrictions.eq("administrator", false))
      .setProjection(Projections.rowCount()).uniqueResult();

      if(num < leadUserCountTrigger) {   // not enough to fool with
        MSysOut.println(BADGEMANAGER_LOGS,"leaderboard badge check ended (< min users): "+System.currentTimeMillis());
        return false;
      }

      // Query database for list of users, limit result set to 50, sort by basic score, exclude GM's

      List<User> lis = (List<User>)sess.createCriteria(User.class)
      .add(Restrictions.eq("gameMaster", false))
      .add(Restrictions.eq("administrator", false))
      .setMaxResults(leadGroupLen)
      .addOrder( Order.desc("basicScore"))
      .list();

      ret |= processLeadersTL(lis);

      // do the same for innovation score

      lis = (List<User>)sess.createCriteria(User.class)
      .add(Restrictions.eq("gameMaster", false))
      .add(Restrictions.eq("administrator", false))
      .setMaxResults(leadGroupLen)
      .addOrder( Order.desc("innovationScore"))
      .list();

      ret |= processLeadersTL(lis);

      MSysOut.println(BADGEMANAGER_LOGS,"leaderboard badge check ended: "+System.currentTimeMillis());
    }
    return ret;
  }

  private boolean processLeadersTL(List<User> lis)
  {
    boolean ret = false;
    for(User u: lis) {
      if(!hasBadge(u,BADGE_SEVEN_ID)) {
        addBadgeTL(u,BADGE_SEVEN_ID);
        ret = true;
      }
    }
    return ret;
  }

  private boolean hasBadge(User u, long badgeID)
  {
    Set<Badge> bSet = u.getBadges();
    for (Badge b : bSet) {
      if (b.getBadge_pk() == badgeID)
        return true; // we're done here
    }
    return false;
  }

  private void addBadgeTL(User u, long badgeID)
  {
    Set<Badge> bSet = u.getBadges();
    Badge bdg = Badge.getTL(badgeID);
    bSet.add(bdg);
    User.updateTL(u);
  }

  /* Give the user Badge #5 if they've played a card which somebody else thinks is a favorite */
  /* The user here is the one who just marked */
  /*
  private boolean checkBadgeFive(Pkt pkt, Session sess)
  {
    User marker = DBGet.getUser(pkt.id,sess);
    return checkBadgeFive(marker, sess);
  }
  */
  private boolean checkBadgeFiveTL(User marker)
  {
    boolean ret = false;
    Set<Card> favs = marker.getFavoriteCards();
    for(Card c : favs) {
      User author = c.getAuthor();
      if(!hasBadge(author,BADGE_FIVE_ID)) {  // First see if he's already got this one
        addBadgeTL(author,BADGE_FIVE_ID);
        ret = true;
      }
    }
    return ret;
  }

  /* Give the user Badge #6 if they've accepted an action plan invite*/
  private boolean checkBadgeSixTL(ActionPlan ap)
  {
    boolean ret = false;
    Set<User> authors = ap.getAuthors();
    for(User u : authors) {
      if(!hasBadge(u,BADGE_SIX_ID)) {
        addBadgeTL(u,BADGE_SIX_ID);
        ret = true;
      }
    }
    return ret;
  }

  /* Give the user Badge #1 if they've played each of 2 root types */
  private boolean checkBadgeOneTL(Card c)
  {
    User author = c.getAuthor();

    if(hasBadge(author,BADGE_ONE_ID))    // First see if he's already got this one
      return false;

    return checkBadgeOneTL(author);
  }

  private boolean checkBadgeOneTL(User author)
  {
    Session sess = HSess.get();
    
    Long numInnos =  (Long)sess.createCriteria(Card.class)
    .add(Restrictions.eq("author", author))
    .add(Restrictions.eq("cardType", CardTypeManager.getPositiveIdeaCardType(sess)))
    .setProjection(Projections.rowCount()).uniqueResult();
    if(numInnos <= 0) {
      return false;
    }
    Long numDefs =  (Long)sess.createCriteria(Card.class)
    .add(Restrictions.eq("author", author))
    .add(Restrictions.eq("cardType", CardTypeManager.getNegativeIdeaCardType(sess)))
    .setProjection(Projections.rowCount()).uniqueResult();
    if(numDefs <= 0) {
      return false;
    }
    // Got one of each
    if(!hasBadge(author,BADGE_ONE_ID)) {
      addBadgeTL(author,BADGE_ONE_ID);
      return true;
    }
    return false;
  }

  /* Give the user Badge #4 if they've played a super-interesting card */
  private boolean checkBadgeFourTL(Card c)
  {
    if(CardMarkingManager.isSuperInteresting(c)) {
      User author = c.getAuthor();
      // Should check against user, don't let user get a badge for his own card
      if(!hasBadge(author,BADGE_FOUR_ID)) {    // First see if he's already got this one
        addBadgeTL(author,BADGE_FOUR_ID);
        return true;
      }
    }
    return false;
  }

  /* Give the user Badge #3 if they've played the root of a super-active chain */
  private boolean checkBadgeThreeTL()
  {
//    Card c = DBGet.getCard(pkt.id,sess);
//    User author = c.getAuthor();
//    if(hasBadge(author,BADGE_TWO_ID))    // First see if he's already got this one
//      return;
    boolean ret = false;
    // This checks everybody
    List<Card> roots = master.getMcache().getSuperActiveChainRoots();
    Session sess = HSess.get();
    for(Card crd : roots) {
      User author = crd.getAuthor();
      MSysOut.println(DEBUG_LOGS, "User.get(sess) from BadgeManager.checkBadgeThreeTL()");
      author = User.get(author.getId(), sess);  // Hb class not current in this sess
      if(!hasBadge(author,BADGE_THREE_ID)) {
        Badge third = (Badge)sess.get(Badge.class, BADGE_THREE_ID);
        author.getBadges().add(third);
        // User update here
        User.updateTL(author);
        ret = true;
      }
    }
    return ret;
  }

  /* Give the user Badge #2 if they've played all six types */
  /*
  private void checkBadgeTwo(Pkt pkt, Session sess)
  {
    Card c = DBGet.getCard(pkt.id, sess);
    User author = c.getAuthor();
    if(hasBadge(author,BADGE_TWO_ID))    // First see if he's already got this one
      return;

    checkBadgeTwo(author, sess);
  }
  */

  private boolean checkBadgeTwoTL(User author)
  {
    CardType[] allTypes = new CardType[] {
        CardTypeManager.getNegativeIdeaCardTypeTL(),
        CardTypeManager.getPositiveIdeaCardTypeTL(),
        CardTypeManager.getAdaptTypeTL(),
        CardTypeManager.getCounterTypeTL(),
        CardTypeManager.getExpandTypeTL(),
        CardTypeManager.getExploreTypeTL()
    };

    for(CardType ct : allTypes) {
      Long num =  (Long)HSess.get().createCriteria(Card.class)
      .add(Restrictions.eq("author", author))
      .add(Restrictions.eq("cardType", ct))
      .setProjection(Projections.rowCount()).uniqueResult();

      if(num <= 0)  // If any fail, no go
        return false;
    }
    addBadgeTL(author,BADGE_TWO_ID);
    return true;
  }

  // Done once per launch
  @SuppressWarnings("unchecked")
  private void updateAllBadges()
  {
    try {
      Thread.sleep(FIRSTRUNDELAY_MS);
    } catch(InterruptedException ex) {}

    MSysOut.println(BADGEMANAGER_LOGS,"BadgeManager: begin one-time sync of all badges.");

    HSess.init();

    checkBadgeThreeTL(); // get checked every time
    
    Session sess = HSess.get();
    
    List<User> uLis = (List<User>)sess.createCriteria(User.class).list();
    for(User u: uLis) {
      checkBadgeOneTL (u); // one of each root card type
      checkBadgeTwoTL (u); // one of everytype
      checkBadgeFiveTL(u); // user fav list
    }

    List<Card> cLis = (List<Card>)sess.createCriteria(Card.class).list();
    for(Card c: cLis)
      checkBadgeFourTL(c); // marked superinteresting

    List<ActionPlan> apLis = (List<ActionPlan>)sess.createCriteria(ActionPlan.class).list();
    for(ActionPlan ap: apLis)
      checkBadgeSixTL(ap); // ap author

    // todo: badge 8, logged in each day

    checkLeaderBoardTL(); // top 50 of leader board
    
    HSess.close();
    MSysOut.println(BADGEMANAGER_LOGS,"BadgeManager: end one-time sync of all badges.");
  }
}
