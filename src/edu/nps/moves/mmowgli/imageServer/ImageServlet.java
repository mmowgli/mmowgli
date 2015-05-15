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

package edu.nps.moves.mmowgli.imageServer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.db.Image;
import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * ImageServlet.java Created on Feb. 26, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@WebServlet(description = "Serve mmowgli images from database", urlPatterns = { "/image/*" })
public class ImageServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    ServletOutputStream out = response.getOutputStream();
    
    if(!AppMaster.isInitted()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.setContentType("text/html");
      out.print("<html><head><title>Application not initialized </title></head><body>Image not returned</body></html>");
      return;
    }
    
    Object sessKey = HSess.checkInit();
    try {
      String s = request.getPathInfo();
      if(s.startsWith("/") || s.startsWith("\\"))
        s = s.substring(1);
      if(s.endsWith("/") || s.endsWith("\\"))
        s = s.substring(0, s.length()-1);
      
      @SuppressWarnings("unchecked")
      List<Image> lis = HSess.get().createCriteria(Image.class)
                                   .add(Restrictions.eq("name", s)).list();
      
      if(lis == null || lis.size() <= 0) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.setContentType("text/html");
        out.print("<html><head><title>Not Found</title></head><body>Image not found</body></html>");
      }
      else {
        Image img = lis.get(0);
        String mimeType = img.getMimeType();
        if(mimeType != null && mimeType.length()>0)
          response.setContentType(mimeType);
        else
          response.setContentType(guessMimeType(s));
        out.write(img.getBytes());
      }
    }
    catch(Exception e) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.setContentType("text/html");
      out.println("<html><head><title>Can't get image</title></head>");
      out.println("<body>"+e.getClass().getSimpleName()+" / "+e.getLocalizedMessage()+"</body></html>");
    }
    HSess.checkClose(sessKey);
  }
  
  public static String guessMimeType(String name)
  {
    String nameLower = name.toLowerCase();
    if(nameLower.endsWith(".gif"))
      return Image.MIMETYPE_GIF;
    if(nameLower.endsWith(".tif"))
      return Image.MIMETYPE_TIFF;
    if(nameLower.endsWith(".tiff"))
      return Image.MIMETYPE_TIFF;
    if(nameLower.endsWith(".png"))
      return Image.MIMETYPE_PNG;
    
    return null;
  }
  
  public static boolean isSupportedMimeType(String typ)
  {
    if(typ==null)
      return false;
    
    String lcType = typ.toLowerCase();
    return
      lcType.equals(Image.MIMETYPE_GIF)  ||
      lcType.equals(Image.MIMETYPE_JPEG) ||
      lcType.equals(Image.MIMETYPE_PNG)  ||
      lcType.equals(Image.MIMETYPE_TIFF);
  }
  
  public static URL getBaseImageUrl() throws MalformedURLException
  {
    WebServlet ann = ImageServlet.class.getAnnotation(WebServlet.class);
    String s = ann.urlPatterns()[0];
    if(s.endsWith("*"))
      s=s.substring(0,s.length()-1);   
    return new URL(AppMaster.instance().getAppUrlString()+s);
  }
}
