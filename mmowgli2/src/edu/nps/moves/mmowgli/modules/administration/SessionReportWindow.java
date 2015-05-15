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
  along with Mmowgli, in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.modules.administration;

import static edu.nps.moves.mmowgli.AppMaster.SESSION_REPORT_MAX_WIDTH;
import static edu.nps.moves.mmowgli.AppMaster.SESS_RPT_ID_COLUMN;

import java.util.Arrays;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.data.Item;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * SessionReportWindow.java created on Jan 29, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class SessionReportWindow extends Window
{
  private static final long serialVersionUID = -1106920231938294885L;

  public static void showSessionReport()
  {
    Window me = new SessionReportWindow("Active Players");
    UI.getCurrent().addWindow(me);
    me.center();
  }
  
  private int widestRowSize = 0;
  private Table table;
  public SessionReportWindow(String caption)
  {
    super(caption);
    setWidth("700px");
    setHeight("400px");
    VerticalLayout vLay = new VerticalLayout();
    vLay.setSizeFull();
    setContent(vLay);
    
    String header = AppMaster.instance().getSessionReportHeader();
    String[] _headerArr = header.split("\t");
    widestRowSize=_headerArr.length;
    
    String svrName = AppMaster.instance().getServerName();
    int idx;
    if((idx=svrName.indexOf('.')) != -1)
      svrName = svrName.substring(0, idx);
    
    String me = AppMaster.instance().getLocalNodeReportRaw().toString();
    me = svrName+"\n"+me;
    String[][] localReport = parseReport(me);
        
    String all = AppMaster.instance().getCompletePlayerReportRaw().toString();
    String[][] remoteReports = parseReport(all);
    
    String[] headerArr;
    if(widestRowSize>_headerArr.length) {
      headerArr = new String[widestRowSize];
      Arrays.fill(headerArr," ");
      for(int i=0;i<_headerArr.length;i++)
        headerArr[i]=_headerArr[i];       
    }
    else
      headerArr = _headerArr;
    
    table = new Table();
    table.setSizeFull();
    
    for(int col=0; col<headerArr.length; col++) {
      if(col==0)
        table.addContainerProperty(headerArr[col], Label.class, null);
      else
        table.addContainerProperty(headerArr[col], String.class, null);
    }       
    addReportToTable(headerArr, localReport);
    addReportToTable(headerArr, remoteReports);

    vLay.addComponent(makeTopSummary(localReport,remoteReports));
    vLay.addComponent(table);
    vLay.setExpandRatio(table, 1.0f);
  }
  
  @SuppressWarnings("unchecked")
  private void addReportToTable(String[] header, String[][] report)
  {    
    for(int r=0;r<report.length;r++) {
      String[] rowArray = report[r];
      if(rowArray.length>0) {
        Item row = table.getItem(table.addItem());    
        for(int col=0; col<rowArray.length;col++) {
          if(col == 0)
            row.getItemProperty(header[col]).setValue(new HtmlLabel(rowArray[col]));
          else
            row.getItemProperty(header[col]).setValue(rowArray[col]);
        }
      }
    }    
  }
  
  int count = 0;
  String[][] parseReport(String s)
  {
    if(s.trim().length()<=0)
      return new String[0][];
    
    String[] rows = s.split("\n");
    String[][] grid = new String[rows.length][];
    int r=0;
    for(String row : rows) {
      String[] cols = row.split("\t");
      if(cols.length > widestRowSize)
        widestRowSize = cols.length;
      grid[r++]=cols;
      if(cols.length >1)
        cols[0]= "<div style='text-align:right;width:inherit'>"+ ++count+"</div>";
    }
    return grid;
  }
  
  private Component makeTopSummary(String[][] localRpt, String[][] remoteRpts)
  {
    Object key = HSess.checkInit();
    HorizontalLayout hLay = new MHorizontalLayout().withMargin(new MarginInfo(false,true,false,true));
    totals tots = new totals();
    
    count(localRpt,tots);
    count(remoteRpts,tots);
    
    Criteria criteria = HSess.get().createCriteria(User.class);
    criteria.setProjection(Projections.rowCount());
    criteria.add(Restrictions.eq("accountDisabled", false));
    int totcount = ((Long)criteria.list().get(0)).intValue();
        
    hLay.addComponent(new Label("Logged-in players: "+tots.loggedin));
    hLay.addComponent(new Label("   Total open sessions: "+tots.total));
    hLay.addComponent(new Label("   Total registered players: "+totcount));
    hLay.addStyleName("m-margintop-5");
    hLay.addStyleName("m-marginbottom-5");
    
    HSess.checkClose(key);
    return hLay;
  }
  
  class totals {
    public int total;
    public int loggedin;
  }
  
  private void count(String[][] rpt, totals tots)
  {
    for(String[] row : rpt) {
      if(row.length != SESSION_REPORT_MAX_WIDTH)
        continue;  // thats the server name
      tots.total++;
      if(row[SESS_RPT_ID_COLUMN].trim().length()>0)
        tots.loggedin++;      
    }    
  }
}
