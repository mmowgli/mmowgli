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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.annotations.*;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.hibernate.DB;

/**
 *  This is a database table, listing action plans
 * 
 * @created on Dec 16, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@SuppressWarnings("deprecation")
@Entity
@Indexed(index="mmowgli")
public class ActionPlan implements Serializable
{
  private static final long serialVersionUID = 3666861771515672241L;
 
  public static String[] ACTIONPLAN_SEARCH_FIELDS = {"id","title","subTitle","whatIsItText","whatWillItTakeText","howWillItWorkText","howWillItChangeText"};  // must be annotated for hibernate search
  public static int HISTORY_SIZE = 10;
  
  //@formatter:off
  long         id;          // primary key*/
  long         idForSorting;
  String       title;
  SortedSet<Edits>  titlesEditHistory = new TreeSet<Edits>();

  String       subTitle;
  SortedSet<Edits>   subTitleEditHistory = new TreeSet<Edits>();

  @IndexedEmbedded
  Set<User>    authors = new HashSet<User>();
  User         lockedBy;
  Set<User>    innovators = new HashSet<User>(); // not used?
  Card         chainRoot; 

  @IndexedEmbedded
  Set<User>    invitees = new HashSet<User>();
  Set<User>    declinees = new HashSet<User>();

  String       quickAuthorList;
  boolean      powerPlay = false;
  boolean      hidden = false;
  
  float        currentAuthorInnovationPoints = 0.0f;     // not used ?
  float        currentInnoBrokerInnovationPoints = 0.0f; // not used ?
  
  String       headline;
  List<String> planFields;
  
  // Similar to the map of thumb SCORES in User
  Map<User,Integer> userThumbs   = new HashMap<User,Integer>();
  double       averageThumb = 0.0d;
  double       sumThumbs = 0.0d;
  
  Set<Award> awards = new HashSet<Award>();
  
  SortedSet<Message> comments = new TreeSet<Message>();
  SortedSet<Message> authorMessages = new TreeSet<Message>();
  String       discussion; // replaces authorMessages?
  ChatLog      chatLog = new ChatLog();
  List<Media>  media = new ArrayList<Media>(); // images and videos
  
  String       planInstructions;
  String       talkItOverInstructions;
  String       mapInstructions; 
  String       imagesInstructions;
  String       videosInstructions;
  String       helpWanted;
  
  String             whatIsItText;
  SortedSet<Edits>   whatIsItEditHistory = new TreeSet<Edits>();
  
  String             whatWillItTakeText;
  SortedSet<Edits>   whatTakeEditHistory = new TreeSet<Edits>();

  String             howWillItWorkText;
  SortedSet<Edits>   howWorkEditHistory = new TreeSet<Edits>();

  String             howWillItChangeText;
  SortedSet<Edits>   howChangeEditHistory = new TreeSet<Edits>();

  GoogleMap    map = new GoogleMap();
  double       priceToInvest = 200.0d;
  
  Date        creationDate;
  Move        createdInMove;
  
  boolean     superInteresting;
  
  Long revision = 0L;   // used internally by hibernate for optimistic locking, but not here
//@formatter:on

  public ActionPlan()
  {}
  
  public ActionPlan(String headline)
  {
    setHeadline(headline);
  }

  public static HbnContainer<ActionPlan> getContainer()
  {
    return DB.getContainer(ActionPlan.class);
  }

  public static ActionPlan getTL(Object id)
  {
    return DB.getTL(ActionPlan.class, id);
  }
  
  public static ActionPlan get(Object id, Session sess)
  {
    return DB.get(ActionPlan.class, id, sess);
  }  
  
  public static ActionPlan mergeTL(ActionPlan ap)
  {
    return DB.mergeTL(ap);
  }
  
  public static ActionPlan merge(ActionPlan ap, Session sess)
  {
    return DB.merge(ap, sess);
  }
  
  public static void updateTL(ActionPlan ap)
  {
    ap.incrementRevision();
    DB.updateTL(ap);  
  }
  
  public static void saveTL(ActionPlan ap)
  {
    DB.saveTL(ap);
  }

  /**
   * @return the primary key
   */
  @Id
  @Basic
  @DocumentId
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Field(analyze=Analyze.NO) //index=Index.UN_TOKENIZED)
  public long getId()
  {
    return id;
  }

  /**
   * @param id
   */
  public void setId(long id)
  {
    this.id = id;
    idForSorting = id;
  }

  @Basic
  public long getIdForSorting()
  {
    return idForSorting;
  }

  public void setIdForSorting(long idForSorting)
  {
    this.idForSorting = idForSorting;
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
   * @return the headline
   */
  @Basic
  public String getHeadline()
  {
    return headline;
  }

  /**
   * @headline to set
   */
  public void setHeadline(String headline)
  {
    this.headline = headline;
  }

  /**
   * @return the title
   */
  @Basic
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getTitle()
  {
    return title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title)
  {
    this.title = title;
  }
  
  public void setTitleWithHistoryTL(String s)
  {
    setTitle(s);
    pushHistoryTL(getTitlesEditHistory(),s);//pushHistory(titles,s);
  }
  
  /**
   * @return the subTitle (now the "who is involved" field)
   */
  @Lob
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getSubTitle()
  {
    return subTitle;
  }

  /**
   * @param subTitle the subTitle to set
   */
  public void setSubTitle(String subTitle)
  {
    this.subTitle = subTitle;
  }
  
  public void setSubTitleWithHistoryTL(String s)
  {
    setSubTitle(s);
    pushHistoryTL(getSubTitleEditHistory(),s); //pushHistory(subTitleHistory,s);
  }
  
  /**
   * @return the authors
   * explicitly name the table for clarity, although we don't have to since we did it for innovators
   */
  @ManyToMany
  @JoinTable(name="ActionPlan_Authors",
      joinColumns = @JoinColumn(name="actionPlan_id"),
      inverseJoinColumns = @JoinColumn(name="author_user_id")
  )
  public Set<User> getAuthors()
  {
    return authors;
  }

  /**
   * @param authors the authors to set
   */
  public void setAuthors(Set<User> authors)
  {
    this.authors = authors;
  }
 
  public void addAuthor(User u)
  {
    getAuthors().add(u);
    rebuildQuickAuthorList();
  }  
  
  public void removeAuthor(User u)
  {
    getAuthors().remove(u);
    rebuildQuickAuthorList();
  }
  
  /**
   * @return the quickAuthorList
   */
  @Basic
  public String getQuickAuthorList()
  {
    return quickAuthorList;
  }

  /**
   * @param quickAuthorList the quickAuthorList to set
   */
  public void setQuickAuthorList(String quickAuthorList)
  {
    this.quickAuthorList = quickAuthorList;
  }

  public void rebuildQuickAuthorList()
  {
    StringBuilder sb = new StringBuilder();
    for(User u : getAuthors()) {
      sb.append(u.getUserName());
      sb.append(",");
    }
    if(sb.length()>0) {  // can happen when looking at old, flaky db's
      sb.setLength(sb.length()-1); //lose last comma
      setQuickAuthorList(sb.toString());
    }
  }
  
   /**
   * @return the innovators
   * Have to do this to force another table, ActionPlan_User is taken
   */
  @ManyToMany
  @JoinTable(name="ActionPlan_InnovationBrokers",
      joinColumns = @JoinColumn(name="actionPlan_id"),
      inverseJoinColumns = @JoinColumn(name="broker_user_id")
  )
 public Set<User> getInnovators()
  {
    return innovators;
  }

  /**
   * @param innovators the innovators to set
   */
  public void setInnovators(Set<User> innovators)
  {
    this.innovators = innovators;
  }

  @ManyToOne
  public User getLockedBy()
  {
    return lockedBy;
  }

  public void setLockedBy(User lockedBy)
  {
    this.lockedBy = lockedBy;
  }

  /**
   * @return the chainRoot
   */
  @ManyToOne
  public Card getChainRoot()
  {
    return chainRoot;
  }

  /**
   * @param chainRoot the chainRoot to set
   */
  public void setChainRoot(Card chainRoot)
  {
    this.chainRoot = chainRoot;
  }

  /**
   * @return the planFields
   */
  @ElementCollection
  @CollectionTable(name="ActionPlan_PlanFields")
  public List<String> getPlanFields()
  {
    return planFields;
  }

  /**
   * @param planFields the planFields to set
   */
  public void setPlanFields(List<String> planFields)
  {
    this.planFields = planFields;
  }

  /**
   * @return the planInstructions
   */
  @Lob
  public String getPlanInstructions()
  {
    return planInstructions;
  }

  /**
   * @param planInstructions the planInstructions to set
   */
  public void setPlanInstructions(String planInstructions)
  {
    this.planInstructions = planInstructions;
  }

  /**
   * @return the comments
   */
  @OneToMany
  @JoinTable(name="ActionPlan_Comments",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="message_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=ChatLog.DateDescComparator.class)
  //@SortComparator(value=ChatLog.DateDescComparator.class)   //undeprecated way, but but in Hib 4?
  public SortedSet<Message> getComments()
  {
    return comments;
  }

  /**
   * @param messages the messages to set
   */
  public void setComments(SortedSet<Message> comments)
  {
    this.comments = comments;
  }

  /**
   * @return the authorMessages
   */
  @OneToMany
  @JoinTable(name="ActionPlan_AuthorMessages",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="message_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=ChatLog.DateDescComparator.class)
  //@SortComparator(value=ChatLog.DateDescComparator.class)   //undeprecated way, but but in Hib 4?
  public SortedSet<Message> getAuthorMessages()
  {
    return authorMessages;
  }

  /**
   * @param authorMessages the authorMessages to set
   */
  public void setAuthorMessages(SortedSet<Message> authorMessages)
  {
    this.authorMessages = authorMessages;
  }
  
  /**
   * @return the discussion
   */
  @Lob
  public String getDiscussion()
  {
    return discussion;
  }

  /**
   * @param discussion the discussion to set
   */
  public void setDiscussion(String discussion)
  {
    this.discussion = discussion;
  }


  /**
   * @return the talkItOverInstructions
   */
  @Lob
  public String getTalkItOverInstructions()
  {
    return talkItOverInstructions;
  }

  /**
   * @param talkItOverInstructions the talkItOverInstructions to set
   */
  public void setTalkItOverInstructions(String talkItOverInstructions)
  {
    this.talkItOverInstructions = talkItOverInstructions;
  }
  
  /**
   * @return the media
   */
  @ManyToMany
  @OrderBy("id")
  public List<Media> getMedia()
  {
    return media;
  }

  /**
   * @param media the media to set
   */
  public void setMedia(List<Media> media)
  {
    this.media = media;
  }

  /**
   * @return the mapInstructions
   */
  @Lob
  public String getMapInstructions()
  {
    return mapInstructions;
  }

  /**
   * @param mapInstructions the mapInstructions to set
   */
  public void setMapInstructions(String mapInstructions)
  {
    this.mapInstructions = mapInstructions;
  }

  /**
   * @return the imagesInstructions
   */
  @Lob
  public String getImagesInstructions()
  {
    return imagesInstructions;
  }

  /**
   * @param imagesInstructions the imagesInstructions to set
   */
  public void setImagesInstructions(String imagesInstructions)
  {
    this.imagesInstructions = imagesInstructions;
  }

  /**
   * @return the videosInstructions
   */
  @Lob
  public String getVideosInstructions()
  {
    return videosInstructions;
  }

  /**
   * @param videosInstructions the videosInstructions to set
   */
  public void setVideosInstructions(String videosInstructions)
  {
    this.videosInstructions = videosInstructions;
  }
  
  /**
   * @return the powerPlay
   */
  @Basic
  public boolean isPowerPlay()
  {
    return powerPlay;
  }

  /**
   * @param powerPlay the powerPlay to set
   */
  public void setPowerPlay(boolean powerPlay)
  {
    this.powerPlay = powerPlay;
  }

  /**
   * @return the currentAuthorInnovationPoints
   */
  @Basic
  public float getCurrentAuthorInnovationPoints()
  {
    return currentAuthorInnovationPoints;
  }

  /**
   * @param currentAuthorInnovationPoints the currentAuthorInnovationPoints to set
   */
  public void setCurrentAuthorInnovationPoints(float currentAuthorInnovationPoints)
  {
    this.currentAuthorInnovationPoints = currentAuthorInnovationPoints;
  }

  /**
   * @return the currentInnoBrokerInnovationPoints
   */
  @Basic
  public float getCurrentInnoBrokerInnovationPoints()
  {
    return currentInnoBrokerInnovationPoints;
  }

  /**
   * @param currentInnoBrokerInnovationPoints the currentInnoBrokerInnovationPoints to set
   */
  public void setCurrentInnoBrokerInnovationPoints(float currentInnoBrokerInnovationPoints)
  {
    this.currentInnoBrokerInnovationPoints = currentInnoBrokerInnovationPoints;
  }

  @Lob
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getWhatIsItText()
  {
    return whatIsItText;
  }

  public void setWhatIsItText(String whatIsItText)
  {
    this.whatIsItText = whatIsItText;
  }
  
  public void setWhatIsItTextWithHistoryTL(String s)
  {
    setWhatIsItText(s);
    pushHistoryTL(getWhatIsItEditHistory(),s);//pushHistory(whatIsItHistory, s);
  }
  
  @Lob
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getWhatWillItTakeText()
  {
    return whatWillItTakeText;
  }
  
  public void setWhatWillItTakeText(String whatWillItTakeText)
  {
    this.whatWillItTakeText = whatWillItTakeText;
  }
  
  public void setWhatWillItTakeTextWithHistoryTL(String s)
  {
    setWhatWillItTakeText(s);
    pushHistoryTL(getWhatTakeEditHistory(),s); //pushHistory(whatTakeHistory,s);
  }
  
  @Lob
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getHowWillItWorkText()
  {
    return howWillItWorkText;
  }

  public void setHowWillItWorkText(String howWillItWorkText)
  {
    this.howWillItWorkText = howWillItWorkText;
  }

  public void setHowWillItWorkTextWithHistoryTL(String s)
  {
    setHowWillItWorkText(s);
    pushHistoryTL(getHowWorkEditHistory(),s); //pushHistory(howWorkHistory, s);
  }
 
  private void pushHistoryTL(SortedSet<Edits>set, String s) //LinkedList<String>lis, String s)
  {
    if(set != null) {
      Edits e = new Edits(s);
      Edits.saveTL(e);
      set.add(e);
   /* Don't need to remove   while(set.size() > HISTORY_SIZE) {
        Edits junk = set.first();
        System.out.println("Removing "+junk.getValue());
        set.remove(set.first());
      } */
    }  
  }
  
  @Lob
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getHowWillItChangeText()
  {
    return howWillItChangeText;
  }

  public void setHowWillItChangeText(String howWillItChangeText)
  {
    this.howWillItChangeText = howWillItChangeText;
  }
  
  public void setHowWillItChangeTextWithHistoryTL(String s)
  {
    setHowWillItChangeText(s);
    pushHistoryTL(getHowChangeEditHistory(),s); // pushHistory(howChangeHistory,s);
  }
  
  @ManyToOne
  public ChatLog getChatLog()
  {
    return chatLog;
  }

  public void setChatLog(ChatLog chatLog)
  {
    this.chatLog = chatLog;
  }

  @ManyToOne
  public GoogleMap getMap()
  {
    return map;
  }

  public void setMap(GoogleMap map)
  {
    this.map = map;
  }

  @ElementCollection
  @CollectionTable(name="ActionPlan_ThumbsByUser")
  public Map<User, Integer> getUserThumbs()
  {
    return userThumbs;
  }

  public void setUserThumbs(Map<User, Integer> userThumbs)
  {
    this.userThumbs = userThumbs;
  }

  // Easiest way to do it
  /**
   * @param u user object
   * @param thumbs 0-3; 0 means no vote: remove from score consideration
   */
  public void setUserThumbValue(User u, int thumbs)
  {
    Map<User,Integer> thumbMap = getUserThumbs();
    Integer I = thumbMap.get(u);
    double smTh = getSumThumbs();
    if(I != null)
      smTh -= I;

    if(thumbs == 0 && I != null)
      thumbMap.remove(u);
    else
      thumbMap.put(u, thumbs);

    smTh += thumbs;
    
    int sz = thumbMap.size();
    double avg = 0;
    if(sz>0)
      avg = smTh / sz;
    
    setAverageThumb(avg);
    setSumThumbs(smTh);
  }
 /* 
  public void recalculateThumbs(ApplicationEntryPoint app)
  {
    Map<User,Integer> thumbMap = getUserThumbs();
    float sumTh = 0.0f;
    int numVotes = 0;
    
    Set<User> userSet = thumbMap.keySet();
    Vector<User> vect = new Vector<User>(userSet);
    
    Iterator<User> itr = vect.iterator();
    while(itr.hasNext()) {
      User u = itr.next();
      
      Integer I = thumbMap.get(u);
      if(I != null) {
        if(I.intValue() == 0) {
          thumbMap.remove(u);
          app.globs().scoreManager().actionPlanWillBeRated(u, this, 0); // take away 5 points if they (mistakenly) got some for rating 0
        }
        else {
          sumTh += I;
          numVotes++;
        }
      }
    }
    this.setSumThumbs(sumTh);
    this.setAverageThumb(numVotes==0 ? 0.0d : sumTh/numVotes);

    ActionPlan.update(this);
  }
  */
  //todo combine
/*  public void recalculateThumbs(Session sess)
  {
    Map<User,Integer> thumbMap = getUserThumbs();
    float sumTh = 0.0f;
    int numVotes = 0;
    
    Set<User> userSet = thumbMap.keySet();
    Vector<User> vect = new Vector<User>(userSet);
    
    Iterator<User> itr = vect.iterator();
    while(itr.hasNext()) {
      User u = itr.next();
      
      Integer I = thumbMap.get(u);
      if(I != null) {
        if(I.intValue() == 0) {
          thumbMap.remove(u);
          ScoreManager2.actionPlanWillBeRated_oob(u, this, 0, sess);
          //app.globs().scoreManager().userWillRateActionPlan(u, this, 0); // take away 5 points if they (mistakenly) got some for rating 0
        }
        else {
          sumTh += I;
          numVotes++;
        }
      }
    }
    this.setSumThumbs(sumTh);
    this.setAverageThumb(numVotes==0 ? 0.0d : sumTh/numVotes);
    sess.update(this);
    //ActionPlan.update(this);
  }
*/  
  @Basic
  public double getAverageThumb()
  {
    return averageThumb;
  }

  public void setAverageThumb(double averageThumb)  // not used
  {
    this.averageThumb = averageThumb;
  }
  
  @Basic
  public double getSumThumbs()
  {
    return sumThumbs;
  }

  public void setSumThumbs(double sumThumbs)
  {
    this.sumThumbs = sumThumbs;
  }

  @OneToMany
  public Set<Award> getAwards()
  {
    return awards;
  }

  public void setAwards(Set<Award> awards)
  {
    this.awards = awards;
  }

  @Basic
  public double getPriceToInvest()
  {
    return priceToInvest;
  }

  public void setPriceToInvest(double priceToInvest)
  {
    this.priceToInvest = priceToInvest;
  }

   @ManyToMany
  @JoinTable(name="ActionPlan_Invitees",
      joinColumns = @JoinColumn(name="actionPlan_id"),
      inverseJoinColumns = @JoinColumn(name="user_id")
  )
  public Set<User> getInvitees()
  {
    return invitees;
  }

  public void setInvitees(Set<User> invitees)
  {
    this.invitees = invitees;
  }
  
  public void addInvitee(User u)
  {
    getInvitees().add(u);
  }
  
  public void removeInvitee(User u)
  {
    getInvitees().remove(u);
  }
  
  @ManyToMany
  @JoinTable(name="ActionPlan_Declinees",
      joinColumns = @JoinColumn(name="actionPlan_id"),
      inverseJoinColumns = @JoinColumn(name="user_id")
  )
  public Set<User> getDeclinees()
  {
    return declinees;
  }

  public void setDeclinees(Set<User> declinees)
  {
    this.declinees = declinees;
  }

  @Lob
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getHelpWanted()
  {
    return helpWanted;
  }

  public void setHelpWanted(String helpWanted)
  {
    this.helpWanted = helpWanted;
  }

  /**
   * @return the hidden
   */
  @Basic
  public boolean isHidden()
  {
    return hidden;
  }

  /**
   * @param hidden the hidden to set
   */
  public void setHidden(boolean hidden)
  {
    this.hidden = hidden;
  }
  
  @OneToMany
  @JoinTable(name="ActionPlan_Titles_History",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="edits_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=Edits.EditsDateDescComparator.class)
  //@SortComparator(value=Edits.EditsDateDescComparator.class)
  public SortedSet<Edits> getTitlesEditHistory()
  {
    return titlesEditHistory;
  }

  /**
   * @param titlesEditHistory the titlesEditHistory to set
   */
  public void setTitlesEditHistory(SortedSet<Edits> titlesEditHistory)
  {
    this.titlesEditHistory = titlesEditHistory;
  }

  @OneToMany
  @JoinTable(name="ActionPlan_SubTitles_History",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="edits_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=Edits.EditsDateDescComparator.class)
  //@SortComparator(value=Edits.EditsDateDescComparator.class)
  public SortedSet<Edits> getSubTitleEditHistory()
  {
    return subTitleEditHistory;
  }

  /**
   * @param subTitleEditHistory the subTitleEditHistory to set
   */
  public void setSubTitleEditHistory(SortedSet<Edits> subTitleEditHistory)
  {
    this.subTitleEditHistory = subTitleEditHistory;
  }

  @OneToMany
  @JoinTable(name="ActionPlan_WhatIs_History",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="edits_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=Edits.EditsDateDescComparator.class)
  //@SortComparator(value=Edits.EditsDateDescComparator.class)

  public SortedSet<Edits> getWhatIsItEditHistory()
  {
    return whatIsItEditHistory;
  }

  /**
   * @param whatIsItEditHistory the whatIsItEditHistory to set
   */
  public void setWhatIsItEditHistory(SortedSet<Edits> whatIsItEditHistory)
  {
    this.whatIsItEditHistory = whatIsItEditHistory;
  }

  @OneToMany
  @JoinTable(name="ActionPlan_WhatTake_History",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="edits_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=Edits.EditsDateDescComparator.class)
  //@SortComparator(value=Edits.EditsDateDescComparator.class)

  public SortedSet<Edits> getWhatTakeEditHistory()
  {
    return whatTakeEditHistory;
  }

  /**
   * @param whatTaketEditHistory the whatTaketEditHistory to set
   */
  public void setWhatTakeEditHistory(SortedSet<Edits> whatTaketEditHistory)
  {
    this.whatTakeEditHistory = whatTaketEditHistory;
  }

  @OneToMany
  @JoinTable(name="ActionPlan_HowWork_History",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="edits_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=Edits.EditsDateDescComparator.class)
  //@SortComparator(value=Edits.EditsDateDescComparator.class)

  public SortedSet<Edits> getHowWorkEditHistory()
  {
    return howWorkEditHistory;
  }

  /**
   * @param howWorkEditHistory the howWorkEditHistory to set
   */
  public void setHowWorkEditHistory(SortedSet<Edits> howWorkEditHistory)
  {
    this.howWorkEditHistory = howWorkEditHistory;
  }

  @OneToMany
  @JoinTable(name="ActionPlan_HowChange_History",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="edits_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=Edits.EditsDateDescComparator.class)
  //@SortComparator(value=Edits.EditsDateDescComparator.class)

  public SortedSet<Edits> getHowChangeEditHistory()
  {
    return howChangeEditHistory;
  }

  /**
   * @param howChangeEditHistory the howChangeEditHistory to set
   */
  public void setHowChangeEditHistory(SortedSet<Edits> howChangeEditHistory)
  {
    this.howChangeEditHistory = howChangeEditHistory;
  }

  @Basic
  public Date getCreationDate()
  {
    return creationDate;
  }

  public void setCreationDate(Date creationDate)
  {
    this.creationDate = creationDate;
  }

  // A move is referenced by many entities
  @ManyToOne
  public Move getCreatedInMove()
  {
    return createdInMove;
  }

  public void setCreatedInMove(Move createdInMove)
  {
    this.createdInMove = createdInMove;
  }

  @Basic
  public boolean isSuperInteresting()
  {
    return superInteresting;
  }

  public void setSuperInteresting(boolean superInteresting)
  {
    this.superInteresting = superInteresting;
  }

//  public static Criteria adjustCriteriaToOmitActionPlans(Criteria crit, User me)
//  {
//    Move thisMove = Move.getCurrentMove();
//    if(me.isAdministrator() || Game.get().isShowPriorMovesActionPlans())
//      ;
//    else {
//     crit.createAlias("createdInMove", "MOVE")
//         .add(Restrictions.eq("MOVE.number", thisMove.getNumber()));
//    }
//    if(!me.isAdministrator())
//      crit.add(Restrictions.ne("hidden", true));
//    return crit;
//  }

  public static Criteria adjustCriteriaToOmitActionPlansTL(Criteria crit, User me)
  {
    Move thisMove = Move.getCurrentMoveTL();
    if(me.isAdministrator() || Game.getTL().isShowPriorMovesActionPlans())
      ;
    else {
     crit.createAlias("createdInMove", "MOVE")
         .add(Restrictions.eq("MOVE.number", thisMove.getNumber()));
    }
    if(!me.isAdministrator())
      crit.add(Restrictions.ne("hidden", true));
    return crit;
  }


}
