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

package edu.nps.moves.mmowgliMobile.ui;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.ui.CssLayout;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;
import edu.nps.moves.mmowgliMobile.data.*;

/**
 * MessageListRenderer.java Created on Feb 26, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class ListEntryRenderer2
{
  public final String NORMAL_STYLE_NAME = "m-messageList-normal";
  public final String ITALIC_STYLE_NAME = "m-messageList-italics";
  public final String BOLD_STYLE_NAME = "m-messageList-bold";
  public final String ITALIC_BOLD_STYLE_NAME = "m-messageList-italic-bold";
  
  abstract public void renderEntry(ListEntry msg, ListView2 messageList, CssLayout layout);

  protected SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy hh:mm");
  protected StringBuilder sb = new StringBuilder();
  
  protected Serializable getPojoId(ListEntry msg)
  {
    if (msg instanceof CardListEntry)
      return ((CardListEntry) msg).getCard().getId();
    if (msg instanceof UserListEntry)
      return ((UserListEntry) msg).getUser().getId();
    if (msg instanceof ActionPlanListEntry)
      return ((ActionPlanListEntry) msg).getActionPlan().getId();
    return null;
  }

  private void _text(String styleName, StringBuilder sb, Object ... strs)
  {
    sb.append("<span class='");
    sb.append(styleName);
    sb.append("'>");
    for(Object s : strs)
      sb.append(s.toString());
    sb.append("</span>");   
  }
  
  private void _text2(StringBuilder sb, Object str, String ... styleNames)
  {
    sb.append("<span class='");
    for(String sty : styleNames) {
      sb.append(sty);
      sb.append(" ");
    };
    sb.append("'>");

    sb.append(str.toString());
    sb.append("</span>");       
  }
  
  protected void boldStyledText( StringBuilder sb, Object str, String ... styleNames) 
  {
     ArrayList<String>lis = new ArrayList<String>(Arrays.asList(styleNames));
     lis.add("m-bold14");
    _text2(sb,str,lis.toArray(new String[lis.size()]));
  }
  
  protected void styledText(String style, StringBuilder sb, Object ... strs)
  {
    _text(style,sb,strs);
  }
  protected void italicText(StringBuilder sb, Object ... strs)
  {
    _text(ITALIC_STYLE_NAME,sb,strs);
  }
  protected void italicBoldText(StringBuilder sb, Object ... strs)
  {
    _text(ITALIC_BOLD_STYLE_NAME,sb,strs);
  }
  protected void normalText(StringBuilder sb, Object ... strs)
  {
    _text(NORMAL_STYLE_NAME,sb,strs);
  }
  protected void boldText(StringBuilder sb, Object ... strs)
  {
    _text(BOLD_STYLE_NAME,sb,strs);
  }
  protected void text(StringBuilder sb, Object ...strs)
  {
    normalText(sb,strs);
  }
  private static UserListEntryRenderer u_rend = null;
  private static ActionPlanListEntryRenderer ap_rend = null;
  private static CardListEntryRenderer c_rend = null;

  public static UserListEntryRenderer u()
  {
    if (u_rend == null)
      u_rend = new UserListEntryRenderer();
    return u_rend;
  }
  public static ActionPlanListEntryRenderer ap()
  {
    if(ap_rend == null)
      ap_rend = new ActionPlanListEntryRenderer();
    return ap_rend;
  }
  public static CardListEntryRenderer c()
  {
    if(c_rend == null)
      c_rend = new CardListEntryRenderer();
    return c_rend;
  }
  
  public static class CardListEntryRenderer extends ListEntryRenderer2
  {
    private CardListEntryRenderer(){}
    @Override
    public void renderEntry(ListEntry message, ListView2 messageList, CssLayout layout)
    {
      CardListEntry wc = (CardListEntry) message;
      Card c = wc.getCard();
      CardType typ = c.getCardType();

      layout.removeAllComponents();      
      sb.setLength(0);
      
      boldStyledText(sb,c.getId(),CardStyler.getCardTextColorOverBaseStyle(typ),CardStyler.getCardBaseStyle(typ),"m-horiz-padding-3");
      sb.append(" ");
      normalText(sb,c.getText());
      sb.append(" ");
      normalText(sb,formatter.format(c.getCreationDate()));
      sb.append(" ");
      styledText("m-card-text-author",sb,c.getAuthorName());
      
      layout.addComponent(new HtmlLabel(sb.toString()));
    }
  }

  public static class ActionPlanListEntryRenderer extends ListEntryRenderer2
  {
    private ActionPlanListEntryRenderer(){}
    @Override
    public void renderEntry(ListEntry entry, ListView2 messageList, CssLayout layout)
    {
      ActionPlanListEntry wap = (ActionPlanListEntry) entry;
      ActionPlan ap = wap.getActionPlan();
      layout.removeAllComponents();      
      sb.setLength(0);
      
      boldText(sb,ap.getId());
      sb.append(". ");
      boldText(sb,formatter.format(ap.getCreationDate()));
      sb.append(", ");
      String s = ap.getQuickAuthorList().replaceAll(",",", "); // add space
      styledText("m-actionplan-text-authors",sb,s);
      sb.append(". ");
      normalText(sb,ap.getTitle());
      
      layout.addComponent(new HtmlLabel(sb.toString()));
    }
  }

  public static class UserListEntryRenderer extends ListEntryRenderer2
  {
    private UserListEntryRenderer(){}
    @Override
    public void renderEntry(ListEntry msg, ListView2 messageList, CssLayout layout)
    {
      UserListEntry wu = (UserListEntry) msg;
      User u = wu.getUser();
      layout.removeAllComponents();      
      sb.setLength(0);
      
      styledText("m-card-text-author",sb,u.getUserName());
      sb.append(", ");
      normalText(sb,u.getId());
      sb.append(", ");
      
      normalText(sb,u.getAffiliation());
      sb.append(", ");
      italicText(sb,u.getLocation());
      sb.append(", ");
      boldText(sb,u.getBasicScore());
      sb.append("/");
      boldText(sb,u.getInnovationScore());
      
      layout.addComponent(new HtmlLabel(sb.toString()));
    }
  }
}
