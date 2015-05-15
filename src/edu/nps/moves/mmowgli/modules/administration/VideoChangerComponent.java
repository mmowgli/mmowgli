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

import java.lang.reflect.Method;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;

/**
 * VideoChangerComponent.java
 * Created on Apr 4, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class VideoChangerComponent extends HorizontalLayout implements ClickListener
{
  private static final long serialVersionUID = -1085118491132091846L;
  
  private MovePhase mp;
  private Media currentMedia;
  private Method movePhaseSetter;
  private TextField roTF;
  VideoChangerComponent(MovePhase mp, String setterMethodName, Media m, GameDesignGlobals globs)
  {
    this.mp = mp;
    this.currentMedia = m;
    try {
      movePhaseSetter = MovePhase.class.getDeclaredMethod(setterMethodName, new Class<?>[]{Media.class});
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
   
    setSpacing(true);
    setMargin(false);
    setSizeUndefined();

    addComponent(roTF = new TextField());
    roTF.addStyleName("m-textarea-greyborder");
    //tf.setColumns(20);
    roTF.setValue(currentMedia==null?"":currentMedia.getUrl());
    roTF.setReadOnly(true);
    Button butt;
    addComponent(butt=new Button("change",this));
    setComponentAlignment(butt,Alignment.MIDDLE_CENTER);
    butt.addStyleName(Reindeer.BUTTON_SMALL);
    butt.setReadOnly(globs.readOnlyCheck(false));
    butt.setEnabled(!butt.isReadOnly());
  }
  
  @Override
  public void buttonClick(ClickEvent event)
  {
    new EditYoutubeIdDialog().showDialog(this);    
  }
  
  @SuppressWarnings("serial")
  public class EditYoutubeIdDialog extends Window
  {
    TextField tf;
    Button saveButt,cancelButt;
    public EditYoutubeIdDialog()
    {
      super("Enter new Youtube ID");
      setSizeUndefined();
      setWidth("285px");
      VerticalLayout vLay;
      setContent(vLay = new VerticalLayout());
      vLay.setSpacing(true);
      vLay.setMargin(true);
      
      vLay.addComponent(tf = new TextField("Youtube ID"));
      tf.setColumns(20);
          
      HorizontalLayout buttHLay;
      vLay.addComponent(buttHLay= new HorizontalLayout());
      buttHLay.setWidth("100%");
      buttHLay.setSpacing(true);
      Label lab;
      buttHLay.addComponent(lab=new Label());
      lab.setWidth("1px");
      buttHLay.setExpandRatio(lab, 1.0f);
      buttHLay.addComponent(cancelButt = new Button("Cancel",saveCancelListener));
      buttHLay.addComponent(saveButt = new Button("Save",saveCancelListener));
    }
 
    public void showDialog(Component parent)
    {
      UI.getCurrent().addWindow(this);
      center();
    }
    
    ClickListener saveCancelListener = new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        if(event.getButton() == saveButt) {
          String newUrl = tf.getValue().toString().trim();
          if(currentMedia != null && newUrl.equals(currentMedia.getUrl()))
            return;
          
          HSess.init();
          Media m;
          if(newUrl.length()<=0 && currentMedia != null) {
            m = null;
          }
          else if(currentMedia != null) {
            m = new Media();
            m.cloneFrom(currentMedia);
            m.setUrl(newUrl);
          }
          else {
            m = Media.newYoutubeMedia(newUrl);            
          }
          
          if(m != null)
            Media.saveTL(m);         
          try {
            movePhaseSetter.invoke(mp, m);
          }
          catch (Exception e) {
            throw new RuntimeException(e);
          }
          MovePhase.updateTL(mp);
          
          currentMedia = m;
          roTF.setReadOnly(false);
          roTF.setValue(newUrl);
          roTF.setReadOnly(true);
          
          HSess.close();
        }       
        UI.getCurrent().removeWindow(EditYoutubeIdDialog.this);
      }
    };
  }
}
