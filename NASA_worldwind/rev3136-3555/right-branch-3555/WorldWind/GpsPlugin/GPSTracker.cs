using System.Globalization;
using System.ComponentModel;
using System.Threading;
using System.Windows.Forms;
using System;
using System.IO;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using System.Runtime.InteropServices;
using WorldWind;
using WorldWind.Renderable;
using WorldWind.PluginEngine;
using System.Net;
using System.Net.Sockets;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System.Drawing;
using System.Xml;
using System.Data;
using System.Collections;
using System.Diagnostics;
using Org.Mentalis.Security.Ssl;
using System.Text;
namespace GpsTrackerPlugin
{
 public class GpsTracker : System.Windows.Forms.Form
 {
  public static int m_iMaxDevices =512;
  public GpsTrackerPlugin m_gpsTrackerPlugin;
  public GpsTrackerNMEA m_NMEA;
  private GpsTrackerUDPTCP m_UDPTCP;
  public GpsTrackerFile m_File;
  public GpsTrackerAPRS m_APRS;
  public ArrayList m_gpsSourceList = new ArrayList();
  public MessageMonitor m_MessageMonitor;
  private TreeNode m_treeNodeCOM;
  private TreeNode m_treeNodeUDP;
  private TreeNode m_treeNodeTCP;
  private TreeNode m_treeNodeFile;
  private TreeNode m_treeNodeAPRS;
  private TreeNode m_treeNodePOI;
  private Object LockPOI = new Object();
  private Object LockShowIcon = new Object();
  private Object LockCOM = new Object();
  private bool m_bHandleControlValueChangeEvent=false;
  private int m_iSourceNameCount;
  private float m_fVerticalExaggeration;
  public bool m_bTrackHeading;
  public bool m_bTrackLine;
  public bool m_bRecordSession;
  public bool m_bInfoText;
  private StreamWriter m_swRecorder;
  private StreamReader m_srReader;
  public bool m_fPlayback;
  private String m_sPlaybackFile;
  public int m_iPlaybackSpeed;
  private Thread m_hPOIThread;
  public bool m_fCloseThreads;
  private int m_iLocked;
  private int m_iPrevLocked;
  System.Threading.Timer m_timerLocked;
  private System.Windows.Forms.Button StartStop;
  private System.Windows.Forms.Label labelTrackCode;
  private System.ComponentModel.IContainer components;
  private bool m_fInitialized=false;
  private System.Windows.Forms.ProgressBar progressBarSetup;
  private System.Windows.Forms.Label labelSettingup;
  private System.Windows.Forms.ColorDialog colorPicker;
  private System.Windows.Forms.ImageList imageListGpsIcons;
  private System.Windows.Forms.GroupBox groupBox2;
  private System.Windows.Forms.PictureBox pictureBoxLogo;
  private System.Windows.Forms.TabControl tabControlGPS;
  private System.Windows.Forms.TabPage tabPageCOM;
  private System.Windows.Forms.Label label23;
  private System.Windows.Forms.Button buttonTrackColorCOM;
  private System.Windows.Forms.Label label20;
  private System.Windows.Forms.ComboBox comboBoxFlowControl;
  private System.Windows.Forms.Button buttonAutoDetect;
  private System.Windows.Forms.ProgressBar progressBarAutoDetect;
  private System.Windows.Forms.Label label5;
  private System.Windows.Forms.Label label4;
  private System.Windows.Forms.Label label3;
  private System.Windows.Forms.Label label2;
  private System.Windows.Forms.Label label1;
  private System.Windows.Forms.ComboBox comboBoxStopBits;
  private System.Windows.Forms.ComboBox comboParity;
  private System.Windows.Forms.ComboBox comboBoxByteSize;
  private System.Windows.Forms.ComboBox comboBoxBaudRate;
  private System.Windows.Forms.ComboBox comboBoxCOMPort;
  private System.Windows.Forms.TabPage tabPageUDP;
  private System.Windows.Forms.Label label21;
  private System.Windows.Forms.Button buttonTrackColorUDP;
  private System.Windows.Forms.Label label6;
  private System.Windows.Forms.NumericUpDown numericUpDownUDPPort;
  private System.Windows.Forms.TabPage tabPageTCP;
  private System.Windows.Forms.Label label22;
  private System.Windows.Forms.Button buttonTrackColorTCP;
  private System.Windows.Forms.Label label8;
  private System.Windows.Forms.Label label7;
  private System.Windows.Forms.NumericUpDown numericUpDownTCPPort;
  private System.Windows.Forms.TabPage tabPageFile;
  private System.Windows.Forms.Label label19;
  private System.Windows.Forms.Button buttonTrackColor;
  private System.Windows.Forms.CheckBox checkBoxForcePreprocessing;
  private System.Windows.Forms.Label labelPreprocessing;
  private System.Windows.Forms.ProgressBar progressBarPreprocessing;
  private System.Windows.Forms.CheckBox checkBoxTrackAtOnce;
  private System.Windows.Forms.CheckBox checkBoxNoDelay;
  private System.Windows.Forms.ComboBox comboBoxFile;
  private System.Windows.Forms.Label label13;
  private System.Windows.Forms.Label label12;
  private System.Windows.Forms.TrackBar trackBarFileSpeed;
  private System.Windows.Forms.Button buttonBrowseGpsFile;
  private System.Windows.Forms.Label label9;
  private System.Windows.Forms.Label label14;
  private System.Windows.Forms.TabPage tabPageUpdate;
  private System.Windows.Forms.Label label11;
  private System.Windows.Forms.TextBox textBoxVersionInfo;
  private System.Windows.Forms.Button buttonCheckForUpdates;
  private System.Windows.Forms.TabPage tabPageGeneral;
  private System.Windows.Forms.Label label32;
  private System.Windows.Forms.Label label33;
  private System.Windows.Forms.Label label30;
  private System.Windows.Forms.Label label18;
  private System.Windows.Forms.Label label31;
  private System.Windows.Forms.Label label17;
  private System.Windows.Forms.GroupBox groupBox3;
  private System.Windows.Forms.GroupBox groupBox1;
  private System.Windows.Forms.CheckBox checkBoxRecordSession;
  private System.Windows.Forms.Label label16;
  private System.Windows.Forms.Label label15;
  private System.Windows.Forms.Label label10;
  private System.Windows.Forms.CheckBox checkBoxTrackLine;
  private System.Windows.Forms.CheckBox checkBoxInformationText;
  private System.Windows.Forms.CheckBox checkBoxTrackHeading;
  private System.Windows.Forms.CheckBox checkBoxVExaggeration;
  private System.Windows.Forms.TabPage tabPageAPRS;
  private System.Windows.Forms.TabPage tabPageUsage;
  private System.Windows.Forms.TextBox textBoxUsage;
  private System.Windows.Forms.TreeView treeViewSources;
  private System.Windows.Forms.ContextMenu contextMenuSourceTree;
  private System.Windows.Forms.MenuItem menuItemAdd;
  private System.Windows.Forms.MenuItem menuItemRename;
  private System.Windows.Forms.MenuItem menuItemSetIcon;
  private System.Windows.Forms.MenuItem menuItemDelete;
  private System.Windows.Forms.MenuItem menuItemSetTrack;
  private System.Windows.Forms.Button buttonApply;
  private System.Windows.Forms.Button buttonApplyUDP;
  private System.Windows.Forms.Button buttonApplyTCP;
  private System.Windows.Forms.TabPage tabPageAPRSInternet;
  private System.Windows.Forms.Label label27;
  private System.Windows.Forms.Label label26;
  private System.Windows.Forms.Label label24;
  private System.Windows.Forms.Button buttonTrackColorAPRS;
  private System.Windows.Forms.TextBox textBoxAPRSISCallSign;
  private System.Windows.Forms.NumericUpDown numericUpDownAPRSIS;
  private System.Windows.Forms.Button buttonApplyAPRSInternet;
  private System.Windows.Forms.TextBox textBox1;
  private System.Windows.Forms.TextBox textBoxCallSignFilter;
  private System.Windows.Forms.TabPage tabPagePOI;
  private System.Windows.Forms.Label label25;
  private System.Windows.Forms.Label label29;
  private System.Windows.Forms.Label label34;
  private System.Windows.Forms.TextBox textBoxLatitud;
  private System.Windows.Forms.TextBox textBoxLongitud;
  private System.Windows.Forms.Button buttonApplyPOI;
  private System.Windows.Forms.TabPage tabPageCOMHelp;
  private System.Windows.Forms.TabPage tabPagePOIHelp;
  private System.Windows.Forms.TabPage tabPageAPRSInternetHelp;
  private System.Windows.Forms.TabPage tabPageFileHelp;
  private System.Windows.Forms.TabPage tabPageTCPHelp;
  private System.Windows.Forms.TabPage tabPageUDPHelp;
  private System.Windows.Forms.TextBox textBox2;
  private System.Windows.Forms.TextBox textBox3;
  private System.Windows.Forms.TextBox textBox4;
  private System.Windows.Forms.TextBox textBox5;
  private System.Windows.Forms.TextBox textBox6;
  private System.Windows.Forms.TextBox textBox7;
  private System.Windows.Forms.PictureBox pictureBoxHelpLogo;
  private System.Windows.Forms.Button buttonApplyAPRSFilter;
  private System.Windows.Forms.CheckBox checkBoxSecureSocket;
  private System.Windows.Forms.GroupBox groupBox4;
  private System.Windows.Forms.CheckBox checkBoxMessagesMonitor;
  private System.Windows.Forms.Label label35;
  private System.Windows.Forms.GroupBox groupBox5;
  private System.Windows.Forms.NumericUpDown numericUpDownReload;
  private System.Windows.Forms.Label label36;
  private System.Windows.Forms.Label label37;
  private System.Windows.Forms.Label label38;
  private System.Windows.Forms.GroupBox groupBox6;
  private System.Windows.Forms.NumericUpDown numericUpDownAltitud;
  private System.Windows.Forms.Label label39;
  private System.Windows.Forms.CheckBox checkBoxSetAltitud;
  private System.Windows.Forms.Label label40;
  private System.Windows.Forms.ComboBox comboBoxAPRSInternetServer;
  private System.Windows.Forms.ComboBox comboBoxTcpIP;
  private System.Windows.Forms.Button buttonApplyFile;
  public GpsTracker(GpsTrackerPlugin gpsPlugin)
  {
   m_gpsTrackerPlugin = gpsPlugin;
   InitializeComponent();
   m_iSourceNameCount=0;
   SetupTree();
   Bitmap image = new Bitmap(GpsTrackerPlugin.m_sPluginDirectory + "\\satellite.png");
   pictureBoxLogo.Image = image;
   pictureBoxHelpLogo.Image= image;
            m_gpsSourceList.Clear();
   m_NMEA = new GpsTrackerNMEA(this);
   m_UDPTCP = new GpsTrackerUDPTCP(this);
   m_File = new GpsTrackerFile(this);
   m_APRS = new GpsTrackerAPRS(this);
   m_fVerticalExaggeration=World.Settings.VerticalExaggeration;
   m_timerLocked=null;
   m_hPOIThread=null;
   m_iLocked=0;
   m_iPrevLocked=0;
   progressBarAutoDetect.Value=0;
   progressBarAutoDetect.Width=0;
   StartStop.Enabled=false;
   LoadSettings(null,true);
   m_swRecorder=null;
   m_srReader=null;
   m_fPlayback=false;
   textBoxVersionInfo.Text="Your Version: " + m_gpsTrackerPlugin.m_sVersion;
   m_MessageMonitor = null;
   SetDefaultSettings(true);
  }
  protected override void OnLoad(EventArgs e)
  {
   base.OnLoad(e);
  }
  protected override void OnVisibleChanged(EventArgs e)
  {
   try
   {
   if (this.Visible==false)
    Deinitialize();
   else
   {
    this.Left=100;
    this.Top=100;
    labelTrackCode.Text="";
    m_gpsTrackerPlugin.m_fGpsTrackerRunning=true;
    LoadSettings(null,true);
   }
   base.OnVisibleChanged (e);
   }
   catch(Exception)
   {
   }
  }
  protected override void OnClosing(CancelEventArgs e)
  {
   try
   {
   e.Cancel = true;
   this.Hide();
   base.OnClosing(e);
   }
   catch(Exception)
   {
   }
  }
  protected override void Dispose( bool disposing )
  {
   try
   {
   base.Dispose( disposing );
   }
   catch(Exception)
   {
   }
  }
  private void GpsTracker_Load(object sender, System.EventArgs e)
  {
   this.Text = "GPSTracker :: Version: " + m_gpsTrackerPlugin.m_sVersion;
   m_gpsTrackerPlugin.m_fGpsTrackerRunning=true;
            m_gpsSourceList.Clear();
  }
  private void GpsTracker_Closing(object sender, System.ComponentModel.CancelEventArgs e)
  {
   Deinitialize();
   m_gpsTrackerPlugin.pluginWorldWindowFocus();
   m_gpsTrackerPlugin.m_fGpsTrackerRunning=false;
  }
  private void InitializeComponent()
  {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(GpsTracker));
            this.StartStop = new System.Windows.Forms.Button();
            this.imageListGpsIcons = new System.Windows.Forms.ImageList(this.components);
            this.labelTrackCode = new System.Windows.Forms.Label();
            this.progressBarSetup = new System.Windows.Forms.ProgressBar();
            this.labelSettingup = new System.Windows.Forms.Label();
            this.colorPicker = new System.Windows.Forms.ColorDialog();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.pictureBoxLogo = new System.Windows.Forms.PictureBox();
            this.treeViewSources = new System.Windows.Forms.TreeView();
            this.contextMenuSourceTree = new System.Windows.Forms.ContextMenu();
            this.menuItemAdd = new System.Windows.Forms.MenuItem();
            this.menuItemRename = new System.Windows.Forms.MenuItem();
            this.menuItemSetIcon = new System.Windows.Forms.MenuItem();
            this.menuItemSetTrack = new System.Windows.Forms.MenuItem();
            this.menuItemDelete = new System.Windows.Forms.MenuItem();
            this.tabControlGPS = new System.Windows.Forms.TabControl();
            this.tabPageCOM = new System.Windows.Forms.TabPage();
            this.buttonApply = new System.Windows.Forms.Button();
            this.label23 = new System.Windows.Forms.Label();
            this.buttonTrackColorCOM = new System.Windows.Forms.Button();
            this.label20 = new System.Windows.Forms.Label();
            this.comboBoxFlowControl = new System.Windows.Forms.ComboBox();
            this.buttonAutoDetect = new System.Windows.Forms.Button();
            this.progressBarAutoDetect = new System.Windows.Forms.ProgressBar();
            this.label5 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.comboBoxStopBits = new System.Windows.Forms.ComboBox();
            this.comboParity = new System.Windows.Forms.ComboBox();
            this.comboBoxByteSize = new System.Windows.Forms.ComboBox();
            this.comboBoxBaudRate = new System.Windows.Forms.ComboBox();
            this.comboBoxCOMPort = new System.Windows.Forms.ComboBox();
            this.tabPageGeneral = new System.Windows.Forms.TabPage();
            this.checkBoxSetAltitud = new System.Windows.Forms.CheckBox();
            this.label39 = new System.Windows.Forms.Label();
            this.groupBox6 = new System.Windows.Forms.GroupBox();
            this.label38 = new System.Windows.Forms.Label();
            this.numericUpDownAltitud = new System.Windows.Forms.NumericUpDown();
            this.label35 = new System.Windows.Forms.Label();
            this.groupBox4 = new System.Windows.Forms.GroupBox();
            this.checkBoxMessagesMonitor = new System.Windows.Forms.CheckBox();
            this.label32 = new System.Windows.Forms.Label();
            this.label33 = new System.Windows.Forms.Label();
            this.label30 = new System.Windows.Forms.Label();
            this.label18 = new System.Windows.Forms.Label();
            this.label31 = new System.Windows.Forms.Label();
            this.label17 = new System.Windows.Forms.Label();
            this.groupBox3 = new System.Windows.Forms.GroupBox();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.checkBoxRecordSession = new System.Windows.Forms.CheckBox();
            this.label16 = new System.Windows.Forms.Label();
            this.label15 = new System.Windows.Forms.Label();
            this.label10 = new System.Windows.Forms.Label();
            this.checkBoxTrackLine = new System.Windows.Forms.CheckBox();
            this.checkBoxInformationText = new System.Windows.Forms.CheckBox();
            this.checkBoxTrackHeading = new System.Windows.Forms.CheckBox();
            this.checkBoxVExaggeration = new System.Windows.Forms.CheckBox();
            this.tabPageFileHelp = new System.Windows.Forms.TabPage();
            this.textBox5 = new System.Windows.Forms.TextBox();
            this.tabPageAPRSInternetHelp = new System.Windows.Forms.TabPage();
            this.textBox4 = new System.Windows.Forms.TextBox();
            this.tabPageUsage = new System.Windows.Forms.TabPage();
            this.pictureBoxHelpLogo = new System.Windows.Forms.PictureBox();
            this.textBoxUsage = new System.Windows.Forms.TextBox();
            this.tabPageTCP = new System.Windows.Forms.TabPage();
            this.comboBoxTcpIP = new System.Windows.Forms.ComboBox();
            this.checkBoxSecureSocket = new System.Windows.Forms.CheckBox();
            this.buttonApplyTCP = new System.Windows.Forms.Button();
            this.label22 = new System.Windows.Forms.Label();
            this.buttonTrackColorTCP = new System.Windows.Forms.Button();
            this.label8 = new System.Windows.Forms.Label();
            this.label7 = new System.Windows.Forms.Label();
            this.numericUpDownTCPPort = new System.Windows.Forms.NumericUpDown();
            this.tabPageFile = new System.Windows.Forms.TabPage();
            this.label37 = new System.Windows.Forms.Label();
            this.label36 = new System.Windows.Forms.Label();
            this.numericUpDownReload = new System.Windows.Forms.NumericUpDown();
            this.groupBox5 = new System.Windows.Forms.GroupBox();
            this.buttonApplyFile = new System.Windows.Forms.Button();
            this.label19 = new System.Windows.Forms.Label();
            this.buttonTrackColor = new System.Windows.Forms.Button();
            this.checkBoxForcePreprocessing = new System.Windows.Forms.CheckBox();
            this.labelPreprocessing = new System.Windows.Forms.Label();
            this.progressBarPreprocessing = new System.Windows.Forms.ProgressBar();
            this.checkBoxTrackAtOnce = new System.Windows.Forms.CheckBox();
            this.checkBoxNoDelay = new System.Windows.Forms.CheckBox();
            this.comboBoxFile = new System.Windows.Forms.ComboBox();
            this.label13 = new System.Windows.Forms.Label();
            this.label12 = new System.Windows.Forms.Label();
            this.trackBarFileSpeed = new System.Windows.Forms.TrackBar();
            this.buttonBrowseGpsFile = new System.Windows.Forms.Button();
            this.label9 = new System.Windows.Forms.Label();
            this.label14 = new System.Windows.Forms.Label();
            this.tabPageUDPHelp = new System.Windows.Forms.TabPage();
            this.textBox7 = new System.Windows.Forms.TextBox();
            this.tabPageAPRSInternet = new System.Windows.Forms.TabPage();
            this.label40 = new System.Windows.Forms.Label();
            this.comboBoxAPRSInternetServer = new System.Windows.Forms.ComboBox();
            this.buttonApplyAPRSInternet = new System.Windows.Forms.Button();
            this.label26 = new System.Windows.Forms.Label();
            this.label24 = new System.Windows.Forms.Label();
            this.buttonTrackColorAPRS = new System.Windows.Forms.Button();
            this.textBoxAPRSISCallSign = new System.Windows.Forms.TextBox();
            this.numericUpDownAPRSIS = new System.Windows.Forms.NumericUpDown();
            this.label27 = new System.Windows.Forms.Label();
            this.tabPageCOMHelp = new System.Windows.Forms.TabPage();
            this.textBox2 = new System.Windows.Forms.TextBox();
            this.tabPagePOIHelp = new System.Windows.Forms.TabPage();
            this.textBox3 = new System.Windows.Forms.TextBox();
            this.tabPageTCPHelp = new System.Windows.Forms.TabPage();
            this.textBox6 = new System.Windows.Forms.TextBox();
            this.tabPageAPRS = new System.Windows.Forms.TabPage();
            this.buttonApplyAPRSFilter = new System.Windows.Forms.Button();
            this.textBox1 = new System.Windows.Forms.TextBox();
            this.textBoxCallSignFilter = new System.Windows.Forms.TextBox();
            this.tabPageUDP = new System.Windows.Forms.TabPage();
            this.buttonApplyUDP = new System.Windows.Forms.Button();
            this.label21 = new System.Windows.Forms.Label();
            this.buttonTrackColorUDP = new System.Windows.Forms.Button();
            this.label6 = new System.Windows.Forms.Label();
            this.numericUpDownUDPPort = new System.Windows.Forms.NumericUpDown();
            this.tabPageUpdate = new System.Windows.Forms.TabPage();
            this.label11 = new System.Windows.Forms.Label();
            this.textBoxVersionInfo = new System.Windows.Forms.TextBox();
            this.buttonCheckForUpdates = new System.Windows.Forms.Button();
            this.tabPagePOI = new System.Windows.Forms.TabPage();
            this.buttonApplyPOI = new System.Windows.Forms.Button();
            this.label34 = new System.Windows.Forms.Label();
            this.label29 = new System.Windows.Forms.Label();
            this.label25 = new System.Windows.Forms.Label();
            this.textBoxLongitud = new System.Windows.Forms.TextBox();
            this.textBoxLatitud = new System.Windows.Forms.TextBox();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBoxLogo)).BeginInit();
            this.tabControlGPS.SuspendLayout();
            this.tabPageCOM.SuspendLayout();
            this.tabPageGeneral.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownAltitud)).BeginInit();
            this.tabPageFileHelp.SuspendLayout();
            this.tabPageAPRSInternetHelp.SuspendLayout();
            this.tabPageUsage.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBoxHelpLogo)).BeginInit();
            this.tabPageTCP.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownTCPPort)).BeginInit();
            this.tabPageFile.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownReload)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.trackBarFileSpeed)).BeginInit();
            this.tabPageUDPHelp.SuspendLayout();
            this.tabPageAPRSInternet.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownAPRSIS)).BeginInit();
            this.tabPageCOMHelp.SuspendLayout();
            this.tabPagePOIHelp.SuspendLayout();
            this.tabPageTCPHelp.SuspendLayout();
            this.tabPageAPRS.SuspendLayout();
            this.tabPageUDP.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownUDPPort)).BeginInit();
            this.tabPageUpdate.SuspendLayout();
            this.tabPagePOI.SuspendLayout();
            this.SuspendLayout();
            this.StartStop.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.StartStop.Location = new System.Drawing.Point(472, 288);
            this.StartStop.Name = "StartStop";
            this.StartStop.Size = new System.Drawing.Size(56, 49);
            this.StartStop.TabIndex = 0;
            this.StartStop.Text = "Track";
            this.StartStop.Click += new System.EventHandler(this.StartStop_Click);
            this.imageListGpsIcons.ColorDepth = System.Windows.Forms.ColorDepth.Depth8Bit;
            this.imageListGpsIcons.ImageSize = new System.Drawing.Size(16, 16);
            this.imageListGpsIcons.TransparentColor = System.Drawing.Color.Transparent;
            this.labelTrackCode.BackColor = System.Drawing.SystemColors.Control;
            this.labelTrackCode.ImageAlign = System.Drawing.ContentAlignment.TopCenter;
            this.labelTrackCode.Location = new System.Drawing.Point(72, 288);
            this.labelTrackCode.Name = "labelTrackCode";
            this.labelTrackCode.Size = new System.Drawing.Size(392, 27);
            this.labelTrackCode.TabIndex = 3;
            this.progressBarSetup.Location = new System.Drawing.Point(196, 319);
            this.progressBarSetup.Name = "progressBarSetup";
            this.progressBarSetup.Size = new System.Drawing.Size(268, 16);
            this.progressBarSetup.Step = 1;
            this.progressBarSetup.TabIndex = 5;
            this.labelSettingup.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.labelSettingup.Location = new System.Drawing.Point(88, 311);
            this.labelSettingup.Name = "labelSettingup";
            this.labelSettingup.Size = new System.Drawing.Size(96, 24);
            this.labelSettingup.TabIndex = 31;
            this.labelSettingup.Text = "Setting up...";
            this.labelSettingup.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.groupBox2.Location = new System.Drawing.Point(-24, 272);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(848, 8);
            this.groupBox2.TabIndex = 35;
            this.groupBox2.TabStop = false;
            this.pictureBoxLogo.Location = new System.Drawing.Point(8, 283);
            this.pictureBoxLogo.Name = "pictureBoxLogo";
            this.pictureBoxLogo.Size = new System.Drawing.Size(56, 56);
            this.pictureBoxLogo.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.pictureBoxLogo.TabIndex = 36;
            this.pictureBoxLogo.TabStop = false;
            this.treeViewSources.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.treeViewSources.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.treeViewSources.HideSelection = false;
            this.treeViewSources.ImageIndex = 0;
            this.treeViewSources.ImageList = this.imageListGpsIcons;
            this.treeViewSources.Indent = 15;
            this.treeViewSources.Location = new System.Drawing.Point(0, 0);
            this.treeViewSources.Name = "treeViewSources";
            this.treeViewSources.SelectedImageIndex = 0;
            this.treeViewSources.Size = new System.Drawing.Size(184, 272);
            this.treeViewSources.TabIndex = 39;
            this.treeViewSources.AfterLabelEdit += new System.Windows.Forms.NodeLabelEditEventHandler(this.treeViewSources_AfterLabelEdit);
            this.treeViewSources.MouseUp += new System.Windows.Forms.MouseEventHandler(this.treeViewSources_MouseUp);
            this.treeViewSources.MouseDown += new System.Windows.Forms.MouseEventHandler(this.treeViewSources_MouseDown);
            this.contextMenuSourceTree.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItemAdd,
            this.menuItemRename,
            this.menuItemSetIcon,
            this.menuItemSetTrack,
            this.menuItemDelete});
            this.menuItemAdd.Index = 0;
            this.menuItemAdd.Text = "Add";
            this.menuItemAdd.Click += new System.EventHandler(this.menuItemAdd_Click);
            this.menuItemRename.Index = 1;
            this.menuItemRename.Text = "Rename";
            this.menuItemRename.Click += new System.EventHandler(this.menuItemRename_Click);
            this.menuItemSetIcon.Index = 2;
            this.menuItemSetIcon.Text = "Icon";
            this.menuItemSetIcon.Click += new System.EventHandler(this.menuItemSetIcon_Click);
            this.menuItemSetTrack.Index = 3;
            this.menuItemSetTrack.Text = "Track";
            this.menuItemSetTrack.Click += new System.EventHandler(this.menuItemSetTrack_Click);
            this.menuItemDelete.Index = 4;
            this.menuItemDelete.Text = "Delete";
            this.menuItemDelete.Click += new System.EventHandler(this.menuItemDelete_Click);
            this.tabControlGPS.Controls.Add(this.tabPageCOM);
            this.tabControlGPS.Controls.Add(this.tabPageGeneral);
            this.tabControlGPS.Controls.Add(this.tabPageFileHelp);
            this.tabControlGPS.Controls.Add(this.tabPageAPRSInternetHelp);
            this.tabControlGPS.Controls.Add(this.tabPageUsage);
            this.tabControlGPS.Controls.Add(this.tabPageTCP);
            this.tabControlGPS.Controls.Add(this.tabPageFile);
            this.tabControlGPS.Controls.Add(this.tabPageUDPHelp);
            this.tabControlGPS.Controls.Add(this.tabPageAPRSInternet);
            this.tabControlGPS.Controls.Add(this.tabPageCOMHelp);
            this.tabControlGPS.Controls.Add(this.tabPagePOIHelp);
            this.tabControlGPS.Controls.Add(this.tabPageTCPHelp);
            this.tabControlGPS.Controls.Add(this.tabPageAPRS);
            this.tabControlGPS.Controls.Add(this.tabPageUDP);
            this.tabControlGPS.Controls.Add(this.tabPageUpdate);
            this.tabControlGPS.Controls.Add(this.tabPagePOI);
            this.tabControlGPS.Location = new System.Drawing.Point(184, 0);
            this.tabControlGPS.Name = "tabControlGPS";
            this.tabControlGPS.SelectedIndex = 0;
            this.tabControlGPS.Size = new System.Drawing.Size(352, 272);
            this.tabControlGPS.TabIndex = 40;
            this.tabPageCOM.Controls.Add(this.buttonApply);
            this.tabPageCOM.Controls.Add(this.label23);
            this.tabPageCOM.Controls.Add(this.buttonTrackColorCOM);
            this.tabPageCOM.Controls.Add(this.label20);
            this.tabPageCOM.Controls.Add(this.comboBoxFlowControl);
            this.tabPageCOM.Controls.Add(this.buttonAutoDetect);
            this.tabPageCOM.Controls.Add(this.progressBarAutoDetect);
            this.tabPageCOM.Controls.Add(this.label5);
            this.tabPageCOM.Controls.Add(this.label4);
            this.tabPageCOM.Controls.Add(this.label3);
            this.tabPageCOM.Controls.Add(this.label2);
            this.tabPageCOM.Controls.Add(this.label1);
            this.tabPageCOM.Controls.Add(this.comboBoxStopBits);
            this.tabPageCOM.Controls.Add(this.comboParity);
            this.tabPageCOM.Controls.Add(this.comboBoxByteSize);
            this.tabPageCOM.Controls.Add(this.comboBoxBaudRate);
            this.tabPageCOM.Controls.Add(this.comboBoxCOMPort);
            this.tabPageCOM.Location = new System.Drawing.Point(4, 22);
            this.tabPageCOM.Name = "tabPageCOM";
            this.tabPageCOM.Size = new System.Drawing.Size(344, 246);
            this.tabPageCOM.TabIndex = 0;
            this.tabPageCOM.Text = "COM";
            this.buttonApply.Location = new System.Drawing.Point(280, 216);
            this.buttonApply.Name = "buttonApply";
            this.buttonApply.Size = new System.Drawing.Size(56, 24);
            this.buttonApply.TabIndex = 46;
            this.buttonApply.Text = "Apply";
            this.buttonApply.Click += new System.EventHandler(this.buttonApply_Click);
            this.label23.Location = new System.Drawing.Point(32, 168);
            this.label23.Name = "label23";
            this.label23.Size = new System.Drawing.Size(96, 16);
            this.label23.TabIndex = 45;
            this.label23.Text = "Track Color:";
            this.label23.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.buttonTrackColorCOM.Location = new System.Drawing.Point(136, 168);
            this.buttonTrackColorCOM.Name = "buttonTrackColorCOM";
            this.buttonTrackColorCOM.Size = new System.Drawing.Size(128, 13);
            this.buttonTrackColorCOM.TabIndex = 44;
            this.buttonTrackColorCOM.Click += new System.EventHandler(this.button3_Click);
            this.buttonTrackColorCOM.BackColorChanged += new System.EventHandler(this.ControlValueChanged);
            this.label20.Location = new System.Drawing.Point(24, 136);
            this.label20.Name = "label20";
            this.label20.Size = new System.Drawing.Size(104, 24);
            this.label20.TabIndex = 25;
            this.label20.Text = "Flow Control:";
            this.label20.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.comboBoxFlowControl.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboBoxFlowControl.ItemHeight = 13;
            this.comboBoxFlowControl.Items.AddRange(new object[] {
            "None",
            "Hardware",
            "Software (XOn|XOff)"});
            this.comboBoxFlowControl.Location = new System.Drawing.Point(136, 136);
            this.comboBoxFlowControl.Name = "comboBoxFlowControl";
            this.comboBoxFlowControl.Size = new System.Drawing.Size(128, 21);
            this.comboBoxFlowControl.TabIndex = 24;
            this.comboBoxFlowControl.SelectedIndexChanged += new System.EventHandler(this.ControlValueChanged);
            this.buttonAutoDetect.Location = new System.Drawing.Point(136, 208);
            this.buttonAutoDetect.Name = "buttonAutoDetect";
            this.buttonAutoDetect.Size = new System.Drawing.Size(48, 24);
            this.buttonAutoDetect.TabIndex = 22;
            this.buttonAutoDetect.Text = "Auto Detect";
            this.buttonAutoDetect.Visible = false;
            this.progressBarAutoDetect.Cursor = System.Windows.Forms.Cursors.Default;
            this.progressBarAutoDetect.Location = new System.Drawing.Point(16, 208);
            this.progressBarAutoDetect.Maximum = 32;
            this.progressBarAutoDetect.Name = "progressBarAutoDetect";
            this.progressBarAutoDetect.Size = new System.Drawing.Size(112, 24);
            this.progressBarAutoDetect.Step = 1;
            this.progressBarAutoDetect.TabIndex = 23;
            this.label5.Location = new System.Drawing.Point(56, 112);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(72, 24);
            this.label5.TabIndex = 21;
            this.label5.Text = "Stop Bits:";
            this.label5.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.label4.Location = new System.Drawing.Point(56, 88);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(72, 24);
            this.label4.TabIndex = 20;
            this.label4.Text = "Parity:";
            this.label4.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.label3.Location = new System.Drawing.Point(56, 64);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(72, 24);
            this.label3.TabIndex = 19;
            this.label3.Text = "Data Bits:";
            this.label3.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.label2.Location = new System.Drawing.Point(56, 40);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(72, 24);
            this.label2.TabIndex = 18;
            this.label2.Text = "Baud Rate:";
            this.label2.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.label1.Location = new System.Drawing.Point(56, 16);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(72, 24);
            this.label1.TabIndex = 17;
            this.label1.Text = "Port Number:";
            this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.comboBoxStopBits.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboBoxStopBits.ItemHeight = 13;
            this.comboBoxStopBits.Items.AddRange(new object[] {
            "1",
            "1.5",
            "2"});
            this.comboBoxStopBits.Location = new System.Drawing.Point(136, 112);
            this.comboBoxStopBits.Name = "comboBoxStopBits";
            this.comboBoxStopBits.Size = new System.Drawing.Size(128, 21);
            this.comboBoxStopBits.TabIndex = 16;
            this.comboBoxStopBits.SelectedIndexChanged += new System.EventHandler(this.ControlValueChanged);
            this.comboParity.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboParity.ItemHeight = 13;
            this.comboParity.Items.AddRange(new object[] {
            "None",
            "Odd",
            "Even",
            "Mark",
            "Space"});
            this.comboParity.Location = new System.Drawing.Point(136, 88);
            this.comboParity.Name = "comboParity";
            this.comboParity.Size = new System.Drawing.Size(128, 21);
            this.comboParity.TabIndex = 15;
            this.comboParity.SelectedIndexChanged += new System.EventHandler(this.ControlValueChanged);
            this.comboBoxByteSize.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboBoxByteSize.ItemHeight = 13;
            this.comboBoxByteSize.Items.AddRange(new object[] {
            "5",
            "6",
            "7",
            "8"});
            this.comboBoxByteSize.Location = new System.Drawing.Point(136, 64);
            this.comboBoxByteSize.Name = "comboBoxByteSize";
            this.comboBoxByteSize.Size = new System.Drawing.Size(128, 21);
            this.comboBoxByteSize.TabIndex = 14;
            this.comboBoxByteSize.SelectedIndexChanged += new System.EventHandler(this.ControlValueChanged);
            this.comboBoxBaudRate.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboBoxBaudRate.ItemHeight = 13;
            this.comboBoxBaudRate.Items.AddRange(new object[] {
            "110",
            "300",
            "600",
            "1200",
            "2400",
            "4800",
            "9600",
            "14400",
            "19200",
            "38400",
            "56000",
            "57600",
            "115200",
            "128000",
            "256000"});
            this.comboBoxBaudRate.Location = new System.Drawing.Point(136, 40);
            this.comboBoxBaudRate.Name = "comboBoxBaudRate";
            this.comboBoxBaudRate.Size = new System.Drawing.Size(128, 21);
            this.comboBoxBaudRate.TabIndex = 13;
            this.comboBoxBaudRate.SelectedIndexChanged += new System.EventHandler(this.ControlValueChanged);
            this.comboBoxCOMPort.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboBoxCOMPort.ItemHeight = 13;
            this.comboBoxCOMPort.Items.AddRange(new object[] {
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26",
            "27",
            "28",
            "29",
            "30",
            "31",
            "32"});
            this.comboBoxCOMPort.Location = new System.Drawing.Point(136, 16);
            this.comboBoxCOMPort.Name = "comboBoxCOMPort";
            this.comboBoxCOMPort.Size = new System.Drawing.Size(128, 21);
            this.comboBoxCOMPort.TabIndex = 12;
            this.comboBoxCOMPort.SelectedIndexChanged += new System.EventHandler(this.ControlValueChanged);
            this.tabPageGeneral.Controls.Add(this.checkBoxSetAltitud);
            this.tabPageGeneral.Controls.Add(this.label39);
            this.tabPageGeneral.Controls.Add(this.label38);
            this.tabPageGeneral.Controls.Add(this.numericUpDownAltitud);
            this.tabPageGeneral.Controls.Add(this.label35);
            this.tabPageGeneral.Controls.Add(this.checkBoxMessagesMonitor);
            this.tabPageGeneral.Controls.Add(this.label32);
            this.tabPageGeneral.Controls.Add(this.label33);
            this.tabPageGeneral.Controls.Add(this.label30);
            this.tabPageGeneral.Controls.Add(this.label18);
            this.tabPageGeneral.Controls.Add(this.label31);
            this.tabPageGeneral.Controls.Add(this.label17);
            this.tabPageGeneral.Controls.Add(this.checkBoxRecordSession);
            this.tabPageGeneral.Controls.Add(this.label16);
            this.tabPageGeneral.Controls.Add(this.label15);
            this.tabPageGeneral.Controls.Add(this.label10);
            this.tabPageGeneral.Controls.Add(this.checkBoxTrackLine);
            this.tabPageGeneral.Controls.Add(this.checkBoxInformationText);
            this.tabPageGeneral.Controls.Add(this.checkBoxTrackHeading);
            this.tabPageGeneral.Controls.Add(this.checkBoxVExaggeration);
            this.tabPageGeneral.Controls.Add(this.groupBox6);
            this.tabPageGeneral.Controls.Add(this.groupBox4);
            this.tabPageGeneral.Controls.Add(this.groupBox3);
            this.tabPageGeneral.Controls.Add(this.groupBox1);
            this.tabPageGeneral.Location = new System.Drawing.Point(4, 22);
            this.tabPageGeneral.Name = "tabPageGeneral";
            this.tabPageGeneral.Size = new System.Drawing.Size(344, 246);
            this.tabPageGeneral.TabIndex = 6;
            this.tabPageGeneral.Text = "General";
            this.tabPageGeneral.Visible = false;
            this.checkBoxSetAltitud.CheckAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.checkBoxSetAltitud.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.checkBoxSetAltitud.Location = new System.Drawing.Point(280, 88);
            this.checkBoxSetAltitud.Name = "checkBoxSetAltitud";
            this.checkBoxSetAltitud.Size = new System.Drawing.Size(40, 16);
            this.checkBoxSetAltitud.TabIndex = 31;
            this.checkBoxSetAltitud.Text = "Set";
            this.checkBoxSetAltitud.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.label39.Location = new System.Drawing.Point(240, 88);
            this.label39.Name = "label39";
            this.label39.Size = new System.Drawing.Size(24, 16);
            this.label39.TabIndex = 30;
            this.label39.Text = "Km.";
            this.label39.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.groupBox6.Location = new System.Drawing.Point(-12, 72);
            this.groupBox6.Name = "groupBox6";
            this.groupBox6.Size = new System.Drawing.Size(368, 8);
            this.groupBox6.TabIndex = 29;
            this.groupBox6.TabStop = false;
            this.label38.Location = new System.Drawing.Point(0, 88);
            this.label38.Name = "label38";
            this.label38.Size = new System.Drawing.Size(176, 16);
            this.label38.TabIndex = 28;
            this.label38.Text = "Set start altitud on tracked source:";
            this.label38.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.numericUpDownAltitud.Increment = new decimal(new int[] {
            500,
            0,
            0,
            0});
            this.numericUpDownAltitud.Location = new System.Drawing.Point(176, 88);
            this.numericUpDownAltitud.Maximum = new decimal(new int[] {
            13000,
            0,
            0,
            0});
            this.numericUpDownAltitud.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.numericUpDownAltitud.Name = "numericUpDownAltitud";
            this.numericUpDownAltitud.Size = new System.Drawing.Size(64, 20);
            this.numericUpDownAltitud.TabIndex = 27;
            this.numericUpDownAltitud.Value = new decimal(new int[] {
            20,
            0,
            0,
            0});
            this.label35.Location = new System.Drawing.Point(136, 160);
            this.label35.Name = "label35";
            this.label35.Size = new System.Drawing.Size(176, 16);
            this.label35.TabIndex = 26;
            this.label35.Text = "Open Messages Monitor Window";
            this.groupBox4.Location = new System.Drawing.Point(-12, 176);
            this.groupBox4.Name = "groupBox4";
            this.groupBox4.Size = new System.Drawing.Size(368, 8);
            this.groupBox4.TabIndex = 25;
            this.groupBox4.TabStop = false;
            this.checkBoxMessagesMonitor.CheckAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.checkBoxMessagesMonitor.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.checkBoxMessagesMonitor.Location = new System.Drawing.Point(0, 160);
            this.checkBoxMessagesMonitor.Name = "checkBoxMessagesMonitor";
            this.checkBoxMessagesMonitor.Size = new System.Drawing.Size(128, 16);
            this.checkBoxMessagesMonitor.TabIndex = 23;
            this.checkBoxMessagesMonitor.Text = "Messages Monitor";
            this.checkBoxMessagesMonitor.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.label32.Location = new System.Drawing.Point(200, 224);
            this.label32.Name = "label32";
            this.label32.Size = new System.Drawing.Size(120, 16);
            this.label32.TabIndex = 21;
            this.label32.Text = "L. Shift+Click on World";
            this.label33.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label33.Location = new System.Drawing.Point(0, 224);
            this.label33.Name = "label33";
            this.label33.Size = new System.Drawing.Size(216, 24);
            this.label33.TabIndex = 22;
            this.label33.Text = "Set a Point of Interest (POI):";
            this.label30.Location = new System.Drawing.Point(200, 208);
            this.label30.Name = "label30";
            this.label30.Size = new System.Drawing.Size(136, 16);
            this.label30.TabIndex = 18;
            this.label30.Text = "R. Alt+Click on GPS Icon";
            this.label18.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label18.Location = new System.Drawing.Point(0, 208);
            this.label18.Name = "label18";
            this.label18.Size = new System.Drawing.Size(216, 24);
            this.label18.TabIndex = 19;
            this.label18.Text = "Distance && Bearing to POI from all GPS:";
            this.label31.Location = new System.Drawing.Point(200, 192);
            this.label31.Name = "label31";
            this.label31.Size = new System.Drawing.Size(120, 16);
            this.label31.TabIndex = 20;
            this.label31.Text = "R. Alt+Click on POI";
            this.label17.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label17.Location = new System.Drawing.Point(0, 192);
            this.label17.Name = "label17";
            this.label17.Size = new System.Drawing.Size(168, 16);
            this.label17.TabIndex = 16;
            this.label17.Text = "Distance from all POIs to GPS:";
            this.groupBox3.Location = new System.Drawing.Point(-16, 144);
            this.groupBox3.Name = "groupBox3";
            this.groupBox3.Size = new System.Drawing.Size(368, 8);
            this.groupBox3.TabIndex = 15;
            this.groupBox3.TabStop = false;
            this.groupBox1.Location = new System.Drawing.Point(-16, 112);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(368, 8);
            this.groupBox1.TabIndex = 14;
            this.groupBox1.TabStop = false;
            this.checkBoxRecordSession.CheckAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.checkBoxRecordSession.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.checkBoxRecordSession.Location = new System.Drawing.Point(0, 128);
            this.checkBoxRecordSession.Name = "checkBoxRecordSession";
            this.checkBoxRecordSession.Size = new System.Drawing.Size(128, 16);
            this.checkBoxRecordSession.TabIndex = 13;
            this.checkBoxRecordSession.Text = "Record Session";
            this.checkBoxRecordSession.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.checkBoxRecordSession.CheckedChanged += new System.EventHandler(this.checkBoxRecordSession_CheckedChanged);
            this.label16.Location = new System.Drawing.Point(136, 56);
            this.label16.Name = "label16";
            this.label16.Size = new System.Drawing.Size(176, 16);
            this.label16.TabIndex = 12;
            this.label16.Text = "[Left Alt+Click on GPS Icon]";
            this.label15.Location = new System.Drawing.Point(136, 40);
            this.label15.Name = "label15";
            this.label15.Size = new System.Drawing.Size(176, 16);
            this.label15.TabIndex = 11;
            this.label15.Text = "[Right Control+Click on GPS Icon]";
            this.label10.Location = new System.Drawing.Point(136, 24);
            this.label10.Name = "label10";
            this.label10.Size = new System.Drawing.Size(184, 16);
            this.label10.TabIndex = 10;
            this.label10.Text = "[Left Control+Click on GPS Icon]";
            this.checkBoxTrackLine.CheckAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.checkBoxTrackLine.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.checkBoxTrackLine.Location = new System.Drawing.Point(0, 56);
            this.checkBoxTrackLine.Name = "checkBoxTrackLine";
            this.checkBoxTrackLine.Size = new System.Drawing.Size(128, 16);
            this.checkBoxTrackLine.TabIndex = 9;
            this.checkBoxTrackLine.Text = "Track line";
            this.checkBoxTrackLine.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.checkBoxTrackLine.CheckedChanged += new System.EventHandler(this.checkBoxTrackLine_CheckedChanged);
            this.checkBoxInformationText.CheckAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.checkBoxInformationText.Checked = true;
            this.checkBoxInformationText.CheckState = System.Windows.Forms.CheckState.Checked;
            this.checkBoxInformationText.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.checkBoxInformationText.Location = new System.Drawing.Point(0, 24);
            this.checkBoxInformationText.Name = "checkBoxInformationText";
            this.checkBoxInformationText.Size = new System.Drawing.Size(128, 16);
            this.checkBoxInformationText.TabIndex = 8;
            this.checkBoxInformationText.Text = "Information Text";
            this.checkBoxInformationText.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.checkBoxInformationText.CheckedChanged += new System.EventHandler(this.checkBoxInformationText_CheckedChanged);
            this.checkBoxTrackHeading.CheckAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.checkBoxTrackHeading.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.checkBoxTrackHeading.Location = new System.Drawing.Point(24, 40);
            this.checkBoxTrackHeading.Name = "checkBoxTrackHeading";
            this.checkBoxTrackHeading.Size = new System.Drawing.Size(104, 16);
            this.checkBoxTrackHeading.TabIndex = 6;
            this.checkBoxTrackHeading.Text = "Track Heading";
            this.checkBoxTrackHeading.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.checkBoxTrackHeading.CheckedChanged += new System.EventHandler(this.checkBoxTrackHeading_CheckedChanged);
            this.checkBoxVExaggeration.CheckAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.checkBoxVExaggeration.Checked = true;
            this.checkBoxVExaggeration.CheckState = System.Windows.Forms.CheckState.Checked;
            this.checkBoxVExaggeration.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.checkBoxVExaggeration.Location = new System.Drawing.Point(0, 8);
            this.checkBoxVExaggeration.Name = "checkBoxVExaggeration";
            this.checkBoxVExaggeration.Size = new System.Drawing.Size(128, 16);
            this.checkBoxVExaggeration.TabIndex = 7;
            this.checkBoxVExaggeration.Text = "V. Exaggeration to 0";
            this.checkBoxVExaggeration.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.tabPageFileHelp.Controls.Add(this.textBox5);
            this.tabPageFileHelp.Location = new System.Drawing.Point(4, 22);
            this.tabPageFileHelp.Name = "tabPageFileHelp";
            this.tabPageFileHelp.Size = new System.Drawing.Size(344, 246);
            this.tabPageFileHelp.TabIndex = 13;
            this.tabPageFileHelp.Text = "Help";
            this.textBox5.BackColor = System.Drawing.Color.White;
            this.textBox5.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.textBox5.Location = new System.Drawing.Point(8, 11);
            this.textBox5.Multiline = true;
            this.textBox5.Name = "textBox5";
            this.textBox5.ReadOnly = true;
            this.textBox5.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.textBox5.Size = new System.Drawing.Size(328, 224);
            this.textBox5.TabIndex = 6;
            this.textBox5.Text = resources.GetString("textBox5.Text");
            this.tabPageAPRSInternetHelp.Controls.Add(this.textBox4);
            this.tabPageAPRSInternetHelp.Location = new System.Drawing.Point(4, 22);
            this.tabPageAPRSInternetHelp.Name = "tabPageAPRSInternetHelp";
            this.tabPageAPRSInternetHelp.Size = new System.Drawing.Size(344, 246);
            this.tabPageAPRSInternetHelp.TabIndex = 12;
            this.tabPageAPRSInternetHelp.Text = "Help";
            this.textBox4.BackColor = System.Drawing.Color.White;
            this.textBox4.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.textBox4.Location = new System.Drawing.Point(8, 11);
            this.textBox4.Multiline = true;
            this.textBox4.Name = "textBox4";
            this.textBox4.ReadOnly = true;
            this.textBox4.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.textBox4.Size = new System.Drawing.Size(328, 224);
            this.textBox4.TabIndex = 6;
            this.textBox4.Text = resources.GetString("textBox4.Text");
            this.tabPageUsage.Controls.Add(this.pictureBoxHelpLogo);
            this.tabPageUsage.Controls.Add(this.textBoxUsage);
            this.tabPageUsage.Location = new System.Drawing.Point(4, 22);
            this.tabPageUsage.Name = "tabPageUsage";
            this.tabPageUsage.Size = new System.Drawing.Size(344, 246);
            this.tabPageUsage.TabIndex = 5;
            this.tabPageUsage.Text = "Help";
            this.tabPageUsage.Visible = false;
            this.pictureBoxHelpLogo.Location = new System.Drawing.Point(8, 8);
            this.pictureBoxHelpLogo.Name = "pictureBoxHelpLogo";
            this.pictureBoxHelpLogo.Size = new System.Drawing.Size(48, 45);
            this.pictureBoxHelpLogo.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.pictureBoxHelpLogo.TabIndex = 37;
            this.pictureBoxHelpLogo.TabStop = false;
            this.textBoxUsage.BackColor = System.Drawing.Color.White;
            this.textBoxUsage.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.textBoxUsage.Location = new System.Drawing.Point(56, 8);
            this.textBoxUsage.Multiline = true;
            this.textBoxUsage.Name = "textBoxUsage";
            this.textBoxUsage.ReadOnly = true;
            this.textBoxUsage.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.textBoxUsage.Size = new System.Drawing.Size(280, 224);
            this.textBoxUsage.TabIndex = 5;
            this.textBoxUsage.Text = resources.GetString("textBoxUsage.Text");
            this.tabPageTCP.Controls.Add(this.comboBoxTcpIP);
            this.tabPageTCP.Controls.Add(this.checkBoxSecureSocket);
            this.tabPageTCP.Controls.Add(this.buttonApplyTCP);
            this.tabPageTCP.Controls.Add(this.label22);
            this.tabPageTCP.Controls.Add(this.buttonTrackColorTCP);
            this.tabPageTCP.Controls.Add(this.label8);
            this.tabPageTCP.Controls.Add(this.label7);
            this.tabPageTCP.Controls.Add(this.numericUpDownTCPPort);
            this.tabPageTCP.Location = new System.Drawing.Point(4, 22);
            this.tabPageTCP.Name = "tabPageTCP";
            this.tabPageTCP.Size = new System.Drawing.Size(344, 246);
            this.tabPageTCP.TabIndex = 2;
            this.tabPageTCP.Text = "TCP";
            this.tabPageTCP.Visible = false;
            this.comboBoxTcpIP.Location = new System.Drawing.Point(176, 16);
            this.comboBoxTcpIP.Name = "comboBoxTcpIP";
            this.comboBoxTcpIP.Size = new System.Drawing.Size(128, 21);
            this.comboBoxTcpIP.TabIndex = 50;
            this.comboBoxTcpIP.SelectedIndexChanged += new System.EventHandler(this.ControlValueChanged);
            this.comboBoxTcpIP.TextChanged += new System.EventHandler(this.ControlValueChanged);
            this.checkBoxSecureSocket.Location = new System.Drawing.Point(48, 120);
            this.checkBoxSecureSocket.Name = "checkBoxSecureSocket";
            this.checkBoxSecureSocket.RightToLeft = System.Windows.Forms.RightToLeft.Yes;
            this.checkBoxSecureSocket.Size = new System.Drawing.Size(256, 16);
            this.checkBoxSecureSocket.TabIndex = 49;
            this.checkBoxSecureSocket.Text = "Use SSL3 or TLS1 Secure TCP Connection";
            this.checkBoxSecureSocket.CheckedChanged += new System.EventHandler(this.ControlValueChanged);
            this.buttonApplyTCP.Location = new System.Drawing.Point(280, 216);
            this.buttonApplyTCP.Name = "buttonApplyTCP";
            this.buttonApplyTCP.Size = new System.Drawing.Size(56, 24);
            this.buttonApplyTCP.TabIndex = 48;
            this.buttonApplyTCP.Text = "Apply";
            this.buttonApplyTCP.Click += new System.EventHandler(this.buttonApply_Click);
            this.label22.Location = new System.Drawing.Point(72, 80);
            this.label22.Name = "label22";
            this.label22.Size = new System.Drawing.Size(96, 16);
            this.label22.TabIndex = 43;
            this.label22.Text = "Track Color:";
            this.label22.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.buttonTrackColorTCP.Location = new System.Drawing.Point(176, 80);
            this.buttonTrackColorTCP.Name = "buttonTrackColorTCP";
            this.buttonTrackColorTCP.Size = new System.Drawing.Size(128, 13);
            this.buttonTrackColorTCP.TabIndex = 42;
            this.buttonTrackColorTCP.Click += new System.EventHandler(this.buttonTrackColorTCP_Click);
            this.buttonTrackColorTCP.BackColorChanged += new System.EventHandler(this.ControlValueChanged);
            this.label8.Location = new System.Drawing.Point(24, 16);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(152, 24);
            this.label8.TabIndex = 22;
            this.label8.Text = "Gps/ APRS Server IP/Name:";
            this.label8.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.label7.Location = new System.Drawing.Point(96, 48);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(72, 24);
            this.label7.TabIndex = 20;
            this.label7.Text = "Port Number:";
            this.label7.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.numericUpDownTCPPort.Location = new System.Drawing.Point(176, 48);
            this.numericUpDownTCPPort.Maximum = new decimal(new int[] {
            65519,
            0,
            0,
            0});
            this.numericUpDownTCPPort.Minimum = new decimal(new int[] {
            1000,
            0,
            0,
            0});
            this.numericUpDownTCPPort.Name = "numericUpDownTCPPort";
            this.numericUpDownTCPPort.Size = new System.Drawing.Size(128, 20);
            this.numericUpDownTCPPort.TabIndex = 0;
            this.numericUpDownTCPPort.Value = new decimal(new int[] {
            1000,
            0,
            0,
            0});
            this.numericUpDownTCPPort.ValueChanged += new System.EventHandler(this.ControlValueChanged);
            this.numericUpDownTCPPort.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.ControlValueChanged);
            this.tabPageFile.Controls.Add(this.label37);
            this.tabPageFile.Controls.Add(this.label36);
            this.tabPageFile.Controls.Add(this.numericUpDownReload);
            this.tabPageFile.Controls.Add(this.buttonApplyFile);
            this.tabPageFile.Controls.Add(this.label19);
            this.tabPageFile.Controls.Add(this.buttonTrackColor);
            this.tabPageFile.Controls.Add(this.checkBoxForcePreprocessing);
            this.tabPageFile.Controls.Add(this.labelPreprocessing);
            this.tabPageFile.Controls.Add(this.progressBarPreprocessing);
            this.tabPageFile.Controls.Add(this.checkBoxTrackAtOnce);
            this.tabPageFile.Controls.Add(this.checkBoxNoDelay);
            this.tabPageFile.Controls.Add(this.comboBoxFile);
            this.tabPageFile.Controls.Add(this.label13);
            this.tabPageFile.Controls.Add(this.label12);
            this.tabPageFile.Controls.Add(this.trackBarFileSpeed);
            this.tabPageFile.Controls.Add(this.buttonBrowseGpsFile);
            this.tabPageFile.Controls.Add(this.label9);
            this.tabPageFile.Controls.Add(this.label14);
            this.tabPageFile.Controls.Add(this.groupBox5);
            this.tabPageFile.Location = new System.Drawing.Point(4, 22);
            this.tabPageFile.Name = "tabPageFile";
            this.tabPageFile.Size = new System.Drawing.Size(344, 246);
            this.tabPageFile.TabIndex = 3;
            this.tabPageFile.Text = "File";
            this.tabPageFile.Visible = false;
            this.label37.Location = new System.Drawing.Point(130, 52);
            this.label37.Name = "label37";
            this.label37.Size = new System.Drawing.Size(184, 16);
            this.label37.TabIndex = 52;
            this.label37.Text = "seconds after playback is done.";
            this.label37.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.label36.Location = new System.Drawing.Point(-5, 52);
            this.label36.Name = "label36";
            this.label36.Size = new System.Drawing.Size(72, 16);
            this.label36.TabIndex = 51;
            this.label36.Text = "Reload after";
            this.label36.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.numericUpDownReload.Location = new System.Drawing.Point(72, 52);
            this.numericUpDownReload.Maximum = new decimal(new int[] {
            1800,
            0,
            0,
            0});
            this.numericUpDownReload.Name = "numericUpDownReload";
            this.numericUpDownReload.Size = new System.Drawing.Size(56, 20);
            this.numericUpDownReload.TabIndex = 50;
            this.numericUpDownReload.ValueChanged += new System.EventHandler(this.ControlValueChanged);
            this.groupBox5.Location = new System.Drawing.Point(-252, 72);
            this.groupBox5.Name = "groupBox5";
            this.groupBox5.Size = new System.Drawing.Size(848, 8);
            this.groupBox5.TabIndex = 49;
            this.groupBox5.TabStop = false;
            this.buttonApplyFile.Location = new System.Drawing.Point(280, 216);
            this.buttonApplyFile.Name = "buttonApplyFile";
            this.buttonApplyFile.Size = new System.Drawing.Size(56, 24);
            this.buttonApplyFile.TabIndex = 48;
            this.buttonApplyFile.Text = "Apply";
            this.buttonApplyFile.Click += new System.EventHandler(this.buttonApply_Click);
            this.label19.Location = new System.Drawing.Point(160, 168);
            this.label19.Name = "label19";
            this.label19.Size = new System.Drawing.Size(96, 16);
            this.label19.TabIndex = 39;
            this.label19.Text = "Track Color";
            this.label19.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.buttonTrackColor.Location = new System.Drawing.Point(258, 168);
            this.buttonTrackColor.Name = "buttonTrackColor";
            this.buttonTrackColor.Size = new System.Drawing.Size(13, 13);
            this.buttonTrackColor.TabIndex = 38;
            this.buttonTrackColor.Click += new System.EventHandler(this.buttonTrackColor_Click);
            this.buttonTrackColor.BackColorChanged += new System.EventHandler(this.ControlValueChanged);
            this.checkBoxForcePreprocessing.Location = new System.Drawing.Point(64, 200);
            this.checkBoxForcePreprocessing.Name = "checkBoxForcePreprocessing";
            this.checkBoxForcePreprocessing.RightToLeft = System.Windows.Forms.RightToLeft.Yes;
            this.checkBoxForcePreprocessing.Size = new System.Drawing.Size(208, 16);
            this.checkBoxForcePreprocessing.TabIndex = 37;
            this.checkBoxForcePreprocessing.Text = "Force Track at Once Preprocessing";
            this.checkBoxForcePreprocessing.CheckedChanged += new System.EventHandler(this.ControlValueChanged);
            this.labelPreprocessing.Location = new System.Drawing.Point(8, 224);
            this.labelPreprocessing.Name = "labelPreprocessing";
            this.labelPreprocessing.Size = new System.Drawing.Size(80, 16);
            this.labelPreprocessing.TabIndex = 36;
            this.labelPreprocessing.Text = "Preprocessing:";
            this.labelPreprocessing.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.progressBarPreprocessing.Location = new System.Drawing.Point(96, 224);
            this.progressBarPreprocessing.Name = "progressBarPreprocessing";
            this.progressBarPreprocessing.Size = new System.Drawing.Size(160, 16);
            this.progressBarPreprocessing.Step = 1;
            this.progressBarPreprocessing.TabIndex = 35;
            this.checkBoxTrackAtOnce.Location = new System.Drawing.Point(96, 184);
            this.checkBoxTrackAtOnce.Name = "checkBoxTrackAtOnce";
            this.checkBoxTrackAtOnce.RightToLeft = System.Windows.Forms.RightToLeft.Yes;
            this.checkBoxTrackAtOnce.Size = new System.Drawing.Size(176, 16);
            this.checkBoxTrackAtOnce.TabIndex = 34;
            this.checkBoxTrackAtOnce.Text = "Track at Once";
            this.checkBoxTrackAtOnce.CheckedChanged += new System.EventHandler(this.ControlValueChanged);
            this.checkBoxNoDelay.Location = new System.Drawing.Point(96, 144);
            this.checkBoxNoDelay.Name = "checkBoxNoDelay";
            this.checkBoxNoDelay.RightToLeft = System.Windows.Forms.RightToLeft.Yes;
            this.checkBoxNoDelay.Size = new System.Drawing.Size(176, 16);
            this.checkBoxNoDelay.TabIndex = 33;
            this.checkBoxNoDelay.Text = "Maximum Speed";
            this.checkBoxNoDelay.CheckedChanged += new System.EventHandler(this.ControlValueChanged);
            this.comboBoxFile.Location = new System.Drawing.Point(8, 24);
            this.comboBoxFile.Name = "comboBoxFile";
            this.comboBoxFile.Size = new System.Drawing.Size(304, 21);
            this.comboBoxFile.TabIndex = 32;
            this.comboBoxFile.SelectedIndexChanged += new System.EventHandler(this.ControlValueChanged);
            this.comboBoxFile.TextChanged += new System.EventHandler(this.ControlValueChanged);
            this.label13.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.label13.Location = new System.Drawing.Point(24, 104);
            this.label13.Name = "label13";
            this.label13.Size = new System.Drawing.Size(56, 24);
            this.label13.TabIndex = 30;
            this.label13.Text = "Real Time";
            this.label13.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.label12.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.label12.Location = new System.Drawing.Point(24, 88);
            this.label12.Name = "label12";
            this.label12.Size = new System.Drawing.Size(120, 16);
            this.label12.TabIndex = 29;
            this.label12.Text = "Playback Speed:";
            this.label12.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.trackBarFileSpeed.LargeChange = 2;
            this.trackBarFileSpeed.Location = new System.Drawing.Point(72, 104);
            this.trackBarFileSpeed.Minimum = 1;
            this.trackBarFileSpeed.Name = "trackBarFileSpeed";
            this.trackBarFileSpeed.Size = new System.Drawing.Size(208, 42);
            this.trackBarFileSpeed.TabIndex = 28;
            this.trackBarFileSpeed.Value = 1;
            this.trackBarFileSpeed.ValueChanged += new System.EventHandler(this.ControlValueChanged);
            this.buttonBrowseGpsFile.Location = new System.Drawing.Point(312, 24);
            this.buttonBrowseGpsFile.Name = "buttonBrowseGpsFile";
            this.buttonBrowseGpsFile.Size = new System.Drawing.Size(21, 21);
            this.buttonBrowseGpsFile.TabIndex = 25;
            this.buttonBrowseGpsFile.Text = "...";
            this.buttonBrowseGpsFile.Click += new System.EventHandler(this.buttonBrowseGpsFile_Click);
            this.label9.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.label9.Location = new System.Drawing.Point(8, 8);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(272, 16);
            this.label9.TabIndex = 24;
            this.label9.Text = "File Name (use http:// for internet downloaded files):";
            this.label9.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.label14.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.label14.Location = new System.Drawing.Point(280, 104);
            this.label14.Name = "label14";
            this.label14.Size = new System.Drawing.Size(48, 24);
            this.label14.TabIndex = 31;
            this.label14.Text = "Faster";
            this.label14.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.tabPageUDPHelp.Controls.Add(this.textBox7);
            this.tabPageUDPHelp.Location = new System.Drawing.Point(4, 22);
            this.tabPageUDPHelp.Name = "tabPageUDPHelp";
            this.tabPageUDPHelp.Size = new System.Drawing.Size(344, 246);
            this.tabPageUDPHelp.TabIndex = 15;
            this.tabPageUDPHelp.Text = "Help";
            this.textBox7.BackColor = System.Drawing.Color.White;
            this.textBox7.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.textBox7.Location = new System.Drawing.Point(8, 11);
            this.textBox7.Multiline = true;
            this.textBox7.Name = "textBox7";
            this.textBox7.ReadOnly = true;
            this.textBox7.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.textBox7.Size = new System.Drawing.Size(328, 224);
            this.textBox7.TabIndex = 6;
            this.textBox7.Text = resources.GetString("textBox7.Text");
            this.tabPageAPRSInternet.Controls.Add(this.label40);
            this.tabPageAPRSInternet.Controls.Add(this.comboBoxAPRSInternetServer);
            this.tabPageAPRSInternet.Controls.Add(this.buttonApplyAPRSInternet);
            this.tabPageAPRSInternet.Controls.Add(this.label26);
            this.tabPageAPRSInternet.Controls.Add(this.label24);
            this.tabPageAPRSInternet.Controls.Add(this.buttonTrackColorAPRS);
            this.tabPageAPRSInternet.Controls.Add(this.textBoxAPRSISCallSign);
            this.tabPageAPRSInternet.Controls.Add(this.numericUpDownAPRSIS);
            this.tabPageAPRSInternet.Controls.Add(this.label27);
            this.tabPageAPRSInternet.Location = new System.Drawing.Point(4, 22);
            this.tabPageAPRSInternet.Name = "tabPageAPRSInternet";
            this.tabPageAPRSInternet.Size = new System.Drawing.Size(344, 246);
            this.tabPageAPRSInternet.TabIndex = 8;
            this.tabPageAPRSInternet.Text = "APRS Internet";
            this.label40.Location = new System.Drawing.Point(8, 15);
            this.label40.Name = "label40";
            this.label40.Size = new System.Drawing.Size(344, 16);
            this.label40.TabIndex = 58;
            this.label40.Text = "Internet Server URL (Call Sign will be appended at the end of URL):";
            this.label40.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.comboBoxAPRSInternetServer.Items.AddRange(new object[] {
            "http://db.aprsworld.net/datamart/csv.php?call=",
            "http://www.findu.com/cgi-bin/rawposit.cgi?call="});
            this.comboBoxAPRSInternetServer.Location = new System.Drawing.Point(8, 32);
            this.comboBoxAPRSInternetServer.Name = "comboBoxAPRSInternetServer";
            this.comboBoxAPRSInternetServer.Size = new System.Drawing.Size(328, 21);
            this.comboBoxAPRSInternetServer.TabIndex = 57;
            this.comboBoxAPRSInternetServer.SelectedIndexChanged += new System.EventHandler(this.ControlValueChanged);
            this.comboBoxAPRSInternetServer.TextChanged += new System.EventHandler(this.ControlValueChanged);
            this.buttonApplyAPRSInternet.Location = new System.Drawing.Point(280, 216);
            this.buttonApplyAPRSInternet.Name = "buttonApplyAPRSInternet";
            this.buttonApplyAPRSInternet.Size = new System.Drawing.Size(56, 24);
            this.buttonApplyAPRSInternet.TabIndex = 56;
            this.buttonApplyAPRSInternet.Text = "Apply";
            this.buttonApplyAPRSInternet.Click += new System.EventHandler(this.buttonApply_Click);
            this.label26.Location = new System.Drawing.Point(152, 64);
            this.label26.Name = "label26";
            this.label26.Size = new System.Drawing.Size(88, 16);
            this.label26.TabIndex = 53;
            this.label26.Text = "Call Sign Filter:";
            this.label26.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.label24.Location = new System.Drawing.Point(152, 128);
            this.label24.Name = "label24";
            this.label24.Size = new System.Drawing.Size(80, 16);
            this.label24.TabIndex = 52;
            this.label24.Text = "Track Color:";
            this.label24.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.buttonTrackColorAPRS.Location = new System.Drawing.Point(240, 128);
            this.buttonTrackColorAPRS.Name = "buttonTrackColorAPRS";
            this.buttonTrackColorAPRS.Size = new System.Drawing.Size(96, 13);
            this.buttonTrackColorAPRS.TabIndex = 51;
            this.buttonTrackColorAPRS.Click += new System.EventHandler(this.buttonTrackColorAPRS_Click);
            this.buttonTrackColorAPRS.BackColorChanged += new System.EventHandler(this.ControlValueChanged);
            this.textBoxAPRSISCallSign.Location = new System.Drawing.Point(240, 64);
            this.textBoxAPRSISCallSign.Name = "textBoxAPRSISCallSign";
            this.textBoxAPRSISCallSign.Size = new System.Drawing.Size(96, 20);
            this.textBoxAPRSISCallSign.TabIndex = 50;
            this.textBoxAPRSISCallSign.TextChanged += new System.EventHandler(this.ControlValueChanged);
            this.numericUpDownAPRSIS.Location = new System.Drawing.Point(240, 88);
            this.numericUpDownAPRSIS.Maximum = new decimal(new int[] {
            300,
            0,
            0,
            0});
            this.numericUpDownAPRSIS.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.numericUpDownAPRSIS.Name = "numericUpDownAPRSIS";
            this.numericUpDownAPRSIS.Size = new System.Drawing.Size(96, 20);
            this.numericUpDownAPRSIS.TabIndex = 49;
            this.numericUpDownAPRSIS.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.numericUpDownAPRSIS.ValueChanged += new System.EventHandler(this.ControlValueChanged);
            this.numericUpDownAPRSIS.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.ControlValueChanged);
            this.label27.Location = new System.Drawing.Point(152, 88);
            this.label27.Name = "label27";
            this.label27.Size = new System.Drawing.Size(96, 16);
            this.label27.TabIndex = 54;
            this.label27.Text = "Refresh Rate (s):";
            this.label27.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.tabPageCOMHelp.Controls.Add(this.textBox2);
            this.tabPageCOMHelp.Location = new System.Drawing.Point(4, 22);
            this.tabPageCOMHelp.Name = "tabPageCOMHelp";
            this.tabPageCOMHelp.Size = new System.Drawing.Size(344, 246);
            this.tabPageCOMHelp.TabIndex = 10;
            this.tabPageCOMHelp.Text = "Help";
            this.textBox2.BackColor = System.Drawing.Color.White;
            this.textBox2.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.textBox2.Location = new System.Drawing.Point(8, 11);
            this.textBox2.Multiline = true;
            this.textBox2.Name = "textBox2";
            this.textBox2.ReadOnly = true;
            this.textBox2.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.textBox2.Size = new System.Drawing.Size(328, 224);
            this.textBox2.TabIndex = 6;
            this.textBox2.Text = resources.GetString("textBox2.Text");
            this.tabPagePOIHelp.Controls.Add(this.textBox3);
            this.tabPagePOIHelp.Location = new System.Drawing.Point(4, 22);
            this.tabPagePOIHelp.Name = "tabPagePOIHelp";
            this.tabPagePOIHelp.Size = new System.Drawing.Size(344, 246);
            this.tabPagePOIHelp.TabIndex = 11;
            this.tabPagePOIHelp.Text = "Help";
            this.textBox3.BackColor = System.Drawing.Color.White;
            this.textBox3.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.textBox3.Location = new System.Drawing.Point(8, 11);
            this.textBox3.Multiline = true;
            this.textBox3.Name = "textBox3";
            this.textBox3.ReadOnly = true;
            this.textBox3.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.textBox3.Size = new System.Drawing.Size(328, 224);
            this.textBox3.TabIndex = 6;
            this.textBox3.Text = "Enter the Latitude and Longitude of the Point of Interest in degrees decimal.\r\n\r\n" +
                "Use Negative Latitude for South and Negative Longitude for West.\r\n\r\nClick on \'Ap" +
                "ply\' to accept the settings.";
            this.tabPageTCPHelp.Controls.Add(this.textBox6);
            this.tabPageTCPHelp.Location = new System.Drawing.Point(4, 22);
            this.tabPageTCPHelp.Name = "tabPageTCPHelp";
            this.tabPageTCPHelp.Size = new System.Drawing.Size(344, 246);
            this.tabPageTCPHelp.TabIndex = 14;
            this.tabPageTCPHelp.Text = "Help";
            this.textBox6.BackColor = System.Drawing.Color.White;
            this.textBox6.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.textBox6.Location = new System.Drawing.Point(8, 11);
            this.textBox6.Multiline = true;
            this.textBox6.Name = "textBox6";
            this.textBox6.ReadOnly = true;
            this.textBox6.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.textBox6.Size = new System.Drawing.Size(328, 224);
            this.textBox6.TabIndex = 6;
            this.textBox6.Text = resources.GetString("textBox6.Text");
            this.tabPageAPRS.Controls.Add(this.buttonApplyAPRSFilter);
            this.tabPageAPRS.Controls.Add(this.textBox1);
            this.tabPageAPRS.Controls.Add(this.textBoxCallSignFilter);
            this.tabPageAPRS.Location = new System.Drawing.Point(4, 22);
            this.tabPageAPRS.Name = "tabPageAPRS";
            this.tabPageAPRS.Size = new System.Drawing.Size(344, 246);
            this.tabPageAPRS.TabIndex = 7;
            this.tabPageAPRS.Text = "APRS Filter";
            this.tabPageAPRS.Visible = false;
            this.buttonApplyAPRSFilter.Location = new System.Drawing.Point(280, 216);
            this.buttonApplyAPRSFilter.Name = "buttonApplyAPRSFilter";
            this.buttonApplyAPRSFilter.Size = new System.Drawing.Size(56, 24);
            this.buttonApplyAPRSFilter.TabIndex = 47;
            this.buttonApplyAPRSFilter.Text = "Apply";
            this.buttonApplyAPRSFilter.Click += new System.EventHandler(this.buttonApply_Click);
            this.textBox1.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.textBox1.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.textBox1.Location = new System.Drawing.Point(144, 16);
            this.textBox1.Multiline = true;
            this.textBox1.Name = "textBox1";
            this.textBox1.ReadOnly = true;
            this.textBox1.Size = new System.Drawing.Size(184, 192);
            this.textBox1.TabIndex = 3;
            this.textBox1.Text = resources.GetString("textBox1.Text");
            this.textBoxCallSignFilter.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.textBoxCallSignFilter.Location = new System.Drawing.Point(16, 16);
            this.textBoxCallSignFilter.Multiline = true;
            this.textBoxCallSignFilter.Name = "textBoxCallSignFilter";
            this.textBoxCallSignFilter.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.textBoxCallSignFilter.Size = new System.Drawing.Size(120, 224);
            this.textBoxCallSignFilter.TabIndex = 2;
            this.textBoxCallSignFilter.TextChanged += new System.EventHandler(this.ControlValueChanged);
            this.tabPageUDP.Controls.Add(this.buttonApplyUDP);
            this.tabPageUDP.Controls.Add(this.label21);
            this.tabPageUDP.Controls.Add(this.buttonTrackColorUDP);
            this.tabPageUDP.Controls.Add(this.label6);
            this.tabPageUDP.Controls.Add(this.numericUpDownUDPPort);
            this.tabPageUDP.Location = new System.Drawing.Point(4, 22);
            this.tabPageUDP.Name = "tabPageUDP";
            this.tabPageUDP.Size = new System.Drawing.Size(344, 246);
            this.tabPageUDP.TabIndex = 1;
            this.tabPageUDP.Text = "UDP";
            this.tabPageUDP.Visible = false;
            this.buttonApplyUDP.Location = new System.Drawing.Point(280, 216);
            this.buttonApplyUDP.Name = "buttonApplyUDP";
            this.buttonApplyUDP.Size = new System.Drawing.Size(56, 24);
            this.buttonApplyUDP.TabIndex = 47;
            this.buttonApplyUDP.Text = "Apply";
            this.buttonApplyUDP.Click += new System.EventHandler(this.buttonApply_Click);
            this.label21.Location = new System.Drawing.Point(32, 48);
            this.label21.Name = "label21";
            this.label21.Size = new System.Drawing.Size(96, 16);
            this.label21.TabIndex = 41;
            this.label21.Text = "Track Color:";
            this.label21.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.buttonTrackColorUDP.Location = new System.Drawing.Point(136, 48);
            this.buttonTrackColorUDP.Name = "buttonTrackColorUDP";
            this.buttonTrackColorUDP.Size = new System.Drawing.Size(128, 13);
            this.buttonTrackColorUDP.TabIndex = 40;
            this.buttonTrackColorUDP.Click += new System.EventHandler(this.buttonTrackColorUDP_Click);
            this.buttonTrackColorUDP.BackColorChanged += new System.EventHandler(this.ControlValueChanged);
            this.label6.Location = new System.Drawing.Point(56, 16);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(72, 24);
            this.label6.TabIndex = 18;
            this.label6.Text = "Port Number:";
            this.label6.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.numericUpDownUDPPort.Location = new System.Drawing.Point(136, 16);
            this.numericUpDownUDPPort.Maximum = new decimal(new int[] {
            65519,
            0,
            0,
            0});
            this.numericUpDownUDPPort.Minimum = new decimal(new int[] {
            1000,
            0,
            0,
            0});
            this.numericUpDownUDPPort.Name = "numericUpDownUDPPort";
            this.numericUpDownUDPPort.Size = new System.Drawing.Size(128, 20);
            this.numericUpDownUDPPort.TabIndex = 0;
            this.numericUpDownUDPPort.Value = new decimal(new int[] {
            1000,
            0,
            0,
            0});
            this.numericUpDownUDPPort.ValueChanged += new System.EventHandler(this.ControlValueChanged);
            this.numericUpDownUDPPort.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.ControlValueChanged);
            this.tabPageUpdate.Controls.Add(this.label11);
            this.tabPageUpdate.Controls.Add(this.textBoxVersionInfo);
            this.tabPageUpdate.Controls.Add(this.buttonCheckForUpdates);
            this.tabPageUpdate.Location = new System.Drawing.Point(4, 22);
            this.tabPageUpdate.Name = "tabPageUpdate";
            this.tabPageUpdate.Size = new System.Drawing.Size(344, 246);
            this.tabPageUpdate.TabIndex = 4;
            this.tabPageUpdate.Text = "Update";
            this.tabPageUpdate.Visible = false;
            this.label11.Location = new System.Drawing.Point(16, 8);
            this.label11.Name = "label11";
            this.label11.Size = new System.Drawing.Size(168, 16);
            this.label11.TabIndex = 5;
            this.label11.Text = "Version Information:";
            this.textBoxVersionInfo.BackColor = System.Drawing.Color.White;
            this.textBoxVersionInfo.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.textBoxVersionInfo.Location = new System.Drawing.Point(16, 24);
            this.textBoxVersionInfo.Multiline = true;
            this.textBoxVersionInfo.Name = "textBoxVersionInfo";
            this.textBoxVersionInfo.ReadOnly = true;
            this.textBoxVersionInfo.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.textBoxVersionInfo.Size = new System.Drawing.Size(312, 176);
            this.textBoxVersionInfo.TabIndex = 4;
            this.textBoxVersionInfo.WordWrap = false;
            this.buttonCheckForUpdates.Location = new System.Drawing.Point(16, 208);
            this.buttonCheckForUpdates.Name = "buttonCheckForUpdates";
            this.buttonCheckForUpdates.Size = new System.Drawing.Size(312, 24);
            this.buttonCheckForUpdates.TabIndex = 2;
            this.buttonCheckForUpdates.Text = "Check for Updates";
            this.buttonCheckForUpdates.Click += new System.EventHandler(this.buttonCheckForUpdates_Click);
            this.tabPagePOI.Controls.Add(this.buttonApplyPOI);
            this.tabPagePOI.Controls.Add(this.label34);
            this.tabPagePOI.Controls.Add(this.label29);
            this.tabPagePOI.Controls.Add(this.label25);
            this.tabPagePOI.Controls.Add(this.textBoxLongitud);
            this.tabPagePOI.Controls.Add(this.textBoxLatitud);
            this.tabPagePOI.Location = new System.Drawing.Point(4, 22);
            this.tabPagePOI.Name = "tabPagePOI";
            this.tabPagePOI.Size = new System.Drawing.Size(344, 246);
            this.tabPagePOI.TabIndex = 9;
            this.tabPagePOI.Text = "POI";
            this.buttonApplyPOI.Location = new System.Drawing.Point(280, 216);
            this.buttonApplyPOI.Name = "buttonApplyPOI";
            this.buttonApplyPOI.Size = new System.Drawing.Size(56, 24);
            this.buttonApplyPOI.TabIndex = 57;
            this.buttonApplyPOI.Text = "Apply";
            this.buttonApplyPOI.Click += new System.EventHandler(this.buttonApply_Click);
            this.label34.Location = new System.Drawing.Point(16, 16);
            this.label34.Name = "label34";
            this.label34.Size = new System.Drawing.Size(312, 16);
            this.label34.TabIndex = 4;
            this.label34.Text = "Enter Latitude and Longitude of the POI in degrees decimal.";
            this.label34.TextAlign = System.Drawing.ContentAlignment.TopCenter;
            this.label29.Location = new System.Drawing.Point(48, 88);
            this.label29.Name = "label29";
            this.label29.Size = new System.Drawing.Size(64, 24);
            this.label29.TabIndex = 3;
            this.label29.Text = "Longitude:";
            this.label29.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.label25.Location = new System.Drawing.Point(48, 56);
            this.label25.Name = "label25";
            this.label25.Size = new System.Drawing.Size(64, 24);
            this.label25.TabIndex = 2;
            this.label25.Text = "Latitude:";
            this.label25.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.textBoxLongitud.Location = new System.Drawing.Point(112, 88);
            this.textBoxLongitud.Name = "textBoxLongitud";
            this.textBoxLongitud.Size = new System.Drawing.Size(144, 20);
            this.textBoxLongitud.TabIndex = 1;
            this.textBoxLongitud.TextChanged += new System.EventHandler(this.ControlValueChanged);
            this.textBoxLatitud.Location = new System.Drawing.Point(112, 56);
            this.textBoxLatitud.Name = "textBoxLatitud";
            this.textBoxLatitud.Size = new System.Drawing.Size(144, 20);
            this.textBoxLatitud.TabIndex = 0;
            this.textBoxLatitud.TextChanged += new System.EventHandler(this.ControlValueChanged);
            this.AccessibleName = "";
            this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
            this.ClientSize = new System.Drawing.Size(538, 343);
            this.Controls.Add(this.pictureBoxLogo);
            this.Controls.Add(this.tabControlGPS);
            this.Controls.Add(this.treeViewSources);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.labelSettingup);
            this.Controls.Add(this.progressBarSetup);
            this.Controls.Add(this.labelTrackCode);
            this.Controls.Add(this.StartStop);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "GpsTracker";
            this.ShowInTaskbar = false;
            this.SizeGripStyle = System.Windows.Forms.SizeGripStyle.Hide;
            this.Closing += new System.ComponentModel.CancelEventHandler(this.GpsTracker_Closing);
            this.Load += new System.EventHandler(this.GpsTracker_Load);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBoxLogo)).EndInit();
            this.tabControlGPS.ResumeLayout(false);
            this.tabPageCOM.ResumeLayout(false);
            this.tabPageGeneral.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownAltitud)).EndInit();
            this.tabPageFileHelp.ResumeLayout(false);
            this.tabPageAPRSInternetHelp.ResumeLayout(false);
            this.tabPageUsage.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBoxHelpLogo)).EndInit();
            this.tabPageTCP.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownTCPPort)).EndInit();
            this.tabPageFile.ResumeLayout(false);
            this.tabPageFile.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownReload)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.trackBarFileSpeed)).EndInit();
            this.tabPageUDPHelp.ResumeLayout(false);
            this.tabPageAPRSInternet.ResumeLayout(false);
            this.tabPageAPRSInternet.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownAPRSIS)).EndInit();
            this.tabPageCOMHelp.ResumeLayout(false);
            this.tabPagePOIHelp.ResumeLayout(false);
            this.tabPageTCPHelp.ResumeLayout(false);
            this.tabPageAPRS.ResumeLayout(false);
            this.tabPageAPRS.PerformLayout();
            this.tabPageUDP.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.numericUpDownUDPPort)).EndInit();
            this.tabPageUpdate.ResumeLayout(false);
            this.tabPagePOI.ResumeLayout(false);
            this.tabPagePOI.PerformLayout();
            this.ResumeLayout(false);
  }
  public void Deinitialize()
  {
   try
   {
    m_fInitialized=false;
    m_gpsTrackerPlugin.m_fGpsTrackerRunning=false;
    Stop();
    m_gpsTrackerPlugin.pluginRemoveAllOverlay();
   }
   catch(Exception)
   {
   }
  }
  private bool Start()
  {
   bool fRet=false;
   GPSSource gpsSource;
   if (m_fInitialized)
   {
    progressBarSetup.Visible=false;
    labelSettingup.Visible=false;
    m_iLocked=0;
    m_iPrevLocked=0;
    m_fCloseThreads=false;
    m_fVerticalExaggeration=World.Settings.VerticalExaggeration;
    if (checkBoxVExaggeration.Checked==true)
     World.Settings.VerticalExaggeration=0F;
    labelSettingup.Visible=true;
    labelSettingup.Update();
    progressBarSetup.Visible=true;
    progressBarSetup.Update();
    progressBarSetup.Maximum=m_gpsSourceList.Count+1;
    if (m_fPlayback==false)
    {
     for (int i=0; i<m_gpsSourceList.Count; i++)
     {
      gpsSource=(GPSSource)m_gpsSourceList[i];
      if (checkBoxSetAltitud.Checked && gpsSource.bTrack)
       gpsSource.iStartAltitud = Convert.ToInt32(numericUpDownAltitud.Value);
      else
       gpsSource.iStartAltitud=0;
      if (!gpsSource.bSetup)
       continue;
                        gpsSource.GpsPos.m_gpsTrack = null;
      progressBarSetup.Value=i+1;
                        if (gpsSource.sType == "COM" && gpsSource.GpsCOM.IsOpen)
                            gpsSource.GpsCOM.StartRx();
      else
      if (gpsSource.sType=="POI")
      {
       if (m_hPOIThread==null)
       {
        m_hPOIThread = new Thread(new ThreadStart(this.threadPOI));
        m_hPOIThread.IsBackground = true;
        m_hPOIThread.Priority = System.Threading.ThreadPriority.Normal;
        m_hPOIThread.Start();
       }
      }
      else
      {
       if (gpsSource.sType=="TCP")
       {
        SecurityOptions options;
        if (gpsSource.bSecureSocket)
         options = new SecurityOptions(
          SecureProtocol.Ssl3 | SecureProtocol.Tls1,
          null,
          ConnectionEnd.Client,
          CredentialVerification.None,
          null,
          gpsSource.sTCPAddress,
          SecurityFlags.Default,
          SslAlgorithms.SECURE_CIPHERS,
          null);
        else
         options = new SecurityOptions(SecureProtocol.None, null, ConnectionEnd.Client);
        SecureSocket socketTCP = new SecureSocket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp, options);
        IPHostEntry IPHost = Dns.GetHostEntry(gpsSource.sTCPAddress);
        IPEndPoint ipEndPoint = new IPEndPoint(IPHost.AddressList[0], gpsSource.iTCPPort);
        socketTCP.Blocking = true ;
        AsyncCallback callbackProc = new AsyncCallback(m_UDPTCP.TcpConnectCallback);
                                gpsSource.tcpSockets = new TCPSockets();
                                gpsSource.tcpSockets.sStream = "";
                                gpsSource.tcpSockets.socket = socketTCP;
                                gpsSource.tcpSockets.iDeviceIndex = i;
                                gpsSource.tcpSockets.byTcpBuffer = new byte[1024];
                                gpsSource.tcpSockets.socket.BeginConnect(ipEndPoint, callbackProc, gpsSource.tcpSockets);
       }
       else
       if (gpsSource.sType=="UDP")
       {
        EndPoint endPoint = new IPEndPoint(IPAddress.Any, gpsSource.iUDPPort);
        SecurityOptions options = new SecurityOptions(SecureProtocol.None , null, ConnectionEnd.Client);
        SecureSocket socketUDP = new SecureSocket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp, options);
        socketUDP.Bind(endPoint);
                                gpsSource.tcpSockets = new TCPSockets();
                                gpsSource.tcpSockets.sStream = "";
                                gpsSource.tcpSockets.socket = socketUDP;
                                gpsSource.tcpSockets.iDeviceIndex = i;
                                gpsSource.tcpSockets.byTcpBuffer = new byte[1024];
                                socketUDP.BeginReceiveFrom(gpsSource.tcpSockets.byTcpBuffer, 0, 1024, SocketFlags.None, ref endPoint, new AsyncCallback(m_UDPTCP.UdpReceiveData), gpsSource.tcpSockets);
       }
       else
       if (gpsSource.sType=="File")
       {
        gpsSource.fileThread = new Thread(new ThreadStart(m_File.threadStartFile));
        gpsSource.fileThread.IsBackground = true;
        gpsSource.fileThread.Priority = System.Threading.ThreadPriority.Normal;
        gpsSource.fileThread.Name= i.ToString();
        gpsSource.fileThread.Start();
       }
       else
       if (gpsSource.sType=="APRS Internet")
       {
                                gpsSource.aprsThread = new Thread(new ThreadStart(m_APRS.threadAPRSIS));
                                gpsSource.aprsThread.IsBackground = true;
                                gpsSource.aprsThread.Name = i.ToString();
                                gpsSource.aprsThread.Start();
       }
      }
     }
    }
    else
    {
     for (int i=0; i<m_gpsSourceList.Count; i++)
     {
      gpsSource=(GPSSource)m_gpsSourceList[i];
      gpsSource.iReload=0;
      if (checkBoxSetAltitud.Checked && gpsSource.bTrack)
       gpsSource.iStartAltitud = Convert.ToInt32(numericUpDownAltitud.Value);
      else
       gpsSource.iStartAltitud=0;
      if (!gpsSource.bSetup)
       continue;
                        gpsSource.GpsPos.m_gpsTrack = null;
      progressBarSetup.Value=i+1;
      if (gpsSource.sType=="POI")
      {
       if (m_hPOIThread==null)
       {
        m_hPOIThread = new Thread(new ThreadStart(this.threadPOI));
        m_hPOIThread.IsBackground = true;
        m_hPOIThread.Priority = System.Threading.ThreadPriority.Normal;
        m_hPOIThread.Start();
       }
      }
      else
      {
       if (gpsSource.bTrackAtOnce!=true)
       {
                                gpsSource.fileThread = new Thread(new ThreadStart(m_File.threadFile));
                                gpsSource.fileThread.IsBackground = true;
                                gpsSource.fileThread.Priority = System.Threading.ThreadPriority.Normal;
                                gpsSource.fileThread.Name = i.ToString();
        gpsSource.sFileNameSession=m_sPlaybackFile;
        if (gpsSource.sType=="File")
         gpsSource.iFilePlaySpeed=gpsSource.iPlaySpeed;
        else
         gpsSource.iFilePlaySpeed=1;
                                gpsSource.fileThread.Start();
       }
       else
       {
        gpsSource.sFileName=gpsSource.sFileName.Replace("*PLUGINDIR*",GpsTrackerPlugin.m_sPluginDirectory);
        if (File.Exists(gpsSource.sFileName))
        {
         m_File.PreprocessFile(i,gpsSource.sFileName,gpsSource.sFileName,true,gpsSource.bForcePreprocessing);
         GPSRenderInformation renderInfo = new GPSRenderInformation();
         renderInfo.bPOI=false;
         renderInfo.iIndex=i;
         renderInfo.sDescription=gpsSource.sDescription;
         renderInfo.fFix=false;
         renderInfo.bShowInfo=m_bInfoText;
         renderInfo.bTrackLine=false;
                                    renderInfo.gpsTrack = gpsSource.GpsPos.m_gpsTrack;
         renderInfo.bRestartTrack=false;
                                    renderInfo.iDay = gpsSource.GpsPos.m_iDay;
                                    renderInfo.iMonth = gpsSource.GpsPos.m_iMonth;
                                    renderInfo.iYear = gpsSource.GpsPos.m_iYear;
         renderInfo.colorTrack=gpsSource.colorTrack;
         m_gpsTrackerPlugin.pluginShowOverlay(renderInfo);
         if (gpsSource.bTrack==true)
                                        m_gpsTrackerPlugin.pluginWorldWindowGotoLatLonHeading(gpsSource.GpsPos.m_gpsTrack.m_fLat[0], gpsSource.GpsPos.m_gpsTrack.m_fLat[0], -1F, gpsSource.iStartAltitud);
         if (gpsSource.iStartAltitud>0)
         {
          Thread.Sleep(3000);
          gpsSource.iStartAltitud=0;
         }
        }
       }
      }
     }
    }
    progressBarSetup.Value=m_gpsSourceList.Count+1;
    if (m_timerLocked==null)
     m_timerLocked = new System.Threading.Timer(new TimerCallback(timerCallbackLocked),0, 500, 5000);
    fRet=true;
   }
   return fRet;
  }
  public void COMCallback(int iGPSIndex, string sRawData)
  {
   lock(LockCOM)
   {
                GPSSource gpsSource = (GPSSource)m_gpsSourceList[iGPSIndex];
                gpsSource.sCOMData = gpsSource.sCOMData + sRawData;
    int iIndex=-1;
    char [] cEOL = {'\n', '\r'};
    do
    {
                    iIndex = gpsSource.sCOMData.IndexOfAny(cEOL);
     if (iIndex>=0)
     {
                        string sData = gpsSource.sCOMData.Substring(0, iIndex);
      ShowGPSIcon(sData.ToCharArray(),sData.Length,false,iGPSIndex,false,true);
                        gpsSource.sCOMData = gpsSource.sCOMData.Remove(0, iIndex + 1);
     }
    }
    while(iIndex>=0);
   }
  }
  public bool Stop()
  {
   bool fRet=true;
   try
   {
       if (m_MessageMonitor!=null)
       {
        m_MessageMonitor.Close();
        m_MessageMonitor=null;
       }
   }
   catch (Exception)
   {
    m_MessageMonitor=null;
   }
   if (checkBoxVExaggeration.Checked==true)
    World.Settings.VerticalExaggeration=m_fVerticalExaggeration;
   m_fCloseThreads=true;
   Thread.Sleep(500);
   if (m_timerLocked!=null)
   {
    m_timerLocked.Dispose();
    m_timerLocked=null;
   }
   CloseCOMs();
   if (m_hPOIThread!=null)
   {
    m_hPOIThread.Interrupt();
    m_hPOIThread.Abort();
    m_hPOIThread.Join(1000);
    m_hPOIThread=null;
   }
   for (int i=0; i<m_gpsSourceList.Count; i++)
   {
                GPSSource gpsSource = (GPSSource)m_gpsSourceList[i];
                if (gpsSource.tcpSockets.socket != null)
    {
                    gpsSource.tcpSockets.socket.Close();
                    gpsSource.tcpSockets.socket = null;
                    gpsSource.tcpSockets.sStream = "";
    }
                if (gpsSource.fileThread != null)
                {
                    gpsSource.fileThread.Interrupt();
                    gpsSource.fileThread.Abort();
                    gpsSource.fileThread.Join(1000);
                    gpsSource.fileThread = null;
                }
                if (gpsSource.aprsThread != null)
    {
                    gpsSource.aprsThread.Interrupt();
                    gpsSource.aprsThread.Abort();
                    gpsSource.aprsThread.Join(1000);
                    gpsSource.aprsThread = null;
    }
   }
   if (m_swRecorder!=null)
   {
    m_swRecorder.Close();
    m_swRecorder=null;
    try
    {
     SaveFileDialog dlgSaveFile = new SaveFileDialog();
     dlgSaveFile.Title = "Select file to save recorded Session" ;
     dlgSaveFile.Filter = "GPSTrackerSession (*.GPSTrackerSession)|*.GPSTrackerSession" ;
     dlgSaveFile.FilterIndex = 0 ;
     dlgSaveFile.RestoreDirectory = true ;
     if(dlgSaveFile.ShowDialog() == DialogResult.OK)
                        File.Copy(GpsTrackerPlugin.m_sPluginDirectory + "\\GpsTracker.recording", dlgSaveFile.FileName, true);
                    File.Delete(GpsTrackerPlugin.m_sPluginDirectory + "\\GpsTracker.recording");
    }
    catch(Exception)
    {
    }
   }
   if (m_srReader!=null)
   {
    m_srReader.Close();
    m_srReader=null;
   }
   m_gpsTrackerPlugin.pluginRemoveAllOverlay();
   m_gpsTrackerPlugin.gpsOverlay.IsOn = false;
   SaveSettings(null);
            m_fInitialized = false;
   return fRet;
  }
  private void CloseCOMs()
  {
            for (int i = 0; i < m_gpsSourceList.Count; i++)
            {
                GPSSource gpsSource = (GPSSource)m_gpsSourceList[i];
                gpsSource.sCOMData = "";
                if (gpsSource.GpsCOM != null)
    {
                    gpsSource.GpsCOM.Close();
                    gpsSource.GpsCOM = null;
    }
   }
  }
  public void SetActiveTrack(int iIndex, bool bTrack)
  {
   GPSSource gpsSource;
   if (bTrack)
   {
    for (int i=0; i<m_gpsSourceList.Count; i++)
    {
     gpsSource = (GPSSource)m_gpsSourceList[i];
     gpsSource.treeNode.NodeFont = new System.Drawing.Font(treeViewSources.Font, FontStyle.Regular);
     gpsSource.bTrack=false;
    }
    gpsSource = (GPSSource)m_gpsSourceList[iIndex];
    gpsSource.bTrack=true;
    gpsSource.treeNode.NodeFont = new System.Drawing.Font(treeViewSources.Font, FontStyle.Bold);
   }
   else
   {
    gpsSource = (GPSSource)m_gpsSourceList[iIndex];
    gpsSource.treeNode.NodeFont = new System.Drawing.Font(treeViewSources.Font, FontStyle.Regular);
    gpsSource.bTrack=false;
   }
   SaveSettings(null);
  }
  public void SetTrackHeading(bool bTrackHeading)
  {
   m_bTrackHeading=bTrackHeading;
   checkBoxTrackHeading.Checked=m_bTrackHeading;
   SaveSettings(null);
  }
  public void SetTrackLine(bool bTrackLine)
  {
   m_bTrackLine=bTrackLine;
   checkBoxTrackLine.Checked=m_bTrackLine;
   SaveSettings(null);
  }
  public int GetActiveTrack()
  {
   int i=-1;
   for (i=0; i<m_gpsSourceList.Count; i++)
   {
    GPSSource gpsSource=(GPSSource)m_gpsSourceList[i];
    if (gpsSource.bTrack==true)
     break;
   }
   return i;
  }
  public bool ShowGPSIcon(char [] cNMEAMessage, int iMsgLength, bool fCheck, int iIndex, bool bRestartTrack, bool bParse)
  {
   bool bRet=false;
   lock(LockShowIcon)
   {
    GPSSource gpsSource = (GPSSource)m_gpsSourceList[iIndex];
   try
   {
    if (bParse)
     bRet=m_NMEA.ParseGPSMessage(cNMEAMessage,iMsgLength,fCheck,iIndex);
    else
     bRet=true;
    if (bRet==true && m_fInitialized==true)
    {
     if(m_swRecorder!=null)
     {
      String sMsg = new String(cNMEAMessage);
      char [] cAny = new char[3]; cAny[0]='\r'; cAny[1]='\n'; cAny[2]='\0';
      int iEnd=sMsg.IndexOfAny(cAny);
      if (iEnd>0)
       sMsg=sMsg.Substring(0,iEnd);
      sMsg = Convert.ToString(iIndex) + "," + sMsg;
      m_swRecorder.WriteLine(sMsg);
     }
     m_gpsTrackerPlugin.pluginLocked(true);
     string sPortInfo=gpsSource.sType;
     switch (sPortInfo)
     {
      case "UDP":
       sPortInfo += ": Port " + Convert.ToString(gpsSource.iUDPPort);
       break;
      case "COM":
       sPortInfo += ": Port " + Convert.ToString(gpsSource.iCOMPort);
       break;
      case "TCP":
       sPortInfo += ": IP:Port " + gpsSource.sTCPAddress + ":" + Convert.ToString(gpsSource.iTCPPort);
       break;
      case "File":
       string sFileName=gpsSource.sFileName;
       int iIndexName = sFileName.LastIndexOf('\\');
       if (iIndexName>=0 && iIndexName<sFileName.Length-1)
        sFileName=sFileName.Substring(iIndexName+1);
       sPortInfo += ": " + sFileName;
       break;
     }
     if (m_fInitialized==true)
     {
      GPSRenderInformation renderInfo = new GPSRenderInformation();
      renderInfo.fTrack=gpsSource.bTrack;
      renderInfo.bPOI=false;
      renderInfo.iIndex=iIndex;
                        if (gpsSource.GpsPos.m_sName != "")
                            renderInfo.sDescription = gpsSource.GpsPos.m_sName;
      else
       renderInfo.sDescription=gpsSource.sDescription;
                        renderInfo.sComment = gpsSource.GpsPos.m_sComment;
                        renderInfo.iAPRSIconTable = gpsSource.GpsPos.m_iAPRSIconTable;
                        renderInfo.iAPRSIconCode = gpsSource.GpsPos.m_iAPRSIconCode;
      renderInfo.fFix=true;
                        renderInfo.fLat = gpsSource.GpsPos.m_fLat;
                        renderInfo.fLon = gpsSource.GpsPos.m_fLon;
                        renderInfo.fAlt = gpsSource.GpsPos.m_fAlt;
                        renderInfo.fRoll = gpsSource.GpsPos.m_fRoll;
                        renderInfo.fPitch = gpsSource.GpsPos.m_fPitch;
                        renderInfo.fDepth = gpsSource.GpsPos.m_fDepth;
                        renderInfo.sAltUnit = gpsSource.GpsPos.m_sAltUnit;
                        renderInfo.sSpeedUnit = gpsSource.GpsPos.m_sSpeedUnit;
                        renderInfo.fSpeed = gpsSource.GpsPos.m_fSpeed;
                        renderInfo.fESpeed = gpsSource.GpsPos.m_fESpeed;
                        renderInfo.fNSpeed = gpsSource.GpsPos.m_fNSpeed;
                        renderInfo.fVSpeed = gpsSource.GpsPos.m_fVSpeed;
                        renderInfo.fHeading = gpsSource.GpsPos.m_fHeading;
                        if (gpsSource.GpsPos.m_iAPRSIconCode >= 0 && gpsSource.GpsPos.m_iAPRSIconTable >= 0)
      {
       string sPath="";
                            if (gpsSource.GpsPos.m_iAPRSIconTable == Convert.ToInt32('/'))
                                sPath = GpsTrackerPlugin.m_sPluginDirectory + "\\aprs\\primary";
                            if (gpsSource.GpsPos.m_iAPRSIconTable == Convert.ToInt32('\\'))
                                sPath = GpsTrackerPlugin.m_sPluginDirectory + "\\aprs\\secondary";
       if (sPath!="")
       {
                                string sFileName = String.Format("{0:000}", gpsSource.GpsPos.m_iAPRSIconCode) + ".png";
        if (File.Exists(sPath + "\\" + sFileName))
         sPath=sPath + "\\" + sFileName;
        else
         sPath=gpsSource.sIconPath;
       }
       else
        sPath=gpsSource.sIconPath;
       renderInfo.sIcon=sPath;
      }
      else
       renderInfo.sIcon=gpsSource.sIconPath;
      renderInfo.iStartAltitud=gpsSource.iStartAltitud;
      renderInfo.sPortInfo=sPortInfo;
                        renderInfo.iHour = gpsSource.GpsPos.m_iHour;
                        renderInfo.iMin = gpsSource.GpsPos.m_iMin;
                        renderInfo.fSec = gpsSource.GpsPos.m_iSec;
      renderInfo.bShowInfo=m_bInfoText;
      renderInfo.bTrackLine=m_bTrackLine;
                        renderInfo.gpsTrack = gpsSource.GpsPos.m_gpsTrack;
      renderInfo.bRestartTrack=bRestartTrack;
                        renderInfo.iDay = gpsSource.GpsPos.m_iDay;
                        renderInfo.iMonth = gpsSource.GpsPos.m_iMonth;
                        renderInfo.iYear = gpsSource.GpsPos.m_iYear;
      renderInfo.colorTrack=gpsSource.colorTrack;
      m_gpsTrackerPlugin.pluginShowOverlay(renderInfo);
      if (gpsSource.iStartAltitud>0)
      {
       Thread.Sleep(3000);
       gpsSource.iStartAltitud=0;
      }
     }
     m_iLocked++;
    }
    try
    {
     if (m_MessageMonitor!=null && bRet)
       m_MessageMonitor.AddMessage(cNMEAMessage, iMsgLength);
    }
    catch (Exception)
    {
     m_MessageMonitor=null;
    }
     }
   catch (Exception)
   {
   }
   }
   return bRet;
  }
  public void AddPOI(string sName, double fLat, double fLon)
  {
   lock (LockPOI)
   {
    GPSSource gpsSource = new GPSSource();
    gpsSource.sType="POI";
    gpsSource.iNameIndex=GetAvailableIndex();
    if (sName=="")
     gpsSource.sDescription=gpsSource.sType + " #" + Convert.ToString(gpsSource.iNameIndex);
    else
     gpsSource.sDescription=sName;
    gpsSource.bTrack=false;
    gpsSource.bSetup=false;
    gpsSource.bPOISet=false;
    TreeNode treeNode = new TreeNode(gpsSource.sDescription);
    treeNode.ImageIndex=1;
    treeNode.SelectedImageIndex=1;
    treeNode.Tag=gpsSource;
    gpsSource.treeNode=treeNode;
    gpsSource.bNeedApply=false;
    gpsSource.fLat=fLat;
    gpsSource.fLon=fLon;
    m_gpsSourceList.Add(gpsSource);
    ApplySettings(gpsSource,true,false,true);
    m_treeNodePOI.Nodes.Add(treeNode);
    m_treeNodePOI.ExpandAll();
    m_iSourceNameCount++;
    if (m_hPOIThread==null)
    {
     m_hPOIThread = new Thread(new ThreadStart(this.threadPOI));
     m_hPOIThread.IsBackground = true;
     m_hPOIThread.Priority = System.Threading.ThreadPriority.Normal;
     m_hPOIThread.Start();
    }
   }
  }
  public void SaveSettings(StreamWriter sWriter)
  {
   int i;
   if (m_fPlayback==false)
   {
    StreamWriter sw;
    try
    {
     if (sWriter!=null)
      sw=sWriter;
     else
                        sw = File.CreateText(GpsTrackerPlugin.m_sPluginDirectory + "\\GpsTracker.cfg");
     sw.WriteLine("GPSTracker Version " + m_gpsTrackerPlugin.m_sVersion + " settings...");
     sw.WriteLine("checkBoxSecureSocket=" + checkBoxSecureSocket.Checked.ToString());
     sw.WriteLine("comboBoxCOMPort=" + comboBoxCOMPort.Text);
     sw.WriteLine("comboBoxBaudRate=" + comboBoxBaudRate.Text);
     sw.WriteLine("comboBoxByteSize=" + comboBoxByteSize.Text);
     sw.WriteLine("comboParity=" + comboParity.SelectedIndex.ToString());
     sw.WriteLine("comboBoxStopBits=" + comboBoxStopBits.SelectedIndex.ToString());
     sw.WriteLine("numericUpDownUDPPort=" + numericUpDownUDPPort.Value.ToString());
     sw.WriteLine("numericUpDownTCPPort=" + numericUpDownTCPPort.Value.ToString());
     sw.WriteLine("numericUpDownReload=" + numericUpDownReload.Value.ToString());
     sw.WriteLine("comboBoxTcpIP=" + comboBoxTcpIP.Text.Trim());
     sw.WriteLine("comboBoxAPRSInternetServer=" + comboBoxAPRSInternetServer.Text.Trim());
     sw.WriteLine("comboBoxFile=" + comboBoxFile.Text.Trim());
     sw.WriteLine("trackBarFileSpeed=" + trackBarFileSpeed.Value.ToString());
     sw.WriteLine("m_bTrackHeading=" + m_bTrackHeading.ToString());
     sw.WriteLine("m_bTrackLine=" + m_bTrackLine.ToString());
     sw.WriteLine("m_bRecordSession=" + m_bRecordSession.ToString());
     sw.WriteLine("m_bInfoText=" + m_bInfoText.ToString());
     sw.WriteLine("checkBoxNoDelay=" + checkBoxNoDelay.Checked.ToString());
     sw.WriteLine("comboBoxFlowControl=" + comboBoxFlowControl.SelectedIndex.ToString());
     sw.WriteLine("buttonTrackColorCOM=" + Convert.ToString(buttonTrackColorCOM.BackColor.ToArgb()));
     sw.WriteLine("buttonTrackColorTCP=" + Convert.ToString(buttonTrackColorTCP.BackColor.ToArgb()));
     sw.WriteLine("buttonTrackColorUDP=" + Convert.ToString(buttonTrackColorUDP.BackColor.ToArgb()));
     sw.WriteLine("buttonTrackColor=" + Convert.ToString(buttonTrackColor.BackColor.ToArgb()));
     string sCallSignFilter="";
     for (i=0; i<textBoxCallSignFilter.Lines.Length; i++)
      if (textBoxCallSignFilter.Lines[i].Length>=1)
       sCallSignFilter+=(textBoxCallSignFilter.Lines[i] + ",");
     sw.WriteLine("textBoxCallSignFilter=" + sCallSignFilter);
     sw.WriteLine("textBoxAPRSISCallSign=" + textBoxAPRSISCallSign.Text);
     sw.WriteLine("numericUpDownAPRSIS=" + numericUpDownAPRSIS.Value.ToString());
     sw.WriteLine("buttonTrackColorAPRS=" + Convert.ToString(buttonTrackColorAPRS.BackColor.ToArgb()));
     sw.WriteLine("textBoxLongitud=" + textBoxLongitud.Text);
     sw.WriteLine("textBoxLatitud=" + textBoxLatitud.Text);
     sw.WriteLine("numericUpDownAltitud=" + numericUpDownAltitud.Value.ToString());
     sw.WriteLine("checkBoxSetAltitud=" + checkBoxSetAltitud.Checked.ToString());
     sw.WriteLine("comboBoxAPRSInternetServer=" + comboBoxAPRSInternetServer.Text.Trim());
     sw.WriteLine("FILE COMBOBOX COUNT=" + Convert.ToString(comboBoxFile.Items.Count));
     for (i=0; i<comboBoxFile.Items.Count; i++)
      sw.WriteLine((string)comboBoxFile.Items[i]);
     sw.WriteLine("TCPIP COMBOBOX COUNT=" + Convert.ToString(comboBoxTcpIP.Items.Count));
     for (i=0; i<comboBoxTcpIP.Items.Count; i++)
      sw.WriteLine((string)comboBoxTcpIP.Items[i]);
     sw.WriteLine("APRSINTERNET COMBOBOX COUNT=" + Convert.ToString(comboBoxAPRSInternetServer.Items.Count));
     for (i=0; i<comboBoxAPRSInternetServer.Items.Count; i++)
      sw.WriteLine((string)comboBoxAPRSInternetServer.Items[i]);
     sw.WriteLine("END UI CONTROLS");
     sw.WriteLine("SOURCE COUNT=" + Convert.ToString(m_gpsSourceList.Count));
     for (i=0; i<m_gpsSourceList.Count; i++)
     {
      GPSSource gpsSource = (GPSSource)m_gpsSourceList[i];
      sw.WriteLine("iNameIndex=" + Convert.ToString(gpsSource.iNameIndex));
      sw.WriteLine("bNeedApply=" + Convert.ToString(gpsSource.bNeedApply));
      sw.WriteLine("bSetup=" + Convert.ToString(gpsSource.bSetup));
      sw.WriteLine("sType=" + gpsSource.sType);
      sw.WriteLine("sDescription=" + gpsSource.sDescription);
      sw.WriteLine("sComment=" + gpsSource.sComment);
      sw.WriteLine("sIconPath=" + gpsSource.sIconPath);
      sw.WriteLine("colorTrack=" + Convert.ToString(gpsSource.colorTrack.ToArgb()));
      sw.WriteLine("bTrack=" + Convert.ToString(gpsSource.bTrack));
      sCallSignFilter="";
      if (gpsSource.sCallSignFilterLines!=null)
      {
       for (int ii=0; ii<gpsSource.sCallSignFilterLines.Length; ii++)
        if (gpsSource.sCallSignFilterLines[ii].Length>=1)
         sCallSignFilter+=(gpsSource.sCallSignFilterLines[ii] + ",");
      }
      if (sCallSignFilter=="")
       sCallSignFilter="*";
      sw.WriteLine("sCallSignFilter=" + sCallSignFilter);
      switch(gpsSource.sType)
      {
       case "COM":
        sw.WriteLine("iCOMPort=" + Convert.ToString(gpsSource.iCOMPort));
        sw.WriteLine("iBaudRate=" + Convert.ToString(gpsSource.iBaudRate));
        sw.WriteLine("iByteSize=" + Convert.ToString(gpsSource.iByteSize));
        sw.WriteLine("iSelectedItem=" + Convert.ToString(gpsSource.iSelectedItem));
        sw.WriteLine("iParity=" + Convert.ToString(gpsSource.iParity));
        sw.WriteLine("iStopBits=" + Convert.ToString(gpsSource.iStopBits));
        sw.WriteLine("iFlowControl=" + Convert.ToString(gpsSource.iFlowControl));
        break;
       case "UDP":
        sw.WriteLine("iUDPPort=" + Convert.ToString(gpsSource.iUDPPort));
        break;
       case "TCP":
        sw.WriteLine("sTCPAddress=" + gpsSource.sTCPAddress);
        sw.WriteLine("iTCPPort=" + Convert.ToString(gpsSource.iTCPPort));
        sw.WriteLine("bSecureSocket=" + Convert.ToString(gpsSource.bSecureSocket));
        break;
       case "File":
        sw.WriteLine("sFileName=" + gpsSource.sFileName);
        sw.WriteLine("bNoDelay=" + Convert.ToString(gpsSource.bNoDelay));
        sw.WriteLine("bTrackAtOnce=" + Convert.ToString(gpsSource.bTrackAtOnce));
        sw.WriteLine("iPlaySpeed=" + Convert.ToString(gpsSource.iPlaySpeed));
        sw.WriteLine("iFilePlaySpeed=" + Convert.ToString(gpsSource.iFilePlaySpeed));
        sw.WriteLine("sFileNameSession=" + gpsSource.sFileNameSession);
        sw.WriteLine("iReload=" + Convert.ToString(gpsSource.iReload));
        sw.WriteLine("bForcePreprocessing=" + Convert.ToString(gpsSource.bForcePreprocessing));
        sw.WriteLine("bSession=" + Convert.ToString(gpsSource.bSession));
        break;
       case "APRS Internet":
        sw.WriteLine("sAPRSServerURL=" + gpsSource.sAPRSServerURL);
        sw.WriteLine("sCallSign=" + gpsSource.sCallSign);
        sw.WriteLine("iRefreshRate=" + Convert.ToString(gpsSource.iRefreshRate));
        sw.WriteLine("iServerIndex=" + Convert.ToString(gpsSource.iServerIndex));
        break;
       case "POI":
        sw.WriteLine("fLat=" + Convert.ToString(gpsSource.fLat));
        sw.WriteLine("fLon=" + Convert.ToString(gpsSource.fLon));
        break;
      }
      sw.WriteLine("END SOURCE INDEX=" + Convert.ToString(i));
     }
     if (sWriter==null)
      sw.Close();
     else
      sw.WriteLine("--------------------------------------------------");
    }
    catch(Exception)
    {
    }
   }
  }
  public bool LoadSettings(StreamReader srReader, bool bSet)
  {
   bool bRet=false;
   Bitmap image;
   StreamReader sr;
   m_bHandleControlValueChangeEvent=false;
   if (bSet)
   {
    SetupTree();
    m_bHandleControlValueChangeEvent=false;
    m_gpsSourceList.Clear();
    progressBarPreprocessing.Value=0;
    progressBarSetup.Value=0;
    comboBoxFile.Items.Clear();
    comboBoxTcpIP.Items.Clear();
    comboBoxAPRSInternetServer.Items.Clear();
    checkBoxNoDelay.Checked=false;
    checkBoxSecureSocket.Checked=false;
    progressBarSetup.Visible=false;
    labelSettingup.Visible=false;
    labelPreprocessing.Visible=false;
    progressBarPreprocessing.Visible=false;
                image = new Bitmap(GpsTrackerPlugin.m_sPluginDirectory + "\\satellite.png");
    imageListGpsIcons.Images.Add(image);
                image = new Bitmap(GpsTrackerPlugin.m_sPluginDirectory + "\\gpsnotset.png");
    imageListGpsIcons.Images.Add(image);
   }
   try
   {
    if (srReader==null)
    {
                    if (!File.Exists(GpsTrackerPlugin.m_sPluginDirectory + "\\GpsTracker.cfg"))
     {
      SetDefaultSettings(bSet);
      return false;
     }
                    sr = File.OpenText(GpsTrackerPlugin.m_sPluginDirectory + "\\GpsTracker.cfg");
    }
    else
     sr=srReader;
   {
    string line=sr.ReadLine();
    if (line!=null && line.StartsWith("GPSTracker Version") )
    {
     if (bSet)
     {
      while(true)
      {
       line = sr.ReadLine();
       if (line==null || line.StartsWith("END UI CONTROLS"))
        break;
       if (line.StartsWith("checkBoxSecureSocket="))
        checkBoxSecureSocket.Checked=Convert.ToBoolean(line.Remove(0,"checkBoxSecureSocket=".Length));
       else
       if (line.StartsWith("comboBoxCOMPort="))
        comboBoxCOMPort.Text=line.Remove(0,"comboBoxCOMPort=".Length);
       else
        if (line.StartsWith("comboBoxBaudRate="))
        comboBoxBaudRate.Text=line.Remove(0,"comboBoxBaudRate=".Length);
       else
        if (line.StartsWith("comboBoxByteSize="))
        comboBoxByteSize.Text=line.Remove(0,"comboBoxByteSize=".Length);
       else
        if (line.StartsWith("comboParity="))
        comboParity.SelectedIndex=Convert.ToInt32(line.Remove(0,"comboParity=".Length));
       else
        if (line.StartsWith("comboBoxStopBits="))
        comboBoxStopBits.SelectedIndex=Convert.ToInt32(line.Remove(0,"comboBoxStopBits=".Length));
       else
        if (line.StartsWith("numericUpDownUDPPort="))
        numericUpDownUDPPort.Value=Convert.ToDecimal(line.Remove(0,"numericUpDownUDPPort=".Length));
       else
        if (line.StartsWith("numericUpDownTCPPort="))
        numericUpDownTCPPort.Value=Convert.ToDecimal(line.Remove(0,"numericUpDownTCPPort=".Length));
       else
        if (line.StartsWith("numericUpDownReload="))
        numericUpDownReload.Value=Convert.ToDecimal(line.Remove(0,"numericUpDownReload=".Length));
       else
        if (line.StartsWith("comboBoxTcpIP="))
        comboBoxTcpIP.Text=line.Remove(0,"comboBoxTcpIP=".Length);
       else
        if (line.StartsWith("comboBoxAPRSInternetServer="))
        comboBoxAPRSInternetServer.Text=line.Remove(0,"comboBoxAPRSInternetServer=".Length);
       else
        if (line.StartsWith("comboBoxFile="))
        comboBoxFile.Text=line.Remove(0,"comboBoxFile=".Length);
       else
        if (line.StartsWith("trackBarFileSpeed="))
        trackBarFileSpeed.Value=Convert.ToInt32(line.Remove(0,"trackBarFileSpeed=".Length));
       else
        if (line.StartsWith("m_bTrackHeading="))
        m_bTrackHeading=Convert.ToBoolean(line.Remove(0,"m_bTrackHeading=".Length));
       else
        if (line.StartsWith("m_bTrackLine="))
        m_bTrackLine=Convert.ToBoolean(line.Remove(0,"m_bTrackLine=".Length));
       else
        if (line.StartsWith("m_bInfoText="))
        m_bInfoText=Convert.ToBoolean(line.Remove(0,"m_bInfoText=".Length));
       else
        if (line.StartsWith("checkBoxNoDelay="))
        checkBoxNoDelay.Checked=Convert.ToBoolean(line.Remove(0,"checkBoxNoDelay=".Length));
       else
        if (line.StartsWith("checkBoxNoDelay="))
        checkBoxNoDelay.Checked=Convert.ToBoolean(line.Remove(0,"checkBoxNoDelay=".Length));
       else
        if (line.StartsWith("comboBoxFlowControl="))
        comboBoxFlowControl.SelectedIndex=Convert.ToInt32(line.Remove(0,"comboBoxFlowControl=".Length));
       else
        if (line.StartsWith("buttonTrackColorCOM="))
        buttonTrackColorCOM.BackColor=Color.FromArgb(Convert.ToInt32(line.Remove(0,"buttonTrackColorCOM=".Length)));
       else
        if (line.StartsWith("buttonTrackColorTCP="))
        buttonTrackColorTCP.BackColor=Color.FromArgb(Convert.ToInt32(line.Remove(0,"buttonTrackColorTCP=".Length)));
       else
        if (line.StartsWith("buttonTrackColorUDP="))
        buttonTrackColorUDP.BackColor=Color.FromArgb(Convert.ToInt32(line.Remove(0,"buttonTrackColorUDP=".Length)));
       else
        if (line.StartsWith("buttonTrackColor="))
        buttonTrackColor.BackColor=Color.FromArgb(Convert.ToInt32(line.Remove(0,"buttonTrackColor=".Length)));
       else
        if (line.StartsWith("textBoxCallSignFilter="))
       {
        CSVReader csvReader=new CSVReader();
        string [] sLines=csvReader.GetCSVLine(line.Remove(0,"textBoxCallSignFilter=".Length));
        if (sLines!=null && sLines.Length>0)
         textBoxCallSignFilter.Lines=(string[])sLines.Clone();
        else
         textBoxCallSignFilter.Text="";
       }
       else
        if (line.StartsWith("FILE COMBOBOX COUNT"))
       {
        int iCount=Convert.ToInt32(line.Remove(0,"FILE COMBOBOX COUNT=".Length));
        for (int i=0; i<iCount; i++)
         comboBoxFile.Items.Add(sr.ReadLine());
       }
       else
       if (line.StartsWith("TCPIP COMBOBOX COUNT"))
       {
        int iCount=Convert.ToInt32(line.Remove(0,"TCPIP COMBOBOX COUNT=".Length));
        for (int i=0; i<iCount; i++)
         comboBoxTcpIP.Items.Add(sr.ReadLine());
       }
       else
        if (line.StartsWith("APRSINTERNET COMBOBOX COUNT"))
       {
        int iCount=Convert.ToInt32(line.Remove(0,"APRSINTERNET COMBOBOX COUNT=".Length));
        for (int i=0; i<iCount; i++)
         comboBoxAPRSInternetServer.Items.Add(sr.ReadLine());
       }
       else
        if (line.StartsWith("textBoxAPRSISCallSign="))
        textBoxAPRSISCallSign.Text=line.Remove(0,"textBoxAPRSISCallSign=".Length);
       else
        if (line.StartsWith("buttonTrackColorAPRS="))
        buttonTrackColorAPRS.BackColor=Color.FromArgb(Convert.ToInt32(line.Remove(0,"buttonTrackColorAPRS=".Length)));
       else
        if (line.StartsWith("numericUpDownAPRSIS="))
        numericUpDownAPRSIS.Value=Convert.ToDecimal(line.Remove(0,"numericUpDownAPRSIS=".Length));
       else
        if (line.StartsWith("textBoxLongitud="))
        textBoxLongitud.Text=line.Remove(0,"textBoxLongitud=".Length);
       else
        if (line.StartsWith("textBoxLatitud="))
        textBoxLatitud.Text=line.Remove(0,"textBoxLatitud=".Length);
       else
        if (line.StartsWith("numericUpDownAltitud="))
        numericUpDownAltitud.Value=Convert.ToDecimal(line.Remove(0,"numericUpDownAltitud=".Length));
       else
        if (line.StartsWith("checkBoxSetAltitud="))
        checkBoxSetAltitud.Checked=Convert.ToBoolean(line.Remove(0,"checkBoxSetAltitud=".Length));
       else
        if (line.StartsWith("comboBoxAPRSInternetServer="))
        comboBoxAPRSInternetServer.SelectedIndex=Convert.ToInt32(line.Remove(0,"comboBoxAPRSInternetServer=".Length));
      }
     }
     int iSourceCount=0;
     line = sr.ReadLine();
     if (line!=null && line.StartsWith("SOURCE COUNT"))
      iSourceCount=Convert.ToInt32(line.Remove(0,"SOURCE COUNT=".Length));
     for (int i=0; i<iSourceCount; i++)
     {
      GPSSource gpsS=new GPSSource();
      gpsS.bNeedApply=true;
      m_gpsSourceList.Add(gpsS);
     }
     int iItem=0;
     while(true)
     {
      line = sr.ReadLine();
      if (line!=null && line.StartsWith("END SOURCE INDEX"))
      {
       iItem++;
       m_iSourceNameCount++;
       line = sr.ReadLine();
      }
      if (line==null || (srReader!=null && line=="--------------------------------------------------"))
      {
       bRet=true;
       break;
      }
      if (bSet)
      {
       GPSSource gpsSource = (GPSSource)m_gpsSourceList[iItem];
       if (line.StartsWith("iNameIndex="))
        gpsSource.iNameIndex=Convert.ToInt32(line.Remove(0,"iNameIndex=".Length));
       else
        if (line.StartsWith("bSecureSocket="))
        gpsSource.bSecureSocket=Convert.ToBoolean(line.Remove(0,"bSecureSocket=".Length));
       else
        if (line.StartsWith("bNeedApply="))
        gpsSource.bNeedApply=Convert.ToBoolean(line.Remove(0,"bNeedApply=".Length));
       else
        if (line.StartsWith("bSetup="))
        gpsSource.bSetup=Convert.ToBoolean(line.Remove(0,"bSetup=".Length));
       else
        if (line.StartsWith("sType="))
        gpsSource.sType=line.Remove(0,"sType=".Length);
       else
        if (line.StartsWith("sDescription="))
        gpsSource.sDescription=line.Remove(0,"sDescription=".Length);
       else
        if (line.StartsWith("sComment="))
        gpsSource.sComment=line.Remove(0,"sComment=".Length);
       else
        if (line.StartsWith("sIconPath="))
        gpsSource.sIconPath=line.Remove(0,"sIconPath=".Length);
       else
        if (line.StartsWith("colorTrack="))
        gpsSource.colorTrack=Color.FromArgb(Convert.ToInt32(line.Remove(0,"colorTrack=".Length)));
       else
        if (line.StartsWith("fLat="))
        gpsSource.fLat=Convert.ToDouble(line.Remove(0,"fLat=".Length));
       else
        if (line.StartsWith("fLon="))
        gpsSource.fLon=Convert.ToDouble(line.Remove(0,"fLon=".Length));
       else
        if (line.StartsWith("bTrack="))
        gpsSource.bTrack=Convert.ToBoolean(line.Remove(0,"bTrack=".Length));
       else
        if (line.StartsWith("iCOMPort="))
        gpsSource.iCOMPort=Convert.ToInt32(line.Remove(0,"iCOMPort=".Length));
       else
        if (line.StartsWith("iBaudRate="))
        gpsSource.iBaudRate=Convert.ToInt32(line.Remove(0,"iBaudRate=".Length));
       else
        if (line.StartsWith("iByteSize="))
        gpsSource.iByteSize=Convert.ToInt32(line.Remove(0,"iByteSize=".Length));
       else
        if (line.StartsWith("iSelectedItem="))
        gpsSource.iSelectedItem=Convert.ToInt32(line.Remove(0,"iSelectedItem=".Length));
       else
        if (line.StartsWith("iParity="))
        gpsSource.iParity=Convert.ToInt32(line.Remove(0,"iParity=".Length));
       else
        if (line.StartsWith("iStopBits="))
        gpsSource.iStopBits=Convert.ToInt32(line.Remove(0,"iStopBits=".Length));
       else
        if (line.StartsWith("iFlowControl="))
        gpsSource.iFlowControl=Convert.ToInt32(line.Remove(0,"iFlowControl=".Length));
       else
        if (line.StartsWith("iUDPPort="))
        gpsSource.iUDPPort=Convert.ToInt32(line.Remove(0,"iUDPPort=".Length));
       else
        if (line.StartsWith("iTCPPort="))
        gpsSource.iTCPPort=Convert.ToInt32(line.Remove(0,"iTCPPort=".Length));
       else
        if (line.StartsWith("sTCPAddress="))
        gpsSource.sTCPAddress=line.Remove(0,"sTCPAddress=".Length);
       else
        if (line.StartsWith("sFileName="))
        gpsSource.sFileName=line.Remove(0,"sFileName=".Length);
       else
        if (line.StartsWith("sFileNameSession="))
        gpsSource.sFileNameSession=line.Remove(0,"sFileNameSession=".Length);
       else
        if (line.StartsWith("bNoDelay="))
        gpsSource.bNoDelay=Convert.ToBoolean(line.Remove(0,"bNoDelay=".Length));
       else
        if (line.StartsWith("bTrackAtOnce="))
        gpsSource.bTrackAtOnce=Convert.ToBoolean(line.Remove(0,"bTrackAtOnce=".Length));
       else
        if (line.StartsWith("iPlaySpeed="))
        gpsSource.iPlaySpeed=Convert.ToInt32(line.Remove(0,"iPlaySpeed=".Length));
       else
        if (line.StartsWith("iFilePlaySpeed="))
        gpsSource.iFilePlaySpeed=Convert.ToInt32(line.Remove(0,"iFilePlaySpeed=".Length));
       else
        if (line.StartsWith("sCallSign="))
        gpsSource.sCallSign=line.Remove(0,"sCallSign=".Length);
       else
        if (line.StartsWith("iRefreshRate="))
        gpsSource.iRefreshRate=Convert.ToInt32(line.Remove(0,"iRefreshRate=".Length));
       else
        if (line.StartsWith("iReload="))
        gpsSource.iReload=Convert.ToInt32(line.Remove(0,"iReload=".Length));
       else
        if (line.StartsWith("bForcePreprocessing="))
        gpsSource.bForcePreprocessing=Convert.ToBoolean(line.Remove(0,"bForcePreprocessing=".Length));
       else
        if (line.StartsWith("bSession="))
        gpsSource.bSession=Convert.ToBoolean(line.Remove(0,"bSession=".Length));
       else
        if (line.StartsWith("sCallSignFilter="))
       {
        CSVReader csvReader=new CSVReader();
        string [] sLines=csvReader.GetCSVLine(line.Remove(0,"sCallSignFilter=".Length));
        if (sLines!=null && sLines.Length>0)
         gpsSource.sCallSignFilterLines=(string[])sLines.Clone();
       }
       else
        if (line.StartsWith("sAPRSServerURL="))
        gpsSource.sAPRSServerURL=line.Remove(0,"sAPRSServerURL=".Length);
      }
     }
    }
    else
    {
     SetDefaultSettings(bSet);
     m_bHandleControlValueChangeEvent=false;
    }
   }
    if (srReader==null)
     sr.Close();
   }
   catch(Exception)
   {
    SetDefaultSettings(bSet);
    m_bHandleControlValueChangeEvent=false;
    if (m_gpsSourceList.Count>=1 && bSet)
     StartStop.Enabled=true;
    bRet=false;
   }
   if (bSet)
   {
    int i;
    for (i=0; i<m_gpsSourceList.Count; i++)
    {
     GPSSource gpsSource = (GPSSource)m_gpsSourceList[i];
     TreeNode treeNode;
     TreeNode treeNodeParent;
     treeNode = new TreeNode(gpsSource.sDescription);
     gpsSource.treeNode=treeNode;
     treeNode.Tag=gpsSource;
     switch (gpsSource.sType)
     {
      case "COM":
       treeNodeParent=m_treeNodeCOM;
       break;
      case "UDP":
       treeNodeParent=m_treeNodeUDP;
       break;
      case "TCP":
       treeNodeParent=m_treeNodeTCP;
       break;
      case "File":
       treeNodeParent=m_treeNodeFile;
       break;
      case "APRS Internet":
       treeNodeParent=m_treeNodeAPRS;
       break;
      case "POI":
       treeNodeParent=m_treeNodePOI;
       break;
      default:
       treeNodeParent=m_treeNodeCOM;
       break;
     }
     treeNodeParent.Nodes.Add(treeNode);
     if (gpsSource.sIconPath=="")
                        gpsSource.sIconPath = GpsTrackerPlugin.m_sPluginDirectory + "\\Gpsnotset.png";
     if(!File.Exists(gpsSource.sIconPath))
                        gpsSource.sIconPath = GpsTrackerPlugin.m_sPluginDirectory + "\\Gpsx.png";
     image = new Bitmap(gpsSource.sIconPath);
     imageListGpsIcons.Images.Add(image);
     treeNode.ImageIndex=imageListGpsIcons.Images.Count-1;
     treeNode.SelectedImageIndex=treeNode.ImageIndex;
     if (gpsSource.bTrack)
     {
      treeNode.NodeFont = new System.Drawing.Font(treeViewSources.Font, FontStyle.Bold);
      treeNode.Text=treeNode.Text+"     ";
     }
     treeNodeParent.ExpandAll();
    }
    checkBoxTrackHeading.Checked=m_bTrackHeading;
    checkBoxTrackLine.Checked=m_bTrackLine;
    checkBoxRecordSession.Checked=m_bRecordSession;
    checkBoxInformationText.Checked=m_bInfoText;
    for (i=0; i<comboBoxFile.Items.Count; i++)
                    if ((String)comboBoxFile.Items[i] == GpsTrackerPlugin.m_sPluginDirectory + "\\SampleSession.GPSTrackerSession")
      break;
    if (i==comboBoxFile.Items.Count)
                    comboBoxFile.Items.Add(GpsTrackerPlugin.m_sPluginDirectory + "\\SampleSession.GPSTrackerSession");
   }
   if (m_gpsSourceList.Count>=1 && bSet)
    StartStop.Enabled=true;
   m_bHandleControlValueChangeEvent=true;
   return bRet;
  }
  void SetDefaultSettings(bool bSet)
  {
   m_bHandleControlValueChangeEvent=false;
   if (bSet)
   {
    comboBoxCOMPort.Text="1";
    comboBoxBaudRate.Text="4800";
    comboBoxByteSize.Text="8";
    comboParity.SelectedIndex=0;
    comboBoxStopBits.SelectedIndex=0;
    trackBarFileSpeed.Value=1;
    numericUpDownUDPPort.Value=5555;
    numericUpDownTCPPort.Value=4444;
    comboBoxTcpIP.Text="";
    checkBoxSecureSocket.Checked=false;
                comboBoxFile.Text = GpsTrackerPlugin.m_sPluginDirectory + "\\SampleSession.GPSTrackerSession";
    if (comboBoxAPRSInternetServer.Items.Count==0)
    {
     comboBoxAPRSInternetServer.Items.Add("http://db.aprsworld.net/datamart/csv.php?call=");
     comboBoxAPRSInternetServer.Items.Add("http://www.findu.com/cgi-bin/rawposit.cgi?call=");
    }
    comboBoxAPRSInternetServer.Text="http://db.aprsworld.net/datamart/csv.php?call=";
    m_bTrackHeading=false;
    m_bTrackLine=false;
    m_bRecordSession=false;
    m_bInfoText=true;
    StartStop.Enabled=false;
    checkBoxNoDelay.Checked=false;
    comboBoxFlowControl.SelectedIndex=0;
    buttonTrackColorCOM.BackColor=Color.Blue;
    buttonTrackColorTCP.BackColor=Color.Yellow;
    buttonTrackColorUDP.BackColor=Color.LightGreen;
    buttonTrackColor.BackColor=Color.Red;
    checkBoxTrackHeading.Checked=m_bTrackHeading;
    checkBoxTrackLine.Checked=m_bTrackLine;
    checkBoxRecordSession.Checked=m_bRecordSession;
    checkBoxInformationText.Checked=m_bInfoText;
    textBoxCallSignFilter.Text="";
    textBoxAPRSISCallSign.Text="";
    numericUpDownAPRSIS.Value=1;
    buttonTrackColorAPRS.BackColor=Color.WhiteSmoke;
   }
   m_bHandleControlValueChangeEvent=true;
  }
  private void buttonBrowseGpsFile_Click(object sender, System.EventArgs e)
  {
   OpenFileDialog dlgOpenFile = new OpenFileDialog();
   dlgOpenFile.Title = "Select Gps File" ;
   dlgOpenFile.Filter = "All files (*.*)|*.*|Text (*.txt)|*.txt|GPX (*.gpx)|*.gpx|NMEA Text (*.NMEAText)|*.NMEAText|Session (*.GPSTrackerSession)|*.GPSTrackerSession|Track At Once (*.TrackAtOnce)|*.TrackAtOnce" ;
   dlgOpenFile.FilterIndex = 1 ;
   dlgOpenFile.RestoreDirectory = true ;
   if(dlgOpenFile.ShowDialog() == DialogResult.OK)
   {
    comboBoxFile.Text=dlgOpenFile.FileName;
    int i;
    for (i=0; i<comboBoxFile.Items.Count; i++)
     if ((String)comboBoxFile.Items[i] == dlgOpenFile.FileName)
      break;
    if (i==comboBoxFile.Items.Count)
     comboBoxFile.Items.Add(dlgOpenFile.FileName);
   }
  }
  private void buttonCheckForUpdates_Click(object sender, System.EventArgs e)
  {
   WebUpdate();
  }
  private void tabControlGPS_SelectedIndexChanged(object sender, System.EventArgs e)
  {
   if (tabControlGPS.SelectedTab.Name=="tabPageUpdate")
   {
    buttonCheckForUpdates.Text="Check for Updates";
    labelTrackCode.Text="";
    textBoxVersionInfo.Text="Your Version: " + m_gpsTrackerPlugin.m_sVersion + "\r\n";
   }
  }
  private void checkBoxTrackHeading_CheckedChanged(object sender, System.EventArgs e)
  {
   m_bTrackHeading=checkBoxTrackHeading.Checked;
  }
  private void checkBoxInformationText_CheckedChanged(object sender, System.EventArgs e)
  {
   m_bInfoText=checkBoxInformationText.Checked;
  }
  private void checkBoxTrackLine_CheckedChanged(object sender, System.EventArgs e)
  {
   m_bTrackLine=checkBoxTrackLine.Checked;
  }
  private void checkBoxRecordSession_CheckedChanged(object sender, System.EventArgs e)
  {
   m_bRecordSession=checkBoxRecordSession.Checked;
  }
  private void buttonTrackColor_Click(object sender, System.EventArgs e)
  {
   colorPicker.SolidColorOnly=true;
   colorPicker.Color=buttonTrackColor.BackColor;
   if (colorPicker.ShowDialog()== DialogResult.OK)
   {
    buttonTrackColor.BackColor=colorPicker.Color;
   }
  }
  private void buttonTrackColorAPRS_Click(object sender, System.EventArgs e)
  {
   colorPicker.SolidColorOnly=true;
   colorPicker.Color=buttonTrackColor.BackColor;
   if (colorPicker.ShowDialog()== DialogResult.OK)
   {
    buttonTrackColorAPRS.BackColor=colorPicker.Color;
   }
  }
  private void button3_Click(object sender, System.EventArgs e)
  {
   colorPicker.SolidColorOnly=true;
   colorPicker.Color=buttonTrackColorCOM.BackColor;
   if (colorPicker.ShowDialog()== DialogResult.OK)
   {
    buttonTrackColorCOM.BackColor=colorPicker.Color;
   }
  }
  private void buttonTrackColorUDP_Click(object sender, System.EventArgs e)
  {
   colorPicker.SolidColorOnly=true;
   colorPicker.Color=buttonTrackColorUDP.BackColor;
   if (colorPicker.ShowDialog()== DialogResult.OK)
   {
    buttonTrackColorUDP.BackColor=colorPicker.Color;
   }
  }
  private void buttonTrackColorTCP_Click(object sender, System.EventArgs e)
  {
   colorPicker.SolidColorOnly=true;
   colorPicker.Color=buttonTrackColorTCP.BackColor;
   if (colorPicker.ShowDialog()== DialogResult.OK)
   {
    buttonTrackColorTCP.BackColor=colorPicker.Color;
   }
  }
  private void menuItemAdd_Click(object sender, System.EventArgs e)
  {
   GPSSource gpsSource = new GPSSource();
   TreeNode treeNode;
   labelTrackCode.Text="";
   if (m_gpsSourceList.Count==m_iMaxDevices)
   {
    labelTrackCode.ForeColor = System.Drawing.Color.Red;
    labelTrackCode.Text="The max. number of GPS devices is " + m_iMaxDevices.ToString();
   }
   else
   {
    gpsSource.sType=treeViewSources.SelectedNode.Text;
    gpsSource.iNameIndex=GetAvailableIndex();
    gpsSource.sDescription=gpsSource.sType + " #" + gpsSource.iNameIndex;
    gpsSource.bTrack=false;
    gpsSource.bSetup=false;
    treeNode = new TreeNode(gpsSource.sDescription);
    treeNode.ImageIndex=1;
    treeNode.SelectedImageIndex=1;
    treeNode.Tag=gpsSource;
    gpsSource.treeNode=treeNode;
    gpsSource.bNeedApply=true;
    m_gpsSourceList.Add(gpsSource);
    ApplySettings(gpsSource,false,false,false);
    treeViewSources.SelectedNode.Nodes.Add(treeNode);
    treeViewSources.SelectedNode.ExpandAll();
    treeViewSources.SelectedNode=treeNode;
    SetupTabs();
    m_iSourceNameCount++;
    StartStop.Enabled=true;
   }
  }
  private bool ApplySettings(GPSSource gpsS, bool bNoSet, bool bCheck, bool bAddedPOI)
  {
   gpsS.bSetup=true;
   if (bNoSet && bCheck)
   {
    labelTrackCode.Text="";
    int i;
    for (i=0; i<m_gpsSourceList.Count; i++)
    {
     GPSSource gpsSource=(GPSSource)m_gpsSourceList[i];
     GPSSource gpsSourceSelected=(GPSSource)treeViewSources.SelectedNode.Tag;
     if ( tabControlGPS.SelectedTab.Name == "tabPageCOM" && gpsSourceSelected!=gpsSource && gpsSource.bSetup==true &&
      (gpsSource.sType=="COM" && gpsSource.iCOMPort==Convert.ToInt32(comboBoxCOMPort.Text)))
     {
      labelTrackCode.ForeColor = System.Drawing.Color.Red;
      labelTrackCode.Text="COM Port " + comboBoxCOMPort.Text + " is already in use.";
      gpsS.bSetup=false;
      break;
     }
     if ( tabControlGPS.SelectedTab.Name == "tabPageUDP" && gpsSourceSelected!=gpsSource && gpsSource.bSetup==true &&
      ( (gpsSource.sType=="UDP" && gpsSource.iUDPPort==(int)numericUpDownUDPPort.Value) ||
      (gpsSource.sType=="TCP" && gpsSource.iTCPPort==(int)numericUpDownUDPPort.Value) ) )
     {
      labelTrackCode.ForeColor = System.Drawing.Color.Red;
      labelTrackCode.Text="Port " + numericUpDownUDPPort.Value.ToString().Trim() + " is already in use.";
      gpsS.bSetup=false;
      break;
     }
     if ( tabControlGPS.SelectedTab.Name == "tabPageTCP" && gpsSourceSelected!=gpsSource && gpsSource.bSetup==true &&
      ( (gpsSource.sType=="UDP" && gpsSource.iUDPPort==(int)numericUpDownTCPPort.Value) ||
      (gpsSource.sType=="TCP" && gpsSource.iTCPPort==(int)numericUpDownTCPPort.Value) ) )
     {
      labelTrackCode.ForeColor = System.Drawing.Color.Red;
      labelTrackCode.Text="Port " + numericUpDownTCPPort.Value.ToString().Trim() + " is already in use.";
      gpsS.bSetup=false;
      break;
     }
    }
    try
   {
    float fLat;
    float fLon;
    if (tabControlGPS.SelectedTab.Name == "tabPagePOI")
    {
     fLat=Convert.ToSingle(textBoxLatitud.Text);
     fLon=Convert.ToSingle(textBoxLongitud.Text);
     if (fLat>=(float)-90 && fLat<=(float)90 &&
      fLon>=(float)-180 && fLon<=(float)180)
      gpsS.bSetup=true;
     else
     {
      gpsS.bSetup=false;
      labelTrackCode.ForeColor = System.Drawing.Color.Red;
      labelTrackCode.Text="Please enter a valid Latitud and Longitud.";
     }
    }
   }
    catch (Exception)
    {
     gpsS.bSetup=false;
     labelTrackCode.ForeColor = System.Drawing.Color.Red;
     labelTrackCode.Text="Please enter a valid Latitud and Longitud.";
    }
   }
   if (gpsS.bSetup==true)
   {
    gpsS.sComment="";
    switch(gpsS.sType)
    {
     case "COM":
      gpsS.iCOMPort=Convert.ToInt32(comboBoxCOMPort.Text);
      gpsS.iBaudRate=Convert.ToInt32(comboBoxBaudRate.Text);
      gpsS.iByteSize=Convert.ToInt32(comboBoxByteSize.Text);
      gpsS.iParity=comboParity.SelectedIndex;
      gpsS.iStopBits=comboBoxStopBits.SelectedIndex;
      gpsS.iFlowControl=comboBoxFlowControl.SelectedIndex;
      gpsS.colorTrack=buttonTrackColorCOM.BackColor;
      gpsS.sCallSignFilter = textBoxCallSignFilter.Text;
      gpsS.sCallSignFilterLines = (string[])textBoxCallSignFilter.Lines.Clone();
      gpsS.bSetup=true;
      break;
     case "UDP":
      gpsS.iUDPPort=(int)numericUpDownUDPPort.Value;
      gpsS.colorTrack=buttonTrackColorUDP.BackColor;
      gpsS.sCallSignFilter = textBoxCallSignFilter.Text;
      gpsS.sCallSignFilterLines = (string[])textBoxCallSignFilter.Lines.Clone();
      gpsS.bSetup=true;
      break;
     case "TCP":
      gpsS.iTCPPort=(int)numericUpDownTCPPort.Value;
      gpsS.sTCPAddress=comboBoxTcpIP.Text.Trim();
      gpsS.colorTrack=buttonTrackColorTCP.BackColor;
      gpsS.sCallSignFilter = textBoxCallSignFilter.Text;
      gpsS.sCallSignFilterLines = (string[])textBoxCallSignFilter.Lines.Clone();
      gpsS.bSecureSocket=checkBoxSecureSocket.Checked;
      if (bNoSet==true && gpsS.sTCPAddress!="")
      {
       int i;
       for (i=0; i<comboBoxTcpIP.Items.Count; i++)
        if ((String)comboBoxTcpIP.Items[i] == gpsS.sTCPAddress)
         break;
       if (i==comboBoxTcpIP.Items.Count)
       {
        if (comboBoxTcpIP.Items.Count==10)
        {
         for (i=1; i<=9; i++)
          comboBoxTcpIP.Items[i-1]=comboBoxTcpIP.Items[i];
         comboBoxTcpIP.Items.RemoveAt(9);
        }
        comboBoxTcpIP.Items.Add(gpsS.sTCPAddress);
       }
      }
      if (gpsS.sTCPAddress=="")
       gpsS.bSetup=false;
      break;
     case "File":
      gpsS.iReload=Convert.ToInt32(numericUpDownReload.Value);
      gpsS.bSession=false;
      string sFileName=comboBoxFile.Text.Trim();
      try
     {
      if(File.Exists(sFileName) && bCheck)
      {
       StreamReader srReader = File.OpenText(comboBoxFile.Text.Trim());
       if (LoadSettings(srReader,false))
        gpsS.bSession=true;
       srReader.Close();
      }
     }
      catch(Exception)
      {
       gpsS.bSetup=false;
      }
      gpsS.bSetup=true;
      if (!gpsS.bSetup)
      {
       labelTrackCode.ForeColor = System.Drawing.Color.Red;
       labelTrackCode.Text="Unable to open the selected file.";
      }
      else
      {
       string sDescription=gpsS.sDescription;
       if (gpsS.bSession && sDescription.StartsWith("File #"))
        sDescription="Session #" + gpsS.sDescription.Substring(6);
       else
       if (checkBoxTrackAtOnce.Checked==true && gpsS.bSession==false && sDescription.StartsWith("File #"))
        sDescription="Track #" + gpsS.sDescription.Substring(6);
       gpsS.treeNode.Text=sDescription;
       gpsS.sDescription=sDescription;
       gpsS.sFileName=comboBoxFile.Text.Trim();
       gpsS.bNoDelay=checkBoxNoDelay.Checked;
       gpsS.bTrackAtOnce=checkBoxTrackAtOnce.Checked;
       if (gpsS.bNoDelay)
        gpsS.iPlaySpeed=0;
       else
        gpsS.iPlaySpeed=trackBarFileSpeed.Value;
       gpsS.colorTrack=buttonTrackColor.BackColor;
       gpsS.sCallSignFilter = textBoxCallSignFilter.Text;
       gpsS.sCallSignFilterLines = (string[])textBoxCallSignFilter.Lines.Clone();
       gpsS.bForcePreprocessing=checkBoxForcePreprocessing.Checked;
       if (bNoSet==true && comboBoxFile.Text!="")
       {
        int i;
        for (i=0; i<comboBoxFile.Items.Count; i++)
         if ((String)comboBoxFile.Items[i] == comboBoxFile.Text)
          break;
        if (i==comboBoxFile.Items.Count)
        {
         if (comboBoxFile.Items.Count==10)
         {
          for (i=2; i<=9; i++)
           comboBoxFile.Items[i-1]=comboBoxFile.Items[i];
          comboBoxFile.Items.RemoveAt(9);
         }
         comboBoxFile.Items.Add(comboBoxFile.Text);
        }
       }
      }
      break;
     case "APRS Internet":
      gpsS.sAPRSServerURL=comboBoxAPRSInternetServer.Text;
      gpsS.sCallSign=textBoxAPRSISCallSign.Text.Trim();
      gpsS.iRefreshRate=(int)numericUpDownAPRSIS.Value;
      gpsS.colorTrack=buttonTrackColorAPRS.BackColor;
      gpsS.sCallSignFilter = textBoxCallSignFilter.Text;
      gpsS.sCallSignFilterLines = (string[])textBoxCallSignFilter.Lines.Clone();
      if (bNoSet==true && gpsS.sAPRSServerURL!="")
      {
       int i;
       for (i=0; i<comboBoxAPRSInternetServer.Items.Count; i++)
        if ((String)comboBoxAPRSInternetServer.Items[i] == gpsS.sAPRSServerURL)
         break;
       if (i==comboBoxAPRSInternetServer.Items.Count)
       {
        if (comboBoxAPRSInternetServer.Items.Count==10)
        {
         for (i=3; i<=9; i++)
          comboBoxAPRSInternetServer.Items[i-1]=comboBoxAPRSInternetServer.Items[i];
         comboBoxAPRSInternetServer.Items.RemoveAt(9);
        }
        comboBoxAPRSInternetServer.Items.Add(gpsS.sAPRSServerURL);
       }
      }
      if (gpsS.sAPRSServerURL=="")
       gpsS.bSetup=false;
      break;
     case "POI":
      if (bNoSet==false)
      {
       gpsS.fLat=(float)0;
       gpsS.fLon=(float)0;
      }
      else
      {
       if (!bAddedPOI)
       {
        gpsS.fLat=Convert.ToSingle(textBoxLatitud.Text);
        gpsS.fLon=Convert.ToSingle(textBoxLongitud.Text);
       }
      }
      gpsS.bPOISet=false;
      break;
    }
    if (bNoSet==false)
     gpsS.bSetup=false;
    if (gpsS.bSetup)
    {
     string sIconName;
     int iIconLimit;
     if (gpsS.sType=="POI")
     {
      sIconName="poi";
      iIconLimit=10;
     }
     else
     {
      sIconName="gps";
      iIconLimit=20;
     }
     if (gpsS.iNameIndex<=iIconLimit)
     {
                        gpsS.sIconPath = GpsTrackerPlugin.m_sPluginDirectory + "\\" + sIconName + Convert.ToString(gpsS.iNameIndex) + ".png";
      Bitmap image = new Bitmap(gpsS.sIconPath);
      imageListGpsIcons.Images.Add(image);
      gpsS.treeNode.ImageIndex=imageListGpsIcons.Images.Count-1;
      gpsS.treeNode.SelectedImageIndex=gpsS.treeNode.ImageIndex;
     }
     else
     {
                        gpsS.sIconPath = GpsTrackerPlugin.m_sPluginDirectory + "\\" + sIconName + "x.png";
      Bitmap image = new Bitmap(gpsS.sIconPath);
      imageListGpsIcons.Images.Add(image);
      gpsS.treeNode.ImageIndex=imageListGpsIcons.Images.Count-1;
      gpsS.treeNode.SelectedImageIndex=gpsS.treeNode.ImageIndex;
     }
    }
   }
   SaveSettings(null);
   return gpsS.bSetup;
  }
  private int GetAvailableIndex()
  {
   int iFreeIndex=0;
   if (m_gpsSourceList.Count>0)
   {
    for (int i=0; i<m_iMaxDevices; i++)
    {
     int ii;
     for (ii=0; ii<m_gpsSourceList.Count; ii++)
     {
      GPSSource gpsS = (GPSSource)m_gpsSourceList[ii];
      if (gpsS.iNameIndex==i)
       break;
     }
     if (ii==m_gpsSourceList.Count)
     {
      iFreeIndex=i;
      break;
     }
    }
   }
   return iFreeIndex;
  }
  private void treeViewSources_MouseDown(object sender, System.Windows.Forms.MouseEventArgs e)
  {
   if (treeViewSources.GetNodeAt(e.X,e.Y)!=null)
   {
    treeViewSources.SelectedNode = treeViewSources.GetNodeAt(e.X,e.Y);
    SetupTabs();
   }
  }
  private void treeViewSources_MouseUp(object sender, System.Windows.Forms.MouseEventArgs e)
  {
   if(e.Button == MouseButtons.Right)
   {
    switch (treeViewSources.SelectedNode.Tag.ToString())
    {
     case "COM":
     case "TCP":
     case "UDP":
     case "File":
     case "APRS Internet":
     case "POI":
      menuItemAdd.Visible=true;
      menuItemDelete.Visible=true;
      menuItemRename.Visible=false;
      menuItemSetIcon.Visible=false;
      menuItemSetTrack.Visible=false;
      menuItemDelete.Text="Delete all " + treeViewSources.SelectedNode.Tag.ToString() + " sources";
      break;
     case "SOURCES":
      menuItemSetTrack.Visible=false;
      menuItemAdd.Visible=false;
      menuItemRename.Visible=false;
      menuItemSetIcon.Visible=false;
      menuItemDelete.Visible=true;
      menuItemDelete.Text="Delete all Sources";
      break;
     default:
      menuItemSetTrack.Visible=true;
      menuItemAdd.Visible=false;
      menuItemRename.Visible=true;
      menuItemSetIcon.Visible=true;
      menuItemDelete.Visible=true;
      menuItemDelete.Text="Delete " + treeViewSources.SelectedNode.Text;
      break;
    }
    contextMenuSourceTree.Show(treeViewSources,new Point(e.X,e.Y));
   }
  }
  private void SetupTree()
  {
   treeViewSources.Nodes.Clear();
   m_gpsSourceList.Clear();
   m_iSourceNameCount=0;
   StartStop.Enabled=false;
   TreeNode nodeSources=new TreeNode("Sources");
   nodeSources.Tag="SOURCES";
   treeViewSources.Nodes.Add(nodeSources);
   m_treeNodeCOM=new TreeNode("COM");
   m_treeNodeCOM.Tag="COM";
   nodeSources.Nodes.Add(m_treeNodeCOM);
   m_treeNodeUDP=new TreeNode("UDP");
   m_treeNodeUDP.Tag="UDP";
   nodeSources.Nodes.Add(m_treeNodeUDP);
   m_treeNodeTCP=new TreeNode("TCP");
   m_treeNodeTCP.Tag="TCP";
   nodeSources.Nodes.Add(m_treeNodeTCP);
   m_treeNodeFile=new TreeNode("File");
   m_treeNodeFile.Tag="File";
   nodeSources.Nodes.Add(m_treeNodeFile);
   m_treeNodeAPRS=new TreeNode("APRS Internet");
   m_treeNodeAPRS.Tag="APRS Internet";
   nodeSources.Nodes.Add(m_treeNodeAPRS);
   m_treeNodePOI=new TreeNode("POI");
   m_treeNodePOI.Tag="POI";
   nodeSources.Nodes.Add(m_treeNodePOI);
   treeViewSources.SelectedNode=nodeSources;
   SetupTabs();
   treeViewSources.ExpandAll();
  }
  private void SetupTabs()
  {
   m_bHandleControlValueChangeEvent=false;
   if (treeViewSources.SelectedNode!=null)
   {
    switch (treeViewSources.SelectedNode.Tag.ToString())
    {
     case "COM":
     case "TCP":
     case "UDP":
     case "File":
     case "APRS Internet":
     case "POI":
     case "SOURCES":
      tabControlGPS.TabPages.Clear();
      tabControlGPS.TabPages.Add(tabPageUsage);
      tabControlGPS.TabPages.Add(tabPageGeneral);
      tabControlGPS.TabPages.Add(tabPageUpdate);
      break;
     default:
      GPSSource gpsSource = (GPSSource)treeViewSources.SelectedNode.Tag;
      tabControlGPS.TabPages.Clear();
     switch(gpsSource.sType)
     {
      case "COM":
       tabControlGPS.TabPages.Add(tabPageCOM);
       tabControlGPS.TabPages.Add(tabPageAPRS);
       tabControlGPS.TabPages.Add(tabPageGeneral);
       tabControlGPS.TabPages.Add(tabPageCOMHelp);
       if (gpsSource.bSetup)
       {
        comboBoxCOMPort.Text=Convert.ToString(gpsSource.iCOMPort);
        comboBoxBaudRate.Text=Convert.ToString(gpsSource.iBaudRate);
        comboBoxByteSize.Text=Convert.ToString(gpsSource.iByteSize);
        comboParity.SelectedIndex=gpsSource.iParity;
        comboBoxStopBits.SelectedIndex=gpsSource.iStopBits;
        comboBoxFlowControl.SelectedIndex=gpsSource.iFlowControl;
        buttonTrackColorCOM.BackColor=gpsSource.colorTrack;
        if (gpsSource.sCallSignFilterLines!=null && gpsSource.sCallSignFilterLines.Length>0)
         textBoxCallSignFilter.Lines=(string[])gpsSource.sCallSignFilterLines.Clone();
        else
         textBoxCallSignFilter.Text="";
       }
       buttonApply.Enabled=gpsSource.bNeedApply;
       buttonApplyAPRSFilter.Enabled=gpsSource.bNeedApply;
       break;
      case "UDP":
       tabControlGPS.TabPages.Add(tabPageUDP);
       tabControlGPS.TabPages.Add(tabPageAPRS);
       tabControlGPS.TabPages.Add(tabPageGeneral);
       tabControlGPS.TabPages.Add(tabPageUDPHelp);
       if (gpsSource.bSetup)
       {
        numericUpDownUDPPort.Value=gpsSource.iUDPPort;
        buttonTrackColorUDP.BackColor=gpsSource.colorTrack;
        if (gpsSource.sCallSignFilterLines!=null && gpsSource.sCallSignFilterLines.Length>0)
         textBoxCallSignFilter.Lines=(string[])gpsSource.sCallSignFilterLines.Clone();
        else
         textBoxCallSignFilter.Text="";
       }
       buttonApplyUDP.Enabled=gpsSource.bNeedApply;
       buttonApplyAPRSFilter.Enabled=gpsSource.bNeedApply;
       break;
      case "TCP":
       tabControlGPS.TabPages.Add(tabPageTCP);
       tabControlGPS.TabPages.Add(tabPageAPRS);
       tabControlGPS.TabPages.Add(tabPageGeneral);
       tabControlGPS.TabPages.Add(tabPageTCPHelp);
       if (gpsSource.bSetup)
       {
        numericUpDownTCPPort.Value=gpsSource.iTCPPort;
        comboBoxTcpIP.Text=gpsSource.sTCPAddress;
        buttonTrackColorTCP.BackColor=gpsSource.colorTrack;
        if (gpsSource.sCallSignFilterLines!=null && gpsSource.sCallSignFilterLines.Length>0)
         textBoxCallSignFilter.Lines=(string[])gpsSource.sCallSignFilterLines.Clone();
        else
         textBoxCallSignFilter.Text="";
       }
       buttonApplyTCP.Enabled=gpsSource.bNeedApply;
       buttonApplyAPRSFilter.Enabled=gpsSource.bNeedApply;
       break;
      case "File":
       tabControlGPS.TabPages.Add(tabPageFile);
       tabControlGPS.TabPages.Add(tabPageAPRS);
       tabControlGPS.TabPages.Add(tabPageGeneral);
       tabControlGPS.TabPages.Add(tabPageFileHelp);
       if (gpsSource.bSetup)
       {
        comboBoxFile.Text=gpsSource.sFileName;
        if (gpsSource.iFilePlaySpeed>0)
         trackBarFileSpeed.Value=gpsSource.iFilePlaySpeed;
        checkBoxNoDelay.Checked=gpsSource.bNoDelay;
        checkBoxTrackAtOnce.Checked=gpsSource.bTrackAtOnce;
        checkBoxNoDelay.Checked=gpsSource.bNoDelay;
        buttonTrackColor.BackColor=gpsSource.colorTrack;
        checkBoxForcePreprocessing.Checked=gpsSource.bForcePreprocessing;
        numericUpDownReload.Value=gpsSource.iReload;
        if (gpsSource.sCallSignFilterLines!=null && gpsSource.sCallSignFilterLines.Length>0)
         textBoxCallSignFilter.Lines=(string[])gpsSource.sCallSignFilterLines.Clone();
        else
         textBoxCallSignFilter.Text="";
       }
       buttonApplyFile.Enabled=gpsSource.bNeedApply;
       buttonApplyAPRSFilter.Enabled=gpsSource.bNeedApply;
       break;
      case "APRS Internet":
       tabControlGPS.TabPages.Add(tabPageAPRSInternet);
       tabControlGPS.TabPages.Add(tabPageGeneral);
       tabControlGPS.TabPages.Add(tabPageAPRSInternetHelp);
       if (gpsSource.bSetup)
       {
        comboBoxAPRSInternetServer.Text=gpsSource.sAPRSServerURL;
        textBoxAPRSISCallSign.Text=gpsSource.sCallSign;
        buttonTrackColorAPRS.BackColor=gpsSource.colorTrack;
        numericUpDownAPRSIS.Value=gpsSource.iRefreshRate;
       }
       buttonApplyAPRSInternet.Enabled=gpsSource.bNeedApply;
       buttonApplyAPRSFilter.Enabled=gpsSource.bNeedApply;
       break;
      case "POI":
       tabControlGPS.TabPages.Add(tabPagePOI);
       tabControlGPS.TabPages.Add(tabPageGeneral);
       tabControlGPS.TabPages.Add(tabPagePOIHelp);
       textBoxLatitud.Text=Convert.ToString(gpsSource.fLat);
       textBoxLongitud.Text=Convert.ToString(gpsSource.fLon);
       buttonApplyPOI.Enabled=gpsSource.bNeedApply;
       buttonApplyAPRSFilter.Enabled=gpsSource.bNeedApply;
       break;
     }
      break;
    }
    this.Text="GPSTracker :: " + treeViewSources.SelectedNode.Text;
   }
   m_bHandleControlValueChangeEvent=true;
  }
  private void menuItemDelete_Click(object sender, System.EventArgs e)
  {
   GPSSource gpsSource;
   switch (treeViewSources.SelectedNode.Tag.ToString())
   {
    case "COM":
    case "TCP":
    case "UDP":
    case "File":
    case "APRS Internet":
    case "POI":
     for (int i=0; i<m_gpsSourceList.Count; i++)
     {
      gpsSource = (GPSSource)m_gpsSourceList[i];
      if (gpsSource.sType==treeViewSources.SelectedNode.Tag.ToString())
      {
       treeViewSources.Nodes.Remove(gpsSource.treeNode);
       m_gpsSourceList.RemoveAt(i);
       i=-1;
      }
     }
     break;
    case "SOURCES":
     SetupTree();
     break;
    default:
     gpsSource = (GPSSource)treeViewSources.SelectedNode.Tag;
     treeViewSources.Nodes.Remove(gpsSource.treeNode);
     m_gpsSourceList.Remove(gpsSource);
     break;
   }
   if (m_gpsSourceList.Count==0)
    StartStop.Enabled=false;
   SetupTabs();
   SaveSettings(null);
  }
  private void menuItemSetTrack_Click(object sender, System.EventArgs e)
  {
   GPSSource gpsSource = (GPSSource)treeViewSources.SelectedNode.Tag;
   if (gpsSource.bTrack)
   {
    gpsSource.bTrack=false;
    treeViewSources.SelectedNode.NodeFont = new System.Drawing.Font(treeViewSources.Font, FontStyle.Regular);
   }
   else
   {
    for (int i=0; i<m_gpsSourceList.Count; i++)
    {
     gpsSource = (GPSSource)m_gpsSourceList[i];
     if (gpsSource.bTrack)
     {
      gpsSource.bTrack=false;
      gpsSource.treeNode.NodeFont = new System.Drawing.Font(treeViewSources.Font, FontStyle.Regular);
      break;
     }
    }
    gpsSource = (GPSSource)treeViewSources.SelectedNode.Tag;
    gpsSource.bTrack=true;
    treeViewSources.SelectedNode.Text+="     ";
    treeViewSources.SelectedNode.NodeFont = new System.Drawing.Font(treeViewSources.Font, FontStyle.Bold);
   }
  }
  private void menuItemSetIcon_Click(object sender, System.EventArgs e)
  {
   try
  {
   OpenFileDialog dlgOpenFile = new OpenFileDialog();
   dlgOpenFile.Title = "Select Gps Device Icon" ;
   dlgOpenFile.Filter = "PNG (*.png)|*.png|JPEG (*.jpg)|*.jpg" ;
   dlgOpenFile.FilterIndex = 0 ;
   dlgOpenFile.RestoreDirectory = true ;
   if(dlgOpenFile.ShowDialog() == DialogResult.OK)
   {
    Bitmap image = new Bitmap(dlgOpenFile.FileName);
    imageListGpsIcons.Images.Add(image);
    treeViewSources.SelectedNode.ImageIndex=imageListGpsIcons.Images.Count-1;
    treeViewSources.SelectedNode.SelectedImageIndex=treeViewSources.SelectedNode.ImageIndex;
    GPSSource gpsSource = (GPSSource)treeViewSources.SelectedNode.Tag;
    gpsSource.sIconPath=dlgOpenFile.FileName;
    SaveSettings(null);
   }
  }
   catch(Exception)
   {
   }
  }
  private void buttonApply_Click(object sender, System.EventArgs e)
  {
   GPSSource gpsSource=(GPSSource)treeViewSources.SelectedNode.Tag;
   EnableApply(!ApplySettings(gpsSource,true,true,false));
  }
  private void menuItemRename_Click(object sender, System.EventArgs e)
  {
   treeViewSources.LabelEdit=true;
   treeViewSources.SelectedNode.BeginEdit();
  }
  private void treeViewSources_AfterLabelEdit(object sender, System.Windows.Forms.NodeLabelEditEventArgs e)
  {
   if (e.Label!=null && e.Label.Length>0)
   {
    GPSSource gpsSource = (GPSSource)treeViewSources.SelectedNode.Tag;
    gpsSource.sDescription=e.Label;
    SaveSettings(null);
   }
   treeViewSources.LabelEdit=false;
  }
  private void ControlValueChanged(object sender, System.EventArgs e)
  {
   if (m_bHandleControlValueChangeEvent)
    EnableApply(true);
  }
  private void ControlValueChanged(object sender, System.Windows.Forms.KeyPressEventArgs e)
  {
   if (m_bHandleControlValueChangeEvent)
    EnableApply(true);
  }
  private void EnableApply(bool bEnable)
  {
   GPSSource gpsSource = (GPSSource)treeViewSources.SelectedNode.Tag;
   gpsSource.bNeedApply=bEnable;
   buttonApplyAPRSFilter.Enabled=gpsSource.bNeedApply;
   switch(gpsSource.sType)
   {
    case "COM":
     buttonApply.Enabled=gpsSource.bNeedApply;
     break;
    case "UDP":
     buttonApplyUDP.Enabled=gpsSource.bNeedApply;
     break;
    case "TCP":
     buttonApplyTCP.Enabled=gpsSource.bNeedApply;
     break;
    case "File":
     buttonApplyFile.Enabled=gpsSource.bNeedApply;
     break;
    case "APRS Internet":
     buttonApplyAPRSInternet.Enabled=gpsSource.bNeedApply;
     break;
    case "POI":
     buttonApplyPOI.Enabled=gpsSource.bNeedApply;
     break;
   }
  }
  private void StartStop_Click(object sender, System.EventArgs e)
  {
   bool fRet;
   GPSSource gpsSource;
   if (m_fInitialized==false)
   {
    bool bContinue=true;
    for (int i=0; i<m_gpsSourceList.Count; i++)
    {
     gpsSource = (GPSSource)m_gpsSourceList[i];
     if (gpsSource.bSetup==false)
     {
      bContinue=false;
      break;
     }
    }
    if (bContinue==false)
    {
     if (MessageBox.Show("One or more sources have not been configured.\nContinue anyway?", "GPSTracker", MessageBoxButtons.YesNo,MessageBoxIcon.Question, MessageBoxDefaultButton.Button1) == DialogResult.Yes)
      bContinue=true;
    }
    if (bContinue)
    {
     labelTrackCode.Text="";
     fRet=true;
     m_gpsTrackerPlugin.gpsOverlay.RemoveAll();
     m_gpsTrackerPlugin.gpsOverlay.IsOn = true;
     fRet=true;
     if (bContinue)
     {
      m_fInitialized=true;
      m_fPlayback=false;
      progressBarSetup.Maximum=m_gpsSourceList.Count+1;
      SaveSettings(null);
      m_iPlaybackSpeed=1;
      int i;
      for (i=0; i<m_gpsSourceList.Count; i++)
      {
       gpsSource = (GPSSource)m_gpsSourceList[i];
       if (gpsSource.bSetup)
       {
        if (gpsSource.sType=="File" && gpsSource.bSession==true)
        {
         if(File.Exists(gpsSource.sFileName))
         {
          m_iPlaybackSpeed = gpsSource.iPlaySpeed;
          m_sPlaybackFile = gpsSource.sFileName;
          m_srReader = File.OpenText(gpsSource.sFileName);
          if (LoadSettings(m_srReader,false))
          {
           m_srReader.Close();
           m_srReader = File.OpenText(m_sPlaybackFile);
           LoadSettings(m_srReader,true);
           m_fPlayback=true;
           m_bRecordSession=false;
           break;
          }
          m_srReader.Close();
          m_srReader=null;
         }
        }
       }
      }
      if (m_bRecordSession==false && m_fPlayback==false)
       LoadSettings(null,true);
      if (m_bRecordSession && m_fPlayback==false)
      {
                            m_swRecorder = File.CreateText(GpsTrackerPlugin.m_sPluginDirectory + "\\GpsTracker.recording");
       SaveSettings(m_swRecorder);
      }
      for (i=0; i<m_gpsSourceList.Count && m_fPlayback==false; i++)
      {
       gpsSource = (GPSSource)m_gpsSourceList[i];
       if (gpsSource.sType=="COM" && gpsSource.bSetup)
       {
        gpsSource.GpsCOM = new SerialPort(this, i, (uint)gpsSource.iCOMPort, (uint)gpsSource.iBaudRate, Convert.ToByte(gpsSource.iByteSize), Convert.ToByte(gpsSource.iParity), Convert.ToByte(gpsSource.iStopBits), (uint)gpsSource.iFlowControl);
        fRet = gpsSource.GpsCOM.Open();
        if (!fRet)
        {
         if (MessageBox.Show("Unable to initialize COM" + Convert.ToString(gpsSource.iCOMPort) + ":\nPlease check your port settings.\n\nContinue anyway?", "GPSTracker", MessageBoxButtons.YesNo,MessageBoxIcon.Question, MessageBoxDefaultButton.Button1) == DialogResult.Yes)
          bContinue=true;
         else
         {
          CloseCOMs();
          bContinue=false;
          break;
         }
        }
       }
      }
                        if (bContinue)
                        {
                            Start();
                            if (checkBoxMessagesMonitor.Checked)
                            {
                                m_MessageMonitor = new MessageMonitor();
                                m_MessageMonitor.Show();
                            }
                            else
                                m_MessageMonitor = null;
                            this.Left = -1000;
                            m_gpsTrackerPlugin.pluginWorldWindowFocus();
                            progressBarSetup.Visible = false;
                            labelSettingup.Visible = false;
                        }
                        else
                        {
                            m_fInitialized = false;
                            Stop();
                        }
        }
       }
   }
   else
   {
    labelTrackCode.ForeColor = System.Drawing.Color.Black;
    labelTrackCode.Text="Tracking enabled...";
   }
  }
  public void timerCallbackLocked(Object obj)
  {
   if (m_iPrevLocked==m_iLocked)
   {
    m_gpsTrackerPlugin.pluginLocked(false);
    m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: No Fix");
   }
   else
   {
    m_gpsTrackerPlugin.pluginLocked(true);
    m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Fix");
   }
   m_iPrevLocked=m_iLocked;
  }
  private void threadPOI()
  {
            try
            {
                Thread.Sleep(500);
                while (!m_fCloseThreads)
                {
                    lock (LockPOI)
                    {
                        for (int i = 0; i < m_gpsSourceList.Count; i++)
                        {
                            GPSSource gpsSource = (GPSSource)m_gpsSourceList[i];
                            if (!gpsSource.bSetup)
                                continue;
                            if (gpsSource.sType == "POI" && gpsSource.bPOISet == false)
                            {
                                gpsSource.bPOISet = true;
                                GPSRenderInformation renderInfo = new GPSRenderInformation();
                                renderInfo.bPOI = true;
                                renderInfo.iIndex = i;
                                renderInfo.sDescription = gpsSource.sDescription;
                                renderInfo.fFix = false;
                                renderInfo.fLat = gpsSource.fLat;
                                renderInfo.fLon = gpsSource.fLon;
                                renderInfo.sIcon = gpsSource.sIconPath;
                                renderInfo.bShowInfo = m_bInfoText;
                                renderInfo.bTrackLine = m_bTrackLine;
                                renderInfo.gpsTrack = gpsSource.GpsPos.m_gpsTrack;
                                renderInfo.bRestartTrack = false;
                                renderInfo.iDay = gpsSource.GpsPos.m_iDay;
                                renderInfo.iMonth = gpsSource.GpsPos.m_iMonth;
                                renderInfo.iYear = gpsSource.GpsPos.m_iYear;
                                renderInfo.colorTrack = gpsSource.colorTrack;
                                m_gpsTrackerPlugin.pluginShowOverlay(renderInfo);
                                if (gpsSource.bTrack)
                                    m_gpsTrackerPlugin.pluginWorldWindowGotoLatLonHeading(Convert.ToSingle(gpsSource.fLat), Convert.ToSingle(gpsSource.fLon), -1F, gpsSource.iStartAltitud);
                            }
                            else
                                if (gpsSource.sType == "POI" && gpsSource.bTrack)
                                    m_gpsTrackerPlugin.pluginWorldWindowGotoLatLonHeading(Convert.ToSingle(gpsSource.fLat), Convert.ToSingle(gpsSource.fLon), -1F, gpsSource.iStartAltitud);
                            if (gpsSource.iStartAltitud > 0)
                            {
                                Thread.Sleep(3000);
                                gpsSource.iStartAltitud = 0;
                            }
                            if (m_fCloseThreads)
                                break;
                        }
                    }
                    if (!m_fCloseThreads)
                        Thread.Sleep(1000);
                }
            }
            catch (Exception)
            {
            }
  }
  public void WebUpdate()
  {
   try
   {
    WebClient myWebClient = new WebClient();
    if (buttonCheckForUpdates.Text=="Check for Updates")
    {
     labelTrackCode.Text="";
     textBoxVersionInfo.Text="Your Version: " + m_gpsTrackerPlugin.m_sVersion + "\r\n";
                    myWebClient.DownloadFile("http://2bn.net/files/6/GpsTracker/GpsTrackerManifest.txt", GpsTrackerPlugin.m_sPluginDirectory + "\\GpsTrackerManifest.txt");
    }
    try
    {
                    if (!File.Exists(GpsTrackerPlugin.m_sPluginDirectory + "\\GpsTrackerManifest.txt"))
     {
      labelTrackCode.ForeColor = System.Drawing.Color.Red;
      labelTrackCode.Text="Unable to update GPSTracker...";
      return;
     }
                    using (StreamReader sr = File.OpenText(GpsTrackerPlugin.m_sPluginDirectory + "\\GpsTrackerManifest.txt"))
     {
      string line=sr.ReadLine();
      string sVersion=line;
                        char [] cSplit = {'R'};
                        string[] sVR = sVersion.Split(cSplit);
                        string sV = sVR[0].Substring(1);
                        string sR = sVR[1];
                        int iVersion = Convert.ToInt32(sV);
                        int iRevision = Convert.ToInt32(sR);
                        sVR = m_gpsTrackerPlugin.m_sVersion.Split(cSplit);
                        sV = sVR[0].Substring(1);
                        sR = sVR[1];
                        int iCVersion = Convert.ToInt32(sV);
                        int iCRevision = Convert.ToInt32(sR);
                        if (line != null && (iVersion > iCVersion || (iVersion == iCVersion && iRevision > iCRevision)))
      {
       sr.ReadLine();
       string sNotes="";
       do
       {
        line=sr.ReadLine();
        if (line.StartsWith("-")==true)
         break;
        sNotes+= line + "\r\n";
       } while(true);
       if (buttonCheckForUpdates.Text=="Check for Updates")
       {
        string sMsg="There's a new version of GPSTracker.\r\n";
        sMsg+="Your Version: " + m_gpsTrackerPlugin.m_sVersion + "\r\n";
        sMsg+="New Version: " + sVersion + "\r\n\r\n";
        sMsg+="Version Notes:\r\n";
        sMsg+=sNotes;
        textBoxVersionInfo.Text=sMsg;
        buttonCheckForUpdates.Text="Go to Update Page";
       }
       else
        System.Diagnostics.Process.Start("http://www.worldwindcentral.com/wiki/Add-on:GPS_Tracker_(plugin)");
      }
      else
      if (line!=null)
      {
       string sMsg="There's no need for an update.\r\n";
       sMsg+="Your Version: " + m_gpsTrackerPlugin.m_sVersion + "\r\n";
       sMsg+="Update Version: " + sVersion + "\r\n\r\n";
       textBoxVersionInfo.Text=sMsg;
      }
     }
    }
    catch(Exception)
    {
     labelTrackCode.ForeColor = System.Drawing.Color.Red;
     labelTrackCode.Text="Unable to update GPSTracker...";
     buttonCheckForUpdates.Text="Check for Updates";
    }
   }
   catch(Exception)
   {
    labelTrackCode.ForeColor = System.Drawing.Color.Red;
    labelTrackCode.Text="Unable to update GPSTracker...";
    buttonCheckForUpdates.Text="Check for Updates";
   }
  }
 }
}
