using System;
using System.Collections;
using System.IO;
using System.Text.RegularExpressions;
using System.Xml.Serialization;
using System.Xml.XPath;
using NewsComponents.Feed;
using NewsComponents.Search.BooleanSearch;
using NewsComponents.Utils;
namespace NewsComponents.Search
{
 [XmlInclude(typeof(SearchCriteriaAge)),
 XmlInclude(typeof(SearchCriteriaString)),
 XmlInclude(typeof(SearchCriteriaProperty))]
 public abstract class ISearchCriteria {
  public abstract bool Match(NewsItem item);
  public abstract bool Match(FeedInfo feed);
 }
 [Flags]
 public enum SearchStringElement {
  Undefined = 0,
  Title = 1,
  Content = 2,
  Subject = 4,
  Link = 8,
  Author = 16,
  All = Title | Content | Subject | Link | Author
 }
 public enum StringExpressionKind {
  Text,
  LuceneExpression,
  RegularExpression,
  XPathExpression
 }
 public enum DateExpressionKind {
  Equal,
  OlderThan,
  NewerThan,
 }
 public enum PropertyExpressionKind {
  Unread,
  Flagged
 }
 public enum ItemReadState {
  Ignore,
  BeenRead,
  Unread,
 }
 [Serializable]
 public class SearchCriteriaCollection: ICollection {
  ArrayList criteriaList = new ArrayList(1);
  public bool Match(NewsItem item) {
   if (criteriaList.Count == 0)
    return false;
   foreach(ISearchCriteria sc in criteriaList){
    if(!sc.Match(item))
     return false;
   }
   return true;
  }
  public bool Match(FeedInfo feed) {
   if (criteriaList.Count == 0)
    return false;
   foreach(ISearchCriteria sc in criteriaList){
    if(!sc.Match(feed))
     return false;
   }
   return true;
  }
  public void Add(ISearchCriteria criteria) {
   criteriaList.Add(criteria);
  }
  public void Remove(ISearchCriteria criteria) {
   criteriaList.Remove(criteria);
  }
  public ISearchCriteria this[int criteria] {
   get { return (ISearchCriteria)criteriaList[criteria]; }
   set { criteriaList[criteria] = value; }
  }
  public int Count {
   get { return criteriaList.Count; }
  }
  public bool IsSynchronized { get { return criteriaList.IsSynchronized;} }
  public void CopyTo(Array array, int index) { criteriaList.CopyTo(array, index); }
  public object SyncRoot { get { return criteriaList.SyncRoot; } }
  public IEnumerator GetEnumerator() { return criteriaList.GetEnumerator(); }
 }
 [Serializable]
 public class SearchCriteriaAge: ISearchCriteria {
  [XmlElement("WhatRelativeToToday")]
  public string WhatRelativeToTodayString{
   get {
    return WhatRelativeToToday.ToString();
   }
   set {
    WhatRelativeToToday = TimeSpan.Parse(value);
   }
  }
  [XmlIgnore()]
  public TimeSpan WhatRelativeToToday {
   get {
    return whatRelativeToToday;
   }
   set {
    whatRelativeToToday = value;
    if (whatRelativeToToday > TimeSpan.Zero) {
     what = DateTime.MinValue;
     whatYearOnly = 0;
    }
   }
  }
  internal int WhatAsIntDateOnly {
   get { return whatYearOnly; }
  }
  public DateExpressionKind WhatKind;
  public DateTime What {
   get {
    return what;
   }
   set {
    what = value;
    whatYearOnly = DateTimeExt.DateAsInteger(what);
    if (what > DateTime.MinValue)
     whatRelativeToToday = TimeSpan.Zero;
   }
  }
  private TimeSpan whatRelativeToToday;
  private int whatYearOnly;
  private DateTime what;
  public SearchCriteriaAge() {
   this.WhatRelativeToToday = TimeSpan.Zero;
   this.What = DateTime.MinValue;
  }
  public SearchCriteriaAge(DateExpressionKind whatKind):this(){
   this.WhatKind = whatKind;
  }
  public SearchCriteriaAge(DateTime what, DateExpressionKind whatKind):this(whatKind){
   this.What = what;
  }
  public SearchCriteriaAge(TimeSpan whatRelative, DateExpressionKind whatKind):this(whatKind){
   this.WhatRelativeToToday = whatRelative;
  }
  public override bool Match(NewsItem item) {
   if (this.WhatRelativeToToday.CompareTo(TimeSpan.Zero) == 0) {
    int itemDate = DateTimeExt.DateAsInteger(item.Date);
    switch(this.WhatKind){
     case DateExpressionKind.Equal:
      return itemDate == whatYearOnly;
     case DateExpressionKind.OlderThan:
      return itemDate < whatYearOnly;
     case DateExpressionKind.NewerThan:
      return itemDate > whatYearOnly;
     default:
      return false;
    }
   } else {
    DateTime dt = DateTime.Now.ToUniversalTime().Subtract(this.WhatRelativeToToday);
    switch(this.WhatKind){
     case DateExpressionKind.OlderThan:
      return item.Date <= dt;
     case DateExpressionKind.NewerThan:
      return item.Date >= dt;
     default:
      return false;
    }
   }
  }
  public override bool Match(FeedInfo feed) {
   return false;
  }
 }
 [Serializable]
 public class SearchCriteriaDateRange: ISearchCriteria {
  public static readonly DateTime MinValue = new DateTime(1980, 1, 1);
  public DateTime Bottom {
   get {
    return lowDate;
   }
   set {
    lowDate = value;
    lowDateOnly = DateTimeExt.DateAsInteger(lowDate);
   }
  }
  private int lowDateOnly;
  private DateTime lowDate;
  public DateTime Top {
   get {
    return highDate;
   }
   set {
    highDate = value;
    highDateOnly = DateTimeExt.DateAsInteger(highDate);
   }
  }
  private int highDateOnly;
  private DateTime highDate;
  public SearchCriteriaDateRange() {
   this.Bottom = DateTime.MinValue;
   this.Top = DateTime.Now;
  }
  public SearchCriteriaDateRange(DateTime bottom, DateTime top):this(){
   this.Bottom = bottom;
   this.Top = top;
  }
  public override bool Match(NewsItem item) {
   int itemDate = DateTimeExt.DateAsInteger(item.Date);
   return itemDate > lowDateOnly && itemDate < highDateOnly;
  }
  public override bool Match(FeedInfo feed) {
   return false;
  }
 }
 [Serializable]
 public class SearchCriteriaProperty: ISearchCriteria {
  public bool BeenRead;
  public Flagged Flags;
  public PropertyExpressionKind WhatKind;
  public SearchCriteriaProperty() {}
  public override bool Match(NewsItem item) {
   switch(WhatKind){
    case PropertyExpressionKind.Flagged:
     return item.FlagStatus != Flagged.None;
    case PropertyExpressionKind.Unread:
     return item.BeenRead == this.BeenRead;
    default:
     return false;
   }
  }
  public override bool Match(FeedInfo feed) {
   return false;
  }
 }
  public class SearchRssDocument:IDocument
  {
   private NewsItem item;
   private SearchStringElement Where;
   private Regex htmlRegex = SearchCriteriaString.htmlRegex;
   public SearchRssDocument(NewsItem item,SearchStringElement where)
   {
    this.item=item;
    this.Where=where;
   }
   public bool Find(string What)
   {
    string lowerWhat = What.ToLower();
    if(((Where & SearchStringElement.Title) > 0) && item.Title != null)
    {
     if(item.Title.ToLower().IndexOf(lowerWhat)!= -1) return true;
    }
    if(((Where & SearchStringElement.Link) > 0) && item.Link != null)
    {
     if(item.Link.ToLower().IndexOf(lowerWhat)!= -1) return true;
    }
    if(((Where & SearchStringElement.Content) > 0) && item.Content != null)
    {
     string strippedxhtml = htmlRegex.Replace(item.Content, String.Empty);
     if(strippedxhtml.ToLower().IndexOf(lowerWhat)!= -1) return true;
    }
    if(((Where & SearchStringElement.Subject) > 0) && item.Subject != null)
    {
     if(item.Subject.ToLower().IndexOf(lowerWhat)!= -1) return true;
    }
    return false;
   }
   public string Name()
   {
    return item.ToString();
   }
  }
 [Serializable]
 public class SearchCriteriaString:ISearchCriteria {
  public static Regex htmlRegex = new Regex("</?[^>]+>");
  public static Regex placeholderRegex = new Regex("\\$\\!\\$");
  public SearchCriteriaString() {}
  public SearchCriteriaString(string what, SearchStringElement where, StringExpressionKind kind) {
   this.What = what;
   this.Where = where;
   this.WhatKind = kind;
  }
  private SearchStringElement where;
  private string what;
  private StringExpressionKind whatKind;
  public SearchStringElement Where {
   get { return where; }
   set { where = value; currentExpressionRegex = null; }
  }
  public string What {
   get { return what; }
   set { what = value; currentExpressionRegex = null; }
  }
  public StringExpressionKind WhatKind {
   get { return whatKind; }
   set { whatKind = value; currentExpressionRegex = null; }
  }
  private Regex currentExpressionRegex = null;
  public override bool Match(NewsItem item)
  {
   switch(WhatKind){
    case StringExpressionKind.Text:
      QueryBuilder builder = new QueryBuilder(What);
      if (builder.Validate())
      {
       QueryTree tree = builder.BuildTree();
       IDocument[] docs = new IDocument[1];
       docs[0]= new SearchRssDocument(item,Where);
       IDocument[] matches = tree.GetMatches(docs);
       return matches.Length > 0;
      }
      else
      {
       return false;
      }
    case StringExpressionKind.RegularExpression:
     if (currentExpressionRegex == null) {
      RegexOptions opts = RegexOptions.IgnoreCase | RegexOptions.Compiled;
      if ((Where & SearchStringElement.Content) > 0) {
       opts |= RegexOptions.Multiline;
      }
      currentExpressionRegex = new Regex(What, opts);
     }
     if(((Where & SearchStringElement.Title) > 0) && item.Title != null){
      if(currentExpressionRegex.Match(item.Title).Success) return true;
     }
     if(((Where & SearchStringElement.Link) > 0) && item.Link != null){
      if(currentExpressionRegex.Match(item.Link).Success) return true;
     }
     if(((Where & SearchStringElement.Content) > 0) && item.Content != null){
      string strippedxhtml = htmlRegex.Replace(item.Content, String.Empty);
      if(currentExpressionRegex.Match(strippedxhtml).Success) return true;
     }
     if(((Where & SearchStringElement.Subject) > 0) && item.Subject != null){
      if(currentExpressionRegex.Match(item.Subject).Success) return true;
     }
     return false;
    case StringExpressionKind.XPathExpression:
     XPathDocument doc = new XPathDocument(new StringReader(item.ToString(NewsItemSerializationFormat.RssItem)));
     XPathNavigator nav = doc.CreateNavigator();
     if((bool)nav.Evaluate("boolean(" + What + ")")){
      return true;
     }else{
      return false;
     }
    case StringExpressionKind.LuceneExpression:
     return true;
    default:
     return false;
   }
  }
  public override bool Match(FeedInfo feed) {
   return false;
  }
 }
}
