using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Windows.Forms;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Presentation
{
    internal interface INotifyIconEx
    {
        uint IconId { get; }
        void NotifyClick();
        void NotifyDoubleClick();
        void NotifyShowContextMenu();
        void NotifyClickBalloon();
        void NotifyRecreateIcon();
        void NotifyBalloonShown();
        void NotifyBalloonHidden();
    }
    public class NotifyIconEx : Component, INotifyIconEx
    {
        private const uint MSG_ICON_CALLBACK = WM_USER;
        private const uint WM_USER = 0x400;
        private static readonly INotifyIconManager manager = new NotifyIconTarget();
        private uint icon_id;
        private ContextMenu contextMenu;
        private Icon icon;
        private string tooltip = string.Empty;
        private bool visible;
        private bool m_doubleClick;
        private bool HasBeenCreated
        {
            get { return icon_id != 0; }
        }
        public string Text
        {
            set
            {
                if (tooltip != value)
                {
                    tooltip = value;
                    CreateOrUpdate();
                }
            }
            get { return tooltip; }
        }
        public Icon Icon
        {
            set
            {
                icon = value;
                CreateOrUpdate();
            }
            get { return icon; }
        }
        public ContextMenu ContextMenu
        {
            set { contextMenu = value; }
            get { return contextMenu; }
        }
        public bool Visible
        {
            set
            {
                if (visible != value)
                {
                    visible = value;
                    CreateOrUpdate();
                }
            }
            get { return visible; }
        }
        uint INotifyIconEx.IconId
        {
            get { return icon_id; }
        }
        void INotifyIconEx.NotifyClick()
        {
            if (!m_doubleClick)
            {
                if (Click != null) Click(this, EventArgs.Empty);
            }
            m_doubleClick = false;
        }
        void INotifyIconEx.NotifyDoubleClick()
        {
            m_doubleClick = true;
            if (DoubleClick != null) DoubleClick(this, EventArgs.Empty);
        }
        void INotifyIconEx.NotifyShowContextMenu()
        {
            if (contextMenu == null)
                return;
            POINT point = new POINT();
            GetCursorPos(ref point);
            SetForegroundWindow(manager.WindowHandle);
            contextMenu.GetType().InvokeMember(
                "OnPopup",
                BindingFlags.NonPublic | BindingFlags.InvokeMethod | BindingFlags.Instance,
                null, contextMenu, new Object[] {EventArgs.Empty});
            TrackPopupMenuEx(contextMenu.Handle, 64, point.x, point.y, manager.WindowHandle, IntPtr.Zero);
        }
        void INotifyIconEx.NotifyClickBalloon()
        {
            if (BalloonClick != null) BalloonClick(this, EventArgs.Empty);
        }
        void INotifyIconEx.NotifyRecreateIcon()
        {
            if (HasBeenCreated)
                Create();
        }
        void INotifyIconEx.NotifyBalloonShown()
        {
            if (BalloonShown != null) BalloonShown(this, EventArgs.Empty);
        }
        void INotifyIconEx.NotifyBalloonHidden()
        {
            if (BalloonDismissed != null) BalloonDismissed(this, EventArgs.Empty);
        }
        public event EventHandler Click;
        public event EventHandler DoubleClick;
        public event EventHandler BalloonClick;
        public event EventHandler BalloonShown;
        public event EventHandler BalloonDismissed;
        private void SetComonNotifyIconFields(ref NotifyIconData data)
        {
            if (icon != null)
                data.hIcon = icon.Handle;
            data.uFlags |= NotifyFlags.Icon;
            data.szTip = tooltip;
            data.uFlags |= NotifyFlags.Tip;
            data.uFlags |= NotifyFlags.State;
            if (!visible)
                data.dwState = NotifyState.Hidden;
            data.dwStateMask |= NotifyState.Hidden;
        }
        private void CreateNewIcon()
        {
            if (HasBeenCreated)
                return;
            if (icon == null)
                return;
            icon_id = manager.GetNextId();
            Create();
            SetIconVersion();
            manager.RegisterIcon(this);
        }
        private void CreateOrUpdate()
        {
            if (DesignMode)
                return;
            if (HasBeenCreated)
                Update();
            else
                CreateNewIcon();
        }
        private NotifyIconData NotifyIconDataFactory()
        {
            NotifyIconData data = new NotifyIconData();
            data.cbSize = (uint) Marshal.SizeOf(data);
            data.hWnd = manager.WindowHandle;
            data.uID = icon_id;
            return data;
        }
        private void SetIconVersion()
        {
            NotifyIconData data = NotifyIconDataFactory();
            data.uTimeoutOrVersion = (uint) NotifyVersion.Windows2000;
            Shell_NotifyIcon(NotifyCommand.SetVersion, ref data);
        }
        private void Create()
        {
            NotifyIconData data = NotifyIconDataFactory();
            data.uCallbackMessage = MSG_ICON_CALLBACK;
            data.uFlags |= NotifyFlags.Message;
            SetComonNotifyIconFields(ref data);
            Shell_NotifyIcon(NotifyCommand.Add, ref data);
        }
        private void Update()
        {
            NotifyIconData data = NotifyIconDataFactory();
            SetComonNotifyIconFields(ref data);
            Shell_NotifyIcon(NotifyCommand.Modify, ref data);
        }
        protected override void Dispose(bool disposing)
        {
            Remove();
            base.Dispose(disposing);
        }
        public void Remove()
        {
            if (!HasBeenCreated) return;
            manager.UnregisterIcon(this);
            NotifyIconData data = NotifyIconDataFactory();
            Shell_NotifyIcon(NotifyCommand.Delete, ref data);
            icon_id = 0;
        }
        public void ShowBalloon(string title, string text, NotifyInfoFlags type, int timeoutInMilliseconds)
        {
            if (timeoutInMilliseconds < 0)
                throw new ArgumentException("The parameter must be positive", "timeoutInMilliseconds");
            NotifyIconData data = NotifyIconDataFactory();
            data.uFlags = NotifyFlags.Info;
            data.uTimeoutOrVersion = (uint) timeoutInMilliseconds;
            data.szInfoTitle = title;
            data.szInfo = text;
            data.dwInfoFlags = type;
            Shell_NotifyIcon(NotifyCommand.Modify, ref data);
        }
        private interface INotifyIconManager
        {
            IntPtr WindowHandle { get; }
            uint GetNextId();
            void RegisterIcon(INotifyIconEx icon);
            void UnregisterIcon(INotifyIconEx icon);
        }
        private enum NotifyCommand
        {
            Add = 0,
            Modify = 1,
            Delete = 2,
            SetVersion = 4,
        }
        [Flags]
        private enum NotifyFlags
        {
            Message = 0x01,
            Icon = 0x02,
            Tip = 0x04,
            State = 0x08,
            Info = 0x10,
        }
        [Flags]
        private enum NotifyState
        {
            Hidden = 0x01
        }
        private enum NotifyVersion
        {
            Old = 0,
            Windows2000 = 3,
            Vista = 4,
        }
        private class NotifyIconTarget : Form, INotifyIconManager
        {
            private const uint NIN_BALLOONHIDE = WM_USER+3;
            private const uint NIN_BALLOONSHOW = WM_USER+2;
            private const uint NIN_BALLOONTIMEOUT = WM_USER+4;
            private const uint NIN_BALLOONUSERCLICK = WM_USER+5;
            private const uint WM_LBUTTONDBLCLK = 0x203;
            private const uint WM_LBUTTONDOWN = 0x201;
            private const uint WM_LBUTTONUP = 0x202;
            private const uint WM_MOUSEMOVE = 0x200;
            private const uint WM_RBUTTONUP = 0x205;
            private static uint next_icon_id;
            private readonly List<INotifyIconEx> icons = new List<INotifyIconEx>();
            private readonly uint WM_TASKBARCREATED = RegisterWindowMessage("TaskbarCreated");
            private IntPtr cachedWindowHandle = IntPtr.Zero;
            public NotifyIconTarget()
            {
                Text = "Hidden NotifyIconTarget Window";
            }
            public uint GetNextId()
            {
                next_icon_id++;
                return next_icon_id;
            }
            public void RegisterIcon(INotifyIconEx notifyIcon)
            {
                icons.Add(notifyIcon);
            }
            public void UnregisterIcon(INotifyIconEx notifyIcon)
            {
                icons.Remove(notifyIcon);
            }
            public IntPtr WindowHandle
            {
                get
                {
                    if (cachedWindowHandle == IntPtr.Zero)
                        cachedWindowHandle = Handle;
                    return cachedWindowHandle;
                }
            }
            protected override void DefWndProc(ref Message msg)
            {
                if (msg.Msg == MSG_ICON_CALLBACK)
                {
                    ForwardEvents(msg);
                }
                else if (msg.Msg == WM_TASKBARCREATED)
                {
                    icons.ForEach(delegate(INotifyIconEx x) {x.NotifyRecreateIcon(); });
                }
                else
                {
                    base.DefWndProc(ref msg);
                }
            }
            private void ForwardEvents(Message msg)
            {
                uint msgId = (uint) msg.LParam;
                uint which_icon = (uint) msg.WParam;
                INotifyIconEx icon = icons.Find(delegate(INotifyIconEx x) { return x.IconId == which_icon; });
                if (icon == null) return;
                switch (msgId)
                {
                    case WM_LBUTTONUP:
                        icon.NotifyClick();
                        break;
                    case WM_LBUTTONDBLCLK:
                        icon.NotifyDoubleClick();
                        break;
                    case WM_RBUTTONUP:
                        icon.NotifyShowContextMenu();
                        break;
                    case NIN_BALLOONUSERCLICK:
                        icon.NotifyClickBalloon();
                        break;
                    case NIN_BALLOONSHOW:
                        icon.NotifyBalloonShown();
                        break;
                    case NIN_BALLOONTIMEOUT:
                        icon.NotifyBalloonHidden();
                        break;
                }
            }
        }
        [DllImport("shell32.dll")]
        private static extern Int32 Shell_NotifyIcon(NotifyCommand cmd, ref NotifyIconData data);
        [DllImport("user32.dll")]
        private static extern Int32 TrackPopupMenuEx(IntPtr hMenu,
                                                     UInt32 uFlags,
                                                     Int32 x,
                                                     Int32 y,
                                                     IntPtr hWnd,
                                                     IntPtr ignore);
        [DllImport("user32.dll")]
        private static extern Int32 GetCursorPos(ref POINT point);
        [DllImport("user32.dll")]
        private static extern Int32 SetForegroundWindow(IntPtr hWnd);
        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        private static extern uint RegisterWindowMessage(string lpString);
        [StructLayout(LayoutKind.Sequential)]
        private struct NotifyIconData
        {
            public UInt32 cbSize;
            public IntPtr hWnd;
            public UInt32 uID;
            public NotifyFlags uFlags;
            public UInt32 uCallbackMessage;
            public IntPtr hIcon;
            [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)] public String szTip;
            public NotifyState dwState;
            public NotifyState dwStateMask;
            [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 256)] public String szInfo;
            public UInt32 uTimeoutOrVersion;
            [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 64)] public String szInfoTitle;
            public NotifyInfoFlags dwInfoFlags;
        }
        [StructLayout(LayoutKind.Sequential)]
        private struct POINT
        {
            public Int32 x;
            public Int32 y;
        }
    }
}
