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

import static edu.nps.moves.mmowgli.MmowgliEvent.CARDCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.SHOWUSERPROFILECLICK;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.Vector;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.data.hbnutil.HbnContainer.EntityItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.AppEvent;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliController;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.utility.IDButton;

/**
 * CardTable.java
 * Created on Mar 16, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardTable extends Table implements ItemClickListener
{
  private static final long serialVersionUID = 7139821641900322453L;
  
  private HbnContainer<Card> container;
  private SimpleDateFormat dateForm;
  private MyColumnCustomizer columnCustomizer;
  private boolean showAuthor,showIsRoot,showFollowOns;
  private String GENAUTHOR = "genauthor";
  private String GENDATE = "gendate";
  private String GENCARDTYPE = "gencardtype";
  private String GENTEXT = "gentext";
  private String ID = "id";
  
  public static CardTable makeFullTable(String caption, HbnContainer<Card> container)
  {
    return new CardTable(caption,container);
  }
  
  public CardTable(String caption, HbnContainer<Card> cntr)
  {
    this(caption,cntr,false,true,true);
  }
  public CardTable(String caption, HbnContainer<Card> cntr, boolean showAuthor, boolean showIsRoot, boolean showFollowOns)
  {
    super(caption);
    this.showAuthor = showAuthor;
    this.showIsRoot = showIsRoot;
    this.showFollowOns = showFollowOns;
    
    if(cntr == null) {
      this.container = Card.getContainer();
    }
    else
      this.container = cntr;   
    dateForm = new SimpleDateFormat("MM/dd HH:mm"); // z");

    setSelectable(true);
    setMultiSelect(false);
    setImmediate(true); // remove if not necessary
    setContainerDataSource(container);
    addItemClickListener((ItemClickListener)this);
    
    addStyleName("m-userprofile-table");
    
    // Special column renderers
    columnCustomizer = new MyColumnCustomizer();
    addAllGeneratedColumns();
    
    privateSetVisibleColumns();
    privateSetColumnHeaders();
    
    setAllColumnWidths();
    
    setPageLength(20);  // This makes some cards show up right away
    setCacheRate(0.0d); //.1d);
  }
  
  @Override
  public String getColumnHeader(Object property)
  {
    String defaultHdr = super.getColumnHeader(property);
    if (property == GENDATE)
      return "<span title='Date & time posted'>" + defaultHdr + "</span>";
    if (property == GENAUTHOR)
      return "<span title='View author user profile'>" + defaultHdr + "</span>";
    if (property == GENCARDTYPE)
      return "<span title='Type of card played'>" + defaultHdr + "</span>";
    if (property == GENTEXT)
      return "<span title='What&#39;s the big idea?'>" + defaultHdr + "</span>";
    if (property == ID)
      return "<span title='Card ID number'>" + defaultHdr + "</span>";

    return defaultHdr;
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  @MmowgliCodeEntry
  @HibernateOpened 
  public void itemClick(ItemClickEvent event)
  {
    HSess.init();
    //if (event.isDoubleClick()) {
    Object cellId = event.getPropertyId();
    EntityItem item = (EntityItem) event.getItem();
    Card card = (Card) ((EntityItem) item).getPojo();  
    MmowgliController cntlr = Mmowgli2UI.getGlobals().getController();
    if(cellId != null && GENAUTHOR.equals(cellId))
      cntlr.miscEventTL(new AppEvent(SHOWUSERPROFILECLICK, this, card.getAuthor().getId()));
    else
      cntlr.miscEventTL(new AppEvent(CARDCLICK, this, card.getId()));
   // }
    HSess.close(); //commit
  }

  private void privateSetColumnHeaders()
  {
    Vector<String> v = new Vector<String>();
    v.add("ID");
    v.add("Creation");
    v.add("Type");
    if(showAuthor)
      v.add("Author");
    if(showIsRoot)
      v.add("Root");
    if(showFollowOns)
      v.add("Follow ons");
    v.add("Text");
    String[] sa = new String[]{};
    sa = v.toArray(sa);
    setColumnHeaders(sa);
  }
  private void privateSetVisibleColumns()
  {
    Vector<String> v = new Vector<String>();
    v.add(ID);
    v.add(GENDATE);
    v.add(GENCARDTYPE);
    if(showAuthor)
      v.add(GENAUTHOR);
    if(showIsRoot)
      v.add("chainroot");
    if(showFollowOns)
      v.add("followons");
    v.add(GENTEXT);
    setVisibleColumns(v.toArray());
    
  }
  protected void setAllColumnWidths()
  {
    setColumnWidth(ID, 40);
    setColumnWidth(GENDATE, 80);
    setColumnWidth(GENCARDTYPE, 155);
    
    if(showIsRoot)
      setColumnWidth("chainroot",40);
    if(showFollowOns)
      setColumnWidth("followons", 95); 
    if(showAuthor)
      setColumnWidth(GENAUTHOR,100);
  }
  
  protected void addAllGeneratedColumns()
  {
    addGeneratedColumn(ID, columnCustomizer);
    
    if(showIsRoot)
      addGeneratedColumn("chainroot",   columnCustomizer);
    if(showFollowOns)
      addGeneratedColumn("followons",   columnCustomizer);
    if(showAuthor)
      addGeneratedColumn("genauthor", columnCustomizer);
    
    addGeneratedColumn(GENCARDTYPE, columnCustomizer);
    addGeneratedColumn(GENDATE,     columnCustomizer);
    addGeneratedColumn(GENTEXT,     columnCustomizer); 
  }
  
  class MyColumnCustomizer implements Table.ColumnGenerator
  {
    private static final long serialVersionUID = 1938821794468835620L;

    @Override
    public Component generateCell(Table table, Object itemId, Object columnId)
    {
      @SuppressWarnings("rawtypes")
      EntityItem ei = (EntityItem)table.getItem(itemId);
      Card card = (Card)ei.getPojo();

      if(ID.equals(columnId)) {
        if(card.isHidden()) {
          Label lab = new HtmlLabel(""+card.getId()+"<span style='color:red'>(H)</span>");
          lab.setDescription("hidden");
          return lab;
        }
        else
          return new Label(""+card.getId());
      }
      if("chainroot".equals(columnId)) {
        boolean tf = card.getParentCard()==null;
        return new Label(tf?"yes":"");
      }
      if("followons".equals(columnId)) {
        Set<Card> set = card.getFollowOns();
        if(set != null && set.size()<=0)
          set = null;
        return new Label((set!= null)?""+set.size():"");
      }
      if(GENCARDTYPE.equals(columnId)) {
        CardType ct = card.getCardType();
        HorizontalLayout hl = new HorizontalLayout();
        hl.setMargin(false);
        hl.setSpacing(true);
        Embedded emb  = new Embedded(null,Mmowgli2UI.getGlobals().mediaLocator().getCardDot(ct));
        emb.setWidth("19px");
        emb.setHeight("15px");
        hl.addComponent(emb);
        hl.addComponent(new Label((ct==null)?"":ct.getTitle()));
        return hl;
      }
      if(GENAUTHOR.equals(columnId)) {
        return buildAuthorNameColumn(card.getAuthorName());
      }
      if(GENDATE.equals(columnId)) {
        Label lab = new Label(dateForm.format(card.getCreationDate()));
        lab.setSizeUndefined();
        return lab;
      }
      if(GENTEXT.equals(columnId)) {
        Label lab = new Label(card.getText());
        lab.addStyleName("m-nowrap");  // has no effect
        lab.setDescription(card.getText()); // tooltip
        return lab;
      }
      return new Label("Program error in UserProfileMyIdeasPanel.java");
    }   
  }

  /* Doesn't scale well */
  @SuppressWarnings("unused")
  private Component orig_buildGenAuthorColumn(Card card)
  {    
    // IE7 can't handle fanciness:
    if (Mmowgli2UI.getGlobals().isIE7()) {
      return new Label(card.getAuthorName()); //.getAuthor().getUserName());
    }
    
    HorizontalLayout hl = new HorizontalLayout();
    hl.setMargin(false);
    hl.setSpacing(true);

    User auth = card.getAuthor();

    if (auth.getAvatar() != null) {
      Embedded avatar = new Embedded(null, Mmowgli2UI.getGlobals().getMediaLocator().locate(auth.getAvatar().getMedia(), 32));
      avatar.setWidth("24px");
      avatar.setHeight("24px");
      hl.addComponent(avatar);
      hl.setComponentAlignment(avatar, Alignment.MIDDLE_LEFT);
      avatar.addStyleName("m-cursor-pointer");
    }
    IDButton uButt = new IDButton(auth.getUserName(), SHOWUSERPROFILECLICK, auth.getId());
    uButt.addStyleName(BaseTheme.BUTTON_LINK);
    uButt.setWidth(8.0f, Unit.EM); 
    
    hl.addComponent(uButt);
    hl.setComponentAlignment(uButt, Alignment.MIDDLE_LEFT);

    return hl;
  }

  private Component buildAuthorNameColumn(String name)
  {
    Label lab = new Label(name);
    lab.addStyleName("m-cursor-pointer");
    lab.addStyleName("m-bluetext-on-hover");
    return lab;
  }

  public HbnContainer<Card> getContainer()
  {
    return container;
  }
  
  public void setContainer(HbnContainer<Card> c)
  {
    setContainerDataSource(container=c);
  }
}
