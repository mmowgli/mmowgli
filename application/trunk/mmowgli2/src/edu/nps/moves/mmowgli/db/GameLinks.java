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

import javax.persistence.*;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.hibernate.DB;

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
public class GameLinks implements Serializable
{
  private static final long serialVersionUID = -5376587367981087375L;

  long id; // Primary key

  String aboutLink;
  String actionPlanRequestLink;
  String blogLink;
  String creditsLink;
  String faqLink;
  String fixesLink;
  String fouoLink;
  String gameFromEmail;
  String gameFullLink;
  String gameHomeUrl;
  String glossaryLink;
  String howToPlayLink;
  String improveScoreLink;
  String informedConsentLink;
  String learnMoreLink;
  String mmowgliMapLink;
  String surveyConsentLink;
  String termsLink;
  String thanksForInterestLink;
  String thanksForPlayingLink;
  String troubleLink;
  String troubleMailto;
  String userAgreementLink;
  String videosLink;

  public static GameLinks get(Session sess)
  {
    return get(sess, 1L); // only one entry in current design
  }

  private static GameLinks get(Session sess, Serializable id)
  {
    return (GameLinks) sess.get(GameLinks.class, id);
  }
 
  public static GameLinks getTL()
  {
    return getTL(1L);
  }
  
  public static GameLinks getTL(Object id)
  {
    return DB.getTL(GameLinks.class, id);
  }

  public static void updateTL(GameLinks gl)
  {
    DB.updateTL(gl);
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
  public String getGameHomeUrl()
  {
    return gameHomeUrl;
  }

  public void setGameHomeUrl(String s)
  {
    this.gameHomeUrl = s;
  }

  @Basic
  public String getLearnMoreLink()
  {
    return learnMoreLink;
  }

  public void setLearnMoreLink(String learnMoreLink)
  {
    this.learnMoreLink = learnMoreLink;
  }

  @Basic
  public String getCreditsLink()
  {
    return creditsLink;
  }

  public void setCreditsLink(String creditsLink)
  {
    this.creditsLink = creditsLink;
  }

  @Basic
  public String getAboutLink()
  {
    return aboutLink;
  }

  public void setAboutLink(String aboutLink)
  {
    this.aboutLink = aboutLink;
  }

  @Basic
  public String getTermsLink()
  {
    return termsLink;
  }

  public void setTermsLink(String termsLink)
  {
    this.termsLink = termsLink;
  }

  @Basic
  public String getFaqLink()
  {
    return faqLink;
  }

  public void setFaqLink(String faqLink)
  {
    this.faqLink = faqLink;
  }

  @Basic
  public String getTroubleLink()
  {
    return troubleLink;
  }

  public void setTroubleLink(String troubleLink)
  {
    this.troubleLink = troubleLink;
  }

  @Basic
  public String getBlogLink()
  {
    return blogLink;
  }

  public void setBlogLink(String blogLink)
  {
    this.blogLink = blogLink;
  }

  @Basic
  public String getImproveScoreLink()
  {
    return improveScoreLink;
  }

  public void setImproveScoreLink(String improvesScoreLink)
  {
    this.improveScoreLink = improvesScoreLink;
  }

  @Basic
  public String getActionPlanRequestLink()
  {
    return actionPlanRequestLink;
  }

  public void setActionPlanRequestLink(String actionPlanRequestLink)
  {
    this.actionPlanRequestLink = actionPlanRequestLink;
  }

  @Basic
  public String getThanksForPlayingLink()
  {
    return thanksForPlayingLink;
  }

  public void setThanksForPlayingLink(String thanksForPlayingLink)
  {
    this.thanksForPlayingLink = thanksForPlayingLink;
  }

  @Basic
  public String getGameFullLink()
  {
    return gameFullLink;
  }

  public void setGameFullLink(String gameFullLink)
  {
    this.gameFullLink = gameFullLink;
  }

  @Basic
  public String getThanksForInterestLink()
  {
    return thanksForInterestLink;
  }

  public void setThanksForInterestLink(String thanksForInterestLink)
  {
    this.thanksForInterestLink = thanksForInterestLink;
  }

  @Basic
  public String getMmowgliMapLink()
  {
    return mmowgliMapLink;
  }

  public void setMmowgliMapLink(String mmowgliMapLink)
  {
    this.mmowgliMapLink = mmowgliMapLink;
  }

  @Basic
  public String getInformedConsentLink()
  {
    return informedConsentLink;
  }

  public void setInformedConsentLink(String informedConsentLink)
  {
    this.informedConsentLink = informedConsentLink;
  }

  @Basic
  public String getUserAgreementLink()
  {
    return userAgreementLink;
  }

  public void setUserAgreementLink(String userAgreementLink)
  {
    this.userAgreementLink = userAgreementLink;
  }

  @Basic
  public String getSurveyConsentLink()
  {
    return surveyConsentLink;
  }

  public void setSurveyConsentLink(String surveyConsentLink)
  {
    this.surveyConsentLink = surveyConsentLink;
  }

  @Basic
  public String getFouoLink()
  {
    return fouoLink;
  }

  public void setFouoLink(String fouoLink)
  {
    this.fouoLink = fouoLink;
  }

  @Basic
  public String getFixesLink()
  {
    return fixesLink;
  }

  public void setFixesLink(String s)
  {
    fixesLink = s;
  }

  @Basic
  public String getGlossaryLink()
  {
    return glossaryLink;
  }

  public void setGlossaryLink(String s)
  {
    glossaryLink = s;
  }

  @Basic
  public String getGameFromEmail()
  {
    return gameFromEmail;
  }

  public void setGameFromEmail(String s)
  {
    gameFromEmail = s;
  }

  @Basic
  public String getTroubleMailto()
  {
    return troubleMailto;
  }

  public void setTroubleMailto(String s)
  {
    troubleMailto = s;
  }

  @Basic
  public String getHowToPlayLink()
  {
    return howToPlayLink;
  }

  public void setHowToPlayLink(String s)
  {
    howToPlayLink = s;
  }
  
  @Basic
  public String getVideosLink()
  {
    return videosLink;
  }
  
  public void setVideosLink(String s)
  {
    videosLink = s;
  }
}
