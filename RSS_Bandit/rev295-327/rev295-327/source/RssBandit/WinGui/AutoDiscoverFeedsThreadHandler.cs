using System; 
using System.IO; 
using System.Net; 
using System.Threading; 
using System.Collections; 
using NewsComponents; 
using NewsComponents.Feed; 
using NewsComponents.Net; 
using NewsComponents.Utils; 
using RssBandit.Resources; namespace  RssBandit.WinGui {
	
 public class  AutoDiscoverFeedsThreadHandler : EntertainmentThreadHandlerBase {
		
  private  string webPageUrl = String.Empty;
 
  private  string searchTerms = String.Empty;
 
  private  FeedLocationMethod locationMethod = FeedLocationMethod.AutoDiscoverUrl;
 
  private  Hashtable discoveredFeeds = null;
 
  private  IWebProxy proxy;
 
  private  ICredentials credentials = CredentialCache.DefaultCredentials;
 
  public  AutoDiscoverFeedsThreadHandler():base() {;}
 
  public  IWebProxy Proxy {
   get { return proxy; }
   set { proxy = value; }
  }
 
  public  ICredentials Credentials {
   get { return credentials; }
   set { credentials = value; }
  }
 
  public  string WebPageUrl {
   get { return webPageUrl; }
   set { webPageUrl = value; }
  }
 
  public  string SearchTerms {
   get { return searchTerms; }
   set { searchTerms = value; }
  }
 
  public  FeedLocationMethod LocationMethod {
   get { return locationMethod; }
   set { locationMethod = value; }
  }
 
  public  Hashtable DiscoveredFeeds {
   get { return discoveredFeeds; }
  }
 
  protected override  void Run() {
   RssLocater locator = new RssLocater(proxy, RssBanditApplication.UserAgent, this.Credentials);
   ArrayList arrFeedUrls = null;
   Hashtable htFeedUrls = null;
   try {
    if (locationMethod == FeedLocationMethod.AutoDiscoverUrl) {
     arrFeedUrls = locator.GetRssFeedsForUrl(webPageUrl, true);
     htFeedUrls = new Hashtable(arrFeedUrls.Count);
     foreach (string rssurl in arrFeedUrls) {
      NewsFeed discoveredFeed = new NewsFeed();
      discoveredFeed.link = rssurl;
      IFeedDetails feedInfo = null;
      try {
       feedInfo = NewsHandler.GetItemsForFeed(discoveredFeed, this.GetWebResponseStream(rssurl), false);
      } catch (Exception) {
       feedInfo = FeedInfo.Empty;
      }
      htFeedUrls.Add (rssurl, new string[]{(!string.IsNullOrEmpty(feedInfo.Title) ? feedInfo.Title: SR.AutoDiscoveredDefaultTitle), feedInfo.Description, feedInfo.Link, rssurl} );
     }
    } else {
     htFeedUrls = locator.GetFeedsFromSyndic8(searchTerms, locationMethod);
    }
   } catch (ThreadAbortException) {
   } catch (System.Net.WebException wex) {
    p_operationException = wex;
    htFeedUrls = new Hashtable();
   } catch (Exception e) {
    p_operationException = e;
    htFeedUrls = new Hashtable();
   } finally {
    discoveredFeeds = htFeedUrls;
    WorkDone.Set();
   }
  }
 
  private  Stream GetWebResponseStream(string url) {
   return GetWebResponseStream(url, this.Credentials);
  }
 
  private  Stream GetWebResponseStream(string url, ICredentials credentials) {
   return AsyncWebRequest.GetSyncResponseStream(url, credentials, RssBanditApplication.UserAgent, this.Proxy);
  }

	}

}
