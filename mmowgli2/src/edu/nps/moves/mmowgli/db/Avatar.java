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

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.hibernate.DB;

/**
 * Avatar.java
 * Created on Dec 16, 2010
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * This is a database table, listing available avatars for the game
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class Avatar implements Serializable
{
  private static final long serialVersionUID = -3082976926906562077L;
  
  long id;            /* Primary key, auto-generated. */
  Media  media;       /* the filename and where it sits */
  String description; /* small handle */

  public Avatar()
  {
  }
  
  public Avatar(Media media, String description)
  {
    setMedia(media);
    setDescription(description);  
  }
  
  public static Avatar getTL(Object id)
  {
    return DB.getTL(Avatar.class, id);
  }
  
  public static HbnContainer<Avatar> getContainer()
  {
    return DB.getContainer(Avatar.class);
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    if(description != null)
      sb.append(description);
    else
      sb.append("<no description>");
    sb.append(" / ");
    sb.append(media.getUrl());
    return sb.toString();
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

  @ManyToOne
  public Media getMedia()
  {
    return media;
  }

  public void setMedia(Media media)
  {
    this.media = media;
  }

  @Basic
  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
 
}
