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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.Move;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.administration.AbstractGameBuilderPanel.AuxiliaryChangeListener;
import edu.nps.moves.mmowgli.modules.administration.AbstractGameBuilderPanel.IndivListener;
import edu.nps.moves.mmowgli.modules.administration.MoveSelector.MWrap;

/**
 * BuildGamePanel.java Created on Oct 31, 2012
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: GameDesignPanel.java 3276 2014-01-14 23:55:51Z tdnorbra $
 */
public class GameDesignPanel extends Panel implements MmowgliComponent, GameDesignGlobals, View
{
  private static final long serialVersionUID = -6052661380574875970L;
  VerticalLayout mainVL;
  TabSheet tabSh;
  GlobalEditPanel globPan;
  RoundsEditPanel roundsPan;
  PhasesEditPanel phasesPan;

  String STATEKEY;

  List<Component> tabPanels;
  boolean allReadOnly = false;

  public GameDesignPanel()
  {
    this(false);
  }

  @HibernateSessionThreadLocalConstructor
  public GameDesignPanel(boolean readonly)
  {
    this.allReadOnly = readonly;
    addStyleName("m-marginleft-25");
    
    STATEKEY = getClass().getSimpleName();

    mainVL = new VerticalLayout();
    setContent(mainVL);
    mainVL.setWidth("100%");

    tabSh = new TabSheet();
    tabPanels = new ArrayList<Component>();

    tabPanels.add(globPan = new GlobalEditPanel(this));
    Move m = Move.getCurrentMoveTL();
    tabPanels.add(roundsPan = new RoundsEditPanel(m, this));
    tabPanels.add(phasesPan = new PhasesEditPanel(m, this));

    roundsPan.addMoveListener(phasesPan);
    
    setWidth("100%");
    setHeight("100%");
  }

  @Override
  public void initGui()
  {
  }
  public void initGuiTL()
  {
    tabSh.setHeight("100%");
    tabSh.setWidth("930px");

    tabSh.addTab(globPan, "Global Settings");
    //ClassResource cr = new ClassResource("/edu/nps/moves/mmowgli/mmowgliOne/resources/images/dot.png",getApplication());
    tabSh.addTab(roundsPan, "Round-dependent Settings"); //,cr);
    tabSh.addTab(phasesPan, "Phase-dependent Settings"); //,cr);
    mainVL.addComponent(tabSh);

    globPan.initGui();
    roundsPan.initGui();
    phasesPan.initGui();
  }

  @Override
  public boolean readOnlyCheck(boolean ro)
  {
    return allReadOnly || ro;
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

/*****************************************************/
class GlobalEditPanel extends VerticalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = 1L;

  TabSheet tabSh;
  List<Component> tabPanels = new ArrayList<Component>();

  BooleansGameDesignPanel booleansPan;
  HeaderFooterGameDesignPanel headerPan;
  GameLinksGameDesignPanel linksPan;
  ActionPlansGameDesignPanel aplansPan;
  MapGameDesignPanel mapPan;
  ReportsGameDesignPanel reportsPan;
  ScoringGameDesignPanel scorePan;

  @HibernateSessionThreadLocalConstructor
  public GlobalEditPanel(GameDesignGlobals globs)
  {
    setWidth("100%");

    tabSh = new TabSheet();

    tabPanels.add(booleansPan = new BooleansGameDesignPanel(globs));
    tabPanels.add(linksPan = new GameLinksGameDesignPanel(globs));
    tabPanels.add(headerPan = new HeaderFooterGameDesignPanel(globs));

    if (Game.getTL().isActionPlansEnabled())
        tabPanels.add(aplansPan = new ActionPlansGameDesignPanel(globs));

    tabPanels.add(mapPan = new MapGameDesignPanel(globs));
    tabPanels.add(reportsPan = new ReportsGameDesignPanel(globs));
    tabPanels.add(scorePan = new ScoringGameDesignPanel(globs));
  }

  @Override
  public void initGui()
  {
    tabSh.setHeight("100%");
    tabSh.setWidth("930px");

    tabSh.addTab(booleansPan, "Game Switches");
    tabSh.addTab(linksPan, "Game Links");
    tabSh.addTab(headerPan, "Header & Footer Links");

    if (Game.getTL().isActionPlansEnabled())
        tabSh.addTab(aplansPan, "Action Plan User Help");

    tabSh.addTab(mapPan, "Map");
    tabSh.addTab(reportsPan, "Reports");
    tabSh.addTab(scorePan, "Scoring");

    addComponent(tabSh);

    booleansPan.initGui();
    linksPan.initGui();
    headerPan.initGui();

    if (Game.getTL().isActionPlansEnabled())
        aplansPan.initGui();

    mapPan.initGui();
    reportsPan.initGui();
    scorePan.initGui();
  }
}

/*****************************************************/
class RoundsEditPanel extends VerticalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = 1L;

  TabSheet tabSh;
  List<Component> tabPanels = new ArrayList<Component>();
  NativeButton newMoveButt;

  TitlesGameDesignPanel titlesPan;
  TopCardsGameDesignPanel topCardsPan;
  SubCardsGameDesignPanel subCardsPan;
  //SeedCardsGameDesignPanel seedCardsPan;
  ActionPlanChaptersGameDesignPanel chaptersPan;

  MoveSelector moveSelector;
  private Move moveBeingEdited;
  private Label runningMoveWarningLabel;
  private MoveChangeListener externalListener;
  
  @HibernateSessionThreadLocalConstructor
  public RoundsEditPanel(Move editMove, GameDesignGlobals globs)
  {
    this.moveBeingEdited = editMove;
    setWidth("100%");

    tabSh = new TabSheet();

    tabPanels.add(titlesPan = new TitlesGameDesignPanel(editMove, globs));
    tabPanels.add(topCardsPan = new TopCardsGameDesignPanel(editMove, globs));
    tabPanels.add(subCardsPan = new SubCardsGameDesignPanel(editMove, globs));
    // this is not understandable to users, nor very functional
    //tabPanels.add(seedCardsPan = new SeedCardsGameDesignPanel(editMove, globs));
    tabPanels.add(chaptersPan = new ActionPlanChaptersGameDesignPanel(editMove, globs));
  }

  public void addMoveListener(MoveChangeListener lis)
  {
    externalListener = lis;
  }

  @Override
  public void initGui()
  {
    initGuiTL();
  }

  public void initGuiTL()
  {
    tabSh.setHeight("100%");
    tabSh.setWidth("930px");

    HorizontalLayout topHL = new HorizontalLayout();
    topHL.setSpacing(true);
    topHL.setMargin(true);
    Label lab;
    topHL.addComponent(lab = new Label());
    lab.setWidth("1px");
    topHL.setExpandRatio(lab, 0.5f);
    topHL.addComponent(lab = new Label("Round being edited:"));
    lab.setSizeUndefined();
    topHL.addComponent(moveSelector = new MoveSelector(null));
    moveSelector.addValueChangeListener(new MoveSelectorListener());
    topHL.addComponent(runningMoveWarningLabel = new HtmlLabel("<font color='red'><i>Active game round!</i></font>"));
    runningMoveWarningLabel.setSizeUndefined();
    runningMoveWarningLabel.setVisible(AbstractGameBuilderPanel.isRunningMoveTL(moveBeingEdited));

    topHL.addComponent(newMoveButt = new NativeButton("Add new round to game",new NewMoveListener()));
    topHL.addComponent(lab = new Label());
    lab.setWidth("1px");
    topHL.setExpandRatio(lab, 0.5f);
    topHL.setWidth("100%");
    addComponent(topHL);

    addComponent(lab = new Label("The currently active round is set through the Game Administrator menu"));
    lab.setSizeUndefined();
    setComponentAlignment(lab, Alignment.TOP_CENTER);

    tabSh.addTab(titlesPan, "Game Titles");
    tabSh.addTab(topCardsPan, "Top Card Types");
    tabSh.addTab(subCardsPan, "Sub Card Types");
    //tabSh.addTab(seedCardsPan, "Seed Card Initialization");
    tabSh.addTab(chaptersPan, "Action Plan Headings");
    addComponent(tabSh);

    titlesPan.initGui();
    topCardsPan.initGui();
    subCardsPan.initGui();
    //seedCardsPan.initGui();
    chaptersPan.initGui();
    moveSelector.setMove(Game.getTL().getCurrentMove());
  }
  
  @SuppressWarnings("serial")
  @MmowgliCodeEntry
  @HibernateOpened
  @HibernateClosed
  class NewMoveListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      @SuppressWarnings("unchecked")
           
      List<Move> lis = (List<Move>)HSess.get().createCriteria(Move.class).list();
      int largestNum = -1;
      Move largestMove = null;
      MovePhase largestMovePhase = null;
      for(Move m : lis) {
        if(m.getNumber()>largestNum) {
          largestNum = m.getNumber();
          largestMove = m;
          largestMovePhase = m.getCurrentMovePhase();
        }
      }
      if(largestMove == null || largestMovePhase == null) {
        HSess.close();
        throw new RuntimeException("Program error in GameDesignPanel.NewMoveListener");
      }

      largestNum++;

      Move m = new Move();
      m.setTitle(Game.getTL().getAcronym()+largestNum);
      m.setNumber(largestNum);
      m.setName("Round "+largestNum);

      List<MovePhase> arr = new ArrayList<MovePhase>();
      String[] descriptions = MovePhase.PhaseType.stringValues();
      for(int i=0;i<descriptions.length;i++) {
        MovePhase mp = new MovePhase();
        mp.cloneFrom(largestMovePhase);
        mp.setDescription(descriptions[i]);
        MovePhase.saveTL(mp);
        arr.add(mp);
      }
      m.setMovePhases(arr);
      m.setCurrentMovePhase(arr.get(0));
      Move.saveTL(m);

      moveSelector.newMoveTL(m);
      HSess.close();
      moveSelector.setMove(m);
    }
  }

  @SuppressWarnings("serial")
  class MoveSelectorListener implements Property.ValueChangeListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void valueChange(ValueChangeEvent event)
    {
      MWrap mw = (MWrap) event.getProperty().getValue();
      if (mw != null) {
        Object key = HSess.checkInit();
        Move mov = Move.mergeTL(mw.m);
        titlesPan.moveChangedTL(mov);
        topCardsPan.moveChangedTL(mov);
        subCardsPan.moveChangedTL(mov);
       // seedCardsPan.moveChangedTL(mov);
        chaptersPan.moveChangedTL(mov);

        if(externalListener != null)
          externalListener.moveChangedTL(mov);

        runningMoveWarningLabel.setVisible(AbstractGameBuilderPanel.isRunningMoveTL(mov));
        moveBeingEdited = mov;
        HSess.checkClose(key);
      }
    }
  }
}
  class PhasesEditPanel extends VerticalLayout implements MmowgliComponent, MoveChangeListener
  {
    private static final long serialVersionUID = 1L;

    TabSheet tabSh;
    List<Component> tabPanels = new ArrayList<Component>();

    PhaseTitlesGameDesignPanel titlePan;
    SignupHTMLGameDesignPanel signupPan;
    LoginSignupGameDesignPanel loginPan;
    WelcomeScreenGameDesignPanel welcomePan;
    CallToActionGameDesignPanel call2ActionPan;

    PhaseSelector phaseSelector;
    NativeButton newPhaseButt;

    Move moveBeingEdited;
    MovePhase phaseBeingEdited;
    Label runningPhaseWarningLabel;
    Label topLevelLabel;
    CheckBox propagateCB;

    @SuppressWarnings("serial")
    @HibernateSessionThreadLocalConstructor
    public PhasesEditPanel(Move move, GameDesignGlobals globs)
    {
      this.moveBeingEdited = move;
      setWidth("100%");
      setSpacing(true);
      phaseBeingEdited = moveBeingEdited.getCurrentMovePhase();
      tabSh = new TabSheet();

      tabPanels.add(titlePan = new PhaseTitlesGameDesignPanel(phaseBeingEdited, auxListener, globs));
      tabPanels.add(signupPan = new SignupHTMLGameDesignPanel(phaseBeingEdited, auxListener, globs));
      tabPanels.add(loginPan = new LoginSignupGameDesignPanel(phaseBeingEdited, auxListener, globs));
      tabPanels.add(welcomePan = new WelcomeScreenGameDesignPanel(phaseBeingEdited, auxListener, globs));
      tabPanels.add(call2ActionPan = new CallToActionGameDesignPanel(phaseBeingEdited, auxListener, globs));

      Label lab;
      addComponent(lab = new Label());
      lab.setHeight("5px");

      HorizontalLayout topHL = new HorizontalLayout();
      topHL.setSpacing(true);

      topHL.addComponent(lab = new Label());
      lab.setWidth("1px");
      topHL.setExpandRatio(lab, 0.5f);
      topHL.addComponent(topLevelLabel = new Label());
      setTopLabelText(moveBeingEdited);
      topLevelLabel.setSizeUndefined();
      topHL.addComponent(phaseSelector = new PhaseSelector(null, Move.getCurrentMoveTL()));
      phaseSelector.addValueChangeListener(new PhaseComboListener());

      topHL.addComponent(runningPhaseWarningLabel = new HtmlLabel("<font color='red'><i>Active game phase!</i></font>"));
      runningPhaseWarningLabel.setSizeUndefined();
      runningPhaseWarningLabel.setVisible(AbstractGameBuilderPanel.isRunningPhaseTL(phaseBeingEdited));

      topHL.addComponent(newPhaseButt = new NativeButton("Add new phase to round"));
      newPhaseButt.setEnabled(false);
      topHL.addComponent(lab = new Label());
      lab.setWidth("1px");
      topHL.setExpandRatio(lab, 0.5f);
      topHL.setWidth("100%");
      addComponent(topHL);

      propagateCB = new CheckBox("Propagate new phase-dependent edits to all other phases in this round");
      addComponent(propagateCB);
      setComponentAlignment(propagateCB,Alignment.MIDDLE_CENTER);
      propagateCB.setVisible(phaseBeingEdited.isPreparePhase());

      addComponent(lab = new HtmlLabel("<b>The currently running phase is set through the Game Administrator menu</b>"));
      lab.setSizeUndefined();
      setComponentAlignment(lab, Alignment.TOP_CENTER);

      newPhaseButt.addClickListener(new ClickListener()
      {
        @Override
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void buttonClick(ClickEvent event)
        {
          HSess.init();
          NewMovePhaseDialog dial = new NewMovePhaseDialog(moveBeingEdited);
          dial.addCloseListener(new CloseListener()
          {
            @Override
            public void windowClose(CloseEvent e)
            {
              Object key = HSess.checkInit();
              phaseSelector.fillCombo(moveBeingEdited);
              HSess.checkClose(key);
            }
          });
          dial.showDialog();
          HSess.close();
        }
      });
    }

    private void setTopLabelText(Move m)
    {
      topLevelLabel.setValue(m.getName()+" phase being edited:");
    }

    @Override
    public void initGui()
    {
      tabSh.setHeight("100%");
      tabSh.setWidth("930px");

      tabSh.addTab(titlePan, "Page Title & Prompt");
      tabSh.addTab(loginPan, "Login & Signup Labels");
      tabSh.addTab(signupPan, "Signup Page");
      tabSh.addTab(welcomePan, "Welcome Page");
      tabSh.addTab(call2ActionPan, "Call To Action Screen");

      addComponent(tabSh);

      titlePan.initGui();
      signupPan.initGui();
      loginPan.initGui();
      welcomePan.initGui();
      call2ActionPan.initGui();
    }

    @Override
    public void moveChangedTL(Move newMove)
    {
      moveBeingEdited = newMove;
      setTopLabelText(newMove);

      phaseSelector.fillCombo(newMove);
      MovePhase mp = newMove.getCurrentMovePhase();
      titlePan.movePhaseChanged(mp);
      signupPan.movePhaseChanged(mp);
      loginPan.movePhaseChanged(mp);
      welcomePan.movePhaseChanged(mp);
      call2ActionPan.movePhaseChanged(mp);
    }

    @SuppressWarnings("serial")
    class PhaseComboListener implements Property.ValueChangeListener
    {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        Object key = HSess.checkInit();
        PWrap pw = (PWrap) event.getProperty().getValue();
        if (pw != null) {
          titlePan.movePhaseChanged(pw.mp);
          signupPan.movePhaseChanged(pw.mp);
          loginPan.movePhaseChanged(pw.mp);
          welcomePan.movePhaseChanged(pw.mp);
          call2ActionPan.movePhaseChanged(pw.mp);
          phaseBeingEdited = pw.mp;
          propagateCB.setVisible(phaseBeingEdited.isPreparePhase());
          runningPhaseWarningLabel.setVisible(AbstractGameBuilderPanel.isRunningPhaseTL(phaseBeingEdited));
        }
        HSess.checkClose(key);
      }
    }

    /*package public*/AuxiliaryChangeListener auxListener = new AuxiliaryChangeListener()
    {
      @Override
      public void valueChange(IndivListener indLis, ValueChangeEvent event)
      {
        if(propagateCB.isVisible() && propagateCB.getValue())  {// means propagate
          AuxiliaryChangeListener lis = indLis.edLine.auxListener;
          indLis.edLine.auxListener = null; // prevent loop

          long alreadyDone = (Long) indLis.edLine.objId;
          List<MovePhase> phases = moveBeingEdited.getMovePhases();
          for(MovePhase ph : phases) {
            if(ph.getId() == alreadyDone)
              ;
            else {
              indLis.edLine.objId = ph.getId();
              indLis.valueChange(event);
            }
          }
          // restore
          indLis.edLine.objId = alreadyDone;
          indLis.edLine.auxListener = lis;
        }
      }
    };
  }

  class PhaseSelector extends NativeSelect
  {
    private static final long serialVersionUID = 1L;
    
    @HibernateSessionThreadLocalConstructor
    public PhaseSelector(String caption, Move initialMove)
    {
      super(caption);
      fillCombo(initialMove);
      setImmediate(true);
      setNullSelectionAllowed(false);
    }

    public void fillCombo(Move mm)
    {
      removeAllItems();
      mm = Move.mergeTL(mm);
      List<MovePhase> phases = mm.getMovePhases();
      MovePhase current = mm.getCurrentMovePhase();
      PWrap selected = null;
      for (MovePhase ph : phases) {
        PWrap pw;
        addItem(pw = new PWrap(ph));
        if (ph.getId() == current.getId())
          selected = pw;
      }
      setValue(selected);
    }
  }

  class PWrap
  {
    MovePhase mp;

    public PWrap(MovePhase mp)
    {
      this.mp = mp;
    }

    @Override
    public String toString()
    {
      return mp.getDescription();
    }
  }

  class MoveSelector extends NativeSelect
  {
    private static final long serialVersionUID = 1L;
    @HibernateSessionThreadLocalConstructor
    public MoveSelector(String caption)
    {
      super(caption);
      int i = 1;
      Move current = Move.getCurrentMoveTL();
      MWrap selected = null;
      do {
        Move m = Move.getMoveByNumberTL(i++);
        if (m == null)
          break;
        MWrap mw;
        addItem(mw = new MWrap(m));
        if (m.getNumber() == current.getNumber())
          selected = mw;
      }
      while (true);

      setImmediate(true);
      setNullSelectionAllowed(true);
      setValue(selected);
    }

    class MWrap
    {
      Move m;

      public MWrap(Move m)
      {
        this.m = m;
      }

      @Override
      public String toString()
      {
        return m.getName();
      }
    }

    public void setMove(Move m)
    {
      setNullSelectionAllowed(false);
      Collection<?> coll = this.getItemIds();
      for (Object obj : coll) {
        if (((MWrap) obj).m.getNumber() == m.getNumber()) {
          this.setValue(obj);
          break;
        }
      }
    }

    public void newMoveTL(Move newMove)
    {
      // Just rebuild
      MWrap sel = (MWrap) getValue();
      removeAllItems();
      int i = 1;
      do {
        Move m = Move.getMoveByNumberTL(i++);
        if (m == null)
          break;
        MWrap mw;
        addItem(mw = new MWrap(m));
        if (m.getNumber() == sel.m.getNumber())
          sel = mw;
      }
      while (true);

      setValue(sel);
    }
}

@SuppressWarnings("serial")
class NewMoveListener implements ClickListener
{
  @Override
  public void buttonClick(ClickEvent event)
  {
    /*
     * Move m = new Move(); MovePhase mp = new MovePhase(); fillMovePhase(mp);
     *
     * Media med = Media.getDefaultOrientationVideo(); if(med == null) { med = new Media( "", "", "", Media.MediaType.YOUTUBE); Media.save(med);
     * mp.setOrientationVideo(med); } med = Media.getDefaultCallToActionVideo(); if(med == null) { med = new Media( "", "", "", Media.MediaType.YOUTUBE);
     * Media.save(med); mp.setCallToActionBriefingVideo(med); }
     *
     * m.setCurrentPhase(Move.Phase.PRE); m.setInMove(mp); m.setPreMove(mp); m.setPostMove(mp); m.setNumber(nextMoveNumber++);
     * addButt.setEnabled(nextMoveNumber<=5); m.setName("Round "+m.getNumber()); m.setTitle("move title here"); MovePhase.save(mp); Move.save(m);
     *
     * int nComps = MoveEditPanel.this.getComponentCount(); // put in right before add button MoveLine ml=null; MoveEditPanel.this.addComponent(ml=new
     * MoveLine(m), nComps-1); moveLines.add(ml);
     *
     * tellOtherPanelsNewMove(m);
     */
  }
}
