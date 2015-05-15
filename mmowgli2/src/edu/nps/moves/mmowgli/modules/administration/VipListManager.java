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

import java.util.*;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.pii.VipPii;
import edu.nps.moves.mmowgli.hibernate.VHibPii;

/**
 * ActionPlanExporter.java Created on Nov 28, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class VipListManager
{
  Thread thread;
  private Window dialog;
  private OptionGroup radios;
  private static String EMAILTYPE = "Emails";
  private static String DOMAINTYPE = "Domains";
  private ListSelect vipListSelect;
  private Button deleteButt;
  
  public interface DoneListener
  {
    public void continueOrCancel(String[] s);
  }
  
  public interface DeleteListener
  {
    public void continueOrCancel(Set<VipPii> s);
  }
  
  public VipListManager()
  {
  }
  
  public void add()
  {
    // add names to vip list
    showAddDialogOrCancel(new DoneListener()
    {
      public void continueOrCancel(String[] sa)
      {
        if(sa != null) {
          List<VipPii>lis = VHibPii.getAllVips();
          for(String s : sa) {
            if(s.length()>0) {
              VipPii v;
              if(!entryInDb(s,lis)) {  // check for already there
                if(radios.getValue().toString().equals(EMAILTYPE)) {
                  v = new VipPii(s,VipPii.VipType.EMAIL);
                }
                else {
                  v = new VipPii(s,VipPii.VipType.DOMAIN);
                }
                VHibPii.save(v);
              }
            }
          }
        }
      }
    });
  }
  
  private boolean entryInDb(String entry, List<VipPii>all)
  {
    // We can't simply query on the entry field since it's encrypted.  Since this will probably never be a very big table
    // and this code only gets occasionally hit, just brute-force it.
    entry = entry.toLowerCase();
    for(VipPii vip : all)
      if(vip.getEntry().toLowerCase().equals(entry))
        return true;
    return false;
  }
  
  private void showAddDialogOrCancel(final DoneListener lis)
  {
    dialog = new Window("Add to VIP list");
    dialog.setModal(true);
    dialog.setWidth("400px");
    dialog.setHeight("350px");
    
    VerticalLayout layout = new VerticalLayout();
    dialog.setContent(layout);
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setSizeFull();

    List<String> rtypes = Arrays.asList(new String[]{EMAILTYPE,DOMAINTYPE});
    radios = new OptionGroup("Select type", rtypes);

    radios.setNullSelectionAllowed(false); // user can not 'unselect'
    radios.select("Emails"); // select this by default
    radios.setImmediate(false); // don't send the change to the server at once
    layout.addComponent(radios);
    
    final TextArea ta = new TextArea();
    //ta.setColumns(40);
    ta.setSizeFull();
    ta.setInputPrompt("Type or paste a tab-, comma- or space-separated list of emails or domains.  For domains, "+
    "use forms such as \"army.mil\", \"nmci.navy.mil\", \"ucla.edu\", \"gov\", etc.");
    layout.addComponent(ta);

    HorizontalLayout hl = new HorizontalLayout();
    hl.setSpacing(true);
    @SuppressWarnings("serial")
    Button cancelButt = new Button("Cancel", new Button.ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        dialog.close();
        lis.continueOrCancel(null);
      }
    });
    
    @SuppressWarnings("serial")
    Button addButt = new Button("Add", new Button.ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        String[] returnArr = null;
        String result = ta.getValue().toString();
        if(result == null || result.length()<=0)
          returnArr = null;
        else if((returnArr=parseIt(result)) == null)
          return;
                
        dialog.close();
        lis.continueOrCancel(returnArr);
      }
    });
    
    hl.addComponent(cancelButt);
    hl.addComponent(addButt);
    hl.setComponentAlignment(cancelButt, Alignment.MIDDLE_RIGHT);
    hl.setExpandRatio(cancelButt, 1.0f);

    // The components added to the window are actually added to the window's
    // layout; you can use either. Alignments are set using the layout
    layout.addComponent(hl);

    hl.setWidth("100%");
    ta.setWidth("100%");
    ta.setHeight("100%");
    layout.setExpandRatio(ta, 1.0f);

    
    UI.getCurrent().addWindow(dialog);
    dialog.center();
  }
  
  private String[] parseIt(String s)
  {
    String[] sa = null;
    try {
      sa=s.trim().split("[\\s,]+");
      int numAts = 0;
      for(String str : sa) {
        if(str.contains("@"))
          numAts++;
      }
      if(radios.getValue().toString().equals(EMAILTYPE)) {
        if(numAts != sa.length) {
          Notification.show(
              "Error!",
              "Email addresses must include an 'at' symbol",
              Notification.Type.ERROR_MESSAGE);
          sa = null;
        }
      }
      else {
        if(numAts != 0) {
          Notification.show(
              "Error!",
              "Domain names must not contain an 'at' symbol",
              Notification.Type.ERROR_MESSAGE);
          sa = null;
        }  
      }
    }
    catch(Throwable t) {
      Notification.show(
          "Error!",
          "Had some difficulty parsing your data -- check for errors",
          Notification.Type.ERROR_MESSAGE);
    }
    return sa;
  }

  public void view()
  {
    showViewOrDelete(new DeleteListener()
    {
      @SuppressWarnings("unchecked")
      public void continueOrCancel(Set<VipPii> set)
      {
        if (set != null) {
          Session sess = VHibPii.getASession();
          sess.beginTransaction();
          for (VipPii v : set) {
            List<VipPii> lis = sess.createCriteria(VipPii.class).add(Restrictions.eq("id", (Long)v.getId())).list();
            if (lis.size() > 0)
              sess.delete(lis.get(0));
            else
              System.err.println("didn't find VipPii to delete in VipListManager.showViewOrDelete");
          }
          sess.getTransaction().commit();
          sess.close();
        }
      }
    });
  }

  @SuppressWarnings({ "unchecked", "serial" })
  private void showViewOrDelete(final DeleteListener lis)
  {
    dialog = new Window("View / Delete VIPs");
    dialog.setModal(true);

    VerticalLayout layout = new VerticalLayout();
    dialog.setContent(layout);
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setSizeFull();

    List<VipPii> vLis = VHibPii.getAllVips();
    
    vipListSelect = new ListSelect("Select items to delete");
    StringBuffer sb = new StringBuffer(); // for popup
    vipListSelect.addStyleName("m-greyborder");
    String lf = System.getProperty("line.separator");
    for (int i = 0; i < vLis.size(); i++) {
      VipPii v;
      vipListSelect.addItem(v=vLis.get(i));
      sb.append(v.getEntry());
      sb.append(lf);
    }
    if(sb.length() > 0)
      sb.setLength(sb.length()-1); // last space
      
    vipListSelect.setNullSelectionAllowed(true);
    vipListSelect.setMultiSelect(true);
    vipListSelect.setImmediate(true);
    vipListSelect.addValueChangeListener(new VipSelectListener());

    layout.addComponent(vipListSelect);
    
    Label copyPopupList = new HtmlLabel("<pre>"+sb.toString()+"</pre>");
    Panel p = new Panel();
    VerticalLayout lay = new VerticalLayout();
    p.setContent(lay);
    lay.addComponent(copyPopupList);
    p.setWidth("400px");
    p.setHeight("300px");
    PopupView popup = new PopupView("Display list as copyable text", p);
    popup.setHideOnMouseOut(false);
    if(sb.length()<=0)
      popup.setEnabled(false);
    
    layout.addComponent(popup);
    layout.setComponentAlignment(popup, Alignment.MIDDLE_CENTER);
    
    HorizontalLayout hl = new HorizontalLayout();
    hl.setSpacing(true);
    Button cancelButt = new Button("Cancel", new Button.ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        dialog.close();
        lis.continueOrCancel(null);
      }
    });
    
    deleteButt = new Button("Delete & Close", new Button.ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        Set<VipPii> set = (Set<VipPii>)vipListSelect.getValue();
        if(set.size()<=0)
          set = null;
        dialog.close();
        lis.continueOrCancel(set);
      }
    });
    deleteButt.setEnabled(false);
    hl.addComponent(cancelButt);
    hl.addComponent(deleteButt);
    hl.setComponentAlignment(cancelButt, Alignment.MIDDLE_RIGHT);
    hl.setExpandRatio(cancelButt, 1.0f);

    // The components added to the window are actually added to the window's
    // layout; you can use either. Alignments are set using the layout
    layout.addComponent(hl);
    dialog.setWidth("300px");
    dialog.setHeight("350px");
    hl.setWidth("100%");
    vipListSelect.setWidth("99%");
    vipListSelect.setHeight("99%");
    layout.setExpandRatio(vipListSelect, 1.0f);

    UI.getCurrent().addWindow(dialog);
    dialog.center();
  }
  
  @SuppressWarnings("serial")
  class VipSelectListener implements Property.ValueChangeListener
  {
    @SuppressWarnings("unchecked")
    @Override
    public void valueChange(ValueChangeEvent event)
    {
      Set<VipPii> set = (Set<VipPii>)vipListSelect.getValue();
      deleteButt.setEnabled(set.size()>0);
    }    
  }
}
