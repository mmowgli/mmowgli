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

import static edu.nps.moves.mmowgli.MmowgliConstants.HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Set;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardMarking;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.db.GameEvent;
import edu.nps.moves.mmowgli.db.GameEvent.EventType;
import edu.nps.moves.mmowgli.db.Message;
import edu.nps.moves.mmowgli.db.MessageUrl;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateUserRead;
import edu.nps.moves.mmowgli.utility.MmowgliMailer;
import edu.nps.moves.mmowgli.utility.MmowgliMailer.QPacket;

/**
 * GameEventLogger.java
 * Created on May 3, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class GameEventLogger
{
  private static String svrName;
  private static int SVRNAME_LIMIT = 5;
  static {
    svrName = AppMaster.instance().getServerName();
    if(svrName == null)
      svrName = "";
    else {
      int idx = -1;
      if((idx = svrName.indexOf('.')) >0)
        svrName = svrName.substring(0,idx);
      if(svrName.length()> SVRNAME_LIMIT)
        svrName = svrName.substring(0,SVRNAME_LIMIT);        
    }
  }

  public static void cardPlayedTL(Card cd)
  {
    CardType ct = cd.getCardType();
    GameEvent ge;
    StringBuilder sb = new StringBuilder();
    sb.append(" on ");
    sb.append(svrName);
    sb.append(" / ");
    sb.append("card " + cd.getId());
    if (!ct.isIdeaCard()) {
      Card p = cd.getParentCard();
      if (p != null) { // obsessive checking !!
        sb.append(" on card ");
        sb.append(p.getId());
      }
    }
    sb.append(" by user ");
    sb.append(cd.getAuthor().getId()); //.getAuthor().getUserName());
    sb.append(" / ");
    sb.append(cd.getText());
    
    if (ct.isIdeaCard())
      ge = new GameEvent(GameEvent.EventType.IDEACARDPLAYED, sb.toString());
    else
      ge = new GameEvent(GameEvent.EventType.CHILDCARDPLAYED, sb.toString());

    GameEvent.saveTL(ge);
  }

  public static void cardTextEdittedTL(Card c, User u)
  {
    cardChangedCommonTL(GameEvent.EventType.CARDTEXTEDITED,c,u,null);
  }
   
  public static void cardMarkedTL(Card c, User u, Set<CardMarking> oldMarking)
  {
    cardChangedCommonTL(GameEvent.EventType.CARDMARKED,c,u,oldMarking);
  }
  
  private static void cardChangedCommonTL(GameEvent.EventType typ, Card c, User marker, Set<CardMarking> oldMarking)
  {
    Set<CardMarking> cm = c.getMarking();
    StringBuilder sb = new StringBuilder();
    sb.append(" ");
    sb.append(svrName);
    sb.append(" / ");
    sb.append("card "+c.getId());
    sb.append(" / user ");

    sb.append(marker.getId());
    //sb.append(marker.getUserName());
    sb.append(" / ");
    if(cm == null || cm.size()<=0) {
    	if(oldMarking != null && oldMarking.size()>0)
    		sb.append("unmarked / former marking: "+oldMarking.iterator().next().getLabel());
    	else
        sb.append("unmarked");
    }
    else
      sb.append(cm.iterator().next().getLabel());
    sb.append(" / ");
    sb.append(c.getText());
    
    GameEvent ev = new GameEvent(typ,sb.toString());
    GameEvent.saveTL(ev);
  }
  
  // Don't get User from db, because it might have just been updated in this thread and the get would reverse that
  public static void logGameDesignChangeTL(String field, String value, Object uid)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.GAMEDESIGNEDITED,"/ "+svrName+" user "+uid+ " / "+field+" / "+value);
    GameEvent.saveTL(ev);
  }

  // Don't get User from db, because it might have just been updated in this thread and the get would reverse that

  public static void logUserNameChangedTL(Object uid, Object changer, String oldName, String newName)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.USERGAMENAMECHANGED,"/ "+svrName+" userId "+uid+" old: "+oldName+" new: "+newName+" by userId "+changer);    
    GameEvent.saveTL(ev);
  }
  
  @HibernateUserRead
  public static void logHelpWantedTL(ActionPlan ap)
  {
    Serializable uid = Mmowgli2UI.getGlobals().getUserID();
    User me = User.getTL(uid);
    String s = ap.getHelpWanted();
    if(s == null)
      s = "(removed)";
    GameEvent ev = new GameEvent(GameEvent.EventType.ACTIONPLANHELPWANTED,"/ "+ap.getId()+" / user "+me.getId()+" / "+s);
    GameEvent.saveTL(ev);
  }
  
  private static String getUserString(GameEvent.EventType typ, User u)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(" user ");
    sb.append(u.getId()); //getUserName());
    sb.append(" on ");
    sb.append(svrName); //ApplicationSessionGlobals.SERVERNAME);
    sb.append(" from ");
    sb.append(u.getLocation());
    sb.append(" / ");
    String browAddr = Mmowgli2UI.getGlobals().getBrowserAddress();
    sb.append(browAddr==null?"null":browAddr);
    
   // if(typ == GameEvent.EventType.USERLOGIN && u.isViewOnly())  // guest
   //   sb.append(" (Cannot see current cards if PREPARE phase and round > 1)");
    
    return sb.toString();
  }
  
  public static void logUserLoginTL(User u)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.USERLOGIN, " "+getUserString(GameEvent.EventType.USERLOGIN,u));
    GameEvent.saveTL(ev);
  }

  public static void logUserLogoutTL(User u)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.USERLOGOUT, " "+getUserString(GameEvent.EventType.USERLOGOUT,u));
    GameEvent.saveTL(ev);
  }

  public static void logLoginLimitChangeTL(int old, int newly)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.LOGINLIMITCHANGE," / old: "+old+" new: "+newly);
    GameEvent.saveTL(ev);
  }
  
  public static void logGameMasterCommentTL(String comment, User u)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.GAMEMASTERNOTE,"/  "+svrName+" (user "+u.getId()+") "+comment);
    GameEvent.saveTL(ev);
  }
 
  public static void logGameMasterBroadcastTL(EventType typ, String msg, User u)
  {
    GameEvent ev = new GameEvent(typ," From user "+u.getId()+": "+msg);
    GameEvent.saveTL(ev);   
  }
  
  public static void logApplicationLaunch()
  {
    String SERVERNAME = "";
    try
    {
      InetAddress addr = InetAddress.getLocalHost();
      SERVERNAME = addr.getHostName();
    }
    catch(Exception e)
    {
      System.err.println("Can't look up host name in GameEventLogger");
    }
    HSess.init();
    HSess.get().getTransaction().setTimeout(HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS);
    GameEvent ev = new GameEvent(GameEvent.EventType.APPLICATIONSTARTUP,"/ "+SERVERNAME);
    GameEvent.saveTL(ev);
    HSess.close();   
  }

  private static String clampTitleLength(String s)
  {
    String ret = (s==null?"":s);
    if(ret.length() > 70)
      ret = ret.substring(0, 69)+"...";
   return ret;
  }

  public static void logActionPlanCreationTL(ActionPlan ap)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.ACTIONPLANCREATED," / action plan "+ap.getId()+" created, \""+clampTitleLength(ap.getTitle())+"\"");
    GameEvent.saveTL(ev);    
  }

  public static void logActionPlanUpdateTL(ActionPlan ap, String field, long id)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.ACTIONPLANUPDATED," / action plan "+ap.getId()+" by user "+id+", "+field);
    GameEvent.saveTL(ev);    
  }
  
  public static void logActionPlanInvitationExtendedTL(ActionPlan ap, String extender, String extendee)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.ACTIONPLANUPDATED," / action plan "+ap.getId()+" author invitation extended by user "+extender+" to "+extendee);
    GameEvent.saveTL(ev);        
  }
  
  public static void logActionPlanInvitationAcceptedTL(ActionPlan ap, String accepter)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.ACTIONPLANUPDATED," / action plan "+ap.getId()+" author invitation accepted by user "+accepter);
    GameEvent.saveTL(ev);        
  }
  
  public static void logActionPlanInvitationDeclinedTL(ActionPlan ap, String decliner)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.ACTIONPLANUPDATED," / action plan "+ap.getId()+" author invitation declined by user "+decliner);
    GameEvent.saveTL(ev);        
  }
  
  public static void logActionPlanImageAddedTL(ActionPlan ap, String adder, String title)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.ACTIONPLANUPDATED," / action plan "+ap.getId()+" image added by user "+adder);
    GameEvent.saveTL(ev);        
  }
  
  public static void logActionPlanVideoAddedTL(ActionPlan ap, String adder, String title)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.ACTIONPLANUPDATED," / action plan "+ap.getId()+" video added by user "+adder);
    GameEvent.saveTL(ev);        
  }
  
  public static void logSessionTimeoutL(User u)
  {
     GameEvent ev = new GameEvent(GameEvent.EventType.SESSIONEND," "+svrName+" / user "+u.getId()+" / "+u.getLocation());
     GameEvent.saveTL(ev);
  }

  public static void logRegistrationAttemptTL(String email)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.REGISTRATIONATTEMPT, " by <a href='mailto:"+email+"'>"+email+"</a>");
    GameEvent.saveTL(ev);
  }
  
  public static void updateBlogHeadlineTL(String txt, String tooltip, String url, long uid)
  {
    if(url!=null && url.length()>255)  // db limit
      url = url.substring(0, 255);
    
    MessageUrl mu = new MessageUrl(txt,url);
    mu.setTooltip(tooltip);
    MessageUrl.saveTL(mu);
    long id = mu.getId();
    txt = txt==null?" (removed)":txt;
    tooltip = tooltip==null?"":tooltip;
    url = url==null?"":url;
    StringBuilder sb = new StringBuilder(txt);
    sb.append(" / (tooltip:) ");
    sb.append(tooltip);
    sb.append(" / (url:) ");
    sb.append(url);
    GameEvent ev = new GameEvent(GameEvent.EventType.BLOGHEADLINEPOST," by user "+uid+" / "+ sb.toString(),id);
    GameEvent.saveTL(ev);
  }
  
  public static void logRequestReportGenerationTL(User u)
  {
     GameEvent ev = new GameEvent(GameEvent.EventType.REPORTGENERATIONREQUESTED, " by "+u.getUserName());
     GameEvent.saveTL(ev);
  }
  
  public static void logEndReportGenerationTL()
  {
    logReportGenerationTL(" completed");
  }
  
  public static void logBeginReportGenerationTL()
  {
    logReportGenerationTL(" begun");  
  }
  
  private static void logReportGenerationTL(String txt)
  {
    String url = AppMaster.instance().getAppUrlString();
    if(!url.endsWith("/"))
      url = url+"/";
    txt = txt + " "+url+"reports";
    
    GameEvent ev = new GameEvent(GameEvent.EventType.AUTOREPORTGENERATION, txt);
    GameEvent.saveTL(ev);
  }

  public static void logNewUserTL(User user)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(" / user ");
    sb.append(user.getId()); //user.getUserName());
    sb.append(" / ");
    sb.append(user.getLocation());
    sb.append(" / ");
    sb.append(user.getExpertise());
    sb.append(" / \"");
    sb.append(user.getAnswer());
    sb.append("\"");
    GameEvent ev = new GameEvent(GameEvent.EventType.USERNEW, sb.toString());
    GameEvent.saveTL(ev);   
  }

  // Don't get ActionPlan from db, because it might have just been updated in this thread and the get would reverse that
  public static void commentMarkedSuperInterestingTL(String user, Object apId, Message msg, boolean superInteresting)
  {
    StringBuilder sb = new StringBuilder(user);
    sb.append(" / marked a comment in action plan ");
    sb.append(apId.toString());
    sb.append(superInteresting?"":" NOT");
    sb.append(" super-interesting");
    GameEvent ev = new GameEvent(GameEvent.EventType.COMMENTSUPERINTERESTING,sb.toString());
    GameEvent.saveTL(ev);    
  }

  // Don't get ActionPlan from db, because it might have just been updated in this thread and the get would reverse that
  public static void commentTextEdittedTL(String userName, Object apid, Message msg)
  {
    StringBuilder sb = new StringBuilder(userName);
    sb.append(" edited a comment in action plan ");
    sb.append(apid.toString());
    sb.append(": ");
    sb.append(msg.getText());
    GameEvent ev = new GameEvent(GameEvent.EventType.COMMENTEDITED,sb.toString());
    GameEvent.saveTL(ev);
  }
  
  // Don't get ActionPlan from db, because it might have just been updated in this thread and the get would reverse that
  public static void chatTextEdittedTL(String userName, Object apid, Message msg)
  {
    StringBuilder sb = new StringBuilder(userName);
    sb.append(" edited a chat in action plan ");
    sb.append(apid.toString());
    sb.append(": ");
    sb.append(msg.getText());
    GameEvent ev = new GameEvent(GameEvent.EventType.CHATEDITED,sb.toString());
    GameEvent.saveTL(ev);
  }

  public static void logUserPasswordChangedTL(User user)
  {
    StringBuilder sb = new StringBuilder(user.getUserName());
    sb.append(" changed his/her password");
    GameEvent ev = new GameEvent(GameEvent.EventType.USERPASSWORDCHANGED,sb.toString());
    GameEvent.saveTL(ev);    
  }

	public static void logEmailSentTL(QPacket qp)
	{
		StringBuilder sb = new StringBuilder("Email successfully sent with subject = \"");
		sb.append(qp.subject);
		sb.append("\".");
		GameEvent ev = new GameEvent(GameEvent.EventType.EMAILSENT, sb.toString());
		GameEvent.saveTL(ev);
		;
	}

	public static void logEmailFailureTL(QPacket qp, Throwable ex)
	{
		StringBuilder sb = new StringBuilder("Email transmission failure,  subject = \"");
		sb.append(qp.subject);
		sb.append("\".");
		sb.append(MmowgliMailer.explainException(ex));
		GameEvent ev = new GameEvent(GameEvent.EventType.EMAILFAILURE,sb.toString());
		GameEvent.saveTL(ev);
	}
}
