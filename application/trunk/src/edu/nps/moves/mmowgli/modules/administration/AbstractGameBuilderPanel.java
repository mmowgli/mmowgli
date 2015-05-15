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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;

/**
 * AbstractGameBuilder.java Created on Nov 1, 2012
 * Updated 12 Mar, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: AbstractGameBuilderPanel.java 3267 2014-01-11 00:54:40Z tdnorbra $
 */
abstract class AbstractGameBuilderPanel extends VerticalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = 5870704500956108449L;
  
  abstract Embedded getImage(); // can be null
  protected void testButtonClickedTL(ClickEvent ev) {};

  transient private List<EditLine> lines;
  private GridLayout grid;
  private int COL1PIXELWIDTH = 80;
  private int COL2PIXELWIDTH = 240;
  private boolean showTestButton = true;
  private boolean autoSave = true;
  protected Move runningMove;

  protected Boolean okToUpdateDbFlag = true;

  protected GameDesignGlobals globals;

  private DoUpdates updatesOK = new DoUpdates()
  {
    @Override
    public boolean updatesOK()
    {
      return okToUpdateDbFlag;
    }
  };

  public interface MoveListener
  {
    public void setMove(Move m);
  };

  public AbstractGameBuilderPanel(boolean showTestButton, GameDesignGlobals globs)
  {
    this(showTestButton,true,globs);
  }

  @HibernateSessionThreadLocalConstructor
  public AbstractGameBuilderPanel(boolean showTestButton, boolean autoSave, GameDesignGlobals globs)
  {
    this.showTestButton = showTestButton;
    this.autoSave = autoSave;
    this.globals = globs;

    lines = new ArrayList<EditLine>();
    setSizeFull();
    setMargin(true);
    setSpacing(true);
    grid = new GridLayout();
    grid.setMargin(true);
    grid.setSpacing(true);
    grid.addStyleName("m-greyborder3");
    grid.addStyleName("m-greybackground");

    runningMove = Game.getTL().getCurrentMove();
  }

  protected String getTitle() // can be null
  {
    return null;
  }

  protected int getColumn1PixelWidth()
  {
    return COL1PIXELWIDTH;
  }

  protected int getColumn2PixelWidth()
  {
    return COL2PIXELWIDTH;
  }

  protected String getTextButtonText()
  {
    return "Click to view changes, then use browser back button to return";
  }

  private String getColumn2WidthString()
  {
    return "" + getColumn2PixelWidth() + "px";
  }

  private String getColumn1WidthString()
  {
    return "" + getColumn1PixelWidth() + "px";
  }

  protected void changeMove(Move m)
  {
    for (EditLine eLine : lines) {
      if (eLine.listener != null) {
        eLine.listener.setMove(m);
      }
      else if (eLine.getter != null) {
        if (eLine.getter.getDeclaringClass() == Move.class) {
          eLine.objId = m.getId();
          populateEditLine(eLine);
        }
        else if (eLine.getter.getDeclaringClass() == MovePhase.class) {
          eLine.objId = m.getCurrentMovePhase().getId();
          populateEditLine(eLine);
        }
      }
    }
  }

  protected void changeMovePhase(MovePhase mp)
  {
    for (EditLine eLine : lines) {
      if (eLine.getter != null && eLine.getter.getDeclaringClass() == MovePhase.class) {
        eLine.objId = mp.getId();
        populateEditLine(eLine);
      }
    }
  }

  protected void changeCardType(CardType ct)
  {
    for (EditLine eLine : lines) {
      if (eLine.getter != null && eLine.getter.getDeclaringClass() == CardType.class) {
        eLine.objId = ct.getId();
        populateEditLine(eLine);
      }
    }

  }
  public static boolean isRunningMoveTL(Move m)
  {
    return Move.getCurrentMoveTL().getId() == m.getId();
  }

  public static boolean isRunningPhaseTL(MovePhase mp)
  {
    return MovePhase.getCurrentMovePhaseTL().getId() == mp.getId();
  }

  protected void addThirdColumnComponent(AbstractComponent c)
  {
    lines.add(new EditLine("","",c,null,null,null));
  }

  protected TextArea addEditLine(String name, String info )
  {
    TextArea ta = new TextArea();
    ta.setRows(2);
    ta.setReadOnly(globals.readOnlyCheck(false));
    lines.add(new EditLine( name, info, ta, null, null, null));
    return ta;
  }
  
  protected EditLine addEditLine(String name, String info, Object dbObj, Object dbObjId, String dbObjFieldName)
  {
    return addEditLine(name,info,dbObj,dbObjId,dbObjFieldName,null);
  }
  
  protected EditLine addEditLine(String name, String info, Object dbObj, Object dbObjId, String dbObjFieldName, MoveListener lis)
  {
    return addEditLine(name,info,dbObj,dbObjId,dbObjFieldName,lis,null);
  }

  protected EditLine addEditLine(String name, String info, Object dbObj, Object dbObjId, String dbObjFieldName, MoveListener lis, Class<?> fieldClass)
  {
    return addEditLine(name,info,dbObj,dbObjId,dbObjFieldName,lis,fieldClass,null);
  }

  protected EditLine addEditLine(String name, String info, Object dbObj, Object dbObjId, String dbObjFieldName, MoveListener lis, Class<?> fieldClass, String tooltip)
  {
    TextArea ta = new TextArea();
    ta.setRows(2);
    ta.setReadOnly(globals.readOnlyCheck(false));

    EditLine edLine = getLineData(dbObj);
    edLine.name=name;
    edLine.info=info;
    edLine.ta = ta;
    edLine.fieldName = dbObjFieldName;
    edLine.objId = dbObjId;
    edLine.listener = lis;
    edLine.fieldClass = fieldClass;
    edLine.setTooltip(tooltip);
    lines.add(edLine);
    populateEditLine(edLine);
    return edLine;
  }

  protected EditLine addEditBoolean(String name, String info, Object dbObj, Object dbObjId, String dbObjFieldName)
  {
    return addEditBoolean(name,info,dbObj,dbObjId,dbObjFieldName,null,null);
  }

  protected EditLine addEditBoolean(String name, String info, Object dbObj, Object dbObjId, String dbObjFieldName, String tooltip)
  {
    return addEditBoolean(name,info,dbObj,dbObjId,dbObjFieldName,null,tooltip);
  }

  protected EditLine addEditBoolean(String name, String info, Object dbObj, Object dbObjId, String dbObjFieldName, MoveListener lis, String tooltip)
  {
    CheckBox cb = new CheckBox();
    cb.setReadOnly(globals.readOnlyCheck(false));

    EditLine edLine = getLineData(dbObj);
    edLine.name=name;
    edLine.info=info;
    edLine.ta = cb;
    edLine.fieldName = dbObjFieldName;
    edLine.objId = dbObjId;
    edLine.listener = lis;
    edLine.setTooltip(tooltip);
    lines.add(edLine);
    populateEditLine(edLine);
    return edLine;
  }

  protected EditLine addEditComponent(String name, String info, AbstractComponent comp)
  {
    EditLine edLine = new EditLine();
    edLine.name= name;
    edLine.info = info;
    edLine.ta = comp;
    lines.add(edLine);
    return edLine;
  }

  protected void addSeparator()
  {
    lines.add(new EditLine()); // empty implies space
  }

  /**
   * Gets the static getter method from the Hibernate class, of the form of Blah.getTL() or Blah.get() or Blah.get(long)
   */
  public static Method getGetter(Object dbObj)
  {
    Method objGetter;
    try {
      objGetter = dbObj.getClass().getDeclaredMethod("getTL", new Class<?>[] { Object.class });
    }
    catch (Exception ex) {
      objGetter = null;
    }
    
    if (objGetter == null) {
      try {
        objGetter = dbObj.getClass().getDeclaredMethod("get", new Class<?>[] { Object.class });
      }
      catch (Exception ex) {
        objGetter = null;
      }
    }
    
    if (objGetter == null) {
      try {
        objGetter = dbObj.getClass().getDeclaredMethod("get", new Class<?>[] { Serializable.class });
      }
      catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
    return objGetter;
  }
  
  private EditLine getLineData(Object dbObj)
  {
    Method objGetter = getGetter(dbObj);
    return new EditLine( null, null, null, null, objGetter, null );
  }

  protected void addComponentLine(AbstractComponent c)
  {
    lines.add(new EditLine (null,null,c,null,null,null));
  }

  protected void addLineComponent(GridLayout grid, int row, Component c)
  {
    grid.addComponent(c, 0, row, 2, row);
  }
  protected void addSeparator(GridLayout grid, int row)
  {
    Label lab;
    grid.addComponent(lab=new Label(),0,row,2,row);
    lab.setHeight("5px");
    lab.addStyleName("m-greybackground");
  }
  protected String getHeading()
  {
    return null;
  }

  protected Component getFooter()
  {
    return null;
  }

  @Override
  public void initGui()
  {
    String title = getTitle();
    if (title != null) {
      Label titleLab;
      addComponent(titleLab = new Label(title));
      titleLab.addStyleName("m-centeralign");
    }
    Embedded e = this.getImage();
    if (e != null) {
      e.setWidth("800px"); // "930px");
      e.setHeight("400px"); // "465px");
      e.addStyleName("m-greyborder3");
      addComponent(e);
      setComponentAlignment(e, Alignment.MIDDLE_CENTER);
    }

    if (lines.size() > 0) {
      grid.setColumns(3);
      String heading = getHeading();
      Component footer = getFooter();
      int nRows = lines.size() + (heading!=null? 1:0) + (footer!=null? 1:0);
      grid.setRows(nRows);
      int rowOffst = 0;

      if(heading != null) {
        grid.addComponent(makeLabel(heading), 0, 0, 2, 0);
        rowOffst = 1;
      }
      for (int r = 0; r < lines.size(); r++) {
        EditLine edLine = lines.get(r);
        if(edLine.ta != null)
          edLine.ta.setDescription(edLine.tooltip);

        if(edLine.isSeparator()) {
          addSeparator(grid,r+rowOffst);
          continue;
        }
        if(edLine.justComponent()) {
          addLineComponent(grid,r+rowOffst,edLine.ta);
          continue;
        }
        Label textLab = new HtmlLabel(edLine.name);
        textLab.setDescription(edLine.tooltip);
        textLab.addStyleName("m-font-bold14");
        textLab.setWidth(getColumn1WidthString());
        grid.addComponent(textLab, 0, r+rowOffst); // c0,r0,c1,r1

        Label fieldLab = new Label(edLine.info);
        fieldLab.setDescription(edLine.tooltip);
        fieldLab.addStyleName("m-italic");
        fieldLab.setWidth(getColumn2WidthString());
        grid.addComponent(fieldLab, 1, r+rowOffst);

        if(edLine.ta instanceof TextArea) {
          TextArea ta = (TextArea) edLine.ta;
          ta.setDescription(edLine.tooltip);
          ta.setImmediate(true);
          ta.setWidth("100%");

          if(edLine.fieldName != null && autoSave)
             ta.addValueChangeListener(new IndivListener(edLine,updatesOK,edLine.fieldClass==null?String.class:edLine.fieldClass));
          grid.addComponent(ta, 2, r+rowOffst);
        }
        else if(edLine.ta instanceof CheckBox) {
          CheckBox cb = (CheckBox) edLine.ta;
          cb.setDescription(edLine.tooltip);
          cb.setImmediate(true);

          if(edLine.fieldName != null && autoSave)
             cb.addValueChangeListener(new IndivListener(edLine,updatesOK,boolean.class));
          grid.addComponent(cb, 2, r+rowOffst);
        }
        else if(edLine.ta instanceof Component){
          grid.addComponent(edLine.ta,2,r+rowOffst);
        }
      }
      if(footer != null) {
        int frow = lines.size()+rowOffst;
        grid.addComponent(footer,0, frow, 2, frow);
      }
      grid.setWidth("99%");
      grid.setHeight("100%");

      addComponent(grid);
      grid.setColumnExpandRatio(2, 1.0f);

      if (showTestButton) {
        Button testButt = new Button(getTextButtonText(), new ClickListener()
        {
          private static final long serialVersionUID = 1L;

          @Override
          @MmowgliCodeEntry
          @HibernateOpened
          @HibernateClosed
          public void buttonClick(ClickEvent event)
          {
            HSess.init();
            testButtonClickedTL(event);
            HSess.close();           
          }
        });
        addComponent(testButt);
        setComponentAlignment(testButt, Alignment.MIDDLE_CENTER);
      }
    }
  }

  private Label makeLabel(String s)
  {
    Label lab = new Label(s);
    lab.addStyleName("m-centeralign");
    return lab;
  }
  
  private void populateEditLine(EditLine eLine)
  {
    if (eLine.fieldName != null) {
      try {
        Class<? extends Object> hiCls = eLine.getter.getDeclaringClass();
        if (eLine.ta instanceof AbstractTextField) {
          Method getter = hiCls.getDeclaredMethod("get" + eLine.fieldName, (Class<?>[]) null);
          Object dbObj = eLine.getter.invoke(null, new Object[] { eLine.objId });
          AbstractTextField atf = (AbstractTextField)eLine.ta;
          boolean origRo = atf.isReadOnly();
          atf.setReadOnly(false);
          Object val = getter.invoke(dbObj, (Object[])null);
          atf.setValue(val==null?"":val.toString());
          atf.setReadOnly(origRo);
        }
        else if (eLine.ta instanceof CheckBox) {
          CheckBox cb = (CheckBox)eLine.ta;
          Method getter = hiCls.getDeclaredMethod("is" + eLine.fieldName, (Class<?>[]) null);
          Object dbObj = eLine.getter.invoke(null, new Object[] { eLine.objId });
          boolean origRo = cb.isReadOnly();
          cb.setReadOnly(false);
          Object val = getter.invoke(dbObj, (Object[]) null);
          ((CheckBox) eLine.ta).setValue(val==null?Boolean.FALSE:(Boolean)val);
          cb.setReadOnly(origRo);
        }
      }
      catch (Exception exc) {
        System.err.println("Programming error in AbstractGameBuilderPanel.populateEditLine: " + exc.getClass().getSimpleName() + ": "
            + exc.getLocalizedMessage());
        exc.printStackTrace();
      }
    }
  }

  public interface DoUpdates
  {
    public boolean updatesOK();
  }

  public static interface AuxiliaryChangeListener
  {
    public void valueChange(IndivListener indLis, Property.ValueChangeEvent event);
  }

  @SuppressWarnings("serial")
  public static class IndivListener implements Property.ValueChangeListener
  {
    transient Method setter, update;
    transient EditLine edLine;
    transient DoUpdates updatesOK;

    public IndivListener(EditLine edLine, DoUpdates updatesOK)
    {
      this(edLine,updatesOK,String.class);
    }
    public IndivListener(EditLine edLine, DoUpdates updatesOK, Class<?> valueClass)
    {
      this.edLine = edLine;
      this.updatesOK = updatesOK;

      try {
        Class<? extends Object>hiCls = edLine.getter.getDeclaringClass();
        setter = hiCls.getDeclaredMethod("set" + edLine.fieldName, new Class<?>[] { valueClass });
        update = hiCls.getDeclaredMethod("updateTL", new Class<?>[] { hiCls });
      }
      catch (Exception ex) {
        System.err.println("exception " + ex.getClass().getSimpleName() + " in IndivListener constructor");
        ex.printStackTrace();
      }
    }

    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void valueChange(Property.ValueChangeEvent event)
    {
      if (updatesOK.updatesOK()) {
        HSess.init();
        try {
          Object val = event.getProperty().getValue();
          Object dbObj = edLine.getter.invoke(null, new Object[] { edLine.objId});
          setter.invoke(dbObj, new Object[] { val });
          update.invoke(null, new Object[] { dbObj });
          String field = dbObj.getClass().getSimpleName() + "." + setter.getName();
          GameEventLogger.logGameDesignChangeTL(field, val==null?"NULL":val.toString(), Mmowgli2UI.getGlobals().getUserID());
        }
        catch (Exception ex) {
          System.err.println("exception " + ex.getClass().getSimpleName() + " in valueChange listener");
          ex.printStackTrace();
        }
        // See if someone else wants to do something
        if(edLine.auxListener != null)
          edLine.auxListener.valueChange(this, event);
        HSess.close();
      }
      else
        ;
    }
  }

  public static class EditLine
  {
    String name;
    String info;
    AbstractComponent ta;
    String fieldName;
    Method getter;
    Object objId;
    MoveListener listener;
    Class<?> fieldClass;
    String tooltip;
    AuxiliaryChangeListener auxListener;
    public String objectGetter = "get";
    
    public EditLine()
    {
    }
    public EditLine(String name,String info,AbstractComponent ta,String fieldName,Method getter,Object objId)
    {
      this(name,info,ta,fieldName,getter,objId,null);
    }
    public EditLine(String name,String info,AbstractComponent ta,String fieldName,Method getter,Object objId,MoveListener listener)
    {
      this(name,info,ta,fieldName,getter,objId,listener,null);
    }
    public EditLine(String name,String info,AbstractComponent ta,String fieldName,Method getter,Object objId,MoveListener listener,Class<?>fieldClass)
    {
      this.name=name;
      this.info=info;
      this.ta = ta;
      this.fieldName=fieldName;
      this.getter = getter;
      this.objId = objId;
      this.listener = listener;
      this.fieldClass = fieldClass;
    }

    public boolean justComponent()
    {
      return ta != null && name==null && info==null && fieldName==null && getter==null && objId==null;
    }
    public boolean isSeparator()
    {
      return ta == null && name==null && info==null && fieldName==null && getter==null && objId==null;
    }

    public void setTooltip(String s)
    {
      this.tooltip = s;
    }
  }
}