using System;
using System.Drawing;
using System.Windows.Forms;
using RssBandit.Resources;
using RssBandit.WinGui.Controls;
namespace RssBandit.WinGui
{
 public enum ApplicationTrayState
 {
  NormalIdle,
  BusyRefreshFeeds,
  NewUnreadFeedsReceived,
  NewUnreadFeeds
 }
 public class TrayStateManager
 {
  private ApplicationTrayState _currentState;
  private NotifyIconAnimation _notifyIcon;
  private TrayStateManager(){
   _currentState = ApplicationTrayState.NormalIdle;
  }
  public TrayStateManager(NotifyIconAnimation notifyIconAnimation , ImageList il ):this() {
   _notifyIcon = notifyIconAnimation;
   string postFix = String.Empty;
   if (Win32.IsOSAtLeastWindowsXP) {
    postFix = "XP";
   }
   _notifyIcon.AddState(
    new NotifyIconState(ApplicationTrayState.NormalIdle.ToString(),
     SR.GUIStatusIdle,
     Resource.LoadIcon(String.Format("Resources.AppTray{0}.ico", postFix))
    )
   );
   if (Win32.IsOSAtLeastWindowsXP) {
    _notifyIcon.AddState(
     new NotifyIconState(ApplicationTrayState.NewUnreadFeedsReceived.ToString(),
     SR.GUIStatusNewFeedItemsReceived,
     (il == null ? Resource.LoadBitmapStrip(String.Format("Resources.AniImages{0}.png", postFix), new Size(16,16) ) : il) , 3)
     );
   } else {
    _notifyIcon.AddState(
     new NotifyIconState(ApplicationTrayState.NewUnreadFeedsReceived.ToString(),
     SR.GUIStatusNewFeedItemsReceived,
     Resource.LoadIcon(String.Format("Resources.UnreadFeedItems{0}.ico", postFix))
     )
    );
   }
   _notifyIcon.AddState(
    new NotifyIconState(ApplicationTrayState.BusyRefreshFeeds.ToString(),
    SR.GUIStatusBusyRefresh,
    Resource.LoadIcon(String.Format("Resources.AppBusy{0}.ico", postFix))
    )
   );
   _notifyIcon.AddState(
    new NotifyIconState(ApplicationTrayState.NewUnreadFeeds.ToString(),
     SR.GUIStatusUnreadFeedItemsAvailable,
     Resource.LoadIcon(String.Format("Resources.UnreadFeedItems{0}.ico", postFix))
    )
   );
   _notifyIcon.AnimationFinished += new NotifyIconAnimation.AnimationFinishedDelegate(this.OnAnimationFinished);
   _notifyIcon.Visible = true;
  }
  public ApplicationTrayState CurrentState {
   get { return _currentState; }
  }
  public void SetState(ApplicationTrayState state){
   _currentState = state;
   _notifyIcon.SetState(_currentState.ToString());
  }
  private void OnAnimationFinished(object sender, NotifyIconState animation) {
   if (animation.Key.Equals(ApplicationTrayState.NewUnreadFeedsReceived.ToString()))
    this.SetState(ApplicationTrayState.NewUnreadFeeds);
  }
 }
}
