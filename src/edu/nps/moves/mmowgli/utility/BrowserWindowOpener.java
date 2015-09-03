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

import com.vaadin.ui.JavaScript;
import org.apache.commons.net.util.Base64;

/**
 * BrowserWindowOpener.java
 * Created on Mar 18, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class BrowserWindowOpener
{
  public static void open(String url)
  {
    JavaScript.getCurrent().execute("window.open('"+url+"');");
  }
  
  public static void open(String url, String windowName)
  {
    JavaScript.getCurrent().execute("window.open('"+url+"','"+windowName+"');");
  }
  
  private static String winVar="win1xxx";
  public static void openWithHTML(String htmlStr, String title, String windowName)
  {
    StringBuilder javascript = new StringBuilder();
    htmlStr = openCommon(htmlStr,windowName,javascript);
    
    javascript.append(winVar);
    javascript.append(".document.title='");
    javascript.append(title);
    javascript.append("';\n");
    
    javascript.append(winVar);
    javascript.append(".document.open();\n");
    
    javascript.append(winVar);
    javascript.append(".document.write(\"");
    javascript.append(htmlStr);
    javascript.append("\");\n"); 
    
    javascript.append(winVar);
    javascript.append(".document.close();\n");
    
    //System.out.println(javascript.toString());
    JavaScript.getCurrent().execute(javascript.toString());
  }
  
  public static void openWithInnerHTML(String htmlStr, String title, String windowName)
  {  
    StringBuilder javascript = new StringBuilder();
    htmlStr=openCommon(htmlStr,windowName,javascript);
     
    javascript.append(winVar);
    javascript.append(".document.title='");
    javascript.append(title);
    javascript.append("';\n");
    
    javascript.append(winVar);
    javascript.append(".document.body.innerHTML=\"");
    javascript.append(htmlStr);    
    javascript.append("\";");
    
    //System.out.println(javascript.toString());
    JavaScript.getCurrent().execute(javascript.toString());  // this does work...tested on small content
  }
  
  public static void openHtmlReport(String htmlStr, String title, String windowName)
  {
  	 StringBuilder javascript = new StringBuilder();

     javascript.append("var ");
     javascript.append(winVar);
     javascript.append("=window.open('', '");
     javascript.append(windowName);
     javascript.append("');\n");
     
     htmlStr = htmlStr.replace("\n", "&#xA;");  // This was hard to find!, won't work in style elements 
     htmlStr = htmlStr.replace("'", "&apos;");
     javascript.append(winVar);
     javascript.append(".document.title='");
     javascript.append(title);
     javascript.append("';\n");
     
     javascript.append(winVar);
     javascript.append(".location.href=\"data:text/html;base64,\"+");
     javascript.append("btoa('");
     javascript.append(htmlStr);
     javascript.append("');"); 
     
     //System.out.println(javascript.toString());
     JavaScript.getCurrent().execute(javascript.toString());  	
  }
  
  public static void openXmlReport(String xmlStr, String title, String windowName)
  {
    StringBuilder javascript = new StringBuilder();

  	javascript.append("var ");
    javascript.append(winVar);
    javascript.append("=window.open('', '");
    javascript.append(windowName);
    javascript.append("');\n");  
    javascript.append(winVar);
    javascript.append(".document.title='");
    javascript.append(title);
    javascript.append("';\n");
    
    javascript.append(winVar);
    javascript.append(".location.href=\"data:text/xml;base64,");
    javascript.append(new String(Base64.encodeBase64(xmlStr.getBytes(),false))); 
    javascript.append("\";\n");

    //System.out.println(javascript.toString());
    JavaScript.getCurrent().execute(javascript.toString());  	
  }
  
  private static String openCommon(String s, String windowName, StringBuilder javascript)
  {
    //javascript.append("debugger;\n");
    javascript.append("var ");
    javascript.append(winVar);
    javascript.append("=window.open('', '");
    javascript.append(windowName);
    javascript.append("');\n");
    s = s.replace("\"", "&nbsp;");
    s = s.replace("\n", "&#xA;");  // This was hard to find!
    s = s.replace("\r", "&#xD;");
    return s;
  }
  
  /*
     // Neither one of these works because the browser sets the title after the page is loaded, so you have to 
    // play games with a timeout.  Not worth it here.
    //javascript.append("win1xxx.document.write('<title>");
    //javascript.append(title);
   // javascript.append("</title>');");
    
    //javascript.append("win1xxx.document.title=\"");
    //javascript.append(title);
    //javascript.append("\";");
    */
}
