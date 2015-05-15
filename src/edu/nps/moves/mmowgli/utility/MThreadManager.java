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

package edu.nps.moves.mmowgli.utility;

import static edu.nps.moves.mmowgli.MmowgliConstants.MESSAGING_LOGS;

import java.util.concurrent.*;

import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
public class MThreadManager
{
  private MThreadManager(){} // not instanciable
  
  // Using a fixed thread pool is a way to let database listener thread complete before other db listener threads run.
  // It necessarily synchronizes db access which is counter to what a pool normally does.
  // I think some of our problems have had to do with some code getting the message that the db has changed, but we're running
  // in the context of the listener, which is in the context of the hibernate update/save calls.  This seems to let the first
  // finish before the latter tries to access the first's data.
  
  private static ExecutorService pool = Executors.newFixedThreadPool(1); //newCachedThreadPool();
  
  public static void run(Runnable runner)
  {
    pool.execute(new Preamble(runner,Thread.currentThread().getPriority()-2));
  }
  
  public static void run(Runnable runner, boolean wait)
  {
    Future<?> f = pool.submit(runner);
    if(wait)
      try {
        f.get();
      }
      catch(InterruptedException | ExecutionException ex) {
        System.err.println("Exception waiting for thread completion in MThreadManager: "+ex.getLocalizedMessage());
      }

  }
  /* This is a little shim which makes sure our priorities are straight */
  /* Should be minimally expensive to make and destroy */
  private static class Preamble implements Runnable
  {
    private Runnable runner;
    private int prior;
    private static int seq=0;
    
    public Preamble(Runnable runner, int priority)
    {
      this.runner = runner;
      prior = priority;
      if(prior < Thread.MIN_PRIORITY)
        prior = Thread.MIN_PRIORITY;
      else if(prior>Thread.MAX_PRIORITY)
        prior = Thread.MAX_PRIORITY;
    }
    @Override
    public void run()
    {
      int myseq = seq++;
      MSysOut.println(MESSAGING_LOGS,"MThreadManager.Preamble start "+myseq);
      Thread thr = Thread.currentThread();
      thr.setPriority(prior);
      thr.setName("MThreadManagerPoolThread");
      try{Thread.sleep(100L);}catch(InterruptedException ex){} //Thread.yield();
      runner.run();
      MSysOut.println(MESSAGING_LOGS,"MThreadManager.Preamble end "+myseq);
    }
  }
  
  static private int UP_PRIORITY = 0;
  
  static {
    int mx = Thread.MAX_PRIORITY;
    int mn = Thread.MIN_PRIORITY;
    UP_PRIORITY = (mx>mn?+1:-1);    
  }
  public static void priorityNormalPlus1(Thread t)
  {
    t.setPriority(Thread.NORM_PRIORITY);
    priorityUp(t);
  }
  public static void priorityNormalLess1(Thread t)
  {
    t.setPriority(Thread.NORM_PRIORITY);
    priorityDown(t);
  }
  public static void priorityUp()
  {
    priorityUp(Thread.currentThread());
  }
  public static void priorityUp(Thread t)
  {
    t.setPriority(t.getPriority()+UP_PRIORITY);
  }
  public static void priorityDown()
  {
    priorityDown(Thread.currentThread());
  }
  public static void priorityDown(Thread t)
  {
    t.setPriority(t.getPriority()-UP_PRIORITY);    
  }
}
