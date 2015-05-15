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

import java.util.Calendar;

import javax.persistence.*;

import edu.nps.moves.mmowgli.hibernate.DB;

/**
 * @author DMcG
 * 
 *         This is a database table, listing registered users
 * 
 *         Modified on Dec 16, 2010
 * 
 *         MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 *         www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class Award
{

//@formatter:off
  long      id;          // Award primary key*/
  AwardType awardType;   // Type of award; some are prefab, some may be created on the fly  by game masters */
  User      awardedBy;   // The user that set the award */
  User      awardedTo;   // Who is it was awarded to */
  Move      move;        // What game turn this happened in */
  Calendar  timeAwarded; // When this was awarded (timestamp)*/
  String    storyUrl;    // Blog post describing the award */
//@formatter:on

  public static void deleteTL(Award aw)
  {
    DB.deleteTL(aw);
  }

  public static void saveTL(Award aw)
  {
    DB.saveTL(aw);
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

  @ManyToOne
  public AwardType getAwardType()
  {
    return awardType;
  }

  public void setAwardType(AwardType awardType)
  {
    this.awardType = awardType;
  }

  @ManyToOne
  public User getAwardedBy()
  {
    return awardedBy;
  }

  public void setAwardedBy(User awardedBy)
  {
    this.awardedBy = awardedBy;
  }

  @ManyToOne
  public User getAwardedTo()
  {
    return awardedTo;
  }

  public void setAwardedTo(User awardedTo)
  {
    this.awardedTo = awardedTo;
  }

  @ManyToOne
  public Move getMove()
  {
    return move;
  }

  public void setMove(Move aMove)
  {
    this.move = aMove;
  }

  @Temporal(TemporalType.TIMESTAMP)
  public Calendar getTimeAwarded()
  {
    return timeAwarded;
  }

  public void setTimeAwarded(Calendar timeAwarded)
  {
    this.timeAwarded = timeAwarded;
  }
  
  @Basic
  public String getStoryUrl()
  {
    return storyUrl;
  }

  public void setStoryUrl(String storyUrl)
  {
    this.storyUrl = storyUrl;
  }
}
