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

package edu.nps.moves.mmowgli.modules.administration;

import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.modules.scoring.ScoreManager2;

/**
 * ScoringHelpPanel.java Created on Aug 29, 2013 Updated on Mar 12, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ScoringHelpWindow extends Window
{
  private static final long serialVersionUID = -5349961748618605848L;

  Game game;

  private ScoringHelpWindow(Game game)
  {
    this.game = game;
    setHeight("405px");
    setWidth("630px");
    setContent(new ScoringHelpPanel());
    setCaption("Scoring Examples");
  }

  String author = "When a card is <b>played</b>:" + "<ol><li>The author's basic score changes by:&nbsp;&nbsp;&nbsp;";

  String parent = "</li>" + "<li>The parent card author's by:&nbsp;&nbsp;&nbsp;";

  String gparent = "</li>" + "<li>The g-parent author's by:&nbsp;&nbsp;&nbsp;";

  String ggparent = "</li>" + "<li>The gg-parent author's by:&nbsp;&nbsp;&nbsp;";

  String gggparent = "</li>" + "<li>The ggg-parent author's by:&nbsp;&nbsp;&nbsp;";

  String ggggparent = "</li>" + "<li>The gggg-parent author's by:&nbsp;&nbsp;&nbsp;";

  String gggggparent = "</li>" + "<li>The ggggg-parent author's by:&nbsp;&nbsp;&nbsp;";

  String ggggggparent = "</li>" + "<li>The gggggg-parent author's by:&nbsp;&nbsp;&nbsp;";

  String gggggggparent = "</li>" + "<li>The ggggggg-parent author's by:&nbsp;&nbsp;&nbsp;";

  String nopoints = "<br/><span style='font-size:smaller';><i>(No points are added for parent cards played by the current author.)</i></span>";
  
  String superint = "</li>" + "</ol>" + "When a card is marked <b>\"super-interesting\"</b>:"
      + "<ul><li>The author's basic score changes by:&nbsp;&nbsp;&nbsp;";

  String tail = "</li>" + "</ul>";

  String apauthor = "When a player <b>accepts an invitation to co-author</b> an action plan:"
      + "<ul><li>The player's innovation score changes by:&nbsp;&nbsp;&nbsp;";

  String thum = "</li></ul>" + "When an action plan is given a <b>\"thumb\"</b> rating by a player:"
      + "<ol><li>Each author's innovation portion from this plan's thumb scores is:&nbsp;&nbsp;&nbsp;";

  String thum2 = "</li>" + "<li>The rater's innovation score changes by:&nbsp;&nbsp;&nbsp;";

  String comment = "</li></ol>" + "When a player <b>enters a comment</b> on an action plan:"
      + "<ol><li>Each author's innovation score changes by:&nbsp;&nbsp;&nbsp;";

  String comment1 = "</li>" + "<li>The commenter's innovation changes by:&nbsp;&nbsp;&nbsp;";

  String aptail = "</li>" + "</ol>";

  @SuppressWarnings("serial")
  class ScoringHelpPanel extends VerticalLayout
  {
    public ScoringHelpPanel()
    {
      setMargin(true);
      TabSheet sheet = new TabSheet();
      sheet.setWidth("100%");
      sheet.setHeight("100%");

      sheet.addTab(doCards(), "Card Play");
      sheet.addTab(doActionPlans(), "Action Plan Play");

      addComponent(sheet);
    }
  }

  private Component doCards()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(author);
    sb.append("<b>+" + game.getCardAuthorPoints());
    sb.append("</b> (cardAuthorPoints)");

    float[] factors = ScoreManager2.parseGenerationFactors(game);
    float points = game.getCardAncestorPoints();

    if (factors.length > 0) {
      parentPart(parent, sb, 0, points, factors);
    }
    if (factors.length > 1) {
      parentPart(gparent, sb, 1, points, factors);
    }
    if (factors.length > 2) {
      parentPart(ggparent, sb, 2, points, factors);
    }
    if (factors.length > 3) {
      parentPart(gggparent, sb, 3, points, factors);
    }
    if (factors.length > 4) {
      parentPart(ggggparent, sb, 4, points, factors);
    }
    if (factors.length > 5) {
      parentPart(gggggparent, sb, 5, points, factors);
    }
    if (factors.length > 6) {
      parentPart(ggggggparent, sb, 6, points, factors);
    }
    if (factors.length > 7) {
      parentPart(gggggggparent, sb, 7, points, factors);
    }
    sb.append(nopoints);
    
    sb.append(superint);
    sb.append("<b>+");
    sb.append(game.getCardSuperInterestingPoints());
    sb.append("</b> (cardSuperInterestingPoints)");

    sb.append(tail);
    
    Label lab = new HtmlLabel(sb.toString());

    VerticalLayout vl = new VerticalLayout();
    vl.addComponent(lab);
    vl.setHeight("100%");
    vl.setMargin(true);
    return vl;
  }

  private Component doActionPlans()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(apauthor);
    sb.append("<b>+" + game.getActionPlanAuthorPoints());
    sb.append("</b> (actionPlanAuthorPoints)");

    sb.append(thum);
    sb.append("<b>");
    sb.append(game.getActionPlanThumbFactor());
    sb.append("</b> times total user thumbs");
    sb.append(thum2);
    sb.append("<b>");
    sb.append(game.getActionPlanRaterPoints());
    sb.append("</b>");
    sb.append(comment);
    sb.append("<b>");
    sb.append(game.getActionPlanCommentPoints());
    sb.append("</b>");
    sb.append(comment1);
    sb.append("<b>");
    sb.append(game.getUserActionPlanCommentPoints());
    sb.append("</b>");

    sb.append(aptail);
    Label lab = new HtmlLabel(sb.toString());

    VerticalLayout vl = new VerticalLayout();
    vl.addComponent(lab);
    vl.setHeight("100%");
    vl.setMargin(true);
    return vl;
  }

  private void parentPart(String hdr, StringBuilder sb, int idx, float points, float[] factors)
  {
    sb.append(hdr);
    sb.append("<b>+");
    float f = points * factors[idx];
    sb.append(f);
    sb.append("</b> = ");
    sb.append(points);
    sb.append(" x ");
    sb.append(factors[idx]);
    sb.append(" (cardAncestorPoints x generationFactor[");
    sb.append(idx);
    sb.append("])");
  }

  public static void showTL(Game game)
  {
    Window win = new ScoringHelpWindow(game);
    UI.getCurrent().addWindow(win);
    win.center();
  }

}
