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

import static edu.nps.moves.mmowgli.MmowgliConstants.APPLICATION_SCREEN_WIDTH;
import static edu.nps.moves.mmowgli.MmowgliEvent.IMPROVESCORECLICK;

import java.util.ArrayList;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.db.Move;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.WantsUserUpdates;
import edu.nps.moves.mmowgli.utility.IDNativeButton;

/**
 * Leaderboard.java Created on Dec 23, 2010
 * Updated 
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Leaderboard extends VerticalLayout implements MmowgliComponent, WantsUserUpdates, View
{
  private static final long serialVersionUID = -3438922984075729555L;
  private Table table;
  private VerticalLayout tableVLayout;
  private IDNativeButton tips;

  @HibernateSessionThreadLocalConstructor
  public Leaderboard()
  {
    tips = new IDNativeButton("Want to improve your score?", IMPROVESCORECLICK);
  }

  @Override
  public void initGui()
  {
    throw new UnsupportedOperationException("");
  }
  
  public void initGuiTL()
  {
    setSizeUndefined();
    setWidth(APPLICATION_SCREEN_WIDTH);
    setSpacing(false);

    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    Label sp;
    
    HorizontalLayout titleHL = new HorizontalLayout();
    addComponent(titleHL);
    titleHL.setMargin(new MarginInfo(false,true,false,true));  //sides only
    titleHL.setWidth("100%");

    Component c;
    titleHL.addComponent(c=globs.getMediaLocator().getLeaderboardTitle());
    titleHL.setComponentAlignment(c, Alignment.MIDDLE_LEFT);
    titleHL.setExpandRatio(c, .5f);

    maybeShowMoveSelector(titleHL);
  
    tips.setStyleName(BaseTheme.BUTTON_LINK);
    tips.addStyleName("m-link-button");
    titleHL.addComponent(tips);
    titleHL.setComponentAlignment(tips, Alignment.MIDDLE_RIGHT);
    titleHL.setExpandRatio(tips, 0.5f);
    
    tableVLayout = new VerticalLayout();
    tableVLayout.setSizeUndefined();
    tableVLayout.setWidth(APPLICATION_SCREEN_WIDTH);
    tableVLayout.setSpacing(true);
    tableVLayout.setMargin(true);
    tableVLayout.addStyleName("m-whitepage-header");
    
    addComponent(tableVLayout);
 
    tableVLayout.addComponent(sp = new Label());
    sp.setHeight("20px"); // to fit top of background

    tableVLayout.addComponent(table = createTableTL());
    tableVLayout.setComponentAlignment(table, Alignment.TOP_CENTER);
    table.setValue(globs.getUserID());
    tableVLayout.setExpandRatio(table, 1.0f);
    
    /* I can't get this to properly refresh, todo
    refreshButt = new Button("Refresh");
    mainVLayout.addComponent(refreshButt);
    mainVLayout.setComponentAlignment(refreshButt, Alignment.TOP_CENTER);
    
    refreshButt.addListener(new RefreshListener());
    refreshButt.setEnabled(false);
 */
  }
  private static String COMBINED = "Combined";
  private void maybeShowMoveSelector(HorizontalLayout hl)
  {
    Move m = Move.getCurrentMoveTL();
    int thisMove = m.getNumber();
    if(thisMove<=1)
      return;

    Label lab = null;
    hl.addComponent(lab=new Label("Showing scores from "));
    lab.setSizeUndefined();
    hl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
    hl.addComponent(lab = new Label());
    lab.setWidth("5px");

    ArrayList<String> mvLst = new ArrayList<String>();
    int i = 1;
    while(i<=thisMove) {
      mvLst.add("Round "+i++);
    }
    mvLst.add(COMBINED);
    
    NativeSelect sel = new NativeSelect(null,mvLst);

    sel.setNullSelectionAllowed(false);
    sel.setValue("Round "+thisMove);
    sel.setImmediate(true);
    sel.addValueChangeListener(new MoveListener());

    hl.addComponent(sel);
    hl.setComponentAlignment(sel, Alignment.MIDDLE_CENTER);
  }
 
  @SuppressWarnings("serial")
  class MoveListener implements ValueChangeListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void valueChange(ValueChangeEvent event)
    {
      HSess.init();
      
      String mv = (String)event.getProperty().getValue();
      if(mv.equals(COMBINED))
        setTableByCombinedScoreTL();
      else {
        String[] sa = mv.split(" ");  // format "Round n"
        setTableByMoveNumberTL(Integer.parseInt(sa[1]));
      }
      
      HSess.close();
    }   
  }
  
  // This is very close to the BuddyTable,
  // todo merge
  Table createTableTL()
  {
    Table tab = UserTable.makeLeaderBoardTableTL(Move.getCurrentMoveTL().getNumber());
    tab.setWidth("920px");
    tab.setHeight("100%");
    return tab;
  }
  
  private void setTableByCombinedScoreTL()
  {
    setTableCommon(UserTable.makeLeaderBoardCombinedScoreTableTL());
  }
  
  private void setTableByMoveNumberTL(int num)
  {
    setTableCommon(UserTable.makeLeaderBoardTableTL(num));    
  }
  
  private void setTableCommon(Table newTable)
  {
    if(table != null)
      tableVLayout.removeComponent(table);
    
    table = newTable;
    table.setWidth("920px");
    table.setHeight("100%");
    tableVLayout.addComponent(table);
    
    tableVLayout.setComponentAlignment(table, Alignment.TOP_CENTER);
    table.setValue(Mmowgli2UI.getGlobals().getUserID());
    tableVLayout.setExpandRatio(table, 1.0f);
    
  }
  
  // Here we're told that user data has changed, probably scores
  @Override
  public boolean userUpdated_oobTL(Object uId)
  {
    return false; // don't need a ui update
  }

  @Override
  @MmowgliCodeEntry
  @HibernateConditionallyOpened
  @HibernateConditionallyClosed
  public void enter(ViewChangeEvent event)
  {
    Object sessKey = HSess.checkInit();
    initGuiTL(); 
    HSess.checkClose(sessKey);
  }

}
