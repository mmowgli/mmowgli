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

package edu.nps.moves.mmowgli;

import com.vaadin.annotations.*;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;

/**
 * Mmowgli2UISubsequent.java
 * Created on Apr 28, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

//The following is done in web.xml to allow different values for different deployments
@Push(value=PushMode.MANUAL,transport=Transport.LONG_POLLING) //potentially overridden in  web.xm

//This preserves the UI across page reloads
@PreserveOnRefresh
@StyleSheet({"https://fonts.googleapis.com/css?family=Nothing+You+Could+Do", // jason-style-handwriting
          "https://fonts.googleapis.com/css?family=Varela+Round", // like vagabond
          "https://fonts.googleapis.com/css?family=Special+Elite",// typewriter
          "https://fonts.googleapis.com/css?family=Open+Sans:700&subset=latin,latin-ext", // army sci tech
          "https://fonts.googleapis.com/css?family=Gentium+Book+Basic&subset=latin,latin-ext",
          "https://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css"})//ditto
//Loading the google code this way (anno) runs into the X-Frame-Options SAMEORIGIN error
@JavaScript ({
           "https://platform.twitter.com/widgets.js",
           "http://openlayers.org/api/OpenLayers.js",
           "http://ol3js.org/en/master/build/ol.js",
           //"http://maps.google.com/maps/api/js?v=3&output=embed"})  // last one for openstrmap plus google layers
           //"https://maps.google.com/maps/api/js?v=3&key=AIzaSyBeWoPydbJRnvH0D8DnCCeLDP1VVPURKh0&sensor=false&output=embed"})
         })
@Theme("mmowgli2")
@Widgetset("edu.nps.moves.mmowgli.widgetset.Mmowgli2Widgetset")

public class Mmowgli2UISubsequent extends Mmowgli2UI
{
  private static final long serialVersionUID = -6366320429083964969L;
  public Mmowgli2UISubsequent()
  {
    super(false);
  }
}
