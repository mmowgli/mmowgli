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

import java.util.HashMap;

import com.vaadin.shared.ui.ui.Transport;

/**
 * MmowgliConstants.java Created on Jan 22, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class MmowgliConstants
{
  // Build constants (set by AppMaster, value from edu/nps/moves/mmowgli/buildstamp.properties, which is automatically updated by Eclipse builder
  public static String MMOWGLI_BUILD_ID = "unset";
  public static String MMOWGLI_BUILD_PROPERTIES_PATH = "/edu/nps/moves/mmowgli/buildstamp.properties";
  public static String MMOWGLI_BUILD_ID_KEY = "build.datetime";
  
  public static String VAADIN_BUILD_VERSION = "unset";
  
  public static double MAP_LAT_DEFAULT = 10.919618;
  public static double MAP_LON_DEFAULT = 53.613281;
  public static int    MAP_ZOOM_DEFAULT = 5;
  
  public static String MOBILE_QR_IMAGE_NAME = "mobileqr.png";
  public static String MOBILE_QR_IMAGE_MIMETYPE = "image/png";
  public static String MOBILE_QR_IMAGE_FILETYPE = "png";
  
  // Database version matching this code
  public static long DATABASE_VERSION = 20150504; // db which matches this code
  
  public static long DATABASE_VERSION_BEFORE_EMAILPII_DIGESTS = 20130626;
  public static long DATABASE_VERSION_AFTER_EMAILPII_DIGESTS = 20130627;

  public static long DATABASE_VERSION_WITH_MOVE_PATCHED_SCORING = 20130215;
  public static long DATABASE_VERSION_WITH_CONFIGURABLE_LOGIN_BUTTONS = 20130124;
  public static long DATABASE_VERSION_WITH_EMAIL_CONFIRMATION = 20120911;
  public static long DATABASE_VERSION_WITH_HASHED_PASSWORDS = 20120718;
  public static long DATABASE_VERSION_WITH_QUICKUSERS = 20120715;

  public static String DUMMY_DATABASE_ENCRYPTION_PASSWORD = "changeMeNow!";
  public static int HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS = 10;

  public static String MMOWGLI_FRAME_WIDTH = "1100px";
  public static int MMOWGLI_FRAME_WIDTH_I = 1100;

  public static long INSTANCEPOLLERINTVERVAL_MS = 1000L * 1000 * 5; // test 60L*1000*5; // 5 minutes
  public static Long NO_LOGGEDIN_USER_ID = -1L;
  
  // Let this be set in webxml
  //public static int USER_SESSION_TIMEOUT_IN_SECONDS = 15*60;
  
  public static int GAMEMASTER_SESSION_TIMEOUT_SECONDS   = 2*60*60;   // 2 hours
  public static int ADMINSTRATOR_SESSION_TIMEOUT_SECONDS = 24*60*60;  //whole day
 
  // Servlet context constants
  // This is the attribute name in the web app servlet context which holds a
  // reference to the single ApplicationMaster instance,
  // which holds the "global" data common to all sessions (Vaadin
  // "applications") of the mmowgli web app.
  public static final String APPLICATION_MASTER_ATTR_NAME = "applicationmaster";
  public static final String APPLICATION_STARTUP_ERROR_STRING = "applicationstartuperrorstring";
  
  // Main application content
  public static String APPLICATION_SCREEN_WIDTH  = "1000px"; //"992px";  // with varying margins
  public static String HEADER_SCREEN_WIDTH = "1020px"; // for centering header
  public static String APPLICATION_CENTRAL_WIDTH = "960px";
  
  // Default, typically overridden by web.xml param.
  public static String SMTP_HOST = "mule.nps.edu";

  public static final String PORTALWIKI_URL = "https://portal.mmowgli.nps.edu/game-wiki";
  public static final String PORTALTARGETWINDOWNAME = "_portal";
  
  public static final String TWEETBUTTONEMBEDDED_0 = "<iframe allowtransparency='true' frameborder='0' scrolling='no' src='http://platform.twitter.com/widgets/tweet_button.html?count=none&url=http://portal.mmowgli.nps.edu&data-via=TruthSeal&text=";
  public static final String TWEETBUTTONEMBEDDED_1 = "' style='width:56pxx; height:20px;'></iframe>"; // just the button, otherwise: default: 130px w 50px h
  public static final String TWEETBUTTON_WIDTH = "56px";
  public static final String TWEETBUTTON_HEIGHT = "20px";
  
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
  
  // Atmosphere parameters
  public static final String ATMOS_TIMEOUT = "timeout"; // default = 300,000; max time a connection will stay open with no messages
  public static final String ATMOS_CONNECT_TIMEOUT = "connectTimeout"; // def = -1; if client fails to connect, fallbackTransport will be used
  public static final String ATMOS_RECONNECT_INTERVAL = "reconnectInterval"; // def = 0; interval in ms before an attempt to reconnect will be made
  
  // Logging
  //@formatter:off
  public static final int ALL_LOGS = -1;
  public static final int MESSAGING_LOGS              = 0x1;
  public static final int DB_LISTENER_LOGS            = 0x2;
  public static final int MCACHE_LOGS                 = 0x4;
  public static final int TICK_LOGS                   = 0x8;
  public static final int USER_UPDATE_LOGS           = 0x10;
  public static final int CARD_UPDATE_LOGS           = 0x20;
  public static final int PUSH_LOGS                  = 0x40;
  public static final int BROADCASTER_LOGS           = 0x80;
  public static final int HIBERNATE_LOGS            = 0x100;
  public static final int BADGEMANAGER_LOGS         = 0x200;
  public static final int SYSTEM_LOGS               = 0x400;
  public static final int SCOREMANAGER_LOGS         = 0x800;
  public static final int ACTIONPLAN_UPDATE_LOGS   = 0x1000;
  public static final int REPORT_LOGS              = 0x2000;
  public static final int JMS_LOGS                 = 0x4000;
  public static final int ERROR_LOGS               = 0x8000;
  public static final int MISC_LOGS               = 0x10000;
  public static final int MAIL_LOGS               = 0x20000;
  public static final int MOBILE_LOGS             = 0x40000;
  public static final int HIBERNATE_SESSION_LOGS  = 0x80000;
  public static final int NEWUSER_CREATION_LOGS  = 0x100000;
  public static final int DEBUG_LOGS             = 0x200000;
  //@formatter:on
  
  public static HashMap<Integer,String> logTokens = new HashMap<Integer,String>();
  static {
    logTokens.put(MESSAGING_LOGS, "MSG");
    logTokens.put(DB_LISTENER_LOGS,"DBL");
    logTokens.put(MCACHE_LOGS,"CAC");
    logTokens.put(TICK_LOGS,"TCK");
    logTokens.put(USER_UPDATE_LOGS,"USR");
    logTokens.put(CARD_UPDATE_LOGS,"CAR");
    logTokens.put(PUSH_LOGS,"PSH");
    logTokens.put(BROADCASTER_LOGS,"BRO");
    logTokens.put(HIBERNATE_LOGS,"HIB");
    logTokens.put(BADGEMANAGER_LOGS,"BAD"); 
    logTokens.put(SYSTEM_LOGS, "SYS");
    logTokens.put(SCOREMANAGER_LOGS, "SCO");
    logTokens.put(ACTIONPLAN_UPDATE_LOGS, "ACT");
    logTokens.put(REPORT_LOGS, "RPT");
    logTokens.put(JMS_LOGS, "JMS");
    logTokens.put(ERROR_LOGS, "ERR");
    logTokens.put(MISC_LOGS, "MSC");
    logTokens.put(MAIL_LOGS, "MAI");
    logTokens.put(MOBILE_LOGS,  "MOB");
    logTokens.put(HIBERNATE_SESSION_LOGS, "SES");
    logTokens.put(NEWUSER_CREATION_LOGS, "NEW");
    logTokens.put(DEBUG_LOGS, "DBG");
  }
  // web.xml param names
//@formatter:off
  public static String WEB_XML_DB_DROPCREATE_KEY    = "dbDropAndCreate"; 
  public static String WEB_XML_DB_NAME_KEY          = "dbName"; 
  public static String WEB_XML_DB_PASSWORD_KEY      = "dbPassword";
  public static String WEB_XML_DB_URL_KEY           = "dbUrl"; 
  public static String WEB_XML_DB_USER_KEY          = "dbUser"; 
  public static String WEB_XML_PIIDB_URL_KEY        = "piiDbUrl";
  public static String WEB_XML_PIIDB_NAME_KEY       = "piiDbName";
  public static String WEB_XML_PIIDB_USER_KEY       = "piiDbUser";
  public static String WEB_XML_PIIDB_PASSWORD_KEY   = "piiDbPassword";
  
  public static String WEB_XML_C3P0_MAX_SIZE          = "c3p0MaxSize";
  public static String WEB_XML_C3P0_MIN_SIZE          = "c3p0MinSize";
  public static String WEB_XML_C3P0_ACQUIRE_INCREMENT = "c3p0AcquireIncrement";
  public static String WEB_XML_C3P0_TIMEOUT           = "c3p0Timeout";
  public static String WEB_XML_C3P0_IDLE_TEST_PERIOD  = "c3p0IdleTestPeriod";
  
  public static String WEB_XML_DEPLOYMENT_KEY       = "deployment";
  public static String WEB_XML_DEPLOYMENT_TOKEN_KEY = "deploymentToken";
  public static String WEB_XML_DISABLE_XSRF_KEY     = "disable-xsrf-protection"; 
  public static String WEB_XML_GAME_IMAGES_URL_KEY  = "gameImagesUrl";
  public static String WEB_XML_GAME_URL_TOKEN_KEY   = "gameUrlToken";
  public static String WEB_XML_HIBERNATE_SEARCH_KEY = "hibernateSearchIndexPath"; 
  public static String WEB_XML_JMS_KEEPALIVE_KEY    = "jmsKeepAliveIntervalMS"; 

  public static String WEB_XML_JMS_TOPIC_KEY        = "jmsTopic";  
  public static String WEB_XML_JMS_URL_KEY          = "jmsUrl"; 
  public static String WEB_XML_SMTP_HOST_KEY        = "smtpHost";
  
  public static String WEB_XML_USER_IMAGES_FILESYSTEM_PATH_KEY = "userImagesPath";
  public static String WEB_XML_USER_IMAGES_URL_KEY             = "userImagesUrl";
  public static String WEB_XML_REPORTS_FILESYSTEM_PATH_KEY     = "gameReportsPath";
  public static String WEB_XML_REPORTS_URL_KEY                 = "gameReportsUrl";
  public static String WEB_XML_REPORTS_TO_IMAGES_RELATIVE_PATH_PREFIX = "reports2ImagesPrefix"; 
  
  public static String WEB_XML_CLAMSCAN_VIRUS_SCANNER_PATH     = "clamScanPath";
  public static String WEB_XML_CLAMSCAN_ARGUMENT               = "clamScanArgument";
  
  public static String WEB_XML_PUSH_TRANSPORT_KEY = "transport";
  
  // Class to determine cluster master
  // possible values: static classes in ClusterMasterController (implementations of the interface)
  public static String WEB_XML_CLUSTERMASTER_ARBITER_KEY   = "clusterMasterArbiter";
  
  // parameters for alternate methods:
  public static String WEB_XML_CLUSTERMASTER_NAME_KEY      = "clusterMaster";         // for WebXmlParameterReader
  public static String WEB_XML_CLUSTERMASTER_LOCK_PATH_KEY = "clusterMasterLockPath"; //SharedFileLockGetter
  
//@formatter:on

  // Following get set in ApplicationMaster
  public static String DEPLOYMENT = null;
  public static String DEPLOYMENT_TOKEN = null;
  
  public static String GAME_URL_TOKEN = null;
  public static String GAME_IMAGES_URL_RAW = null; // may need token
                                                   // replacement, done in
                                                   // ApplicationSessionGlobals
  public static String USER_IMAGES_FILESYSTEM_PATH_RAW = null; // ditto
  public static String USER_IMAGES_FILESYSTEM_PATH = null; // after replacement
  public static String USER_IMAGES_URL_RAW = null; // ditto
  public static String REPORTS_FILESYSTEM_PATH_RAW = null;
  public static String REPORTS_FILESYSTEM_PATH = null; // Report Generator hangs on this until non-null
  public static String REPORTS_URL_RAW = null;
  public static String REPORTS_URL = null;
  
  public static String REPORT_TO_IMAGE_URL_PREFIX = null;
  public static String IMAGE_TO_REPORT_FILESYSTEM_REL_PATH = null;

  public static String PATH_TO_CLAMSCAN_VIRUS_SCANNER = null;
  public static String[] CLAMSCAN_ARGUMENTS = null;

  public static String QUERY_START_MARKER = "<<START>>";
  public static String QUERY_END_MARKER = "<<END>>";
  public static String QUERY_MARKER_FIELD = "name";

  public static boolean FULL_MESSAGE_LOG = false;

  public static Transport PUSHTRANSPORT = Transport.LONG_POLLING;
  
  /**
   * URL of the Java Messaging Server (JMS) broker running in the private
   * cluster
   */
  public static String JMS_INTERNODE_URL = null;
  public static String JMS_INTERNODE_TOPIC = null;

  /** URL of the JMS broker running locally on each cluster */
  public static String JMS_LOCAL_HANDLE = null;
  public static String JMS_LOCAL_TOPIC = null;
  public static String JMS_LOCAL_PORT = null;
  public static String JMS_LOCAL_BROKER_NAME = null;

  /** Our jms message properties */
  public static String JMS_MESSAGE_TYPE = "messageType";
  public static String JMS_MESSAGE_TEXT = "message";
  public static String JMS_MESSAGE_UUID = "messageUUID";
  public static String JMS_MESSAGE_SOURCE_SESSION_ID = "messageSourceSessionId";
  public static String JMS_MESSAGE_SOURCE_TOMCAT_ID = "messageSourceTomcatId";
  
  public static final String CLUSTERMONITORURL = "http://test.mmowgli.nps.edu/ganglia";
  public static final String CLUSTERMONITORTARGETWINDOWNAME = "_cluster0";
  
  // Messages to/from ApplicationMaster start with one of these:
  public static final char GAMEEVENT = 'G'; // followed by gameevent ID
  public static final char NEW_CARD = 'C'; // followed by card ID
  public static final char NEW_USER = 'U'; // followed by user ID
  public static final char NEW_ACTIONPLAN = 'A'; // followed by ap ID
  public static final char NEW_MESSAGE = 'M'; // followed by a msg ID
  public static final char UPDATED_CARD = 'c'; // ditto
  public static final char UPDATED_USER = 'u';
  public static final char UPDATED_ACTIONPLAN = 'a';
  public static final char UPDATED_CHAT = 'm';
  public static final char UPDATED_MEDIA = 'i';
  public static final char UPDATED_GAME = 'g';
  public static final char UPDATED_CARDTYPE = 'e'; // followed by cardtype ID
  public static final char UPDATED_MOVE = 'r'; // round
  public static final char UPDATED_MOVEPHASE = 'h'; 
  public static final char DELETED_USER = 'd'; // followed by user ID
  public static final char USER_LOGON = 'L';
  public static final char USER_LOGOUT = 'l';
  
  public static final char UPDATE_SESSION_COUNT = 's';
  /*
  public static final char INSTANCEREPORTCOMMAND = 'P'; // uc
  public static final char INSTANCEREPORT = 'p'; // lc
  */
  public static final char SESSIONS_REPORT = 'n';
  public static final char JMSKEEPALIVE = 'K';
  public static final char REBUILD_REPORTS = 'R';
  public static final char KILLALL_SESSIONS = 'X';
  
  public static final String SESSION_REPORT_FIELD_DELIMITER_WIRE = "&euro;";
  public static final String SESSION_REPORT_ITEM_DELIMITER_WIRE = "&yen;";
  public static final String SESSION_REPORT_FIELD_DELIMITER = "\t";
  public static final String SESSION_REPORT_ITEM_DELIMITER = "\n";
  
  // Debug IDs for auto testing
  public static String GOOD_IDEA_CARD_OPEN_TEXT = "good_idea_card_open_text";
  public static String GOOD_IDEA_CARD_TEXTBOX = "good_idea_card_textbox";
  public static String GOOD_IDEA_CARD_SUBMIT = "good_idea_card_submit";
  public static String BAD_IDEA_CARD_OPEN_TEXT = "bad_idea_card_open_text";
  public static String BAD_IDEA_CARD_TEXTBOX = "bad_idea_card_textbox";
  public static String BAD_IDEA_CARD_SUBMIT = "bad_idea_card_submit";
  public static String EXPAND_CARD_OPEN_TEXT = "expand_card_open_text";
  public static String EXPAND_CARD_TEXTBOX = "expand_card_textbox";
  public static String EXPAND_CARD_SUBMIT = "expand_card_submit";
  public static String COUNTER_CARD_OPEN_TEXT = "counter_card_open_text";
  public static String COUNTER_CARD_TEXTBOX = "counter_card_textbox";
  public static String COUNTER_CARD_SUBMIT = "counter_card_submit";
  public static String ADAPT_CARD_OPEN_TEXT = "adapt_card_open_text";
  public static String ADAPT_CARD_TEXTBOX = "adapt_card_textbox";
  public static String ADAPT_CARD_SUBMIT = "adapt_card_submit";
  public static String EXPLORE_CARD_OPEN_TEXT = "explore_card_open_text";
  public static String EXPLORE_CARD_TEXTBOX = "explore_card_textbox";
  public static String EXPLORE_CARD_SUBMIT = "explore_card_submit";
  
/* these are debug strings */
  public static String IM_NEW_BUTTON = "im_new_button";
  public static String IM_REGISTERED_BUTTON = "im_registered_button";
  public static String USER_NAME_TEXTBOX = "user_name_textbox";
  public static String USER_PASSWORD_TEXTBOX = "user_password_textbox";
  public static String PLAY_AN_IDEA_BLUE_BUTTON = "play_an_idea_blue_button";
  public static String GO_TO_IDEA_DASHBOARD_BUTTON = "go_to_idea_dashboard_button";
  public static String ACTIONPLAN_TABLE_TITLE_CELL = "action_plan_table_title_cell";
  public static String ACTIONPLAN_TAB_THEPLAN = "action_plan_tab_theplan";
  public static String ACTIONPLAN_TAB_TALK = "action_plan_tab_talk";
  public static String ACTIONPLAN_TAB_IMAGES = "action_plan_tab_images";
  public static String ACTIONPLAN_TAB_VIDEO = "action_plan_tab_video";
  public static String ACTIONPLAN_TAB_MAP = "action_plan_tab_map";
  public static String ACTIONPLAN_TAB_THEPLAN_WHO = "action_plan_tab_theplan_who";
  public static String ACTIONPLAN_TAB_THEPLAN_WHAT = "action_plan_tab_theplan_what";
  public static String ACTIONPLAN_TAB_THEPLAN_TAKE = "action_plan_tab_theplan_take";
  public static String ACTIONPLAN_TAB_THEPLAN_WORK = "action_plan_tab_theplan_work";
  public static String ACTIONPLAN_TAB_THEPLAN_CHANGE = "action_plan_tab_theplan_change";
  public static String LOGIN_CONTINUE_BUTTON = "login_continue_button";

  public static final String HEADER_W               = "992px";
  public static final String HEADER_H               = "188px";
  public static final String HEADER_AVATAR_W        = "50px";
  public static final String HEADER_AVATAR_H        = "50px";
  public static final String HEADER_USERNAME_POS    = "top:18px;left:62px";
  public static final int    HEADER_OFFSET_LEFT_MARGIN = 19; //px
  public static final String FOOTER_H               = "93px";
  public static final String FOOTER_W               = "981px";
  public static final int    FOOTER_HOR_OFFSET      = HEADER_OFFSET_LEFT_MARGIN - 10;
  public static final String FOOTER_OFFSET_POS      = "top:0px;left:"+FOOTER_HOR_OFFSET+"px";
  public static final int    CALLTOACTION_HOR_OFFSET     = HEADER_OFFSET_LEFT_MARGIN - 13; //px
  public static final String CALLTOACTION_HOR_OFFSET_STR = ""+CALLTOACTION_HOR_OFFSET+"px";
  public static final String CALLTOACTION_VIDEO_W        = "538px";
  public static final String CALLTOACTION_VIDEO_H        = "328px";

  public static final String ACTIONDASHBOARD_W           = "980px";
  public static final String ACTIONDASHBOARD_H           = "833px";
  public static final int    ACTIONDASHBOARD_HOR_OFFSET  = HEADER_OFFSET_LEFT_MARGIN - 19; // px same as header
  public static final String ACTIONDASHBOARD_OFFSET_POS  = "top:0px;left:"+ACTIONDASHBOARD_HOR_OFFSET+"px";

  public static final String ACTIONPLAN_TABCONTENT_W         = "956px"; //"910px";
  public static final String ACTIONPLAN_TABCONTENT_H         = "766px"; //"720px"; //"682px";
  public static final String ACTIONPLAN_TABCONTENT_POS       = "top:357px;left:46px";
  public static final String ACTIONPLAN_TABCONTENT_LEFT_W    = "280px"; //"188px";
  public static final String ACTIONPLAN_TABCONTENT_LEFT_H    = "300px";
  public static final String ACTIONPLAN_TABCONTENT_LEFT_POS  = "top:25px;left:0px";
  public static final String ACTIONPLAN_TABCONTENT_RIGHT_W   = "705px";
  public static final String ACTIONPLAN_TABCONTENT_RIGHT_H   = "720px"; //"682px";
  public static final String ACTIONPLAN_TABCONTENT_RIGHT_POS = "top:0px;left:285px"; //239px";

  public static final String ACTIONDASHBOARD_TABCONTENT_POS       = "top:103px;left:0px"; //46px";
  public static final String ACTIONDASHBOARD_TABCONTENT_W         = ACTIONPLAN_TABCONTENT_W;
  public static final String ACTIONDASHBOARD_TABCONTENT_H         = ACTIONPLAN_TABCONTENT_H;
  public static final String ACTIONDASHBOARD_TABCONTENT_LEFT_W    = ACTIONPLAN_TABCONTENT_LEFT_W;
  public static final String ACTIONDASHBOARD_TABCONTENT_LEFT_H    = ACTIONPLAN_TABCONTENT_LEFT_H;
  public static final String ACTIONDASHBOARD_TABCONTENT_RIGHT_W   = ACTIONPLAN_TABCONTENT_RIGHT_W;
  public static final String ACTIONDASHBOARD_TABCONTENT_RIGHT_H   = ACTIONPLAN_TABCONTENT_RIGHT_H;
  public static final String ACTIONDASHBOARD_TABCONTENT_LEFT_POS  = ACTIONPLAN_TABCONTENT_LEFT_POS;
  public static final String ACTIONDASHBOARD_TABCONTENT_RIGHT_POS = ACTIONPLAN_TABCONTENT_RIGHT_POS;
}
