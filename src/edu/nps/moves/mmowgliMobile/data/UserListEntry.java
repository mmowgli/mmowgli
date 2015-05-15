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

package edu.nps.moves.mmowgliMobile.data;

import edu.nps.moves.mmowgli.db.User;

/**
 * CardListEntry.java
 * Created on Feb 18, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserListEntry extends ListEntry
{
  private static final long serialVersionUID = 2917669864501606058L;
  
  private User user;
  public UserListEntry(User user)
  {
    super(user);
    this.user = user;
  }
  
  public User getUser()
  {
    return user;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(obj instanceof User)
      return ((User)obj).getId() == getUser().getId();
    if(obj instanceof UserListEntry)
      return ((UserListEntry)obj).getUser().getId() == getUser().getId();
    return false;
  }
}
