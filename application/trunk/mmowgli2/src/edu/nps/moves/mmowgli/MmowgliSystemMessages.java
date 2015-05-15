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

import com.vaadin.server.CustomizedSystemMessages;

/*
 * MmowgliSystemMessages.java
 * Created on Aug 28, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
/**
 Contains the system messages used to notify the user about various critical situations that can occur.

 Vaadin gets the SystemMessages from your application by calling a static getSystemMessages() method.
 By default the Application.getSystemMessages() is used. You can customize this by defining a static 
 MyApplication.getSystemMessages() and returning CustomizedSystemMessages. Note that getSystemMessages() 
 is static - changing the system messages will by default change the message for all users of the 
 application.

 The default behavior is to show a notification, and restart the application the the user clicks the
 message.
 
 Instead of restarting the application, you can set a specific URL that the user is taken to.

 Setting both caption and message to null will restart the application (or go to the specified URL)
 without displaying a notification. set*NotificationEnabled(false) will achieve the same thing.

 The situations are:

    Session expired: the user session has expired, usually due to inactivity.
    Communication error: the client failed to contact the server, or the server returned and invalid response.
    Internal error: unhandled critical server error (e.g out of memory, database crash)
    Out of sync: the client is not in sync with the server. E.g the user opens two windows showing the same
      application, but the application does not support this and uses the same Window instance. When the user
      makes changes in one of the windows - the other window is no longer in sync, and (for instance) 
      pressing a button that is no longer present in the UI will cause a out-of-sync -situation.
      
      
 A session is kept alive by server requests caused by user interaction with the application as well
 as the heartbeat monitoring of the UIs. Once all UIs have expired, the session still remains. It is
 cleaned up from the server when the session timeout configured in the web application expires.
 If there are active UIs in an application, their heartbeat keeps the session alive indefinitely.You
 may want to have the sessions timeout if the user is inactive long enough, which is the original
 purpose of the session timeout setting. If the closeIdleSessions parameter of the servlet is
 set to true in the web.xml, the session and all of its UIs are closed when the timeout specified
 by the session-timeout parameter of the servlet expires after the last non-heartbeat request 
 
      heartbeatInterval
      closeIdleSessions
      session-timeout
 
      Default messages:
        sessionExpiredURL = null
        sessionExpiredNotificationEnabled = true
        sessionExpiredCaption = "Session Expired"
        sessionExpiredMessage = "Take note of any unsaved data, and click here to continue."
        communicationErrorURL = null
        communicationErrorNotificationEnabled = true
        communicationErrorCaption = "Communication problem"
        communicationErrorMessage = "Take note of any unsaved data, and click here to continue."
        internalErrorURL = null
        internalErrorNotificationEnabled = true
        internalErrorCaption = "Internal error"
        internalErrorMessage = "Please notify the administrator.
                                Take note of any unsaved data, and click here to continue."
        outOfSyncURL = null
        outOfSyncNotificationEnabled = true
        outOfSyncCaption = "Out of sync"
        outOfSyncMessage = "Something has caused us to be out of sync with the server.
                            Take note of any unsaved data, and click here to re-sync."
        cookiesDisabledURL = null
        cookiesDisabledNotificationEnabled = true
        cookiesDisabledCaption = "Cookies disabled"
        cookiesDisabledMessage = "This application requires cookies to function.
                                 Please enable cookies in your browser and click here to try again.
*/
@SuppressWarnings("serial")
public class MmowgliSystemMessages extends CustomizedSystemMessages
{
  public MmowgliSystemMessages()
  {
    setSessionExpiredCaption("Your <i>Mmowgli</i> session expired");
    setSessionExpiredMessage("<center>Click this message to sign in again.</center>");

    setCookiesDisabledMessage("This application requires cookies to function."+
        "  Please enable cookies in your browser and click here to try again.");
  }
  /*********** session expired ************/
  //setSessionExpiredURL(String sessionExpiredURL)
  //setSessionExpiredNotificationEnabled(boolean sessionExpiredNotificationEnabled)
  //setSessionExpiredCaption(String sessionExpiredCaption)
  //setSessionExpiredMessage(String sessionExpiredMessage)

  /*************** authentication ******************/
  //setAuthenticationErrorURL(String authenticationErrorURL)
  //setAuthenticationErrorNotificationEnabled(boolean authenticationErrorNotificationEnabled)
  //setAuthenticationErrorCaption(String authenticationErrorCaption)
  //setAuthenticationErrorMessage(String authenticationErrorMessage)

  /************ communication ***************/
  //setCommunicationErrorURL(String communicationErrorURL)
  //setCommunicationErrorNotificationEnabled(boolean communicationErrorNotificationEnabled)
  //setCommunicationErrorCaption(String communicationErrorCaption)
  //setCommunicationErrorMessage(String communicationErrorMessage)

  /********** internal error *************/
  //setInternalErrorURL(String internalErrorURL)
  //setInternalErrorNotificationEnabled(boolean internalErrorNotificationEnabled)
  //setInternalErrorCaption(String internalErrorCaption)
  //setInternalErrorMessage(String internalErrorMessage)

  /******** Out of sync **********/
  //setOutOfSyncURL(String outOfSyncURL)
  //setOutOfSyncNotificationEnabled(boolean outOfSyncNotificationEnabled)
  //setOutOfSyncCaption(String outOfSyncCaption)
  //setOutOfSyncMessage(String outOfSyncMessage)

  /********* Cookies *********/
  //setCookiesDisabledURL(String cookiesDisabledURL)
  //setCookiesDisabledNotificationEnabled(boolean cookiesDisabledNotificationEnabled)
  //setCookiesDisabledCaption(String cookiesDisabledCaption)
  //setCookiesDisabledMessage(String cookiesDisabledMessage)
}
