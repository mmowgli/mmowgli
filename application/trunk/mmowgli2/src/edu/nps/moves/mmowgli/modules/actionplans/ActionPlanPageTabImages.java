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

package edu.nps.moves.mmowgli.modules.actionplans;

import static edu.nps.moves.mmowgli.MmowgliConstants.ERROR_LOGS;
import static edu.nps.moves.mmowgli.MmowgliConstants.MISC_LOGS;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.hibernate.Session;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.db.Image;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.WantsMediaUpdates;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPageTabVideos.VMPanelWrapper;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * ActionPlanPageTabImages.java Created on Feb 8, 2011
 * Updated on Mar 14, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanPageTabImages extends ActionPlanPageTabPanel implements WantsMediaUpdates
{
  private static final long serialVersionUID = -2281534361362318819L;
  
  private NativeButton addImageButt;
  private Panel imageScroller;

  private AddImageDialog addDialog;
  private ClickListener replaceLis;
  private Label nonAuthorLabel;
  
  @HibernateSessionThreadLocalConstructor
  public ActionPlanPageTabImages(Object apId, boolean isMockup, boolean readonly)
  {
    super(apId, isMockup, readonly);

    addImageButt = new NativeButton();
    replaceLis = new ImageReplacer();
  }

  @Override
  public void initGui()
  {
    setSizeUndefined();
    VerticalLayout leftLay = getLeftLayout();
    leftLay.setSpacing(false);
    leftLay.setMargin(false);

    VerticalLayout flowLay = new VerticalLayout();
    flowLay.setWidth("100%");
    leftLay.addComponent(flowLay);
    flowLay.setSpacing(true);

    Label missionLab = new Label("Authors, add some images!");
    flowLay.addComponent(missionLab);
    flowLay.setComponentAlignment(missionLab, Alignment.TOP_LEFT);
    missionLab.addStyleName("m-actionplan-mission-title-text");

    ActionPlan ap = ActionPlan.getTL(apId);

    Label missionContentLab;
    if(!isMockup)
      missionContentLab = new HtmlLabel(ap.getImagesInstructions());
    else {
      Game g = Game.getTL();
      missionContentLab = new HtmlLabel(g.getDefaultActionPlanImagesText());
    }
    flowLay.addComponent(missionContentLab);
    flowLay.setComponentAlignment(missionContentLab, Alignment.TOP_LEFT);
    flowLay.addStyleName("m-actionplan-mission-content-text");
    
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    flowLay.addComponent(addImageButt);
    addImageButt.addStyleName("m-actionplan-addimage-butt");
    addImageButt.addStyleName("borderless");
    addImageButt.setIcon(globs.getMediaLocator().getActionPlanAddImageButt());
    addImageButt.addClickListener(new ImageAdder());
    addImageButt.setEnabled(!isReadOnly);

    flowLay.addComponent(nonAuthorLabel = new Label("Authors may add images when editing the plan."));
    nonAuthorLabel.setVisible(false);

    VerticalLayout rightLay = getRightLayout();
    rightLay.setSpacing(false);
    rightLay.setMargin(false);

    imageScroller = new Panel();
    GridLayout gridL = new GridLayout();
    gridL.setColumns(2);
    gridL.setSpacing(true);
    gridL.setMargin(new MarginInfo(true));
    imageScroller.setContent(gridL);
    imageScroller.setStyleName(Reindeer.PANEL_LIGHT); // make a transparent scroller
    imageScroller.setWidth("100%");
    imageScroller.setHeight("99%");
    setUpIndexListener(imageScroller);

    rightLay.addComponent(imageScroller);
    fillWithImagesTL();
  }

  // All this does is put the index number in the top line
  private void setUpIndexListener(Panel p)
  {
    ((AbstractLayout)p.getContent()).addComponentAttachListener(new IndexListener()); 
  }
  
  private void fillWithImagesTL()
  {
    fillWithImages(HSess.get());
  }
  
  private void fillWithImages(Session sess)
  {
    ((AbstractLayout)imageScroller.getContent()).removeAllComponents();

    ActionPlan actionPlan = ActionPlan.get(apId, sess);
    List<Media> lis = actionPlan.getMedia();

    for (Media m : lis) {
      if (m.getType() == MediaType.IMAGE)
        addOneImage(m);
    }
  }

  class MPanelWrapper extends VerticalLayout
  {
    private static final long serialVersionUID = 1L;
    NativeButton killButt;
    MediaPanel ip;
    public MPanelWrapper()
    {
      setSizeUndefined();
    }
    public void setIndex(int i)
    {
      ip.setIndex(i);
    }
  }
  
  private void addOneImage(Media m)
  {
    MPanelWrapper wrap = new MPanelWrapper();
    wrap.setMargin(false);
    wrap.setSpacing(false);
    wrap.ip = new MediaPanel(m,apId,0, replaceLis);
    wrap.addComponent(wrap.ip);
    
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth(MediaPanel.WIDTH);
    Label lab;
    hl.addComponent(lab = new Label());
    lab.setWidth("3px");
    hl.addComponent(lab = new Label(getDisplayedName(m)));
    lab.addStyleName("m-font-size-11");
    hl.setExpandRatio(lab, 1.0f);
    hl.addComponent(wrap.killButt = new NativeButton(null));
    hl.addComponent(lab = new Label());
    lab.setWidth("3px");
    
    wrap.addComponent(hl);
    wrap.killButt.setCaption("delete");
    wrap.killButt.setStyleName(BaseTheme.BUTTON_LINK);
    wrap.killButt.addStyleName("borderless");
    wrap.killButt.addStyleName("m-actionplan-nothumbs-button");
    wrap.killButt.addClickListener(new ImageRemover(m));
    ((AbstractLayout)imageScroller.getContent()).addComponent(wrap);
    wrap.ip.initGui();      
  }

  @SuppressWarnings("serial")
  class ImageRemover implements ClickListener
  {
    Media m;
    public ImageRemover(Media m)
    {
      this.m = m;
    }
    
    @Override
    public void buttonClick(ClickEvent event)
    {
      if (m != null) {
        ConfirmDialog.show(UI.getCurrent(),"Confirm:", "Delete this image from the ActionPlan?", "Yes", "No", new ConfirmDialog.Listener()
        {
          @MmowgliCodeEntry
          @HibernateOpened
          @HibernateClosed
          public void onClose(ConfirmDialog dialog)
          {
            if (!dialog.isConfirmed()) {
              return;
            }
            else {
              HSess.init();
              User u;
              sendStartEditMessage((u=Mmowgli2UI.getGlobals().getUserTL()).getUserName()+" has deleted an image from the action plan."); 

              ActionPlan ap = ActionPlan.getTL(apId);
              List<Media> lis = ap.getMedia();
              Media.updateTL(m); // get into same session
              lis.remove(m);
              ActionPlan.updateTL(ap);
              Media.deleteTL(m); // remove from db
              GameEventLogger.logActionPlanUpdateTL(ap, "image deleted", u.getId()); //u.getUserName());

              fillWithImagesTL();
              doCaption();
              HSess.close();
            }
          }
        });
      }
    }
  }
    
  private MediaPanel findImagePanel(Button butt)
  {
    Component com = butt;
    while (!(com instanceof MediaPanel)) {
      com = com.getParent();
    }
    return (MediaPanel) com;
  }

  private int findMediaIndex(Button butt)
  {
    MediaPanel pan = findImagePanel(butt);
    Media m = pan.getMedia();
    return getMediaIndexTL(m);
  }

  private Media findMediaTL(Button butt)
  {
    int wh = findMediaIndex(butt);
    if (wh == -1)
      return null;
    ActionPlan ap = ActionPlan.getTL(apId);
    List<Media> lis = ap.getMedia();
    return lis.get(wh);
  }

  private int getMediaIndexTL(Media m)
  {
    ActionPlan ap = ActionPlan.getTL(apId);
    List<Media> lis = ap.getMedia();
    for (int i = 0; i < lis.size(); i++)
      if (lis.get(i).getId() == m.getId())
        return i;
    return -1;
  }

  private ActionPlan replaceMediaTL(Media oldM, Media newM)
  {
    int oldIdx = getMediaIndexTL(oldM);
    ActionPlan ap = ActionPlan.getTL(apId);
    List<Media> lis = ap.getMedia();
    lis.set(oldIdx, newM);
    return ap;
  }

  @SuppressWarnings("serial")
  class ImageReplacer implements ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      final Media oldM = findMediaTL(event.getButton());
      final MediaPanel iPan = findImagePanel(event.getButton());
      if (oldM != null) {
        /* if(addDialog == null) */{ // this was an attempt to persist the dialog, retaining file path, etc., but it was screwing with the media objects in some
                                     // way I don't have time to figure out
          addDialog = new AddImageDialog(apId);

          addDialog.setModal(true);
          addDialog.addListener(new CloseListener()
          {
            @Override
            @MmowgliCodeEntry
            @HibernateOpened
            @HibernateClosed
            public void windowClose(CloseEvent e)
            {
              if (addDialog.getParent() != null)
                UI.getCurrent().removeWindow(addDialog);

              Media med = addDialog.getMedia();
              if (med != null) {
                HSess.init();
               /* already saved Media.saveTL(med); */
                ActionPlan ap = replaceMediaTL(oldM, med);
                iPan.setMedia(med);
                ActionPlan.updateTL(ap);
                User u = Mmowgli2UI.getGlobals().getUserTL();
                GameEventLogger.logActionPlanUpdateTL(ap, "image replaced", u.getId());
                HSess.close();
              }
            }
          });
        }
        UI.getCurrent().addWindow(addDialog);
      }
      HSess.close();
    }
  }

  @SuppressWarnings("serial")
  class ImageAdder implements ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
/*
      if(true) {
      // Create a notification with default settings for a warning.
      Window.Notification notif = new Window.Notification(
              "Sorry!",
              "Adding images is temporarily disabled in the game.",
              Window.Notification.TYPE_WARNING_MESSAGE);

      notif.setPosition(Window.Notification.POSITION_CENTERED);

      event.getButton().getWindow().showNotification(notif);// Show it in the main window.
      return;
      }
*/

      addDialog = new AddImageDialog(apId);

      addDialog.setModal(true);
      addDialog.addListener(new CloseListener()
      {
        @Override
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void windowClose(CloseEvent e)
        {
          UI.getCurrent().removeWindow(addDialog);

          Media med = addDialog.getMedia();
          if (med != null) {
            HSess.init();
            addOneImage(med);
            ActionPlan ap = ActionPlan.getTL(apId);
            ap.getMedia().add(med);
            ActionPlan.updateTL(ap);
            User u = Mmowgli2UI.getGlobals().getUserTL();
            GameEventLogger.logActionPlanImageAddedTL(ap, u.getUserName(), med.getTitle());
            
            spawnImageSizeThread(med.getId());
            HSess.close();
          }
        }
      });

      UI.getCurrent().addWindow(addDialog);
      HSess.close();
    }
  }

  private void spawnImageSizeThread(final long mId)
  {
    Thread thr = new Thread(new Runnable()
    {

      @Override
      public void run()
      {
        Object key = HSess.checkInit();
        Media m = Media.getTL(mId);
        if(m.getSource() == Media.Source.WEB_FULL_URL) {
          String url = m.getUrl();     
          try {
            BufferedImage bi = ImageIO.read(new URL(url));
            int w = bi.getWidth();
            int h = bi.getHeight();
            m.setHeight((long)h);
            m.setWidth((long)w);
            Media.updateTL(m);
            MSysOut.println(MISC_LOGS,"Computed image size: "+url+" w:"+w+" h: "+h);
          }
          catch (IOException e) {
            MSysOut.println(ERROR_LOGS,"Error computing image size: "+e.getClass().getSimpleName()+": "+e.getLocalizedMessage());
            m.setHeight(null);
            m.setWidth(null);
          }
         HSess.checkClose(key);
        }         
      }      
    });
    
    thr.setDaemon(true); // won't hold up vm
    thr.setPriority(Thread.NORM_PRIORITY);
    thr.start();
  }
  
  private void doCaption()
  {
    // not implemented: put number of images into tab text
  }
 
  @Override
  public boolean mediaUpdatedOobTL(Serializable medId)
  {
    return mediaUpdatedOobTL((ComponentContainer)imageScroller.getContent(),medId);  //super
  }
  
  @Override
  public boolean actionPlanUpdatedOobTL(Serializable apId)
  {
    if(apId != this.apId)
      return false;

    // For media updates, such as caption, url changes, etc., the mediaUpdatedOob method gets hit
    // Here, we have to check for additions and subtractions
    Vector<Media> imagesInAp = new Vector<Media>(); // what the plan has
    Vector<Media> imagesInGui = new Vector<Media>(); // what the gui is showing

    ActionPlan ap = ActionPlan.getTL(apId);
    List<Media> mLis = ap.getMedia();
    
    for(Media m : mLis) {
      if(m.getType() == MediaType.IMAGE)
        imagesInAp.add(m);
    }
    
    GridLayout gl = (GridLayout)this.imageScroller.getContent();
    Iterator<Component> cItr = gl.iterator();
    while(cItr.hasNext()) {
      Component c = cItr.next();
      MPanelWrapper mpw = (MPanelWrapper)c;
      imagesInGui.add(mpw.ip.getMedia());
    }    
    int apNum = imagesInAp.size();
    int guiNum = imagesInGui.size();
    if(apNum == guiNum)
      return false;
    
    if(imagesInAp.size() > imagesInGui.size()) 
      addTheNewOne(ap, imagesInAp, imagesInGui);
    else if(imagesInAp.size() < imagesInGui.size())
      deleteTheOldOne_oobTL(ap, imagesInAp, imagesInGui);
    return true;
  }

  private void addTheNewOne(ActionPlan ap, Vector<Media>big, Vector<Media>little)
  {
    Iterator<Media> itr = big.iterator();
    while(itr.hasNext()) {
      Media mm = itr.next();
      if(foundIt(mm,little) != null)
        continue;
      else {
        addOneImage(mm);
        return;
      }
    }
  }
  
  private Media foundIt(Media m, Vector<Media>v)
  {
    Iterator<Media> itr = v.iterator();
    while(itr.hasNext()) {
      Media mm = itr.next();
      if(mm.getId() == m.getId()) {
        return mm;
      }
    }
    return null;
  }
  
  private void deleteTheOldOne_oobTL(ActionPlan ap, Vector<Media> little, Vector<Media> big)
  {
    fillWithImagesTL();
    doCaption();
  }

  @Override
  public void setICanEdit(boolean yn)
  {
    addImageButt.setEnabled(yn && !isReadOnly);
    nonAuthorLabel.setVisible(!yn);

    Iterator<Component> itr = ((AbstractLayout)imageScroller.getContent()).iterator();
    while (itr.hasNext()) {
      MPanelWrapper wrapper = (MPanelWrapper)itr.next();
      wrapper.killButt.setVisible(yn);
      wrapper.ip.setReadOnly(!yn);
    }
  }
  
  @SuppressWarnings("serial")
  public static class IndexListener implements ComponentContainer.ComponentAttachListener
  {
    @Override
    public void componentAttachedToContainer(ComponentAttachEvent event)
    {
      ComponentContainer.ComponentAttachEvent ev = (ComponentContainer.ComponentAttachEvent)event;
      GridLayout grid = (GridLayout)ev.getContainer();
      Object o = ev.getAttachedComponent();
      if(o instanceof MPanelWrapper) {
        MPanelWrapper wrap = (MPanelWrapper)o;
        Iterator<Component>itr = grid.iterator();
        int i=1;
        while(itr.hasNext()) {
          if(itr.next()==wrap) {
            wrap.setIndex(i);
            return;
          }
          i++;
        }
      }
      else if (o instanceof VMPanelWrapper) {
        VMPanelWrapper wrap = (VMPanelWrapper)o;
        Iterator<Component>itr = grid.iterator();
        int i=1;
        while(itr.hasNext()) {
          if(itr.next()==wrap) {
            wrap.setIndex(i);
            return;
          }
          i++;
        }        
      }
    }
  }
}
