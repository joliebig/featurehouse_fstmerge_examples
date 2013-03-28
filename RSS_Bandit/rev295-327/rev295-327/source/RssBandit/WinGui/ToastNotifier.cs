using System; 
using System.Collections; 
using System.Collections.Generic; 
using System.Drawing; 
using System.Windows.Forms; 
using NewsComponents; 
using NewsComponents.Net; 
using NewsComponents.Feed; 
using RssBandit.WinGui.Forms; 
using Logger = RssBandit.Common.Logging; namespace  RssBandit.WinGui {
	
 public delegate  void  ItemActivateCallback (NewsItem item);
	
 public delegate  void  DisplayFeedPropertiesCallback (INewsFeed f);
	
 public delegate  void  FeedActivateCallback (INewsFeed f);
	
 public delegate  void  EnclosureActivateCallback (DownloadItem enclosure);
	
 public class  ToastNotifier : IDisposable {
		
  private  const int TOASTWINDOW_HEIGHT = 145; 
  private  const int TOASTWINDOW_OFFSET = 2; 
  private static readonly  log4net.ILog _log = Logger.Log.GetLogger(typeof(RssBanditApplication));
 
  private  ItemActivateCallback _itemActivateCallback;
 
  private  DisplayFeedPropertiesCallback _displayFeedPropertiesCallback;
 
  private  FeedActivateCallback _feedActivateCallback;
 
  private  EnclosureActivateCallback _enclosureActivateCallback;
 
  private  int _usedToastWindowLocations;
 
  private  object SyncRoot = new object();
 
  private  bool _disposing = false;
 
  public  ToastNotifier():this(null, null, null, null) {}
 
  public  ToastNotifier(ItemActivateCallback onItemActivateCallback,
   DisplayFeedPropertiesCallback onFeedPropertiesDialog,
   FeedActivateCallback onFeedActivateCallback,
   EnclosureActivateCallback onEnclosureActivateCallback) {
   this._usedToastWindowLocations = 0;
   this._itemActivateCallback = onItemActivateCallback;
   this._displayFeedPropertiesCallback = onFeedPropertiesDialog;
   this._feedActivateCallback = onFeedActivateCallback;
   this._enclosureActivateCallback = onEnclosureActivateCallback;
  }
 
        public  void Alert(string feedName, int dispItemCount, IList<NewsItem> items) {
            this.Alert(feedName, dispItemCount, (IList)items);
        }
 
        public  void Alert(string feedName, int dispItemCount, IList<DownloadItem> items) {
            this.Alert(feedName, dispItemCount, (IList)items);
        }
 
        private  void Alert(string feedName, int dispItemCount, IList items)
  {
   if (dispItemCount < 0 || items == null || items.Count == 0)
    return;
   if (_disposing)
    return;
    ToastNotify theWindow = this.GetToastWindow(items[0]);
    if (theWindow != null) {
     try {
      if (theWindow.ItemsToDisplay(feedName, dispItemCount, items) &&
       !theWindow.Disposing)
      {
       if (this._usedToastWindowLocations == 1)
       {
        if (theWindow is NewsItemToastNotify)
         Win32.PlaySound(Resource.ApplicationSound.NewItemsReceived);
        else if (theWindow is EnclosureToastNotify)
         Win32.PlaySound(Resource.ApplicationSound.NewAttachmentDownloaded);
       }
       theWindow.Animate();
      }
      else
      {
       this.OnToastAnimatingDone(theWindow, EventArgs.Empty);
       theWindow.Close();
       theWindow.Dispose();
      }
     } catch(Exception e) {
      _log.Fatal("ToastNotify.Alert() caused an error", e);
     }
    }
  }
 
  private  ToastNotify GetToastWindow(object toastObject) {
   ToastNotify tn = null;
   int windowIndex = GetFreeToastWindowOffset();
   if (windowIndex < 0)
    return null;
   Rectangle rPrimeScreen = Screen.PrimaryScreen.WorkingArea;
   int newX = (rPrimeScreen.Height - TOASTWINDOW_OFFSET) - (windowIndex * TOASTWINDOW_HEIGHT);
   if (newX < (rPrimeScreen.Top + TOASTWINDOW_HEIGHT)) {
    tn = null;
   } else {
    if (tn == null) {
     ToastNotify tnNew = null;
     if(toastObject is NewsItem){
      tnNew = new NewsItemToastNotify(_itemActivateCallback, _displayFeedPropertiesCallback, _feedActivateCallback);
     }else {
      tnNew = new EnclosureToastNotify(_enclosureActivateCallback, _displayFeedPropertiesCallback, _feedActivateCallback);
     }
     tnNew.AutoDispose = true;
     tnNew.Tag = windowIndex;
     tnNew.AnimatingDone += new EventHandler(this.OnToastAnimatingDone);
     tn = tnNew;
    }
   }
   return tn;
  }
 
  private  void OnToastAnimatingDone(object sender, EventArgs e) {
   ToastNotify n = sender as ToastNotify;
   if (n != null) {
    n.AnimatingDone -= new EventHandler(this.OnToastAnimatingDone);
    MarkToastWindowOffsetFree((int)n.Tag);
   }
  }
 
  private  int GetFreeToastWindowOffset() {
   int max = (Screen.PrimaryScreen.WorkingArea.Height - TOASTWINDOW_OFFSET) / TOASTWINDOW_HEIGHT;
   lock (this.SyncRoot) {
    for (int i = 0; i < max; i++)
     if (0 == (this._usedToastWindowLocations & (1 << i))) {
      this._usedToastWindowLocations |= (1 << i);
      return i;
     }
   }
   return -1;
  }
 
  private  void MarkToastWindowOffsetFree(int index)
  {
   lock (this.SyncRoot)
   {
    this._usedToastWindowLocations &= ~(1 << index);
   }
  }
 
  public  void Dispose() {
   _disposing = true;
  }

	}

}
