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

package edu.nps.moves.mmowgli.export;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * UserExporter.java Created on Dec 3, 2013
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserExporter extends BaseExporter
{
  Thread thread;
  
  private String CDATA_ELEMENTS       = BRIEFING_TEXT_ELEM/*+" "+blah1+" "+blah2*/;
  
  public final String STYLESHEET_NAME = "PlayerProfiles.xsl";
  public final String FILE_NAME       = "PlayerProfiles";
  public final String THREAD_NAME     = "UserExporter";
  
  private static Map<String,String> parameters = null;
  
  @HibernateSessionThreadLocalConstructor
  public UserExporter()
  { 
  }
  
  /**
   * Normal use: let base class manage.  Just store and return through following 2 methods.
   * return null first time if letting base class handle, else return map generated here.
   */
  public Map<String,String> getStaticTransformationParameters()
  {
    return parameters;
  }
  
  /**
   * Normal use: called by base class.  Save for subsequent returns through getStaticTransformationParameters().
   */
  public void setStaticTransformationParameters(Map<String,String> map)
  {
    parameters = map;
  }
    
  // Entry from menu item
  public void exportUserListToBrowser(String title)
  {
    showXml = false;
    
    exportToBrowser(title);      
  }
  
  public Document buildXmlDocument() throws Throwable
  {
    Document doc;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = factory.newDocumentBuilder();
      doc = parser.newDocument();
      doc.setXmlStandalone(true);
      // Can't get regex stuf in xsl 2.0 to work...
      // ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"UserList.xsl\"");
      // Use comment instead
      Comment comm = doc.createComment("<xml-stylesheet\", \"type=\"text/xsl\" href=\""+STYLESHEET_NAME+"\""); 
      Element root = doc.createElement("UserList");
      doc.appendChild(root);
      // doc.insertBefore(pi, root);
      doc.insertBefore(comm, root);

      // Skip schema for now (needs tweeking)
      // root.setAttribute("xmlns","http://edu.nps.moves.mmowgli.actionPlanList");
      // root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
      // root.setAttribute("xsi:schemaLocation", "http://edu.nps.moves.mmowgli.actionPlanList ActionPlanList.xsd");

      root.setAttribute("exported", dateFmt.format(new Date()));
      
      HSess.init();      
      Game g = Game.getTL();
      GameLinks links = GameLinks.getTL();
      String s = g.getTitle();
      addElementWithText(root, "GameTitle", s.replace(' ','_'));  // better file name handling
      addElementWithText(root, "GameAcronym", g.getAcronym());
      addElementWithText(root, "GameSecurity", g.isShowFouo()?"FOUO":"open");
      addElementWithText(root, "GameSummary", metaString);
      addElementWithText(root, "TroubleLink", links.getTroubleLink());
      addElementWithText(root, "TroubleEmail", links.getTroubleMailto());      
            
      newAddCall2Action(root, HSess.get(), g);
      doBadgeTypes(root,HSess.get());
      doAwardTypes(root,HSess.get());
      doAffiliationDefaults(root,HSess.get());
      
      @SuppressWarnings("unchecked")
      List<User> lis = HSess.get().createCriteria(User.class).addOrder(Order.asc("id")).list();
      HSess.close();
      
      int highestMove = g.getCurrentMove().getNumber();  //1-based  
      // Build a work array
      ArrayList<RankedUser> ruLis = new ArrayList<RankedUser>();
      for (User u : lis) {
        ruLis.add(new RankedUser(u,highestMove));
      }
        // Let's do some processing to rank by scores
      for(int move = 1; move<=highestMove; move++) {
        int move_zbased = move-1;
        
        // Combined score by move
        Collections.sort(ruLis, RankedUser.getCombinedMoveComparator(move));
        int i = 0;
        float last = Float.MAX_VALUE;
        float score;
        for(RankedUser ru : ruLis) {
          score = ru.u.getBasicScoreMoveX(move) + ru.u.getInnovationScoreMoveX(move);
          if(score < last) {
            ru.combinedRankByMove[move_zbased] = ++i; // 0-based
            last = score;
          }
          else
            ru.combinedRankByMove[move_zbased] = i;  // equal in rank to last
        }
        
        // Basic score by move
        Collections.sort(ruLis, RankedUser.getBasicMoveComparator(move));
        i = 0;
        last = Float.MAX_VALUE;
        for(RankedUser ru : ruLis) {
          score = ru.u.getBasicScoreMoveX(move);
          if(score < last) {
            ru.basicRankByMove[move_zbased] = ++i; // 0-based
            last = score;
          }
          else
            ru.basicRankByMove[move_zbased] = i;
        } 
        
        // Innov score by move
        Collections.sort(ruLis, RankedUser.getInnovMoveComparator(move));
        i = 1;
        last = Float.MAX_VALUE;
        for(RankedUser ru : ruLis) {
          score = ru.u.getInnovationScoreMoveX(move);
          if(score < last) {
            ru.innovRankByMove[move_zbased] = ++i; // 0-based    
            last = score;
          }
          else
            ru.innovRankByMove[move_zbased] = i;
        }
      }
      
      // Basic score across moves
      Collections.sort(ruLis, RankedUser.basicComparer);
      int i = 0;
      float last = Float.MAX_VALUE;
      for (RankedUser ru : ruLis) {
        float score = ru.u.getCombinedBasicScore();
        if (score < last) {
          ru.basicScoreRank = ++i;
          last = score;
        }
        else
          ru.basicScoreRank = i; // same
      }
      
      // Innovation score across moves
      Collections.sort(ruLis, RankedUser.innovComparer);
      i=0;
      last = Float.MAX_VALUE;
      for(RankedUser ru : ruLis) {
        float score = ru.u.getCombinedInnovScore();
        if(score < last) {
          ru.innovScoreRank = ++i;
          last = score;
        }
        else
          ru.innovScoreRank = i; // same
      }
      
      // Combined score across move
      Collections.sort(ruLis, RankedUser.combinedComparer);
      i = 0;
      last = Float.MAX_VALUE;
      for (RankedUser ru : ruLis) {
        float cScore = ru.u.getCombinedBasicScore() + ru.u.getCombinedInnovScore();
        if (cScore < last) {
          ru.combinedScoreRank = ++i;
          last = cScore;
        }
        else
          ru.combinedScoreRank = i; // same as last
      }
      
      // Used the combined order (last sort above) when writing out Users
           
      for (RankedUser ru : ruLis) {
        addUserToDocument(root, ru.u.getId(), ru);
      }
    }
    catch (Throwable t) {
      HSess.close(); // will not fail if already closed
      t.printStackTrace();
      throw t; // rethrow
    }

    return doc;
  }
  
  private void addUserToDocument(Element root, long userid, RankedUser ru)
  {
    HSess.init();
    User u = User.getTL(userid);

    Element uElem = createAppend(root,"User");
    uElem.setAttribute("id", ""+u.getId());
    uElem.setAttribute("gameName", u.getUserName());
    
    Date rd = u.getRegisterDate();
    uElem.setAttribute("registrationDate", (rd==null?"":dateFmt.format(rd)));
    
    Move mv = u.getRegisteredInMove();
    uElem.setAttribute("registeredInMove", (mv==null?"":""+mv.getNumber()));
    
    uElem.setAttribute("okEmail", Boolean.toString(u.isOkEmail()));
    uElem.setAttribute("okGameMessages", Boolean.toString(u.isOkGameMessages()));
    uElem.setAttribute("okSurvey", Boolean.toString(u.isOkSurvey()));
    uElem.setAttribute("isGameAdministrator", Boolean.toString(u.isAdministrator()));
    uElem.setAttribute("isGameMaster", Boolean.toString(u.isGameMaster()));
    uElem.setAttribute("isGameDesigner", Boolean.toString(u.isDesigner()));
    uElem.setAttribute("isAccountDisabled", Boolean.toString(u.isAccountDisabled()));
    
    // Todo ....got a media locator in base class, see how it's used in awards and badges
    // Want to do this:  but don't have an app instance; should redesign
    // ExternalResource extR = (ExternalResource)app.globs().mediaLocator().locate(u.getAvatar().getMedia());
    // Hack
    Avatar av = u.getAvatar();
    Media med = av.getMedia();
    String url = med.getUrl();
    uElem.setAttribute("iconUrl", "https://web.mmowgli.nps.edu/mmowMedia/images/avatars/"+url);
    
    addElementWithText(uElem,"Affiliation",toUtf8(nn(u.getAffiliation()).trim()));
    addElementWithText(uElem,"Location",toUtf8(nn(u.getLocation())));
    addElementWithText(uElem,"Expertise",toUtf8(nn(u.getExpertise())));
    
    doBadges(uElem,u);
    doAwards(uElem,u);
    
    Element scores = createAppend(uElem,"Scores");
    
    float cBas = u.getCombinedBasicScore();
    float cInn = u.getCombinedInnovScore();
    scores.setAttribute("combined", ""+(int)Math.round(cBas+cInn));
    scores.setAttribute("combinedRank", ""+ru.combinedScoreRank);
    scores.setAttribute("basic", ""+(int)Math.round(cBas));
    scores.setAttribute("basicRank",""+ru.basicScoreRank);
    scores.setAttribute("implementation",""+(int)Math.round(cInn));
    scores.setAttribute("implementationRank", ""+ru.innovScoreRank);
    
    Game g = Game.getTL();
    int highestMove = g.getCurrentMove().getNumber();
    
    @SuppressWarnings("unchecked")    
    List<Move> mvs = HSess.get().createCriteria(Move.class).addOrder(Order.asc("number")).list();
    for(Move m : mvs) {
      int num = m.getNumber();
      if(num > highestMove)
        continue;
      Element  ms = createAppend(scores,"MoveScores");
      ms.setAttribute("move",""+m.getNumber());
      ms.setAttribute("basic",""+(int)Math.round(u.getBasicScoreMoveX(num)));
      ms.setAttribute("basicRank", ""+ru.basicRankByMove[num-1]); // 0-based
      ms.setAttribute("implementation",""+(int)Math.round(u.getInnovationScoreMoveX(num)));
      ms.setAttribute("implementationRank",""+ru.innovRankByMove[num-1]);  // 0-based
    }
       
    Element apsAuthored = createAppend(uElem,"AuthoredPlans");
    Set<ActionPlan> aps = u.getActionPlansAuthored();
    // TODO sort numerically
    apsAuthored.setAttribute("count", ""+aps.size());
    for(ActionPlan ap : aps) {
      Element pln = createAppend(apsAuthored, "ActionPlanID");
      pln.setTextContent(""+ap.getId());
    }
    Element cardsPlayed = createAppend(uElem,"CardsPlayed");
    @SuppressWarnings("unchecked")
    List<Card> cds = HSess.get().createCriteria(Card.class).add(Restrictions.eq("author", u)).list();
    cardsPlayed.setAttribute("count", ""+cds.size());
    for(Card cd : cds) {
      Element c = createAppend(cardsPlayed, "CardID");
      c.setTextContent(""+cd.getId());
    } 
    HSess.close();
  }
  
  @SuppressWarnings("unchecked")
  private void doBadgeTypes(Element elem, Session sess)
  {
    Element bTypElem = createAppend(elem,"BadgeTypes");
    List<Badge> bgs = sess.createCriteria(Badge.class).addOrder(Order.asc("badge_pk")).list();

    for(Badge b : bgs) {
      Element bdgEl = createAppend(bTypElem, "BadgeType");
      bdgEl.setAttribute("type",""+b.getBadge_pk());
      bdgEl.setAttribute("name", b.getBadgeName());
      bdgEl.setAttribute("description", b.getDescription());
      
      Resource res = mediaLocator.locate(b.getMedia());
      String url="";
      if(res instanceof ExternalResource)
      	url = ((ExternalResource)res).getURL();
      bdgEl.setAttribute("iconUrl",url);
      
      //bdgEl.setAttribute("iconUrl", b.getMedia().getUrl());
    }
  }
  
  @SuppressWarnings("unchecked")
  private void doAffiliationDefaults(Element elem, Session sess)
  {
    Element adElem = createAppend(elem, "AffiliationDefaults");
    List<Affiliation> affs = sess.createCriteria(Affiliation.class).addOrder(Order.asc("id")).list();
    for(Affiliation a : affs) {
      if(a.getAffiliation().toLowerCase().contains("please select:"))
        continue;      
      Element aEl = createAppend(adElem, "Affiliation");
      aEl.setTextContent(a.getAffiliation());
    }
  }
  
  @SuppressWarnings("unchecked")
  private void doAwardTypes(Element elem, Session sess)
  {
    Element aTypElem = createAppend(elem,"AwardTypes");
    List<AwardType> ats = sess.createCriteria(AwardType.class).addOrder(Order.asc("id")).list();

    for(AwardType at : ats) {
      Element atEl = createAppend(aTypElem, "AwardType");
      atEl.setAttribute("type",""+at.getId());
      atEl.setAttribute("name", at.getName());
      atEl.setAttribute("description", at.getDescription());
      Resource res = mediaLocator.locate(at.getIcon55x55());
      String url="";
      if(res instanceof ExternalResource)
      	url = ((ExternalResource)res).getURL();
      atEl.setAttribute("iconUrl",url);
    }   
  }
  
  private void doBadges(Element elem, User u)
  {
    Element bdgs = createAppend(elem,"Badges");
    Set<Badge> bs = u.getBadges();
    for(Badge b : bs) {
      Element bdgEl = createAppend(bdgs, "Badge");
      bdgEl.setAttribute("type",""+b.getBadge_pk());
    }
  }
  
  private void doAwards(Element elem, User u)
  {
    Element awds = createAppend(elem,"Awards");
    Set<Award> aLis = u.getAwards();
    for(Award a : aLis) {
      Element awEl = createAppend(awds, "Award");
      awEl.setAttribute("type",  ""+a.getAwardType().getId());
      awEl.setAttribute("round", ""+a.getMove().getNumber());
      addElementWithText(awEl,"url",a.getStoryUrl());
    }  
  }
  
  @Override
  protected void showFile(String ignore, Document doc, String name, String styleSheetNameInThisPackage, boolean showXml) throws TransformerConfigurationException, TransformerException
  {
    super.showFile("Players", doc, name, styleSheetNameInThisPackage, showXml);
  }

  @Override
  protected void showFile(String ignore, Document doc, String name, String styleSheetNameInThisPackage, String cdataElementList, boolean showXml) throws TransformerConfigurationException, TransformerException
  {
    super.showFile("Players", doc, name, styleSheetNameInThisPackage, cdataElementList, showXml);
  }

  @Override
  public String getCdataSections()
  {
    return CDATA_ELEMENTS;
  }
  @Override
  public String getStyleSheetName()
  {
    return STYLESHEET_NAME;
  }
  @Override
  public String getFileNamePrefix()
  {
    return FILE_NAME;
  }
  @Override
  public String getThreadName()
  {
    return THREAD_NAME;
  }

  class RankStruct
  {
    public int combinedRank;
    public int basicRank;
    public int innovRank;
    public int[] combinedRankByMove;
    public int[] basicRankByMove;
    public int[] innovRankByMove;
  }
 }
