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

import java.util.Collection;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.Leaderboard;
import edu.nps.moves.mmowgli.components.SignupsTable;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.export.ActionPlanExporter;
import edu.nps.moves.mmowgli.export.CardExporter;
import edu.nps.moves.mmowgli.export.GameExporter;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.MMessagePacket;
import edu.nps.moves.mmowgli.messaging.MessagingManager2;
import edu.nps.moves.mmowgli.messaging.MessagingManager2.MMMessageListener2;
import edu.nps.moves.mmowgli.modules.actionplans.ActionDashboard;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPage2;
import edu.nps.moves.mmowgli.modules.actionplans.HowToWinActionPopup;
import edu.nps.moves.mmowgli.modules.administration.GameDesignPanel;
import edu.nps.moves.mmowgli.modules.administration.VipListManager;
import edu.nps.moves.mmowgli.modules.cards.*;
import edu.nps.moves.mmowgli.modules.gamemaster.*;
import edu.nps.moves.mmowgli.modules.maps.LeafletMap;
import edu.nps.moves.mmowgli.modules.registrationlogin.RegistrationPageBase;
import edu.nps.moves.mmowgli.modules.userprofile.UserProfilePage3;
import edu.nps.moves.mmowgli.utility.BrowserWindowOpener;
import edu.nps.moves.mmowgli.utility.IDButtonIF;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
/**
 * AbstractMmowgliController.java
 * Created on Mar 6, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class AbstractMmowgliController implements MmowgliController, MMMessageListener2
{
  private boolean initted = false;
  //private Navigator navigator;
  
  private AbstractMmowgliControllerHelper helper;
  public AbstractMmowgliController()
  {
    if(!initted) {
      init();
      initted = true;
    }
  }

  public void init()
  {
    helper = new AbstractMmowgliControllerHelper();
  }
  
  public void setupNavigator(Navigator nav)
  {
    nav.addProvider(new MyViewProvider());
    nav.addView("", CallToActionPage.class);  // to start with
    nav.setErrorProvider(new MyErrorViewProvider());
  }
  
  public void miscEventTL(AppEvent appEvent)
  {
    MmowgliEvent mEv = appEvent.getEvent();
    Object param = appEvent.getData();
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    ActionPlan ap;

    switch(mEv) {
      case ACTIONPLANSHOWCLICK:
        if(param instanceof Long)
          ap = ActionPlan.getTL((Long)param);
        else {
          ap = (ActionPlan) param;
          ap = ActionPlan.mergeTL(ap); // dif session
        }
        if(ap == null) {
          System.err.println("ACTIONPLANSHOWCLICK with invalid id: "+param);
          break;
        }
        ui.navigateTo(new AppEvent(mEv,ui,param));
        break;
        
      case CARDCLICK:
        Card c = Card.getTL(param);
        if(c == null) {
          System.err.println("CARDCLICK with invalid card id: "+param);
          // I'd like to remove the fragment, probably by emulating the browser button
          break;
        }        
        ui.navigateTo(appEvent);// how about sending along the component
        break;
        
      case CARDCHAINPOPUPCLICK:
        CardChainTreeTablePopup chainpopup = new CardChainTreeTablePopup(appEvent.getData());
        chainpopup.center();
        Mmowgli2UI.getAppUI().addWindow(chainpopup);  // does initGui() internally
        break;
      
      case SHOWUSERPROFILECLICK:
        ui.navigateTo(new AppEvent(mEv,ui,param));
        break;
      
      case CARDAUTHORCLICK:
        ui.navigateTo(new AppEvent(MmowgliEvent.SHOWUSERPROFILECLICK,ui,param));
        break;
       
      default:
        MSysOut.println(SYSTEM_LOGS,"TODO, AbstractMmowgliController.miscEvent(): "+mEv.toString());
    }    
  }

  @MmowgliCodeEntry
  @HibernateOpened
  @HibernateClosed
  public void menuClick(MmowgliEvent mEv, MenuBar menubar)
  {
    HSess.init();
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    switch(mEv) {
      case MENUGAMEMASTERUSERADMIN:
        ui.navigateTo(new AppEvent(MmowgliEvent.MENUGAMEMASTERUSERADMIN,ui,null));    
        break;
      case MENUGAMEMASTERABOUTMMOWGLI:
        helper.handleAboutMmowgli(menubar);
        break;
      case MENUGAMEMASTERACTIVECOUNTCLICK:
        helper.handleShowActiveUsersActionTL(menubar);
        break;
      case MENUGAMEMASTERACTIVECOUNTBYSERVERCLICK:
        helper.handleShowActiveUsersPerServer(menubar);
        break;
      case MENUGAMEMASTERACTIVEPLAYERREPORTCLICK:
        helper.handleShowPlayerReport(menubar);
        break;
      case MENUGAMEMASTERMONITOREVENTS:
        EventMonitorPanel mpan = new EventMonitorPanel();
        Mmowgli2UI.getAppUI().setFrameContent(mpan);
        mpan.initGui();
        break;
      case MENUGAMEMASTERPOSTCOMMENT:
        helper.handleGMCommentAction(menubar);
        break;
      case MENUGAMEMASTERBROADCAST:
        helper.handleMessageBroadcastAction(menubar);
        break;
      case MENUGAMEMASTERBROADCASTTOGMS:
        helper.handleGMBroadcastAction(menubar);
        break;
      case MENUGAMEMASTERBLOGHEADLINE:
        helper.handleSetBlogHeadlineAction(menubar);
        break;
//      case MENUGAMEMASTERUSERPOLLINGCLICK:
//        helper.handleShowPollingResults(menubar);
//        break;
      case MENUGAMEMASTERCARDCOUNTCLICK:
        helper.handleShowNumberCardsActionTL(menubar);
        break;
      case MENUGAMEMASTERTOTALREGISTEREDUSERS:
        helper.handleShowTotalRegisteredTL(menubar);
        break;
        
      case MENUGAMEADMINPUBLISHREPORTS:
        helper.handlePublishReportsTL();
        break;
        
      case MENUGAMEADMINEXPORTACTIONPLANS:
        new ActionPlanExporter().exportAllPlansToBrowser("Export ActionPlans");
        break;
        
      case MENUGAMEMASTER_EXPORT_SELECTED_CARD:
        Component cmp = ui.getFrameContent();
        if(cmp != null && cmp instanceof CardChainPage) {
          Object cId = ((CardChainPage)cmp).getCardId();
          new CardExporter().exportSingleCardTreeToBrowser("Card "+cId.toString()+" chain", cId);
          break;
        }
        //else fall through
      case MENUGAMEADMINEXPORTCARDS:
        new CardExporter().exportToBrowser("Export Card Tree");
        break;
        
      case MENUGAMEMASTEROPENREPORTSPAGE:
        String url = AppMaster.instance().getReportsUrl();
        //if(!url.endsWith("/"))
       //   url = url+"/";
        BrowserWindowOpener.open(url); //+"reports");
        break;
        
      case MENUGAMEADMIN_EXPORTGAMESETTINGS:
        new GameExporter().exportToBrowser("Game Design");
        break;
        
      case MENUGAMEADMIN_BUILDGAMECLICK_READONLY:
      case MENUGAMEADMIN_BUILDGAMECLICK:
        ui.navigateTo(new AppEvent(mEv,ui,null));
        break;
      
      case MENUGAMEMASTER_EXPORT_SELECTED_ACTIONPLAN:
        helper.exportSelectedActionPlan();
        break;
        
      case MENUGAMEADMINLOGINLIMIT:
        helper.handleLoginLimitActionTL();
        break;
        
      case MENUGAMEADMINSETCARDSREADWRITE:
        helper.setCardsTL(false,GameEvent.EventType.CARDSREADWRITE);
        break;       
      case MENUGAMEADMINSETCARDSREADONLY:
        helper.setCardsTL(true,GameEvent.EventType.CARDSREADWRITE);
        break;
        
      case MENUGAMEADMINSETGAMEREADWRITE:
        helper.setGameTL(false,GameEvent.EventType.GAMEREADWRITE);
        break;
      case MENUGAMEADMINSETGAMEREADONLY:
        helper.setGameTL(true,GameEvent.EventType.GAMEREADONLY);
        break;
        
      case MENUGAMEADMINSETTOPCARDSREADONLY:
        helper.setTopCardsTL(true,GameEvent.EventType.TOPCARDSREADONLY);
        break;
      case MENUGAMEADMINSETTOPCARDSREADWRITE:
        helper.setTopCardsTL(false,GameEvent.EventType.TOPCARDSREADWRITE);
        break;
      
      case MENUGAMEADMIN_START_EMAILCONFIRMATION:
        helper.setEmailConfirmationTL(true,GameEvent.EventType.GAMEEMAILCONFIRMATIONSTART);
        break;
      case MENUGAMEADMIN_END_EMAILCONFIRMATION:
        helper.setEmailConfirmationTL(false,GameEvent.EventType.GAMEEMAILCONFIRMATIONEND);
      
      case MENUGAMEADMINMANAGESIGNUPS:
        SignupsTable.showDialog("Manage Signups");
        break;
      
      case MENUGAMEADMINDUMPSIGNUPS:
        helper.handleDumpSignupsTL();
        break;
        
      case MENUGAMEMASTERADDTOVIPLIST:
        new VipListManager().add();
        break;
        
      case MENUGAMEMASTERVIEWVIPLIST:
        new VipListManager().view();
        break;
      
      case MENUGAMEADMINDUMPEMAILS:
        helper.handleDumpEmailsTL();
        break;
      case MENUGAMEADMINDUMPGAMEMASTERS:
        helper.handleDumpGameMasterEmailsTL();
        break;
      
      case MENUGAMEMASTERCREATEACTIONPLAN:
        helper.handleCreateActionPlanTL();
        break;
      
      case MENUGAMEMASTERINVITEAUTHORSCLICK:
        AddAuthorEventHandler.inviteAuthorsToActionPlan();
        break;
        
      case MENUGAMEADMINKILLALLSESSIONS:
        helper.handleKillAllSessions();
        break;
        
      default:
        MSysOut.println(SYSTEM_LOGS,"TODO, AbstractMmowgliController.menuEvent(): "+mEv);
    }
    HSess.close();
  }

  @HibernateOpened
  public void buttonClick(ClickEvent event)
  {
    if(!(event.getButton() instanceof IDButtonIF))
      throw new RuntimeException("Programming error, AbstractMmowgliController.buttonClick() expets IDButtons");
    
    Object key = HSess.checkInit();
    
    IDButtonIF butt = (IDButtonIF) event.getButton();
    MmowgliEvent mEv = butt.getEvent();
    Object param = butt.getParam(); // maybe null
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    GameLinks gl;
    switch(mEv) {
      case HOWTOWINACTIONCLICK:
        HowToWinActionPopup winPopup = new HowToWinActionPopup("How to Win the Action");  //No hib
        RegistrationPageBase.openPopupWindow(UI.getCurrent(), winPopup, 650);
        break;
      case IMPROVESCORECLICK:
        gl = GameLinks.getTL();
        BrowserWindowOpener.open(gl.getImproveScoreLink(),PORTALTARGETWINDOWNAME);  //No hib
        break;
      case SIGNOUTCLICK:
        MmowgliSessionGlobals globs = ui.getSessionGlobals();
        globs.loggingOut = true;
        User u = globs.getUserTL();
        GameEventLogger.logUserLogoutTL(u);
        
        MessagingManager2 mgr = Mmowgli2UI.getGlobals().getMessagingManager();
        if(mgr != null)
          mgr.sendSessionMessage(new MMessagePacket(USER_LOGOUT,""+globs.getUserID()),ui);
        
        globs.vaadinSessionClosing();
        
      /*  sendToBus(USER_LOGOUT, "" + uid, false);
        
        InterTomcatIO sIO = getSessIO();
        if(sIO != null)
          sIO.kill();
      */  
        gl = GameLinks.getTL();
        Mmowgli2UI.getAppUI().quitAndGoTo(gl.getThanksForPlayingLink());
        break;
      case HOWTOPLAYCLICK:
        HowToPlayCardsPopup popup = new HowToPlayCardsPopup();
        RegistrationPageBase.openPopupWindow(Mmowgli2UI.getAppUI(), popup, 650); // reuse centering code to miss video already on the
        break;
        
      case MAPCLICK:
        LeafletMap lMap = new LeafletMap();
        Mmowgli2UI.getAppUI().setFrameContent(lMap);
        lMap.initGuiTL();
        break;
        
      case CARDCREATEACTIONPLANCLICK:
        Object cardId = butt.getParam();
        helper.handleCreateActionPlan(event.getButton(), cardId);
        break;
        
      case PLAYIDEACLICK:
      case CALLTOACTIONCLICK:
      case SHOWUSERPROFILECLICK:
      case IDEADASHBOARDCLICK:
      case TAKEACTIONCLICK:
      case LEADERBOARDCLICK:
        ui.navigateTo(new AppEvent(mEv,ui,param));
        break;
      
      case RFECLICK:
        helper.showRfeWindow(param);
        break;
        
      case SEARCHCLICK:
        helper.handleSearchClick(param);
        break;
        
      case POSTTROUBLECLICK:
        gl = GameLinks.getTL();
        BrowserWindowOpener.open(gl.getTroubleLink(),PORTALTARGETWINDOWNAME);
        break;
        
      case ACTIONPLANREQUESTCLICK:
        gl = GameLinks.getTL();
        BrowserWindowOpener.open(gl.getActionPlanRequestLink(),PORTALTARGETWINDOWNAME);
        break;
  
      default:
        MSysOut.println(SYSTEM_LOGS,"TODO, AbstractMmowgliController.buttonClick(): "+mEv);
    } 
    HSess.checkClose(key);
  }
  
  public void handleEventTL(MmowgliEvent mEv, Object obj, Component cmp)
  {
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    switch(mEv) {
      case HANDLE_LOGIN_STARTUP:
        doStartupTL(obj);
        break;
      case SHOWUSERPROFILECLICK:
        ui.navigateTo(new AppEvent(mEv,ui,obj));
        break;        
      case SEARCHCLICK:
        helper.handleSearchClick(obj);
        break;
      default:
        MSysOut.println(SYSTEM_LOGS,"TODO, AbstractMmowgliController.handleEvent(): "+mEv);
    }
  }
  
  private void doStartupTL(Object userId)
  {
  	User u;
  	if(userId instanceof User)
  		u = (User)userId;
  	else
      u = User.getTL(userId);
  	
    Mmowgli2UI.getGlobals().setUserIDTL(u);
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    ui.setRunningApplicationFrameworkTL(); 

    Game g = Game.getTL();
    ui.showOrHideFouoButton(g.isShowFouo());

    goHome(ui); // "Home page"

    if(u.isAdministrator() && g.getAdminLoginMessage() != null)
      handleAdminMessage(g);
  }
  
  private void goHome(Mmowgli2UI ui)
  {
    String s = Page.getCurrent().getUriFragment();
    if(s!=null && s.length()>0)
      try {
        ui.navigateTo(new AppEvent(s));
        return;
      }
      catch(IllegalArgumentException iae) {
        System.err.println("Don't understand uri fragment "+s);
      }

    ui.setFrameContent(new CallToActionPage());
  }
  
  @SuppressWarnings("serial")
  private void handleAdminMessage(Game g)
  {
    final Window dialog = new Window("Important!");
    VerticalLayout vl = new VerticalLayout();
    dialog.setContent(vl);
    vl.setSizeUndefined();
    vl.setMargin(true);
    vl.setSpacing(true);

    vl.addComponent(new HtmlLabel(g.getAdminLoginMessage()));

    HorizontalLayout buttHL = new HorizontalLayout();
    buttHL.setSpacing(true);
    final CheckBox cb;
    buttHL.addComponent(cb = new CheckBox("Show this message again on the next administrator login"));
    cb.setValue(true);
    Button closeButt;
    buttHL.addComponent(closeButt = new Button("Close"));
    closeButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        if(Boolean.parseBoolean(cb.getValue().toString()))
          ; // do nothing
        else {
          HSess.init();
          Game.getTL().setAdminLoginMessage(null);
          Game.updateTL();
          HSess.close();
        }
        UI.getCurrent().removeWindow(dialog);
      }
    });

    vl.addComponent(buttHL);
    vl.setComponentAlignment(buttHL, Alignment.MIDDLE_RIGHT);

    UI.getCurrent().addWindow(dialog);
    dialog.center();
  }
  
  // MessageReceiver interface for oob events
  // This is the controller for one user session, but the user may have several windows, in the form of
  // Vaadin UI objects.  The message needs to be passed to each UI, if appropriate.
  @Override
  public void receiveMessage(MMessagePacket pkt)
  {
    MSysOut.println(MESSAGING_LOGS,"AbstractMmowgliController.receiveMessage() handling msg type "+pkt.msgType);
    // Our session might be down or going down, so check first
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    if(globs == null || globs.stopping)
      return;
    
    HSess.init();
    // First check session-global handlers
    switch (pkt.msgType) {
    case UPDATED_GAME:
      globs.gameUpdatedExternallyTL(null);
      break;
    case UPDATED_CARDTYPE:
      CardType ct = (CardType) HSess.get().get(CardType.class, Long.parseLong(pkt.msg));
      CardTypeManager.updateCardType(ct);
      break;
 /* Not used with new session report
    case INSTANCEREPORTCOMMAND:
      helper.doSessionReport(pkt.msg);
      break;
*/
    }

    // Now check each UI
    Mmowgli2UI appui = Mmowgli2UI.getAppUI(); // got some null ptrs here
    if (appui != null) {
      VaadinSession vSess = appui.getSession();
      if (vSess != null) {
        Collection<UI> uis = vSess.getUIs();
        if (uis != null) {
          for (UI ui : uis) {
            if (ui == null || !(ui instanceof Mmowgli2UI) || !((Mmowgli2UI) ui).isUiFullyInitted()) // might be the error ui
              continue;
            Mmowgli2UI mui = (Mmowgli2UI) ui;
            mui.access(mui.getControllerHelper().getAccessRunner(pkt));
          }
        }
      }
    }
    try { HSess.close(); } catch(Throwable t) {MSysOut.println(ERROR_LOGS,"AbstractMmowgliController.receiveMessage():"+t.getClass().getSimpleName()+" "+t.getLocalizedMessage());}
  }  
  
  // MessageReceiver interface for oob events
  // This is the controller for one user session, but the user may have several windows, in the form of
  // Vaadin UI objects.  The message needs to be passed to each UI, if appropriate.
  // TODO (future) it would be more appropriate to attach a message handler to each UI.  That is, each UI
  // would have it's own receiveMessage(pkt).
  //@Override
  public void oldreceiveMessage(MMessagePacket pkt)
  {
   // MSysOut.println("AbstractMmowgliController receiveMessage(pkt) type= " + pkt.msgType);
    HSess.init();
    try {
      switch (pkt.msgType) {
      
      case UPDATED_ACTIONPLAN:
      case NEW_ACTIONPLAN:
        helper.actionPlanUpdated_oob(Long.parseLong(pkt.msg));
        break;
      case NEW_CARD:
        helper.cardPlayed_oob(Long.parseLong(pkt.msg));
        break;
      case UPDATED_CARD:
        helper.cardUpdated_oob(Long.parseLong(pkt.msg));
        break;
      case UPDATED_CHAT:
        helper.chatLogUpdated_oob(Long.parseLong(pkt.msg));
        break;
      case UPDATED_GAME:
        helper.gameUpdated_oob();
        break;
      case UPDATED_MEDIA: // normally means only that the caption has been edited
        helper.mediaUpdated_oob(Long.parseLong(pkt.msg));
        break;
      case UPDATED_MOVE:
        helper.moveUpdated_oob(Long.parseLong(pkt.msg));
        break;
      case UPDATED_MOVEPHASE:
        helper.movePhaseUpdated_oob(Long.parseLong(pkt.msg));
        break;
      case NEW_MESSAGE:
        helper.newGameMessage_oob(Long.parseLong(pkt.msg));
        break;
      case UPDATED_USER:
        // probably a scoring change
        helper.userUpdated_oob(Long.parseLong(pkt.msg));
        break;
      case GAMEEVENT:
        helper.gameEvent_oob(pkt.msgType, pkt.msg); // messageType,message);
        break;

      case NEW_USER:
        helper.newUser_oob(Long.parseLong(pkt.msg));
        break;

      case USER_LOGON:
        // id = Long.parseLong(message);
        // User u = DBGet.getUser(id,sessMgr.getSession());
        // broadcastNews_oob(sessMgr,"User " + u.getUserName() + " / " + u.getLocation() + " now online");
        break;
      case USER_LOGOUT:
        // id = Long.parseLong(message);
        // User usr = DBGet.getUser(id,sessMgr.getSession());
        // broadcastNews_oob(sessMgr,"User " + usr.getUserName() + " / " + usr.getLocation() + " went offline");
        break;
/* not used with new session report
      case INSTANCEREPORTCOMMAND:
        helper.doSessionReport(pkt.msg);
        break;
 */
      case UPDATED_CARDTYPE:
        Object key = HSess.checkInit();
        CardType ct = (CardType) HSess.get().get(CardType.class, Long.parseLong(pkt.msg));
        HSess.checkClose(key);

        CardTypeManager.updateCardType(ct);
        break;
      }
    }
    catch (RuntimeException re) {
      System.err.println("RuntimeException trapped in MmowgliOneApplicationController oob loop: " + re.getClass().getSimpleName() + ", "
          + re.getLocalizedMessage());
      re.printStackTrace();
    }
    catch (Throwable t) {
      System.err.println("Throwable trapped in MmowgliOneApplicationController oob loop: " + t.getClass().getSimpleName() + ", " + t.getLocalizedMessage());
      t.printStackTrace();
    }
    HSess.close();
  }

  public String buildFragment(AppEvent ev)
  {
    return "" + ev.getEvent().ordinal()+"_"+(ev.getData()==null?"":ev.getData().toString());
  }
  
  @SuppressWarnings("serial")
  class MyViewProvider implements ViewProvider
  {
    View myView=null;
    @Override
    @MmowgliCodeEntry
    @HibernateConditionallyOpened
    @HibernateConditionallyClosed
    public String getViewName(String viewAndParameters)
    {
      Object key = HSess.checkInit();
      String retrn = null;
      try {
       AppEvent evt = new AppEvent(viewAndParameters);
       retrn = handleEventTL(evt);
      }
      catch(Throwable ex) {
        if(viewAndParameters == null || viewAndParameters.equals(""))
          retrn = ""; // startup
        else {
          System.err.println("Bad fragment:"+viewAndParameters);
          retrn = null;
        }
      }
      HSess.checkClose(key);
      return retrn;
    }

    @Override
    @MmowgliCodeEntry
    @HibernateConditionallyOpened
    @HibernateConditionallyClosed
    public View getView(String viewName)
    {
      Object key = HSess.checkInit();
      if(viewName.equals("")) {
        View vw = new CallToActionPage();  // startup
        HSess.checkClose(key);
        return vw;
      }
      View v = myView;
      myView = null;
      HSess.checkClose(key);
      return v;
    }
    
    // Return null if don't understand
    private String handleEventTL(AppEvent appEvent)
    {
      MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
      MmowgliEvent mEv = appEvent.getEvent();
      Object param = appEvent.getData();
      switch(mEv) {
        case CARDCLICK:
          long cardid = Long.parseLong(param.toString());
          if(Card.canSeeCardTL(Card.getTL(cardid),globs.getUserTL()))
            myView = new CardChainPage(Long.parseLong(param.toString()));
          else
            myView = new CallToActionPage(); // default
          break;
        case MAPCLICK:
          myView = new LeafletMap();
          break;
        case LEADERBOARDCLICK:
          myView = new Leaderboard();
          break;
        case PLAYIDEACLICK:
          myView = new PlayAnIdeaPage2();
          break;
        case CALLTOACTIONCLICK:
          myView = new CallToActionPage();//this one doesn't need it c2ap.initGui();
          break;
        case SHOWUSERPROFILECLICK:
          myView = new UserProfilePage3(Long.parseLong(param.toString()));
          break;
        case IDEADASHBOARDCLICK:
          myView = new IdeaDashboard();
          break;
        case TAKEACTIONCLICK:
          myView = new ActionDashboard();
          break;            
        case MENUGAMEADMIN_BUILDGAMECLICK_READONLY:
          if(globs.getUserTL().isGameMaster())
            myView = new GameDesignPanel(true);
          else
            myView = new CallToActionPage();
          break;       
        case MENUGAMEADMIN_BUILDGAMECLICK:
          if(globs.getUserTL().isDesigner())
            myView = new GameDesignPanel(false);
          else
            myView = new CallToActionPage();
          break;
        case MENUGAMEMASTERUSERADMIN:
          if(globs.getUserTL().isAdministrator())
            myView = new UserAdminPanel();
          else
            myView = new CallToActionPage();
          break;
        case ACTIONPLANSHOWCLICK:
          myView = new ActionPlanPage2(Long.parseLong(param.toString()));
          break;

        default:
          return null;
      }
      return appEvent.getFragmentString();
    }
  }

  @SuppressWarnings("serial")
  class MyErrorViewProvider implements ViewProvider
  {
    @Override
    public String getViewName(String viewAndParameters)
    {
      return "";
    }
    
    @Override
    @MmowgliCodeEntry
    @HibernateConditionallyOpened
    @HibernateConditionallyClosed
    public View getView(String viewName)
    {
      MSysOut.println(ERROR_LOGS,"Bad url tag, error provider redirecting to CallToAction");
      Object key = HSess.checkInit();
      View vw = new CallToActionPage();
      HSess.checkClose(key);
      return vw;
    }   
  }
}

