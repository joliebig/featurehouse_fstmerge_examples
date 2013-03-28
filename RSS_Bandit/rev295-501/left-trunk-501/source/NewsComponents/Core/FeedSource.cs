using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net;
using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Xml;
using System.Xml.Schema;
using System.Xml.Serialization;
using System.Xml.Xsl;
using log4net;
using NewsComponents.Collections;
using NewsComponents.Feed;
using NewsComponents.Net;
using NewsComponents.News;
using NewsComponents.RelationCosmos;
using NewsComponents.Resources;
using NewsComponents.Search;
using NewsComponents.Storage;
using NewsComponents.Threading;
using NewsComponents.Utils;
using RssBandit.Common;
using RssBandit.Common.Logging;
using RssBandit.AppServices.Core;
namespace NewsComponents
{
    public enum FeedListFormat
    {
        OCS,
        OPML,
        NewsHandler,
        NewsHandlerLite,
    }
    public enum FeedSourceType {
        Google,
        NewsGator,
        WindowsRSS,
        DirectAccess
    }
    public class SubscriptionLocation {
        private SubscriptionLocation() { ; }
        public SubscriptionLocation(string location, NetworkCredential credentials)
        {
            this.Location = location;
            this.Credentials = credentials;
        }
        public SubscriptionLocation(string location) {
            this.Location = location;
            this.Credentials = CredentialCache.DefaultNetworkCredentials;
        }
        public string Location { get; set; }
        public NetworkCredential Credentials { get; set; }
    }
    public abstract class FeedSource: ISharedProperty
    {
        static FeedSource()
        {
            StringBuilder sb = new StringBuilder(200);
            sb.Append("{0}");
            sb.Append(" (.NET CLR ");
            sb.Append(Environment.Version);
            sb.Append("; ");
            sb.Append(Environment.OSVersion.ToString().Replace("Microsoft Windows ", "Win"));
            sb.Append("; http://www.rssbandit.org");
            sb.Append(")");
            userAgentTemplate = sb.ToString();
        }
        public static FeedSource CreateFeedSource(FeedSourceType handlerType, SubscriptionLocation location) {
            return CreateFeedSource(handlerType, location, DefaultConfiguration);
        }
        public static FeedSource CreateFeedSource(FeedSourceType handlerType,SubscriptionLocation location, INewsComponentsConfiguration configuration)
        {
   if (location == null)
    throw new ArgumentNullException("location");
            if (String.IsNullOrEmpty(location.Location))
                throw new ArgumentNullException("location.Location");
            FeedSource handler = null;
            switch (handlerType)
            {
                case FeedSourceType.DirectAccess:
                    handler = new BanditFeedSource(configuration, location);
                    break;
                case FeedSourceType.WindowsRSS:
                    handler = new WindowsRssFeedSource(configuration);
                    break;
                case FeedSourceType.Google:
                    handler = new GoogleReaderFeedSource(configuration, location);
                    break;
                case FeedSourceType.NewsGator:
                    handler = new NewsGatorFeedSource(configuration, location);
                    break;
                default:
                    break;
            }
            if (handler != null && (handler.Configuration.SearchIndexBehavior != SearchIndexBehavior.NoIndexing)
                && (handler.Configuration.SearchIndexBehavior == DefaultConfiguration.SearchIndexBehavior))
            {
                SearchHandler.AddNewsHandler(handler);
            }
            return handler;
        }
        private static INewsComponentsConfiguration defaultConfiguration;
        public static INewsComponentsConfiguration DefaultConfiguration {
            get { return defaultConfiguration ?? NewsComponentsConfiguration.Default; }
            set { defaultConfiguration = value; }
        }
        protected INewsComponentsConfiguration p_configuration = null;
        public INewsComponentsConfiguration Configuration
        {
            get
            {
                return this.p_configuration;
            }
        }
        protected static void ValidateAndThrow(INewsComponentsConfiguration configuration)
        {
            if (configuration == null)
                throw new ArgumentNullException("configuration");
            if (string.IsNullOrEmpty(configuration.ApplicationID))
                throw new InvalidOperationException(
                    "INewsComponentsConfiguration.ApplicationID cannot be null or empty.");
            if (configuration.CacheManager == null)
                throw new InvalidOperationException("INewsComponentsConfiguration.CacheManager cannot be null.");
            if (configuration.PersistedSettings == null)
                throw new InvalidOperationException("INewsComponentsConfiguration.PersistedSettings cannot be null.");
            if (string.IsNullOrEmpty(configuration.UserApplicationDataPath))
                throw new InvalidOperationException(
                    "INewsComponentsConfiguration.UserApplicationDataPath cannot be null or empty.");
            if (string.IsNullOrEmpty(configuration.UserLocalApplicationDataPath))
                throw new InvalidOperationException(
                    "INewsComponentsConfiguration.UserLocalApplicationDataPath cannot be null or empty.");
        }
     public static readonly Dictionary<string, object> MigrationProperties = new Dictionary<string, object>();
        internal CacheManager CacheHandler
        {
            get
            {
                return p_configuration.CacheManager;
            }
        }
        protected SubscriptionLocation location = null;
        protected AsyncWebRequest AsyncWebRequest = null;
        private static DateTime ApplicationStartTime = DateTime.Now;
        public bool DownloadIntervalReached
        {
            get
            {
                return (DateTime.Now - ApplicationStartTime).TotalMilliseconds >= this.RefreshRate;
            }
        }
        protected BackgroundDownloadManager enclosureDownloader;
        internal string CacheLocation
        {
            get
            {
                return this.CacheHandler.CacheLocation;
            }
        }
        protected RssParser rssParser;
        internal RssParser RssParserInstance
        {
            get
            {
                return this.rssParser;
            }
        }
        protected static LuceneSearch p_searchHandler;
        public static LuceneSearch SearchHandler
        {
            get
            {
                if (p_searchHandler == null)
                    p_searchHandler = new LuceneSearch(FeedSource.DefaultConfiguration);
                return p_searchHandler;
            }
            set
            {
                p_searchHandler = value;
            }
        }
        public static readonly List<INewsItem> EmptyItemList = new List<INewsItem>(0);
        private static readonly ILog _log = Log.GetLogger(typeof (FeedSource));
        private static readonly IRelationCosmos relationCosmos = RelationCosmosFactory.Create();
        private static readonly NewsChannelServices receivingNewsChannel = new NewsChannelServices();
     private class ProxyWrapper
     {
      private IWebProxy _proxy;
      public IWebProxy Proxy {
       get {
        if (_proxy == null)
         return WebRequest.DefaultWebProxy;
        return _proxy;
       }
       set { _proxy = value; }
      }
      public void ResetProxy() {
       _proxy = null;
      }
     }
     private static readonly ProxyWrapper globalProxy = new ProxyWrapper();
     public static IWebProxy GlobalProxy
     {
      get {
       lock (globalProxy)
        return globalProxy.Proxy;
      }
      set {
       lock (globalProxy)
        globalProxy.Proxy = value;
      }
     }
     public static void UseDefaultProxy()
     {
      lock (globalProxy)
       globalProxy.ResetProxy();
     }
     public IWebProxy Proxy
     {
      get { return GlobalProxy; }
     }
        private static bool setCookies = true;
        public static bool SetCookies
        {
            set
            {
                setCookies = value;
            }
            get
            {
                return setCookies;
            }
        }
        internal static bool buildRelationCosmos = true;
        public static bool BuildRelationCosmos
        {
            set
            {
                buildRelationCosmos = value;
                if (buildRelationCosmos == false)
                    relationCosmos.Clear();
            }
            get
            {
                return buildRelationCosmos;
            }
        }
        protected bool isOffline = false;
        public bool Offline
        {
            set
            {
                isOffline = value;
    if (RssParserInstance != null)
     RssParserInstance.Offline = value;
            }
            get
            {
                return isOffline;
            }
        }
        protected static bool p_traceMode = false;
        public static bool TraceMode
        {
            set
            {
                p_traceMode = value;
            }
            get
            {
                return p_traceMode;
            }
        }
        protected static void Trace(string formatString, params object[] paramArray)
        {
            if (p_traceMode)
                _log.Info(String.Format(formatString, paramArray));
        }
        private static bool unconditionalCommentRss;
        public static bool UnconditionalCommentRss
        {
            set
            {
                unconditionalCommentRss = value;
            }
            get
            {
                return unconditionalCommentRss;
            }
        }
        private static bool topStoriesModified = false;
        public static bool TopStoriesModified
        {
            get
            {
                return topStoriesModified;
            }
        }
        private class storyNdate
        {
            public storyNdate(string title, DateTime date)
            {
                storyTitle = title;
                firstSeen = date;
            }
            public readonly string storyTitle;
            public readonly DateTime firstSeen;
        }
        private static readonly Dictionary<string, storyNdate> TopStoryTitles = new Dictionary<string, storyNdate>();
        public static ICredentials CreateCredentialsFrom(INewsFeed feed)
        {
            if (feed != null && !string.IsNullOrEmpty(feed.authUser))
            {
                string u = null, p = null;
                GetFeedCredentials(feed, ref u, ref p);
                return CreateCredentialsFrom(feed.link, u, p);
            }
            return null;
        }
        public static ICredentials CreateCredentialsFrom(string url, string domainUser, string password)
        {
            ICredentials c = null;
            if (!string.IsNullOrEmpty(domainUser))
            {
                NetworkCredential credentials = CreateCredentialsFrom(domainUser, password);
                try
                {
                    Uri feedUri = new Uri(url);
                    CredentialCache cc = new CredentialCache();
                    cc.Add(feedUri, "Basic", credentials);
                    cc.Add(feedUri, "Digest", credentials);
                    cc.Add(feedUri, "NTLM", credentials);
                    c = cc;
                }
                catch (UriFormatException)
                {
                    c = credentials;
                }
            }
            return c;
        }
        public static NetworkCredential CreateCredentialsFrom(string domainUser, string password)
        {
            NetworkCredential c = null;
            if (domainUser != null)
            {
                NetworkCredential credentials;
                string[] aDomainUser = domainUser.Split(new char[] {'\\'});
                if (aDomainUser.GetLength(0) > 1)
                    credentials = new NetworkCredential(aDomainUser[1], password, aDomainUser[0]);
                else
                    credentials = new NetworkCredential(aDomainUser[0], password);
                c = credentials;
            }
            return c;
        }
        public static void SetFeedCredentials(INewsFeed feed, string user, string pwd)
        {
            if (feed == null) return;
            feed.authPassword = CryptHelper.EncryptB(pwd);
            feed.authUser = user;
        }
        public static void GetFeedCredentials(INewsFeed feed, ref string user, ref string pwd)
        {
            if (feed == null) return;
            pwd = CryptHelper.Decrypt(feed.authPassword);
            user = feed.authUser;
        }
        public ICredentials GetFeedCredentials(string feedUrl)
        {
            if (feedUrl != null && feedsTable.ContainsKey(feedUrl))
                return GetFeedCredentials(feedsTable[feedUrl]);
            return null;
        }
        public static ICredentials GetFeedCredentials(INewsFeed feed)
        {
            ICredentials c = null;
            if (feed != null && feed.authUser != null)
            {
                return CreateCredentialsFrom(feed);
            }
            return c;
        }
        public static void SetNntpServerCredentials(INntpServerDefinition sd, string user, string pwd)
        {
            NntpServerDefinition server = (NntpServerDefinition) sd;
            if (server == null) return;
            server.AuthPassword = CryptHelper.EncryptB(pwd);
            server.AuthUser = user;
        }
        public static void GetNntpServerCredentials(INntpServerDefinition sd, ref string user, ref string pwd)
        {
            NntpServerDefinition server = (NntpServerDefinition) sd;
            if (server == null) return;
            pwd = (server.AuthPassword != null ? CryptHelper.Decrypt(server.AuthPassword) : null);
            user = server.AuthUser;
        }
        public ICredentials GetNntpServerCredentials(string serverAccountName)
        {
            if (serverAccountName != null && nntpServers.ContainsKey(serverAccountName))
                return GetFeedCredentials(nntpServers[serverAccountName]);
            return null;
        }
        internal ICredentials GetNntpServerCredentials(INewsFeed f)
        {
            ICredentials c = null;
            if (f == null || ! RssHelper.IsNntpUrl(f.link))
                return c;
            try
            {
                Uri feedUri = new Uri(f.link);
                foreach (NntpServerDefinition nsd in this.nntpServers.Values)
                {
                    if (nsd.Server.Equals(feedUri.Authority))
                    {
                        c = this.GetNntpServerCredentials(nsd.Name);
                        break;
                    }
                }
            }
            catch (UriFormatException)
            {
                ;
            }
            return c;
        }
        public static ICredentials GetFeedCredentials(INntpServerDefinition sd)
        {
            ICredentials c = null;
            if (sd.AuthUser != null)
            {
                string u = null, p = null;
                GetNntpServerCredentials(sd, ref u, ref p);
                c = CreateCredentialsFrom(u, p);
            }
            return c;
        }
        protected TimeSpan maxitemage = new TimeSpan(90, 0, 0, 0);
        public TimeSpan MaxItemAge
        {
            get
            {
                return this.maxitemage;
            }
            set
            {
                this.maxitemage = value;
            }
        }
  [MethodImpl(MethodImplOptions.Synchronized)]
  public void ResetAllMaxItemAgeSettings()
  {
   string[] keys;
   lock (feedsTable)
   {
    keys = new string[feedsTable.Count];
    if (feedsTable.Count > 0)
     feedsTable.Keys.CopyTo(keys, 0);
   }
   for (int i = 0, len = keys.Length; i < len; i++)
   {
    INewsFeed f;
    if (feedsTable.TryGetValue(keys[i], out f))
    {
     f.maxitemage = null;
    }
   }
   foreach (INewsFeedCategory c in this.categories.Values)
   {
    c.maxitemage = null;
   }
  }
        static string stylesheet;
        public static string Stylesheet
        {
            get
            {
                return stylesheet;
            }
            set
            {
                stylesheet = value;
            }
        }
        static string enclosurefolder = String.Empty;
        public static string EnclosureFolder
        {
            get
            {
                return enclosurefolder;
            }
            set
            {
                enclosurefolder = value;
            }
        }
        protected readonly ArrayList podcastfileextensions = new ArrayList();
        public string PodcastFileExtensionsAsString
        {
            get
            {
                StringBuilder toReturn = new StringBuilder();
                foreach (string s in this.podcastfileextensions)
                {
                    if (!StringHelper.EmptyTrimOrNull(s))
                    {
                        toReturn.Append(s);
                        toReturn.Append(";");
                    }
                }
                return toReturn.ToString();
            }
            set
            {
                string[] fileexts = value.Split(new char[] {';', ' '});
                this.podcastfileextensions.Clear();
                foreach (string s in fileexts)
                {
                    this.podcastfileextensions.Add(s);
                }
            }
        }
        protected string podcastfolder;
        public string PodcastFolder
        {
            get
            {
                return this.podcastfolder;
            }
            set
            {
                this.podcastfolder = value;
            }
        }
        static bool markitemsreadonexit;
        public static bool MarkItemsReadOnExit
        {
            get
            {
                return markitemsreadonexit;
            }
            set
            {
                markitemsreadonexit = value;
            }
        }
     public const int DefaultEnclosureCacheSize = Int32.MaxValue;
  static int enclosurecachesize = DefaultEnclosureCacheSize;
        public static int EnclosureCacheSize
        {
            get
            {
                return enclosurecachesize;
            }
            set
            {
                enclosurecachesize = value;
            }
        }
     public const int DefaultNumEnclosuresToDownloadOnNewFeed = Int32.MaxValue;
  static int numtodownloadonnewfeed = DefaultNumEnclosuresToDownloadOnNewFeed;
        public static int NumEnclosuresToDownloadOnNewFeed
        {
            get
            {
                return numtodownloadonnewfeed;
            }
            set
            {
                numtodownloadonnewfeed = value;
            }
        }
        static bool createsubfoldersforenclosures;
        public static bool CreateSubfoldersForEnclosures
        {
            get
            {
                return createsubfoldersforenclosures;
            }
            set
            {
                createsubfoldersforenclosures = value;
            }
        }
        private static bool enclosurealert;
        public static bool EnclosureAlert
        {
            get
            {
                return enclosurealert;
            }
            set
            {
                enclosurealert = value;
            }
        }
        protected string listviewlayout;
        public string FeedColumnLayout
        {
            get
            {
                return this.listviewlayout;
            }
            set
            {
                this.listviewlayout = value;
            }
        }
        public const string DefaultUserAgent = "RssBandit/2.x";
        private static readonly string userAgentTemplate;
        private static string globalLongUserAgent;
        public static string UserAgentString(string userAgent)
        {
            if (string.IsNullOrEmpty(userAgent))
                return GlobalUserAgentString;
            return String.Format(userAgentTemplate, userAgent);
        }
        public static string GlobalUserAgentString
        {
            get
            {
                if (null == globalLongUserAgent)
                    globalLongUserAgent = UserAgentString(DefaultUserAgent);
                return globalLongUserAgent;
            }
        }
        private string useragent;
        public string UserAgent
        {
            get
            {
    if (String.IsNullOrEmpty(useragent)) {
     if (Configuration != null) {
      useragent = String.Format("{0}/{1}", Configuration.ApplicationID, Configuration.ApplicationVersion);
      globalLongUserAgent = UserAgentString(useragent);
     } else {
      return DefaultUserAgent;
     }
    }
                return useragent;
            }
            set
            {
                useragent = value;
                globalLongUserAgent = UserAgentString(useragent);
            }
        }
        public string FullUserAgent
        {
            get
            {
                return UserAgentString(this.UserAgent);
            }
        }
        protected IDictionary<string, INewsFeed> feedsTable = new SortedDictionary<string, INewsFeed>(UriHelper.Comparer);
        protected ReadOnlyDictionary<string, INewsFeed> readonly_feedsTable = null;
        protected IDictionary<string, INewsFeedCategory> categories = new SortedDictionary<string, INewsFeedCategory>();
        protected ReadOnlyDictionary<string, INewsFeedCategory> readonly_categories = null;
        protected FeedColumnLayoutCollection layouts = new FeedColumnLayoutCollection();
        protected readonly Dictionary<string, IFeedDetails> itemsTable =
            new Dictionary<string, IFeedDetails>();
        private IDictionary<string, INntpServerDefinition> nntpServers = new Dictionary<string, INntpServerDefinition>();
        private IDictionary<string, UserIdentity> identities = new Dictionary<string, UserIdentity>();
        public delegate void DownloadFeedStartedCallback(object sender, DownloadFeedCancelEventArgs e);
        public event DownloadFeedStartedCallback BeforeDownloadFeedStarted = null;
        [ComVisible(false)]
        public class DownloadFeedCancelEventArgs : CancelEventArgs
        {
            public DownloadFeedCancelEventArgs(Uri feed, bool cancel) : base(cancel)
            {
                this.feedUri = feed;
            }
            private readonly Uri feedUri;
            public Uri FeedUri
            {
                get
                {
                    return feedUri;
                }
            }
        }
        public delegate void UpdatedFeedCallback(object sender, UpdatedFeedEventArgs e);
        public event UpdatedFeedCallback OnUpdatedFeed = null;
        public delegate void DeletedCategoryCallback(object sender, CategoryEventArgs e);
        public event DeletedCategoryCallback OnDeletedCategory = null;
        public delegate void AddedCategoryCallback(object sender, CategoryEventArgs e);
        public event AddedCategoryCallback OnAddedCategory = null;
        public delegate void RenamedCategoryCallback(object sender, CategoryChangedEventArgs e);
        public event RenamedCategoryCallback OnRenamedCategory = null;
        public delegate void MovedCategoryCallback(object sender, CategoryChangedEventArgs e);
        public event MovedCategoryCallback OnMovedCategory = null;
        public delegate void AddedFeedCallback(object sender, FeedChangedEventArgs e);
        public event AddedFeedCallback OnAddedFeed = null;
        public delegate void DeletedFeedCallback(object sender, FeedDeletedEventArgs e);
        public event DeletedFeedCallback OnDeletedFeed = null;
        public delegate void RenamedFeedCallback(object sender, FeedRenamedEventArgs e);
        public event RenamedFeedCallback OnRenamedFeed = null;
        public delegate void MovedFeedCallback(object sender, FeedMovedEventArgs e);
        public event MovedFeedCallback OnMovedFeed = null;
        public delegate void DownloadedEnclosureCallback(object sender, DownloadItemEventArgs e);
        public event DownloadedEnclosureCallback OnDownloadedEnclosure = null;
        public delegate void UpdatedFaviconCallback(object sender, UpdatedFaviconEventArgs e);
        public event UpdatedFaviconCallback OnUpdatedFavicon = null;
        public class UpdatedFaviconEventArgs : EventArgs
        {
            public UpdatedFaviconEventArgs(string favicon, StringCollection feedUrls)
            {
                this.favicon = favicon;
                this.feedUrls = feedUrls;
            }
            private readonly string favicon;
            public string Favicon
            {
                get
                {
                    return this.favicon;
                }
            }
            private readonly StringCollection feedUrls;
            public StringCollection FeedUrls
            {
                get
                {
                    return this.feedUrls;
                }
            }
        }
        public class FeedChangedEventArgs : EventArgs
        {
            public string FeedUrl { get; set; }
            public FeedChangedEventArgs(string feedUrl)
            {
                this.FeedUrl = feedUrl;
            }
        }
        public class FeedDeletedEventArgs : FeedChangedEventArgs
        {
            public string Title { get; set; }
            public FeedDeletedEventArgs(string feedUrl, string title): base(feedUrl)
            {
                this.Title = title;
            }
        }
        public class FeedMovedEventArgs : FeedChangedEventArgs
        {
            public string NewCategory { get; set; }
            public FeedMovedEventArgs(string feedUrl, string newCategory): base(feedUrl)
            {
                this.NewCategory = newCategory;
            }
        }
        public class FeedRenamedEventArgs : FeedChangedEventArgs
        {
            public string NewName { get; set; }
            public FeedRenamedEventArgs(string feedUrl, string newName):base(feedUrl)
            {
                this.NewName = newName;
            }
        }
        public class CategoryEventArgs : EventArgs
        {
            public string CategoryName { get; set; }
            public CategoryEventArgs(string categoryName)
            {
                this.CategoryName = categoryName;
            }
        }
        public class CategoryChangedEventArgs : CategoryEventArgs
        {
            public string NewCategoryName { get; set; }
            public CategoryChangedEventArgs(string categoryName, string newCategoryName)
                : base(categoryName)
            {
                this.NewCategoryName = newCategoryName;
            }
        }
        public class UpdatedFeedEventArgs : EventArgs
        {
            public UpdatedFeedEventArgs(Uri requestUri, Uri newUri, RequestResult result, int priority,
                                        bool firstSuccessfulDownload)
            {
                this.requestUri = requestUri;
                this.newUri = newUri;
                this.result = result;
                this.priority = priority;
                this.firstSuccessfulDownload = firstSuccessfulDownload;
            }
            private readonly Uri requestUri;
            private readonly Uri newUri;
            public Uri UpdatedFeedUri
            {
                get
                {
                    return requestUri;
                }
            }
            public Uri NewFeedUri
            {
                get
                {
                    return newUri;
                }
            }
            private readonly RequestResult result;
            public RequestResult UpdateState
            {
                get
                {
                    return result;
                }
            }
            private readonly int priority;
            public int Priority
            {
                get
                {
                    return priority;
                }
            }
            private readonly bool firstSuccessfulDownload;
            public bool FirstSuccessfulDownload
            {
                get
                {
                    return firstSuccessfulDownload;
                }
            }
        }
        public delegate void UpdateFeedExceptionCallback(object sender, UpdateFeedExceptionEventArgs e);
        public event UpdateFeedExceptionCallback OnUpdateFeedException = null;
        public class UpdateFeedExceptionEventArgs : EventArgs
        {
            public UpdateFeedExceptionEventArgs(string requestUri, Exception e, int priority)
            {
                this.requestUri = requestUri;
                this.exception = e;
                this.priority = priority;
            }
            private readonly string requestUri;
            public string FeedUri
            {
                get
                {
                    return requestUri;
                }
            }
            private readonly Exception exception;
            public Exception ExceptionThrown
            {
                get
                {
                    return exception;
                }
            }
            private readonly int priority;
            public int Priority
            {
                get
                {
                    return priority;
                }
            }
        }
        public class UpdateFeedsEventArgs : EventArgs
        {
            public UpdateFeedsEventArgs(bool forced)
            {
                this.forced = forced;
            }
            private readonly bool forced;
            public bool ForcedRefresh
            {
                get
                {
                    return forced;
                }
            }
        }
        public class UpdateFeedEventArgs : UpdateFeedsEventArgs
        {
            public UpdateFeedEventArgs(Uri feed, bool forced, int priority) : base(forced)
            {
                this.feedUri = feed;
                this.priority = priority;
            }
            private readonly Uri feedUri;
            public Uri FeedUri
            {
                get
                {
                    return feedUri;
                }
            }
            private readonly int priority;
            public int Priority
            {
                get
                {
                    return priority;
                }
            }
        }
        public delegate void UpdateFeedsStartedHandler(object sender, UpdateFeedsEventArgs e);
        public event UpdateFeedsStartedHandler UpdateFeedsStarted = null;
        public delegate void UpdateFeedStartedHandler(object sender, UpdateFeedEventArgs e);
        public event UpdateFeedStartedHandler UpdateFeedStarted = null;
        public event EventHandler OnAllAsyncRequestsCompleted = null;
        public delegate void NewsItemSearchResultEventHandler(object sender, NewsItemSearchResultEventArgs e);
        public delegate void SearchFinishedEventHandler(object sender, SearchFinishedEventArgs e);
        public event NewsItemSearchResultEventHandler NewsItemSearchResult;
        public event SearchFinishedEventHandler SearchFinished;
        [ComVisible(false)]
        public class FeedSearchResultEventArgs : CancelEventArgs
        {
            public FeedSearchResultEventArgs(
                NewsFeed f, object tag, bool cancel) : base(cancel)
            {
                this.Feed = f;
                this.Tag = tag;
            }
            public NewsFeed Feed;
            public object Tag;
        }
        [ComVisible(false)]
        public class NewsItemSearchResultEventArgs : CancelEventArgs
        {
            public NewsItemSearchResultEventArgs(
                List<INewsItem> items, object tag, bool cancel) : base(cancel)
            {
                this.NewsItems = items;
                this.Tag = tag;
            }
            public List<INewsItem> NewsItems;
            public object Tag;
        }
        public class SearchFinishedEventArgs : EventArgs
        {
            public SearchFinishedEventArgs(
                object tag, FeedInfoList matchingFeeds, int matchingFeedsCount, int matchingItemsCount) :
                    this(tag, matchingFeeds, new List<INewsItem>(), matchingFeedsCount, matchingItemsCount)
            {
                List<INewsItem> temp = new List<INewsItem>();
                foreach (FeedInfo fi in matchingFeeds)
                {
                    foreach (INewsItem ni in fi.ItemsList)
                    {
                        if (ni is SearchHitNewsItem)
                            temp.Add(ni);
                        else
                            temp.Add(new SearchHitNewsItem(ni));
                    }
                    fi.ItemsList.Clear();
                    fi.ItemsList.AddRange(temp);
                    this.MatchingItems.AddRange(temp);
                    temp.Clear();
                }
            }
            public SearchFinishedEventArgs(
                object tag, FeedInfoList matchingFeeds, IEnumerable<INewsItem> matchingNewsItems, int matchingFeedsCount,
                int matchingItemsCount)
            {
                this.MatchingFeedsCount = matchingFeedsCount;
                this.MatchingItemsCount = matchingItemsCount;
                this.MatchingFeeds = matchingFeeds;
                this.MatchingItems = new List<INewsItem>(matchingNewsItems);
                this.Tag = tag;
            }
            public readonly int MatchingFeedsCount;
            public readonly int MatchingItemsCount;
            public readonly object Tag;
            public readonly FeedInfoList MatchingFeeds;
            public readonly List<INewsItem> MatchingItems;
        }
        private const int maxItemsPerSearchResult = 10;
        private List<INewsItem> SearchNewsItemsHelper(IEnumerable<INewsItem> prevMatchItems,
                                                     SearchCriteriaCollection criteria, FeedDetailsInternal fi,
                                                     FeedDetailsInternal fiMatchedItems, ref int itemmatches,
                                                     ref int feedmatches, object tag)
        {
            List<INewsItem> matchItems = new List<INewsItem>(maxItemsPerSearchResult);
            matchItems.AddRange(prevMatchItems);
            bool cancel = false;
            bool feedmatch = false;
            foreach (NewsItem item in fi.ItemsList)
            {
                if (criteria.Match(item))
                {
                    feedmatch = true;
                    matchItems.Add(item);
                    fiMatchedItems.ItemsList.Add(item);
                    itemmatches++;
                    if ((itemmatches%50) == 0)
                    {
                        cancel = RaiseNewsItemSearchResultEvent(matchItems, tag);
                        matchItems.Clear();
                    }
                    if (cancel) throw new InvalidOperationException("SEARCH CANCELLED");
                }
            }
            if (feedmatch) feedmatches++;
            return matchItems;
        }
        public void SearchNewsItems(SearchCriteriaCollection criteria, INewsFeed[] scope, object tag, string cultureName,
                                    bool returnFullItemText)
        {
            int feedmatches = 0;
            int itemmatches = 0;
            IList<INewsItem> unreturnedMatchItems = new List<INewsItem>();
            FeedInfoList fiList = new FeedInfoList(String.Empty);
            Exception ex;
            bool valid = SearchHandler.ValidateSearchCriteria(criteria, cultureName, out ex);
            if (ex != null)
            {
                fiList.Add((FeedInfo) CreateHelpNewsItemFromException(ex).FeedDetails);
                feedmatches = fiList.Count;
                unreturnedMatchItems = fiList.GetAllNewsItems();
                itemmatches = unreturnedMatchItems.Count;
            }
            if (valid)
            {
                try
                {
                    LuceneSearch.Result r = SearchHandler.ExecuteSearch(criteria, scope, new List<FeedSource>() { this }, cultureName);
                    SearchCriteriaProperty criteriaProperty = null;
                    foreach (ISearchCriteria sc in criteria)
                    {
                        criteriaProperty = sc as SearchCriteriaProperty;
                        if (criteriaProperty != null &&
                            PropertyExpressionKind.Unread == criteriaProperty.WhatKind)
                            break;
                    }
                    ItemReadState readState = ItemReadState.Ignore;
                    if (criteriaProperty != null)
                    {
                        if (criteriaProperty.BeenRead)
                            readState = ItemReadState.BeenRead;
                        else
                            readState = ItemReadState.Unread;
                    }
                    if (r != null && r.ItemMatchCount > 0)
                    {
                        SearchHitNewsItem[] nids = new SearchHitNewsItem[r.ItemsMatched.Count];
                        r.ItemsMatched.CopyTo(nids, 0);
                        fiList.AddRange(FindNewsItems(nids, readState, returnFullItemText));
                        feedmatches = fiList.Count;
                        unreturnedMatchItems = fiList.GetAllNewsItems();
                        itemmatches = unreturnedMatchItems.Count;
                    }
                }
                catch (Exception searchEx)
                {
                    fiList.Add((FeedInfo) CreateHelpNewsItemFromException(searchEx).FeedDetails);
                    feedmatches = fiList.Count;
                    unreturnedMatchItems = fiList.GetAllNewsItems();
                    itemmatches = unreturnedMatchItems.Count;
                }
            }
            RaiseSearchFinishedEvent(tag, fiList, unreturnedMatchItems, feedmatches, itemmatches);
        }
        private static ExceptionalNewsItem CreateHelpNewsItemFromException(Exception e)
        {
            if (e == null)
                throw new ArgumentNullException("e");
            NewsFeed f = new NewsFeed();
            f.link = "http://www.rssbandit.org/docs/";
            f.title = ComponentsText.ExceptionHelpFeedTitle;
            ExceptionalNewsItem newsItem =
                new ExceptionalNewsItem(f, ComponentsText.ExceptionHelpFeedItemTitle(e.GetType().Name),
                                        (e.HelpLink ?? "http://www.rssbandit.org/docs/"),
                                        e.Message, e.Source, DateTime.Now.ToUniversalTime(), Guid.NewGuid().ToString());
            newsItem.Subject = e.GetType().Name;
            newsItem.CommentStyle = SupportedCommentStyle.None;
            newsItem.Enclosures = GetList<IEnclosure>.Empty;
            newsItem.WatchComments = false;
            newsItem.Language = CultureInfo.CurrentUICulture.Name;
            newsItem.HasNewComments = false;
            FeedInfo fi = new FeedInfo(f.id, f.cacheurl, new List<INewsItem>(new NewsItem[] {newsItem}),
                                       f.title, f.link, ComponentsText.ExceptionHelpFeedDesc,
                                       new Dictionary<XmlQualifiedName, string>(1), newsItem.Language);
            newsItem.FeedDetails = fi;
            return newsItem;
        }
        public void SearchNewsItems(SearchCriteriaCollection criteria, NewsFeed[] scope, object tag)
        {
            int feedmatches = 0;
            int itemmatches = 0;
            int feedcounter = 0;
            List<INewsItem> unreturnedMatchItems = new List<INewsItem>();
            FeedInfoList fiList = new FeedInfoList(String.Empty);
            try
            {
                FeedInfo[] feedInfos;
                if (scope.Length == 0)
                {
                    lock (itemsTable)
                    {
                        feedInfos = new FeedInfo[itemsTable.Count];
                        itemsTable.Values.CopyTo(feedInfos, 0);
                    }
                    foreach (FeedInfo fi in feedInfos)
                    {
                        FeedInfo fiClone = fi.Clone(false);
                        unreturnedMatchItems =
                            SearchNewsItemsHelper(unreturnedMatchItems, criteria, fi, fiClone, ref itemmatches,
                                                  ref feedmatches, tag);
                        feedcounter++;
                        if ((feedcounter%5) == 0)
                        {
                            bool cancel = RaiseNewsItemSearchResultEvent(unreturnedMatchItems, tag);
                            unreturnedMatchItems.Clear();
                            if (cancel)
                                break;
                        }
                        if (fiClone.ItemsList.Count != 0)
                        {
                            fiList.Add(fiClone);
                        }
                    }
                }
                else
                {
                    lock (itemsTable)
                    {
                        feedInfos = new FeedInfo[scope.Length];
                        for (int i = 0; i < scope.Length; i++)
                        {
                            feedInfos[i] = (FeedInfo) itemsTable[scope[i].link];
                        }
                    }
                    foreach (FeedInfo fi in feedInfos)
                    {
                        if (fi != null)
                        {
                            FeedInfo fiClone = fi.Clone(false);
                            unreturnedMatchItems =
                                SearchNewsItemsHelper(unreturnedMatchItems, criteria, fi, fiClone, ref itemmatches,
                                                      ref feedmatches, tag);
                            feedcounter++;
                            if ((feedcounter%5) == 0)
                            {
                                bool cancel = RaiseNewsItemSearchResultEvent(unreturnedMatchItems, tag);
                                unreturnedMatchItems.Clear();
                                if (cancel)
                                    break;
                            }
                            if (fiClone.ItemsList.Count != 0)
                            {
                                fiList.Add(fiClone);
                            }
                        }
                    }
                }
                if (unreturnedMatchItems.Count > 0)
                {
                    RaiseNewsItemSearchResultEvent(unreturnedMatchItems, tag);
                }
            }
            catch (InvalidOperationException ioe)
            {
                Trace("SearchNewsItems() casued InvalidOperationException: {0}", ioe);
            }
            RaiseSearchFinishedEvent(tag, fiList, feedmatches, itemmatches);
        }
        public void SearchRemoteFeed(string searchFeedUrl, object tag)
        {
            int feedmatches;
            int itemmatches;
            List<INewsItem> unreturnedMatchItems = RssParser.DownloadItemsFromFeed(searchFeedUrl);
            RaiseNewsItemSearchResultEvent(unreturnedMatchItems, tag);
            feedmatches = 1;
            itemmatches = unreturnedMatchItems.Count;
            FeedInfo fi =
                new FeedInfo(String.Empty, String.Empty, unreturnedMatchItems, String.Empty, String.Empty, String.Empty,
                             new Dictionary<XmlQualifiedName, string>(), String.Empty);
            FeedInfoList fil = new FeedInfoList(String.Empty);
            fil.Add(fi);
            RaiseSearchFinishedEvent(tag, fil, feedmatches, itemmatches);
        }
        public void SearchFeeds(SearchCriteriaCollection criteria, NewsFeed[] scope, object tag)
        {
            throw new NotSupportedException();
        }
        private bool RaiseNewsItemSearchResultEvent(IEnumerable<INewsItem> matchItems, object tag)
        {
            try
            {
                if (NewsItemSearchResult != null)
                {
                    NewsItemSearchResultEventArgs ea =
                        new NewsItemSearchResultEventArgs(new List<INewsItem>(matchItems), tag, false);
                    NewsItemSearchResult(this, ea);
                    return ea.Cancel;
                }
            }
            catch
            {
            }
            return false;
        }
        private void RaiseSearchFinishedEvent(object tag, FeedInfoList matchingFeeds, int matchingFeedsCount,
                                              int matchingItemsCount)
        {
            try
            {
                if (SearchFinished != null)
                {
                    SearchFinished(this,
                                   new SearchFinishedEventArgs(tag, matchingFeeds, matchingFeedsCount,
                                                               matchingItemsCount));
                }
            }
            catch (Exception e)
            {
                Trace("SearchFinished() event code raises exception: {0}", e);
            }
        }
        private void RaiseSearchFinishedEvent(object tag, FeedInfoList matchingFeeds, IEnumerable<INewsItem> matchingItems,
                                              int matchingFeedsCount, int matchingItemsCount)
        {
            try
            {
                if (SearchFinished != null)
                {
                    SearchFinished(this,
                                   new SearchFinishedEventArgs(tag, matchingFeeds, matchingItems, matchingFeedsCount,
                                                               matchingItemsCount));
                }
            }
            catch (Exception e)
            {
                Trace("SearchFinished() event code raises exception: {0}", e);
            }
        }
        public INewsItem FindNewsItem(SearchHitNewsItem nid)
        {
            if (nid != null)
            {
                FeedInfo fi = this.itemsTable[nid.FeedLink] as FeedInfo;
                if (fi != null)
                {
                    List<INewsItem> items = new List<INewsItem>(fi.ItemsList);
                    foreach (INewsItem ni in items)
                    {
                        if (ni.Id.Equals(nid.Id))
                        {
                            return ni;
                        }
                    }
                }
            }
            return null;
        }
        public FeedInfoList FindNewsItems(SearchHitNewsItem[] nids)
        {
            return this.FindNewsItems(nids, ItemReadState.Ignore, false);
        }
        public FeedInfoList FindNewsItems(SearchHitNewsItem[] nids, ItemReadState readState, bool returnFullItemText)
        {
            FeedInfoList fiList = new FeedInfoList(String.Empty);
            Dictionary<string, FeedInfo> matchedFeeds = new Dictionary<string, FeedInfo>();
            Dictionary<string, List<INewsItem> > itemlists = new Dictionary<string, List<INewsItem> >();
            foreach (SearchHitNewsItem nid in nids)
            {
                IFeedDetails fdi;
                FeedInfo fi, originalfi = null;
                if (this.itemsTable.TryGetValue(nid.FeedLink, out fdi))
                    originalfi = fdi as FeedInfo;
                if (originalfi != null)
                {
                    List<INewsItem> items;
                    if (matchedFeeds.ContainsKey(nid.FeedLink))
                    {
                        fi = matchedFeeds[nid.FeedLink];
                        items = itemlists[nid.FeedLink];
                    }
                    else
                    {
                        fi = originalfi.Clone(false);
                        items = new List<INewsItem>(originalfi.ItemsList);
                        matchedFeeds.Add(nid.FeedLink, fi);
                        itemlists.Add(nid.FeedLink, items);
                    }
                    bool beenRead = (readState == ItemReadState.BeenRead);
                    foreach (NewsItem ni in items)
                    {
                        if (ni.Id.Equals(nid.Id))
                        {
                            if (readState == ItemReadState.Ignore ||
                                ni.BeenRead == beenRead)
                            {
                                nid.BeenRead = ni.BeenRead;
                                if (returnFullItemText && !nid.HasContent)
                                    this.GetCachedContentForItem(nid);
                                fi.ItemsList.Add(nid);
                                nid.FeedDetails = fi;
                            }
                            break;
                        }
                    }
                }
            }
            foreach (FeedInfo f in matchedFeeds.Values)
            {
                if (f.ItemsList.Count > 0)
                {
                    fiList.Add(f);
                }
            }
            return fiList;
        }
        internal string applicationName = "NewsComponents";
        public FeedColumnLayoutCollection ColumnLayouts
        {
            get
            {
                if (layouts == null)
                {
                    layouts = new FeedColumnLayoutCollection();
                }
                return layouts;
            }
        }
        public static string CategorySeparator = @"\";
        public IDictionary<string, INntpServerDefinition> NntpServers
        {
            [DebuggerStepThrough]
            get
            {
                if (this.nntpServers == null)
                {
                    this.nntpServers = new Dictionary<string, INntpServerDefinition>();
                }
                return this.nntpServers;
            }
        }
        public IDictionary<string, UserIdentity> UserIdentity
        {
            [DebuggerStepThrough]
            get
            {
                if (this.identities == null)
                {
                    this.identities = new Dictionary<string, UserIdentity>();
                }
                return this.identities;
            }
        }
     public static int DefaultRefreshRate = 60*60*1000;
        public virtual int RefreshRate
        {
            get
            {
                return p_configuration.RefreshRate;
            }
        }
  public void ResetAllRefreshRateSettings()
  {
   string[] keys;
   lock (feedsTable)
   {
    keys = new string[feedsTable.Count];
    if (feedsTable.Count > 0)
     feedsTable.Keys.CopyTo(keys, 0);
   }
   for (int i = 0, len = keys.Length; i < len; i++)
   {
    INewsFeed f = null;
    if (feedsTable.TryGetValue(keys[i], out f))
    {
     f.refreshrate = 0;
     f.refreshrateSpecified = false;
    }
   }
   foreach (INewsFeedCategory c in this.categories.Values)
   {
    c.refreshrate = 0;
    c.refreshrateSpecified = false;
   }
  }
        public static bool validationErrorOccured;
        public bool FeedsListOK
        {
            get
            {
                return !validationErrorOccured;
            }
        }
        protected string[] GetFeedsTableKeys()
        {
            string[] keys;
            lock (feedsTable)
            {
                keys = new string[feedsTable.Count];
                if (feedsTable.Count > 0)
                    feedsTable.Keys.CopyTo(keys, 0);
            }
            return keys;
        }
        public IList<RelationHRefEntry> GetTopStories(TimeSpan since, int numStories)
        {
            string[] keys = GetFeedsTableKeys();
            Dictionary<RelationHRefEntry, List<RankedNewsItem> > allLinks =
                new Dictionary<RelationHRefEntry, List<RankedNewsItem> >();
            for (int i = 0; i < keys.Length; i++)
            {
                if (!itemsTable.ContainsKey(keys[i]))
                {
                    continue;
                }
                FeedInfo fi = (FeedInfo) itemsTable[keys[i]];
                List<INewsItem> items =
                    fi.ItemsList.FindAll(delegate(INewsItem item)
                                             {
                                                 return (DateTime.Now - item.Date) < since;
                                             });
                foreach (INewsItem item in items)
                {
                    float score = 1.0f - (DateTime.Now.Ticks - item.Date.Ticks)*1.0f/since.Ticks;
                    RankedNewsItem rni = new RankedNewsItem(item, score);
                    foreach (string url in item.OutGoingLinks)
                    {
                        RelationHRefEntry href = new RelationHRefEntry(url, null, 0.0f);
                        if (!allLinks.ContainsKey(href))
                        {
                            allLinks[href] = new List<RankedNewsItem>();
                        }
                        allLinks[href].Add(rni);
                    }
                }
            }
            List<RelationHRefEntry> weightedLinks = new List<RelationHRefEntry>();
            foreach (KeyValuePair<RelationHRefEntry, List<RankedNewsItem> > linkNvotes in allLinks)
            {
                Dictionary<string, float> votesPerFeed = new Dictionary<string, float>();
                foreach (RankedNewsItem voteItem in linkNvotes.Value)
                {
                    string feedLink = voteItem.Item.FeedLink;
                    if (votesPerFeed.ContainsKey(feedLink))
                    {
                        votesPerFeed[feedLink] = Math.Min(votesPerFeed[feedLink], voteItem.Score);
                    }
                    else
                    {
                        votesPerFeed.Add(feedLink, voteItem.Score);
                        linkNvotes.Key.References.Add(voteItem.Item);
                    }
                }
                float totalScore = 0.0f;
                foreach (float value in votesPerFeed.Values)
                {
                    totalScore += value;
                }
                linkNvotes.Key.Score = totalScore;
                weightedLinks.Add(linkNvotes.Key);
            }
            weightedLinks.Sort(delegate(RelationHRefEntry x, RelationHRefEntry y)
                                   {
                                       return y.Score.CompareTo(x.Score);
                                   });
            weightedLinks = weightedLinks.GetRange(0, Math.Min(numStories, weightedLinks.Count));
            int numTitlesToDownload = Math.Min(numStories, weightedLinks.Count);
   ManualResetEvent eventX = new ManualResetEvent(false);
   try
   {
    foreach (RelationHRefEntry rhf in weightedLinks) {
     if (TopStoryTitles.ContainsKey(rhf.HRef)) {
      rhf.Text = TopStoryTitles[rhf.HRef].storyTitle;
      Interlocked.Decrement(ref numTitlesToDownload);
     } else {
                        RelationHRefEntry weightedLink = rhf;
      PriorityThreadPool.QueueUserWorkItem(
       delegate
        {
         try {
          string title =
           HtmlHelper.FindTitle(weightedLink.HRef, weightedLink.HRef, this.Proxy,
                                CredentialCache.DefaultCredentials);
          weightedLink.Text = title;
          if (!title.Equals(weightedLink.HRef) &&
           !TopStoryTitles.ContainsKey(weightedLink.HRef))
          {
           TopStoryTitles.Add(weightedLink.HRef, new storyNdate(title, DateTime.Now));
           topStoriesModified = true;
          }
         } finally {
          Interlocked.Decrement(ref numTitlesToDownload);
          if (numTitlesToDownload <= 0) {
           if (eventX != null)
            eventX.Set();
          }
         }
        },
       weightedLink,
       (int) ThreadPriority.Normal);
     }
    }
    if (numTitlesToDownload > 0) {
     eventX.WaitOne(Timeout.Infinite, true);
    }
    return weightedLinks;
   }
   finally
   {
    IDisposable disposable = eventX;
    disposable.Dispose();
    eventX = null;
   }
        }
        public IEnumerable<NewsFeed> GetNonInternetFeeds()
        {
            List<NewsFeed> toReturn = new List<NewsFeed>();
            if (this.feedsTable.Count == 0)
                return toReturn;
            string[] keys = new string[this.feedsTable.Keys.Count];
            this.feedsTable.Keys.CopyTo(keys, 0);
            foreach (string url in keys)
            {
                try
                {
                    Uri uri = new Uri(url);
                    if (uri.IsFile || uri.IsUnc || !uri.Authority.Contains(".")) {
                        INewsFeed f = null;
                        if (feedsTable.TryGetValue(url, out f))
                        {
                            toReturn.Add(new NewsFeed(f));
                        }
                    }
                }
                catch (Exception e)
                {
                    _log.Error("Exception in GetNonInternetFeeds()", e);
                }
            }
            return toReturn;
        }
        private void LoadCachedTopStoryTitles()
        {
            try
            {
    string topStories = Path.Combine(Configuration.UserApplicationDataPath, "top-stories.xml");
                if (File.Exists(topStories))
                {
                    XmlDocument doc = new XmlDocument();
                    doc.Load(topStories);
                    foreach (XmlElement story in doc.SelectNodes(""))
                    {
                        TopStoryTitles.Add(story.Attributes["url"].Value,
                                           new storyNdate(story.Attributes["title"].Value,
                                                          XmlConvert.ToDateTime(story.Attributes["firstSeen"].Value, XmlDateTimeSerializationMode.Utc))
                            );
                    }
                }
            }
            catch (Exception e)
            {
                _log.Error("Error in LoadCachedTopStoryTitles()", e);
            }
        }
        public static void SaveCachedTopStoryTitles()
        {
            DateTime TwoWeeksAgo = DateTime.Now.Subtract(new TimeSpan(14, 0, 0, 0));
            topStoriesModified = false;
            try
            {
    XmlWriter writer = XmlWriter.Create(Path.Combine(DefaultConfiguration.UserApplicationDataPath, "top-stories.xml"));
                writer.WriteStartDocument();
                writer.WriteStartElement("stories");
                foreach (KeyValuePair<string, storyNdate> story in TopStoryTitles)
                {
                    if (story.Value.firstSeen > TwoWeeksAgo)
                    {
                        writer.WriteStartElement("story");
                        writer.WriteAttributeString("url", story.Key);
                        writer.WriteAttributeString("title", story.Value.storyTitle);
                        writer.WriteAttributeString("firstSeen", XmlConvert.ToString(story.Value.firstSeen, XmlDateTimeSerializationMode.Utc));
                        writer.WriteEndElement();
                    }
                }
                writer.WriteEndDocument();
                writer.Flush();
                writer.Close();
            }
            catch (Exception e)
            {
                _log.Error("Error in SaveCachedTopStoryTitles()", e);
            }
        }
        public abstract void LoadFeedlist();
        public abstract void BootstrapAndLoadFeedlist(feeds feedlist);
        public void DisableFeed(string feedUrl)
        {
            if (!feedsTable.ContainsKey(feedUrl))
            {
                return;
            }
            INewsFeed f = feedsTable[feedUrl];
            f.refreshrate = 0;
            f.refreshrateSpecified = true;
        }
        public virtual void DeleteItem(INewsItem item)
        {
            if (item.Feed != null && !string.IsNullOrEmpty(item.Feed.link))
            {
                FeedInfo fi = itemsTable[item.Feed.link] as FeedInfo;
                if (fi != null)
                {
                    lock (fi.itemsList)
                    {
                        item.Feed.AddDeletedStory(item.Id);
                        fi.itemsList.Remove(item);
                    }
                }
            }
        }
        public void DeleteAllItemsInFeed(INewsFeed feed)
        {
            if (feed != null && !string.IsNullOrEmpty(feed.link) && feedsTable.ContainsKey(feed.link))
            {
                FeedInfo fi = itemsTable[feed.link] as FeedInfo;
                if (fi == null)
                {
                    fi = (FeedInfo) this.GetFeed(feed);
                }
                if (fi != null)
                {
                    lock (fi.itemsList)
                    {
                        foreach (NewsItem item in fi.itemsList)
                        {
                            feed.AddDeletedStory(item.Id);
                        }
                        fi.itemsList.Clear();
                    }
                }
                SearchHandler.IndexRemove(feed.id);
            }
        }
        public void DeleteAllItemsInFeed(string feedUrl)
        {
            if (feedsTable.ContainsKey(feedUrl))
            {
                this.DeleteAllItemsInFeed(feedsTable[feedUrl]);
            }
        }
        public void RestoreDeletedItem(INewsItem item)
        {
            if (item.Feed != null && !string.IsNullOrEmpty(item.Feed.link) && feedsTable.ContainsKey(item.Feed.link))
            {
                FeedInfo fi = itemsTable[item.Feed.link] as FeedInfo;
                if (fi == null)
                {
                    fi = (FeedInfo) this.GetFeed(item.Feed);
                }
                if (fi != null)
                {
                    lock (fi.itemsList)
                    {
                        item.Feed.RemoveDeletedStory(item.Id);
                        fi.itemsList.Add(item);
                    }
                }
                SearchHandler.IndexAdd(item);
            }
        }
        public void RestoreDeletedItem(IList<INewsItem> deletedItems)
        {
            foreach (INewsItem item in deletedItems)
            {
                this.RestoreDeletedItem(item);
            }
            SearchHandler.IndexAdd(deletedItems);
        }
        public virtual void SaveFeedList(Stream feedStream)
        {
            this.SaveFeedList(feedStream, FeedListFormat.NewsHandler);
        }
        private static XmlElement CreateCategoryHive(XmlElement startNode, string category)
        {
            if (category == null || category.Length == 0 || startNode == null) return startNode;
            string[] catHives = category.Split(CategorySeparator.ToCharArray());
            XmlElement n;
            bool wasNew = false;
            foreach (string catHive in catHives)
            {
                if (!wasNew)
                {
                    string xpath = "child::outline[@title=" + buildXPathString(catHive) + " and (count(@*)= 1)]";
                    n = (XmlElement) startNode.SelectSingleNode(xpath);
                }
                else
                {
                    n = null;
                }
                if (n == null)
                {
                    n = startNode.OwnerDocument.CreateElement("outline");
                    n.SetAttribute("title", catHive);
                    startNode.AppendChild(n);
                    wasNew = true;
                }
                startNode = n;
            }
            return startNode;
        }
        private static listviewLayout FindLayout(IEquatable<string> id, IEnumerable<listviewLayout> layouts)
        {
            foreach (listviewLayout layout in layouts)
            {
                if (id.Equals(layout.ID))
                    return layout;
            }
            return null;
        }
        public static string buildXPathString(string input)
        {
            string[] components = input.Split(new char[] {'\''});
            string result = "";
            result += "concat(''";
            for (int i = 0; i < components.Length; i++)
            {
                result += ", '" + components[i] + "'";
                if (i < components.Length - 1)
                {
                    result += ", \"'\"";
                }
            }
            result += ")";
            Console.WriteLine(result);
            return result;
        }
        public virtual void SaveFeedList(Stream feedStream, FeedListFormat format)
        {
            this.SaveFeedList(feedStream, format, this.feedsTable, true);
        }
        public virtual void SaveFeedList(Stream feedStream, FeedListFormat format, IDictionary<string, INewsFeed> feeds,
                                 bool includeEmptyCategories)
        {
            if (feedStream == null)
                throw new ArgumentNullException("feedStream");
            if (format.Equals(FeedListFormat.OPML))
            {
                XmlDocument opmlDoc = new XmlDocument();
                opmlDoc.LoadXml("<opml version='1.0'><head /><body /></opml>");
                Dictionary<string, XmlElement> categoryTable = new Dictionary<string, XmlElement>(categories.Count);
                foreach (NewsFeed f in feeds.Values)
                {
                    XmlElement outline = opmlDoc.CreateElement("outline");
                    outline.SetAttribute("title", f.title);
                    outline.SetAttribute("xmlUrl", f.link);
                    outline.SetAttribute("type", "rss");
                    outline.SetAttribute("text", f.title);
                   IFeedDetails fi;
                    bool success = itemsTable.TryGetValue(f.link, out fi);
     if(success){
      outline.SetAttribute("htmlUrl", fi.Link);
      outline.SetAttribute("description", fi.Description);
     }
                    string category = (f.category ?? String.Empty);
                    XmlElement catnode;
                    if (categoryTable.ContainsKey(category))
                        catnode = categoryTable[category];
                    else
                    {
                        catnode = CreateCategoryHive((XmlElement) opmlDoc.DocumentElement.ChildNodes[1], category);
                        categoryTable.Add(category, catnode);
                    }
                    catnode.AppendChild(outline);
                }
                if (includeEmptyCategories)
                {
                    foreach (string category in this.categories.Keys)
                    {
                        CreateCategoryHive((XmlElement) opmlDoc.DocumentElement.ChildNodes[1], category);
                    }
                }
                XmlTextWriter opmlWriter = new XmlTextWriter(feedStream, Encoding.UTF8);
                opmlWriter.Formatting = Formatting.Indented;
                opmlDoc.Save(opmlWriter);
            }
            else if (format.Equals(FeedListFormat.NewsHandler) || format.Equals(FeedListFormat.NewsHandlerLite))
            {
                XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof (feeds));
                feeds feedlist = new feeds();
                if (feeds != null)
                {
                    feedlist.listviewlayout = this.listviewlayout;
                    feedlist.podcastfolder = this.PodcastFolder;
                    feedlist.podcastfileexts = this.PodcastFileExtensionsAsString;
     feedlist.markitemsreadonexitSpecified = false;
     feedlist.downloadenclosuresSpecified = false;
     feedlist.enclosurealertSpecified = false;
     feedlist.refreshrateSpecified = false;
     feedlist.createsubfoldersforenclosuresSpecified = false;
     feedlist.numtodownloadonnewfeedSpecified = false;
     feedlist.enclosurecachesizeSpecified = false;
                    foreach (INewsFeed f in feeds.Values)
                    {
                        if (f is NewsFeed)
                            feedlist.feed.Add((NewsFeed)f);
                        else
                            feedlist.feed.Add(new NewsFeed(f));
                        if (itemsTable.ContainsKey(f.link))
                        {
                            IList<INewsItem> items = itemsTable[f.link].ItemsList;
                            if (!format.Equals(FeedListFormat.NewsHandlerLite))
                            {
                                foreach (INewsItem ri in items)
                                {
                                    if (ri.BeenRead && !f.storiesrecentlyviewed.Contains(ri.Id))
                                    {
                                        f.AddViewedStory(ri.Id);
                                    }
                                }
                            }
                        }
                    }
                }
                List<category> c = new List<category>(this.categories.Count);
                foreach(INewsFeedCategory cat in this.categories.Values)
                {
                    if (!StringHelper.EmptyTrimOrNull(cat.Value))
                    {
                        c.Add(new category(cat));
                    }
                }
                if (c.Count == 0)
                {
                    feedlist.categories = null;
                }
                else
                {
                    feedlist.categories = c;
                }
                List<listviewLayout> lvl = new List<listviewLayout>(this.layouts.Count);
                for (int i = 0; i < this.layouts.Count; i++)
                {
                    FeedColumnLayoutEntry s = this.layouts[i];
                    if (s.Value == null)
                    {
                        this.layouts.RemoveAt(i);
                        i--;
                    }
                    else
                    {
                        lvl.Add(new listviewLayout(s.Key, s.Value));
                    }
                }
                if (lvl.Count == 0)
                {
                    feedlist.listviewLayouts = null;
                }
                else
                {
                    feedlist.listviewLayouts = lvl;
                }
                List<NntpServerDefinition> nntps = new List<NntpServerDefinition>(nntpServers.Values.Count);
                foreach (INntpServerDefinition val in nntpServers.Values)
                    nntps.Add((NntpServerDefinition)val);
                if (nntps.Count == 0)
                {
                    feedlist.nntpservers = null;
                }
                else
                {
                    feedlist.nntpservers = nntps;
                }
                List<UserIdentity> ids = new List<UserIdentity>(this.identities.Values);
                if (ids.Count == 0)
                {
                    feedlist.identities = null;
                }
                else
                {
                    feedlist.identities = ids;
                }
                TextWriter writer = new StreamWriter(feedStream);
                serializer.Serialize(writer, feedlist);
            }
        }
        public void MarkForDownload(INewsFeed f)
        {
            f.etag = null;
            f.lastretrievedSpecified = false;
            f.lastretrieved = DateTime.MinValue;
            f.lastmodified = DateTime.MinValue;
        }
        public void MarkForDownload()
        {
            if (this.FeedsListOK)
            {
                foreach (NewsFeed f in feedsTable.Values)
                {
                    this.MarkForDownload(f);
                }
            }
        }
        public void ClearItemsCache()
        {
            this.itemsTable.Clear();
            this.CacheHandler.ClearCache();
        }
        public void MarkAllCachedItemsAsRead()
        {
            foreach (INewsFeed f in feedsTable.Values)
            {
                this.MarkAllCachedItemsAsRead(f);
            }
        }
        public void MarkAllCachedCategoryItemsAsRead(string category)
        {
            if (FeedsListOK)
            {
                if (this.categories.ContainsKey(category))
                {
                    foreach (NewsFeed f in feedsTable.Values)
                    {
                        if ((f.category != null) && f.category.Equals(category))
                        {
                            this.MarkAllCachedItemsAsRead(f);
                        }
                    }
                }
                else if (category == null )
                {
                    foreach (NewsFeed f in feedsTable.Values)
                    {
                        if (f.category == null)
                        {
                            this.MarkAllCachedItemsAsRead(f);
                        }
                    }
                }
            }
        }
        public virtual void MarkAllCachedItemsAsRead(string feedUrl)
        {
            if (!string.IsNullOrEmpty(feedUrl))
            {
                INewsFeed feed = null;
                if (feedsTable.TryGetValue(feedUrl, out feed))
                {
                    this.MarkAllCachedItemsAsRead(feed);
                }
            }
        }
        public virtual void MarkAllCachedItemsAsRead(INewsFeed feed)
        {
            if (feed != null && !string.IsNullOrEmpty(feed.link) && itemsTable.ContainsKey(feed.link))
            {
                IFeedDetails fi = itemsTable[feed.link] as IFeedDetails;
                if (fi != null)
                {
                    foreach (INewsItem ri in fi.ItemsList)
                    {
                        ri.BeenRead = true;
                    }
                }
                feed.containsNewMessages = false;
            }
        }
        public virtual INewsFeedCategory AddCategory(string cat)
        {
            if (StringHelper.EmptyTrimOrNull(cat))
                return null;
               if( this.categories.ContainsKey(cat))
                return this.categories[cat];
            List<string> ancestors = category.GetAncestors(cat);
            for (int i = ancestors.Count; i-- > 0; ){
                INewsFeedCategory c = null;
                if (!this.categories.TryGetValue(ancestors[i], out c))
                {
                    this.categories.Add(ancestors[i], new category(ancestors[i]));
                }
            }
            INewsFeedCategory newCategory = new category(cat);
            newCategory.parent = (ancestors.Count == 0 ? null : this.categories[ancestors[ancestors.Count - 1]]);
            this.categories.Add(cat, newCategory);
            readonly_categories = new ReadOnlyDictionary<string, INewsFeedCategory>(this.categories);
            return newCategory;
        }
        public virtual INewsFeedCategory AddCategory(INewsFeedCategory cat)
        {
            if (this.categories.ContainsKey(cat.Value))
            {
                return categories[cat.Value];
            }
            else
            {
                this.categories.Add(cat.Value, cat);
                return cat;
            }
        }
        public virtual bool HasCategory(string cat) {
            if (cat == null)
            {
                return false;
            }
            return this.categories.ContainsKey(cat);
        }
        public virtual ReadOnlyDictionary<string, INewsFeedCategory> GetCategories() {
            readonly_categories = readonly_categories ?? new ReadOnlyDictionary<string, INewsFeedCategory>(categories);
            return readonly_categories;
        }
        public virtual void DeleteCategory(string cat)
        {
            if (!StringHelper.EmptyTrimOrNull(cat) && categories.ContainsKey(cat))
            {
                IList<string> categories2remove = this.GetChildCategories(cat);
                categories2remove.Add(cat);
                lock (this.categories)
                {
                    foreach (string c in categories2remove)
                    {
                        this.categories.Remove(c);
                    }
                }
                var feeds2delete =
                   from f in this.feedsTable.Values
                   where categories2remove.Contains(f.category)
                   select f.link.ToString();
                string[] feeds2remove = feeds2delete.ToArray<string>();
                lock (this.feedsTable)
                {
                    foreach (var feedUrl in feeds2remove)
                    {
                        this.feedsTable.Remove(feedUrl);
                    }
                }
                readonly_categories = new ReadOnlyDictionary<string, INewsFeedCategory>(this.categories);
            }
        }
        public virtual void ChangeCategory(INewsFeed feed, string cat)
        {
            if (feed == null)
                throw new ArgumentNullException("feed");
            feed.category = cat;
        }
        public virtual void ChangeCategory(INewsFeed feed, INewsFeedCategory cat) {
            if(feed == null)
                throw new ArgumentNullException("feed");
            if(cat != null)
            {
                feed.category = cat.Value;
            }else{
                feed.category = null;
            }
        }
        public virtual void RenameCategory(string oldName, string newName)
        {
            if (StringHelper.EmptyTrimOrNull(oldName))
                throw new ArgumentNullException("oldName");
            if (StringHelper.EmptyTrimOrNull(newName))
                throw new ArgumentNullException("newName");
            if (this.categories.ContainsKey(oldName))
            {
                INewsFeedCategory cat = this.categories[oldName];
                this.categories.Remove(oldName);
                cat.Value = newName;
                categories.Add(newName, cat);
            }
        }
        private INewsFeedCategory GetParentCategory(string category)
        {
            int index = category.LastIndexOf(FeedSource.CategorySeparator);
            INewsFeedCategory c = null;
            if (index != -1)
            {
                string parentName = category.Substring(0, index);
                categories.TryGetValue(parentName, out c);
            }
            return c;
        }
        protected List<string> GetChildCategories(string name)
        {
            List<string> list = new List<string>();
            foreach (INewsFeedCategory c in this.categories.Values)
            {
                if (c.Value.StartsWith(name + FeedSource.CategorySeparator))
                {
                    list.Add(c.Value);
                }
            }
            return list;
        }
        protected List<INewsFeedCategory> GetChildCategories(INewsFeedCategory parent)
        {
            List<INewsFeedCategory> list = new List<INewsFeedCategory>();
            foreach (INewsFeedCategory c in this.categories.Values)
            {
                if (c.Value.StartsWith(parent.Value + FeedSource.CategorySeparator))
                {
                    list.Add(c);
                }
            }
            return list;
        }
        protected virtual void OnNewsFeedPropertyChanged(object sender, PropertyChangedEventArgs e)
        {
        }
        public virtual INewsFeed AddFeed(INewsFeed f)
        {
            return this.AddFeed(f, null);
        }
        public virtual INewsFeed AddFeed(INewsFeed feed, FeedInfo feedInfo)
        {
            if (feed != null)
            {
                lock (feedsTable)
                {
                    if (feedsTable.ContainsKey(feed.link))
                    {
                        feedsTable.Remove(feed.link);
                    }
                    feed.owner = this;
                    feedsTable.Add(feed.link, feed);
                }
            }
            if (feedInfo != null && feed != null)
            {
                lock (this.itemsTable)
                {
                    if (itemsTable.ContainsKey(feed.link))
                    {
                        itemsTable.Remove(feed.link);
                    }
                    itemsTable.Add(feed.link, feedInfo);
                }
            }
            readonly_feedsTable = new ReadOnlyDictionary<string, INewsFeed>(feedsTable);
            return feed;
        }
        public virtual void DeleteFeed(string feedUrl)
        {
            if (!feedsTable.ContainsKey(feedUrl))
            {
                return;
            }
            INewsFeed f = feedsTable[feedUrl];
            feedsTable.Remove(feedUrl);
            if (itemsTable.ContainsKey(feedUrl))
            {
                itemsTable.Remove(feedUrl);
            }
            SearchHandler.IndexRemove(f.id);
            if (this.enclosureDownloader != null)
                this.enclosureDownloader.CancelPendingDownloads(feedUrl);
            try
            {
                this.CacheHandler.RemoveFeed(f);
            }
            catch (Exception e)
            {
                throw new ApplicationException(e.Message, e);
            }
            readonly_feedsTable = new ReadOnlyDictionary<string, INewsFeed>(feedsTable);
        }
        public ReadOnlyDictionary<string, INewsFeed> GetFeeds()
        {
            readonly_feedsTable = readonly_feedsTable ?? new ReadOnlyDictionary<string, INewsFeed>(feedsTable);
            return readonly_feedsTable;
        }
        public virtual bool IsSubscribed(string feedUrl)
        {
            if (StringHelper.EmptyTrimOrNull(feedUrl))
            {
                return false;
            }
            return feedsTable.ContainsKey(feedUrl);
        }
        public virtual void DeleteAllFeedsAndCategories()
        {
            this.feedsTable.Clear();
            this.categories.Clear();
            this.readonly_categories = new ReadOnlyDictionary<string, INewsFeedCategory>(categories);
            this.readonly_feedsTable = new ReadOnlyDictionary<string, INewsFeed>(feedsTable);
        }
        private const NewsFeedProperty cacheRelevantPropertyChanges =
            NewsFeedProperty.FeedItemFlag |
            NewsFeedProperty.FeedItemReadState |
            NewsFeedProperty.FeedItemCommentCount |
            NewsFeedProperty.FeedItemNewCommentsRead |
            NewsFeedProperty.FeedItemWatchComments |
            NewsFeedProperty.FeedCredentials;
        public bool IsCacheRelevantChange(NewsFeedProperty changedProperty)
        {
            return (cacheRelevantPropertyChanges & changedProperty) != NewsFeedProperty.None;
        }
        private const NewsFeedProperty subscriptionRelevantPropertyChanges =
            NewsFeedProperty.FeedLink |
            NewsFeedProperty.FeedTitle |
            NewsFeedProperty.FeedCategory |
            NewsFeedProperty.FeedItemsDeleteUndelete |
            NewsFeedProperty.FeedItemReadState |
            NewsFeedProperty.FeedMaxItemAge |
            NewsFeedProperty.FeedRefreshRate |
            NewsFeedProperty.FeedCacheUrl |
            NewsFeedProperty.FeedAdded |
            NewsFeedProperty.FeedRemoved |
            NewsFeedProperty.FeedCategoryAdded |
            NewsFeedProperty.FeedCategoryRemoved |
            NewsFeedProperty.FeedAlertOnNewItemsReceived |
            NewsFeedProperty.FeedMarkItemsReadOnExit |
            NewsFeedProperty.General;
        public bool IsSubscriptionRelevantChange(NewsFeedProperty changedProperty)
        {
            return (subscriptionRelevantPropertyChanges & changedProperty) != NewsFeedProperty.None;
        }
        public void ApplyFeedModifications(string feedUrl)
        {
            if (feedUrl == null || feedUrl.Length == 0)
                throw new ArgumentNullException("feedUrl");
            IFeedDetails fi = null;
            INewsFeed f = null;
            if (itemsTable.ContainsKey(feedUrl))
            {
                fi = itemsTable[feedUrl];
            }
            if (feedsTable.ContainsKey(feedUrl))
            {
                f = feedsTable[feedUrl];
            }
            if (fi != null && f != null)
            {
                try
                {
                    f.cacheurl = this.SaveFeed(f);
                }
                catch (Exception ex)
                {
                    Trace("ApplyFeedModifications() cause exception while saving feed '{0}'to cache: {1}", feedUrl,
                          ex.Message);
                }
            }
        }
        private static bool IsPropertyValueSet(object value, string propertyName, ISharedProperty owner)
        {
            if (value == null)
            {
                return false;
            }
            else if (value is string)
            {
                bool isSet = !string.IsNullOrEmpty((string) value);
                if (propertyName.Equals("maxitemage") && isSet)
                {
                    isSet = !value.Equals(XmlConvert.ToString(TimeSpan.MaxValue));
                }
                return isSet;
            }
            else
            {
             return (bool) GetSharedPropertyValue(owner, propertyName + "Specified");
            }
        }
        private object GetFeedProperty(string feedUrl, string propertyName)
        {
            return this.GetFeedProperty(feedUrl, propertyName, false);
        }
        private object GetFeedProperty(string feedUrl, string propertyName, bool inheritCategory)
        {
         object value = GetSharedPropertyValue(this, propertyName);
   if (propertyName.Equals("maxitemage"))
   {
    value = XmlConvert.ToTimeSpan((string)value);
   }
            if (feedsTable.ContainsKey(feedUrl))
            {
                INewsFeed f = feedsTable[feedUrl];
    object f_value = GetSharedPropertyValue(f, propertyName);
                if (IsPropertyValueSet(f_value, propertyName, f))
                {
                    if (propertyName.Equals("maxitemage"))
                    {
                        f_value = XmlConvert.ToTimeSpan((string) f_value);
                    }
                    value = f_value;
                }
                else if (inheritCategory && !string.IsNullOrEmpty(f.category))
                {
                    INewsFeedCategory c;
                    this.categories.TryGetValue(f.category, out c);
                    while (c != null)
                    {
      object c_value = GetSharedPropertyValue(c, propertyName);
                        if (IsPropertyValueSet(c_value, propertyName, c))
                        {
                            if (propertyName.Equals("maxitemage"))
                            {
                                c_value = XmlConvert.ToTimeSpan((string) c_value);
                            }
                            value = c_value;
                            break;
                        }
                        else
                        {
                            c = c.parent;
                        }
                    }
                }
            }
            return value;
        }
        private void SetFeedProperty(string feedUrl, string propertyName, object value)
        {
            if (feedsTable.ContainsKey(feedUrl))
            {
                INewsFeed f = feedsTable[feedUrl];
                if (value is TimeSpan)
                {
                    value = XmlConvert.ToString((TimeSpan) value);
                }
    SetSharedPropertyValue(f, propertyName, value);
                if ((value != null) && !(value is string))
                {
     SetSharedPropertyValue(f, propertyName + "Specified", true);
                }
            }
        }
        public void SetMaxItemAge(string feedUrl, TimeSpan age)
        {
            this.SetFeedProperty(feedUrl, "maxitemage", age);
        }
        public TimeSpan GetMaxItemAge(string feedUrl)
        {
            return (TimeSpan) this.GetFeedProperty(feedUrl, "maxitemage", true);
        }
        public void SetRefreshRate(string feedUrl, int refreshRate)
        {
            this.SetFeedProperty(feedUrl, "refreshrate", refreshRate);
        }
        public int GetRefreshRate(string feedUrl)
        {
            return (int) this.GetFeedProperty(feedUrl, "refreshrate", true);
        }
        public void SetStyleSheet(string feedUrl, string style)
        {
            this.SetFeedProperty(feedUrl, "stylesheet", style);
        }
        public string GetStyleSheet(string feedUrl)
        {
            return (string) this.GetFeedProperty(feedUrl, "stylesheet");
        }
        public void SetEnclosureFolder(string feedUrl, string folder)
        {
            this.SetFeedProperty(feedUrl, "enclosurefolder", folder);
        }
        public string GetEnclosureFolder(string feedUrl, string filename)
        {
            string folderName = (IsPodcast(filename) ? this.PodcastFolder : EnclosureFolder);
            if (CreateSubfoldersForEnclosures && feedsTable.ContainsKey(feedUrl))
            {
                INewsFeed f = feedsTable[feedUrl];
                folderName = Path.Combine(folderName, FileHelper.CreateValidFileName(f.title));
            }
            return folderName;
        }
        public void SetFeedColumnLayout(string feedUrl, string layout)
        {
            this.SetFeedProperty(feedUrl, "listviewlayout", layout);
        }
        public string GetFeedColumnLayout(string feedUrl)
        {
            return (string) this.GetFeedProperty(feedUrl, "listviewlayout");
        }
        public void SetMarkItemsReadOnExit(string feedUrl, bool markitemsread)
        {
            this.SetFeedProperty(feedUrl, "markitemsreadonexit", markitemsread);
        }
        public bool GetMarkItemsReadOnExit(string feedUrl)
        {
            return (bool) this.GetFeedProperty(feedUrl, "markitemsreadonexit");
        }
        public void SetDownloadEnclosures(string feedUrl, bool download)
        {
            this.SetFeedProperty(feedUrl, "downloadenclosures", download);
        }
        public bool GetDownloadEnclosures(string feedUrl)
        {
            return (bool) this.GetFeedProperty(feedUrl, "downloadenclosures");
        }
        public void SetEnclosureAlert(string feedUrl, bool alert)
        {
            this.SetFeedProperty(feedUrl, "enclosurealert", alert);
        }
        public bool GetEnclosureAlert(string feedUrl)
        {
            return (bool) this.GetFeedProperty(feedUrl, "enclosurealert");
        }
        private object GetCategoryProperty(string category, string propertyName)
        {
         object value = GetSharedPropertyValue(this, propertyName);
   if (propertyName.Equals("maxitemage"))
   {
    value = XmlConvert.ToTimeSpan((string)value);
   }
            if (!string.IsNullOrEmpty(category))
   {
    INewsFeedCategory c;
    this.categories.TryGetValue(category, out c);
    while (c != null)
    {
     object c_value = GetSharedPropertyValue(c, propertyName);
     if (IsPropertyValueSet(c_value, propertyName, c))
     {
      if (propertyName.Equals("maxitemage"))
      {
       c_value = XmlConvert.ToTimeSpan((string)c_value);
      }
      value = c_value;
      break;
     }
     else
     {
      c = c.parent;
     }
    }
   }
            return value;
        }
        private void SetCategoryProperty(string category, string propertyName, object value)
        {
            if (!string.IsNullOrEmpty(category))
            {
                foreach (category c in this.categories.Values)
                {
                    if (c.Value.Equals(category) || c.Value.StartsWith(category + CategorySeparator))
                    {
                        if (value is TimeSpan)
                        {
                            value = XmlConvert.ToString((TimeSpan) value);
                        }
      SetSharedPropertyValue(c, propertyName, value);
                        if ((value != null) && !(value is string))
                        {
       SetSharedPropertyValue(c, propertyName + "Specified", true);
                        }
                        break;
                    }
                }
            }
        }
        public void SetCategoryMaxItemAge(string category, TimeSpan age)
        {
            this.SetCategoryProperty(category, "maxitemage", age);
        }
        public TimeSpan GetCategoryMaxItemAge(string category)
        {
            return (TimeSpan) this.GetCategoryProperty(category, "maxitemage");
        }
        public void SetCategoryRefreshRate(string category, int refreshRate)
        {
            this.SetCategoryProperty(category, "refreshrate", refreshRate);
        }
        public int GetCategoryRefreshRate(string category)
        {
            return (int) this.GetCategoryProperty(category, "refreshrate");
        }
        public void SetCategoryStyleSheet(string category, string style)
        {
            this.SetCategoryProperty(category, "stylesheet", style);
        }
        public string GetCategoryStyleSheet(string category)
        {
            return (string) this.GetCategoryProperty(category, "stylesheet");
        }
        public void SetCategoryEnclosureFolder(string category, string folder)
        {
            this.SetCategoryProperty(category, "enclosurefolder", folder);
        }
        public string GetCategoryEnclosureFolder(string category)
        {
            return (string) this.GetCategoryProperty(category, "enclosurefolder");
        }
        public void SetCategoryFeedColumnLayout(string category, string layout)
        {
            this.SetCategoryProperty(category, "listviewlayout", layout);
        }
        public string GetCategoryFeedColumnLayout(string category)
        {
            return (string) this.GetCategoryProperty(category, "listviewlayout");
        }
        public void SetCategoryMarkItemsReadOnExit(string category, bool markitemsread)
        {
            this.SetCategoryProperty(category, "markitemsreadonexit", markitemsread);
        }
        public bool GetCategoryMarkItemsReadOnExit(string category)
        {
            return (bool) this.GetCategoryProperty(category, "markitemsreadonexit");
        }
        public void SetCategoryDownloadEnclosures(string category, bool download)
        {
            this.SetCategoryProperty(category, "downloadenclosures", download);
        }
        public bool GetCategoryDownloadEnclosures(string category)
        {
            return (bool) this.GetCategoryProperty(category, "downloadenclosures");
        }
        public void SetCategoryEnclosureAlert(string category, bool alert)
        {
            this.SetCategoryProperty(category, "enclosurealert", alert);
        }
        public bool GetCategoryEnclosureAlert(string category)
        {
            return (bool) this.GetCategoryProperty(category, "enclosurealert");
        }
        public virtual IFeedDetails GetFeedDetails(string feedUrl)
        {
            return this.GetFeedInfo(feedUrl, null);
        }
        public IFeedDetails GetFeedInfo(string feedUrl, ICredentials credentials)
        {
            if (string.IsNullOrEmpty(feedUrl))
                return null;
            IFeedDetails fd = null;
            if (!itemsTable.ContainsKey(feedUrl))
            {
                INewsFeed theFeed = feedsTable[feedUrl];
                if (theFeed == null)
                {
                    using (
                        Stream mem =
                            AsyncWebRequest.GetSyncResponseStream(feedUrl, credentials, this.UserAgent, this.Proxy))
                    {
                        NewsFeed f = new NewsFeed();
                        f.link = feedUrl;
                        if (RssParser.CanProcessUrl(feedUrl))
                        {
                            fd = RssParser.GetItemsForFeed(f, mem, false);
                        }
                    }
                    return fd;
                }
                fd = this.GetFeed(theFeed);
                lock (itemsTable)
                {
                    if (!itemsTable.ContainsKey(feedUrl) && (fd != null))
                    {
                        itemsTable.Add(feedUrl, fd);
                    }
                }
            }
            else
            {
                fd = itemsTable[feedUrl];
            }
            return fd;
        }
        public virtual IList<INewsItem> GetItemsForFeed(string feedUrl, bool force_download)
        {
            string url2Access = feedUrl;
            if (((!force_download) || this.isOffline) && itemsTable.ContainsKey(feedUrl))
            {
                return itemsTable[feedUrl].ItemsList;
            }
            INewsFeed theFeed = null;
            if (feedsTable.ContainsKey(feedUrl))
                theFeed = feedsTable[feedUrl];
            if (theFeed == null)
                return EmptyItemList;
            try
            {
                if (((!force_download) || this.isOffline) && (!itemsTable.ContainsKey(feedUrl)) &&
                    ((theFeed.cacheurl != null) && (theFeed.cacheurl.Length > 0) &&
                     (this.CacheHandler.FeedExists(theFeed))))
                {
                    bool getFromCache;
                    lock (itemsTable)
                    {
                        getFromCache = !itemsTable.ContainsKey(feedUrl);
                    }
                    if (getFromCache)
                    {
                        FeedDetailsInternal fi = this.GetFeed(theFeed);
                        if (fi != null)
                        {
                            lock (itemsTable)
                            {
                                if (!itemsTable.ContainsKey(feedUrl))
                                    itemsTable.Add(feedUrl, fi);
                            }
                        }
                    }
                    return itemsTable[feedUrl].ItemsList;
                }
            }
            catch (Exception ex)
            {
                Trace("Error retrieving feed '{0}' from cache: {1}", feedUrl, ex.ToString());
            }
            if (this.isOffline)
            {
                return EmptyItemList;
            }
            try
            {
                new Uri(url2Access);
            }
            catch (UriFormatException ufex)
            {
                Trace("Uri format exception on '{0}': {1}", url2Access, ufex.Message);
                throw;
            }
            this.AsyncGetItemsForFeed(feedUrl, true, true);
            return EmptyItemList;
        }
        public int AsyncRequestsPending()
        {
            return this.AsyncWebRequest.PendingRequests;
        }
        public INewsItem CopyNewsItemTo(INewsItem item, INewsFeed f)
        {
            if (!item.HasContent)
                this.GetCachedContentForItem(item);
            NewsItem n = new NewsItem(f, item);
            return n;
        }
        public void GetCachedContentForItem(INewsItem item)
        {
            this.CacheHandler.LoadItemContent(item);
        }
        protected virtual void OnNewsItemPropertyChanged(object sender, PropertyChangedEventArgs e)
        {
        }
        public virtual IList<INewsItem> GetCachedItemsForFeed(string feedUrl)
        {
            lock (itemsTable)
            {
                if (itemsTable.ContainsKey(feedUrl))
                {
                    return itemsTable[feedUrl].ItemsList;
                }
            }
            INewsFeed theFeed = null;
            try
            {
                if (feedsTable.TryGetValue(feedUrl, out theFeed))
                {
                    if ((theFeed.cacheurl != null) && (theFeed.cacheurl.Trim().Length > 0) &&
                        (this.CacheHandler.FeedExists(theFeed)))
                    {
                        bool getFromCache;
                        lock (itemsTable)
                        {
                            getFromCache = !itemsTable.ContainsKey(feedUrl);
                        }
                        if (getFromCache)
                        {
                            FeedDetailsInternal fi = this.GetFeed(theFeed);
                            if (fi != null)
                            {
                                lock (itemsTable)
                                {
                                    if (!itemsTable.ContainsKey(feedUrl))
                                        itemsTable.Add(feedUrl, fi);
                                }
                            }
                        }
                        return itemsTable[feedUrl].ItemsList;
                    }
                }
            }
            catch (FileNotFoundException)
            {
            }
            catch (XmlException xe)
            {
                Trace("Xml Error retrieving feed '{0}' from cache: {1}", feedUrl, xe.ToString());
                this.CacheHandler.RemoveFeed(theFeed);
            }
            catch (Exception ex)
            {
                Trace("Error retrieving feed '{0}' from cache: {1}", feedUrl, ex.ToString());
                if (theFeed != null && !theFeed.causedException)
                {
                    theFeed.causedException = true;
                    RaiseOnUpdateFeedException(feedUrl,
                                               new Exception(
                                                   "Error retrieving feed {" + feedUrl + "} from cache: " + ex.Message,
                                                   ex), 11);
                }
            }
            return EmptyItemList;
        }
        public bool AsyncGetItemsForFeed(string feedUrl, bool forceDownload)
        {
            return this.AsyncGetItemsForFeed(feedUrl, forceDownload, false);
        }
        public virtual bool AsyncGetItemsForFeed(string feedUrl, bool forceDownload, bool manual)
        {
            if (feedUrl == null || feedUrl.Trim().Length == 0)
                throw new ArgumentNullException("feedUrl");
            string etag = null;
            bool requestQueued = false;
            int priority = 10;
            if (forceDownload)
                priority += 100;
            if (manual)
                priority += 1000;
            try
            {
                Uri reqUri = new Uri(feedUrl);
                try
                {
                    if ((!forceDownload) || this.isOffline)
                    {
                        GetCachedItemsForFeed(feedUrl);
                        RaiseOnUpdatedFeed(reqUri, null, RequestResult.NotModified, priority, false);
                        return false;
                    }
                }
                catch (XmlException xe)
                {
                    Trace("Unexpected error retrieving cached feed '{0}': {1}", feedUrl, xe.ToString());
                }
                INewsFeed theFeed = null;
                if (feedsTable.ContainsKey(feedUrl))
                    theFeed = feedsTable[feedUrl];
                if (theFeed == null)
                    return false;
                RaiseOnUpdateFeedStarted(reqUri, forceDownload, priority);
                DateTime lastModified = DateTime.MinValue;
                if (itemsTable.ContainsKey(feedUrl))
                {
                    etag = theFeed.etag;
                    lastModified = (theFeed.lastretrievedSpecified ? theFeed.lastretrieved : theFeed.lastmodified);
                }
                ICredentials c;
                if (RssHelper.IsNntpUrl(theFeed.link))
                {
                    c = GetNntpServerCredentials(theFeed);
                }
                else
                {
                    c = CreateCredentialsFrom(theFeed);
                }
                RequestParameter reqParam =
                    RequestParameter.Create(reqUri, this.UserAgent, this.Proxy, c, lastModified, etag);
                reqParam.SetCookies = SetCookies;
                AsyncWebRequest.QueueRequest(reqParam,
                                             null ,
                                             OnRequestStart,
                                             OnRequestComplete,
                                             OnRequestException, priority);
                requestQueued = true;
            }
            catch (Exception e)
            {
                Trace("Unexpected error on QueueRequest(), processing feed '{0}': {1}", feedUrl, e.ToString());
                RaiseOnUpdateFeedException(feedUrl, e, priority);
            }
            return requestQueued;
        }
        public Hashtable GetFailureContext(Uri feedUri)
        {
            INewsFeed f = null;
            if (feedUri == null || !feedsTable.TryGetValue(feedUri.CanonicalizedUri(), out f))
                return new Hashtable();
            return this.GetFailureContext(f);
        }
        public Hashtable GetFailureContext(string feedUri)
        {
            if (feedUri == null)
                return new Hashtable();
            if (feedsTable.ContainsKey(feedUri))
                return this.GetFailureContext(feedsTable[feedUri]);
            else
                return new Hashtable();
        }
        public Hashtable GetFailureContext(INewsFeed f)
        {
            if (f == null)
            {
                return new Hashtable();
            }
            FeedInfo fi = null;
            lock (itemsTable)
            {
                if (itemsTable.ContainsKey(f.link))
                {
                    fi = itemsTable[f.link] as FeedInfo;
                }
            }
            return GetFailureContext(f, fi);
        }
        public static Hashtable GetFailureContext(INewsFeed f, IFeedDetails fi)
        {
            Hashtable context = new Hashtable();
            if (f == null)
            {
                return context;
            }
            context.Add("FULL_TITLE", (f.category ?? String.Empty) + CategorySeparator + f.title);
            context.Add("FAILURE_OBJECT", f);
            if (fi == null)
                return context;
            context.Add("PUBLISHER_HOMEPAGE", fi.Link);
            XmlElement xe = RssHelper.GetOptionalElement(fi.OptionalElements, "managingEditor", String.Empty);
            if (xe != null)
                context.Add("PUBLISHER", xe.InnerText);
            xe = RssHelper.GetOptionalElement(fi.OptionalElements, "webMaster", String.Empty);
            if (xe != null)
            {
                context.Add("TECH_CONTACT", xe.InnerText);
            }
            else
            {
                xe = RssHelper.GetOptionalElement(fi.OptionalElements, "errorReportsTo", "http://webns.net/mvcb/");
                if (xe != null && xe.Attributes["resource", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"] != null)
                    context.Add("TECH_CONTACT",
                                xe.Attributes["resource", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"].InnerText);
            }
            xe = RssHelper.GetOptionalElement(fi.OptionalElements, "generator", String.Empty);
            if (xe != null)
                context.Add("GENERATOR", xe.InnerText);
            return context;
        }
        protected virtual void OnRequestStart(Uri requestUri, ref bool cancel)
        {
            Trace("AsyncRequest.OnRequestStart('{0}') downloading", requestUri.ToString());
            this.RaiseBeforeDownloadFeedStarted(requestUri, ref cancel);
            if (!cancel)
                cancel = this.Offline;
        }
        protected virtual void OnRequestException(Uri requestUri, Exception e, int priority)
        {
            Trace("AsyncRequst.OnRequestException() fetching '{0}': {1}", requestUri.ToString(), e.ToString());
            string key = requestUri.CanonicalizedUri();
            if (feedsTable.ContainsKey(key))
            {
                Trace("AsyncRequest.OnRequestException() '{0}' found in feedsTable.", requestUri.ToString());
                INewsFeed f = feedsTable[key];
                f.causedException = true;
            }
            else
            {
                Trace("AsyncRequst.OnRequestException() '{0}' NOT found in feedsTable.", requestUri.ToString());
            }
            RaiseOnUpdateFeedException(requestUri.CanonicalizedUri(), e, priority);
        }
        protected virtual void OnRequestComplete(Uri requestUri, Stream response, Uri newUri, string eTag, DateTime lastModified,
                                       RequestResult result, int priority)
        {
            Trace("AsyncRequest.OnRequestComplete: '{0}': {1}", requestUri.ToString(), result);
            if (newUri != null)
                Trace("AsyncRequest.OnRequestComplete: perma redirect of '{0}' to '{1}'.", requestUri.ToString(),
                      newUri.ToString());
            IList<INewsItem> itemsForFeed;
            bool firstSuccessfulDownload = false;
            try
            {
                INewsFeed theFeed = null;
                if (!feedsTable.TryGetValue(requestUri.CanonicalizedUri(), out theFeed))
                {
                    Trace("ATTENTION! FeedsTable[requestUri] as NewsFeed returns null for: '{0}'",
                          requestUri.ToString());
                    return;
                }
                string feedUrl = theFeed.link;
                if (true)
                {
                    if (String.Compare(feedUrl, requestUri.CanonicalizedUri(), true) != 0)
                        Trace("feed.link != requestUri: \r\n'{0}'\r\n'{1}'", feedUrl, requestUri.CanonicalizedUri());
                }
                if (newUri != null)
                {
                    feedsTable.Remove(feedUrl);
                    theFeed.link = newUri.CanonicalizedUri();
                    this.feedsTable.Add(theFeed.link, theFeed);
                    lock (itemsTable)
                    {
                        if (itemsTable.ContainsKey(feedUrl))
                        {
                            IFeedDetails FI = itemsTable[feedUrl];
                            itemsTable.Remove(feedUrl);
                            itemsTable.Remove(theFeed.link);
                            itemsTable.Add(theFeed.link, FI);
                        }
                    }
                    feedUrl = theFeed.link;
                }
                if (result == RequestResult.OK)
                {
                    FeedDetailsInternal fi;
                    if ((requestUri.Scheme == NntpWebRequest.NntpUriScheme) ||
                        (requestUri.Scheme == NntpWebRequest.NewsUriScheme))
                    {
                        fi = NntpParser.GetItemsForNewsGroup(theFeed, response, false);
                    }
                    else
                    {
                        fi = RssParser.GetItemsForFeed(theFeed, response, false);
                    }
                    FeedDetailsInternal fiFromCache = null;
                    try
                    {
                        if (!itemsTable.ContainsKey(feedUrl))
                        {
                            fiFromCache = this.GetFeed(theFeed);
                        }
                    }
                    catch (Exception ex)
                    {
                        Trace("this.GetFeed(theFeed) caused exception: {0}", ex.ToString());
                    }
                    List<INewsItem> newReceivedItems = null;
                    lock (itemsTable)
                    {
                        if (!itemsTable.ContainsKey(feedUrl) && (fiFromCache != null))
                        {
                            itemsTable.Add(feedUrl, fiFromCache);
                        }
                        if (itemsTable.ContainsKey(feedUrl))
                        {
                            IFeedDetails fi2 = itemsTable[feedUrl];
                            if (RssParser.CanProcessUrl(feedUrl))
                            {
                                fi.ItemsList = MergeAndPurgeItems(fi2.ItemsList, fi.ItemsList, theFeed.deletedstories,
                                                                  out newReceivedItems, theFeed.replaceitemsonrefresh,
                                                                  true );
                            }
                            if ((String.Compare(fi2.Link, fi.Link, true) != 0) &&
                                (newReceivedItems.Count == fi.ItemsList.Count))
                            {
                                foreach (FeedDetailsInternal fdi in itemsTable.Values)
                                {
                                    if (String.Compare(fdi.Link, fi.Link, true) == 0)
                                    {
                                        RaiseOnUpdatedFeed(requestUri, null, RequestResult.NotModified, priority, false);
                                        _log.Error(
                                            String.Format(
                                                "Feed mixup encountered when downloading {2} because fi2.link != fi.link: {0}!= {1}",
                                                fi2.Link, fi.Link, requestUri.CanonicalizedUri()));
                                        return;
                                    }
                                }
                            }
                            itemsTable.Remove(feedUrl);
                        }
                        else
                        {
                            firstSuccessfulDownload = true;
                            newReceivedItems = fi.ItemsList;
                            RelationCosmosAddRange(newReceivedItems);
                        }
                        itemsTable.Add(feedUrl, fi);
                    }
                    theFeed.etag = eTag;
                    if (lastModified > theFeed.lastmodified)
                    {
                        theFeed.lastmodified = lastModified;
                    }
                    theFeed.lastretrieved = new DateTime(DateTime.Now.Ticks);
                    theFeed.lastretrievedSpecified = true;
                    theFeed.cacheurl = this.SaveFeed(theFeed);
                    SearchHandler.IndexAdd(newReceivedItems);
                    theFeed.causedException = false;
                    itemsForFeed = fi.ItemsList;
                    if (this.GetDownloadEnclosures(theFeed.link))
                    {
                        int numDownloaded = 0;
                        int maxDownloads = (firstSuccessfulDownload
                                                ? NumEnclosuresToDownloadOnNewFeed
            : DefaultNumEnclosuresToDownloadOnNewFeed);
                        if (newReceivedItems != null)
                            foreach (NewsItem ni in newReceivedItems)
                            {
                                if (numDownloaded >= maxDownloads)
                                {
                                    MarkEnclosuresDownloaded(ni);
                                    continue;
                                }
                                try
                                {
                                    numDownloaded += this.DownloadEnclosure(ni, maxDownloads - numDownloaded);
                                }
                                catch (DownloaderException de)
                                {
                                    _log.Error("Error occured when downloading enclosures in OnRequestComplete():", de);
                                }
                            }
                    }
                    theFeed.containsNewMessages = false;
                    theFeed.storiesrecentlyviewed.Clear();
                    foreach (NewsItem ri in itemsForFeed)
                    {
                        if (ri.BeenRead)
                        {
                            theFeed.AddViewedStory(ri.Id);
                        }
                        if (ri.HasNewComments)
                        {
                            theFeed.containsNewComments = true;
                        }
                    }
                    if (itemsForFeed.Count > theFeed.storiesrecentlyviewed.Count)
                    {
                        theFeed.containsNewMessages = true;
                    }
                }
                else if (result == RequestResult.NotModified)
                {
                    theFeed.lastretrieved = new DateTime(DateTime.Now.Ticks);
                    theFeed.lastretrievedSpecified = true;
                    theFeed.causedException = false;
                }
                else
                {
                    throw new NotImplementedException("Unhandled RequestResult: " + result);
                }
                RaiseOnUpdatedFeed(requestUri, newUri, result, priority, firstSuccessfulDownload);
            }
            catch (Exception e)
            {
                string key = requestUri.CanonicalizedUri();
                if (feedsTable.ContainsKey(key))
                {
                    Trace("AsyncRequest.OnRequestComplete('{0}') Exception: ", requestUri.ToString(), e.StackTrace);
                    INewsFeed f = feedsTable[key];
                    f.causedException = true;
                }
                else
                {
                    Trace("AsyncRequest.OnRequestComplete('{0}') Exception on feed not contained in FeedsTable: ",
                          requestUri.ToString(), e.StackTrace);
                }
                RaiseOnUpdateFeedException(requestUri.CanonicalizedUri(), e, priority);
            }
            finally
            {
                if (response != null)
                    response.Close();
            }
        }
        protected void OnAllRequestsComplete()
        {
            RaiseOnAllAsyncRequestsCompleted();
        }
        protected void OnEnclosureDownloadComplete(object sender, DownloadItemEventArgs e)
        {
            if (this.OnDownloadedEnclosure != null)
            {
                try
                {
                    this.OnDownloadedEnclosure(sender, e);
                }
                catch
                {
                }
            }
        }
        private static readonly byte[] ico_magic = new byte[] {0, 0, 1, 0};
        private static readonly int ico_magic_len = ico_magic.Length;
        private static readonly byte[] png_magic = new byte[] {0x89, 0x50, 0x4e, 0x47};
        private static readonly int png_magic_len = png_magic.Length;
        private static readonly byte[] gif_magic = new byte[] {0x47, 0x49, 0x46};
        private static readonly int gif_magic_len = gif_magic.Length;
        private static readonly byte[] jpg_magic = new byte[] {0xff, 0xd8};
        private static readonly int jpg_magic_len = jpg_magic.Length;
        private static readonly byte[] bmp_magic = new byte[] {0x42, 0x4d};
        private static readonly int bmp_magic_len = bmp_magic.Length;
        private static string GetExtensionForDetectedImage(byte[] bytes)
        {
            if (bytes == null)
                throw new ArgumentNullException("bytes");
            int i, len = bytes.Length;
            for (i = 0; i < jpg_magic_len && i < len; i++)
            {
                if (bytes[i] != jpg_magic[i]) break;
            }
            if (i == jpg_magic_len) return ".jpg";
            for (i = 0; i < ico_magic_len && i < len; i++)
            {
                if (bytes[i] != ico_magic[i]) break;
            }
            if (i == ico_magic_len) return ".ico";
            for (i = 0; i < png_magic_len && i < len; i++)
            {
                if (bytes[i] != png_magic[i]) break;
            }
            if (i == png_magic_len) return ".png";
            for (i = 0; i < gif_magic_len && i < len; i++)
            {
                if (bytes[i] != gif_magic[i]) break;
            }
            if (i == gif_magic_len) return ".gif";
            for (i = 0; i < bmp_magic_len && i < len; i++)
            {
                if (bytes[i] != bmp_magic[i]) break;
            }
            if (i == bmp_magic_len) return ".bmp";
            return null;
        }
        private void OnFaviconRequestComplete(Uri requestUri, Stream response, Uri newUri, string eTag,
                                              DateTime lastModified, RequestResult result, int priority)
        {
            Trace("AsyncRequest.OnFaviconRequestComplete: '{0}': {1}", requestUri.ToString(), result);
            if (newUri != null)
                Trace("AsyncRequest.OnFaviconRequestComplete: perma redirect of '{0}' to '{1}'.", requestUri.ToString(),
                      newUri.ToString());
            try
            {
                StringCollection feedUrls = new StringCollection();
                string favicon = null;
                if (result == RequestResult.OK)
                {
                    BinaryReader br = new BinaryReader(response);
                    byte[] bytes = new byte[response.Length];
                    if (bytes.Length > 0)
                    {
                        bytes = br.ReadBytes((int) response.Length);
                        string ext = GetExtensionForDetectedImage(bytes);
                        if (ext != null)
                        {
                            favicon = GenerateFaviconUrl(requestUri, ext);
                            string filelocation = Path.Combine(this.CacheHandler.CacheLocation, favicon);
                            using (FileStream fs = FileHelper.OpenForWrite(filelocation))
                            {
                                BinaryWriter bw = new BinaryWriter(fs);
                                bw.Write(bytes);
                                bw.Flush();
                            }
                        }
                    }
                    else
                    {
                    }
                    string[] keys;
                    lock (feedsTable)
                    {
                        keys = new string[feedsTable.Count];
                        if (feedsTable.Count > 0)
                            feedsTable.Keys.CopyTo(keys, 0);
                    }
                    foreach (string feedUrl in keys)
                    {
                        if (itemsTable.ContainsKey(feedUrl))
                        {
                            string websiteUrl = ((FeedInfo) itemsTable[feedUrl]).Link;
                            Uri uri = null;
                            try
                            {
                                uri = new Uri(websiteUrl);
                            }
                            catch (Exception)
                            {
                                ;
                            }
                            if ((uri != null) && uri.Authority.Equals(requestUri.Authority))
                            {
                                feedUrls.Add(feedUrl);
                                INewsFeed f = feedsTable[feedUrl];
                                f.favicon = favicon;
                            }
                        }
                    }
                }
                if (favicon != null)
                {
                    RaiseOnUpdatedFavicon(favicon, feedUrls);
                }
            }
            catch (Exception e)
            {
                Trace("AsyncRequest.OnFaviconRequestComplete('{0}') Exception on fetching favicon at: ",
                      requestUri.ToString(), e.StackTrace);
            }
            finally
            {
                if (response != null)
                    response.Close();
            }
        }
        protected void RaiseBeforeDownloadFeedStarted(Uri requestUri, ref bool cancel)
        {
            if (BeforeDownloadFeedStarted != null)
            {
                try
                {
                    DownloadFeedCancelEventArgs ea = new DownloadFeedCancelEventArgs(requestUri, cancel);
                    BeforeDownloadFeedStarted(this, ea);
                    cancel = ea.Cancel;
                }
                catch
                {
                }
            }
        }
        protected void RaiseOnMovedFeed(FeedMovedEventArgs fmea)
        {
            if (OnMovedFeed != null)
            {
                try
                {
                    OnMovedFeed(this, fmea);
                }
                catch
                {
                }
            }
        }
        private void RaiseOnUpdatedFavicon(string favicon, StringCollection feedUrls)
        {
            if (OnUpdatedFavicon != null)
            {
                try
                {
                    OnUpdatedFavicon(this, new UpdatedFaviconEventArgs(favicon, feedUrls));
                }
                catch
                {
                }
            }
        }
        protected void RaiseOnUpdatedFeed(Uri requestUri, Uri newUri, RequestResult result, int priority,
                                        bool firstSuccessfulDownload)
        {
            if (OnUpdatedFeed != null)
            {
                try
                {
                    OnUpdatedFeed(this,
                                  new UpdatedFeedEventArgs(requestUri, newUri, result, priority, firstSuccessfulDownload));
                }
                catch
                {
                }
            }
        }
        protected virtual void RaiseOnUpdateFeedException(string requestUri, Exception e, int priority)
        {
            if (OnUpdateFeedException != null)
            {
                try
                {
                    if (requestUri != null && RssParser.CanProcessUrl(requestUri))
                        e = new FeedRequestException(e.Message, e, this.GetFailureContext(requestUri));
                    OnUpdateFeedException(this, new UpdateFeedExceptionEventArgs(requestUri, e, priority));
                }
                catch
                {
                }
            }
        }
        protected void RaiseOnAllAsyncRequestsCompleted()
        {
            if (OnAllAsyncRequestsCompleted != null)
            {
                try
                {
                    OnAllAsyncRequestsCompleted(this, new EventArgs());
                }
                catch
                {
                }
            }
        }
        protected void RaiseOnAddedCategory(CategoryEventArgs cea)
        {
            if (OnAddedCategory != null)
            {
                try
                {
                    OnAddedCategory(this, cea);
                }
                catch
                {
                }
            }
        }
        protected void RaiseOnDeletedCategory(CategoryEventArgs cea)
        {
            if (OnDeletedCategory != null)
            {
                try
                {
                    OnDeletedCategory(this, cea);
                }
                catch
                {
                }
            }
        }
        protected void RaiseOnRenamedCategory(CategoryChangedEventArgs ccea)
        {
            if (OnRenamedCategory != null)
            {
                try
                {
                    OnRenamedCategory(this, ccea);
                }
                catch
                {
                }
            }
        }
        protected void RaiseOnDeletedFeed(FeedDeletedEventArgs fdea)
        {
            if (OnDeletedFeed != null)
            {
                try
                {
                    OnDeletedFeed(this, fdea);
                }
                catch
                {
                }
            }
        }
        protected void RaiseOnRenamedFeed(FeedRenamedEventArgs frea)
        {
            if (OnRenamedFeed != null)
            {
                try
                {
                    OnRenamedFeed(this, frea);
                }
                catch
                {
                }
            }
        }
        protected void RaiseOnAddedFeed(FeedChangedEventArgs fcea)
        {
            if (OnAddedFeed != null)
            {
                try
                {
                    OnAddedFeed(this, fcea);
                }
                catch
                {
                }
            }
        }
        protected void RaiseOnMovedCategory(CategoryChangedEventArgs ccea)
        {
            if (OnMovedCategory != null)
            {
                try
                {
                    OnMovedCategory(this, ccea);
                }
                catch
                {
                }
            }
        }
        protected void RaiseOnUpdateFeedsStarted(bool forced)
        {
            if (UpdateFeedsStarted != null)
            {
                try
                {
                    UpdateFeedsStarted(this, new UpdateFeedsEventArgs(forced));
                }
                catch
                {
                }
            }
        }
        protected void RaiseOnUpdateFeedStarted(Uri feedUri, bool forced, int priority)
        {
            if (UpdateFeedStarted != null)
            {
                try
                {
                    UpdateFeedStarted(this, new UpdateFeedEventArgs(feedUri, forced, priority));
                }
                catch
                {
                }
            }
        }
        private static string GenerateFaviconUrl(Uri uri, string extension)
        {
            return uri.Authority.Replace(".", "-") + extension;
        }
        public bool IsPodcast(string filename)
        {
            if (string.IsNullOrEmpty(filename))
            {
                return false;
            }
            string fileext = Path.GetExtension(filename);
            if (fileext.Length > 1)
            {
                fileext = fileext.Substring(1);
                foreach (string podcastExt in this.podcastfileextensions)
                {
                    if (fileext.ToLower().Equals(podcastExt.ToLower()))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        internal static void MarkEnclosuresDownloaded(INewsItem item)
        {
            if (item == null)
            {
                return;
            }
            foreach (Enclosure enc in item.Enclosures)
            {
                enc.Downloaded = true;
            }
        }
        protected int DownloadEnclosure(INewsItem item, int maxNumToDownload)
        {
            int numDownloaded = 0;
            if ((maxNumToDownload > 0) && (item != null) && (item.Enclosures.Count > 0))
            {
                foreach (Enclosure enc in item.Enclosures)
                {
                    DownloadItem di = new DownloadItem(item.Feed.link, item.Id, enc, this.enclosureDownloader);
                    if (!enc.Downloaded)
                    {
                        this.enclosureDownloader.BeginDownload(di);
                        enc.Downloaded = true;
                        numDownloaded++;
                    }
                    if (numDownloaded >= maxNumToDownload) break;
                }
            }
            if (item != null && numDownloaded < item.Enclosures.Count)
            {
                MarkEnclosuresDownloaded(item);
            }
            return numDownloaded;
        }
        public void DownloadEnclosure(INewsItem item)
        {
            this.DownloadEnclosure(item, Int32.MaxValue);
        }
        public void DownloadEnclosure(INewsItem item, string fileName)
        {
            if ((item != null) && (item.Enclosures.Count > 0))
            {
                foreach (Enclosure enc in item.Enclosures)
                {
                    if (enc.Url.EndsWith(fileName))
                    {
                        DownloadItem di = new DownloadItem(item.Feed.link, item.Id, enc, this.enclosureDownloader);
                        this.enclosureDownloader.BeginDownload(di);
                        enc.Downloaded = true;
                        break;
                    }
                }
            }
        }
        public virtual void ResumePendingDownloads()
        {
            this.enclosureDownloader.ResumePendingDownloads();
        }
        public void RefreshFavicons()
        {
            if ((this.FeedsListOK == false) || this.isOffline)
            {
                return;
            }
            StringCollection websites = new StringCollection();
            try
            {
                string[] keys = GetFeedsTableKeys();
                for (int i = 0, len = keys.Length; i < len; i++)
                {
                    if (!itemsTable.ContainsKey(keys[i]))
                    {
                        continue;
                    }
                    FeedInfo fi = (FeedInfo) itemsTable[keys[i]];
                    Uri webSiteUrl = null;
                    try
                    {
                        webSiteUrl = new Uri(fi.link);
                    }
                    catch (Exception)
                    {
                        ;
                    }
                    if (webSiteUrl == null || !webSiteUrl.Scheme.ToLower().Equals("http"))
                    {
                        continue;
                    }
                    if (!websites.Contains(webSiteUrl.Authority))
                    {
                        UriBuilder reqUri = new UriBuilder("http", webSiteUrl.Authority);
                        reqUri.Path = "favicon.ico";
                        try
                        {
                            webSiteUrl = reqUri.Uri;
                        }
                        catch (UriFormatException)
                        {
                            _log.ErrorFormat("Error creating URL '{0}/{1}' in RefreshFavicons", webSiteUrl,
                                             "favicon.ico");
                            continue;
                        }
                        RequestParameter reqParam = RequestParameter.Create(webSiteUrl, this.UserAgent, this.Proxy,
                                                                             null,
                                                                             DateTime.MinValue,
                                                                             null);
                        reqParam.SetCookies = SetCookies;
                        AsyncWebRequest.QueueRequest(reqParam,
                                                     null ,
                                                     null ,
                                                     OnFaviconRequestComplete,
                                                     null ,
                                                     100 );
                        websites.Add(webSiteUrl.Authority);
                    }
                }
            }
            catch (InvalidOperationException ioe)
            {
                Trace("RefreshFavicons() InvalidOperationException: {0}", ioe.ToString());
            }
        }
        public virtual void RefreshFeeds(bool force_download)
        {
            if (this.FeedsListOK == false)
            {
                return;
            }
            bool anyRequestQueued = false;
            try
            {
                RaiseOnUpdateFeedsStarted(force_download);
                string[] keys = GetFeedsTableKeys();
                for (int i = 0, len = keys.Length; i < len; i++)
                {
                    if (!feedsTable.ContainsKey(keys[i]))
                        continue;
                    INewsFeed current = feedsTable[keys[i]];
                    try
                    {
                        if (!force_download && current.causedExceptionCount >= 10)
                        {
                            continue;
                        }
                        if (current.refreshrateSpecified && (current.refreshrate == 0))
                        {
                            continue;
                        }
                        if (itemsTable.ContainsKey(current.link))
                        {
                            if ((!force_download) && current.lastretrievedSpecified)
                            {
                                double timeSinceLastDownload =
                                    DateTime.Now.Subtract(current.lastretrieved).TotalMilliseconds;
                                int refreshRate = this.GetRefreshRate(current.link);
                                if (!DownloadIntervalReached || (timeSinceLastDownload < refreshRate))
                                {
                                    continue;
                                }
                            }
                            if (this.AsyncGetItemsForFeed(current.link, true, false))
                                anyRequestQueued = true;
                        }
                        else
                        {
                            if ((!force_download) && current.lastretrievedSpecified && string.IsNullOrEmpty(current.cacheurl))
                            {
                                double timeSinceLastDownload =
                                    DateTime.Now.Subtract(current.lastretrieved).TotalMilliseconds;
                                int refreshRate = this.GetRefreshRate(current.link);
                                if (!DownloadIntervalReached || (timeSinceLastDownload < refreshRate))
                                {
                                    continue;
                                }
                            }
                            if (!force_download)
                            {
                                if (!string.IsNullOrEmpty(current.cacheurl) &&
                                    !this.CacheHandler.FeedExists(current))
                                    force_download = true;
                            }
                            if (this.AsyncGetItemsForFeed(current.link, force_download, false))
                                anyRequestQueued = true;
                        }
                        Thread.Sleep(15);
                    }
                    catch (Exception e)
                    {
                        Trace("RefreshFeeds(bool) unexpected error processing feed '{0}': {1}", keys[i], e.ToString());
                    }
                }
            }
            catch (InvalidOperationException ioe)
            {
                Trace("RefreshFeeds(bool) InvalidOperationException: {0}", ioe.ToString());
            }
            finally
            {
                if (isOffline || !anyRequestQueued)
                    RaiseOnAllAsyncRequestsCompleted();
            }
        }
        public virtual void RefreshFeeds(string category, bool force_download)
        {
            if (this.FeedsListOK == false)
            {
                return;
            }
            bool anyRequestQueued = false;
            try
            {
                RaiseOnUpdateFeedsStarted(force_download);
                string[] keys = GetFeedsTableKeys();
                for (int i = 0, len = keys.Length; i < len; i++)
                {
                    if (!feedsTable.ContainsKey(keys[i]))
                        continue;
                    INewsFeed current = feedsTable[keys[i]];
                    try
                    {
                        if (!force_download && current.causedExceptionCount >= 3)
                        {
                            continue;
                        }
                        if (current.refreshrateSpecified && (current.refreshrate == 0))
                        {
                            continue;
                        }
                        if (itemsTable.ContainsKey(current.link))
                        {
                            if ((!force_download) && current.lastretrievedSpecified)
                            {
                                double timeSinceLastDownload =
                                    DateTime.Now.Subtract(current.lastretrieved).TotalMilliseconds;
                                int refreshRate = this.GetRefreshRate(current.link);
                                if (!DownloadIntervalReached || (timeSinceLastDownload < refreshRate))
                                {
                                    continue;
                                }
                            }
                            if (current.category != null && IsChildOrSameCategory(category, current.category))
                            {
                                if (this.AsyncGetItemsForFeed(current.link, true, false))
                                    anyRequestQueued = true;
                            }
                        }
                        else
                        {
                            if (current.category != null && IsChildOrSameCategory(category, current.category))
                            {
                                if (this.AsyncGetItemsForFeed(current.link, force_download, false))
                                    anyRequestQueued = true;
                            }
                        }
                        Thread.Sleep(15);
                    }
                    catch (Exception e)
                    {
                        Trace("RefreshFeeds(string,bool) unexpected error processing feed '{0}': {1}", current.link,
                              e.ToString());
                    }
                }
            }
            catch (InvalidOperationException ioe)
            {
                Trace("RefreshFeeds(string,bool) InvalidOperationException: {0}", ioe.ToString());
            }
            finally
            {
                if (isOffline || !anyRequestQueued)
                    RaiseOnAllAsyncRequestsCompleted();
            }
        }
        protected static bool IsChildOrSameCategory(string category, string testCategory)
        {
            if (testCategory.Equals(category) || testCategory.StartsWith(category + CategorySeparator))
                return true;
            else
                return false;
        }
        public XmlDocument ConvertFeedList(XmlDocument doc)
        {
            ImportFilter importFilter = new ImportFilter(doc);
            XslTransform transform = importFilter.GetImportXsl();
            if (transform != null)
            {
                XmlDocument temp = new XmlDocument();
                temp.Load(transform.Transform(doc, null));
                doc = temp;
            }
            else
            {
                if (importFilter.Format == ImportFeedFormat.Bandit)
                {
                    XmlParserContext context =
                        new XmlParserContext(null, new RssBanditXmlNamespaceResolver(), null, XmlSpace.None);
                    XmlReader vr = new RssBanditXmlReader(doc.OuterXml, XmlNodeType.Document, context);
                    doc.Load(vr);
                    vr.Close();
                }
                else
                {
                    throw new ApplicationException("Unknown Feed Format.", null);
                }
            }
            return doc;
        }
        public void ReplaceFeedlist(Stream feedlist)
        {
            this.ImportFeedlist(feedlist, String.Empty, true);
        }
        public void ImportFeedlist(Stream feedlist, string category, bool replace)
        {
            XmlDocument doc = new XmlDocument();
            doc.Load(feedlist);
            doc = ConvertFeedList(doc);
            XmlNodeReader reader = new XmlNodeReader(doc);
            XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof (feeds));
            feeds myFeeds = (feeds) serializer.Deserialize(reader);
            reader.Close();
            bool keepLocalSettings = true;
            this.ImportFeedlist(myFeeds, category, replace, keepLocalSettings);
        }
        public void ImportFeedlist(feeds myFeeds, string category, bool replace, bool keepLocalSettings)
        {
            IDictionary<string, INewsFeedCategory> cats = new Dictionary<string, INewsFeedCategory>();
            FeedColumnLayoutCollection colLayouts = new FeedColumnLayoutCollection();
            IDictionary<string, INewsFeed> syncedfeeds = new SortedDictionary<string, INewsFeed>();
            DateTime[] dta = RssHelper.InitialLastRetrievedSettings(myFeeds.feed.Count, this.RefreshRate);
            int dtaCount = dta.Length, count = 0;
            while (myFeeds.feed.Count != 0)
            {
                INewsFeed f1 = myFeeds.feed[0];
                bool isBadUri = false;
                try
                {
                    new Uri(f1.link);
                }
                catch (Exception)
                {
                    isBadUri = true;
                }
                if (isBadUri)
                {
                    myFeeds.feed.RemoveAt(0);
                    continue;
                }
                if (replace && feedsTable.ContainsKey(f1.link))
                {
                    INewsFeed f2 = feedsTable[f1.link];
                    if (!keepLocalSettings)
                    {
                        f2.category = f1.category;
                        if ((f2.category != null) && !cats.ContainsKey(f2.category))
                        {
                            cats.Add(f2.category, new category(f2.category));
                        }
                        if ((f1.listviewlayout != null) && !colLayouts.ContainsKey(f1.listviewlayout))
                        {
                            listviewLayout layout = FindLayout(f1.listviewlayout, myFeeds.listviewLayouts);
                            if (layout != null)
                                colLayouts.Add(f1.listviewlayout, layout.FeedColumnLayout);
                            else
                                f1.listviewlayout = null;
                        }
                        f2.listviewlayout = (f1.listviewlayout ?? f2.listviewlayout);
                        f2.title = f1.title;
                        f2.markitemsreadonexitSpecified = f1.markitemsreadonexitSpecified;
                        if (f1.markitemsreadonexitSpecified)
                        {
                            f2.markitemsreadonexit = f1.markitemsreadonexit;
                        }
                        f2.stylesheet = (f1.stylesheet ?? f2.stylesheet);
                        f2.maxitemage = (f1.maxitemage ?? f2.maxitemage);
                        f2.alertEnabledSpecified = f1.alertEnabledSpecified;
                        f2.alertEnabled = (f1.alertEnabledSpecified ? f1.alertEnabled : f2.alertEnabled);
                        f2.refreshrateSpecified = f1.refreshrateSpecified;
                        f2.refreshrate = (f1.refreshrateSpecified ? f1.refreshrate : f2.refreshrate);
                        f2.authPassword = f1.authPassword;
                        f2.authUser = f1.authUser;
                    }
                    foreach (string story in f1.deletedstories)
                    {
                        if (!f2.deletedstories.Contains(story))
                        {
                            f2.AddDeletedStory(story);
                        }
                    }
                    foreach (string story in f1.storiesrecentlyviewed)
                    {
                        if (!f2.storiesrecentlyviewed.Contains(story))
                        {
                            f2.AddViewedStory(story);
                        }
                    }
                    if (itemsTable.ContainsKey(f2.link))
                    {
                        List<INewsItem> items = ((FeedInfo) itemsTable[f2.link]).itemsList;
                        foreach (INewsItem item in items)
                        {
                            if (f2.storiesrecentlyviewed.Contains(item.Id))
                            {
                                item.BeenRead = true;
                            }
                        }
                    }
                    f2.owner = this;
                    syncedfeeds.Add(f2.link, f2);
                }
                else
                {
                    if (replace)
                    {
                        if ((f1.category != null) && !cats.ContainsKey(f1.category))
                        {
                            cats.Add(f1.category, new category(f1.category));
                        }
                        if ((f1.listviewlayout != null) && !colLayouts.ContainsKey(f1.listviewlayout))
                        {
                            listviewLayout layout = FindLayout(f1.listviewlayout, myFeeds.listviewLayouts);
                            if (layout != null)
                                colLayouts.Add(f1.listviewlayout, layout.FeedColumnLayout);
                            else
                                f1.listviewlayout = null;
                        }
                        if (!syncedfeeds.ContainsKey(f1.link))
                        {
                            syncedfeeds.Add(f1.link, f1);
                        }
                    }
                    else
                    {
                        if (category.Length > 0)
                        {
                            f1.category = (f1.category == null ? category : category + CategorySeparator + f1.category);
                        }
                        if (!feedsTable.ContainsKey(f1.link))
                        {
                            f1.lastretrievedSpecified = true;
                            f1.lastretrieved = dta[count%dtaCount];
                            feedsTable.Add(f1.link, f1);
                        }
                    }
                }
                myFeeds.feed.RemoveAt(0);
                count++;
            }
            IDictionary<string, INntpServerDefinition> serverList = new Dictionary<string, INntpServerDefinition>();
            IDictionary<string, UserIdentity> identityList = new Dictionary<string, UserIdentity>();
            foreach (UserIdentity identity in myFeeds.identities)
            {
                if (replace)
                {
                    identityList.Add(identity.Name, identity);
                }
                else if (!this.identities.ContainsKey(identity.Name))
                {
                    this.identities.Add(identity.Name, identity);
                }
            }
            foreach (NntpServerDefinition server in myFeeds.nntpservers)
            {
                if (replace)
                {
                    serverList.Add(server.Name, server);
                }
                else if (!this.identities.ContainsKey(server.Name))
                {
                    this.nntpServers.Add(server.Name, server);
                }
            }
            foreach (listviewLayout layout in myFeeds.listviewLayouts)
            {
                if (replace)
                {
                    if (layout.FeedColumnLayout.LayoutType == LayoutType.GlobalFeedLayout ||
                        layout.FeedColumnLayout.LayoutType == LayoutType.GlobalCategoryLayout ||
                        layout.FeedColumnLayout.LayoutType == LayoutType.SearchFolderLayout ||
                        layout.FeedColumnLayout.LayoutType == LayoutType.SpecialFeedsLayout)
                        colLayouts.Add(layout.ID, layout.FeedColumnLayout);
                }
                else if (!this.layouts.ContainsKey(layout.ID))
                {
                    if (layout.FeedColumnLayout.LayoutType != LayoutType.GlobalFeedLayout ||
                        layout.FeedColumnLayout.LayoutType != LayoutType.GlobalCategoryLayout ||
                        layout.FeedColumnLayout.LayoutType != LayoutType.SearchFolderLayout ||
                        layout.FeedColumnLayout.LayoutType != LayoutType.SpecialFeedsLayout)
                        this.layouts.Add(layout.ID, layout.FeedColumnLayout);
                }
            }
            if (replace)
            {
                this.feedsTable = syncedfeeds;
                this.categories = cats;
                this.identities = identityList;
                this.nntpServers = serverList;
                this.layouts = colLayouts;
            }
            else
            {
                if (myFeeds.categories.Count == 0)
                {
                    if (category.Length > 0 && this.categories.ContainsKey(category) == false)
                    {
                        this.AddCategory(category);
                    }
                }
                else
                {
                    foreach (category cat in myFeeds.categories)
                    {
                        string cat2 = (category.Length == 0 ? cat.Value : category + CategorySeparator + cat.Value);
                        if (this.categories.ContainsKey(cat2) == false)
                        {
                            this.AddCategory(cat2);
                        }
                    }
                }
            }
            if (validationErrorOccured)
            {
                validationErrorOccured = false;
            }
        }
        public void ImportFeedlist(Stream feedlist)
        {
            this.ImportFeedlist(feedlist, String.Empty, false);
        }
        public void ImportFeedlist(Stream feedlist, string category)
        {
            try
            {
                this.ImportFeedlist(feedlist, category, false);
            }
            catch (Exception e)
            {
                throw new ApplicationException(e.Message, e);
            }
        }
        public static void ValidationCallbackOne(object sender,
                                                 ValidationEventArgs args)
        {
            if (args.Severity == XmlSeverityType.Error)
            {
                Trace("ValidationCallbackOne() message: {0}", args.Message);
                XmlSchemaException xse = args.Exception;
                if (xse != null)
                {
                    Type xseType = xse.GetType();
                    FieldInfo resFieldInfo = xseType.GetField("res", BindingFlags.NonPublic | BindingFlags.Instance);
                    string errorType = (string) resFieldInfo.GetValue(xse);
                    if (!errorType.Equals("Sch_UnresolvedKeyref") && !errorType.Equals("Sch_DuplicateKey"))
                    {
                        validationErrorOccured = true;
                    }
                    else
                    {
                    }
                }
            }
        }
        protected string SaveFeed(INewsFeed feed)
        {
            TimeSpan maxItemAge = this.GetMaxItemAge(feed.link);
            FeedDetailsInternal fi = this.itemsTable[feed.link] as FeedDetailsInternal;
            IList<INewsItem> items = fi.ItemsList;
            if (maxItemAge != TimeSpan.MinValue)
            {
                lock (items)
                {
                    for (int i = 0, count = items.Count; i < count; i++)
                    {
                        INewsItem item = items[i];
                        if (feed.deletedstories.Contains(item.Id) || ((DateTime.Now - item.Date) >= maxItemAge))
                        {
                            items.Remove(item);
                            RelationCosmosRemove(item);
                            SearchHandler.IndexRemove(item);
                            count--;
                            i--;
                        }
                    }
                }
            }
            return this.CacheHandler.SaveFeed(fi);
        }
        internal FeedDetailsInternal GetFeed(INewsFeed feed)
        {
            FeedDetailsInternal fi = this.CacheHandler.GetFeed(feed);
            if (fi != null)
            {
                TimeSpan maxItemAge = this.GetMaxItemAge(feed.link);
                int readItems = 0;
                IList<INewsItem> items = fi.ItemsList;
                lock (items)
                {
                    bool keepAll = (maxItemAge == TimeSpan.MinValue) && (feed.deletedstories.Count == 0);
                    maxItemAge = (maxItemAge == TimeSpan.MinValue ? TimeSpan.MaxValue : maxItemAge);
                    for (int i = 0, count = items.Count; i < count; i++)
                    {
                        INewsItem item = items[i];
                        if ((!keepAll) && ((DateTime.Now - item.Date) >= maxItemAge) ||
                            feed.deletedstories.Contains(item.Id))
                        {
                            items.RemoveAt(i);
                            RelationCosmosRemove(item);
                            i--;
                            count--;
                        }
                        else if (item.BeenRead)
                        {
                            readItems++;
                        }
                        INotifyPropertyChanged inpc = item as INotifyPropertyChanged;
                        if (inpc != null)
                        {
                            inpc.PropertyChanged -= this.OnNewsItemPropertyChanged;
                            inpc.PropertyChanged += this.OnNewsItemPropertyChanged;
                        }
                    }
                }
                if (readItems == items.Count)
                {
                    feed.containsNewMessages = false;
                }
                else
                {
                    feed.containsNewMessages = true;
                }
            }
            return fi;
        }
        public static List<INewsItem> MergeAndPurgeItems(List<INewsItem> oldItems, List<INewsItem> newItems,
                                                        ICollection<string> deletedItems, out List<INewsItem> receivedNewItems,
                                                        bool onlyKeepNewItems, bool respectOldItemState)
        {
            receivedNewItems = new List<INewsItem>();
            lock (oldItems)
            {
                foreach (NewsItem newitem in newItems)
                {
                    int index = oldItems.IndexOf(newitem);
                    if (index == -1)
                    {
                        if (!deletedItems.Contains(newitem.Id))
                        {
                            receivedNewItems.Add(newitem);
                            oldItems.Add(newitem);
                            ReceivingNewsChannelServices.ProcessItem(newitem);
                        }
                    }
                    else
                    {
                        INewsItem olditem = oldItems[index];
                        if (respectOldItemState)
                        {
                            newitem.BeenRead = olditem.BeenRead;
                        }
                        newitem.Date = olditem.Date;
                        if (respectOldItemState)
                        {
                            newitem.FlagStatus = olditem.FlagStatus;
                        }
                        if (olditem.WatchComments)
                        {
                            newitem.WatchComments = true;
                            if ((olditem.HasNewComments) || (olditem.CommentCount < newitem.CommentCount))
                            {
                                newitem.HasNewComments = true;
                            }
                        }
                        if (newitem.CommentCount == NewsItem.NoComments)
                        {
                            newitem.CommentCount = olditem.CommentCount;
                        }
                        if (olditem.Enclosures.Count > 0)
                        {
                            foreach (Enclosure enc in olditem.Enclosures)
                            {
                                int j = newitem.Enclosures.IndexOf(enc);
                                if (j != -1)
                                {
                                    IEnclosure oldEnc = newitem.Enclosures[j];
                                    enc.Downloaded = oldEnc.Downloaded;
                                }
                                else
                                {
                                    if (ReferenceEquals(newitem.Enclosures, GetList<IEnclosure>.Empty))
                                    {
                                        newitem.Enclosures = new List<IEnclosure>();
                                    }
                                    newitem.Enclosures.Add(enc);
                                }
                            }
                        }
                        oldItems.RemoveAt(index);
                        oldItems.Add(newitem);
                        RelationCosmosRemove(olditem);
                    }
                }
                RelationCosmosAddRange(receivedNewItems);
            }
            if (onlyKeepNewItems)
            {
                return newItems;
            }
            else
            {
                return oldItems;
            }
        }
        public void PostComment(string url, INewsItem item2post, INewsItem inReply2item)
        {
            if (inReply2item.CommentStyle == SupportedCommentStyle.CommentAPI)
            {
    this.RssParserInstance.PostCommentViaCommentAPI(url, item2post, inReply2item,
                                                        GetFeedCredentials(inReply2item.Feed));
            }
            else if (inReply2item.CommentStyle == SupportedCommentStyle.NNTP)
            {
                NntpParser.PostCommentViaNntp(item2post, inReply2item, GetNntpServerCredentials(inReply2item.Feed));
            }
        }
        public void PostComment(INewsItem item2post, INewsFeed postTarget)
        {
            if (item2post.CommentStyle == SupportedCommentStyle.NNTP)
            {
                NntpParser.PostCommentViaNntp(item2post, postTarget, GetNntpServerCredentials(postTarget));
            }
        }
        public ICollection<INewsItem> GetItemsWithIncomingLinks(INewsItem item, IList<INewsItem> excludeItemsList)
        {
            if (buildRelationCosmos)
                return relationCosmos.GetIncoming(item, excludeItemsList);
            else
                return new List<INewsItem>();
        }
        public IList<INewsItem> GetItemsWithIncomingLinks(string url, DateTime since)
        {
            url = RelationCosmos.RelationCosmos.UrlTable.Add(url);
            if (buildRelationCosmos)
                return relationCosmos.GetIncoming<INewsItem>(url, since);
            else
                return new List<INewsItem>();
        }
        public ICollection<INewsItem> GetItemsFromOutGoingLinks(INewsItem item, IList<INewsItem> excludeItemsList)
        {
            if (buildRelationCosmos)
                return relationCosmos.GetOutgoing(item, excludeItemsList);
            else
                return new List<INewsItem>();
        }
        public bool HasItemAnyRelations(INewsItem item, IList<INewsItem> excludeItemsList)
        {
            if (buildRelationCosmos)
                return relationCosmos.HasIncomingOrOutgoing(item, excludeItemsList);
            else
                return false;
        }
        internal static void RelationCosmosAdd<T>(T relation)
            where T : IRelation
        {
            if (buildRelationCosmos)
                relationCosmos.Add(relation);
            else
                return;
        }
        internal static void RelationCosmosAddRange<T>(IEnumerable<T> relations)
            where T : IRelation
        {
            if (buildRelationCosmos)
                relationCosmos.AddRange(relations);
            else
                return;
        }
        internal static void RelationCosmosRemove<T>(T relation)
            where T : IRelation
        {
            if (buildRelationCosmos)
                relationCosmos.Remove(relation);
            else
                return;
        }
        internal static void RelationCosmosRemoveRange<T>(IList<T> relations)
            where T : IRelation
        {
            if (buildRelationCosmos)
                relationCosmos.RemoveRange(relations);
            else
                return;
        }
        public void RegisterReceivingNewsChannel(INewsChannel channel)
        {
            receivingNewsChannel.RegisterNewsChannel(channel);
        }
        public void UnregisterReceivingNewsChannel(INewsChannel channel)
        {
            receivingNewsChannel.UnregisterNewsChannel(channel);
        }
        internal static NewsChannelServices ReceivingNewsChannelServices
        {
            get
            {
                return receivingNewsChannel;
            }
        }
  private static object GetSharedPropertyValue(ISharedProperty instance, string propertyName) {
   switch (propertyName)
   {
    case "maxitemage": return instance.maxitemage;
    case "downloadenclosures": return instance.downloadenclosures;
    case "downloadenclosuresSpecified": return instance.downloadenclosuresSpecified;
    case "enclosurealert": return instance.enclosurealert;
    case "enclosurealertSpecified": return instance.enclosurealertSpecified;
    case "enclosurefolder": return instance.enclosurefolder;
    case "listviewlayout": return instance.listviewlayout;
    case "markitemsreadonexit": return instance.markitemsreadonexit;
    case "markitemsreadonexitSpecified": return instance.markitemsreadonexitSpecified;
    case "refreshrate": return instance.refreshrate;
    case "refreshrateSpecified": return instance.refreshrateSpecified;
    case "stylesheet": return instance.stylesheet;
    default: Debug.Assert(true, "unknown shared property name: " + propertyName);
     break;
   }
   return null;
  }
  private static void SetSharedPropertyValue(ISharedProperty instance, string propertyName, object value)
  {
   switch (propertyName)
   {
    case "maxitemage": instance.maxitemage = value as string;
     break;
    case "downloadenclosures": instance.downloadenclosures = (bool)value;
     break;
    case "downloadenclosuresSpecified": instance.downloadenclosuresSpecified = (bool)value;
     break;
    case "enclosurealert": instance.enclosurealert = (bool)value;
     break;
    case "enclosurealertSpecified": instance.enclosurealertSpecified = (bool)value;
     break;
    case "enclosurefolder": instance.enclosurefolder = value as string;
     break;
    case "listviewlayout": instance.listviewlayout = value as string;
     break;
    case "markitemsreadonexit": instance.markitemsreadonexit = (bool)value;
     break;
    case "markitemsreadonexitSpecified": instance.markitemsreadonexitSpecified = (bool)value;
     break;
    case "refreshrate": instance.refreshrate = (int)value;
     break;
    case "refreshrateSpecified": instance.refreshrateSpecified = (bool)value;
     break;
    case "stylesheet": instance.stylesheet = value as string;
     break;
    default: Debug.Assert(true, "unknown shared property name: " + propertyName);
     break;
   }
  }
     string ISharedProperty.maxitemage {
      get { return XmlConvert.ToString(this.MaxItemAge); }
      set { this.MaxItemAge = XmlConvert.ToTimeSpan(value); }
     }
     int ISharedProperty.refreshrate {
      get { return this.RefreshRate; }
   set { }
     }
     bool ISharedProperty.refreshrateSpecified {
      get { return true; }
   set { }
     }
     bool ISharedProperty.downloadenclosures {
      get { return p_configuration.DownloadEnclosures; }
   set { }
     }
     bool ISharedProperty.downloadenclosuresSpecified {
   get { return true; }
   set { }
     }
     bool ISharedProperty.enclosurealert {
      get { return EnclosureAlert; }
   set { }
     }
     bool ISharedProperty.enclosurealertSpecified {
   get { return true; }
   set { }
     }
     string ISharedProperty.enclosurefolder {
   get { return EnclosureFolder; }
   set { EnclosureFolder = value; }
     }
     string ISharedProperty.listviewlayout {
      get { return this.listviewlayout; }
   set { this.listviewlayout = value; }
     }
     string ISharedProperty.stylesheet {
      get { return Stylesheet; }
   set { Stylesheet = value; }
     }
     bool ISharedProperty.markitemsreadonexit {
      get { return MarkItemsReadOnExit; }
   set { MarkItemsReadOnExit = value; }
     }
     bool ISharedProperty.markitemsreadonexitSpecified {
   get { return true; }
   set { }
     }
    }
    [Flags]
    public enum NewsFeedProperty
    {
        None = 0,
        FeedLink = 0x1,
        FeedUrl = 0x2,
        FeedTitle = 0x4,
        FeedCategory = 0x8,
        FeedDescription = 0x10,
        FeedType = 0x20,
        FeedItemsDeleteUndelete = 0x40,
        FeedItemFlag = 0x80,
        FeedItemReadState = 0x100,
        FeedItemCommentCount = 0x200,
        FeedMaxItemAge = 0x400,
        FeedItemWatchComments = 0x800,
        FeedRefreshRate = 0x1000,
        FeedCacheUrl = 0x2000,
        FeedAdded = 0x4000,
        FeedRemoved = 0x8000,
        FeedCategoryRemoved = 0x10000,
        FeedCategoryAdded = 0x20000,
        FeedCredentials = 0x40000,
        FeedAlertOnNewItemsReceived = 0x80000,
        FeedMarkItemsReadOnExit = 0x100000,
        FeedStylesheet = 0x200000,
        FeedItemNewCommentsRead = 0x400000,
        General = 0x8000000,
    }
    internal interface FeedDetailsInternal : IFeedDetails
    {
        string FeedLocation { get; set; }
        void WriteItemContents(BinaryReader reader, BinaryWriter writer);
        void WriteTo(XmlWriter writer, bool noDescriptions);
    }
    public interface ISizeInfo
    {
        int GetSize();
        string GetSizeDetails();
    }
    internal class RssBanditXmlNamespaceResolver : XmlNamespaceManager
    {
        public RssBanditXmlNamespaceResolver() : base(new NameTable())
        {
        }
        public override void AddNamespace(string prefix, string uri)
        {
            if (uri == NamespaceCore.Feeds_v2003)
            {
                uri = NamespaceCore.Feeds_vCurrent;
            }
            base.AddNamespace(prefix, uri);
        }
    }
    internal class RssBanditXmlReader : XmlTextReader
    {
        public RssBanditXmlReader(Stream s, XmlNodeType nodeType, XmlParserContext context) : base(s, nodeType, context)
        {
        }
        public RssBanditXmlReader(string s, XmlNodeType nodeType, XmlParserContext context) : base(s, nodeType, context)
        {
        }
        public override string Value
        {
            get
            {
                if ((this.NodeType == XmlNodeType.Attribute) &&
                    (base.Value == NamespaceCore.Feeds_v2003))
                {
                    return NamespaceCore.Feeds_vCurrent;
                }
                else
                {
                    return base.Value;
                }
            }
        }
    }
}
