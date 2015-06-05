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

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;
import javax.net.ssl.*;
import javax.servlet.ServletContext;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.vaadin.server.*;
import com.vaadin.shared.Version;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.UI;

import edu.nps.moves.mmowgli.cache.MCacheManager;
import edu.nps.moves.mmowgli.components.BadgeManager;
import edu.nps.moves.mmowgli.components.KeepAliveManager;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.export.ReportGenerator;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.imageServer.ImageServlet;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.messaging.InterTomcatIO;
import edu.nps.moves.mmowgli.messaging.MMessagePacket;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.modules.scoring.ScoreManager2;
import edu.nps.moves.mmowgli.utility.*;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
import edu.nps.moves.mmowgliMobile.MmowgliMobileVaadinServlet;

/**
 * AppMaster.java Created on Jan 22, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class AppMaster
{
  private ServletContext servletContext;
  
  //private InstancePollerThread instancePollerThread;
  private MiscellaneousMmowgliTimer miscTimer;
  private BadgeManager badgeManager;
  private MailManager mailManager;

  private MCacheManager mCacheManager;
  private ReportGenerator reportGenerator;

  private String appUrlString = ""; // gets setup before any logons, then
                                    // completed on first login
  private URL appUrl;
  
  private KeepAliveManager keepAliveManager;
  private VHib vaadinHibernate;
  private VHibPii piiHibernate;

  private AppMasterMessaging appMasterMessaging;

  private String gameImagesUrlString;
  private String userImagesFileSystemPath;
  private String userImagesUrlString;
  private URL    userImagesUrl;
  
  private ClusterMasterController clusterMasterMaster;
  
  public static int sysOutLogLevel = 0; //ALL_LOGS;
  static {
    //sysOutLogLevel |= BROADCASTER_LOGS;
    //sysOutLogLevel |= BADGEMANAGER_LOGS;
    sysOutLogLevel |= CARD_UPDATE_LOGS;
    sysOutLogLevel |= DB_LISTENER_LOGS;
    //sysOutLogLevel |= HIBERNATE_LOGS;
    //sysOutLogLevel |= HIBERNATE_SESSION_LOGS;
    //sysOutLogLevel |= MCACHE_LOGS;
    //sysOutLogLevel |= MESSAGING_LOGS;
    //sysOutLogLevel |= PUSH_LOGS;
    sysOutLogLevel |= TICK_LOGS;
    sysOutLogLevel |= USER_UPDATE_LOGS;
    sysOutLogLevel |= SYSTEM_LOGS;
    sysOutLogLevel |= ACTIONPLAN_UPDATE_LOGS;
    sysOutLogLevel |= REPORT_LOGS;
    //sysOutLogLevel |= JMS_LOGS;
    sysOutLogLevel |= ERROR_LOGS;
    //sysOutLogLevel |= MISC_LOGS;
    //sysOutLogLevel |= MAIL_LOGS;
    //sysOutLogLevel |= MOBILE_LOGS;
    sysOutLogLevel |= NEWUSER_CREATION_LOGS;
    //sysOutLogLevel |= DEBUG_LOGS;
    
    MSysOut.println(SYSTEM_LOGS,"System log level = "+sysOutLogLevel);
  }
  private static AppMaster myInstance = null;
  private static boolean initted = false;
  
  public static AppMaster instance(VaadinServlet servlet, ServletContext context)
  {
    if (myInstance == null) {
      myInstance = new AppMaster(servlet,context);
      myInstance.init2();
    }
    return myInstance;
  }

  public static AppMaster instance()
  {
    if (myInstance == null)
      throw new RuntimeException("AppMaster must be initialized from servlet");
    return myInstance;
  }

  private AppMaster(VaadinServlet vservlet, ServletContext context)
  {
    MSysOut.println(SYSTEM_LOGS,"Running Vaadin "+Version.getFullVersion());

    servletContext = context;
    
    setConstants();

    appMasterMessaging = new AppMasterMessaging(this);
    new MmowgliEncryption(context); // initializes the singleton
    trustAllCerts();

    mailManager = new MailManager();
  }
  
  // called after the instance variable is set up
  private void init2()
  {
    // JMS keep-alive monitor
    Long keepAliveInterval = null;
    String kaIntv = servletContext.getInitParameter(WEB_XML_JMS_KEEPALIVE_KEY);
    if (kaIntv != null) {
      try {
        keepAliveInterval = Long.parseLong(kaIntv);
      }
      catch (NumberFormatException ex) {
        System.err.println("Bad format for long jmsKeepAliveIntervalMS in web.xml");
      }
    }
    keepAliveManager = new KeepAliveManager(this, keepAliveInterval); // latter maybe null
    miscTimer = new MiscellaneousMmowgliTimer();
  }
  
  //@formatter:off
  private ClusterMasterController buildClusterMaster()
  {
    String arbiter = getInitParameter(WEB_XML_CLUSTERMASTER_ARBITER_KEY);
    if (arbiter == null)
      return new ClusterMasterController.SingleDeployment();

    String fullClassName = ClusterMasterController.class.getName();
    Class<?> arbiterCls = null;
    try { arbiterCls = Class.forName(fullClassName + "$" + arbiter); } catch (ClassNotFoundException ex) {}
    
    if (arbiterCls == null) {
      try {
        arbiterCls = Class.forName(arbiter);  // for grins
      }
      catch (ClassNotFoundException ex) {
        MSysOut.println(ERROR_LOGS, "ClusterMasterArbiter class not found, using SingleDeployment instead");
        return new ClusterMasterController.SingleDeployment();
      }
    }
    // Here if we found a class to work with
    ClusterMasterController cntl;
    try {
      Constructor<?> constr = arbiterCls.getConstructor((Class<?>[]) null);
      cntl = (ClusterMasterController) constr.newInstance((Object[]) null);
      return cntl;
    }
    catch (Exception ex) {
      MSysOut.println(ERROR_LOGS, "Could not instantiate ClusterMasterArbiter, using SingleDeployment instead");
      return new ClusterMasterController.SingleDeployment();
    }
  }
  //@formatter:on
  
  public boolean amIClusterMaster()
  {
    return clusterMasterMaster.amIMaster();
  }
  
  public String getMasterLockPath()
  {
    String s = getInitParameter(WEB_XML_CLUSTERMASTER_LOCK_PATH_KEY);
    if(s != null) {
      s = replaceTokens(s);
    }
    return s;
  }
  
  public String getInitParameter(String s)
  {
    String ret = servletContext.getInitParameter(s);
    MSysOut.println(SYSTEM_LOGS,"Web.xml key "+s+" returns "+ret);
    return ret;
  }
  
  private void tweekPushTransport()
  {
    String transportstr = getInitParameter(WEB_XML_PUSH_TRANSPORT_KEY);
    if (transportstr != null) {
      try {
        PUSHTRANSPORT = Enum.valueOf(Transport.class, transportstr.toUpperCase());
      }
      catch (IllegalArgumentException ex) {
        for (Transport tr : Transport.values()) {
          if (tr.getIdentifier().equalsIgnoreCase(transportstr)) {
            PUSHTRANSPORT = tr;
            return;
          }
        }
        System.err.println("************Bad value for config-parameter 'transport'=" + transportstr + "************************");
      }
    }
  }
  
  private void setConstants()
  {
    VAADIN_BUILD_VERSION = Version.getFullVersion(); // 7.3.0
    
    String s = getInitParameter(WEB_XML_SMTP_HOST_KEY);
    if (s != null && s.length() > 0)
      MmowgliConstants.SMTP_HOST = s;

  //@formatter:off
    JMS_INTERNODE_URL               = getInitParameter(WEB_XML_JMS_URL_KEY);
    JMS_INTERNODE_TOPIC             = getInitParameter(WEB_XML_JMS_TOPIC_KEY);

    DEPLOYMENT_TOKEN                = getInitParameter(WEB_XML_DEPLOYMENT_TOKEN_KEY);
    GAME_URL_TOKEN                  = getInitParameter(WEB_XML_GAME_URL_TOKEN_KEY);
    DEPLOYMENT                      = getInitParameter(WEB_XML_DEPLOYMENT_KEY);
    GAME_IMAGES_URL_RAW             = getInitParameter(WEB_XML_GAME_IMAGES_URL_KEY);
    USER_IMAGES_URL_RAW             = getInitParameter(WEB_XML_USER_IMAGES_URL_KEY);
    USER_IMAGES_FILESYSTEM_PATH_RAW = getInitParameter(WEB_XML_USER_IMAGES_FILESYSTEM_PATH_KEY);

    REPORTS_FILESYSTEM_PATH_RAW     = getInitParameter(WEB_XML_REPORTS_FILESYSTEM_PATH_KEY);
    REPORTS_URL_RAW                 = getInitParameter(WEB_XML_REPORTS_URL_KEY);
    
    REPORT_TO_IMAGE_URL_PREFIX      = getInitParameter(WEB_XML_REPORTS_TO_IMAGES_RELATIVE_PATH_PREFIX);
  //@formatter:on
    
    REPORTS_FILESYSTEM_PATH = replaceTokens(REPORTS_FILESYSTEM_PATH_RAW);
    new File(REPORTS_FILESYSTEM_PATH).mkdirs();
     
    
    userImagesFileSystemPath = replaceTokens(USER_IMAGES_FILESYSTEM_PATH_RAW);
    
    computeUrls();
    
    tweekPushTransport();
    
    setClamScanConstants(servletContext);
    
     try {
      InputStream is = getClass().getResourceAsStream(MMOWGLI_BUILD_PROPERTIES_PATH);
      Properties prop = new Properties();
      prop.load(is);
      MMOWGLI_BUILD_ID = prop.getProperty(MMOWGLI_BUILD_ID_KEY);
    }
    catch (IOException ioe) {
      System.err.println("Build id could not be retrieved: " + ioe.getLocalizedMessage());
    }
  }
  
  // This is also done later, because the appUrlString can't be gotten in V7 until page is opened.
  private void computeUrls()
  {
    REPORTS_URL = replaceTokens(REPORTS_URL_RAW);   
    gameImagesUrlString = replaceTokens(GAME_IMAGES_URL_RAW);
    userImagesUrlString = replaceTokens(USER_IMAGES_URL_RAW);
  }
  
  private String replaceTokens(String s)
  {
    String ret = s.replace(DEPLOYMENT_TOKEN,  DEPLOYMENT);
    return ret.replace(GAME_URL_TOKEN, appUrlString);
  }
  
  public MailManager getMailManager()
  {
    return mailManager;
  }

  public String getAppUrlString()
  {
    return appUrlString;
  }

  public URL getAppUrl()
  {
     return appUrl;
  }

  public void oneTimeSetAppUrlFromUI()
  {
    MSysOut.println(SYSTEM_LOGS,"AppMaster.oneTimeSetAppUrlFromUI()");
    if(appUrlString == null || appUrlString.length()<=0) {
      try {
        URL url = Page.getCurrent().getLocation().toURL();
        url = new URL(url.getProtocol(),url.getHost(),url.getPort(),url.getFile());  //lose any query bit
        appUrl = url;
        appUrlString = url.toString();
        if(appUrlString.endsWith("/") || appUrlString.endsWith("\\"))
          appUrlString = appUrlString.substring(0, appUrlString.length()-1);       

        computeUrls();
        MSysOut.println(SYSTEM_LOGS,"AppMaster.oneTimeSetAppUrlFromUI() url set to "+appUrlString);
      }
      catch(MalformedURLException ex) {
        System.err.println("Can't form App URL in AppMaster.oneTimeSetAppUrlFromUI()");
      }
    }
    else {
      MSysOut.println(SYSTEM_LOGS,"AppMaster.oneTimeSetAppUrlFromUI() url already set, currently: "+appUrlString);      
    }
  }
  
  // todo combine,
  public static String getUrlString()
  {
    String rets = null;
    try {
      URL url = Page.getCurrent().getLocation().toURL();
      if(url.getPort() == 80 || url.getPort() == 443)
        url = new URL(url.getProtocol(),url.getHost(),url.getFile());  //lose any query bit
      else
        url = new URL(url.getProtocol(),url.getHost(),url.getPort(),url.getFile());
      String urlString = url.toString();
      if(urlString.endsWith("/")|| urlString.endsWith("\\"))
        urlString = urlString.substring(0, urlString.length()-1);
      
      rets = urlString;
    }
    catch(MalformedURLException ex) {
      System.err.println("Can't form App URL in AppMaster.oneTimeSetAppUrlFromUI()");
    }
    return rets;
  }
  
  public void init(ServletContext context)
  {
    piiHibernate = VHibPii.instance();
    piiHibernate.init(context);
    
    vaadinHibernate = VHib.instance();
    vaadinHibernate.init(context);

    vaadinHibernate.installDataBaseListeners();
    
    mCacheManager = MCacheManager.instance();
    
    clusterMasterMaster = buildClusterMaster();  // can be a race here
    handleClusterMaster(amIClusterMaster());     // and here

    GameEventLogger.logApplicationLaunch();

    startThreads();
    
    initted = true;
    MSysOut.println(SYSTEM_LOGS,"Out of AppMaster.init");
  }
  
  public static boolean isInitted()
  {
    return initted;
  }
  
  private Object clusterMasterSyncher = new Object();  // must sync because the zookeeper listener can jump in
  
  private boolean oneTimeTasksDone = false;
  private boolean changedTasksDone = false;
  
  public void handleClusterMaster(boolean itsMe)
  {
    synchronized(clusterMasterSyncher) {
      if(itsMe) {
        if(!oneTimeTasksDone) {
          doClusterMasterOneTimeTasks();
          oneTimeTasksDone = true;
        }
        if(!changedTasksDone) {
          doClusterMasterChangedTasks();
          changedTasksDone = true;
        }
      }
      else {
        if(changedTasksDone) {
          undoClusterMasterChangedTasks();
          changedTasksDone = false;
        }
      }
    }
  }
 
  private void doClusterMasterOneTimeTasks()
  {
    handleMoveSwitchScoring();
  }
  
  private void doClusterMasterChangedTasks()
  {
    startBadgeManager();
    startAutomaticReportGeneration();
  }
  
  private void undoClusterMasterChangedTasks()
  {
    stopBadgeManager();
    stopAutomaticReportGeneration();
  }
  
  /**
   * Called after the db has been setup; We need to read game table to see if we
   * should be the badgemanager among clusters.
   */
  public void startBadgeManager()
  {
    badgeManager = new BadgeManager(this);
    MSysOut.println(BADGEMANAGER_LOGS,"Badge Manager instantiated on " + AppMaster.instance().getServerName());
    // miscStartup(context);
  }
  
  public void stopBadgeManager()
  {
    if(badgeManager != null)
      badgeManager.kill();
    badgeManager = null;
    MSysOut.println(BADGEMANAGER_LOGS,"Badge Manager killed on " + AppMaster.instance().getServerName());
  }
  
  public void startAutomaticReportGeneration()
  {
    reportGenerator = new ReportGenerator(this);
    MSysOut.println(REPORT_LOGS,"Report generator launched on " + AppMaster.instance().getServerName()); 
  }
  
  public void stopAutomaticReportGeneration()
  {
    if(reportGenerator != null)
      reportGenerator.kill();
    reportGenerator = null;
    MSysOut.println(REPORT_LOGS,"Report generator killed on " + AppMaster.instance().getServerName());      
  }
  
  /**
   * This puts all scores from the userscore/move table into the basicScore field in the user object.  They are duplicates, but the
   * one in the table is required for table sorting.
   * Done once per startup only on cluster master
   * @param context
   */
  @SuppressWarnings("unchecked")
  public void handleMoveSwitchScoring()
  {
    HSess.init();
    Session sess = HSess.get();

    Game game = (Game) sess.get(Game.class, 1L);
    if (game.getCurrentMove().getNumber() != game.getLastMove().getNumber()) {
      MSysOut.println(SYSTEM_LOGS, "AppMaster setting up user points for new move number " + game.getCurrentMove().getNumber());
      List<User> users = (List<User>) sess.createCriteria(User.class).list();
      for (User u : users) {
        u.setBasicScoreOnly(ScoreManager2.getBasicPointsFromCurrentMove(u, sess)); // needed for table sorting
        u.setInnovationScoreOnly(ScoreManager2.getInnovPointsFromCurrentMove(u, sess));
        sess.update(u);
      }
      game.setLastMove(game.getCurrentMove());
      sess.update(game);
    }
    HSess.close();
  }

  private void startThreads()
  {
    getInterNodeIO(); // may fail will get retried in sender thread
    
    // poller  no longer used with new session report
//    instancePollerThread = new InstancePollerThread("Instance Poller");
//    instancePollerThread.setPriority(Thread.NORM_PRIORITY);
//    instancePollerThread.setDaemon(true); // allow tomcat to kill the app w/ no
//                                          // warnings
//    instancePollerThread.start();
  }

  private void trustAllCerts()
  {
    // Lifted from http://www.exampledepot.com/egs/javax.net.ssl/TrustAll.html
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
    {
      @Override
      public java.security.cert.X509Certificate[] getAcceptedIssuers()
      {
        return null;
      }

      @Override
      public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
      {
      }

      @Override
      public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
      {
      }
    } };

    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    catch (Exception e) {
      System.err.println("Error installing \"All-trusting SSL trust manager\" : " + e.getClass().getSimpleName() + " / " + e.getLocalizedMessage());
    }
  }

  public String getServerName()
  {
    String name = "unknown";
    try {
      InetAddress addr = InetAddress.getLocalHost();
      name = addr.getHostName();
    }
    catch (Exception e) {
      System.err.println("Can't look up host name in ApplicationMaster");
    }
    return name;

  }

  private void setClamScanConstants(ServletContext context)
  {
    PATH_TO_CLAMSCAN_VIRUS_SCANNER = context.getInitParameter(WEB_XML_CLAMSCAN_VIRUS_SCANNER_PATH);
    if (PATH_TO_CLAMSCAN_VIRUS_SCANNER == null)
      return;

    String argKEY = WEB_XML_CLAMSCAN_ARGUMENT;
    String arg = context.getInitParameter(argKEY);
    if (arg == null)
      return;

    Vector<String> vec = new Vector<String>();
    vec.add(arg);
    int i = 1;
    while ((arg = context.getInitParameter(argKEY + i)) != null) {
      vec.add(arg);
      i++;
    }
    CLAMSCAN_ARGUMENTS = vec.toArray(new String[vec.size()]);
  }

  public boolean sendJmsMessage(char jmskeepalive, String serializeMsg)
  {
    return true; // todo V7
  }

  /* May return null if can't do it yet */
  public InterTomcatIO getInterNodeIO()
  {
    return appMasterMessaging.getInterTomcatIO();
  }

  public MCacheManager getMcache()
  {
    return mCacheManager;
  }

  public BadgeManager getBadgeManager()
  {
    return badgeManager;
  }

  public KeepAliveManager getKeepAliveManager()
  {
    return keepAliveManager;
  }

  public MiscellaneousMmowgliTimer getMiscTimer()
  {
    return miscTimer;
  }

  /*
   * Instance message format: servername, clientip, "userid " userid, uuid
   * Not used with new session report
   */
  /*
  class InstancePollerThread extends Thread
  {
    public boolean killed = false;

    public InstancePollerThread(String name)
    {
      super(name);
      getInterNodeIO().addReceiver(new JmsReceiver()
      {
        @Override
        public boolean handleIncomingTomcatMessageTL(MMessagePacket pkt)
        {
          if (pkt.msgType == INSTANCEREPORT) {
            MSysOut.println(SYSTEM_LOGS,"Instance report received: " + pkt.msg);
            AppMaster.this.logPollReport(pkt.msg);
          }
          return false;
        }

        @Override
        public void handleIncomingTomcatMessageEventBurstCompleteTL()
        {
        }
      });
    }

    @Override
    public void run()
    {
      while (true) {
        try {
          Thread.sleep(INSTANCEPOLLERINTVERVAL_MS); // 5 minutes

          InterTomcatIO sessIO = getInterNodeIO();
          if (sessIO != null) {
            AppMaster me = AppMaster.instance();
            MSysOut.println(SYSTEM_LOGS,me.getServerName() + " ApplicationMaster requesting instances to respond with \"YES-IM_AWAKE\"");
            AppMaster.this.resetPollReports();
            // sessIO.send(INSTANCEREPORTCOMMAND, AppMaster.getServerName() +
            // "\n","");// add EOMessage token
            Broadcaster.broadcast(new MMessagePacket(INSTANCEREPORTCOMMAND, me.getServerName() + "\n", "", // ui_id
                "", // session_id
                me.getServerName())); // tomcat_id
          }
        }
        catch (InterruptedException intEx) {
          if (killed)
            return;
          else
            ; // System.out.println("Thread interrupted but not killed"); just got nudged
        }
      }
    }
  }

  private HashSet<PollReport> pollReports = new HashSet<PollReport>();

  private Pattern regex = Pattern.compile("(.*),(.*),(.*),\\s*userid\\s*(.*)");

  private void logPollReport(String msg)
  {
    Matcher m = regex.matcher(msg);
    if (m.matches()) {
      if (m.groupCount() == 4) {
        String svr = m.group(1);
        String brw = m.group(2);
        String ip = m.group(3);
        String uid = m.group(4);
        uid = uid.equals("-1") ? "--" : uid;
        // String uuid = m.group(5);
        synchronized (pollReports) {
          pollReports.add(new PollReport(svr, brw, ip, uid));// ,uuid));
        }
        return;
      }
    }
    System.err.println("Poll report format error: " + msg);
  }

  public String[][] getPollReport()
  {
    synchronized (pollReports) {
      String[][] oa = new String[pollReports.size()][];
      int count = pollReports.size();
      int i = 0;
      Iterator<PollReport> itr = pollReports.iterator();
      while (itr.hasNext() && i < count) {
        PollReport pr = itr.next();
        oa[i++] = new String[] { pr.server, pr.browser, pr.clientIP, pr.userid };
      }
      return oa;
    }
  }

  private void resetPollReports()
  {
    synchronized (pollReports) {
      pollReports.clear();
      ;
    }
  }

  class PollReport
  {
    public String server;
    public String clientIP;
    public String userid;
    public String browser;

    public PollReport(String server, String browser, String clientIP, String userid)
    {
      this.server = server;
      this.browser = browser;
      this.clientIP = clientIP;
      this.userid = userid;
    }
  }
*/
  
  // Called from AppMasterMessaging on receipt of a rebuild_reports message
  public void pokeReportGenerator()
  {
    if (reportGenerator != null) {
    	MSysOut.println(SYSTEM_LOGS,"AppMaster.pokeReportGenerator()");
      reportGenerator.poke();
    }
  }
  
  // Called from the Game Admin menu to get the reports to be be rebuild
  public void requestPublishReportsTL()
  {
    if(reportGenerator != null)  // if we're the master...
      reportGenerator.poke();
    else
      appMasterMessaging.doRebuildReportsRequestTL();  // sends requests to other AppMasters  
  }

  public static String getAlternateVideoUrlTL()
  {
    return getAlternateVideoUrl(HSess.get());
  }

  public static String getAlternateVideoUrl(Session sess)
  {
    Game g = Game.get(sess);
    StringBuilder sb = new StringBuilder();
    sb.append("http://portal.mmowgli.nps.edu/");

    String acro = g.getAcronym();
    if (acro == null || acro.length() <= 0)
      sb.append("game-wiki/-/wiki/PlayerResources/Video+Resources");
    else {
      sb.append(acro);
      sb.append("-videos");
    }
    return sb.toString();
  }

  public String getGameImagesUrlString()
  {
    return gameImagesUrlString;
  }
  
  public URL getUserImagesUrl()
  {
    if(userImagesUrl == null)
      getUserImagesUrlString(); // this builds it
    
    return userImagesUrl;
  }
  
  public String getUserImagesUrlString()
  {
    try {
      if (userImagesUrlString.contains(GAME_URL_TOKEN)) {
        URL url = Page.getCurrent().getLocation().toURL();
        url = new URL(url.getProtocol(), url.getHost(), url.getFile());
        String gameUrl = url.toString();
        if (gameUrl.endsWith("/"))
          gameUrl = gameUrl.substring(0, gameUrl.length() - 1);
        userImagesUrlString = userImagesUrlString.replace(GAME_URL_TOKEN, gameUrl);
      }
      userImagesUrl = new URL(userImagesUrlString);
    }
    catch (MalformedURLException ex) {
      System.err.println("** Error constructing user images url from:" + userImagesUrlString);
    }
    return userImagesUrlString;
  }

  public String getUserImagesFileSystemPath()
  {
    return userImagesFileSystemPath;
  }

  @SuppressWarnings("unchecked")
  public String getMobileQRUrlStringTL()
  {
    String rets = "errorInAppMaster.getMobileQRUrlStringTL()"; // error default
    try {
      rets = ImageServlet.getBaseImageUrl().toURI().toString() + MOBILE_QR_IMAGE_NAME;

      List<Image> iLis = HSess.get().createCriteria(Image.class).add(Restrictions.eq("name", MOBILE_QR_IMAGE_NAME)).list();
      if(iLis == null || iLis.size() <= 0) {
        createQrImageTL();
      }
      else {
        Image img = iLis.get(0);
        if(! img.getDescription().equals(MmowgliMobileVaadinServlet.getBaseMobileUrl().toExternalForm())) {
          // deployment url has changed
          Image.deleteTL(img);
          createQrImageTL();
        }
      }
    }
    catch (Exception ex) {
      System.err.println("Program error in AppMaster.getMobileQRUrlStringTL(): " + ex.getClass().getSimpleName() + "/ " + ex.getLocalizedMessage());
    }
    return rets;
  }
  
  private void createQrImageTL() throws WriterException, IOException
  {
    Image imgObj = new Image(MOBILE_QR_IMAGE_NAME, MOBILE_QR_IMAGE_MIMETYPE);

    QRCodeWriter writer = new QRCodeWriter();
    HashMap<EncodeHintType, Object> hints = new HashMap<>();
    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
    hints.put(EncodeHintType.MARGIN, 4);

    String url = MmowgliMobileVaadinServlet.getBaseMobileUrl().toExternalForm();
    BitMatrix bm = writer.encode(url, BarcodeFormat.QR_CODE, 550, 550, hints);
    BufferedImage bi = MatrixToImageWriter.toBufferedImage(bm);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(bi, MOBILE_QR_IMAGE_FILETYPE, baos);
    baos.flush();
    imgObj.setBytes(baos.toByteArray());
    imgObj.setDescription(url);
    Image.saveTL(imgObj);
    
    @SuppressWarnings("unchecked")
		List<Media> lis = HSess.get().createCriteria(Media.class)
																.add(Restrictions.eq("source",Media.Source.DATABASE))
																.add(Restrictions.eq("url",imgObj.getName()))
																.list();
    if(lis != null) {
    	for(Media m : lis)
    		Media.deleteTL(m);
    }
    Media media = new Media();
    media.setDescription(imgObj.getName() + " in db");

    media.setSource(Media.Source.DATABASE);
    media.setUrl(imgObj.getName());
    media.setType(Media.MediaType.IMAGE);
    Media.saveTL(media);
  }
  
  /**
   * Called from the servlet listener, which keeps track of our myInstance count
   * 
   * @param sessCount
   */
  public void doSessionCountUpdate(int sessCount)
  {
    appMasterMessaging.doSessionCountUpdate(sessCount);
  }

  public int getSessionCount()
  {
    return appMasterMessaging.getSessionCount();
  }

  public Object[][] getSessionCountByServer()
  {
    return appMasterMessaging.getSessionCountByServer();
  }

  public void incomingDatabaseEvent(final MMessagePacket mMessagePacket)
  {
    appMasterMessaging.incomingDatabaseEvent(mMessagePacket);
  }

  /* This is where database listener messages come in */
  public void sendToOtherNodes(MMessagePacket mMessagePacket)
  {
    appMasterMessaging.handleIncomingSessionMessage(mMessagePacket);
  }

  public String getReportsUrl()
  {
    return REPORTS_URL;
  }

  /* This is intended to be used before redeploying */
  public void  killAllSessionsAndTellOtherNodesTL()
  {
    killAllSessionsTL();
    appMasterMessaging.sendKillAllSessionsCommand();
  }
  
  public void killAllSessionsTL()
  {
    // First send a message to everybody else
    GameLinks links = GameLinks.getTL();
    String thanks = links.getThanksForPlayingLink();
    
    Iterator<VaadinSession> itr = sessionsInThisMmowgliNode.iterator();
    while(itr.hasNext()) {
      VaadinSession sess = itr.next();
      for(UI ui : sess.getUIs()) {
        ui.getPage().setLocation(thanks);
        ui.close();
      }
      sess.close();
    }
  }
  
  public void pushPingAllSessionsInThisClusterNode()
  {
  	Iterator<VaadinSession> itr = sessionsInThisMmowgliNode.iterator();
    while(itr.hasNext()) {
      VaadinSession sess = itr.next();
      for(final UI ui : sess.getUIs()) {
      	if(!ui.isClosing() && ui instanceof Mmowgli2UI) {
          ui.access(new Runnable() {
        	  public void run() {
        		  ((Mmowgli2UI)ui).pingPush();
        		  ui.push(); // empty push to keep connection alive over Akamai
        		  MSysOut.println(TICK_LOGS,"Keep alive push, ui "+ui.hashCode());
        	  }
          });
      	}
      }
    }  	
  }
  
  private HashSet<VaadinSession> sessionsInThisMmowgliNode = new HashSet<VaadinSession>();
  
  // The synchronized methods below protect concurrent access to the hashset  
  private synchronized void removeVaadinSession(VaadinSession sess)
  {
    sessionsInThisMmowgliNode.remove(sess);   
  }
  
  private synchronized void addVaadinSession(VaadinSession sess)
  {
    sessionsInThisMmowgliNode.add(sess);    
  }
  
   /**
   * Called from servlet
   */
  public void logSessionInit(SessionInitEvent event)
  {
    addVaadinSession(event.getSession());
    sendMySessionReport();
  }
  
  /**
   * Called from servlet
   * Might be entered as a result of session.close() from controller, in which case we're mostly done.
   */ 
  @HibernateOpened
  @HibernateClosed
  public void sessionEndingFromTimeoutOrLogout(MmowgliSessionGlobals globs, VaadinSession sess)
  {
    globs.stopping=true;
    Object key = HSess.checkInit();
    
    removeVaadinSession(sess);
    sendMySessionReport();
    if(!globs.loggingOut) {
    	// Try to do this, but check...the User.get has been generating some misc errors
    	try {
        Serializable id = globs.getUserID();
        if(id != null) {
          User u = globs.getUserTL();
          if(u != null)
            GameEventLogger.logSessionTimeoutL(u);
        }
    	}
    	catch(Throwable t) {
    		MSysOut.println(ERROR_LOGS, "Error logging Session timeout, probably trying to use db: "+t.getClass().getSimpleName()+": "+t.getLocalizedMessage());
    	}
    }    
    HSess.checkClose(key);  
  }
  
  public void sendMySessionReport()
  {    
    appMasterMessaging.doSessionReportBroadcast(getLocalNodeReportRaw());
  }
  
  /******* Player reports *********/
/*  public synchronized String getCompletePlayerReportHTML()
  {
    //String s = getCompletePlayerReport();
    // temp
    return getSinglePlayerReportHTML();
  }
*/  
/*  public synchronized String getCompletePlayerReport()
  {
    return null; //todo
  }
*/  
  public synchronized StringBuilder getCompletePlayerReportRaw()
  {
    String[][] sa = appMasterMessaging.getSessionReportsByServer();
    StringBuilder sb = new StringBuilder();
    for(String[] row : sa) {
      sb.append(row[0]); // servername
      sb.append(SESSION_REPORT_ITEM_DELIMITER);
      sb.append(row[1]); // report
      sb.append(SESSION_REPORT_ITEM_DELIMITER);
    }
    return sb;
  }
 
  private String reportHTMLpre = "<html><head><style>td{padding:10px;}table {font-family:'Trebuchet MS',Arial,Helvetica,sans-serif;border:1px solid darkgrey;background-color:#CCCCCC;}</style></head><body><table><tr><td>";
  private String reportHTMLpost = "</td></tr></table>";
  public synchronized String getSinglePlayerReportHTML()
  {
    String s = getLocalNodeReport();
    s=s.replace(SESSION_REPORT_FIELD_DELIMITER, "</td><td>");
    s=s.replace(SESSION_REPORT_ITEM_DELIMITER, "</td></tr><tr><td>");
    return reportHTMLpre + s + reportHTMLpost;
  }
  
  public synchronized String getLocalNodeReport()
  {
    StringBuilder sb = getLocalNodeReportRaw();
    sb.insert(0, getSessionReportHeader());
    sb.insert(0, getServerName()+SESSION_REPORT_ITEM_DELIMITER);
    return sb.toString();  
  }
  
  int nuts=0;
  public String getSessionReportHeader()
  {
    return "<b>Server</b>\t<b>User</b>\t<b>ID</b>\t<b>Start</b>\t<b>IP</b>\t<b>Browser</b>\t<b>Version</b>\t<b>OS</b>"+SESSION_REPORT_ITEM_DELIMITER;
  }
  
  // Synchronized to protect hashmap
  public synchronized StringBuilder getLocalNodeReportRaw()
  {
    StringBuilder sb = new StringBuilder();
    Iterator<VaadinSession> itr = sessionsInThisMmowgliNode.iterator();

    while(itr.hasNext()) {
      VaadinSession sess = itr.next();
      MmowgliSessionGlobals sGlobs = sess.getAttribute(MmowgliSessionGlobals.class);
      // empty first is servername
      sb.append(SESSION_REPORT_FIELD_DELIMITER); //" User ");
      String uname = sGlobs.getUserName();
      sb.append(uname.length()<=0?"not logged in":uname);
      sb.append(SESSION_REPORT_FIELD_DELIMITER);
      Serializable id = sGlobs.getUserID();
      sb.append(id==null?" ":id);
      sb.append(SESSION_REPORT_FIELD_DELIMITER); //" at ");
      sb.append(sGlobs.getUserLoginTimeData());
      sb.append(SESSION_REPORT_FIELD_DELIMITER); //" from ");
      sb.append(sGlobs.getBrowserAddress());
      sb.append(SESSION_REPORT_FIELD_DELIMITER); //" using ");
      sb.append(sGlobs.getBrowserMiniType()); 
      sb.append(SESSION_REPORT_FIELD_DELIMITER);
      sb.append(sGlobs.getBrowserMajorVersionString());
      sb.append(SESSION_REPORT_FIELD_DELIMITER);
      sb.append(sGlobs.getBrowserOS());
      
      sb.append(SESSION_REPORT_ITEM_DELIMITER);
    }   
    return sb;
  }
  public static int SESS_RPT_SERVER_COLUMN = 0;
  public static int SESS_RPT_NAME_COLUMN = 1;
  public static int SESS_RPT_ID_COLUMN = 2;
  public static int SESS_RPT_START_COLUMN = 3;
  public static int SESS_RPT_IP_COLUMN = 4;
  public static int SESS_RPT_BROWSER_COLUMN = 5;
  public static int SESS_RPT_VERSION_COLUMN = 6;
  public static int SESS_RPT_OS_COLUMN = 7;
  
  public static int SESSION_REPORT_MAX_WIDTH = 8;
}
