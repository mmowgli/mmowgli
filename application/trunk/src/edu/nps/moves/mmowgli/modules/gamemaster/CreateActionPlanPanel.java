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

package edu.nps.moves.mmowgli.modules.gamemaster;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.cache.MCacheUserHelper.QuickUser;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPage2.UserList;
import edu.nps.moves.mmowgli.modules.actionplans.AddAuthorDialog;
import edu.nps.moves.mmowgli.modules.cards.CardChainTreeTablePopup;

/**
 * CreateActionPlanPanel.java Created on Mar 30, 2011
 * Updated 13 Mar, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CreateActionPlanPanel extends Panel implements MmowgliComponent
{
  private static final long serialVersionUID = 8371676076359330962L;
  
  private CreateActionPlanLayout layout;
  
  public CreateActionPlanPanel()
  {
    this(null, null);
  }

  public CreateActionPlanPanel (Object apId, Object rootCardId)
  {
    setWidth("100%");
    setHeight("600px");

    layout = new CreateActionPlanLayout(apId, rootCardId, null);
    setContent(layout);
    layout.setSizeFull();
  }

  public void initGui()
  {
    layout.initGui();
  }

  public static class CreateActionPlanLayout extends VerticalLayout implements MmowgliComponent
  {
    private static final long serialVersionUID = 5206619447249427630L;
    private ActionPlan ap;
    private Object apId;
    private Object rootCardId;
    private Game game; // for default text
    private boolean newAp;
    private TreeSet<User> invitees;
    private UserList inviteeLis;
    private Button inviteButt;

    private Button clearListButt;
    // private CheckBox includeAuthorsCheck;
    private Button includeAuthorsButt;
    private HtmlLabel cardText;
    private TextField cardIdTF;
    private ClickListener closer;
   // private Form form;
    private TextField titleTF, whoTF, whatTF, accomplTF, howTF, changeTF;

    @HibernateSessionThreadLocalConstructor
    public CreateActionPlanLayout(Object apId, Object rootCardId, ClickListener closer)
    {
      this.apId = apId;
      this.rootCardId = rootCardId;
      this.closer = closer;
      
      if (apId == null) {
        newAp = true;
        ap = new ActionPlan();
        ap.setCreationDate(new Date()); // now
        ap.setCreatedInMove(Move.getCurrentMoveTL());
      }
      else
        ap = ActionPlan.getTL(apId);

      game = Game.getTL();
      invitees = new TreeSet<User>(new User.AlphabeticalComparator());
    }

    @SuppressWarnings("serial")
    @Override
    public void initGui()
    {
      setSpacing(true);
      setMargin(true);
      
      VerticalLayout vLay = new MVerticalLayout().withMargin(new MarginInfo(false,true,true,true));
      vLay.addStyleName("m-greyborder");
      addComponent(vLay);

      if (!newAp) {
        ap = ActionPlan.getTL(apId);
      }
      else
        fillDefaults(ap); // won't cut it, never used

      FormLayout formLay = new MFormLayout().withFullWidth().withCaption(null);
      formLay.addComponent(titleTF = new MTextField("Title (required)").withFullWidth());
      formLay.addComponent(whoTF = new MTextField("Who is involved in this activity?").withFullWidth());
      formLay.addComponent(whatTF = new MTextField("What is the plan about?").withFullWidth());
      formLay.addComponent(accomplTF = new MTextField("What will it take to accomplish this?").withFullWidth());
      formLay.addComponent(howTF = new MTextField("How will it work?").withFullWidth());
      formLay.addComponent(changeTF = new MTextField("How will it change the situation?").withFullWidth());
      vLay.addComponent(formLay);
     
      GridLayout gLay = new GridLayout();
      gLay.addStyleName("m-greyborder");
      gLay.setColumns(3);
      gLay.setRows(6);
      gLay.setSpacing(true);
      gLay.setMargin(true);
      gLay.setWidth("820px");
      gLay.setColumnExpandRatio(2,1.0f);
      vLay.addComponent(gLay);
      vLay.setExpandRatio(gLay, 1.0f);

      Label lab;

      gLay.addComponent(lab = new Label("Card chain root id (required)"),0,0);
      lab.setWidth("150px"); // column definer
      gLay.setComponentAlignment(lab, Alignment.TOP_RIGHT);

      gLay.addComponent(cardIdTF = new TextField(),1,0);
      cardIdTF.setWidth("160px");
      cardIdTF.setImmediate(true);

      Button viewChainButt = new Button("View card chain",new ViewChainListener());
      gLay.addComponent(viewChainButt,2,0);
            
      cardText = new HtmlLabel("&nbsp;");
      gLay.addComponent(cardText,1,1,2,1);
      cardText.setWidth("100%");
      cardText.addStyleName("m-greyborder");

      /*
       * HorizontalLayout chainHL = new HorizontalLayout(); chainHL.setSpacing(true); gLay.addComponent(chainHL);
       * 
       * chainHL.addComponent(chainButt = new Button("Choose card chain")); chainButt.addListener(new CardChainChooser()); chainHL.addComponent(lab = new
       * Label("or by ID")); chainHL.setComponentAlignment(lab, Alignment.MIDDLE_LEFT); TextField cardID; chainHL.addComponent(cardID = new TextField());
       * cardID.setImmediate(true); cardID.setWidth("115px"); cardID.setDescription("Enter a card ID and press enter"); cardID.addListener(new
       * CardIDListener());
       */

      // gLay.addComponent(includeAuthorsCheck = new CheckBox("Include chosen card chain players as authors"));
      // includeAuthorsCheck.setImmediate(true);
      // includeAuthorsCheck.addListener(new ClickListener()
      // {
      // @Override
      // public void buttonClick(ClickEvent event)
      // {
      // Set<User> set = buildCardChainAuthorList();
      // if(set==null)
      // return;
      //
      // if(includeAuthorsCheck.booleanValue()) {
      // // checked
      // for(User u : set)
      // handleAddUser(u);
      // }
      // else {
      // // unchecked
      // for(User u : set)
      // handleDeleteUser(u);
      // }
      // }
      // });

      gLay.addComponent(lab = new Label("Invited authors (required)"),0,2);
      lab.setWidth(null);
      gLay.setComponentAlignment(lab, Alignment.TOP_RIGHT);
      gLay.addComponent(inviteeLis = new UserList(null, null),1,2,1,4);
      inviteeLis.setImmediate(true);
      inviteeLis.setWidth("100%");
      inviteeLis.setHeight("100%"); //inviteeLis.setRows(5);
      inviteeLis.setNullSelectionAllowed(false);
      
      gLay.addComponent(includeAuthorsButt = new Button("Include card chain players as authors"),2,2);
      includeAuthorsButt.addClickListener(new ClickListener()
      {
        @Override
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void buttonClick(ClickEvent event)
        {
          HSess.init();
          // still transient ap = ActionPlan.mergeTL(ap);
          Set<User> set = buildCardChainAuthorListTL();
          if (set == null)
            return;

          for (User u : set)
            handleAddUser(u);
          HSess.close();
        }
      });


      gLay.addComponent(inviteButt = new Button("Add to invitation list"),2,3);

      gLay.addComponent(lab = new Label("(Be sure to invite yourself if appropriate)"),2,4);
      inviteButt.addClickListener(new AddInviteesListener());
      
      gLay.addComponent(clearListButt = new Button("Clear invitation list"),1,5);
      clearListButt.addClickListener(new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
          // includeAuthorsCheck.setValue(false);
          handleClearUsers();
        }
      });

      HorizontalLayout buttons = new HorizontalLayout();
      buttons.setSpacing(true);
      Button discardChanges = new Button("Cancel and close"); //"Discard changes");
      discardChanges.addClickListener(new Button.ClickListener()
      {
        public void buttonClick(ClickEvent event)
        {
          if(closer != null)
            closer.buttonClick(event);
        }
      });
      //discardChanges.setStyleName(BaseTheme.BUTTON_LINK);
      buttons.addComponent(discardChanges);
      buttons.setComponentAlignment(discardChanges, Alignment.MIDDLE_LEFT);

      Button apply = new Button("Save and go to Action Dashboard");
      apply.addClickListener(new Button.ClickListener()
      {
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void buttonClick(ClickEvent event)
        {
          HSess.init();
          ap = ActionPlan.mergeTL(ap);
          try {
            if (checkInvitees() && checkChainRoot()) {
              unloadTextFields();//form.commit();
              ChatLog.saveTL(ap.getChatLog());
              
              notifyInviteesTL();   //@HibernateUserUpdate  // put invitees on list, but doesn't update (which was a bug)
              
              if (newAp) {
                GoogleMap gm = new GoogleMap(); // put a default map in place
                Game game = Game.getTL();
                gm.setLatCenter(game.getDefaultActionPlanMapLat());
                gm.setLonCenter(game.getDefaultActionPlanMapLon());
                GoogleMap.saveTL(gm);
                ap.setMap(gm);
                
                // This little squirrly bit puts the appropriate fields into the history lists
                ap.setTitleWithHistoryTL(ap.getTitle());
                ap.setSubTitleWithHistoryTL(ap.getSubTitle());
                ap.setHowWillItChangeTextWithHistoryTL(ap.getHowWillItChangeText());
                ap.setHowWillItWorkTextWithHistoryTL(ap.getHowWillItWorkText());
                ap.setWhatIsItTextWithHistoryTL(ap.getWhatIsItText());
                ap.setWhatWillItTakeTextWithHistoryTL(ap.getWhatWillItTakeText());
                
                ActionPlan.saveTL(ap); // saveorupdate does not get broadcast, save and update do
                GameEventLogger.logActionPlanCreationTL(ap);
              }
              else
                ActionPlan.updateTL(ap);
                       
              Mmowgli2UI.getGlobals().getController().miscEventTL(new AppEvent(MmowgliEvent.TAKEACTIONCLICK, CreateActionPlanLayout.this, null));
            }
            else {
              HSess.close();
              return;  // leave window open
            }
          }
          catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            // Ignored, we'll let the Form handle the errors
          }
          HSess.close();
          
          if(closer != null)
            closer.buttonClick(event);
        }
      });
      buttons.addComponent(apply);

      buttons.addComponent(lab = new Label());
      lab.setWidth("20px");

      addComponent(buttons);
      setComponentAlignment(buttons, Alignment.TOP_RIGHT);

      if (rootCardId != null) {
        Card c = Card.getTL(rootCardId);
        cardIdTF.setValue("" + c.getId());
        cardText.setValue(c.getText());
        titleTF.setValue(c.getText());
        ap.setChainRoot(c);
        checkPreviousActionPlanTL(c);
      }
      cardIdTF.addValueChangeListener(new CardIdChangedListener());
    }
    
    private void unloadTextFields()
    {
      ap.setTitle              (titleTF.getValue());
      ap.setSubTitle           (whoTF.getValue());
      ap.setWhatIsItText       (whatTF.getValue());
      ap.setWhatWillItTakeText (accomplTF.getValue());
      ap.setHowWillItWorkText  (howTF.getValue());
      ap.setHowWillItChangeText(changeTF.getValue());
    }
    
    private void checkPreviousActionPlanTL(Card root)
    {
      Criteria criteria = HSess.get().createCriteria(ActionPlan.class);
      criteria.add(Restrictions.eq("chainRoot", root));
      
      @SuppressWarnings("unchecked")
      List<ActionPlan> lis = criteria.list();
      if(lis != null && lis.size()>0) {
        ActionPlan ap = lis.get(0);
        Notification not = new Notification("The chosen card is already the root of Action Plan \""+ap.getTitle()+"\", id = "+ap.getId(), Notification.Type.WARNING_MESSAGE);
        not.setDelayMsec(-1); // must be clicked
        not.show(Page.getCurrent());
      }
    }
    @SuppressWarnings("serial")
    class CardIdChangedListener implements ValueChangeListener
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void valueChange(ValueChangeEvent event)
      {
        String idStr = (String) cardIdTF.getValue();
        if (idStr == null || idStr.length() <= 0) {
          ap.setChainRoot(null);
          rootCardId = null;
          return;
        }
        HSess.init();
        try {
          Long id = Long.parseLong(idStr);
          Card crd = Card.getTL(id);
          /*if (!crd.getCardType().isIdeaCard()) {
            cardIdTF.getWindow()
                .showNotification("Action Plan card chain roots must be \"Idea\" cards (Disrupt, Protect)", Notification.TYPE_HUMANIZED_MESSAGE);
            return;
          } */
          checkPreviousActionPlanTL(crd);
          rootCardId = id;
          cardText.setValue(crd.getText());
          
          //Put the card text as the title if the title is empty
          String titleStr = titleTF.getValue().toString();
          if(titleStr == null || titleStr.length()<=0)
            titleTF.setValue(crd.getText());
          ap.setChainRoot(crd);
        }
        catch (Throwable t) {
          Notification.show("Invalid card id", Notification.Type.WARNING_MESSAGE);
        }
        HSess.close();
      }
    }

    // Check for creating a plan without specifying a chain root
    private boolean checkChainRoot()
    {
      if (ap == null || ap.getChainRoot() == null) {
        Notification notif = new Notification("Important!", "You must choose a card chain on which to base the Action Plan.",Notification.Type.WARNING_MESSAGE);
        notif.setPosition(Position.MIDDLE_CENTER);
        notif.setDelayMsec(1500);// Let it stay there until the user clicks it
        notif.show(Page.getCurrent());// Show it in the main window.

        return false;
      }
      return true;
    }

    private Set<User> buildCardChainAuthorListTL()
    {
      if (ap == null || ap.getChainRoot() == null)
        return null;

      HashSet<User> set = new HashSet<User>();
      addAuthorsTL(ap.getChainRoot(), set);
      return set;
    }

    private void addAuthorsTL(Card c, HashSet<User> set)
    {
      c = Card.mergeTL(c);
      set.add(c.getAuthor());
      for (Card ch : c.getFollowOns())
        addAuthorsTL(ch, set); // recurse
    }

    // Called when someone has created an action plan without inviting anybody
    private boolean checkInvitees()
    {
      if (newAp && invitees.size() <= 0) {
        Notification notif = new Notification("Important!", "One or more users must be invited to author an Action Plan.",Notification.Type.WARNING_MESSAGE);
        notif.setPosition(Position.MIDDLE_CENTER);
        notif.setDelayMsec(1500);// Let it stay there until the user clicks it
        notif.show(Page.getCurrent());// Show it in the main window.

        return false;
      }
      else {
        return true; // go ahead and get out of here
      }
    }

    private void notifyInviteesTL()
    {
      for (User u : invitees) {
        u = User.mergeTL(u);
        notifyApInviteeTL(u, ap);   //@HibernateUserUpdate
      }
      // done by caller    ActionPlan.update(ap);
    }
    
    @HibernateUserUpdate
    @HibernateUpdate
    public static void notifyApInviteeTL(User u, ActionPlan ap)
    {
      Set<ActionPlan> set = u.getActionPlansInvited();
      if (set == null)
        u.setActionPlansInvited(set = new HashSet<ActionPlan>(1));
      
      if(!apContainsByIds(set,ap))
        set.add(ap);
      
      if(!usrContainsByIds(ap.getInvitees(),u)) {
        ap.addInvitee(u); //ap.getInvitees().add(u);
        // dont: ActionPlan.update(ap); // ap may be new, not saved
      }
      User.updateTL(u);

      AppMaster.instance().getMailManager().actionPlanInviteTL(ap, u);
    }
    
    @SuppressWarnings("serial")
    class ViewChainListener implements ClickListener
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      { 
        HSess.init();
        final CardChainTreeTablePopup chainpopup = new CardChainTreeTablePopup(rootCardId, true, true);   //model, show save
        chainpopup.center();
        // use window where source component exists
        UI.getCurrent().addWindow(chainpopup);
        
        chainpopup.addCloseListener(new CloseListener()
        {
          @Override
          public void windowClose(CloseEvent e)
          {
            if(chainpopup.saveClicked) {
              Object newSelected = chainpopup.getSelectedCardId();
              if(newSelected != null && !newSelected.equals(rootCardId)) {
                rootCardId = newSelected;
                cardIdTF.setValue("" + rootCardId);
              }
            }            
          }         
        });
        HSess.close();
      }     
    }
    
    @SuppressWarnings("serial")
    class AddInviteesListener implements ClickListener
    {
      AddAuthorDialog dial;

      @SuppressWarnings("unchecked")
      @Override
      public void buttonClick(ClickEvent event)
      {
        dial = new AddAuthorDialog((Collection<User>) inviteeLis.getItemIds());
        dial.addListener(new CloseListener()
        {
          @Override
          @MmowgliCodeEntry
          @HibernateOpened
          @HibernateClosed
          public void windowClose(CloseEvent e)
          {
            HSess.init();
            if (dial.addClicked) {
              Object o = dial.getSelected();

              // no, inviteeLis.removeAllItems();

              if (o instanceof Set<?>)
                handleMultipleUsersTL((Set<?>) o);
              else
                handleSingleUserTL(o);
            }
            HSess.close();
          }
        });
        UI.getCurrent().addWindow(dial);
        dial.center();
      }

      @SuppressWarnings("unchecked")
      @HibernateUserRead
      private void handleMultipleUsersTL(Set<?> set)
      {
        if (set.size() > 0) {
          Object o = set.iterator().next();
          if (o instanceof User) {
            Iterator<User> itr = (Iterator<User>) set.iterator();
            while (itr.hasNext()) {
              handleAddUser(itr.next());
            }
          }
          else if (o instanceof QuickUser) {
            Iterator<QuickUser> itr = (Iterator<QuickUser>) set.iterator();
            while (itr.hasNext()) {
              QuickUser qu = itr.next();
              handleAddUser(User.getTL(qu.id));
            }
          }
        }
      }

     @HibernateUserRead
     private void handleSingleUserTL(Object o)
      {
        if (o instanceof User) {
          handleAddUser((User) o);
        }
        else if (o instanceof QuickUser) {
          QuickUser qu = (QuickUser) o;
          handleAddUser(User.getTL(qu.id)); //feb refactor DBGet.getUserTL(qu.id));
        }
      }
    }

    @SuppressWarnings("unchecked")
    private void handleAddUser(User u)
    {
      long uid = u.getId();
      big: {
        for(User tus : invitees){
          if(tus.getId()==uid)
            break big;
        }
        invitees.add(u);
      }
      // We've got to compare id's, not objects
      Collection<User> coll = (Collection<User>) inviteeLis.getItemIds();
      for (User usr : coll)
        if (usr.getId() == uid)
          return; // already there
      inviteeLis.addItem(u);
    }

    private void handleClearUsers()
    {
      invitees.clear();
      inviteeLis.removeAllItems();
    }
/*
    @SuppressWarnings("unchecked")
    private void handleDeleteUser(User u)
    {
      invitees.remove(u);
      // We've got to compare id's, not objects
      Collection<User> coll = (Collection<User>) inviteeLis.getItemIds();
      for (User usr : coll)
        if (usr.getId() == u.getId()) {
          inviteeLis.removeItem(usr); // got him
          return;
        }
    }
*/
  
    private void fillDefaults(ActionPlan ap)
    {
      ap.setPlanInstructions(game.getDefaultActionPlanThePlanText());
      ap.setTalkItOverInstructions(game.getDefaultActionPlanTalkText());
      ap.setImagesInstructions(game.getDefaultActionPlanImagesText());
      ap.setVideosInstructions(game.getDefaultActionPlanVideosText());
      ap.setMapInstructions(game.getDefaultActionPlanMapText());
      ap.setSubTitle("");

      ap.setTitle("");
      ap.setWhatIsItText("");
      ap.setWhatWillItTakeText("");
      ap.setHowWillItWorkText("");
      ap.setHowWillItChangeText("");
    }
  }
  
  public static boolean apContainsByIds(Set<ActionPlan> set, ActionPlan ap)
  {
    for(ActionPlan actPln : set)
      if(actPln.getId() == ap.getId())
        return true;
    return false;
  }
  
  public static boolean usrContainsByIds(Set<User> set, User u)
  {
    for(User usr : set)
      if(usr.getId() == u.getId())
        return true;
    return false;
  }

}
