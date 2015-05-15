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

package edu.nps.moves.mmowgli.modules.administration;

import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.db.Move;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;

/**
 * NewMovePhaseDialog.java Created on Apr 2, 2013
 * Updated on Mar 12, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class NewMovePhaseDialog extends Window
{
  private static final long serialVersionUID = -3543980329987811294L;
  
  private Move moveBeingEdited;
  private ComboBox descriptionCB;
  private String[] strings = { "PREPARATION", "PLAY", "PUBLISH" };
  private GridLayout existingPhasesGLay;
  Button saveButt,cancelButt;
  
  @SuppressWarnings("serial")
  public NewMovePhaseDialog(Move move)
  {
    super("Make a new Phase for " + move.getName());
    this.moveBeingEdited = Move.getTL(move.getId());
    setSizeUndefined();
    setWidth("390px");
    VerticalLayout vLay;
    setContent(vLay = new VerticalLayout());
    vLay.setSpacing(true);
    vLay.setMargin(true);
    vLay.addComponent(new Label("Choose a descriptive name for this phase.  Suggested names are shown in the " +
    		"drop down list, but any text is permitted."));

    descriptionCB = new ComboBox("Phase description");
    vLay.addComponent(descriptionCB);
    fillCombo(descriptionCB);
    
    descriptionCB.setInputPrompt("Choose suggested description, or enter text");
    descriptionCB.setWidth("350px");

    descriptionCB.setImmediate(true);
    descriptionCB.setNullSelectionAllowed(false);
    descriptionCB.setTextInputAllowed(true);
    descriptionCB.addValueChangeListener(new ValueChangeListener()
    {
      @Override
      public void valueChange(final ValueChangeEvent event)
      {
        // final String valueString = String.valueOf(event.getProperty().getValue());
        // Notification.show("Value changed:", valueString,
        // Type.TRAY_NOTIFICATION);
      }
    });
    vLay.addComponent(existingPhasesGLay = new GridLayout());
    existingPhasesGLay.setColumns(3);
    existingPhasesGLay.setRows(1); // to start
    existingPhasesGLay.setSpacing(true);
    existingPhasesGLay.addStyleName("m-greyborder");
    existingPhasesGLay.setCaption("Existing Phases");
    fillExistingPhases();
    
    HorizontalLayout buttHLay;
    vLay.addComponent(buttHLay= new HorizontalLayout());
    buttHLay.setWidth("100%");
    buttHLay.setSpacing(true);
    Label lab;
    buttHLay.addComponent(lab=new Label());
    lab.setWidth("1px");
    buttHLay.setExpandRatio(lab, 1.0f);
    buttHLay.addComponent(cancelButt = new Button("Cancel",saveCancelListener));
    buttHLay.addComponent(saveButt = new Button("Save",saveCancelListener));
  }
  
  class PhaseDeleteButt extends Button
  {
    private static final long serialVersionUID = 1L;
    public MovePhase movePhase;
    public PhaseDeleteButt(MovePhase mp)
    {
      super("delete",deleteListener);
      movePhase = mp;
    }
  }
  
  @SuppressWarnings("serial")
  ClickListener saveCancelListener = new ClickListener()
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    { 
      if(event.getButton() == saveButt) {
        Object obj = descriptionCB.getValue();
        if(obj == null || obj.toString().length()<=0) {
          Notification.show("Error","You must enter a phase description", Notification.Type.ERROR_MESSAGE);
          return;         
        }
        HSess.init();
        Move thisMove = Move.getTL(moveBeingEdited.getId());
        MovePhase existing = thisMove.getCurrentMovePhase();
        MovePhase mp = new MovePhase();
        mp.cloneFrom(existing);
        mp.setDescription(obj.toString());
        MovePhase.saveTL(mp);
        
        List<MovePhase> lis = thisMove.getMovePhases();
        lis.add(mp);
        Move.updateTL(thisMove);
        HSess.close();
      }
      
      UI.getCurrent().removeWindow(NewMovePhaseDialog.this);
    }   
  };
  
  @SuppressWarnings("serial")
  ClickListener deleteListener = new ClickListener()
  {
    private MovePhase mp;
    private Move thisMove;
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      thisMove = Move.getTL(moveBeingEdited.getId());
      HSess.close();
      
      int numPhases = thisMove.getMovePhases().size();
      if (numPhases <= 1) {
        Notification.show("Error","A round must contain at least one phase", Notification.Type.ERROR_MESSAGE);
        return;
      }

      mp = ((PhaseDeleteButt) event.getButton()).movePhase;
      if (thisMove.getCurrentMovePhase().getId() == mp.getId()) {
        Notification.show("Error","You cannot delete the active phase of a round", Notification.Type.ERROR_MESSAGE);
        return;
      }

      ConfirmDialog.show(UI.getCurrent(), "Please confirm phase delete:", "Are you really sure?", "Yes", "Never mind", confLis);
    }

    ConfirmDialog.Listener confLis = new ConfirmDialog.Listener()
    {
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void onClose(ConfirmDialog dialog)
      {
        if (!dialog.isConfirmed())
          return;
        HSess.init();
        thisMove = Move.mergeTL(thisMove);
        mp = MovePhase.mergeTL(mp);
        thisMove.getMovePhases().remove(mp);
        Move.updateTL(thisMove);
        MovePhase.deleteTL(mp);
        fillExistingPhases();
        HSess.close();
      }
    };
  };
  
  private void fillExistingPhases()
  {
    existingPhasesGLay.removeAllComponents();
    List<MovePhase> lis = moveBeingEdited.getMovePhases();
    int i = 1;
    for(MovePhase mp : lis) {
      existingPhasesGLay.addComponent(new Label(""+i++));
      existingPhasesGLay.addComponent(new Label(mp.getDescription()));
      Button butt;
      existingPhasesGLay.addComponent(butt=new PhaseDeleteButt(mp));
      butt.addStyleName(Reindeer.BUTTON_SMALL);
    }   
  }
  private void fillCombo(ComboBox box)
  {
    for (String s : strings)
      box.addItem(s);
  }

  public void showDialog()
  {
    UI.getCurrent().addWindow(this);
    center();
  }
}
