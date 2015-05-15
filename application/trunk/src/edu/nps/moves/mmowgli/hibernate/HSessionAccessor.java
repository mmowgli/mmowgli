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

import static edu.nps.moves.mmowgli.MmowgliConstants.HIBERNATE_SESSION_LOGS;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
/**
 * HSessionAccessor.java Created on Jan 20, 2015
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 * 
 * The purpose here is to put all accesses of the DB through Hibernate sessions here, as a series of static methods. This way, if there is a standard
 * protocol for transaction rollback on error, etc., it can be used throughout the app.
 * 
 * Methods are package-local, intended for use only by Vaading hibernate code, specifically DB
 * 
 */
@SuppressWarnings("unchecked")
public class HSessionAccessor
{
  static int RETRY_COUNT = 10;
  static long RETRY_SLEEP = 500;
  
  static void update(Object obj, Session sess)
  //------------------------------------------
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session update of "+obj.getClass().getSimpleName()+" "+obj.hashCode()+" session: "+sess.hashCode());
    sess.update(obj);
  }
  
  static void updateTL(Object obj)
  //------------------------------
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session update of "+obj.getClass().getSimpleName()+" "+obj.hashCode()+" session: "+HSess.get().hashCode());
    HSess.get().update(obj);
  }
  
  static void deleteTL(Object obj)
  //------------------------------
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session delete of "+obj.getClass().getSimpleName()+" "+obj.hashCode()+" session: "+HSess.get().hashCode());
    HSess.get().delete(obj);
  }
  
  static void deleteUserTL(Object id)
  {
    Object instance = HSess.get().load(User.class, (Serializable)id);
    if(instance != null)
      HSess.get().delete(instance);
  }
  
  static void save(Object obj, Session sess)
  //----------------------------------------
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session save of "+obj.getClass().getSimpleName()+" "+obj.hashCode()+" session: "+sess.hashCode());
    sess.save(obj);
  }
  
  static void saveTL(Object obj)
  //----------------------------
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session save of "+obj.getClass().getSimpleName()+" "+obj.hashCode()+" session: "+HSess.get().hashCode());
    HSess.get().save(obj);
  }
  
  static <T> T merge(T obj, Session sess)
  //-------------------------------------
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session merge of "+obj.getClass().getSimpleName()+" "+obj.hashCode()+" session: "+sess.hashCode());
    return (T) sess.merge(obj);
  }
  
  static <T> T mergeTL(T obj)
  //-------------------------
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session merge of "+obj.getClass().getSimpleName()+" "+obj.hashCode()+" session: "+HSess.get().hashCode());
    return (T) HSess.get().merge(obj);
  }
  
  static <T> T get(Class<T> cls, Object id, Session sess)
  //-----------------------------------------------------
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session get of "+cls.getSimpleName()+" "+id+" session: "+sess.hashCode());
    return (T) sess.get(cls, (Serializable)id);
  }

  static <T> T getTL(Class<T> cls, Object id)
  //-----------------------------------------
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session get of "+cls.getSimpleName()+" "+id+" session: "+HSess.get().hashCode());
    return HSessionAccessor.get(cls, id, HSess.get());
  }

  static <T> T getRevision(Class<T> cls, Object id, long revision, Session sess)
  //--------------------------------------------------------------------------
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session getRevision of "+cls.getSimpleName()+" "+id+" revision: "+revision+" session: "+sess.hashCode());
    List<T> lis = (List<T>)sess.createCriteria(cls)
                     .add(Restrictions.eq("id", id))
                     .add(Restrictions.gt("revision", revision)).list();
    if(lis.size()>0)
      return lis.get(0);
    return null;
  }
  
  static <T> T getRevisionTL(Class<T> cls, Object id, long revision)
  //------------------------------------------------------------------
  {
    return HSessionAccessor.getRevision(cls, id, revision, HSess.get());
  }
    
  public static <T> T getSingleTL(Class<T> cls, Criterion... restrictions)
  {
    return getSingle(cls,HSess.get(),restrictions);
  }
  
  public static <T> T getSingle(Class<T> cls, Session sess, Criterion... restrictions)
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session getSingle of "+cls.getSimpleName()+" session: "+sess.hashCode());
    Criteria criteria = sess.createCriteria(cls);
    for(Criterion c : restrictions)
      criteria.add(c);
    
    List<T> results = criteria.list();
    
    if (results.size() > 0)
      return results.get(0);
    return null;    
  }
  
  public static <T> List<T> getMultiple(Class<T> cls, Session sess, Criterion...restrictions)
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session getMultiple of "+" session: "+sess.hashCode());
    Criteria criteria = sess.createCriteria(cls);
    if(restrictions != null)
      for(Criterion c : restrictions)
        criteria.add(c);
    
    return criteria.list();
  }
  
  public static <T> List<T> getMultipleTL(Class<T> cls, Order order)
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session getRevision of "+cls.getSimpleName()+" session: "+HSess.get().hashCode());
    Criteria criteria = HSess.get().createCriteria(cls);
    if(order != null)
      criteria.addOrder(order);
    
    return criteria.list();
  }

  public static <T> HbnContainer<T> getContainer(Class<T> cls)
  {
    MSysOut.println(HIBERNATE_SESSION_LOGS, "Session getContainer of "+cls.getSimpleName());
    return (HbnContainer<T>) HSess.getContainer(cls);
  }

}
