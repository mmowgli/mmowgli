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
 * @author Mike Bailey, jmbailey@nps.edu
 * 
 * @version $Id$
 * @since $Date$
 * @copyright Copyright (C) 2011
 */

@Entity
public class CardMarking implements Serializable
{
  private static final long serialVersionUID = -7947016930784739148L;

  public static String SUPER_INTERESTING_LABEL = "Super-Interesting";
  public static String NOCHILDREN_LABEL        = "No More Children";
  public static String COMMON_KNOWLEDGE_LABEL  = "Common Knowledge";
  public static String HIDDEN_LABEL            = "Hidden";

//@formatter:off
  long   id;          // Primary key, auto-generated.
  String label;       // displayed
  String description; // what it means to game masters
//@formatter:on

  public CardMarking()
  {
  }

  public CardMarking(String label, String description)
  {
    setLabel(label);
    setDescription(description);
  }
  
  public static HbnContainer<CardMarking> getContainer()
  {
    return DB.getContainer(CardMarking.class);
  }

  public static CardMarking getTL(Object id)
  {
    return DB.getTL(CardMarking.class, id);
  }
  
  public static CardMarking mergeTL(CardMarking cm)
  {
    return DB.mergeTL(cm);
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  public long getId()
  {
    return id;
  }

  public void setId(long card_pk)
  {
    this.id = card_pk;
  }

  @Basic
  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
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
  
  @Override
  public String toString()
  {
    return label;
  }
}
