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

import java.util.Calendar;
import java.util.UUID;

import javax.jms.*;

import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * JMSMessageFactory.java
 * Created on Feb 17, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class JMSMessageUtil
{
  // Prevent instantiation
  private JMSMessageUtil()
  {}
  
  public static Message create(Session sess, char typ, String text, String sourceUiId, String sourceTomcatId, String msg_uuid) throws JMSException
  {
    TextMessage msg = sess.createTextMessage("mmowgli event update");
    char[] mtyp = { typ };
    msg.setStringProperty(JMS_MESSAGE_TYPE, new String(mtyp));
    msg.setStringProperty(JMS_MESSAGE_TEXT, text);
    msg.setStringProperty(JMS_MESSAGE_SOURCE_SESSION_ID, sourceUiId);
    msg.setStringProperty(JMS_MESSAGE_SOURCE_TOMCAT_ID, sourceTomcatId);
    msg.setStringProperty(JMS_MESSAGE_UUID, msg_uuid);
    return msg;
  }
  
  public static class JMSPacket
  {
    public char type;
    public String text;
    public String sourceUiId;
    public String sourceTomcatId;
    public String uuid;
  }
  
  public static MMessagePacket decode(Message message) throws JMSException
  {
    char type = message.getStringProperty(JMS_MESSAGE_TYPE).charAt(0);
    String text = message.getStringProperty(JMS_MESSAGE_TEXT);
    String sourceSessionId = message.getStringProperty(JMS_MESSAGE_SOURCE_SESSION_ID);
    String sourceTomcatId = message.getStringProperty(JMS_MESSAGE_SOURCE_TOMCAT_ID);
    String msg_uuid = message.getStringProperty(JMS_MESSAGE_UUID);
    MMessagePacket mpkt = new MMessagePacket(type,text,msg_uuid, sourceSessionId,sourceTomcatId);
    return mpkt;
  }
  public static JMSPacket xdecode(Message message) throws JMSException
  {
    JMSPacket pkt = new JMSPacket();
    
    pkt.type = message.getStringProperty(JMS_MESSAGE_TYPE).charAt(0);
    pkt.text = message.getStringProperty(JMS_MESSAGE_TEXT);
    pkt.sourceUiId = message.getStringProperty(JMS_MESSAGE_SOURCE_SESSION_ID);
    pkt.sourceTomcatId = message.getStringProperty(JMS_MESSAGE_SOURCE_TOMCAT_ID);
    pkt.uuid = message.getStringProperty(JMS_MESSAGE_UUID);
    if(pkt.uuid == null)
      pkt.uuid = UUID.randomUUID().toString();
    
    return pkt;
  }
  
  public static void dump(String s, Message mess)
  {
    try {
      MSysOut.println(JMS_LOGS, s +
                       mess.getStringProperty(JMS_MESSAGE_TYPE) + ", " +
                       mess.getStringProperty(JMS_MESSAGE_TEXT) + ", " +
                       mess.getStringProperty(JMS_MESSAGE_SOURCE_SESSION_ID) + ", " +
                       mess.getStringProperty(JMS_MESSAGE_UUID) + ", " +
                       mess.getStringProperty(JMS_MESSAGE_SOURCE_TOMCAT_ID)

                       + " \t/" + getHHMMSSmmm());
    }
    catch(JMSException ex) {
      System.err.println("Exception dumping message: "+ex.getLocalizedMessage());
    }
  }
  
  public static void showException(String s, Throwable ex)
  {
    System.err.println(s + ex.getClass().getSimpleName() + " " + ex.getLocalizedMessage());
    //("JmsIO2: Error processing message received from external JMS /" + getHHMMSSmmm());
  }

  public static char getType(Message jmsMessage) throws JMSException
  {
    return jmsMessage.getStringProperty(JMS_MESSAGE_TYPE).charAt(0);
  }
  

  public static Message clone(Session sess, MMessagePacket mess) throws JMSException
  {
    return clone(sess,mess,"dummy",null);
  }
  
  public static Message clone(Session sess, MMessagePacket mess, String key, String value) throws JMSException
  {
    TextMessage newM = sess.createTextMessage("mmowgli event update");

    if(key.equals(JMS_MESSAGE_SOURCE_SESSION_ID))
      newM.setStringProperty(key, value);
    else
      newM.setStringProperty(JMS_MESSAGE_SOURCE_SESSION_ID,mess.session_id);//getStringProperty(JMS_MESSAGE_SOURCE_UI_ID));
    
    if(key.equals(JMS_MESSAGE_SOURCE_TOMCAT_ID))
      newM.setStringProperty(key, value);
    else
      newM.setStringProperty(JMS_MESSAGE_SOURCE_TOMCAT_ID,mess.tomcat_id); //getStringProperty(JMS_MESSAGE_SOURCE_TOMCAT_ID));
    
    if(key.equals(JMS_MESSAGE_TYPE))
      newM.setStringProperty(key, value);
    else
      newM.setStringProperty(JMS_MESSAGE_TYPE,Character.toString(mess.msgType)); //getStringProperty(JMS_MESSAGE_TYPE));
    
    if(key.equals(JMS_MESSAGE_TEXT))
        newM.setStringProperty(key, value);
    else
      newM.setStringProperty(JMS_MESSAGE_TEXT,mess.msg); //getStringProperty(JMS_MESSAGE_TEXT));
    
    if(key.equals(JMS_MESSAGE_UUID))
        newM.setStringProperty(key, value);
    else
      newM.setStringProperty(JMS_MESSAGE_UUID,mess.message_uuid); //getStringProperty(JMS_MESSAGE_UUID));
   
    return newM;
  }

  private static long midnight;
  private static long MS_PER_HR = 1000l * 60 * 60;
  private static long MS_PER_MIN = 1000l * 60;
  private static long MS_PER_SEC = 1000l;
  
  static {
    Calendar now = Calendar.getInstance();
    now.set(Calendar.HOUR_OF_DAY, 0);
    now.set(Calendar.MINUTE, 0);
    now.set(Calendar.SECOND, 0);
    now.set(Calendar.MILLISECOND, 0);
    midnight = now.getTimeInMillis();
  }

  public static String getHHMMSSmmm()
  {
    StringBuilder sb = new StringBuilder();
    long offs = System.currentTimeMillis() - midnight;
    long hr = (offs / MS_PER_HR) % 24; // wrap at 23:59
    offs %= MS_PER_HR;
    long min = offs / MS_PER_MIN;
    offs %= MS_PER_MIN;
    long sec = offs / MS_PER_SEC;
    long ms = offs % MS_PER_SEC;

    return sb.append(hr).append(':').append(min).append(':').append(sec).append('.').append(ms).toString();
  }

  public static String last6(String s)
  {
    if (s != null)
      if (s.length() > 6)
        s = s.substring(s.length() - 6);
    return s;
  }
}
