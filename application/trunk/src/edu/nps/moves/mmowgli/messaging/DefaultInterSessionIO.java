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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;

import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * DefaultInterSessionIO.java
 * Created on Nov 24, 2010
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class DefaultInterSessionIO extends InterSessionIOBase
{
  protected HashSet<JmsReceiver> receivers;
  
  public DefaultInterSessionIO()
  {
    this(null);
  }
  
  public DefaultInterSessionIO(JmsReceiver recvr)
  {
    receivers = new HashSet<JmsReceiver>();
    addReceiver(recvr);
  }
  
  @Override
  abstract public void send(MMessagePacket pkt);
  
  @Override
  public void addReceiver(JmsReceiver recvr)
  {
    if(recvr != null) {
      synchronized(receivers) {
        receivers.add(recvr);
      }
    }
  }

  @Override
  public void removeReceiver(JmsReceiver recvr)
  {
    if(recvr != null) {
      synchronized(receivers) {
        receivers.remove(recvr);
      }
    }
  }
  
  public void deliverToReceivers(MMessagePacket packet)
  {
    synchronized(receivers) {
      deliverToReceivers(packet, receivers);
    }
  }
  
  /** this routine used to be synched, but JMS message delivery is single-thread */
  public void deliverToReceivers(MMessagePacket packet, HashSet<JmsReceiver> receivers)
  {
    if (HSess.getSessionFactory() == null)
      return; // we haven't gotten started yet

    HSess.init();

    for (JmsReceiver rcvr : receivers) {
      // Attempt to keep thread alive
      try {
        rcvr.handleIncomingTomcatMessageTL(packet);
      }
      catch (Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(">>>>>>>>>>>>>\n");
        sb.append("Throwable trapped in DefaultInterSessionIO, throwing receiver: ");
        sb.append("\n");
        sb.append(rcvr.getClass().getSimpleName());
        sb.append("\n");
        sb.append("  Throwable: " + t.getClass().getSimpleName() + ": " + t.getLocalizedMessage());
        sb.append("\n");
        sb.append("  Thread should still be alive / Thr nm = " + Thread.currentThread().getName());
        sb.append("\n");
        sb.append("  Stack dump follows:");
        StringWriter stackW = new StringWriter();
        t.printStackTrace(new PrintWriter(stackW));
        sb.append(stackW.toString());
        sb.append("\n<<<<<<<<<<<<\n");

        System.err.println(sb.toString());
      }
    }
    HSess.close();
  }
  
  /**
   * Stop whatever is going on in IO object; 
   */
  @Override
  public void kill()
  {
    super.kill();
    
    if(HSess.get() != null)
      HSess.close();
  }

  protected void doSysOut(String s)
  {
    MSysOut.print(s);
  }
}
