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

package edu.nps.moves.mmowgli.utility;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.DefaultItemSorter;
import com.vaadin.data.util.DefaultItemSorter.DefaultPropertyValueComparator;

/**
 * BeanContainerWithCaseInsensitiveSorter.java Created on Aug 8, 2012
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class BeanContainerWithCaseInsensitiveSorter<IDTYPE, BEANTYPE> extends BeanContainer<IDTYPE, BEANTYPE>
{
  private static final long serialVersionUID = 1L;

  public BeanContainerWithCaseInsensitiveSorter(Class<? super BEANTYPE> type)
  {
    super(type);
    setItemSorter(new DefaultItemSorter(new NoCaseComparator()));
  }

  class NoCaseComparator extends DefaultPropertyValueComparator 
  {
    private static final long serialVersionUID = 1L;

    public int compare(Object o1, Object o2)
    {
      if(o1 instanceof String)
        o1 = ((String)o1).toLowerCase();
      if(o2 instanceof String)
        o2 = ((String)o2).toLowerCase();
      
      return super.compare(o1, o2);      
    }
  }
}
