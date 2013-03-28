using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Linq;
using System.Net;
using System.Xml;
using System.Xml.Serialization;
using log4net;
using NewsComponents.Collections;
using NewsComponents.Net;
using NewsComponents.Utils;
using RssBandit.Common;
using RssBandit.Common.Logging;
namespace NewsComponents.Feed
{
    class GoogleReaderFeedSource : FeedSource
    {
        private static readonly ILog _log = Log.GetLogger(typeof(GoogleReaderFeedSource));
        private static readonly string authUrl = @"https://www.google.com/accounts/ClientLogin?continue=http://www.google.com&service=reader&source=RssBandit&Email={0}&Passwd={1}";
        private static readonly string feedUrlPrefix = @"http://www.google.com/reader/atom/";
        private static readonly string apiUrlPrefix = @"http://www.google.com/reader/api/0/";
        private static readonly XmlQualifiedName continuationQName = new XmlQualifiedName("continuation", "http://www.google.com/schemas/reader/atom/");
        private static string MostRecentGoogleEditToken = null;
        private string SID = String.Empty;
        private string googleUserId = String.Empty;
        private static GoogleReaderModifier googleReaderUpdater;
        private GoogleReaderFeedSource() { ; }
        internal GoogleReaderFeedSource(INewsComponentsConfiguration configuration, SubscriptionLocation location)
        {
            this.p_configuration = configuration;
            if (this.p_configuration == null)
                this.p_configuration = FeedSource.DefaultConfiguration;
            this.location = location;
            GoogleReaderUpdater.RegisterFeedSource(this);
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
        public string GoogleUserName
        {
            get { return this.location.Credentials.UserName; }
        }
        public string GoogleUserId
        {
            get { return googleUserId; }
            private set
            {
                this.googleUserId = value;
            }
        }
        public GoogleReaderModifier GoogleReaderUpdater
        {
            get
            {
                if (googleReaderUpdater == null)
                {
                    googleReaderUpdater = new GoogleReaderModifier(this.Configuration.UserApplicationDataPath);
                }
                return googleReaderUpdater;
            }
            set
            {
                googleReaderUpdater = value;
            }
        }
        public override void SaveFeedList(Stream feedStream)
        {
            base.SaveFeedList(feedStream);
            GoogleReaderUpdater.SavePendingOperations();
        }
        public override void LoadFeedlist()
        {
            XmlReader reader = XmlReader.Create(this.location.Location);
            XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(feeds));
            feeds myFeeds = (feeds)serializer.Deserialize(reader);
            reader.Close();
            this.BootstrapAndLoadFeedlist(myFeeds);
            GoogleReaderUpdater.StartBackgroundThread();
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
            if (this.Offline)
            {
                foreach (NewsFeed ff in feedlist.feed) { this.feedsTable.Add(ff.link, ff); }
                foreach (category cc in feedlist.categories) { this.categories.Add(cc.Value, cc); }
            }
            else
            {
                IEnumerable<GoogleReaderSubscription> gReaderFeeds = this.LoadFeedlistFromGoogleReader();
                foreach (GoogleReaderSubscription gfeed in gReaderFeeds)
                {
                    NewsFeed feed = null;
                    this.feedsTable.Add(gfeed.FeedUrl, new GoogleReaderNewsFeed(gfeed, (bootstrapFeeds.TryGetValue(gfeed.FeedUrl, out feed) ? feed : null), this));
                }
                IEnumerable<string> gReaderLabels = this.LoadTaglistFromGoogleReader();
                string labelPrefix = "user/" + this.GoogleUserId + "/label/";
                foreach (string gLabel in gReaderLabels)
                {
                    string label = gLabel.Replace("/", FeedSource.CategorySeparator);
                    category cat = null;
                    bootstrapCategories.TryGetValue(label, out cat);
                    this.categories.Add(gLabel, cat ?? new category(label));
                }
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
        private IEnumerable<string> LoadTaglistFromGoogleReader()
        {
            string taglistUrl = apiUrlPrefix + "tag/list";
            XmlDocument doc = new XmlDocument();
            doc.Load(XmlReader.Create(AsyncWebRequest.GetSyncResponseStream(taglistUrl, null, this.Proxy, MakeGoogleCookie(this.SID))));
            string temp = doc.SelectSingleNode("/object/list/object/string[contains(string(.), 'state/com.google/starred')]").InnerText;
            this.GoogleUserId = temp.Replace("/state/com.google/starred", "").Substring(5);
            XmlNode node;
            var taglist = from node in doc.SelectNodes("/object/list[@name='tags']/object/string[@name='id']")
                          where node.InnerText.IndexOf("/com.google/") == -1
                          select node.InnerText.Replace("user/" + this.GoogleUserId + "/label/", "");
            return taglist;
        }
        private IEnumerable<GoogleReaderSubscription> LoadFeedlistFromGoogleReader()
        {
            string feedlistUrl = apiUrlPrefix + "subscription/list";
            this.AuthenticateUser();
            XmlDocument doc = new XmlDocument();
            doc.Load(XmlReader.Create(AsyncWebRequest.GetSyncResponseStream(feedlistUrl, null, this.Proxy, MakeGoogleCookie(this.SID))));
            XmlNode node;
            var feedlist = from node in doc.SelectNodes("/object/list[@name='subscriptions']/object")
                           select MakeSubscription(node);
            return feedlist;
        }
        private static GoogleReaderSubscription MakeSubscription(XmlNode node)
        {
            XmlNode id_node = node.SelectSingleNode("string[@name='id']");
            string feedid = (id_node == null ? String.Empty : id_node.InnerText);
            XmlNode title_node = node.SelectSingleNode("string[@name='title']");
            string title = (title_node == null ? String.Empty : title_node.InnerText);
            XmlNode fim_node = node.SelectSingleNode("string[@name='firstitemmsec']");
            long firstitemmsec = (fim_node == null ? 0 : Int64.Parse(fim_node.InnerText));
            List<GoogleReaderLabel> categories = MakeLabelList(node.SelectNodes("list[@name='categories']/object"));
            return new GoogleReaderSubscription(feedid, title, categories, firstitemmsec);
        }
        private static List<GoogleReaderLabel> MakeLabelList(XmlNodeList nodes)
        {
            List<GoogleReaderLabel> labels = new List<GoogleReaderLabel>();
            foreach (XmlNode node in nodes) {
                XmlNode id_node = node.SelectSingleNode("string[@name='id']");
                string catid = (id_node == null ? String.Empty : id_node.InnerText);
                XmlNode label_node = node.SelectSingleNode("string[@name='label']");
                string label = (label_node == null ? String.Empty : label_node.InnerText);
                labels.Add(new GoogleReaderLabel(label, catid));
            }
            return labels;
        }
        private void AuthenticateUser()
        {
            string requestUrl = String.Format(authUrl, location.Credentials.UserName, location.Credentials.Password);
            WebRequest req = HttpWebRequest.Create(requestUrl);
            WebResponse resp = req.GetResponse();
            StreamReader reader = new StreamReader(AsyncWebRequest.GetSyncResponseStream(requestUrl, null, this.Proxy));
            string[] response = reader.ReadToEnd().Split('\n');
            foreach(string s in response){
                if(s.StartsWith("SID=",StringComparison.Ordinal)){
                    this.SID = s.Substring(4);
                    return;
                }
            }
            throw new WebException("Could not authenticate user to Google Reader because no SID provided in response", WebExceptionStatus.UnknownError);
        }
        private static Cookie MakeGoogleCookie(string sid)
        {
           Cookie cookie = new Cookie("SID", sid, "/", ".google.com");
           cookie.Expires = DateTime.Now + new TimeSpan(7,0,0,0);
           return cookie;
        }
        private static string GetGoogleEditToken(string sid)
        {
            string tokenUrl = apiUrlPrefix + "token";
            HttpWebRequest request = HttpWebRequest.Create(tokenUrl) as HttpWebRequest;
            request.Timeout = 5 * 1000;
            request.CookieContainer = new CookieContainer();
            request.CookieContainer.Add(MakeGoogleCookie(sid));
            try
            {
                StreamReader reader = new StreamReader(request.GetResponse().GetResponseStream());
                MostRecentGoogleEditToken = reader.ReadToEnd();
            } catch (WebException we){
                if (we.Status != WebExceptionStatus.Timeout)
                {
                    throw;
                }
            }
            return MostRecentGoogleEditToken;
        }
        private static string CreateDownloadUrl(GoogleReaderNewsFeed feed)
        {
            TimeSpan maxItemAge = (feed.lastretrievedSpecified ? DateTime.Now - feed.lastretrieved : new TimeSpan(90, 0, 0, 0));
            string feedUrl = feedUrlPrefix + Uri.EscapeDataString(feed.GoogleReaderFeedId) + "?n=50&r=o&ot=" + Convert.ToInt32(maxItemAge.TotalSeconds);
            return feedUrl;
        }
        private static Uri CreateContinuedDownloadUrl(Uri requestUri, string continuationToken)
        {
            string feedUrl = requestUri.CanonicalizedUri();
            int index = feedUrl.IndexOf("&c=");
            if (index == -1)
            {
                feedUrl = feedUrl + "&c=" + continuationToken;
            }
            else
            {
                feedUrl = feedUrl.Substring(0, index) + "&c=" + continuationToken;
            }
            return new Uri(feedUrl);
        }
        private static Uri CreateFeedUriFromDownloadUri(Uri downloadUri)
        {
            if(downloadUri == null)
                throw new ArgumentNullException("downloadUri");
            string downloadUrl = downloadUri.AbsoluteUri;
            int startIndex = feedUrlPrefix.Length + "feed/".Length;
            int endIndex = downloadUrl.IndexOf("?n=50");
            return new Uri(Uri.UnescapeDataString(downloadUrl.Substring(startIndex, endIndex - startIndex)));
        }
        public override bool AsyncGetItemsForFeed(string feedUrl, bool forceDownload, bool manual)
        {
           return this.AsyncGetItemsForFeed(feedUrl, forceDownload, manual, null);
        }
        private bool AsyncGetItemsForFeed(string feedUrl, bool forceDownload, bool manual, string continuationToken)
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
            if (continuationToken != null)
            {
                feedUri = CreateFeedUriFromDownloadUri(reqUri);
                reqUri = CreateContinuedDownloadUrl(reqUri, continuationToken);
            }
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
                GoogleReaderNewsFeed theFeed = null;
                if (feedsTable.ContainsKey(feedUri.CanonicalizedUri()))
                    theFeed = feedsTable[feedUri.CanonicalizedUri()] as GoogleReaderNewsFeed;
                if (theFeed == null)
                    return false;
                if (continuationToken == null)
                {
                    reqUri = new Uri(CreateDownloadUrl(theFeed));
                }
                RaiseOnUpdateFeedStarted(feedUri, forceDownload, priority);
                DateTime lastModified = DateTime.MinValue;
                if (itemsTable.ContainsKey(feedUrl))
                {
                    etag = theFeed.etag;
                    lastModified = (theFeed.lastretrievedSpecified ? theFeed.lastretrieved : theFeed.lastmodified);
                }
                ICredentials c = null;
                RequestParameter reqParam =
                    RequestParameter.Create(reqUri, this.UserAgent, this.Proxy, c, lastModified, etag);
                reqParam.SetCookies = false;
                reqParam.Cookies = new CookieCollection();
                reqParam.Cookies.Add(MakeGoogleCookie(this.SID));
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
            Trace("AsyncRequest.OnRequestStart('{0}') downloading", requestUri.ToString());
            this.RaiseBeforeDownloadFeedStarted(CreateFeedUriFromDownloadUri(requestUri), ref cancel);
            if (!cancel)
                cancel = this.Offline;
        }
        protected override void OnRequestException(Uri requestUri, Exception e, int priority)
        {
            Trace("AsyncRequst.OnRequestException() fetching '{0}': {1}", requestUri.ToString(), e.ToString());
            string key = CreateFeedUriFromDownloadUri(requestUri).CanonicalizedUri();
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
            RaiseOnUpdateFeedException(key, e, priority);
        }
        protected override void OnRequestComplete(Uri requestUri, Stream response, Uri newUri, string eTag, DateTime lastModified,
                                    RequestResult result, int priority)
        {
            Trace("AsyncRequest.OnRequestComplete: '{0}': {1}", requestUri.ToString(), result);
            if (newUri != null)
                Trace("AsyncRequest.OnRequestComplete: perma redirect of '{0}' to '{1}'.", requestUri.ToString(),
                      newUri.ToString());
            IList<INewsItem> itemsForFeed;
            bool firstSuccessfulDownload = false;
            bool feedDownloadComplete = true;
            Uri feedUri = CreateFeedUriFromDownloadUri(requestUri);
            try
            {
                INewsFeed theFeed = null;
                if (!feedsTable.TryGetValue(feedUri.CanonicalizedUri(), out theFeed))
                {
                    Trace("ATTENTION! FeedsTable[requestUri] as NewsFeed returns null for: '{0}'",
                          requestUri.ToString());
                    return;
                }
                string feedUrl = theFeed.link;
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
                    if (fi.OptionalElements.ContainsKey(continuationQName))
                    {
                        XmlNode continuationNode = RssHelper.GetOptionalElement(fi.OptionalElements, continuationQName.Name, continuationQName.Namespace);
                        this.AsyncGetItemsForFeed(requestUri.CanonicalizedUri(), true, true, continuationNode.InnerText);
                        feedDownloadComplete = false;
                    }
                    else
                    {
                        theFeed.lastretrieved = new DateTime(DateTime.Now.Ticks);
                        theFeed.lastretrievedSpecified = true;
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
        internal void DeleteFeedFromGoogleReader(string feedUrl)
        {
            if (!StringHelper.EmptyTrimOrNull(feedUrl))
            {
                string subscribeUrl = apiUrlPrefix + "subscription/edit";
                string feedId = "feed/" + feedUrl;
                string body = "s=" + Uri.EscapeDataString(feedId) + "&T=" + GetGoogleEditToken(this.SID) + "&ac=unsubscribe&i=null" ;
                HttpWebResponse response = AsyncWebRequest.PostSyncResponse(subscribeUrl, body, MakeGoogleCookie(this.SID), null, this.Proxy);
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
                base.DeleteFeed(feedUrl);
            }
        }
        public override void DeleteFeed(string feedUrl)
        {
            GoogleReaderUpdater.DeleteFeedFromGoogleReader(this.GoogleUserName, feedUrl);
        }
        internal void AddFeedInGoogleReader(string feedUrl)
        {
            this.AddFeedInGoogleReader(feedUrl, null);
        }
        private void AddFeedInGoogleReader(string feedUrl, string label)
        {
            if (!StringHelper.EmptyTrimOrNull(feedUrl) && (feedsTable.ContainsKey(feedUrl) || label != null))
            {
                INewsFeed f = null;
                if (label == null)
                {
                    f = feedsTable[feedUrl];
                }
                string subscribeUrl = apiUrlPrefix + "subscription/edit";
                string feedId = "feed/" + feedUrl;
                string labelParam = String.Empty;
                if (label == null)
                {
                    foreach (string category in f.categories)
                    {
                        GoogleReaderLabel grl = new GoogleReaderLabel(category, "user/" + this.GoogleUserId + "/label/" + category);
                        labelParam += "&a=" + Uri.EscapeDataString(grl.Id);
                    }
                }else{
                    labelParam = "&a=" + Uri.EscapeDataString("user/" + this.GoogleUserId + "/label/" + label);
                }
                string body = "s=" + Uri.EscapeDataString(feedId) + "&T=" + GetGoogleEditToken(this.SID) + "&ac=subscribe" + labelParam;
                HttpWebResponse response = AsyncWebRequest.PostSyncResponse(subscribeUrl, body, MakeGoogleCookie(this.SID), null, this.Proxy);
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
            }
        }
        public override INewsFeed AddFeed(INewsFeed feed, FeedInfo feedInfo)
        {
            feed = base.AddFeed(feed, feedInfo);
            string feedId = "feed/" + feed.link;
            List<GoogleReaderLabel> labels = new List<GoogleReaderLabel>();
            foreach (string category in feed.categories)
            {
                GoogleReaderLabel label = new GoogleReaderLabel(category, "user/" + this.GoogleUserId + "/label/" + category);
                labels.Add(label);
            }
            GoogleReaderSubscription sub = new GoogleReaderSubscription(feedId, feed.title, labels, 0);
            feedsTable.Remove(feed.link);
            feed = new GoogleReaderNewsFeed(sub, feed, this);
            feedsTable.Add(feed.link, feed);
            GoogleReaderUpdater.AddFeedInGoogleReader(this.GoogleUserName, feed.link);
            return feed;
        }
        internal void RenameFeedInGoogleReader(string url, string title)
        {
            if (StringHelper.EmptyTrimOrNull(title))
            {
                return;
            }
            if(!StringHelper.EmptyTrimOrNull(url) && feedsTable.ContainsKey(url)){
                GoogleReaderNewsFeed f = feedsTable[url] as GoogleReaderNewsFeed;
                if (f != null)
                {
                    string apiUrl = apiUrlPrefix + "subscription/edit";
                    string body = "ac=edit&i=null&T=" + GetGoogleEditToken(this.SID) + "&t=" + Uri.EscapeDataString(title) + "&s=" + Uri.EscapeDataString(f.GoogleReaderFeedId);
                    HttpWebResponse response = AsyncWebRequest.PostSyncResponse(apiUrl, body, MakeGoogleCookie(this.SID), null, this.Proxy);
                    if (response.StatusCode != HttpStatusCode.OK)
                    {
                        throw new WebException(response.StatusDescription);
                    }
                }
            }
        }
        protected override void OnNewsItemPropertyChanged(object sender, PropertyChangedEventArgs e)
        {
            NewsItem item = sender as NewsItem;
            if(item == null){
                return;
            }
            switch (e.PropertyName)
            {
                case "BeenRead":
                    if (item.Feed != null)
                    {
                        GoogleReaderNewsFeed f = item.Feed as GoogleReaderNewsFeed;
                        GoogleReaderUpdater.ChangeItemReadStateInGoogleReader(this.GoogleUserName, f.GoogleReaderFeedId, item.Id, item.BeenRead);
                    }
                    break;
                case "FlagStatus":
                    if (item.Feed != null)
                    {
                        GoogleReaderNewsFeed f = item.Feed as GoogleReaderNewsFeed;
                        GoogleReaderUpdater.ChangeItemStarredStateInGoogleReader(this.GoogleUserName, f.GoogleReaderFeedId, item.Id, item.FlagStatus != Flagged.None);
                    }
                    break;
            }
        }
        internal void ChangeItemStarredStateInGoogleReader(string feedId, string itemId, bool starred)
        {
            string itemReadUrl = apiUrlPrefix + "edit-tag";
            string op = (starred ? "&a=" : "&r=");
            string starredLabel = Uri.EscapeDataString("user/" + this.GoogleUserId + "/state/com.google/starred");
            string body = "s=" + Uri.EscapeDataString(feedId) + "&i=" + Uri.EscapeDataString(itemId) + "&ac=edit-tags" + op + starredLabel + "&async=true&T=" + GetGoogleEditToken(this.SID);
            HttpWebResponse response = AsyncWebRequest.PostSyncResponse(itemReadUrl, body, MakeGoogleCookie(this.SID), null, this.Proxy);
            if (response.StatusCode != HttpStatusCode.OK)
            {
                throw new WebException(response.StatusDescription);
            }
        }
        internal void ChangeItemReadStateInGoogleReader(string feedId, string itemId, bool beenRead)
        {
            string itemReadUrl = apiUrlPrefix + "edit-tag";
            string op = (beenRead ? "&a=" : "&r=");
            string readLabel = Uri.EscapeDataString("user/" + this.GoogleUserId + "/state/com.google/read");
            string body = "s=" + Uri.EscapeDataString(feedId) + "&i=" + Uri.EscapeDataString(itemId) + "&ac=edit-tags" + op + readLabel + "&async=true&T=" + GetGoogleEditToken(this.SID);
            HttpWebResponse response = AsyncWebRequest.PostSyncResponse(itemReadUrl, body, MakeGoogleCookie(this.SID), null, this.Proxy);
            if (response.StatusCode != HttpStatusCode.OK)
            {
                throw new WebException(response.StatusDescription);
            }
        }
        internal void MarkAllItemsAsReadInGoogleReader(string feedUrl, DateTime olderThan)
        {
            if (!StringHelper.EmptyTrimOrNull(feedUrl) && feedsTable.ContainsKey(feedUrl))
            {
                GoogleReaderNewsFeed f = feedsTable[feedUrl] as GoogleReaderNewsFeed;
                DateTime UnixEpoch = new DateTime(1970, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc);
                string markReadUrl = apiUrlPrefix + "mark-all-as-read";
                string body = "T=" + GetGoogleEditToken(this.SID) + "&ts=" + Convert.ToInt32((olderThan.ToUniversalTime() - UnixEpoch).TotalSeconds) + "&s=" + Uri.EscapeDataString(f.GoogleReaderFeedId);
                HttpWebResponse response = AsyncWebRequest.PostSyncResponse(markReadUrl, body, MakeGoogleCookie(this.SID), null, this.Proxy);
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
            }
        }
        public override void MarkAllCachedItemsAsRead(INewsFeed feed)
        {
            DateTime newestItemAge = DateTime.MinValue;
            if (feed != null && !string.IsNullOrEmpty(feed.link) && itemsTable.ContainsKey(feed.link))
            {
                IFeedDetails fi = itemsTable[feed.link] as IFeedDetails;
                if (fi != null)
                {
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
                GoogleReaderUpdater.MarkAllItemsAsReadInGoogleReader(this.GoogleUserName, feed.link, newestItemAge);
            }
        }
        public override INewsFeedCategory AddCategory(INewsFeedCategory cat)
        {
            if (cat == null)
                throw new ArgumentNullException("cat");
            if (cat.Value.IndexOf(FeedSource.CategorySeparator) != -1)
                throw new NotSupportedException("Google Reader does not support nested categories");
            if (this.categories.ContainsKey(cat.Value))
                return this.categories[cat.Value];
            this.categories.Add(cat.Value, cat);
            readonly_categories = new ReadOnlyDictionary<string, INewsFeedCategory>(this.categories);
            GoogleReaderUpdater.AddCategoryInGoogleReader(this.GoogleUserName, cat.Value);
            return cat;
        }
        public override INewsFeedCategory AddCategory(string cat)
        {
            if (StringHelper.EmptyTrimOrNull(cat))
                return null;
            if (cat.IndexOf(FeedSource.CategorySeparator)!= -1)
                throw new NotSupportedException("Google Reader does not support nested categories");
            if (this.categories.ContainsKey(cat))
                return this.categories[cat];
            category c = new category(cat);
            this.categories.Add(cat, c);
            readonly_categories = new ReadOnlyDictionary<string, INewsFeedCategory>(this.categories);
            GoogleReaderUpdater.AddCategoryInGoogleReader(this.GoogleUserName, cat);
            return c;
        }
        internal void AddCategoryInGoogleReader(string name)
        {
            string dummyFeed = "http://rss.netflix.com/QueueRSS?id=P2792793912689011005960561087208982";
            this.AddFeedInGoogleReader(dummyFeed, name);
            this.DeleteFeedFromGoogleReader(dummyFeed);
        }
        public override void DeleteCategory(string cat)
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
                        GoogleReaderUpdater.DeleteCategoryInGoogleReader(this.GoogleUserName, c);
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
                        GoogleReaderUpdater.DeleteFeedFromGoogleReader(this.GoogleUserName, feedUrl);
                    }
                }
                readonly_categories = new ReadOnlyDictionary<string, INewsFeedCategory>(this.categories);
            }
        }
        internal void DeleteCategoryInGoogleReader(string name)
        {
            if (!StringHelper.EmptyTrimOrNull(name))
            {
                string labelUrl = apiUrlPrefix + "disable-tag";
                string labelParams = "&s=" + "user/" + this.GoogleUserId + "/label/" + Uri.EscapeDataString(name) + "&t=" + Uri.EscapeDataString(name);
                string body = "ac=disable-tags&i=null&T=" + GetGoogleEditToken(this.SID) + labelParams;
                HttpWebResponse response = AsyncWebRequest.PostSyncResponse(labelUrl, body, MakeGoogleCookie(this.SID), null, this.Proxy);
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
            }
        }
        internal void RenameCategoryInGoogleReader(string oldName, string newName)
        {
            if (!feedsTable.Any(x => x.Value.categories.Contains(oldName)))
            {
                this.AddCategoryInGoogleReader(newName);
            }
            this.DeleteCategoryInGoogleReader(oldName);
        }
        public override void RenameCategory(string oldName, string newName)
        {
            if(( StringHelper.EmptyTrimOrNull(oldName) || StringHelper.EmptyTrimOrNull(newName) )
                || oldName.Equals(newName) ){
                return;
            }
            base.RenameCategory(oldName, newName);
            GoogleReaderUpdater.RenameCategoryInGoogleReader(this.GoogleUserName, oldName, newName);
        }
        internal void ChangeCategoryInGoogleReader(string feedUrl, string newCategory, string oldCategory)
        {
            if (!StringHelper.EmptyTrimOrNull(feedUrl) && feedsTable.ContainsKey(feedUrl))
            {
                GoogleReaderNewsFeed f = feedsTable[feedUrl] as GoogleReaderNewsFeed;
                string labelUrl = apiUrlPrefix + "subscription/edit";
                string labelParams = String.Empty;
                if (oldCategory != null)
                {
                    labelParams = "&r=" + "user/" + this.GoogleUserId + "/label/" + Uri.EscapeDataString(oldCategory);
                }
                if (newCategory != null)
                {
                    labelParams += "&a=" + "user/" + this.GoogleUserId + "/label/" + Uri.EscapeDataString(newCategory);
                }
                string body = "ac=edit&i=null&T=" + GetGoogleEditToken(this.SID) + "&t=" + Uri.EscapeDataString(f.title) + "&s=" + Uri.EscapeDataString(f.GoogleReaderFeedId) + labelParams;
                HttpWebResponse response = AsyncWebRequest.PostSyncResponse(labelUrl, body, MakeGoogleCookie(this.SID), null, this.Proxy);
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    throw new WebException(response.StatusDescription);
                }
            }
        }
    }
    internal class GoogleReaderSubscription
    {
        public string Id { get; set; }
        public string Title { get; set; }
        public List<GoogleReaderLabel> Categories { get; set; }
        public long FirstItemMSec { get; set; }
        public string FeedUrl
        {
            get
            {
                string url = Id.Substring("feed/".Length);
                Uri uri = null;
                if (Uri.TryCreate(url, UriKind.Absolute, out uri))
                {
                    return uri.CanonicalizedUri();
                }
                else
                {
                    return url;
                }
            }
        }
        internal GoogleReaderSubscription(string id, string title, List<GoogleReaderLabel> categories, long firstitemmsec)
        {
            this.Id = id;
            this.Title = title;
            this.Categories = categories;
            this.FirstItemMSec = firstitemmsec;
        }
    }
    internal class GoogleReaderLabel
    {
        public string Label { get; set; }
        public string Id { get; set; }
        internal GoogleReaderLabel(string label, string id){
            this.Label = label;
            this.Id = id;
        }
    }
    [XmlType(Namespace = NamespaceCore.Feeds_vCurrent)]
    public class GoogleReaderNewsFeed : NewsFeed
    {
        private GoogleReaderNewsFeed() { ;}
        internal GoogleReaderNewsFeed(GoogleReaderSubscription subscription, INewsFeed banditfeed, object owner)
        {
            if (subscription == null) throw new ArgumentNullException("subscription");
            this.mysubscription = subscription;
            foreach (GoogleReaderLabel label in subscription.Categories)
            {
                this._categories.Add(label.Label);
            }
            if (banditfeed != null)
            {
                this.lastretrievedSpecified = banditfeed.lastretrievedSpecified;
                this.lastretrieved = banditfeed.lastretrieved;
                this.lastmodifiedSpecified = banditfeed.lastmodifiedSpecified;
                this.lastmodified = banditfeed.lastmodified;
                this.id = banditfeed.id;
                this.enclosurefolder = banditfeed.enclosurefolder;
                this.deletedstories = banditfeed.deletedstories;
                this.storiesrecentlyviewed = banditfeed.storiesrecentlyviewed;
                this.refreshrate = banditfeed.refreshrate;
                this.refreshrateSpecified = banditfeed.refreshrateSpecified;
                this.maxitemage = banditfeed.maxitemage;
                this.markitemsreadonexit = banditfeed.markitemsreadonexit;
                this.markitemsreadonexitSpecified = banditfeed.markitemsreadonexitSpecified;
                this.listviewlayout = banditfeed.listviewlayout;
                this.favicon = banditfeed.favicon;
                this.stylesheet = banditfeed.stylesheet;
                this.cacheurl = banditfeed.cacheurl;
                this.enclosurealert = banditfeed.enclosurealert;
                this.enclosurealertSpecified = banditfeed.enclosurealertSpecified;
                this.alertEnabled = banditfeed.alertEnabled;
                this.alertEnabledSpecified = banditfeed.alertEnabledSpecified;
                this.Any = banditfeed.Any;
            }
            if (owner is GoogleReaderFeedSource)
            {
                this.owner = owner;
            }
        }
        GoogleReaderSubscription mysubscription = null;
        public string GoogleReaderFeedId
        {
            get { return this.mysubscription.Id; }
        }
        [XmlElement(DataType = "anyURI")]
        public override string link
        {
            get
            {
                return (mysubscription == null ? this._link : mysubscription.FeedUrl);
            }
            set
            {
                this._link = value;
            }
        }
        public override string title
        {
            get
            {
                return mysubscription.Title;
            }
            set
            {
                GoogleReaderFeedSource myowner = owner as GoogleReaderFeedSource;
                if (myowner != null && !StringHelper.EmptyTrimOrNull(value) && !mysubscription.Title.Equals(value))
                {
                    myowner.GoogleReaderUpdater.RenameFeedInGoogleReader(myowner.GoogleUserName, this.link, value);
                    mysubscription.Title = value;
                }
            }
        }
        [XmlAttribute]
        public override string category
        {
            get
            {
                return base.category;
            }
            set
            {
                GoogleReaderFeedSource myowner = owner as GoogleReaderFeedSource;
                if (myowner != null &&
                    ((base.category == null && value != null) || !base.category.Equals(value)) )
                {
                    myowner.GoogleReaderUpdater.ChangeCategoryInGoogleReader(myowner.GoogleUserName, this.link, value, base.category);
                }
                base.category = value;
            }
        }
        public override List<string> categories
        {
            get
            {
                return this._categories;
            }
            set
            {
            }
        }
    }
}
