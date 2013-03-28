using System;
using System.Collections;
using System.IO;
using System.Net;
using System.Security.Permissions;
using NewsComponents;
using NewsComponents.Feed;
using NewsComponents.Storage;
using NUnit.Framework;
namespace RssBandit.UnitTests
{
 [TestFixture]
 public class RssParserTests : BaseTestFixture
 {
  const string CACHE_DIR = UNPACK_DESTINATION + @"\Cache";
  const string FEEDS_DIR = UNPACK_DESTINATION;
  const string APP_NAME = "RssBanditUnitTests";
  const string BASE_URL = "http://127.0.0.1:8081/RssParserTestFiles/";
  [Test]
  public void TestLoadFeeds()
  {
   RssParser handler = new RssParser(APP_NAME);
   handler.LoadFeedlist(BASE_URL + "FeedList03Feeds.xml", null);
   Assertion.Assert("Feeds should be valid!", handler.FeedsListOK);
   Assertion.AssertEquals("FeedList03Feeds.xml should contain 3 feeds. Hence the name.", 3, handler.FeedsTable.Count);
   Assertion.AssertEquals("MSDN: Visual C#", handler.FeedsTable["http://msdn.microsoft.com/vcsharp/rss.xml"].title);
   Assertion.AssertEquals("ASP.NET Forums: Architecture", handler.FeedsTable["http://www.asp.net/Forums/rss.aspx?forumid=16"].title);
   Assertion.AssertEquals("Slashdot", handler.FeedsTable["http://slashdot.org/slashdot.rss"].title);
  }
  [Test, ExpectedException(typeof(WebException))]
  public void TestLoadNonExistentFeed()
  {
   RssParser handler = new RssParser(APP_NAME);
   handler.LoadFeedlist(BASE_URL + "ThisFeedDoesNotExist.xml", null);
  }
  [Test]
  public void TestImportFeedThatCausedKeyrefError()
  {
   RssParser handler = new RssParser(APP_NAME);
   using(Stream feedStream = base.GetResourceStream("WebRoot.RssParserTestFiles.KeyRefBugTest.xml"))
   {
    handler.ImportFeedlist(feedStream);
    feedStream.Close();
   }
  }
  [Test]
  public void TestImportFeedlist()
  {
   RssParser handler = new RssParser(APP_NAME);
   handler.LoadFeedlist(BASE_URL + "FeedList03Feeds.xml", null);
   Assertion.Assert("Feeds should be valid!", handler.FeedsListOK);
   Assertion.AssertEquals("FeedList03Feeds.xml should contain 3 feeds. Hence the name.", 3, handler.FeedsTable.Count);
   using(FileStream stream = File.OpenRead(Path.Combine(WEBROOT_PATH, @"RssParserTestFiles\FeedList04Feeds.xml")))
   {
    handler.ImportFeedlist(stream);
    stream.Close();
   }
   Assertion.AssertEquals("3 + 4 = 7.  7 Feeds expected.", 7, handler.FeedsTable.Count);
   Assertion.AssertEquals("MSDN: Visual C#", handler.FeedsTable["http://msdn.microsoft.com/vcsharp/rss.xml"].title);
   Assertion.AssertEquals("Development", handler.FeedsTable["http://msdn.microsoft.com/vcsharp/rss.xml"].category);
   Assertion.AssertEquals("ASP.NET Forums: Architecture", handler.FeedsTable["http://www.asp.net/Forums/rss.aspx?forumid=16"].title);
   Assertion.AssertEquals("Forums", handler.FeedsTable["http://www.asp.net/Forums/rss.aspx?forumid=16"].category);
   Assertion.AssertEquals("Slashdot", handler.FeedsTable["http://slashdot.org/slashdot.rss"].title);
   Assertion.AssertEquals("News Technology", handler.FeedsTable["http://slashdot.org/slashdot.rss"].category);
   Assertion.AssertEquals("Torsten's .NET Blog", handler.FeedsTable["http://www.rendelmann.info/blog/SyndicationService.asmx/GetRss"].title);
   Assertion.AssertEquals("Blogs", handler.FeedsTable["http://www.rendelmann.info/blog/SyndicationService.asmx/GetRss"].category);
   Assertion.AssertEquals("you've been HAACKED", handler.FeedsTable["http://haacked.com/Rss.aspx"].title);
   Assertion.AssertEquals("Blogs", handler.FeedsTable["http://haacked.com/Rss.aspx"].category);
   Assertion.AssertEquals("kuro5hin.org", handler.FeedsTable["http://www.kuro5hin.org/backend.rdf"].title);
   Assertion.AssertEquals("News Technology", handler.FeedsTable["http://www.kuro5hin.org/backend.rdf"].category);
   Assertion.AssertEquals("Dare Obasanjo aka Carnage4Life", handler.FeedsTable["http://www.25hoursaday.com/weblog/SyndicationService.asmx/GetRss"].title);
   Assertion.AssertEquals("Blogs Microsoft", handler.FeedsTable["http://www.25hoursaday.com/weblog/SyndicationService.asmx/GetRss"].category);
  }
  [Test]
  public void TestImportFeedlistCategory()
  {
   RssParser handler = new RssParser(APP_NAME);
   handler.LoadFeedlist(BASE_URL + "FeedList03Feeds.xml", null);
   Assertion.Assert("Feeds should be valid!", handler.FeedsListOK);
   Assertion.AssertEquals("FeedList03Feeds.xml should contain 3 feeds. Hence the name.", 3, handler.FeedsTable.Count);
   using(FileStream stream = File.OpenRead(Path.Combine(WEBROOT_PATH, @"RssParserTestFiles\FeedList04Feeds.xml")))
   {
    handler.ImportFeedlist(stream, "News Technology");
    stream.Close();
   }
   Assertion.AssertEquals("3 + 4 = 7.  7 Feeds expected.", 7, handler.FeedsTable.Count);
   Assertion.AssertEquals("MSDN: Visual C#", handler.FeedsTable["http://msdn.microsoft.com/vcsharp/rss.xml"].title);
   Assertion.AssertEquals("Development", handler.FeedsTable["http://msdn.microsoft.com/vcsharp/rss.xml"].category);
   Assertion.AssertEquals("ASP.NET Forums: Architecture", handler.FeedsTable["http://www.asp.net/Forums/rss.aspx?forumid=16"].title);
   Assertion.AssertEquals("Forums", handler.FeedsTable["http://www.asp.net/Forums/rss.aspx?forumid=16"].category);
   Assertion.AssertEquals("Slashdot", handler.FeedsTable["http://slashdot.org/slashdot.rss"].title);
   Assertion.AssertEquals("News Technology", handler.FeedsTable["http://slashdot.org/slashdot.rss"].category);
   Assertion.AssertEquals("Torsten's .NET Blog", handler.FeedsTable["http://www.rendelmann.info/blog/SyndicationService.asmx/GetRss"].title);
   Assertion.AssertEquals(@"News Technology\Blogs", handler.FeedsTable["http://www.rendelmann.info/blog/SyndicationService.asmx/GetRss"].title);
   Assertion.AssertEquals("you've been HAACKED", handler.FeedsTable["http://haacked.com/Rss.aspx"].title);
   Assertion.AssertEquals(@"News Technology\Blogs", handler.FeedsTable["http://haacked.com/Rss.aspx"].category);
   Assertion.AssertEquals("kuro5hin.org", handler.FeedsTable["http://www.kuro5hin.org/backend.rdf"].title);
   Assertion.AssertEquals(@"News Technology\News Technology", handler.FeedsTable["http://www.kuro5hin.org/backend.rdf"].category);
   Assertion.AssertEquals("Dare Obasanjo aka Carnage4Life", handler.FeedsTable["http://www.25hoursaday.com/weblog/SyndicationService.asmx/GetRss"].title);
   Assertion.AssertEquals(@"News Technology\Blogs Microsoft", handler.FeedsTable["http://www.25hoursaday.com/weblog/SyndicationService.asmx/GetRss"].category);
  }
  [Test]
  public void TestImportFeedListWithDuplicate()
  {
   RssParser handler = new RssParser(APP_NAME);
   handler.LoadFeedlist(BASE_URL + "FeedList03Feeds.xml", null);
   Assertion.Assert("Feeds should be valid!", handler.FeedsListOK);
   Assertion.AssertEquals("FeedList03Feeds.xml should contain 3 feeds. Hence the name.", 3, handler.FeedsTable.Count);
   using(FileStream stream = File.OpenRead(Path.Combine(WEBROOT_PATH, @"RssParserTestFiles\FeedListWithDuplicateFrom03.xml")))
   {
    handler.ImportFeedlist(stream);
    stream.Close();
   }
   Assertion.AssertEquals("3 + 1 = 4.  4 Feeds expected because one is a duplicate!", 4, handler.FeedsTable.Count);
   Assertion.AssertEquals("MSDN: Visual C#", handler.FeedsTable["http://msdn.microsoft.com/vcsharp/rss.xml"].title);
   Assertion.AssertEquals("Development", handler.FeedsTable["http://msdn.microsoft.com/vcsharp/rss.xml"].category);
   Assertion.AssertEquals("ASP.NET Forums: Architecture", handler.FeedsTable["http://www.asp.net/Forums/rss.aspx?forumid=16"].title);
   Assertion.AssertEquals("Forums", handler.FeedsTable["http://www.asp.net/Forums/rss.aspx?forumid=16"].category);
   Assertion.AssertEquals("Slashdot", handler.FeedsTable["http://slashdot.org/slashdot.rss"].title);
   Assertion.AssertEquals("News Technology", handler.FeedsTable["http://slashdot.org/slashdot.rss"].category);
   Assertion.AssertEquals("Channel 9", handler.FeedsTable["http://channel9.msdn.com/rss.aspx"].title);
   Assertion.AssertEquals("Microsoft", handler.FeedsTable["http://channel9.msdn.com/rss.aspx"].category);
  }
  [Test, FileIOPermission(SecurityAction.Demand)]
  public void TestRecentlyViewed()
  {
   CacheManager cache = new FileCacheManager(CACHE_DIR, TimeSpan.MaxValue);
   RssParser handler = new RssParser(APP_NAME, cache);
   handler.LoadFeedlist(BASE_URL + "LocalTestFeedList.xml", null);
   Assertion.Assert("Feeds should be valid!", handler.FeedsListOK);
   feedsFeed feed = handler.FeedsTable["http://localhost/RssHandlerTestFiles/LocalTestFeed.xml"];
   Assertion.AssertEquals("Rss Bandit Unit Test Feed", feed.title);
   Assertion.AssertEquals(1, feed.storiesrecentlyviewed.Count);
   ArrayList items = handler.GetItemsForFeed(feed);
   Assertion.AssertEquals(2, items.Count);
   NewsItem item = (NewsItem)items[0];
   Assertion.Assert(item.BeenRead);
   item = (NewsItem)items[1];
   Assertion.Assert(!item.BeenRead);
   handler.MarkAllCachedItemsAsRead(feed);
   handler.ApplyFeedModifications(feed.link);
   using(FileStream newFeedStream = File.OpenWrite(Path.Combine(WEBROOT_PATH, @"RssParserTestFiles\LocalTestFeedList_NEW.xml")))
   {
    newFeedStream.Close();
    Assertion.Assert(File.Exists(Path.Combine(WEBROOT_PATH, @"RssParserTestFiles\LocalTestFeedList_NEW.xml")));
   }
   handler = new RssParser(APP_NAME, cache);
   handler.LoadFeedlist(BASE_URL + "LocalTestFeedList_NEW.xml", null);
   feed = handler.FeedsTable["http://localhost/RssHandlerTestFiles/LocalTestFeed.xml"];
   Assertion.AssertEquals("Should be two now.", 2, feed.storiesrecentlyviewed.Count);
  }
  [Test]
  public void TestPostCommentViaCommentAPI()
  {
  }
  [SetUp]
  public void SetUp()
  {
   DeleteDirectory(UNPACK_DESTINATION);
   UnpackResourceDirectory("WebRoot.RssParserTestFiles");
   UnpackResourceDirectory("Cache");
   UnpackResourceDirectory("Expected");
   UnpackResourceDirectory("Settings");
   StartWebServer();
  }
  [TearDown]
  public void TearDown()
  {
   StopWebServer();
   DeleteDirectory(UNPACK_DESTINATION);
  }
 }
}
