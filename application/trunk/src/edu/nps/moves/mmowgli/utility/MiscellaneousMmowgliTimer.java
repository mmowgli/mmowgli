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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliConstants;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;

public class MiscellaneousMmowgliTimer
{
  private Timer timer;

  public MiscellaneousMmowgliTimer() {
    timer = new Timer("MiscMmowTimer", true); // deamon, does not prolong life
                                              // of app
    addRepeatingTask(new Tick(), Tick.PERIOD_MS);
    addRepeatingTask(new MSysOut(), MSysOut.PERIOD_MS);
    addRepeatingTask(new PushPing(), PushPing.PERIOD_MS);
   /* use if the cluster-master-selection-scheme supports dynamic re-selection 
    addRepeatingTask(new CheckClusterMaster(), 5*60*1000,CheckClusterMaster.PERIOD_MS);  // wait 5 minutes before starting
    */
  }

  /* App is being shut down, remove all timers */
  public void cancelTimer()
  {
    timer.cancel();
  }

  public void addRepeatingTask(TimerTask task, long delay_in_ms, long period_in_ms)
  {
    timer.schedule(task, delay_in_ms, period_in_ms);    
  }
  
  public void addRepeatingTask(TimerTask task, long period_in_ms)
  {
    addRepeatingTask(task,0,period_in_ms);
  }

  // Some default timers
  public static class Tick extends TimerTask
  {
    public static long PERIOD_MS = 60 * 1000 * 2; // every 2 min
    DateFormat tickFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss.SSS");

    @Override
    public void run()
    {
      MSysOut.immPrint(MmowgliConstants.TICK_LOGS,"-tick-" + tickFormat.format(new Date()));
    }
  }
/*  
  public static class CheckClusterMaster extends TimerTask
  {
    public static long PERIOD_MS = 60*1000;  // 1 min
    boolean lastVal = true; // just for starters, may not really be the case
    @Override
    public void run()
    {
      boolean b = AppMaster.instance().amIClusterMaster();
      if(b == lastVal)
        return;
      
      lastVal = b;
      if (b) {      // am I newly chosen?
        AppMaster.instance().doClusterMasterChangedTasks(); 
      }
    }    
  }
*/ 
  public static class PushPing extends TimerTask
  {
  	public static long PERIOD_MS = 60*1000 + 45*1000;  // 1 min 45 secs
  	private PushPing() {}
  	@Override
  	public void run()
  	{
  		AppMaster.instance().pushPingAllSessionsInThisClusterNode();
  	}
  }
  
  public static class MSysOut extends TimerTask
  {
    public static long PERIOD_MS = 100; //todo back to 5 * 1000;
    private static StringBuilder sb = new StringBuilder();
    private static String nl = System.getProperty("line.separator");

    private MSysOut() {
    }

    public static void println(int logLevel, String...sa)
    {
      if((AppMaster.sysOutLogLevel & logLevel) == logLevel)
        addToSb(logLevel,true,false,sa);
    }
    
    public static void println(String... sa)
    {
      addToSb(null,true,false,sa);
    }

    public static void print(int logLevel, String...sa)
    {
      if((AppMaster.sysOutLogLevel & logLevel) == logLevel)
        addToSb(logLevel,false,false,sa);
    }
    
    public static void print(String... sa)
    {
      addToSb(null,false,false,sa);
    }

    private static void addToSb(Integer logLevel, boolean indivNL, boolean endNL, String... sa)
    {
      if (sb != null) {
        synchronized (sb) {
          for(String s : sa) {
            if(logLevel != null) {
              sb.append(MmowgliConstants.logTokens.get(logLevel));
              sb.append(' ');
            }
            sb.append(vaadinCookie());//msTimeStamp());
            sb.append(s);
            sb.append(' ');
            sb.append(Thread.currentThread().hashCode());
            if(indivNL)
              sb.append(nl);
          }
          if(endNL)
            sb.append(nl);
        }
      }
    }
    
    private static String vaadinCookie()
    {
      MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
      if(globs != null)
        return globs.getVaadinSessionCookie()+" ";
      else
        return "null-cookie ";
    }
/*    
    private static String msTimeStamp()
    {
      Long t = System.currentTimeMillis();
      t&=0xFFFFFF;
      return ""+t+" ";
    }
*/    
    // immediate write
    public static void immPrint(Integer logLevel, String... sa)
    {
      if ((AppMaster.sysOutLogLevel & logLevel) == logLevel) {
        if (sb != null) {
          synchronized (sb) {
            if (logLevel != null) {
              sb.append(MmowgliConstants.logTokens.get(logLevel));
              sb.append(' ');
            }
            if (sb.length() > 0) {
              System.out.print(sb.toString());
              sb.setLength(0);
            }
            SysoutVarargs(sa);
          }
        }
      }
    }
    
    private static void SysoutVarargs(String...sa)
    {
      for(String s : sa) {
        System.out.print(vaadinCookie()+" ");//msTimeStamp());
        System.out.println(s);
      }
    }
    
    @Override
    public void run()
    {
      synchronized (sb) {
        if (sb.length() > 0) {
          System.out.print(sb.toString());
          sb.setLength(0);
        }
      }
    }
    
    public static void dumpStack(int logLevel)
    {
      PrintWriter pw = new PrintWriter(new MSysOutStream(logLevel),true);
      new Exception().printStackTrace(pw);
    }
  }
  
  private static class MSysOutStream extends OutputStream
  {
    int nl = System.getProperty("line.separator").charAt(0);
    
    StringWriter sw = new StringWriter(4096);
    int logLevel;
    
    MSysOutStream(int logLevel)
    {
      this.logLevel = logLevel;
    }

    @Override
    public void write(int b) throws IOException
    {
      if(b != nl)
        sw.write(b);     
    }

    @Override
    public void flush() throws IOException
    {
      String s = sw.toString();
      if(s.trim().length()>0)
        MSysOut.println(logLevel,s);
      sw = new StringWriter();
    }
  }
}
