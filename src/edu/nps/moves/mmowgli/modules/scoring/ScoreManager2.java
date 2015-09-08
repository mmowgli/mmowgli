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

package edu.nps.moves.mmowgli.modules.scoring;

import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.modules.cards.CardMarkingManager;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
import static edu.nps.moves.mmowgli.MmowgliConstants.*;
/**
 * ScoreManager2.java
 * Created on Aug 8, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ScoreManager2
{
  public ScoreManager2()
  {
    // uncomment to make params constant while game in play.  remove individual calls below
    // refreshScoringParameters();
  }
    
  private float cardAuthorPoints = 0.0f;
  private float cardAncestorPoints = 0.0f;
  private float[] ancestorFactors = null;
  private float cardSuperInterestingPoints = 0.0f;
  
  private float actionPlanRaterPoints = 0.0f;
  //private float actionPlanSuperInterestingPoints = 0.0f;
  private float actionPlanCommentPoints = 0.0f;
  private float actionPlanAuthorPoints = 0.0f;
  private float actionPlanThumbFactor = 1.0f;
  
  private float userActionPlanCommentPoints = 0.0f;
  private float userSignupAnswerPoints;
  
  private String marker = ""; //"=======>>";
  
  // Called by code which has just created and saved to db.
  // This call updates author Users in db.
//  public void cardPlayed(Card newCard)
//  //----------------------------------
//  {
//    MSysOut.println(marker+"ScoreManager2.cardPlayed()");
//    refreshScoringParameters();
//    
//    awardCardAuthorPoints(newCard);
//    awardCardAncestorPoints(newCard); /**/
//  }
  
  public void cardPlayedTL(Card newCard)
  {
    MSysOut.println(SCOREMANAGER_LOGS,marker+"ScoreManager2.cardPlayed()");
    refreshScoringParametersTL();
    
    awardCardAuthorPointsTL(newCard);
    awardCardAncestorPointsTL(newCard); /**/
    
  }
  
  // Called when the card marking is about to be removed.  This concerns us (Score mgr) if the card was previously super-interesting
  // or hidden.  In the former, we take away points; in the latter we add points.
  // Does nothing with Hibernate on the card, such as update();

  public void cardMarkingWillBeClearedTL(Card card)
  //---------------------------------------------
  {
    MSysOut.println(SCOREMANAGER_LOGS,marker+"ScoreManager2.cardMarkingWillBeClearedTL()");
    refreshScoringParametersTL();
    Set<CardMarking> mark = card.getMarking();
    if(mark==null || mark.size()<=0)
      return; // already cleared
    
    if(CardMarkingManager.isHidden(card)) {
      awardCardAuthorPointsTL(card);
    }
    
    if(CardMarkingManager.isSuperInteresting(card)) {
      removeCardSuperInterestingPointsTL(card);
    }
  }
  
  // Called when the card is about to be (re) marked.  Same issues as above.
  // Does nothing with Hibernate on the card, such as update();

  public void cardMarkingWillBeSetTL(Card card, CardMarking cm)
  //---------------------------------------------------------
  {
    MSysOut.println(SCOREMANAGER_LOGS,marker+"ScoreManager2.cardMarkingWillBeSet()");
    refreshScoringParametersTL();
    Set<CardMarking> mark = card.getMarking();
    // First look as existing
    if (mark != null && mark.size() > 0) {
      if (CardMarkingManager.isHidden(card)) {
        if(CardMarkingManager.isHiddenMarking(cm))
          return; // hidden to hidden
        awardCardAuthorPointsTL(card); // was hidden, now not
      }
      if (CardMarkingManager.isSuperInteresting(card)) {
        if (CardMarkingManager.isSuperInteresting(card)) {
          if(CardMarkingManager.isSuperInterestingMarking(cm))
            return; // no change
          removeCardSuperInterestingPointsTL(card);  // was super interesting, now not
        }
      }
    }
    
    if(CardMarkingManager.isHiddenMarking(cm))
      removeCardAuthorPointsTL(card);
    
    if(CardMarkingManager.isSuperInterestingMarking(cm))
      awardCardSuperInterestingPointsTL(card);
  }

  /* Begin ActionPlan scoring events */
  /***********************************/
  /** Called after user creates an action plan
  *
  * @param ap the created action plan
  * @param usr the user who created the action plan
  */
  public void actionPlanUserJoinsTL(ActionPlan ap, User usr)
  {
    MSysOut.println(SCOREMANAGER_LOGS,marker+"ScoreManager2.actionPlanUserJoins()");
    refreshScoringParametersTL();
    actionPlanNewAuthorPointsTL(usr,ap);
    actionPlanNewAuthorCommentPointsTL(usr,ap);
    actionPlanNewAuthorThumbPointsTL(usr,ap);
  }
    
  // C
  public void actionPlanCommentEnteredTL(ActionPlan plan, Message comment)
  // --------------------------------------------------------------------
  {
    // Little bump for making a comment
    MSysOut.println(SCOREMANAGER_LOGS,marker+"ScoreManager2.actionPlanCommentEntered()");
    refreshScoringParametersTL();
    User writer = comment.getFromUser();
    writer = User.mergeTL(writer);
    incrementInnovationScoreTL(writer, userActionPlanCommentPoints);
    User.updateTL(writer); HSess.closeAndReopen();

    // Authors need a bump
    Set<User> authors = plan.getAuthors();
    for (User author : authors) {
      setActionPlanCommentScoreTL(author, plan);
      User.updateTL(author); /**/
    }
    if(!authors.isEmpty())
      HSess.closeAndReopen();
  }

  // F
//  public void actionPlanMarkedSuperInteresting(ActionPlan plan)
//  //-----------------------------------------------------------
//  {
//    MSysOut.println(marker+"ScoreManager2.actionPlanMarkedSuperInteresting()");
//    refreshScoringParameters();
//    Set<User> authors =  plan.getAuthors();
//    for(User author : authors) {
//      incrementInnovationScore(author,actionPlanSuperInterestingPoints);
//      User.update(author); /**/
//    }   
//  }
  
  // F
//  public void actionPlanUnmarkedSuperInteresting(ActionPlan plan)
//  // -------------------------------------------------------------
//  {
//    MSysOut.println(marker+"ScoreManager2.actionPlanUnmarkedSuperInteresting()");
//    refreshScoringParameters();
//    Set<User> authors = plan.getAuthors();
//    for (User author : authors) {
//      incrementInnovationScore(author, actionPlanSuperInterestingPoints * -1.0f);
//      User.update(author); /**/
//    }
//  }
  
  //Called when user is clicking or unclicking a thumb.  Does not do Hibernate.updates
  // The map of user->thumbs is now updated
  // Caller should do User.update
  // Users who are authors  are User.update 'ed here
  // D
  public void actionPlanWasRatedTL(User me, ActionPlan ap, int count)
  //---------------------------------------------------------------
  {
    MSysOut.println(SCOREMANAGER_LOGS,marker+"ScoreManager2.actionPlanWasRated()");
    refreshScoringParametersTL();
    // count not used, already given to ap
    Set<User> authors = ap.getAuthors();
    if (authors.contains(me))
      return; // no points for rating your own plan

    // The rater gets a bump
    setUsersActionPlanThumbScoreTL(me, ap, actionPlanRaterPoints);
    
    // The authors get points
    for(User author : authors) {
      setActionPlanThumbScoreTL(author,ap,actionPlanThumbFactor * (float)ap.getSumThumbs());
      User.updateTL(author); /**/
    }
    if(!authors.isEmpty())
      HSess.closeAndReopen();
  }
 
   /* Begin non-card / non-actionplan scoring event(s) */
  /****************************************************/
  // Called when login completes
  public void userCreatedTL(User user)
  //--------------------------------
  {
    MSysOut.println(SCOREMANAGER_LOGS,marker+"ScoreManager2.userCreated()");
    refreshScoringParametersTL();
    if(userSignupAnswerPoints == 0.0f)
      return;
    
    String answer = user.getAnswer();
    if(answer != null && answer.length()>0)
      incrementBasicScoreTL(user,userSignupAnswerPoints);    
  }
      
  /*************************************************************/
  /* utility methods */
  /*************************************************************/
  // A
  private void actionPlanNewAuthorPointsTL(User author, ActionPlan ap)
  {
    setActionPlanAuthorScoreTL(author, ap, actionPlanAuthorPoints);
  }
  //C
  private void actionPlanNewAuthorCommentPointsTL(User author, ActionPlan ap)
  {
    Set<Message> set = ap.getComments();
    if(set.isEmpty())
      return;
    setActionPlanCommentScoreTL(author, ap);
  }
  // B
  private void actionPlanNewAuthorThumbPointsTL(User author, ActionPlan ap)
  {
    setActionPlanThumbScoreTL(author,ap,actionPlanThumbFactor * (float)ap.getSumThumbs());
  }
  
  private void refreshScoringParametersTL()
  {
    _refreshScoringParameters(Game.getTL());
  }
  private void _refreshScoringParameters(Game g)
  {
    cardAuthorPoints = g.getCardAuthorPoints();
    cardAncestorPoints = g.getCardAncestorPoints();
    cardSuperInterestingPoints = g.getCardSuperInterestingPoints();
    ancestorFactors = parseGenerationFactors(g);
    
    actionPlanRaterPoints = g.getActionPlanRaterPoints();
   // actionPlanSuperInterestingPoints = g.getActionPlanSuperInterestingPoints();
    actionPlanCommentPoints = g.getActionPlanCommentPoints();
    actionPlanAuthorPoints = g.getActionPlanAuthorPoints();
    actionPlanThumbFactor = g.getActionPlanThumbFactor();
    
    userActionPlanCommentPoints = g.getUserActionPlanCommentPoints();
    userSignupAnswerPoints = g.getUserSignupAnswerPoints();
  }
  
  public static float[] parseGenerationFactors(Game g)
  {
    String str = g.getCardAncestorPointsGenerationFactors();
    float[] factors=null;
    
    if(str != null) {
      str = str.trim();
      if(str.length()>0) {
        String[] sa = str.split("\\s+");
        if(sa.length>0) {
          factors = new float[sa.length];
          int i=0;
          for(String s : sa){
            try {
              factors[i] = Float.parseFloat(s);
            }
            catch(Throwable t) {
              System.err.println("Error parsing "+s+" to a float in ScoreManager1.parseFactors()");
              factors[i] = 1.0f;
            }
            i++;
          }
        }
      }
    }
    else
      factors = new float[]{1.0f};
    
    return factors;
  }
  
  
  private boolean setActionPlanCommentScoreTL(User author, ActionPlan ap)
  {
    Set<Message> set = ap.getComments();
    if(set.isEmpty())
      return false;
    
    return setActionPlanMappedScoreTL(author.getActionPlanCommentScores(), author, ap, set.size() * actionPlanCommentPoints);
  }

  private boolean setActionPlanThumbScoreTL(User author, ActionPlan ap, float newThumbScoreForThisAP)
  {
    return setActionPlanMappedScoreTL(author.getActionPlanThumbScores(), author, ap, newThumbScoreForThisAP);
  }

  private boolean setUsersActionPlanThumbScoreTL(User rater, ActionPlan ap, float newRatedScoreForThisAP)
  {
    return setActionPlanMappedScoreTL(rater.getActionPlanRatedScores(), rater, ap, newRatedScoreForThisAP);
  }

  private boolean setActionPlanAuthorScoreTL(User author, ActionPlan ap, float newAuthorScoreForThisAP)
  {
    return setActionPlanMappedScoreTL(author.getActionPlanAuthorScores(), author, ap, newAuthorScoreForThisAP);
  }
  
  private boolean setActionPlanMappedScoreTL(Map<ActionPlan,Double> apScores, User usr, ActionPlan ap, float newScore)
  {
    Double oldScore = apScores.get(ap);
    if(oldScore == null)
      oldScore = 0.0d;
    if(oldScore == newScore)
      return false;
    
    apScores.remove(ap);  // shouldn't have to do this for hibernate I think
    apScores.put(ap, (double) newScore);
    
    // now tweek the total by subtracting our old value, adding the new
    float existingTotalScoreForAllAPs = usr.getInnovationScore();
    usr.mmowgliSetInnovationScoreTL(existingTotalScoreForAllAPs - oldScore.floatValue() + (float)newScore);
     
    return true;
  }
  
  private User incrementInnovationScoreTL(User u, float f)
  {
    User author = User.getTL(u.getId()); //DBGet.getUserFresh(u.getId());
    float pts = Math.max(author.getInnovationScore()+f, 0.0f);
    author.mmowgliSetInnovationScoreTL(pts); 
    return author;
  }

  // This call updates User objects in db /**/
  private void awardCardAuthorPointsTL(Card card)
  {
    awardOrRemoveCardAuthorPointsTL(card, +1.0f); /**/
  }
  
  // This call updates User objects in db /**/
  private void removeCardAuthorPointsTL(Card card)
  {
    awardOrRemoveCardAuthorPointsTL(card, -1.0f);    /**/
  }

  // Updated users in db /**/
  private void awardOrRemoveCardAuthorPointsTL(Card newCard, float factor)
  {    
    float authorPoints = cardAuthorPoints * factor;
    if(authorPoints != 0.0f) {
      User u = incrementBasicScoreTL(newCard.getAuthor(),authorPoints);
      User.updateTL(u);
      HSess.closeAndReopen();
    }
  }
  
  private User incrementBasicScoreTL(User u, float f)
  {
    User author = User.getTL(u.getId()); //DBGet.getUserFresh(u.getId());
    float pts = Math.max(author.getBasicScore()+f, 0.0f); // never negative
    author.mmowgliSetBasicScoreTL(pts);
    return author;
  }
  
  private User incrementBasicScoreTL(long userid, float f)
  {
    return incrementBasicScoreTL(User.getTL(userid),f); //DBGet.getUserFresh(userid),f);
  }
  
  private void awardCardAncestorPointsTL(Card c)  /**/
  {
    if(cardAncestorPoints == 0.0f)
      return;
    int numGens = ancestorFactors.length;
    long authorId = c.getAuthor().getId();   
    
    int level = 0;
    while((c = c.getParentCard()) != null && level < numGens) {
      long aId =(long)c.getAuthor().getId();
      if(aId != authorId) {  // can't earn points from your own card
        if(CardMarkingManager.isHidden(c))// || CardMarkingManager.isScenarioFail(c))
          ;
        else
          awardCardAncestorPointsTL(aId, cardAncestorPoints*ancestorFactors[level]);  
      }
      level++;
    }  
  }
  
  private void awardCardAncestorPointsTL(long aId, float points) /**/
  {
    User usr = incrementBasicScoreTL(aId,points);
    User.updateTL(usr); /**/
    HSess.closeAndReopen();
  }
  
  private void removeCardSuperInterestingPointsTL(Card c)
  {
    if(cardSuperInterestingPoints == 0.0f)
      return;
    User author = c.getAuthor();
    User me = Mmowgli2UI.getGlobals().getUserTL();
    
    if(me.getId() != author.getId())  // can't get points for saying you're interesting
      incrementBasicScoreTL(author,-1.0f*cardSuperInterestingPoints); 
  }
  
  private void awardCardSuperInterestingPointsTL(Card c)
  {
    if(cardSuperInterestingPoints == 0.0f)
      return;
    User author = c.getAuthor();
    User me = Mmowgli2UI.getGlobals().getUserTL();
    
    if(me.getId() != author.getId())  // can't get points for saying you're interesting
      incrementBasicScoreTL(author,cardSuperInterestingPoints);
  }
  
 /* Called only from ApplicationMaster when switching moves */
 public static float getBasicPointsFromCurrentMove(User u, Session sess)
 {
   int moveNum = Move.getCurrentMove(sess).getNumber();
   return u.getBasicScoreMoveX(moveNum);
 }
 
 /* Called only from ApplicationMaster when switching moves */
 public static float getInnovPointsFromCurrentMove(User u, Session sess)
 {
   int moveNum = Move.getCurrentMove(sess).getNumber();
   return u.getInnovationScoreMoveX(moveNum);
 }
 
 /*********************** Rebuilding scores and testing **********/
 /*
 private HashMap<User, FauxUser> userMap = new HashMap<User,FauxUser>();


 public static ArrayList<String> rebuildInnovationPoints_test()
 {
  ScoreManager2 mgr2 = new ScoreManager2(null);
  mgr2.rebuildInnovPoints_test();
  return mgr2.dumpUserPoints(mgr2);
 }
 @SuppressWarnings("unchecked")
 public void rebuildInnovPoints_test()
 {
   double ACTIONPLAN_NEWAUTHOR_POINTS = 250.d;
   double ACTIONPLAN_POINTS_PER_COMMENT = 5.0d;
   double ACTIONPLAN_POINTS_FOR_RATING = 5.0d;

   Criteria crit = HibernateContainers.getSession().createCriteria(ActionPlan.class);
   crit.addOrder(Order.asc("id"));
   
   List<ActionPlan> lis = (List<ActionPlan>)crit.list();
   
   for(ActionPlan ap : lis) {
     Move mov = ap.getCreatedInMove();
     // Authors get 250 * total user thumbs; but if 0 thumbs, they still get 250, so do it this way:
     double newAuthorScore = ACTIONPLAN_NEWAUTHOR_POINTS + (ACTIONPLAN_NEWAUTHOR_POINTS * ap.getSumThumbs()); //ap.getAverageThumb()); // whoa! bug: ap.getSumThumbs());

     Set<User> authors =  ap.getAuthors();
     for(User author : authors) {
       FauxUser fu = getFauxUser(author);
       fu.incrementInnovationScoreByMove(mov, (float)newAuthorScore);
     }
     // Note:  If the user was added as an author in a Move following the one in which the AP is created, he's not supposed
     // to receive points, but we've got no way of telling here, and that hasn't been a frequent use case.
     
     // Now all the non-authors who have commented on the plan; give them 5 points per comment
     HashMap<User, Integer> hmap = new HashMap<User, Integer>();
     for (Message m : ap.getComments()) {
       if(m.getCreatedInMove().getNumber() != mov.getNumber())
         continue;   // comments made in other rounds don't affect scoring
       User commenter = m.getFromUser();
       Integer I = hmap.get(commenter);
       if (I == null)
         I = 0;
       hmap.put(commenter, I + 1);
     }

     // Hash map now has number of comments made by each user
     for (User commenter : hmap.keySet()) {
       if (authors.contains(commenter))
         continue; // don't get points for commenting on your own plan
       
       double val = ACTIONPLAN_POINTS_PER_COMMENT * hmap.get(commenter); // 5 * number of comments
       FauxUser fu = getFauxUser(commenter);
       fu.incrementInnovationScoreByMove(mov, (float)val);
       
       // And give the boost to each author
       for(User auth : authors) {
         getFauxUser(auth).incrementInnovationScoreByMove(mov, (float)val);
       }
    } 
    
    // Anyone who has rated this plan gets a bump
    Map<User,Integer> thumbMap = ap.getUserThumbs();
    Set<User> raters = thumbMap.keySet();
    for(User rater : raters) {
      getFauxUser(rater).incrementInnovationScoreByMove(mov, (float)ACTIONPLAN_POINTS_FOR_RATING);
    }
     
  }
 }
 
 @SuppressWarnings("unchecked")
 private ArrayList<String> dumpUserPoints(ScoreManager2 mgr2)
 {
   Criteria crit = HibernateContainers.getSession().createCriteria(User.class);
   crit.addOrder(Order.asc("registerDate"));
   
   List<User> lis = (List<User>)crit.list();
   String nl = System.getProperty("line.separator");
   StringBuilder sb = new StringBuilder();
   ArrayList<String> arlis = new ArrayList<String>();
   sb.append("Name,RegDate,RegInMove,BasicScore1,BasicScore2,InnovScore1, InnovScore2");
   sb.append(nl);
   arlis.add(sb.toString());
   sb.setLength(0);
   for(User user : lis) {
     FauxUser fu = mgr2.getFauxUser(user);
     sb.append(user.getUserName());
     sb.append(',');
     Date d = user.getRegisterDate();
     String ds = d==null?"":DateFormat.getInstance().format(d);
     sb.append(ds);
     sb.append(',');
     Move mv = user.getRegisteredInMove();
     int movNum = mv==null?1:mv.getNumber();
     sb.append(movNum);
     sb.append(',');
     
     for(int mov = 1; mov<=2; mov++) {
       sb.append(fu.getBasicScoreMoveX(mov));
       sb.append(',');
     }
     for(int mov = 1; mov<=2; mov++) {
       sb.append(fu.getInnovationScoreMoveX(mov));
       sb.append(',');
     }

     sb.append(nl);
     arlis.add(sb.toString());
     sb.setLength(0);
   }
   return arlis;
 }

 private FauxUser getFauxUser(User u)
 {
   FauxUser fu = userMap.get(u);
   if (fu == null) {
     fu = new FauxUser();
     userMap.put(u, fu);
   }
   return fu;
 }

 
 class FauxUser
 {
   private ArrayList<Float> basicScoresByMoveNumber = new ArrayList<Float>(Collections.nCopies(6, 0.0f));
   private ArrayList<Float> innovScoresByMoveNumber = new ArrayList<Float>(Collections.nCopies(6, 0.0f));
   
   FauxUser()
   {
   }

   public float getInnovationScoreMove1(){return innovScoresByMoveNumber.get(1);}
   public float getInnovationScoreMove2(){return innovScoresByMoveNumber.get(2);}
   public float getInnovationScoreMove3(){return innovScoresByMoveNumber.get(3);}
   public float getInnovationScoreMove4(){return innovScoresByMoveNumber.get(4);}
   public float getInnovationScoreMove5(){return innovScoresByMoveNumber.get(5);}
   
   public float getInnovationScoreMoveX(int n)   { return innovScoresByMoveNumber.get(n);}
   public float getInnovationScoreByMove(Move m) { return getInnovationScoreMoveX(m.getNumber());}
   
   public void setInnovationScoreMove1(float f){innovScoresByMoveNumber.set(1, f);}
   public void setInnovationScoreMove2(float f){innovScoresByMoveNumber.set(2, f);}
   public void setInnovationScoreMove3(float f){innovScoresByMoveNumber.set(3, f);}
   public void setInnovationScoreMove4(float f){innovScoresByMoveNumber.set(4, f);}
   public void setInnovationScoreMove5(float f){innovScoresByMoveNumber.set(5, f);}
   
   public void setInnovationScoreMoveX(int n, float f)   { innovScoresByMoveNumber.set(n,f);}
   public void setInnovationScoreByMove(Move m, float f) { setInnovationScoreMoveX(m.getNumber(),f); }
   
   public float getBasicScoreMove1(){return basicScoresByMoveNumber.get(1);}
   public float getBasicScoreMove2(){return basicScoresByMoveNumber.get(2);}
   public float getBasicScoreMove3(){return basicScoresByMoveNumber.get(3);}
   public float getBasicScoreMove4(){return basicScoresByMoveNumber.get(4);}
   public float getBasicScoreMove5(){return basicScoresByMoveNumber.get(5);}
   
   public float getBasicScoreMoveX(int n)   { return basicScoresByMoveNumber.get(n);}
   public float getBasicScoreByMove(Move m) { return getBasicScoreMoveX(m.getNumber());}
   
   public void setBasicScoreMove1(float f){basicScoresByMoveNumber.set(1, f);}
   public void setBasicScoreMove2(float f){basicScoresByMoveNumber.set(2, f);}
   public void setBasicScoreMove3(float f){basicScoresByMoveNumber.set(3, f);}
   public void setBasicScoreMove4(float f){basicScoresByMoveNumber.set(4, f);}
   public void setBasicScoreMove5(float f){basicScoresByMoveNumber.set(5, f);}
   
   public void setBasicScoreMoveX(int n, float f)   { basicScoresByMoveNumber.set(n,f);}
   public void setBasicScoreByMove(Move m, float f) { setBasicScoreMoveX(m.getNumber(),f); }
   
   public void incrementInnovationScoreByMove(Move m, float f)
   {
     setInnovationScoreByMove(m,getInnovationScoreByMove(m)+f);
   }
   public void incrementBasicScoreByMove(Move m, float f)
   {
     setBasicScoreByMove(m,getBasicScoreByMove(m)+f);
   }
 }
*/ 

}
