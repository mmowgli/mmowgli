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

package edu.nps.moves.mmowgli.modules.maps;

import java.util.*;

import org.vaadin.addon.leaflet.*;
import org.vaadin.addon.leaflet.control.LLayers;
import org.vaadin.addon.leaflet.shared.Point;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Extension;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.markers.HibernateUpdate;
import edu.nps.moves.mmowgli.markers.HibernateUserUpdate;
import edu.nps.moves.mmowgli.modules.maps.LeafletLayers.MLTileLayer;
import edu.nps.moves.mmowgli.modules.maps.LeafletLayers.MLWmsLayer;
import edu.nps.moves.mmowgli.modules.maps.LeafletLayers.MLayer;

/**
 * LeafletMap.java
 * Created on May 7, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
/* uses V-Leaflet Vaadin add-on */
public class LeafletMap extends VerticalLayout implements MmowgliComponent, View
{
  private static final long serialVersionUID = 6277983933298162890L;
  private String title;
  private LMap map= new LMap();
  HashMap<String,MLayer> layerMap;
  private PopupButton baseLayerPopup;
  private OptionGroup baseOptGr;
  private PopupButton overlayPopup;
  private OptionGroup overlayOpt;
  
  public static String DEF_TITLE_FIRST_PART = "<b style=\"color:#4F4F4F;font-family:'Arial';font-size:2.0em;line-height:150%;margin-left:20px;\">";
  public static String DEF_TITLE_LAST_PART  = "</b>";
 
  private boolean imAGuest = false;
  
  // Find providers at http://leaflet-extras.github.io/leaflet-providers/preview/index.html
  @HibernateSessionThreadLocalConstructor
  public LeafletMap()
  {
    this(DEF_TITLE_FIRST_PART +Game.getTL().getMapTitle()+DEF_TITLE_LAST_PART);
  }
  
  @HibernateSessionThreadLocalConstructor
  public LeafletMap(String title)
  {
    this.title = title;
  }
    
  @Override
  public void initGui()
  {
  }
  public void initGuiTL()
  {
    setSpacing(true);
    setSizeUndefined();
    setWidth("950px");
    addStyleName("m-marginleft-20"); 
    Label lab;
    
    HorizontalLayout hLay = new HorizontalLayout();
    hLay.setMargin(false); hLay.setSpacing(false);hLay.setWidth("100%");
    NativeButton butt;
    hLay.addComponent(butt=new NativeButton("go to default game location", new MyDefaultLocationListener()));
    hLay.setExpandRatio(butt, 0.5f);
    hLay.setComponentAlignment(butt, Alignment.BOTTOM_LEFT);

    hLay.addComponent(lab=new HtmlLabel(title));
    lab.setWidth(null);
    
    makeLayerPopups();
    HorizontalLayout popLay = new HorizontalLayout();
    popLay.setMargin(false); popLay.setSpacing(false); popLay.setWidth("100%");
    
    popLay.addComponent(lab = new Label());
    lab.setWidth("1px");
    popLay.setExpandRatio(lab, 1.0f);   
    popLay.addComponent(baseLayerPopup);
    popLay.addComponent(overlayPopup);
    
    hLay.addComponent(popLay);
    hLay.setComponentAlignment(popLay, Alignment.BOTTOM_RIGHT);
    hLay.setExpandRatio(popLay, 0.5f);
    
    addComponent(hLay);

    User me = Mmowgli2UI.getGlobals().getUserTL();
    this.imAGuest = me.isViewOnly() || me.isAccountDisabled();
    
    map.setAttributionPrefix("Powered by Leaflet with v-leaflet");
    map.addStyleName("m-greyborder");
    map.removeAllComponents();
   // map.addControl(new LScale());
    layerMap = installAllLayers(map);
    
    fillLayerPopupsTL(); // build the widgets
    
    setDefaultMapValuesTL(me);  // set default zoom, center, layers
  
    if(!imAGuest)
      setUserMapValuesTL(me);  // set zoom, center and layers from userID pref.
    
    setOptionGroupWidgetsFromLayerMap();// syncs up the widgets to match the active layers

    Collection<Extension> exts = map.getExtensions();
    LLayers llayers = null;
    for(Extension ex : exts)
      if(ex instanceof LLayers) {
        llayers = (LLayers)ex;
        break;
      }
    if(llayers != null)
      map.removeExtension(llayers);
    
    addComponent(map);

    setExpandRatio(map, 1);
    map.setHeight("600px");
    map.setWidth("100%");
    map.addMoveEndListener(new MyMoveEndListener());
  }
  
  private void makeLayerPopups()
  {
    baseLayerPopup = new PopupButton("Base layer");
    baseLayerPopup.addStyleName("popupbutton-legacy");
    overlayPopup = new PopupButton("Overlays");
    LayerChoiceListener lis = new LayerChoiceListener();
    
    initting=true;
    baseOptGr = new OptionGroup();
    baseOptGr.addValueChangeListener(lis);
    baseOptGr.setImmediate(true);
    baseOptGr.setNullSelectionAllowed(false);
    baseLayerPopup.setContent(baseOptGr);
    
    overlayOpt = new OptionGroup();
    overlayOpt.addValueChangeListener(lis);
    overlayOpt.setMultiSelect(true);
    overlayOpt.setImmediate(true);
    overlayOpt.setNullSelectionAllowed(true);
    overlayPopup.setContent(overlayOpt);
    initting=false;
  }
    
  private void setDefaultMapValuesTL(User me)
  {
    Game g = Game.getTL();
    activateLayersFromCSV(g.getMapLayersCSV());  //sets the active bit of the appropriate layers in the layer map
    
    map.setCenter(g.getMapLatitude(),g.getMapLongitude());
    map.setZoomLevel(g.getMapZoom());
  }
 
  private void fillLayerPopupsTL()
  {
    boolean oldinitting = initting;
    initting = true;
    
    Iterator<Component> itr = map.iterator();
    while (itr.hasNext()) {
      LTileLayer lay = (LTileLayer) itr.next();
      boolean baseLayer = false;
      String handle = "";
      if (lay instanceof MLTileLayer) {
        baseLayer = ((MLTileLayer) lay).isBaseLayer();
        handle = ((MLTileLayer) lay).getHandle();
      }
      if (lay instanceof MLWmsLayer) {
        baseLayer = ((MLWmsLayer) lay).isBaseLayer();
        handle = ((MLWmsLayer) lay).getHandle();
      }
      if (baseLayer) {
        baseOptGr.addItem(lay);
        baseOptGr.setItemCaption(lay, handle);
      }
      else {
        overlayOpt.addItem(lay);
        overlayOpt.setItemCaption(lay, handle);
      }
    }
    initting = oldinitting;
  }
  
  private void setOptionGroupWidgetsFromLayerMap()
  {
    Collection<MLayer> vals = layerMap.values();
    ArrayList<MLayer> overlays = new ArrayList<>();
    
    for(MLayer lay : vals) {
      if(lay.isActive()) {
        if(baseOptGr.containsId(lay))
          baseOptGr.setValue(lay);
        else if(overlayOpt.containsId(lay))
          overlays.add(lay);
      }
    }
    overlayOpt.setValue(overlays);
  }
/*
  private void setOptionGroupWidgetsFromDBTL()
  {
    User me = Mmowgli2UI.getGlobals().getUserTL();
    List<String> actives = me.getActiveMapLayers();
    ArrayList<MLayer> overlays = new ArrayList<>();
    for(String s : actives) {
      MLayer lay = layerMap.get(s);
      if(baseOptGr.containsId(lay))
        baseOptGr.setValue(lay);
      else if(overlayOpt.containsId(lay))
        overlays.add(lay);     
    }
    overlayOpt.setValue(overlays);
  }
 */ 
  private void activateLayersFromCSV(String s)
  {
    if(s == null)
      return;
    String[] sa = s.split(",");
    ArrayList<String> aLis = new ArrayList<>(sa.length);
    for(String layer : sa) {
      aLis.add(layer.trim());
    }
    _activateLayers(aLis);
  }

  private void setUserMapValuesTL(User me)
  {
    if (me == null)
      me = Mmowgli2UI.getGlobals().getUserTL();

    Float myLat = me.getMapCenterLatitude();
    Float myLon = me.getMapCenterLongitude();
    Integer myZoom = me.getMapZoom();
    if (myLat != null && myLon != null & myZoom != null) {
      map.setCenter(myLat, myLon);
      map.setZoomLevel(myZoom);
    }

    List<String> activeLayers = me.getActiveMapLayers();
    _activateLayers(activeLayers);
  }
  
  private void _activateLayers(List<String> activeLayers)
  {
    if(activeLayers.isEmpty())
      return;
    
    _clearActiveLayers();
    boolean oldinitting = initting;
    initting = true;
    for(String key : activeLayers) {
      MLayer lay = layerMap.get(key);      
      lay.setActive(true);
    }
    initting = oldinitting;
  }
  
  private void _clearActiveLayers()
  {
    Collection<MLayer> lays = layerMap.values();
    for (MLayer lay : lays)
      lay.setActive(false);
  }
  
  boolean initting = false;
  private class LayerChoiceListener implements ValueChangeListener
  {
    private static final long serialVersionUID = 1L;

    @Override
    public void valueChange(ValueChangeEvent event)
    {
      if(!initting) {
        Object prop = event.getProperty();  // this is the option group
        if(prop == baseOptGr) {
          Collection<?> allBaseLayers = baseOptGr.getItemIds();
          for(Object o : allBaseLayers) {
            ((AbstractLeafletLayer)o).setActive(false);
          }
          AbstractLeafletLayer lay = (AbstractLeafletLayer)event.getProperty().getValue();
          lay.setActive(true);
        }
        else /* if(prop == overlayOpt)*/ {
         Collection<?> allLayers = overlayOpt.getItemIds();
         for(Object o : allLayers) {
           ((AbstractLeafletLayer)o).setActive(false);
         }
          Set<?> checked = (Set<?>)event.getProperty().getValue();
          for(Object o : checked)
            ((AbstractLeafletLayer)o).setActive(true);
        }
        if(!imAGuest)
          updateDBActiveList();
      }
    }
  }
  
  @HibernateUpdate
  @HibernateUserUpdate
  private void updateDBActiveList()
  {
    Object key = HSess.checkInit();
    User me = Mmowgli2UI.getGlobals().getUserTL();
    List<String> lis= me.getActiveMapLayers();
    lis.clear();
    for(MLayer lay : layerMap.values()) {
      if(lay.isActive())
        lis.add(lay.getHandle());     
    }
    User.updateTL(me);
    HSess.closeAndCheckReopen(key);
  }

  @SuppressWarnings("serial")
  class MyDefaultLocationListener implements ClickListener
  {
    @Override
    @HibernateUpdate
    @HibernateUserUpdate
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      User me = Mmowgli2UI.getGlobals().getUserTL();
      me.setMapCenterLatitude(null);
      me.setMapCenterLongitude(null);
      me.setMapZoom(null);
      me.getActiveMapLayers().clear();
      
      setDefaultMapValuesTL(me);
      setOptionGroupWidgetsFromLayerMap();// syncs up the widgets to match the active layers

      User.updateTL(me);
      HSess.close();
    }    
  }
  
  class MyMoveEndListener implements LeafletMoveEndListener
  {
    @Override
    @HibernateUpdate
    @HibernateUserUpdate
    public void onMoveEnd(LeafletMoveEndEvent event)
    {
      if(imAGuest)
        return;
      HSess.init();
      User me = Mmowgli2UI.getGlobals().getUserTL();
      Point center = event.getCenter();
      me.setMapCenterLatitude(center.getLat().floatValue());
      me.setMapCenterLongitude(center.getLon().floatValue());
      me.setMapZoom(event.getZoomLevel());
      User.updateTL(me);
      HSess.close();
    }    
  }

  private HashMap<String,MLayer> installAllLayers(LMap map)
  {
    try {
      return LeafletLayers.installAllProviders(map);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.err.println("Error installing all leaflet layers / "+ex.getLocalizedMessage());
      return new HashMap<String,MLayer>(); // empty
    }   
  }
  
  @Override 
  public void enter(ViewChangeEvent event)
  {
    Object key = HSess.checkInit();
    initGuiTL();
    HSess.checkClose(key);
  }
}
