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

package edu.nps.moves.mmowgli.signupServer;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jasypt.digest.StandardStringDigester;

import edu.nps.moves.mmowgli.db.pii.Query2Pii;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
import static edu.nps.moves.mmowgli.MmowgliConstants.*;

/**
 * SignupHandler.java
 * Created on Dec 21, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class SignupHandler
{
  static private StandardStringDigester emailDigester = VHibPii.getDigester(); //PiiHibernate.getDigester();
 /* static
  {
    emailDigester = new StandardStringDigester();
    emailDigester.setAlgorithm("SHA-1"); // optionally set the algorithm
    emailDigester.setIterations(1);  // low iterations are OK, brute force attacks not a problem
    emailDigester.setSaltGenerator(new ZeroSaltGenerator()); // No salt; OK, because we don't fear brute force attacks   
  }
  */
/*  
  public static void handle(String email, String interest)
  {
    SingleSessionManager ssm = new SingleSessionManager();
    Session sess = ssm.getSession();
    sess.beginTransaction();
    
    Query2 q = new Query2();
    q.setEmail(email);
    if(interest != null && interest.length()>255) {
      interest = interest.substring(0, 255);
    }
    q.setInterest(interest);
    q.setDate(new Date());
    q.setDigest(emailDigester.digest(email.toLowerCase()));
    
    sess.save(q);
    ssm.setNeedsCommit(true);
    ssm.endSession();
  }
  */
  public static void handle(String email, String interest)
  {
    Session sess = VHibPii.getASession();
    sess.beginTransaction();
    
    Query2Pii q = new Query2Pii();
    q.setEmail(email);
    if(interest != null && interest.length()>255) {
      interest = interest.substring(0, 255);
    }
    q.setInterest(interest);
    q.setDate(new Date());
    q.setDigest(emailDigester.digest(email.toLowerCase()));
    
    sess.save(q);
    sess.getTransaction().commit();
    sess.close();
  }
 
  @SuppressWarnings("unchecked")
  public static Query2Pii getQuery2WithEmail(String email)
  {
    List<Query2Pii> tlis=null;
    Session sess=null;
    try {
      sess = VHibPii.getASession();
      String checkDigest = emailDigester.digest(email.toLowerCase());
    
      Criteria crit = sess.createCriteria(Query2Pii.class)
                    .add(Restrictions.eq("digest", checkDigest));
    
      tlis = (List<Query2Pii>)crit.list(); 
    }
    catch(Exception ex) {
      MSysOut.println(ERROR_LOGS,"***Exception in SignupHandler: "+ex.getClass().getSimpleName()+": "+ex.getLocalizedMessage());
    }
    if(sess != null)
      sess.close();
    
    if(tlis == null || tlis.size()<=0)
      return null;
    
    return tlis.get(0);    
  }
}
