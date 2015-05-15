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

package edu.nps.moves.mmowgli.db;

import static edu.nps.moves.mmowgli.hibernate.DbUtils.len255;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.hibernate.DB;
import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * MessageUrl.java
 * Created on Apr 11, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class MessageUrl implements Serializable
{
  private static final long serialVersionUID = -5242999177806502319L;

  public static int TEXT_FIELD_WIDTH = 255;
  public static int URL_FIELD_WITDH = 255;
  public static int TOOLTIP_FIELD_WIDTH = 255;
  
  long id;         // Primary key, auto-generated.
  String  text;
  String  tooltip;
  String  url;
  Date    date;

  public MessageUrl()
  {}
  
  public MessageUrl(String text, String url)
  {
    setText(text);
    setUrl(url);
    setDate(new Date());
  }
  
  @SuppressWarnings({ "serial"})
  public static HbnContainer<MessageUrl> getContainer()
  {
    return new HbnContainer<MessageUrl>(MessageUrl.class,HSess.getSessionFactory())
    {
      @Override
      protected Criteria getBaseCriteriaTL()
      {
        return super.getBaseCriteriaTL().addOrder(Order.desc("date")); // newest first
      }      
    };
  }
  
  public static void saveTL(MessageUrl mu)
  {
    DB.saveTL(mu);
  }
  
  public static MessageUrl getTL(Object id)
  {
    return DB.getTL(MessageUrl.class, id);
  }
 
  public static MessageUrl getLastTL()
  {
    org.hibernate.Query q = HSess.get().createQuery("select max(id) from MessageUrl");
    Object o = q.uniqueResult();
    if(o == null)
      return null;
    return MessageUrl.getTL(o);
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  public long getId()
  {
    return id;
  }
  public void setId(long id)
  {
    this.id = id;
  }
  
  @Basic
  public String getText()
  {
    return text;
  }
  
  public void setText(String text)
  {
    this.text = len255(text);
  }
  
  @Basic
  public String getUrl()
  {
    return url;
  }
  
  public void setUrl(String url)
  {
    this.url = len255(url);
  }
  
  @Basic
  public Date getDate()
  {
    return date;
  }

  public void setDate(Date date)
  {
    this.date = date;
  }

  @Basic
  public String getTooltip()
  {
    return tooltip;
  }

  public void setTooltip(String tooltip)
  {
    this.tooltip = len255(tooltip);
  }
  
}
