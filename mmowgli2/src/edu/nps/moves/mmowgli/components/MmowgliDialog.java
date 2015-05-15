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

package edu.nps.moves.mmowgli.components;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.db.pii.EmailPii;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
import static edu.nps.moves.mmowgli.MmowgliConstants.*;

/**
 * MmowgliDialog.java Created on Feb 18, 2011
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: MmowgliDialog.java 3299 2014-01-24 01:14:48Z tdnorbra $
 */
public abstract class MmowgliDialog extends Window implements MmowgliComponent
{
  private static final long serialVersionUID = 1542794854583759780L;

  protected Button cancelButt;
  private VerticalLayout outerLayout;
  private HorizontalLayout headerHL2;

  protected VerticalLayout contentVLayout;
  protected ClickListener listener;

  protected String topLeftCss = "top:0px;left:0px";
  protected String titleStyle = "m-dialog-title";
  protected String titleStyleSmall = "m-dialog-title-smaller";
  protected String labelStyle = "m-dialog-label";
  protected String labelStyleNoIndent = "m-dialog-label-noindent";
  protected String topLabelStyle = "m-dialog-toplabel";
  private Label titleLab = null;

  public abstract Long getUserId();
  public abstract void setUser(User u);

  public MmowgliDialog(ClickListener listener)
  {
    this.listener = listener;

    setClosable(false);
    setResizable(false);
    setStyleName("m-mmowglidialog");
    addStyleName("m-transparent");   // don't know why I need this, .mmowglidialog sets it too
  }
 
  @Override
  public void initGui()
  {
    outerLayout = new VerticalLayout();
    outerLayout.setSpacing(false);
    outerLayout.setSizeUndefined();
    outerLayout.addStyleName("m-transparent");
    setContent(outerLayout);
   
    Label sp;
    sp = new Label();
    sp.setHeight("100px");
    outerLayout.addComponent(sp);
    
    HorizontalLayout headerWrapper2 = new HorizontalLayout();
    outerLayout.addComponent(headerWrapper2); // at the top
    headerWrapper2.addStyleName("m-dialog-header");
    headerWrapper2.setHeight("75px");
    headerWrapper2.setWidth("592px");
    headerWrapper2.setSpacing(false);
    headerWrapper2.setMargin(false);
    headerWrapper2.addComponent(sp = new Label());  // indent from left
    sp.setWidth("45px");

    headerHL2 = new HorizontalLayout();  // Where the title gets written
    headerHL2.setSpacing(false);
    headerHL2.setMargin(false);
    headerHL2.setHeight("75px");
    headerWrapper2.addComponent(headerHL2);
    headerWrapper2.setExpandRatio(headerHL2, 1.0f);
    
    cancelButt = makeCancelButton();
    cancelButt.addClickListener(new MyCancelListener());
    cancelButt.setClickShortcut(KeyCode.ESCAPE);
    headerWrapper2.addComponent(cancelButt);
    headerWrapper2.setComponentAlignment(cancelButt, Alignment.MIDDLE_CENTER);
    
    headerWrapper2.addComponent(sp=new Label());
    sp.setWidth("15px");
    
    contentVLayout = new VerticalLayout();
    contentVLayout.addStyleName("m-dialog-content");
    contentVLayout.setSizeUndefined();
    contentVLayout.setWidth("592px"); // but do the width explicitly

    outerLayout.addComponent(contentVLayout);

    Image footer = new Image(null, Mmowgli2UI.getGlobals().mediaLocator().getDialogFooterBackground());
    footer.setWidth("592px");
    footer.setHeight("36px");
    outerLayout.addComponent(footer);
  }
  
  protected Button makeCancelButton()
  {
    NativeButton butt = new NativeButton(null);
    butt.setStyleName("m-cancelButton");
    return butt;
  }

  protected void setListener(ClickListener lis)
  {
    this.listener = lis;
  }

  protected void setTitleString(String s)
  {
    setTitleString(s,false);
  }
  
  protected void setTitleString(String s, boolean small)
  {
    if (titleLab != null)
      headerHL2.removeComponent(titleLab);
    titleLab = new Label(s);
    titleLab.addStyleName(small?titleStyleSmall:titleStyle);
    headerHL2.addComponent(titleLab); //, "top:25px;left:50px");
    headerHL2.setComponentAlignment(titleLab, Alignment.MIDDLE_LEFT);
  }

  /**
   * Override by subclass, which normally calls super.cancelClicked(event) when done
   * @param event
   */
  protected void cancelClickedTL(ClickEvent event)
  {
    Long uid = getUserId();
    if (uid != null) {
      User u = User.getTL(getUserId());
      if (u != null) {
        User.deleteTL(u);
        UserPii uPii = VHibPii.getUserPii(uid);
        Long uoid = uPii.getUserObjectId();
        if(uoid != null) {
          EmailPii epii = VHibPii.getUserPiiEmail(uoid);
          VHibPii.delete(epii);
        }
        VHibPii.delete(uPii);

        MSysOut.println(NEWUSER_CREATION_LOGS, "User cancelled (didn't finish login) " + u.getId());
        setUser(null);
      }
    }
    if (listener != null)
      listener.buttonClick(event); // back up the chain
  }

  @SuppressWarnings("serial")
  @MmowgliCodeEntry
  @HibernateOpened
  @HibernateClosed
  class MyCancelListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      cancelClickedTL(event);   // allow subclass to override
      HSess.close();
    }
  }
}
