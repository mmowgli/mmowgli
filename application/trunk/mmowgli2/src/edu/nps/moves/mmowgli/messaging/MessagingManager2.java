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

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.messaging.Broadcaster.BroadcastListener;
import edu.nps.moves.mmowgli.utility.MThreadManager;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * MessagingManager.java
 * Created on Apr 2, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MessagingManager2 implements BroadcastListener
{
  private LinkedBlockingQueue<Object> messageQueue = new LinkedBlockingQueue<Object>();
  private Thread messageRunnerThread;
  private HashSet<MMMessageListener2> listenersInThisSession = new HashSet<MMMessageListener2>();
  private static int seq = 1;
  private int myseq = -1;
  private static int myLogLevel = MESSAGING_LOGS;
  
  public boolean alive = true;
  
  public interface MMMessageListener2
  {
    public void receiveMessage(MMessagePacket pkt);
  }

  public MessagingManager2()
  {
    myseq = seq++;
    messageRunnerThread = new Thread(queueReader,"UI inside-tomcat message receiver");
    MThreadManager.priorityNormalLess1(messageRunnerThread);  // One less that database listener, which is VaadinEvent
    messageRunnerThread.start();
  }
  
  public void registerSession()
  {
    Broadcaster.register(this);
  }
  
  public void unregisterSession()
  {
    Broadcaster.unregister(this);
  }
  
  public void killThread()
  {
  	if(messageRunnerThread != null) {
  	  alive = false;
  	  messageRunnerThread.interrupt();
  	}
  }
  
  public void addMessageListener(MMMessageListener2 ml)
  {
    listenersInThisSession.add(ml);
  }
  
  public void removeMessageListener(MMMessageListener2 ml)
  {
    listenersInThisSession.remove(ml);
  }

  public void sendSessionMessage(MMessagePacket message, Mmowgli2UI ui)
  {
    MSysOut.println(myLogLevel,"MessagingManager2.sendSessionMessage() typ="+message.msgType);
    message.session_id = ui.getUserSessionUUID();
    Broadcaster.broadcast(message);
  }
  
  /*
   * After registering with Broadcaster singleton, this is where messages come in.
   * This must not block and be quick.
   */
  @Override
  public void handleIncomingSessionMessage(final MMessagePacket message)
  {
    MSysOut.println(myLogLevel,"MessagingManager2.handleIncomingSessionMessage() typ="+message.msgType);    
    // If this message is from this session, we know we're in the session thread
    // try to deliver the message to us directly  

    // Can be hit before any ui exists
    // Here we're checking to see if we're in the Vaadin event / DB listener thread of
    // this UI.  If so we deliver inline.
 /*   if(ui != null && (message.getUi_id().equals(ui.getUI_UUID())) ) {
      MSysOut.println("MessagingManager"+myseq+" delivering inline");
      deliverInLine(message);
    }
    else { */
      messageQueue.add(message);
//   }
  }
 /* 
  private void deliverInLine(MMessagePacket msg)
  {
    if (!listenersInThisSession.isEmpty()) {
      for (MMMessageListener lis : listenersInThisSession) {
        MSysOut.println("MessagingManager.deliverInline to "+lis.getClass().getSimpleName()+" "+lis.hashCode());
        lis.receiveMessageTL((MMessagePacket) msg);
      }
    }
    else
      MSysOut.println("MessagingManager"+myseq+": no listeners");
  }
 */
  /*
   * This is our internal thread which serializes handling of messages for this session when received from another session and we aren't in Vaadin thread
   */
  Runnable queueReader = new Runnable()
  {
    public void run()
    {
      while (alive) {
        try {
          //MSysOut.println(MESSAGING_LOGS, "MessageingManager2.queueReader() taking from queue (block here)");
          Object message = messageQueue.take();
          //MSysOut.println(MESSAGING_LOGS, "MessagingManager2.queueReader() got "+((MMessagePacket)message).toString());
          for (MMMessageListener2 lis : listenersInThisSession) {
            //MSysOut.println(MESSAGING_LOGS, "MessagingManager2.queueReader() delivering to "+lis.getClass().getSimpleName());
            lis.receiveMessage((MMessagePacket) message);
          }
        }
        catch (InterruptedException ex) { // | UIDetachedException ex) {
          if (!alive) {
          	messageRunnerThread = null;
            return; // End thread
          }
          MSysOut.println(ERROR_LOGS,ex.getClass().getSimpleName() + " in MessageingManager2.queueReader catch "+myseq);
        }
      }
    }
  };
}
