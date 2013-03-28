using System; 
using System.Collections; 
using System.Collections.Generic; 
using System.Collections.Specialized; 
using System.ComponentModel; 
using System.Diagnostics; 
using System.Drawing; 
using System.Globalization; 
using System.IO; 
using System.Reflection; 
using System.Security.Cryptography.X509Certificates; 
using System.Security.Permissions; 
using System.Text; 
using System.Timers; 
using System.Windows.Forms; 
using System.Windows.Forms.ThListView; 
using System.Windows.Forms.ThListView.Sorting; 
using AppInteropServices; 
using IEControl; 
using Infragistics.Win; 
using Infragistics.Win.Misc; 
using Infragistics.Win.UltraWinExplorerBar; 
using Infragistics.Win.UltraWinToolbars; 
using Infragistics.Win.UltraWinToolTip; 
using Infragistics.Win.UltraWinTree; 
using log4net; 
using Microsoft.ApplicationBlocks.ExceptionManagement; 
using NewsComponents; 
using NewsComponents.Collections; 
using NewsComponents.Feed; 
using NewsComponents.Net; 
using NewsComponents.News; 
using NewsComponents.RelationCosmos; 
using NewsComponents.Search; 
using NewsComponents.Utils; 
using RssBandit.AppServices; 
using RssBandit.Common; 
using RssBandit.Common.Logging; 
using RssBandit.Filter; 
using RssBandit.Resources; 
using RssBandit.Utility.Keyboard; 
using RssBandit.WebSearch; 
using RssBandit.WinGui.Controls; 
using RssBandit.WinGui.Dialogs; 
using RssBandit.WinGui.Forms.ControlHelpers; 
using RssBandit.WinGui.Interfaces; 
using RssBandit.WinGui.Menus; 
using RssBandit.WinGui.Tools; 
using RssBandit.WinGui.Utility; 
using SHDocVw; 
using Syndication.Extensibility; 
using TD.SandDock; 
using TD.SandDock.Rendering; 
using AppExceptions = Microsoft.ApplicationBlocks.ExceptionManagement; 
using ExecuteCommandHandler=RssBandit.WinGui.Interfaces.ExecuteCommandHandler; 
using K = RssBandit.Utility.Keyboard; 
using SortOrder=System.Windows.Forms.SortOrder; 
using Timer=System.Windows.Forms.Timer; 
using ToolTip=System.Windows.Forms.ToolTip; namespace  RssBandit.WinGui.Forms {
	
    internal enum  BrowseAction 
    {
        NavigateCancel,
        NavigateBack,
        NavigateForward,
        DoRefresh
    } 
    internal enum  RootFolderType 
    {
        MyFeeds,
        SmartFolders,
        Finder
    } 
    internal enum  FeedProcessingState 
    {
        Normal,
        Updating,
        Failure,
    } 
    [Flags] 
    internal enum  DelayedTasks 
    {
        None = 0,
        NavigateToWebUrl = 1,
        StartRefreshOneFeed = 2,
        StartRefreshAllFeeds = 4,
        ShowFeedPropertiesDialog = 8,
        NavigateToFeedNewsItem = 16,
        AutoSubscribeFeedUrl = 32,
        ClearBrowserStatusInfo = 64,
        RefreshTreeUnreadStatus = 128,
        SyncRssSearchTree = 256,
        InitOnFinishLoading = 512,
        SaveUIConfiguration = 1024,
        NavigateToFeed = 2048,
        RefreshTreeCommentStatus = 4096,
    } 
    internal enum  NavigationPaneView 
    {
        Subscriptions,
        RssSearch,
    } 
    internal class  WinGuiMain  : Form,
                                ITabState, IMessageFilter, ICommandBarImplementationSupport {
		
        public delegate  void  UpdateTreeStatusDelegate (IDictionary<string, NewsFeed> theFeeds, RootFolderType rootFolder);
		
        private delegate  void  PopulateTreeFeedsDelegate (
            ICollection<category> categories, IDictionary<string, NewsFeed> feedsTable, string defaultCategory);
		
        private delegate  void  PopulateListViewDelegate (
            TreeFeedsNodeBase associatedFeedsNode, IList<NewsItem> list, bool forceReload, bool categorizedView,
            TreeFeedsNodeBase initialFeedsNode);
		
        public delegate  void  SetGuiMessageFeedbackDelegate (string message);
		
        public delegate  void  SetGuiMessageStateFeedbackDelegate (string message, ApplicationTrayState state);
		
        public delegate  void  GetCommentNewsItemsDelegate (NewsItem item, ThreadedListViewItem listViewItem);
		
        private delegate  void  StartNewsSearchDelegate (FinderNode node);
		
        private delegate  void  StartRssRemoteSearchDelegate (string searchUrl, FinderNode resultContainer);
		
        public delegate  void  NavigateToURLDelegate (string url, string tab, bool createNewTab, bool setFocus);
		
        private delegate  void  SubscribeToFeedUrlDelegate (string newFeedUrl);
		
        private delegate  void  DelayTaskDelegate (DelayedTasks task, object data, int interval);
		
        internal delegate  void  CloseMainForm (bool force);
		
        public  event EventHandler OnMinimize; 
        private readonly  Action<Action> InvokeOnGui;
 
        private readonly  Action<Action> InvokeOnGuiSync;
 
        private static readonly  TimeSpan SevenDays = new TimeSpan(7, 0, 0, 0);
 
        private  const int BrowserProgressBarWidth = 120; 
        private  const int MaxHeadlineWidth = 133; 
        private  const int _currentToolbarsVersion = 7; 
        private  const int _currentExplorerBarVersion = 1; 
        private static readonly  ILog _log = Log.GetLogger(typeof (WinGuiMain));
 
        private readonly  History _feedItemImpressionHistory;
 
        private  bool _navigationActionInProgress = false;
 
        private  bool _faviconsDownloaded = false;
 
        private  bool _browserTabsRestored = false;
 
        private  ToastNotifier toastNotifier;
 
        private  WheelSupport wheelSupport;
 
        internal  UrlCompletionExtender urlExtender;
 
        private  NavigatorHeaderHelper navigatorHeaderHelper;
 
        private  ToolbarHelper toolbarHelper;
 
        internal  HistoryMenuManager historyMenuManager;
 
        private readonly  TreeFeedsNodeBase[] _roots = new TreeFeedsNodeBase[3];
 
        private  TreeFeedsNodeBase _unreadItemsFeedsNode = null;
 
        private  TreeFeedsNodeBase _feedExceptionsFeedsNode = null;
 
        private  TreeFeedsNodeBase _sentItemsFeedsNode = null;
 
        private  TreeFeedsNodeBase _watchedItemsFeedsNode = null;
 
        private  TreeFeedsNodeBase _deletedItemsFeedsNode = null;
 
        private  TreeFeedsNodeBase _flaggedFeedsNodeRoot = null;
 
        private  TreeFeedsNodeBase _flaggedFeedsNodeFollowUp = null;
 
        private  TreeFeedsNodeBase _flaggedFeedsNodeRead = null;
 
        private  TreeFeedsNodeBase _flaggedFeedsNodeReview = null;
 
        private  TreeFeedsNodeBase _flaggedFeedsNodeForward = null;
 
        private  TreeFeedsNodeBase _flaggedFeedsNodeReply = null;
 
        private  FinderNode _searchResultNode = null;
 
        private  NewsItemFilterManager _filterManager = null;
 
        private  SearchPanel searchPanel;
 
        private  IList<IBlogExtension> blogExtensions = null;
 
        private readonly  Dictionary<string, object> tempFeedItemsRead = new Dictionary<string, object>();
 
        private readonly  Dictionary<string, Image> _favicons = new Dictionary<string, Image>();
 
        private readonly  Dictionary<string, object> feedsCurrentlyPopulated = new Dictionary<string, object>();
 
        private  ShortcutHandler _shortcutHandler;
 
        private  NewsItem _currentNewsItem = null;
 
        private  TreeFeedsNodeBase _currentSelectedFeedsNode = null;
 
        private  TreeFeedsNodeBase _currentDragFeedsNode = null;
 
        private  TreeFeedsNodeBase _currentDragHighlightFeedsNode = null;
 
        private  FeedInfo _currentFeedNewsItems = null;
 
        private  FeedInfoList _currentCategoryNewsItems = null;
 
        private  int _currentPageNumber = 1;
 
        private  int _lastPageNumber = 1;
 
        private  Rectangle _formRestoreBounds = Rectangle.Empty;
 
        private  int _lastUnreadFeedItemCountBeforeRefresh = 0;
 
        private  int _beSilentOnBalloonPopupCounter = 0;
 
        private  bool _forceShutdown = false;
 
        private readonly  FormWindowState initialStartupState = FormWindowState.Normal;
 
        private  int _webTabCounter = 0;
 
        private  bool _webUserNavigated = false;
 
        private  bool _webForceNewTab = false;
 
        private  Point _lastMousePosition = Point.Empty;
 
        private  ImageList _allToolImages = null;
 
        private  ImageList _treeImages = null;
 
        private  ImageList _listImages = null;
 
        private  MenuItem _feedInfoContextMenu = null;
 
        private  ContextMenu _treeRootContextMenu = null;
 
        private  ContextMenu _treeCategoryContextMenu = null;
 
        private  ContextMenu _treeFeedContextMenu = null;
 
        private  ContextMenu _notifyContextMenu = null;
 
        private  ContextMenu _listContextMenu = null;
 
        private  ContextMenu _treeLocalFeedContextMenu = null;
 
        private  ContextMenu _treeSearchFolderRootContextMenu = null;
 
        private  ContextMenu _treeSearchFolderContextMenu = null;
 
        private  ContextMenu _treeTempSearchFolderContextMenu = null;
 
        private  ContextMenu _docTabContextMenu = null;
 
        private  Point _contextMenuCalledAt = Point.Empty;
 
        private  AppContextMenuCommand _listContextMenuDownloadAttachment = null;
 
        private  MenuItem _listContextMenuDeleteItemsSeparator = null;
 
        private  MenuItem _listContextMenuDownloadAttachmentsSeparator = null;
 
        private  TrayStateManager _trayManager = null;
 
        private  NotifyIconAnimation _trayAni = null;
 
        private  string _tabStateUrl;
 
        private  RssBanditApplication owner;
 
        private  Panel panelFeedDetails;
 
        private  Panel panelWebDetail;
 
        private  ThreadedListView listFeedItems;
 
        private  UltraToolbarsDockArea _Main_Toolbars_Dock_Area_Left;
 
        private  UltraToolbarsDockArea _Main_Toolbars_Dock_Area_Right;
 
        private  UltraToolbarsDockArea _Main_Toolbars_Dock_Area_Top;
 
        private  UltraToolbarsDockArea _Main_Toolbars_Dock_Area_Bottom;
 
        private  UltraToolbarsManager ultraToolbarsManager;
 
        internal  ToolTip toolTip;
 
        private  Timer _timerResetStatus;
 
        private  Timer _startupTimer;
 
        private  System.Timers.Timer _timerTreeNodeExpand;
 
        private  System.Timers.Timer _timerRefreshFeeds;
 
        private  System.Timers.Timer _timerRefreshCommentFeeds;
 
        private  HtmlControl htmlDetail;
 
        private  StatusBar _status;
 
        private  StatusBarPanel statusBarBrowser;
 
        private  StatusBarPanel statusBarBrowserProgress;
 
        private  StatusBarPanel statusBarConnectionState;
 
        private  StatusBarPanel statusBarRssParser;
 
        private  ProgressBar progressBrowser;
 
        private  Panel panelRssSearch;
 
        private  UITaskTimer _uiTasksTimer;
 
        private  HelpProvider helpProvider1;
 
        private  SandDockManager sandDockManager;
 
        private  DockContainer rightSandDock;
 
        private  DockContainer bottomSandDock;
 
        private  DockContainer topSandDock;
 
        private  DocumentContainer _docContainer;
 
        private  DockControl _docFeedDetails;
 
        private  ThreadedListViewColumnHeader colHeadline;
 
        private  ThreadedListViewColumnHeader colDate;
 
        private  ThreadedListViewColumnHeader colTopic;
 
        private  CollapsibleSplitter detailsPaneSplitter;
 
        private  Timer _timerDispatchResultsToUI;
 
        private  UltraTree treeFeeds;
 
        private  UltraToolTipManager ultraToolTipManager;
 
        private  UltraTreeExtended listFeedItemsO;
 
        private  UltraExplorerBar Navigator;
 
        private  UltraExplorerBarContainerControl NavigatorFeedSubscriptions;
 
        private  UltraExplorerBarContainerControl NavigatorSearch;
 
        private  Panel panelFeedItems;
 
        private  Splitter splitterNavigator;
 
        private  Panel pNavigatorCollapsed;
 
        private  Panel panelClientAreaContainer;
 
        private  Panel panelFeedDetailsContainer;
 
        private  UltraLabel detailHeaderCaption;
 
        private  VerticalHeaderLabel navigatorHiddenCaption;
 
        private  IContainer components;
 
        public  WinGuiMain(RssBanditApplication theGuiOwner, FormWindowState initialFormState)
        {
            InvokeOnGuiSync = delegate(Action a)
            {
                GuiInvoker.Invoke(this, a);
            };
            InvokeOnGui = delegate(Action a)
            {
                GuiInvoker.InvokeAsync(this, a);
            };
            GuiOwner = theGuiOwner;
            initialStartupState = initialFormState;
            urlExtender = new UrlCompletionExtender(this);
            _feedItemImpressionHistory = new History( );
            _feedItemImpressionHistory.StateChanged += OnFeedItemImpressionHistoryStateChanged;
            InitializeComponent();
            Init();
            ApplyComponentTranslation();
        }
 
        protected  void Init()
        {
            OnMinimize += OnFormMinimize;
            MouseDown += OnFormMouseDown;
            Resize += OnFormResize;
            Closing += OnFormClosing;
            Load += OnLoad;
            HandleCreated += OnFormHandleCreated;
            Move += OnFormMove;
            Activated += OnFormActivated;
            Deactivate += OnFormDeactivate;
            owner.PreferencesChanged += OnPreferencesChanged;
            owner.FeedDeleted += OnFeedDeleted;
            wheelSupport = new WheelSupport(this);
            wheelSupport.OnGetChildControl += OnWheelSupportGetChildControl;
            SetFontAndColor(
                owner.Preferences.NormalFont, owner.Preferences.NormalFontColor,
                owner.Preferences.UnreadFont, owner.Preferences.UnreadFontColor,
                owner.Preferences.FlagFont, owner.Preferences.FlagFontColor,
                owner.Preferences.ErrorFont, owner.Preferences.ErrorFontColor,
                owner.Preferences.RefererFont, owner.Preferences.RefererFontColor,
                owner.Preferences.NewCommentsFont, owner.Preferences.NewCommentsFontColor
                );
            InitColors();
            InitResources();
            InitShortcutManager();
            CreateIGToolbars();
            InitStatusBar();
            InitDockHosts();
            InitDocumentManager();
            InitContextMenus();
            InitTrayIcon();
            InitWidgets();
        }
 
        private  void ApplyComponentTranslation()
        {
            Text = RssBanditApplication.CaptionOnly;
            detailHeaderCaption.Text = SR.MainForm_DetailHeaderCaption_AtStartup;
            navigatorHiddenCaption.Text = SR.MainForm_SubscriptionNavigatorCaption;
            Navigator.Groups[Resource.NavigatorGroup.Subscriptions].Text = SR.MainForm_SubscriptionNavigatorCaption;
            Navigator.Groups[Resource.NavigatorGroup.RssSearch].Text = SR.MainForm_SearchNavigatorCaption;
            _docFeedDetails.Text = SR.FeedDetailDocumentTabCaption;
            statusBarBrowser.ToolTipText = SR.MainForm_StatusBarBrowser_ToolTipText;
            statusBarBrowserProgress.ToolTipText = SR.MainForm_StatusBarBrowserProgress_ToolTipText;
            statusBarConnectionState.ToolTipText = SR.MainForm_StatusBarConnectionState_ToolTipText;
            statusBarRssParser.ToolTipText = SR.MainForm_StatusBarRssParser_ToolTipText;
        }
 
        protected  void InitColors()
        {
            BackColor = FontColorHelper.UiColorScheme.FloatingControlContainerToolbar;
            panelClientAreaContainer.BackColor = BackColor;
            splitterNavigator.BackColor = BackColor;
        }
 
        protected  void InitFilter()
        {
            _filterManager = new NewsItemFilterManager();
            _filterManager.Add("NewsItemReferrerFilter", new NewsItemReferrerFilter(owner));
            _filterManager.Add("NewsItemFlagFilter", new NewsItemFlagFilter());
        }
 
        public  int CurrentPageNumber
        {
            get
            {
                return _currentPageNumber;
            }
        }
 
        public  int LastPageNumber
        {
            get
            {
                return _lastPageNumber;
            }
        }
 
        public  RssBanditApplication GuiOwner
        {
            get
            {
                return owner;
            }
            set
            {
                owner = value;
            }
        }
 
        public  FormWindowState InitialStartupState
        {
            get
            {
                return initialStartupState;
            }
        }
 
        public  string UrlText
        {
            get
            {
                if (UrlComboBox.Text.Trim().Length > 0)
                    return UrlComboBox.Text.Trim();
                else
                    return _urlText;
            }
            set
            {
                _urlText = (value ?? String.Empty);
                if (_urlText.Equals("about:blank"))
                {
                    _urlText = String.Empty;
                }
                UrlComboBox.Text = _urlText;
            }
        }
 
        private  string _urlText = String.Empty;
 
        public  string WebSearchText
        {
            get
            {
                if (SearchComboBox.Text.Trim().Length > 0)
                    return SearchComboBox.Text.Trim();
                else
                    return _webSearchText;
            }
            set
            {
                _webSearchText = (value ?? String.Empty);
                SearchComboBox.Text = _webSearchText;
            }
        }
 
        private  string _webSearchText = String.Empty;
 
        public  ISmartFolder ExceptionNode
        {
            get
            {
                return _feedExceptionsFeedsNode as ISmartFolder;
            }
        }
 
        public  ISmartFolder FlaggedFeedsNode(Flagged flag)
        {
            switch (flag)
            {
                case Flagged.FollowUp:
                    return _flaggedFeedsNodeFollowUp as ISmartFolder;
                case Flagged.Read:
                    return _flaggedFeedsNodeRead as ISmartFolder;
                case Flagged.Review:
                    return _flaggedFeedsNodeReview as ISmartFolder;
                case Flagged.Reply:
                    return _flaggedFeedsNodeReply as ISmartFolder;
                case Flagged.Forward:
                    return _flaggedFeedsNodeForward as ISmartFolder;
                default:
                    return _flaggedFeedsNodeFollowUp as ISmartFolder;
            }
        }
 
        public  ISmartFolder SentItemsNode
        {
            get
            {
                return _sentItemsFeedsNode as ISmartFolder;
            }
        }
 
        public  ISmartFolder WatchedItemsNode
        {
            get
            {
                return _watchedItemsFeedsNode as ISmartFolder;
            }
        }
 
        public  ISmartFolder UnreadItemsNode
        {
            get
            {
                return _unreadItemsFeedsNode as ISmartFolder;
            }
        }
 
        public  ISmartFolder DeletedItemsNode
        {
            get
            {
                return _deletedItemsFeedsNode as ISmartFolder;
            }
        }
 
        public  ISmartFolder SearchResultNode
        {
            get
            {
                return _searchResultNode;
            }
        }
 
        public  void SetFontAndColor(
            Font normalFont, Color normalColor,
            Font unreadFont, Color unreadColor,
            Font highlightFont, Color highlightColor,
            Font errorFont, Color errorColor,
            Font refererFont, Color refererColor,
            Font newCommentsFont, Color newCommentsColor)
        {
            FontColorHelper.DefaultFont = normalFont;
            FontColorHelper.NormalStyle = normalFont.Style;
            FontColorHelper.NormalColor = normalColor;
            FontColorHelper.UnreadStyle = unreadFont.Style;
            FontColorHelper.UnreadColor = unreadColor;
            FontColorHelper.HighlightStyle = highlightFont.Style;
            FontColorHelper.HighlightColor = highlightColor;
            FontColorHelper.ReferenceStyle = refererFont.Style;
            FontColorHelper.ReferenceColor = refererColor;
            FontColorHelper.FailureStyle = errorFont.Style;
            FontColorHelper.FailureColor = errorColor;
            FontColorHelper.NewCommentsStyle = newCommentsFont.Style;
            FontColorHelper.NewCommentsColor = newCommentsColor;
            ResetTreeViewFontAndColor();
            ResetListViewFontAndColor();
            ResetListViewOutlookFontAndColor();
        }
 
        private  void ResetTreeViewFontAndColor()
        {
            try
            {
                treeFeeds.BeginUpdate();
                treeFeeds.Font = FontColorHelper.NormalFont;
                using (Graphics g = CreateGraphics())
                {
                    SizeF sz = g.MeasureString("(99999)", FontColorHelper.UnreadFont);
                    Size gz = new Size((int) sz.Width, (int) sz.Height);
                    if (! treeFeeds.RightImagesSize.Equals(gz))
                        treeFeeds.RightImagesSize = gz;
                }
                treeFeeds.Override.NodeAppearance.ForeColor = FontColorHelper.NormalColor;
                if (treeFeeds.Nodes.Count > 0)
                {
                    for (int i = 0; i < _roots.Length; i++)
                    {
                        TreeFeedsNodeBase startNode = _roots[i];
                        if (null != startNode)
                            WalkdownThenRefreshFontColor(startNode);
                    }
                }
            }
            finally
            {
                treeFeeds.EndUpdate();
            }
        }
 
        private static  void WalkdownThenRefreshFontColor(TreeFeedsNodeBase startNode)
        {
            if (startNode == null) return;
            if (startNode.UnreadCount > 0)
            {
                FontColorHelper.CopyFromFont(startNode.Override.NodeAppearance.FontData, FontColorHelper.UnreadFont);
                startNode.ForeColor = FontColorHelper.UnreadColor;
            }
            else if (startNode.HighlightCount > 0)
            {
                FontColorHelper.CopyFromFont(startNode.Override.NodeAppearance.FontData, FontColorHelper.HighlightFont);
                startNode.ForeColor = FontColorHelper.HighlightColor;
            }
            else if (startNode.ItemsWithNewCommentsCount > 0)
            {
                FontColorHelper.CopyFromFont(startNode.Override.NodeAppearance.FontData, FontColorHelper.NewCommentsFont);
                startNode.ForeColor = FontColorHelper.NewCommentsColor;
            }
            else
            {
                FontColorHelper.CopyFromFont(startNode.Override.NodeAppearance.FontData, FontColorHelper.NormalFont);
                startNode.ForeColor = FontColorHelper.NormalColor;
            }
            for (TreeFeedsNodeBase child = startNode.FirstNode; child != null; child = child.NextNode)
            {
                WalkdownThenRefreshFontColor(child);
            }
        }
 
        private  void ResetListViewFontAndColor()
        {
            listFeedItems.BeginUpdate();
            listFeedItems.Font = FontColorHelper.NormalFont;
            listFeedItems.ForeColor = FontColorHelper.NormalColor;
            for (int i = 0; i < listFeedItems.Items.Count; i++)
            {
                ThreadedListViewItem lvi = listFeedItems.Items[i];
                ApplyStyles(lvi);
            }
            listFeedItems.EndUpdate();
        }
 
        private  void ResetListViewOutlookFontAndColor()
        {
            listFeedItemsO.BeginUpdate();
            listFeedItemsO.Font = FontColorHelper.NormalFont;
            int hh = (int) listFeedItemsO.CreateGraphics().MeasureString("W", listFeedItemsO.Font).Height;
            int hh2 = 2 + hh + 3 + hh + 2;
            listFeedItemsO.Override.ItemHeight = hh2%2 == 0 ? hh2 + 1 : hh2;
            listFeedItemsO.ColumnSettings.ColumnSets[0].Columns[0].LayoutInfo.PreferredCellSize =
                new Size(listFeedItemsO.Width, listFeedItemsO.Override.ItemHeight);
            hh2 = 5 + hh;
            UltraTreeExtended.COMMENT_HEIGHT = hh2%2 == 0 ? hh2 + 1 : hh2;
            hh2 = hh + 9;
            UltraTreeExtended.DATETIME_GROUP_HEIGHT = hh2%2 == 0 ? hh2 + 1 : hh2;
            for (int i = 0; i < listFeedItemsO.Nodes.Count; i++)
            {
                listFeedItemsO.Nodes[i].Override.ItemHeight = UltraTreeExtended.DATETIME_GROUP_HEIGHT;
                for (int j = 0; j < listFeedItemsO.Nodes[i].Nodes.Count; j++)
                {
                    for (int k = 0; k < listFeedItemsO.Nodes[i].Nodes[j].Nodes.Count; k++)
                    {
                        listFeedItemsO.Nodes[i].Nodes[j].Nodes[k].Override.ItemHeight = UltraTreeExtended.COMMENT_HEIGHT;
                    }
                }
            }
            listFeedItemsO.EndUpdate();
        }
 
        public  void CmdExecuteSearchEngine(ICommand sender)
        {
            if (sender is AppButtonToolCommand)
            {
                AppButtonToolCommand cmd = (AppButtonToolCommand) sender;
                SearchEngine se = (SearchEngine) cmd.Tag;
                StartSearch(se);
            }
        }
 
        public  void CmdSearchGo(ICommand sender)
        {
            StartSearch(null);
        }
 
        public  void StartSearch(SearchEngine thisEngine)
        {
            string phrase = WebSearchText;
            if (phrase.Length > 0)
            {
                if (!SearchComboBox.Items.Contains(phrase))
                    SearchComboBox.Items.Add(phrase);
                if (thisEngine != null)
                {
                    string s = thisEngine.SearchLink;
                    if (s != null && s.Length > 0)
                    {
                        try
                        {
                            s = String.Format(new UrlFormatter(), s, phrase);
                        }
                        catch (Exception fmtEx)
                        {
                            _log.Error("Invalid search phrase placeholder, or no placeholder '{0}'", fmtEx);
                            return;
                        }
                        if (thisEngine.ReturnRssResult)
                        {
                            owner.BackgroundDiscoverFeedsHandler.Add(
                                new DiscoveredFeedsInfo(s, thisEngine.Title + ": '" + phrase + "'", s));
                            AsyncStartRssRemoteSearch(phrase, s, thisEngine.MergeRssResult, true);
                        }
                        else
                        {
                            DetailTabNavigateToUrl(s, thisEngine.Title, owner.SearchEngineHandler.NewTabRequired, true);
                        }
                    }
                }
                else
                {
                    bool isFirstItem = true;
                    int engineCount = 0;
                    foreach (SearchEngine engine in owner.SearchEngineHandler.Engines)
                    {
                        if (engine.IsActive)
                        {
                            engineCount++;
                            string s = engine.SearchLink;
                            if (s != null && s.Length > 0)
                            {
                                try
                                {
                                    s = String.Format(new UrlFormatter(), s, phrase);
                                }
                                catch (Exception fmtEx)
                                {
                                    _log.Error("Invalid search phrase placeholder, or no placeholder '{0}'", fmtEx);
                                    continue;
                                }
                                if (engine.ReturnRssResult)
                                {
                                    owner.BackgroundDiscoverFeedsHandler.Add(
                                        new DiscoveredFeedsInfo(s, engine.Title + ": '" + phrase + "'", s));
                                    AsyncStartRssRemoteSearch(phrase, s, engine.MergeRssResult, isFirstItem);
                                    isFirstItem = false;
                                }
                                else
                                {
                                    DetailTabNavigateToUrl(s, engine.Title, true, engineCount > 1);
                                    Application.DoEvents();
                                }
                            }
                        }
                    }
                }
            }
        }
 
        public  void CmdOpenConfigIdentitiesDialog(ICommand sender)
        {
            IdentityNewsServerManager imng = new IdentityNewsServerManager(owner);
            imng.ShowIdentityDialog(this);
        }
 
        public  void CmdOpenConfigNntpServerDialog(ICommand sender)
        {
            IdentityNewsServerManager imng = new IdentityNewsServerManager(owner);
            imng.ShowNewsServerSubscriptionsDialog(this);
        }
 
        public  TreeFeedsNodeBase CurrentSelectedFeedsNode
        {
            get
            {
                if (_currentSelectedFeedsNode != null)
                    return _currentSelectedFeedsNode;
                return TreeSelectedFeedsNode;
            }
            set
            {
                _currentSelectedFeedsNode = value;
            }
        }
 
        public  TreeFeedsNodeBase TreeSelectedFeedsNode
        {
            get
            {
                if (treeFeeds.SelectedNodes.Count == 0)
                    return null;
                else
                    return (TreeFeedsNodeBase) treeFeeds.SelectedNodes[0];
            }
            set
            {
                if (value.Control != null)
                {
                    listFeedItems.CheckForLayoutModifications();
                    treeFeeds.BeforeSelect -= OnTreeFeedBeforeSelect;
                    treeFeeds.AfterSelect -= OnTreeFeedAfterSelect;
                    treeFeeds.SelectedNodes.Clear();
                    value.Selected = true;
                    value.Control.ActiveNode = value;
                    treeFeeds.BeforeSelect += OnTreeFeedBeforeSelect;
                    treeFeeds.AfterSelect += OnTreeFeedAfterSelect;
                    listFeedItems.FeedColumnLayout = GetFeedColumnLayout(value);
                }
            }
        }
 
        public  int NumSelectedListViewItems
        {
            get
            {
                return listFeedItems.SelectedItems.Count;
            }
        }
 
        public  ImageList TreeImageList
        {
            get
            {
                return _treeImages;
            }
        }
 
        public  NewsItem CurrentSelectedFeedItem
        {
            get
            {
                if (_currentNewsItem != null)
                    return _currentNewsItem;
                if (listFeedItems.Visible && (listFeedItems.SelectedItems.Count > 0))
                {
                    return (NewsItem) ((ThreadedListViewItem) listFeedItems.SelectedItems[0]).Key;
                }
                if (listFeedItemsO.Visible && (listFeedItemsO.SelectedItems.Count > 0))
                {
                    return (NewsItem) ((ThreadedListViewItem) listFeedItemsO.SelectedItems[0]).Key;
                }
                return null;
            }
            set
            {
                _currentNewsItem = value;
            }
        }
 
        internal  Timer ResultDispatcher
        {
            get
            {
                return _timerDispatchResultsToUI;
            }
        }
 
        internal  bool ShutdownInProgress
        {
            get
            {
                return _forceShutdown;
            }
        }
 
        private static  string CurrentToolbarsVersion
        {
            get
            {
                return
                    String.Format("{0}.{1}", StateSerializationHelper.InfragisticsToolbarVersion,
                                  _currentToolbarsVersion);
            }
        }
 
        private static  string CurrentExplorerBarVersion
        {
            get
            {
                return
                    String.Format("{0}.{1}", StateSerializationHelper.InfragisticsExplorerBarVersion,
                                  _currentExplorerBarVersion);
            }
        }
 
        private  ComboBox _urlComboBox = null;
 
        internal  ComboBox UrlComboBox
        {
            get
            {
                if (_urlComboBox == null)
                {
                    Debug.Assert(false, "UrlComboBox control not yet initialized (by ToolbarHelper)");
                }
                return _urlComboBox;
            }
            set
            {
                _urlComboBox = value;
            }
        }
 
        private  ComboBox _searchComboBox = null;
 
        internal  ComboBox SearchComboBox
        {
            get
            {
                if (_searchComboBox == null)
                {
                    Debug.Assert(false, "SearchComboBox control not yet initialized (by ToolbarHelper)");
                }
                return _searchComboBox;
            }
            set
            {
                _searchComboBox = value;
            }
        }
 
        internal  void SetTitleText(string newTitle)
        {
            if (newTitle != null && newTitle.Trim().Length != 0)
                Text = RssBanditApplication.CaptionOnly + " - " + newTitle;
            else
                Text = RssBanditApplication.CaptionOnly;
            if (0 != (owner.InternetConnectionState & INetState.Offline))
                Text += " " + SR.MenuAppInternetConnectionModeOffline;
        }
 
        internal  void SetDetailHeaderText(TreeFeedsNodeBase node)
        {
            if (node != null && ! string.IsNullOrEmpty(node.Text))
            {
                if (node.UnreadCount > 0)
                    detailHeaderCaption.Text = String.Format("{0} ({1})", node.Text, node.UnreadCount);
                else
                    detailHeaderCaption.Text = node.Text;
                detailHeaderCaption.Appearance.Image = node.ImageResolved;
            }
            else
            {
                detailHeaderCaption.Text = SR.DetailHeaderCaptionWelcome;
                detailHeaderCaption.Appearance.Image = null;
            }
        }
 
        protected  void AddUrlToHistory(string newUrl)
        {
            if (!newUrl.Equals("about:blank"))
            {
                UrlComboBox.Items.Remove(newUrl);
                UrlComboBox.Items.Insert(0, newUrl);
            }
        }
 
        internal  TreeFeedsNodeBase GetRoot(RootFolderType rootFolder)
        {
            if (treeFeeds.Nodes.Count > 0)
            {
                return _roots[(int) rootFolder];
            }
            return null;
        }
 
        internal  RootFolderType GetRoot(TreeFeedsNodeBase feedsNode)
        {
            if (feedsNode == null)
                throw new ArgumentNullException("feedsNode");
            if (feedsNode.Type == FeedNodeType.Root || feedsNode.Parent == null)
            {
                for (int i = 0; i < _roots.GetLength(0); i++)
                {
                    if (feedsNode == _roots[i])
                        return (RootFolderType) i;
                }
            }
            else if (feedsNode.Parent != null)
            {
                return GetRoot(feedsNode.Parent);
            }
            return RootFolderType.MyFeeds;
        }
 
        protected  TreeFeedsNodeBase CurrentDragNode
        {
            get
            {
                return _currentDragFeedsNode;
            }
            set
            {
                _currentDragFeedsNode = value;
            }
        }
 
        protected  TreeFeedsNodeBase CurrentDragHighlightNode
        {
            get
            {
                return _currentDragHighlightFeedsNode;
            }
            set
            {
                if (_currentDragHighlightFeedsNode != null && _currentDragHighlightFeedsNode != value)
                {
                    _currentDragHighlightFeedsNode.Override.NodeAppearance.ResetBackColor();
                    _currentDragHighlightFeedsNode.Override.NodeAppearance.ResetForeColor();
                }
                _currentDragHighlightFeedsNode = value;
                if (_currentDragHighlightFeedsNode != null)
                {
                    _currentDragHighlightFeedsNode.Override.NodeAppearance.BackColor = SystemColors.Highlight;
                    _currentDragHighlightFeedsNode.Override.NodeAppearance.ForeColor = SystemColors.HighlightText;
                }
            }
        }
 
        private static  void SetFocus2WebBrowser(HtmlControl theBrowser)
        {
            if (theBrowser == null)
                return;
            theBrowser.Focus();
        }
 
           
           
           
        private  bool FindNextUnreadItem(TreeFeedsNodeBase tn)
        {
            INewsFeed f = null;
            bool repopulated = false, isTopLevel = true;
            ListViewItem foundLVItem = null;
            if (tn.Type == FeedNodeType.Feed)
                f = owner.GetFeed(tn.DataKey);
            bool containsUnread = ((f != null && f.containsNewMessages) ||
                                   (tn == TreeSelectedFeedsNode && tn.UnreadCount > 0));
            if (containsUnread)
            {
                if (tn != TreeSelectedFeedsNode && f != null)
                {
                    containsUnread = false;
                    IList<NewsItem> items = owner.FeedHandler.GetCachedItemsForFeed(f.link);
                    for (int i = 0; i < items.Count; i++)
                    {
                        NewsItem item = items[i];
                        if (!item.BeenRead)
                        {
                            containsUnread = true;
                            break;
                        }
                    }
                    if (containsUnread)
                    {
                        TreeFeedsNodeBase tnSelected = TreeSelectedFeedsNode;
                        if (tnSelected == null)
                            tnSelected = GetRoot(RootFolderType.MyFeeds);
                        if (tnSelected.Type == FeedNodeType.SmartFolder || tnSelected.Type == FeedNodeType.Finder ||
                            tnSelected.Type == FeedNodeType.Root ||
                            (tnSelected != tn && tnSelected.Type == FeedNodeType.Feed) ||
                            (tnSelected.Type == FeedNodeType.Category && !NodeIsChildOf(tn, tnSelected)))
                        {
                            TreeSelectedFeedsNode = tn;
                            CurrentSelectedFeedsNode = null;
                            PopulateListView(tn, items, true);
                            repopulated = true;
                        }
                    }
                    else
                    {
                        f.containsNewMessages = false;
                    }
                }
            }
            for (int i = 0; i < listFeedItems.SelectedItems.Count; i++)
            {
                ThreadedListViewItem tlvi = (ThreadedListViewItem) listFeedItems.SelectedItems[i];
                if ((tlvi != null) && (tlvi.IndentLevel != 0))
                {
                    isTopLevel = false;
                    break;
                }
            }
            if ((!isTopLevel) || containsUnread)
            {
                foundLVItem = FindUnreadListViewItem();
            }
            if (foundLVItem != null)
            {
                MoveFeedDetailsToFront();
                listFeedItems.BeginUpdate();
                listFeedItems.SelectedItems.Clear();
                foundLVItem.Selected = true;
                foundLVItem.Focused = true;
                htmlDetail.Activate();
                OnFeedListItemActivate(null, EventArgs.Empty);
                SetTitleText(tn.Text);
                SetDetailHeaderText(tn);
                foundLVItem.Selected = true;
                foundLVItem.Focused = true;
                listFeedItems.Focus();
                listFeedItems.EnsureVisible(foundLVItem.Index);
                listFeedItems.EndUpdate();
                if (TreeSelectedFeedsNode != tn && repopulated)
                {
                    SelectNode(tn);
                }
                return true;
            }
            return false;
        }
 
        private  ThreadedListViewItem FindUnreadListViewItem()
        {
            bool inComments = false;
            for (int i = 0; i < listFeedItems.SelectedItems.Count; i++)
            {
                ThreadedListViewItem tlvi = (ThreadedListViewItem) listFeedItems.SelectedItems[i];
                if ((tlvi != null) && (tlvi.IsComment))
                {
                    inComments = true;
                    break;
                }
            }
            if (listFeedItems.Items.Count == 0)
                return null;
            NewsItem compareItem = null;
            ThreadedListViewItem foundLVItem = null;
            int pos = 0, incrementor = 1;
            if ((!inComments) && (listFeedItems.SortManager.SortOrder == SortOrder.Descending))
            {
                pos = listFeedItems.Items.Count - 1;
                incrementor = -1;
            }
            while (pos >= 0 && pos < listFeedItems.Items.Count)
            {
                ThreadedListViewItem lvi = listFeedItems.Items[pos];
                NewsItem item = lvi.Key as NewsItem;
                if (item != null && !item.BeenRead)
                {
                    if (compareItem == null) compareItem = item;
                    if (foundLVItem == null) foundLVItem = lvi;
                    if (!(listFeedItems.SortManager.GetComparer() is ThreadedListViewDateTimeItemComparer))
                    {
                        if (DateTime.Compare(item.Date, compareItem.Date) < 0)
                        {
                            compareItem = item;
                            foundLVItem = lvi;
                        }
                    }
                    else
                    {
                        foundLVItem = lvi;
                        break;
                    }
                }
                pos += incrementor;
            }
            return foundLVItem;
        }
 
        private  void SelectNode(TreeFeedsNodeBase feedsNode)
        {
            TreeSelectedFeedsNode = feedsNode;
            feedsNode.BringIntoView();
            if (feedsNode.Parent != null) feedsNode.Parent.BringIntoView();
        }
 
        private static  TreeFeedsNodeBase NextNearFeedNode(TreeFeedsNodeBase startNode, bool ignoreStartNode)
        {
            TreeFeedsNodeBase found = null;
            if (!ignoreStartNode)
            {
                if (startNode.Type == FeedNodeType.Feed) return startNode;
            }
            for (TreeFeedsNodeBase sibling = startNode.FirstNode;
                 sibling != null && found == null;
                 sibling = sibling.NextNode)
            {
                if (sibling.Type == FeedNodeType.Feed) return sibling;
                if (sibling.FirstNode != null)
                    found = NextNearFeedNode(sibling.FirstNode, false);
            }
            if (found != null) return found;
            for (TreeFeedsNodeBase sibling = (ignoreStartNode ? startNode.NextNode : startNode.FirstNode);
                 sibling != null && found == null;
                 sibling = sibling.NextNode)
            {
                if (sibling.Type == FeedNodeType.Feed) return sibling;
                if (sibling.FirstNode != null)
                    found = NextNearFeedNode(sibling.FirstNode, false);
            }
            if (found != null) return found;
            if (startNode.Parent == null) return null;
            for (startNode = startNode.Parent;
                 startNode != null && startNode.NextNode == null;
                 startNode = startNode.Parent)
            {
            }
            if (startNode == null) return null;
            for (TreeFeedsNodeBase parentSibling = startNode.NextNode;
                 parentSibling != null && found == null;
                 parentSibling = parentSibling.NextNode)
            {
                if (parentSibling.Type == FeedNodeType.Feed) return parentSibling;
                if (parentSibling.FirstNode != null)
                    found = NextNearFeedNode(parentSibling.FirstNode, false);
            }
            return found;
        }
 
        public  void MoveToNextUnreadItem()
        {
            TreeFeedsNodeBase startNode = null, foundFeedsNode, rootNode = GetRoot(RootFolderType.MyFeeds);
            bool unreadFound = false;
            if (listFeedItems.Items.Count > 0)
            {
                startNode = TreeSelectedFeedsNode;
                if (startNode != null && startNode.UnreadCount > 0)
                {
                    unreadFound = FindNextUnreadItem(startNode);
                    if (!unreadFound)
                    {
                        startNode = null;
                    }
                    else
                    {
                        return;
                    }
                }
            }
            if (startNode == null)
                startNode = CurrentSelectedFeedsNode;
            if (startNode != null && !NodeIsChildOf(startNode, rootNode))
                startNode = null;
            if (startNode == null)
                startNode = rootNode;
            if (startNode.Type == FeedNodeType.Feed)
            {
                MoveFeedDetailsToFront();
                if (FindNextUnreadItem(startNode))
                {
                    unreadFound = true;
                }
            }
            if (!unreadFound)
            {
                foundFeedsNode = NextNearFeedNode(startNode, true);
                while (foundFeedsNode != null && !unreadFound)
                {
                    if (FindNextUnreadItem(foundFeedsNode))
                    {
                        unreadFound = true;
                    }
                    foundFeedsNode = NextNearFeedNode(foundFeedsNode, true);
                }
            }
            if (!unreadFound && startNode != GetRoot(RootFolderType.MyFeeds))
            {
                foundFeedsNode = NextNearFeedNode(GetRoot(RootFolderType.MyFeeds), true);
                while (foundFeedsNode != null && !unreadFound)
                {
                    if (FindNextUnreadItem(foundFeedsNode))
                    {
                        unreadFound = true;
                    }
                    foundFeedsNode = NextNearFeedNode(foundFeedsNode, true);
                }
            }
            if (!unreadFound)
            {
                if (owner.StateHandler.NewsHandlerState == NewsHandlerState.Idle)
                    SetGuiStateFeedback(SR.GUIStatusNoUnreadFeedItemsLeft, ApplicationTrayState.NormalIdle);
            }
        }
 
        private static  string BoundsToString(Rectangle b)
        {
            return string.Format("{0};{1};{2};{3}", b.X, b.Y, b.Width, b.Height);
        }
 
        private static  Rectangle StringToBounds(string b)
        {
            string[] ba = b.Split(new char[] {';'});
            Rectangle r = Rectangle.Empty;
            if (ba.GetLength(0) == 4)
            {
                try
                {
                    r = new Rectangle(Int32.Parse(ba[0]), Int32.Parse(ba[1]), Int32.Parse(ba[2]), Int32.Parse(ba[3]));
                }
                catch
                {
                }
            }
            return r;
        }
 
        public  void PopulateSmartFolder(TreeFeedsNodeBase feedNode, bool updateGui)
        {
            IList<NewsItem> items;
            ISmartFolder isFolder = feedNode as ISmartFolder;
            if (isFolder == null)
                return;
            items = isFolder.Items;
            if (updateGui || TreeSelectedFeedsNode == feedNode)
            {
                NewsItem itemSelected = null;
                if (listFeedItems.SelectedItems.Count > 0)
                    itemSelected = (NewsItem) ((ThreadedListViewItem) listFeedItems.SelectedItems[0]).Key;
                InvokeOnGuiSync(delegate
                {
                    PopulateListView(feedNode, items, true, false, feedNode);
                });
                if (updateGui)
                {
                    htmlDetail.Clear();
                    if (itemSelected == null || listFeedItems.Items.Count == 0)
                    {
                        CurrentSelectedFeedItem = null;
                    }
                    else
                        ReSelectListViewItem(itemSelected);
                }
            }
        }
 
        public  void PopulateFinderNode(FinderNode node, bool updateGui)
        {
            if (node == null)
                return;
            if (updateGui || TreeSelectedFeedsNode == node)
            {
                NewsItem itemSelected = null;
                if (listFeedItems.SelectedItems.Count > 0)
                    itemSelected = (NewsItem) ((ThreadedListViewItem) listFeedItems.SelectedItems[0]).Key;
                node.AnyUnread = false;
                node.Clear();
                if (!string.IsNullOrEmpty(node.Finder.ExternalSearchUrl))
                {
                    StartRssRemoteSearch(node.Finder.ExternalSearchUrl, node);
                }
                else
                {
                    AsyncStartNewsSearch(node);
                }
                IList<NewsItem> items = node.Items;
                InvokeOnGuiSync(delegate
                {
                    PopulateListView(node, items, true, true, node);
                });
                if (updateGui)
                {
                    if (itemSelected != null)
                    {
                        CurrentSelectedFeedItem = null;
                        htmlDetail.Clear();
                    }
                    else
                        ReSelectListViewItem(itemSelected);
                }
            }
        }
 
        private  ThreadedListViewItem CreateThreadedLVItem(NewsItem newsItem, bool hasChilds, int imgOffset,
                                                          ColumnKeyIndexMap colIndex, bool authorInTopicColumn)
        {
            string[] lvItems = new string[colIndex.Count];
            foreach (string colKey in colIndex.Keys)
            {
                lvItems[colIndex[colKey]] = String.Empty;
                switch ((NewsItemSortField) Enum.Parse(typeof (NewsItemSortField), colKey, true))
                {
                    case NewsItemSortField.Title:
                        lvItems[colIndex[colKey]] = StringHelper.ShortenByEllipsis(newsItem.Title, MaxHeadlineWidth);
                        break;
                    case NewsItemSortField.Subject:
                        if (authorInTopicColumn && !colIndex.ContainsKey("Author"))
                        {
                            lvItems[colIndex[colKey]] = newsItem.Author;
                        }
                        else
                        {
                            lvItems[colIndex[colKey]] = newsItem.Subject;
                        }
                        break;
                    case NewsItemSortField.FeedTitle:
                        INewsFeed f = newsItem.Feed;
                        string feedUrl = GetOriginalFeedUrl(newsItem);
                        if ((feedUrl != null) && owner.FeedHandler.FeedsTable.ContainsKey(feedUrl))
                        {
                            f = owner.FeedHandler.FeedsTable[feedUrl];
                        }
                        lvItems[colIndex[colKey]] = HtmlHelper.HtmlDecode(f.title);
                        break;
                    case NewsItemSortField.Author:
                        lvItems[colIndex[colKey]] = newsItem.Author;
                        break;
                    case NewsItemSortField.Date:
                        lvItems[colIndex[colKey]] = newsItem.Date.ToLocalTime().ToString();
                        break;
                    case NewsItemSortField.CommentCount:
                        if (newsItem.CommentCount != NewsItem.NoComments)
                            lvItems[colIndex[colKey]] = newsItem.CommentCount.ToString();
                        break;
                    case NewsItemSortField.Enclosure:
                        if (null != newsItem.Enclosures && newsItem.Enclosures.Count > 0)
                            lvItems[colIndex[colKey]] = newsItem.Enclosures.Count.ToString();
                        break;
                    case NewsItemSortField.Flag:
                        if (newsItem.FlagStatus != Flagged.None)
                            lvItems[colIndex[colKey]] = newsItem.FlagStatus.ToString();
                        break;
                    default:
                        Trace.Assert(false, "CreateThreadedLVItem::NewsItemSortField NOT handled: " + colKey);
                        break;
                }
            }
            ThreadedListViewItem lvi = new ThreadedListViewItem(newsItem, lvItems);
            if (!newsItem.BeenRead)
                imgOffset++;
            lvi.ImageIndex = imgOffset;
            ApplyStyles(lvi, newsItem.BeenRead, newsItem.HasNewComments);
            lvi.HasChilds = hasChilds;
            lvi.IsComment = authorInTopicColumn;
            return lvi;
        }
 
        private  void ApplyNewsItemPropertyImages(IEnumerable<ThreadedListViewItem> items)
        {
            ColumnKeyIndexMap indexMap = listFeedItems.Columns.GetColumnIndexMap();
            bool applyFlags = indexMap.ContainsKey(NewsItemSortField.Flag.ToString());
            bool applyAttachments = indexMap.ContainsKey(NewsItemSortField.Enclosure.ToString());
            if (!applyFlags && !applyAttachments)
                return;
            foreach (ThreadedListViewItem lvi in items)
            {
                NewsItem item = lvi.Key as NewsItem;
                if (item == null) continue;
                if (applyFlags && item.FlagStatus != Flagged.None)
                    ApplyFlagStateTo(lvi, item.FlagStatus, indexMap);
                if (applyAttachments && item.Enclosures != null && item.Enclosures.Count > 0)
                    ApplyAttachmentImageTo(lvi, item.Enclosures.Count, indexMap);
            }
        }
 
        private static  void ApplyAttachmentImageTo(ThreadedListViewItem lvi, int attachemtCount, ColumnKeyIndexMap indexMap)
        {
            if (lvi == null || lvi.ListView == null)
                return;
            string key = NewsItemSortField.Enclosure.ToString();
            if (! indexMap.ContainsKey(key))
                return;
            string text = (attachemtCount > 0 ? attachemtCount.ToString() : String.Empty);
            if (indexMap[key] > 0)
            {
                lvi.SubItems[indexMap[key]].Text = text;
                if (attachemtCount > 0)
                    lvi.SetSubItemImage(indexMap[key], Resource.NewsItemRelatedImage.Attachment);
            }
            else
            {
                lvi.SubItems[indexMap[key]].Text = text;
            }
        }
 
        private static  void ApplyFlagStateTo(ThreadedListViewItem lvi, Flagged flagStatus, ColumnKeyIndexMap indexMap)
        {
            if (lvi == null || lvi.ListView == null)
                return;
            string key = NewsItemSortField.Flag.ToString();
            if (! indexMap.ContainsKey(key))
                return;
            int imgIndex = -1;
            Color bkColor = lvi.BackColor;
            string text = flagStatus.ToString();
            switch (flagStatus)
            {
                case Flagged.Complete:
                    imgIndex = Resource.FlagImage.Complete;
                    break;
                case Flagged.FollowUp:
                    imgIndex = Resource.FlagImage.Red;
                    bkColor = Resource.ItemFlagBackground.Red;
                    break;
                case Flagged.Forward:
                    imgIndex = Resource.FlagImage.Blue;
                    bkColor = Resource.ItemFlagBackground.Blue;
                    break;
                case Flagged.Read:
                    imgIndex = Resource.FlagImage.Green;
                    bkColor = Resource.ItemFlagBackground.Green;
                    break;
                case Flagged.Review:
                    imgIndex = Resource.FlagImage.Yellow;
                    bkColor = Resource.ItemFlagBackground.Yellow;
                    break;
                case Flagged.Reply:
                    imgIndex = Resource.FlagImage.Purple;
                    bkColor = Resource.ItemFlagBackground.Purple;
                    break;
                case Flagged.None:
                    text = String.Empty;
                    break;
            }
            if (indexMap[key] > 0)
            {
                lvi.SubItems[indexMap[key]].Text = text;
                lvi.SetSubItemImage(indexMap[key], imgIndex);
                lvi.SubItems[indexMap[key]].BackColor = bkColor;
            }
            else
            {
                lvi.SubItems[indexMap[key]].Text = text;
            }
        }
 
        private static  ThreadedListViewItem CreateThreadedLVItemInfo(string infoMessage, bool isError)
        {
            ThreadedListViewItem lvi = new ThreadedListViewItemPlaceHolder(infoMessage);
            if (isError)
            {
                lvi.Font = FontColorHelper.FailureFont;
                lvi.ForeColor = FontColorHelper.FailureColor;
                lvi.ImageIndex = Resource.NewsItemRelatedImage.Failure;
            }
            else
            {
                lvi.Font = FontColorHelper.NormalFont;
                lvi.ForeColor = FontColorHelper.NormalColor;
            }
            lvi.HasChilds = false;
            return lvi;
        }
 
        private  void PopulateListView(TreeFeedsNodeBase associatedFeedsNode, IList<NewsItem> list, bool forceReload)
        {
            PopulateListView(associatedFeedsNode, list, forceReload, false, associatedFeedsNode);
        }
 
        private  void PopulateListView(TreeFeedsNodeBase associatedFeedsNode, IList<NewsItem> list, bool forceReload,
                                      bool categorizedView, TreeFeedsNodeBase initialFeedsNode)
        {
            try
            {
                lock (listFeedItems.Items)
                {
                    if (TreeSelectedFeedsNode != initialFeedsNode)
                    {
                        return;
                    }
                }
                IList<NewsItem> unread;
                lock (listFeedItems.Items)
                {
                    if (TreeSelectedFeedsNode != initialFeedsNode)
                    {
                        return;
                    }
                    if (initialFeedsNode.Type == FeedNodeType.Category)
                    {
                        if (NodeIsChildOf(associatedFeedsNode, initialFeedsNode))
                        {
                            if (forceReload)
                            {
                                EmptyListView();
                                feedsCurrentlyPopulated.Clear();
                            }
                            bool checkForDuplicates = feedsCurrentlyPopulated.ContainsKey(associatedFeedsNode.DataKey);
                            unread = PopulateSmartListView(list, categorizedView, checkForDuplicates);
                            if (!checkForDuplicates)
                                feedsCurrentlyPopulated.Add(associatedFeedsNode.DataKey, null);
                            if (unread.Count != associatedFeedsNode.UnreadCount)
                                UpdateTreeNodeUnreadStatus(associatedFeedsNode, unread.Count);
                        }
                        else if (associatedFeedsNode == initialFeedsNode)
                        {
                            feedsCurrentlyPopulated.Clear();
                            PopulateFullListView(list);
                            if (associatedFeedsNode.DataKey != null)
                                feedsCurrentlyPopulated.Add(associatedFeedsNode.DataKey, null);
                        }
                    }
                    else if (TreeSelectedFeedsNode is UnreadItemsNode)
                    {
                        if (forceReload)
                        {
                            EmptyListView();
                        }
                        PopulateSmartListView(list, categorizedView, true);
                    }
                    else if (TreeSelectedFeedsNode == associatedFeedsNode)
                    {
                        if (forceReload)
                        {
                            unread = PopulateFullListView(list);
                            if (unread.Count != associatedFeedsNode.UnreadCount)
                                UpdateTreeNodeUnreadStatus(associatedFeedsNode, unread.Count);
                        }
                        else
                        {
                            unread = PopulateSmartListView(list, categorizedView, true);
                            if (unread.Count > 0)
                            {
                                int unreadItems = unread.Count;
                                if (categorizedView)
                                    unreadItems += associatedFeedsNode.UnreadCount;
                                UpdateTreeNodeUnreadStatus(associatedFeedsNode, unreadItems);
                            }
                        }
                    }
                }
                SetGuiStateFeedback(SR.StatisticsItemsDisplayedMessage(listFeedItems.Items.Count));
            }
            catch (Exception ex)
            {
                _log.Error("PopulateListView() failed.", ex);
            }
        }
 
        public  void AsyncPopulateListView(TreeFeedsNodeBase associatedFeedsNode, IList<NewsItem> list, bool forceReload,
                                          bool categorizedView, TreeFeedsNodeBase initialFeedsNode)
        {
            InvokeOnGui(delegate
            {
                PopulateListView(associatedFeedsNode, list, forceReload, categorizedView, initialFeedsNode);
            });
        }
 
        private  IList<NewsItem> PopulateFullListView(IList<NewsItem> list)
        {
            ThreadedListViewItem[] aNew = new ThreadedListViewItem[list.Count];
            List<NewsItem> unread = new List<NewsItem>(list.Count);
            ColumnKeyIndexMap colIndex = listFeedItems.Columns.GetColumnIndexMap();
            INewsItemFilter flagFilter = null;
            if (CurrentSelectedFeedsNode is FlaggedItemsNode)
            {
                flagFilter = _filterManager["NewsItemFlagFilter"];
                _filterManager.Remove("NewsItemFlagFilter");
            }
            EmptyListView();
            listFeedItems.BeginUpdate();
            try
            {
                for (int i = 0; i < list.Count; i++)
                {
                    NewsItem item = list[i];
                    if (!item.BeenRead)
                        unread.Add(item);
                    bool hasRelations;
                    hasRelations = NewsItemHasRelations(item);
                    ThreadedListViewItem newItem = CreateThreadedLVItem(item, hasRelations, Resource.NewsItemImage.DefaultRead, colIndex, false);
                    _filterManager.Apply(newItem);
                    aNew[i] = newItem;
                }
                Array.Sort(aNew, listFeedItems.SortManager.GetComparer());
                listFeedItems.Items.AddRange(aNew);
                ApplyNewsItemPropertyImages(aNew);
                if (listFeedItemsO.Visible)
                {
                    listFeedItemsO.AddRange(aNew);
                }
                return unread;
            }
            catch (Exception ex)
            {
                _log.Error("PopulateFullListView exception", ex);
                return unread;
            }
            finally
            {
                listFeedItems.EndUpdate();
                if (flagFilter != null)
                {
                    _filterManager.Add("NewsItemFlagFilter", flagFilter);
                }
            }
        }
 
        public  IList<NewsItem> PopulateSmartListView(IList<NewsItem> list, bool categorizedView, bool checkDuplicates)
        {
            List<ThreadedListViewItem> items = new List<ThreadedListViewItem>(listFeedItems.Items.Count);
            List<ThreadedListViewItem> newItems = new List<ThreadedListViewItem>(list.Count);
            List<NewsItem> unread = new List<NewsItem>(list.Count);
            lock (listFeedItems.Items)
            {
                items.AddRange(listFeedItems.Items);
            }
            ColumnKeyIndexMap colIndexes = listFeedItems.Columns.GetColumnIndexMap();
            try
            {
                for (int i = 0; i < list.Count; i++)
                {
                    NewsItem item = list[i];
                    bool hasRelations = NewsItemHasRelations(item);
                    bool isDuplicate = false;
                    ThreadedListViewItem tlvi = null;
                    if (checkDuplicates)
                    {
                        for (int j = 0; j < items.Count; j++)
                        {
                            tlvi = items[j];
                            if (item.Equals(tlvi.Key) && tlvi.IndentLevel == 0)
                            {
                                tlvi.Key = item;
                                isDuplicate = true;
                                break;
                            }
                        }
                    }
                    if (isDuplicate)
                    {
                        if (!tlvi.HasChilds && hasRelations)
                            tlvi.HasChilds = hasRelations;
                        ApplyStyles(tlvi);
                    }
                    else
                    {
                        ThreadedListViewItem newItem = CreateThreadedLVItem(item, hasRelations, Resource.NewsItemImage.DefaultRead, colIndexes,
                                                                            false);
                        _filterManager.Apply(newItem);
                        newItems.Add(newItem);
                    }
                    if (!item.BeenRead)
                        unread.Add(item);
                }
                if (newItems.Count > 0)
                {
                    try
                    {
                        listFeedItems.BeginUpdate();
                        lock (listFeedItems.Items)
                        {
                            ThreadedListViewItem[] a = new ThreadedListViewItem[newItems.Count];
                            newItems.CopyTo(a);
                            listFeedItems.ListViewItemSorter = listFeedItems.SortManager.GetComparer();
                            listFeedItems.Items.AddRange(a);
                            if (listFeedItemsO.Visible)
                                listFeedItemsO.AddRange(a);
                            ApplyNewsItemPropertyImages(a);
                            listFeedItems.ListViewItemSorter = null;
                            if (listFeedItems.SelectedItems.Count > 0)
                            {
                                listFeedItems.EnsureVisible(listFeedItems.SelectedItems[0].Index);
                                if (listFeedItemsO.Visible)
                                    listFeedItemsO.GetFromLVI((ThreadedListViewItem) listFeedItems.SelectedItems[0]).
                                        BringIntoView();
                            }
                        }
                    }
                    finally
                    {
                        listFeedItems.EndUpdate();
                    }
                }
            }
            catch (Exception ex)
            {
                _log.Error("PopulateSmartListView exception", ex);
            }
            return unread;
        }
 
        private  bool NewsItemHasRelations(NewsItem item)
        {
            return NewsItemHasRelations(item, new NewsItem[] {});
        }
 
        private  bool NewsItemHasRelations(NewsItem item, IList<NewsItem> itemKeyPath)
        {
            bool hasRelations = false;
            if (item.Feed != null & owner.FeedHandler.FeedsTable.ContainsKey(item.Feed.link))
            {
                hasRelations = owner.FeedHandler.HasItemAnyRelations(item, itemKeyPath);
            }
            if (!hasRelations) hasRelations = (item.HasExternalRelations && owner.InternetAccessAllowed);
            return hasRelations;
        }
 
        public  void BeginLoadCommentFeed(NewsItem item, string ticket, IList<NewsItem> itemKeyPath)
        {
            owner.MakeAndQueueTask(ThreadWorker.Task.LoadCommentFeed,
                                   OnLoadCommentFeedProgress,
                                   item, ticket, itemKeyPath);
        }
 
        private  void OnLoadCommentFeedProgress(object sender, ThreadWorkerProgressArgs args)
        {
            if (args.Exception != null)
            {
                args.Cancel = true;
                ExceptionManager.Publish(args.Exception);
                object[] results = (object[]) args.Result;
                string insertionPointTicket = (string) results[2];
                ThreadedListViewItem[] newChildItems =
                    new ThreadedListViewItem[] {CreateThreadedLVItemInfo(args.Exception.Message, true)};
                listFeedItems.InsertItemsForPlaceHolder(insertionPointTicket, newChildItems, false);
                if (listFeedItemsO.Visible && newChildItems.Length > 0)
                {
                    listFeedItemsO.AddRangeComments(newChildItems[0].Parent, newChildItems);
                }
            }
            else if (!args.Done)
            {
            }
            else if (args.Done)
            {
                object[] results = (object[]) args.Result;
                List<NewsItem> commentItems = (List<NewsItem>) results[0];
                NewsItem item = (NewsItem) results[1];
                string insertionPointTicket = (string) results[2];
                IList<NewsItem> itemKeyPath = (IList<NewsItem>) results[3];
                if (item.CommentCount != commentItems.Count)
                {
                    item.CommentCount = commentItems.Count;
                    owner.FeedWasModified(item.Feed, NewsFeedProperty.FeedItemCommentCount);
                }
                commentItems.Sort(RssHelper.GetComparer(false, NewsItemSortField.Date));
                item.SetExternalRelations(commentItems);
                ThreadedListViewItem[] newChildItems = null;
                if (commentItems.Count > 0)
                {
                    ArrayList newChildItemsArray = new ArrayList(commentItems.Count);
                    ColumnKeyIndexMap colIndex = listFeedItems.Columns.GetColumnIndexMap();
                    for (int i = 0; i < commentItems.Count; i++)
                    {
                        NewsItem o = commentItems[i];
                        if (itemKeyPath != null && itemKeyPath.Contains(o))
                            continue;
                        bool hasRelations = NewsItemHasRelations(o, itemKeyPath);
                        o.BeenRead = tempFeedItemsRead.ContainsKey(RssHelper.GetHashCode(o));
                        ThreadedListViewItem newListItem =
                            CreateThreadedLVItem(o, hasRelations, Resource.NewsItemImage.CommentRead, colIndex, true);
                        _filterManager.Apply(newListItem);
                        newChildItemsArray.Add(newListItem);
                    }
                    if (newChildItemsArray.Count > 0)
                    {
                        newChildItems = new ThreadedListViewItem[newChildItemsArray.Count];
                        newChildItemsArray.CopyTo(newChildItems);
                    }
                }
                listFeedItems.InsertItemsForPlaceHolder(insertionPointTicket, newChildItems, false);
                if (listFeedItemsO.Visible && newChildItems.Length > 0)
                {
                    listFeedItemsO.AddRangeComments(newChildItems[0].Parent, newChildItems);
                }
            }
        }
 
        public  void TriggerGUIStateOnNewFeeds(bool handleNewReceived)
        {
            int unreadFeeds, unreadMessages;
            CountUnread(out unreadFeeds, out unreadMessages);
            if (unreadMessages != 0)
            {
                _timerResetStatus.Stop();
                if (handleNewReceived && unreadMessages > _lastUnreadFeedItemCountBeforeRefresh)
                {
                    string message = SR.GUIStatusNewFeedItemsReceivedMessage(unreadFeeds, unreadMessages);
                    if (Visible)
                    {
                        SetGuiStateFeedback(message, ApplicationTrayState.NewUnreadFeeds);
                    }
                    else
                    {
                        SetGuiStateFeedback(message, ApplicationTrayState.NewUnreadFeedsReceived);
                    }
                    if (owner.Preferences.ShowNewItemsReceivedBalloon &&
                        (SystemTrayOnlyVisible || WindowState == FormWindowState.Minimized))
                    {
                        if (_beSilentOnBalloonPopupCounter <= 0)
                        {
                            message = SR.GUIStatusNewFeedItemsReceivedMessage(
                                unreadFeeds,
                                unreadMessages);
                            _trayAni.ShowBalloon(NotifyIconAnimation.EBalloonIcon.Info, message,
                                                 RssBanditApplication.CaptionOnly + " - " +
                                                 SR.GUIStatusNewFeedItemsReceived);
                        }
                        else
                        {
                            _beSilentOnBalloonPopupCounter--;
                        }
                    }
                }
                else
                {
                    SetGuiStateFeedback(String.Empty, ApplicationTrayState.NewUnreadFeeds);
                }
            }
            else
            {
                SetGuiStateFeedback(String.Empty, ApplicationTrayState.NormalIdle);
            }
        }
 
           
        private  void UpdateCommentStatus(TreeFeedsNodeBase tn, IList<NewsItem> items, bool commentsRead)
        {
            ;
            int multiplier = (commentsRead ? -1 : 1);
            if (commentsRead)
            {
                tn.UpdateCommentStatus(tn, items.Count*multiplier);
                owner.UpdateWatchedItems(items);
            }
            else
            {
                IList<NewsItem> itemsWithNewComments = GetFeedItemsWithNewComments(items);
                tn.UpdateCommentStatus(tn, itemsWithNewComments.Count*multiplier);
                owner.UpdateWatchedItems(itemsWithNewComments);
            }
            WatchedItemsNode.UpdateCommentStatus();
        }
 
        private static  IList<NewsItem> GetFeedItemsWithNewComments(IList<NewsItem> items)
        {
            List<NewsItem> itemsWithNewComments = new List<NewsItem>();
            if (items == null) return itemsWithNewComments;
            if (items.Count == 0) return itemsWithNewComments;
            for (int i = 0; i < items.Count; i++)
            {
                NewsItem item = items[i];
                if (item.HasNewComments) itemsWithNewComments.Add(item);
            }
            return itemsWithNewComments;
        }
 
        private  void UnreadItemsNodeRemoveItems(string feedLink)
        {
            if (string.IsNullOrEmpty(feedLink) ||
                ! owner.FeedHandler.FeedsTable.ContainsKey(feedLink))
                return;
            IList<NewsItem> items = owner.FeedHandler.GetCachedItemsForFeed(feedLink);
            UnreadItemsNodeRemoveItems(FilterUnreadFeedItems(items));
        }
 
           
        private  void UnreadItemsNodeRemoveItems(IList<NewsItem> unread)
        {
            if (unread == null) return;
            for (int i = 0; i < unread.Count; i++)
                UnreadItemsNode.Remove(unread[i]);
            UnreadItemsNode.UpdateReadStatus();
        }
 
           
        private static  IList<NewsItem> FilterUnreadFeedItems(IList<NewsItem> items)
        {
            List<NewsItem> result = new List<NewsItem>();
            if (items == null || items.Count == 0)
                return result;
            for (int i = 0; i < items.Count; i++)
            {
                NewsItem item = items[i];
                if (!item.BeenRead)
                    result.Add(item);
            }
            return result;
        }
 
        private  int CountUnreadFeedItems(NewsFeed f)
        {
            if (f == null) return 0;
            return FilterUnreadFeedItems(f).Count;
        }
 
           
        private  void CountUnread(out int unreadFeeds, out int unreadMessages)
        {
            unreadFeeds = unreadMessages = 0;
            foreach (NewsFeed f in owner.FeedHandler.FeedsTable.Values)
            {
                if (f.containsNewMessages)
                {
                    unreadFeeds++;
                    int urm = CountUnreadFeedItems(f);
                    unreadMessages += urm;
                }
            }
        }
 
        private  void CheckForAddIns()
        {
            owner.CheckAndLoadAddIns();
            IBlogExtension ibe = null;
            try
            {
                blogExtensions = ServiceManager.SearchForIBlogExtensions(RssBanditApplication.GetPlugInPath());
                if (blogExtensions == null || blogExtensions.Count == 0)
                    return;
                _listContextMenu.MenuItems.Add(new MenuItem("-"));
                for (int i = 0; i < blogExtensions.Count; i++)
                {
                    ibe = blogExtensions[i];
                    AppContextMenuCommand m = new AppContextMenuCommand("cmdIBlogExt." + i,
                                                                        owner.Mediator,
                                                                        new ExecuteCommandHandler(
                                                                            owner.CmdGenericListviewCommand),
                                                                        ibe.DisplayName,
                                                                        SR.MenuIBlogExtensionCommandDesc);
                    _listContextMenu.MenuItems.Add(m);
                    if (ibe.HasConfiguration)
                    {
                        AppContextMenuCommand mc = new AppContextMenuCommand("cmdIBlogExtConfig." + i,
                                                                             owner.Mediator,
                                                                             new ExecuteCommandHandler(
                                                                                 owner.CmdGenericListviewCommandConfig),
                                                                             ibe.DisplayName + " - " +
                                                                             SR.MenuConfigCommandCaption,
                                                                             SR.MenuIBlogExtensionConfigCommandDesc);
                        _listContextMenu.MenuItems.Add(mc);
                    }
                }
            }
            catch (Exception ex)
            {
                _log.Fatal(
                    "Failed to load IBlogExtension plugin: " + (ibe == null ? String.Empty : ibe.GetType().FullName), ex);
                ExceptionManager.Publish(ex);
            }
            finally
            {
                ServiceManager.UnloadLoaderAppDomain();
            }
        }
 
        private  void OnFeedTransformed(object sender, ThreadWorkerProgressArgs args)
        {
            if (args.Exception != null)
            {
                args.Cancel = true;
                RssBanditApplication.PublishException(args.Exception);
            }
            else if (!args.Done)
            {
            }
            else if (args.Done)
            {
                object[] results = (object[]) args.Result;
                UltraTreeNode node = (UltraTreeNode) results[0];
                string html = (string) results[1];
                if ((listFeedItems.SelectedItems.Count == 0) && treeFeeds.SelectedNodes.Count > 0 &&
                    ReferenceEquals(treeFeeds.SelectedNodes[0], node))
                {
                    htmlDetail.Html = html;
                    htmlDetail.Navigate(null);
                }
            }
        }
 
        internal  void OnEnclosureReceived(object sender, DownloadItemEventArgs e)
        {
            if (owner.FeedHandler.FeedsTable.ContainsKey(e.DownloadItem.OwnerFeedId))
            {
                INewsFeed f = owner.FeedHandler.FeedsTable[e.DownloadItem.OwnerFeedId];
                if (owner.FeedHandler.GetEnclosureAlert(f.link))
                {
                    e.DownloadItem.OwnerFeed = f;
                    List<DownloadItem> items = new List<DownloadItem>();
                    items.Add(e.DownloadItem);
                    toastNotifier.Alert(f.title, 1, items);
                }
            }
        }
 
        private  void BeginTransformFeed(FeedInfo feed, UltraTreeNode feedNode, string stylesheet)
        {
            owner.MakeAndQueueTask(ThreadWorker.Task.TransformFeed, OnFeedTransformed,
                                   ThreadWorkerBase.DuplicateTaskQueued.Abort, feed, feedNode, stylesheet);
        }
 
        private  void BeginTransformFeedList(FeedInfoList feeds, UltraTreeNode feedNode, string stylesheet)
        {
            owner.MakeAndQueueTask(ThreadWorker.Task.TransformCategory,
                                   OnFeedTransformed,
                                   ThreadWorkerBase.DuplicateTaskQueued.Abort, feeds, feedNode, stylesheet);
        }
 
        private  FeedInfoList GetCategoryItemsAtPage(int pageNum)
        {
            if (_currentCategoryNewsItems == null)
            {
                return null;
            }
            int itemsPerPage = Convert.ToInt32(owner.Preferences.NumNewsItemsPerPage);
            bool validPageNum = (pageNum >= 1) && (pageNum <= _lastPageNumber);
            if (owner.Preferences.LimitNewsItemsPerPage && validPageNum)
            {
                FeedInfoList fil = new FeedInfoList(_currentCategoryNewsItems.Title);
                int endindex = pageNum*itemsPerPage;
                int startindex = endindex - itemsPerPage;
                int counter = 0;
                int numLeft = itemsPerPage;
                foreach (FeedInfo fi in _currentCategoryNewsItems)
                {
                    if (numLeft <= 0)
                    {
                        break;
                    }
                    FeedInfo ficlone = fi.Clone(false);
                    if ((fi.ItemsList.Count + counter) > startindex)
                    {
                        int actualstart = startindex - counter;
                        int actualend = actualstart + numLeft;
                        if (actualend > fi.ItemsList.Count)
                        {
                            int numAdded = fi.ItemsList.Count - actualstart;
                            ficlone.ItemsList.AddRange(fi.ItemsList.GetRange(actualstart, numAdded));
                            numLeft -= numAdded;
                            startindex += numAdded;
                        }
                        else
                        {
                            ficlone.ItemsList.AddRange(fi.ItemsList.GetRange(actualstart, numLeft));
                            numLeft -= numLeft;
                        }
                        fil.Add(ficlone);
                    }
                    counter += fi.ItemsList.Count;
                }
                return fil;
            }
            else
            {
                return _currentCategoryNewsItems;
            }
        }
 
        private  FeedInfo GetFeedItemsAtPage(int pageNum)
        {
            if (_currentFeedNewsItems == null)
            {
                return null;
            }
            int itemsPerPage = Convert.ToInt32(owner.Preferences.NumNewsItemsPerPage);
            int numItems = _currentFeedNewsItems.ItemsList.Count;
            bool validPageNum = (pageNum >= 1) && (pageNum <= _lastPageNumber);
            if (owner.Preferences.LimitNewsItemsPerPage && validPageNum)
            {
                FeedInfo fi = _currentFeedNewsItems.Clone(false);
                int endindex = pageNum*itemsPerPage;
                int startindex = endindex - itemsPerPage;
                if (endindex > numItems)
                {
                    fi.ItemsList.AddRange(_currentFeedNewsItems.ItemsList.GetRange(startindex, numItems - startindex));
                }
                else
                {
                    fi.ItemsList.AddRange(_currentFeedNewsItems.ItemsList.GetRange(startindex, itemsPerPage));
                }
                return fi;
            }
            else
            {
                return _currentFeedNewsItems;
            }
        }
 
        internal  void RefreshFeedDisplay(TreeFeedsNodeBase tn, bool populateListview)
        {
            if (tn == null)
                tn = CurrentSelectedFeedsNode;
            if (tn == null)
                return;
            if (!tn.Selected || tn.Type != FeedNodeType.Feed)
                return;
            INewsFeed f = owner.GetFeed(tn.DataKey);
            if (f != null)
            {
                owner.StateHandler.MoveNewsHandlerStateTo(NewsHandlerState.RefreshOne);
                try
                {
                    htmlDetail.Clear();
                    IList<NewsItem> items = owner.FeedHandler.GetCachedItemsForFeed(tn.DataKey);
                    IList<NewsItem> unread = FilterUnreadFeedItems(items);
                    if ((DisplayFeedAlertWindow.All == owner.Preferences.ShowAlertWindow ||
                         (DisplayFeedAlertWindow.AsConfiguredPerFeed == owner.Preferences.ShowAlertWindow &&
                          f.alertEnabled)) &&
                        tn.UnreadCount < unread.Count)
                    {
                        toastNotifier.Alert(tn.Text, unread.Count, unread);
                    }
                    if (tn.UnreadCount != unread.Count)
                    {
                        UnreadItemsNodeRemoveItems(items);
                        UnreadItemsNode.Items.AddRange(unread);
                        UnreadItemsNode.UpdateReadStatus();
                    }
                    if (populateListview)
                    {
                        PopulateListView(tn, items, true, false, tn);
                    }
                    IFeedDetails fi = owner.GetFeedInfo(tn.DataKey);
                    if (fi != null)
                    {
                        FeedDetailTabState.Url = fi.Link;
                        FeedInfo fi2 = (FeedInfo) fi.Clone();
                        fi2.ItemsList.Clear();
                        fi2.ItemsList.AddRange(unread);
                        ThreadedListViewColumnHeader colHeader =
                            listFeedItems.Columns[listFeedItems.SortManager.SortColumnIndex];
                        IComparer<NewsItem> newsItemSorter =
                            RssHelper.GetComparer(listFeedItems.SortManager.SortOrder == SortOrder.Descending,
                                                  (NewsItemSortField)
                                                  Enum.Parse(typeof (NewsItemSortField), colHeader.Key));
                        fi2.ItemsList.Sort(newsItemSorter);
                        _currentFeedNewsItems = fi2;
                        _currentCategoryNewsItems = null;
                        _currentPageNumber = _lastPageNumber = 1;
                        int numItems = _currentFeedNewsItems.ItemsList.Count;
                        string stylesheet = owner.FeedHandler.GetStyleSheet(tn.DataKey);
                        if (numItems > 0)
                        {
                            int itemsPerPage = Convert.ToInt32(owner.Preferences.NumNewsItemsPerPage);
                            _lastPageNumber = (numItems/itemsPerPage) + (numItems%itemsPerPage == 0 ? 0 : 1);
                            if (string.IsNullOrEmpty(stylesheet))
                            {
                                fi2 = GetFeedItemsAtPage(1);
                                ;
                            }
                        }
                        if (tn.Selected)
                        {
                            BeginTransformFeed(fi2, tn, stylesheet);
                        }
                    }
                }
                catch (Exception e)
                {
                    EmptyListView();
                    owner.PublishXmlFeedError(e, tn.DataKey, true);
                }
                owner.StateHandler.MoveNewsHandlerStateTo(NewsHandlerState.RefreshOneDone);
            }
        }
 
        private  void RefreshCategoryDisplay(TreeFeedsNodeBase tn)
        {
            listFeedItems.BeginUpdate();
            string category = tn.CategoryStoreName;
            FeedInfoList fil2, unreadItems = new FeedInfoList(category);
            PopulateListView(tn, new List<NewsItem>(), true);
            htmlDetail.Clear();
            WalkdownThenRefreshFeed(tn, false, true, tn, unreadItems);
            listFeedItems.EndUpdate();
            ThreadedListViewColumnHeader colHeader = listFeedItems.Columns[listFeedItems.SortManager.SortColumnIndex];
            IComparer<NewsItem> newsItemSorter =
                RssHelper.GetComparer(listFeedItems.SortManager.SortOrder == SortOrder.Descending,
                                      (NewsItemSortField) Enum.Parse(typeof (NewsItemSortField), colHeader.Key));
            foreach (FeedInfo f in unreadItems)
            {
                f.ItemsList.Sort(newsItemSorter);
            }
            _currentFeedNewsItems = null;
            fil2 = _currentCategoryNewsItems = unreadItems;
            _currentPageNumber = _lastPageNumber = 1;
            int numItems = _currentCategoryNewsItems.NewsItemCount;
            string stylesheet = owner.FeedHandler.GetCategoryStyleSheet(category);
            if (numItems > 0)
            {
                int itemsPerPage = Convert.ToInt32(owner.Preferences.NumNewsItemsPerPage);
                _lastPageNumber = (numItems/itemsPerPage) + (numItems%itemsPerPage == 0 ? 0 : 1);
                if (string.IsNullOrEmpty(stylesheet))
                {
                    fil2 = GetCategoryItemsAtPage(1);
                }
            }
            if (tn.Selected)
            {
                FeedDetailTabState.Url = String.Empty;
                BeginTransformFeedList(fil2, tn, stylesheet);
            }
        }
 
        internal  void DeleteCategory(TreeFeedsNodeBase categoryFeedsNode)
        {
            if (categoryFeedsNode == null) categoryFeedsNode = CurrentSelectedFeedsNode;
            if (categoryFeedsNode == null) return;
            if (categoryFeedsNode.Type != FeedNodeType.Category) return;
            TreeFeedsNodeBase cnf = null;
            if (listFeedItems.Items.Count > 0)
                cnf = TreeHelper.FindNode(categoryFeedsNode, (NewsItem) (listFeedItems.Items[0]).Key);
            if (cnf != null)
            {
                EmptyListView();
                htmlDetail.Clear();
            }
            if (categoryFeedsNode.Selected ||
                TreeHelper.IsChildNode(categoryFeedsNode, TreeSelectedFeedsNode))
            {
                TreeSelectedFeedsNode = TreeHelper.GetNewNodeToActivate(categoryFeedsNode);
                RefreshFeedDisplay(TreeSelectedFeedsNode, true);
            }
            WalkdownThenDeleteFeedsOrCategories(categoryFeedsNode);
            UpdateTreeNodeUnreadStatus(categoryFeedsNode, 0);
            try
            {
                categoryFeedsNode.Parent.Nodes.Remove(categoryFeedsNode);
            }
            finally
            {
                DelayTask(DelayedTasks.SyncRssSearchTree);
            }
        }
 
        private  void WalkdownThenRefreshFeed(TreeFeedsNodeBase startNode, bool forceRefresh, bool categorized,
                                             TreeFeedsNodeBase initialFeedsNode, FeedInfoList unreadItems)
        {
            if (startNode == null) return;
            if (TreeSelectedFeedsNode != initialFeedsNode)
                return;
            try
            {
                for (TreeFeedsNodeBase child = startNode.FirstNode; child != null; child = child.NextNode)
                {
                    if (Disposing)
                        return;
                    if (child.Type != FeedNodeType.Feed && child.FirstNode != null)
                    {
                        WalkdownThenRefreshFeed(child, forceRefresh, categorized, initialFeedsNode, unreadItems);
                    }
                    else
                    {
                        string feedUrl = child.DataKey;
                        if (feedUrl == null || !owner.FeedHandler.FeedsTable.ContainsKey(feedUrl))
                            continue;
                        try
                        {
                            if (forceRefresh)
                            {
                                DelayTask(DelayedTasks.StartRefreshOneFeed, feedUrl);
                            }
                            else if (categorized)
                            {
                                IList<NewsItem> items = owner.FeedHandler.GetCachedItemsForFeed(feedUrl);
                                INewsFeed f = owner.GetFeed(feedUrl);
                                FeedInfo fi;
                                if (f != null)
                                {
                                    fi = (FeedInfo) owner.GetFeedInfo(f.link);
                                    if (fi == null)
                                        continue;
                                    fi = fi.Clone(false);
                                }
                                else
                                {
                                    fi = FeedInfo.Empty;
                                }
                                foreach (NewsItem i in items)
                                {
                                    if (!i.BeenRead)
                                        fi.ItemsList.Add(i);
                                }
                                if (fi.ItemsList.Count > 0)
                                {
                                    unreadItems.Add(fi);
                                    if (fi.ItemsList.Count != child.UnreadCount)
                                    {
                                        UpdateTreeNodeUnreadStatus(child, fi.ItemsList.Count);
                                        UnreadItemsNodeRemoveItems(items);
                                        UnreadItemsNode.Items.AddRange(fi.ItemsList);
                                        UnreadItemsNode.UpdateReadStatus();
                                    }
                                }
                                else
                                {
                                }
                                PopulateListView(child, items, false, true, initialFeedsNode);
                                Application.DoEvents();
                            }
                        }
                        catch (Exception e)
                        {
                            owner.PublishXmlFeedError(e, feedUrl, true);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                _log.Error("WalkdownThenRefreshFeed() failed.", ex);
            }
        }
 
        private  void WalkdownAndCatchupCategory(TreeFeedsNodeBase startNode)
        {
            if (startNode == null) return;
            if (startNode.Type == FeedNodeType.Category)
            {
                for (TreeFeedsNodeBase child = startNode.FirstNode; child != null; child = child.NextNode)
                {
                    if (child.Type == FeedNodeType.Category)
                        WalkdownAndCatchupCategory(child);
                    else
                    {
                        UnreadItemsNodeRemoveItems(child.DataKey);
                        owner.FeedHandler.MarkAllCachedItemsAsRead(child.DataKey);
                        UpdateTreeNodeUnreadStatus(child, 0);
                    }
                }
            }
            else
            {
                owner.FeedHandler.MarkAllCachedItemsAsRead(startNode.DataKey);
            }
        }
 
        private  void WalkdownThenRenameFeedCategory(TreeFeedsNodeBase startNode, string newCategory)
        {
            if (startNode == null) return;
            INewsFeed f;
            if (startNode.Type == FeedNodeType.Feed)
            {
                f = owner.GetFeed(startNode.DataKey);
                if (f != null)
                {
                    f.category = newCategory;
                    owner.FeedWasModified(f, NewsFeedProperty.FeedCategory);
                }
<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442342405/fstmerge_var1_5304251764660495411
                if (newCategory != null && !owner.FeedHandler.HasCategory(newCategory))
                    owner.FeedHandler.AddCategory(newCategory);
=======
                if (newCategory != null && !owner.FeedHandler.Categories.ContainsKey(newCategory))
                    owner.FeedHandler.Categories.Add(newCategory);
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442342405/fstmerge_var2_4763637799808219827
            }
            else
            {
                for (TreeFeedsNodeBase child = startNode.FirstNode; child != null; child = child.NextNode)
                {
                    if (child.Type == FeedNodeType.Feed)
                        WalkdownThenRenameFeedCategory(child, child.Parent.CategoryStoreName);
                    else
                        WalkdownThenRenameFeedCategory(child, null );
                }
            }
        }
 
        private  void WalkdownThenDeleteFeedsOrCategories(TreeFeedsNodeBase startNode)
        {
            if (startNode == null) return;
            if (startNode.Type == FeedNodeType.Feed)
            {
                if (owner.FeedHandler.FeedsTable.ContainsKey(startNode.DataKey))
                {
                    INewsFeed f = owner.GetFeed(startNode.DataKey);
                    if (f != null)
                    {
                        UnreadItemsNodeRemoveItems(f);
                        f.Tag = null;
                        try
                        {
                            owner.FeedHandler.DeleteFeed(f.link);
                        }
                        catch
                        {
                        }
<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442342466/fstmerge_var1_2371573564196166348
                        if (owner.FeedHandler.HasCategory(f.category))
                            owner.FeedHandler.DeleteCategory(f.category);
=======
                        if (owner.FeedHandler.Categories.ContainsKey(f.category))
                            owner.FeedHandler.Categories.Remove(f.category);
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442342466/fstmerge_var2_401813579544081351
                    }
                }
                else
                {
                    string catName = TreeFeedsNodeBase.BuildCategoryStoreName(startNode.Parent);
                    if (owner.FeedHandler.HasCategory(catName))
                        owner.FeedHandler.DeleteCategory(catName);
                }
            }
            else
            {
                string catName = startNode.CategoryStoreName;
                if (owner.FeedHandler.HasCategory(catName))
                    owner.FeedHandler.DeleteCategory(catName);
                for (TreeFeedsNodeBase child = startNode.FirstNode; child != null; child = child.NextNode)
                {
                    WalkdownThenDeleteFeedsOrCategories(child);
                }
            }
        }
 
        private  FeedColumnLayout GetFeedColumnLayout(TreeFeedsNodeBase startNode)
        {
            if (startNode == null)
                startNode = TreeSelectedFeedsNode;
            if (startNode == null)
                return listFeedItems.FeedColumnLayout;
            FeedColumnLayout layout = listFeedItems.FeedColumnLayout;
            if (startNode.Type == FeedNodeType.Feed)
            {
                layout = owner.GetFeedColumnLayout(startNode.DataKey);
                if (layout == null) layout = owner.GlobalFeedColumnLayout;
            }
            else if (startNode.Type == FeedNodeType.Category)
            {
                layout = owner.GetCategoryColumnLayout(startNode.CategoryStoreName);
                if (layout == null) layout = owner.GlobalCategoryColumnLayout;
            }
            else if (startNode.Type == FeedNodeType.Finder)
            {
                layout = owner.GlobalSearchFolderColumnLayout;
            }
            else if (startNode.Type == FeedNodeType.SmartFolder)
            {
                layout = owner.GlobalSpecialFolderColumnLayout;
            }
            return layout;
        }
 
        private  void SetFeedHandlerFeedColumnLayout(TreeFeedsNodeBase feedsNode, FeedColumnLayout layout)
        {
            if (feedsNode == null) feedsNode = CurrentSelectedFeedsNode;
            if (feedsNode != null)
            {
                if (feedsNode.Type == FeedNodeType.Feed)
                {
                    owner.SetFeedColumnLayout(feedsNode.DataKey, layout);
                }
                else if (feedsNode.Type == FeedNodeType.Category)
                {
                    owner.SetCategoryColumnLayout(feedsNode.CategoryStoreName, layout);
                }
                else if (feedsNode.Type == FeedNodeType.Finder)
                {
                    owner.GlobalSearchFolderColumnLayout = layout;
                }
                else if (feedsNode.Type == FeedNodeType.SmartFolder)
                {
                    owner.GlobalSpecialFolderColumnLayout = layout;
                }
            }
        }
 
        private  void SetGlobalFeedColumnLayout(FeedNodeType type, FeedColumnLayout layout)
        {
            if (layout == null) throw new ArgumentNullException("layout");
            if (type == FeedNodeType.Feed)
            {
                owner.GlobalFeedColumnLayout = layout;
            }
            else if (type == FeedNodeType.Category)
            {
                owner.GlobalCategoryColumnLayout = layout;
            }
            else
            {
            }
        }
 
        public  ThreadedListViewItem GetListViewItem(NewsItem item)
        {
            ThreadedListViewItem theItem = null;
            for (int i = 0; i < listFeedItems.Items.Count; i++)
            {
                ThreadedListViewItem currentItem = listFeedItems.Items[i];
                if (item.Equals(currentItem.Key))
                {
                    theItem = currentItem;
                    break;
                }
            }
            return theItem;
        }
 
        public  ThreadedListViewItem GetListViewItem(string id)
        {
            string normalizedId = HtmlHelper.UrlDecode(id);
            ThreadedListViewItem theItem = null;
            for (int i = 0; i < listFeedItems.Items.Count; i++)
            {
                ThreadedListViewItem currentItem = listFeedItems.Items[i];
                NewsItem item = (NewsItem) currentItem.Key;
                if (item.Id.Equals(id) || item.Id.Equals(normalizedId))
                {
                    theItem = currentItem;
                    break;
                }
            }
            return theItem;
        }
 
        internal  TreeFeedsNodeBase CreateSubscriptionsCategoryHive(TreeFeedsNodeBase startNode, string category)
        {
            return TreeHelper.CreateCategoryHive(startNode, category, _treeCategoryContextMenu);
        }
 
        private  void DoEditTreeNodeLabel()
        {
            if (CurrentSelectedFeedsNode != null)
            {
                CurrentSelectedFeedsNode.BeginEdit();
            }
        }
 
        protected override  void Dispose(bool disposing)
        {
            try
            {
                if (disposing)
                {
                    if (components != null)
                    {
                        components.Dispose();
                    }
                }
                base.Dispose(disposing);
            }
            catch
            {
            }
        }
 
        private  void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(WinGuiMain));
            Infragistics.Win.UltraWinTree.Override _override1 = new Infragistics.Win.UltraWinTree.Override();
            Infragistics.Win.UltraWinTree.UltraTreeColumnSet ultraTreeColumnSet1 = new Infragistics.Win.UltraWinTree.UltraTreeColumnSet();
            Infragistics.Win.UltraWinTree.UltraTreeNodeColumn ultraTreeNodeColumn1 = new Infragistics.Win.UltraWinTree.UltraTreeNodeColumn();
            Infragistics.Win.UltraWinTree.Override _override2 = new Infragistics.Win.UltraWinTree.Override();
            Infragistics.Win.Appearance appearance7 = new Infragistics.Win.Appearance();
            Infragistics.Win.UltraWinExplorerBar.UltraExplorerBarGroup ultraExplorerBarGroup1 = new Infragistics.Win.UltraWinExplorerBar.UltraExplorerBarGroup();
            Infragistics.Win.Appearance appearance2 = new Infragistics.Win.Appearance();
            Infragistics.Win.Appearance appearance3 = new Infragistics.Win.Appearance();
            Infragistics.Win.UltraWinExplorerBar.UltraExplorerBarGroup ultraExplorerBarGroup2 = new Infragistics.Win.UltraWinExplorerBar.UltraExplorerBarGroup();
            Infragistics.Win.Appearance appearance4 = new Infragistics.Win.Appearance();
            Infragistics.Win.Appearance appearance5 = new Infragistics.Win.Appearance();
            Infragistics.Win.Appearance appearance8 = new Infragistics.Win.Appearance();
            this.NavigatorFeedSubscriptions = new Infragistics.Win.UltraWinExplorerBar.UltraExplorerBarContainerControl();
            this.treeFeeds = new Infragistics.Win.UltraWinTree.UltraTree();
            this.NavigatorSearch = new Infragistics.Win.UltraWinExplorerBar.UltraExplorerBarContainerControl();
            this.panelRssSearch = new System.Windows.Forms.Panel();
            this.ultraToolTipManager = new Infragistics.Win.UltraWinToolTip.UltraToolTipManager(this.components);
            this.panelFeedDetails = new System.Windows.Forms.Panel();
            this.panelWebDetail = new System.Windows.Forms.Panel();
            this.htmlDetail = new IEControl.HtmlControl();
            this.detailsPaneSplitter = new RssBandit.WinGui.Controls.CollapsibleSplitter();
            this.panelFeedItems = new System.Windows.Forms.Panel();
            this.listFeedItemsO = new RssBandit.WinGui.Controls.UltraTreeExtended();
            this.listFeedItems = new System.Windows.Forms.ThListView.ThreadedListView();
            this.colHeadline = new System.Windows.Forms.ThListView.ThreadedListViewColumnHeader();
            this.colDate = new System.Windows.Forms.ThListView.ThreadedListViewColumnHeader();
            this.colTopic = new System.Windows.Forms.ThListView.ThreadedListViewColumnHeader();
            this.toolTip = new System.Windows.Forms.ToolTip(this.components);
            this._status = new System.Windows.Forms.StatusBar();
            this.statusBarBrowser = new System.Windows.Forms.StatusBarPanel();
            this.statusBarBrowserProgress = new System.Windows.Forms.StatusBarPanel();
            this.statusBarConnectionState = new System.Windows.Forms.StatusBarPanel();
            this.statusBarRssParser = new System.Windows.Forms.StatusBarPanel();
            this.progressBrowser = new System.Windows.Forms.ProgressBar();
            this.rightSandDock = new TD.SandDock.DockContainer();
            this.sandDockManager = new TD.SandDock.SandDockManager();
            this.bottomSandDock = new TD.SandDock.DockContainer();
            this.topSandDock = new TD.SandDock.DockContainer();
            this._docContainer = new TD.SandDock.DocumentContainer();
            this._docFeedDetails = new TD.SandDock.DockControl();
            this.panelClientAreaContainer = new System.Windows.Forms.Panel();
            this.panelFeedDetailsContainer = new System.Windows.Forms.Panel();
            this.detailHeaderCaption = new Infragistics.Win.Misc.UltraLabel();
            this.splitterNavigator = new System.Windows.Forms.Splitter();
            this.Navigator = new Infragistics.Win.UltraWinExplorerBar.UltraExplorerBar();
            this.pNavigatorCollapsed = new System.Windows.Forms.Panel();
            this.navigatorHiddenCaption = new RssBandit.WinGui.Controls.VerticalHeaderLabel();
            this._startupTimer = new System.Windows.Forms.Timer(this.components);
            this._timerTreeNodeExpand = new System.Timers.Timer();
            this._timerRefreshFeeds = new System.Timers.Timer();
            this._timerRefreshCommentFeeds = new System.Timers.Timer();
            this._timerResetStatus = new System.Windows.Forms.Timer(this.components);
            this._uiTasksTimer = new RssBandit.WinGui.Forms.WinGuiMain.UITaskTimer(this.components);
            this.helpProvider1 = new System.Windows.Forms.HelpProvider();
            this._timerDispatchResultsToUI = new System.Windows.Forms.Timer(this.components);
            this.NavigatorFeedSubscriptions.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.treeFeeds)).BeginInit();
            this.NavigatorSearch.SuspendLayout();
            this.panelFeedDetails.SuspendLayout();
            this.panelWebDetail.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.htmlDetail)).BeginInit();
            this.panelFeedItems.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.listFeedItemsO)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarBrowser)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarBrowserProgress)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarConnectionState)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarRssParser)).BeginInit();
            this._docContainer.SuspendLayout();
            this._docFeedDetails.SuspendLayout();
            this.panelClientAreaContainer.SuspendLayout();
            this.panelFeedDetailsContainer.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.Navigator)).BeginInit();
            this.Navigator.SuspendLayout();
            this.pNavigatorCollapsed.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this._timerTreeNodeExpand)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this._timerRefreshFeeds)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this._timerRefreshCommentFeeds)).BeginInit();
            this.SuspendLayout();
            this.NavigatorFeedSubscriptions.Controls.Add(this.treeFeeds);
            resources.ApplyResources(this.NavigatorFeedSubscriptions, "NavigatorFeedSubscriptions");
            this.NavigatorFeedSubscriptions.Name = "NavigatorFeedSubscriptions";
            this.helpProvider1.SetShowHelp(this.NavigatorFeedSubscriptions, ((bool)(resources.GetObject("NavigatorFeedSubscriptions.ShowHelp"))));
            this.treeFeeds.AllowDrop = true;
            this.treeFeeds.BorderStyle = Infragistics.Win.UIElementBorderStyle.None;
            resources.ApplyResources(this.treeFeeds, "treeFeeds");
            this.treeFeeds.HideSelection = false;
            this.treeFeeds.ImageTransparentColor = System.Drawing.Color.Transparent;
            this.treeFeeds.Name = "treeFeeds";
            this.treeFeeds.NodeConnectorColor = System.Drawing.SystemColors.ControlDark;
            _override1.LabelEdit = Infragistics.Win.DefaultableBoolean.True;
            _override1.Sort = Infragistics.Win.UltraWinTree.SortType.Ascending;
            this.treeFeeds.Override = _override1;
            this.treeFeeds.SettingsKey = "";
            this.helpProvider1.SetShowHelp(this.treeFeeds, ((bool)(resources.GetObject("treeFeeds.ShowHelp"))));
            this.NavigatorSearch.Controls.Add(this.panelRssSearch);
            resources.ApplyResources(this.NavigatorSearch, "NavigatorSearch");
            this.NavigatorSearch.Name = "NavigatorSearch";
            this.helpProvider1.SetShowHelp(this.NavigatorSearch, ((bool)(resources.GetObject("NavigatorSearch.ShowHelp"))));
            this.panelRssSearch.BackColor = System.Drawing.SystemColors.InactiveCaption;
            resources.ApplyResources(this.panelRssSearch, "panelRssSearch");
            this.panelRssSearch.Name = "panelRssSearch";
            this.helpProvider1.SetShowHelp(this.panelRssSearch, ((bool)(resources.GetObject("panelRssSearch.ShowHelp"))));
            this.ultraToolTipManager.ContainingControl = this;
            this.ultraToolTipManager.DisplayStyle = Infragistics.Win.ToolTipDisplayStyle.Office2007;
            this.panelFeedDetails.Controls.Add(this.panelWebDetail);
            this.panelFeedDetails.Controls.Add(this.detailsPaneSplitter);
            this.panelFeedDetails.Controls.Add(this.panelFeedItems);
            resources.ApplyResources(this.panelFeedDetails, "panelFeedDetails");
            this.panelFeedDetails.Name = "panelFeedDetails";
            this.helpProvider1.SetShowHelp(this.panelFeedDetails, ((bool)(resources.GetObject("panelFeedDetails.ShowHelp"))));
            this.panelWebDetail.Controls.Add(this.htmlDetail);
            resources.ApplyResources(this.panelWebDetail, "panelWebDetail");
            this.panelWebDetail.Name = "panelWebDetail";
            this.helpProvider1.SetShowHelp(this.panelWebDetail, ((bool)(resources.GetObject("panelWebDetail.ShowHelp"))));
            this.htmlDetail.AllowDrop = true;
            resources.ApplyResources(this.htmlDetail, "htmlDetail");
            this.htmlDetail.Name = "htmlDetail";
            this.htmlDetail.OcxState = ((System.Windows.Forms.AxHost.State)(resources.GetObject("htmlDetail.OcxState")));
            this.helpProvider1.SetShowHelp(this.htmlDetail, ((bool)(resources.GetObject("htmlDetail.ShowHelp"))));
            this.detailsPaneSplitter.AnimationDelay = 20;
            this.detailsPaneSplitter.AnimationStep = 20;
            this.detailsPaneSplitter.BackColor = System.Drawing.SystemColors.Control;
            this.detailsPaneSplitter.BorderStyle3D = System.Windows.Forms.Border3DStyle.Flat;
            this.detailsPaneSplitter.ControlToHide = this.panelFeedItems;
            this.detailsPaneSplitter.Cursor = System.Windows.Forms.Cursors.HSplit;
            resources.ApplyResources(this.detailsPaneSplitter, "detailsPaneSplitter");
            this.detailsPaneSplitter.ExpandParentForm = false;
            this.detailsPaneSplitter.Name = "detailsPaneSplitter";
            this.helpProvider1.SetShowHelp(this.detailsPaneSplitter, ((bool)(resources.GetObject("detailsPaneSplitter.ShowHelp"))));
            this.detailsPaneSplitter.TabStop = false;
            this.detailsPaneSplitter.UseAnimations = false;
            this.detailsPaneSplitter.VisualStyle = RssBandit.WinGui.Controls.VisualStyles.XP;
            this.panelFeedItems.Controls.Add(this.listFeedItemsO);
            this.panelFeedItems.Controls.Add(this.listFeedItems);
            resources.ApplyResources(this.panelFeedItems, "panelFeedItems");
            this.panelFeedItems.Name = "panelFeedItems";
            this.helpProvider1.SetShowHelp(this.panelFeedItems, ((bool)(resources.GetObject("panelFeedItems.ShowHelp"))));
            this.listFeedItemsO.ColumnSettings.AllowCellEdit = Infragistics.Win.UltraWinTree.AllowCellEdit.Disabled;
            this.listFeedItemsO.ColumnSettings.AutoFitColumns = Infragistics.Win.UltraWinTree.AutoFitColumns.ResizeAllColumns;
            ultraTreeColumnSet1.AllowCellEdit = Infragistics.Win.UltraWinTree.AllowCellEdit.Disabled;
            ultraTreeNodeColumn1.AllowCellEdit = Infragistics.Win.UltraWinTree.AllowCellEdit.Disabled;
            ultraTreeNodeColumn1.Key = "Arranged by: Date";
            ultraTreeColumnSet1.Columns.Add(ultraTreeNodeColumn1);
            ultraTreeColumnSet1.Key = "csOutlook";
            this.listFeedItemsO.ColumnSettings.ColumnSets.Add(ultraTreeColumnSet1);
            this.listFeedItemsO.ColumnSettings.HeaderStyle = Infragistics.Win.HeaderStyle.XPThemed;
            resources.ApplyResources(this.listFeedItemsO, "listFeedItemsO");
            this.listFeedItemsO.FullRowSelect = true;
            this.listFeedItemsO.HideSelection = false;
            this.listFeedItemsO.ImageTransparentColor = System.Drawing.Color.Transparent;
            this.listFeedItemsO.IsUpdatingSelection = false;
            this.listFeedItemsO.Name = "listFeedItemsO";
            this.listFeedItemsO.NodeConnectorColor = System.Drawing.SystemColors.ControlDark;
            _override2.ColumnSetIndex = 0;
            _override2.ItemHeight = 35;
            _override2.SelectionType = Infragistics.Win.UltraWinTree.SelectType.Extended;
            this.listFeedItemsO.Override = _override2;
            this.listFeedItemsO.SettingsKey = "";
            this.helpProvider1.SetShowHelp(this.listFeedItemsO, ((bool)(resources.GetObject("listFeedItemsO.ShowHelp"))));
            this.listFeedItems.Activation = System.Windows.Forms.ItemActivation.OneClick;
            this.listFeedItems.AllowColumnReorder = true;
            this.listFeedItems.Columns.AddRange(new System.Windows.Forms.ThListView.ThreadedListViewColumnHeader[] {
            this.colHeadline,
            this.colDate,
            this.colTopic});
            resources.ApplyResources(this.listFeedItems, "listFeedItems");
            this.listFeedItems.FullRowSelect = true;
            this.listFeedItems.HideSelection = false;
            this.listFeedItems.Name = "listFeedItems";
            this.listFeedItems.NoThreadChildsPlaceHolder = null;
            this.helpProvider1.SetShowHelp(this.listFeedItems, ((bool)(resources.GetObject("listFeedItems.ShowHelp"))));
            this.listFeedItems.UseCompatibleStateImageBehavior = false;
            this.listFeedItems.View = System.Windows.Forms.View.Details;
            this.listFeedItems.ListLayoutModified += new System.Windows.Forms.ThListView.ThreadedListView.OnListLayoutModifiedEventHandler(this.OnFeedListLayoutModified);
            this.listFeedItems.ItemActivate += new System.EventHandler(this.OnFeedListItemActivate);
            this.listFeedItems.ExpandThread += new System.Windows.Forms.ThListView.ThreadedListView.OnExpandThreadEventHandler(this.OnFeedListExpandThread);
            this.listFeedItems.ListLayoutChanged += new System.Windows.Forms.ThListView.ThreadedListView.OnListLayoutChangedEventHandler(this.OnFeedListLayoutChanged);
            this.listFeedItems.MouseDown += new System.Windows.Forms.MouseEventHandler(this.OnFeedListMouseDown);
            this.listFeedItems.ItemDrag += new System.Windows.Forms.ItemDragEventHandler(this.OnFeedListItemDrag);
            this.listFeedItems.AfterExpandThread += new System.Windows.Forms.ThListView.ThreadedListView.OnAfterExpandThreadEventHandler(this.OnFeedListAfterExpandThread);
            this.listFeedItems.KeyUp += new System.Windows.Forms.KeyEventHandler(this.OnFeedListItemKeyUp);
            this.colHeadline.ColumnValueType = typeof(string);
            this.colHeadline.Key = "Title";
            resources.ApplyResources(this.colHeadline, "colHeadline");
            this.colDate.ColumnValueType = typeof(System.DateTime);
            this.colDate.Key = "Date";
            resources.ApplyResources(this.colDate, "colDate");
            this.colTopic.ColumnValueType = typeof(string);
            this.colTopic.Key = "Subject";
            resources.ApplyResources(this.colTopic, "colTopic");
            resources.ApplyResources(this._status, "_status");
            this._status.Name = "_status";
            this._status.Panels.AddRange(new System.Windows.Forms.StatusBarPanel[] {
            this.statusBarBrowser,
            this.statusBarBrowserProgress,
            this.statusBarConnectionState,
            this.statusBarRssParser});
            this.helpProvider1.SetShowHelp(this._status, ((bool)(resources.GetObject("_status.ShowHelp"))));
            this._status.ShowPanels = true;
            this.statusBarBrowser.AutoSize = System.Windows.Forms.StatusBarPanelAutoSize.Spring;
            resources.ApplyResources(this.statusBarBrowser, "statusBarBrowser");
            resources.ApplyResources(this.statusBarBrowserProgress, "statusBarBrowserProgress");
            resources.ApplyResources(this.statusBarConnectionState, "statusBarConnectionState");
            resources.ApplyResources(this.statusBarRssParser, "statusBarRssParser");
            resources.ApplyResources(this.progressBrowser, "progressBrowser");
            this.progressBrowser.Name = "progressBrowser";
            this.helpProvider1.SetShowHelp(this.progressBrowser, ((bool)(resources.GetObject("progressBrowser.ShowHelp"))));
            resources.ApplyResources(this.rightSandDock, "rightSandDock");
            this.rightSandDock.Guid = new System.Guid("c6e4c477-596c-4e8c-9d35-840718d4c40d");
            this.rightSandDock.LayoutSystem = new TD.SandDock.SplitLayoutSystem(250, 400);
            this.rightSandDock.Manager = this.sandDockManager;
            this.rightSandDock.Name = "rightSandDock";
            this.helpProvider1.SetShowHelp(this.rightSandDock, ((bool)(resources.GetObject("rightSandDock.ShowHelp"))));
            this.sandDockManager.DockingManager = TD.SandDock.DockingManager.Whidbey;
            this.sandDockManager.OwnerForm = this;
            this.sandDockManager.Renderer = new TD.SandDock.Rendering.Office2003Renderer();
            resources.ApplyResources(this.bottomSandDock, "bottomSandDock");
            this.bottomSandDock.Guid = new System.Guid("9ffc7b96-a550-4e79-a533-8eee52ac0da1");
            this.bottomSandDock.LayoutSystem = new TD.SandDock.SplitLayoutSystem(250, 400);
            this.bottomSandDock.Manager = this.sandDockManager;
            this.bottomSandDock.Name = "bottomSandDock";
            this.helpProvider1.SetShowHelp(this.bottomSandDock, ((bool)(resources.GetObject("bottomSandDock.ShowHelp"))));
            resources.ApplyResources(this.topSandDock, "topSandDock");
            this.topSandDock.Guid = new System.Guid("e1c62abd-0e7a-4bb6-aded-a74f27027165");
            this.topSandDock.LayoutSystem = new TD.SandDock.SplitLayoutSystem(250, 400);
            this.topSandDock.Manager = this.sandDockManager;
            this.topSandDock.Name = "topSandDock";
            this.helpProvider1.SetShowHelp(this.topSandDock, ((bool)(resources.GetObject("topSandDock.ShowHelp"))));
            this._docContainer.Controls.Add(this._docFeedDetails);
            this._docContainer.Cursor = System.Windows.Forms.Cursors.Default;
            this._docContainer.DockingManager = TD.SandDock.DockingManager.Whidbey;
            this._docContainer.Guid = new System.Guid("f032a648-4262-4312-ab2b-abe5094272bd");
            this._docContainer.LayoutSystem = new TD.SandDock.SplitLayoutSystem(250, 400, System.Windows.Forms.Orientation.Horizontal, new TD.SandDock.LayoutSystemBase[] {
            ((TD.SandDock.LayoutSystemBase)(new TD.SandDock.DocumentLayoutSystem(392, 414, new TD.SandDock.DockControl[] {
                        this._docFeedDetails}, this._docFeedDetails)))});
            resources.ApplyResources(this._docContainer, "_docContainer");
            this._docContainer.Manager = null;
            this._docContainer.Name = "_docContainer";
            this._docContainer.Renderer = new TD.SandDock.Rendering.Office2003Renderer();
            this.helpProvider1.SetShowHelp(this._docContainer, ((bool)(resources.GetObject("_docContainer.ShowHelp"))));
            this._docFeedDetails.Closable = false;
            this._docFeedDetails.Controls.Add(this.panelFeedDetails);
            this._docFeedDetails.Guid = new System.Guid("9c7b7643-2ed3-402c-9e86-3c958341c81f");
            resources.ApplyResources(this._docFeedDetails, "_docFeedDetails");
            this._docFeedDetails.Name = "_docFeedDetails";
            this.helpProvider1.SetShowHelp(this._docFeedDetails, ((bool)(resources.GetObject("_docFeedDetails.ShowHelp"))));
            this.panelClientAreaContainer.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(243)))), ((int)(((byte)(243)))), ((int)(((byte)(247)))));
            this.panelClientAreaContainer.Controls.Add(this.panelFeedDetailsContainer);
            this.panelClientAreaContainer.Controls.Add(this.splitterNavigator);
            this.panelClientAreaContainer.Controls.Add(this.Navigator);
            this.panelClientAreaContainer.Controls.Add(this.pNavigatorCollapsed);
            resources.ApplyResources(this.panelClientAreaContainer, "panelClientAreaContainer");
            this.panelClientAreaContainer.Name = "panelClientAreaContainer";
            this.helpProvider1.SetShowHelp(this.panelClientAreaContainer, ((bool)(resources.GetObject("panelClientAreaContainer.ShowHelp"))));
            this.panelFeedDetailsContainer.Controls.Add(this._docContainer);
            this.panelFeedDetailsContainer.Controls.Add(this.detailHeaderCaption);
            resources.ApplyResources(this.panelFeedDetailsContainer, "panelFeedDetailsContainer");
            this.panelFeedDetailsContainer.Name = "panelFeedDetailsContainer";
            this.helpProvider1.SetShowHelp(this.panelFeedDetailsContainer, ((bool)(resources.GetObject("panelFeedDetailsContainer.ShowHelp"))));
            appearance7.BackColor = System.Drawing.Color.CornflowerBlue;
            appearance7.BackColor2 = System.Drawing.Color.MidnightBlue;
            appearance7.BackGradientStyle = Infragistics.Win.GradientStyle.Vertical;
            appearance7.ForeColor = System.Drawing.SystemColors.ActiveCaptionText;
            appearance7.ImageHAlign = Infragistics.Win.HAlign.Right;
            appearance7.ImageVAlign = Infragistics.Win.VAlign.Middle;
            resources.ApplyResources(appearance7, "appearance7");
            appearance7.TextTrimming = Infragistics.Win.TextTrimming.EllipsisWord;
            this.detailHeaderCaption.Appearance = appearance7;
            resources.ApplyResources(this.detailHeaderCaption, "detailHeaderCaption");
            this.detailHeaderCaption.Name = "detailHeaderCaption";
            this.detailHeaderCaption.Padding = new System.Drawing.Size(5, 0);
            this.helpProvider1.SetShowHelp(this.detailHeaderCaption, ((bool)(resources.GetObject("detailHeaderCaption.ShowHelp"))));
            this.detailHeaderCaption.WrapText = false;
            resources.ApplyResources(this.splitterNavigator, "splitterNavigator");
            this.splitterNavigator.Name = "splitterNavigator";
            this.helpProvider1.SetShowHelp(this.splitterNavigator, ((bool)(resources.GetObject("splitterNavigator.ShowHelp"))));
            this.splitterNavigator.TabStop = false;
            this.Navigator.Controls.Add(this.NavigatorFeedSubscriptions);
            this.Navigator.Controls.Add(this.NavigatorSearch);
            resources.ApplyResources(this.Navigator, "Navigator");
            ultraExplorerBarGroup1.Container = this.NavigatorFeedSubscriptions;
            ultraExplorerBarGroup1.Key = "groupFeedsTree";
            appearance2.Image = ((object)(resources.GetObject("appearance2.Image")));
            ultraExplorerBarGroup1.Settings.AppearancesLarge.HeaderAppearance = appearance2;
            appearance3.Image = ((object)(resources.GetObject("appearance3.Image")));
            ultraExplorerBarGroup1.Settings.AppearancesSmall.HeaderAppearance = appearance3;
            resources.ApplyResources(ultraExplorerBarGroup1, "ultraExplorerBarGroup1");
            ultraExplorerBarGroup2.Container = this.NavigatorSearch;
            ultraExplorerBarGroup2.Key = "groupFeedsSearch";
            appearance4.Image = ((object)(resources.GetObject("appearance4.Image")));
            ultraExplorerBarGroup2.Settings.AppearancesLarge.HeaderAppearance = appearance4;
            appearance5.Image = ((object)(resources.GetObject("appearance5.Image")));
            ultraExplorerBarGroup2.Settings.AppearancesSmall.HeaderAppearance = appearance5;
            resources.ApplyResources(ultraExplorerBarGroup2, "ultraExplorerBarGroup2");
            this.Navigator.Groups.AddRange(new Infragistics.Win.UltraWinExplorerBar.UltraExplorerBarGroup[] {
            ultraExplorerBarGroup1,
            ultraExplorerBarGroup2});
            this.Navigator.GroupSettings.Style = Infragistics.Win.UltraWinExplorerBar.GroupStyle.ControlContainer;
            this.Navigator.Name = "Navigator";
            this.Navigator.NavigationMaxGroupHeaders = 0;
            this.helpProvider1.SetShowHelp(this.Navigator, ((bool)(resources.GetObject("Navigator.ShowHelp"))));
            this.Navigator.Style = Infragistics.Win.UltraWinExplorerBar.UltraExplorerBarStyle.OutlookNavigationPane;
            this.pNavigatorCollapsed.BackColor = System.Drawing.Color.Transparent;
            this.pNavigatorCollapsed.Controls.Add(this.navigatorHiddenCaption);
            resources.ApplyResources(this.pNavigatorCollapsed, "pNavigatorCollapsed");
            this.pNavigatorCollapsed.Name = "pNavigatorCollapsed";
            this.helpProvider1.SetShowHelp(this.pNavigatorCollapsed, ((bool)(resources.GetObject("pNavigatorCollapsed.ShowHelp"))));
            appearance8.BackColor = System.Drawing.Color.CornflowerBlue;
            appearance8.BackColor2 = System.Drawing.Color.MidnightBlue;
            appearance8.BackGradientStyle = Infragistics.Win.GradientStyle.Horizontal;
            appearance8.ForeColor = System.Drawing.SystemColors.ActiveCaptionText;
            appearance8.Image = ((object)(resources.GetObject("appearance8.Image")));
            appearance8.ImageHAlign = Infragistics.Win.HAlign.Center;
            appearance8.ImageVAlign = Infragistics.Win.VAlign.Top;
            resources.ApplyResources(appearance8, "appearance8");
            appearance8.TextTrimming = Infragistics.Win.TextTrimming.EllipsisWord;
            this.navigatorHiddenCaption.Appearance = appearance8;
            resources.ApplyResources(this.navigatorHiddenCaption, "navigatorHiddenCaption");
            this.navigatorHiddenCaption.Name = "navigatorHiddenCaption";
            this.navigatorHiddenCaption.Padding = new System.Drawing.Size(0, 5);
            this.helpProvider1.SetShowHelp(this.navigatorHiddenCaption, ((bool)(resources.GetObject("navigatorHiddenCaption.ShowHelp"))));
            this.navigatorHiddenCaption.WrapText = false;
            this._startupTimer.Interval = 45000;
            this._startupTimer.Tick += new System.EventHandler(this.OnTimerStartupTick);
            this._timerTreeNodeExpand.Interval = 1000;
            this._timerTreeNodeExpand.SynchronizingObject = this;
            this._timerTreeNodeExpand.Elapsed += new System.Timers.ElapsedEventHandler(this.OnTimerTreeNodeExpandElapsed);
            this._timerRefreshFeeds.Interval = 600000;
            this._timerRefreshFeeds.SynchronizingObject = this;
            this._timerRefreshFeeds.Elapsed += new System.Timers.ElapsedEventHandler(this.OnTimerFeedsRefreshElapsed);
            this._timerRefreshCommentFeeds.Interval = 600000;
            this._timerRefreshCommentFeeds.SynchronizingObject = this;
            this._timerRefreshCommentFeeds.Elapsed += new System.Timers.ElapsedEventHandler(this.OnTimerCommentFeedsRefreshElapsed);
            this._timerResetStatus.Interval = 5000;
            this._timerResetStatus.Tick += new System.EventHandler(this.OnTimerResetStatusTick);
            this._uiTasksTimer.Enabled = true;
            resources.ApplyResources(this.helpProvider1, "helpProvider1");
            this._timerDispatchResultsToUI.Interval = 250;
            resources.ApplyResources(this, "$this");
            this.Controls.Add(this.panelClientAreaContainer);
            this.Controls.Add(this.rightSandDock);
            this.Controls.Add(this.bottomSandDock);
            this.Controls.Add(this.topSandDock);
            this.Controls.Add(this.progressBrowser);
            this.Controls.Add(this._status);
            this.helpProvider1.SetHelpNavigator(this, ((System.Windows.Forms.HelpNavigator)(resources.GetObject("$this.HelpNavigator"))));
            this.KeyPreview = true;
            this.Name = "WinGuiMain";
            this.helpProvider1.SetShowHelp(this, ((bool)(resources.GetObject("$this.ShowHelp"))));
            this.NavigatorFeedSubscriptions.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.treeFeeds)).EndInit();
            this.NavigatorSearch.ResumeLayout(false);
            this.panelFeedDetails.ResumeLayout(false);
            this.panelWebDetail.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.htmlDetail)).EndInit();
            this.panelFeedItems.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.listFeedItemsO)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarBrowser)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarBrowserProgress)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarConnectionState)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarRssParser)).EndInit();
            this._docContainer.ResumeLayout(false);
            this._docFeedDetails.ResumeLayout(false);
            this.panelClientAreaContainer.ResumeLayout(false);
            this.panelFeedDetailsContainer.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.Navigator)).EndInit();
            this.Navigator.ResumeLayout(false);
            this.pNavigatorCollapsed.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this._timerTreeNodeExpand)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this._timerRefreshFeeds)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this._timerRefreshCommentFeeds)).EndInit();
            this.ResumeLayout(false);
        }
 
        public  void Close(bool forceShutdown)
        {
            _forceShutdown = forceShutdown;
            Close();
        }
 
        public  void SaveUIConfiguration(bool forceFlush)
        {
            try
            {
                OnSaveConfig(owner.GuiSettings);
                SaveSubscriptionTreeState();
                SaveBrowserTabState();
                listFeedItems.CheckForLayoutModifications();
                if (forceFlush)
                {
                    owner.GuiSettings.Flush();
                }
            }
            catch (Exception ex)
            {
                _log.Error("Save .settings.xml failed", ex);
            }
        }
 
        internal  bool LoadAndRestoreBrowserTabState()
        {
            _browserTabsRestored = true;
            string fileName = RssBanditApplication.GetBrowserTabStateFileName();
            try
            {
                if (!File.Exists(fileName))
                    return false;
                using (Stream stream = FileHelper.OpenForRead(fileName))
                {
                    SerializableWebTabState state = SerializableWebTabState.Load(stream);
                    foreach (string url in state.Urls)
                    {
                        try
                        {
                            DetailTabNavigateToUrl(url, String.Empty , true , false
                                );
                        }
                        catch (AxHost.InvalidActiveXStateException)
                        {
                        }
                    }
                }
                return true;
            }
            catch (Exception ex)
            {
                _log.Error("Load " + fileName + " failed", ex);
                return false;
            }
        }
 
        internal  void SaveBrowserTabState()
        {
            string fileName = RssBanditApplication.GetBrowserTabStateFileName();
            SerializableWebTabState state = new SerializableWebTabState();
            try
            {
                foreach (DockControl doc in _docContainer.Documents)
                {
                    ITabState docState = (ITabState) doc.Tag;
                    if ((docState != null) && docState.CanClose)
                    {
                        state.Urls.Add(docState.Url);
                    }
                }
                using (Stream stream = FileHelper.OpenForWrite(fileName))
                {
                    SerializableWebTabState.Save(stream, state);
                }
            }
            catch (Exception ex)
            {
                _log.Error("Save " + fileName + " failed", ex);
                try
                {
                    File.Delete(fileName);
                }
                catch (IOException)
                {
                }
            }
        }
 
        internal  void SaveSubscriptionTreeState()
        {
            string fileName = RssBanditApplication.GetSubscriptionTreeStateFileName();
            try
            {
                using (Stream s = FileHelper.OpenForWrite(fileName))
                {
                    UltraTreeNodeExpansionMemento.Save(s, treeFeeds);
                }
            }
            catch (Exception ex)
            {
                _log.Error("Save " + fileName + " failed", ex);
                try
                {
                    File.Delete(fileName);
                }
                catch (IOException)
                {
                }
            }
        }
 
        internal  bool LoadAndRestoreSubscriptionTreeState()
        {
            string fileName = RssBanditApplication.GetSubscriptionTreeStateFileName();
            try
            {
                if (!File.Exists(fileName))
                    return false;
                using (Stream s = FileHelper.OpenForRead(fileName))
                {
                    UltraTreeNodeExpansionMemento m = UltraTreeNodeExpansionMemento.Load(s);
                    m.Restore(treeFeeds);
                }
                return true;
            }
            catch (Exception ex)
            {
                SetDefaultExpansionTreeNodeState();
                owner.MessageWarn(SR.GUILoadFileOperationExceptionMessage(fileName, ex.Message,
                                                                          SR.GUIUserInfoAboutDefaultTreeState));
                _log.Error("Load " + fileName + " failed", ex);
                return false;
            }
        }
 
        private static  bool IsTreeStateAvailable()
        {
            try
            {
                return File.Exists(RssBanditApplication.GetSubscriptionTreeStateFileName());
            }
            catch (Exception)
            {
                return false;
            }
        }
 
        public  void InitiatePopulateTreeFeeds()
        {
            if (owner == null)
            {
                SetGuiStateFeedback(String.Empty, ApplicationTrayState.NormalIdle);
                return;
            }
            if (owner.FeedHandler.FeedsListOK == false)
            {
                SetGuiStateFeedback(SR.GUIStatusNoFeedlistFile, ApplicationTrayState.NormalIdle);
                return;
            }
            InvokeOnGui(delegate
            {
<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442343513/fstmerge_var1_4893119355730039810
                PopulateFeedSubscriptions(owner.FeedHandler.GetCategories().Values, owner.FeedHandler.FeedsTable,
=======
                PopulateFeedSubscriptions(owner.FeedHandler.Categories, owner.FeedHandler.FeedsTable,
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442343513/fstmerge_var2_7353131865207962721
                                          RssBanditApplication.DefaultCategory);
                PopulateTreeSpecialFeeds();
            });
        }
 
        private  void CheckForFlaggedNodeAndCreate(NewsItem ri)
        {
            ISmartFolder isf;
            TreeFeedsNodeBase tn = null;
            TreeFeedsNodeBase root = _flaggedFeedsNodeRoot;
            if (ri.FlagStatus == Flagged.FollowUp && _flaggedFeedsNodeFollowUp == null)
            {
                _flaggedFeedsNodeFollowUp = new FlaggedItemsNode(Flagged.FollowUp, owner.FlaggedItemsFeed,
                                                                 SR.FeedNodeFlaggedForFollowUpCaption,
                                                                 Resource.SubscriptionTreeImage.RedFlag,
                                                                 Resource.SubscriptionTreeImage.RedFlagSelected,
                                                                 _treeLocalFeedContextMenu);
                root.Nodes.Add(_flaggedFeedsNodeFollowUp);
                isf = _flaggedFeedsNodeFollowUp as ISmartFolder;
                tn = _flaggedFeedsNodeFollowUp;
                if (isf != null) isf.UpdateReadStatus();
            }
            else if (ri.FlagStatus == Flagged.Read && _flaggedFeedsNodeRead == null)
            {
                _flaggedFeedsNodeRead = new FlaggedItemsNode(Flagged.Read, owner.FlaggedItemsFeed,
                                                             SR.FeedNodeFlaggedForReadCaption,
                                                             Resource.SubscriptionTreeImage.GreenFlag,
                                                             Resource.SubscriptionTreeImage.GreenFlagSelected,
                                                             _treeLocalFeedContextMenu);
                root.Nodes.Add(_flaggedFeedsNodeRead);
                isf = _flaggedFeedsNodeRead as ISmartFolder;
                tn = _flaggedFeedsNodeRead;
                if (isf != null) isf.UpdateReadStatus();
            }
            else if (ri.FlagStatus == Flagged.Review && _flaggedFeedsNodeReview == null)
            {
                _flaggedFeedsNodeReview = new FlaggedItemsNode(Flagged.Review, owner.FlaggedItemsFeed,
                                                               SR.FeedNodeFlaggedForReviewCaption,
                                                               Resource.SubscriptionTreeImage.YellowFlag,
                                                               Resource.SubscriptionTreeImage.YellowFlagSelected,
                                                               _treeLocalFeedContextMenu);
                root.Nodes.Add(_flaggedFeedsNodeReview);
                isf = _flaggedFeedsNodeReview as ISmartFolder;
                tn = _flaggedFeedsNodeReview;
                if (isf != null) isf.UpdateReadStatus();
            }
            else if (ri.FlagStatus == Flagged.Forward && _flaggedFeedsNodeForward == null)
            {
                _flaggedFeedsNodeForward = new FlaggedItemsNode(Flagged.Forward, owner.FlaggedItemsFeed,
                                                                SR.FeedNodeFlaggedForForwardCaption,
                                                                Resource.SubscriptionTreeImage.BlueFlag,
                                                                Resource.SubscriptionTreeImage.BlueFlagSelected,
                                                                _treeLocalFeedContextMenu);
                root.Nodes.Add(_flaggedFeedsNodeForward);
                isf = _flaggedFeedsNodeForward as ISmartFolder;
                tn = _flaggedFeedsNodeForward;
                if (isf != null) isf.UpdateReadStatus();
            }
            else if (ri.FlagStatus == Flagged.Reply && _flaggedFeedsNodeReply == null)
            {
                _flaggedFeedsNodeReply = new FlaggedItemsNode(Flagged.Reply, owner.FlaggedItemsFeed,
                                                              SR.FeedNodeFlaggedForReplyCaption,
                                                              Resource.SubscriptionTreeImage.ReplyFlag,
                                                              Resource.SubscriptionTreeImage.ReplyFlagSelected,
                                                              _treeLocalFeedContextMenu);
                root.Nodes.Add(_flaggedFeedsNodeReply);
                isf = _flaggedFeedsNodeReply as ISmartFolder;
                tn = _flaggedFeedsNodeReply;
                if (isf != null) isf.UpdateReadStatus();
            }
            if (tn != null)
            {
                tn.DataKey = owner.FlaggedItemsFeed.link + "?id=" + ri.FlagStatus;
            }
        }
 
        public  void PopulateTreeSpecialFeeds()
        {
            treeFeeds.BeginUpdate();
            TreeFeedsNodeBase root = GetRoot(RootFolderType.SmartFolders);
            root.Nodes.Clear();
            _feedExceptionsFeedsNode = new ExceptionReportNode(SR.FeedNodeFeedExceptionsCaption,
                                                               Resource.SubscriptionTreeImage.Exceptions,
                                                               Resource.SubscriptionTreeImage.ExceptionsSelected,
                                                               _treeLocalFeedContextMenu);
            _feedExceptionsFeedsNode.DataKey = SpecialFeeds.ExceptionManager.GetInstance().link;
            ExceptionNode.UpdateReadStatus();
            _sentItemsFeedsNode = new SentItemsNode(owner.SentItemsFeed,
                                                    Resource.SubscriptionTreeImage.SentItems,
                                                    Resource.SubscriptionTreeImage.SentItems, _treeLocalFeedContextMenu);
            _sentItemsFeedsNode.DataKey = owner.SentItemsFeed.link;
            SentItemsNode.UpdateReadStatus();
            _watchedItemsFeedsNode = new WatchedItemsNode(owner.WatchedItemsFeed,
                                                          Resource.SubscriptionTreeImage.WatchedItems,
                                                          Resource.SubscriptionTreeImage.WatchedItemsSelected,
                                                          _treeLocalFeedContextMenu);
            _watchedItemsFeedsNode.DataKey = owner.WatchedItemsFeed.link;
            WatchedItemsNode.UpdateReadStatus();
            WatchedItemsNode.UpdateCommentStatus();
            _unreadItemsFeedsNode = new UnreadItemsNode(owner.UnreadItemsFeed,
                                                        Resource.SubscriptionTreeImage.WatchedItems,
                                                        Resource.SubscriptionTreeImage.WatchedItemsSelected,
                                                        _treeLocalFeedContextMenu);
            _unreadItemsFeedsNode.DataKey = owner.UnreadItemsFeed.link;
            UnreadItemsNode.UpdateReadStatus();
            _deletedItemsFeedsNode = new WasteBasketNode(owner.DeletedItemsFeed,
                                                         Resource.SubscriptionTreeImage.WasteBasketEmpty,
                                                         Resource.SubscriptionTreeImage.WasteBasketEmpty,
                                                         _treeLocalFeedContextMenu);
            _deletedItemsFeedsNode.DataKey = owner.DeletedItemsFeed.link;
            DeletedItemsNode.UpdateReadStatus();
            _flaggedFeedsNodeRoot = new FlaggedItemsRootNode(SR.FeedNodeFlaggedFeedsCaption,
                                                             Resource.SubscriptionTreeImage.SubscriptionsCategory,
                                                             Resource.SubscriptionTreeImage.
                                                                 SubscriptionsCategoryExpanded,
                                                             null);
            root.Nodes.AddRange(
                new UltraTreeNode[]
                    {
                        _unreadItemsFeedsNode,
                        _watchedItemsFeedsNode,
                        _flaggedFeedsNodeRoot,
                        _feedExceptionsFeedsNode,
                        _sentItemsFeedsNode,
                        _deletedItemsFeedsNode
                    });
            _flaggedFeedsNodeFollowUp = _flaggedFeedsNodeRead = null;
            _flaggedFeedsNodeReview = _flaggedFeedsNodeForward = null;
            _flaggedFeedsNodeReply = null;
            foreach (NewsItem ri in owner.FlaggedItemsFeed.Items)
            {
                CheckForFlaggedNodeAndCreate(ri);
                if (_flaggedFeedsNodeFollowUp != null && _flaggedFeedsNodeRead != null &&
                    _flaggedFeedsNodeReview != null && _flaggedFeedsNodeForward != null &&
                    _flaggedFeedsNodeReply != null)
                {
                    break;
                }
            }
            bool expandRoots = ! IsTreeStateAvailable();
            root.Expanded = expandRoots;
            FinderRootNode froot = (FinderRootNode) GetRoot(RootFolderType.Finder);
            SyncFinderNodes(froot);
            if (expandRoots)
                froot.ExpandAll();
            treeFeeds.EndUpdate();
        }
 
        public  void SyncFinderNodes()
        {
            SyncFinderNodes((FinderRootNode) GetRoot(RootFolderType.Finder));
        }
 
        private  void SyncFinderNodes(FinderRootNode finderRoot)
        {
            if (finderRoot == null)
                return;
            finderRoot.Nodes.Clear();
            finderRoot.InitFromFinders(owner.FinderList, _treeSearchFolderContextMenu);
        }
 
        public  void PopulateFeedSubscriptions(CategoriesCollection categories, IDictionary<string, NewsFeed> feedsTable,
                                              string defaultCategory)
        {
            EmptyListView();
            TreeFeedsNodeBase root = GetRoot(RootFolderType.MyFeeds);
            try
            {
                treeFeeds.BeginUpdate();
                TreeFeedsNodeBase tn;
                root.Nodes.Clear();
                UpdateTreeNodeUnreadStatus(root, 0);
                UnreadItemsNode.Items.Clear();
                Hashtable categoryTable = new Hashtable();
                CategoriesCollection categoryList = (CategoriesCollection) categories.Clone();
                foreach (NewsFeed f in feedsTable.Values)
                {
                    if (Disposing)
                        return;
                    if (RssHelper.IsNntpUrl(f.link))
                    {
                        tn = new FeedNode(f.title, Resource.SubscriptionTreeImage.Nntp,
                                          Resource.SubscriptionTreeImage.NntpSelected,
                                          _treeFeedContextMenu);
                    }
                    else
                    {
                        tn = new FeedNode(f.title, Resource.SubscriptionTreeImage.Feed,
                                          Resource.SubscriptionTreeImage.FeedSelected,
                                          _treeFeedContextMenu,
                                          (owner.Preferences.UseFavicons ? LoadFavicon(f.favicon) : null));
                    }
                    tn.DataKey = f.link;
                    f.Tag = tn;
                    string category = (f.category ?? String.Empty);
                    TreeFeedsNodeBase catnode;
                    if (categoryTable.ContainsKey(category))
                        catnode = (TreeFeedsNodeBase) categoryTable[category];
                    else
                    {
                        catnode = TreeHelper.CreateCategoryHive(root, category, _treeCategoryContextMenu);
                        categoryTable.Add(category, catnode);
                    }
                    catnode.Nodes.Add(tn);
                    SetSubscriptionNodeState(f, tn, FeedProcessingState.Normal);
                    if (f.containsNewMessages)
                    {
                        IList<NewsItem> unread = FilterUnreadFeedItems(f);
                        if (unread.Count > 0)
                        {
                            UpdateTreeNodeUnreadStatus(tn, unread.Count);
                            UnreadItemsNode.Items.AddRange(unread);
                            UnreadItemsNode.UpdateReadStatus();
                        }
                    }
                    if (f.containsNewComments)
                    {
                        UpdateCommentStatus(tn, f);
                    }
                    if (categoryList.ContainsKey(category))
                        categoryList.Remove(category);
                }
                foreach (string category in categoryList.Keys)
                {
                    TreeHelper.CreateCategoryHive(root, category, _treeCategoryContextMenu);
                }
            }
            finally
            {
                treeFeeds.EndUpdate();
            }
            if (Disposing)
                return;
            TreeSelectedFeedsNode = root;
            if (! IsTreeStateAvailable())
                root.Expanded = true;
            DelayTask(DelayedTasks.SyncRssSearchTree);
            SetGuiStateFeedback(String.Empty, ApplicationTrayState.NormalIdle);
            _faviconsDownloaded = false;
        } 
        internal  void SetDefaultExpansionTreeNodeState()
        {
            foreach (TreeFeedsNodeBase node in _roots)
                node.Expanded = true;
        }
 
        private  void PlayEnclosure(DownloadItem enclosure)
        {
            if (enclosure == null)
                return;
            string fileName = Path.Combine(enclosure.TargetFolder, enclosure.File.LocalName);
            if (string.IsNullOrEmpty(fileName))
                return;
            try
            {
                using (Process p = new Process())
                {
                    p.StartInfo.CreateNoWindow = true;
                    p.StartInfo.FileName = fileName;
                    p.Start();
                }
            }
            catch (Exception ex)
            {
                Win32Exception ex32 = ex as Win32Exception;
                if ((ex32 == null) || (ex32.NativeErrorCode != 1223))
                {
                    owner.MessageError(SR.ExceptionProcessStartToPlayEnclosure(fileName, ex.Message));
                    RssBanditApplication.PublishException(ex);
                }
            }
        }
 
        private  Image LoadFavicon(string name)
        {
            Image favicon = null;
            try
            {
                if (!string.IsNullOrEmpty(name))
                {
                    if (_favicons.ContainsKey(name))
                    {
                        return _favicons[name];
                    }
                    string location = Path.Combine(RssBanditApplication.GetFeedFileCachePath(), name);
                    if (! File.Exists(location))
                        return null;
                    if (String.Compare(Path.GetExtension(location), ".ico", true) == 0)
                        try
                        {
                            using (Icon ico = new Icon(location, new Size(16, 16)))
                            {
                                if(!Win32.IsOSAtLeastWindowsVista)
                                {
                                    if (ico.Width != ico.Height)
                                        return null;
                                }
                                favicon = ResizeFavicon(ico.ToBitmap(), null);
                            }
                        }
                        catch (Exception e)
                        {
                            _log.Debug("LoadFavicon(" + name + ") failed with error:", e);
                        }
                    if (favicon == null)
                        favicon = ResizeFavicon(Image.FromFile(location, true), location);
                    lock (_favicons)
                    {
                        if (!_favicons.ContainsKey(name))
                            _favicons.Add(name, favicon);
                    }
                }
            }
            catch (Exception e)
            {
                _log.Debug("LoadFavicon(" + name + ") failed with error:", e);
            }
            return favicon;
        }
 
        private static  Image ResizeFavicon(Image toResize, string location)
        {
            if ((toResize.Height == 16) && (toResize.Width == 16))
            {
                return toResize;
            }
            else
            {
                Bitmap result = new Bitmap(16, 16, toResize.PixelFormat);
                result.SetResolution(toResize.HorizontalResolution, toResize.VerticalResolution);
                using (Graphics g = Graphics.FromImage(result))
                {
                    g.DrawImage(toResize, 0, 0, 16, 16);
                }
                toResize.Dispose();
                if (location != null)
                    result.Save(location);
                return result;
            }
        }
 
        private  void PopulateTreeRssSearchScope()
        {
            if (searchPanel != null)
                searchPanel.PopulateTreeRssSearchScope(GetRoot(RootFolderType.MyFeeds), _treeImages);
        }
 
        public  void DetailTabNavigateToUrl(string url, string tab, bool createNewTab, bool setFocus)
        {
            Debug.Assert(!InvokeRequired, "DetailTabNavigateToUrl() from Non-UI Thread called");
            if (owner.Preferences.OpenNewTabsInBackground)
            {
                setFocus = false;
            }
            if (string.IsNullOrEmpty(url))
                return;
            if (url == "about:blank" && !createNewTab)
                return;
            if (string.IsNullOrEmpty(tab))
                tab = "Web Link";
            HtmlControl hc = null;
            DockControl previousDoc, currentDoc;
            previousDoc = currentDoc = _docContainer.ActiveDocument;
            ITabState docState = (ITabState) currentDoc.Tag;
            if (!docState.CanClose)
            {
                if (!createNewTab && owner.Preferences.ReuseFirstBrowserTab)
                {
                    foreach (DockControl c in currentDoc.LayoutSystem.Controls)
                    {
                        if (c != currentDoc)
                        {
                            hc = (HtmlControl) c.Controls[0];
                            break;
                        }
                    }
                }
            }
            else if (!createNewTab)
            {
                hc = (HtmlControl) _docContainer.ActiveDocument.Controls[0];
            }
            if (hc == null)
            {
                hc = CreateAndInitIEControl(tab);
                DockControl doc = new DockControl(hc, tab);
                doc.Tag = new WebTabState(tab, url);
                hc.Tag = doc;
                _docContainer.AddDocument(doc);
                if (Win32.IsOSAtLeastWindowsXP)
                    ColorEx.ColorizeOneNote(doc, ++_webTabCounter);
                if (setFocus)
                {
                    hc.Activate();
                    currentDoc = (DockControl) hc.Tag;
                }
                else
                    doc.Activate();
            }
            else
            {
                currentDoc = (DockControl) hc.Tag;
            }
            currentDoc.Activate();
            _docContainer.ActiveDocument = (setFocus ? currentDoc : previousDoc);
            hc.Navigate(url);
        }
 
        private  HtmlControl CreateAndInitIEControl(string tabName)
        {
            HtmlControl hc = new HtmlControl();
            ComponentResourceManager resources = new ComponentResourceManager(typeof (WinGuiMain));
            hc.BeginInit();
            hc.AllowDrop = true;
            resources.ApplyResources(hc, "htmlDetail");
            hc.Name = tabName;
            hc.OcxState = ((AxHost.State) (resources.GetObject("htmlDetail.OcxState")));
            helpProvider1.SetShowHelp(hc, ((bool) (resources.GetObject("htmlDetail.ShowHelp"))));
            hc.ContainingControl = this;
            hc.EndInit();
            hc.ScriptEnabled = owner.Preferences.BrowserJavascriptAllowed;
            hc.JavaEnabled = owner.Preferences.BrowserJavaAllowed;
            hc.ActiveXEnabled = owner.Preferences.BrowserActiveXAllowed;
            HtmlControl.SetInternetFeatureEnabled(
                InternetFeatureList.FEATURE_RESTRICT_ACTIVEXINSTALL,
                SetFeatureFlag.SET_FEATURE_ON_THREAD_INTERNET,
                hc.ActiveXEnabled);
            hc.BackroundSoundEnabled = owner.Preferences.BrowserBGSoundAllowed;
            hc.VideoEnabled = owner.Preferences.BrowserVideoAllowed;
            hc.ImagesDownloadEnabled = owner.Preferences.BrowserImagesAllowed;
            hc.SilentModeEnabled = false;
            hc.Border3d = true;
            hc.StatusTextChanged += OnWebStatusTextChanged;
            hc.BeforeNavigate += OnWebBeforeNavigate;
            hc.NavigateComplete += OnWebNavigateComplete;
            hc.DocumentComplete += OnWebDocumentComplete;
            hc.TitleChanged += OnWebTitleChanged;
            hc.CommandStateChanged += OnWebCommandStateChanged;
            hc.NewWindow += OnWebNewWindow;
            hc.ProgressChanged += OnWebProgressChanged;
            hc.TranslateAccelerator += OnWebTranslateAccelerator;
            hc.OnQuit += OnWebQuit;
            return hc;
        }
 
        private static  ITabState GetTabStateFor(HtmlControl control)
        {
            if (control == null) return null;
            DockControl doc = (DockControl) control.Tag;
            if (doc == null) return null;
            ITabState state = (ITabState) doc.Tag;
            return state;
        }
 
        private  bool UrlRequestHandledExternally(string url, bool forceNewTab)
        {
            if (forceNewTab || BrowserBehaviorOnNewWindow.OpenNewTab == owner.Preferences.BrowserOnNewWindow)
            {
                return false;
            }
            else if (BrowserBehaviorOnNewWindow.OpenDefaultBrowser == owner.Preferences.BrowserOnNewWindow)
            {
                owner.NavigateToUrlInExternalBrowser(url);
            }
            else if (BrowserBehaviorOnNewWindow.OpenWithCustomExecutable == owner.Preferences.BrowserOnNewWindow)
            {
                try
                {
                    Process.Start(owner.Preferences.BrowserCustomExecOnNewWindow, url);
                }
                catch (Exception ex)
                {
                    if (
                        owner.MessageQuestion(
                            SR.ExceptionStartBrowserCustomExecMessage(owner.Preferences.BrowserCustomExecOnNewWindow,
                                                                      ex.Message, url)) == DialogResult.Yes)
                    {
                        DetailTabNavigateToUrl(url, null, true, true);
                    }
                }
            }
            else
            {
                Debug.Assert(false, "Unhandled BrowserBehaviorOnNewWindow");
            }
            return true;
        }
 
        public  void RequestBrowseAction(BrowseAction action)
        {
            if (_docContainer.ActiveDocument == _docFeedDetails)
            {
                switch (action)
                {
                    case BrowseAction.NavigateBack:
                        NavigateToHistoryEntry(_feedItemImpressionHistory.GetPrevious());
                        break;
                    case BrowseAction.NavigateForward:
                        NavigateToHistoryEntry(_feedItemImpressionHistory.GetNext());
                        break;
                    case BrowseAction.DoRefresh:
                        OnTreeFeedAfterSelectManually(TreeSelectedFeedsNode);
                        break;
                    default:
                        break;
                }
            }
            else
            {
                HtmlControl wb = (HtmlControl) _docContainer.ActiveDocument.Controls[0];
                try
                {
                    switch (action)
                    {
                        case BrowseAction.NavigateCancel:
                            wb.Stop();
                            break;
                        case BrowseAction.NavigateBack:
                            wb.GoBack();
                            break;
                        case BrowseAction.NavigateForward:
                            wb.GoForward();
                            break;
                        case BrowseAction.DoRefresh:
                            object level = 2;
                            wb.Refresh2(ref level);
                            break;
                        default:
                            break;
                    }
                }
                catch
                {
                    ;
                }
            }
            DeactivateWebProgressInfo();
        }
 
        public  void RefreshListviewContextMenu()
        {
            NewsItem item = null;
            IList selectedItems = GetSelectedLVItems();
            if (selectedItems.Count > 0)
                item = ((ThreadedListViewItem) selectedItems[0]).Key as NewsItem;
            if ((selectedItems.Count == 1) && (item != null))
            {
                RefreshListviewContextMenu(item);
            }
            else
            {
                RefreshListviewContextMenu(null);
            }
        }
 
        public  void RefreshListviewContextMenu(NewsItem item)
        {
            if (item != null)
            {
                owner.Mediator.SetVisible("+cmdWatchItemComments", "+cmdFeedItemPostReply");
                owner.Mediator.SetEnabled("+cmdCopyNewsItem");
                if (listFeedItems.Visible)
                {
                    owner.Mediator.SetVisible("+cmdColumnChooserMain");
                }
                else
                {
                    owner.Mediator.SetVisible("-cmdColumnChooserMain");
                }
                if (item.BeenRead)
                {
                    owner.Mediator.SetVisible("+cmdMarkSelectedFeedItemsUnread", "-cmdMarkSelectedFeedItemsRead");
                }
                else
                {
                    owner.Mediator.SetVisible("-cmdMarkSelectedFeedItemsUnread", "+cmdMarkSelectedFeedItemsRead");
                }
                _listContextMenuDownloadAttachmentsSeparator.Visible = false;
                owner.Mediator.SetVisible("-cmdDownloadAttachment");
                if (item.Enclosures != null && item.Enclosures.Count > 0)
                {
                    _listContextMenuDownloadAttachmentsSeparator.Visible = true;
                    owner.Mediator.SetVisible("+cmdDownloadAttachment");
                    _listContextMenuDownloadAttachment.MenuItems.Clear();
                    foreach (Enclosure enc in item.Enclosures)
                    {
                        int index = enc.Url.LastIndexOf("/");
                        string fileName;
                        if ((index != -1) && (index + 1 < enc.Url.Length))
                        {
                            fileName = enc.Url.Substring(index + 1);
                        }
                        else
                        {
                            fileName = enc.Url;
                        }
                        AppContextMenuCommand downloadFileMenuItem =
                            new AppContextMenuCommand("cmdDownloadAttachment<" + fileName,
                                                      owner.Mediator, new ExecuteCommandHandler(CmdDownloadAttachment),
                                                      fileName, fileName, _shortcutHandler);
                        _listContextMenuDownloadAttachment.MenuItems.AddRange(new MenuItem[] {downloadFileMenuItem});
                    }
                }
                owner.Mediator.SetChecked("-cmdWatchItemComments");
                if (string.IsNullOrEmpty(item.CommentRssUrl) && (item.CommentCount == NewsItem.NoComments))
                {
                    owner.Mediator.SetEnabled("-cmdWatchItemComments");
                }
                else
                {
                    owner.Mediator.SetEnabled("+cmdWatchItemComments");
                    if (item.WatchComments)
                    {
                        owner.Mediator.SetChecked("+cmdWatchItemComments");
                    }
                }
            }
            else
            {
                _listContextMenuDownloadAttachmentsSeparator.Visible = false;
                owner.Mediator.SetVisible("+cmdMarkSelectedFeedItemsUnread", "+cmdMarkSelectedFeedItemsRead");
                owner.Mediator.SetVisible("-cmdWatchItemComments", "-cmdColumnChooserMain", "-cmdFeedItemPostReply",
                                          "-cmdDownloadAttachment");
                owner.Mediator.SetEnabled("-cmdCopyNewsItem");
            }
            if (CurrentSelectedFeedsNode is WasteBasketNode)
            {
                owner.Mediator.SetVisible("+cmdRestoreSelectedNewsItems");
            }
            else
            {
                owner.Mediator.SetVisible("-cmdRestoreSelectedNewsItems");
            }
        }
 
        public  void RefreshTreeFeedContextMenus(TreeFeedsNodeBase feedsNode)
        {
            owner.Mediator.SetEnabled(false, "cmdColumnChooserResetToDefault");
            if (feedsNode.Type == FeedNodeType.Feed || feedsNode.Type == FeedNodeType.Category)
            {
                owner.Mediator.SetEnabled(true, "cmdColumnChooserResetToDefault");
                owner.Mediator.SetEnabled(
                    "+cmdFlagNewsItem", "+cmdNavigateToFeedHome", "+cmdNavigateToFeedCosmos",
                    "+cmdViewSourceOfFeed", "+cmdValidateFeed");
                if (RssHelper.IsNntpUrl(feedsNode.DataKey))
                {
                    _feedInfoContextMenu.Enabled = false;
                }
                else
                {
                    _feedInfoContextMenu.Enabled = true;
                }
            }
            else if (feedsNode.Type == FeedNodeType.SmartFolder)
            {
                owner.Mediator.SetEnabled(
                    "-cmdFlagNewsItem", "-cmdNavigateToFeedHome", "-cmdNavigateToFeedCosmos",
                    "-cmdViewSourceOfFeed", "-cmdValidateFeed");
                if ((feedsNode as FlaggedItemsNode) != null)
                    owner.Mediator.SetEnabled("+cmdFlagNewsItem");
            }
            else if (feedsNode.Type == FeedNodeType.Finder)
            {
                owner.Mediator.SetEnabled("-cmdDeleteAllFinders", "+cmdDeleteFinder", "+cmdShowFinderProperties",
                                          "+cmdFlagNewsItem", "-cmdSubscribeToFinderResult");
                FinderNode agfn = feedsNode as FinderNode;
                if (agfn != null && agfn == _searchResultNode && agfn.Finder != null)
                {
                    bool extResult = !string.IsNullOrEmpty(agfn.Finder.ExternalSearchUrl);
                    owner.Mediator.SetEnabled(extResult, "cmdSubscribeToFinderResult");
                    owner.Mediator.SetEnabled(extResult && agfn.Finder.ExternalResultMerged, "cmdShowFinderProperties");
                }
                if (agfn != null && agfn.Finder != null)
                {
                    owner.Mediator.SetChecked(!agfn.Finder.ShowFullItemContent, "cmdFinderShowFullItemText");
                }
            }
            else if (feedsNode.Type == FeedNodeType.FinderCategory)
            {
                owner.Mediator.SetEnabled("+cmdDeleteAllFinders", "+cmdDeleteFinder", "-cmdShowFinderProperties");
            }
            else if (feedsNode.Type == FeedNodeType.Root)
            {
                if ((feedsNode as FinderRootNode) != null)
                    owner.Mediator.SetEnabled("+cmdDeleteAllFinders", "-cmdDeleteFinder", "-cmdShowFinderProperties");
            }
        }
 
        private  void MoveFeedDetailsToFront()
        {
            if (_docContainer.ActiveDocument != _docFeedDetails)
                _docContainer.ActiveDocument = _docFeedDetails;
        }
 
        private  void RefreshDocumentState(Control doc)
        {
            if (doc == null)
                return;
            ITabState state = doc.Tag as ITabState;
            if (state == null)
                return;
            if (state.CanClose)
            {
                doc.Text = StringHelper.ShortenByEllipsis(state.Title, 30);
            }
            if (_docContainer.ActiveDocument == doc)
            {
                SetTitleText(state.Title);
                UrlText = state.Url;
                historyMenuManager.ReBuildBrowserGoBackHistoryCommandItems(state.GoBackHistoryItems(10));
                historyMenuManager.ReBuildBrowserGoForwardHistoryCommandItems(state.GoForwardHistoryItems(10));
                owner.Mediator.SetEnabled(state.CanGoBack, "cmdBrowserGoBack");
                owner.Mediator.SetEnabled(state.CanGoForward, "cmdBrowserGoForward");
            }
        }
 
        public  void SetGuiStateINetConnected(bool connected)
        {
            try
            {
                StatusBarPanel p = statusBarConnectionState;
                if (connected)
                {
                    p.Icon = Resource.LoadIcon("Resources.Connected.ico");
                }
                else
                {
                    p.Icon = Resource.LoadIcon("Resources.Disconnected.ico");
                }
            }
            catch
            {
            }
            _status.Refresh();
        }
 
        public  void SetGuiStateFeedback(string text)
        {
            try
            {
                StatusBarPanel p = statusBarRssParser;
                if (!p.Text.Equals(text))
                {
                    p.Text = p.ToolTipText = text;
                    if (text.Length == 0 && p.Icon != null)
                    {
                        p.Icon = null;
                    }
                    _status.Refresh();
                }
            }
            catch
            {
            }
        }
 
        public  void SetGuiStateFeedback(string text, ApplicationTrayState state)
        {
            try
            {
                StatusBarPanel p = statusBarRssParser;
                if (state == ApplicationTrayState.NormalIdle)
                {
                    _timerResetStatus.Start();
                    if (!string.IsNullOrEmpty(text))
                    {
                        SetGuiStateFeedback(text);
                    }
                }
                else
                {
                    _timerResetStatus.Stop();
                    SetGuiStateFeedback(text);
                    _trayManager.SetState(state);
                    if (state == ApplicationTrayState.BusyRefreshFeeds)
                    {
                        if (p.Icon == null)
                        {
                            p.Icon = Resource.LoadIcon("Resources.feedRefresh.ico");
                            _status.Refresh();
                        }
                    }
                    else
                    {
                        if (p.Icon != null)
                        {
                            p.Icon = null;
                            _status.Refresh();
                        }
                    }
                }
            }
            catch
            {
            }
        }
 
        public  void SetBrowserStatusBarText(string text)
        {
            try
            {
                StatusBarPanel p = statusBarBrowser;
                if (!p.Text.Equals(text))
                {
                    p.Text = text;
                    _status.Refresh();
                }
            }
            catch
            {
            }
        }
 
        public  void SetSearchStatusText(string text)
        {
            SetGuiStateFeedback(text);
        }
 
        public  void UpdateCategory(bool forceRefresh)
        {
            TreeFeedsNodeBase selectedNode = CurrentSelectedFeedsNode;
            if (selectedNode == null) return;
            owner.BeginRefreshCategoryFeeds(selectedNode.CategoryStoreName, forceRefresh);
        }
 
        public  void UpdateAllCommentFeeds(bool force_download)
        {
            if (_timerRefreshCommentFeeds.Enabled)
                _timerRefreshCommentFeeds.Stop();
            owner.BeginRefreshCommentFeeds(force_download);
        }
 
        public  void UpdateAllFeeds(bool force_download)
        {
            if (_timerRefreshFeeds.Enabled)
                _timerRefreshFeeds.Stop();
            TreeFeedsNodeBase root = GetRoot(RootFolderType.MyFeeds);
            _lastUnreadFeedItemCountBeforeRefresh = root.UnreadCount;
            owner.BeginRefreshFeeds(force_download);
        }
 
        public  void OnAllAsyncUpdateCommentFeedsFinished()
        {
            if (!_timerRefreshCommentFeeds.Enabled)
                _timerRefreshCommentFeeds.Start();
        }
 
        public  void OnAllAsyncUpdateFeedsFinished()
        {
            if (!_timerRefreshFeeds.Enabled)
                _timerRefreshFeeds.Start();
            if (!_faviconsDownloaded && owner.Preferences.UseFavicons && owner.FeedHandler.DownloadIntervalReached)
            {
                try
                {
                    owner.FeedHandler.RefreshFavicons();
                }
                finally
                {
                    _faviconsDownloaded = true;
                }
            }
        }
 
        private  void OnApplicationIdle(object sender, EventArgs e)
        {
            if (IdleTask.IsTask(IdleTasks.IndexAllItems))
            {
                IdleTask.RemoveTask(IdleTasks.IndexAllItems);
                try
                {
                    owner.FeedHandler.SearchHandler.CheckIndex(true);
                }
                catch (Exception ex)
                {
                    Trace.WriteLine("LuceneIndexer failed: " + ex);
                }
            }
        }
 
        public  string CategoryOfSelectedNode()
        {
            TreeFeedsNodeBase tn = CurrentSelectedFeedsNode;
            if (tn != null)
            {
                if (tn.Type == FeedNodeType.Feed)
                {
                    INewsFeed f = owner.GetFeed(tn.DataKey);
                    if (f != null)
                    {
                        return f.category;
                    }
                    else
                    {
                        return tn.CategoryStoreName;
                    }
                }
                else if (tn.Type == FeedNodeType.Category || tn.Type == FeedNodeType.Root)
                {
                    return tn.CategoryStoreName;
                }
                else
                {
                    return RssBanditApplication.DefaultCategory;
                }
            }
            else
                return RssBanditApplication.DefaultCategory;
        }
 
           
        public  void InitiateRenameFeedOrCategory()
        {
            if (CurrentSelectedFeedsNode != null)
                DoEditTreeNodeLabel();
        }
 
        public  bool NodeEditingActive
        {
            get
            {
                return (CurrentSelectedFeedsNode != null && CurrentSelectedFeedsNode.IsEditing);
            }
        }
 
        private static  bool NodeIsChildOf(UltraTreeNode tn, UltraTreeNode parent)
        {
            if (parent == null)
                return false;
            UltraTreeNode p = tn.Parent;
            while (p != null)
            {
                if (p == parent) return true;
                p = p.Parent;
            }
            return false;
        }
 
        public  void UpdateFavicon(string favicon, StringCollection feedUrls)
        {
            Image icon = null;
            if (!string.IsNullOrEmpty(favicon))
            {
                string location = Path.Combine(RssBanditApplication.GetFeedFileCachePath(), favicon);
                if (String.Compare(Path.GetExtension(location), ".ico", true) == 0)
                    try
                    {
                        using (Icon ico = new Icon(location, new Size(16, 16)))
                        {
                            if (Win32.IsOSAtLeastWindowsVista)
                            {
                                icon = ResizeFavicon(ico.ToBitmap(), null);
                            }
                            else
                            {
                                if (ico.Width == ico.Height)
                                    icon = ResizeFavicon(ico.ToBitmap(), null);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        _log.Debug("UpdateFavicon(" + location + ") failed with error:", e);
                    }
                else
                    try
                    {
                        icon = ResizeFavicon(Image.FromFile(location, true), location);
                    }
                    catch (Exception e)
                    {
                        _log.Debug("UpdateFavicon() failed", e);
                    }
            }
            if (feedUrls != null)
            {
                foreach (string feedUrl in feedUrls)
                {
                    TreeFeedsNodeBase tn = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), feedUrl);
                    if (tn == null)
                    {
                        _log.Debug("TreeHelper.FindNode() could not find matching tree node for " + feedUrl);
                    }
                    else
                    {
                        tn.SetIndividualImage(icon);
                    }
                }
            }
            if (icon != null)
            {
                lock (_favicons)
                {
                    if (!_favicons.ContainsKey(favicon))
                        _favicons.Add(favicon, icon);
                }
                owner.SubscriptionModified(NewsFeedProperty.General);
            }
        }
 
        public  void ApplyFavicons()
        {
            try
            {
                string[] keys;
                lock (owner.FeedHandler.FeedsTable)
                {
                    keys = new string[owner.FeedHandler.FeedsTable.Count];
                    if (owner.FeedHandler.FeedsTable.Count > 0)
                        owner.FeedHandler.FeedsTable.Keys.CopyTo(keys, 0);
                }
                for (int i = 0, len = keys.Length; i < len; i++)
                {
                    string feedUrl = keys[i];
                    INewsFeed f = null;
                    if (!owner.FeedHandler.FeedsTable.TryGetValue(feedUrl,out f))
                    {
                        continue;
                    }
                    if (owner.Preferences.UseFavicons)
                    {
                        if (string.IsNullOrEmpty(f.favicon))
                        {
                            continue;
                        }
                        string location = Path.Combine(RssBanditApplication.GetFeedFileCachePath(), f.favicon);
                        Image icon = null;
                        if (_favicons.ContainsKey(f.favicon))
                        {
                            icon = _favicons[f.favicon];
                        }
                        else if(File.Exists(location))
                        {
                            try
                            {
                                icon = ResizeFavicon(Image.FromFile(location), location);
                                lock (_favicons)
                                {
                                    if (!_favicons.ContainsKey(f.favicon))
                                        _favicons.Add(f.favicon, icon);
                                }
                            }
                            catch (Exception e)
                            {
                                _log.Error("Error in ApplyFavicons(): {0}", e);
                            }
                        }
                        if (icon != null)
                        {
                            TreeFeedsNodeBase tn = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), feedUrl);
                            if (tn != null)
                            {
                                tn.SetIndividualImage(icon);
                            }
                        }
                    }
                    else
                    {
                        TreeFeedsNodeBase tn = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), feedUrl);
                        if (tn != null)
                        {
                            SetSubscriptionNodeState(f, tn, FeedProcessingState.Normal);
                        }
                    }
                }
            }
            catch (InvalidOperationException ioe)
            {
                _log.Error("ApplyFavicons - InvalidOperationException: {0}", ioe);
            }
        }
 
        public  void UpdateCommentFeed(Uri feedUri, Uri newFeedUri)
        {
            IList<NewsItem> items;
<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442345776/fstmerge_var1_5417889501058063797
            string feedUrl = feedUri.CanonicalizedUri();
            INewsFeed feed;
=======
            string feedUrl = feedUri.CanonicalizedUri();
            NewsFeed feed;
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442345776/fstmerge_var2_2378555652216294059
            TreeFeedsNodeBase tn;
            NewsItem item = null;
            bool modified = false;
            if (newFeedUri != null)
            {
                items = owner.CommentFeedsHandler.GetCachedItemsForFeed(newFeedUri.CanonicalizedUri());
            }
            else
            {
                items = owner.CommentFeedsHandler.GetCachedItemsForFeed(feedUrl);
            }
            int commentCount = (items.Count == 0 ? NewsItem.NoComments : items.Count);
            if (!owner.CommentFeedsHandler.FeedsTable.ContainsKey(feedUrl) && (feedUri.IsFile || feedUri.IsUnc))
            {
                feedUrl = feedUri.LocalPath;
            }
            owner.CommentFeedsHandler.FeedsTable.TryGetValue(feedUrl, out feed);
            if (feed != null && feed.Tag != null)
            {
                NewsFeed itemFeed = (NewsFeed) feed.Tag;
                FeedInfo itemFeedInfo = owner.FeedHandler.GetFeedInfo(itemFeed.link) as FeedInfo;
                tn = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), itemFeed);
                if (tn != null && itemFeedInfo != null)
                {
                    lock (itemFeedInfo.ItemsList)
                    {
                        foreach (NewsItem ni in itemFeedInfo.ItemsList)
                        {
                            if (!string.IsNullOrEmpty(ni.CommentRssUrl) &&
                                feedUrl.Equals(ni.CommentRssUrl))
                            {
                                item = ni;
                                if (items.Contains(item))
                                {
                                    commentCount--;
                                }
                                break;
                            }
                        }
                        if (item == null)
                        {
                            owner.CommentFeedsHandler.DeleteFeed(feedUrl);
                            owner.WatchedItemsFeed.Remove(feedUrl);
                            return;
                        }
                        else if (item.WatchComments)
                        {
                            if (commentCount > item.CommentCount)
                            {
                                itemFeed.containsNewComments = item.HasNewComments = true;
                                item.CommentCount = commentCount;
                                modified = true;
                            }
                            if (newFeedUri != null && newFeedUri != feedUri)
                            {
                                if (newFeedUri.IsFile || newFeedUri.IsUnc)
                                    feedUrl = newFeedUri.LocalPath;
                                else
                                    feedUrl = newFeedUri.ToString();
                                item.CommentRssUrl = feedUrl;
                                modified = true;
                            }
                        }
                    }
                    if ((DateTime.Now.Subtract(item.Date) > SevenDays))
                    {
                        feed.refreshrateSpecified = true;
                        feed.refreshrate = 12*60*60*1000;
                    }
                    if (itemFeed.containsNewComments)
                    {
                        UpdateCommentStatus(tn, itemFeedInfo.ItemsList, false);
                        bool categorized = false;
                        TreeFeedsNodeBase ftnSelected = TreeSelectedFeedsNode;
                        if (ftnSelected.Type == FeedNodeType.Category && NodeIsChildOf(tn, ftnSelected))
                            categorized = true;
                        if (tn.Selected || categorized)
                        {
                            ThreadedListViewItem lvItem = GetListViewItem(item.Id);
                            if (lvItem != null)
                            {
                                ApplyStyles(lvItem);
                            }
                        }
                    }
                    if (modified)
                    {
                        owner.FeedHandler.ApplyFeedModifications(itemFeed.link);
                    }
                }
            }
        }
 
        public  void UpdateFeed(string feedUrl, Uri newFeedUri, bool modified)
        {
            Uri feedUri = null;
            try
            {
                feedUri = new Uri(feedUrl);
            }
            catch (Exception)
            {
            }
            UpdateFeed(feedUri, newFeedUri, modified);
        }
 
        public  void UpdateFeed(Uri feedUri, Uri newFeedUri, bool modified)
        {
            if (feedUri == null)
                return;
            IList<NewsItem> items;
            IList<NewsItem> unread = null;
<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442345903/fstmerge_var1_4786681500570525664
            string feedUrl = feedUri.CanonicalizedUri();
            INewsFeed feed;
=======
            string feedUrl = feedUri.CanonicalizedUri();
            NewsFeed feed;
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442345903/fstmerge_var2_2103083393805736355
            TreeFeedsNodeBase tn;
            if (newFeedUri != null)
            {
                items = owner.FeedHandler.GetCachedItemsForFeed(newFeedUri.CanonicalizedUri());
            }
            else
            {
                items = owner.FeedHandler.GetCachedItemsForFeed(feedUrl);
            }
            if (!owner.FeedHandler.FeedsTable.ContainsKey(feedUrl) && (feedUri.IsFile || feedUri.IsUnc))
            {
                feedUrl = feedUri.LocalPath;
            }
            feed = null;
            if (owner.FeedHandler.FeedsTable.TryGetValue(feedUrl, out feed) && feed != null)
            {
                tn = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), feed);
            }
            else
            {
                tn = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), feedUrl);
            }
            if (tn != null)
            {
                if (newFeedUri != null && newFeedUri != feedUri)
                {
                    if (newFeedUri.IsFile || newFeedUri.IsUnc)
                        feedUrl = newFeedUri.LocalPath;
                    else
                        feedUrl = newFeedUri.CanonicalizedUri();
                    tn.DataKey = feedUrl;
                    feed = owner.GetFeed(feedUrl);
                    if (feed != null)
                        feed.Tag = tn;
                }
                if (feed != null)
                {
                    SetSubscriptionNodeState(feed, tn, FeedProcessingState.Normal);
                    if (feed.containsNewMessages)
                    {
                        if (modified)
                            owner.FeedWasModified(feed, NewsFeedProperty.FeedItemReadState);
                        if ((DisplayFeedAlertWindow.All == owner.Preferences.ShowAlertWindow ||
                             (DisplayFeedAlertWindow.AsConfiguredPerFeed == owner.Preferences.ShowAlertWindow &&
                              feed.alertEnabled)) &&
                            modified)
                        {
                            List<NewsItem> sortedItems = new List<NewsItem>(items);
                            sortedItems.Sort(RssHelper.GetComparer());
                            toastNotifier.Alert(tn.Text, tn.UnreadCount, sortedItems);
                        }
                        unread = FilterUnreadFeedItems(items);
                        UnreadItemsNodeRemoveItems(unread);
                        UnreadItemsNode.Items.AddRange(unread);
                        UpdateTreeNodeUnreadStatus(tn, unread.Count);
                        UnreadItemsNode.UpdateReadStatus();
                    }
                    if (feed.containsNewComments)
                    {
                        if (modified)
                            owner.FeedWasModified(feed, NewsFeedProperty.FeedItemCommentCount);
                        UpdateCommentStatus(tn, items, false);
                    }
                }
                bool categorized = false;
                TreeFeedsNodeBase ftnSelected = TreeSelectedFeedsNode;
                if (ftnSelected.Type == FeedNodeType.Category && NodeIsChildOf(tn, ftnSelected))
                    categorized = true;
                if (ftnSelected is UnreadItemsNode && unread != null && unread.Count > 0)
                {
                    modified = categorized = true;
                    items = unread;
                }
                if (modified && (tn.Selected || categorized))
                {
                    NewsItem itemSelected = null;
                    if (listFeedItems.SelectedItems.Count > 0)
                        itemSelected = (NewsItem) ((ThreadedListViewItem) listFeedItems.SelectedItems[0]).Key;
                    PopulateListView(tn, items, false, categorized, ftnSelected);
                    if (itemSelected == null || (!categorized && !itemSelected.Feed.link.Equals(tn.DataKey)))
                    {
                        CurrentSelectedFeedItem = null;
                        RefreshFeedDisplay(tn, false);
                    }
                    else
                    {
                        ReSelectListViewItem(itemSelected);
                    }
                }
                UpdateFindersReadStatus(items);
            }
            else
            {
                _log.Info("UpdateFeed() could not find node for '" + feedUri + "'...");
            }
        }
 
        private  void UpdateFindersReadStatus(IEnumerable<NewsItem> items)
        {
            if (_searchResultNode != null && !_searchResultNode.AnyUnread)
            {
                SearchCriteriaCollection sc = _searchResultNode.Finder.SearchCriterias;
                foreach (NewsItem item in items)
                {
                    if (!item.BeenRead && sc.Match(item))
                    {
                        _searchResultNode.UpdateReadStatus(_searchResultNode, true);
                        break;
                    }
                }
            }
            foreach (RssFinder finder in owner.FinderList)
            {
                if (finder.Container != null && !finder.Container.AnyUnread)
                {
                    SearchCriteriaCollection sc = finder.SearchCriterias;
                    foreach (NewsItem item in items)
                    {
                        if (!item.BeenRead && sc.Match(item))
                        {
                            finder.Container.UpdateReadStatus(finder.Container, true);
                            break;
                        }
                    }
                }
            }
        }
 
        private  void ResetFindersReadStatus()
        {
            if (_searchResultNode != null)
            {
                UpdateTreeNodeUnreadStatus(_searchResultNode, 0);
            }
            foreach (RssFinder finder in owner.FinderList)
            {
                if (finder.Container != null)
                    UpdateTreeNodeUnreadStatus(finder.Container, 0);
            }
        }
 
        public  void NewCategory()
        {
            if (CurrentSelectedFeedsNode != null && CurrentSelectedFeedsNode.AllowedChild(FeedNodeType.Category))
            {
                TreeFeedsNodeBase curFeedsNode = CurrentSelectedFeedsNode;
                int i = 1;
                string s = SR.GeneralNewItemText;
                while (TreeHelper.FindChildNode(curFeedsNode, s, FeedNodeType.Category) != null)
                {
                    s = SR.GeneralNewItemTextWithCounter(i++);
                }
                TreeFeedsNodeBase newFeedsNode = new CategoryNode(s,
                                                                  Resource.SubscriptionTreeImage.SubscriptionsCategory,
                                                                  Resource.SubscriptionTreeImage.
                                                                      SubscriptionsCategoryExpanded,
                                                                  _treeCategoryContextMenu);
                curFeedsNode.Nodes.Add(newFeedsNode);
                newFeedsNode.BringIntoView();
                TreeSelectedFeedsNode = newFeedsNode;
                s = newFeedsNode.CategoryStoreName;
                if (!owner.FeedHandler.HasCategory(s))
                {
                    owner.FeedHandler.Categories.Add(s);
                    owner.SubscriptionModified(NewsFeedProperty.FeedCategoryAdded);
                }
                if (!treeFeeds.Focused) treeFeeds.Focus();
                newFeedsNode.BeginEdit();
            }
        }
 
        internal  void UpdateTreeNodeUnreadStatus(TreeFeedsNodeBase node, int newCount)
        {
            if (node != null)
            {
                node.UpdateReadStatus(node, newCount);
                if (node.Selected)
                {
                    SetDetailHeaderText(node);
                }
            }
        }
 
        public  void MarkSelectedNodeRead(TreeFeedsNodeBase startNode)
        {
            TreeFeedsNodeBase selectedNode = startNode ?? CurrentSelectedFeedsNode;
            if (selectedNode == null) return;
            INewsFeed f = null;
            if (selectedNode.Type == FeedNodeType.Feed)
                f = owner.GetFeed(selectedNode.DataKey);
            if (f != null)
            {
                UnreadItemsNodeRemoveItems(f);
                owner.FeedHandler.MarkAllCachedItemsAsRead(f);
                owner.FeedWasModified(f, NewsFeedProperty.FeedItemReadState);
                UpdateTreeNodeUnreadStatus(selectedNode, 0);
            }
            bool selectedIsChild = NodeIsChildOf(TreeSelectedFeedsNode, selectedNode);
            bool isSmartOrAggregated = (selectedNode.Type == FeedNodeType.Finder ||
                                        selectedNode.Type == FeedNodeType.SmartFolder);
            if (listFeedItems.Items.Count > 0)
            {
                listFeedItems.BeginUpdate();
                for (int i = 0; i < listFeedItems.Items.Count; i++)
                {
                    ThreadedListViewItem lvi = listFeedItems.Items[i];
                    NewsItem newsItem = (NewsItem) lvi.Key;
                    if (newsItem != null &&
                        (newsItem.Feed == f || selectedIsChild || selectedNode == TreeSelectedFeedsNode ||
                         isSmartOrAggregated || lvi.IndentLevel > 0 || selectedNode.Type == FeedNodeType.Root))
                    {
                        if ((lvi.ImageIndex%2) != 0)
                            lvi.ImageIndex--;
                        ApplyStyles(lvi, true);
                        if (!newsItem.BeenRead)
                        {
                            newsItem.BeenRead = true;
                            UnreadItemsNode.Remove(newsItem);
                            if (lvi.IndentLevel > 0 || selectedNode.Type == FeedNodeType.Finder)
                            {
                                selectedNode.UpdateReadStatus(
                                    TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), newsItem), -1);
                            }
                            else if (selectedNode.Type != FeedNodeType.Feed)
                            {
                                if (newsItem.Feed.containsNewMessages)
                                {
                                    TreeFeedsNodeBase itemFeedsNode = TreeHelper.FindNode(selectedNode, newsItem);
                                    if (itemFeedsNode != null)
                                    {
                                        UpdateTreeNodeUnreadStatus(itemFeedsNode, 0);
                                        newsItem.Feed.containsNewMessages = false;
                                    }
                                }
                            }
                        }
                    }
                }
                listFeedItems.EndUpdate();
            }
            if (selectedNode.Type == FeedNodeType.Root)
            {
                owner.FeedHandler.MarkAllCachedItemsAsRead();
                UpdateTreeStatus(owner.FeedHandler.FeedsTable);
                ResetFindersReadStatus();
                UnreadItemsNode.Items.Clear();
                UnreadItemsNode.UpdateReadStatus();
                SetGuiStateFeedback(String.Empty, ApplicationTrayState.NormalIdle);
            }
            else if (selectedNode.Type == FeedNodeType.Category)
            {
                WalkdownAndCatchupCategory(selectedNode);
            }
            if (isSmartOrAggregated)
            {
                ISmartFolder sfNode = startNode as ISmartFolder;
                if (sfNode != null) sfNode.UpdateReadStatus();
            }
        }
 
        public  void ToggleItemFlagState(string id)
        {
            ThreadedListViewItem lvItem = GetListViewItem(id);
            if (lvItem != null)
            {
                NewsItem item = (NewsItem) lvItem.Key;
                Flagged oldStatus = item.FlagStatus;
                if (oldStatus != Flagged.None)
                {
                    item.FlagStatus = Flagged.None;
                }
                else
                {
                    item.FlagStatus = Flagged.FollowUp;
                }
                if (item.FlagStatus != Flagged.None)
                {
                    lvItem.Font = FontColorHelper.MergeFontStyles(lvItem.Font, FontColorHelper.HighlightStyle);
                    lvItem.ForeColor = FontColorHelper.HighlightColor;
                }
                else
                {
                    lvItem.Font = FontColorHelper.MergeFontStyles(lvItem.Font, FontColorHelper.NormalStyle);
                    lvItem.ForeColor = FontColorHelper.NormalColor;
                }
                ApplyFlagStateTo(lvItem, item.FlagStatus, listFeedItems.Columns.GetColumnIndexMap());
                CheckForFlaggedNodeAndCreate(item);
                if ((CurrentSelectedFeedsNode as FlaggedItemsNode) != null)
                {
                    owner.ReFlagNewsItem(item);
                }
                else
                {
                    owner.FlagNewsItem(item);
                }
                if (FlaggedFeedsNode(item.FlagStatus) != null)
                {
                    FlaggedFeedsNode(item.FlagStatus).UpdateReadStatus();
                }
                if (listFeedItemsO.Visible)
                    listFeedItemsO.Invalidate();
            }
        }
 
        public  void CmdWatchItemComments(ICommand sender)
        {
            IList selectedItems = GetSelectedLVItems();
            for (int i = 0; i < selectedItems.Count; i++)
            {
                ThreadedListViewItem selectedItem = (ThreadedListViewItem) selectedItems[i];
                NewsItem item = (NewsItem) selectedItem.Key;
                item.WatchComments = !item.WatchComments;
                owner.WatchNewsItem(item);
            }
        }
 
        public  void MarkFeedItemsFlagged(Flagged flag)
        {
            NewsItem item;
            ColumnKeyIndexMap colIndex = listFeedItems.Columns.GetColumnIndexMap();
            IList selectedItems = GetSelectedLVItems();
            ArrayList toBeRemoved = null;
            for (int i = 0; i < selectedItems.Count; i++)
            {
                ThreadedListViewItem selectedItem = (ThreadedListViewItem) selectedItems[i];
                item = (NewsItem) selectedItem.Key;
                if (item.FlagStatus == flag)
                    continue;
                item.FlagStatus = flag;
                if (item.FlagStatus != Flagged.None)
                {
                    selectedItem.Font =
                        FontColorHelper.MergeFontStyles(selectedItem.Font, FontColorHelper.HighlightStyle);
                    selectedItem.ForeColor = FontColorHelper.HighlightColor;
                }
                else
                {
                    selectedItem.Font = FontColorHelper.MergeFontStyles(selectedItem.Font, FontColorHelper.NormalStyle);
                    selectedItem.ForeColor = FontColorHelper.NormalColor;
                }
                ApplyFlagStateTo(selectedItem, item.FlagStatus, colIndex);
                CheckForFlaggedNodeAndCreate(item);
                if ((CurrentSelectedFeedsNode as FlaggedItemsNode) != null)
                {
                    owner.ReFlagNewsItem(item);
                    if (item.FlagStatus == Flagged.None || item.FlagStatus == Flagged.Complete)
                    {
                        if (toBeRemoved == null)
                            toBeRemoved = new ArrayList();
                        toBeRemoved.Add(selectedItem);
                    }
                }
                else
                {
                    owner.FlagNewsItem(item);
                }
            }
            if (toBeRemoved != null && toBeRemoved.Count > 0)
                RemoveListviewItems(toBeRemoved, false, false, false);
            if (FlaggedFeedsNode(flag) != null)
            {
                FlaggedFeedsNode(flag).UpdateReadStatus();
            }
        }
 
        public  void RemoveListviewItems(ICollection itemsToRemove,
                                        bool moveItemsToTrash, bool removeFromSmartFolder, bool updateUnreadCounters)
        {
            if (itemsToRemove == null || itemsToRemove.Count == 0)
                return;
            ThreadedListViewItem[] items = new ThreadedListViewItem[itemsToRemove.Count];
            itemsToRemove.CopyTo(items, 0);
            TreeFeedsNodeBase thisNode = TreeSelectedFeedsNode;
            ISmartFolder isFolder = thisNode as ISmartFolder;
            int unreadItemsCount = 0;
            int itemIndex = 0;
            bool anyUnreadItem = false;
            try
            {
                listFeedItems.BeginUpdate();
                int delCounter = itemsToRemove.Count;
                while (--delCounter >= 0)
                {
                    ThreadedListViewItem currentItem = items[delCounter];
                    if (currentItem == null || currentItem.IndentLevel > 0)
                        continue;
                    if (currentItem.HasChilds && currentItem.Expanded)
                    {
                        int j = currentItem.Index + 1;
                        if (j < listFeedItems.Items.Count)
                        {
                            lock (listFeedItems.Items)
                            {
                                ThreadedListViewItem child = listFeedItems.Items[j];
                                while (child != null && child.IndentLevel > currentItem.IndentLevel)
                                {
                                    listFeedItems.Items.Remove(child);
                                    if (listFeedItemsO.Visible)
                                        listFeedItemsO.Remove(child);
                                    if (j < listFeedItems.Items.Count)
                                        child = listFeedItems.Items[j];
                                    else
                                        child = null;
                                }
                            }
                        }
                    }
                    itemIndex = currentItem.Index;
                    NewsItem item = (NewsItem) currentItem.Key;
                    if (item == null)
                        continue;
                    if (moveItemsToTrash)
                        owner.DeleteNewsItem(item);
                    if (!item.BeenRead)
                        UnreadItemsNode.Remove(item);
                    if (item.WatchComments)
                        WatchedItemsNode.Remove(item);
                    if (item.HasNewComments)
                    {
                        TreeFeedsNodeBase n = TreeHelper.FindNode(thisNode, item);
                        n.UpdateCommentStatus(n, -1);
                    }
                    if (thisNode.Type == FeedNodeType.Category)
                    {
                        if (updateUnreadCounters && !item.BeenRead)
                        {
                            anyUnreadItem = true;
                            TreeFeedsNodeBase n = TreeHelper.FindNode(thisNode, item);
                            UpdateTreeNodeUnreadStatus(n, -1);
                        }
                    }
                    else if (isFolder == null)
                    {
                        isFolder = TreeHelper.FindNode(GetRoot(RootFolderType.SmartFolders), item) as ISmartFolder;
                    }
                    if (updateUnreadCounters && !item.BeenRead)
                    {
                        anyUnreadItem = true;
                        unreadItemsCount++;
                    }
                    if (removeFromSmartFolder && isFolder != null)
                        owner.RemoveItemFromSmartFolder(isFolder, item);
                    lock (listFeedItems.Items)
                    {
                        listFeedItems.Items.Remove(currentItem);
                        if (listFeedItemsO.Visible)
                            listFeedItemsO.Remove(currentItem);
                    }
                }
            }
            finally
            {
                listFeedItems.EndUpdate();
            }
            if (updateUnreadCounters && unreadItemsCount > 0)
            {
                UpdateTreeNodeUnreadStatus(thisNode, -unreadItemsCount);
            }
            if (moveItemsToTrash && anyUnreadItem)
                DeletedItemsNode.UpdateReadStatus();
            if (listFeedItems.Items.Count > 0 && listFeedItems.SelectedItems.Count == 0)
            {
                if ((itemIndex != 0) && (itemIndex >= listFeedItems.Items.Count))
                {
                    itemIndex = listFeedItems.Items.Count - 1;
                }
                listFeedItems.Items[itemIndex].Selected = true;
                listFeedItems.Items[itemIndex].Focused = true;
                OnFeedListItemActivate(this, EventArgs.Empty);
            }
            else if (listFeedItems.SelectedItems.Count > 0)
            {
                OnFeedListItemActivate(this, EventArgs.Empty);
            }
            else
            {
                htmlDetail.Clear();
            }
        }
 
        public  void RemoveSelectedFeedItems()
        {
            IList selectedItems = GetSelectedLVItems();
            if (selectedItems.Count == 0)
                return;
            RemoveListviewItems(selectedItems, true, true, true);
        }
 
        public  void RestoreSelectedFeedItems()
        {
            IList selectedItems = GetSelectedLVItems();
            if (selectedItems.Count == 0)
                return;
            TreeFeedsNodeBase thisNode = TreeSelectedFeedsNode;
            ISmartFolder isFolder = thisNode as ISmartFolder;
            if (!(isFolder is WasteBasketNode))
                return;
            int itemIndex = 0;
            bool anyUnreadItem = false;
            try
            {
                listFeedItems.BeginUpdate();
                while (selectedItems.Count > 0)
                {
                    ThreadedListViewItem selectedItem = (ThreadedListViewItem) selectedItems[0];
                    if (selectedItem.IndentLevel > 0)
                        continue;
                    if (selectedItem.HasChilds && selectedItem.Expanded)
                    {
                        int j = selectedItem.Index + 1;
                        if (j < listFeedItems.Items.Count)
                        {
                            lock (listFeedItems.Items)
                            {
                                ThreadedListViewItem child = listFeedItems.Items[j];
                                while (child != null && child.IndentLevel > selectedItem.IndentLevel)
                                {
                                    listFeedItems.Items.Remove(child);
                                    if (listFeedItemsO.Visible)
                                        listFeedItemsO.Remove(child);
                                    if (j < listFeedItems.Items.Count)
                                        child = listFeedItems.Items[j];
                                    else
                                        child = null;
                                }
                            }
                        }
                    }
                    itemIndex = selectedItem.Index;
                    NewsItem item = (NewsItem) selectedItem.Key;
                    if (item == null)
                    {
                        selectedItems.Remove(selectedItem);
                        continue;
                    }
                    TreeFeedsNodeBase originalContainerNode = owner.RestoreNewsItem(item);
                    if (null != originalContainerNode && ! item.BeenRead)
                    {
                        anyUnreadItem = true;
                        UpdateTreeNodeUnreadStatus(originalContainerNode, 1);
                        UnreadItemsNode.Add(item);
                    }
                    if (null == originalContainerNode)
                    {
                        _log.Error("Item could not be restored, maybe the container feed was removed meanwhile: " +
                                   item.Title);
                    }
                    lock (listFeedItems.Items)
                    {
                        listFeedItems.Items.Remove(selectedItem);
                        if (listFeedItemsO.Visible)
                            listFeedItemsO.Remove(selectedItem);
                        selectedItems.Remove(selectedItem);
                    }
                }
            }
            finally
            {
                listFeedItems.EndUpdate();
            }
            if (anyUnreadItem)
                DeletedItemsNode.UpdateReadStatus();
            if (listFeedItems.Items.Count > 0)
            {
                itemIndex--;
                if (itemIndex < 0)
                {
                    itemIndex = 0;
                }
                else if (itemIndex >= listFeedItems.Items.Count)
                {
                    itemIndex = listFeedItems.Items.Count - 1;
                }
                listFeedItems.Items[itemIndex].Selected = true;
                listFeedItems.Items[itemIndex].Focused = true;
                OnFeedListItemActivate(this, EventArgs.Empty);
            }
            else
            {
                htmlDetail.Clear();
            }
        }
 
        private  IList GetSelectedLVItems()
        {
            if (listFeedItems.Visible)
            {
                return listFeedItems.SelectedItems;
            }
            else
            {
                return listFeedItemsO.SelectedItems;
            }
        }
 
        public  void MarkSelectedItemsLVRead()
        {
            SetFeedItemsReadState(GetSelectedLVItems(), true);
        }
 
        public  void MarkSelectedItemsLVUnread()
        {
            SetFeedItemsReadState(GetSelectedLVItems(), false);
        }
 
        public  void MarkAllItemsLVRead()
        {
            SetFeedItemsReadState(listFeedItems.Items, true);
        }
 
        public  void MarkAllItemsLVUnread()
        {
            SetFeedItemsReadState(listFeedItems.Items, false);
        }
 
        private  void ApplyStyles(ThreadedListViewItem item)
        {
            if (item != null)
            {
                NewsItem n = (NewsItem) item.Key;
                if (n != null)
                    ApplyStyles(item, n.BeenRead, n.HasNewComments);
            }
        }
 
        private  void ApplyStyles(ThreadedListViewItem item, bool beenRead)
        {
            if (item != null)
            {
                NewsItem n = (NewsItem) item.Key;
                if (n != null)
                    ApplyStyles(item, beenRead, n.HasNewComments);
            }
        }
 
        private  void ApplyStyles(ThreadedListViewItem item, bool beenRead, bool newComments)
        {
            if (item != null)
            {
                if (beenRead)
                {
                    item.Font = FontColorHelper.NormalFont;
                    item.ForeColor = FontColorHelper.NormalColor;
                }
                else
                {
                    item.Font = FontColorHelper.UnreadFont;
                    item.ForeColor = FontColorHelper.UnreadColor;
                }
                if (newComments)
                {
                    item.Font = FontColorHelper.MergeFontStyles(item.Font, FontColorHelper.NewCommentsStyle);
                    item.ForeColor = FontColorHelper.NewCommentsColor;
                }
                _filterManager.Apply(item);
                if (listFeedItemsO.Visible)
                    listFeedItemsO.Invalidate();
            }
        }
 
        public  void MarkDiscussionAsRead(string storyId)
        {
            DateTime since = DateTime.Now - new TimeSpan(7, 0, 0, 0, 0);
            IList<NewsItem> affectedItems = owner.FeedHandler.GetItemsWithIncomingLinks(storyId, since);
            List<ThreadedListViewItem> affectedItemsInListView = new List<ThreadedListViewItem>();
            for (int i = 0; i < affectedItems.Count; i++)
            {
                ThreadedListViewItem lvItem = GetListViewItem(affectedItems[i].Id);
                if (lvItem != null)
                {
                    affectedItemsInListView.Add(lvItem);
                    affectedItems.RemoveAt(i);
                }
            }
            SetFeedItemsReadState(affectedItemsInListView, true);
            SetNewsItemsReadState(affectedItems, true);
        }
 
        public  void SwitchPage(string pageType, bool go2nextPage)
        {
            TreeFeedsNodeBase tn = CurrentSelectedFeedsNode;
            if (tn == null)
                return;
            if (go2nextPage)
            {
                _currentPageNumber++;
            }
            else
            {
                _currentPageNumber--;
            }
            if (pageType.Equals("feed"))
            {
                FeedInfo fi = GetFeedItemsAtPage(_currentPageNumber);
                if (fi != null)
                {
                    BeginTransformFeed(fi, tn, owner.FeedHandler.GetStyleSheet(tn.DataKey));
                }
            }
            else
            {
                FeedInfoList fil = GetCategoryItemsAtPage(_currentPageNumber);
                if (fil != null)
                {
                    BeginTransformFeedList(fil, tn, owner.FeedHandler.GetCategoryStyleSheet(tn.CategoryStoreName));
                }
            }
        }
 
        public  void ToggleItemReadState(string id, bool markRead)
        {
            ThreadedListViewItem lvItem = GetListViewItem(id);
            if (lvItem != null)
            {
                bool oldReadState = ((NewsItem) lvItem.Key).BeenRead;
                if (!markRead || (markRead != oldReadState))
                {
                    SetFeedItemsReadState(new ThreadedListViewItem[] {lvItem}, !oldReadState);
                }
            }
        }
 
        public  void ToggleItemReadState(string id)
        {
            ToggleItemReadState(id, false);
        }
 
        public  void ToggleItemWatchState(string id)
        {
            ThreadedListViewItem lvItem = GetListViewItem(id);
            if (lvItem != null)
            {
                NewsItem item = (NewsItem) lvItem.Key;
                item.WatchComments = !item.WatchComments;
                owner.WatchNewsItem(item);
            }
        }
 
        private class  RefLookupItem {
			
            public readonly  TreeFeedsNodeBase Node;
 
               
            public  int UnreadCount;
 
               
            public readonly  INewsFeed Feed; 
            public  RefLookupItem(TreeFeedsNodeBase feedsNode, INewsFeed feed, int unreadCount)
            {
                Node = feedsNode;
                Feed = feed;
                UnreadCount = unreadCount;
            }
		}
		
        public  void SetFeedItemsReadState(IList items, bool beenRead)
        {
            List<NewsItem> modifiedItems = new List<NewsItem>(listFeedItems.SelectedItems.Count);
            int amount = (beenRead ? -1 : 1);
            for (int i = 0; i < items.Count; i++)
            {
                ThreadedListViewItem selectedItem = (ThreadedListViewItem) items[i];
                NewsItem item = (NewsItem) selectedItem.Key;
                ApplyStyles(selectedItem, beenRead);
                if (item.BeenRead != beenRead)
                {
                    selectedItem.ImageIndex += amount;
                    modifiedItems.Add(item);
                }
            }
            SetNewsItemsReadState(modifiedItems, beenRead);
        }
 
        public  void SetNewsItemsReadState(IList<NewsItem> items, bool beenRead)
        {
            List<NewsItem> modifiedItems = new List<NewsItem>(listFeedItems.SelectedItems.Count);
            int amount = (beenRead ? -1 : 1);
            for (int i = 0; i < items.Count; i++)
            {
                NewsItem item = items[i];
                if (item.BeenRead != beenRead)
                {
                    item.BeenRead = beenRead;
                    modifiedItems.Add(item);
                    if (beenRead)
                    {
                        if (!item.Feed.storiesrecentlyviewed.Contains(item.Id))
                        {
                            item.Feed.storiesrecentlyviewed.Add(item.Id);
                        }
                    }
                    else
                    {
                        item.Feed.storiesrecentlyviewed.Remove(item.Id);
                    }
                    SearchHitNewsItem sItem = item as SearchHitNewsItem;
                    NewsItem realItem = owner.FeedHandler.FindNewsItem(sItem);
                    if (realItem != null && sItem != null)
                    {
                        realItem.BeenRead = sItem.BeenRead;
                    }
                }
            }
            if (modifiedItems.Count > 0)
            {
                List<NewsItem> deepModifiedItems = new List<NewsItem>();
                int unexpectedImageState = (beenRead ? 1 : 0);
                for (int i = 0; i < listFeedItems.Items.Count; i++)
                {
                    ThreadedListViewItem th = listFeedItems.Items[i];
                    NewsItem selfRef = th.Key as NewsItem;
                    foreach (NewsItem modifiedItem in modifiedItems)
                    {
                        if (modifiedItem.Equals(selfRef) && (th.ImageIndex%2) == unexpectedImageState)
                        {
                            ApplyStyles(th, beenRead);
                            th.ImageIndex += amount;
                            if (selfRef.BeenRead != beenRead)
                            {
                                selfRef.BeenRead = beenRead;
                                deepModifiedItems.Add(selfRef);
                            }
                        }
                    }
                }
                modifiedItems.AddRange(deepModifiedItems);
                owner.SubscriptionModified(NewsFeedProperty.FeedItemReadState);
                UpdateFindersReadStatus(modifiedItems);
                if (beenRead)
                    UnreadItemsNodeRemoveItems(modifiedItems);
                else
                {
                    UnreadItemsNode.Items.AddRange(modifiedItems);
                    UnreadItemsNode.UpdateReadStatus();
                }
            }
            ISmartFolder sf = CurrentSelectedFeedsNode as ISmartFolder;
            if (sf != null)
            {
                sf.UpdateReadStatus();
                if (!(sf is FinderNode) && !(sf is UnreadItemsNode))
                    return;
            }
            Hashtable lookup = new Hashtable(modifiedItems.Count);
            foreach (NewsItem item in modifiedItems)
            {
                string feedurl = item.Feed.link;
                if (feedurl != null)
                {
                    RefLookupItem lookupItem = lookup[feedurl] as RefLookupItem;
                    TreeFeedsNodeBase refNode = lookupItem != null ? lookupItem.Node : null;
                    if (refNode == null)
                    {
                        if (owner.FeedHandler.FeedsTable.ContainsKey(feedurl))
                            refNode = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), item);
                        else
                            refNode = TreeHelper.FindNode(GetRoot(RootFolderType.SmartFolders), item);
                    }
                    if (refNode != null)
                    {
                        if (!lookup.ContainsKey(feedurl))
                        {
                            lookup.Add(feedurl, new RefLookupItem(refNode, item.Feed, amount));
                        }
                        else
                        {
                            lookupItem.UnreadCount += amount;
                        }
                    }
                    else
                    {
                        string hash = RssHelper.GetHashCode(item);
                        if (tempFeedItemsRead.ContainsKey(hash))
                            tempFeedItemsRead.Remove(hash);
                    }
                }
            }
            foreach (RefLookupItem item in lookup.Values)
            {
                UpdateTreeNodeUnreadStatus(item.Node, item.Node.UnreadCount + item.UnreadCount);
                item.Feed.containsNewMessages = (item.Node.UnreadCount > 0);
            }
            if (listFeedItemsO.Visible)
                listFeedItemsO.Invalidate();
        }
 
        public  void MoveNode(TreeFeedsNodeBase theNode, TreeFeedsNodeBase target)
        {
            if (theNode == null || target == null)
                return;
            if (theNode == target)
                return;
            NewsFeedProperty changes = NewsFeedProperty.None;
            if (theNode.Type == FeedNodeType.Feed)
            {
                INewsFeed f = owner.GetFeed(theNode.DataKey);
                if (f == null)
                    return;
                string category = target.CategoryStoreName;
                f.category = category;
                changes |= NewsFeedProperty.FeedCategory;
                if (category != null && !owner.FeedHandler.HasCategory(category))
                {
                    owner.FeedHandler.Categories.Add(category);
                    changes |= NewsFeedProperty.FeedCategoryAdded;
                }
                treeFeeds.BeginUpdate();
                if (theNode.UnreadCount > 0)
                    theNode.UpdateReadStatus(theNode.Parent, -theNode.UnreadCount);
                theNode.Parent.Nodes.Remove(theNode);
                target.Nodes.Add(theNode);
                theNode.Control.ActiveNode = theNode;
                if (theNode.UnreadCount > 0)
                    theNode.UpdateReadStatus(theNode.Parent, theNode.UnreadCount);
                theNode.BringIntoView();
                treeFeeds.EndUpdate();
                owner.FeedWasModified(f, changes);
            }
            else if (theNode.Type == FeedNodeType.Category)
            {
                string targetCategory = target.CategoryStoreName;
                string sourceCategory = theNode.CategoryStoreName;
                if (sourceCategory != null && owner.FeedHandler.HasCategory(sourceCategory))
                {
                    owner.FeedHandler.DeleteCategory(sourceCategory);
                    changes |= NewsFeedProperty.FeedCategoryRemoved;
                }
                if (targetCategory == null && !owner.FeedHandler.HasCategory(theNode.Text))
                {
                    owner.FeedHandler.Categories.Add(theNode.Text);
                    changes |= NewsFeedProperty.FeedCategoryAdded;
                }
                if (targetCategory != null && !owner.FeedHandler.HasCategory(targetCategory))
                {
                    owner.FeedHandler.Categories.Add(targetCategory);
                    changes |= NewsFeedProperty.FeedCategoryAdded;
                }
                treeFeeds.BeginUpdate();
                if (theNode.UnreadCount > 0)
                    theNode.UpdateReadStatus(theNode.Parent, -theNode.UnreadCount);
                theNode.Parent.Nodes.Remove(theNode);
                target.Nodes.Add(theNode);
                WalkdownThenRenameFeedCategory(theNode, targetCategory);
                owner.SubscriptionModified(changes);
                if (theNode.UnreadCount > 0)
                    theNode.UpdateReadStatus(theNode.Parent, theNode.UnreadCount);
                theNode.BringIntoView();
                treeFeeds.EndUpdate();
            }
            else
            {
                Debug.Assert(false, "MoveNode(): unhandled NodeType:'" + theNode.Type);
            }
        }
 
        public  void AddAutoDiscoveredUrl(DiscoveredFeedsInfo info)
        {
            AppButtonToolCommand duplicateItem =
                new AppButtonToolCommand(
                    String.Concat("cmdDiscoveredFeed_", ++(AutoDiscoveredFeedsMenuHandler.cmdKeyPostfix)),
                    owner.BackgroundDiscoverFeedsHandler.mediator,
                    owner.BackgroundDiscoverFeedsHandler.OnDiscoveredItemClick,
                    owner.BackgroundDiscoverFeedsHandler.StripAndShorten(info.Title), (string) info.FeedLinks[0]);
            if (owner.BackgroundDiscoverFeedsHandler.itemDropdown.ToolbarsManager.Tools.Exists(duplicateItem.Key))
                owner.BackgroundDiscoverFeedsHandler.itemDropdown.ToolbarsManager.Tools.Remove(duplicateItem);
            owner.BackgroundDiscoverFeedsHandler.itemDropdown.ToolbarsManager.Tools.Add(duplicateItem);
            duplicateItem.SharedProps.StatusText = info.SiteBaseUrl;
            duplicateItem.SharedProps.ShowInCustomizer = false;
            Win32.PlaySound(Resource.ApplicationSound.FeedDiscovered);
            lock (owner.BackgroundDiscoverFeedsHandler.discoveredFeeds)
            {
                owner.BackgroundDiscoverFeedsHandler.discoveredFeeds.Add(duplicateItem, info);
            }
            lock (owner.BackgroundDiscoverFeedsHandler.newDiscoveredFeeds)
            {
                owner.BackgroundDiscoverFeedsHandler.newDiscoveredFeeds.Enqueue(duplicateItem);
            }
        }
 
        public  void AddFeedUrlSynchronized(string newFeedUrl)
        {
            InvokeOnGui(delegate
            {
                newFeedUrl = owner.HandleUrlFeedProtocol(newFeedUrl);
                owner.CmdNewFeed(null, newFeedUrl, null);
            });
        }
 
        public  void OnFeedUpdateStart(Uri feedUri, ref bool cancel)
        {
            string feedUrl;
            TreeFeedsNodeBase feedsNode;
            if (feedUri.IsFile || feedUri.IsUnc)
                feedUrl = feedUri.LocalPath;
            else
<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442347929/fstmerge_var1_2452742031446889879
                feedUrl = feedUri.CanonicalizedUri();
            INewsFeed f;
=======
                feedUrl = feedUri.CanonicalizedUri();
            NewsFeed f;
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442347929/fstmerge_var2_5931957949457317962
            if (owner.FeedHandler.FeedsTable.TryGetValue(feedUrl, out f))
            {
                feedsNode = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), f);
            }
            else
            {
                feedsNode = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), feedUrl);
            }
            if (feedsNode != null)
            {
                SetSubscriptionNodeState(f, feedsNode, FeedProcessingState.Updating);
            }
        }
 
        public  void OnFeedUpdateFinishedWithException(string feedUrl, Exception exception)
        {
            TreeFeedsNodeBase feedsNode;
            INewsFeed f = null;
            if (owner.FeedHandler.FeedsTable.TryGetValue(feedUrl, out f))
            {
                feedsNode = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), f);
            }
            else
            {
                feedsNode = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), feedUrl);
            }
            if (feedsNode != null)
            {
                SetSubscriptionNodeState(f, feedsNode, FeedProcessingState.Failure);
            }
        }
 
        public  void OnRequestCertificateIssue(object sender, CertificateIssueCancelEventArgs e)
        {
            e.Cancel = true;
            if (!Visible)
                return;
<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442348045/fstmerge_var1_5077895027143577386
   string feedUrl = e.WebRequest.RequestUri.CanonicalizedUri();
            INewsFeed f = null;
=======
            string feedUrl = e.WebRequest.RequestUri.CanonicalizedUri();
            NewsFeed f = null;
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442348045/fstmerge_var2_44247456098319859
            string issueCaption, issueDesc;
            if (owner.FeedHandler.FeedsTable.TryGetValue(feedUrl, out f))
            {
                issueCaption = SR.CertificateIssueOnFeedCaption(f.title);
            }
            else
            {
                issueCaption = SR.CertificateIssueOnSiteCaption(feedUrl);
            }
            switch (e.CertificateIssue)
            {
                case CertificateIssue.CertCN_NO_MATCH:
                    issueDesc = SR.CertificateIssue_CertCN_NO_MATCH;
                    break;
                case CertificateIssue.CertEXPIRED:
                    issueDesc = SR.CertificateIssue_CertEXPIRED(e.Certificate.GetExpirationDateString());
                    break;
                case CertificateIssue.CertREVOKED:
                    issueDesc = SR.CertificateIssue_CertREVOKED;
                    break;
                case CertificateIssue.CertUNTRUSTEDCA:
                    issueDesc = SR.CertificateIssue_CertUNTRUSTEDCA;
                    break;
                case CertificateIssue.CertUNTRUSTEDROOT:
                    issueDesc = SR.CertificateIssue_CertUNTRUSTEDROOT;
                    break;
                case CertificateIssue.CertUNTRUSTEDTESTROOT:
                    issueDesc = SR.CertificateIssue_CertUNTRUSTEDTESTROOT;
                    break;
                case CertificateIssue.CertPURPOSE:
                    issueDesc = SR.CertificateIssue_CertPURPOSE;
                    break;
                case CertificateIssue.CertCHAINING:
                    issueDesc = SR.CertificateIssue_CertCHAINING;
                    break;
                case CertificateIssue.CertCRITICAL:
                    issueDesc = SR.CertificateIssue_CertCRITICAL;
                    break;
                case CertificateIssue.CertISSUERCHAINING:
                    issueDesc = SR.CertificateIssue_CertISSUERCHAINING;
                    break;
                case CertificateIssue.CertMALFORMED:
                    issueDesc = SR.CertificateIssue_CertMALFORMED;
                    break;
                case CertificateIssue.CertPATHLENCONST:
                    issueDesc = SR.CertificateIssue_CertPATHLENCONST;
                    break;
                case CertificateIssue.CertREVOCATION_FAILURE:
                    issueDesc = SR.CertificateIssue_CertREVOCATION_FAILURE;
                    break;
                case CertificateIssue.CertROLE:
                    issueDesc = SR.CertificateIssue_CertROLE;
                    break;
                case CertificateIssue.CertVALIDITYPERIODNESTING:
                    issueDesc = SR.CertificateIssue_CertVALIDITYPERIODNESTING;
                    break;
                case CertificateIssue.CertWRONG_USAGE:
                    issueDesc = SR.CertificateIssue_CertWRONG_USAGE;
                    break;
                default:
                    issueDesc = SR.CertificateIssue_Unknown(e.CertificateIssue.ToString());
                    break;
            }
   using (SecurityIssueDialog dialog = new SecurityIssueDialog(issueCaption, issueDesc))
   {
    dialog.CustomCommand.Tag = e.Certificate;
    dialog.CustomCommand.Click += OnSecurityIssueDialogCustomCommandClick;
    dialog.CustomCommand.Visible = (e.Certificate != null && e.Certificate.Handle != IntPtr.Zero);
    Win32.SetForegroundWindow(Handle);
    if (dialog.ShowDialog(this) == DialogResult.OK) {
     e.Cancel = false;
     owner.AddTrustedCertificateIssue(feedUrl, e.CertificateIssue);
    }
   }
        }
 
        private static  void OnSecurityIssueDialogCustomCommandClick(object sender, EventArgs e)
        {
            Button cmd = (Button) sender;
            cmd.Enabled = false;
            Application.DoEvents();
            X509Certificate cert = (X509Certificate) cmd.Tag;
<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442348104/fstmerge_var1_2837733897866430769
   if (cert == null)
    return;
   string certFilename = Path.Combine(Path.GetTempPath(), cert.GetHashCode() + ".temp.cer");
=======
   if (cert == null)
    return;
            string certFilename = Path.Combine(Path.GetTempPath(), cert.GetHashCode() + ".temp.cer");
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442348104/fstmerge_var2_7944325722708070266
            try
            {
                if (File.Exists(certFilename))
                    File.Delete(certFilename);
                using (Stream stream = FileHelper.OpenForWrite(certFilename))
                {
                    BinaryWriter writer = new BinaryWriter(stream);
                    writer.Write(cert.GetRawCertData());
                    writer.Flush();
                    writer.Close();
                }
            }
            catch (Exception ex)
            {
                ExceptionManager.Publish(ex);
                cmd.Enabled = false;
                return;
            }
            try
            {
                if (File.Exists(certFilename))
                {
                    Process p = Process.Start(certFilename);
                    p.WaitForExit();
                }
            }
            finally
            {
                if (File.Exists(certFilename))
                    File.Delete(certFilename);
                cmd.Enabled = true;
            }
        }
 
        protected  void InitShortcutManager()
        {
            _shortcutHandler = new ShortcutHandler();
            string settingsPath = RssBanditApplication.GetShortcutSettingsFileName();
            try
            {
                     _shortcutHandler.Load(settingsPath);
            }
            catch (InvalidShortcutSettingsFileException e)
            {
                _log.Warn("The user defined shortcut settings file is invalid. Using the default instead.", e);
                using (Stream settingsStream = Resource.GetStream("Resources.ShortcutSettings.xml"))
                {
                    _shortcutHandler.Load(settingsStream);
                }
            }
        }
 
        protected  void InitResources()
        {
                _treeImages = Resource.LoadBitmapStrip("Resources.TreeImagesXP.png", new Size(16, 16));
            _listImages = Resource.LoadBitmapStrip("Resources.ListImages.png", new Size(16, 16));
            _allToolImages = Resource.LoadBitmapStrip("Resources.AllToolImages.png", new Size(16, 16));
        }
 
        private  void InitWidgets()
        {
            InitFeedTreeView();
            InitFeedDetailsCaption();
            InitListView();
            InitOutlookListView();
            InitHtmlDetail();
            InitToaster();
            InitializeSearchPanel();
            InitOutlookNavigator();
            InitNavigatorHiddenCaption();
        }
 
        private  void InitOutlookNavigator()
        {
            navigatorHeaderHelper = new NavigatorHeaderHelper(Navigator, Properties.Resources.Arrows_Left_16);
            navigatorHeaderHelper.ImageClick += OnNavigatorCollapseClick;
            Navigator.GroupClick += OnNavigatorGroupClick;
            if (SearchIndexBehavior.NoIndexing == owner.FeedHandler.Configuration.SearchIndexBehavior)
            {
                ToggleNavigationPaneView(NavigationPaneView.Subscriptions);
                owner.Mediator.SetDisabled("cmdToggleRssSearchTabState");
                Navigator.Groups[Resource.NavigatorGroup.RssSearch].Enabled = false;
            }
        }
 
        private  void InitOutlookListView()
        {
            listFeedItemsO.ViewStyle = ViewStyle.OutlookExpress;
            UltraTreeNodeExtendedDateTimeComparer sc = new UltraTreeNodeExtendedDateTimeComparer();
            listFeedItemsO.ColumnSettings.ColumnSets[0].Columns[0].SortComparer = sc;
            listFeedItemsO.ColumnSettings.ColumnSets[0].Columns[0].SortType = SortType.Ascending;
            listFeedItemsO.ColumnSettings.AllowSorting = DefaultableBoolean.True;
            listFeedItemsO.Override.SortComparer = sc;
            listFeedItemsO.DrawFilter = new ListFeedsDrawFilter();
            listFeedItemsO.AfterSelect += OnListFeedItemsO_AfterSelect;
            listFeedItemsO.KeyDown += OnListFeedItemsO_KeyDown;
            listFeedItemsO.BeforeExpand += listFeedItemsO_BeforeExpand;
            listFeedItemsO.MouseDown += listFeedItemsO_MouseDown;
            listFeedItemsO.AfterSortChange += listFeedItemsO_AfterSortChange;
        }
 
        private  void InitFeedDetailsCaption()
        {
            detailHeaderCaption.Font = new Font("Arial", 12f, FontStyle.Bold);
            detailHeaderCaption.DrawFilter = new SmoothLabelDrawFilter(detailHeaderCaption);
            detailHeaderCaption.Appearance.BackColor =
                FontColorHelper.UiColorScheme.OutlookNavPaneCurrentGroupHeaderGradientLight;
            detailHeaderCaption.Appearance.BackColor2 =
                FontColorHelper.UiColorScheme.OutlookNavPaneCurrentGroupHeaderGradientDark;
            detailHeaderCaption.Appearance.BackGradientStyle = GradientStyle.Vertical;
            detailHeaderCaption.Appearance.ForeColor =
                FontColorHelper.UiColorScheme.OutlookNavPaneCurrentGroupHeaderForecolor;
        }
 
        private  void InitNavigatorHiddenCaption()
        {
            navigatorHiddenCaption.Font = new Font("Arial", 12f, FontStyle.Bold);
            navigatorHiddenCaption.Appearance.BackColor =
                FontColorHelper.UiColorScheme.OutlookNavPaneCurrentGroupHeaderGradientLight;
            navigatorHiddenCaption.Appearance.BackColor2 =
                FontColorHelper.UiColorScheme.OutlookNavPaneCurrentGroupHeaderGradientDark;
            navigatorHiddenCaption.Appearance.BackGradientStyle = GradientStyle.Horizontal;
            navigatorHiddenCaption.ForeColor = FontColorHelper.UiColorScheme.OutlookNavPaneCurrentGroupHeaderForecolor;
            navigatorHiddenCaption.ImageClick += OnNavigatorExpandImageClick;
        }
 
        private  void InitializeSearchPanel()
        {
            searchPanel = new SearchPanel();
            searchPanel.Dock = DockStyle.Fill;
            searchPanel.Location = new Point(0, 0);
            searchPanel.Name = "searchPanel";
            searchPanel.Size = new Size(237, 496);
            searchPanel.TabIndex = 0;
            panelRssSearch.Controls.Add(searchPanel);
            owner.FeedHandler.SearchFinished += OnNewsItemSearchFinished;
            searchPanel.BeforeNewsItemSearch += OnSearchPanelBeforeNewsItemSearch;
            searchPanel.NewsItemSearch += OnSearchPanelStartNewsItemSearch;
            owner.FindersSearchRoot.SetScopeResolveCallback(ScopeResolve);
        }
 
        private  void InitToaster()
        {
            toastNotifier = new ToastNotifier(
                OnExternalActivateFeedItem,
                OnExternalDisplayFeedProperties,
                OnExternalActivateFeed,
                PlayEnclosure);
        }
 
        private  void InitListView()
        {
            colTopic.Text = SR.ListviewColumnCaptionTopic;
            colHeadline.Text = SR.ListviewColumnCaptionHeadline;
            colDate.Text = SR.ListviewColumnCaptionDate;
            listFeedItems.SmallImageList = _listImages;
            listFeedItemsO.ImageList = _listImages;
            owner.FeedlistLoaded += OnOwnerFeedlistLoaded;
            listFeedItems.ColumnClick += OnFeedListItemsColumnClick;
            listFeedItems.SelectedIndexChanged += listFeedItems_SelectedIndexChanged;
            if (owner.Preferences.BuildRelationCosmos)
            {
                listFeedItems.ShowAsThreads = true;
            }
            else
            {
                listFeedItems.ShowInGroups = false;
                listFeedItems.AutoGroupMode = false;
            }
        }
 
        public  void ResetHtmlDetail()
        {
            ResetHtmlDetail(false);
        }
 
        private  void InitHtmlDetail()
        {
            ResetHtmlDetail(true);
        }
 
        private  void ResetHtmlDetail(bool clearContent)
        {
            htmlDetail.EnhanceBrowserSecurityForProcess();
            htmlDetail.ActiveXEnabled = owner.Preferences.BrowserActiveXAllowed;
            HtmlControl.SetInternetFeatureEnabled(
                InternetFeatureList.FEATURE_RESTRICT_ACTIVEXINSTALL,
                SetFeatureFlag.SET_FEATURE_ON_PROCESS,
                htmlDetail.ActiveXEnabled);
            htmlDetail.ImagesDownloadEnabled = owner.Preferences.BrowserImagesAllowed;
            htmlDetail.JavaEnabled = owner.Preferences.BrowserJavaAllowed;
            htmlDetail.VideoEnabled = owner.Preferences.BrowserVideoAllowed;
            htmlDetail.FrameDownloadEnabled =
                (bool)
                RssBanditApplication.ReadAppSettingsEntry("FeedDetailPane.FrameDownloadEnabled", typeof (bool), false);
            htmlDetail.Border3d = true;
            htmlDetail.FlatScrollBars = true;
            htmlDetail.ScriptEnabled = owner.Preferences.BrowserJavascriptAllowed;
            htmlDetail.ScriptObject = null;
            htmlDetail.ScrollBarsEnabled = true;
            htmlDetail.AllowInPlaceNavigation = false;
   this.htmlDetail.SilentModeEnabled = true;
            htmlDetail.Tag = _docFeedDetails;
            htmlDetail.StatusTextChanged += OnWebStatusTextChanged;
            htmlDetail.BeforeNavigate += OnWebBeforeNavigate;
            htmlDetail.NavigateComplete += OnWebNavigateComplete;
            htmlDetail.DocumentComplete += OnWebDocumentComplete;
            htmlDetail.NewWindow += OnWebNewWindow;
            htmlDetail.ProgressChanged += OnWebProgressChanged;
            htmlDetail.TranslateAccelerator += OnWebTranslateAccelerator;
            if (clearContent)
            {
                htmlDetail.Clear();
            }
        }
 
        private  void InitFeedTreeView()
        {
            treeFeeds.PathSeparator = NewsHandler.CategorySeparator;
            treeFeeds.ImageList = _treeImages;
            treeFeeds.Nodes.Override.Sort = SortType.None;
            treeFeeds.ScrollBounds = ScrollBounds.ScrollToFill;
            treeFeeds.Override.SelectionType = SelectType.SingleAutoDrag;
            TreeFeedsNodeBase root =
                new RootNode(SR.FeedNodeMyFeedsCaption, Resource.SubscriptionTreeImage.AllSubscriptions,
                             Resource.SubscriptionTreeImage.AllSubscriptionsExpanded, _treeRootContextMenu);
            treeFeeds.Nodes.Add(root);
            root.ReadCounterZero += OnTreeNodeFeedsRootReadCounterZero;
            _roots[(int) RootFolderType.MyFeeds] = root;
            AddHistoryEntry(root, null);
            root =
                new FinderRootNode(SR.FeedNodeFinderRootCaption, Resource.SubscriptionTreeImage.AllFinderFolders,
                                   Resource.SubscriptionTreeImage.AllFinderFoldersExpanded,
                                   _treeSearchFolderRootContextMenu);
            treeFeeds.Nodes.Add(root);
            _roots[(int) RootFolderType.Finder] = root;
            if (SearchIndexBehavior.NoIndexing == owner.FeedHandler.Configuration.SearchIndexBehavior)
                root.Visible = false;
            root =
                new SpecialRootNode(SR.FeedNodeSpecialFeedsCaption, Resource.SubscriptionTreeImage.AllSmartFolders,
                                    Resource.SubscriptionTreeImage.AllSmartFoldersExpanded, null);
            treeFeeds.Nodes.Add(root);
            _roots[(int) RootFolderType.SmartFolders] = root;
            treeFeeds.DrawFilter = new TreeFeedsDrawFilter();
            treeFeeds.MouseDown += OnTreeFeedMouseDown;
            treeFeeds.MouseUp += OnTreeFeedMouseUp;
            treeFeeds.DragOver += OnTreeFeedDragOver;
            treeFeeds.AfterSelect += OnTreeFeedAfterSelect;
            treeFeeds.QueryContinueDrag += OnTreeFeedQueryContiueDrag;
            treeFeeds.DragEnter += OnTreeFeedDragEnter;
            treeFeeds.MouseMove += OnTreeFeedMouseMove;
            treeFeeds.BeforeSelect += OnTreeFeedBeforeSelect;
            treeFeeds.BeforeLabelEdit += OnTreeFeedBeforeLabelEdit;
            treeFeeds.ValidateLabelEdit += OnTreeFeedsValidateLabelEdit;
            treeFeeds.AfterLabelEdit += OnTreeFeedAfterLabelEdit;
            treeFeeds.DragDrop += OnTreeFeedDragDrop;
            treeFeeds.SelectionDragStart += OnTreeFeedSelectionDragStart;
            treeFeeds.GiveFeedback += OnTreeFeedGiveFeedback;
            treeFeeds.DoubleClick += OnTreeFeedDoubleClick;
        }
 
        protected  void InitContextMenus()
        {
            _treeRootContextMenu = new ContextMenu();
            AppContextMenuCommand sub1 = new AppContextMenuCommand("cmdNewFeed",
                                                                   owner.Mediator,
                                                                   new ExecuteCommandHandler(owner.CmdNewFeed),
                                                                   SR.MenuNewFeedCaption2, SR.MenuNewFeedDesc, 1,
                                                                   _shortcutHandler);
            AppContextMenuCommand sub2 = new AppContextMenuCommand("cmdNewCategory",
                                                                   owner.Mediator,
                                                                   new ExecuteCommandHandler(owner.CmdNewCategory),
                                                                   SR.MenuNewCategoryCaption, SR.MenuNewCategoryDesc, 2,
                                                                   _shortcutHandler);
            MenuItem sep = new MenuItem("-");
            AppContextMenuCommand subR1 = new AppContextMenuCommand("cmdRefreshFeeds",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(owner.CmdRefreshFeeds),
                                                                    SR.MenuUpdateAllFeedsCaption,
                                                                    SR.MenuUpdateAllFeedsDesc, 0, _shortcutHandler);
            AppContextMenuCommand subR2 = new AppContextMenuCommand("cmdCatchUpCurrentSelectedNode",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(
                                                                        owner.CmdCatchUpCurrentSelectedNode),
                                                                    SR.MenuCatchUpOnAllCaption, SR.MenuCatchUpOnAllDesc,
                                                                    0, _shortcutHandler);
            AppContextMenuCommand subR3 = new AppContextMenuCommand("cmdDeleteAll",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(owner.CmdDeleteAll),
                                                                    SR.MenuDeleteAllFeedsCaption,
                                                                    SR.MenuDeleteAllFeedsDesc, 2, _shortcutHandler);
            AppContextMenuCommand subR4 = new AppContextMenuCommand("cmdShowMainAppOptions",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(owner.CmdShowOptions),
                                                                    SR.MenuAppOptionsCaption, SR.MenuAppOptionsDesc, 10,
                                                                    _shortcutHandler);
            _treeRootContextMenu.MenuItems.AddRange(
                new MenuItem[] {sub1, sub2, sep, subR1, subR2, sep.CloneMenu(), subR3, sep.CloneMenu(), subR4});
            _treeCategoryContextMenu = new ContextMenu();
            AppContextMenuCommand subC1 = new AppContextMenuCommand("cmdUpdateCategory",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(owner.CmdUpdateCategory),
                                                                    SR.MenuUpdateCategoryCaption,
                                                                    SR.MenuUpdateCategoryDesc, _shortcutHandler);
            AppContextMenuCommand subC2 = new AppContextMenuCommand("cmdCatchUpCurrentSelectedNode",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(
                                                                        owner.CmdCatchUpCurrentSelectedNode),
                                                                    SR.MenuCatchUpCategoryCaption,
                                                                    SR.MenuCatchUpCategoryDesc, 0, _shortcutHandler);
            AppContextMenuCommand subC3 = new AppContextMenuCommand("cmdRenameCategory",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(owner.CmdRenameCategory),
                                                                    SR.MenuRenameCategoryCaption,
                                                                    SR.MenuRenameCategoryDesc, _shortcutHandler);
            AppContextMenuCommand subC4 = new AppContextMenuCommand("cmdDeleteCategory",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(owner.CmdDeleteCategory),
                                                                    SR.MenuDeleteCategoryCaption,
                                                                    SR.MenuDeleteCategoryDesc, 2, _shortcutHandler);
            AppContextMenuCommand subC5 = new AppContextMenuCommand("cmdShowCategoryProperties",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(
                                                                        owner.CmdShowCategoryProperties),
                                                                    SR.MenuShowCategoryPropertiesCaption,
                                                                    SR.MenuShowCategoryPropertiesDesc, 10,
                                                                    _shortcutHandler);
            AppContextMenuCommand subCL_ColLayoutMain = new AppContextMenuCommand("cmdColumnChooserMain",
                                                                                  owner.Mediator,
                                                                                  new ExecuteCommandHandler(CmdNop),
                                                                                  SR.MenuColumnChooserCaption,
                                                                                  SR.MenuColumnChooserDesc,
                                                                                  _shortcutHandler);
            foreach (string colID in Enum.GetNames(typeof (NewsItemSortField)))
            {
                AppContextMenuCommand subCL4_layoutSubColumn = new AppContextMenuCommand("cmdListviewColumn." + colID,
                                                                                         owner.Mediator,
                                                                                         new ExecuteCommandHandler(
                                                                                             CmdToggleListviewColumn),
                                                                                         SR.Keys.GetString(
                                                                                             "MenuColumnChooser" + colID +
                                                                                             "Caption"),
                                                                                         SR.Keys.GetString(
                                                                                             "MenuColumnChooser" + colID +
                                                                                             "Desc"), _shortcutHandler);
                subCL_ColLayoutMain.MenuItems.AddRange(new MenuItem[] {subCL4_layoutSubColumn});
            }
            AppContextMenuCommand subCL_subUseCatLayout =
                new AppContextMenuCommand("cmdColumnChooserUseCategoryLayoutGlobal",
                                          owner.Mediator,
                                          new ExecuteCommandHandler(CmdColumnChooserUseCategoryLayoutGlobal),
                                          SR.MenuColumnChooserUseCategoryLayoutGlobalCaption,
                                          SR.MenuColumnChooserUseCategoryLayoutGlobalDesc, _shortcutHandler);
            AppContextMenuCommand subCL_subUseFeedLayout =
                new AppContextMenuCommand("cmdColumnChooserUseFeedLayoutGlobal",
                                          owner.Mediator, new ExecuteCommandHandler(CmdColumnChooserUseFeedLayoutGlobal),
                                          SR.MenuColumnChooserUseFeedLayoutGlobalCaption,
                                          SR.MenuColumnChooserUseFeedLayoutGlobalDesc, _shortcutHandler);
            AppContextMenuCommand subCL_subResetLayout = new AppContextMenuCommand("cmdColumnChooserResetToDefault",
                                                                                   owner.Mediator,
                                                                                   new ExecuteCommandHandler(
                                                                                       CmdColumnChooserResetToDefault),
                                                                                   SR.
                                                                                       MenuColumnChooserResetLayoutToDefaultCaption,
                                                                                   SR.
                                                                                       MenuColumnChooserResetLayoutToDefaultDesc,
                                                                                   _shortcutHandler);
            subCL_ColLayoutMain.MenuItems.AddRange(
                new MenuItem[]
                    {
                        sep.CloneMenu(), subCL_subUseCatLayout, subCL_subUseFeedLayout, sep.CloneMenu(),
                        subCL_subResetLayout
                    });
            _treeCategoryContextMenu.MenuItems.AddRange(
                new MenuItem[]
                    {
                        sub1.CloneMenu(), sub2.CloneMenu(), sep.CloneMenu(), subC1, subC2, sep.CloneMenu(), subC3,
                        sep.CloneMenu(), subC4, sep.CloneMenu(), subCL_ColLayoutMain, sep.CloneMenu(), subC5
                    });
            _treeFeedContextMenu = new ContextMenu();
            AppContextMenuCommand subF1 = new AppContextMenuCommand("cmdUpdateFeed",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(owner.CmdUpdateFeed),
                                                                    SR.MenuUpdateThisFeedCaption,
                                                                    SR.MenuUpdateThisFeedDesc, _shortcutHandler);
            AppContextMenuCommand subF2 = new AppContextMenuCommand("cmdCatchUpCurrentSelectedNode",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(
                                                                        owner.CmdCatchUpCurrentSelectedNode),
                                                                    SR.MenuCatchUpThisFeedCaption,
                                                                    SR.MenuCatchUpThisFeedDesc, 0, _shortcutHandler);
            AppContextMenuCommand subF3 = new AppContextMenuCommand("cmdRenameFeed",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(owner.CmdRenameFeed),
                                                                    SR.MenuRenameThisFeedCaption,
                                                                    SR.MenuRenameThisFeedDesc, _shortcutHandler);
            AppContextMenuCommand subF4 = new AppContextMenuCommand("cmdDeleteFeed",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(CmdDeleteFeed),
                                                                    SR.MenuDeleteThisFeedCaption,
                                                                    SR.MenuDeleteThisFeedDesc, 2, _shortcutHandler);
            AppContextMenuCommand subFeedCopy = new AppContextMenuCommand("cmdCopyFeed",
                                                                          owner.Mediator,
                                                                          new ExecuteCommandHandler(CmdCopyFeed),
                                                                          SR.MenuCopyFeedCaption, SR.MenuCopyFeedDesc, 1,
                                                                          _shortcutHandler);
            AppContextMenuCommand subFeedCopy_sub1 = new AppContextMenuCommand("cmdCopyFeedLinkToClipboard",
                                                                               owner.Mediator,
                                                                               new ExecuteCommandHandler(
                                                                                   CmdCopyFeedLinkToClipboard),
                                                                               SR.MenuCopyFeedLinkToClipboardCaption,
                                                                               SR.MenuCopyFeedLinkToClipboardDesc, 1,
                                                                               _shortcutHandler);
            AppContextMenuCommand subFeedCopy_sub2 = new AppContextMenuCommand("cmdCopyFeedHomepageLinkToClipboard",
                                                                               owner.Mediator,
                                                                               new ExecuteCommandHandler(
                                                                                   CmdCopyFeedHomeLinkToClipboard),
                                                                               SR.MenuCopyFeedHomeLinkToClipboardCaption,
                                                                               SR.MenuCopyFeedHomeLinkToClipboardDesc, 1,
                                                                               _shortcutHandler);
            AppContextMenuCommand subFeedCopy_sub3 =
                new AppContextMenuCommand("cmdCopyFeedHomepageTitleLinkToClipboard",
                                          owner.Mediator, new ExecuteCommandHandler(CmdCopyFeedHomeTitleLinkToClipboard),
                                          SR.MenuCopyFeedFeedHomeTitleLinkToClipboardCaption,
                                          SR.MenuCopyFeedFeedHomeTitleLinkToClipboardDesc, 1, _shortcutHandler);
            subFeedCopy.MenuItems.AddRange(new MenuItem[] {subFeedCopy_sub1, subFeedCopy_sub2, subFeedCopy_sub3});
            _feedInfoContextMenu = new MenuItem(SR.MenuAdvancedFeedInfoCaption);
            AppContextMenuCommand subF6 = new AppContextMenuCommand("cmdShowFeedProperties",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(
                                                                        owner.CmdShowFeedProperties),
                                                                    SR.MenuShowFeedPropertiesCaption,
                                                                    SR.MenuShowFeedPropertiesDesc, 10, _shortcutHandler);
            AppContextMenuCommand subFL_ColLayoutMain = new AppContextMenuCommand("cmdColumnChooserMain",
                                                                                  owner.Mediator,
                                                                                  new ExecuteCommandHandler(CmdNop),
                                                                                  SR.MenuColumnChooserCaption,
                                                                                  SR.MenuColumnChooserDesc,
                                                                                  _shortcutHandler);
            foreach (string colID in Enum.GetNames(typeof (NewsItemSortField)))
            {
                AppContextMenuCommand subFL4_layoutSubColumn = new AppContextMenuCommand("cmdListviewColumn." + colID,
                                                                                         owner.Mediator,
                                                                                         new ExecuteCommandHandler(
                                                                                             CmdToggleListviewColumn),
                                                                                         SR.Keys.GetString(
                                                                                             "MenuColumnChooser" + colID +
                                                                                             "Caption"),
                                                                                         SR.Keys.GetString(
                                                                                             "MenuColumnChooser" + colID +
                                                                                             "Desc"), _shortcutHandler);
                subFL_ColLayoutMain.MenuItems.AddRange(new MenuItem[] {subFL4_layoutSubColumn});
            }
            AppContextMenuCommand subFL_subUseCatLayout =
                new AppContextMenuCommand("cmdColumnChooserUseCategoryLayoutGlobal",
                                          owner.Mediator,
                                          new ExecuteCommandHandler(CmdColumnChooserUseCategoryLayoutGlobal),
                                          SR.MenuColumnChooserUseCategoryLayoutGlobalCaption,
                                          SR.MenuColumnChooserUseCategoryLayoutGlobalDesc, _shortcutHandler);
            AppContextMenuCommand subFL_subUseFeedLayout =
                new AppContextMenuCommand("cmdColumnChooserUseFeedLayoutGlobal",
                                          owner.Mediator, new ExecuteCommandHandler(CmdColumnChooserUseFeedLayoutGlobal),
                                          SR.MenuColumnChooserUseFeedLayoutGlobalCaption,
                                          SR.MenuColumnChooserUseFeedLayoutGlobalDesc, _shortcutHandler);
            AppContextMenuCommand subFL_subResetLayout = new AppContextMenuCommand("cmdColumnChooserResetToDefault",
                                                                                   owner.Mediator,
                                                                                   new ExecuteCommandHandler(
                                                                                       CmdColumnChooserResetToDefault),
                                                                                   SR.
                                                                                       MenuColumnChooserResetLayoutToDefaultCaption,
                                                                                   SR.
                                                                                       MenuColumnChooserResetLayoutToDefaultDesc,
                                                                                   _shortcutHandler);
            subFL_ColLayoutMain.MenuItems.AddRange(
                new MenuItem[]
                    {
                        sep.CloneMenu(), subFL_subUseCatLayout, subFL_subUseFeedLayout, sep.CloneMenu(),
                        subFL_subResetLayout
                    });
            _treeFeedContextMenu.MenuItems.AddRange(
                new MenuItem[]
                    {
                        subF1, subF2, subF3, sep.CloneMenu(), subF4, sep.CloneMenu(), subFeedCopy, sep.CloneMenu(),
                        _feedInfoContextMenu, sep.CloneMenu(), subFL_ColLayoutMain, sep.CloneMenu(), subF6
                    });
            AppContextMenuCommand subInfoHome = new AppContextMenuCommand("cmdNavigateToFeedHome",
                                                                          owner.Mediator,
                                                                          new ExecuteCommandHandler(
                                                                              owner.CmdNavigateFeedHome),
                                                                          SR.MenuNavigateToFeedHomeCaption,
                                                                          SR.MenuNavigateToFeedHomeDesc,
                                                                          _shortcutHandler);
            AppContextMenuCommand subInfoCosmos = new AppContextMenuCommand("cmdNavigateToFeedCosmos",
                                                                            owner.Mediator,
                                                                            new ExecuteCommandHandler(
                                                                                owner.CmdNavigateFeedLinkCosmos),
                                                                            SR.MenuShowLinkCosmosCaption,
                                                                            SR.MenuShowLinkCosmosCaption);
            AppContextMenuCommand subInfoSource = new AppContextMenuCommand("cmdViewSourceOfFeed",
                                                                            owner.Mediator,
                                                                            new ExecuteCommandHandler(
                                                                                owner.CmdViewSourceOfFeed),
                                                                            SR.MenuViewSourceOfFeedCaption,
                                                                            SR.MenuViewSourceOfFeedDesc,
                                                                            _shortcutHandler);
            AppContextMenuCommand subInfoValidate = new AppContextMenuCommand("cmdValidateFeed",
                                                                              owner.Mediator,
                                                                              new ExecuteCommandHandler(
                                                                                  owner.CmdValidateFeed),
                                                                              SR.MenuValidateFeedCaption,
                                                                              SR.MenuValidateFeedDesc, _shortcutHandler);
            _feedInfoContextMenu.MenuItems.AddRange(
                new MenuItem[] {subInfoHome, subInfoCosmos, subInfoSource, subInfoValidate});
            _treeSearchFolderRootContextMenu = new ContextMenu();
            subF1 = new AppContextMenuCommand("cmdNewFinder",
                                              owner.Mediator, new ExecuteCommandHandler(CmdNewFinder),
                                              SR.MenuNewFinderCaption, SR.MenuNewFinderDesc, _shortcutHandler);
            subF2 = new AppContextMenuCommand("cmdDeleteAllFinders",
                                              owner.Mediator, new ExecuteCommandHandler(CmdDeleteAllFinder),
                                              SR.MenuFinderDeleteAllCaption, SR.MenuFinderDeleteAllDesc,
                                              _shortcutHandler);
            _treeSearchFolderRootContextMenu.MenuItems.AddRange(new MenuItem[] {subF1, sep.CloneMenu(), subF2});
            _treeSearchFolderContextMenu = new ContextMenu();
            subF1 = new AppContextMenuCommand("cmdMarkFinderItemsRead",
                                              owner.Mediator, new ExecuteCommandHandler(CmdMarkFinderItemsRead),
                                              SR.MenuCatchUpOnAllCaption, SR.MenuCatchUpOnAllDesc, _shortcutHandler);
            subF2 = new AppContextMenuCommand("cmdRenameFinder",
                                              owner.Mediator, new ExecuteCommandHandler(CmdRenameFinder),
                                              SR.MenuFinderRenameCaption, SR.MenuFinderRenameDesc, _shortcutHandler);
            subF3 = new AppContextMenuCommand("cmdRefreshFinder",
                                              owner.Mediator, new ExecuteCommandHandler(CmdRefreshFinder),
                                              SR.MenuRefreshFinderCaption, SR.MenuRefreshFinderDesc, _shortcutHandler);
            subF4 = new AppContextMenuCommand("cmdDeleteFinder",
                                              owner.Mediator, new ExecuteCommandHandler(CmdDeleteFinder),
                                              SR.MenuFinderDeleteCaption, SR.MenuFinderDeleteDesc, _shortcutHandler);
            AppContextMenuCommand subFinderShowFullText = new AppContextMenuCommand("cmdFinderShowFullItemText",
                                                                                    owner.Mediator,
                                                                                    new ExecuteCommandHandler(
                                                                                        CmdFinderToggleExcerptsFullItemText),
                                                                                    SR.MenuFinderShowExcerptsCaption,
                                                                                    SR.MenuFinderShowExcerptsDesc,
                                                                                    _shortcutHandler);
            subF6 = new AppContextMenuCommand("cmdShowFinderProperties",
                                              owner.Mediator, new ExecuteCommandHandler(CmdShowFinderProperties),
                                              SR.MenuShowFinderPropertiesCaption, SR.MenuShowFinderPropertiesDesc,
                                              _shortcutHandler);
            _treeSearchFolderContextMenu.MenuItems.AddRange(
                new MenuItem[]
                    {
                        subF1, subF2, subF3, sep.CloneMenu(), subF4, sep.CloneMenu(), subFinderShowFullText,
                        sep.CloneMenu(), subF6
                    });
            _treeTempSearchFolderContextMenu = new ContextMenu();
            subF1 = new AppContextMenuCommand("cmdMarkFinderItemsRead",
                                              owner.Mediator, new ExecuteCommandHandler(CmdMarkFinderItemsRead),
                                              SR.MenuCatchUpOnAllCaption, SR.MenuCatchUpOnAllDesc, _shortcutHandler);
            subF2 = new AppContextMenuCommand("cmdRefreshFinder",
                                              owner.Mediator, new ExecuteCommandHandler(CmdRefreshFinder),
                                              SR.MenuRefreshFinderCaption, SR.MenuRefreshFinderDesc, _shortcutHandler);
            subF3 = new AppContextMenuCommand("cmdSubscribeToFinderResult",
                                              owner.Mediator, new ExecuteCommandHandler(CmdSubscribeToFinderResult),
                                              SR.MenuSubscribeToFinderResultCaption, SR.MenuSubscribeToFinderResultDesc,
                                              _shortcutHandler);
            subF3.Enabled = false;
            subF4 = new AppContextMenuCommand("cmdShowFinderProperties",
                                              owner.Mediator, new ExecuteCommandHandler(CmdShowFinderProperties),
                                              SR.MenuShowFinderPropertiesCaption, SR.MenuShowFinderPropertiesDesc,
                                              _shortcutHandler);
            _treeTempSearchFolderContextMenu.MenuItems.AddRange(
                new MenuItem[]
                    {
                        subF1, subF2, sep.CloneMenu(), subF3, sep.CloneMenu(), subFinderShowFullText.CloneMenu(),
                        sep.CloneMenu(), subF4
                    });
            treeFeeds.ContextMenu = _treeRootContextMenu;
            _listContextMenu = new ContextMenu();
            AppContextMenuCommand subL0 = new AppContextMenuCommand("cmdMarkSelectedFeedItemsRead",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(owner.CmdMarkFeedItemsRead),
                                                                    SR.MenuCatchUpSelectedNodeCaption,
                                                                    SR.MenuCatchUpSelectedNodeDesc, 0, _shortcutHandler);
            AppContextMenuCommand subL1 = new AppContextMenuCommand("cmdMarkSelectedFeedItemsUnread",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(
                                                                        owner.CmdMarkFeedItemsUnread),
                                                                    SR.MenuMarkFeedItemsUnreadCaption,
                                                                    SR.MenuMarkFeedItemsUnreadDesc, 1, _shortcutHandler);
            AppContextMenuCommand subL2 = new AppContextMenuCommand("cmdFeedItemPostReply",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(owner.CmdPostReplyToItem),
                                                                    SR.MenuFeedItemPostReplyCaption,
                                                                    SR.MenuFeedItemPostReplyDesc, 5, _shortcutHandler);
            subL2.Enabled = false;
            AppContextMenuCommand subL3 = new AppContextMenuCommand("cmdFlagNewsItem",
                                                                    owner.Mediator, new ExecuteCommandHandler(CmdNop),
                                                                    SR.MenuFlagFeedItemCaption, SR.MenuFlagFeedItemDesc,
                                                                    1, _shortcutHandler);
            subL3.Enabled = false;
            AppContextMenuCommand subL3_sub1 = new AppContextMenuCommand("cmdFlagNewsItemForFollowUp",
                                                                         owner.Mediator,
                                                                         new ExecuteCommandHandler(
                                                                             CmdFlagNewsItemForFollowUp),
                                                                         SR.MenuFlagFeedItemFollowUpCaption,
                                                                         SR.MenuFlagFeedItemFollowUpDesc, 1,
                                                                         _shortcutHandler);
            AppContextMenuCommand subL3_sub2 = new AppContextMenuCommand("cmdFlagNewsItemForReview",
                                                                         owner.Mediator,
                                                                         new ExecuteCommandHandler(
                                                                             CmdFlagNewsItemForReview),
                                                                         SR.MenuFlagFeedItemReviewCaption,
                                                                         SR.MenuFlagFeedItemReviewDesc, 1,
                                                                         _shortcutHandler);
            AppContextMenuCommand subL3_sub3 = new AppContextMenuCommand("cmdFlagNewsItemForReply",
                                                                         owner.Mediator,
                                                                         new ExecuteCommandHandler(
                                                                             CmdFlagNewsItemForReply),
                                                                         SR.MenuFlagFeedItemReplyCaption,
                                                                         SR.MenuFlagFeedItemReplyDesc, 1,
                                                                         _shortcutHandler);
            AppContextMenuCommand subL3_sub4 = new AppContextMenuCommand("cmdFlagNewsItemRead",
                                                                         owner.Mediator,
                                                                         new ExecuteCommandHandler(CmdFlagNewsItemRead),
                                                                         SR.MenuFlagFeedItemReadCaption,
                                                                         SR.MenuFlagFeedItemReadDesc, 1,
                                                                         _shortcutHandler);
            AppContextMenuCommand subL3_sub5 = new AppContextMenuCommand("cmdFlagNewsItemForward",
                                                                         owner.Mediator,
                                                                         new ExecuteCommandHandler(
                                                                             CmdFlagNewsItemForward),
                                                                         SR.MenuFlagFeedItemForwardCaption,
                                                                         SR.MenuFlagFeedItemForwardDesc, 1,
                                                                         _shortcutHandler);
            AppContextMenuCommand subL3_sub8 = new AppContextMenuCommand("cmdFlagNewsItemComplete",
                                                                         owner.Mediator,
                                                                         new ExecuteCommandHandler(
                                                                             CmdFlagNewsItemComplete),
                                                                         SR.MenuFlagFeedItemCompleteCaption,
                                                                         SR.MenuFlagFeedItemCompleteDesc, 1,
                                                                         _shortcutHandler);
            AppContextMenuCommand subL3_sub9 = new AppContextMenuCommand("cmdFlagNewsItemNone",
                                                                         owner.Mediator,
                                                                         new ExecuteCommandHandler(CmdFlagNewsItemNone),
                                                                         SR.MenuFlagFeedItemClearCaption,
                                                                         SR.MenuFlagFeedItemClearDesc, 1,
                                                                         _shortcutHandler);
            subL3.MenuItems.AddRange(
                new MenuItem[]
                    {
                        subL3_sub1, subL3_sub2, subL3_sub3, subL3_sub4, subL3_sub5, sep.CloneMenu(), subL3_sub8,
                        sep.CloneMenu(), subL3_sub9
                    });
            AppContextMenuCommand subL10 = new AppContextMenuCommand("cmdCopyNewsItem",
                                                                     owner.Mediator,
                                                                     new ExecuteCommandHandler(CmdCopyNewsItem),
                                                                     SR.MenuCopyFeedItemCaption, SR.MenuCopyFeedItemDesc,
                                                                     1, _shortcutHandler);
            AppContextMenuCommand subL10_sub1 = new AppContextMenuCommand("cmdCopyNewsItemLinkToClipboard",
                                                                          owner.Mediator,
                                                                          new ExecuteCommandHandler(
                                                                              CmdCopyNewsItemLinkToClipboard),
                                                                          SR.MenuCopyFeedItemLinkToClipboardCaption,
                                                                          SR.MenuCopyFeedItemLinkToClipboardDesc, 1,
                                                                          _shortcutHandler);
            AppContextMenuCommand subL10_sub2 = new AppContextMenuCommand("cmdCopyNewsItemTitleLinkToClipboard",
                                                                          owner.Mediator,
                                                                          new ExecuteCommandHandler(
                                                                              CmdCopyNewsItemTitleLinkToClipboard),
                                                                          SR.MenuCopyFeedItemTitleLinkToClipboardCaption,
                                                                          SR.MenuCopyFeedItemTitleLinkToClipboardDesc, 1,
                                                                          _shortcutHandler);
            AppContextMenuCommand subL10_sub3 = new AppContextMenuCommand("cmdCopyNewsItemContentToClipboard",
                                                                          owner.Mediator,
                                                                          new ExecuteCommandHandler(
                                                                              CmdCopyNewsItemContentToClipboard),
                                                                          SR.MenuCopyFeedItemContentToClipboardCaption,
                                                                          SR.MenuCopyFeedItemContentToClipboardDesc, 1,
                                                                          _shortcutHandler);
            subL10.MenuItems.AddRange(new MenuItem[] {subL10_sub1, subL10_sub2, subL10_sub3});
            AppContextMenuCommand subL4 = new AppContextMenuCommand("cmdColumnChooserMain",
                                                                    owner.Mediator, new ExecuteCommandHandler(CmdNop),
                                                                    SR.MenuColumnChooserCaption,
                                                                    SR.MenuColumnChooserDesc, _shortcutHandler);
            foreach (string colID in Enum.GetNames(typeof (NewsItemSortField)))
            {
                AppContextMenuCommand subL4_subColumn = new AppContextMenuCommand("cmdListviewColumn." + colID,
                                                                                  owner.Mediator,
                                                                                  new ExecuteCommandHandler(
                                                                                      CmdToggleListviewColumn),
                                                                                  SR.Keys.GetString(
                                                                                      "MenuColumnChooser" + colID +
                                                                                      "Caption"),
                                                                                  SR.Keys.GetString(
                                                                                      "MenuColumnChooser" + colID +
                                                                                      "Desc"), _shortcutHandler);
                subL4.MenuItems.AddRange(new MenuItem[] {subL4_subColumn});
            }
            AppContextMenuCommand subL4_subUseCatLayout =
                new AppContextMenuCommand("cmdColumnChooserUseCategoryLayoutGlobal",
                                          owner.Mediator,
                                          new ExecuteCommandHandler(CmdColumnChooserUseCategoryLayoutGlobal),
                                          SR.MenuColumnChooserUseCategoryLayoutGlobalCaption,
                                          SR.MenuColumnChooserUseCategoryLayoutGlobalDesc, _shortcutHandler);
            AppContextMenuCommand subL4_subUseFeedLayout =
                new AppContextMenuCommand("cmdColumnChooserUseFeedLayoutGlobal",
                                          owner.Mediator, new ExecuteCommandHandler(CmdColumnChooserUseFeedLayoutGlobal),
                                          SR.MenuColumnChooserUseFeedLayoutGlobalCaption,
                                          SR.MenuColumnChooserUseFeedLayoutGlobalDesc, _shortcutHandler);
            AppContextMenuCommand subL4_subResetLayout = new AppContextMenuCommand("cmdColumnChooserResetToDefault",
                                                                                   owner.Mediator,
                                                                                   new ExecuteCommandHandler(
                                                                                       CmdColumnChooserResetToDefault),
                                                                                   SR.
                                                                                       MenuColumnChooserResetLayoutToDefaultCaption,
                                                                                   SR.
                                                                                       MenuColumnChooserResetLayoutToDefaultDesc,
                                                                                   _shortcutHandler);
            subL4.MenuItems.AddRange(
                new MenuItem[]
                    {
                        sep.CloneMenu(), subL4_subUseCatLayout, subL4_subUseFeedLayout, sep.CloneMenu(),
                        subL4_subResetLayout
                    });
            AppContextMenuCommand subL5 = new AppContextMenuCommand("cmdDeleteSelectedNewsItems",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(
                                                                        owner.CmdDeleteSelectedFeedItems),
                                                                    SR.MenuDeleteSelectedFeedItemsCaption,
                                                                    SR.MenuDeleteSelectedFeedItemsDesc, _shortcutHandler);
            AppContextMenuCommand subL6 = new AppContextMenuCommand("cmdRestoreSelectedNewsItems",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(
                                                                        owner.CmdRestoreSelectedFeedItems),
                                                                    SR.MenuRestoreSelectedFeedItemsCaption,
                                                                    SR.MenuRestoreSelectedFeedItemsDesc,
                                                                    _shortcutHandler);
            subL6.Visible = false;
            AppContextMenuCommand subL7 = new AppContextMenuCommand("cmdWatchItemComments",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(CmdWatchItemComments),
                                                                    SR.MenuFeedItemWatchCommentsCaption,
                                                                    SR.MenuFeedItemWatchCommentsDesc, 5,
                                                                    _shortcutHandler);
            subL7.Enabled = false;
            AppContextMenuCommand subL8 = new AppContextMenuCommand("cmdViewOutlookReadingPane",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(CmdViewOutlookReadingPane),
                                                                    SR.MenuViewOutlookReadingPane,
                                                                    SR.MenuViewOutlookReadingPane, _shortcutHandler);
            AppContextMenuCommand subL9 = new AppContextMenuCommand("cmdDownloadAttachment",
                                                                    owner.Mediator, new ExecuteCommandHandler(CmdNop),
                                                                    SR.MenuDownloadAttachmentCaption,
                                                                    SR.MenuDownloadAttachmentDesc, _shortcutHandler);
            subL9.Visible = false;
            _listContextMenuDownloadAttachment = subL9;
            _listContextMenuDeleteItemsSeparator = sep.CloneMenu();
            _listContextMenuDownloadAttachmentsSeparator = sep.CloneMenu();
            _listContextMenu.MenuItems.AddRange(
                new MenuItem[]
                    {
                        subL2, subL3, subL0, subL1, subL7, sep.CloneMenu(), subL10,
                        _listContextMenuDownloadAttachmentsSeparator, subL9, _listContextMenuDeleteItemsSeparator, subL5,
                        subL6, sep.CloneMenu(), subL4, subL8
                    });
            listFeedItems.ContextMenu = _listContextMenu;
            listFeedItemsO.ContextMenu = _listContextMenu;
            _treeLocalFeedContextMenu = new ContextMenu();
            AppContextMenuCommand subTL1 = new AppContextMenuCommand("cmdDeleteAllNewsItems",
                                                                     owner.Mediator,
                                                                     new ExecuteCommandHandler(
                                                                         owner.CmdDeleteAllFeedItems),
                                                                     SR.MenuDeleteAllFeedItemsCaption,
                                                                     SR.MenuDeleteAllFeedItemsDesc, 1, _shortcutHandler);
            _treeLocalFeedContextMenu.MenuItems.AddRange(new MenuItem[] {subTL1});
            _docTabContextMenu = new ContextMenu();
            AppContextMenuCommand subDT1 = new AppContextMenuCommand("cmdDocTabCloseThis",
                                                                     owner.Mediator,
                                                                     new ExecuteCommandHandler(CmdDocTabCloseSelected),
                                                                     SR.MenuDocTabsCloseCurrentCaption,
                                                                     SR.MenuDocTabsCloseCurrentDesc, 1, _shortcutHandler);
            AppContextMenuCommand subDT2 = new AppContextMenuCommand("cmdDocTabCloseAllOnStrip",
                                                                     owner.Mediator,
                                                                     new ExecuteCommandHandler(CmdDocTabCloseAllOnStrip),
                                                                     SR.MenuDocTabsCloseAllOnStripCaption,
                                                                     SR.MenuDocTabsCloseAllOnStripDesc, 2,
                                                                     _shortcutHandler);
            AppContextMenuCommand subDT3 = new AppContextMenuCommand("cmdDocTabCloseAll",
                                                                     owner.Mediator,
                                                                     new ExecuteCommandHandler(CmdDocTabCloseAll),
                                                                     SR.MenuDocTabsCloseAllCaption,
                                                                     SR.MenuDocTabsCloseAllDesc, 3, _shortcutHandler);
            AppContextMenuCommand subDT4 = new AppContextMenuCommand("cmdDocTabLayoutHorizontal",
                                                                     owner.Mediator,
                                                                     new ExecuteCommandHandler(CmdDocTabLayoutHorizontal),
                                                                     SR.MenuDocTabsLayoutHorizontalCaption,
                                                                     SR.MenuDocTabsLayoutHorizontalDesc,
                                                                     _shortcutHandler);
            subDT4.Checked = (_docContainer.LayoutSystem.SplitMode == Orientation.Horizontal);
            AppContextMenuCommand subDT5 = new AppContextMenuCommand("cmdFeedDetailLayoutPosition",
                                                                     owner.Mediator, new ExecuteCommandHandler(CmdNop),
                                                                     SR.MenuFeedDetailLayoutCaption,
                                                                     SR.MenuFeedDetailLayoutDesc, _shortcutHandler);
            AppContextMenuCommand subSub1 = new AppContextMenuCommand("cmdFeedDetailLayoutPosTop",
                                                                      owner.Mediator,
                                                                      new ExecuteCommandHandler(
                                                                          CmdFeedDetailLayoutPosTop),
                                                                      SR.MenuFeedDetailLayoutTopCaption,
                                                                      SR.MenuFeedDetailLayoutTopDesc, _shortcutHandler);
            subSub1.Checked = true;
            AppContextMenuCommand subSub2 = new AppContextMenuCommand("cmdFeedDetailLayoutPosLeft",
                                                                      owner.Mediator,
                                                                      new ExecuteCommandHandler(
                                                                          CmdFeedDetailLayoutPosLeft),
                                                                      SR.MenuFeedDetailLayoutLeftCaption,
                                                                      SR.MenuFeedDetailLayoutLeftDesc, _shortcutHandler);
            AppContextMenuCommand subSub3 = new AppContextMenuCommand("cmdFeedDetailLayoutPosRight",
                                                                      owner.Mediator,
                                                                      new ExecuteCommandHandler(
                                                                          CmdFeedDetailLayoutPosRight),
                                                                      SR.MenuFeedDetailLayoutRightCaption,
                                                                      SR.MenuFeedDetailLayoutRightDesc, _shortcutHandler);
            AppContextMenuCommand subSub4 = new AppContextMenuCommand("cmdFeedDetailLayoutPosBottom",
                                                                      owner.Mediator,
                                                                      new ExecuteCommandHandler(
                                                                          CmdFeedDetailLayoutPosBottom),
                                                                      SR.MenuFeedDetailLayoutBottomCaption,
                                                                      SR.MenuFeedDetailLayoutBottomDesc,
                                                                      _shortcutHandler);
            subDT5.MenuItems.AddRange(new MenuItem[] {subSub1, subSub2, subSub3, subSub4});
            _docTabContextMenu.MenuItems.AddRange(
                new MenuItem[] {subDT1, subDT2, subDT3, sep.CloneMenu(), subDT4, sep.CloneMenu(), subDT5});
            _notifyContextMenu = new ContextMenu();
            AppContextMenuCommand subT1 = new AppContextMenuCommand("cmdShowGUI",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(owner.CmdShowMainGui),
                                                                    SR.MenuShowMainGuiCaption, SR.MenuShowMainGuiDesc,
                                                                    _shortcutHandler);
            subT1.DefaultItem = true;
            AppContextMenuCommand subT1_1 = new AppContextMenuCommand("cmdRefreshFeeds",
                                                                      owner.Mediator,
                                                                      new ExecuteCommandHandler(owner.CmdRefreshFeeds),
                                                                      SR.MenuUpdateAllFeedsCaption,
                                                                      SR.MenuUpdateAllFeedsDesc, _shortcutHandler);
            AppContextMenuCommand subT2 = new AppContextMenuCommand("cmdShowMainAppOptions",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(owner.CmdShowOptions),
                                                                    SR.MenuAppOptionsCaption, SR.MenuAppOptionsDesc, 10,
                                                                    _shortcutHandler);
            AppContextMenuCommand subT5 = new AppContextMenuCommand("cmdShowConfiguredAlertWindows",
                                                                    owner.Mediator, new ExecuteCommandHandler(CmdNop),
                                                                    SR.MenuShowAlertWindowsCaption,
                                                                    SR.MenuShowAlertWindowsDesc, _shortcutHandler);
            AppContextMenuCommand subT5_1 = new AppContextMenuCommand("cmdShowAlertWindowNone",
                                                                      owner.Mediator,
                                                                      new ExecuteCommandHandler(
                                                                          owner.CmdShowAlertWindowNone),
                                                                      SR.MenuShowNoneAlertWindowsCaption,
                                                                      SR.MenuShowNoneAlertWindowsDesc, _shortcutHandler);
            subT5_1.Checked = (owner.Preferences.ShowAlertWindow == DisplayFeedAlertWindow.None);
            AppContextMenuCommand subT5_2 = new AppContextMenuCommand("cmdShowAlertWindowConfiguredFeeds",
                                                                      owner.Mediator,
                                                                      new ExecuteCommandHandler(
                                                                          owner.CmdShowAlertWindowConfigPerFeed),
                                                                      SR.MenuShowConfiguredFeedAlertWindowsCaption,
                                                                      SR.MenuShowConfiguredFeedAlertWindowsDesc,
                                                                      _shortcutHandler);
            subT5_2.Checked = (owner.Preferences.ShowAlertWindow == DisplayFeedAlertWindow.AsConfiguredPerFeed);
            AppContextMenuCommand subT5_3 = new AppContextMenuCommand("cmdShowAlertWindowAll",
                                                                      owner.Mediator,
                                                                      new ExecuteCommandHandler(
                                                                          owner.CmdShowAlertWindowAll),
                                                                      SR.MenuShowAllAlertWindowsCaption,
                                                                      SR.MenuShowAllAlertWindowsDesc, _shortcutHandler);
            subT5_3.Checked = (owner.Preferences.ShowAlertWindow == DisplayFeedAlertWindow.All);
            subT5.MenuItems.AddRange(new MenuItem[] {subT5_1, subT5_2, subT5_3});
            AppContextMenuCommand subT6 = new AppContextMenuCommand("cmdShowNewItemsReceivedBalloon",
                                                                    owner.Mediator,
                                                                    new ExecuteCommandHandler(
                                                                        owner.CmdToggleShowNewItemsReceivedBalloon),
                                                                    SR.MenuShowNewItemsReceivedBalloonCaption,
                                                                    SR.MenuShowNewItemsReceivedBalloonDesc,
                                                                    _shortcutHandler);
            subT6.Checked = owner.Preferences.ShowNewItemsReceivedBalloon;
            AppContextMenuCommand subT10 = new AppContextMenuCommand("cmdCloseExit",
                                                                     owner.Mediator,
                                                                     new ExecuteCommandHandler(owner.CmdExitApp),
                                                                     SR.MenuAppCloseExitCaption, SR.MenuAppCloseExitDesc,
                                                                     _shortcutHandler);
            _notifyContextMenu.MenuItems.AddRange(
                new MenuItem[]
                    {
                        subT1, subT1_1, sep.CloneMenu(), sub1.CloneMenu(), subT2, sep.CloneMenu(), subT5, subT6,
                        sep.CloneMenu(), subT10
                    });
        }
 
        private  void CreateIGToolbars()
        {
            ultraToolbarsManager = new ToolbarHelper.RssBanditToolbarManager(components);
            toolbarHelper = new ToolbarHelper(ultraToolbarsManager);
            historyMenuManager = new HistoryMenuManager();
            historyMenuManager.OnNavigateBack += OnHistoryNavigateGoBackItemClick;
            historyMenuManager.OnNavigateForward +=
                OnHistoryNavigateGoForwardItemClick;
            _Main_Toolbars_Dock_Area_Left = new UltraToolbarsDockArea();
            _Main_Toolbars_Dock_Area_Right = new UltraToolbarsDockArea();
            _Main_Toolbars_Dock_Area_Top = new UltraToolbarsDockArea();
            _Main_Toolbars_Dock_Area_Bottom = new UltraToolbarsDockArea();
            ((ISupportInitialize) (ultraToolbarsManager)).BeginInit();
            ultraToolbarsManager.DesignerFlags = 1;
            ultraToolbarsManager.DockWithinContainer = this;
            ultraToolbarsManager.ImageListSmall = _allToolImages;
            ultraToolbarsManager.ShowFullMenusDelay = 500;
            ultraToolbarsManager.ShowToolTips = true;
            ultraToolbarsManager.Style = ToolbarStyle.Office2003;
            _Main_Toolbars_Dock_Area_Left.AccessibleRole = AccessibleRole.Grouping;
            _Main_Toolbars_Dock_Area_Left.BackColor = Color.FromArgb(158, 190, 245);
            _Main_Toolbars_Dock_Area_Left.DockedPosition = DockedPosition.Left;
            _Main_Toolbars_Dock_Area_Left.ForeColor = SystemColors.ControlText;
            _Main_Toolbars_Dock_Area_Left.Location = new Point(0, 99);
            _Main_Toolbars_Dock_Area_Left.Name = "_Main_Toolbars_Dock_Area_Left";
            _Main_Toolbars_Dock_Area_Left.Size = new Size(0, 212);
            _Main_Toolbars_Dock_Area_Left.ToolbarsManager = ultraToolbarsManager;
            _Main_Toolbars_Dock_Area_Right.AccessibleRole = AccessibleRole.Grouping;
            _Main_Toolbars_Dock_Area_Right.BackColor = Color.FromArgb(158, 190, 245);
            _Main_Toolbars_Dock_Area_Right.DockedPosition = DockedPosition.Right;
            _Main_Toolbars_Dock_Area_Right.ForeColor = SystemColors.ControlText;
            _Main_Toolbars_Dock_Area_Right.Location = new Point(507, 99);
            _Main_Toolbars_Dock_Area_Right.Name = "_Main_Toolbars_Dock_Area_Right";
            _Main_Toolbars_Dock_Area_Right.Size = new Size(0, 212);
            _Main_Toolbars_Dock_Area_Right.ToolbarsManager = ultraToolbarsManager;
            _Main_Toolbars_Dock_Area_Top.AccessibleRole = AccessibleRole.Grouping;
            _Main_Toolbars_Dock_Area_Top.BackColor = Color.FromArgb(158, 190, 245);
            _Main_Toolbars_Dock_Area_Top.DockedPosition = DockedPosition.Top;
            _Main_Toolbars_Dock_Area_Top.ForeColor = SystemColors.ControlText;
            _Main_Toolbars_Dock_Area_Top.Location = new Point(0, 0);
            _Main_Toolbars_Dock_Area_Top.Name = "_Main_Toolbars_Dock_Area_Top";
            _Main_Toolbars_Dock_Area_Top.Size = new Size(507, 99);
            _Main_Toolbars_Dock_Area_Top.ToolbarsManager = ultraToolbarsManager;
            _Main_Toolbars_Dock_Area_Bottom.AccessibleRole = AccessibleRole.Grouping;
            _Main_Toolbars_Dock_Area_Bottom.BackColor = Color.FromArgb(158, 190, 245);
            _Main_Toolbars_Dock_Area_Bottom.DockedPosition = DockedPosition.Bottom;
            _Main_Toolbars_Dock_Area_Bottom.ForeColor = SystemColors.ControlText;
            _Main_Toolbars_Dock_Area_Bottom.Location = new Point(0, 311);
            _Main_Toolbars_Dock_Area_Bottom.Name = "_Main_Toolbars_Dock_Area_Bottom";
            _Main_Toolbars_Dock_Area_Bottom.Size = new Size(507, 0);
            _Main_Toolbars_Dock_Area_Bottom.ToolbarsManager = ultraToolbarsManager;
            Controls.Add(_Main_Toolbars_Dock_Area_Left);
            Controls.Add(_Main_Toolbars_Dock_Area_Right);
            Controls.Add(_Main_Toolbars_Dock_Area_Top);
            Controls.Add(_Main_Toolbars_Dock_Area_Bottom);
            toolbarHelper.CreateToolbars(this, owner, _shortcutHandler);
            ((ISupportInitialize) (ultraToolbarsManager)).EndInit();
            ultraToolbarsManager.ToolClick += OnAnyToolbarToolClick;
            ultraToolbarsManager.BeforeToolDropdown += OnToolbarBeforeToolDropdown;
            owner.Mediator.BeforeCommandStateChanged += OnMediatorBeforeCommandStateChanged;
            owner.Mediator.AfterCommandStateChanged += OnMediatorAfterCommandStateChanged;
        }
 
        private  void InitDocumentManager()
        {
            _docContainer.SuspendLayout();
            _docContainer.LayoutSystem.SplitMode = Orientation.Vertical;
            _docFeedDetails.TabImage = _listImages.Images[0];
            _docFeedDetails.Tag = this;
            if (Win32.IsOSAtLeastWindowsXP)
                ColorEx.ColorizeOneNote(_docFeedDetails, 0);
            _docContainer.ActiveDocument = _docFeedDetails;
            _docContainer.ShowControlContextMenu += OnDocContainerShowControlContextMenu;
            _docContainer.MouseDown += OnDocContainerMouseDown;
            _docContainer.DocumentClosing += OnDocContainerDocumentClosing;
            _docContainer.ActiveDocumentChanged += OnDocContainerActiveDocumentChanged;
            _docContainer.DoubleClick += OnDocContainerDoubleClick;
            panelFeedDetails.Dock = DockStyle.Fill;
            _docContainer.ResumeLayout(false);
        }
 
        private  void InitDockHosts()
        {
            sandDockManager.DockingFinished += OnDockManagerDockingFinished;
            sandDockManager.DockingStarted += OnDockManagerDockStarted;
        }
 
        private  void InitTrayIcon()
        {
            if (components == null)
                components = new Container();
            _trayAni = new NotifyIconAnimation(components);
            _trayAni.DoubleClick += OnTrayIconDoubleClick;
            _trayAni.BalloonClick += OnTrayIconDoubleClick;
            _trayAni.BalloonTimeout += OnTrayAniBalloonTimeoutClose;
            _trayAni.ContextMenu = _notifyContextMenu;
            _trayManager = new TrayStateManager(_trayAni, null);
            _trayManager.SetState(ApplicationTrayState.NormalIdle);
        }
 
        private  void InitStatusBar()
        {
            _status.PanelClick += OnStatusPanelClick;
            _status.LocationChanged += OnStatusPanelLocationChanged;
            statusBarBrowserProgress.Width = 0;
            progressBrowser.Visible = false;
        }
 
        private  void OnDockManagerDockStarted(object sender, EventArgs e)
        {
            SetBrowserStatusBarText(SR.DragDockablePanelInfo);
        }
 
        private  void OnDockManagerDockingFinished(object sender, EventArgs e)
        {
            SetBrowserStatusBarText(String.Empty);
        }
 
        private static  void CmdNop(ICommand sender)
        {
        }
 
        internal  void CmdOpenLinkInExternalBrowser(ICommand sender)
        {
            owner.NavigateToUrlInExternalBrowser(UrlText);
        }
 
        internal  void CmdToggleMainTBViewState(ICommand sender)
        {
            bool enable = owner.Mediator.IsChecked(sender);
            owner.Mediator.SetChecked(enable, "cmdToggleMainTBViewState");
            toolbarHelper.SetToolbarVisible(Resource.Toolbar.MainTools, enable);
        }
 
        internal  void CmdToggleWebTBViewState(ICommand sender)
        {
            bool enable = owner.Mediator.IsChecked(sender);
            owner.Mediator.SetChecked(enable, "cmdToggleWebTBViewState");
            toolbarHelper.SetToolbarVisible(Resource.Toolbar.WebTools, enable);
        }
 
        internal  void CmdToggleWebSearchTBViewState(ICommand sender)
        {
            bool enable = owner.Mediator.IsChecked(sender);
            owner.Mediator.SetChecked(enable, "cmdToggleWebSearchTBViewState");
            toolbarHelper.SetToolbarVisible(Resource.Toolbar.SearchTools, enable);
        }
 
        private  void OnToolbarBeforeToolDropdown(object sender, BeforeToolDropdownEventArgs e)
        {
            if (e.Tool.Key == "mnuViewToolbars")
            {
                owner.Mediator.SetChecked(toolbarHelper.IsToolbarVisible(Resource.Toolbar.MainTools),
                                          "cmdToggleMainTBViewState");
                owner.Mediator.SetChecked(toolbarHelper.IsToolbarVisible(Resource.Toolbar.WebTools),
                                          "cmdToggleWebTBViewState");
                owner.Mediator.SetChecked(toolbarHelper.IsToolbarVisible(Resource.Toolbar.SearchTools),
                                          "cmdToggleWebSearchTBViewState");
            }
            else if (e.Tool.Key == "cmdColumnChooserMain")
            {
                RefreshListviewColumnContextMenu();
            }
            else if (e.Tool.Key == "cmdBrowserGoBack" || e.Tool.Key == "cmdBrowserGoForward")
            {
                AppPopupMenuCommand cmd = e.Tool as AppPopupMenuCommand;
                if (cmd != null)
                {
                    if (cmd.Tools.Count == 0)
                    {
                        e.Cancel = true;
                        OnAnyToolbarToolClick(this, new ToolClickEventArgs(e.Tool, null));
                    }
                }
            }
        }
 
        internal  void ToggleNavigationPaneView(NavigationPaneView pane)
        {
            if (!Navigator.Visible)
                OnNavigatorExpandImageClick(this, EventArgs.Empty);
            if (pane == NavigationPaneView.RssSearch)
            {
                Navigator.SelectedGroup = Navigator.Groups[Resource.NavigatorGroup.RssSearch];
                owner.Mediator.SetChecked("+cmdToggleRssSearchTabState");
                owner.Mediator.SetChecked("-cmdToggleTreeViewState");
            }
            else
            {
                Navigator.SelectedGroup = Navigator.Groups[Resource.NavigatorGroup.Subscriptions];
                owner.Mediator.SetChecked("-cmdToggleRssSearchTabState");
                owner.Mediator.SetChecked("+cmdToggleTreeViewState");
            }
        }
 
        internal  void CmdDockShowSubscriptions(ICommand sender)
        {
            ToggleNavigationPaneView(NavigationPaneView.Subscriptions);
        }
 
        internal  void CmdDockShowRssSearch(ICommand sender)
        {
            ToggleNavigationPaneView(NavigationPaneView.RssSearch);
        }
 
        private  void CmdDocTabCloseSelected(ICommand sender)
        {
            Point pos = (_contextMenuCalledAt != Point.Empty ? _contextMenuCalledAt : Cursor.Position);
            ControlLayoutSystem underMouse =
                _docContainer.GetLayoutSystemAt(_docContainer.PointToClient(pos)) as ControlLayoutSystem;
            if (underMouse != null)
            {
                DockControl docUnderMouse = underMouse.GetControlAt(_docContainer.PointToClient(pos));
                if (docUnderMouse != null)
                {
                    RemoveDocTab(docUnderMouse);
                    return;
                }
            }
            RemoveDocTab(_docContainer.ActiveDocument);
        }
 
        private  void CmdDocTabCloseAllOnStrip(ICommand sender)
        {
            Point pos = (_contextMenuCalledAt != Point.Empty ? _contextMenuCalledAt : Cursor.Position);
            ControlLayoutSystem underMouse =
                _docContainer.GetLayoutSystemAt(_docContainer.PointToClient(pos)) as ControlLayoutSystem;
            if (underMouse == null)
                underMouse = _docContainer.ActiveDocument.LayoutSystem;
            DockControl[] docs = new DockControl[underMouse.Controls.Count];
            underMouse.Controls.CopyTo(docs, 0);
            foreach (DockControl doc in docs)
            {
                ITabState state = (ITabState) doc.Tag;
                if (state.CanClose)
                    _docContainer.RemoveDocument(doc);
            }
        }
 
        private  void CmdDocTabCloseAll(ICommand sender)
        {
            DockControl[] docs = new DockControl[_docContainer.Documents.Length];
            _docContainer.Documents.CopyTo(docs, 0);
            foreach (DockControl doc in docs)
            {
                ITabState state = (ITabState) doc.Tag;
                if (state.CanClose)
                    _docContainer.RemoveDocument(doc);
            }
        }
 
        private  void CmdDocTabLayoutHorizontal(ICommand sender)
        {
            if (owner.Mediator.IsChecked("cmdDocTabLayoutHorizontal"))
            {
                _docContainer.LayoutSystem.SplitMode = Orientation.Vertical;
            }
            else
            {
                _docContainer.LayoutSystem.SplitMode = Orientation.Horizontal;
            }
            owner.Mediator.SetChecked((_docContainer.LayoutSystem.SplitMode == Orientation.Horizontal),
                                      "cmdDocTabLayoutHorizontal");
        }
 
        internal  void CmdViewOutlookReadingPane(ICommand sender)
        {
            if (sender != null)
            {
                bool enable = owner.Mediator.IsChecked(sender);
                owner.Mediator.SetChecked(enable, "cmdViewOutlookReadingPane");
                ShowOutlookReadingPane(enable);
            }
        }
 
        private  void ShowOutlookReadingPane(bool enable)
        {
            if (enable)
            {
                ThreadedListViewItem[] items = new ThreadedListViewItem[listFeedItems.Items.Count];
                int ind = 0;
                foreach (ThreadedListViewItem lvi in listFeedItems.Items)
                {
                    items[ind++] = lvi;
                }
                listFeedItemsO.Clear();
                listFeedItemsO.AddRange(items);
                listFeedItems.Visible = false;
                listFeedItemsO.Visible = true;
            }
            else
            {
                listFeedItems.Visible = true;
                listFeedItemsO.Visible = false;
                listFeedItemsO.Clear();
            }
        }
 
        internal  void CmdFeedDetailLayoutPosTop(ICommand sender)
        {
            SetFeedDetailLayout(DockStyle.Top);
        }
 
        internal  void CmdFeedDetailLayoutPosBottom(ICommand sender)
        {
            SetFeedDetailLayout(DockStyle.Bottom);
        }
 
        internal  void CmdFeedDetailLayoutPosLeft(ICommand sender)
        {
            SetFeedDetailLayout(DockStyle.Left);
        }
 
        internal  void CmdFeedDetailLayoutPosRight(ICommand sender)
        {
            SetFeedDetailLayout(DockStyle.Right);
        }
 
        public  void CmdFlagNewsItemForFollowUp(ICommand sender)
        {
            if (CurrentSelectedFeedItem != null)
            {
                MarkFeedItemsFlagged(Flagged.FollowUp);
            }
        }
 
        public  void CmdFlagNewsItemNone(ICommand sender)
        {
            if (CurrentSelectedFeedItem != null)
            {
                MarkFeedItemsFlagged(Flagged.None);
            }
        }
 
        public  void CmdFlagNewsItemComplete(ICommand sender)
        {
            if (CurrentSelectedFeedItem != null)
            {
                MarkFeedItemsFlagged(Flagged.Complete);
            }
        }
 
        public  void CmdFlagNewsItemForward(ICommand sender)
        {
            if (CurrentSelectedFeedItem != null)
            {
                MarkFeedItemsFlagged(Flagged.Forward);
            }
        }
 
        public  void CmdFlagNewsItemRead(ICommand sender)
        {
            if (CurrentSelectedFeedItem != null)
            {
                MarkFeedItemsFlagged(Flagged.Read);
            }
        }
 
        public  void CmdFlagNewsItemForReply(ICommand sender)
        {
            if (CurrentSelectedFeedItem != null)
            {
                MarkFeedItemsFlagged(Flagged.Reply);
            }
        }
 
        public  void CmdFlagNewsItemForReview(ICommand sender)
        {
            if (CurrentSelectedFeedItem != null)
            {
                MarkFeedItemsFlagged(Flagged.Review);
            }
        }
 
        private  void CmdCopyFeed(ICommand sender)
        {
            if (sender is AppContextMenuCommand)
                CurrentSelectedFeedsNode = null;
        }
 
        private  void CmdCopyFeedLinkToClipboard(ICommand sender)
        {
            TreeFeedsNodeBase feedsNode = CurrentSelectedFeedsNode;
            if (feedsNode != null && feedsNode.Type == FeedNodeType.Feed && feedsNode.DataKey != null)
            {
                if (owner.FeedHandler.FeedsTable.ContainsKey(feedsNode.DataKey))
                    Clipboard.SetDataObject(feedsNode.DataKey);
            }
            if (sender is AppContextMenuCommand)
                CurrentSelectedFeedsNode = null;
        }
 
        private  void CmdCopyFeedHomeLinkToClipboard(ICommand sender)
        {
            TreeFeedsNodeBase feedsNode = CurrentSelectedFeedsNode;
            if (feedsNode != null && feedsNode.Type == FeedNodeType.Feed)
            {
                IFeedDetails fd = owner.GetFeedInfo(feedsNode.DataKey);
                string link;
                if (fd != null)
                {
                    link = fd.Link;
                }
                else
                {
                    link = feedsNode.DataKey;
                }
                if (!string.IsNullOrEmpty(link))
                {
                    Clipboard.SetDataObject(link);
                }
            }
            if (sender is AppContextMenuCommand)
                CurrentSelectedFeedsNode = null;
        }
 
        private  void CmdCopyFeedHomeTitleLinkToClipboard(ICommand sender)
        {
            TreeFeedsNodeBase feedsNode = CurrentSelectedFeedsNode;
            if (feedsNode != null && feedsNode.Type == FeedNodeType.Feed)
            {
                IFeedDetails fd = owner.GetFeedInfo(feedsNode.DataKey);
                string link, title;
                if (fd != null)
                {
                    link = fd.Link;
                    title = fd.Title;
                }
                else
                {
                    link = feedsNode.DataKey;
                    title = feedsNode.Text;
                }
                if (!string.IsNullOrEmpty(link))
                {
                    Clipboard.SetDataObject(
                        String.Format("<a href=\"{0}\" title=\"{1}\">{2}</a>", link, title, feedsNode.Text));
                }
            }
            if (sender is AppContextMenuCommand)
                CurrentSelectedFeedsNode = null;
        }
 
        private static  void CmdCopyNewsItem(ICommand sender)
        {
        }
 
        private  void CmdCopyNewsItemLinkToClipboard(ICommand sender)
        {
            if (listFeedItems.SelectedItems.Count == 0)
                return;
            StringBuilder data = new StringBuilder();
            for (int i = 0; i < listFeedItems.SelectedItems.Count; i++)
            {
                NewsItem item = (NewsItem) ((ThreadedListViewItem) listFeedItems.SelectedItems[i]).Key;
                if (item != null)
                {
                    string link = item.Link;
                    if (!string.IsNullOrEmpty(link))
                    {
                        data.AppendFormat("{0}{1}", (i > 0 ? Environment.NewLine : String.Empty), link);
                    }
                }
            }
            if (data.Length > 0)
                Clipboard.SetDataObject(data.ToString());
        }
 
        private  void CmdCopyNewsItemTitleLinkToClipboard(ICommand sender)
        {
            if (listFeedItems.SelectedItems.Count == 0)
                return;
            StringBuilder data = new StringBuilder();
            for (int i = 0; i < listFeedItems.SelectedItems.Count; i++)
            {
                NewsItem item = (NewsItem) ((ThreadedListViewItem) listFeedItems.SelectedItems[i]).Key;
                if (item != null)
                {
                    string link = item.Link;
                    if (!string.IsNullOrEmpty(link))
                    {
                        string title = item.Title;
                        if (!string.IsNullOrEmpty(title))
                        {
                            data.AppendFormat("{0}<a href=\"{1}\" title=\"{2}\">{3}</a>",
                                              (i > 0 ? "<br />" + Environment.NewLine : String.Empty), link, title,
                                              title);
                        }
                        else
                        {
                            data.AppendFormat("{0}<a href=\"{1}\">{2}</a>",
                                              (i > 0 ? "<br />" + Environment.NewLine : String.Empty), link, link);
                        }
                    }
                }
            }
            if (data.Length > 0)
                Clipboard.SetDataObject(data.ToString());
        }
 
        private  void CmdCopyNewsItemContentToClipboard(ICommand sender)
        {
            if (listFeedItems.SelectedItems.Count == 0)
                return;
            StringBuilder data = new StringBuilder();
            for (int i = 0; i < listFeedItems.SelectedItems.Count; i++)
            {
                NewsItem item = (NewsItem) ((ThreadedListViewItem) listFeedItems.SelectedItems[i]).Key;
                if (item != null)
                {
                    string link = item.Link;
                    string content = item.Content;
                    if (!string.IsNullOrEmpty(content))
                    {
                        data.AppendFormat("{0}{1}", (i > 0 ? "<br />" + Environment.NewLine : String.Empty), link);
                    }
                    else if (!string.IsNullOrEmpty(link))
                    {
                        string title = item.Title;
                        if (!string.IsNullOrEmpty(title))
                        {
                            data.AppendFormat("{0}<a href=\"{1}\" title=\"{2}\">{3}</a>",
                                              (i > 0 ? "<br />" + Environment.NewLine : String.Empty), link,
                                              item.Feed.title, title);
                        }
                        else
                        {
                            data.AppendFormat("{0}<a href=\"{1}\" title=\"{2}\">{3}</a>",
                                              (i > 0 ? "<br />" + Environment.NewLine : String.Empty), link,
                                              item.Feed.title, link);
                        }
                    }
                }
            }
            if (data.Length > 0)
                Clipboard.SetDataObject(data.ToString());
        }
 
        private  void CmdRefreshFinder(ICommand sender)
        {
            EmptyListView();
            htmlDetail.Clear();
            FinderNode afn = TreeSelectedFeedsNode as FinderNode;
            if (afn != null)
            {
                afn.Clear();
                UpdateTreeNodeUnreadStatus(afn, 0);
                if (afn.Finder != null && !string.IsNullOrEmpty(afn.Finder.ExternalSearchUrl))
                {
                    AsyncStartRssRemoteSearch(afn.Finder.ExternalSearchPhrase, afn.Finder.ExternalSearchUrl,
                                              afn.Finder.ExternalResultMerged, true);
                }
                else
                {
                    AsyncStartNewsSearch(afn);
                }
            }
        }
 
        private  void CmdMarkFinderItemsRead(ICommand sender)
        {
            SetFeedItemsReadState(listFeedItems.Items, true);
            UpdateTreeStatus(owner.FeedHandler.FeedsTable);
        }
 
        private  void CmdRenameFinder(ICommand sender)
        {
            if (CurrentSelectedFeedsNode != null)
                DoEditTreeNodeLabel();
        }
 
        private  void CmdNewFinder(ICommand sender)
        {
            CmdNewRssSearch(sender);
        }
 
        private  void CmdDeleteFinder(ICommand sender)
        {
            if (owner.MessageQuestion(SR.MessageBoxDeleteThisFinderQuestion) == DialogResult.Yes)
            {
                if (NodeEditingActive)
                    return;
                TreeFeedsNodeBase feedsNode = CurrentSelectedFeedsNode;
                WalkdownThenDeleteFinders(feedsNode);
                UpdateTreeNodeUnreadStatus(feedsNode, 0);
                try
                {
                    feedsNode.Parent.Nodes.Remove(feedsNode);
                }
                catch
                {
                }
                if (sender is AppContextMenuCommand)
                    CurrentSelectedFeedsNode = null;
            }
        }
 
        private  void CmdFinderToggleExcerptsFullItemText(ICommand sender)
        {
            TreeFeedsNodeBase feedsNode = CurrentSelectedFeedsNode;
            if (feedsNode != null && feedsNode is FinderNode)
            {
                FinderNode fn = (FinderNode) feedsNode;
                fn.Finder.ShowFullItemContent = ! fn.Finder.ShowFullItemContent;
                fn.Clear();
                UpdateTreeNodeUnreadStatus(fn, 0);
                EmptyListView();
                htmlDetail.Clear();
                AsyncStartNewsSearch(fn);
            }
        }
 
        private  void WalkdownThenDeleteFinders(TreeFeedsNodeBase startNode)
        {
            if (startNode == null) return;
            if (startNode.Type == FeedNodeType.Finder)
            {
                FinderNode agn = startNode as FinderNode;
                if (agn != null)
                {
                    owner.FinderList.Remove(agn.Finder);
                }
            }
            else
            {
                for (TreeFeedsNodeBase child = startNode.FirstNode; child != null; child = child.NextNode)
                {
                    WalkdownThenDeleteFinders(child);
                }
            }
        }
 
        private  void CmdDeleteAllFinder(ICommand sender)
        {
            if (owner.MessageQuestion(SR.MessageBoxDeleteAllFindersQuestion) == DialogResult.Yes)
            {
                owner.FinderList.Clear();
                owner.SaveSearchFolders();
                FinderRootNode finderRoot = GetRoot(RootFolderType.Finder) as FinderRootNode;
                if (finderRoot != null)
                {
                    finderRoot.Nodes.Clear();
                    UpdateTreeNodeUnreadStatus(finderRoot, 0);
                }
            }
            if (sender is AppContextMenuCommand)
                CurrentSelectedFeedsNode = null;
        }
 
        private  void CmdShowFinderProperties(ICommand sender)
        {
            CmdDockShowRssSearch(null);
            FinderNode node = CurrentSelectedFeedsNode as FinderNode;
            if (node != null)
            {
                searchPanel.SearchDialogSetSearchCriterias(node);
            }
            if (sender is AppContextMenuCommand)
                CurrentSelectedFeedsNode = null;
        }
 
        private  void CmdSubscribeToFinderResult(ICommand sender)
        {
            FinderNode node = CurrentSelectedFeedsNode as FinderNode;
            if (node != null && node.Finder != null)
            {
                if (!string.IsNullOrEmpty(node.Finder.ExternalSearchUrl))
                {
                    owner.CmdNewFeed(node.Text, node.Finder.ExternalSearchUrl, node.Finder.ExternalSearchPhrase);
                }
            }
        }
 
        internal  void CmdColumnChooserUseFeedLayoutGlobal(ICommand sender)
        {
            SetGlobalFeedColumnLayout(FeedNodeType.Feed, listFeedItems.FeedColumnLayout);
            listFeedItems.ApplyLayoutModifications();
        }
 
        internal  void CmdColumnChooserUseCategoryLayoutGlobal(ICommand sender)
        {
            SetGlobalFeedColumnLayout(FeedNodeType.Category, listFeedItems.FeedColumnLayout);
            listFeedItems.ApplyLayoutModifications();
        }
 
        internal  void CmdColumnChooserResetToDefault(ICommand sender)
        {
            SetFeedHandlerFeedColumnLayout(CurrentSelectedFeedsNode, null);
            listFeedItems.ApplyLayoutModifications();
            IList<NewsItem> items = NewsItemListFrom(listFeedItems.Items);
            listFeedItems.FeedColumnLayout = GetFeedColumnLayout(CurrentSelectedFeedsNode);
            RePopulateListviewWithContent(items);
        }
 
        private  void CmdDownloadAttachment(ICommand sender)
        {
            string fileName = sender.CommandID.Split(new char[] {'<'})[1];
            NewsItem item = CurrentSelectedFeedItem;
            try
            {
                if (item != null)
                {
                    owner.FeedHandler.DownloadEnclosure(item, fileName);
                }
            }
            catch (DownloaderException de)
            {
                MessageBox.Show(de.Message, SR.ExceptionEnclosureDownloadError, MessageBoxButtons.OK,
                                MessageBoxIcon.Error);
            }
        }
 
        internal  void CmdToggleListviewColumn(ICommand sender)
        {
            if (listFeedItems.Columns.Count > 1)
            {
                string[] name = sender.CommandID.Split(new char[] {'.'});
                bool enable = owner.Mediator.IsChecked(sender);
                owner.Mediator.SetChecked(enable, sender.CommandID);
                if (!enable)
                {
                    listFeedItems.Columns.Remove(name[1]);
                }
                else
                {
                    AddListviewColumn(name[1], 120);
                    RePopulateListviewWithCurrentContent();
                }
            }
        }
 
        private  void RefreshListviewColumnContextMenu()
        {
            ColumnKeyIndexMap map = listFeedItems.Columns.GetColumnIndexMap();
            foreach (string colID in Enum.GetNames(typeof (NewsItemSortField)))
            {
                owner.Mediator.SetChecked(map.ContainsKey(colID), "cmdListviewColumn." + colID);
            }
            bool enableIndividual = (CurrentSelectedFeedsNode != null &&
                                     (CurrentSelectedFeedsNode.Type == FeedNodeType.Feed ||
                                      CurrentSelectedFeedsNode.Type == FeedNodeType.Category));
            owner.Mediator.SetEnabled(enableIndividual, "cmdColumnChooserResetToDefault");
        }
 
        private  void AddListviewColumn(string colID, int width)
        {
            switch (colID)
            {
                case "Title":
                    listFeedItems.Columns.Add(colID, SR.ListviewColumnCaptionHeadline, typeof (string), width,
                                              HorizontalAlignment.Left);
                    break;
                case "Subject":
                    listFeedItems.Columns.Add(colID, SR.ListviewColumnCaptionTopic, typeof (string), width,
                                              HorizontalAlignment.Left);
                    break;
                case "Date":
                    listFeedItems.Columns.Add(colID, SR.ListviewColumnCaptionDate, typeof (DateTime), width,
                                              HorizontalAlignment.Left);
                    break;
                case "FeedTitle":
                    listFeedItems.Columns.Add(colID, SR.ListviewColumnCaptionFeedTitle, typeof (string), width,
                                              HorizontalAlignment.Left);
                    break;
                case "Author":
                    listFeedItems.Columns.Add(colID, SR.ListviewColumnCaptionCreator, typeof (string), width,
                                              HorizontalAlignment.Left);
                    break;
                case "CommentCount":
                    listFeedItems.Columns.Add(colID, SR.ListviewColumnCaptionCommentCount, typeof (int), width,
                                              HorizontalAlignment.Left);
                    break;
                case "Enclosure":
                    listFeedItems.Columns.Add(colID, SR.ListviewColumnCaptionEnclosure, typeof (int), width,
                                              HorizontalAlignment.Left);
                    break;
                case "Flag":
                    listFeedItems.Columns.Add(colID, SR.ListviewColumnCaptionFlagStatus, typeof (string), width,
                                              HorizontalAlignment.Left);
                    break;
                default:
                    Trace.Assert(false, "AddListviewColumn::NewsItemSortField NOT handled: " + colID);
                    break;
            }
        }
 
        private  void ResetFeedDetailLayoutCmds()
        {
            owner.Mediator.SetChecked(false, "cmdFeedDetailLayoutPosTop", "cmdFeedDetailLayoutPosLeft",
                                      "cmdFeedDetailLayoutPosRight", "cmdFeedDetailLayoutPosBottom");
        }
 
        private  void SetFeedDetailLayout(DockStyle style)
        {
            ResetFeedDetailLayoutCmds();
            panelFeedItems.Dock = style;
            if (style == DockStyle.Left || style == DockStyle.Right)
            {
                detailsPaneSplitter.Dock = style;
                detailsPaneSplitter.Cursor = Cursors.VSplit;
                panelFeedItems.Width = panelFeedDetails.Width/3;
            }
            else if (style == DockStyle.Bottom || style == DockStyle.Top)
            {
                detailsPaneSplitter.Dock = style;
                detailsPaneSplitter.Cursor = Cursors.HSplit;
                panelFeedItems.Height = panelFeedDetails.Height/2;
            }
            owner.Mediator.SetChecked(true, "cmdFeedDetailLayoutPos" + detailsPaneSplitter.Dock.ToString());
        }
 
        public  void CmdSelectAllNewsItems(ICommand sender)
        {
            for (int i = 0; i < listFeedItems.Items.Count; i++)
            {
                listFeedItems.Items[i].Selected = true;
            }
            listFeedItems.Select();
        }
 
        private  void OnHistoryNavigateGoBackItemClick(object sender, HistoryNavigationEventArgs e)
        {
            NavigateToHistoryEntry(_feedItemImpressionHistory.GetPreviousAt(e.Index));
        }
 
        private  void OnHistoryNavigateGoForwardItemClick(object sender, HistoryNavigationEventArgs e)
        {
            NavigateToHistoryEntry(_feedItemImpressionHistory.GetNextAt(e.Index));
        }
 
        public  void CmdDeleteFeed(ICommand sender)
        {
            if (NodeEditingActive)
                return;
            TreeFeedsNodeBase tn = CurrentSelectedFeedsNode;
            if (tn == null) return;
            if (tn.Type != FeedNodeType.Feed) return;
            if (DialogResult.Yes == owner.MessageQuestion(
                                        SR.MessageBoxDeleteThisFeedQuestion,
                                        String.Format(" - {0} ({1})", SR.MenuDeleteThisFeedCaption, tn.Text)))
            {
                owner.DeleteFeed(tn.DataKey);
                if (sender is AppContextMenuCommand)
                    CurrentSelectedFeedsNode = null;
            }
        }
 
        private  ITabState FeedDetailTabState
        {
            get
            {
                return (ITabState) _docFeedDetails.Tag;
            }
        }
 
        private  bool RemoveDocTab(DockControl doc)
        {
            if (doc == null)
                doc = _docContainer.ActiveDocument;
            if (doc == null)
                return false;
            ITabState state = doc.Tag as ITabState;
            if (state != null && state.CanClose)
            {
                try
                {
                    _docContainer.RemoveDocument(doc);
                    HtmlControl browser = doc.Controls[0] as HtmlControl;
                    if (browser != null)
                    {
                        browser.Tag = null;
                        browser.Dispose();
                    }
                }
                catch (Exception ex)
                {
                    _log.Error("_docContainer.RemoveDocument(doc) caused exception", ex);
                }
                return true;
            }
            return false;
        }
 
        public  void OnGenericListviewCommand(int index, bool hasConfig)
        {
            IBlogExtension ibe = blogExtensions[index];
            if (hasConfig)
            {
                try
                {
                    ibe.Configure(this);
                }
                catch (Exception e)
                {
                    _log.Error("IBlogExtension configuration exception", e);
                    owner.MessageError(SR.ExceptionIBlogExtensionFunctionCall("Configure()", e.Message));
                }
            }
            else
            {
                if (CurrentSelectedFeedItem != null)
                {
                    try
                    {
                        ibe.BlogItem(CurrentSelectedFeedItem, false);
                    }
                    catch (Exception e)
                    {
                        _log.Error("IBlogExtension command exception", e);
                        owner.MessageError(SR.ExceptionIBlogExtensionFunctionCall("BlogItem()", e.Message));
                    }
                }
            }
        }
 
        private  void OnFormHandleCreated(object sender, EventArgs e)
        {
            if (InitialStartupState == FormWindowState.Minimized)
                OnLoad(this, new EventArgs());
            Application.Idle += OnApplicationIdle;
        }
 
        private  void OnLoad(object sender, EventArgs eva)
        {
            Win32.ShowWindow(Handle, Win32.ShowWindowStyles.SW_HIDE);
            _uiTasksTimer.Tick += new EventHandler(OnTasksTimerTick);
            LoadUIConfiguration();
            SetTitleText(String.Empty);
            SetDetailHeaderText(null);
            InitSearchEngines();
            CheckForAddIns();
            InitFilter();
            SetGuiStateFeedback(SR.GUIStatusLoadingFeedlist);
            DelayTask(DelayedTasks.InitOnFinishLoading);
        }
 
        private  void OnFinishLoading()
        {
            if (owner.CommandLineArgs.StartInTaskbarNotificationAreaOnly || SystemTrayOnlyVisible)
            {
                Win32.ShowWindow(Handle, Win32.ShowWindowStyles.SW_HIDE);
            }
            else
            {
                Activate();
            }
            Splash.Status = SR.GUIStatusRefreshConnectionState;
            owner.UpdateInternetConnectionState();
            Utils.SetIEOffline(owner.InternetConnectionOffline);
            owner.CmdCheckForUpdates(AutoUpdateMode.OnApplicationStart);
            RssBanditApplication.CheckAndInitSoundEvents();
            owner.BeginLoadingFeedlist();
            owner.BeginLoadingSpecialFeeds();
            Splash.Close();
            owner.AskAndCheckForDefaultAggregator();
            _timerRefreshFeeds.Start();
            _timerRefreshCommentFeeds.Start();
        }
 
        private  void OnFormMove(object sender, EventArgs e)
        {
            if (WindowState == FormWindowState.Normal)
            {
                _formRestoreBounds.Location = Location;
            }
        }
 
        private  void OnFormResize(object sender, EventArgs e)
        {
            if (WindowState == FormWindowState.Normal)
            {
                _formRestoreBounds.Size = Size;
            }
            if (WindowState != FormWindowState.Minimized)
            {
                rightSandDock.MaximumSize = ClientSize.Width - 20;
                topSandDock.MaximumSize = bottomSandDock.MaximumSize = ClientSize.Height - 20;
            }
            if (Visible)
            {
                SystemTrayOnlyVisible = false;
            }
        }
 
        private  void OnFormMinimize(object sender, EventArgs e)
        {
            if (owner.Preferences.HideToTrayAction == HideToTray.OnMinimize)
            {
                HideToSystemTray();
            }
        }
 
        public virtual  bool PreFilterMessage(ref Message m)
        {
            bool processed = false;
            try
            {
                if (m.Msg == (int) Win32.Message.WM_KEYDOWN ||
                    m.Msg == (int) Win32.Message.WM_SYSKEYDOWN)
                {
                    Keys msgKey = ((Keys) (int) m.WParam & Keys.KeyCode);
                        if ((ModifierKeys == Keys.Alt) && msgKey == Keys.F4)
                        {
                            if (owner.Preferences.HideToTrayAction == HideToTray.OnClose &&
                                _forceShutdown == false)
                            {
                                processed = true;
                                HideToSystemTray();
                            }
                        }
                        else if (msgKey == Keys.Tab)
                        {
                            if (ModifierKeys == 0)
                            {
                                Trace.WriteLine("PreFilterMessage[Tab Only], ");
                                if (treeFeeds.Visible)
                                {
                                    if (treeFeeds.Focused)
                                    {
                                        if (listFeedItems.Visible)
                                        {
                                            listFeedItems.Focus();
                                            if (listFeedItems.Items.Count > 0 && listFeedItems.SelectedItems.Count == 0)
                                            {
                                                listFeedItems.Items[0].Selected = true;
                                                listFeedItems.Items[0].Focused = true;
                                                OnFeedListItemActivate(this, new EventArgs());
                                            }
                                            processed = true;
                                        }
                                        else if (_docContainer.ActiveDocument != _docFeedDetails)
                                        {
                                            SetFocus2WebBrowser((HtmlControl) _docContainer.ActiveDocument.Controls[0]);
                                            processed = true;
                                        }
                                    }
                                    else if (listFeedItems.Focused)
                                    {
                                        SetFocus2WebBrowser(htmlDetail);
                                        processed = true;
                                    }
                                    else
                                    {
                                    }
                                }
                                else
                                {
                                    if (listFeedItems.Visible)
                                    {
                                        if (listFeedItems.Focused)
                                        {
                                            SetFocus2WebBrowser(htmlDetail);
                                            processed = true;
                                        }
                                        else
                                        {
                                            Trace.WriteLine("PreFilterMessage[Tab Only] IE browser focused?" +
                                                            htmlDetail.Focused);
                                        }
                                    }
                                }
                            }
                            else if ((ModifierKeys & Keys.Shift) == Keys.Shift &&
                                     (ModifierKeys & Keys.Control) == 0)
                            {
                                Trace.WriteLine("PreFilterMessage[Shift-Tab Only]");
                                if (treeFeeds.Visible)
                                {
                                    if (treeFeeds.Focused)
                                    {
                                        if (listFeedItems.Visible)
                                        {
                                            SetFocus2WebBrowser(htmlDetail);
                                            processed = true;
                                        }
                                        else if (_docContainer.ActiveDocument != _docFeedDetails)
                                        {
                                            SetFocus2WebBrowser((HtmlControl) _docContainer.ActiveDocument.Controls[0]);
                                            processed = true;
                                        }
                                    }
                                    else if (listFeedItems.Focused)
                                    {
                                        treeFeeds.Focus();
                                        processed = true;
                                    }
                                    else
                                    {
                                        Trace.WriteLine("PreFilterMessage[Shift-Tab Only] IE browser focused?" +
                                                        htmlDetail.Focused);
                                    }
                                }
                                else
                                {
                                    if (listFeedItems.Visible)
                                    {
                                        if (listFeedItems.Focused)
                                        {
                                            SetFocus2WebBrowser(htmlDetail);
                                            processed = true;
                                        }
                                        else
                                        {
                                        }
                                    }
                                }
                            }
                        }
                        else if (listFeedItems.Focused &&
                                 _shortcutHandler.IsCommandInvoked("ExpandListViewItem", m.WParam))
                        {
                            if (listFeedItems.Visible && listFeedItems.SelectedItems.Count > 0)
                            {
                                ThreadedListViewItem lvi = (ThreadedListViewItem) listFeedItems.SelectedItems[0];
                                if (lvi.HasChilds && lvi.Collapsed)
                                {
                                    lvi.Expanded = true;
                                    processed = true;
                                }
                            }
                        }
                        else if (listFeedItems.Focused &&
                                 _shortcutHandler.IsCommandInvoked("CollapseListViewItem", m.WParam))
                        {
                            if (listFeedItems.Visible && listFeedItems.SelectedItems.Count > 0)
                            {
                                ThreadedListViewItem lvi = (ThreadedListViewItem) listFeedItems.SelectedItems[0];
                                if (lvi.HasChilds && lvi.Expanded)
                                {
                                    lvi.Collapsed = true;
                                    processed = true;
                                }
                            }
                        }
                        else if (_shortcutHandler.IsCommandInvoked("RemoveDocTab", m.WParam))
                        {
                            if (RemoveDocTab(_docContainer.ActiveDocument))
                            {
                                processed = true;
                            }
                        }
                        else if (_shortcutHandler.IsCommandInvoked("CatchUpCurrentSelectedNode", m.WParam))
                        {
                            owner.CmdCatchUpCurrentSelectedNode(null);
                            processed = true;
                        }
                        else if (_shortcutHandler.IsCommandInvoked("MarkFeedItemsUnread", m.WParam))
                        {
                            owner.CmdMarkFeedItemsUnread(null);
                            processed = true;
                        }
                        else if ((msgKey == Keys.Space && ModifierKeys == 0) ||
                                 _shortcutHandler.IsCommandInvoked("MoveToNextUnread", m.WParam))
                        {
                            if (listFeedItems.Focused || treeFeeds.Focused &&
                                                         !(TreeSelectedFeedsNode != null &&
                                                           TreeSelectedFeedsNode.IsEditing))
                            {
                                MoveToNextUnreadItem();
                                processed = true;
                            }
                            else if (searchPanel.ContainsFocus || treeFeeds.Focused ||
                                     UrlComboBox.Focused || SearchComboBox.Focused ||
                                     (CurrentSelectedFeedsNode != null && CurrentSelectedFeedsNode.IsEditing))
                            {
                            }
                            else if (_docContainer.ActiveDocument == _docFeedDetails &&
                                     !listFeedItems.Focused)
                            {
                                IHTMLDocument2 htdoc = htmlDetail.Document2;
                                if (htdoc != null)
                                {
                                    IHTMLElement2 htbody = htdoc.GetBody();
                                    if (htbody != null)
                                    {
                                        int num1 = htbody.getScrollTop();
                                        htbody.setScrollTop(num1 + 20);
                                        int num2 = htbody.getScrollTop();
                                        if (num1 == num2)
                                        {
                                            MoveToNextUnreadItem();
                                            processed = true;
                                        }
                                    }
                                }
                            }
                            else
                            {
                            }
                        }
                        else if (_shortcutHandler.IsCommandInvoked("InitiateRenameFeedOrCategory", m.WParam))
                        {
                            if (treeFeeds.Focused)
                            {
                                InitiateRenameFeedOrCategory();
                                processed = true;
                            }
                        }
                        else if (_shortcutHandler.IsCommandInvoked("UpdateFeed", m.WParam))
                        {
                            CurrentSelectedFeedsNode = null;
                            owner.CmdUpdateFeed(null);
                            processed = true;
                        }
                        else if (_shortcutHandler.IsCommandInvoked("GiveFocusToUrlTextBox", m.WParam))
                        {
                            UrlComboBox.Focus();
                            processed = true;
                        }
                        else if (_shortcutHandler.IsCommandInvoked("GiveFocusToSearchTextBox", m.WParam))
                        {
                            SearchComboBox.Focus();
                            processed = true;
                        }
                        else if ((msgKey == Keys.Delete && ModifierKeys == 0) ||
                                 _shortcutHandler.IsCommandInvoked("DeleteItem", m.WParam))
                        {
                            if (treeFeeds.Focused && TreeSelectedFeedsNode != null &&
                                !TreeSelectedFeedsNode.IsEditing)
                            {
                                TreeFeedsNodeBase root = GetRoot(RootFolderType.MyFeeds);
                                TreeFeedsNodeBase current = CurrentSelectedFeedsNode;
                                if (NodeIsChildOf(current, root))
                                {
                                    if (current.Type == FeedNodeType.Category)
                                    {
                                        owner.CmdDeleteCategory(null);
                                        processed = true;
                                    }
                                    if (current.Type == FeedNodeType.Feed)
                                    {
                                        CmdDeleteFeed(null);
                                        processed = true;
                                    }
                                }
                            }
                        }
                }
                else if (m.Msg == (int) Win32.Message.WM_LBUTTONDBLCLK ||
                         m.Msg == (int) Win32.Message.WM_RBUTTONDBLCLK ||
                         m.Msg == (int) Win32.Message.WM_MBUTTONDBLCLK ||
                         m.Msg == (int) Win32.Message.WM_LBUTTONUP ||
                         m.Msg == (int) Win32.Message.WM_MBUTTONUP ||
                         m.Msg == (int) Win32.Message.WM_RBUTTONUP ||
                         m.Msg == (int) Win32.Message.WM_XBUTTONDBLCLK ||
                         m.Msg == (int) Win32.Message.WM_XBUTTONUP)
                {
                    _lastMousePosition = new Point(Win32.LOWORD(m.LParam), Win32.HIWORD(m.LParam));
                    Control mouseControl = wheelSupport.GetTopmostChild(this, MousePosition);
                    _webUserNavigated = (mouseControl is HtmlControl);
                    _webForceNewTab = false;
                    if (_webUserNavigated)
                    {
                        _webForceNewTab = (Interop.GetAsyncKeyState(Interop.VK_CONTROL) < 0);
                    }
                }
                else if (m.Msg == (int) Win32.Message.WM_MOUSEMOVE)
                {
                    Point p = new Point(Win32.LOWORD(m.LParam), Win32.HIWORD(m.LParam));
                    if (Math.Abs(p.X - _lastMousePosition.X) > 5 ||
                        Math.Abs(p.Y - _lastMousePosition.Y) > 5)
                    {
                        _webForceNewTab = _webUserNavigated = false;
                        _lastMousePosition = p;
                    }
                }
            }
            catch (Exception ex)
            {
                _log.Error("PreFilterMessage() failed", ex);
            }
            return processed;
        }
 
        [SecurityPermission(SecurityAction.LinkDemand)] 
        protected override  void WndProc(ref Message m)
        {
            try
            {
                if (m.Msg == (int) Win32.Message.WM_SIZE)
                {
                    if (((int) m.WParam) == 1 && OnMinimize != null)
                    {
                        OnMinimize(this, EventArgs.Empty);
                    }
                }
                else if ( m.Msg == (int) Win32.Message.WM_QUERYENDSESSION ||
                                                          m.Msg == (int) Win32.Message.WM_ENDSESSION)
                {
                    _forceShutdown = true;
                    owner.SaveApplicationState(true);
                }
                else if (m.Msg == (int) Win32.Message.WM_CLOSE &&
                         owner.Preferences.HideToTrayAction != HideToTray.OnClose)
                {
                    _forceShutdown = true;
                    owner.SaveApplicationState(true);
                }
                base.WndProc(ref m);
            }
            catch (Exception ex)
            {
                _log.Fatal("WndProc() failed with an exception", ex);
            }
        }
 
        private  void OnFormMouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.XButton1)
                RequestBrowseAction(BrowseAction.NavigateBack);
            else if (e.Button == MouseButtons.XButton2)
                RequestBrowseAction(BrowseAction.NavigateForward);
        }
 
        private  void OnFormActivated(object sender, EventArgs e)
        {
            Application.AddMessageFilter(this);
            KeyPreview = true;
        }
 
        private  void OnFormDeactivate(object sender, EventArgs e)
        {
            Application.RemoveMessageFilter(this);
            KeyPreview = false;
        }
 
        private  void OnDocContainerShowControlContextMenu(object sender, ShowControlContextMenuEventArgs e)
        {
            _contextMenuCalledAt = Cursor.Position;
            _docTabContextMenu.Show(_docContainer, e.Position);
        }
 
        private  void OnDocContainerMouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                if (_docContainer.Visible)
                {
                    _contextMenuCalledAt = Cursor.Position;
                    _docTabContextMenu.Show(_docContainer, new Point(e.X, e.Y));
                }
            }
            else if (e.Button == MouseButtons.Middle)
            {
                OnDocContainerDoubleClick(sender, e);
            }
        }
 
        private  void OnDocContainerDocumentClosing(object sender, DocumentClosingEventArgs e)
        {
            RemoveDocTab(e.DockControl);
        }
 
        private  void OnDocContainerActiveDocumentChanged(object sender, ActiveDocumentEventArgs e)
        {
            RefreshDocumentState(e.NewActiveDocument);
            DeactivateWebProgressInfo();
        }
 
        private  void OnDocContainerDoubleClick(object sender, EventArgs e)
        {
            Point p = _docContainer.PointToClient(MousePosition);
            DocumentLayoutSystem lb = (DocumentLayoutSystem) _docContainer.GetLayoutSystemAt(p);
            if (lb != null)
            {
                DockControl doc = lb.GetControlAt(p);
                if (doc != null)
                    RemoveDocTab(doc);
            }
        }
 
        protected  void OnSaveConfig(Settings writer)
        {
            try
            {
                writer.SetProperty("version", 4);
                writer.SetProperty(Name + "/Bounds", BoundsToString(_formRestoreBounds));
                writer.SetProperty(Name + "/WindowState", (int) WindowState);
                writer.SetProperty(Name + "/panelFeedItems.Height", panelFeedItems.Height);
                writer.SetProperty(Name + "/panelFeedItems.Width", panelFeedItems.Width);
                writer.SetProperty(Name + "/Navigator.Width", Navigator.Width);
                writer.SetProperty(Name + "/docManager.WindowAlignment", (int) _docContainer.LayoutSystem.SplitMode);
                Office2003Renderer sdRenderer = sandDockManager.Renderer as Office2003Renderer;
                writer.SetProperty(Name + "/dockManager.LayoutStyle.Office2003", (sdRenderer != null));
                using (CultureChanger.InvariantCulture)
                {
                    writer.SetProperty(Name + "/dockManager.LayoutInfo", sandDockManager.GetLayout());
                }
                writer.SetProperty(Name + "/ToolbarsVersion", CurrentToolbarsVersion);
                writer.SetProperty(Name + "/Toolbars",
                                   StateSerializationHelper.SaveControlStateToString(ultraToolbarsManager, true));
                writer.SetProperty(Name + "/ExplorerBarVersion", CurrentExplorerBarVersion);
                StateSerializationHelper.SaveExplorerBar(Navigator, writer, Name + "/" + Navigator.Name);
                writer.SetProperty(Name + "/" + Navigator.Name + "/Visible",
                                   owner.Mediator.IsChecked("cmdToggleTreeViewState") ||
                                   owner.Mediator.IsChecked("cmdToggleRssSearchTabState")
                    );
                writer.SetProperty(Name + "/feedDetail.LayoutInfo.Position", (int) detailsPaneSplitter.Dock);
                writer.SetProperty(Name + "/outlookView.Visible",
                                   owner.Mediator.IsChecked("cmdViewOutlookReadingPane")
                    );
            }
            catch (Exception ex)
            {
                _log.Error("Exception while writing config entries to .settings.xml", ex);
            }
        }
 
        protected  void OnLoadConfig(Settings reader)
        {
            if (owner.CommandLineArgs.ResetUserInterface)
                return;
            try
            {
                Rectangle r = StringToBounds(reader.GetString(Name + "/Bounds", BoundsToString(Bounds)));
                if (r != Rectangle.Empty)
                {
                    if (Screen.AllScreens.Length < 2)
                    {
                        if (r.X < 0) r.X = 0;
                        if (r.Y < 0) r.Y = 0;
                        if (r.X >= Screen.PrimaryScreen.WorkingArea.Width)
                            r.X -= Screen.PrimaryScreen.WorkingArea.Width;
                        if (r.Y >= Screen.PrimaryScreen.WorkingArea.Height)
                            r.Y -= Screen.PrimaryScreen.WorkingArea.Height;
                    }
                    _formRestoreBounds = r;
                    SetBounds(r.X, r.Y, r.Width, r.Height, BoundsSpecified.All);
                }
                FormWindowState windowState = (FormWindowState) reader.GetInt32(Name + "/WindowState",
                                                                                (int) WindowState);
                if (InitialStartupState != FormWindowState.Normal &&
                    WindowState != InitialStartupState)
                {
                    WindowState = InitialStartupState;
                }
                else
                {
                    WindowState = windowState;
                }
                DockStyle feedDetailLayout =
                    (DockStyle) reader.GetInt32(Name + "/feedDetail.LayoutInfo.Position", (int) DockStyle.Top);
                if (feedDetailLayout != DockStyle.Top && feedDetailLayout != DockStyle.Left &&
                    feedDetailLayout != DockStyle.Right && feedDetailLayout != DockStyle.Bottom)
                    feedDetailLayout = DockStyle.Top;
                SetFeedDetailLayout(feedDetailLayout);
                panelFeedItems.Height = reader.GetInt32(Name + "/panelFeedItems.Height", (panelFeedDetails.Height/2));
                panelFeedItems.Width = reader.GetInt32(Name + "/panelFeedItems.Width", (panelFeedDetails.Width/2));
                Navigator.Width = reader.GetInt32(Name + "/Navigator.Width", Navigator.Width);
                _docContainer.LayoutSystem.SplitMode =
                    (Orientation)
                    reader.GetInt32(Name + "/docManager.WindowAlignment", (int) _docContainer.LayoutSystem.SplitMode);
                owner.Mediator.SetChecked((_docContainer.LayoutSystem.SplitMode == Orientation.Horizontal),
                                          "cmdDocTabLayoutHorizontal");
                using (CultureChanger.InvariantCulture)
                {
                    string fallbackSDM = sandDockManager.GetLayout();
                    try
                    {
                        sandDockManager.SetLayout(reader.GetString(Name + "/dockManager.LayoutInfo", fallbackSDM));
                    }
                    catch (Exception ex)
                    {
                        _log.Error("Exception on restore sandDockManager layout", ex);
                        sandDockManager.SetLayout(fallbackSDM);
                    }
                }
                bool office2003 = reader.GetBoolean(Name + "/dockManager.LayoutStyle.Office2003", true);
                if (office2003)
                {
                    Office2003Renderer sdRenderer = new Office2003Renderer();
                    sdRenderer.ColorScheme = Office2003Renderer.Office2003ColorScheme.Automatic;
                    if (!RssBanditApplication.AutomaticColorSchemes)
                    {
                        sdRenderer.ColorScheme = Office2003Renderer.Office2003ColorScheme.Standard;
                    }
                    sandDockManager.Renderer = sdRenderer;
                    _docContainer.Renderer = sdRenderer;
                }
                else
                {
                    WhidbeyRenderer sdRenderer = new WhidbeyRenderer();
                    sandDockManager.Renderer = sdRenderer;
                    _docContainer.Renderer = sdRenderer;
                }
                if (reader.GetString(Name + "/ExplorerBarVersion", "0") == CurrentExplorerBarVersion)
                {
                    StateSerializationHelper.LoadExplorerBar(Navigator, reader, Name + "/" + Navigator.Name);
                }
                if (! reader.GetBoolean(Name + "/" + Navigator.Name + "/Visible", true))
                {
                    OnNavigatorCollapseClick(this, EventArgs.Empty);
                }
                if (Navigator.Visible)
                    ToggleNavigationPaneView(NavigationPaneView.Subscriptions);
                if (reader.GetString(Name + "/ToolbarsVersion", "0") == CurrentToolbarsVersion)
                {
                    StateSerializationHelper.LoadControlStateFromString(
                        ultraToolbarsManager, reader.GetString(Name + "/Toolbars", null),
                        owner.Mediator);
                    ultraToolbarsManager.Tools["cmdUrlDropdownContainer"].Control = UrlComboBox;
                    ultraToolbarsManager.Tools["cmdSearchDropdownContainer"].Control = SearchComboBox;
                    historyMenuManager.SetControls(
                        (AppPopupMenuCommand) ultraToolbarsManager.Tools["cmdBrowserGoBack"],
                        (AppPopupMenuCommand) ultraToolbarsManager.Tools["cmdBrowserGoForward"]);
                    owner.BackgroundDiscoverFeedsHandler.SetControls(
                        (AppPopupMenuCommand) ultraToolbarsManager.Tools["cmdDiscoveredFeedsDropDown"],
                        (AppButtonToolCommand) ultraToolbarsManager.Tools["cmdDiscoveredFeedsListClear"]);
                    InitSearchEngines();
                }
                bool outlookView = reader.GetBoolean(Name + "/outlookView.Visible", false);
                owner.Mediator.SetChecked(outlookView, "cmdViewOutlookReadingPane");
                ShowOutlookReadingPane(outlookView);
                owner.Mediator.SetEnabled(
                    SearchIndexBehavior.NoIndexing != owner.FeedHandler.Configuration.SearchIndexBehavior,
                    "cmdNewRssSearch",
                    "cmdToggleRssSearchTabState");
            }
            catch (Exception ex)
            {
                _log.Error("Exception while loading .settings.xml", ex);
            }
        }
 
        private  void OnFormClosing(object sender, CancelEventArgs e)
        {
            if (owner.Preferences.HideToTrayAction == HideToTray.OnClose &&
                _forceShutdown == false)
            {
                e.Cancel = true;
                HideToSystemTray();
            }
            else
            {
                _trayAni.Visible = false;
                toastNotifier.Dispose();
                SaveUIConfiguration(true);
            }
        }
 
        private  bool SystemTrayOnlyVisible
        {
            get
            {
                return owner.GuiSettings.GetBoolean(Name + "/TrayOnly.Visible", false);
            }
            set
            {
                owner.GuiSettings.SetProperty(Name + "/TrayOnly.Visible", value);
            }
        }
 
        private  void HideToSystemTray()
        {
            Win32.ShowWindow(Handle, Win32.ShowWindowStyles.SW_HIDE);
            if (WindowState != FormWindowState.Minimized)
                WindowState = FormWindowState.Minimized;
            SystemTrayOnlyVisible = true;
        }
 
        private  void RestoreFromSystemTray()
        {
            Show();
            Win32.ShowWindow(Handle, Win32.ShowWindowStyles.SW_RESTORE);
            Win32.SetForegroundWindow(Handle);
            SystemTrayOnlyVisible = false;
            if (!_browserTabsRestored)
            {
                LoadAndRestoreBrowserTabState();
            }
        }
 
        internal  void DoShow()
        {
            if (Visible)
            {
                if (WindowState == FormWindowState.Minimized)
                {
                    Win32.ShowWindow(Handle, Win32.ShowWindowStyles.SW_RESTORE);
                    Win32.SetForegroundWindow(Handle);
                }
                else
                {
                    Activate();
                }
            }
            else
            {
                RestoreFromSystemTray();
            }
        }
 
        private  void LoadUIConfiguration()
        {
            try
            {
                OnLoadConfig(owner.GuiSettings);
            }
            catch (Exception ex)
            {
                _log.Error("Load .settings.xml failed", ex);
            }
        }
 
        public  void InitSearchEngines()
        {
            if (!owner.SearchEngineHandler.EnginesLoaded || !owner.SearchEngineHandler.EnginesOK)
                owner.LoadSearchEngines();
            toolbarHelper.BuildSearchMenuDropdown(owner.SearchEngineHandler.Engines,
                                                  owner.Mediator, CmdExecuteSearchEngine);
            owner.Mediator.SetEnabled(owner.SearchEngineHandler.Engines.Count > 0, "cmdSearchGo");
        }
 
           
           
        private  void OnTreeFeedMouseDown(object sender, MouseEventArgs e)
        {
            try
            {
                UltraTree tv = (UltraTree) sender;
                TreeFeedsNodeBase selectedNode = (TreeFeedsNodeBase) tv.GetNodeFromPoint(e.X, e.Y);
                if (e.Button == MouseButtons.Right)
                {
                    if (selectedNode != null)
                    {
                        selectedNode.UpdateContextMenu();
                        RefreshTreeFeedContextMenus(selectedNode);
                    }
                    else
                    {
                        tv.ContextMenu = null;
                    }
                    CurrentSelectedFeedsNode = selectedNode;
                }
                else
                {
                    if ((CurrentSelectedFeedsNode != null) && (selectedNode != null))
                    {
                        if (ReferenceEquals(CurrentSelectedFeedsNode, selectedNode))
                        {
                            if (selectedNode.Type == FeedNodeType.Feed)
                            {
                                IFeedDetails fi = owner.GetFeedInfo(selectedNode.DataKey);
                                if (fi != null && fi.Link == FeedDetailTabState.Url)
                                    return;
                            }
                            else
                            {
                                if (string.IsNullOrEmpty(FeedDetailTabState.Url))
                                    return;
                            }
                            OnTreeFeedAfterSelectManually(selectedNode);
                        }
                        else
                        {
                            CurrentSelectedFeedsNode = null;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                _log.Error("Unexpected exception in OnTreeFeedMouseDown()", ex);
            }
        }
 
        private  void OnTreeFeedMouseMove(object sender, MouseEventArgs e)
        {
            try
            {
                TreeFeedsNodeBase t = (TreeFeedsNodeBase) treeFeeds.GetNodeFromPoint(e.X, e.Y);
                if (CurrentDragNode != null)
                {
                    if (t == null)
                        CurrentDragHighlightNode = null;
                    if (t != null)
                    {
                        if (t.Type == FeedNodeType.Feed)
                            CurrentDragHighlightNode = t.Parent;
                        else
                            CurrentDragHighlightNode = t;
                    }
                }
            }
            catch (Exception ex)
            {
                _log.Error("Unexpected exception in OnTreeFeedMouseMove()", ex);
            }
        }
 
        private  void OnTreeFeedMouseUp(object sender, MouseEventArgs e)
        {
            try
            {
                CurrentDragHighlightNode = CurrentDragNode = null;
                if (e.Button == MouseButtons.Left)
                {
                    UltraTree tv = (UltraTree) sender;
                    TreeFeedsNodeBase selectedNode = (TreeFeedsNodeBase) tv.GetNodeFromPoint(e.X, e.Y);
                    if (selectedNode != null && TreeSelectedFeedsNode == selectedNode)
                    {
                        SetTitleText(selectedNode.Text);
                        SetDetailHeaderText(selectedNode);
                        MoveFeedDetailsToFront();
                    }
                }
            }
            catch (Exception ex)
            {
                _log.Error("Unexpected exception in OnTreeFeedMouseUp()", ex);
            }
        }
 
        private  void EmptyListView()
        {
            if (listFeedItems.Items.Count > 0)
            {
                listFeedItems.BeginUpdate();
                listFeedItems.ListViewItemSorter = null;
                listFeedItems.Items.Clear();
                listFeedItems.EndUpdate();
                listFeedItemsO.Clear();
            }
            owner.Mediator.SetEnabled("-cmdFeedItemPostReply");
        }
 
        private  void OnTreeFeedDoubleClick(object sender, EventArgs e)
        {
            try
            {
                Point point = treeFeeds.PointToClient(MousePosition);
                TreeFeedsNodeBase feedsNode = (TreeFeedsNodeBase) treeFeeds.GetNodeFromPoint(point);
                if (feedsNode != null)
                {
                    CurrentSelectedFeedsNode = feedsNode;
                    owner.CmdNavigateFeedHome((ICommand) null);
                    CurrentSelectedFeedsNode = null;
                }
            }
            catch (Exception ex)
            {
                _log.Error("Unexpected Error in OnTreeFeedDoubleClick()", ex);
            }
        }
 
        private  void OnTreeFeedBeforeSelect(object sender, BeforeSelectEventArgs e)
        {
            if (treeFeeds.SelectedNodes.Count == 0 || e.NewSelections.Count == 0)
            {
                e.Cancel = true;
                return;
            }
            if (ReferenceEquals(treeFeeds.SelectedNodes[0], e.NewSelections[0]))
            {
                return;
            }
            if (TreeSelectedFeedsNode != null)
            {
                listFeedItems.CheckForLayoutModifications();
                TreeFeedsNodeBase tn = TreeSelectedFeedsNode;
                if (tn.Type == FeedNodeType.Category)
                {
                    string category = tn.CategoryStoreName;
                    if (owner.FeedHandler.GetCategoryMarkItemsReadOnExit(category) &&
                        !TreeHelper.IsChildNode(tn, (TreeFeedsNodeBase) e.NewSelections[0]))
                    {
                        MarkSelectedNodeRead(tn);
                        owner.SubscriptionModified(NewsFeedProperty.FeedItemReadState);
                    }
                }
                else if (tn.Type == FeedNodeType.Feed)
                {
                    string feedUrl = tn.DataKey;
                    INewsFeed f = owner.GetFeed(feedUrl);
                    if (f != null && feedUrl != null && owner.FeedHandler.GetMarkItemsReadOnExit(feedUrl) &&
                        f.containsNewMessages)
                    {
                        MarkSelectedNodeRead(tn);
                        owner.SubscriptionModified(NewsFeedProperty.FeedItemReadState);
                    }
                }
            }
        }
 
        private  void OnTreeFeedAfterSelect(object sender, SelectEventArgs e)
        {
            if (e.NewSelections.Count == 0)
            {
                return;
            }
            TreeFeedsNodeBase tn = (TreeFeedsNodeBase) e.NewSelections[0];
            OnTreeFeedAfterSelectManually(tn);
        }
 
        private  void OnTreeFeedAfterSelectManually(UltraTreeNode node)
        {
            try
            {
                TreeFeedsNodeBase tn = (TreeFeedsNodeBase) node;
                if (tn.Type != FeedNodeType.Root)
                {
                    SetTitleText(tn.Text);
                    SetDetailHeaderText(tn);
                    MoveFeedDetailsToFront();
                }
                if (tn.Selected)
                {
                    if (tn.Type != FeedNodeType.Feed)
                    {
                        owner.Mediator.SetEnabled("-cmdFeedItemNewPost");
                    }
                    else
                    {
                        string feedUrl = tn.DataKey;
                        if (feedUrl != null && owner.FeedHandler.FeedsTable.ContainsKey(feedUrl))
                        {
                            owner.Mediator.SetEnabled(RssHelper.IsNntpUrl(feedUrl), "cmdFeedItemNewPost");
                        }
                        else
                        {
                            owner.Mediator.SetEnabled("-cmdFeedItemNewPost");
                        }
                    }
                    listFeedItems.FeedColumnLayout = GetFeedColumnLayout(tn);
                    switch (tn.Type)
                    {
                        case FeedNodeType.Feed:
                            MoveFeedDetailsToFront();
                            RefreshFeedDisplay(tn, true);
                            AddHistoryEntry(tn, null);
                            break;
                        case FeedNodeType.Category:
                            RefreshCategoryDisplay(tn);
                            AddHistoryEntry(tn, null);
                            break;
                        case FeedNodeType.SmartFolder:
                            try
                            {
                                FeedDetailTabState.Url = String.Empty;
                                AddHistoryEntry(tn, null);
                                ISmartFolder isf = tn as ISmartFolder;
                                if (isf != null)
                                    PopulateSmartFolder(tn, true);
                                if (tn is UnreadItemsNode && UnreadItemsNode.Items.Count > 0)
                                {
                                    FeedInfoList fiList = CreateFeedInfoList(tn.Text, UnreadItemsNode.Items);
                                    BeginTransformFeedList(fiList, tn, owner.FeedHandler.Stylesheet);
                                }
                            }
                            catch (Exception ex)
                            {
                                _log.Error("Unexpected Error on PopulateSmartFolder()", ex);
                                owner.MessageError(SR.ExceptionGeneral(ex.Message));
                            }
                            break;
                        case FeedNodeType.Finder:
                            try
                            {
                                FeedDetailTabState.Url = String.Empty;
                                AddHistoryEntry(tn, null);
                                PopulateFinderNode((FinderNode) tn, true);
                            }
                            catch (Exception ex)
                            {
                                _log.Error("Unexpected Error on PopulateAggregatedFolder()", ex);
                                owner.MessageError(SR.ExceptionGeneral(ex.Message));
                            }
                            break;
                        case FeedNodeType.FinderCategory:
                            FeedDetailTabState.Url = String.Empty;
                            AddHistoryEntry(tn, null);
                            break;
                        case FeedNodeType.Root:
                            FeedDetailTabState.Url = String.Empty;
                            AddHistoryEntry(tn, null);
                            SetGuiStateFeedback(SR.StatisticsAllFeedsCountMessage(owner.FeedHandler.FeedsTable.Count));
                            break;
                        default:
                            break;
                    }
                }
                else
                {
                    owner.Mediator.SetEnabled("-cmdFeedItemNewPost");
                }
            }
            catch (Exception ex)
            {
                _log.Error("Unexpected Error in OnTreeFeedAfterSelect()", ex);
                owner.MessageError(SR.ExceptionGeneral(ex.Message));
            }
        }
 
        private  FeedInfoList CreateFeedInfoList(string title, IList<NewsItem> items)
        {
            FeedInfoList result = new FeedInfoList(title);
            if (items == null || items.Count == 0)
                return result;
            Hashtable fiCache = new Hashtable();
            for (int i = 0; i < items.Count; i++)
            {
                NewsItem item = items[i];
                if (item == null || item.Feed == null)
                    continue;
                string feedUrl = item.Feed.link;
                if (feedUrl == null || !owner.FeedHandler.FeedsTable.ContainsKey(feedUrl))
                    continue;
                try
                {
                    FeedInfo fi;
                    if (fiCache.ContainsKey(feedUrl))
                        fi = fiCache[feedUrl] as FeedInfo;
                    else
                    {
                        fi = (FeedInfo) owner.GetFeedInfo(feedUrl);
                        if (fi != null)
                        {
                            fi = fi.Clone(false);
                        }
                    }
                    if (fi == null)
                        continue;
                    if (!item.BeenRead)
                        fi.ItemsList.Add(item);
                    if (fi.ItemsList.Count > 0 && !fiCache.ContainsKey(feedUrl))
                    {
                        fiCache.Add(feedUrl, fi);
                        result.Add(fi);
                    }
                }
                catch (Exception e)
                {
                    owner.PublishXmlFeedError(e, feedUrl, true);
                }
            }
            return result;
        }
 
        internal  void RenameTreeNode(TreeFeedsNodeBase tn, string newName)
        {
            tn.TextBeforeEditing = tn.Text;
            tn.Text = newName;
            OnTreeFeedAfterLabelEdit(this, new NodeEventArgs(tn));
        }
 
        private static  void OnTreeFeedBeforeLabelEdit(object sender, CancelableNodeEventArgs e)
        {
            TreeFeedsNodeBase editedNode = (TreeFeedsNodeBase) e.TreeNode;
            e.Cancel = !editedNode.Editable;
            if (!e.Cancel)
            {
                editedNode.TextBeforeEditing = editedNode.Text;
            }
        }
 
        private  void OnTreeFeedsValidateLabelEdit(object sender, ValidateLabelEditEventArgs e)
        {
            string newText = e.LabelEditText;
            if (string.IsNullOrEmpty(newText))
            {
                e.StayInEditMode = true;
                return;
            }
            TreeFeedsNodeBase editedNode = (TreeFeedsNodeBase) e.Node;
            string newLabel = newText.Trim();
            if (editedNode.Type != FeedNodeType.Feed &&
                editedNode.Type != FeedNodeType.Finder)
            {
                TreeFeedsNodeBase existingNode =
                    TreeHelper.FindChildNode(editedNode.Parent, newLabel, FeedNodeType.Category);
                if (existingNode != null && existingNode != editedNode)
                {
                    owner.MessageError(SR.ExceptionDuplicateCategoryName(newLabel));
                    e.StayInEditMode = true;
                    return;
                }
            }
        }
 
        private  void OnTreeFeedAfterLabelEdit(object sender, NodeEventArgs e)
        {
            TreeFeedsNodeBase editedNode = (TreeFeedsNodeBase) e.TreeNode;
            string newLabel = e.TreeNode.Text.Trim();
            string oldLabel = editedNode.TextBeforeEditing;
            editedNode.TextBeforeEditing = null;
            if (editedNode.Type == FeedNodeType.Feed)
            {
                INewsFeed f = owner.GetFeed(editedNode.DataKey);
                if (f != null)
                {
                    f.title = newLabel;
                    owner.FeedWasModified(f, NewsFeedProperty.FeedTitle);
                }
            }
            else if (editedNode.Type == FeedNodeType.Finder)
            {
            }
            else
            {
                string oldFullname = oldLabel;
                string[] catArray = TreeFeedsNodeBase.BuildCategoryStoreNameArray(editedNode);
                if (catArray.Length > 0)
                {
                    catArray[catArray.Length - 1] = oldLabel;
                    oldFullname = String.Join(NewsHandler.CategorySeparator, catArray);
                }
                if (GetRoot(editedNode) == RootFolderType.MyFeeds)
                {
                    string newFullname = editedNode.CategoryStoreName;
<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442355588/fstmerge_var1_5265414185251007596
                    IDictionary<string, INewsFeedCategory> categories = owner.FeedHandler.GetCategories();
=======
                    CategoriesCollection categories = owner.FeedHandler.Categories;
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442355588/fstmerge_var2_1191971859122413806
                    string[] catList = new string[categories.Count];
                    categories.Keys.CopyTo(catList, 0);
                    foreach (string catKey in catList)
                    {
                        if (catKey.Equals(oldFullname) || catKey.StartsWith(oldFullname + NewsHandler.CategorySeparator))
                        {
                            int i = categories.IndexOfKey(catKey);
                            CategoryEntry c = categories[i];
                            categories.RemoveAt(i);
                            c.Key = catKey.Replace(oldFullname, newFullname);
                            c.Value.Value = c.Key;
                            categories.Insert(i, c);
                        }
                    }
                    WalkdownThenRenameFeedCategory(editedNode, newFullname);
                    owner.SubscriptionModified(NewsFeedProperty.FeedCategory);
                }
            }
        }
 
        private  void OnTreeFeedSelectionDragStart(object sender, EventArgs e)
        {
            TreeFeedsNodeBase tn = TreeSelectedFeedsNode;
            if (tn != null && (tn.Type == FeedNodeType.Feed || tn.Type == FeedNodeType.Category))
            {
                CurrentDragNode = tn;
                if (CurrentDragNode.Expanded)
                    CurrentDragNode.Expanded = false;
                string dragObject = null;
                if ((ModifierKeys & Keys.Control) == Keys.Control)
                {
                    IFeedDetails fd = null;
                    if (CurrentDragNode.Type == FeedNodeType.Feed)
                        fd = owner.GetFeedInfo(CurrentDragNode.DataKey);
                    if (fd != null)
                    {
                        dragObject = fd.Link;
                    }
                }
                if (dragObject != null)
                {
                    DoDragDrop(dragObject, DragDropEffects.Copy | DragDropEffects.Link);
                }
                else
                {
                    if (CurrentDragNode.Type == FeedNodeType.Feed)
                    {
                        dragObject = CurrentDragNode.DataKey;
                    }
                    else
                    {
                        dragObject = CurrentDragNode.Text;
                    }
                    DoDragDrop(dragObject, DragDropEffects.Copy | DragDropEffects.Move);
                }
                CurrentDragHighlightNode = CurrentDragNode = null;
            }
        }
 
        private  void OnTreeFeedDragEnter(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.Text))
            {
                if ((e.AllowedEffect & DragDropEffects.Link) == DragDropEffects.Link)
                {
                    e.Effect = DragDropEffects.Link;
                }
                else if ((e.AllowedEffect & DragDropEffects.Move) == DragDropEffects.Move)
                {
                    e.Effect = DragDropEffects.Move;
                }
                else if ((e.AllowedEffect & DragDropEffects.Copy) == DragDropEffects.Copy)
                {
                    e.Effect = DragDropEffects.Copy;
                }
                else
                {
                    e.Effect = DragDropEffects.None;
                    CurrentDragHighlightNode = null;
                    return;
                }
                Point p = new Point(e.X, e.Y);
                TreeFeedsNodeBase t = (TreeFeedsNodeBase) treeFeeds.GetNodeFromPoint(treeFeeds.PointToClient(p));
                if (t == null)
                {
                    e.Effect = DragDropEffects.None;
                    CurrentDragHighlightNode = null;
                }
                if (t != null)
                {
                    if (t.Type == FeedNodeType.Feed)
                        CurrentDragHighlightNode = t.Parent;
                    else if (t.Type == FeedNodeType.Category || GetRoot(RootFolderType.MyFeeds).Equals(t))
                        CurrentDragHighlightNode = t;
                    else
                    {
                        e.Effect = DragDropEffects.None;
                        CurrentDragHighlightNode = null;
                    }
                }
            }
            else
            {
                e.Effect = DragDropEffects.None;
                CurrentDragHighlightNode = null;
            }
        }
 
        private static  void OnTreeFeedGiveFeedback(object sender, GiveFeedbackEventArgs e)
        {
            _log.Debug("OnTreeFeedGiveFeedback() effect:" + e.Effect);
        }
 
        private static  void OnTreeFeedQueryContiueDrag(object sender, QueryContinueDragEventArgs e)
        {
            _log.Debug("OnTreeFeedQueryContiueDrag() action:" + e.Action + ", KeyState:" +
                       e.KeyState);
        }
 
        private  void OnTreeFeedDragOver(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.Text))
            {
                if ((e.AllowedEffect & DragDropEffects.Link) == DragDropEffects.Link)
                {
                    e.Effect = DragDropEffects.Link;
                }
                else if ((e.AllowedEffect & DragDropEffects.Move) == DragDropEffects.Move)
                {
                    e.Effect = DragDropEffects.Move;
                }
                else if ((e.AllowedEffect & DragDropEffects.Copy) == DragDropEffects.Copy)
                {
                    e.Effect = DragDropEffects.Copy;
                }
                else
                {
                    e.Effect = DragDropEffects.None;
                    CurrentDragHighlightNode = null;
                    return;
                }
                Point p = treeFeeds.PointToClient(new Point(e.X, e.Y));
                TreeFeedsNodeBase t = (TreeFeedsNodeBase) treeFeeds.GetNodeFromPoint(p);
                if (t == null)
                {
                    e.Effect = DragDropEffects.None;
                    CurrentDragHighlightNode = null;
                }
                if (t != null)
                {
                    if (t.Type == FeedNodeType.Feed)
                        CurrentDragHighlightNode = t.Parent;
                    else if (t.Type == FeedNodeType.Category || GetRoot(RootFolderType.MyFeeds).Equals(t))
                        CurrentDragHighlightNode = t;
                    else
                    {
                        e.Effect = DragDropEffects.None;
                        CurrentDragHighlightNode = null;
                    }
                }
            }
            else
            {
                e.Effect = DragDropEffects.None;
                CurrentDragHighlightNode = null;
            }
        }
 
        private  void OnTreeFeedDragDrop(object sender, DragEventArgs e)
        {
            CurrentDragHighlightNode = null;
            Point p = new Point(e.X, e.Y);
            TreeFeedsNodeBase target = (TreeFeedsNodeBase) treeFeeds.GetNodeFromPoint(treeFeeds.PointToClient(p));
            if (target != null)
            {
                TreeFeedsNodeBase node2move = CurrentDragNode;
                if (target.Type == FeedNodeType.Feed)
                {
                    target = target.Parent;
                }
                if (node2move != null)
                {
                    MoveNode(node2move, target);
                }
                else
                {
                    Win32.SetForegroundWindow(Handle);
                    string sData = (string) e.Data.GetData(DataFormats.Text);
                    DelayTask(DelayedTasks.AutoSubscribeFeedUrl, new object[] {target, sData});
                }
            }
            CurrentDragNode = null;
        }
 
        private  void OnTimerTreeNodeExpandElapsed(object sender, ElapsedEventArgs e)
        {
        }
 
        private  void OnTimerFeedsRefreshElapsed(object sender, ElapsedEventArgs e)
        {
            if (owner.InternetAccessAllowed && owner.FeedHandler.RefreshRate > 0)
            {
                UpdateAllFeeds(false);
            }
        }
 
        private  void OnTimerCommentFeedsRefreshElapsed(object sender, ElapsedEventArgs e)
        {
            if (owner.InternetAccessAllowed && owner.FeedHandler.RefreshRate > 0)
            {
                UpdateAllCommentFeeds(true);
            }
        }
 
        private  void OnTimerStartupTick(object sender, EventArgs e)
        {
            _startupTimer.Enabled = false;
            if (owner.InternetAccessAllowed)
            {
                UpdateAllFeeds(owner.Preferences.FeedRefreshOnStartup);
            }
        }
 
        private  void OnTimerResetStatusTick(object sender, EventArgs e)
        {
            _timerResetStatus.Stop();
            SetGuiStateFeedback(String.Empty);
            if (_trayManager.CurrentState == ApplicationTrayState.BusyRefreshFeeds ||
                GetRoot(RootFolderType.MyFeeds).UnreadCount == 0)
            {
                _trayManager.SetState(ApplicationTrayState.NormalIdle);
            }
        }
 
        internal  void OnFeedListItemActivate(object sender, EventArgs e)
        {
            if (listFeedItems.SelectedItems.Count == 0)
                return;
            if ((MouseButtons & MouseButtons.Right) == MouseButtons.Right)
                return;
            ThreadedListViewItem selectedItem = (ThreadedListViewItem) listFeedItems.SelectedItems[0];
            OnFeedListItemActivateManually(selectedItem);
        }
 
        internal  void OnFeedListItemActivateManually(ThreadedListViewItem selectedItem)
        {
            try
            {
                NewsItem item = CurrentSelectedFeedItem = (NewsItem) selectedItem.Key;
                TreeFeedsNodeBase tn = TreeSelectedFeedsNode;
                string stylesheet;
                if (item != null && !item.HasContent)
                {
                    owner.FeedHandler.GetCachedContentForItem(item);
                }
                RefreshTreeFeedContextMenus(tn);
                if (item != null && tn != _sentItemsFeedsNode &&
                    item.CommentStyle != SupportedCommentStyle.None &&
                    owner.InternetAccessAllowed)
                    owner.Mediator.SetEnabled("+cmdFeedItemPostReply");
                else
                    owner.Mediator.SetEnabled("-cmdFeedItemPostReply");
                SearchCriteriaCollection searchCriterias = null;
                FinderNode agNode = CurrentSelectedFeedsNode as FinderNode;
                if (agNode != null && agNode.Finder.DoHighlight)
                    searchCriterias = agNode.Finder.SearchCriterias;
                bool itemJustRead = false;
                if (item != null && !item.BeenRead)
                {
                    itemJustRead = item.BeenRead = true;
                    if (item is SearchHitNewsItem)
                    {
                        SearchHitNewsItem sItem = item as SearchHitNewsItem;
                        NewsItem realItem = owner.FeedHandler.FindNewsItem(sItem);
                        if (realItem != null)
                        {
                            realItem.BeenRead = true;
                            item = realItem;
                        }
                    }
                }
                if (item == null)
                {
                    htmlDetail.Clear();
                    FeedDetailTabState.Url = String.Empty;
                    RefreshDocumentState(_docContainer.ActiveDocument);
                }
                else if (!item.HasContent && !string.IsNullOrEmpty(item.Link))
                {
                    if (owner.Preferences.NewsItemOpenLinkInDetailWindow)
                    {
                        htmlDetail.Navigate(item.Link);
                    }
                    else
                    {
                        stylesheet = (item.Feed != null ? owner.FeedHandler.GetStyleSheet(item.Feed.link) : String.Empty);
                        htmlDetail.Html = owner.FormatNewsItem(stylesheet, item, searchCriterias);
                        htmlDetail.Navigate(null);
                    }
                    FeedDetailTabState.Url = item.Link;
                    if (! _navigationActionInProgress)
                    {
                        AddHistoryEntry(tn, item);
                    }
                    else
                    {
                        RefreshDocumentState(_docContainer.ActiveDocument);
                    }
                }
                else
                {
                    stylesheet = (item.Feed != null ? owner.FeedHandler.GetStyleSheet(item.Feed.link) : String.Empty);
                    htmlDetail.Html = owner.FormatNewsItem(stylesheet, item, searchCriterias);
                    htmlDetail.Navigate(null);
                    FeedDetailTabState.Url = item.Link;
                    if (! _navigationActionInProgress)
                    {
                        AddHistoryEntry(tn, item);
                    }
                    else
                    {
                        RefreshDocumentState(_docContainer.ActiveDocument);
                    }
                }
                if (item != null)
                {
                    if (item.WatchComments && string.IsNullOrEmpty(item.CommentRssUrl))
                    {
                        MarkCommentsAsViewed(tn, item);
                        ApplyStyles(selectedItem, true);
                    }
                    if (itemJustRead)
                    {
                        ApplyStyles(selectedItem, true);
                        SmartFolderNodeBase sfNode = CurrentSelectedFeedsNode as SmartFolderNodeBase;
                        if (selectedItem.ImageIndex > 0) selectedItem.ImageIndex--;
                        bool isTopLevelItem = (selectedItem.IndentLevel == 0);
                        int equalItemsRead = (isTopLevelItem ? 1 : 0);
                        lock (listFeedItems.Items)
                        {
                            for (int j = 0; j < listFeedItems.Items.Count; j++)
                            {
                                ThreadedListViewItem th = listFeedItems.Items[j];
                                NewsItem selfRef = th.Key as NewsItem;
                                if (item.Equals(selfRef) && (th.ImageIndex%2) != 0)
                                {
                                    ApplyStyles(th, true);
                                    th.ImageIndex--;
                                    if (!selfRef.BeenRead)
                                    {
                                        selfRef.BeenRead = true;
                                    }
                                    if (th.IndentLevel == 0)
                                    {
                                        isTopLevelItem = true;
                                        equalItemsRead++;
                                    }
                                }
                            }
                        }
                        if (isTopLevelItem && tn.Type == FeedNodeType.Feed || tn.Type == FeedNodeType.SmartFolder ||
                            tn.Type == FeedNodeType.Finder)
                        {
                            UpdateTreeNodeUnreadStatus(tn, -equalItemsRead);
                            UnreadItemsNode.MarkItemRead(item);
                        }
                        TreeFeedsNodeBase root = GetRoot(RootFolderType.MyFeeds);
                        if (item.Feed.link == tn.DataKey)
                        {
                            item.Feed.containsNewMessages = (tn.UnreadCount != 0);
                        }
                        else
                        {
                            if (agNode != null) agNode.UpdateReadStatus();
                            if (sfNode != null) sfNode.UpdateReadStatus();
                            if (isTopLevelItem && tn.Type == FeedNodeType.Category)
                                UnreadItemsNode.MarkItemRead(item);
                            TreeFeedsNodeBase refNode = TreeHelper.FindNode(root, item.Feed);
                            if (refNode != null)
                            {
                                DelayTask(DelayedTasks.RefreshTreeUnreadStatus, new object[] {refNode, -1});
                                item.Feed.containsNewMessages = (refNode.UnreadCount != 0);
                            }
                            else
                            {
                                string hash = RssHelper.GetHashCode(item);
                                if (!tempFeedItemsRead.ContainsKey(hash))
                                    tempFeedItemsRead.Add(hash, null );
                            }
                        }
                        owner.FeedWasModified(item.Feed, NewsFeedProperty.FeedItemReadState);
                    }
                }
            }
            catch (Exception ex)
            {
                _log.Error("OnFeedListItemActivateManually() failed.", ex);
            }
        }
 
        private static  string GetOriginalFeedUrl(INewsItem currentNewsItem)
        {
            string feedUrl = null;
            if (currentNewsItem.OptionalElements.ContainsKey(AdditionalFeedElements.OriginalFeedOfWatchedItem))
            {
                string str = (string) currentNewsItem.OptionalElements[AdditionalFeedElements.OriginalFeedOfWatchedItem];
                if (
                    str.StartsWith("<" + AdditionalFeedElements.ElementPrefix + ":" +
                                   AdditionalFeedElements.OriginalFeedOfWatchedItem.Name) ||
                    str.StartsWith("<" + AdditionalFeedElements.OldElementPrefix + ":" +
                                   AdditionalFeedElements.OriginalFeedOfWatchedItem.Name))
                {
                    int startIndex = str.IndexOf(">") + 1;
                    int endIndex = str.LastIndexOf("<");
                    feedUrl = str.Substring(startIndex, endIndex - startIndex);
                }
            }
            return feedUrl;
        }
 
        private  void MarkCommentsAsViewed(TreeFeedsNodeBase tn, NewsItem currentNewsItem)
        {
            INewsFeed feed = currentNewsItem.Feed;
            bool commentsJustRead = currentNewsItem.HasNewComments;
            currentNewsItem.HasNewComments = false;
            if (commentsJustRead && (CurrentSelectedFeedsNode != null))
            {
                TreeFeedsNodeBase refNode = null;
                if (tn.Type == FeedNodeType.Feed)
                {
                    UpdateCommentStatus(tn, new List<NewsItem>(new NewsItem[] {currentNewsItem}), true);
                }
                else
                {
                    if (tn.Type == FeedNodeType.Category || tn.Type == FeedNodeType.Finder)
                    {
                        refNode = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), currentNewsItem.Feed);
                        UpdateCommentStatus(refNode, new List<NewsItem>(new NewsItem[] {currentNewsItem}), true);
                        if (tn.Type == FeedNodeType.Finder)
                            tn.UpdateCommentStatus(tn, -1);
                    }
                    else if (tn.Type == FeedNodeType.SmartFolder)
                    {
                        string feedUrl = GetOriginalFeedUrl(currentNewsItem);
                        if (feedUrl != null && owner.FeedHandler.FeedsTable.TryGetValue(feedUrl, out feed))
                        {
                            IList<NewsItem> newsItems = owner.FeedHandler.GetCachedItemsForFeed(feedUrl);
                            foreach (NewsItem ni in newsItems)
                            {
                                if (currentNewsItem.Equals(ni))
                                {
                                    ni.HasNewComments = false;
                                }
                            }
                            refNode = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), feedUrl);
                            UpdateCommentStatus(refNode, new List<NewsItem>(new NewsItem[] {currentNewsItem}), true);
                        }
                    }
                    if (refNode == null)
                    {
                        feed.containsNewComments = (tn.ItemsWithNewCommentsCount != 0);
                    }
                    else
                    {
                        feed.containsNewComments = (refNode.ItemsWithNewCommentsCount != 0);
                    }
                    owner.FeedWasModified(feed, NewsFeedProperty.FeedItemNewCommentsRead);
                }
            }
        }
 
        private  void OnFeedListExpandThread(object sender, ThreadEventArgs e)
        {
            try
            {
                NewsItem currentNewsItem = (NewsItem) e.Item.Key;
                NewsItem[] ikp = new NewsItem[e.Item.KeyPath.Length];
                e.Item.KeyPath.CopyTo(ikp, 0);
                IList<NewsItem> itemKeyPath = ikp;
                ColumnKeyIndexMap colIndex = listFeedItems.Columns.GetColumnIndexMap();
                ICollection<NewsItem> outGoingItems =
                    owner.FeedHandler.GetItemsFromOutGoingLinks(currentNewsItem, itemKeyPath);
                ICollection<NewsItem> inComingItems =
                    owner.FeedHandler.GetItemsWithIncomingLinks(currentNewsItem, itemKeyPath);
                ArrayList childs = new ArrayList(outGoingItems.Count + inComingItems.Count + 1);
                ThreadedListViewItem newListItem;
                try
                {
                    foreach (NewsItem o in outGoingItems)
                    {
                        bool hasRelations = NewsItemHasRelations(o, itemKeyPath);
                        newListItem =
                            CreateThreadedLVItem(o, hasRelations, Resource.NewsItemImage.OutgoingRead, colIndex, false);
                        _filterManager.Apply(newListItem);
                        childs.Add(newListItem);
                    }
                }
                catch (Exception e1)
                {
                    _log.Error("OnFeedListExpandThread exception (iterate outgoing)", e1);
                }
                try
                {
                    foreach (NewsItem o in inComingItems)
                    {
                        bool hasRelations = NewsItemHasRelations(o, itemKeyPath);
                        newListItem =
                            CreateThreadedLVItem(o, hasRelations, Resource.NewsItemImage.IncomingRead, colIndex, false);
                        _filterManager.Apply(newListItem);
                        childs.Add(newListItem);
                    }
                }
                catch (Exception e2)
                {
                    _log.Error("OnFeedListExpandThread exception (iterate incoming)", e2);
                }
                if (currentNewsItem.HasExternalRelations)
                {
                    if (currentNewsItem.GetExternalRelations().Count == 0 ||
                        currentNewsItem.CommentCount != currentNewsItem.GetExternalRelations().Count)
                    {
                        if (owner.InternetAccessAllowed)
                        {
                            ThreadedListViewItemPlaceHolder insertionPoint =
                                (ThreadedListViewItemPlaceHolder)
                                CreateThreadedLVItemInfo(SR.GUIStatusLoadingChildItems, false);
                            childs.Add(insertionPoint);
                            BeginLoadCommentFeed(currentNewsItem, insertionPoint.InsertionPointTicket, itemKeyPath);
                        }
                        else
                        {
                            newListItem = CreateThreadedLVItemInfo(SR.GUIStatusChildItemsNA, false);
                            childs.Add(newListItem);
                        }
                    }
                    else
                    {
                        foreach (NewsItem o in currentNewsItem.GetExternalRelations())
                        {
                            bool hasRelations = NewsItemHasRelations(o, itemKeyPath);
                            o.BeenRead = tempFeedItemsRead.ContainsKey(RssHelper.GetHashCode(o));
                            newListItem =
                                CreateThreadedLVItem(o, hasRelations, Resource.NewsItemImage.CommentRead, colIndex, true);
                            _filterManager.Apply(newListItem);
                            childs.Add(newListItem);
                        }
                    }
                }
                e.ChildItems = new ThreadedListViewItem[childs.Count];
                childs.CopyTo(e.ChildItems);
                MarkCommentsAsViewed(CurrentSelectedFeedsNode, currentNewsItem);
                ApplyStyles(e.Item, currentNewsItem.BeenRead, currentNewsItem.HasNewComments);
            }
            catch (Exception ex)
            {
                _log.Error("OnFeedListExpandThread exception", ex);
            }
        }
 
        private  void OnFeedListAfterExpandThread(object sender, ThreadEventArgs e)
        {
            ApplyNewsItemPropertyImages(e.ChildItems);
        }
 
        private  void OnFeedListLayoutChanged(object sender, ListLayoutEventArgs e)
        {
            if (e.Layout.Columns.Count > 0)
            {
                EmptyListView();
                lock (listFeedItems.Columns)
                {
                    listFeedItems.Columns.Clear();
                    int i = 0;
                    IList<int> colW = e.Layout.ColumnWidths;
                    foreach (string colID in e.Layout.Columns)
                    {
                        AddListviewColumn(colID, colW[i++]);
                    }
                }
            }
            RefreshListviewColumnContextMenu();
        }
 
        private  void OnFeedListLayoutModified(object sender, ListLayoutEventArgs e)
        {
            if (TreeSelectedFeedsNode != null)
                SetFeedHandlerFeedColumnLayout(TreeSelectedFeedsNode, e.Layout);
        }
 
        private  void OnFeedListItemsColumnClick(object sender, ColumnClickEventArgs e)
        {
            if (listFeedItems.Items.Count == 0)
                return;
            if (listFeedItems.SelectedItems.Count > 0)
                return;
            TreeFeedsNodeBase feedsNode = CurrentSelectedFeedsNode;
            if (feedsNode == null)
                return;
            bool unreadOnly = true;
            if (feedsNode.Type == FeedNodeType.Finder)
                unreadOnly = false;
            IList<NewsItem> items = NewsItemListFrom(listFeedItems.Items, unreadOnly);
            if (items == null || items.Count <= 1)
                return;
            Hashtable temp = new Hashtable();
            foreach (NewsItem item in items)
            {
                FeedInfo fi;
                if (temp.ContainsKey(item.Feed.link))
                {
                    fi = (FeedInfo) temp[item.Feed.link];
                }
                else
                {
                    fi = (FeedInfo) item.FeedDetails.Clone();
                    fi.ItemsList.Clear();
                    temp.Add(item.Feed.link, fi);
                }
                fi.ItemsList.Add(item);
            }
            string category = feedsNode.CategoryStoreName;
            FeedInfoList redispItems = new FeedInfoList(category);
            foreach (FeedInfo fi in temp.Values)
            {
                if (fi.ItemsList.Count > 0)
                    redispItems.Add(fi);
            }
            BeginTransformFeedList(redispItems, CurrentSelectedFeedsNode,
                                   owner.FeedHandler.GetCategoryStyleSheet(category));
        }
 
        private  void OnFeedListMouseDown(object sender, MouseEventArgs e)
        {
            try
            {
                ListView lv = (ListView) sender;
                ThreadedListViewItem lvi = null;
                try
                {
                    lvi = (ThreadedListViewItem) lv.GetItemAt(e.X, e.Y);
                }
                catch
                {
                }
                if (e.Button == MouseButtons.Right)
                {
                    if (lv.Items.Count > 0)
                    {
                        if (lvi != null)
                        {
                            lvi.Selected = true;
                            lvi.Focused = true;
                            RefreshListviewContextMenu();
                            OnFeedListItemActivateManually(lvi);
                        }
                    }
                }
                else
                {
                    if (lv.Items.Count <= 0)
                        return;
                    if (lvi != null && e.Clicks > 1)
                    {
                        NewsItem item = CurrentSelectedFeedItem = (NewsItem) lvi.Key;
                        lv.SelectedItems.Clear();
                        lvi.Selected = true;
                        lvi.Focused = true;
                        if (item != null && !string.IsNullOrEmpty(item.Link))
                        {
                            if (!UrlRequestHandledExternally(item.Link, false))
                                DetailTabNavigateToUrl(item.Link, null, false, true);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                _log.Error("OnFeedListMouseDown() failed", ex);
            }
        }
 
        private  void OnStatusPanelClick(object sender, StatusBarPanelClickEventArgs e)
        {
            if (e.Clicks > 1 && e.StatusBarPanel == statusBarConnectionState)
            {
                owner.UpdateInternetConnectionState(true);
            }
        }
 
        private  void OnStatusPanelLocationChanged(object sender, EventArgs e)
        {
            progressBrowser.SetBounds(_status.Width -
                                      (statusBarRssParser.Width + statusBarConnectionState.Width +
                                       BrowserProgressBarWidth + 10),
                                      _status.Location.Y + 6, 0, 0, BoundsSpecified.Location);
        }
 
        private  void RePopulateListviewWithCurrentContent()
        {
            RePopulateListviewWithContent(NewsItemListFrom(listFeedItems.Items));
        }
 
        private  void RePopulateListviewWithContent(IList<NewsItem> newsItemList)
        {
            if (newsItemList == null)
                newsItemList = new List<NewsItem>(0);
            ThreadedListViewItem lvLastSelected = null;
            if (listFeedItems.SelectedItems.Count > 0)
                lvLastSelected = (ThreadedListViewItem) listFeedItems.SelectedItems[0];
            bool categorizedView = (CurrentSelectedFeedsNode.Type == FeedNodeType.Category) ||
                                   (CurrentSelectedFeedsNode.Type == FeedNodeType.Finder);
            PopulateListView(CurrentSelectedFeedsNode, newsItemList, true, categorizedView, CurrentSelectedFeedsNode);
            if (lvLastSelected != null && lvLastSelected.IndentLevel == 0)
            {
                ReSelectListViewItem((NewsItem) lvLastSelected.Key);
            }
        }
 
        private  void ReSelectListViewItem(IRelation item)
        {
            if (item == null) return;
            string selItemId = item.Id;
            if (selItemId != null)
            {
                for (int i = 0; i < listFeedItems.Items.Count; i++)
                {
                    ThreadedListViewItem theItem = listFeedItems.Items[i];
                    string thisItemId = ((NewsItem) theItem.Key).Id;
                    if (selItemId.CompareTo(thisItemId) == 0)
                    {
                        listFeedItems.Items[i].Selected = true;
                        listFeedItems.EnsureVisible(listFeedItems.Items[i].Index);
                        break;
                    }
                }
            }
        }
 
        private static  IList<NewsItem> NewsItemListFrom(ThreadedListViewItemCollection list)
        {
            return NewsItemListFrom(list, false);
        }
 
        private static  IList<NewsItem> NewsItemListFrom(ThreadedListViewItemCollection list, bool unreadOnly)
        {
            List<NewsItem> items = new List<NewsItem>(list.Count);
            for (int i = 0; i < list.Count; i++)
            {
                ThreadedListViewItem tlvi = list[i];
                if (tlvi.IndentLevel == 0)
                {
                    NewsItem item = (NewsItem) tlvi.Key;
                    if (unreadOnly && item != null && item.BeenRead)
                        item = null;
                    if (item != null)
                        items.Add(item);
                }
            }
            return items;
        }
 
        private  void OnFeedListItemDrag(object sender, ItemDragEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                ThreadedListViewItem item = (ThreadedListViewItem) e.Item;
                NewsItem r = (NewsItem) item.Key;
                if (r.Link != null)
                {
                    treeFeeds.AllowDrop = false;
                    DoDragDrop(r.Link, DragDropEffects.All | DragDropEffects.Link);
                    treeFeeds.AllowDrop = true;
                }
            }
        }
 
        private  void OnFeedListItemKeyUp(object sender, KeyEventArgs e)
        {
            try
            {
                if (!listFeedItems.Focused)
                    return;
                if (e.KeyCode == Keys.Down || e.KeyCode == Keys.PageDown || e.KeyCode == Keys.End)
                {
                    if (listFeedItems.SelectedItems.Count == 1)
                        if (listFeedItems.SelectedItems[0].Index <= listFeedItems.Items.Count)
                            OnFeedListItemActivate(sender, EventArgs.Empty);
                }
                else if (e.KeyCode == Keys.Up || e.KeyCode == Keys.PageUp || e.KeyCode == Keys.Home)
                {
                    if (listFeedItems.SelectedItems.Count == 1)
                        if (listFeedItems.SelectedItems[0].Index >= 0)
                            OnFeedListItemActivate(sender, EventArgs.Empty);
                }
                else if (e.KeyCode == Keys.A && (e.Modifiers & Keys.Control) == Keys.Control)
                {
                    if (listFeedItems.Items.Count > 0 && listFeedItems.Items.Count != listFeedItems.SelectedItems.Count)
                    {
                        try
                        {
                            listFeedItems.BeginUpdate();
                            lock (listFeedItems.Items)
                            {
                                for (int i = 0; i < listFeedItems.Items.Count; i++)
                                {
                                    listFeedItems.Items[i].Selected = true;
                                }
                            }
                        }
                        finally
                        {
                            listFeedItems.EndUpdate();
                        }
                    }
                }
                else if (e.KeyCode == Keys.Delete)
                {
                    RemoveSelectedFeedItems();
                }
            }
            catch (Exception ex)
            {
                _log.Error("OnFeedListItemKeyUp() failed", ex);
            }
        }
 
        private  void OnTrayIconDoubleClick(object sender, EventArgs e)
        {
            owner.CmdShowMainGui(null);
            _beSilentOnBalloonPopupCounter = 0;
        }
 
        private  void OnTrayAniBalloonTimeoutClose(object sender, EventArgs e)
        {
            _beSilentOnBalloonPopupCounter = 12;
        }
 
        internal  void OnNavigateComboBoxKeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Return && e.Control == false)
            {
                DetailTabNavigateToUrl(UrlText, null, e.Shift, false);
            }
        }
 
        internal static  void OnNavigateComboBoxDragOver(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.Text))
            {
                if ((e.AllowedEffect & DragDropEffects.Link) == DragDropEffects.Link)
                {
                    e.Effect = DragDropEffects.Link;
                }
                else if ((e.AllowedEffect & DragDropEffects.Move) == DragDropEffects.Move)
                {
                    e.Effect = DragDropEffects.Move;
                }
                else if ((e.AllowedEffect & DragDropEffects.Copy) == DragDropEffects.Copy)
                {
                    e.Effect = DragDropEffects.Copy;
                }
                else
                {
                    e.Effect = DragDropEffects.None;
                }
            }
            else
            {
                e.Effect = DragDropEffects.None;
            }
        }
 
        internal  void OnNavigateComboBoxDragDrop(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.Text))
            {
                string sData = (string) e.Data.GetData(typeof (string));
                try
                {
                    Uri uri = new Uri(sData);
                    UrlText = uri.CanonicalizedUri();
                }
                catch
                {
                }
            }
        }
 
        internal  void OnSearchComboBoxKeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Return)
            {
                e.Handled = true;
                StartSearch(null);
            }
        }
 
        internal  void OnSearchComboBoxDragOver(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.Text))
            {
                if ((e.AllowedEffect & DragDropEffects.Link) == DragDropEffects.Link)
                {
                    e.Effect = DragDropEffects.Link;
                }
                else if ((e.AllowedEffect & DragDropEffects.Move) == DragDropEffects.Move)
                {
                    e.Effect = DragDropEffects.Move;
                }
                else if ((e.AllowedEffect & DragDropEffects.Copy) == DragDropEffects.Copy)
                {
                    e.Effect = DragDropEffects.Copy;
                }
                else
                {
                    e.Effect = DragDropEffects.None;
                }
            }
            else
            {
                e.Effect = DragDropEffects.None;
            }
        }
 
        internal  void OnSearchComboBoxDragDrop(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.Text))
            {
                string sData = (string) e.Data.GetData(typeof (string));
                WebSearchText = sData;
            }
        }
 
        private  void OnHtmlWindowError(string description, string url, int line)
        {
            HtmlControl hc = (HtmlControl) _docContainer.ActiveDocument.Controls[0];
            if (hc != null)
            {
                IHTMLWindow2 window = (IHTMLWindow2) hc.Document2.GetParentWindow();
                IHTMLEventObj eventObj = window.eventobj;
                eventObj.ReturnValue = true;
            }
        }
 
        private  void OnWebStatusTextChanged(object sender, BrowserStatusTextChangeEvent e)
        {
            SetBrowserStatusBarText(e.text);
        }
 
        private  void OnWebBeforeNavigate(object sender, BrowserBeforeNavigate2Event e)
        {
            bool userNavigates = _webUserNavigated;
            bool forceNewTab = _webForceNewTab;
            string url = e.url;
            if (!url.ToLower().StartsWith("javascript:"))
            {
                _webForceNewTab = _webUserNavigated = false;
            }
            if (!url.Equals("about:blank"))
            {
                if (owner.InterceptUrlNavigation(url))
                {
                    e.Cancel = true;
                    return;
                }
                if (url.StartsWith("mailto:") || url.StartsWith("news:"))
                {
                    return;
                }
                bool framesAllowed = false;
                bool forceSetFocus = true;
                bool tabCanClose = true;
                if ((ModifierKeys & Keys.Control) == Keys.Control)
                    forceSetFocus = false;
                HtmlControl hc = sender as HtmlControl;
                if (hc != null)
                {
                    DockControl dc = (DockControl) hc.Tag;
                    ITabState ts = (ITabState) dc.Tag;
                    tabCanClose = ts.CanClose;
                    framesAllowed = hc.FrameDownloadEnabled;
                }
                if (userNavigates && UrlRequestHandledExternally(url, forceNewTab))
                {
                    e.Cancel = true;
                    return;
                }
                if (!tabCanClose && !userNavigates && !forceNewTab)
                {
                    if (!framesAllowed)
                        e.Cancel = !e.IsRootPage;
                    return;
                }
                if ((!tabCanClose && userNavigates) || forceNewTab)
                {
                    e.Cancel = true;
                    DelayTask(DelayedTasks.NavigateToWebUrl, new object[] {url, null, forceNewTab, forceSetFocus});
                }
            }
        }
 
        private  void OnWebNavigateComplete(object sender, BrowserNavigateComplete2Event e)
        {
            try
            {
                HtmlControl hc = (HtmlControl) sender;
                HTMLWindowEvents2_Event window = (HTMLWindowEvents2_Event) hc.Document2.GetParentWindow();
                window.onerror += OnHtmlWindowError;
                if (!string.IsNullOrEmpty(e.url) && e.url != "about:blank" && e.IsRootPage)
                {
                    AddUrlToHistory(e.url);
                    DockControl doc = (DockControl) hc.Tag;
                    ITabState state = (ITabState) doc.Tag;
                    state.Url = e.url;
                    RefreshDocumentState(doc);
                    DelayTask(DelayedTasks.ClearBrowserStatusInfo, null, 2000);
                }
            }
            catch (Exception ex)
            {
                _log.Error("OnWebNavigateComplete(): " + e.url, ex);
            }
        }
 
        private  void OnWebDocumentComplete(object sender, BrowserDocumentCompleteEvent e)
        {
            try
            {
                HtmlControl hc = (HtmlControl) sender;
                HTMLWindowEvents2_Event window = (HTMLWindowEvents2_Event) hc.Document2.GetParentWindow();
                window.onerror += OnHtmlWindowError;
                if (!string.IsNullOrEmpty(e.url) && e.url != "about:blank" && e.IsRootPage)
                {
                    AddUrlToHistory(e.url);
                    DockControl doc = (DockControl) hc.Tag;
                    ITabState state = (ITabState) doc.Tag;
                    state.Url = e.url;
                    RefreshDocumentState(doc);
                    owner.BackgroundDiscoverFeedsHandler.DiscoverFeedInContent(hc.DocumentInnerHTML, state.Url,
                                                                               state.Title);
                }
            }
            catch (Exception ex)
            {
                _log.Error("OnWebDocumentComplete(): " + e.url, ex);
            }
        }
 
        private  void OnWebTitleChanged(object sender, BrowserTitleChangeEvent e)
        {
            try
            {
                HtmlControl hc = (HtmlControl) sender;
                if (hc == null) return;
                DockControl doc = (DockControl) hc.Tag;
                if (doc == null) return;
                ITabState state = (ITabState) doc.Tag;
                if (state == null) return;
                state.Title = e.text;
                RefreshDocumentState(doc);
            }
            catch (Exception ex)
            {
                _log.Error("OnWebTitleChanged()", ex);
            }
        }
 
        private static  void OnWebCommandStateChanged(object sender, BrowserCommandStateChangeEvent e)
        {
            try
            {
                ITabState state = GetTabStateFor(sender as HtmlControl);
                if (state == null) return;
                if (e.command == CommandStateChangeConstants.CSC_NAVIGATEBACK)
                    state.CanGoBack = e.enable;
                else if (e.command == CommandStateChangeConstants.CSC_NAVIGATEFORWARD)
                    state.CanGoForward = e.enable;
                else if (e.command == CommandStateChangeConstants.CSC_UPDATECOMMANDS)
                {
                }
            }
            catch (Exception ex)
            {
                _log.Error("OnWebCommandStateChanged() ", ex);
            }
        }
 
        private  void OnWebNewWindow(object sender, BrowserNewWindowEvent e)
        {
            try
            {
                bool userNavigates = _webUserNavigated;
                bool forceNewTab = _webForceNewTab;
                _webForceNewTab = _webUserNavigated = false;
                e.Cancel = true;
                string url = e.url;
                _log.Debug("OnWebNewWindow(): '" + url + "'");
                bool forceSetFocus = true;
                if (UrlRequestHandledExternally(url, forceNewTab))
                {
                    return;
                }
                if (userNavigates)
                {
                    DelayTask(DelayedTasks.NavigateToWebUrl, new object[] {url, null, true, forceSetFocus});
                }
            }
            catch (Exception ex)
            {
                _log.Error("OnWebNewWindow(): " + e.url, ex);
            }
        }
 
        private  void OnWebQuit(object sender, EventArgs e)
        {
            try
            {
                RemoveDocTab(_docContainer.ActiveDocument);
            }
            catch (Exception ex)
            {
                _log.Error("OnWebQuit()", ex);
            }
        }
 
        private  void OnWebTranslateAccelerator(object sender, KeyEventArgs e)
        {
            try
            {
                bool shift = (ModifierKeys & Keys.Shift) == Keys.Shift;
                bool ctrl = (ModifierKeys & Keys.Control) == Keys.Control;
                bool alt = (ModifierKeys & Keys.Alt) == Keys.Alt;
                bool noModifier = (!shift && !ctrl && ! alt);
                bool shiftOnly = (shift && !ctrl && !alt);
                bool ctrlOnly = (ctrl && !shift && !alt);
                bool ctrlShift = (ctrl && shift && !alt);
                if (_shortcutHandler.IsCommandInvoked("BrowserCreateNewTab", e.KeyData))
                {
                    owner.CmdBrowserCreateNewTab(null);
                    e.Handled = true;
                }
                if (_shortcutHandler.IsCommandInvoked("Help", e.KeyData))
                {
                    Help.ShowHelp(this, helpProvider1.HelpNamespace, HelpNavigator.TableOfContents);
                    e.Handled = true;
                }
                if (!e.Handled)
                {
                    e.Handled = (e.KeyCode == Keys.N && ctrlOnly ||
                                 e.KeyCode == Keys.F1);
                }
                if (!e.Handled)
                {
                    if (e.KeyCode == Keys.Tab && noModifier)
                    {
                        if (htmlDetail.Document2 != null && null == htmlDetail.Document2.GetActiveElement())
                        {
                            if (treeFeeds.Visible)
                            {
                                treeFeeds.Focus();
                                e.Handled = true;
                            }
                            else if (listFeedItems.Visible)
                            {
                                listFeedItems.Focus();
                                e.Handled = true;
                            }
                        }
                    }
                    else if (e.KeyCode == Keys.Tab && shiftOnly)
                    {
                        if (htmlDetail.Document2 != null && null == htmlDetail.Document2.GetActiveElement())
                        {
                            if (listFeedItems.Visible)
                            {
                                listFeedItems.Focus();
                                e.Handled = true;
                            }
                            else if (treeFeeds.Visible)
                            {
                                treeFeeds.Focus();
                                e.Handled = true;
                            }
                        }
                    }
                }
                if (!e.Handled)
                {
                    if (e.KeyCode == Keys.Tab && ctrlOnly)
                    {
                        if (_docContainer.Documents.Length > 1)
                        {
                            InvokeProcessCmdKey(_docContainer.ActiveDocument, Keys.Next | Keys.Control);
                            e.Handled = true;
                        }
                    }
                    else if (e.KeyCode == Keys.Tab && ctrlShift)
                    {
                        if (_docContainer.Documents.Length > 1)
                        {
                            InvokeProcessCmdKey(_docContainer.ActiveDocument, Keys.Prior | Keys.Control);
                            e.Handled = true;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                _log.Error("OnWebTranslateAccelerator(): " + e.KeyCode, ex);
            }
        }
 
        private  void InvokeProcessCmdKey(DockControl c, Keys keyData)
        {
            if (c != null)
            {
                Type cType = c.GetType();
                try
                {
                    Message m = Message.Create(Handle, (int) Win32.Message.WM_NULL, IntPtr.Zero, IntPtr.Zero);
                    cType.InvokeMember("ProcessCmdKey",
                                           BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.InvokeMethod,
                                           null, c, new object[] {m, keyData});
                }
                catch (Exception ex)
                {
                    _log.Error("InvokeProcessCmdKey() failed: " + ex.Message);
                }
            }
        }
 
        private  void OnWebProgressChanged(object sender, BrowserProgressChangeEvent e)
        {
            try
            {
                if (_lastBrowserThatProgressChanged == null)
                    _lastBrowserThatProgressChanged = sender;
                if (sender != _lastBrowserThatProgressChanged)
                {
                    DeactivateWebProgressInfo();
                    return;
                }
                if (((e.progress < 0) || (e.progressMax <= 0)) || (e.progress >= e.progressMax))
                {
                    DeactivateWebProgressInfo();
                }
                else
                {
                    if (!progressBrowser.Visible) progressBrowser.Visible = true;
                    if (statusBarBrowserProgress.Width < BrowserProgressBarWidth)
                    {
                        statusBarBrowserProgress.Width = BrowserProgressBarWidth;
                        progressBrowser.Width = BrowserProgressBarWidth - 12;
                    }
                    progressBrowser.Minimum = 0;
                    progressBrowser.Maximum = e.progressMax;
                    progressBrowser.Value = e.progress;
                }
            }
            catch (Exception ex)
            {
                _log.Error("OnWebProgressChanged()", ex);
            }
        }
 
        private  object _lastBrowserThatProgressChanged = null;
 
        private  void DeactivateWebProgressInfo()
        {
            progressBrowser.Minimum = 0;
            progressBrowser.Maximum = 128;
            progressBrowser.Value = 128;
            progressBrowser.Visible = false;
            statusBarBrowserProgress.Width = 0;
            _lastBrowserThatProgressChanged = null;
        }
 
        public  bool CanClose
        {
            get
            {
                return false;
            }
            set
            {
            }
        }
 
        public  bool CanGoBack
        {
            get
            {
                return _feedItemImpressionHistory.CanGetPrevious &&
                       _feedItemImpressionHistory.Count > 1;
            }
            set
            {
            }
        }
 
        public  bool CanGoForward
        {
            get
            {
                return _feedItemImpressionHistory.CanGetNext;
            }
            set
            {
            }
        }
 
        public  string Title
        {
            get
            {
                if (CurrentSelectedFeedsNode != null)
                    return CurrentSelectedFeedsNode.Text;
                else
                    return String.Empty;
            }
            set
            {
            }
        }
 
        public  string Url
        {
            get
            {
                return _tabStateUrl;
            }
            set
            {
                _tabStateUrl = value;
            }
        }
 
        public  ITextImageItem[] GoBackHistoryItems(int maxItems)
        {
            return _feedItemImpressionHistory.GetHeadOfPreviousEntries(maxItems);
        }
 
        public  ITextImageItem[] GoForwardHistoryItems(int maxItems)
        {
            return _feedItemImpressionHistory.GetHeadOfNextEntries(maxItems);
        }
 
        private  void OnAnyToolbarToolClick(object sender, ToolClickEventArgs e)
        {
            owner.Mediator.Execute(e.Tool.Key);
        }
 
        internal static  void OnAnyEnterKeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar == '\r' && ModifierKeys == Keys.None)
                e.Handled = true;
        }
 
        private  void OnNewsItemSearchFinished(object sender, NewsHandler.SearchFinishedEventArgs e)
        {
            InvokeOnGui(delegate
            {
                SearchFinishedAction(e.Tag, e.MatchingFeeds, e.MatchingItems, e.MatchingFeedsCount, e.MatchingItemsCount);
            });
        }
 
        private  void OnSearchPanelStartNewsItemSearch(object sender, NewsItemSearchEventArgs e)
        {
            AsyncStartNewsSearch(e.FinderNode);
        }
 
        private  void AsyncStartNewsSearch(FinderNode node)
        {
            StartNewsSearchDelegate start = StartNewsSearch;
            start.BeginInvoke(node, AsyncInvokeCleanup, start);
        }
 
        private  void StartNewsSearch(FinderNode node)
        {
            owner.FeedHandler.SearchNewsItems(
                node.Finder.SearchCriterias,
                node.Finder.SearchScope,
                node.Finder,
                CultureInfo.CurrentUICulture.Name,
                node.Finder.ShowFullItemContent);
        }
 
        private  void OnSearchPanelBeforeNewsItemSearch(object sender, NewsItemSearchCancelEventArgs e)
        {
            SetSearchStatusText(SR.RssSearchStateMessage);
            Exception criteriaValidationException;
            if (!owner.FeedHandler.SearchHandler.ValidateSearchCriteria(
                     e.SearchCriteria, CultureInfo.CurrentUICulture.Name,
                     out criteriaValidationException))
            {
                e.Cancel = true;
                throw criteriaValidationException;
            }
            FinderNode resultContainer;
            string newName = e.ResultContainerName;
            if (!string.IsNullOrEmpty(newName))
            {
                TreeFeedsNodeBase parent = GetRoot(RootFolderType.Finder);
                if (newName.IndexOf(NewsHandler.CategorySeparator) > 0)
                {
                    string[] a = newName.Split(NewsHandler.CategorySeparator.ToCharArray());
                    parent = TreeHelper.CreateCategoryHive(parent,
                                                           String.Join(NewsHandler.CategorySeparator, a, 0, a.Length - 1),
                                                           _treeSearchFolderContextMenu,
                                                           FeedNodeType.FinderCategory);
                    newName = a[a.Length - 1].Trim();
                }
                TreeFeedsNodeBase feedsNode = TreeHelper.FindChildNode(parent, newName, FeedNodeType.Finder);
                if (feedsNode != null)
                {
                    resultContainer = (FinderNode) feedsNode;
                }
                else
                {
                    resultContainer =
                        new FinderNode(newName, Resource.SubscriptionTreeImage.SearchFolder,
                                       Resource.SubscriptionTreeImage.SearchFolderSelected, _treeSearchFolderContextMenu);
                    resultContainer.DataKey = resultContainer.InternalFeedLink;
                    parent.Nodes.Add(resultContainer);
                }
            }
            else
            {
                AddTempResultNode();
                resultContainer = _searchResultNode;
            }
            if (resultContainer.Finder == null)
            {
                resultContainer.Finder = new RssFinder(resultContainer, e.SearchCriteria,
                                                       e.CategoryScope, e.FeedScope,
                                                       ScopeResolve, true);
                if (resultContainer != _searchResultNode && !owner.FinderList.Contains(resultContainer.Finder))
                {
                    owner.FinderList.Add(resultContainer.Finder);
                }
            }
            else
            {
                resultContainer.Finder.SearchCriterias = e.SearchCriteria;
                resultContainer.Finder.SetSearchScope(e.CategoryScope, e.FeedScope);
                resultContainer.Finder.ExternalResultMerged = false;
                resultContainer.Finder.ExternalSearchPhrase = null;
                resultContainer.Finder.ExternalSearchUrl = null;
            }
            owner.SaveSearchFolders();
            resultContainer.Clear();
            UpdateTreeNodeUnreadStatus(resultContainer, 0);
            EmptyListView();
            htmlDetail.Clear();
            e.ResultContainer = resultContainer;
            TreeSelectedFeedsNode = resultContainer;
        }
 
        internal  void SearchFinishedAction(object tag, FeedInfoList matchingFeeds, List<NewsItem> matchingItems,
                                           int rssFeedsCount, int newsItemsCount)
        {
            if (newsItemsCount == 0)
            {
                searchPanel.SetControlStateTo(ItemSearchState.Finished, SR.RssSearchNoResultMessage);
            }
            else
            {
                searchPanel.SetControlStateTo(ItemSearchState.Finished, SR.RssSearchSuccessResultMessage(newsItemsCount));
            }
            RssFinder finder = tag as RssFinder;
            FinderNode tn = (finder != null ? finder.Container : null);
            if (tn != null)
            {
                tn.AddRange(matchingItems);
                if (tn.Selected)
                {
                    PopulateListView(tn, matchingItems, false, true, tn);
                    if (!tn.Finder.ShowFullItemContent)
                        BeginTransformFeedList(matchingFeeds, tn, NewsItemFormatter.SearchTemplateId);
                    else
                        BeginTransformFeedList(matchingFeeds, tn, owner.FeedHandler.Stylesheet);
                }
            }
        }
 
        private  NewsFeed[] ScopeResolve(ArrayList categories, ArrayList feedUrls)
        {
            if (categories == null && feedUrls == null)
                return new NewsFeed[] {};
            ArrayList result = new ArrayList();
            if (categories != null)
            {
                string sep = NewsHandler.CategorySeparator;
                foreach (NewsFeed f in owner.FeedHandler.FeedsTable.Values)
                {
                    foreach (string category in categories)
                    {
                        if (f.category != null && (f.category.Equals(category) || f.category.StartsWith(category + sep)))
                        {
                            result.Add(f);
                        }
                    }
                }
            }
            if (feedUrls != null)
            {
                foreach (string url in feedUrls)
                {
                    if (url != null && owner.FeedHandler.FeedsTable.ContainsKey(url))
                    {
                        result.Add(owner.FeedHandler.FeedsTable[url]);
                    }
                }
            }
            if (result.Count > 0)
            {
                NewsFeed[] fa = new NewsFeed[result.Count];
                result.CopyTo(fa);
                return fa;
            }
            return new NewsFeed[] {};
        }
 
        internal  void CmdNewRssSearch(ICommand sender)
        {
            searchPanel.ResetControlState();
            PopulateTreeRssSearchScope();
            SetSearchStatusText(String.Empty);
            if (sender != null)
                CmdDockShowRssSearch(sender);
            searchPanel.SetFocus();
        }
 
        private  void AddTempResultNode()
        {
            if (_searchResultNode == null)
            {
                _searchResultNode =
                    new TempFinderNode(SR.RssSearchResultNodeCaption, Resource.SubscriptionTreeImage.SearchFolder,
                                       Resource.SubscriptionTreeImage.SearchFolderSelected,
                                       _treeTempSearchFolderContextMenu);
                _searchResultNode.DataKey = _searchResultNode.InternalFeedLink;
                GetRoot(RootFolderType.SmartFolders).Nodes.Add(_searchResultNode);
            }
        }
 
        private  void AsyncStartRssRemoteSearch(string searchPhrase, string searchUrl, bool mergeWithLocalResults,
                                               bool initialize)
        {
            AddTempResultNode();
            if (initialize)
            {
                _searchResultNode.Clear();
                EmptyListView();
                htmlDetail.Clear();
                TreeSelectedFeedsNode = _searchResultNode;
                SetSearchStatusText(SR.RssSearchStateMessage);
            }
            SearchCriteriaCollection scc = new SearchCriteriaCollection();
            if (mergeWithLocalResults)
            {
                scc.Add(new SearchCriteriaString(searchPhrase, SearchStringElement.All, StringExpressionKind.Text));
            }
            RssFinder finder = new RssFinder(_searchResultNode, scc,
                                             null, null, ScopeResolve, true);
            finder.ExternalResultMerged = mergeWithLocalResults;
            finder.ExternalSearchPhrase = searchPhrase;
            finder.ExternalSearchUrl = searchUrl;
            _searchResultNode.Finder = finder;
            StartRssRemoteSearchDelegate start = StartRssRemoteSearch;
            start.BeginInvoke(searchUrl, _searchResultNode, AsyncInvokeCleanup, start);
            if (mergeWithLocalResults)
            {
                AsyncStartNewsSearch(_searchResultNode);
            }
        }
 
        private  void StartRssRemoteSearch(string searchUrl, FinderNode resultContainer)
        {
            try
            {
                owner.FeedHandler.SearchRemoteFeed(searchUrl, resultContainer.Finder);
            }
            catch (Exception ex)
            {
                SetSearchStatusText("Search '" + StringHelper.ShortenByEllipsis(searchUrl, 30) + "' caused a problem: " +
                                    ex.Message);
            }
        }
 
        private static  void AsyncInvokeCleanup(IAsyncResult ar)
        {
            StartNewsSearchDelegate startNewsSearchDelegate = ar.AsyncState as StartNewsSearchDelegate;
            if (startNewsSearchDelegate != null)
            {
                try
                {
                    startNewsSearchDelegate.EndInvoke(ar);
                }
                catch (Exception ex)
                {
                    _log.Error("AsyncCall 'StartNewsSearchDelegate' caused this exception", ex);
                }
                return;
            }
            StartRssRemoteSearchDelegate startRssRemoteSearchDelegate = ar.AsyncState as StartRssRemoteSearchDelegate;
            if (startRssRemoteSearchDelegate != null)
            {
                try
                {
                    startRssRemoteSearchDelegate.EndInvoke(ar);
                }
                catch (Exception ex)
                {
                    _log.Error("AsyncCall 'StartRssRemoteSearchDelegate' caused this exception", ex);
                }
                return;
            }
            GetCommentNewsItemsDelegate getCommentNewsItemsDelegate = ar.AsyncState as GetCommentNewsItemsDelegate;
            if (getCommentNewsItemsDelegate != null)
            {
                try
                {
                    getCommentNewsItemsDelegate.EndInvoke(ar);
                }
                catch (Exception ex)
                {
                    _log.Error("AsyncCall 'GetCommentNewsItemsDelegate' caused this exception", ex);
                }
                return;
            }
        }
 
           
        private  void OnExternalActivateFeedItem(NewsItem item)
        {
            DelayTask(DelayedTasks.NavigateToFeedNewsItem, item);
        }
 
           
           
        private  void NavigateToNode(TreeFeedsNodeBase feedsNode)
        {
            NavigateToNode(feedsNode, null);
        }
 
        private  void NavigateToNode(TreeFeedsNodeBase feedsNode, IEquatable<NewsItem> item)
        {
            _navigationActionInProgress = true;
            if (feedsNode != null && feedsNode.Control != null)
            {
                SelectNode(feedsNode);
                OnTreeFeedAfterSelectManually(feedsNode);
                MoveFeedDetailsToFront();
                if (item != null)
                {
                    ThreadedListViewItem foundLVItem = null;
                    for (int i = 0; i < listFeedItems.Items.Count; i++)
                    {
                        ThreadedListViewItem lvi = listFeedItems.Items[i];
                        NewsItem ti = (NewsItem) lvi.Key;
                        if (item.Equals(ti))
                        {
                            foundLVItem = lvi;
                            break;
                        }
                    }
                    if (foundLVItem == null && listFeedItems.Items.Count > 0)
                    {
                        foundLVItem = listFeedItems.Items[0];
                    }
                    if (foundLVItem != null)
                    {
                        listFeedItems.BeginUpdate();
                        listFeedItems.SelectedItems.Clear();
                        foundLVItem.Selected = true;
                        foundLVItem.Focused = true;
                        OnFeedListItemActivate(null, EventArgs.Empty);
                        SetTitleText(feedsNode.Text);
                        SetDetailHeaderText(feedsNode);
                        foundLVItem.Selected = true;
                        foundLVItem.Focused = true;
                        listFeedItems.Focus();
                        listFeedItems.EnsureVisible(foundLVItem.Index);
                        listFeedItems.EndUpdate();
                    }
                }
            }
            owner.CmdShowMainGui(null);
            _navigationActionInProgress = false;
        }
 
           
        private  void NavigateToFeedNewsItem(NewsItem item)
        {
            NavigateToNode(TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), item), item);
        }
 
        private  void NavigateToHistoryEntry(HistoryEntry historyEntry)
        {
            if (historyEntry != null)
            {
                if (historyEntry.Node != null)
                {
                    NavigateToNode(historyEntry.Node, historyEntry.Item);
                }
                else
                {
                    NavigateToFeedNewsItem(historyEntry.Item);
                }
            }
        }
 
        private  void AddHistoryEntry(TreeFeedsNodeBase feedsNode, NewsItem item)
        {
            if (feedsNode == null && item == null) return;
            if (_navigationActionInProgress) return;
            _feedItemImpressionHistory.Add(new HistoryEntry(feedsNode, item));
        }
 
        private  void AutoSubscribeFeed(TreeFeedsNodeBase parent, string feedUrl)
        {
            if (string.IsNullOrEmpty(feedUrl))
                return;
            if (parent == null)
                parent = GetRoot(RootFolderType.MyFeeds);
            string feedTitle = null;
            string category = parent.CategoryStoreName;
            string[] urls = feedUrl.Split(Environment.NewLine.ToCharArray());
            bool multipleSubscriptions = (urls.Length > 1);
            for (int i = 0; i < urls.Length; i++)
            {
                feedUrl = owner.HandleUrlFeedProtocol(urls[i]);
                try
                {
                    try
                    {
                        Uri reqUri = new Uri(feedUrl);
                        feedTitle = reqUri.Host;
                    }
                    catch (UriFormatException)
                    {
                        feedTitle = feedUrl;
                        if (!feedUrl.ToLower().StartsWith("http://"))
                        {
                            feedUrl = "http://" + feedUrl;
                        }
                    }
                    if (!multipleSubscriptions && owner.FeedHandler.FeedsTable.ContainsKey(feedUrl))
                    {
                        INewsFeed f2 = owner.FeedHandler.FeedsTable[feedUrl];
                        owner.MessageInfo(SR.GUIFieldLinkRedundantInfo(
                                              (f2.category == null
                                                   ? String.Empty
                                                   : f2.category + NewsHandler.CategorySeparator) + f2.title, f2.link));
                        return;
                    }
                    if (owner.InternetAccessAllowed)
                    {
                        PrefetchFeedThreadHandler fetchHandler = new PrefetchFeedThreadHandler(feedUrl, owner.Proxy);
                        DialogResult result = fetchHandler.Start(this, SR.GUIStatusWaitMessagePrefetchFeed(feedUrl));
                        if (result != DialogResult.OK)
                            return;
                        if (!fetchHandler.OperationSucceeds)
                        {
                            _log.Error("AutoSubscribeFeed() caused exception", fetchHandler.OperationException);
                        }
                        else
                        {
                            if (fetchHandler.DiscoveredDetails != null)
                            {
                                if (fetchHandler.DiscoveredDetails.Title != null)
                                    feedTitle = HtmlHelper.HtmlDecode(fetchHandler.DiscoveredDetails.Title);
                                INewsFeed f = fetchHandler.DiscoveredFeed;
                                f.link = feedUrl;
                                f.title = feedTitle;
                                f.refreshrate = 60;
                                f.category = category;
                                if (!owner.FeedHandler.HasCategory(f.category))
                                {
                                    owner.FeedHandler.Categories.Add(f.category);
                                }
                                f.alertEnabled = false;
                                f = owner.FeedHandler.AddFeed(f);
                                owner.FeedWasModified(f, NewsFeedProperty.FeedAdded);
                                AddNewFeedNode(f.category, f);
                                try
                                {
                                    owner.FeedHandler.AsyncGetItemsForFeed(f.link, true, true);
                                }
                                catch (Exception e)
                                {
                                    owner.PublishXmlFeedError(e, f.link, true);
                                }
                                if (!multipleSubscriptions)
                                    return;
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                    _log.Error("AutoSubscribeFeed() caused exception", ex);
                }
            }
            if (!multipleSubscriptions)
                owner.CmdNewFeed(category, feedUrl, feedTitle);
        }
 
        private  void OnOwnerFeedlistLoaded(object sender, EventArgs e)
        {
            listFeedItems.FeedColumnLayout = owner.GlobalFeedColumnLayout;
            LoadAndRestoreSubscriptionTreeState();
            if (Visible)
            {
                LoadAndRestoreBrowserTabState();
            }
            _startupTimer.Interval = 1000*
                                     (int)
                                     RssBanditApplication.ReadAppSettingsEntry(
                                         "ForcedRefreshOfFeedsAtStartupDelay.Seconds", typeof (int), 30);
            _startupTimer.Enabled = true;
        }
 
        private  Control OnWheelSupportGetChildControl(Control control)
        {
            if (control == _docContainer)
            {
                if (_docContainer.ActiveDocument != null && _docContainer.ActiveDocument != _docFeedDetails)
                    return _docContainer.ActiveDocument.Controls[0];
            }
            return null;
        }
 
        private  void OnFeedItemImpressionHistoryStateChanged(object sender, EventArgs e)
        {
            RefreshDocumentState(_docContainer.ActiveDocument);
        }
 
        private  void OnPreferencesChanged(object sender, EventArgs e)
        {
            InvokeOnGui(delegate
                            {
                                if (listFeedItems.ShowAsThreads != owner.Preferences.BuildRelationCosmos)
                                {
                                    if (owner.Preferences.BuildRelationCosmos)
                                    {
                                        listFeedItems.ShowAsThreads = true;
                                    }
                                    else
                                    {
                                        listFeedItems.ShowInGroups = false;
                                        listFeedItems.AutoGroupMode = false;
                                    }
                                }
                                SetFontAndColor(
                                    owner.Preferences.NormalFont, owner.Preferences.NormalFontColor,
                                    owner.Preferences.UnreadFont, owner.Preferences.UnreadFontColor,
                                    owner.Preferences.FlagFont, owner.Preferences.FlagFontColor,
                                    owner.Preferences.ErrorFont, owner.Preferences.ErrorFontColor,
                                    owner.Preferences.RefererFont, owner.Preferences.RefererFontColor,
                                    owner.Preferences.NewCommentsFont, owner.Preferences.NewCommentsFontColor
                                    );
                                owner.Mediator.SetEnabled(owner.Preferences.UseRemoteStorage, "cmdUploadFeeds",
                                                          "cmdDownloadFeeds");
                                if (Visible)
                                {
                                    OnFeedListItemActivate(this, EventArgs.Empty);
                                    if (CurrentSelectedFeedsNode != null)
                                    {
                                        CurrentSelectedFeedsNode.Control.SelectedNodes.Clear();
                                        CurrentSelectedFeedsNode.Selected = true;
                                        CurrentSelectedFeedsNode.Control.ActiveNode = CurrentSelectedFeedsNode;
                                        if (NumSelectedListViewItems == 0)
                                        {
                                            OnTreeFeedAfterSelectManually(CurrentSelectedFeedsNode);
                                        }
                                    }
                                }
                            });
        }
 
        private  void OnFeedDeleted(object sender, FeedDeletedEventArgs e)
        {
            TreeFeedsNodeBase tn = CurrentSelectedFeedsNode;
            ExceptionNode.UpdateReadStatus();
            PopulateSmartFolder((TreeFeedsNodeBase) ExceptionNode, false);
            if (tn == null || tn.Type != FeedNodeType.Feed || e.FeedUrl != tn.DataKey)
            {
                tn = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), e.FeedUrl);
            }
            if (tn == null || tn.Type != FeedNodeType.Feed || e.FeedUrl != tn.DataKey)
            {
                return;
            }
            UpdateTreeNodeUnreadStatus(tn, 0);
            UnreadItemsNodeRemoveItems(e.FeedUrl);
            if (tn.Selected)
            {
                EmptyListView();
                htmlDetail.Clear();
                TreeSelectedFeedsNode = TreeHelper.GetNewNodeToActivate(tn);
                RefreshFeedDisplay(TreeSelectedFeedsNode, true);
            }
            try
            {
                tn.DataKey = null;
                tn.Parent.Nodes.Remove(tn);
                DelayTask(DelayedTasks.SyncRssSearchTree);
            }
            catch
            {
            }
        }
 
        private  void OnTasksTimerTick(object sender, EventArgs e)
        {
            if (_uiTasksTimer[DelayedTasks.SyncRssSearchTree])
            {
                _uiTasksTimer.StopTask(DelayedTasks.SyncRssSearchTree);
                PopulateTreeRssSearchScope();
            }
            if (_uiTasksTimer[DelayedTasks.RefreshTreeUnreadStatus])
            {
                _uiTasksTimer.StopTask(DelayedTasks.RefreshTreeUnreadStatus);
                object[] param = (object[]) _uiTasksTimer.GetData(DelayedTasks.RefreshTreeUnreadStatus, true);
                TreeFeedsNodeBase tn = (TreeFeedsNodeBase) param[0];
                int counter = (int) param[1];
                if (tn != null)
                    UpdateTreeNodeUnreadStatus(tn, counter);
            }
            if (_uiTasksTimer[DelayedTasks.RefreshTreeCommentStatus])
            {
                _uiTasksTimer.StopTask(DelayedTasks.RefreshTreeCommentStatus);
                object[] param = (object[]) _uiTasksTimer.GetData(DelayedTasks.RefreshTreeCommentStatus, true);
                TreeFeedsNodeBase tn = (TreeFeedsNodeBase) param[0];
                IList<NewsItem> items = (IList<NewsItem>) param[1];
                bool commentsRead = (bool) param[2];
                if (tn != null)
                    UpdateCommentStatus(tn, items, commentsRead);
            }
            if (_uiTasksTimer[DelayedTasks.NavigateToWebUrl])
            {
                _uiTasksTimer.StopTask(DelayedTasks.NavigateToWebUrl);
                object[] param = (object[]) _uiTasksTimer.GetData(DelayedTasks.NavigateToWebUrl, true);
                DetailTabNavigateToUrl((string) param[0], (string) param[1], (bool) param[2], (bool) param[3]);
            }
            if (_uiTasksTimer[DelayedTasks.StartRefreshOneFeed])
            {
                _uiTasksTimer.StopTask(DelayedTasks.StartRefreshOneFeed);
                string feedUrl = (string) _uiTasksTimer.GetData(DelayedTasks.StartRefreshOneFeed, true);
                owner.FeedHandler.AsyncGetItemsForFeed(feedUrl, true, true);
            }
            if (_uiTasksTimer[DelayedTasks.SaveUIConfiguration])
            {
                _uiTasksTimer.StopTask(DelayedTasks.SaveUIConfiguration);
                SaveUIConfiguration(true);
            }
            if (_uiTasksTimer[DelayedTasks.ShowFeedPropertiesDialog])
            {
                _uiTasksTimer.StopTask(DelayedTasks.ShowFeedPropertiesDialog);
                NewsFeed f = (NewsFeed) _uiTasksTimer.GetData(DelayedTasks.ShowFeedPropertiesDialog, true);
                DisplayFeedProperties(f);
            }
            if (_uiTasksTimer[DelayedTasks.NavigateToFeedNewsItem])
            {
                _uiTasksTimer.StopTask(DelayedTasks.NavigateToFeedNewsItem);
                NewsItem item = (NewsItem) _uiTasksTimer.GetData(DelayedTasks.NavigateToFeedNewsItem, true);
                NavigateToFeedNewsItem(item);
            }
            if (_uiTasksTimer[DelayedTasks.NavigateToFeed])
            {
                _uiTasksTimer.StopTask(DelayedTasks.NavigateToFeed);
                NewsFeed f = (NewsFeed) _uiTasksTimer.GetData(DelayedTasks.NavigateToFeed, true);
                NavigateToFeed(f);
            }
            if (_uiTasksTimer[DelayedTasks.AutoSubscribeFeedUrl])
            {
                _uiTasksTimer.StopTask(DelayedTasks.AutoSubscribeFeedUrl);
                object[] parameter = (object[]) _uiTasksTimer.GetData(DelayedTasks.AutoSubscribeFeedUrl, true);
                AutoSubscribeFeed((TreeFeedsNodeBase) parameter[0], (string) parameter[1]);
            }
            if (_uiTasksTimer[DelayedTasks.ClearBrowserStatusInfo])
            {
                _uiTasksTimer.StopTask(DelayedTasks.ClearBrowserStatusInfo);
                SetBrowserStatusBarText(String.Empty);
                DeactivateWebProgressInfo();
                _uiTasksTimer.Interval = 100;
            }
            if (_uiTasksTimer[DelayedTasks.InitOnFinishLoading])
            {
                _uiTasksTimer.StopTask(DelayedTasks.InitOnFinishLoading);
                OnFinishLoading();
            }
            if (!_uiTasksTimer.AllTaskDone)
            {
                if (!_uiTasksTimer.Enabled)
                    _uiTasksTimer.Start();
            }
            else
            {
                if (_uiTasksTimer.Enabled)
                    _uiTasksTimer.Stop();
            }
        }
 
        private  void OnTreeNodeFeedsRootReadCounterZero(object sender, EventArgs e)
        {
            ResetFindersReadStatus();
            SetGuiStateFeedback(String.Empty, ApplicationTrayState.NormalIdle);
        }
 
        public  void DelayTask(DelayedTasks task)
        {
            DelayTask(task, null, 100);
        }
 
        public  void DelayTask(DelayedTasks task, object data)
        {
            DelayTask(task, data, 100);
        }
 
        public  void DelayTask(DelayedTasks task, object data, int interval)
        {
            GuiInvoker.InvokeAsync(this, delegate
            {
                _uiTasksTimer.SetData(task, data);
                if (_uiTasksTimer.Interval != interval)
                    _uiTasksTimer.Interval = interval;
                _uiTasksTimer.StartTask(task);
            });
        }
 
        public  void StopTask(DelayedTasks task)
        {
            _uiTasksTimer.StopTask(task);
        }
 
        private class  UITaskTimer  : Timer {
			
            private readonly  object SynRoot = new object();
 
            private  DelayedTasks tasks;
 
            private readonly  Dictionary<DelayedTasks, object> taskData = new Dictionary<DelayedTasks, object>(7);
 
            public  UITaskTimer(IContainer component) : base(component)
            {
                base.Enabled = true;
            }
 
            public  bool this[DelayedTasks task]
            {
                get
                {
                    lock (SynRoot)
                    {
                        if ((tasks & task) == task)
                            return true;
                        return false;
                    }
                }
                set
                {
                    lock (SynRoot)
                    {
                        if (value)
                            tasks |= task;
                        else
                            tasks ^= task;
                    }
                }
            }
 
            public  void StartTask(DelayedTasks task)
            {
                lock (SynRoot)
                {
                    this[task] = true;
                    Stop();
                    Start();
                }
            }
 
            public  void StopTask(DelayedTasks task)
            {
                lock (SynRoot)
                {
                    this[task] = false;
                    if (AllTaskDone && base.Enabled)
                        Stop();
                }
            }
 
            public  bool AllTaskDone
            {
                get
                {
                    lock (SynRoot)
                    {
                        return (tasks == DelayedTasks.None);
                    }
                }
            }
 
            public  object GetData(DelayedTasks task, bool clear)
            {
                object data = null;
                lock (SynRoot)
                {
                    if (taskData.ContainsKey(task))
                    {
                        data = taskData[task];
                        if (clear)
                            taskData.Remove(task);
                    }
                }
                return data;
            }
 
            public  void SetData(DelayedTasks task, object data)
            {
                lock (SynRoot)
                {
                    if (taskData.ContainsKey(task))
                        taskData.Remove(task);
                    taskData.Add(task, data);
                }
            }

		}
		
        public  CommandBar GetToolBarInstance(string id)
        {
            throw new NotImplementedException();
        }
 
        public  CommandBar GetMenuBarInstance(string id)
        {
            throw new NotImplementedException();
        }
 
        public  CommandBar AddToolBar(string id)
        {
            throw new NotImplementedException();
        }
 
        public  CommandBar AddMenuBar(string id)
        {
            throw new NotImplementedException();
        }
 
        public  CommandBar GetContexMenuInstance(string id)
        {
            throw new NotImplementedException();
        }
 
        public  CommandBar AddContextMenu(string id)
        {
            throw new NotImplementedException();
        }
 
        private  void OnNavigatorCollapseClick(object sender, EventArgs e)
        {
            splitterNavigator.Hide();
            Navigator.Hide();
            pNavigatorCollapsed.Show();
            owner.Mediator.SetChecked("-cmdToggleTreeViewState");
            owner.Mediator.SetChecked("-cmdToggleRssSearchTabState");
        }
 
        private  void OnNavigatorExpandImageClick(object sender, EventArgs e)
        {
            Navigator.Show();
            pNavigatorCollapsed.Hide();
            splitterNavigator.Show();
            OnNavigatorGroupClick(null, null);
        }
 
        private  void OnNavigatorGroupClick(object sender, GroupEventArgs e)
        {
            if (Navigator.Visible)
            {
                if (Navigator.SelectedGroup.Key == Resource.NavigatorGroup.Subscriptions)
                {
                    owner.Mediator.SetChecked("+cmdToggleTreeViewState");
                    owner.Mediator.SetChecked("-cmdToggleRssSearchTabState");
                }
                else if (Navigator.SelectedGroup.Key == Resource.NavigatorGroup.RssSearch)
                {
                    owner.Mediator.SetChecked("-cmdToggleTreeViewState");
                    owner.Mediator.SetChecked("+cmdToggleRssSearchTabState");
                }
            }
        }
 
        private  void OnListFeedItemsO_AfterSelect(object sender, SelectEventArgs e)
        {
            if (listFeedItemsO.IsUpdatingSelection)
                return;
            listFeedItemsO.IsUpdatingSelection = true;
            ArrayList groupSelected = null;
            listFeedItems.SelectedItems.Clear();
            for (int i = 0; i < listFeedItemsO.Nodes.Count; i++)
            {
                if (listFeedItemsO.Nodes[i].Selected)
                {
                    if (groupSelected == null)
                        groupSelected = new ArrayList();
                    for (int j = 0; j < listFeedItemsO.Nodes[i].Nodes.Count; j++)
                    {
                        UltraTreeNodeExtended node = (UltraTreeNodeExtended) listFeedItemsO.Nodes[i].Nodes[j];
                        if (node.NewsItem != null && !node.NewsItem.BeenRead)
                        {
                            groupSelected.Add(node.NewsItem);
                        }
                    }
                }
                for (int j = 0; j < listFeedItemsO.Nodes[i].Nodes.Count; j++)
                {
                    if (listFeedItemsO.Nodes[i].Nodes[j].Selected)
                    {
                        UltraTreeNodeExtended node = (UltraTreeNodeExtended) listFeedItemsO.Nodes[i].Nodes[j];
                        if (node.NodeOwner != null)
                            node.NodeOwner.Selected = true;
                    }
                    for (int k = 0; k < listFeedItemsO.Nodes[i].Nodes[j].Nodes.Count; k++)
                    {
                        if (listFeedItemsO.Nodes[i].Nodes[j].Nodes[k].Selected)
                        {
                            UltraTreeNodeExtended node =
                                (UltraTreeNodeExtended) listFeedItemsO.Nodes[i].Nodes[j].Nodes[k];
                            if (node.NodeOwner != null)
                            {
                                node.NodeOwner.Selected = true;
                            }
                        }
                    }
                }
            }
            listFeedItemsO.IsUpdatingSelection = false;
            if (groupSelected == null)
            {
                OnFeedListItemActivate(null, EventArgs.Empty);
            }
            else
            {
                TreeFeedsNodeBase tn = TreeSelectedFeedsNode;
                if (tn != null)
                {
                    if (tn.Type == FeedNodeType.Category)
                    {
                        string category = tn.CategoryStoreName;
                        Hashtable temp = new Hashtable();
                        foreach (NewsItem item in groupSelected)
                        {
                            FeedInfo fi;
                            if (temp.ContainsKey(item.Feed.link))
                            {
                                fi = (FeedInfo) temp[item.Feed.link];
                            }
                            else
                            {
                                fi = (FeedInfo) item.FeedDetails.Clone();
                                fi.ItemsList.Clear();
                                temp.Add(item.Feed.link, fi);
                            }
                            fi.ItemsList.Add(item);
                        }
                        FeedInfoList redispItems = new FeedInfoList(category);
                        foreach (FeedInfo fi in temp.Values)
                        {
                            if (fi.ItemsList.Count > 0)
                                redispItems.Add(fi);
                        }
                        BeginTransformFeedList(redispItems, tn, owner.FeedHandler.GetCategoryStyleSheet(category));
                    }
                    else
                    {
                        string feedUrl = tn.DataKey;
                        IFeedDetails fi = owner.FeedHandler.GetFeedInfo(feedUrl);
                        if (fi != null)
                        {
                            FeedInfo fi2;
                            fi2 = (FeedInfo) fi.Clone();
                            fi2.ItemsList.Clear();
                            foreach (NewsItem ni in groupSelected)
                            {
                                fi2.ItemsList.Add(ni);
                            }
                            BeginTransformFeed(fi2, tn, owner.FeedHandler.GetStyleSheet(tn.DataKey));
                        }
                    }
                }
            }
        }
 
        private  void listFeedItems_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (listFeedItemsO.IsUpdatingSelection)
                return;
            if (listFeedItemsO.Visible)
            {
                listFeedItemsO.IsUpdatingSelection = true;
                listFeedItemsO.SelectedNodes.Clear();
                for (int i = 0; i < listFeedItems.Items.Count; i++)
                {
                    if (listFeedItems.Items[i].Selected)
                    {
                        UltraTreeNodeExtended n = listFeedItemsO.GetFromLVI(listFeedItems.Items[i]);
                        if (n != null)
                        {
                            n.Selected = true;
                        }
                    }
                }
                listFeedItemsO.IsUpdatingSelection = false;
            }
        }
 
        private  void OnListFeedItemsO_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
            {
                if (listFeedItemsO.SelectedNodes.Count == 0)
                    return;
                UltraTreeNodeExtended n = (UltraTreeNodeExtended) listFeedItemsO.SelectedNodes[0];
                if (n.Level == 1 && n.NewsItem != null)
                {
                    DetailTabNavigateToUrl(n.NewsItem.Link, null, true, true);
                }
            }
        }
 
        private  void listFeedItemsO_BeforeExpand(object sender, CancelableNodeEventArgs e)
        {
            UltraTreeNodeExtended n = (UltraTreeNodeExtended) e.TreeNode;
            if (n.Level == 1)
            {
                if (n.NewsItem != null && n.NewsItem.CommentCount > 0)
                {
                    ThreadedListViewItem lvi = n.NodeOwner;
                    lvi.Expanded = true;
                    listFeedItemsO.AddCommentUpdating(lvi);
                }
            }
        }
 
        private  void listFeedItemsO_MouseDown(object sender, MouseEventArgs e)
        {
            UltraTreeNodeExtended n = (UltraTreeNodeExtended) listFeedItemsO.GetNodeFromPoint(e.X, e.Y);
            if (e.Button == MouseButtons.Left)
            {
                if (n != null)
                {
                    if (!n.CollapseRectangle.IsEmpty && n.CollapseRectangle.Contains(e.X, e.Y))
                    {
                        n.Expanded = !n.Expanded;
                    }
                    if (!n.EnclosureRectangle.IsEmpty && n.EnclosureRectangle.Contains(e.X, e.Y))
                    {
                    }
                    if (!n.CommentsRectangle.IsEmpty && n.CommentsRectangle.Contains(e.X, e.Y))
                    {
                        if ((n.NewsItem != null) && (!string.IsNullOrEmpty(n.NewsItem.CommentRssUrl)))
                        {
                            n.Expanded = !n.Expanded;
                            listFeedItemsO_BeforeExpand(sender, new CancelableNodeEventArgs(n));
                        }
                    }
                    if (!n.FlagRectangle.IsEmpty && n.FlagRectangle.Contains(e.X, e.Y))
                    {
                        if (n.NewsItem != null)
                        {
                            ToggleItemFlagState(n.NewsItem.Id);
                        }
                    }
                }
                else
                {
                }
                if (listFeedItemsO.ItemsCount() <= 0)
                    return;
                if (n != null && e.Clicks > 1)
                {
                    NewsItem item = CurrentSelectedFeedItem = n.NewsItem;
                    n.Selected = true;
                    if (item != null && !string.IsNullOrEmpty(item.Link))
                    {
                        if (!UrlRequestHandledExternally(item.Link, false))
                            DetailTabNavigateToUrl(item.Link, null, false, true);
                    }
                }
            }
            else if (e.Button == MouseButtons.Right)
            {
                if (n != null)
                {
                    n.Selected = true;
                    listFeedItemsO.ActiveNode = n;
                }
                RefreshListviewContextMenu();
                if (n != null && n.NodeOwner != null)
                    OnFeedListItemActivateManually(n.NodeOwner);
            }
        }
 
        private  void listFeedItemsO_AfterSortChange(object sender, AfterSortChangeEventArgs e)
        {
            UltraTreeNode n = listFeedItemsO.ActiveNode;
            if (n == null)
                n = listFeedItemsO.TopNode;
            if (n != null)
                n.BringIntoView(true);
        }
 
        private  void OnMediatorBeforeCommandStateChanged(object sender, EventArgs e)
        {
            ultraToolbarsManager.EventManager.AllEventsEnabled = false;
        }
 
        private  void OnMediatorAfterCommandStateChanged(object sender, EventArgs e)
        {
            ultraToolbarsManager.EventManager.AllEventsEnabled = true;
        }
 
        internal  void SetSubscriptionNodeState(INewsFeed f, TreeFeedsNodeBase feedsNode, FeedProcessingState state)
        {
            if (f == null || feedsNode == null) return;
            if (RssHelper.IsNntpUrl(f.link))
            {
                SetNntpNodeState(f, feedsNode, state);
            }
            else
            {
                SetFeedNodeState(f, feedsNode, state);
            }
        } 
        private static  void SetNntpNodeState(INewsFeed f, TreeFeedsNodeBase feedsNode, FeedProcessingState state)
        {
            if (f == null || feedsNode == null) return;
            switch (state)
            {
                case FeedProcessingState.Normal:
                    if (f.refreshrateSpecified && f.refreshrate <= 0)
                    {
                        feedsNode.Override.NodeAppearance.Image = Resource.SubscriptionTreeImage.NntpDisabled;
                        feedsNode.Override.SelectedNodeAppearance.Image =
                            Resource.SubscriptionTreeImage.NntpDisabledSelected;
                    }
                    else if (f.authUser != null || f.link.StartsWith(NntpWebRequest.NntpsUriScheme))
                    {
                        feedsNode.Override.NodeAppearance.Image = Resource.SubscriptionTreeImage.NntpSecured;
                        feedsNode.Override.SelectedNodeAppearance.Image =
                            Resource.SubscriptionTreeImage.NntpSecuredSelected;
                    }
                    else
                    {
                        feedsNode.Override.NodeAppearance.Image = Resource.SubscriptionTreeImage.Nntp;
                        feedsNode.Override.SelectedNodeAppearance.Image = Resource.SubscriptionTreeImage.NntpSelected;
                    }
                    break;
                case FeedProcessingState.Failure:
                    feedsNode.Override.NodeAppearance.Image = Resource.SubscriptionTreeImage.NntpFailure;
                    feedsNode.Override.SelectedNodeAppearance.Image = Resource.SubscriptionTreeImage.NntpFailureSelected;
                    break;
                case FeedProcessingState.Updating:
                    feedsNode.Override.NodeAppearance.Image = Resource.SubscriptionTreeImage.NntpUpdating;
                    feedsNode.Override.SelectedNodeAppearance.Image =
                        Resource.SubscriptionTreeImage.NntpUpdatingSelected;
                    break;
                default:
                    Trace.WriteLine("Unhandled/unknown FeedProcessingState: " + state);
                    break;
            }
        } 
        private  void SetFeedNodeState(INewsFeed f, TreeFeedsNodeBase feedsNode, FeedProcessingState state)
        {
            if (f == null || feedsNode == null) return;
            switch (state)
            {
                case FeedProcessingState.Normal:
                    if (!string.IsNullOrEmpty(f.favicon) && feedsNode.HasCustomIcon && owner.Preferences.UseFavicons)
                    {
                        feedsNode.SetIndividualImage(null);
                    }
                    else if (f.refreshrateSpecified && f.refreshrate <= 0)
                    {
                        feedsNode.Override.NodeAppearance.Image = Resource.SubscriptionTreeImage.FeedDisabled;
                        feedsNode.Override.SelectedNodeAppearance.Image =
                            Resource.SubscriptionTreeImage.FeedDisabledSelected;
                    }
                    else if (f.authUser != null || f.link.StartsWith("https"))
                    {
                        feedsNode.Override.NodeAppearance.Image = Resource.SubscriptionTreeImage.FeedSecured;
                        feedsNode.Override.SelectedNodeAppearance.Image =
                            Resource.SubscriptionTreeImage.FeedSecuredSelected;
                    }
                    else
                    {
                        feedsNode.Override.NodeAppearance.Image = Resource.SubscriptionTreeImage.Feed;
                        feedsNode.Override.SelectedNodeAppearance.Image = Resource.SubscriptionTreeImage.FeedSelected;
                    }
                    break;
                case FeedProcessingState.Failure:
                    feedsNode.Override.NodeAppearance.Image = Resource.SubscriptionTreeImage.FeedFailure;
                    feedsNode.Override.SelectedNodeAppearance.Image = Resource.SubscriptionTreeImage.FeedFailureSelected;
                    break;
                case FeedProcessingState.Updating:
                    feedsNode.Override.NodeAppearance.Image = Resource.SubscriptionTreeImage.FeedUpdating;
                    feedsNode.Override.SelectedNodeAppearance.Image =
                        Resource.SubscriptionTreeImage.FeedUpdatingSelected;
                    break;
                default:
                    Trace.WriteLine("Unhandled/unknown FeedProcessingState: " + state);
                    break;
            }
        } 
        private  void UpdateCommentStatus(TreeFeedsNodeBase tn, INewsFeed f)
        {
            IList<NewsItem> itemsWithNewComments = GetFeedItemsWithNewComments(f);
            tn.UpdateCommentStatus(tn, itemsWithNewComments.Count);
            owner.UpdateWatchedItems(itemsWithNewComments);
            WatchedItemsNode.UpdateCommentStatus();
        } 
        private  void UnreadItemsNodeRemoveItems(INewsFeed f)
        {
            if (f == null) return;
            UnreadItemsNodeRemoveItems(FilterUnreadFeedItems(f));
        } 
        private  IList<NewsItem> FilterUnreadFeedItems(INewsFeed f)
        {
            List<NewsItem> result = new List<NewsItem>();
            if (f == null)
                return result;
            if (f.containsNewMessages)
            {
                IList<NewsItem> items = null;
                try
                {
                    items = owner.FeedHandler.GetCachedItemsForFeed(f.link);
                }
                catch
                {
                }
                return FilterUnreadFeedItems(items);
            }
            return result;
        } 
        private  IList<NewsItem> GetFeedItemsWithNewComments(INewsFeed f)
        {
            List<NewsItem> itemsWithNewComments = new List<NewsItem>();
            if (f == null) return itemsWithNewComments;
            if (f.containsNewComments)
            {
                IList<NewsItem> items = null;
                try
                {
                    items = owner.FeedHandler.GetCachedItemsForFeed(f.link);
                }
                catch
                {
                }
                if (items == null) return itemsWithNewComments;
                for (int i = 0; i < items.Count; i++)
                {
                    NewsItem item = items[i];
                    if (item.HasNewComments) itemsWithNewComments.Add(item);
                }
            }
            return itemsWithNewComments;
        } 
        public  void PopulateFeedSubscriptions(ICollection<INewsFeedCategory> categories, IDictionary<string, INewsFeed> feedsTable,
                                              string defaultCategory)
        {
            EmptyListView();
            TreeFeedsNodeBase root = GetRoot(RootFolderType.MyFeeds);
            try
            {
                treeFeeds.BeginUpdate();
                TreeFeedsNodeBase tn;
                root.Nodes.Clear();
                UpdateTreeNodeUnreadStatus(root, 0);
                UnreadItemsNode.Items.Clear();
                Hashtable categoryTable = new Hashtable();
                List<INewsFeedCategory> categoryList = new List<INewsFeedCategory>(categories);
                foreach (NewsFeed f in feedsTable.Values)
                {
                    if (Disposing)
                        return;
                    if (RssHelper.IsNntpUrl(f.link))
                    {
                        tn = new FeedNode(f.title, Resource.SubscriptionTreeImage.Nntp,
                                          Resource.SubscriptionTreeImage.NntpSelected,
                                          _treeFeedContextMenu);
                    }
                    else
                    {
                        tn = new FeedNode(f.title, Resource.SubscriptionTreeImage.Feed,
                                          Resource.SubscriptionTreeImage.FeedSelected,
                                          _treeFeedContextMenu,
                                          (owner.Preferences.UseFavicons ? LoadFavicon(f.favicon) : null));
                    }
                    tn.DataKey = f.link;
                    f.Tag = tn;
                    string category = (f.category ?? String.Empty);
                    TreeFeedsNodeBase catnode;
                    if (categoryTable.ContainsKey(category))
                        catnode = (TreeFeedsNodeBase)categoryTable[category];
                    else
                    {
                        catnode = TreeHelper.CreateCategoryHive(root, category, _treeCategoryContextMenu);
                        categoryTable.Add(category, catnode);
                    }
                    catnode.Nodes.Add(tn);
                    SetSubscriptionNodeState(f, tn, FeedProcessingState.Normal);
                    if (f.containsNewMessages)
                    {
                        IList<NewsItem> unread = FilterUnreadFeedItems(f);
                        if (unread.Count > 0)
                        {
                            UpdateTreeNodeUnreadStatus(tn, unread.Count);
                            UnreadItemsNode.Items.AddRange(unread);
                            UnreadItemsNode.UpdateReadStatus();
                        }
                    }
                    if (f.containsNewComments)
                    {
                        UpdateCommentStatus(tn, f);
                    }
                    for (int i = 0; i < categoryList.Count; i++)
                    {
                        if (categoryList[i].Value.Equals(category))
                        {
                            categoryList.RemoveAt(i);
                            break;
                        }
                    }
                }
                foreach (category c in categoryList)
                {
                    TreeHelper.CreateCategoryHive(root, c.Value, _treeCategoryContextMenu);
                }
            }
            finally
            {
                treeFeeds.EndUpdate();
            }
            if (Disposing)
                return;
            TreeSelectedFeedsNode = root;
            if (! IsTreeStateAvailable())
                root.Expanded = true;
            DelayTask(DelayedTasks.SyncRssSearchTree);
            SetGuiStateFeedback(String.Empty, ApplicationTrayState.NormalIdle);
            _faviconsDownloaded = false;
        } 
        public  void AddNewFeedNode(string category, INewsFeed f)
        {
            TreeFeedsNodeBase catnode;
            TreeFeedsNodeBase tn;
            if (RssHelper.IsNntpUrl(f.link))
            {
                tn = new FeedNode(f.title, Resource.SubscriptionTreeImage.Nntp,
                                  Resource.SubscriptionTreeImage.NntpSelected,
                                  _treeFeedContextMenu);
            }
            else
            {
                tn = new FeedNode(f.title, Resource.SubscriptionTreeImage.Feed,
                                  Resource.SubscriptionTreeImage.FeedSelected,
                                  _treeFeedContextMenu,
                                  (owner.Preferences.UseFavicons ? LoadFavicon(f.favicon) : null));
            }
            tn.DataKey = f.link;
            f.Tag = tn;
            SetSubscriptionNodeState(f, tn, FeedProcessingState.Normal);
            category = (f.category == RssBanditApplication.DefaultCategory ? null : f.category);
            catnode = TreeHelper.CreateCategoryHive(GetRoot(RootFolderType.MyFeeds), category, _treeCategoryContextMenu);
            if (catnode == null)
                catnode = GetRoot(RootFolderType.MyFeeds);
            catnode.Nodes.Add(tn);
            if (f.containsNewMessages)
            {
                IList<NewsItem> unread = FilterUnreadFeedItems(f);
                if (unread.Count > 0)
                {
                    UpdateTreeNodeUnreadStatus(tn, unread.Count);
                    UnreadItemsNode.Items.AddRange(unread);
                    UnreadItemsNode.UpdateReadStatus();
                }
            }
            if (f.containsNewComments)
            {
                UpdateCommentStatus(tn, f);
            }
            tn.BringIntoView();
            DelayTask(DelayedTasks.SyncRssSearchTree);
        } 
        private  void UpdateTreeStatus(IDictionary<string, INewsFeed> feedsTable)
        {
            UpdateTreeStatus(feedsTable, RootFolderType.MyFeeds);
        } 
        private  void UpdateTreeStatus(IDictionary<string, INewsFeed> feedsTable, RootFolderType rootFolder)
        {
            if (feedsTable == null) return;
            if (feedsTable.Count == 0) return;
            TreeFeedsNodeBase root = GetRoot(rootFolder);
            if (root == null)
                return;
            foreach (NewsFeed f in feedsTable.Values)
            {
                TreeFeedsNodeBase tn = TreeHelper.FindNode(root, f);
                if (f.containsNewMessages)
                {
                    UpdateTreeNodeUnreadStatus(tn, CountUnreadFeedItems(f));
                }
                else
                {
                    UpdateTreeNodeUnreadStatus(tn, 0);
                }
                Application.DoEvents();
            }
        } 
        private  void OnExternalDisplayFeedProperties(INewsFeed f)
        {
            DelayTask(DelayedTasks.ShowFeedPropertiesDialog, f);
        } 
        private  void OnExternalActivateFeed(INewsFeed f)
        {
            DelayTask(DelayedTasks.NavigateToFeed, f);
        } 
        private  void DisplayFeedProperties(INewsFeed f)
        {
            TreeFeedsNodeBase tn = TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), f);
            if (tn != null)
            {
                CurrentSelectedFeedsNode = tn;
                owner.CmdShowFeedProperties(null);
                CurrentSelectedFeedsNode = null;
            }
        } 
        internal  void NavigateToFeed(INewsFeed f)
        {
            NavigateToNode(TreeHelper.FindNode(GetRoot(RootFolderType.MyFeeds), f));
        }
	}

}
