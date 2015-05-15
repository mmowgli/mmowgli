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

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.Container;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CssLayout;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.modules.cards.CardTypeManager;
import edu.nps.moves.mmowgliMobile.data.*;

/**
 * Displays accounts, mailboxes, message list hierarchically
 */
public class GameDataCategoriesView2 extends ForwardButtonView
{
  private static final long serialVersionUID = 1534596274849619076L;

  public GameDataCategoriesView2()
  {
    HSess.init();
    setCaption(Game.getTL().getTitle());
    setWidth("100%");
    setHeight("100%");

    CssLayout root = new CssLayout();

    VerticalComponentGroup accounts = new VerticalComponentGroup();

    // Cards
    NavigationButton butt = new NavigationButton("Idea Cards");
    butt.setIcon(FontAwesome.LIGHTBULB_O); //cardsIcon);
    butt.addStyleName("m-touchkit-blueicon");
    accounts.addComponent(butt);
    butt.addClickListener(new NavigationButton.NavigationButtonClickListener()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public void buttonClick(NavigationButtonClickEvent event)
      {
        HSess.init();
        Folder[] fa = new Folder[2];
        CardType posTyp = CardTypeManager.getPositiveIdeaCardTypeTL();
        Container cntr = new CardsByTypeContainer<Card>(posTyp);
        fa[0] = new Folder(posTyp.getTitle(), cntr, Card.class);
        fa[0].addParam(CardType.class.getSimpleName(),posTyp);

        CardType negTyp = CardTypeManager.getNegativeIdeaCardTypeTL();
        cntr = new CardsByTypeContainer<Card>(negTyp);
        fa[1] = new Folder(negTyp.getTitle(), cntr, Card.class);
        fa[1].addParam(CardType.class.getSimpleName(),negTyp);

        // Go to a FolderView
        FolderView2 v = new FolderView2(fa, "Top Level Cards");
        getNavigationManager().navigateTo(v);
        HSess.close();
      }
    });

    // Action Plans
    butt = new NavigationButton("Action Plans");
    butt.setIcon(FontAwesome.LIST_OL);//apIcon
    butt.addStyleName("m-touchkit-blueicon");
    accounts.addComponent(butt);
    butt.addClickListener(new NavigationButton.NavigationButtonClickListener()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public void buttonClick(NavigationButtonClickEvent event)
      {
        // go to a ListView
        Folder f = new Folder("Action Plans", new AllActionPlansContainer<Object>(), ActionPlan.class);
        getNavigationManager().navigateTo(new ListView2(f));
      }
    });

    butt = new NavigationButton("Player Profiles");
    butt.setIcon(FontAwesome.USERS); //usersIcon);
    butt.addStyleName("m-touchkit-blueicon");
    accounts.addComponent(butt);
    butt.addClickListener(new NavigationButton.NavigationButtonClickListener()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public void buttonClick(NavigationButtonClickEvent event)
      {
        // go to a ListView
        Folder f = new Folder("Player Profiles", new AllUsersContainer<Object>(), User.class);
        getNavigationManager().navigateTo(new ListView2(f));
      }
    });

    root.addComponent(accounts);
    setContent(root);
    setToolbar(new MmowgliFooter2());

    HSess.close();
  }

  @Override
  protected void onBecomingVisible()
  {
    super.onBecomingVisible();
    ((MmowgliFooter2)getToolbar()).setNavigationManager(getNavigationManager());
  }
  
}
