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

package edu.nps.moves.mmowgliMobile.data;

import java.io.Serializable;

public class AbstractPojo implements Serializable
{
  private static final long serialVersionUID = 1L;

  private static long idCounter=0;
  private long id;

  protected AbstractPojo parent;

  protected String name = "";

  public AbstractPojo()
  {
    id = idCounter++;
  }
  
  /**
   * @return the parent
   */
  public AbstractPojo getParent()
  {
    return parent;
  }

  /**
   * @param parent
   *          the parent to set
   */
  public void setParent(AbstractPojo parent)
  {
    this.parent = parent;
  }

  /**
   * @return the shortName
   */
  public String getName()
  {
    return name;
  }

  /**
   * @param shortName
   *          the shortName to set
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Get an unique id for this pojo
   * 
   * @return
   */
  public long getId()
  {
    return id;
  }

  /**
   * Set a unique id for this pojo
   * 
   * @param id
   */
  public void setId(long id)
  {
    this.id = id;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof AbstractPojo) {
      AbstractPojo p = (AbstractPojo) obj;
      return p.getId() == getId();
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return String.valueOf(getId()).hashCode();
  }
}
