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

package edu.nps.moves.mmowgli.modules.userprofile;

import static edu.nps.moves.mmowgli.MmowgliConstants.DEBUG_LOGS;
import static edu.nps.moves.mmowgli.MmowgliConstants.PORTALTARGETWINDOWNAME;
import static edu.nps.moves.mmowgli.db.Badge.BADGE_AP_AUTHOR;
import static edu.nps.moves.mmowgli.db.Badge.BADGE_EIGHT_ID;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

import javax.swing.text.NumberFormatter;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.*;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.pii.EmailPii;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.WantsUserUpdates;
import edu.nps.moves.mmowgli.modules.gamemaster.CreateActionPlanPanel;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.BrowserWindowOpener;
import edu.nps.moves.mmowgli.utility.MediaLocator;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
/**
 * UserProfile3Top.java
 * Created on Oct 7, 2011
 * Updated on Mar 13, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: UserProfile3Top.java 3303 2014-01-31 00:13:15Z tdnorbra $
 */
public class UserProfile3Top extends AbsoluteLayout implements MmowgliComponent, WantsUserUpdates
{
  private static final long serialVersionUID = 1661825886438492459L;

  private Object uid;

  private Label nameLab;
  private Label learnLab;
  private Label coverUpChangePW;
  private NativeButton avatarButt;
  private NativeButton manageAwardsButt;
  private Label locationLab;
  private Label expertiseLab;
  private Label affiliationLab;
  private Label levelLab;
  private Label scoresLab;
  private Label exploreScoreLab;
  private Label innovateScoreLab;
  private Button changePWButt;
  private Button sendEmailButt;
  private Button changeEmailButt;
  private CheckBox externMailCB;
  private CheckBox ingameMailCB;
  private CheckBox followCB;
  private TextField locationTF;
  private TextArea affiliationTA;
  private BoundAffiliationCombo affiliationCombo;
  private TextArea expertiseTA;
  private TextArea learnTA;
  private GridLayout badgeLayout;
  private NativeButton[] badgeButts;
  private Object currentAvatarId;

  private int NAME_LEFT = 484;
  private int LEVEL_LEFT = 747;
  private int CHANGE_PW_LEFT = 480;

  private int LOCATION_LEFT = 330;
  private int LOCATION_LAB_LEFT=240;
  private int EXPERTISE_LEFT = LOCATION_LEFT;
  private int EXPERTISE_LAB_LEFT = LOCATION_LAB_LEFT;
  private int AFFILIATION_LEFT = EXPERTISE_LEFT;
  private int AFFILIATION_LAB_LEFT = EXPERTISE_LAB_LEFT;

  private int LEARN_LEFT      = LOCATION_LEFT;
  private int BADGES_LEFT     = 658;
  private int AVATAR_LEFT     = 49;
  private int AVATAR_TOP      = 141;
  private int BADGES_TOP      = 168;
  private int EXPERTISE_TOP   = 170;
  private int EXPERTISE_H     = 60; //120;
  private int EXPERTISE_W     = 280;
  private int AFFILIATION_TOP = 235;
  private int AFFILIATION_H   = 55;
  private int AFFILIATION_W   = 280;

  private int COVERUPCHANGEPW_H=25;
  private int COVERUPCHANGEPW_W=140;
  private int LOCATION_TOP = 142;
  private int LOCATION_H = 22;
  private int LOCATION_W = EXPERTISE_W;
  private int BADGES_W = 242;
  private int BADGES_H = 198;

  private int SCORES_LAB_W = 273;
  private int SCORES_LAB_H = 67;
  private int SCORES_LAB_TOP = 63;
  private int SCORES_LAB_LEFT = 80;

  private int EXPLORE_SCORE_LAB_TOP = 74;
  private int EXPLORE_SCORE_LAB_LEFT = 352;
  private int EXPLORE_SCORE_LAB_WIDTH = 100;
  private int EXPLORE_SCORE_LAB_HEIGHT = 20;

  private int INNOVATION_SCORE_LAB_TOP = 104;
  private int INNOVATION_SCORE_LAB_LEFT = 340;
  private int INNOVATION_SCORE_LAB_WIDTH = 100;
  private int INNOVATION_SCORE_LAB_HEIGHT = 20;

  private int LEARN_LAB_TOP = 297;
  private int LEARN_LAB_LEFT = 240;
  private int LEARN_LAB_H_ORIG = 45;
  private int LEARN_LAB_H = 60;
  private int LEARN_LAB_W = 75;
  private int LEARN_TOP = 297;
  private int LEARN_W = EXPERTISE_W;
  private int LEARN_H = 58;
  private int NAME_TOP = 35;
  private int LEVEL_TOP = NAME_TOP;
  private int NAME_W = 244;
  private int LEVEL_W = 140;
  private int NAME_H = 24;
  private int LEVEL_H = NAME_H;
  private int EXTERN_MAIL_TOP = 67;
  private int EXTERN_MAIL_LEFT = 600;
  private int INGAME_MAIL_TOP = EXTERN_MAIL_TOP;
  private int INGAME_MAIL_LEFT = 765;
  private int CHANGE_PW_TOP = EXTERN_MAIL_TOP;
  private int SENDEMAIL_TOP = 100;
  private int SENDEMAIL_LEFT = 700;

  private int FOLLOW_W= 200;
  private int FOLLOW_H = 20;
  private int FOLLOW_TOP = SENDEMAIL_TOP;
  private int FOLLOW_LEFT = SENDEMAIL_LEFT-230;

  private int CHANGEEMAIL_TOP= SENDEMAIL_TOP;//DEREK
  private int CHANGEEMAIL_LEFT=SENDEMAIL_LEFT-100;

  private int TITLE_TOP = 12;
  private int TITLE_LEFT = 100;

  private boolean itsSomebodyElse = false;
  private boolean gameReadOnly = false;
  private boolean imGuestAccount = false;
  private boolean imAdmin = false;
  private boolean imAdminOrGameMaster = false;

  private ChangePasswordDialog.PasswordPacket packet;
  private ChangeEmailDialog.EmailPacket emailPacket;

  private boolean listenersDisabled=false;
  
  @HibernateSessionThreadLocalConstructor
  @HibernateUserRead
  public UserProfile3Top(Object uid)
  {
    this.uid = uid;

    User u = User.getTL(uid);
    User me = Mmowgli2UI.getGlobals().getUserTL();
    itsSomebodyElse = (u.getId() != me.getId());
    imGuestAccount = me.isViewOnly();
    imAdmin = me.isAdministrator();
    imAdminOrGameMaster = me.isAdministrator() || me.isGameMaster();

    Game game = Game.getTL();
    gameReadOnly = game.isReadonly();

    scoresLab = new Label();
    exploreScoreLab = new Label();
    innovateScoreLab = new Label();

    if (game.isActionPlansEnabled()) {
      scoresLab.addStyleName("m-userProfile3-scores-label");

      exploreScoreLab.addStyleName("m-userProfile3-explorescore-label");
      exploreScoreLab.setValue(formatFloat(u.getCombinedBasicScore())); // u.getBasicScore()));
      exploreScoreLab.setDescription("rounds combined");

      innovateScoreLab.addStyleName("m-userProfile3-explorescore-label");
      innovateScoreLab.setValue(formatFloat(u.getCombinedInnovScore())); // u.getInnovationScore()));
      innovateScoreLab.setDescription("rounds combined");
    }
    else {
      scoresLab.addStyleName("m-userProfile3-exploreonlyscore-label");

      exploreScoreLab.addStyleName("m-userProfile3-explorescorebig-label");
      exploreScoreLab.setValue(formatFloat(u.getCombinedBasicScore())); // u.getBasicScore()));
      exploreScoreLab.setDescription("rounds combined");
    }

    avatarButt = new NativeButton();

    locationLab= new Label();
    locationLab.setDescription("Where are you from?");

    expertiseLab= new Label();
    expertiseLab.setDescription("Enter a short description of your pertinent expertise.");

    affiliationLab = new Label("AFFILIATION");
    affiliationLab.setDescription("Your professional affliation");
    affiliationLab.addStyleName("m-userProfile3-learn-label");

    coverUpChangePW= new Label();

    nameLab = new Label();
    nameLab.setValue(u.getUserName());

    learnLab = new Label();

    GameQuestion q = u.getQuestion();

    String questSumm=null;
    if(q != null)
      questSumm = q.getSummary();
    questSumm = questSumm==null?"":questSumm;
    learnLab.setValue(questSumm);

    String questDesc=null;
    if(q != null)
      questDesc = q.getQuestion();
    questDesc = questDesc==null?"":questDesc;
    learnLab.setDescription(questDesc);

    levelLab = new Label();
    Level lev = u.getLevel();
    if(u.isGameMaster()) {
      Level l = Level.getLevelByOrdinalTL(Level.GAME_MASTER_ORDINAL);
      if(l != null)
        lev = l;
    }
    levelLab.setValue(lev.getDescription());

    changePWButt = new NativeButton();

    externMailCB = new CheckBox();
    externMailCB.setValue(u.isOkEmail());
    ingameMailCB = new CheckBox();
    ingameMailCB.setValue(u.isOkGameMessages());

    String s;
    locationTF = new TextField();
    s = u.getLocation();
    locationTF.setValue(s==null?"":s);
    expertiseTA = new TextArea();
    s = u.getExpertise();
    expertiseTA.setValue(s==null?"":s);

    affiliationTA = new TextArea();
    affiliationTA.setInputPrompt("optional additional affiliations");
    affiliationCombo = new BoundAffiliationCombo();
    affiliationCombo.setInputPrompt("required");  // override "optional"
    affiliationCombo.setNewItemsAllowed(false);
    s = u.getAffiliation();
    fillAffiliation(s);

    learnTA = new TextArea();
    s = u.getAnswer();
    learnTA.setValue(s==null?"":s);
    badgeButts = new NativeButton[8];
    badgeLayout = new GridLayout(4,3); //col,row
    if(itsSomebodyElse) {
      followCB = new CheckBox();
      followCB.setValue(amIFollowing(me));
      followCB.addValueChangeListener(new FollowListener());
    }

    if(imAdmin) {
      manageAwardsButt = new NativeButton("manage awards");
      manageAwardsButt.setDescription("<p>accessible to game administrators</p>");
      manageAwardsButt.addClickListener(new ManageAwardsListener());
    }
  }
  
  private String AFFILIATION_DELIMITER = "\t";
  @SuppressWarnings("unchecked")
  private void fillAffiliation(String s)
  {
    s = s==null?"":s;
    String[]sa = s.split(AFFILIATION_DELIMITER);
    Collection<Affiliation> contents = (Collection<Affiliation>)affiliationCombo.getItemIds();

    for(Affiliation a : contents) {
      if(a.getAffiliation().equalsIgnoreCase(sa[0])) {
        affiliationCombo.setValue(a);
        String taStr = s.substring(sa[0].length()).trim();
        affiliationTA.setValue(taStr);
        return;
      }
    }
    affiliationTA.setValue(s);
  }

  private String buildAffiliation()
  {
    Affiliation o = (Affiliation)affiliationCombo.getValue();
    String s = o==null?"":o.getAffiliation();
    String ss = affiliationTA.getValue().toString();
    if(s.equalsIgnoreCase("required") || s.equalsIgnoreCase("optional"))
      s = "";
    String ret = s + AFFILIATION_DELIMITER + ss.trim();
    return ret.trim();
  }

  private boolean amIFollowing(User me)
  {
    Set<User> buds = me.getImFollowing();
    for (User budU : buds) {
      if (budU.getId() == (Long) uid) {
        return true;
      }
    }
    return false;
  }

  private boolean iCanEdit()
  {
    return !gameReadOnly && !imGuestAccount;
  }

  @Override
  public void initGui()
  {
    User u = User.getTL(uid);
    Game g = Game.getTL();

    setWidth("945px");
    setHeight("579px");

    addStyleName("m-userprofile3top");

    if(iCanEdit() && itsSomebodyElse){
    	coverUpChangePW.setWidth(buildPxString(COVERUPCHANGEPW_W));
        coverUpChangePW.setHeight(buildPxString(COVERUPCHANGEPW_H));
	    coverUpChangePW.addStyleName("m-coverUpChangePW");
	    addComponent(coverUpChangePW, buildLocString(CHANGE_PW_TOP,CHANGE_PW_LEFT));
    }
    setAvatarIconFromDb();
    avatarButt.setHeight("151px");
    avatarButt.setWidth("151px");
    avatarButt.addStyleName("m-userprofile3-avatar");

    avatarButt.setDescription("Choose your avatar image");

    addComponent(avatarButt,buildLocString(AVATAR_TOP,AVATAR_LEFT));

    if(iCanEdit() && !itsSomebodyElse){
        String changeEmail = "Change email";
        changeEmailButt= new NativeButton(changeEmail);
        changeEmailButt.addStyleName("m-userprofile3-changeemailbutt");
        addComponent(changeEmailButt, buildLocString(CHANGEEMAIL_TOP,CHANGEEMAIL_LEFT));
        changePWButt.addStyleName("m-userprofile3-changepasswordbutt");
        addComponent(changePWButt,buildLocString(CHANGE_PW_TOP,CHANGE_PW_LEFT));
    }

    String prompt = itsSomebodyElse?"Send "+u.getUserName()+" private mail":"Send yourself test mail";
    sendEmailButt = new NativeButton(prompt);
    sendEmailButt.addStyleName("m-userprofile3-sendemailbutt");

    if( iCanEdit() &&
        ( (itsSomebodyElse && (u.isOkEmail() || u.isOkGameMessages()) ) || !itsSomebodyElse )
      )
    {
      addComponent(sendEmailButt,buildLocString(SENDEMAIL_TOP,SENDEMAIL_LEFT));
      sendEmailButt.addClickListener(new SendEmailListener());
    }

    if(iCanEdit() && itsSomebodyElse) {
      String followTxt = "Follow "+u.getUserName();
      followCB.setCaption(followTxt);
      followCB.setWidth(buildPxString(FOLLOW_W));
      followCB.setHeight(buildPxString(FOLLOW_H));
      followCB.addStyleName("m-userProfile3-follow-button");
      followCB.setImmediate(true);
      addComponent(followCB,buildLocString(FOLLOW_TOP,FOLLOW_LEFT));
    }
    scoresLab.setWidth(buildPxString(SCORES_LAB_W));
    scoresLab.setHeight(buildPxString(SCORES_LAB_H));
    addComponent(scoresLab,buildLocString(SCORES_LAB_TOP,SCORES_LAB_LEFT));

    if(g.isActionPlansEnabled()) {
      exploreScoreLab.setWidth(buildPxString(EXPLORE_SCORE_LAB_WIDTH));
      exploreScoreLab.setHeight(buildPxString(EXPLORE_SCORE_LAB_HEIGHT));
      addComponent(exploreScoreLab,buildLocString(EXPLORE_SCORE_LAB_TOP,EXPLORE_SCORE_LAB_LEFT));

      innovateScoreLab.setWidth(buildPxString(INNOVATION_SCORE_LAB_WIDTH));
      innovateScoreLab.setHeight(buildPxString(INNOVATION_SCORE_LAB_HEIGHT));
      addComponent(innovateScoreLab,buildLocString(INNOVATION_SCORE_LAB_TOP,INNOVATION_SCORE_LAB_LEFT));
    }
    else {
      exploreScoreLab.setWidth(buildPxString(EXPLORE_SCORE_LAB_WIDTH));
      exploreScoreLab.setHeight("25px"); //buildPxString(EXPLORE_SCORE_LAB_HEIGHT));
      addComponent(exploreScoreLab,buildLocString(EXPLORE_SCORE_LAB_TOP+10,EXPLORE_SCORE_LAB_LEFT-50));
    }
    learnLab.setWidth(buildPxString(LEARN_LAB_W));
    learnLab.setHeight(buildPxString(LEARN_LAB_H));
    learnLab.setStyleName("m-userProfile3-learn-label");
    addComponent(learnLab,buildLocString(LEARN_LAB_TOP,LEARN_LAB_LEFT));

    locationLab.setWidth(buildPxString(LEARN_LAB_W));
    locationLab.setHeight(buildPxString(LEARN_LAB_H_ORIG));
    addComponent(locationLab, buildLocString(LOCATION_TOP,LOCATION_LAB_LEFT));

    expertiseLab.setWidth(buildPxString(LEARN_LAB_W));
    expertiseLab.setHeight(buildPxString(LEARN_LAB_H_ORIG));
    addComponent(expertiseLab, buildLocString(EXPERTISE_TOP,EXPERTISE_LAB_LEFT));

    expertiseTA.setWidth(buildPxString(EXPERTISE_W));
    expertiseTA.setHeight(buildPxString(EXPERTISE_H));
    expertiseTA.addStyleName("m-overflow-hidden"); // don't want scroll bar
    expertiseTA.addStyleName("m-transparent");
    expertiseTA.addStyleName("m-noresize");
    expertiseTA.addStyleName("m-userprofile3-text-border");
    addComponent(expertiseTA,buildLocString(EXPERTISE_TOP,EXPERTISE_LEFT));

    affiliationLab.setWidth(buildPxString(LEARN_LAB_W));
    affiliationLab.setHeight(buildPxString(LEARN_LAB_H_ORIG));
    addComponent(affiliationLab, buildLocString(AFFILIATION_TOP,AFFILIATION_LAB_LEFT));

    affiliationCombo.addStyleName("m-transparent");
    //affiliationCombo.addStyleName("m-userprofile3-affiliation-border");  to be tested
    addComponent(affiliationCombo,buildLocString(AFFILIATION_TOP,AFFILIATION_LEFT));

    affiliationTA.setWidth(buildPxString(AFFILIATION_W));
    affiliationTA.setHeight(buildPxString(AFFILIATION_H-23));
    affiliationTA.addStyleName("m-overflow-hidden"); // don't want scroll bar
    affiliationTA.addStyleName("m-transparent");
    affiliationTA.addStyleName("m-noresize");
    affiliationTA.addStyleName("m-userprofile3-text-border");
    addComponent(affiliationTA,buildLocString(AFFILIATION_TOP+23,AFFILIATION_LEFT));

    locationTF.setWidth(buildPxString(LOCATION_W));
    locationTF.setHeight(buildPxString(LOCATION_H));
    locationTF.addStyleName("m-noresize");
    locationTF.addStyleName("m-overflow-hidden"); // don't want scroll bar on safari
    locationTF.addStyleName("m-userprofile3-text-border");
    locationTF.addStyleName("m-transparent");
    addComponent(locationTF,buildLocString(LOCATION_TOP,LOCATION_LEFT));

    if(imAdmin)
      addComponent(manageAwardsButt, buildLocString(142,770));

    badgeLayout.setMargin(false);
    badgeLayout.setWidth(buildPxString(BADGES_W));
    badgeLayout.setHeight(buildPxString(BADGES_H));
    badgeLayout.setSpacing(false);
    addComponent(badgeLayout,buildLocString(BADGES_TOP,BADGES_LEFT));

    learnTA.setWidth(buildPxString(LEARN_W));
    learnTA.setHeight(buildPxString(LEARN_H));
    learnTA.addStyleName("m-noresize");
    learnTA.addStyleName("m-overflow-hidden"); // no scroll bar
    learnTA.addStyleName("m-transparent");
    learnTA.addStyleName("m-userprofile3-text-border");
    addComponent(learnTA,buildLocString(LEARN_TOP,LEARN_LEFT));

    nameLab.setWidth(buildPxString(NAME_W));
    nameLab.setHeight(buildPxString(NAME_H));
    nameLab.addStyleName("m-userprofile3name");
    addComponent(nameLab,buildLocString(NAME_TOP,NAME_LEFT));

    Component titleComp = Mmowgli2UI.getGlobals().getMediaLocator().getUserProfileTitle();
    addComponent(titleComp, buildLocString(TITLE_TOP,TITLE_LEFT));

    levelLab.setWidth(buildPxString(LEVEL_W));
    levelLab.setHeight(buildPxString(LEVEL_H));
    levelLab.addStyleName("m-userprofile3name");
    addComponent(levelLab,buildLocString(LEVEL_TOP,LEVEL_LEFT));

    addComponent(externMailCB,buildLocString(EXTERN_MAIL_TOP, EXTERN_MAIL_LEFT));
    addComponent(ingameMailCB,buildLocString(INGAME_MAIL_TOP, INGAME_MAIL_LEFT));

    displayBadgesAndAwardsTL(uid);

    if (!itsSomebodyElse)
      avatarButt.addClickListener(new ClickListener()
      {
        private static final long serialVersionUID = 1L;

        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        @Override
        public void buttonClick(ClickEvent event)
        {
          HSess.init();
          User u = User.getTL(uid);
          Avatar av = u.getAvatar();
          AvatarChooser chooser = new AvatarChooser(av==null?null:av.getId());
          chooser.initGui();
          chooser.addCloseListener(new ChooserClosed());
          UI.getCurrent().addWindow(chooser);
          chooser.center();
          HSess.close();
        }
      });

    learnTA.addValueChangeListener(new ValueChangeListener()
    {
      private static final long serialVersionUID = 1L;

      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateUpdate
      @HibernateClosed
      @HibernateUserUpdate
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        if(listenersDisabled)
          return;
        HSess.init();
        String s = learnTA.getValue().toString().trim();
        User u = User.getTL(uid);
        u.setAnswer(clampToVarchar255(s));  // Db field is 255 varchar
        User.updateTL(u);
        Notification.show("Answer changed", Notification.Type.HUMANIZED_MESSAGE);
        HSess.close();
      }
    });

    expertiseTA.addValueChangeListener(new ValueChangeListener()
    {
      private static final long serialVersionUID = 1L;

      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateUpdate
      @HibernateClosed
      @HibernateUserUpdate
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        if(listenersDisabled)
          return;
        
        HSess.init();
        String s = expertiseTA.getValue().toString().trim();
        User u = User.getTL(uid);
        u.setExpertise(clampToVarchar255(s));
        User.updateTL(u);
        Notification.show("Expertised changed", Notification.Type.HUMANIZED_MESSAGE);
        HSess.close();
      }
    });

    ValueChangeListener affiliationListener = new ValueChangeListener()
    {
      private static final long serialVersionUID = 1L;

      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateUpdate
      @HibernateClosed
      @HibernateUserUpdate
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        if(listenersDisabled)
          return;
        
        HSess.init();
        String s = buildAffiliation();
        User u = User.getTL(uid);
        u.setAffiliation(clampToVarchar255(s));
        User.updateTL(u);
        Notification.show("Affiliation changed", Notification.Type.HUMANIZED_MESSAGE);
        HSess.close();
      }
    };
    affiliationTA.addValueChangeListener(affiliationListener);
    affiliationCombo.addValueChangeListener(affiliationListener);

    locationTF.addValueChangeListener(new ValueChangeListener()
    {
      private static final long serialVersionUID = 1L;

      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateUpdate
      @HibernateClosed
      @HibernateUserUpdate
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        if(listenersDisabled)
          return;
        
        HSess.init();
        String s = locationTF.getValue().toString().trim();
        User u = User.getTL(uid);
        u.setLocation(clampToVarchar255(s));
        User.updateTL(u);
        Notification.show("Location changed",Notification.Type.HUMANIZED_MESSAGE);
        HSess.close();
      }
    });

    externMailCB.addValueChangeListener(new EmailListener(externMailCB));
    ingameMailCB.addValueChangeListener(new EmailListener(ingameMailCB));

    if (!itsSomebodyElse && iCanEdit()) {
      changeEmailButt.addClickListener(new ClickListener()
      {
        private static final long serialVersionUID = 1L;

        @Override
        @MmowgliCodeEntry
        public void buttonClick(ClickEvent event)
        {
          emailPacket = new ChangeEmailDialog.EmailPacket();
          EmailPii emailAddr = VHibPii.getUserPiiEmail((Long) uid);
          if (emailAddr != null)
            emailPacket.original = emailAddr.getAddress();
          else
            emailPacket.original = "";

          ChangeEmailDialog email = new ChangeEmailDialog(emailPacket);
          UI.getCurrent().addWindow(email);
          email.center();

          email.setSaveListener(new ClickListener()
          {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event)
            {
              VHibPii.newUserPiiEmail((Long) uid, emailPacket.updated);
              /*
               * MailManager mmgr = app.globs().applicationMaster().mailManager(); User me = DBGet.getUser(app.getUser()); for(User u : authors) {
               * mmgr.mailToUser(me.getId(), u.getId(), subj.toString(), msg.toString()); } try { if(!u.isOkEmail()) return;
               *
               * List<Email> elis= u.getEmailAddresses();
               * if(elis == null || elis.size()<=0) {
               *   System.err.println("No email address found for user "+u.getUserName()); return;
               * }
               *
               * String toAddr = elis.get(0).getAddress();
               * String from = "mmowgli<mmowgli@nps.navy.mil>";
               * String subj = "MMOWGLI: Email Updated";
               *
               * StringBuilder sb = new StringBuilder();
               *
               * sb.append("<p>Greetings, <b>");
               * sb.append(u.getUserName());
               * sb.append("</b>, from <i>mmowgli</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet\".</p><p>At ");
               * sb.append(new SimpleDateFormat("MM/dd HH:mm z").format(new Date()));
               * sb.append(", you changed your email. This is a confirmation of this new email address. <br/>");
               * sb.append("</p><p>");
               * sb.append("If you believe you received this email by mistake, please disregard this. If you did not request a change of email or have any other questions, ");
               * sb.append("you may report such concerns on the <a href='http://mmowgli.nps.edu/trouble'>MMOWGLI Trouble Report</a> page at <a href='http://mmowgli.nps.edu/trouble'>mmowgli.nps.edu/trouble</a>.");
               * sb.append("</p><p>Thanks for playing mmowgli.</p>");
               *
               * String body = sb.toString();
               * mailer.send(toAddr, from, subj, body, true);
               * changeEmailButt.getWindow().showNotification("Test email sent to "+emailPacket.updated,null);//Password Changed DEREK }
               * catch (Throwable t) {
               *   System.err.println("Error sending action plan invitation email: " + t.getClass().getSimpleName() + ": " + t.getLocalizedMessage());
               *  }
               */
            }
          });
        }
      });

    changePWButt.addClickListener(new ClickListener()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public void buttonClick(ClickEvent event)
      {
        packet = new ChangePasswordDialog.PasswordPacket();
        final UserPii uPii = VHibPii.getUserPii((Long)uid);
        packet.original = uPii.getPassword();

        ChangePasswordDialog dial = new ChangePasswordDialog(packet);
        UI.getCurrent().addWindow(dial);
        dial.center();

        dial.setSaveListener(new ClickListener()
        {
          private static final long serialVersionUID = 1L;
          @Override
          @MmowgliCodeEntry
          @HibernateOpened
          @HibernateClosed
          public void buttonClick(ClickEvent event)
          {
            HSess.init();
            uPii.setPassword(new StrongPasswordEncryptor().encryptPassword(packet.updated));
            VHibPii.update(uPii);
            Notification.show("Password Changed",Notification.Type.HUMANIZED_MESSAGE);

            GameEventLogger.logUserPasswordChangedTL(User.getTL(uid));

            // Clean up for security
            packet.original = null;
            packet.updated = null;
            HSess.close();
          }
        });
      }
    });}

    if(!itsSomebodyElse && iCanEdit())
     ;
    else {
      changePWButt.setEnabled(false);
      locationTF.setReadOnly(true);
      externMailCB.setEnabled(false);
      ingameMailCB.setEnabled(false);
      expertiseTA.setReadOnly(true);
      avatarButt.setReadOnly(true);
      learnTA.setReadOnly(true);
    }
  }

  class AwardButton extends NativeButton
  {
    private static final long serialVersionUID = 1L;
    ArrayList<Integer> moveNumber = new ArrayList<Integer>();
    AwardType at;
    String url;
    public AwardButton(AwardType at)
    {
      this.at = at;
    }
  }
  private String getBadgeDescription(Game g, Badge b)
  {
    if(!g.isActionPlansEnabled() && (b.getBadge_pk() == BADGE_AP_AUTHOR))
      return "";
    else
      return b.getDescription();
  }

  private void displayBadgesAndAwardsTL(Object uid)
  {
    displayBadgesAndAwards(uid,HSess.get());
  }
  
  private void displayBadgesAndAwards(Object uid, Session sess)
  {
    MediaLocator loc = Mmowgli2UI.getGlobals().getMediaLocator();

    ClickListener lis = new BadgeListener();
    Game g = Game.get(sess);
    
    MSysOut.println(DEBUG_LOGS, "User.get(sess) from UserProfile3Top.displayBadgesAndAwards(sess)");
    User u = User.get(uid, sess);
    Set<Badge> badges = u.getBadges();

    badgeLayout.removeAllComponents();

    @SuppressWarnings("unchecked")
    List<Badge> list = (List<Badge>)sess.createCriteria(Badge.class).addOrder(Order.asc("badge_pk")).list();
    int nDefinedBadges = list.size();

    for(int b=0;b<nDefinedBadges;b++)  {
      Badge bd = getBadgeById(badges,b+1);
      badgeButts[b] = new NativeButton();
      badgeButts[b].addStyleName("m-badgeButton");
      badgeLayout.addComponent(badgeButts[b]);

      if(bd != null) {
        badgeButts[b].setIcon(loc.locate(bd.getMedia()));
        badgeButts[b].setDescription(getBadgeDescription(g,bd));   // check for undesired action plan mention
        badgeButts[b].addClickListener(lis);
      }
      else {
        badgeButts[b].setIcon(loc.getEmptyBadgeImage());
        if(!((b+1) == BADGE_EIGHT_ID))
          badgeButts[b].setDescription(getBadgeDescription(g,list.get(b)));   // check for undesired action plan mention
      }
    }

    List<AwardButton> ablis = getAwardButtons(u,sess);
    Iterator<AwardButton> bItr = ablis.iterator();

    for(int i=0;i<4;i++) {
      NativeButton awb = bItr.hasNext()?bItr.next():null;
      if(awb == null) {

        awb = new NativeButton();
        awb.addStyleName("m-badgeButton");
        awb.setDescription("Game-specific awards");
        awb.setIcon(loc.getEmptyBadgeImage());
      }
      badgeLayout.addComponent(awb);
    }

  }
  /*
   * Only one button per type, but check for being awarded in multiple moves and indicate it
   */
  List<AwardButton> getAwardButtons(User u, Session sess)
  {
    ArrayList<Award> alis = new ArrayList<Award>();
    alis.addAll(u.getAwards());
    Collections.sort(alis, new Comparator<Award>() {
      @Override
      public int compare(Award arg0, Award arg1)
      {
        return (int)(arg0.getAwardType().getId()) - (int)(arg1.getAwardType().getId());
      }
    });

    ArrayList<AwardButton> blis = new ArrayList<AwardButton>();
    Iterator<Award> itr = alis.iterator();

    AwardButton lastButt = null;
    while(itr.hasNext()) {
      Award aw = itr.next();
      if(lastButt != null && aw.getAwardType().getId() == lastButt.at.getId()) {
        // just update the existing one.
      }
      else {
        lastButt = new AwardButton(aw.getAwardType());
        lastButt.addStyleName("m-badgeButton");

        blis.add(lastButt);
      }
      lastButt.moveNumber.add(aw.getMove().getNumber());
      lastButt.url = aw.getStoryUrl();
    }

    MediaLocator loc = Mmowgli2UI.getGlobals().getMediaLocator();
    Iterator<AwardButton> bitr = blis.iterator();
    Move curMove = Move.getCurrentMove(sess);
    while (bitr.hasNext()) {
      AwardButton butt = bitr.next();
      butt.setIcon(loc.locate(butt.at.getIcon55x55()));
      StringBuilder description = new StringBuilder(butt.at.getDescription());
      if(curMove.getNumber()>1) {
        if(butt.moveNumber.size() == 1) {
          description.append("<br/>Round number ");
          description.append(butt.moveNumber.get(0));
        }
        else {
          description.append("<br/>Rounds ");
          for(Integer intg : butt.moveNumber) {
            description.append(intg.intValue());
            description.append(", ");
          }
          description.setLength(description.length()-2);
        }
      }
      if(butt.url != null && butt.url.trim().length()>0) {
        description.append("<br/>Click for more information");
        butt.addClickListener(new GotoListener(butt.url));
      }
      butt.setDescription(description.toString());
    }
    return blis;
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

  @SuppressWarnings("serial")
  private class GotoListener implements Button.ClickListener
  {
    String url;
    GotoListener(String url)
    {
      this.url = url;
    }
    @Override
    public void buttonClick(ClickEvent event)
    {
      BrowserWindowOpener.open(url,PORTALTARGETWINDOWNAME);
    }
  }

  private String clampToVarchar255(String s)
  {
    if(s != null && s.length() > 255)
      s = s.substring(0, 255);
    return s;
  }

  private Badge getBadgeById(Set<Badge> set, int id)
  {
    for(Badge b : set)
      if(b.getBadge_pk() ==(long)id)
        return b;
    return null;
  }

  @SuppressWarnings("serial")
  private class FollowListener implements ValueChangeListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    @HibernateUpdate
    @HibernateUserUpdate
    public void valueChange(ValueChangeEvent event)
    {
      if(listenersDisabled)
        return;
      HSess.init();
      Boolean b = (Boolean) followCB.getValue();
      User me = Mmowgli2UI.getGlobals().getUserTL();
      User him = User.getTL(uid);
      Set<User> buds = me.getImFollowing();
      if (b)
        if(!CreateActionPlanPanel.usrContainsByIds(buds, him))
          buds.add(him);
      else
        if(CreateActionPlanPanel.usrContainsByIds(buds, him))
          buds.remove(him);

      User.updateTL(me);   //NonUniqueObjectException: a different object with the same identifier value was already associated with this session
                                                    // this might work, I can't really figure it out, though
      HSess.close();
    }
  }

  @SuppressWarnings("serial")
  private class SendEmailListener implements Button.ClickListener
  {
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      User user = User.getTL(uid);

      // Already checked above, and this button is not shown if no email, so this handler is a no-op
      if(user.isOkEmail() || user.isOkGameMessages())
        new SendMessageWindow(user,imAdminOrGameMaster);
      else
        Notification.show("Sorry", "Player "+user.getUserName()+" does not receive mail.", Notification.Type.WARNING_MESSAGE);
      
      HSess.close();
    }
  }

  @SuppressWarnings("serial")
  class EmailListener implements ValueChangeListener
  {
    private CheckBox source;
    public EmailListener(CheckBox cb)
    {
      source = cb;
    }
    
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    @Override
    @HibernateUpdate
    @HibernateUserUpdate
    public void valueChange(final ValueChangeEvent event)
    {
      if(listenersDisabled)
        return;
      
      HSess.init();
      User u = User.getTL(uid); //feb refactor DBGet.getUserTL(uid);

      boolean wh = source.getValue();
      if(source == externMailCB) {
        u.setOkEmail(wh);
      }
      else /*if (event.getSource() == ingameMailCB)*/ {
        u.setOkGameMessages(wh);
      }

      User.updateTL(u);
      HSess.close();
    }
  }

  @SuppressWarnings("serial")
  class ChooserClosed implements CloseListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateRead
    @HibernateUpdate
    @HibernateCommitted
    @HibernateUserUpdate
    public void windowClose(CloseEvent e)
    {
      HSess.init();
      AvatarChooser chooser = (AvatarChooser)e.getWindow();
      currentAvatarId = chooser.getSelectedAvatarId();
      if(currentAvatarId != null) {
        Avatar newA = Avatar.getTL(currentAvatarId);
        setAvatarIcon(newA);
        User u = User.getTL(uid);
        u.setAvatar(newA);
        User.updateTL(u);
      }
      HSess.close();
    }
  }

  @SuppressWarnings("serial")
  class ManageAwardsListener implements ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      ManageAwardsDialog dial = new ManageAwardsDialog(uid);
      UI.getCurrent().addWindow(dial);
      dial.center();
      HSess.close();
    }
  }

  private String buildPxString(int i)
  {
    return ""+i+"px";
  }

  private String buildLocString(int top,int left)
  {
    return "top:"+top+"px;left:"+left+"px";
  }

  private void setAvatarIconFromDb()
  {
    User u = User.getTL(uid);
    setAvatarIcon(u.getAvatar());
  }

  private void setAvatarIcon(Avatar av)
  {
    if(av != null)
      avatarButt.setIcon(Mmowgli2UI.getGlobals().getMediaLocator().locate(av.getMedia()));
  }

  class BadgeListener implements ClickListener
  {
    private static final long serialVersionUID = 1L;

    @Override
    @MmowgliCodeEntry
    public void buttonClick(ClickEvent event)
    {
      Notification.show(((Button)event.getSource()).getDescription());
    }
  }

  private void refreshData(User u)
  {
    listenersDisabled=true;
    
    setAvatarIcon(u.getAvatar());
    nameLab.setValue(u.getUserName());
    exploreScoreLab.setValue(formatFloat(u.getCombinedBasicScore()));
    innovateScoreLab.setValue(formatFloat(u.getCombinedInnovScore()));
    String loc = u.getLocation();loc = loc==null?"":loc;
    String ans = u.getAnswer(); ans = ans==null?"":ans;
    boolean prev;
    prev = externMailCB.isEnabled();   externMailCB.setEnabled(true);   externMailCB.setValue(u.isOkEmail());        externMailCB.setEnabled(prev);
    prev = ingameMailCB.isEnabled();   ingameMailCB.setEnabled(true);   ingameMailCB.setValue(u.isOkGameMessages()); ingameMailCB.setEnabled(prev);
    prev = locationTF.isReadOnly();    locationTF.setReadOnly(false);   locationTF.setValue(loc);                    locationTF.setReadOnly(prev);
    prev = expertiseTA.isReadOnly();   expertiseTA.setReadOnly(false);  expertiseTA.setValue(u.getExpertise());      expertiseTA.setReadOnly(prev);
    prev = learnTA.isReadOnly();       learnTA.setReadOnly(false);      learnTA.setValue(ans);                       learnTA.setReadOnly(prev);
    
    prev = affiliationTA.isReadOnly(); affiliationTA.setReadOnly(false); 
    boolean prev2 = affiliationCombo.isEnabled(); affiliationCombo.setEnabled(true);;
    fillAffiliation(u.getAffiliation());
    affiliationCombo.setEnabled(prev2);;
    affiliationTA.setReadOnly(prev);
    
    listenersDisabled=false;
 }
  
  @Override
  @HibernateUserRead
  public boolean userUpdated_oobTL(Object uId)
  {
    if(uId != this.uid)
      return false;
    refreshData(User.getTL(uId));
    
    displayBadgesAndAwardsTL(uId);
    return true;
  }
}
