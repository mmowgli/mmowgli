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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.components.WebContentDisplayer;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;

/**
 * HeaderFooterGameDesignPanel.java
 * Created on Mar 28, 2013
 * Updated on Mar 12, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class SignupHTMLGameDesignPanel extends AbstractGameBuilderPanel implements MovePhaseChangeListener
{
  private static final long serialVersionUID = -5658127898958076712L;
  
  public static final String SIGNUPPAGEHEADERIMAGE_DEFAULT = "mmowgli_logo_final.png";

  private SignupImageTextFieldWithDefaultButton imgComponent;
  public SignupHTMLGameDesignPanel(MovePhase phase, AuxiliaryChangeListener auxLis, GameDesignGlobals globs)
  {
    super(false,globs);
    addEditComponent("Signup page header image", "MovePhase.signupHeaderImage", imgComponent = new SignupImageTextFieldWithDefaultButton(phase,globs));
    
    EditLine edLine = addEditLine("Signup page HTML", "MovePhase.signupText", phase, phase.getId(), "SignupText");
    TextArea ta = (TextArea)edLine.ta;
    ta.setRows(25);
    edLine.auxListener = auxLis;
    
    addThirdColumnComponent(makeButton("Show signup HTML",new RenderListener(ta)));
  }
  
  private AbstractComponent makeButton(String s, ClickListener lis)
  {
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth("100%");
    Button b;
    hl.addComponent(b=new Button(s,lis));
    hl.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
    return hl;
  }
  
  class RenderListener implements ClickListener
  {
    private static final long serialVersionUID = 1L;
    TextArea ta;
    RenderListener(TextArea ta)
    {
      this.ta = ta;
    }
    @Override
    public void buttonClick(ClickEvent event)
    {
      new WebContentDisplayer(ta.getValue().toString()).show(SignupHTMLGameDesignPanel.this,"500px","400px","Signup HTML");
    }
  }
 
  @Override
  Embedded getImage()
  {
    return null;
  }

  @Override
  public void movePhaseChanged(MovePhase newPhase)
  {
    okToUpdateDbFlag = false; 
    changeMovePhase(newPhase); 
    imgComponent.movePhaseChanged(newPhase);
    okToUpdateDbFlag = true; 
  }
  
  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 55; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 55; // default = 240
  }
  
  class SignupImageTextFieldWithDefaultButton extends HorizontalLayout implements ClickListener, ValueChangeListener
  {
    private static final long serialVersionUID = 1L;
    
    TextField tf;
    Button defaultButt;
    MovePhase phase;
    boolean nocommit = false;
    
    public SignupImageTextFieldWithDefaultButton(MovePhase phase, GameDesignGlobals globs)
    {
      this.phase = phase;
      setSpacing(true);
      setMargin(false);
      setSizeUndefined();

      addComponent(tf = new TextField());
      tf.setColumns(30);
      String val = phase.getSignupHeaderImage();
      if(val == null || val.trim().length()<=0)
        val = "";
      tf.setValue(val);
      tf.setReadOnly(globs.readOnlyCheck(false));
      tf.setDescription("Name of image in mmowgli repository which will be displayed at a size of 400 pixels wide by 114 pixels high.  Enter blank for no display.");
      tf.addValueChangeListener(this);
      addComponent(defaultButt=new Button("set to default",this));
      defaultButt.setDescription("Set the signup page header image to the default (mmowgli name and logo)");
      defaultButt.addStyleName(Reindeer.BUTTON_SMALL);
      setComponentAlignment(defaultButt, Alignment.MIDDLE_CENTER);
      defaultButt.setReadOnly(globs.readOnlyCheck(false));
      defaultButt.setEnabled(!defaultButt.isReadOnly());
      if(!defaultButt.isReadOnly())
        defaultButt.addClickListener((ClickListener)this);
    }
    
    @Override
    public void buttonClick(ClickEvent event)
    {
      boolean origRO = tf.isReadOnly();
      tf.setReadOnly(false);
      tf.setValue(SIGNUPPAGEHEADERIMAGE_DEFAULT);
      tf.setReadOnly(origRO);
    }
    
    public void movePhaseChanged(MovePhase phase)
    {
      this.phase = phase;
      nocommit=true;
      boolean origRO = tf.isReadOnly();
      tf.setReadOnly(false);
      String val = phase.getSignupHeaderImage();
      if(val == null || val.trim().length()<=0)
        val = "";
      tf.setValue(val);
      tf.setReadOnly(origRO);
      nocommit = false;
    }

    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void valueChange(ValueChangeEvent event)
    {
      if(!nocommit) {
        HSess.init();
        String val = event.getProperty().toString();
        if(val != null && val.trim().length()<=0)
          val = null;
        phase = MovePhase.mergeTL(phase);
        phase.setSignupHeaderImage(val);
        MovePhase.updateTL(phase);
        HSess.close();
      }
    }
  }
}
