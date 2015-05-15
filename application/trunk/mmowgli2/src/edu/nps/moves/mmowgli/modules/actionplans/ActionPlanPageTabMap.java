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

package edu.nps.moves.mmowgli.modules.actionplans;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.modules.maps.LeafletLayers;

//import com.google.maps.gwt.client.Marker;

/**
 * ActionPlanPageTabMap.java
 * Created on Feb 8, 2011
 * Updated on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@SuppressWarnings("unused")
public class ActionPlanPageTabMap extends ActionPlanPageTabPanel
{
  private static final long serialVersionUID = 7244591383530793474L;
  
  private edu.nps.moves.mmowgli.db.GoogleMap mmowgliMap;
 // private                   MmowgliMapWidget googleMapWidget;
  
  private static String MAPWIDTH  = "685px";
  private static String MAPHEIGHT = "670px";
  private static String MAPHEIGHT_WITH_BUTTONS = "650px";

  private static double MAPW_CALC = 685.0d;
  private static double MAPH_CALC = 670.0d;
  private static double MAPH_CALC_WITH_BUTTONS = 650.0d;
  
  private long markerIdx = 1;
  boolean editingOK = false;
  boolean messageSent = false;
  private GridLayout flagGrid;
  private MapSaveButtPan savePanel;
  
  private int throwAwayMoveEvents = 0;
  private final int THROWAWAYMOVES = 2;
  private LMap map= new LMap();
 // private LTileLayer osmTiles = new LTileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png");
  private LTileLayer mapBoxTiles = new LTileLayer();
  private LTileLayer mapQstTiles = new LTileLayer();

  @HibernateSessionThreadLocalConstructor
  public ActionPlanPageTabMap(Object apId, boolean isMockup, boolean readonly)
  {
    super(apId, isMockup, readonly);
  }
  
  @Override
  public void setVisible(boolean visible)
  {
    if (visible)
      // do this only when we're showing ourselves
      togglePopupsAndMarkers(editingOK);
    super.setVisible(visible);
  }
  
  private void togglePopupsAndMarkers(boolean show)
  {
/*    // Cycle through all the markers and tell their content obj to show or hide the bottom tools
    List<Marker> lis = googleMapWidget.getMarkers();
    for (Marker m : lis) {
      PopupContent pop = (PopupContent) m.getInfoWindowContent()[0].getContent();
      pop.setToolsVisible(show);
      ((MmowgliMarker)m).setDraggable(show);
    }
    */
  }

  @Override
  public void initGui()
  {
	setSizeUndefined();

    ActionPlan ap = ActionPlan.getTL(apId);
    mmowgliMap = ap.getMap();
    if(mmowgliMap == null) {
      mmowgliMap = new edu.nps.moves.mmowgli.db.GoogleMap();
      GoogleMap.saveTL(mmowgliMap);
      
      ap.setMap(mmowgliMap);
      ActionPlan.updateTL(ap);
    }
         
    VerticalLayout leftLay = getLeftLayout();
    leftLay.setSpacing(false);
    leftLay.setMargin(false);

    Label missionLab = new Label("Authors, put your plan on the map!");
    leftLay.addComponent(missionLab);
    leftLay.setComponentAlignment(missionLab, Alignment.TOP_LEFT);
    missionLab.addStyleName("m-actionplan-mission-title-text");
        
    Label missionContentLab;
    if(!isMockup)
      missionContentLab = new HtmlLabel(ap.getMapInstructions());
    else {
      Game g = Game.getTL();
      missionContentLab = new HtmlLabel(g.getDefaultActionPlanMapText());
    }
    
    leftLay.addComponent(missionContentLab);
    leftLay.setComponentAlignment(missionContentLab, Alignment.TOP_LEFT);
    leftLay.addStyleName("m-actionplan-mission-content-text");
    /*
    Component c;
    c=buildMapFlags(leftLay);  // does the addComponent
    leftLay.setComponentAlignment(c, Alignment.TOP_CENTER);
    */
    toggleFlags(editingOK);
    
    Label sp;
    leftLay.addComponent(sp=new Label());
    sp.setHeight("1px");
    leftLay.setExpandRatio(sp, 1.0f);

    map.setAttributionPrefix("Powered by Leaflet with v-leaflet");
    map.addStyleName("m-greyborder");
    map.removeAllComponents();
    try {
      LeafletLayers.installAllProviders(map);
    }
    catch(Exception ex) {
      System.err.println("ActionPlanPageTabMap error loading layers: "+ex.getClass().getSimpleName()+" "+ex.getLocalizedMessage());
    }
    double lat = mmowgliMap.getLatCenter();
    double lon = mmowgliMap.getLonCenter();
    map.setCenter(lat,lon);
    map.setZoomLevel(mmowgliMap.getZoom());
    map.setWidth(MAPWIDTH);
    map.setHeight(editingOK ? MAPHEIGHT_WITH_BUTTONS : MAPHEIGHT);
    
    // Build a mmowgliMap widget from our content
    /*
    googleMapWidget = new MmowgliMapWidget(app, mmowgliMap.getLatLonCenter(), mmowgliMap.getZoom(), GOOGLEMAPS_KEY);

    googleMapWidget.setWidth(MAPWIDTH);
    googleMapWidget.setHeight(editingOK ? MAPHEIGHT_WITH_BUTTONS : MAPHEIGHT);
    googleMapWidget.addControl(MapControl.MenuMapTypeControl);
    googleMapWidget.addControl(MapControl.SmallMapControl);
    //googleMapWidget.addListener(new MyMapClickListener());
    googleMapWidget.addListener(new MyMapMoveListener());
    //googleMapWidget.addListener(new MyMarkerClickListener());
    googleMapWidget.addListener(new MyMarkerMovedListener());
    googleMapWidget.reportMapBounds();
*/
    loadMarkers(ap);
    
    VerticalLayout rightLay = getRightLayout();
    rightLay.setSpacing(false);
    rightLay.setMargin(false);

    rightLay.addComponent(sp=new Label());
    sp.setHeight("15px");
    
    savePanel = new MapSaveButtPan();
    rightLay.addComponent(savePanel);
    rightLay.setComponentAlignment(savePanel, Alignment.TOP_CENTER);
    savePanel.setVisible(editingOK);
   // MapSaveListener msLis = new MapSaveListener();
  //  savePanel.setClickHearers(msLis.mapLocListener,msLis.mapMarkerListener,msLis.cancelListener);
    
  /*  DragAndDropWrapper ddw = new DragAndDropWrapper(googleMapWidget);
    ddw.setDropHandler(new MapDropHandler());
    ddw.setSizeFull();
    
    rightLay.addComponent(ddw);
    rightLay.setExpandRatio(ddw, 1.0f);
    */
    rightLay.addComponent(map);
    rightLay.setExpandRatio(map, 1);
  }
  
  private void reloadMarkers(ActionPlan ap)
  {
 /*   googleMapWidget.getMarkers().clear();
    loadMarkers(ap);
    */
  }
  
  private void loadMarkers(ActionPlan ap)
  {
/*    List<GoogleMapMarker> lis = ap.getMap().getMarkers(); //mmowgliMap.getMarkers();
    for(GoogleMapMarker gmm : lis) {
      MmowgliMarker mm;
      googleMapWidget.addMarker(mm=new MmowgliMarker(googleMapWidget,markerIdx++,gmm));
      mm.setDraggable(true);
    }    
*/
  }
  
//@formatter:off
  public static String NUMBERTAG = "${num}";
  public static String ALPHATAG  = "${let}";
  static Object [][] flagData = {
    { -1, "http://google-maps-icons.googlecode.com/files/redblank.png",               "http://google-maps-icons.googlecode.com/files/redblank.png"},
    { -1, "http://google-maps-icons.googlecode.com/files/blackblank.png",             "http://google-maps-icons.googlecode.com/files/blackblank.png"},
    
    { 21,  "http://google-maps-icons.googlecode.com/files/teal"    +NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/teal"     +"(\\p{Digit}+)" + "\\.png"},// 00 - 20
    { 21,  "http://google-maps-icons.googlecode.com/files/green"   +NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/green"    +"(\\p{Digit}+)" + "\\.png"},
    { 21,  "http://google-maps-icons.googlecode.com/files/yellow"  +NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/yellow"   +"(\\p{Digit}+)" + "\\.png"},
    { 21,  "http://google-maps-icons.googlecode.com/files/orange"  +NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/orange"   +"(\\p{Digit}+)" + "\\.png"},
    { 101, "http://google-maps-icons.googlecode.com/files/red"     +NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/red"      +"(\\p{Digit}+)" + "\\.png"},
    { 21,  "http://google-maps-icons.googlecode.com/files/pink"    +NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/pink"     +"(\\p{Digit}+)" + "\\.png"},
    { 21,  "http://google-maps-icons.googlecode.com/files/purple"  +NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/purple"   +"(\\p{Digit}+)" + "\\.png"},
    { 21,  "http://google-maps-icons.googlecode.com/files/blue"    +NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/blue"     +"(\\p{Digit}+)" + "\\.png"},
    { 21,  "http://google-maps-icons.googlecode.com/files/darkblue"+NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/darkblue" +"(\\p{Digit}+)" + "\\.png"},
    { 101, "http://google-maps-icons.googlecode.com/files/black"   +NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/black"    +"(\\p{Digit}+)" + "\\.png"},
    { 21, "http://google-maps-icons.googlecode.com/files/brown"    +NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/brown"    +"(\\p{Digit}+)" + "\\.png"},
    { 21, "http://google-maps-icons.googlecode.com/files/gray"     +NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/gray"     +"(\\p{Digit}+)" + "\\.png"},
    { 21, "http://google-maps-icons.googlecode.com/files/white"    +NUMBERTAG+".png", "http://google-maps-icons.googlecode.com/files/white"    +"(\\p{Digit}+)" + "\\.png"},

    { 26, "http://google-maps-icons.googlecode.com/files/blue"     +ALPHATAG+".png",  "http://google-maps-icons.googlecode.com/files/blue"     +"(\\p{Upper}+)" + "\\.png"},
    { 26, "http://google-maps-icons.googlecode.com/files/black"    +ALPHATAG+".png",  "http://google-maps-icons.googlecode.com/files/black"    +"(\\p{Upper}+)" + "\\.png"},

     };
//@formatter:on
  
  Component buildMapFlags(VerticalLayout vl)
  {
    flagGrid = new GridLayout();
    vl.addComponent(flagGrid);
    flagGrid.setSizeUndefined();
    flagGrid.addStyleName("m-greyborder");
    flagGrid.setColumns(6);
    
    for (Object[] row : flagData) {
      int num    = (Integer)row[0];
      String url = (String)row[1];
      if(num == -1)
        flagGrid.addComponent(MapFlag.getMapFlag(1,null,url));
      else {
        if(url.indexOf(NUMBERTAG) != -1 )
          flagGrid.addComponent(MapFlag.getMapFlag(num, null, url, false));

        else if(url.indexOf(ALPHATAG) != -1 )
          flagGrid.addComponent(MapFlag.getMapFlag(num, null, url, true));
      }
    }
    return flagGrid;    
  }
  
  private void toggleSaveButtPan(boolean enabled)
  {
    savePanel.setVisible(enabled & !isReadOnly);
    map.setHeight(enabled ? MAPHEIGHT_WITH_BUTTONS : MAPHEIGHT);
    //googleMapWidget.setHeight(enabled ? MAPHEIGHT_WITH_BUTTONS : MAPHEIGHT);
  }
  
  private void toggleFlags(boolean enabled)
  {
    return;
    /*
    Iterator<Component> itr = flagGrid.iterator();
    while(itr.hasNext())
      itr.next().setEnabled(enabled);
    */
  }
 /* 
  @SuppressWarnings("serial")
  class MapDropHandler implements DropHandler
  {
    @Override
    public void drop(DragAndDropEvent event)
    {
      if(!editingOK)
        return;
      
      Component c = event.getTransferable().getSourceComponent();
      if(! (c instanceof MapFlag))
        return;
      
      WrapperTargetDetails details = (WrapperTargetDetails) event.getTargetDetails();
      
      int absTop = details.getAbsoluteTop();  // absolute pos of drop wrapper (mmowgliMap here) on the page
      int absLeft = details.getAbsoluteLeft(); 
      MouseEventDetails deets = details.getMouseEvent();
      int clientX = deets.getClientX();
      int clientY = deets.getClientY();
      Point2D.Double neBounds = googleMapWidget.getBoundsNE();
      Point2D.Double swBounds = googleMapWidget.getBoundsSW();
      
      int mouseLeft = clientX - absLeft ; //- (int)Math.round(gmm.getIconAnchorX());
      int mouseUp   = clientY - absTop  ;
      
      double percentLeft = ((double)mouseLeft) / MAPW_CALC;
      double hCalc = editingOK ? MAPH_CALC_WITH_BUTTONS : MAPH_CALC;
      double percentDown = ((double)mouseUp) / hCalc;
      double lonWidth  = Math.abs(neBounds.x - swBounds.x);
      double latHeight = Math.abs(neBounds.y - swBounds.y);
      
      double lon = swBounds.x+(percentLeft*lonWidth);
      double lat = neBounds.y-(percentDown*latHeight);
      
      //System.out.println("calculated drop point: lon: "+lon+" lat: "+lat);
       showDropWindow(lat,lon,(MapFlag)c);
    }
    
    @Override
    public AcceptCriterion getAcceptCriterion()
    {
      return AcceptAll.get();
    }    
  }
  
  @SuppressWarnings("serial")
  private void showDropWindow(final double lat, final double lon, MapFlag flag)
  {
    final DropWindow win = new DropWindow(lat,lon,flag);
    UI.getCurrent().addWindow(win);
    win.center();
    win.addCloseListener(new CloseListener()
    {
      @Override
      public void windowClose(CloseEvent e)
      {
        MapFlag flag = win.newFlag;
        if(flag != null) {   // meaning save clicked, not cancel       
          GoogleMapMarker gmm = new GoogleMapMarker();  // our db object
          gmm.setLat(lat);
          gmm.setLon(lon);
          gmm.setIconUrl(win.getIconUrl());
          gmm.setPopupContent(win.getPopupContent());
          gmm.setDraggable(true);  // since we've been dropped we're in the right mode
          googleMapWidget.addMarker(new MmowgliMarker(googleMapWidget,markerIdx++,gmm));
          savePanel.saveMarkerButt.setEnabled(true);
        }
      }      
    });
  }
  @SuppressWarnings("serial")
  private void showDropWindow(final MmowgliMarker mm)
  {
    final DropWindow win = new DropWindow(mm);
    UI.getCurrent().addWindow(win);
    win.center();
    win.addCloseListener(new CloseListener()
    {
      @Override
      public void windowClose(CloseEvent e)
      {
        // Think we can just update our marker
        MapFlag flag = win.newFlag;
        if(flag != null) {
          
          GoogleMapMarker gmm = mm.wrappee;  // meaning save clicked, not cancel
          gmm.setLat(win.lat);
          gmm.setLon(win.lon);
          gmm.setIconUrl(win.getIconUrl());
          gmm.setPopupContent(win.getPopupContent());
          gmm.setDraggable(true);
          
          mm.setLatLng(gmm.getLatLon());
          mm.setIconUrl(gmm.getIconUrl());
          mm.setPopupContent(gmm.getTitle(),gmm.getPopupContent());
          
          mm.popContent.requestRepaint();
          savePanel.saveMarkerButt.setEnabled(true);
        }
      }      
    });
  }

  @SuppressWarnings("serial")
  class PopupContent extends VerticalLayout
  {
    private MmowgliMarker marker;
    Label titleLabel;
    Label contentLabel;
    PopupContent(MmowgliMarker mark, String labtxt, String txt)
    {
      this.marker = mark;

      setSizeUndefined(); //setHeight("75px");
      setWidth("215px");  // about the min width
      setSpacing(true);

      titleLabel = new Label(labtxt);
      addComponent(titleLabel);
      setComponentAlignment(titleLabel,Alignment.TOP_CENTER);
      contentLabel = new HtmlLabel(txt);
      contentLabel.setWidth("99%");
      addComponent(contentLabel);
      setComponentAlignment(contentLabel,Alignment.TOP_LEFT);

      makeFooterTools();
    }
    
    public void setToolsVisible(boolean yn)
    {
      footerSpacer.setVisible(yn);
      buttLayout.setVisible(yn); 
    }
       
    private Label footerSpacer;
    private HorizontalLayout buttLayout;
    
    private void makeFooterTools()
    {
      addComponent(footerSpacer = new Label());
      footerSpacer.setHeight("12px");

      buttLayout = new HorizontalLayout();
      buttLayout.setMargin(false);
      addComponent(buttLayout);
      setComponentAlignment(buttLayout,Alignment.MIDDLE_CENTER);

      Button deleteButt = new Button("delete this marker");
      deleteButt.setStyleName(BaseTheme.BUTTON_LINK);
      deleteButt.setWidth("110px");   // can't get vaadin to size it properly
      buttLayout.addComponent(deleteButt);
      
      Label sp;
      buttLayout.addComponent(sp = new Label());
      sp.setWidth("10px");
     
      Button editButt = new Button("edit this text");
      editButt.setStyleName(BaseTheme.BUTTON_LINK);
      editButt.setWidth("70px");// can't get vaadin to size it properly
      buttLayout.addComponent(editButt);
      
      deleteButt.addClickListener(new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
          if (marker != null) {
            
       //     googleMapWidget.removeMarker(marker);
            marker = null; 
            savePanel.saveMarkerButt.setEnabled(true);
          }
        }
      });
      editButt.addClickListener(new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
         // showDropWindow(marker);         
        }
      });
    }
    public String getContentText()
    {
      return contentLabel.getValue().toString();
    }    
    public String getTitle()
    {
      return titleLabel.getValue().toString();
    }
    public void setContentText(String text)
    {
      contentLabel.setValue(text);     
    }
    public void setTitle(String title2)
    {
      titleLabel.setValue(title2);      
    }
  }
  
  class MyMarkerMovedListener implements MarkerMovedListener
  {
    @Override
    public void markerMoved(Marker movedMarker)
    {
      savePanel.saveMarkerButt.setEnabled(true);
      savePanel.canMarkerButt.setEnabled(true);
    }   
  }*/
  /*
  class MyMarkerClickListener implements MarkerClickListener
  {
    @Override
    public void markerClicked(Marker clickedMarker)
    {
   }   
  }
  */
  /*
  class MyMapMoveListener implements MapMoveListener
  {
    @Override
    public void mapMoved(int newZoomLevel, Point2D.Double newCenter,Point2D.Double boundsNE, Point2D.Double boundsSW)
    {
     // showSavePanel();  // will only show save if a marker has been updated
      if(++throwAwayMoveEvents <= THROWAWAYMOVES)
        return;
      
      if(editingOK) {
        if(!savePanel.saveLocButt.isEnabled()) {
          savePanel.saveLocButt.setEnabled(true);
          savePanel.canMarkerButt.setEnabled(true);
        }
        if(!messageSent) {
          sendStartEditMessage( DBGet.getUser(app.getUser()).getUserName()+" may be editing the action plan map.");    
          messageSent = true;
        }
      }
    }    
  }
*/ 
  @SuppressWarnings("serial")
  class MapSaveListener
  {
    public ClickListener mapLocListener = new ClickListener()
    {
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        ActionPlan ap = ActionPlan.getTL(apId);
        mmowgliMap = ap.getMap();
     
      //  mmowgliMap.setLatLonCenter(googleMapWidget.getCenter());
      //  mmowgliMap.setZoom(googleMapWidget.getZoom());
        GoogleMap.updateTL(mmowgliMap);
        ActionPlan.updateTL(ap);
        User u = Mmowgli2UI.getGlobals().getUserTL();
        GameEventLogger.logActionPlanUpdateTL(ap, "map changed", u.getId());

        savePanel.saveLocButt.setEnabled(false);
        if(!savePanel.saveMarkerButt.isEnabled())
          savePanel.canMarkerButt.setEnabled(false);
        //messageSent = false;
        HSess.close();
      }
    };
    
    public ClickListener cancelListener = new ClickListener()
    {
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        reloadMarkers(ActionPlan.getTL(apId));

        savePanel.saveMarkerButt.setEnabled(false);
        savePanel.saveLocButt.setEnabled(false);
        savePanel.canMarkerButt.setEnabled(false);
        HSess.close();
      }     
    };
  /*  
    public ClickListener mapMarkerListener = new ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        ActionPlan ap = ActionPlan.get(apId);
        mmowgliMap = ap.getMap();
        mmowgliMap.setDescription(googleMapWidget.getDescription());

        List<Marker> lis =googleMapWidget.getMarkers();
        
        List<GoogleMapMarker> dbMarkerList = mmowgliMap.getMarkers();
        for(GoogleMapMarker gmm : dbMarkerList)
          GoogleMapMarker.delete(gmm);
        dbMarkerList.clear();
        
        for(Marker m : lis) {
          GoogleMapMarker gmm = new GoogleMapMarker();
          gmm.setIconAnchorXY(m.getIconAnchor());
          gmm.setLatLon(m.getLatLng());
          PopupContent pop = (PopupContent)m.getInfoWindowContent()[0].getContent();
          gmm.setPopupContent(pop.getContentText());
          gmm.setIconUrl(m.getIconUrl());
          gmm.setTitle(m.getTitle());
          GoogleMapMarker.save(gmm);
          dbMarkerList.add(gmm);
        }
        GoogleMap.update(mmowgliMap);
        ActionPlan.update(ap);
        User u = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
        GameEventLogger.logActionPlanUpdate(ap, "map marker changed", u.getId()); //u.getUserName());

        savePanel.saveMarkerButt.setEnabled(false);
        if(!savePanel.saveLocButt.isEnabled())
          savePanel.canMarkerButt.setEnabled(false);
      }
    };     
 */
    }

  @Override
  public boolean actionPlanUpdatedOobTL(Serializable apId)
  {
    // This panel does NOT do a dynamic update of someone else's changes....too problematic for losing work.
    return false;
  }
    
  @Override
  public void setICanEdit(boolean yn)
  {
    editingOK = yn;
    toggleSaveButtPan(yn);
    toggleFlags(yn);
    togglePopupsAndMarkers(yn);
  }
  
  @SuppressWarnings("serial")
  static class MapFlag extends DragAndDropWrapper
  {
    public static MapFlag getMapFlag(int count, String caption, String urlBase)
    {
      return getMapFlag(count,caption,urlBase, false);
    }
    public static MapFlag getMapFlag(int count, String caption, String urlBase, boolean isAlpha)
    {
      String url = urlBase;
      if(count > 1) {
        if(isAlpha)
          url = url.replace(ALPHATAG, "A");
        else
          url = url.replace(NUMBERTAG, "00");
      }
      Resource res = new ExternalResource(url);
      Embedded embedded = new Embedded(caption,res);
      embedded.setWidth("27px");  // These 2 shouldn't be required, and AREN'T on my desktop machine.
      embedded.setHeight("27px");
      return new MapFlag(count,embedded,urlBase, isAlpha);
    }
    
    public static MapFlag deriveMapFlag(String iconUrl)
    {
      boolean matched = false;
      String group=null;
      int row;
      for(row = 0; row<flagData.length; row++) {
        Pattern p = Pattern.compile(flagData[row][2].toString());  // reg expr = indx 2
        Matcher m = p.matcher(iconUrl);
        matched = m.matches();
        if(matched) {  // got a match
          if(m.groupCount() == 0)
            ; //System.out.println("Whole thing matches");
          else
            group = m.group(1);
          break;
        }
      }
      
      if(! matched)
        return getMapFlag(1,null,flagData[0][1].toString()); //redblank
            
      int range = (Integer)flagData[row][0];
      if(range == -1)
        return getMapFlag(1,null,flagData[row][1].toString());
      
      MapFlag mf = getMapFlag(range,null,flagData[row][1].toString(),!Character.isDigit(group.charAt(0)));
      if(group != null)
        mf.indx = decodeGroup(group);
      
      return mf;
    }
    
    private static int decodeGroup(String s)
    {
      if(Character.isDigit(s.charAt(0)))
          return Integer.parseInt(s);
      return Character.getNumericValue(s.charAt(0));
    }
    
    Embedded embedded;
    int count;
    String urlBase;
    boolean isAlpha;
    int indx = -1;
    private MapFlag(int count, Embedded comp, String urlBase, boolean isAlpha)
    {
      this(count,comp,urlBase,isAlpha,-1);
    }
    private MapFlag(int count, Embedded comp, String urlBase, boolean isAlpha, int idx)
    {
      super(comp);
      this.setSizeUndefined();
      this.count = count;
      this.embedded = comp;
      this.urlBase = urlBase;
      this.isAlpha = isAlpha;
      this.indx = idx;
      
      setDragStartMode(DragStartMode.COMPONENT);
    }
    public String urlFromCount(Object obj)
    {
      int val=0;
      if(obj instanceof Integer)
        val = (Integer)obj;
      else if(obj instanceof Double)
        val = ((Double)obj).intValue();
      else if(obj instanceof Float)
        val = ((Float)obj).intValue();
      count = val;
      
      if(isAlpha)
        return urlBase.replace(ALPHATAG, alphaFromCount(val));
      else
        return urlBase.replace(NUMBERTAG,fmtr.format(val));
    }
    
    private DecimalFormat fmtr = new DecimalFormat("00");
    
    static char[]  charArray = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    String alphaFromCount(int idx)
    {
       if(idx < 1)
         idx = 1;
       if(idx>26)
         idx = 26;
      return String.valueOf(charArray[idx-1]);
    }
    public MapFlag clone()
    {
      Embedded emb = new Embedded(null,embedded.getSource());
      return new MapFlag(count, emb, urlBase, isAlpha);
    }
  }
 /* 
  class MmowgliMarker extends com.google.maps.gwt.client.BasicMarker
  {
    GoogleMapMarker wrappee;
    PopupContent popContent;
    int numberIndx;
    
    public MmowgliMarker(GoogleMap googleMap, long idx, GoogleMapMarker gmm)
    {
      super(idx,gmm.getLatLon(),gmm.getTitle());
      wrappee=gmm;
      
      super.setIconUrl    (gmm.getIconUrl());
      super.setIconAnchor (gmm.getIconAnchorXY());
      super.setVisible    (gmm.isVisible());
      super.setDraggable  (gmm.isDraggable());
      
      super.setInfoWindowContent(googleMap,popContent = new PopupContent(this,gmm.getTitle(),gmm.getPopupContent()));
      
      numberIndx = MapFlag.deriveMapFlag(gmm.getIconUrl()).indx;
    }
    
    @Override
    public void setIconUrl(String imageUrl)
    {
      super.setIconUrl(imageUrl);
      wrappee.setIconUrl(imageUrl);
    }
    
    @Override
    public void setIconAnchor(java.awt.geom.Point2D.Double iconAnchor)
    {
      super.setIconAnchor(iconAnchor);
      wrappee.setIconAnchorXY(iconAnchor);
    }
    
    public void setPopupContent(String title, String text)
    {
      popContent.setTitle(title);
      popContent.setContentText(text);
    }
  }

  @SuppressWarnings("serial")
  class MmowgliMapWidget extends com.vaadin.tapio.googlemaps.GoogleMap
  {
    ArrayList<Marker> markers = new ArrayList<Marker>();
      
    public MmowgliMapWidget(Point2D.Double center, int zoom, String apiKey)
    {
      super(center, zoom, apiKey);
    }

    @Override
    public void addMarker(Marker marker)
    {
      markers.add(marker);
      super.addMarker(marker);
    }
    
    public List<Marker> getMarkers()
    {
      return markers;
    }

    @Override
    public void removeMarker(Marker marker)
    {
      markers.remove(marker);
      super.removeMarker(marker);      
    }

    @Override
    public void removeAllMarkers()
    {
      markers.clear();
      super.removeAllMarkers();
    }    
  }
  */ 

  /*   class DropWindow extends Window
  {
    Label numberLab;
    double lat,lon;
    private MapFlag flag;
    public MapFlag newFlag;
    TextArea popupContentTextArea;
    Embedded emb;
    Label popupContentTextLab;
    Slider slider;
  
    public DropWindow(MmowgliMarker mm)
    {
      super("Edit Marker");
      setWidth("290px");
      setHeight("290px");
      this.lat = mm.getLatLng().y;
      this.lon = mm.getLatLng().x;
      this.flag = MapFlag.deriveMapFlag(mm.getIconUrl());
      initGui();
      int flagIdx = flag.indx;
      flagIdx = (flagIdx==-1?0:flagIdx);
      
      try{slider.setValue((double)flagIdx);}
      catch(Exception ex) {
        System.err.println("Error in ActionPlanPageTabMap.DropWindow");
        System.err.println(ex.getClass().getSimpleName()+"/ "+ex.getLocalizedMessage());
      }
      
      PopupContent popCont = (PopupContent)mm.getInfoWindowContent()[0].getContent();
      popupContentTextArea.setValue(popCont.getContentText());
    }
    public DropWindow(double lat, double lon, MapFlag flag)
    {
      super("Add Marker");
      setWidth("290px");
      setHeight("290px");
      
      this.lat = lat;
      this.lon = lon;
      this.flag = flag.clone();
      initGui();
    }
  
    private void initGui()
    {
      VerticalLayout vl = getMyContent();
      vl.setMargin(true);
      vl.setSpacing(true);
      vl.setSizeFull();
      setContent(vl);     
    }
    
    private VerticalLayout getMyContent()
    {
      VerticalLayout vl = new VerticalLayout();
      Label popupContentTextLab;
      vl.addComponent(popupContentTextLab=new Label("Enter popup text"));
      popupContentTextLab.setSizeUndefined();
      
      popupContentTextArea = new TextArea();;
      popupContentTextArea.setSizeFull();
      vl.addComponent(popupContentTextArea);
      vl.setExpandRatio(popupContentTextArea, 1.0f);
      HorizontalLayout iconHL = new HorizontalLayout();
      vl.addComponent(iconHL);
      iconHL.setSpacing(true);
      iconHL.setMargin(false);
      iconHL.setWidth("100%");
      vl.setComponentAlignment(iconHL, Alignment.TOP_CENTER);
      emb = new Embedded(null,flag.embedded.getSource());
      iconHL.addComponent(emb);
      if(flag.count > 1) {
        String sliderLab;
        int min,max;
        if(flag.isAlpha) {
          numberLab = new Label("A");
          numberLab.setSizeUndefined();
          sliderLab = "Select between A and "+flag.alphaFromCount(flag.count);
          min = 1;
          max = flag.count;
        }
        else {
          numberLab = new Label("0");
          numberLab.setSizeUndefined();
          sliderLab = "Select between 0 and "+(flag.count-1);
          min = 0;
          max = flag.count-1;
        }
        iconHL.addComponent(numberLab);
        slider = new Slider(sliderLab);
        slider.setWidth("100%");
        slider.setMin(min);
        slider.setMax(max);
        slider.setImmediate(true);
        slider.addValueChangeListener(new ValueChangeListener()
        {
          @Override
          public void valueChange(ValueChangeEvent event)
          {
            Object obj = event.getProperty().getValue();
            if(obj instanceof Integer)
              flag.indx = (Integer)obj;
            
            emb.setSource(new ExternalResource(flag.urlFromCount(obj)));          
          }          
        });
        iconHL.addComponent(slider);
        iconHL.setExpandRatio(slider, 1.0f);
      }
      HorizontalLayout buttLayout = new HorizontalLayout();
      buttLayout.setWidth("100%");
      vl.addComponent(buttLayout);
      buttLayout.setMargin(false);
      buttLayout.setSpacing(true);
      
      Button cancelButt = new Button("Cancel",new CancelListener());
      Button saveButt = new Button("Save",new SaveListener());
      Label sp;
      buttLayout.addComponent(sp = new Label());
      sp.setWidth("1px");
      buttLayout.setExpandRatio(sp, 1.0f);
      buttLayout.addComponent(cancelButt);
      buttLayout.addComponent(saveButt);
      return vl;
    }
    
    public String getPopupContent()
    {
      return popupContentTextArea.getValue().toString();
    }
    
    public String getIconUrl()
    {
      return ((ExternalResource)emb.getSource()).getURL();
    }
    
    class CancelListener implements ClickListener
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        newFlag = null;
        UI.getCurrent().removeWindow(DropWindow.this);
      }     
    }

    class SaveListener implements ClickListener
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        newFlag = flag;
        UI.getCurrent().removeWindow(DropWindow.this);
      }      
    }
  }
   */ 
  public static class MapSaveButtPan extends HorizontalLayout
  {
    private static final long serialVersionUID = 1L;
    public static int SAVELOC_BUTTON = 0;
    public static int SAVEMARKER_BUTTON = 1;

    Button saveLocButt, saveMarkerButt, canMarkerButt;;
   
    public MapSaveButtPan()
    {
      setSpacing(true);
      setMargin(false);
      Label lab;
      addComponent(lab = new Label());
      lab.setWidth("1px");
      setExpandRatio(lab, 1.0f);
      saveLocButt = new Button("Save map location");
      addComponent(saveLocButt);
      saveLocButt.setStyleName(Reindeer.BUTTON_SMALL);
      saveLocButt.addStyleName("m-greenbutton");
      saveLocButt.setEnabled(false);
      saveLocButt.setImmediate(true);
      saveLocButt.setDescription("Saves map dimensions so map is initially shown this way for all viewers.  Without saving, the map reverts to its startup size and zoom when you leave this page.");

      saveMarkerButt = new Button("Save marker changes");
      addComponent(saveMarkerButt);
      saveMarkerButt.setStyleName(Reindeer.BUTTON_SMALL);
      saveMarkerButt.addStyleName("m-greenbutton");
      saveMarkerButt.setEnabled(false);
      saveMarkerButt.setImmediate(true);
      saveMarkerButt.setDescription("Save marker locations and text.  Without saving, leaving this action plan then returning will display previous marker information.");

      canMarkerButt = new Button("Cancel");
      addComponent(canMarkerButt);
      canMarkerButt.setStyleName(Reindeer.BUTTON_SMALL);
      canMarkerButt.addStyleName("m-greenbutton");
      canMarkerButt.setEnabled(false);
      canMarkerButt.setImmediate(true);
      canMarkerButt.setDescription("Cancel pending marker and position changes");
          
      addComponent(lab=new Label());
      lab.setWidth("5px");
    }
    
    public void setClickHearers(ClickListener saveLocLis, ClickListener saveMarkerLis, ClickListener cancelLis)
    {
      saveMarkerButt.addClickListener(saveMarkerLis);
      saveLocButt.addClickListener(saveLocLis);
      canMarkerButt.addClickListener(cancelLis);
    }
    
    public void setClickHearer(ClickListener lis)
    {
      setClickHearers(lis,lis,lis);
    }
  }
}
