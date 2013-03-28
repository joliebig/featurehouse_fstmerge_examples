using System;
using System.Runtime.InteropServices;
namespace ShellLib
{
 public class ShellBrowseForFolderDialog
 {
  [DllImport("User32.dll")]
  public static extern Int32 SendMessage(
   IntPtr hWnd,
   UInt32 Msg,
   UInt32 wParam,
   Int32 lParam
   );
  [DllImport("User32.dll")]
  public static extern Int32 SendMessage(
   IntPtr hWnd,
   UInt32 Msg,
   UInt32 wParam,
   [MarshalAs(UnmanagedType.LPWStr)]
   String lParam
   );
  public enum RootTypeOptions
  {
   BySpecialFolder,
   ByPath
  }
  [Flags]
  public enum BrowseInfoFlag
  {
   BIF_RETURNONLYFSDIRS = 0x0001,
   BIF_DONTGOBELOWDOMAIN = 0x0002,
   BIF_STATUSTEXT = 0x0004,
   BIF_RETURNFSANCESTORS = 0x0008,
   BIF_EDITBOX = 0x0010,
   BIF_VALIDATE = 0x0020,
   BIF_NEWDIALOGSTYLE = 0x0040,
   BIF_USENEWUI = (BIF_NEWDIALOGSTYLE | BIF_EDITBOX),
   BIF_BROWSEINCLUDEURLS = 0x0080,
   BIF_UAHINT = 0x0100,
   BIF_NONEWFOLDERBUTTON = 0x0200,
   BIF_NOTRANSLATETARGETS = 0x0400,
   BIF_BROWSEFORCOMPUTER = 0x1000,
   BIF_BROWSEFORPRINTER = 0x2000,
   BIF_BROWSEINCLUDEFILES = 0x4000,
   BIF_SHAREABLE = 0x8000
  }
  public enum BrowseForFolderMessages
  {
   BFFM_INITIALIZED = 1,
   BFFM_SELCHANGED = 2,
   BFFM_VALIDATEFAILEDA = 3,
   BFFM_VALIDATEFAILEDW = 4,
   BFFM_IUNKNOWN = 5,
   BFFM_SETSTATUSTEXTA = (0x0400 + 100),
   BFFM_ENABLEOK = (0x0400 + 101),
   BFFM_SETSELECTIONA = (0x0400 + 102),
   BFFM_SETSELECTIONW = (0x0400 + 103),
   BFFM_SETSTATUSTEXTW = (0x0400 + 104),
   BFFM_SETOKTEXT = (0x0400 + 105),
   BFFM_SETEXPANDED = (0x0400 + 106)
  }
  public class InitializedEventArgs : EventArgs
  {
   public InitializedEventArgs(IntPtr hwnd)
   {
    this.hwnd = hwnd;
   }
   public readonly IntPtr hwnd;
  }
  public class IUnknownEventArgs : EventArgs
  {
   public IUnknownEventArgs(IntPtr hwnd, IntPtr iunknown)
   {
    this.hwnd = hwnd;
    this.iunknown = iunknown;
   }
   public readonly IntPtr hwnd;
   public readonly IntPtr iunknown;
  }
  public class SelChangedEventArgs : EventArgs
  {
   public SelChangedEventArgs(IntPtr hwnd, IntPtr pidl)
   {
    this.hwnd = hwnd;
    this.pidl = pidl;
   }
   public readonly IntPtr hwnd;
   public readonly IntPtr pidl;
  }
  public class ValidateFailedEventArgs : EventArgs
  {
   public ValidateFailedEventArgs(IntPtr hwnd, string invalidSel)
   {
    this.hwnd = hwnd;
    this.invalidSel = invalidSel;
   }
   public readonly IntPtr hwnd;
   public readonly string invalidSel;
  }
  public delegate void InitializedHandler(ShellBrowseForFolderDialog sender, InitializedEventArgs args);
  public delegate void IUnknownHandler(ShellBrowseForFolderDialog sender, IUnknownEventArgs args);
  public delegate void SelChangedHandler(ShellBrowseForFolderDialog sender, SelChangedEventArgs args);
  public delegate int ValidateFailedHandler(ShellBrowseForFolderDialog sender, ValidateFailedEventArgs args);
  public event InitializedHandler OnInitialized;
  public event IUnknownHandler OnIUnknown;
  public event SelChangedHandler OnSelChanged;
  public event ValidateFailedHandler OnValidateFailed;
  public void EnableOk(IntPtr hwnd, bool Enabled)
  {
   SendMessage(hwnd, (uint)BrowseForFolderMessages.BFFM_ENABLEOK, 0, Enabled ? 1 : 0);
  }
  public void SetExpanded(IntPtr hwnd, string path)
  {
   SendMessage(hwnd, (uint)BrowseForFolderMessages.BFFM_SETEXPANDED, 1, path);
  }
  public void SetOkText(IntPtr hwnd, string text)
  {
   SendMessage(hwnd, (uint)BrowseForFolderMessages.BFFM_SETOKTEXT, 0, text);
  }
  public void SetSelection(IntPtr hwnd, string path)
  {
   SendMessage(hwnd, (uint)BrowseForFolderMessages.BFFM_SETSELECTIONW, 1, path);
  }
  public void SetStatusText(IntPtr hwnd, string text)
  {
   SendMessage(hwnd, (uint)BrowseForFolderMessages.BFFM_SETSTATUSTEXTW, 1, text);
  }
  private Int32 myBrowseCallbackProc(IntPtr hwnd, UInt32 uMsg, Int32 lParam, Int32 lpData)
  {
   switch ((BrowseForFolderMessages)uMsg)
   {
    case BrowseForFolderMessages.BFFM_INITIALIZED:
     System.Diagnostics.Debug.WriteLine("BFFM_INITIALIZED");
     if (OnInitialized != null)
     {
      InitializedEventArgs args = new InitializedEventArgs(hwnd);
      OnInitialized(this,args);
     }
     break;
    case BrowseForFolderMessages.BFFM_IUNKNOWN:
     System.Diagnostics.Debug.WriteLine("BFFM_IUNKNOWN");
     if (OnIUnknown != null)
     {
      IUnknownEventArgs args = new IUnknownEventArgs(hwnd,(IntPtr)lParam);
      OnIUnknown(this,args);
     }
     break;
    case BrowseForFolderMessages.BFFM_SELCHANGED:
     System.Diagnostics.Debug.WriteLine("BFFM_SELCHANGED");
     if (OnSelChanged != null)
     {
      SelChangedEventArgs args = new SelChangedEventArgs(hwnd,(IntPtr)lParam);
      OnSelChanged(this,args);
     }
     break;
    case BrowseForFolderMessages.BFFM_VALIDATEFAILEDA:
     System.Diagnostics.Debug.WriteLine("BFFM_VALIDATEFAILEDA");
     if (OnValidateFailed != null)
     {
      string failedSel = Marshal.PtrToStringAnsi((IntPtr)lParam);
      ValidateFailedEventArgs args = new ValidateFailedEventArgs(hwnd,failedSel);
      return OnValidateFailed(this,args);
     }
     break;
    case BrowseForFolderMessages.BFFM_VALIDATEFAILEDW:
     System.Diagnostics.Debug.WriteLine("BFFM_VALIDATEFAILEDW");
     if (OnValidateFailed != null)
     {
      string failedSel = Marshal.PtrToStringUni((IntPtr)lParam);
      ValidateFailedEventArgs args = new ValidateFailedEventArgs(hwnd,failedSel);
      return OnValidateFailed(this,args);
     }
     break;
   }
   return 0;
  }
  public ShellBrowseForFolderDialog()
  {
   hwndOwner = IntPtr.Zero;
   RootType = RootTypeOptions.BySpecialFolder;
   RootSpecialFolder = ShellApi.CSIDL.CSIDL_DESKTOP;
   RootPath = "";
   m_DisplayName = "";
   Title = "";
   UserToken = IntPtr.Zero;
   m_FullName = "";
   DetailsFlags = BrowseInfoFlag.BIF_BROWSEINCLUDEFILES
    | BrowseInfoFlag.BIF_EDITBOX
    | BrowseInfoFlag.BIF_NEWDIALOGSTYLE
    | BrowseInfoFlag.BIF_SHAREABLE
    | BrowseInfoFlag.BIF_STATUSTEXT
    | BrowseInfoFlag.BIF_USENEWUI
    | BrowseInfoFlag.BIF_VALIDATE;
  }
  public void ShowDialog()
  {
   m_FullName = "";
   m_DisplayName = "";
   IMalloc pMalloc;
   pMalloc = ShellFunctions.GetMalloc();
   IntPtr pidlRoot;
   if (RootType == RootTypeOptions.BySpecialFolder)
   {
    ShellApi.SHGetFolderLocation(hwndOwner,(int)RootSpecialFolder,UserToken,0,out pidlRoot);
   }
   else
   {
    uint iAttribute;
    ShellApi.SHParseDisplayName(RootPath,IntPtr.Zero,out pidlRoot,0,out iAttribute);
   }
   ShellApi.BROWSEINFO bi = new ShellApi.BROWSEINFO();
   bi.hwndOwner = hwndOwner;
   bi.pidlRoot = pidlRoot;
   bi.pszDisplayName = new String(' ',256);
   bi.lpszTitle = Title;
   bi.ulFlags = (uint)DetailsFlags;
   bi.lParam = 0;
   bi.lpfn = new ShellApi.BrowseCallbackProc(this.myBrowseCallbackProc);
   IntPtr pidlSelected;
   pidlSelected = ShellLib.ShellApi.SHBrowseForFolder(ref bi);
   m_DisplayName = bi.pszDisplayName.ToString();
   IShellFolder isf = ShellFunctions.GetDesktopFolder();
   ShellApi.STRRET ptrDisplayName;
   isf.GetDisplayNameOf(pidlSelected,(uint)ShellApi.SHGNO.SHGDN_NORMAL | (uint)ShellApi.SHGNO.SHGDN_FORPARSING,out ptrDisplayName);
   String sDisplay;
   ShellLib.ShellApi.StrRetToBSTR(ref ptrDisplayName,pidlRoot,out sDisplay);
   m_FullName = sDisplay;
   if (pidlRoot != IntPtr.Zero)
    pMalloc.Free(pidlRoot);
   if (pidlSelected != IntPtr.Zero)
    pMalloc.Free(pidlSelected);
   Marshal.ReleaseComObject(isf);
   Marshal.ReleaseComObject(pMalloc);
  }
  public IntPtr hwndOwner;
  public RootTypeOptions RootType;
  public string RootPath;
  public ShellApi.CSIDL RootSpecialFolder;
  public string DisplayName
  {
   get
   {
    return m_DisplayName;
   }
  }
  private string m_DisplayName;
  public string Title;
  public IntPtr UserToken;
  public string FullName
  {
   get
   {
    return m_FullName;
   }
  }
  private string m_FullName;
  public BrowseInfoFlag DetailsFlags;
 }
}
