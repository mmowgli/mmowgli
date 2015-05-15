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
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

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
 * MmowgliDialog2.java
 * Created on Aug 31, 2011
 * Updated on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class MmowgliDialog2 extends Window implements MmowgliComponent
{
  private static final long serialVersionUID = 6482190374611558218L;
  
  protected ClickListener listener;

  public abstract User getUser();
  public abstract void setUser(User u);

  protected VerticalLayout contentVLayout;
  private MmowgliDialogContent content;

  public MmowgliDialog2(ClickListener listener)
  {
    this.listener = listener;

    setClosable(false);
    setResizable(false);
    setStyleName("m-mmowglidialog2");
    addStyleName("m-transparent");   // don't know why I need this, .mmowglidialog sets it too
  }

  @Override
  public void initGui()
  {
    content = new MmowgliDialogContent();
    setContent(content);
    content.setSizeFull();
    content.initGui();
    contentVLayout = content.getContentVLayout();
    content.setCancelListener(new ThisCancelListener());
  }

  protected void setTitleString(String s)
  {
    setTitleString(s,false);
  }

  public void setTitleString(String s, boolean small)
  {
    content.setTitleString(s,small);
  }

  protected void setListener(ClickListener lis)
  {
    this.listener = lis;
  }

  /**
   * Override by subclass, which normally calls super.cancelClicked(event) when done
   * @param event
   */
  protected void cancelClickedTL(ClickEvent event)
  {
    User u = getUser();
    if(u != null) {
      User.deleteTL(u);
      UserPii uPii = VHibPii.getUserPii(u.getId());
      Long uoid = uPii.getUserObjectId();
      if(uoid != null) {
      	EmailPii epii = VHibPii.getUserPiiEmail(uoid);
      	VHibPii.delete(epii);
      }
      VHibPii.delete(uPii);

      MSysOut.println(NEWUSER_CREATION_LOGS,"User cancelled (didn't finish login) "+u.getId());
    }

    setUser(null);
    if(listener != null)
      listener.buttonClick(event); // back up the chain
  }

  @SuppressWarnings("serial")
  @MmowgliCodeEntry
  @HibernateOpened
  @HibernateClosed
  class ThisCancelListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      Object key = HSess.checkInit();
      cancelClickedTL(event);   // allow subclass to override
      HSess.checkClose(key);
    }
  }

}
