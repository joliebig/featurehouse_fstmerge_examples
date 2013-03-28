using System; 
using System.Collections; 
using System.Diagnostics; 
using System.Windows.Forms; 
using Infragistics.Win; 
using Infragistics.Win.UltraWinToolbars; 
using NewsComponents.Collections; 
using NewsComponents.Utils; 
using RssBandit.Common; 
using RssBandit.Common.Logging; 
using RssBandit.WinGui.Tools; 
using RssBandit.WinGui.Utility; 
using RssBandit.WinGui.Interfaces; 
using RssBandit.WinGui.Forms; 
using NewsComponents.Threading; 
using Appearance=Infragistics.Win.Appearance; namespace  RssBandit.WinGui {
	
 public class  AutoDiscoveredFeedsMenuHandler {
		
  public delegate  void  AddAutoDiscoveredFeedCallback (DiscoveredFeedsInfo info);
		
  private  RssBanditApplication app = null;
 
  internal  CommandMediator mediator = null;
 
  internal  AppPopupMenuCommand itemDropdown = null;
 
  private  AppButtonToolCommand clearListButton = null;
 
  private  Infragistics.Win.Appearance[] discoveredAppearance;
 
  internal  Hashtable discoveredFeeds = null;
 
  internal  Queue newDiscoveredFeeds = null;
 
  private  Hashtable discoveredItems = null;
 
  private  Queue newItems = null;
 
  private  PriorityThread worker;
 
  private  int workerPriorityCounter = 0;
 
  internal static  long cmdKeyPostfix = 0;
 
  private  AutoDiscoveredFeedsMenuHandler() {
   worker = new PriorityThread();
   newItems = new Queue(1);
   discoveredItems = new Hashtable(11);
   newDiscoveredFeeds = new Queue(1);
   discoveredFeeds = new Hashtable(9);
   mediator = new CommandMediator();
  }
 
  internal  AutoDiscoveredFeedsMenuHandler(RssBanditApplication app):this()
  {
   this.app = app;
  }
 
  public delegate  void  DiscoveredFeedsSubscribeCallback (object sender, DiscoveredFeedsSubscribeCancelEventArgs e);
		
  public  event DiscoveredFeedsSubscribeCallback OnDiscoveredFeedsSubscribe; 
  internal  void SetControls (AppPopupMenuCommand dropDown, AppButtonToolCommand clearList)
  {
   this.itemDropdown = dropDown;
   this.clearListButton = clearList;
   this.discoveredAppearance = new Appearance[4];
   this.discoveredAppearance[0] = new Infragistics.Win.Appearance();
   this.discoveredAppearance[0].Image = Properties.Resources.no_feed_discovered_16;
   this.discoveredAppearance[1] = new Infragistics.Win.Appearance();
   this.discoveredAppearance[1].Image = Properties.Resources.no_feed_discovered_32;
   this.discoveredAppearance[2] = new Infragistics.Win.Appearance();
   this.discoveredAppearance[2].Image = Properties.Resources.feed_discovered_16;
   this.discoveredAppearance[3] = new Infragistics.Win.Appearance();
   this.discoveredAppearance[3].Image = Properties.Resources.feed_discovered_32;
   Reset();
   this.itemDropdown.ToolbarsManager.ToolClick -= OnToolbarsManager_ToolClick;
   this.itemDropdown.ToolbarsManager.ToolClick += OnToolbarsManager_ToolClick;
  }
 
  internal  void Reset() {
   this.itemDropdown.Tools.Clear();
   this.itemDropdown.Tools.Add(this.clearListButton);
   this.itemDropdown.Tools["cmdDiscoveredFeedsListClear"].InstanceProps.IsFirstInGroup = true;
   this.itemDropdown.SharedProps.AppearancesSmall.Appearance = discoveredAppearance[0];
   this.itemDropdown.SharedProps.AppearancesLarge.Appearance = discoveredAppearance[1];
  }
 
  public  void DiscoverFeedInContent(string htmlContent, string pageUrl, string pageTitle) {
   if (workerPriorityCounter == Int32.MaxValue )
    workerPriorityCounter = 0;
   if (string.IsNullOrEmpty(pageTitle)) {
    string def = pageUrl;
    try { def = new Uri(pageUrl).Host; } catch (UriFormatException) { }
    pageTitle = HtmlHelper.FindTitle(htmlContent, def);
   }
   CallbackState state = new CallbackState(htmlContent, pageUrl, pageTitle);
   worker.QueueUserWorkItem(this.ThreadRun, state, ++workerPriorityCounter);
  }
 
  public  void Add(DiscoveredFeedsInfo info) {
   if (info == null)
    return;
   AppButtonToolCommand duplicateItem = FindYetDiscoveredFeedMenuItem(info);
   if (duplicateItem != null) {
    duplicateItem.SharedProps.Caption = StripAndShorten(info.Title);
    lock(discoveredFeeds) {
     discoveredFeeds.Remove(duplicateItem);
    }
   } else {
    WinGuiMain guiMain = (WinGuiMain)app.MainForm;
    GuiInvoker.InvokeAsync(guiMain, delegate
                {
     guiMain.AddAutoDiscoveredUrl(info);
    });
   }
   RefreshDiscoveredItemContainer();
  }
 
  internal  string StripAndShorten(string s) {
   return Utilities.StripMnemonics(StringHelper.ShortenByEllipsis(s, 40));
  }
 
  private  void ThreadRun(object state) {
   CallbackState cbs = (CallbackState)state;
   this.AsyncDiscoverFeedsInContent(cbs.HtmlContent, cbs.PageUrl, cbs.PageTitle);
  }
 
  private  void AsyncDiscoverFeedsInContent(string htmlContent, string pageUrl, string pageTitle) {
   if (string.IsNullOrEmpty( pageUrl) )
    return;
   string baseUrl = GetBaseUrlOf(pageUrl);
   AppButtonToolCommand foundItem = null;
   lock(discoveredFeeds) {
    foreach (AppButtonToolCommand item in discoveredFeeds.Keys) {
     string url = ((DiscoveredFeedsInfo)discoveredFeeds[item]).SiteBaseUrl;
     if (0 == String.Compare(baseUrl, url, true)) {
      foundItem = item;
     }
    }
   }
   if (foundItem != null) {
    foundItem.SharedProps.Caption = StripAndShorten(pageTitle);
    lock(newItems) {
     newItems.Enqueue(foundItem);
    }
   } else {
    NewsComponents.Feed.RssLocater locator = new NewsComponents.Feed.RssLocater(Proxy, RssBanditApplication.UserAgent);
    ArrayList feeds = null;
    try {
     feeds = locator.GetRssFeedsForUrlContent(pageUrl, htmlContent, false);
    } catch (Exception){
    }
    if (feeds != null && feeds.Count > 0) {
     feeds = this.CheckAndRemoveSubscribedFeeds(feeds, baseUrl);
    }
    if (feeds != null && feeds.Count > 0) {
     DiscoveredFeedsInfo info = new DiscoveredFeedsInfo(feeds, pageTitle, baseUrl);
     this.Add(info);
     return;
    }
   }
   RefreshDiscoveredItemContainer();
  }
 
  private  string GetBaseUrlOf(string pageUrl) {
   Uri uri = null;
   try {
    uri = new Uri(pageUrl);
   } catch {}
   if (uri == null)
    return pageUrl;
   string leftPart = uri.GetLeftPart(UriPartial.Path);
   return leftPart.Substring(0, leftPart.LastIndexOf("/"));
  }
 
  internal  void CmdClearFeedsList(ICommand sender) {
   this.itemDropdown.Tools.Clear();
   this.discoveredFeeds.Clear();
   this.itemDropdown.Tools.Add(clearListButton);
   this.itemDropdown.Tools["cmdDiscoveredFeedsListClear"].InstanceProps.IsFirstInGroup = true;
   RefreshDiscoveredItemContainer();
  }
 
  internal  void OnDiscoveredItemClick(ICommand sender) {
   AppButtonToolCommand itemClicked = sender as AppButtonToolCommand;
   Debug.Assert(itemClicked != null);
   DiscoveredFeedsInfo info = discoveredFeeds[itemClicked] as DiscoveredFeedsInfo;
   Debug.Assert(info != null);
   bool cancel = this.RaiseOnDiscoveredFeedsSubscribe(info);
   if (! cancel) {
    foreach (AppButtonToolCommand item in discoveredFeeds.Keys) {
     if (item.InstanceProps != null)
      item.InstanceProps.IsFirstInGroup = false;
    }
    discoveredFeeds.Remove(itemClicked);
    itemDropdown.Tools.Remove(itemClicked);
    if (itemDropdown.Tools.Count > 1)
     itemDropdown.Tools[1].InstanceProps.IsFirstInGroup = true;
    RefreshDiscoveredItemContainer();
   }
  }
 
  private  void RefreshDiscoveredItemContainer() {
   if (app == null || app.MainForm == null || app.MainForm.Disposing)
    return;
            GuiInvoker.InvokeAsync(app.MainForm,
                delegate
                {
                    while (newDiscoveredFeeds.Count > 0)
                    {
                        AppButtonToolCommand item;
                        lock (newDiscoveredFeeds)
                        {
                            item = (AppButtonToolCommand)newDiscoveredFeeds.Dequeue();
                        }
                        lock (itemDropdown.Tools)
                        {
                            if (itemDropdown.Tools.Contains(item))
                                itemDropdown.Tools.Remove(item);
                            itemDropdown.Tools.Insert(1, item);
                        }
                    }
                    lock (itemDropdown.Tools)
                    {
                        foreach (AppButtonToolCommand m in itemDropdown.Tools)
                        {
                            if (m.InstanceProps != null)
                                m.InstanceProps.IsFirstInGroup = false;
                        }
                    }
                    lock (itemDropdown.Tools)
                    {
                        if (itemDropdown.Tools.Count > 1)
                            itemDropdown.Tools[1].InstanceProps.IsFirstInGroup = true;
                        int cnt = itemDropdown.Tools.Count;
                        if (cnt <= 1)
                        {
                            itemDropdown.SharedProps.AppearancesSmall.Appearance = this.discoveredAppearance[0];
                            itemDropdown.SharedProps.AppearancesLarge.Appearance = this.discoveredAppearance[1];
                        }
                        else
                        {
                            itemDropdown.SharedProps.AppearancesSmall.Appearance = this.discoveredAppearance[2];
                            itemDropdown.SharedProps.AppearancesLarge.Appearance = this.discoveredAppearance[3];
                            if (!itemDropdown.Enabled)
                                itemDropdown.Enabled = true;
                        }
                    }
                });
  }
 
  private  void OnToolbarsManager_ToolClick(object sender, ToolClickEventArgs e) {
   this.mediator.Execute(e.Tool.Key);
  }
 
  private  bool RaiseOnDiscoveredFeedsSubscribe(DiscoveredFeedsInfo feedInfo) {
   bool cancel = false;
   if (OnDiscoveredFeedsSubscribe != null) try {
    DiscoveredFeedsSubscribeCancelEventArgs ea = new DiscoveredFeedsSubscribeCancelEventArgs(feedInfo, cancel);
    OnDiscoveredFeedsSubscribe(this, ea);
    cancel = ea.Cancel;
   } catch (Exception ex) {
    Log.Error("OnDiscoveredFeedsSubscribe() event causes an exception", ex);
   }
   return cancel;
  }
 
  private  AppButtonToolCommand FindYetDiscoveredFeedMenuItem(DiscoveredFeedsInfo info) {
   if (info == null)
    return null;
   AppButtonToolCommand foundItem = null;
   lock(discoveredItems) {
    foreach (AppButtonToolCommand item in discoveredFeeds.Keys) {
     if (null == foundItem) {
      DiscoveredFeedsInfo itemInfo = (DiscoveredFeedsInfo) discoveredFeeds[item];
      if (0 == String.Compare(itemInfo.SiteBaseUrl, info.SiteBaseUrl, true)) {
       foundItem = item;
      } else {
       ArrayList knownFeeds = itemInfo.FeedLinks;
       foreach (string feedLink in knownFeeds) {
        if (info.FeedLinks.Contains(feedLink)) {
         foundItem = item;
         break;
        }
       }
      }
     }
    }
   }
   return foundItem;
  }
 
  private  ArrayList CheckAndRemoveSubscribedFeeds(ArrayList feeds, string baseUrl) {
   ArrayList ret = new ArrayList(feeds.Count);
   Uri baseUri = null;
   try {
    if (baseUrl != null && baseUrl.Length > 0)
     baseUri = new Uri(baseUrl);
   } catch (UriFormatException) {}
   foreach (string url in feeds) {
    Uri uri;
    try {
     if (baseUri != null) {
      uri = new Uri(baseUri, url);
     } else {
      uri = new Uri(url);
     }
                    string key = uri.CanonicalizedUri();
     if (!this.app.FeedHandler.FeedsTable.ContainsKey(key)) {
      ret.Add(key);
     }
    } catch (UriFormatException) { }
   }
   return ret;
  }
 
  private  System.Net.IWebProxy Proxy {
   get { return this.app.Proxy; }
  }
 
  private class  CallbackState {
			
   public  string HtmlContent, PageUrl, PageTitle;
 
   public  CallbackState(string htmlContent, string pageUrl, string pageTitle) {
    this.HtmlContent = htmlContent;
    this.PageUrl = pageUrl;
    this.PageTitle = pageTitle;
   }

		}

	}
	
 public class  DiscoveredFeedsSubscribeCancelEventArgs : System.ComponentModel.CancelEventArgs {
		
  public  DiscoveredFeedsSubscribeCancelEventArgs():base(false) {
   feedsInfo = new DiscoveredFeedsInfo();
  }
 
  public  DiscoveredFeedsSubscribeCancelEventArgs(DiscoveredFeedsInfo feedsInfo, bool cancel) {
   base.Cancel = cancel;
   this.feedsInfo = feedsInfo;
  }
 
  public  DiscoveredFeedsInfo FeedsInfo {
   get { return this.feedsInfo; }
  }
 
  private  DiscoveredFeedsInfo feedsInfo;

	}
	
 public class  DiscoveredFeedsInfo {
		
  public  ArrayList FeedLinks;
 
  public  string Title;
 
  public  string SiteBaseUrl;
 
  public  DiscoveredFeedsInfo() {
   this.FeedLinks = new ArrayList(1);
   this.Title = String.Empty;
   this.SiteBaseUrl = String.Empty;
  }
 
  public  DiscoveredFeedsInfo(string feedLink, string title, string baseUrl):this() {
   this.FeedLinks.Add(feedLink);
   this.Title = title;
   this.SiteBaseUrl = baseUrl;
  }
 
  public  DiscoveredFeedsInfo(ArrayList feedLinks, string title, string baseUrl) {
   this.FeedLinks = feedLinks;
   this.Title = title;
   this.SiteBaseUrl = baseUrl;
  }

	}

}
