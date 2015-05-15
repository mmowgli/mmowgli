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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.event.*;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;

import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgliMobile.data.*;

public class ListView2 extends ForwardButtonView implements LayoutClickListener
{
  private static final long serialVersionUID = -6279802809551568787L;

  private Table table;
  private Folder folder;
  private class EntryButton2 extends CssLayout
  {
    private static final long serialVersionUID = 1L;
    private final ListEntry entry;
    private static final String STYLENAME = "message-button";

    public EntryButton2(ListEntry entry, ListEntryRenderer2 renderer)
    {
      this.entry = entry;

      setWidth("100%");
      setStyleName(STYLENAME);
      renderer.renderEntry(entry, ListView2.this, this);
    }

    public ListEntry getMessage()
    {
      return entry;
    }
  }

  private ListEntryRenderer2 renderer;

  private void setRenderer()
  {
    Class<?> cls = folder.getPojoClass();
    if (cls == Card.class)
      renderer = ListEntryRenderer2.c();
    else if (cls == ActionPlan.class)
      renderer = ListEntryRenderer2.ap();
    else
      // if(cls == User.class)
      renderer = ListEntryRenderer2.u();
  }

  @SuppressWarnings("serial")
  public ListView2(final Folder folder)
  {
    setCaption(folder.getName());
    if(folder.getPojoClass() == Card.class) {
      addStyleName("m-card-list");
      }
    else if(folder.getPojoClass() == ActionPlan.class) {
      addStyleName("m-actionplan-list");
    }
    else if(folder.getPojoClass() == User.class){
      addStyleName("m-user-list");
    }
    else {
      addStyleName("message-list");
    }

    this.folder = folder;
    setRenderer();

    table = new Table(null, folder.getContainer());
    table.setImmediate(true);
    table.setSelectable(true);
    table.setMultiSelect(false);
    table.setNullSelectionAllowed(false);
    table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
    table.setSizeFull();
    
    // Replace name column with navigation buttons
    table.addGeneratedColumn("name", new Table.ColumnGenerator() {
      private static final long serialVersionUID = 1L;

      @Override
      public Component generateCell(Table source, Object itemId, Object columnId)
      {
        Object key = HSess.checkInit();
        Class<?> cls = folder.getPojoClass();
        if (cls == Card.class) {
          final ListEntry m = new CardListEntry(Card.get(itemId, HSess.get()));
          m.setParent(folder);
          EntryButton2 btn = new EntryButton2(m, renderer);
          btn.addLayoutClickListener(ListView2.this);
          HSess.checkClose(key);
          return btn;
        }
        if (cls == ActionPlan.class) {
          final ListEntry m = new ActionPlanListEntry(ActionPlan.get(itemId, HSess.get()));
          m.setParent(folder);
          EntryButton2 btn = new EntryButton2(m, renderer);
          btn.addLayoutClickListener(ListView2.this);
          HSess.checkClose(key);
          return btn;

        }
        if (cls == User.class) {
          final ListEntry m = new UserListEntry(User.get(itemId, HSess.get()));
          m.setParent(folder);
          EntryButton2 btn = new EntryButton2(m, renderer);
          btn.addLayoutClickListener(ListView2.this);
          HSess.checkClose(key);
          return btn;

        }
        HSess.checkClose(key);
        return null;
      }
    });
    table.setColumnExpandRatio("name", 1);
    table.setVisibleColumns(new Object[] { "name" });

    table.addItemClickListener(new ItemClickListener() {
      @Override
      public void itemClick(ItemClickEvent event)
      {
        Object key = HSess.checkInit();
        Class<?> cls = folder.getPojoClass();
        ListEntry entry;
        if (cls == Card.class)
          entry = new CardListEntry(Card.get(event.getItemId(), HSess.get()));
        else if (cls == ActionPlan.class)
          entry = new ActionPlanListEntry(ActionPlan.get(event.getItemId(), HSess.get()));
        else //if (cls == User.class) {
          entry = new UserListEntry(User.get(event.getItemId(), HSess.get()));

        entry.setParent(folder);
        entryClicked(entry, null);
        HSess.checkClose(key);
      }
    });

    table.setCellStyleGenerator(new CellStyleGenerator() {
      @Override
      public String getStyle(Table source, Object itemId, Object propertyId)
      {
        if (table.firstItemId() == itemId && propertyId == null) {
          return "first";
        }
        if (propertyId == "new") {
          return "new";
        }

        return null;
      }
    });

    setContent(table);
    setToolbar(new MmowgliFooter2());
  }

  @Override
  protected void onBecomingVisible()
  {
    super.onBecomingVisible();
    ((MmowgliFooter2)getToolbar()).setNavigationManager(getNavigationManager());
  }

 
  List<ListEntry> selected = new ArrayList<ListEntry>();

  @Override
  public void layoutClick(LayoutClickEvent event)
  {
    EntryButton2 btn = (EntryButton2) event.getSource();
    ListEntry msg = btn.getMessage();
    entryClicked(msg, btn);
  }

  private void entryClicked(ListEntry msg, EntryButton2 btn)
  {
     table.select(getMessageTableId(msg));
     setMessage(msg);
  }

  private Serializable getMessageTableId(ListEntry msg)
  {
    if (msg instanceof CardListEntry)
      return ((CardListEntry) msg).getCard().getId();
    if (msg instanceof UserListEntry)
      return ((UserListEntry) msg).getUser().getId();
    if (msg instanceof ActionPlanListEntry)
      return ((ActionPlanListEntry) msg).getActionPlan().getId();
    return null;
  }

  private void setMessage(final ListEntry entry)
  {
    // This doesn't work with the breadcrumbs
    /*
     * ComponentContainer cc = (ComponentContainer) getUI().getContent(); if (cc instanceof MainViewIF) { MainViewIF mainView = (MainViewIF) cc;
     * mainView.setMessage(message, this); }
     */

    NavigationManager nav = getNavigationManager();
   // FullEntryView mv = new FullEntryView((MmowgliMobileNavManager) nav);
    FullEntryView2 ev = getNextView();
    ev.setEntry(entry, this);
    nav.navigateTo(ev);
  }

  public void selectMessage(Object msg)
  {
    table.setValue(msg);
  }
  
  private FullEntryView2 fev;
  private FullEntryView2 getNextView()
  {
     if(fev == null)
       fev = new FullEntryView2();
     return fev;
  }
}
