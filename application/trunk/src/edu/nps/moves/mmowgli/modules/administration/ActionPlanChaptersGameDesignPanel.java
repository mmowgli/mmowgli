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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.hibernate.Session;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.TextArea;

import edu.nps.moves.mmowgli.db.Move;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.HibernateUpdate;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

/**
 * HeaderFooterGameDesignPanel.java
 * Created on Mar 28, 2013
 * Updated on Mar 12, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanChaptersGameDesignPanel extends AbstractGameBuilderPanel implements MoveChangeListener
{
  private static final long serialVersionUID = 7466151010695755468L;

  private TextArea ta_who,ta_what,ta_take,ta_work,ta_change;

 //Although the headers are stored in the phase table, they're changed en masse...i.e., all phases in a move are identical
  private Move editMove;
  public ActionPlanChaptersGameDesignPanel(Move move, GameDesignGlobals globs)
  {
    super(false,globs);
    editMove = move;
    MovePhase ph = move.getCurrentMovePhase();
    
    ta_who = addEditLine("1 (Who is involved?)", "MovePhase.actionPlanWhoIsInvolvedHeader");
    ta_who.addValueChangeListener(new MyValueChangeListener("setActionPlanWhoIsInvolvedHeader"));
    
    ta_what = addEditLine("2 (What is it?)", "MovePhase.actionPlanWhatIsItHeader");
    ta_what.addValueChangeListener(new MyValueChangeListener("setActionPlanWhatIsItHeader")); 
    
    ta_take = addEditLine("3 (What will it take?)", "MovePhase.actionPlanWhatWillItTakeHeader");
    ta_take.addValueChangeListener(new MyValueChangeListener("setActionPlanWhatWillItTakeHeader"));
    
    ta_work = addEditLine("4 (How will it work?)", "MovePhase.actionPlanHowWillItWorkHeader");
    ta_work.addValueChangeListener(new MyValueChangeListener("setActionPlanHowWillItWorkHeader"));
    
    ta_change = addEditLine("5 (How will it change the situation?)", "MovePhase.actionPlanHowWillItChangeHeader");
    ta_change.addValueChangeListener(new MyValueChangeListener("setActionPlanHowWillItChangeHeader"));
    
    loadPanels(ph,globs);
  }
  
  private void loadPanels(MovePhase ph, GameDesignGlobals globs)
  {
    commitOK = false;
    boolean glRo = globs.readOnlyCheck(false);
    ta_who.setReadOnly(false);   ta_who.setValue(ph.getActionPlanWhoIsInvolvedHeader());     ta_who.setReadOnly(glRo);  
    ta_what.setReadOnly(false);  ta_what.setValue(ph.getActionPlanWhatIsItHeader());         ta_what.setReadOnly(glRo);
    ta_take.setReadOnly(false);  ta_take.setValue(ph.getActionPlanWhatWillItTakeHeader());   ta_take.setReadOnly(glRo);
    ta_work.setReadOnly(false);  ta_work.setValue(ph.getActionPlanHowWillItWorkHeader());    ta_work.setReadOnly(glRo);
    ta_change.setReadOnly(false);ta_change.setValue(ph.getActionPlanHowWillItChangeHeader());ta_change.setReadOnly(glRo);
    commitOK = true;
  }
  
  boolean commitOK = true;
  
  @SuppressWarnings("serial")
  class MyValueChangeListener implements ValueChangeListener
  {
    String setMethod;
    public MyValueChangeListener(String setMethod)
    {
      this.setMethod = setMethod;
    }
    @Override
    @HibernateOpened
    @HibernateUpdate
    @HibernateClosed
    public void valueChange(ValueChangeEvent event)
    {
      if (!commitOK)
        return;

      String newtxt = event.getProperty().getValue().toString();

      HSess.init();

      editMove = (Move) HSess.get().merge(editMove);
      List<MovePhase> phases = editMove.getMovePhases();
      try {
        for (MovePhase ph : phases) {
          Method meth = MovePhase.class.getDeclaredMethod(setMethod, String.class);
          meth.invoke(ph, newtxt);
          MovePhase.updateTL(ph);
        }
        Move.updateTL(editMove);
      }
      catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
        MSysOut.println(ERROR_LOGS,"Program error in ActionPlanChaptersGameDesignPanel.MyValueChangeListener: " + ex.getClass().getSimpleName() + " "
            + ex.getLocalizedMessage());
      }
      HSess.close();
    }
  }
  
  @Override
  public void moveChangedTL(Move move)
  {
    editMove = move;
    loadPanels(move.getCurrentMovePhase(), globals);  // Although the headers are stored in the phase table, they're changed en masse...i.e., all phases in a move are identical   
  }

   @Override
  Embedded getImage()
  {
    return null;
  }
  
  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 170; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() + 20; // default = 240
  }

}
