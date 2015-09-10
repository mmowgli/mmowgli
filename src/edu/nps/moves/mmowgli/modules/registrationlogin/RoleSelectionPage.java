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

package edu.nps.moves.mmowgli.modules.registrationlogin;

import static edu.nps.moves.mmowgli.MmowgliConstants.NEWUSER_CREATION_LOGS;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.GameQuestion;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateUpdate;
import edu.nps.moves.mmowgli.markers.HibernateUserUpdate;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * RoleSelectionPage.java Created on Dec 15, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RoleSelectionPage extends MmowgliDialog
{
  private static final long serialVersionUID = 510839107556363497L;
  
 // private ComboBox rolesCb; //, expertiseCb;
  private TextField expertiseTf;
  
  private NativeButton continueButt;
  private TextArea ansTf;
  private GameQuestion ques;
  private Long localUserId;
  private CheckBox emailCb, messagesCb;
  
  public RoleSelectionPage(ClickListener listener, Long uId)
  {
    super(listener);
    super.initGui();
    this.localUserId = uId;
    
    setTitleString("Last Step: tell others of your interests"); //"Role Selection");

    contentVLayout.setSpacing(true);
    contentVLayout.setMargin(true);
    contentVLayout.addStyleName("m-role-page"); 
    
    Label lab;
    contentVLayout.addComponent(lab = new HtmlLabel("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This optional information is revealed to other players."));
    lab.addStyleName(labelStyle);
    contentVLayout.addComponent(lab=new Label());
    lab.setHeight("10px");

    expertiseTf = new TextField();
    expertiseTf.addStyleName("m-noleftmargin");
    expertiseTf.setCaption("Enter a short description of your pertinent expertise.");
    expertiseTf.setColumns(38);
    expertiseTf.setInputPrompt("optional");
    contentVLayout.addComponent(expertiseTf);
    
    Game game = Game.getTL();
    ques = game.getQuestion();

    ansTf = new TextArea(ques.getQuestion());
    ansTf.setWidth("98%");
    ansTf.setRows(10);
    ansTf.setInputPrompt("(optional, but worth 10 points if you answer)");
    contentVLayout.addComponent(ansTf);

    emailCb = new CheckBox("I agree to receive private email during game play.");
    contentVLayout.addComponent(emailCb);
    emailCb.addStyleName(labelStyle);
    emailCb.addStyleName("m-nopaddingormargin");
    emailCb.setValue(true);

    messagesCb = new CheckBox("I agree to receive private in-game messages during game play.");
    contentVLayout.addComponent(messagesCb);
    messagesCb.addStyleName(labelStyle);
    messagesCb.addStyleName("m-nopaddingormargin");
    messagesCb.setValue(true);
    
    HorizontalLayout buttPan = new HorizontalLayout();
    buttPan.setWidth("100%");
    buttPan.setSpacing(true);
    
    buttPan.addComponent(lab = new Label("OK great, thanks for registering!  Let's play."));
    lab.addStyleName(labelStyle);
    lab.addStyleName("m-nopaddingormargin");
    lab.setSizeUndefined();
    
    Label spacer;
    buttPan.addComponent(spacer = new Label());
    spacer.setWidth("1px");
    buttPan.setExpandRatio(spacer, 1.0f);
    
    buttPan.addComponent(continueButt = new NativeButton(null));
    Mmowgli2UI.getGlobals().mediaLocator().decorateGetABriefingButton(continueButt);

    Label sp;   
    buttPan.addComponent(sp = new Label());
    sp.setWidth("10px");
    
    contentVLayout.addComponent(buttPan);
    
    continueButt.addClickListener(new ContinueListener()); 
    continueButt.setClickShortcut(KeyCode.ENTER);
    expertiseTf.focus();
  }
  
  @SuppressWarnings("serial")
  @HibernateUpdate
  @HibernateUserUpdate
  class LaterListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      User _usr = User.getTL(localUserId);
      MSysOut.println(NEWUSER_CREATION_LOGS,"Expertise/get-email dialog \"later\" button clicked, userID "+_usr.getUserName());
      _usr.setQuestion(ques);  // This is what was asked
      User.updateTL(_usr);
      HSess.close();
      listener.buttonClick(event);  // up the chain
    }   
  }
  
  @SuppressWarnings("serial")
  @HibernateUpdate
  @HibernateUserUpdate
  class ContinueListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      User _usr = User.getTL(localUserId);
      MSysOut.println(NEWUSER_CREATION_LOGS,"Expertise/get-email dialog continue button clicked, userID "+_usr.getUserName());
//      userID.setRole((Role)rolesCb.getValue());
//      o = expertiseCb.getValue();
//      if(o != null)
//        userID.setExpertise(o.toString());
      Object o = expertiseTf.getValue();
      if(o != null)
        _usr.setExpertise(o.toString());
      
      _usr.setOkEmail(emailCb.getValue());
      _usr.setOkGameMessages(messagesCb.getValue());
      
      _usr.setAnswer(checkValue(ansTf));
      _usr.setQuestion(ques);
      User.updateTL(_usr);
      
      GameEventLogger.logNewUserTL(_usr);
      HSess.close();
      
      listener.buttonClick(event); // up the chain
    }    
  }
  
  private String checkValue(AbstractTextField tf)
  {
    Object o = tf.getValue();
    if(o != null) {
      String s = o.toString();
      if(s.length()>255)         // answer col in db should be glob, but until that's fixed, clamp at varchar(255)
        s = s.substring(0,254);
      return s;
    }
    return null;
  }
  
  @Override
  public Long getUserId()
  {
    return localUserId;
  }

  @Override
  public void setUser(User u)
  {
    this.localUserId = (u==null?null:u.getId());   
  }
  
  // Used to center the dialog
  public int getUsualWidth()
  {
    return 585;
  }
}
