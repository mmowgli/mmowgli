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

/**
 * DbUtils.java
 * Created on Dec 6, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class DbUtils
{
  public static String len255(String s)
  {
    s = (s==null)?"" : s;

    if(s.length()>= 255)
      s = s.substring(0,253);
    
    return s;
  }

  public static String len511(String s)
  {
    s = (s==null)?"" : s;

    if(s.length()>= 511)
      s = s.substring(0,509);
    
    return s;
  }
  
  /*
   * Using the post-update event listener, I found that update events were not being generated on updates to the "joins".  For instance,
   * if I mark a card as a favorite, that card goes into the favorites list in the User object.  Hibernate adds the card and user ids to
   * the "User_FavoriteCards" join table.  Neither the user nor the carc db row is changed, so apparently no event gets generated for "post-update".
   * This routine, called from every db class update routine bumps an unused field in the row (linkedin_id) and that forces the event
   * Update...use the evict.
   */
  
  public static void forceUpdateEvent(Object o)
  {
    HSess.get().evict(o);
/*    
    try {
      Method setter = o.getClass().getDeclaredMethod("setLinkedInId", String.class);

      if (setter == null) {
        MSysOut.println("No setter for update-forcing in " + o.getClass().getSimpleName());
      }
      else {
        Double junk = Math.random();
        setter.invoke(o, Double.toString(junk));   // should do it.
      }
    }
    catch (Exception ex) {
      MSysOut.println("Exception forcing db update for class " + o.getClass().getSimpleName());
    }
    */
  }

}
