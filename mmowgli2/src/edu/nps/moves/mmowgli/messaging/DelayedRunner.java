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

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * DelayedRunner.java
 * Created on Feb 28, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class DelayedRunner 
{
  public int POOLSIZE = 5;
  private ScheduledThreadPoolExecutor stpe;
  
  public DelayedRunner()
  {
    stpe = new ScheduledThreadPoolExecutor(POOLSIZE);
  }

  public ScheduledFuture<?> runDelayed(char msgType, String message, String ui_id, long msec, MessageSender sender)
  {
    Runner r = new Runner(msgType,message,ui_id,sender);
    ScheduledFuture<?> sf = stpe.schedule(r,msec,TimeUnit.MILLISECONDS);
    r.setSchedFuture(sf);
    return sf;
  }
  
  class Runner implements Runnable
  {
    private char msgType;
    private String message;
    private String ui_id;
    private MessageSender messageSender;
    private ScheduledFuture<?> sf;
    
    public Runner(char msgType, String message, String ui_id, MessageSender sender)
    {
      this.msgType = msgType;
      this.message = message;
      this.ui_id   = ui_id;
      this.messageSender = sender;
    }

    public void setSchedFuture(ScheduledFuture<?> sf)
    {
      this.sf = sf;
    }
    
    @Override
    public void run()
    {
      messageSender.sendMessage(msgType,message,ui_id,sf);
    }
  }
  
  public static interface MessageSender
  {
    public void sendMessage(char msgType, String message, String ui_id, ScheduledFuture<?> sf);
  }
}
