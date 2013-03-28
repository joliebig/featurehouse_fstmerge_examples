using System;
using System.Collections.Generic;
using System.IO;
using System.Threading;
using System.Xml;
using System.Xml.Serialization;
using System.Xml.Schema;
using NewsComponents.Net;
using NewsComponents.News;
using RssBandit.Common;
namespace NewsComponents.Feed
{
    class BanditFeedSource : FeedSource
    {
        internal BanditFeedSource(INewsComponentsConfiguration configuration, SubscriptionLocation location)
        {
            this.p_configuration = configuration;
            if (this.p_configuration == null)
                this.p_configuration = FeedSource.DefaultConfiguration;
            this.location = location;
            ValidateAndThrow(this.Configuration);
            this.LoadFeedlistSchema();
            this.rssParser = new RssParser(this);
            this.PodcastFolder = this.Configuration.DownloadedFilesDataPath;
            if (!String.IsNullOrEmpty(EnclosureFolder))
            {
                this.enclosureDownloader = new BackgroundDownloadManager(this.Configuration, this);
                this.enclosureDownloader.DownloadCompleted += this.OnEnclosureDownloadComplete;
            }
            this.AsyncWebRequest = new AsyncWebRequest();
            this.AsyncWebRequest.OnAllRequestsComplete += this.OnAllRequestsComplete;
        }
        private IDictionary<string, INntpServerDefinition> nntpServers = new Dictionary<string, INntpServerDefinition>();
        private IDictionary<string, UserIdentity> identities = new Dictionary<string, UserIdentity>();
        private XmlSchema feedsSchema = null;
        private void LoadFeedlistSchema()
        {
            using (Stream xsdStream = Resource.Manager.GetStream("Resources.feedListSchema.xsd"))
            {
                feedsSchema = XmlSchema.Read(xsdStream, null);
            }
        }
        private void LoadFeedlist(string feedListUrl, ValidationEventHandler veh)
        {
            LoadFeedlist(AsyncWebRequest.GetSyncResponseStream(feedListUrl, null, this.UserAgent, this.Proxy), veh);
            SearchHandler.CheckIndex();
        }
        private void LoadFeedlist(Stream xmlStream, ValidationEventHandler veh)
        {
            XmlParserContext context =
                new XmlParserContext(null, new RssBanditXmlNamespaceResolver(), null, XmlSpace.None);
            XmlReader reader = new RssBanditXmlReader(xmlStream, XmlNodeType.Document, context);
            validationErrorOccured = false;
            XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(feeds));
            feeds myFeeds = (feeds)serializer.Deserialize(reader);
            reader.Close();
   FeedSource.MigrationProperties.Clear();
            if (myFeeds.categories != null)
            {
                foreach (category cat in myFeeds.categories)
                {
                    string cat_trimmed = cat.Value.Trim();
                    if (!this.categories.ContainsKey(cat_trimmed))
                    {
                        cat.Value = cat_trimmed;
                        this.categories.Add(cat_trimmed, cat);
                    }
                }
            }
            if (myFeeds.feed != null)
            {
                foreach (NewsFeed f in myFeeds.feed)
                {
                    if (feedsTable.ContainsKey(f.link) == false)
                    {
                        bool isBadUri = false;
                        try
                        {
                            Uri uri = new Uri(f.link);
                            if (NntpWebRequest.NewsUriScheme.Equals(uri.Scheme))
                            {
                                f.link = NntpWebRequest.NntpUriScheme + uri.CanonicalizedUri().Substring(uri.Scheme.Length);
                            }
                            else
                            {
                                f.link = uri.CanonicalizedUri();
                            }
                        }
                        catch (Exception)
                        {
                            isBadUri = true;
                        }
                        if (isBadUri)
                        {
                            continue;
                        }
                        else
                        {
                            if (feedsTable.ContainsKey(f.link) == false)
                            {
                                f.owner = this;
                                this.feedsTable.Add(f.link, f);
                                if (f.category != null)
                                {
                                    string cat_trimmed = f.category = f.category.Trim();
                                    if (!this.categories.ContainsKey(cat_trimmed))
                                    {
                                        this.AddCategory(cat_trimmed);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (myFeeds.listviewLayouts != null)
            {
                foreach (listviewLayout layout in myFeeds.listviewLayouts)
                {
                    string layout_trimmed = layout.ID.Trim();
                    if (!this.layouts.ContainsKey(layout_trimmed))
                    {
                        this.layouts.Add(layout_trimmed, layout.FeedColumnLayout);
                    }
                }
            }
            if (myFeeds.nntpservers != null)
            {
                foreach (NntpServerDefinition sd in myFeeds.nntpservers)
                {
                    if (nntpServers.ContainsKey(sd.Name) == false)
                    {
                        nntpServers.Add(sd.Name, sd);
                    }
                }
            }
            if (myFeeds.identities != null)
            {
                foreach (UserIdentity ui in myFeeds.identities)
                {
                    if (identities.ContainsKey(ui.Name) == false)
                    {
                        identities.Add(ui.Name, ui);
                    }
                }
            }
            if (myFeeds.refreshrateSpecified)
            {
    FeedSource.MigrationProperties.Add("RefreshRate", myFeeds.refreshrate);
            }
            if (!string.IsNullOrEmpty(myFeeds.stylesheet))
            {
    FeedSource.MigrationProperties.Add("Stylesheet", myFeeds.stylesheet);
            }
            if (myFeeds.downloadenclosuresSpecified)
            {
    FeedSource.MigrationProperties.Add("DownloadEnclosures", myFeeds.downloadenclosures);
            }
            if (myFeeds.enclosurecachesizeSpecified)
            {
    FeedSource.MigrationProperties.Add("EnclosureCacheSize", myFeeds.enclosurecachesize);
            }
            if (myFeeds.numtodownloadonnewfeedSpecified)
            {
    FeedSource.MigrationProperties.Add("NumEnclosuresToDownloadOnNewFeed", myFeeds.numtodownloadonnewfeed);
            }
            if (myFeeds.enclosurealertSpecified)
            {
             FeedSource.MigrationProperties.Add("EnclosureAlert", myFeeds.enclosurealert);
            }
            if (myFeeds.createsubfoldersforenclosuresSpecified)
            {
    FeedSource.MigrationProperties.Add("CreateSubfoldersForEnclosures", myFeeds.createsubfoldersforenclosures);
            }
            if (myFeeds.markitemsreadonexitSpecified)
            {
    FeedSource.MigrationProperties.Add("MarkItemsReadOnExit", myFeeds.markitemsreadonexit);
            }
            if (!string.IsNullOrEmpty(myFeeds.enclosurefolder))
            {
    FeedSource.MigrationProperties.Add("EnclosureFolder", myFeeds.enclosurefolder);
            }
            if (!string.IsNullOrEmpty(myFeeds.podcastfolder))
            {
                this.PodcastFolder = myFeeds.podcastfolder;
            }
            if (!string.IsNullOrEmpty(myFeeds.podcastfileexts))
            {
                this.PodcastFileExtensionsAsString = myFeeds.podcastfileexts;
            }
            if (!string.IsNullOrEmpty(myFeeds.listviewlayout))
            {
                this.listviewlayout = myFeeds.listviewlayout;
            }
            try
            {
                if (!string.IsNullOrEmpty(myFeeds.maxitemage))
                {
     FeedSource.MigrationProperties.Add("MaxItemAge", myFeeds.maxitemage);
                }
            }
            catch (FormatException fe)
            {
                Trace("Error occured while parsing maximum item age from feed list: {0}", fe.ToString());
            }
        }
        public override void DeleteAllFeedsAndCategories()
        {
            base.DeleteAllFeedsAndCategories();
            this.ClearItemsCache();
        }
        public override void LoadFeedlist()
        {
            this.LoadFeedlist(location.Location, FeedSource.ValidationCallbackOne);
        }
        public override void BootstrapAndLoadFeedlist(feeds feedlist)
        {
            this.ImportFeedlist(feedlist, null, true, false);
        }
    }
}
