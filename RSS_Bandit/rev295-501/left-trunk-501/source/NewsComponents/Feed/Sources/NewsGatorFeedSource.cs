using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Linq;
using System.Net;
using System.ServiceModel;
using System.ServiceModel.Description;
using System.ServiceModel.Channels;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using log4net;
using RssBandit.Common;
using RssBandit.Common.Logging;
using NewsComponents.Collections;
using NewsComponents.Net;
using NewsComponents.Search;
using NewsComponents.Utils;
namespace NewsComponents.Feed
{
    public enum NewsGatorFlagStatus
    {
        None = 0,
        FollowUp = 6,
        Forward = 5,
        Read = 3,
        Review = 4,
        Reply = 1,
        Complete = 1000
    }
    internal enum NewsGatorItemState
    {
        Read, Unread, Deleted
    }
    class NewsGatorFeedSource : FeedSource
    {
        private static readonly ILog _log = Log.GetLogger(typeof(NewsGatorFeedSource));
        private string NgosSyncToken;
        private static readonly string NoXmlUrlFoundInOpml = "http://www.example.com/no-url-for-rss-feed-provided-in-imported-opml";
        private static readonly string NgosProductKey = "7AF62582A5334A9CADF967818E734558";
        private static readonly WebHeaderCollection NgosTokenHeader = new WebHeaderCollection();
        private static readonly string NgosLocationName = "RssBandit-" + Environment.MachineName;
        private static readonly string LocationApiUrl = "http://services.newsgator.com/ngws/svc/Location.aspx";
        private static readonly string SubscriptionApiUrl = "http://services.newsgator.com/ngws/svc/Subscription.aspx";
       private static readonly string FeedApiUrl = "http://services.newsgator.com/ngws/svc/Feed.aspx";
       private static readonly string PostItemApiUrl = "http://services.newsgator.com/ngws/svc/PostItem.aspx";
       private static readonly string FolderApiUrl = "http://services.newsgator.com/ngws/svc/Folder.aspx";
       private static readonly string NewsGatorRssNS = "http://newsgator.com/schema/extensions";
       private static readonly string NewsGatorOpmlNS = "http://newsgator.com/schema/opml";
       private static NewsGatorModifier newsGatorUpdater;
       static NewsGatorFeedSource()
       {
           NgosTokenHeader.Add("X-NGAPIToken", NgosProductKey);
       }
        internal NewsGatorFeedSource(INewsComponentsConfiguration configuration, SubscriptionLocation location)
        {
            this.p_configuration = configuration;
            if (this.p_configuration == null)
                this.p_configuration = FeedSource.DefaultConfiguration;
            this.location = location;
            NewsGatorUpdater.RegisterFeedSource(this);
            ValidateAndThrow(this.Configuration);
            this.rssParser = new RssParser(this);
            this.PodcastFolder = this.Configuration.DownloadedFilesDataPath;
            if (String.IsNullOrEmpty(EnclosureFolder))
            {
                this.enclosureDownloader = new BackgroundDownloadManager(this.Configuration, this);
                this.enclosureDownloader.DownloadCompleted += this.OnEnclosureDownloadComplete;
            }
            this.AsyncWebRequest = new AsyncWebRequest();
            this.AsyncWebRequest.OnAllRequestsComplete += this.OnAllRequestsComplete;
        }
        public string NewsGatorUserName
        {
            get { return this.location.Credentials.UserName; }
        }
        public NewsGatorModifier NewsGatorUpdater
        {
            get
            {
                if (newsGatorUpdater == null)
                {
                    newsGatorUpdater = new NewsGatorModifier(this.Configuration.UserApplicationDataPath);
                }
                return newsGatorUpdater;
            }
            set
            {
                newsGatorUpdater = value;
            }
        }
        public override void SaveFeedList(Stream feedStream)
        {
            base.SaveFeedList(feedStream);
            NewsGatorUpdater.SavePendingOperations();
        }
        private static NewsFeed TransferSettings(NewsFeed ngFeed, NewsFeed banditfeed)
        {
            if (banditfeed != null)
            {
                ngFeed.lastretrievedSpecified = banditfeed.lastretrievedSpecified;
                ngFeed.lastretrieved = banditfeed.lastretrieved;
                ngFeed.lastmodifiedSpecified = banditfeed.lastmodifiedSpecified;
                ngFeed.lastmodified = banditfeed.lastmodified;
                ngFeed.id = banditfeed.id;
                ngFeed.enclosurefolder = banditfeed.enclosurefolder;
                ngFeed.deletedstories = banditfeed.deletedstories;
                ngFeed.storiesrecentlyviewed = banditfeed.storiesrecentlyviewed;
                ngFeed.refreshrate = banditfeed.refreshrate;
                ngFeed.refreshrateSpecified = banditfeed.refreshrateSpecified;
                ngFeed.maxitemage = banditfeed.maxitemage;
                ngFeed.markitemsreadonexit = banditfeed.markitemsreadonexit;
                ngFeed.markitemsreadonexitSpecified = banditfeed.markitemsreadonexitSpecified;
                ngFeed.listviewlayout = banditfeed.listviewlayout;
                ngFeed.favicon = banditfeed.favicon;
                ngFeed.stylesheet = banditfeed.stylesheet;
                ngFeed.cacheurl = banditfeed.cacheurl;
                ngFeed.enclosurealert = banditfeed.enclosurealert;
                ngFeed.enclosurealertSpecified = banditfeed.enclosurealertSpecified;
                ngFeed.alertEnabled = banditfeed.alertEnabled;
                ngFeed.alertEnabledSpecified = banditfeed.alertEnabledSpecified;
            }
            return ngFeed;
        }
        private void CreateLocation()
        {
            opml location = new opml();
            location.body = new opmloutline[1];
            opmloutline outline = new opmloutline();
            outline.text = NgosLocationName;
            location.body[0] = outline;
            XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(opml));
            StringBuilder sb = new StringBuilder();
            XmlWriterSettings xws = new XmlWriterSettings();
            xws.OmitXmlDeclaration = true;
            serializer.Serialize(XmlWriter.Create(sb, xws), location);
            HttpWebResponse response = AsyncWebRequest.PostSyncResponse(LocationApiUrl, sb.ToString(), this.location.Credentials , this.Proxy, NgosTokenHeader);
            if (response.StatusCode != HttpStatusCode.OK)
            {
                _log.Debug(String.Format("Error occured when creating location in NewsGator Online: {0}-{1}", response.StatusCode, response.StatusDescription));
            }
        }
        private feeds LoadFeedlistFromNewsGatorOnline()
        {
            this.CreateLocation();
            string feedlistUrl = SubscriptionApiUrl + "/" + NgosLocationName;
            XmlDocument doc = new XmlDocument();
            doc.Load(XmlReader.Create(AsyncWebRequest.GetSyncResponseStream(feedlistUrl, this.location.Credentials, this.Proxy, NgosTokenHeader)));
            XmlNode tokenNode = doc.DocumentElement.Attributes.GetNamedItem("token", "http://newsgator.com/schema/opml");
            if (tokenNode != null)
            {
                NgosSyncToken = tokenNode.Value;
            }
            XmlNodeReader reader = new XmlNodeReader(this.ConvertFeedList(doc));
            XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(feeds));
            feeds ngFeeds = (feeds)serializer.Deserialize(reader);
            reader.Close();
            return ngFeeds;
        }
        public override void LoadFeedlist()
        {
            XmlReader reader = XmlReader.Create(this.location.Location);
            XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(feeds));
            feeds myFeeds = (feeds)serializer.Deserialize(reader);
            reader.Close();
            this.BootstrapAndLoadFeedlist(myFeeds);
            NewsGatorUpdater.StartBackgroundThread();
        }
        public override void BootstrapAndLoadFeedlist(feeds feedlist)
        {
            Dictionary<string, NewsFeed> bootstrapFeeds = new Dictionary<string, NewsFeed>();
            Dictionary<string, category> bootstrapCategories = new Dictionary<string, category>();
            foreach (NewsFeed f in feedlist.feed)
            {
                bootstrapFeeds.Add(f.link, f);
            }
            foreach (category c in feedlist.categories)
            {
                bootstrapCategories.Add(c.Value, c);
            }
            feeds ngFeeds = (this.Offline ? feedlist : this.LoadFeedlistFromNewsGatorOnline());
            foreach (NewsFeed ngFeed in ngFeeds.feed)
            {
                if (!ngFeed.link.Equals(NoXmlUrlFoundInOpml))
                {
                    NewsFeed feed = null;
                    bootstrapFeeds.TryGetValue(ngFeed.link, out feed);
                    feed = TransferSettings(ngFeed, feed);
                    feed.PropertyChanged += this.OnNewsFeedPropertyChanged;
                    this.feedsTable.Add(ngFeed.link, feed);
                }
            }
            foreach (category ngCategory in ngFeeds.categories)
            {
                category cat = null;
                bootstrapCategories.TryGetValue(ngCategory.Value, out cat);
                cat = cat ?? ngCategory;
                cat.AnyAttr = ngCategory.AnyAttr;
                this.categories.Add(ngCategory.Value, cat);
            }
            if (feedlist.listviewLayouts != null)
            {
                foreach (listviewLayout layout in feedlist.listviewLayouts)
                {
                    string layout_trimmed = layout.ID.Trim();
                    if (!this.layouts.ContainsKey(layout_trimmed))
                    {
                        this.layouts.Add(layout_trimmed, layout.FeedColumnLayout);
                    }
                }
            }
        }
        public override bool AsyncGetItemsForFeed(string feedUrl, bool forceDownload, bool manual)
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
            Uri feedUri = new Uri(feedUrl);
            Uri reqUri = feedUri;
            try
            {
                try
                {
                    if ((!forceDownload) || this.isOffline)
                    {
                        GetCachedItemsForFeed(feedUri.CanonicalizedUri());
                        RaiseOnUpdatedFeed(feedUri, null, RequestResult.NotModified, priority, false);
                        return false;
                    }
                }
                catch (XmlException xe)
                {
                    Trace("Unexpected error retrieving cached feed '{0}': {1}", feedUrl, xe.ToString());
                }
                NewsFeed theFeed = null;
                if (feedsTable.ContainsKey(feedUri.CanonicalizedUri()))
                    theFeed = feedsTable[feedUri.CanonicalizedUri()] as NewsFeed;
                if (theFeed == null || theFeed.Any == null)
                    return false;
                XmlElement syncXmlUrl = theFeed.Any.First(elem => elem.LocalName == "syncXmlUrl");
                if (syncXmlUrl == null)
                {
                    return false;
                }
                string requestUrl = syncXmlUrl.InnerText;
                reqUri = new Uri(requestUrl + "?unread=False");
                RaiseOnUpdateFeedStarted(feedUri, forceDownload, priority);
                DateTime lastModified = DateTime.MinValue;
                if (itemsTable.ContainsKey(feedUrl))
                {
                    etag = theFeed.etag;
                    lastModified = (theFeed.lastretrievedSpecified ? theFeed.lastretrieved : theFeed.lastmodified);
                }
                ICredentials c = location.Credentials;
                RequestParameter reqParam =
                    RequestParameter.Create(reqUri, this.UserAgent, this.Proxy, c, lastModified, etag);
                reqParam.SetCookies = false;
                reqParam.Headers = NgosTokenHeader;
                AsyncWebRequest.QueueRequest(reqParam,
                                             null,
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
        protected override void OnRequestStart(Uri requestUri, ref bool cancel)
        {
            string syncUrl = requestUri.CanonicalizedUri().Substring(0, requestUri.CanonicalizedUri().LastIndexOf("?unread=False"));
            string feedUrl = feedsTable.First(kvp =>
                                kvp.Value.Any.First(elem => elem.LocalName == "syncXmlUrl").InnerText.Equals(syncUrl)).Key;
            if (!StringHelper.EmptyTrimOrNull(feedUrl))
            {
                Uri feedUri = new Uri(feedUrl);
                base.OnRequestStart(feedUri, ref cancel);
            }
        }
        protected override void OnRequestException(Uri requestUri, Exception e, int priority)
        {
            string syncUrl = requestUri.CanonicalizedUri().Substring(0, requestUri.CanonicalizedUri().LastIndexOf("?unread=False"));
            string feedUrl = feedsTable.First(kvp =>
                                kvp.Value.Any.First(elem => elem.LocalName == "syncXmlUrl").InnerText.Equals(syncUrl)).Key;
            if (!StringHelper.EmptyTrimOrNull(feedUrl))
            {
                Uri feedUri = new Uri(feedUrl);
                base.OnRequestException(feedUri, e, priority);
            }
        }
        protected override void OnRequestComplete(Uri requestUri, Stream response, Uri newUri, string eTag, DateTime lastModified,
                                    RequestResult result, int priority)
        {
            string syncUrl = requestUri.CanonicalizedUri().Substring(0, requestUri.CanonicalizedUri().LastIndexOf("?unread=False"));
            string feedUrl = feedsTable.First(kvp =>
                                kvp.Value.Any.First(elem => elem.LocalName == "syncXmlUrl").InnerText.Equals(syncUrl)).Key;
            Uri feedUri = new Uri(feedUrl);
            Trace("AsyncRequest.OnRequestComplete: '{0}': {1}", requestUri.ToString(), result);
            if (newUri != null)
                Trace("AsyncRequest.OnRequestComplete: perma redirect of '{0}' to '{1}'.", requestUri.ToString(),
                      newUri.ToString());
            IList<INewsItem> itemsForFeed;
            bool firstSuccessfulDownload = false;
            bool feedDownloadComplete = true;
            try
            {
                INewsFeed theFeed = null;
                if (!feedsTable.TryGetValue(feedUri.CanonicalizedUri(), out theFeed))
                {
                    Trace("ATTENTION! FeedsTable[requestUri] as NewsFeed returns null for: '{0}'",
                          requestUri.ToString());
                    return;
                }
                feedUrl = theFeed.link;
                if (true)
                {
                    if (String.Compare(feedUrl, feedUri.CanonicalizedUri(), true) != 0)
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
                    FeedDetailsInternal fi = RssParser.GetItemsForFeed(theFeed, response, false);
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
                                                                  false );
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
                        ri.PropertyChanged -= this.OnNewsItemPropertyChanged;
                        ri.PropertyChanged += this.OnNewsItemPropertyChanged;
                    }
                    if (itemsForFeed.Count > theFeed.storiesrecentlyviewed.Count)
                    {
                        theFeed.containsNewMessages = true;
                    }
                    theFeed.lastretrieved = new DateTime(DateTime.Now.Ticks);
                    theFeed.lastretrievedSpecified = true;
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
                if (feedDownloadComplete)
                {
                    RaiseOnUpdatedFeed(feedUri, newUri, result, priority, firstSuccessfulDownload);
                }
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
        internal void ChangeItemClippedStateInNewsGatorOnline(string itemId, bool clipped)
        {
            if (!StringHelper.EmptyTrimOrNull(itemId))
            {
                string clipApiUrl = PostItemApiUrl + (clipped ? "/clipposts" : "/unclipposts");
                string bodyTemplate = "<clippings> <item><postid>{0}</postid>{1}</item> </clippings>";
                string clippingFolder = "<folderid>0</folderid>";
                string body = String.Format(bodyTemplate, itemId, clipped ? clippingFolder : String.Empty);
                HttpWebResponse response = AsyncWebRequest.PostSyncResponse(clipApiUrl, body, this.location.Credentials, this.Proxy, NgosTokenHeader);
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
            }
        }
        protected override void OnNewsFeedPropertyChanged(object sender, PropertyChangedEventArgs e)
        {
            NewsFeed feed = sender as NewsFeed;
            if (feed == null)
            {
                return;
            }
            switch (e.PropertyName)
            {
                case "title":
                    NewsGatorUpdater.RenameFeedInNewsGatorOnline(this.NewsGatorUserName, feed.link, feed.title);
                    break;
            }
        }
        protected override void OnNewsItemPropertyChanged(object sender, PropertyChangedEventArgs e)
        {
            NewsItem item = sender as NewsItem;
            if (item == null)
            {
                return;
            }
            XmlElement elem = RssHelper.GetOptionalElement(item, "postId", NewsGatorRssNS);
            if (elem != null)
            {
                switch (e.PropertyName)
                {
                    case "BeenRead":
                        NewsGatorUpdater.ChangeItemStateInNewsGatorOnline(this.NewsGatorUserName, elem.InnerText,
                            item.BeenRead ? NewsGatorItemState.Read : NewsGatorItemState.Unread);
                        break;
                    case "FlagStatus":
                        NewsGatorUpdater.ChangeItemStateInNewsGatorOnline(this.NewsGatorUserName, elem.InnerText,
                            item.Feed.link, (NewsGatorFlagStatus) Enum.Parse(typeof(NewsGatorFlagStatus), item.FlagStatus.ToString())
                            );
                        break;
                }
            }
        }
        internal void ChangeItemStateInNewsGatorOnline(string itemId, string feedUrl, NewsGatorFlagStatus state)
        {
            if (!StringHelper.EmptyTrimOrNull(itemId) && !StringHelper.EmptyTrimOrNull(feedUrl) && feedsTable.ContainsKey(feedUrl))
            {
                INewsFeed f = feedsTable[feedUrl];
                string feedId = f.Any.First(elem => elem.LocalName == "id").InnerText;
                string flagItemApiUrl = PostItemApiUrl + "/updatepostmetadata";
                NewsGatorItemMetaData body = new NewsGatorItemMetaData();
                body.updatepostmetadata = new bodyUpdatepostmetadata();
                body.updatepostmetadata.location = NgosLocationName;
                body.updatepostmetadata.synctoken = NgosSyncToken;
                body.updatepostmetadata.newstates = new bodyUpdatepostmetadataNewstates();
                body.updatepostmetadata.newstates.feedmetadata = new bodyUpdatepostmetadataNewstatesFeedmetadata();
                body.updatepostmetadata.newstates.feedmetadata.feedid = feedId;
                body.updatepostmetadata.newstates.feedmetadata.postmetadata = new bodyUpdatepostmetadataNewstatesFeedmetadataPostmetadata();
                body.updatepostmetadata.newstates.feedmetadata.postmetadata.flagstate = (int) state;
                body.updatepostmetadata.newstates.feedmetadata.postmetadata.flagstatespecified = true;
                body.updatepostmetadata.newstates.feedmetadata.postmetadata.postid = itemId;
                body.updatepostmetadata.newstates.feedmetadata.postmetadata.statespecified = false;
                XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(NewsGatorItemMetaData));
                StringBuilder sb = new StringBuilder();
                XmlWriterSettings xws = new XmlWriterSettings();
                xws.OmitXmlDeclaration = true;
                serializer.Serialize(XmlWriter.Create(sb, xws), body);
                HttpWebResponse response = AsyncWebRequest.PostSyncResponse(flagItemApiUrl, sb.ToString(), this.location.Credentials, this.Proxy, NgosTokenHeader);
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
            }
        }
        internal void ChangeItemStateInNewsGatorOnline(string itemId, NewsGatorItemState state)
        {
            if(!StringHelper.EmptyTrimOrNull(itemId)){
                string body = "loc=" + NgosLocationName + "&";
                switch (state)
                {
                    case NewsGatorItemState.Deleted:
                        body += "d=" + itemId;
                        break;
                    case NewsGatorItemState.Read:
                        body += "r=" + itemId;
                        break;
                    case NewsGatorItemState.Unread:
                        body += "u=" + itemId;
                        break;
                }
            HttpWebResponse response = AsyncWebRequest.PostSyncResponse(PostItemApiUrl, body, this.location.Credentials, this.Proxy, NgosTokenHeader);
             if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
            }
        }
        internal void MarkAllItemsAsReadInNewsGatorOnline(string feedUrl, string syncToken)
        {
            if (!StringHelper.EmptyTrimOrNull(feedUrl) && feedsTable.ContainsKey(feedUrl))
            {
                NewsFeed f = feedsTable[feedUrl] as NewsFeed;
                string feedId = f.Any.First(elem => elem.LocalName == "id").InnerText;
                string markReadUrl = FeedApiUrl + "/" + feedId;
                string body = "tok=" + syncToken + "&read=true";
                HttpWebResponse response = AsyncWebRequest.PostSyncResponse(markReadUrl, body, this.location.Credentials, this.Proxy, NgosTokenHeader);
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
            }
        }
        public override void DeleteItem(INewsItem item)
        {
            XmlElement elem = RssHelper.GetOptionalElement(item, "postId", NewsGatorRssNS);
            if (elem != null)
            {
                NewsGatorUpdater.ChangeItemStateInNewsGatorOnline(this.NewsGatorUserName, elem.InnerText, NewsGatorItemState.Deleted);
            }
            base.DeleteItem(item);
        }
        public override void MarkAllCachedItemsAsRead(INewsFeed feed)
        {
            DateTime newestItemAge = DateTime.MinValue;
            string syncToken = null;
            if (feed != null && !string.IsNullOrEmpty(feed.link) && itemsTable.ContainsKey(feed.link))
            {
                IFeedDetails fi = itemsTable[feed.link] as IFeedDetails;
                if (fi != null)
                {
                    XmlElement elem = RssHelper.GetOptionalElement(fi.OptionalElements, "token",NewsGatorRssNS);
                    if(elem != null){
                        syncToken = elem.InnerText;
                    }
                    foreach (NewsItem ri in fi.ItemsList)
                    {
                        ri.PropertyChanged -= this.OnNewsItemPropertyChanged;
                        ri.BeenRead = true;
                        newestItemAge = (ri.Date > newestItemAge ? ri.Date : newestItemAge);
                        ri.PropertyChanged += this.OnNewsItemPropertyChanged;
                    }
                }
                feed.containsNewMessages = false;
            }
            if (newestItemAge != DateTime.MinValue)
            {
                NewsGatorUpdater.MarkAllItemsAsReadInNewsGatorOnline(this.NewsGatorUserName, feed.link, syncToken);
            }
        }
        internal void RenameFolderInNewsGatorOnline(string oldName, string newName)
        {
            INewsFeedCategory cat = null;
            if (this.categories.TryGetValue(newName, out cat))
            {
                string folderRenameUrl = FolderApiUrl + "/rename";
                string body = "fld=" + cat.AnyAttr.First(a => a.LocalName == "id").Value + "&name="
                    + Uri.EscapeDataString(newName);
                HttpWebResponse response = AsyncWebRequest.PostSyncResponse(folderRenameUrl, body, this.location.Credentials, this.Proxy, NgosTokenHeader);
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
            }
        }
        public override void RenameCategory(string oldName, string newName)
        {
            if ((StringHelper.EmptyTrimOrNull(oldName) || StringHelper.EmptyTrimOrNull(newName))
                || oldName.Equals(newName))
            {
                return;
            }
            base.RenameCategory(oldName, newName);
            NewsGatorUpdater.RenameFolderInNewsGatorOnline(this.NewsGatorUserName, oldName, newName);
        }
        public override INewsFeedCategory AddCategory(INewsFeedCategory cat)
        {
            if (cat == null)
                throw new ArgumentNullException("cat");
            if (this.categories.ContainsKey(cat.Value))
                return this.categories[cat.Value];
            this.categories.Add(cat.Value, cat);
            readonly_categories = new ReadOnlyDictionary<string, INewsFeedCategory>(this.categories);
            NewsGatorUpdater.AddFolderInNewsGatorOnline(this.NewsGatorUserName, cat.Value);
            return cat;
        }
        public override INewsFeedCategory AddCategory(string cat)
        {
            if (StringHelper.EmptyTrimOrNull(cat))
                return null;
            return this.AddCategory(new category(cat));
        }
        internal void AddFolderInNewsGatorOnline(string name)
        {
            if (StringHelper.EmptyTrimOrNull(name))
                return;
            INewsFeedCategory cat = null;
            this.categories.TryGetValue(name, out cat);
            if (cat == null)
            {
                cat = new category(name);
                this.categories.Add(name, cat);
            }
            if (cat.AnyAttr != null && cat.AnyAttr.First( attr => attr.LocalName == "folderId" )!= null)
                return;
            List<string> ancestors = category.GetAncestors(name);
            for (int i = ancestors.Count; i-- > 0; )
            {
                INewsFeedCategory c = null;
                if (!this.categories.TryGetValue(ancestors[i], out c))
                {
                    c = new category(ancestors[i]);
                    this.categories.Add(ancestors[i], c);
                }
                if (c.AnyAttr==null || c.AnyAttr.First(attr => attr.LocalName == "folderId") == null)
                {
                    this.AddFolderInNewsGatorOnline(ancestors[i]);
                    if (c.Value.Contains(FeedSource.CategorySeparator))
                    {
                        c.parent = this.categories[ancestors[i - 1]];
                    }
                }
            }
            cat.parent = (ancestors.Count == 0 ? null : this.categories[ancestors[ancestors.Count - 1]]);
            int index = cat.Value.LastIndexOf(FeedSource.CategorySeparator);
            string folderName = (index == -1 ? cat.Value : cat.Value.Substring(index + 1));
            string folderCreateUrl = FolderApiUrl + "/create";
            string body = "parentid=" + (cat.parent == null ? "0" : cat.parent.AnyAttr.First(a => a.LocalName == "folderId").Value)
                + "&name=" + Uri.EscapeDataString(folderName) + "&root=MYF";
            HttpWebResponse response = AsyncWebRequest.PostSyncResponse(folderCreateUrl, body, this.location.Credentials, this.Proxy, NgosTokenHeader);
            if (response.StatusCode == HttpStatusCode.OK)
            {
                XmlDocument doc = new XmlDocument();
                doc.Load(response.GetResponseStream());
                XmlElement outlineElem = doc.SelectSingleNode("//outline[@text=" + buildXPathString(folderName) + "]") as XmlElement;
                if (outlineElem != null)
                {
                    cat.AnyAttr = new XmlAttribute[1];
                    XmlAttribute idNode = outlineElem.Attributes["id", NewsGatorOpmlNS];
                    if (idNode != null)
                    {
                        cat.AnyAttr[0] = idNode;
                    }
                }
            }
            else
            {
                throw new WebException(response.StatusDescription);
            }
        }
        internal void RenameFeedInNewsGatorOnline(string url, string title)
        {
            if (StringHelper.EmptyTrimOrNull(title))
            {
                return;
            }
            if (!StringHelper.EmptyTrimOrNull(url) && feedsTable.ContainsKey(url))
            {
                INewsFeed f = feedsTable[url];
                opml location = new opml();
                location.body = new opmloutline[1];
                opmloutline outline = new opmloutline();
                outline.xmlUrl = f.link;
                outline.text = title;
                outline.id = f.Any.First(elem => elem.LocalName == "id").InnerText;
                if (!StringHelper.EmptyTrimOrNull(f.category))
                {
                    string[] catHives = f.category.Split(CategorySeparator.ToCharArray());
                    foreach (string cat in catHives.Reverse())
                    {
                        opmloutline folder = new opmloutline();
                        folder.text = cat;
                        folder.outline = new opmloutline[] { outline };
                        outline = folder;
                    }
                }
                location.body[0] = outline;
                XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(opml));
                StringBuilder sb = new StringBuilder();
                XmlWriterSettings xws = new XmlWriterSettings();
                xws.OmitXmlDeclaration = true;
                serializer.Serialize(XmlWriter.Create(sb, xws), location);
                HttpWebResponse response = AsyncWebRequest.PostSyncResponse(SubscriptionApiUrl, sb.ToString(), this.location.Credentials, this.Proxy, NgosTokenHeader);
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
            }
        }
        internal void ChangeFolderInNewsGatorOnline(string feedUrl, string cat)
        {
            if (!StringHelper.EmptyTrimOrNull(feedUrl) && feedsTable.ContainsKey(feedUrl)
                && !StringHelper.EmptyTrimOrNull(cat) && categories.ContainsKey(cat) )
            {
                INewsFeed f = feedsTable[feedUrl];
                string feedId = f.Any.First(elem => elem.LocalName == "id").InnerText;
                INewsFeedCategory c = categories[cat];
                string folderId = c.AnyAttr.First(attr => attr.LocalName == "id").Value;
                string moveApiUrl = SubscriptionApiUrl + "/" + NgosLocationName + "/movesubscription/" + feedId;
                string body = "tofolderid=" + folderId;
                HttpWebResponse response = AsyncWebRequest.PostSyncResponse(moveApiUrl, body, this.location.Credentials, this.Proxy, NgosTokenHeader);
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
                XmlElement folderIdNode = f.Any.First(elem => elem.LocalName == "folderId");
                folderIdNode.InnerText = folderId;
            }
        }
        public override void ChangeCategory(INewsFeed feed, string cat)
        {
            base.ChangeCategory(feed, cat);
            NewsGatorUpdater.ChangeFolderInNewsGatorOnline(this.NewsGatorUserName, feed.link, cat);
        }
        public override void ChangeCategory(INewsFeed feed, INewsFeedCategory cat)
        {
            base.ChangeCategory(feed, cat);
            NewsGatorUpdater.ChangeFolderInNewsGatorOnline(this.NewsGatorUserName, feed.link, cat.Value);
        }
        public override INewsFeed AddFeed(INewsFeed feed, FeedInfo feedInfo)
        {
            feed = base.AddFeed(feed, feedInfo);
            NewsGatorUpdater.AddFeedInNewsGatorOnline(this.NewsGatorUserName, feed.link);
            feed.PropertyChanged += this.OnNewsFeedPropertyChanged;
            return feed;
        }
        internal void AddFeedInNewsGatorOnline(string feedUrl)
        {
            if (!StringHelper.EmptyTrimOrNull(feedUrl) && feedsTable.ContainsKey(feedUrl))
            {
                INewsFeed f = feedsTable[feedUrl];
                    opml location = new opml();
                    location.body = new opmloutline[1];
                    opmloutline outline = new opmloutline();
                    outline.xmlUrl = f.link;
                    outline.text = f.title;
                    if (!StringHelper.EmptyTrimOrNull(f.category))
                    {
                        string[] catHives = f.category.Split(CategorySeparator.ToCharArray());
                        foreach (string cat in catHives.Reverse())
                        {
                            opmloutline folder = new opmloutline();
                            folder.text = cat;
                            folder.outline = new opmloutline[] { outline };
                            outline = folder;
                        }
                    }
                    location.body[0] = outline;
                    XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(opml));
                    StringBuilder sb = new StringBuilder();
                    XmlWriterSettings xws = new XmlWriterSettings();
                    xws.OmitXmlDeclaration = true;
                    serializer.Serialize(XmlWriter.Create(sb, xws), location);
                    HttpWebResponse response = AsyncWebRequest.PostSyncResponse(SubscriptionApiUrl, sb.ToString(), this.location.Credentials, this.Proxy, NgosTokenHeader);
                    if (response.StatusCode == HttpStatusCode.OK)
                    {
                        XmlDocument doc = new XmlDocument();
                        doc.Load(response.GetResponseStream());
                        XmlElement outlineElem = doc.SelectSingleNode("//outline[@xmlUrl='" + f.link + "']") as XmlElement;
                        if (outlineElem != null)
                        {
                            f.Any = new XmlElement[2];
                            string id = outlineElem.GetAttribute("id", NewsGatorOpmlNS);
                            if (!StringHelper.EmptyTrimOrNull(id))
                            {
                                XmlElement idNode = doc.CreateElement("ng", "id", NewsGatorRssNS);
                                idNode.InnerText = id;
                                f.Any[0] = idNode;
                            }
                            string syncXmlUrl = outlineElem.GetAttribute("syncXmlUrl", NewsGatorOpmlNS);
                            if (!StringHelper.EmptyTrimOrNull(syncXmlUrl))
                            {
                                XmlElement syncXmlUrlNode = doc.CreateElement("ng", "syncXmlUrl", NewsGatorRssNS);
                                syncXmlUrlNode.InnerText = syncXmlUrl;
                                f.Any[1] = syncXmlUrlNode;
                            }
                        }
                        if (!feedsTable.ContainsKey(f.link))
                        {
                            feedsTable.Add(f.link, f);
                        }
                    }else
                    {
                        throw new WebException(response.StatusDescription);
                    }
            }
        }
        public override void DeleteFeed(string feedUrl)
        {
            NewsGatorUpdater.DeleteFeedFromNewsGatorOnline(this.NewsGatorUserName, feedUrl);
        }
        internal void DeleteFeedFromNewsGatorOnline(string feedUrl)
        {
            if (!StringHelper.EmptyTrimOrNull(feedUrl) && feedsTable.ContainsKey(feedUrl))
            {
                INewsFeed f = feedsTable[feedUrl];
                string feedId = f.Any.First(elem => elem.LocalName == "id").InnerText;
                opml location = new opml();
                location.body = new opmloutline[1];
                opmloutline outline = new opmloutline();
                outline.id = feedId;
                location.body[0] = outline;
                XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(opml));
                StringBuilder sb = new StringBuilder();
                XmlWriterSettings xws = new XmlWriterSettings();
                xws.OmitXmlDeclaration = true;
                serializer.Serialize(XmlWriter.Create(sb, xws), location);
                HttpWebResponse response = AsyncWebRequest.DeleteSyncResponse(SubscriptionApiUrl, sb.ToString(), this.location.Credentials, this.Proxy, NgosTokenHeader);
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
                base.DeleteFeed(feedUrl);
            }
        }
    }
    [System.Xml.Serialization.XmlRoot(ElementName="body")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true)]
    public class NewsGatorItemMetaData
    {
        private bodyUpdatepostmetadata updatepostmetadataField;
        public bodyUpdatepostmetadata updatepostmetadata
        {
            get
            {
                return this.updatepostmetadataField;
            }
            set
            {
                this.updatepostmetadataField = value;
            }
        }
    }
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true)]
    public class bodyUpdatepostmetadata
    {
        private string locationField;
        private string synctokenField;
        private bodyUpdatepostmetadataNewstates newstatesField;
        public string location
        {
            get
            {
                return this.locationField;
            }
            set
            {
                this.locationField = value;
            }
        }
        public string synctoken
        {
            get
            {
                return this.synctokenField;
            }
            set
            {
                this.synctokenField = value;
            }
        }
        public bodyUpdatepostmetadataNewstates newstates
        {
            get
            {
                return this.newstatesField;
            }
            set
            {
                this.newstatesField = value;
            }
        }
    }
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true)]
    public class bodyUpdatepostmetadataNewstates
    {
        private bodyUpdatepostmetadataNewstatesFeedmetadata feedmetadataField;
        public bodyUpdatepostmetadataNewstatesFeedmetadata feedmetadata
        {
            get
            {
                return this.feedmetadataField;
            }
            set
            {
                this.feedmetadataField = value;
            }
        }
    }
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true)]
    public class bodyUpdatepostmetadataNewstatesFeedmetadata
    {
        private string feedidField;
        private bodyUpdatepostmetadataNewstatesFeedmetadataPostmetadata postmetadataField;
        public string feedid
        {
            get
            {
                return this.feedidField;
            }
            set
            {
                this.feedidField = value;
            }
        }
        public bodyUpdatepostmetadataNewstatesFeedmetadataPostmetadata postmetadata
        {
            get
            {
                return this.postmetadataField;
            }
            set
            {
                this.postmetadataField = value;
            }
        }
    }
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true)]
    public class bodyUpdatepostmetadataNewstatesFeedmetadataPostmetadata
    {
        private string postidField;
        private int stateField;
        private bool statespecifiedField;
        private int flagstateField;
        private bool flagstatespecifiedField;
        public string postid
        {
            get
            {
                return this.postidField;
            }
            set
            {
                this.postidField = value;
            }
        }
        public int state
        {
            get
            {
                return this.stateField;
            }
            set
            {
                this.stateField = value;
            }
        }
        public bool statespecified
        {
            get
            {
                return this.statespecifiedField;
            }
            set
            {
                this.statespecifiedField = value;
            }
        }
        public int flagstate
        {
            get
            {
                return this.flagstateField;
            }
            set
            {
                this.flagstateField = value;
            }
        }
        public bool flagstatespecified
        {
            get
            {
                return this.flagstatespecifiedField;
            }
            set
            {
                this.flagstatespecifiedField = value;
            }
        }
    }
}
