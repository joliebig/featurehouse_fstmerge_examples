using System;
namespace NewsComponents
{
 public enum FeedType {
  Rss,
  Nntp
 }
 public enum ContentType{
  None,
  Unknown,
  Text,
  Html,
  Xhtml
 }
 public enum ChannelProcessingType
 {
  NewsItem,
  Feed,
 }
 public enum SortOrder{
  None,
  Ascending,
  Descending,
 }
 public enum LayoutType {
  IndividualLayout,
  GlobalFeedLayout,
  GlobalCategoryLayout,
  SearchFolderLayout,
  SpecialFeedsLayout,
 }
}
