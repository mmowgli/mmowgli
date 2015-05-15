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

import javax.persistence.*;

import java.io.*;

/**
 * A set of possible questions, maybe security security questions, such as "What high school did you attend", "What is your dog's name", etc. The assumption is that we have a finite set
 * of these questions that are pre-configured in this table.  The game master selects one of these to present
 * to the user during registration
 * 
 * @author DMcG
 * 
 * Modified on Dec 16, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@Entity
public class GameQuestion implements Serializable
{
  private static final long serialVersionUID = 7520363785937710083L;
//@formatter:off
  
  long   id;       // primary key
  String question; // The question
  String summary;  // Summary of question
//@formatter:off

  public GameQuestion()
  {}
  
  public GameQuestion(String question)
  {
    setQuestion(question);
  }
  
  /**
   * @return the id
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
   * @return the question
   */
  @Basic
  public String getQuestion()
  {
    return question;
  }

  /**
   * @param question
   */
  public void setQuestion(String question)
  {
    this.question = question;
  }
  
  /**
   * @return the summary
   */
  @Basic
  public String getSummary()
  {
    return summary;
  }

  /**
   * @param summary
   */
  public void setSummary(String summary)
  {
    this.summary = summary;
  }
}
