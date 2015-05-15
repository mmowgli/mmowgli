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

import static edu.nps.moves.mmowgli.MmowgliEvent.ACTIONPLANSHOWCLICK;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.data.hbnutil.HbnContainer.EntityItem;
import com.vaadin.data.util.AbstractBeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.AppEvent;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.cache.MCacheActionPlanHelper.QuickActionPlan;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanContainers.HelpWantedContainer;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanContainers.QuickAllPlansInThisMove;

/**
 * ActionPlanTable.java
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
public class ActionPlanTable extends Table
{
  private static final long serialVersionUID = -9089657488080127786L;
  
  private static String IDFORSORTING_COLUMN_NAME = "idForSorting";
  private static String TITLE_COLUMN_NAME = "title";
  private static String AUTHORS_COLUMN_NAME = "authors";
  private static String HELPWANTED_COLUMN_NAME = "helpWanted";
  private static String MYTHUMBS_COLUMN_NAME = "myThumbs";
  private static String AVGTHUMBS_COLUMN_NAME = "averageThumb";
  private static String ROUND_COLUMN_NAME = "createdInMove";
  
  private static String[] visibleColumns = {IDFORSORTING_COLUMN_NAME, TITLE_COLUMN_NAME, AUTHORS_COLUMN_NAME, /*MYTHUMBS_COLUMN_NAME,*/ AVGTHUMBS_COLUMN_NAME};  
  private static String[] columnNames    = {"ID","Title","Authors",/*"Thumbs","Avg"*/"Thumbs"}; // These get auto capitalized
  
  private static String[] visibleColumnsWithRound = {IDFORSORTING_COLUMN_NAME, ROUND_COLUMN_NAME,TITLE_COLUMN_NAME, AUTHORS_COLUMN_NAME,/* MYTHUMBS_COLUMN_NAME,*/ AVGTHUMBS_COLUMN_NAME}; 
  private static String[] columnNamesWithRound = {"ID","RND","Title","Authors",/*"Thumbs","Avg"*/"Thumbs"}; // These get auto capitalized
  
  private static String[] visibleColumnsHelpWanted = {IDFORSORTING_COLUMN_NAME, TITLE_COLUMN_NAME, HELPWANTED_COLUMN_NAME};
  private static String[] columnNamesHelpWanted = {"ID","Title","Expertise Needed"};
  
  private static String[] visibleColumnsHelpWantedWithRound = {IDFORSORTING_COLUMN_NAME, ROUND_COLUMN_NAME, TITLE_COLUMN_NAME, HELPWANTED_COLUMN_NAME};
  private static String[] columnNamesHelpWantedWithRound = {"ID","RND","Title","Expertise Needed"};
  

  private HbnContainer<ActionPlan> hbncontainer;
  private Container container;

  private long myUserId;
  private NumberFormat avgThumbFormatter = new DecimalFormat("0.00");
  
  public ActionPlanTable()
  {
    this(null);
  }
  @HibernateSessionThreadLocalConstructor
  @SuppressWarnings("unchecked")
  public ActionPlanTable(Container cont)
  {
    if(cont instanceof HbnContainer)
      this.hbncontainer = (HbnContainer<ActionPlan>)cont;
    else
      this.container = cont;
    
    setSelectable(true);
    setImmediate(true);
    setMultiSelect(false);
    setEditable(false);
    setNullSelectionAllowed(true); // why not
    setPageLength(100); // this helps with the thumbs bug
    addStyleName("m-actiondashboard-table");
    myUserId = (Long) Mmowgli2UI.getGlobals().getUserID();
    
    if(hbncontainer == null && container == null) {
      hbncontainer=ActionPlan.getContainer();
    }
    initFromDataSource(hbncontainer!=null?hbncontainer:container);
    
    addItemClickListener(getItemClickListener());
  }
  
  @SuppressWarnings("serial")
  public ItemClickListener getItemClickListener()
  {
    return new ItemClickListener()
    {
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      @SuppressWarnings("rawtypes")
      @Override
      public void itemClick(ItemClickEvent event)
      {
        HSess.init();
     // if(event.isDoubleClick()){
        Object item = event.getItem();
        Long apId;
        if(item instanceof BeanItem<?>) {
          Object bean = ((BeanItem<?>)item).getBean();
          if(bean instanceof ActionPlan)
            apId = ((ActionPlan)bean).getId();
          else
            apId = ((QuickActionPlan)bean).getId();
        }
        else
          apId = ((ActionPlan)(((EntityItem)event.getItem()).getPojo())).getId();
          
        Mmowgli2UI.getGlobals().getController().miscEventTL(new AppEvent(ACTIONPLANSHOWCLICK,ActionPlanTable.this,apId));
    // }
       HSess.close();
      }     
    };
  }
  
  private boolean columnsInitted = false;
  public void initFromDataSource(Container con)
  {
    if(con instanceof AbstractBeanContainer & !(con instanceof QuickAllPlansInThisMove))  // don't know the details here
     ((AbstractBeanContainer<?, ?>)con).addNestedContainerProperty(IDFORSORTING_COLUMN_NAME);
    else
      ; //con.addContainerProperty(IDFORSORTING_COLUMN_NAME,Long.class,null);
    try {
      setContainerDataSource(con);
    }
    catch(Throwable t) {
      t.printStackTrace();
    }
    
    if(con instanceof HelpWantedContainer) {
      finishFromHelpWantedDataSource();
      return;
    }
    
    boolean showRound = User.getTL(myUserId).isAdministrator() || (Game.getTL().isShowPriorMovesActionPlans() && Move.getCurrentMoveTL().getNumber()>1);

    if(!columnsInitted) {
      Table.ColumnGenerator colGen = new columnCustomizer();
      addGeneratedColumn(IDFORSORTING_COLUMN_NAME,colGen);                // id not standardly retrievable for some reason
      
      if(showRound) {
        addGeneratedColumn(ROUND_COLUMN_NAME,colGen);
      }    
      addGeneratedColumn(AUTHORS_COLUMN_NAME,colGen);
      addGeneratedColumn(TITLE_COLUMN_NAME,colGen);
      //addGeneratedColumn(MYTHUMBS_COLUMN_NAME,colGen);
      addGeneratedColumn(AVGTHUMBS_COLUMN_NAME,colGen);
      setColumnExpandRatio(TITLE_COLUMN_NAME, 1.0f);
      columnsInitted = true;
    }
    if(showRound) {
      setVisibleColumns((Object[])visibleColumnsWithRound);
      setColumnHeaders(columnNamesWithRound);
      setAllColumnWidthsWithRound();
    }
    else {
      setVisibleColumns((Object[])visibleColumns);
      setColumnHeaders(columnNames);
      setAllColumnWidths();    
    }
  }
  
  private void finishFromHelpWantedDataSource()
  {
    boolean showRound = User.getTL(myUserId).isAdministrator() || (Game.getTL().isShowPriorMovesActionPlans() && Move.getCurrentMoveTL().getNumber()>1);

    if(!columnsInitted) {
      Table.ColumnGenerator colGen = new columnCustomizer();
    
      addGeneratedColumn(IDFORSORTING_COLUMN_NAME,colGen); 
      if(showRound)
        addGeneratedColumn(ROUND_COLUMN_NAME,colGen);
      
      addGeneratedColumn(HELPWANTED_COLUMN_NAME,colGen);
      addGeneratedColumn(TITLE_COLUMN_NAME,colGen);
 //     setColumnExpandRatio(HELPWANTED_COLUMN_NAME, 1.0f);
      columnsInitted = true;
    }
    if(showRound) {
      setVisibleColumns((Object[])visibleColumnsHelpWantedWithRound);
      setColumnHeaders(columnNamesHelpWantedWithRound);
      setAllColumnWidthsHelpWantedWithRound();
    }
    else {
      setVisibleColumns((Object[])visibleColumnsHelpWanted);
      setColumnHeaders(columnNamesHelpWanted);
      setAllColumnWidthsHelpWanted();     
    }
  }
  
  private void setAllColumnWidths()
  {
    setColumnWidth(IDFORSORTING_COLUMN_NAME,35);
    setColumnWidth(AUTHORS_COLUMN_NAME,165);
    //setColumnWidth(MYTHUMBS_COLUMN_NAME,60);
    setColumnWidth(AVGTHUMBS_COLUMN_NAME,60); //32);
  }
  private void setAllColumnWidthsWithRound()
  {
    setColumnWidth(IDFORSORTING_COLUMN_NAME,35);
    setColumnWidth(ROUND_COLUMN_NAME,30);
    setColumnWidth(AUTHORS_COLUMN_NAME,140);
    //setColumnWidth(MYTHUMBS_COLUMN_NAME,60);
    setColumnWidth(AVGTHUMBS_COLUMN_NAME,60); //32);
  }
  
  private void setAllColumnWidthsHelpWanted()
  {
    setColumnWidth(IDFORSORTING_COLUMN_NAME,35);
    setColumnWidth(TITLE_COLUMN_NAME,266); 
  }
  private void setAllColumnWidthsHelpWantedWithRound()
  {
    setColumnWidth(IDFORSORTING_COLUMN_NAME,35);
    setColumnWidth(ROUND_COLUMN_NAME,30);
    setColumnWidth(TITLE_COLUMN_NAME,236); 
  }
 
  private User getUser(Object obj)
  {
    return User.getTL(obj);
  }
  
  class columnCustomizer implements Table.ColumnGenerator
  {
    private static final long serialVersionUID = 1938821794468835620L;
    StringBuilder sb = new StringBuilder();

    @SuppressWarnings("rawtypes")
    @Override
    public Component generateCell(Table source, Object itemId, Object columnId)
    {
      Object sessKey = HSess.checkInit();
      ActionPlan ap;
      MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
      Object obj = ActionPlanTable.this.getItem(itemId);
      
      if(obj instanceof BeanItem<?>) {
        Object bean = ((BeanItem<?>)obj).getBean();
        if(bean instanceof QuickActionPlan)
          return generateCellFromQuickActionPlan(source,itemId, columnId);
        ap = (ActionPlan)((BeanItem<?>)obj).getBean();
      }
      else {
        EntityItem ei = (EntityItem) obj;
        ap = (ActionPlan) ei.getPojo();
      }
      ap = ActionPlan.mergeTL(ap);
      if (IDFORSORTING_COLUMN_NAME.equals(columnId)) {
        Label lab = new HtmlLabel(""+ap.getId());
        String hw=null;
        if((hw=ap.getHelpWanted()) != null) {
          lab.addStyleName("m-actionplan-redtext");
          lab.setDescription("Help wanted: "+hw);
        }
        if(ap.isHidden()) {
          lab.setValue(lab.getValue().toString()+"<span style='color:#CB0613'>(H)</span>");
          lab.setDescription("hidden");
        } 
        HSess.checkClose(sessKey);
        return lab;
      }
      if (ROUND_COLUMN_NAME.equals(columnId)) {
        Label lab = new Label("" + ap.getCreatedInMove().getNumber());
        lab.setDescription("Creation round");
        HSess.checkClose(sessKey);
        return lab;
      }
      if(TITLE_COLUMN_NAME.equals(columnId)) {
        Label lab = null;     
        if(ap.isSuperInteresting()) {
          lab = new HtmlLabel("<b>"+FontAwesome.STAR.getHtml()+"&nbsp;"+ap.getTitle()+"</b>");
          lab.addStyleName("m-actionplan-highlighted_background");
          lab.setDescription("SUPER INTERESTING: "+ap.getTitle());
        }
        else {
          lab = new Label(ap.getTitle());
          lab.setDescription(lab.getValue().toString());
        }
        HSess.checkClose(sessKey);
        return lab;
      }
      if(HELPWANTED_COLUMN_NAME.equals(columnId)) {
        Label lab = new Label(ap.getHelpWanted());
        lab.setDescription(lab.getValue().toString());
        HSess.checkClose(sessKey);
        return lab;        
      }
      if (MYTHUMBS_COLUMN_NAME.equals(columnId)) {
        Map<User,Integer>map = ap.getUserThumbs();       
        Integer myRating = map.get(User.getTL(ActionPlanTable.this.myUserId));
        Label lab;
        if(myRating == null || myRating.intValue()==0)
          lab = new Label("--");
        else
          lab = new Label(""+myRating.intValue());
        lab.setDescription("My thumb rating for this action plan");
        HSess.checkClose(sessKey);
        return lab;
      }
      if (AVGTHUMBS_COLUMN_NAME.equals(columnId)) {
        double avg = ap.getAverageThumb();
        Label lab;
        if(avg == 0.0d)
          lab = new HtmlLabel("--");
        else
          lab = new HtmlLabel(avgThumbFormatter.format(avg));
        int numVoters = ap.getUserThumbs().size();
        lab.setDescription("<center>Average thumb rating for this action plan<br/>(\"--\" means no votes received)<br/>"
            +"Total voters: "+numVoters+"</center>");
        HSess.checkClose(sessKey);
        return lab;
      }
      if (AUTHORS_COLUMN_NAME.equals(columnId)) {
        // First see if I've been invited to any action plans, put up the link if so
        User me = getUser(globs.getUserID());
        Set<User> invitees = ap.getInvitees();
        
        if(invitees != null && invitees.size()>0) {
          for(User invited : invitees) {
            if(invited.getId() == me.getId()) {
              Button b = new Button("you're invited to join");
              b.setStyleName(BaseTheme.BUTTON_LINK);
              b.addClickListener(new AuthorPlanListener(ap));
              b.setDescription("You've been invited to become an author of this plan");
              b.setEnabled(!globs.isGameReadOnly() && !globs.isViewOnlyUser());
              HSess.checkClose(sessKey);
              return b;
            }
          }
        }
        
        // Else list authors
        Label lab;
        
        String quickAuthors = ap.getQuickAuthorList();
        if(quickAuthors != null && quickAuthors.length()>0) {
          lab = new Label(quickAuthors);
          lab.setDescription(quickAuthors);
        }       
        else if(ap.getInvitees().size()>0){ // no authors, if there are invitees, put "pending"
          lab = new Label("pending"); //sb.append("pending");
          lab.setDescription("This plan has outstanding invitations");
        }
        else {
          // No authors, no invitees, Let anybody in
          Button b = new Button("author this plan");
          b.setStyleName(BaseTheme.BUTTON_LINK);
          b.addClickListener(new AuthorPlanListener(ap));
          b.setDescription("You are free to become an author of this plan");
          HSess.checkClose(sessKey);
          return b;
        }
        HSess.checkClose(sessKey);
        return lab;
      }
      HSess.checkClose(sessKey);
      return new Label("Program error in ActionPlanTable.java");
    }
    
    public Component generateCellFromQuickActionPlan(Table source, Object itemId, Object columnId)
    {
     // Object sessKey = HSess.checkInit();
      QuickActionPlan qap;
      MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
      Object thisRow = ActionPlanTable.this.getItem(itemId);      
      qap = (QuickActionPlan)((BeanItem<?>)thisRow).getBean();
      Component ret;
      
      if (IDFORSORTING_COLUMN_NAME.equals(columnId)) {
        Label lab  = new HtmlLabel(""+qap.getId());
        String hw=null;
        if((hw=qap.getHelpWanted()) != null) {
          lab.addStyleName("m-actionplan-redtext");
          lab.setDescription("Help wanted: "+hw);
        }
        if(qap.isHidden()) {
          lab.setValue(lab.getValue().toString()+"<span style='color:#CB0613'>(H)</span>");
          lab.setDescription("hidden");
        } 
        ret = lab;
      }
      //------------------
      else if (ROUND_COLUMN_NAME.equals(columnId)) {
        Label lab = new Label(""+qap.getCreatedInMove());
        lab.setDescription("Creation round");
        ret = lab;
      }
      //------------------
      else if (TITLE_COLUMN_NAME.equals(columnId)) {        
        Label lab = null;     
        if(qap.isSuperInteresting()) {
          lab = new HtmlLabel("<b>"+FontAwesome.STAR.getHtml()+"&nbsp;"+qap.getTitle()+"</b>");
          lab.addStyleName("m-actionplan-highlighted_background");
          lab.setDescription("SUPER INTERESTING: "+qap.getTitle());
        }
        else {
          lab = new Label(qap.getTitle());
          lab.setDescription(lab.getValue().toString());
        }
        ret = lab;
      }
      //------------------
      else if (AUTHORS_COLUMN_NAME.equals(columnId)) {
        // First see if I've been invited to any action plans, put up the link if so
        long meId = (Long)globs.getUserID();
        long[] inviteesArr = qap.getInviteeIds();
        String quickAuthors = qap.getAuthorNames();
        
        Button b = null;
        if(inviteesArr != null && inviteesArr.length>0) {
          for(long id : inviteesArr) {
            if(id == meId) {
              b = new Button("you're invited to join");
              b.setStyleName(BaseTheme.BUTTON_LINK);
              b.addClickListener(new AuthorPlanListener(qap.getId()));
              b.setDescription("You've been invited to become an author of this plan");
              b.setEnabled(!globs.isGameReadOnly() && !globs.isViewOnlyUser());
              ret = b;
              break;
            }
          }
        }
        if(b != null) {
          ret = b;
        }
        // Else list authors
        else if(quickAuthors != null && quickAuthors.length()>0) {
          Label lab = new Label(quickAuthors);
          lab.setDescription(quickAuthors);
          ret = lab;
        }       
        else if(inviteesArr !=null && inviteesArr.length>0) { // no authors, so if there are invitees, put "pending"
          Label lab = new Label("pending");
          lab.setDescription("This plan has outstanding invitations");
          ret = lab;
        }
        else {
          // No authors, no invitees, Let anybody in
          b = new Button("author this plan");
          b.setStyleName(BaseTheme.BUTTON_LINK);
          b.addClickListener(new AuthorPlanListener(qap.getId()));
          b.setDescription("You are free to become an author of this plan");
          ret = b;
        }       
      }
      //------------------
      else if (AVGTHUMBS_COLUMN_NAME.equals(columnId)) {
        if(qap.getAverageThumbs() == 0.0d)
          ret = new HtmlLabel("--");
        else
          ret = new HtmlLabel(avgThumbFormatter.format(qap.getAverageThumbs()));
      }
      //------------------
      else
        ret = new Label("error");
      return ret;
    }
  }
  
  @SuppressWarnings("serial")
  class AuthorPlanListener implements ClickListener
  {
    private long apId;
    public AuthorPlanListener(ActionPlan ap)
    {
      this.apId = ap.getId();
    }
    public AuthorPlanListener(Long apId)
    {
      this.apId = apId;
    }
    @Override
    public void buttonClick(ClickEvent event)
    {
      Window w = new AuthorThisPlanPopup(apId);
      w.setModal(true);
      w.center();
      UI.getCurrent().addWindow(w);
      w.addCloseListener(new CloseListener()
      {
        @Override
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void windowClose(CloseEvent e)
        {
          Object key = HSess.checkInit();  // can be entered in same thread as AuthorThisPlanPopup.buttonclick()
          Mmowgli2UI.getGlobals().getController().miscEventTL(new AppEvent(ACTIONPLANSHOWCLICK,ActionPlanTable.this,apId)); 
          HSess.checkClose(key);
        }       
      });
    }   
  }
 
  
}
