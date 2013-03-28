using System;
using System.Globalization;
using System.Runtime.InteropServices;
using System.Net;
using Utility;
namespace WorldWind.Net
{
 public sealed class ProxyHelper
 {
  const int WINHTTP_ACCESS_TYPE_DEFAULT_PROXY = 0;
  const int WINHTTP_ACCESS_TYPE_NO_PROXY = 1;
  const int WINHTTP_ACCESS_TYPE_NAMED_PROXY = 3;
  const int WINHTTP_AUTOPROXY_AUTO_DETECT = 0x00000001;
  const int WINHTTP_AUTOPROXY_CONFIG_URL = 0x00000002;
  const int WINHTTP_AUTOPROXY_RUN_INPROCESS = 0x00010000;
  const int WINHTTP_AUTOPROXY_RUN_OUTPROCESS_ONLY = 0x00020000;
  const int WINHTTP_AUTO_DETECT_TYPE_DHCP = 0x00000001;
  const int WINHTTP_AUTO_DETECT_TYPE_DNS_A = 0x00000002;
  [StructLayout(LayoutKind.Sequential, CharSet=CharSet.Unicode)]
   struct WINHTTP_AUTOPROXY_OPTIONS
  {
   [MarshalAs(UnmanagedType.U4)]
   public int dwFlags;
   [MarshalAs(UnmanagedType.U4)]
   public int dwAutoDetectFlags;
   public string lpszAutoConfigUrl;
   public IntPtr lpvReserved;
   [MarshalAs(UnmanagedType.U4)]
   public int dwReserved;
   public bool fAutoLoginIfChallenged;
  }
  [StructLayout(LayoutKind.Sequential, CharSet=CharSet.Unicode)]
   struct WINHTTP_PROXY_INFO
  {
   [MarshalAs(UnmanagedType.U4)]
   public int dwAccessType;
   public IntPtr pwszProxy;
   public IntPtr pwszProxyBypass;
  }
  [DllImport("winhttp.dll", SetLastError=true, CharSet=CharSet.Unicode)]
  static extern IntPtr WinHttpOpen(
   string pwszUserAgent,
   int dwAccessType,
   IntPtr pwszProxyName,
   IntPtr pwszProxyBypass,
   int dwFlags
   );
  [DllImport("winhttp.dll", SetLastError=true, CharSet=CharSet.Unicode)]
  static extern bool WinHttpCloseHandle(IntPtr hInternet);
  [DllImport("winhttp.dll", SetLastError=true, CharSet=CharSet.Unicode)]
  static extern bool WinHttpGetProxyForUrl(
   IntPtr hSession,
   string lpcwszUrl,
   ref WINHTTP_AUTOPROXY_OPTIONS pAutoProxyOptions,
   ref WINHTTP_PROXY_INFO pProxyInfo
   );
  private ProxyHelper()
  {
  }
  static bool IsEmpty(string s)
  {
   return (s == null || s.Length == 0);
  }
  static IntPtr hSession = IntPtr.Zero;
  static void OpenWinHttpSession()
  {
   hSession = WinHttpOpen("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)",
    WINHTTP_ACCESS_TYPE_DEFAULT_PROXY,
    IntPtr.Zero,
    IntPtr.Zero,
    0);
  }
  static void CloseWinHttpSession()
  {
   if(hSession != IntPtr.Zero)
   {
    WinHttpCloseHandle(hSession);
    hSession = IntPtr.Zero;
   }
  }
  static ICredentials DetermineCredentials(string name, string password, string domain)
  {
   ICredentials theCreds = null;
   if(!IsEmpty(name))
   {
    theCreds = (domain == null) ?
     new NetworkCredential(name, password) :
     new NetworkCredential(name, password, domain);
   }
   return theCreds;
  }
  static IWebProxy DetermineAutoProxyForUrl(string targetUrl, string proxyScriptUrl, ref int errorCode)
  {
   if(hSession == IntPtr.Zero)
   {
    OpenWinHttpSession();
   }
   WINHTTP_AUTOPROXY_OPTIONS autoProxyOptions = new WINHTTP_AUTOPROXY_OPTIONS();
   WINHTTP_PROXY_INFO proxyInfo = new WINHTTP_PROXY_INFO();
   proxyInfo.pwszProxy = proxyInfo.pwszProxyBypass = IntPtr.Zero;
   if(!IsEmpty(proxyScriptUrl))
   {
    autoProxyOptions.dwFlags = WINHTTP_AUTOPROXY_CONFIG_URL;
    autoProxyOptions.lpszAutoConfigUrl = proxyScriptUrl;
    autoProxyOptions.dwAutoDetectFlags = 0;
   }
   else
   {
    autoProxyOptions.dwFlags = WINHTTP_AUTOPROXY_AUTO_DETECT;
    autoProxyOptions.dwAutoDetectFlags = (WINHTTP_AUTO_DETECT_TYPE_DHCP|WINHTTP_AUTO_DETECT_TYPE_DNS_A);
   }
   autoProxyOptions.fAutoLoginIfChallenged = true;
   bool result = WinHttpGetProxyForUrl(hSession, targetUrl, ref autoProxyOptions, ref proxyInfo);
   if(!result)
   {
    errorCode = Marshal.GetLastWin32Error();
   }
   string proxyUrl = "";
   if(proxyInfo.pwszProxy != IntPtr.Zero)
   {
    proxyUrl = Marshal.PtrToStringUni(proxyInfo.pwszProxy);
    Marshal.FreeHGlobal(proxyInfo.pwszProxy);
    string [] theUrls = proxyUrl.Split(';');
    proxyUrl = theUrls[0].Replace("PROXY ", "").Trim();
   }
   if(proxyInfo.pwszProxyBypass != IntPtr.Zero) Marshal.FreeHGlobal(proxyInfo.pwszProxyBypass);
   return IsEmpty(proxyUrl) ? null : new WebProxy(proxyUrl);
  }
  public static IWebProxy DetermineProxyForUrl(
   string targetUrl,
   bool useDefaultProxy,
   bool useDynamicProxy,
   string proxyUrl,
   string userName,
   string password
   )
  {
   IWebProxy theProxy = null;
   if(useDefaultProxy)
   {
                theProxy = WebRequest.DefaultWebProxy;
   }
   else
   {
    if(useDynamicProxy)
    {
     int errCode = 0;
     theProxy = DetermineAutoProxyForUrl(targetUrl, proxyUrl, ref errCode);
     if(errCode != 0)
     {
      throw new System.Exception(
       String.Format(
       CultureInfo.CurrentCulture,
       "Determining dynamic proxy for target url '{0}' using script url '{1}' failed with Win32 error '{2}'",
       targetUrl, IsEmpty(proxyUrl) ? "(none)" : proxyUrl, Win32Message.GetMessage(errCode))
       );
     }
    }
    else
    {
     if(IsEmpty(proxyUrl))
     {
                        theProxy = null;
     }
     else
     {
      theProxy = new WebProxy(proxyUrl);
     }
    }
   }
   if (theProxy != null)
    theProxy.Credentials = DetermineCredentials(userName, password, null);
   return theProxy;
  }
 }
}
