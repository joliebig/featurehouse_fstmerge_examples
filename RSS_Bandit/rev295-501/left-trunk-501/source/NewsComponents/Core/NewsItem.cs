using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Text;
using System.Xml;
using System.Xml.XPath;
using NewsComponents.Collections;
using NewsComponents.News;
using NewsComponents.RelationCosmos;
using NewsComponents.Utils;
using NewsComponents.Feed;
namespace NewsComponents
{
    public class Enclosure: IEnclosure
    {
        private readonly string mimeType;
        private readonly long length;
        private TimeSpan duration;
        private readonly string url;
        private string description;
        private bool downloaded;
        public Enclosure(string mimeType, long length, string url, string description)
        {
            this.length = length;
            this.mimeType = mimeType;
            this.url = url;
            this.description = description;
            Duration = TimeSpan.MinValue;
        }
        public string MimeType
        {
            get
            {
                return mimeType;
            }
        }
        public long Length
        {
            get
            {
                return length;
            }
        }
        public string Url
        {
            get
            {
                return url;
            }
        }
        public string Description
        {
            get
            {
                return description;
            }
            set
            {
                description = value;
            }
        }
        public bool Downloaded
        {
            get
            {
                return downloaded;
            }
            set
            {
                downloaded = value;
            }
        }
        public TimeSpan Duration
        {
            get
            {
                return duration;
            }
            set
            {
                duration = value;
            }
        }
        public override bool Equals(object obj)
        {
            return Equals(obj as Enclosure);
        }
        public bool Equals(IEnclosure other)
        {
            return Equals(other as Enclosure);
        }
        public bool Equals(Enclosure item)
        {
            if (item == null)
            {
                return false;
            }
            if (ReferenceEquals(this, item))
            {
                return true;
            }
            if (String.Compare(Url, item.Url, StringComparison.OrdinalIgnoreCase) == 0)
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
    public class NewsItem : RelationBase<INewsItem>, INewsItem, ISizeInfo, IEquatable<NewsItem>,INotifyPropertyChanged
    {
        public string FeedLink
        {
            [DebuggerStepThrough]
            get
            {
                if (p_feed != null)
                    return p_feed.link;
                return null;
            }
        }
        public string Link
        {
            [DebuggerStepThrough]
            get
            {
                return HRef;
            }
        }
        public DateTime Date
        {
            get
            {
                return aPointInTime;
            }
            set
            {
                base.PointInTime = value;
            }
        }
        protected INewsFeed p_feed;
        public INewsFeed Feed
        {
            get
            {
                return p_feed;
            }
        }
        private string p_title;
        private readonly string p_parentId;
        public string ParentId
        {
            get
            {
                return p_parentId;
            }
        }
        private string p_author;
        private byte[] p_content;
        public string Content
        {
            get
            {
                return p_content != null ? Encoding.UTF8.GetString(p_content) : null;
            }
        }
        void INewsItem.SetContent(string newContent, ContentType contentType)
        {
            SetContent(newContent, contentType);
        }
        internal byte[] GetContent()
        {
            return p_content;
        }
        internal void SetContent(byte[] newContent, ContentType contentType)
        {
            if (newContent != null)
            {
                p_content = newContent;
                p_contentType = contentType;
                return;
            }
        }
        internal void SetContent(string newContent, ContentType contentType)
        {
            if (string.IsNullOrEmpty(newContent))
            {
                p_content = null;
                p_contentType = ContentType.None;
                return;
            }
            p_content = Encoding.UTF8.GetBytes(newContent);
            p_contentType = contentType;
        }
        public bool HasContent
        {
            get
            {
                return (p_contentType != ContentType.None);
            }
        }
        protected ContentType p_contentType = ContentType.None;
        public ContentType ContentType
        {
            get
            {
                return p_contentType;
            }
            set
            {
                p_contentType = value;
            }
        }
        protected bool p_beenRead = false;
        public bool BeenRead
        {
            get
            {
                return p_beenRead;
            }
            set
            {
                p_beenRead = value;
                this.OnPropertyChanged("BeenRead");
            }
        }
        protected IFeedDetails feedInfo;
        public IFeedDetails FeedDetails
        {
            get
            {
                return feedInfo;
            }
            set
            {
                feedInfo = value;
            }
        }
        protected Flagged p_flagStatus = Flagged.None;
        public Flagged FlagStatus
        {
            get
            {
                return p_flagStatus;
            }
            set
            {
                p_flagStatus = value;
                this.OnPropertyChanged("FlagStatus");
            }
        }
        protected bool p_hasNewComments;
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
        protected bool p_watchComments;
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
        protected string p_language;
        public string Language
        {
            get
            {
                if (string.IsNullOrEmpty(p_language) && (feedInfo != null))
                {
                    return feedInfo.Language;
                }
                else
                {
                    return p_language;
                }
            }
            set
            {
                p_language = value;
            }
        }
        protected NewsItem()
        {
            ;
        }
        public NewsItem (INewsFeed parent, INewsItem item)
            : this(parent, item.Title, item.Link, null, item.Date, item.Subject, item.Id, item.ParentId)
        {
            this.OptionalElements = (Hashtable)OptionalElements.Clone();
            this.p_beenRead = item.BeenRead;
            this.p_author = item.Author;
            this.p_flagStatus = item.FlagStatus;
            this.SetContent(item.Content, item.ContentType);
            this.p_contentType = item.ContentType;
            this.commentUrl = item.CommentUrl;
            this.commentRssUrl = item.CommentRssUrl;
            this.commentCount = item.CommentCount;
            this.commentStyle = item.CommentStyle;
            this.p_watchComments = item.WatchComments;
            this.p_hasNewComments = item.HasNewComments;
        }
        public NewsItem(INewsFeed feed, string title, string link, string content, DateTime date, string subject)
            :
                this(feed, title, link, content, date, subject, ContentType.Unknown, new Hashtable(), link, null)
        {
        }
        public NewsItem(INewsFeed feed, string title, string link, string content, string author, DateTime date,
                        string id, string parentId)
            :
                this(feed, title, link, content, date, null, ContentType.Text, null, id, parentId)
        {
            p_author = author;
        }
        public NewsItem(INewsFeed feed, string title, string link, string content, DateTime date, string subject,
                        string id, string parentId)
            :
                this(feed, title, link, content, date, subject, ContentType.Unknown, new Hashtable(), id, parentId)
        {
        }
        public NewsItem(INewsFeed feed, string title, string link, string content, DateTime date, string subject,
                        ContentType ctype, Hashtable otherElements, string id, string parentId)
            :
                this(feed, title, link, content, date, subject, ctype, otherElements, id, parentId, link)
        {
        }
        public NewsItem(INewsFeed feed, string title, string link, string content, DateTime date, string subject,
                        ContentType ctype, Hashtable otherElements, string id, string parentId, string baseUrl) :
                            this(
                            feed, title, link, content, date, subject, ctype, otherElements, id, parentId, baseUrl, null
                            )
        {
        }
        public NewsItem(INewsFeed feed, string title, string link, string content, DateTime date, string subject,
                        ContentType ctype, Hashtable otherElements, string id, string parentId, string baseUrl,
                        List<string> outgoingLinks)
        {
            OptionalElements = otherElements;
            p_feed = feed;
            p_title = title;
            HRef = (link != null ? link.Trim() : null);
            HRef = HtmlHelper.ConvertToAbsoluteUrl(HRef, baseUrl);
            if (content != null)
            {
                if (content.StartsWith("<![CDATA["))
                {
                    content = content.Replace("<![CDATA[", String.Empty).Replace("]]>", String.Empty);
                }
            }
            ProcessTitle(content);
            content = HtmlHelper.ExpandRelativeUrls(content, baseUrl);
            SetContent(content, ctype);
            p_id = id;
            if (p_id == null)
            {
                int hc = (p_title != null ? p_title.GetHashCode() : 0) +
                         (HasContent ? Content.GetHashCode() : 0);
                p_id = hc.ToString();
            }
            p_parentId = parentId;
            base.PointInTime = date;
            this.subject = subject;
            if (outgoingLinks != null)
                OutGoingLinks = outgoingLinks;
            if (FeedSource.buildRelationCosmos)
            {
                if (outgoingLinks == null)
                {
                    ProcessOutGoingLinks(content);
                }
                bool idEqHref = ReferenceEquals(HRef, p_id);
                if (null != HRef)
                    HRef = RelationCosmos.RelationCosmos.UrlTable.Add(HRef);
                if (idEqHref)
                    p_id = HRef;
                else
                    p_id = RelationCosmos.RelationCosmos.UrlTable.Add(p_id);
                if (null != p_parentId && p_parentId.Length > 0)
                {
                    string p_parentIdUrl = RelationCosmos.RelationCosmos.UrlTable.Add(
                        NntpParser.CreateGoogleUrlFromID(p_parentId));
                    if (ReferenceEquals(outgoingRelationships, GetList<string>().Empty))
                    {
                        outgoingRelationships = new List<string>(1);
                    }
                    outgoingRelationships.Add(p_parentIdUrl);
                }
            }
        }
        private string subject;
        private SupportedCommentStyle commentStyle;
        private string commentUrl = null;
        private string commentRssUrl = null;
        private int commentCount = NoComments;
        private Hashtable optionalElements;
        private List<IEnclosure> enclosures = null;
        public static int NoComments = Int32.MinValue;
        public override bool HasExternalRelations
        {
            get
            {
                if (!string.IsNullOrEmpty(commentRssUrl))
                {
                    if (FeedSource.UnconditionalCommentRss)
                        return true;
                    if (commentCount > 0)
                        return true;
                }
                return base.HasExternalRelations;
            }
        }
        public override void SetExternalRelations<T>(IList<T> relations)
        {
            if (base.GetExternalRelations().Count > 0)
            {
                FeedSource.RelationCosmosRemoveRange(relations);
            }
            FeedSource.RelationCosmosAddRange(relations);
            base.SetExternalRelations(relations);
        }
        public string Author
        {
            get
            {
                if (FeedDetails.Type == FeedType.Rss)
                {
                    string t = HtmlHelper.StripAnyTags(p_author);
                    if (t.IndexOf("&") >= 0 && t.IndexOf(";") >= 0)
                    {
                        t = HtmlHelper.HtmlDecode(t);
                    }
                    return t;
                }
                else
                {
                    return p_author;
                }
            }
            set
            {
                p_author = value;
            }
        }
        public string Title
        {
            get
            {
                string t = HtmlHelper.StripAnyTags(p_title);
                if (t.IndexOf("&") >= 0 && t.IndexOf(";") >= 0)
                {
                    t = HtmlHelper.HtmlDecode(t);
                }
                return t.Trim();
            }
            set
            {
                p_title = (value ?? String.Empty);
            }
        }
        public string Subject
        {
            get
            {
                string t = HtmlHelper.StripAnyTags(subject);
                if (t.IndexOf("&") >= 0 && t.IndexOf(";") >= 0)
                {
                    t = HtmlHelper.HtmlDecode(t);
                }
                return t;
            }
            set
            {
                subject = (value ?? String.Empty);
            }
        }
        public SupportedCommentStyle CommentStyle
        {
            get
            {
                return commentStyle;
            }
            set
            {
                commentStyle = value;
            }
        }
        public int CommentCount
        {
            get
            {
                return commentCount;
            }
            set
            {
                commentCount = value;
            }
        }
        public string CommentUrl
        {
            get
            {
                return commentUrl;
            }
            set
            {
                commentUrl = value;
            }
        }
        public string CommentRssUrl
        {
            get
            {
                return commentRssUrl;
            }
            set
            {
                commentRssUrl = value;
            }
        }
        public List<IEnclosure> Enclosures
        {
            get
            {
                return enclosures;
            }
            set
            {
                enclosures = value;
            }
        }
        public Hashtable OptionalElements
        {
            get
            {
                if (optionalElements == null)
                {
                    optionalElements = new Hashtable();
                }
                return optionalElements;
            }
            set
            {
                optionalElements = value;
            }
        }
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
        public object Clone()
        {
            NewsItem item = new NewsItem(p_feed, p_title, HRef, null, Date, subject, p_id, p_parentId);
            item.OptionalElements = (Hashtable) OptionalElements.Clone();
            item.p_beenRead = p_beenRead;
            item.p_author = p_author;
            item.p_flagStatus = p_flagStatus;
            item.p_content = p_content;
            item.p_contentType = p_contentType;
            item.commentUrl = commentUrl;
            item.commentRssUrl = commentRssUrl;
            item.commentCount = commentCount;
            item.commentStyle = commentStyle;
            item.p_watchComments = p_watchComments;
            item.p_hasNewComments = p_hasNewComments;
            return item;
        }
        public INewsItem Clone(INewsFeed f)
        {
            NewsItem newItem = (NewsItem) Clone();
            newItem.p_feed = f;
            return newItem;
        }
        public void WriteItem(XmlWriter writer, bool useGMTDate, bool noDescriptions)
        {
            writer.WriteStartElement("item");
            if ((p_language != null) && (p_language.Length != 0))
            {
                writer.WriteAttributeString("xml", "lang", null, p_language);
            }
            if ((p_title != null) && (p_title.Length != 0))
            {
                writer.WriteElementString("title", p_title);
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
            if ((subject != null) && (subject.Length != 0))
            {
                writer.WriteElementString("category", subject);
            }
            if ((p_id != null) && (p_id.Length != 0) && (p_id.Equals(HRef) == false))
            {
                writer.WriteStartElement("guid");
                writer.WriteAttributeString("isPermaLink", "false");
                writer.WriteString(p_id);
                writer.WriteEndElement();
            }
            if ((p_author != null) && (p_author.Length != 0))
            {
                writer.WriteElementString("creator", "http://purl.org/dc/elements/1.1/", p_author);
            }
            if ((p_parentId != null) && (p_parentId.Length != 0))
            {
                writer.WriteStartElement("annotate", "reference", "http://purl.org/rss/1.0/modules/annotate/");
                writer.WriteAttributeString("rdf", "resource", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", p_parentId);
                writer.WriteEndElement();
            }
            if (!noDescriptions && HasContent)
            {
                writer.WriteStartElement("description");
                writer.WriteCData(Content);
                writer.WriteEndElement();
            }
            if ((commentUrl != null) && (commentUrl.Length != 0))
            {
                if (commentStyle == SupportedCommentStyle.CommentAPI)
                {
                    writer.WriteStartElement("wfw", "comment", RssHelper.NsCommentAPI);
                    writer.WriteString(commentUrl);
                    writer.WriteEndElement();
                }
            }
            if ((commentRssUrl != null) && (commentRssUrl.Length != 0))
            {
                writer.WriteStartElement("wfw", "commentRss", RssHelper.NsCommentAPI);
                writer.WriteString(commentRssUrl);
                writer.WriteEndElement();
            }
            if (commentCount != NoComments)
            {
                writer.WriteStartElement("slash", "comments", "http://purl.org/rss/1.0/modules/slash/");
                writer.WriteString(commentCount.ToString(CultureInfo.InvariantCulture));
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
            if (enclosures != null)
            {
                foreach (Enclosure enc in enclosures)
                {
                    writer.WriteStartElement("enclosure");
                    writer.WriteAttributeString("url", enc.Url);
                    writer.WriteAttributeString("type", enc.MimeType);
                    writer.WriteAttributeString("length", enc.Length.ToString(CultureInfo.InvariantCulture));
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
                    toReturn = ToNntpMessage();
                    break;
                default:
                    throw new NotSupportedException(format.ToString());
            }
            return toReturn;
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
        public override String ToString()
        {
            return ToString(NewsItemSerializationFormat.RssItem);
        }
        private void ProcessTitle(string content)
        {
            if ((p_title == null) || (p_title.Length == 0))
            {
                p_title = GetFirstWords(HtmlHelper.StripAnyTags(content), 8);
            }
            if (p_title.StartsWith("<![CDATA["))
            {
                p_title = p_title.Replace("<![CDATA[", String.Empty).Replace("]]>", String.Empty);
            }
            p_title = p_title.Replace(Environment.NewLine, " ").Replace("\r", " ").Replace("\n", " ");
        }
        private void ProcessOutGoingLinks(string content)
        {
            if (FeedSource.BuildRelationCosmos)
            {
                outgoingRelationships = HtmlHelper.RetrieveLinks(content);
            }
            else
            {
                outgoingRelationships = new List<string>();
            }
        }
        private static string GetFirstWords(string text, int wordCount)
        {
            if (text == null)
                return String.Empty;
            return StringHelper.GetFirstWords(text, wordCount) + "(...)";
        }
        public XPathNavigator CreateNavigator()
        {
            return CreateNavigator(false);
        }
        public XPathNavigator CreateNavigator(bool standalone)
        {
            return CreateNavigator(standalone, true);
        }
        public XPathNavigator CreateNavigator(bool standalone, bool useGMTDate)
        {
            NewsItemSerializationFormat format = (standalone
                                                      ? NewsItemSerializationFormat.RssItem
                                                      : NewsItemSerializationFormat.RssFeed);
            XPathDocument doc =
                new XPathDocument(new XmlTextReader(new StringReader(ToString(format, useGMTDate))), XmlSpace.Preserve);
            return doc.CreateNavigator();
        }
        public override int GetHashCode()
        {
            return Id.GetHashCode();
        }
        public override bool Equals(object obj)
        {
            return Equals(obj as NewsItem);
        }
        public bool Equals(INewsItem other)
        {
            return Equals(other as NewsItem);
        }
        public bool Equals(NewsItem other)
        {
            if (ReferenceEquals(this, other))
            {
                return true;
            }
            if (ReferenceEquals(other, null))
                return false;
            if (Id.Equals(other.Id))
            {
                return true;
            }
            return false;
        }
        private string ToNntpMessage()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("From: ");
            sb.Append(p_author);
            try
            {
                Uri newsgroupUri = new Uri(FeedDetails.Link);
                sb.Append("\r\nNewsgroups: ");
                sb.Append(newsgroupUri.AbsolutePath.Substring(1));
            }
            catch (UriFormatException)
            {
            }
            sb.Append("\r\nX-Newsreader: ");
            sb.Append(FeedSource.GlobalUserAgentString);
            sb.Append("\r\nSubject: ");
            sb.Append(p_title);
            if ((p_parentId != null) && (p_parentId.Length != 0))
            {
                sb.Append("\r\nReferences: ");
                sb.Append(p_parentId);
            }
            sb.Append("\r\n\r\n");
            sb.Append(Content);
            sb.Append("\r\n.\r\n");
            return sb.ToString();
        }
        public int GetSize()
        {
            int l = StringHelper.SizeOfStr(p_id);
            l += StringHelper.SizeOfStr(Link);
            if (HasContent)
                l += p_content.Length;
            l += StringHelper.SizeOfStr(subject);
            l += StringHelper.SizeOfStr(commentUrl);
            return l;
        }
        public string GetSizeDetails()
        {
            return GetSize().ToString();
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
    }
    public class SearchHitNewsItem : NewsItem
    {
        public SearchHitNewsItem(INewsFeed feed, string title, string link, string summary, string author, DateTime date,
                                 string id)
            :
                base(feed, title, link, null, author, date, id, null)
        {
            Enclosures = GetList<IEnclosure>.Empty;
            Summary = summary;
        }
        public SearchHitNewsItem(INewsItem item)
            :
                this(item.Feed, item.Title, item.Link, item.Content, item.Author, item.Date, item.Id)
        {
        }
        private string summary = null;
        public string Summary
        {
            get
            {
                return summary;
            }
            set
            {
                summary = value;
                try
                {
                    XmlQualifiedName qname = new XmlQualifiedName("summary", "http://www.w3.org/2005/Atom");
                    using (StringWriter sw = new StringWriter())
                    {
                        XmlTextWriter xtw = new XmlTextWriter(sw);
                        xtw.WriteElementString(qname.Name, qname.Namespace, value);
                        xtw.Close();
                        OptionalElements.Remove(qname);
                        OptionalElements.Add(qname, sw.ToString());
                    }
                }
                catch (Exception)
                {
                }
            }
        }
    }
    public class ExceptionalNewsItem : SearchHitNewsItem
    {
        public ExceptionalNewsItem(INewsFeed feed, string title, string link, string summary, string author,
                                   DateTime date, string id)
            :
                base(feed, title, link, summary, author, date, id)
        {
            SetContent(summary, ContentType.Html);
        }
    }
    internal class RankedNewsItem
    {
        private readonly INewsItem item;
        private readonly float score;
        internal RankedNewsItem(INewsItem item, float score)
        {
            this.score = score;
            this.item = item;
        }
        internal float Score
        {
            get
            {
                return score;
            }
        }
        internal INewsItem Item
        {
            get
            {
                return item;
            }
        }
    }
}
