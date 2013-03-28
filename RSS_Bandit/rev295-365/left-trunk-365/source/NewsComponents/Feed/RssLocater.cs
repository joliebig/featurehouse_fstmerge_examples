using System;
using System.Globalization;
using System.Text.RegularExpressions;
using System.Xml;
using System.Collections;
using System.IO;
using System.Net;
using System.Text;
using RssBandit.Common;
using NewsComponents.Net;
using NewsComponents.Utils;
namespace NewsComponents.Feed {
 public enum FeedLocationMethod{
  AutoDiscoverUrl,
  Syndic8Search,
 }
 public class RssLocater
 {
  private static readonly log4net.ILog _log = RssBandit.Common.Logging.Log.GetLogger(typeof(RssLocater));
  private string userAgent = FeedSource.DefaultUserAgent;
  private ICredentials credentials = null;
  public ICredentials Credentials{
   set{ credentials = value;}
   get { return credentials;}
  }
        private IWebProxy proxy = WebRequest.DefaultWebProxy;
  public IWebProxy Proxy{
   set{ proxy = value;}
   get { return proxy;}
  }
  private bool offline = false;
  public bool Offline
  {
   set { offline = value; }
   get { return offline; }
  }
  public static string UrlFromFeedProtocolUrl(string webUrl)
  {
   if (webUrl == null)
    return String.Empty;
   string retUrl = webUrl;
   if (retUrl == null)
    return String.Empty;
   if (retUrl.ToLower(CultureInfo.InvariantCulture).StartsWith("feed:"))
    retUrl = retUrl.Substring(5);
   if (retUrl.StartsWith("//"))
    retUrl = retUrl.Substring(2);
   try
   {
    new Uri(retUrl);
   }
   catch
   {
    if (!retUrl.ToLower(CultureInfo.InvariantCulture).StartsWith("http"))
    {
     if (retUrl.StartsWith("/"))
      retUrl = "http://" + retUrl.Substring(1);
     else
      retUrl = "http://" + retUrl;
    }
   }
   return retUrl;
  }
  public static ArrayList UrlsFromWellknownListener(string webUrl)
  {
   Uri url = null;
   ArrayList feedurls = new ArrayList();
   try {
    url = new Uri(webUrl);
   } catch {
    return feedurls;
   }
   if (url.IsLoopback) {
    string urlQuery = System.Web.HttpUtility.UrlDecode(url.Query);
    string urlQueryLowerCase = urlQuery.ToLower(CultureInfo.InvariantCulture);
    if (url.Port == 8888)
    {
     string urlpart = urlQuery.Substring(urlQuery.IndexOf("=")+1);
     urlpart = urlpart.Replace("&amp;go", "");
     urlpart = urlpart.Replace("&go", "");
     if (urlQueryLowerCase.StartsWith("?add_urls"))
     {
      feedurls.AddRange(urlpart.Split(new char[]{','}));
     }
     else if (urlQueryLowerCase.StartsWith("?add_url"))
     {
      feedurls.Add(urlpart);
     }
    }
    else if (url.Port == 5335 && urlQueryLowerCase.StartsWith("?url=")) {
     string urlpart = urlQuery.Substring(urlQuery.IndexOf("=")+1);
     feedurls.Add(urlpart);
    }
    else if (url.Port == 8666 && urlQueryLowerCase.StartsWith("?rss=")) {
     string urlpart = urlQuery.Substring(urlQuery.IndexOf("=")+1);
     feedurls.Add(urlpart);
    }
    else if (url.Port == 8900 && urlQueryLowerCase.StartsWith("?url=")) {
     string urlpart = urlQuery.Substring(urlQuery.IndexOf("=")+1);
     feedurls.Add(urlpart);
    }
    else if (url.Port == 7810 && urlQueryLowerCase.StartsWith("?action=")) {
     string urlpart = urlQuery.Substring(urlQuery.IndexOf("&")+1);
     urlpart = urlpart.Substring(urlpart.IndexOf("=")+1);
     feedurls.Add(urlpart);
    }
    else if (url.Port == 2604 && urlQueryLowerCase.StartsWith("?url=")) {
     string urlpart = urlQuery.Substring(urlQuery.IndexOf("=")+1);
     feedurls.Add(urlpart);
    }
   }
   return feedurls;
  }
  private string rpc_start = "<methodCall><methodName>";
  private string rpc_middle = "</methodName><params><param><value>";
  private string rpc_end = "</value></param></params></methodCall>";
  private string rpc_end2 = "</value></param><param><value><string>feedid</string></value></param><param><value><int>100</int></value></param></params></methodCall>";
  private RssLocater(){;}
  public RssLocater(IWebProxy p, string userAgent):
   this(p, userAgent, null) {
  }
  public RssLocater(IWebProxy p, string userAgent, ICredentials credentials){
   this.Proxy = p;
   if (!string.IsNullOrEmpty(userAgent))
    this.userAgent = userAgent;
   this.Credentials = credentials;
  }
  private Stream GetWebPage(string url)
  {
   return AsyncWebRequest.GetSyncResponseStream(url, this.Credentials, this.userAgent, this.Proxy);
  }
  public ArrayList GetRssFeedsForUrlContent(string url, string content, bool deepSearch)
  {
   ArrayList list = null;
   if(!url.ToLower().StartsWith("http")){
    url = "http://" + url;
   }
   list = GetRssFeedsFromXml(content, deepSearch, url);
   return list;
  }
  private ArrayList GetRssFeedsFromXml(string htmlContent, bool deepSearch, string url)
  {
   ArrayList list = new ArrayList();
   list.AddRange(GetRssAutoDiscoveryLinks(htmlContent, url));
   if(list.Count == 0){
    list.AddRange(GetLinksFromWellknownLocalListenersOrProtocol(htmlContent));
    if (list.Count == 0) {
     list.AddRange(GetLinksToInternalXmlFiles(htmlContent, url, FeedUrlSearchType.Extension));
     if(list.Count == 0){
      list.AddRange(GetLinksToInternalXmlFiles(htmlContent, url, FeedUrlSearchType.Anywhere));
      if(list.Count == 0){
       list.AddRange(GetLinksToExternalXmlFiles(htmlContent, url, FeedUrlSearchType.Extension));
       if(list.Count == 0)
       {
        list.AddRange(GetLinksToExternalXmlFiles(htmlContent, url, FeedUrlSearchType.Anywhere));
        if(list.Count == 0 && deepSearch)
        {
         try
         {
          list.AddRange(GetFeedsFromSyndic8(url));
         }
         catch (WebException){;}
        }
       }
      }
     }
    }
   }
   return list;
  }
  public ArrayList GetRssFeedsForUrl(string url, bool throwExceptions)
  {
   ArrayList list = null;
   if(!url.ToLower().StartsWith("http")){
    url = "http://" + url;
   }
   try
   {
    if (LooksLikeRssFeed(url))
     return new ArrayList(new string[]{url});
    list = GetRssFeedsFromXml(GetHtmlContent(url), false, url);
   }
   catch(WebException)
   {
    list = new ArrayList();
    if (throwExceptions)
     throw;
   }
   return list;
  }
  private string GetHtmlContent(string url)
  {
   string htmlContent = string.Empty;
   using(StreamReader reader = new StreamReader(this.GetWebPage(url)))
   {
    htmlContent = reader.ReadToEnd();
   }
   return htmlContent;
  }
  public ArrayList GetRssAutoDiscoveryLinks(string url)
  {
   return GetRssAutoDiscoveryLinks(GetHtmlContent(url), url);
  }
  public ArrayList GetRssAutoDiscoveryLinks(string htmlContent, string baseUri)
  {
   Regex autoDiscoverRegex = new Regex(autoDiscoverRegexPattern, RegexOptions.Compiled | RegexOptions.IgnoreCase);
   ArrayList list = new ArrayList();
   MatchCollection matches = autoDiscoverRegex.Matches(htmlContent);
   foreach(Match match in matches)
   {
    if((match.Value.ToLower().IndexOf("application/atom+xml") > 0)
     || (match.Value.ToLower().IndexOf("application/rss+xml") > 0))
    {
     string url = match.Groups["href"].Value;
     url = ConvertToAbsoluteUrl(url, baseUri);
     if(LooksLikeRssFeed(url))
     {
      if(!list.Contains(url))
       list.Add(url);
     }
    }
   }
   return list;
  }
  private ArrayList GetLinksFromWellknownLocalListenersOrProtocol(string htmlContent)
  {
   ArrayList list = new ArrayList();
   Regex regexfeedProtocolHrefRegex = new Regex(hrefFeedProtocolPattern, RegexOptions.Compiled | RegexOptions.IgnoreCase);
   MatchCollection matches = regexfeedProtocolHrefRegex.Matches(htmlContent);
   foreach(Match match in matches)
   {
    string url = UrlFromFeedProtocolUrl(match.Groups["href"].Value);
    if (url.Length > 0 && !list.Contains(url))
     list.Add(url);
   }
   if (list.Count > 0)
    return list;
   Regex hrefListenersRegex = new Regex(hrefListenersPattern, RegexOptions.Compiled | RegexOptions.IgnoreCase);
   matches = hrefListenersRegex.Matches(htmlContent);
   foreach(Match match in matches)
   {
    list.AddRange(UrlsFromWellknownListener(match.Groups["href"].Value));
   }
   return list;
  }
  private ArrayList GetLinksToInternalXmlFiles(string htmlContent, string baseUri, FeedUrlSearchType searchType)
  {
   ArrayList list = new ArrayList();
   MatchCollection matches = null;
   if(searchType == FeedUrlSearchType.Extension)
   {
    Regex feedExtensionLinkRegex = new Regex(hrefRegexFeedExtensionPattern, RegexOptions.Compiled | RegexOptions.IgnoreCase);
    matches = feedExtensionLinkRegex.Matches(htmlContent);
   }
   else
   {
    Regex feedUrlLinkRegex = new Regex(hrefRegexFeedUrlPattern, RegexOptions.Compiled | RegexOptions.IgnoreCase);
    matches = feedUrlLinkRegex.Matches(htmlContent);
   }
   foreach(Match match in matches)
   {
    string url = ConvertToAbsoluteUrl(match.Groups["href"].Value, baseUri);
    if(OnSameServer(baseUri, url) && LooksLikeRssFeed(url))
    {
     if (!list.Contains(url))
      list.Add(url);
    }
   }
   return list;
  }
  private ArrayList GetLinksToExternalXmlFiles(string htmlContent, string baseUri, FeedUrlSearchType searchType)
  {
   ArrayList list = new ArrayList();
   MatchCollection matches = null;
   if(searchType == FeedUrlSearchType.Extension)
   {
    Regex feedExtensionLinkRegex = new Regex(hrefRegexFeedExtensionPattern, RegexOptions.Compiled | RegexOptions.IgnoreCase);
    matches = feedExtensionLinkRegex.Matches(htmlContent);
   }
   else
   {
    Regex feedUrlLinkRegex = new Regex(hrefRegexFeedUrlPattern, RegexOptions.Compiled | RegexOptions.IgnoreCase);
    matches = feedUrlLinkRegex.Matches(htmlContent);
   }
   foreach(Match match in matches)
   {
    string url = ConvertToAbsoluteUrl(match.Groups["href"].Value, baseUri);
    if((!OnSameServer(baseUri, url)) && LooksLikeRssFeed(url))
    {
     if (!list.Contains(url))
      list.Add(url);
    }
   }
   return list;
  }
  private bool OnSameServer(string url1, string url2){
   Uri uri1 = new Uri(url1);
   Uri uri2 = new Uri(url2);
   if(uri1.Host.Equals(uri2.Host)){
    return true;
   }else{
    return false;
   }
  }
  private string ConvertToAbsoluteUrl(string url, string baseurl)
  {
   try
   {
    Uri uri = new Uri(url);
    return uri.CanonicalizedUri();
   }
   catch(UriFormatException)
   {
    try{
     Uri baseUri= new Uri(baseurl);
     return (new Uri(baseUri,url).CanonicalizedUri());
    }catch(UriFormatException){
     return "http://www.example.com";
    }
   }
  }
  private bool LooksLikeRssFeed(string url){
   XmlTextReader reader = null;
   try{
    reader = new XmlTextReader(this.GetWebPage(url));
    reader.XmlResolver = null;
    reader.MoveToContent();
    if((reader.LocalName == "rss") || (reader.LocalName == "RDF") || (reader.LocalName == "feed") ){
     return true;
    }else{
     return false;
    }
   }catch(Exception){
    return false;
   }finally{
    if(reader != null){
     reader.Close();
    }
   }
  }
  private HttpWebResponse SendRequestToSyndic8(string functionname, string param){
   string rpc_message = rpc_start + functionname + rpc_middle + param + (functionname.StartsWith("syndic8.FindF") ? rpc_end2 : rpc_end);
   Encoding enc = Encoding.UTF8, unicode = Encoding.Unicode;
   byte[] encBytes = Encoding.Convert(unicode, enc, unicode.GetBytes(rpc_message));
   HttpWebRequest request = (HttpWebRequest) WebRequest.Create("http://www.syndic8.com/xmlrpc.php");
   request.Timeout = 1 * 60 * 1000;
   request.Credentials = CredentialCache.DefaultCredentials;
   request.UserAgent = FeedSource.GlobalUserAgentString;
   request.Method = "POST";
   request.ContentType = "text/xml";
   request.Proxy = this.Proxy;
   request.Headers.Add("charset", "UTF-8");
   request.ContentLength = encBytes.Length;
   StreamWriter myWriter = null;
   try{
    myWriter = new StreamWriter(request.GetRequestStream());
    myWriter.Write(rpc_message);
   } catch(Exception e){
    throw new WebException(e.Message, e);
   }finally{
    if(myWriter != null){
     myWriter.Close();
    }
   }
   return (HttpWebResponse) request.GetResponse();
  }
  private string GetResponseString(HttpWebResponse response){
   StringBuilder sb = new StringBuilder();
   StringWriter writeStream = null;
   StreamReader readStream = null;
   writeStream = new StringWriter(sb);
   Stream receiveStream = response.GetResponseStream();
   Encoding encode = System.Text.Encoding.GetEncoding("utf-8");
   readStream = new StreamReader( receiveStream, encode );
   Char[] read = new Char[256];
   int count = readStream.Read( read, 0, 256 );
   while (count > 0) {
    writeStream.Write(read, 0, count);
    count = readStream.Read(read, 0, 256);
   }
   return sb.ToString();
  }
  public Hashtable GetFeedsFromSyndic8(string searchTerm, FeedLocationMethod locationMethod){
   string rpc_method_name = null;
   switch(locationMethod){
    case FeedLocationMethod.AutoDiscoverUrl:
     rpc_method_name = "syndic8.FindSites";
     break;
    case FeedLocationMethod.Syndic8Search:
     rpc_method_name = "syndic8.FindFeeds";
     break;
    default:
     rpc_method_name = "syndic8.FindSites";
     break;
   }
   HttpWebResponse response = SendRequestToSyndic8(rpc_method_name,
    "<DataURL>" + searchTerm + "</DataURL>");
   string syndic8response = GetResponseString(response);
   _log.Debug(syndic8response);
   XmlDocument doc = new XmlDocument();
   doc.LoadXml(syndic8response);
   StringBuilder requestArray = new StringBuilder("<array><data>");
   foreach(XmlNode node in doc.SelectNodes("//value/int")){
    requestArray.Append("<value><FeedID>");
    requestArray.Append(node.InnerText);
    requestArray.Append("</FeedID></value>");
   }
   requestArray.Append("</data></array>");
   response = SendRequestToSyndic8("syndic8.GetFeedInfo", requestArray.ToString());
   syndic8response = GetResponseString(response);
   Hashtable list = new Hashtable();
   try {
    _log.Debug(syndic8response);
    doc.LoadXml(syndic8response);
    foreach(XmlNode node in doc.SelectNodes("//member[name = 'feedid']")){
     XmlNode dataurl = node.ParentNode.SelectSingleNode("member[name = 'dataurl']/value/string");
     if (dataurl != null && dataurl.InnerText.Trim().Length > 0 && !list.ContainsKey(dataurl.InnerText)) {
      XmlNode sitename = node.ParentNode.SelectSingleNode("member[name = 'sitename']/value/string");
      XmlNode desc = node.ParentNode.SelectSingleNode("member[name = 'description']/value/string");
      XmlNode siteurl = node.ParentNode.SelectSingleNode("member[name = 'siteurl']/value/string");
      list.Add(dataurl.InnerText, new string[]{sitename.InnerText, desc.InnerText, siteurl.InnerText, dataurl.InnerText});
     }
    }
   }catch(XmlException){
   }
   return list;
  }
  public ArrayList GetFeedsFromSyndic8(string url){
   Hashtable ht = this.GetFeedsFromSyndic8(url, FeedLocationMethod.AutoDiscoverUrl);
   return new ArrayList(ht.Keys);
  }
  enum FeedUrlSearchType
  {
   Extension
   ,Anywhere
  }
  const string feedExtensionsPattern = "(xml|rdf|rss)";
  const string hrefRegexPattern = @"(\s+href\s*=\s*(?:""(?<href>[^""]*?)""|'(?<href>[^']*?)'|(?<href>[^'""<>\s]+)))";
  const string hrefRegexFeedExtensionPattern = @"(\s+href\s*=\s*(?:""(?<href>[^""]*?\." + feedExtensionsPattern + @")""|'(?<href>[^']*?\." + feedExtensionsPattern + @")'|(?<href>[^'""<>\s]+\." + feedExtensionsPattern + ")))";
  const string hrefRegexFeedUrlPattern = @"(\s+href\s*=\s*(?:""(?<href>[^""]*?" + feedExtensionsPattern + @"[^""]+)""|'(?<href>[^']*?" + feedExtensionsPattern + @"[^']+)'|(?<href>[^'""<>\s]*" + feedExtensionsPattern + @"[^'""<>\s]+)))";
  const string hrefFeedProtocolPattern = @"(\s+href\s*=\s*(?:""feed:(//)?(?<href>[^""]*?)""|'feed:(//)?(?<href>[^']*?)'|feed:(//)?(?<href>[^'""<>\s]+)))";
  const string hrefListenersPattern = @"(\s+href\s*=\s*(?:""(?<href>http://(127.0.0.1|localhost):[^""]*?)""|'(?<href>http://(127.0.0.1|localhost):[^']*?)'|(?<href>http://(127.0.0.1|localhost):[^'""<>\s]+)))";
  const string attributeRegexPattern = @"(\s+(?<attName>\w+)\s*=\s*(?:""(?<attVal>[^""]*?)""|'(?<attVal>[^']*?)'|(?<attVal>[^'""<>\s]+))?)";
  const string autoDiscoverRegexPattern = "<link(" + attributeRegexPattern + @"+|\s*)" + hrefRegexPattern + "(" + attributeRegexPattern + @"+|\s*)\s*/?>";
 }
}
