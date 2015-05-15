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
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;
import edu.nps.moves.mmowgliMobile.data.Folder;

public class FolderView2 extends ForwardButtonView //NavigationView
{
  private static final long serialVersionUID = 5259417395401918413L;

  public FolderView2(final Folder[] folders, String title)
  {
    setCaption(title);
    setWidth("100%");
    setHeight("100%");

    final Table table = new Table();
    table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);

    table.setSizeFull();

    for (Folder f : folders)
      table.addItem(f);

    table.addGeneratedColumn("name", new Table.ColumnGenerator()
    {
      private static final long serialVersionUID = 1L;

      @SuppressWarnings("serial")
      @Override
      public Component generateCell(Table source, Object itemId, Object columnId)
      {
        if (columnId.equals("name") && itemId instanceof Folder) {
          final Folder f = (Folder) itemId;
          NavigationButton btn = new NavigationButton(f.getName());
          btn.addClickListener(new NavigationButton.NavigationButtonClickListener()
          {
            @Override
            public void buttonClick(NavigationButtonClickEvent event)
            {
             getNavigationManager().navigateTo(new ListView2(f));
            }
          });
          
          btn.setIcon(FontAwesome.FOLDER_OPEN); //childFolderIcon);
          if(f.getPojoClass() == Card.class){
            CardType typ = (CardType) f.getParam(CardType.class.getSimpleName());
            String col = CardStyler.getCardBaseColor(typ);
            if(col.toUpperCase().contains("FFFFFF"))
              col = "#888888";
            col = col.replace("#", "m-");
            btn.addStyleName(col);
          }
          return btn;
        }
        return null;
      }
    });

    table.setVisibleColumns(new Object[] { "name" });

    setContent(table);
    setToolbar(new MmowgliFooter2());
  }

  @Override
  protected void onBecomingVisible()
  {
    super.onBecomingVisible();
    ((MmowgliFooter2)getToolbar()).setNavigationManager(getNavigationManager());
  }

}
