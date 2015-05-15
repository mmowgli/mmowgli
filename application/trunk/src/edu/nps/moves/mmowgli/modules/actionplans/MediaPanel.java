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

import org.vaadin.alump.scaleimage.ScaleImage;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * MediaPanel.java
 * Created on Mar 9, 2012
 * Updated on Mar 14, 2014
 * 
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MediaPanel extends VerticalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = -1414358432821912217L;
  
  public static String WIDTH = "309px";
  private String PLAYER_HEIGHT = "207px"; 
  private int PLAYERINDEX_IN_LAYOUT = 1;
  
  NativeButton zoom;
  TextArea caption;
  TextField title;
  HorizontalLayout titleHL;
  Label indexLab;
  Media m;
  ClickListener scaler;
  Button canButt, saveButt;
  HorizontalLayout captionSavePan;
  Object apId;
  int idx = -1;

  ScaleImage scaledImage;
  Component mediaPlayer;
  Label placeHolder;

  private boolean titleFocused=false;
  private boolean captionFocused = false;
  
  MediaPanel(Media m, Object apId, int idx, ClickListener replaceListener)
  {
    this.idx = idx;
    this.m = m;
    
    zoom = new NativeButton();
    caption = new TextArea();
    caption.setInputPrompt("Description");
    title = new TextField();
    title.setInputPrompt("Title");
    titleHL = new HorizontalLayout();
    indexLab = new HtmlLabel("");
    
    FocusHandler fHandler = new FocusHandler();
    caption.addFocusListener((FocusListener)fHandler);
    title.addFocusListener((FocusListener)fHandler);
     
    captionSavePan = new HorizontalLayout();
    captionSavePan.setSpacing(true);
    captionSavePan.setMargin(false);
    Label lab;
    captionSavePan.addComponent(lab = new Label());
    lab.setWidth("1px");
    captionSavePan.setExpandRatio(lab, 1.0f);
    canButt = new Button("Cancel");
    captionSavePan.addComponent(canButt);
    canButt.setStyleName(Reindeer.BUTTON_SMALL);
    canButt.addClickListener((ClickListener)fHandler);
    saveButt = new Button("Save");
    captionSavePan.addComponent(saveButt);
    saveButt.setStyleName(Reindeer.BUTTON_SMALL);
    saveButt.addStyleName("m-redbutton");
    saveButt.addClickListener((ClickListener)fHandler);
    captionSavePan.addComponent(lab=new Label());
    lab.setWidth("5px");
  }
  
  public void setIndex(int i)
  {
    indexLab.setValue("<b style='font-size:150%'>"+i+"</b>");    
  }
  
  class FocusHandler implements FocusListener,ClickListener //BlurListener,
  {
    private static final long serialVersionUID = -5412529699678903650L;

    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void focus(FocusEvent event)
    {
      String s = "";
      if(event.getSource() == caption) {
        if(caption.isReadOnly())
          return; 
        caption.selectAll();
        s = "caption ";
        captionFocused=true;
      }
      else if(event.getSource() == title) {
        if(title.isReadOnly())
          return;
        title.selectAll();
        s = "title ";
        titleFocused=true;
      }
      HSess.init();
      captionSavePan.setVisible(true);
      String substring = m.getType()==MediaType.IMAGE?" is editing image "+s+"number ":" is editing video "+s+"number ";
      sendStartEditMessage(Mmowgli2UI.getGlobals().getUserTL().getUserName()+substring+(idx+1));
      HSess.close();
    }
   
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      if(event.getSource()  == canButt) {
        m = Media.getTL(m.getId());  // might have changed under us, and we don't update fields being edited
        setValueIfNonNull(caption,m.getDescription());
        setValueIfNonNull(title,m.getTitle());
      }
      else { // Save
        m.setDescription(nullOrString(caption.getValue()));
        m.setTitle(nullOrString(title.getValue()));
        Media.updateTL(m);
      }
      captionSavePan.setVisible(false);
      titleFocused = false;
      captionFocused = false;
      HSess.close();
    }     
  }
  
  public void setIdx(int idx)
  {
    this.idx = idx;
  }
  
  private String nullOrString(Object o)
  {
    if(o == null)
      return null;
    return o.toString();
  }
  
  public void setReadOnly(boolean ro)
  {
    caption.setReadOnly(ro);
    title.setReadOnly(ro);
  }
  
  public void setMedia(Media m)
  {
    this.m = m;
    if(MediaType.IMAGE == m.getType())
      setImageMedia(m);
    else if(MediaType.YOUTUBE == m.getType())
      setVideoMedia(m);
  }
  
  public void mediaUpdatedOobTL()
  {
    Media oobM = getOobMediaTL(null);
    if(oobM == null)  // may be null if image removed
      return;
    if(!titleFocused)
      setTitleVal(oobM);
    if(!captionFocused)
      setCaptionVal(oobM);
  }

  private Media getOobMediaTL(Media oobM)
  {
    if(oobM != null)
      return oobM;
    return (Media)HSess.get().get(Media.class,m.getId()); // this can be null if the media was deleted
  }
  
  public Media getMedia()
  {
    return m;
  }

  private Component buildPlayer(Media m)
  {
    try {
      Flash ytp = new Flash();
      ytp.setSource(new ExternalResource("https://www.youtube.com/v/" + m.getUrl()));
      ytp.setParameter("allowFullScreen", "true");
      ytp.setParameter("showRelated", "false");
      ytp.setWidth(150.0f, Unit.PIXELS);
      ytp.setHeight(150.0f, Unit.PIXELS);

      ytp.setWidth(WIDTH);
      ytp.setHeight(PLAYER_HEIGHT);

      placeHolder = new Label("Mmowgli Video");
      placeHolder.setWidth(WIDTH);
      placeHolder.setHeight(PLAYER_HEIGHT);

      return ytp;
    }
    catch(Exception e) {
      return new Label("Wrong media type");
    }
  }
  
  private AbsoluteLayout buildImage(Media m)
  {
    MediaLocator mLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    AbsoluteLayout imageStack = new AbsoluteLayout();
    imageStack.setWidth(WIDTH);
    imageStack.setHeight(PLAYER_HEIGHT);
    scaledImage = new ScaleImage();
    scaledImage.setSource(mLoc.locate(m));
    scaledImage.setWidth(WIDTH);
    scaledImage.setHeight(PLAYER_HEIGHT);
    imageStack.addComponent(scaledImage,"top:0px;left:0px");
    zoom.setIcon(mLoc.getActionPlanZoomButt());
    zoom.addStyleName("m-actionplan-zoom-button");
    zoom.addStyleName("borderless");
    imageStack.addComponent(zoom, "top:10px;left:10px");
    return imageStack;
  }
    
  private void setVideoMedia(Media m)
  {
    this.m = m;
    Component comp = buildPlayer(m);
    if(mediaPlayer != null)
      removeComponent(mediaPlayer);
    addComponent(mediaPlayer = comp, PLAYERINDEX_IN_LAYOUT);
    
    setCaptionAndTitle(m);
    
    mediaPlayer = comp;
  }

  private void setCaptionAndTitle(Media m)
  {
    setCaptionVal(m);
    setTitleVal(m);
  }
  
  private void setCaptionVal(Media m)
  {
    boolean isRo = caption.isReadOnly();
    caption.setReadOnly(false);
    setValueIfNonNull(caption,m.getDescription());// "caption" here is "description" in db   
    caption.setReadOnly(isRo);   
  }
  private void setTitleVal(Media m)
  {
    boolean isRo = title.isReadOnly();
    title.setReadOnly(false);
    setValueIfNonNull(title,m.getTitle());
    title.setReadOnly(isRo);
    
  }
  private void setImageMedia(Media m)
  {
    this.m = m;
    Component comp = buildImage(m);
    if(mediaPlayer != null)
      removeComponent(mediaPlayer);
    addComponent(mediaPlayer = comp, PLAYERINDEX_IN_LAYOUT);
    scaledImage.setSource(Mmowgli2UI.getGlobals().getMediaLocator().locate(m));
    if (scaler != null)
      zoom.removeClickListener(scaler);
    zoom.addClickListener(scaler = new Scaler(m));
    
    setCaptionAndTitle(m);
   
    mediaPlayer = comp;
  }

  private void setValueIfNonNull(AbstractTextField comp, String s)
  {
    if(s != null)
      comp.setValue(s);
  }
  
  @Override
  public void initGui()
  {
    setStyleName("m-actionplan-image-panel");
    setSizeUndefined();
    setWidth(WIDTH);
    
    setSpacing(false);
    titleHL.setMargin(false);
    titleHL.setSpacing(true);
    titleHL.setSizeUndefined();
    titleHL.setWidth("307px"); //WIDTH);
    Label sp;
    titleHL.addComponent(sp=new Label());
    sp.setWidth("3px");
    titleHL.addComponent(indexLab);
    titleHL.setComponentAlignment(indexLab, Alignment.MIDDLE_CENTER);
    titleHL.addComponent(title);
    titleHL.setExpandRatio(title, 1.0f);
    addComponent(titleHL);
    title.addStyleName("m-actionplan-image-title");
    title.setWidth("100%");
    
    setMedia(m);
    
    caption.setHeight("65px");// this: setRows(4); doesn't size the same on chrome and ff
    caption.setWidth("100%");
    addComponent(caption);
    caption.addStyleName("m-actionplan-images-caption");
    
    addComponent(captionSavePan);
    captionSavePan.setWidth("99%");
    captionSavePan.setVisible(false);
  }
 
  public  void sendStartEditMessage(String msg)
  {
    /* Have seen event flurries
    if(app.isAlive()) {
      ApplicationMaster master = app.globs().applicationMaster();
      master.sendLocalMessage(ACTIONPLAN_EDIT_BEGIN, ""+apId+MMESSAGE_DELIM+msg);
    }*/
  }
  
  public void enableVideo()
  {
    hideVideo(false);
  }
  
  public void disableVideo()
  {
    hideVideo(true);
  }
  
  private void hideVideo(boolean tf)
  {
    if(tf) {
      replaceComponent(mediaPlayer, placeHolder); //PLAYERINDEX_IN_LAYOUT
    }
    else {
      replaceComponent(placeHolder,mediaPlayer);
    }
  }
  class Scaler implements ClickListener
  {
    private static final long serialVersionUID = -6183261170803030233L;

    Media m;

    Scaler(Media m)
    {
      this.m = m;
    }
    
    ScaleImage image;
    // We now skip trying to get the size of the image -- we were trying to do that to manage aspect ratio.
    // 1. Doing ImageIO.read() was failing because some URLs visible from the client (browser) were not visible from the server (JVM).
    // 2. Eliminates the need for ImageScaler plugin.
    // 3. Uses browser's ability to zoom an img element when window size changes.
    // 4. Downside, can't get client code to report size of rendered image.
    
    public void buttonClick(ClickEvent event)
    {
      Resource r = Mmowgli2UI.getGlobals().getMediaLocator().locate(m);
      MediaSubWindow win = new MediaSubWindow(r);
      UI.getCurrent().addWindow(win);
      win.center();
    }
  }
}
