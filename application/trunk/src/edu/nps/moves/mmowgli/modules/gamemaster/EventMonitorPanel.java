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

package edu.nps.moves.mmowgli.modules.gamemaster;

import java.text.SimpleDateFormat;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.dom.Dom;
import org.vaadin.jouni.dom.client.Css;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DB;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.WantsGameEventUpdates;
import edu.nps.moves.mmowgli.utility.MmowgliLinkInserter;
/**
 * CreateActionPlanPanel.java Created on Mar 30, 2011
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: EventMonitorPanel.java 3283 2014-01-16 19:52:05Z tdnorbra $
 */
public class EventMonitorPanel extends VerticalLayout implements MmowgliComponent, WantsGameEventUpdates, ClickListener
{
  private static final long serialVersionUID = -8355423509968486168L;
  private SimpleDateFormat dateFormatter;
  private VerticalLayout vLay;
  public static int MAX_RESULT_SET = 27;
  private int eventCount = 0;
  private Label captionLabel, statsLabel;
  private Panel pan;
  private String PANEL_HEIGHT = "490px";
  private StringBuilder sb;
  private Label newEventLabel;
  private TextArea messageTA;
  @HibernateSessionThreadLocalConstructor
  public EventMonitorPanel()
  {
    sb = new StringBuilder();
    dateFormatter = new SimpleDateFormat("MM/dd HH:mm z");
  }

  @Override
  public void initGui()
  {
    setWidth("950px");
    addStyleName("m-greyborder");
    addStyleName("m-background-lightgrey");
    addStyleName("m-marginleft-25");
    setMargin(true);
    setSpacing(false);

    buildTopInfo(this);

    pan = new Panel();
    addComponent(pan);
    pan.setWidth("99%");
    pan.setHeight(PANEL_HEIGHT);
    vLay = new VerticalLayout();
    vLay.setMargin(true);
    pan.setContent(vLay);

    setComponentAlignment(pan, Alignment.TOP_CENTER);
    pan.addStyleName("m-greyborder");

    NativeButton moreButt = new NativeButton("Get another page of prior events",this);
    addComponent(moreButt);
    setComponentAlignment(moreButt,Alignment.TOP_RIGHT);

    Label lab;
    addComponent(lab=new Label());
    lab.setHeight("10px");
    
    addComponent(new Label("Broadcast message to game masters"));
    
    
    messageTA = new TextArea();
    messageTA.setRows(2);
    messageTA.setWidth("100%");

    addComponent(messageTA);
    
    NativeButton sendButt = new NativeButton("Send",new SendListener());
    addComponent(sendButt);
    
    loadEvents();
   }

  class SendListener implements ClickListener
  {
    private static final long serialVersionUID = 1L;

    @Override
    public void buttonClick(ClickEvent event)
    {
      String msg = messageTA.getValue();
      if(msg.length()<5)
        Notification.show("Message not sent.", "Your message is too short to be useful.", Notification.Type.ERROR_MESSAGE);
      else if(msg.length()>255)
        Notification.show("Message not sent.", "Limit your message to 255 characters. ("+msg.length()+")", Notification.Type.ERROR_MESSAGE);
       
      else {
        HSess.init();
        User u = Mmowgli2UI.getGlobals().getUserTL();
        GameEventLogger.logGameMasterBroadcastTL(GameEvent.EventType.MESSAGEBROADCASTGM, msg, u);
        HSess.close();
      }
      messageTA.selectAll();
    }    
  }
  
  // Want to see more
  @Override
  @MmowgliCodeEntry
  @HibernateOpened
  @HibernateClosed
  public void buttonClick(ClickEvent event)
  {
    int numLines = vLay.getComponentCount();
    if(numLines <= 0)
      return;
    HSess.init();
    EventLine line = (EventLine)vLay.getComponent(numLines-1);
    GameEvent[] arr = AppMaster.instance().getMcache().getNextGameEvents(numLines-1, line.gameEvent.getId(), MAX_RESULT_SET);

    if(arr.length != MAX_RESULT_SET)
      event.getButton().setEnabled(false);

    addEvents(arr);
    HSess.close();
   }

  private void buildTopInfo(VerticalLayout vertL)
  {
    captionLabel = new HtmlLabel("dummy");
    vertL.addComponent(captionLabel);
     
    HorizontalLayout bottomHL = new HorizontalLayout();
    bottomHL.setMargin(false);
    bottomHL.setWidth("100%");
    vertL.addComponent(bottomHL);

    statsLabel = new HtmlLabel("dummy");
    statsLabel.setSizeUndefined();
    bottomHL.addComponent(statsLabel);

    Label lab;
    bottomHL.addComponent(lab=new Label());
    lab.setWidth("1px");
    bottomHL.setExpandRatio(lab, 1.0f);
    
    newEventLabel =new HtmlLabel("new event&nbsp;&nbsp;");  // safari cuts off tail
    newEventLabel.setSizeUndefined();
    newEventLabel.addStyleName("m-newcardpopup");
    newEventLabel.setImmediate(true);
    bottomHL.addComponent(newEventLabel);
    Animator.animate(newEventLabel,new Css().opacity(0.0d));   // hide it
  }
  
  private void setTopInfo()
  {
    sb.setLength(0);

    Criteria criteria = HSess.get().createCriteria(User.class);
    criteria.setProjection(Projections.rowCount());
    int count = ((Long) criteria.list().get(0)).intValue();
    sb.append("<html><b>");
    sb.append(count);
    sb.append("</b> player");
    if (count != 1) sb.append("s");
    sb.append(" registered, <b>");

    count = Mmowgli2UI.getGlobals().getSessionCount();
    sb.append(count);
    sb.append("</b> player");
    if (count != 1) sb.append("s");
    sb.append(" online, <b>");

    int mov = Game.getTL().getCurrentMove().getNumber();
    criteria = HSess.get().createCriteria(Card.class)
        .createAlias("createdInMove", "MOVE")
        .add(Restrictions.eq("MOVE.number", mov))
        .setProjection(Projections.rowCount());
    count = ((Long) criteria.list().get(0)).intValue();
    sb.append(count);
    sb.append("</b> Idea Card");
    if (count != 1) sb.append("s");
    sb.append(" played during this round, ");

      if (Game.getTL().isActionPlansEnabled()) {
          criteria = HSess.get().createCriteria(ActionPlan.class)
              .createAlias("createdInMove", "MOVE")
              .add(Restrictions.eq("MOVE.number", mov))
              .setProjection(Projections.rowCount());
          count = ((Long) criteria.list().get(0)).intValue();
          sb.append("<b>");
          sb.append(count);
          sb.append("</b>");
          sb.append(" Action Plan");
          if (count != 1) {
              sb.append("s");
          }
          sb.append(" created during this round, ");
      }

      String space = " ";
      count = Game.getTL().getMaxUsersRegistered();
      sb.append("<html>");
      sb.append("<b>");
      sb.append(count);
      sb.append("</b>");
      sb.append(space);
      sb.append("player");
      if (count != 1) {
          sb.append("s");
      }
      sb.append(space);
      sb.append("currently");
      sb.append(space);
      sb.append("allowed");

    statsLabel.setValue(sb.toString());
  }

  private void setCaptionPrivate()
  {
    sb.setLength(0);
    sb.append("<b>Game Master Events Log</b> -- showing most recent ");
    sb.append(eventCount);
    sb.append(" events");
    captionLabel.setValue(sb.toString());
  }

  public void addEvent(GameEvent e)
  {
    addEventCommon(new EventLine(e),true);
  }
  public void addEventOobTL(GameEvent e)
  {
    addEventCommon(new EventLine(e,HSess.get()),true);
  }

  public void addEvent(GameEvent e, boolean head)
  {
    addEventCommon(new EventLine(e),head);
  }

  private void addEventCommon(EventLine el, boolean head)
  {
    if(head) {
      vLay.addComponent(el, 0);
    }
    else {
      vLay.addComponent(el);
    }
    vLay.setComponentAlignment(el, Alignment.TOP_LEFT);
    eventCount++;
  }

  @SuppressWarnings("serial")
  class EventLine extends HtmlLabel
  {
    public GameEvent gameEvent;
    public EventLine(GameEvent e)
    {
      this(e,null);
    }
    @HibernateSessionThreadLocalConstructor
    public EventLine(GameEvent e, Session sess)
    {
      gameEvent = e;
      String dt = dateFormatter.format(e.getDateTime());
      String typ = e.getEventtype().description();
      String des = e.getDescription();

      if(sess == null)
        sess = HSess.get();
      des = MmowgliLinkInserter.insertLinksOob(des, null, sess);

      long id  = e.getId();

      sb.setLength(0);
      sb.append("<b>");
      sb.append(id);
      sb.append("</b> <i>");
      sb.append(dt);
      sb.append("</i> <b>");
      sb.append(typ);
      sb.append("</b> ");
      sb.append(des);

      setValue(sb.toString());
      setDescription(sb.toString());
    }
  }

  private void addEvents(GameEvent[] arr)
  {
    for(GameEvent ge :arr) {
      addEvent(ge,false);
    }
    setCaptionPrivate();
    setTopInfo();
  }

  private void loadEvents()
  {
    addEvents(AppMaster.instance().getMcache().getNextGameEvents(null, null, MAX_RESULT_SET));
  }

  @Override
  public boolean gameEventLoggedOobTL(Object evId)
  {
    GameEvent ev = DB.getRetry(GameEvent.class, evId, null, HSess.get());
    if(ev == null) {
      System.err.println("ERROR: EventMonitorPanel.gameEventLoggedOobTL(): GameEvent matching id "+evId+" not found in db.");
      return false;
    }
    addEventOobTL(ev);
    setCaptionPrivate();
    showNotif();
    return true;
  }

  private void showNotif()
  {
    new Dom(newEventLabel).getStyle().opacity(1.0d);
    Animator.animate(newEventLabel, new Css().opacity(0.0d)).delay(3000).duration(1500);
  }
}
