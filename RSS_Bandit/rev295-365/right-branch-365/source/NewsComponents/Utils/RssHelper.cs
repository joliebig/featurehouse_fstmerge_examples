using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Xml;
using NewsComponents.News;
namespace NewsComponents.Utils
{
 public enum NewsItemSortField{
  Title,
  Date,
  Subject,
  Author,
  FeedTitle,
  CommentCount,
  Enclosure,
  Flag
 }
 public sealed class RssHelper {
  private RssHelper() {}
  public const string NsSlashModules = "http://purl.org/rss/1.0/modules/slash/";
  public const string NsDcElements = "http://purl.org/dc/elements/1.1/";
  public const string NsCommentAPI = "http://wellformedweb.org/CommentAPI/";
  private static readonly XmlDocument elementCreator = new XmlDocument();
  public static DateTime[] InitialLastRetrievedSettings(int expectedAmountOfBulkRequests, int defaultRefreshRateInMSecs) {
   if (expectedAmountOfBulkRequests <= 100)
    return new DateTime[]{DateTime.MinValue, DateTime.MinValue, DateTime.MinValue};
   double defaultRefreshRate = 60 * 60 * 1000;
   if (defaultRefreshRateInMSecs > (10*60*1000))
    defaultRefreshRate = defaultRefreshRateInMSecs;
   int max = 103;
   double step = defaultRefreshRate / (max + 1);
   DateTime[] vals = new DateTime[max];
   DateTime startDate = DateTime.Now.AddMilliseconds(-defaultRefreshRate);
   int overwrite = (expectedAmountOfBulkRequests >> 6);
   for (int i = 0; i < max; i++) {
    vals[i] = startDate.AddMilliseconds(i*step);
    if ((i % overwrite) == 0)
     vals[i] = DateTime.MinValue;
   }
   return vals;
  }
  static public IList ItemListToHRefList(IList items) {
   ArrayList retList = new ArrayList(items.Count);
   foreach (NewsItem item in items) {
    if (!string.IsNullOrEmpty(item.Link))
     retList.Add(item.Link);
   }
   return retList;
  }
  static public string GetDcElementValue(NewsItem item, string elementName) {
   return GetOptionalElementValue(item, elementName, NsDcElements);
  }
  static public string GetHashCode(NewsItem item) {
   if (item == null) return null;
   System.Text.StringBuilder newHash = new System.Text.StringBuilder();
   if (item.Feed != null && item.Feed.link != null && item.Feed.link.Length > 0)
    newHash.Append(item.Feed.link.GetHashCode().ToString());
   if (item.Link != null && item.Link.Length > 0)
    newHash.Append(item.Link.GetHashCode().ToString());
   if (item.Title != null && item.Title.Length > 0)
    newHash.Append(item.Title.GetHashCode().ToString());
   if (item.HasContent)
    newHash.Append(item.Content.GetHashCode().ToString());
   return newHash.ToString();
  }
  static private string GetOptionalElementValue(NewsItem item, string elementName, string elementNamespace) {
   if (item.OptionalElements == null || item.OptionalElements.Count == 0)
    return null;
   string retStr = null;
   XmlElement elem = GetOptionalElement(item, elementName, elementNamespace);
   if (elem != null)
    retStr = elem.InnerText;
   return retStr;
  }
  static public XmlElement GetOptionalElement(NewsItem item, string elementName, string elementNamespace) {
   if (item == null || item.OptionalElements == null)
    return null;
   return GetOptionalElement(item.OptionalElements, elementName, elementNamespace);
  }
  static public XmlElement GetOptionalElement(NewsItem item, XmlQualifiedName qName) {
   if (item == null || item.OptionalElements == null || qName == null)
    return null;
   return GetOptionalElement(item.OptionalElements, qName.Name, qName.Namespace);
  }
  static public XmlElement GetOptionalElement(IDictionary elements, string elementName, string elementNamespace) {
   if (elements == null || elements.Count == 0)
    return null;
   XmlQualifiedName qname = GetOptionalElementKey(elements, elementName, elementNamespace);
   if (qname != null) {
    string elem = (string) elements[qname];
    if(elem != null) {
     return (XmlElement) elementCreator.ReadNode(new XmlTextReader(new StringReader(elem)));
    }
   }
   return null;
  }
  static public XmlElement[] GetOptionalElements(IDictionary elements, string elementName, string elementNamespace) {
   if (elements == null || elements.Count == 0)
    return null;
   XmlQualifiedName[] qnames = GetOptionalElementKeys(elements, elementName, elementNamespace);
   if (qnames != null) {
    ArrayList xElements = new ArrayList();
    foreach (XmlQualifiedName qname in qnames) {
     string elem = (string) elements[qname];
     if(elem != null) {
      xElements.Add(elementCreator.ReadNode(new XmlTextReader(new StringReader(elem))));
     }
    }
    return (XmlElement[])xElements.ToArray( typeof(XmlElement) );
   }
   return null;
  }
  static public XmlQualifiedName GetOptionalElementKey(IDictionary elements, XmlQualifiedName qName) {
   if (elements == null || elements.Count == 0 || qName == null)
    return null;
   return GetOptionalElementKey(elements, qName.Name, qName.Namespace);
  }
  static public XmlQualifiedName GetOptionalElementKey(IDictionary elements, string elementName, string elementNamespace) {
   if (elements == null || elements.Count == 0)
    return null;
   foreach(XmlQualifiedName qname in elements.Keys){
    if (qname.Namespace.Equals(elementNamespace) && qname.Name.IndexOf(elementName) >= 0) {
     return qname;
    }
   }
   return null;
  }
  static public XmlQualifiedName[] GetOptionalElementKeys(IDictionary elements, string elementName, string elementNamespace) {
   if (elements == null || elements.Count == 0)
    return null;
   ArrayList names = new ArrayList();
   foreach(XmlQualifiedName qname in elements.Keys){
    if (qname.Namespace.Equals(elementNamespace) && qname.Name.IndexOf(elementName) >= 0) {
     names.Add(qname);
    }
   }
   return (XmlQualifiedName[])names.ToArray( typeof(XmlQualifiedName) );
  }
  static public XmlElement CreateXmlElement(string prefix, string elementName, string elementNamespace, string value) {
   XmlElement e = elementCreator.CreateElement(prefix, elementName, elementNamespace);
   e.InnerText = value;
   return e;
  }
  static public XmlElement CreateXmlElement(XmlReader reader) {
   return (XmlElement) elementCreator.ReadNode(reader);
  }
        static public IComparer<NewsItem> GetComparer() {
   return new NewsItemComparer();
  }
  static public IComparer<NewsItem> GetComparer(bool sortDescending) {
   return new NewsItemComparer(sortDescending);
  }
  static public IComparer<NewsItem> GetComparer(bool sortDescending, NewsItemSortField sortField) {
   return new NewsItemComparer(sortDescending, sortField);
  }
  public static bool IsFeedUrl(string url) {
   if (string.IsNullOrEmpty(url))
    return false;
   if (url.StartsWith("http") || url.StartsWith("file") || File.Exists(url))
    return true;
   return false;
  }
  public static bool IsNntpUrl(string url) {
   if (string.IsNullOrEmpty(url))
    return false;
   if (url.StartsWith(NntpWebRequest.NntpUriScheme) || url.StartsWith(NntpWebRequest.NewsUriScheme) || url.StartsWith(NntpWebRequest.NntpsUriScheme))
    return true;
   return false;
  }
  internal class NewsItemComparer: IComparer, IComparer<NewsItem> {
   private readonly bool sortDescending;
   private readonly NewsItemSortField sortField;
   public NewsItemComparer():this(false, NewsItemSortField.Date) {}
   public NewsItemComparer(bool sortDescending):this(sortDescending, NewsItemSortField.Date){}
   public NewsItemComparer(NewsItemSortField sortField):this(false, sortField) {}
   public NewsItemComparer(bool sortDescending, NewsItemSortField sortField) {
    this.sortField = sortField;
    this.sortDescending = sortDescending;
   }
            public int Compare(object o1, object o2) {
                return this.Compare(o1 as NewsItem, o2 as NewsItem);
            }
            public int Compare(NewsItem ri1, NewsItem ri2) {
    try {
     if (ri1 == null || ri2 == null)
      return 0;
     int reverse = (this.sortDescending ? 1: -1);
     switch(this.sortField){
      case NewsItemSortField.Date:
       return reverse * DateTime.Compare(ri2.Date, ri1.Date);
      case NewsItemSortField.Author:
       return reverse * String.Compare(ri2.Author, ri1.Author);
      case NewsItemSortField.CommentCount:
       return reverse * ri2.CommentCount.CompareTo(ri1.CommentCount);
      case NewsItemSortField.Subject:
       return reverse * String.Compare(ri2.Subject, ri1.Subject);
      case NewsItemSortField.Title:
       return reverse * String.Compare(ri2.Title, ri1.Title);
      case NewsItemSortField.FeedTitle:
       return reverse * String.Compare(ri2.Feed.title, ri1.Feed.title);
      case NewsItemSortField.Flag:
       return reverse * ri2.FlagStatus.CompareTo(ri1.FlagStatus);
      case NewsItemSortField.Enclosure:
       XmlElement x1 = GetOptionalElement(ri1, "enclosure", String.Empty);
       XmlElement x2 = GetOptionalElement(ri2, "enclosure", String.Empty);
       if (x1 == null && x2 == null) return 0;
       if (x2 != null)
        return reverse * 1;
       else
        return reverse * -1;
      default:
       return 0;
     }
    }
    catch (System.Threading.ThreadAbortException) {}
    return 0;
   }
  }
 }
}
