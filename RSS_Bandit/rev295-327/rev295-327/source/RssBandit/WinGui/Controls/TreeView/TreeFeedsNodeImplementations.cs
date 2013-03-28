using System; 
using System.Collections; 
using System.Collections.Generic; 
using System.Text; 
using System.Windows.Forms; 
using System.Drawing; 
using Infragistics.Win.UltraWinTree; 
using NewsComponents; 
using RssBandit.SpecialFeeds; 
using RssBandit.WinGui.Interfaces; 
using RssBandit.WinGui.Utility; namespace  RssBandit.WinGui.Controls {
	
 internal class  RootNode : TreeFeedsNodeBase {
		
  private static  ContextMenu _popup = null;
 
  public  RootNode(string text, int imageIndex, int selectedImageIndex, ContextMenu menu):base(text,FeedNodeType.Root, false, imageIndex, selectedImageIndex) {
   _popup = menu;
   base.Editable = false;
  }
 
  public override  object Clone() {
   return new RootNode(this.Text, (int)this.Override.NodeAppearance.Image, (int)this.Override.SelectedNodeAppearance.Image, _popup);
  }
 
  public override  bool AllowedChild(FeedNodeType nsType) {
   return (nsType == FeedNodeType.Feed || nsType == FeedNodeType.Category);
  }
 
  public override  void PopupMenu(System.Drawing.Point screenPos) {
  }
 
  public override  void UpdateContextMenu() {
   if (base.Control != null)
    base.Control.ContextMenu = _popup;
  }

	}
	
 internal class  CategoryNode : TreeFeedsNodeBase {
		
  private static  ContextMenu _popup = null;
 
  public  CategoryNode(string text):
   base(text,FeedNodeType.Category, true, 2, 3)
  {
  }
 
  public  CategoryNode(string text, int imageIndex, int selectedImageIndex, ContextMenu menu):base(text,FeedNodeType.Category, true, imageIndex, selectedImageIndex) {
   _popup = menu;
  }
 
  public override  object Clone() {
   return new CategoryNode(this.Text, (int)this.Override.NodeAppearance.Image, (int)this.Override.SelectedNodeAppearance.Image, _popup);
  }
 
  public override  bool AllowedChild(FeedNodeType nsType) {
   return (nsType == FeedNodeType.Category || nsType == FeedNodeType.Feed);
  }
 
  public override  void PopupMenu(System.Drawing.Point screenPos) {
  }
 
  public override  void UpdateContextMenu() {
   if (base.Control != null)
    base.Control.ContextMenu = _popup;
  }

	}
	
 internal class  FeedNode : TreeFeedsNodeBase {
		
  private static  ContextMenu _popup = null;
 
  public  FeedNode(string text, int imageIndex, int selectedImageIndex, ContextMenu menu):
   this(text, imageIndex, selectedImageIndex, menu, null) {
  }
 
  public  FeedNode(string text, int imageIndex, int selectedImageIndex, ContextMenu menu, Image image):base(text,FeedNodeType.Feed, true, imageIndex, selectedImageIndex, image) {
   _popup = menu;
  }
 
  public override  object Clone() {
   return new FeedNode(this.Text, (int)this.Override.NodeAppearance.Image, (int)this.Override.SelectedNodeAppearance.Image, _popup, this.Override.NodeAppearance.Image as Image);
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

	}
	
 internal class  SpecialRootNode :TreeFeedsNodeBase {
		
  private  ContextMenu _popup = null;
 
  public  SpecialRootNode(string text, int imageIndex, int selectedImageIndex, ContextMenu menu):base(text,FeedNodeType.Root, true, imageIndex, selectedImageIndex) {
   _popup = menu;
   base.Editable = false;
   base.Nodes.Override.Sort = SortType.None;
  }
 
  public override  bool AllowedChild(FeedNodeType nsType) {
   if (nsType == FeedNodeType.Finder ||
    nsType == FeedNodeType.SmartFolder
    )
    return true;
   return false;
  }
 
  public override  void PopupMenu(System.Drawing.Point screenPos) {
  }
 
  public override  void UpdateContextMenu() {
   if (base.Control != null)
    base.Control.ContextMenu = _popup;
  }

	}
	
 internal class  WasteBasketNode : SmartFolderNodeBase {
		
  public  WasteBasketNode(LocalFeedsFeed itemStore, int imageIndex, int selectedImageIndex, ContextMenu menu):
   base(itemStore, imageIndex, selectedImageIndex, menu) {}

	}
	
 internal class  SentItemsNode : SmartFolderNodeBase {
		
  public  SentItemsNode(LocalFeedsFeed itemStore, int imageIndex, int selectedImageIndex, ContextMenu menu):
   base(itemStore, imageIndex, selectedImageIndex, menu) { }

	}
	
 internal class  WatchedItemsNode : SmartFolderNodeBase {
		
  public  WatchedItemsNode(LocalFeedsFeed itemStore, int imageIndex, int selectedImageIndex, ContextMenu menu):
   base(itemStore, imageIndex, selectedImageIndex, menu) { }
 
  public override  void Remove(NewsItem item) {
   base.Remove (item);
   this.UpdateCommentStatus(this, -1);
  }

	}
	
 internal class  ExceptionReportNode : SmartFolderNodeBase {
		
  public  ExceptionReportNode(string text, int imageIndex, int selectedImageIndex, ContextMenu menu):
   base(null, text, imageIndex, selectedImageIndex, menu) {
  }
 
  public override  void MarkItemRead(NewsItem item) {
   if (item == null) return;
   foreach (NewsItem ri in ExceptionManager.GetInstance().Items) {
    if (item.Equals(ri)) {
     ri.BeenRead = true;
     base.UpdateReadStatus(this, -1);
     break;
    }
   }
  }
 
  public override  void MarkItemUnread(NewsItem item) {
   if (item == null) return;
   foreach (NewsItem ri in ExceptionManager.GetInstance().Items) {
    if (item.Equals(ri)) {
     ri.BeenRead = false;
     break;
    }
   }
  }
 
  public override  bool ContainsNewMessages {
   get {
    foreach (NewsItem ri in ExceptionManager.GetInstance().Items) {
     if (!ri.BeenRead) return true;
    }
    return false;
   }
  }
 
  public override  int NewMessagesCount {
   get {
    int i = 0;
    foreach (NewsItem ri in ExceptionManager.GetInstance().Items) {
     if (!ri.BeenRead) i++;
    }
    return i;
   }
  }
 
  public override  List<NewsItem> Items {
   get { return ExceptionManager.GetInstance().Items as List<NewsItem>; }
  }
 
  public override  void Add(NewsItem item) {
   ExceptionManager.GetInstance().Add(item);
  }
 
  public override  void Remove(NewsItem item) {
   ExceptionManager.GetInstance().Remove(item);
  }
 
  public override  bool Modified {
   get { return ExceptionManager.GetInstance().Modified; }
   set { ExceptionManager.GetInstance().Modified = value; }
  }

	}
	
 internal class  FlaggedItemsNode : SmartFolderNodeBase {
		
  private  Flagged flagsFiltered = Flagged.None;
 
  public  FlaggedItemsNode(Flagged flag, LocalFeedsFeed itemStore, string text, int imageIndex, int selectedImageIndex, ContextMenu menu):
   base(itemStore, text, imageIndex, selectedImageIndex, menu) {
   flagsFiltered = flag;
  }
 
  public  Flagged FlagFilter {
   get { return flagsFiltered; }
  }
 
  public override  void MarkItemRead(NewsItem item) {
   if (item == null) return;
   foreach (NewsItem ri in base.itemsFeed.Items) {
    if (item.Equals(ri) && ri.FlagStatus == flagsFiltered) {
     ri.BeenRead = true;
     base.UpdateReadStatus(this, -1);
     break;
    }
   }
  }
 
  public override  void MarkItemUnread(NewsItem item) {
   if (item == null) return;
   foreach (NewsItem ri in base.itemsFeed.Items) {
    if (item.Equals(ri) && ri.FlagStatus == flagsFiltered) {
     ri.BeenRead = false;
     break;
    }
   }
  }
 
  public override  bool ContainsNewMessages {
   get {
    foreach (NewsItem ri in base.itemsFeed.Items) {
     if (!ri.BeenRead && ri.FlagStatus == flagsFiltered) return true;
    }
    return false;
   }
  }
 
  public override  int NewMessagesCount {
   get {
    int i = 0;
    foreach (NewsItem ri in base.itemsFeed.Items) {
     if (!ri.BeenRead && ri.FlagStatus == flagsFiltered) i++;
    }
    return i;
   }
  }
 
  public override  List<NewsItem> Items {
   get {
    List<NewsItem> a = new List<NewsItem>(base.itemsFeed.Items.Count);
    foreach (NewsItem ri in base.itemsFeed.Items) {
     if (ri.FlagStatus == flagsFiltered)
      a.Add(ri);
    }
    return a;
   }
  }

	}
	
 internal class  FlaggedItemsRootNode : TreeFeedsNodeBase {
		
  private static  ContextMenu _popup = null;
 
  public  FlaggedItemsRootNode(string text, int imageIndex, int selectedImageIndex, ContextMenu menu):
   base(text, FeedNodeType.Root, false, imageIndex, selectedImageIndex) {
   _popup = menu;
  }
 
  public override  bool AllowedChild(FeedNodeType nsType) {
   return (nsType == FeedNodeType.SmartFolder);
  }
 
  public override  void PopupMenu(System.Drawing.Point screenPos) {
  }
 
  public override  void UpdateContextMenu() {
   if (base.Control != null)
    base.Control.ContextMenu = _popup;
  }

	}
	
 internal class  UnreadItemsNode : SmartFolderNodeBase {
		
  public  UnreadItemsNode(LocalFeedsFeed itemStore, int imageIndex, int selectedImageIndex, ContextMenu menu):
   base(itemStore, imageIndex, selectedImageIndex, menu) { }
 
  public override  void MarkItemRead(NewsItem item) {
   if (item == null) return;
   int idx = itemsFeed.Items.IndexOf(item);
   if (idx >= 0) {
    itemsFeed.Items.RemoveAt(idx);
    base.UpdateReadStatus(this, -1);
   }
  }
 
  public override  void MarkItemUnread(NewsItem item) {
   if (item == null) return;
   int idx = itemsFeed.Items.IndexOf(item);
   if (idx < 0) {
    itemsFeed.Items.Add(item);
    base.UpdateReadStatus(this, 1);
   } else {
    ((NewsItem) itemsFeed.Items[idx]).BeenRead = false;
    base.UpdateReadStatus(this, 1);
   }
  }

	}
	
 internal class  FinderRootNode :TreeFeedsNodeBase {
		
  private  ContextMenu _popup = null;
 
  public  FinderRootNode(string text, int imageIndex, int selectedImageIndex, ContextMenu menu):
   base(text,FeedNodeType.Root, true, imageIndex, selectedImageIndex) {
   _popup = menu;
   base.Editable = false;
  }
 
  public  void InitFromFinders(ArrayList finderList, ContextMenu menu) {
   Hashtable categories = new Hashtable();
   foreach (RssFinder finder in finderList) {
    TreeFeedsNodeBase parent = this;
    finder.Container = null;
    if (finder.FullPath.IndexOf(NewsHandler.CategorySeparator) > 0) {
     string[] a = finder.FullPath.Split(NewsHandler.CategorySeparator.ToCharArray());
     int aLen = a.GetLength(0);
     string sCat = String.Join(NewsHandler.CategorySeparator,a, 0, aLen-1);
     if (categories.ContainsKey(sCat)) {
      parent = (TreeFeedsNodeBase)categories[sCat];
     } else {
      StringBuilder sb = new StringBuilder();
      sb.Append(a[0]);
      for (int i = 0; i <= aLen - 2; i++) {
       sCat = sb.ToString();
       if (categories.ContainsKey(sCat)) {
        parent = (TreeFeedsNodeBase)categories[sCat];
       } else {
        TreeFeedsNodeBase cn = new FinderCategoryNode(a[i], 2, 3, menu);
        categories.Add(sCat, cn);
        parent.Nodes.Add(cn);
        parent = cn;
       }
       sb.Append(NewsHandler.CategorySeparator + a[i+1]);
      }
     }
    }
    FinderNode n = new FinderNode(finder.Text, 10, 10, menu);
    n.Finder = finder;
    finder.Container = n;
    parent.Nodes.Add(n);
   }
  }
 
  public override  bool AllowedChild(FeedNodeType nsType) {
   if (nsType == FeedNodeType.Finder)
    return true;
   return false;
  }
 
  public override  void PopupMenu(System.Drawing.Point screenPos) {
  }
 
  public override  void UpdateContextMenu() {
   if (base.Control != null)
    base.Control.ContextMenu = _popup;
  }

	}
	
 internal class  FinderCategoryNode : TreeFeedsNodeBase {
		
  private static  ContextMenu _popup = null;
 
  public  FinderCategoryNode(string text):
   base(text,FeedNodeType.FinderCategory, true, 2, 3) {}
 
  public  FinderCategoryNode(string text, int imageIndex, int selectedImageIndex, ContextMenu menu):
   base(text,FeedNodeType.FinderCategory, true, imageIndex, selectedImageIndex) {
   _popup = menu;
  }
 
  public override  bool AllowedChild(FeedNodeType nsType) {
   return (nsType == FeedNodeType.FinderCategory || nsType == FeedNodeType.Finder);
  }
 
  public override  void PopupMenu(System.Drawing.Point screenPos) {
  }
 
  public override  void UpdateContextMenu() {
   if (base.Control != null)
    base.Control.ContextMenu = _popup;
  }

	}
	
 public class  FinderNode :TreeFeedsNodeBase, ISmartFolder {
		
  private  ContextMenu _popup = null;
 
  private  LocalFeedsFeed itemsFeed;
 
        private  List<NewsItem> items = new List<NewsItem>();
 
  private  RssFinder finder = null;
 
  public  FinderNode():base() {}
 
  public  FinderNode(string text, int imageIndex, int selectedImageIndex, ContextMenu menu):
   base(text,FeedNodeType.Finder, true, imageIndex, selectedImageIndex) {
   itemsFeed = new LocalFeedsFeed(
    "http://localhost/rssbandit/searchfolder?id="+Guid.NewGuid(),
    text, String.Empty, false);
   _popup = menu;
  }
 
  public virtual  bool IsTempFinderNode {
   get { return false; }
  }
 
  public virtual  RssFinder Finder {
   get { return finder; }
   set { finder = value; }
  }
 
  public  string InternalFeedLink {
   get { return itemsFeed.link; }
  }
 
  public  bool Contains(NewsItem item) {
   return items.Contains(item);
  }
 
  public  void Clear() {
   items.Clear();
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
 
  public  bool ContainsNewMessages {
   get {
    foreach (NewsItem ri in items) {
     if (!ri.BeenRead) return true;
    }
    return false;
   }
  }
 
  public  int NewMessagesCount {
   get {
    int i = 0;
    foreach (NewsItem ri in items) {
     if (!ri.BeenRead) i++;
    }
    return i;
   }
  }
 
  public virtual  bool HasNewComments {
   get {
    foreach (NewsItem ri in items) {
     if (ri.HasNewComments) return true;
    }
    return false;
   }
  }
 
  public virtual  int NewCommentsCount {
   get {
    int count = 0;
    foreach (NewsItem ri in items) {
     if (ri.HasNewComments) count++;
    }
    return count;
   }
  }
 
  public  void MarkItemRead(NewsItem item) {
   if (item == null) return;
            int index = items.IndexOf(item);
   if (index!= -1){
    NewsItem ri = items[index];
    if (!ri.BeenRead) {
     ri.BeenRead = true;
     base.UpdateReadStatus(this, -1);
    }
   }
  }
 
  public  void MarkItemUnread(NewsItem item) {
   if (item == null) return;
            int index = items.IndexOf(item);
   if (index!= -1){
    NewsItem ri = items[index];
    if (ri.BeenRead) {
     ri.BeenRead = false;
     base.UpdateReadStatus(this, 1);
    }
   }
  }
 
  public  List<NewsItem> Items {
   get {
    return items;
   }
  }
 
  public  void Add(NewsItem item) {
   if (item == null) return;
            if (!items.Contains(item))
    items.Add(item);
  }
 
  public  void AddRange(IList<NewsItem> newItems)
  {
   if (newItems == null) return;
   for (int i=0; i < newItems.Count; i++) {
    NewsItem item = newItems[i] as NewsItem;
    if (item != null && !items.Contains(item))
     items.Add(item);
   }
  }
 
  public  void Remove(NewsItem item) {
   if (item == null) return;
   if (items.Contains(item))
    items.Remove(item);
  }
 
  public  void UpdateReadStatus() {
   base.UpdateReadStatus(this, this.NewMessagesCount);
  }
 
  public virtual  void UpdateCommentStatus(){
   base.UpdateCommentStatus(this, this.NewCommentsCount);
  }
 
  public  bool Modified {
   get { return itemsFeed.Modified; }
   set { itemsFeed.Modified = value; }
  }

	}
	
 public class  TempFinderNode  : FinderNode {
		
  public  TempFinderNode():base() {}
 
  public  TempFinderNode(string text, int imageIndex, int selectedImageIndex, ContextMenu menu):
  base(text, imageIndex, selectedImageIndex, menu) {
  }
 
  public override  bool IsTempFinderNode {
   get { return true; }
  }
 
  public override  RssFinder Finder
  {
   get { return base.Finder; }
   set {
    base.Finder = value;
    if (base.Finder != null)
     base.Finder.ShowFullItemContent = false;
   }
  }

	}

}
