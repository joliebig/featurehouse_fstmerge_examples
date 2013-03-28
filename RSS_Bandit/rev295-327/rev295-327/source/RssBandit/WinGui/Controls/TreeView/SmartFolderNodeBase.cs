using System.Collections; 
using System.Collections.Generic; 
using System.Windows.Forms; 
using NewsComponents; 
using RssBandit.SpecialFeeds; 
using RssBandit.WinGui.Interfaces; namespace  RssBandit.WinGui.Controls {
	
 public class  SmartFolderNodeBase : TreeFeedsNodeBase, ISmartFolder {
		
  private  ContextMenu _popup = null;
 
  protected  LocalFeedsFeed itemsFeed;
 
  public  SmartFolderNodeBase(LocalFeedsFeed itemStore, int imageIndex, int selectedImageIndex, ContextMenu menu):
   this(itemStore, itemStore.title, imageIndex, selectedImageIndex, menu) {
  }
 
  public  SmartFolderNodeBase(LocalFeedsFeed itemStore, string text, int imageIndex, int selectedImageIndex, ContextMenu menu):
   base(text, FeedNodeType.SmartFolder, false, imageIndex, selectedImageIndex) {
   _popup = menu;
   itemsFeed = itemStore;
  }
 
  public override  bool AllowedChild(FeedNodeType nsType) {
   return false;
  }
 
  public override  void PopupMenu(System.Drawing.Point screenPos) {
  }
 
  public override  void UpdateContextMenu() {
   if (base.Control != null)
    base.Control.ContextMenu = _popup;
  }
 
  public virtual  bool ContainsNewMessages {
   get {
    foreach (NewsItem ri in itemsFeed.Items) {
     if (!ri.BeenRead) return true;
    }
    return false;
   }
  }
 
  public virtual  int NewMessagesCount {
   get {
    int count = 0;
    for (int i=0; i < itemsFeed.Items.Count; i++)
    {
     NewsItem ri = (NewsItem)itemsFeed.Items[i];
     if (!ri.BeenRead) count++;
    }
    return count;
   }
  }
 
  public virtual  bool HasNewComments {
   get {
    foreach (NewsItem ri in itemsFeed.Items) {
     if (ri.HasNewComments) return true;
    }
    return false;
   }
  }
 
  public virtual  int NewCommentsCount {
   get {
    int count = 0;
    for (int i=0; i < itemsFeed.Items.Count; i++) {
     NewsItem ri = (NewsItem)itemsFeed.Items[i];
     if (ri.HasNewComments) count++;
    }
    return count;
   }
  }
 
  public virtual  void MarkItemRead(NewsItem item) {
   if (item == null) return;
   int index = itemsFeed.Items.IndexOf(item);
   if (index >= 0)
   {
    NewsItem ri = (NewsItem)itemsFeed.Items[index];
    ri.BeenRead = true;
    base.UpdateReadStatus(this, -1);
   }
  }
 
  public virtual  void MarkItemUnread(NewsItem item) {
   if (item == null) return;
   int index = itemsFeed.Items.IndexOf(item);
   if (index >= 0)
   {
    NewsItem ri = (NewsItem)itemsFeed.Items[index];
    ri.BeenRead = false;
   }
  }
 
  public virtual  List<NewsItem> Items {
   get { return itemsFeed.Items; }
  }
 
  public virtual  void Add(NewsItem item) {
   itemsFeed.Add(item);
  }
 
  public virtual  void Remove(NewsItem item) {
   itemsFeed.Remove(item);
  }
 
  public virtual  void UpdateReadStatus() {
   base.UpdateReadStatus(this, this.NewMessagesCount);
  }
 
  public virtual  void UpdateCommentStatus(){
   base.UpdateCommentStatus(this, this.NewCommentsCount);
  }
 
  public virtual  bool Modified {
   get { return itemsFeed.Modified; }
   set { itemsFeed.Modified = value; }
  }

	}

}
