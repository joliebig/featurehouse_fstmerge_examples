using System;
using System.Collections;
using System.Collections.Generic;
using System.Xml;
using System.Xml.XPath;
using NewsComponents.Feed;
using NewsComponents.RelationCosmos;
namespace NewsComponents
{
    public enum SupportedCommentStyle
    {
        None = 0,
        CommentAPI = 1,
        NNTP = 2
    }
    public enum Flagged
    {
        None,
        FollowUp,
        Read,
        Review,
        Forward,
        Reply,
        Complete
    }
    public enum NewsItemSerializationFormat
    {
        RssItem,
        RssFeed,
        NntpMessage,
        NewsPaper,
        Channel
    }
 public interface INewsItem : IRelation, ICloneable, IXPathNavigable, IEquatable<INewsItem>
 {
  string FeedLink { get; }
  string Link { get; }
  DateTime Date { get; set; }
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
        string CommentRssUrl { get; set; }
  Hashtable OptionalElements { get; set; }
        List<string> OutGoingLinks { get; }
        INewsFeed Feed { get; }
        Flagged FlagStatus { get; set; }
        bool WatchComments { get; set; }
        bool HasNewComments { get; set; }
        SupportedCommentStyle CommentStyle { get; set; }
        string Language { get; }
        List<IEnclosure> Enclosures { get; set; }
        INewsItem Clone(INewsFeed newParent);
        void WriteItem(XmlWriter writer, bool useGMTDate, bool noDescriptions);
        String ToRssFeedOrItem(NewsItemSerializationFormat format, bool useGMTDate, bool noDescriptions);
        String ToString(NewsItemSerializationFormat format, bool useGMTDate, bool noDescriptions);
        String ToString(NewsItemSerializationFormat format, bool useGMTDate);
        String ToString(NewsItemSerializationFormat format);
 }
    public interface IEnclosure: IEquatable<IEnclosure>
    {
        string MimeType { get; }
        long Length { get; }
        string Url { get; }
        string Description { get; set; }
        bool Downloaded { get; set; }
        TimeSpan Duration { get; set; }
    }
}
