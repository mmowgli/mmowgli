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
import java.util.Date;

import javax.persistence.*;

import edu.nps.moves.mmowgli.hibernate.DB;

/**
 * Message.java Created on Dec 16, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This is a database table, listing private message between users
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class Message implements Serializable, Comparable<Object>
{
  private static final long serialVersionUID = -8410734285666768092L;
//@formatter:off
  long    id;         // Primary key, auto-generated.
  String  text;       // the filename and where it sits
  User    fromUser;
  User    toUser;
  Date    dateTime;
  boolean hidden;
  boolean superInteresting;
  Move    createdInMove;
  //@formatter:on

  /*
   * Only called by hibernate; use others from app since we need to always set current move
   */
  public Message()
  {
    setDateTime(new Date());
  }
  
  public Message(String text)
  {
    this();
    setText(text);
    setCreatedInMove(Move.getCurrentMoveTL());   
  }
  
  public Message(String text, User fromUser)
  {
    this(text);
    setFromUser(fromUser);
  }
  
  public Message(String text, User fromUser, User toUser)
  {
    this(text,fromUser);
    setToUser(toUser);
  }
    
  public static void saveTL(Message m)
  {
    DB.saveTL(m);
  }
  
  public static void updateTL(Message m)
  {
    DB.updateTL(m);
  }
  
 @Override
  public int compareTo(Object arg0)
  {
    if(this.dateTime == null)
      return -1;
    Date d = ((Message)arg0).getDateTime();
    if(d == null)
      return +1;
    
    return (int)(d.getTime() - dateTime.getTime());
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

  @Lob
  public String getText()
  {
    return text;
  }

  public void setText(String text)
  {
    this.text = text;
  }

  @Basic
  public Date getDateTime()
  {
    return dateTime;
  }

  public void setDateTime(Date dateTime)
  {
    this.dateTime = dateTime;
  }

  @ManyToOne
  public User getFromUser()
  {
    return fromUser;
  }

  public void setFromUser(User fromUser)
  {
    this.fromUser = fromUser;
  }
  
  @ManyToOne
  public User getToUser()
  {
    return toUser;
  }

  public void setToUser(User toUser)
  {
    this.toUser = toUser;
  }

  @Basic
  public boolean isHidden()
  {
    return hidden;
  }

  public void setHidden(boolean hidden)
  {
    this.hidden = hidden;
  }
  
  @Basic
  public boolean isSuperInteresting()
  {
    return superInteresting;
  }

  public void setSuperInteresting(boolean superInteresting)
  {
    this.superInteresting = superInteresting;
  }
  
  @ManyToOne
  public Move getCreatedInMove()
  {
    return createdInMove;
  }

  public void setCreatedInMove(Move createdInMove)
  {
    this.createdInMove = createdInMove;
  }


}
