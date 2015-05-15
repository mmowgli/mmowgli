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

import org.vaadin.cssinject.CSSInject;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;

/**
 * MmowgliDialogContent.java
 * Created on Aug 29, 2011
 * Updated on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliDialogContent extends HorizontalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = -7264754054128836048L;
  
  private VerticalLayout leftVL;
  private HorizontalLayout headerWrapper;
  private VerticalLayout contentVLayout;
  private Label footer;
  private VerticalLayout rightShadowVL;
  private Label leftEdge,rightEdge;
  private Embedded corner;
  private Button cancelButt;
  private Label titleLab;
  private HorizontalLayout header;
  protected String titleStyle = "m-dialog2-title";
  protected String titleStyleSmall = "m-dialog2-title-smaller";
  
  public static void throwUpDialog2()
  {
    Window w = new Window();
    w.setClosable(false);
    w.setResizable(true);
    w.setStyleName("m-mmowglidialog2");
    w.addStyleName("m-transparent");   // don't know why I need this, .mmowglidialog sets it too
    w.setWidth("600px");
    w.setHeight("400px");
    MmowgliDialogContent con = new MmowgliDialogContent();
    w.setContent(con);
    con.setSizeFull();
    con.initGui();
    con.setTitleString("Yippee ki awol!");

    UI.getCurrent().addWindow(w);
    w.center();
  
  }
  public MmowgliDialogContent()
  {
    leftVL = new VerticalLayout();
    leftVL.setSpacing(false);
    headerWrapper = new HorizontalLayout();
    contentVLayout = new VerticalLayout();
    contentVLayout.setSpacing(false);
    rightShadowVL = new VerticalLayout();
    rightShadowVL.setSpacing(false);
    
    footer = new Label();
    footer.addStyleName("m-mmowglidialog2-footer"); // has the background
    
    rightEdge = new Label();
    rightEdge.addStyleName("m-mmowglidialog2-rightedge");
    
    corner = new Embedded(null,new ExternalResource("https://web.mmowgli.nps.edu/mmowMedia/images/dialog2Corner28w36h.png"));
    
    leftEdge = new Label();
    leftEdge.addStyleName("m-mmowglidialog2-leftedge");
  }
  
  private void doHeaderWrapper()
  {
    Label sp;
    VerticalLayout titleWrapper = new VerticalLayout();
    titleWrapper.setSpacing(false);
    titleWrapper.setMargin(false);
    titleWrapper.setHeight("75px");
    titleWrapper.setWidth("100%");//("460px"); //"504px"); //"592px");
    headerWrapper.addComponent(titleWrapper);
    headerWrapper.setExpandRatio(titleWrapper, 1.0f);
//    titleWrapper.addComponent(sp=new Label());
//    sp.setHeight("25px");
    
    header = new HorizontalLayout();  // Where the title gets written
    header.setSpacing(false);
    header.setMargin(false);
    header.setHeight("55px");
    header.setWidth("100%");
    header.addStyleName("m-transparent");
    titleWrapper.addComponent(header);
    titleWrapper.setComponentAlignment(header, Alignment.MIDDLE_CENTER);
    headerWrapper.addComponent(sp = new Label());
    sp.setWidth("25px"); //"50px");
    
    cancelButt = makeCancelButton();
    cancelButt.setClickShortcut(KeyCode.ESCAPE);
    
    headerWrapper.addComponent(cancelButt);//, "top:9px;left:504px");
    headerWrapper.setComponentAlignment(cancelButt, Alignment.MIDDLE_CENTER);
  }
  protected Button makeCancelButton()
  {
    NativeButton butt = new NativeButton(null);
    butt.setStyleName("m-cancelButton");
    return butt;
  }
  protected void setTitleString(String s)
  {
    setTitleString(s,false);
  }
  protected void setTitleString(String s, boolean small)
  {
    if (titleLab != null)
      header.removeComponent(titleLab);
    titleLab = new Label(s);
    titleLab.addStyleName(small?titleStyleSmall:titleStyle);
    titleLab.setWidth("450px"); // can't overlay cancel butt
    header.addComponent(titleLab); //, "top:25px;left:50px");
    header.setComponentAlignment(titleLab, Alignment.MIDDLE_LEFT);
  }
  
  private CSSInject css = null;
  @Override
  public void attach()
  {
    super.attach();
    CSSInject css = new CSSInject(UI.getCurrent());
    //css.setValue(".custom-style { color: rgb(100, 200, 300); }");
    css.setStyles(".v-shadow-window {display: none;}");
  }
  @Override
  public void detach()
  {
    super.detach();
    if(css != null) {
      css.remove();
      css=null;
    }
  }
  public void initGui()
  {
    addComponent(leftVL);
    leftVL.setSizeFull();
    
    setExpandRatio(leftVL, 1.0f);
    
    leftVL.addComponent(headerWrapper);
    headerWrapper.addStyleName("m-mmowglidialog2-header");
    headerWrapper.setHeight("75px");
    headerWrapper.setWidth("100%");
    
    doHeaderWrapper();
       
    HorizontalLayout midHL = new HorizontalLayout();
    leftVL.addComponent(midHL);
    leftVL.setExpandRatio(midHL, 1.0f);
    midHL.setHeight("100%");
    midHL.setWidth("100%");
    midHL.addComponent(leftEdge);
    leftEdge.setHeight("100%");
    leftEdge.setWidth("10px");
    midHL.addComponent(contentVLayout);
    contentVLayout.addStyleName("m-mmowglidialog2-middle");
    contentVLayout.setSizeFull();
    midHL.setExpandRatio(contentVLayout, 1.0f); // gets it all
    leftVL.addComponent(footer);
    footer.setWidth("100%");  
    footer.setHeight("36px");
    
    addComponent(rightShadowVL);
    rightShadowVL.setHeight("100%");
    rightShadowVL.setWidth("28px");
    rightShadowVL.addComponent(rightEdge);
    rightEdge.setHeight("100%");
    rightEdge.setWidth("28px");
   // rightEdge.addStyleName("m-clip-overflow");
    rightShadowVL.setExpandRatio(rightEdge, 1.0f);
    rightShadowVL.addComponent(corner);
  }
  
  public void setCancelListener(ClickListener lis)
  {
    cancelButt.addClickListener(lis);
  }
  
  public VerticalLayout getContentVLayout()
  {
    return contentVLayout;
  }
}
