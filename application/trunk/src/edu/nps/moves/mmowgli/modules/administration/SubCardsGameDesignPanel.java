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
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.Runo;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.CardType.DescendantCardType;
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
 * @version $Id$
 */
public class SubCardsGameDesignPanel extends VerticalLayout implements MmowgliComponent,MoveChangeListener
{
  private static final long serialVersionUID = 1693996606922590065L;
  
  private NativeSelect expandTypeSelect, counterTypeSelect, adaptTypeSelect, exploreTypeSelect;
  private CardTypeFields expandFields, counterFields, adaptFields, exploreFields;
  private Move moveBeingEdited, currentMove;

  private CardTypeListener myCardTypeListener = new CardTypeListener();
  private boolean commitCardType = true;
  private Label childCardsWarning;
  private GameDesignGlobals globs;
  
  @HibernateSessionThreadLocalConstructor
  public SubCardsGameDesignPanel(Move move, GameDesignGlobals globs)
  {
    this.moveBeingEdited = move;
    this.globs = globs;
    this.currentMove = Game.getTL().getCurrentMove();
  }
  
  @Override
  public void initGui()
  {
    setMargin(true);
    setSpacing(true);
    
    expandFields = new CardTypeFields(CardType.getExpandTypeTL(), globs);
    expandTypeSelect = new NativeSelect();
    addComponent(renderFields(expandFields, expandTypeSelect, "1 Choose EXPAND card type for this round"));
    expandFields.initGui();

    counterFields = new CardTypeFields(CardType.getCounterTypeTL(), globs);
    counterTypeSelect = new NativeSelect();
    addComponent(renderFields(counterFields, counterTypeSelect, "2 Choose COUNTER card type for this round"));
    counterFields.initGui();

    adaptFields = new CardTypeFields(CardType.getAdaptTypeTL(), globs);
    adaptTypeSelect = new NativeSelect();
    addComponent(renderFields(adaptFields, adaptTypeSelect, "3 Choose ADAPT card type for this round"));
    adaptFields.initGui();

    exploreFields = new CardTypeFields(CardType.getExploreTypeTL(), globs);
    exploreTypeSelect = new NativeSelect();
    addComponent(renderFields(exploreFields, exploreTypeSelect, "4 Choose EXPLORE card type for this round"));
    exploreFields.initGui();

    fillSelectCommon(expandTypeSelect, CardType.getDefinedExpandTypesTL());
    fillSelectCommon(counterTypeSelect, CardType.getDefinedCounterTypesTL());
    fillSelectCommon(adaptTypeSelect, CardType.getDefinedAdaptTypesTL());
    fillSelectCommon(exploreTypeSelect, CardType.getDefinedExploreTypesTL());
    
    expandTypeSelect.addValueChangeListener(myCardTypeListener);
    counterTypeSelect.addValueChangeListener(myCardTypeListener);
    adaptTypeSelect.addValueChangeListener(myCardTypeListener);
    exploreTypeSelect.addValueChangeListener(myCardTypeListener);
    
    addComponent(childCardsWarning=new Label("(Cannot change types at this point.)"), 0);
    adjustRO(); // will set above to (in)visible
  }
  
  private void adjustRO()
  {
    boolean locked = !isInMoveConstructionTL();
    boolean ro = globs.readOnlyCheck(locked);
    
    expandTypeSelect.setReadOnly(ro);
    expandFields.colorComp.setReadOnly(ro);
    counterTypeSelect.setReadOnly(ro);
    counterFields.colorComp.setReadOnly(ro);
    adaptTypeSelect.setReadOnly(ro);
    adaptFields.colorComp.setReadOnly(ro);
    exploreTypeSelect.setReadOnly(ro);
    exploreFields.colorComp.setReadOnly(ro);
    
    childCardsWarning.setVisible(ro); 
  }
  
  private boolean isPriorMove()
  {
    return moveBeingEdited.getNumber() < currentMove.getNumber();
  }
  
  private boolean isThisMove()
  {
    return moveBeingEdited.getNumber() == currentMove.getNumber();
  }

  private boolean isInMoveConstructionTL()
  {
    if (isPriorMove())
      return false; // previous move
    if (!isThisMove())
      return true; // future move

    // Here if we're editing the running move. Report if we detect a significant number of child cards played in this move so the
    // designer can be warned

    Criteria criteria = HSess.get().createCriteria(Card.class).add(Restrictions.eq("createdInMove", moveBeingEdited))
        .add(Restrictions.isNotNull("parentCard")).setProjection(Projections.rowCount());

    int count = ((Long) criteria.list().get(0)).intValue();
    return count < 10;
  }

  @SuppressWarnings("serial")
  class NewTypeListener implements ClickListener
  {
    int ctyp;
    String title;
    NewTypeListener(int ctyp)
    {
      this.ctyp = ctyp;
      if(ctyp == CardType.EXPAND_CARD_TYPE)
        title = "New Expand Card Type";
      else if(ctyp == CardType.EXPLORE_CARD_TYPE)
        title = "New Explore Card Type";
      else if(ctyp == CardType.COUNTER_CARD_TYPE)
        title = "New Counter Card Type";
      else //if(ctyp == CardType.ADAPT_CARD_TYPE)
        title = "New Adapt Card Type";
    }
    
    @Override
    public void buttonClick(ClickEvent event)
    {
      Window win=new AddCardTypeDialog(ctyp,title);     
      win.addCloseListener(new CloseListener()
      {
        @Override
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void windowClose(CloseEvent e)
        {
          HSess.init();
          if(ctyp == CardType.EXPAND_CARD_TYPE) {
            fillSelectCommon(expandTypeSelect, CardType.getDefinedExpandTypesTL());            
          }
          else if(ctyp == CardType.COUNTER_CARD_TYPE) {
            fillSelectCommon(counterTypeSelect, CardType.getDefinedCounterTypesTL());            
          }
          else if(ctyp == CardType.ADAPT_CARD_TYPE) {
            fillSelectCommon(adaptTypeSelect, CardType.getDefinedAdaptTypesTL());            
          }
          else { //if(ctyp == CardType.EXPLORE_CARD_TYPE) {
            fillSelectCommon(exploreTypeSelect, CardType.getDefinedExploreTypesTL());            
          }
          HSess.close();
        }        
      });
      UI.getCurrent().addWindow(win);
      win.center();
    }    
  }
  
  private Component renderFields(CardTypeFields fields, NativeSelect combo, String name)
  {
    VerticalLayout topPan = new VerticalLayout();
    topPan.setWidth("98%");
    topPan.addStyleName("m-greyborder3");
    Label lab;
    topPan.addComponent(lab = new Label());
    lab.setHeight("18px");

    HorizontalLayout topHL = new HorizontalLayout();
    topHL.setSpacing(true);
    ;
    topHL.addComponent(lab = new Label());
    lab.setWidth("1px");
    topHL.setExpandRatio(lab, 0.5f);
    topHL.addComponent(lab = new HtmlLabel("<b>" + name + "</b>"));
    lab.setSizeUndefined();

    topHL.addComponent(combo);

    Button newTypeButt;
    topHL.addComponent(newTypeButt = new NativeButton("Define new type"));
    newTypeButt.addStyleName(Runo.BUTTON_SMALL);

    newTypeButt.setReadOnly(globs.readOnlyCheck(false));
    newTypeButt.setEnabled(!newTypeButt.isReadOnly());
    if(!newTypeButt.isReadOnly())
      newTypeButt.addClickListener(new NewTypeListener(fields.typeOrdinal));
  
    topHL.addComponent(lab = new Label());
    lab.setWidth("1px");
    topHL.setExpandRatio(lab, 0.5f);

    topPan.addComponent(topHL);
    topHL.setWidth("100%");

    topPan.addComponent(fields);
    fields.setWidth("100%");
    return topPan;
  }

  public TypeLine setTypeLine(CardType ct, NativeSelect combo)
  {
    Collection<?> coll = combo.getContainerPropertyIds();
    for (Object o : coll) {
      TypeLine tl = (TypeLine) o;
      if (tl.typ.getId() == ct.getId())
        return tl;
    }
    return null;
  }

  private void saveCardTypeTL(CardType ct, CardType.DescendantCardType ordinal, ConfirmDialog.Listener backOutListener)
  {
    ConfirmListener lis = new ConfirmListener(ordinal,ct,backOutListener);
    int count= getCardTypeCountFromThisMoveTL(moveBeingEdited,ct.getDescendantOrdinal());
    if(count > 0) {
      ConfirmDialog.show(UI.getCurrent(), "Card Type Change","Changing a card type's descriptive text for a round will change all "+count+" existing cards of the former type, which may not be what you intend"+
      " since the cards have been played based on different text.  "+"Is this what you want to do?", "Yes", "Cancel", lis);          
    } 
    else
      lis.confirmedTL();
  }
  
  @SuppressWarnings("serial")
  class ConfirmListener implements ConfirmDialog.Listener
  {
    ConfirmDialog.Listener whoToCallIfTheyChangeTheirMind;
    CardType ct;
    DescendantCardType dct;
    ConfirmListener(DescendantCardType dct, CardType ct, ConfirmDialog.Listener cancelLis)
    {
      whoToCallIfTheyChangeTheirMind = cancelLis;
      this.dct = dct;
      this.ct = ct;      
    }
    
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void onClose(ConfirmDialog dialog)
    {
      if (dialog.isConfirmed()) {
        HSess.init();
        confirmedTL();
        HSess.close();
      }
      else {
        if (whoToCallIfTheyChangeTheirMind != null)
          whoToCallIfTheyChangeTheirMind.onClose(dialog);
      }
    } 
    
    public void confirmedTL()
    {
      Move m = Move.mergeTL(moveBeingEdited);
      ct = CardType.mergeTL(ct);
      switch(dct) {
      case EXPAND:
        CardType.setExpandCardTypeAllPhasesTL(m, ct);
        break;
      case COUNTER:
        CardType.setCounterCardTypeAllPhasesTL(m, ct);
        break;
      case ADAPT:
        CardType.setAdaptCardTypeAllPhasesTL(m, ct);
        break;
      case EXPLORE:
        CardType.setExploreCardTypeAllPhasesTL(m, ct);
        break;
      }
      changeCardTypesTL(m,ct);      
    }
  }
  
  private int getCardTypeCountFromThisMoveTL(Move m, int ordinal)
  {
    m = Move.mergeTL(m);
    Criteria criteria = makeCardClassQueryTL(m,ordinal);
    criteria.setProjection(Projections.rowCount());
    return ((Long) criteria.list().get(0)).intValue();
  }
 
  @SuppressWarnings("unchecked")
  private void changeCardTypesTL(Move m, CardType typ)
  {
    Criteria criteria = makeCardClassQueryTL(m,typ.getDescendantOrdinal());
    List<Card> lis = criteria.list(); 
    for(Card c : lis) {
      c.setCardType(typ);
      Card.updateTL(c);
    }   
  }
  
  private Criteria makeCardClassQueryTL(Move m, int ordinal)
  {
    return HSess.get().createCriteria(Card.class)
        .createAlias("createdInMove", "MOVE")
        .createAlias("cardType", "CARDTYPE")
        .add(Restrictions.eq("MOVE.number", m.getNumber()))
        .add(Restrictions.eq("CARDTYPE.descendantOrdinal", ordinal));    
  }
   
  @Override
  public void moveChangedTL(Move m)
  {
    moveBeingEdited = m;
    boolean oldFlag = commitCardType;
    commitCardType = false;

    adjustRO();
    
    CardType ct = CardType.getExpandType(moveBeingEdited);
    changeTypeCombo(expandTypeSelect, ct);
    expandFields.cardTypeChanged(ct);

    ct = CardType.getCounterType(moveBeingEdited);
    changeTypeCombo(counterTypeSelect, ct);
    counterFields.cardTypeChanged(ct);

    ct = CardType.getAdaptType(moveBeingEdited);
    changeTypeCombo(adaptTypeSelect, ct);
    adaptFields.cardTypeChanged(ct);

    ct = CardType.getExploreType(moveBeingEdited);
    changeTypeCombo(exploreTypeSelect, ct);
    exploreFields.cardTypeChanged(ct);
    
    commitCardType = oldFlag; 
  }
  
  @SuppressWarnings("serial")
  class CardTypeCancelListener implements ConfirmDialog.Listener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void onClose(ConfirmDialog arg0)
    {
      HSess.init();
      boolean oldcommitflag = commitCardType;
      commitCardType = false;
      moveChangedTL(moveBeingEdited); // this will do it 
      commitCardType = oldcommitflag;
      HSess.close();
    }    
  }
  
  @SuppressWarnings("serial")
  class CardTypeListener implements ValueChangeListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void valueChange(ValueChangeEvent event)
    { 
      NativeSelect combo = (NativeSelect) event.getProperty();
      TypeLine tLine = (TypeLine) combo.getValue();
      if(tLine == null)
        return;
      
      HSess.init();
      tLine.typ = CardType.mergeTL(tLine.typ);
      CardTypeFields fields = null;
      CardType.DescendantCardType dct = null;
      
      if (combo == expandTypeSelect) {
        fields = expandFields;
        dct = CardType.DescendantCardType.EXPAND;
      }
      else if (combo == counterTypeSelect) {
        fields = counterFields;
        dct = CardType.DescendantCardType.COUNTER;
      }
      else if (combo == adaptTypeSelect) {
        fields = adaptFields;
        dct = CardType.DescendantCardType.ADAPT;
      }
      else { // if(combo = exploreTypeSelect) {
        fields = exploreFields;
        dct = CardType.DescendantCardType.EXPLORE;
      }
      
      if (commitCardType)
        saveCardTypeTL(tLine.typ, dct, new CardTypeCancelListener());//saveExploreCardType(tLine.typ);

      fields.titleTA.setReadOnly(false);
      fields.titleTA.setValue(tLine.typ.getTitle());
      fields.titleTA.setReadOnly(true);
      fields.summaryTA.setReadOnly(false);
      fields.summaryTA.setValue(tLine.typ.getSummaryHeader());
      fields.summaryTA.setReadOnly(true);
      fields.promptTA.setReadOnly(false);
      fields.promptTA.setValue(tLine.typ.getPrompt());
      fields.promptTA.setReadOnly(true);
      
      boolean ro = fields.colorComp.isReadOnly();
      fields.colorComp.setReadOnly(false);
      fields.colorComp.changeCardType(tLine.typ);
      fields.colorComp.setReadOnly(ro);

      HSess.close();
    }
  }

  @SuppressWarnings("serial")
  class TypeLine extends Label
  {
    private CardType typ;

    public TypeLine(CardType typ)
    {
      super(typ.getTitle() + "/" + typ.getSummaryHeader());
      this.typ = typ;
    }

    public CardType getCardType()
    {
      return typ;
    }

    @Override
    public String toString()
    {
      return typ.getTitle() + "/" + typ.getSummaryHeader();
    }
  }

  private void changeTypeCombo(NativeSelect combo, CardType ct)
  {
    Collection<?> ids = combo.getItemIds();
    for (Object o : ids) {
      if (((TypeLine) o).typ.getId() == ct.getId()) {
        ((TypeLine)o).typ = ct; // prevent stale objects
        boolean ro = combo.isReadOnly();
        combo.setReadOnly(false);
        combo.select(o);
        combo.setReadOnly(ro);
        return;
      }
    }
  }

  private void fillSelectCommon(NativeSelect combo, List<CardType> lis)
  {
    boolean oldCommit = commitCardType;
    commitCardType = false;
    
    TypeLine selectedTL = (TypeLine)combo.getValue();
    TypeLine typeLFirst = null;
    boolean ro = combo.isReadOnly();
    combo.setReadOnly(false);
    combo.removeAllItems();

    for (CardType ct : lis) {
      TypeLine tl = new TypeLine(ct);
      if (typeLFirst == null)
        typeLFirst = tl;
      if(selectedTL != null && selectedTL.typ.getId() == ct.getId())
        selectedTL = tl;
        
      combo.addItem(tl);
    }

    combo.setNullSelectionAllowed(false);
    combo.setImmediate(true);

    if(selectedTL != null)
      combo.select(selectedTL);
    else if (typeLFirst != null)
      combo.select(typeLFirst);
    
    combo.setReadOnly(ro);
    commitCardType = oldCommit;
  }

  class CardTypeFields extends AbstractGameBuilderPanel implements CardTypeChangeListener
  {
    public TextArea titleTA, summaryTA, promptTA;
    private static final long serialVersionUID = 1L;
    public int typeOrdinal;
    CardColorChooserComponent colorComp;
    
    public CardTypeFields(CardType typ, GameDesignGlobals globs)
    {
      super(false, false, globs);
      typeOrdinal = typ.getDescendantOrdinal();

      String posTitle = typ.getTitle();
      String posPrompt = typ.getPrompt();
      String posSummHdr = typ.getSummaryHeader();

      titleTA = (TextArea) addEditLine("Card title", "CardType.title", typ, typ.getId(), "Title").ta;
      titleTA.setReadOnly(false);
      titleTA.setValue(posTitle);
      titleTA.setRows(1);
      titleTA.setReadOnly(true);
      titleTA.addStyleName("m-textarea-greyborder");

      summaryTA = (TextArea) addEditLine("Card summary header", "CardType.summaryHeader", typ, typ.getId(), "SummaryHeader").ta;
      summaryTA.setReadOnly(false);
      summaryTA.setValue(posSummHdr);
      summaryTA.setRows(1);
      summaryTA.setReadOnly(true);
      summaryTA.addStyleName("m-textarea-greyborder");

      promptTA = (TextArea) addEditLine("Card prompt", "CardType.prompt", typ, typ.getId(), "Prompt").ta;
      promptTA.setReadOnly(false);
      promptTA.setValue(posPrompt);
      promptTA.setRows(2);
      promptTA.setReadOnly(true);
      promptTA.addStyleName("m-textarea-greyborder");
      
      addEditComponent("Card color","CardType.cssColorStyle",colorComp=new CardColorChooserComponent(null));
      colorComp.setReadOnly(false);
      colorComp.changeCardType(typ);
      colorComp.setReadOnly(true);
    }

    @Override
    Embedded getImage()
    {
      return null;
    }

    @Override
    protected int getColumn1PixelWidth()
    {
      return super.getColumn1PixelWidth() + 70; // default = 80
    }

    @Override
    protected int getColumn2PixelWidth()
    {
      return super.getColumn2PixelWidth() - 50; // default = 240
    }

    @Override
    public void cardTypeChanged(CardType ct)
    {
      okToUpdateDbFlag = false; 
      changeCardType(ct); 
      colorComp.changeCardType(ct);
      okToUpdateDbFlag = true;      
    }
  }
}
