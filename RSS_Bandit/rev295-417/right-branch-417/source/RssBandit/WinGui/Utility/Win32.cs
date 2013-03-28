using System;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Security;
using System.Security.Permissions;
using System.Text;
using System.Threading;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using Microsoft.Win32;
using NewsComponents.Utils;
using RssBandit.Resources;
using Logger = RssBandit.Common.Logging;
namespace RssBandit
{
 [System.Security.SuppressUnmanagedCodeSecurity]
 internal sealed class Win32 {
  public enum ShowWindowStyles : short {
   SW_HIDE = 0,
   SW_SHOWNORMAL = 1,
   SW_NORMAL = 1,
   SW_SHOWMINIMIZED = 2,
   SW_SHOWMAXIMIZED = 3,
   SW_MAXIMIZE = 3,
   SW_SHOWNOACTIVATE = 4,
   SW_SHOW = 5,
   SW_MINIMIZE = 6,
   SW_SHOWMINNOACTIVE = 7,
   SW_SHOWNA = 8,
   SW_RESTORE = 9,
   SW_SHOWDEFAULT = 10,
   SW_FORCEMINIMIZE = 11,
   SW_MAX = 11
  }
  public enum Message {
   WM_NULL = 0x0000,
   WM_CREATE = 0x0001,
   WM_DESTROY = 0x0002,
   WM_MOVE = 0x0003,
   WM_SIZE = 0x0005,
   WM_ACTIVATE = 0x0006,
   WM_SETFOCUS = 0x0007,
   WM_KILLFOCUS = 0x0008,
   WM_ENABLE = 0x000A,
   WM_SETREDRAW = 0x000B,
   WM_SETTEXT = 0x000C,
   WM_GETTEXT = 0x000D,
   WM_GETTEXTLENGTH = 0x000E,
   WM_PAINT = 0x000F,
   WM_CLOSE = 0x0010,
   WM_QUERYENDSESSION = 0x0011,
   WM_QUIT = 0x0012,
   WM_QUERYOPEN = 0x0013,
   WM_ERASEBKGND = 0x0014,
   WM_SYSCOLORCHANGE = 0x0015,
   WM_ENDSESSION = 0x0016,
   WM_SHOWWINDOW = 0x0018,
   WM_WININICHANGE = 0x001A,
   WM_SETTINGCHANGE = 0x001A,
   WM_DEVMODECHANGE = 0x001B,
   WM_ACTIVATEAPP = 0x001C,
   WM_FONTCHANGE = 0x001D,
   WM_TIMECHANGE = 0x001E,
   WM_CANCELMODE = 0x001F,
   WM_SETCURSOR = 0x0020,
   WM_MOUSEACTIVATE = 0x0021,
   WM_CHILDACTIVATE = 0x0022,
   WM_QUEUESYNC = 0x0023,
   WM_GETMINMAXINFO = 0x0024,
   WM_PAINTICON = 0x0026,
   WM_ICONERASEBKGND = 0x0027,
   WM_NEXTDLGCTL = 0x0028,
   WM_SPOOLERSTATUS = 0x002A,
   WM_DRAWITEM = 0x002B,
   WM_MEASUREITEM = 0x002C,
   WM_DELETEITEM = 0x002D,
   WM_VKEYTOITEM = 0x002E,
   WM_CHARTOITEM = 0x002F,
   WM_SETFONT = 0x0030,
   WM_GETFONT = 0x0031,
   WM_SETHOTKEY = 0x0032,
   WM_GETHOTKEY = 0x0033,
   WM_QUERYDRAGICON = 0x0037,
   WM_COMPAREITEM = 0x0039,
   WM_GETOBJECT = 0x003D,
   WM_COMPACTING = 0x0041,
   WM_COMMNOTIFY = 0x0044 ,
   WM_WINDOWPOSCHANGING = 0x0046,
   WM_WINDOWPOSCHANGED = 0x0047,
   WM_POWER = 0x0048,
   WM_COPYDATA = 0x004A,
   WM_CANCELJOURNAL = 0x004B,
   WM_NOTIFY = 0x004E,
   WM_INPUTLANGCHANGEREQUEST = 0x0050,
   WM_INPUTLANGCHANGE = 0x0051,
   WM_TCARD = 0x0052,
   WM_HELP = 0x0053,
   WM_USERCHANGED = 0x0054,
   WM_NOTIFYFORMAT = 0x0055,
   WM_CONTEXTMENU = 0x007B,
   WM_STYLECHANGING = 0x007C,
   WM_STYLECHANGED = 0x007D,
   WM_DISPLAYCHANGE = 0x007E,
   WM_GETICON = 0x007F,
   WM_SETICON = 0x0080,
   WM_NCCREATE = 0x0081,
   WM_NCDESTROY = 0x0082,
   WM_NCCALCSIZE = 0x0083,
   WM_NCHITTEST = 0x0084,
   WM_NCPAINT = 0x0085,
   WM_NCACTIVATE = 0x0086,
   WM_GETDLGCODE = 0x0087,
   WM_SYNCPAINT = 0x0088,
   WM_NCMOUSEMOVE = 0x00A0,
   WM_NCLBUTTONDOWN = 0x00A1,
   WM_NCLBUTTONUP = 0x00A2,
   WM_NCLBUTTONDBLCLK = 0x00A3,
   WM_NCRBUTTONDOWN = 0x00A4,
   WM_NCRBUTTONUP = 0x00A5,
   WM_NCRBUTTONDBLCLK = 0x00A6,
   WM_NCMBUTTONDOWN = 0x00A7,
   WM_NCMBUTTONUP = 0x00A8,
   WM_NCMBUTTONDBLCLK = 0x00A9,
   WM_NCXBUTTONDOWN = 0x00AB,
   WM_NCXBUTTONUP = 0x00AC,
   WM_KEYDOWN = 0x0100,
   WM_KEYUP = 0x0101,
   WM_CHAR = 0x0102,
   WM_DEADCHAR = 0x0103,
   WM_SYSKEYDOWN = 0x0104,
   WM_SYSKEYUP = 0x0105,
   WM_SYSCHAR = 0x0106,
   WM_SYSDEADCHAR = 0x0107,
   WM_KEYLAST = 0x0108,
   WM_IME_STARTCOMPOSITION = 0x010D,
   WM_IME_ENDCOMPOSITION = 0x010E,
   WM_IME_COMPOSITION = 0x010F,
   WM_IME_KEYLAST = 0x010F,
   WM_INITDIALOG = 0x0110,
   WM_COMMAND = 0x0111,
   WM_SYSCOMMAND = 0x0112,
   WM_TIMER = 0x0113,
   WM_HSCROLL = 0x0114,
   WM_VSCROLL = 0x0115,
   WM_INITMENU = 0x0116,
   WM_INITMENUPOPUP = 0x0117,
   WM_MENUSELECT = 0x011F,
   WM_MENUCHAR = 0x0120,
   WM_ENTERIDLE = 0x0121,
   WM_MENURBUTTONUP = 0x0122,
   WM_MENUDRAG = 0x0123,
   WM_MENUGETOBJECT = 0x0124,
   WM_UNINITMENUPOPUP = 0x0125,
   WM_MENUCOMMAND = 0x0126,
   WM_CTLCOLORMSGBOX = 0x0132,
   WM_CTLCOLOREDIT = 0x0133,
   WM_CTLCOLORLISTBOX = 0x0134,
   WM_CTLCOLORBTN = 0x0135,
   WM_CTLCOLORDLG = 0x0136,
   WM_CTLCOLORSCROLLBAR = 0x0137,
   WM_CTLCOLORSTATIC = 0x0138,
   WM_MOUSEMOVE = 0x0200,
   WM_LBUTTONDOWN = 0x0201,
   WM_LBUTTONUP = 0x0202,
   WM_LBUTTONDBLCLK = 0x0203,
   WM_RBUTTONDOWN = 0x0204,
   WM_RBUTTONUP = 0x0205,
   WM_RBUTTONDBLCLK = 0x0206,
   WM_MBUTTONDOWN = 0x0207,
   WM_MBUTTONUP = 0x0208,
   WM_MBUTTONDBLCLK = 0x0209,
   WM_MOUSEWHEEL = 0x020A,
   WM_XBUTTONDOWN = 0x020B,
   WM_XBUTTONUP = 0x020C,
   WM_XBUTTONDBLCLK = 0x020D,
   WM_PARENTNOTIFY = 0x0210,
   WM_ENTERMENULOOP = 0x0211,
   WM_EXITMENULOOP = 0x0212,
   WM_NEXTMENU = 0x0213,
   WM_SIZING = 0x0214,
   WM_CAPTURECHANGED = 0x0215,
   WM_MOVING = 0x0216,
   WM_DEVICECHANGE = 0x0219,
   WM_MDICREATE = 0x0220,
   WM_MDIDESTROY = 0x0221,
   WM_MDIACTIVATE = 0x0222,
   WM_MDIRESTORE = 0x0223,
   WM_MDINEXT = 0x0224,
   WM_MDIMAXIMIZE = 0x0225,
   WM_MDITILE = 0x0226,
   WM_MDICASCADE = 0x0227,
   WM_MDIICONARRANGE = 0x0228,
   WM_MDIGETACTIVE = 0x0229,
   WM_MDISETMENU = 0x0230,
   WM_ENTERSIZEMOVE = 0x0231,
   WM_EXITSIZEMOVE = 0x0232,
   WM_DROPFILES = 0x0233,
   WM_MDIREFRESHMENU = 0x0234,
   WM_IME_SETCONTEXT = 0x0281,
   WM_IME_NOTIFY = 0x0282,
   WM_IME_CONTROL = 0x0283,
   WM_IME_COMPOSITIONFULL = 0x0284,
   WM_IME_SELECT = 0x0285,
   WM_IME_CHAR = 0x0286,
   WM_IME_REQUEST = 0x0288,
   WM_IME_KEYDOWN = 0x0290,
   WM_IME_KEYUP = 0x0291,
   WM_MOUSEHOVER = 0x02A1,
   WM_MOUSELEAVE = 0x02A3,
   WM_CUT = 0x0300,
   WM_COPY = 0x0301,
   WM_PASTE = 0x0302,
   WM_CLEAR = 0x0303,
   WM_UNDO = 0x0304,
   WM_RENDERFORMAT = 0x0305,
   WM_RENDERALLFORMATS = 0x0306,
   WM_DESTROYCLIPBOARD = 0x0307,
   WM_DRAWCLIPBOARD = 0x0308,
   WM_PAINTCLIPBOARD = 0x0309,
   WM_VSCROLLCLIPBOARD = 0x030A,
   WM_SIZECLIPBOARD = 0x030B,
   WM_ASKCBFORMATNAME = 0x030C,
   WM_CHANGECBCHAIN = 0x030D,
   WM_HSCROLLCLIPBOARD = 0x030E,
   WM_QUERYNEWPALETTE = 0x030F,
   WM_PALETTEISCHANGING = 0x0310,
   WM_PALETTECHANGED = 0x0311,
   WM_HOTKEY = 0x0312,
   WM_PRINT = 0x0317,
   WM_PRINTCLIENT = 0x0318,
   WM_HANDHELDFIRST = 0x0358,
   WM_HANDHELDLAST = 0x035F,
   WM_AFXFIRST = 0x0360,
   WM_AFXLAST = 0x037F,
   WM_PENWINFIRST = 0x0380,
   WM_PENWINLAST = 0x038F,
   WM_APP = 0x8000,
   WM_USER = 0x0400
  }
  private const int GWL_STYLE = -16;
  public const int TVS_INFOTIP = 0x0800;
  [StructLayout(LayoutKind.Sequential)]
  internal struct POINT {
   public int x;
   public int y;
  }
  [Serializable(), StructLayout(LayoutKind.Sequential)]
  internal struct RECT {
   public int left;
   public int top;
   public int right;
   public int bottom;
  }
  [StructLayout(LayoutKind.Sequential)]
  internal struct API_STARTUPINFO {
   public int cb;
   public IntPtr lpReserved;
   public IntPtr lpDesktop;
   public IntPtr lpTitle;
   public int dwX;
   public int dwY;
   public int dwXSize;
   public int dwYSize;
   public int dwXCountChars;
   public int dwYCountChars;
   public int dwFillAttribute;
   public int dwFlags;
   public short wShowWindow;
   public short cbReserved2;
   public IntPtr lpReserved2;
   public IntPtr hStdInput;
   public IntPtr hStdOutput;
   public IntPtr hStdError;
  }
  [StructLayout(LayoutKind.Sequential)]
  internal class HDITEM {
   public int mask = 0;
   public int cxy = 0;
   public IntPtr pszText = IntPtr.Zero;
   public IntPtr hbm = IntPtr.Zero;
   public int cchTextMax = 0;
   public int fmt = 0;
            public IntPtr lParam = IntPtr.Zero;
   public int iImage = 0;
   public int iOrder = 0;
  };
  [Serializable(),
  StructLayout(LayoutKind.Sequential)]
  internal struct DLLVERSIONINFO {
   public int cbSize;
   public int dwMajorVersion;
   public int dwMinorVersion;
   public int dwBuildNumber;
   public int dwPlatformID;
  }
  [StructLayout(LayoutKind.Sequential)]
  private struct OSVERSIONINFOEX {
   public int dwOSVersionInfoSize;
   public int dwMajorVersion;
   public int dwMinorVersion;
   public int dwBuildNumber;
   public int dwPlatformId;
   [MarshalAs(UnmanagedType.ByValTStr, SizeConst=128)]
   public string szCSDVersion;
   public UInt16 wServicePackMajor;
   public UInt16 wServicePackMinor;
   public UInt16 wSuiteMask;
   public byte wProductType;
   public byte wReserved;
  }
  [DllImport("User32.dll")] public static extern
            IntPtr SendMessage(IntPtr hWnd, int msg, int wParam, IntPtr lParam);
  [DllImport("user32", EntryPoint="SendMessage")] public static extern
   IntPtr SendMessage2(IntPtr Handle, int msg, IntPtr wParam, HDITEM lParam);
  [DllImport("user32", EntryPoint="SendMessage")] public static extern
   IntPtr SendMessage3(IntPtr Handle, int msg, IntPtr wParam, IntPtr lParam);
  [DllImport("User32.dll", SetLastError=true)] public static extern
   uint SetForegroundWindow (IntPtr hwnd);
  [DllImport("User32.dll", SetLastError=true)] public static extern
   uint SetForegroundWindow (HandleRef hwnd);
  [DllImport("user32.dll", CharSet=CharSet.Auto)] static public extern
   bool ShowWindow(IntPtr hWnd, ShowWindowStyles State);
  [DllImport("user32.dll")] public static extern
   bool ShowWindowAsync(IntPtr hWnd, int nCmdShow);
  [DllImport("user32.dll")] public static extern
   bool IsIconic(IntPtr hWnd);
  [DllImport("user32.dll", SetLastError=true)] public static extern
   IntPtr FindWindow(string lpClassName, string lpWindowName);
  [DllImport("user32.dll")] public static extern
   IntPtr GetWindowThreadProcessId(IntPtr hWnd, ref IntPtr ProcessId);
  [DllImport("user32.dll", SetLastError=true)] public static extern
   int GetWindowText(IntPtr hWnd, System.Text.StringBuilder title, int size);
  [DllImport("user32.dll")] public static extern
   int EnumWindows(EnumWindowsProc ewp, IntPtr lParam);
  [DllImport("User32", CharSet=CharSet.Auto)] public static extern
   int GetWindowLong(IntPtr hWnd, int Index);
  [DllImport("User32", CharSet=CharSet.Auto)] public static extern
   int SetWindowLong(IntPtr hWnd, int Index, int Value);
  [DllImport("user32.dll", CharSet=CharSet.Auto, ExactSpelling=true)] public static extern
   bool GetCursorPos(out POINT pt);
  [DllImport("user32.dll")] public static extern
   bool TrackPopupMenuEx(HandleRef hmenu, uint fuFlags, int x, int y, HandleRef hwnd, IntPtr lptpm);
  [DllImport("user32.dll")] public static extern
   bool PostMessage(HandleRef hWnd, uint msg, IntPtr wParam, IntPtr lParam);
  public delegate bool EnumWindowsProc(IntPtr hWnd, IntPtr lParam);
  [DllImport("Kernel32.dll", CharSet=CharSet.Auto)] public static extern
   void GetStartupInfo(ref API_STARTUPINFO info);
  [DllImport("Comctl32.dll")]
  public static extern int DllGetVersion(ref DLLVERSIONINFO pdvi);
  [ DllImport( "kernel32.dll", SetLastError=true )]
  private static extern bool GetVersionEx(ref OSVERSIONINFOEX osvi );
  [DllImport("Winmm.dll", CharSet=CharSet.Auto, SetLastError=true)]
  private static extern bool PlaySound(string sound, IntPtr hModule, SoundFlags fdwSound);
  [Flags]
  private enum SoundFlags : uint {
   SND_SYNC = 0x0000,
   SND_ASYNC = 0x0001,
   SND_NODEFAULT = 0x0002,
   SND_MEMORY = 0x0004,
   SND_LOOP = 0x0008,
   SND_NOSTOP = 0x0010,
   SND_NOWAIT = 0x00002000,
   SND_ALIAS = 0x00010000,
   SND_ALIAS_ID = 0x00110000,
   SND_FILENAME = 0x00020000,
   SND_RESOURCE = 0x00040004,
   SND_PURGE = 0x0040,
   SND_APPLICATION = 0x0080,
  }
  public static bool ApplicationSoundsAllowed = false;
  public static void PlaySound(string applicationSound) {
   if (! ApplicationSoundsAllowed)
    return;
   if (RssBanditApplication.PortableApplicationMode) {
    PlaySoundFromFile(applicationSound);
   } else {
    PlaySoundFromRegistry(applicationSound);
   }
  }
  private static void PlaySoundFromRegistry(string applicationSound) {
   try {
    switch (applicationSound) {
     case Resource.ApplicationSound.FeedDiscovered:
      PlaySound(applicationSound, IntPtr.Zero, SoundFlags.SND_APPLICATION |
       SoundFlags.SND_NOWAIT | SoundFlags.SND_NODEFAULT);
      break;
     case Resource.ApplicationSound.NewItemsReceived:
      PlaySound(applicationSound, IntPtr.Zero, SoundFlags.SND_APPLICATION |
       SoundFlags.SND_NOWAIT | SoundFlags.SND_NODEFAULT);
      break;
     case Resource.ApplicationSound.NewAttachmentDownloaded:
      PlaySound(applicationSound, IntPtr.Zero, SoundFlags.SND_APPLICATION |
       SoundFlags.SND_NOWAIT | SoundFlags.SND_NODEFAULT);
      break;
    }
   } catch (Exception ex) {
    int err = Marshal.GetLastWin32Error();
    if (err != 0)
     _log.Error("Error #" + err + " occured on playing sound '" + applicationSound + "'", ex);
    else
     _log.Error("Error playing sound '" + applicationSound + "'", ex);
   }
  }
  private static void PlaySoundFromFile(string applicationSound) {
   try {
    string soundFile = null;
    switch (applicationSound) {
     case Resource.ApplicationSound.FeedDiscovered:
      soundFile = Path.Combine(Application.StartupPath, @"Media\Feed Discovered.wav");
      break;
     case Resource.ApplicationSound.NewItemsReceived:
      soundFile = Path.Combine(Application.StartupPath, @"Media\New Feed Items Received.wav");
      break;
     case Resource.ApplicationSound.NewAttachmentDownloaded:
      soundFile = Path.Combine(Application.StartupPath, @"Media\New Attachment Downloaded.wav");
      break;
    }
    if (File.Exists(soundFile)) {
     PlaySound(soundFile, IntPtr.Zero, SoundFlags.SND_FILENAME |
      SoundFlags.SND_NOWAIT | SoundFlags.SND_ASYNC);
    }
   } catch (Exception ex) {
    _log.Error("Error playing sound '" + applicationSound + "'", ex);
   }
  }
  internal interface IRegistry {
   int InstanceActivatorPort {get ;set ;}
   string CurrentFeedProtocolHandler {get ;set ;}
   void CheckAndInitSounds(string appKey);
   bool RunAtStartup { get; set ;}
   bool IsInternetExplorerExtensionRegistered(IEMenuExtension extension) ;
   void RegisterInternetExplorerExtension(IEMenuExtension extension);
   void UnRegisterInternetExplorerExtension(IEMenuExtension extension) ;
   Version GetInternetExplorerVersion();
  }
  public enum IEMenuExtension {
   DefaultFeedAggregator,
   Bandit
  }
  private static IRegistry registryInstance;
  public static IRegistry Registry {
   get {
    if (registryInstance != null)
     return registryInstance;
    if (RssBanditApplication.PortableApplicationMode)
     registryInstance = new PortableRegistry();
    else
     registryInstance = new WindowsRegistry();
    return registryInstance;
   }
  }
  internal class WindowsRegistry : IRegistry
  {
   private static readonly log4net.ILog _log = Logger.Log.GetLogger(typeof(Registry));
            private readonly static string BanditSettings;
   private readonly static string BanditKey = "RssBandit";
            static WindowsRegistry()
            {
                BanditSettings = @"Software\RssBandit\Settings";
            }
   internal static Version GetInternetExplorerVersion() {
    RegistryKey key = Microsoft.Win32.Registry.LocalMachine.OpenSubKey(@"SOFTWARE\\Microsoft\\Internet Explorer", false);
    string s = null;
    if (key != null) {
     s = key.GetValue("Version") as string;
     key.Close();
    }
    if (s != null) {
     try {
      return new Version(s);
     }catch (ArgumentOutOfRangeException) {
     } catch (ArgumentException) {
     } catch (FormatException) {
     }
    }
    return new Version(3, 0);
   }
   int IRegistry.InstanceActivatorPort {
    get {
     try {
        int retval = 0;
        RegistryKey key = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(BanditSettings, false);
        string val = ((key == null) ? null : (key.GetValue("InstanceActivatorPort") as string));
        if (val != null && val.Trim().Length > 0) {
         try {
          int iConfPort = Int32.Parse(val);
          retval = iConfPort;
         } catch {}
        }
        if (key != null) key.Close();
        return retval;
       } catch (Exception ex) {
        Win32._log.Error("Cannot get InstanceActivatorPort", ex);
        return 0;
       }
    }
    set {
     try {
      int newPort = value;
      RegistryKey keySettings = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(BanditSettings, true);
      if (keySettings == null) {
       keySettings = Microsoft.Win32.Registry.CurrentUser.CreateSubKey(BanditSettings);
      }
      keySettings.SetValue("InstanceActivatorPort", newPort.ToString());
      keySettings.Close();
     } catch (Exception ex) {
      Win32._log.Error("Cannot set InstanceActivatorPort", ex);
     }
    }
   }
   string IRegistry.CurrentFeedProtocolHandler {
    get {
     try {
      RegistryKey key = ClassesRootKey().OpenSubKey(@"feed\shell\open\command", false);
      string val = ((key == null) ? null : (key.GetValue(null) as string));
      if (key != null) key.Close();
      return val;
     } catch (Exception ex) {
      Win32._log.Error("Cannot get CurrentFeedProtocolHandler", ex);
      return null;
     }
    }
    set {
     try {
      string appExePath = value;
      RegistryKey keyFeed = ClassesRootKey().OpenSubKey(@"feed", true);
      if (keyFeed == null) {
       keyFeed = ClassesRootKey(true).CreateSubKey(@"feed");
      }
      keyFeed.SetValue(null, "URL:feed protocol");
      keyFeed.SetValue("URL Protocol", "");
      RegistryKey keyIcon = keyFeed.OpenSubKey("DefaultIcon", true);
      if (keyIcon == null) {
       keyIcon = keyFeed.CreateSubKey("DefaultIcon");
      }
      keyIcon.SetValue(null, appExePath+",0");
      RegistryKey keyFeedSub = keyFeed.OpenSubKey(@"shell\open\command", true);
      if (keyFeedSub == null) {
       keyFeedSub = keyFeed.CreateSubKey(@"shell\open\command");
      }
      keyFeedSub.SetValue(null, String.Concat(appExePath, " ", "\"", "%1", "\""));
      if (keyFeed != null) keyFeed.Close();
      if (keyIcon != null) keyIcon.Close();
      if (keyFeedSub != null) keyFeedSub.Close();
     } catch (SecurityException sec) {
      Win32._log.Error("Cannot set application as CurrentFeedProtocolHandler", sec);
     }
    }
   }
   void IRegistry.CheckAndInitSounds(string appKey) {
    string rootKey = "AppEvents\\Schemes\\Apps";
    string rootLabels = "AppEvents\\EventLabels";
    string appName = "RSS Bandit";
    string defaultKeyName = ".default";
    string currentKeyName = ".current";
    RegistryKey labelRoot = null, schemeRoot = null;
    CultureInfo prev = RssBanditApplication.SharedUICulture;
    RssBanditApplication.SharedUICulture = CultureInfo.InstalledUICulture;
    try {
     schemeRoot = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(rootKey, true);
     if (schemeRoot == null)
      return;
     labelRoot = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(rootLabels, true);
     if (labelRoot == null)
      return;
     RegistryKey appSchemeRoot = null;
     RegistryKey sndSchemeDefinition = null;
     RegistryKey sndSchemeLabel = null;
     RegistryKey currentKey = null, defaultKey = null;
     if (null == (appSchemeRoot = schemeRoot.OpenSubKey(appKey, true))) {
      appSchemeRoot = schemeRoot.CreateSubKey(appKey);
      appSchemeRoot.SetValue(null, appName);
     }
     string currentSndKey = Resource.ApplicationSound.FeedDiscovered;
     if (null == (sndSchemeDefinition = appSchemeRoot.OpenSubKey(currentSndKey, true))) {
      sndSchemeDefinition = appSchemeRoot.CreateSubKey(currentSndKey);
     }
     if (null == (currentKey = sndSchemeDefinition.OpenSubKey(currentKeyName, true))) {
      currentKey = sndSchemeDefinition.CreateSubKey(currentKeyName);
      if (File.Exists(Environment.ExpandEnvironmentVariables(@"%WinDir%\media\Windows Feed Discovered.wav")))
       currentKey.SetValue(null, Environment.ExpandEnvironmentVariables(@"%WinDir%\media\Windows Feed Discovered.wav"));
      else
       currentKey.SetValue(null, Path.Combine(Application.StartupPath, @"Media\Feed Discovered.wav"));
      currentKey.Close();
     }
     if (null == (defaultKey = sndSchemeDefinition.OpenSubKey(defaultKeyName, true))) {
      defaultKey = sndSchemeDefinition.CreateSubKey(defaultKeyName);
      defaultKey.SetValue(null, Path.Combine(Application.StartupPath, @"Media\Feed Discovered.wav"));
      defaultKey.Close();
     }
     sndSchemeDefinition.Close();
     if (null == (sndSchemeLabel = labelRoot.OpenSubKey(currentSndKey, true))) {
      sndSchemeLabel = labelRoot.CreateSubKey(currentSndKey);
     }
     sndSchemeLabel.SetValue(null, SR.WindowsSoundControlPanelNewFeedDiscovered);
     sndSchemeLabel.Close();
     currentSndKey = Resource.ApplicationSound.NewItemsReceived;
     if (null == (sndSchemeDefinition = appSchemeRoot.OpenSubKey(currentSndKey, true))) {
      sndSchemeDefinition = appSchemeRoot.CreateSubKey(currentSndKey);
     }
     if (null == (currentKey = sndSchemeDefinition.OpenSubKey(currentKeyName, true))) {
      currentKey = sndSchemeDefinition.CreateSubKey(currentKeyName);
      currentKey.SetValue(null, Path.Combine(Application.StartupPath, @"Media\New Feed Items Received.wav"));
      currentKey.Close();
     }
     if (null == (defaultKey = sndSchemeDefinition.OpenSubKey(defaultKeyName, true))) {
      defaultKey = sndSchemeDefinition.CreateSubKey(defaultKeyName);
      defaultKey.SetValue(null, Path.Combine(Application.StartupPath, @"Media\New Feed Items Received.wav"));
      defaultKey.Close();
     }
     sndSchemeDefinition.Close();
     if (null == (sndSchemeLabel = labelRoot.OpenSubKey(currentSndKey, true))) {
      sndSchemeLabel = labelRoot.CreateSubKey(currentSndKey);
     }
     sndSchemeLabel.SetValue(null, SR.WindowsSoundControlPanelNewItemsReceived);
     sndSchemeLabel.Close();
     currentSndKey = Resource.ApplicationSound.NewAttachmentDownloaded;
     if (null == (sndSchemeDefinition = appSchemeRoot.OpenSubKey(currentSndKey, true))) {
      sndSchemeDefinition = appSchemeRoot.CreateSubKey(currentSndKey);
     }
     if (null == (currentKey = sndSchemeDefinition.OpenSubKey(currentKeyName, true))) {
      currentKey = sndSchemeDefinition.CreateSubKey(currentKeyName);
      currentKey.SetValue(null, Path.Combine(Application.StartupPath, @"Media\New Attachment Downloaded.wav"));
      currentKey.Close();
     }
     if (null == (defaultKey = sndSchemeDefinition.OpenSubKey(defaultKeyName, true))) {
      defaultKey = sndSchemeDefinition.CreateSubKey(defaultKeyName);
      defaultKey.SetValue(null, Path.Combine(Application.StartupPath, @"Media\New Attachment Downloaded.wav"));
      defaultKey.Close();
     }
     sndSchemeDefinition.Close();
     if (null == (sndSchemeLabel = labelRoot.OpenSubKey(currentSndKey, true))) {
      sndSchemeLabel = labelRoot.CreateSubKey(currentSndKey);
     }
     sndSchemeLabel.SetValue(null, SR.WindowsSoundControlPanelNewAttachmentDownloaded);
     sndSchemeLabel.Close();
     appSchemeRoot.Close();
    }
    finally {
     RssBanditApplication.SharedUICulture = prev;
     if (schemeRoot != null)
      schemeRoot.Close();
     if (labelRoot != null)
      labelRoot.Close();
    }
   }
   bool IRegistry.RunAtStartup {
    get {
     try {
      RegistryKey key = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(@"Software\\Microsoft\\Windows\\CurrentVersion\\Run", false);
      string s = null;
      if (key != null) {
       s = key.GetValue(BanditKey) as string;
       key.Close();
      }
      if (s != null) {
       string location = Assembly.GetEntryAssembly().Location.ToString(CultureInfo.CurrentUICulture);
       if (s == "\"" + location + "\" -t")
        return true;
      }
     } catch (SecurityException sec) {
      Win32._log.Error("Cannot set application to RunAtStartup", sec);
     }
     return false;
    }
    set {
     try {
      RegistryKey key = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(@"Software\\Microsoft\\Windows\\CurrentVersion\\Run", true);
      string location = Assembly.GetEntryAssembly().Location.ToString(CultureInfo.CurrentUICulture);
      if (key != null) {
       if (value) {
        key.SetValue(BanditKey, "\"" + location + "\" -t");
       }
       else {
        string text2 = key.GetValue(BanditKey) as string;
        if (text2 != null) {
         key.DeleteValue(BanditKey);
        }
       }
       key.Close();
      }
     } catch (SecurityException sec) {
      Win32._log.Error("Cannot set application to RunAtStartup", sec);
     }
    }
   }
   bool IRegistry.IsInternetExplorerExtensionRegistered(IEMenuExtension extension) {
    string scriptName = GetIEExtensionScriptName(extension);
    string keyName = FindIEExtensionKey(extension, scriptName);
    if (keyName != null) {
     if (!File.Exists(Path.Combine(RssBanditApplication.GetUserPath(), scriptName))) {
      try {
       Microsoft.Win32.Registry.CurrentUser.DeleteSubKey(String.Format(@"Software\Microsoft\Internet Explorer\MenuExt\{0}", keyName), false);
       keyName = null;
      } catch (Exception) {
       return true;
      }
     }
    }
    return (keyName != null);
   }
   void IRegistry.RegisterInternetExplorerExtension(IEMenuExtension extension) {
    WriteIEExtensionScript(extension);
    WriteIEExtensionRegistryEntry(extension);
   }
   void IRegistry.UnRegisterInternetExplorerExtension(IEMenuExtension extension) {
    DeleteIEExtensionRegistryEntry(extension);
    DeleteIEExtensionScript(extension);
   }
   Version IRegistry.GetInternetExplorerVersion() {
    return WindowsRegistry.GetInternetExplorerVersion();
   }
   private static string FindIEExtensionKey(IEMenuExtension extension) {
    return FindIEExtensionKey(extension, GetIEExtensionScriptName(extension));
   }
   private static string FindIEExtensionKey(IEMenuExtension extension, string scriptName) {
    if (scriptName == null)
     scriptName = GetIEExtensionScriptName(extension);
    try {
     RegistryKey menuBase = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(@"Software\Microsoft\Internet Explorer\MenuExt", false);
     foreach (string skey in menuBase.GetSubKeyNames()) {
      RegistryKey subMenu = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(String.Format(@"Software\Microsoft\Internet Explorer\MenuExt\{0}", skey), false);
      string defVal = ((subMenu == null) ? null : (subMenu.GetValue(null) as string));
      if (defVal != null && defVal.EndsWith(scriptName) && File.Exists(defVal)) {
       return skey;
      }
     }
     return null;
    } catch (Exception ex) {
     _log.Error("Registry:FindIEExtensionKey() cause exception", ex);
     return null;
    }
   }
   private static void WriteIEExtensionRegistryEntry(IEMenuExtension extension) {
    string scriptName = GetIEExtensionScriptName(extension);
    string caption = null;
    if (extension == IEMenuExtension.DefaultFeedAggregator) {
     caption = SR.InternetExplorerMenuExtDefaultCaption;
    } else {
     caption = SR.InternetExplorerMenuExtBanditCaption;
    }
    try {
     RegistryKey menuBase = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(@"Software\Microsoft\Internet Explorer\MenuExt", true);
     RegistryKey subMenu = menuBase.OpenSubKey(caption, true);
     if (subMenu == null) {
      subMenu = menuBase.CreateSubKey(caption);
     }
     subMenu.SetValue(null, Path.Combine(RssBanditApplication.GetUserPath(), scriptName));
     subMenu.SetValue("contexts", InternetExplorerExtensionsContexts);
     if (menuBase != null) menuBase.Close();
     if (subMenu != null) subMenu.Close();
    } catch (Exception ex) {
     _log.Error("Registry:WriteIEExtensionRegistryEntry() cause exception", ex);
    }
   }
   private static void WriteIEExtensionScript(IEMenuExtension extension) {
    string scriptName = GetIEExtensionScriptName(extension);
    try {
     string scriptContent = null;
     using (Stream resStream = Resource.GetStream("Resources."+scriptName)) {
      StreamReader reader = new StreamReader(resStream);
      scriptContent = reader.ReadToEnd();
     }
     if (scriptContent != null) {
      if (extension == IEMenuExtension.Bandit) {
       scriptContent = scriptContent.Replace("__COMMAND_PATH_PLACEHOLDER__", Application.ExecutablePath.Replace(@"\", @"\\"));
      }
      using (Stream outStream = NewsComponents.Utils.FileHelper.OpenForWrite(Path.Combine(RssBanditApplication.GetUserPath(), scriptName))) {
       StreamWriter writer = new StreamWriter(outStream);
       writer.Write(scriptContent);
       writer.Flush();
      }
     }
    } catch (Exception ex) {
     _log.Error("Registry:WriteIEExtensionScript("+scriptName+") cause exception",ex);
     throw;
    }
   }
   private static void DeleteIEExtensionRegistryEntry(IEMenuExtension extension) {
    string keyName = FindIEExtensionKey(extension);
    if (keyName != null) {
     Microsoft.Win32.Registry.CurrentUser.DeleteSubKey(String.Format(@"Software\Microsoft\Internet Explorer\MenuExt\{0}", keyName), false);
    }
   }
   private static void DeleteIEExtensionScript(IEMenuExtension extension) {
    string scriptName = GetIEExtensionScriptName(extension);
    string scriptPath = Path.Combine(RssBanditApplication.GetUserPath(), scriptName);
    if (File.Exists(scriptPath)) {
     NewsComponents.Utils.FileHelper.Delete(scriptPath);
    }
   }
   private static string GetIEExtensionScriptName(IEMenuExtension extension) {
    return (extension == IEMenuExtension.DefaultFeedAggregator ? InternetExplorerExtensionsExecFeedScript : InternetExplorerExtensionsExecBanditScript);
   }
   private const string InternetExplorerExtensionsExecFeedScript = "iecontext_subscribefeed.htm";
   private const string InternetExplorerExtensionsExecBanditScript = "iecontext_subscribebandit.htm";
   private const int InternetExplorerExtensionsContexts = 0x22;
   private static RegistryKey ClassesRootKey() {
    return ClassesRootKey(false);
   }
   internal static RegistryKey ClassesRootKey(bool writable) {
    if (IsOSAtLeastWindows2000)
     return Microsoft.Win32.Registry.CurrentUser.OpenSubKey(@"Software\Classes", writable);
    else
     return Microsoft.Win32.Registry.ClassesRoot;
   }
  }
  private class PortableRegistry : IRegistry
  {
   private int instanceActivatorPort = 0;
   int IRegistry.InstanceActivatorPort {
    get {
     if (instanceActivatorPort != 0)
      return instanceActivatorPort;
     string portFile = Path.Combine(RssBanditApplication.GetUserPath(), ".port");
     int retval = 0;
     try {
      if (!File.Exists(portFile))
       return retval;
      using (Stream s = FileHelper.OpenForRead(portFile)) {
       TextReader reader = new StreamReader(s);
       string content = reader.ReadToEnd();
       if (StringHelper.EmptyTrimOrNull(content))
        return retval;
       retval = Int16.Parse(content);
      }
      instanceActivatorPort = retval;
      return instanceActivatorPort;
     } catch (Exception ex) {
      Win32._log.Error("Cannot get InstanceActivatorPort from_ .port file", ex);
      return 0;
     }
    }
    set {
     if (instanceActivatorPort != value) {
      string portFilePath = RssBanditApplication.GetUserPath();
      try {
       if (!Directory.Exists(portFilePath))
        Directory.CreateDirectory(portFilePath);
       using (Stream s = FileHelper.OpenForWrite(Path.Combine(portFilePath, ".port"))) {
        TextWriter w = new StreamWriter(s);
        w.Write(value);
        w.Flush();
       }
       instanceActivatorPort = value;
      } catch (Exception ex) {
       Win32._log.Error("Cannot set InstanceActivatorPort in .port file", ex);
      }
     }
    }
   }
   string IRegistry.CurrentFeedProtocolHandler {
    get {
     return String.Concat(Application.ExecutablePath, " ", "\"", "%1", "\"");
    }
    set {
    }
   }
   void IRegistry.CheckAndInitSounds(string appKey) {
    return;
   }
   bool IRegistry.RunAtStartup {
    get { return false; }
    set { }
   }
   bool IRegistry.IsInternetExplorerExtensionRegistered(IEMenuExtension extension) {
    return true;
   }
   void IRegistry.RegisterInternetExplorerExtension(IEMenuExtension extension) {
    return;
   }
   void IRegistry.UnRegisterInternetExplorerExtension(IEMenuExtension extension) {
    return;
   }
   Version IRegistry.GetInternetExplorerVersion() {
    return WindowsRegistry.GetInternetExplorerVersion();
   }
  }
  private Win32() {}
  [EnvironmentPermission(SecurityAction.Assert, Unrestricted=true)]
  static Win32()
  {
   _os = Environment.OSVersion;
   if (_os.Platform == PlatformID.Win32Windows) {
    Win32.IsWin9x = true;
   } else {
    try {
     Win32.IsAspNetServer = Thread.GetDomain().GetData(".appDomain") != null;
    }
    catch { }
    Win32.IsWinNt = true;
    int spMajor, spMinor;
    Win32.GetWindowsServicePackInfo(out spMajor, out spMinor);
    if ((_os.Version.Major == 5) && (_os.Version.Minor == 0)) {
     Win32.IsWin2K = true;
     Win32.IsWinHttp51 = (spMajor >= 3);
    } else {
     Win32.IsPostWin2K = true;
     if ((_os.Version.Major == 5) && (_os.Version.Minor == 1)) {
      Win32.IsWinHttp51 = (spMajor >= 1);
     }
     else {
      Win32.IsWinHttp51 = true;
      Win32.IsWin2k3 = true;
     }
    }
   }
   IEVersion = Registry.GetInternetExplorerVersion();
  }
  private static readonly log4net.ILog _log = Logger.Log.GetLogger(typeof(Win32));
  private static int _paintFrozen = 0;
  private static OperatingSystem _os;
  internal static readonly bool IsAspNetServer;
  internal static readonly bool IsPostWin2K;
  internal static readonly bool IsWin2K;
  internal static readonly bool IsWin2k3;
  internal static readonly bool IsWin9x;
  internal static readonly bool IsWinHttp51;
  internal static readonly bool IsWinNt;
  internal static readonly Version IEVersion;
  public static bool IsOSAtLeastWindows2000 {
   get {
    return (IsWinNt && _os.Version.Major >= 5 );
   }
  }
  public static bool IsOSAtLeastWindowsXP {
   get {
    return (IsWinNt &&
            (_os.Version.Major > 5 ||
             (_os.Version.Major == 5 &&
              _os.Version.Minor >= 1)));
   }
  }
  public static bool IsOSWindowsXP {
   get {
    return (IsWinNt &&
            (_os.Version.Major == 5 &&
             _os.Version.Minor == 1));
   }
  }
  public static bool IsOSAtLeastWindowsXPSP2 {
   get {
    if (IsOSWindowsXP) {
     int spMajor, spMinor;
     GetWindowsServicePackInfo(out spMajor, out spMinor);
     return spMajor <= 2;
    } else {
     return IsOSAtLeastWindowsXP;
    }
   }
  }
  public static bool IsOSWindowsVista {
   get {
    return (IsWinNt &&
            (_os.Version.Major == 6));
   }
  }
  public static bool IsOSAtLeastWindowsVista {
   get {
    return (IsWinNt &&
            (_os.Version.Major >= 6));
   }
  }
  public static bool IsIE6 {
   get {
    return (IEVersion.Major >= 6);
   }
  }
  public static bool IsIE6SP2 {
   get {
    return (IEVersion.Major > 6 ||
            (IEVersion.Major == 6 && IEVersion.Minor == 0 && IEVersion.Build >= 2900) ||
            (IEVersion.Major == 6 && IEVersion.Minor > 0 ));
   }
  }
  public static void GetWindowsServicePackInfo(out int servicePackMajor, out int servicePackMinor) {
   OSVERSIONINFOEX ifex = new OSVERSIONINFOEX();
   ifex.dwOSVersionInfoSize = Marshal.SizeOf(ifex);
   if (!GetVersionEx(ref ifex)) {
    int err = Marshal.GetLastWin32Error();
    throw new Exception("Requesting Windows Service Pack Information caused an windows error (Code: " + err.ToString() +").");
   }
   servicePackMajor = ifex.wServicePackMajor;
   servicePackMinor = ifex.wServicePackMinor;
  }
  public static System.Windows.Forms.FormWindowState GetStartupWindowState() {
   API_STARTUPINFO sti = new API_STARTUPINFO();
   try {
    sti.cb = Marshal.SizeOf(typeof(API_STARTUPINFO));
    GetStartupInfo(ref sti);
    if (sti.wShowWindow == (short)ShowWindowStyles.SW_MINIMIZE || sti.wShowWindow == (short)ShowWindowStyles.SW_SHOWMINIMIZED ||
        sti.wShowWindow == (short)ShowWindowStyles.SW_SHOWMINNOACTIVE || sti.wShowWindow == (short)ShowWindowStyles.SW_FORCEMINIMIZE)
     return System.Windows.Forms.FormWindowState.Minimized;
    else if (sti.wShowWindow == (short)ShowWindowStyles.SW_MAXIMIZE || sti.wShowWindow == (short)ShowWindowStyles.SW_SHOWMAXIMIZED ||
             sti.wShowWindow == (short)ShowWindowStyles.SW_MAX)
     return System.Windows.Forms.FormWindowState.Maximized;
   } catch (Exception e) {
    _log.Error("GetStartupWindowState() caused exception", e);
   }
   return System.Windows.Forms.FormWindowState.Normal;
  }
  public static void ModifyWindowStyle(IntPtr hWnd, int styleToRemove, int styleToAdd) {
   int style = GetWindowLong(hWnd, GWL_STYLE);
   style &= ~styleToRemove;
   style |= styleToAdd;
   SetWindowLong(hWnd, GWL_STYLE, style);
  }
  public static void FreezePainting (Control ctrl, bool freeze) {
   if (freeze && ctrl != null && ctrl.IsHandleCreated && ctrl.Visible) {
    if (0 == _paintFrozen++) {
     SendMessage(ctrl.Handle, (int) Message.WM_SETREDRAW, 0, IntPtr.Zero);
    }
   }
   if (!freeze) {
    if (_paintFrozen == 0) {
     return;
    }
    if (0 == --_paintFrozen && ctrl != null) {
     SendMessage(ctrl.Handle, (int)Message.WM_SETREDRAW, 1, IntPtr.Zero);
     ctrl.Invalidate(true);
    }
   }
  }
  public static int HIWORD(IntPtr x) {
   return (unchecked((int)(long)x) >> 16) & 0xffff;
  }
  public static int LOWORD(IntPtr x) {
   return unchecked((int)(long)x) & 0xffff;
  }
 }
 internal sealed class UxTheme {
  private UxTheme() {
  }
  public static bool AppThemed {
   get {
    bool themed = false;
    OperatingSystem os = System.Environment.OSVersion;
    if (os.Platform == PlatformID.Win32NT && ((os.Version.Major == 5 && os.Version.Minor >= 1) || os.Version.Major > 5)) {
     themed = IsAppThemed();
    }
    return themed;
   }
  }
  public static String ThemeName {
   get {
    StringBuilder themeName = new StringBuilder(256);
    GetCurrentThemeName(themeName, 256, null, 0, null, 0);
    return themeName.ToString();
   }
  }
  public static String ColorName {
   get {
    StringBuilder themeName = new StringBuilder(256);
    StringBuilder colorName = new StringBuilder(256);
    GetCurrentThemeName(themeName, 256, colorName, 256, null, 0);
    return colorName.ToString();
   }
  }
  [DllImport("UxTheme.dll")]
  public static extern IntPtr OpenThemeData(IntPtr hwnd, [MarshalAs(UnmanagedType.LPTStr)] string pszClassList);
  [DllImport("UxTheme.dll")]
  public static extern int CloseThemeData(IntPtr hTheme);
  [DllImport("UxTheme.dll")]
  public static extern int DrawThemeBackground(IntPtr hTheme, IntPtr hdc, int iPartId, int iStateId, ref Win32.RECT pRect, ref Win32.RECT pClipRect);
  [DllImport("UxTheme.dll")]
  public static extern bool IsThemeActive();
  [DllImport("UxTheme.dll")]
  public static extern bool IsAppThemed();
  [DllImport("UxTheme.dll", ExactSpelling=true, CharSet=CharSet.Unicode)]
  internal static extern int GetCurrentThemeName(System.Text.StringBuilder pszThemeFileName, int dwMaxNameChars, System.Text.StringBuilder pszColorBuff, int cchMaxColorChars, System.Text.StringBuilder pszSizeBuff, int cchMaxSizeChars);
  [DllImport("UxTheme.dll")]
  public static extern int DrawThemeParentBackground(IntPtr hwnd, IntPtr hdc, ref Win32.RECT prc);
  public class WindowClasses {
   public static readonly string Edit = "EDIT";
   public static readonly string ListView = "LISTVIEW";
   public static readonly string TreeView = "TREEVIEW";
  }
  public class Parts {
   public enum Edit {
    EditText = 1
   }
   public enum ListView {
    ListItem = 1
   }
   public enum TreeView {
    TreeItem = 1
   }
  }
  public class PartStates {
   public enum EditText {
    Normal = 1,
    Hot = 2,
    Selected = 3,
    Disabled = 4,
    Focused = 5,
    Readonly = 6
   }
   public enum ListItem {
    Normal = 1,
    Hot = 2,
    Selected = 3,
    Disabled = 4,
    SelectedNotFocused = 5
   }
   public enum TreeItem {
    Normal = 1,
    Hot = 2,
    Selected = 3,
    Disabled = 4,
    SelectedNotFocused = 5
   }
  }
 }
}
