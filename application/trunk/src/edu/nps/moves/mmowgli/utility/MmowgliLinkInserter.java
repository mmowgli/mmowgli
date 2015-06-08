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
import static edu.nps.moves.mmowgli.MmowgliEvent.ACTIONPLANSHOWCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.CARDCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.SHOWUSERPROFILECLICK;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.Game.RegexPair;
import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * MmowgliLinkInserter.java
 * Created on Aug 5, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliLinkInserter
{
  static enum linkType {CARD,ACTIONPLAN,USER};
  
	// todo convert to Java regex
  public static final String USERLINK_REGEX   = "user\\s+(\\d+)";
  public static final String USERLINK_REGEX2  = "player\\s+(\\d+)";
  public static final String USERLINK_REGEX3  = "@(\\d+)";
  public static final String ACTPLNLINK_2_REGEX = "ap\\s+(\\d+)";
  public static final String CARDLINK_REGEX   = "card\\s+(\\d+)"; //"(([Gg][Aa][Mm][Ee]\s*(\d|\.)+\s*)?([Ii][Dd][Ee][Aa]\s*)?[Cc][Aa][Rr][Dd]\s*#?\s*([Cc][Hh][Aa][Ii][Nn]\s*#?\s*)?(\d+))";
  public static final String ACTPLNLINK_REGEX = "(?:action\\s)?plan\\s+(\\d+)"; //(([Gg][Aa][Mm][Ee]\s*(\d|\.)+\s*)?([Aa][Cc][Tt][Ii][Oo][Nn]\s*#?\s*)?[Pp][Ll][Aa][Nn]\s*#?\s*(\d+))"

  // This allows us to avoid linkifying the above sequences when they appear in tooltips; we would build bogus html otherwise
  // One for each of the 4 above; avoid using strings which would match above
  // The commented lines would more closely match the case of the original, but at the expense of doubling the compute time
  private static String[][] regexSubs = {
    {"@", "USR "},
    {"[pP][lL][aA][yY][eE][rR]\\s","USR "},
    //{"user\\s", "usr "},
    //{"USER\\s", "USR "},
    {"[uU][sS][eE][rR]\\s", "USR "},
    //{"plan\\s", "pln "},
    //{"PLAN\\s", "PLN "},
    {"[pP][lL][aA][nN]\\s", "PLN "},
    //{"card\\s", "crd "},
    //{"CARD\\s", "CRD "},
    {"[cC][aA][rR][dD]\\s", "CRD "},
    {"[aA][pP]\\s",         "PLN "}
 };

  // Gotten from http://daringfireball.net/2010/07/improved_regex_for_matching_urls
  public static final String URLLINK_REGEX = 
  "(?i)"                           +
  "\\b"                              +
  "("                               + // # Capture 1: entire matched URL
    "(?:"                           +
      "https?://"                   + // # http or https protocol
      "|"                           + // #   or
      "www\\d{0,3}[.]"              + // # "www.", "www1.", "www2.", "www999."
      "|"                           + // #   or
      "[a-z0-9.\\-]+[.][a-z]{2,4}/" + // # looks like domain name followed by a slash
    ")"                             +
    "(?:"                           + // # One or more:
      "[^\\s()<>]+"                              + //  # Run of non-space, non-()<>
      "|"                                        + //  #   or
      "\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)" + //  # balanced parens, up to 2 levels
    ")+"                                         + 
    "(?:"                                        + //  # End with:
      "\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)" + //  # balanced parens, up to 2 levels
      "|"                                        + //  #   or
      "[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]"       + //  # not a space or one of these punct chars
    ")"+
  ")";
  
  public static final String ACTPLN_EVENTNUM  = "" + ACTIONPLANSHOWCLICK.ordinal();
  public static final String CARD_EVENTNUM    = "" + CARDCLICK.ordinal();
  public static final String USER_EVENTNUM    = "" + SHOWUSERPROFILECLICK.ordinal();
  
  private static Pattern userLinkPattern   = Pattern.compile(USERLINK_REGEX,  Pattern.CASE_INSENSITIVE);
  private static Pattern userLinkPattern2  = Pattern.compile(USERLINK_REGEX2, Pattern.CASE_INSENSITIVE);
  private static Pattern userLinkPattern3  = Pattern.compile(USERLINK_REGEX3, Pattern.CASE_INSENSITIVE);
  private static Pattern cardLinkPattern   = Pattern.compile(CARDLINK_REGEX,  Pattern.CASE_INSENSITIVE);
  private static Pattern actPlnLinkPattern = Pattern.compile(ACTPLNLINK_REGEX,Pattern.CASE_INSENSITIVE);
  private static Pattern urlLinkPattern    = Pattern.compile(URLLINK_REGEX,   Pattern.CASE_INSENSITIVE);
  private static Pattern apLinkPattern     = Pattern.compile(ACTPLNLINK_2_REGEX,Pattern.CASE_INSENSITIVE);
 
  /**
   * Replaces strings of "user nnn" with game name
   * @param s original
   * @return replaced
   */
  
  public static String insertUserName_oobTL(String s)
  {
    StringBuilder sb = null;
    Matcher m = userLinkPattern.matcher(s);
    int start = 0;

    while (start < s.length() && m.find(start)) {
      if(sb == null)
        sb = new StringBuilder(s);
      User u = null;
      try {
        Long lng = Long.parseLong(m.group(1));
        u = User.getTL(lng);
      }
      catch(Throwable t) {
        return s;
      }
      if(u != null) {
        String nm = u.getUserName();
        sb.replace(m.start(), m.end(), nm);
        start = m.start()+nm.length();
      }
      else
        start = m.start()+m.group().length();
    }
    if(sb == null)
      return s;
    return sb.toString();
  }
  
  /**
   * 
   * @param s String to modify
   * @return modified String
   */
  public static String insertLinksOob(String s, Game g, Session sess)
  {
    return insertLinksCommon(s,g,sess);
  }
  
  public static String insertLinksTL(String s, Game g)
  {
    return insertLinksCommon(s,g,HSess.get());
  }
  
  private static String insertLinksCommon(String s, Game g, Session sess)
  {
    if(s==null || s.length()<=0)
      return "";

    StringBuilder sb = new StringBuilder(s);
    String news=null;
    if(g != null) {
      if((news = handleGlobalGameReplacements(sb, g)) != null)
        return news;
    }
    
    Matcher matcher = urlLinkPattern.matcher(sb);  // Important to do this first, because card and ap insert links, which then screw up urlLinkPattern
    urlLoopMatcher(sb,matcher);

    matcher = cardLinkPattern.matcher(sb);
    loopMatcher(sb,matcher,CARD_EVENTNUM, linkType.CARD, sess);

    matcher = actPlnLinkPattern.matcher(sb);
    loopMatcher(sb,matcher,ACTPLN_EVENTNUM, linkType.ACTIONPLAN, sess);

    matcher = apLinkPattern.matcher(sb);
    loopMatcher(sb,matcher,ACTPLN_EVENTNUM, linkType.ACTIONPLAN, sess);
   
    matcher = userLinkPattern.matcher(sb);
    loopMatcher(sb,matcher,USER_EVENTNUM, linkType.USER, sess);
    
    matcher = userLinkPattern2.matcher(sb);
    loopMatcher(sb,matcher,USER_EVENTNUM, linkType.USER, sess);
    
    matcher = userLinkPattern3.matcher(sb);
    loopMatcher(sb,matcher,USER_EVENTNUM, linkType.USER, sess);
    
    return sb.toString();
  }
    
  private static String handleGlobalGameReplacements(StringBuilder sb, Game g)
  {
    // This db field is not used; db had contained an old game:
    //regex: Game 2011.(\d) Action Plan (\d+)
    //replacement: <a href="https://web.mmowgli.nps.edu/piracy/ActionPlanList2011.$1.html#ActionPlan$2" target="apwindow">$0</a>

    List<RegexPair> regXs = g.getLinkRegexs();
    if(regXs == null || regXs.size()<=0)
      return null;
    
    boolean found=false;
    for(RegexPair regx : regXs) {
      Pattern pp = Pattern.compile(regx.regex);
      Matcher mm = pp.matcher(sb);
      if(mm.find()) {
        sb = new StringBuilder(mm.replaceAll(regx.replacement));
        found=true;
      }
    }
    return found ? sb.toString() : null;
  }
  
  /**
   * Insert <a links into the string in place of "card nnn" and "plan nnn" instances
   * @param sb target string in the form of a StringBuilder, gets modified
   * @param matcher Reg Ex expression matcher
   * @param eventNum event # from ApplicationConstants which can be used in a URL to move the the appropriate screen
   */
  private static void loopMatcher(StringBuilder sb, Matcher matcher, String eventNum, linkType lTyp, Session sess)
  {
    int start = 0;
    while(matcher.find(start)) {
      String idnum = matcher.group(1);  // since we matched, I know there is a group 1 which is the card/ap id
      Object cardOrApOrUser = checkExistence(lTyp,idnum,sess);
      if(cardOrApOrUser != null ) {
        String link = buildLink(lTyp,matcher.group(),idnum,eventNum,sess,cardOrApOrUser);
        sb.replace(matcher.start(), matcher.end(), link);
        start = matcher.start()+link.length();
      }
      else
        start = matcher.end();
    }
  }
  
  private static Object checkExistence(linkType lTyp, String idnum, Session sess)
  {
    try {
      Long oid = Long.parseLong(idnum);
      if(lTyp == linkType.ACTIONPLAN)
        return sess.get(ActionPlan.class,oid);
      if(lTyp == linkType.CARD) 
        return Card.get(oid, sess);
      if(lTyp == linkType.USER)
        return User.get(oid, sess);
    }
    catch(Throwable t) {}
    return null;
  }
  
  /**
   * Create a <a string from the inputs
   * @param original located String which is getting replaced
   * @param objectId card or action plan id
   * @param eventNum "SHOW CARD" or "SHOW ACTION PLAN" event number
   * @return
   */
 
  private static String buildLink(linkType lTyp, String original, String objectId, String eventNum, Session sess, Object cardOrApOrUser)
  {
    StringBuilder sb = new StringBuilder("#!");
    sb.append(eventNum);
    sb.append("_");
    sb.append(objectId);
    String ln = sb.toString();
    sb.setLength(0);

    sb.append("<a href=\"");
    sb.append(ln);
    sb.append("\"");
    String tt = buildToolTip(ln,cardOrApOrUser,sess);
    if(tt != null) {
      sb.append(" title=\"");
      sb.append(tt);
      sb.append("\"");
    }
    sb.append(">");
    if(lTyp == linkType.USER) {
      User u = User.get(Long.parseLong(objectId), sess);//todo get from quickuser cache
      if(u != null)
        original = u.getUserName();
    }
    sb.append(original);
    sb.append("</a>");
    return sb.toString();
  }
  
  private static String buildToolTip(String ln, Object obj, Session sess)
  {
    if(obj instanceof ActionPlan)
      return avoidRegex(((ActionPlan)obj).getTitle());

    if(obj instanceof Card)
      return avoidRegex(((Card)obj).getText());
   
    if(obj instanceof User)
      return avoidRegex(((User)obj).getUserName());
    return ln;
  }
  
  private static String avoidRegex(String s)
  {
    for(String[] pair : regexSubs) {
      s = s.replaceAll(pair[0], pair[1]);
    }
    return s;
  }

  private static void urlLoopMatcher(StringBuilder sb, Matcher matcher)
  {
    int start = 0;
    while(matcher.find(start)) {     
      String link = buildUrlLink(matcher.group());
      sb.replace(matcher.start(), matcher.end(), link);
      start = matcher.start()+link.length();
    }
  }

  /**
   * Create a <a string from the input
   */
  private static String buildUrlLink(String url)
  {
    StringBuilder sb = new StringBuilder("<a href=\"");
    sb.append(url);
    sb.append("\" title=\"");
    sb.append(url);
    sb.append("\" target=\"linkWindow\">");
    sb.append(url);
    sb.append("</a>");
    return sb.toString();   
  }

}
