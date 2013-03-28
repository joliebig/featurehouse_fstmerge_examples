using System; 
using System.Drawing; 
using System.Drawing.Drawing2D; 
using System.Collections; 
using System.ComponentModel; 
using System.Windows.Forms; 
using System.Data; 
using System.Threading; 
using System.Runtime.CompilerServices; 
using System.Runtime.InteropServices; 
using RssBandit; namespace  Genghis.Windows.Forms {
	
    public class  AniForm  : System.Windows.Forms.Form {
		
        private  const Int32 GWL_STYLE = (-16); 
        private  const int WS_BORDER = 0x00800000; 
        private  const Int32 WS_CAPTION = 0x00C00000; 
        private  const Int32 WS_EX_APPWINDOW = 0x00040000; 
        private  const Int32 GWL_EXSTYLE = (-20); 
        private  const Int32 SW_SHOWNOACTIVATE = 4; 
        private  const Int32 SW_HIDE = 0; 
        private  const UInt32 SWP_NOSIZE = 0x0001; 
        private  const UInt32 SWP_NOMOVE = 0x0002; 
        private  const UInt32 SWP_NOZORDER = 0x0004; 
        private  const UInt32 SWP_NOREDRAW = 0x0008; 
        private  const UInt32 SWP_NOACTIVATE = 0x0010; 
        private  const UInt32 SWP_FRAMECHANGED = 0x0020; 
        private  const UInt32 SWP_SHOWWINDOW = 0x0040; 
        private  const UInt32 SWP_HIDEWINDOW = 0x0080; 
        private  const UInt32 SWP_NOCOPYBITS = 0x0100; 
        private  const UInt32 SWP_NOOWNERZORDER = 0x0200; 
        private  const UInt32 SWP_NOSENDCHANGING = 0x0400; 
        private  const int HWND_TOPMOST = (-1); 
        [DllImport("user32.dll")] 
        private static extern  IntPtr GetDesktopWindow();
 
        [DllImport("user32.dll")] 
        private static extern  IntPtr SetParent(IntPtr hWndChild, IntPtr hParent);
 
        [DllImport("user32.dll")] 
        private static extern  Int32 SetWindowLong(IntPtr hWnd, Int32 Offset, Int32 newLong);
 
        [DllImport("user32.dll")] 
        private static extern  Int32 GetWindowLong(IntPtr hWnd, Int32 Offset);
 
        [DllImport("user32.dll")] 
        private static extern  Int32 ShowWindow(IntPtr hWnd, Int32 dwFlags);
 
        [DllImport("user32.dll")] 
        private static extern  Int32 SetWindowPos(IntPtr hWnd, IntPtr hWndAfter, Int32 x, Int32 y, Int32 cx, Int32 cy, UInt32 uFlags);
 
        private  bool m_bPersistent;
 
        private  AnimateDirection m_direction;
 
        private  int m_iSpeed;
 
        private  Color m_colorStart;
 
        private  Color m_colorEnd;
 
        private  BackgroundMode m_bgMode;
 
        private  int m_iDelay;
 
        private  FormPlacement m_placement;
 
        private  int m_iGradientSize;
 
        private  bool m_bAnimating;
 
        private  bool m_bCloseRequested;
 
        private  bool m_bSavedBounds;
 
        private  AutoResetEvent m_eventClosed;
 
        private  ManualResetEvent m_eventNotifyClosed;
 
        private  int m_iInterval;
 
        private  int m_iDelta;
 
        private  int m_iCalcSpeed;
 
        private  int m_iAdjSpeed;
 
        private  int m_iLastDelta;
 
        private  Rectangle m_oldBounds;
 
        private  Point m_startLocation;
 
        private  bool m_bActivated;
 
        private  BorderStyle m_borderStyle;
 
        private  int m_iBorderWidth;
 
        private  Point mouseOffset;
 
        private  bool isMouseDown = false;
 
        private  WndMover m_wndMover = null;
 
        private  AniForm m_baseForm = null;
 
        private  StackMode m_stackMode = StackMode.None;
 
        private  Point m_origLocation;
 
        private  bool m_bAutoDispose;
 
        ThreadStart tsAnimate;
 
        Thread thAnimate;
 delegate  void  CloseWindowDelegate ();
		
        private static  StackArray s_currentForms = new StackArray();
 
        private  System.ComponentModel.Container components = null;
 
        public  AniForm()
        {
            CommonConstruction();
        }
 
        private  void CommonConstruction()
        {
            InitializeComponent();
            m_eventClosed = new AutoResetEvent(false);
            m_eventNotifyClosed = new ManualResetEvent(false);
            m_direction = AnimateDirection.BottomToTop;
            m_iSpeed = 40;
            m_colorStart = Color.FromArgb(255, 168, 168, 255);
            m_colorEnd = SystemColors.Window;
            m_bgMode = BackgroundMode.GradientVertical;
            m_iDelay = 5000;
            m_bCloseRequested = false;
            m_iInterval = 10;
            InitCenterLocation();
            m_startLocation = this.Location;
            m_bSavedBounds = false;
            m_bActivated = false;
            m_borderStyle = BorderStyle.None;
            m_iBorderWidth = 0;
            m_wndMover = new WndMover(this.DoMove);
            m_bAutoDispose = false;
        }
 
        public  event EventHandler AnimatingDone; 
        public  event EventHandler Expanded; 
        protected override  void Dispose(bool disposing)
        {
            if (disposing)
            {
                if (components != null)
                {
                    components.Dispose();
                }
            }
            base.Dispose(disposing);
        }
 
        private  void InitializeComponent()
        {
            this.ClientSize = new System.Drawing.Size(208, 184);
            this.ControlBox = false;
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "AniForm";
            this.ShowInTaskbar = false;
            this.Text = "AniForm";
        }
 
        public  bool Persistent
        {
            get
            {
                return m_bPersistent;
            }
            set
            {
                m_bPersistent = value;
            }
        }
 
        public  AnimateDirection Direction
        {
            get
            {
                return m_direction;
            }
            set
            {
                m_direction = value;
            }
        }
 
        public  int Speed
        {
            get
            {
                return m_iSpeed;
            }
            set
            {
                if (value < 1 || value > 89)
                    throw new ArgumentOutOfRangeException("Speed");
                m_iSpeed = value;
            }
        }
 
        public  Color StartColor
        {
            get
            {
                return m_colorStart;
            }
            set
            {
                m_colorStart = value;
            }
        }
 
        public  Color EndColor
        {
            get
            {
                return m_colorEnd;
            }
            set
            {
                m_colorEnd = value;
            }
        }
 
        public  BackgroundMode BackgroundMode
        {
            get
            {
                return m_bgMode;
            }
            set
            {
                m_bgMode = value;
                Invalidate();
            }
        }
 
        public  int Delay
        {
            get
            {
                return m_iDelay;
            }
            set
            {
                m_iDelay = value;
            }
        }
 
        [Browsable(false)] 
        public  bool Animating
        {
            get
            {
                return m_bAnimating;
            }
            set
            {
                lock (this)
                {
                    m_bAnimating = value;
                }
            }
        }
 
        [Browsable(false)] 
        public  bool CloseRequested
        {
            get
            {
                bool bReturn;
                lock (this)
                {
                    bReturn = m_bCloseRequested;
                }
                return bReturn;
            }
            set
            {
                lock (this)
                {
                    m_bCloseRequested = value;
                }
            }
        }
 
        public  FormPlacement Placement
        {
            get
            {
                return m_placement;
            }
            set
            {
                m_placement = value;
            }
        }
 
        public  Point StartLocation
        {
            get
            {
                return m_startLocation;
            }
            set
            {
                m_startLocation = value;
            }
        }
 
        [Browsable(false)] 
        public  bool IsActivated
        {
            get
            {
                bool bActivated = false;
                lock (this)
                {
                    bActivated = m_bActivated;
                }
                return bActivated;
            }
            set
            {
                lock (this)
                {
                    m_bActivated = value;
                }
            }
        }
 
        public  BorderStyle BorderStyle
        {
            get
            {
                return m_borderStyle;
            }
            set
            {
                m_borderStyle = value;
                if (m_borderStyle == BorderStyle.Raised)
                {
                    m_iBorderWidth = 4;
                }
                else if (m_borderStyle == BorderStyle.FixedSingle)
                {
                    m_iBorderWidth = 1;
                }
                else
                {
                    m_iBorderWidth = 0;
                }
            }
        }
 
        public  Size FullSize
        {
            get
            {
                return new Size(m_oldBounds.Width, m_oldBounds.Height);
            }
        }
 
        public  StackMode StackMode
        {
            get
            {
                return m_stackMode;
            }
            set
            {
                m_stackMode = value;
            }
        }
 
        public  bool AutoDispose
        {
            get
            {
                return m_bAutoDispose;
            }
            set
            {
                m_bAutoDispose = value;
            }
        }
 
        public  void Animate()
        {
            if (!Animating)
            {
                m_eventNotifyClosed.Reset();
                if (!m_bSavedBounds)
                {
                    if (BackgroundMode == BackgroundMode.GradientVertical)
                        m_iGradientSize = this.ClientRectangle.Height;
                    else if (BackgroundMode == BackgroundMode.GradientHorizontal)
                        m_iGradientSize = this.ClientRectangle.Width;
                    m_oldBounds = this.Bounds;
                    m_bSavedBounds = true;
                    m_origLocation = StartLocation;
                }
                ResetPosition();
                Calculate();
                AddToStack();
                try
                {
                    InitLocation();
                    ResetPosition();
                    Animating = true;
                    SetWindowPos(this.Handle, (IntPtr)HWND_TOPMOST, 0, 0, 0, 0,
                        SWP_HIDEWINDOW | SWP_NOMOVE | SWP_NOSIZE | SWP_NOACTIVATE);
                    ShowWindow(this.Handle, SW_SHOWNOACTIVATE);
                    tsAnimate = new ThreadStart(this.AniFunc);
                    thAnimate = new Thread(tsAnimate);
                    thAnimate.Start();
                }
                catch (OffDisplayException ex)
                {
                    RemoveFromStack();
                    ex.GetHashCode();
                }
            }
        }
 
        public  void RequestClose()
        {
            if (this.IsHandleCreated)
            {
                if (Visible)
                {
                    ShowWindow(this.Handle, SW_HIDE);
                }
                m_bActivated = false;
                CloseRequested = true;
                SetClosedEvent();
            }
        }
 
        public  bool WaitForClose(int iTime)
        {
            return m_eventNotifyClosed.WaitOne(iTime, false);
        }
 
        protected virtual  void OnAnimatingDone(EventArgs e)
        {
            if (AnimatingDone != null)
                AnimatingDone(this, e);
        }
 
        protected virtual  void OnExpanded(EventArgs e)
        {
            if (Expanded != null)
                Expanded(this, e);
        }
 
        protected  void Calculate()
        {
            int iInitialSize = 0;
            int iPrimaryDimension = 0;
            if (Direction == AnimateDirection.LeftToRight)
            {
                iInitialSize = this.Bounds.Width + 1;
                iPrimaryDimension = m_oldBounds.Width;
            }
            else
            {
                iInitialSize = this.Bounds.Height + 1;
                iPrimaryDimension = m_oldBounds.Height;
            }
            m_iCalcSpeed = 90 - Speed;
            m_iAdjSpeed = m_iCalcSpeed;
            m_iLastDelta = (iPrimaryDimension - iInitialSize) % m_iCalcSpeed;
            m_iDelta = (iPrimaryDimension - iInitialSize - m_iLastDelta) / m_iCalcSpeed;
            if (m_iLastDelta > m_iDelta)
            {
                m_iAdjSpeed = m_iCalcSpeed + ((m_iLastDelta - (m_iLastDelta % m_iDelta)) / m_iDelta);
                m_iLastDelta = m_iLastDelta % m_iDelta;
            }
        }
 
        protected  void ThreadSafeResize(bool bExpand, int iDiff)
        {
            if (CloseRequested == true)
            {
                throw new CloseRequestedException();
            }
            else
            {
                if (!bExpand)
                    iDiff = 0 - iDiff;
                object[] parms = { iDiff };
    if (!this.Disposing && !this.IsDisposed)
     try { this.Invoke(m_wndMover, parms); } catch { }
            }
        }
 
        protected override  void OnPaint(System.Windows.Forms.PaintEventArgs e)
        {
            if (BackgroundMode != BackgroundMode.Normal)
            {
                if (this.DesignMode)
                {
                    if (BackgroundMode == BackgroundMode.GradientVertical)
                    {
                        m_iGradientSize = this.ClientRectangle.Height;
                    }
                    else
                    {
                        m_iGradientSize = this.ClientRectangle.Width;
                    }
                }
                if (this.ClientRectangle.Height > 0 && this.ClientRectangle.Width > 0)
                {
                    Rectangle rectFill = Rectangle.Inflate(this.ClientRectangle, -(m_iBorderWidth), -(m_iBorderWidth));
                    if ((rectFill.Width > 0) && (rectFill.Height > 0))
                    {
                        LinearGradientBrush lgb;
                        if (BackgroundMode == BackgroundMode.GradientVertical)
                        {
                            lgb = new LinearGradientBrush(
                                new Rectangle(this.ClientRectangle.Left, this.ClientRectangle.Top, this.ClientRectangle.Width, m_iGradientSize),
                                StartColor,
                                EndColor,
                                LinearGradientMode.Vertical);
                        }
                        else
                        {
                            lgb = new LinearGradientBrush(
                                new Rectangle(this.ClientRectangle.Left, this.ClientRectangle.Top, m_iGradientSize, this.ClientRectangle.Height),
                                StartColor,
                                EndColor,
                                LinearGradientMode.Horizontal);
                        }
                        e.Graphics.FillRectangle(lgb, rectFill);
                        lgb.Dispose();
                        lgb = null;
                    }
                }
            }
            base.OnPaint(e);
            if (this.BorderStyle != BorderStyle.None)
            {
                Rectangle rectBorder = new Rectangle(this.ClientRectangle.Left, this.ClientRectangle.Top, this.ClientRectangle.Width - 1, this.ClientRectangle.Height - 1);
                if (this.BorderStyle == BorderStyle.FixedSingle)
                {
                    if (rectBorder.Width > 1 && rectBorder.Height > 1)
                    {
                        e.Graphics.DrawRectangle(new Pen(new SolidBrush(Color.Black), 1.0f), rectBorder);
                    }
                }
                else if (this.BorderStyle == BorderStyle.Raised)
                {
                    if (rectBorder.Width > 1 && rectBorder.Height > 1)
                    {
                        e.Graphics.DrawRectangle(new Pen(new SolidBrush(SystemColors.ControlDark), 1.0f), rectBorder);
                        rectBorder.Inflate(-1, -1);
                    }
                    if (rectBorder.Width > 1 && rectBorder.Height > 1)
                    {
                        e.Graphics.DrawRectangle(new Pen(new SolidBrush(SystemColors.ControlLight), 1.0f), rectBorder);
                        rectBorder.Inflate(-1, -1);
                    }
                    if (rectBorder.Width > 1 && rectBorder.Height > 1)
                    {
                        e.Graphics.DrawRectangle(new Pen(new SolidBrush(SystemColors.ControlDark), 1.0f), rectBorder);
                        rectBorder.Inflate(-1, -1);
                    }
                    if (rectBorder.Width > 1 && rectBorder.Height > 1)
                    {
                        e.Graphics.DrawRectangle(new Pen(new SolidBrush(SystemColors.ControlDarkDark), 1.0f), rectBorder);
                    }
                }
            }
        }
 
        private  void ResetPosition()
        {
            if (Direction == AnimateDirection.LeftToRight)
            {
                this.SetBounds(this.Bounds.Left, this.Bounds.Top, 1, this.Bounds.Height);
            }
            else
            {
                this.SetBounds(this.Bounds.Left, this.Bounds.Top, this.Bounds.Width, 1);
            }
        }
 
        private  void AniFunc()
        {
            try
            {
                Expand();
                if (WaitForCloseRequest(Delay))
                {
                    throw new CloseRequestedException();
                }
                if (!IsActivated)
                {
                    Contract();
                }
            }
            catch (CloseRequestedException)
            {
                CloseRequested = false;
            }
            if (!IsActivated)
            {
                EndAnimation();
            }
            CloseRequested = false;
        }
 
        private  void DoMove(int iDelta)
        {
            Rectangle newRect;
            if (Direction == AnimateDirection.LeftToRight)
            {
                newRect = new Rectangle(this.Bounds.Left, this.Bounds.Top,
                    this.Bounds.Width + iDelta, this.Bounds.Height);
            }
            else
            {
                newRect = new Rectangle(this.Bounds.Left, this.Bounds.Top - iDelta,
                    this.Bounds.Width, this.Bounds.Height + iDelta);
            }
            this.SetBounds(newRect.Left, newRect.Top, newRect.Width, newRect.Height);
            if (Direction == AnimateDirection.LeftToRight)
            {
                this.Invalidate(this.RectangleToClient(new Rectangle(newRect.Right - iDelta - m_iBorderWidth - 2, newRect.Top, iDelta + m_iBorderWidth + 2, newRect.Height)));
            }
            else
            {
                this.Invalidate(this.RectangleToClient(new Rectangle(newRect.Left, newRect.Bottom - iDelta - m_iBorderWidth - 2, newRect.Width, iDelta + m_iBorderWidth)));
            }
        }
 
        private  void InitLocation()
        {
            if (this.Handle != IntPtr.Zero)
            {
                if (m_baseForm != null)
                {
                    Point ptNew;
                    if (this.Direction == AnimateDirection.LeftToRight)
                        ptNew = new Point(m_baseForm.StartLocation.X + m_oldBounds.Width + 1, m_baseForm.StartLocation.Y);
                    else
                        ptNew = new Point(m_baseForm.StartLocation.X, m_baseForm.StartLocation.Y - m_oldBounds.Height - 1);
                    Rectangle rcScreen = Screen.PrimaryScreen.Bounds;
                    if ((ptNew.X > rcScreen.Right) || (ptNew.Y < rcScreen.Top))
                        throw new OffDisplayException();
                    this.Location = ptNew;
                }
                else if (Placement == FormPlacement.Tray)
                {
                    InitTrayLocation();
                }
                else if (Placement == FormPlacement.Centered)
                {
                    InitCenterLocation();
                }
                else if (Placement == FormPlacement.Normal)
                {
                    this.Location = m_origLocation;
                }
                this.StartLocation = this.Location;
            }
        }
 
        private  void InitTrayLocation()
        {
            AppBarInfo info = new AppBarInfo();
            info.GetSystemTaskBarPosition();
            Rectangle rcWorkArea = info.WorkArea;
            int x = 0, y = 0;
   if (info.Edge == AppBarInfo.ScreenEdge.Left)
            {
                x = rcWorkArea.Left + 2;
                y = rcWorkArea.Bottom - 5;
            }
            else if (info.Edge == AppBarInfo.ScreenEdge.Bottom)
            {
                x = rcWorkArea.Right - m_oldBounds.Width - 5;
                y = rcWorkArea.Bottom - 1;
            }
            else if (info.Edge == AppBarInfo.ScreenEdge.Top)
            {
                x = rcWorkArea.Right - m_oldBounds.Width - 5;
                y = rcWorkArea.Top + m_oldBounds.Height + 1;
            }
            else if (info.Edge == AppBarInfo.ScreenEdge.Right)
            {
                x = rcWorkArea.Right - m_oldBounds.Width - 5;
                y = rcWorkArea.Bottom - 1;
            }
            SetWindowPos(this.Handle, (IntPtr)HWND_TOPMOST, x, y, 0, 0,
                SWP_HIDEWINDOW | SWP_NOSIZE | SWP_NOACTIVATE);
        }
 
        private  void InitCenterLocation()
        {
            AppBarInfo info = new AppBarInfo();
            Rectangle workArea = info.WorkArea;
            this.Location = new Point((workArea.Left + (workArea.Width / 2)) - (m_oldBounds.Width / 2),
                (workArea.Top + (workArea.Height / 2)) + (m_oldBounds.Height / 2));
        }
 
        private  void Expand()
        {
            for (int i = 0; i < m_iAdjSpeed; i++)
            {
                ThreadSafeResize(true, m_iDelta);
                Thread.Sleep(m_iInterval);
            }
            ThreadSafeResize(true, m_iLastDelta);
            OnExpanded(EventArgs.Empty);
        }
 
        private  void Contract()
        {
   if (!this.Disposing && !this.IsDisposed) {
    for (int i = 0; i < m_iAdjSpeed; i++) {
     if (!this.Disposing && !this.IsDisposed)
      ThreadSafeResize(false, m_iDelta);
     else
      break;
     Thread.Sleep(m_iInterval);
    }
   }
   if (!this.Disposing && !this.IsDisposed)
    ThreadSafeResize(false, m_iLastDelta);
            RemoveFromStack();
        }
 
        private  void AddToStack()
        {
            if (m_stackMode != StackMode.None)
            {
                m_baseForm = (AniForm)s_currentForms.Push(this, m_stackMode);
            }
        }
 
        private  void RemoveFromStack()
        {
            s_currentForms.Pop(this, StackMode);
            m_baseForm = null;
        }
 
        private  void EndAnimation()
        {
   GuiInvoker.InvokeAsync(this,
            delegate
            {
                if (Visible)
                {
                    ShowWindow(this.Handle, SW_HIDE);
                }
                Animating = false;
                OnAnimatingDone(EventArgs.Empty);
                if (!this.IsDisposed && m_bAutoDispose)
                {
                    Hide();
                    Close();
                    Dispose();
                }
            });
        }
 
        private  void SetClosedEvent()
        {
            m_eventClosed.Set();
        }
 
        private  void ResetClosedEvent()
        {
            m_eventClosed.Reset();
        }
 
        private  bool WaitForCloseRequest(int iTime)
        {
            if (Animating)
            {
                bool bReturn = m_eventClosed.WaitOne(iTime, false);
                return bReturn;
            }
            return false;
        }
 
        protected override  void OnActivated(EventArgs e)
        {
            IsActivated = true;
            base.OnActivated(e);
        }
 
        protected override  void OnDeactivate(EventArgs e)
        {
            if (IsActivated && !Persistent)
            {
                Contract();
                IsActivated = false;
                EndAnimation();
            }
            base.OnDeactivate(e);
        }
 
        protected override  void OnClosed(EventArgs e)
        {
            Animating = false;
            RemoveFromStack();
            base.OnClosed(e);
        }
 
        protected override  void OnResize(System.EventArgs e)
        {
            if (this.DesignMode)
            {
                this.Invalidate();
            }
            base.OnResize(e);
        }
 
        protected override  void OnLoad(System.EventArgs e)
        {
            m_oldBounds = this.Bounds;
            base.OnLoad(e);
        }
 
        protected override  void OnVisibleChanged(System.EventArgs e)
        {
            if (this.Visible == false)
            {
                Animating = false;
                RemoveFromStack();
            }
            base.OnVisibleChanged(e);
        }
 
        protected override  void OnMouseDown(System.Windows.Forms.MouseEventArgs e)
        {
            int xOffset;
            int yOffset;
            if (e.Button == MouseButtons.Left)
            {
                xOffset = -e.X;
                yOffset = -e.Y;
                mouseOffset = new Point(xOffset, yOffset);
                isMouseDown = true;
            }
            base.OnMouseDown(e);
        }
 
        protected override  void OnMouseMove(System.Windows.Forms.MouseEventArgs e)
        {
            if (isMouseDown)
            {
                Point mousePos = Control.MousePosition;
                mousePos.Offset(mouseOffset.X, mouseOffset.Y);
                Location = mousePos;
            }
            base.OnMouseMove(e);
        }
 
        protected override  void OnMouseUp(System.Windows.Forms.MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                isMouseDown = false;
            }
            base.OnMouseUp(e);
        }

	}
	
    public class  AppBarInfo {
		
        private  const int ABM_NEW = 0x00000000; 
        private  const int ABM_REMOVE = 0x00000001; 
        private  const int ABM_QUERYPOS = 0x00000002; 
        private  const int ABM_SETPOS = 0x00000003; 
        private  const int ABM_GETSTATE = 0x00000004; 
        private  const int ABM_GETTASKBARPOS = 0x00000005; 
        private  const int ABM_ACTIVATE = 0x00000006; 
        private  const int ABM_GETAUTOHIDEBAR = 0x00000007; 
        private  const int ABM_SETAUTOHIDEBAR = 0x00000008; 
        private  const int ABE_LEFT = 0; 
        private  const int ABE_TOP = 1; 
        private  const int ABE_RIGHT = 2; 
        private  const int ABE_BOTTOM = 3; 
        private  const System.UInt32 SPI_GETWORKAREA = 0x0030; 
        [StructLayout(LayoutKind.Sequential)] 
        private struct  RECT {
			
            public  System.Int32 left;
 
            public  System.Int32 top;
 
            public  System.Int32 right;
 
            public  System.Int32 bottom;

		}
		
        [StructLayout(LayoutKind.Sequential)] 
        private struct  APPBARDATA {
			
            public  System.UInt32 cbSize;
 
            public  System.IntPtr hWnd;
 
            public  System.UInt32 uCallbackMessage;
 
            public  System.UInt32 uEdge;
 
            public  RECT rc;
 
            public  System.Int32 lParam;

		}
		
        [DllImport("user32.dll")] 
        private static extern  System.IntPtr FindWindow(String lpClassName, String lpWindowName);
 
        [DllImport("shell32.dll")] 
        private static extern  System.UInt32 SHAppBarMessage(System.UInt32 dwMessage, ref APPBARDATA data);
 
        [DllImport("user32.dll")] 
        private static extern  System.Int32 SystemParametersInfo(System.UInt32 uiAction, System.UInt32 uiParam,
            System.IntPtr pvParam, System.UInt32 fWinIni);
 
        private  APPBARDATA m_data;
 
        public enum  ScreenEdge 
        {
            Undefined = -1,
            Left = ABE_LEFT,
            Top = ABE_TOP,
            Right = ABE_RIGHT,
            Bottom = ABE_BOTTOM
        } 
        public  ScreenEdge Edge
        {
            get
            {
                return (ScreenEdge)m_data.uEdge;
            }
        }
 
        public  Rectangle WorkArea
        {
            get
            {
                Int32 bResult = 0;
                RECT rc = new RECT();
                IntPtr rawRect = System.Runtime.InteropServices.Marshal.AllocHGlobal(System.Runtime.InteropServices.Marshal.SizeOf(rc));
                bResult = SystemParametersInfo(SPI_GETWORKAREA, 0, rawRect, 0);
                rc = (RECT)System.Runtime.InteropServices.Marshal.PtrToStructure(rawRect, rc.GetType());
                if (bResult == 1)
                {
                    System.Runtime.InteropServices.Marshal.FreeHGlobal(rawRect);
                    return new Rectangle(rc.left, rc.top, rc.right - rc.left, rc.bottom - rc.top);
                }
                return new Rectangle(0, 0, 0, 0);
            }
        }
 
        public  void GetPosition(string strClassName, string strWindowName)
        {
            m_data = new APPBARDATA();
            m_data.cbSize = (UInt32)System.Runtime.InteropServices.Marshal.SizeOf(m_data.GetType());
            IntPtr hWnd = FindWindow(strClassName, strWindowName);
            if (hWnd != IntPtr.Zero)
            {
                UInt32 uResult = SHAppBarMessage(ABM_GETTASKBARPOS, ref m_data);
                if (uResult == 1)
                {
                }
                else
                {
                    throw new Exception("Failed to communicate with the given AppBar");
                }
            }
            else
            {
                throw new Exception("Failed to find an AppBar that matched the given criteria");
            }
        }
 
        public  void GetSystemTaskBarPosition()
        {
            GetPosition("Shell_TrayWnd", null);
        }

	}
	
    internal delegate  void  WndMover (int iDelta);
	
    internal class  CloseRequestedException  : Exception {
		
        public  CloseRequestedException() : base("Close requested")
        {
        }

	}
	
    public enum  AnimateDirection 
    {
        LeftToRight,
        BottomToTop
    } 
    public enum  BackgroundMode 
    {
        Normal,
        GradientHorizontal,
        GradientVertical
    } 
    public enum  FormPlacement 
    {
        Normal,
        Tray,
        Centered
    } 
    public enum  BorderStyle 
    {
        None,
        FixedSingle,
        Raised
    } 
    public enum  StackMode 
    {
        None,
        Top,
        FirstAvailable
    } 
    internal class  StackArray  : ArrayList {
		
        private  ArrayList m_syncList = null;
 
        private  int m_iCount = 0;
 
        public  StackArray()
        {
            m_syncList = ArrayList.Synchronized(this);
        }
 
        public  object Peek(StackMode stackMode)
        {
            if (m_syncList.Count > 0)
                return m_syncList[m_syncList.Count - 1];
            else
                return null;
        }
 
        public  object Push(object newObject, StackMode stackMode)
        {
            bool bInserted = false;
            object previous = null;
            if (stackMode == StackMode.None)
                return null;
            if (stackMode == StackMode.FirstAvailable)
            {
                for (int i = 0; i < m_syncList.Count; i++)
                {
                    if (m_syncList[i] == null)
                    {
                        m_syncList[i] = newObject;
                        bInserted = true;
                        break;
                    }
                    else
                    {
                        previous = m_syncList[i];
                    }
                }
                if (!bInserted)
                {
                    m_syncList.Add(newObject);
                }
            }
            else if (stackMode == StackMode.Top)
            {
                if (m_syncList.Count > 0)
                    previous = m_syncList[m_syncList.Count - 1];
                m_syncList.Add(newObject);
            }
            m_iCount++;
            return previous;
        }
 
        public  void Pop(object targetForm, StackMode stackMode)
        {
            if (stackMode == StackMode.None)
                return;
            if (stackMode == StackMode.FirstAvailable)
            {
                for (int i = 0; i < m_syncList.Count; i++)
                {
                    if (m_syncList[i] == targetForm)
                    {
                        m_syncList[i] = null;
                        m_iCount--;
                        break;
                    }
                }
            }
            else if (stackMode == StackMode.Top)
            {
                if (m_syncList.Contains(targetForm))
                {
                    m_syncList.Remove(targetForm);
                    m_iCount--;
                }
            }
            if (m_iCount == 0)
            {
                m_syncList.Clear();
            }
        }

	}
	
    [Serializable] 
    public class  OffDisplayException  : Exception {
		
        public  OffDisplayException() : base("off screen")
        {
        }

	}

}
