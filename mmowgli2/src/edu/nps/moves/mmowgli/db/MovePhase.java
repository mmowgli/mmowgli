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

package edu.nps.moves.mmowgli.db;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.hibernate.DB;
import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 * 
 * @version $Id$
 * @since $Date$
 * @copyright Copyright (C) 2011
 */

@Entity
public class MovePhase implements Serializable
{
  private static final long serialVersionUID = 9102792756172096715L;
  
  public static enum PhaseType
  {
    PREPARE, PLAY, REVIEW, PUBLISH;
    
    public static String[] stringValues()
    {
      PhaseType[] ptAr = PhaseType.values();
      String[] sa = new String[ptAr.length];
      int i=0;
      for(PhaseType pt : ptAr)
        sa[i++]=pt.toString();
      return sa;
    }
  }
  
  //@formatter:off
  long          id;

  String        description;  // PhaseType.toString goes here
  Set<CardType> allowedCards;
  
  Media         orientationVideo;
  String        orientationHeadline,
                orientationSummary,
                orientationCallToActionText;
  
  Media         callToActionBriefingVideo;
  String        callToActionBriefingHeadline,
                callToActionBriefingSummary,
                callToActionBriefingText,
                callToActionBriefingPrompt;
  
  Media         authorInviteVideo;
  String        authorInviteHeadline,
                authorInviteSummary,
                authorInviteCallToActionText;
  
  Media         investorInviteVideo;
  String        investorInviteHeadline,
                investorInviteSummary,
                investorInviteCallToActionText;
  
  String        playACardTitle;
  String        playACardSubtitle;
  
  String        windowTitle;
  
  String        signupText;
  String        signupHeaderImage;
  
  boolean       signupPageEnabled=false;
  
  boolean       signupButtonEnabled=false;
  boolean       signupButtonShow=false;
  String        signupButtonIcon="";
  String        signupButtonSubText="";
  String        signupButtonToolTip="";
  
  boolean       newButtonEnabled=true;
  boolean       newButtonShow=true;
  String        newButtonIcon="";
  String        newButtonSubText="";
  String        newButtonToolTip="";
  
  boolean       loginButtonEnabled=true;
  boolean       loginButtonShow=true;
  String        loginButtonIcon="";
  String        loginButtonSubText="";
  String        loginButtonToolTip="";
  
  boolean       guestButtonEnabled=false;
  boolean       guestButtonShow=false;
  String        guestButtonIcon="";
  String        guestButtonSubText="";
  String        guestButtonToolTip="";
  
  boolean       restrictByQueryList=false; // VIP list

  short         loginPermissions;
  
  public static short LOGIN_ALLOW_GAMEADMINS        = 0x0001;
  public static short LOGIN_ALLOW_GAMEMASTERS       = 0x0002;
  public static short LOGIN_ALLOW_GAMEDESIGNERS     = 0x0004;
  public static short LOGIN_ALLOW_GUESTS            = 0x0008;
  public static short LOGIN_ALLOW_REGISTEREDPLAYERS = 0x0010;
  public static short LOGIN_ALLOW_NEWPLAYERS        = 0x0020;
  public static short LOGIN_ALLOW_VIPLIST           = 0x0040;
  
  //public static short LOGIN_ALLOW_ALL = -1; //0xFFFF;
  public static short LOGIN_ALLOW_ALL = (short) (
      LOGIN_ALLOW_GAMEADMINS +
      LOGIN_ALLOW_GAMEMASTERS +
      LOGIN_ALLOW_GAMEDESIGNERS +
      LOGIN_ALLOW_GUESTS +
      LOGIN_ALLOW_REGISTEREDPLAYERS +
      LOGIN_ALLOW_NEWPLAYERS +
      LOGIN_ALLOW_VIPLIST);
  
  public static short LOGIN_ALLOW_NONE = 0x0;
  
  public static final String WHO_IS_INVOLVED   = "Who is involved?";
  public static final String WHAT_IS_IT        = "What is it?";
  public static final String WHAT_WILL_IT_TAKE = "What will it take?";
  public static final String HOW_WILL_IT_WORK  = "How will it work?";
  public static final String HOW_WILL_IT_CHANGE_THE_SITUATION = "How will it change the situation?";
  
  String actionPlanWhoIsInvolvedHeader=WHO_IS_INVOLVED;
  String actionPlanWhatIsItHeader=WHAT_IS_IT;
  String actionPlanWhatWillItTakeHeader=WHAT_WILL_IT_TAKE;
  String actionPlanHowWillItWorkHeader=HOW_WILL_IT_WORK;
  String actionPlanHowWillItChangeHeader=HOW_WILL_IT_CHANGE_THE_SITUATION;
  
  Long revision = 0L;   // used internally by hibernate for optimistic locking, but not here
//@formatter:on

  public static MovePhase mergeTL(MovePhase ph)
  {
    return DB.mergeTL(ph);
  }

  public static MovePhase getTL(Object id)
  {
    return DB.getTL(MovePhase.class, id);
  }

  public static void updateTL(MovePhase ph)
  {
    DB.updateTL(ph);
  }

  public static void saveTL(MovePhase ph)
  {
    DB.saveTL(ph);
  }

  public static void deleteTL(MovePhase mp)
  {
    DB.saveTL(mp);
  }
  
  public MovePhase()
  {}
    
  @Id
  @Basic
  @GeneratedValue(strategy=GenerationType.AUTO)
  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }
  
  @Basic
  public Long getRevision()
  {
    return revision;
  }

  public void setRevision(Long revision)
  {
    this.revision = revision;
  }

  public Long incrementVersion()
  {
    setRevision(revision+1);
    return getRevision();
  }
 
  @Override
  public String toString()
  {
    return super.toString();
  }

  @Transient
  public boolean isPreparePhase()
  {
    return description != null && description.equalsIgnoreCase(PhaseType.PREPARE.toString());
  }
 
  // This move can have many allowed cardtypes, and each cardtype can be associated with many moves
  @ManyToMany
  public Set<CardType> getAllowedCards()
  {
    return allowedCards;
  }

  public void setAllowedCards(Set<CardType> allowedCards)
  {
    this.allowedCards = allowedCards;
  }
  
  @Lob
  public String getOrientationHeadline()
  {
    return orientationHeadline;
  }

  public void setOrientationHeadline(String orientationHeadline)
  {
    this.orientationHeadline = orientationHeadline;
  }
  
  @Basic
  public String getOrientationSummary()
  {
    return orientationSummary;
  }

  public void setOrientationSummary(String orientationSummary)
  {
    this.orientationSummary = orientationSummary;
  }

  @Basic
  public String getOrientationCallToActionText()
  {
    return orientationCallToActionText;
  }

  public void setOrientationCallToActionText(String orientationCallToActionText)
  {
    this.orientationCallToActionText = orientationCallToActionText;
  }

  @ManyToOne
  public Media getOrientationVideo()
  {
    return orientationVideo;
  }

  public void setOrientationVideo(Media orientationVideo)
  {
    this.orientationVideo = orientationVideo;
  }

  @Basic
  public String getDescription()  //not used
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  @Basic
  public String getCallToActionBriefingHeadline()  // not used
  {
    return callToActionBriefingHeadline;
  }

  public void setCallToActionBriefingHeadline(String callToActionBriefingHeadline)
  {
    this.callToActionBriefingHeadline = callToActionBriefingHeadline;
  }

  @Basic
  public String getCallToActionBriefingSummary()
  {
    return callToActionBriefingSummary;
  }

  public void setCallToActionBriefingSummary(String callToActionBriefingSummary)
  {
    this.callToActionBriefingSummary = callToActionBriefingSummary;
  }

  @Lob
  public String getCallToActionBriefingText()
  {
    return callToActionBriefingText;
  }

  public void setCallToActionBriefingText(String callToActionBriefingText)
  {
    this.callToActionBriefingText = callToActionBriefingText;
  }

  @Basic
  public String getAuthorInviteHeadline() // not used
  {
    return authorInviteHeadline;
  }

  public void setAuthorInviteHeadline(String authorInviteHeadline)
  {
    this.authorInviteHeadline = authorInviteHeadline;
  }

  @Basic
  public String getAuthorInviteSummary() // not used
  {
    return authorInviteSummary;
  }

  public void setAuthorInviteSummary(String authorInviteSummary)
  {
    this.authorInviteSummary = authorInviteSummary;
  }

  @Basic
  public String getAuthorInviteCallToActionText()  // not used
  {
    return authorInviteCallToActionText;
  }

  public void setAuthorInviteCallToActionText(String authorInviteCallToActionText)
  {
    this.authorInviteCallToActionText = authorInviteCallToActionText;
  }

  @Basic
  public String getInvestorInviteHeadline()  //not used
  {
    return investorInviteHeadline;
  }

  public void setInvestorInviteHeadline(String investorInviteHeadline)
  {
    this.investorInviteHeadline = investorInviteHeadline;
  }

  @Basic
  public String getInvestorInviteSummary() // not used
  {
    return investorInviteSummary;
  }

  public void setInvestorInviteSummary(String investorInviteSummary)
  {
    this.investorInviteSummary = investorInviteSummary;
  }

  @Basic
  public String getInvestorInviteCallToActionText() //not used
  {
    return investorInviteCallToActionText;
  }

  public void setInvestorInviteCallToActionText(String investorInviteCallToActionText)
  {
    this.investorInviteCallToActionText = investorInviteCallToActionText;
  }

  @ManyToOne
  public Media getCallToActionBriefingVideo()
  {
    return callToActionBriefingVideo;
  }

  public void setCallToActionBriefingVideo(Media v)
  {
    this.callToActionBriefingVideo = v;
  }

  @ManyToOne
  public Media getAuthorInviteVideo()  //not used
  {
    return authorInviteVideo;
  }

  public void setAuthorInviteVideo(Media authorInviteVideo)
  {
    this.authorInviteVideo = authorInviteVideo;
  }

  @ManyToOne
  public Media getInvestorInviteVideo()
  {
    return investorInviteVideo;
  }

  public void setInvestorInviteVideo(Media investorInviteVideo)
  {
    this.investorInviteVideo = investorInviteVideo;
  }

  @Basic
  public String getCallToActionBriefingPrompt()
  {
    return callToActionBriefingPrompt;
  }

  public void setCallToActionBriefingPrompt(String callToActionBriefingPrompt)
  {
    this.callToActionBriefingPrompt = callToActionBriefingPrompt;
  }

  @Basic
  public String getPlayACardTitle()
  {
    return playACardTitle;
  }

  public void setPlayACardTitle(String playACardTitle)
  {
    this.playACardTitle = playACardTitle;
  }

  @Basic
  public String getPlayACardSubtitle()
  {
    return playACardSubtitle;
  }

  public void setPlayACardSubtitle(String playACardSubtitle)
  {
    this.playACardSubtitle = playACardSubtitle;
  }

  @Basic
  public String getWindowTitle()
  {
    return windowTitle;
  }

  public void setWindowTitle(String windowTitle)
  {
    this.windowTitle = windowTitle;
  }

  @Lob
  public String getSignupText()
  {
    return signupText;
  }

  public void setSignupText(String signupText)
  {
    this.signupText = signupText;
  }
  
  @Basic
  public String getSignupHeaderImage()
  {
    return signupHeaderImage;
  }
  
  public void setSignupHeaderImage(String imgname)
  {
    this.signupHeaderImage = imgname;
  }
  
  @Basic
  public boolean isSignupPageEnabled()
  {
    return signupPageEnabled;
  }

  public void setSignupPageEnabled(boolean signupPageEnabled)
  {
    this.signupPageEnabled = signupPageEnabled;
  }

  @Basic
  public boolean isSignupButtonEnabled()
  {
    return signupButtonEnabled;
  }

  public void setSignupButtonEnabled(boolean signupButtonEnabled)
  {
    this.signupButtonEnabled = signupButtonEnabled;
  }
  
  @Basic
  public boolean isSignupButtonShow()
  {
    return signupButtonShow;
  }

  public void setSignupButtonShow(boolean signupButtonShow)
  {
    this.signupButtonShow = signupButtonShow;
  }
  
  @Basic
  public String getSignupButtonIcon()
  {
    return signupButtonIcon;
  }

  public void setSignupButtonIcon(String signupButtonIcon)
  {
    this.signupButtonIcon = signupButtonIcon;
  }
  
  @Basic
  public String getSignupButtonSubText()
  {
    return signupButtonSubText;
  }

  public void setSignupButtonSubText(String signupButtonSubText)
  {
    this.signupButtonSubText = signupButtonSubText;
  }
  
  @Basic
  public String getSignupButtonToolTip()
  {
    return signupButtonToolTip;
  }

  public void setSignupButtonToolTip(String signupButtonToolTip)
  {
    this.signupButtonToolTip = signupButtonToolTip;
  }
  
  @Basic
  public boolean isNewButtonEnabled()
  {
    return newButtonEnabled;
  }

  public void setNewButtonEnabled(boolean newButtonEnabled)
  {
    this.newButtonEnabled = newButtonEnabled;
  }
  
  @Basic
  public boolean isNewButtonShow()
  {
    return newButtonShow;
  }

  public void setNewButtonShow(boolean newButtonShow)
  {
    this.newButtonShow = newButtonShow;
  }
  
  @Basic
  public String getNewButtonIcon()
  {
    return newButtonIcon;
  }

  public void setNewButtonIcon(String newButtonIcon)
  {
    this.newButtonIcon = newButtonIcon;
  }
  
  @Basic
  public String getNewButtonSubText()
  {
    return newButtonSubText;
  }

  public void setNewButtonSubText(String newButtonSubText)
  {
    this.newButtonSubText = newButtonSubText;
  }
  
  @Basic
  public String getNewButtonToolTip()
  {
    return newButtonToolTip;
  }

  public void setNewButtonToolTip(String newButtonToolTip)
  {
    this.newButtonToolTip = newButtonToolTip;
  }
  
  @Basic
  public boolean isLoginButtonEnabled()
  {
    return loginButtonEnabled;
  }

  public void setLoginButtonEnabled(boolean loginButtonEnabled)
  {
    this.loginButtonEnabled = loginButtonEnabled;
  }
  
  @Basic
  public boolean isLoginButtonShow()
  {
    return loginButtonShow;
  }

  public void setLoginButtonShow(boolean loginButtonShow)
  {
    this.loginButtonShow = loginButtonShow;
  }
  
  @Basic
  public String getLoginButtonIcon()
  {
    return loginButtonIcon;
  }

  public void setLoginButtonIcon(String loginButtonIcon)
  {
    this.loginButtonIcon = loginButtonIcon;
  }
  @Basic
  public String getLoginButtonSubText()
  {
    return loginButtonSubText;
  }

  public void setLoginButtonSubText(String loginButtonSubText)
  {
    this.loginButtonSubText = loginButtonSubText;
  }
  
  @Basic
  public String getLoginButtonToolTip()
  {
    return loginButtonToolTip;
  }

  public void setLoginButtonToolTip(String loginButtonToolTip)
  {
    this.loginButtonToolTip = loginButtonToolTip;
  }
  
  @Basic
  public boolean isGuestButtonEnabled()
  {
    return guestButtonEnabled;
  }

  public void setGuestButtonEnabled(boolean guestButtonEnabled)
  {
    this.guestButtonEnabled = guestButtonEnabled;
  }
  
  @Basic
  public boolean isGuestButtonShow()
  {
    return guestButtonShow;
  }

  public void setGuestButtonShow(boolean guestButtonShow)
  {
    this.guestButtonShow = guestButtonShow;
  }
  
  @Basic
  public String getGuestButtonIcon()
  {
    return guestButtonIcon;
  }

  public void setGuestButtonIcon(String guestButtonIcon)
  {
    this.guestButtonIcon = guestButtonIcon;
  }
  
  @Basic
  public String getGuestButtonSubText()
  {
    return guestButtonSubText;
  }

  public void setGuestButtonSubText(String guestButtonSubText)
  {
    this.guestButtonSubText = guestButtonSubText;
  }
  
  @Basic
  public String getGuestButtonToolTip()
  {
    return guestButtonToolTip;
  }

  public void setGuestButtonToolTip(String guestButtonToolTip)
  {
    this.guestButtonToolTip = guestButtonToolTip;
  }
  
  @Basic
  public short getLoginPermissions()
  {
    return loginPermissions;
  }

  public void setLoginPermissions(short loginPermissions)
  {
    this.loginPermissions = loginPermissions;
  }
    
  @Transient
  public boolean isLoginAllowGameAdmins()
  {
    return (getLoginPermissions() & LOGIN_ALLOW_GAMEADMINS) != 0;
  }
  
  public void loginAllowGameAdmins(boolean set)
  {
    if(set)
      setLoginPermissions((short) (getLoginPermissions() | LOGIN_ALLOW_GAMEADMINS));
    else
      setLoginPermissions((short) (getLoginPermissions() & ~LOGIN_ALLOW_GAMEADMINS));
  }

  @Transient
  public boolean isLoginAllowGameMasters()
  {
    return (getLoginPermissions() & LOGIN_ALLOW_GAMEMASTERS) != 0;
  }
  
  public void loginAllowGameMasters(boolean set)
  {
    if(set)
      setLoginPermissions((short) (getLoginPermissions() | LOGIN_ALLOW_GAMEMASTERS));
    else
      setLoginPermissions((short) (getLoginPermissions() & ~LOGIN_ALLOW_GAMEMASTERS));   
  }
  
 
  public void loginAllowGuests(boolean set)
  {
    if(set)
      setLoginPermissions((short) (getLoginPermissions() | LOGIN_ALLOW_GUESTS));
    else
      setLoginPermissions((short) (getLoginPermissions() & ~LOGIN_ALLOW_GUESTS));     
  }
  
  @Transient
  public boolean isLoginAllowGameDesigners()
  {
    return (getLoginPermissions() & LOGIN_ALLOW_GAMEDESIGNERS) != 0;
  }
  
  public void loginAllowGameDesigners(boolean set)
  {
    if(set)
      setLoginPermissions((short) (getLoginPermissions() | LOGIN_ALLOW_GAMEDESIGNERS));
    else
      setLoginPermissions((short) (getLoginPermissions() & ~LOGIN_ALLOW_GAMEDESIGNERS));     
    
  }
  @Transient
  public boolean isLoginAllowGuests()
  {
    return (getLoginPermissions() & LOGIN_ALLOW_GUESTS) != 0;
  }

  public void loginAllowAll()
  {
    setLoginPermissions(LOGIN_ALLOW_ALL);
  }
  @Transient
  public boolean isLoginAllowAll()
  {
    return getLoginPermissions() == LOGIN_ALLOW_ALL;
  }
  
  public void loginAllowNone()
  {
    setLoginPermissions(LOGIN_ALLOW_NONE);
  }
  
  @Transient
  public boolean isLoginAllowNone()
  {
    return getLoginPermissions() == LOGIN_ALLOW_NONE;
  }

  @Transient
  public boolean isLoginAllowNewUsers()
  {
    return ( getLoginPermissions() & LOGIN_ALLOW_NEWPLAYERS) != 0;
  }
  
  public void loginAllowNewUsers(boolean set)
  {
    if(set)
      setLoginPermissions((short) (getLoginPermissions() | LOGIN_ALLOW_NEWPLAYERS));
    else
      setLoginPermissions((short) (getLoginPermissions() & ~LOGIN_ALLOW_NEWPLAYERS));       
  }
  
  @Transient
  public boolean isLoginAllowRegisteredUsers()
  {
    return ( getLoginPermissions() & LOGIN_ALLOW_REGISTEREDPLAYERS) != 0;
  }
  
  public void loginAllowRegisteredUsers(boolean set)
  {
    if(set)
      setLoginPermissions((short) (getLoginPermissions() | LOGIN_ALLOW_REGISTEREDPLAYERS));
    else
      setLoginPermissions((short) (getLoginPermissions() & ~LOGIN_ALLOW_REGISTEREDPLAYERS));       
  }

  @Transient
  public boolean isLoginAllowVIPList()
  {
    return ( getLoginPermissions() & LOGIN_ALLOW_VIPLIST) != 0;
  }
  
  public void loginAllowVIPList(boolean set)
  {
    if(set)
      setLoginPermissions((short) (getLoginPermissions() | LOGIN_ALLOW_VIPLIST));
    else
      setLoginPermissions((short) (getLoginPermissions() & ~LOGIN_ALLOW_VIPLIST));       
  }
  
  // This following should be renamed: it is used only for newly registering players;  For existing
  // players, use the bit LOGIN_ALLOW_VIPLIST
  @Basic
  public boolean isRestrictByQueryList()
  {
    return restrictByQueryList;
  }

  public void setRestrictByQueryList(boolean restrictByQueryList)
  {
    this.restrictByQueryList = restrictByQueryList;
  }

  @Basic
  public String getActionPlanWhoIsInvolvedHeader()
  {
    return actionPlanWhoIsInvolvedHeader;
  }

  public void setActionPlanWhoIsInvolvedHeader(String actionPlanWhoIsInvolvedHeader)
  {
    this.actionPlanWhoIsInvolvedHeader = actionPlanWhoIsInvolvedHeader;
  }
  
  @Basic
  public String getActionPlanWhatIsItHeader()
  {
    return actionPlanWhatIsItHeader;
  }

  public void setActionPlanWhatIsItHeader(String actionPlanWhatIsItHeader)
  {
    this.actionPlanWhatIsItHeader = actionPlanWhatIsItHeader;
  }
  
  @Basic
  public String getActionPlanWhatWillItTakeHeader()
  {
    return actionPlanWhatWillItTakeHeader;
  }

  public void setActionPlanWhatWillItTakeHeader(String actionPlanWhatWillItTakeHeader)
  {
    this.actionPlanWhatWillItTakeHeader = actionPlanWhatWillItTakeHeader;
  }
  
  @Basic
  public String getActionPlanHowWillItWorkHeader()
  {
    return actionPlanHowWillItWorkHeader;
  }

  public void setActionPlanHowWillItWorkHeader(String actionPlanHowWillItWorkHeader)
  {
    this.actionPlanHowWillItWorkHeader = actionPlanHowWillItWorkHeader;
  }

  @Basic
  public String getActionPlanHowWillItChangeHeader()
  {
    return actionPlanHowWillItChangeHeader;
  }

  public void setActionPlanHowWillItChangeHeader(String actionPlanHowWillItChangeHeader)
  {
    this.actionPlanHowWillItChangeHeader = actionPlanHowWillItChangeHeader;
  }

  public void cloneFrom(MovePhase existing)
  {
//@formatter:off
    setDescription                   (existing.getDescription());
    setAuthorInviteCallToActionText  (existing.getAuthorInviteCallToActionText());
    //setAllowedCards                  (existing.getAllowedCards());
    setOrientationVideo              (existing.getOrientationVideo());
    setOrientationHeadline           (existing.getOrientationHeadline());
    setOrientationSummary            (existing.getOrientationSummary());
    setOrientationCallToActionText   (existing.getOrientationCallToActionText());
    setCallToActionBriefingVideo     (existing.getCallToActionBriefingVideo());
    setCallToActionBriefingHeadline  (existing.getCallToActionBriefingHeadline());
    setCallToActionBriefingSummary   (existing.getCallToActionBriefingSummary());
    setCallToActionBriefingText      (existing.getCallToActionBriefingText());
    setCallToActionBriefingPrompt    (existing.getCallToActionBriefingPrompt());
    setAuthorInviteVideo             (existing.getAuthorInviteVideo());
    setAuthorInviteHeadline          (existing.getAuthorInviteHeadline());
    setAuthorInviteSummary           (existing.getAuthorInviteSummary());
    setAuthorInviteCallToActionText  (existing.getAuthorInviteCallToActionText());
    setInvestorInviteVideo           (existing.getInvestorInviteVideo());
    setInvestorInviteHeadline        (existing.getInvestorInviteHeadline());
    setInvestorInviteSummary         (existing.getInvestorInviteSummary());
    setInvestorInviteCallToActionText(existing.getInvestorInviteCallToActionText());
    setPlayACardTitle                (existing.getPlayACardTitle());
    setPlayACardSubtitle             (existing.getPlayACardSubtitle());
    setWindowTitle                   (existing.getWindowTitle());
    setSignupText                    (existing.getSignupText());
    setSignupPageEnabled             (existing.isSignupPageEnabled());
    setSignupButtonEnabled           (existing.isSignupButtonEnabled());
    setSignupButtonShow              (existing.isSignupButtonShow());
    setSignupButtonIcon              (existing.getSignupButtonIcon());
    setSignupButtonSubText           (existing.getSignupButtonSubText());
    setSignupButtonToolTip           (existing.getSignupButtonToolTip());
    setNewButtonEnabled              (existing.isNewButtonEnabled());
    setNewButtonShow                 (existing.isNewButtonShow());
    setNewButtonIcon                 (existing.getNewButtonIcon());
    setNewButtonSubText              (existing.getNewButtonSubText());
    setNewButtonToolTip              (existing.getNewButtonToolTip());
    setLoginButtonEnabled            (existing.isLoginButtonEnabled());
    setLoginButtonShow               (existing.isLoginButtonShow());
    setLoginButtonIcon               (existing.getLoginButtonIcon());
    setLoginButtonSubText            (existing.getLoginButtonSubText());
    setLoginButtonToolTip            (existing.getLoginButtonToolTip());
    setGuestButtonEnabled            (existing.isGuestButtonEnabled());
    setGuestButtonShow               (existing.isGuestButtonShow());
    setGuestButtonIcon               (existing.getGuestButtonIcon());
    setGuestButtonSubText            (existing.getGuestButtonSubText());
    setGuestButtonToolTip            (existing.getGuestButtonToolTip());
    setRestrictByQueryList           (existing.isRestrictByQueryList());
    setLoginPermissions              (existing.getLoginPermissions());
//@formatter:on
    HashSet<CardType> cSet = new HashSet<CardType>();
    for(CardType ct : existing.getAllowedCards())
      cSet.add(ct);
    setAllowedCards(cSet);
  }
  
  public static MovePhase getCurrentMovePhaseTL()
  {
    return getCurrentMovePhase(HSess.get());
  }
  
  public static MovePhase getCurrentMovePhase(Session sess)
  {
    return Move.getCurrentMove(sess).getCurrentMovePhase();
  }
  
  // no longer used
  @Deprecated
  public static boolean isGuestAndIsPreparePhaseTL(User me)
  {
    if(!me.isViewOnly())
      return false;
    
    return inThePreparePhaseTL();
  }
  
  public static boolean inThePreparePhaseTL()
  {
    return MovePhase.getCurrentMovePhaseTL().isPreparePhase();
  }

}
