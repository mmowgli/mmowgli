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

package edu.nps.moves.mmowgli.messaging;

import java.util.HashSet;
import java.util.concurrent.ScheduledFuture;

import edu.nps.moves.mmowgli.messaging.DelayedRunner.MessageSender;

/**
 * InterSessionIOBase.java Created on Feb 17, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This class supports detached sending...i.e., the calling thread returns before the message is sent.
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public abstract class InterSessionIOBase implements InterTomcatIO
{
  public static long DEFAULT_DELAY_MSEC = 250l;

  private static DelayedRunner runner = new DelayedRunner();  // One of these per JVM/
  
  private HashSet<ScheduledFuture<?>> tasks = new HashSet<ScheduledFuture<?>>();
  private MessageSender sender;
  
  public InterSessionIOBase()
  {
    sender = new MySender();
  }
  
  public void sendDelayed(char messageType, String message, String ui_id)
  {
    sendDelayed(messageType, message, ui_id, DEFAULT_DELAY_MSEC);
  }

  public void sendDelayed(char msgTyp, String msg, String ui_id, long msec)
  {
    tasks.add(runner.runDelayed(msgTyp, msg, ui_id, msec, sender));
  }
  
  class MySender implements MessageSender
  {
    @Override
    public void sendMessage(char msgType, String message, String session_id, ScheduledFuture<?>sf)
    {
      tasks.remove(sf);
      send(msgType, message, session_id);
    }   
  }
  
  @Override
  public void kill()
  {
    for(ScheduledFuture<?> sf : tasks)
      sf.cancel(false);
    tasks.clear();
  }
  /*
  private Timer timer;
  
  public InterSessionIOBase()
  {
    timer = new Timer("InterSessionIOSendDelayedTimer");
  }
  public void sendDelayed(char messageType, String message)
  {
    sendDelayed(messageType, message, DEFAULT_DELAY_MSEC);
  }

  public void sendDelayed(char msgTyp, String msg, long msec)
  {
    // Give the db a change to actually receive the update
    TimerTask tt = new MyTimerTask(msgTyp, msg);
    timer.schedule(tt, msec);
  }

  class MyTimerTask extends TimerTask
  {
    char msgTyp;
    String msg;

    public MyTimerTask(char msgTyp, String msg)
    {
      this.msgTyp = msgTyp;
      this.msg = msg;
    }

    @Override
    public void run()
    {
      send(msgTyp, msg);
    }
  }

  @Override
  public void kill()
  {
   timer.cancel();
  }
  */
}
