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

package edu.nps.moves.mmowgli.utility;

import java.io.Serializable;

import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Embedded;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 *
 * @version	$Id$
 * @since $Date$
 * @copyright	Copyright (C) 2011
 */
public class Res implements Serializable
{
  private static final long serialVersionUID = -8528778581352835108L;

  /** Use this one to first get back out to mmowgliOne pkg/dir */
  /* used from QueryLogger "app" to point to the resources which are normally relative to the main app */
  public ClassResource getClasspathImageResource(String prefixPath, String filename)
  {
    prefixPath = (prefixPath==null?"":prefixPath);
    return new ClassResource(prefixPath+"resources/images/"+filename);
  }
  
  public ClassResource getClasspathImageResource(String filename)
  {
    //System.out.println("classpathimageResource : "+filename);
    return getClasspathImageResource(null,filename);//new ClassResource("resources/images/"+filename,app);
  }
  
  public Embedded getClasspathImage(String filename)
  {
    ClassResource clR = getClasspathImageResource(filename);
    Embedded emb = new Embedded();
    emb.setSource(clR);
    return emb;
  }

  public ClassResource getClasspathSoundResource(String filename)
  {
    return new ClassResource("resources/sounds/"+filename);
  }
  
  public Embedded cpImg(String filename)
  {
    return getClasspathImage(filename);
  }

  public Embedded getExternalImage(String url)
  {
    ExternalResource exR = new ExternalResource(url);
    Embedded emb = new Embedded();
    emb.setSource(exR);
    return emb;
  }

  public Embedded extImg(String url)
  {
    return getExternalImage(url);
  }
}
