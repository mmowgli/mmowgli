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

import edu.nps.moves.mmowgli.hibernate.DB;

/**
 * GoogleMap.java
 * Created on June 8, 2011
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
public class GoogleMap implements Serializable
{
  private static final long serialVersionUID = 6888772864010356397L;
  
  long id; // Primary key, auto-increment, unique
  Double latCenter = 12.763073d; // Somalia area
  Double lonCenter = 52.750318d;
  int    zoom = 6;
  List<GoogleMapMarker>  markers = new ArrayList<GoogleMapMarker>();
  List<GoogleMapPolyOverlay> overlays = new ArrayList<GoogleMapPolyOverlay>();
  String title = "";
  String description = "";
  
  public GoogleMap()
  {
  }
  public GoogleMap(double lat, double lon, int zoom)
  {
    this.latCenter = lat;
    this.lonCenter = lon;
    this.zoom = zoom;
  }
    
  public static void updateTL(GoogleMap map)
  {
    DB.updateTL(map);
  }
  
  public static void saveTL(GoogleMap map)
  {
    DB.saveTL(map);
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
  @Basic
  public Double getLatCenter()
  {
    return latCenter;
  }
  public void setLatCenter(Double latCenter)
  {
    this.latCenter = latCenter;
  }
  @Basic
  public Double getLonCenter()
  {
    return lonCenter;
  }
  public void setLonCenter(Double lonCenter)
  {
    this.lonCenter = lonCenter;
  }
  
  // Not a db field:
  @Transient
  public Point2D.Double getLatLonCenter()
  {
    return new Point2D.Double(getLonCenter(),getLatCenter());
  }
  public void setLatLonCenter(Point2D.Double pd)
  {
    setLonCenter(pd.x);
    setLatCenter(pd.y);
  }
  
  @Basic
  public int getZoom()
  {
    return zoom;
  }
  public void setZoom(int zoom)
  {
    this.zoom = zoom;
  }
  
  @OneToMany
  public List<GoogleMapMarker> getMarkers()
  {
    return markers;
  }
  public void setMarkers(List<GoogleMapMarker> markers)
  {
    this.markers = markers;
  }
  
  @OneToMany
  public List<GoogleMapPolyOverlay> getOverlays()
  {
    return overlays;
  }
  public void setOverlays(List<GoogleMapPolyOverlay> overlays)
  {
    this.overlays = overlays;
  }
  @Basic
  
  public String getTitle()
  {
    return title;
  }
  public void setTitle(String title)
  {
    this.title = title;
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
