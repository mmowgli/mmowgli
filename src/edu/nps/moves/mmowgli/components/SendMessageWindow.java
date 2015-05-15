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

package edu.nps.moves.mmowgli.components;

import java.util.List;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.GameLinks;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.utility.MailManager;
/**
 * EditCardTextWindow.java
 * Created on Dec 29, 2010
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class SendMessageWindow extends Window
{
  private static final long serialVersionUID = 5905911799142426074L;
  private Button sendButt,cancelButt;

  private User usr;
  private TextArea ta;
  private TextField subjTf;
  private boolean ccSelf = false;
  private CheckBox ccTroubleListCB;
  private MailManager.Channel channel;
  
  private List<String> emails;
  
  public SendMessageWindow(User user, boolean showCcTrouble)
  {
    this(user,false,MailManager.Channel.BOTH, showCcTrouble);
  }
  
  public SendMessageWindow(User user)
  {
    this(user,false, MailManager.Channel.BOTH);
  }
  
  public SendMessageWindow(User user, boolean ccSelf, MailManager.Channel channel)
  {
    this(user,ccSelf,channel,false);
  }
  
  // For signup emailing...no user accounts
  // This assumes Threadlocal session
  @HibernateSessionThreadLocalConstructor
  public SendMessageWindow(List<String> emails)
  {
    super("A message to mmowgli followers");

    this.emails = emails;
    
    setModal(true);
    VerticalLayout layout = new VerticalLayout();
    setContent(layout);
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setWidth("100%");
    layout.setHeight("100%");
    
    Label lab = new Label(makeString(emails));
    lab.setCaption("To: (other addresses hidden from each recipient)");
    lab.setDescription(lab.getValue().toString());
    lab.addStyleName("m-nowrap");
 //   lab.setHeight("100%"); // makes label clip
    lab.addStyleName("m-greyborder");
    layout.addComponent(lab);
    
    subjTf = new TextField();
    subjTf.setCaption("Subject:");
    subjTf.setWidth("100%");
    
    Game game = Game.getTL();
    String acronym = game.getAcronym();
    acronym = acronym==null?"":acronym+" ";
    subjTf.setValue("Message from "+acronym+"Mmowgli");
    layout.addComponent(subjTf);
    
    ta = new TextArea();
    ta.setCaption("Content: (may include HTML tags)");
    ta.setRows(10);
    ta.setColumns(50);
    ta.setWidth("100%");
    ta.setHeight("100%");
    ta.setInputPrompt("Type message here");
    layout.addComponent(ta);
    layout.setExpandRatio(ta, 1.0f);
    HorizontalLayout buttHL = new HorizontalLayout();
    buttHL.setSpacing(true);

    ClickListener closeListener = new SignupsWindowCloser(this);
    cancelButt = new Button("Cancel", closeListener);
    buttHL.addComponent(cancelButt);

    sendButt = new Button("Send", closeListener);
    sendButt.addClickListener(closeListener);
    buttHL.addComponent(sendButt);
    
    layout.addComponent(buttHL);
    layout.setComponentAlignment(buttHL, Alignment.MIDDLE_RIGHT);
    setWidth("650px");
    setHeight("500px");
    UI.getCurrent().addWindow(this);
    ta.focus();   
  }
  
  private String makeString(List<String>lis)
  {
    StringBuilder sb = new StringBuilder();
    for(String s : lis) {
      sb.append(s);
      sb.append(", ");
    }
    sb.setLength(sb.length()-2);
    return sb.toString();
  }
  
  /* This assumes a threadlocal session in place */
  public SendMessageWindow(User user, boolean ccSelf, MailManager.Channel channel, boolean showCcTroubleList)  
  {
    super("A message to " + user.getUserName());

    this.usr = user;
    this.ccSelf = ccSelf;
    this.channel = channel;
    
    setModal(true);
    VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    layout.setSpacing(true);
    setContent(layout);
    
    subjTf = new TextField();
    subjTf.setCaption("Subject");
    subjTf.setWidth("100%");
    
    User me = Mmowgli2UI.getGlobals().getUserTL();
    Game game = Game.getTL();
    String acronym = game.getAcronym();
    acronym = acronym==null?"":acronym+" ";
    subjTf.setValue(acronym+"Mmowgli message to "+usr.getUserName()+" from "+me.getUserName());
    layout.addComponent(subjTf);
    if(showCcTroubleList) {
      layout.addComponent(ccTroubleListCB = new CheckBox("CC mmowgli trouble list"));
      ccTroubleListCB.setValue(false);
    }
    ta = new TextArea();
    ta.setCaption("Content");
    ta.setRows(10);
    ta.setColumns(50);
    ta.setInputPrompt("Type message here");
    layout.addComponent(ta);

    HorizontalLayout buttHL = new HorizontalLayout();
    buttHL.setSpacing(true);

    WindowCloser closeListener = new WindowCloser(this);
    cancelButt = new Button("Cancel", closeListener);
    buttHL.addComponent(cancelButt);

    sendButt = new Button("Send", closeListener);//new IDButton("Send", SENDPRIVATEMESSAGECLICK, user);
    sendButt.addClickListener(closeListener);
    buttHL.addComponent(sendButt);
    
    layout.addComponent(buttHL);
    layout.setComponentAlignment(buttHL, Alignment.MIDDLE_RIGHT);
    layout.setSizeUndefined(); // does a "pack"

    UI.getCurrent().addWindow(this);
    ta.focus();
  }
  
  @SuppressWarnings("serial")
  private class WindowCloser implements ClickListener
  {
    Window w;
    WindowCloser(Window w)
    {
      this.w = w;
    }
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {     
      if(event.getButton() == sendButt) {
        HSess.init();
        
        String msg = ta.getValue().toString();
        if(msg.length()>0) { 
          MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
          MailManager mmgr = AppMaster.instance().getMailManager();
          User me = globs.getUserTL(); //feb refactor DBGet.getUserTL(globs.getUserID());
          String subj = subjTf.getValue().toString().trim();
          String troubleList = null;
          if(ccTroubleListCB != null) {
            if(ccTroubleListCB.getValue())
              troubleList = GameLinks.getTL().getTroubleMailto();
            else
              troubleList = null;
          }
          mmgr.mailToUserTL(me.getId(), usr.getId(), subj, msg, troubleList, channel);
          
          if(ccSelf)
            mmgr.mailToUserTL(me.getId(), me.getId(), "(CC:)"+subj, msg, null, channel); // why not use MailManager's cc capability?
        }
        HSess.close();
      }
      UI.getCurrent().removeWindow(w);
    }
  }
  
  @SuppressWarnings("serial")
  private class SignupsWindowCloser implements ClickListener
  {
    Window w;
    SignupsWindowCloser(Window w)
    {
      this.w = w;
    }

    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      if(event.getButton() == sendButt) {
        String msg = ta.getValue().toString();
        if(msg.length()>0) { 
          HSess.init();
          String subj = subjTf.getValue().toString().trim();
          for(String recpt : emails) {
            MailManager mgr = AppMaster.instance().getMailManager();
            String retAddr = mgr.buildMmowgliReturnAddressTL();
            mgr.getMailer().send(recpt, retAddr, subj, msg, true);
          }
          HSess.close();
        }
      }
      UI.getCurrent().removeWindow(w);    
    }    
  }
}
