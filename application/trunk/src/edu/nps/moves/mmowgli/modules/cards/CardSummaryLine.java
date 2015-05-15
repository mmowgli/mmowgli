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

package edu.nps.moves.mmowgli.modules.cards;

import static edu.nps.moves.mmowgli.MmowgliEvent.CARDCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.SHOWUSERPROFILECLICK;

import java.text.SimpleDateFormat;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.AppEvent;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliController;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.utility.IDButton;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * CardSummaryLine.java
 * Created on Mar 7, 2011
 * Updated on Mar 26, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardSummaryLine extends HorizontalLayout implements MmowgliComponent, LayoutClickListener
{
  private static final long serialVersionUID = 4252802685171320685L;
  
  private Object cardId;
  private SimpleDateFormat dateForm;
  private Embedded avatar;
  
  @HibernateSessionThreadLocalConstructor
  public CardSummaryLine(Object cardId)
  {
    this.cardId = cardId;

    dateForm = new SimpleDateFormat("MM/dd HH:mm z");
    setSpacing(true);
    addStyleName("m-greyborder");
    addStyleName("m-cardsummaryline");
    addLayoutClickListener(this);
  }
  
  @Override
  public void initGui()
  {
    Card c = Card.getTL(cardId);
    String tooltip = c.getText();
    
    User auth = c.getAuthor();
    
    Label lab=new Label(dateForm.format(c.getCreationDate()));
    lab.setWidth(6.0f, Unit.EM);
    addComponent(lab);
    setComponentAlignment(lab,Alignment.MIDDLE_LEFT);
    lab.addStyleName("m-cursor-pointer");
    lab.setDescription(tooltip);
    
    addComponent(lab=new Label(c.getCardType().getTitle()));
    lab.setWidth(5.0f, Unit.EM);
    setComponentAlignment(lab, Alignment.MIDDLE_LEFT);
    lab.addStyleName("m-cursor-pointer");
    lab.setDescription(tooltip);
    
    MediaLocator mLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    Embedded emb = new Embedded(null,mLoc.getCardDot(c.getCardType()));
    emb.setWidth("19px");
    emb.setHeight("15px");
    addComponent(emb);
    setComponentAlignment(emb, Alignment.MIDDLE_LEFT);
    emb.addStyleName("m-cursor-pointer");
    emb.setDescription(tooltip);
    
    addComponent(lab = new Label(c.getText()));
    lab.setHeight(1.0f, Unit.EM); ;
    setComponentAlignment(lab, Alignment.MIDDLE_LEFT);
    setExpandRatio(lab, 1.0f); // all the extra
    lab.addStyleName("m-cursor-pointer");
    lab.setDescription(tooltip); 
    
    if (auth.getAvatar() != null) {
      avatar = new Embedded(null, mLoc.locate(auth.getAvatar().getMedia(), 32));
      avatar.setWidth("24px");
      avatar.setHeight("24px");
      addComponent(avatar);
      setComponentAlignment(avatar, Alignment.MIDDLE_LEFT);
      avatar.addStyleName("m-cursor-pointer");
      avatar.setDescription(tooltip);
    }
    IDButton uButt = new IDButton(c.getAuthorName(),SHOWUSERPROFILECLICK,c.getAuthor().getId());
    uButt.addStyleName(BaseTheme.BUTTON_LINK);
    uButt.setWidth(8.0f, Unit.EM);
    addComponent(uButt);
    setComponentAlignment(uButt, Alignment.MIDDLE_LEFT);
    uButt.setDescription(tooltip);
  }

  @Override
  @MmowgliCodeEntry
  @HibernateOpened
  @HibernateClosed
  public void layoutClick(LayoutClickEvent event)
  {
    HSess.init();
    MmowgliController cntlr = Mmowgli2UI.getGlobals().getController();
    if(event.getClickedComponent() == avatar) {
      Card c = Card.getTL(cardId);
      cntlr.miscEventTL(new AppEvent(SHOWUSERPROFILECLICK,this,c.getAuthor().getId()));
    }
    else
      cntlr.miscEventTL(new AppEvent(CARDCLICK, this, cardId));
    HSess.close();
  }

}
