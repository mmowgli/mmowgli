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

import org.vaadin.viritin.fields.MTextField;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.AwardType;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.modules.userprofile.InstallImageDialog.InstallImageResultListener;
import edu.nps.moves.mmowgli.modules.userprofile.InstallImageDialog.MediaImage;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * EditAwardTypeDialog.java created on Feb 26, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class EditAwardTypeDialog extends Window
{
  private static final long serialVersionUID = -2467848071630290310L;
  
  public static void show()
  {
    show(null, null);
  }

  public static void show(AwardType awt, EditAwardResultListener lis)
  {
    EditAwardTypeDialog dialog = new EditAwardTypeDialog(awt, lis);
    dialog.center();
    UI.getCurrent().addWindow(dialog);
  }

  public static interface EditAwardResultListener
  {
    public void doneTL(AwardType a);
  }

  // ------------------------------------
  private EditAwardResultListener listener;
  private AwardType awardType;
  private static String pix55text = "Text describing where the 55x55 pixel image is used.";
  private static String pix300text= "Text describing where the 300x300 pixel image is used.";
  public static String pix55filter = "55x55";
  public static String pix300filter = "300x300";
  private NativeButton butt55, butt300;
  private Image image55, image300;
  private HorizontalLayout hLay55, hLay300;
  private Media media55, media300;
  private Label lab55, lab300;
  private TextField nameTF, descTF;
  
  @SuppressWarnings("serial")
  private EditAwardTypeDialog(AwardType awt, EditAwardResultListener lis)
  {
    Object sessKey = HSess.checkInit();
    listener = lis;
    awardType = awt;
    
    setCaption("Edit Award Type");
    setModal(true);
    setWidth("450px");

    VerticalLayout vLay = new VerticalLayout();
    setContent(vLay);
    vLay.setMargin(true);
    vLay.setSpacing(true);
    vLay.addStyleName("m-greybackground");
    
    FormLayout formLay;
    vLay.addComponent(formLay = new FormLayout());
    formLay.setSizeFull();
    
    formLay.addComponent(nameTF = new MTextField("Award Title")
    .withFullWidth().withNullRepresentation("required field"));
    nameTF.setRequired(true);nameTF.setRequiredError("Required field");nameTF.setSizeFull();
    
    formLay.addComponent(descTF = new MTextField("Description")
    .withFullWidth().withNullRepresentation("required field"));
    descTF.setRequired(true);descTF.setRequiredError("Required field");
    
    Label sp;
    
    formLay.addComponent(hLay55 = new HorizontalLayout());
    hLay55.setWidth("100%");
    hLay55.setCaption("55x55 pixel icon");  
    hLay55.setSpacing(true);
    hLay55.addComponent(lab55 = new HtmlLabel("<i>image name</i>"));
    hLay55.setComponentAlignment(lab55, Alignment.MIDDLE_CENTER);
    hLay55.addComponent(butt55 = new NativeButton("Choose 55x55 image"));
    hLay55.setComponentAlignment(butt55, Alignment.MIDDLE_CENTER);
    hLay55.addComponent(sp=new Label());
    hLay55.setExpandRatio(sp, 1.0f);
    hLay55.addComponent(image55 = new Image(null));
    image55.setWidth("55px");
    image55.setHeight("55px");
    image55.addStyleName("m-greyborder3");
    
    formLay.addComponent(hLay300 = new HorizontalLayout());
    hLay300.setWidth("100%");
    hLay300.setCaption("300x300 pixel icon");  
    hLay300.setSpacing(true);
    hLay300.addComponent(lab300 = new HtmlLabel("<i>image name</i>"));
    hLay300.setComponentAlignment(lab300, Alignment.MIDDLE_CENTER);
    hLay300.addComponent(butt300 = new NativeButton("Choose 300x300 image"));
    hLay300.setComponentAlignment(butt300, Alignment.MIDDLE_CENTER);
    hLay300.addComponent(sp=new Label());
    hLay300.setExpandRatio(sp, 1.0f);
    hLay300.addComponent(image300 = new Image(null));
    image300.setWidth("55px");
    image300.setHeight("55px");
    image300.addStyleName("m-greyborder3");
     
    ClickListener chooseIconListener = new ClickListener()
    {
      boolean is55 = false;
      @Override
      public void buttonClick(ClickEvent event)
      {
        is55 = (event.getButton() == butt55);
        String txt = (is55?pix55text:pix300text);
        InstallImageResultListener lis = 
        new InstallImageResultListener()
        {
          @Override
          public void doneTL(MediaImage mimg)
          {
            Media m = null;
            if(mimg != null)
              m = mimg.media;
            
            if(m != null) {            	
              MediaLocator mediaLoc = Mmowgli2UI.getGlobals().getMediaLocator();
              String handle = m.getHandle();
              if(handle != null && handle.trim().length()<=0)
                handle = null;
              if(is55) {
                media55 = m;
                if(handle== null) {
                  m.setHandle("55x55");
                  Media.updateTL(m);
                }
                lab55.setValue(m.getUrl());
              	image55.setSource(mediaLoc.locate(m));
              }
              else {
                media300 = m;
                if(handle==null) {
                  m.setHandle("300x300");
                  Media.updateTL(m);
                }
                lab300.setValue(m.getUrl());
              	image300.setSource(mediaLoc.locate(m));
              }
            }
          }          
        };
        InstallImageDialog.show(txt,lis,is55?pix55filter:pix300filter);
      }       
    };
    
    butt55.addClickListener(chooseIconListener);
    butt300.addClickListener(chooseIconListener);
    
    HorizontalLayout buttHL = new HorizontalLayout();
    vLay.addComponent(buttHL);
    buttHL.setWidth("100%");

    buttHL.addComponent(sp = new Label());
    sp.setWidth("1px");
    buttHL.setExpandRatio(sp, 1.0f);
    
    buttHL.addComponent(new NativeButton("Cancel", new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
      	awardType = null;
      	doneHereTL();
      }
    }));
    
    
    buttHL.addComponent(new NativeButton("Close", new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        String title = nameTF.getValue().trim();
        String description = descTF.getValue().trim();
        if(title.length()<=0 || description.length()<=0 || media300==null || media55 == null) {
        	Notification.show("All fields must be completed",Notification.Type.ERROR_MESSAGE);
        	return;
        }
        
        HSess.init();
        
        boolean save = false;
        if(awardType == null) {
          awardType = new AwardType();
          save = true;
        }

        awardType.setName(nameTF.getValue().trim());
        awardType.setDescription(descTF.getValue().trim());
        awardType.setIcon300x300(media300);
        awardType.setIcon55x55(media55); 
        
        if(save)
          HSess.get().save(awardType);
        else
        	HSess.get().update(awardType);
        
        doneHereTL();
        HSess.close();
      }
    }));
    
    HSess.checkClose(sessKey);
  }
  
  private void doneHereTL()
  {
    UI.getCurrent().removeWindow(this);
    if (listener != null)
      listener.doneTL(awardType); // maybe null
  }
}
