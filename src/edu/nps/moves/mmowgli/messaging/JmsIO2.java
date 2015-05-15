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

import java.util.UUID;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQTopicPublisher;

import edu.nps.moves.mmowgli.MmowgliConstants;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * JmsIo.java Created on Apr 24, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This uses Java Messaging System (JMS) in addition to the Simple Bus IO model
 * to achieve communication between both different sessions in the same tomcat
 * server and different tomcat servers running on different hosts. It uses
 * Apache's ActiveMQ and topic-based, publish-subscribe to achieve this.
 * <p>
 * 
 * This is intended to be simply another observer in the observer pattern; it is
 * added to the ApplicationMaster _sessionIO object as a listener. One side is
 * hooked up to the internal event bus side, one side to JMS.
 * 
 * @author DMcG
 * @version $Id$
 */
public class JmsIO2 extends DefaultInterSessionIO implements JMSMessageListener
{

  /**
   * How long a message can live on the broker before it is expired, in ms. This
   * can prevent some message from living in the publisher for hours only to be
   * delivered in an inappropriate context
   */
  public static final int MESSAGE_TTL = 20000;

  /**
   * Randomly generated UUID that is used to as a unique ID for a single tomcat
   * server
   */
  public String tomcatServerIdentifier;

  /** One JMS session for communications with the JMS server, one locally */
  public TopicSession jmsExternalSession, jmsLocalSession;

  /** A topic, the name for the publish/subscribe channels */
  public Topic jmsExternalTopic, jmsLocalTopic;

  /** Writes messages to the JMS servers */
  public TopicPublisher jmsExternalPublisher;

  /** Reads messages from the JMS servers */
  public MessageConsumer jmsExternalConsumer;

  /** Cache handler */
  private JmsPreviewListener firstListener;
  
  public JmsIO2()
  {
    // Subscribes to the external jms, for communication between Tomcat servers within a private cluster.
    // We want to pass things from JMS to the local jms, and from the local jms to JMS

    // UUID for this tomcat server instance. This is used to disambiguate the
    // sender of messages;
    // we can send a message to a topic as a producer and read the same message
    // back as a consumer. The unique identifier on the sender allows us to
    // discard it when we get it back.
    
    tomcatServerIdentifier = "tomcat/"+UUID.randomUUID().toString();

    String jmsUrl = JMS_INTERNODE_URL;
    String jmsTopic = JMS_INTERNODE_TOPIC;

    if (jmsUrl == null || jmsTopic == null) {
      MSysOut.println(JMS_LOGS,"JmsIO2: No JMS server URL = "+jmsUrl + " jmsTopic = " + jmsTopic + ". Not performing any between-tomcat-servers event messaging");
    }
    else { // appropriate constants found, set up JMS
      try {
        // Create a connection to the JMS server. The reliance on ActiveMQ can
        // be
        // reduced by using a JNDI lookup (so we can use only abstract
        // interfaces
        // to talk to JMS). The below works well enough for us. We use
        // "topic connections"
        // which allow publish/subscribe semantics.
        MSysOut.println(JMS_LOGS,"JmsIO2: Getting external connection factory at "+jmsUrl);
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsUrl);
        /*new*/connectionFactory.setAlwaysSessionAsync(false);
        MSysOut.println(JMS_LOGS,"JmsIO2: Creating external topic connection");
        Connection jmsTopicConnection = connectionFactory.createTopicConnection();
        MSysOut.println(JMS_LOGS,"JmsIO2: Starting external topic connecton");
        jmsTopicConnection.start();

        // A topic session allows publish/subscribe, vs a queue connection which
        // does point-to-point messaging.
        MSysOut.println(JMS_LOGS,"JmSIO2: Creating external non-transacted, auto-ack topic session.");
        /*new -- only one session on this connection*/
        jmsExternalSession = (TopicSession) jmsTopicConnection.createSession(
            false, // transacted  or not
            Session.AUTO_ACKNOWLEDGE); // session acks a client's receipt

        // If the topic does not exist, it is created on the broker. If it does
        // exist, we get a reference to that.
        MSysOut.println(JMS_LOGS,"JmsIO2: Creating external topic: " + JMS_INTERNODE_TOPIC);
        jmsExternalTopic = jmsExternalSession.createTopic(JMS_INTERNODE_TOPIC);

        // Create a topic publisher. This gives us a channel to send messages to
        // the broker.
        jmsExternalPublisher = (ActiveMQTopicPublisher)jmsExternalSession.createPublisher(jmsExternalTopic);
      //todo resolve  jmsExternalPublisher.setTimeToLive(MESSAGE_TTL);
        jmsExternalPublisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      //  MSysOut.println("   JmsIO2: Created external topic " + jmsExternalTopic + " with TTL " + MESSAGE_TTL + " for publishing");
        MSysOut.println(JMS_LOGS,"   JmsIO2: Created external topic " + jmsExternalTopic + " with deliver mode non-persistent for publishing");
        // Create a topic subscriber. (This will receive messages published only
        // since
        // it was created; you don't have to worry about getting pre-creation
        // messages.)
        MSysOut.println(JMS_LOGS,"JmsIO2: Creating external subscriber (consumer)");
        jmsExternalConsumer = jmsExternalSession.createSubscriber(jmsExternalTopic);
        MSysOut.println(JMS_LOGS,"  JmsIO2: Created external consumer for topic: " + JMS_INTERNODE_TOPIC);

        // We receive messages async.
        jmsExternalConsumer.setMessageListener(this);

        MSysOut.println(JMS_LOGS,"JmsIO2: External JMS Server connection established for inter-tomcat comms. Server ID = " + tomcatServerIdentifier);
      }
      catch (Exception e) {
        MSysOut.println(JMS_LOGS,"JmsIO2: Exception: " + e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
        MSysOut.println(JMS_LOGS,"JmsIO2: Cannot create external JMS session; JMS server may be down. ");
        MSysOut.println(JMS_LOGS,"JmsIO2: There will be no inter-cluster communication");
        jmsExternalPublisher = null;
        jmsExternalConsumer = null;
      }      
    }
  }

  /**
   * Sends out a locally-generated message to the external JMS side.
   * 
   * @param messageType
   * @param message
   */
  public boolean sendJms(char messageType, String message, String session_id, String msgID)
  {
    // If we've got a valid JMS connection, ie it didn't fail on setup
    if ((jmsExternalPublisher != null) && (JMS_INTERNODE_TOPIC != null)) {  
      try {
        return sendJms(JMSMessageUtil.create(jmsExternalSession, messageType, message, session_id, tomcatServerIdentifier,msgID));
      }
      catch(JMSException ex) {
        JMSMessageUtil.showException("Exception in JMSIO2.sendJms(): ",ex);
      }
    }
    return false;
  }
  
  /**
   * Sends out a locally-generated message to the external JMS side.
   */
  public boolean sendJms(Message jmsMessage)  // this is a javax.jms.Message
  {
    // If we've got a valid JMS connection, ie it didn't fail on setup
    if ((jmsExternalPublisher != null) && (JMS_INTERNODE_TOPIC != null)) {
      try {
        //JMSMessageUtil.dump("-----Sending from appmaster (JmsIO2) to external jms: ", jmsMessage);  //test
        jmsExternalPublisher.publish(jmsMessage);

        if (MmowgliConstants.FULL_MESSAGE_LOG)
          JMSMessageUtil.dump("JmsIO: Pub: ",jmsMessage);        
        else {
          //char mTyp = JMSMessageUtil.getType(jmsMessage);
          //doMSysOut("P"+mTyp);
        }
        return true; // good send if we got here
      }
      catch (Throwable e) { //JMSException e) {
        JMSMessageUtil.showException("Exception in JMSIO2.sendJms(): ", e); 
      }
    }
    return false;
    
  }

  /**
   * This is the "send" method to send to external
   */
  @Override
  public void send(char messageType, String message, String session_id)
  {
    sendJms(messageType,message,session_id,UUID.randomUUID().toString());
  }

  @Override
  public void send(MMessagePacket pkt)
  {
    sendJms(pkt.msgType,pkt.msg,pkt.session_id,pkt.message_uuid);
  }
  /* Where messages from the local tomcat broadcaster come in*/

  public void sendSessionMessage(MMessagePacket pkt)
  {
    pkt.tomcat_id = tomcatServerIdentifier;  // mark  who's sending
    // First give to local receivers (AppMaster)
    // is this required?
    /*
    try {
      deliverToReceivers(pkt,false);
    }
    catch (Throwable ex) { // JMSException ex) {
      JMSMessageUtil.showException("JmsIO2: Cannot decode received message/ ", ex);
      return;
    }
*/
    if (jmsExternalSession == null) // no inter-node comms.
      return;

    //if (isLocalMessageOnly(pkt.msgType))
    //  return;


    String whichException = "";
    try {
        whichException = "Error in JMSMessageUtil.clone(), ";
        Message newmess = JMSMessageUtil.clone(jmsExternalSession, pkt, JMS_MESSAGE_SOURCE_TOMCAT_ID, tomcatServerIdentifier);
        
        whichException = "Error in sendJMS(newmess), ";
        sendJms(newmess);
    }
    catch (Throwable e) {
      JMSMessageUtil.showException("JmsIO2: Cannot send locally-generated JMS message (" + whichException + ")/ ", e);
      e.printStackTrace();
    }
  }

  /**
   * This is the method to which messages come when received from external JMS.
   * 
   * Note that we must sort out messages sent by us; when we send a message we
   * will also receive it as a consumer. If we don't remove those messages we
   * will fall into an infinite loop.
   */
  @Override
  public void onMessage(javax.jms.Message message)
  {
    try {
      MSysOut.println(JMS_LOGS,"Message received on appmaster (JmsIO2) from external jms");
      MMessagePacket pkt = JMSMessageUtil.decode(message);
      // We discard anything sent by us so we don't get into an infinite feedback loop      
      if (pkt.tomcat_id==null || !pkt.tomcat_id.equals(tomcatServerIdentifier)) {
              
        // Want our local object cache to be updated first so all the instances on this local machine
        // are able to use the fresh object in the cache.  So we give to cache mgr, but message also comes
        // back to us in the local onMessage handler above, so the db will get hit twice per cluster...better
        // than once per instance as before;

        if(firstListener != null)
          if( firstListener.doPreviewJmsMessage(pkt))
            return; // consumed

         deliverToReceivers(pkt); // Let the registered receivers handle it
      }
      else
        JMSMessageUtil.dump("Message dumped because it's from me.", message);
    }
    catch (JMSException e) {
      JMSMessageUtil.showException("Exception in JmsIO2.onMessage(): ",e);
      return;
    }
    catch (Throwable t) {
      System.err.println("Exception in JmsIO2.onMessage(): "+t.getClass().getSimpleName()+": "+t.getLocalizedMessage());
      t.printStackTrace();
    }
  }

  public static interface JmsPreviewListener
  {
    public boolean doPreviewJmsMessage(MMessagePacket pkt);
  }

  public void addPreviewJmsListener(JmsPreviewListener fListener)
  {
    firstListener = fListener;
  }
}
