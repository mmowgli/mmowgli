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

import static edu.nps.moves.mmowgli.MmowgliConstants.APPLICATION_SCREEN_WIDTH;
import static edu.nps.moves.mmowgli.cache.MCacheUserHelper.QuickUser.*;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.cache.MCacheUserHelper.QuickUser;
import edu.nps.moves.mmowgli.components.*;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.WantsUserUpdates;
import edu.nps.moves.mmowgli.utility.BeanContainerWithCaseInsensitiveSorter;
import edu.nps.moves.mmowgli.utility.MailManager;

/**
 * UserAdminPanel.java Created on May 6, 2011
 * Updated 11 Apr, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserAdminPanel extends VerticalLayout implements MmowgliComponent, WantsUserUpdates, View
{
  private static final long serialVersionUID = 9143783913338522647L;
  public static int BACKGND_SEARCHER_RESULTSET_SIZE = 50;
  public static long BACKGND_SEARCH_SLEEP_MS = 250; // quar sec

  public static String SEARCHING_STRING = " (searching)";
  private Table table;
  private AbstractOrderedLayout tableContainer;
  private TableFiller lastTableFiller = null;

  // This table does not use the HbnContainer and db access, it uses the quickuser cache
  private String USER_ID_COL = QUICKUSER_ID;
  private String ADMIN_COL = QUICKUSER_ADMIN;
  private String DESIGNER_COL = QUICKUSER_DESIGNER;
  private String LOCKEDOUT_COL = QUICKUSER_LOCKEDOUT;
  private String GAMEMASTER_COL = QUICKUSER_GM;
  private String TWEETER_COL = QUICKUSER_TWEETER;
  private String UNAME_COL = QUICKUSER_UNAME;
  private String FIRSTNAME_COL = QUICKUSER_REALFIRSTNAME;
  private String LASTNAME_COL = QUICKUSER_REALLASTNAME;
  private String EMAIL_COL = QUICKUSER_EMAIL;
  private String CONFIRMED_COL = QUICKUSER_CONFIRMED;
  
  public static final String WARNING_LABEL = "<b>WARNING</b>: Do <u>not</u> communicate player Personal Identifying Information (PII) to anyone other than another game administrator or the individual player.";
  
  private String[] visibleColumns = {
    USER_ID_COL,
    LOCKEDOUT_COL,
    ADMIN_COL,
    DESIGNER_COL,
    GAMEMASTER_COL,
    TWEETER_COL,
    UNAME_COL,
    FIRSTNAME_COL,
    LASTNAME_COL,
    EMAIL_COL,
    CONFIRMED_COL
  };

  private static String[] columnNames = {
    "id", 
    "locked", //"<html>locked<br/>out",//"<html><span class='m-admintable-2line-header'>locked<br/>out</span>",
    "GA",
    "GD",
    "GM",
    "tweeter", 
    "game name", 
    "real first name", 
    "real last name",  
    "email",
    "confirmed",
  };

  boolean isAttached = false;
  
  @Override
  public void attach()
  {
    super.attach();
    isAttached=true;
  }

  @Override
  public void detach()
  {
    super.detach();
    isAttached=false;
  }

  @HibernateSessionThreadLocalConstructor
  public UserAdminPanel()
  {
  }

  @Override
  public void initGui()
  {
  }
  public void initGuiTL()
  {
    setWidth(APPLICATION_SCREEN_WIDTH);
    setHeight("100%");
    setSpacing(false);

    HorizontalLayout titleHL = new HorizontalLayout();
    addComponent(titleHL);
    Label lab;
    titleHL.addComponent(lab = new Label());
    lab.setWidth("20px");

    VerticalLayout tableVLayout = new VerticalLayout();
    tableContainer = tableVLayout;
    tableVLayout.setWidth(APPLICATION_SCREEN_WIDTH);
    tableVLayout.setHeight("900px");
    tableVLayout.setSpacing(true);
    tableVLayout.addStyleName("m-whitepage-header");

    addComponent(tableVLayout);

    tableVLayout.addComponent(lab = new Label());
    lab.setHeight("20px");
    tableVLayout.addComponent(lab=new HtmlLabel("<span style='margin-left:40px;color:red;'>" + WARNING_LABEL + "</span>"));
    lab.addStyleName("m-text-align-center");
    tableVLayout.addComponent(new HtmlLabel("<span style='margin-left:40px;'>" + getNumberUsersLabelTL() + "</span>"));
    tableVLayout.addComponent(new HtmlLabel("<span style='margin-left:40px;'>" + getNumberOnlineLabel() + "</span>"));
    tableVLayout.addComponent(new HtmlLabel("<span style='margin-left:40px;'>" + getNumberGameMastersTL() + "</span>"));
    tableVLayout.addComponent(new HtmlLabel("<span style='margin-left:40px;'>" + getNumberCardsLabelTL() + "</span>"));
    tableVLayout.addComponent(new HtmlLabel("<span style='margin-left:40px;'>Double click a row to edit</span>"));

    tableVLayout.addComponent(lab = new HtmlLabel("<center><b><span style='font-size:175%'>Player Administration</span></b></center>"));
    
    HorizontalLayout srchHL = buildSearchRow();
    tableVLayout.addComponent(srchHL);
    tableVLayout.setComponentAlignment(srchHL, Alignment.MIDDLE_CENTER);

    table = createTable(lastTableFiller = new SimpleTableFiller());

    table.setCaption(null);
    addTableToLayout(Mmowgli2UI.getGlobals().getUserID());
  }

  private HorizontalLayout buildSearchRow()
  {
    return new SearchHandler();
  }

  class SearchHandler extends HorizontalLayout implements ValueChangeListener
  {
    private static final long serialVersionUID = 1L;

    private HashMap<String, String> fieldHM = new HashMap<String, String>();
    private TextField srchTF = new TextField();
    private ComboBox fldCombo = new ComboBox();

    SearchHandler()
    {
      HorizontalLayout srchHL = this;
      srchHL.setSpacing(true);
      Label lab;
      srchHL.addComponent(lab = new Label("Search"));
      srchHL.setComponentAlignment(lab, Alignment.MIDDLE_LEFT);

      String[] srchFldsTitles = { "Game name", "ID number", "First name", "Last name", "Email"};
      String[] srchFlds = { UNAME_COL, USER_ID_COL, FIRSTNAME_COL, LASTNAME_COL, EMAIL_COL};

      fldCombo.addStyleName("m-useradmin-search-combo");
      fldCombo.setImmediate(true);
      fldCombo.setNewItemsAllowed(false);
      fldCombo.setNullSelectionAllowed(false);
      for (int i = 0; i < srchFlds.length; i++) {
        fieldHM.put(srchFldsTitles[i], srchFlds[i]);
         fldCombo.addItem(srchFldsTitles[i]);
      }
      fldCombo.setValue(srchFldsTitles[0]);
      srchHL.addComponent(fldCombo);

      srchTF.setColumns(20);
      srchTF.setImmediate(true);
      srchHL.addComponent(srchTF);
      srchTF.addValueChangeListener(this);
      fldCombo.addValueChangeListener(this);
    }
    
    private boolean listenerEnabled = true;
    @Override
    public void valueChange(ValueChangeEvent event)
    {
      if(!listenerEnabled)
        return;
      Serializable uid = Mmowgli2UI.getGlobals().getUserID();
      String dbField = fieldHM.get(fldCombo.getValue());
      String srchStr = srchTF.getValue().toString().trim();
      if (srchStr.length() <= 0) { // null search
        tableContainer.removeComponent(table);
        table = createTable(lastTableFiller=new SimpleTableFiller());
        table.setCaption("Player Adminstration");
        addTableToLayout(uid);
        return;
      }

      tableContainer.removeComponent(table);
      table = createTable(lastTableFiller=new DefaultTableFiller(dbField,srchStr));
      table.setCaption("Player Adminstration");
      addTableToLayout(uid);
    } 
  }

  private String getNumberUsersLabelTL()
  {
    Criteria criteria = HSess.get().createCriteria(User.class);
    criteria.setProjection(Projections.rowCount());
    int count = ((Long) criteria.list().get(0)).intValue();
    return "Number of registered players: " + count;
  }
  
  private String getNumberCardsLabelTL()
  {
    Criteria criteria = HSess.get().createCriteria(Card.class);
    criteria.setProjection(Projections.rowCount());
    int count = ((Long)criteria.list().get(0)).intValue();
    return "Number cards played: "+count;
  }
  
  private String getNumberOnlineLabel()
  {
    int count = Mmowgli2UI.getGlobals().getSessionCount();
    return "Number online: " + count;
  }
  
  private String getNumberGameMastersTL()
  {
    Criteria criteria = HSess.get().createCriteria(User.class);
    criteria.add(Restrictions.eq("gameMaster", true));
    criteria.setProjection(Projections.rowCount());
    int count = ((Long) criteria.list().get(0)).intValue();
    return "Number of registered game masters: " + count;    
  }
  
  private void addTableToLayout(Object selectedUserId)
  {
    tableContainer.addComponent(table);
    tableContainer.setComponentAlignment(table, Alignment.TOP_CENTER);
    table.setValue(selectedUserId);
    tableContainer.setExpandRatio(table, 1.0f);
  }
  
  @SuppressWarnings("serial")
  Table createTable(TableFiller filler)
  {
    final Table tab = new Table();
    tab.setStyleName("m-useradmintable");
    tab.setWidth("920px");
    tab.setHeight("100%");
    tab.setPageLength(40);
    
    // Special column renderers
    Table.ColumnGenerator colGen = new columnCustomizer();
    tab.addGeneratedColumn(USER_ID_COL, colGen);
    tab.addGeneratedColumn(ADMIN_COL, colGen);
    tab.addGeneratedColumn(DESIGNER_COL, colGen);
    tab.addGeneratedColumn(LOCKEDOUT_COL,colGen);
    tab.addGeneratedColumn(TWEETER_COL, colGen);
    tab.addGeneratedColumn(GAMEMASTER_COL,colGen);
    tab.addGeneratedColumn(EMAIL_COL,colGen);
    tab.addGeneratedColumn(CONFIRMED_COL,colGen);
    
    filler.fillTable(tab);

    tab.setColumnWidth(USER_ID_COL, 25);
    tab.setColumnWidth(ADMIN_COL, 25);
    tab.setColumnWidth(GAMEMASTER_COL, 25);
    tab.setColumnWidth(DESIGNER_COL, 25);
    tab.setColumnWidth(LOCKEDOUT_COL, 43);
    tab.setColumnWidth(TWEETER_COL, 50);
    tab.setColumnWidth(UNAME_COL, 120);
    tab.setColumnWidth(FIRSTNAME_COL, 108); //128);
    tab.setColumnWidth(LASTNAME_COL, 108); //128);
    tab.setColumnWidth(EMAIL_COL, 190);
    tab.setColumnWidth(CONFIRMED_COL, 67);
    
    tab.setEditable(false);
    tab.setSelectable(true);
    tab.setImmediate(true); // to immed update view
    tab.setNullSelectionAllowed(false); // can't deselect a row

    tab.addItemClickListener(new ItemClickListener()
    {
      EditPanel ep;

      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      @HibernateUserRead
      public void itemClick(ItemClickEvent event)
      {
        if (event.isDoubleClick()) {
          HSess.init();
          Window w = new Window("Edit Player Account");
          w.setWidth("620px");
          w.setHeight("505px");
          w.setModal(true);
          
          @SuppressWarnings({ "unchecked" })
          final QuickUser qu = (QuickUser)((BeanItem<QuickUser>)event.getItem()).getBean();
          User u = User.getTL(qu.getId());
          if(u == null) {
            // This has been happening infrequently...some error on signup where (maybe) a user object gets created
            // but doesn't make it into the db.
            Notification.show(
               "Woops, database error!",
               "A player account identified by id = "+qu.getId()+" is not in the database.",
               Notification.Type.ERROR_MESSAGE);
            HSess.close();
            return;
          }
          VerticalLayout vl = new VerticalLayout();
          w.setContent(vl);
          vl.addComponent(ep = new EditPanel(w, qu.getId()));
          ep.setWidth("100%");
          UI.getCurrent().addWindow(w);
          w.center();

          w.addCloseListener(new CloseListener()
          {
            @Override
            public void windowClose(CloseEvent e)
            {
              if (ep.user != null) {
                BeanItem<QuickUser> bi = lastTableFiller.getContainer().getItem(ep.user.getId());
                QuickUser qu = bi.getBean();
                qu.update(ep.user);
                tab.refreshRowCache();
              }
            }
          });
          HSess.close();
        }
      }
    });
    return tab;
  }

  class columnCustomizer implements Table.ColumnGenerator
  {
    private static final long serialVersionUID = 1938821794468835620L;
    StringBuilder sb = new StringBuilder();

    @Override
    public Component generateCell(Table source, Object itemId, Object columnId)
    {
      @SuppressWarnings("unchecked")
      BeanItem<QuickUser>bi = (BeanItem<QuickUser>)table.getItem(itemId);
      QuickUser qu = bi.getBean();
      if (USER_ID_COL.equals(columnId)) {
        Label lab = new Label(""+qu.getId());
       /* if(qu.isAdmin()) {
          lab.addStyleName("m-whitetext");
          lab.addStyleName("m-background-olivedrab");
          lab.setDescription("game administrator");
        }*/
        return lab;
      }
      else if(EMAIL_COL.equals(columnId)) {
        Label lab = new Label(""+qu.getEmail());
        if(qu.isMultipleEmails()) {
          lab.addStyleName("m-background-red");
          lab.addStyleName("m-white-text");
          lab.setDescription("player has used multiple emails");
        }
        return lab;
      }
      else if(GAMEMASTER_COL.equals(columnId)) {
        boolean gm = qu.isGm();
        Label lab = new Label(gm?"true":"");
        if(gm) {
          lab.addStyleName("m-white-text");
          lab.addStyleName("m-background-dodgerblue");
          lab.setDescription("game master");
        }
        return lab;
      }
      else if(LOCKEDOUT_COL.equals(columnId)) {
        boolean lo = qu.isLockedOut();
        Label lab = new Label(lo?"true":"");
        if(lo) {
          lab.addStyleName("m-background-red");
          lab.addStyleName("m-white-text");
          lab.setDescription("locked out of game play");
        }
        return lab;
      }
      else if(TWEETER_COL.equals(columnId)) {
        boolean tw = qu.isTweeter();
        Label lab = new Label(tw?"true":"");
        if(tw) {
          lab.addStyleName("m-background-canary-yellow");
        }
        return lab;
      }
      else if(ADMIN_COL.equals(columnId)) {
        boolean ad = qu.isAdmin();
        Label lab = new Label(ad?"true":"");
        if(ad) {
          lab.addStyleName("m-background-orange");
          lab.addStyleName("m-white-text");
          lab.setDescription("game administrator");
        }
        return lab;
      }
      else if(DESIGNER_COL.equals(columnId)) {
        boolean ds = qu.isDesigner();
        Label lab = new Label(ds?"true":"");
        if(ds) {
          lab.addStyleName("m-background-greenyellow");
          lab.setDescription("game designer");
        }
        return lab;
      }
      else if(CONFIRMED_COL.equals(columnId)) {
        boolean cn = qu.isConfirmed();
        Label lab = new Label(cn?"true":"");
        return lab;
      }
     
      return new Label("Program error in UserAdminPanel");
    }
  }
  
  @SuppressWarnings("serial")
  class EditPanel extends VerticalLayout
  {
    private static final String COMMON_FIELD_WIDTH = "20em"; //"23em";
    User user = null;
    Object uid;
    TextField uNameTf, firstTf, lastTf, emailTf, lockoutTf;
    CheckBox gameMasterCb, lockedOutCb, tweeterCb, gameAdminCb, gameDesignerCb, confirmedCb;
    PasswordField pwFld;
    Window win;

    @HibernateSessionThreadLocalConstructor
    @HibernateUserRead
    public EditPanel(Window w, Object uid)
    {
      this.uid = uid;
      this.win = w;
      setSpacing(true);
      setMargin(true);

      FormLayout formLay = new FormLayout();
      formLay.addStyleName("m-greyborder");
      formLay.addStyleName("m-useradmin-edit-form");
      formLay.setMargin(true);
      addComponent(formLay);
      setComponentAlignment(formLay,Alignment.TOP_CENTER);
      formLay.setWidth("98%");
      User u = User.getTL(uid); //feb refactor DBGet.getUserFreshTL(uid);
      TextField f = new TextField("id");
      f.setValue(""+u.getId());
      f.setReadOnly(true);
      f.setWidth(COMMON_FIELD_WIDTH);
      formLay.addComponent(f);

      HorizontalLayout hl;
       // A current admin can allow others to be admins
      User me = Mmowgli2UI.getGlobals().getUserTL();
      if(me.isAdministrator()) {
        hl = new HorizontalLayout();
        formLay.addComponent(hl);       
        hl.setCaption("game administrator");
        gameAdminCb = new CheckBox("");
        hl.addComponent(gameAdminCb);
        gameAdminCb.setValue(u.isAdministrator());       
      }
      hl = new HorizontalLayout();
      formLay.addComponent(hl);
      hl.setCaption("game master");
      gameMasterCb = new CheckBox("");
      hl.addComponent(gameMasterCb);
      gameMasterCb.setValue(u.isGameMaster());

      if(me.isAdministrator()) {
        hl = new HorizontalLayout();
        formLay.addComponent(hl);
        hl.setCaption("game designer");
        gameDesignerCb = new CheckBox("");
        hl.addComponent(gameDesignerCb);
        gameDesignerCb.setValue(u.isDesigner());
      }
      
      hl = new HorizontalLayout();
      formLay.addComponent(hl);
      hl.setCaption("Tweeter");
      tweeterCb = new CheckBox("");
      hl.addComponent(tweeterCb);
      tweeterCb.setValue(u.isTweeter());

      hl = new HorizontalLayout();
      hl.setSpacing(true);
      hl.setCaption("game name");
        uNameTf = new TextField();
        uNameTf.setValue(u.getUserName());
        uNameTf.setWidth(COMMON_FIELD_WIDTH);
      hl.addComponent(uNameTf);
      Button b;
      hl.addComponent(b=new Button("send in-game mail", new InGameMailerListener()));
      if(!u.isOkGameMessages()) {
        b.setEnabled(false);
        b.setDescription("User does not receive in-game messages");
      }
      formLay.addComponent(hl);

      hl = new HorizontalLayout();
      hl.setSpacing(true);
      hl.setCaption("email");
        emailTf = new TextField();
        List<String> emailLis = VHibPii.getUserPiiEmails(u.getId());
        if(emailLis != null && emailLis.size() > 0) {
          emailTf.setValue(emailLis.get(0));
          if(emailLis.size()>1) {
            String desc = concatAllButFirst(emailLis);
            emailTf.setDescription(desc);
            hl.setCaption("email *");
            hl.setDescription(desc);
          }
        }
        emailTf.setWidth(COMMON_FIELD_WIDTH);
        
      hl.addComponent(emailTf);
      hl.addComponent(b=new Button("send external email", new EmailListener()));
      if(!u.isOkEmail()) {
        b.setEnabled(false);
        b.setDescription("User does not receive email");
      }
      formLay.addComponent(hl);
      
      hl = new HorizontalLayout();
      hl.setSpacing(true);
      hl.setCaption("real first name");
      
      UserPii upii = VHibPii.getUserPii(u.getId());
      firstTf = new TextField(); //"real first name");
      firstTf.setValue(upii.getRealFirstName()); //u.getRealFirstName());
      firstTf.setWidth(COMMON_FIELD_WIDTH);
      hl.addComponent(firstTf);
      hl.addComponent(b = new Button("resend confirmation email", new ResendListener()));
      formLay.addComponent(hl);
           
      lastTf = new TextField("real last name");
      lastTf.setValue(upii.getRealLastName()); //u.getRealLastName());
      lastTf.setWidth(COMMON_FIELD_WIDTH);
      formLay.addComponent(lastTf);

      hl = new HorizontalLayout();
      hl.setSpacing(true);
      hl.setCaption("password");
        pwFld = new PasswordField(); //"password");
        pwFld.setWidth(COMMON_FIELD_WIDTH);
      hl.addComponent(pwFld);
      Label lab;
      hl.addComponent(lab=new Label("(Leave blank for no change)"));
      lab.addStyleName("m-font-size-smaller");
      formLay.addComponent(hl);;

      hl = new HorizontalLayout();
      hl.setWidth("96%");
      formLay.addComponent(hl);
      hl.setCaption("locked out");
      lockedOutCb = new CheckBox("");
      lockedOutCb.setImmediate(true);
      hl.addComponent(lockedOutCb);

      hl.addComponent(lockoutTf=new TextField());
      lockoutTf.setWidth("100%");
      hl.setExpandRatio(lockoutTf, 1.0f);
      lockoutTf.setInputPrompt("Enter reason for lock/unlock (optional)");
      lockoutTf.setEnabled(false);
      
      lockedOutCb.setValue(u.isAccountDisabled());
      lockedOutCb.addValueChangeListener(new LockOutListener());

      hl = new HorizontalLayout();
      formLay.addComponent(hl);
      hl.setCaption("Email confirmed");
      confirmedCb = new CheckBox("");
      hl.addComponent(confirmedCb);
      confirmedCb.setValue(u.isEmailConfirmed());
      
      // The cancel / apply buttons
      HorizontalLayout buttons = new HorizontalLayout();
      addComponent(buttons);
      setComponentAlignment(buttons, Alignment.TOP_RIGHT);
      buttons.setSpacing(true);

      Button discardChanges = new Button("Discard changes", new Button.ClickListener()
      {
        public void buttonClick(ClickEvent event)
        {
          user = null;
          closePopup(event);
        }
      });
      buttons.addComponent(discardChanges);
      buttons.setComponentAlignment(discardChanges, Alignment.MIDDLE_RIGHT);

      Button apply = new Button("Apply and close", new Button.ClickListener()
      {
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void buttonClick(ClickEvent event)
        {
          HSess.init();
          if(loadAndSaveUserTL()) { //  @HibernateUserUpdate
            closePopup(event);
          }
          HSess.close();
        }
      });
      buttons.addComponent(apply);
      
      Button gotoProfileButt = new Button("Apply and go to user profile", new Button.ClickListener()
      {
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void buttonClick(ClickEvent event)
        {
          HSess.init();
          
          if(loadAndSaveUserTL()) {   //  @HibernateUserUpdate       
            closePopup(event);
            HSess.close();  //Commit, else a get() in the call below will retrieve old data.
            HSess.init();
            Mmowgli2UI.getGlobals().getController().miscEventTL(new AppEvent(MmowgliEvent.SHOWUSERPROFILECLICK,event.getButton(),user.getId()));
          }             
          HSess.close();
        }
      });
      buttons.addComponent(gotoProfileButt);
      
      Label sp;
      buttons.addComponent(sp = new Label());
      sp.setWidth("5px");
    }
    
    private void closePopup(ClickEvent event)
    {
      win.close();
    }
    
    private String concatAllButFirst(List<String> lis)
    {
      if(lis.size()<2)
        return "";

      StringBuilder sb = new StringBuilder("previous emails: ");
      int sz = lis.size();
      for (int i=1;i<sz;i++) {
        sb.append(lis.get(i));
        sb.append(' ');
      }
      return sb.toString().trim();
    }
    
    class ResendListener implements ClickListener
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      @HibernateUserRead
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        User u = User.getTL(uid);
        List<String> lis = VHibPii.getUserPiiEmails(u.getId());
        String email = lis.get(0);
        String gameUrl = AppMaster.instance().getAppUrlString();
        
        boolean warn = false;
        if(confirmedCb.getValue() == false) {
          confirmedCb.setValue(true);
          warn = true;
        }
        AppMaster.instance().getMailManager().sendConfirmedReminderTL(email, u.getUserName(), gameUrl);
        if(warn)
          Notification.show("Email sent", "Be sure to \"apply\" before closing dialog.",Notification.Type.WARNING_MESSAGE);
        else
          Notification.show("Email sent",Notification.Type.WARNING_MESSAGE);
        HSess.close();
      }         
    }
    
    class EmailListener implements ClickListener
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      @HibernateUserRead
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        User u = User.getTL(uid);
        if(u.isOkEmail()) {
          new SendMessageWindow(u, false, MailManager.Channel.EXTERNALEMAIL,true);
        }
        HSess.close();
      }   
    }
    
    class InGameMailerListener implements ClickListener
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      @HibernateUserRead
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        User u = User.getTL(uid);
        if(u.isOkGameMessages()) {
          new SendMessageWindow(u, false, MailManager.Channel.INGAMEMESSAGE);
        }
        HSess.close();
      }      
    }
    
    class LockOutListener implements ValueChangeListener
    {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        lockoutTf.setEnabled(true);
        if (lockedOutCb.getValue()) {
          Notification.show("If you save this change, the indicated user will IMMEDIATELY be logged out and not permitted to play again.",Notification.Type.WARNING_MESSAGE);
        }
      }
    }

    private String tweekString(Object o)
    {
      String ret = null;
      if (o != null)
        ret = o.toString();
      return ret;
    }

    /**
     * @return true if the account is truely to be disabled
     */
    @HibernateUserRead
    private boolean handleDisabledTL()
    {
      boolean wantToDisable = lockedOutCb.getValue();
      User badGuy = User.getTL(uid);
      User me = Mmowgli2UI.getGlobals().getUserTL();
      String message = "";
      GameEvent.EventType lockEvent = null;
      Long lockParameter = null;

      if (!wantToDisable) { // letting back in
        if (badGuy.isAccountDisabled()) {
          message = "Player " + badGuy.getUserName() + " has been allowed back into mmowgli by " + me.getUserName() + ".";
          lockEvent = GameEvent.EventType.USERUNLOCKOUT;
          lockParameter = badGuy.getId();
        }
      }
      else {
        if (badGuy.isAccountDisabled()) {
          Notification.show("This guy's already been locked out.",Notification.Type.WARNING_MESSAGE);
          return wantToDisable;
        }
        String reason = null;
        if (badGuy.isGameMaster())
          reason = "a game master";
        if (badGuy.isAdministrator())
          reason = "a game administrator";
        if (badGuy.getId() == me.getId())
          reason = "yourself";
        if (reason != null)
          Notification.show("Warning:  You've locked out " + reason + "!",Notification.Type.WARNING_MESSAGE);
        message = "Player " + badGuy.getUserName() + " has been locked out of mmowgli by " + me.getUserName();
        lockEvent = GameEvent.EventType.USERLOCKOUT;
        lockParameter = badGuy.getId();
      }
      
      Object los = lockoutTf.getValue();
      if(los == null || los.toString().length() <=0)
        message = message + ".";
      else
        message = message + " : "+los.toString();
        
      if (lockParameter != null) {
        GameEvent.saveTL(new GameEvent(lockEvent, message, lockParameter));
        GameEvent.saveTL(new GameEvent(GameEvent.EventType.MESSAGEBROADCASTGM, message));
      }
      return wantToDisable;
    }

    private boolean errorOut(String s)
    {
      Notification.show(s,Notification.Type.ERROR_MESSAGE);
      return false;
    }
    
    @HibernateUserUpdate
    @HibernateActionPlanUpdate
    @HibernateCardUpdate
    @HibernateUpdate    
    @HibernateUserRead
    private boolean loadAndSaveUserTL()
    {
      String newUname = tweekString(uNameTf.getValue());
      if(newUname == null || newUname.length()<=0)
        return errorOut("User name must be entered");

      String newPw = tweekString(pwFld.getValue());
      
      String newEmail = tweekString(emailTf.getValue());
      if(newEmail == null || newEmail.length()<=0)
        return errorOut("Email address must be entered");
      
      user = User.getTL(uid);
      String oldUname = user.getUserName();
      
      user.setAccountDisabled(handleDisabledTL());
      user.setGameMaster(gameMasterCb.getValue());
      if(gameAdminCb != null)
        user.setAdministrator(gameAdminCb.getValue());
      if(gameDesignerCb != null)
        user.setDesigner(gameDesignerCb.getValue());
      user.setTweeter(tweeterCb.getValue());
      user.setEmailConfirmed(confirmedCb.getValue());
      user.setUserName(newUname);
      
      UserPii upii = VHibPii.getUserPii((Long)uid);
      upii.setRealFirstName(tweekString(firstTf.getValue()));
      upii.setRealLastName(tweekString(lastTf.getValue()));
      
      if(newPw != null && newPw.length()>0)
        upii.setPassword(new StrongPasswordEncryptor().encryptPassword(newPw));
      
      //todo Somehow some were sneaking in w/out emails

      VHibPii.newUserPiiEmail((Long)uid,newEmail);

      User.updateTL(user);
      VHibPii.update(upii);
      
      // if the user name has been changed, a few more things need to happen
      if(!newUname.equals(oldUname)) {
        @SuppressWarnings("unchecked")
        List<Card> cardList = (List<Card>)HSess.get().createCriteria(Card.class)
                              .add(Restrictions.eq("author", user)).list();
        for(Card c : cardList) {
          c.setAuthorName(newUname);  // It already has my name, but holding the author name in the card is an optimization
          Card.updateTL(c);
        }
        
        Set<ActionPlan> apAuthored = user.getActionPlansAuthored();
        for(ActionPlan ap : apAuthored) {
          ap.rebuildQuickAuthorList();  // also an optimization
          ActionPlan.updateTL(ap);
        }
        
        GameEventLogger.logUserNameChangedTL(user.getId(), Mmowgli2UI.getGlobals().getUserID(), oldUname, newUname);
      }
      return true;
    }
  }

  interface TableFiller
  {
    public void fillTable(Table table);
    public BeanContainer<Long,QuickUser> getContainer();
  }

  class SimpleTableFiller extends DefaultTableFiller
  {
    public SimpleTableFiller()
    {
      super(null,null);
    }
  }
  class DefaultTableFiller implements TableFiller
  {
    String[] termsArr;
    String dbField;
    BeanContainer<Long,QuickUser> container;
    
    public DefaultTableFiller(String dbField, String terms)
    {
      this.dbField = dbField;
      if(terms != null) 
        this.termsArr = terms.toLowerCase().split("\\s");
    }
    
    @Override
    public void fillTable(Table table)
    {
      if (container == null) {
        List<QuickUser> lis = AppMaster.instance().getMcache().getUsersQuickFullList();
        container = new BeanContainerWithCaseInsensitiveSorter<Long, QuickUser>(QuickUser.class);
        for (QuickUser qu : lis) {
          if (termsArr != null)
            if (!foundTerm(qu))
              continue;
          container.addItem(qu.id, qu);
        }
        container.sort(new Object[] { UNAME_COL }, new boolean[] { true });
        table.setContainerDataSource(container);

        table.setVisibleColumns((Object[]) visibleColumns);
        table.setColumnHeaders(columnNames);
      }
    }
    
    private boolean foundTerm(QuickUser qu)
    {
      Object value=null;
           if(dbField.equals(UNAME_COL))     value = qu.uname.toLowerCase();     
      else if(dbField.equals(USER_ID_COL))   value = qu.id;
      else if(dbField.equals(FIRSTNAME_COL)) value = qu.realFirstName.toLowerCase();
      else if(dbField.equals(LASTNAME_COL))  value = qu.realLastName.toLowerCase();
      else if(dbField.equals(EMAIL_COL))     value = qu.email.toLowerCase();
      
      if(value instanceof Long) {
        for(String s : termsArr) {
          try {
            Long lng = Long.parseLong(s);
            if(((Long)value).equals(lng))
              return true;
          }
          catch(Throwable t) {}
        }
      }
      else {
        for (String s : termsArr)
          if(((String)value).contains(s))
            return true;
      }
      return false;
    }

    @Override
    public BeanContainer<Long, QuickUser> getContainer()
    {
      return container;
    }
  }

  @Override
  public boolean userUpdated_oobTL(Object uId)
  {
    // Not used here, we update the table container directly after edits.
    return false;
  }

  /* View interface*/
  @Override
  public void enter(ViewChangeEvent event)
  {
    Object key = HSess.checkInit();
    initGuiTL();
    HSess.checkClose(key);
  }
}
