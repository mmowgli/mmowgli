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

package edu.nps.moves.mmowgli.db;


import java.io.Serializable;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.*;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import edu.nps.moves.mmowgli.hibernate.DB;

/**
 * ChatLog.java
 * Created on Apr 12, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@Entity
public class ChatLog implements Serializable
{
  private static final long serialVersionUID = 6004754551353410622L;
    
  long        id;            // Primary key, auto-generated.
  
  SortedSet<Message>   messages = new TreeSet<Message>();
  
  public ChatLog()
  {
  }
  
  public static void updateTL(ChatLog c)
  {
    DB.updateTL(c);
  }
 
  public static void saveTL(ChatLog c)
  {
    DB.saveTL(c);
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  // This card can have many follow-on cards, but each follow-on has only one "parent"
  @SuppressWarnings("deprecation")
  @OneToMany
  @JoinTable(name="ChatLog_Messagess",
        joinColumns = @JoinColumn(name="chatlog_id"),
        inverseJoinColumns = @JoinColumn(name="message_id")
    )
  @Sort(type=SortType.COMPARATOR, comparator=DateDescComparator.class)
  //@SortComparator(value = DateDescComparator.class) // hib 4 bug?
  public SortedSet<Message> getMessages()
  {
    return messages;
  }
  
  public void setMessages(SortedSet<Message> messages)
  {
    this.messages = messages;
  }

  public static class DateDescComparator implements Comparator<Message>
  {
    @Override
    public int compare(Message m0, Message m1)
    {
      long l0 = m0.getDateTime().getTime();
      long l1 = m1.getDateTime().getTime();
      if(l1==l0)
        return 0;
      if(l1>l0)
        return +1;
      return -1;
      //return (int)(l1-l0); //(l0-l1);  i think this causes rounding errors w long->int
    }
  }
}
