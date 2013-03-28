using System;
using NewsComponents.Feed;
namespace NewsComponents.Storage {
 public abstract class CacheManager
 {
  internal abstract string CacheLocation {get;}
  internal abstract FeedDetailsInternal GetFeed(NewsFeed feed);
  internal abstract string SaveFeed(FeedDetailsInternal feed);
  public abstract void RemoveFeed(NewsFeed feed);
  public abstract bool FeedExists(NewsFeed feed);
  public abstract void ClearCache();
  public abstract void LoadItemContent(NewsItem item);
 }
}
