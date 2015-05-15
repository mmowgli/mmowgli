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

import java.util.Collection;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;

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
 * @version $Id: ScoringGameDesignPanel.java 3276 2014-01-14 23:55:51Z tdnorbra $
 */
public class ScoringGameDesignPanel extends AbstractGameBuilderPanel
{
  private static final long serialVersionUID = 3971486176821026871L;

  @HibernateSessionThreadLocalConstructor
  public ScoringGameDesignPanel(GameDesignGlobals globs)
  {
    super(false,globs);
    Game g = Game.getTL();
    TextArea ta = (TextArea)addEditLine("Card authorship points", "Game.cardAuthorPoints", g, 1L, "CardAuthorPoints",null,Float.class,
                                        "Points for the author on card creation").ta;
    ta.setValue(""+g.getCardAuthorPoints());
    setupFloatValueListener(ta);
    ta.setRows(1);

    ta = (TextArea)addEditLine("Card ancestor points", "Game.cardAncestorPoints", g, 1L, "CardAncestorPoints",null,Float.class,
                               "Point bonus (adjusted by factor below) for card parents").ta;
    ta.setValue(""+g.getCardAncestorPoints());
    setupFloatValueListener(ta);
    ta.setRows(1);

    ta = (TextArea)addEditLine("Card ancestor generation factors", "Game.cardAncestorPointsGenerationFactors", g, 1L, "CardAncestorPointsGenerationFactors",null,String.class,
                               "Factors multiplied by ancestor points to calculate ancestor card points").ta;
    ta.setValue(g.getCardAncestorPointsGenerationFactors());
    setupFactorsListener(ta);
    ta.setRows(1);
/*
    addEditBoolean("Earliest ancestor highest reward", "Game.cardAncestorEarlyPointsBias", g, 1L, "CardAncestorEarlyPointsBias",
        "Default is ON, meaning the ancestor increment factor favors the author of earliest card instead of the nearest ancestor card");
*/
    addSeparator();

    ta = (TextArea)addEditLine("Card super-interesting bonus points", "Game.cardSuperInterestingPoints", g, 1L, "CardSuperInterestingPoints",null,Float.class,
                               "Points added to card author when card is marked super-interesting (points removed if card unmarked)").ta;
    ta.setValue(""+g.getCardSuperInterestingPoints());
    setupFloatValueListener(ta);
    ta.setRows(1);

      if (Game.getTL().isActionPlansEnabled()) {
          addSeparator();

          ta = (TextArea) addEditLine("Action plan authorship points", "Game.actionPlanAuthorPoints", g, 1L, "ActionPlanAuthorPoints", null, Float.class,
                  "Points awarded to players who accept action plan authorship invitations").ta;
          ta.setValue(""+g.getActionPlanAuthorPoints());
          setupFloatValueListener(ta);
          ta.setRows(1);

//    ta = (TextArea)addEditLine("Action plan thumb points", "Game.actionPlanThumbPoints", g, 1L, "ActionPlanThumbPoints",null,Float.class,
//        "Point bonus (adusted by factor) based on ").ta;
//    ta.setValue(g.getActionPlanThumbPoints());
//    ta.setRows(1);
//
          ta = (TextArea) addEditLine("Action plan thumb factor", "Game.actionPlanThumbFactor", g, 1L, "ActionPlanThumbFactor", null, Float.class,
                  "A point bonus for each plan author is calulated by the product of this factor and total user thumb ratings").ta;
          ta.setValue(""+g.getActionPlanThumbFactor());
          setupFloatValueListener(ta);
          ta.setRows(1);

          ta = (TextArea) addEditLine("Action plan comment points", "Game.actionPlanCommentPoints", g, 1L, "ActionPlanCommentPoints", null, Float.class,
                  "Point bonus for each plan author when a non-author comment is entered").ta;
          ta.setValue(""+g.getActionPlanCommentPoints());
          setupFloatValueListener(ta);
          ta.setRows(1);

          ta = (TextArea) addEditLine("Action plan rater points", "Game.actionPlanRaterPoints", g, 1L, "ActionPlanRaterPoints", null, Float.class,
                  "Point bonus to player for first rating an action plan -- removed if unrated").ta;
          ta.setValue(""+g.getActionPlanRaterPoints());
          setupFloatValueListener(ta);
          ta.setRows(1);

          ta = (TextArea) addEditLine("Action plan commenter points", "Game.userActionPlanCommentPoints", g, 1L, "UserActionPlanCommentPoints", null, Float.class,
                  "Point bonus for commenter when posting a comment to an action plan.").ta;
          ta.setValue(""+g.getUserActionPlanCommentPoints());
          setupFloatValueListener(ta);
          ta.setRows(1);
      }
/*
    addSeparator();

    ta = (TextArea)addEditLine("Action plan super-interesting bonus points", "Game.cardSuperInterestingPoints", g, 1L, "CardSuperInterestingPoints",null,Float.class,
        "Point bonus for each plan author when a plan is marked \"super-interesting\" by a game-master (points removed if plan unmarked)").ta;
    ta.setValue(g.getCardSuperInterestingPoints());
    setupFloatValueListener(ta);
    ta.setRows(1);
*/
    addSeparator();

    ta = (TextArea)addEditLine("New user bonus for answering registration question", "Game.userSignupAnswerPoints", g, 1L, "UserSignupAnswerPoints",null,Float.class,
        "Point bonus if a new user chooses to answer the optional question posed during the registration process").ta;
    ta.setValue(""+g.getUserSignupAnswerPoints());
    setupFloatValueListener(ta);
    ta.setRows(1);
  }

  private void setupFloatValueListener(TextArea ta)
  {
    @SuppressWarnings("unchecked")
    Collection<ValueChangeListener> listers = (Collection<ValueChangeListener>)ta.getListeners(ValueChangeEvent.class);
    for(ValueChangeListener lis : listers) {
      ta.removeValueChangeListener(lis);
    }
    ta.addValueChangeListener(new StringToFloatListener(listers));
  }

  @SuppressWarnings("unchecked")
  private void setupFactorsListener(TextArea ta)
  {
    Collection<ValueChangeListener> coll = (Collection<ValueChangeListener>)ta.getListeners(ValueChangeEvent.class);
    for(ValueChangeListener lis : coll)
      ta.removeValueChangeListener(lis);
    ta.addValueChangeListener(new FactorsListener(coll));
  }

  @SuppressWarnings("serial")
  class FactorsListener implements ValueChangeListener
  {
    Collection<ValueChangeListener> coll;
    FactorsListener(Collection<ValueChangeListener> coll)
    {
      this.coll = coll;
    }
    @Override
    public void valueChange(ValueChangeEvent event)
    {
      if(validateFactors(event))
        propagate(event);
      else
        new Notification("Data error.&nbsp;&nbsp;", "Your entry must be a space-separated list of numbers, or be empty, signifying<br/>no ancestor points awarded."+
                                                   "  The change you entered has not been saved.", Notification.Type.ERROR_MESSAGE,true).show(Page.getCurrent());
    }

    private void propagate(ValueChangeEvent event)
    {
      for(ValueChangeListener lis : coll)
        lis.valueChange(event);
    }

    private boolean validateFactors(ValueChangeEvent event)
    {
      String val = (String) event.getProperty().getValue();
      val = val.trim();
      if(val.length() > 0){
        String[] sa = val.split("\\s+");
        if (sa.length > 0) {
          for (String s : sa) {
            try {
             Float.parseFloat(s);
            }
            catch (Throwable t) {
              return false;
            }
          }
        }
      }
      return true;
    }
  }

  @SuppressWarnings("serial")
  class StringToFloatListener implements ValueChangeListener
  {
    private Collection<ValueChangeListener> olderListeners;

    StringToFloatListener(Collection<ValueChangeListener> olderListeners)
    {
      this.olderListeners = olderListeners;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void valueChange(ValueChangeEvent event)
    {
      if(olderListeners != null) {
        Float f = 0.0f;
        try {
          f = Float.parseFloat(event.getProperty().getValue().toString());
          event.getProperty().setValue(f);
          for(ValueChangeListener lis : olderListeners)
            lis.valueChange(event);
        }
        catch(Throwable t) {
          new Notification("Data error.&nbsp;&nbsp;", "Your entry must be a decimal number.<br/>"+
              "The change you entered has not been saved.", Notification.Type.ERROR_MESSAGE,true).show(Page.getCurrent());
       }
      }
    }
  }

  @Override
  Embedded getImage()
  {
    return null;
  }

  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 250; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() + 10; // default = 240
  }

  @Override
  protected String getHeading()
  {
    return "Changing scoring variables during a running game may confuse players.";
  }

  @Override
  protected Component getFooter()
  {
    HorizontalLayout hlay = new HorizontalLayout();
    Label sp;
    hlay.addComponent(sp=new Label());
    sp.setWidth("1px");
    hlay.setExpandRatio(sp, 0.5f);
    Button butt = new Button("Click for scoring help and examples", new HelpListener());
    hlay.addComponent(butt);
    hlay.addComponent(sp=new Label());
    sp.setWidth("1px");
    hlay.setExpandRatio(sp, 0.5f);

    hlay.setWidth("100%");
    return hlay;
  }

  @SuppressWarnings("serial")
  class HelpListener implements ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      ScoringHelpWindow.showTL(Game.getTL());
      HSess.close();
    }
  }
}
