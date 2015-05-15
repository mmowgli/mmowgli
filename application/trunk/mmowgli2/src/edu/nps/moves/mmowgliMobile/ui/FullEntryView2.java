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

import java.util.Iterator;
import java.util.List;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgliMobile.data.*;

/**
 * A navigation view to display a single message.
 * 
 */
public class FullEntryView2 extends ForwardButtonView// implements ClickListener
{
  private static final long serialVersionUID = -1101712454186250982L;

  private final VerticalLayout layout = new VerticalLayout(); // CssLayout layout = new CssLayout();

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof FullEntryView2))
      return false;
    if (((FullEntryView2) obj).getEntry() == null)
      return false;
    if (getEntry() == null)
      return false;
    return ((FullEntryView2) obj).getEntry().equals(getEntry());
  }

  public FullEntryView2()
  {
    setContent(layout);
    layout.setWidth("100%");
    layout.setStyleName("message-layout");
    addStyleName("message-view");

    setToolbar(new MmowgliFooter2());

    setEntry(null, null);
  }

  @Override
  protected void onBecomingVisible()
  {
    super.onBecomingVisible();
    ((MmowgliFooter2)getToolbar()).setNavigationManager(getNavigationManager());
  }

  private ListEntry entry;

  public ListEntry getEntry()
  {
    return entry;
  }

  private EntryRenderer2 getRenderer(ListEntry msg)
  {
    if (msg instanceof CardListEntry)
      return new CardRenderer2();
    if (msg instanceof ActionPlanListEntry)
      return new ActionPlanRenderer2();
    // if(msg instanceof UserListEntry)
      return new UserRenderer2();
  }

  private EntryRenderer2 renderer;

  public void setEntry(final ListEntry ent, ListView2 messageList)
  {
    entry = ent;
    //currentMessageList = messageList;
    if (ent != null) {
      renderer = getRenderer(ent);
      removeStyleName("no-message");
      renderer.setMessage(this, ent, messageList, layout);
    }
    else {
      layout.removeAllComponents();
      Label noMessageLbl = new Label("No Message Selected");
      noMessageLbl.setStyleName(Reindeer.LABEL_SMALL);
      noMessageLbl.addStyleName(Reindeer.LABEL_H1);
      layout.addComponent(noMessageLbl);
      addStyleName("no-message");
    }
    updateNewListItems();
  }
  
  public void updateNewListItems()
  {
    String caption = null;
    if (entry != null) {
      Folder folder = (Folder) entry.getParent();

      String extra = "";
      if (folder != null) {
        List<AbstractPojo> siblings = folder.getChildren();
        int index = siblings.indexOf(entry);
        extra = (index + 1) + " of " + siblings.size() + " ";
      }
      caption = extra + getTypeName(entry);
    }
    setCaption(caption);

    NavigationButton backButton = getBackButton();
    if (backButton != null) {
      Component target = backButton.getTargetView();
      if (target != null && target == getPreviousComponent()) {
        backButton.setCaption(getPreviousComponent().getCaption());
      }
    }
  }

  private String getTypeName(ListEntry m)
  {
    if (m instanceof CardListEntry)
      return "Cards";
    if (m instanceof ActionPlanListEntry)
      return "Action Plans";
    // if(m instanceof UserListEntry)
    return "Players";
  }

  private NavigationButton getBackButton()
  {
    NavigationButton backButton = null;
    Iterator<Component> i = getNavigationBar().getComponentIterator();
    while (i.hasNext()) {
      Component comp = i.next();
      if (comp instanceof NavigationButton) {
        backButton = (NavigationButton) comp;
      }
    }
    return backButton;
  }
}
