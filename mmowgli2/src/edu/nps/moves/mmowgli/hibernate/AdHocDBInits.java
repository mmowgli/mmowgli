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

package edu.nps.moves.mmowgli.hibernate;

import java.util.*;

import org.hibernate.Session;
import org.jasypt.util.password.StrongPasswordEncryptor;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.pii.UserPii;

/**
 * AdHocDBInits.java
 * Created on Mar 6, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AdHocDBInits
{
  public static void databaseCheckUpdate(Session sess)
  {
    Game game = Game.get(sess);
    
    if(game.isBootStrapping()) {
      setUserPii(sess,"Administrator",true,  true,  true,  false);
      User seedCard = setUserPii(sess,"SeedCard",     false, true,  false, false);
      setUserPii(sess,"Guest",        false, false, false, true);
      setUserPii(sess,"GameMaster",   false, true,  false, false);

      setUpCardsAndAuthor(seedCard, sess, game);
      
      MovePhase mp = game.getCurrentMove().getCurrentMovePhase();
      mp.setNewButtonEnabled(false);
      sess.update(mp);

      game.setBootStrapping(false);
      sess.update(game);
    }
  }
  
  @SuppressWarnings("unchecked")
  private static void setUpCardsAndAuthor(User u, Session sess, Game g)
  {
    Calendar cal = new GregorianCalendar(2015,04,13);
    cal.set(Calendar.HOUR_OF_DAY, 1300);
    Move curMov = g.getCurrentMove();
    CardType pos = CardType.getPositiveIdeaCardType(curMov);
    CardType neg = CardType.getNegativeIdeaCardType(curMov);
    
    Card crd = null;
    for(int i=0; i<8; i++) {      
      Card c = new Card("Game designer enter initial text", i<4?pos:neg, cal.getTime());
      c.setAuthor(u);
      c.setCreatedInMove(curMov);
      sess.save(c);
      
      if(crd == null)
        crd = c;
    }
    
    List<ActionPlan> alis = sess.createCriteria(ActionPlan.class).list();
    for(ActionPlan ap: alis) {
      ap.addAuthor(u);
      ap.setChainRoot(crd);
      sess.update(ap);
    }
  }
  
  /**
   * This is a way to jam passwords into the PII 
   * @param uname
   */
  @SuppressWarnings("unchecked")
  private static User setUserPii(Session sess, String uname, boolean admin, boolean gm, boolean designer, boolean guest)
  {
    User u = User.getUserWithUserName(uname, sess);
    if(u == null) {
      u = new User();
      u.setUserName(uname);
      sess.save(u);
    }

    u.setAdministrator(admin);
    u.setGameMaster(gm);
    u.setExpertise("expertise");
    u.setDesigner(designer);
    u.setViewOnly(guest);
    u.setAccountDisabled(false);
    u.setEmailConfirmed(true);
    u.setRegisteredInMove(Move.getCurrentMove(sess));
    u.setWelcomeEmailSent(true);
    
    List<Avatar> avLis = sess.createCriteria(Avatar.class).list();
    if(!avLis.isEmpty())
      u.setAvatar(avLis.get(0));
    
    sess.update(u);

    Long uid = u.getId();
    UserPii upii = new UserPii();
    upii.setUserObjectId(uid); 
    VHibPii.save(upii);
    upii.setRealFirstName(uname);
    upii.setRealLastName(uname);
    upii.setPassword(new StrongPasswordEncryptor().encryptPassword(uname));
    VHibPii.setUserPiiEmail(uid, GameLinks.get(sess).getTroubleMailto());

    VHibPii.update(upii);
    return u;
  }
}
   // need to redo for Pii checkRequiredUsers(smgr);
 
/* This stuff is not needed anymore 
    long databaseVersion = game.getVersion();
    if(databaseVersion == MmowgliConstants.DATABASE_VERSION) {
      System.out.println("Database "+MmowgliConstants.DATABASE_VERSION+" matches this codebase");
      smgr.endSession();
      return;
    }
    if(databaseVersion > MmowgliConstants.DATABASE_VERSION) {
      System.out.println("Database "+databaseVersion+" is compatible with this codebase "+MmowgliConstants.DATABASE_VERSION);
      smgr.endSession();
      return;
    }
    
    
    if(databaseVersion == MmowgliConstants.DATABASE_VERSION_BEFORE_EMAILPII_DIGESTS) {
      handleEmailDigests();
    }
    
    // Begin attempt to bring old db's up-to-date
    // If there are any db schema changes, we should error out here.
    // because the following stuff can't address that.
    
    //if(databaseVersion < ApplicationConstants.DATABASE_VERSION_WITH_MOVE_PATCHED_SCORING)
    //  fixScoresByMove(smgr);
       
    //if(databaseVersion < ApplicationConstants.DATABASE_VERSION_WITH_HASHED_PASSWORDS)
    //  hashPasswords(smgr);

    if(databaseVersion < MmowgliConstants.DATABASE_VERSION_WITH_QUICKUSERS)
      checkSetQuickAuthorField(smgr);  // This doesn't really cause performance problems...can keep enabled if we wish

    
  //  fillVipDb();
  //insertPiracyRegexs();
  //addAuthorNamesToCards();
  //setupActionPlanHistories();
  //fillOutAuthorName();
  //testHibernateMaps();
  //oneTimePrintInvitees();
  //oneTimePrintInvitees();
  //oneTimePutQuery2Markers();
  //oneTimeHashEmail();
  //oneTimeEncryptFields();
  //oneTimeChangeEncryptionKey();
  //oneTimeEncryptTeaserEmail();
  //oneTimeCopyEncryptTeaserEmail();
  //oneTimePrintLeaders();
  // LoadPirates.DoIt("/Users/mike/Desktop/piratemapping/ASAM 26 MAY 11/AutoAsam2_opt.kml");
        
    game.setVersion(MmowgliConstants.DATABASE_VERSION);  // mark that we are up-to-date
    smgr.setNeedsCommit(true);
    smgr.endSession();
    }
    */

 /* 
  @SuppressWarnings("unchecked")
  private static void handleEmailDigests()
  {
    StandardStringDigester emailDigester = VHibPii.getDigester(); 
    //new StandardStringDigester();
    //emailDigester.setAlgorithm("SHA-1"); // optionally set the algorithm
    //emailDigester.setIterations(1);  // low iterations are OK, brute force attacks not a problem
    //emailDigester.setSaltGenerator(new ZeroSaltGenerator()); // No salt; OK, because we don't fear brute force attacks 
    
    Session sess = new SingleSessionManager().getSession();
    List<EmailPii> lis = (List<EmailPii>)sess.createCriteria(EmailPii.class).list();
    sess.beginTransaction();
    for(EmailPii epii : lis) {
      epii.setDigest(emailDigester.digest(epii.getAddress().toLowerCase()));
      sess.update(epii);
    }
    sess.getTransaction().commit();
    sess.close();
  }
  */
/*  old scoring logic
  @SuppressWarnings("unchecked")
  private static void fixScoresByMove(SingleSessionManager smgr)
  {
    Session sess = smgr.getSession();
    List<User> users = (List<User>) sess.createCriteria(User.class).list();
    List<Move> moves = (List<Move>) sess.createCriteria(Move.class)
        .add(Restrictions.eq("number",1)).list();
    Move firstM = moves.get(0);
    
    for(User u : users) {
      float ubs = u.getBasicScore();
      float mvs = ScoreManager2.getBasicPointsByMove(u, firstM, sess);
      System.out.println("Basic scores: "+ubs+" "+mvs);

      if(Math.abs(ubs-mvs) > .01f) {
        float mx = Math.max(ubs, mvs);
        ScoreManager2.setBasicPointsByMove(u,firstM,sess,mx);
        u.setBasicScore(mx);  // needed for table sorting
      }
      
      // move inno scores into place
      ScoreManager2.setInnovPointsByMove(u,firstM,sess, u.getInnovationScore());
      sess.update(u);
    }    
    smgr.setNeedsCommit(true);
  }
*/
 

 /* 
  @SuppressWarnings("unchecked")
  private static void hashPasswords(SingleSessionManager smgr)
  {
    System.out.println("Encrypted, but plain text passwords being hashed");
    Session sess = smgr.getSession();
   
    List<User> users = (List<User>) sess.createCriteria(User.class).list();
    StrongPasswordEncryptor encr = new StrongPasswordEncryptor();
    for(User u : users) {
      String decryptedPw = u.getPassword();
      u.setPassword(encr.encryptPassword(decryptedPw));   // hashes plaintext, then writes back and it gets encrypted again through Hibernate
      smgr.setNeedsCommit(true);
    } 
  }
*/  
/*    
  @SuppressWarnings("unchecked")
  private static void checkSetQuickAuthorField(SingleSessionManager smgr)
  {
    Session sess = smgr.getSession();

    List<ActionPlan> aps = (List<ActionPlan>) sess.createCriteria(ActionPlan.class).list();
    boolean needit=false;
    for(ActionPlan ap : aps) {
      if(ap.getAuthors().size() > 0) { // good check
        if(ap.getQuickAuthorList() == null || ap.getQuickAuthorList().length()<=0) {
          needit=true;
          break;
        }
      }
    }
    
    if(needit) {
      for(ActionPlan ap : aps) {
        ap.rebuildQuickAuthorList();
        sess.update(ap);
      }
      smgr.setNeedsCommit(true);
    }
  }
*/  
  /* 
  private static String[] vipLst =
  {
    "email@email.blah", ...
  };
  
  private static String[] vipDomainLst =
  {
    ".mil", 
    "nps.edu",
    ".gov",
    "iftf.org",
    "oneearthfuture.org",
    "nato.int",
    "cimicweb.org",
    "gatech.edu"
  };

  private static void fillVipDb()
  {
    SingleSessionManager smgr = new SingleSessionManager();
    Session sess = smgr.getSession();
    
    String delQ = "DELETE FROM Vip";
    Query dQ = sess.createQuery(delQ);
    dQ.executeUpdate();
    
    for(String s : vipDomainLst) {
      sess.save(new Vip(s,Vip.VipType.DOMAIN));
    }

    for(String s : vipLst) {
      sess.save(new Vip(s,Vip.VipType.EMAIL));
    }
    smgr.setNeedsCommit(true);
    smgr.endSession();    
  }
*/
 /*
  private static void insertPiracyRegexs()
  {
    SingleSessionManager smgr = new SingleSessionManager();
    Session sess = smgr.getSession();
    Game g = (Game)sess.get(Game.class, 1L);
    
    LinkedList<RegexPair> lis = new LinkedList<RegexPair>();
    lis.add(new RegexPair("Game 2011.(\\d) Action Plan (\\d+)",
                          "<a href=\"https://web.mmowgli.nps.edu/piracy/ActionPlanList2011.$1.html#ActionPlan$2\" target=\"apwindow\">$0</a>"));
    g.setLinkRegexs(lis);
    smgr.setNeedsCommit(true);

    smgr.endSession();    
  }
*/
/*
  @SuppressWarnings("unchecked")
  private static void addAuthorNamesToCards()
  {
    SingleSessionManager smgr = new SingleSessionManager();
    Session sess = smgr.getSession();

    // First, read only one card and see if we need to continue
    Criteria cr = sess.createCriteria(Card.class);
    cr.setFirstResult(0);
    cr.setMaxResults(1);

    List<Card> cards = (List<Card>) cr.list();
    String nm = cards.get(0).getAuthorName();
    if (nm == null || nm.length() <= 0) {
      cr = sess.createCriteria(Card.class); // want them all
      cards = (List<Card>) sess.createCriteria(Card.class).list();
      for (Card c : cards) {
        String s = c.getAuthorName();
        if (s == null || s.length() <= 0) {
          c.setAuthorName(c.getAuthor().getUserName());
          smgr.setNeedsCommit(true);
        }
      }
    }
    smgr.endSession();
  }
*/
/*
@SuppressWarnings("unchecked")
private static void fillOutAuthorName()
{
  SingleSessionManager smgr = new SingleSessionManager();
  Session sess = smgr.getSession();
  Criteria crit = sess.createCriteria(Card.class);
  List<Card> lis = (List<Card>)crit.list();
  int count=0;
  for(Card c : lis) {
    c.setAuthorName(c.getAuthor().getUserName());
    count++;
  }
  // Done
  System.out.println(""+count+" cards handled");
  sess.getTransaction().commit();
  sess.close();
}
*/
  
  /*
@SuppressWarnings("unchecked")
private static void setupActionPlanHistories()
{
  System.out.println("In setupActionPlanHistories()");
  SingleSessionManager smgr = new SingleSessionManager();
  Session sess = smgr.getSession();
  Criteria crit = sess.createCriteria(ActionPlan.class);
  List<ActionPlan> aplis = (List<ActionPlan>)crit.list();
  for(ActionPlan c : aplis) {
    c.setTitles(new LinkedList<String>());
    c.setTitleWithHistory(c.getTitle());  // cause a push
    
    c.setSubTitleHistory(new LinkedList<String>());
    c.setSubTitleWithHistory(c.getSubTitle());
    
    c.setHowChangeHistory(new LinkedList<String>());
    c.setHowWillItChangeTextWithHistory(c.getHowWillItChangeText());
    
    c.setHowWorkHistory(new LinkedList<String>());
    c.setHowWillItWorkTextWithHistory(c.getHowWillItWorkText());
    
    c.setWhatIsItHistory(new LinkedList<String>());
    c.setWhatIsItTextWithHistory(c.getWhatIsItText());
    
    c.setWhatTakeHistory(new LinkedList<String>());
    c.setWhatWillItTakeTextWithHistory(c.getWhatWillItTakeText());
     
    sess.update(c);
  }
  sess.getTransaction().commit();
  sess.close(); 
}
*/
/* 
private void testHibernateMaps()
{
  SingleSessionManager smgr = new SingleSessionManager();
  Session sess = smgr.getSession();
  
  User u0 = (User)sess.get(User.class, 950L);
  ActionPlan ap0 = (ActionPlan)sess.get(ActionPlan.class,1L);
  
//  ap0.getAuthors().add(u0);
//  sess.update(ap0);
//  ap0.getAuthors().add(u0);
//  sess.update(ap0);
//  smgr.endSession();   
//  return;
  
  u0.getActionPlanCommentScores().put(ap0, 12.0d);
  sess.update(u0);
  u0.getActionPlanCommentScores().put(ap0, 13.0d);
  sess.update(u0);

  smgr.endSession();
  return;
  slash*
  User u = User.get(1L);
  User u2 = User.get(2L);
  u.getImFollowing().add(u2);
  sess.update(u);
  u.getImFollowing().add(u2);
  sess.update(u);
  
  Set<ActionPlan> set = u.getActionPlansInvited();
  ActionPlan ap = ActionPlan.get(1L);
  if(set == null)
    u.setActionPlansInvited(set = new HashSet<ActionPlan>(1)); 
  //if(!CreateActionPlanPanel.apContainsByIds(set, ap)) {
    set.add(ap);
    User.update(u);
    set.add(ap);
    User.update(u);

  
  Map<Long, Float> map = u.getInnovationByMove();
  map.put(1L, 5.0f);
  sess.update(u);
  map.put(1L, 6.0f);
  sess.update(u);
  *slash
}
*/
/*
private void oneTimePrintInvitees()
{
  Session sess = HibernateContainers.getSession();
  Criteria crit = sess.createCriteria(Query2.class)
                  .addOrder(Order.asc("date"))
                  .setFirstResult(7500)
                  .setMaxResults(7500);
  
 //Criterion restr = RegistrationPagePopupFirst.getIntervalRestriction();
 // if(restr != null)
 //   crit.add(restr);
  
  @SuppressWarnings("unchecked")
  List<Query2> lis = (List<Query2>)crit.list();
  System.out.println("\n\n\n========== begin invite list ==========");
  int i = 7501;
  SimpleDateFormat dateFmttr = new SimpleDateFormat("yyyy-MM-dd'/'HH:mm:ss.SSS");
  for(Query2 q : lis)
    System.out.println(""+i++ +"\t"+q.getEmail()+"\t"+dateFmttr.format(q.getDate()));
  System.out.println("========== end invite list ==========");
                
}
*/
 /**
  * This is a ONE TIME AND ONE TIME ONLY operation that changes the encryption key
  * of several fields. <p>
  * 
  *  The existing fields were encrypted with a key that is publicly visible, because
  *  it was checked into subversion. We need to change to another encyrption key.
  *  So we read all the data from the file, (which decrypts the data using the old
  *  key), re-encrypts the data with the new key. This method may be used any time
  *  you want to re-key the encrypted fields of the databaase. The steps involved are:
  *  
  *  0) BACK UP THE DATABASE! You should run this on a development box with the
  *  database you want to convert loaded on your local mysql instance. This should
  *  never touch production hardware. The operation will run once on the development
  *  database box, then you can dump it and move it to production hardware.
  *  
  *  1) Create a new encryptor and encryptor name in ApplicationServletContext. In
  *  this particular case, I've created a new encryptor named "propertiesFileHibernateStringEncryptor",
  *  which will replace the encryptor "strongHibernateStringEncryptor". The two are
  *  identical except for a different key.
  *  
  *  2) Comment out the @Type(type="encryptedString") in User for getRealFirstName(), 
  *  getRealLastName(), getFacebookId(), getTwitterId(), and getLinkedInId() in User.java, and 
  *  getAddress() in Email.java. This will cause the field to be returned in encrypted form 
  *  (using the old key) from the database.
  *  
  *  3) Start and run the application with the method below activated and called from 
  *  init() in this class. This will decrypt the field with the old key, re-encrypt it
  *  with the new key, and write it back to the database.
  *  
  *  4) Wait for the application to finish initializing, ie for catalina.out to show
  *  the app is finished coming up, and that the fields described below have been
  *  changed. Don't write anything to the database after it comes up!
  *  
  *  5) Stop the application, remove the call to this method in init().
  *  
  *  6) Change the following in User.java and Email.java:
  *  
  *  @TypeDef(
*    name="encryptedString", 
*    typeClass=EncryptedStringType.class, 
*    parameters={@Parameter(name="encryptorRegisteredName",
*                           value="propertiesFileHibernateStringEncryptor")}
*
*    That is, change the "value" property to point to the new encryptor created
*    in step 1.
*    
  *  7) re-enable the @Type methods you commented out in step 2.
  *  
  *  8) Recompile, redeploy.
  *
  * DMcG
  */
/*  
 private void oneTimeHashEmail()
 {
   // A digest generator. THis should NOT be used to digest passwords; this should be used for emails only.
   StandardStringDigester emailDigester = makeDigester();
  
   System.out.println("--------------------");
   System.out.println("Hashing Query2 email field into Query2 hash column");
        
   Collection<?> coll = Query2.getContainer().getItemIds();
   Iterator<?> itr = coll.iterator();
   
   while(itr.hasNext()) 
   {
      Object obj = itr.next();
      Query2 qry = Query2.get(obj);
      //System.out.print("got email: " + qry.getEmail() );
      String email = qry.getEmail().toLowerCase();        
      String s = emailDigester.digest(email);
      //System.out.println("Hash = "+s+" len = "+s.length());        
      qry.setDigest(s);
      Query2.update(qry);
    }
   HibernateContainers.closeSession();
   System.out.println("Finished hashing of query email");
   System.out.println("--------------------");
 }
*/
/*  
private StandardStringDigester makeDigester()
{
  // A digest generator. THis should NOT be used to digest passwords; this should be used for emails only.
  StandardStringDigester emailDigester = new StandardStringDigester();
  emailDigester.setAlgorithm("SHA-1"); // optionally set the algorithm
  emailDigester.setIterations(1);  // low iterations are OK, brute force attacks not a problem
  emailDigester.setSaltGenerator(new ZeroSaltGenerator()); // No salt; OK, because we don't fear brute force attacks
  return emailDigester;
}
*/
/*  
private void oneTimePutQuery2Markers()
{
  String junkEmail = "placeholder@nowhere.xxx";
  StandardStringDigester emailDigester = makeDigester();
  if(!QUERY_MARKER_FIELD.equals("name"))
    throw new RuntimeException("Unsupported parameter detected in ApplicationEntryPoint");
  
  Query2 q2 = new Query2();
  q2.setDate(new Date());  // patched externally
  q2.setEmail(junkEmail);
  q2.setName(QUERY_START_MARKER);
  q2.setDigest(emailDigester.digest(junkEmail));
  Query2.save(q2);
  
  q2 = new Query2();
  q2.setDate(new Date());  // patched externally
  q2.setEmail(junkEmail);
  q2.setName(QUERY_END_MARKER);
  q2.setDigest(emailDigester.digest(junkEmail));
  Query2.save(q2);     
}
*/
/*
private void oneTimeCopyEncryptTeaserEmail()
{
  System.out.println("--------------------");
  System.out.println("Encrypting Query email field into Query2 table");
       
  Collection<?> coll = Query.getContainer().getItemIds();
  Iterator<?> itr = coll.iterator();
  
  while(itr.hasNext()) 
  {
     Object obj = itr.next();
     Query qry = Query.get(obj);
     System.out.print("Encrypting email: " + qry.getEmail() );
     Query2 q2 = new Query2();
     q2.setBackground(qry.getBackground());
     q2.setDate(qry.getDate());
     q2.setEmail(qry.getEmail());
     q2.setName(q2.getName());
     Query2.save(q2);
   }
  HibernateContainers.closeSession();
  System.out.println("Finished encryption of query email");
  System.out.println("--------------------");

}
*/
/*
private void oneTimeEncryptTeaserEmail()
{
  System.out.println("--------------------");
  System.out.println("Encrypting Query email field");
  Session session = HibernateContainers.getSession();
  
  // Retrieve all the users in the database. Because the encryptor was turned off in step 2 above, the
  // retrieved data will still be in encrypted form (with the old key). 
  Criteria allQueries = session.createCriteria(Query.class);
   
  HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
  PBEStringEncryptor newEncryptor = registry.getPBEStringEncryptor("propertiesFileHibernateStringEncryptor"); // The new encryptor, which we'll use to encrypt
  
  Collection<?> coll = Query.getContainer().getItemIds();
  Iterator<?> itr = coll.iterator();
  
  // Loop through all the users decrypting and encrypting
  while(itr.hasNext()) 
  {
     Object obj = itr.next();
     Query qry = Query.get(obj);
     System.out.print("Encrypting email: " + qry.getEmail() );
    
     // Swap the encryption keys: decrypt the encrypted text, then re-encrypt with the new key.
     String encr = newEncryptor.encrypt(qry.getEmail());
     qry.setEmail( encr );
     System.out.println(" to "+encr);
     Query.update(qry);
   }
   
   HibernateContainers.closeSession();
   System.out.println("Finished encryption of query email");
   System.out.println("--------------------");
  
}
*/
/*
@SuppressWarnings("unused")
private static void oneTimeChangeEncryptionKey()
{
  System.out.println("--------------------");
  System.out.println("Changing encryption key in User, Email");
  SingleSessionManager mgr = new SingleSessionManager();
  Session session = mgr.getSession();
  session.beginTransaction();
  mgr.setNeedsCommit(true);
  
  // Retrieve all the users in the database. Because the encryptor was turned off in step 2 above, the
  // retrieved data will still be in encrypted form (with the old key). 
  Criteria allUsers = session.createCriteria(User.class);
   
  HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
  PBEStringEncryptor oldEncryptor = registry.getPBEStringEncryptor("strongHibernateStringEncryptor");         // The old encryptor, which we'll use to decrypt
  PBEStringEncryptor newEncryptor = registry.getPBEStringEncryptor("propertiesFileHibernateStringEncryptor"); // The new encryptor, which we'll use to encrypt
  
  Collection<?> coll = User.getContainer().getItemIds();
  Iterator<?> itr = coll.iterator();
  
  // Loop through all the users decrypting and encrypting
  while(itr.hasNext()) 
  {
     Object obj = itr.next();
     User aUser = (User)session.get(User.class, (Serializable)obj);
     
     System.out.print("Changing key for UserName: " + aUser.getUserName() );
    
     // This should be in plaintext. If not, there's  a problem!
     System.out.println("    " + oldEncryptor.decrypt( aUser.getRealFirstName() ) );
     
     // Swap the encryption keys: decrypt the encrypted text, then re-encrypt with the new key.
     aUser.setRealFirstName( newEncryptor.encrypt(oldEncryptor.decrypt(aUser.getRealFirstName()) ));
     aUser.setRealLastName(  newEncryptor.encrypt(oldEncryptor.decrypt(aUser.getRealLastName() ) ));
     aUser.setTwitterId( newEncryptor.encrypt(oldEncryptor.decrypt(aUser.getTwitterId() ) ) );
     aUser.setFacebookId(  newEncryptor.encrypt(oldEncryptor.decrypt(aUser.getFacebookId()) ) );
     aUser.setLinkedInId(  newEncryptor.encrypt(oldEncryptor.decrypt(aUser.getLinkedInId()) ) );
     aUser.setPassword(  newEncryptor.encrypt(oldEncryptor.decrypt(aUser.getPassword()) ) );
     
     
     List emails = aUser.getEmailAddresses();
     for(int jdx = 0; jdx < emails.size(); jdx++)
     {
       Email anEmail = (Email)emails.get(jdx);
       anEmail.setAddress( newEncryptor.encrypt(oldEncryptor.decrypt(anEmail.getAddress())) );
       Email.update(anEmail);
       session.update(anEmail);
     }
     session.update(aUser);
   }
   
   mgr.endSession();
   System.out.println("Finished upgrade of encrypted field key");
   System.out.println("--------------------");  
}
*/
/**
 * This is a ONE TIME AND ONE TIME ONLY operation that encrypts several fields. 
 * 
 * Hibernate has some annotations that automatically decrypt a field when reading
 * from the db, and encrypt it when writing back to the db. But the fields have
 * to be initially changed from plaintext to encrypted the first time around; otherwise
 * hibernate will try to decrypt plain text. This does the first-time encryption
 * of the existing fields in the database. You should bring up the application
 * opening page, watch for the debugging info below, then quit the browser
 * before doing anything. Once the fields have been encrypted turn on the 
 * annotations for encryption in the User and Email classes, restart, and
 * you're off.
 * 
 */
/*
private void oneTimeEncryptFields()
{
  System.out.println("--------------------");
  Session session = HibernateContainers.getSession();
  Criteria allUsers = session.createCriteria(User.class);
  
  HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
  PBEStringEncryptor encryptor = registry.getPBEStringEncryptor("strongHibernateStringEncryptor");
  Collection<?> coll = User.getContainer().getItemIds();
  Iterator<?> itr = coll.iterator();
  while(itr.hasNext()) {
    Object obj = itr.next();
    User aUser = User.get(obj);
//  }
//  List users = allUsers.list();
//  System.out.println("Users=" + users.size());
//  for(int idx = 0; idx < users.size(); idx++)
//  {
//    User aUser = (User) users.get(idx);
    System.out.println("User: " + aUser.getUserName() + " Twitter: " + aUser.getTwitterId());
    System.out.println("    " + encryptor.encrypt(aUser.getTwitterId()));
    aUser.setTwitterId(encryptor.encrypt(aUser.getTwitterId()));
    aUser.setFacebookId(encryptor.encrypt(aUser.getFacebookId()));
    aUser.setLinkedInId(encryptor.encrypt(aUser.getLinkedInId()));
    aUser.setPassword(encryptor.encrypt(aUser.getPassword()));
    
    List emails = aUser.getEmailAddresses();
    for(int jdx = 0; jdx < emails.size(); jdx++)
    {
      Email anEmail = (Email)emails.get(jdx);
      anEmail.setAddress(encryptor.encrypt(anEmail.getAddress()));
    }
  }
  
  HibernateContainers.closeSession();
  System.out.println("--------------------");

}
*/
/*
private void oneTimePrintLeaders()
{
  // to get 150 leaders in both scores 
  Session sess = HibernateContainers.getSession();
  List<User> users = (List<User>) sess.createCriteria(User.class).
  addOrder(Order.desc("basicScore")).
  list();
  System.out.println("By basic score");
  System.out.println("--------------");
  int i=0;
  for(User u : users) {
    if(i++>150)
      break;
    String email = u.getEmailAddresses().get(0).getAddress();
    String uname = u.getUserName();
    Float f = u.getBasicScore();
    Float insc = u.getInnovationScore();
    System.out.println((u.isGameMaster()?"GM":" ")+"\t"+uname+"\t"+email+"\t"+f.intValue()+"\t"+insc.intValue());
  }
  
  System.out.println("By innov. score");
  System.out.println("----------------");
  users = (List<User>) sess.createCriteria(User.class).
  addOrder(Order.desc("innovationScore")).
  list();
  i=0;
  for(User u : users) {
    if(i++>150)
      break;
    String email = u.getEmailAddresses().get(0).getAddress();
    String uname = u.getUserName();
    Float f = u.getBasicScore();
    Float insc = u.getInnovationScore();
    System.out.println((u.isGameMaster()?"GM":" ")+"\t"+uname+"\t"+email+"\t"+f.intValue()+"\t"+insc.intValue());
  }     
}
*/

