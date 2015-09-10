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

package edu.nps.moves.mmowgli;

import static edu.nps.moves.mmowgli.MmowgliConstants.MESSAGING_LOGS;

import java.io.Serializable;

import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.components.AppMenuBar;
import edu.nps.moves.mmowgli.components.Footer;
import edu.nps.moves.mmowgli.components.Header;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.messaging.*;
import edu.nps.moves.mmowgli.utility.Instrumentation;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * MmowgliOuterFrame.java
 * Created on Jan 27, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliOuterFrame extends VerticalLayout implements WantsMoveUpdates, WantsMovePhaseUpdates, WantsGameUpdates, WantsGameEventUpdates
{
  private static final long serialVersionUID = 6619931431041760684L;
  private Header header;
  private Footer footer;
  private MmowgliContentFrame mContentFr;
  private AppMenuBar menubar;
  private Component fouoButton;
  
  public MmowgliOuterFrame(User me)
  {
    setSizeUndefined();
    setWidth("1040px");
    addStyleName("m-mmowgliouterframe");
   // addStyleName("m-redborder");   this is a good debugging border
    if(me == null)
      me = Mmowgli2UI.getGlobals().getUserTL();

    addMenuBarAndFouoRowTL(me);
    addComponent(header=new Header());
    header.initGui();
    addComponent(mContentFr = new MmowgliContentFrame());
    addComponent(footer=new Footer());
    footer.initGui();
    Instrumentation.addInstrumentation(this);
  }
  
  public void pingPush()
  {
  	footer.pingPush();
  }
  
  public void addMenuBarAndFouoRowTL(User me)
  {
    HorizontalLayout hlay = new MHorizontalLayout().withMargin(false).withSpacing(false).withFullHeight();
    addComponent(hlay);
    
    if(me.isGameMaster() || me.isAdministrator() || me.isDesigner()) {
      menubar = new AppMenuBar(me.isGameMaster(),me.isAdministrator(),me.isDesigner());
      hlay.addComponent(menubar);
      hlay.setExpandRatio(menubar, 0.5f);
      hlay.setComponentAlignment(menubar, Alignment.TOP_LEFT);
    }
    else {
      Label lab = new Label();
      hlay.addComponent(lab);
      hlay.setExpandRatio(lab, 0.5f);
    }
    hlay.addComponent(fouoButton = makeFouoButtonTL());
    
    Label lab = new Label();
    hlay.addComponent(lab);
    hlay.setExpandRatio(lab, 0.5f);
  }
  
  private Component makeFouoButtonTL()
  {
    Component comp =  Footer.buildFouoNoticeTL();
    comp.setVisible(Game.getTL().isShowFouo());
    return comp;
  }
  
  public void setFrameContent(Component c)
  {
    mContentFr.setFrameContent(c);
  }
  
  public Component getFrameContent()
  {
    return mContentFr.getFrameContent();
  }
  
  public ComponentContainer getContentContainer()
  {
    return mContentFr.getContentContainer();
  }
  
  public AppMenuBar getMenuBar()
  {
    return menubar;
  }

  public boolean refreshUser_oobTL(Object uId)
  {
    return header.refreshUserTL(uId);
  }

  public void showOrHideFouoButton(boolean show)
  {
    fouoButton.setVisible(show);
    footer.showHideFouoButton(show);    
  }

  @Override
  public boolean moveUpdatedOobTL(Serializable mvId)
  {
    return header.moveUpdatedOobTL(mvId);
  }

  public boolean movePhaseUpdatedOobTL(Serializable pId)
  {
    return false; // header doesn't use it
  }

  @Override
  public boolean gameUpdatedExternallyTL(Object nullObj)
  {
    MSysOut.println(MESSAGING_LOGS,"MmowgliOuterFrame.gameUpdatedExternally()");
    boolean ret = false;

    boolean fouoShow = Game.getTL().isShowFouo();
    MSysOut.println(MESSAGING_LOGS,"Game object.isShowFouo = "+fouoShow);
    if (fouoShow != fouoButton.isVisible()) {
      fouoButton.setVisible(fouoShow);
      ret = true;
    }
    if (header.gameUpdatedExternallyTL(null))
      ret = true;
    if (footer.gameUpdatedExternallyTL(null))
      ret = true;
    return ret;
  }

  @Override
  public boolean gameEventLoggedOobTL(Object evId)
  {
    return header.gameEventLoggedOobTL(evId);
  }
}
