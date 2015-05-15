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

import static edu.nps.moves.mmowgli.MmowgliConstants.BROADCASTER_LOGS;

import java.io.Serializable;
import java.util.LinkedList;

import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/* This is straight out of the vaadin 7 book.  Simple is good */
/* This singleton will be global across all sessions */

/*
 * There is one of these in a Tomcat deployment of mmowgli.  It is shared by all the user
 * sessions and the AppMaster, of which there is also one per Tomcat deployment.
 * 
 * Currently, the registrants are
 *  1. AppMasterMessaging, which belongs to AppMaster
 *  2. MessagingManager, of which there is one per user session
 * 
 * A user session can have multiple UI's, corresponding to different windows in the same browser.
 */
/**
 * Broadcaster.java
 * Created on Apr 2, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Broadcaster implements Serializable
{
  private static final long serialVersionUID = 11160928201779804L;
  private static final int myLogLevel = BROADCASTER_LOGS;

  public interface BroadcastListener
  {
    void handleIncomingSessionMessage(MMessagePacket message);
  }
  
  private static LinkedList<BroadcastListener> listeners = new LinkedList<BroadcastListener>();

  public static synchronized void register(BroadcastListener listener)
  {
    listeners.add(listener);
  }
  
  public static synchronized void unregister( BroadcastListener listener)
  {
    listeners.remove(listener);
  }
  
  public static synchronized void broadcast(MMessagePacket message, BroadcastListener blackout)
  {
    MSysOut.println(myLogLevel,"Broadcaster will deliver message type "+message.msgType);
    for(BroadcastListener listener: listeners)
      if(blackout == null || !blackout.equals(listener)) {
        MSysOut.println(myLogLevel,"Broadcaster delivering message "+message.msgType+" to "+listener.getClass().getSimpleName()+" "+listener.hashCode());
        listener.handleIncomingSessionMessage(message); 
      }
  }
  
  public static synchronized void broadcast(final MMessagePacket message)
  {
    broadcast(message,null);
  }
  
  /* to keep this from being instantiated */
  private Broadcaster(){}
}