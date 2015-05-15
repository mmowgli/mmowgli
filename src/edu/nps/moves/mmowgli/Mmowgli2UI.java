/*
  Copyright (C) 2010-2015 Modeling Virtual Environments and Simulation
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.hibernate.Session;
import org.vaadin.cssinject.CSSInject;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.components.AppMenuBar;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.Move;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.hibernate.DB;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HasUUID;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.messaging.*;
import edu.nps.moves.mmowgli.modules.registrationlogin.RegistrationPageBase;
import edu.nps.moves.mmowgli.utility.MediaLocator;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * Mmowgli2UI.java
 * Created on Jan 22, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * This is the entry point for a new application session
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

/*
  Do NOT put vaadin annotations here.  The annotations are in the descendants of this
  class.  If this is enabled, the browser just hangs.
*/

@SuppressWarnings("serial")
abstract public class Mmowgli2UI extends UI implements WantsMoveUpdates, WantsMovePhaseUpdates, WantsGameUpdates, WantsGameEventUpdates, WantsUserUpdates, HasUUID
{
  private MmowgliOuterFrame outerFr;
  private MmowgliSessionGlobals globals;
  private RegistrationPageBase regpg;
  private AbstractMmowgliControllerHelper2 controllerHelper;
  private Navigator navigator;
  private UUID uuid;
  private boolean uiFullyInitted = false;  // after components added, AFTER login screens
  
  private boolean firstUI = false;
  protected Mmowgli2UI(boolean firstUI)
  {
    this.firstUI = firstUI;
    setPushParameters();
  }
  
  private void setPushParameters()
  {
    getPushConfiguration().setTransport(PUSHTRANSPORT);
    //getPushConfiguration().setParameter(ATMOS_TIMEOUT, "90000"); // ninety seconds  // this severs the conection and a red banner appears
  }
  
  @Override
  @MmowgliCodeEntry
  @HibernateOpened
  protected void init(VaadinRequest request)
  {  
    MSysOut.println(SYSTEM_LOGS,"Into "+(firstUI?"Mmowgli2UILogin":"Mmowgli2UISubsequent") +".init()");
    AppMaster.instance().oneTimeSetAppUrlFromUI();
    
    Object sessKey = HSess.checkInit();
    uuid = UUID.randomUUID();

    setWindowTitleTL();
    VerticalLayout layout = new VerticalLayout();
    setContent(layout);
      
    MmowgliSessionGlobals globs = getSession().getAttribute(MmowgliSessionGlobals.class);
    if(!globs.initted) {
      globs.init(Page.getCurrent().getWebBrowser());
      globs.setController(new DefaultMmowgliController());
      globs.setMediaLocator(new MediaLocator());
      globs.setFirstUI(this);
      
      MessagingManager2 mm = new MessagingManager2();
      globs.setMessagingManager(mm);
      globs.getMessagingManager().registerSession();
      
      globs.setGameBooleans(Game.getTL());
    }
    
    globals = globs;          
    setCustomBackgroundTL();
  
    if(firstUI) {      
      setLoginContentTL(); 
    }
    else {
      setRunningApplicationFrameworkTL();
    }
    controllerHelper = new AbstractMmowgliControllerHelper2(this);
    
    //This has caused some recent exceptions, break it apart temporarily    
    //globs.getMessagingManager().addMessageListener((AbstractMmowgliController)globs.getController());
    MessagingManager2 mm = globs.getMessagingManager();
    MmowgliController cntlr = globs.getController();
    mm.addMessageListener((AbstractMmowgliController)cntlr);
    
    HSess.checkClose(sessKey);
    
    if(!globs.initted)
      globs.initted=true;
    
    MSysOut.println(SYSTEM_LOGS,"Out of "+(firstUI?"Mmowgli2UILogin":"Mmowgli2UISubsequent") +".init()");
  }
   
  public void setWindowTitleTL()
  {
    setWindowTitle(HSess.get());
  }
  
  public void setWindowTitle(Session sess)
  {
    Game game = Game.get(sess);
    boolean gameReadOnly = game.isReadonly();
    boolean cardsReadOnly = game.isCardsReadonly();
    
    VaadinSession vsess = getSession();
    if (vsess != null) {  // occasionally see null here
      ArrayList<UI> uis = new ArrayList<UI>(vsess.getUIs());

      String title = Move.getCurrentMove(sess).getCurrentMovePhase().getWindowTitle();

      uis.add(this);// Set this one, since we may not be in the list yet
      
      if (gameReadOnly)
        title = title + " (Read-only)";
      else if (cardsReadOnly)
        title = title + " (Cards read-only)";
      else
        ;
      for (UI ui : uis) {
        ui.getPage().setTitle(title);
      }
    }
  }
  
  public void setLoginContentTL()
  {
    VerticalLayout layout = (VerticalLayout)getContent();
    layout.removeAllComponents();

    layout.addComponent(regpg = new RegistrationPageBase());
    layout.setComponentAlignment(regpg,  Alignment.TOP_CENTER);    
  }
  
  public void setRunningApplicationFrameworkTL()
  {
    VerticalLayout layout = (VerticalLayout)getContent();
    layout.removeAllComponents();
    
    layout.addStyleName("m-background");
    layout.setMargin(false);
    // This is the layout that fills the browser window
    // layout spans browser window and tracks its resize
    // the outerframe below is centered
    layout.setWidth("100%");
    
    outerFr = new MmowgliOuterFrame();  // contains header and footer
    layout.addComponent(outerFr);
    layout.setComponentAlignment(outerFr, Alignment.TOP_CENTER);
    
    navigator = new Navigator(this,getContentContainer());
    
    getSessionGlobals().getController().setupNavigator(navigator);
    
    Game g = Game.getTL();
    showOrHideFouoButton(g.isShowFouo());

    uiFullyInitted = true;
  }
  
  /* Similar functionality...*/
  public void navigateTo(AppEvent ev)
  {
    navigator.navigateTo(ev.getFragmentString());
  }
  
  public boolean isUiFullyInitted()
  {
    return uiFullyInitted;
  }
  public boolean isFirstUI()
  {
    return firstUI;
  }
  public void setFrameContent(Component c)
  {
    outerFr.setFrameContent(c);
  }
  /*end similar functionality */
  
  public Component getFrameContent()
  {
    if(outerFr != null)     
      return outerFr.getFrameContent();
    return null;
  }
  
  private ComponentContainer getContentContainer()
  {
    return outerFr.getContentContainer();
  }
  
  public MmowgliSessionGlobals getSessionGlobals()
  {
    return globals;
  }
  
  public static MmowgliSessionGlobals getGlobals()
  {
    UI curr = UI.getCurrent();
    if(!(curr instanceof Mmowgli2UI))
      return null;
    Mmowgli2UI mui = (Mmowgli2UI)UI.getCurrent();
    if(mui != null)
      return mui.getSessionGlobals();
    return null;
  }
  
  public static Mmowgli2UI getAppUI()
  {
    return (Mmowgli2UI)UI.getCurrent();
  }
  
  private String css1 = ".mmowgli2.v-app {background-image:url('";
  private String css2 = "')"+
  ";background-color:transparent"+
  ";background-repeat:repeat"+
  ";background-attachment:fixed"+
  ";background-position:top center;}";

  private void setCustomBackgroundTL()
  {
    String bkgUrl = Game.getTL().getBackgroundImageLink();
    if (bkgUrl != null) {
      CSSInject css = new CSSInject(this);
      css.setStyles(css1 + bkgUrl + css2);
    } 
  }
  public AbstractMmowgliControllerHelper2 getControllerHelper()
  {
    return controllerHelper;
  }
  
  public MediaLocator getMediaLocator()
  {
    return globals.getMediaLocator();
  }

  public AppMenuBar getMenuBar()
  {
    return (outerFr==null)?null:outerFr.getMenuBar();
  }

  public void quitAndGoTo(String logoutUrl)
  {
    getPage().setLocation(logoutUrl);
    getSession().close();
  }

  public void showOrHideFouoButton(boolean show)
  {
    if(outerFr != null)
      outerFr.showOrHideFouoButton(show);    
  }

  public boolean userUpdated_oobTL(Object uId)
  {
    if(outerFr != null)
      return outerFr.refreshUser_oobTL(uId); 
    return false;
    
  }
  
  @Override
  public boolean gameEventLoggedOobTL(Object evId)
  {
    if(outerFr != null)
      return outerFr.gameEventLoggedOobTL(evId);
    return false;
  }
  
  @Override
  public boolean gameUpdatedExternallyTL(Object nullObj)
  {
    if(outerFr != null)
      return outerFr.gameUpdatedExternallyTL(null);
    return false;
  }
  
  @Override
  public boolean moveUpdatedOobTL(Serializable mvId)
  {
    if(outerFr != null)
      return outerFr.moveUpdatedOobTL(mvId);
    return false;
  }
  
  @Override
  public boolean movePhaseUpdatedOobTL(Serializable pId)
  {
    MSysOut.println(MISC_LOGS,"Mmowgli2UI.movePhaseUpdated_oobTL.handle() UI = "+getClass().getSimpleName()+" "+hashCode());

    if(outerFr != null)
      outerFr.movePhaseUpdatedOobTL(pId);  // maybe a nop

    MovePhase mp = DB.getRetry(MovePhase.class, pId, null, HSess.get());
    if(mp == null) {
      System.err.println("ERROR: Mmowgli2UI.movePhaseUpdatedOobTL: MovePhase matching id "+pId+" not found in db.");
    }
    // Just wanted to make sure we could get it for the following
    setWindowTitle(HSess.get());
    return true; // may need update, assume so.
  }

  public String getUI_UUID()
  {
    return uuid.toString();
  }
  
  public UUID getUI_UUIDObj()
  {
    return uuid;
  }

  public String getUserSessionUUID()
  {
    return getGlobals().getUserSessionIdentifier().toString();
  }
  
  public UUID getUserSessionUUIDObj()
  {
    return getGlobals().getUserSessionIdentifier();
  }
  
  public void pingPush()
  {
  	if(outerFr != null)
  	  outerFr.pingPush();
  	else if(regpg != null)
  	  regpg.pingPush();
  }
}