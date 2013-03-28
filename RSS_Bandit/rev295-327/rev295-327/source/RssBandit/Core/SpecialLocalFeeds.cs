using System; 
using System.IO; 
using System.Xml; 
using System.Collections; 
using System.Collections.Generic; 
using System.Text; 
using NewsComponents; 
using NewsComponents.Feed; 
using NewsComponents.Net; 
using NewsComponents.Utils; 
using RssBandit; 
using RssBandit.Resources; 
using RssBandit.Common; 
using Logger = RssBandit.Common.Logging; namespace  RssBandit.SpecialFeeds {
	
 public class  LocalFeedsFeed :NewsFeed {
		
  private static readonly  log4net.ILog _log = Logger.Log.GetLogger(typeof(LocalFeedsFeed));
 
  private  string filePath = null;
 
  protected  FeedInfo feedInfo = null;
 
  private  string description = null;
 
  private  bool modified = false;
 
  public  LocalFeedsFeed() {
   base.refreshrate = 0;
   base.refreshrateSpecified = true;
   this.feedInfo = new FeedInfo(null, null, null);
   base.cacheurl = "non.cached.feed";
  }
 
  public  LocalFeedsFeed(string feedUrl, string feedTitle, string feedDescription):
   this(feedUrl, feedTitle, feedDescription, true) {
  }
 
  public  LocalFeedsFeed(string feedUrl, string feedTitle, string feedDescription, bool loadItems):this() {
   description = feedDescription;
   filePath = feedUrl;
   try {
    Uri feedUri = new Uri(feedUrl);
    base.link = feedUri.CanonicalizedUri();
   } catch {
    base.link = feedUrl;
   }
   base.title = feedTitle;
   this.feedInfo = new FeedInfo(null, filePath, new List<NewsItem>(), feedTitle, base.link, feedDescription);
   if (loadItems)
    LoadItems(this.GetDefaultReader());
  }
 
  public  LocalFeedsFeed(string feedUrl, string feedTitle, string feedDescription, XmlReader reader):
   this(feedUrl, feedTitle, feedDescription, false) {
   LoadItems(reader);
  }
 
  public  List<NewsItem> Items {
   get { return this.feedInfo.ItemsList; }
   set { this.feedInfo.ItemsList = new List<NewsItem>(value); }
  }
 
  public  void Add(LocalFeedsFeed lff){
   foreach(NewsItem item in lff.Items){
    if(!this.feedInfo.ItemsList.Contains(item)){
     this.Add(item);
    }
   }
  }
 
  public  void Add(NewsItem item) {
   if (item == null)
    return;
   item.FeedDetails = this.feedInfo;
   this.feedInfo.ItemsList.Add(item);
   this.modified = true;
  }
 
  public  void Remove(NewsItem item) {
   if (item != null)
   {
    int index = this.feedInfo.ItemsList.IndexOf(item);
    if (index >= 0)
    {
     this.feedInfo.ItemsList.RemoveAt(index);
     this.modified = true;
    }
   }
  }
 
  public  void Remove(string commentFeedUrl){
   if(!string.IsNullOrEmpty(commentFeedUrl)){
    for(int i = 0; i < this.feedInfo.ItemsList.Count; i++){
     NewsItem ni = feedInfo.ItemsList[i] as NewsItem;
     if(ni.CommentRssUrl.Equals(commentFeedUrl)){
      this.feedInfo.ItemsList.RemoveAt(i);
      break;
     }
    }
   }
  }
 
  public  string Location {
   get { return this.filePath; }
   set { this.filePath = value; }
  }
 
  public  string Url {
   get { return base.link; }
  }
 
  public  bool Modified {
   get { return this.modified; }
   set { this.modified = value; }
  }
 
  public  string Description {
   get { return this.feedInfo.Description; }
  }
 
  protected  void LoadItems(XmlReader reader){
   if (feedInfo.ItemsList.Count > 0)
    feedInfo.ItemsList.Clear();
   if (reader != null) {
    try{
     XmlDocument doc = new XmlDocument();
     doc.Load(reader);
     foreach(XmlElement elem in doc.SelectNodes("//item")){
      NewsItem item = RssParser.MakeRssItem(this, new XmlNodeReader(elem));
      item.BeenRead = true;
      item.FeedDetails = this.feedInfo;
      feedInfo.ItemsList.Add(item);
     }
    }catch(Exception e){
     ExceptionManager.GetInstance().Add(RssBanditApplication.CreateLocalFeedRequestException(e, this, this.feedInfo));
    }
   }
  }
 
  protected  XmlReader GetDefaultReader(){
   if (File.Exists(this.filePath)) {
    return new XmlTextReader(this.filePath);
   }
   return null;
  }
 
  public  void WriteTo(XmlWriter writer){
   this.feedInfo.WriteTo(writer);
  }
 
  public  void Save() {
   XmlTextWriter writer = null;
   try {
    writer = new XmlTextWriter(new StreamWriter( this.filePath ));
    this.WriteTo(writer);
    writer.Flush();
    writer.Close();
    Modified = false;
   } catch (Exception e) {
    _log.Error("LocalFeedsFeed.Save()", e);
   }
   finally { if (writer!=null) writer.Close(); }
  }

	}
	
 public sealed class  ExceptionManager :LocalFeedsFeed {
		
  private  ExceptionManager() { }
 
  public  ExceptionManager(string feedUrl, string feedTitle, string feedDescription):base(feedUrl, feedTitle, feedDescription, false){
   try {
    base.Save();
   } catch (Exception ex) {
    Common.Logging.Log.Fatal("ExceptionManager.Save() failed", ex);
   }
  }
 
  [System.Runtime.CompilerServices.MethodImpl(System.Runtime.CompilerServices.MethodImplOptions.Synchronized)] 
  public  void Add(System.Exception e) {
   FeedException fe = new FeedException(this, e);
   base.Add(fe.NewsItemInstance);
   try {
    base.Save();
   } catch (Exception ex) {
    Common.Logging.Log.Fatal("ExceptionManager.Save() failed", ex);
   }
  }
 
  public  void RemoveFeed(string feedUrl) {
   if (string.IsNullOrEmpty(feedUrl) || base.feedInfo.ItemsList.Count == 0)
    return;
   Stack removeAtIndex = new Stack();
   for (int i = 0; i < base.feedInfo.ItemsList.Count; i++) {
    NewsItem n = base.feedInfo.ItemsList[i] as NewsItem;
    if (n != null) {
     XmlElement xe = RssHelper.GetOptionalElement(n, AdditionalFeedElements.OriginalFeedOfErrorItem);
     if (xe != null && xe.InnerText == feedUrl) {
      removeAtIndex.Push(i);
      break;
     }
    }
   }
   while (removeAtIndex.Count > 0)
    base.feedInfo.ItemsList.RemoveAt((int)removeAtIndex.Pop());
  }
 
  public new  IList<NewsItem> Items {
   get { return base.feedInfo.ItemsList; }
  }
 
  public static  ExceptionManager GetInstance() {
   return InstanceHelper.instance;
  }
 
  private class  InstanceHelper {
			
   static  InstanceHelper() {;}
 
   internal static readonly  ExceptionManager instance = new ExceptionManager(
    RssBanditApplication.GetFeedErrorFileName(),
    SR.FeedNodeFeedExceptionsCaption,
    SR.FeedNodeFeedExceptionsDesc);

		}
		
  public class  FeedException {
			
   private static  int idCounter = 0;
 
   private  NewsItem _delegateTo = null;
 
   private  string _ID;
 
   private  string _feedCategory = String.Empty;
 
   private  string _feedTitle = String.Empty;
 
   private  string _resourceUrl = String.Empty;
 
   private  string _resourceUIText = String.Empty;
 
   private  string _publisherHomepage = String.Empty;
 
   private  string _publisher = String.Empty;
 
   private  string _techContact = String.Empty;
 
   private  string _generator = String.Empty;
 
   private  string _fullErrorInfoFile = String.Empty;
 
   public  FeedException(ExceptionManager ownerFeed, Exception e) {
    idCounter++;
    this._ID = String.Concat("#", idCounter.ToString());
    if (e is FeedRequestException)
     this.InitWith(ownerFeed, (FeedRequestException)e);
    else if (e is XmlException)
     this.InitWith(ownerFeed, (XmlException)e);
    else if (e is System.Net.WebException)
     this.InitWith(ownerFeed, (System.Net.WebException)e);
    else if (e is System.Net.ProtocolViolationException)
     this.InitWith(ownerFeed, (System.Net.ProtocolViolationException)e);
    else
     this.InitWith(ownerFeed, e);
   }
 
   private  void InitWith(ExceptionManager ownerFeed, FeedRequestException e) {
    if (e.Feed != null) {
     this._feedCategory = e.Feed.category;
     this._feedTitle = e.Feed.title;
     this._resourceUrl = e.Feed.link;
    }
    this._resourceUIText = e.FullTitle;
    this._publisherHomepage = e.PublisherHomepage;
    this._techContact = e.TechnicalContact;
    this._publisher = e.Publisher;
    this._generator = e.Generator;
    if (e.InnerException is XmlException)
     this.InitWith(ownerFeed, (XmlException)e.InnerException);
    else if (e.InnerException is System.Net.WebException)
     this.InitWith(ownerFeed, (System.Net.WebException)e.InnerException);
    else if (e.InnerException is System.Net.ProtocolViolationException)
     this.InitWith(ownerFeed, (System.Net.ProtocolViolationException)e.InnerException);
    else
     this.InitWith(ownerFeed, e.InnerException);
   }
 
   private  void InitWith(ExceptionManager ownerFeed, XmlException e) {
    this.InitWith(ownerFeed, e, SR.XmlExceptionCategory);
   }
 
   private  void InitWith(ExceptionManager ownerFeed, System.Net.WebException e) {
    this.InitWith(ownerFeed, e, SR.WebExceptionCategory);
   }
 
   private  void InitWith(ExceptionManager ownerFeed, System.Net.ProtocolViolationException e) {
    this.InitWith(ownerFeed, e, SR.ProtocolViolationExceptionCategory);
   }
 
   private  void InitWith(ExceptionManager ownerFeed, Exception e) {
    this.InitWith(ownerFeed, e, SR.ExceptionCategory);
   }
 
   private  void InitWith(ExceptionManager ownerFeed, Exception e, string categoryName) {
    FeedInfo fi = new FeedInfo(null, String.Empty, ownerFeed.Items , ownerFeed.title, ownerFeed.link, ownerFeed.Description );
    DateTime exDT = new DateTime(DateTime.Now.Ticks).ToUniversalTime();
    bool enableValidation = (e is XmlException);
    string link = this.BuildBaseLink(e, enableValidation);
    _delegateTo = new NewsItem(ownerFeed, this._feedTitle, link,
     this.BuildBaseDesc(e, enableValidation),
     exDT, categoryName,
     ContentType.Xhtml, CreateAdditionalElements(this._resourceUrl), link, null );
    _delegateTo.FeedDetails = fi;
    _delegateTo.BeenRead = false;
   }
 
   private  Hashtable CreateAdditionalElements(string errorCausingFeedUrl) {
    Hashtable r = new Hashtable();
    if (null != errorCausingFeedUrl) {
     XmlElement originalFeed = RssHelper.CreateXmlElement(
      AdditionalFeedElements.ElementPrefix,
      AdditionalFeedElements.OriginalFeedOfErrorItem.Name,
      AdditionalFeedElements.OriginalFeedOfErrorItem.Namespace,
      errorCausingFeedUrl);
     r.Add(AdditionalFeedElements.OriginalFeedOfErrorItem,
      originalFeed.OuterXml);
    }
    return r;
   }
 
   public  string ID { get {return _ID; } }
 
   public  NewsItem NewsItemInstance {
    get {
     NewsItem ri = (NewsItem)_delegateTo.Clone();
     ri.FeedDetails = _delegateTo.FeedDetails;
     return ri;
    }
   }
 
   private  bool ContainsInvalidXmlCharacter(string errorMessage){
    foreach(char c in errorMessage){
     if(Char.IsControl(c) && !c.Equals('\t') && !c.Equals('\r') && !c.Equals('\n')){
      return true;
     }
    }
    return false;
   }
 
   private  string BuildBaseDesc(Exception e, bool provideXMLValidationUrl) {
    StringBuilder s = new StringBuilder();
    XmlTextWriter writer = new XmlTextWriter(new StringWriter(s));
    writer.Formatting = Formatting.Indented;
    string msg = e.Message;
    if((e is XmlException) && ContainsInvalidXmlCharacter(e.Message)){
     msg = e.Message.Substring(5);
    }
    if (msg.IndexOf("<") >= 0) {
     msg = System.Web.HttpUtility.HtmlEncode(msg);
    }
    writer.WriteStartElement("p");
    writer.WriteRaw(SR.RefreshFeedExceptionReportStringPart(this._resourceUIText, msg));
    writer.WriteEndElement();
    if (this._publisher.Length > 0 || this._techContact.Length > 0 || this._publisherHomepage.Length > 0 )
    {
     writer.WriteStartElement("p");
     writer.WriteString(SR.RefreshFeedExceptionReportContactInfo);
     writer.WriteStartElement("ul");
     if (this._publisher.Length > 0) {
      writer.WriteStartElement("li");
      writer.WriteString(SR.RefreshFeedExceptionReportManagingEditor + ": ");
      if (StringHelper.IsEMailAddress(this._publisher)) {
       string mail = StringHelper.GetEMailAddress(this._publisher);
       writer.WriteStartElement("a");
       writer.WriteAttributeString("href", "mailto:"+mail);
       writer.WriteString(mail);
       writer.WriteEndElement();
      } else {
       writer.WriteString(this._publisher);
      }
      writer.WriteEndElement();
     }
     if (this._techContact.Length > 0) {
      writer.WriteStartElement("li");
      writer.WriteString(SR.RefreshFeedExceptionReportWebMaster + ": ");
      if (StringHelper.IsEMailAddress(this._techContact)) {
       string mail = StringHelper.GetEMailAddress(this._techContact);
       writer.WriteStartElement("a");
       writer.WriteAttributeString("href", "mailto:"+mail);
       writer.WriteString(mail);
       writer.WriteEndElement();
      } else {
       writer.WriteString(this._techContact);
      }
      writer.WriteEndElement();
     }
     if (this._publisherHomepage.Length > 0) {
      writer.WriteStartElement("li");
      writer.WriteString(SR.RefreshFeedExceptionReportHomePage + ": ");
      writer.WriteStartElement("a");
      writer.WriteAttributeString("href", this._publisherHomepage);
      writer.WriteString(this._publisherHomepage);
      writer.WriteEndElement();
      writer.WriteEndElement();
     }
     writer.WriteEndElement();
     if (this._generator .Length > 0) {
      writer.WriteString(SR.RefreshFeedExceptionGeneratorStringPart(this._generator));
     }
     writer.WriteEndElement();
    }
    writer.WriteStartElement("p");
    writer.WriteString(SR.RefreshFeedExceptionUserActionIntroText);
    writer.WriteStartElement("ul");
    if (provideXMLValidationUrl && this._resourceUrl.StartsWith("http")) {
     writer.WriteStartElement("li");
     writer.WriteStartElement("a");
     writer.WriteAttributeString("href", RssBanditApplication.FeedValidationUrlBase+this._resourceUrl);
     writer.WriteRaw(SR.RefreshFeedExceptionReportValidationPart);
     writer.WriteEndElement();
     writer.WriteEndElement();
    }
    writer.WriteStartElement("li");
    writer.WriteStartElement("a");
    writer.WriteAttributeString("href", String.Format("fdaction:?action=navigatetofeed&feedid={0}", HtmlHelper.UrlEncode(this._resourceUrl)));
    writer.WriteRaw(SR.RefreshFeedExceptionUserActionNavigateToSubscription);
    writer.WriteEndElement();
    writer.WriteEndElement();
    writer.WriteStartElement("li");
    writer.WriteStartElement("a");
    writer.WriteAttributeString("href", String.Format("fdaction:?action=unsubscribefeed&feedid={0}", HtmlHelper.UrlEncode(this._resourceUrl)));
    writer.WriteRaw(SR.RefreshFeedExceptionUserActionDeleteSubscription);
    writer.WriteEndElement();
    writer.WriteEndElement();
    writer.WriteEndElement();
    writer.WriteEndElement();
    return s.ToString();
   }
 
   private  string BuildBaseLink(Exception e, bool provideXMLValidationUrl) {
    string sLink = String.Empty;
    if (e.HelpLink != null) {
    }
    if (this._fullErrorInfoFile.Length == 0) {
     if (this._resourceUrl.StartsWith("http")) {
      sLink = (provideXMLValidationUrl ? RssBanditApplication.FeedValidationUrlBase : String.Empty) + this._resourceUrl;
     } else {
      sLink = this._resourceUrl;
     }
    } else {
     sLink = this._fullErrorInfoFile;
    }
    return sLink;
   }

		}

	}

}
