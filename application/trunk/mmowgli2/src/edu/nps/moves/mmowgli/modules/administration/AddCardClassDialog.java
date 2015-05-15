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

import java.util.Iterator;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.db.CardType.CardClass;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;

/**
 * AddCardTypeDialog.java
 * Created on Mar 22, 2013
 * Updated on Mar 12, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AddCardClassDialog extends Window
{
  private static final long serialVersionUID = 2007806287075524580L;
  
  CardClass cls;
  TextField titleTF,summTF,promptTF;
  NativeSelect colorCombo;
  Button cancelButt, saveButt;
  
  public AddCardClassDialog(CardClass cls, String title)
  {
    super(title);
    this.cls = cls;
    
    VerticalLayout vl = new VerticalLayout();
    setContent(vl);
    vl.setSpacing(true);
    vl.setMargin(true);
    vl.setSizeUndefined();
   
    vl.addComponent(titleTF = new TextField("Title"));
    titleTF.setValue("title goes here");
    titleTF.setColumns(35);
    
    vl.addComponent(summTF = new TextField("Summary"));
    summTF.setValue("summary goes here");
    summTF.setColumns(35);
    
    vl.addComponent(promptTF = new TextField("Prompt"));
    promptTF.setValue("prompt goes here");
    promptTF.setColumns(35);
    
    vl.addComponent(colorCombo = new NativeSelect("Color"));
    fillCombo();
    
    HorizontalLayout buttHL = new HorizontalLayout();
    buttHL.setSpacing(true);
    buttHL.addComponent(cancelButt = new Button("Cancel"));
    cancelButt.addClickListener(new CancelListener());
    buttHL.addComponent(saveButt = new Button("Save"));
    saveButt.addClickListener(new SaveListener());
    vl.addComponent(buttHL);
    vl.setComponentAlignment(buttHL, Alignment.MIDDLE_RIGHT);   
  }
  
  @SuppressWarnings("serial")
  class CancelListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      UI.getCurrent().removeWindow(AddCardClassDialog.this);
    }    
  }
  
  private void fillCombo()
  {
    Set<String> styles = CardStyler.getCardStyles();
    // Creates the options container and add given options to it
    final Container c = new IndexedContainer();
    String first=null;
    for (final Iterator<String> i = styles.iterator(); i.hasNext();) {
      String s = i.next();
      if(first==null)
        first = s;
      c.addItem(s);
    }
    colorCombo.setContainerDataSource(c);
    colorCombo.select(first);
    colorCombo.setNullSelectionAllowed(false);
  }
  
  @SuppressWarnings("serial")
  class SaveListener implements ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      
      String title = titleTF.getValue().toString();
      title = (title==null || title.length()<=0)?"":title;
      String summ = summTF.getValue().toString();
      summ = (summ==null || summ.length()<=0)?"":summ;
      String prompt = promptTF.getValue().toString();
      prompt = (prompt==null || prompt.length()<=0)?"":prompt;
      CardType ct = new CardType(title,"",false,prompt,summ);
      ct.setCardClass(cls);
      ct.setDescendantOrdinal(null);
      ct.setIdeaCard(true);
      ct.setCssColorStyle(colorCombo.getValue().toString());
      HSess.get().save(ct);
      
      UI.getCurrent().removeWindow(AddCardClassDialog.this);
      HSess.close();
    }    
  }
}
