using System;
using System.Text;
using Lucene.Net.Documents;
using NewsComponents.Utils;
namespace NewsComponents.Search
{
 public class LuceneNewsItemSearch {
  private LuceneNewsItemSearch(){}
  internal static Document Document(NewsItem item) {
   Document doc = new Document();
   doc.Add(Field.Keyword(LuceneSearch.IndexDocument.ItemID, UID(item)));
   doc.Add(Field.Keyword(LuceneSearch.IndexDocument.FeedID, item.Feed.id));
   doc.Add(Field.Keyword(LuceneSearch.Keyword.ItemLink, CheckNull(item.Link)));
   doc.Add(Field.Keyword(LuceneSearch.Keyword.ItemDate,
    DateTools.TimeToString(item.Date.Ticks, DateTools.Resolution.MINUTE)));
   doc.Add(Field.Text(LuceneSearch.Keyword.ItemTitle, CheckNull(item.Title)));
   doc.Add(Field.Text(LuceneSearch.Keyword.ItemAuthor, CheckNull(item.Author)));
   doc.Add(Field.Text(LuceneSearch.Keyword.ItemTopic, CheckNull(item.Subject)));
   if (item.HasContent)
   {
    StringBuilder content = new StringBuilder(HtmlHelper.StripAnyTags(item.Content));
    doc.Add(Field.UnIndexed(LuceneSearch.IndexDocument.ItemSummary, StringHelper.GetFirstWords(content.ToString(), 50)));
    foreach (string link in item.OutGoingLinks)
     content.AppendFormat(" {0}", link);
    doc.Add(Field.Text(LuceneSearch.IndexDocument.ItemContent, content.ToString()));
   } else {
    doc.Add(Field.Text(LuceneSearch.IndexDocument.ItemContent, CheckNull(item.Title)));
    doc.Add(Field.UnIndexed(LuceneSearch.IndexDocument.ItemSummary, StringHelper.GetFirstWords(item.Title, 50)));
   }
   doc.Add(Field.Keyword(LuceneSearch.Keyword.FeedLink, CheckNull(item.FeedLink)));
   doc.Add(Field.Keyword(LuceneSearch.Keyword.FeedUrl, CheckNull(item.FeedDetails.Link)));
   doc.Add(Field.Text(LuceneSearch.Keyword.FeedTitle, CheckNull(item.FeedDetails.Title)));
   doc.Add(Field.Keyword(LuceneSearch.Keyword.FeedType, item.FeedDetails.Type.ToString()));
   doc.Add(Field.Text(LuceneSearch.Keyword.FeedDescription, CheckNull(item.FeedDetails.Description)));
   return doc;
  }
  class Field
  {
   public static Lucene.Net.Documents.Field Keyword(string name, string value) {
    return new Lucene.Net.Documents.Field(name, value,
        Lucene.Net.Documents.Field.Store.YES,
        Lucene.Net.Documents.Field.Index.UN_TOKENIZED);
   }
   public static Lucene.Net.Documents.Field Text(string name, string value) {
    return new Lucene.Net.Documents.Field(name, value,
     Lucene.Net.Documents.Field.Store.YES,
     Lucene.Net.Documents.Field.Index.TOKENIZED);
   }
   public static Lucene.Net.Documents.Field UnIndexed(string name, string value) {
    return new Lucene.Net.Documents.Field(name, value,
     Lucene.Net.Documents.Field.Store.YES,
     Lucene.Net.Documents.Field.Index.NO);
   }
  }
  public static System.String DateToString(System.DateTime date) {
   TimeSpan ts = date.Subtract(new DateTime(1970, 1, 1));
   ts = ts.Subtract(TimeZone.CurrentTimeZone.GetUtcOffset(date));
   return DateField.TimeToString(ts.Ticks / TimeSpan.TicksPerMillisecond);
  }
  private static string CheckNull(string s) {
   if (s == null) return String.Empty;
   return s;
  }
  internal static char UrlPathSeparator = '/';
  internal static char UnicodeNullChar = '\u0000';
  public static string UID(NewsItem item) {
   string s = String.Concat(item.Feed.id, UnicodeNullChar, item.Id.Replace(UrlPathSeparator, UnicodeNullChar));
   return s;
  }
  public static string NewsItemIDFromUID(string uid) {
   return uid.Substring(1+uid.IndexOf(UnicodeNullChar)).Replace(UnicodeNullChar, UrlPathSeparator);
  }
  public static string FeedIDFromUID(string uid) {
   return uid.Substring(0, uid.IndexOf(UnicodeNullChar));
  }
 }
}
