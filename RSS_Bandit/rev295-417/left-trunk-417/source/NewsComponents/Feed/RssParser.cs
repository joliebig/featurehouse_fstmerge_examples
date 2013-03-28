using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Runtime.Serialization;
using System.Text;
using System.Xml;
using log4net;
using NewsComponents.Collections;
using NewsComponents.Net;
using NewsComponents.Resources;
using NewsComponents.Utils;
using RssBandit.Common.Logging;
namespace NewsComponents.Feed
{
    public enum SyndicationFormat
    {
        Rss,
        Rdf,
        Atom,
        Unknown
    }
    public class RssParser
    {
        private static readonly ILog _log = Log.GetLogger(typeof (RssParser));
        private bool offline = false;
        public bool Offline
        {
            set
            {
                offline = value;
            }
            get
            {
                return offline;
            }
        }
        private const int nt_ispermalink = 0;
        private const int nt_description = 1;
        private const int nt_body = 2;
        private const int nt_encoded = 3;
        private const int nt_guid = 4;
        private const int nt_link = 5;
        private const int nt_title = 6;
        private const int nt_pubdate = 7;
        private const int nt_date = 8;
        private const int nt_category = 9;
        private const int nt_subject = 10;
        private const int nt_comments = 11;
        private const int nt_flagstatus = 12;
        private const int nt_content = 13;
        private const int nt_summary = 14;
        private const int nt_rel = 15;
        private const int nt_href = 16;
        private const int nt_modified = 17;
        private const int nt_issued = 18;
        private const int nt_type = 19;
        private const int nt_rss = 20;
        private const int nt_rdf = 21;
        private const int nt_feed = 22;
        private const int nt_channel = 23;
        private const int nt_lastbuilddate = 24;
        private const int nt_image = 25;
        private const int nt_item = 26;
        private const int nt_items = 27;
        private const int nt_maxitemage = 28;
        private const int nt_tagline = 29;
        private const int nt_entry = 30;
        private const int nt_id = 31;
        private const int nt_author = 32;
        private const int nt_creator = 33;
        private const int nt_name = 34;
        private const int nt_reference = 35;
        private const int nt_ns_dc = 36;
        private const int nt_ns_xhtml = 37;
        private const int nt_ns_content = 38;
        private const int nt_ns_annotate = 39;
        private const int nt_ns_bandit_2003 = 40;
        private const int nt_ns_wfw = 41;
        private const int nt_comment = 42;
        private const int nt_commentRSS = 43;
        private const int nt_commentRss = 44;
        private const int nt_ns_slash = 45;
        private const int nt_enclosure = 46;
        private const int nt_updated = 47;
        private const int nt_published = 48;
        private const int nt_ns_fd = 49;
        private const int nt_inreplyto = 50;
        private const int nt_watchcomments = 51;
        private const int nt_language = 52;
        private const int nt_ns_thr = 53;
        private const int nt_hasnewcomments = 54;
        private const int nt_ns_mediarss = 55;
        private const int nt_ns_itunes = 56;
        private const int nt_created = 57;
        private const int nt_outgoinglinks = 58;
        private const int nt_duration = 59;
        private const int NT_SIZE = 1 + nt_duration;
        public static bool CanProcessUrl(string url)
        {
            if (string.IsNullOrEmpty(url))
                return false;
            if (url.StartsWith("nntp") || url.StartsWith("news") || url.StartsWith("http") || url.StartsWith("file") ||
                File.Exists(url))
                return true;
            return false;
        }
        private readonly FeedSource owner;
        public RssParser(FeedSource owner)
        {
            if (owner == null)
                throw new ArgumentNullException("owner");
            this.owner = owner;
        }
        public static NewsItem MakeRssItem(INewsFeed f, XmlReader reader)
        {
            object[] atomized_strings = FillNameTable(reader.NameTable);
            if (reader.ReadState == ReadState.Initial)
            {
                reader.Read();
            }
            return MakeRssItem(f, reader, atomized_strings, DateTime.Now.ToUniversalTime());
        }
        public static NewsItem MakeRssItem(INewsFeed f, XmlReader reader, object[] atomized_strings,
                                           DateTime defaultItemDate)
        {
            ContentType ctype = ContentType.None;
            string description = null;
            string id = null;
            string parentId = null;
            string baseUrl = reader.BaseURI;
            string link = null;
            string title = null;
            string subject = null;
            string author = null;
            string commentUrl = null;
            string commentRssUrl = null;
            Enclosure enc = null;
            TimeSpan encDuration = TimeSpan.MinValue;
            int commentCount = NewsItem.NoComments;
            DateTime date = defaultItemDate;
            DateTime now = date;
            Hashtable optionalElements = new Hashtable();
            Flagged flagged = Flagged.None;
            bool watchComments = false, hasNewComments = false;
            ArrayList subjects = new ArrayList();
            List<IEnclosure> enclosures = null;
            List<string> outgoingLinks = null;
            string itemNamespaceUri = reader.NamespaceURI;
            bool nodeRead = false;
            while ((nodeRead || reader.Read()) && reader.NodeType != XmlNodeType.EndElement)
            {
                nodeRead = false;
                object localname = reader.LocalName;
                object namespaceuri = reader.NamespaceURI;
                if (reader.NodeType != XmlNodeType.Element)
                {
                    continue;
                }
                bool nodeNamespaceUriEqual2Item = reader.NamespaceURI.Equals(itemNamespaceUri);
                bool nodeNamespaceUriEqual2DC = (namespaceuri == atomized_strings[nt_ns_dc]);
                if (namespaceuri == atomized_strings[nt_ns_fd])
                {
                    reader.Skip();
                    nodeRead = true;
                    continue;
                }
                if ((description == null) || (localname == atomized_strings[nt_body]) ||
                    (localname == atomized_strings[nt_encoded]))
                {
                    if ((namespaceuri == atomized_strings[nt_ns_xhtml])
                        && (localname == atomized_strings[nt_body]))
                    {
                        if (!reader.IsEmptyElement)
                        {
                            baseUrl = reader.BaseURI;
                            XmlElement elem = RssHelper.CreateXmlElement(reader);
                            nodeRead = true;
                            description = elem.InnerXml;
                        }
                        ctype = ContentType.Xhtml;
                        continue;
                    }
                    else if ((namespaceuri == atomized_strings[nt_ns_content])
                             && (localname == atomized_strings[nt_encoded]))
                    {
                        if (!reader.IsEmptyElement)
                        {
                            baseUrl = reader.BaseURI;
                            description = ReadElementString(reader);
                        }
                        ctype = ContentType.Html;
                        continue;
                    }
                    else if ((nodeNamespaceUriEqual2Item || nodeNamespaceUriEqual2DC)
                             && (localname == atomized_strings[nt_description]))
                    {
                        if (!reader.IsEmptyElement)
                        {
                            baseUrl = reader.BaseURI;
                            description = ReadElementString(reader);
                        }
                        ctype = ContentType.Text;
                        continue;
                    }
                }
                if (link != null && link.Trim().Length == 0)
                    link = null;
                if ((link == null) || (localname == atomized_strings[nt_guid]))
                {
                    if (nodeNamespaceUriEqual2Item
                        && (localname == atomized_strings[nt_guid]))
                    {
                        if ((reader["isPermaLink"] == null) ||
                            (StringHelper.AreEqualCaseInsensitive(reader["isPermaLink"], "true")))
                        {
                            if (!reader.IsEmptyElement)
                            {
                                link = ReadElementUrl(reader);
                            }
                        }
                        else if (StringHelper.AreEqualCaseInsensitive(reader["isPermaLink"], "false"))
                        {
                            if (!reader.IsEmptyElement)
                            {
                                id = ReadElementString(reader);
                            }
                        }
                        continue;
                    }
                    else if (nodeNamespaceUriEqual2Item
                             && (localname == atomized_strings[nt_link]))
                    {
                        if (!reader.IsEmptyElement)
                        {
                            link = ReadElementUrl(reader);
                        }
                        continue;
                    }
                }
                if (title == null)
                {
                    if (nodeNamespaceUriEqual2Item
                        && (localname == atomized_strings[nt_title]))
                    {
                        if (!reader.IsEmptyElement)
                        {
                            title = ReadElementString(reader);
                        }
                        continue;
                    }
                }
                if ((author == null) || (localname == atomized_strings[nt_creator]))
                {
                    if (nodeNamespaceUriEqual2DC &&
                        (localname == atomized_strings[nt_creator] ||
                         localname == atomized_strings[nt_author]))
                    {
                        if (!reader.IsEmptyElement)
                        {
                            author = ReadElementString(reader);
                        }
                        continue;
                    }
                    else if (nodeNamespaceUriEqual2Item && (localname == atomized_strings[nt_author]))
                    {
                        if (!reader.IsEmptyElement)
                        {
                            author = ReadElementString(reader);
                        }
                        continue;
                    }
                }
                if ((parentId == null) && (localname == atomized_strings[nt_reference]))
                {
                    if (namespaceuri == atomized_strings[nt_ns_annotate])
                    {
                        parentId = reader.GetAttribute("resource", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
                    }
                    continue;
                }
                if (nodeNamespaceUriEqual2DC && (localname == atomized_strings[nt_subject]))
                {
                    if (!reader.IsEmptyElement)
                    {
                        subjects.Add(ReadElementString(reader));
                    }
                    continue;
                }
                else if (nodeNamespaceUriEqual2Item
                         && (localname == atomized_strings[nt_category]))
                {
                    if (!reader.IsEmptyElement)
                    {
                        subjects.Add(ReadElementString(reader));
                    }
                    continue;
                }
                if ((localname == atomized_strings[nt_flagstatus])
                    && (namespaceuri == atomized_strings[nt_ns_bandit_2003]))
                {
                    if (!reader.IsEmptyElement)
                    {
                        flagged = (Flagged) Enum.Parse(flagged.GetType(), ReadElementString(reader));
                    }
                    continue;
                }
                if ((localname == atomized_strings[nt_duration])
                    && (namespaceuri == atomized_strings[nt_ns_itunes]))
                {
                    if (!reader.IsEmptyElement)
                    {
                        try
                        {
                            string durationStr = ReadElementString(reader);
                            if (durationStr.IndexOf(":") == -1)
                            {
                                durationStr = "00:00:" + durationStr;
                            }
                            TimeSpan t;
                            if (TimeSpan.TryParse(durationStr, out t))
                                encDuration = t;
                            if (enc != null)
                            {
                                enc.Duration = encDuration;
                            }
                        }
                        catch (Exception)
                        {
                        }
                    }
                    continue;
                }
                if ((namespaceuri == atomized_strings[nt_ns_mediarss])
                    && (localname == atomized_strings[nt_content]))
                {
                    try
                    {
                        if (reader["duration"] != null)
                        {
                            TimeSpan t;
                            if (TimeSpan.TryParse("00:00:" + reader["duration"], out t))
                                encDuration = t;
                        }
                        if (enc != null)
                        {
                            enc.Duration = encDuration;
                        }
                    }
                    catch
                    {
                    }
                    if (!reader.IsEmptyElement)
                    {
                        reader.Skip();
                        nodeRead = true;
                    }
                    continue;
                }
                if ((localname == atomized_strings[nt_watchcomments])
                    && (namespaceuri == atomized_strings[nt_ns_bandit_2003]))
                {
                    if (!reader.IsEmptyElement && ReadElementString(reader).Equals("1"))
                    {
                        watchComments = true;
                    }
                    continue;
                }
                if ((localname == atomized_strings[nt_hasnewcomments])
                    && (namespaceuri == atomized_strings[nt_ns_bandit_2003]))
                {
                    if (!reader.IsEmptyElement && ReadElementString(reader).Equals("1"))
                    {
                        hasNewComments = true;
                    }
                    continue;
                }
                if ((localname == atomized_strings[nt_outgoinglinks])
                    && (namespaceuri == atomized_strings[nt_ns_bandit_2003]))
                {
                    outgoingLinks = (outgoingLinks ?? new List<string>());
                    if (!reader.IsEmptyElement)
                    {
                        reader.Read();
                        do
                        {
                            string hrefOut =
                                RelationCosmos.RelationCosmos.UrlTable.Add(reader.ReadElementContentAsString());
                            outgoingLinks.Add(hrefOut);
                        } while (reader.NodeType != XmlNodeType.EndElement);
                    }
                    continue;
                }
                if (nodeNamespaceUriEqual2Item
                    && (localname == atomized_strings[nt_enclosure]))
                {
                    string url = reader["url"];
                    string type = reader["type"];
                    long length = Int64.MinValue;
                    bool downloaded = false;
                    try
                    {
                        if (reader["duration"] != null)
                        {
                            TimeSpan t;
                            if (TimeSpan.TryParse(reader["duration"], out t))
                                encDuration = t;
                        }
                        if (reader["length"] != null)
                            length = Int64.Parse(reader["length"]);
                    }
                    catch
                    {
                    }
                    try
                    {
                        downloaded = (reader["downloaded"] == null ? false : reader["downloaded"].Equals("1"));
                    }
                    catch
                    {
                    }
                    enclosures = (enclosures ?? new List<IEnclosure>());
                    enc = new Enclosure(type, length, url, String.Empty);
                    enc.Downloaded = downloaded;
                    if (encDuration != TimeSpan.MinValue)
                    {
                        enc.Duration = encDuration;
                    }
                    enclosures.Add(enc);
                    if (!reader.IsEmptyElement)
                    {
                        reader.Skip();
                        nodeRead = true;
                    }
                    continue;
                }
                if ((localname == atomized_strings[nt_comment])
                    && (namespaceuri == atomized_strings[nt_ns_wfw]))
                {
                    if (!reader.IsEmptyElement)
                    {
                        commentUrl = ReadElementUrl(reader);
                    }
                    continue;
                }
                if ((localname == atomized_strings[nt_commentRss] || localname == atomized_strings[nt_commentRSS])
                    && (namespaceuri == atomized_strings[nt_ns_wfw]))
                {
                    if (!reader.IsEmptyElement)
                    {
                        commentRssUrl = ReadElementUrl(reader);
                    }
                    continue;
                }
                if (commentCount == NewsItem.NoComments)
                {
                    if ((localname == atomized_strings[nt_comments])
                        && (namespaceuri == atomized_strings[nt_ns_slash]))
                    {
                        try
                        {
                            if (!reader.IsEmptyElement)
                            {
                                commentCount = Int32.Parse(ReadElementString(reader));
                            }
                        }
                        catch (Exception)
                        {
                        }
                        continue;
                    }
                }
                if (date == now)
                {
                    try
                    {
                        if (nodeNamespaceUriEqual2Item
                            && (localname == atomized_strings[nt_pubdate]))
                        {
                            if (!reader.IsEmptyElement)
                            {
                                date = DateTimeExt.Parse(ReadElementString(reader));
                            }
                            continue;
                        }
                        else if (nodeNamespaceUriEqual2DC && (localname == atomized_strings[nt_date]))
                        {
                            if (!reader.IsEmptyElement)
                            {
                                date = DateTimeExt.ToDateTime(ReadElementString(reader));
                            }
                            continue;
                        }
                    }
                    catch (FormatException fe)
                    {
                        _log.Warn("Error parsing date from item {" + subject +
                                  "} from feed {" + link + "}: " + fe.Message);
                        continue;
                    }
                }
                XmlQualifiedName qname = new XmlQualifiedName(reader.LocalName, reader.NamespaceURI);
                string optionalNode = reader.ReadOuterXml();
                nodeRead = true;
                if (!optionalElements.Contains(qname))
                {
                    optionalElements.Add(qname, optionalNode);
                }
            }
            if (link == null && id == null && title == null && date == now)
            {
                return null;
            }
            for (int i = 0; i < subjects.Count; i++)
            {
                subject += (i > 0 ? " | " + subjects[i] : subjects[i]);
            }
            id = (id ?? link);
            NewsItem newsItem =
                new NewsItem(f, title, link, description, date, subject, ctype, optionalElements, id, parentId, baseUrl,
                             outgoingLinks);
            newsItem.FlagStatus = flagged;
            newsItem.CommentCount = commentCount;
            newsItem.Author = author;
            newsItem.CommentRssUrl = commentRssUrl;
            newsItem.CommentUrl = commentUrl;
            newsItem.CommentStyle = (commentUrl == null ? SupportedCommentStyle.None : SupportedCommentStyle.CommentAPI);
            newsItem.Enclosures = (enclosures ?? GetList<IEnclosure>.Empty);
            newsItem.WatchComments = watchComments;
            newsItem.Language = reader.XmlLang;
            newsItem.HasNewComments = hasNewComments;
            return newsItem;
        }
        public static NewsItem MakeAtomItem(INewsFeed f, XmlReader reader)
        {
            object[] atomized_strings = FillNameTable(reader.NameTable);
            if (reader.ReadState == ReadState.Initial)
            {
                reader.Read();
            }
            return MakeAtomItem(f, reader, atomized_strings, DateTime.Now.ToUniversalTime());
        }
        private static string ResolveRelativeUrl(XmlReader reader, string url)
        {
            XmlBaseAwareXmlValidatingReader xmlBaseReader = reader as XmlBaseAwareXmlValidatingReader;
            try
            {
                if (xmlBaseReader != null)
                {
                    url = new Uri(xmlBaseReader.BaseURIasUri, url).AbsoluteUri;
                }
            }
            catch (Exception)
            {
                ;
            }
            return url;
        }
        public static NewsItem MakeAtomItem(INewsFeed f, XmlReader reader, object[] atomized_strings,
                                            DateTime defaultItemDate)
        {
            ContentType ctype = ContentType.None;
            string description = null;
            string author = null;
            string baseUrl = reader.BaseURI;
            string id = null;
            string parentId = null;
            string link = null;
            string title = null;
            string subject = null;
            string commentUrl = null;
            string commentRssUrl = null;
            Enclosure enc = null;
            TimeSpan encDuration = TimeSpan.MinValue;
            int commentCount = NewsItem.NoComments;
            DateTime date = defaultItemDate;
            DateTime now = date;
            Hashtable optionalElements = new Hashtable();
            Flagged flagged = Flagged.None;
            ArrayList subjects = new ArrayList();
            List<IEnclosure> enclosures = null;
            string itemNamespaceUri = reader.NamespaceURI;
            bool nodeRead = false;
            while ((nodeRead || reader.Read()) && reader.NodeType != XmlNodeType.EndElement)
            {
                nodeRead = false;
                object localname = reader.LocalName;
                object namespaceuri = reader.NamespaceURI;
                if (reader.NodeType != XmlNodeType.Element)
                {
                    continue;
                }
                bool nodeNamespaceUriEqual2Item = itemNamespaceUri.Equals(reader.NamespaceURI);
                bool nodeNamespaceUriEqual2DC = (namespaceuri == atomized_strings[nt_ns_dc]);
                if (namespaceuri == atomized_strings[nt_ns_fd])
                {
                    reader.Skip();
                    nodeRead = true;
                    continue;
                }
                if ((description == null) || ((localname == atomized_strings[nt_content]) && (reader["src"] == null)))
                {
                    if (nodeNamespaceUriEqual2Item
                        && (localname == atomized_strings[nt_content]))
                    {
                        ctype = GetMimeTypeOfAtomElement(reader);
                        if ((ctype != ContentType.Unknown) && (!reader.IsEmptyElement))
                        {
                            baseUrl = reader.BaseURI;
                            description = GetContentFromAtomElement(reader, ref nodeRead);
                        }
                        continue;
                    }
                    else if (nodeNamespaceUriEqual2Item
                             && (localname == atomized_strings[nt_summary]))
                    {
                        ctype = GetMimeTypeOfAtomElement(reader);
                        if ((ctype != ContentType.Unknown) && (!reader.IsEmptyElement))
                        {
                            baseUrl = reader.BaseURI;
                            description = GetContentFromAtomElement(reader, ref nodeRead);
                        }
                        continue;
                    }
                }
                if (link != null && link.Trim().Length == 0)
                    link = null;
                if (link == null)
                {
                    if (nodeNamespaceUriEqual2Item
                        && (localname == atomized_strings[nt_link]) &&
                        ((reader["rel"] == null) ||
                         (reader["rel"].Equals("alternate") )))
                    {
                        if (reader["href"] != null)
                        {
                            link = reader.GetAttribute("href");
                        }
                        link = ResolveRelativeUrl(reader, link);
                        if (!reader.IsEmptyElement)
                        {
                            reader.Skip();
                            nodeRead = true;
                        }
                        continue;
                    }
                }
                if (nodeNamespaceUriEqual2Item
                    && ((localname == atomized_strings[nt_link]) &&
                        reader["rel"].Equals("enclosure")))
                {
                    string url = ResolveRelativeUrl(reader, reader["href"]);
                    string type = reader["type"];
                    long length = Int64.MinValue;
                    try
                    {
                        length = Int64.Parse(reader["length"]);
                    }
                    catch
                    {
                    }
                    if (!string.IsNullOrEmpty(url))
                    {
                        enclosures = (enclosures ?? new List<IEnclosure>());
                        enc = new Enclosure(type, length, url, String.Empty);
                        if (encDuration != TimeSpan.MinValue)
                        {
                            enc.Duration = encDuration;
                        }
                        enclosures.Add(enc);
                    }
                    if (!reader.IsEmptyElement)
                    {
                        reader.Skip();
                        nodeRead = true;
                    }
                    continue;
                }
                if (nodeNamespaceUriEqual2Item
                    && (localname == atomized_strings[nt_content] &&
                        reader["src"] != null))
                {
                    string url = ResolveRelativeUrl(reader, reader["src"]);
                    string type = reader["type"];
                    long length = Int64.MinValue;
                    if (!string.IsNullOrEmpty(url))
                    {
                        enclosures = (enclosures ?? new List<IEnclosure>());
                        enclosures.Add(new Enclosure(type, length, url, String.Empty));
                    }
                    if (!reader.IsEmptyElement)
                    {
                        reader.Skip();
                        nodeRead = true;
                    }
                    continue;
                }
                if (title == null)
                {
                    if (nodeNamespaceUriEqual2Item
                        && (localname == atomized_strings[nt_title]))
                    {
                        if (!reader.IsEmptyElement)
                        {
                            ctype = GetMimeTypeOfAtomElement(reader);
                            if ((ctype != ContentType.Unknown))
                            {
                                title = GetContentFromAtomElement(reader, ref nodeRead);
                            }
                        }
                        continue;
                    }
                }
                if (id == null)
                {
                    if (nodeNamespaceUriEqual2Item
                        && (localname == atomized_strings[nt_id]))
                    {
                        if (!reader.IsEmptyElement)
                        {
                            id = ReadElementString(reader);
                        }
                        continue;
                    }
                }
                if (author == null)
                {
                    if (nodeNamespaceUriEqual2Item && (localname == atomized_strings[nt_author]))
                    {
                        if (!reader.IsEmptyElement)
                        {
                            while (reader.Read() && reader.NodeType != XmlNodeType.EndElement)
                            {
                                if (reader.NodeType != XmlNodeType.Element)
                                {
                                    continue;
                                }
                                localname = reader.LocalName;
                                if (localname == atomized_strings[nt_name])
                                {
                                    if (!reader.IsEmptyElement)
                                    {
                                        author = ReadElementString(reader);
                                    }
                                }
                                else
                                {
                                    if (!reader.IsEmptyElement)
                                    {
                                        ReadElementString(reader);
                                    }
                                }
                            }
                        }
                        continue;
                    }
                }
                if ((parentId == null) && (localname == atomized_strings[nt_inreplyto]))
                {
                    if (namespaceuri == atomized_strings[nt_ns_thr])
                    {
                        parentId = reader.GetAttribute("ref");
                    }
                    continue;
                }
                if (nodeNamespaceUriEqual2DC
                    && (localname == atomized_strings[nt_subject]))
                {
                    if (!reader.IsEmptyElement)
                    {
                        subjects.Add(ReadElementString(reader));
                    }
                    continue;
                }
                else if (nodeNamespaceUriEqual2Item
                         && (localname == atomized_strings[nt_category]))
                {
                    if (reader["label"] != null)
                    {
                        subjects.Add(reader.GetAttribute("label"));
                    }
                    else if (reader["term"] != null)
                    {
                        subjects.Add(reader.GetAttribute("term"));
                    }
                    if (!reader.IsEmptyElement)
                    {
                        reader.Skip();
                        nodeRead = true;
                    }
                    continue;
                }
                if ((localname == atomized_strings[nt_duration])
                    && (namespaceuri == atomized_strings[nt_ns_itunes]))
                {
                    if (!reader.IsEmptyElement)
                    {
                        try
                        {
                            string durationStr = ReadElementString(reader);
                            if (durationStr.IndexOf(":") == -1)
                            {
                                durationStr = "00:00:" + durationStr;
                            }
                            encDuration = TimeSpan.Parse(durationStr);
                            if (enc != null)
                            {
                                enc.Duration = encDuration;
                            }
                        }
                        catch (Exception)
                        {
                        }
                    }
                    continue;
                }
                if ((namespaceuri == atomized_strings[nt_ns_mediarss])
                    && (localname == atomized_strings[nt_content]))
                {
                    try
                    {
                        if (reader["duration"] != null)
                            encDuration = TimeSpan.Parse("00:00:" + reader["duration"]);
                        if (enc != null)
                        {
                            enc.Duration = encDuration;
                        }
                    }
                    catch
                    {
                    }
                    if (!reader.IsEmptyElement)
                    {
                        reader.Skip();
                        nodeRead = true;
                    }
                    continue;
                }
                if ((namespaceuri == atomized_strings[nt_ns_bandit_2003])
                    && (localname == atomized_strings[nt_flagstatus]))
                {
                    if (!reader.IsEmptyElement)
                    {
                        flagged = (Flagged) Enum.Parse(flagged.GetType(), ReadElementString(reader));
                    }
                    continue;
                }
                if ((localname == atomized_strings[nt_comment])
                    && (namespaceuri == atomized_strings[nt_ns_wfw]))
                {
                    if (!reader.IsEmptyElement)
                    {
                        commentUrl = ReadElementUrl(reader);
                    }
                    continue;
                }
                if ((localname == atomized_strings[nt_commentRss] || localname == atomized_strings[nt_commentRSS])
                    && (namespaceuri == atomized_strings[nt_ns_wfw]))
                {
                    if (!reader.IsEmptyElement)
                    {
                        commentRssUrl = ReadElementUrl(reader);
                    }
                    continue;
                }
                if (commentCount == NewsItem.NoComments)
                {
                    if (nodeNamespaceUriEqual2Item
                        && (localname == atomized_strings[nt_link])
                        && !string.IsNullOrEmpty(reader["rel"])
                        && reader["rel"].Equals("replies"))
                    {
                        try
                        {
                            string thrNs = (string) atomized_strings[nt_ns_thr];
                            if (!string.IsNullOrEmpty(reader["count", thrNs]))
                                commentCount = Int32.Parse(reader["count", thrNs]);
                        }
                        catch (Exception)
                        {
                        }
                        if (reader["href"] != null)
                        {
                            if (string.IsNullOrEmpty(reader["type"]) || reader["type"].Equals("application/atom+xml"))
                            {
                                commentRssUrl = ResolveRelativeUrl(reader, reader.GetAttribute("href"));
                            }
                            else
                            {
                                commentUrl = ResolveRelativeUrl(reader, reader.GetAttribute("href"));
                            }
                        }
                        if (!reader.IsEmptyElement)
                        {
                            reader.Skip();
                            nodeRead = true;
                        }
                        continue;
                    }
                    else if ((namespaceuri == atomized_strings[nt_ns_slash])
                             && (localname == atomized_strings[nt_comments]))
                    {
                        try
                        {
                            if (!reader.IsEmptyElement)
                            {
                                commentCount = Int32.Parse(ReadElementString(reader));
                            }
                        }
                        catch (Exception)
                        {
                        }
                        continue;
                    }
                }
                if ((date == now) || (localname == atomized_strings[nt_modified] ||
                                      localname == atomized_strings[nt_updated]))
                {
                    try
                    {
                        if (nodeNamespaceUriEqual2Item
                            && (localname == atomized_strings[nt_modified] ||
                                localname == atomized_strings[nt_updated]))
                        {
                            if (!reader.IsEmptyElement)
                            {
                                date = DateTimeExt.ToDateTime(ReadElementString(reader));
                            }
                            continue;
                        }
                        else if (nodeNamespaceUriEqual2Item
                                 && (localname == atomized_strings[nt_issued] ||
                                     localname == atomized_strings[nt_published] ||
                                     localname == atomized_strings[nt_created]))
                        {
                            if (!reader.IsEmptyElement)
                            {
                                date = DateTimeExt.ToDateTime(ReadElementString(reader));
                            }
                            continue;
                        }
                    }
                    catch (FormatException fe)
                    {
                        _log.Warn("Error parsing date from item {" + subject +
                                  "} from feed {" + link + "}: " + fe.Message);
                        continue;
                    }
                }
                XmlQualifiedName qname = new XmlQualifiedName(reader.LocalName, reader.NamespaceURI);
                string optionalNode = reader.ReadOuterXml();
                nodeRead = true;
                if (!optionalElements.Contains(qname))
                {
                    optionalElements.Add(qname, optionalNode);
                }
            }
            if (link == null && id == null && title == null && date == now)
            {
                return null;
            }
            for (int i = 0; i < subjects.Count; i++)
            {
                subject += (i > 0 ? " | " + subjects[i] : subjects[i]);
            }
            id = (id ?? link);
            NewsItem newsItem =
                new NewsItem(f, title, link, description, date, subject, ctype, optionalElements, id, parentId, baseUrl);
            newsItem.FlagStatus = flagged;
            newsItem.CommentCount = commentCount;
            newsItem.Author = author;
            newsItem.CommentRssUrl = commentRssUrl;
            newsItem.CommentUrl = commentUrl;
            newsItem.CommentStyle = (commentUrl == null ? SupportedCommentStyle.None : SupportedCommentStyle.CommentAPI);
            newsItem.Enclosures = (enclosures ?? GetList<IEnclosure>.Empty);
            newsItem.Language = reader.XmlLang;
            return newsItem;
        }
        public static List<INewsItem> DownloadItemsFromFeed(NewsFeed f, IWebProxy proxy, bool offline)
        {
            List<INewsItem> returnList = GetList<INewsItem>.Empty;
            if (offline)
                return returnList;
            ICredentials c = FeedSource.CreateCredentialsFrom(f);
            using (Stream mem = AsyncWebRequest.GetSyncResponseStream(f.link, c, FeedSource.DefaultUserAgent, proxy))
            {
                if (RssParser.CanProcessUrl(f.link))
                {
                    returnList = RssParser.GetItemsForFeed(f, mem, false).itemsList;
                }
            }
            return returnList;
        }
        public static List<INewsItem> DownloadItemsFromFeed(string feedUrl)
        {
            NewsFeed f = new NewsFeed();
            f.link = feedUrl;
            return DownloadItemsFromFeed(f, HttpWebRequest.DefaultWebProxy, false);
        }
        public List<INewsItem> GetItemsForFeed(INewsFeed f)
        {
            if (offline)
                return new List<INewsItem>();
            List<INewsItem> returnList;
            using (Stream mem = AsyncWebRequest.GetSyncResponseStream(f.link, null, owner.UserAgent, owner.Proxy))
            {
                returnList = GetItemsForFeed(f, mem, false).itemsList;
            }
            return returnList;
        }
        public List<INewsItem> GetItemsForFeed(string feedUrl)
        {
            NewsFeed f = new NewsFeed();
            f.link = feedUrl;
            return GetItemsForFeed(f);
        }
        public static FeedInfo GetItemsForFeed(INewsFeed f, XmlReader feedReader, bool cachedStream)
        {
            List<INewsItem> items = new List<INewsItem>();
            Dictionary<XmlQualifiedName, string> optionalElements = new Dictionary<XmlQualifiedName, string>();
            string feedLink = String.Empty,
                   feedDescription = String.Empty,
                   feedTitle = String.Empty,
                   maxItemAge = String.Empty,
                   language = String.Empty;
            DateTime defaultItemDate = RelationCosmos.RelationCosmos.UnknownPointInTime;
            DateTime channelBuildDate = DateTime.Now.ToUniversalTime();
            bool newComments = false;
            int readItems = 0;
            object[] atomized_strings = FillNameTable(feedReader.NameTable);
            feedReader.MoveToContent();
            object localname = feedReader.LocalName;
            try
            {
                SyndicationFormat feedFormat;
                string rssNamespaceUri;
                if ((localname == atomized_strings[nt_rdf]) &&
                    feedReader.NamespaceURI.Equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#"))
                {
                    feedReader.Read();
                    feedReader.MoveToContent();
                    feedFormat = SyndicationFormat.Rdf;
                    localname = feedReader.LocalName;
                    if (localname == atomized_strings[nt_channel])
                    {
                        rssNamespaceUri = feedReader.NamespaceURI;
                    }
                    else
                    {
                        rssNamespaceUri = "http://purl.org/rss/1.0/";
                    }
                }
                else if (localname == atomized_strings[nt_rss])
                {
                    feedFormat = SyndicationFormat.Rss;
                    rssNamespaceUri = feedReader.NamespaceURI;
                    do
                    {
                        feedReader.Read();
                        feedReader.MoveToContent();
                        localname = feedReader.LocalName;
                    } while (localname != atomized_strings[nt_channel]
                             && localname != atomized_strings[nt_rss]);
                }
                else if (feedReader.NamespaceURI.Equals("http://purl.org/atom/ns#")
                         && (localname == atomized_strings[nt_feed]))
                {
                    rssNamespaceUri = feedReader.NamespaceURI;
                    if (feedReader.MoveToAttribute("version") && feedReader.Value.Equals("0.3"))
                    {
                        feedFormat = SyndicationFormat.Atom;
                        feedReader.MoveToElement();
                    }
                    else
                    {
                        throw new RssParserException(ComponentsText.ExceptionUnsupportedAtomVersion);
                    }
                }
                else if (feedReader.NamespaceURI.Equals("http://www.w3.org/2005/Atom")
                         && (localname == atomized_strings[nt_feed]))
                {
                    rssNamespaceUri = feedReader.NamespaceURI;
                    feedFormat = SyndicationFormat.Atom;
                }
                else
                {
                    throw new RssParserException(ComponentsText.ExceptionUnknownXmlDialect);
                }
                ProcessFeedElements(f, feedReader, atomized_strings, rssNamespaceUri, feedFormat, ref feedLink,
                                    ref feedTitle, ref feedDescription, ref channelBuildDate,
                                    optionalElements, items, defaultItemDate, ref language);
            }
            finally
            {
                feedReader.Close();
            }
            if (string.IsNullOrEmpty(feedLink))
            {
                feedLink = HtmlHelper.ConvertToAbsoluteUrlPath(f.link);
            }
            else if (-1 == feedLink.IndexOf("://"))
            {
                feedLink = HtmlHelper.ConvertToAbsoluteUrl(feedLink, f.link, false);
            }
            FeedInfo fi =
                new FeedInfo(f.id, f.cacheurl, items, feedTitle, feedLink, feedDescription, optionalElements, language);
            bool isNntpFeed = f.link.StartsWith("news") || f.link.StartsWith("nntp");
            for (int i = 0, count = items.Count; i < count; i++)
            {
                INewsItem ri = items[i];
                if ((f.storiesrecentlyviewed != null) && f.storiesrecentlyviewed.Contains(ri.Id))
                {
                    ri.BeenRead = true;
                    readItems++;
                }
                if (ri.HasNewComments)
                {
                    newComments = true;
                }
                ri.FeedDetails = fi;
                if (ri.Date == defaultItemDate)
                {
                    ri.Date = channelBuildDate;
                    channelBuildDate = channelBuildDate.AddSeconds(-1.0);
                }
                if (isNntpFeed)
                {
                    ri.CommentStyle = SupportedCommentStyle.NNTP;
                }
                if ((f.deletedstories.Contains(ri.Id) ||
                     null == FeedSource.ReceivingNewsChannelServices.ProcessItem(ri)))
                {
                    if (ri.BeenRead) readItems--;
                    ri.FeedDetails = null;
                    items.RemoveAt(i);
                    FeedSource.RelationCosmosRemove(ri);
                    i--;
                    count--;
                }
            }
            if (!string.IsNullOrEmpty(maxItemAge))
            {
                f.maxitemage = maxItemAge;
            }
            if (!cachedStream)
            {
                f.lastretrieved = new DateTime(DateTime.Now.Ticks);
                f.lastretrievedSpecified = true;
            }
            else
            {
                FeedSource.ReceivingNewsChannelServices.ProcessItem(fi);
                FeedSource.RelationCosmosAddRange(items);
            }
            if (readItems == items.Count)
            {
                f.containsNewMessages = false;
            }
            else
            {
                f.containsNewMessages = true;
            }
            f.containsNewComments = newComments;
            return fi;
        }
        private static object[] FillNameTable(XmlNameTable nt)
        {
            object[] atomized_names = new object[NT_SIZE];
            atomized_names[nt_author] = nt.Add("author");
            atomized_names[nt_body] = nt.Add("body");
            atomized_names[nt_category] = nt.Add("category");
            atomized_names[nt_channel] = nt.Add("channel");
            atomized_names[nt_comment] = nt.Add("comment");
            atomized_names[nt_commentRSS] = nt.Add("commentRSS");
            atomized_names[nt_commentRss] = nt.Add("commentRss");
            atomized_names[nt_comments] = nt.Add("comments");
            atomized_names[nt_content] = nt.Add("content");
            atomized_names[nt_created] = nt.Add("created");
            atomized_names[nt_creator] = nt.Add("creator");
            atomized_names[nt_date] = nt.Add("date");
            atomized_names[nt_description] = nt.Add("description");
            atomized_names[nt_duration] = nt.Add("duration");
            atomized_names[nt_enclosure] = nt.Add("enclosure");
            atomized_names[nt_encoded] = nt.Add("encoded");
            atomized_names[nt_entry] = nt.Add("entry");
            atomized_names[nt_flagstatus] = nt.Add("flag-status");
            atomized_names[nt_feed] = nt.Add("feed");
            atomized_names[nt_guid] = nt.Add("guid");
            atomized_names[nt_hasnewcomments] = nt.Add("has-new-comments");
            atomized_names[nt_href] = nt.Add("href");
            atomized_names[nt_id] = nt.Add("id");
            atomized_names[nt_image] = nt.Add("image");
            atomized_names[nt_ispermalink] = nt.Add("isPermaLink");
            atomized_names[nt_issued] = nt.Add("issued");
            atomized_names[nt_item] = nt.Add("item");
            atomized_names[nt_items] = nt.Add("items");
            atomized_names[nt_language] = nt.Add("language");
            atomized_names[nt_lastbuilddate] = nt.Add("lastBuildDate");
            atomized_names[nt_link] = nt.Add("link");
            atomized_names[nt_maxitemage] = nt.Add("maxItemAge");
            atomized_names[nt_modified] = nt.Add("modified");
            atomized_names[nt_name] = nt.Add("name");
            atomized_names[nt_outgoinglinks] = nt.Add("outgoing-links");
            atomized_names[nt_pubdate] = nt.Add("pubDate");
            atomized_names[nt_rdf] = nt.Add("RDF");
            atomized_names[nt_reference] = nt.Add("reference");
            atomized_names[nt_rel] = nt.Add("rel");
            atomized_names[nt_rss] = nt.Add("rss");
            atomized_names[nt_subject] = nt.Add("subject");
            atomized_names[nt_summary] = nt.Add("summary");
            atomized_names[nt_tagline] = nt.Add("tagline");
            atomized_names[nt_title] = nt.Add("title");
            atomized_names[nt_type] = nt.Add("type");
            atomized_names[nt_inreplyto] = nt.Add("in-reply-to");
            atomized_names[nt_watchcomments] = nt.Add("watch-comments");
            atomized_names[nt_ns_dc] = nt.Add("http://purl.org/dc/elements/1.1/");
            atomized_names[nt_ns_xhtml] = nt.Add("http://www.w3.org/1999/xhtml");
            atomized_names[nt_ns_content] = nt.Add("http://purl.org/rss/1.0/modules/content/");
            atomized_names[nt_ns_annotate] = nt.Add("http://purl.org/rss/1.0/modules/annotate/");
            atomized_names[nt_ns_bandit_2003] = nt.Add(NamespaceCore.Feeds_v2003);
            atomized_names[nt_ns_slash] = nt.Add("http://purl.org/rss/1.0/modules/slash/");
            atomized_names[nt_ns_wfw] = nt.Add("http://wellformedweb.org/CommentAPI/");
            atomized_names[nt_ns_fd] = nt.Add("http://www.bradsoft.com/feeddemon/xmlns/1.0/");
            atomized_names[nt_ns_thr] = nt.Add("http://purl.org/syndication/thread/1.0");
            atomized_names[nt_updated] = nt.Add("updated");
            atomized_names[nt_published] = nt.Add("published");
            atomized_names[nt_ns_mediarss] = nt.Add("http://search.yahoo.com/mrss/");
            atomized_names[nt_ns_itunes] = nt.Add("http://www.itunes.com/dtds/podcast-1.0.dtd");
            return atomized_names;
        }
        public static FeedInfo GetItemsForFeed(INewsFeed f, Stream feedStream, bool cachedStream)
        {
            if (f == null || f.link == null)
                return null;
            if (!CanProcessUrl(f.link))
            {
                throw new ApplicationException(ComponentsText.ExceptionNoProcessingHandlerMessage(f.link));
            }
            XmlTextReader r = new XmlTextReader(feedStream);
            r.WhitespaceHandling = WhitespaceHandling.Significant;
            XmlBaseAwareXmlValidatingReader vr = new XmlBaseAwareXmlValidatingReader(f.link, r);
            vr.ValidationType = ValidationType.None;
            vr.XmlResolver = new ProxyXmlUrlResolver(FeedSource.GlobalProxy);
            return GetItemsForFeed(f, vr, cachedStream);
        }
        private static string GetContentFromAtomElement(XmlReader element, ref bool onNextElement)
        {
            string typeAttr = element.GetAttribute("type"), modeAttr = element.GetAttribute("mode");
            string type = (typeAttr == null ? "text/plain" : typeAttr.ToLower());
            string mode = (modeAttr == null ? "xml" : modeAttr.ToLower());
            if (element.NamespaceURI.Equals("http://purl.org/atom/ns#"))
            {
                if ((type.IndexOf("text") != -1) || (type.IndexOf("html") != -1))
                {
                    if (mode.Equals("escaped") || type.Equals("html"))
                    {
                        onNextElement = false;
                        return ReadElementString(element);
                    }
                    else if (mode.Equals("xml"))
                    {
                        onNextElement = true;
                        return element.ReadInnerXml();
                    }
                }
            }
            else if (element.NamespaceURI.Equals("http://www.w3.org/2005/Atom"))
            {
                if (type.IndexOf("xhtml") != -1)
                {
                    onNextElement = true;
                    return element.ReadInnerXml();
                }
                else if ((type.IndexOf("text") != -1))
                {
                    onNextElement = false;
                    return ReadElementString(element).Replace("<", "&lt;").Replace(">", "&gt;");
                }
                else if (type.IndexOf("html") != -1)
                {
                    onNextElement = false;
                    return ReadElementString(element);
                }
            }
            element.Skip();
            return String.Empty;
        }
        private static ContentType GetMimeTypeOfAtomElement(XmlReader element)
        {
            string mimetype;
            if (element["type"] != null)
            {
                mimetype = element["type"].ToLower();
                if (mimetype.IndexOf("xhtml") != -1)
                {
                    return ContentType.Xhtml;
                }
                else if (mimetype.IndexOf("html") != -1)
                {
                    return ContentType.Html;
                }
                else if (mimetype.IndexOf("text") != -1)
                {
                    return ContentType.Text;
                }
                else
                {
                    return ContentType.Unknown;
                }
            }
            else
            {
                return ContentType.Text;
            }
        }
        private static string ReadElementString(XmlReader reader)
        {
            String result = reader.ReadString();
            StringBuilder sb = null;
            while (reader.NodeType != XmlNodeType.EndElement)
            {
                if (sb == null)
                {
                    sb = new StringBuilder(result);
                }
                reader.Skip();
                sb.Append(reader.ReadString());
            }
            return (sb == null ? result : sb.ToString());
        }
        private static string ReadElementUrl(XmlReader reader)
        {
            return HtmlHelper.HtmlDecode(ReadElementString(reader)).Replace(Environment.NewLine, String.Empty).Trim();
        }
        private static void ProcessFeedElements(INewsFeed f, XmlReader reader, object[] atomized_strings, string rssNamespaceUri, SyndicationFormat format, ref string feedLink, ref string feedTitle, ref string feedDescription, ref DateTime channelBuildDate, IDictionary<XmlQualifiedName, string> optionalElements, ICollection<INewsItem> items, DateTime defaultItemDate, ref string language)
        {
            bool matched;
            bool nodeRead = false;
            if ((format == SyndicationFormat.Rdf) || (format == SyndicationFormat.Rss))
            {
                while ((nodeRead || reader.Read()) && reader.NodeType != XmlNodeType.EndElement)
                {
                    object localname = reader.LocalName;
                    object namespaceuri = reader.NamespaceURI;
                    matched = false;
                    nodeRead = false;
                    if (reader.NodeType != XmlNodeType.Element)
                    {
                        continue;
                    }
                    if (reader.NamespaceURI.Equals(rssNamespaceUri) || reader.NamespaceURI.Equals(String.Empty))
                    {
                        if (localname == atomized_strings[nt_title])
                        {
                            if (!reader.IsEmptyElement)
                            {
                                feedTitle = ReadElementString(reader);
                            }
                            matched = true;
                        }
                        else if (localname == atomized_strings[nt_description])
                        {
                            if (!reader.IsEmptyElement)
                            {
                                feedDescription = ReadElementString(reader);
                            }
                            matched = true;
                        }
                        else if (localname == atomized_strings[nt_link])
                        {
                            if (!reader.IsEmptyElement)
                            {
                                feedLink = ResolveRelativeUrl(reader, ReadElementUrl(reader));
                            }
                            matched = true;
                        }
                        else if (localname == atomized_strings[nt_language])
                        {
                            if (!reader.IsEmptyElement)
                            {
                                language = ReadElementString(reader);
                            }
                            matched = true;
                        }
                        else if (localname == atomized_strings[nt_lastbuilddate])
                        {
                            try
                            {
                                if (!reader.IsEmptyElement)
                                {
                                    channelBuildDate = DateTimeExt.Parse(ReadElementString(reader));
                                }
                            }
                            catch (FormatException fex)
                            {
                                _log.Warn("Error parsing date from channel {" + feedTitle +
                                          "} from feed {" + (feedLink ?? f.title) + "}: ", fex);
                            }
                            finally
                            {
                                matched = true;
                            }
                        }
                        else if (localname == atomized_strings[nt_items])
                        {
                            reader.Skip();
                            nodeRead = matched = true;
                        }
                        else if ((localname == atomized_strings[nt_image]) && format == SyndicationFormat.Rdf)
                        {
                            reader.Skip();
                            nodeRead = matched = true;
                        }
                        else if (localname == atomized_strings[nt_item])
                        {
                            if (!reader.IsEmptyElement)
                            {
                                NewsItem rssItem = MakeRssItem(f, reader, atomized_strings, defaultItemDate);
                                if (rssItem != null)
                                {
                                    items.Add(rssItem);
                                }
                            }
                            matched = true;
                        }
                    }
                    else if (namespaceuri == atomized_strings[nt_ns_bandit_2003])
                    {
                        if (localname == atomized_strings[nt_maxitemage])
                        {
                            if (!reader.IsEmptyElement)
                            {
                                f.maxitemage = XmlConvert.ToString(TimeSpan.Parse(ReadElementString(reader)));
                            }
                            matched = true;
                        }
                    }
                    if (!matched)
                    {
                        XmlQualifiedName qname = new XmlQualifiedName(reader.LocalName, reader.NamespaceURI);
                        string optionalNode = reader.ReadOuterXml();
                        if (!optionalElements.ContainsKey(qname))
                        {
                            optionalElements.Add(qname, optionalNode);
                        }
                        nodeRead = true;
                    }
                }
                if (format == SyndicationFormat.Rdf)
                {
                    reader.ReadEndElement();
                    do
                    {
                        object localname = reader.LocalName;
                        nodeRead = false;
                        if ((localname == atomized_strings[nt_image]) &&
                            reader.NamespaceURI.Equals(rssNamespaceUri))
                        {
                            XmlElement optionalNode = RssHelper.CreateXmlElement(reader);
                            optionalNode.SetAttribute("xmlns", String.Empty);
                            XmlQualifiedName qname =
                                new XmlQualifiedName(optionalNode.LocalName, optionalNode.NamespaceURI);
                            if (!optionalElements.ContainsKey(qname))
                            {
                                optionalElements.Add(qname, optionalNode.OuterXml);
                            }
                            nodeRead = true;
                        }
                        if ((localname == atomized_strings[nt_item]) &&
                            reader.NamespaceURI.Equals(rssNamespaceUri))
                        {
                            if (!reader.IsEmptyElement)
                            {
                                NewsItem rssItem = MakeRssItem(f, reader, atomized_strings, defaultItemDate);
                                if (rssItem != null)
                                {
                                    items.Add(rssItem);
                                }
                            }
                        }
                    } while (nodeRead || reader.Read());
                }
            }
            else if (format == SyndicationFormat.Atom)
            {
                while ((nodeRead || reader.Read()) && reader.NodeType != XmlNodeType.EndElement)
                {
                    object localname = reader.LocalName;
                    object namespaceuri = reader.NamespaceURI;
                    matched = false;
                    nodeRead = false;
                    if (reader.NodeType != XmlNodeType.Element)
                    {
                        continue;
                    }
                    if (reader.NamespaceURI.Equals(rssNamespaceUri) || reader.NamespaceURI.Equals(String.Empty))
                    {
                        if (localname == atomized_strings[nt_title])
                        {
                            if (!reader.IsEmptyElement)
                            {
                                feedTitle = ReadElementString(reader);
                            }
                            matched = true;
                        }
                        else if (localname == atomized_strings[nt_tagline])
                        {
                            if (!reader.IsEmptyElement)
                            {
                                feedDescription = ReadElementString(reader);
                            }
                            matched = true;
                        }
                        else if (localname == atomized_strings[nt_link])
                        {
                            string rel = reader.GetAttribute("rel");
                            string href = reader.GetAttribute("href");
                            if (string.IsNullOrEmpty(feedLink))
                            {
                                if ((rel != null) && (href != null) &&
                                    rel.Equals("alternate"))
                                {
                                    feedLink = ResolveRelativeUrl(reader, href);
                                    matched = true;
                                    if (!reader.IsEmptyElement)
                                    {
                                        reader.Skip();
                                        nodeRead = true;
                                    }
                                }
                            }
                        }
                        else if (localname == atomized_strings[nt_modified] ||
                                 localname == atomized_strings[nt_updated])
                        {
                            try
                            {
                                if (!reader.IsEmptyElement)
                                {
                                    channelBuildDate = DateTimeExt.Parse(ReadElementString(reader));
                                }
                            }
                            catch (FormatException fex)
                            {
                                _log.Warn(string.Format("Error parsing date from channel {{{0}}} from feed {{{1}}}: ", feedTitle, (feedLink ?? f.title)), fex);
                            }
                            finally
                            {
                                matched = true;
                            }
                        }
                        else if (localname == atomized_strings[nt_entry])
                        {
                            if (!reader.IsEmptyElement)
                            {
                                NewsItem atomItem = MakeAtomItem(f, reader, atomized_strings, defaultItemDate);
                                if (atomItem != null)
                                {
                                    items.Add(atomItem);
                                }
                            }
                            matched = true;
                        }
                    }
                    else if (namespaceuri == atomized_strings[nt_ns_bandit_2003])
                    {
                        if (localname == atomized_strings[nt_maxitemage])
                        {
                            if (!reader.IsEmptyElement)
                            {
                                TimeSpan maxItemAgeTS = TimeSpan.Parse(ReadElementString(reader));
                                if (maxItemAgeTS != TimeSpan.MaxValue)
                                {
                                    f.maxitemage = XmlConvert.ToString(maxItemAgeTS);
                                }
                            }
                            matched = true;
                        }
                    }
                    if (!matched)
                    {
                        XmlQualifiedName qname = new XmlQualifiedName(reader.LocalName, reader.NamespaceURI);
                        string optionalNode = reader.ReadOuterXml();
                        if (!optionalElements.ContainsKey(qname))
                        {
                            optionalElements.Add(qname, optionalNode);
                        }
                        nodeRead = true;
                    }
                }
            }
        }
        public HttpStatusCode PostCommentViaCommentAPI(string url, INewsItem item2post, INewsItem inReply2item,
                                                       ICredentials credentials)
        {
            string comment = item2post.ToString(NewsItemSerializationFormat.RssItem);
            Encoding enc = Encoding.UTF8, unicode = Encoding.Unicode;
            byte[] encBytes = Encoding.Convert(unicode, enc, unicode.GetBytes(comment));
            HttpWebRequest request = (HttpWebRequest) WebRequest.Create(url);
            request.Timeout = 1*60*1000;
            request.UserAgent = owner.FullUserAgent;
            request.Proxy = owner.Proxy;
            if (credentials == null)
                credentials = CredentialCache.DefaultCredentials;
            request.Credentials = credentials;
            request.Method = "POST";
            request.Headers.Add("charset", "UTF-8");
            request.ContentType = "text/xml";
            request.ContentLength = encBytes.Length;
            _log.Info("PostCommentViaCommentAPI() post item content: " + comment);
            Stream myWriter = null;
            try
            {
                myWriter = request.GetRequestStream();
                myWriter.Write(encBytes, 0, encBytes.Length);
            }
            catch (Exception e)
            {
                throw new WebException(e.Message, e);
            }
            finally
            {
                if (myWriter != null)
                {
                    myWriter.Close();
                }
            }
            HttpWebResponse response = (HttpWebResponse) request.GetResponse();
            return response.StatusCode;
        }
    }
    [Serializable]
    public class RssParserException : ApplicationException
    {
        public RssParserException()
        {
        }
        public RssParserException(string message) : base(message)
        {
        }
        public RssParserException(string message, Exception inner) : base(message, inner)
        {
        }
        protected RssParserException(
            SerializationInfo info,
            StreamingContext context)
            : base(info, context)
        {
        }
    }
    internal class XmlBaseAwareXmlValidatingReader : XmlValidatingReader
    {
        private XmlBaseState _state = new XmlBaseState();
        private readonly Stack<XmlBaseState> _states = new Stack<XmlBaseState>();
        public XmlBaseAwareXmlValidatingReader(string baseUri, XmlReader reader)
            : base(reader)
        {
            _state.BaseUri = new Uri(baseUri);
        }
        public override string BaseURI
        {
            get
            {
                return _state.BaseUri == null ? String.Empty : _state.BaseUri.AbsoluteUri;
            }
        }
        public Uri BaseURIasUri
        {
            get
            {
                return _state.BaseUri ?? new Uri(String.Empty);
            }
        }
        public override bool Read()
        {
            bool baseRead = base.Read();
            if (baseRead)
            {
                if (base.NodeType == XmlNodeType.Element &&
                    base.HasAttributes)
                {
                    string baseAttr = GetAttribute("xml:base");
                    if (baseAttr == null)
                        return baseRead;
                    Uri newBaseUri;
                    if (_state.BaseUri == null)
                        newBaseUri = new Uri(baseAttr);
                    else
                        newBaseUri = new Uri(_state.BaseUri, baseAttr);
                    _states.Push(_state);
                    _state = new XmlBaseState(newBaseUri, base.Depth);
                }
                else if (base.NodeType == XmlNodeType.EndElement)
                {
                    if (base.Depth == _state.Depth && _states.Count > 0)
                    {
                        _state = _states.Pop();
                    }
                }
            }
            return baseRead;
        }
    }
    internal class XmlBaseState
    {
        public XmlBaseState()
        {
        }
        public XmlBaseState(Uri baseUri, int depth)
        {
            BaseUri = baseUri;
            Depth = depth;
        }
        public Uri BaseUri;
        public int Depth;
    }
    public class ProxyXmlUrlResolver : XmlUrlResolver
    {
        public ProxyXmlUrlResolver(IWebProxy proxy)
        {
            this.proxy = proxy;
        }
        private readonly IWebProxy proxy;
        public override object GetEntity(Uri absoluteUri, string role,
                                         Type ofObjectToReturn)
        {
            if (absoluteUri.AbsoluteUri.ToLower().Equals("http://my.netscape.com/publish/formats/rss-0.91.dtd") ||
                absoluteUri.AbsoluteUri.EndsWith("0.91/EN"))
            {
                return Resource.Manager.GetStream("Resources.rss-0.91.dtd");
            }
            else if (absoluteUri.AbsoluteUri.ToLower().Equals("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd") ||
                     absoluteUri.AbsoluteUri.EndsWith("Strict/EN"))
            {
                return Resource.Manager.GetStream("Resources.xhtml1-strict.dtd");
            }
            else if (
                absoluteUri.AbsoluteUri.ToLower().Equals("http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd") ||
                absoluteUri.AbsoluteUri.EndsWith("Transitional/EN"))
            {
                return Resource.Manager.GetStream("Resources.xhtml1-transitional.dtd");
            }
            else if (absoluteUri.AbsoluteUri.ToLower().EndsWith("xhtml-lat1.ent") ||
                     absoluteUri.AbsoluteUri.IndexOf("Latin") == -1)
            {
                return Resource.Manager.GetStream("Resources.xhtml-lat1.ent");
            }
            else if (absoluteUri.AbsoluteUri.ToLower().EndsWith("xhtml-special.ent") ||
                     absoluteUri.AbsoluteUri.IndexOf("Special") == -1)
            {
                return Resource.Manager.GetStream("Resources.xhtml-special.ent");
            }
            else if (absoluteUri.AbsoluteUri.ToLower().EndsWith("xhtml-symbol.ent") ||
                     absoluteUri.AbsoluteUri.IndexOf("Symbol") == -1)
            {
                return Resource.Manager.GetStream("Resources.xhtml-symbol.ent");
            }
            try
            {
                if (absoluteUri.IsFile)
                    return base.GetEntity(absoluteUri, role, ofObjectToReturn);
                WebRequest req = WebRequest.Create(absoluteUri);
                req.Proxy = proxy;
                return req.GetResponse().GetResponseStream();
            }
            catch (WebException)
            {
                return base.GetEntity(absoluteUri, role, ofObjectToReturn);
            }
            catch (IOException)
            {
                if (Common.ClrVersion.Major > 1)
                    return null;
                return base.GetEntity(absoluteUri, role, ofObjectToReturn);
            }
        }
    }
}
