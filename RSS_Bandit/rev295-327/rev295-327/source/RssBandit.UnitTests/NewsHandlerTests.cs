using System; 
using System.Security.Permissions; 
using System.Collections; 
using System.IO; 
using System.Windows.Forms; 
using NewsComponents; 
using NewsComponents.Feed; 
using NewsComponents.Storage; 
using NUnit.Framework; namespace  RssBandit.UnitTests {
	
 [TestFixture] 
 public class  NewsHandlerTests  : CassiniHelperTestFixture {
		
  internal  const string BASE_URL = "http://127.0.0.1:8081/NewsHandlerTestFiles/"; 
  string _cacheDirectory = string.Empty;
 
  [Test, Ignore("Unit Tests should be fully automated and not require user intervention.")] 
  public  void LoadFeedFromFile()
  {
   NewsHandler handler = new NewsHandler(APP_NAME);
   OpenFileDialog dlg = new OpenFileDialog();
   dlg.CheckFileExists = true;
   dlg.Title = "Select a RSS/ATOM feed file (XML)";
   dlg.InitialDirectory = FEEDS_DIR;
   if (DialogResult.OK == dlg.ShowDialog())
   {
    Uri feedUri = new Uri(dlg.FileName);
    string feedUrl = feedUri.ToString();
    IFeedDetails fi = handler.GetFeedInfo(feedUrl);
    Assert.IsNotNull(fi, "GetFeedInfo failed");
    handler.GetItemsForFeed(feedUrl, true);
   }
  }
 
  [Test, ExpectedException(typeof(System.Net.WebException))] 
  public  void LoadNonExistentEnsureExceptionThrown()
  {
   NewsHandler handler = new NewsHandler(APP_NAME);
   handler.LoadFeedlist(BASE_URL + "ThisFeedDoesNotExist.xml", null);
  }
 
  [Test] 
  public  void LoadFeedListWithThreeFeeds()
  {
   NewsHandler handler = CreateNewsHandlerWithFeedList();
   AssertFeedTitle("MSDN: Visual C#", "http://msdn.microsoft.com/vcsharp/rss.xml", handler);
   AssertFeedTitle("ASP.NET Forums: Architecture", "http://www.asp.net/Forums/rss.aspx?forumid=16", handler);
   AssertFeedTitle("Slashdot", "http://slashdot.org/slashdot.rss", handler);
  }
 
  private  void AssertFeedTitle(string expectedTitle, string feedUrl, NewsHandler handler)
  {
   Assert.AreEqual(expectedTitle, handler.FeedsTable[feedUrl].title);
  }
 
  private  void AssertFeedCategory(string expectedCategory, string feedUrl, NewsHandler handler)
  {
   Assert.AreEqual(expectedCategory, handler.FeedsTable[feedUrl].category);
  }
 
  private  NewsHandler CreateNewsHandlerWithFeedList()
  {
   NewsHandler handler = new NewsHandler(APP_NAME);
   handler.LoadFeedlist(new FileStream(WEBROOT_PATH + @"\NewsHandlerTestFiles\FeedList03Feeds.xml", FileMode.Open), null);
   Assert.IsTrue(handler.FeedsListOK, "Feeds should be valid!");
   Assert.AreEqual(3, handler.FeedsTable.Count, "FeedList03Feeds.xml should contain 3 feeds. Hence the name");
   return handler;
  }
 
  [Test] 
  public  void ImportTwoFeedlists()
  {
   NewsHandler handler = CreateNewsHandlerWithFeedList();
   using(FileStream stream = File.OpenRead(Path.Combine(WEBROOT_PATH, @"NewsHandlerTestFiles\FeedList04Feeds.xml")))
   {
    handler.ImportFeedlist(stream);
    stream.Close();
   }
   Assert.AreEqual(7, handler.FeedsTable.Count, "3 + 4 = 7.  7 Feeds expected.");
   AssertFeedTitle("MSDN: Visual C#", "http://msdn.microsoft.com/vcsharp/rss.xml", handler);
   AssertFeedCategory("Development", "http://msdn.microsoft.com/vcsharp/rss.xml", handler);
   AssertFeedTitle("ASP.NET Forums: Architecture", "http://www.asp.net/Forums/rss.aspx?forumid=16", handler);
   AssertFeedCategory("Forums", "http://www.asp.net/Forums/rss.aspx?forumid=16", handler);
   AssertFeedTitle("Slashdot", "http://slashdot.org/slashdot.rss", handler);
   AssertFeedCategory("News Technology", "http://slashdot.org/slashdot.rss", handler);
   AssertFeedTitle("Torsten's .NET Blog", "http://www.rendelmann.info/blog/SyndicationService.asmx/GetRss", handler);
   AssertFeedCategory("Blogs", "http://www.rendelmann.info/blog/SyndicationService.asmx/GetRss", handler);
   AssertFeedTitle("you've been HAACKED", "http://haacked.com/Rss.aspx", handler);
   AssertFeedCategory("Blogs", "http://haacked.com/Rss.aspx", handler);
   AssertFeedTitle("kuro5hin.org", "http://www.kuro5hin.org/backend.rdf", handler);
   AssertFeedCategory("News Technology", "http://www.kuro5hin.org/backend.rdf", handler);
   AssertFeedTitle("Dare Obasanjo aka Carnage4Life", "http://www.25hoursaday.com/weblog/SyndicationService.asmx/GetRss", handler);
   AssertFeedCategory("Blogs Microsoft", "http://www.25hoursaday.com/weblog/SyndicationService.asmx/GetRss", handler);
  }
 
  [Test] 
  public  void ImportFeedListCategoryItemsAreImported()
  {
   NewsHandler handler = CreateNewsHandlerWithFeedList();
   using(FileStream stream = File.OpenRead(Path.Combine(WEBROOT_PATH, @"NewsHandlerTestFiles\FeedList04Feeds.xml")))
   {
    handler.ImportFeedlist(stream, "News Technology");
    stream.Close();
   }
   Assert.AreEqual(7, handler.FeedsTable.Count, "3 + 4 = 7.  7 Feeds expected.");
   AssertFeedTitle("MSDN: Visual C#", "http://msdn.microsoft.com/vcsharp/rss.xml", handler);
   AssertFeedCategory("Development", "http://msdn.microsoft.com/vcsharp/rss.xml", handler);
   AssertFeedTitle("ASP.NET Forums: Architecture", "http://www.asp.net/Forums/rss.aspx?forumid=16", handler);
   AssertFeedCategory("Forums", "http://www.asp.net/Forums/rss.aspx?forumid=16", handler);
   AssertFeedTitle("Slashdot", "http://slashdot.org/slashdot.rss", handler);
   AssertFeedCategory("News Technology", "http://slashdot.org/slashdot.rss", handler);
   AssertFeedTitle("Torsten's .NET Blog", "http://www.rendelmann.info/blog/SyndicationService.asmx/GetRss", handler);
   AssertFeedCategory(@"News Technology\Blogs", "http://www.rendelmann.info/blog/SyndicationService.asmx/GetRss", handler);
   AssertFeedTitle("you've been HAACKED", "http://haacked.com/Rss.aspx", handler);
   AssertFeedCategory(@"News Technology\Blogs", "http://haacked.com/Rss.aspx", handler);
   AssertFeedTitle("kuro5hin.org", "http://www.kuro5hin.org/backend.rdf", handler);
   AssertFeedCategory(@"News Technology\News Technology", "http://www.kuro5hin.org/backend.rdf", handler);
   AssertFeedTitle("Dare Obasanjo aka Carnage4Life", "http://www.25hoursaday.com/weblog/SyndicationService.asmx/GetRss", handler);
   AssertFeedCategory(@"News Technology\Blogs Microsoft", "http://www.25hoursaday.com/weblog/SyndicationService.asmx/GetRss", handler);
  }
 
  [Test, ExpectedException(typeof(ArgumentNullException))] 
  public  void ApplyFeedModificationsThrowsArgumentNullException()
  {
   NewsHandler handler = CreateNewsHandlerWithFeedList();
   handler.ApplyFeedModifications(null);
  }
 
  [Test] 
  public  void ImportFeedListDuplicateFeedIgnored()
  {
   NewsHandler handler = CreateNewsHandlerWithFeedList();
   using(FileStream stream = File.OpenRead(Path.Combine(WEBROOT_PATH, @"NewsHandlerTestFiles\FeedListWithDuplicateFrom03.xml")))
   {
    handler.ImportFeedlist(stream);
    stream.Close();
   }
   Assertion.AssertEquals("3 + 1 = 4.  4 Feeds expected because one is a duplicate!", 4, handler.FeedsTable.Count);
   AssertFeedTitle("MSDN: Visual C#", "http://msdn.microsoft.com/vcsharp/rss.xml", handler);
   AssertFeedCategory("Development", "http://msdn.microsoft.com/vcsharp/rss.xml", handler);
   AssertFeedTitle("ASP.NET Forums: Architecture", "http://www.asp.net/Forums/rss.aspx?forumid=16", handler);
   AssertFeedCategory("Forums", "http://www.asp.net/Forums/rss.aspx?forumid=16", handler);
   AssertFeedTitle("Slashdot", "http://slashdot.org/slashdot.rss", handler);
   AssertFeedCategory("News Technology", "http://slashdot.org/slashdot.rss", handler);
   AssertFeedTitle("Channel 9", "http://channel9.msdn.com/rss.aspx", handler);
   AssertFeedCategory("Microsoft", "http://channel9.msdn.com/rss.aspx", handler);
  }
 
  [Test] 
  public  void MarkAllCachedItemsAsReadPlacesItemsIntoTheRecentlyViewedCount()
  {
   FileCacheManager cache = new FileCacheManager(Path.Combine(_cacheDirectory, "Cache"));
   NewsHandler handler = new NewsHandler(APP_NAME, cache);
   handler.MaxItemAge = TimeSpan.MaxValue.Subtract(TimeSpan.FromDays(1));
   handler.LoadFeedlist(new FileStream(WEBROOT_PATH + @"\NewsHandlerTestFiles\LocalTestFeedList.xml", FileMode.Open), null);
   Assert.IsTrue(handler.FeedsListOK, "Feeds should be valid!");
   feedsFeed feed = handler.FeedsTable[BASE_URL + "LocalTestFeed.xml"];
   IFeedDetails feedInfo = handler.GetFeedInfo(feed.link);
   Assert.IsNotNull(feedInfo);
   Console.WriteLine("CACHEURL: " + feed.cacheurl);
   handler.ApplyFeedModifications(feed.link);
   Assert.AreEqual(feed.title, "Rss Bandit Unit Test Feed");
   Assert.AreEqual(1, feed.storiesrecentlyviewed.Count);
   ArrayList items = handler.GetItemsForFeed(feed);
   Assert.AreEqual(2, items.Count);
   NewsItem item = (NewsItem)items[0];
   Assertion.Assert(item.BeenRead);
   item = (NewsItem)items[1];
   Assertion.Assert(!item.BeenRead);
   handler.MarkAllCachedItemsAsRead(feed);
   handler.ApplyFeedModifications(feed.link);
   using(FileStream newFeedStream = File.OpenWrite(Path.Combine(WEBROOT_PATH, @"NewsHandlerTestFiles\LocalTestFeedList_NEW.xml")))
   {
    handler.SaveFeedList(newFeedStream, FeedListFormat.NewsHandler);
    newFeedStream.Close();
    Assertion.Assert(File.Exists(Path.Combine(WEBROOT_PATH, @"NewsHandlerTestFiles\LocalTestFeedList_NEW.xml")));
   }
   handler = new NewsHandler(APP_NAME);
   handler.LoadFeedlist(BASE_URL + "LocalTestFeedList_NEW.xml", null);
   feed = handler.FeedsTable.GetByIndex(0);
   Assertion.AssertEquals("Should be two now.", 2, feed.storiesrecentlyviewed.Count);
  }
 
  [Test] 
  public  void PostCommentViaCommentAPI()
  {
  }
 
  [SetUp] 
  protected override  void SetUp()
  {
   _cacheDirectory = NewsHandler.GetUserPath(APP_NAME);
   DeleteDirectory(UNPACK_DESTINATION);
   UnpackResourceDirectory("WebRoot.RssLocaterTestFiles");
   UnpackResourceDirectory("WebRoot.NewsHandlerTestFiles");
   UnpackResourceDirectory("Cache", new DirectoryInfo(_cacheDirectory));
   UnpackResourceDirectory("Expected");
   UnpackResourceDirectory("Settings");
   base.SetUp();
  }
 
  [TearDown] 
  protected override  void TearDown()
  {
   base.TearDown();
   DeleteDirectory(UNPACK_DESTINATION);
   if(_cacheDirectory.Length > 0)
    DeleteDirectory(_cacheDirectory);
  }

	}

}
