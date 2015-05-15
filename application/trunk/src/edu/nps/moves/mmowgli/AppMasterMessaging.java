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

package edu.nps.moves.mmowgli;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.util.HashMap;
import java.util.Set;

import edu.nps.moves.mmowgli.cache.MCacheManager;
import edu.nps.moves.mmowgli.components.BadgeManager;
import edu.nps.moves.mmowgli.components.KeepAliveManager;
import edu.nps.moves.mmowgli.hibernate.SearchManager;
import edu.nps.moves.mmowgli.messaging.*;
import edu.nps.moves.mmowgli.messaging.Broadcaster.BroadcastListener;
import edu.nps.moves.mmowgli.messaging.InterTomcatIO.JmsReceiver;
import edu.nps.moves.mmowgli.messaging.JmsIO2.JmsPreviewListener;
import edu.nps.moves.mmowgli.utility.MThreadManager;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * AppMasterMessaging.java
 * Created on Jan 22, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class AppMasterMessaging implements JmsReceiver, JmsPreviewListener, BroadcastListener
{
  private AppMaster appMaster;
  private JmsIO2     _jmsIO;
  private static int sequenceCount = 0;
  private int mysequence = -1;
  private static final int myLogLevel = MESSAGING_LOGS;
  
  public AppMasterMessaging(AppMaster appMaster)
  {
    mysequence = sequenceCount++;
    this.appMaster = appMaster;
    getInterTomcatIO(); // may fail, will get retried in sender thread
    Broadcaster.register(this);
  }

  public InterTomcatIO getInterTomcatIO()
  {
    if (_jmsIO == null) {
      try {
        _jmsIO = new JmsIO2();
        _jmsIO.addReceiver(this);
        _jmsIO.addPreviewJmsListener(this);
        MSysOut.println(myLogLevel,"*****Internode IO built in AppMasterMessaging");
      }
      catch (Exception ex) {
        System.err.println("Can't build internode IO in ApplicationMaster: "+ex.getClass().getSimpleName()+" "+ex.getLocalizedMessage());
      }
    }

    return _jmsIO;
  }
  
  /**
   * This guy gets to look at all external-JMS incoming message, and report back if it is "consumed";
   * The idea is to support our local object cache only.  Want it to be updated before any of our
   * instances want to do anything with the object
   * @param mess
   * @return consumed
   */
  @Override
  public boolean doPreviewJmsMessage(MMessagePacket pkt)
  {
    return false;  //not used
  }
  
  private MCacheManager mcache;
  private MCacheManager getMcache()
  {
    if(mcache == null)
      mcache = appMaster.getMcache();
    return mcache;
  }
  private BadgeManager badgeMgr;
  private BadgeManager getBadgeManager()
  {
    if(badgeMgr == null)
      badgeMgr = appMaster.getBadgeManager();
    return badgeMgr;
  }
  
  private HashMap<String,Integer> serverSessionCounts = new HashMap<String,Integer>();
  private HashMap<String,String> serverSessionReports = new HashMap<String,String>();
  
  /**
   * Called from the servlet listener, which keeps track of our instance count
   * @param sessCount
   */
  public void doSessionCountUpdate(int sessCount)
  {
    // We want to let everyone know we've been updated
    InterTomcatIO sessIO = getInterTomcatIO();
    String msgStr = AppMaster.instance().getServerName()+"\t"+sessCount;
    if (sessIO != null)
      sessIO.sendDelayed(UPDATE_SESSION_COUNT, msgStr, ""); // let this thread return

    // This is because we've changed so that now we don't receive the jms messages we've sent
    handleSessionCountMsg(msgStr);
  }
  
  // This has come in off the message bus. Keep track of server/logins.
  private void handleSessionCountMsg(String message)
  {
    String[] sa = message.split("\t");
    int count = Integer.parseInt(sa[1]);
    serverSessionCounts.put(sa[0], count);
  }

  public int getSessionCount()
  {
    Set<String> keys = serverSessionCounts.keySet();
    int sum = 0;
    for(String s : keys)
      sum += serverSessionCounts.get(s);
    return sum;
  }

  public Object[][] getSessionCountByServer()
  {
    Object[][] oa = new Object[serverSessionCounts.size()][2];
    Set<String> keys = serverSessionCounts.keySet();
    int i = 0;
    for(String s : keys) {
      oa[i][0] = s;
      oa[i][1] = serverSessionCounts.get(s);
      i++;
    }
    return oa;
  }
  
  public String[][] getSessionReportsByServer()
  {
    String[][] sa = new String[serverSessionReports.size()][2];
    Set<String> keys = serverSessionReports.keySet();
    if(keys.isEmpty())
      return new String[0][];
    int i = 0;
    for(String s : keys) {
      sa[i][0] = s;
      sa[i][1] = serverSessionReports.get(s);
      i++;
    }
    return sa;    
  }
  
  public void doSessionReportBroadcast(StringBuilder singleNodeReportSB)
  {
    String singleNodeReport = singleNodeReportSB.toString();
    singleNodeReport = singleNodeReport.replace(SESSION_REPORT_FIELD_DELIMITER, SESSION_REPORT_FIELD_DELIMITER_WIRE);
    InterTomcatIO sessIO = getInterTomcatIO();
    
    String svrName = AppMaster.instance().getServerName();
    int idx;
    if((idx=svrName.indexOf('.')) != -1)
      svrName = svrName.substring(0, idx);

    String msgStr = svrName + SESSION_REPORT_ITEM_DELIMITER_WIRE + singleNodeReport;
    if (sessIO != null) {
      sessIO.sendDelayed(SESSIONS_REPORT, msgStr, ""); // let this thread return 
      MSysOut.println(MESSAGING_LOGS,svrName+" SENT report: "+svrName + SESSION_REPORT_ITEM_DELIMITER_WIRE + singleNodeReport);    
    }
  }
  
  private void handleSessionsReportMsgTL(String message)
  {
    String[] sa = message.split(SESSION_REPORT_ITEM_DELIMITER_WIRE);
    if(sa.length != 2)
      serverSessionReports.remove(sa[0]);
    else {
      // first field server name
      // second \n-separated list of sessions
      sa[1] = sa[1].replace(SESSION_REPORT_FIELD_DELIMITER_WIRE, SESSION_REPORT_FIELD_DELIMITER);    
      serverSessionReports.put(sa[0], sa[1]);
    }
  }
  
  // Handler of messages off the JmsIO2 object, which is receiving msgs from other custer nodes
  @Override 
  public boolean handleIncomingTomcatMessageTL(MMessagePacket pkt)
  {
    MSysOut.println(myLogLevel,"AppMasterMessaging/JMSIO2.handleIncomingTomcatMessageTL()");
    
    if(getMcache() != null)
      mcache.handleIncomingTomcatMessageTL(pkt);

    if(getBadgeManager() != null)
      badgeMgr.messageReceivedTL(pkt);

    switch (pkt.msgType) {
      case JMSKEEPALIVE:
        KeepAliveManager kmgr = appMaster.getKeepAliveManager();
        if(kmgr != null)
          kmgr.receivedKeepAlive(pkt.msg);
        break;
      case UPDATE_SESSION_COUNT:
        handleSessionCountMsg(pkt.msg);
        break;
      case SESSIONS_REPORT:
        handleSessionsReportMsgTL(pkt.msg);
        break;
      case REBUILD_REPORTS:
        handleRebuildReportsMsgTL();
        break;
      case KILLALL_SESSIONS:
        handleKillAllSessionsCommandTL();
        break;
      default:
        Broadcaster.broadcast(pkt,this);  // last param means I don't want to hear my own messages
    }

    // We also pass the message to the SearchManager, which can cause Lucene/Hibernate Search, to index specific
    // object types that are annotated as @Indexed. We sort out which objects to index on the
    // receiving end and ignore the rest.
    SearchManager.indexObjectFromMessageTL(pkt.msgType, pkt.msg);

    return false; // don't want a retry    
  }
 
  public void doRebuildReportsRequestTL()
  {
    // We want to let everyone know we've been updated
    InterTomcatIO sessIO = getInterTomcatIO();
    if (sessIO != null)
      sessIO.sendDelayed(REBUILD_REPORTS, "", ""); // let this thread return

    // This is because we've changed so that now we don't receive the jms messages we've sent
    handleRebuildReportsMsgTL();    
  }
  
  private void handleRebuildReportsMsgTL()
  {
    AppMaster.instance().pokeReportGenerator(); // if we don't build reports on the cluster node, nothing happens here
  }
  
  public void sendKillAllSessionsCommand()
  {
    InterTomcatIO sessIO = getInterTomcatIO();
    if (sessIO != null)
      sessIO.sendDelayed(KILLALL_SESSIONS, "", "");
  }
  
  private void handleKillAllSessionsCommandTL()
  {
    AppMaster.instance().killAllSessionsTL();
  }
  
  /**
   * This is where all messages generated from user sessions in this cluster node come in
   */
  @Override
  public void handleIncomingSessionMessage(MMessagePacket message)
  {
    MSysOut.println(myLogLevel,"AppMasterMessaging, seq "+mysequence+", incomingSessionMessageHandler(receiveBroadcast()), tomcat_id = "+message.tomcat_id);
    ((JmsIO2)getInterTomcatIO()).sendSessionMessage(message);
  }
  
/**
 * This is where all database messages from this cluster come in
 */
  public void incomingDatabaseEvent(final MMessagePacket mMessagePacket)
  {
    MSysOut.println(myLogLevel, "AppMasterMessaging.incomingDatabaseEvent() type: "+mMessagePacket.msgType+" msg: "+mMessagePacket.msg);
    /* We're in the hibernate thread here. Have to let it complete before we can look up the object */
    MThreadManager.run(new Runnable()
    {
      public void run()
      {
        MSysOut.println(myLogLevel, "AppMasterMessaging.incomingDatabaseEvent() running in new thread");
        if (getMcache() != null)
          mcache.handleIncomingDatabaseMessage(mMessagePacket);  // this will make sure our hibernate can see the object
        
        Broadcaster.broadcast(mMessagePacket);
      }
    });
  }
}
