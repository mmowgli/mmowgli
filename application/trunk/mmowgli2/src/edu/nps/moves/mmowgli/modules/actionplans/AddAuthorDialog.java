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

import java.util.Collection;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.cache.MCacheUserHelper.QuickUser;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.User;

/**
 * AddImageDialog.java
 * Created on Jan 7, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AddAuthorDialog extends Window
{ 
  private static final long serialVersionUID = 3921555770079516076L;
  
  private ListSelect userList; //BoundUsersListSelect userList;
  public Button cancelButt, addButt;
  public boolean addClicked = false;
  private Object selected;
  
  public Label infoLabel = new HtmlLabel("Choose users to receive invitations to author this plan.  Their names will appear on the action plan author list when/if they accept.");
  private HorizontalLayout buttHL;
  
  public AddAuthorDialog(Collection<User> currentlySelectedNames)
  {
    this(currentlySelectedNames,false);
  }
  
  @SuppressWarnings("serial")
  public AddAuthorDialog(Collection<User> currentlySelectedNames, boolean removeExisting)
  {
    setCaption("Invite Players to be Authors");
    setClosable(false); // no x in corner
    setWidth("300px");
    setHeight("400px");
    VerticalLayout mainVL = new VerticalLayout();
    mainVL.setSpacing(true);
    mainVL.setMargin(true);
    mainVL.setSizeFull();
    setContent(mainVL);
    
    mainVL.addComponent(infoLabel);
    userList = new ListSelect();
    userList.addStyleName("m-greyborder");
    
    List<QuickUser> qlis = AppMaster.instance().getMcache().getUsersQuickList();
    BeanItemContainer<QuickUser> beanContainerQ = new BeanItemContainer<QuickUser>(QuickUser.class,qlis);
    
    userList.setContainerDataSource(beanContainerQ);
    userList.setItemCaptionMode(ListSelect.ItemCaptionMode.PROPERTY);  // works!
    userList.setItemCaptionPropertyId("uname");
    userList.setNewItemsAllowed(false);
    
    mainVL.addComponent(userList);
    userList.setSizeFull();
    mainVL.setExpandRatio(userList, 1.0f);
    userList.setRows(15);
    userList.setImmediate(true);
    setMultiSelect(true); //userList.setMultiSelect(true);
    userList.setNullSelectionAllowed(false);
   
    if(currentlySelectedNames != null)
      for(User selU : currentlySelectedNames) {
        Collection<?> all = userList.getItemIds();
        for(Object o : all) {
          QuickUser qu = (QuickUser)o;
          if(selU.getId() == qu.getId()) {           
            if(removeExisting)
              userList.removeItem(o);
            else
              userList.select(o);
            break;
          }
        }
      } 
   
    buttHL  = new HorizontalLayout();
    buttHL.setSpacing(true);
    mainVL.addComponent(buttHL);
    buttHL.setWidth("100%");
    Label spacer;
    buttHL.addComponent(spacer=new Label());
    spacer.setWidth("1px");
    buttHL.setExpandRatio(spacer, 1.0f);
    
    buttHL.addComponent(addButt = new Button("Select"));
    buttHL.addComponent(cancelButt = new Button("Cancel"));
    
    cancelButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        selected = null;
        addClicked = false;
        UI.getCurrent().removeWindow(AddAuthorDialog.this);//getParent().removeWindow(AddAuthorDialog.this);
        if(closer != null)
          closer.windowClose(null);
      }      
    });   
    addButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        selected =  userList.getValue();
       
        addClicked = true;
        UI.getCurrent().removeWindow(AddAuthorDialog.this);//getParent().removeWindow(AddAuthorDialog.this);        
        if(closer != null)
          closer.windowClose(null);
      }      
    });
  } 
  public HorizontalLayout getButtonHorizontalLayout()
  {
    return buttHL;
  }
  public void setMultiSelect(boolean tf)
  {
    userList.setMultiSelect(tf);
  }
  
  public void selectItemAt(int idx)
  {
    Container cont = userList.getContainerDataSource();
    if( cont instanceof Container.Indexed) {
      Object o = ((Container.Indexed)cont).getIdByIndex(idx);
      userList.select(o);
    }
  }
  
  public Object getSelected()
  {
    return selected;
  }
  
  private Window.CloseListener closer;
  
  @Override
  public void addListener(CloseListener listener)
  {
    closer = listener;
  }
}
