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

import static edu.nps.moves.mmowgli.MmowgliConstants.ERROR_LOGS;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.Image;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.modules.actionplans.UploadHandler.UploadStatus;
import edu.nps.moves.mmowgli.modules.userprofile.InstallImageDialog;
import edu.nps.moves.mmowgli.modules.userprofile.InstallImageDialog.MediaImage;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
import edu.nps.moves.security.MalwareChecker;

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
public class AddImageDialog extends Window
{
  private static final long serialVersionUID = -1798626091332933916L;
  
  public static enum Type {IMAGES, VIDEOS};

  private AbsoluteLayout holder;
  private TextField localTF;
  private TextField webUrl;
  private Upload uploadWidget;
  private Button testButt;
  private Button submitButt;
  private Button cancelButt;
  
  private Media media = null;
  private Image image = null; // if put into db
  
  private UploadHandler handler = null;
  private MediaType mediaType = MediaType.IMAGE;;
  private HorizontalLayout mainHL;
  private UploadStatus panel;
  
  private CheckBox fromWebCheck;
  private CheckBox fromDeskCheck;
  
  @SuppressWarnings("serial")
  @HibernateSessionThreadLocalConstructor
  public AddImageDialog(Object apId)
  {
    super("Add an Image");

    addStyleName("m-greybackground");

    setClosable(false); // no x in corner

    VerticalLayout mainVL = new VerticalLayout();
    mainVL.setSpacing(true);
    mainVL.setMargin(true);
    mainVL.setSizeUndefined();  // auto size
    setContent(mainVL);
    
    mainHL = new HorizontalLayout();
    mainVL.addComponent(mainHL);
    
    mainHL.setSpacing(true);

    holder = new AbsoluteLayout();
    mainHL.addComponent(holder);
    holder.setWidth("150px");
    holder.setHeight("150px");
    holder.addStyleName("m-darkgreyborder");
    
    VerticalLayout rightVL = new VerticalLayout();
    mainHL.addComponent(rightVL);
    
    fromWebCheck = new CheckBox();
    fromWebCheck.addStyleName("v-radiobutton");
    fromWebCheck.setValue(true);
    fromWebCheck.setImmediate(true);
    fromWebCheck.addValueChangeListener(new RadioListener(fromWebCheck));
    
    HorizontalLayout frWebHL = new HorizontalLayout();
    rightVL.addComponent(frWebHL);
    frWebHL.addComponent(fromWebCheck);
    VerticalLayout frWebVL = new VerticalLayout();
    frWebVL.setMargin(true);
    frWebVL.addStyleName("m-greyborder");
    frWebHL.addComponent(frWebVL);
    frWebVL.setWidth("370px");
    
    frWebVL.addComponent(new Label("From the web:"));
    HorizontalLayout webHL = new HorizontalLayout();
    webHL.setSpacing(true);
    frWebVL.addComponent(webHL);
      webHL.addComponent(webUrl = new TextField());
      webUrl.setColumns(21);
      webHL.addComponent(testButt = new Button("Test"));
      Label sp;
      webHL.addComponent(sp=new Label());
      sp.setWidth("1px");
      webHL.setExpandRatio(sp, 1.0f);
    
    fromDeskCheck = new CheckBox();
    fromDeskCheck.addStyleName("v-radiobutton");
    fromDeskCheck.setValue(false);
    fromDeskCheck.addValueChangeListener(new RadioListener(fromDeskCheck));
    fromDeskCheck.setImmediate(true);
    HorizontalLayout dtHL = new HorizontalLayout();
    rightVL.addComponent(dtHL);
    dtHL.addComponent(fromDeskCheck);
    
    VerticalLayout dtopVL = new VerticalLayout();
    dtopVL.setMargin(true);
    dtopVL.addStyleName("m-greyborder");
    dtHL.addComponent(dtopVL);
    dtopVL.setWidth("370px");
    dtopVL.addComponent(new Label("From your desktop:"));
    HorizontalLayout localHL = new HorizontalLayout();
    localHL.setSpacing(true);
    dtopVL.addComponent(localHL);
      localHL.addComponent(localTF = new TextField());
      localTF.setColumns(21);
      localHL.addComponent(uploadWidget = new Upload());
      panel = new UploadStatus(uploadWidget);
      uploadWidget.setButtonCaption("Browse");
      
      File tempDir = Files.createTempDir();
      tempDir.deleteOnExit();
      handler = new UploadHandler(uploadWidget, panel, tempDir.getAbsolutePath());
      uploadWidget.setReceiver(handler);
      uploadWidget.setImmediate(true);      
      panel.setWidth("100%");
      dtopVL.addComponent(panel);
      dtopVL.setComponentAlignment(panel, Alignment.TOP_CENTER);
    
    HorizontalLayout bottomHL = new HorizontalLayout();
    mainVL.addComponent(bottomHL);
    bottomHL.setSpacing(true);
    bottomHL.setWidth("100%");
    Label spacer;
    bottomHL.addComponent(spacer=new Label());
    spacer.setWidth("100%");
    bottomHL.setExpandRatio(spacer, 1.0f);
    
    bottomHL.addComponent(cancelButt = new Button("Cancel"));
    bottomHL.addComponent(submitButt = new Button("Add"));
    setDisabledFields();
    
    uploadWidget.addFinishedListener(new FinishedListener()
    {
      @Override
      public void uploadFinished(FinishedEvent event)
      {
        Object key = HSess.checkInit();
        System.out.println("AddImageDialog.uploadFinished()");
        String fpath = handler.getFullUploadedPath();
        if(fpath != null) {  // error of some kind if null 
          if(!MalwareChecker.isFileVirusFree(fpath)) {
            panel.state.setValue("<span style='color:red;'>Failed malware check</span>");
            fpath = null;
            localTF.setValue("");
            HSess.checkClose(key);
            return;
          }

          File f = new File(fpath);
          f.deleteOnExit();
          
          try {
            MediaImage mediaImage = InstallImageDialog.putFileImageIntoDbTL(f, f.getName(), event.getMIMEType());
            f.delete();
            media = mediaImage.media;
            image = mediaImage.image; 
            media.setCaption(null);
            Resource res = Mmowgli2UI.getGlobals().getMediaLocator().locate(media);
            setupEmbeddedImageThumbnail(res,media);
            localTF.setValue(event.getFilename());
          }
          catch(IOException ex) {
            Notification.show("Error loading image", Notification.Type.ERROR_MESSAGE);
            MSysOut.println(ERROR_LOGS,"Error in AddImageDialog loading image: "+ex.getClass().getSimpleName()+": "+ex.getLocalizedMessage());
          }
        }
        HSess.checkClose(key); 
      }
    });
    
    testButt.addClickListener(new testWebImageHandler());
   
    submitButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        if(fromWebCheck.getValue()) {
          if(checkBadUrl(webUrl.getValue(),event.getButton()))
            return;
        }
        UI.getCurrent().removeWindow(AddImageDialog.this);
        if(closer != null)
          closer.windowClose(null);
      }      
    });
    cancelButt.addClickListener(new ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        Object key = HSess.checkInit();
        if(media != null) {
          Media.deleteTL(media);       
          media = null;
        }
        if(image != null) {
          Image.deleteTL(image);
          image = null;
        }
        uploadWidget.interruptUpload();
        UI.getCurrent().removeWindow(AddImageDialog.this);
        if(closer != null)
          closer.windowClose(null);
        
        HSess.checkClose(key);
      }      
    });
    
    webUrl.focus();
  }
  
  private boolean checkBadUrl(Object toStringUrl, Component comp)
  {
    if(!MalwareChecker.isUrlOk(toStringUrl.toString())) {
      Notification.show(
         "Black-listed site!",
         "The url you entered has been associated with malware and cannot be used in Mmowgli",
         Notification.Type.ERROR_MESSAGE);
      return true;
    }
    return false;
  }
/*  
  private String buildWebAddr(String fpath)
  {
    String lastBitOnly = fpath.replace(uploadFSPath, "");
    return uploadUrlBase+lastBitOnly;
  }
  
  private String buildRelativeAppAddress(String fpath)
  {
    return fpath.replace(uploadFSPath,"");
  }
*/  
  private void setDisabledFields()
  {   
    boolean web = fromWebCheck.getValue();
    boolean desk = fromDeskCheck.getValue();
    testButt.setEnabled(web);
    webUrl.setEnabled(web);
    localTF.setEnabled(desk);
    uploadWidget.setEnabled(desk); 
  }
  
  @SuppressWarnings("serial")
  class RadioListener implements ValueChangeListener
  {
    CheckBox butt;
    public RadioListener(CheckBox rb)
    {
      butt = rb;
    }
    @Override
    public void valueChange(ValueChangeEvent event)
    {
      boolean sel = butt.getValue();
      if(butt == fromWebCheck) {
        fromDeskCheck.setValue(!sel);
      }
      else {
        fromWebCheck.setValue(!sel);
      }
      setDisabledFields();
    }
  }
  
  @SuppressWarnings("serial")
  class testWebImageHandler implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      String webaddr = webUrl.getValue().toString();
      if(webaddr == null || webaddr.length()<=0)
        return;
      
      if(!MalwareChecker.isUrlOk(webaddr)) {
        Notification.show(
           "Black-listed site!",
           "The url you entered has been associated with malware and cannot be used in Mmowgli",
           Notification.Type.ERROR_MESSAGE);
        media = null;
        return;
      }
      
      ExternalResource extRes = new ExternalResource(webaddr);

      media = new Media(extRes.getURL(),"handle","",mediaType);
      media.setCaption("");
      media.setSource(Media.Source.WEB_FULL_URL);
      
      setupEmbeddedImageThumbnail(extRes,media);
    }         
  }
  
  /**
   * If path != null, it's a local file; else its a url
   * @param path
   * @param extRes
   */
  private void setupEmbeddedImageThumbnail(Resource extRes, Media m)
  {
    com.vaadin.ui.Image comp = new com.vaadin.ui.Image();
    comp.setSource(extRes);
    
    holder.removeAllComponents();
    holder.addComponent(comp,"top:0px;left:0px");
    
    long w = m.getWidth();
    long h = m.getHeight();
    float scale = (float)(w>h ? 150./(double)w : 150./(double)h);
    comp.setWidth(w*scale,Unit.PIXELS);
    comp.setHeight(h*scale,Unit.PIXELS);
    //embedded.addStyleName("m-greyborder");
  }

  /**
   * 
   * @return null if canceled, else the Media object
   */
  public Media getMedia()
  {
    return media;
  }
  public Image getImage()
  {
    return image;
  }
 
  private Window.CloseListener closer;
  
  @Override
  public void addListener(CloseListener listener)
  {
    closer = listener;
  }

 }
