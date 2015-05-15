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

import static edu.nps.moves.mmowgli.MmowgliConstants.CARD_UPDATE_LOGS;
import static edu.nps.moves.mmowgli.MmowgliConstants.USER_UPDATE_LOGS;
import static edu.nps.moves.mmowgli.MmowgliEvent.CARDAUTHORCLICK;

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
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.cards.CardMarkingManager;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;
import edu.nps.moves.mmowgli.utility.*;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * CardLarge.java
 * Created on Nov 22, 2010
 * Updated Mar 12, 2014
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardLarge extends AbsoluteLayout implements MmowgliComponent
{
  private static final long serialVersionUID = -6462434171917940619L;
  
  public static final String CARDBIG_W           = "541px";
  public static final String CARDBIG_H           = "354px";
  public static final String CARDBIG_TITLE_W     = "340px";
  public static final String CARDBIG_TITLE_H     = "20px";
  public static final String CARDBIG_TITLE_POS   = "top:55px;left:70px";
  public static final String CARDBIG_STAR_POS    = "top:36px;left:452px";
  public static final String CARDBIG_CONTENT_POS = "top:105px;left:70px";
  public static final String CARDBIG_CONTENT_W   = "420px";
  public static final String CARDBIG_ICON_POS    = "top:261px;left:70px";
  public static final String CARDBIG_ICON_W      = "53px";
  public static final String CARDBIG_ICON_H      = "53px";
  public static final String CARDBIG_UNAME_POS   = "top:278px;left:138px";
  public static final String CARDBIG_UNAME_W     = "200";
  public static final String CARDBIG_UNAME_H     = "20px";
  public static final String CARDBIG_DATE_POS    = "top:278px;left:363px";
  public static final String CARDBIG_DATE_W      = "150px";
  public static final String CARDBIG_DATE_H      = "20px";
  
  private static String userProfile_tt = "View author player profile";
  private static String title_tt = "Card type";
  private static String id_tt = "Card ID number";
  private static String star_tt = "Mark or unmark as a favorite of yours";
  private static String date_tt = "Date & time posted";
  
  private Button starButton;
  private Resource starGreyResource, starRedResource, bckgrndResource;
  private Embedded avatarIcon, bckgrndImg;
  private Label title;
  private Label content;
  private Label uname;
  private Label dateLab;
  private Label idLab;
  private Label moveLab;
  private SimpleDateFormat dateFormatter;
  
  private HorizontalLayout markingPanel;
  
  private Object cardId;

  Res resources;
  
  public static CardLarge newCardLargeTL(Object cardId)
  {
    CardLarge cardL = new CardLarge(cardId);
    MediaLocator mloc = Mmowgli2UI.getGlobals().getMediaLocator();
    cardL.bckgrndResource = mloc.getCardLargeBackgroundTL(cardId);
    cardL.starGreyResource = mloc.getCardLargeGreyStar(cardId);
    cardL.starRedResource = mloc.getCardLargeGoldStar(cardId);
    
    return cardL;
  }

  private CardLarge(Object cardId)
  {
    this.cardId = cardId;

    starButton = new NativeButton();
    title = new Label();
    content = new HtmlLabel();
    uname = new Label();
    dateLab = new Label();
    moveLab = new Label();
    idLab = new HtmlLabel();
    dateFormatter = new SimpleDateFormat("MM/dd HH:mm z");
  }
  
  public void initGui()
  {
    throw new UnsupportedOperationException();
  }
  @SuppressWarnings("serial")
  @HibernateRead
  public void initGuiTL()
  {
    setWidth(CARDBIG_W);
    setHeight(CARDBIG_H);
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    if(bckgrndResource != null) {
      bckgrndImg = new Embedded(null,bckgrndResource);
      addComponent(bckgrndImg,"top:0px;left:0px");
    }
    Card card = Card.getTL(cardId);
    title.setValue(card.getCardType().getTitle().toUpperCase());
    title.setWidth(CARDBIG_TITLE_W);
    title.setHeight(CARDBIG_TITLE_H);
    title.setDescription(title_tt);
    addComponent(title,CARDBIG_TITLE_POS);
    title.addStyleName("m-cardlarge-title");
    title.addStyleName(CardStyler.getCardTextColorOverWhiteStyle(card.getCardType()));
    
    User userMe = globs.getUserTL();
    if(starGreyResource!=null && starRedResource!=null ) {
      if(userMe.getFavoriteCards().contains(card))
        starButton.setIcon(starRedResource);
      else
        starButton.setIcon(starGreyResource);
      addComponent(starButton,CARDBIG_STAR_POS);
      
      starButton.addClickListener(new StarClick());
      starButton.addStyleName("borderless");
      starButton.setDescription(star_tt);
    }
  
    content.setWidth(CARDBIG_CONTENT_W);
    content.setHeight("115px");// 130 CARDBIG_CONTENT_H);
    addComponent(content, CARDBIG_CONTENT_POS);
    Game g = Game.getTL();
    content.setValue(formatText(card.getText(),g,HSess.get()));
    content.addStyleName("m-cardlarge-content");
    if(card.isHidden())
      content.addStyleName("m-cardsummary-hidden");     // red "HIDDEN" text background
    
    // Removing the tool-tip because:
    //  1 it doesn't tell us anything new since the text is already visible
    //  2 new tooltips for inner-links to other ap's and cards conflict
    
    //content.setDescription(card.getText());
    
    addComponent(idLab, "top:55px;left:340px");
    idLab.setHeight("15px");
    idLab.setWidth("70px");
    idLab.addStyleName("m-cardlarge-id");
    idLab.setValue(""+card.getId());
    idLab.setDescription(id_tt);
    markingPanel = new HorizontalLayout();
    markingPanel.setHeight("15px");
    markingPanel.setWidth(CARDBIG_CONTENT_W);
    markingPanel.setSpacing(true);
    markingPanel.addStyleName("m-cardlarge-markings");
    addComponent(markingPanel,"top:220px;left:70px");
    
    showMarking_oob(card);
    
    User author = card.getAuthor();
    if (author.getAvatar() != null) {
      Resource avRes = globs.mediaLocator().locate(author.getAvatar().getMedia());
      if (avRes != null) {
        avatarIcon = new Embedded(null, avRes);
        avatarIcon.setWidth(CARDBIG_ICON_W);
        avatarIcon.setHeight(CARDBIG_ICON_H);
        avatarIcon.addStyleName("m-cursor-pointer");
        avatarIcon.setDescription(userProfile_tt);
        addComponent(avatarIcon, CARDBIG_ICON_POS);
      }
    }
    uname.setValue(author.getUserName());
    uname.setWidth(CARDBIG_UNAME_W);
    uname.setHeight(CARDBIG_UNAME_H);
    uname.addStyleName("m-cardlarge-user");
    uname.addStyleName("m-cursor-pointer");
    uname.setDescription(userProfile_tt);
    // This silly wrapper is so we can listen for mouse clicks on the name (should really used a link-style button); listing on this
    // component as a whole kill the ability to follow url links which are in the card text.
    VerticalLayout wrapper = new VerticalLayout();
    wrapper.addComponent(uname);
    addComponent(wrapper,CARDBIG_UNAME_POS);
    
    dateLab.setValue(dateFormatter.format(card.getCreationDate()));
    dateLab.setWidth(CARDBIG_DATE_W);
    dateLab.setHeight(CARDBIG_DATE_H);
    dateLab.addStyleName("m-cardlarge-user");
    dateLab.setDescription(date_tt);
    addComponent(dateLab,CARDBIG_DATE_POS);
    
    long currentMoveId = Move.getCurrentMoveTL().getId();
    if(card.getCreatedInMove().getId() != currentMoveId) {
      moveLab.setValue(card.getCreatedInMove().getName());
      moveLab.setWidth("198px");
      moveLab.setHeight("20px");
      moveLab.addStyleName("m-cardlarge-movelabel");
      moveLab.addStyleName("m-cursor-pointer");
      moveLab.setDescription("This card was created in a prior round");
      addComponent(moveLab,"top:300px;left:300px");
    }
     
    wrapper.addLayoutClickListener(new LayoutClickListener()
    {
      @MmowgliCodeEntry
       public void layoutClick(LayoutClickEvent event)
      {
        Component c = event.getChildComponent();
        if(c == uname)
          goToUserProfilePage();
      }
    });
    
    addLayoutClickListener(new LayoutClickListener()
    {
      @MmowgliCodeEntry
      public void layoutClick(LayoutClickEvent event)
      {
        Component c = event.getChildComponent();
        if(c == avatarIcon)
          goToUserProfilePage();
      }     
    });
  }
  
  @HibernateOpened
  @HibernateRead
  @HibernateClosed
  private void goToUserProfilePage()
  {
    HSess.init();
    Card card = Card.getTL(cardId);
    AppEvent evt = new AppEvent(CARDAUTHORCLICK, CardLarge.this, card.getAuthor().getId());
    Mmowgli2UI.getGlobals().getController().miscEventTL(evt);
    HSess.close();   
  }
  
  private void showMarking_oob(Card card)
  {
    Set<CardMarking> set = card.getMarking();
    markingPanel.removeAllComponents();
    
    if (set != null && set.size() > 0) {    
      StringBuilder sb = new StringBuilder();
      sb.append("A gamemaster has marked this card <b>");
      CardMarking cm = set.iterator().next(); // know there's one
      boolean hidden = CardMarkingManager.isHidden(card); 
      if(hidden)
        sb.append("<span style='color:rgba(100%, 0%, 0%, 0.5);'>");
      sb.append(cm.getLabel());
      if(hidden)
        sb.append("</span>");
      sb.append("</b>");
      markingPanel.addComponent(new HtmlLabel(sb.toString()));
    }    
  }
  
  // This tried to emphasize the 1st 40 characters...never would have worked if the link split 40
  private String formatText(String s, Game g, Session sess)
  {
    //int spaceLoc = s.indexOf(' ', 40);
    //if(spaceLoc == -1 || spaceLoc > 50)
      return morphLinks(s,g,sess);
  }
  
  private String morphLinks(String txt, Game g, Session sess)
  {
    return MmowgliLinkInserter.insertLinksOob(txt,g,sess);
  }
  
  @SuppressWarnings("serial")
  class StarClick implements ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateRead
    @HibernateUpdate
    @HibernateCommitted
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      User userMe = Mmowgli2UI.getGlobals().getUserTL();
      Card card = Card.getTL(cardId);
      Set<Card> set = userMe.getFavoriteCards();
      if (set.contains(card)) {
        // remove it
        set.remove(card);
        starButton.setIcon(starGreyResource);
      }
      else {
        set.add(card);
        starButton.setIcon(starRedResource);
      }
      userMe.setFavoriteCards(set);
      User.updateTL(userMe);
      
      HSess.close(); // will commit
    }  
  }

  private boolean checkStarTL(Card c, Object uid)
  {
    User u = User.getTL(uid);
    Set<Card> set = u.getFavoriteCards();
    if (set.contains(c)) {
      starButton.setIcon(starRedResource);
      return true;
    }
    else {
      starButton.setIcon(starGreyResource);
      return true;
    }
  }
  
  public void update_oobTL(Card c)
  {
    if(!((Long)c.getId()).equals(cardId))
      return;
    update_oobTL_common(c);
  }
  
  // OOB update
  public void update_oobTL(Object id)
  {
    if(!id.equals(cardId))  //; the card should be for us, but just to make sure
      return;

    Card c = Card.getTL(id);
    update_oobTL_common(c);
  }
  
  private void update_oobTL_common(Card c)
  {
    Session sess = HSess.get();
    
    MSysOut.println(CARD_UPDATE_LOGS, "CardLarge.update_oobTL(), card "+c.getId()+" text: "+c.getText()+" hidden = "+c.isHidden()+" hash = "+hashCode());
    
    // Only 2 things to update...text and marking
    Game g = Game.getTL();
    content.setValue(formatText(c.getText(),g,sess));
    if(c.isHidden())
      content.addStyleName("m-cardsummary-hidden");     // red "HIDDEN" text background
    else
      content.removeStyleName("m-cardsummary-hidden");
    showMarking_oob(c);
  }
  
  public boolean updateUser_oobTL(Object uid)
  {
    MSysOut.println(USER_UPDATE_LOGS,"CardLarge.userUpdated_oobTL("+uid.toString()+")"); 
    if(uid.equals(Mmowgli2UI.getGlobals().getUserID())) {
      Card c = Card.getTL(cardId);
      return checkStarTL(c,uid);
    }
    else
      ;//MSysOut.println(AppMaster.USER_UPDATE_LOGS,"CardLarge.userUpdated_oobTL() not me");

    return false;
  }
}
