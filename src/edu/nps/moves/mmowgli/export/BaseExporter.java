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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.*;

import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliConstants;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.utility.BrowserWindowOpener;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * BaseExporter.java Created on Nov 28, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class BaseExporter implements Runnable
{
  private Thread thread;

  protected UI xui;
  protected SimpleDateFormat dateFmt = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss-z");
  protected DecimalFormat twoPlaceDecimalFmt = new DecimalFormat("#.##"); 
  protected DecimalFormat onePlaceDecimalFmt = new DecimalFormat("#.#"); 
  protected String metaString = "MMOWGLI: Massive Multiplayer Online Wargame Leveraging the Internet"; 
  protected String BRIEFING_TEXT_ELEM   = "BriefingText";
  protected String REPORTS_DIRECTORY_URL = "reportsDirectoryUrl";
  protected MediaLocator mediaLocator = new MediaLocator();
  
  protected boolean showXml = true; // default
  
  public BaseExporter()
  { 
    if(getStyleSheetName() != null)
      initParametersIfNeeded();
  }
  
  public static class ExportProducts
  {
    public StringWriter xmlSW, htmlSW;
    public ExportProducts(StringWriter xmlSW, StringWriter htmlSW)
    {
      this.xmlSW = xmlSW;
      this.htmlSW = htmlSW;
    }
  }
  
  abstract protected Document buildXmlDocument() throws Throwable;
  abstract protected String getThreadName();
  abstract protected String getCdataSections();
  abstract protected String getStyleSheetName();
  abstract public    String getFileNamePrefix();
  
  abstract protected Map<String,String> getStaticTransformationParameters();
  abstract protected void setStaticTransformationParameters(Map<String,String>map);
  
  private void initParametersIfNeeded()
  {
    if(getStaticTransformationParameters() != null)
      return;
    
    HashMap<String,String> hMap = new HashMap<String,String>();

    InputStream ssInpStr = getClass().getResourceAsStream(getStyleSheetName());  // get style sheet here
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
    try {
      DocumentBuilder parser = factory.newDocumentBuilder();
      Document doc = parser.parse(ssInpStr);
      Element root = doc.getDocumentElement();
      NodeList lis = root.getElementsByTagName("xsl:param");
      int count = lis.getLength();
      for (int i = 0; i < count; i++) {
        Node n = lis.item(i);
        if (n.getNodeType() == Node.ELEMENT_NODE) {
          if (n.getParentNode() == root) {
            Element elem = (Element) n;
            if (elem.getNodeName().equals("xsl:param")) {
              String nm = elem.getAttribute("name");
              String value = elem.getTextContent();
              hMap.put(nm, value);
            }
          }
        }
      }
    } catch (Exception e) {
       System.err.println(e.getLocalizedMessage());
       e.printStackTrace();
      }

    hMap.put(REPORTS_DIRECTORY_URL, getReportsDirectoryUrl());
    setStaticTransformationParameters(hMap);
  }
  
  MetaListener mLis = new MetaListener()
  {
    @Override
    public void continueOrCancel(String s)
    {
      if(s != null) {
        metaString=escapeText(s);
        _export();
      }
    }   
  };
  
  static /* package public */ String getReportsDirectoryUrl()
  {
    return "file://"+getReportsDirectory();
  }
  
  static /* package public */ String getReportsDirectory()
  {
    String s = MmowgliConstants.REPORTS_FILESYSTEM_PATH;
    if(s.endsWith("/"))
      s = s.substring(0,s.length()-1);  // lose trailing /
    return s;
  }
  
  private String escapeText(String s)
  {
    s = s.replace("<", "&lt;");
    return s.replace(">", "&gt;");
  }
  @Override
  public void run()
  {
    try {
      Document doc = buildXmlDocument();
      
      if(getStyleSheetName() != null) {
        String fn;
        showFile("File",doc, fn=buildFileName(getFileNamePrefix()), getStyleSheetName(), getCdataSections(), showXml);
        showEndNotification(fn); //"CardTree");
      }
      Mmowgli2UI.getAppUI().access(new Runnable(){public void run(){Mmowgli2UI.getAppUI().push();}});
    }
    catch (Throwable ex) {
      System.err.println(ex.getClass().getSimpleName()+": "+ex.getLocalizedMessage());
    }
  }
  
  protected void _export()
  {
    thread = new Thread(this, getThreadName());
    thread.setPriority(Thread.NORM_PRIORITY);
    thread.setDaemon(true); // won't stop vm (Tomcat) from being killed
    
    showStartNotification(getFileNamePrefix());
    thread.start();
  }

  public ExportProducts exportToRepository() throws Throwable
  {
    Document doc = buildXmlDocument();
    StringWriter xmlSW  = this.doc2Xml(doc,  getCdataSections());
    StringWriter htmlSW = null;
    if(getStyleSheetName() != null)
      htmlSW = this.doc2Html(doc, xmlSW, getStyleSheetName());
    return new ExportProducts(xmlSW, htmlSW);
  }

  public void exportToBrowser( String title)
  {  
    getMetaStringOrCancel(mLis, title, getStaticTransformationParameters());
  }
  
  protected void getMetaStringOrCancel(final MetaListener lis, String title, final Map<String,String>params)
  {
    final Window dialog = new Window(title);
    final TextField[] parameterFields;
    
    dialog.setModal(true);

    VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setSizeFull();
    dialog.setContent(layout);
    
    final TextArea ta = new TextArea();
    ta.setWidth("100%");
    ta.setInputPrompt("Type a description of this data, or the game which generated this data (optional)");

    ta.setImmediate(true);
    layout.addComponent(ta);

    Set<String>keySet = params.keySet();
    parameterFields = new TextField[keySet.size()];
    int i=0;
    GridLayout pGL = new GridLayout();
    pGL.addStyleName("m-greyborder");
    pGL.setColumns(2);
    Label hdr=new HtmlLabel("<b>Parameters</b>");
    hdr.addStyleName("m-textaligncenter");
    pGL.addComponent(hdr, 0, 0, 1, 0); // top row
    pGL.setComponentAlignment(hdr, Alignment.MIDDLE_CENTER);
    pGL.setSpacing(false);
    for(String key : keySet) {
      pGL.addComponent(new HtmlLabel("&nbsp;"+key+"&nbsp;&nbsp;"));
      pGL.addComponent(parameterFields[i] = new TextField());
      parameterFields[i++].setValue(params.get(key));
    }
    if(i>0) {
      layout.addComponent(pGL);
      layout.setComponentAlignment(pGL, Alignment.TOP_CENTER);
    }

    HorizontalLayout hl = new HorizontalLayout();
    hl.setSpacing(true);
    @SuppressWarnings("serial")
    Button cancelButt = new Button("Cancel", new Button.ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        dialog.close();
        lis.continueOrCancel(null);
      }
    });

    @SuppressWarnings("serial")
    Button exportButt = new Button("Export", new Button.ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        dialog.close();
      
        Set<String>keySet = params.keySet();
        int i=0;
        for(String key : keySet)
          params.put(key, parameterFields[i++].getValue().toString());
     
        lis.continueOrCancel(ta.getValue().toString());
      }
    });
    hl.addComponent(cancelButt);
    hl.addComponent(exportButt);
    hl.setComponentAlignment(cancelButt, Alignment.MIDDLE_RIGHT);
    hl.setExpandRatio(cancelButt, 1.0f);

    // The components added to the window are actually added to the window's
    // layout; you can use either. Alignments are set using the layout
    layout.addComponent(hl);
    dialog.setWidth("385px");
    dialog.setHeight("310px");
    hl.setWidth("100%");
    ta.setWidth("100%");
    ta.setHeight("100%");
    layout.setExpandRatio(ta, 1.0f);

    UI.getCurrent().addWindow(dialog);
  }

  protected Element createAppend(Element parent, String elName)
  {
    Element el = parent.getOwnerDocument().createElement(elName);
    parent.appendChild(el);
    return el;
  }

  protected Element addElementWithText(Element parent, String elName, String text)
  {
    Element el = createAppend(parent, elName);
    Text txt = parent.getOwnerDocument().createTextNode(text);
    el.appendChild(txt);
    return el;
  }
  
  protected void addAttribute(Element elm, String attName, String content)
  {
    elm.setAttribute(attName, content);
  }

  /*
   * We find out whether a game has been run with multiple moves in three ways:
   * 1. if the game current move is > 1
   * 2. if any cards were created in a move > 1
   * 3. if any actionplans were created in a move > 1.
   */
  protected boolean isMultipleMoves(Session session)
  {
    Game g = Game.get(session);
    if(g.getCurrentMove().getNumber() > 1)
      return true;
    
    Criteria criteria = session.createCriteria(Card.class)
        .createAlias("createdInMove", "MOVE")
        .add(Restrictions.gt("MOVE.number", 1))
        .setProjection(Projections.rowCount());

    int count = ((Long) criteria.list().get(0)).intValue();
    if(count>0)
      return true;
    
    criteria = session.createCriteria(ActionPlan.class)
        .createAlias("createdInMove", "MOVE")
        .add(Restrictions.gt("MOVE.number", 1))
        .setProjection(Projections.rowCount());

    count = ((Long) criteria.list().get(0)).intValue();
    return count>0;
  }
  
  protected void addImageContent(Element imageEl, Media med)
  {
    addElementWithText(imageEl, "ImagePngBase64", "omitted");
    return;

    /*
     * // ENCODING BufferedImage img = ImageIO.read(new File("image.png"));
     * ByteArrayOutputStream baos = new ByteArrayOutputStream();
     * ImageIO.write(img, "png", baos); baos.flush(); String encodedImage =
     * Base64.encodeToString(baos.toByteArray()); baos.close(); // should be
     * inside a finally block node.setTextContent(encodedImage); // store it
     * inside node
     *
     * // DECODING String encodedImage = node.getTextContent(); byte[] bytes =
     * Base64.decode(encodedImage); BufferedImage image = ImageIO.read(new
     * ByteArrayInputStream(bytes)); }
     */

    /*
     * ByteArrayOutputStream baos; String imageString; try { BufferedImage bi =
     * ImageIO.read(new URL(med.getUrl())); baos = new ByteArrayOutputStream();
     * ImageIO.write(bi, "png", baos); baos.flush();
     *
     * imageString = Base64.encodeBase64String(baos.toByteArray());
     * baos.close(); } catch (Exception ex) { imageString =
     * "Image encoding error: " + ex.getLocalizedMessage(); }
     *
     * addElementWithText(imageEl, "ImagePngBase64", imageString);
     */
  }

  protected class ImageSize
  {
    public Image image;
    public Dimension size;
    public Dimension scaledSize;
  }

  protected ImageSize getImageSize(String url)
  {
    ImageSize iSz = new ImageSize();
    try {
      iSz.image = Toolkit.getDefaultToolkit().getImage(new URL(url));
      long startTime = System.currentTimeMillis();

      do {
        int imgW = iSz.image.getWidth(null);
        int imgH = iSz.image.getHeight(null);
        if (imgW > 0 && imgH > 0) {
          iSz.size = new Dimension(imgW, imgH);
          return getScaledImageSize(iSz);
        }
        Thread.sleep(100l);
      } while (System.currentTimeMillis() - startTime < 10000l); // 10 secs

    } catch (MalformedURLException e) {
      System.err.println("Can't use image url: " + url);
    } catch (InterruptedException intEx) {
      System.err.println("Image wait thread sleep interrupted");
    }
    iSz.size = new Dimension(100, 100);
    iSz.scaledSize = new Dimension(100, 100);
    return iSz;
  }

  protected ImageSize getScaledImageSize(ImageSize iSz)
  {
    Dimension d = new Dimension(iSz.size);
    if (iSz.size.width > 800) {
      d.height = (int) ((float) iSz.size.height * (800.0f / (float) iSz.size.width));
      d.width = 800;
    }
    if (d.height > 600) {
      d.width = (int) ((float) d.width * (600.0f / (float) d.height));
      d.height = 600;
    }
    iSz.scaledSize = d;
    return iSz;
  }

  protected void showFile(String handle, Document doc, String name, String styleSheetNameInThisPackage, boolean showXml) throws TransformerConfigurationException, TransformerException
  {
    showFile(handle, doc,name,styleSheetNameInThisPackage,null,showXml);
  }
  
  protected StringWriter doc2Xml(Document doc, String cdataElementList) throws TransformerException
  {
    Transformer trans = TransformerFactory.newInstance().newTransformer();
    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    trans.setOutputProperty(OutputKeys.INDENT, "yes");
    trans.setOutputProperty(OutputKeys.METHOD, "xml");
    if(cdataElementList != null)
      trans.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, cdataElementList);
    trans.setErrorListener(new ErrorListener()
    {
      @Override
      public void error(TransformerException ex) throws TransformerException
      {
        System.err.println("Err: " + ex.getLocalizedMessage());
      }

      @Override
      public void fatalError(TransformerException ex) throws TransformerException
      {
        System.err.println("Fat: " + ex.getLocalizedMessage());
      }

      @Override
      public void warning(TransformerException ex) throws TransformerException
      {
        System.err.println("Warn: " + ex.getLocalizedMessage());
      }
    });
    StringWriter sw = new StringWriter();  // where resultant xml gets put
    StreamResult result = new StreamResult(sw);
    DOMSource source = new DOMSource(doc);
    trans.transform(source, result); // puts the doc into the result, which is a writer

    return sw;
  }
    
  protected StringWriter doc2Html(Document doc, StringWriter xmlStrWriter, String styleSheetNameInThisPackage) throws TransformerFactoryConfigurationError, TransformerException
  {
    // relative to this class's package
    InputStream ssInpStr = getClass().getResourceAsStream(styleSheetNameInThisPackage);
    javax.xml.transform.stream.StreamSource styleSheetSource = new javax.xml.transform.stream.StreamSource(ssInpStr);
    Transformer ssTrans = TransformerFactory.newInstance().newTransformer(styleSheetSource);
    Map<String,String> params = getStaticTransformationParameters();
    if(params != null && params.size()>0) {
      for(String key : params.keySet()) {
        String val = params.get(key);
        if(val != null && val.length()>0) {
          ssTrans.setParameter(key,val);
        }
      }
    }

    final StringWriter htmlSW = new StringWriter();
    StreamResult htmlSR = new StreamResult(htmlSW);
    InputStream is = new ByteArrayInputStream(xmlStrWriter.toString().getBytes());   // setup by doc2Xml
    ssTrans.transform(new javax.xml.transform.stream.StreamSource(is), htmlSR);
    return htmlSW;
  }
  
  protected void showFile(String handl, Document doc, String name, String styleSheetNameInThisPackage, String cdataElementList, boolean showXml) throws TransformerConfigurationException, TransformerException
  {
    StringWriter xmlSW = doc2Xml(doc, cdataElementList);
    StringWriter htmlSW = null;
    // style to html if we can find a style sheet on our classpath
    if (styleSheetNameInThisPackage != null)
      // Do the transformation
      htmlSW = doc2Html(doc, xmlSW, styleSheetNameInThisPackage);  
    
    if(!showXml)
      xmlSW = null;
    
    if(xmlSW == null && htmlSW == null)
      ;
    else
      showExport(handl, xmlSW, htmlSW);
  }
  
  private void showExport(final String handle, final StringWriter xmlSW, final StringWriter htmlSW)
  {
    // Build a source for browser display of xml
    Mmowgli2UI.getAppUI().access(new Runnable()
    {
      public void run()
      {
        if(htmlSW != null)
          BrowserWindowOpener.openHtmlReport(htmlSW.toString(), handle, "_blank"); 
        if(xmlSW != null)
          BrowserWindowOpener.openXmlReport(xmlSW.toString(),handle+" XML", "_blank");
        
        Mmowgli2UI.getAppUI().access(new Runnable(){public void run(){Mmowgli2UI.getAppUI().push();}});
      }
    });
  }
  
  public String toUtf8(final String inString)
  {
    if (null == inString)
      return null;
    byte[] byteArr = inString.getBytes();
    for (int i = 0; i < byteArr.length; i++) {
      byte ch = byteArr[i];
      // remove any characters outside the valid UTF-8 range as well as all
      // control characters
      // except tabs and new lines
      if (!((ch > 31 && ch < 253) || ch == '\t' || ch == '\n' || ch == '\r')) {
        byteArr[i] = ' ';
      }
    }
    return new String(byteArr);
  }

  protected void showStartNotification(String exportType)
  {
    Notification notif = new Notification("",
        "Export of " + exportType + " begun.  Results may appear in another browser window (unless popups blocked).",
        Notification.Type.WARNING_MESSAGE); // not warning, but want yellow

    notif.setPosition(Position.MIDDLE_CENTER);
    notif.setDelayMsec(-1);
    notif.show(Page.getCurrent());
  }

  protected String nn(String s)
  {
    return s == null ? "" : s;
  }

  protected void showEndNotification(String exportType)
  {
    Notification notif = new Notification("", "Export of " + exportType + " complete.  Results may appear in another browser window (unless popups blocked).",
        Notification.Type.WARNING_MESSAGE);

    notif.setPosition(Position.TOP_CENTER);
    notif.setDelayMsec(5000);
    notif.show(Page.getCurrent());
  }

  public interface MetaListener
  {
    public void continueOrCancel(String s);
  }
 // from game
  /*  
  private void addCall2Action(Element root, Session sess, Game g)
  {
    Element call2ActionElem = createAppend(root,"CallToAction");
    //addElementWithText(call2ActionElem,"ScreenShot","callToActionScreenShot.png");

    MovePhase phase = MovePhase.getCurrentMovePhase(sess);

    String s = "";
    Media vid = phase.getCallToActionBriefingVideo();
    if(vid != null) {
      String url = vid.getUrl();
      if(url != null)
        s = url;
    }
    addElementWithText(call2ActionElem,"Video",s==null?"":s);

    s = phase.getCallToActionBriefingSummary();
    addElementWithText(call2ActionElem,"BriefingSummary",s==null?"":s);
    s = phase.getCallToActionBriefingText();
    addElementWithText(call2ActionElem,CALL2ACTION_BRIEFINGTEXT,s==null?"":s);
  }
*/ 
  /*
  protected void addCallToAction(Element root, Session sess)
  {
    MovePhase mp = MovePhase.getCurrentMovePhase(sess);
    Element cto = createAppend(root,"CallToAction");
    String vidUrl = "";
    Media vid = mp.getCallToActionBriefingVideo();
    if(vid != null)
      vidUrl = vid.getUrl();
    addElementWithText(cto,"VideoYouTubeID",toUtf8(nn(vidUrl)));
    addElementWithText(cto,"VideoAlternateUrl","");
    addElementWithText(cto,"BriefingSummary",toUtf8(nn(mp.getCallToActionBriefingSummary())));    
    addElementWithText(cto,BRIEFING_TEXT_ELEM,toUtf8(nn(mp.getCallToActionBriefingText())));
  }
  */
  @SuppressWarnings("unchecked")
  protected void newAddCall2Action(Element root, Session sess, Game g)
  {
    List<Move> lis = sess.createCriteria(Move.class).list();
    Collections.sort(lis, new Comparator<Move>() {
      @Override
      public int compare(Move m1, Move m2)
      {
        return m1.getNumber()-m2.getNumber();
      }     
    });
    Move currentMove = g.getCurrentMove();
    for(Move m : lis){
      if(m.getNumber()<= currentMove.getNumber())  // if any moves have been defined, but never executed, skip them
        _addMoveCall2Action(root,sess,m);
    }
  }
  
  private void _addMoveCall2Action(Element root, Session sess, Move m)
  {
//    List<MovePhase> lis = m.getMovePhases();
//    Collections.sort(lis, new Comparator<MovePhase>() {
//      @Override
//      public int compare(MovePhase o1, MovePhase o2)
//      {
//        return (int)(o1.getId()-o2.getId());
//      }     
//    });
//    for(MovePhase mp : lis)
//      _addMovePhaseCall2Action(root,sess, m, mp);

    // Only the current/last phase gets shown
    _addMovePhaseCall2Action(root,sess,m,m.getCurrentMovePhase());
  }
  
  private void _addMovePhaseCall2Action(Element root, Session sess, Move m, MovePhase phase)
  {
    Element call2ActionElem = createAppend(root,"CallToAction");
    call2ActionElem.setAttribute("round", ""+m.getNumber());
    call2ActionElem.setAttribute("phase", phase.getDescription());
   
    String s = "";
    Media vid = phase.getCallToActionBriefingVideo();
    if(vid != null) {
      String url = vid.getUrl();
      if(url != null)
        s = url;
    }
    addElementWithText(call2ActionElem,"VideoYouTubeID",toUtf8(nn(s)));
    addElementWithText(call2ActionElem,"VideoAlternateUrl","");

    s = phase.getCallToActionBriefingSummary();
    addElementWithText(call2ActionElem,"BriefingSummary",toUtf8(nn(s)));
    s = phase.getCallToActionBriefingText();
    addElementWithText(call2ActionElem,BRIEFING_TEXT_ELEM,toUtf8(nn(s)));    
  }
  /*
  protected void addCallToAction(Element root, Session sess)
  {
    MovePhase mp = MovePhase.getCurrentMovePhase(sess);
    Element cto = createAppend(root,"CallToAction");
    String vidUrl = "";
    Media vid = mp.getCallToActionBriefingVideo();
    if(vid != null)
      vidUrl = vid.getUrl();
    addElementWithText(cto,"VideoYouTubeID",toUtf8(nn(vidUrl)));
    addElementWithText(cto,"VideoAlternateUrl","");
    addElementWithText(cto,"BriefingSummary",toUtf8(nn(mp.getCallToActionBriefingSummary())));    
    addElementWithText(cto,BRIEFING_TEXT_ELEM,toUtf8(nn(mp.getCallToActionBriefingText())));
  }
  */
  
  public String buildFileName(String prefix)
  {
    Object sessKey = HSess.checkInit();
    Game game = Game.getTL();
    String name = prefix+"_"+game.getTitle();
    String ret = name.replaceAll(" ", "_"); // no spaces
    HSess.checkClose(sessKey);
    return ret;
  }
}
