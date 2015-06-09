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

package edu.nps.moves.mmowgli.utility;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.db.Media.Source;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.imageServer.ImageServlet;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;

/**
 * MediaLocator.java Created on Dec 22, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * Given a Media object from the database, return a vaadin resource instance
 * from which the media can be retrieved;
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MediaLocator implements Serializable
{
  private static final long serialVersionUID = -3729767364940957084L;
  
  private    URL gameImagesUrl;
  private String gameImagesUrlStr;
   
  public static int HTTP = 80;
  public static int HTTPS = 443;
  
	public MediaLocator()
	{		
		gameImagesUrlStr = AppMaster.instance().getGameImagesUrlString();
		
    if(!gameImagesUrlStr.endsWith("/"))
      gameImagesUrlStr = gameImagesUrlStr+"/";
	  
		gameImagesUrl = buildGameImagesUrl(AppMaster.instance().getAppUrl(), gameImagesUrlStr);
	}
	
//@formatter:off
	private URL buildGameImagesUrl(URL appURL, String urlString)
	{	  
		try {
	    if(urlString.contains(":"))
	      return new URL(urlString);

			return new URL(appURL.getProtocol(),
					           appURL.getHost(),
			               "/");
		}
		catch (MalformedURLException ex) {
			System.err.println("Program error in MediaLocatior");
			return appURL;
		}
	}
//@formatter:on

	private URL getUserImagesUrl()
	{
	  return AppMaster.instance().getUserImagesUrl();
	}	

	public Resource locate(Media m)
	{
	  return locate(m,null);
	}
	
	String mangleUrl(String s, int sz)
	{
	  int lstDot = s.lastIndexOf('.');
	  if(lstDot <0)
	    return s;
	  return s.substring(0, lstDot) + sz + s.substring(lstDot);
	}
	
	public Resource locate(Media m, Integer sz)
	{
    String url = m.getUrl();
    
	  if(sz != null) {
	    url=mangleUrl(url,sz);
	  }

		switch(m.getSource())
		{
		case USER_UPLOADS_REPOSITORY:  // used to be CLASSPATH
	     try {
	        if(url.indexOf(':') == -1)
	          return new ExternalResource(new URL(getUserImagesUrl(),url));
	        return new ExternalResource(new URL(url));
	      }
	      catch(MalformedURLException ex) {
	        System.err.println(ex.getClass().getSimpleName()+" "+ex.getLocalizedMessage()+" in MediaLocator.locate()");
	        return new ExternalResource(gameImagesUrl); // anything
	      }

		case GAME_IMAGES_REPOSITORY:		  
			String path = url;
			if(m.getType() == MediaType.AVATARIMAGE)
				path = "avatars/"+path;
			else if(m.getType() == MediaType.VIDEO)
			  path = "mov/"+path;			
			try {
			  if(path.indexOf(':') == -1)
			    return new ExternalResource(new URL(gameImagesUrl,path));
			  return new ExternalResource(new URL(path));
			}
			catch(MalformedURLException ex) {
				System.err.println(ex.getClass().getSimpleName()+" "+ex.getLocalizedMessage()+" in MediaLocator.locate()");
				return new ExternalResource(gameImagesUrl); // anything
			}
			
		case WEB_FULL_URL:
		  try {
        return new ExternalResource(new URL(url));
      }
      catch (MalformedURLException e) {
        System.err.println(e.getClass().getSimpleName()+" "+e.getLocalizedMessage()+" in MediaLocator.locate()");
        return new ExternalResource(gameImagesUrl); // anything
      }
		  
		case FILESYSTEM_PATH:
			return new FileResource(new File(url));
			
		case DATABASE:
      try {
        return new ExternalResource(new URL(ImageServlet.getBaseImageUrl(),url));
      }
		  catch(MalformedURLException e ) {
        System.err.println("Bad URL, type DATABASE, in "+e.getClass().getSimpleName()+" "+e.getLocalizedMessage()+" in MediaLocator.locate()");
        return new ExternalResource(gameImagesUrl); // anything	    
		  }

		default:
			System.err.println("Program error in MediaLocator.locate");
			return null;
		}
	}
	
	public Resource locateUserImage(String url)
	{
	  try {
      return new ExternalResource(new URL(getUserImagesUrl(),url));
    }
    catch (MalformedURLException e) {
      System.err.println("Program error in MediaLocator.locateUserImage(): "+e.getLocalizedMessage());
      return null;
    }	  
	}

	public ExternalResource locateAvatar(Media m)
	{
		Resource res = locate(m);
		if(! (res instanceof ExternalResource) )
			throw new RuntimeException("Avatar images must be externalresources");
		
		return (ExternalResource)res;
	}

	public Resource getCardSummaryBackground(Object cardId, Session sess)
	{
	  Card c = Card.get(cardId, sess);
	  return locate(new Media(CardStyler.getCardSummaryImage(c.getCardType()),"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
	}
	
  public Resource getCardSummaryBackgroundMultiple(Object cardId, Session sess)
	{
	  Card c = Card.get(cardId,sess);
	  CardType ct = c.getCardType();
	  return locate(new Media(CardStyler.getCardSummaryMultipleImage(ct),"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
	}
  
  public Resource getCardSummaryBackgroundSmallTL(Object cardId)
  {
    Card c = Card.getTL(cardId);
    CardType ct = c.getCardType();
    
    return locate(new Media(CardStyler.getCardParentImage(ct),"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getCardSummaryGreyStar()
  {
    return locate(new Media("cardSummaryStarGray.png","cardSummStarGray","disabled favorite star image",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getCardSummaryGoldStar()
  {
    return locate(new Media("cardSummaryStarGold.png","cardSummStarGold","enabled favorite star image",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));

  }
/*
	public String getCardChainPopupColorSelector(Card c)
	{
		// todo, switch on card type
		return "m-card-chain-node-selected";
	}
*/
  public Resource getCardSummaryListHeaderBackground(CardType ct)
  {
    return locate(new Media(CardStyler.getCardHeaderImage(ct),"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getCardSummaryDrawerBackground(CardType ct)
  {
    return locate(new Media("cardDrawer.png","card summary drawer", "background of card summary drawer",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getCardLargeBackgroundTL(Object cardId)
  {
    Card c = Card.getTL(cardId);
    return locate(new Media(CardStyler.getCardBigImage(c.getCardType()),"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getCardLargeGreyStar(Object cardId)
  {
    return locate(new Media("cardBigStarGrey.png","cardBigStarGrey","disabled favorite star image",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getCardLargeGoldStar(Object cardId)
  {
    return locate(new Media("cardBigStarGold.png","cardBigStarGold","enabled favorite star image",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
/*
  public Resource getCardChainPopupIcon()
  {
    return locate(new Media("viewCardChainSwoosh.png","cardChainIcon","mini card chain pict.",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getCallToActionTheSituation()
  {
    return locate(new Media("theSituation.png","theSituationIcon","\"The Situation\" in script",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
*/
  public Resource getCallToActionBang()
  {
    return locate(new Media("callToActionBang290w31h.png","callToActionInScriptIcon","\"Call To Action!\" in script",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

/*
  public Resource getCallToActionWeNeedYourHelp()
  {
    return locate(new Media("weNeedYourHelp.png","weNeedYourHelpIcon","\"We need your help\" in script",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getCallToActionVideoBackground()
  {
    return locate(new Media("callToActionBackground.png","callToActionBackground","call-to-action top background",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
*/
  public Resource getFooterBackground()
  {
    return locate(new Media("footer.png","footerbackground","footer background",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getImage(String fn)
  {
    return locate(new Media(fn,"headerbackground","header background",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getHeaderBackground()
  {
    //return locate(new Media("headerNew992w172h.png","headerbackground","header background",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
    //return locate(new Media("headerNoBanner992w172h.png","headerbackground","header background",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
    return locate(new Media("headerNoBannerNoScore992w172h.png","headerbackground","header background",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getHeaderBanner(Game g)
  {
    String bannerFileName = g.getHeaderBannerImage();
    return locate(new Media(bannerFileName,"headerbanner","header banner",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getCheckMark12px()
  {
    return locate(new Media("12px-Black_check.svg.png","black check mark","black check mark",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));   
  }
 /* 
  public Resource getActionPlanBackground()
  {
    return locate(new Media("actionPlanTemplate985w1093h.png","actplanbackground","action plan background",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
*/  
  public Resource getIdeaDashboardBackground()
  {
    return locate(new Media("ideaDashboardBackground.png","ideadashbackground","idea dashboard background",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getActionDashboardPlanBackground()
  {
    return locate(new Media("actionDashboardBackgroundNew980x833.png","actdashbackground","action dashboard background",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  //return locate(new Media("actionDashboardBackground.png",          "actdashbackground","action dashboard background",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
/*
  public Resource getGoToIdeaDashboardIcon()
  {
    return locate(new Media("goToIdeaDashboard.png","goToIdeaDashboardbackground","go to idea dashboard background",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  
  public Resource getViewCardChainSwoosh()
  {
    return locate(new Media("viewCardChainSwoosh.png","viewCardChainSwooshImage","view Card Chain Swoosh",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getAddAuthorTabImage()
  {
    return locate(new Media("addAuthButt.png","addauthorbuttimg","add author tab button image",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getPowerPlayTabImage()
  {
    return locate(new Media("powPlayButt.png","dopowerplaybuttimg","do power play tab button image",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getActionDashboardPowerPlayTabBlue()
  {
    return locate(new Media("powerPlaysTabBlue.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getActionDashboardPlansInPlayTabBlue()
  {
    return locate(new Media("plansInPlayTabBlue.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getActionPlanTalkTabBlue()
  {
    return locate(new Media("talkItOverTabBlue.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getActionPlanImagesTabBlue()
  {
    return locate(new Media("imagesTabBlue.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getActionPlanVideosTabBlue()
  {
    return locate(new Media("videosTabBlue.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getActionPlanMapTabBlue()
  {
    return locate(new Media("mapTabBlue.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
*/
  public Resource getActionPlanAddImageButt()
  {
    return locate(new Media("addYourImageOrange.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getActionPlanAddVideoButt()
  {
    return locate(new Media("addYourVideoOrange189w50h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
/*  
  public Resource getActionPlanSearchLibButt()
  {
    return locate(new Media("searchImageLibraryBlack.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getActionPlanSearchVidLibButt()
  {
    return locate(new Media("searchVideoLibraryBlack188w50h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
*/  
  public Resource getActionPlanZoomButt()
  {
    return locate(new Media("zoomIcon.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
/*  
  public Resource getActionPlanReplaceImageButt()
  {
    return locate(new Media("replaceGreen.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
 
  public Resource getActionPlanDeleteImageButt()
  {
    return locate(new Media("deleteBlack.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getIdeaDashboardBestTabBlue()
  {
    return locate(new Media("ideaDashboardBestTab.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getIdeaDashboardWorstTabBlue()
  {
    return locate(new Media("ideaDashboardWorstTab.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getIdeaDashboardSuperActiveTabBlue()
  {
    return locate(new Media("ideaDashboardActiveTab.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getDialogBackground()
  {
    return locate(new Media("lightboxDialogBckgrnd.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public void decorateDialogCancelButton(Button butt)
  {
    butt.setIcon(locate(new Media("lightboxDialogCancelButton.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    butt.addStyleName("borderless");
  }
*/  
  public void decorateDialogContinueButton(Button butt)
  {
    butt.setIcon(locate(new Media("continue.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    butt.addStyleName("borderless"); 
  }
/*
  public void decorateDialogRejectButton(NativeButton rejectButt)
  {
    rejectButt.setIcon(locate(new Media("rejectNoThanks244w76h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    rejectButt.addStyleName("borderless");  
  }

  public void decorateDialogAcceptAndContinueButton(NativeButton continueButt)
  {
    continueButt.setIcon(locate(new Media("acceptAndContinue244w76h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    continueButt.addStyleName("borderless");  
  }

  public Resource getDialogHeaderBackground()
  {
    return locate(new Media("lightboxDialogHeaderBckgrnd.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
*/
  public Resource getDialogFooterBackground()
  {
    return locate(new Media("lightboxDialogFooterBckgrnd.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Media getHowToPlayCardsVideoMediaTL()
  {
    return (Media)HSess.get().createCriteria(Media.class).
            add(Restrictions.eq("handle", "HowToPlayCardsVideo")).list().get(0);
  }
  
  public Media getHowToWinActionMediaTL()
  {
    return (Media)HSess.get().createCriteria(Media.class).
            add(Restrictions.eq("handle", "HowToWinActionVideo")).list().get(0);
  }

  public Resource getHowToWinActionVideoTL()
  {
    return locate(getHowToWinActionMediaTL());
  }

  public Resource getCardDot(CardType ct)
  {
    return locate(new Media(CardStyler.getCardDot(ct),"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getCardDotFromStyleName(String name)
  {
    return locate(new Media(CardStyler.getCardDot(name),"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getCardLargeBackgroundFromStyleName(String name)
  {
    return locate(new Media(CardStyler.getCardBigImage(name),"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getCardSummaryBackgroundFromStyleName(String name)
  {
    return locate(new Media(CardStyler.getCardSummaryImage(name),"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getCardSummaryBackgroundMultipleFromStyleName(String name)
  {
    return locate(new Media(CardStyler.getCardSummaryMultipleImage(name),"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getCardHeaderImageFromStyleName(String name)
  {
    return locate(new Media(CardStyler.getCardHeaderImage(name),"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));    
  } 

  public Resource getCardParentImageFromStyleName(String name)
  {
    return locate(new Media(CardStyler.getCardParentImage(name),"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
/* 
  public Resource getUserProfileActionPlansTabBlue()
  {
    return locate(new Media("userProfileActPlanTab158x46h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getUserProfileMyIdeasTab()
  {
    return locate(new Media("userProfileMyIdeasTab173w51h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getUserProfileMyIdeasTab_grey()
  {
    return locate(new Media("userProfileMyIdeasTab173w51h_grey.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getUserProfileInvestmentsTabBlue()
  {
    return locate(new Media("userProfileInvestTab139w46h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getUserProfileBuddiesTabBlue()
  {
    return locate(new Media("userProfileBuddiesTab124w46h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public void decorateResetButton(Button butt)
  {
    butt.setIcon(locate(new Media("resetButton54w17h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    butt.addStyleName("borderless");   
  }
  
  public void decorateSaveChangesButton(Button butt)
  {
    butt.setIcon(locate(new Media("saveChangesButton119w24h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    butt.addStyleName("borderless");      
  }
 */ 
  public void decorateImageButton(Button butt, String imageName)
  {
    butt.setIcon(locate(new Media(imageName,"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    butt.addStyleName("borderless");   
  }
  /*
  public void decorateImNewToMmowgliButton(Button butt)
  {
    butt.setIcon(locate(new Media("imNewButton202w22h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    butt.addStyleName("borderless");
  }
*/
//  public void decorateImRegisteredButton(Button butt)
//  {
//    butt.setIcon(locate(new Media("imaplayer144w24h.png"/*"imRegisteredButton133w24h.png"*/,"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
//    butt.addStyleName("borderless");
//  }
/*
  public Resource getIllDoThisLaterButton()
  {
    return locate(new Media("illDoThisLaterButton160w18h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
*/
  public void decorateGetABriefingButton(Button butt)
  {
    butt.setIcon(locate(new Media("getABriefing129w24h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    butt.addStyleName("borderless");       
  }
/*
  public void decorateChangePasswordButton(Button butt)
  {
    butt.setIcon(locate(new Media("changePassword155w25h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    butt.addStyleName("borderless");          
  }
*/  
  public void decorateCancelButton(Button butt)
  {
    butt.setIcon(getCancelButtonIcon());
    butt.addStyleName("borderless");    
  }
  
  public Resource getCancelButtonIcon()
  {
    return locate(new Media("cancel61w15h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getEmptyBadgeImage()
  {
    return locate(new Media("badge_empty_55w55h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getSubmitButtonIcon()
  {
    return locate(new Media("submitButton61w17h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  /*
  public Resource getDiscardMyChangesButtonIcon()
  {
    return locate(new Media("discardMyChanges181w26h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));    
  }
*/
  
  public void decorateSelectButton(Button butt)
  {
    butt.setIcon(getSelectButtonIcon());
    butt.addStyleName("borderless");
  }
  
  public Resource getOkButtonIcon()
  {
    return locate(new Media("okButt23w18h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public Resource getSelectButtonIcon()
  {
    return locate(new Media("save39w13h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  
  public void decorateOkButton(Button butt)
  {
    butt.setIcon(getOkButtonIcon());
    butt.addStyleName("borderless");    
  }
/*  
  public void decorateSaveButton(Button butt)
  {
    decorateSelectButton(butt);
  }
*/  
  public Resource getSaveButtonIcon()
  {
    return getSelectButtonIcon();
  }
/*
  public Resource getDialogRegenButton()
  {
    return locate(new Media("regenerate98w24h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Resource getDialogCloseButtonIcon()
  {
    return locate(new Media("closeButton85w42h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
*/
  public void decoratePlayIdeaButton(Button butt, Game g)
  {
    butt.setIcon(getPlayIdeaButt(g));//locate(new Media("playIdeaButt124w18h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    butt.addStyleName("borderless");
  }
  
  public Resource getPlayIdeaButt(Game g)
  {
    String img = g.getPlayIdeaButtonImage();
    if(img == null || img.length()<=0)
      return null;
   // return locate(new Media("playIdeaButt124w18h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
    return locate(new Media(img,"playideabuttonimage","play idea button image",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }
  /*
  public void decorateTakeActionButton(Button butt)
  {
    butt.setIcon(locate(new Media("takeActionButt119w18h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    butt.addStyleName("borderless");
  }

  public Resource getTakeActionButt()
  {
    return locate(new Media("takeActionButt119w18h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public void decorateSignOutButton(Button butt)
  {
    butt.setIcon(getSignOutButt());
    butt.addStyleName("borderless");   
  }

  public Resource getSignOutButt()
  {
    return locate(new Media("signOutButt72w26h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));    
  }
  */
  public Component getLeaderboardTitle()
  {
    Embedded emb = new Embedded(null,locate(new Media("LeaderboardTitle246w28h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    emb.setWidth("246px");
    emb.setHeight("28px");
    return emb;
  }
  
  public Component getUserProfileTitle()
  {
    Embedded emb = new Embedded(null,locate(new Media("playerProfile269w28h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    emb.setWidth("269px");
    emb.setHeight("28px");
    return emb;  
  }
  
  public Component getActionDashboardTitle()
  {
    Embedded emb = new Embedded(null,locate(new Media("ActionPlanDashboardTitle458w28h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    emb.setWidth("458px");
    emb.setHeight("28px");
    return emb;
  }

  public Component getIdeaDashboardTitle()
  {
    Embedded emb = new Embedded(null,locate(new Media("IdeaDashboardTitle304w28h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    emb.setWidth("304px");
    emb.setHeight("28px");
    return emb;
  }

  public Embedded getGreyActionPlanThumb()
  {
    Embedded emb = new Embedded(null,locate(new Media("greyThumb21w29h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    emb.setWidth("21px");
    emb.setHeight("29px");
    return emb;
  }
  
  public Embedded getBlackActionPlanThumb()
  {
    Embedded emb = new Embedded(null,locate(new Media("blackThumb21w29h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    emb.setWidth("21px");
    emb.setHeight("29px");
    return emb;
  }
/*
  public String getBackgroundImageLinkTL()
  {
    return imageGetCommon(Game.getTL().getBackgroundImageLink());
  }

  public String getSignupHeaderImageLinkTL()
  {
    return imageGetCommon(Game.getTL().getCurrentMove().getCurrentMovePhase().getSignupHeaderImage());
  }

  public String imageGetCommon(String url)
  {
    if(url == null)
      return url;
    
    if(url.toLowerCase().startsWith("http"))
      return url;

    // else find it in the repository
    Resource res = locate(new Media(url,"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
    if(res instanceof ExternalResource)
      return ((ExternalResource)res).getURL();
    
    // else
    return new Media(url,"","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY).getUrl();   // not vetted 
  }
  */

  public Embedded getDialog2CornerResource()
  {
    Embedded emb = new Embedded(null,locate(new Media("dialog2Corner28w36h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    emb.setWidth("28px");
    emb.setHeight("36px");
    return emb;
  }

  public Resource getEmpty353w135h() //
  {
    return locate(new Media("empty353w135h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
  }

  public Embedded getTellMeMore130w15h()
  {
    Embedded emb = new Embedded(null,locate(new Media("tellMeMore130w15h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    emb.setWidth("130px");
    emb.setHeight("15px");
    return emb;
  }

  public Embedded getImNewButton202w22h()
  {
    Embedded emb = new Embedded(null,locate(new Media("imNewButton202w22h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    emb.setWidth("202px");
    emb.setHeight("22px");
    return emb;
  }

  public Embedded getImRegisteredButton133w24h()
  {
    Embedded emb = new Embedded(null,locate(new Media("imRegisteredButton133w24h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    emb.setWidth("133px");
    emb.setHeight("24px");
    return emb;
  }

  public Embedded getGuestLogin97w24h()
  {
    Embedded emb = new Embedded(null,locate(new Media("guestLogin97w24h.png","","",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY)));
    emb.setWidth("97px");
    emb.setHeight("24px");
    return emb;
  }
}
