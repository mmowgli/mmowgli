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

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.util.*;

import org.getopt.luke.HighFreqTerms;
import org.getopt.luke.TermInfo;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.messaging.MMessage;

/**
 * SearchManager.java Created on May 18, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */


/** Sample usage:
    Collection<?> result = SearchManager.searchAll(new String[]{"Arlington","Minnesota"});
    System.out.println("Got "+result.size());   
    Collection<?> resultc = SearchManager.searchCards(new String[]{"Arlington","Minnesota"});
    System.out.println("Got "+resultc.size() + " cards");     
    Collection<?> resultu = SearchManager.searchUsers(new String[]{"Arlington","Minnesota"});
    System.out.println("Got "+resultu.size() + " users");   
 */

public class SearchManager
{
  public static Collection<?> searchAll(String[] terms)
  {
    Pkt pkt = _preAmble();
    
    Collection<Card> retn = _searchCards(terms, pkt);
    Collection<User> retu = _searchUsers(terms, pkt);
    Collection<ActionPlan> retap = _searchActionPlans(terms, pkt);
    Vector<Object> v = new Vector<Object>();
    v.addAll(retu); // first u
    v.addAll(retap);// then ap
    v.addAll(retn); // then cards
    
    _postAmble(pkt);
    
    return v;
  }
  
  public static TermInfo[] getHighFrequencyTerms()
  {
    /*
    TermInfo[] ta = HighFreqTerms.getHighFreqTerms();
    for(TermInfo ti : ta)
      System.out.println("TermInfo: "+ti.term.toString()+" "+ti.docFreq);
    return ta;
    */
    return HighFreqTerms.getHighFreqTerms();
  }
  
  public static Collection<?> searchCards(String[] terms)
  {
    Pkt pkt = _preAmble();
    Collection<?> retn = _searchCards(terms, pkt);
    _postAmble(pkt);
    return retn;
  }

  public static Collection<?> searchUsers(String[] terms)
  {
    Pkt pkt = _preAmble();
    Collection<?> retn = _searchUsers(terms, pkt);
    _postAmble(pkt);
    return retn;
  }
  
  public static Collection<?> searchActionPlans(String[] terms)
  {
    Pkt pkt = _preAmble();
    Collection<?> retn = _searchActionPlans(terms, pkt);
    _postAmble(pkt);
    return retn;   
  }
  
  @SuppressWarnings("unchecked")
  private static Collection<Card> _searchCards(String[] terms, Pkt pkt)
  {
    return (Collection<Card>) _searchClass(terms, Card.class, Card.CARD_SEARCH_FIELDS, pkt.ftSess);
  }

  @SuppressWarnings("unchecked")
  private static Collection<User> _searchUsers(String[] terms, Pkt pkt)
  {
    return (Collection<User>) _searchClass(terms, User.class, User.USER_SEARCH_FIELDS, pkt.ftSess);
  }

  @SuppressWarnings("unchecked")
  private static Collection<ActionPlan> _searchActionPlans(String[] terms, Pkt pkt)
  {
    return (Collection<ActionPlan>) _searchClass(terms, ActionPlan.class, ActionPlan.ACTIONPLAN_SEARCH_FIELDS, pkt.ftSess);
  }

  private static Collection<?> _searchClass(String[] terms, Class<?> cls, String[] fields, FullTextSession srSess)
  {
    StringBuilder allTerms = new StringBuilder();
    for (String s : terms) {
      allTerms.append(s);
      allTerms.append(" ");
    }
    String termString = allTerms.toString().trim();

    try {
      QueryBuilder qb = srSess.getSearchFactory().buildQueryBuilder().forEntity(cls).get();
      org.apache.lucene.search.Query query = qb.keyword().onFields(fields).matching(termString).createQuery();
      org.hibernate.Query hQuery = srSess.createFullTextQuery(query, cls); // Wrap lucene query in hibernate query
      return hQuery.list(); // execute search
    }
    catch (Throwable t) {
      System.err.println("Caught exception when searching: "+t.getClass().getSimpleName()+" "+t.getLocalizedMessage()+" search terms: "+termString);
      return new ArrayList<Object>(); // empty
    }
  }
  
  
  /** Messages from the inter-session message bus arrive here, sent from ApplicationMaster. Returns true if the message
   * needs to be resent.
   */
  public static boolean indexObjectFromMessageTL(char messageType, String message)
  {
	  // Message type tags are defined in ApplicationConstants. C=new card, U = new user,
	  // u = modified user. Right now we index cards and users.
	  switch(messageType)
	  {
	  	case NEW_CARD: //C':
	  		Long cardId = MMessage.MMParse(messageType,message).id;
	  		Card aCard = Card.getTL(cardId);
		    SearchManager.indexHibernateObject(aCard, HSess.get());
		    break;
		 
	  	case NEW_USER: //'U':
	  	case UPDATED_USER: //'u':
	  	  Long userId = MMessage.MMParse(messageType,message).id; 
	  		User aUser = User.getTL(userId);
	  		SearchManager.indexHibernateObject(aUser, HSess.get());
	  		break;
	  		
	  	case NEW_ACTIONPLAN:
	  	case UPDATED_ACTIONPLAN:
	  	  Long apId = MMessage.MMParse(messageType,message).id; 
	  	  ActionPlan ap = ActionPlan.getTL(apId);
	  	  SearchManager.indexHibernateObject(ap, HSess.get());
	  	  break;
  	
		default:
			break;
	  }
	  
	  return false;
  }

  /**
   * Given a new object that has the hibernate search annotation "@Indexed" present,
   * add it to the existing index. The index is initially created at startup, and
   * this incrementally adds a new object to the existing index. If the object does
   * not implement the annotation, or is null, the exception is swallowed and nothing
   * happens, other than an error message. The indexing operation is pretty fast, a
   * ms or less, for a single object.
   * @param newObject
   */
  public static void indexHibernateObject(Object newObject, Session session)
  {
	  // Note that we do not want to use the default sessions associated with
	  // Pre and Post here.
	  try
	  {		  
		  FullTextSession srSess = Search.getFullTextSession(session);
		  //Transaction srTx = srSess.beginTransaction();  //don't because it appears to be handled differently in Lucene TransactionalWorker, and transaction gets auto committed anyway
		  srSess.index(newObject);
		  //srTx.commit();                                 // ditto
	  }
	  catch(java.lang.IllegalArgumentException badArg)
	  {
		  // This is thrown if the object passed in is null, or does not use the
		  // @Indexed hibernate search annotation. As of this writing, Card and User use the 
		  // @Indexed annotation.
		  System.err.println("Object to be Lucene-indexed does not implement the @Indexed annotation, ignored");
	  }
	  catch(Exception e)
	  {
		  System.out.println("Exception: " + e);
		  System.out.println("Unable to Lucene-index object, ignoring");
	  }
	  
  }
  private static Pkt _preAmble()
  {
    Session sess = VHib.getSessionFactory().openSession();
    FullTextSession srSess = Search.getFullTextSession(sess);
    Transaction srTx = srSess.beginTransaction();
    srTx.setTimeout(HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS);
    return new Pkt(sess, srSess, srTx);
  }

  private static void _postAmble(Pkt pkt)
  {
    pkt.trans.commit();
    pkt.sess.close();
  }

  private static class Pkt
  {
    public Session sess;
    public FullTextSession ftSess;
    public Transaction trans;

    public Pkt(Session sess, FullTextSession ftSess, Transaction trans)
    {
      this.sess = sess;
      this.ftSess = ftSess;
      this.trans = trans;
    }
  }
  /*
   * Original test code
   *    //String[]{"Minnesota","Arlington"});
  // First try for search
  // Build index
  // This would be done on application load
  
  Session sess = HibernateContainers.sessionFactory.openSession();
  // Now here's how we search
  FullTextSession srSess = Search.getFullTextSession(sess);
  Transaction srTx = srSess.beginTransaction(); todo HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS
  QueryBuilder qb = srSess.getSearchFactory().buildQueryBuilder().forEntity(Card.class).get();
  
  org.apache.lucene.search.Query query =
    qb.keyword().onField("text").matching("Minnesota").createQuery();
  
  // Wrap lucene query in hibernate query
  org.hibernate.Query hQuery = srSess.createFullTextQuery(query, Card.class);
  // execute search
  List<?> result = hQuery.list();
  
  qb = srSess.getSearchFactory().buildQueryBuilder().forEntity(User.class).get();
  query = qb.keyword().onField("location").matching("Arlington").createQuery();
  hQuery = srSess.createFullTextQuery(query, User.class);
  result.addAll(hQuery.list());
  
  System.out.println("Got "+result.size());
  srTx.commit();
  sess.close();
  return result;
*/
}
