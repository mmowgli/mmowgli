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

package edu.nps.moves.mmowgli.modules.cards;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.nps.moves.mmowgli.db.CardType;

/**
 * CardStyler.java
 * Created on Oct 30, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardStyler
{
  private static HashMap<String, CardStyle> styleMap;
//@formatter:off  
  static {
    styleMap = new HashMap<String,CardStyle>();
    styleMap.put("m-purple", new CardStyle(
        "innovatePurpleHeader.png",         "innovatePurpleBig.png",          "innovatePurpleSummary.png",
        "innovatePurpleSummaryMultiple.png","innovatePurpleParent145w95h.png","innovateDotPurple.png",
        //"#662D91","m-white-text","#FFFFFF","m-black-text"));
        "#662D91",
        "m-white-text","#FFFFFF","m-purple-text","#662D91"));
        
    //baseColor
    //textColorOverBaseStyle textColorOverBase textColorOverWhiteStyle textColorOverWhite

    styleMap.put("m-green",  new CardStyle(
        "defendGreenHeader.png",            "defendGreenBig.png",             "defendGreenSummary.png",
        "defendGreenSummaryMultiple.png",   "defendGreenParent145w95h.png",   "defendDotGreen.png",
        //"#00A651","m-white-text","#FFFFFF","m-black-text"));
        "#00A651",
        "m-white-text","#FFFFFF","m-green-text","#24AB51"));
        
    styleMap.put("m-orange", new CardStyle(
        "expandOrangeHeader.png",           "expandOrangeBig.png",            "expandOrangeSummary.png",
        "expandOrangeSummaryMultiple.png",  "expandOrangeParent145w95h.png",  "expandDotOrange.png",
        //"#F26522","m-white-text","#FFFFFF","m-black-text"));
        "#F26522",
        "m-white-text","#FFFFFF","m-orange-text","#EB642D"));
    
    styleMap.put("m-red",    new CardStyle(
        "counterRedHeader.png",             "counterRedBig.png",              "counterRedSummary.png",
        "counterRedSummaryMultiple.png",    "counterRedParent145w95h.png",    "counterDotRed.png",
        //"#B61861","m-white-text","#FFFFFF","m-black-text"));
        "#B61861",
        "m-white-text","#FFFFFF","m-red-text","#B20062"));
    
    
    styleMap.put("m-blue",   new CardStyle(
        "adaptBlueHeader.png",              "adaptBlueBig.png",               "adaptBlueSummary.png", 
        "adaptBlueSummaryMultiple.png",     "adaptBlueParent145w95h.png",     "adaptDotBlue.png",
        //"#0F75BC","m-white-text","#FFFFFF","m-black-text"));
        "#0F75BC",
        "m-white-text","#FFFFFF","m-blue-text","#2775BD"));
    
    styleMap.put("m-lime",   new CardStyle(
        "exploreLimeHeader.png",            "exploreLimeBig.png",             "exploreLimeSummary.png",
        "exploreLimeSummaryMultiple.png",   "exploreLimeParent145w95h.png",   "exploreDotLime.png",
        //"#8CC63F","m-white-text","#FFFFFF","m-black-text"));
        "#8CC63F",
        "m-white-text","#FFFFFF","m-lime-text","#90CB41"));

    styleMap.put("m-darkred",   new CardStyle(
        "header_darkred.png",               "cardbig_darkred.png",       "cardSummary_darkred.png",
        "summaryMultiple_darkred.png",      "parent145w95h_darkred.png", "carddot_darkred.png",
        // "#AB0000","m-white-text","#FFFFFF","m-black-text"));
        "#AB0000",
        "m-white-text","#FFFFFF","m-darkred-text","#AB0000"));
    
    styleMap.put("m-armyblack", new CardStyle(
        "header_armyblack.png",             "cardbig_armyblack.png",       "cardSummary_armyblack.png",
        "summaryMultiple_armyblack.png",    "parent145w95h_armyblack.png", "carddot_armyblack.png",
        //"#000000","m-armygold-text","#FFFFFF","m-black-text"));
        "#000000",
        "m-armygold-text","#FCC702","m-armyblack-text","#000000"));
    
    styleMap.put("m-armygold",  new CardStyle(
        "header_armygold.png",              "cardbig_armygold.png",       "cardSummary_armygold.png",
        "summaryMultiple_armygold.png",     "parent145w95h_armygold.png", "carddot_armygold.png",
        //"#FCC702","m-armyblack-text","#FFFFFF","m-black-text"));
        "#FCC702",
        "m-armyblack-text","#000000","m-armygold-text","#FCC702"));

    //baseColor
    //textColorOverBaseStyle textColorOverBase textColorOverWhiteStyle textColorOverWhite

    styleMap.put("m-navyblue",  new CardStyle(
        "header_navyblue.png",              "cardbig_navyblue.png",       "cardSummary_navyblue.png",
        "summaryMultiple_navyblue.png",     "parent145w95h_navyblue.png", "carddot_navyblue.png",
        //"#032E57","m-white-text","#FFFFFF","m-black-text"));
        "#032E57",
        "m-white-text","#FFFFFF","m-navyblue-text","#032E57"));
    
    styleMap.put("m-navygold",  new CardStyle(
        "header_navygold.png",              "cardbig_navygold.png",       "cardSummary_navygold.png",
        "summaryMultiple_navygold.png",     "parent145w95h_navygold.png", "carddot_navygold.png",
        //"#FCBB38","m-lemonchiffon4text","#8B8970","m-black-text"));
        "#FCBB38",
        "m-lemonchiffon4text","#8B8970","m-navygold-text","#fcbb38"));
    
    styleMap.put("m-whitecard",  new CardStyle(
        "header_white.png",                 "cardbig_white.png",          "cardSummary_white.png",
        "summaryMultiple_white.png",        "parent145w95h_white.png",    "carddot_white.png",
        //"#FFFFFF","m-whitecard-text","FFFFFF","m-black-text"));
        "#FFFFFF",
        "m-blackcard-text","#58595B","m-blackcard-text","#58595B"));
    
    styleMap.put("m-blackcard", new CardStyle(
        "header_armyblack.png",             "cardbig_armyblack.png",       "cardSummary_armyblack.png",
        "summaryMultiple_armyblack.png",    "parent145w95h_armyblack.png", "carddot_armyblack.png",
        //"#373737","m-blackcard-text","#373737","m-black-text"));
        "#373737",
        "m-white-text","#FFFFFF","m-black=text","#000000"));
  }
  
  public static String TOP_POSITIVE_STYLE_DEFAULT = "m-purple";
  public static String TOP_NEGATIVE_STYLE_DEFAULT = "m-green";
  public static String EXPAND_STYLE_DEFAULT = "m-orange";
  public static String COUNTER_STYLE_DEFAULT = "m-red";
  public static String ADAPT_STYLE_DEFAULT = "m-adapt";
  public static String EXPLORE_STYLE_DEFAULT = "m-lime";
  
  static class CardStyle
  {
    public CardStyle( String listHeaderBackgroundImage,
                      String cardBigImage,
                      String summaryImage,
                      String summaryMultipleImage,
                      String parent145w95hImage,
                      String dotImage,
                      String baseColor,
                      String textColorOverBaseStyle,
                      String textColorOverBase,
                      String textColorOverWhiteStyle,
                      String textColorOverWhite)
    {
      this.listHeaderBackgroundImage = listHeaderBackgroundImage;
      this.cardBigImage = cardBigImage;
      this.summaryImage = summaryImage;
      this.summaryMultipleImage = summaryMultipleImage;
      this.parent145w95hImage = parent145w95hImage;
      this.dotImage = dotImage;
      this.baseColor = baseColor;
      this.textColorOverBaseStyle = textColorOverBaseStyle;
      this.textColorOverBase = textColorOverBase;
      this.textColorOverWhiteStyle = textColorOverWhiteStyle;
      this.textColorOverWhite = textColorOverWhite;
    }
    String listHeaderBackgroundImage;
    String cardBigImage;
    String summaryImage;
    String summaryMultipleImage;
    String parent145w95hImage;
    String dotImage;
    String baseColor;
    String textColorOverBaseStyle;
    String textColorOverBase;
    String textColorOverWhiteStyle;
    String textColorOverWhite;
  }
  
  public static Color makeColor(String rgbinhex)
  {
    String num = rgbinhex.toUpperCase();
    if(num.startsWith("#"))
      num = num.substring(1);
    if(!num.startsWith("0x"))
      num = num.substring(2);
    
    int intg = Integer.parseInt(num,16);
        
    return new Color(intg);
  }
  
  private static String getDefaultStyle(CardType ct)
  {
    if(ct.isPositiveIdeaCard())
      return TOP_POSITIVE_STYLE_DEFAULT;
    if(ct.isNegativeIdeaCard())
      return TOP_NEGATIVE_STYLE_DEFAULT;
    switch(ct.getDescendantOrdinal()) {
    case CardType.EXPAND_CARD_TYPE:
      return EXPAND_STYLE_DEFAULT;
    case CardType.COUNTER_CARD_TYPE:
      return COUNTER_STYLE_DEFAULT;
    case CardType.ADAPT_CARD_TYPE:
      return ADAPT_STYLE_DEFAULT;
    case CardType.EXPLORE_CARD_TYPE:
    default:
      return EXPLORE_STYLE_DEFAULT;
    }
  }
  
  private static CardStyle getCardStyle(CardType ct)
  {
    CardStyle cs = styleMap.get(ct.getCssColorStyle());
    if(cs == null)
      cs = styleMap.get(getDefaultStyle(ct));
    return cs;
  }
  
  private static CardStyle getCardStyle(String styleName)
  {
    CardStyle cs = styleMap.get(styleName);
    if(cs == null)
      cs = styleMap.get(TOP_POSITIVE_STYLE_DEFAULT);  // return something
    return cs;    
  }
  
  public static String getCardHeaderImage            (CardType ct) {return getCardStyle(ct).listHeaderBackgroundImage;}  
  public static String getCardBigImage               (CardType ct) {return getCardStyle(ct).cardBigImage;}
  public static String getCardSummaryImage           (CardType ct) {return getCardStyle(ct).summaryImage;}
  public static String getCardSummaryMultipleImage   (CardType ct) {return getCardStyle(ct).summaryMultipleImage;}
  public static String getCardParentImage            (CardType ct) {return getCardStyle(ct).parent145w95hImage;}
  public static String getCardDot                    (CardType ct) {return getCardStyle(ct).dotImage;}
  public static String getCardBaseColor              (CardType ct) {return getCardStyle(ct).baseColor;}
  public static String getCardTextColorOverBaseStyle (CardType ct) {return getCardStyle(ct).textColorOverBaseStyle;}
  public static String getCardTextColorOverBase      (CardType ct) {return getCardStyle(ct).textColorOverBase;}
  public static String getCardTextColorOverWhiteStyle(CardType ct) {return getCardStyle(ct).textColorOverWhiteStyle;}
  
  public static String getCardBaseStyle(CardType ct)
  {
    String sty = ct.getCssColorStyle();
    if(sty == null)
      sty = getDefaultStyle(ct);
    return sty;
  }
  
  public static String getCardDot                    (String styleName) {return getCardStyle(styleName).dotImage;}
  public static String getCardBigImage               (String styleName) {return getCardStyle(styleName).cardBigImage;}
  public static String getCardParentImage            (String styleName) {return getCardStyle(styleName).parent145w95hImage;}
  public static String getCardSummaryImage           (String styleName) {return getCardStyle(styleName).summaryImage;}
  public static String getCardSummaryMultipleImage   (String styleName) {return getCardStyle(styleName).summaryMultipleImage;}
  public static String getCardHeaderImage            (String styleName) {return getCardStyle(styleName).listHeaderBackgroundImage;}
  public static String getCardBaseColor              (String styleName) {return getCardStyle(styleName).baseColor;}
  public static String getCardTextColorOverBaseStyle (String styleName) {return getCardStyle(styleName).textColorOverBaseStyle;}
  public static String getCardTextColorOverBase      (String styleName) {return getCardStyle(styleName).textColorOverBase;}
  public static String getCardTextColorOverWhiteStyle(String styleName) {return getCardStyle(styleName).textColorOverWhiteStyle;}
  
  public static Set<String> getCardStyles()
  {
    return new HashSet<String>(styleMap.keySet());
  }
//@formatter:on
}
