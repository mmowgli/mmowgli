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

import java.util.*;

import edu.nps.moves.mmowgli.db.*;

public abstract class ListEntry extends AbstractPojo {

    private static final long serialVersionUID = 1L;

    private Date timestamp = new Date();

    private List<ListEntryField> fields;

    public ListEntry (ActionPlan ap)
    {
      setFields(Arrays.asList(
          new ListEntryField("From", ap.getTitle()),
          new ListEntryField("To", "blah ap to"),
          new ListEntryField("Subject", "blah ap subject"),
          new ListEntryField("Body", "blah ap body")));
          this.timestamp = ap.getCreationDate();      
    }
    public ListEntry (Card card)
    {
      setFields(Arrays.asList(
          new ListEntryField("From", card.getAuthorName()),
          new ListEntryField("To", "blah card to"),
          new ListEntryField("Subject", card.getCardType().getTitle()),
          new ListEntryField("Body", card.getText())));
      this.timestamp = card.getCreationDate();
    }
    public ListEntry (User user)
    {
      setFields(Arrays.asList(
          new ListEntryField("From", user.getUserName()),
          new ListEntryField("To", "blah user to"),
          new ListEntryField("Subject", "blah user subject"),
          new ListEntryField("Body", "blah user body")));
         this.timestamp = user.getRegisterDate();      
    }
    /**
     * Constructor
     * 
     * @param from
     *            Email address from who the email is sent
     * @param to
     *            The recipients email address
     * @param subject
     *            The subject of the email
     */
    public ListEntry(String from, String to, String subject) {
        setFields(Arrays.asList(new ListEntryField("From", from),
                new ListEntryField("To", to),
                new ListEntryField("Subject", subject)));
    }

    /**
     * Constructor
     * 
     * @param from
     *            Email address from who the email is sent
     * @param to
     *            The recipients email address
     * @param subject
     *            The subject of the email
     * @param content
     *            The body part of the email
     */
    public ListEntry(String from, String to, String subject, String content) {
        setFields(Arrays.asList(new ListEntryField("From", from),
                new ListEntryField("To", to),
                new ListEntryField("Subject", subject), new ListEntryField("Body",
                        content)));
    }

    /**
     * @return the fields
     */
    public List<ListEntryField> getFields() {
        return fields;
    }

    /**
     * @param fields
     *            the fields to set
     */
    public void setFields(List<ListEntryField> fields) {
        this.fields = fields;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Fetches a field by matching its caption
     * 
     * @param field
     *            The field caption to match
     */
    public ListEntryField getMessageField(String field) {
        if (field != null) {
            for (ListEntryField f : fields) {
                if (f.getCaption().toLowerCase().equals(field.toLowerCase())) {
                    return f;
                }
            }
        }
        return null;
    }
}
