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
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.MmowgliMessageBroadcaster;
import edu.nps.moves.mmowgli.components.*;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.Move;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;

/**
 * EntryPermissionsDialog.java Created on May 15, 2013
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AdvanceMoveDialog extends Window
{
  private static final long serialVersionUID = 7926143141115782509L;

  public static String topParagraph = "<span style='margin:5px'>Use this dialog to change the game round and/or game phase.  A game can be made up of multiple rounds, "
      + "typically 1-3, and each round will consist of several phases.  This change must be considered to "
      + "be one-way, i.e., an <i>advancement</i>.  The game is not designed to arbitrarily change phases or "
      + "rounds.  In addition, when deciding to advance the round, keep in mind that the game will need to be "
      + "rebooted and any existing player sessions will be abruptly closed, so you must change the round, then immediately "
      + "restart the mmowgli web app.</span>";

  public static String bottomParagraph = "<span style='font-size:150%'>You must restart the game after advancing rounds!</span>";

  @HibernateSessionThreadLocalConstructor
  public AdvanceMoveDialog() {
    setSizeUndefined();
    setWidth("600px");
    setContent(new AdvanceMovePanel(this));
    setCaption("Advance Game Round or Phase");
  }

  @HibernateSessionThreadLocalConstructor
  public static class AdvanceMovePanel extends VerticalLayoutSpaced
  {
    private static final long serialVersionUID = -352472580105616508L;
    private Button broadcastButt, cancelButt, doItButt;
    private ListSelect nextRound, nextPhase;
    private Window myWindow;

    public AdvanceMovePanel(Window w) {
      myWindow = w;
      VerticalLayoutSpaced vl = this;
      vl.setSizeUndefined();
      vl.setWidth("100%");
      vl.setMargin(true);

      Label lab;
      vl.addComponent(lab = new HtmlLabel(topParagraph));
      // lab.addStyleName("m-greyborder");

      vl.addComponent(broadcastButt = new Button("Broadcast message to all players", broadcastListener));
      vl.setComponentAlignment(broadcastButt, Alignment.MIDDLE_CENTER);

      HorizontalLayout hl = new HorizontalLayoutSpaced();
      vl.addComponent(hl);
      vl.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);

      hl.addComponent(new Label("Current round: "));
      Move runningMove = Game.getTL().getCurrentMove();
      hl.addComponent(new HtmlLabel("<b>" + runningMove.getName() + "</b>"));

      hl = new HorizontalLayoutSpaced();
      vl.addComponent(hl);
      vl.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);

      hl.addComponent(new Label("Current phase: "));
      hl.addComponent(new HtmlLabel("<b>" + runningMove.getCurrentMovePhase().getDescription() + "</b>"));

      vl.addComponent(new Hr());

      hl = new HorizontalLayoutSpaced();
      vl.addComponent(hl);
      vl.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);

      hl.addComponent(new Label("Advance to round : "));
      hl.addComponent(nextRound = new ListSelect());
      hl.addComponent(new Label("phase : "));
      hl.addComponent(nextPhase = new ListSelect());

      vl.addComponent(doItButt = new Button("Advance", doItHandler));
      vl.setComponentAlignment(doItButt, Alignment.MIDDLE_CENTER);

      vl.addComponent(lab = new HtmlLabel(bottomParagraph));
      vl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
      lab.setSizeUndefined();
      lab.setHeight("20px");

      Component c;
      vl.addComponent(c = buildButtons());
      vl.setComponentAlignment(c, Alignment.MIDDLE_CENTER);

      fillCombos(runningMove);
    }

    private void fillCombos(Move move)
    {
      nextRound.setRows(4);
      nextRound.setNullSelectionAllowed(false);
      nextRound.setImmediate(true);
      nextRound.addValueChangeListener(roundListener);
      nextPhase.setRows(4);
      nextPhase.setNullSelectionAllowed(false);

      int curNum = move.getNumber();
      List<Move> lis = Move.getAllTL();
      MoveWrap sel = null;
      for (Move m : lis) {
        MoveWrap mw = new MoveWrap(m);
        nextRound.addItem(mw);
        if (sel == null || m.getNumber() == curNum)
          sel = mw;
      }
      if (sel != null)
        nextRound.setValue(sel);
    }

    @SuppressWarnings("serial")
    ValueChangeListener roundListener = new ValueChangeListener() {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void valueChange(ValueChangeEvent event)
      {
        Object sessKey = HSess.checkInit();
        MoveWrap mw = (MoveWrap) event.getProperty().getValue();
        MovePhase curmp = mw.m.getCurrentMovePhase();
        Move m = Move.getTL(mw.m.getId()); // freshen
        List<MovePhase> lis = m.getMovePhases();
        PhaseWrap sel = null;
        nextPhase.removeAllItems();
        for (MovePhase mp : lis) {
          PhaseWrap pw = new PhaseWrap(mp);
          nextPhase.addItem(pw);
          if (sel == null || mp.getId() == curmp.getId())
            sel = pw;
        }
        if (sel != null)
          nextPhase.setValue(sel);
        HSess.checkClose(sessKey);
      }
    };

    @SuppressWarnings("serial")
    ClickListener broadcastListener = new ClickListener() {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        MmowgliMessageBroadcaster.handleMessageBroadcastAction();
      }
    };

    @SuppressWarnings("serial")
    private void deadStop()
    {
      Window stopDialog = new Window("Restart mmowgli now") {
        @Override
        public void close()
        {
        }
      };

      stopDialog.setModal(true);
      
      VerticalLayout layout = new VerticalLayout();
      stopDialog.setContent(layout);
      layout.setMargin(true);
      layout.setSpacing(true);
      Label message = new Label("Restart mmowgli on cluster nodes now.");
      layout.addComponent(message);

      UI.getCurrent().addWindow(stopDialog);
      
      HSess.init();
      AppMaster.instance().killAllSessionsAndTellOtherNodesTL();
      HSess.close();
    }

    class MoveWrap
    {
      public Move m;

      public MoveWrap(Move m) {
        this.m = m;
      }

      public String toString()
      {
        return m.getName();
      }
    }

    class PhaseWrap
    {
      public MovePhase mp;

      public PhaseWrap(MovePhase mp) {
        this.mp = mp;
      }

      public String toString()
      {
        return mp.getDescription();
      }
    }

    private AbstractOrderedLayout buildButtons()
    {
      HorizontalLayout hl = new HorizontalLayout();
      hl.setWidth("99%");
      hl.setSpacing(true);
      hl.setMargin(false);
      Label lab;
      hl.addComponent(lab = new Label());
      lab.setWidth("1px");
      hl.setExpandRatio(lab, 1.0f);
      cancelButt = new Button("Cancel", cancelHandler);
      hl.addComponent(cancelButt);

      return hl;
    }

    @SuppressWarnings("serial")
    class PhaseChangeListener implements ConfirmDialog.Listener
    {
      MovePhase newPhase;

      PhaseChangeListener(MovePhase newPhase)
      {
        this.newPhase = newPhase;
      }

      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void onClose(ConfirmDialog dialog)
      {
        if (dialog.isConfirmed()) {
          HSess.init();
          Move m = Game.getTL().getCurrentMove();
          m.setCurrentMovePhase(MovePhase.mergeTL(newPhase));
          Move.updateTL(m);
          HSess.close();
          deadStop();
        }
      }
    };

    @SuppressWarnings("serial")
    class MoveChangeListener implements ConfirmDialog.Listener
    {
      Move newMove;
      MovePhase newPhase;

      MoveChangeListener(Move newMove, MovePhase newPhase)
      {
        this.newMove = newMove;
        this.newPhase = newPhase;
      }

      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void onClose(ConfirmDialog dialog)
      {
        if (dialog.isConfirmed()) {
          HSess.init();
          
          newMove = Move.mergeTL(newMove);
          newPhase = MovePhase.mergeTL(newPhase);
          newMove.setCurrentMovePhase(newPhase);
          Move.updateTL(newMove);
          Game.getTL().setCurrentMove(newMove);
          Game.updateTL();
          
          AppMaster.instance().killAllSessionsAndTellOtherNodesTL();

          HSess.close();
          deadStop();         
        }
      }
    };

    @SuppressWarnings("serial")
    private ClickListener doItHandler = new ClickListener() {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        HSess.init();

        Move currentMove = Game.getTL().getCurrentMove();
        Move chosenMove = ((MoveWrap) (nextRound.getValue())).m;
        MovePhase chosenPhase = ((PhaseWrap) (nextPhase.getValue())).mp;

        if (currentMove.getId() == chosenMove.getId()) {
          if (currentMove.getCurrentMovePhase().getId() == chosenPhase.getId()) {
            Notification n = new Notification("No change", "No advancement is indicated", Notification.Type.HUMANIZED_MESSAGE);
            n.setDelayMsec(3000);
            n.show(Page.getCurrent());// getWindow().showNotification(n);
            HSess.close();
            return;
          }
          else {
            ConfirmDialog.show(UI.getCurrent(), // AdvanceMovePanel.this.getWindow().getParent().getWindow(),
                "Change round phase to " + chosenPhase.getDescription() + "?  (The current "
                    + "game stops and a restart is required. Cancel if this is not what you want to do.)", new PhaseChangeListener(chosenPhase));
          }
        }
        else if (chosenMove.getNumber() != currentMove.getNumber() + 1) {
          if (chosenMove.getNumber() < currentMove.getNumber()) {
            Notification n = new Notification("No change", "Cannot change to previous rounds", Notification.Type.HUMANIZED_MESSAGE);
            n.setDelayMsec(3000);
            n.show(Page.getCurrent());
            HSess.close();
            return;
          }
        }
        else {
          ConfirmDialog.show(UI.getCurrent(), "Change to " + chosenMove.getName() + ", phase " + chosenPhase.getDescription() + "?  (The current "
              + "game stops and a restart is required. Cancel if this is not what you want to do.)", new MoveChangeListener(chosenMove, chosenPhase));
        }
        HSess.close();
      }
    };

    @SuppressWarnings("serial")
    private ClickListener cancelHandler = new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event)
      {
        myWindow.close();
      }
    };
  }
}
