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

import static edu.nps.moves.mmowgli.MmowgliConstants.DEBUG_LOGS;
import static edu.nps.moves.mmowgli.MmowgliConstants.SYSTEM_LOGS;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Session;

import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.UI;

import edu.nps.moves.mmowgli.CACManager.CACData;
import edu.nps.moves.mmowgli.components.AppMenuBar;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.messaging.MessagingManager2;
import edu.nps.moves.mmowgli.messaging.WantsGameUpdates;
import edu.nps.moves.mmowgli.modules.scoring.ScoreManager2;
import edu.nps.moves.mmowgli.utility.MediaLocator;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * MmowgliSessionGlobals.java
 * Created on Jan 24, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliSessionGlobals implements Serializable, WantsGameUpdates
{
  private static final long serialVersionUID = -2942884991365648347L;

  public boolean initted = false;
  public boolean stopping = false;
  public boolean loggingOut = false;
  
  private String browserApp="";
  private String browserMiniType = "";
  private String browserOS = "";
  private int browserMajVersion=0;
  private String browserMajVersionString="";
  private int browserMinVersion=0;
  private String browserAddress="";
  private boolean internetExplorer7 = false;
  private boolean internetExplorer  = false;

  private String loginTimeStamp = "";
  private MmowgliController controller;
  private MessagingManager2 messagingManager;
  private MediaLocator mediaLoc;
  private ScoreManager2 scoreManager;
  private Serializable userId=null;
  private String userName="";
  private Mmowgli2UI firstUI = null;
  private boolean loggedIn = false;
  private UUID userSessionIdentifier = UUID.randomUUID();
  private CACData cacData = null;  // CAC information
  
  private boolean gameAdministrator = false;
  private boolean gameMaster = false;
  private boolean viewOnlyUser = false;
  private boolean gameReadOnly = false;
  private boolean cardsReadOnly = false;
  private boolean topCardsReadOnly = false;
  private boolean priorActionPlansReadOnly = true;
  
  private HashMap<Object,Object> panelState = new HashMap<Object,Object>();
  
  public MmowgliSessionGlobals(SessionInitEvent event, Mmowgli2VaadinServlet servlet)
  {
    event.getSession().setAttribute(MmowgliSessionGlobals.class, this);  // store this for use across the app
    
    //appMaster = (AppMaster)servlet.getServletContext().getAttribute(MmowgliConstants.APPLICATION_MASTER_ATTR_NAME);
    
    scoreManager = new ScoreManager2();
    loginTimeStamp = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss").format(new Date()).toString();
  }
  
  public void init(WebBrowser webBr)
  {
    deriveBrowserBooleans(webBr);
    MSysOut.println(SYSTEM_LOGS,"Login from "+browserIDString());
  }
  
  private void deriveBrowserBooleans(WebBrowser webBr)
  {
    browserApp = webBr.getBrowserApplication();
    browserMiniType = returnBrowserType(webBr);
    browserOS = returnBrowserOS(webBr);
    browserMajVersion = webBr.getBrowserMajorVersion();
    browserMajVersionString = ""+browserMajVersion;
    browserMinVersion = webBr.getBrowserMinorVersion();
    browserAddress    = webBr.getAddress();

    if(browserApp.contains("MSIE 7.0")) {
      internetExplorer = true;
      if( browserMajVersion <= 7)
        internetExplorer7 = true;
    }
  }
  
  public String getBrowserMiniType()
  {
    return browserMiniType;
  }
  public String getBrowserOS()
  {
    return browserOS;
  }
  
  public int getBrowserMajorVersion()
  {
    return browserMajVersion;
  }
  
  public String getBrowserMajorVersionString()
  {
    return browserMajVersionString;
  }
  
  public String getBrowserAddress()
  {
    return browserAddress;
  }

  public String browserIDString()
  {
    return browserAddress+" with "+browserApp+" "+browserMajVersion+" "+browserMinVersion;
  }

  public boolean isIE()
  {
    return internetExplorer;
  }

  public boolean isIE7()
  {
    return internetExplorer7;
  }

  public void setController(MmowgliController mmowgliController)
  {
    controller = mmowgliController;    
  }

  public MmowgliController getController()
  {
    return controller;
  }

  public MediaLocator getMediaLocator()
  {
    return mediaLoc;
  }

  public void setMediaLocator(MediaLocator mediaLocator)
  {
    this.mediaLoc = mediaLocator;    
  }

  public void setUserID(User u)
  {
    this.userId = u.getId();  
    gameAdministrator = u.isAdministrator();
    gameMaster = u.isGameMaster();
    viewOnlyUser = u.isViewOnly();
    userName = u.getUserName();    
  }
  
  public void setUserIDTL(Object userOrUserId)
  {
  	User me;
  	if(userOrUserId instanceof User)
  		me = (User)userOrUserId;
  	else {
  		me = User.getTL((Serializable)userOrUserId);
      MSysOut.println(DEBUG_LOGS,"User.getTL() in MmowgliSessionGlobals.setUserIDTL()");
  	}
  	userId = me.getId();
  	
    gameAdministrator = me.isAdministrator();
    gameMaster = me.isGameMaster();
    viewOnlyUser = me.isViewOnly();
    userName = me.getUserName();
  }
  
  public Serializable getUserID()
  {
    return userId;
  }
  
  public User getUserTL()
  {
    MSysOut.println(DEBUG_LOGS,"User.getTL() in MmowgliSessionGlobals.getUserTL()");
    return User.getTL(getUserID());
  }
  
  public String getUserName()
  {
    return userName;
  }
 
  public String getUserLoginTimeData()
  {
    return loginTimeStamp;
  }
  
  public MediaLocator mediaLocator()
  {
    return mediaLoc;
  }

  public String getGameImagesUrl()
  {
    return AppMaster.instance().getGameImagesUrlString();
  }
  
  public UI getFirstUI()
  {
    return firstUI;
  }
  
  public void setFirstUI(Mmowgli2UI ui)
  {
    firstUI=ui;
  }

  public UUID getUserSessionIdentifier()
  {
    return userSessionIdentifier;
  }
  
  public ScoreManager2 getScoreManager()
  {
    return scoreManager;
  }

  public int getSessionCount()
  {
    return AppMaster.instance().getSessionCount();
  }

  public Object[][] getSessionCountByServer()
  {
    return AppMaster.instance().getSessionCountByServer();
  }

  public void setLoggedIn(boolean b)
  {
    loggedIn = b;
  }
  
  public boolean isLoggedIn()
  {
    return loggedIn;
  }
  
  public Object getPanelState(Object key)
  {
    return panelState.get(key);    
  }
  
  public void setPanelState(Object key, Object val)
  {
    panelState.put(key, val);
  }
  
  public boolean isGameAdministrator()
  {
    return gameAdministrator;
  }
  public boolean isGameMaster()
  {
    return gameMaster;
  }

  public boolean isGameReadOnly()
  {
    return gameReadOnly;
  }
  
  public void setGameReadOnly(boolean wh)
  {
    gameReadOnly = wh;
  }
  
  private boolean isCardsReadOnly()
  {
    return cardsReadOnly | gameReadOnly;
  }
  
  private void setCardsReadOnly(boolean wh)
  {
    cardsReadOnly = wh;
  }
  
  private boolean isTopCardsReadOnly()
  {
    return topCardsReadOnly | gameReadOnly;
  }
  
  private void setTopCardsReadOnly(boolean wh)
  {
    topCardsReadOnly = wh;
  }
  
  public boolean isViewOnlyUser()
  {
    return viewOnlyUser;
  }
  private void setPriorActionPlansReadOnly(boolean wh)
  {
    priorActionPlansReadOnly = wh;
  }
  
  public boolean isPriorActionPlansReadOnly()
  {
    return priorActionPlansReadOnly;
  }
  
  public static class CardPermission
  {
    public boolean canCreate = true;
    public String whyNot = null;
    CardPermission(boolean canCreate, String whyNot)
    {
      this.canCreate = canCreate;
      this.whyNot = whyNot;
    }
  }

  public String whyCantCreateCard(boolean isTopCard)
  {
    return cardPermissionsCommon(isTopCard).whyNot;
  }

  public boolean canCreateCard(boolean isTopCard)
  {
    return cardPermissionsCommon(isTopCard).canCreate;
  }

  public CardPermission cardPermissionsCommon(boolean isTopCard)
  {
    if(isViewOnlyUser())
      return new CardPermission(false,"View-only account cannot create cards");

    if(isTopCard && isTopCardsReadOnly() && !isGameAdministrator() )
      return new CardPermission(false,"Adding top-level cards is disabled");

    if(isGameReadOnly())
      return new CardPermission(false,"Game is read-only");

    if(isCardsReadOnly())
      return new CardPermission(false,"Adding cards is disabled");

    return new CardPermission(true,null);
  }
  
  /*
   * Something in the game object was changed
   */
  @Override
  public boolean gameUpdatedExternallyTL(Object nullObj)
  {
    Mmowgli2UI.getAppUI().setWindowTitle(HSess.get());

    // Got a null ptr exception here once, so do some checking
    // Collection<UI> uis = Mmowgli2UI.getAppUI().getSession().getUIs();
    
    Mmowgli2UI mui = Mmowgli2UI.getAppUI();
    if (mui != null) {
      VaadinSession sess = mui.getSession();
      if (sess != null) {
        Collection<UI> uis = sess.getUIs();
        for (UI ui : uis) {
          if (ui instanceof Mmowgli2UI) {
            AppMenuBar menubar = ((Mmowgli2UI) ui).getMenuBar();
            if (menubar != null) { // can be at start
              menubar.gameUpdatedExternallyTL(null);
            }
          }
        }
      }
    }
    setGameBooleans(Game.getTL());
    return true;
  }

  public void setMessagingManager(MessagingManager2 mm)
  {
    messagingManager = mm;    
  }

  public MessagingManager2 getMessagingManager()
  {
    return messagingManager;   
  }

  public void vaadinSessionClosing()
  {
    MessagingManager2 mgr = getMessagingManager();
    if(mgr != null) {
      mgr.unregisterSession();
      mgr.killThread();
    }  	
  }
  
  public void setGameBooleans(Game g)
  {
    setGameReadOnly(g.isReadonly());
    setCardsReadOnly(g.isCardsReadonly());
    setTopCardsReadOnly(g.isTopCardsReadonly());
    setPriorActionPlansReadOnly(!g.isEditPriorMovesActionPlans());
    MSysOut.println(SYSTEM_LOGS,"Session game globals set to game r/o:"+g.isReadonly()+" cards r/o:"+g.isCardsReadonly()+" top cards r/o:"+g.isTopCardsReadonly());
  }
  private String returnBrowserType(WebBrowser webBr)
  {
    if( webBr.isFirefox() ) { return "Firefox"; }
    if( webBr.isSafari() ) { return "Safari"; }
    if( webBr.isIE() ) { return "IE"; }
    if( webBr.isChrome() ) { return "Chrome"; }
    if( webBr.isOpera() ) { return "Opera"; }
    if( webBr.isLinux() ) { return "Linux"; }
    if( webBr.isAndroid() ) { return "Android"; }
    if( webBr.isIPhone() ) { return "IPhone"; }
    if( webBr.isIPad() ) { return "IPad"; }
    if( webBr.isIOS() ) { return "IOS"; }
    return "";
  }

  private String returnBrowserOS(WebBrowser webBr)
  {
    if( webBr.isAndroid()) { return "Android"; }
    if( webBr.isIOS()) { return "IOS"; }
    if( webBr.isIPad()){ return "IPad"; }
    if( webBr.isIPhone()) { return "IPhone"; }
    if( webBr.isLinux()) { return "Linux"; }
    if( webBr.isMacOSX()) { return "MacOSX"; }
    if( webBr.isWindows()) { return "Windows"; }
    if( webBr.isWindowsPhone()) { return "Windows Phone"; }
    return "Unknown OS/platform";
  }
  
  public String getAlternateVideoUrlTL()
  {
    return getAlternateVideoUrl(HSess.get());
  }

  public String getAlternateVideoUrl(Session sess)
  {
    Game g = Game.get(sess);
    StringBuilder sb = new StringBuilder();
    sb.append("http://portal.mmowgli.nps.edu/");

    String acro = g.getAcronym();
    if(acro == null || acro.isEmpty())
      sb.append("game-wiki/-/wiki/PlayerResources/Video+Resources");
    else {
      sb.append(acro);
      sb.append("-videos");
    }
    return sb.toString();
  }

  public void setCACInfo(CACData cData)
  {
    this.cacData = cData;    
  }
  
  public CACData getCACInfo()
  {
    return cacData;
  }
}
