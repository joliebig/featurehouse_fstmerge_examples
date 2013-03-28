using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
using System.Xml;
using System.Xml.Serialization;
using System.Xml.XPath;
using System.Runtime.InteropServices;
using Microsoft.Feeds.Interop;
using RssBandit.Common;
using RssBandit.Common.Logging;
using log4net;
using NewsComponents.Collections;
using NewsComponents.Net;
using NewsComponents.RelationCosmos;
using NewsComponents.Search;
using NewsComponents.Utils;
namespace NewsComponents.Feed {
    public class WindowsRssPlatformException : Exception {
        public WindowsRssPlatformException(string message) : base(message) { }
    }
    class WindowsRssFeedSource : FeedSource, IFeedFolderEvents
    {
        public WindowsRssFeedSource(INewsComponentsConfiguration configuration)
        {
            this.p_configuration = configuration;
            if (this.p_configuration == null)
                this.p_configuration = FeedSource.DefaultConfiguration;
            ValidateAndThrow(this.Configuration);
            this.AttachEventHandlers();
            feedManager.BackgroundSync(FEEDS_BACKGROUNDSYNC_ACTION.FBSA_ENABLE);
        }
        private IFeedsManager feedManager = new FeedsManagerClass();
        private IFeedFolderEvents_Event fw;
        internal static bool event_caused_by_rssbandit = false;
        internal static Object event_caused_by_rssbandit_syncroot = new Object();
         private static readonly ILog _log = Log.GetLogger(typeof (WindowsRssFeedSource));
         public override int RefreshRate
         {
             set
             {
                 if (value >= 15 * 60000)
                 {
                     this.feedManager.DefaultInterval = value / 60000;
                 }
             }
             get
             {
                 return feedManager.DefaultInterval * 60000;
             }
         }
        internal void AttachEventHandlers()
        {
            IFeedFolder folder = feedManager.RootFolder as IFeedFolder;
    if (folder != null)
   {
     fw = (IFeedFolderEvents_Event)folder.GetWatcher(
         FEEDS_EVENTS_SCOPE.FES_ALL, FEEDS_EVENTS_MASK.FEM_FOLDEREVENTS);
    fw.Error += Error;
    fw.FeedAdded += FeedAdded;
    fw.FeedDeleted += FeedDeleted;
    fw.FeedDownloadCompleted += FeedDownloadCompleted;
    fw.FeedDownloading += FeedDownloading;
    fw.FeedItemCountChanged += FeedItemCountChanged;
    fw.FeedMovedFrom += FeedMovedFrom;
    fw.FeedMovedTo += FeedMovedTo;
    fw.FeedRenamed += FeedRenamed;
    fw.FeedUrlChanged += FeedUrlChanged;
    fw.FolderAdded += FolderAdded;
    fw.FolderDeleted += FolderDeleted;
    fw.FolderItemCountChanged += FolderItemCountChanged;
    fw.FolderMovedFrom += FolderMovedFrom;
    fw.FolderMovedTo += FolderMovedTo;
    fw.FolderRenamed += FolderRenamed;
   }
        }
        internal void DetachEventHandlers()
        {
            IFeedFolder folder = feedManager.RootFolder as IFeedFolder;
   if (folder != null) {
    fw = (IFeedFolderEvents_Event)folder.GetWatcher(
     FEEDS_EVENTS_SCOPE.FES_ALL, FEEDS_EVENTS_MASK.FEM_FOLDEREVENTS);
    fw.Error -= Error;
    fw.FeedAdded -= FeedAdded;
    fw.FeedDeleted -= FeedDeleted;
    fw.FeedDownloadCompleted -= FeedDownloadCompleted;
    fw.FeedDownloading -= FeedDownloading;
    fw.FeedItemCountChanged -= FeedItemCountChanged;
    fw.FeedMovedFrom -= FeedMovedFrom;
    fw.FeedMovedTo -= FeedMovedTo;
    fw.FeedRenamed -= FeedRenamed;
    fw.FeedUrlChanged -= FeedUrlChanged;
    fw.FolderAdded -= FolderAdded;
    fw.FolderDeleted -= FolderDeleted;
    fw.FolderItemCountChanged -= FolderItemCountChanged;
    fw.FolderMovedFrom -= FolderMovedFrom;
    fw.FolderMovedTo -= FolderMovedTo;
    fw.FolderRenamed -= FolderRenamed;
   }
        }
        public IFeedFolder AddFolder(string path)
        {
            IFeedFolder folder = feedManager.RootFolder as IFeedFolder;
            lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
            {
                if (!StringHelper.EmptyTrimOrNull(path))
                {
                    string[] categoryPath = path.Split(new char[] { '\\' });
                    foreach (string c in categoryPath)
                    {
                        if (folder.ExistsSubfolder(c))
                        {
                            folder = folder.GetSubfolder(c) as IFeedFolder;
                        }
                        else
                        {
                            folder = folder.CreateSubfolder(c) as IFeedFolder;
                            if (!folder.Path.Equals(path) && !categories.ContainsKey(folder.Path))
                            {
                                this.categories.Add(folder.Path, new WindowsRssNewsFeedCategory(folder));
                            }
                        }
                    }
                }
                WindowsRssFeedSource.event_caused_by_rssbandit = true;
            }
            return folder;
        }
        public override void ResumePendingDownloads()
        {
        }
         public override IFeedDetails GetFeedDetails(string feedUrl)
        {
            INewsFeed f = null;
            feedsTable.TryGetValue(feedUrl, out f);
            return f as IFeedDetails;
        }
        public override IList<INewsItem> GetCachedItemsForFeed(string feedUrl)
        {
            return this.GetItemsForFeed(feedUrl, false);
        }
        public override void MarkAllCachedItemsAsRead(INewsFeed feed)
        {
            WindowsRssNewsFeed f = feed as WindowsRssNewsFeed;
            this.DetachEventHandlers();
            if (f != null && !string.IsNullOrEmpty(f.link))
            {
                f.MarkAllItemsAsRead();
            }
            this.AttachEventHandlers();
        }
        public override IList<INewsItem> GetItemsForFeed(string feedUrl, bool force_download)
        {
            WindowsRssNewsFeed theFeed = null;
            INewsFeed f = null;
            feedsTable.TryGetValue(feedUrl, out f);
            if (f == null)
                return EmptyItemList;
            else
                theFeed = f as WindowsRssNewsFeed;
            try
            {
                if (force_download)
                {
                    theFeed.RefreshFeed();
                }
                return theFeed.ItemsList;
            }
            catch (Exception ex)
            {
                Trace("Error retrieving feed '{0}' from cache: {1}", feedUrl, ex.ToString());
            }
            return EmptyItemList;
        }
        public override INewsFeedCategory AddCategory(INewsFeedCategory cat)
        {
            if (cat is WindowsRssNewsFeedCategory)
            {
                if (!categories.ContainsKey(cat.Value))
                {
                    categories.Add(cat.Value, cat);
                }
            }
            else
            {
                if (!categories.ContainsKey(cat.Value))
                {
                    IFeedFolder folder = this.AddFolder(cat.Value);
                    cat = new WindowsRssNewsFeedCategory(folder, cat);
                    this.categories.Add(cat.Value, cat);
                }
                else
                {
                    cat = categories[cat.Value];
                }
            }
            return cat;
        }
        public override INewsFeedCategory AddCategory(string cat)
        {
            INewsFeedCategory toReturn;
            if (!this.categories.ContainsKey(cat))
            {
                    IFeedFolder folder = this.AddFolder(cat);
                    toReturn = new WindowsRssNewsFeedCategory(folder);
                    this.categories.Add(cat, toReturn);
            }else{
                toReturn = categories[cat];
            }
            return toReturn;
        }
        public override void ChangeCategory(INewsFeed feed, INewsFeedCategory cat)
        {
            if (feed == null)
                throw new ArgumentNullException("feed");
            if (cat == null)
                throw new ArgumentNullException("cat");
            WindowsRssNewsFeed f = feed as WindowsRssNewsFeed;
            if (f != null && feedsTable.ContainsKey(f.link))
            {
                IFeedFolder folder = String.IsNullOrEmpty(f.category) ? feedManager.RootFolder as IFeedFolder
                                                                      : feedManager.GetFolder(f.category) as IFeedFolder;
                IFeed ifeed = folder.GetFeed(f.title) as IFeed;
                ifeed.Move(cat.Value);
            }
        }
        public override void RenameCategory(string oldName, string newName)
        {
            if (StringHelper.EmptyTrimOrNull(oldName))
                throw new ArgumentNullException("oldName");
            if (StringHelper.EmptyTrimOrNull(newName))
                throw new ArgumentNullException("newName");
            lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
            {
                if (this.categories.ContainsKey(oldName))
                {
                    WindowsRssNewsFeedCategory cat = this.categories[oldName] as WindowsRssNewsFeedCategory;
                    IFeedFolder folder = feedManager.GetFolder(oldName) as IFeedFolder;
                    if (folder != null)
                    {
                        folder.Rename(newName);
                        this.categories.Remove(oldName);
                        categories.Add(newName, new WindowsRssNewsFeedCategory(folder, cat));
                    }
                }
                WindowsRssFeedSource.event_caused_by_rssbandit = true;
            }
        }
        public override INewsFeed AddFeed(INewsFeed feed, FeedInfo feedInfo)
        {
            lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
            {
                if (feed is WindowsRssNewsFeed)
                {
                    if (!feedsTable.ContainsKey(feed.link))
                    {
                        feedsTable.Add(feed.link, feed);
                    }
                }
                else
                {
                    if (!feedManager.ExistsFolder(feed.category))
                    {
                        this.AddCategory(feed.category);
                    }
                    IFeedFolder folder = feedManager.GetFolder(feed.category) as IFeedFolder;
                    IFeed newFeed = folder.CreateFeed(feed.title, feed.link) as IFeed;
                    feed = new WindowsRssNewsFeed(newFeed, feed, this);
                    feedsTable.Add(feed.link, feed);
                }
                WindowsRssFeedSource.event_caused_by_rssbandit = true;
            }
            return feed;
        }
        public override void DeleteAllFeedsAndCategories()
        {
            string[] keys = new string[categories.Count];
            this.categories.Keys.CopyTo(keys, 0);
            foreach (string categoryName in keys)
            {
                this.DeleteCategory(categoryName);
            }
            keys = new string[feedsTable.Count];
            this.feedsTable.Keys.CopyTo(keys, 0);
            foreach (string feedUrl in keys) {
                this.DeleteFeed(feedUrl);
            }
            base.DeleteAllFeedsAndCategories();
        }
        public override void DeleteFeed(string feedUrl)
        {
            if (feedsTable.ContainsKey(feedUrl))
            {
                WindowsRssNewsFeed f = feedsTable[feedUrl] as WindowsRssNewsFeed;
                this.feedsTable.Remove(f.link);
                try
                {
                    IFeed feed = feedManager.GetFeedByUrl(feedUrl) as IFeed;
                    if (feed != null)
                    {
                        feed.Delete();
                    }
                }
                catch(Exception e)
                {
                    _log.Debug("Exception on deleting feed: " + feedUrl, e);
                }
            }
        }
        public override void DeleteCategory(string cat)
        {
            base.DeleteCategory(cat);
            IFeedFolder folder = feedManager.GetFolder(cat) as IFeedFolder;
            if (folder != null)
            {
                folder.Delete();
            }
        }
        private void LoadFolder(IFeedFolder folder2load, Dictionary<string, NewsFeed> bootstrapFeeds, Dictionary<string, INewsFeedCategory> bootstrapCategories)
        {
            if (folder2load != null)
            {
                IFeedsEnum Feeds = folder2load.Feeds as IFeedsEnum;
                IFeedsEnum Subfolders = folder2load.Subfolders as IFeedsEnum;
                if (Feeds.Count > 0)
                {
                    foreach (IFeed feed in Feeds)
                    {
                        Uri uri = null;
                        try
                        {
                            uri = new Uri(feed.DownloadUrl);
                        }
                        catch (Exception)
                        {
                            try
                            {
                                uri = new Uri(feed.Url);
                            }
                            catch (Exception)
                            {
                                continue;
                            }
                        }
                        string feedUrl = uri.CanonicalizedUri();
                        INewsFeed bootstrapFeed = (bootstrapFeeds.ContainsKey(feedUrl) ? bootstrapFeeds[feedUrl] : null);
                        if (this.feedsTable.ContainsKey(feedUrl))
                        {
                            try {
                                feed.Delete();
                            }catch (Exception e){
                                _log.Debug("Exception deleting duplicate feed " + feedUrl, e);
                            }
                        }
                        else
                        {
                            this.feedsTable.Add(feedUrl, new WindowsRssNewsFeed(feed, bootstrapFeed, this));
                        }
                    }
                }
                if (Subfolders.Count > 0)
                {
                    foreach (IFeedFolder folder in Subfolders)
                    {
                        string categoryName = folder.Path;
                        INewsFeedCategory bootstrapCategory = (bootstrapCategories.ContainsKey(categoryName) ? bootstrapCategories[categoryName] : null);
                        this.categories.Add(folder.Path, new WindowsRssNewsFeedCategory(folder, bootstrapCategory));
                        LoadFolder(folder, bootstrapFeeds, bootstrapCategories);
                    }
                }
            }
        }
        public override void LoadFeedlist()
        {
            this.BootstrapAndLoadFeedlist(new feeds());
        }
        public override void BootstrapAndLoadFeedlist(feeds feedlist)
        {
            Dictionary<string, NewsFeed> bootstrapFeeds = new Dictionary<string, NewsFeed>();
            Dictionary<string, INewsFeedCategory> bootstrapCategories = new Dictionary<string, INewsFeedCategory>();
            foreach (NewsFeed f in feedlist.feed)
            {
                bootstrapFeeds.Add(f.link, f);
            }
            foreach (category c in feedlist.categories)
            {
                bootstrapCategories.Add(c.Value, c);
            }
            IFeedFolder root = feedManager.RootFolder as IFeedFolder;
            LoadFolder(root, bootstrapFeeds, bootstrapCategories);
        }
        public override bool AsyncGetItemsForFeed(string feedUrl, bool forceDownload, bool manual)
        {
            if (feedUrl == null || feedUrl.Trim().Length == 0)
                throw new ArgumentNullException("feedUrl");
            INewsFeed f = null;
            feedsTable.TryGetValue(feedUrl, out f);
            WindowsRssNewsFeed f2 = f as WindowsRssNewsFeed;
            if (f2 != null)
            {
                f2.RefreshFeed(true);
            }
            return true;
        }
        public override void RefreshFeeds(bool force_download)
        {
            string[] keys = GetFeedsTableKeys();
            this.feedManager.AsyncSyncAll();
            this.RaiseOnAllAsyncRequestsCompleted();
        }
        public override void RefreshFeeds(string category, bool force_download)
        {
            string[] keys = GetFeedsTableKeys();
            for (int i = 0, len = keys.Length; i < len; i++)
            {
                if (!feedsTable.ContainsKey(keys[i]))
                    continue;
                WindowsRssNewsFeed current = feedsTable[keys[i]] as WindowsRssNewsFeed;
                if (current.category != null && IsChildOrSameCategory(category, current.category))
                {
                    current.RefreshFeed(true);
                }
                Thread.Sleep(15);
            }
        }
        public void Error()
        {
            throw new WindowsRssPlatformException("Windows RSS platform has raised an error. Please reload the Windows RSS feed list");
        }
        public void FolderAdded(string Path)
        {
            this.categories.Add(Path, new WindowsRssNewsFeedCategory(feedManager.GetFolder(Path) as IFeedFolder));
            this.readonly_categories = new ReadOnlyDictionary<string, INewsFeedCategory>(this.categories);
            RaiseOnAddedCategory(new CategoryEventArgs(Path));
        }
        public void FolderDeleted(string Path)
        {
            this.categories.Remove(Path);
            this.readonly_categories = new ReadOnlyDictionary<string, INewsFeedCategory>(this.categories);
            RaiseOnDeletedCategory(new CategoryEventArgs(Path));
        }
        public void FolderMovedFrom(string Path, string oldPath)
        {
            Console.WriteLine("Folder moved from {0} to {1}", oldPath, Path);
        }
        public void FolderMovedTo(string Path, string oldPath)
        {
            INewsFeedCategory cat = this.categories[oldPath];
            List<INewsFeedCategory> childCategories = this.GetChildCategories(cat);
            base.DeleteCategory(oldPath);
            this.categories.Add(Path, new WindowsRssNewsFeedCategory(feedManager.GetFolder(Path) as IFeedFolder, cat));
            foreach (INewsFeedCategory c in childCategories)
            {
                string newPath = Path + FeedSource.CategorySeparator + c.Value.Substring(c.Value.LastIndexOf(FeedSource.CategorySeparator) + 1);
                this.categories.Add(newPath, new WindowsRssNewsFeedCategory(feedManager.GetFolder(newPath) as IFeedFolder, cat));
            }
            this.readonly_categories = new ReadOnlyDictionary<string, INewsFeedCategory>(this.categories);
            RaiseOnMovedCategory(new CategoryChangedEventArgs(oldPath, Path));
        }
        public void FolderRenamed(string Path, string oldPath)
        {
            INewsFeedCategory cat = this.categories[oldPath];
            this.categories.Remove(oldPath);
            this.categories.Add(Path, new WindowsRssNewsFeedCategory(feedManager.GetFolder(Path) as IFeedFolder, cat));
            this.readonly_categories = new ReadOnlyDictionary<string, INewsFeedCategory>(this.categories);
            RaiseOnRenamedCategory(new CategoryChangedEventArgs(oldPath, Path));
        }
        public void FolderItemCountChanged(string Path, int itemCountType)
        {
        }
        public void FeedAdded(string Path)
        {
            lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
            {
                if (WindowsRssFeedSource.event_caused_by_rssbandit)
                {
                    WindowsRssFeedSource.event_caused_by_rssbandit = false;
                    return;
                }
            }
            IFeed ifeed = feedManager.GetFeed(Path) as IFeed;
            this.feedsTable.Add(ifeed.DownloadUrl, new WindowsRssNewsFeed(ifeed));
            this.readonly_feedsTable = new ReadOnlyDictionary<string, INewsFeed>(this.feedsTable);
            RaiseOnAddedFeed(new FeedChangedEventArgs(ifeed.DownloadUrl));
        }
        public void FeedDeleted(string Path)
        {
            lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
            {
                if (WindowsRssFeedSource.event_caused_by_rssbandit)
                {
                    WindowsRssFeedSource.event_caused_by_rssbandit = false;
                    return;
                }
            }
            int index = Path.LastIndexOf(FeedSource.CategorySeparator);
            string categoryName = null, title = null;
            if (index == -1)
            {
                title = Path;
            }
            else
            {
                categoryName = Path.Substring(0, index);
                title = Path.Substring(index + 1);
            }
            string[] keys = GetFeedsTableKeys();
            for (int i = 0; i < keys.Length; i++)
            {
                INewsFeed f = null;
                feedsTable.TryGetValue(keys[i], out f);
                if (f != null)
                {
                    if (f.title.Equals(title) && (Object.Equals(f.category, categoryName)))
                    {
                        this.feedsTable.Remove(f.link);
                        this.readonly_feedsTable = new ReadOnlyDictionary<string, INewsFeed>(this.feedsTable);
                        RaiseOnDeletedFeed(new FeedDeletedEventArgs(f.link, f.title));
                        break;
                    }
                }
            }
        }
        public void FeedRenamed(string Path, string oldPath)
        {
            lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
            {
                if (WindowsRssFeedSource.event_caused_by_rssbandit)
                {
                    WindowsRssFeedSource.event_caused_by_rssbandit = false;
                    return;
                }
            }
            int index = oldPath.LastIndexOf(FeedSource.CategorySeparator);
            string categoryName = null, title = null;
            if (index == -1)
            {
                title = oldPath;
            }
            else
            {
                categoryName = oldPath.Substring(0, index);
                title = oldPath.Substring(index + 1);
            }
            string[] keys = GetFeedsTableKeys();
            for (int i = 0; i < keys.Length; i++)
            {
                INewsFeed f = null;
                feedsTable.TryGetValue(keys[i], out f);
                if (f != null)
                {
                    if (f.title.Equals(title) && (Object.Equals(f.category, categoryName)))
                    {
                        index = Path.LastIndexOf(FeedSource.CategorySeparator);
                        string newTitle = (index == -1 ? Path : Path.Substring(index + 1));
                        RaiseOnRenamedFeed(new FeedRenamedEventArgs(f.link, newTitle));
                        break;
                    }
                }
            }
        }
        public void FeedMovedFrom(string Path, string oldPath)
        {
            Console.WriteLine("Feed moved from {0} to {1}", oldPath, Path);
        }
        public void FeedMovedTo(string Path, string oldPath)
        {
            lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
            {
                if (WindowsRssFeedSource.event_caused_by_rssbandit)
                {
                    WindowsRssFeedSource.event_caused_by_rssbandit = false;
                    return;
                }
            }
            int index = oldPath.LastIndexOf(FeedSource.CategorySeparator);
            string categoryName = null, title = null;
            if (index == -1)
            {
                title = oldPath;
            }
            else
            {
                categoryName = oldPath.Substring(0, index);
                title = oldPath.Substring(index + 1);
            }
            string[] keys = GetFeedsTableKeys();
            for (int i = 0; i < keys.Length; i++)
            {
                INewsFeed f = null;
                feedsTable.TryGetValue(keys[i], out f);
                if (f != null)
                {
                    if (f.title.Equals(title) && (Object.Equals(f.category, categoryName)))
                    {
                        ((WindowsRssNewsFeed)f).SetIFeed(feedManager.GetFeed(Path) as IFeed);
                        RaiseOnMovedFeed(new FeedMovedEventArgs(f.link, f.category));
                        break;
                    }
                }
            }
        }
        public void FeedUrlChanged(string Path)
        {
            lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
            {
                if (WindowsRssFeedSource.event_caused_by_rssbandit)
                {
                    WindowsRssFeedSource.event_caused_by_rssbandit = false;
                    return;
                }
            }
            IFeed ifeed = feedManager.GetFeed(Path) as IFeed;
            int index = Path.LastIndexOf(FeedSource.CategorySeparator);
            string categoryName = null, title = null;
            if (index == -1)
            {
                title = Path;
            }
            else
            {
                categoryName = Path.Substring(0, index);
                title = Path.Substring(index + 1);
            }
            string[] keys = GetFeedsTableKeys();
            for (int i = 0; i < keys.Length; i++)
            {
                INewsFeed f = null;
                feedsTable.TryGetValue(keys[i], out f);
                if (f != null)
                {
                    if (f.title.Equals(title) && (Object.Equals(f.category, categoryName)))
                    {
                        Uri requestUri = new Uri(f.link);
                        Uri newUri = new Uri(ifeed.DownloadUrl);
                        RaiseOnUpdatedFeed(requestUri, newUri, RequestResult.NotModified, 1110, false);
                        break;
                    }
                }
            }
        }
        public void FeedItemCountChanged(string Path, int itemCountType)
        {
        }
        public void FeedDownloading(string Path)
        {
            lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
            {
                if (WindowsRssFeedSource.event_caused_by_rssbandit)
                {
                    WindowsRssFeedSource.event_caused_by_rssbandit = false;
                    return;
                }
            }
            IFeed ifeed = feedManager.GetFeed(Path) as IFeed;
            Uri requestUri = new Uri(ifeed.DownloadUrl);
            bool cancel = false;
            RaiseBeforeDownloadFeedStarted(requestUri, ref cancel);
            if (cancel)
            {
                ifeed.CancelAsyncDownload();
            }
        }
        public void FeedDownloadCompleted(string Path, FEEDS_DOWNLOAD_ERROR Error)
        {
            lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
            {
                if (WindowsRssFeedSource.event_caused_by_rssbandit)
                {
                    WindowsRssFeedSource.event_caused_by_rssbandit = false;
                    return;
                }
            }
            IFeed ifeed = feedManager.GetFeed(Path) as IFeed;
            Uri requestUri = new Uri(ifeed.DownloadUrl);
            if (Error == FEEDS_DOWNLOAD_ERROR.FDE_NONE)
            {
                RaiseOnUpdatedFeed(requestUri, null, RequestResult.OK, 1110, false);
            }
            else
            {
                INewsFeed f = null;
                feedsTable.TryGetValue(ifeed.DownloadUrl, out f);
                WindowsRssNewsFeed wf = f as WindowsRssNewsFeed;
                if (wf == null)
                {
                    Exception e = new FeedRequestException(Error.ToString(), new WebException(Error.ToString()), FeedSource.GetFailureContext(wf, wf));
                    RaiseOnUpdateFeedException(ifeed.DownloadUrl, e, 1100);
                }
            }
        }
    }
    public class WindowsRssNewsItem : INewsItem, IDisposable
    {
        private WindowsRssNewsItem() { ;}
        internal WindowsRssNewsItem(IFeedItem item, WindowsRssNewsFeed owner)
        {
            if (item == null) throw new ArgumentNullException("item");
            this.myitem = item;
            this.myfeed = owner;
            this._id = myitem.LocalId.ToString();
            this._beenRead = myitem.IsRead;
        }
        private bool disposed = false;
        private IFeedItem myitem = null;
        private WindowsRssNewsFeed myfeed = null;
        private static readonly ILog _log = Log.GetLogger(typeof(WindowsRssNewsItem));
        ~WindowsRssNewsItem() {
            Dispose(false);
        }
        public void Dispose()
        {
            if (!disposed)
            {
                Dispose(true);
            }
        }
        public void Dispose(bool disposing)
        {
            lock (this)
            {
                if (myitem != null)
                {
                    Marshal.ReleaseComObject(myitem);
                }
                System.GC.SuppressFinalize(this);
                disposed = true;
            }
        }
        public List<IEnclosure> Enclosures
        {
            get
            {
                if (myitem.Enclosure != null)
                {
                    IEnclosure enc = new WindowsRssEnclosure(myitem.Enclosure as IFeedEnclosure);
                    return new List<IEnclosure>() { enc };
                }
                else
                {
                    return GetList<IEnclosure>.Empty;
                }
            }
            set
            {
            }
        }
        private bool p_hasNewComments;
        public bool HasNewComments
        {
            get
            {
                return p_hasNewComments;
            }
            set
            {
                p_hasNewComments = value;
            }
        }
        private bool p_watchComments;
        public bool WatchComments
        {
            get
            {
                return p_watchComments;
            }
            set
            {
                p_watchComments = value;
            }
        }
        private Flagged p_flagStatus = Flagged.None;
        public Flagged FlagStatus
        {
            get
            {
                return p_flagStatus;
            }
            set
            {
                p_flagStatus = value;
            }
        }
        public SupportedCommentStyle CommentStyle
        {
            get
            {
                return SupportedCommentStyle.None;
            }
            set
            {
            }
        }
        public string Language
        {
            get { return myfeed.Language; }
        }
        public string FeedLink
        {
            get { return (myitem.Parent as IFeed).DownloadUrl; }
        }
        public string Link
        {
            get
            {
                try
                {
                    return myitem.Link;
                }
                catch
                {
                    return String.Empty;
                }
            }
        }
        public DateTime Date
        {
            get
            {
                try
                {
                    return myitem.PubDate;
                }
                catch (Exception)
                {
                    return myitem.LastDownloadTime;
                }
            }
            set { }
        }
        private string _id;
        public string Id
        {
            get { return _id; }
            set { }
        }
        public string ParentId
        {
            get { return null; }
        }
        public string Content
        {
            get { return myitem.Description; }
        }
        public bool HasContent
        {
            get { return true; }
        }
        public void SetContent(string newContent, ContentType contentType)
        {
        }
        public ContentType ContentType
        {
            get { return ContentType.Html; }
            set { }
        }
        private bool _beenRead;
        public bool BeenRead
        {
            get
            {
                try
                {
                    return _beenRead;
                }
                catch (Exception e)
                {
                    _log.Debug(e.StackTrace);
                    return false;
                }
            }
            set {
                lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
                {
                    WindowsRssFeedSource.event_caused_by_rssbandit = true;
                    myitem.IsRead = value;
                }
            }
        }
        public IFeedDetails FeedDetails {
            get { return this.myfeed; }
            set
            {
                if (value is WindowsRssNewsFeed)
                    this.myfeed = value as WindowsRssNewsFeed;
            }
        }
        public string Author {
            get { return this.myitem.Author; }
            set { }
        }
       public string Title {
            get { return myitem.Title; }
            set { }
       }
        public string Subject {
            get { return null; }
            set { }
        }
        public int CommentCount
        {
            get { return 0; }
            set { }
        }
        public string CommentUrl
        {
            get{ return null;}
        }
        public string CommentRssUrl {
            get { return null; }
            set { }
        }
        private Hashtable _optionalElements = new Hashtable();
        public Hashtable OptionalElements {
            get { return _optionalElements; }
            set { _optionalElements = null; }
        }
        private List<string> outgoingRelationships = new List<string>();
        public List<string> OutGoingLinks
        {
            get
            {
                return outgoingRelationships;
            }
            internal set
            {
                outgoingRelationships = value;
            }
        }
        public INewsFeed Feed
        {
            get
            {
                return this.myfeed;
            }
        }
        public String ToString(NewsItemSerializationFormat format)
        {
            return ToString(format, true, false);
        }
        public String ToString(NewsItemSerializationFormat format, bool useGMTDate)
        {
            return ToString(format, useGMTDate, false);
        }
        public String ToString(NewsItemSerializationFormat format, bool useGMTDate, bool noDescriptions)
        {
            string toReturn;
            switch (format)
            {
                case NewsItemSerializationFormat.NewsPaper:
                case NewsItemSerializationFormat.RssFeed:
                case NewsItemSerializationFormat.RssItem:
                    toReturn = ToRssFeedOrItem(format, useGMTDate, noDescriptions);
                    break;
                case NewsItemSerializationFormat.NntpMessage:
                    throw new NotSupportedException(format.ToString());
                default:
                    throw new NotSupportedException(format.ToString());
            }
            return toReturn;
        }
        public override String ToString()
        {
            return ToString(NewsItemSerializationFormat.RssItem);
        }
        public String ToRssFeedOrItem(NewsItemSerializationFormat format, bool useGMTDate, bool noDescriptions)
        {
            StringBuilder sb = new StringBuilder("");
            XmlTextWriter writer = new XmlTextWriter(new StringWriter(sb));
            writer.Formatting = Formatting.Indented;
            if (format == NewsItemSerializationFormat.RssFeed || format == NewsItemSerializationFormat.NewsPaper)
            {
                if (format == NewsItemSerializationFormat.NewsPaper)
                {
                    writer.WriteStartElement("newspaper");
                    writer.WriteAttributeString("type", "newsitem");
                }
                else
                {
                    writer.WriteStartElement("rss");
                    writer.WriteAttributeString("version", "2.0");
                }
                writer.WriteStartElement("channel");
                writer.WriteElementString("title", FeedDetails.Title);
                writer.WriteElementString("link", FeedDetails.Link);
                writer.WriteElementString("description", FeedDetails.Description);
                foreach (string s in FeedDetails.OptionalElements.Values)
                {
                    writer.WriteRaw(s);
                }
            }
            WriteItem(writer, useGMTDate, noDescriptions);
            if (format == NewsItemSerializationFormat.RssFeed || format == NewsItemSerializationFormat.NewsPaper)
            {
                writer.WriteEndElement();
                writer.WriteEndElement();
            }
            return sb.ToString();
        }
        public void WriteItem(XmlWriter writer, bool useGMTDate, bool noDescriptions)
        {
            writer.WriteStartElement("item");
            if ((Language != null) && (Language.Length != 0))
            {
                writer.WriteAttributeString("xml", "lang", null, Language);
            }
            if ((Title != null) && (Title.Length != 0))
            {
                writer.WriteElementString("title", Title);
            }
            if ((HRef != null) && (HRef.Length != 0))
            {
                writer.WriteElementString("link", HRef);
            }
            if (useGMTDate)
            {
                writer.WriteElementString("pubDate", Date.ToString("r", DateTimeFormatInfo.InvariantInfo));
            }
            else
            {
                writer.WriteElementString("pubDate", Date.ToLocalTime().ToString("F", DateTimeFormatInfo.InvariantInfo));
            }
            if ((Subject != null) && (Subject.Length != 0))
            {
                writer.WriteElementString("category", Subject);
            }
            if ((Id != null) && (Id.Length != 0) && (Id.Equals(HRef) == false))
            {
                writer.WriteStartElement("guid");
                writer.WriteAttributeString("isPermaLink", "false");
                writer.WriteString(Id);
                writer.WriteEndElement();
            }
            if ((Author != null) && (Author.Length != 0))
            {
                writer.WriteElementString("creator", "http://purl.org/dc/elements/1.1/", Author);
            }
            if ((ParentId != null) && (ParentId.Length != 0))
            {
                writer.WriteStartElement("annotate", "reference", "http://purl.org/rss/1.0/modules/annotate/");
                writer.WriteAttributeString("rdf", "resource", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", ParentId);
                writer.WriteEndElement();
            }
            if (!noDescriptions && HasContent)
            {
                writer.WriteStartElement("description");
                writer.WriteCData(Content);
                writer.WriteEndElement();
            }
            if ((CommentUrl != null) && (CommentUrl.Length != 0))
            {
                if (CommentStyle == SupportedCommentStyle.CommentAPI)
                {
                    writer.WriteStartElement("wfw", "comment", RssHelper.NsCommentAPI);
                    writer.WriteString(CommentUrl);
                    writer.WriteEndElement();
                }
            }
            if ((CommentRssUrl != null) && (CommentRssUrl.Length != 0))
            {
                writer.WriteStartElement("wfw", "commentRss", RssHelper.NsCommentAPI);
                writer.WriteString(CommentRssUrl);
                writer.WriteEndElement();
            }
            if (CommentCount != NewsItem.NoComments)
            {
                writer.WriteStartElement("slash", "comments", "http://purl.org/rss/1.0/modules/slash/");
                writer.WriteString(CommentCount.ToString());
                writer.WriteEndElement();
            }
            writer.WriteStartElement("fd", "state", "http://www.bradsoft.com/feeddemon/xmlns/1.0/");
            writer.WriteAttributeString("read", BeenRead ? "1" : "0");
            writer.WriteAttributeString("flagged", FlagStatus == Flagged.None ? "0" : "1");
            writer.WriteEndElement();
            if (FlagStatus != Flagged.None)
            {
                writer.WriteElementString("flag-status", NamespaceCore.Feeds_v2003, FlagStatus.ToString());
            }
            if (p_watchComments)
            {
                writer.WriteElementString("watch-comments", NamespaceCore.Feeds_v2003, "1");
            }
            if (HasNewComments)
            {
                writer.WriteElementString("has-new-comments", NamespaceCore.Feeds_v2003, "1");
            }
            if (Enclosures != null)
            {
                foreach (IEnclosure enc in Enclosures)
                {
                    writer.WriteStartElement("enclosure");
                    writer.WriteAttributeString("url", enc.Url);
                    writer.WriteAttributeString("type", enc.MimeType);
                    writer.WriteAttributeString("length", enc.Length.ToString());
                    if (enc.Downloaded)
                    {
                        writer.WriteAttributeString("downloaded", "1");
                    }
                    if (enc.Duration != TimeSpan.MinValue)
                    {
                        writer.WriteAttributeString("duration", enc.Duration.ToString());
                    }
                    writer.WriteEndElement();
                }
            }
            writer.WriteStartElement("outgoing-links", NamespaceCore.Feeds_v2003);
            foreach (string outgoingLink in OutGoingLinks)
            {
                writer.WriteElementString("link", NamespaceCore.Feeds_v2003, outgoingLink);
            }
            writer.WriteEndElement();
            foreach (string s in OptionalElements.Values)
            {
                writer.WriteRaw(s);
            }
            writer.WriteEndElement();
        }
        public string HRef
        {
            get { return this.Link; }
        }
        public IList<string> OutgoingRelations
        {
            get
            {
                return outgoingRelationships;
            }
        }
        public virtual DateTime PointInTime
        {
            get { return this.Date; }
            set { }
        }
        public virtual bool HasExternalRelations { get { return false; } }
        public virtual IList<IRelation> GetExternalRelations()
        {
                return GetList<IRelation>.Empty;
        }
        public virtual void SetExternalRelations<T>(IList<T> relations) where T: IRelation
        {
        }
        public object Clone()
        {
            return new WindowsRssNewsItem(this.myitem, this.myfeed);
        }
        public INewsItem Clone(INewsFeed f)
        {
            return new WindowsRssNewsItem(this.myitem, f as WindowsRssNewsFeed);
        }
        public override bool Equals(object obj)
        {
            return Equals(obj as INewsItem);
        }
        public bool Equals(INewsItem other)
        {
            return Equals(other as WindowsRssNewsItem);
        }
        public bool Equals(WindowsRssNewsItem item) {
            if (item == null)
                return false;
            return item.myfeed.id.Equals(this.myfeed.id) && item.Id.Equals(this.Id);
        }
        public override int GetHashCode()
        {
            return this.Id.GetHashCode();
        }
        public int CompareTo(object obj)
        {
            return CompareTo(obj as WindowsRssNewsItem);
        }
        public int CompareTo(WindowsRssNewsItem other)
        {
            if (ReferenceEquals(this, other))
                return 0;
            if (ReferenceEquals(other, null))
                return 1;
            return this.Date.CompareTo(other.Date);
        }
        public int CompareTo(IRelation other)
        {
            return CompareTo(other as WindowsRssNewsItem);
        }
        public XPathNavigator CreateNavigator()
        {
            XmlDocument doc = new XmlDocument();
            doc.LoadXml(this.myitem.Xml(FEEDS_XML_INCLUDE_FLAGS.FXIF_NONE));
            return doc.CreateNavigator();
        }
    }
    public class WindowsRssEnclosure : IEnclosure
    {
        private WindowsRssEnclosure() { ;}
        internal WindowsRssEnclosure(IFeedEnclosure enclosure)
        {
            if (enclosure == null) throw new ArgumentNullException("enclosure");
            this.myenclosure = enclosure;
        }
        private IFeedEnclosure myenclosure = null;
        public string MimeType
        {
            get { return myenclosure.Type; }
        }
        public long Length
        {
            get { return myenclosure.Length; }
        }
        public string Url
        {
            get { return myenclosure.DownloadUrl; }
        }
        public string Description {
            get { return null; }
            set { ; }
        }
        private bool _downloaded;
        public bool Downloaded
        {
            get { return _downloaded; }
            set { _downloaded = value; }
        }
        public TimeSpan Duration
        {
            get { return TimeSpan.MinValue; }
            set { }
        }
        public override bool Equals(object obj)
        {
            return Equals(obj as WindowsRssEnclosure);
        }
        public bool Equals(IEnclosure item)
        {
            if (item == null)
            {
                return false;
            }
            if (ReferenceEquals(this, item))
            {
                return true;
            }
            if (String.Compare(Url, item.Url) == 0)
            {
                return true;
            }
            return false;
        }
        public override int GetHashCode()
        {
            if (string.IsNullOrEmpty(Url))
            {
                return String.Empty.GetHashCode();
            }
            else
            {
                return Url.GetHashCode();
            }
        }
    }
    public class WindowsRssNewsFeed : INewsFeed, IDisposable, IFeedDetails
    {
        private WindowsRssNewsFeed() { ;}
        internal WindowsRssNewsFeed(IFeed feed) {
            if (feed == null) throw new ArgumentNullException("feed");
            this.myfeed = feed;
            this._id = myfeed.LocalId;
            this.LoadItemsList();
        }
        internal WindowsRssNewsFeed(IFeed feed, INewsFeed banditfeed, object owner): this(feed)
        {
            if (banditfeed != null)
            {
                this.refreshrate = banditfeed.refreshrate;
                this.refreshrateSpecified = banditfeed.refreshrateSpecified;
                this.maxitemage = banditfeed.maxitemage;
                this.markitemsreadonexit = banditfeed.markitemsreadonexit;
                this.markitemsreadonexitSpecified = banditfeed.markitemsreadonexitSpecified;
                this.listviewlayout = banditfeed.listviewlayout;
                this.favicon = banditfeed.favicon;
                this.stylesheet = banditfeed.stylesheet;
                this.enclosurealert = banditfeed.enclosurealert;
                this.enclosurealertSpecified = banditfeed.enclosurealertSpecified;
                this.alertEnabled = banditfeed.alertEnabled;
                this.alertEnabledSpecified = banditfeed.alertEnabledSpecified;
                this.Any = banditfeed.Any;
                this.AnyAttr = banditfeed.AnyAttr;
            }
            if (owner is WindowsRssFeedSource)
            {
                this.owner = owner;
            }
        }
        ~WindowsRssNewsFeed() {
            Dispose(false);
        }
        public void Dispose()
        {
            Dispose(true);
   System.GC.SuppressFinalize(this);
        }
        protected virtual void Dispose(bool disposing)
  {
   if (!disposed)
    lock (this)
    {
     if (!disposed && myfeed != null)
     {
      Marshal.ReleaseComObject(myfeed);
     }
     disposed = true;
    }
  }
        private bool disposed = false;
        private IFeed myfeed = null;
        private List<INewsItem> items = new List<INewsItem>();
        private static readonly ILog _log = Log.GetLogger(typeof (WindowsRssNewsFeed));
        internal void LoadItemsList()
        {
            this.items.Clear();
            IFeedsEnum feedItems = this.myfeed.Items as IFeedsEnum;
            foreach (IFeedItem item in feedItems)
            {
                this.items.Add(new WindowsRssNewsItem(item, this));
            }
            _log.DebugFormat("LOAD_ITEMS_LIST:'{0}' loaded {1} item(s)", myfeed.Path, items.Count);
        }
        internal void SetIFeed(IFeed feed) {
            if (feed != null)
            {
                lock (this)
                {
                    if (myfeed != null)
                    {
                        Marshal.ReleaseComObject(myfeed);
                    }
                    myfeed = feed;
                }
            }
        }
        public void MarkAllItemsAsRead()
        {
            if (myfeed != null)
            {
                lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
                {
                    WindowsRssFeedSource.event_caused_by_rssbandit = true;
                    myfeed.MarkAllItemsRead();
                }
            }
        }
        public string title
        {
            get { return myfeed.Name; }
            set
            {
                lock (WindowsRssFeedSource.event_caused_by_rssbandit_syncroot)
                {
                    if (!StringHelper.EmptyTrimOrNull(value))
                    {
                        myfeed.Rename(value);
                        OnPropertyChanged("title");
                    }
                }
                WindowsRssFeedSource.event_caused_by_rssbandit = true;
            }
        }
        private string _link;
        [XmlElement(DataType = "anyURI")]
        public string link
        {
            get
            {
                try
                {
                    _link = myfeed.DownloadUrl;
                }
                catch (Exception e)
                {
                    _log.Debug("Exception on accessing IFeed.DownloadUrl: " + _link, e);
                    try
                    {
                        _link = myfeed.Url;
                    }
                    catch (Exception e2)
                    {
                        _log.Debug("Exception on accessing IFeed.Url: " + _link, e2);
                    }
                }
                return _link;
            }
            set
            {
            }
        }
        private string _id;
        [XmlAttribute]
        public string id
        {
            get { return _id; }
            set
            {
            }
        }
        [XmlElement("last-retrieved")]
        public DateTime lastretrieved
        {
            get { return myfeed.LastDownloadTime; }
            set
            {
            }
        }
        [XmlIgnore]
        public bool lastretrievedSpecified
        {
            get
            {
                return true;
            }
            set
            {
            }
        }
        [XmlElement("refresh-rate")]
        public int refreshrate { get; set; }
        [XmlIgnore]
        public bool refreshrateSpecified { get; set; }
        public string etag { get { return null; } set { } }
        [XmlElement(DataType = "anyURI")]
        public string cacheurl { get { return null; } set { } }
        [XmlElement("max-item-age", DataType = "duration")]
        public string maxitemage { get; set; }
        private List<string> _storiesrecentlyviewed = new List<string>();
        [XmlArray(ElementName = "stories-recently-viewed", IsNullable = false)]
        [XmlArrayItem("story", Type = typeof(String), IsNullable = false)]
        public List<string> storiesrecentlyviewed
        {
            get
            {
                foreach (INewsItem item in items)
                {
                    if (item.BeenRead)
                    {
                        _storiesrecentlyviewed.Add(item.Id);
                    }
                }
                return _storiesrecentlyviewed;
            }
            set
            {
                _storiesrecentlyviewed = new List<string>(value);
            }
        }
        private List<string> _deletedstories = new List<string>();
        [XmlArray(ElementName = "deleted-stories", IsNullable = false)]
        [XmlArrayItem("story", Type = typeof(String), IsNullable = false)]
        public List<string> deletedstories
        {
            get
            {
                return _deletedstories;
            }
            set
            {
                _deletedstories = new List<string>(value);
            }
        }
        [XmlElement("if-modified-since")]
        public DateTime lastmodified {
            get
            {
                return myfeed.LastWriteTime;
            }
            set
            {
            }
        }
        [XmlIgnore]
        public bool lastmodifiedSpecified { get { return true; } set { } }
        [XmlElement("auth-user")]
        public string authUser { get { return null; } set { } }
        [XmlElement("auth-password", DataType = "base64Binary")]
        public Byte[] authPassword { get { return null; } set { } }
        [XmlElement("listview-layout")]
        public string listviewlayout { get; set; }
        private string _favicon;
        public string favicon
        {
            get
            {
                return _favicon;
            }
            set
            {
                if (String.IsNullOrEmpty(_favicon) || !_favicon.Equals(value))
                {
                    _favicon = value;
                    this.OnPropertyChanged("favicon");
                }
            }
        }
        [XmlElement("download-enclosures")]
        public bool downloadenclosures
        {
            get
            {
                return myfeed.DownloadEnclosuresAutomatically;
            }
            set
            {
                myfeed.DownloadEnclosuresAutomatically = value;
            }
        }
        [XmlIgnore]
        public bool downloadenclosuresSpecified { get { return true; } set { } }
        [XmlElement("enclosure-folder")]
        public string enclosurefolder
        {
            get
            {
                return myfeed.LocalEnclosurePath;
            }
            set
            {
            }
        }
        [XmlAttribute("replace-items-on-refresh")]
        public bool replaceitemsonrefresh
        {
            get
            {
                try
                {
                    return myfeed.IsList;
                }
                catch
                {
                    return false;
                }
            }
            set
            {
            }
        }
        [XmlIgnore]
        public bool replaceitemsonrefreshSpecified { get { return true; } set { } }
        public string stylesheet { get; set; }
        [XmlElement("news-account")]
        public string newsaccount { get; set; }
        [XmlElement("mark-items-read-on-exit")]
        public bool markitemsreadonexit { get; set; }
        [XmlIgnore]
        public bool markitemsreadonexitSpecified { get; set; }
        [XmlAnyElement]
        public XmlElement[] Any { get; set; }
        [XmlAttribute("alert"), DefaultValue(false)]
        public bool alertEnabled { get; set; }
        [XmlIgnore]
        public bool alertEnabledSpecified { get; set; }
        [XmlAttribute("enclosure-alert"), DefaultValue(false)]
        public bool enclosurealert { get; set; }
        [XmlIgnore]
        public bool enclosurealertSpecified { get; set; }
        [XmlAttribute]
        public string category {
            get
            {
                string categoryName = null, path = this.myfeed.Path;
                int index = path.LastIndexOf(FeedSource.CategorySeparator);
                if (index != -1)
                {
                    categoryName = path.Substring(0, index);
                }
                return categoryName;
            }
            set
            {
                if (!StringHelper.EmptyTrimOrNull(value) && !value.Equals(this.category))
                {
                    WindowsRssFeedSource handler = owner as WindowsRssFeedSource;
                    handler.ChangeCategory(this, handler.AddCategory(value));
                }
            }
        }
        [XmlAnyAttribute]
        public XmlAttribute[] AnyAttr { get; set; }
        [XmlIgnore]
        public bool causedException
        {
            get
            {
                return causedExceptionCount != 0;
            }
            set
            {
                if (value)
                {
                    causedExceptionCount++;
                    lastretrievedSpecified = true;
                    lastretrieved = new DateTime(DateTime.Now.Ticks);
                }
                else
                    causedExceptionCount = 0;
            }
        }
        [XmlIgnore]
        public int causedExceptionCount { get; set; }
        [XmlIgnore]
        public object Tag { get; set; }
        [XmlIgnore]
        public bool containsNewMessages
        {
            get
            {
                bool hasUnread = items.Any(item => item.BeenRead == false);
                return hasUnread;
            }
            set
            {
            }
        }
        private bool _containsNewComments;
        [XmlIgnore]
        public bool containsNewComments
        {
            get
            {
                return _containsNewComments;
            }
            set
            {
                if (!_containsNewComments.Equals(value))
                {
                    _containsNewComments = value;
                    this.OnPropertyChanged("containsNewComments");
                }
            }
        }
        [XmlIgnore]
        public object owner { get; set; }
        public string GetElementWildCardValue(string namespaceUri, string localName)
        {
            foreach (XmlElement element in Any)
            {
                if (element.LocalName == localName && element.NamespaceURI == namespaceUri)
                    return element.InnerText;
            }
            return null;
        }
        public void AddViewedStory(string storyid)
        {
            if (!_storiesrecentlyviewed.Contains(storyid))
            {
                _storiesrecentlyviewed.Add(storyid);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("storiesrecentlyviewed", CollectionChangeAction.Add, storyid));
                }
            }
        }
        public void RemoveViewedStory(string storyid)
        {
            if (_storiesrecentlyviewed.Contains(storyid))
            {
                _storiesrecentlyviewed.Remove(storyid);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("storiesrecentlyviewed", CollectionChangeAction.Remove, storyid));
                }
            }
        }
        public void AddDeletedStory(string storyid)
        {
            if (!_deletedstories.Contains(storyid))
            {
                _deletedstories.Add(storyid);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("deletedstories", CollectionChangeAction.Add, storyid));
                }
            }
        }
        public void RemoveDeletedStory(string storyid)
        {
            if (_deletedstories.Contains(storyid))
            {
                _deletedstories.Remove(storyid);
                if (null != PropertyChanged)
                {
                    this.OnPropertyChanged(new CollectionChangedEventArgs("deletedstories", CollectionChangeAction.Remove, storyid));
                }
            }
        }
        public string Language {
            get { return this.myfeed.Language; }
        }
        public string Title {
            get { return this.myfeed.Title; }
        }
        public string Link {
            get { return this.myfeed.Link; }
        }
        public string Description {
            get { return this.myfeed.Description; }
        }
        public List<INewsItem> ItemsList
        {
            get
            {
                lock (this.items)
                {
                    LoadItemsList();
                }
                return this.items;
            }
            set
            {
            }
        }
        private Dictionary<XmlQualifiedName, string> _optionalElements = new Dictionary<XmlQualifiedName, string>();
        public Dictionary<XmlQualifiedName, string> OptionalElements {
            get { return this._optionalElements; }
        }
        public FeedType Type {
            get { return FeedType.Rss; }
        }
        string IFeedDetails.Id
        {
            get { return this.id; }
            set
            {
            }
        }
        public object Clone()
        {
            return new WindowsRssNewsFeed(myfeed);
        }
        public void WriteTo(XmlWriter writer)
        {
            this.WriteTo(writer, NewsItemSerializationFormat.RssFeed, true);
        }
        public void WriteTo(XmlWriter writer, NewsItemSerializationFormat format)
        {
            this.WriteTo(writer, format, true);
        }
        public void WriteTo(XmlWriter writer, NewsItemSerializationFormat format, bool useGMTDate)
        {
            if (format == NewsItemSerializationFormat.NewsPaper)
            {
                writer.WriteStartElement("newspaper");
                writer.WriteAttributeString("type", "channel");
                writer.WriteElementString("title", this.title);
            }
            else if (format != NewsItemSerializationFormat.Channel)
            {
                writer.WriteStartElement("rss");
                writer.WriteAttributeString("version", "2.0");
            }
            writer.WriteStartElement("channel");
            writer.WriteElementString("title", this.Title);
            writer.WriteElementString("link", this.Link);
            writer.WriteElementString("description", this.Description);
            foreach (string s in this.OptionalElements.Values)
            {
                writer.WriteRaw(s);
            }
            foreach (INewsItem item in this.ItemsList)
            {
                writer.WriteRaw(item.ToString(NewsItemSerializationFormat.RssItem, true));
            }
            writer.WriteEndElement();
            if (format != NewsItemSerializationFormat.Channel)
            {
                writer.WriteEndElement();
            }
        }
        public string ToString(NewsItemSerializationFormat format)
        {
            return this.ToString(format, true);
        }
        public string ToString(NewsItemSerializationFormat format, bool useGMTDate)
        {
            StringBuilder sb = new StringBuilder("");
            XmlTextWriter writer = new XmlTextWriter(new StringWriter(sb));
            this.WriteTo(writer, format, useGMTDate);
            writer.Flush();
            writer.Close();
            return sb.ToString();
        }
        public event PropertyChangedEventHandler PropertyChanged;
        protected virtual void OnPropertyChanged(string propertyName)
        {
            OnPropertyChanged(DataBindingHelper.GetPropertyChangedEventArgs(propertyName));
        }
        protected virtual void OnPropertyChanged(PropertyChangedEventArgs e)
        {
            if (null != PropertyChanged)
            {
                PropertyChanged(this, e);
            }
        }
        public void RefreshFeed()
        {
            this.RefreshFeed(false);
        }
        public void RefreshFeed(bool async)
        {
            try
            {
                if (async)
                {
                    this.myfeed.AsyncDownload();
                }
                else
                {
                    this.myfeed.Download();
                }
            }
            catch (Exception e)
            {
                _log.Debug("Exception in WindowsRssNewsFeed.RefreshFeed()", e);
            }
        }
    }
    public class WindowsRssNewsFeedCategory : INewsFeedCategory, IDisposable
    {
        private WindowsRssNewsFeedCategory() { ;}
        public WindowsRssNewsFeedCategory(IFeedFolder folder)
        {
            if (folder == null) throw new ArgumentNullException("folder");
            this.myfolder = folder;
        }
        public WindowsRssNewsFeedCategory(IFeedFolder folder, INewsFeedCategory category)
            : this(folder)
        {
            if (category != null)
            {
                this.AnyAttr = category.AnyAttr;
                this.downloadenclosures = category.downloadenclosures;
                this.downloadenclosuresSpecified = category.downloadenclosuresSpecified;
                this.enclosurealert = category.enclosurealert;
                this.enclosurealertSpecified = category.enclosurealertSpecified;
                this.listviewlayout = category.listviewlayout;
                this.markitemsreadonexit = category.markitemsreadonexit;
                this.markitemsreadonexitSpecified = category.markitemsreadonexitSpecified;
                this.maxitemage = category.maxitemage;
                this.refreshrate = category.refreshrate;
                this.refreshrateSpecified = category.refreshrateSpecified;
                this.stylesheet = category.stylesheet;
            }
        }
        private bool disposed = false;
        private IFeedFolder myfolder = null;
  ~WindowsRssNewsFeedCategory()
        {
            Dispose(false);
        }
        public void Dispose()
        {
            if (!disposed)
            {
                Dispose(true);
            }
        }
        public void Dispose(bool disposing)
        {
            lock (this)
            {
                if (myfolder != null)
                {
                    Marshal.ReleaseComObject(myfolder);
                }
                System.GC.SuppressFinalize(this);
                disposed = true;
            }
        }
        [XmlAttribute("mark-items-read-on-exit")]
        public bool markitemsreadonexit { get; set; }
        [XmlIgnore]
        public bool markitemsreadonexitSpecified { get; set; }
        [XmlAttribute("download-enclosures")]
        public bool downloadenclosures { get; set; }
        [XmlIgnore]
        public bool downloadenclosuresSpecified { get; set; }
        [XmlAttribute("enclosure-folder")]
        public string enclosurefolder { get { return null; } set { } }
        [XmlAttribute("listview-layout")]
        public string listviewlayout { get; set; }
        [XmlAttribute]
        public string stylesheet { get; set; }
        [XmlAttribute("refresh-rate")]
        public int refreshrate { get; set; }
        [XmlIgnore]
        public bool refreshrateSpecified { get; set; }
        [XmlAttribute("max-item-age", DataType = "duration")]
        public string maxitemage { get; set; }
        [XmlText]
        public string Value
        {
            get
            {
                return myfolder.Path;
            }
            set
            {
                if (!StringHelper.EmptyTrimOrNull(value))
                {
                    myfolder.Rename(value);
                }
            }
        }
        [XmlIgnore]
        public INewsFeedCategory parent { get; set; }
        [XmlAttribute("enclosure-alert"), DefaultValue(false)]
        public bool enclosurealert { get; set; }
        [XmlIgnore]
        public bool enclosurealertSpecified { get; set; }
        [XmlAnyAttribute]
        public XmlAttribute[] AnyAttr { get; set; }
        public override bool Equals(Object obj)
        {
            if (ReferenceEquals(this, obj))
            {
                return true;
            }
            WindowsRssNewsFeedCategory c = obj as WindowsRssNewsFeedCategory;
            if (c == null)
            {
                return false;
            }
            return this.myfolder.Path.Equals(c.myfolder.Path);
        }
        public override int GetHashCode()
        {
            return this.myfolder.Path.GetHashCode();
        }
    }
}
