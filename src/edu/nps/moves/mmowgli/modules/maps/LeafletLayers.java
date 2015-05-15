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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LWmsLayer;

public class LeafletLayers
{
  public static String LAYERDATAFILE = "LeafletLayersExportedAsTabDelimited.txt"; // in this package
  private static HashMap<String,LeafletProvider> providers;

  public static class LeafletProvider
  {
    public String key = null;
    public Boolean baseLayer = false;
    public Boolean active = false;
    public String prettyName = "";
    public String link = "";
    public String attribution = "";
    public Integer maxZoom = null;
    public Double opacity = null;
    public String subdomains = null;
    public Boolean wms = false;
    public Boolean wmstransparent = false;
    public String wmsformat = "";
    public String wmslayers = "";

    public LeafletProvider()
    {
    }

    public LeafletProvider(String key, String prettyName, Boolean baseLayer, Boolean active,
        String link, String attribution, Integer maxZoom, Double opacity,
        String subdomains, Boolean wms, Boolean wmstransparent, String wmsformat, String wmslayers)
    {
      this.key = key;
      this.baseLayer = baseLayer;
      this.active = active;
      this.prettyName = prettyName;
      this.link = link;
      this.attribution = attribution;
      this.maxZoom = maxZoom;
      this.opacity = opacity;
      this.subdomains = subdomains;
      this.wms = wms;
      this.wmstransparent = wmstransparent;
      this.wmsformat = wmsformat;
      this.wmslayers = wmslayers;
    }
  }

  // Data
  // The rows in the tab-delimited data file, in this package/directory on the classpath, are in this order
  public static  int KEY = 0, PRETTYNAME = 1, BASELAYER = 2, ACTIVE = 3, LINK = 4,
      ATTRIBUTION = 5, MAXZOOM = 6, OPACITY = 7, SUBDOMAINS = 8,
      WMS = 9, WMSTRANSPARENT = 10, WMSFORMAT = 11, WMSLAYERS = 12;
  
  //@formatter:off
  static {
    providers = new HashMap<String, LeafletProvider>();

    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(LeafletLayers.class.getResourceAsStream(LAYERDATAFILE)));
      boolean headerRead = false;
      while(br.ready()) {
        String line = br.readLine();
        if(!headerRead) {
          headerRead = true;
          continue;
        }
        String[] sa = line.split("\t");
        
        if(sa.length > 0 && sa[0].startsWith("//"))    //comment
          continue;
        
        LeafletProvider prov = new LeafletProvider();

        if(sa.length>=KEY+1) {
          prov.key = sa[KEY];
          if (sa.length>=PRETTYNAME+1) {
            prov.prettyName = handleString(sa[PRETTYNAME]);
            if(sa.length>=BASELAYER+1) {
              prov.baseLayer = handleBoolean(sa[BASELAYER]);
              if(sa.length>=ACTIVE+1) {
                prov.active = handleBoolean(sa[ACTIVE]);
                if(sa.length>=LINK+1) {
                  prov.link = handleString(sa[LINK]);
                  if(sa.length>=ATTRIBUTION+1) {
                    prov.attribution = handleString(sa[ATTRIBUTION]);
                    if(sa.length>=MAXZOOM+1) {
                      prov.maxZoom = handleInteger(sa[MAXZOOM]);
                      if(sa.length>=OPACITY+1) {
                        prov.opacity=handleDouble(sa[OPACITY]);
                        if(sa.length>=SUBDOMAINS+1) {
                          prov.subdomains=handleString(sa[SUBDOMAINS]);
                          if(sa.length>=WMS+1) {
                            prov.wms = handleBoolean(sa[WMS]);
                            if(sa.length>=WMSTRANSPARENT+1) {
                              prov.wmstransparent = handleBoolean(sa[WMSTRANSPARENT]);
                              if(sa.length>= WMSFORMAT) {
                                prov.wmsformat = handleString(sa[WMSFORMAT]);
                                if(sa.length>= WMSLAYERS) {
                                  prov.wmslayers = handleString(sa[WMSLAYERS]);
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }      
        providers.put(prov.key, prov);
      } 
    }                 
    catch(Throwable ex) {
      System.err.println("Program data error in LeafletLayers: "+ex.getClass().getSimpleName()+" / "+ex.getLocalizedMessage());
    }
  }
  
  private static String handleString(String s)
  {
    return s;
  }
  
  private static Boolean handleBoolean(String b)
  {
    if(b == null || b.length()<=0)
      return false;
    return Boolean.parseBoolean(b);
  }
  
  private static Integer handleInteger(String i)
  {
    if(i == null || i.length()<=0)
      return null;
    return Integer.parseInt(i);
  }
  
  private static Double handleDouble(String d)
  {
    if(d == null || d.length()<=0)
      return null;
    return Double.parseDouble(d);
  }
  
  public static interface MLayer
  {
    public boolean isActive();
    public void setActive(boolean b);
    public boolean isBaseLayer();
    public void setBaseLayer(boolean b);
    public String getHandle();
  }
  
  public static class MLTileLayer extends LTileLayer implements MLayer
  {
    private static final long serialVersionUID = 1L;
    private boolean baseLayer;
    private String handle;
    MLTileLayer(String link, String handle)
    {
      super(link);
      this.handle = handle;
    }
    public boolean isActive()
    {
      return getState().active;
    }
    public boolean isBaseLayer()
    {
      return baseLayer;
    }
    public void setBaseLayer(boolean b)
    {
      baseLayer = b;
    }
    public String getHandle()
    {
      return handle;
    }
    public void setActive(boolean b)
    {
      super.setActive(b);
    }
  }
  
  public static class MLWmsLayer extends LWmsLayer implements MLayer
  {
    private static final long serialVersionUID = 1L;
    private boolean baseLayer;
    private String handle;
    MLWmsLayer(String handle)
    {
      super();
      this.handle = handle;
    }
    public boolean isActive()
    {
      return getState().active;
    }
    public boolean isBaseLayer()
    {
      return baseLayer;
    }
    public void setBaseLayer(boolean b)
    {
      baseLayer = b;
    }
    public String getHandle()
    {
      return handle;
    }
    public void setActive(boolean b)
    {
      super.setActive(b);
    }
  }

  public static LTileLayer installProvider(String key, LMap map) throws Exception
  {
    LeafletProvider prov = providers.get(key);
    if (prov == null)
      throw new Exception("Unrecognized provider key");
    LTileLayer tileLayer;
    if(prov.wms) {
      tileLayer = new MLWmsLayer(key);
      tileLayer.setUrl(prov.link);
      ((MLWmsLayer)tileLayer).setBaseLayer(prov.baseLayer);
    }
    else {
      tileLayer = new MLTileLayer(prov.link,key);
      ((MLTileLayer)tileLayer).setBaseLayer(prov.baseLayer);
    }
    
    if(prov.attribution != null && prov.attribution.length()>0)
      tileLayer.setAttributionString(prov.attribution);
    if(prov.maxZoom != null)
      tileLayer.setMaxZoom(prov.maxZoom);
    if(prov.opacity != null)
      tileLayer.setOpacity(prov.opacity);
    if(prov.subdomains != null && prov.subdomains.length()>0)      
      tileLayer.setSubDomains(prov.subdomains);
    
    if(prov.wms){
      ((LWmsLayer)tileLayer).setTransparent(prov.wmstransparent);
      if(prov.wmsformat != null && prov.wmsformat.length()>0)
         ((LWmsLayer)tileLayer).setFormat(prov.wmsformat);
      if(prov.wmslayers != null && prov.wmslayers.length()>0)        
        ((LWmsLayer)tileLayer).setLayers(prov.wmslayers);
    }
    if(prov.baseLayer) {
      tileLayer.setActive(prov.active);
      map.addBaseLayer(tileLayer, prov.prettyName);
      tileLayer.bringToBack();
    }
    else {
      tileLayer.setActive(prov.active);
      map.addOverlay(tileLayer, prov.prettyName);
      tileLayer.bringToFront();  // overlays must be above base
    }

    return tileLayer;
  }
  
  public static HashMap<String,MLayer> installAllProviders(LMap map) throws Exception
  {
    HashMap<String,MLayer> retArr = new HashMap<>();
    
    List<String> lis = new ArrayList<String>(providers.keySet());
    Collections.sort(lis);

    for(String key : lis) {
      LTileLayer tileLayer = installProvider(key,map);
      retArr.put(key, (MLayer)tileLayer);
    }
    return retArr;
  }
}
