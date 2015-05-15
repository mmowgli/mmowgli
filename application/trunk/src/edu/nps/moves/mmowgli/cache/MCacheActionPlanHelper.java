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
  along with Mmowgli, in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.cache;

import static edu.nps.moves.mmowgli.MmowgliConstants.ERROR_LOGS;

import java.util.*;

import org.hibernate.Session;
import org.hibernate.criterion.Order;

import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.DB;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.messaging.MMessage;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * MCacheActionPlanHelper.java created on Feb 24, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class MCacheActionPlanHelper
{

  private SortedMap<Long,QuickActionPlan> apQuick; // key = id, full data

  public MCacheActionPlanHelper(Session sess)
  {
    apQuick =    Collections.synchronizedSortedMap(new TreeMap<Long, QuickActionPlan> ()); //(new IDComparator()));
    _rebuildUsers(sess);
  }
  
  // Used by ActionPlanTable
  public List<QuickActionPlan> getQuickActionPlanList()
  {
    ArrayList<QuickActionPlan> lis = new ArrayList<QuickActionPlan>(apQuick.size());
    Collection<QuickActionPlan> coll = apQuick.values();
    Iterator<QuickActionPlan> itr = coll.iterator();
    while (itr.hasNext())
      lis.add(itr.next());
    return lis;
  }

  private void newOrUpdatedActionPlanTL(char messageType, String message)
  {
    MMessage msg = MMessage.MMParse(messageType, message);
    Long id = msg.id;
    Long revision = msg.version;
    ActionPlan ap = DB.getRevisionTL(ActionPlan.class, id, revision);
    if (ap == null) {
      MSysOut.println(ERROR_LOGS, "MCacheActionPlanHelper.externallyNewOrUpdatedActionPlanTL(), cant get ap id = " + id);
      return;
    }
    apQuick.put(id, new QuickActionPlan(ap));
  }  
  
  @SuppressWarnings("unchecked")
  private void _rebuildUsers(Session sess)
  {
    List<ActionPlan> aps = (List<ActionPlan>) sess.createCriteria(ActionPlan.class).
    addOrder(Order.asc("id")).list();

    for(ActionPlan ap: aps) {
      apQuick.put(ap.getId(), new QuickActionPlan(ap));
    }
  }

  /*package*/ void dbUpdatedActionPlan(char msgType, String msg)
  {
    HSess.init();
    newOrUpdatedActionPlanTL(msgType,msg);
    HSess.close();
  }


  /*package*/ void dbNewActionPlan(char msgType, String msg)
  {
    HSess.init();
    newOrUpdatedActionPlanTL(msgType,msg);
    HSess.close();
  }


  /*package*/ void externallyUpdatedActionPlanTL(char msgType, String msg)
  {
    newOrUpdatedActionPlanTL(msgType,msg);
  }


  /*package*/ void externallyNewActionPlanTL(char msgType, String msg)
  {
    newOrUpdatedActionPlanTL(msgType,msg);
  }

//  class IDComparator implements Comparator<Long>
//  {
//    @Override
//    public int compare(Long one, Long two)
//    {
//      return (int)(one-two);
//    }
//  }

  public static class QuickActionPlan
  {
    public String NAME_ID_DELIMITER = ",";
    private int delimiterLen = NAME_ID_DELIMITER.length();
    
    public long id;
    public String title;
    public int createdInMove;
    public String authorNames;
    public String authorIds;
    public float averageThumbs;
    public boolean hidden;
    public String helpWanted;
    public boolean superInteresting;
    public long[] inviteeIds;
    
    public static String QUICKAP_ID = "id";
    public static String QUICKAP_TITLE = "title";
    public static String QUICKAP_CREATEDINMOVE = "createdInMove";
    public static String QUICKAP_AUTHORNAMES = "authorNames";
    public static String QUICKAP_AUTHORIDS = "authorIds";
    public static String QUICKAP_AVERAGETHUMGS = "averageThumbs";
    public static String QUICKAP_HIDDEN = "hidden";
    public static String QUICKAP_HELPWANTED = "helpWanted";
    public static String QUICKAP_SUPERINTERESTING = "superInteresting";
    public static String QUICKAP_INVITEEIDS = "inviteeIds";
        
    public QuickActionPlan(ActionPlan ap)
    {
      update(ap);
    }
    
    public void update(ActionPlan ap)
    {
      setId(              ap.getId());
      setTitle(           ap.getTitle());
      setCreatedInMove(   ap.getCreatedInMove().getNumber());
      setAuthorNames(     listAuthorNames(ap));
      setAuthorIds(       listAuthorIds(ap));
      setAverageThumbs(   (float)ap.getAverageThumb());
      setHidden(          ap.isHidden());
      setHelpWanted(      ap.getHelpWanted());
      setSuperInteresting(ap.isSuperInteresting());
      setInviteeIds(      listInviteeIds(ap));
     }

    public long    getId()              {return id;}
    public String  getTitle()           {return title;}
    public int     getCreatedInMove()   {return createdInMove;}
    public String  getAuthorNames()     {return authorNames;}
    public String  getAuthorIds()       {return authorIds;}
    public float   getAverageThumbs()   {return averageThumbs;}
    public boolean isHidden()           {return hidden;}
    public String  getHelpWanted()      {return helpWanted;}
    public boolean isSuperInteresting() {return superInteresting;}
    public long[]  getInviteeIds()      {return inviteeIds;}

    public void setId(long id)                 {this.id = id;}
    public void setTitle(String string)        {this.title = string;}
    public void setCreatedInMove(int intt)     {this.createdInMove = intt;}
    public void setAuthorNames(String string)  {this.authorNames = string;}
    public void setAuthorIds(String string)    {this.authorIds = string;}
    public void setAverageThumbs(float f)      {this.averageThumbs = f;}
    public void setHidden(boolean b)           {this.hidden = b;}
    public void setHelpWanted(String s)        {this.helpWanted = s;}
    public void setSuperInteresting(boolean b) {this.superInteresting = b;}
    public void setInviteeIds(long[] la)       {this.inviteeIds = la;}
    
    private String listAuthorNames(ActionPlan ap)
    {
      return listAuthors(ap,true);
    }
    private String listAuthorIds(ActionPlan ap)
    {
      return listAuthors(ap,false);
    }
    private String listAuthors(ActionPlan ap, boolean name)
    {
      StringBuilder sb = new StringBuilder();
      Set<User> authors = ap.getAuthors();
      for(User u : authors) {
        sb.append(name?u.getUserName():u.getId());
        sb.append(NAME_ID_DELIMITER);
      }     
      int len = sb.length();
      if(len>0)
        return sb.substring(0, len-delimiterLen);
      return "";      
    }
    
    private long[] listInviteeIds(ActionPlan ap)
    {
      Set<User> invitees = ap.getInvitees();
      long[] arr = new long[invitees.size()];
      int i=0;
      for(User u : invitees)
        arr[i++] = u.getId();
      return arr;
    }
  }

}
