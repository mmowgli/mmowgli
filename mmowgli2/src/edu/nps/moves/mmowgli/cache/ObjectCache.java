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

import java.util.*;

/**
 * Saves objects in a cache. Key is a UUID (in string format), value is an inner
 * class with a date stamp, so we can time out entries.
 * <p>
 *
 * Note that multiple threads may access this object. For that reason we
 * synchronize on access to any instance variables. Acquiring a lock is somewhat
 * slow in Java terms, but compared to hitting the JDBC connection it's very
 * fast.
 * <p>
 *
 * ObjectCache.java Created on Feb 14, 2012
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author DMcG
 * @version $Id$
 */
public class ObjectCache<T> //implements Runnable
{
  /** How long we keep data in cache, in ms */
  public static final int MAX_CACHE_TIME = 30000;

  /**
   * Hash map that contains the cache. Key is a string UUID, value is an object
   * with time of entry into the cache
   */
  private HashMap<Object, TimedEntry> cache = new HashMap<Object, TimedEntry>();

  /** Timer class to periodically clean out the cache */
  private Timer cacheCleanupTimer;

  public ObjectCache()
  {
    this(MAX_CACHE_TIME);
  }

  public ObjectCache(Integer cacheTimeMs)
  {
    if(cacheTimeMs == null)
      return;  // no timeouts

    // Use a timer to schedule periodic runs to clean out the cache.
    cacheCleanupTimer = new Timer("ObjectCache flusher");
    Date now = new Date(); // Current time
    long sec = now.getTime(); // ms since 1970
    Date firstRun = new Date(sec + cacheTimeMs); // Date of first run

    // Schedule a recurring run of the cache cleanup
    cacheCleanupTimer.schedule(new CacheCleanupTask(), firstRun, cacheTimeMs);
  }

  /* App is being shut down, remove all timers */
  public void cancelApp()
  {
      if (cacheCleanupTimer != null)
        cacheCleanupTimer.cancel();
  }

  public synchronized T getObjectForKey(Object key)
  {
    TimedEntry entry = cache.get(key);
    if(entry == null)
      return null;
    return entry.cachedObject;
  }

  public synchronized void addToCache(Object key, T objectToCache)
  {
    TimedEntry entry = new TimedEntry(objectToCache);
    cache.put(key, entry);
  }

  /**
   * Times out any old entries in the cache. Should be called periodically so
   * that our cache does not grow to infinite size.
   */
  public synchronized void cleanCache()
  {

    Set<Object> keys = cache.keySet();
    Iterator<Object> iterator = keys.iterator();
    long currentTime = System.currentTimeMillis();
    while (iterator.hasNext()) {
      Object aKey = iterator.next();
      TimedEntry anEntry = cache.get(aKey);
      if (currentTime - anEntry.entryTime > MAX_CACHE_TIME) {
        iterator.remove();
      }
    }
  }

  public synchronized void clearCache()
  {
    cache.clear();
  }

  /** Returns the number of entries in the object cache. */
  public synchronized int getCacheSize()
  {
    return cache.size();
  }

  public synchronized void remove(Object id)
  {
    cache.remove(id);
  }

  /**
   * used for testing. Set up another object, launch this in a thread, and test
   * concurrent access by having both threads add and remove objects from the
   * cache.
   */
  /*
  @Override
  public void run()
  {
    for (int idx = 0; idx < 100000; idx++) {
      UUID rand = UUID.randomUUID();
      this.addToCache(rand.toString(), new Double(Math.random()));
    }
  }
  */
  /**
   * Utility class that holds the object and the time that the object entered
   * cache
   */
  public class TimedEntry
  {
    public long entryTime;
    public T cachedObject;

    public TimedEntry(T cachedObject)
    {
      this.entryTime = System.currentTimeMillis();
      this.cachedObject = cachedObject;
    }
  }

  /**
   * Inner utility class used with Timer; simply calls the cache cleanup method
   * in the main class
   *
   * @author DMcG
   * @version $Id$
   */
  public class CacheCleanupTask extends TimerTask
  {
    /** The method called by the timer task */
    @Override
    public void run()
    {
      ObjectCache.this.cleanCache();
    }
  }

}