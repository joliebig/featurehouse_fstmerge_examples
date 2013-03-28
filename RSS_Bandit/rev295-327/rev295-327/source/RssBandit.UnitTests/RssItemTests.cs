using System; 
using NewsComponents; 
using NewsComponents.Feed; 
using NewsComponents.Utils; 
using NUnit.Framework; namespace  RssBandit.UnitTests {
	
 [TestFixture] 
 public class  RssItemTests  : BaseTestFixture {
		
  [Test] 
  public  void DateTimeExtEnsureFormats()
  {
   DateTime dtUTCTime = new DateTime(2004, 6, 5, 5, 20, 9,0);
   DateTime dtLocalTime = TimeZone.CurrentTimeZone.ToLocalTime(dtUTCTime);
   if (!TimeZone.CurrentTimeZone.IsDaylightSavingTime(dtLocalTime))
   {
     dtLocalTime.AddHours(-1);
   }
   DateTime dt = DateTimeExt.Parse("2004-06-05T04:20:09-01:00");
   Assert.AreEqual(dtLocalTime , dt, "Invalid date returned by .Parse().");
   dt = DateTimeExt.Parse("2004-06-05T05:20:09+00:00");
   Assert.AreEqual(dtLocalTime , dt, "Invalid date returned by .Parse().");
   DateTime dtx = DateTimeExt.ToDateTime("2004-06-05T06:20:09+01:00");
   Assert.AreEqual(dtUTCTime , dtx, "Invalid date returned by .ToDateTime().");
   dtx = DateTimeExt.ToDateTime("2004-06-05T05:20:09+00:00");
   Assert.AreEqual(dtUTCTime , dtx, "Invalid date returned by .ToDateTime().");
  }
 
  [Test] 
  public  void CreateNewsItemEnsureProperties()
  {
   DateTime fetchDate = DateTime.Now;
   NewsItem item = new NewsItem(null, "TestTitle", "TestLink", "TestDescription", fetchDate, "TestSubject");
   Assertion.AssertNull("Feed should be null.", item.Feed);
   Assertion.AssertEquals("Strange. The title is wrong.", "TestTitle", item.Title);
   Assertion.AssertEquals("Curious. The date is wrong.", fetchDate, item.Date);
   Assertion.AssertEquals("Odd. The subject is wrong.", "TestSubject", item.Subject);
  }
 
  [Test] 
  public  void CDATAStrippingAndEntityEscaping()
  {
   NewsItem item = new NewsItem(null, "", "link", "<![CDATA[This is stripped]]>", DateTime.Now, "");
   Assertion.AssertEquals("Bad strip job.", "This is stripped", item.Content);
   item = new NewsItem(null, "", "link", "<![CDATA[]]>How about now?", DateTime.Now, "");
   Assertion.AssertEquals("Bad strip job.", "How about now?", item.Content);
   item = new NewsItem(null, "", "link", "<![CDATA[]]>", DateTime.Now, "");
   Assertion.AssertEquals("Should be empty.", null, item.Content);
   item = new NewsItem(null, "", "link", "<?xml:namespace xmlns=blah>", DateTime.Now, "");
   Assertion.AssertEquals("Bad escaping! Bad!", "<?xml:namespace xmlns=blah>", item.Content);
   item = new NewsItem(null, "", "link", "<?XML:NAMESPACE xmlns=blah>", DateTime.Now, "");
   Assertion.AssertEquals("Bad escaping! Bad!", "<?XML:NAMESPACE xmlns=blah>", item.Content);
  }
 
  [Test] 
  public  void ToStringProducesCorrectXml()
  {
   DateTime fetchDate = DateTimeExt.Parse("Fri, 04 Apr 2003 10:41:37 GMT");
   NewsItem item = new NewsItem(null, "TestTitle", "TestLink", "TestDescription", fetchDate, "TestSubject");
   item.FeedDetails = new FeedInfo("", null, "FeedTitle", "FeedLink", "FeedDescription");
   Assert.AreEqual("FeedTitle", item.FeedDetails.Title, "The oh so creative title is wrong!");
   Assertion.AssertEquals("Capt'n! The XML was not as we expected.", UnpackResource("Expected.RssItemTests.TestToString.xml"), item.ToString());
   Assertion.AssertEquals("No GMT wrong.", UnpackResource("Expected.RssItemTests.TestToString.NoGMT.xml"), item.ToString(NewsItemSerializationFormat.RssItem, true));
   Assertion.AssertEquals("Not Standalone wrong.", UnpackResource("Expected.RssItemTests.TestToString.NotStandalone.xml"), item.ToString(NewsItemSerializationFormat.RssFeed, true));
  }
 
  [SetUp] 
  public  void SetUp()
  {
   UnpackResourceDirectory("Expected");
  }
 
  [TearDown] 
  public  void TearDown()
  {
   DeleteDirectory(UNPACK_DESTINATION);
  }

	}

}
