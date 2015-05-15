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

package edu.nps.moves.mmowgliMobile.ui;

import java.text.SimpleDateFormat;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.data.Container;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;
import edu.nps.moves.mmowgli.utility.MediaLocator;
import edu.nps.moves.mmowgliMobile.data.*;

/**
 * CardRenderer.java Created on Feb 24, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardRenderer2 extends EntryRenderer2 //implements ClickListener
{
  private SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy hh:mm");
  private static MediaLocator mediaLocator = new MediaLocator();
  
  public void setMessage(FullEntryView2 mView, ListEntry message, ListView2 messageList, AbstractOrderedLayout layout)
  {
    Object key = HSess.checkInit();
    CardListEntry wc = (CardListEntry) message;
    Card c = wc.getCard();
    CardType typ = c.getCardType();
    
    layout.removeAllComponents();
    layout.setSpacing(true);
    
    VerticalLayout cardLay = new VerticalLayout();
    cardLay.addStyleName("m-card-render");
    cardLay.setWidth("98%"); //100%");
    cardLay.setSpacing(true);
    layout.addComponent(cardLay);
    
    HorizontalLayout horl = new HorizontalLayout();
    horl.addStyleName("m-card-header");
    String stl = CardStyler.getCardBaseStyle(typ);
    horl.addStyleName(stl);
    horl.addStyleName(CardStyler.getCardTextColorOverBaseStyle(typ));
    horl.setMargin(true);
    horl.setWidth("100%");
    
    Label lbl = new Label(typ.getTitle());//c.getText());
    horl.addComponent(lbl);
    lbl = new Label(""+getPojoId(message));
    lbl.addStyleName("m-text-align-right");
    horl.addComponent(lbl);
    cardLay.addComponent(horl);
    
    horl = new HorizontalLayout();
    horl.setWidth("100%");
    horl.setMargin(true);
    cardLay.addComponent(horl);    
    lbl = new Label(c.getText());
    horl.addComponent(lbl);
    
    horl = new HorizontalLayout();
    horl.addStyleName("m-card-footer");
    
    horl.setMargin(true);
    horl.setWidth("100%");
    horl.addComponent(lbl=new Label(""));
    lbl.setWidth("5px");

    Image img = new Image();
    img.setSource(mediaLocator.locate(c.getAuthor().getAvatar().getMedia()));
    img.setWidth("30px");
    img.setHeight("30px");
    horl.addComponent(img);
       
//    horl.addComponent(lbl=new Label(c.getAuthorName()));
//    lbl.setWidth("100%");
//    lbl.addStyleName("m-text-align-center");
//    horl.setComponentAlignment(lbl, Alignment.MIDDLE_CENTER);
//    horl.setExpandRatio(lbl, 1.0f);
    
    Button authButt = new MyButton(c.getAuthorName(),c,mView);    
    authButt.setStyleName(BaseTheme.BUTTON_LINK);
    authButt.setWidth("100%");
    horl.addComponent(authButt);
    horl.setComponentAlignment(authButt, Alignment.MIDDLE_CENTER);
    horl.setExpandRatio(authButt, 1.0f);
    
    horl.addComponent(lbl=new HtmlLabel(formatter.format(message.getTimestamp())));
    lbl.setWidth("115px");;
    lbl.addStyleName("m-text-align-right");
    horl.setComponentAlignment(lbl, Alignment.MIDDLE_CENTER);

    cardLay.addComponent(horl);
    
//    lbl = new Hr();   
//    layout.addComponent(lbl);
    
    lbl = new Label("Child Cards");
    layout.addComponent(lbl);
    lbl.addStyleName("m-text-center");
    
//    lbl = new Hr();
//    layout.addComponent(lbl);

    horl = new HorizontalLayout();
    horl.setSpacing(true);
    horl.setMargin(true);
    horl.setWidth("100%");
    layout.addComponent(horl);
      
    horl.addComponent(makeChildGroupButton("Expand", (CardListEntry) message, CardType.getExpandTypeTL(), messageList));
    horl.addComponent(makeChildGroupButton("Counter", (CardListEntry) message, CardType.getCounterTypeTL(), messageList));
    horl.addComponent(makeChildGroupButton("Adapt", (CardListEntry) message, CardType.getAdaptTypeTL(), messageList));
    horl.addComponent(makeChildGroupButton("Explore", (CardListEntry) message, CardType.getExploreTypeTL(), messageList));
    
    HSess.checkClose(key);
  }

  @SuppressWarnings("serial")
  private Component makeChildGroupButton(final String title, CardListEntry card, CardType typ, final ListView2 currentMessageList)
  {
     final Button btn = new Button();
     btn.addStyleName("m-card-child-button");
     btn.addStyleName(CardStyler.getCardTextColorOverBaseStyle(typ));
     btn.addStyleName(CardStyler.getCardBaseStyle(typ));
    
     final Card parent = card.getCard();
     final Container container = new ChildCardsByTypeContainer<Card>(parent, typ);
     if (container.size() > 0) {
       btn.addClickListener(new ClickListener() {
         @Override
         public void buttonClick(ClickEvent event)
         {
           NavigationManager nav = currentMessageList.getNavigationManager();
           if(nav == null)
             nav = currentMessageList.getNavigationManager();
           String par = parent==null?"?":(""+parent.getId());
           nav.navigateTo(new ListView2(new Folder(title+"s on card "+par, container, Card.class)));
         }
       });
       btn.setCaption(title+" "+container.size());
     }
     else {
       btn.setCaption(title);
       btn.setEnabled(false); 
     }
     return btn;
  } 
  
  @SuppressWarnings("serial")
  class MyButton extends Button implements ClickListener
  {
    private Card c;
    private NavigationView view;
    public MyButton (String title, Card c, NavigationView view)
    {
      super(title);
      this.c = c;
      this.view = view;
      addClickListener(this);
    }
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      String authorName = c.getAuthorName();
      User u = User.getUserWithUserNameTL(authorName);
      if(u == null)  {
        System.out.println("*****DB erroror, no user exists with name = "+authorName);
      }
      else {
        UserListEntry entry = new UserListEntry(u);
        NavigationManager nav = view.getNavigationManager();
        FullEntryView2 fev = new FullEntryView2();
        fev.setEntry(entry, null);
        nav.navigateTo(fev);
      }
      HSess.close();      
    }
  }
}
