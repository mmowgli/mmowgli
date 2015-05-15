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

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;

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
  /*
    // CAC Card constants
  public static final String CAC_CLIENT_DN_HEADER = "SSL_CLIENT_S_DN";   //Request headers: SSL_CLIENT_S_DN = /C=US/O=U.S. Government/OU=DoD/OU=PKI/OU=USN/CN=BAILEY.JOSEPH.M.1254218711
  public static final String CAC_CLIENT_VERIFY_HEADER = "SSL_CLIENT_VERIFY"; //Request headers: SSL_CLIENT_VERIFY = SUCCESS
  public static final String VERIFY_SUCCESS = "SUCCESS";
  public static final String CAC_CERT_HEADER = "SSL_CLIENT_CERT";
  // http://www.oid-info.com/
  public static final String OID_COUNTRY_NAME = "2.5.4.6";         //US
  public static final String OID_ORGANIZATION_NAME = "2.5.4.10";  //U.S. Government
  public static final String OID_ORGANIZATION_UNIT_NAME = "2.5.4.11"; // Dod and PKI and USN
  public static final String OID_COMMON_NAME_3 = "2.5.4.3"; //DOD EMAIL CA-31  and BAILEY.JOSEPH.M.1254218711
  public static final String OID_SUBJECT_ALT_NAME = "2.5.29.17";  //email address
  public static final String OID_CITIZENSHIP = "1.3.61.5.57.9.4";

   */
  
  class CacValues {
    public String email;
    public String country;
    public String firstname;
    public String lastname;
  
  }
  @SuppressWarnings("rawtypes")
  private void parseCert(String cert)
  {
    cert = cert.replace(' ','\r');
    cert = cert.replace("BEGIN\rCERTIFICATE","BEGIN CERTIFICATE");
    cert = cert.replace("END\rCERTIFICATE", "END CERTIFICATE");
    PEMParser pr = new PEMParser(new StringReader(cert));
    try {
      Object o = pr.readObject();
      pr.close();
      if(o instanceof X509CertificateHolder) {
        X509CertificateHolder x509 = (X509CertificateHolder)o;
        
        X500Name x500name = x509.getSubject();

        RDN rdns[] = x500name.getRDNs();
        for(RDN rdn : rdns) {
           AttributeTypeAndValue[] tandV = rdn.getTypesAndValues();
           for(AttributeTypeAndValue tv : tandV) {
             System.out.println(IETFUtils.valueToString(tv.getType()));
             System.out.println(IETFUtils.valueToString(tv.getValue()));
           }
        }
                
        System.out.println("X509 version: "+x509.getVersionNumber());
        System.out.println("X509 Serial num: "+x509.getSerialNumber());
        System.out.println("X509 Sig algo: "+x509.getSignatureAlgorithm());
        System.out.println("X509 Issuer: "+x509.getIssuer());
        System.out.println("X509 Not before: "+x509.getNotBefore());
        System.out.println("X509 Not after: "+x509.getNotAfter());
        System.out.println("X509 Subject: "+x509.getSubject());
        System.out.println("X509 Subect Public Key Info: "+x509.getSubjectPublicKeyInfo());
        
        System.out.println("CriticalExtensionOIDs: ");
        Set set = x509.getCriticalExtensionOIDs();
        Iterator itr = set.iterator();
        while(itr.hasNext()) {
          ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)itr.next();
          System.out.println(oid.toString()+" : "+x509.getExtension(oid));
        }
          
        System.out.println("NonCriticalExtensionOIDs: ");
        set = x509.getNonCriticalExtensionOIDs();
        itr = set.iterator();
        while(itr.hasNext()) {
          ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)itr.next();
          System.out.println(oid.toString()+" : "+x509.getExtension(oid));
        }
        
        System.out.println("Other api: getExtensionOIDs");
        List lis = x509.getExtensionOIDs();
        itr = lis.iterator();
        while(itr.hasNext()) {
          ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)itr.next();
          System.out.println(oid.toString()+" : "+x509.getExtension(oid));
        }
       
        System.out.println("From the extensions \"block\"");
        Extensions exts = x509.getExtensions();
        ASN1ObjectIdentifier[] ids = exts.getExtensionOIDs();
        for(ASN1ObjectIdentifier oid : ids) {
          org.bouncycastle.asn1.x509.Extension ext = exts.getExtension(oid);
          System.out.println(oid.toString()+": "+ext.getParsedValue());
        }
      }
    }
    catch(IOException ex) {
      System.err.println(ex);
    }

  }
  @Override
  public void sessionInit(SessionInitEvent event) throws ServiceException
  {  
    VaadinRequest req = event.getRequest();
    String client;
    String cert;
    String val =  req.getHeader(CAC_CLIENT_VERIFY_HEADER);
    cactest: {
      if(val != null) {
        if(val.equals(VERIFY_SUCCESS)) {
          client = req.getHeader(CAC_CLIENT_DN_HEADER);
          if(client != null) {
            cert = req.getHeader(CAC_CERT_HEADER);
            if(cert != null) {
              parseCert(cert);
            }
            break cactest;
          }
        }
      }
    // here if we failed CAC
      // todo, check if game is configured as 1)requiring CAC, 2)allowing CAC, or not using CAC
    }
    
    
   Enumeration<String> en = event.getRequest().getHeaderNames();
    while(en.hasMoreElements()) {
      String hdr = en.nextElement();
      String valu = event.getRequest().getHeader(hdr);
      System.out.println("Request header "+hdr+" = "+valu);
    }

    new MmowgliSessionGlobals(event,this);   // Initialize global object across all users windows, gets stored in VaadinSession object referenced in event
    event.getSession().addUIProvider(new Mmowgli2UIProvider());
    
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
