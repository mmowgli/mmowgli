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

package edu.nps.moves.mmowgli.signupServer;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.GameLinks;
import edu.nps.moves.mmowgli.db.pii.Query2Pii;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * SignupWindow.java
 * Created on Dec 21, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class SignupWindow extends VerticalLayout//Window
{
  private static final long serialVersionUID = -7472323396288688209L;
  
  private TextField emailTF;
  private TextField interestTF;
  private String gameImagesUrl;
  private final SignupServer ui;
  private String appUrl;
  
  @HibernateSessionThreadLocalConstructor
  public SignupWindow(String title, SignupServer ssui)
  {
   // super(title);
    this.ui = ssui;
    gameImagesUrl = AppMaster.instance().getGameImagesUrlString();
    if(!gameImagesUrl.endsWith("/"))
      gameImagesUrl = gameImagesUrl+"/";
    appUrl = Page.getCurrent().getLocation().toString();
    appUrl = appUrl.substring(0,appUrl.lastIndexOf("/"));
    addComponent(new Content());
  //  setContent(new Content());
  //  this.setHeightUndefined();
//    this.addCloseListener(new CloseListener()
//    {
//      @Override
//      public void windowClose(CloseEvent e)
//      {
//        ui.quitAndGoTo(appUrl);        
//      }     
//    });
  }
  
  String thanksHdr1 = "Please sign up below for the <a href='";
  String thanksHdr2 = "'>";
  String thanksHdr3 = "</a> game.";
  
  //String bannerUrl = "https://web.mmowgli.nps.edu/mmowMedia/images/mmowgli_logo_final.png";
  String bannerWidthPx = "400px";
  String bannerHeightPx = "114px";
  
  String thanksForInterestLink = null;
  String aboutLink = null;
  
  class Content extends VerticalLayout implements ClickListener
  {
    private static final long serialVersionUID = 1L;

    @HibernateSessionThreadLocalConstructor
    public Content()
    {
      Label lab;
      Button submitButton;
      setMargin(true);
      lab = new Label();
      lab.setHeight("30px");
      addComponent(lab);

      Game g = Game.getTL();
      GameLinks gl = GameLinks.getTL();
      String signupImgLink = g.getCurrentMove().getCurrentMovePhase().getSignupHeaderImage();
      if(signupImgLink != null) {
        if(!signupImgLink.toLowerCase().startsWith("http"))
          signupImgLink = gameImagesUrl+signupImgLink;
        
        Embedded mmowBanner = new Embedded(null,new ExternalResource(signupImgLink));
        mmowBanner.setWidth(bannerWidthPx);
        mmowBanner.setHeight(bannerHeightPx);
        mmowBanner.addClickListener(new headerListener());
        mmowBanner.addStyleName("m-cursor-pointer");
        addComponent(mmowBanner);
        setComponentAlignment(mmowBanner, Alignment.MIDDLE_CENTER);

        lab = new Label();
        lab.setHeight("15px");
        addComponent(lab);
      }
      
      VerticalLayout vl = new VerticalLayout();
      addComponent(vl);
      setComponentAlignment(vl,Alignment.MIDDLE_CENTER);
      vl.setWidth("800px"); //"66%");
      //vl.addStyleName("m-greyborder");
      vl.addStyleName("m-greyborder3");
      vl.addStyleName("m-mmowglidialog2-middle"); // after a while, change to this .m-background-white
      vl.setMargin(true);
      vl.setSpacing(true);

      SignupWindow.this.thanksForInterestLink = gl.getThanksForInterestLink();
      SignupWindow.this.aboutLink = gl.getAboutLink();

      String brand = g.getCurrentMove().getTitle();
     // SignupWindow.this.setCaption("Signup for "+brand+" mmowgli");
      ui.getPage().setTitle("Signup for "+brand+" mmowgli");
      String blog = gl.getBlogLink();
      String mainText = g.getCurrentMove().getCurrentMovePhase().getSignupText();

      vl.addComponent(lab = new HtmlLabel(""));
      lab.addStyleName("m-font-21-bold");
      lab.setSizeUndefined();
      //lab.setHeight("50px");
      vl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
      StringBuilder sb = new StringBuilder();
      sb.append(thanksHdr1);
      sb.append(blog);
      sb.append(thanksHdr2);
      sb.append(brand);
      sb.append(thanksHdr3);
      lab.setValue(sb.toString());

      vl.addComponent(lab = new HtmlLabel(mainText));
      vl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

      vl.addComponent(lab = new HtmlLabel(""));
      lab.setHeight("15px");

      vl.addComponent(new HtmlLabel("<b>Email address:</b>")); 
      vl.addComponent(emailTF = new TextField());
      emailTF.setWidth("100%");
      vl.addComponent(new HtmlLabel("<b>What is your interest in mmowgli?</b>"));
      vl.addComponent(interestTF = new TextField());
      interestTF.setInputPrompt("required for approval");
      interestTF.setWidth("100%");

      HorizontalLayout butts = new HorizontalLayout();
      butts.setSpacing(true);
      vl.addComponent(butts);
      butts.addComponent(submitButton = new Button("Signup"));
      submitButton.addClickListener(this);
      butts.addComponent(new Button("Cancel", new ClickListener()
      {
        private static final long serialVersionUID = 1L;
        @Override
        public void buttonClick(ClickEvent event)
        {
          ui.quitAndGoTo(appUrl);
        }
      }));
      
      /*
      Embedded npsBanner = new Embedded(null,new ExternalResource(npsUrl));
      npsBanner.setWidth(npsWidthPx);
      npsBanner.setHeight(npsHeightPx);
      addComponent(npsBanner);
      setComponentAlignment(npsBanner, Alignment.MIDDLE_CENTER);
      */

      vl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
      vl.addComponent(lab=new Label());
      vl.setExpandRatio(lab, 1.0f);
    }

    @Override
    public void buttonClick(ClickEvent event)
    {
      String email = emailTF.getValue().toString();
      if(email == null ||
         (email.length() <= 0) ||
         !email.contains("@") ) {
        new Notification(
            "Invalid email.",
            "Please enter a valid email address<br/>to be notified when mmowgli is ready to play.",
            Notification.Type.ERROR_MESSAGE,true).show(Page.getCurrent());
        return;
      }
      Query2Pii q = SignupHandler.getQuery2WithEmail(email);
      if(q != null) {
        Notification not = new Notification(
            "We've already got you!",
            "This email address has already been submitted. Thanks!",
            Notification.Type.WARNING_MESSAGE);
        not.setPosition(Position.TOP_CENTER); //to miss video
        not.show(Page.getCurrent());
        return;
      }
      SignupHandler.handle(email, interestTF.getValue().toString());
      
      ui.quitAndGoTo(SignupWindow.this.thanksForInterestLink);
    }
  }
  
  class headerListener implements MouseEvents.ClickListener
  {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void click(com.vaadin.event.MouseEvents.ClickEvent event)
    {
      ui.quitAndGoTo(SignupWindow.this.aboutLink);
    }   
  }
}
