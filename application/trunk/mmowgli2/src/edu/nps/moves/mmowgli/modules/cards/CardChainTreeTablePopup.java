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

package edu.nps.moves.mmowgli.modules.cards;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliDialog2;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * CardChainTreeTablePopup.java Created on Feb 26, 2011
 * Updated on Mar 14, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardChainTreeTablePopup extends MmowgliDialog2 implements ClickListener, ItemClickListener
{
  private static final long serialVersionUID = -2617923342376583365L;
  
  private CardChainTree treeT;
  private Object selectedId;
  private Object tempSelectedId;
  private SaveListener saveListener;
  
  public CardChainTreeTablePopup(Object rootId)
  {
    this(rootId,false,false);
  }
  
  @HibernateSessionThreadLocalConstructor
  public CardChainTreeTablePopup(Object rootId, boolean modal, boolean wantSaveButton)
  {
    super(null);
    setWidth("600px");
    setHeight("400px");
    addStyleName("m-noborder"); // V7 difference
    
    super.initGui();
    selectedId = tempSelectedId = rootId;
    setModal(modal);
    setListener(this);
    setResizable(true);
    
    setTitleString("Card chain");
    saveClicked = false;
    
    contentVLayout.setSpacing(true);
 
    treeT = new CardChainTree(rootId,false,!modal);
    if(rootId == null) {
      setTitleString("Card chains");
     // instead, do some creative backgrounding to pseudo select children of a card, treeT.setMultiSelect(true);
    }
    //treeT.setSizeFull();
    treeT.setWidth("99%");
    treeT.setHeight("99%");
    treeT.addItemClickListener((ItemClickListener)this);
    treeT.addStyleName("m-greyborder");
    contentVLayout.addComponent(treeT);
    contentVLayout.setComponentAlignment(treeT, Alignment.MIDDLE_CENTER);
    contentVLayout.setExpandRatio(treeT, 1.0f);
    
    /* todo...the saved data was never being retrieved, should pass it back to create action plan panel */
    
    if(wantSaveButton) {
      // need a save button
      HorizontalLayout hl = new HorizontalLayout();
      hl.setWidth("100%");
      contentVLayout.addComponent(hl);
      Label lab;
      hl.addComponent(lab = new Label());
      hl.setExpandRatio(lab, 1.0f);
      
      NativeButton saveButt = new NativeButton();
      hl.addComponent(saveButt);
      saveButt.setIcon(Mmowgli2UI.getGlobals().getMediaLocator().getSaveButtonIcon());
      saveButt.setWidth("45px"); //38px");
      saveButt.setHeight("16px");
      saveButt.addStyleName("borderless");
      saveButt.addClickListener(saveListener=new SaveListener());
      saveButt.setClickShortcut(KeyCode.ENTER);
      hl.addComponent(lab = new Label());
      lab.setWidth("30px");
      contentVLayout.addComponent(hl);    
    }
  }
  
  @SuppressWarnings("serial")
  class SaveListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      saveClicked = true;
      if(tempSelectedId == null)
        tempSelectedId = treeT.getCardIdFromSelectedItem(treeT.getValue());
      selectedId = tempSelectedId; 
      UI.getCurrent().removeWindow(CardChainTreeTablePopup.this);
    }   
  }
  
  @Override
  public User getUser()
  {
    return null;
  }

  @Override
  public void setUser(User u)
  {
  }

  public boolean saveClicked = false;
  
  public TreeTable getTreeTable()
  {
    return treeT;
  }
  
  @Override
  public void buttonClick(ClickEvent event)
  {
    saveClicked = false;
    UI.getCurrent().removeWindow(this);
  }
  
  @Override
  public void itemClick(ItemClickEvent event)
  {
    tempSelectedId = treeT.getCardIdFromSelectedItem(event.getItemId());
    if(this.isModal())
      if(event.isDoubleClick() && saveListener != null) {
        saveListener.buttonClick(null);
    }
  }

  public Object getSelectedCardId()
  {
    return selectedId;
  }
}
