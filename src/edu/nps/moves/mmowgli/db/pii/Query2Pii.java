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

package edu.nps.moves.mmowgli.db.pii;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.jasypt.hibernate4.type.EncryptedStringType;

/** Used for jasypt encryption of fields */

@TypeDef(
      name="encryptedString", 
      typeClass=EncryptedStringType.class, 
      parameters={@Parameter(name="encryptorRegisteredName",
                             value="propertiesFileHibernateStringEncryptor")}
  )
  
@Entity
public class Query2Pii implements Serializable
{
  private static final long serialVersionUID = -864698802656733140L;
  long   id;
  String email; // primary key
  String name; // user handle
  String digest;
  Date   date; // signup date
  String background;
  boolean invited = false;
  boolean confirmed = false;
  Boolean ingame = null;
  
  /**
   * Primary key
   */
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
  
  @Basic
  public String getDigest()
  {
    return digest;
  }
  public void setDigest(String s)
  {
    digest = s;
  }
  
  @Type(type="encryptedString")
  public String getEmail()
  {
    return email;
  }
  public void setEmail(String email)
  {
    this.email = email;
  }

  @Basic
  public String getName()
  {
    return name;
  }
  public void setName(String name)
  {
    this.name = name;
  }

  @Basic
  public String getBackground()
  {
    return background;
  }
  public void setBackground(String background)
  {
    this.background = background;
  }

  @Temporal(TemporalType.TIMESTAMP)
  public Date getDate()
  {
    return date;
  }

  public void setDate(Date date)
  {
    this.date = date;
  } 
  
  // not in db, just rename of background
  @Transient
  public String getInterest()
  {
    return getBackground();
  }
  
  public void setInterest(String interest)
  {
    setBackground(interest);
  }

  @Basic
  public boolean isInvited()
  {
    return invited;
  }

  public void setInvited(boolean invited)
  {
    this.invited = invited;
  }

  @Basic
  public boolean isConfirmed()
  {
    return confirmed;
  }

  public void setConfirmed(boolean confirmed)
  {
    this.confirmed = confirmed;
  }

  @Basic
  public Boolean isIngame()
  {
    return ingame;
  }

  public void setIngame(Boolean ingame)
  {
    this.ingame = ingame;
  }
}
