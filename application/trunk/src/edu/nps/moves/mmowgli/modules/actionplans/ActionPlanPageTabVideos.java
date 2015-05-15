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

import static edu.nps.moves.mmowgli.MmowgliConstants.PORTALTARGETWINDOWNAME;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.hibernate.Session;

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
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPageTabImages.IndexListener;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.BrowserWindowOpener;

/**
 * ActionPlanPageTabImages.java Created on Feb 8, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanPageTabVideos extends ActionPlanPageTabPanel
{
  private static final long serialVersionUID = 6134419136079278086L;

  private NativeButton addVideoButt;
  private Panel rightScroller;
  private ClickListener replaceLis;
  private AddVideoDialog addDialog;
  private Label nonAuthorLabel;
  
  @HibernateSessionThreadLocalConstructor
  public ActionPlanPageTabVideos(Object apId, boolean isMockup, boolean isReadOnly)
  {
    super(apId, isMockup, isReadOnly);
    addVideoButt = new NativeButton();
    replaceLis = new VideoReplacer();
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

    Label missionLab = new Label("Authors, add some videos!");
    flowLay.addComponent(missionLab);
    flowLay.setComponentAlignment(missionLab, Alignment.TOP_LEFT);
    missionLab.addStyleName("m-actionplan-mission-title-text");

    ActionPlan ap = ActionPlan.getTL(apId);

    Label missionContentLab;
    if(!isMockup)
      missionContentLab = new HtmlLabel(ap.getVideosInstructions());
    else {
      Game g = Game.getTL();
      missionContentLab = new HtmlLabel(g.getDefaultActionPlanVideosText());
    }
    flowLay.addComponent(missionContentLab);
    flowLay.setComponentAlignment(missionContentLab, Alignment.TOP_LEFT);
    flowLay.addStyleName("m-actionplan-mission-content-text");

    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    flowLay.addComponent(addVideoButt);
    addVideoButt.addStyleName("m-actionplan-addimage-butt");
    addVideoButt.addStyleName("borderless");
    addVideoButt.setIcon(globs.getMediaLocator().getActionPlanAddVideoButt());
    addVideoButt.addClickListener(new VideoAdder());
    addVideoButt.setEnabled(!isReadOnly);
    
    flowLay.addComponent(nonAuthorLabel = new Label("Authors may add videos when editing the plan."));
    nonAuthorLabel.setVisible(false);
    
    VerticalLayout rightLay = getRightLayout();
    rightLay.setSpacing(false);
    rightLay.setMargin(false);

    rightScroller = new Panel();
    GridLayout gridL = new GridLayout();
    gridL.setColumns(2);
    gridL.setSpacing(true);
    gridL.setMargin(new MarginInfo(true));
    rightScroller.setContent(gridL);
    rightScroller.setStyleName(Reindeer.PANEL_LIGHT); // make a transparent scroller
    rightScroller.setWidth("100%");
    rightScroller.setHeight("99%");
    setUpIndexListener(rightScroller);
    
    rightLay.addComponent(rightScroller);;
    fillWithVideosTL();
  }
  
  // All this does is put the index number in the top line
  private void setUpIndexListener(Panel p)
  {
    ((AbstractLayout)p.getContent()).addComponentAttachListener(new IndexListener());
  }
  
  private void fillWithVideosTL()
  {
    fillWithVideos(HSess.get());
  }
  private void fillWithVideos(Session sess)
  {
    ((AbstractLayout)rightScroller.getContent()).removeAllComponents();
    ActionPlan actionPlan = ActionPlan.get(apId, sess);
    List<Media> lis = actionPlan.getMedia();
    for (Media m : lis) {
      if (m.getType() == MediaType.VIDEO || m.getType() == MediaType.YOUTUBE)
        addOneVideo(m);
    }
  }
  
  class VMPanelWrapper extends VerticalLayout
  {
    private static final long serialVersionUID = 1L;
    
    NativeButton killButt;
    MediaPanel ip;
    
    public void setIndex(int i)
    {
      ip.setIndex(i);
    }
  }

  private void addOneVideo(Media m)
  {
    VMPanelWrapper vl = new VMPanelWrapper();
    vl.setMargin(false);
    vl.setSpacing(false);
    vl.ip = new MediaPanel(m,apId,0, replaceLis);
    vl.addComponent(vl.ip);
    
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth(MediaPanel.WIDTH);
    Label lab;
    hl.addComponent(lab = new Label());
    lab.setWidth("3px");
    
    if(m.getType() != MediaType.YOUTUBE) {
      hl.addComponent(lab = new Label( getDisplayedName(m)));  // label
      lab.addStyleName("m-font-size-11");
      hl.setExpandRatio(lab, 1.0f);
    }
    else {
      NativeButton linkButt;
      hl.addComponent(linkButt = new NativeButton(null));  // link
      linkButt.setCaption(getDisplayedName(m));
      linkButt.setStyleName(BaseTheme.BUTTON_LINK);
      linkButt.addStyleName("borderless");
      linkButt.addStyleName("m-actionplan-nothumbs-button");
      linkButt.addClickListener(new LinkVisitor(m));
      
      hl.addComponent(lab = new Label());
      lab.setWidth("1px");
      hl.setExpandRatio(lab,1.0f);
   }
    
    hl.addComponent(vl.killButt = new NativeButton(null));
    vl.killButt.setCaption("delete");
    vl.killButt.setStyleName(BaseTheme.BUTTON_LINK);
    vl.killButt.addStyleName("borderless");
    vl.killButt.addStyleName("m-actionplan-nothumbs-button");
    vl.killButt.addClickListener(new VideoRemover(m));
    
    hl.addComponent(lab = new Label());
    lab.setWidth("3px");
    
    vl.addComponent(hl);

    ((AbstractLayout)rightScroller.getContent()).addComponent(vl);
    vl.ip.initGui();    
  }
  

  private Media findMediaTL(Button butt)
  {
    int wh = findMediaIndexTL(butt);
    if (wh == -1)
      return null;
    ActionPlan ap = ActionPlan.getTL(apId);
    List<Media> lis = ap.getMedia();
    return lis.get(wh);
  }

  private int findMediaIndexTL(Button butt)
  {
    MediaPanel pan = findVideoPanel(butt);
    Media m = pan.getMedia();
    return getMediaIndexTL(m);
  }

  private MediaPanel findVideoPanel(Button butt)
  {
    Component com = butt;
    while (!(com instanceof MediaPanel)) {
      com = com.getParent();
    }
    return (MediaPanel) com;
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
  class VideoAdder implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      hideExistingVideos(); // if ie
      addDialog = new AddVideoDialog();

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
          showExistingVideos();
          Media med = addDialog.getMedia();
          if (med != null) {
            HSess.init();
            Media.saveTL(med);
            addOneVideo(med);
            ActionPlan ap = ActionPlan.getTL(apId);
            ap.getMedia().add(med);
            ActionPlan.updateTL(ap);
            User u = Mmowgli2UI.getGlobals().getUserTL();
            GameEventLogger.logActionPlanVideoAddedTL(ap, u.getUserName(), med.getTitle());
            HSess.close();
          }
        }
      });
      UI.getCurrent().addWindow(addDialog);
      addDialog.setPositionX(0);
      addDialog.setPositionY(50); // miss videos
      HSess.close();
    }
  }

  private void toggleExistingVideos(boolean show)
  {
    if(Mmowgli2UI.getGlobals().isIE()) {
      Iterator<Component> itr = ((AbstractLayout)rightScroller.getContent()).iterator();
      while(itr.hasNext()) {        
        VMPanelWrapper wrap = (VMPanelWrapper)itr.next();
        if(show)
          wrap.ip.enableVideo();
        else
          wrap.ip.disableVideo();
      }
    }  
  }
  
  public void hideExistingVideos()
  {
    toggleExistingVideos(false);   
  }
  
  public void showExistingVideos()
  {
    toggleExistingVideos(true);
  }
  
  @SuppressWarnings("serial")
  class VideoRemover implements ClickListener
  {
    Media m;
    VideoRemover(Media m)
    {
      this.m = m;
    }
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      if (m != null) {
        HSess.init();
        
        ActionPlan ap = ActionPlan.getTL(apId);
        List<Media> lis = ap.getMedia();
        Media.updateTL(m);  // get into same session
        lis.remove(m);
        ActionPlan.updateTL(ap);
        User u = Mmowgli2UI.getGlobals().getUserTL();
        GameEventLogger.logActionPlanUpdateTL(ap, "video removed", u.getId());

        fillWithVideosTL();
        HSess.close();
      }
    }
  }

  @SuppressWarnings("serial")
  class VideoReplacer implements ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      final Media oldM = findMediaTL(event.getButton());
      final MediaPanel vPan = findVideoPanel(event.getButton());
      if (oldM != null) {
        //if (addDialog == null) {
          hideExistingVideos(); //if ie
          addDialog = new AddVideoDialog();

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
              showExistingVideos();
              Media med = addDialog.getMedia();
              if (med != null) {
                HSess.init();
                Media.saveTL(med);
                ActionPlan ap = replaceMediaTL(oldM, med);
                vPan.setMedia(med);
                ActionPlan.updateTL(ap);
                User u = Mmowgli2UI.getGlobals().getUserTL();
                GameEventLogger.logActionPlanUpdateTL(ap, "video replaced", u.getId());
                HSess.close();              }
            }
          });
        //}
        UI.getCurrent().addWindow(addDialog);
      }
      HSess.close();
    }
  }
  @SuppressWarnings("serial")
  class LinkVisitor implements ClickListener
  {
    Media m;
    LinkVisitor(Media m)
    {
      this.m = m;
    }
    @Override
    public void buttonClick(ClickEvent event)
    {
      String url = "https://www.youtube.com/watch?v="+m.getUrl();
      BrowserWindowOpener.open(url, PORTALTARGETWINDOWNAME);
    }   
  }
  
  @Override
  public boolean actionPlanUpdatedOobTL(Serializable apId)
  {
    if(apId != this.apId)
      return false;

    // For media updates, such as caption, url changes, etc., the mediaUpdatedOob method gets hit
    // Here, we have to check for additions and subtractions
    Vector<Media> videosInAp = new Vector<Media>(); // what the plan has
    Vector<Media> videosInGui = new Vector<Media>(); // what the gui is showing

    ActionPlan ap = ActionPlan.getTL(apId);
    List<Media> mLis = ap.getMedia();
    
    for(Media m : mLis) {
      if(m.getType() == MediaType.YOUTUBE || m.getType() == MediaType.VIDEO)
        videosInAp.add(m);
    }
    
    Iterator<Component> cItr = ((AbstractLayout)rightScroller.getContent()).iterator();
    while(cItr.hasNext()) {
      Component c = cItr.next();
      VMPanelWrapper mpw = (VMPanelWrapper)c;
      videosInGui.add(mpw.ip.getMedia());
    }    
    int apNum = videosInAp.size();
    int guiNum = videosInGui.size();
    if(apNum == guiNum)
      return false;
    
    if(videosInAp.size() > videosInGui.size()) 
      addTheNewOne(ap, videosInAp, videosInGui);
    else if(videosInAp.size() < videosInGui.size())
      deleteTheOldOneOobTL(ap, videosInAp, videosInGui);
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
        addOneVideo(mm);
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

  private void deleteTheOldOneOobTL(ActionPlan ap, Vector<Media> little, Vector<Media> big)
  {
    fillWithVideosTL();
  }

  @Override
  public void setICanEdit(boolean yn)
  {
    addVideoButt.setEnabled(yn && !isReadOnly);
    nonAuthorLabel.setVisible(!yn);
    
    Iterator<Component> itr = ((AbstractLayout)rightScroller.getContent()).iterator();
    while(itr.hasNext()) {
      VMPanelWrapper wrap = (VMPanelWrapper)itr.next();
      wrap.ip.setReadOnly(!yn);
      wrap.killButt.setVisible(yn);
    }    
  }

  public boolean mediaUpdatedOobTL(Serializable medId)
  {
    // calls superclass
    return mediaUpdatedOobTL((ComponentContainer)rightScroller.getContent(),medId);
  }
}
