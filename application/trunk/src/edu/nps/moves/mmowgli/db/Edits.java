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
import java.util.Date;

import javax.persistence.*;

import edu.nps.moves.mmowgli.hibernate.DB;

/**
 * Edits.java Created on June 12, 2012
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This is a database table, listing private message between users
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class Edits implements Serializable, Comparable<Object>
{
  private static final long serialVersionUID = -6084412157143097743L;

  long    id;         // Primary key, auto-generated.
  String  value = "";
  Date    dateTime;

  public Edits(){
    setDateTime(new Date());
  }
  
  public Edits(String value)
  {
    this();
    setValue(value);;   
  }
  
  public static void saveTL(Edits e)
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

  @Lob
  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
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
  
  public static class EditsDateDescComparator implements Comparator<Edits>
  {
    @Override
    public int compare(Edits c0, Edits c1)
    {
      long l0 = c0.getDateTime().getTime();
      long l1 = c1.getDateTime().getTime();
      if(l1==l0)
        return 0;
      if(l1>l0)
        return +1;
      return -1;
      //return (int)(l1-l0); //(l0-l1);  // rounding err
    }
  }
 
  @Override
  public int compareTo(Object arg0)
  {
    if(getDateTime() == null)
      return -1;
    Date d = ((Edits)arg0).getDateTime();
    if(d == null)
      return +1;
    
    return (int)(d.getTime() - getDateTime().getTime());
  } 

 }
