using System; 
using System.ComponentModel; 
using System.Drawing; 
using System.Collections; 
using System.Reflection; 
using System.Windows.Forms; 
using System.Threading; 
using System.Runtime.InteropServices; namespace  RssBandit.WinGui.Controls {
	
 public struct  NotifyIconState {
		
  private  Icon _icon;
 
  private  Icon[] _aniList;
 
  private  int _aniLoops;
 
  private  string _stateID;
 
  private  string _stateTip;
 
  public  NotifyIconState(string stateID, string stateTip, Icon icon){
   _stateID = stateID;
   _stateTip = stateTip;
   _icon = icon;
   _aniLoops = 0;
   _aniList = null;
  }
 
  public  NotifyIconState(string stateID, string stateTip, ImageList aniImages, int aniLoops){
   _stateID = stateID;
   _stateTip = stateTip;
   _icon = null;
   _aniLoops = aniLoops;
   _aniList = new Icon[aniImages.Images.Count];
   for (int i=0; i<aniImages.Images.Count; i++)
    _aniList [i] = Icon.FromHandle(((Bitmap) aniImages.Images [i]).GetHicon());
  }
 
  public  NotifyIconState(string stateID, string stateTip, Icon[] aniImages, int aniLoops) {
   _stateID = stateID;
   _stateTip = stateTip;
   _icon = null;
   _aniLoops = aniLoops;
   _aniList = aniImages;
  }
 
  internal  bool IsAnimation { get { return (_aniList != null && _aniList.GetLength(0) > 0); } }
 
  internal  bool IsIcon { get { return (_icon != null); } }
 
  public  string Key { get { return _stateID; } }
 
  public  string Text { get { return _stateTip; } set { _stateTip = value; } }
 
  public  Icon Icon { get { return _icon; } }
 
  public  int AniLoops { get { return _aniLoops; } set { _aniLoops = value; } }
 
  public  Icon IconAt(int index) { return _aniList[index]; }
 
  public  int IconCount() { return (_aniList != null ? _aniList.GetLength(0) : 0) ; }

	}
	
 public class  NotifyIconAnimation : System.ComponentModel.Component {
		
  private class  MessageHandler :NativeWindow, IDisposable {
			
   [DllImport("User32.dll", SetLastError=true)] 
   protected static extern  Int32 RegisterWindowMessage(string lpString);
 
   private  const int MF_POPUP = 0x10; 
   private  const int WM_USER = 0x400; 
   private  const int WM_NOTIFYICONCALLBACK = WM_USER + 1024; 
   private  const int WM_MOUSEMOVE = 0x200; 
   private  const int WM_COMMAND = 0x111; 
   private  const int WM_LBUTTONDOWN = 0x201; 
   private  const int WM_LBUTTONUP = 0x202; 
   private  const int WM_LBUTTONDBLCLK = 0x203; 
   private  const int WM_RBUTTONDOWN = 0x204; 
   private  const int WM_RBUTTONUP = 0x205; 
   private  const int WM_RBUTTONDBLCLK = 0x206; 
   private  const int WM_MBUTTONDOWN = 0x207; 
   private  const int WM_MBUTTONUP = 0x208; 
   private  const int WM_MBUTTONDBLCLK = 0x209; 
   private  const int NIN_BALLOONSHOW = 0x402; 
   private  const int NIN_BALLOONHIDE = 0x403; 
   private  const int NIN_BALLOONTIMEOUT = 0x404; 
   private  const int NIN_BALLOONUSERCLICK = 0x405; 
   public  event MouseEventHandler ClickNotify; 
   public  event MouseEventHandler DoubleClickNotify; 
   public  event MouseEventHandler MouseDownNotify; 
   public  event MouseEventHandler MouseUpNotify; 
   public  event MouseEventHandler MouseMoveNotify; 
   public  event EventHandler TaskbarReload; 
   public  event EventHandler BalloonShow; 
   public  event EventHandler BalloonHide; 
   public  event EventHandler BalloonTimeout; 
   public  event EventHandler BalloonClick; 
   private  Int32 WM_TASKBARCREATED = RegisterWindowMessage("TaskbarCreated");
 
   protected  void OnClickNotify(MouseEventArgs e) { if (ClickNotify != null) ClickNotify(this, e); }
 
   protected  void OnDoubleClickNotify(MouseEventArgs e) { if (DoubleClickNotify != null) DoubleClickNotify(this, e); }
 
   protected  void OnMouseUp(MouseEventArgs e) { if (MouseUpNotify != null) MouseUpNotify(this, e); }
 
   protected  void OnMouseDown(MouseEventArgs e) { if (MouseDownNotify != null) MouseDownNotify(this, e); }
 
   protected  void OnMouseMove(MouseEventArgs e) { if (MouseMoveNotify != null) MouseMoveNotify(this, e); }
 
   protected  void OnBallonClick(EventArgs e) { if (BalloonClick != null) BalloonClick(this, e); }
 
   protected  void OnBalloonShow(EventArgs e) { if (BalloonShow != null) BalloonShow(this, e); }
 
   protected  void OnBalloonHide(EventArgs e) { if (BalloonHide != null) BalloonHide(this, e); }
 
   protected  void OnBalloonTimeout(EventArgs e) { if (BalloonTimeout != null) BalloonTimeout(this, e); }
 
   protected  void OnTaskbarReload(EventArgs e) { if (TaskbarReload != null) TaskbarReload(this, e); }
 
   internal delegate  void  PerformContextMenuClickHandler (int menuID);
			
   internal  event PerformContextMenuClickHandler PerformContextMenuClick; 
   private  void OnPerformContextMenuClick(int menuID) {
    if (PerformContextMenuClick != null)
     PerformContextMenuClick(menuID);
   }
 
   public  MessageHandler() {
    CreateHandle(new CreateParams());
   }
 
   protected override  void WndProc(ref System.Windows.Forms.Message m) {
    switch (m.Msg) {
     case WM_NOTIFYICONCALLBACK:
     switch ((int)m.LParam) {
      case WM_LBUTTONDBLCLK:
       OnDoubleClickNotify(new MouseEventArgs(MouseButtons.Left, 2, 0, 0, 0));
       break;
      case WM_RBUTTONDBLCLK:
       OnDoubleClickNotify(new MouseEventArgs(MouseButtons.Right, 2, 0, 0, 0));
       break;
      case WM_MBUTTONDBLCLK:
       OnDoubleClickNotify(new MouseEventArgs(MouseButtons.Middle, 2, 0, 0, 0));
       break;
      case WM_LBUTTONDOWN:
       OnMouseDown(new MouseEventArgs(MouseButtons.Left, 1, 0, 0, 0));
       break;
      case WM_RBUTTONDOWN:
       OnMouseDown(new MouseEventArgs(MouseButtons.Right, 1, 0, 0, 0));
       break;
      case WM_MBUTTONDOWN:
       OnMouseDown(new MouseEventArgs(MouseButtons.Middle, 1, 0, 0, 0));
       break;
      case WM_MOUSEMOVE:
       OnMouseMove(new MouseEventArgs(Control.MouseButtons, 0, 0, 0, 0));
       break;
      case WM_LBUTTONUP:
       OnMouseUp(new MouseEventArgs(MouseButtons.Left, 0, 0, 0, 0));
       OnClickNotify(new MouseEventArgs(MouseButtons.Left, 0, 0, 0, 0));
       break;
      case WM_RBUTTONUP:
       OnMouseUp(new MouseEventArgs(MouseButtons.Right, 0, 0, 0, 0));
       OnClickNotify(new MouseEventArgs(MouseButtons.Right, 0, 0, 0, 0));
       break;
      case WM_MBUTTONUP:
       OnMouseUp(new MouseEventArgs(MouseButtons.Middle, 0, 0, 0, 0));
       OnClickNotify(new MouseEventArgs(MouseButtons.Middle, 0, 0, 0, 0));
       break;
      case NIN_BALLOONSHOW:
       OnBalloonShow(new EventArgs());
       break;
      case NIN_BALLOONHIDE:
       OnBalloonHide(new EventArgs());
       break;
      case NIN_BALLOONTIMEOUT:
       OnBalloonTimeout(new EventArgs());
       break;
      case NIN_BALLOONUSERCLICK:
       OnBallonClick(new EventArgs());
       break;
      default:
       break;
     }
     break;
     case WM_COMMAND:
      if (IntPtr.Zero == m.LParam) {
       int item = LOWORD(m.WParam);
       int flags = HIWORD(m.WParam);
       if ((flags & MF_POPUP) == 0) {
        OnPerformContextMenuClick(item);
       }
      }
      break;
     default:
      if (m.Msg == WM_TASKBARCREATED)
       OnTaskbarReload(new EventArgs());
      break;
    }
    base.WndProc(ref m);
   }
 
   private static  int HIWORD(IntPtr x) {
    return (unchecked((int)(long)x) >> 16) & 0xffff;
   }
 
   private static  int LOWORD(IntPtr x) {
    return unchecked((int)(long)x) & 0xffff;
   }
 
   public  void Dispose() {
    Dispose(true);
    GC.SuppressFinalize(this);
   }
 
   protected virtual  void Dispose(bool disposing) {
    Win32.PostMessage(new HandleRef(this, Handle), (uint)Win32.Message.WM_CLOSE, IntPtr.Zero, IntPtr.Zero);
    DestroyHandle();
   }
 
   ~MessageHandler() {
    Dispose(false);
   }
		}
		
  [DescriptionAttribute("Occurs when the user clicks the icon in the status area."),
  System.ComponentModel.CategoryAttribute("Action")] 
  public  event EventHandler Click; 
  [DescriptionAttribute("Occurs when the user double-clicks the icon in the status notification area of the taskbar."),
  System.ComponentModel.CategoryAttribute("Action")] 
  public  event EventHandler DoubleClick; 
  [DescriptionAttribute("Occurs when the user presses the mouse button while the pointer is over the icon in the status notification area of the taskbar."),
  System.ComponentModel.CategoryAttribute("Mouse")] 
  public  event MouseEventHandler MouseDown; 
  [DescriptionAttribute("Occurs when the user releases the mouse button while the pointer is over the icon in the status notification area of the taskbar."),
  System.ComponentModel.CategoryAttribute("Mouse")] 
  public  event MouseEventHandler MouseUp; 
  [DescriptionAttribute("Occurs when the user moves the mouse while the pointer is over the icon in the status notification area of the taskbar."),
  System.ComponentModel.CategoryAttribute("Mouse")] 
  public  event MouseEventHandler MouseMove; 
  [DescriptionAttribute("Occurs when the balloon is shown (balloons are queued)."),
  System.ComponentModel.CategoryAttribute("Behavior")] 
  public  event EventHandler BalloonShow; 
  [DescriptionAttribute("Occurs when the balloon disappearsfor example, when the icon is deleted. This message is not sent if the balloon is dismissed because of a timeout or a mouse click."),
  System.ComponentModel.CategoryAttribute("Behavior")] 
  public  event EventHandler BalloonHide; 
  [DescriptionAttribute("Occurs when the balloon is dismissed because of a timeout."),
  System.ComponentModel.CategoryAttribute("Behavior")] 
  public  event EventHandler BalloonTimeout; 
  [DescriptionAttribute("Occurs when the balloon is dismissed because of a mouse click."),
  System.ComponentModel.CategoryAttribute("Action")] 
  public  event EventHandler BalloonClick; 
  public delegate  void  AnimationFinishedDelegate (object sender, NotifyIconState animation);
		
  [DescriptionAttribute("Occurs when a icon animation finishes to enable the change to another icon."),
  System.ComponentModel.CategoryAttribute("Action")] 
  public  event AnimationFinishedDelegate AnimationFinished; 
  [StructLayout(LayoutKind.Sequential, CharSet=CharSet.Auto)] 
  internal struct  NOTIFYICONDATA {
			
   public  int cbSize;
 
   public  IntPtr hWnd;
 
   public  int uID;
 
   public  int uFlags;
 
   public  int uCallbackMessage;
 
   public  IntPtr hIcon;
 
   [MarshalAs(UnmanagedType.ByValTStr, SizeConst=128)] 
   public  string szTip;
 
   public  int dwState;
 
   public  int dwStateMask;
 
   [MarshalAs(UnmanagedType.ByValTStr, SizeConst=256)] 
   public  string szInfo;
 
   public  int uVersion;
 
   [MarshalAs(UnmanagedType.ByValTStr, SizeConst=64)] 
   public  string szInfoTitle;
 
   public  int dwInfoFlags;

		}
		
  [DllImport("shell32.dll", CharSet=CharSet.Auto)] 
  private static extern  bool Shell_NotifyIcon(int dwMessage, [In] ref NOTIFYICONDATA lpdata);
 
  private  const Int32 NIF_MESSAGE = 0x1; 
  private  const Int32 NIF_ICON = 0x2; 
  private  const Int32 NIF_STATE = 0x8; 
  private  const Int32 NIF_INFO = 0x10; 
  private  const Int32 NIF_TIP = 0x4; 
  private  const Int32 NIM_ADD = 0x0; 
  private  const Int32 NIM_MODIFY = 0x1; 
  private  const Int32 NIM_DELETE = 0x2; 
  private  const Int32 NIM_SETVERSION = 0x4; 
  private  const Int32 NOTIFYICON_VERSION = 5; 
  private  const int WM_USER = 0x400; 
  private  const int WM_NOTIFYICONCALLBACK = WM_USER + 1024; 
  public enum  EBalloonIcon 
  {
   None = 0x0,
   Error = 0x3,
   Info = 0x1,
   Warning = 0x2
  } 
  private  NOTIFYICONDATA _NID;
 
  private  MessageHandler _messages = new MessageHandler();
 
  private  bool _visibleBeforeBalloon;
 
  private  bool _visible;
 
  private  bool _versionSet;
 
  private  ContextMenu _contextMenu;
 
  private  System.ComponentModel.Container components = null;
 
  private  Hashtable _iconStates;
 
  private  NotifyIconState _currentState;
 
  private  System.Timers.Timer _aniStepTimer;
 
  private  int _aniStep = 0;
 
  private  int _aniLoopStep = 0;
 
  private  bool _disposed = false;
 
  public  NotifyIconAnimation() :base()
  {
   InitializeComponent();
   Initialize();
  }
 
  public  NotifyIconAnimation(System.ComponentModel.IContainer container)
  {
   container.Add(this);
   InitializeComponent();
   Initialize();
  }
 
  public  NotifyIconAnimation(NotifyIconState notifyIconState):this()
  {
   _currentState = this.AddState(notifyIconState);
  }
 
  public  NotifyIconAnimation(System.ComponentModel.IContainer container, NotifyIconState notifyIconState): this(container)
  {
   _currentState = this.AddState(notifyIconState);
  }
 
  private  void Initialize()
  {
   _NID = new NOTIFYICONDATA();
   _NID.hWnd = _messages.Handle;
   _NID.szTip = String.Empty;
   _NID.szInfo = String.Empty;
   _NID.szInfoTitle = String.Empty;
   _NID.cbSize = Marshal.SizeOf(typeof(NOTIFYICONDATA));
   _NID.dwState = 0;
   _NID.dwStateMask = 0;
   _NID.uFlags = NIF_ICON | NIF_TIP | NIF_MESSAGE;
   _NID.uCallbackMessage = WM_NOTIFYICONCALLBACK;
   _NID.uVersion = NOTIFYICON_VERSION;
   _NID.uID = 1;
   _iconStates = new Hashtable();
   InitAniTimer();
   InitEventHandler();
  }
 
  private  bool CallShellNotify(int dwMessage)
  {
   return Shell_NotifyIcon(dwMessage, ref _NID);
  }
 
  private  void InitAniTimer()
  {
   _aniStepTimer = new System.Timers.Timer();
   _aniStepTimer.BeginInit();
   _aniStepTimer.Interval = 100;
   _aniStepTimer.Elapsed += new System.Timers.ElapsedEventHandler(this.AnimationElapsed);
   _aniStepTimer.EndInit();
  }
 
  private  void InitEventHandler()
  {
   _messages.ClickNotify += new MouseEventHandler(this.OnClickDownCast);
   _messages.DoubleClickNotify += new MouseEventHandler(this.OnDoubleClickDownCast);
   _messages.MouseDownNotify += new MouseEventHandler(this.OnMouseDown);
   _messages.MouseMoveNotify += new MouseEventHandler(this.OnMouseMove);
   _messages.MouseUpNotify += new MouseEventHandler(this.OnMouseUp);
   _messages.BalloonShow += new EventHandler(this.OnBalloonShow);
   _messages.BalloonHide += new EventHandler(this.OnBalloonHide);
   _messages.BalloonClick += new EventHandler(this.OnBalloonClick);
   _messages.BalloonTimeout += new EventHandler(this.OnBalloonTimeout);
   _messages.TaskbarReload += new EventHandler(this.OnTaskbarReload);
   _messages.PerformContextMenuClick += new MessageHandler.PerformContextMenuClickHandler(OnPerformContextMenuClick);
  }
 
  ~NotifyIconAnimation()
  {
   Dispose(false);
  } 
  public new  void Dispose()
  {
   Dispose(true);
   GC.SuppressFinalize(this);
  }
 
  protected override  void Dispose(bool disposing) {
   if(!this._disposed) {
    if(disposing) {
     _aniStepTimer.Stop();
     _aniStepTimer.Dispose();
     Visible = false;
     _iconStates.Clear();
     if(components != null) components.Dispose();
    }
   }
   _disposed = true;
   base.Dispose(disposing);
  }
 
  private  void InitializeComponent()
  {
   components = new System.ComponentModel.Container();
  }
 
  private  void SetIcon()
  {
   this.SetIcon(_currentState.Icon);
  }
 
  private  void SetIcon(Icon icon)
  {
   _NID.uFlags = _NID.uFlags | NIF_ICON;
   _NID.hIcon = icon.Handle;
   if (Visible)
    CallShellNotify(NIM_MODIFY);
  }
 
  private  void SetText()
  {
   _NID.szTip = _currentState.Text;
   if (Visible)
   {
    _NID.uFlags = _NID.uFlags | NIF_TIP;
    CallShellNotify(NIM_MODIFY);
   }
  }
 
  [Description("The pop-up menu to show when the user right-clicks the icon."),
  CategoryAttribute("Behavior"),
  System.ComponentModel.DefaultValueAttribute("")] 
  public  System.Windows.Forms.ContextMenu ContextMenu
  {
   get { return _contextMenu; }
   set { _contextMenu = value; }
  }
 
  [Description("Determines whether the control is visible or hidden."),
  CategoryAttribute("Behavior"),
  System.ComponentModel.DefaultValueAttribute(false)] 
  public  bool Visible
  {
   get { return _visible; }
   set
   {
    _visible = value;
    if (!DesignMode)
    {
     if (_visible)
     {
      CallShellNotify(NIM_ADD);
      if (!_versionSet)
      {
       if (Environment.OSVersion.Version.Major >= 5)
        CallShellNotify(NIM_SETVERSION);
       _versionSet = true;
      }
     }
     else
      CallShellNotify(NIM_DELETE);
    }
   }
  }
 
  public  void ShowBalloon(EBalloonIcon icon, string text, string title)
  {
   this.ShowBalloon(icon, text, title ,15000);
  }
 
  public  void ShowBalloon(EBalloonIcon icon, string text, string title, int timeout)
  {
   int _old = _NID.uFlags;
   _visibleBeforeBalloon = _visible;
   _NID.uFlags |= NIF_INFO;
   _NID.uVersion = timeout;
   _NID.hWnd = _messages.Handle;
   _NID.szInfo = text;
   _NID.szInfoTitle = title;
   _NID.dwInfoFlags = (int)icon;
   if (!Visible)
   {
    Visible = true;
   }
   else
   {
    CallShellNotify(NIM_MODIFY);
   }
   _NID.uVersion = NOTIFYICON_VERSION;
   _NID.uFlags = _old;
  }
 
  public  NotifyIconState AddState(NotifyIconState newState)
  {
   _iconStates.Add (newState.Key, newState);
   return newState;
  }
 
  public  void RemoveState(string stateKey)
  {
   _iconStates.Remove(stateKey);
   if (_currentState.Key.Equals(stateKey))
    SetState(_iconStates.Count != 0 ? ((NotifyIconState)_iconStates[0]).Key: "");
  }
 
  public  NotifyIconState SetState(string stateKey)
  {
   if (_currentState.IsAnimation)
   {
    _aniStepTimer.Stop();
    _aniStep = 0;
    _aniLoopStep = 0;
   }
   if (_iconStates.ContainsKey(stateKey))
    _currentState = (NotifyIconState)_iconStates[stateKey];
   else
    _currentState = new NotifyIconState();
   if (_currentState.IsIcon) SetIcon();
   SetText();
   if (_currentState.IsAnimation)
   {
    ThreadPool.QueueUserWorkItem(new WaitCallback(this.AnimationThreadRun));
   }
   return _currentState;
  }
 
  private  void AnimationThreadRun(object state) {
   _aniStepTimer.Start();
   if (!_aniStepTimer.Enabled) _aniStepTimer.Enabled = true;
   while (_aniStepTimer.Enabled == true) {
    Application.DoEvents();
    Thread.Sleep(15);
   }
  }
 
  private  void AnimationElapsed(object sender, System.Timers.ElapsedEventArgs e)
  {
   Application.DoEvents();
   if (_currentState.AniLoops != -1)
   {
    if (_aniLoopStep >= _currentState.AniLoops)
    {
     _aniStepTimer.Enabled = false;
     Application.DoEvents();
     OnAnimationFinished(_currentState);
    }
   }
   if (!_currentState.IsAnimation)
    return;
   if (_aniStep >= _currentState.IconCount())
   {
    _aniStep = 0;
    _aniLoopStep++;
   }
   SetIcon(_currentState.IconAt(_aniStep));
   _aniStep++;
   Application.DoEvents();
  }
 
  protected virtual  void OnClickDownCast(object o, MouseEventArgs e) { if (Click != null) Click(this, e); }
 
  protected virtual  void OnDoubleClickDownCast(object o, MouseEventArgs e) { if (DoubleClick != null) DoubleClick(this, e); }
 
  protected virtual  void OnClick(object o, EventArgs e) { if (Click != null) Click(this, e); }
 
  protected virtual  void OnDoubleClick(object o, EventArgs e) { if (DoubleClick != null) DoubleClick(this, e); }
 
  protected virtual  void OnMouseUp(object o, MouseEventArgs e)
  {
   if (MouseUp != null) MouseUp(this, e);
   if (e.Button == MouseButtons.Right && _contextMenu != null)
   {
    Win32.POINT p = new Win32.POINT();
    Win32.GetCursorPos(out p);
    Win32.SetForegroundWindow(new HandleRef(_messages, _messages.Handle));
    Type ctxType = _contextMenu.GetType();
    try {
     ctxType.InvokeMember("OnPopup", BindingFlags.InvokeMethod | BindingFlags.NonPublic | BindingFlags.Instance, null, _contextMenu, new object[]{EventArgs.Empty});
     Win32.TrackPopupMenuEx(new HandleRef(ContextMenu, ContextMenu.Handle), 0, p.x, p.y, new HandleRef(_messages, _messages.Handle), IntPtr.Zero);
     Win32.PostMessage(new HandleRef(_messages, _messages.Handle), (uint)Win32.Message.WM_NULL, IntPtr.Zero, IntPtr.Zero);
    } catch{}
   }
  }
 
  protected virtual  void OnMouseDown(object s,MouseEventArgs e) { if (MouseDown != null) MouseDown(this, e); }
 
  protected virtual  void OnMouseMove(object s, MouseEventArgs e) { if (MouseMove != null) MouseMove(this, e); }
 
  private  void OnTaskbarReload(object sender, EventArgs e) {
   if (Visible) Visible = true;
  }
 
  protected virtual  void OnBalloonShow(object s, EventArgs e) { if (BalloonShow != null) BalloonShow(this, e); }
 
  protected virtual  void OnBalloonHide(object s, EventArgs e) { if (BalloonHide != null) BalloonHide(this, e); }
 
  protected virtual  void OnBalloonClick(object s, EventArgs e)
  {
   if (!_visibleBeforeBalloon) Visible = false;
   if (BalloonClick != null) BalloonClick(this, e);
  }
 
  protected virtual  void OnBalloonTimeout(object s, EventArgs e)
  {
   if (!_visibleBeforeBalloon) Visible = false;
   if (BalloonTimeout != null) BalloonTimeout(this, e);
  }
 
  protected virtual  void OnAnimationFinished(NotifyIconState animation) { if (AnimationFinished != null) AnimationFinished(this, animation); }
 
  private  void OnPerformContextMenuClick(int menuID) {
   foreach (MenuItem menuItem in _contextMenu.MenuItems) {
    if (PerformContextMenuClick(menuID, menuItem))
     break;
   }
  }
 
  private  bool PerformContextMenuClick(int menuID, MenuItem item){
   if (item == null)
    return false;
   int id = 0; Type itemType = item.GetType();
   try {
    id = (int)itemType.InvokeMember("MenuID", BindingFlags.GetProperty | BindingFlags.NonPublic | BindingFlags.Instance, null, item, new object[]{});
   } catch{}
   if (menuID == id) {
    item.PerformClick();
    return true;
   }
   foreach (MenuItem menuItem in item.MenuItems) {
    if (PerformContextMenuClick(menuID, menuItem))
     return true;
   }
   return false;
  }

	}

}
