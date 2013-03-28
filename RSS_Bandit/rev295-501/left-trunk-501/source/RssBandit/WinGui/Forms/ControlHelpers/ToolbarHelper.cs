using System;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.IO;
using System.Windows.Forms;
using Infragistics.Win.UltraWinToolbars;
using NewsComponents.Utils;
using RssBandit.Resources;
using RssBandit.Utility.Keyboard;
using RssBandit.WebSearch;
using RssBandit.WinGui.Interfaces;
using RssBandit.WinGui.Tools;
using RssBandit.WinGui.Utility;
using Logger = RssBandit.Common.Logging;
namespace RssBandit.WinGui.Forms.ControlHelpers
{
 internal class ToolbarHelper
 {
  private readonly UltraToolbarsManager manager;
  private WinGuiMain main;
  private RssBanditApplication owner;
  private ShortcutHandler shortcutHandler;
  private static readonly log4net.ILog _log = Logger.Log.GetLogger(typeof(RssBanditApplication));
  public ToolbarHelper(UltraToolbarsManager manager) {
   this.manager = manager;
  }
  public void CreateToolbars(WinGuiMain main, RssBanditApplication owner, ShortcutHandler shortcutHandler) {
   this.main = main;
   this.owner = owner;
   this.shortcutHandler = shortcutHandler;
   UltraToolbar webBrowser = new UltraToolbar(Resource.Toolbar.WebTools);
   UltraToolbar mainMenu = new UltraToolbar(Resource.Toolbar.MenuBar);
   UltraToolbar mainTools = new UltraToolbar(Resource.Toolbar.MainTools);
   UltraToolbar searchTools = new UltraToolbar(Resource.Toolbar.SearchTools);
   webBrowser.DockedColumn = 0;
   webBrowser.DockedRow = 2;
   webBrowser.FloatingSize = new System.Drawing.Size(100, 20);
   webBrowser.Text = SR.MainForm_MainWebToolbarCaption;
   mainMenu.DockedColumn = 0;
   mainMenu.DockedRow = 0;
   mainMenu.IsMainMenuBar = true;
   mainMenu.Text = SR.MainForm_MainMenuToolbarCaption;
   mainTools.DockedColumn = 0;
   mainTools.DockedRow = 1;
   mainTools.Text = SR.MainForm_MainAppToolbarCaption;
   searchTools.DockedColumn = 1;
   searchTools.DockedRow = 2;
   searchTools.Text = SR.MainForm_MainSearchToolbarCaption;
   this.manager.Toolbars.AddRange(
    new UltraToolbar[] {webBrowser, mainMenu, mainTools, searchTools});
   InitMenuBar();
   CreateMainToolbar(this.manager.Toolbars[Resource.Toolbar.MainTools]);
   CreateBrowserToolbar(this.manager.Toolbars[Resource.Toolbar.WebTools]);
   CreateSearchToolbar(this.manager.Toolbars[Resource.Toolbar.SearchTools]);
   this.main = null;
   this.owner = null;
   this.shortcutHandler = null;
  }
  public void CreateToolbars(NewsgroupsConfiguration main) {
   UltraToolbar mainTools = new UltraToolbar(Resource.Toolbar.MainTools);
   mainTools.DockedColumn = 0;
   mainTools.DockedRow = 0;
   mainTools.Text = SR.NewsGroupConfiguration_MainToolbarCaption;
   this.manager.Toolbars.AddRange(
    new UltraToolbar[] { mainTools });
   Infragistics.Win.Appearance a = null;
   ButtonTool newIdentity = new ButtonTool("toolNewIndentity");
   newIdentity.SharedProps.Caption = SR.NewsGroupConfiguration_NewIdentityToolCaption;
   newIdentity.SharedProps.StatusText = newIdentity.SharedProps.ToolTipText = SR.NewsGroupConfiguration_NewIdentityToolDesc;
   a = new Infragistics.Win.Appearance();
   a.Image = Properties.Resources.add_user_16;
   newIdentity.SharedProps.AppearancesSmall.Appearance = a;
   newIdentity.SharedProps.DisplayStyle = ToolDisplayStyle.ImageAndText;
   ButtonTool newServer = new ButtonTool("toolNewNntpServer");
   newServer.SharedProps.Caption = SR.NewsGroupConfiguration_NewNewsServerToolCaption;
   newServer.SharedProps.StatusText = newServer.SharedProps.ToolTipText = SR.NewsGroupConfiguration_NewNewsServerToolDesc;
   a = new Infragistics.Win.Appearance();
   a.Image = Properties.Resources.add_newsserver_16;
   newServer.SharedProps.AppearancesSmall.Appearance = a;
   newServer.SharedProps.DisplayStyle = ToolDisplayStyle.ImageAndText;
   ButtonTool deleteItem = new ButtonTool("toolDelete");
   deleteItem.SharedProps.Caption = SR.NewsGroupConfiguration_DeleteToolCaption;
   deleteItem.SharedProps.StatusText = deleteItem.SharedProps.ToolTipText = SR.NewsGroupConfiguration_DeleteToolDesc;
   a = new Infragistics.Win.Appearance();
   a.Image = Properties.Resources.delete_16;
   deleteItem.SharedProps.AppearancesSmall.Appearance = a;
   deleteItem.SharedProps.DisplayStyle = ToolDisplayStyle.ImageAndText;
   this.manager.Tools.AddRange(new ToolBase[] {newIdentity, newServer, deleteItem});
   mainTools.Tools.AddRange(new ToolBase[]{newIdentity, newServer, deleteItem});
   foreach (ToolBase tool in mainTools.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategoryTools;
   }
   ToolBase t = mainTools.Tools["toolDelete"];
   t.InstanceProps.IsFirstInGroup = true;
  }
  public void SetToolbarVisible(string toolbarId, bool visible) {
   this.manager.Toolbars[toolbarId].Visible = visible;
  }
  public bool IsToolbarVisible(string toolbarId) {
   return this.manager.Toolbars[toolbarId].Visible;
  }
  private void InitMenuBar() {
   AppPopupMenuCommand fileMenu = new AppPopupMenuCommand(
    "mnuFile", owner.Mediator, null,
    SR.MainMenuFileCaption, SR.MainMenuFileDesc);
   fileMenu.SharedProps.ShowInCustomizer = false;
   AppPopupMenuCommand editMenu = new AppPopupMenuCommand(
    "mnuEdit", owner.Mediator, null,
    SR.MainMenuEditCaption, SR.MainMenuEditDesc);
   editMenu.SharedProps.ShowInCustomizer = false;
   AppPopupMenuCommand viewMenu = new AppPopupMenuCommand(
    "mnuView", owner.Mediator, null,
    SR.MainMenuViewCaption, SR.MainMenuViewDesc);
   viewMenu.SharedProps.ShowInCustomizer = false;
   AppPopupMenuCommand toolsMenu = new AppPopupMenuCommand(
    "mnuTools", owner.Mediator, null,
    SR.MainMenuToolsCaption, SR.MainMenuToolsDesc);
   toolsMenu.SharedProps.ShowInCustomizer = false;
   AppPopupMenuCommand helpMenu = new AppPopupMenuCommand(
    "mnuHelp", owner.Mediator, null,
    SR.MainMenuHelpCaption, SR.MainMenuHelpDesc);
   helpMenu.SharedProps.ShowInCustomizer = false;
   this.manager.Tools.AddRange(new ToolBase[] {fileMenu, editMenu, viewMenu, toolsMenu, helpMenu});
   UltraToolbar menu = this.manager.Toolbars["tbMainMenu"];
   menu.Tools.AddRange(new ToolBase[]{fileMenu, editMenu, viewMenu, toolsMenu, helpMenu});
   foreach (ToolBase tool in menu.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategoryMenu;
   }
   CreateFileMenu(fileMenu);
   CreateEditMenu(editMenu);
   CreateViewMenu(viewMenu);
   CreateToolsMenu(toolsMenu);
   CreateHelpMenu(helpMenu);
  }
  private void CreateFileMenu(AppPopupMenuCommand mc) {
   AppButtonToolCommand newSubscription = new AppButtonToolCommand(
    "cmdNewSubscription", owner.Mediator, owner.CmdNewSubscription,
    SR.MenuNewSubscriptionByWizardCaption, SR.MenuNewSubscriptionByWizardDesc,
    Resource.ToolItemImage.NewSubscription, shortcutHandler);
   AppButtonToolCommand importFeeds = new AppButtonToolCommand(
    "cmdImportFeeds", owner.Mediator, owner.CmdImportFeeds,
    SR.MenuImportFeedsCaption, SR.MenuImportFeedsDesc, shortcutHandler);
   AppButtonToolCommand exportFeeds = new AppButtonToolCommand(
    "cmdExportFeeds", owner.Mediator, owner.CmdExportFeeds,
    SR.MenuExportFeedsCaption, SR.MenuExportFeedsDesc, shortcutHandler);
   AppButtonToolCommand appExit = new AppButtonToolCommand(
    "cmdCloseExit", owner.Mediator, owner.CmdExitApp,
    SR.MenuAppCloseExitCaption, SR.MenuAppCloseExitDesc, shortcutHandler);
   AppStateButtonToolCommand toggleOffline = new AppStateButtonToolCommand(
    "cmdToggleOfflineMode", owner.Mediator, owner.CmdToggleInternetConnectionMode,
    SR.MenuAppInternetConnectionModeCaption, SR.MenuAppInternetConnectionModeDesc, shortcutHandler);
   this.manager.Tools.AddRange(new ToolBase[] {newSubscription,importFeeds,exportFeeds,toggleOffline,appExit});
   mc.Tools.AddRange(new ToolBase[]{newSubscription,importFeeds,exportFeeds,toggleOffline,appExit});
   foreach (ToolBase tool in mc.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategoryFile;
   }
   ToolBase t = mc.Tools["cmdImportFeeds"];
   t.InstanceProps.IsFirstInGroup = true;
   t = mc.Tools["cmdToggleOfflineMode"];
   t.InstanceProps.IsFirstInGroup = true;
   t = mc.Tools["cmdCloseExit"];
   t.InstanceProps.IsFirstInGroup = true;
  }
  private void CreateEditMenu(AppPopupMenuCommand mc) {
   AppButtonToolCommand selectAll = new AppButtonToolCommand(
    "cmdSelectAllFeedItems", owner.Mediator, main.CmdSelectAllNewsItems,
    SR.MenuListViewSelectAllCaption, SR.MenuListViewSelectAllDesc, shortcutHandler);
   this.manager.Tools.AddRange(new ToolBase[] {selectAll});
   mc.Tools.AddRange(new ToolBase[]{selectAll});
   foreach (ToolBase tool in mc.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategoryEdit;
   }
  }
  private void CreateViewMenu(AppPopupMenuCommand mc) {
   AppStateButtonToolCommand toogleTreeViewState = new AppStateButtonToolCommand(
    "cmdToggleTreeViewState", owner.Mediator, main.CmdDockShowSubscriptions,
    SR.MenuToggleTreeViewStateCaption, SR.MenuToggleTreeViewStateDesc,
    Resource.ToolItemImage.ToggleSubscriptions, shortcutHandler);
   AppStateButtonToolCommand toggleRssSearchViewState = new AppStateButtonToolCommand(
    "cmdToggleRssSearchTabState", owner.Mediator, main.CmdDockShowRssSearch,
    SR.MenuToggleRssSearchTabStateCaption, SR.MenuToggleRssSearchTabStateDesc,
    Resource.ToolItemImage.ToggleRssSearch, shortcutHandler);
   AppPopupMenuCommand toolbarsDropDownMenu = new AppPopupMenuCommand(
    "mnuViewToolbars", owner.Mediator, null,
    SR.MenuViewToolbarsCaption, SR.MenuViewToolbarsDesc);
   AppStateButtonToolCommand subTbMain = new AppStateButtonToolCommand(
    "cmdToggleMainTBViewState", owner.Mediator, main.CmdToggleMainTBViewState,
    SR.MenuViewToolbarMainCaption, SR.MenuViewToolbarMainDesc, shortcutHandler);
   subTbMain.Checked = true;
   subTbMain.SharedProps.ShowInCustomizer = false;
   AppStateButtonToolCommand subTbWeb = new AppStateButtonToolCommand(
    "cmdToggleWebTBViewState", owner.Mediator, main.CmdToggleWebTBViewState,
    SR.MenuViewToolbarWebNavigationCaption, SR.MenuViewToolbarWebNavigationDesc, shortcutHandler);
   subTbWeb.Checked = true;
   subTbWeb.SharedProps.ShowInCustomizer = false;
   AppStateButtonToolCommand subTbWebSearch = new AppStateButtonToolCommand(
    "cmdToggleWebSearchTBViewState", owner.Mediator, main.CmdToggleWebSearchTBViewState,
    SR.MenuViewToolbarWebSearchCaption, SR.MenuViewToolbarWebSearchDesc, shortcutHandler);
   subTbWebSearch.Checked = true;
   subTbWebSearch.SharedProps.ShowInCustomizer = false;
   this.manager.Tools.AddRange(new ToolBase[] {subTbMain, subTbWeb, subTbWebSearch});
   toolbarsDropDownMenu.Tools.AddRange(new ToolBase[]{subTbMain, subTbWeb, subTbWebSearch});
   foreach (ToolBase tool in toolbarsDropDownMenu.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategoryView;
   }
   AppPopupMenuCommand columnChooserDropDownMenu = new AppPopupMenuCommand(
    "cmdColumnChooserMain", owner.Mediator, null,
    SR.MenuColumnChooserCaption, SR.MenuColumnChooserDesc);
   foreach (string colID in Enum.GetNames(typeof(NewsItemSortField))) {
    AppStateButtonToolCommand subL4_subColumn = new AppStateButtonToolCommand(
     "cmdListviewColumn." + colID, owner.Mediator, main.CmdToggleListviewColumn,
                    SR.Keys.GetString("MenuColumnChooser" + colID + "Caption"),
                    SR.Keys.GetString("MenuColumnChooser" + colID + "Desc"), shortcutHandler);
    subL4_subColumn.SharedProps.Category = SR.MainForm_ToolCategoryView;
    this.manager.Tools.AddRange(new ToolBase[] {subL4_subColumn});
    columnChooserDropDownMenu.Tools.AddRange(new ToolBase[]{subL4_subColumn});
   }
   AppButtonToolCommand subL4_subUseCatLayout = new AppButtonToolCommand(
    "cmdColumnChooserUseCategoryLayoutGlobal", owner.Mediator, main.CmdColumnChooserUseCategoryLayoutGlobal,
    SR.MenuColumnChooserUseCategoryLayoutGlobalCaption, SR.MenuColumnChooserUseCategoryLayoutGlobalDesc, shortcutHandler);
   AppButtonToolCommand subL4_subUseFeedLayout = new AppButtonToolCommand(
    "cmdColumnChooserUseFeedLayoutGlobal", owner.Mediator, main.CmdColumnChooserUseFeedLayoutGlobal,
    SR.MenuColumnChooserUseFeedLayoutGlobalCaption, SR.MenuColumnChooserUseFeedLayoutGlobalDesc, shortcutHandler);
   AppButtonToolCommand subL4_subResetLayout = new AppButtonToolCommand(
    "cmdColumnChooserResetToDefault", owner.Mediator, main.CmdColumnChooserResetToDefault,
    SR.MenuColumnChooserResetLayoutToDefaultCaption, SR.MenuColumnChooserResetLayoutToDefaultDesc, shortcutHandler);
   subL4_subResetLayout.Enabled = false;
   this.manager.Tools.AddRange(new ToolBase[] {subL4_subUseCatLayout, subL4_subUseFeedLayout, subL4_subResetLayout});
   columnChooserDropDownMenu.Tools.AddRange(new ToolBase[]{ subL4_subUseCatLayout, subL4_subUseFeedLayout, subL4_subResetLayout});
   foreach (ToolBase tool in columnChooserDropDownMenu.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategoryView;
   }
   AppStateButtonToolCommand outlookReadingViewState = new AppStateButtonToolCommand(
    "cmdViewOutlookReadingPane", owner.Mediator, main.CmdViewOutlookReadingPane,
    SR.MenuViewOutlookReadingPane, SR.MenuViewOutlookReadingPane, shortcutHandler);
   AppPopupMenuCommand layoutPositionDropDownMenu = new AppPopupMenuCommand(
    "cmdFeedDetailLayoutPosition", owner.Mediator, null,
    SR.MenuFeedDetailLayoutCaption, SR.MenuFeedDetailLayoutDesc);
   AppStateButtonToolCommand subSub1 = new AppStateButtonToolCommand(
    "cmdFeedDetailLayoutPosTop", owner.Mediator, main.CmdFeedDetailLayoutPosTop,
    SR.MenuFeedDetailLayoutTopCaption, SR.MenuFeedDetailLayoutTopDesc,
    Resource.ToolItemImage.ItemDetailViewAtTop, shortcutHandler);
   subSub1.Checked = true;
   AppStateButtonToolCommand subSub2 = new AppStateButtonToolCommand(
    "cmdFeedDetailLayoutPosLeft", owner.Mediator, main.CmdFeedDetailLayoutPosLeft,
    SR.MenuFeedDetailLayoutLeftCaption, SR.MenuFeedDetailLayoutLeftDesc,
    Resource.ToolItemImage.ItemDetailViewAtLeft, shortcutHandler);
   AppStateButtonToolCommand subSub3 = new AppStateButtonToolCommand(
    "cmdFeedDetailLayoutPosRight", owner.Mediator, main.CmdFeedDetailLayoutPosRight,
    SR.MenuFeedDetailLayoutRightCaption, SR.MenuFeedDetailLayoutRightDesc,
    Resource.ToolItemImage.ItemDetailViewAtRight, shortcutHandler);
   AppStateButtonToolCommand subSub4 = new AppStateButtonToolCommand(
    "cmdFeedDetailLayoutPosBottom", owner.Mediator, main.CmdFeedDetailLayoutPosBottom,
    SR.MenuFeedDetailLayoutBottomCaption, SR.MenuFeedDetailLayoutBottomDesc,
    Resource.ToolItemImage.ItemDetailViewAtBottom, shortcutHandler);
            this.manager.Tools.AddRange(new ToolBase[] { subSub1, subSub2, subSub3, subSub4 });
            layoutPositionDropDownMenu.Tools.AddRange(new ToolBase[] { subSub1, subSub2, subSub3, subSub4 });
            foreach (ToolBase tool in layoutPositionDropDownMenu.Tools)
            {
                tool.SharedProps.Category = SR.MainForm_ToolCategoryView;
            }
            AppPopupMenuCommand textSizeDropDownMenu = new AppPopupMenuCommand(
                "cmdFeedDetailTextSize", owner.Mediator, null,
                SR.MenuFeedDetailTextSizeCaption, SR.MenuFeedDetailTextSizeDesc);
            AppStateButtonToolCommand subSub10 = new AppStateButtonToolCommand(
                "cmdFeedDetailTextSizeSmallest", owner.Mediator, main.CmdFeedDetailTextSizeSmallest,
                SR.MenuFeedDetailTextSizeSmallestCaption, SR.MenuFeedDetailTextSizeSmallestDesc,
                shortcutHandler);
            subSub10.Checked = (owner.Preferences.ReadingPaneTextSize == TextSize.Smallest);
            AppStateButtonToolCommand subSub20 = new AppStateButtonToolCommand(
                "cmdFeedDetailTextSizeSmaller", owner.Mediator, main.CmdFeedDetailTextSizeSmaller,
                SR.MenuFeedDetailTextSizeSmallerCaption, SR.MenuFeedDetailTextSizeSmallerDesc,
                shortcutHandler);
            subSub20.Checked = (owner.Preferences.ReadingPaneTextSize == TextSize.Smaller);
            AppStateButtonToolCommand subSub30 = new AppStateButtonToolCommand(
               "cmdFeedDetailTextSizeMedium", owner.Mediator, main.CmdFeedDetailTextSizeMedium,
               SR.MenuFeedDetailTextSizeMediumCaption, SR.MenuFeedDetailTextSizeMediumDesc,
               shortcutHandler);
            subSub30.Checked = (owner.Preferences.ReadingPaneTextSize == TextSize.Medium);
            AppStateButtonToolCommand subSub40 = new AppStateButtonToolCommand(
               "cmdFeedDetailTextSizeLarger", owner.Mediator, main.CmdFeedDetailTextSizeLarger,
               SR.MenuFeedDetailTextSizeLargerCaption, SR.MenuFeedDetailTextSizeLargerDesc,
               shortcutHandler);
            subSub40.Checked = (owner.Preferences.ReadingPaneTextSize == TextSize.Larger);
            AppStateButtonToolCommand subSub50 = new AppStateButtonToolCommand(
               "cmdFeedDetailTextSizeLargest", owner.Mediator, main.CmdFeedDetailTextSizeLargest,
               SR.MenuFeedDetailTextSizeLargestCaption, SR.MenuFeedDetailTextSizeLargestDesc,
               shortcutHandler);
            subSub50.Checked = (owner.Preferences.ReadingPaneTextSize == TextSize.Largest);
            this.manager.Tools.AddRange(new ToolBase[] { subSub10, subSub20, subSub30, subSub40, subSub50 });
            textSizeDropDownMenu.Tools.AddRange(new ToolBase[] { subSub10, subSub20, subSub30, subSub40, subSub50 });
            foreach (ToolBase tool in textSizeDropDownMenu.Tools)
            {
                tool.SharedProps.Category = SR.MainForm_ToolCategoryView;
            }
            this.manager.Tools.AddRange(new ToolBase[] { toogleTreeViewState, toggleRssSearchViewState, toolbarsDropDownMenu, layoutPositionDropDownMenu, columnChooserDropDownMenu, textSizeDropDownMenu, outlookReadingViewState });
            mc.Tools.AddRange(new ToolBase[] { toogleTreeViewState, toggleRssSearchViewState, toolbarsDropDownMenu, layoutPositionDropDownMenu, columnChooserDropDownMenu, textSizeDropDownMenu, outlookReadingViewState });
   foreach (ToolBase tool in mc.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategoryView;
   }
   ToolBase t = mc.Tools["mnuViewToolbars"];
   t.InstanceProps.IsFirstInGroup = true;
   t = columnChooserDropDownMenu.Tools["cmdColumnChooserUseCategoryLayoutGlobal"];
   t.InstanceProps.IsFirstInGroup = true;
   t = columnChooserDropDownMenu.Tools["cmdColumnChooserResetToDefault"];
   t.InstanceProps.IsFirstInGroup = true;
   t = mc.Tools["cmdFeedDetailLayoutPosition"];
   t.InstanceProps.IsFirstInGroup = true;
  }
  private void CreateToolsMenu(AppPopupMenuCommand mc) {
   AppButtonToolCommand style1 = new AppButtonToolCommand(
    "cmdRefreshFeeds", owner.Mediator, owner.CmdRefreshFeeds,
    SR.MenuUpdateAllFeedsCaption, SR.MenuUpdateAllFeedsDesc,
    Resource.ToolItemImage.RefreshAll, shortcutHandler);
   AppButtonToolCommand style2 = new AppButtonToolCommand(
    "cmdOpenConfigIdentitiesDialog", owner.Mediator, main.CmdOpenConfigIdentitiesDialog,
    SR.MenuOpenConfigIdentitiesDialogCaption, SR.MenuOpenConfigIdentitiesDialogdesc, shortcutHandler);
   AppButtonToolCommand style3 = new AppButtonToolCommand(
    "cmdOpenConfigNntpServerDialog", owner.Mediator, main.CmdOpenConfigNntpServerDialog,
    SR.MenuOpenConfigNntpServerDialogCaption, SR.MenuOpenConfigNntpServerDialogDesc,
    shortcutHandler);
   style3.Enabled = true;
   AppButtonToolCommand toolTopStories = new AppButtonToolCommand(
    "cmdTopStories", owner.Mediator, owner.CmdTopStories,
    SR.MenuTopStoriesCaption, SR.MenuTopStoriesDesc);
   toolTopStories.SharedProps.AppearancesSmall.Appearance.Image = Properties.Resources.hotnews_16;
   toolTopStories.SharedProps.DisplayStyle = ToolDisplayStyle.ImageAndText;
   AppButtonToolCommand style51 = new AppButtonToolCommand(
    "cmdFeedItemNewPost", owner.Mediator, owner.CmdPostNewItem,
    SR.MenuPostNewFeedItemCaption, SR.MenuPostNewFeedItemDesc,
    Resource.ToolItemImage.NewPost,shortcutHandler);
   style51.Enabled = false;
   AppButtonToolCommand style52 = new AppButtonToolCommand(
    "cmdFeedItemPostReply", owner.Mediator, owner.CmdPostReplyToItem,
    SR.MenuPostReplyFeedItemCaption, SR.MenuPostReplyFeedItemDesc,
    Resource.ToolItemImage.PostReply, shortcutHandler);
   style52.Enabled = false;
   AppButtonToolCommand style6 = new AppButtonToolCommand(
    "cmdUploadFeeds", owner.Mediator, owner.CmdUploadFeeds,
    SR.MenuUploadFeedsCaption, SR.MenuUploadFeedsDesc, shortcutHandler);
   AppButtonToolCommand style7 = new AppButtonToolCommand(
    "cmdDownloadFeeds", owner.Mediator, owner.CmdDownloadFeeds,
    SR.MenuDownloadFeedsCaption, SR.MenuDownloadFeedsDesc, shortcutHandler);
   AppButtonToolCommand style8 = new AppButtonToolCommand(
    "cmdOpenManageAddInsDialog", owner.Mediator, owner.CmdOpenManageAddInsDialog,
    SR.MenuOpenManageAddInsDialogCaption, SR.MenuOpenManageAddInsDialogDesc,
    shortcutHandler);
   AppButtonToolCommand style9 = new AppButtonToolCommand(
    "cmdShowMainAppOptions", owner.Mediator, owner.CmdShowOptions,
    SR.MenuAppOptionsCaption, SR.MenuAppOptionsDesc,
    Resource.ToolItemImage.OptionsDialog, shortcutHandler);
   this.manager.Tools.AddRange(new ToolBase[] { style1, toolTopStories, style51, style52, style6, style7, style2, style3, style8, style9 });
   mc.Tools.AddRange(new ToolBase[] { style1, toolTopStories, style51, style52, style6, style7, style2, style3, style8, style9 });
   foreach (ToolBase tool in mc.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategoryTools;
   }
   foreach (string toolKey in new string[] {
    "cmdOpenConfigIdentitiesDialog",
    "cmdTopStories",
    "cmdFeedItemNewPost",
    "cmdUploadFeeds",
    "cmdOpenManageAddInsDialog",
    "cmdShowMainAppOptions"
   })
   {
    ToolBase t = mc.Tools[toolKey];
    t.InstanceProps.IsFirstInGroup = true;
   }
  }
  private void CreateHelpMenu(AppPopupMenuCommand mc) {
   AppButtonToolCommand styleHelpWebDoc = new AppButtonToolCommand(
    "cmdHelpWebDoc", owner.Mediator, owner.CmdWebHelp,
    SR.MenuWebHelpCaption, SR.MenuWebHelpDesc, shortcutHandler);
   AppButtonToolCommand style0 = new AppButtonToolCommand(
    "cmdWorkspaceNews", owner.Mediator, owner.CmdWorkspaceNews,
    SR.MenuWorkspaceNewsCaption, SR.MenuWorkspaceNewsDesc, shortcutHandler);
   AppButtonToolCommand style1 = new AppButtonToolCommand(
    "cmdReportBug", owner.Mediator, owner.CmdReportAppBug,
    SR.MenuBugReportCaption, SR.MenuBugReportDesc, shortcutHandler);
   AppButtonToolCommand style2 = new AppButtonToolCommand(
    "cmdAbout", owner.Mediator, owner.CmdAboutApp ,
    SR.MenuAboutCaption, SR.MenuAboutDesc, shortcutHandler);
   style2.SharedProps.AppearancesSmall.Appearance.Image = Properties.Resources.rssbandit_16;
   style2.SharedProps.AppearancesLarge.Appearance.Image = Properties.Resources.rssbandit_32;
   AppButtonToolCommand style3 = new AppButtonToolCommand(
    "cmdCheckForUpdates", owner.Mediator, owner.CmdCheckForUpdates,
    SR.MenuCheckForUpdatesCaption, SR.MenuCheckForUpdatesDesc, shortcutHandler);
   AppButtonToolCommand style4 = new AppButtonToolCommand(
    "cmdWikiNews", owner.Mediator, owner.CmdWikiNews,
    SR.MenuBanditWikiCaption, SR.MenuBanditWikiDesc, shortcutHandler);
   AppButtonToolCommand style5 = new AppButtonToolCommand(
    "cmdVisitForum",owner.Mediator, owner.CmdVisitForum,
    SR.MenuBanditForumCaption, SR.MenuBanditForumDesc, shortcutHandler);
   AppButtonToolCommand style6 = new AppButtonToolCommand(
    "cmdDonateToProject",owner.Mediator, owner.CmdDonateToProject,
    SR.MenuDonateToProjectCaption, SR.MenuDonateToProjectDesc, shortcutHandler);
   AppButtonToolCommand sendLogs = new AppButtonToolCommand(
    "cmdSendLogsByMail", owner.Mediator, owner.CmdSendLogsByMail,
    SR.MenuSendLogsByMailCaption, SR.MenuSendLogsByMailDesc, shortcutHandler);
   sendLogs.Enabled = false;
   try {
    if (File.Exists(RssBanditApplication.GetLogFileName()))
     sendLogs.Enabled = true;
   } catch { }
   this.manager.Tools.AddRange(new ToolBase[] {sendLogs, styleHelpWebDoc, style4, style5, style0,style1,style3,style6,style2});
   mc.Tools.AddRange(new ToolBase[]{sendLogs, styleHelpWebDoc, style4, style5, style0,style1,style3,style6,style2});
   foreach (ToolBase tool in mc.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategoryHelp;
   }
   ToolBase t = mc.Tools["cmdAbout"];
   t.InstanceProps.IsFirstInGroup = true;
   t = mc.Tools["cmdCheckForUpdates"];
   t.InstanceProps.IsFirstInGroup = true;
   t = mc.Tools["cmdWikiNews"];
   t.InstanceProps.IsFirstInGroup = true;
  }
  private void CreateMainToolbar(UltraToolbarBase tb) {
   AppPopupMenuCommand toolNew = new AppPopupMenuCommand(
    "mnuNewFeed", owner.Mediator, owner.CmdNewFeed,
    SR.MenuNewCmdsCaption, SR.MenuNewCmdsDesc, Resource.ToolItemImage.NewSubscription);
   toolNew.DropDownArrowStyle = DropDownArrowStyle.Segmented;
   toolNew.SharedProps.DisplayStyle = ToolDisplayStyle.ImageAndText;
   AppButtonToolCommand subNewFeed = new AppButtonToolCommand(
    "cmdNewFeed", owner.Mediator, owner.CmdNewFeed,
    SR.MenuNewFeedCaption, SR.MenuNewFeedDesc, Resource.ToolItemImage.NewSubscription);
   AppButtonToolCommand subNewNntp = new AppButtonToolCommand(
    "cmdNewNntpFeed", owner.Mediator, owner.CmdNewNntpFeed,
    SR.MenuNewNntpFeedCaption, SR.MenuNewNntpFeedDesc,
    Resource.ToolItemImage.NewNntpSubscription);
   AppButtonToolCommand subNewDiscovered = new AppButtonToolCommand(
    "cmdAutoDiscoverFeed", owner.Mediator, owner.CmdAutoDiscoverFeed,
    SR.MenuNewDiscoveredFeedCaption, SR.MenuNewDiscoveredFeedDesc,
    Resource.ToolItemImage.NewDiscoveredSubscription);
   this.manager.Tools.AddRange(new ToolBase[]{subNewFeed,subNewNntp,subNewDiscovered});
            toolNew.Tools.AddRange(new ToolBase[] { subNewFeed, subNewNntp, subNewDiscovered});
   foreach (ToolBase tool in toolNew.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategoryTools;
   }
   AppButtonToolCommand tool0 = (AppButtonToolCommand)this.manager.Tools["cmdRefreshFeeds"];
   tool0.SharedProps.DisplayStyle = ToolDisplayStyle.ImageAndText;
   AppButtonToolCommand tool1 = new AppButtonToolCommand(
    "cmdNextUnreadFeedItem", owner.Mediator, owner.CmdNextUnreadFeedItem,
    SR.MenuNextUnreadItemCaption, SR.MenuNextUnreadItemDesc,
    Resource.ToolItemImage.NextUnreadItem);
   tool1.SharedProps.DisplayStyle = ToolDisplayStyle.ImageAndText;
   AppButtonToolCommand tool2 = new AppButtonToolCommand(
    "cmdCatchUpCurrentSelectedNode", owner.Mediator, owner.CmdCatchUpCurrentSelectedNode,
    SR.MenuCatchUpSelectedNodeCaption, SR.MenuCatchUpSelectedNodeDesc,
    Resource.ToolItemImage.MarkAsRead);
   tool2.SharedProps.DisplayStyle = ToolDisplayStyle.ImageAndText;
   AppButtonToolCommand topStories = (AppButtonToolCommand)this.manager.Tools["cmdTopStories"];
   AppButtonToolCommand tool4 = (AppButtonToolCommand)this.manager.Tools["cmdFeedItemNewPost"];
   tool4.Enabled = false;
   AppButtonToolCommand tool5 = (AppButtonToolCommand)this.manager.Tools["cmdFeedItemPostReply"];
   tool5.SharedProps.DisplayStyle = ToolDisplayStyle.ImageAndText;
   tool5.Enabled = false;
   AppButtonToolCommand tool6 = new AppButtonToolCommand(
    "cmdNewRssSearch", owner.Mediator, main.CmdNewRssSearch,
    SR.MenuNewRssSearchCaption, SR.MenuNewRssSearchDesc,
    Resource.ToolItemImage.Search);
   AppPopupMenuCommand discoveredDropDown = new AppPopupMenuCommand(
    "cmdDiscoveredFeedsDropDown", owner.Mediator, null,
    SR.MenuAutodiscoveredFeedsDropdownCaption, SR.MenuAutodiscoveredFeedsDropdownDesc);
   Infragistics.Win.Appearance a = new Infragistics.Win.Appearance();
   a.Image = Properties.Resources.no_feed_discovered_16;
   discoveredDropDown.SharedProps.AppearancesSmall.Appearance = a;
   a = new Infragistics.Win.Appearance();
   a.Image = Properties.Resources.no_feed_discovered_32;
   discoveredDropDown.SharedProps.AppearancesLarge.Appearance = a;
   discoveredDropDown.SharedProps.DisplayStyle = ToolDisplayStyle.ImageOnlyOnToolbars;
   AppButtonToolCommand clearDiscoveredList = new AppButtonToolCommand(
    "cmdDiscoveredFeedsListClear", owner.Mediator, owner.BackgroundDiscoverFeedsHandler.CmdClearFeedsList,
    SR.MenuClearAutodiscoveredFeedsListCaption, SR.MenuClearAutodiscoveredFeedsListDesc);
   clearDiscoveredList.SharedProps.Category = SR.MainForm_ToolCategoryTools;
            this.manager.Tools.AddRange(new ToolBase[] { toolNew, tool1, tool2, tool6, discoveredDropDown, clearDiscoveredList });
   owner.BackgroundDiscoverFeedsHandler.SetControls(discoveredDropDown, clearDiscoveredList);
            tb.Tools.AddRange(new ToolBase[] { toolNew, tool0, tool1, tool2,topStories, tool4, tool5, tool6, discoveredDropDown });
   foreach (ToolBase tool in tb.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategoryTools;
   }
   foreach (string toolKey in new string[] {
    "cmdRefreshFeeds",
    "cmdTopStories",
    "cmdNextUnreadFeedItem",
    "cmdFeedItemNewPost"
   })
   {
    ToolBase t = tb.Tools[toolKey];
    t.InstanceProps.IsFirstInGroup = true;
   }
  }
  private void CreateBrowserToolbar(UltraToolbarBase tb)
  {
   AppPopupMenuCommand tool0 = new AppPopupMenuCommand(
    "cmdBrowserGoBack", owner.Mediator, owner.CmdBrowserGoBack,
    SR.MenuBrowserNavigateBackCaption, SR.MenuBrowserNavigateBackDesc,
    Resource.ToolItemImage.BrowserItemImageOffset + Resource.BrowserItemImage.GoBack);
   tool0.Enabled = false;
   tool0.DropDownArrowStyle = DropDownArrowStyle.Segmented;
   tool0.SharedProps.DisplayStyle = ToolDisplayStyle.ImageAndText;
   AppPopupMenuCommand tool1 = new AppPopupMenuCommand(
    "cmdBrowserGoForward", owner.Mediator, owner.CmdBrowserGoForward,
    SR.MenuBrowserNavigateForwardCaption, SR.MenuBrowserNavigateForwardDesc,
    Resource.ToolItemImage.BrowserItemImageOffset + Resource.BrowserItemImage.GoForward);
   tool1.Enabled = false;
   tool1.DropDownArrowStyle = DropDownArrowStyle.Segmented;
   tool1.SharedProps.DisplayStyle = ToolDisplayStyle.ImageOnlyOnToolbars;
   AppButtonToolCommand tool2 = new AppButtonToolCommand(
    "cmdBrowserCancelNavigation", owner.Mediator, owner.CmdBrowserCancelNavigation,
    SR.MenuBrowserNavigateCancelCaption, SR.MenuBrowserNavigateCancelDesc,
    Resource.ToolItemImage.BrowserItemImageOffset + Resource.BrowserItemImage.CancelNavigation);
   AppButtonToolCommand tool3 = new AppButtonToolCommand(
    "cmdBrowserRefresh", owner.Mediator, owner.CmdBrowserRefresh,
    SR.MenuBrowserRefreshCaption, SR.MenuBrowserRefreshDesc,
    Resource.ToolItemImage.BrowserItemImageOffset + Resource.BrowserItemImage.Refresh);
   ControlContainerTool urlDropdownContainerTool = new Infragistics.Win.UltraWinToolbars.ControlContainerTool("cmdUrlDropdownContainer");
   urlDropdownContainerTool.SharedProps.Caption = SR.MenuBrowserNavigateComboBoxCaption;
   urlDropdownContainerTool.SharedProps.StatusText = SR.MenuBrowserNavigateComboBoxDesc;
   urlDropdownContainerTool.SharedProps.MinWidth = 330;
   ComboBox navigateComboBox = new ComboBox();
   navigateComboBox.Name = "tbUrlComboBox";
   main.toolTip.SetToolTip(navigateComboBox, SR.MenuBrowserNavigateComboBoxDesc);
   navigateComboBox.KeyDown += main.OnNavigateComboBoxKeyDown;
   navigateComboBox.KeyPress += WinGuiMain.OnAnyEnterKeyPress;
   navigateComboBox.DragOver += WinGuiMain.OnNavigateComboBoxDragOver;
   navigateComboBox.DragDrop += main.OnNavigateComboBoxDragDrop;
   navigateComboBox.AllowDrop = true;
   navigateComboBox.Width = 330;
   urlDropdownContainerTool.Control = navigateComboBox;
   main.UrlComboBox = navigateComboBox;
   main.Controls.Add(navigateComboBox);
   main.urlExtender.Add(navigateComboBox);
   AppButtonToolCommand tool5 = new AppButtonToolCommand(
    "cmdBrowserNavigate", owner.Mediator, owner.CmdBrowserNavigate,
    SR.MenuBrowserDoNavigateCaption, SR.MenuBrowserDoNavigateDesc,
    Resource.ToolItemImage.BrowserItemImageOffset + Resource.BrowserItemImage.DoNavigate);
   tool5.SharedProps.DisplayStyle = ToolDisplayStyle.ImageAndText;
   AppButtonToolCommand tool6 = new AppButtonToolCommand(
    "cmdBrowserNewTab", owner.Mediator, owner.CmdBrowserCreateNewTab,
    SR.MenuBrowserNewTabCaption, SR.MenuBrowserNewTabDesc,
    Resource.ToolItemImage.BrowserItemImageOffset + Resource.BrowserItemImage.OpenNewTab);
   AppButtonToolCommand tool7 = new AppButtonToolCommand(
    "cmdBrowserNewExternalWindow", owner.Mediator, main.CmdOpenLinkInExternalBrowser,
    SR.MenuBrowserNewExternalWindowCaption, SR.MenuBrowserNewExternalWindowDesc,
    Resource.ToolItemImage.BrowserItemImageOffset + Resource.BrowserItemImage.OpenInExternalBrowser);
   this.manager.Tools.AddRange(new ToolBase[] {tool0,tool1,tool2,tool3,urlDropdownContainerTool,tool5,tool6,tool7});
   tb.Tools.AddRange(new ToolBase[]{tool0,tool1,tool2,tool3,urlDropdownContainerTool,tool5,tool6,tool7});
   foreach (ToolBase tool in tb.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategoryBrowse;
   }
   main.historyMenuManager.SetControls(tool0, tool1);
   ToolBase t = tb.Tools["cmdBrowserNewTab"];
   t.InstanceProps.IsFirstInGroup = true;
  }
  private void CreateSearchToolbar(UltraToolbarBase tb) {
   ComboBox searchComboBox = new ComboBox();
   searchComboBox.Name = "tbSearchComboBox";
   main.toolTip.SetToolTip(searchComboBox, SR.MenuDoSearchComboBoxDesc);
   searchComboBox.KeyDown += main.OnSearchComboBoxKeyDown;
   searchComboBox.KeyPress += WinGuiMain.OnAnyEnterKeyPress;
   searchComboBox.DragOver += main.OnSearchComboBoxDragOver;
   searchComboBox.DragDrop += main.OnSearchComboBoxDragDrop;
   searchComboBox.AllowDrop = true;
   searchComboBox.Width = 150;
   searchComboBox.DropDownWidth = 350;
   ControlContainerTool searchDropdownContainerTool = new Infragistics.Win.UltraWinToolbars.ControlContainerTool("cmdSearchDropdownContainer");
   searchDropdownContainerTool.SharedProps.Caption = SR.MenuDoSearchComboBoxCaption;
   searchDropdownContainerTool.SharedProps.StatusText = SR.MenuDoSearchComboBoxDesc;
   searchDropdownContainerTool.SharedProps.MinWidth = 150;
   searchDropdownContainerTool.Control = searchComboBox;
   main.SearchComboBox = searchComboBox;
   main.Controls.Add(searchComboBox);
   AppPopupMenuCommand tool2 = new AppPopupMenuCommand(
    "cmdSearchGo", owner.Mediator, main.CmdSearchGo,
    SR.MenuDoSearchWebCaption, SR.MenuDoSearchWebDesc,
    Resource.ToolItemImage.BrowserItemImageOffset + Resource.BrowserItemImage.SearchWeb);
   tool2.DropDownArrowStyle = DropDownArrowStyle.Segmented;
   this.manager.Tools.AddRange(new ToolBase[] {searchDropdownContainerTool, tool2});
   tb.Tools.AddRange(new ToolBase[]{searchDropdownContainerTool, tool2});
   foreach (ToolBase tool in tb.Tools) {
    tool.SharedProps.Category = SR.MainForm_ToolCategorySearch;
   }
  }
  public void BuildSearchMenuDropdown(IList searchEngines, CommandMediator mediator, ExecuteCommandHandler executor)
  {
   AppPopupMenuCommand parent = (AppPopupMenuCommand)manager.Toolbars[Resource.Toolbar.SearchTools].Tools["cmdSearchGo"];
   parent.Tools.Clear();
   parent.Enabled = false;
   foreach (SearchEngine engine in searchEngines) {
    AppButtonToolCommand item = null;
    string engineKey = "cmdExecuteSearchEngine" + engine.Title;
    if (manager.Tools.Exists(engineKey)) {
     item = (AppButtonToolCommand)manager.Tools[engineKey];
    } else {
     item = new AppButtonToolCommand(
      engineKey, mediator, executor,
      engine.Title, engine.Description);
     manager.Tools.Add(item);
    }
    if (item.Mediator == null) {
     item.Mediator = mediator;
     item.OnExecute += executor;
     mediator.RegisterCommand(engineKey, item);
    } else
     if (item.Mediator != mediator) {
     mediator.ReRegisterCommand(item);
    }
    item.SharedProps.ShowInCustomizer = false;
    item.Tag = engine;
    Infragistics.Win.Appearance a = new Infragistics.Win.Appearance();
    item.SharedProps.AppearancesSmall.Appearance = a;
    if (engine.ImageName != null && engine.ImageName.Trim().Length > 0) {
     string p = Path.Combine(RssBanditApplication.GetSearchesPath(), engine.ImageName);
     if (File.Exists(p)) {
      Icon ico = null;
      Image img = null;
      try {
       if (Path.GetExtension(p).ToLower().EndsWith("ico")) {
        ico = new Icon(p);
        img = ico.ToBitmap();
       }
       else {
        img = Image.FromFile(p);
       }
      }
      catch (Exception e) {
       _log.Error("Exception reading bitmap or Icon for searchEngine '" + engine.Title + "'.", e);
      }
      if (img != null) {
       a.Image = img;
      }
     }
    }
    parent.Tools.Add(item);
   }
   if (parent.Tools.Count > 0)
    parent.Enabled = true;
  }
  public class RssBanditToolbarManager : UltraToolbarsManager {
   private static readonly RssBanditToolbarManager.ToolFactory toolFactory = null;
   private static readonly log4net.ILog _log = Logger.Log.GetLogger(typeof(RssBanditToolbarManager));
   private bool isRegistered = false;
   static RssBanditToolbarManager() {
    RssBanditToolbarManager.toolFactory = new RssBanditToolbarManager.ToolFactory();
   }
   public RssBanditToolbarManager() {
    RssBanditToolbarManager.toolFactory.Register();
    this.isRegistered = true;
   }
   public RssBanditToolbarManager(IContainer container):base(container) {
    RssBanditToolbarManager.toolFactory.Register();
    this.isRegistered = true;
   }
   internal static readonly Guid AppPopupMenuCommand_TOOLID = new Guid("B2C41B23-539C-402b-A991-0277C65AA91B");
   internal static readonly Guid AppButtonToolCommand_TOOLID = new Guid("BAD223CC-C7A0-4d28-9280-2A41049B2F82");
   internal static readonly Guid AppStateButtonToolCommand_TOOLID = new Guid("C3705644-795B-4ea7-9182-F4BC9DAD6AFE");
   protected override void Dispose(bool disposing) {
    if (this.isRegistered) {
     RssBanditToolbarManager.toolFactory.Unregister();
     this.isRegistered = false;
    }
    base.Dispose(disposing);
   }
   protected override bool ShowBuiltInToolTypesInCustomizer {
    get {
     return true;
    }
   }
   private class ToolFactory : IToolProvider
   {
    private int counter = 0;
    internal void Register() {
     if (this.counter == 0)
      this.RegisterTools();
     this.counter++;
    }
    internal void Unregister() {
     this.counter--;
     System.Diagnostics.Debug.Assert(this.counter >= 0, "The Register/Unregister counter is out of sync!");
     if (this.counter == 0)
      this.UnregisterTools();
    }
    ToolBase IToolProvider.CreateToolInstance(Guid toolID, string key) {
     if (toolID == RssBanditToolbarManager.AppPopupMenuCommand_TOOLID)
      return new AppPopupMenuCommand(key);
     if (toolID == RssBanditToolbarManager.AppButtonToolCommand_TOOLID)
      return new AppButtonToolCommand(key);
     if (toolID == RssBanditToolbarManager.AppStateButtonToolCommand_TOOLID)
      return new AppStateButtonToolCommand(key);
     return null;
    }
    private void RegisterTools() {
     try {
      bool result = UltraToolbarsManager.RegisterCustomToolType(this, RssBanditToolbarManager.AppPopupMenuCommand_TOOLID, "Application Popup Menu", "AppPopupMenuTool");
      if (result == false)
       _log.Error("Error registering AppPopupMenuCommand class!");
      result = UltraToolbarsManager.RegisterCustomToolType(this, RssBanditToolbarManager.AppButtonToolCommand_TOOLID, "Application Button Tool", "AppButtonToolButton");
      if (result == false)
       _log.Error("Error registering AppButtonToolCommand class!");
      result = UltraToolbarsManager.RegisterCustomToolType(this, RssBanditToolbarManager.AppStateButtonToolCommand_TOOLID, "Application State Button Tool", "AppStateButtonToolButton");
      if (result == false)
       _log.Error("Error registering AppStateButtonToolCommand class!");
     }
     catch {
      _log.Error("Error registering custom tool classes!");
     }
    }
    private void UnregisterTools() {
     UltraToolbarsManager.UnRegisterCustomToolType(RssBanditToolbarManager.AppPopupMenuCommand_TOOLID);
     UltraToolbarsManager.UnRegisterCustomToolType(RssBanditToolbarManager.AppButtonToolCommand_TOOLID);
     UltraToolbarsManager.UnRegisterCustomToolType(RssBanditToolbarManager.AppStateButtonToolCommand_TOOLID);
    }
   }
  }
 }
}
