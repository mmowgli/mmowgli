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

package edu.nps.moves.mmowgli.modules.actionplans;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import static edu.nps.moves.mmowgli.MmowgliEvent.*;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import com.vaadin.data.*;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.cache.MCacheUserHelper.QuickUser;
import edu.nps.moves.mmowgli.components.*;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.*;
import edu.nps.moves.mmowgli.modules.gamemaster.CreateActionPlanPanel;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.*;
import edu.nps.moves.mmowgli.utility.HistoryDialog.DoneListener;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * ActionPlanPage.java Created on Feb 8, 2011
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanPage2 extends AbsoluteLayout implements MmowgliComponent, WantsActionPlanEdits, WantsActionPlanUpdates, WantsActionPlanTimeouts,
    WantsChatLogUpdates, WantsMediaUpdates, View// , TextChangeListener
{
  static final long serialVersionUID = 688322808925939444L;

  public static String ONE_THUMB_TOOLTIP = "Needs work";
  public static String TWO_THUMBS_TOOLTIP = "Looks good, might work";
  public static String THREE_THUMBS_TOOLTIP = "Looks great!  Make it happen!";
  public static final String ACTIONPLAN_TITLE_W = "490px";
  
  private Label lastCommentLabel;

  private NativeButton commentsButt, envelopeButt;
  private NativeButton addCommentButt, addCommentButtBottom;
  private NativeButton viewChainButt;
  private NativeButton browseBackButt, browseFwdButt;

  private IDNativeButton rfeButt;
  private NativeButton addAuthButton;

  private ClickListener addCommentListener;

  private Object apId;

  private TextAreaLabelUnion titleUnion;
  private NativeButton titleHistoryButt;
  private Object chatLogId;
  private boolean titleFocused = false;

  ActionPlanPageTabImages imagesTab;
  ActionPlanPageTabVideos videosTab;
  ActionPlanPageTabMap mapTab;
  ActionPlanPageTabTalk talkTab;
  ActionPlanPageTabThePlan2 thePlanTab;
  NativeButton thePlanTabButt, talkTabButt, imagesTabButt, videosTabButt, mapTabButt;
  Resource talkTabRes, imagesTabRes, videosTabRes, mapTabRes;
  private ActionPlanPageCommentPanel2 commentPanel;

  Button currentTabButton;
  ActionPlanPageTabPanel currentTabPanel;

  private UserList authorList;
  NativeButton newChatLab;
  private ThumbPanel thumbPanel;
  boolean imAuthor = false;
  SaveCancelPan saveCanPan;
  boolean readonly = false;
  
  ClickListener helpWantedListener, interestedListener;

  public ActionPlanPage2(Object actPlnId)
  {
    this(actPlnId, false);
  }

  @HibernateSessionThreadLocalConstructor
  public ActionPlanPage2(Object actPlnId, boolean isMockup)
  {
    this.apId = actPlnId;
    ActionPlan actPln = ActionPlan.getTL(actPlnId);
    Game g = Game.getTL();
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    if(globs.isGameReadOnly())
      readonly = true;
    if(globs.isViewOnlyUser())
      readonly = true;
    if((actPln.getCreatedInMove().getNumber() != g.getCurrentMove().getNumber()) && globs.isPriorActionPlansReadOnly())
      readonly = true;
    
    ChatLog cl = actPln.getChatLog();
    if (cl != null)
      chatLogId = cl.getId();

    saveCanPan = new SaveCancelPan();
    MyTitleListener scLis = new MyTitleListener(saveCanPan);

    titleUnion = new TextAreaLabelUnion(null, null, scLis, "m-actionplan-title");

    commentPanel = new ActionPlanPageCommentPanel2(this, actPlnId, readonly);
    commentsButt = new NativeButton();
    envelopeButt = new NativeButton();
    addCommentButt = new NativeButton();
    addCommentButt.setEnabled(!readonly);
    addCommentButtBottom = new NativeButton();
    addCommentButtBottom.setEnabled(!readonly);

    viewChainButt = new NativeButton();
    browseBackButt = new NativeButton();
    browseFwdButt = new NativeButton();

    rfeButt = new IDNativeButton(null, RFECLICK);
    rfeButt.setParam(actPlnId);
    addAuthButton = new NativeButton();

    thePlanTab = new ActionPlanPageTabThePlan2(this, actPlnId, isMockup, readonly);
    talkTab = new ActionPlanPageTabTalk(actPlnId, isMockup, readonly);
    imagesTab = new ActionPlanPageTabImages(actPlnId, isMockup, readonly);
    videosTab = new ActionPlanPageTabVideos(actPlnId, isMockup, readonly);
    mapTab = new ActionPlanPageTabMap(actPlnId, isMockup, readonly);
    thePlanTabButt = new NativeButton();
    talkTabButt = new NativeButton();
    imagesTabButt = new NativeButton();
    videosTabButt = new NativeButton();
    mapTabButt = new NativeButton();

    currentTabButton = thePlanTabButt;
    currentTabPanel = thePlanTab;

    newChatLab = new NativeButton();
  }

  class MyTitleListener implements FocusListener, ClickListener
  {
    private static final long serialVersionUID = 1L;

    SaveCancelPan pan;
    public MyTitleListener(SaveCancelPan pan)
    {
      this.pan = pan;
      pan.setClickHearer(this);
    }

    @Override
    public void focus(FocusEvent event)
    {
      // if(titleTA.isReadOnly())
      // return;
      // bad idea titleTA.selectAll();

      pan.setVisible(true);
      titleFocused = true;
      // no, have seen event flurry start up
  //    sendStartEditMessage(DBGet.getUser(Mmowgli2UI.getGlobals().getUserID()).getUserName() + " is editing action plan title");
    }

    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      ActionPlan actPln = ActionPlan.getTL(apId);

      if (event.getSource() == pan.canButt) { // cancel
        if (actPln.getTitle() != null)
          titleUnion.setValueTL(actPln.getTitle());
        titleUnion.labelTop();
        // setValueIfNonNull(titleTA,actPln.getTitle());
      }
      else { // Save
        // int len = titleTA.getValue().toString().length();
        int len = titleUnion.getValue().length();
        if (len >= 255) {
          Notification notif = new Notification("<center>Not so fast!</center>", "Limit title length to 255 characters (now " + len
              + "). <small>Click this message to continue.</small>", Notification.Type.WARNING_MESSAGE, true);
          notif.setDelayMsec(-1); // must click
          notif.show(Page.getCurrent());
          HSess.close();
          return;
        }
        String s = nullOrString(titleUnion.getValue());
        actPln.setTitleWithHistoryTL(s);
        titleUnion.setLabelValueTL(s);
        titleUnion.labelTop();
        ActionPlan.updateTL(actPln);
        User u = Mmowgli2UI.getGlobals().getUserTL();
        GameEventLogger.logActionPlanUpdateTL(actPln, "title edited", u.getId());
      }
      pan.setVisible(false);
      titleFocused = false;
      HSess.close();
    }
  }
/*
  private void setValueIfNonNull(AbstractTextField comp, String s)
  {
    if(s != null)
      comp.setValue(s);
  }
*/
  private String nullOrString(Object o)
  {
    if(o == null)
      return null;
    return o.toString();
  }
  
  @Override
  public void initGui()
  {
    throw new UnsupportedOperationException("");
  }

  @SuppressWarnings("serial")
  public void initGuiTL()
  {
    ActionPlan actPln = ActionPlan.getTL(apId);
    User me = Mmowgli2UI.getGlobals().getUserTL();
    addStyleName("m-cssleft-45");
    
    setWidth("1089px");
    setHeight("1821px");
    Label sp;

    VerticalLayout mainVL = new VerticalLayout();
    addComponent(mainVL, "top:0px;left:0px");
    mainVL.addStyleName("m-overflow-visible");
    mainVL.setWidth("1089px");
    mainVL.setHeight(null);
    mainVL.setSpacing(false);
    mainVL.setMargin(false);

    VerticalLayout mainVLayout = new VerticalLayout();

    mainVLayout.setSpacing(false);
    mainVLayout.setMargin(false);
    mainVLayout.addStyleName("m-actionplan-background2");
    mainVLayout.setWidth("1089px");
    mainVLayout.setHeight(null); //"1821px");
    mainVL.addComponent(mainVLayout);

    mainVLayout.addComponent(makeIdField(actPln));
    
    mainVLayout.addComponent(sp = new Label());
    sp.setHeight("5px");
    
    VerticalLayout leftTopVL = new VerticalLayout();
    leftTopVL.setWidth("820px");
    leftTopVL.setSpacing(false);
    leftTopVL.setMargin(false);
    mainVLayout.addComponent(leftTopVL);

    HorizontalLayout titleAndThumbsHL = new HorizontalLayout();
    titleAndThumbsHL.setSpacing(false);
    titleAndThumbsHL.setMargin(false);
    titleAndThumbsHL.setHeight("115px");
    titleAndThumbsHL.addStyleName("m-actionplan-header-container");
    leftTopVL.addComponent(titleAndThumbsHL);

    titleAndThumbsHL.addComponent(sp=new Label());
    sp.setWidth("55px");

    VerticalLayout vl = new VerticalLayout();
    vl.addComponent(titleUnion); //titleTA);
    titleUnion.initGui();

    titleHistoryButt = new NativeButton();
    titleHistoryButt.setCaption("history");
    titleHistoryButt.setStyleName(BaseTheme.BUTTON_LINK);
    titleHistoryButt.addStyleName("borderless");
    titleHistoryButt.addStyleName("m-actionplan-history-button");
    titleHistoryButt.addClickListener(new TitleHistoryListener());
    titleHistoryButt.setEnabled(!readonly);
    vl.addComponent(titleHistoryButt);
    vl.setComponentAlignment(titleHistoryButt, Alignment.TOP_RIGHT);
    titleAndThumbsHL.addComponent(vl); //titleTA);

    titleUnion.setWidth(ACTIONPLAN_TITLE_W);
    titleUnion.setValueTL(actPln.getTitle());
    
    titleUnion.addStyleName("m-lightgrey-border");
    // titleUnion.addStyleName("m-opacity-75");
    titleUnion.setHeight("95px"); // 120 px); must make it this way for alignment of r/o vs rw

    addComponent(saveCanPan, "top:0px;left:395px");
    saveCanPan.setVisible(false);

    titleAndThumbsHL.addComponent(sp=new Label());
    sp.setWidth("50px");

    VerticalLayout thumbVL = new VerticalLayout();
    titleAndThumbsHL.addComponent(thumbVL);
    thumbVL.addComponent(sp=new Label());
    sp.setHeight("50px");

    thumbPanel = new ThumbPanel();
    Map<User, Integer> map = actPln.getUserThumbs();
    Integer t = map.get(me);
  /*  if(t == null) {
      map.put(me, 0);
      ActionPlan.update(actPln);
      GameEventLogger.logActionPlanUpdate(actPln, "thumbs changed",me.getUserName());
      t = 0;
    } */
    thumbPanel.setNumUserThumbs(t==null?0:t);
    thumbVL.addComponent(thumbPanel);

    HorizontalLayout commentAndViewChainHL = new HorizontalLayout();
    leftTopVL.addComponent(commentAndViewChainHL);
    commentAndViewChainHL.setSpacing(false);
    commentAndViewChainHL.setMargin(false);
    commentAndViewChainHL.addComponent(sp=new Label());
    sp.setWidth("55px");

    VerticalLayout commLeftVL = new VerticalLayout();
    commentAndViewChainHL.addComponent(commLeftVL);
    commLeftVL.setWidth("95px");
    commLeftVL.addComponent(commentsButt);
    commentsButt.setStyleName(BaseTheme.BUTTON_LINK);
    commentsButt.addStyleName("borderless");
    commentsButt.addStyleName("m-actionplan-comments-button");
    ClickListener commLis;
    commentsButt.addClickListener(commLis = new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event)
      {
        UI.getCurrent().setScrollTop(1250); //commentsButt.getWindow().setScrollTop(1250);
      }
    });
    commLeftVL.addComponent(sp=new Label());
    sp.setHeight("65px"); //"50px");

    commLeftVL.addComponent(envelopeButt);
    envelopeButt.addStyleName("m-actionplan-envelope-button");
    envelopeButt.addClickListener(commLis); // same as the link button above

    commentAndViewChainHL.addComponent(sp = new Label());
    sp.setWidth("5px");

    VerticalLayout commMidVL = new VerticalLayout();
    commentAndViewChainHL.addComponent(commMidVL);
    commMidVL.setWidth("535px");
    commMidVL.addComponent(addCommentButt);
    addCommentButt.setCaption("Add Comment");
    addCommentButt.setStyleName(BaseTheme.BUTTON_LINK);
    addCommentButt.addStyleName("borderless");
    addCommentButt.addStyleName("m-actionplan-comments-button");
    addCommentButt.addClickListener(addCommentListener = new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event)
      {
        UI.getCurrent().setScrollTop(1250); //addCommentButt.getWindow().setScrollTop(1250);
        commentPanel.AddCommentClicked(event);
      }
    });

    commMidVL.addComponent(sp = new Label());
    sp.setHeight("5px");

    commMidVL.addComponent(lastCommentLabel = new HtmlLabel());
    lastCommentLabel.setWidth("100%");
    lastCommentLabel.setHeight("94px");
    lastCommentLabel.addStyleName("m-actionplan-textentry");
    lastCommentLabel.addStyleName("m-opacity-75");

    addComponent(viewChainButt, "left:690px;top:140px");
    viewChainButt.setStyleName("m-viewCardChainButton");
    viewChainButt.addClickListener(new ViewCardChainHandler());
    viewChainButt.setId(ACTIONPLAN_VIEW_CARD_CHAIN_BUTTON);
    // This guy sits on the bottom naw, gets covered
    // author list and rfe
    VerticalLayout rightVL = new VerticalLayout();
    this.addComponent(rightVL, "left:830px;top:0px");
    rightVL.setSpacing(false);
    rightVL.setMargin(false);
    rightVL.setWidth(null);

    VerticalLayout listVL = new VerticalLayout();
    listVL.setSpacing(false);
    listVL.addStyleName("m-actionPlanAddAuthorList");
    listVL.addStyleName("m-actionplan-header-container");
    listVL.setHeight(null); 
    listVL.setWidth("190px");

    listVL.addComponent(sp = new Label());
    sp.setHeight("35px");
    sp.setDescription("List of current authors and (invited authors)");

    Label subTitle;
    listVL.addComponent(subTitle = new Label("(invited in parentheses)"));
    subTitle.setWidth(null); // keep it from being 100% wide
    subTitle.setDescription("List of current authors and (invited authors)");
    subTitle.addStyleName("m-actionplan-authorlist-sublabel");
    listVL.setComponentAlignment(subTitle, Alignment.MIDDLE_CENTER);

    rightVL.addComponent(listVL);

    TreeSet<User> ts = new TreeSet<User>(new User.AlphabeticalComparator());
    ts.addAll(actPln.getAuthors());
    TreeSet<User> greyTs = new TreeSet<User>(new User.AlphabeticalComparator());
    greyTs.addAll(actPln.getInvitees());
    authorList = new UserList(null, ts, greyTs);

    listVL.addComponent(authorList);
    authorList.addStyleName("m-greyborder");
    listVL.setComponentAlignment(authorList, Alignment.TOP_CENTER);
    authorList.setWidth("150px");
    authorList.setHeight("95px");
    listVL.addComponent(sp = new Label());
    sp.setHeight("5px");
    listVL.addComponent(addAuthButton);
    listVL.setComponentAlignment(addAuthButton, Alignment.TOP_CENTER);
    addAuthButton.setStyleName("m-actionPlanAddAuthorButt");
    addAuthButton.addClickListener(new AddAuthorHandler());
    addAuthButton.setDescription("Invite players to be authors of this action plan");

    rightVL.addComponent(sp = new Label());
    sp.setHeight("5px");
    rightVL.addComponent(rfeButt);
    rightVL.setComponentAlignment(rfeButt, Alignment.TOP_CENTER);
    // done in handleDisabledments() rfeButt.setStyleName("m-rfeButton");

    // end authorList and rfe
    
    mainVLayout.addComponent(sp = new Label());
    sp.setHeight("5px");
    sp.setWidth("20px");
    // Tabs:
    AbsoluteLayout absL = new AbsoluteLayout();
    mainVLayout.addComponent(absL);
    absL.setHeight("60px");
    absL.setWidth("830px");
    HorizontalLayout tabsHL = new HorizontalLayout();
    tabsHL.setStyleName("m-actionPlanBlackTabs");
    tabsHL.setSpacing(false);

    absL.addComponent(tabsHL,"left:40px;top:0px");

    NewTabClickHandler ntabHndlr = new NewTabClickHandler();

    tabsHL.addComponent(sp=new Label());
    sp.setWidth("19px");
    thePlanTabButt.setStyleName("m-actionPlanThePlanTab");
    thePlanTabButt.addStyleName(ACTIONPLAN_TAB_THEPLAN); // debug
    thePlanTabButt.addClickListener(ntabHndlr);
    tabsHL.addComponent(thePlanTabButt);

    talkTabButt.setStyleName("m-actionPlanTalkItOverTab");
    //talkTabButt.addStyleName(ACTIONPLAN_TAB_TALK);
    talkTabButt.addClickListener(ntabHndlr);
    tabsHL.addComponent(talkTabButt);
    talkTabButt.addStyleName("m-transparent-background"); // initially

    imagesTabButt.setStyleName("m-actionPlanImagesTab");
    imagesTabButt.addStyleName(ACTIONPLAN_TAB_IMAGES);
    imagesTabButt.addClickListener(ntabHndlr);
    tabsHL.addComponent(imagesTabButt);
    imagesTabButt.addStyleName("m-transparent-background"); // initially

    videosTabButt.setStyleName("m-actionPlanVideosTab");
    videosTabButt.addStyleName(ACTIONPLAN_TAB_VIDEO);
    videosTabButt.addClickListener(ntabHndlr);
    tabsHL.addComponent(videosTabButt);
    videosTabButt.addStyleName("m-transparent-background"); // initially

    mapTabButt.setStyleName("m-actionPlanMapTab");
    mapTabButt.addStyleName(ACTIONPLAN_TAB_MAP);
    mapTabButt.addClickListener(ntabHndlr);
    tabsHL.addComponent(mapTabButt);
    mapTabButt.addStyleName("m-transparent-background"); // initially

    newChatLab.setStyleName("m-newChatLabel");
    absL.addComponent(newChatLab, "left:340px;top:15px");
    newChatLab.setVisible(false);

    // stack the pages
    HorizontalLayout hsp = new HorizontalLayout();
    hsp.setHeight("742px"); // allows for differing ghost box heights
    mainVLayout.addComponent(hsp);

    hsp.addComponent(sp = new Label());
    sp.setWidth("45px");

    hsp.addComponent(thePlanTab);
    thePlanTab.initGui();
    
    hsp.addComponent(talkTab);
    talkTab.initGui();
    talkTab.setVisible(false);

    hsp.addComponent(imagesTab);
    imagesTab.initGui();
    imagesTab.setVisible(false);

    hsp.addComponent(videosTab);
    videosTab.initGui();
    videosTab.setVisible(false);
    
    hsp.addComponent(mapTab);
    mapTab.initGui();
    mapTab.setVisible(false);
    
    mainVLayout.addComponent(sp = new Label());
    sp.setHeight("90px");
    
    HorizontalLayout buttLay = new HorizontalLayout();
    buttLay.addStyleName("m-marginleft-60");
    mainVLayout.addComponent(buttLay);
    buttLay.setWidth(ActionPlanPageCommentPanel2.COMMENT_PANEL_WIDTH);
    addCommentButtBottom.setCaption("Add Comment");
    addCommentButtBottom.setStyleName(BaseTheme.BUTTON_LINK);
    addCommentButtBottom.addStyleName("borderless");
    addCommentButtBottom.addStyleName("m-actionplan-comments-button");
    addCommentButtBottom.addClickListener(addCommentListener);
    buttLay.addComponent(addCommentButtBottom);

    if (me.isAdministrator() || me.isGameMaster()) {

      buttLay.addComponent(sp = new Label());
      sp.setWidth("1px"); // "810px");
      buttLay.setExpandRatio(sp, 1.0f);
      ToggleLinkButton tlb = new ToggleLinkButton("View all", "View unhidden only", "m-actionplan-comment-text");
      tlb.setToolTips("Temporarily show all messages, including those marked \"hidden\" (gm)", "Temporarily hide messages marked \"hidden\" (gm)");
      tlb.addStyleName("m-actionplan-comments-button");
      tlb.addOnListener(new ViewAllListener());
      tlb.addOffListener(new ViewUnhiddenOnlyListener());
      buttLay.addComponent(tlb);
      buttLay.addComponent(sp=new Label());
      sp.setWidth("5px");
    }
    // And the comments
    hsp = new HorizontalLayout();
    mainVLayout.addComponent(hsp);
    mainVLayout.addComponent(sp = new Label());
    sp.setHeight("5px");
    hsp.addComponent(sp = new Label());
    sp.setWidth("56px");

    hsp.addComponent(commentPanel);
    commentPanel.initGui();
    
    // Set thumbs
    double thumbs = actPln.getAverageThumb();
    long round = Math.round(thumbs);
    int numApThumbs = (int) (Math.min(round, 3));
    thumbPanel.setNumApThumbs(numApThumbs);

    Integer myRating = actPln.getUserThumbs().get(me);
    if (myRating == null)
      myRating = 0;
    thumbPanel.setNumUserThumbs(myRating);

    helpWantedListener = new HelpWantedListener();
    interestedListener = new InterestedListener();

    handleDisablementsTL();
  }

  @SuppressWarnings("serial")
  class HelpWantedListener implements ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      HelpWantedDialog dial = new HelpWantedDialog(apId);
      UI.getCurrent().addWindow(dial);
      dial.center();
      HSess.close();
    }
  }

  @SuppressWarnings("serial")
  @MmowgliCodeEntry
  @HibernateOpened
  @HibernateClosed
  class InterestedListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      HelpWantedDialog dial = new HelpWantedDialog(apId, true);
      UI.getCurrent().addWindow(dial);
      dial.center();
      HSess.close();
    }
  }

  class TitleHistoryListener implements ClickListener, DoneListener
  {
    private static final long serialVersionUID = 1L;

    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      SortedSet<Edits> titHistSet = ActionPlan.getTL(apId).getTitlesEditHistory();
      HistoryDialog dial = new HistoryDialog(titHistSet, "Title history", "Previous Action Plan titles", "Title", this);
      UI.getCurrent().addWindow(dial);
      dial.center();
      HSess.close();
    }

    // Treat this as MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void doneTL(String sel, int idx /* not used */)
    {
      if (sel != null) {
        HSess.init();
        ActionPlan ap = ActionPlan.getTL(apId);
        String currentTitle = ap.getTitle();
        if (!sel.equals(currentTitle)) {
          ap.setTitleWithHistoryTL(sel); // will push and delete if needed
          ActionPlan.updateTL(ap);
          User u = Mmowgli2UI.getGlobals().getUserTL();
          GameEventLogger.logActionPlanUpdateTL(ap, "title edited", u.getId());
        }
        HSess.close();
      }
    }
  }

  public Object getApId()
  {
    return apId;
  }

  private Component makeIdField(ActionPlan ap)
  {
    HorizontalLayout hl = new HorizontalLayout();
    hl.setMargin(false);
    hl.setSpacing(false);
    hl.setHeight("22px");

    Label lab;
    hl.addComponent(lab = new Label());
    lab.setWidth("270px");
    hl.addComponent(lab = new Label("ID " + ap.getId()));
    hl.setComponentAlignment(lab, Alignment.BOTTOM_LEFT);

    maybeAddHiddenCheckBoxTL(hl, ap);
    return hl;
  }

  @SuppressWarnings("serial")
  private void maybeAddHiddenCheckBoxTL(HorizontalLayout hl, ActionPlan ap)
  {
    User me = Mmowgli2UI.getGlobals().getUserTL();

    if (me.isAdministrator() || me.isGameMaster()) {
      Label sp;
      hl.addComponent(sp = new Label());
      sp.setWidth("80px");

      final CheckBox hidCb = new CheckBox("hidden");
      hidCb.setValue(ap.isHidden());
      hidCb.setDescription("Only game masters see this");
      hidCb.setImmediate(true);
      hl.addComponent(hidCb);
      hl.setComponentAlignment(hidCb, Alignment.BOTTOM_RIGHT);

      hidCb.addValueChangeListener(new ValueChangeListener() {
        @Override
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed        
        public void valueChange(ValueChangeEvent event)
        {
          HSess.init();
          ActionPlan acntp = ActionPlan.getTL(getApId());
          boolean nowHidden = acntp.isHidden();
          boolean tobeHidden = hidCb.getValue();
          if (nowHidden != tobeHidden) {
            acntp.setHidden(tobeHidden);
            ActionPlan.updateTL(acntp);
          }
          HSess.close();
        }
      });

      final CheckBox supIntCb = new CheckBox("super interesting");
      supIntCb.setValue(ap.isSuperInteresting());
      supIntCb.setDescription("Mark plan super-interesting (only game masters see this)");
      supIntCb.setImmediate(true);
      hl.addComponent(supIntCb);
      hl.setComponentAlignment(supIntCb, Alignment.BOTTOM_RIGHT);
      supIntCb.addValueChangeListener(new ValueChangeListener() {

        @Override
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void valueChange(ValueChangeEvent event)
        {
          HSess.init();
          ActionPlan acntp = ActionPlan.getTL(getApId());
          boolean nowSupInt = acntp.isSuperInteresting();
          boolean tobeSupInt = supIntCb.getValue();
          if (nowSupInt != tobeSupInt) {
            acntp.setSuperInteresting(tobeSupInt);
            ActionPlan.updateTL(acntp);
          }
          HSess.close();
        }
      });
    }
  }

  @SuppressWarnings("serial")
  class ViewAllListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      commentPanel.showAllComments(true);
    }
  }

  @SuppressWarnings("serial")
  class ViewUnhiddenOnlyListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      commentPanel.showAllComments(false);
    }
  }

  public void fillHeaderCommentWithLatest(String s, Session sess)
  {
    lastCommentLabel.setValue(MmowgliLinkInserter.insertLinksOob(s,null,sess));
  }

  public void adjustCommentsLinkCaption(int numComments)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(numComments);
    sb.append(' ');
    sb.append(" Comments ");
    commentsButt.setCaption(sb.toString());
  }

  @SuppressWarnings("serial")
  class BrowsePanel extends HorizontalLayout
  {
    BrowsePanel()
    {
      setHeight("19px");
      setWidth("90px");
      setSpacing(false);
      Label sp;

      addComponent(browseBackButt);
      browseBackButt.setStyleName("m-vcrBackButton");
      browseBackButt.addClickListener(new BrowseHandler());
      browseBackButt.setDescription("View previous Action Plan");
      setComponentAlignment(browseBackButt, Alignment.MIDDLE_CENTER);

      addComponent(sp = new HtmlLabel("rate other<br/>plans"));
      sp.setWidth("50px");
      sp.addStyleName("m-centered-10px-label");

      addComponent(browseFwdButt);
      browseFwdButt.setStyleName("m-vcrFwdButton");
      browseFwdButt.addClickListener(new BrowseHandler());
      browseFwdButt.setDescription("View next Action Plan");
      setComponentAlignment(browseFwdButt, Alignment.MIDDLE_CENTER);

      addComponent(sp=new Label());
      sp.setWidth("1px");
      setExpandRatio(sp, 0.5f);
    }
  }

  /** This is a wrapper for the former thumb panel which didn't have a zero link */
  class ThumbPanel extends VerticalLayout implements ClickListener
  {
    private static final long serialVersionUID = 1L;

    InnerPanel pan;
    NativeButton zeroButt;

    @HibernateSessionThreadLocalConstructor
    public ThumbPanel()
    {
      addComponent(pan = new InnerPanel(this));

      HorizontalLayout hl = new HorizontalLayout();
      hl.setMargin(false);
      hl.setSpacing(false);
      addComponent(hl);

      BrowsePanel bp = new BrowsePanel();
      hl.addComponent(bp);

      Label sp;
      hl.addComponent(sp=new Label());
      sp.setWidth("75px");

      Game g = Game.getTL();

      zeroButt = new NativeButton(null, this);
      if (!g.isReadonly())
        hl.addComponent(zeroButt);

      zeroButt.setCaption("no vote");
      zeroButt.setDescription("abstain");
      zeroButt.setStyleName(BaseTheme.BUTTON_LINK);
      zeroButt.addStyleName("borderless");
      zeroButt.addStyleName("m-actionplan-nothumbs-button");

      hl.addComponent(sp = new Label());
      sp.setWidth("25px"); // "15px");
    }

    public void toggleNoThumbs(int numThumbs)
    {
      zeroButt.setVisible(numThumbs > 0);
    }

    public void setNumApThumbs(int n)
    {
      pan.setNumApThumbs(n);
      toggleNoThumbs(n);
    }

    public void setNumUserThumbs(int n)
    {
      pan.setNumUserThumbs(n);
    }

    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      if (event.getButton() == zeroButt) {
        HSess.init();
        pan.setNumUserThumbs(0);
        pan.updateDbTL(0); //  @HibernateUserUpdate
        HSess.close();
      }
      else
        pan.getThumbListener().buttonClick(event);
    }

    @SuppressWarnings("serial")
    class InnerPanel extends HorizontalLayout
    {
      Component[] average = new Component[3];
      Embedded[] greys = new Embedded[3];
      Embedded[] blacks = new Embedded[3];

      Component[] your = new Component[3];
      Button[] greyBs = new Button[3];
      Button[] blackBs = new Button[3];

      ThumbListener tLis = new ThumbListener();
      ThumbPanel outerPan;

      public InnerPanel(ThumbPanel outerPan)
      {
        this.outerPan = outerPan;

        setSpacing(false);
        Label sp;
        MediaLocator mLoc = Mmowgli2UI.getGlobals().getMediaLocator();
        average[0] = greys[0] = mLoc.getGreyActionPlanThumb();
        addComponent(average[0]);
        addComponent(sp = new Label());
        sp.setWidth("10px");
        average[1] = greys[1] = mLoc.getGreyActionPlanThumb();
        addComponent(average[1]);
        addComponent(sp = new Label());
        sp.setWidth("10px");
        average[2] = greys[2] = mLoc.getGreyActionPlanThumb();
        addComponent(average[2]);

        blacks[0] = mLoc.getBlackActionPlanThumb();
        blacks[1] = mLoc.getBlackActionPlanThumb();
        blacks[2] = mLoc.getBlackActionPlanThumb();

        addComponent(sp = new Label());
        sp.setWidth("50px");

        ClickListener lis = new ThumbListener();

        your[0] = greyBs[0] = new NativeButton(null, lis);
        your[0].setStyleName("m-actionPlanGreyThumb");
        greyBs[0].setDescription(ONE_THUMB_TOOLTIP);
        addComponent(your[0]);
        addComponent(sp = new Label());
        sp.setWidth("10px");
        your[1] = greyBs[1] = new NativeButton(null, lis);
        your[1].setStyleName("m-actionPlanGreyThumb");
        greyBs[1].setDescription(TWO_THUMBS_TOOLTIP);
        addComponent(your[1]);
        addComponent(sp = new Label());
        sp.setWidth("10px");
        your[2] = greyBs[2] = new NativeButton(null, lis);
        your[2].setStyleName("m-actionPlanGreyThumb");
        greyBs[2].setDescription(THREE_THUMBS_TOOLTIP);
        addComponent(your[2]);

        blackBs[0] = new NativeButton(null, lis);
        blackBs[0].setStyleName("m-actionPlanBlackThumb");
        blackBs[0].setDescription(ONE_THUMB_TOOLTIP);
        blackBs[1] = new NativeButton(null, lis);
        blackBs[1].setStyleName("m-actionPlanBlackThumb");
        blackBs[1].setDescription(TWO_THUMBS_TOOLTIP);
        blackBs[2] = new NativeButton(null, lis);
        blackBs[2].setStyleName("m-actionPlanBlackThumb");
        blackBs[2].setDescription(THREE_THUMBS_TOOLTIP);

        MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
        boolean gameRo = globs.isGameReadOnly() || globs.isViewOnlyUser();
        for (Button b : blackBs)
          b.setEnabled(!gameRo);
        for (Button b : greyBs)
          b.setEnabled(!gameRo);
      }

      // Called by "no vote" click
      public void setNumUserThumbs(int n)
      {
        if (n < 0) {
          System.err.println("Error passing " + n + " to setNumUserThumbs; min = 0");
          n = 0;
        }
        if (n > 3) {
          System.err.println("Error passing " + n + " to setNumUserThumbs; max = 3");
          n = 3;
        }

        for (int i = 1; i <= 3; i++) {
          if (n < i)
            setUserThumb(i - 1, false);
          else
            setUserThumb(i - 1, true);
        }
        outerPan.toggleNoThumbs(n);
      }

      private void setUserThumb(int i, boolean black)
      {
        int idx = getComponentIndex(your[i]);
        Component old = getComponent(idx);
        Component newC = null;
        if (black)
          newC = blackBs[i];
        else
          newC = greyBs[i];

        your[i] = newC;
        replaceComponent(old, newC);
      }

      public void setNumApThumbs(int n) // 0 to 3
      {
        if (n < 0) {
          System.err.println("Error passing " + n + " to setNumApThumbs; min = 0");
          n = 0;
        }
        if (n > 3) {
          System.err.println("Error passing " + n + " to setNumApThumbs; max = 3");
          n = 3;
        }

        for (int i = 1; i <= 3; i++) {
          if (n < i)
            setApThumb(i - 1, false);
          else
            setApThumb(i - 1, true);
        }
        if (n == 0)
          outerPan.setNumUserThumbs(0);
      }

      private void setApThumb(int i, boolean black)
      {
        int idx = getComponentIndex(average[i]);
        Component old = getComponent(idx);
        Component newC = null;
        if (black)
          newC = blacks[i];
        else
          newC = greys[i];

        average[i] = newC;
        replaceComponent(old, newC);
      }

      public ThumbListener getThumbListener()
      {
        return tLis;
      }

      class ThumbListener implements ClickListener
      {
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void buttonClick(ClickEvent event)
        {
          HSess.init();
          int count = 0;
          for (int i = 0; i < your.length; i++)
            if (event.getButton() == your[i]) {
              count = i + 1;
              break;
            }
          setNumUserThumbs(count);
          updateDbTL(count); //  @HibernateUserUpdate
          HSess.close();
        }
      }
      
      @HibernateUpdate
      @HibernateUserUpdate
      public void updateDbTL(int count)
      {
        ActionPlan ap = ActionPlan.getTL(apId);
        MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
        User me = globs.getUserTL();

        // The ap stores user votes
        ap.setUserThumbValue(me, count);
        ActionPlan.updateTL(ap);

        // Author scores are affected, as is the rater
        globs.getScoreManager().actionPlanWasRatedTL(me, ap, count);
        User.updateTL(me);

        GameEventLogger.logActionPlanUpdateTL(ap, "thumbs changed", me.getId()); // me.getUserName());
      }
    }
  }

  @SuppressWarnings("serial")
  class NewTabClickHandler implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      Button b = event.getButton();
      if(b == currentTabButton)
        return;

      if(currentTabButton == videosTabButt)
        videosTab.hideExistingVideos();

      currentTabButton.addStyleName("m-transparent-background");
      currentTabPanel.setVisible(false);
      currentTabButton = b;

      if(b == thePlanTabButt) {
        thePlanTabButt.removeStyleName("m-transparent-background");
        thePlanTab.setVisible(true);
        currentTabPanel = thePlanTab;
      }
      else if(b == talkTabButt) {
        talkTabButt.removeStyleName("m-transparent-background");
        talkTab.setVisible(true);
        newChatLab.setVisible(false);
        currentTabPanel = talkTab;
      }
      else if(b == imagesTabButt) {
        imagesTabButt.removeStyleName("m-transparent-background");
        currentTabPanel = imagesTab;
        imagesTab.setVisible(true);
      }
      else if(b == videosTabButt) {
        videosTabButt.removeStyleName("m-transparent-background");
        videosTab.setVisible(true);
        videosTab.showExistingVideos();
        currentTabPanel = videosTab;
      }
      else if(b == mapTabButt) {
        mapTabButt.removeStyleName("m-transparent-background");
        currentTabPanel = mapTab;
        mapTab.setVisible(true);
      }
    }
  }

  @SuppressWarnings("serial")
  class ViewCardChainHandler implements ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      ActionPlan ap = ActionPlan.getTL(apId);
      AppEvent evt = new AppEvent(CARDCHAINPOPUPCLICK, ActionPlanPage2.this, ap.getChainRoot().getId());
      Mmowgli2UI.getGlobals().getController().miscEventTL(evt);
      HSess.close();
      return;
    }
  }

  @SuppressWarnings("serial")
  class AddAuthorHandler implements ClickListener
  {
    AddAuthorDialog dial;

    @SuppressWarnings("unchecked")
    @Override
    public void buttonClick(ClickEvent event)
    {
      if (true)/* dial == null) */{
        dial = new AddAuthorDialog((Collection<User>) authorList.getItemIds(), true);
        dial.addListener(new CloseListener() {
          @Override
          @MmowgliCodeEntry
          @HibernateOpened
          @HibernateClosed
          public void windowClose(CloseEvent e)
          {
            if (dial.addClicked) {
              HSess.init();
              Object o = dial.getSelected();
              ActionPlan ap = ActionPlan.getTL(apId);

              if (o instanceof Set<?>)
                handleMultipleUsersTL(ap, (Set<?>) o); //  @HibernateUserUpdate
              else
                handleSingleUserTL(ap, o); //  @HibernateUserUpdate
              HSess.close();
            }
            /*
             * if (dial.addClicked) { Object o = dial.getSelected(); if (o != null && (o instanceof Set<?>)) { Set<User> uids = (Set<User>) o; ActionPlan ap =
             * ActionPlan.get(apId); for (User u : uids) { if (doAuthors && !authorList.contains(u)) { authorList.addItem(u); // this puts at the end of the
             * list ap.getAuthors().add(u); // this causes the db to be hit, then we're notified, and we get sorted } if (doBrokers && !innoList.contains(u)) {
             * innoList.addItem(u); // same as above ap.getInnovators().add(u); } ActionPlan.update(ap); } }
             * 
             * }
             */
          } // windowClose()
        }); // add Listener
      } // dial != null

      UI.getCurrent().addWindow(dial);
      dial.center();
    } // button Click
  } // class

  @SuppressWarnings("unchecked")
  @HibernateUserRead
  private void handleMultipleUsersTL(ActionPlan ap, Set<?> set)
  {
    if (set.size() > 0) {
      Object o = set.iterator().next();
      if (o instanceof User) {
        Iterator<User> itr = (Iterator<User>) set.iterator();
        while (itr.hasNext()) {
          handleUserTL(ap, itr.next());   //@HibernateUserUpdate
        }
      }
      else if (o instanceof QuickUser) {
        Iterator<QuickUser> itr = (Iterator<QuickUser>) set.iterator();
        while (itr.hasNext()) {
          QuickUser qu = itr.next();
          handleUserTL(ap, User.getTL(qu.id));
        }
      }
    }
    ActionPlan.updateTL(ap);
    // app.globs().scoreManager().actionPlanUpdated(apId); // check for scoring changes //todo put this in one place, like ActionPlan.update()
  }

  @HibernateUserRead
  private void handleSingleUserTL(ActionPlan ap, Object o)
  {
    if (o instanceof User) {
      handleUserTL(ap, (User) o);
    }
    else if (o instanceof QuickUser) {
      QuickUser qu = (QuickUser) o;
      handleUserTL(ap, User.getTL(qu.id));  // @HibernateUserUpdate
    }
    ActionPlan.updateTL(ap);
    // app.globs().scoreManager().actionPlanUpdated(apId); // check for scoring changes //todo put this in one place, like ActionPlan.update()
  }
  
  @HibernateUpdate
  @HibernateUserUpdate
  private void handleUserTL(ActionPlan ap, User u)
  {
    boolean needUpdate = false;
    Set<ActionPlan> set = u.getActionPlansInvited();
    if (set == null) {
      u.setActionPlansInvited(new HashSet<ActionPlan>(1));
      set = u.getActionPlansInvited();
      needUpdate = true;
    }
    if (!CreateActionPlanPanel.apContainsByIds(set, ap)) {
      set.add(ap);
      needUpdate = true;
    }
    if (needUpdate)
      User.updateTL(u);

    if (!CreateActionPlanPanel.usrContainsByIds(ap.getInvitees(), u)) {
      ap.addInvitee(u);
      // done above ActionPlan.update(ap);
    }

    AppMaster.instance().getMailManager().actionPlanInviteTL(ap, u);
    
    User me = Mmowgli2UI.getGlobals().getUserTL();
    GameEventLogger.logActionPlanInvitationExtendedTL(ap, me.getUserName(), u.getUserName());
  }

  @SuppressWarnings("serial")
  static class GreyUser extends User
  {
    public GreyUser(String name)
    {
      this.setUserName(name);
    }
  }

  @SuppressWarnings("serial")
  public static class UserList extends ListSelect
  {
    public UserList(String caption, Collection<?> lis)
    {
      super(caption, lis);
      setNullSelectionAllowed(false); // eliminates top blank? yes!
    }

    public UserList(String caption, Collection<User> blackLis, Collection<User> greyList)
    {
      super(caption);
      setNullSelectionAllowed(false);
      setCollection(blackLis);
      IndexedContainer cont = (IndexedContainer) this.getContainerDataSource();
      for (User grey : greyList) {
        cont.addItem(new GreyUser(grey.getUserName()));
      }
      addValueChangeListener(new clickedListener());
      setImmediate(true);
    }

    // Show user profile when author clicked
    class clickedListener implements Property.ValueChangeListener
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void valueChange(Property.ValueChangeEvent event)
      {
        HSess.init();
        Property<?> prop = event.getProperty();
        Object uObj = prop.getValue();
        Long uid = null;
        if (uObj instanceof GreyUser) {
          String s = ((GreyUser) uObj).getUserName();
          User u = User.getUserWithUserNameTL(s);
          if (u == null) {
            System.err.println("ActionPlanPage2.UserList.clickListener...can't get user id");
            return;
          }
          uid = u.getId();
        }
        else if (uObj instanceof User) {
          uid = ((User) uObj).getId();
        }
        Mmowgli2UI.getGlobals().getController().miscEventTL(new AppEvent(SHOWUSERPROFILECLICK, UserList.this, uid));
        HSess.close();
      }
    }

    @Override
    public String getItemCaption(Object itemId)
    {
      if(itemId instanceof GreyUser)
        return "("+((GreyUser)itemId).getUserName()+")";

      return ((User)itemId).getUserName();
    }

    public void setCollection(Collection<?> lis)
    {
      final Container c = new IndexedContainer();
      if (lis != null) {
        for (final Iterator<?> i = lis.iterator(); i.hasNext();) {
          c.addItem(i.next());
        }
      }
      setContainerDataSource(c);
    }

    public void updateFromActionPlan_oobTL(ActionPlan ap)
    {
      Set<User> auths = ap.getAuthors();
      Set<User> invs = ap.getInvitees();

      final Container c = new IndexedContainer();
      if (auths != null)
        for (final Iterator<?> i = auths.iterator(); i.hasNext();)
          c.addItem(i.next());

      if (invs != null)
        for (final Iterator<User> i = invs.iterator(); i.hasNext();)
          c.addItem(new GreyUser(i.next().getUserName()));

      setContainerDataSource(c);
    }

    public Set<User> getBlackUserSetTL()
    {
      Container c = getContainerDataSource();
      Collection<?> coll = c.getItemIds();
      HashSet<User> hs = new HashSet<User>();

      for (Iterator<?> i = coll.iterator(); i.hasNext();) {
        Object o = i.next();
        if (!(o instanceof GreyUser))
          hs.add(User.mergeTL((User) o));
      }
      return hs;
    }
  }

  @SuppressWarnings("serial")
  class MyLayoutListener implements LayoutClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void layoutClick(LayoutClickEvent event)
    {
      if (event.isDoubleClick()) {
        Object clickee = (ListSelect) event.getChildComponent();
        if (clickee instanceof UserList) {
          HSess.init();
          User author = (User) ((UserList) clickee).getValue();
          Mmowgli2UI.getGlobals().getController().miscEventTL(new AppEvent(SHOWUSERPROFILECLICK, ActionPlanPage2.this, author.getId()));
          HSess.close();
        }
      }
    }
  }

  @Override
  public boolean actionPlanUpdatedOobTL(Serializable apId)
  {
    if (apId.equals(this.apId)) {
      ActionPlan ap = ActionPlan.getTL(apId);
      MSysOut.println(ACTIONPLAN_UPDATE_LOGS, "ActionPlanPage2.actionPlanUpdatedOobTL() apId = "+apId);
      if (!titleFocused) { // don't update while being edited
        boolean taRo = titleUnion.isRo();
        titleUnion.setRo(false);
        titleUnion.setValueOobTL(ap.getTitle());
        titleUnion.setRo(taRo);
      }
      authorList.updateFromActionPlan_oobTL(ap);

      commentPanel.actionPlanUpdatedOobTL(apId);

      imagesTab.actionPlanUpdatedOobTL(apId);
      videosTab.actionPlanUpdatedOobTL(apId);
      mapTab.actionPlanUpdatedOobTL(apId);
      talkTab.actionPlanUpdatedOobTL(apId);
      thePlanTab.actionPlanUpdatedOobTL(apId);

      handleDisablements_oobTL();
      return true;
    }
    return false;
  }

  @Override
  public boolean mediaUpdatedOobTL(Serializable medId)
  {
    boolean retn = imagesTab.mediaUpdatedOobTL(medId);
    if (videosTab.mediaUpdatedOobTL(medId))
      retn = true;
    return retn;
  }

  /*
   * We're being informed that a timeout has occurred. If it's this ap and I've got it locked,
   */
  @Override
  public boolean actionPlanEditTimeoutEvent(Serializable apId)
  {
     return false;
  }

  @Override
  public boolean actionPlanEditTimeoutWarningEvent(Serializable apId)
  {
    return false;
  }

  private void handleDisablementsTL()
  {
    handleDisablements_oob(HSess.get());
  }

  private void handleDisablements_oobTL()
  {
    handleDisablements_oob(HSess.get());
  }
  
  private void handleDisablements_oob(Session sess)
  {
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    boolean au = amIAnAuthor(sess);
    imAuthor = au; // save locally
    User me = User.get(globs.getUserID(),sess);
    boolean gm = me.isGameMaster();

    thePlanTab.setICanEdit((gm ||au) && !readonly);

    talkTab.setICanChat((au || gm) && !readonly); // temp until todo below
    talkTab.setICanEdit(au);
    talkTab.setImGM(gm);

    imagesTab.setICanEdit(au && !readonly); // todo, separate into author, gm and ro
    videosTab.setICanEdit(au && !readonly);
    mapTab.setICanEdit(au && !readonly);

    titleUnion.setRo(!au || readonly); // titleTA.setReadOnly (!au || ro);
    titleHistoryButt.setVisible(au && !readonly);
    addAuthButton.setEnabled((gm || au) && !readonly);

    String helpWanted = helpWanted(sess);
    if (imAuthor) {
      if (helpWanted != null) {
        rfeButt.setStyleName("m-rfePendingButton");
        rfeButt.setDescription(helpWanted);
      }
      else {
        rfeButt.setStyleName("m-rfeButton");
        rfeButt.setDescription("Click to request action plan assistance");
      }
      rfeButt.enableAction(true);
      rfeButt.removeClickListener(helpWantedListener);
      rfeButt.removeClickListener(interestedListener);
    }
    else {
      if (helpWanted != null) {
        rfeButt.setStyleName("m-helpWantedButton");
        rfeButt.enableAction(false);
        rfeButt.removeClickListener(interestedListener);
        rfeButt.addListener(helpWantedListener);
        rfeButt.setDescription(helpWanted);
      }
      else {
        rfeButt.setStyleName("m-interestedButton");
        rfeButt.enableAction(false);
        rfeButt.removeClickListener(helpWantedListener);
        rfeButt.addListener(interestedListener);
        rfeButt.setDescription("Click to offer help with this action plan");
      }
    }
    // but, If I'm a guest, disable the rfeButton entirely
    if(me.isViewOnly())
      rfeButt.setEnabled(false);
  }
  
  private boolean amIAnAuthor_oobTL()
  {
    return amIAnAuthor(HSess.get());
  }
  private boolean amIAnAuthor(Session sess)
  {
    // assume read only unless i'm in the list of authors (or invitees)
    ActionPlan ap = ActionPlan.get(apId, sess);
    User me = User.get(Mmowgli2UI.getGlobals().getUserID(), sess);
    // Let admins edit
    if (me.isAdministrator())
      return true;

    Set<User> authors = ap.getAuthors();

    if(authors != null)
      for (User u : authors)
        if(u.getId() == me.getId())
          return true; // yes, I can edit

    return false; // no, I can't edit
  }

  private String helpWanted(Session sess)
  {
    ActionPlan ap = ActionPlan.get(apId,sess);
    return ap.getHelpWanted();
  }

  @Override
  public boolean logUpdated_oobTL(Serializable chatLogId)
  {
    if (this.chatLogId.equals(chatLogId)) {
      if (this.currentTabPanel != talkTab) {
        if (amIAnAuthor_oobTL())
          newChatLab.setVisible(true);
    }
      // Give it to my chat panel
      return talkTab.logUpdated_oobTL(chatLogId);
    }
    return false;
  }

  @Override
  public boolean actionPlanEditBeginEvent(Serializable apId, String msg)
  {
    if (apId != this.apId)
      return false;

    if (imAuthor) {
      Notification notif = new Notification("", "", Notification.Type.HUMANIZED_MESSAGE);
      notif.setPosition(Position.TOP_LEFT);
      notif.setStyleName("m-actionplan-edit-notification");
      notif.setDelayMsec(3000); // 3 secs to disappear

      notif.setCaption("");
      notif.setDescription(msg);
      notif.show(Page.getCurrent());
      return true;
    }
    return false;
  }

  @Override
  public boolean actionPlanEditEndEvent(Serializable apId, String msg)
  {
    return false;
  }

  public static class SaveCancelPan extends HorizontalLayout
  {
    private static final long serialVersionUID = 1L;
    public static int SAVE_BUTTON = 0;
    public static int CANCEL_BUTTON = 1;

    Button canButt, saveButt;

    public SaveCancelPan()
    {
      setSpacing(true);
      setMargin(false);
      Label lab;
      addComponent(lab = new Label());
      lab.setWidth("1px");
      setExpandRatio(lab, 1.0f);
      canButt = new Button("Cancel");
      addComponent(canButt);
      canButt.setStyleName(Reindeer.BUTTON_SMALL);
      saveButt = new Button("Save");
      addComponent(saveButt);
      saveButt.setStyleName(Reindeer.BUTTON_SMALL);
      saveButt.addStyleName("m-greenbutton");
      addComponent(lab=new Label());
      lab.setWidth("5px");
    }

    public void setClickHearers(ClickListener saveLis, ClickListener cancelLis)
    {
      saveButt.addClickListener(saveLis);
      canButt.addClickListener(cancelLis);
    }

    public void setClickHearer(ClickListener lis)
    {
      setClickHearers(lis,lis);
    }
  }

  @SuppressWarnings("serial")
  class BrowseHandler implements ClickListener
  {
    @SuppressWarnings("unchecked")
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      Criteria crit = HSess.get().createCriteria(ActionPlan.class);
      MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
      ActionPlan.adjustCriteriaToOmitActionPlansTL(crit, globs.getUserTL());
      List<Long> lis = (List<Long>) crit.setProjection(Projections.id()).list();

      if (event.getButton() == browseBackButt)
        Collections.reverse(lis);

      int i = 0;
      for (Long id : lis) {
        if (apId.equals(id)) {
          int nxtIdx = i + 1;
          if (nxtIdx >= lis.size())
            nxtIdx = 0;
          globs.getController().miscEventTL(new AppEvent(ACTIONPLANSHOWCLICK, ActionPlanPage2.this, lis.get(nxtIdx)));
        }
        i++;
      }
      HSess.close();
    }
  }

  /* View interface */
  @Override
  @MmowgliCodeEntry
  @HibernateConditionallyOpened
  @HibernateConditionallyClosed
  public void enter(ViewChangeEvent event)
  {
    Object key = HSess.checkInit();
    initGuiTL();
    HSess.checkClose(key);
  }
}
