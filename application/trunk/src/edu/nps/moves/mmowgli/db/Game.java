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

package edu.nps.moves.mmowgli.db;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.hibernate.DB;
import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * One game represents an interaction that may have several "turns". For example, a game about piracy in Somalia.
 * 
 * @author DMcG
 * 
 *         * Modified on Dec 16, 2010
 * 
 *         MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@Entity
public class Game implements Serializable
{
  private static final long serialVersionUID = -7897552836534872867L;
  public static class RegexPair implements Serializable
  {
    private static final long serialVersionUID = 1L;
    public String regex;
    public String replacement;
    public RegexPair(String regex, String replacement)
    {
      assert regex != null;
      assert replacement != null;
      this.regex = regex;
      this.replacement = replacement;
    }
  }
  
//@formatter:off

    long         id;          // Primary key
    String       title;       // Title of the game
    String       acronym;
    String       description; // Brief description of the game
    long         version;     // of the form yyyymmdd, e.g., 20120716
    Media        headerBranding;
    
    String       mapTitle;
    double       mapLatitude;
    double       mapLongitude;
    int          mapZoom;
    String       mapLayersCSV;
    
    String       backgroundImageLink;
    String       headerBannerImage;
    String       playIdeaButtonImage;
    String       clusterMaster;
    
    Date         startDate;
    Date         endDate;
    Date         preregistrationDate;
    
    int          maxUsersOnline = 200;   // will be set by gameadmin
    int          maxUsersRegistered = Integer.MAX_VALUE;   // can be set by gameadmin, but probably ignored now
    
    GameQuestion question;    // Which gameQuestion is chosen for this game  
    Move         lastMove;    // set by code
    Move         currentMove; // Set by gamemaster
    List<Move>   moves;
    
    LinkedList<RegexPair>  linkRegexs = new LinkedList<RegexPair>();
    
    String       defaultActionPlanThePlanText;
    String       defaultActionPlanTalkText;
    String       defaultActionPlanImagesText;
    String       defaultActionPlanVideosText;
    String       defaultActionPlanMapText;
    double       defaultActionPlanMapLon;
    double       defaultActionPlanMapLat;
    
    Set<ActionPlan> top5ActionPlans = new HashSet<ActionPlan>();
    boolean      readonly=false;   // game is a normal r/w run by default
    boolean      cardsReadonly=false;
    boolean      actionPlansEnabled=true;
    boolean      showHeaderBranding=true;
    
    //boolean      restrictByQueryList=false;
    boolean      restrictByQueryListInterval=false; // not used
    boolean      topCardsReadonly=false;
    //boolean      registeredLogonsOnly=false;
    boolean      emailConfirmation=false;
    
    long         reportIntervalMinutes = 0;
    
    boolean      secondLoginPermissionPage = false;
    String       secondLoginPermissionPageTitle = "";
    String       secondLoginPermissionPageText = "";

    boolean      showFouo=false;
    String       fouoDescription;

    boolean      showPriorMovesCards;
    boolean      showPriorMovesActionPlans;
    boolean      playOnPriorMovesCards;
    boolean      editPriorMovesActionPlans;

    //short        loginPermissions;
    //short        newUserPermissions;

    // Hack
    String       displayedMoveNumberOverride;
    boolean      pdfAvailable;
    boolean      inGameMailEnabled=true;
    boolean      externalMailEnabled=true;
    boolean      bootStrapping=true;
    
    String       adminLoginMessage;
    String       gameHandle; // default = mmowgli
    Long         revision = 0L;   // used internally by hibernate for optimistic locking, but not here
    boolean      reportsShowHiddenCards;
    boolean      requireCACregistration;
    boolean      enforceCACdataRegistration;
    boolean      requireCAClogin;
    boolean      useCAClogin;
    
//@formatter:on
/*    
  public static short LOGIN_ALLOW_GAMEADMINS    = 0x0001;
  public static short LOGIN_ALLOW_GAMEMASTERS   = 0x0002;
  public static short LOGIN_ALLOW_GAMEDESIGNERS = 0x0004;
  public static short LOGIN_ALLOW_GUESTS        = 0x0008;
  
  public static short LOGIN_ALLOW_ALL = -1; //0xFFFF;
  public static short LOGIN_ALLOW_NONE = 0x0;
*/  
  public static Game get(Session sess)
  {
    return (Game)sess.get(Game.class,1L); //only one entry in current design
  }
  
  public static Game getTL()
  {
    return getTL(1L);
  }
  
  public static Game getTL(Object id)
  {
    return get(HSess.get());
  }
  
  public static void updateTL()
  {
    Game g = Game.getTL();
    g.incrementRevision();
    DB.updateTL(g);
  }
  
  // This is needed for game designer
  public static void updateTL(Game g) 
  {
    g.incrementRevision();
    DB.updateTL(g);
  }
 
  /**
   * Primary key
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
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

  public Long incrementRevision()
  {
    setRevision(revision+1);
    return getRevision();
  }

  /**
   * Brief description of the game
   * 
   * @return the description
   */
  @Basic
  public String getDescription()  //not used
  {
    return description;
  }

  /**
   * Brief description of the game
   * 
   * @param description
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * Which move is current
   */
  @ManyToOne
  public Move getCurrentMove()
  {
    return currentMove;
  }

  public void setCurrentMove(Move m)
  {
    currentMove = m;
  }
  
  /**
   * Which move was last
   */
  @ManyToOne
  public Move getLastMove()
  {
    return lastMove;
  }

  public void setLastMove(Move m)
  {
    lastMove = m;
  }

  /**
   * Defined moves for this game
   */
  @ManyToMany
  public List<Move> getMoves()
  {
    return moves;
  }
  
  public void setMoves(List<Move> moves)
  {
    this.moves = moves;
  }
  /**
   * Which question was configured for this game
   * 
   * @return the description
   */

  @ManyToOne
  public GameQuestion getQuestion()
  {
    return question;
  }

  /**
   * Configure the registration question for this game
   * 
   * @param description
   */
  public void setQuestion(GameQuestion question)
  {
    this.question = question;
  }

  /**
   * Title of the game
   * 
   * @return the title
   */
  @Basic
  public String getTitle()
  {
    return title;
  }

  /**
   * Title of the game
   * 
   * @param title
   */
  public void setTitle(String title)
  {
    this.title = title;
  }

  @Basic
  public Date getStartDate()
  {
    return startDate;
  }

  public void setStartDate(Date startDate)
  {
    this.startDate = startDate;
  }

  @Basic
  public Date getEndDate()
  {
    return endDate;
  }

  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }

  @Basic
  public Date getPreregistrationDate()
  {
    return preregistrationDate;
  }

  public void setPreregistrationDate(Date preregistrationDate)
  {
    this.preregistrationDate = preregistrationDate;
  }

  @Lob
  public String getDefaultActionPlanThePlanText()
  {
    return defaultActionPlanThePlanText;
  }

  public void setDefaultActionPlanThePlanText(String defaultActionPlanThePlanText)
  {
    this.defaultActionPlanThePlanText = defaultActionPlanThePlanText;
  }
  @Lob
  public String getDefaultActionPlanTalkText()
  {
    return defaultActionPlanTalkText;
  }

  public void setDefaultActionPlanTalkText(String defaultActionPlanTalkText)
  {
    this.defaultActionPlanTalkText = defaultActionPlanTalkText;
  }
  @Lob
  public String getDefaultActionPlanImagesText()
  {
    return defaultActionPlanImagesText;
  }

  public void setDefaultActionPlanImagesText(String defaultActionPlanImagesText)
  {
    this.defaultActionPlanImagesText = defaultActionPlanImagesText;
  }
  @Lob
  public String getDefaultActionPlanVideosText()
  {
    return defaultActionPlanVideosText;
  }

  public void setDefaultActionPlanVideosText(String defaultActionPlanVideosText)
  {
    this.defaultActionPlanVideosText = defaultActionPlanVideosText;
  }
  @Lob
  public String getDefaultActionPlanMapText()
  {
    return defaultActionPlanMapText;
  }

  public void setDefaultActionPlanMapText(String defaultActionPlanMapText)
  {
    this.defaultActionPlanMapText = defaultActionPlanMapText;
  }
  @Basic
  public int getMaxUsersOnline()
  {
    return maxUsersOnline;
  }

  public void setMaxUsersOnline(int maxUsersOnline)
  {
    this.maxUsersOnline = maxUsersOnline;
  }
  @Basic
  public int getMaxUsersRegistered()
  {
    return maxUsersRegistered;
  }

  public void setMaxUsersRegistered(int maxUsersRegistered)
  {
    this.maxUsersRegistered = maxUsersRegistered;
  }

  @OneToMany
  public Set<ActionPlan> getTop5ActionPlans()
  {
    return top5ActionPlans;
  }

  public void setTop5ActionPlans(Set<ActionPlan> top5ActionPlans)
  {
    this.top5ActionPlans = top5ActionPlans;
  }

  @Basic
  public boolean isReadonly()
  {
    return readonly;
  }

  public void setReadonly(boolean readonly)
  {
    this.readonly = readonly;
  }

//  @Basic
//  public boolean isRestrictByQueryList()
//  {
//    return restrictByQueryList;
//  }
//
//  public void setRestrictByQueryList(boolean restrictByQueryList)
//  {
//    this.restrictByQueryList = restrictByQueryList;
//  }

  @Basic
  public boolean isRestrictByQueryListInterval()
  {
    return restrictByQueryListInterval;
  }

  public void setRestrictByQueryListInterval(boolean restrictByQueryListInterval)
  {
    this.restrictByQueryListInterval = restrictByQueryListInterval;
  }

  @Basic
  public boolean isCardsReadonly()
  {
    return cardsReadonly;
  }

  public void setCardsReadonly(boolean cardsReadonly)
  {
    this.cardsReadonly = cardsReadonly;
  }
 
  @Basic
  public boolean isEmailConfirmation()
  {
    return emailConfirmation;
  }
  
  public void setEmailConfirmation(boolean emailConfirmation)
  {
    this.emailConfirmation = emailConfirmation;
  }
/*  
  @Basic
  public boolean isRegisteredLogonsOnly()
  {
    return registeredLogonsOnly;
  }

  public void setRegisteredLogonsOnly(boolean registeredLogonsOnly)
  {
    this.registeredLogonsOnly = registeredLogonsOnly;
  }
*/
  @Basic
  public String getDisplayedMoveNumberOverride()
  {
    return displayedMoveNumberOverride;
  }

  public void setDisplayedMoveNumberOverride(String displayedMoveNumberOverride)
  {
    this.displayedMoveNumberOverride = displayedMoveNumberOverride;
  }

  @Basic
  public String getClusterMaster()
  {
    return clusterMaster;
  }

  public void setClusterMaster(String clusterMaster)
  {
    this.clusterMaster = clusterMaster;
  }

  @Basic
  public String getBackgroundImageLink()
  {
    return backgroundImageLink;
  }

  public void setBackgroundImageLink(String backgroundImageLink)
  {
    this.backgroundImageLink = backgroundImageLink;
  }

  @ManyToOne
  public Media getHeaderBranding()
  {
    return headerBranding;
  }

  public void setHeaderBranding(Media headerBranding)
  {
    this.headerBranding = headerBranding;
  }

  @Basic
  public boolean isTopCardsReadonly()
  {
    return topCardsReadonly;
  }

  public void setTopCardsReadonly(boolean topCardsReadonly)
  {
    this.topCardsReadonly = topCardsReadonly;
  }

  @Lob
  public LinkedList<RegexPair> getLinkRegexs()
  {
    return linkRegexs;
  }

  public void setLinkRegexs(LinkedList<RegexPair> linkRegexs)
  {
    this.linkRegexs = linkRegexs;
  }
  @Basic
  public String getMapTitle()
  {
    return mapTitle;
  }

  public void setMapTitle(String mapTitle)
  {
    this.mapTitle = mapTitle;
  }
  
  @Basic
  public String getMapLayersCSV()
  {
    return mapLayersCSV;
  }
  
  public void setMapLayersCSV(String s)
  {
    mapLayersCSV = s;
  }
  
  @Basic
  public long getVersion()
  {
	return version;
  }

	public void setVersion(long version) {
		this.version = version;
	}

	@Basic
	public double getDefaultActionPlanMapLon()
	{
		return defaultActionPlanMapLon;
	}

	public void setDefaultActionPlanMapLon(double defaultActionPlanMapLon)
	{
		this.defaultActionPlanMapLon = defaultActionPlanMapLon;
	}

	@Basic
	public double getDefaultActionPlanMapLat()
	{
		return defaultActionPlanMapLat;
	}

	public void setDefaultActionPlanMapLat(double defaultActionPlanMapLat)
	{
		this.defaultActionPlanMapLat = defaultActionPlanMapLat;
	}

  public long getReportIntervalMinutes()
  {
    return reportIntervalMinutes;
  }
  
  @Basic
  public void setReportIntervalMinutes(long reportIntervalMinutes)
  {
    this.reportIntervalMinutes = reportIntervalMinutes;
  }
  
  public String getAcronym()
  {
    return acronym;
  }
  
  @Basic
  public void setAcronym(String acronym)
  {
    this.acronym = acronym;
  }
  
  @Basic
  public boolean isSecondLoginPermissionPage()
  {
    return secondLoginPermissionPage;
  }

  public void setSecondLoginPermissionPage(boolean secondLoginPermissionPage)
  {
    this.secondLoginPermissionPage = secondLoginPermissionPage;
  }

  @Basic
  public String getSecondLoginPermissionPageTitle()
  {
    return secondLoginPermissionPageTitle;
  }

  public void setSecondLoginPermissionPageTitle(String secondLoginPermissionPageTitle)
  {
    this.secondLoginPermissionPageTitle = secondLoginPermissionPageTitle;
  }
  
  @Lob
  public String getSecondLoginPermissionPageText()
  {
    return secondLoginPermissionPageText;
  }

  public void setSecondLoginPermissionPageText(String secondLoginPermissionPageText)
  {
    this.secondLoginPermissionPageText = secondLoginPermissionPageText;
  }

  @Basic
  public boolean isShowFouo()
  {
    return showFouo;
  }

  public void setShowFouo(boolean showFouo)
  {
    this.showFouo = showFouo;
  }

  @Basic
  public String getFouoDescription()
  {
    return fouoDescription;
  }

  public void setFouoDescription(String fouoDescription)
  {
    this.fouoDescription = fouoDescription;
  }

  @Basic
  public boolean isShowPriorMovesCards()
  {
    return showPriorMovesCards;
  }

  public void setShowPriorMovesCards(boolean showPriorMovesCards)
  {
    this.showPriorMovesCards = showPriorMovesCards;
  }

  @Basic
  public boolean isShowPriorMovesActionPlans()
  {
    return showPriorMovesActionPlans;
  }

  public void setShowPriorMovesActionPlans(boolean showPriorMovesActionPlans)
  {
    this.showPriorMovesActionPlans = showPriorMovesActionPlans;
  }

  @Basic
  public boolean isPlayOnPriorMovesCards()
  {
    return playOnPriorMovesCards;
  }
  
  public void setPlayOnPriorMovesCards(boolean playOnPriorMovesCards)
  {
    this.playOnPriorMovesCards = playOnPriorMovesCards;
  }

  @Basic
  public boolean isEditPriorMovesActionPlans()
  {
    return editPriorMovesActionPlans;
  }

  public void setEditPriorMovesActionPlans(boolean editPriorMovesActionPlans)
  {
    this.editPriorMovesActionPlans = editPriorMovesActionPlans;
  }
 
  @Basic
  public boolean isPdfAvailable()
  {
    return pdfAvailable;
  }

  public void setPdfAvailable(boolean pdfAvailable)
  {
    this.pdfAvailable = pdfAvailable;
  }
  
  @Basic
  public boolean isReportsShowHiddenCards()
  {
    return reportsShowHiddenCards;
  }

  public void setReportsShowHiddenCards(boolean reportsShowHiddenCards)
  {
    this.reportsShowHiddenCards = reportsShowHiddenCards;
  }
  
  @Basic
  public boolean isInGameMailEnabled()
  {
    return inGameMailEnabled;
  }

  public void setInGameMailEnabled(boolean inGameMailEnabled)
  {
    this.inGameMailEnabled = inGameMailEnabled;
  }

  @Basic
  public boolean isExternalMailEnabled()
  {
    return externalMailEnabled;
  }

  public void setExternalMailEnabled(boolean externalMailEnabled)
  {
    this.externalMailEnabled = externalMailEnabled;
  }
  
  @Basic
  public boolean isBootStrapping()
  {
    return bootStrapping;
  }

  public void setBootStrapping(boolean bootStrapping)
  {
    this.bootStrapping = bootStrapping;
  }

  @Lob
  public String getAdminLoginMessage()
  {
    return adminLoginMessage;
  }

  public void setAdminLoginMessage(String adminLoginMessage)
  {
    this.adminLoginMessage = adminLoginMessage;
  }

  private float userSignupAnswerPoints = 10.f;
  private float userActionPlanCommentPoints = 0.0f;
  
  private float cardSuperInterestingPoints = 5.0f;
  //private float actionPlanThumbPoints = 0.0f;  // unused?
  private float actionPlanCommentPoints = 3.0f;
  private float actionPlanThumbFactor = 1.0f;
  private float actionPlanAuthorPoints = 100.0f;
  private float actionPlanSuperInterestingPoints = 12.0f;
  private float actionPlanRaterPoints = 5.0f;
  
  private float cardAncestorPoints = 1.0f;
  private String cardAncestorPointsGenerationFactors = "2.0 1.8 1.6 1.4 1.2 1.0 0.8";
  
  private float cardAuthorPoints = 7.0f;
  //private boolean cardAncestorEarlyPointsBias = true;
  
  @Basic
  public Float getUserSignupAnswerPoints()
  {
    return userSignupAnswerPoints;
  }

  public void setUserSignupAnswerPoints(Float userSignupAnswerPoints)
  {
    this.userSignupAnswerPoints = userSignupAnswerPoints;
  }
  
  @Basic
  public Float getUserActionPlanCommentPoints()
  {
    return userActionPlanCommentPoints;
  }

  public void setUserActionPlanCommentPoints(Float userActionPlanCommentPoints)
  {
    this.userActionPlanCommentPoints = userActionPlanCommentPoints;
  }  
  
  @Basic
  public Float getCardSuperInterestingPoints()
  {
    return cardSuperInterestingPoints;
  }
  @Transient
  public void setCardSuperInterestingPoints(Float cardSuperInterestingPoints)
  {
    this.cardSuperInterestingPoints = cardSuperInterestingPoints;
  }
  
  @Basic
  public Float getActionPlanSuperInterestingPoints()
  {
    return actionPlanSuperInterestingPoints;
  }
  
  public void setActionPlanSuperInterestingPoints(Float f)
  {
    actionPlanSuperInterestingPoints = f;
  }
  
  @Basic
  public Float getActionPlanCommentPoints()
  {
    return actionPlanCommentPoints;
  }

  public void setActionPlanCommentPoints(Float actionPlanCommentPoints)
  {
    this.actionPlanCommentPoints = actionPlanCommentPoints;
  }

  @Basic
  public Float getActionPlanThumbFactor()
  {
    return actionPlanThumbFactor;
  }

  public void setActionPlanThumbFactor(Float actionPlanThumbFactor)
  {
    this.actionPlanThumbFactor = actionPlanThumbFactor;
  }

  @Basic
  public Float getActionPlanAuthorPoints()
  {
    return actionPlanAuthorPoints;
  }

  public void setActionPlanAuthorPoints(Float actionPlanAuthorPoints)
  {
    this.actionPlanAuthorPoints = actionPlanAuthorPoints;
  }

  @Basic
  public String getCardAncestorPointsGenerationFactors()
  {
    return cardAncestorPointsGenerationFactors;
  }

  public void setCardAncestorPointsGenerationFactors(String f)
  {
    this.cardAncestorPointsGenerationFactors = f;
  }
  
  @Basic
  public float getCardAncestorPoints()
  {
    return cardAncestorPoints;
  }

  public void setCardAncestorPoints(Float f)
  {
    this.cardAncestorPoints = f;
  }

  @Basic
  public Float getCardAuthorPoints()
  {
    return cardAuthorPoints;
  }  

  public void setCardAuthorPoints(Float cardAuthorPoints)
  {
    this.cardAuthorPoints = cardAuthorPoints;
  }  

  @Basic
  public float getActionPlanRaterPoints()
  {
    return actionPlanRaterPoints;
  }  

  public void setActionPlanRaterPoints(Float actionPlanRaterPoints)
  {
    this.actionPlanRaterPoints=actionPlanRaterPoints;
  }

  @Basic
  public String getHeaderBannerImage()
  {
    return headerBannerImage;
  } 
  
  public void setHeaderBannerImage(String name)
  {
    headerBannerImage = name;
  }

 @Basic
  public boolean isActionPlansEnabled()
  {
    return actionPlansEnabled;
  }
 
 public void setActionPlansEnabled(boolean yn)
 {
   actionPlansEnabled = yn;
 }

 @Basic
 public boolean isShowHeaderBranding()
 {
  return showHeaderBranding;
 }
 
 public void setShowHeaderBranding(boolean yn)
                                                                       {
  showHeaderBranding = yn;
 }
 
@Basic
public String getPlayIdeaButtonImage()
{
  return playIdeaButtonImage;
}

public void setPlayIdeaButtonImage(String s)
{
  playIdeaButtonImage = s;
}

@Basic
public String getGameHandle()
{
  return gameHandle;
}

public void setGameHandle(String s)
{
  gameHandle = s;
}

@Basic
public double getMapLatitude()
{
  return mapLatitude;
}

public void setMapLatitude(double mapLatitude)
{
  this.mapLatitude = mapLatitude;
}

@Basic
public double getMapLongitude()
{
  return mapLongitude;
}

public void setMapLongitude(double mapLongitude)
{
  this.mapLongitude = mapLongitude;
}

@Basic
public int getMapZoom()
{
  return mapZoom;
}

public void setMapZoom(int mapZoom) {
	this.mapZoom = mapZoom;
}

public boolean isRequireCACregistration()
{
  return requireCACregistration;
}

public void setRequireCACregistration(boolean requireCACregistration)
{
  this.requireCACregistration = requireCACregistration;
}

public boolean isEnforceCACdataRegistration()
{
  return enforceCACdataRegistration;
}

public void setEnforceCACdataRegistration(boolean enforceCACdataRegistration)
{
  this.enforceCACdataRegistration = enforceCACdataRegistration;
}

public boolean isRequireCAClogin()
{
  return requireCAClogin;
}

public void setRequireCAClogin(boolean requireCAClogin)
{
  this.requireCAClogin = requireCAClogin;
}

public boolean isUseCAClogin()
{
  return useCAClogin;
}

public void setUseCAClogin(boolean useCAClogin)
{
  this.useCAClogin = useCAClogin;
}

}
