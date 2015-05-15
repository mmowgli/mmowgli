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

import javax.persistence.*;
	
/**
 * GoogleMapMarker.java
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
public class GoogleMapMarker implements Serializable
{
  private static final long serialVersionUID = 4891919588012235495L;

  long id; // Primary key, auto-increment, unique
  boolean visible = true;

  Double lat = 0.0d;
  Double lon = 0.0d;
  String iconUrl = "http://google-maps-icons.googlecode.com/files/redblank.png";
  Double iconAnchorX = 13.0d;
  Double iconAnchorY = 25.0d;
  String title = "";
  boolean draggable = false;
  String popupContent = "";
  
  public GoogleMapMarker()
  {
  }
  
  public GoogleMapMarker(double lat, double lon)
  {
    this.lat = lat;
    this.lon = lon;
  }
  
  public GoogleMapMarker(Point2D.Double dub)
  {
    this.lon = dub.x;
    this.lat = dub.y;
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
  public boolean isVisible()
  {
    return visible;
  }
  public void setVisible(boolean visible)
  {
    this.visible = visible;
  }

  @Basic
  public Double getLat()
  {
    return lat;
  }
  public void setLat(Double lat)
  {
    this.lat = lat;
  }

  @Basic
  public Double getLon()
  {
    return lon;
  }
  public void setLon(Double lon)
  {
    this.lon = lon;
  }
  
  @Transient
  public Point2D.Double getLatLon()
  {
    return new Point2D.Double(lon,lat);
  }
  public void setLatLon(Point2D.Double ll)
  {
    lon = ll.x;
    lat = ll.y;
  }
  
  @Basic
  public String getIconUrl()
  {
    return iconUrl;
  }
  public void setIconUrl(String iconUrl)
  {
    this.iconUrl = iconUrl;
  }

  @Basic
  public Double getIconAnchorX()
  {
    return iconAnchorX;
  }
  public void setIconAnchorX(Double iconAnchorX)
  {
    this.iconAnchorX = iconAnchorX;
  }

  @Basic
  public Double getIconAnchorY()
  {
    return iconAnchorY;
  }
  public void setIconAnchorY(Double iconAnchorY)
  {
    this.iconAnchorY = iconAnchorY;
  }

  @Transient
  public Point2D.Double getIconAnchorXY()
  {
    return new Point2D.Double(iconAnchorX,iconAnchorY);
  }
  public void setIconAnchorXY(Point2D.Double pd)
  {
    iconAnchorX = pd.x;
    iconAnchorY = pd.y;
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
  public boolean isDraggable()
  {
    return draggable;
  }
  public void setDraggable(boolean draggable)
  {
    this.draggable = draggable;
  }

  @Lob
  public String getPopupContent()
  {
    return popupContent;
  }
  public void setPopupContent(String popupContent)
  {
    this.popupContent = popupContent;
  }
}
