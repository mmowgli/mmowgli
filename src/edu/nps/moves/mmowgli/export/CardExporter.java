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

package edu.nps.moves.mmowgli.export;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;

/**
 * ActionPlanExporter.java Created on Nov 28, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardExporter extends BaseExporter
{
  Thread thread;

  private String CARD_ELEMENT         = "Card";
  private String CDATA_ELEMENTS       = BRIEFING_TEXT_ELEM +" "+ CARD_ELEMENT; /* "elem1" + " "+ "elem2" */;

  public final String STYLESHEET_NAME    = "CardTree.xsl";
  public final String THREAD_NAME        = "CardExporter";
  public final String FILE_NAME          = "IdeaCardChain";
  
  public final String CARD_TREE_ROOT_KEY = "singleIdeaCardChainRootNumber";
  public final String SHOW_HIDDEN_KEY    = "displayHiddenCards";
  
  private static Map<String,String> parameters = null;
 
  public CardExporter()
  {
  	parameters = new HashMap<>();
  	HSess.init();
  	parameters.put(SHOW_HIDDEN_KEY,Boolean.toString(Game.getTL().isReportsShowHiddenCards()));
  	HSess.close();
  }
  
  @Override
  protected Map<String, String> getStaticTransformationParameters()
  {
    return parameters;
  }

  @Override
  protected void setStaticTransformationParameters(Map<String, String> map)
  {
    parameters = map;    
  }
  
  public void exportSingleCardTreeToBrowser(String title, Object cId)
  {
    parameters.put(CARD_TREE_ROOT_KEY,cId.toString());
    showXml = false;
    
    exportToBrowser(title); //_export();    
  }
  
  @Override
  public Document buildXmlDocument() throws Throwable
  {
    Document doc;;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = factory.newDocumentBuilder();
      doc = parser.newDocument();
      doc.setXmlStandalone(true);
      // Don't put the xsl directive in ..we're doing the conversion on the server
      //ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"CardTree.xsl\"");
      // Do a comment instead
      Comment comm = doc.createComment("xml-stylesheet\", \"type=\"text/xsl\" href=\""+STYLESHEET_NAME+"\"");//CardTree.xsl\"");
      Element root = doc.createElement("CardTree");
      doc.appendChild(root);
      //doc.insertBefore(pi, root);
      doc.insertBefore(comm, root);
      
      // Skip schema for now (needs tweeking)
      //root.setAttribute("xmlns","http://edu.nps.moves.mmowgli.cardTree");
      //root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
      //root.setAttribute("xsi:schemaLocation", "http://edu.nps.moves.mmowgli.cardTree CardTree.xsd");
      
      root.setAttribute("exported", dateFmt.format(new Date()));
      
      HSess.init();      
      root.setAttribute("multipleMoves", Boolean.toString(isMultipleMoves(HSess.get())));      
      Game g = Game.getTL();
      String s = g.getTitle();
      addElementWithText(root, "GameTitle", s.replace(' ', '_'));     // for better file-name building
      addElementWithText(root, "GameAcronym", g.getAcronym());
      addElementWithText(root, "GameSecurity", g.isShowFouo()?"FOUO":"open");
      addElementWithText(root, "GameSummary", metaString);
      newAddCall2Action(root, HSess.get(), g);
      
      Element topCards = createAppend(root, "TopLevelCardTypes");
      insertTopCardsForAllRounds(topCards,g,HSess.get());
      
      Element innovateRoot = createAppend(root, "InnovateCards");
      Element defendRoot = createAppend(root,"DefendCards");
      
      HSess.closeAndReopen();
      
      @SuppressWarnings("unchecked")    
      ArrayList<Card> alis = new ArrayList<Card>(HSess.get().createCriteria(Card.class).list()); // avoids lazy init after HSess.close();
      HSess.close();
      
      for (Card card : alis) {
        CardType typ = card.getCardType();
        if(typ.isIdeaCard()) {
          if(typ.isPositiveIdeaCard())
            walkCardTree(innovateRoot,card.getId(),1);
          else 
            walkCardTree(defendRoot,card.getId(),1);
        }
      }
    }
    catch (Throwable t) {
      t.printStackTrace();
      HSess.close();  // handles already closed
      throw t; // rethrow
    }
    return doc;  
  }
  
  @SuppressWarnings("unchecked")
  private void insertTopCardsForAllRounds(Element parent, Game g, Session sess)
  {
    Move lastMove = g.getCurrentMove();
    
    Element innovType = createAppend(parent,"InnovateType");
    Element defendType = createAppend(parent,"DefendType");
    
    List<Move> mvs = sess.createCriteria(Move.class).addOrder(Order.asc("number")).list();
    for(Move m : mvs) {
      if(m.getNumber() > lastMove.getNumber())
        continue;  // don't want future moves
      MovePhase mp = m.getCurrentMovePhase();
      Set<CardType> allowedCards = mp.getAllowedCards();
      for(CardType ct : allowedCards) {
        if(ct.isPositiveIdeaCard()) {
          Element elm = createAppend(innovType, "Type");
          elm.setAttribute("title", ct.getTitle());
          elm.setAttribute("round",""+m.getNumber());
          elm.setAttribute("prompt", ct.getPrompt());
        }
        else if(ct.isNegativeIdeaCard()) {
          Element elm = createAppend(defendType, "Type");
          elm.setAttribute("title", ct.getTitle());
          elm.setAttribute("round",""+m.getNumber());
          elm.setAttribute("prompt", ct.getPrompt());
        }
      }
    }    
  }
  
  private void walkCardTree(Element parent, long cardId, int level)
  {
    Thread.yield();
    
    HSess.init();
    Card child = Card.getTL(cardId);
    Element elm = addElementWithText(parent,CARD_ELEMENT,toUtf8(child.getText()));
    elm.setAttribute("type", toUtf8(child.getCardType().getTitle()));
    elm.setAttribute("level", ""+level);
    elm.setAttribute("author", toUtf8(child.getAuthorName())); //child.getAuthor().getUserName()));
    elm.setAttribute("date", dateFmt.format(child.getCreationDate()));
    elm.setAttribute("id", ""+child.getId());
    elm.setAttribute("color", getBackgroundColor(child));
    elm.setAttribute("textcolor",getTextColor(child));
    if(child.isHidden())
      elm.setAttribute("hidden", "true");
    Set<CardMarking> markings = child.getMarking();
    for(CardMarking m : markings) {
      String label = deSpace(m.getLabel());
      char[] ca = label.toCharArray();
      ca[0] = Character.toLowerCase(ca[0]);
      label = new String(ca);
      elm.setAttribute(label,"true");
    }
    elm.setAttribute("moveNumber", ""+child.getCreatedInMove().getNumber());
    HashSet<Card>children = new HashSet<Card>(child.getFollowOns());  // avoids lazy update
    HSess.close();
    
    level++;
    for(Card c : children)
      walkCardTree(elm,c.getId(),level);    
  }
  
  @Override
  protected void showFile(String ignore, Document doc, String name, String styleSheetNameInThisPackage, boolean showXml) throws TransformerConfigurationException, TransformerException
  {
    super.showFile("Cards", doc, name, styleSheetNameInThisPackage, showXml);
  }

  @Override
  protected void showFile(String ignore, Document doc, String name, String styleSheetNameInThisPackage, String cdataElementList, boolean showXml) throws TransformerConfigurationException, TransformerException
  {
    super.showFile("Cards", doc, name, styleSheetNameInThisPackage, cdataElementList, showXml);
  }

  private String getBackgroundColor(Card c)
  {
    String s = CardStyler.getCardBaseColor(c.getCardType());
    return checkHash(s);
  }
  
  private String getTextColor(Card c)
  {
    String s = CardStyler.getCardTextColorOverBase(c.getCardType());
    return checkHash(s);
  }
  
  private String checkHash(String s)
  {
    if(!s.startsWith("#"))
      s = "#"+s;
    return s;    
  }
  
  private String deSpace(String s)
  {
    return s.replace(" ", "").replace("-", "");
  }
  
  @Override
  public String getCdataSections()
  {
    return CDATA_ELEMENTS;
  }
  @Override
  public String getStyleSheetName()
  {
    return STYLESHEET_NAME;
  }
  @Override
  public String getFileNamePrefix()
  {
    return FILE_NAME;
  }
  @Override
  public String getThreadName()
  {
    return THREAD_NAME;
  }
}
