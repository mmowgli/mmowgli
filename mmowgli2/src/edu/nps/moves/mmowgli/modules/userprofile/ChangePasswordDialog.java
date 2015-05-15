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

import org.jasypt.util.password.StrongPasswordEncryptor;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * ChangePasswordDialog.java
 * Created on Mar 21, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ChangePasswordDialog extends Window
{
  private static final long serialVersionUID = 6956067556135345125L;
  
  private PasswordField oldPw, newPw, newPw2;
  private PasswordPacket packet;

  private NativeButton saveButt;
  public static class PasswordPacket
  {
    public String original;
    public String updated;
  }

  @SuppressWarnings("serial")
  public ChangePasswordDialog(PasswordPacket pkt)
  {
    this.packet = pkt;
    
    setCaption("Change Password");
    setModal(true);
    setWidth("350px");
    
    VerticalLayout vLay = new VerticalLayout();
    setContent(vLay);
    FormLayout fLay = new FormLayout();
    oldPw = new PasswordField("Current");
    //oldPw.setColumns(20);
    oldPw.setWidth("99%");
    fLay.addComponent(oldPw);
    newPw = new PasswordField("New");
    newPw.setWidth("99%");
    fLay.addComponent(newPw);
    newPw2 = new PasswordField("New again");
    newPw2.setWidth("99%");
    fLay.addComponent(newPw2);
    
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
        UI.getCurrent().removeWindow(ChangePasswordDialog.this);
      }     
    });
    saveButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        String oldTry = oldPw.getValue().toString();
        StrongPasswordEncryptor spe = new StrongPasswordEncryptor();
        if(!spe.checkPassword(oldTry, packet.original)) {
          Notification.show("Error","Existing password incorrect",Notification.Type.ERROR_MESSAGE);
          return;
        }
        
        String newStr = newPw.getValue().toString();
        if(newStr == null || newStr.length()<6) {
          Notification.show("Error","Enter a password of at least six characters",Notification.Type.ERROR_MESSAGE);
          return;        
        }
        String check = newPw2.getValue().toString();
        if(check == null || !newStr.trim().equals(check.trim())) {
          Notification.show("Error","Passwords do not match",Notification.Type.ERROR_MESSAGE);
          return;        
        }
        
        packet.updated = newStr.trim();
        if(saveListener != null)
          saveListener.buttonClick(event);
        UI.getCurrent().removeWindow(ChangePasswordDialog.this);
      }  
    });
  }
  
  private ClickListener saveListener;
  public void setSaveListener(ClickListener lis)
  {
    saveListener = lis;
  }
}
