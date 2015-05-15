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

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.components.Hr;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgliMobile.data.ActionPlanListEntry;
import edu.nps.moves.mmowgliMobile.data.ListEntry;
import edu.nps.moves.mmowgliMobile.data.UserListEntry;

/**
 * ActionPlanRenderer.java
 * Created on Feb 24, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanRenderer2 extends EntryRenderer2
{
  private Component makeLabel(String s)
  {
    Label lab = new Label(s);
    lab.setSizeUndefined();
    lab.addStyleName("m-actionplan-label");
    return lab;
  }
  private Component makeText(String s)
  {
    Label lab = new Label(s);
    lab.addStyleName("m-actionplan-text");
    return lab;  
  }
  private Component makeAuthors(String str, FullEntryView2 fullView)
  {
    authorListener.setView(fullView);
    String[] sa = str.split(",");
    VerticalLayout vlay = new VerticalLayout();
    HorizontalLayout horl = new HorizontalLayout();  // won't wrap, so add new one every other time
    int i=0;
    for(String s : sa) {
      Button b = new Button(s);
      b.addClickListener(authorListener);
      b.addStyleName("m-author-button");
      horl.addComponent(b);
      if(++i % 2 == 0) {
        vlay.addComponent(horl);
        horl = new HorizontalLayout();
      }
    }
    if(horl.getComponentCount() >0)
      vlay.addComponent(horl);
    return vlay;
    /*
    s = s.replaceAll(",",", ");  // add a space
    Label lab = new Label(s);
    lab.addStyleName("m-actionplan-text-authors");
    return lab;  */
  }
  private Component makeHr()
  {
    Label lab = new Hr();
    lab.addStyleName("m-actionplan-hr");
    return lab;
  }
  public void setMessage(FullEntryView2 mView, ListEntry message, ListView2 messageList, AbstractOrderedLayout layout)
  {
    ActionPlanListEntry wap = (ActionPlanListEntry) message;
    ActionPlan ap = wap.getActionPlan();

    layout.removeAllComponents();

    layout.addComponent(makeLabel("Title"));
    layout.addComponent(makeText(ap.getTitle()));
    layout.addComponent(makeHr());

    layout.addComponent(makeLabel("Authors"));
    layout.addComponent(makeAuthors(ap.getQuickAuthorList(),mView));
    layout.addComponent(makeHr());

    layout.addComponent(makeLabel("Who is involved?"));
    layout.addComponent(makeText(ap.getSubTitle()));
    layout.addComponent(makeHr());

    layout.addComponent(makeLabel("What is it?"));
    layout.addComponent(makeText(ap.getWhatIsItText()));
    layout.addComponent(makeHr());

    layout.addComponent(makeLabel("What will it take?"));
    layout.addComponent(makeText(ap.getWhatWillItTakeText()));
    layout.addComponent(makeHr());

    layout.addComponent(makeLabel("How will it work?"));
    layout.addComponent(makeText(ap.getHowWillItWorkText()));
    layout.addComponent(makeHr());

    layout.addComponent(makeLabel("How will it change the situation?"));
    layout.addComponent(makeText(ap.getHowWillItChangeText()));

  }
  
  private AuthorListener authorListener = new AuthorListener();
  @SuppressWarnings("serial")
  class AuthorListener implements ClickListener
  {
    NavigationView view;
    public void setView(NavigationView nv)
    {
      view = nv;
    }
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      String authorName = event.getButton().getCaption();
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
