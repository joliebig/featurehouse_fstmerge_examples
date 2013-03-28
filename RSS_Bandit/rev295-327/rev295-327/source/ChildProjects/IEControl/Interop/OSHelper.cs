using System; 
using System.Runtime.InteropServices; 
using System.Security; 
using System.Security.Permissions; 
using System.Threading; 
using Microsoft.Win32; namespace  IEControl {
	
 [Flags] 
 internal enum  WindowsSuite  {
  VER_SERVER_NT = unchecked((int)0x80000000),
  VER_WORKSTATION_NT = 0x40000000,
  VER_SUITE_SMALLBUSINESS = 0x00000001,
  VER_SUITE_ENTERPRISE = 0x00000002,
  VER_SUITE_BACKOFFICE = 0x00000004,
  VER_SUITE_COMMUNICATIONS = 0x00000008,
  VER_SUITE_TERMINAL = 0x00000010,
  VER_SUITE_SMALLBUSINESS_RESTRICTED = 0x00000020,
  VER_SUITE_EMBEDDEDNT = 0x00000040,
  VER_SUITE_DATACENTER = 0x00000080,
  VER_SUITE_SINGLEUSERTS = 0x00000100,
  VER_SUITE_PERSONAL = 0x00000200,
  VER_SUITE_BLADE = 0x00000400,
  VER_SUITE_EMBEDDED_RESTRICTED = 0x00000800,
 } 
 [SuppressUnmanagedCodeSecurity()] 
 internal sealed class  OSHelper {
		
  public static readonly  bool IsAspNetServer;
 
  public static readonly  bool IsPostWin2K;
 
  public static readonly  bool IsWin2K;
 
  public static readonly  bool IsWin2k3;
 
  public static readonly  bool IsWin9x;
 
  public static readonly  bool IsWinHttp51;
 
  public static readonly  bool IsWinNt;
 
  public static readonly  Version IEVersion;
 
  private static  OperatingSystem _os;
 
  public static  bool IsOSAtLeastWindows2000 {
   get {
    return (IsWinNt && _os.Version.Major >= 5 );
   }
  }
 
  public static  bool IsOSAtLeastWindowsXP {
   get {
    return (IsWinNt &&
     (_os.Version.Major > 5 ||
     (_os.Version.Major == 5 &&
     _os.Version.Minor >= 1)));
   }
  }
 
  public static  bool IsOSAtLeastWindowsXPSP2 {
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
 
  public static  bool IsOSWindowsXP {
   get {
    return (IsWinNt &&
     (_os.Version.Major == 5 &&
     _os.Version.Minor == 1));
   }
  }
 
  public static  bool IsOSWindowsVista {
   get {
    return (IsWinNt &&
     (_os.Version.Major == 6));
   }
  }
 
  public static  bool IsOSAtLeastWindowsVista {
   get {
    return (IsWinNt &&
     (_os.Version.Major >= 6));
   }
  }
 
  public static  bool IsIE6 {
   get {
    return (IEVersion.Major >= 6);
   }
  }
 
  public static  bool IsIE6SP2 {
   get {
    return (IEVersion.Major > 6 ||
     (IEVersion.Major == 6 && IEVersion.Minor == 0 && IEVersion.Build >= 2900) ||
     (IEVersion.Major == 6 && IEVersion.Minor > 0 ));
   }
  }
 
  public static  void GetWindowsServicePackInfo(out int servicePackMajor, out int servicePackMinor) {
   OSVERSIONINFOEX ifex = new OSVERSIONINFOEX();
   ifex.dwOSVersionInfoSize = Marshal.SizeOf(ifex);
   if (!GetVersionEx(ref ifex)) {
    int err = Marshal.GetLastWin32Error();
    throw new Exception("GetVersionEx() failed with error ("+ err.ToString() +").");
   }
   servicePackMajor = ifex.wServicePackMajor;
   servicePackMinor = ifex.wServicePackMinor;
  }
 
  public static  WindowsSuite GetWindowsSuiteInfo() {
   OSVERSIONINFOEX ifex = new OSVERSIONINFOEX();
   ifex.dwOSVersionInfoSize = Marshal.SizeOf(ifex);
   if (!GetVersionEx(ref ifex)) {
    int err = Marshal.GetLastWin32Error();
    throw new Exception("GetVersionEx() failed with error ("+ err.ToString() +").");
   }
   return (WindowsSuite) ifex.wSuiteMask;
  }
 
  public static  Version GetInternetExplorerVersion() {
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
 
  private  OSHelper(){ }
 
  [EnvironmentPermission(SecurityAction.Assert, Unrestricted=true)] 
  static  OSHelper() {
   _os = Environment.OSVersion;
   if (_os.Platform == PlatformID.Win32Windows) {
    IsWin9x = true;
   } else {
    try {
     IsAspNetServer = Thread.GetDomain().GetData(".appDomain") != null;
    }
    catch { }
    IsWinNt = true;
    int spMajor, spMinor;
    GetWindowsServicePackInfo(out spMajor, out spMinor);
    if ((_os.Version.Major == 5) && (_os.Version.Minor == 0)) {
     IsWin2K = true;
     IsWinHttp51 = (spMajor >= 3);
    } else {
     IsPostWin2K = true;
     if ((_os.Version.Major == 5) && (_os.Version.Minor == 1)) {
      IsWinHttp51 = (spMajor >= 1);
     }
     else {
      IsWinHttp51 = true;
      IsWin2k3 = true;
     }
    }
   }
   IEVersion = GetInternetExplorerVersion();
  }
 
  [ DllImport( "kernel32", SetLastError=true )] 
  private static extern  bool GetVersionEx(ref OSVERSIONINFOEX osvi );
 
  [StructLayout(LayoutKind.Sequential)] 
  private struct  OSVERSIONINFOEX {
			
   public  int dwOSVersionInfoSize;
 
   public  int dwMajorVersion;
 
   public  int dwMinorVersion;
 
   public  int dwBuildNumber;
 
   public  int dwPlatformId;
 
   [MarshalAs(UnmanagedType.ByValTStr, SizeConst=128)] 
   public  string szCSDVersion;
 
   public  UInt16 wServicePackMajor;
 
   public  UInt16 wServicePackMinor;
 
   public  UInt16 wSuiteMask;
 
   public  byte wProductType;
 
   public  byte wReserved;

		}

	}

}
