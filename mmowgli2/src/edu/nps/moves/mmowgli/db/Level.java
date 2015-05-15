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
import java.util.List;

import javax.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * Level.java Created on Dec 16, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This is a database table, listing available levels for the game
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class Level implements Serializable
{
  private static final long serialVersionUID = 4891919588012235495L;
  public static int GAME_MASTER_ORDINAL = -1;
  
  /** Primary key, auto-increment, unique */
  long                      id;

  /** 1-7 */
  int                       ordinal;

  String                    description;

  public Level()
  {
  }

  public Level(int ordinal, String description)
  {
    setOrdinal(ordinal);
    setDescription(description);
  }

  /**
   * Primary key, auto-increment, unique
   * 
   * @return the primary key (id)
   */
  @Id
  @Basic
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long getId()
  {
    return id;
  }

  /**
   * Primary key, auto-increment, unique
   * 
   * @param id
   *          the id to set
   */
  public void setId(long id)
  {
    this.id = id;
  }

  /**
   * The order of this level
   * 
   * @return the order
   */
  @Basic
  @Column(unique = true)
  public int getOrdinal()
  {
    return ordinal;
  }

  /**
   * The order
   * 
   * @param order
   */
  public void setOrdinal(int ordinal)
  {
    this.ordinal = ordinal;
  }

  /**
   * @return the description
   */
  @Basic
  public String getDescription()
  {
    return description;
  }

  /**
   * @param description
   */
  public void setDescription(String desc)
  {
    this.description = desc;
  }
  
  public static Level getFirstLevelTL()
  {
    return getLevelByOrdinalTL(1);
  }
  
  public static Level getLevelByOrdinalTL(int ord)
  {
    return getLevelByOrdinal(ord, HSess.get());
  }
  
  public static Level getLevelByOrdinal(int ord, Session sess)
  {
    Criteria crit = sess.createCriteria(Level.class)
    .add(Restrictions.eq("ordinal", ord));    
    @SuppressWarnings("rawtypes")
    List lis = crit.list();
    if(lis != null && lis.size()>0) // should only be 1
      return (Level)lis.get(0);
    return null;  
  }
  
  public String toString()
  {
    return ""+getOrdinal()+" "+getDescription();
  }
}
