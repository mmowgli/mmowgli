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

package edu.nps.moves.mmowgli.modules.actionplans;

import static edu.nps.moves.mmowgli.MmowgliConstants.PORTALTARGETWINDOWNAME;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.db.Media.Source;

/**
 * AddImageDialog.java
 * Created on Jan 7, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AddVideoDialog extends Window
{
  private static final long serialVersionUID = -8933947446095329923L;
  
  private Button cancelButt, submitButt, testButt;
  private Media media;
  private TextField addrTf;
  private AbsoluteLayout holder;
  
  @SuppressWarnings("serial")
  public AddVideoDialog()
  {
    super("Add a Video");
    addStyleName("m-greybackground");

    setClosable(false); // no x in corner
    setWidth("530px");
    setHeight("400px");
    
    VerticalLayout mainVL = new VerticalLayout();
    mainVL.setSpacing(true);
    mainVL.setMargin(true);
    mainVL.setSizeUndefined();  // auto size
    mainVL.setWidth("100%");
    setContent(mainVL);
    
    Label helpLab = new HtmlLabel("Add YouTube videos to your Action Plan this way:"+
        "<OL><LI>Find the video you want at <a href=\"https://www.youtube.com\" target=\""+PORTALTARGETWINDOWNAME+"\">www.youtube.com</a>.</LI>"+
        "<LI>Click the \"share\" button below the video screen.</LI>"+
        "<LI>Copy the URL under \"Link to this video:\"</LI>"+
        "<LI>Paste the URL into the field below.</LI>"+
        "</OL>"+
        "If you have media that "+
        "has not been uploaded to YouTube, see <a href=\"https://www.youtube.com\" target=\""+PORTALTARGETWINDOWNAME+"\">www.youtube.com</a> "+
        "for help with establishing a free account.<br/>"
        );
    helpLab.setWidth("100%");
    mainVL.addComponent(helpLab);
    
    HorizontalLayout mainHL = new HorizontalLayout();
    mainHL.setMargin(false);
    mainHL.setSpacing(true);
    mainVL.addComponent(mainHL);
    
    holder = new AbsoluteLayout();
    mainHL.addComponent(holder);
    holder.addStyleName("m-darkgreyborder");
    holder.setWidth("150px");
    holder.setHeight("150px");
    holder.addComponent(new Label("Test video display"),"top:0px;left:0px;");
    VerticalLayout rightVL = new VerticalLayout();
    mainHL.addComponent(rightVL);
    rightVL.setMargin(false);
    rightVL.setSpacing(true);
    rightVL.addComponent(new Label("YouTube video address"));
    
    HorizontalLayout tfHL = new HorizontalLayout();
    tfHL.setSpacing(true);
    rightVL.addComponent(tfHL);
    addrTf = new TextField();
    tfHL.addComponent(addrTf);
    addrTf.setColumns(21);
    tfHL.addComponent(testButt = new Button("Test"));
    
    rightVL.addComponent(new Label("Using the test button will set the"));
    rightVL.addComponent(new Label("default title and description."));
    
    Label sp;
    rightVL.addComponent(sp=new Label());
    sp.setHeight("15px");
    
    HorizontalLayout bottomHL = new HorizontalLayout();
    rightVL.addComponent(bottomHL);
    rightVL.setComponentAlignment(bottomHL, Alignment.TOP_RIGHT);
    bottomHL.setSpacing(true);
    bottomHL.setWidth("100%");
    Label spacer;
    bottomHL.addComponent(spacer=new Label());
    spacer.setWidth("100%");
    bottomHL.setExpandRatio(spacer, 1.0f);
    
    bottomHL.addComponent(cancelButt = new Button("Cancel"));
    bottomHL.addComponent(submitButt = new Button("Add"));
    testButt.addClickListener(new TestVidHandler());
   
    submitButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {       
        UI.getCurrent().removeWindow(AddVideoDialog.this);
        if(closer != null)
          closer.windowClose(null);
      }      
    });
    
    cancelButt.addClickListener(new ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        media = null;
        UI.getCurrent().removeWindow(AddVideoDialog.this);
        if(closer != null)
          closer.windowClose(null);
      }      
    });
  }
  
  /**
   * 
   * @return null if canceled, else the Media object
   */
  public Media getMedia()
  {
    return media;
  }
  
  private Window.CloseListener closer;
  
  @Override
  public void addListener(CloseListener listener)
  {
    closer = listener;
  }
  @SuppressWarnings("serial")
  class TestVidHandler implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      String addrOrId = addrTf.getValue().toString();
      if (addrOrId == null || addrOrId.length() <= 0)
        return;
      // char[] ca = addrOrId.toCharArray();
      // boolean isID = false;
      // for(char c : ca)
      // if(!Character.isLetterOrDigit(c)) {
      // isID = false;
      // break;
      // }

      // Above is not a good test, because youtube id's can contain other than alphanum.

      String id = extractId(addrOrId);

      media = new Media(id, "YouTubeVideo", "Action Plan video", MediaType.YOUTUBE, Source.WEB_FULL_URL);
      media.setCaption("Describe this video here");
      media.setTitle("Title here");
      
      Flash ytp = new Flash();
      ytp.setSource(new ExternalResource("http://www.youtube.com/v/"+media.getUrl()));
      ytp.setParameter("allowFullScreen", "false");
      ytp.setParameter("showRelated", "false");
      ytp.setWidth(150.0f,Unit.PIXELS);
      ytp.setHeight(150.0f,Unit.PIXELS);

      holder.removeAllComponents();
      holder.addComponent(new Label("video will appear if found"),"top:0px;left:0px");
      holder.addComponent(ytp, "top:0px;left:0px");

      fillDefaults(media,id);
    }
    
  }
  
  private void fillDefaults(Media med, String id)
  {
    String feed = "https://gdata.youtube.com/feeds/api/videos/"+id;
    try {

      YouTubeService svc = new YouTubeService("mmowgli");
      VideoEntry videoEntry = svc.getEntry(new URL(feed), VideoEntry.class);
      media.setTitle(videoEntry.getTitle().getPlainText());
      YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
      media.setCaption( mediaGroup.getDescription().getPlainTextContent());
      media.setDescription(media.getCaption());
    }
    catch (Exception ex) {
      //silently fail
    }
  }
  
  private static String extractId(String url)
  {
    int lastSlashIdx;

    // forms:
    // id only: Lc6U7_-BeGc
    // link:    http://youtu.be/Lc6U7_-BeGc
    // embed:  <iframe width="560" height="349" src="https://www.youtube.com/embed/Lc6U7_-BeGc" frameborder="0" allowfullscreen></iframe>

    boolean isID = ((lastSlashIdx=url.lastIndexOf('/')) == -1);  // any slashes?
    if(isID)
      return url;  // if no slashes, assume youtube id only

    boolean isLink = (url.indexOf('<') == -1);   // any brackets?
    if(isLink)
      return url.substring(lastSlashIdx+1);  // if none, assume link, return extrated id

    // else embed
    Pattern p = Pattern.compile(".*src=\"(.*?)\".*");
    Matcher m = p.matcher(url);
    boolean b = m.matches();
    if(b) {
      url = m.group(1);
      if((lastSlashIdx=url.lastIndexOf('/')) != -1)
        return url.substring(lastSlashIdx+1);
    }

    // Give up
    return url;
  }
}
