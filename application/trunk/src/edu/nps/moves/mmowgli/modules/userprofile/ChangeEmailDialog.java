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

package edu.nps.moves.mmowgli.modules.userprofile;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.utility.MediaLocator;


/**
 * ChangeEmailDialog.java
 * Created on Jul 9, 2012
 * Updated on Mar 13, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu; Edited by Derek Zhou, derek.zhou@berkeley.edu, based off 
 * of ChangePasswordDialog.java which was written by Mike Bailey
 * @version $Id$
 */
public class ChangeEmailDialog extends Window
{
  private static final long serialVersionUID = 3019922277051945968L;
  
  private TextField oldEmail, newEmail, newEmail2;
  private EmailPacket packet;
  private NativeButton saveButt;
  //private Object uid;
  public static class EmailPacket
  {
    public String original;
    public String updated;
  }

  @SuppressWarnings("serial")
  public ChangeEmailDialog(EmailPacket pkt)
  {
    this.packet = pkt;
   // this.uid=uid;
    //User user = DBGet.getUser(uid);
    
    setCaption("Change Email");
    setModal(true);
    setWidth("350px");
    //setHeight("200px");
    
    VerticalLayout vLay = new VerticalLayout();
    setContent(vLay);
    FormLayout fLay = new FormLayout();
    oldEmail = new TextField("Current Email",pkt.original);//user.getEmailAddresses().toString());
    //oldPw.setColumns(20);
    oldEmail.setWidth("99%");
    fLay.addComponent(oldEmail);
    newEmail = new TextField("New Email");
    newEmail.setWidth("99%");
    fLay.addComponent(newEmail);
    newEmail2 = new TextField("Confirm Email");
    newEmail2.setWidth("99%");
    fLay.addComponent(newEmail2);
    
    vLay.addComponent(fLay);
    
    HorizontalLayout buttLay = new HorizontalLayout();
    buttLay.setSpacing(true);
    vLay.addComponent(buttLay);
    vLay.setComponentAlignment(buttLay, Alignment.TOP_RIGHT);
    MediaLocator mLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    NativeButton cancelButt = new NativeButton();
    mLoc.decorateCancelButton(cancelButt);
    buttLay.addComponent(cancelButt);
    buttLay.setComponentAlignment(cancelButt, Alignment.BOTTOM_RIGHT);

//    Label sp;
//    buttLay.addComponent(sp = new Label());
//    sp.setWidth("30px");

    saveButt = new NativeButton();
    //app.globs().mediaLocator().decorateSaveButton(saveButt);  //"save"
    mLoc.decorateOkButton(saveButt);      //"ok"
    buttLay.addComponent(saveButt);
    buttLay.setComponentAlignment(saveButt, Alignment.BOTTOM_RIGHT);
    
//    buttLay.addComponent(sp = new Label());
//    sp.setWidth("5px");
    
    cancelButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        UI.getCurrent().removeWindow(ChangeEmailDialog.this);
      }     
    });
    saveButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        String oldTry = oldEmail.getValue().toString();
        if(!packet.original.equals(oldTry)) {
          Notification.show("Error","This should never show",Notification.Type.ERROR_MESSAGE);
          return;
        }
        
        String check = newEmail2.getValue().toString();
        String newStr = newEmail.getValue().toString();
        if(check == null || !newStr.trim().equals(check.trim())) {
          Notification.show("Error","Emails do not match",Notification.Type.ERROR_MESSAGE);
          return;        
        }
        

        EmailValidator v=new EmailValidator("");
        if(newStr == null || !v.isValid(newStr)) {
          Notification.show("Error","Please enter a valid email",Notification.Type.ERROR_MESSAGE);
          return;        
        }
        

        
        packet.updated = newStr.trim();
        if(saveListener != null)
          saveListener.buttonClick(event);
        UI.getCurrent().removeWindow(ChangeEmailDialog.this);
      }  
    });
  }
  
  private ClickListener saveListener;
  public void setSaveListener(ClickListener lis)
  {
    saveListener = lis;
  }
}