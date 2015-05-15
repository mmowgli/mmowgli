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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.Pages.PagesData;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.utility.MailManager;

/**
 * HelpWantedDialog.java Created on Apr 19, 2012
 * Updated Mar 14, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class HelpWantedDialog extends Window
{
  private static final long serialVersionUID = -7479926071207399556L;
  
  private String msg1 = "Action plan authors have posted this help-wanted message:";
  private String msg2 = "Want to help develop this action plan?  Drop the authors a note to say why.  "+
                        "We want to hear what you bring to the table!";
  
  public HelpWantedDialog(Object aplnId)
  {
    this(aplnId,false);
  }
  
  @SuppressWarnings("serial")
  @HibernateSessionThreadLocalConstructor
  public HelpWantedDialog(final Object aplnId, boolean interested)
  {
    setCaption(interested?"Express Interest in Action Plan":"Offer Assistance with Action Plan");
    setModal(true);
    setSizeUndefined();
    setWidth("500px");
    setHeight("550px");

    VerticalLayout vLay = new VerticalLayout();
    setContent(vLay);
    vLay.setMargin(true);
    vLay.setSpacing(true);
    vLay.setSizeFull();

    StringBuilder sb = new StringBuilder();

    ActionPlan ap = ActionPlan.getTL(aplnId);
    String s = ap.getHelpWanted();

    if(s != null) {
      vLay.addComponent(new Label(msg1));
      Label helpWantedLab = new Label(s);
      helpWantedLab.addStyleName("m-helpWantedLabel");
      helpWantedLab.setWidth("100%");
      vLay.addComponent(helpWantedLab);
    }

    vLay.addComponent(new Label(msg2));
    final TextArea toTA = new TextArea("To");
    toTA.addStyleName("m-textareaboldcaption");
    toTA.setWidth("100%");
    toTA.setRows(1);
    toTA.setNullRepresentation("");
    toTA.setValue(getAuthors(sb,ap));
    vLay.addComponent(toTA);

    final TextArea ccTA = new TextArea("CC");
    ccTA.addStyleName("m-textareaboldcaption");
    ccTA.setWidth("100%");
    ccTA.setRows(1);
    ccTA.setNullRepresentation("");

    PagesData pd = new PagesData();
    ccTA.setValue(pd.gettroubleMailto());
    
    vLay.addComponent(ccTA);

    final TextArea subjTA = new TextArea("Subject");
    subjTA.addStyleName("m-textareaboldcaption");
    subjTA.setWidth("100%");
    subjTA.setRows(2);
    subjTA.setNullRepresentation("");
    sb.setLength(0);
    sb.append("My interest in Action Plan ");
    sb.append(ap.getId());
    sb.append(", \"");
    sb.append(ap.getTitle());
    sb.append('"');
    subjTA.setValue(sb.toString());
    vLay.addComponent(subjTA);

    final TextArea msgTA = new TextArea("Message");
    msgTA.addStyleName("m-textareaboldcaption");
    msgTA.setWidth("100%");
    msgTA.setHeight("100%");
    msgTA.setNullRepresentation("");
    vLay.addComponent(msgTA);
    vLay.setExpandRatio(msgTA, 1.0f);

    HorizontalLayout buttLay = new HorizontalLayout();
    vLay.addComponent(buttLay);
    buttLay.setSpacing(true);
    buttLay.setWidth("100%");
    Label sp;
    buttLay.addComponent(sp=new Label());
    sp.setHeight("1px");
    buttLay.setExpandRatio(sp, 1.0f);

    Button canButt = new Button("Cancel");
    buttLay.addComponent(canButt);

    Button sendButt = new Button("Send to authors");
    buttLay.addComponent(sendButt);

    canButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        UI.getCurrent().removeWindow(HelpWantedDialog.this);        
      }
    });

    sendButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        Object tos = toTA.getValue();
        if(tos == null || tos.toString().length()<=0) {
          Notification.show("No recipients", Notification.Type.ERROR_MESSAGE);
          return;
        }
        Object cc = ccTA.getValue();
        if(cc == null || cc.toString().length()<=0)
          cc = null;
               
        Object msg = msgTA.getValue();
        if(msg == null || msg.toString().length()<=0) {
          Notification.show("No Message", Notification.Type.ERROR_MESSAGE);
          return;
        }
        Object subj = subjTA.getValue();
        if(subj == null)
          subj = "";

        HSess.init();
        
        List<User> authors = parseAuthorsTL(tos.toString().trim());
        MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
        MailManager mmgr = AppMaster.instance().getMailManager();
        User me = globs.getUserTL();
        for(User u : authors) {
          mmgr.mailToUserTL(me.getId(), u.getId(), subj.toString(), msg.toString());
        }

        if(cc == null)
          mmgr.mailToUserTL(me.getId(), me.getId(), "(CC:)"+subj.toString(), msg.toString());
        else
          mmgr.mailToUserTL(me.getId(), me.getId(), subj.toString(), msg.toString(), cc.toString(), MailManager.Channel.BOTH);  // the cc is an email, not a user name

        UI.getCurrent().removeWindow(HelpWantedDialog.this); 
        Notification.show("Message(s) sent",Notification.Type.HUMANIZED_MESSAGE); // fixed 21 Jan 2015

        HSess.close();
      }
    });
  }
  
  private List<User> parseAuthorsTL(String s)
  {
    ArrayList<User> lis = new ArrayList<User>();
    StringBuilder sb = new StringBuilder();
   // String[] sa = s.split("([.,!?:;'\"-]|\\s)+"); // split by white and punct
    String[] sa = s.split("([,!?:;'\"-]|\\s)+");  // allow . in name
    for(String str : sa) {
      User u = User.getUserWithUserNameTL(str);
      if(u != null)
        lis.add(u);
      else {
        sb.append(str);
        sb.append(' ');
      }
    }
    String nono=sb.toString();
    if(nono.length()>0)
      Notification.show("User(s) "+nono+" not found", Notification.Type.ERROR_MESSAGE);
    return lis;
  }
  
  private String getAuthors(StringBuilder sb, ActionPlan ap)
  {
    sb.setLength(0);
    Set<User> set = ap.getAuthors();
    for(User u : set) {
      sb.append(u.getUserName());
      sb.append(' ');
    }
    return sb.toString().trim();
  }
}
