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

import java.util.*;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals.CardPermission;
import edu.nps.moves.mmowgli.cache.MCacheUserHelper.QuickUser;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.actionplans.AddAuthorDialog;
import edu.nps.moves.mmowgli.modules.cards.CardMarkingManager;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;
import edu.nps.moves.mmowgli.modules.cards.CardTypeManager;
import edu.nps.moves.mmowgli.utility.BaseCoroutine;

/**
 * CardSummaryListHeader.java Created on Feb 3, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardSummaryListHeader extends AbsoluteLayout implements MmowgliComponent
{
  private static final long serialVersionUID = 7131657887407479242L;

  public static final String CARDLISTHEADER_H = "165px";
  public static final String CARDLISTHEADER_W = "235px";
  public static final String CARDLISTHEADER_TOTAL_H = "262px";
  public static final String CARDLISTHEADER_TITLE_H = "26px";
  public static final String CARDLISTHEADER_TITLE_W = "214px";
  public static final String CARDLISTHEADER_TITLE_POS = "top:17px;left:20px";
  public static final String CARDLISTHEADER_CONTENT_H = "80px";
  public static final String CARDLISTHEADER_CONTENT_W = "190px";
  public static final String CARDLISTHEADER_CONTENT_POS = "top:58px;left:20px";
  public static final String CARDLISTHEADER_DRAWER_H = "138px";
  public static final String CARDLISTHEADER_DRAWER_W = "236px";
  public static final String CARDLISTHEADER_DRAWER_POS = "top:127px;left:-1px";

  public static final String CARDLISTHEADER_DRAWER_TEXT_W = "208px";
  public static final String CARDLISTHEADER_DRAWER_TEXT_H = "70px";
  public static final String CARDLISTHEADER_DRAWER_TEXT_POS = "top:32px;left:14px";

  public static final String CARDLISTHEADER_DRAWER_COUNT_W = "64px";
  public static final String CARDLISTHEADER_DRAWER_COUNT_H = "15px";
  public static final String CARDLISTHEADER_DRAWER_COUNT_POS = "top:109px;left:15px";
  public static final String CARDLISTHEADER_DRAWER_CANCEL_W = "64px";
  public static final String CARDLISTHEADER_DRAWER_CANCEL_H = "15px";
  public static final String CARDLISTHEADER_DRAWER_CANCEL_POS = "top:106px;left:87px";
  public static final String CARDLISTHEADER_DRAWER_OKBUTT_W = "64px";
  public static final String CARDLISTHEADER_DRAWER_OKBUTT_H = "15px";
  public static final String CARDLISTHEADER_DRAWER_OKBUTT_POS = "top:106px;left:155px";

  public static CardSummaryListHeader newCardSummaryListHeader(CardType ct, Card parent)
  {
    return newCardSummaryListHeader(ct, false, parent);
  }

  public static CardSummaryListHeader newCardSummaryListHeader(CardType ct, boolean mockupOnly, Card parent)
  {
    CardSummaryListHeader lstHdr = new CardSummaryListHeader(ct.getId(), mockupOnly, parent);
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    lstHdr.bckgrndResource = globs.mediaLocator().getCardSummaryListHeaderBackground(ct);
    lstHdr.drawerResource = globs.mediaLocator().getCardSummaryDrawerBackground(ct);
    return lstHdr;
  }

  private Resource bckgrndResource;
  private Resource drawerResource;
  private Label title;
  private Embedded titleImage;
  private Label content;
  private Object ctId;
  private CardType ct;
  BuilderDrawer drawerComponent;

  private boolean mockupOnly = false;
  private Card parent = null; // may remain null
  private String HEIGHT_NODRAWER = CARDLISTHEADER_H;
  private String HEIGHT_YESDRAWER = CARDLISTHEADER_TOTAL_H;

  @HibernateSessionThreadLocalConstructor
  private CardSummaryListHeader(Object cardTypeId, boolean mockupOnly, Card parent)
  {
    title = new Label();
    content = new Label();
    this.ctId = cardTypeId;
    this.mockupOnly = mockupOnly;
    this.parent = parent;
  }

  @SuppressWarnings("serial")
  @Override
  public void initGui()
  {
    addStyleName("m-cursor-pointer");
    if (bckgrndResource != null) {
      Embedded bkgnd = new Embedded(null, bckgrndResource);
      addComponent(bkgnd, "top:0px;left:0px");
    }
    final MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    ct = CardType.getTL(ctId);
    String textColorStyle = CardStyler.getCardTextColorOverBaseStyle(ct);

    // nested abslay for the click handler
    AbsoluteLayout topHalfLay = new AbsoluteLayout();
    topHalfLay.setWidth(CARDLISTHEADER_W);
    topHalfLay.setHeight(HEIGHT_NODRAWER);
    addComponent(topHalfLay, "top:0px;left:0px");

    title.setValue(ct.getTitle()); // .toUpperCase());
    title.setHeight(CARDLISTHEADER_TITLE_H);
    title.setWidth(CARDLISTHEADER_TITLE_W);
    title.addStyleName("m-cardsummarylist-header-title");
    title.addStyleName("m-cursor-pointer");
    title.addStyleName("m-vagabond-font");
    if (textColorStyle != null)
      title.addStyleName(textColorStyle);

    topHalfLay.addComponent(title, CARDLISTHEADER_TITLE_POS);

    content.setValue(ct.getPrompt());
    content.setHeight(CARDLISTHEADER_CONTENT_H);
    content.setWidth(CARDLISTHEADER_CONTENT_W);
    content.addStyleName("m-cardsummarylist-header-content");
    content.addStyleName("m-cursor-pointer");
    if (textColorStyle != null)
      content.addStyleName(textColorStyle);
    // cause exception w/ 2 windows?
    // content.setDebugId(CardTypeManager.getCardCreateClickDebugId(ct));
    content.setId(CardTypeManager.getCardCreateClickDebugId(ct));
    topHalfLay.addComponent(content, CARDLISTHEADER_CONTENT_POS);
    
    boolean cantCreateBecauseHiddenParent = checkNoCreateBecauseHiddenTL(parent);
    boolean cantCreateBecauseParentMarkedNoChild = checkNoCreateBecauseParentMarkedNoChild(parent);
    
    if (globs.canCreateCard(ct.isIdeaCard())) {
      markedAsNoCreate = false;
      if(!cantCreateBecauseHiddenParent && !cantCreateBecauseParentMarkedNoChild) {
      	//Add the text at the bottom
        Label lab;
        topHalfLay.addComponent(lab = new Label("click to add new"), "top:130px;left:75px");
        lab.addStyleName("m-click-to-add-new");
        if (textColorStyle != null)
          lab.addStyleName(textColorStyle);
      }
    }
    else
      markedAsNoCreate = true;
    
    drawerComponent = new BuilderDrawer();
    addComponent(drawerComponent, CARDLISTHEADER_DRAWER_POS);
    drawerComponent.setVisible(false);

    setWidth(CARDLISTHEADER_W);
    setHeight(HEIGHT_NODRAWER);

    if (!mockupOnly)// && !cantCreateBecauseHiddenParent && !cantCreateBecauseParentMarkedNoChild)
      topHalfLay.addLayoutClickListener(new LayoutClickListener()
      {
        @Override
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void layoutClick(LayoutClickEvent event)
        {
          HSess.init();
          if(checkNoCreateBecauseHiddenTL(parent) || checkNoCreateBecauseParentMarkedNoChild(parent)) {
            if (drawerComponent.isVisible())
              closeDrawer();
          	HSess.close();
          	return;
          }
          
          if (drawerComponent.isVisible())
            closeDrawer();
          else {
            CardPermission cp = globs.cardPermissionsCommon(ct.isIdeaCard());
            if (!cp.canCreate) {
              if (!markedAsNoCreate)
                handleNoCreate();
              Notification.show(cp.whyNot);
            }
            else {
              showDrawer();
              handleCanCreate(); // reset tt, etc.
              if (newCardListener != null)
                newCardListener.drawerOpenedTL(ctId);
            }
          }
          HSess.close();
        }
      });
    if (cantCreateBecauseHiddenParent)
      handleNoCreate("Can't add card to hidden parent");
    else if(cantCreateBecauseParentMarkedNoChild)
    	handleNoCreate("New child cards cannot be added to this card");
    else if (!globs.canCreateCard(ct.isIdeaCard()))
      handleNoCreate();
    else
      setTooltip("Click to add card");
  }

  private boolean checkNoCreateBecauseHiddenTL(Card c)
  {
    if (c == null)
      return false; // ok to create
    User me = Mmowgli2UI.getGlobals().getUserTL();
    return c.isHidden() && !me.isGameMaster();
  }
  
  private boolean checkNoCreateBecauseParentMarkedNoChild(Card c)
  {
    if (c == null)
      return false; // ok to create
  	return CardMarkingManager.isNoChildren(c);
  }
  
  private void handleCanCreate()
  {
    if (markedAsNoCreate) {
      markedAsNoCreate = false;
      setTooltip("Click to add card");
      CardSummaryListHeader.this.addStyleName("m-cursor-pointer");
      title.addStyleName("m-cursor-pointer");
      content.addStyleName("m-cursor-pointer");
    }
  }

  private boolean markedAsNoCreate = false;

  private void handleNoCreate()
  {
    handleNoCreate(null);
  }

  private void handleNoCreate(String msg)
  {
    if (!markedAsNoCreate) {
      markedAsNoCreate = true;
      if (msg == null) {
        MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
        setTooltip(globs.whyCantCreateCard(ct.isIdeaCard()));
      }
      else
        setTooltip(msg);

      CardSummaryListHeader.this.removeStyleName("m-cursor-pointer");
      title.removeStyleName("m-cursor-pointer");
      content.removeStyleName("m-cursor-pointer");
    }
  }

  private void setTooltip(String tt)
  {
    setDescription(tt); // abslay
    if (titleImage != null)
      titleImage.setDescription(tt);
    if (title != null)
      title.setDescription(tt);
    content.setDescription(tt);
  }

  public void closeDrawer()
  {
    drawerComponent.setVisible(false);
    CardSummaryListHeader.this.setHeight(HEIGHT_NODRAWER);
  }

  private void showDrawer()
  {
    drawerComponent.setVisible(true);
    CardSummaryListHeader.this.setHeight(HEIGHT_YESDRAWER);
    drawerComponent.getTextEntryComponent().focus();
  }

  class BuilderDrawer extends AbsoluteLayout
  {
    private static final long serialVersionUID = -9012026151912117528L;
    TextArea content;
    Label count;
    NativeButton submitButt;
    NativeButton cancelButt;

    BuilderDrawer()
    {
      if (drawerResource != null) {
        Embedded drawerBkg = new Embedded(null, drawerResource);
        addComponent(drawerBkg, "top:0px;left:0px");
      }
      content = new TextArea();
      // only shows if no focus, and if we don't have focus, it's not normally showing
      // content.setInputPrompt("Type here to add to this card chain.");
      content.setWordwrap(true);
      content.setImmediate(true);
      content.setTextChangeEventMode(TextChangeEventMode.LAZY);
      content.setTextChangeTimeout(500);
      // cause exception w/ 2 windows?
      // content.setDebugId(CardTypeManager.getCardContentDebugId(ct));
      content.setId(CardTypeManager.getCardContentDebugId(ct));

      content.addTextChangeListener(new characterTypedHandler());

      content.setWidth(CARDLISTHEADER_DRAWER_TEXT_W);
      content.setHeight(CARDLISTHEADER_DRAWER_TEXT_H);
      content.addStyleName("m-white-background");
      addComponent(content, CARDLISTHEADER_DRAWER_TEXT_POS);

      count = new Label("0/140");
      count.setWidth(CARDLISTHEADER_DRAWER_COUNT_W);
      count.setHeight(CARDLISTHEADER_DRAWER_COUNT_H);
      count.addStyleName("m-cardbuilder-count-text");
      addComponent(count, CARDLISTHEADER_DRAWER_COUNT_POS);

      cancelButt = new NativeButton("cancel");
      cancelButt.setWidth(CARDLISTHEADER_DRAWER_CANCEL_W);
      cancelButt.setHeight(CARDLISTHEADER_DRAWER_CANCEL_H);
      cancelButt.addStyleName("borderless");
      cancelButt.addStyleName("m-cardbuilder-button-text");
      cancelButt.addClickListener(new CancelHandler());
      addComponent(cancelButt, CARDLISTHEADER_DRAWER_CANCEL_POS);

      submitButt = new NativeButton("submit");
      // cause exception w/ 2 windows?
      // submitButt.setDebugId(CardTypeManager.getCardSubmitDebugId(ct));
      submitButt.setId(CardTypeManager.getCardSubmitDebugId(ct));

      submitButt.setWidth(CARDLISTHEADER_DRAWER_OKBUTT_W);
      submitButt.setHeight(CARDLISTHEADER_DRAWER_OKBUTT_H);
      submitButt.addStyleName("borderless");
      submitButt.addStyleName("m-cardbuilder-button-text");
      submitButt.addClickListener(new CardPlayHandler());
      addComponent(submitButt, CARDLISTHEADER_DRAWER_OKBUTT_POS);

      setWidth(CARDLISTHEADER_DRAWER_W);
      setHeight(CARDLISTHEADER_DRAWER_H);
    }

    public AbstractField<?> getTextEntryComponent()
    {
      return content;
    }

    @Override
    public void setVisible(boolean visible)
    {
      super.setVisible(visible);
      if (visible && content.getValue().toString().length() > 0)
        content.selectAll();
    }

    @SuppressWarnings("serial")
    class CancelHandler implements ClickListener
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        closeDrawer();
      }
    }

    @SuppressWarnings("serial")
    class characterTypedHandler implements TextChangeListener
    {
      @Override
      public void textChange(TextChangeEvent event)
      {
        String s = event.getText();
        if (s == null)
          ;
        else {
          int num = s.trim().length();
          count.setValue("" + num + "/140");
        }
      }
    }

    @SuppressWarnings("serial")
    class CardPlayHandler extends BaseCoroutine implements Button.ClickListener
    {
      private User author;
      private String txt;
      private ClickEvent event;

      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        this.event = event;
        run(); // executes step1() of the coroutine
        HSess.close();
      }

      @Override
      public void step1()
      {
        txt = content.getValue().toString();
        txt = txt.trim();
        if (txt.length() < 5) {
          Notification.show("Card not played.", "Your message is too short to be useful.", Notification.Type.ERROR_MESSAGE);
          doNotAdvanceSteps(); // come into step1 again next time
          return;
        }
        if (txt.length() > 140) {
          Notification.show("Card not played.", "Only 140 characters please.", Notification.Type.ERROR_MESSAGE);
          doNotAdvanceSteps(); // come into step1 again next time
          return;
        }

        // Admins get to add cards under other names
        author = Mmowgli2UI.getGlobals().getUserTL(); 
        if (author.isAdministrator())
          adminSwitchAuthorsTL(event.getButton(), this);
        else
          run(); // does not need to suspend, so "continues" and executes step2
                 // in the same clicklistener thread
      }

      @Override
      @HibernateUserRead
      public void step2()
      {
        CardType ct = CardType.getTL(ctId);
        Date dt = new Date();
        Card c = new Card(txt, ct, dt);
        c.setCreatedInMove(Move.getCurrentMoveTL());
        c.setAuthor(User.getTL(author.getId())); // fresh
        if (newCardListener != null)
          newCardListener.cardCreatedTL(c);

        content.setValue("");
        content.setInputPrompt("Enter text for another card.");
        closeDrawer();

        resetCoroutine(); // for another click
      }
    }

    @SuppressWarnings("serial")
    @HibernateRead
    private void adminSwitchAuthorsTL(Button butt, final CardPlayHandler coroutine)
    {
      ArrayList<User> meLis = new ArrayList<User>(1);
      meLis.add(coroutine.author);
      User me = User.getTL(Mmowgli2UI.getGlobals().getUserID());
      
      final AddAuthorDialog dial = new AddAuthorDialog(meLis, true);
      StringBuilder sb = new StringBuilder("As adminstrator, <b>");
      sb.append(me.getUserName());
      sb.append("</b>, you may choose another player to be card author.");
      dial.infoLabel.setValue(sb.toString());
      dial.setCaption("Select Proxy Author");
      dial.setMultiSelect(false);
      dial.cancelButt.setCaption("Use myself");
      dial.addButt.setCaption("Use selected");

      // Rearrange buttons, add real cancel butt.
      // -------------------
      HorizontalLayout buttonHL = dial.getButtonHorizontalLayout();
      Iterator<Component> itr = buttonHL.iterator();
      Vector<Component> v = new Vector<Component>();
      while (itr.hasNext()) {
        Component component = itr.next();
        if (component instanceof Button)
          v.add(component);
      }
      buttonHL.removeAllComponents();
      itr = v.iterator();
      while (itr.hasNext()) {
        buttonHL.addComponent(itr.next());
      }
      Label sp = null;
      buttonHL.addComponent(sp = new Label());
      sp.setWidth("1px");
      buttonHL.setExpandRatio(sp, 1.0f);

      Button cancelButt = null;
      buttonHL.addComponent(cancelButt = new Button("Cancel"));
      cancelButt.addClickListener(new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
          UI.getCurrent().removeWindow(dial);// dial.getParent().removeWindow(dial);
          coroutine.resetCoroutine();
        }
      });
      // -------------------

      dial.selectItemAt(0);
      dial.addListener(new CloseListener()
      {
        @Override
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        @HibernateUserRead
        public void windowClose(CloseEvent e)
        {
          HSess.init();
          if (dial.addClicked) {
            Object o = dial.getSelected();

            if (o instanceof User) {
              coroutine.author = (User) o;
            }
            else if (o instanceof QuickUser) {
              QuickUser qu = (QuickUser) o;
              coroutine.author = User.getTL(qu.id);
            }
          }
          coroutine.run(); // finish up
          HSess.close();
        }
      });

      UI.getCurrent().addWindow(dial);
      dial.center();
    }
  }

  NewCardListener newCardListener;

  public void addNewCardListener(NewCardListener lis)
  {
    newCardListener = lis;
  }

  public static interface NewCardListener
  {
    public void cardCreatedTL(Card c);

    public void drawerOpenedTL(Object cardTypeId);
  }
  
  public void cardUpdated_oobTL()
  {
  	parent = Card.getTL(parent.getId()); // refresh in case markings have changed
  	parent.getMarking(); // make sure it's retrieved
  	if (drawerComponent.isVisible())
      closeDrawer();
  }
}
