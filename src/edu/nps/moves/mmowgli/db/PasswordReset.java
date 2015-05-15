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
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.*;

import edu.nps.moves.mmowgli.hibernate.DB;

/*
 * Program:      MMOWGLI
 *
 * Filename:     PasswordReset.java
 *
 * Author(s):    Terry Norbraten
 *               http://www.nps.edu and http://www.movesinstitute.org
 *
 * Created on:   Created on Jan 24, 2014 13:15
 *
 * Description:  Servlet to handle a forgot password request
 *
 * References:
 *
 * URL:          http://www<URL>/PasswordReset.java
 */

/**
 * Allow a registered user to reset their forgotten password
 * @author <a href="mailto:tdnorbra@nps.edu?subject=edu.nps.moves.mmowgli.db.PasswordReset">Terry Norbraten, NPS MOVES</a>
 * @version $Id: PasswordReset.java 3357 2014-03-25 23:32:36Z tdnorbra $
 */
@Entity
public class PasswordReset implements Serializable
{
  private static final long serialVersionUID = -600245570929859739L;
  long    id;         // Primary key, auto-generated.

  Timestamp creationDate;
  Timestamp expireDate;
  String  resetCode;
  User    user;

  public PasswordReset()
  {
    resetCode = UUID.randomUUID().toString();
    creationDate = new Timestamp(System.currentTimeMillis());

    // Set for 3 hours after creation date.  10800000 ms is three hours.
    expireDate = new Timestamp(creationDate.getTime() + 10800000L);
  }

  public PasswordReset(User u)
  {
    this();
    user = u;
  }

  public static void saveTL(PasswordReset e)
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
  public String getResetCode()
  {
    return resetCode;
  }

  public void setResetCode(String confirmationCode)
  {
    this.resetCode = confirmationCode;
  }

  @Basic
  public Timestamp getCreationDate()
  {
    return creationDate;
  }

  public void setCreationDate(Timestamp creationDate)
  {
    this.creationDate = creationDate;
  }

  @Basic
  public Timestamp getExpireDate()
  {
    return expireDate;
  }

  public void setExpireDate(Timestamp expireDate)
  {
    this.expireDate = expireDate;
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
