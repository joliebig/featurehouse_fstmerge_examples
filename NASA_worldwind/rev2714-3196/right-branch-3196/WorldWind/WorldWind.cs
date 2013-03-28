using System;
using System.Collections;
using System.ComponentModel;
using System.Diagnostics;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Security.Permissions;
using System.Threading;
using System.Windows.Forms;
using WorldWind.Net.Monitor;
using WorldWind.Configuration;
using WorldWind.PluginEngine;
using WorldWind.Camera;
using WorldWind.Menu;
using WorldWind.Net;
using WorldWind.Net.Wms;
using WorldWind.Terrain;
using WorldWind.Renderable;
using Utility;
using AxSHDocVw;
namespace WorldWind
{
 public class MainApplication : System.Windows.Forms.Form, IGlobe
 {
  public const string WebsiteUrl = "http://worldwind.arc.nasa.gov/";
  public const string CreditsWebsiteUrl = "http://worldwind.arc.nasa.gov/credits.html";
  public static string Release;
  public static readonly string DirectoryPath = Path.GetDirectoryName(Application.ExecutablePath);
  private Splash splashScreen;
  private PlaceBuilder placeBuilderDialog;
  private RapidFireModisManager rapidFireModisManager;
  private AnimatedEarthManager animatedEarthMananger;
  private GotoDialog gotoDialog;
  private PathMaker pathMaker;
  private WMSBrowser wmsBrowser;
  private PluginDialog pluginManager;
  private ProgressMonitor progressMonitor;
  private AboutDialog aboutDialog;
  private System.ComponentModel.IContainer components;
  private System.Windows.Forms.ImageList imageListFunctions;
  private System.Windows.Forms.ToolBarButton toolBarButtonSearch;
  private System.Windows.Forms.ToolBarButton toolBarButtonPosition;
  private System.Windows.Forms.ToolBarButton toolBarButtonLatLonLines;
  private System.Windows.Forms.ToolBarButton toolBarButtonWebsite;
  private System.Windows.Forms.ToolBarButton toolBarButtonKeyChart;
  private System.Windows.Forms.ToolBarButton toolBarButtonLayerManager;
  private System.Windows.Forms.ToolBarButton toolBarButtonWMS;
  private System.Windows.Forms.ToolBarButton toolBarButtonAnimatedEarth;
  private System.Windows.Forms.ToolBarButton toolBarButtonRapidFireModis;
  private System.Windows.Forms.ToolBarButton toolBarButtonAddons;
  private System.Windows.Forms.ContextMenu contextMenuAddons;
  private System.Windows.Forms.MainMenu mainMenu;
  private System.Windows.Forms.MenuItem menuItemFile;
  private System.Windows.Forms.MenuItem menuItemView;
  private System.Windows.Forms.MenuItem menuItemShowPosition;
  private System.Windows.Forms.MenuItem menuItemShowCrosshairs;
  private System.Windows.Forms.MenuItem menuItemModisHotSpots;
  private System.Windows.Forms.MenuItem menuItemConstantMotion;
  private System.Windows.Forms.MenuItem menuItemPointGoTo;
  private System.Windows.Forms.MenuItem menuItemShowLatLonLines;
  private System.Windows.Forms.MenuItem menuItemVerticalExaggeration;
  private System.Windows.Forms.MenuItem menuItemPlanetAxis;
  private System.Windows.Forms.MenuItem menuItemSpacer2;
  private System.Windows.Forms.MenuItem menuItemSpacer3;
  private System.Windows.Forms.MenuItem menuItemAnimatedEarth;
  private System.Windows.Forms.MenuItem menuItemCoordsToClipboard;
  private System.Windows.Forms.MenuItem menuItemQuit;
  private System.Windows.Forms.MenuItem menuItemKeyChart;
  private System.Windows.Forms.MenuItem menuItemWalkthrough;
  private System.Windows.Forms.MenuItem menuItemAbout;
  private System.Windows.Forms.MenuItem menuItemSaveScreenShot;
  private System.Windows.Forms.MenuItem menuItemShowToolbar;
  private System.Windows.Forms.MenuItem menuItemWMS;
  private System.Windows.Forms.MenuItem menuItemWebsite;
  private System.Windows.Forms.MenuItem menuItemSpacer10;
  private System.Windows.Forms.MenuItem menuItemSpacer11;
  private System.Windows.Forms.MenuItem menuItemSeparator3;
  private System.Windows.Forms.MenuItem menuItemForums;
  private System.Windows.Forms.MenuItem menuItemChatRoom;
  private System.Windows.Forms.MenuItem menuItemHotspots;
  private System.Windows.Forms.MenuItem menuItemFaq;
  private System.Windows.Forms.MenuItem menuItemAlwaysOnTop;
  private System.Windows.Forms.MenuItem menuItemFullScreen;
  private System.Windows.Forms.MenuItem menuItemEditPaste;
  private System.Windows.Forms.MenuItem menuItemEdit;
  private System.Windows.Forms.MenuItem menuItemTools;
  private System.Windows.Forms.MenuItem menuItemHelp;
  private System.Windows.Forms.MenuItem menuItemInertia;
  private System.Windows.Forms.MenuItem menuItemLockPlanetAxis;
  private System.Windows.Forms.MenuItem menuItem2;
  private System.Windows.Forms.MenuItem menuItemReset;
  private System.Windows.Forms.MenuItem menuItemLayerManager;
  private System.Windows.Forms.MenuItem menuItem3;
  private System.Windows.Forms.MenuItem menuItemCameraBanks;
  private System.Windows.Forms.MenuItem menuItemOptions;
  private System.Windows.Forms.MenuItem menuItemPlayScript;
  private System.Windows.Forms.MenuItem menuItemStopScript;
  private System.Windows.Forms.MenuItem menuItemRefreshCurrentView;
  private System.Windows.Forms.MenuItem menuItemPluginManager;
  private System.Windows.Forms.MenuItem menuItem1;
  private System.Windows.Forms.MenuItem menuItemPlugins;
  private System.Windows.Forms.MenuItem menuItem7;
  private System.Windows.Forms.MenuItem menuItem5;
  private System.Windows.Forms.MenuItem menuItemWiki;
        private System.Windows.Forms.Panel webBrowserPanel;
        private System.Windows.Forms.Panel webBrowserButtonsPanel;
        private System.Windows.Forms.TextBox webBrowserURL;
        private System.Windows.Forms.Button webBrowserBack;
        private System.Windows.Forms.Button webBrowserGo;
        private System.Windows.Forms.Button webBrowserStop;
        private System.Windows.Forms.Button webBrowserForward;
  private FormWindowState normalWindowState;
  private static bool startFullScreen;
  public static string CurrentSettingsDirectory;
  public static bool issetCurrentSettingsDirectory;
  public static WorldWindSettings Settings = new WorldWindSettings();
  private static WorldWindUri worldWindUri;
  private static string[] cmdArgs;
  private WorldWindow worldWindow;
  private System.Collections.Hashtable availableWorldList = new Hashtable();
  private System.Windows.Forms.MenuItem menuItem6;
        private System.Windows.Forms.MenuItem menuItemConfigWizard;
        private MenuItem menuItemShowWebBrowser;
        private MenuItem menuItemBrowserHoriz;
        private MenuItem menuItemBrowserVertical;
        private MenuItem menuItemBrowserVisible;
        private Splitter browserSplitterHorz;
        private Splitter browserSplitterVert;
        private AxSHDocVw.AxWebBrowser webBrowser;
  private PluginCompiler compiler;
  [STAThread]
  static void Main(string[] args)
  {
   try
   {
    Version ver = new Version(Application.ProductVersion);
    Release = string.Format("{0}.{1}.{2}.{3}", ver.Major, ver.Minor, ver.Build, ver.Revision);
    IntPtr handle = GetWWHandle();
    if (!System.IntPtr.Zero.Equals(handle))
    {
     if(args.Length>0)
      NativeMethods.SendArgs( handle, string.Join("\n",args) );
     return;
    }
    if(BindingsCheck.FiftyBindingsWarning()) return;
                System.Threading.Thread.CurrentThread.Name = "Main Thread";
    ParseArgs(args);
    if(CurrentSettingsDirectory == null)
    {
     LoadSettings();
     World.LoadSettings();
    }
    else
    {
     LoadSettings(CurrentSettingsDirectory);
     World.LoadSettings(CurrentSettingsDirectory);
    }
    Application.ThreadException += new ThreadExceptionEventHandler(Application_ThreadException);
    MainApplication app = new MainApplication();
    Application.Idle += new EventHandler(app.WorldWindow.OnApplicationIdle);
    Application.Run(app);
    World.Settings.Save();
    DataProtector dp = new DataProtector(DataProtector.Store.USE_USER_STORE);
    Settings.ProxyUsername = dp.TransparentEncrypt(Settings.ProxyUsername);
    Settings.ProxyPassword = dp.TransparentEncrypt(Settings.ProxyPassword);
    Settings.Save();
   }
   catch (NullReferenceException)
   {
   }
   catch (Exception caught)
   {
    Exception e;
    string errorMessages;
    try
    {
     Utility.Log.Write(caught);
    }
    catch
    {
    }
    finally
    {
     e = caught;
     errorMessages = "The following error(s) occurred:";
     do
     {
      errorMessages += "\r\n" + e.Message;
      e = e.InnerException;
     }
     while( e != null );
     Abort(errorMessages);
    }
   }
  }
  public MainApplication()
  {
   if(Settings.ConfigurationWizardAtStartup)
   {
    if(!File.Exists(Settings.FileName))
    {
     Settings.ConfigurationWizardAtStartup = false;
    }
    ConfigurationWizard.Wizard wizard = new ConfigurationWizard.Wizard( Settings );
    wizard.TopMost = true;
    wizard.ShowInTaskbar = true;
    wizard.ShowDialog();
   }
   using( this.splashScreen = new Splash() )
   {
    this.splashScreen.Owner = this;
    this.splashScreen.Show();
    this.splashScreen.SetText("Initializing...");
    Application.DoEvents();
    InitializeComponent();
    if(startFullScreen && !FullScreen)
    {
     FullScreen = true;
    }
    long CacheUpperLimit = (long)Settings.CacheSizeMegaBytes * 1024L * 1024L;
    long CacheLowerLimit = (long)Settings.CacheSizeMegaBytes * 768L * 1024L;
    worldWindow.Cache = new Cache(
     Settings.CachePath,
     CacheLowerLimit,
     CacheUpperLimit,
     Settings.CacheCleanupInterval,
     Settings.TotalRunTime );
    WorldWind.Net.WebDownload.Log404Errors = World.Settings.Log404Errors;
    DirectoryInfo worldsXmlDir = new DirectoryInfo( Settings.ConfigPath );
    if (!worldsXmlDir.Exists)
     throw new ApplicationException(
      string.Format(CultureInfo.CurrentCulture,
      "World Wind configuration directory '{0}' could not be found.", worldsXmlDir.FullName));
    FileInfo[] worldXmlDescriptorFiles = worldsXmlDir.GetFiles("*.xml");
    int worldIndex = 0;
    menuItemFile.MenuItems.Add(
     0, new System.Windows.Forms.MenuItem("-"));
    foreach (FileInfo worldXmlDescriptorFile in worldXmlDescriptorFiles)
    {
     try
     {
                        Log.Write(Log.Levels.Debug+1, "CONF", "checking world " + worldXmlDescriptorFile.FullName + " ...");
                        World w = WorldWind.ConfigurationLoader.Load(worldXmlDescriptorFile.FullName, worldWindow.Cache);
      if(!availableWorldList.Contains(w.Name))
       this.availableWorldList.Add(w.Name, worldXmlDescriptorFile.FullName);
      w.Dispose();
      System.Windows.Forms.MenuItem mi = new System.Windows.Forms.MenuItem(w.Name, new System.EventHandler(OnWorldChange));
      menuItemFile.MenuItems.Add(worldIndex, mi);
      worldIndex++;
     }
     catch( Exception caught )
     {
      splashScreen.SetError( worldXmlDescriptorFile + ": " + caught.Message );
      Log.Write(caught);
     }
    }
                Log.Write(Log.Levels.Debug, "CONF", "loading startup world...");
                OpenStartupWorld();
    float[] verticalExaggerationMultipliers = { 0.0f, 1.0f, 1.5f, 2.0f, 3.0f, 5.0f, 7.0f, 10.0f };
    foreach (float multiplier in verticalExaggerationMultipliers)
    {
     MenuItem curItem = new MenuItem(multiplier.ToString("f1",CultureInfo.CurrentCulture) + "x", new EventHandler(this.menuItemVerticalExaggerationChange));
     curItem.RadioCheck = true;
     this.menuItemVerticalExaggeration.MenuItems.Add(curItem);
     if (Math.Abs(multiplier - World.Settings.VerticalExaggeration)<0.1f)
      curItem.Checked = true;
    }
    this.menuItemPointGoTo.Checked = World.Settings.CameraIsPointGoto;
    this.menuItemInertia.Checked = World.Settings.CameraHasInertia;
    this.menuItemConstantMotion.Checked = World.Settings.CameraHasMomentum;
    this.menuItemCameraBanks.Checked = World.Settings.CameraBankLock;
    this.menuItemPlanetAxis.Checked = World.Settings.ShowPlanetAxis;
    this.menuItemShowLatLonLines.Checked = World.Settings.ShowLatLonLines;
    this.menuItemShowToolbar.Checked = World.Settings.ShowToolbar;
    this.menuItemLayerManager.Checked = World.Settings.ShowLayerManager;
    this.worldWindow.ShowLayerManager = World.Settings.ShowLayerManager;
    this.menuItemLockPlanetAxis.Checked = World.Settings.CameraTwistLock;
    this.menuItemShowCrosshairs.Checked = World.Settings.ShowCrosshairs;
    this.menuItemShowPosition.Checked = World.Settings.ShowPosition;
    while (!this.splashScreen.IsDone)
     System.Threading.Thread.Sleep(50);
    worldWindow.Render();
                WorldWindow.Focus();
   }
            webBrowserPanel.Visible = false;
            menuItemBrowserHoriz.Checked = false;
            browserSplitterHorz.Visible = false;
            browserSplitterVert.Visible = false;
            worldWindow.Dock = DockStyle.Fill;
   Rectangle screenBounds = Screen.GetBounds(this);
   this.Location = new Point(screenBounds.Width / 2 - this.Size.Width / 2, screenBounds.Height / 2 - this.Size.Height / 2);
        }
  static MainApplication()
  {
  }
        protected override void OnGotFocus(EventArgs e)
        {
            if (worldWindow != null)
                worldWindow.Focus();
            base.OnGotFocus(e);
        }
  public WorldWindow WorldWindow
  {
   get
   {
    return worldWindow;
   }
  }
  public float VerticalExaggeration
  {
   get
   {
    return World.Settings.VerticalExaggeration;
   }
   set
   {
    World.Settings.VerticalExaggeration = value;
    this.worldWindow.Invalidate();
    string label = value.ToString("f1", CultureInfo.CurrentCulture)+"x";
    foreach (MenuItem m in this.menuItemVerticalExaggeration.MenuItems)
     m.Checked = (label==m.Text);
   }
  }
  public Splash SplashScreen
  {
   get
   {
    return splashScreen;
   }
  }
  public MainMenu MainMenu
  {
   get
   {
    return mainMenu;
   }
  }
  public MenuItem ToolsMenu
  {
   get
   {
    return menuItemTools;
   }
  }
  public MenuItem PluginsMenu
  {
   get
   {
    return menuItemPlugins;
   }
  }
  public static string[] CmdArgs
  {
   get
   {
    return cmdArgs;
   }
  }
  private bool FullScreen
  {
   get
   {
    return this.FormBorderStyle == FormBorderStyle.None;
   }
   set
   {
    if (value)
    {
     this.normalWindowState = this.WindowState;
     this.WindowState = FormWindowState.Normal;
     this.FormBorderStyle = FormBorderStyle.None;
     this.Menu = null;
     this.WindowState = FormWindowState.Maximized;
    }
    else
    {
     this.WindowState = normalWindowState;
     this.FormBorderStyle = FormBorderStyle.Sizable;
     this.Menu = mainMenu;
    }
   }
  }
  public static void Abort( string errorMessages )
  {
   ErrorDisplay errorDialog = new ErrorDisplay();
   errorDialog.errorMessages(errorMessages);
   errorDialog.ShowDialog();
   Environment.Exit(0);
  }
        public void BrowseTo(string url)
        {
            if (this.webBrowserPanel.Visible == true)
            {
                webBrowser.Navigate(url);
            }
            else
            {
                try { System.Diagnostics.Process.Start(url); }
                catch (Exception e) { }
            }
 }
  protected override void Dispose( bool disposing )
  {
   if(animatedEarthMananger!=null)
   {
    animatedEarthMananger.Dispose();
    animatedEarthMananger = null;
   }
   if( disposing )
   {
    if (components != null)
    {
     components.Dispose();
    }
   }
   base.Dispose( disposing );
  }
  protected override void OnKeyUp(KeyEventArgs e)
  {
   e.Handled = HandleKeyUp(e);
   base.OnKeyUp(e);
  }
  protected override void OnActivated(EventArgs e)
  {
   this.menuItemWMS.Checked = this.wmsBrowser!=null && this.wmsBrowser.Visible;
   this.menuItemAnimatedEarth.Checked = this.animatedEarthMananger!=null && this.animatedEarthMananger.Visible;
   this.menuItemModisHotSpots.Checked = this.rapidFireModisManager!=null && this.rapidFireModisManager.Visible;
   UpdateToolBarStates();
   base.OnActivated (e);
  }
  protected override void OnLoad(EventArgs e)
  {
   if (worldWindUri!=null)
   {
    ProcessWorldWindUri();
   }
   base.OnLoad (e);
  }
  private void InitializeComponent()
  {
   this.components = new System.ComponentModel.Container();
   System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(MainApplication));
   this.worldWindow = new WorldWind.WorldWindow();
   this.mainMenu = new System.Windows.Forms.MainMenu();
   this.menuItemFile = new System.Windows.Forms.MenuItem();
   this.menuItemSaveScreenShot = new System.Windows.Forms.MenuItem();
   this.menuItemSpacer11 = new System.Windows.Forms.MenuItem();
   this.menuItemQuit = new System.Windows.Forms.MenuItem();
   this.menuItemEdit = new System.Windows.Forms.MenuItem();
   this.menuItemCoordsToClipboard = new System.Windows.Forms.MenuItem();
   this.menuItemEditPaste = new System.Windows.Forms.MenuItem();
   this.menuItem2 = new System.Windows.Forms.MenuItem();
   this.menuItemRefreshCurrentView = new System.Windows.Forms.MenuItem();
   this.menuItemView = new System.Windows.Forms.MenuItem();
   this.menuItemShowToolbar = new System.Windows.Forms.MenuItem();
            this.menuItemShowWebBrowser = new System.Windows.Forms.MenuItem();
            this.menuItemBrowserHoriz = new System.Windows.Forms.MenuItem();
            this.menuItemBrowserVertical = new System.Windows.Forms.MenuItem();
   this.menuItemLayerManager = new System.Windows.Forms.MenuItem();
   this.menuItem3 = new System.Windows.Forms.MenuItem();
   this.menuItemShowLatLonLines = new System.Windows.Forms.MenuItem();
   this.menuItemPlanetAxis = new System.Windows.Forms.MenuItem();
   this.menuItemShowCrosshairs = new System.Windows.Forms.MenuItem();
   this.menuItemShowPosition = new System.Windows.Forms.MenuItem();
   this.menuItemSpacer3 = new System.Windows.Forms.MenuItem();
   this.menuItemConstantMotion = new System.Windows.Forms.MenuItem();
   this.menuItemInertia = new System.Windows.Forms.MenuItem();
   this.menuItemPointGoTo = new System.Windows.Forms.MenuItem();
   this.menuItemLockPlanetAxis = new System.Windows.Forms.MenuItem();
   this.menuItemCameraBanks = new System.Windows.Forms.MenuItem();
   this.menuItemSpacer2 = new System.Windows.Forms.MenuItem();
   this.menuItemVerticalExaggeration = new System.Windows.Forms.MenuItem();
   this.menuItemAlwaysOnTop = new System.Windows.Forms.MenuItem();
   this.menuItemFullScreen = new System.Windows.Forms.MenuItem();
   this.menuItemReset = new System.Windows.Forms.MenuItem();
   this.menuItemTools = new System.Windows.Forms.MenuItem();
   this.menuItemWMS = new System.Windows.Forms.MenuItem();
   this.menuItemAnimatedEarth = new System.Windows.Forms.MenuItem();
   this.menuItemModisHotSpots = new System.Windows.Forms.MenuItem();
   this.menuItem5 = new System.Windows.Forms.MenuItem();
   this.menuItemConfigWizard = new System.Windows.Forms.MenuItem();
   this.menuItemOptions = new System.Windows.Forms.MenuItem();
   this.menuItemPlugins = new System.Windows.Forms.MenuItem();
   this.menuItemPluginManager = new System.Windows.Forms.MenuItem();
   this.menuItem1 = new System.Windows.Forms.MenuItem();
   this.menuItem7 = new System.Windows.Forms.MenuItem();
   this.menuItemPlayScript = new System.Windows.Forms.MenuItem();
   this.menuItemStopScript = new System.Windows.Forms.MenuItem();
   this.menuItemHelp = new System.Windows.Forms.MenuItem();
   this.menuItemFaq = new System.Windows.Forms.MenuItem();
   this.menuItemWiki = new System.Windows.Forms.MenuItem();
   this.menuItemKeyChart = new System.Windows.Forms.MenuItem();
   this.menuItemWalkthrough = new System.Windows.Forms.MenuItem();
   this.menuItem6 = new System.Windows.Forms.MenuItem();
   this.menuItemForums = new System.Windows.Forms.MenuItem();
   this.menuItemChatRoom = new System.Windows.Forms.MenuItem();
   this.menuItemSpacer10 = new System.Windows.Forms.MenuItem();
   this.menuItemWebsite = new System.Windows.Forms.MenuItem();
   this.menuItemHotspots = new System.Windows.Forms.MenuItem();
   this.menuItemSeparator3 = new System.Windows.Forms.MenuItem();
   this.menuItemAbout = new System.Windows.Forms.MenuItem();
   this.toolBarButtonAddons = new System.Windows.Forms.ToolBarButton();
   this.contextMenuAddons = new System.Windows.Forms.ContextMenu();
   this.toolBarButtonLayerManager = new System.Windows.Forms.ToolBarButton();
   this.toolBarButtonWMS = new System.Windows.Forms.ToolBarButton();
   this.toolBarButtonAnimatedEarth = new System.Windows.Forms.ToolBarButton();
   this.toolBarButtonRapidFireModis = new System.Windows.Forms.ToolBarButton();
   this.imageListFunctions = new System.Windows.Forms.ImageList(this.components);
   this.toolBarButtonKeyChart = new System.Windows.Forms.ToolBarButton();
   this.toolBarButtonWebsite = new System.Windows.Forms.ToolBarButton();
   this.toolBarButtonSearch = new System.Windows.Forms.ToolBarButton();
   this.toolBarButtonPosition = new System.Windows.Forms.ToolBarButton();
   this.toolBarButtonLatLonLines = new System.Windows.Forms.ToolBarButton();
            this.webBrowser = new AxWebBrowser();
            this.menuItemBrowserVisible = new System.Windows.Forms.MenuItem();
            this.browserSplitterHorz = new System.Windows.Forms.Splitter();
            this.browserSplitterVert = new System.Windows.Forms.Splitter();
            this.webBrowserPanel = new System.Windows.Forms.Panel();
            this.webBrowserButtonsPanel = new System.Windows.Forms.Panel();
            this.webBrowserBack = new System.Windows.Forms.Button();
            this.webBrowserForward = new System.Windows.Forms.Button();
            this.webBrowserURL = new System.Windows.Forms.TextBox();
            this.webBrowserStop = new System.Windows.Forms.Button();
            this.webBrowserGo = new System.Windows.Forms.Button();
            this.webBrowserPanel.SuspendLayout();
            this.webBrowserButtonsPanel.SuspendLayout();
   this.SuspendLayout();
   this.worldWindow.AllowDrop = true;
   this.worldWindow.Cache = null;
   this.worldWindow.Caption = "";
   this.worldWindow.CurrentWorld = null;
   this.worldWindow.Dock = System.Windows.Forms.DockStyle.Top;
   this.worldWindow.IsRenderDisabled = false;
   this.worldWindow.Location = new System.Drawing.Point(0, 0);
   this.worldWindow.Name = "worldWindow";
   this.worldWindow.ShowLayerManager = false;
   this.worldWindow.Size = new System.Drawing.Size(992, 441);
   this.worldWindow.TabIndex = 0;
   this.worldWindow.Text = "worldWindow";
   this.worldWindow.DragEnter += new System.Windows.Forms.DragEventHandler(app_DragEnter);
   this.mainMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                      this.menuItemFile,
                      this.menuItemEdit,
                      this.menuItemView,
                      this.menuItemTools,
                      this.menuItemPlugins,
                      this.menuItemHelp});
   this.menuItemFile.Index = 0;
   this.menuItemFile.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                       this.menuItemSaveScreenShot,
                       this.menuItemSpacer11,
                       this.menuItemQuit});
   this.menuItemFile.Text = "&File";
   this.menuItemSaveScreenShot.Index = 0;
   this.menuItemSaveScreenShot.Text = "&Save Screenshot...\tCtrl+S";
   this.menuItemSaveScreenShot.Click += new System.EventHandler(this.menuItemSaveScreenShot_Click);
   this.menuItemSpacer11.Index = 1;
   this.menuItemSpacer11.Text = "-";
   this.menuItemQuit.Index = 2;
   this.menuItemQuit.Text = "E&xit\tAlt+F4";
   this.menuItemQuit.Click += new System.EventHandler(this.menuItemQuit_Click);
   this.menuItemEdit.Index = 1;
   this.menuItemEdit.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                       this.menuItemCoordsToClipboard,
                       this.menuItemEditPaste,
                       this.menuItem2,
                       this.menuItemRefreshCurrentView});
   this.menuItemEdit.Text = "&Edit";
   this.menuItemCoordsToClipboard.Index = 0;
   this.menuItemCoordsToClipboard.Text = "&Copy Coordinates\tCtrl+C";
   this.menuItemCoordsToClipboard.Click += new System.EventHandler(this.menuItemCoordsToClipboard_Click);
   this.menuItemEditPaste.Index = 1;
   this.menuItemEditPaste.Text = "&Paste Coordinates\tCtrl+V";
   this.menuItemEditPaste.Click += new System.EventHandler(this.menuItemEditPaste_Click);
   this.menuItem2.Index = 2;
   this.menuItem2.Text = "-";
   this.menuItemRefreshCurrentView.Index = 3;
   this.menuItemRefreshCurrentView.Text = "&Refresh View\tF5";
   this.menuItemRefreshCurrentView.Click += new System.EventHandler(this.menuItemRefreshCurrentView_Click);
   this.menuItemView.Index = 2;
   this.menuItemView.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                       this.menuItemShowToolbar,
   this.menuItemBrowserVisible,
            this.menuItemShowWebBrowser,
                       this.menuItemLayerManager,
                       this.menuItem3,
                       this.menuItemShowLatLonLines,
                       this.menuItemPlanetAxis,
                       this.menuItemShowCrosshairs,
                       this.menuItemShowPosition,
                       this.menuItemSpacer3,
                       this.menuItemConstantMotion,
                       this.menuItemInertia,
                       this.menuItemPointGoTo,
                       this.menuItemLockPlanetAxis,
                       this.menuItemCameraBanks,
                       this.menuItemSpacer2,
                       this.menuItemVerticalExaggeration,
                       this.menuItemAlwaysOnTop,
                       this.menuItemFullScreen,
                       this.menuItemReset});
   this.menuItemView.Text = "&View";
   this.menuItemShowToolbar.Checked = true;
   this.menuItemShowToolbar.Index = 0;
   this.menuItemShowToolbar.Text = "&Toolbar\tCtrl+T";
   this.menuItemShowToolbar.Click += new System.EventHandler(this.menuItemShowToolbar_Click);
            this.menuItemShowWebBrowser.Index = 1;
            this.menuItemShowWebBrowser.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItemBrowserHoriz,
            this.menuItemBrowserVertical});
            this.menuItemShowWebBrowser.Text = "Browser Orientation";
            this.menuItemShowWebBrowser.Enabled = false;
            this.menuItemBrowserHoriz.Checked = true;
            this.menuItemBrowserHoriz.Index = 0;
            this.menuItemBrowserHoriz.Text = "Horizontal";
            this.menuItemBrowserHoriz.Click += new System.EventHandler(this.menuItemBrowserHoriz_Click);
            this.menuItemBrowserVertical.Index = 1;
            this.menuItemBrowserVertical.Text = "Vertical";
            this.menuItemBrowserVertical.Click += new System.EventHandler(this.menuItemBrowserVertical_Click);
   this.menuItemLayerManager.Index = 1;
   this.menuItemLayerManager.Text = "&Layer Manager\tL";
   this.menuItemLayerManager.Click += new System.EventHandler(this.menuItemLayerManager_Click);
   this.menuItem3.Index = 2;
   this.menuItem3.Text = "-";
   this.menuItemShowLatLonLines.Index = 3;
   this.menuItemShowLatLonLines.Text = "Show L&at/Lon Lines\tF7";
   this.menuItemShowLatLonLines.Click += new System.EventHandler(this.menuItemShowLatLonLines_Click);
   this.menuItemPlanetAxis.Index = 4;
   this.menuItemPlanetAxis.Text = "Show Planet A&xis\tF8";
   this.menuItemPlanetAxis.Click += new System.EventHandler(this.menuItemPlanetAxis_Click);
   this.menuItemShowCrosshairs.Index = 5;
   this.menuItemShowCrosshairs.Text = "Show &Cross Hairs\tF9";
   this.menuItemShowCrosshairs.Click += new System.EventHandler(this.menuItemShowCrosshairs_Click);
   this.menuItemShowPosition.Index = 6;
   this.menuItemShowPosition.Text = "Show &Position\tF10";
   this.menuItemShowPosition.Click += new System.EventHandler(this.menuItemShowPosition_Click);
   this.menuItemSpacer3.Index = 7;
   this.menuItemSpacer3.Text = "-";
   this.menuItemConstantMotion.Index = 8;
   this.menuItemConstantMotion.Text = "&Motion Momentum\tF11";
   this.menuItemConstantMotion.Click += new System.EventHandler(this.menuItemConstantMotion_Click);
   this.menuItemInertia.Index = 9;
   this.menuItemInertia.Text = "Planet &Inertia";
   this.menuItemInertia.Click += new System.EventHandler(this.menuItemInertia_Click);
   this.menuItemPointGoTo.Checked = true;
   this.menuItemPointGoTo.Index = 10;
   this.menuItemPointGoTo.Text = "Point &Go-To\tF12";
   this.menuItemPointGoTo.Click += new System.EventHandler(this.menuItemPointGoTo_Click);
   this.menuItemLockPlanetAxis.Index = 11;
   this.menuItemLockPlanetAxis.Text = "Twi&st Lock";
   this.menuItemLockPlanetAxis.Click += new System.EventHandler(this.menuItemLockPlanetAxis_Click);
   this.menuItemCameraBanks.Index = 12;
   this.menuItemCameraBanks.Text = "&Bank Lock";
   this.menuItemCameraBanks.Click += new System.EventHandler(this.menuItemCameraBanking_Click);
   this.menuItemSpacer2.Index = 13;
   this.menuItemSpacer2.Text = "-";
   this.menuItemVerticalExaggeration.Index = 14;
   this.menuItemVerticalExaggeration.Text = "&Vertical Exaggeration";
   this.menuItemAlwaysOnTop.Index = 15;
   this.menuItemAlwaysOnTop.Text = "Al&ways on Top\tAlt+A";
   this.menuItemAlwaysOnTop.Click += new System.EventHandler(this.menuItemAlwaysOnTop_Click);
   this.menuItemFullScreen.Index = 16;
   this.menuItemFullScreen.Text = "&Full Screen\tAlt+Enter";
   this.menuItemFullScreen.Click += new System.EventHandler(this.menuItemFullScreen_Click);
   this.menuItemReset.Index = 17;
   this.menuItemReset.Text = "&Reset Default View\tSpace";
   this.menuItemReset.Click += new System.EventHandler(this.menuItemReset_Click);
   this.menuItemTools.Index = 3;
   this.menuItemTools.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                        this.menuItemWMS,
                        this.menuItemAnimatedEarth,
                        this.menuItemModisHotSpots,
                        this.menuItem5,
                        this.menuItemConfigWizard,
                        this.menuItemOptions});
   this.menuItemTools.Text = "&Tools";
   this.menuItemWMS.Index = 0;
   this.menuItemWMS.RadioCheck = true;
   this.menuItemWMS.Text = "&WMS Browser\tB";
   this.menuItemWMS.Click += new System.EventHandler(this.menuItemWMS_Click);
   this.menuItemAnimatedEarth.Index = 1;
   this.menuItemAnimatedEarth.RadioCheck = true;
   this.menuItemAnimatedEarth.Text = "&Scientific Visualization Studio\tF1";
   this.menuItemAnimatedEarth.Click += new System.EventHandler(this.menuItemAnimatedEarth_Click);
   this.menuItemModisHotSpots.Index = 2;
   this.menuItemModisHotSpots.RadioCheck = true;
   this.menuItemModisHotSpots.Text = "&Rapid Fire MODIS\tF2";
   this.menuItemModisHotSpots.Click += new System.EventHandler(this.menuItemModisHotSpots_Click);
   this.menuItem5.Index = 3;
   this.menuItem5.Text = "-";
   this.menuItemConfigWizard.Index = 4;
   this.menuItemConfigWizard.Text = "&Configuration Wizard";
   this.menuItemConfigWizard.Click += new System.EventHandler(this.menuItemConfigWizard_Click);
   this.menuItemOptions.Index = 5;
   this.menuItemOptions.Text = "&Options\tAlt+W";
   this.menuItemOptions.Click += new System.EventHandler(this.menuItemOptions_Click);
   this.menuItemPlugins.Index = 4;
   this.menuItemPlugins.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                       this.menuItemPluginManager,
                       this.menuItem1,
                       this.menuItem7});
   this.menuItemPlugins.Text = "&Plug-Ins";
   this.menuItemPluginManager.Index = 0;
   this.menuItemPluginManager.Text = "&Load/Unload...";
   this.menuItemPluginManager.Click += new System.EventHandler(this.menuItemPluginManager_Click);
   this.menuItem1.Index = 1;
   this.menuItem1.Text = "-";
   this.menuItem7.Index = 2;
   this.menuItem7.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                       this.menuItemPlayScript,
                       this.menuItemStopScript});
   this.menuItem7.Text = "&Scripts";
   this.menuItemPlayScript.Index = 0;
   this.menuItemPlayScript.Text = "&Play Script...";
   this.menuItemPlayScript.Click += new System.EventHandler(this.menuItemPlayScript_Click);
   this.menuItemStopScript.Enabled = false;
   this.menuItemStopScript.Index = 1;
   this.menuItemStopScript.Text = "S&top Current Script";
   this.menuItemStopScript.Click += new System.EventHandler(this.menuItemStopScript_Click);
   this.menuItemHelp.Index = 5;
   this.menuItemHelp.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                       this.menuItemFaq,
                       this.menuItemWiki,
                       this.menuItemKeyChart,
                       this.menuItemWalkthrough,
                       this.menuItem6,
                       this.menuItemForums,
                       this.menuItemChatRoom,
                       this.menuItemSpacer10,
                       this.menuItemWebsite,
                       this.menuItemHotspots,
                       this.menuItemSeparator3,
                       this.menuItemAbout});
   this.menuItemHelp.Text = "&Help";
   this.menuItemFaq.Index = 0;
   this.menuItemFaq.Text = "&Frequently Asked Questions";
   this.menuItemFaq.Click += new System.EventHandler(this.menuItemFaq_Click);
   this.menuItemWiki.Index = 1;
   this.menuItemWiki.Text = "&Knowledge Base";
   this.menuItemWiki.Click += new System.EventHandler(this.menuItemWiki_Click);
   this.menuItemKeyChart.Index = 2;
   this.menuItemKeyChart.Text = "Keyboard &Chart";
   this.menuItemKeyChart.Click += new System.EventHandler(this.menuItemKeyChart_Click);
   this.menuItemWalkthrough.Index = 3;
   this.menuItemWalkthrough.Text = "&Walkthrough";
   this.menuItemWalkthrough.Click += new System.EventHandler(this.menuItemWalkthrough_Click);
   this.menuItem6.Index = 4;
   this.menuItem6.Text = "-";
   this.menuItemForums.Index = 5;
   this.menuItemForums.Text = "World Wind &Forums";
   this.menuItemForums.Click += new System.EventHandler(this.menuItemForums_Click);
   this.menuItemChatRoom.Index = 6;
   this.menuItemChatRoom.Text = "#worldwind &IRC Chat";
   this.menuItemChatRoom.Click += new System.EventHandler(this.menuItemChatRoom_Click);
   this.menuItemSpacer10.Index = 7;
   this.menuItemSpacer10.Text = "-";
   this.menuItemWebsite.Index = 8;
   this.menuItemWebsite.Text = "&NASA World Wind Website";
   this.menuItemWebsite.Click += new System.EventHandler(this.menuItemWebsite_Click);
   this.menuItemHotspots.Index = 9;
   this.menuItemHotspots.Text = "&Hotspot Finder";
   this.menuItemHotspots.Click += new System.EventHandler(this.menuItemHotspots_Click);
   this.menuItemSeparator3.Index = 10;
   this.menuItemSeparator3.Text = "-";
   this.menuItemAbout.Index = 11;
   this.menuItemAbout.Text = "&About World Wind";
   this.menuItemAbout.Click += new System.EventHandler(this.menuItemAbout_Click);
   this.toolBarButtonAddons.DropDownMenu = this.contextMenuAddons;
   this.toolBarButtonAddons.ImageIndex = 0;
   this.toolBarButtonAddons.Style = System.Windows.Forms.ToolBarButtonStyle.DropDownButton;
   this.toolBarButtonAddons.Text = "Add-Ons";
   this.toolBarButtonAddons.ToolTipText = "Show Add-Ons";
   this.toolBarButtonLayerManager.ImageIndex = 3;
   this.toolBarButtonLayerManager.ToolTipText = "Show Layer Manager";
   this.toolBarButtonWMS.ImageIndex = 4;
   this.toolBarButtonWMS.ToolTipText = "Show WMS Browser";
   this.toolBarButtonAnimatedEarth.ImageIndex = 5;
   this.toolBarButtonAnimatedEarth.ToolTipText = "Show NASA Scientific Visualization Studio";
   this.toolBarButtonRapidFireModis.ImageIndex = 6;
   this.toolBarButtonRapidFireModis.ToolTipText = "Show Rapid Fire MODIS";
   this.imageListFunctions.ColorDepth = System.Windows.Forms.ColorDepth.Depth32Bit;
   this.imageListFunctions.ImageSize = new System.Drawing.Size(48, 32);
   this.imageListFunctions.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageListFunctions.ImageStream")));
   this.imageListFunctions.TransparentColor = System.Drawing.Color.Transparent;
   this.toolBarButtonKeyChart.ImageIndex = 7;
   this.toolBarButtonKeyChart.ToolTipText = "Show Key Chart";
   this.toolBarButtonWebsite.ImageIndex = 8;
   this.toolBarButtonWebsite.ToolTipText = "Show World Wind Website";
   this.toolBarButtonSearch.ImageIndex = 0;
   this.toolBarButtonSearch.Style = System.Windows.Forms.ToolBarButtonStyle.ToggleButton;
   this.toolBarButtonSearch.ToolTipText = "Search For a Place";
   this.toolBarButtonPosition.ImageIndex = 1;
   this.toolBarButtonPosition.Style = System.Windows.Forms.ToolBarButtonStyle.ToggleButton;
   this.toolBarButtonPosition.ToolTipText = "Show Current Position";
   this.toolBarButtonLatLonLines.ImageIndex = 2;
   this.toolBarButtonLatLonLines.Style = System.Windows.Forms.ToolBarButtonStyle.ToggleButton;
   this.toolBarButtonLatLonLines.ToolTipText = "Show Latitude/Longitude Lines";
            this.browserSplitterHorz.BackColor = System.Drawing.Color.Gray;
            this.browserSplitterHorz.Dock = System.Windows.Forms.DockStyle.Top;
            this.browserSplitterHorz.Location = new System.Drawing.Point(0, 441);
            this.browserSplitterHorz.Name = "browserSplitterHorz";
            this.browserSplitterHorz.Size = new System.Drawing.Size(992, 3);
            this.browserSplitterHorz.TabIndex = 6;
            this.browserSplitterHorz.TabStop = false;
            this.browserSplitterVert.BackColor = System.Drawing.Color.Gray;
            this.browserSplitterVert.Dock = System.Windows.Forms.DockStyle.Right;
            this.browserSplitterVert.Location = new System.Drawing.Point(989, 444);
            this.browserSplitterVert.Name = "browserSplitterVert";
            this.browserSplitterVert.Size = new System.Drawing.Size(3, 229);
            this.browserSplitterVert.TabIndex = 8;
            this.browserSplitterVert.TabStop = false;
            this.browserSplitterVert.Visible = false;
            this.webBrowser.BeginInit();
            this.webBrowser.Dock = System.Windows.Forms.DockStyle.Fill;
            this.webBrowser.Location = new System.Drawing.Point(0, 26);
            this.webBrowser.Size = new System.Drawing.Size(989, 161);
            this.webBrowser.TabIndex = 3;
            this.webBrowser.BeforeNavigate2 += new DWebBrowserEvents2_BeforeNavigate2EventHandler(webBrowser_BeforeNavigate2);
            this.webBrowser.EndInit();
            this.menuItemBrowserVisible.Index = 1;
            this.menuItemBrowserVisible.Text = "Web Browser";
            this.menuItemBrowserVisible.Click += new System.EventHandler(this.webBrowserVisible_Click);
            this.webBrowserPanel.Controls.Add(this.webBrowserButtonsPanel);
            this.webBrowserPanel.Controls.Add(this.webBrowser);
            this.webBrowserPanel.Dock = DockStyle.Fill;
            this.webBrowserPanel.Location = new System.Drawing.Point(0, 137);
            this.webBrowserPanel.Name = "webBrowserPanel";
            this.webBrowserPanel.Size = new System.Drawing.Size(420, 209);
            this.webBrowserPanel.TabIndex = 0;
            this.webBrowserButtonsPanel.BackColor = System.Drawing.Color.LightGray;
            this.webBrowserButtonsPanel.Controls.Add(this.webBrowserGo);
            this.webBrowserButtonsPanel.Controls.Add(this.webBrowserURL);
            this.webBrowserButtonsPanel.Controls.Add(this.webBrowserStop);
            this.webBrowserButtonsPanel.Controls.Add(this.webBrowserForward);
            this.webBrowserButtonsPanel.Controls.Add(this.webBrowserBack);
            this.webBrowserButtonsPanel.Dock = System.Windows.Forms.DockStyle.Top;
            this.webBrowserButtonsPanel.Location = new System.Drawing.Point(0, 0);
            this.webBrowserButtonsPanel.Name = "webBrowserButtonsPanel";
            this.webBrowserButtonsPanel.Size = new System.Drawing.Size(420, 32);
            this.webBrowserButtonsPanel.TabIndex = 0;
            this.webBrowserBack.BackColor = System.Drawing.Color.DodgerBlue;
            this.webBrowserBack.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.webBrowserBack.Location = new System.Drawing.Point(0, 3);
            this.webBrowserBack.Name = "webBrowserBack";
            this.webBrowserBack.Size = new System.Drawing.Size(46, 22);
            this.webBrowserBack.TabIndex = 4;
            this.webBrowserBack.Text = "Back";
            this.webBrowserBack.Click += new EventHandler(webBrowserBack_Click);
            this.webBrowserForward.BackColor = System.Drawing.Color.DodgerBlue;
            this.webBrowserForward.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.webBrowserForward.Location = new System.Drawing.Point(50, 3);
            this.webBrowserForward.Name = "webBrowserForward";
            this.webBrowserForward.Size = new System.Drawing.Size(59, 22);
            this.webBrowserForward.TabIndex = 5;
            this.webBrowserForward.Text = "Forward";
            this.webBrowserForward.Click += new EventHandler(webBrowserForward_Click);
            this.webBrowserStop.BackColor = System.Drawing.Color.DodgerBlue;
            this.webBrowserStop.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.webBrowserStop.Location = new System.Drawing.Point(115, 3);
            this.webBrowserStop.Name = "webBrowserStop";
            this.webBrowserStop.Size = new System.Drawing.Size(43, 22);
            this.webBrowserStop.TabIndex = 6;
            this.webBrowserStop.Text = "Stop";
            this.webBrowserStop.Click += new EventHandler(webBrowserStop_Click);
            this.webBrowserGo.BackColor = System.Drawing.Color.DodgerBlue;
            this.webBrowserGo.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.webBrowserGo.Location = new System.Drawing.Point(354, 3);
            this.webBrowserGo.Name = "webBrowserGo";
            this.webBrowserGo.Size = new System.Drawing.Size(35, 22);
            this.webBrowserGo.TabIndex = 7;
            this.webBrowserGo.Text = "Go";
            this.webBrowserGo.Click += new EventHandler(webBrowserGo_Click);
            this.webBrowserURL.Location = new System.Drawing.Point(170, 3);
            this.webBrowserURL.Name = "webBrowserURL";
            this.webBrowserURL.Size = new System.Drawing.Size(170, 20);
            this.webBrowserURL.TabIndex = 3;
            this.webBrowserURL.KeyDown += new KeyEventHandler(webBrowserURL_KeyDown);
            this.Controls.Add(this.webBrowserPanel);
            this.webBrowserPanel.ResumeLayout(false);
            this.webBrowserButtonsPanel.ResumeLayout(false);
            this.webBrowserButtonsPanel.PerformLayout();
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.ClientSize = new System.Drawing.Size(992, 673);
   this.Controls.Add(this.worldWindow);
            this.Controls.Add(browserSplitterHorz);
            this.Controls.Add(browserSplitterVert);
   this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
   this.IsMdiContainer = true;
   this.KeyPreview = true;
   this.Menu = this.mainMenu;
   this.MinimumSize = new System.Drawing.Size(200, 200);
   this.Name = "MainApplication";
   this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
   this.Text = "NASA World Wind";
   this.ResumeLayout(false);
  }
  private void InitializePluginCompiler()
  {
   this.splashScreen.SetText("Initializing plugins...");
   string pluginRoot = Path.Combine(DirectoryPath, "Plugins");
   compiler = new PluginCompiler(this, pluginRoot);
   compiler.FindPlugins(Assembly.GetExecutingAssembly());
   compiler.FindPlugins();
   compiler.LoadStartupPlugins();
  }
  private void OnWorldChange(object sender, System.EventArgs e)
  {
   System.Windows.Forms.MenuItem curMenuItem = (System.Windows.Forms.MenuItem)sender;
   string curWorld = availableWorldList[curMenuItem.Text] as string;
   if(curWorld != null)
   {
    OpenWorld(curWorld);
   }
  }
  private void AddInternalPluginMenuButtons()
  {
   if(this.worldWindow.CurrentWorld.IsEarth)
   {
    this.rapidFireModisManager = new RapidFireModisManager(this.worldWindow);
    this.rapidFireModisManager.Icon = this.Icon;
    this.worldWindow.MenuBar.AddToolsMenuButton( new WindowsControlMenuButton( "Rapid Fire MODIS", DirectoryPath + "\\Data\\Icons\\Interface\\modis.png", this.rapidFireModisManager) );
   }
   this.wmsBrowser = new WMSBrowser(this.worldWindow);
   this.wmsBrowser.Icon = this.Icon;
   this.worldWindow.MenuBar.AddToolsMenuButton( new WindowsControlMenuButton( "WMS Browser", DirectoryPath + "\\Data\\Icons\\Interface\\wms.png", this.wmsBrowser ));
   if(this.worldWindow.CurrentWorld.IsEarth)
   {
    this.animatedEarthMananger = new AnimatedEarthManager(this.worldWindow);
    this.animatedEarthMananger.Icon = this.Icon;
    this.worldWindow.MenuBar.AddToolsMenuButton( new WindowsControlMenuButton( "Scientific Visualization Studio", DirectoryPath + "\\Data\\Icons\\Interface\\svs2.png", this.animatedEarthMananger));
   }
  }
  private void menuItemShowPosition_Click(object sender, System.EventArgs e)
  {
   World.Settings.ShowPosition = !World.Settings.ShowPosition;
   this.toolBarButtonPosition.Pushed = World.Settings.ShowPosition;
   this.menuItemShowPosition.Checked = World.Settings.ShowPosition;
   this.worldWindow.Invalidate();
  }
  private void menuItemShowCrosshairs_Click(object sender, System.EventArgs e)
  {
   World.Settings.ShowCrosshairs = !World.Settings.ShowCrosshairs;
   this.menuItemShowCrosshairs.Checked = World.Settings.ShowCrosshairs;
   this.worldWindow.Invalidate();
  }
  private void menuItemConstantMotion_Click(object sender, System.EventArgs e)
  {
   World.Settings.CameraHasMomentum = !World.Settings.CameraHasMomentum;
   this.menuItemConstantMotion.Checked = World.Settings.CameraHasMomentum;
  }
  private void menuItemPointGoTo_Click(object sender, System.EventArgs e)
  {
   this.worldWindow.DrawArgs.WorldCamera.IsPointGoto = !this.worldWindow.DrawArgs.WorldCamera.IsPointGoto;
   this.menuItemPointGoTo.Checked = this.worldWindow.DrawArgs.WorldCamera.IsPointGoto;
  }
  private void menuItemInertia_Click(object sender, System.EventArgs e)
  {
   World.Settings.CameraHasInertia = !World.Settings.CameraHasInertia;
   this.menuItemInertia.Checked = World.Settings.CameraHasInertia;
  }
  private void menuItemShowLatLonLines_Click(object sender, System.EventArgs e)
  {
   SetLatLonGridShow( !World.Settings.ShowLatLonLines );
  }
  private void menuItemVerticalExaggerationChange(object sender, System.EventArgs e)
  {
   MenuItem multiplier = (MenuItem)sender;
   this.VerticalExaggeration = Single.Parse(multiplier.Text.Replace("x", ""), CultureInfo.CurrentCulture);
  }
  private void menuItemPlanetAxis_Click(object sender, System.EventArgs e)
  {
   World.Settings.ShowPlanetAxis = !World.Settings.ShowPlanetAxis;
   this.menuItemPlanetAxis.Checked = World.Settings.ShowPlanetAxis;
  }
  private void menuItemCoordsToClipboard_Click(object sender, System.EventArgs e)
  {
   if (this.worldWindow.CurrentWorld == null)
    return;
   WorldWindUri uri = new WorldWindUri(worldWindow.CurrentWorld.Name, worldWindow.DrawArgs.WorldCamera);
   string uriString = uri.ToString();
   Clipboard.SetDataObject(uriString, true);
  }
  private void menuItemQuit_Click(object sender, System.EventArgs e)
  {
   Close();
  }
  private void menuItemLayerManager_Click(object sender, System.EventArgs e)
  {
   World.Settings.ShowLayerManager = !World.Settings.ShowLayerManager;
   this.worldWindow.ShowLayerManager = World.Settings.ShowLayerManager;
   this.menuItemLayerManager.Checked = World.Settings.ShowLayerManager;
  }
  private void menuItemModisHotSpots_Click(object sender, System.EventArgs e)
  {
   if (this.animatedEarthMananger != null && this.animatedEarthMananger.Visible)
   {
    this.menuItemAnimatedEarth.Checked = false;
    this.animatedEarthMananger.Reset();
    this.animatedEarthMananger.Visible = false;
   }
   if (this.wmsBrowser != null && this.wmsBrowser.Visible)
   {
    this.menuItemWMS.Checked = false;
    this.wmsBrowser.Reset();
    this.wmsBrowser.Visible = false;
   }
   if (this.rapidFireModisManager == null)
   {
    this.rapidFireModisManager = new RapidFireModisManager(this.worldWindow);
    this.rapidFireModisManager.Icon = this.Icon;
   }
   menuItemModisHotSpots.Checked = !menuItemModisHotSpots.Checked;
   this.rapidFireModisManager.Visible = this.menuItemModisHotSpots.Checked;
   this.rapidFireModisManager.WindowState = FormWindowState.Normal;
   this.toolBarButtonRapidFireModis.Pushed = true;
   UpdateToolBarStates();
  }
  private void menuItemAnimatedEarth_Click(object sender, System.EventArgs e)
  {
   if (this.rapidFireModisManager != null && this.rapidFireModisManager.Visible)
   {
    this.menuItemModisHotSpots.Checked = false;
    this.rapidFireModisManager.Reset();
    this.rapidFireModisManager.Visible = false;
   }
   if (this.wmsBrowser != null && this.wmsBrowser.Visible)
   {
    this.menuItemWMS.Checked = false;
    this.wmsBrowser.Reset();
    this.wmsBrowser.Visible = false;
   }
   if (this.animatedEarthMananger == null)
   {
    this.animatedEarthMananger = new AnimatedEarthManager(this.worldWindow);
    this.animatedEarthMananger.Icon = this.Icon;
   }
   menuItemAnimatedEarth.Checked = !menuItemAnimatedEarth.Checked;
   this.animatedEarthMananger.Visible = this.menuItemAnimatedEarth.Checked;
   this.animatedEarthMananger.WindowState = FormWindowState.Normal;
   UpdateToolBarStates();
  }
  private void menuItemWMS_Click(object sender, System.EventArgs e)
  {
   if(this.rapidFireModisManager != null && this.rapidFireModisManager.Visible)
   {
    this.menuItemModisHotSpots.Checked = false;
    this.rapidFireModisManager.Reset();
    this.rapidFireModisManager.Visible = false;
   }
   if(this.animatedEarthMananger != null && this.animatedEarthMananger.Visible)
   {
    this.menuItemAnimatedEarth.Checked = false;
    this.animatedEarthMananger.Reset();
    this.animatedEarthMananger.Visible = false;
   }
   if(this.wmsBrowser == null)
   {
    this.wmsBrowser = new WMSBrowser(this.worldWindow);
    this.wmsBrowser.Icon = this.Icon;
   }
   menuItemWMS.Checked = !menuItemWMS.Checked;
   this.wmsBrowser.Visible = menuItemWMS.Checked;
   this.wmsBrowser.WindowState = FormWindowState.Normal;
   UpdateToolBarStates();
  }
  private void menuItemWebsite_Click(object sender, System.EventArgs e)
  {
   BrowseTo( WebsiteUrl );
  }
  private void menuItemAbout_Click(object sender, System.EventArgs e)
  {
   if(this.aboutDialog == null)
   {
    this.aboutDialog = new AboutDialog(this.worldWindow);
    this.aboutDialog.Owner = this;
    this.aboutDialog.Icon = this.Icon;
   }
   this.aboutDialog.WindowState = FormWindowState.Normal;
   this.aboutDialog.ShowDialog();
  }
  private void menuItemKeyChart_Click(object sender, System.EventArgs e)
  {
   string keyChartPath = Path.Combine(DirectoryPath, "keychart.html");
   if (File.Exists( keyChartPath ))
    BrowseTo( keyChartPath );
   else
    BrowseTo("http://worldwind.arc.nasa.gov/graphics/keychart.jpg");
  }
  private void menuItemWalkthrough_Click(object sender, System.EventArgs e)
  {
   string TourPath = Path.Combine(MainApplication.DirectoryPath, @"Data\Documentation\WW_Tour.exe");
   if (File.Exists(TourPath))
    System.Diagnostics.Process.Start(TourPath);
   else
    System.Diagnostics.Process.Start("http://www.alteviltech.com/WorldWind/Tour/");
  }
  private void menuItemSaveScreenShot_Click(object sender, System.EventArgs e)
  {
   SaveFileDialog dlg = new SaveFileDialog();
   dlg.Title = "Save screenshot as";
   dlg.RestoreDirectory = true;
   dlg.Filter = "Portable Network Graphics (*.png)|*.png|JPEG (*.jpg)|*.jpg|Windows Bitmap (*.bmp)|*.bmp|Targa (*.tga)|*.tga";
   dlg.FileName = string.Format( CultureInfo.InvariantCulture,
    "{0:f5}{1}_{2:f5}{3}",
    Math.Abs(worldWindow.DrawArgs.WorldCamera.Longitude.Degrees),
    worldWindow.DrawArgs.WorldCamera.Longitude > Angle.Zero ? "E" : "W",
    Math.Abs( worldWindow.DrawArgs.WorldCamera.Latitude.Degrees ),
    worldWindow.DrawArgs.WorldCamera.Latitude > Angle.Zero ? "N" : "S" );
   if (dlg.ShowDialog() != DialogResult.OK)
    return;
   string ext = "*" + Path.GetExtension(dlg.FileName).ToLower(CultureInfo.InvariantCulture);
   if(dlg.Filter.IndexOf(ext)<0)
   {
    ext = dlg.Filter.Split('|')[ dlg.FilterIndex * 2 - 1 ].Substring(1);
    dlg.FileName += ext;
   }
   this.worldWindow.SaveScreenshot(dlg.FileName);
  }
  private void menuItemShowToolbar_Click(object sender, System.EventArgs e)
  {
   World.Settings.ShowToolbar = !World.Settings.ShowToolbar;
   this.menuItemShowToolbar.Checked = World.Settings.ShowToolbar;
  }
  private void toolBarFunctions_ButtonClick(object sender, System.Windows.Forms.ToolBarButtonClickEventArgs e)
  {
   if(this.worldWindow == null || this.worldWindow.CurrentWorld == null)
    return;
   if(e.Button == this.toolBarButtonPosition)
   {
    menuItemShowPosition_Click(sender,e);
   }
   else if(e.Button == this.toolBarButtonLatLonLines)
   {
    menuItemShowLatLonLines_Click(sender,e);
   }
  }
  private void toolBarCustomFeatures_ButtonClick(object sender, System.Windows.Forms.ToolBarButtonClickEventArgs e)
  {
   if(this.worldWindow == null || this.worldWindow.CurrentWorld == null)
    return;
   if(e.Button == this.toolBarButtonAnimatedEarth)
   {
    menuItemAnimatedEarth_Click(this,e);
   }
   else if(e.Button == this.toolBarButtonLayerManager)
   {
    menuItemLayerManager_Click(this,e);
   }
   else if(e.Button == this.toolBarButtonRapidFireModis)
   {
    menuItemModisHotSpots_Click(sender,e);
   }
   else if(e.Button == this.toolBarButtonWMS)
   {
    menuItemWMS_Click(sender,e);
   }
  }
  private void toolBarHelp_ButtonClick(object sender, System.Windows.Forms.ToolBarButtonClickEventArgs e)
  {
   if(e.Button == this.toolBarButtonWebsite)
   {
    BrowseTo("http://worldwind.arc.nasa.gov/");
   }
   else if(e.Button == this.toolBarButtonKeyChart)
   {
    menuItemKeyChart_Click(sender,e);
   }
  }
  private void menuItemForums_Click(object sender, System.EventArgs e)
  {
   BrowseTo("http://forum.worldwindcentral.com/");
  }
  private void menuItemChatRoom_Click(object sender, System.EventArgs e)
  {
   BrowseTo("http://chat.worldwindcentral.com");
  }
  private void menuItemHotspots_Click(object sender, System.EventArgs e)
  {
   BrowseTo("http://www.worldwindcentral.com/hotspots/");
  }
  private void menuItemFaq_Click(object sender, System.EventArgs e)
  {
   BrowseTo("http://wiki.worldwindcentral.com/World_Wind_FAQ");
  }
  private void menuItemWiki_Click(object sender, System.EventArgs e)
  {
   BrowseTo("http://wiki.worldwindcentral.com");
  }
  private void menuItemAlwaysOnTop_Click(object sender, System.EventArgs e)
  {
   this.menuItemAlwaysOnTop.Checked = !this.menuItemAlwaysOnTop.Checked;
   this.TopMost = this.menuItemAlwaysOnTop.Checked;
  }
  private void menuItemFullScreen_Click(object sender, System.EventArgs e)
  {
   this.FullScreen = !this.FullScreen;
  }
  private void menuItemLockPlanetAxis_Click(object sender, System.EventArgs e)
  {
   World.Settings.CameraTwistLock = ! World.Settings.CameraTwistLock;
   menuItemLockPlanetAxis.Checked = World.Settings.CameraTwistLock;
   if(World.Settings.CameraTwistLock)
   {
    worldWindow.DrawArgs.WorldCamera.SetPosition(double.NaN, double.NaN, 0, double.NaN, double.NaN, 0);
   }
  }
  private void menuItemCameraBanking_Click(object sender, System.EventArgs e)
  {
   World.Settings.CameraBankLock = ! World.Settings.CameraBankLock;
   menuItemCameraBanks.Checked = World.Settings.CameraBankLock;
   if(!World.Settings.CameraBankLock)
   {
    worldWindow.DrawArgs.WorldCamera.SetPosition(double.NaN, double.NaN, double.NaN, double.NaN, double.NaN, 0);
   }
  }
  private void menuItemReset_Click(object sender, System.EventArgs e)
  {
   worldWindow.DrawArgs.WorldCamera.Reset();
  }
  private void menuItemRefreshCurrentView_Click(object sender, System.EventArgs e)
  {
   DialogResult result = System.Windows.Forms.MessageBox.Show(this,
    "Warning: This will delete the section of cached data for this area.",
    "WARNING",
    MessageBoxButtons.OKCancel);
   if(result == DialogResult.OK)
   {
    foreach(RenderableObject ro in worldWindow.CurrentWorld.RenderableObjects.ChildObjects)
    {
     if(ro.IsOn)
      resetQuadTileSetCache(ro);
    }
   }
  }
  private void menuItemOptions_Click(object sender, System.EventArgs e)
  {
   using( PropertyBrowserForm worldSettings = new PropertyBrowserForm( World.Settings, "World Settings" ) )
   {
    worldSettings.Icon = this.Icon;
    worldSettings.ShowDialog();
   }
  }
  private void menuItemConfigWizard_Click(object sender, System.EventArgs e)
  {
   ConfigurationWizard.Wizard wizard = new ConfigurationWizard.Wizard( Settings );
   wizard.ShowDialog();
  }
  void menuItemEditPaste_Click(object sender, System.EventArgs e)
  {
   IDataObject iData = Clipboard.GetDataObject();
   if(!iData.GetDataPresent(DataFormats.Text))
    return;
   string clipBoardString = (string)iData.GetData(DataFormats.Text);
   try
   {
    worldWindUri = WorldWindUri.Parse(clipBoardString);
    ProcessWorldWindUri();
   }
   catch(UriFormatException caught)
   {
    MessageBox.Show(caught.Message, "Unable to paste", MessageBoxButtons.OK, MessageBoxIcon.Warning );
   }
  }
  private void menuItemPluginManager_Click(object sender, System.EventArgs e)
  {
   if(pluginManager!=null)
   {
    pluginManager.Dispose();
    pluginManager = null;
   }
   pluginManager = new PluginDialog(compiler);
   pluginManager.Icon = this.Icon;
   pluginManager.Show();
  }
        private void menuItemBrowserHoriz_Click(object sender, EventArgs e)
        {
            if (menuItemBrowserHoriz.Checked == true)
            {
                menuItemShowWebBrowser.Enabled = false;
                menuItemBrowserHoriz.Checked = false;
                menuItemBrowserVisible.Checked = false;
                this.webBrowserPanel.Visible = false;
                this.browserSplitterHorz.Visible = false;
                this.WorldWindow.Dock = DockStyle.Fill;
            }else {
                menuItemBrowserHoriz.Checked = true;
                menuItemBrowserVertical.Checked = false;
                this.webBrowserPanel.Visible = true;
                this.webBrowserPanel.Size = new Size(992, 232);
                this.browserSplitterHorz.Visible = true;
                this.browserSplitterHorz.Location = new Point(0, 444);
                this.browserSplitterHorz.BringToFront();
                this.browserSplitterVert.Visible = false;
                this.BrowseTo(MainApplication.WebsiteUrl);
                this.webBrowser.BringToFront();
                this.WorldWindow.Dock = DockStyle.Top;
            }
        }
        private void menuItemBrowserVertical_Click(object sender, EventArgs e)
        {
            if (menuItemBrowserVertical.Checked == true)
            {
                menuItemShowWebBrowser.Enabled = false;
                menuItemBrowserVertical.Checked = false;
                menuItemBrowserVisible.Checked = false;
                this.webBrowserPanel.Visible = false;
                this.browserSplitterVert.Visible = false;
                this.WorldWindow.Dock = DockStyle.Fill;
            }else{
                menuItemBrowserVertical.Checked = true;
                menuItemBrowserHoriz.Checked = false;
                this.WorldWindow.Dock = DockStyle.Right;
                this.webBrowserPanel.Visible = true;
                this.webBrowserPanel.Size = new Size(232, 992);
                this.webBrowserPanel.Location = new Point(0, 0);
                this.browserSplitterVert.Visible = true;
                this.browserSplitterVert.Location = new Point(0, 200);
                this.browserSplitterVert.BringToFront();
                this.browserSplitterHorz.Visible = false;
                this.BrowseTo(MainApplication.WebsiteUrl);
                this.webBrowser.BringToFront();
            }
        }
        private void webBrowserVisible_Click(object sender, EventArgs e)
        {
            if (menuItemBrowserVisible.Checked == true)
            {
                menuItemBrowserVisible.Checked = false;
                menuItemShowWebBrowser.Enabled = false;
                menuItemBrowserHoriz.Checked = false;
                menuItemBrowserVertical.Checked = false;
                this.webBrowserPanel.Visible = false;
                this.browserSplitterHorz.Visible = false;
                this.WorldWindow.Dock = DockStyle.Fill;
            }
            else
            {
                menuItemBrowserVisible.Checked = true;
                menuItemShowWebBrowser.Enabled = true;
                menuItemBrowserHoriz.Checked = true;
                menuItemBrowserVertical.Checked = false;
                this.webBrowserPanel.Visible = true;
                this.webBrowser.Visible = true;
                this.browserSplitterHorz.Visible = true;
                this.browserSplitterVert.Visible = false;
                this.WorldWindow.Dock = DockStyle.Top;
                this.webBrowser.BringToFront();
                this.browserSplitterHorz.BringToFront();
                this.BrowseTo(MainApplication.WebsiteUrl);
            }
        }
        void webBrowserGo_Click(object sender, EventArgs e)
        {
            try
            {
                this.BrowseTo(this.webBrowserURL.Text);
            }
            catch (Exception ex) { }
        }
        void webBrowserStop_Click(object sender, EventArgs e)
        {
            try
            {
                this.webBrowser.Stop();
            }
            catch (Exception ex) { }
        }
        void webBrowserForward_Click(object sender, EventArgs e)
        {
            try
            {
                this.webBrowser.GoForward();
            }
            catch (Exception ex) { }
        }
        void webBrowserBack_Click(object sender, EventArgs e)
        {
            try
            {
                this.webBrowser.GoBack();
            }
            catch (Exception ex) { }
        }
        void webBrowserURL_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
                this.BrowseTo(webBrowserURL.Text);
        }
  public void SetVerticalExaggeration(double exageration)
  {
   World.Settings.VerticalExaggeration = (float)exageration;
  }
  public void SetDisplayMessages(System.Collections.IList messages)
  {
   this.worldWindow.SetDisplayMessages(messages);
  }
  public void SetLayers(System.Collections.IList layers)
  {
   this.worldWindow.SetLayers(layers);
  }
  public void SetWmsImage(WmsDescriptor imageA,
   WmsDescriptor imageB, double alpha)
  {
   this.SetWmsImage(imageA, imageB, alpha);
  }
  public void SetViewDirection(string type, double horiz, double vert, double elev)
  {
   this.worldWindow.SetViewDirection(type, horiz, vert, elev);
  }
  public void SetViewPosition(double degreesLatitude, double degreesLongitude,
   double metersElevation)
  {
   this.worldWindow.SetViewPosition(degreesLatitude, degreesLongitude,
    metersElevation);
  }
  public void SetLatLonGridShow(bool show)
  {
   World.Settings.ShowLatLonLines = show;
   if (this.worldWindow!=null)
   {
    this.toolBarButtonLatLonLines.Pushed = World.Settings.ShowLatLonLines;
    this.menuItemShowLatLonLines.Checked = World.Settings.ShowLatLonLines;
    this.worldWindow.Invalidate();
   }
  }
  private Timeline.ScriptPlayer currentScriptPlayer;
  private void menuItemPlayScript_Click(object sender, System.EventArgs e)
  {
   OpenFileDialog dialog = new OpenFileDialog();
   dialog.Filter = "XML Script Files (*.xml)|*.xml";
   dialog.CheckFileExists = true;
   dialog.InitialDirectory = Path.Combine(DirectoryPath, "Scripts");
   if (dialog.ShowDialog() == DialogResult.OK)
   {
    String scriptPath = dialog.FileName;
    this.currentScriptPlayer =
     new Timeline.ScriptPlayer(scriptPath, worldWindow);
    this.currentScriptPlayer.StatusChanged +=
     new Timeline.ScriptPlayer.StatusChangeHandler(
     this.scriptPlayerStatusChange);
    this.currentScriptPlayer.Start();
   }
  }
  private void menuItemStopScript_Click(object sender, System.EventArgs e)
  {
   if (this.currentScriptPlayer != null)
    this.currentScriptPlayer.Stop();
  }
  private void scriptPlayerStatusChange(Timeline.ScriptPlayer player,
   Timeline.ScriptPlayer.StatusChange change)
  {
   switch (change)
   {
    case Timeline.ScriptPlayer.StatusChange.ScriptStarted:
     this.menuItemPlayScript.Enabled = false;
     this.menuItemStopScript.Enabled = true;
     break;
    case Timeline.ScriptPlayer.StatusChange.ScriptStopped:
    case Timeline.ScriptPlayer.StatusChange.ScriptEnded:
     this.menuItemPlayScript.Enabled = true;
     this.menuItemStopScript.Enabled = false;
     this.restorePreScriptState();
     break;
   }
  }
  private void restorePreScriptState()
  {
   this.SetDisplayMessages(null);
  }
  public static IntPtr GetWWHandle()
  {
   return NativeMethods.FindWindow(null, "NASA World Wind");
  }
  public static string[] StringFromCopyData( Message m )
  {
   NativeMethods.CopyDataStruct cds = (NativeMethods.CopyDataStruct)
    m.GetLParam(typeof(NativeMethods.CopyDataStruct));
   string[] args = Marshal.PtrToStringAuto(cds.lpData).Split('\n');
   return args;
  }
  [SecurityPermission(SecurityAction.LinkDemand, UnmanagedCode=true), SecurityPermission(SecurityAction.InheritanceDemand, UnmanagedCode=true)]
  protected override void WndProc(ref Message m)
  {
   switch (m.Msg)
   {
    case NativeMethods.WM_ACTIVATEAPP:
     if(worldWindow!=null)
      worldWindow.IsRenderDisabled = m.WParam.ToInt32() == 0;
     break;
    case NativeMethods.WM_COPYDATA:
     string[] args = StringFromCopyData(m);
     ParseArgs(args);
     if(startFullScreen && !FullScreen)
     {
      FullScreen = true;
     }
     if (worldWindUri != null)
     {
      ProcessWorldWindUri();
      this.Activate();
     }
     m.Result = (IntPtr)1;
     break;
   }
   base.WndProc(ref m);
  }
  private void ProcessWorldWindUri()
  {
   if(worldWindUri.RawUrl.IndexOf("wmsimage")>=0)
    ProcessWmsEncodedUri();
   worldWindow.Goto( worldWindUri );
   worldWindUri = null;
  }
  private void ProcessWmsEncodedUri()
  {
   string uri = worldWindUri.RawUrl.Substring(12, worldWindUri.RawUrl.Length - 12);
   uri = uri.Replace("wmsimage=", "wmsimage|");
   uri = uri.Replace("&wmsimage", "#wmsimage");
   string[] uriFunctions = uri.Split('#');
   foreach(string uriFunction in uriFunctions)
   {
    string[] paramValuePair = uriFunction.Split('|');
    if(String.Compare(paramValuePair[0], "wmsimage", true, CultureInfo.InvariantCulture) == 0)
    {
     string displayName = null;
     int transparencyPercent = 0;
     double heightAboveSurface = 0.0;
     string wmslink = "";
     string[] wmsImageParams = new string[0];
     if(paramValuePair[1].IndexOf("://") > 0)
     {
      wmsImageParams = paramValuePair[1].Replace("%26", "|").Split('|');
     }
     else
     {
      wmsImageParams = System.Web.HttpUtility.UrlDecode(paramValuePair[1]).Replace("%26", "|").Split('|');
     }
     foreach(string p in wmsImageParams)
     {
      string new_p = p.Replace("%3d", "|");
      char[] deliminator = new char[1] {'|'};
      string[] functionParam = new_p.Split(deliminator, 2);
      if(String.Compare(functionParam[0], "displayname", true, CultureInfo.InvariantCulture) == 0)
      {
       displayName = functionParam[1];
      }
      else if(String.Compare(functionParam[0], "transparency", true, CultureInfo.InvariantCulture) == 0)
      {
       transparencyPercent = Int32.Parse(functionParam[1], CultureInfo.InvariantCulture);
      }
      else if(String.Compare(functionParam[0], "altitude", true, CultureInfo.InvariantCulture) == 0)
      {
       heightAboveSurface = Double.Parse(functionParam[1], CultureInfo.InvariantCulture);
      }
      else if(String.Compare(functionParam[0], "link", true, CultureInfo.InvariantCulture) == 0)
      {
       wmslink = functionParam[1];
       if(wmslink.EndsWith("/"))
        wmslink = wmslink.Substring(0, wmslink.Length - 1);
      }
     }
     try
     {
      string[] wmslinkParams = wmslink.Split('?')[1].Split('&');
      string wmsLayerName = null;
      LayerSet.Type_LatitudeCoordinate2 bb_north = new LayerSet.Type_LatitudeCoordinate2();
      LayerSet.Type_LatitudeCoordinate2 bb_south = new LayerSet.Type_LatitudeCoordinate2();
      LayerSet.Type_LongitudeCoordinate2 bb_west = new LayerSet.Type_LongitudeCoordinate2();
      LayerSet.Type_LongitudeCoordinate2 bb_east = new LayerSet.Type_LongitudeCoordinate2();
      foreach(string wmslinkParam in wmslinkParams)
      {
       string linkParamUpper = wmslinkParam.ToUpper(CultureInfo.InvariantCulture);
       if(linkParamUpper.IndexOf("BBOX") >= 0)
       {
        string[] bb_parts = wmslinkParam.Split('=')[1].Split(',');
        bb_west.AddValue2(new LayerSet.ValueType4(bb_parts[0]));
        bb_south.AddValue2(new LayerSet.ValueType3(bb_parts[1]));
        bb_east.AddValue2(new LayerSet.ValueType4(bb_parts[2]));
        bb_north.AddValue2(new LayerSet.ValueType3(bb_parts[3]));
       }
       else if(linkParamUpper.IndexOf("LAYERS") >= 0)
       {
        wmsLayerName = wmslinkParam.Split('=')[1];
       }
      }
      string path = String.Format(CultureInfo.InvariantCulture,
       @"{0}\{1}\___DownloadedWMSImages.xml", Settings.ConfigPath, "");
      string texturePath = string.Format(CultureInfo.InvariantCulture,
       @"{0}\Data\DownloadedWMSImages\{1}", DirectoryPath, System.DateTime.Now.ToFileTimeUtc());
      if(!File.Exists(path))
      {
       LayerSet.LayerSetDoc newDoc = new LayerSet.LayerSetDoc();
       LayerSet.Type_LayerSet root = new LayerSet.Type_LayerSet();
       root.AddName(new LayerSet.NameType2("Downloaded WMS Images"));
       root.AddShowAtStartup(new Altova.Types.SchemaBoolean(true));
       root.AddShowOnlyOneLayer(new Altova.Types.SchemaBoolean(false));
       newDoc.SetRootElementName("", "LayerSet");
       newDoc.Save(path, root);
      }
      LayerSet.LayerSetDoc doc = new LayerSet.LayerSetDoc();
      LayerSet.Type_LayerSet curRoot = new LayerSet.Type_LayerSet(doc.Load(path));
      if(displayName == null)
      {
       displayName = wmslink.Split('?')[0] + " - " + wmsLayerName + " : " + System.DateTime.Now.ToShortDateString() + " " + System.DateTime.Now.ToLongTimeString();
      }
      for(int i = 0; i < curRoot.ImageLayerCount; i++)
      {
       LayerSet.Type_ImageLayer curImageLayerType = (LayerSet.Type_ImageLayer)curRoot.GetImageLayerAt(i);
       if(curImageLayerType.Name.Value.Equals(displayName))
       {
        displayName += String.Format(CultureInfo.CurrentCulture, " : {0} {1}", System.DateTime.Now.ToShortDateString(), System.DateTime.Now.ToLongTimeString());
       }
      }
      LayerSet.Type_ImageLayer newImageLayer = new LayerSet.Type_ImageLayer();
      newImageLayer.AddShowAtStartup(new Altova.Types.SchemaBoolean(false));
      if(bb_north.Value2.DoubleValue() - bb_south.Value2.DoubleValue() > 90 ||
       bb_east.Value2.DoubleValue() - bb_west.Value2.DoubleValue() > 90)
       heightAboveSurface = 10000.0;
      newImageLayer.AddName(new LayerSet.NameType(
       displayName));
      newImageLayer.AddDistanceAboveSurface( new Altova.Types.SchemaDecimal(heightAboveSurface));
      LayerSet.Type_LatLonBoundingBox2 bb = new LayerSet.Type_LatLonBoundingBox2();
      bb.AddNorth(bb_north);
      bb.AddSouth(bb_south);
      bb.AddWest(bb_west);
      bb.AddEast(bb_east);
      newImageLayer.AddBoundingBox(bb);
      newImageLayer.AddTexturePath(new Altova.Types.SchemaString(
       texturePath));
      byte opacityValue = (byte)((100.0 - transparencyPercent) * 0.01 * 255);
      newImageLayer.AddOpacity(new LayerSet.OpacityType( opacityValue.ToString(CultureInfo.InvariantCulture)));
      newImageLayer.AddTerrainMapped(new Altova.Types.SchemaBoolean(false));
      curRoot.AddImageLayer(newImageLayer);
      doc.Save(path, curRoot);
      ImageLayer newLayer = new ImageLayer(
       displayName,
       this.worldWindow.CurrentWorld,
       (float)heightAboveSurface,
       texturePath,
       (float)bb_south.Value2.DoubleValue(),
       (float)bb_north.Value2.DoubleValue(),
       (float)bb_west.Value2.DoubleValue(),
       (float)bb_east.Value2.DoubleValue(),
       0.01f * (100.0f - transparencyPercent),
       this.worldWindow.CurrentWorld.TerrainAccessor);
      newLayer.ImageUrl = wmslink;
      RenderableObjectList downloadedImagesRol = (RenderableObjectList)this.worldWindow.CurrentWorld.RenderableObjects.GetObject("Downloaded WMS Images");
      if(downloadedImagesRol == null)
       downloadedImagesRol = new RenderableObjectList("Downloaded WMS Images");
      this.worldWindow.CurrentWorld.RenderableObjects.Add(newLayer);
      worldWindUri.Latitude = Angle.FromDegrees(0.5 * (bb_north.Value2.DoubleValue() + bb_south.Value2.DoubleValue()));
      worldWindUri.Longitude = Angle.FromDegrees(0.5 * (bb_west.Value2.DoubleValue() + bb_east.Value2.DoubleValue()));
      if(bb_north.Value2.DoubleValue() - bb_south.Value2.DoubleValue() > bb_east.Value2.DoubleValue() - bb_west.Value2.DoubleValue())
       worldWindUri.ViewRange = Angle.FromDegrees(bb_north.Value2.DoubleValue() - bb_south.Value2.DoubleValue());
      else
       worldWindUri.ViewRange = Angle.FromDegrees(bb_east.Value2.DoubleValue() - bb_west.Value2.DoubleValue());
      if(worldWindUri.ViewRange.Degrees > 180)
       worldWindUri.ViewRange = Angle.FromDegrees(180);
      System.Threading.Thread.Sleep(10);
     }
     catch
     {}
    }
   }
  }
  private static void LoadSettings()
  {
   try
   {
    Settings = (WorldWindSettings) SettingsBase.Load(Settings, SettingsBase.LocationType.User);
    if(!File.Exists(Settings.FileName))
    {
     Settings.PluginsLoadedOnStartup.Add("ShapeFileInfoTool");
     Settings.PluginsLoadedOnStartup.Add("Atmosphere");
                    Settings.PluginsLoadedOnStartup.Add("BmngLoader");
     Settings.PluginsLoadedOnStartup.Add("MeasureTool");
     Settings.PluginsLoadedOnStartup.Add("NRLWeatherLoader");
     Settings.PluginsLoadedOnStartup.Add("ShapeFileLoader");
     Settings.PluginsLoadedOnStartup.Add("Stars3D");
     Settings.PluginsLoadedOnStartup.Add("GlobalClouds");
     Settings.PluginsLoadedOnStartup.Add("PlaceFinderLoader");
     Settings.PluginsLoadedOnStartup.Add("Earthquake_2.0.2.1");
     Settings.PluginsLoadedOnStartup.Add("Historical_Earthquake_2.0.2.2");
     Settings.PluginsLoadedOnStartup.Add("KMLImporter");
     Settings.PluginsLoadedOnStartup.Add("PlanetaryRings");
                    Settings.PluginsLoadedOnStartup.Add("TimeController");
                    Settings.PluginsLoadedOnStartup.Add("WavingFlags");
                    Settings.PluginsLoadedOnStartup.Add("ScaleBarLegend");
                    Settings.PluginsLoadedOnStartup.Add("Compass3D");
    }
    DataProtector dp = new DataProtector(DataProtector.Store.USE_USER_STORE);
    if(Settings.ProxyUsername.Length > 0) Settings.ProxyUsername = dp.TransparentDecrypt(Settings.ProxyUsername);
    if(Settings.ProxyPassword.Length > 0) Settings.ProxyPassword = dp.TransparentDecrypt(Settings.ProxyPassword);
   }
   catch(Exception caught)
   {
    Log.Write(caught);
   }
  }
  private static void LoadSettings(string directory)
  {
   try
   {
    Settings = (WorldWindSettings) SettingsBase.LoadFromPath(Settings, directory);
    DataProtector dp = new DataProtector(DataProtector.Store.USE_USER_STORE);
    if(Settings.ProxyUsername.Length > 0) Settings.ProxyUsername = dp.TransparentDecrypt(Settings.ProxyUsername);
    if(Settings.ProxyPassword.Length > 0) Settings.ProxyPassword = dp.TransparentDecrypt(Settings.ProxyPassword);
   }
   catch(Exception caught)
   {
    Log.Write(caught);
   }
  }
  private static void ParseArgs(string[] args)
  {
   try
   {
    NLT.Plugins.ShapeFileLoaderGUI.m_ShapeLoad.ParseUri(args);
   }
   catch
   { }
   cmdArgs = args;
   foreach(string rawArg in args)
   {
    string arg = rawArg.Trim();
    if(arg.Length<=0)
     continue;
    try
    {
     if(arg.StartsWith("worldwind://"))
     {
      worldWindUri = WorldWindUri.Parse( arg );
     }
     else if(arg.StartsWith("/"))
     {
      if (arg.Length<=1)
      {
       throw new ArgumentException("Empty command line option.");
      }
      string key = arg.Substring(1,1).ToLower(CultureInfo.CurrentCulture);
      switch(key)
      {
       case "f":
        startFullScreen = true;
        break;
       case "s":
        if(issetCurrentSettingsDirectory)
        {
         continue;
        }
        if (arg.Length<6)
        {
         throw new ArgumentException("Invalid value(too short) for command line option /S: " + arg );
        }
        if (arg.Substring(2,1) != "=")
        {
         throw new ArgumentException("Invalid value(no = after S) for command line option /S: " + arg );
        }
        CurrentSettingsDirectory = arg.Substring(3);
        issetCurrentSettingsDirectory = true;
        break;
       default:
        throw new ArgumentException("Unknown command line option: " + arg );
      }
     }
     else
      throw new ArgumentException("Unknown command line option: " + arg );
    }
    catch (Exception ex)
    {
     Log.Write(ex);
    }
   }
  }
  private void UpdateToolBarStates()
  {
   this.toolBarButtonLayerManager.Pushed = this.menuItemLayerManager.Checked;
   this.toolBarButtonWMS.Pushed = this.menuItemWMS.Checked;
   this.toolBarButtonAnimatedEarth.Pushed = this.menuItemAnimatedEarth.Checked;
   this.toolBarButtonRapidFireModis.Pushed = this.menuItemModisHotSpots.Checked;
  }
  private void updateMenuItems()
  {
   this.menuItemShowCrosshairs.Checked = World.Settings.ShowCrosshairs;
   this.menuItemShowLatLonLines.Checked = World.Settings.ShowLatLonLines;
   this.menuItemShowPosition.Checked = World.Settings.ShowPosition;
   this.menuItemLayerManager.Checked = World.Settings.ShowLayerManager;
   if(this.rapidFireModisManager != null)
    this.menuItemModisHotSpots.Checked = this.rapidFireModisManager.Visible;
   if(this.wmsBrowser != null)
    this.menuItemWMS.Checked = this.wmsBrowser.Visible;
   if(this.animatedEarthMananger != null)
    this.menuItemAnimatedEarth.Checked = this.animatedEarthMananger.Visible;
  }
  private void OpenStartupWorld()
  {
   string startupWorldName = null;
   if (worldWindUri != null)
   {
    foreach(string curWorld in availableWorldList.Keys )
     if (string.Compare( worldWindUri.World, curWorld, true, CultureInfo.InvariantCulture)==0)
     {
      startupWorldName = curWorld;
      break;
     }
    if (startupWorldName==null)
    {
    }
   }
   if (startupWorldName == null && availableWorldList.Contains(Settings.DefaultWorld))
   {
    startupWorldName = Settings.DefaultWorld;
   }
   if (startupWorldName == null)
   {
    foreach(string curWorld in availableWorldList.Keys )
    {
     startupWorldName = curWorld;
     break;
    }
   }
   this.splashScreen.SetText("Initializing " + startupWorldName + "..." );
   if (startupWorldName != null)
   {
    string curWorldFile = availableWorldList[startupWorldName] as string;
    if(curWorldFile == null)
    {
     throw new ApplicationException(
      string.Format(CultureInfo.CurrentCulture, "Unable to load planet {0} configuration file from '{1}'.",
      startupWorldName,
      Settings.ConfigPath));
    }
    OpenWorld( curWorldFile );
   }
  }
  private void OpenWorld(string worldXmlFile)
  {
   if(this.worldWindow.CurrentWorld != null)
   {
    try
    {
     this.worldWindow.ResetToolbar();
    }
    catch
    {}
    try
    {
     foreach(PluginInfo p in this.compiler.Plugins)
     {
      try
      {
       if(p.Plugin.IsLoaded)
        p.Plugin.Unload();
      }
      catch
      {}
     }
    }
    catch
    {}
    try
    {
     this.worldWindow.CurrentWorld.Dispose();
    }
    catch
    {}
   }
   if(this.gotoDialog != null)
   {
    this.gotoDialog.Dispose();
    this.gotoDialog = null;
   }
   if(this.rapidFireModisManager != null)
   {
    this.rapidFireModisManager.Dispose();
    this.rapidFireModisManager = null;
   }
   if(this.animatedEarthMananger != null)
   {
    this.animatedEarthMananger.Dispose();
    this.animatedEarthMananger = null;
   }
   if(this.wmsBrowser != null)
   {
    this.wmsBrowser.Dispose();
    this.wmsBrowser = null;
   }
   worldWindow.CurrentWorld = WorldWind.ConfigurationLoader.Load(worldXmlFile, worldWindow.Cache);
   this.splashScreen.SetText("Initializing menus...");
   InitializePluginCompiler();
   foreach(RenderableObject worldRootObject in this.worldWindow.CurrentWorld.RenderableObjects.ChildObjects)
   {
    this.AddLayerMenuButtons(this.worldWindow, worldRootObject);
   }
   this.AddInternalPluginMenuButtons();
   this.menuItemModisHotSpots.Enabled = worldWindow.CurrentWorld.IsEarth;
   this.menuItemAnimatedEarth.Enabled = worldWindow.CurrentWorld.IsEarth;
  }
  private void AddLayerMenuButtons(WorldWindow ww, RenderableObject ro)
  {
   if(ro.MetaData.Contains("ToolBarImagePath"))
   {
    string imagePath = Path.Combine(DirectoryPath, (string)ro.MetaData["ToolBarImagePath"]);
    if(File.Exists(imagePath))
    {
     LayerShortcutMenuButton button = new LayerShortcutMenuButton(imagePath, ro);
     ww.MenuBar.AddLayersMenuButton( button );
     if( ro.Name=="Placenames" )
      button.SetPushed( World.Settings.ShowPlacenames );
     if( ro.Name=="Boundaries" )
      button.SetPushed( World.Settings.ShowBoundaries );
    }
   }
   if(ro.GetType() == typeof(RenderableObjectList))
   {
    RenderableObjectList rol = (RenderableObjectList)ro;
    foreach(RenderableObject child in rol.ChildObjects)
     AddLayerMenuButtons(ww, child);
   }
  }
  private NltTerrainAccessor getTerrainAccessorFromXML(WorldXmlDescriptor.TerrainAccessor curTerrainAccessorType)
  {
   double east = curTerrainAccessorType.LatLonBoundingBox.East.Value.DoubleValue();
   double west = curTerrainAccessorType.LatLonBoundingBox.West.Value.DoubleValue();
   double north = curTerrainAccessorType.LatLonBoundingBox.North.Value.DoubleValue();
   double south = curTerrainAccessorType.LatLonBoundingBox.South.Value.DoubleValue();
   NltTerrainAccessor[] subsets = null;
   if(curTerrainAccessorType.HasHigherResolutionSubsets())
   {
    subsets = new NltTerrainAccessor[curTerrainAccessorType.HigherResolutionSubsetsCount];
    for(int i = 0; i < curTerrainAccessorType.HigherResolutionSubsetsCount; i++)
    {
     subsets[i] = this.getTerrainAccessorFromXML(curTerrainAccessorType.GetHigherResolutionSubsetsAt(i));
    }
   }
   if(curTerrainAccessorType.HasDownloadableWMSSet())
   {
   }
   else if(curTerrainAccessorType.HasTerrainTileService())
   {
   }
   return null;
  }
  private RenderableObject getRenderableObjectListFromLayerSet(World curWorld, LayerSet.Type_LayerSet curLayerSet, string layerSetFile)
  {
   RenderableObjectList rol = null;
   if(curLayerSet.HasIcon())
   {
    rol = new Icons(curLayerSet.Name.Value);
    rol.RenderPriority = RenderPriority.Icons;
   }
   else
    rol = new RenderableObjectList(curLayerSet.Name.Value);
   if(curLayerSet.HasShowOnlyOneLayer())
    rol.ShowOnlyOneLayer = curLayerSet.ShowOnlyOneLayer.Value;
   if(curLayerSet.Name.ToString().ToUpper()=="PLACENAMES")
    rol.RenderPriority = RenderPriority.Placenames;
   if(curLayerSet.HasExtendedInformation())
   {
    if(curLayerSet.ExtendedInformation.HasToolBarImage())
     rol.MetaData.Add("ToolBarImagePath", curLayerSet.ExtendedInformation.ToolBarImage.Value);
   }
   if(curLayerSet.HasImageLayer())
   {
    for(int i = 0; i < curLayerSet.ImageLayerCount; i++)
    {
     LayerSet.Type_ImageLayer curImageLayerType = curLayerSet.GetImageLayerAt(i);
     string imagePath = null;
     string imageUrl = null;
     if(curImageLayerType.TexturePath.Value.ToLower().StartsWith(("http://")))
     {
      imageUrl = curImageLayerType.TexturePath.Value;
     }
     else
     {
      imagePath = curImageLayerType.TexturePath.Value;
      if(!Path.IsPathRooted(imagePath))
       imagePath = Path.Combine(DirectoryPath, imagePath);
     }
     int transparentColor = 0;
     if(curImageLayerType.HasTransparentColor())
     {
      transparentColor = System.Drawing.Color.FromArgb(
       curImageLayerType.TransparentColor.Red.Value,
       curImageLayerType.TransparentColor.Green.Value,
       curImageLayerType.TransparentColor.Blue.Value).ToArgb();
     }
     ImageLayer newImageLayer = new ImageLayer(
      curImageLayerType.Name.Value,
      curWorld,
      (float)curImageLayerType.DistanceAboveSurface.Value,
      imagePath,
      (float)curImageLayerType.BoundingBox.South.Value2.DoubleValue(),
      (float)curImageLayerType.BoundingBox.North.Value2.DoubleValue(),
      (float)curImageLayerType.BoundingBox.West.Value2.DoubleValue(),
      (float)curImageLayerType.BoundingBox.East.Value2.DoubleValue(),
      (byte)curImageLayerType.Opacity.Value,
      (curImageLayerType.TerrainMapped.Value ? curWorld.TerrainAccessor : null));
     newImageLayer.ImageUrl = imageUrl;
     newImageLayer.TransparentColor = transparentColor;
     newImageLayer.IsOn = curImageLayerType.ShowAtStartup.Value;
     if(curImageLayerType.HasLegendImagePath())
      newImageLayer.LegendImagePath = curImageLayerType.LegendImagePath.Value;
     if(curImageLayerType.HasExtendedInformation() && curImageLayerType.ExtendedInformation.HasToolBarImage())
      newImageLayer.MetaData.Add("ToolBarImagePath", Path.Combine(DirectoryPath, curImageLayerType.ExtendedInformation.ToolBarImage.Value));
     rol.Add(newImageLayer);
    }
   }
   if(curLayerSet.HasQuadTileSet())
   {
    for(int i = 0; i < curLayerSet.QuadTileSetCount; i++)
    {
     LayerSet.Type_QuadTileSet2 curQtsType = curLayerSet.GetQuadTileSetAt(i);
    }
   }
   if(curLayerSet.HasPathList())
   {
    for(int i = 0; i < curLayerSet.PathListCount; i++)
    {
     LayerSet.Type_PathList2 newPathList = curLayerSet.GetPathListAt(i);
     PathList pl = new PathList(
      newPathList.Name.Value,
      curWorld,
      newPathList.MinDisplayAltitude.DoubleValue(),
      newPathList.MaxDisplayAltitude.DoubleValue(),
      DirectoryPath + "//" + newPathList.PathsDirectory.Value,
      newPathList.DistanceAboveSurface.DoubleValue(),
      (newPathList.HasWinColorName() ? System.Drawing.Color.FromName(newPathList.WinColorName.Value) : System.Drawing.Color.FromArgb(newPathList.RGBColor.Red.Value, newPathList.RGBColor.Green.Value, newPathList.RGBColor.Blue.Value)),
      curWorld.TerrainAccessor);
     pl.IsOn = newPathList.ShowAtStartup.Value;
     if(newPathList.HasExtendedInformation() && newPathList.ExtendedInformation.HasToolBarImage())
      pl.MetaData.Add("ToolBarImagePath", Path.Combine(DirectoryPath, newPathList.ExtendedInformation.ToolBarImage.Value));
     rol.Add(pl);
    }
   }
   if(curLayerSet.HasShapeFileLayer())
   {
    for(int i = 0; i < curLayerSet.ShapeFileLayerCount; i++)
    {
     LayerSet.Type_ShapeFileLayer2 newShapefileLayer = curLayerSet.GetShapeFileLayerAt(i);
     Microsoft.DirectX.Direct3D.FontDescription fd = GetLayerFontDescription(newShapefileLayer.DisplayFont);
     Microsoft.DirectX.Direct3D.Font font = worldWindow.DrawArgs.CreateFont( fd );
     ShapeLayer sp = new ShapeLayer(
      newShapefileLayer.Name.Value,
      curWorld,
      newShapefileLayer.DistanceAboveSurface.DoubleValue(),
      newShapefileLayer.MasterFilePath.Value,
      newShapefileLayer.MinimumViewAltitude.DoubleValue(),
      newShapefileLayer.MaximumViewAltitude.DoubleValue(),
      font,
      (newShapefileLayer.HasWinColorName() ? System.Drawing.Color.FromName(newShapefileLayer.WinColorName.Value) : System.Drawing.Color.FromArgb(newShapefileLayer.RGBColor.Red.Value, newShapefileLayer.RGBColor.Green.Value, newShapefileLayer.RGBColor.Blue.Value)),
      (newShapefileLayer.HasScalarKey() ? newShapefileLayer.ScalarKey.Value : null),
      (newShapefileLayer.HasShowBoundaries() ? newShapefileLayer.ShowBoundaries.Value : false),
      (newShapefileLayer.HasShowFilledRegions() ? newShapefileLayer.ShowFilledRegions.Value : false));
     sp.IsOn = newShapefileLayer.ShowAtStartup.BoolValue();
     if(newShapefileLayer.HasExtendedInformation() && newShapefileLayer.ExtendedInformation.HasToolBarImage())
      sp.MetaData.Add("ToolBarImagePath", Path.Combine(DirectoryPath, newShapefileLayer.ExtendedInformation.ToolBarImage.Value));
     rol.Add(sp);
    }
   }
   if(curLayerSet.HasIcon())
   {
    Icons icons = (Icons)rol;
    for(int i = 0; i < curLayerSet.IconCount; i++)
    {
     LayerSet.Type_Icon newIcon = curLayerSet.GetIconAt(i);
     string textureFullPath = newIcon.TextureFilePath.Value;
     if (textureFullPath.Length > 0 && !Path.IsPathRooted(textureFullPath))
      textureFullPath = Path.Combine( DirectoryPath, newIcon.TextureFilePath.Value );
     WorldWind.Renderable.Icon ic = new WorldWind.Renderable.Icon(
      newIcon.Name.Value,
      (float)newIcon.Latitude.Value2.DoubleValue(),
      (float)newIcon.Longitude.Value2.DoubleValue(),
      (float)newIcon.DistanceAboveSurface.DoubleValue() );
     ic.TextureFileName = textureFullPath;
     ic.Width = newIcon.IconWidthPixels.Value;
     ic.Height = newIcon.IconHeightPixels.Value;
     ic.IsOn = newIcon.ShowAtStartup.Value;
     if(newIcon.HasDescription())
      ic.Description = newIcon.Description.Value;
     if(newIcon.HasClickableUrl())
      ic.ClickableActionURL = newIcon.ClickableUrl.Value;
     if(newIcon.HasMaximumDisplayAltitude())
      ic.MaximumDisplayDistance = (float)newIcon.MaximumDisplayAltitude.Value;
     if(newIcon.HasMinimumDisplayAltitude())
      ic.MinimumDisplayDistance = (float)newIcon.MinimumDisplayAltitude.Value;
     icons.Add(ic);
    }
   }
   if(curLayerSet.HasTiledPlacenameSet())
   {
    for(int i = 0; i < curLayerSet.TiledPlacenameSetCount; i++)
    {
     LayerSet.Type_TiledPlacenameSet2 newPlacenames = curLayerSet.GetTiledPlacenameSetAt(i);
     string filePath = newPlacenames.PlacenameListFilePath.Value;
     if(!Path.IsPathRooted(filePath))
      filePath = Path.Combine(DirectoryPath, filePath);
     Microsoft.DirectX.Direct3D.FontDescription fd = GetLayerFontDescription(newPlacenames.DisplayFont);
     TiledPlacenameSet tps = new TiledPlacenameSet(
      newPlacenames.Name.Value,
      curWorld,
      newPlacenames.DistanceAboveSurface.DoubleValue(),
      newPlacenames.MaximumDisplayAltitude.DoubleValue(),
      newPlacenames.MinimumDisplayAltitude.DoubleValue(),
      filePath,
      fd,
      (newPlacenames.HasWinColorName() ? System.Drawing.Color.FromName(newPlacenames.WinColorName.Value) : System.Drawing.Color.FromArgb(newPlacenames.RGBColor.Red.Value, newPlacenames.RGBColor.Green.Value, newPlacenames.RGBColor.Blue.Value)),
      (newPlacenames.HasIconFilePath() ? newPlacenames.IconFilePath.Value : null));
     if(newPlacenames.HasExtendedInformation() && newPlacenames.ExtendedInformation.HasToolBarImage())
      tps.MetaData.Add("ToolBarImagePath", Path.Combine(DirectoryPath, newPlacenames.ExtendedInformation.ToolBarImage.Value));
     tps.IsOn = newPlacenames.ShowAtStartup.Value;
     rol.Add(tps);
    }
   }
   if(curLayerSet.HasChildLayerSet())
   {
    for(int i = 0; i < curLayerSet.ChildLayerSetCount; i++)
    {
     LayerSet.Type_LayerSet ls = curLayerSet.GetChildLayerSetAt(i);
     rol.Add( getRenderableObjectListFromLayerSet( curWorld, ls, layerSetFile));
    }
   }
   rol.IsOn = curLayerSet.ShowAtStartup.Value;
   return rol;
  }
  protected static Microsoft.DirectX.Direct3D.FontDescription GetLayerFontDescription( LayerSet.Type_DisplayFont2 displayFont )
  {
   Microsoft.DirectX.Direct3D.FontDescription fd = new Microsoft.DirectX.Direct3D.FontDescription();
   fd.FaceName = displayFont.Family.Value;
   fd.Height = (int) ((float)displayFont.Size.Value*1.5f);
   if(displayFont.HasStyle())
   {
    LayerSet.StyleType2 layerStyle = displayFont.Style;
    if(displayFont.Style.HasIsItalic() && layerStyle.IsItalic.Value)
     fd.IsItalic = true;
    else
     fd.IsItalic = false;
    if(displayFont.Style.HasIsBold() && layerStyle.IsBold.Value)
     fd.Weight = Microsoft.DirectX.Direct3D.FontWeight.Bold;
    else
     fd.Weight = Microsoft.DirectX.Direct3D.FontWeight.Regular;
   }
   else
   {
    fd.Weight = Microsoft.DirectX.Direct3D.FontWeight.Regular;
   }
   return fd;
  }
  private static void Application_ThreadException( object sender, System.Threading.ThreadExceptionEventArgs e )
  {
   Log.Write( e.Exception );
   if (e.Exception is NullReferenceException)
    return;
   MessageBox.Show( e.Exception.Message, "An error has occurred", MessageBoxButtons.OK, MessageBoxIcon.Stop );
  }
  protected bool HandleKeyUp(KeyEventArgs e)
  {
   if(e.Handled)
    return true;
   if (e.Alt)
   {
    switch(e.KeyCode)
    {
     case Keys.A:
      menuItemAlwaysOnTop_Click(this,e);
      return true;
     case Keys.Q:
      using( PropertyBrowserForm worldWindSettings = new PropertyBrowserForm( Settings, "World Wind Settings" ) )
      {
       worldWindSettings.Icon = this.Icon;
       worldWindSettings.ShowDialog();
      }
      return true;
     case Keys.W:
      menuItemOptions_Click(this, EventArgs.Empty);
      return true;
     case Keys.Enter:
      this.FullScreen = !this.FullScreen;
      return true;
     case Keys.F4:
      Close();
      return true;
    }
   }
   else if (e.Control)
   {
    switch(e.KeyCode)
    {
     case Keys.C:
     case Keys.Insert:
      menuItemCoordsToClipboard_Click(this, e);
      return true;
     case Keys.F:
      return true;
     case Keys.H:
      if (progressMonitor != null)
      {
       bool wasVisible = progressMonitor.Visible;
       progressMonitor.Close();
       progressMonitor.Dispose();
       progressMonitor = null;
       if (wasVisible)
        return true;
      }
      progressMonitor = new ProgressMonitor();
      progressMonitor.Icon = this.Icon;
      progressMonitor.Show();
      return true;
     case Keys.I:
      menuItemConfigWizard_Click(this,e);
      return true;
     case Keys.N:
      menuItemOptions_Click(this,e);
      return true;
     case Keys.T:
      menuItemShowToolbar_Click(this,e);
      return true;
     case Keys.V:
      menuItemEditPaste_Click(this,e);
      return true;
     case Keys.S:
      menuItemSaveScreenShot_Click(this, e);
      return true;
    }
   }
   else if (e.Shift)
   {
    switch(e.KeyCode)
    {
     case Keys.Insert:
      menuItemEditPaste_Click(this,e);
      return true;
    }
   }
   else
   {
    switch(e.KeyCode)
    {
     case Keys.B:
      menuItemWMS_Click(this, e);
      return true;
     case Keys.G:
      return true;
     case Keys.L:
      menuItemLayerManager_Click(this, e);
      return true;
     case Keys.P:
      if(this.pathMaker == null)
      {
       this.pathMaker = new PathMaker(this.worldWindow);
       this.pathMaker.Icon = this.Icon;
      }
      this.pathMaker.Visible = !this.pathMaker.Visible;
      return true;
     case Keys.V:
      if(this.placeBuilderDialog == null)
      {
       this.placeBuilderDialog = new PlaceBuilder( this.worldWindow );
       this.placeBuilderDialog.Icon = this.Icon;
      }
      this.placeBuilderDialog.Visible = !this.placeBuilderDialog.Visible;
      return true;
     case Keys.Escape:
      if (this.FullScreen)
      {
       this.FullScreen = false;
       return true;
      }
      break;
     case Keys.D1:
     case Keys.NumPad1:
      this.VerticalExaggeration = 1.0f;
      return true;
     case Keys.D2:
     case Keys.NumPad2:
      this.VerticalExaggeration = 2.0f;
      return true;
     case Keys.D3:
     case Keys.NumPad3:
      this.VerticalExaggeration = 3.0f;
      return true;
     case Keys.D4:
     case Keys.NumPad4:
      this.VerticalExaggeration = 4.0f;
      return true;
     case Keys.D5:
     case Keys.NumPad5:
      this.VerticalExaggeration = 5.0f;
      return true;
     case Keys.D6:
     case Keys.NumPad6:
      this.VerticalExaggeration = 6.0f;
      return true;
     case Keys.D7:
     case Keys.NumPad7:
      this.VerticalExaggeration = 7.0f;
      return true;
     case Keys.D8:
     case Keys.NumPad8:
      this.VerticalExaggeration = 8.0f;
      return true;
     case Keys.D9:
     case Keys.NumPad9:
      this.VerticalExaggeration = 9.0f;
      return true;
     case Keys.D0:
     case Keys.NumPad0:
      this.VerticalExaggeration = 0.0f;
      return true;
     case Keys.F1:
      this.menuItemAnimatedEarth_Click(this,e);
      return true;
     case Keys.F2:
      this.menuItemModisHotSpots_Click(this,e);
      return true;
     case Keys.F5:
      this.menuItemRefreshCurrentView_Click(this,e);
      return true;
     case Keys.F6:
      return true;
     case Keys.F7:
      this.menuItemShowLatLonLines_Click(this,e);
      return true;
     case Keys.F8:
      this.menuItemPlanetAxis_Click(this,e);
      return true;
     case Keys.F9:
      this.menuItemShowCrosshairs_Click(this,e);
      return true;
     case Keys.F10:
      this.menuItemShowPosition_Click(this,e);
      return true;
     case Keys.F11:
      this.menuItemConstantMotion_Click(this,e);
      return true;
     case Keys.F12:
      this.menuItemPointGoTo_Click(this,e);
      return true;
    }
   }
   return false;
  }
  private void resetQuadTileSetCache(RenderableObject ro)
  {
   if(ro.IsOn && ro is QuadTileSet)
   {
    QuadTileSet qts = (QuadTileSet)ro;
    qts.ResetCacheForCurrentView(worldWindow.DrawArgs.WorldCamera);
   }
   else if(ro is RenderableObjectList)
   {
    RenderableObjectList rol = (RenderableObjectList)ro;
    foreach(RenderableObject curRo in rol.ChildObjects)
    {
     resetQuadTileSetCache(curRo);
    }
   }
  }
  protected override void OnClosing(System.ComponentModel.CancelEventArgs e)
  {
   if(compiler!=null)
    compiler.Dispose();
  }
  private static void app_DragEnter(object sender, DragEventArgs e)
  {
   e.Effect = DragDropEffects.None;
  }
  void webBrowser_BeforeNavigate2(object sender, DWebBrowserEvents2_BeforeNavigate2Event e)
  {
   this.webBrowserURL.Text = e.uRL.ToString();
  }
 }
}
