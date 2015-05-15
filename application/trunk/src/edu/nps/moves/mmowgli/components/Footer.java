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

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.GameLinks;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.db.Media.Source;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.messaging.WantsGameUpdates;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 * 
 * @version $Id$
 * @since $Date$
 * @copyright Copyright (C) 2011
 */
public class Footer extends AbsoluteLayout implements MmowgliComponent, WantsGameUpdates
{
  private static final long serialVersionUID = -5343489408165893669L;

  private Link aboutButt, faqsButt, glossaryButt, creditsButt, 
               troubleButt, termsButt, fixesButt, twitterButt, reportsButt, videosButt;
  private Link fouoLink;
  

  //@formatter:off
  @HibernateSessionThreadLocalConstructor
  public Footer()
  {
    GameLinks gl = GameLinks.getTL();
    aboutButt    = makeLink("About",               gl.getAboutLink(), "About MMOWGLI project");
    creditsButt  = makeLink("Credits and Contact", gl.getCreditsLink(), "Who we are");
    faqsButt     = makeLink("FAQs",                gl.getFaqLink(), "Frequently answered questions");
    glossaryButt = makeLink("Glossary",            gl.getGlossaryLink(), "Terms and acronyms of interest");//"https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Glossary", "Terms and acronyms of interest");
    termsButt    = makeLink("Terms and Conditions",gl.getTermsLink(),    "Player agreements and terms of use");
    troubleButt  = makeLink("Trouble Report",      gl.getTroubleLink(),  "Tell us if you find a problem");
    fixesButt    = makeLink("Fixes",               gl.getFixesLink(),    "Common game fixes and workarounds"); //"https://portal.mmowgli.nps.edu/fixes", "Common game fixes and workarounds");
    twitterButt  = makeLink("Twitter",             getTwitterLink(),     "The Mmowgli twitter feed");
    reportsButt  = makeLink("Reports",             getReportsLink(),     "Game play reports page");
    //videosButt   = makeLink("Videos",              getVideosLinkTL(),    "Game videos");
    videosButt   = makeLink("Videos",              gl.getVideosLink(),   "Game videos");
  }
  //@formatter:on
  HtmlLabel pingPushLab;
  @Override
  public void initGui()
  {
    setWidth(FOOTER_W);
    setHeight("130px");  //room for fouo butt//FOOTER_H);
    AbsoluteLayout mainAbsLay = new AbsoluteLayout(); // offset it from master

    mainAbsLay.setWidth(FOOTER_W);
    mainAbsLay.setHeight(FOOTER_H);
    addComponent(mainAbsLay,FOOTER_OFFSET_POS);
    
    MediaLocator medLoc = ((Mmowgli2UI)UI.getCurrent()).getMediaLocator();
    Embedded back = new Embedded(null, medLoc.getFooterBackground());
    mainAbsLay.addComponent(back, "top:0px;left:0px");
     
    HorizontalLayout outerHorLay = new HorizontalLayout();
    addComponent(outerHorLay, "top:45px;left:0px");
    outerHorLay.setWidth(FOOTER_W);
    HorizontalLayout innerHorLay = new HorizontalLayout();
    innerHorLay.setSpacing(true);
    outerHorLay.addComponent(innerHorLay);
    outerHorLay.setComponentAlignment(innerHorLay, Alignment.MIDDLE_CENTER);

    Label sp;
    innerHorLay.addComponent(aboutButt);
    innerHorLay.addComponent(pingPushLab=new HtmlLabel()); pingPushLab.setWidth("7px");
    innerHorLay.addComponent(creditsButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");
    innerHorLay.addComponent(faqsButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");
    innerHorLay.addComponent(fixesButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");    
    innerHorLay.addComponent(glossaryButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");    
    innerHorLay.addComponent(reportsButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");   
    innerHorLay.addComponent(termsButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");
    innerHorLay.addComponent(troubleButt);
    troubleButt.addStyleName("m-red-text");
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");
    innerHorLay.addComponent(twitterButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");
    innerHorLay.addComponent(videosButt);
   
    GameLinks gl = GameLinks.getTL();
    if(gl.getFixesLink().toLowerCase().contains("armyscitech") || gl.getGlossaryLink().toLowerCase().contains("armyscitech")) {
      ;  // This is a hack, but I don't want to pollute the db with a bogus boolean...this is a special case just for these folks.
    }
    else {
      HorizontalLayout hl = new HorizontalLayout();
      Label lab=null;
      hl.addComponent(lab=new HtmlLabel("Build "+MMOWGLI_BUILD_ID)); lab.addStyleName("m-footer-servername");  //small text
      hl.addComponent(lab=new HtmlLabel("&nbsp;&nbsp;Vaadin "+VAADIN_BUILD_VERSION));lab.addStyleName("m-footer-servername");  //small text
      hl.addComponent(lab=new HtmlLabel("&nbsp;&nbsp;"+AppMaster.instance().getServerName()));lab.addStyleName("m-footer-servername");  //small text
      hl.setSizeUndefined();
      mainAbsLay.addComponent(hl,"bottom:3px;right:15px;");   
    }

    fouoLink = Footer.buildFouoNoticeTL();
    addComponent(fouoLink,"top:92px;left:365px");
    fouoLink.setVisible(Game.getTL().isShowFouo());    
  }
    
  private Link makeLink(String text, String url, String tooltip)
  {
    Link l = new Link(text,new ExternalResource(url));
    l.setTargetName(PORTALTARGETWINDOWNAME);
    l.setTargetBorder(BorderStyle.DEFAULT);
    l.setDescription(tooltip+" (opens in new window or tab)");
    return l;
  }
  
  // Can be deleted
  public void showHideFouoButton(boolean show)
  {
    fouoLink.setVisible(show);
  }
  
  private String getReportsLink()
  {
    return AppMaster.instance().getReportsUrl();
  }
  
  private String getTwitterLink()
  {
    return "http://twitter.com/MMOWGLI";
  }
/*   old
  private String getVideosLinkTL()
  {
    return Mmowgli2UI.getGlobals().getAlternateVideoUrlTL();
  }
*/  
  @Override
  public boolean gameUpdatedExternallyTL(Object nullObj)
  {
    Game g = Game.getTL();
    boolean isVisible = fouoLink.isVisible();
    fouoLink.setVisible(g.isShowFouo());
    return isVisible != g.isShowFouo();
  }
  
  public static Link buildFouoNoticeTL()
  {
    GameLinks gl = GameLinks.getTL();
    MediaLocator mediaLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    Link fouoLink = new Link(null,new ExternalResource(gl.getFouoLink()));
    Resource icon = mediaLoc.locate(new Media("fouo250w36h.png", // todo, database-ize
                                  "", "",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
    fouoLink.setIcon(icon);
    fouoLink.setDescription(Game.getTL().getFouoDescription());
    fouoLink.setTargetName(PORTALTARGETWINDOWNAME);
    fouoLink.setTargetBorder(BorderStyle.DEFAULT);
    return fouoLink;
  }

  int pp = 0;
	public void pingPush()
	{
		pp++;
		if(pp%2 == 0)
			pingPushLab.setValue("&nbsp;");
		else
			pingPushLab.setValue("");		
	}
}
