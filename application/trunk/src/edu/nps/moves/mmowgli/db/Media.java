/*
  Copyright (C) 2010-2015 Modeling Virtual Environments and Simulation
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

import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.hibernate.DB;

/**
 * Media.java
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
public class Media implements Serializable
{
  private static final long serialVersionUID = 4614729186684373247L;
  
  public static String DEFAULT_CALLTOACTION_VIDEO_HANDLE = "default call-to-action video handle";
  public static String DEFAULT_ORIENTATION_VIDEO_HANDLE = "default orientation video handle";
  
  public static enum Source    {
     GAME_IMAGES_REPOSITORY,  // file name only or relative path plus file name
     USER_UPLOADS_REPOSITORY, // file name only or relative path plus file name
     FILESYSTEM_PATH,         // full path on server machine
     WEB_FULL_URL,            // full url
     DATABASE
  };
  
  public static enum MediaType { IMAGE, VIDEO, AVATARIMAGE, YOUTUBE };
  
//@formatter:off
  long      id;           // Primary key, auto-generated.
  String    url;          // the path filename and where it sits
  String    alternateUrl;
  MediaType type;         // one of the above 
  Source    source;       // one of the above
  Long      width;
  Long      height;
  boolean inAppropriate = false; // true if judged undesirable
  
  //todo elim one or more  
  String handle;       // small handle/title
  String title;        // for videos
  String caption;      // used in actionplans 
  String description;  // longer description
//@formatter:on
  
  public Media()
  {
  }
  public Media(String url, String handle)
  {
    this(url, handle, null); // by default
  } 
  public Media(String url, String handle, String description)
  {
    this(url, handle, description, MediaType.IMAGE);
  }
  public Media(String url, String handle, String description, MediaType type)
  {
  	this(url, handle, description, type, Source.USER_UPLOADS_REPOSITORY);
  }
  public Media(String url, String handle, String description, MediaType type, Source source)
  {
    setUrl(url);
    setHandle(handle);
    setDescription(description); 
    setType(type);
    setSource(source);
  }

  public static void saveTL(Media med)
  {
    DB.saveTL(med);
  }

  public static void updateTL(Media med)
  {
    DB.updateTL(med);
  }

  public static void deleteTL(Media med)
  {
    DB.deleteTL(med);
  }

  public static Media getTL(Object o)
  {
    return DB.getTL(Media.class, o);
  }
  
  public static Media getDefaultCallToActionVideoTL()
  {
    return getDefaultVideoTL(DEFAULT_CALLTOACTION_VIDEO_HANDLE);
  }
  
  public static Media getDefaultOrientationVideoTL()
  {
    return getDefaultVideoTL(DEFAULT_ORIENTATION_VIDEO_HANDLE);
  }
  
  private static Media getDefaultVideoTL(String handle) 
  {
    return DB.getSingleTL(Media.class, Restrictions.eq("handle", handle));
  }
  
  public static Media newYoutubeMedia(String url)
  {
    return new Media(url, "YouTubeVideo", "YouTubeVideo", MediaType.YOUTUBE, Source.WEB_FULL_URL);
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

  /**
   * @return the title
   */
  @Lob
  public String getTitle()
  {
    return title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title)
  {
    this.title = title;
  }

  /**
   * @return the caption
   */
  @Lob
  public String getCaption()
  {
    return caption;
  }

  /**
   * @param caption the caption to set
   */
  public void setCaption(String caption)
  {
    this.caption = caption;
  }

  @Lob
  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  @Lob
  public String getAlternateUrl()
  {
    return alternateUrl;
  }

  public void setAlternateUrl(String alternateUrl)
  {
    this.alternateUrl = alternateUrl;
  }

  @Basic
  public String getHandle()
  {
    return handle;
  }

  public void setHandle(String handle)
  {
    this.handle = handle;
  }

  @Lob
  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
 
  @Basic
  public MediaType getType()
  {
    return type;
  }

	public void setType(MediaType type)
  {
    this.type = type;
  } 
  
	@Basic
  public Source getSource()
  {
  	return source;
  }

  public void setSource(Source source)
  {
  	this.source = source;
  }
	
  @Basic
  public boolean isInAppropriate()
  {
    return inAppropriate;
  }

  public void setInAppropriate(boolean inAppropriate)
  {
    this.inAppropriate = inAppropriate;
  }
    
  @Basic
  public Long getWidth()
  {
    return width;
  }
  
  public void setWidth(Long width)
  {
    this.width = width;
  }
  
  @Basic
  public Long getHeight()
  {
    return height;
  }
  public void setHeight(Long height)
  {
    this.height = height;
  }
  
  public void cloneFrom(Media existing)
  {
//@formatter:off
    setUrl          (existing.getUrl());
    setType         (existing.getType());
    setSource       (existing.getSource());
    setInAppropriate(existing.isInAppropriate());
    setHandle       (existing.getHandle());
    setTitle        (existing.getTitle());
    setCaption      (existing.getCaption());
    setDescription  (existing.getDescription());
//@formatter:on
  }  
}
