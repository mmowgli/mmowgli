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

import javax.persistence.*;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.jasypt.hibernate4.type.EncryptedStringType;

import edu.nps.moves.mmowgli.hibernate.VHibPii;

/** Used for jasypt encryption of fields */

@TypeDef(
	    name="encryptedString", 
	    typeClass=EncryptedStringType.class, 
	    parameters={@Parameter(name="encryptorRegisteredName",
	                           value="propertiesFileHibernateStringEncryptor")}
	)
	
/**
 * 
 * @author DMcG
 */
@Entity
public class EmailPii implements Serializable
{
  private static final long serialVersionUID = 4891919588012235495L;

  long id; // Primary key, auto-increment, unique
  String address; // The email address
  String digest; // for searching
  
  public EmailPii()
  {
  }
  
  public EmailPii(String s)
  {
    setAddress(s);
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

  @Type(type="encryptedString")
  public String getAddress()
  {
    return address;
  }

  public void setAddress(String address)
  {
    this.address = address;
    setDigest(VHibPii.getDigester().digest(address));
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

}
