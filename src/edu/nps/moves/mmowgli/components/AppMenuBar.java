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

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import static edu.nps.moves.mmowgli.MmowgliEvent.*;

import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.ui.*;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliController;
import edu.nps.moves.mmowgli.MmowgliEvent;
import edu.nps.moves.mmowgli.cache.MCacheManager;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.WantsGameUpdates;
import edu.nps.moves.mmowgli.modules.administration.AdvanceMoveDialog;
import edu.nps.moves.mmowgli.modules.administration.EntryPermissionsDialog;
import edu.nps.moves.mmowgli.modules.cards.ShowCardCacheCountsDialog;
import edu.nps.moves.mmowgli.modules.userprofile.InstallImageDialog;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 *
 * @version	$Id: AppMenuBar.java 3279 2014-01-15 23:26:15Z tdnorbra $
 * @copyright	Copyright (C) 2011
 */
public class AppMenuBar extends CustomComponent implements WantsGameUpdates
{
  private static final long serialVersionUID = 3017895441955686990L;

  private MmowgliController controller;
  private MenuBar menubar = new MenuBar();
  private MenuBar.MenuItem gameMasterMI;
  private MenuBar.MenuItem adminMI;
  private MenuBar.MenuItem designerMI;

  private MenuBar.MenuItem gameRoMI;
  private MenuBar.MenuItem cardsRoMI;
  private MenuBar.MenuItem topCardsRoMI;
  private MenuBar.MenuItem emailConfirmationMI;

  private MenuBar.MenuItem cardDBTestStartMI;
  private MenuBar.MenuItem cardDBTestEndMI;
  private MenuBar.MenuItem userDBTestStartMI;
  private MenuBar.MenuItem userDBTestEndMI;

  private MenuBar.MenuItem maxUsersMI;

  public MenuBar.MenuItem getCardDBTestStartMI()
  {
    return cardDBTestStartMI;
  }
  public MenuBar.MenuItem getCardDBTestEndMI()
  {
    return cardDBTestEndMI;
  }
  public MenuBar.MenuItem getUserDBTestStartMI()
  {
    return userDBTestStartMI;
  }
  public MenuBar.MenuItem getUserDBTestEndMI()
  {
    return userDBTestEndMI;
  }

  /**
   * Don't have an app object until this happens
   */
  @Override
  public void attach()
  {
    super.attach();
    controller = Mmowgli2UI.getGlobals().getController();
  }
  public AppMenuBar()
  {
    this(false,false);
  }
  public AppMenuBar(boolean doGameMaster)
  {
    this(doGameMaster,false);
  }
  public AppMenuBar(boolean doGameMaster, boolean doAdmin)
  {
    this(doGameMaster, doAdmin, false);
  }
  @HibernateSessionThreadLocalConstructor
  public AppMenuBar(boolean doGameMaster, boolean doAdmin, boolean doDesigner)
  {
    menubar.setHtmlContentAllowed(true); // test for font icons
    
    HorizontalLayout hLayout = new HorizontalLayout();
    // Save reference to individual items so we can add sub-menu items to
    // them
    if(doAdmin)
      adminMI = buildAdminMenu();
    if(doDesigner)
      designerMI = buildDesignerMenu();
    if(doGameMaster)
      gameMasterMI = buildGameMasterMenu();
    
    menubar.setHtmlContentAllowed(true);
    hLayout.addComponent(menubar);

    setCompositionRoot(hLayout);
    setWidth("375px");   // so it doesn't cover fouo butt...adjust if more menus are added
 // doesn't size properly with fonticons and htmlcontent
  }

  /*
   * If you add a new event here, also put it into isGameMasterMenuEvent() below.
   */
  private MenuBar.MenuItem buildGameMasterMenu()
  {
    MenuBar.MenuItem ret = menubar.addItem("Game Master", null);
    ret.setIcon(VaadinIcons.GAVEL);
    ret.addItem("Monitor Game Master Events Log", new MCommand(MENUGAMEMASTERMONITOREVENTS)).setIcon(VaadinIcons.LINES);
    ret.addItem("Post comment to Game Master Event Log", new MCommand(MENUGAMEMASTERPOSTCOMMENT)).setIcon(VaadinIcons.COMMENT_O);
    ret.addSeparator();
    
    ret.addItem("Broadcast message to game masters", new MCommand(MENUGAMEMASTERBROADCASTTOGMS)).setIcon(VaadinIcons.MEGAFONE);
    ret.addItem("Broadcast message to all players", new MCommand(MENUGAMEMASTERBROADCAST)).setIcon(VaadinIcons.MEGAFONE);
    
    ret.addItem("Edit blog headline", new MCommand(MENUGAMEMASTERBLOGHEADLINE)).setIcon(VaadinIcons.EXCLAMATION_CIRCLE);
    ret.addSeparator();

    if (Game.getTL().isActionPlansEnabled()) {
        ret.addItem("Create Action Plan", new MCommand(MENUGAMEMASTERCREATEACTIONPLAN)).setIcon(VaadinIcons.FILE_TEXT_O);
        ret.addItem("Invite additional players to be Action Plan authors", new MCommand(MENUGAMEMASTERINVITEAUTHORSCLICK)).setIcon(VaadinIcons.USERS);
    }

    ret.addSeparator();
    ret.addItem("Show active players",new MCommand(MENUGAMEMASTERACTIVEPLAYERREPORTCLICK)).setIcon(VaadinIcons.USERS);
    //ret.addItem("Show active player count overall", new MCommand(MENUGAMEMASTERACTIVECOUNTCLICK)).setIcon(VaadinIcons.USERS);
    //ret.addItem("Show active player count by server", new MCommand(MENUGAMEMASTERACTIVECOUNTBYSERVERCLICK)).setIcon(VaadinIcons.USERS);
   // this is non functional ret.addItem("Show user polling data for this server",  new MCommand(MENUGAMEMASTERUSERPOLLINGCLICK)).setIcon(VaadinIcons.USERS);
    ret.addItem("Show registered user counts", new MCommand(MENUGAMEMASTERTOTALREGISTEREDUSERS)).setIcon(VaadinIcons.USERS);
    ret.addItem("Show card count", new MCommand(MENUGAMEMASTERCARDCOUNTCLICK)).setIcon(VaadinIcons.HASH);
    ret.addItem("View game login permissions buttons", viewGamePermissionsClicked).setIcon(VaadinIcons.SIGN_IN);

    ret.addSeparator();

    if (Game.getTL().isActionPlansEnabled())
        ret.addItem("Show displayed Action Plan as html", new MCommand(MENUGAMEMASTER_EXPORT_SELECTED_ACTIONPLAN)).setIcon(VaadinIcons.FILE_TEXT_O);
    
    ret.addItem("Show displayed Idea Card tree as html", new MCommand(MENUGAMEMASTER_EXPORT_SELECTED_CARD)).setIcon(VaadinIcons.LIGHTBULB);
    ret.addItem("Open game Reports Index page", new MCommand(MENUGAMEMASTEROPENREPORTSPAGE)).setIcon(VaadinIcons.FILE_TEXT_O);
    ret.addSeparator();
    ret.addItem("View (read-only) game designer values", new MCommand(MENUGAMEADMIN_BUILDGAMECLICK_READONLY)).setIcon(VaadinIcons.EYE);
    ret.addSeparator();
    ret.addItem("About this Mmowgli", new MCommand(MENUGAMEMASTERABOUTMMOWGLI)).setIcon(VaadinIcons.INFO_CIRCLE);
    
    return ret;
  }

  /*
   * If you add a new event here, also put it into isAdminMenuEvent() below.
   */
  private MenuBar.MenuItem buildDesignerMenu()
  {
    MenuBar.MenuItem ret = menubar.addItem("<span style='width:100px'>Game Designer</span>",null);
    ret.setIcon(VaadinIcons.PENCIL);
    
    ret.addItem("Customize game", new MCommand(MENUGAMEADMIN_BUILDGAMECLICK)).setIcon(VaadinIcons.PENCIL);
    ret.addItem("Publish updated game design report ", new MCommand(MENUGAMEADMIN_EXPORTGAMESETTINGS)).setIcon(VaadinIcons.FILE_TEXT_O);
    ret.addItem("Show signup email addresses and feedback", new MCommand(MENUGAMEADMINDUMPSIGNUPS)).setIcon(VaadinIcons.CHECK);
    return ret;
  }

  /*
   * If you add a new event here, also put it into isGameMasterMenuEvent() below.
   */
  private MenuBar.MenuItem buildAdminMenu()
  {
    Game game = Game.getTL();

    MenuBar.MenuItem ret = menubar.addItem("Game Administrator", null); ret.setIcon(VaadinIcons.COG);
    ret.addItem("Player administration", new MCommand(MENUGAMEMASTERUSERADMIN)).setIcon(VaadinIcons.USERS);
    
    maxUsersMI = ret.addItem("null text", new MCommand(MENUGAMEADMINLOGINLIMIT)); maxUsersMI.setIcon(VaadinIcons.BAN);
    setMaxUsersMIText(game);

    ret.addItem("<a href='"+CLUSTERMONITORURL+"' target='"+CLUSTERMONITORTARGETWINDOWNAME+"'>Open cluster monitor</a>",new NullMCommand()).setIcon(VaadinIcons.WRENCH);

    ret.addSeparator();
    ret.addItem("Dump player emails in plain text", new MCommand(MENUGAMEADMINDUMPEMAILS)).setIcon(VaadinIcons.USERS);
    ret.addItem("Dump game master emails in plain text", new MCommand(MENUGAMEADMINDUMPGAMEMASTERS)).setIcon(VaadinIcons.USERS);
    ret.addSeparator();

    topCardsRoMI = ret.addItem("Top idea cards read-only", topCardsReadOnlyChecked); topCardsRoMI.setIcon(VaadinIcons.LOCK);
    topCardsRoMI.setCheckable(true);
    topCardsRoMI.setChecked(game.isTopCardsReadonly());

    cardsRoMI=ret.addItem("Card-play read-only", cardsReadOnlyChecked); cardsRoMI.setIcon(VaadinIcons.LOCK);
    cardsRoMI.setCheckable(true);
    cardsRoMI.setChecked(game.isCardsReadonly());

    gameRoMI = ret.addItem("Entire game read-only", gameReadOnlyChecked); gameRoMI.setIcon(VaadinIcons.LOCK);
    gameRoMI.setCheckable(true);
    gameRoMI.setChecked(game.isReadonly());
    ret.addSeparator();

    ret.addItem("Game login button displays and permissions", gamePermissionsClicked).setIcon(VaadinIcons.SIGN_IN);
    
    emailConfirmationMI = ret.addItem("Require new signup email confirmation",emailConfirmationChecked); emailConfirmationMI.setIcon(VaadinIcons.USER_CHECK);
    emailConfirmationMI.setCheckable(true);
    emailConfirmationMI.setChecked(game.isEmailConfirmation());
    ret.addSeparator();

    ret.addItem("Manage signups", new MCommand(MENUGAMEADMINMANAGESIGNUPS)).setIcon(VaadinIcons.USERS);
    ret.addItem("Add to VIP list", new MCommand(MENUGAMEMASTERADDTOVIPLIST)).setIcon(VaadinIcons.USERS);
    ret.addItem("View and/or delete from VIP list", new MCommand(MENUGAMEMASTERVIEWVIPLIST)).setIcon(VaadinIcons.USERS);

    ret.addSeparator();

    String gameReports = Game.getTL().isActionPlansEnabled() ? "Publish Action Plan, Idea Card and Game Design reports now" : "Publish Idea Card and Game Design reports now";
    ret.addItem(gameReports, new MCommand(MENUGAMEADMINPUBLISHREPORTS)).setIcon(VaadinIcons.FILE_TEXT_O);

    if (Game.getTL().isActionPlansEnabled()) {
        ret.addItem("Create and show Action Plans report in browser", new MCommand(MENUGAMEADMINEXPORTACTIONPLANS)).setIcon(VaadinIcons.FILE_TEXT_O);
    }

    ret.addItem("Create and show Cards report in browser", new MCommand(MENUGAMEADMINEXPORTCARDS)).setIcon(VaadinIcons.FILE_TEXT_O);

    ret.addSeparator();
    ret.addItem("Advance game round and/or phase", advanceRoundClicked).setIcon(VaadinIcons.ARROW_RIGHT);
    ret.addItem("Kill all player sessions", new MCommand(MENUGAMEADMINKILLALLSESSIONS)).setIcon(VaadinIcons.STOP_COG);
    ret.addSeparator();
    ret.addItem("(Dev only) Add Image to database", addImageClicked).setIcon(VaadinIcons.TOOLS);
    ret.addItem("(Dev only) Display card cache counts", showCardCacheCounts).setIcon(VaadinIcons.TOOLS);
    ret.addItem("(Dev only) Rebuild card caches", rebuildCardCaches).setIcon(VaadinIcons.TOOLS);
    ret.addSeparator();
    ret.addItem("Begin resource load-time logging", new ResourceLogging(true)).setIcon(VaadinIcons.TOOLS);
    ret.addItem("Stop resource load-time logging", new ResourceLogging(false)).setIcon(VaadinIcons.TOOLS);

    return ret;
  }

  @Override
  public boolean gameUpdatedExternallyTL(Object nullObj)
  {
    boolean ret = false;
    
    Game game = Game.getTL();
    if(cardsRoMI != null) {
      boolean oldck = cardsRoMI.isChecked();
      boolean newck = game.isCardsReadonly();
      if(oldck != newck) {
        cardsRoMI.setChecked(newck);
        ret = true;
      }
    }
    if(gameRoMI != null) {
      boolean oldck = gameRoMI.isChecked();
      boolean newck = game.isReadonly();
      if(oldck != newck) {
        gameRoMI.setChecked(newck);
        ret = true;
      }
    }
    if(topCardsRoMI != null) {
      boolean oldck = topCardsRoMI.isChecked();
      boolean newck = game.isTopCardsReadonly();
      if(oldck != newck) {
        topCardsRoMI.setChecked(newck);
        ret = true;
      }
    }
    if(maxUsersMI != null) {
      int currentMxUsers = (Integer)((MCommand)maxUsersMI.getCommand()).getData();
      if(currentMxUsers != game.getMaxUsersOnline()) {
        setMaxUsersMIText(game);
        ret = true;
      }
    }
    return ret;
  }

  private void setMaxUsersMIText(Game g)
  {
    Integer num = g.getMaxUsersOnline();
    ((MCommand)maxUsersMI.getCommand()).setData(num);

    maxUsersMI.setText("Set login limit ("+num+")");
  }

  @SuppressWarnings("serial")
  private Command emailConfirmationChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      if(emailConfirmationMI.isChecked())
        controller.menuClick(MENUGAMEADMIN_START_EMAILCONFIRMATION, menubar);
      else
        controller.menuClick(MENUGAMEADMIN_END_EMAILCONFIRMATION, menubar);
    }
  };

  @SuppressWarnings("serial")
  private Command cardsReadOnlyChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      if(selectedItem.isChecked())
        controller.menuClick(MENUGAMEADMINSETCARDSREADONLY,menubar);
      else
        controller.menuClick(MENUGAMEADMINSETCARDSREADWRITE,menubar);
    }
  };

  @SuppressWarnings("serial")
  private Command topCardsReadOnlyChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      if(selectedItem.isChecked())
        controller.menuClick(MENUGAMEADMINSETTOPCARDSREADONLY,menubar);
      else
        controller.menuClick(MENUGAMEADMINSETTOPCARDSREADWRITE,menubar);
    }
  };

  @SuppressWarnings("serial")
  private Command addImageClicked = new Command()
  {
    public void menuSelected(MenuItem selectedItem)
    {
      InstallImageDialog.show("Image names must be unique in the database.",null,false,null);
    }
  };
  
  @SuppressWarnings("serial")
  private Command showCardCacheCounts = new Command()
  {
	public void menuSelected(MenuItem selectedItem)
	{
	  ShowCardCacheCountsDialog.show();
	}
  };
  
  @SuppressWarnings("serial")
  private Command rebuildCardCaches = new Command() {
    public void menuSelected(MenuItem selectedItem) {
      ConfirmDialog.show(UI.getCurrent(), "Warning!", "Heavyweight action -- are you really sure?", "Yes", "No",
        new ConfirmDialog.Listener() {
          public void onClose(ConfirmDialog dialog) {
            if (dialog.isConfirmed()) {
              HSess.init();
              MCacheManager.instance()._rebuildCards(HSess.get());
              HSess.close();
            }
          }
      });
    }
  };

  @SuppressWarnings("serial")
  private Command advanceRoundClicked = new Command()
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void menuSelected(MenuItem selectedItem)
    {
      HSess.init();
      Window win=new AdvanceMoveDialog();
      UI.getCurrent().addWindow(win);
      win.center();
      HSess.close();
    }
  };

  @SuppressWarnings("serial")
  private Command gamePermissionsClicked = new Command()
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void menuSelected(MenuItem selectedItem)
    {
      HSess.init();
      Window win=new EntryPermissionsDialog();
      UI.getCurrent().addWindow(win);
      win.center();
      HSess.close();
    }
  };
  
  @SuppressWarnings("serial")
  private class ResourceLogging implements Command
  {
    boolean onOff;
    ResourceLogging(boolean onOff)
    {
      this.onOff = onOff;
    }
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      if(onOff)
        JavaScript.getCurrent().execute(JS_SHIP_STATS_ON); 
      else
        JavaScript.getCurrent().execute(JS_SHIP_STATS_OFF); 
    }
  }

  @SuppressWarnings("serial")
  private Command viewGamePermissionsClicked = new Command()
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void menuSelected(MenuItem selectedItem)
    {
      HSess.init();
      Window win=new EntryPermissionsDialog(true); // mark as read-only
      UI.getCurrent().addWindow(win);
      win.center();
      HSess.close();
    }
  };

  @SuppressWarnings("serial")
  private Command gameReadOnlyChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      if(selectedItem.isChecked())
        controller.menuClick(MENUGAMEADMINSETGAMEREADONLY,menubar);
      else
        controller.menuClick(MENUGAMEADMINSETGAMEREADWRITE,menubar);
    }
  };

  public boolean showDesignerMenu(boolean yn)
  {
    if(yn) {
      if(designerMI == null)
        designerMI = buildDesignerMenu();
    }
    else {
      if(designerMI != null) {
        menubar.removeItem(designerMI);
        designerMI = null;
      }
    }
    return (gameMasterMI == null) && (adminMI == null) && (designerMI == null);

  }
  /**
   *
   * @param yn
   * @return true if menu is now empty of the admin and gamemaster menus
   */
  public boolean showGameMasterMenu(boolean yn)
  {
    if(yn) {
      if(gameMasterMI == null)
        gameMasterMI = buildGameMasterMenu();
    }
    else {
      if(gameMasterMI != null) {
        menubar.removeItem(gameMasterMI);
        gameMasterMI = null;
      }
    }
    return (gameMasterMI == null) && (adminMI == null) && (designerMI == null);
  }

  /**
   *
   * @param yn
   * @return true if menu is now empty of the admin and gamemaster menus
   */
  public boolean showAdministratorMenu(boolean yn)
  {
    if(yn) {
      if(adminMI == null)
        adminMI = buildAdminMenu();
    }
    else {
      if(adminMI != null) {
        menubar.removeItem(adminMI);
        adminMI = null;
      }
    }
    return (gameMasterMI == null) && (adminMI == null) && (designerMI == null);
  }

  class MCommand implements MenuBar.Command
  {
    private static final long serialVersionUID = -2820399693399561481L;

    private MmowgliEvent mEv;
    private Object data;

    public MCommand(MmowgliEvent mEv)
    {
      this(mEv, null);
    }
    public MCommand(MmowgliEvent mEv, Object data)
    {
      this.mEv = mEv;
      this.data = data;
    }
    public Object getData()
    {
      return data;
    }
    public void setData(Object data)
    {
      this.data = data;
    }
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      controller.menuClick(mEv,menubar);
    }
  }
  class NullMCommand implements MenuBar.Command
  {
    private static final long serialVersionUID = 1L;
    @Override
    public void menuSelected(MenuItem selectedItem)
    { }
  }
}
