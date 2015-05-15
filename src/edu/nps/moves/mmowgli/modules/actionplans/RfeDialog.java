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

package edu.nps.moves.mmowgli.modules.actionplans;
import static edu.nps.moves.mmowgli.MmowgliEvent.POSTTROUBLECLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.SEARCHCLICK;

import java.util.Set;

import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.IDButton;
import edu.nps.moves.mmowgli.utility.MailManager;

/**
 * RfeDialog.java Created on Apr 5, 2012
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: RfeDialog.java 3361 2014-03-26 19:31:29Z tdnorbra $
 */
public class RfeDialog extends Window
{
  private static final long serialVersionUID = -9098739663225035871L;

  private Button postButt, clearButt;
  private ClickListener clearButtLis;
  private Object apId;
  private TextArea helpWantedTA;
  
  @HibernateSessionThreadLocalConstructor
  @SuppressWarnings("serial")
  public RfeDialog(Object aplnId)
  {
    this.apId = aplnId;

    setCaption("Request for Expertise");
    setModal(true);
    setSizeUndefined();
    setWidth("500px");
    setHeight("400px");

    VerticalLayout vLay = new VerticalLayout();
    setContent(vLay);
    vLay.setMargin(true);
    vLay.setSpacing(true);
    vLay.setSizeFull();

    IDButton searchButt = new IDButton("Option 1: Search for players with needed expertise",SEARCHCLICK,null);
    searchButt.enableAction(false); // do manually
    searchButt.addClickListener(new SearchListener());
    vLay.addComponent(searchButt);

    VerticalLayout nuts = new VerticalLayout();
    vLay.addComponent(nuts);
    nuts.setSizeFull();
    vLay.setExpandRatio(nuts, 1.0f);
    Label lab;
    /*vLay*/nuts.addComponent(lab=new Label("Option 2: Post help-wanted notice to action plan"));
    lab.addStyleName("m-font-bold11");

    final VerticalLayout helpWantedPan = new VerticalLayout();
    /*vLay*/nuts.addComponent(helpWantedPan);
    helpWantedPan.addStyleName("m-greyborder");
    helpWantedPan.setWidth("99%");
    helpWantedPan.setHeight("99%");
    helpWantedPan.setSpacing(true);
    helpWantedPan.setMargin(true);
    /*vLay*/nuts.setExpandRatio(helpWantedPan, 1.0f);

    helpWantedTA = new TextArea("Current posting");
    helpWantedTA.setWidth("100%");
    helpWantedTA.setHeight("100%");
    helpWantedTA.setNullRepresentation("");
    helpWantedPan.addComponent(helpWantedTA);
    helpWantedPan.setExpandRatio(helpWantedTA, 1.0f);

    HorizontalLayout buttLay = new HorizontalLayout();
    helpWantedPan.addComponent(buttLay);
    buttLay.setSpacing(true);
    buttLay.setWidth("100%");

    buttLay.addComponent(lab=new Label());
    lab.setWidth("10px");

    clearButt = new Button("Clear");
    buttLay.addComponent(clearButt);
    clearButt.addClickListener(clearButtLis = new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        ActionPlan ap = ActionPlan.getTL(apId);
        helpWantedTA.setValue(null);
        if(null != ap.getHelpWanted()) {
          ap.setHelpWanted(null);
          ActionPlan.updateTL(ap);
          Notification notif = new Notification("Cleared");
          notif.setDelayMsec(3000);
          notif.show(Page.getCurrent());
          GameEventLogger.logHelpWantedTL(ap);
          notifyAuthorsOfChangeTL(ap);
        }
        HSess.close();
      }
    });

    buttLay.addComponent(lab=new Label());
    buttLay.setExpandRatio(lab, 1.0f);

    postButt = new Button("Post");
    buttLay.addComponent(postButt);
    postButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        Object val = helpWantedTA.getValue();
        if(val == null || val.toString().length()<=0) {
          clearButtLis.buttonClick(event);
          return;
        }
        
        HSess.init();
        String s = val.toString();
        ActionPlan ap = ActionPlan.getTL(apId);
        if(s == null ? ap.getHelpWanted() != null : !s.equals(ap.getHelpWanted())) {
          ap.setHelpWanted(s);
          ActionPlan.updateTL(ap);
          Notification notif = new Notification("Posted");
          notif.setDelayMsec(3000);
          notif.show(Page.getCurrent());
          GameEventLogger.logHelpWantedTL(ap);
          notifyAuthorsOfChangeTL(ap);
        }
        HSess.close();
      }
    });
    buttLay.addComponent(lab=new Label());
    lab.setWidth("10px");

    helpWantedPan.addComponent(lab = new Label());
    lab.setHeight("10px");

    IDButton troubleButt = new IDButton("Option 3: Post Trouble Report",POSTTROUBLECLICK,null);
    troubleButt.enableAction(false); // managed manually
    troubleButt.addClickListener(new TroubleListener());
    vLay.addComponent(troubleButt);

    Button closeButt = new Button("Close");
    vLay.addComponent(closeButt);
    closeButt.addClickListener(new CloseListener());

    vLay.setComponentAlignment(closeButt, Alignment.MIDDLE_RIGHT);

    ActionPlan ap = ActionPlan.getTL(apId);
    String s = ap.getHelpWanted();
    helpWantedTA.setValue(s);
  }

  private void notifyAuthorsOfChangeTL(ActionPlan ap)
  {
    User me = Mmowgli2UI.getGlobals().getUserTL();
    Set<User> authors = ap.getAuthors();
    String message = me.getUserName()+ " changed the \"request-for-expertise\" posting on Action Plan "+ap.getId()+" to:  ";
    message = message + (ap.getHelpWanted()==null?"(removed)":ap.getHelpWanted());

    MailManager mmgr = AppMaster.instance().getMailManager();
    for(User u : authors) {
      mmgr.mailToUserTL(me.getId(), u.getId(), "Action Plan Request-for-Expertise Message Changed", message);
    }
  }

  @SuppressWarnings("serial")
  class CloseListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      UI.getCurrent().removeWindow(RfeDialog.this);
    }
  }
  
  @SuppressWarnings("serial")
  class TroubleListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      Mmowgli2UI.getGlobals().getController().buttonClick(event);   // do manually while window remains
      UI.getCurrent().removeWindow(RfeDialog.this);
    }
  }
  
  @SuppressWarnings("serial")
  class SearchListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      Mmowgli2UI.getGlobals().getController().buttonClick(event);      // do manually while window remains
      UI.getCurrent().removeWindow(RfeDialog.this);
    }
  }
}
