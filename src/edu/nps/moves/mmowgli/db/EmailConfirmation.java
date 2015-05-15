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
import java.util.UUID;

import javax.persistence.*;

import edu.nps.moves.mmowgli.hibernate.DB;

/**
 * EmailConfirmation.java Created on Sep 7, 2012
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This is a database table, listing private message between users
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class EmailConfirmation implements Serializable
{
  private static final long serialVersionUID = -600245570929849739L;
  long    id;         // Primary key, auto-generated.

  Date    creationDate;
  String  confirmationCode;
  User    user;
  
  public EmailConfirmation()
  {
    setConfirmationCode(UUID.randomUUID().toString());
    setCreationDate(new Date());
  }
  
  public EmailConfirmation(User u)
  {
    this();
    setUser(u);
  }
   
  public static void saveTL(EmailConfirmation e)
  {
    DB.saveTL(e);
  }

  @Id
  @Basic
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  @Basic
  public String getConfirmationCode()
  {
    return confirmationCode;
  }

  public void setConfirmationCode(String confirmationCode)
  {
    this.confirmationCode = confirmationCode;
  }

  @Basic
  public Date getCreationDate()
  {
    return creationDate;
  }

  public void setCreationDate(Date creationDate)
  {
    this.creationDate = creationDate;
  }
  
  @ManyToOne
  public User getUser()
  {
    return user;
  }
  
  public void setUser(User user)
  {
    this.user = user;
  }  
 }
