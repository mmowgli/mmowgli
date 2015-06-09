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

package edu.nps.moves.mmowgli.components;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import static edu.nps.moves.mmowgli.MmowgliEvent.*;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import org.hibernate.Session;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliController;
import edu.nps.moves.mmowgli.MmowgliEvent;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DB;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.WantsGameEventUpdates;
import edu.nps.moves.mmowgli.messaging.WantsGameUpdates;
import edu.nps.moves.mmowgli.messaging.WantsMoveUpdates;
import edu.nps.moves.mmowgli.utility.IDNativeButton;
import edu.nps.moves.mmowgli.utility.MediaLocator;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * Header.java Created on Feb 5, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Header extends AbsoluteLayout implements MmowgliComponent, WantsGameEventUpdates, WantsMoveUpdates, WantsGameUpdates
{
  private static final long serialVersionUID = 3247182543408578788L;
  private static String user_profile_tt = "View your player profile";
  private static String search_tt       = "Search card, plan and player data";
  
  private Button leaderBoardButt;
  private Button mapButt;
  private Button playIdeaButt;
  private Button signOutButt;
  
  private IDNativeButton callToActionButt;
  private IDNativeButton takeActionButt;
  private IDNativeButton userNameButt;
  private IDNativeButton searchButt;
  
  private Link learnMoreButt;
  private Link liveBlogButt;
  private Link blogHeadlinesLink;
  
  private Embedded avatar;
  
  private Label implPtsLab;
  private Label explorPtsLab;
  private Label moveNumLab;
  private Label brandingLab;

  private TextField searchField;
  private static String leaderboard_tt = "Players with highest scores";
  private static String liveblog_tt    = "Latest news and info (opens in a new window or tab)";
  private static String learnmore_tt   = "Game instructions (opens in a new window or tab)";
  private static String signout_tt     = "Thanks for playing!";
  
  private static String w_implPoints = "50px";
  private static String h_implPoints = "14px";
  private static String w_explPoints = "65px";
  private static String h_explPoints = "22px";
  private static String w_movenum = "300px";
  private static String h_movenum = "20px";
  private static String w_movetitle = "300px";
  private static String h_movetitle = "20px";
  private int buttonChars = 0;
  
  private MediaLocator mediaLoc;
  
  @HibernateSessionThreadLocalConstructor
  public Header()
  {
    Game game = Game.getTL();
    GameLinks gl = GameLinks.getTL();
    mediaLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    
    leaderBoardButt = makeSmallButt("Leaderboard", LEADERBOARDCLICK, leaderboard_tt);
    mapButt         = makeSmallButt("Map",         MAPCLICK,         "View "+game.getMapTitle());
    liveBlogButt    = makeSmallLink("Game Blog",   liveblog_tt,      gl.getBlogLink());
    learnMoreButt   = makeSmallLink("Learn More",  learnmore_tt,     gl.getLearnMoreLink());
    buttonChars = 11+3+9+10;  // num chars of above
    
    signOutButt     = makeSmallButt("Sign Out",    SIGNOUTCLICK,     signout_tt);
    
    if(game.isActionPlansEnabled())
      takeActionButt   = makeTakeActionButt();
    playIdeaButt     = makePlayIdeaButt(game);
    userNameButt     = makeUserNameButt("usernamehere", SHOWUSERPROFILECLICK);
    searchButt       = makeSearchButt("", SEARCHCLICK, search_tt);

    callToActionButt = makeCallToActionButton();
    
    avatar = new Embedded();
    searchField = new TextField();
    searchField.setDescription(search_tt);
    implPtsLab      = makeImplementationPtsLabel(w_implPoints,h_implPoints);
    explorPtsLab    = makeExplorationPtsLabel(w_explPoints,h_explPoints);

    blogHeadlinesLink = makeBlogHeadlineLink();
    moveNumLab = new HtmlLabel();
    moveNumLab.setWidth(w_movenum);
    moveNumLab.setHeight(h_movenum);
    moveNumLab.addStyleName("m-header-movenum-text");

    brandingLab = new HtmlLabel();
    brandingLab.setWidth(w_movetitle);
    brandingLab.setHeight(h_movetitle);
    brandingLab.addStyleName("m-header-branding-text"); //m-header-movetitle-text");
  }
   
  private void addDivider(HorizontalLayout hl, int buttonChars)
  {
    int sp;
    if(buttonChars>=39)
      sp = 3;
    else if(buttonChars<=33)
      sp = 9;
    else
      sp = buttonChars - 30;
    
    Label lab = new Label();
    hl.addComponent(lab);
    lab.setWidth(""+sp+"px");
    Embedded embedded = new Embedded(null,mediaLoc.getImage("headerDivider1w48h.png"));
    hl.addComponent(embedded);
    lab = new Label();
    hl.addComponent(lab);
    lab.setWidth(""+sp+"px");
  }
  
  private static String pos_playIdeaButt = "top:50px;left:686px";
  private static String pos_takeActionButt = "top:49px;left:835px";
  private static String pos_banner        = "top:0px;left:330px";
  @SuppressWarnings("serial")
  @Override
  public void initGui()
  {
    setWidth(HEADER_W);
    setHeight(HEADER_H);
    Game g = Game.getTL();
    GameLinks gl = GameLinks.getTL();
    
    Embedded embedded = new Embedded(null, mediaLoc.getHeaderBackground());
    addComponent(embedded, "top:0px;left:0px");
    
    if(g.isActionPlansEnabled()) {
      embedded = new Embedded(null,mediaLoc.getImage("scoretext200w50h.png"));
      addComponent(embedded, "top:52px;left:63px");
      addComponent(explorPtsLab,"top:55px;left:260px");
      addComponent(implPtsLab,"top:79px;left:247px");
    }
    else {
      embedded = new Embedded(null,mediaLoc.getImage("scoretextoneline200w50h.png"));
      addComponent(embedded, "top:52px;left:73px");
      addComponent(explorPtsLab,"top:65px;left:205px");
    }
    
    Resource res = mediaLoc.getHeaderBanner(g);
    if(res != null) {
      embedded = new Embedded(null, res);
      addComponent(embedded, pos_banner);
    }
    HorizontalLayout buttHL = new HorizontalLayout();
    buttHL.setSpacing(false);
    buttHL.setMargin(false);
    buttHL.setWidth("291px");
    buttHL.setHeight("45px");
    addComponent(buttHL,"top:1px;left:687px");
    
    Label lab;
    boolean armyHack = gl.getFixesLink().toLowerCase().contains("armyscitech") || gl.getGlossaryLink().toLowerCase().contains("armyscitech");
    if(armyHack)
      buttonChars = buttonChars-3+9;  // Replace "Map" with "Resources
    buttHL.addComponent(lab=new Label());
    lab.setWidth("1px");
    buttHL.setExpandRatio(lab, 0.5f);
    buttHL.addComponent(leaderBoardButt);
    buttHL.setComponentAlignment(leaderBoardButt, Alignment.MIDDLE_CENTER);
    addDivider(buttHL,buttonChars);

    // Hack
    if(armyHack) { //Hack
      Link resourceLink = makeSmallLink("Resources", "", "http://futures.armyscitech.com/resources/");
      buttHL.addComponent(resourceLink);
      buttHL.setComponentAlignment(resourceLink, Alignment.MIDDLE_CENTER);
    }
    else {
      buttHL.addComponent(mapButt);
      buttHL.setComponentAlignment(mapButt, Alignment.MIDDLE_CENTER);
    }
    addDivider(buttHL,buttonChars);
    buttHL.addComponent(liveBlogButt);
    buttHL.setComponentAlignment(liveBlogButt, Alignment.MIDDLE_CENTER);
    addDivider(buttHL,buttonChars);
    buttHL.addComponent(learnMoreButt); 
    buttHL.setComponentAlignment(learnMoreButt, Alignment.MIDDLE_CENTER);

    buttHL.addComponent(lab=new Label());
    lab.setWidth("1px");
    buttHL.setExpandRatio(lab, 0.5f);
        
    addComponent(playIdeaButt,    pos_playIdeaButt);
    
    if(g.isActionPlansEnabled()) {
      addComponent(takeActionButt,  pos_takeActionButt);
      toggleTakeActionButt(true); // everbody can click it me.isGameMaster());
    }
    else if(armyHack) {
      embedded = new Embedded(null,mediaLoc.getImage("armylogoxpntbg80w80h.png"));
      addComponent(embedded, "top:54px;left:864px");
    }

    Serializable uid = Mmowgli2UI.getGlobals().getUserID();
    refreshUser(uid, HSess.get());   // assume in vaadin transaction here
    
    avatar.setWidth(HEADER_AVATAR_W);
    avatar.setHeight(HEADER_AVATAR_H);
    avatar.setDescription(user_profile_tt);
    avatar.addClickListener(new MouseEvents.ClickListener() {
      @Override
      public void click(com.vaadin.event.MouseEvents.ClickEvent event)
      {
        userNameButt.buttonClick(new ClickEvent(userNameButt));
      }
    });
    userNameButt.setDescription(user_profile_tt);
    addComponent(userNameButt, HEADER_USERNAME_POS);
    addComponent(avatar, "top:13px;left:6px"); //HEADER_AVATAR_POS);
    
    searchField.setWidth("240px");
//  searchField.setHeight("18px");    // this causes a text _area_ to be used, giving me two lines, default height is good, style removes borders
    searchField.setInputPrompt("Search");
    searchField.setImmediate(true);
    searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);
    searchField.setTextChangeTimeout(5000); // ms
    searchField.addStyleName("m-header-searchfield");
    searchField.addValueChangeListener(new Property.ValueChangeListener()
    {
      private static final long serialVersionUID = 1L;
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void valueChange(ValueChangeEvent event)
      {
        HSess.init();
        handleSearchClickTL();
        HSess.close();
        /*
        searchButt.focus();  // make the white go away
        String s = event.getProperty().getValue().toString();
        if (s.length() > 0) {
          MmowgliController controller = Mmowgli2UI.getGlobals().getController();
          controller.handleEvent(SEARCHCLICK, s, searchField);
        } */
      }
    });
    searchButt.enableAction(false); // want a local listener
    searchButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        handleSearchClickTL();  
        HSess.close();
      }      
    });
    addComponent(searchField,"top:107px;left:74px"); //"top:110px;left:74px");
    addComponent(signOutButt, "top:25px;left:250px"); //"top:18px;left:250px");
    addComponent(searchButt, "top:105px;left:30px"); //"top:100px;left:180px");

    MessageUrl mu = MessageUrl.getLastTL();
    if(mu != null)
      decorateBlogHeadlinesLink(mu);
    addBlogHeadlinesLink("top:147px;left:20px");
    
    String headline = blogHeadlinesLink.getCaption();
    if(headline != null && headline.length()>0) {
      // Add Window.Notification relaying the same info as the BlogHeadLinesLink
      Notification note = new Notification("Today's News", "<br/>"+headline, Notification.Type.WARNING_MESSAGE, true);
      note.setPosition(Position.TOP_CENTER);
      note.setDelayMsec(5*1000);
      // Yellow is more an attention getter
      note.setStyleName("m-blue");
      note.show(Page.getCurrent());
    }
    
    addComponent(callToActionButt, "top:0px;left:333px");
    /* The css has a height, width and even a background, but stupid IE will only properly size the button if an image is
     * used.  Therefore we use an a transparent png of the proper size */
    MediaLocator medLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    callToActionButt.setIcon(medLoc.getEmpty353w135h());
    
    Move move = g.getCurrentMove();  
    if (g.isShowHeaderBranding()) {
      Media brand = g.getHeaderBranding();
      if(brand != null) {
        Embedded bremb = new Embedded(null,mediaLoc.locate(brand));
        addComponent(bremb,"top:0px;left:333px");
      }
      else {
        brandingLab.setHeight("30px");
        setBrandingLabelText(move,g);
        addComponent(brandingLab, "top:0px;left:333px"); //HEADER_MOVETITLE_POS);  //"top:151px;left:476px";      
      } 
    }
    if(move.isShowMoveBranding()) {
      moveNumLab.setValue(move.getName());
      addComponent(moveNumLab, "top:103px;left:333px");
    }
/*    if(user != null && (user.isAdministrator() || user.isGameMaster() || user.isDesigner() )) { // has a menu
      //  fouoLink.addStyleName("m-absolutePositioning");
        addComponent(fouoLink,"top:-10px;left:400px");
    }
    else
      addComponent(fouoLink,"top:0px;left:400px");
    
    fouoLink.setVisible(g.isShowFouo());
    */
  }
  
  private void setBrandingLabelText(Move m, Game g)
  {
    if(!g.isShowHeaderBranding())
      return;
    
    if(g.getHeaderBranding()==null) {
      String title = m.getTitle();
      title = title==null?"":title;
      brandingLab.setValue(title);      
    }
  }
  
  private void handleSearchClickTL()
  {
    searchButt.focus();  // make the white go away
    String s = searchField.getValue().toString();
    //if (s.length() > 0) {
      MmowgliController controller = Mmowgli2UI.getGlobals().getController();
      controller.handleEventTL(SEARCHCLICK, s, searchField);
    //}   
  }

  private void addBlogHeadlinesLink(String pos)
  {
    VerticalLayout vl = new VerticalLayout();
    vl.setWidth("955px");
    vl.addComponent(blogHeadlinesLink);
    vl.setComponentAlignment(blogHeadlinesLink, Alignment.TOP_CENTER);
    addComponent(vl,pos);
    
  }
  
  private Link makeBlogHeadlineLink()
  {
    Link link = new Link("",null);
    link.addStyleName("m-header-blogheadline-link");
    link.setTargetName(PORTALTARGETWINDOWNAME);

    return link;
  }

  public boolean refreshUserTL(Object uid)
  {
    return refreshUser(uid,HSess.get());
  }
  
  public boolean refreshUser(Object uid, Session sess)  // also called oob
  {
    if(!uid.equals(Mmowgli2UI.getGlobals().getUserID()))
       return false;
    
    MSysOut.println(DEBUG_LOGS, "User.get(sess) Header.refreshUser(sess)");    
    User u = User.get(uid, sess);
    userNameButt.setCaption(u.getUserName());
    userNameButt.setParam(uid);
    if(u.getAvatar() != null) {
      avatar.setSource(mediaLoc.locateAvatar(u.getAvatar().getMedia()));
    }
    
    float pts = u.getBasicScore();
    float iPts = u.getInnovationScore();
    explorPtsLab.setValue(formatFloat(pts));
    implPtsLab.setValue(formatFloat(iPts));
    
    // always assume we need an update if oob
    return true;
  }

  private NumberFormatter nf = new NumberFormatter(new DecimalFormat("####0"));  
  private String formatFloat(float f)
  {
    try {
      return nf.valueToString(f);
    }
    catch(ParseException ex) {
      return "invld";
    }  
  }

  private Label makeImplementationPtsLabel(String width, String height)
  {
    Label lab = makeScoreLabel("m-implscore-text",width, height);
    lab.setDescription("Points for action plans");
    return lab;
  }
  
  private Label makeScoreLabel(String style, String width, String height)
  {
    Label lab = new Label();
    lab.setWidth(width);
    lab.setHeight(height);
    lab.addStyleName(style);
    return lab;
  }
  
  private Label makeExplorationPtsLabel(String width, String height)
  {
    Label lab = makeScoreLabel("m-explscore-text",width, height);
    lab.setDescription("Points for idea cards");
    return lab;
  }
  
  private IDNativeButton makeSmallButt(String text, MmowgliEvent mEv, String tooltip)
  {
    IDNativeButton butt = makeButt(text, mEv);
    butt.addStyleName("m-header-grey-text");
    butt.addStyleName("m-padding-0");
    butt.setDescription(tooltip);
    return butt;
  }
  
   private Link makeSmallLink(String caption, String tooltip, String url)
  {
    Link link = new Link(caption,new ExternalResource(url));
    link.setDescription(tooltip);
    link.addStyleName("m-header-link");
    link.setTargetName(PORTALTARGETWINDOWNAME);

    return link;
  }
  private IDNativeButton makePlayIdeaButt(Game g)
  {
    Resource res = mediaLoc.getPlayIdeaButt(g);
    if(res == null)
      return makeBigButt("PLAY AN IDEA", PLAYIDEACLICK);
    
    IDNativeButton butt = makeButt(null,PLAYIDEACLICK);
    mediaLoc.decoratePlayIdeaButton(butt,g);
    butt.addStyleName("m-playIdeaButton");
    butt.setDescription("Review and play idea cards");
    butt.setId(PLAY_AN_IDEA_BLUE_BUTTON);
    return butt;
  }
    
  private IDNativeButton makeTakeActionButt()
  {
    IDNativeButton butt = new IDNativeButton(null,TAKEACTIONCLICK);
    butt.setStyleName("m-takeActionButton");
    return butt;
  }
  
  private IDNativeButton makeCallToActionButton()
  {
    IDNativeButton butt = makeButt("",CALLTOACTIONCLICK);
    butt.addStyleName("m-callToActionButton");
    butt.setWidth("353px");
    butt.setHeight("135px");
    butt.setDescription("Call to action");
    return butt;
  }
  
  private void toggleTakeActionButt(boolean enable)
  {
    takeActionButt.setStyleName(enable?"m-takeActionButton":"m-takeActionButtonDisabled");
    takeActionButt.setDescription(enable?"Review and update Action Plans":"Action Plans not enabled in this move");
    takeActionButt.enableAction(enable);
  }

  private IDNativeButton makeBigButt(String text, MmowgliEvent mEv)
  {
    IDNativeButton butt = makeButt(text, mEv);
    butt.addStyleName("m-header-big-text");
    return butt;
  }

  private IDNativeButton makeUserNameButt(String text, MmowgliEvent mEv)
  {
    IDNativeButton butt = makeButt(text, mEv);
    butt.setEvent(SHOWUSERPROFILECLICK);
    butt.addStyleName("m-header-username-text");
    butt.setDescription("View user profile");
    butt.setParam(Mmowgli2UI.getGlobals().getUserID());

    return butt;
  }

  private IDNativeButton makeButt(String text, MmowgliEvent mEv)
  {
    IDNativeButton butt = new IDNativeButton(text, mEv);
    butt.addStyleName("borderless");
    return butt;
  }

  private IDNativeButton makeSearchButt(String text, MmowgliEvent mEv, String tooltip)
  {
    IDNativeButton butt = new IDNativeButton(text, mEv);
    butt.addStyleName("m-header-search-text");
    butt.addStyleName("borderless");
    butt.setImmediate(true);
    butt.setWidth("25px");
    butt.setHeight("25px");
    butt.setDescription(tooltip);
    return butt;
  }

  private void decorateBlogHeadlinesLink(MessageUrl mu)
  {
   if(mu != null) {
     blogHeadlinesLink.setCaption(mu.getText());
     blogHeadlinesLink.setResource(new ExternalResource(mu.getUrl()));
     blogHeadlinesLink.setDescription(mu.getTooltip());
    }
  }
  
  public boolean gameEventLoggedOobTL(Object evId)
  {
    GameEvent ev = DB.getRetry(GameEvent.class, evId, null, HSess.get());
    if(ev == null) {
      System.err.println("ERROR: Header.gameEventLoggedOobTL(): GameEvent matching id "+evId+" not found in db.");
    }
    else if(ev.getEventtype() == GameEvent.EventType.BLOGHEADLINEPOST) {
      MessageUrl mu = MessageUrl.getTL(ev.getParameter());
      decorateBlogHeadlinesLink(mu);
      return true;
    }
    return false;
  }
  
  @Override
  public boolean moveUpdatedOobTL(Serializable mvId)
  {
    Move m = DB.getRetry(Move.class, mvId, null, HSess.get());
    if(m == null) {
      System.err.println("ERROR: Header.moveUpdatedOob: Move matching id "+mvId+" not found in db.");
    }
    else if(Move.getCurrentMoveTL().getId() == m.getId()) {
      setBrandingLabelText(m, Game.getTL());
      return true;
    }
    return false;
  }

  @Override
  public boolean gameUpdatedExternallyTL(Object nullObj)
  {
    return false;
  } 
 }
