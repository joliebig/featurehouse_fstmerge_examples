using Microsoft.DirectX.Direct3D;
using System.Collections;
using System.ComponentModel;
using System.Diagnostics;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.Net;
using System.Threading;
using System.Windows.Forms;
using System;
using WorldWind.Net;
using WorldWind;
using WorldWind.Renderable;
using WorldWind.VisualControl;
namespace WorldWind
{
 public class AnimatedEarthManager : System.Windows.Forms.Form
 {
  private System.ComponentModel.IContainer components;
  static int startFrameInterval = 200;
  string serviceName = "WMS";
  const string WmsVersion = "1.3.0";
  const string ServerUrl = "http://aes.gsfc.nasa.gov/cgi-bin/wms";
  private WorldWindow worldWindow;
  private Queue downloadQueue = new Queue();
  private DownloadState downloadState = DownloadState.Pending;
  private AnimationState animationState = AnimationState.Pending;
  private TreeNode currentlyAnimatingNode;
  private WMSDownload wmsDownload;
  private Thread downloadThread;
  private System.Timers.Timer animationTimer = new System.Timers.Timer(200);
  private ArrayList animationFrames = new ArrayList();
  private int currentAnimationFrame;
  private WMSLayer currentlySelectedLayer;
  private ImageLayer imageLayer;
  private Colorbar colorbar;
  private Thread refreshThread;
  private bool isRefreshed;
  private TimeSpan timeout = new TimeSpan(0,0,0,20,0);
  private DateTime lastDownloadResponse = System.DateTime.Now;
  private bool isRendering;
  private System.Windows.Forms.Panel panel2;
  private System.Windows.Forms.Panel panel1;
  private System.Windows.Forms.TreeView treeViewLayers;
  private System.Windows.Forms.Panel panelMain;
  private System.Windows.Forms.Panel panelPlayer;
  private System.Windows.Forms.Button buttonRewind;
  private System.Windows.Forms.Button buttonStop;
  private System.Windows.Forms.Button buttonStepBack;
  private System.Windows.Forms.Button buttonStepForward;
  private System.Windows.Forms.Button buttonFastForward;
  private System.Windows.Forms.Button buttonPlay;
  private System.Windows.Forms.Splitter splitter1;
  private System.Windows.Forms.RichTextBox richTextBoxDescription;
  private System.Windows.Forms.ProgressBar progressBarFrame;
  private System.Windows.Forms.TrackBar trackBarOpacity;
  private System.Windows.Forms.Label labelOpacity;
  private System.Windows.Forms.ProgressBar progressBarCurrentProgress;
  private System.Windows.Forms.Label labelStatusBar;
  private System.Windows.Forms.ProgressBar progressBarTotalProgress;
  private System.Windows.Forms.ToolTip toolTip;
  private System.Windows.Forms.ContextMenu contextMenu;
  private System.Windows.Forms.MenuItem menuItemPlay;
  private System.Windows.Forms.MenuItem menuItemStop;
  private System.Windows.Forms.ImageList imageList;
  private System.Windows.Forms.Button buttonColorBar;
  public AnimatedEarthManager(WorldWindow worldWindow)
  {
   InitializeComponent();
   this.worldWindow = worldWindow;
  }
  protected override void OnLoad(EventArgs e)
  {
   this.animationTimer.Elapsed += new System.Timers.ElapsedEventHandler(animationTimer_Elapsed);
   this.animationTimer.Interval = startFrameInterval;
   treeViewLayers.Focus();
   base.OnLoad (e);
  }
  string CapabilitiesFilePath
  {
   get
   {
    string res = Path.Combine( CacheDirectory, "capabilities.xml" );
    return res;
   }
  }
  string CacheDirectory
  {
   get
   {
    string cacheDir = Path.Combine(
     Path.Combine( worldWindow.Cache.CacheDirectory, this.worldWindow.CurrentWorld.Name ),
     "Animated Earth" );
    if(!Directory.Exists(cacheDir))
     Directory.CreateDirectory(cacheDir);
    return cacheDir;
   }
  }
  double CurrentFrameInterval
  {
   get
   {
    return this.animationTimer.Interval;
   }
   set
   {
    if (value<=0)
     throw new ArgumentException("Invalid playback interval: "+ value);
    this.animationTimer.Interval = value;
    if (currentlySelectedLayer == null)
     return;
    if (currentlySelectedLayer.IsAnimation)
     SetCaption( string.Format( CultureInfo.CurrentCulture, "{0:f1} FPS", 1000.0/this.animationTimer.Interval ) );
    else
     SetCaption( "Still image" );
   }
  }
  private void SetCaption(string caption)
  {
   string text = "Scientific Visualization Studio";
   if( caption!=null && caption.Length>0)
    text += " ("+caption+")";
   this.Text = text;
  }
  bool IsSelectedChanged()
  {
   if(this.treeViewLayers.SelectedNode == null ||
    this.treeViewLayers.SelectedNode.Tag == null )
    return false;
   if(this.currentlyAnimatingNode == this.treeViewLayers.SelectedNode)
    return false;
   return true;
  }
  private void updateCurrentProgressBar(int bytesSoFar, int bytesTotal)
  {
   if( this.InvokeRequired )
   {
    DownloadProgressHandler dlgt =
     new DownloadProgressHandler(updateCurrentProgressBar);
    this.BeginInvoke(dlgt, new object[] { bytesSoFar, bytesTotal });
    return;
   }
   if(bytesSoFar < 0)
    bytesSoFar = 0;
   if(bytesTotal < 0)
    bytesTotal = 0;
   this.progressBarCurrentProgress.Maximum = bytesTotal;
   this.progressBarCurrentProgress.Value = bytesSoFar<=bytesTotal ? bytesSoFar : bytesTotal;
  }
  void UpdateProgressBarFrame(int bytesSoFar, int bytesTotal)
  {
   if( this.InvokeRequired )
   {
    DownloadProgressHandler dlgt =
     new DownloadProgressHandler(UpdateProgressBarFrame);
    this.BeginInvoke(dlgt, new object[] { bytesSoFar, bytesTotal });
    return;
   }
   if(bytesSoFar < 0)
    bytesSoFar = 0;
   if(bytesTotal < 0)
    bytesTotal = 0;
   if(bytesTotal<bytesSoFar)
    bytesTotal=bytesSoFar;
   this.progressBarFrame.Maximum = bytesTotal;
   this.progressBarFrame.Value = bytesSoFar<=bytesTotal ? bytesSoFar : bytesTotal;
  }
  public void RefreshCapabilities()
  {
   if(refreshThread!=null && refreshThread.IsAlive)
    return;
   if (isRefreshed)
    return;
   try
   {
    using( Stream capabilitiesStream = File.Open(CapabilitiesFilePath, FileMode.Open, FileAccess.Read))
     RefreshCapabilities(capabilitiesStream);
   }
   catch
   {
   }
   refreshThread = new Thread(new ThreadStart(RefreshCapabilitiesFromUrl));
   refreshThread.Name = "AnimatedEarthManager.RefreshCapabilitiesFromUrl";
   refreshThread.IsBackground = true;
   refreshThread.Start();
  }
  private void RefreshCapabilitiesFromUrl()
  {
   try
   {
    updateStatusBar("Downloading updated table of contents...");
    string wmsQueryString = AnimatedEarthManager.ServerUrl + "?service=" + this.serviceName + "&version=" + WmsVersion + "&request=GetCapabilities";
    using( WebDownload download = new WebDownload(wmsQueryString) )
    {
     download.ProgressCallback += new DownloadProgressHandler( this.updateCurrentProgressBar );
     download.DownloadMemory();
     try
     {
      download.SaveMemoryDownloadToFile(CapabilitiesFilePath);
     }
     catch(Exception)
     {
     }
     RefreshCapabilities( download.ContentStream );
    }
    isRefreshed = true;
   }
   catch
   {
    updateStatusBar("Connection Error.  Please try again later.");
   }
   this.updateCurrentProgressBar(0,0);
  }
  private void RefreshCapabilities( Stream capabilitiesStream )
  {
   System.Xml.XmlReader capReader = null;
   try
   {
    capReader = new System.Xml.XmlTextReader(capabilitiesStream);
    capabilities_1_3_0.capabilities_1_3_0Doc doc = new capabilities_1_3_0.capabilities_1_3_0Doc();
    capabilities_1_3_0.wms.WMS_CapabilitiesType root = new capabilities_1_3_0.wms.WMS_CapabilitiesType(
     doc.Load( capReader ));
    if(root.HasCapability())
    {
     capabilities_1_3_0.wms.LayerType rootLayer = root.Capability.GetLayer();
     for(int i = 0; i < rootLayer.LayerCount; i++)
     {
      capabilities_1_3_0.wms.LayerType curLayer = (capabilities_1_3_0.wms.LayerType)rootLayer.GetLayerAt(i);
      TreeNode tn = this.getTreeNodeFromLayerType(curLayer);
      this.treeViewLayers.BeginInvoke(new UpdateTreeDelegate(UpdateTree), new object[] {tn});
     }
     updateStatusBar("Download successful.");
    }
    else
    {
     updateStatusBar("Invalid table of contents. Please try again later.");
    }
   }
   catch(Exception caught)
   {
    updateStatusBar(caught.Message);
   }
   finally
   {
    if(capReader != null)
     capReader.Close();
   }
  }
  private TreeNode getTreeNodeFromLayerType(capabilities_1_3_0.wms.LayerType curLayer)
  {
   TreeNode tn = new TreeNode(curLayer.Title.Value);
   tn.Tag = new WMSLayer(curLayer);
   for(int i = 0; i < curLayer.LayerCount; i++)
   {
    capabilities_1_3_0.wms.LayerType childLayer = (capabilities_1_3_0.wms.LayerType)curLayer.GetLayerAt(i);
    TreeNode childNode = this.getTreeNodeFromLayerType(childLayer);
    tn.Nodes.Add(childNode);
   }
   return tn;
  }
  delegate void UpdateTreeDelegate( TreeNode newNode);
  private void UpdateTree(TreeNode newNode)
  {
   string selectedText = null;
   if (treeViewLayers.SelectedNode != null)
    selectedText = treeViewLayers.SelectedNode.Text;
   bool wasUpdated = false;
   for (int i=0; i<treeViewLayers.Nodes.Count; i++)
    if (treeViewLayers.Nodes[i].Text == newNode.Text)
    {
     treeViewLayers.Nodes.RemoveAt(i);
     treeViewLayers.Nodes.Insert(i, newNode);
     wasUpdated = true;
     break;
    }
   if (!wasUpdated)
    this.treeViewLayers.Nodes.Add( newNode );
   if(selectedText!=null)
   {
    foreach( TreeNode rootNode in treeViewLayers.Nodes )
    {
     if (rootNode.Text == selectedText)
     {
      treeViewLayers.SelectedNode = rootNode;
     }
     foreach( TreeNode childNode in rootNode.Nodes )
     {
      if (childNode.Text == selectedText)
      {
       treeViewLayers.SelectedNode = childNode;
      }
     }
    }
   }
  }
  delegate
   void updateStatusBarDelegate( string statusMsg );
  private void updateStatusBar( string statusMsg )
  {
   if( InvokeRequired )
   {
    updateStatusBarDelegate updateDelegate =
     new updateStatusBarDelegate(updateStatusBar);
    this.BeginInvoke(updateDelegate, new object[] { statusMsg });
    return;
   }
   this.labelStatusBar.Text = statusMsg;
  }
  delegate
   void AddAnimationFrameDelegate(WMSDownload dl);
  private void AddAnimationFrame(WMSDownload dl)
  {
   if( InvokeRequired )
   {
    AddAnimationFrameDelegate dlgt =
     new AddAnimationFrameDelegate(AddAnimationFrame);
    this.BeginInvoke(dlgt, new object[] { dl });
    return;
   }
   if(this.currentlyAnimatingNode==null)
    return;
   lock(this.animationFrames.SyncRoot)
   {
    this.animationFrames.Add(dl);
    if(this.animationFrames.Count != 1 &&
     this.progressBarTotalProgress.Value != this.progressBarTotalProgress.Maximum)
    {
     this.progressBarTotalProgress.Value = this.animationFrames.Count;
     WMSLayer curLayer = (WMSLayer)this.currentlyAnimatingNode.Tag;
     if(curLayer.dates != null)
      this.progressBarTotalProgress.Maximum = curLayer.dates.Length;
    }
   }
  }
  protected override void Dispose( bool disposing )
  {
   if(refreshThread!=null)
   {
    refreshThread.Abort();
    refreshThread = null;
   }
   if(downloadThread!=null)
   {
    downloadThread.Abort();
    downloadThread = null;
   }
   if( disposing )
   {
    if(components != null)
    {
     components.Dispose();
     components = null;
    }
    this.StopAnimation();
   }
   base.Dispose( disposing );
  }
  private void InitializeComponent()
  {
   this.components = new System.ComponentModel.Container();
   System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(AnimatedEarthManager));
   this.progressBarCurrentProgress = new System.Windows.Forms.ProgressBar();
   this.panel2 = new System.Windows.Forms.Panel();
   this.panel1 = new System.Windows.Forms.Panel();
   this.progressBarTotalProgress = new System.Windows.Forms.ProgressBar();
   this.labelStatusBar = new System.Windows.Forms.Label();
   this.panelMain = new System.Windows.Forms.Panel();
   this.richTextBoxDescription = new System.Windows.Forms.RichTextBox();
   this.contextMenu = new System.Windows.Forms.ContextMenu();
   this.menuItemPlay = new System.Windows.Forms.MenuItem();
   this.menuItemStop = new System.Windows.Forms.MenuItem();
   this.splitter1 = new System.Windows.Forms.Splitter();
   this.treeViewLayers = new System.Windows.Forms.TreeView();
   this.panelPlayer = new System.Windows.Forms.Panel();
   this.buttonColorBar = new System.Windows.Forms.Button();
   this.imageList = new System.Windows.Forms.ImageList(this.components);
   this.labelOpacity = new System.Windows.Forms.Label();
   this.trackBarOpacity = new System.Windows.Forms.TrackBar();
   this.progressBarFrame = new System.Windows.Forms.ProgressBar();
   this.buttonStop = new System.Windows.Forms.Button();
   this.buttonStepBack = new System.Windows.Forms.Button();
   this.buttonStepForward = new System.Windows.Forms.Button();
   this.buttonFastForward = new System.Windows.Forms.Button();
   this.buttonPlay = new System.Windows.Forms.Button();
   this.buttonRewind = new System.Windows.Forms.Button();
   this.toolTip = new System.Windows.Forms.ToolTip(this.components);
   this.panel2.SuspendLayout();
   this.panel1.SuspendLayout();
   this.panelMain.SuspendLayout();
   this.panelPlayer.SuspendLayout();
   ((System.ComponentModel.ISupportInitialize)(this.trackBarOpacity)).BeginInit();
   this.SuspendLayout();
   this.progressBarCurrentProgress.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
   this.progressBarCurrentProgress.Location = new System.Drawing.Point(0, 1);
   this.progressBarCurrentProgress.Name = "progressBarCurrentProgress";
   this.progressBarCurrentProgress.Size = new System.Drawing.Size(136, 14);
   this.progressBarCurrentProgress.Step = 1;
   this.progressBarCurrentProgress.TabIndex = 0;
   this.toolTip.SetToolTip(this.progressBarCurrentProgress, "Current progress");
   this.panel2.Controls.Add(this.panel1);
   this.panel2.Controls.Add(this.labelStatusBar);
   this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
   this.panel2.Location = new System.Drawing.Point(0, 291);
   this.panel2.Name = "panel2";
   this.panel2.Size = new System.Drawing.Size(368, 32);
   this.panel2.TabIndex = 14;
   this.panel1.Controls.Add(this.progressBarCurrentProgress);
   this.panel1.Controls.Add(this.progressBarTotalProgress);
   this.panel1.Dock = System.Windows.Forms.DockStyle.Right;
   this.panel1.Location = new System.Drawing.Point(232, 0);
   this.panel1.Name = "panel1";
   this.panel1.Size = new System.Drawing.Size(136, 32);
   this.panel1.TabIndex = 15;
   this.progressBarTotalProgress.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
   this.progressBarTotalProgress.Location = new System.Drawing.Point(0, 16);
   this.progressBarTotalProgress.Name = "progressBarTotalProgress";
   this.progressBarTotalProgress.Size = new System.Drawing.Size(136, 14);
   this.progressBarTotalProgress.TabIndex = 1;
   this.toolTip.SetToolTip(this.progressBarTotalProgress, "Total progress");
   this.labelStatusBar.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
    | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.labelStatusBar.Location = new System.Drawing.Point(0, 0);
   this.labelStatusBar.Name = "labelStatusBar";
   this.labelStatusBar.Size = new System.Drawing.Size(232, 32);
   this.labelStatusBar.TabIndex = 0;
   this.labelStatusBar.TextAlign = System.Drawing.ContentAlignment.BottomLeft;
   this.panelMain.Controls.Add(this.richTextBoxDescription);
   this.panelMain.Controls.Add(this.splitter1);
   this.panelMain.Controls.Add(this.treeViewLayers);
   this.panelMain.Dock = System.Windows.Forms.DockStyle.Fill;
   this.panelMain.Location = new System.Drawing.Point(0, 0);
   this.panelMain.Name = "panelMain";
   this.panelMain.Size = new System.Drawing.Size(368, 219);
   this.panelMain.TabIndex = 15;
   this.richTextBoxDescription.BackColor = System.Drawing.SystemColors.Control;
   this.richTextBoxDescription.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
   this.richTextBoxDescription.ContextMenu = this.contextMenu;
   this.richTextBoxDescription.Dock = System.Windows.Forms.DockStyle.Fill;
   this.richTextBoxDescription.Location = new System.Drawing.Point(0, 115);
   this.richTextBoxDescription.Name = "richTextBoxDescription";
   this.richTextBoxDescription.ReadOnly = true;
   this.richTextBoxDescription.Size = new System.Drawing.Size(368, 104);
   this.richTextBoxDescription.TabIndex = 1;
   this.richTextBoxDescription.Text = "";
   this.contextMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                            this.menuItemPlay,
                            this.menuItemStop});
   this.menuItemPlay.Index = 0;
   this.menuItemPlay.Text = "&Play";
   this.menuItemPlay.Click += new System.EventHandler(this.buttonPlay_Click);
   this.menuItemStop.Index = 1;
   this.menuItemStop.Text = "&Stop";
   this.menuItemStop.Click += new System.EventHandler(this.buttonStop_Click);
   this.splitter1.Dock = System.Windows.Forms.DockStyle.Top;
   this.splitter1.Location = new System.Drawing.Point(0, 112);
   this.splitter1.Name = "splitter1";
   this.splitter1.Size = new System.Drawing.Size(368, 3);
   this.splitter1.TabIndex = 1;
   this.splitter1.TabStop = false;
   this.treeViewLayers.ContextMenu = this.contextMenu;
   this.treeViewLayers.Dock = System.Windows.Forms.DockStyle.Top;
   this.treeViewLayers.FullRowSelect = true;
   this.treeViewLayers.HideSelection = false;
   this.treeViewLayers.ImageIndex = -1;
   this.treeViewLayers.Location = new System.Drawing.Point(0, 0);
   this.treeViewLayers.Name = "treeViewLayers";
   this.treeViewLayers.SelectedImageIndex = -1;
   this.treeViewLayers.Size = new System.Drawing.Size(368, 112);
   this.treeViewLayers.Sorted = true;
   this.treeViewLayers.TabIndex = 0;
   this.toolTip.SetToolTip(this.treeViewLayers, "Choose animation and press Play");
   this.treeViewLayers.DoubleClick += new System.EventHandler(this.treeViewLayers_DoubleClick);
   this.treeViewLayers.AfterSelect += new System.Windows.Forms.TreeViewEventHandler(this.treeViewLayers_AfterSelect);
   this.panelPlayer.Controls.Add(this.buttonColorBar);
   this.panelPlayer.Controls.Add(this.labelOpacity);
   this.panelPlayer.Controls.Add(this.trackBarOpacity);
   this.panelPlayer.Controls.Add(this.progressBarFrame);
   this.panelPlayer.Controls.Add(this.buttonStop);
   this.panelPlayer.Controls.Add(this.buttonStepBack);
   this.panelPlayer.Controls.Add(this.buttonStepForward);
   this.panelPlayer.Controls.Add(this.buttonFastForward);
   this.panelPlayer.Controls.Add(this.buttonPlay);
   this.panelPlayer.Controls.Add(this.buttonRewind);
   this.panelPlayer.Dock = System.Windows.Forms.DockStyle.Bottom;
   this.panelPlayer.Location = new System.Drawing.Point(0, 219);
   this.panelPlayer.Name = "panelPlayer";
   this.panelPlayer.Size = new System.Drawing.Size(368, 72);
   this.panelPlayer.TabIndex = 0;
   this.buttonColorBar.Enabled = false;
   this.buttonColorBar.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
   this.buttonColorBar.ImageIndex = 8;
   this.buttonColorBar.ImageList = this.imageList;
   this.buttonColorBar.Location = new System.Drawing.Point(248, 8);
   this.buttonColorBar.Name = "buttonColorBar";
   this.buttonColorBar.Size = new System.Drawing.Size(37, 36);
   this.buttonColorBar.TabIndex = 6;
   this.toolTip.SetToolTip(this.buttonColorBar, "Display legend (Alt+L)");
   this.buttonColorBar.Click += new System.EventHandler(this.buttonColorBar_Click);
   this.imageList.ColorDepth = System.Windows.Forms.ColorDepth.Depth32Bit;
   this.imageList.ImageSize = new System.Drawing.Size(32, 32);
   this.imageList.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList.ImageStream")));
   this.imageList.TransparentColor = System.Drawing.Color.Transparent;
   this.labelOpacity.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.labelOpacity.Location = new System.Drawing.Point(299, 41);
   this.labelOpacity.Name = "labelOpacity";
   this.labelOpacity.Size = new System.Drawing.Size(56, 16);
   this.labelOpacity.TabIndex = 7;
   this.labelOpacity.Text = "&Opacity";
   this.labelOpacity.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
   this.trackBarOpacity.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.trackBarOpacity.LargeChange = 10;
   this.trackBarOpacity.Location = new System.Drawing.Point(286, 5);
   this.trackBarOpacity.Maximum = 100;
   this.trackBarOpacity.Name = "trackBarOpacity";
   this.trackBarOpacity.Size = new System.Drawing.Size(75, 45);
   this.trackBarOpacity.SmallChange = 3;
   this.trackBarOpacity.TabIndex = 8;
   this.trackBarOpacity.TickFrequency = 5;
   this.trackBarOpacity.TickStyle = System.Windows.Forms.TickStyle.TopLeft;
   this.toolTip.SetToolTip(this.trackBarOpacity, "Adjust animation transparency");
   this.trackBarOpacity.Value = 100;
   this.trackBarOpacity.Scroll += new System.EventHandler(this.trackBarOpacity_Scroll);
   this.progressBarFrame.Location = new System.Drawing.Point(8, 48);
   this.progressBarFrame.Name = "progressBarFrame";
   this.progressBarFrame.Size = new System.Drawing.Size(277, 12);
   this.progressBarFrame.TabIndex = 9;
   this.toolTip.SetToolTip(this.progressBarFrame, "Animation progress");
   this.buttonStop.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
   this.buttonStop.ImageIndex = 7;
   this.buttonStop.ImageList = this.imageList;
   this.buttonStop.Location = new System.Drawing.Point(88, 8);
   this.buttonStop.Name = "buttonStop";
   this.buttonStop.Size = new System.Drawing.Size(37, 36);
   this.buttonStop.TabIndex = 2;
   this.toolTip.SetToolTip(this.buttonStop, "Stop");
   this.buttonStop.Click += new System.EventHandler(this.buttonStop_Click);
   this.buttonStepBack.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
   this.buttonStepBack.ImageIndex = 6;
   this.buttonStepBack.ImageList = this.imageList;
   this.buttonStepBack.Location = new System.Drawing.Point(48, 8);
   this.buttonStepBack.Name = "buttonStepBack";
   this.buttonStepBack.Size = new System.Drawing.Size(37, 36);
   this.buttonStepBack.TabIndex = 1;
   this.toolTip.SetToolTip(this.buttonStepBack, "Step back");
   this.buttonStepBack.Click += new System.EventHandler(this.buttonStepBack_Click);
   this.buttonStepForward.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
   this.buttonStepForward.ImageIndex = 5;
   this.buttonStepForward.ImageList = this.imageList;
   this.buttonStepForward.Location = new System.Drawing.Point(168, 8);
   this.buttonStepForward.Name = "buttonStepForward";
   this.buttonStepForward.Size = new System.Drawing.Size(37, 36);
   this.buttonStepForward.TabIndex = 4;
   this.toolTip.SetToolTip(this.buttonStepForward, "Step forward");
   this.buttonStepForward.Click += new System.EventHandler(this.buttonStepForward_Click);
   this.buttonFastForward.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
   this.buttonFastForward.ImageIndex = 3;
   this.buttonFastForward.ImageList = this.imageList;
   this.buttonFastForward.Location = new System.Drawing.Point(208, 8);
   this.buttonFastForward.Name = "buttonFastForward";
   this.buttonFastForward.Size = new System.Drawing.Size(37, 36);
   this.buttonFastForward.TabIndex = 5;
   this.toolTip.SetToolTip(this.buttonFastForward, "Fast forward");
   this.buttonFastForward.Click += new System.EventHandler(this.buttonFastForward_Click);
   this.buttonPlay.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
   this.buttonPlay.ImageIndex = 4;
   this.buttonPlay.ImageList = this.imageList;
   this.buttonPlay.Location = new System.Drawing.Point(128, 8);
   this.buttonPlay.Name = "buttonPlay";
   this.buttonPlay.Size = new System.Drawing.Size(37, 36);
   this.buttonPlay.TabIndex = 3;
   this.toolTip.SetToolTip(this.buttonPlay, "Play");
   this.buttonPlay.Click += new System.EventHandler(this.buttonPlay_Click);
   this.buttonRewind.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
   this.buttonRewind.ImageIndex = 0;
   this.buttonRewind.ImageList = this.imageList;
   this.buttonRewind.Location = new System.Drawing.Point(8, 8);
   this.buttonRewind.Name = "buttonRewind";
   this.buttonRewind.Size = new System.Drawing.Size(37, 36);
   this.buttonRewind.TabIndex = 0;
   this.toolTip.SetToolTip(this.buttonRewind, "Rewind");
   this.buttonRewind.Click += new System.EventHandler(this.buttonRewind_Click);
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.ClientSize = new System.Drawing.Size(368, 323);
   this.Controls.Add(this.panelMain);
   this.Controls.Add(this.panelPlayer);
   this.Controls.Add(this.panel2);
   this.KeyPreview = true;
   this.MaximizeBox = false;
   this.MinimumSize = new System.Drawing.Size(376, 297);
   this.Name = "AnimatedEarthManager";
   this.Text = "Scientific Visualization Studio";
   this.panel2.ResumeLayout(false);
   this.panel1.ResumeLayout(false);
   this.panelMain.ResumeLayout(false);
   this.panelPlayer.ResumeLayout(false);
   ((System.ComponentModel.ISupportInitialize)(this.trackBarOpacity)).EndInit();
   this.ResumeLayout(false);
  }
  private void downloadLayer(WMSLayer layer, string curDate)
  {
   bool cancel = (this.downloadState == DownloadState.Cancel);
   if(cancel)
    return;
   string fileName = "Default";
   string time="";
   if(curDate != null)
   {
    time = "&time=" + curDate;
    fileName = curDate.Replace(":","");
   }
   string layerPath = CacheDirectory;
   if(layer.Name!=null)
    layerPath = Path.Combine( CacheDirectory, layer.Name );
   const string fileType = "png";
   string url = AnimatedEarthManager.ServerUrl+
    "?service=" + this.serviceName +
    "&version=" + WmsVersion +
    "&request=GetMap" +
    "&layers=" + layer.Name +
    "&format=image/png" +
    "&width=" + (layer.Width != 0 ? layer.Width.ToString(CultureInfo.InvariantCulture) : "1024") +
    "&height=" + (layer.Height != 0 ? layer.Height.ToString(CultureInfo.InvariantCulture) : "512") +
    time +
    "&crs=" + layer.crs +
    "&bbox=" + String.Format(CultureInfo.InvariantCulture, "{0},{1},{2},{3}", layer.west, layer.south, layer.east, layer.north);
   WMSDownload wmsDownload = new WMSDownload( url );
   wmsDownload.Date = curDate;
   wmsDownload.Title = layer.Title;
   wmsDownload.West = layer.west;
   wmsDownload.South = layer.south;
   wmsDownload.East = layer.east;
   wmsDownload.North = layer.north;
   wmsDownload.SavedFilePath = Path.Combine( layerPath, fileName) + ".dds";
   if(File.Exists(wmsDownload.SavedFilePath))
   {
    AddAnimationFrame(wmsDownload);
    return;
   }
   wmsDownload.SavedFilePath = Path.Combine(layerPath, fileName) + "." + fileType;
   if(File.Exists(wmsDownload.SavedFilePath))
   {
    AddAnimationFrame(wmsDownload);
    return;
   }
   lock(this.downloadQueue.SyncRoot)
   {
    this.downloadQueue.Enqueue(wmsDownload);
   }
  }
  private void AnimateSlower()
  {
   if(this.CurrentFrameInterval >= 10000)
    return;
   this.CurrentFrameInterval *= 2;
  }
  private void AnimateFaster()
  {
   if(this.CurrentFrameInterval <= 15)
    return;
   this.CurrentFrameInterval /= 2;
  }
  private void displayFrame(int frameNumber)
  {
   WMSDownload curFrame = null;
   lock(this.animationFrames.SyncRoot)
   {
    if (this.animationFrames.Count<=0)
     return;
    if (frameNumber>=this.animationFrames.Count)
     frameNumber = 0;
    if (frameNumber<0)
     frameNumber = this.animationFrames.Count-1;
    this.currentAnimationFrame = frameNumber;
    curFrame = (WMSDownload)this.animationFrames[frameNumber];
   }
   if(curFrame != null && File.Exists(curFrame.SavedFilePath))
   {
    if(this.imageLayer == null)
    {
     byte opacity = (byte)(255 * ((float)this.trackBarOpacity.Value / this.trackBarOpacity.Maximum));
     this.imageLayer = new ImageLayer("NASA SVS",
      this.worldWindow.CurrentWorld, 10000.0f,
      curFrame.SavedFilePath, (float)curFrame.South, (float)curFrame.North, (float)curFrame.West, (float)curFrame.East,
      opacity, null);
     this.imageLayer.IsOn = true;
     this.imageLayer.Initialize(this.worldWindow.DrawArgs);
     this.worldWindow.CurrentWorld.RenderableObjects.Add(this.imageLayer);
    }
    else
    {
     this.imageLayer.UpdateTexture(curFrame.SavedFilePath);
    }
    this.worldWindow.Caption = curFrame.Title + "\n" + curFrame.Date;
    this.worldWindow.Invalidate();
    UpdateProgressBarFrame(
     this.animationFrames.Count == 1 ? 1 : frameNumber,
     progressBarFrame.Maximum);
   }
  }
  private void QueueAndDownload(WMSLayer layer)
  {
   if(layer.dates != null && layer.dates.Length>0)
   {
    UpdateProgressBarFrame(0, layer.dates.Length - 1);
    this.progressBarTotalProgress.Maximum = layer.dates.Length;
   }
   else
   {
    UpdateProgressBarFrame(0,1);
    this.progressBarTotalProgress.Maximum = 1;
   }
   if(layer.dates == null)
    this.downloadLayer(layer, null);
   else
   {
    foreach(string curDate in layer.dates)
     this.downloadLayer(layer, curDate);
   }
   this.buttonPlay.Enabled = true;
   this.downloadState = DownloadState.Pending;
   if (downloadThread!=null && downloadThread.IsAlive)
    return;
   downloadThread = new Thread( new ThreadStart(Downloader));
   downloadThread.Name = "AnimatedEarthManager.Downloader";
   downloadThread.IsBackground = true;
   downloadThread.Start();
  }
  public void StartAnimation()
  {
   if(treeViewLayers.SelectedNode==null)
    return;
   if(this.currentlyAnimatingNode == this.treeViewLayers.SelectedNode)
   {
    this.CurrentFrameInterval = this.CurrentFrameInterval;
   }
   else
   {
    this.StopAnimation();
    this.currentlyAnimatingNode = this.treeViewLayers.SelectedNode;
    this.currentlyAnimatingNode.ForeColor = System.Drawing.Color.Blue;
    updateCurrentProgressBar(0,1);
    WMSLayer layer = (WMSLayer)this.treeViewLayers.SelectedNode.Tag;
    if (layer.Name==null)
     return;
    QueueAndDownload(layer);
    this.CurrentFrameInterval = startFrameInterval;
    this.animationTimer.Enabled = true;
    this.buttonColorBar.Enabled = (layer.LegendUrl != null);
   }
   this.buttonPlay.Enabled = true;
   this.buttonPlay.ImageIndex = 2;
   this.animationState = AnimationState.PlayingForward;
  }
  public void PauseAnimation()
  {
   if(this.animationState==AnimationState.Pending)
    StartAnimation();
   this.animationState = AnimationState.Paused;
   SetCaption( "Pause" );
   this.buttonPlay.ImageIndex = 4;
  }
  public void Reset()
  {
   this.StopAnimation();
  }
  public void StopAnimation()
  {
   if(this.animationState==AnimationState.Pending)
    return;
   if(this.currentlyAnimatingNode != null)
    this.currentlyAnimatingNode.ForeColor = this.treeViewLayers.ForeColor;
   this.currentlyAnimatingNode = null;
   this.animationState = AnimationState.Pending;
   this.animationTimer.Enabled = false;
   StopDownloadThread();
   updateCurrentProgressBar(0,1);
   lock(this.downloadQueue.SyncRoot)
    this.downloadQueue.Clear();
   this.downloadState = DownloadState.Pending;
   lock(this.animationFrames.SyncRoot)
    this.animationFrames.Clear();
   this.currentAnimationFrame = 0;
   if(this.imageLayer != null)
   {
    this.worldWindow.CurrentWorld.RenderableObjects.Remove(this.imageLayer.Name);
    this.imageLayer.Dispose();
    this.imageLayer = null;
   }
   this.worldWindow.Caption = "";
   UpdateProgressBarFrame(0,1);
   this.buttonPlay.ImageIndex = 4;
   this.CurrentFrameInterval = startFrameInterval;
   this.buttonColorBar.Enabled = false;
   if(this.colorbar != null)
   {
    this.colorbar.Visible = false;
    this.colorbar.Dispose();
    this.colorbar = null;
   }
   SetCaption( "" );
   updateStatusBar("Stopped.");
  }
  private void buttonPlay_Click(object sender, System.EventArgs e)
  {
   if(IsSelectedChanged())
    StopAnimation();
   switch(this.animationState)
   {
    case AnimationState.Pending:
    case AnimationState.Paused:
     StartAnimation();
     break;
    case AnimationState.PlayingForward:
    case AnimationState.PlayingReverse:
     PauseAnimation();
     break;
   }
  }
  private void buttonStop_Click(object sender, System.EventArgs e)
  {
   this.StopAnimation();
  }
  private void buttonRewind_Click(object sender, System.EventArgs e)
  {
   if(IsSelectedChanged())
    StopAnimation();
   switch(this.animationState)
   {
    case AnimationState.Pending:
    case AnimationState.Paused:
     StartAnimation();
     this.animationState = AnimationState.PlayingReverse;
     break;
    case AnimationState.PlayingForward:
     this.animationState = AnimationState.PlayingReverse;
     this.CurrentFrameInterval = startFrameInterval;
     break;
    case AnimationState.PlayingReverse:
     AnimateFaster();
     break;
    default:
     break;
   }
  }
  private void buttonFastForward_Click(object sender, System.EventArgs e)
  {
   if(IsSelectedChanged())
    StopAnimation();
   switch(this.animationState)
   {
    case AnimationState.Pending:
    case AnimationState.Paused:
     StartAnimation();
     break;
    case AnimationState.PlayingForward:
     AnimateFaster();
     break;
    case AnimationState.PlayingReverse:
     this.animationState = AnimationState.PlayingForward;
     this.CurrentFrameInterval = startFrameInterval;
     break;
    default:
     break;
   }
  }
  private void buttonStepBack_Click(object sender, System.EventArgs e)
  {
   if(IsSelectedChanged())
    StopAnimation();
   switch(this.animationState)
   {
    case AnimationState.Pending:
    case AnimationState.Paused:
     PauseAnimation();
     this.displayFrame(this.currentAnimationFrame-1);
     break;
    case AnimationState.PlayingForward:
     this.animationState = AnimationState.PlayingReverse;
     this.CurrentFrameInterval = startFrameInterval;
     break;
    case AnimationState.PlayingReverse:
     AnimateSlower();
     break;
    default:
     break;
   }
  }
  private void buttonStepForward_Click(object sender, System.EventArgs e)
  {
   if(IsSelectedChanged())
    StopAnimation();
   switch(this.animationState)
   {
    case AnimationState.Pending:
    case AnimationState.Paused:
     PauseAnimation();
     this.displayFrame(this.currentAnimationFrame+1);
     break;
    case AnimationState.PlayingForward:
     AnimateSlower();
     break;
    case AnimationState.PlayingReverse:
     this.animationState = AnimationState.PlayingForward;
     this.CurrentFrameInterval = startFrameInterval;
     break;
    default:
     break;
   }
  }
  private void trackBarOpacity_Scroll(object sender, System.EventArgs e)
  {
   if(this.imageLayer != null)
   {
    this.imageLayer.UpdateOpacity((float)this.trackBarOpacity.Value / this.trackBarOpacity.Maximum);
    this.worldWindow.Invalidate();
   }
  }
  private void buttonColorBar_Click(object sender, System.EventArgs e)
  {
   if(this.currentlyAnimatingNode==null || this.currentlyAnimatingNode.Tag == null)
    return;
   if(colorbar == null)
   {
    colorbar = new Colorbar(this);
   }
   if(this.colorbar.Visible)
   {
    this.colorbar.Visible = false;
    return;
   }
   WMSLayer layer = (WMSLayer)this.currentlyAnimatingNode.Tag;
   if(layer.LegendUrl != null)
    colorbar.LoadImage(layer.LegendUrl);
   colorbar.Show();
  }
  private void treeViewLayers_DoubleClick(object sender, System.EventArgs e)
  {
   if (treeViewLayers.SelectedNode.Parent==null)
    return;
   buttonPlay_Click(sender,e);
  }
  private void treeViewLayers_AfterSelect(object sender, System.Windows.Forms.TreeViewEventArgs e)
  {
   if (e.Action==TreeViewAction.Unknown)
    return;
   if( this.animationState != AnimationState.Pending)
   {
    if(e.Node == this.currentlyAnimatingNode &&
     this.animationState != AnimationState.Paused)
     this.buttonPlay.ImageIndex = 2;
    else
     this.buttonPlay.ImageIndex = 4;
   }
   this.currentlySelectedLayer = (WMSLayer)e.Node.Tag;
   if(this.currentlySelectedLayer == null)
    return;
   float targetViewRange = (float)(4 * (this.currentlySelectedLayer.north - this.currentlySelectedLayer.south > this.currentlySelectedLayer.east - this.currentlySelectedLayer.west ? this.currentlySelectedLayer.north - this.currentlySelectedLayer.south : this.currentlySelectedLayer.east - this.currentlySelectedLayer.west));
   this.worldWindow.GotoLatLon(
    0.5f * (float)(this.currentlySelectedLayer.north + this.currentlySelectedLayer.south),
    0.5f * (float)(this.currentlySelectedLayer.west + this.currentlySelectedLayer.east),
    0,
    double.NaN,
    (targetViewRange > 180.0f ? 180.0f : targetViewRange),
    0);
   this.richTextBoxDescription.Text = this.currentlySelectedLayer.Title;
   if(this.currentlySelectedLayer.Name != null && this.currentlySelectedLayer.dates != null)
   {
    this.richTextBoxDescription.Text += "\n# Frames: " +
     (this.currentlySelectedLayer.dates.Length == 0 ? "1" : this.currentlySelectedLayer.dates.Length.ToString(CultureInfo.CurrentCulture));
   }
   if(this.currentlySelectedLayer.Abstract != null)
   {
    this.richTextBoxDescription.Text += "\n" + this.currentlySelectedLayer.Abstract.Replace("\n", " ");
   }
  }
  void StopDownloadThread()
  {
   if (downloadThread==null || !downloadThread.IsAlive)
    return;
   downloadThread.Abort();
  }
  private void Downloader()
  {
   while(true)
   {
    if(this.animationState == AnimationState.Pending)
    {
     Thread.Sleep(100);
     continue;
    }
    lock(this.downloadQueue)
    {
     if(this.downloadQueue.Count <= 0)
     {
      Thread.Sleep(100);
      continue;
     }
     this.wmsDownload = (WMSDownload)this.downloadQueue.Dequeue();
    }
    if(File.Exists(wmsDownload.SavedFilePath))
    {
     AddAnimationFrame(wmsDownload);
     return;
    }
    try
    {
     this.downloadState = DownloadState.Downloading;
     updateStatusBar( string.Format( CultureInfo.CurrentCulture,
      "Downloading {0} ({1}/{2})",
      wmsDownload.Date,
      this.animationFrames.Count+1,
      this.currentlySelectedLayer.AnimationFrameCount ) );
     using( WebDownload dl = new WebDownload( wmsDownload.Url) )
     {
      dl.DownloadType = DownloadType.Wms;
      dl.ProgressCallback += new DownloadProgressHandler(updateCurrentProgressBar);
      dl.DownloadFile(wmsDownload.SavedFilePath);
     }
     Download_CompleteCallback(wmsDownload);
    }
    catch(ThreadAbortException)
    {
    }
    catch(Exception caught)
    {
     updateStatusBar(caught.Message);
     return;
    }
   }
  }
  private void Download_CompleteCallback(WMSDownload wdl)
  {
   try
   {
    updateStatusBar( "Converting: " + wdl.Date );
    string srcPath = wdl.SavedFilePath;
    AddAnimationFrame(wdl);
    updateStatusBar( "" );
    this.downloadState = DownloadState.Pending;
   }
   catch(Exception caught)
   {
    this.PauseAnimation();
    this.worldWindow.Caption = caught.Message;
    updateStatusBar( caught.Message );
    Thread.Sleep(600);
   }
  }
  private void animationTimer_Elapsed(object sender, System.Timers.ElapsedEventArgs e)
  {
   try
   {
    if(this.animationState != AnimationState.Pending &&
     this.animationState != AnimationState.Paused && !this.isRendering)
    {
     this.isRendering = true;
     lock(this.animationFrames.SyncRoot)
     {
      if(this.animationState == AnimationState.PlayingForward)
       this.currentAnimationFrame++;
      else
       this.currentAnimationFrame--;
      this.displayFrame(this.currentAnimationFrame);
     }
    }
   }
   catch(Exception caught)
   {
    Utility.Log.Write(caught);
    if(this.imageLayer != null)
    {
     this.worldWindow.CurrentWorld.RenderableObjects.Remove(this.imageLayer.Name);
     this.imageLayer = null;
    }
   }
   this.isRendering = false;
  }
  protected override void OnKeyUp(System.Windows.Forms.KeyEventArgs e)
  {
   switch(e.KeyCode)
   {
    case Keys.L:
     if(e.Alt)
     {
      buttonColorBar_Click(this,e);
      e.Handled=true;
     }
     break;
    case Keys.Add:
    case Keys.Oemplus:
     if (animationState==AnimationState.Paused || animationState==AnimationState.Pending)
      buttonStepForward_Click(this,e);
     else
      AnimateFaster();
     break;
    case Keys.Subtract:
    case Keys.OemMinus:
     if (animationState==AnimationState.Paused || animationState==AnimationState.Pending)
      buttonStepBack_Click(this,e);
     else
      AnimateSlower();
     break;
    case Keys.Enter:
    case Keys.Play:
     buttonPlay_Click(this, null);
     e.Handled = true;
     break;
    case Keys.Up:
     buttonFastForward_Click(this,e);
     break;
    case Keys.Down:
     buttonRewind_Click(this,e);
     break;
    case Keys.Escape:
    case Keys.F1:
     Close();
     e.Handled = true;
     break;
    case Keys.F4:
     if(e.Modifiers==Keys.Control)
     {
      Close();
      e.Handled = true;
     }
     break;
   }
   base.OnKeyUp(e);
  }
  protected override void OnActivated(System.EventArgs e)
  {
   RefreshCapabilities();
   base.OnActivated(e);
  }
  protected override void OnClosing(System.ComponentModel.CancelEventArgs e)
  {
   e.Cancel=true;
   this.Reset();
   this.Hide();
   this.worldWindow.Focus();
   base.OnClosing(e);
  }
 }
 public class WMSLayer
 {
  public decimal north = 90.0m;
  public decimal south = -90.0m;
  public decimal east = 180.0m;
  public decimal west = -180.0m;
  public string Title;
  public string Abstract;
  public string Name;
  public uint Width = 1024;
  public uint Height = 512;
  public string crs;
  public string[] dates;
  public string LegendUrl;
  capabilities_1_3_0.wms.LayerType layer;
  public WMSLayer(capabilities_1_3_0.wms.LayerType layer)
  {
   this.layer = layer;
   this.Title = layer.Title.Value;
   if(layer.HasName())
    this.Name = layer.Name.Value;
   if(layer.HasAbstract2())
    this.Abstract = layer.Abstract2.Value;
   if(layer.HasCRS())
    this.crs = layer.CRS.Value;
   if(layer.HasfixedWidth())
    this.Width = (uint)layer.fixedWidth.Value;
   if(layer.HasfixedHeight())
    this.Height = (uint)layer.fixedHeight.Value;
   if(layer.HasEX_GeographicBoundingBox())
   {
    this.north = layer.EX_GeographicBoundingBox.northBoundLatitude.Value;
    this.south = layer.EX_GeographicBoundingBox.southBoundLatitude.Value;
    this.west = layer.EX_GeographicBoundingBox.westBoundLongitude.Value;
    this.east = layer.EX_GeographicBoundingBox.eastBoundLongitude.Value;
   }
   for(int i = 0; i < layer.DimensionCount; i++)
   {
    capabilities_1_3_0.wms.DimensionType dim = (capabilities_1_3_0.wms.DimensionType)layer.GetDimensionAt(i);
    if(String.Compare(dim.name.Value, "time", true,CultureInfo.InvariantCulture) == 0)
    {
     this.dates = WMSLayer.GetDatesFromDateTimeString(dim.Value.Value);
    }
   }
   if(layer.HasStyle())
   {
    for(int i = 0; i < layer.StyleCount; i++)
    {
     capabilities_1_3_0.wms.StyleType curStyle = (capabilities_1_3_0.wms.StyleType)layer.GetStyleAt(i);
     if(curStyle.HasLegendURL())
     {
      for(int j = 0; j < curStyle.LegendURLCount; j++)
      {
       capabilities_1_3_0.wms.LegendURLType curLegend = (capabilities_1_3_0.wms.LegendURLType)curStyle.GetLegendURLAt(j);
       if(curLegend.HasOnlineResource() && (curLegend.Format.Value.IndexOf("png") != -1 || curLegend.Format.Value.IndexOf("jpeg") != -1))
       {
        this.LegendUrl = curLegend.OnlineResource.href.Value;
        break;
       }
       else if(curLegend.HasOnlineResource() && this.LegendUrl == null)
       {
        this.LegendUrl = curLegend.OnlineResource.href.Value;
       }
      }
     }
    }
   }
  }
  public bool IsAnimation
  {
   get
   {
    return (dates!=null && dates.Length>1);
   }
  }
  public int AnimationFrameCount
  {
   get
   {
    if (dates==null)
     return 1;
    return (dates.Length);
   }
  }
  public static string[] GetDatesFromDateTimeString(string dateTime)
  {
   ArrayList dates = new ArrayList();
   string[] parsedTimeValues = dateTime.Split(',');
   foreach(string s in parsedTimeValues)
   {
    if(s.IndexOf("/") != -1)
     dates.AddRange( WMSLayer.GetTimeValuesFromTimePeriodString(s) );
    else
     dates.Add(s);
   }
   return (string[]) dates.ToArray(typeof(string));
  }
  public static ArrayList GetTimeValuesFromTimePeriodString(string timePeriod)
  {
   ArrayList dateList = new ArrayList();
   try
   {
    string[] timePeriodParts = timePeriod.Split('/');
    if(timePeriodParts.Length != 3)
     return null;
    DateTime startDate = WMSLayer.GetDateTimeFromWmsDate(timePeriodParts[0]);
    DateTime endDate = WMSLayer.GetDateTimeFromWmsDate(timePeriodParts[1]);
    string buffer = "";
    int deltaYears = 0;
    int deltaMonths = 0;
    int deltaDays = 0;
    double deltaHours = 0;
    double deltaMins = 0;
    double deltaSecs = 0;
    bool isTime = false;
    foreach( char code in timePeriodParts[2])
    {
     switch( code )
     {
      case 'Y':
       if(buffer.Length == 0)
        return null;
       deltaYears = Int32.Parse(buffer, CultureInfo.InvariantCulture);
       buffer = "";
       break;
      case 'M':
       if(buffer.Length == 0)
        return null;
       if(isTime)
       {
        deltaMins = Double.Parse(buffer, CultureInfo.InvariantCulture);
        buffer = "";
       }
       else
       {
        deltaMonths = Int32.Parse(buffer, CultureInfo.InvariantCulture);
        buffer = "";
       }
       break;
      case 'D':
       if(buffer.Length == 0)
        return null;
       deltaDays = Int32.Parse(buffer, CultureInfo.InvariantCulture);
       buffer = "";
       break;
      case 'H':
       if(buffer.Length == 0)
        return null;
       deltaHours = Double.Parse(buffer, CultureInfo.InvariantCulture);
       buffer = "";
       break;
      case 'S':
       if(buffer.Length == 0)
        return null;
       deltaSecs = Double.Parse(buffer, CultureInfo.InvariantCulture);
       buffer = "";
       break;
      case 'T':
       isTime = true;
       break;
      default:
       if(code != 'P')
        buffer += code;
       break;
     }
    }
    while(startDate <= endDate)
    {
     int length = timePeriodParts[0].Length;
     string dateFormat = null;
     if (length < 8)
      dateFormat = "yyyy-MM";
     else if (length < 11 )
      dateFormat = "yyyy-MM-dd";
     else if (length < 18 )
      dateFormat = "yyyy-MM-ddTHH:mm";
     else
      dateFormat += "yyyy-MM-ddTHH:mm:ss";
     string curDate = null;
     curDate = startDate.ToString( dateFormat + "Z", CultureInfo.InvariantCulture);
     dateList.Add(curDate);
     startDate = startDate.AddYears(deltaYears);
     startDate = startDate.AddMonths(deltaMonths);
     startDate = startDate.AddDays(deltaDays);
     startDate = startDate.AddHours(deltaHours);
     startDate = startDate.AddMinutes(deltaMins);
     startDate = startDate.AddSeconds(deltaSecs);
    }
   }
   catch
   {
   }
   return dateList;
  }
  public static DateTime GetDateTimeFromWmsDate(string wmsDate)
  {
   string[] formats = new string[] {
                    "s",
                    "yyyy-",
                    "yyyy-MM",
                    "yyyy-MM-dd",
                    "yyyy-MM-ddTHH:mm",
                    "yyyy-MM-ddTHH"
               };
   DateTime result = DateTime.ParseExact(wmsDate.TrimEnd('Z'), formats, null, DateTimeStyles.AdjustToUniversal);
   if(result.Year == 9999)
    result = result.AddYears( 1900 - result.Year );
   return result;
  }
 }
 public enum AnimationState
 {
  Pending,
  PlayingForward,
  PlayingReverse,
  Paused,
 }
 public enum DownloadState
 {
  Pending,
  Downloading,
  Cancel,
 }
}
