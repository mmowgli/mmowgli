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

package edu.nps.moves.mmowgli.components;

import java.util.List;

import org.hibernate.criterion.Order;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;

import edu.nps.moves.mmowgli.db.Affiliation;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * BoundAffiliationCombo.java
 * Created on Mar 31, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class BoundAffiliationCombo extends ComboBox
{
  private static final long serialVersionUID = 8304263483697267947L;

  @HibernateSessionThreadLocalConstructor
  public BoundAffiliationCombo()
  {
    super();
    common();
  }
  
  public BoundAffiliationCombo(String caption)
  {
    super(caption);
    common();
  }
  
  private void common()
  {    
    @SuppressWarnings("unchecked")
    List<Affiliation> lis = HSess.get().createCriteria(Affiliation.class).addOrder(Order.asc("id")).list();
    
    setContainerDataSource(new BeanItemContainer<Affiliation>(Affiliation.class,lis));
    setItemCaptionMode(ItemCaptionMode.PROPERTY);
    setItemCaptionPropertyId("affiliation");
    setNullSelectionAllowed(false);
    setNewItemsAllowed(false);
    setWidth("260px");
    setInputPrompt("optional");
    pageLength = 16; // how to set num visible items
  }
}
