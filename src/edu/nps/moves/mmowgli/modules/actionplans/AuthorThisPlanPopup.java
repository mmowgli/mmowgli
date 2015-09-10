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

package edu.nps.moves.mmowgli.modules.actionplans;

import java.util.Set;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.HibernateUpdate;
import edu.nps.moves.mmowgli.markers.HibernateUserUpdate;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;

/**
 * AuthorThisPlanPopup.java
 * Created on Mar 3, 2011
 * Updated on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AuthorThisPlanPopup extends MmowgliDialog implements ClickListener
{
  private static final long serialVersionUID = 5539097111663953895L;
  
  private Object apId;
  private Button okButt,noButt;
  
  @SuppressWarnings("serial")
  public AuthorThisPlanPopup(Object apPlnId)
  {
    super(null);
    super.initGui();
    this.apId = apPlnId;
    
    setListener(this);

    setTitleString("Author This Plan");
  
    contentVLayout.setSpacing(true);
 
    Label lab;
    contentVLayout.addComponent(lab = new Label());
    lab.setHeight("5px");
    
    contentVLayout.addComponent(lab = new Label("Become an author of this plan?"));
    lab.addStyleName("m-dialog-text");
    lab.setWidthUndefined();
    contentVLayout.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
    
    HorizontalLayout buttHL = new HorizontalLayout();
    contentVLayout.addComponent(buttHL);
    contentVLayout.setComponentAlignment(buttHL, Alignment.MIDDLE_CENTER);
    buttHL.setSpacing(true);
    
    buttHL.addComponent(okButt = new Button("Yes, I'm in."));
    buttHL.addComponent(noButt = new Button("I'll pass."));
    noButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      @HibernateUpdate
      @HibernateUserUpdate
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        User me = Mmowgli2UI.getGlobals().getUserTL();
        ActionPlan ap = ActionPlan.getTL(apId);
        
        if(usrContainsByIds(ap.getInvitees(),me))
          ap.removeInvitee(me);
        if(!usrContainsByIds(ap.getDeclinees(),me))
          ap.getDeclinees().add(me);
        ActionPlan.updateTL(ap);
        
        if(apContainsByIds(me.getActionPlansInvited(),ap))
          me.getActionPlansInvited().remove(ap);
        
        User.updateTL(me);        
        GameEventLogger.logActionPlanInvitationDeclinedTL(ap, me.getUserName());
        HSess.close();
        
        AuthorThisPlanPopup.this.buttonClick(event);
      }     
    });
    
    okButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      @HibernateUpdate
      @HibernateUserUpdate
      public void buttonClick(ClickEvent event)
      { 
        HSess.init();
        MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
        User me = globs.getUserTL(); //feb refactor DBGet.getUserFreshTL(globs.getUserID());
        ActionPlan thisAp = ActionPlan.getTL(apId);

        Set<ActionPlan> myInvites = me.getActionPlansInvited();
        Set<ActionPlan> myAuthored = me.getActionPlansAuthored();
        
        boolean usrNeedsUpdate = false;
        if(apContainsByIds(myInvites,thisAp)) {
          //System.out.println("AP-AUTHOR_DEBUG:  removing aplan from users invite list, AuthorThisPlanPopup.128");           
          myInvites.remove(thisAp);
          // Jam it in here
          //ScoreManager.userJoinsActionPlan(me);  // replace by...
          globs.getScoreManager().actionPlanUserJoinsTL(thisAp,me);
          usrNeedsUpdate=true;          
        }
        if(!apContainsByIds(myAuthored,thisAp)) {// if already there, causes exception 
          //System.out.println("AP-AUTHOR_DEBUG:  adding aplan to users authored list, AuthorThisPlanPopup.133");           
          myAuthored.add(thisAp);
          usrNeedsUpdate=true;         
        }
        if(usrNeedsUpdate) {
          // User update here
          User.updateTL(me);
        }
        
        boolean apNeedsUpdate = false;
        if(usrContainsByIds(thisAp.getInvitees(),me)) {
          //System.out.println("AP-AUTHOR_DEBUG:  removing user from ap invite list, AuthorThisPlanPopup.146");                     
          thisAp.removeInvitee(me); //apInvitees.remove(me);
          apNeedsUpdate=true;
        }
        if(!usrContainsByIds(thisAp.getAuthors(),me)) {
          //System.out.println("AP-AUTHOR_DEBUG:  adding user to ap authors list, AuthorThisPlanPopup.151");
          thisAp.addAuthor(me); //apAuthors.add(me);
          apNeedsUpdate=true;
        }
        if(apNeedsUpdate)
          ActionPlan.updateTL(thisAp);
        
        GameEventLogger.logActionPlanInvitationAcceptedTL(thisAp, me.getUserName());

        HSess.close(); 
        
        AuthorThisPlanPopup.this.buttonClick(event);   // sets up its own session
        return;
      }     
    });
   }
  
  boolean apContainsByIds(Set<ActionPlan> set, ActionPlan ap)
  {
    for(ActionPlan actPln : set)
      if(actPln.getId() == ap.getId())
        return true;
    return false;
  }
  
  boolean usrContainsByIds(Set<User> set, User u)
  {
    for(User usr : set)
      if(usr.getId() == u.getId())
        return true;
    return false;
  }
  
  @Override
  public void buttonClick(ClickEvent event)  // has TL session
  {
    UI.getCurrent().removeWindow(this);
  }

  @Override
  public Long getUserId()
  {
    return null;
  }

  @Override
  public void setUser(User u)
  {

  }
}