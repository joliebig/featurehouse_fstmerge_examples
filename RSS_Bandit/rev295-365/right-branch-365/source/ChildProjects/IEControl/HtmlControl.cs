using System;
using System.ComponentModel;
using System.Drawing;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using SHDocVw;
namespace IEControl
{
 [
 System.Windows.Forms.AxHost.ClsidAttribute("{8856f961-340a-11d0-a96b-00c04fd705a2}"),
 DesignTimeVisible(true),
 DefaultProperty("Name"),
 ToolboxItem(true),
 ToolboxBitmap( typeof( HtmlControl ), "Resources.IEControl.Toolbitmap16.bmp" ),
 CLSCompliant(false),
 ComVisible(false)
 ]
 public class HtmlControl : System.Windows.Forms.AxHost {
  public static Assembly SHDocVwAssembly = null;
  private SHDocVw.IWebBrowser2 ocx = null;
  private SHDocVw.WebBrowser_V1 ocx_v1 = null;
  private AxWebBrowserEventMulticaster eventMulticaster;
  private System.Windows.Forms.AxHost.ConnectionPointCookie cookie;
  private ShellUIHelper shellHelper;
  private DocHostUIHandler uiHandler;
  string url = String.Empty;
  string html = String.Empty;
  string body = String.Empty;
  [Flags]
  private enum ControlBehaviorFlags {
   activate = 0x01,
   allowInPlaceNavigation = 0x02,
   border3d = 0x04,
   flatScrollBars = 0x08,
   scriptEnabled = 0x10,
   activeXEnabled = 0x20,
   javaEnabled = 0x40,
   bgSoundEnabled = 0x80,
   imagesDownloadEnabled = 0x100,
   videoEnabled = 0x200,
   scrollBarsEnabled = 0x400,
   silentModeEnabled = 0x800,
   clientPullEnabled = 0x1000,
   behaviorsExecuteEnabled = 0x2000,
   frameDownloadEnabled = 0x4000
  }
  private ControlBehaviorFlags cbFlags;
  private static bool lowSecurity;
  private object scriptObject;
  public event BrowserPrivacyImpactedStateChangeEventHandler PrivacyImpactedStateChange;
  public event BrowserUpdatePageStatusEventHandler UpdatePageStatus;
  public event BrowserPrintTemplateTeardownEventHandler PrintTemplateTeardown;
  public event BrowserPrintTemplateInstantiationEventHandler PrintTemplateInstantiation;
  public event BrowserNavigateErrorEventHandler NavigateError;
  public event BrowserFileDownloadEventHandler FileDownload;
  public event BrowserSetSecureLockIconEventHandler SetSecureLockIcon;
  public event BrowserClientToHostWindowEventHandler ClientToHostWindow;
  public event BrowserWindowClosingEventHandler WindowClosing;
  public event BrowserWindowSetHeightEventHandler WindowSetHeight;
  public event BrowserWindowSetWidthEventHandler WindowSetWidth;
  public event BrowserWindowSetTopEventHandler WindowSetTop;
  public event BrowserWindowSetLeftEventHandler WindowSetLeft;
  public event BrowserWindowSetResizableEventHandler WindowSetResizable;
  public event BrowserOnVisibleEventHandler OnVisible;
  public event System.EventHandler OnQuit;
  public event BrowserDocumentCompleteEventHandler DocumentComplete;
  public event BrowserNavigateComplete2EventHandler NavigateComplete;
  public event BrowserNewWindow2EventHandler NewWindow2;
  public event BrowserNewWindowEventHandler NewWindow;
  public event BrowserBeforeNavigate2EventHandler BeforeNavigate;
  public event BrowserPropertyChangeEventHandler PropertyChanged;
  public event BrowserTitleChangeEventHandler TitleChanged;
  public event System.EventHandler DownloadCompleted;
  public event System.EventHandler DownloadBegin;
  public event BrowserCommandStateChangeEventHandler CommandStateChanged;
  public event BrowserProgressChangeEventHandler ProgressChanged;
  public event BrowserStatusTextChangeEventHandler StatusTextChanged;
  public event BrowserTranslateUrlEventHandler TranslateUrl;
  public event BrowserContextMenuCancelEventHandler ShowContextMenu;
  public event KeyEventHandler TranslateAccelerator;
  public HtmlControl() : base("8856f961-340a-11d0-a96b-00c04fd705a2") {
   this.cbFlags = ControlBehaviorFlags.scrollBarsEnabled |
    ControlBehaviorFlags.imagesDownloadEnabled |
    ControlBehaviorFlags.scriptEnabled |
    ControlBehaviorFlags.javaEnabled |
    ControlBehaviorFlags.flatScrollBars |
    ControlBehaviorFlags.imagesDownloadEnabled |
    ControlBehaviorFlags.behaviorsExecuteEnabled |
    ControlBehaviorFlags.frameDownloadEnabled |
    ControlBehaviorFlags.clientPullEnabled;
   HandleCreated += new EventHandler(SelfHandleCreated);
   HandleDestroyed += new EventHandler(SelfHandleDestroyed);
   NavigateComplete += new BrowserNavigateComplete2EventHandler(SelfNavigateComplete);
  }
  static HtmlControl() {
   try {
    _UseCurrentDll();
    lowSecurity = (Environment.OSVersion.Platform != PlatformID.Win32NT);
    return;
   }
   catch{}
   finally{}
   try {
    SHDocVwAssembly = Interop.GetAssemblyForTypeLib( "SHDocVw.DLL" );
    AppDomain.CurrentDomain.AssemblyResolve += new ResolveEventHandler( RedirectSHDocAssembly );
   }
   catch{}
  }
  private static void _UseCurrentDll() {
   SHDocVwAssembly = typeof( SHDocVw.IWebBrowser2 ).Assembly;
  }
  private static Assembly RedirectSHDocAssembly( object sender, ResolveEventArgs e ) {
   if( e.Name != null && e.Name.StartsWith( "Interop.SHDocVw" ) )
    return SHDocVwAssembly;
   return null;
  }
  void SelfHandleCreated(object s, EventArgs e) {
   HandleCreated -= new EventHandler(SelfHandleCreated);
   if (DesignMode)
    return;
   if (url == null) url = String.Empty;
   if (html == null) html = String.Empty;
   if (body == null) body = String.Empty;
   if (body == String.Empty && html == String.Empty)
    url = "about:blank";
   ocx_v1 = ocx as WebBrowser_V1;
   if (ocx_v1 != null) {
    ocx_v1.NewWindow += new DWebBrowserEvents_NewWindowEventHandler(this.RaiseOnNewBrowserWindow);
   }
   if (!lowSecurity) {
    Interop.IOleObject oleObj = ocx as Interop.IOleObject;
    if (oleObj != null) {
     uiHandler = new DocHostUIHandler(this);
     oleObj.SetClientSite(uiHandler);
    }
   }
  }
  void SelfHandleDestroyed(object s, EventArgs e) {
   if (ocx_v1 != null) {
    ocx_v1.NewWindow -= new DWebBrowserEvents_NewWindowEventHandler(this.RaiseOnNewBrowserWindow);
   }
   ocx_v1 = null;
   uiHandler = null;
  }
  private void SelfNavigateComplete(object sender, BrowserNavigateComplete2Event e) {
   CheckAndActivate();
   if (this.html != String.Empty) {
    this.SetHtmlText(html);
    RaiseOnDocumentComplete(this, new BrowserDocumentCompleteEvent(e.pDisp, e.url, e.IsRootPage ));
    this.html = String.Empty;
   }
   if (this.body != String.Empty) {
    SetBodyText(body);
    RaiseOnDocumentComplete(this, new BrowserDocumentCompleteEvent(e.pDisp, e.url, e.IsRootPage));
    this.body = String.Empty;
   }
  }
  protected override void Dispose(bool disposing) {
   if( disposing ) {
    if (shellHelper != null) {
     try {
      Marshal.ReleaseComObject(shellHelper);
     } catch {}
     shellHelper = null;
    }
   }
   base.Dispose (disposing);
  }
  protected override void WndProc(ref Message m) {
   if (m.Msg == Interop.WM_CLOSE) {
    if (! this.RaiseOnQuit(this, EventArgs.Empty))
     base.WndProc (ref m);
   } else {
    base.WndProc (ref m);
   }
  }
  public void Clear() {
   this.html = String.Empty;
   this.body = String.Empty;
   this.Navigate("about:blank");
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public string Html {
   get { return this.html; }
   set {
    this.html = value; if (this.html == null) this.html = String.Empty;
    this.body = String.Empty;
   }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public string Body {
   get { return this.body; }
   set {
    this.body = value; if (this.body == null) this.body = String.Empty;
    this.html = String.Empty;
   }
  }
  public void Activate() {
   this.SetFlag(ControlBehaviorFlags.activate, true);
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool AllowInPlaceNavigation {
   get { return this.IsFlagSet(ControlBehaviorFlags.allowInPlaceNavigation); }
   set { this.SetFlag(ControlBehaviorFlags.allowInPlaceNavigation, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool Border3d {
   get { return this.IsFlagSet(ControlBehaviorFlags.border3d); }
   set { this.SetFlag(ControlBehaviorFlags.border3d, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool FlatScrollBars {
   get { return this.IsFlagSet(ControlBehaviorFlags.flatScrollBars); }
   set { this.SetFlag(ControlBehaviorFlags.flatScrollBars, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool ScriptEnabled {
   get { return this.IsFlagSet(ControlBehaviorFlags.scriptEnabled); }
   set { this.SetFlag(ControlBehaviorFlags.scriptEnabled, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool ActiveXEnabled {
   get { return this.IsFlagSet(ControlBehaviorFlags.activeXEnabled); }
   set { this.SetFlag(ControlBehaviorFlags.activeXEnabled, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool SilentModeEnabled {
   get { return this.IsFlagSet(ControlBehaviorFlags.silentModeEnabled); }
   set { this.SetFlag(ControlBehaviorFlags.silentModeEnabled, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool JavaEnabled {
   get { return this.IsFlagSet(ControlBehaviorFlags.javaEnabled); }
   set { this.SetFlag(ControlBehaviorFlags.javaEnabled, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool BackroundSoundEnabled {
   get { return this.IsFlagSet(ControlBehaviorFlags.bgSoundEnabled); }
   set { this.SetFlag(ControlBehaviorFlags.bgSoundEnabled, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool ImagesDownloadEnabled {
   get { return this.IsFlagSet(ControlBehaviorFlags.imagesDownloadEnabled); }
   set { this.SetFlag(ControlBehaviorFlags.imagesDownloadEnabled, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool VideoEnabled {
   get { return this.IsFlagSet(ControlBehaviorFlags.videoEnabled); }
   set { this.SetFlag(ControlBehaviorFlags.videoEnabled, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool ScrollBarsEnabled {
   get { return this.IsFlagSet(ControlBehaviorFlags.scrollBarsEnabled); }
   set { this.SetFlag(ControlBehaviorFlags.scrollBarsEnabled, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool ClientPullEnabled {
   get { return this.IsFlagSet(ControlBehaviorFlags.clientPullEnabled); }
   set { this.SetFlag(ControlBehaviorFlags.clientPullEnabled, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool BehaviorsExecuteEnabled {
   get { return this.IsFlagSet(ControlBehaviorFlags.behaviorsExecuteEnabled); }
   set { this.SetFlag(ControlBehaviorFlags.behaviorsExecuteEnabled, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool FrameDownloadEnabled {
   get { return this.IsFlagSet(ControlBehaviorFlags.frameDownloadEnabled); }
   set { this.SetFlag(ControlBehaviorFlags.frameDownloadEnabled, value); }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public object ScriptObject {
   get { return this.scriptObject; }
   set { this.scriptObject = value;}
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public static bool LowSecurity {
   get { return lowSecurity; }
  }
  public void EnhanceBrowserSecurityForProcess() {
   SetFeatureFlag where = SetFeatureFlag.SET_FEATURE_ON_PROCESS;
   GetFeatureFlag from_ = GetFeatureFlag.GET_FEATURE_FROM_PROCESS;
   try {
    if (!InternetFeature.IsEnabled(InternetFeatureList.FEATURE_OBJECT_CACHING, from_))
     InternetFeature.SetEnabled(InternetFeatureList.FEATURE_OBJECT_CACHING, where, true);
    if (!InternetFeature.IsEnabled(InternetFeatureList.FEATURE_MIME_HANDLING, from_))
     InternetFeature.SetEnabled(InternetFeatureList.FEATURE_MIME_HANDLING, where, true);
    if (!InternetFeature.IsEnabled(InternetFeatureList.FEATURE_MIME_SNIFFING, from_))
     InternetFeature.SetEnabled(InternetFeatureList.FEATURE_MIME_SNIFFING, where, true);
    if (!InternetFeature.IsEnabled(InternetFeatureList.FEATURE_WINDOW_RESTRICTIONS, from_))
     InternetFeature.SetEnabled(InternetFeatureList.FEATURE_WINDOW_RESTRICTIONS, where, true);
    if (!InternetFeature.IsEnabled(InternetFeatureList.FEATURE_WEBOC_POPUPMANAGEMENT, from_))
     InternetFeature.SetEnabled(InternetFeatureList.FEATURE_WEBOC_POPUPMANAGEMENT, where, true);
    if (!InternetFeature.IsEnabled(InternetFeatureList.FEATURE_ADDON_MANAGEMENT, from_))
     InternetFeature.SetEnabled(InternetFeatureList.FEATURE_ADDON_MANAGEMENT, where, true);
    if (!InternetFeature.IsEnabled(InternetFeatureList.FEATURE_SECURITYBAND, from_))
     InternetFeature.SetEnabled(InternetFeatureList.FEATURE_SECURITYBAND, where, true);
    if (!InternetFeature.IsEnabled(InternetFeatureList.FEATURE_RESTRICT_FILEDOWNLOAD, from_))
     InternetFeature.SetEnabled(InternetFeatureList.FEATURE_RESTRICT_FILEDOWNLOAD, where, true);
    if (!InternetFeature.IsEnabled(InternetFeatureList.FEATURE_RESTRICT_ACTIVEXINSTALL, from_))
     InternetFeature.SetEnabled(InternetFeatureList.FEATURE_RESTRICT_ACTIVEXINSTALL, where, true);
   } catch (EntryPointNotFoundException) {
    System.Diagnostics.Debug.WriteLine("InternetFeature() not available");
   } catch (COMException come){
    System.Diagnostics.Debug.WriteLine(String.Format("InternetFeature() failed with error ({0}):{1}", come.ErrorCode, come.Message));
   }
  }
  public static bool IsInternetFeatureEnabled(InternetFeatureList feature, GetFeatureFlag flags) {
   try {
    return InternetFeature.IsEnabled(feature, flags);
   } catch (EntryPointNotFoundException) {
    System.Diagnostics.Debug.WriteLine("InternetFeature() not available");
   }
   return false;
  }
  public static void SetInternetFeatureEnabled(InternetFeatureList feature, SetFeatureFlag flags, bool enable) {
   try {
    InternetFeature.SetEnabled(feature, flags, enable);
   } catch (EntryPointNotFoundException) {
    System.Diagnostics.Debug.WriteLine("InternetFeature() not available");
   }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public bool AnyBeforeNavigateEventListener {
   get { return (BeforeNavigate != null);}
  }
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  [Browsable(false)]
  [System.Runtime.InteropServices.DispIdAttribute(203)]
  public virtual object Document {
   get {
    if ((this.ocx == null)) {
     throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("Document", System.Windows.Forms.AxHost.ActiveXInvokeKind.PropertyGet);
    }
    return this.ocx.Document;
   }
  }
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  [Browsable(false)]
  public virtual IHTMLDocument2 Document2 {
   get {
    if ((this.ocx == null)) {
     throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("Document", System.Windows.Forms.AxHost.ActiveXInvokeKind.PropertyGet);
    }
    return this.ocx.Document as IHTMLDocument2;
   }
  }
  protected void SetHtmlText(string text) {
   if (text == null)
    text = String.Empty;
   if (!IsHandleCreated)
    return;
   if (ocx != null) {
    IHTMLDocument2 document = ocx.Document as IHTMLDocument2;
    if (document != null) {
     CheckAndActivate();
     try {
      document.Open("", null, null, null);
      object[] a = new object[]{text};
      document.Write(a);
      document.Close();
      document.ExecCommand("Refresh", false, null);
     } catch {}
    }
   }
  }
  protected void SetBodyText(string text) {
   if (text == null)
    text = String.Empty;
   if (!IsHandleCreated)
    return;
   if (ocx != null) {
    IHTMLDocument2 document = ocx.Document as IHTMLDocument2;
    if (document != null) {
     IHTMLElement e = document.GetBody();
     if (e != null) {
      CheckAndActivate();
      e.SetInnerHTML(text);
      return;
     }
    }
   }
  }
  [Browsable(false)]
  public string DocumentInnerHTML {
   get {
    string content = String.Empty;
    if (ocx == null)
     return content;
    IHTMLDocument3 document3 = ocx.Document as IHTMLDocument3;
    if (document3 == null || document3.documentElement() == null)
     return content;
    content = document3.documentElement().GetInnerHTML();
    return content;
   }
  }
  [Browsable(false)]
  public string DocumentOuterHTML {
   get {
    string content = String.Empty;
    if (ocx == null)
     return content;
    IHTMLDocument3 document3 = ocx.Document as IHTMLDocument3;
    if (document3 == null || document3.documentElement() == null)
     return content;
    content = document3.documentElement().GetOuterHTML();
    return content;
   }
  }
  [Browsable(false)]
  public string DocumentInnerText {
   get {
    string content = String.Empty;
    if (ocx == null)
     return content;
    IHTMLDocument3 document3 = ocx.Document as IHTMLDocument3;
    if (document3 == null || document3.documentElement() == null)
     return content;
    content = document3.documentElement().GetInnerText();
    return content;
   }
  }
  [Browsable(false)]
  public string DocumentOuterText {
   get {
    string content = String.Empty;
    if (ocx == null)
     return content;
    IHTMLDocument3 document3 = ocx.Document as IHTMLDocument3;
    if (document3 == null || document3.documentElement() == null)
     return content;
    content = document3.documentElement().GetOuterText();
    return content;
   }
  }
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  [Browsable(false)]
  [System.Runtime.InteropServices.DispIdAttribute(211)]
  public virtual string LocationURL {
   get {
    if ((this.ocx == null)) {
     throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("LocationURL", System.Windows.Forms.AxHost.ActiveXInvokeKind.PropertyGet);
    }
    return this.ocx.LocationURL;
   }
  }
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  [Browsable(false)]
  [System.Runtime.InteropServices.DispIdAttribute(212)]
  public virtual bool Busy {
   get {
    if ((this.ocx == null)) {
     throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("Busy", System.Windows.Forms.AxHost.ActiveXInvokeKind.PropertyGet);
    }
    return this.ocx.Busy;
   }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  [System.Runtime.InteropServices.DispIdAttribute(-515)]
  [System.Runtime.InteropServices.ComAliasNameAttribute("System.Int32")]
  public virtual int HWND {
   get {
    if ((this.ocx == null)) {
     throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("HWND", System.Windows.Forms.AxHost.ActiveXInvokeKind.PropertyGet);
    }
    return (this.ocx.HWND);
   }
  }
  [Browsable(false)]
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  [System.Runtime.InteropServices.DispIdAttribute(-525)]
  [System.ComponentModel.Bindable(System.ComponentModel.BindableSupport.Yes)]
  public virtual SHDocVw.tagREADYSTATE ReadyState {
   get {
    if ((this.ocx == null)) {
     throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("ReadyState", System.Windows.Forms.AxHost.ActiveXInvokeKind.PropertyGet);
    }
    return this.ocx.ReadyState;
   }
  }
  public virtual void ExecWB(SHDocVw.OLECMDID cmdID, SHDocVw.OLECMDEXECOPT cmdexecopt, [System.Runtime.InteropServices.Optional()] ref object pvaIn, [System.Runtime.InteropServices.Optional()] ref object pvaOut) {
   if ((this.ocx == null)) {
    throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("ExecWB", System.Windows.Forms.AxHost.ActiveXInvokeKind.MethodInvoke);
   }
   this.ocx.ExecWB(cmdID, cmdexecopt, ref pvaIn, ref pvaOut);
  }
  public virtual SHDocVw.OLECMDF QueryStatusWB(SHDocVw.OLECMDID cmdID) {
   if ((this.ocx == null)) {
    throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("QueryStatusWB", System.Windows.Forms.AxHost.ActiveXInvokeKind.MethodInvoke);
   }
   return this.ocx.QueryStatusWB(cmdID);
  }
  public virtual object GetProperty(string property) {
   if ((this.ocx == null)) {
    throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("GetProperty", System.Windows.Forms.AxHost.ActiveXInvokeKind.MethodInvoke);
   }
   return this.ocx.GetProperty(property);
  }
  public virtual void PutProperty(string property, object vtValue) {
   if ((this.ocx == null)) {
    throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("PutProperty", System.Windows.Forms.AxHost.ActiveXInvokeKind.MethodInvoke);
   }
   this.ocx.PutProperty(property, vtValue);
  }
  public virtual void ClientToWindow(ref int pcx, ref int pcy) {
   if ((this.ocx == null)) {
    throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("ClientToWindow", System.Windows.Forms.AxHost.ActiveXInvokeKind.MethodInvoke);
   }
   this.ocx.ClientToWindow(ref pcx, ref pcy);
  }
  public virtual void Stop() {
   if ((this.ocx == null)) {
    throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("Stop", System.Windows.Forms.AxHost.ActiveXInvokeKind.MethodInvoke);
   }
   this.ocx.Stop();
  }
  public virtual void Refresh2([System.Runtime.InteropServices.Optional()] ref object level) {
   if ((this.ocx == null)) {
    throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("Refresh2", System.Windows.Forms.AxHost.ActiveXInvokeKind.MethodInvoke);
   }
   this.ocx.Refresh2(ref level);
  }
  public virtual void Navigate( string location ) {
   if (location == null || location.Length == 0)
    location = "about:blank";
   object flags = null;
   object targetFrame = null;
   object postData = null;
   object headers = null;
   Navigate( location, ref flags, ref targetFrame, ref postData, ref headers );
  }
  public virtual void Navigate(string uRL, [System.Runtime.InteropServices.Optional()] ref object flags, [System.Runtime.InteropServices.Optional()] ref object targetFrameName, [System.Runtime.InteropServices.Optional()] ref object postData, [System.Runtime.InteropServices.Optional()] ref object headers) {
   if ((this.ocx == null)) {
    throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("Navigate", System.Windows.Forms.AxHost.ActiveXInvokeKind.MethodInvoke);
   }
   try {
    this.ocx.Navigate(uRL, ref flags, ref targetFrameName, ref postData, ref headers);
   } catch {}
  }
  public virtual void Navigate2(ref object uRL, [System.Runtime.InteropServices.Optional()] ref object flags, [System.Runtime.InteropServices.Optional()] ref object targetFrameName, [System.Runtime.InteropServices.Optional()] ref object postData, [System.Runtime.InteropServices.Optional()] ref object headers) {
   if ((this.ocx == null)) {
    throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("Navigate2", System.Windows.Forms.AxHost.ActiveXInvokeKind.MethodInvoke);
   }
   try {
    this.ocx.Navigate2(ref uRL, ref flags, ref targetFrameName, ref postData, ref headers);
   } catch {}
  }
  public virtual void GoForward() {
   if ((this.ocx == null)) {
    throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("GoForward", System.Windows.Forms.AxHost.ActiveXInvokeKind.MethodInvoke);
   }
   this.ocx.GoForward();
  }
  public virtual void GoBack() {
   if ((this.ocx == null)) {
    throw new System.Windows.Forms.AxHost.InvalidActiveXStateException("GoBack", System.Windows.Forms.AxHost.ActiveXInvokeKind.MethodInvoke);
   }
   this.ocx.GoBack();
  }
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public void ShowDialogAddFavorite(string url, string title) {
   if (url == null)
    throw new ArgumentNullException("url");
   if (title == null)
    throw new ArgumentNullException("title");
   if (url.IndexOf("://") == -1)
    url = "http://" + url;
   Uri uri = new Uri(url);
   object oTitle = title;
   ShellUIHelperClass uih = new ShellUIHelperClass();
   try {
    uih.AddFavorite(uri.AbsoluteUri, ref oTitle);
   } catch (Exception ex) {
    System.Diagnostics.Trace.WriteLine("IEControl::ShellUIHelperClass.AddFavorite() exception - " + ex.Message);
   }
   uih = null;
  }
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public void ShowDialogPrintPreview() {
   try {
    object o = null;
    this.ExecWB(SHDocVw.OLECMDID.OLECMDID_PRINTPREVIEW,
     SHDocVw.OLECMDEXECOPT.OLECMDEXECOPT_DODEFAULT, ref o, ref o);
   } catch (Exception ex) {
    System.Diagnostics.Trace.WriteLine("IEControl::ExecWB(OLECMDID_PRINTPREVIEW) exception - " + ex.Message);
   }
  }
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public void ShowDialogPrint() {
   try {
    object o = null;
    this.ExecWB(SHDocVw.OLECMDID.OLECMDID_PRINT,
     SHDocVw.OLECMDEXECOPT.OLECMDEXECOPT_DODEFAULT, ref o, ref o);
   } catch (Exception ex) {
    System.Diagnostics.Trace.WriteLine("IEControl::ExecWB(OLECMDID_PRINT) exception - " + ex.Message);
   }
  }
  [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
  public void Print() {
   try {
    object o = null;
    this.ExecWB(SHDocVw.OLECMDID.OLECMDID_PRINT,
     SHDocVw.OLECMDEXECOPT.OLECMDEXECOPT_DONTPROMPTUSER, ref o, ref o);
   } catch (Exception ex) {
    System.Diagnostics.Trace.WriteLine("IEControl::ExecWB(OLECMDID_PRINT) exception - " + ex.Message);
   }
  }
  public new bool Focus() {
   DoVerb(Interop.OLEIVERB_UIACTIVATE);
   if (ocx != null && ocx.Document != null) {
    IHTMLDocument2 doc = ocx.Document as IHTMLDocument2;
    if (doc != null) {
     IHTMLElement2 e = doc.GetBody();
     if (e != null) {
      e.focus();
      return true;
     }
    }
   }
   return false;
  }
  private bool IsFlagSet (ControlBehaviorFlags flag) {
   return ( (this.cbFlags & flag) == flag );
  }
  private void SetFlag(ControlBehaviorFlags flag, bool theValue) {
   if (theValue) {
    this.cbFlags |= flag;
   } else {
    this.cbFlags &= ~flag;
   }
  }
  private void CheckAndActivate() {
   if (this.IsFlagSet(ControlBehaviorFlags.activate)) {
    DoVerb(Interop.OLEIVERB_UIACTIVATE);
    this.SetFlag(ControlBehaviorFlags.activate, false);
   }
  }
  protected override void CreateSink() {
   try {
    this.eventMulticaster = new AxWebBrowserEventMulticaster(this);
    this.cookie = new System.Windows.Forms.AxHost.ConnectionPointCookie(this.ocx, this.eventMulticaster, typeof(SHDocVw.DWebBrowserEvents2));
   } catch (System.Exception ex) {
    System.Diagnostics.Trace.WriteLine("IEControl::CreateSink() exception - " + ex.Message);
   }
  }
  protected override void DetachSink() {
   try {
    this.cookie.Disconnect();
   } catch (System.Exception ex) {
    System.Diagnostics.Trace.WriteLine("IEControl::DetachSink() exception - " + ex.Message);
   }
  }
  protected override void AttachInterfaces() {
   try {
    this.ocx = ((SHDocVw.IWebBrowser2)(base.GetOcx()));
   } catch (System.Exception ex) {
    System.Diagnostics.Trace.WriteLine("IEControl::AttachInterfaces() exception - " + ex.Message);
   }
  }
  internal void RaiseOnNewBrowserWindow(string URL, int Flags, string TargetFrameName, ref object PostData, string Headers, ref bool Processed) {
   Processed = false;
   if (this.NewWindow != null) {
    BrowserNewWindowEvent newwindowEvent = new BrowserNewWindowEvent(URL, Processed);
    this.NewWindow(this, newwindowEvent);
    Processed = newwindowEvent.Cancel;
   }
  }
  internal void RaiseOnPrivacyImpactedStateChange(object sender, BrowserPrivacyImpactedStateChangeEvent e) {
   if ((this.PrivacyImpactedStateChange != null)) {
    this.PrivacyImpactedStateChange(sender, e);
   }
  }
  internal void RaiseOnUpdatePageStatus(object sender, BrowserUpdatePageStatusEvent e) {
   if ((this.UpdatePageStatus != null)) {
    this.UpdatePageStatus(sender, e);
   }
  }
  internal void RaiseOnPrintTemplateTeardown(object sender, BrowserPrintTemplateTeardownEvent e) {
   if ((this.PrintTemplateTeardown != null)) {
    this.PrintTemplateTeardown(sender, e);
   }
  }
  internal void RaiseOnPrintTemplateInstantiation(object sender, BrowserPrintTemplateInstantiationEvent e) {
   if ((this.PrintTemplateInstantiation != null)) {
    this.PrintTemplateInstantiation(sender, e);
   }
  }
  internal void RaiseOnNavigateError(object sender, BrowserNavigateErrorEvent e) {
   if ((this.NavigateError != null)) {
    this.NavigateError(sender, e);
   }
  }
  internal void RaiseOnFileDownload(object sender, BrowserFileDownloadEvent e) {
   if ((this.FileDownload != null)) {
    this.FileDownload(sender, e);
   }
  }
  internal void RaiseOnSetSecureLockIcon(object sender, BrowserSetSecureLockIconEvent e) {
   if ((this.SetSecureLockIcon != null)) {
    this.SetSecureLockIcon(sender, e);
   }
  }
  internal void RaiseOnClientToHostWindow(object sender, BrowserClientToHostWindowEvent e) {
   if ((this.ClientToHostWindow != null)) {
    this.ClientToHostWindow(sender, e);
   }
  }
  internal void RaiseOnWindowClosing(object sender, BrowserWindowClosingEvent e) {
   if ((this.WindowClosing != null)) {
    this.WindowClosing(sender, e);
   }
  }
  internal void RaiseOnWindowSetHeight(object sender, BrowserWindowSetHeightEvent e) {
   if ((this.WindowSetHeight != null)) {
    this.WindowSetHeight(sender, e);
   }
  }
  internal void RaiseOnWindowSetWidth(object sender, BrowserWindowSetWidthEvent e) {
   if ((this.WindowSetWidth != null)) {
    this.WindowSetWidth(sender, e);
   }
  }
  internal void RaiseOnWindowSetTop(object sender, BrowserWindowSetTopEvent e) {
   if ((this.WindowSetTop != null)) {
    this.WindowSetTop(sender, e);
   }
  }
  internal void RaiseOnWindowSetLeft(object sender, BrowserWindowSetLeftEvent e) {
   if ((this.WindowSetLeft != null)) {
    this.WindowSetLeft(sender, e);
   }
  }
  internal void RaiseOnWindowSetResizable(object sender, BrowserWindowSetResizableEvent e) {
   if ((this.WindowSetResizable != null)) {
    this.WindowSetResizable(sender, e);
   }
  }
  internal void RaiseOnVisible(object sender, BrowserOnVisibleEvent e) {
   if ((this.OnVisible != null)) {
    this.OnVisible(sender, e);
   }
  }
  internal bool RaiseOnQuit(object sender, System.EventArgs e) {
   if ((this.OnQuit != null)) {
    this.OnQuit(sender, e);
    return true;
   }
   return false;
  }
  internal void RaiseOnDocumentComplete(object sender, BrowserDocumentCompleteEvent e) {
   if ((this.DocumentComplete != null)) {
    this.DocumentComplete(sender, e);
   }
  }
  internal void RaiseOnNavigateComplete2(object sender, BrowserNavigateComplete2Event e) {
   if ((this.NavigateComplete != null)) {
    this.NavigateComplete(sender, e);
   }
  }
  internal void RaiseOnNewWindow2(object sender, BrowserNewWindow2Event e) {
   if ((this.NewWindow2 != null)) {
    this.NewWindow2(sender, e);
   }
  }
  internal void RaiseOnBeforeNavigate2(object sender, BrowserBeforeNavigate2Event e) {
   if ((this.BeforeNavigate != null)) {
    this.BeforeNavigate(sender, e);
   }
  }
  internal void RaiseOnPropertyChange(object sender, BrowserPropertyChangeEvent e) {
   if ((this.PropertyChanged != null)) {
    this.PropertyChanged(sender, e);
   }
  }
  internal void RaiseOnTitleChange(object sender, BrowserTitleChangeEvent e) {
   if ((this.TitleChanged != null)) {
    this.TitleChanged(sender, e);
   }
  }
  internal void RaiseOnDownloadComplete(object sender, System.EventArgs e) {
   if ((this.DownloadCompleted != null)) {
    this.DownloadCompleted(sender, e);
   }
  }
  internal void RaiseOnDownloadBegin(object sender, System.EventArgs e) {
   if ((this.DownloadBegin != null)) {
    this.DownloadBegin(sender, e);
   }
  }
  internal void RaiseOnCommandStateChange(object sender, BrowserCommandStateChangeEvent e) {
   if ((this.CommandStateChanged != null)) {
    this.CommandStateChanged(sender, e);
   }
  }
  internal void RaiseOnProgressChange(object sender, BrowserProgressChangeEvent e) {
   if ((this.ProgressChanged != null)) {
    this.ProgressChanged(sender, e);
   }
  }
  internal void RaiseOnStatusTextChange(object sender, BrowserStatusTextChangeEvent e) {
   if ((this.StatusTextChanged != null)) {
    this.StatusTextChanged(sender, e);
   }
  }
  internal void RaiseOnShowContextMenu(BrowserContextMenuCancelEventArgs e) {
   if (ShowContextMenu != null)
    ShowContextMenu(this, e);
  }
  internal void RaiseOnTranslateUrl(BrowserTranslateUrlEventArgs e) {
   if (TranslateUrl != null)
    TranslateUrl(this, e);
  }
  internal void RaiseOnTranslateAccelerator(KeyEventArgs e) {
   if (TranslateAccelerator!= null)
    TranslateAccelerator(this, e);
  }
 }
 [CLSCompliant(false)]
 [ComVisible(false)]
 public class AxWebBrowserEventMulticaster : SHDocVw.DWebBrowserEvents2 {
  private HtmlControl parent;
  public AxWebBrowserEventMulticaster(HtmlControl parent) {
   this.parent = parent;
  }
  public virtual void PrivacyImpactedStateChange(bool bImpacted) {
   BrowserPrivacyImpactedStateChangeEvent privacyimpactedstatechangeEvent = new BrowserPrivacyImpactedStateChangeEvent(bImpacted);
   this.parent.RaiseOnPrivacyImpactedStateChange(this.parent, privacyimpactedstatechangeEvent);
  }
  public virtual void UpdatePageStatus(object pDisp, ref object nPage, ref object fDone) {
   BrowserUpdatePageStatusEvent updatepagestatusEvent = new BrowserUpdatePageStatusEvent(pDisp, nPage, fDone);
   this.parent.RaiseOnUpdatePageStatus(this.parent, updatepagestatusEvent);
   nPage = updatepagestatusEvent.nPage;
   fDone = updatepagestatusEvent.fDone;
  }
  public virtual void PrintTemplateTeardown(object pDisp) {
   BrowserPrintTemplateTeardownEvent printtemplateteardownEvent = new BrowserPrintTemplateTeardownEvent(pDisp);
   this.parent.RaiseOnPrintTemplateTeardown(this.parent, printtemplateteardownEvent);
  }
  public virtual void PrintTemplateInstantiation(object pDisp) {
   BrowserPrintTemplateInstantiationEvent printtemplateinstantiationEvent = new BrowserPrintTemplateInstantiationEvent(pDisp);
   this.parent.RaiseOnPrintTemplateInstantiation(this.parent, printtemplateinstantiationEvent);
  }
  public virtual void NavigateError(object pDisp, ref object uRL, ref object frame, ref object statusCode, ref bool cancel) {
   BrowserNavigateErrorEvent navigateerrorEvent = new BrowserNavigateErrorEvent(pDisp, uRL, frame, statusCode, cancel);
   this.parent.RaiseOnNavigateError(this.parent, navigateerrorEvent);
   uRL = navigateerrorEvent.uRL;
   frame = navigateerrorEvent.frame;
   statusCode = navigateerrorEvent.statusCode;
   cancel = navigateerrorEvent.Cancel;
  }
  public virtual void FileDownload(ref bool cancel) {
   BrowserFileDownloadEvent filedownloadEvent = new BrowserFileDownloadEvent(cancel);
   this.parent.RaiseOnFileDownload(this.parent, filedownloadEvent);
   cancel = filedownloadEvent.Cancel;
  }
  public virtual void SetSecureLockIcon(int secureLockIcon) {
   BrowserSetSecureLockIconEvent setsecurelockiconEvent = new BrowserSetSecureLockIconEvent(secureLockIcon);
   this.parent.RaiseOnSetSecureLockIcon(this.parent, setsecurelockiconEvent);
  }
  public virtual void ClientToHostWindow(ref int cX, ref int cY) {
   BrowserClientToHostWindowEvent clienttohostwindowEvent = new BrowserClientToHostWindowEvent(cX, cY);
   this.parent.RaiseOnClientToHostWindow(this.parent, clienttohostwindowEvent);
   cX = clienttohostwindowEvent.cX;
   cY = clienttohostwindowEvent.cY;
  }
  public virtual void WindowClosing(bool isChildWindow, ref bool cancel) {
   BrowserWindowClosingEvent windowclosingEvent = new BrowserWindowClosingEvent(isChildWindow, cancel);
   this.parent.RaiseOnWindowClosing(this.parent, windowclosingEvent);
   cancel = windowclosingEvent.Cancel;
  }
  public virtual void WindowSetHeight(int height) {
   BrowserWindowSetHeightEvent windowsetheightEvent = new BrowserWindowSetHeightEvent(height);
   this.parent.RaiseOnWindowSetHeight(this.parent, windowsetheightEvent);
  }
  public virtual void WindowSetWidth(int width) {
   BrowserWindowSetWidthEvent windowsetwidthEvent = new BrowserWindowSetWidthEvent(width);
   this.parent.RaiseOnWindowSetWidth(this.parent, windowsetwidthEvent);
  }
  public virtual void WindowSetTop(int top) {
   BrowserWindowSetTopEvent windowsettopEvent = new BrowserWindowSetTopEvent(top);
   this.parent.RaiseOnWindowSetTop(this.parent, windowsettopEvent);
  }
  public virtual void WindowSetLeft(int left) {
   BrowserWindowSetLeftEvent windowsetleftEvent = new BrowserWindowSetLeftEvent(left);
   this.parent.RaiseOnWindowSetLeft(this.parent, windowsetleftEvent);
  }
  public virtual void WindowSetResizable(bool resizable) {
   BrowserWindowSetResizableEvent windowsetresizableEvent = new BrowserWindowSetResizableEvent(resizable);
   this.parent.RaiseOnWindowSetResizable(this.parent, windowsetresizableEvent);
  }
  public virtual void OnTheaterMode(bool theaterMode) {
  }
  public virtual void OnFullScreen(bool fullScreen) {
  }
  public virtual void OnStatusBar(bool statusBar) {
  }
  public virtual void OnMenuBar(bool menuBar) {
  }
  public virtual void OnToolBar(bool toolBar) {
  }
  public virtual void OnVisible(bool visible) {
   BrowserOnVisibleEvent onvisibleEvent = new BrowserOnVisibleEvent(visible);
   this.parent.RaiseOnVisible(this.parent, onvisibleEvent);
  }
  public virtual void OnQuit() {
   System.EventArgs onquitEvent = new System.EventArgs();
   if (this.parent != null)
    this.parent.RaiseOnQuit(this.parent, onquitEvent);
  }
  public virtual void DocumentComplete(object pDisp, ref object uRL) {
   BrowserDocumentCompleteEvent documentcompleteEvent = new BrowserDocumentCompleteEvent(pDisp, uRL, pDisp == this.parent.GetOcx());
   this.parent.RaiseOnDocumentComplete(this.parent, documentcompleteEvent);
   uRL = documentcompleteEvent.url;
  }
  public virtual void NavigateComplete2(object pDisp, ref object uRL) {
   BrowserNavigateComplete2Event navigatecomplete2Event = new BrowserNavigateComplete2Event(pDisp, uRL, pDisp == this.parent.GetOcx());
   this.parent.RaiseOnNavigateComplete2(this.parent, navigatecomplete2Event);
   uRL = navigatecomplete2Event.url;
  }
  public virtual void NewWindow2(ref object ppDisp, ref bool cancel) {
   BrowserNewWindow2Event newwindow2Event = new BrowserNewWindow2Event(ppDisp, cancel);
   this.parent.RaiseOnNewWindow2(this.parent, newwindow2Event);
   ppDisp = newwindow2Event.ppDisp;
   cancel = newwindow2Event.Cancel;
  }
  public virtual void BeforeNavigate2(object pDisp, ref object uRL, ref object flags, ref object targetFrameName, ref object postData, ref object headers, ref bool cancel) {
   BrowserBeforeNavigate2Event beforenavigate2Event = new BrowserBeforeNavigate2Event(pDisp, uRL, flags, targetFrameName, postData, headers, cancel, pDisp == this.parent.GetOcx());
   this.parent.RaiseOnBeforeNavigate2(this.parent, beforenavigate2Event);
   uRL = beforenavigate2Event.url;
   flags = beforenavigate2Event.flags;
   targetFrameName = beforenavigate2Event.targetFrameName;
   postData = beforenavigate2Event.postData;
   headers = beforenavigate2Event.headers;
   cancel = beforenavigate2Event.Cancel;
  }
  public virtual void PropertyChange(string szProperty) {
   BrowserPropertyChangeEvent propertychangeEvent = new BrowserPropertyChangeEvent(szProperty);
   this.parent.RaiseOnPropertyChange(this.parent, propertychangeEvent);
  }
  public virtual void TitleChange(string text) {
   BrowserTitleChangeEvent titlechangeEvent = new BrowserTitleChangeEvent(text);
   this.parent.RaiseOnTitleChange(this.parent, titlechangeEvent);
  }
  public virtual void DownloadComplete() {
   System.EventArgs downloadcompleteEvent = new System.EventArgs();
   this.parent.RaiseOnDownloadComplete(this.parent, downloadcompleteEvent);
  }
  public virtual void DownloadBegin() {
   System.EventArgs downloadbeginEvent = new System.EventArgs();
   this.parent.RaiseOnDownloadBegin(this.parent, downloadbeginEvent);
  }
  public virtual void CommandStateChange(int command, bool enable) {
   BrowserCommandStateChangeEvent commandstatechangeEvent = new BrowserCommandStateChangeEvent(command, enable);
   this.parent.RaiseOnCommandStateChange(this.parent, commandstatechangeEvent);
  }
  public virtual void ProgressChange(int progress, int progressMax) {
   BrowserProgressChangeEvent progresschangeEvent = new BrowserProgressChangeEvent(progress, progressMax);
   this.parent.RaiseOnProgressChange(this.parent, progresschangeEvent);
  }
  public virtual void StatusTextChange(string text) {
   BrowserStatusTextChangeEvent statustextchangeEvent = new BrowserStatusTextChangeEvent(text);
   this.parent.RaiseOnStatusTextChange(this.parent, statustextchangeEvent);
  }
 }
}
