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

import com.vaadin.ui.UIDetachedException;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.hibernate.HSess;
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
public class MessagingManager implements BroadcastListener
{
  private Mmowgli2UI ui;
  private LinkedBlockingQueue<Object> messageQueue = new LinkedBlockingQueue<Object>();
  private Thread messageRunnerThread;
  private HashSet<MMMessageListener> listenersInThisSession = new HashSet<MMMessageListener>();
  private static int seq = 1;
  private int myseq = -1;
  
  public interface MMMessageListener
  {
    /**
     * @return whether UI changes need to be pushed
     * if mgr == null, this is in vaadin event thread, so use VHib
     */
    public boolean receiveMessageTL(MMessagePacket pkt);
  }

  public MessagingManager(Mmowgli2UI ui)
  {
    this.ui = ui;
    myseq = seq++;
    messageRunnerThread = new Thread(queueReader,"UI inter-tomcat message receiver");
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
  
  public void addMessageListener(MMMessageListener ml)
  {
    listenersInThisSession.add(ml);
  }
  
  public void removeMessageListener(MMMessageListener ml)
  {
    listenersInThisSession.remove(ml);
  }

  public void sendSessionMessage(MMessagePacket message)
  {
    MSysOut.println(MESSAGING_LOGS,"MessagingManager.sendSessionMessage() typ="+message.msgType);
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
    MSysOut.println(MESSAGING_LOGS,"MessagingManager.handleIncomingSessionMessage() typ="+message.msgType);    
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
  Runnable queueReader = new Runnable() {
    public void run()
    {
      boolean alive = true;
      while (alive) {
        try {
          Object message = messageQueue.take();
          MSysOut.println(PUSH_LOGS,"MessagingManager.queueRunner -- calling UI.access()");
          ui.access(new MessageRunner(message)); // this makes sure our access of the UI does not conflict with normal Vaadin
        }
        catch (InterruptedException | UIDetachedException ex) {
          System.err.println(ex.getClass().getSimpleName()+" in MessagingManager.queueReader" + myseq);
          if(!alive)
            return; // End thread
        }
      }
    }
  };
  
  /*
   * This gets created on each message, but shouldn't be much overhead...it's just a simple object with an interface.
   * It's the thread creation that is potentially a problem when scaling, and that's handled through the thread pool
   * in Broadcaster (but that's not being used)
   */
  class MessageRunner implements Runnable
  {
    private Object msg;

    public MessageRunner(Object msg)
    {
      this.msg = msg;
    }

    @Override
    public void run()
    {
      MSysOut.println(PUSH_LOGS,"MessagingManager.MessageRunner -- in UI.access()");
      try {
        boolean push = false;
        if (!listenersInThisSession.isEmpty()) {
          HSess.init();
          for (MMMessageListener lis : listenersInThisSession) {
            if (lis.receiveMessageTL((MMessagePacket) msg))
              push = true;
          }
          HSess.close(); // commit

          if (push) {
            try {
              ui.push();
            }
            catch (Throwable t) {
              System.err.println("Exception in MessageRunner.run(): "+t.getClass().getSimpleName() + " " + t.getLocalizedMessage());
            }
          }
        }
      }
      catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }
}
