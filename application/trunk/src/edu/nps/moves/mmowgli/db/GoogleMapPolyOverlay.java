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

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * GoogleMapPolyOverlay.java
 * Created on June 8, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class GoogleMapPolyOverlay implements Serializable
{
  private static final long serialVersionUID = -3314458911580595980L;
    
  long id; // Primary key, auto-increment, unique

  List<Point2D.Double> points = new ArrayList<Point2D.Double>();   // delimited by a comma
  String color;
  int weight;
  double opacity;
  boolean clickable;
 
  public GoogleMapPolyOverlay()
  {
  }
    
  /**
   * Primary key, auto-increment, unique
   */
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

  @ElementCollection
  public List<Point2D.Double> getPoints()
  {
    return points;
  }
  public void setPoints(List<Point2D.Double> points)
  {
    this.points = points;
  }

  @Basic
  public String getColor()
  {
    return color;
  }
  public void setColor(String color)
  {
    this.color = color;
  }
  
  @Basic
  public int getWeight()
  {
    return weight;
  }
  public void setWeight(int weight)
  {
    this.weight = weight;
  }
  
  @Basic
  public double getOpacity()
  {
    return opacity;
  }
  public void setOpacity(double opacity)
  {
    this.opacity = opacity;
  }
  
  @Basic
  public boolean isClickable()
  {
    return clickable;
  }
  public void setClickable(boolean clickable)
  {
    this.clickable = clickable;
  }

}
