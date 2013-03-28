using System; 
using NewsComponents.Feed; namespace  NewsComponents.Storage {
	
 public abstract class  CacheManager {
		
  internal abstract  string CacheLocation {get;}
 
     
  internal abstract  string SaveFeed(FeedDetailsInternal feed);
 
     
     
  public abstract  void ClearCache();
 
  public abstract  void LoadItemContent(NewsItem item);
 
  internal abstract  FeedDetailsInternal GetFeed(INewsFeed feed); 
  public abstract  void RemoveFeed(INewsFeed feed); 
  public abstract  bool FeedExists(INewsFeed feed);
	}

}
