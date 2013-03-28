using System;
using System.Collections;
using System.Xml.XPath;
namespace NewsComponents
{
 public interface INewsItem : ICloneable, IXPathNavigable, IEquatable<INewsItem>
 {
  string FeedLink { get; }
  string Link { get; }
  DateTime Date { get; set; }
  string Id { get; }
  string ParentId { get; }
  string Content { get; }
  bool HasContent { get; }
  void SetContent(string newContent, ContentType contentType) ;
  ContentType ContentType { get; set; }
  bool BeenRead { get; set; }
  IFeedDetails FeedDetails { get; set; }
  string Author { get; set; }
  string Title { get; set; }
  string Subject { get; set; }
  int CommentCount { get; set; }
  string CommentUrl { get; }
  string CommentRssUrl { get; }
  Hashtable OptionalElements { get; set; }
 }
}
