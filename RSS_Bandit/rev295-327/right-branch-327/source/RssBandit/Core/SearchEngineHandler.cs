using System;
using System.IO;
using System.Xml;
using System.Xml.Schema;
using System.Xml.Serialization;
using System.Collections;
using System.Collections.Generic;
using NewsComponents;
using RssBandit.Xml;
using AppExceptions = Microsoft.ApplicationBlocks.ExceptionManagement;
using Logger = RssBandit.Common.Logging;
namespace RssBandit.WebSearch
{
 public class SearchEngineHandler
 {
  public SearchEngineHandler()
  {
   this.LoadSearchConfigSchema();
  }
  private static readonly log4net.ILog _log = Logger.Log.GetLogger(typeof(SearchEngineHandler));
  private SearchEngines _engines = null;
  private bool validationErrorOccured = false;
  private bool enginesLoaded = false;
  private XmlSchema searchConfigSchema = null;
  public bool EnginesOK
  {
   get { return !validationErrorOccured; }
  }
  public bool EnginesLoaded {
   get { return enginesLoaded; }
  }
  public bool NewTabRequired
  {
   get
   {
    if(_engines == null)
     _engines = new SearchEngines();
    if(_engines.Engines== null)
     _engines.Engines = new List<SearchEngine>();
    return _engines.NewTabRequired;
   }
   set
   {
    if(_engines == null)
     _engines = new SearchEngines();
    if(_engines.Engines== null)
     _engines.Engines = new List<SearchEngine>();
    _engines.NewTabRequired = value;
   }
  }
  public List<SearchEngine> Engines
  {
   get
   {
    if(_engines == null)
     _engines = new SearchEngines();
    if(_engines.Engines== null)
                    _engines.Engines = new List<SearchEngine>();
    return _engines.Engines;
   }
  }
  private void LoadSearchConfigSchema() {
   using (Stream stream = Resource.GetStream("Resources.SearchEnginesConfig.xsd")) {
    searchConfigSchema = XmlSchema.Read(stream, null);
   }
  }
  public void LoadEngines(string configUrl, ValidationEventHandler veh) {
   XmlDocument doc = new XmlDocument();
            XmlReaderSettings settings = new XmlReaderSettings();
            settings.ValidationType = ValidationType.Schema;
            settings.Schemas.Add(searchConfigSchema);
            settings.ValidationEventHandler += veh;
            settings.ValidationEventHandler += new ValidationEventHandler(LoaderValidationCallback);
            validationErrorOccured = false;
            enginesLoaded = false;
            XmlReader vr = XmlReader.Create(new XmlTextReader(configUrl), settings);
   doc.Load(vr);
   vr.Close();
   if(!validationErrorOccured)
   {
    XmlNodeReader reader = new XmlNodeReader(doc);
    XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(SearchEngines));
    SearchEngines mySearchEngines = (SearchEngines)serializer.Deserialize(reader);
    reader.Close();
    _engines = mySearchEngines;
    enginesLoaded = true;
    if (this.RepairedPhrasePlaceholders()) {
     using (Stream stream = NewsComponents.Utils.FileHelper.OpenForWrite(configUrl)) {
      this.SaveEngines(stream);
     }
    }
   }
  }
  private bool RepairedPhrasePlaceholders() {
   bool anyFound = false;
   if (EnginesOK) {
    foreach (SearchEngine se in Engines) {
     if (se.SearchLink.IndexOf("[PHRASE]") >= 0) {
      se.SearchLink = se.SearchLink.Replace("[PHRASE]", "{0}");
      anyFound = true;
     }
    }
   }
   return anyFound;
  }
  private void LoaderValidationCallback(object sender,
   ValidationEventArgs args)
  {
   if(args.Severity == System.Xml.Schema.XmlSeverityType.Warning) {
    _log.Info(@"searches\config.xml validation warning: " + args.Message);
   }
   else if(args.Severity == System.Xml.Schema.XmlSeverityType.Error) {
    validationErrorOccured = true;
    _log.Error(@"searches\config.xml validation error: " + args.Message);
    AppExceptions.ExceptionManager.Publish(args.Exception);
   }
  }
  public void SaveEngines(Stream configUrl)
  {
   XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(SearchEngines));
   if(_engines != null)
   {
    if(_engines.Engines == null)
    {
     _engines.Engines = new List<SearchEngine>();
    }
   }
   else
   {
    _engines = new SearchEngines();
   }
   TextWriter writer = new StreamWriter(configUrl);
   serializer.Serialize(writer, _engines);
   writer.Close();
  }
  public void GenerateDefaultEngines()
  {
   this.Clear();
   SearchEngine s1 = new SearchEngine();
   s1.Title = "Google";
   s1.SearchLink = @"http://www.google.com/search?sourceid=navclient&ie=UTF-8&oe=UTF-8&q={0}";
   s1.Description = "Search the web with Google...";
   s1.ImageName = "google.bmp";
   s1.IsActive = true;
   _engines.Engines.Add(s1);
   s1 = new SearchEngine();
   s1.Title = "Feedster";
   s1.SearchLink = @"http://www.feedster.com/search.php?hl=en&ie=ISO-8859-1&q={0}&btnG=Search&sort=date&type=rss";
   s1.Description = "Search RSS feeds with Feedster...";
   s1.ImageName = "feedster.gif";
   s1.IsActive = true;
   s1.ReturnRssResult = true;
   _engines.Engines.Add(s1);
   s1 = new SearchEngine();
   s1.Title = "MSN Search";
   s1.SearchLink = @"http://search.msn.com/results.aspx?q={0}&FORM=SMCRT&x=32&y=15";
   s1.Description = "Search the web with MSN Search...";
   s1.ImageName = "msn.ico";
   s1.IsActive = true;
   _engines.Engines.Add(s1);
   s1 = new SearchEngine();
   s1.Title = "Yahoo! News";
   s1.SearchLink = @"http://news.search.yahoo.com/news/rss?p={0}&ie=UTF-8";
   s1.Description = "Search news with Yahoo! News...";
   s1.ImageName = "yahoo.ico";
   s1.IsActive = true;
   s1.ReturnRssResult = true;
   _engines.Engines.Add(s1);
  }
  public void Clear() {
   _engines = new SearchEngines();
   _engines.Engines = new List<SearchEngine>();
   _engines.NewTabRequired = true;
   validationErrorOccured = false;
   enginesLoaded = true;
  }
 }
 [System.Xml.Serialization.XmlTypeAttribute(Namespace=RssBanditNamespace.SearchConfiguration)]
 [System.Xml.Serialization.XmlRootAttribute("searchConfiguration", Namespace=RssBanditNamespace.SearchConfiguration, IsNullable=false)]
 public class SearchEngines
 {
  [System.Xml.Serialization.XmlElementAttribute("engine", Type = typeof(SearchEngine), IsNullable = false)]
  public List<SearchEngine> Engines;
  [System.Xml.Serialization.XmlAttributeAttribute("open-newtab", DataType="boolean")]
  public bool NewTabRequired;
 }
 [System.Xml.Serialization.XmlTypeAttribute(Namespace=RssBanditNamespace.SearchConfiguration)]
 public class SearchEngine: ISearchEngine, ICloneable
 {
  private string title = String.Empty;
  [System.Xml.Serialization.XmlElementAttribute("title")]
  public string Title {
   get { return title; }
   set { title = value; }
  }
  private string searchLink = String.Empty;
  [System.Xml.Serialization.XmlElementAttribute("search-link", DataType="anyURI")]
  public string SearchLink {
   get { return searchLink; }
   set { searchLink = value; }
  }
  private string description = String.Empty;
  [System.Xml.Serialization.XmlElementAttribute("description")]
  public string Description {
   get { return description; }
   set { description = value; }
  }
  private string imageName = String.Empty;
  [System.Xml.Serialization.XmlElementAttribute("image-name")]
  public string ImageName {
   get { return imageName; }
   set { imageName = value; }
  }
  private bool isActive;
  [System.Xml.Serialization.XmlAttributeAttribute("active", DataType="boolean") ]
  public bool IsActive {
   get { return isActive; }
   set { isActive = value; }
  }
  private bool returnRssResult;
  [System.Xml.Serialization.XmlAttributeAttribute("rss-resultset", DataType="boolean" ), System.ComponentModel.DefaultValue(false) ]
  public bool ReturnRssResult {
   get { return returnRssResult; }
   set { returnRssResult = value; }
  }
  private bool mrergeRssResult;
  [System.Xml.Serialization.XmlAttributeAttribute("merge-with-local-resultset", DataType="boolean" ), System.ComponentModel.DefaultValue(false) ]
  public bool MergeRssResult {
   get { return mrergeRssResult; }
   set { mrergeRssResult = value; }
  }
  public object Clone() {
   SearchEngine se = new SearchEngine();
   se.Title = this.Title;
   se.SearchLink = this.SearchLink;
   se.Description = this.Description;
   se.ImageName = this.ImageName;
   se.IsActive = this.IsActive;
   se.ReturnRssResult = this.ReturnRssResult;
   se.MergeRssResult = this.MergeRssResult;
   return se;
  }
  public override string ToString() {
   if (this.Title != null) {
    if (this.Description != null)
     return String.Format("{0} ({1})", this.Title, this.Description);
    else
     return this.Title;
   } else
    return Resources.SR.GeneralNewItemText;
  }
 }
}
