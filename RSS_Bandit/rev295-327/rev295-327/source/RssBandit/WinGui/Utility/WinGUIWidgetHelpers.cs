using System; 
using System.Globalization; 
using System.IO; 
using System.Threading; 
using System.Xml; 
using System.Xml.Serialization; 
using System.Drawing; 
using System.Collections; 
using System.Windows.Forms; 
using System.Runtime.InteropServices; 
using System.Runtime.Remoting.Messaging; 
using System.Net; 
using System.Text; 
using NewsComponents; 
using RssBandit.Resources; 
using RssBandit.WinGui.Controls; 
using RssBandit.WinGui.Interfaces; 
using RssBandit.WinGui.Utility; 
using RssBandit.Xml; 
using NewsComponents.Feed; 
using NewsComponents.Search; 
using NewsComponents.Utils; 
using Genghis; namespace  RssBandit.WinGui.Utility {
	
 public class  UrlCompletionExtender {
		
  private  string[] urlTemplates = new string[] {
               "http://www.{0}.com/",
               "http://www.{0}.net/",
               "http://www.{0}.org/",
               "http://www.{0}.info/",
  };
 
  private  Form ownerForm;
 
  private  IButtonControl ownerCancelButton;
 
  private  int lastExpIndex = -1;
 
  private  string toExpand = null;
 
  public  UrlCompletionExtender(Form f) {
   if (f != null && f.CancelButton != null) {
    ownerForm = f;
    ownerCancelButton = f.CancelButton;
   }
  }
 
  public  void Add(Control monitorControl) {
   this.Add(monitorControl, false);
  }
 
  public  void Add(Control monitorControl, bool includeFileCompletion) {
   if (monitorControl != null) {
    Utils.ApplyUrlCompletionToControl(monitorControl, includeFileCompletion);
    monitorControl.KeyDown += new KeyEventHandler(OnMonitorControlKeyDown);
    if (ownerForm != null && ownerCancelButton != null) {
     monitorControl.Enter += new EventHandler(OnMonitorControlEnter);
     monitorControl.Leave += new EventHandler(OnMonitorControlLeave);
    }
   }
  }
 
  private  void ResetExpansion() {
   lastExpIndex = -1;
   toExpand = null;
  }
 
  private  void RaiseExpansionIndex() {
   lastExpIndex = (++lastExpIndex % urlTemplates.Length);
  }
 
  private  void OnMonitorControlKeyDown(object sender, KeyEventArgs e) {
   Control ctrl = sender as Control;
   if (ctrl == null) return;
   TextBox tb = sender as TextBox;
   ComboBox cb = sender as ComboBox;
   bool ctrlKeyPressed = (Control.ModifierKeys & Keys.Control) == Keys.Control;
   if (e.KeyCode == Keys.Return && ctrlKeyPressed) {
    if (lastExpIndex < 0 || toExpand == null) {
     string txt = ctrl.Text;
     if (txt.Length > 0 && txt.IndexOfAny(new char[]{':', '.', '/'}) < 0) {
      toExpand = txt;
      RaiseExpansionIndex();
     }
    }
    if (lastExpIndex >= 0 && toExpand != null) {
     ctrl.Text = String.Format(urlTemplates[lastExpIndex], toExpand);
     if (tb != null)
      tb.SelectionStart = ctrl.Text.Length;
     if (cb != null && cb.DropDownStyle != ComboBoxStyle.DropDownList)
      cb.SelectionStart = cb.Text.Length;
     RaiseExpansionIndex();
    }
   } else {
    ResetExpansion();
   }
  }
 
  private  void OnMonitorControlLeave(object sender, EventArgs e) {
   ownerForm.CancelButton = ownerCancelButton;
  }
 
  private  void OnMonitorControlEnter(object sender, EventArgs e) {
   ownerForm.CancelButton = null;
  }

	}
	
 public class  CultureChanger : IDisposable {
		
  public static  CultureChanger InvariantCulture {
   get { return new CultureChanger(String.Empty); }
  }
 
  private  CultureInfo _oldCulture;
 
  public  CultureChanger(CultureInfo culture) {
   _oldCulture = Thread.CurrentThread.CurrentCulture;
   Thread.CurrentThread.CurrentCulture = culture;
  }
 
  public  CultureChanger(string culture):this(new CultureInfo(culture)) {}
 
  private  CultureChanger(){}
 
  public  void Dispose() {
   Thread.CurrentThread.CurrentCulture = _oldCulture;
  }

	}
	
 public class  UrlFormatter : IFormatProvider, ICustomFormatter {
		
  public  object GetFormat(Type formatType) {
   if (formatType == typeof (ICustomFormatter)) {
    return this;
   }
   else {
    return null;
   }
  }
 
  public  string Format(string format, object arg, IFormatProvider formatProvider) {
   string s = arg as string;
   if (s == null) {
    return String.Empty;
   }
   if (format == null) {
    return String.Format("{0}", System.Web.HttpUtility.UrlEncode(s));
   }
   try {
    Encoding encoding = Encoding.GetEncoding(format);
    return String.Format("{0}", System.Web.HttpUtility.UrlEncode(s, encoding));
   }
   catch (NotSupportedException) {
    return String.Format("{0}", System.Web.HttpUtility.UrlEncode(s));
   }
  }

	}
	
 public class  EventsHelper {
		
  private static readonly  log4net.ILog _log = Common.Logging.Log.GetLogger(typeof(EventsHelper));
 
  public static  void Fire(Delegate del,params object[] args) {
   Delegate temp = del;
   if(temp == null) {
    return;
   }
   Delegate[] delegates = temp.GetInvocationList();
   foreach(Delegate sink in delegates) {
    try {
     sink.DynamicInvoke(args);
    }
    catch (Exception sinkEx) {
     _log.Error(String.Format("Calling '{0}.{1}' caused an exception." , temp.Method.DeclaringType.FullName, temp.Method.Name ), sinkEx);
    }
   }
  }
 
  public static  void FireAsync(Delegate del,params object[] args) {
   Delegate temp = del;
   if(temp == null) {
    return;
   }
   Delegate[] delegates = del.GetInvocationList();
   AsyncFire asyncFire;
   foreach(Delegate sink in delegates) {
    asyncFire = new AsyncFire(InvokeDelegate);
    asyncFire.BeginInvoke(sink,args,null,null);
   }
  }
 delegate  void  AsyncFire (Delegate del,object[] args);
		
  [OneWay] 
  static  void InvokeDelegate(Delegate del,object[] args) {
   del.DynamicInvoke(args);
  }

	}
	
 public class  Settings : Preferences , IPersistedSettings {
		
  static  System.Collections.Specialized.StringDictionary userStore;
 
  static  bool userStoreModified;
 
  private static readonly  log4net.ILog _log = Common.Logging.Log.GetLogger(typeof(Settings));
 
  public  Settings(string path):base(path) {
   if (userStore == null) {
    userStore = new System.Collections.Specialized.StringDictionary();
    Deserialize();
    userStoreModified = false;
   }
  }
 
  public override  object GetProperty(string name, object defaultValue, Type returnType) {
   string value = userStore[Path + name];
   if (value == null)
    return defaultValue;
   try {
    return Convert.ChangeType(value, returnType);
   }
   catch (Exception e) {
    _log.Debug("Settings: The property " + name + " could not be converted to the intended type (" + returnType + ").  Using defaults.");
    _log.Debug("Settings: The exception was:", e);
    return defaultValue;
   }
  }
 
  object IPersistedSettings.GetProperty(string name, Type returnType, object defaultValue) {
   return this.GetProperty(name, defaultValue, returnType);
  }
 
  void IPersistedSettings.SetProperty(string name, object value) {
   this.SetProperty(name, value);
  }
 
  public override  void SetProperty(string name, object value) {
   if (value == null && userStore.ContainsKey(Path + name)) {
    userStore.Remove(Path + name);
    return;
   }
   userStore[Path + name] = Convert.ToString(value);
   userStoreModified = true;
  }
 
  public override  void Flush() {
   Serialize();
  }
 
  public new  void Close() {
   base.Close();
   Flush();
  }
 
  private static  FileStream CreateSettingsStream() {
   return FileHelper.OpenForWrite(RssBanditApplication.GetSettingsFileName());
  }
 
  private static  FileStream OpenSettingsStream() {
   return FileHelper.OpenForRead(RssBanditApplication.GetSettingsFileName());
  }
 
  private static  void Deserialize() {
   XmlTextReader reader = null;
   try {
    reader = new XmlTextReader( OpenSettingsStream() );
    while (reader.Read()) {
     if (reader.NodeType == XmlNodeType.Element && reader.Name == "property") {
      string name = reader.GetAttribute("name");
      string value = reader.ReadString();
      userStore[name] = value;
     }
    }
    reader.Close();
   }
   catch (Exception e) {
    if (reader != null)
     reader.Close();
    _log.Debug("Settings: There was an error while deserializing from Settings Storage.  Ignoring.");
    _log.Debug("Settings: The exception was:", e);
   }
  }
 
  private static  void Serialize() {
   if (userStoreModified == false)
    return;
   XmlTextWriter writer = null;
   try {
    writer = new XmlTextWriter(CreateSettingsStream(), null);
    writer.Formatting = Formatting.Indented;
    writer.Indentation = 4;
    writer.WriteStartDocument(true);
    writer.WriteStartElement("settings");
    foreach (System.Collections.DictionaryEntry entry in userStore) {
     writer.WriteStartElement("property");
     writer.WriteAttributeString("name", (string) entry.Key);
     writer.WriteString((string) entry.Value);
     writer.WriteEndElement();
    }
    writer.WriteEndElement();
    writer.WriteEndDocument();
    writer.Close();
    userStoreModified = false;
   }
   catch (Exception e) {
    if (writer != null)
     writer.Close();
    _log.Debug("Settings: There was an error while serializing to Storage.  Ignoring.");
    _log.Debug("Settings: The exception was:", e);
   }
  }

	}
	
 public class  SerializableWebTabState {
		
  [System.Xml.Serialization.XmlArrayAttribute("urls")]
  [System.Xml.Serialization.XmlArrayItemAttribute("url", Type = typeof(System.String), IsNullable = false)] 
  public  ArrayList Urls = new ArrayList();
 
  public static  void Save(Stream stream, SerializableWebTabState s) {
   XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(
    typeof(SerializableWebTabState), RssBanditNamespace.BrowserTabState);
   serializer.Serialize(stream, s);
  }
 
  public static  SerializableWebTabState Load(Stream stream) {
   XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(
    typeof(SerializableWebTabState), RssBanditNamespace.BrowserTabState);
   return (SerializableWebTabState)serializer.Deserialize(stream);
  }

	}
	
 internal class  TextImageItem : ITextImageItem {
		
  private  Image image;
 
  private  string text;
 
  public  TextImageItem(string text, Image image) {
   this.text = text;
   this.image = image;
  }
 
  public  Image Image {
   get { return this.image; }
  }
 
  public  string Text {
   get { return this.text; }
  }

	}
	
 internal class  WebTabState : ITabState {
		
  private static readonly  ITextImageItem[] EmptyHistoryItems = new ITextImageItem[]{};
 
  private  string _title;
 
  private  string _currentUrl;
 
  private  bool _canGoBack;
 
  private  bool _canGoForward;
 
  public  WebTabState()
  {
   _title = String.Empty;
   _currentUrl = String.Empty;
   _canGoBack = false;
   _canGoForward = false;
  }
 
  public  WebTabState(string title, string currentUrl):this()
  {
   _title = title;
   _currentUrl = currentUrl;
  }
 
  public  bool CanClose
  {
   get { return true; }
   set {}
  }
 
  public  bool CanGoBack
  {
   get { return _canGoBack; }
   set { _canGoBack = value; }
  }
 
  public  bool CanGoForward
  {
   get { return _canGoForward; }
   set { _canGoForward = value; }
  }
 
  public  string Title
  {
   get { return _title; }
   set {_title = value; }
  }
 
  public  string Url
  {
   get { return _currentUrl; }
   set { _currentUrl = value; }
  }
 
  public  ITextImageItem[] GoBackHistoryItems(int maxItems) {
   return EmptyHistoryItems;
  }
 
  public  ITextImageItem[] GoForwardHistoryItems(int maxItems) {
   return EmptyHistoryItems;
  }

	}
	
 internal class  Utils {
		
  private static readonly  log4net.ILog _log = Common.Logging.Log.GetLogger(typeof(Utils));
 
  private static  string[] probeUrls = new string[]{
   "http://www.w3c.org/", "http://www.google.com/",
   "http://www.heise.de/", "http://www.nyse.com/",
   "http://www.olm.net"
  };
 
  static  Random probeUrlRandomizer = new Random();
 
  [DllImport("wininet.dll", SetLastError=true)] 
  private extern static  bool InternetGetConnectedState(out int flags, int reserved);
 
  [DllImport("wininet.dll", SetLastError=true)] 
  private extern static  bool InternetCheckConnection(string url, int flags, int reserved);
 
  private  const int FLAG_ICC_FORCE_CONNECTION = 0x01; 
  [DllImport("wininet.dll", SetLastError=true)] 
  private static extern  bool InternetSetOption(IntPtr hInternet, uint option, IntPtr buffer, int bufferLength);
 
  [DllImport("wininet.dll", SetLastError=true)] 
  private static extern  bool InternetSetOption(IntPtr hInternet, uint option, ref INTERNET_CONNECTED_INFO buffer, int bufferLength);
 
  [StructLayout(LayoutKind.Sequential)] 
   private struct  INTERNET_CONNECTED_INFO {
			
   public  uint dwConnectedState,
    dwFlags;

		}
		
  private  const uint INTERNET_OPTION_SETTINGS_CHANGED = 39; 
  private  const uint INTERNET_STATE_CONNECTED = 0x00000001; 
  private  const uint INTERNET_STATE_DISCONNECTED_BY_USER = 0x00000010; 
  private  const uint ISO_FORCE_DISCONNECTED = 0x00000001; 
  private  const uint INTERNET_OPTION_CONNECTED_STATE = 50; 
  [Flags] private enum  InternetStates  {
   INTERNET_CONNECTION_MODEM = 0x01,
   INTERNET_CONNECTION_LAN = 0x02,
   INTERNET_CONNECTION_PROXY = 0x04,
   INTERNET_CONNECTION_MODEM_BUSY = 0x08,
   INTERNET_RAS_INSTALLED = 0x10,
   INTERNET_CONNECTION_OFFLINE = 0x20,
   INTERNET_CONNECTION_CONFIGURED = 0x40
  } 
  [Flags] private enum  NetworkAliveFlags  {
   NETWORK_ALIVE_LAN = 0x1,
   NETWORK_ALIVE_WAN = 0x2,
   NETWORK_ALIVE_AOL = 0x4
  } 
  [DllImport("sensapi.dll", SetLastError=true)] 
  private static extern  bool IsNetworkAlive(ref int flags);
 
  private static  int fullInternetStateTestCounter = 0;
 
  public static  INetState CurrentINetState(IWebProxy currentProxy, bool forceFullTest) {
   int f = 0;
   INetState state = INetState.Invalid;
   bool offline = false;
   bool connected = false;
   try {
    connected = InternetGetConnectedState(out f, 0);
   } catch (Exception ex) {
    _log.Error("InternetGetConnectedState() API call failed with error: " + Marshal.GetLastWin32Error(), ex);
   }
   InternetStates flags = (InternetStates)f;
   if (!connected && Win32.IsOSWindowsVista){
    connected = true;
   }
   if (connected) {
    bool sensApiSucceeds = true;
    try {
     int tmp = 0;
     if (!IsNetworkAlive(ref tmp)) {
      connected = false;
     }
    } catch (Exception ex) {
     _log.Error("IsNetworkAlive() API call failed with error: " + Marshal.GetLastWin32Error(), ex);
     sensApiSucceeds = false;
    }
    fullInternetStateTestCounter++;
    if (fullInternetStateTestCounter >= 2) {
     forceFullTest = true;
     fullInternetStateTestCounter = 0;
    }
    if (!sensApiSucceeds || forceFullTest) {
     connected = ApiCheckConnection(currentProxy);
     if (!connected) {
      connected = FrameworkCheckConnection(currentProxy);
     }
    }
   } else {
    if ((flags & InternetStates.INTERNET_CONNECTION_MODEM) != InternetStates.INTERNET_CONNECTION_MODEM) {
     connected = ApiCheckConnection(currentProxy);
     if (!connected) {
      connected = FrameworkCheckConnection(currentProxy);
     }
    } else {
     _log.Info("InternetGetConnectedState() flag INTERNET_CONNECTION_MODEM is set. Give up further tests...");
    }
   }
   state |= connected ? INetState.Connected: INetState.DisConnected;
   if (connected) {
    offline = ((flags & InternetStates.INTERNET_CONNECTION_OFFLINE) == InternetStates.INTERNET_CONNECTION_OFFLINE);
    state |= offline ? INetState.Offline: INetState.Online;
   }
   return state;
  }
 
  private static  string GetProbeUrl() {
   return probeUrls[probeUrlRandomizer.Next(0, probeUrls.GetUpperBound(0))];
  }
 
  public static  bool ApiCheckConnection(IWebProxy proxy) {
   string url = GetProbeUrl();
   try {
    if (InternetCheckConnection(url, 0, 0))
     return true;
   } catch (Exception ex) {
    _log.Error("ApiCheckConnection('"+url+"') failed with error: " + Marshal.GetLastWin32Error(), ex);
   }
   return false;
  }
 
  public static  bool FrameworkCheckConnection(IWebProxy proxy) {
   string url = GetProbeUrl();
   if (proxy == null)
    proxy = WebRequest.DefaultWebProxy;
   try {
    using (HttpWebResponse response = (HttpWebResponse)NewsComponents.Net.AsyncWebRequest.GetSyncResponseHeadersOnly(url, proxy, 3 * 60 * 1000)) {
     if (response != null && String.Compare(response.Method, "HEAD") == 0) {
      response.Close();
      return true;
     }
    }
   } catch (WebException ex) {
    _log.Error("FrameworkCheckConnection('"+url+"') ", ex);
    if (ex.Status == WebExceptionStatus.Timeout)
     return true;
   } catch (Exception ex) {
    _log.Error("FrameworkCheckConnection('"+url+"') ", ex);
   }
   return false;
  }
 
  public static  void SetIEOffline(bool modeOffline) {
   INTERNET_CONNECTED_INFO ci = new INTERNET_CONNECTED_INFO();
   if(modeOffline) {
    ci.dwConnectedState = INTERNET_STATE_DISCONNECTED_BY_USER;
    ci.dwFlags = ISO_FORCE_DISCONNECTED;
   } else {
    ci.dwConnectedState = INTERNET_STATE_CONNECTED;
   }
   InternetSetOption(IntPtr.Zero, INTERNET_OPTION_CONNECTED_STATE, ref
    ci, Marshal.SizeOf(typeof(INTERNET_CONNECTED_INFO)));
   RefreshIESettings();
  }
 
  private static  void RefreshIESettings() {
   InternetSetOption(IntPtr.Zero, INTERNET_OPTION_SETTINGS_CHANGED,
    IntPtr.Zero, 0);
  }
 
  private static  int[] dayIndexMap = new int[]{1,2,3,4,5,6,7,14,21,30,60,90,180,270,365};
 
  public static  TimeSpan MaxItemAgeFromIndex(int index) {
   if (index < 0) {
    return TimeSpan.Zero;
   } else if (index > dayIndexMap.Length-1) {
    return TimeSpan.MinValue;
   } else {
    return TimeSpan.FromDays(dayIndexMap[index]);
   }
  }
 
  public static  int MaxItemAgeToIndex(TimeSpan timespan) {
   int maxItemAgeDays = Math.Abs(timespan.Days);
   if (maxItemAgeDays <= dayIndexMap[6]) {
    if (maxItemAgeDays == 0) {
     return 0;
    }
    return maxItemAgeDays - 1;
   } else if (maxItemAgeDays > dayIndexMap[6] && maxItemAgeDays <= dayIndexMap[7]) {
    return 7;
   } else if (maxItemAgeDays > dayIndexMap[7] && maxItemAgeDays <= dayIndexMap[8]) {
    return 8;
   } else if (maxItemAgeDays > dayIndexMap[8] && maxItemAgeDays <= dayIndexMap[9]) {
    return 9;
   } else if (maxItemAgeDays > dayIndexMap[9] && maxItemAgeDays <= dayIndexMap[10]) {
    return 10;
   } else if (maxItemAgeDays > dayIndexMap[10] && maxItemAgeDays <= dayIndexMap[11]) {
    return 11;
   } else if (maxItemAgeDays > dayIndexMap[11] && maxItemAgeDays <= dayIndexMap[12]) {
    return 12;
   } else if (maxItemAgeDays > dayIndexMap[12] && maxItemAgeDays <= dayIndexMap[13]) {
    return 13;
   } else if (maxItemAgeDays > dayIndexMap[13] && maxItemAgeDays <= dayIndexMap[14]) {
    return 14;
   } else if (maxItemAgeDays > dayIndexMap[14] || timespan.Equals(TimeSpan.MinValue)) {
    return 15;
   } else
    return 9;
  }
 
  public static  string MapRssSearchItemAgeString(int index) {
   switch (index) {
    case 0: return SR.SearchPanel_comboRssSearchItemAge_1_hour;
    case 1: return SR.SearchPanel_comboRssSearchItemAge_x_hours(2);
    case 2: return SR.SearchPanel_comboRssSearchItemAge_x_hours(3);
    case 3: return SR.SearchPanel_comboRssSearchItemAge_x_hours(4);
    case 4: return SR.SearchPanel_comboRssSearchItemAge_x_hours(5);
    case 5: return SR.SearchPanel_comboRssSearchItemAge_x_hours(6);
    case 6: return SR.SearchPanel_comboRssSearchItemAge_x_hours(12);
    case 7: return SR.SearchPanel_comboRssSearchItemAge_x_hours(18);
    case 8: return SR.SearchPanel_comboRssSearchItemAge_1_day;
    case 9: return SR.SearchPanel_comboRssSearchItemAge_x_days(2);
    case 10: return SR.SearchPanel_comboRssSearchItemAge_x_days(3);
    case 11: return SR.SearchPanel_comboRssSearchItemAge_x_days(4);
    case 12: return SR.SearchPanel_comboRssSearchItemAge_x_days(5);
    case 13: return SR.SearchPanel_comboRssSearchItemAge_x_days(6);
    case 14: return SR.SearchPanel_comboRssSearchItemAge_x_days(7);
    case 15: return SR.SearchPanel_comboRssSearchItemAge_x_days(14);
    case 16: return SR.SearchPanel_comboRssSearchItemAge_x_days(21);
    case 17: return SR.SearchPanel_comboRssSearchItemAge_1_month;
    case 18: return SR.SearchPanel_comboRssSearchItemAge_x_months(2);
    case 19: return SR.SearchPanel_comboRssSearchItemAge_1_quarter;
    case 20: return SR.SearchPanel_comboRssSearchItemAge_x_quarters(2);
    case 21: return SR.SearchPanel_comboRssSearchItemAge_x_quarters(3);
    case 22: return SR.SearchPanel_comboRssSearchItemAge_1_year;
    case 23: return SR.SearchPanel_comboRssSearchItemAge_x_years(2);
    case 24: return SR.SearchPanel_comboRssSearchItemAge_x_years(3);
    case 25: return SR.SearchPanel_comboRssSearchItemAge_x_years(5);
    default: return String.Empty;
   }
  }
 
  public static  TimeSpan MapRssSearchItemAge(int index) {
   switch (index) {
    case 0: return new TimeSpan(1,0,0);
    case 1: return new TimeSpan(2,0,0);
    case 2: return new TimeSpan(3,0,0);
    case 3: return new TimeSpan(4,0,0);
    case 4: return new TimeSpan(5,0,0);
    case 5: return new TimeSpan(6,0,0);
    case 6: return new TimeSpan(12,0,0);
    case 7: return new TimeSpan(18,0,0);
    case 8: return new TimeSpan(24,0,0);
    case 9: return new TimeSpan(2*24,0,0);
    case 10: return new TimeSpan(3*24,0,0);
    case 11: return new TimeSpan(4*24,0,0);
    case 12: return new TimeSpan(5*24,0,0);
    case 13: return new TimeSpan(6*24,0,0);
    case 14: return new TimeSpan(7*24,0,0);
    case 15: return new TimeSpan(14*24,0,0);
    case 16: return new TimeSpan(21*24,0,0);
    case 17: return new TimeSpan(30*24,0,0);
    case 18: return new TimeSpan(60*24,0,0);
    case 19: return new TimeSpan(91*24,0,0);
    case 20: return new TimeSpan(2*91*24,0,0);
    case 21: return new TimeSpan(3*91*24,0,0);
    case 22: return new TimeSpan(365*24,0,0);
    case 23: return new TimeSpan(2*365*24,0,0);
    case 24: return new TimeSpan(3*365*24,0,0);
    case 25: return new TimeSpan(5*365*24,0,0);
    default:
     return TimeSpan.MinValue;
   }
  }
 
  public static  int MapRssSearchItemAge(TimeSpan age) {
   switch ((int)age.TotalHours) {
    case 1: return 0;
    case 2: return 1;
    case 3: return 2;
    case 4: return 3;
    case 5: return 4;
    case 6: return 5;
    case 12: return 6;
    case 18: return 7;
    case 24: return 8;
    case 2*24: return 9;
    case 3*24: return 10;
    case 4*24: return 11;
    case 5*24: return 12;
    case 6*24: return 13;
    case 7*24: return 14;
    case 14*24: return 15;
    case 21*24: return 16;
    case 30*24: return 17;
    case 60*24: return 18;
    case 91*24: return 19;
    case 2*91*24: return 20;
    case 3*91*24: return 21;
    case 365*24: return 22;
    case 2*365*24: return 23;
    case 3*365*24: return 24;
    case 5*365*24: return 25;
    default:
     return 0;
   }
  }
 
  public static  void ApplyUrlCompletionToControl(Control control) {
   ApplyUrlCompletionToControl(control, false);
  }
 
  public static  void ApplyUrlCompletionToControl(Control control, bool includeFileCompletion) {
   try {
    ShellLib.ShellAutoComplete ac = new ShellLib.ShellAutoComplete();
    if (control is ComboBox) {
     ShellLib.ShellApi.ComboBoxInfo info = new ShellLib.ShellApi.ComboBoxInfo();
     info.cbSize = System.Runtime.InteropServices.Marshal.SizeOf(info);
     if (ShellLib.ShellApi.GetComboBoxInfo(control.Handle, ref info)) {
      if (info.hwndEdit != IntPtr.Zero)
       ac.EditHandle = info.hwndEdit;
      else {
       _log.Debug("ApplyUrlCompletionToControl()::ComboBox must have the DropDown style!");
       return;
      }
     }
    } else {
     ac.EditHandle = control.Handle;
    }
    ac.ACOptions = ShellLib.ShellAutoComplete.AutoCompleteOptions.None;
    ac.ACOptions |= ShellLib.ShellAutoComplete.AutoCompleteOptions.AutoSuggest;
    ac.ACOptions |= ShellLib.ShellAutoComplete.AutoCompleteOptions.AutoAppend;
    ac.ACOptions |= ShellLib.ShellAutoComplete.AutoCompleteOptions.FilterPreFixes;
    ac.ACOptions |= (control.RightToLeft == RightToLeft.Yes ) ? ShellLib.ShellAutoComplete.AutoCompleteOptions.RtlReading : 0;
    ShellLib.IObjMgr multi = (ShellLib.IObjMgr)ShellLib.ShellAutoComplete.GetACLMulti();
    multi.Append(ShellLib.ShellAutoComplete.GetACLHistory());
    multi.Append(ShellLib.ShellAutoComplete.GetACLMRU());
    if (includeFileCompletion)
     multi.Append(ShellLib.ShellAutoComplete.GetACListISF());
    ac.ListSource = multi;
    ac.SetAutoComplete(true, null);
   } catch (Exception ex) {
    _log.Fatal("ApplyUrlCompletionToControl() failed on Control " + control.Name + ". No completion will be available there.", ex);
   }
  }
 
  public static  bool VisualStylesEnabled {
   get {
    OperatingSystem os = System.Environment.OSVersion;
    if (os.Platform == PlatformID.Win32NT && ((os.Version.Major == 5 && os.Version.Minor >= 1) || os.Version.Major > 5)) {
     if (UxTheme.IsThemeActive() && UxTheme.IsAppThemed()) {
      Win32.DLLVERSIONINFO version = new Win32.DLLVERSIONINFO();
      version.cbSize = Marshal.SizeOf(typeof(Win32.DLLVERSIONINFO));
      if (Win32.DllGetVersion(ref version) == 0) {
       return (version.dwMajorVersion > 5);
      }
     }
    }
    return false;
   }
  }

	}
	
 [Serializable] 
 public class  FinderSearchNodes {
		
  [XmlArrayItem(typeof(RssFinder))] 
  public  ArrayList RssFinderNodes = new ArrayList(2);
 
  public  FinderSearchNodes(TreeFeedsNodeBase[] nodes) {
   foreach (TreeFeedsNodeBase node in nodes) {
    this.GetFinders(node);
   }
  }
 
  public  FinderSearchNodes() { }
 
  public  void SetScopeResolveCallback(RssFinder.SearchScopeResolveCallback resolver) {
   foreach (RssFinder f in RssFinderNodes) {
    f.ScopeResolver = resolver;
   }
  }
 
  private  void GetFinders(TreeFeedsNodeBase startNode) {
   if (startNode == null)
    return;
   if (startNode.Nodes.Count == 0) {
    FinderNode agn = startNode as FinderNode;
    if (agn != null)
     this.RssFinderNodes.Add(agn.Finder);
   } else {
    foreach (TreeFeedsNodeBase node in startNode.Nodes) {
     this.GetFinders(node);
    }
   }
  }

	}
	
 [Serializable] 
 public class  RssFinder {
		
  public delegate  NewsFeed[]  SearchScopeResolveCallback (ArrayList categoryPaths, ArrayList feedUrls);
		
  private  SearchCriteriaCollection searchCriterias = null;
 
  private  NewsFeed[] searchScope = new NewsFeed[]{};
 
  private  ArrayList categoryPathScope, feedUrlScope;
 
  private  SearchScopeResolveCallback resolve;
 
  private  FinderNode container;
 
  private  bool doHighlight, dynamicItemContent = false, dynamicItemContentChecked = false, isInitialized = false;
 
  private  string fullpathname;
 
  private  string externalSearchUrl = null, externalSearchPhrase = null;
 
  private  bool externalResultMergedWithLocal = false;
 
  public  RssFinder(){
   dynamicItemContentChecked = false;
   categoryPathScope = new ArrayList(1);
   feedUrlScope = new ArrayList(1);
   searchCriterias = new SearchCriteriaCollection();
   ShowFullItemContent = true;
  }
 
  public  RssFinder(FinderNode resultContainer, SearchCriteriaCollection criterias, ArrayList categoryPathScope, ArrayList feedUrlScope, SearchScopeResolveCallback resolveSearchScope, bool doHighlight):this(){
   this.container = resultContainer;
   if (resultContainer != null)
    this.fullpathname = resultContainer.FullPath;
   if (criterias != null)
    this.searchCriterias = criterias;
   if (categoryPathScope != null)
    this.categoryPathScope = categoryPathScope;
   if (feedUrlScope != null)
    this.feedUrlScope = feedUrlScope;
   this.resolve = resolveSearchScope;
   this.doHighlight = doHighlight;
   this.dynamicItemContent = this.CheckForDynamicItemContent();
  }
 
  [XmlIgnore] 
  public  bool IsPersisted {
   get { if (container != null)
       return !container.IsTempFinderNode;
    return false;
   }
  }
 
  [XmlIgnore] 
  public  string Text {
   get {
    if (container != null)
     return container.Text;
    string[] a = fullpathname.Split(NewsHandler.CategorySeparator.ToCharArray());
    return a[a.GetLength(0)-1];
   }
   set {
    if (container != null)
     container.Text = value;
   }
  }
 
  public  string FullPath {
   get {
    if (container != null) {
     string s = container.FullPath.Trim();
     string[] a = s.Split(NewsHandler.CategorySeparator.ToCharArray());
     if (a.GetLength(0) > 1)
      return String.Join(NewsHandler.CategorySeparator,a, 1, a.GetLength(0)-1);
     return s;
    } else {
     return fullpathname;
    }
   }
   set {
    fullpathname = value;
   }
  }
 
  public  SearchCriteriaCollection SearchCriterias {
   get {
    RaiseScopeResolver();
    return searchCriterias;
   }
   set {
    searchCriterias = value;
    this.dynamicItemContent = this.CheckForDynamicItemContent();
   }
  }
 
  [XmlArray("category-scopes"), XmlArrayItem("category", Type = typeof(System.String), IsNullable = false)] 
  public  ArrayList CategoryPathScope {
   get { return categoryPathScope;}
   set { categoryPathScope = value; }
  }
 
  [XmlArray("feedurl-scopes"), XmlArrayItem("feedurl", Type = typeof(System.String), IsNullable = false)] 
  public  ArrayList FeedUrlScope {
   get { return feedUrlScope;}
   set { feedUrlScope = value; }
  }
 
  [XmlIgnore()] 
  public  NewsFeed[] SearchScope {
   get {
    RaiseScopeResolver();
    return searchScope;
   }
   set { searchScope = value; }
  }
 
  [XmlIgnore()] 
  public  bool HasDynamicItemContent {
   get {
    if (!dynamicItemContentChecked)
     this.dynamicItemContent = this.CheckForDynamicItemContent();
    return this.dynamicItemContent;
   }
  }
 
  public  bool DoHighlight {
   get { return doHighlight; }
   set { doHighlight = value; }
  }
 
  [XmlIgnore()] 
  public  bool ExternalResultMerged {
   get { return externalResultMergedWithLocal; }
   set { externalResultMergedWithLocal = value; }
  }
 
  [XmlIgnore()] 
  public  string ExternalSearchUrl {
   get { return externalSearchUrl; }
   set { externalSearchUrl = value; }
  }
 
  [XmlIgnore()] 
  public  string ExternalSearchPhrase {
   get { return externalSearchPhrase; }
   set { externalSearchPhrase = value; }
  }
 
  [XmlIgnore()] 
  public  FinderNode Container {
   get { return container; }
   set { container = value; }
  }
 
  [XmlIgnore()] 
  public  SearchScopeResolveCallback ScopeResolver {
   get { return resolve; }
   set { resolve = value; }
  }
 
  [XmlAttribute("show-full-item-content"), System.ComponentModel.DefaultValue(true)] 
  public  bool ShowFullItemContent;
 
  [System.Xml.Serialization.XmlAnyAttributeAttribute()] 
  public  System.Xml.XmlAttribute[] AnyAttr;
 
  public  void SetSearchScope(ArrayList categoryPathScope, ArrayList feedUrlScope) {
   this.categoryPathScope = categoryPathScope;
   this.feedUrlScope = feedUrlScope;
   this.isInitialized = false;
  }
 
  public  void NotifyCategoryChanged(string oldCategoryPath, string newCategoryPath) {
   categoryPathScope.Remove(oldCategoryPath);
   if (newCategoryPath != null)
    categoryPathScope.Add(newCategoryPath);
  }
 
  public  void NotifyFeedUrlChanged(string oldFeedUrl, string newFeedUrl) {
   feedUrlScope.Remove(oldFeedUrl);
   if (newFeedUrl != null)
    feedUrlScope.Add(newFeedUrl);
  }
 
  private  void RaiseScopeResolver() {
   if (this.resolve != null && !isInitialized) {
    this.searchScope = resolve(categoryPathScope, feedUrlScope);
    isInitialized = true;
   }
  }
 
  private  bool CheckForDynamicItemContent() {
   dynamicItemContentChecked = false;
   bool isDynamic = false;
   if (this.searchCriterias == null || this.searchCriterias.Count == 0)
    return isDynamic;
   foreach (ISearchCriteria icriteria in this.searchCriterias) {
    if (icriteria is SearchCriteriaAge) {
     isDynamic = true;
     break;
    }
    if (icriteria is SearchCriteriaProperty) {
     isDynamic = true;
     break;
    }
   }
   dynamicItemContentChecked = true;
   return isDynamic;
  }

	}

}
