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

package edu.nps.moves.security;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.HasUUID;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.modules.userprofile.ChangePasswordDialog;
import edu.nps.moves.mmowgli.modules.userprofile.ChangePasswordDialog.PasswordPacket;

@Theme("mmowgli2")
public class PasswordResetUI extends UI implements ClickListener, HasUUID
{
  private static final long serialVersionUID = 3824891380520659358L;
  private String UUID_PARAM = "uid";
  private String RESETCODE_COL = "resetCode";
  private UUID uuid;
  
  @SuppressWarnings("serial")
  @Override
  protected void init(VaadinRequest request)
  {
    Timestamp rightNow = new Timestamp(System.currentTimeMillis());
    uuid = UUID.randomUUID();

    String uid = request.getParameter(UUID_PARAM);
    System.out.println("uid = " + uid);

    VerticalLayout vLay = new VerticalLayout();
    setContent(vLay);

    if (uid == null) {
      vLay.addComponent(new Label("Bad URL"));
      ;
    }
    else {
      HSess.init();
      Session sess = HSess.get();

      @SuppressWarnings("unchecked")
      List<PasswordReset> lis = sess.createCriteria(PasswordReset.class).add(Restrictions.eq(RESETCODE_COL, uid)).list();

      if (lis == null || lis.isEmpty())
        vLay.addComponent(new Label("Bad URL2"));
      else {
        PasswordReset pr = lis.get(0);
        Timestamp expireDate = pr.getExpireDate();
        if (rightNow.getTime() > expireDate.getTime())
          vLay.addComponent(new Label("PasswordReset request has timed out.  Please initiate new request."));
        else {
          handleChangeTL(pr.getUser());
          HSess.close();
          return;
        }
      }
      HSess.close();
    }
    vLay.addComponent(new Button("Go to help page", new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        String troubleUrl = GameLinks.getTL().getTroubleLink();
        HSess.close();
        getPage().setLocation(troubleUrl);
        getSession().close();
      }
    }));
    
  }

  String thanksHdr1 = "Thanks for playing the <a href='";
  String thanksHdr2 = "'>";
  String thanksHdr3 = "</a> game.";

  String bannerWidthPx = "550px";
  NativeButton saveButt = null;
  PasswordField newPw = null;
  PasswordField newPw2 = null;
  private PasswordPacket packet;

  private User user;

  private void handleChangeTL(User user)
  {
    this.user = user;
    Game g = Game.getTL();

    String brand = g.getCurrentMove().getTitle();
    Page.getCurrent().setTitle("Password reset for " + brand + " mmowgli");

    HorizontalLayout hLay = new HorizontalLayout();
    hLay.setMargin(true);
    hLay.setWidth("100%");
    setContent(hLay);
    
    GameLinks gl = GameLinks.getTL();
    String blog = gl.getBlogLink();
    Label lab;
    hLay.addComponent(lab=new Label());
    hLay.setExpandRatio(lab, 0.5f);
    
    VerticalLayout vl = new VerticalLayout();
    hLay.addComponent(vl);
    vl.setWidth(bannerWidthPx);
    
    hLay.addComponent(lab=new Label());
    hLay.setExpandRatio(lab, 0.5f);

    vl.addStyleName("m-greyborder");
    vl.setMargin(true);
    vl.setSpacing(true);

    vl.addComponent(lab = new HtmlLabel(""));
    lab.addStyleName("m-font-21-bold");
    lab.setSizeUndefined();
    vl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
    StringBuilder sb = new StringBuilder();
    sb.append(thanksHdr1);
    sb.append(blog);
    sb.append(thanksHdr2);
    sb.append(brand);
    sb.append(thanksHdr3);
    lab.setValue(sb.toString());

    vl.addComponent(lab = new HtmlLabel(""));
    lab.setHeight("15px");
    vl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

    FormLayout fLay = new FormLayout();
    fLay.setSizeUndefined();
    fLay.addStyleName("m-login-form"); // to allow styling contents (v-textfield)
    vl.addComponent(fLay);
    vl.setComponentAlignment(fLay, Alignment.MIDDLE_CENTER);

    newPw = new PasswordField("New Password");
    newPw.setWidth("99%");
    fLay.addComponent(newPw);

    newPw2 = new PasswordField("Again, please");
    newPw2.setWidth("99%");
    fLay.addComponent(newPw2);

    HorizontalLayout buttLay = new HorizontalLayout();
    buttLay.setSpacing(true);
    vl.addComponent(buttLay);
    vl.setComponentAlignment(buttLay, Alignment.TOP_CENTER);
    
    NativeButton cancelButt = new NativeButton();
    cancelButt.setStyleName("m-cancelButton");
    buttLay.addComponent(cancelButt);
    buttLay.setComponentAlignment(cancelButt, Alignment.BOTTOM_RIGHT);

    saveButt = new NativeButton();
    saveButt.setClickShortcut(ShortcutAction.KeyCode.ENTER);
    saveButt.setStyleName("m-continueButton"); //m-saveChangesButton");
    buttLay.addComponent(saveButt);
    buttLay.setComponentAlignment(saveButt, Alignment.BOTTOM_RIGHT);

    cancelButt.addClickListener(this);
    saveButt.addClickListener(this);
  }

  @Override
  public void buttonClick(ClickEvent event)
  {
    HSess.init();
    if (event.getButton() == saveButt) {
      String newStr = newPw.getValue().toString();
      if (newStr == null || newStr.length() < 6) {
        Notification.show("Error", "Enter a password of at least six characters", Notification.Type.ERROR_MESSAGE);
        HSess.close();
        return;
      }

      String check = newPw2.getValue().toString();
      if (check == null || !newStr.trim().equals(check.trim())) {
        Notification.show("Error", "Passwords do not match", Notification.Type.ERROR_MESSAGE);
        HSess.close();
        return;
      }

      packet = new ChangePasswordDialog.PasswordPacket();
      packet.updated = newStr.trim();

      // We're not in a normal Vaadin event context here. PiiHibernate knows that, but we need to explicitly go OOB
      // for the GameEventLog save.
      UserPii uPii = VHibPii.getUserPii(user.getId());
      uPii.setPassword(new StrongPasswordEncryptor().encryptPassword(packet.updated));
      VHibPii.update(uPii);

      GameEventLogger.logUserPasswordChangedTL(user);

      // Clean up for security
      packet.original = null;
      packet.updated = null;

      Notification.show("Password Change Successful! Return to your login window and join Mmowgli.");
    }
    else {
      getPage().setLocation(GameLinks.getTL().getThanksForInterestLink());
    }
    
    HSess.close();
    getSession().close();
  }
  
  public String getUI_UUID()
  {
    return uuid.toString();
  }  
}
