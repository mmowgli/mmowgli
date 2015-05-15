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
 * Image.java Created on Feb 25, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This is a database table, holding miscelleneous small images
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class Image implements Serializable
{
  private static final long serialVersionUID = -8103000624976036259L;
  
  public static String MIMETYPE_GIF  = "image/gif";
  public static String MIMETYPE_JPEG = "image/jpeg";
  public static String MIMETYPE_PNG  = "image/png";
  public static String MIMETYPE_TIFF = "image/tiff";

  long id; 
  String name;
  String mimeType;
  byte[] bytes;
  String description;
  int width;
  int height;
  
  public Image()
  {
    setDescription("");
  }

  public Image(String name, String mimeType)
  {
    this();
    setName(name);
    setMimeType(mimeType);
  }

  public static void saveTL(Image img)
  {
    DB.saveTL(img);
  }
  
  public static void deleteTL(Image img)
  {
    DB.deleteTL(img);
  }

  public static HbnContainer<Image> getContainer()
  {
    return DB.getContainer(Image.class);
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

  /**
   * @return the name
   */
  @Basic
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
  
  /**
   * @return the name
   */
  @Basic
  public String getMimeType()
  {
    return mimeType;
  }

  public void setMimeType(String mimeType)
  {
    this.mimeType = mimeType;
  }
 
  /**
   * @return the description
   */
  @Lob
  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
  
  /**
   * @return the image as a byte[]
   */
  @Lob
  public byte[] getBytes()
  {
    return bytes;
  }
  public void setBytes(byte[] bytes)
  {
    this.bytes = bytes;
  }
  
  public String toString()
  {
    return getName()+"/"+getMimeType()+"/"+getDescription();
  }

  @Basic
	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	@Basic
	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}
  
}
