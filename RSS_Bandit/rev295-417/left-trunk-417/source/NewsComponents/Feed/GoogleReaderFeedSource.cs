using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Xml;
using NewsComponents.Net;
using NewsComponents.Search;
namespace NewsComponents.Feed
{
    class GoogleReaderFeedSource : FeedSource
    {
        private static readonly string authUrl = @"https://www.google.com/accounts/ClientLogin?continue=http://www.google.com&service=reader&source=RssBandit&Email={0}&Passwd={1}";
        private static readonly string feedUrlPrefix = @"http://www.google.com/reader/atom/";
        private static readonly string apiUrlPrefix = @"http://www.google.com/reader/api/0/";
        private string SID = String.Empty;
        internal GoogleReaderFeedSource(INewsComponentsConfiguration configuration, SubscriptionLocation location)
        {
            this.p_configuration = configuration;
            if (this.p_configuration == null)
                this.p_configuration = FeedSource.DefaultConfiguration;
            this.location = location;
            ValidateAndThrow(this.Configuration);
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
            this.LoadFeedlistFromGoogleReader();
        }
        private void LoadFeedlistFromGoogleReader()
        {
            string feedlistUrl = apiUrlPrefix + "subscription/list";
            this.AuthenticateUser();
            XmlDocument doc = new XmlDocument();
            doc.Load(XmlReader.Create(AsyncWebRequest.GetSyncResponseStream(feedlistUrl, null, this.Proxy, MakeGoogleCookie(this.SID))));
            XmlNode node;
            var feedlist = from node in doc.SelectNodes("/object/list[@name='subscriptions']/object")
                           select MakeSubscription(node);
        }
        private static GoogleReaderSubscription MakeSubscription(XmlNode node)
        {
            XmlNode id_node = node.SelectSingleNode("string[@name='id']");
            string feedid = (id_node == null ? String.Empty : id_node.InnerText);
            XmlNode title_node = node.SelectSingleNode("string[name='title']");
            string title = (title_node == null ? String.Empty : title_node.InnerText);
            XmlNode fim_node = node.SelectSingleNode("string[@name='firstitemmsec']");
            long firstitemmsec = (id_node == null ? 0 : Int64.Parse(fim_node.InnerText));
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
        private static CookieCollection MakeGoogleCookie(string sid)
        {
           Cookie cookie = new Cookie("SID", sid, "/", ".google.com");
           cookie.Expires = DateTime.Now + new TimeSpan(365,0,0,0);
           CookieCollection collection = new CookieCollection();
           collection.Add(cookie);
           return collection;
        }
        public override void RefreshFeeds(bool force_download)
        {
        }
        public override void RefreshFeeds(string category, bool force_download)
        {
        }
    }
    internal class GoogleReaderSubscription
    {
        public string Id { get; set; }
        public string Title { get; set; }
        public List<GoogleReaderLabel> Categories { get; set; }
        public long FirstItemMSec { get; set; }
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
    internal class GoogleReaderNewsFeed : NewsFeed
    {
        private GoogleReaderNewsFeed() { ;}
        internal GoogleReaderNewsFeed(GoogleReaderSubscription subscription, object owner)
        {
            if (subscription == null) throw new ArgumentNullException("subscription");
            this.mysubscription = subscription;
            if (owner is GoogleReaderFeedSource)
            {
                this.owner = owner;
            }
        }
        GoogleReaderSubscription mysubscription = null;
    }
}
