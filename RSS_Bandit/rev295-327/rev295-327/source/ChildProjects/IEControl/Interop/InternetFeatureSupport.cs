using System; 
using System.Runtime.InteropServices; namespace  IEControl {
	
 internal sealed class  InternetFeature {
		
  private static  PrivateInterop pi = new PrivateInterop();
 
  public static  bool IsEnabled(InternetFeatureList feature, GetFeatureFlag flags) {
   if (!OSHelper.IsOSAtLeastWindowsXPSP2 && !OSHelper.IsIE6)
    return false;
   return pi.isEnabled(feature, flags);
  }
 
  public static  void SetEnabled(InternetFeatureList feature, SetFeatureFlag flags, bool enable) {
   if (OSHelper.IsOSAtLeastWindowsXPSP2 && OSHelper.IsIE6) {
    pi.setEnabled(feature, flags, enable);
   }
  }
 
  private  InternetFeature() {}
 
  sealed class  PrivateInterop {
			
   internal  bool isEnabled(InternetFeatureList feature, GetFeatureFlag flags) {
    int HRESULT = CoInternetIsFeatureEnabled(feature, (int)flags);
    Marshal.ThrowExceptionForHR(HRESULT);
    return (HRESULT == Interop.S_OK);
   }
 
   internal  void setEnabled(InternetFeatureList feature, SetFeatureFlag flags, bool enable) {
    int HRESULT = CoInternetSetFeatureEnabled(feature, (int)flags, enable);
    Marshal.ThrowExceptionForHR(HRESULT);
   }
 
   [DllImport("urlmon.dll")]
   [PreserveSig]
   [return:MarshalAs(UnmanagedType.Error)] 
   private static extern  int CoInternetIsFeatureEnabled(
    InternetFeatureList FeatureEntry,
    [MarshalAs(UnmanagedType.U4)] int dwFlags);
 
   [DllImport("urlmon.dll")]
   [PreserveSig]
   [return:MarshalAs(UnmanagedType.Error)] 
   private static extern  int CoInternetSetFeatureEnabled(
    InternetFeatureList FeatureEntry,
    [MarshalAs(UnmanagedType.U4)] int dwFlags,
    bool fEnable);

		}

	}
	
 public enum  InternetFeatureList : int 
 {
  FEATURE_OBJECT_CACHING = 0,
  FEATURE_ZONE_ELEVATION = 1,
  FEATURE_MIME_HANDLING = 2,
  FEATURE_MIME_SNIFFING = 3,
  FEATURE_WINDOW_RESTRICTIONS = 4,
  FEATURE_WEBOC_POPUPMANAGEMENT = 5,
  FEATURE_BEHAVIORS = 6,
  FEATURE_DISABLE_MK_PROTOCOL = 7,
  FEATURE_LOCALMACHINE_LOCKDOWN = 8,
  FEATURE_SECURITYBAND = 9,
  FEATURE_RESTRICT_ACTIVEXINSTALL = 10,
  FEATURE_VALIDATE_NAVIGATE_URL = 11,
  FEATURE_RESTRICT_FILEDOWNLOAD = 12,
  FEATURE_ADDON_MANAGEMENT = 13,
  FEATURE_PROTOCOL_LOCKDOWN = 14,
  FEATURE_HTTP_USERNAME_PASSWORD_DISABLE = 15,
  FEATURE_SAFE_BINDTOOBJECT = 16,
  FEATURE_UNC_SAVEDFILECHECK = 17,
  FEATURE_GET_URL_DOM_FILEPATH_UNENCODED = 18,
  FEATURE_TABBED_BROWSING = 19,
  FEATURE_SSLUX = 20,
  FEATURE_DISABLE_NAVIGATION_SOUNDS = 21,
  FEATURE_DISABLE_LEGACY_COMPRESSION = 22,
  FEATURE_FORCE_ADDR_AND_STATUS = 23,
  FEATURE_XMLHTTP = 24,
  FEATURE_DISABLE_TELNET_PROTOCOL = 25,
  FEATURE_FEEDS = 26,
  FEATURE_BLOCK_INPUT_PROMPTS = 27,
  FEATURE_ENTRY_COUNT = 28
 } 
 [Flags] 
 public enum  SetFeatureFlag : int 
 {
  SET_FEATURE_ON_THREAD = 0x00000001,
  SET_FEATURE_ON_PROCESS = 0x00000002,
  SET_FEATURE_IN_REGISTRY = 0x00000004,
  SET_FEATURE_ON_THREAD_LOCALMACHINE = 0x00000008,
  SET_FEATURE_ON_THREAD_INTRANET = 0x00000010,
  SET_FEATURE_ON_THREAD_TRUSTED = 0x00000020,
  SET_FEATURE_ON_THREAD_INTERNET = 0x00000040,
  SET_FEATURE_ON_THREAD_RESTRICTED = 0x00000080,
 } 
 [Flags] 
 public enum  GetFeatureFlag : int  {
  GET_FEATURE_FROM_THREAD = 0x00000001,
  GET_FEATURE_FROM_PROCESS = 0x00000002,
  GET_FEATURE_FROM_REGISTRY = 0x00000004,
  GET_FEATURE_FROM_THREAD_LOCALMACHINE = 0x00000008,
  GET_FEATURE_FROM_THREAD_INTRANET = 0x00000010,
  GET_FEATURE_FROM_THREAD_TRUSTED = 0x00000020,
  GET_FEATURE_FROM_THREAD_INTERNET = 0x00000040,
  GET_FEATURE_FROM_THREAD_RESTRICTED = 0x00000080,
 }
}
