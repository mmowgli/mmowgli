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
  along with Mmowgli, in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.modules.userprofile;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.compress.utils.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Image;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.imageServer.ImageServlet;

/**
 * InstallImageDialog.java created on Feb 26, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class InstallImageDialog extends Window
{
  private static final long serialVersionUID = 8355175712103259698L;
  public static long MAXUPLOADSIZE = 512*1024l;
  
  public static void show(boolean showExisting)
  {
    show(null,null,showExisting,null);
  }
  
  public static void show()
  {
    show(null,null,true,null);
  }

  public static void show(String text, InstallImageResultListener lis, String filter)
  {
    show(text,lis,true,filter);
  }
  
  public static void show(String text, InstallImageResultListener lis, boolean showExisting, String filter)
  {
    InstallImageDialog dialog = new InstallImageDialog(text,lis, showExisting, filter);
    dialog.center();
    UI.getCurrent().addWindow(dialog);
  }
  
  public static interface InstallImageResultListener
  {
    public void doneTL(MediaImage mi);
  }
  
  //------------------------------------
  
  private InstallImageResultListener listener;
  private MediaImage mediaImage;
  Upload uploadFileWidget;
  TextField fileNameTF;
  NativeButton saveImageButt,saveExistingButt;
  boolean fileNameTFState, saveImageButtState;
  ListSelect sel;
  private static CheckBox existingCB, newCB;
  
  @SuppressWarnings("serial")
  private InstallImageDialog(String topText, InstallImageResultListener lis, boolean showExisting, String nameFilter)
  {
    Object sessKey = HSess.checkInit();
    listener = lis;

    setCaption(showExisting ? "Choose Existing or Upload New Image" : "Upload New Image");
    setModal(true);
    setWidth("350px");
    VerticalLayout vl;

    VerticalLayout vLay = new VerticalLayout();
    setContent(vLay);
    vLay.setMargin(true);
    vLay.setSpacing(true);

    if (topText != null && topText.length() > 0) {
      HtmlLabel lab = new HtmlLabel(topText);
      lab.setWidth("100%");
      vLay.addComponent(lab);
    }
    
    if (showExisting) {  // put the existing selector in the dialog
    	    	
      Criteria crit = HSess.get().createCriteria(Media.class)
      .add(Restrictions.eq("source", Media.Source.DATABASE))
      .addOrder(Order.asc("url"));
      
      if(nameFilter != null)
      	crit.add(Restrictions.like("handle",nameFilter,MatchMode.ANYWHERE));
            
      @SuppressWarnings({ "unchecked" })
      List<Media> mlis = crit.list();
      
      BeanItemContainer<Media> beanContainer = new BeanItemContainer<Media>(Media.class, mlis);

      vLay.addComponent(existingCB = new CheckBox("Choose from existing images", true));

      vLay.addComponent(vl = new VerticalLayout());
      vl.addStyleName("m-greyborder");
      vl.addStyleName("m-greybackground");
      vl.setMargin(true);
      vl.setSpacing(true);

      vl.addComponent(sel = new ListSelect());
      sel.setWidth("100%");
      sel.setNullSelectionAllowed(false);
      sel.setContainerDataSource(beanContainer);
      sel.setItemCaptionPropertyId("url");

      vl.addComponent(saveExistingButt = new NativeButton("Return selected image", new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
          HSess.init();
          mediaImage = new MediaImage((Media) sel.getValue(),null);
          doneHereTL();
          HSess.close();
        }
      }));
      vLay.addComponent(newCB = new CheckBox("Upload new image", false));
    }

    // Here for the file chooser
    vLay.addComponent(vl = new VerticalLayout());
    vl.addStyleName("m-greyborder");
    vl.addStyleName("m-greybackground");
    vl.setSpacing(true);
    vl.setMargin(true);

    ImgReceiver rec;
    uploadFileWidget = new Upload();// "Image name", rec = new ImgReceiver());
    uploadFileWidget.setReceiver(rec = new ImgReceiver());
    uploadFileWidget.setButtonCaption("Browse");
    uploadFileWidget.setImmediate(true);
    uploadFileWidget.addFailedListener(rec);
    uploadFileWidget.addFinishedListener(rec);
    uploadFileWidget.setEnabled(showExisting ? false : true);
    vl.addComponent(uploadFileWidget);
    vl.addComponent(fileNameTF = new TextField());
    fileNameTF.setWidth("100%");
    fileNameTF.setEnabled(showExisting ? false : true);
    fileNameTFState = false;
    HorizontalLayout hLay;
    vl.addComponent(hLay = new HorizontalLayout());
    hLay.setSpacing(true);
    hLay.addComponent(saveImageButt = new NativeButton("Save image with above name", rec));
    // hLay.addComponent(savedLab = new HtmlLabel("<i>saved</i>"));

    saveImageButt.setImmediate(true);
    saveImageButt.addClickListener(rec);
    saveImageButt.setEnabled(false);
    saveImageButtState = false;

    vLay.addComponent(new NativeButton("Close", new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        doneHereTL();
        HSess.close();
      }
    }));

    if (showExisting) {
      existingCB.addValueChangeListener(new CheckBoxListener(existingCB));
      newCB.addValueChangeListener(new CheckBoxListener(newCB));
    }
    
    HSess.checkClose(sessKey);
  }
  
  private boolean shut=false;
  class CheckBoxListener implements ValueChangeListener
  {
    private static final long serialVersionUID = 1;
    CheckBox source;

    public CheckBoxListener(CheckBox source)
    {
      this.source = source;
    }

    @Override
    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event)
    {
      if(shut) return;      
      shut=true;
      
      if(source==existingCB)
        newCB.setValue(!existingCB.getValue());
      else
        existingCB.setValue(!newCB.getValue());
      
      sel.setEnabled(existingCB.getValue());
      uploadFileWidget.setEnabled(newCB.getValue());
      
      if(newCB.getValue()) {
        saveImageButt.setEnabled(saveImageButtState);
        fileNameTF.setEnabled(fileNameTFState);
      }
      else {
        saveImageButtState = saveImageButt.isEnabled();
        saveImageButt.setEnabled(false);
        fileNameTFState = fileNameTF.isEnabled();
        fileNameTF.setEnabled(false);
      }
        
      saveExistingButt.setEnabled(existingCB.getValue());
      shut=false;
    }
  };

  class ImgReceiver implements Receiver,FailedListener,FinishedListener,ProgressListener, ClickListener
  {
    File f;
    FileOutputStream fos;
    String filename;
    String mimeType;
    String failureMessage = null;
    private static final long serialVersionUID = 1L;
    @Override
    public OutputStream receiveUpload(String filename, String mimeType)
    {
      //System.out.println("********receiveUpload");
      this.failureMessage=null;
      this.filename = filename;
      this.mimeType = mimeType;
      if(!ImageServlet.isSupportedMimeType(mimeType)) {
        failureMessage = "Unsupported file type: mimeType "+mimeType;
        uploadFileWidget.interruptUpload();
        // need to let it continue return null;
      }
      else if(mimeType == null || mimeType.length()<=0)
        this.mimeType = ImageServlet.guessMimeType(filename);
      
      fileNameTF.setValue(filename);
      fileNameTF.setEnabled(false);

      try {
        f = File.createTempFile(filename, ".mmowgliImage");
        f.deleteOnExit();
        fos = new FileOutputStream(f);
        return fos; // will write here
      }
      catch(IOException ioex) {
        failureMessage = "Could not save file: "+ioex.getMessage();
        uploadFileWidget.interruptUpload();
        return null;
      }
    }   

    @Override
    public void updateProgress(long readBytes, long contentLength)
    {
      //System.out.println("********uploadProgress");
      if(readBytes>MAXUPLOADSIZE) {
        failureMessage = "File exceeds size limit of "+MAXUPLOADSIZE+" bytes.";
        uploadFileWidget.interruptUpload();
      }
    }

    @Override
    public void uploadFinished(FinishedEvent event)
    {
      //System.out.println("********uploadFinished");
      if(event.getLength() > MAXUPLOADSIZE)
        Notification.show("Upload Failed", "File exceeds size limit of "+MAXUPLOADSIZE+" bytes.", Notification.Type.ERROR_MESSAGE);
      
      else if(failureMessage != null){
        Notification.show("Upload Failed",failureMessage,Notification.Type.ERROR_MESSAGE);
        failureMessage=null;
      }
      else {
        fileNameTF.setEnabled(true);
        saveImageButt.setEnabled(true);
      }
    }

    @Override
    public void uploadFailed(FailedEvent event)
    {
//      System.out.println("********uploadFailed");
//      if(failureMessage != null){
//        Notification.show("Upload Failed",failureMessage,Notification.Type.ERROR_MESSAGE);
//        failureMessage=null;
//      }
    }

    /* This is the "put image into db" button listener */
    @Override
    public void buttonClick(ClickEvent event)
    {
      Object sessKey = HSess.checkInit();
      long count = (Long) (HSess.get().createCriteria(Image.class)         
          .add(Restrictions.eq("name", fileNameTF.getValue()))
          .setProjection(Projections.rowCount())
          .uniqueResult());
      
      if (count > 0) {
        Notification.show("Name is already used", Notification.Type.ERROR_MESSAGE);
      }
      else {
        try {
          mediaImage = putFileImageIntoDbTL(f,fileNameTF.getValue(),mimeType);
          
          fileNameTF.setValue("");
          Notification.show("Success");
        }
        catch (IOException ex) {
          Notification.show("Error saving image to database / "+ex.getClass().getSimpleName()+" / "+ex.getLocalizedMessage(),
                            Notification.Type.ERROR_MESSAGE);
        }
      }
      HSess.checkClose(sessKey);
      return;
    }
  }
  
  public static class MediaImage
  {
    public Media media;
    public Image image;
    public MediaImage(Media m, Image i)
    {
      media = m;
      image = i;
    }
  }
  
  public static MediaImage putFileImageIntoDbTL(File f, String name, String mimeType) throws IOException
  {
    Image imgObj = new Image(name, mimeType);
    FileInputStream fis = new FileInputStream(f);
    byte[] ba = IOUtils.toByteArray(fis);
    imgObj.setBytes(ba);
    
    InputStream bain = new ByteArrayInputStream(ba);
    BufferedImage bimg = ImageIO.read(bain);
    imgObj.setWidth(bimg.getWidth());
    imgObj.setHeight(bimg.getHeight());
    Image.saveTL(imgObj);
    
    Media med = new Media();
    med.setDescription(imgObj.getName()+" in db");
    med.setSource(Media.Source.DATABASE);
    med.setType(MediaType.IMAGE);
    med.setHandle(""+bimg.getWidth()+"x"+bimg.getHeight());
    med.setUrl(imgObj.getName());
    med.setWidth((long)bimg.getWidth());
    med.setHeight((long)bimg.getHeight());
    Media.saveTL(med);
    return new MediaImage(med,imgObj);
  }
  
  private void doneHereTL()
  {
    UI.getCurrent().removeWindow(this);
    if(listener != null)
      listener.doneTL(mediaImage);  // maybe null media
  }
}
