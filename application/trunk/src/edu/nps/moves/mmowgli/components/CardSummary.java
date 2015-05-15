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

import static edu.nps.moves.mmowgli.MmowgliEvent.CARDAUTHORCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.CARDCLICK;

import java.text.SimpleDateFormat;
import java.util.Set;

import org.hibernate.Session;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.AppEvent;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.Move;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;
/**
 * CardSummary.java
 * Created on Jan 24, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardSummary extends AbsoluteLayout implements MmowgliComponent//, Comparable<CardSummary>
{
  private static final long serialVersionUID = 1683092723635952535L;
  
  public static CardSummary newCardSummary(Object cardId, Session sess, User me)
  {
    return newCardSummary(cardId,sess,me,false);
  }

  public static CardSummary newCardSummary(Object cardId, Session sess, User me, boolean mockupOnly)
  {
    return newCardSummary(cardId, sess, false, me, mockupOnly);
  }
  
  public static CardSummary newCardSummary_oobTL(Object cardId)
  {
    return newCardSummary(cardId,HSess.get(),false,null,false);
  }
  
  public static CardSummary newCardSummary(Object cardId, Session sess, boolean isFactCard, User me, boolean mockupOnly)
  { 
    Card c = Card.get(cardId, sess);
    CardSummary summ = new CardSummary(cardId, isFactCard, sess, mockupOnly);
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    if(c.getFollowOns().size() <= 0)
      summ.bckgrndResource = globs.mediaLocator().getCardSummaryBackground(cardId,sess);
    else
      summ.bckgrndResource = globs.mediaLocator().getCardSummaryBackgroundMultiple(cardId, sess);
    summ.starGreyResource = globs.mediaLocator().getCardSummaryGreyStar();
    summ.starRedResource = globs.mediaLocator().getCardSummaryGoldStar();    
    return summ;
  }
  
  public static CardSummary newCardSummarySmallTL(Object cardId)
  {
    return newCardSummarySmallTL(cardId, null);
  }
  
  public static CardSummary newCardSummarySmallTL(Object cardId, Session sess)
  {
    if(sess == null)
      sess = HSess.get();
    
    CardSummary summ = new CardSummary(cardId,false,sess);
    summ.widthStr           ="145px";//"144px";
    summ.heightStr          ="95px"; //"80px";
    summ.textPrefix         ="<font size='-10'>";  // not used
    summ.textSuffix         ="</font>";
    summ.contentPositionStr = "top:27px;left:12px";
    summ.contentWidthStr    = "123px";
    summ.contentHeightStr   = "40px";
    summ.contentStyle       = "m-cardsummary-content-small";
    summ.userDatePositionStr= "top:72px;left:12px";
    summ.userDateWidthStr   = "120px";
    summ.userWidthStr       = "55px";
    summ.userHeightStr      = "10px";
    summ.userStyle          = "m-cardsummary-user-small";
    summ.bckgrndResource    = Mmowgli2UI.getGlobals().mediaLocator().getCardSummaryBackgroundSmallTL(cardId);
    summ.headerStyleStr     = "m-cardsummary-header-small";
    summ.headerWidthStr     = "100px";
    summ.headerHeightStr    = "10px";
    
    summ.movePositionStr    = "top:9px;left:20px"; //"top:19px;left:10px";
    summ.moveWidthStr       = "95px";//"100px";
    summ.moveHeightStr      = "8px";
    summ.moveStyleStr       = "m-cardsummary-movelabel-small";  
    
    summ.starGreyResource   = null;
    summ.starRedResource    = null;
    summ.idLabPositionStr   = "top:12px;left:88px";
    summ.idLabWidthStr      = "25px";
    summ.idLabHeightStr     = "10px";
    summ.idLabStyleStr      = summ.contentStyle;
    summ.headerPositionStr  = "top:12px;left:12px;";
    
    summ.smallVersion       = true;
    summ.hiddenStyle        = "m-cardsummary-hidden-small";
    
    return summ;   
  }
  
  private Label content;
  private Label user;
  private Label dateLab;
  private Label header;
  private Label idLab;
  
  private NativeButton star;
  private SimpleDateFormat dateFormatter;

  private Object cardId;
  private Resource bckgrndResource;
  private Resource starGreyResource;
  private Resource starRedResource;
  private Embedded backImg;
  private Card card;
  private User me;
  
//@formatter:off
  private String widthStr           = "242px"; //CARDSUMMARY_W;
  private String heightStr          = "166px"; //CARDSUMMARY_H;
  private String headerPositionStr  = "top:20px;left:26px"; //CARDSUMMARY_HEADER_POS;
  private String headerWidthStr     = "162px"; //CARDSUMMARY_HEADER_W;
  private String headerHeightStr    = "15px"; //CARDSUMMARY_HEADER_H;
  private String headerStyleStr     = "m-cardsummary-header";
  private String textColorStyleStr;  // set in constructor
  
  private String contentPositionStr = "top:50px;left:26px";//CARDSUMMARY_CONTENT_POS;
  private String contentWidthStr    = "200px"; //CARDSUMMARY_CONTENT_W;
  private String contentHeightStr   = "70px"; //CARDSUMMARY_CONTENT_H;
  private String contentStyle       = "m-cardsummary-content";
  
  private String userDatePositionStr = "top:130px;left:26px"; //CARDSUMMARY_USER_DATE_POS;
  private String userDateWidthStr   =  "200px"; //CARDSUMMARY_USER_DATE_W;
  private String userWidthStr       = "105px"; //CARDSUMMARY_USER_W;;
  private String userHeightStr      = "15px"; //CARDSUMMARY_USER_H;
  private String starPositionStr    = "top:10px;left:195px"; //CARDSUMMARY_STAR_POS;
  private String userStyle          = "m-cardsummary-user";
  
  private String idLabWidthStr      = "50px";
  private String idLabHeightStr     = "15px";
  private String idLabPositionStr   = "top:20px;left:135px";
  private String idLabStyleStr      = contentStyle;
  
  private String movePositionStr    = "top:35px;left:26px";
  private String moveWidthStr       = "162px";
  private String moveHeightStr      = "12px";
  private String moveStyleStr       = "m-cardsummary-movelabel";
//@formatter:on
  
  private String textPrefix   = "";
  private String textSuffix   = "";
  private boolean smallVersion = false;
  private boolean isFactCard = false;
  private boolean mockupOnly = false;
  
  //private Session constructorSession;
  private String hiddenStyle = "m-cardsummary-hidden";
  
  private CardSummary(Object cardId, boolean isFactCard, Session sess)
  {
    this(cardId,isFactCard,sess,false);
  }
  
  @HibernateSessionThreadLocalConstructor
  @HibernateRead  
  private CardSummary(Object cardId, boolean isFactCard, Session sess, boolean mockupOnly)
  {
    this.cardId = cardId;
    this.isFactCard = isFactCard;
    this.mockupOnly = mockupOnly;
    
    // Assumption: the initGui() method below, where these 2 hib. objects are referenced, will be called in
    // the same thread/vaadin session so the hib. session passed in on the constructor will still be valid
    
    card = Card.getTL(cardId); 
    
    me   = Mmowgli2UI.getGlobals().getUserTL();
    
    content = new HtmlLabel();
    user    = new Label();
    dateLab = new Label();
    header  = new Label();
    idLab   = new Label();
    star    = new NativeButton();
    dateFormatter = new SimpleDateFormat("MM/dd HH:mm z");
    
    textColorStyleStr = CardStyler.getCardTextColorOverWhiteStyle(card.getCardType());
  }
  
  public void initGui()
  {
    initGui(HSess.get());
  }
  
  public void refreshContents(Card c)
  {
    content.removeStyleName(hiddenStyle);
    if(c.isHidden())
      content.addStyleName(hiddenStyle);     // red "HIDDEN" text background
    content.setValue(formatText(c.getText()));
    content.setDescription(c.getText());
    user.setValue(c.getAuthorName()); //.getAuthor().getUserName());    
  }
  
  @SuppressWarnings("serial")
  public void initGui(Session sess)
  {
    setHeight(heightStr);
    setWidth(widthStr);
    addStyleName("m-cursor-pointer"); // pointer when over me, because I'm clickable
    
    if(bckgrndResource != null){
      backImg = new Embedded(null,bckgrndResource); 
      addComponent(backImg, "top:0px;left:0px");
    }
    
    if (!isFactCard) {
      String hdrTxt = card.getCardType().getSummaryHeader();
      header.setDescription(hdrTxt);
      if (hdrTxt != null && hdrTxt.length() > 0) {
        //if(hdrTxt.length()>=15) {
       //   hdrTxt = hdrTxt.substring(0, 15).trim()+"...";
       // }
        header.setValue(hdrTxt);
        header.setWidth(headerWidthStr);
        header.setHeight(headerHeightStr);
        addComponent(header, headerPositionStr);
        header.addStyleName(headerStyleStr);
        header.addStyleName(textColorStyleStr);
        header.addStyleName("m-cursor-pointer");
      }

      if (starGreyResource != null && starRedResource != null) {
        Set<Card> favs = me.getFavoriteCards();
        if (favs != null && favs.contains(card)) {
          star.setIcon(starRedResource);
        }
        else {
          star.setIcon(starGreyResource);
        }
        addComponent(star, starPositionStr);
        if(!mockupOnly)
          star.addClickListener(new StarClick());
        star.addStyleName("borderless");
        star.setDescription("Mark or unmark as a favorite of yours");
      }
    }
    idLab.setWidth(idLabWidthStr);
    idLab.setHeight(idLabHeightStr);
    idLab.setValue(""+card.getId());
    idLab.addStyleName(idLabStyleStr);
    idLab.addStyleName("m-text-align-right");
    addComponent(idLab,idLabPositionStr);
    
    content.setWidth(contentWidthStr);
    content.setHeight(contentHeightStr);
    addComponent(content, contentPositionStr);
    content.addStyleName(contentStyle);
    if(card.isHidden())
      content.addStyleName(hiddenStyle);     // red "HIDDEN" text background

    content.setValue(formatText(card.getText()));
    content.addStyleName("m-cursor-pointer");
    content.setDescription(card.getText());
    
    if (!isFactCard) {
      /* We shouldn't have to be setting widths on the components within this horizontal layout.  The
       * fact that we do is, I think, related to the absolutelayout parent.  Get rid of that and we might
       * be able to yank the .setwidth, setheight stuff.
       */
      HorizontalLayout hLay = new HorizontalLayout();
      hLay.setMargin(false);
      hLay.setHeight(userHeightStr);
      hLay.setWidth(userDateWidthStr);
      addComponent(hLay,userDatePositionStr);
      
      user.setValue(card.getAuthorName()); //.getAuthor().getUserName());
      hLay.addComponent(user);
      user.setWidth(userWidthStr);
      user.setHeight(userHeightStr);
      user.addStyleName(userStyle);
      user.addStyleName("m-cursor-pointer");
      
      Label sp;
      hLay.addComponent(sp=new Label());
      sp.setWidth("1px");
      hLay.setExpandRatio(sp, 1.0f);
      
      dateLab.setValue(dateFormatter.format(card.getCreationDate()));
      hLay.addComponent(dateLab);
      dateLab.setHeight(userHeightStr);
      dateLab.setWidth(null); //dateWidthStr);
      dateLab.addStyleName(userStyle);
     }
        
    if(card.getCreatedInMove().getId() != Move.getCurrentMove(sess).getId()) {
      Label lab = new Label(card.getCreatedInMove().getName());
      lab.addStyleName(moveStyleStr);
      lab.setWidth(moveWidthStr);
      lab.setHeight(moveHeightStr);
      addComponent(lab,movePositionStr);      
    }
    
    // Listen for layout click events
    if(!mockupOnly)
    this.addLayoutClickListener(new LayoutClickListener()
    {
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateRead
      public void layoutClick(LayoutClickEvent event)
      {
        HSess.init();
        Component c = event.getChildComponent();
        Card card = Card.getTL(cardId);
        if(c == star) {
          //Let the starlistener below handle it
        }
        else if (c == user) {
          AppEvent evt = new AppEvent(CARDAUTHORCLICK, CardSummary.this, card.getAuthor().getId());
          Mmowgli2UI.getGlobals().getController().miscEventTL(evt);
        }
        else { //if (c == content) {
          AppEvent evt = new AppEvent(CARDCLICK, CardSummary.this, card.getId());
          Mmowgli2UI.getGlobals().getController().miscEventTL(evt);
        }
        HSess.close();
      }
    });
  }
  
  class StarClick implements ClickListener
  {
    private static final long serialVersionUID = 7092717307047085740L;

    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateRead
    @HibernateUpdate
    @HibernateCommitted
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      
      User me = Mmowgli2UI.getGlobals().getUserTL();
      Card card = Card.getTL(cardId);
      
      if (me.getFavoriteCards().contains(card)) {
        // remove it
        me.getFavoriteCards().remove(card);
        star.setIcon(starGreyResource);
      }
      else {
        me.getFavoriteCards().add(card);
        star.setIcon(starRedResource);
      }
      User.updateTL(me);
      
      HSess.close(); // commit
    }  
  }
  
  private String formatText(String s)
  {
    if(smallVersion)
      return s;
    
    int spaceLoc = s.indexOf(' ', 25);
    if(spaceLoc == -1 || spaceLoc > 35)
      return s;
    
    StringBuilder sb = new StringBuilder();
    sb.append(textPrefix);
    sb.append("<b>");
    sb.append(morphLinks(s.substring(0, spaceLoc)));
    sb.append("</b>");
    sb.append(morphLinks(s.substring(spaceLoc)));
    sb.append(textSuffix);
    return sb.toString();
  }
  
  private String morphLinks(String txt)
  {
    return txt; // Don't do this here, the layout listener will take us to the card, and we get confused...return MmowgliLinkInserter.insertLinks(txt);
  }

  public Object getCardId()
  {
    return cardId;
  }
}
