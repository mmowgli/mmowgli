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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Session;
import org.hibernate.criterion.Order;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.pii.Query2Pii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.registrationlogin.RegistrationPagePopupFirst;
import edu.nps.moves.mmowgli.modules.registrationlogin.Vips;
import edu.nps.moves.mmowgli.utility.BrowserWindowOpener;
import edu.nps.moves.mmowgli.utility.BulkMailHandler;


/**
 * SignupsTable.java
 * Created on Jun 24, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class SignupsTable extends Table
{
  private static final long serialVersionUID = -2752871756174884792L;
  //private ApplicationEntryPoint app;

  private static String EMAIL_COL = "email";
  private static String BACKGROUND_COL = "background";
  private static String DATE_COL = "date";
  private static String INGAME_COL = "name"; // not used for name
  private static String INVITED_COL = "invited";
  private static String CONFIRMED_COL = "confirmed";

  private String[] visibleColumns = {EMAIL_COL,DATE_COL,     BACKGROUND_COL, INVITED_COL, CONFIRMED_COL, INGAME_COL};
  private String[] displayedNames = {"email",  "signup date","background",   "eligible",   "confirmed",     "in game"       };
  private int   [] columnWidths   = {200,      85,          10,             50,              70,            50};

  private ClickListener changeListener;
  private ValueChangeListener selectListener;

  @HibernateSessionThreadLocalConstructor
  public SignupsTable(String caption, ClickListener changeListener, ValueChangeListener selectListener)
  {
    super(caption);
    this.changeListener = changeListener;
    this.selectListener = selectListener;

    setContainerDataSource(new QueryContainer()); //SignupsTableContainer<Query2Pii>());

    initColumnCustomizers();

    setVisibleColumns((Object[])visibleColumns);
    setColumnHeaders(displayedNames);
    setColumnWidths(columnWidths);
    setColumnExpandRatio(BACKGROUND_COL, 1.0f);
    setSelectable(true);
    setPageLength(30);
    setSelectable(true);
    setMultiSelect(true);
    setImmediate(true);

    addValueChangeListener(new SelectListener());
    addStyleName("m-signupstable");
  }

  @SuppressWarnings("serial")
  class CBListener implements ValueChangeListener
  {
    QueryCB cb;
    public CBListener(QueryCB cb)
    {
      this.cb = cb;
    }
    @Override
    @MmowgliCodeEntry
    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event)
    {
      Session sess = VHibPii.getASession();
      sess.beginTransaction();
      
      QueryWrapper wrapper = cb.wrapper;

      boolean localInvited = cb.getValue();
      boolean vipSaysOK = Vips.isVipOrVipDomainAndNotBlackListed(wrapper.getEmail(), sess);

      if(localInvited && vipSaysOK) {
        wrapper.setInvited(localInvited);
        wrapper.setConfirmed(false);
        cb.mate.setConfirmed(false);
      }
      else if(localInvited && !vipSaysOK) {
        Vips.addEmail(wrapper.getEmail(),sess);
        wrapper.setInvited(localInvited);
        wrapper.setConfirmed(false);
        cb.mate.setConfirmed(false);
      }
      else if(!localInvited && vipSaysOK) {
        Vips.blackListEmail(wrapper.getEmail(),sess);
        wrapper.setInvited(localInvited);
        wrapper.setConfirmed(false);
        cb.mate.setConfirmed(false);
      }
      else if(!localInvited && !vipSaysOK) {
        wrapper.setInvited(localInvited);
        wrapper.setConfirmed(false);
        cb.mate.setConfirmed(false);
      }
      sess.getTransaction().commit();
      sess.close();

      if (changeListener != null)
        changeListener.buttonClick(null);  // hope it doesnt need an event
    }
  };

  // This is the handler for the confirmed button.  It's one way:
  // it always implies that the confirmation is positive.
  @SuppressWarnings("serial")
  ClickListener confirmedListener = new ClickListener()
  {
    @Override
    public void buttonClick (ClickEvent event)
    {
      Session sess = VHibPii.getASession();
      sess.beginTransaction();

      Button b = (Button)event.getSource();
      ConfirmedComponent comp = (ConfirmedComponent)b.getParent();
      QueryWrapper wrapper = comp.wrapper;

      boolean localConfirmed = true; //comp.isConfirmed();
      wrapper.setConfirmed(localConfirmed);

      sess.update(wrapper.getQuery());
      sess.getTransaction().commit();
      sess.close();

      if (changeListener != null)
        changeListener.buttonClick(event);
    }
  };

  @SuppressWarnings("serial")
  class SelectListener implements ValueChangeListener
  {
     @Override
     public void valueChange(com.vaadin.data.Property.ValueChangeEvent event)
     {
       if(selectListener != null)
         selectListener.valueChange(event);
     }
  }

  private void setColumnWidths(int[] w)
  {
    int i=0;
    for(String col : visibleColumns) {
      int wid = w[i++];
      if(wid != -1)
        this.setColumnWidth(col, wid);
    }
  }

  private void initColumnCustomizers()
  {
    Table.ColumnGenerator colGen = new ColumnCustomizer();
    addGeneratedColumn(EMAIL_COL,colGen);
    addGeneratedColumn(BACKGROUND_COL, colGen);
    addGeneratedColumn(DATE_COL, colGen);
    addGeneratedColumn(INGAME_COL,colGen);
    addGeneratedColumn(INVITED_COL,colGen);
    addGeneratedColumn(CONFIRMED_COL,colGen);
  }

  HashMap<QueryWrapper,Mated> invitedCBMap = new HashMap<QueryWrapper,Mated>();
  HashMap<QueryWrapper,Mated> confirmedCBMap = new HashMap<QueryWrapper,Mated>();

  private void linkInvitedToConfirmed(QueryWrapper key, QueryCB invitedCB)
  {
    linkCBs(key,invitedCB,invitedCBMap,confirmedCBMap);
  }
  private void linkConfirmedToInvited(QueryWrapper key, ConfirmedComponent confirmedComp)
  {
    linkCBs(key,confirmedComp,confirmedCBMap,invitedCBMap);
  }
  private void linkCBs(QueryWrapper key, Mated myMate, HashMap<QueryWrapper, Mated> myMap, HashMap<QueryWrapper, Mated> otherMap)
  {
    Mated otherMate = otherMap.get(key);
    if(otherMate == null) {
      myMap.put(key, myMate);
      return;
    }
    //System.out.println(""+otherMate+" mated to "+myMate);
    otherMap.remove(key);
    otherMate.setMate(myMate);
    myMate.setMate(otherMate);
  }

  DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm");

  class ColumnCustomizer implements Table.ColumnGenerator
  {
    private static final long serialVersionUID = 1938821794468835620L;

    @Override
    public Component generateCell(Table source, Object itemId, Object columnId)
    {
      QueryWrapper wrapper = (QueryWrapper)itemId;
      Label lab;
      if(EMAIL_COL.equals(columnId)) {
        lab = new Label(wrapper.getEmail());
        lab.setEnabled(!wrapper.isIngame());
        return lab;
      }
      if (BACKGROUND_COL.equals(columnId)) {
        lab = new Label(wrapper.getBackground());
        lab.setDescription(wrapper.getBackground());
        lab.setEnabled(!wrapper.isIngame());
        return lab;
      }
      if(DATE_COL.equals(columnId)) {
        Date d = wrapper.getDate();
        lab = new Label(df.format(d));
        lab.setEnabled(!wrapper.isIngame());
        return lab;
      }
      if(INGAME_COL.equals(columnId)) {
        lab = new Label(wrapper.isIngame()?"yes":"");
        return lab;
      }
      if(INVITED_COL.equals(columnId)) {
        QueryCB cb =  new QueryCB(wrapper);
        cb.setValue(wrapper.isInvited());
        cb.setImmediate(true);
        cb.addValueChangeListener(new CBListener(cb));
        linkInvitedToConfirmed(wrapper,cb);
        boolean inGame = wrapper.isIngame();
        cb.setEnabled(!inGame);
        if(!wrapper.isInvited() && inGame)
          cb.addStyleName("m-redborder");
        return cb;
      }
      if(CONFIRMED_COL.equals(columnId)) {
        ConfirmedComponent cc = new ConfirmedComponent(wrapper);
        cc.setConfirmed(wrapper.isConfirmed());
        cc.setImmediate(true);
        cc.addListener(confirmedListener);
        linkConfirmedToInvited(wrapper,cc);
        cc.setEnabled(!wrapper.isIngame());
        return cc;
      }
      return new Label("Program error in SignupsTable.java");
    }
  }

  @SuppressWarnings("serial")
  public static void showDialog(String title)
  {
    final Button bulkMailButt = new Button("Initiate bulk mail job sending to filtered list");
    
    final Button emailButt = new Button("Compose email");
    emailButt.setDescription("Opens editing dialog to compose an email message to the selected individuals");
    final Button displayButt = new Button("Display as plain text");
    Button closeButt;

    final SignupsTable tab=new SignupsTable(null,null,
      new ValueChangeListener()  // selected
      {
        @Override
        public void valueChange(com.vaadin.data.Property.ValueChangeEvent event)
        {
          emailButt.setEnabled(true);
        }
      });

    final Window dialog = new Window(title);
    dialog.setWidth("950px");
    dialog.setHeight("650px");

    VerticalLayout vl = new VerticalLayout();
    dialog.setContent(vl);
    vl.setSizeFull();
    vl.setMargin(true);
    vl.setSpacing(true);
    addFilterCheckBoxes(vl);
    vl.addComponent(new Label("Individuals who have established game accounts are shown faintly"));

    tab.setSizeFull();
    vl.addComponent(tab);
    vl.setExpandRatio(tab, 1.0f);

    HorizontalLayout buttHL = new HorizontalLayout();
    buttHL.setSpacing(true);
   
    buttHL.addComponent(bulkMailButt);
    bulkMailButt.setImmediate(true);;
    Label lab = new Label("");
    buttHL.addComponent(lab );
    buttHL.setExpandRatio(lab, 1.0f);
    
    buttHL.addComponent(emailButt);
    emailButt.setImmediate(true);
    buttHL.addComponent(displayButt);
    displayButt.setImmediate(true);
    buttHL.addComponent(closeButt = new Button("Close"));
    closeButt.setImmediate(true);

    emailButt.setEnabled(false);

    closeButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        dialog.close();
      }
    });

    emailButt.addClickListener(new ClickListener()
    {
      @SuppressWarnings("rawtypes")
      @Override
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        Set set = (Set)tab.getValue();
        ArrayList<String> emails = new ArrayList<String>(set.size());
        Iterator itr = set.iterator();
        while(itr.hasNext()) {
          QueryWrapper wrap = (QueryWrapper)itr.next();
          emails.add(wrap.getEmail());
        }
        new SendMessageWindow(emails);
        HSess.close();
      }
    });

    displayButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        dumpSignupsTL();
        HSess.close();
      }
    });
    
    bulkMailButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        BulkMailHandler.showDialog((QueryContainer)tab.getContainerDataSource());
      }     
    });
    
    vl.addComponent(buttHL);
    vl.setComponentAlignment(buttHL, Alignment.MIDDLE_RIGHT);

    UI.getCurrent().addWindow(dialog);
    dialog.center();
  }
  private static CheckBox confirmedCB, ingameCB, eligibleCB;
  
  private static void addFilterCheckBoxes(VerticalLayout vl)
  {
    HorizontalLayout hl = new HorizontalLayout();
    hl.setSpacing(true);
    hl.addComponent(new HtmlLabel("Filters:&nbsp;&nbsp;"));
    hl.addComponent(confirmedCB = new CheckBox("confirmed"));
    hl.addComponent(ingameCB = new CheckBox("in-game"));
    hl.addComponent(eligibleCB = new CheckBox("eligible"));
    confirmedCB.setDescription("email address has been confirmed");
    ingameCB.setDescription("a player with this email address has established a game account");
    eligibleCB.setDescription("email address meets elibibility rules (*.mil, *.navy.mil, etc.");
  }
  
  @SuppressWarnings("serial")
  public class QueryContainer extends BeanItemContainer<QueryWrapper>
  {
    @SuppressWarnings("unchecked")
    public QueryContainer()
    {
      super(QueryWrapper.class);

      Session piiSess = VHibPii.getASession();
      piiSess.beginTransaction();

      List<Query2Pii> lis = (List<Query2Pii>)piiSess.createCriteria(Query2Pii.class).list();
      Collections.sort(lis, new DomainComparator());
      Iterator<Query2Pii> itr = lis.iterator();

      while(itr.hasNext()) {
        QueryWrapper wrap = new QueryWrapper(itr.next());
        synchronizeWithVipList(wrap,piiSess);  // may update in session
        if(wrap.isIngame() == null)
          isInGame(wrap, piiSess);
        addBean(wrap);
      }
      piiSess.getTransaction().commit();
      piiSess.close();
    }

    private boolean isInGame(QueryWrapper wrap, Session piiSess)
    {
      if(wrap.isIngame() == null) {
        wrap.setIngame(!RegistrationPagePopupFirst.checkEmail(wrap.getEmail()));
        piiSess.update(wrap.qpii);
      }
      return wrap.isIngame();
    }
  }

  private void synchronizeWithVipList(QueryWrapper wrap, Session piiSess)
  {
    boolean localConfirmed = wrap.isConfirmed();
    boolean localInvited   = wrap.isInvited();
    boolean vipSays = Vips.isVipOrVipDomainAndNotBlackListed(wrap.getEmail(), piiSess);
    boolean conflict = vipSays != localInvited;

    if(!conflict) {
      ; // good
    }
    else if(localConfirmed && conflict) {
      wrap.setConfirmed(false);
      if(vipSays != localInvited)
        wrap.setInvited(vipSays);
      piiSess.update(wrap.getQuery());
    }
    else if(!localConfirmed && conflict) {
      wrap.setInvited(vipSays);
      piiSess.update(wrap.getQuery());
    }
  }  // does not commit transaction

  public static void dumpSignupsTL()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("<h3>");
    String title = Game.getTL().getTitle();

    Session sess = VHibPii.getASession();

    @SuppressWarnings("unchecked")
    List<Query2Pii> lis = sess.createCriteria(Query2Pii.class)
        .addOrder(Order.desc("date"))
        .list();

    sb.append(title);
    sb.append("</h3>");
    sb.append("<h3>User signup list</h3>");
    SimpleDateFormat dateFormatter = new SimpleDateFormat("y/MM/dd HH:mm z");
    sb.append("<h3>");
    sb.append(dateFormatter.format(new Date())); // now
    sb.append("</h2>");
    sb.append("<h4>total count: ");
    sb.append(lis.size());
    sb.append("</h4>");
    sb.append("<pre>");

    String linesep = System.getProperty("line.separator");
    sb.append("ID\tEMAIL\tSIGNUP DATE\tINTEREST IN MMOWGLI\tCONFIRMED\tELIGIBLE\tIN-GAME");
    sb.append(linesep);
    
    int count = lis.size();
    for (Query2Pii q2 : lis) {
      sb.append(count);
      sb.append('\t');
      sb.append(q2.getEmail());
      sb.append('\t');
      sb.append(dateFormatter.format(q2.getDate()));
      sb.append('\t');
      String s = q2.getInterest();
      if (s == null)
        s = "";
      else
        s = s.replace('\t', ';');
      sb.append(s);
      sb.append('\t');
      sb.append(q2.isConfirmed() ? "confirmed" : "");
      sb.append('\t');
      sb.append(q2.isInvited() ? "eligible" : "");
      sb.append('\t');
      sb.append(q2.isIngame() ? "in-game" : "");
      sb.append(linesep);
      count--;
    }
    sb.append(linesep);
    sb.append("Please note: these game signups are not Personal Identifying Information (PII) for anonymous in-game players.");
    sb.append(linesep);
    sb.append("Rather, this list of individuals interested in notification supports the game design team during game preparations.");
    sb.append("</pre>");

    title = title.replace(' ', '_');
    
    title = title+"_mmowgli_signups"+UUID.randomUUID();
    BrowserWindowOpener.openWithInnerHTML(sb.toString(),title,"_blank");
 }

  @SuppressWarnings("serial")
  static class QuickStringStream implements StreamResource.StreamSource
  {
    StringBuilder sb;
    public QuickStringStream(StringBuilder sb)
    {
      this.sb = sb;
    }

    @Override
    public InputStream getStream()
    {
      try {
        return new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
      }
      catch(UnsupportedEncodingException ex) {
        return null;
      }
    }
  }

  class DomainComparator implements Comparator<Query2Pii>
  {
    @Override
    public int compare(Query2Pii arg0, Query2Pii arg1)
    {
      String s0 = arg0.getEmail(); s0 = (s0==null?"":s0);
      String s1 = arg1.getEmail(); s1 = (s1==null?"":s1);
      if(s0.contains("@"))
        s0 = s0.substring(s0.lastIndexOf("@"));
      if(s1.contains("@"))
        s1 = s1.substring(s1.lastIndexOf("@"));
      return s0.compareTo(s1);
    }
  }

  @SuppressWarnings("unused")
  public class QueryWrapper
  {
    private String email;
    private String background;
    private Date date;
    private boolean invited;  // aka "eligible"
    private boolean confirmed;
    private Boolean ingame = null;

    private Query2Pii qpii;
    public QueryWrapper(Query2Pii qpii)
    {
      this.qpii = qpii;
    }

    public String getEmail(){return qpii.getEmail();}
    public void setEmail(String email){qpii.setEmail(email);}

    public String getBackground(){return qpii.getBackground();}
    public void setBackground(String background){qpii.setBackground(background);}

    public Date getDate(){return qpii.getDate();}
    public void setDate(Date date){qpii.setDate(date);}

    public boolean isInvited(){return qpii.isInvited();}
    public void setInvited(boolean invited) {qpii.setInvited(invited);}

    public boolean isConfirmed(){return qpii.isConfirmed();}
    public void setConfirmed(boolean confirmed) {qpii.setConfirmed(confirmed);}

    public Boolean isIngame(){return qpii.isIngame();}
    public void setIngame(Boolean ingame) {qpii.setIngame(ingame);

    }
    public Query2Pii getQuery(){return qpii;}
  }

  class QueryCB extends CheckBox implements Mated
  {
    private static final long serialVersionUID = 1L;

    public QueryWrapper wrapper;
    public ConfirmedComponent mate = null;

    public QueryCB(QueryWrapper wrapper)
    {
      super();
      this.wrapper = wrapper;
   }

    @Override
    public Mated getMate()
    {
      return mate;
    }

    @Override
    public void setMate(Mated mate)
    {
      this.mate = (ConfirmedComponent) mate;
    }

    @Override
    public void setConfirmed(boolean wh)
    {
      // do nothing
    }

  }

  interface Mated
  {
    public Mated getMate();
    public void setMate(Mated mate);
    public void setConfirmed(boolean wh);
  }

  class ConfirmedComponent extends AbsoluteLayout implements ClickListener, Mated
  {
    private static final long serialVersionUID = 1L;

    public QueryWrapper wrapper;
    Label label;
    Button button;
    String position = "top:0px;left:0px";
    String lposition= "top:3px;left:5px";

    public ConfirmedComponent(QueryWrapper wrapper)
    {
      this.wrapper = wrapper;
      label = new HtmlLabel("<b>confirmed</b>");
      button = new Button("confirm");
      button.addStyleName(Reindeer.BUTTON_SMALL);
      button.addClickListener(this);

      this.addComponent(label, lposition);
      this.addComponent(button,position);
      setConfirmed(false);
      setWidth("65px");
      setHeight("20px");
    }

    ClickListener lis;

    @Override
    public void buttonClick(ClickEvent event)
    {
      if(lis != null)
        lis.buttonClick(event);

      setConfirmed(true);
    }

    public void addListener(ClickListener lis)
    {
      this.lis = lis;
    }

    @Override
    public void setEnabled(boolean wh)
    {
      button.setEnabled(wh);
      label.setEnabled(wh);
    }

    @Override
    public Mated getMate()
    {
      return null;
    }

    @Override
    public void setMate(Mated mate)
    {
    }

    @Override
    public void setConfirmed(boolean wh)
    {
      label.setVisible(wh);
      button.setVisible(!wh);
    }
    public boolean isConfirmed()
    {
      return label.isVisible();
    }
  }
}