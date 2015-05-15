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

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * UserProfileTabPanel.java
 * Created on Mar 15, 2011
 * Updated on Mar 13, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class UserProfileTabPanel extends HorizontalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = -2048617558073728089L;
  protected Object uid;
  protected boolean userIsMe = false;
  protected User me;
  protected String userName = "";
  protected boolean imAdminOrGameMaster = false;
  
  private VerticalLayout leftLay;
  private VerticalLayout rightLay;
  private VerticalLayout leftAddedVL;
  private HtmlLabel leftLabel;
  
  @HibernateSessionThreadLocalConstructor
  public UserProfileTabPanel(Object uid)
  {;
    this.uid = uid;
    User u = User.getTL(uid);
    userName = u.getUserName();
    me = Mmowgli2UI.getGlobals().getUserTL();
    imAdminOrGameMaster = me.isAdministrator() || me.isGameMaster();    
    userIsMe = (u.getId() == me.getId());
  }
  
  @Override
  public void initGui()
  {
    setWidth("960px");
    setHeight("750px");  // must fill the background
    setSpacing(false);
    
    leftLay = new VerticalLayout();
    leftLay.addStyleName("m-userprofiletabpanel-left");
    leftLay.setWidth("245px");// plus 45 padding 
    leftLay.setMargin(false);
    
    Label sp;
    leftLay.addComponent(sp=new Label());
    sp.setHeight("45px");
    
    leftLabel = new HtmlLabel("placeholder");
    leftLay.addComponent(leftLabel);

    leftAddedVL = new VerticalLayout();
    leftLay.addComponent(leftAddedVL);
    leftAddedVL.setWidth("100%");
    
    leftLay.addComponent(sp=new Label());
    sp.setHeight("1px");
    leftLay.setExpandRatio(sp, 1.0f); // bottom filler
 
    
    rightLay = new VerticalLayout();
    rightLay.setSizeUndefined();  // will expand with content

    rightLay.addStyleName("m-tabpanel-right");
    rightLay.addStyleName("m-userprofile-tabpanel-font");
    
    addComponent(leftLay);
    
    addComponent(sp = new Label());
    sp.setWidth("15px");
    
    addComponent(rightLay);
    setComponentAlignment(rightLay,Alignment.TOP_CENTER); //del if no help
    this.setExpandRatio(rightLay, 1.0f);
  }
  
  public Label getLeftLabel()
  {
    return leftLabel;
  }
  
  public VerticalLayout getLeftLayout()
  {
    return leftLay;
  }
  public VerticalLayout getLeftAddedVerticalLayout()
  {
    return leftAddedVL;
  }
  public VerticalLayout getRightLayout()
  {
    return rightLay;
  }
  
  protected HorizontalLayout makeTableHeaders()
  {
    HorizontalLayout titleHL = new HorizontalLayout();
    titleHL.setSpacing(true);
    titleHL.setWidth("100%");
    Label lab;
    lab=buildTitleLabel(titleHL,"<center>Creation<br/>Date</center>"); 
    lab.setWidth(4.0f, Sizeable.Unit.EM);
    lab=buildTitleLabel(titleHL,"<center>Card<br/>Type</center>");
    lab.setWidth(6.0f, Sizeable.Unit.EM);
    lab=buildTitleLabel(titleHL,"Text");
    titleHL.setExpandRatio(lab, 1.0f);
    lab=buildTitleLabel(titleHL,"Author");
    lab.setWidth(8.0f, Sizeable.Unit.EM);
    return titleHL;
  }
  
  
  private Label buildTitleLabel(HorizontalLayout c, String s)
  {
    Label lab = new HtmlLabel(s);
    lab.addStyleName("m-tabpanel-right-title");
    c.addComponent(lab);
    c.setComponentAlignment(lab, Alignment.MIDDLE_LEFT);
    return lab;
  }
 }
