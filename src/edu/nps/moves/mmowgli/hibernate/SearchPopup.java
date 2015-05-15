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

package edu.nps.moves.mmowgli.hibernate;

import static edu.nps.moves.mmowgli.MmowgliEvent.ACTIONPLANSHOWCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.CARDCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.SHOWUSERPROFILECLICK;

import java.util.*;

import org.getopt.luke.TermInfo;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.ColumnGenerator;

import edu.nps.moves.mmowgli.AppEvent;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliController;
import edu.nps.moves.mmowgli.components.*;
import edu.nps.moves.mmowgli.components.WordCloudPanel.Word;
import edu.nps.moves.mmowgli.components.WordCloudPanel.WordButton;
import edu.nps.moves.mmowgli.components.WordCloudPanel.WordOrder;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.markers.*;

/**
 * SearchPopup.java
 * Created on May 30, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: SearchPopup.java 3056 2013-11-07 20:26:25Z jmbailey $
 */
public class SearchPopup extends Window implements ClickListener
{
  private static final long serialVersionUID = -3173608977165486650L;
  private static String USERTYPEKEY = "Player";
  private static String CARDTYPEKEY = "Card";
  private static String ACTIONPLANTYPEKEY = "Action Plan";

  private TextField tf;
  private FireableButton searchButt;
  private Table resultsTable;
  private Label statusLabel;
  private Thread searchThread=null;
  private searcher searcherObj = null;
  private Button closeButt;
  private ClickListener closeListener;
  private String initialText=null;

  Map<String,Word> working = new HashMap<String,Word>();
  Map<String,Word> map = new HashMap<String,Word>();
  String[][] stemSets = {{"soma","somal","somaly","somalia","somalian","somali"},
                         {"pira","pirat","piracy","piraci","pirate"},
                         {"acceler","accelerate"},
                         {"anoth", "another"},
                         {"countri","country"},
                         {"resourc","resource"},
                         {"auster","austere"},
                         {"bogu","bogus"},
                         {"navi","navy"}};

  public SearchPopup()
  {
    this(null);
  }

  @SuppressWarnings("serial")
  public SearchPopup(String startingText)
  {
    super("Search for Players, Action Plans, Idea Cards");
    //setModal(true);   better not to be model

    this.initialText = startingText;
    setWidth("635px");
    setHeight("600px");
    center();

    VerticalLayout vLay = new VerticalLayout();
    vLay.setSizeFull();
    vLay.setMargin(true);
    vLay.setSpacing(true);
    setContent(vLay);

    WordCloudPanel wcPan = new WordCloudPanel(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        WordButton wb = (WordButton)event.getButton();
        tf.setValue(wb.word.text);
        searchButt.fireClick();
      }
    });
    wcPan.setImmediate(true);

    TermInfo[] tinfo = SearchManager.getHighFrequencyTerms();
    List<Word> wdLis = new ArrayList<Word>(tinfo.length);

    for(TermInfo inf : tinfo) {
      String txt = inf.term.text();
      Word wd = new Word(txt,inf.docFreq);
      wdLis.add(wd);
    }

// V7
    /*
    TermStats[] tstats = SearchManager.getHighFrequencyTerms();
    List<Word> wdLis = new ArrayList<Word>(tstats.length);
    for(TermStats ts : tstats) {
      String txt = new String(ts.termtext.utf8ToString());
      Word wd = new Word(txt,ts.docFreq);
      wdLis.add(wd);
    }
 */   
    
    wdLis = finalFilter(wdLis);
   // dumpList(wLis);
    wcPan.setWordData(wdLis, WordOrder.ALPHA);
    wcPan.setWidth("590px"); // won't work in ie"100%");
    vLay.addComponent(wcPan);

    HorizontalLayout topHL = new HorizontalLayout();
    vLay.addComponent(topHL);
    topHL.setWidth("100%");
    topHL.setMargin(true);
    topHL.setSpacing(true);

    topHL.addComponent(tf = new TextField());
    tf.addValueChangeListener(new TextFieldChangeListener());
    tf.setWidth("100%");
    //tf.setInputPrompt("Enter search terms");
    tf.setImmediate(true);
    tf.setTextChangeTimeout(5000);
    topHL.setExpandRatio(tf, 1.0f);
    if(startingText != null)
      tf.setValue(startingText);

    searchButt = new FireableButton("Search",this);
    topHL.addComponent(searchButt);
    searchButt.setClickShortcut(KeyCode.ENTER);

    vLay.addComponent(statusLabel = new HtmlLabel("&nbsp;"));
    statusLabel.setImmediate(true);

    resultsTable = new Table();
    resultsTable.addStyleName("m-actiondashboard-table");
    resultsTable.setImmediate(true);
    vLay.addComponent(resultsTable);
    resultsTable.setHeight("100%");
    resultsTable.setWidth("99%");
    vLay.setExpandRatio(resultsTable, 1.0f);

    BeanItemContainer<SearchResult> bCont = new BeanItemContainer<SearchResult>(SearchResult.class);
    bCont.addBean(new SearchResult("","","")); // fix for 6.7.0 bug
    resultsTable.addGeneratedColumn("text", new ToolTipAdder());
    resetTable(bCont);

    resultsTable.setNewItemsAllowed(false);
    resultsTable.setSelectable(true);

    resultsTable.addItemClickListener(new ItemClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void itemClick(ItemClickEvent event)
      {
        HSess.init();
        @SuppressWarnings("unchecked")
        BeanItem<SearchResult> bi = (BeanItem<SearchResult>)event.getItem();
        SearchResult sr = bi.getBean();
        MmowgliController controller = Mmowgli2UI.getGlobals().getController();
        if(sr.getType().equals(USERTYPEKEY))
          controller.miscEventTL(new AppEvent(SHOWUSERPROFILECLICK, SearchPopup.this, sr.getHibId()));
        else if(sr.getType().equals(ACTIONPLANTYPEKEY))
          controller.miscEventTL(new AppEvent(ACTIONPLANSHOWCLICK, SearchPopup.this, sr.getHibId()));
        else
          controller.miscEventTL(new AppEvent(CARDCLICK, SearchPopup.this, sr.getHibId()));
        HSess.close();
        
        closeListener.buttonClick(null);
      }
    });

    vLay.addComponent(closeButt=new Button("Close"));
    vLay.setComponentAlignment(closeButt, Alignment.TOP_RIGHT);
    closeButt.addClickListener(closeListener=new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        if(searchThread != null)
          searcherObj.killed = true;
        //V7 SearchPopup.this.getParent().removeWindow(SearchPopup.this);
        SearchPopup.this.close();
      }
    });
  }

  private List<Word> finalFilter(List<Word> wLis)
  {
    working.clear();
    for (Word w : wLis) {
      big: {
        for (int i = 0; i < stemSets.length; i++) {
          String[] line = stemSets[i];
          for (String line1 : line) {
              if (w.text.equalsIgnoreCase(line1)) {
                  handleException(w,i);
                  break big;
              }
            } // j
        } // i
        if(working.get(w.text) == null)  // check for duplicates
          working.put(w.text,w);
      } // big
    }

    wLis.clear();
    for(Iterator<Word> itr = working.values().iterator(); itr.hasNext();)
      wLis.add(itr.next());
    return wLis;
  }

  private void handleException(Word w, int idx)
  {
    int len = stemSets[idx].length;
    w.text = stemSets[idx][len-1];  // use last in list
    Word mapped = map.get(w.text);
    if(mapped == null) {
      map.put(w.text, w);
      working.put(w.text,w);
    }
    else
      mapped.freq+= w.freq;
  }

//  private void dumpList(ArrayList<Word>lis)
//  {
//    for(Iterator<Word>itr = lis.iterator();itr.hasNext();) {
//      Word w = itr.next();
//      System.out.println(w.text+" --> "+w.freq);
//    }
//  }

  private void resetTable(Container cont)
  {
    resultsTable.setContainerDataSource(cont);
    resultsTable.setVisibleColumns((Object[])new String[]{"typeAndId","text"});
    resultsTable.setColumnHeaders(new String[]{"Item","Content"});
    resultsTable.setColumnWidth("typeAndId", 100);
    resultsTable.setColumnExpandRatio("text", 1.0f);
    resultsTable.setImmediate(true);
  }

  @SuppressWarnings("serial")
  class ToolTipAdder implements ColumnGenerator
  {
    @Override
    public Object generateCell(Table source, Object itemId, Object columnId)
    {
      SearchResult res = (SearchResult)itemId;
      Label lab = new Label(res.getText());
      lab.setDescription(res.getText());
      return lab;
    }
  }

  public class SearchResult
  {
    private Object hibId="";
    private String type="";
    private String typeAndId;
    private String text="";
    private String type2="";

    public SearchResult()
    {}
    public SearchResult(Object hibId, String type, String text)
    {
      this.hibId = hibId;
      this.type = type;
      this.text = text;
      handleTypeAndId();
    }
    public SearchResult(Object hibId, String type, String text, String type2)
    {
      this.hibId = hibId;
      this.type = type;
      this.text = text;
      this.type2 = type2;
      handleTypeAndId();
    }
    public String getText()
    {
      return text;
    }
    public void setText(String text)
    {
      this.text = text;
    }
    public String getType()
    {
      return type;
    }
    public void setType(String type)
    {
      this.type = type;
      handleTypeAndId();
    }
    public void setType2(String type2)
    {
      this.type2 = type2;
      handleTypeAndId();
    }
    public Object getHibId()
    {
      return hibId;
    }
    public void setHibId(Object hibId)
    {
      this.hibId = hibId;
      handleTypeAndId();
    }
    public String getTypeAndId()
    {
      return typeAndId;
    }
    public void setTypeAndId(String typeAndId)
    {
      this.typeAndId = typeAndId;
    }
    private void handleTypeAndId()
    {
      typeAndId = type+" "+hibId.toString() + " " + type2;
      typeAndId = typeAndId.trim();
    }
  }

  // Search clicked
  @Override
  @MmowgliCodeEntry
  @HibernateConditionallyOpened
  @HibernateConditionallyClosed
  public void buttonClick(ClickEvent event)
  {
    String terms = tf.getValue().toString();
    if(terms == null || terms.length()<=0)
      return;
    statusLabel.setValue("<span style='color:red;'>Searching...</span>");
    tf.selectAll();
    tf.focus();

    String [] tokenArray = terms.trim().split("\\s+");
    Object sessKey = HSess.checkInit();
    User me = Mmowgli2UI.getGlobals().getUserTL();
    
    // test Do it in another thread
    /*
    searchThread = new Thread(searcherObj=new searcher(me,tokenArray),"Searcher");
    searchThread.setPriority(Thread.NORM_PRIORITY);
    searchThread.start();
    */
    searcherObj = new searcher(me,tokenArray);
    searcherObj.run();
    HSess.checkClose(sessKey);
  }

  private class searcher implements Runnable
  {
    private String[] tokenArray;
    public boolean killed = false;
    private User me;
    public searcher(User me, String[] tokenArray)
    {
      this.me = me;
      this.tokenArray = tokenArray;
    }

    @Override
    public void run()
    {
      Collection<?> results = SearchManager.searchAll(tokenArray); // do the search
      BeanItemContainer<SearchResult> bCont = new BeanItemContainer<SearchResult>(SearchResult.class);
      if (!results.isEmpty()) {
        statusLabel.setValue("Found " + results.size());
        Iterator<?> itr = results.iterator();

        // Need to know if I'm a game master
        
        while (itr.hasNext()) {
          if (killed)
            return;
          Object o = itr.next();
          SearchResult sr = new SearchResult();
          if (o instanceof Card) {
            Card c = (Card) o;
            if(c.isHidden() && !me.isGameMaster())
              continue;
            sr.setHibId(c.getId());
            sr.setType(CARDTYPEKEY);
            sr.setType2(c.getCardType().getTitle());
            sr.setText(c.getText());
          }
          else if (o instanceof User) {
            User u = (User) o;
            if(u.isAccountDisabled())
              continue;
            sr.setHibId(u.getId());
            sr.setType(USERTYPEKEY);
            sr.setText(u.getUserName() + ", " + u.getLocation() + ", " + u.getExpertise() + ", " + u.getAffiliation());
          }
          else if (o instanceof ActionPlan) {
            ActionPlan ap = (ActionPlan) o;
            if(ap.isHidden())
              continue;
            sr.setHibId(ap.getId());
            sr.setType(ACTIONPLANTYPEKEY);
            sr.setText(ap.getTitle());
          }
          bCont.addItem(sr);
        }
      }
      if(bCont.size()<=0) {
        bCont.addBean(new SearchResult("","","")); // fix for 6.7.0 bug
        statusLabel.setValue("Found none.");
      }
      if(!killed) {
        resetTable(bCont);
        Mmowgli2UI.getAppUI().access(new Runnable(){public void run(){Mmowgli2UI.getAppUI().push();}});
      }
      searchThread = null;
    }
  }

  @Override
  public void attach()
  {
    super.attach();
    tf.focus();

    if(initialText != null)
      searchButt.fireClick();
  }

  @SuppressWarnings("serial")
  private class FireableButton extends Button
  {
    public FireableButton(String s, ClickListener lis)
    {
      super(s,lis);
    }
    @Override
    public void fireClick()
    {
      super.fireClick();
    }
  }

  @SuppressWarnings("serial")
  class TextFieldChangeListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange(ValueChangeEvent event)
    {
      Object o = tf.getValue();
      // First time through, searchButt is null, and we don't want to do a double fire anyway, so it's all good
      if(searchButt !=null && o != null && o.toString().length()>0)
        searchButt.fireClick();
    }
  }
}
