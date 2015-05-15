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

import java.io.Serializable;

/**
 * A field in a message
 */
public class ListEntryField implements Serializable {

    private static final long serialVersionUID = 1L;

    private String caption;

    private String value;

    /**
     * Constructor
     */
    public ListEntryField() {
        this("");
    }

    /**
     * Constructor
     * 
     * @param caption
     *            The caption of the field
     */
    public ListEntryField(String caption) {
        this(caption, null);
    }

    /**
     * Constructor
     * 
     * @param caption
     * @param value
     */
    public ListEntryField(String caption, String value) {
        this.caption = caption;
        this.value = value;
    }

    /**
     * Gets the caption of the field
     * 
     * @return A string describing the function of this field
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption of the field.
     * 
     * @param caption
     *            A string describing the function of this field
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Get the content of the field
     * 
     * @return The content of this field
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the content of the field
     * 
     * @param value
     *            A string content
     */
    public void setValue(String value) {
        this.value = value;
    }
}
