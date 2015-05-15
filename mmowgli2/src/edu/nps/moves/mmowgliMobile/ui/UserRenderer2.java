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

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.data.Container;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Image;

import edu.nps.moves.mmowgli.components.Hr;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.db.Level;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.utility.MediaLocator;
import edu.nps.moves.mmowgliMobile.data.*;

/**
 * UserRenderer.java
 * Created on Feb 24, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserRenderer2 extends EntryRenderer2
{
  private static MediaLocator mediaLocator = new MediaLocator();
  private int numCards = 0;
  private int numAps = 0;
  
  public void setMessage(FullEntryView2 mView, ListEntry message, ListView2 messageList, AbstractOrderedLayout layout)
  {
    // messageList can be null if coming in from ActionPlan
    Object key = HSess.checkInit();
    UserListEntry wu = (UserListEntry) message;
    User u = wu.getUser();
    layout.removeAllComponents();

    HorizontalLayout hlay = new HorizontalLayout();
    layout.addComponent(hlay);
    hlay.addStyleName("m-userview-top");
    hlay.setWidth("100%");
    hlay.setMargin(true);
    hlay.setSpacing(true);
    
    Image img = new Image();
    img.addStyleName("m-ridgeborder");
    img.setSource(mediaLocator.locate(u.getAvatar().getMedia()));
    img.setWidth("90px");
    img.setHeight("90px");
    hlay.addComponent(img);
    hlay.setComponentAlignment(img, Alignment.MIDDLE_CENTER); 
    
    Label lab;
    hlay.addComponent(lab=new Label());
    lab.setWidth("5px");
    
    VerticalLayout vlay = new VerticalLayout();
    vlay.setSpacing(true);
    hlay.addComponent(vlay);
    hlay.setComponentAlignment(vlay, Alignment.MIDDLE_LEFT);
    vlay.setWidth("100%");
    hlay.setExpandRatio(vlay, 1.0f);
    HorizontalLayout horl = new HorizontalLayout();
    horl.setSpacing(false);
    vlay.addComponent(horl);
    vlay.setComponentAlignment(horl,Alignment.BOTTOM_LEFT);
    horl.addComponent(lab=new Label("name"));
    lab.addStyleName("m-user-top-label"); //light-text");
    horl.addComponent(lab=new HtmlLabel("&nbsp;&nbsp;"+u.getUserName()));
    lab.addStyleName("m-user-top-value");
    horl = new HorizontalLayout();
    horl.setSpacing(false);
    vlay.addComponent(horl);
    vlay.setComponentAlignment(horl,Alignment.TOP_LEFT);

    horl.addComponent(lab=new Label("level"));
    lab.addStyleName("m-user-top-label"); //light-text");
    Level lev = u.getLevel();
    if(u.isGameMaster()) {
      Level l = Level.getLevelByOrdinal(Level.GAME_MASTER_ORDINAL,HSess.get());
      if(l != null)
        lev = l;
    }
    horl.addComponent(lab=new HtmlLabel("&nbsp;&nbsp;&nbsp;"+lev.getDescription()));
    lab.addStyleName("m-user-top-value");
    

    GridLayout gLay = new GridLayout();
   // gLay.setHeight("155px");  // won't size properly
    gLay.setMargin(true);
    gLay.addStyleName("m-userview-mid");
    gLay.setColumns(2);
    gLay.setRows(11);
    gLay.setSpacing(true);
    gLay.setWidth("100%");
    gLay.setColumnExpandRatio(1, 1.0f);
    layout.addComponent(gLay);
    
    addRow(gLay,"user ID:",""+getPojoId(message));
    addRow(gLay,"location:",u.getLocation());
    addRow(gLay, "expertise:",u.getExpertise());
    addRow(gLay,"affiliation:",u.getAffiliation());
    addRow(gLay,"date registered:",formatter.format(u.getRegisterDate()));
    
    gLay.addComponent(new Hr(),0,5,1,5); 
    
    Container cntr = new CardsByUserContainer<Card>(u);  // expects ThreadLocal session to be setup
    numCards = cntr.size();
    addRow(gLay,"cards played:",""+numCards);
    cntr = new ActionPlansByUserContainer<Card>(u);  // expects ThreadLocal session to be setup
    numAps = cntr.size();
    addRow(gLay,"action plans:",""+numAps);
    
    gLay.addComponent(new Hr(),0,8,1,8); 
    
    addRow(gLay,"exploration points:",""+u.getBasicScore());
    addRow(gLay,"innovation points:",""+u.getInnovationScore());
    
    cardListener = new CardLis(u,mView);
    apListener = new AppLis(u,mView);

    layout.addComponent(makeButtons());
    
    HSess.checkClose(key);
  }
  
  public void addRow(GridLayout lay, String label, String value)
  {
    Label lbl;
    lay.addComponent(lbl = new Label(label));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    lay.setComponentAlignment(lbl, Alignment.TOP_RIGHT);
    
    lay.addComponent(new HtmlLabel(value));
  }
  
  private Component makeButtons()
  {
    HorizontalLayout horl = new HorizontalLayout();
    horl.setWidth("100%");
    horl.setSpacing(true);
    Label lab;
    horl.addComponent(lab = new Label(""));
    horl.setExpandRatio(lab, 0.5f);
    Button cardsButt = new Button("Cards");
    horl.addComponent(cardsButt);
    Button apButt = new Button("Action Plans");
    horl.addComponent(apButt);
    horl.addComponent(lab = new Label(""));
    horl.setExpandRatio(lab, 0.5f);
    cardsButt.addStyleName("m-author-button");
       apButt.addStyleName("m-author-button");
       
    cardsButt.addClickListener(cardListener);   
    apButt.addClickListener(apListener);
    
    cardsButt.setEnabled(numCards>0);
    apButt.setEnabled(numAps>0);
    return horl;    
  }
  
  private ClickListener apListener;
  private ClickListener cardListener;
  
  @SuppressWarnings("serial")
  class AppLis implements Button.ClickListener
  {
    private NavigationView nav;
    private User u;
    AppLis(User u, NavigationView nav)
    {
      this.u = u;
      this.nav = nav;
    }
    @SuppressWarnings("rawtypes")
    @Override
    public void buttonClick(ClickEvent event)
    {
      nav.getNavigationManager().navigateTo(new ListView2(new Folder("Action Plans",
          new ActionPlansByUserContainer(u),ActionPlan.class)));
    }          
  }

  @SuppressWarnings("serial")
  class CardLis implements Button.ClickListener
  {
    private NavigationView nav;
    private User u;
    CardLis(User u, NavigationView nav)
    {
      this.u = u;
      this.nav = nav;
    }
    @SuppressWarnings("rawtypes")
    @Override
    public void buttonClick(ClickEvent event)
    {
      nav.getNavigationManager().navigateTo(new ListView2(new Folder("Cards",
          new CardsByUserContainer(u),Card.class)));
    }      
  }

}
