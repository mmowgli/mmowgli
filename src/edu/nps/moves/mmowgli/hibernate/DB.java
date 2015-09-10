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

package edu.nps.moves.mmowgli.hibernate;

import static edu.nps.moves.mmowgli.MmowgliConstants.ERROR_LOGS;
import static edu.nps.moves.mmowgli.MmowgliConstants.HIBERNATE_LOGS;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.hibernate.*;
import org.hibernate.criterion.*;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * DB.java created on Jan 20, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class DB
{
  static int RETRY_COUNT = 20;
  static long RETRY_SLEEP = 500;
  
  public static void updateTL(Object obj)
  {
    HSessionAccessor.updateTL(obj);
  }
  
  public static void saveTL(Object obj)
  {
    HSessionAccessor.saveTL(obj);
  }
  
  public static <T> T mergeTL(T obj)
  {
    return HSessionAccessor.mergeTL(obj);
  }
  
  public static <T> T merge(T obj, Session sess)
  {
    return HSessionAccessor.merge(obj, sess);
  }
  
  public static <T> T getTL(Class<T> cls, Object id)
  {
    return get(cls,id,HSess.get());
  }
  public static <T> T getLockedTL(Class<T> cls, Object id)
  {
    return getLocked(cls,id,HSess.get());
  }
  
  public static <T> T get(Class<T> cls, Object id, Session sess)
  {
    return getCommon(cls,id,null,sess);
  }
  
  private static LockOptions getLock;
  static {
    getLock = new LockOptions(LockMode.READ);
    getLock.setTimeOut(5000); // 5 seconds
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T getLocked(Class<T> cls, Object id, Session sess)
  {
    return (T)sess.get(cls, (Serializable)id, getLock);
  }
    
  public static <T> T getRevisionTL(Class<T> cls, Object id, Long revision)
  {
    return getCommon(cls,id,revision,HSess.get());
  }
  
  private static <T> T getCommon(Class<T> cls, Object id, Long revision, Session sess)
  {
    T  obj = getRetry(cls, id, revision, sess);
  /* test (2 things: 1. might this cache an old version if update in progress? 2. if user, does pii get, which
   * hits synchronized decryption code...very slow
    if(obj != null)
       MCacheManager.instance().putObject(obj);
  */
    return obj;    
  }
  
  public static void deleteUserTL(Object id)
  {
    HSessionAccessor.deleteUserTL(id);
  }
  
  public static void deleteTL(Object obj)
  {
    HSessionAccessor.deleteTL(obj);
  }
  
  private static long getRevision(Object obj)
  {
    long ret = Long.MIN_VALUE;
    
    Class<?> cls = obj.getClass();
    try {
      Method m = cls.getDeclaredMethod("getRevision", (Class<?>[])null);
      ret = (Long)m.invoke(obj, (Object[])null);
    }
    catch(NoSuchMethodException|InvocationTargetException|IllegalAccessException ex) {
      MSysOut.println(ERROR_LOGS,"Reflection error in DB.getRevision("+obj.getClass().getSimpleName()+"): "+ex.getLocalizedMessage());
    }
    return ret;
  }
    
  public static <T> T getSingleTL(Class<T> cls, Criterion... restrictions)
  {
    return HSessionAccessor.getSingleTL(cls, restrictions);
  }
  
  public static <T> T getSingle(Class<T> cls, Session sess, Criterion... restrictions)
  {
    return HSessionAccessor.getSingle(cls, sess, restrictions);
  }
  
  public static <T> List<T> getMultiple(Class<T> cls, Session sess, Criterion...restrictions)
  {
    return HSessionAccessor.getMultiple(cls, sess, restrictions);
  }
  
  public static <T> List<T> getMultipleTL(Class<T> cls, Order order)
  {
    return HSessionAccessor.getMultipleTL(cls, order);
  }
  
  public static <T> HbnContainer<T> getContainer(Class<T> cls)
  {
    return HSessionAccessor.getContainer(cls);
  }

  @SuppressWarnings("unchecked")
  public static <T> T getRetry(Class<T> cls, Object id, Long revision, Session sess)
  //---------------------------------------------------------------------------------
  {
    Session ss = VHib.openSession();
    for (int i = 0; i < RETRY_COUNT; i++) {
      //MSysOut.println(HIBERNATE_LOGS, ""+i+" Top of DB.getRetry() loop class: "+cls.getSimpleName()+" id; "+id.toString()+" rev: "+revision);
      Criteria crit = ss.createCriteria(cls).add(Restrictions.eq("id", id));
      List<T> list = crit.list();

      if(list.size()>0){
        if(revision == null || getRevision(list.get(0)) >= revision) {
          ss.close();
          if (i > 0)
            MSysOut.println(HIBERNATE_LOGS,"DB.getRetry() delayed versioned fetch of " + cls.getSimpleName() + " id: "+id.toString()+" got it on try " + (i+1));
          return (T)sess.merge(list.get(0));
        }
        else
          ss.evict(list.get(0));  // session still open
      }
      else {
        ss.close();
        ss = VHib.openSession(); //for retry
      }
      sleep(RETRY_SLEEP);
    }
    ss.close();
    long secs = (RETRY_SLEEP * RETRY_COUNT)/1000l;
    
    MSysOut.println(ERROR_LOGS,"DB.getRetry() couldn't get " + cls.getSimpleName() + " " + id + " rev: "+revision+" in "+secs+" seconds");// give up
    MSysOut.println(ERROR_LOGS,"Stack trace follows (no Exception generated)");
    MSysOut.dumpStack(ERROR_LOGS);
    return null;
  }
   
  private static void sleep(long msec)
  {
    try {
      Thread.sleep(msec);
    }
    catch (InterruptedException ex) {
    }
  }

}
