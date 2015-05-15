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

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * MmowgliMap.java
 * Created on May 27, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class OpenLayersMap extends VerticalLayout implements MmowgliComponent, View
{
  private static final long serialVersionUID = 468319872706075732L;
  
  private String title;
  
  public static String DEF_TITLE_FIRST_PART = "<b style=\"color:#4F4F4F;font-family:'Arial';font-size:2.0em;line-height:150%;margin-left:20px;\">";
  public static String DEF_TITLE_LAST_PART  = "</b>";
    
  public static class MapParms
  {
    public String msid = "";
    public double lat;
    public double lon;
    public int zoom;
    public MapParms(){}
    public MapParms(double lat, double lon, int zoom, String msid)
    {
      this.lat = lat; this.lon = lon; this.zoom = zoom; this.msid = msid;
    }
  }

  public OpenLayersMap()
  {
    this(DEF_TITLE_FIRST_PART +Game.getTL().getMapTitle()+DEF_TITLE_LAST_PART);
  }
  
  public OpenLayersMap(String title)
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
    setWidth("100%");
    addStyleName("m-marginleft-20");
    
     
    Label lab;
    addComponent(lab=new HtmlLabel(title));
    lab.setWidth(null);
    setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
    
    CssLayout cssLay = new CssLayout();
    cssLay.addStyleName("m-greybackground");
    cssLay.addStyleName("m-darkgreyborder");
    cssLay.setWidth("960px");
    cssLay.setHeight("600px");
    cssLay.setId("mmowgliMap");
    addComponent(cssLay);
    setComponentAlignment(cssLay,Alignment.TOP_CENTER);
    
    
    /* See http://wiki.openstreetmap.org/wiki/OpenLayers_Simple_Example
       This requires the  "http://openlayers.org/api/OpenLayers.js" file to be loaded:
       see the annotation in Mmowgli2UI.java. */
    
/* One example layer:
    String js = 
    "var m_map = new OpenLayers.Map('mmowgliMap');"+
    "var m_wms = new OpenLayers.Layer.WMS( \"OpenLayers WMS\","+
        "\"http://vmap0.tiles.osgeo.org/wms/vmap0\", {layers: 'basic'} );"+
    "m_map.addLayer(m_wms);"+
    "m_map.zoomToMaxExtent();";
*/    
    
    String jsOSM = 

   "mMap = new OpenLayers.Map('mmowgliMap');"+
   
  //  "shadeLayer = new OpenLayers.Layer.WMS("+
  //  "\"Shaded Relief\"," +   
  //  "\"http://ims.cr.usgs.gov:80/servlet19/com.esri.wms.Esrimap/USGS_EDC_Elev_NED_3\","+
  //  "{layers: HR-NED.IMAGE, reaspect: false, transparent: true, visibility: false}); "+ 
     
   "var osLay        = new OpenLayers.Layer.OSM();"+      
   "var fromProjection = new OpenLayers.Projection(\"EPSG:4326\");"+   // Transform from WGS 1984
   "var toProjection   = new OpenLayers.Projection(\"EPSG:900913\");"+ // to Spherical Mercator Projection
   "var position       = new OpenLayers.LonLat(-121.875267, 36.599878).transform( fromProjection, toProjection);"+
   "var zoom           = 15;"+

  " mMap.addLayer(osLay);"+
  //" mMap.addLayer(shadeLayer);"+
  //" mMap.addControl(new OpenLayers.Control.LayerSwitcher());"+
  " mMap.setCenter(position, zoom );";
    
    /*
  String worldWind = 
  "mMap = new OpenLayers.Map('mmowgliMap', {'maxResolution': .28125, tileSize: new OpenLayers.Size(512, 512)});"+
 "var osLay        = new OpenLayers.Layer.OSM();"+      

  "var ol_wms = new OpenLayers.Layer.WMS( \"OpenLayers WMS\", \"http://vmap0.tiles.osgeo.org/wms/vmap0?\", {layers: 'basic'} );"+
//  "var ww     = new OpenLayers.Layer.WorldWind( \"Bathy\",\"http://worldwind25.arc.nasa.gov/tile/tile.aspx?\", 36, 4,{T:\"bmng.topo.bathy.200406\"});"+
//  "var ww2    = new OpenLayers.Layer.WorldWind( \"LANDSAT\",\"http://worldwind25.arc.nasa.gov/tile/tile.aspx\", 2.25, 4,{T:\"105\"});"+
  "mMap.addLayers([osLay]); //,ol_wms]);"+ //, ww, ww2]);"+
//  "mMap.addControl(new OpenLayers.Control.LayerSwitcher());"+
"var fromProjection = new OpenLayers.Projection(\"EPSG:4326\");"+   // Transform from WGS 1984
"var toProjection   = new OpenLayers.Projection(\"EPSG:900913\");"+ // to Spherical Mercator Projection
"var position       = new OpenLayers.LonLat(-121.875267, 36.599878).transform( fromProjection, toProjection);"+
"var zoom           = 15;"+

  //"mMap.setCenter(new OpenLayers.LonLat(-71.4, 42.3), 6);"+
  " mMap.setCenter(position, zoom );"+

   ""; */
    
    JavaScript.getCurrent().execute(jsOSM);    
  }

  @Override
  public void enter(ViewChangeEvent event)
  { 
    Object key = HSess.checkInit();
    initGuiTL();
    HSess.checkClose(key);
  }

}
