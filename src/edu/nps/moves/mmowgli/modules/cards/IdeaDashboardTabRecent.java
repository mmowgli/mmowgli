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

import java.util.List;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.CardTable;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;

/**
 * ActionPlanPageTabImages.java
 * Created on Feb 8, 2011
 * Updated on 26 Mar, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class IdeaDashboardTabRecent extends IdeaDashboardTabPanel implements ClickListener
{
  private static final long serialVersionUID = -184027503783432454L;
  
  private VerticalLayout tableLay;
  private Button allIdeasButt, supInterestingButt, expandButt, adaptButt, counterButt, exploreButt;

  private Component lastTable;
  private Button lastButt;
    
  
  public IdeaDashboardTabRecent()
  {
    super();
    allIdeasButt = buildButt("All Cards");
    supInterestingButt = buildButt("Super interesting cards");
    expandButt   = buildButt("Expand cards");
    adaptButt    = buildButt("Adapt cards");
    counterButt  = buildButt("Counter cards");
    exploreButt  = buildButt("Explore cards");
  }
  
  private Button buildButt(String s)
  {
    Button b = new NativeButton(s);
    b.setStyleName(BaseTheme.BUTTON_LINK);
    b.addStyleName("borderless");
    b.addStyleName("m-actionplan-comments-button");
    b.addClickListener(this);
    return b;
  }

  @Override
  public void initGui()
  {
    setupLeftPanel();

    AbstractComponentContainer c = getRightLayout();
    if(c instanceof VerticalLayout) {
      tableLay = (VerticalLayout)c;
      tableLay.setWidth("100%");
      tableLay.setHeight("100%");
    }
    else {
      ((AbsoluteLayout)c).addComponent(tableLay = new VerticalLayout(),"top:0px;left:0px");
      tableLay.setWidth("680px");
      tableLay.setHeight("730px");
    }
    insertAllIdeasTableTL();
    lastButt = allIdeasButt; 
  }

  private void setupLeftPanel()
  {
    VerticalLayout vLay = new VerticalLayout();
    getLeftLayout().addComponent(vLay,"top:0px;left:0px");
    vLay.setSpacing(true);
    
    vLay.addComponent(new Label("Card Filters"));
    Label lab;
    vLay.addComponent(lab=new HtmlLabel("<p>Card play can be fast and thoughtful.  Here are the most recent.  Look for the cards most relevant to your thinking.</p>"));
    lab.addStyleName("m-font-12");

    //todo style here
    vLay.addComponent(allIdeasButt);
    vLay.addComponent(supInterestingButt);
    vLay.addComponent(expandButt);
    vLay.addComponent(adaptButt);
    vLay.addComponent(counterButt);
    vLay.addComponent(exploreButt);
    vLay.addComponent(lab=new HtmlLabel("<br/><br/><p>(Hint: mouse-over the text entry and a popup tooltip should appear with the full text.)</p>"));
    lab.addStyleName("m-font-12");
 }
  
  private CardTable allIdeasTable;
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void insertAllIdeasTableTL()
  {
    if (allIdeasTable == null || (lastTable != null && lastTable != allIdeasTable)) {
      if(isGameMaster)
        allIdeasTable = new CardTable(null, new AllCardsDescendingContainer(), true, false, false);
      else {
        User me = Mmowgli2UI.getGlobals().getUserTL();
        allIdeasTable = new CardTable(null,new NotHiddenCardContainer(me),true,false,false);
      }
      allIdeasTable.setPageLength(40);
      allIdeasTable.setWidth("679px");
      allIdeasTable.setHeight("100%");

      if(lastTable!= null)
        tableLay.removeComponent(lastTable);
      tableLay.addComponent(lastTable=allIdeasTable);
    }
  }
  
  private CardTable superTable;
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void insertSuperInterestingTableTL()
  {
    if (superTable == null || (lastTable != null && lastTable != superTable)) { 
      User me = Mmowgli2UI.getGlobals().getUserTL();
      superTable = new CardTable(null,new SuperInterestingCardContainer(me),true,false,false);
      superTable.setPageLength(40);
      superTable.setWidth("679px");
      superTable.setHeight("730px");

      if(lastTable!= null)
        tableLay.removeComponent(lastTable);
      tableLay.addComponent(lastTable=superTable);
    }
  }
  
  private CardTable createTypeTableTL(CardType typ)
  {
    User me = Mmowgli2UI.getGlobals().getUserTL();
    @SuppressWarnings({ "unchecked", "rawtypes" })
    CardTable ct = new CardTable(null,new CardTypeContainer(typ,me),true,false,false);
    ct.setPageLength(40);
    ct.setWidth("679px");
    ct.setHeight("730px");
    return ct;
  }
  
  private CardTable expandTable;
  private void insertExpandTableTL()
  {
    if(expandTable == null  || (lastTable != null && lastTable != expandTable)) {
      expandTable = createTypeTableTL(CardTypeManager.getExpandTypeTL());
      
      if(lastTable!= null)
        tableLay.removeComponent(lastTable);
      tableLay.addComponent(lastTable=expandTable);
    }
  }
  
  private CardTable adaptTable;
  private void insertAdaptTableTL() 
  {
    if(adaptTable == null || (lastTable != null && lastTable != adaptTable)) {
      adaptTable = createTypeTableTL(CardTypeManager.getAdaptTypeTL());   
      
      if(lastTable!= null)
        tableLay.removeComponent(lastTable);
      tableLay.addComponent(lastTable=adaptTable);
    }
  }
  
  private CardTable counterTable;

  private void insertCounterTableTL()
  {
    if (counterTable == null || (lastTable != null && lastTable != counterTable)) {
      counterTable = createTypeTableTL(CardTypeManager.getCounterTypeTL());

      if (lastTable != null)
        tableLay.removeComponent(lastTable);
      tableLay.addComponent(lastTable = counterTable);
    }
  }
  
  private CardTable exploreTable;
  private void insertExploreTableTL()
  {
    if(exploreTable == null || (lastTable != null && lastTable != exploreTable)) {
      exploreTable = createTypeTableTL(CardTypeManager.getExploreTypeTL());    

      if (lastTable != null)
        tableLay.removeComponent(lastTable);
      tableLay.addComponent(lastTable = exploreTable);
    }
  }

  @Override
  @MmowgliCodeEntry
  @HibernateOpened
  @HibernateClosed
  public void buttonClick(ClickEvent event)
  {
    Button b = event.getButton();
    if(b == lastButt)
      return ;
    lastButt = b;
    
    HSess.init();
    if(b == allIdeasButt) {
      insertAllIdeasTableTL();
    }
    else if(b == supInterestingButt) {
      insertSuperInterestingTableTL();
    }
    else if(b == expandButt) {
      insertExpandTableTL();
    }
    else if(b == adaptButt) {
      insertAdaptTableTL();
    }
    else if(b == counterButt) {
      insertCounterTableTL();
    }
    else if(b == exploreButt) {
      insertExploreTableTL();
    } 
    
    HSess.close();
  }
  
  @Override
  /**
   * Only needed if sub class calls buildCardTable()
   */
  public List<Card> getCardList()
  {
    return null;
  }

  @Override
  /**
   * Only needed if sub class calls buildCardTable()
   */
  boolean confirmCard(Card c)
  {
    return false;
  }

}
