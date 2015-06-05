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

import static edu.nps.moves.mmowgli.MmowgliConstants.SYSTEM_LOGS;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;

import edu.nps.moves.mmowgli.CACManager.CACData;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * Mmowgli2VaadinServlet.java
 * Created on Jan 22, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * Different from mmowgli 1 / vaadin 6, this class will have minimal vaadin-related code.
 * All possible moved to AppMaster
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
/*
 * @WebServlet possibilities:
 *   name           default ""
 *   value          default {}
 *   urlPatterns    default {}
 *   loadOnStartup  default -1
 *   initParams     default {}
 *   asyncSupported default false
 *   smallIcon      default ""
 *   largeIcon      default ""
 *   description    default ""
 *   displayName    default ""
 *   some done in web.xml
 */
/*
 * @VaadinServletConfiguration possibilities:
 *  productionMode
 *  resourceCacheTime       The time resources can be cached in the browser, in seconds. The default value is 3600 seconds, i.e. one hour.
 *  heartbeatInterval     The number of seconds between heartbeat requests of a UI, or a non-positive number if heartbeat is disabled. The default value is 300 seconds, i.e. 5 minutes.
 *  closeIdleSessions Whether a session should be closed when all its open UIs have been idle for longer than its configured maximum inactivity time. The default value is false.
 *  ui
 *  legacyPropertyToString
 *  
 *  Some notes:
 *    initParams = {@WebInitParam(name="org.atmosphere.useWebSocketAndServlet3",  value="true")} )
                 //{@WebInitParam(name="org.atmosphere.useNative",  value="true")})
                 //@WebInitParam(name="org.atmosphere.cpr.AtmosphereInterceptor",value="edu.nps.moves.mmowgli.MmowgliAtmosphereInterceptor")
// AsyncSupported not being recognized? see http://stackoverflow.com/questions/7749350/illegalstateexception-not-supported-on-asynccontext-startasyncreq-res
// Atmosphere parameters are listed in org.atmosphere.cpr.ApplicationConfig
// Streaming plus tomcat 7, see https://vaadin.com/wiki/-/wiki/Main/Working%20around%20push%20issues
*/

@SuppressWarnings("serial")

@WebServlet(value = "/*", loadOnStartup=1, asyncSupported=true)// the "/" means only urls at the context root (Mmowgli2/) come here,  default is /*
@VaadinServletConfiguration(heartbeatInterval=300, closeIdleSessions=true, ui = Mmowgli2UILogin.class, productionMode = false)
// (heartbeat of 300 is Vaadin default....5 min)
// Settings in web.xml (are supposed to) override those listed here

public class Mmowgli2VaadinServlet extends VaadinServlet implements SessionInitListener, SessionDestroyListener
{
  private AppMaster appMaster;
  private int sessionCount = 0;

  
  // Both the constructor and the servletInitialized method get called first only on first browser access, unless load-on-startup=true
  public Mmowgli2VaadinServlet()
  {
    MSysOut.println(SYSTEM_LOGS,"Mmowgli2VaadinServlet().....");
  }
  
  @Override
  protected void servletInitialized() throws ServletException
  {
    super.servletInitialized();
    
    getService().addSessionInitListener(this);
    getService().addSessionDestroyListener(this);

    ServletContext context = getServletContext();
    appMaster = AppMaster.instance(this,context);// Initialize app master, global across on user sessions on this cluster node
    context.setAttribute(MmowgliConstants.APPLICATION_MASTER_ATTR_NAME, appMaster);
    appMaster.init(context);
    
    //You can set the system message provider in the servletInitialized() method of a custom
    //servlet class, for example as follows:
    getService().setSystemMessagesProvider(
      new SystemMessagesProvider()
      {
        @Override
        public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo)
        {
          return new MmowgliSystemMessages();
       }
    });
  }

  @Override
  public void sessionInit(SessionInitEvent event) throws ServiceException
  {  
    MmowgliSessionGlobals globs = new MmowgliSessionGlobals(event,this);   // Initialize global object across all users windows, gets stored in VaadinSession object referenced in event
    event.getSession().addUIProvider(new Mmowgli2UIProvider());
    
    // Support CAC card
    VaadinRequest req = event.getRequest();
    CACData cData = CACManager.findCAC(req);
    req.setAttribute(CACData.class.getName(),cData);
    globs.setCACInfo(cData);
    
    MSysOut.println(SYSTEM_LOGS,"JMETERdebug: Session created, id = "+event.getSession().hashCode());
    if(appMaster != null)  {// might be with error on startup
      appMaster.doSessionCountUpdate(incrementSessionCount()); // remove after the following works
      appMaster.logSessionInit(event);     
    }
  }

  /*
   This gets entered 1) after user actively quits and session.close() is called; and 2) when a timeout event from tomcat happens.
   */
  @Override
  public void sessionDestroy(SessionDestroyEvent event)
  {
    MSysOut.println(SYSTEM_LOGS,"JMETERdebug: Session destroyed, id = "+event.getSession().hashCode()); 
    MmowgliSessionGlobals globs = event.getSession().getAttribute(MmowgliSessionGlobals.class);
    if(globs != null)
    	globs.vaadinSessionClosing();
    
    if(appMaster != null) { // might be with error on startup
      appMaster.doSessionCountUpdate(decrementSessionCount());
      if(globs != null)
        appMaster.sessionEndingFromTimeoutOrLogout(globs, event.getSession());
    }  
  }
  
  private int incrementSessionCount()
  {
    return ++sessionCount;
  }
  
  private int decrementSessionCount()
  {
    if(sessionCount <= 0)
      sessionCount = 1; // for the line below
    return --sessionCount;
  }

/* Methods to override if needed
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{}
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{}
  protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{}
  protected void doOptions(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException{}
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{}
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{}
  protected void doTrace(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException{}
  protected long getLastModified(HttpServletRequest req){}
  public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException{}
  public String getInitParameter(String name){}
  public Enumeration<String> getInitParameterNames(){}
  public ServletConfig getServletConfig(){}
  public ServletContext getServletContext(){}
  public String getServletInfo(){}
  public String getServletName(){}
  public void init() throws ServletException{}
  public void log(String message, Throwable t){}
  public void log(String msg){}
*/
}
