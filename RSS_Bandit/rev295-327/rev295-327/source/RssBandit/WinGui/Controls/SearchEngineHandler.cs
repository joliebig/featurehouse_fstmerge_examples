using System; 
using System.IO; 
using System.Text; 
using System.Xml; 
using System.Xml.Xsl; 
using System.Xml.Schema; 
using System.Xml.Serialization; 
using System.Collections; namespace  RssBandit.WebSearch {
	
 public class  SearchEngineHandler {
		
  public  SearchEngineHandler()
  {
   this.LoadSearchConfigSchema();
  }
 
  private  SearchEngines _engines = null;
 
  private  bool validationErrorOccured = false;
 
  private  bool enginesLoaded = false;
 
  private  XmlSchema searchConfigSchema = null;
 
  public  bool EnginesOK
  {
   get { return !validationErrorOccured; }
  }
 
  public  bool EnginesLoaded {
   get { return enginesLoaded; }
  }
 
  public  bool NewTabRequired
  {
   get
   {
    if(_engines == null)
     _engines = new SearchEngines();
    if(_engines.Engines== null)
     _engines.Engines = new ArrayList();
    return _engines.NewTabRequired;
   }
   set
   {
    if(_engines == null)
     _engines = new SearchEngines();
    if(_engines.Engines== null)
     _engines.Engines = new ArrayList();
    _engines.NewTabRequired = value;
   }
  }
 
  public  ArrayList Engines
  {
   get
   {
    if(_engines == null)
     _engines = new SearchEngines();
    if(_engines.Engines== null)
     _engines.Engines = new ArrayList();
    return _engines.Engines;
   }
  }
 
  private  void LoadSearchConfigSchema() {
   using (Stream stream = Resource.Manager.GetStream("Resources.SearchEnginesConfig.xsd")) {
    searchConfigSchema = XmlSchema.Read(stream, null);
   }
  }
 
  public  void LoadEngines(string configUrl, ValidationEventHandler veh) {
   XmlDocument doc = new XmlDocument();
   XmlValidatingReader vr = new XmlValidatingReader(new XmlTextReader(configUrl));
   vr.Schemas.Add(searchConfigSchema);
   vr.ValidationType = ValidationType.Schema;
   vr.ValidationEventHandler += veh;
   vr.ValidationEventHandler += new ValidationEventHandler(LoaderValidationCallback);
   validationErrorOccured = false;
   enginesLoaded = false;
   doc.Load(vr);
   vr.Close();
   if(!validationErrorOccured)
   {
    XmlNodeReader reader = new XmlNodeReader(doc);
    XmlSerializer serializer = new XmlSerializer(typeof(SearchEngines));
    SearchEngines mySearchEngines = (SearchEngines)serializer.Deserialize(reader);
    reader.Close();
    _engines = mySearchEngines;
    enginesLoaded = true;
    if (this.RepairedPhrasePlaceholders()) {
     using (Stream stream = RssComponents.FileUtils.OpenForWrite(configUrl)) {
      this.SaveEngines(stream);
     }
    }
   }
  }
 
  private  bool RepairedPhrasePlaceholders() {
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
 
  private  void LoaderValidationCallback(object sender,
   ValidationEventArgs args)
  {
   if(args.Severity == XmlSeverityType.Error)
    validationErrorOccured = true;
  }
 
  public  void SaveEngines(Stream configUrl)
  {
   XmlSerializer serializer = new XmlSerializer(typeof(SearchEngines));
   if(_engines != null)
   {
    if(_engines.Engines == null)
    {
     _engines.Engines = new ArrayList();
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
 
  public  void GenerateDefaultEngines()
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
   s1.Description = "Search RSS with Feedster...";
   s1.ImageName = "feedster.bmp";
   s1.IsActive = true;
   s1.ReturnRssResult = true;
   _engines.Engines.Add(s1);
   s1 = new SearchEngine();
   s1.Title = "msn.com";
   s1.SearchLink = @"http://search.msn.com/results.aspx?q={0}&FORM=SMCRT&x=32&y=15";
   s1.Description = "Search the web with msn.com...";
   s1.ImageName = "msn.bmp";
   s1.IsActive = true;
   _engines.Engines.Add(s1);
  }
 
  public  void Clear() {
   _engines = new SearchEngines();
   _engines.Engines = new ArrayList();
   _engines.NewTabRequired = true;
   validationErrorOccured = false;
   enginesLoaded = true;
  }

	}
	
 [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.25hoursaday.com/2003/RSSBandit/searchConfiguration/")]
 [System.Xml.Serialization.XmlRootAttribute("searchConfiguration", Namespace="http://www.25hoursaday.com/2003/RSSBandit/searchConfiguration/", IsNullable=false)] 
 public class  SearchEngines {
		
  [System.Xml.Serialization.XmlElementAttribute("engine", Type = typeof(SearchEngine), IsNullable = false)] 
  public  ArrayList Engines;
 
  [System.Xml.Serialization.XmlAttributeAttribute("open-newtab", DataType="boolean")] 
  public  bool NewTabRequired;

	}
	
 [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.25hoursaday.com/2003/RSSBandit/searchConfiguration/")] 
 public class  SearchEngine : ICloneable {
		
  [System.Xml.Serialization.XmlElementAttribute("title")] 
  public  string Title;
 
  [System.Xml.Serialization.XmlElementAttribute("search-link", DataType="anyURI")] 
  public  string SearchLink;
 
  [System.Xml.Serialization.XmlElementAttribute("description")] 
  public  string Description;
 
  [System.Xml.Serialization.XmlElementAttribute("image-name")] 
  public  string ImageName;
 
  [System.Xml.Serialization.XmlAttributeAttribute("active", DataType="boolean") ] 
  public  bool IsActive;
 
  [System.Xml.Serialization.XmlAttributeAttribute("rss-resultset", DataType="boolean" ), System.ComponentModel.DefaultValue(false) ] 
  public  bool ReturnRssResult;
 
  [System.Xml.Serialization.XmlAttributeAttribute("merge-with-local-resultset", DataType="boolean" ), System.ComponentModel.DefaultValue(false) ] 
  public  bool MergeRssResult;
 
  public  object Clone() {
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
 
  public override  string ToString() {
   if (this.Title != null) {
    if (this.Description != null)
     return String.Format("{0} ({1})", this.Title, this.Description);
    else
     return this.Title;
   } else
    return Resource.Manager["RES_GeneralNewItemText"];
  }

	}

}
