using System;
using System.Collections;
using System.Net;
using NewsComponents.Feed;
using NUnit.Framework;
namespace RssBandit.UnitTests
{
 [TestFixture]
 public class RssLocaterTests : CassiniHelperTestFixture
 {
  const string BASE_URL = "http://127.0.0.1:8081/RssLocaterTestFiles/";
  [Test, Ignore("Auto discovery of ATOM 0.3 Not yet implemented")]
  public void GetRssAutoDiscoveryLinksFindsAtomLinks()
  {
   RssLocater locater = new RssLocater();
   ArrayList feeds = locater.GetRssAutoDiscoveryLinks(BASE_URL + "PageWithAtomLinks.htm");
   Assert.AreEqual(1, feeds.Count, "Hmm, the page only has one feed and I couldn't find it..");
   Assert.AreEqual(BASE_URL + "SampleATOM0.3Feed.xml", feeds[0], "ATOM was too small to discover.");
  }
  [Test]
  public void GetRssAutoDiscoveryLinksFindsRssLinks()
  {
   RssLocater locater = new RssLocater();
   ArrayList feeds = locater.GetRssAutoDiscoveryLinks(BASE_URL + "GetRssAutoDiscoveryLinks.html");
   Assert.AreEqual(4, feeds.Count, "Obviously I could not count as I expected 4 feeds.");
   Assert.AreEqual(BASE_URL + "SampleRss0.91Feed.xml", (string)feeds[0]);
   Assert.AreEqual(BASE_URL + "SampleRss0.92Feed.rss", (string)feeds[1]);
   Assert.AreEqual(BASE_URL + "SampleRss1.0Feed.rss", (string)feeds[2]);
   Assertion.AssertEquals("Missing version 2.0. Stuck in the past.", BASE_URL + "SampleRss2.0Feed.xml", (string)feeds[3]);
   feeds = locater.GetRssFeedsForUrl(BASE_URL + "AutoDiscovery1.htm");
   Assert.AreEqual(feeds.Count, 2);
  }
  [Test]
  public void GetRssFeedsForUrlFindsFeedProtocolUrls()
  {
   RssLocater locater = new RssLocater();
   ArrayList feeds = locater.GetRssFeedsForUrl(BASE_URL + "feedProtocol.htm");
   Assert.AreEqual(3, feeds.Count);
   Assert.AreEqual(BASE_URL + "SampleRss0.91Feed.xml", feeds[0].ToString());
   Assert.AreEqual(BASE_URL + "SampleRss0.92Feed.rss", feeds[1].ToString());
   Assert.AreEqual(BASE_URL + "SampleRss2.0Feed.xml", feeds[2].ToString());
  }
  [Test]
  public void GetRssFeedsForUrlFindsLinksToWellKnownListeners()
  {
   RssLocater locater = new RssLocater();
   ArrayList feeds = locater.GetRssFeedsForUrl(BASE_URL + "localListeners.htm");
   Assert.AreEqual(8, feeds.Count);
   Assert.AreEqual(BASE_URL + "SampleRss0.91Feed.xml", feeds[0].ToString());
   Assert.AreEqual(BASE_URL + "SampleRss0.92Feed.rss", feeds[1].ToString());
   Assert.AreEqual(BASE_URL + "SampleRss1.0Feed.rss", feeds[2].ToString());
   Assert.AreEqual(BASE_URL + "SampleRss2.0Feed.xml", feeds[3].ToString());
   Assert.AreEqual(BASE_URL + "SampleFeed001Rss2.0.rss", feeds[4].ToString());
   Assert.AreEqual(BASE_URL + "SampleFeed002Rss2.0.rss", feeds[5].ToString());
   Assert.AreEqual(BASE_URL + "SampleFeed003Rss2.0.rss", feeds[6].ToString());
   Assert.AreEqual(BASE_URL + "SampleFeed004Rss2.0.rss", feeds[7].ToString());
  }
  [Test]
  public void GetRssFeedsForUrlFindsAtomAndRssFeeds()
  {
   RssLocater locater = new RssLocater();
   ArrayList feeds = locater.GetRssFeedsForUrl(BASE_URL + "GetRssFeedsForUrl.html");
   Assert.AreEqual(4, feeds.Count);
   Assert.AreEqual(BASE_URL + "SampleRss0.91Feed.xml", feeds[0].ToString());
   Assert.AreEqual(BASE_URL + "SampleRss0.92Feed.rss", feeds[1].ToString());
   Assert.AreEqual(BASE_URL + "SampleRss1.0Feed.rss", feeds[2].ToString());
   Assert.AreEqual(BASE_URL + "SampleRss2.0Feed.xml", feeds[3].ToString());
   feeds = locater.GetRssFeedsForUrl(BASE_URL + "LinksToExternalFeed.htm");
   Assertion.AssertEquals(1, feeds.Count);
  }
  [Test, ExpectedException(typeof(WebException))]
  public void GetRssAutoDiscoveryLinksThrowsWebExceptionIfFileNotFound()
  {
   RssLocater locater = new RssLocater();
   locater.GetRssAutoDiscoveryLinks("http://127.0.0.1:8081/RssLocaterTestFiles/FileNotFound.html");
  }
  [Test]
  public void GetRssFeedsForUrlDoesNotThrowExceptionWhen404Encountered()
  {
   RssLocater locater = new RssLocater();
   ArrayList feeds = locater.GetRssFeedsForUrl("http://127.0.0.1:8081/RssLocaterTestFiles/FileNotFound.html");
   Assert.AreEqual(0, feeds.Count);
  }
  [Test]
  public void GetRssFeedsForUrlDoesNotFindPhantomFeeds()
  {
   RssLocater locater = new RssLocater();
   ArrayList feeds = locater.GetRssFeedsForUrl("http://127.0.0.1:8081/RssLocaterTestFiles/NoFeeds.html");
   Assert.AreEqual(0, feeds.Count, "Impossibly, we found a feed where there are none.");
  }
  [Test]
  public void GetRssAutoDiscoveryLinksDoesNotFindPhantomFeeds()
  {
   RssLocater locater = new RssLocater();
   ArrayList feeds = locater.GetRssAutoDiscoveryLinks("http://127.0.0.1:8081/RssLocaterTestFiles/NoFeeds.html");
   Assert.AreEqual(0, feeds.Count, "Impossibly, we auto-discovered a feed where there are none.");
  }
  [Test]
  public void GetRssFeedsForUrlDoesNotThrowExceptionWhenWebServerDown()
  {
   ArrayList feeds;
   try
   {
    StopWebServer();
    RssLocater locater = new RssLocater();
    feeds = locater.GetRssFeedsForUrl("http://127.0.0.1:8081/RssLocaterTestFiles/NoFeeds.html");
    Assert.AreEqual(0, feeds.Count, "Impossibly we found feeds where there are none.");
   }
   finally
   {
    StartWebServer();
   }
  }
  [Test, ExpectedException(typeof(System.Net.WebException))]
  public void GetRssAutoDiscoveryLinksThrowsExceptionIfWebServerIsDown()
  {
   try
   {
    StopWebServer();
    RssLocater locater = new RssLocater();
    locater.GetRssAutoDiscoveryLinks("http://127.0.0.1:8081/RssLocaterTestFiles/NoFeeds.html");
   }
   finally
   {
    StartWebServer();
   }
  }
  [SetUp]
  protected override void SetUp()
  {
   Console.WriteLine("SetupTestFixture");
   DeleteDirectory(UNPACK_DESTINATION);
   UnpackResourceDirectory("WebRoot.RssLocaterTestFiles");
   base.SetUp();
  }
  [TearDown]
  protected override void TearDown()
  {
   base.TearDown();
   DeleteDirectory(UNPACK_DESTINATION);
  }
 }
}
