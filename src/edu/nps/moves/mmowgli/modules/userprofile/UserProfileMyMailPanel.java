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

package edu.nps.moves.mmowgli.modules.userprofile;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.Session;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.SendMessageWindow;
import edu.nps.moves.mmowgli.components.ToggleLinkButton;
import edu.nps.moves.mmowgli.db.Message;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.WantsMessageUpdates;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPageCommentPanel2.ActionPlanComment;

/**
 * UserProfileMyBuddiesPanel.java
 * Created on Mar 15, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserProfileMyMailPanel extends UserProfileTabPanel implements ItemClickListener, WantsMessageUpdates
{
  private static final long serialVersionUID = 6712398487478286813L;
  private Panel mailPanel;
  private VerticalLayout panelVL;
  private boolean showingHiddenMsgs = false;
  private ToggleLinkButton showButt;

  @HibernateSessionThreadLocalConstructor
  public UserProfileMyMailPanel(Object uid)
  {
    super(uid);
    if(!userIsMe)
      return;
    mailPanel = new Panel();
    mailPanel.setSizeFull();
    mailPanel.setContent(panelVL=new VerticalLayout());
    panelVL.setSpacing(false);//(true);
  }

  @Override
  public void initGui()
  {
    super.initGui();

    String left = "Players can choose to receive messages in-game or externally (or both or neither).<br/><br/>Player messages are private and"+
    " actual email identities are hidden.<br/><br/>You can find another player's profile by clicking on their name or using the search"+
    " feature. Then you may send a message to that player by clicking on the "+
    "<i>Send player private email</i> link.<br/><br/>If this link is not present or is disabled, then the player has opted "+
    "to receive neither email nor in-game messages.<br/><br/>";  
    getLeftLabel().setValue(left);
    
    if (!userIsMe) {
      User u = User.getTL(uid);
      if (u.isOkEmail() || u.isOkGameMessages()) {
        final NativeButton sendEmailButt = new NativeButton("Send private mail to this user");
        sendEmailButt.addStyleName(BaseTheme.BUTTON_LINK);
        sendEmailButt.addStyleName("m-userProfile3-sendmail-button");
        Label sp;
        VerticalLayout vl =getRightLayout();
   
        vl.setSizeUndefined();
        vl.setWidth("100%");
        vl.addComponent(sp = new Label());
        sp.setHeight("50px");
        vl.addComponent(sendEmailButt);
        vl.setComponentAlignment(sendEmailButt, Alignment.MIDDLE_CENTER);
        vl.addComponent(sp = new Label());
        sp.setHeight("1px");
        vl.setExpandRatio(sp, 1.0f);

        sendEmailButt.addClickListener(new ClickListener()
        {
          private static final long serialVersionUID = 1L;
          @Override
          @MmowgliCodeEntry
          @HibernateOpened
          @HibernateClosed
          public void buttonClick(ClickEvent event)
          {
            HSess.init();

            User u = User.getTL(uid);
            if (u.isOkEmail() || u.isOkGameMessages())  // redundant here
              new SendMessageWindow(u,imAdminOrGameMaster);
            else
              Notification.show("Sorry", "Player " + u.getUserName() + " does not receive mail.", Notification.Type.WARNING_MESSAGE);

            HSess.close();
          }
        });
      }
      return;
    }
    VerticalLayout rightVL = getRightLayout();
    rightVL.setSizeUndefined();
    rightVL.setWidth("100%");

    getLeftAddedVerticalLayout().addComponent(showButt=new ToggleLinkButton("View all","View unhidden only",null)); //,ttArray));
    showButt.addOnListener(new ViewAllListener());
    showButt.addOffListener(new ViewUnhiddenOnlyListener());
    showButt.setToolTips("Temporarily show all messages, including those marked \"hidden\"", "Temporarily hide messages marked \"hidden\"");
    Label sp;
    rightVL.addComponent(sp = new Label());
    sp.setHeight("10px");
    rightVL.addComponent(mailPanel);
 
    User me = Mmowgli2UI.getGlobals().getUserTL();
    Set<Message> msgs = me.getGameMessages();
    if (msgs.size() > 0) {
      Message[] msgsAr = new Message[msgs.size()];
      msgsAr = msgs.toArray(msgsAr); // avoid concurrent mod
      int num = msgsAr.length;// .size();
      int n = num;
      for (Message m : msgsAr) {
        addOneMessage(m, n--, num, null);
      }
    }
  }
  
  @SuppressWarnings("serial")
  class MyActionPlanComment extends ActionPlanComment
  {
    public MyActionPlanComment(Integer order, Integer total, Message msg, boolean showHideButton)
    {
      super(order,total,msg,showHideButton, null, true);
    }

    @Override
    protected void hideClickedTL()
    { 
      super.hideClickedTL();
      handleVisible(this);
    }   
  }
  
  private void addOneMessage(Message msg, int idx, int total, Integer position)
  {
    addOneMessageCommon(msg,idx,total,position,null);
  }

  private void addOneMessage_oobTL(Message msg, int idx, int total, Integer position)
  {
    addOneMessageCommon(msg,idx,total,position,HSess.get());
  }
  private void addOneMessageCommon(Message msg, int idx, int total, Integer position, Session sess)
  {
    ActionPlanComment comment = new MyActionPlanComment(idx,total,msg,true);  // show indiv show/hide button link
    if(position == null)
      panelVL.addComponent(comment);
    else
      panelVL.addComponent(comment,position);

    comment.initGui(sess);
    comment.setWidth("658px"); //"665px"); // same as above

    handleVisible(comment);
  }

  private void adjustTotals(ActionPlanComment apc, int newtotal)
  {
    apc.setTotal(newtotal);
  }

  @Override
  public boolean messageCreated_oobTL(Serializable uId)
  {
    Message msg = (Message)HSess.get().get(Message.class, (Serializable)uId);
    if(!userIsMe || msg.getToUser().getId() != (Long)Mmowgli2UI.getGlobals().getUserID())  // If I'm displaying some other user, or the message is not for me, bail
      return false;
    
    int oldtotal = panelVL.getComponentCount();
    int newtotal = oldtotal+1;
    for(int i=0;i<oldtotal;i++)
      adjustTotals((ActionPlanComment)panelVL.getComponent(i),newtotal);

    addOneMessage_oobTL(msg,newtotal,newtotal,0);
    return true;
  }

  @Override
  public void itemClick(ItemClickEvent event)
  {
  }

  private void handleVisible(ActionPlanComment apc)
  {
    apc.setVisible(showingHiddenMsgs || !apc.getMessageObject().isHidden());
  }
  private void showMsgs()
  {
    Iterator<Component> itr = panelVL.iterator();
    while(itr.hasNext()) {
      handleVisible((ActionPlanComment)itr.next());
    }
  }

  @SuppressWarnings("serial")
  class ViewAllListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      showingHiddenMsgs = true;
      showMsgs();
    }
  }

  @SuppressWarnings("serial")
  class ViewUnhiddenOnlyListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      showingHiddenMsgs = false;
      showMsgs();
    }
  }
}
