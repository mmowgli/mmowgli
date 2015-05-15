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

import javax.persistence.*;

/**
 *         31 Mar 2011
 * 
 *         MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 *         www.nps.edu
 * 
 *         This is a database table, listing available affiliation instances for
 *         the game
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@Entity
public class Affiliation implements Serializable
{
  private static final long serialVersionUID = -6223299320983932552L;
  long   id;
  String affiliation;
  
  public Affiliation()
  {}
  
  public Affiliation(String s)
  {
    setAffiliation(s);
  }

  /**
   * @return the primary key
   */
  @Id
  @Basic
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long getId()
  {
    return id;
  }

  /**
   * @param id
   */
  public void setId(long id)
  {
    this.id = id;
  }

  /**
   * @return the affiliation
   */
  @Basic
  public String getAffiliation()
  {
    return affiliation;
  }

  /**
   * @param affiliation
   */
  public void setAffiliation(String affiliation)
  {
    this.affiliation = affiliation;
  }
}
