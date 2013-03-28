using System;
using System.IO;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Xml.Serialization;
using System.Runtime.InteropServices;
using System.Net;
using System.Threading;
using System.Text;
using System.Text.RegularExpressions;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Net;
using WorldWind.Renderable;
using WorldWind.Terrain;
namespace bNb.Plugins
{
    public class VirtualEarthForm : System.Windows.Forms.Form
    {
        private System.ComponentModel.Container components = null;
        private System.Windows.Forms.RadioButton rbRoad;
        private System.Windows.Forms.RadioButton rbAerial;
        private System.Windows.Forms.RadioButton rbDebug;
        private System.Windows.Forms.Button btnLocateMe;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.LinkLabel lnkBnb;
        private System.Windows.Forms.LinkLabel lnkLocalLive;
        private System.Windows.Forms.Button btnLocalLive;
        private System.Windows.Forms.TrackBar tbZoomLevel;
        private System.Windows.Forms.Label lblZoomLevel;
        private System.Windows.Forms.CheckBox cbLayerIsOn;
        private System.Windows.Forms.CheckBox cbTerrain;
        private System.Windows.Forms.Button btnFindBusiness;
        private System.Windows.Forms.TextBox txtNameCategory;
        private System.Windows.Forms.TextBox txtCityState;
        private System.Windows.Forms.TextBox txtAddress;
        private System.Windows.Forms.Button btnFindAddress;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.Button btnTrimCache;
        private System.Windows.Forms.NumericUpDown numTrimCache;
        private System.Windows.Forms.Label label9;
        private System.Windows.Forms.Label label10;
        private System.Windows.Forms.Button btnRemovePushPins;
        private System.Windows.Forms.RadioButton rbHybrid;
        private void InitializeComponent()
        {
            this.rbRoad = new System.Windows.Forms.RadioButton();
            this.rbAerial = new System.Windows.Forms.RadioButton();
            this.rbHybrid = new System.Windows.Forms.RadioButton();
            this.rbDebug = new System.Windows.Forms.RadioButton();
            this.btnLocateMe = new System.Windows.Forms.Button();
            this.lnkBnb = new System.Windows.Forms.LinkLabel();
            this.lnkLocalLive = new System.Windows.Forms.LinkLabel();
            this.label1 = new System.Windows.Forms.Label();
            this.btnLocalLive = new System.Windows.Forms.Button();
            this.tbZoomLevel = new System.Windows.Forms.TrackBar();
            this.lblZoomLevel = new System.Windows.Forms.Label();
            this.cbLayerIsOn = new System.Windows.Forms.CheckBox();
            this.cbTerrain = new System.Windows.Forms.CheckBox();
            this.txtNameCategory = new System.Windows.Forms.TextBox();
            this.txtCityState = new System.Windows.Forms.TextBox();
            this.btnFindBusiness = new System.Windows.Forms.Button();
            this.txtAddress = new System.Windows.Forms.TextBox();
            this.btnFindAddress = new System.Windows.Forms.Button();
            this.label3 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.label7 = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.label2 = new System.Windows.Forms.Label();
            this.label8 = new System.Windows.Forms.Label();
            this.btnTrimCache = new System.Windows.Forms.Button();
            this.numTrimCache = new System.Windows.Forms.NumericUpDown();
            this.label9 = new System.Windows.Forms.Label();
            this.label10 = new System.Windows.Forms.Label();
            this.btnRemovePushPins = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.tbZoomLevel)).BeginInit();
            this.groupBox1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.numTrimCache)).BeginInit();
            this.SuspendLayout();
            this.rbRoad.Location = new System.Drawing.Point(16, 16);
            this.rbRoad.Name = "rbRoad";
            this.rbRoad.Size = new System.Drawing.Size(56, 24);
            this.rbRoad.TabIndex = 1;
            this.rbRoad.Text = "Road";
            this.rbRoad.CheckedChanged += new System.EventHandler(this.group_CheckedChanged);
            this.rbAerial.Location = new System.Drawing.Point(16, 40);
            this.rbAerial.Name = "rbAerial";
            this.rbAerial.Size = new System.Drawing.Size(56, 24);
            this.rbAerial.TabIndex = 2;
            this.rbAerial.TabStop = true;
            this.rbAerial.Text = "Aerial";
            this.rbAerial.CheckedChanged += new System.EventHandler(this.group_CheckedChanged);
            this.rbHybrid.Location = new System.Drawing.Point(16, 64);
            this.rbHybrid.Name = "rbHybrid";
            this.rbHybrid.Size = new System.Drawing.Size(56, 24);
            this.rbHybrid.TabIndex = 3;
            this.rbHybrid.Text = "Hybrid";
            this.rbHybrid.CheckedChanged += new System.EventHandler(this.group_CheckedChanged);
            this.rbDebug.Location = new System.Drawing.Point(16, 88);
            this.rbDebug.Name = "rbDebug";
            this.rbDebug.Size = new System.Drawing.Size(56, 24);
            this.rbDebug.TabIndex = 5;
            this.rbDebug.Text = "Debug";
            this.rbDebug.CheckedChanged += new System.EventHandler(this.group_CheckedChanged);
            this.btnLocateMe.Location = new System.Drawing.Point(96, 16);
            this.btnLocateMe.Name = "btnLocateMe";
            this.btnLocateMe.Size = new System.Drawing.Size(144, 23);
            this.btnLocateMe.TabIndex = 6;
            this.btnLocateMe.Text = "\'Locate Me\' by IP Address";
            this.btnLocateMe.Click += new System.EventHandler(this.btnLocateMe_Click);
            this.lnkBnb.Location = new System.Drawing.Point(160, 304);
            this.lnkBnb.Name = "lnkBnb";
            this.lnkBnb.Size = new System.Drawing.Size(240, 23);
            this.lnkBnb.TabIndex = 7;
            this.lnkBnb.TabStop = true;
            this.lnkBnb.Text = "http://www.brains-N-brawn.com/veWorldWind/";
            this.lnkBnb.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.lnkBnb_LinkClicked);
            this.lnkLocalLive.Location = new System.Drawing.Point(288, 328);
            this.lnkLocalLive.Name = "lnkLocalLive";
            this.lnkLocalLive.Size = new System.Drawing.Size(112, 23);
            this.lnkLocalLive.TabIndex = 8;
            this.lnkLocalLive.TabStop = true;
            this.lnkLocalLive.Text = "http://local.live.com/";
            this.lnkLocalLive.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.lnkLocalLive_LinkClicked);
            this.label1.Location = new System.Drawing.Point(136, 328);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(168, 23);
            this.label1.TabIndex = 9;
            this.label1.Text = "data provided by Microsoft from";
            this.btnLocalLive.Location = new System.Drawing.Point(256, 16);
            this.btnLocalLive.Name = "btnLocalLive";
            this.btnLocalLive.Size = new System.Drawing.Size(128, 23);
            this.btnLocalLive.TabIndex = 10;
            this.btnLocalLive.Text = "Open in local.live.com";
            this.btnLocalLive.Click += new System.EventHandler(this.btnLocalLive_Click);
            this.tbZoomLevel.Location = new System.Drawing.Point(8, 216);
            this.tbZoomLevel.Maximum = 13;
            this.tbZoomLevel.Minimum = 3;
            this.tbZoomLevel.Name = "tbZoomLevel";
            this.tbZoomLevel.Size = new System.Drawing.Size(104, 45);
            this.tbZoomLevel.TabIndex = 12;
            this.tbZoomLevel.Value = 8;
            this.tbZoomLevel.ValueChanged += new System.EventHandler(this.tbZoomLevel_ValueChanged);
            this.lblZoomLevel.Location = new System.Drawing.Point(8, 200);
            this.lblZoomLevel.Name = "lblZoomLevel";
            this.lblZoomLevel.Size = new System.Drawing.Size(120, 23);
            this.lblZoomLevel.TabIndex = 13;
            this.lblZoomLevel.Text = "starting zoom level : 8";
            this.cbLayerIsOn.CheckState = System.Windows.Forms.CheckState.Checked;
            this.cbLayerIsOn.Location = new System.Drawing.Point(8, 8);
            this.cbLayerIsOn.Name = "cbLayerIsOn";
            this.cbLayerIsOn.Size = new System.Drawing.Size(72, 24);
            this.cbLayerIsOn.TabIndex = 14;
            this.cbLayerIsOn.Text = "Layer On";
            this.cbLayerIsOn.CheckedChanged += new System.EventHandler(this.cbLayerIsOn_CheckedChanged);
            this.cbTerrain.CheckState = System.Windows.Forms.CheckState.Checked;
            this.cbTerrain.Location = new System.Drawing.Point(8, 32);
            this.cbTerrain.Name = "cbTerrain";
            this.cbTerrain.Size = new System.Drawing.Size(80, 24);
            this.cbTerrain.TabIndex = 15;
            this.cbTerrain.Text = "Terrain On";
            this.cbTerrain.CheckedChanged += new System.EventHandler(this.cbTerrain_CheckedChanged);
            this.txtNameCategory.Location = new System.Drawing.Point(216, 64);
            this.txtNameCategory.Name = "txtNameCategory";
            this.txtNameCategory.Size = new System.Drawing.Size(176, 20);
            this.txtNameCategory.TabIndex = 16;
            this.txtCityState.Location = new System.Drawing.Point(216, 104);
            this.txtCityState.Name = "txtCityState";
            this.txtCityState.Size = new System.Drawing.Size(176, 20);
            this.txtCityState.TabIndex = 17;
            this.btnFindBusiness.Location = new System.Drawing.Point(296, 128);
            this.btnFindBusiness.Name = "btnFindBusiness";
            this.btnFindBusiness.Size = new System.Drawing.Size(96, 23);
            this.btnFindBusiness.TabIndex = 18;
            this.btnFindBusiness.Text = "Find Businesses";
            this.btnFindBusiness.Click += new System.EventHandler(this.btnFindBusiness_Click);
            this.txtAddress.Location = new System.Drawing.Point(216, 168);
            this.txtAddress.Name = "txtAddress";
            this.txtAddress.Size = new System.Drawing.Size(176, 20);
            this.txtAddress.TabIndex = 19;
            this.btnFindAddress.Location = new System.Drawing.Point(296, 208);
            this.btnFindAddress.Name = "btnFindAddress";
            this.btnFindAddress.Size = new System.Drawing.Size(96, 23);
            this.btnFindAddress.TabIndex = 20;
            this.btnFindAddress.Text = "Find Address";
            this.btnFindAddress.Click += new System.EventHandler(this.btnFindAddress_Click);
            this.label3.Location = new System.Drawing.Point(128, 64);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(88, 23);
            this.label3.TabIndex = 21;
            this.label3.Text = "Name/Category";
            this.label4.Location = new System.Drawing.Point(160, 104);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(56, 23);
            this.label4.TabIndex = 22;
            this.label4.Text = "City/State";
            this.label5.Location = new System.Drawing.Point(168, 168);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(48, 23);
            this.label5.TabIndex = 23;
            this.label5.Text = "Address";
            this.label6.Location = new System.Drawing.Point(216, 88);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(64, 23);
            this.label6.TabIndex = 24;
            this.label6.Text = "e.g. hooters";
            this.label7.Location = new System.Drawing.Point(216, 128);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(80, 23);
            this.label7.TabIndex = 25;
            this.label7.Text = "e.g. wisconsin";
            this.groupBox1.Controls.Add(this.rbDebug);
            this.groupBox1.Controls.Add(this.rbHybrid);
            this.groupBox1.Controls.Add(this.rbAerial);
            this.groupBox1.Controls.Add(this.rbRoad);
            this.groupBox1.Location = new System.Drawing.Point(8, 64);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(88, 120);
            this.groupBox1.TabIndex = 26;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Map Type";
            this.label2.Location = new System.Drawing.Point(8, 256);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(112, 40);
            this.label2.TabIndex = 27;
            this.label2.Text = "a lower zoom level will render VE tiles at higher altitudes";
            this.label8.Location = new System.Drawing.Point(216, 192);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(168, 23);
            this.label8.TabIndex = 28;
            this.label8.Text = "e.g. one microsoft way, redmond";
            this.btnTrimCache.Location = new System.Drawing.Point(288, 272);
            this.btnTrimCache.Name = "btnTrimCache";
            this.btnTrimCache.Size = new System.Drawing.Size(104, 23);
            this.btnTrimCache.TabIndex = 29;
            this.btnTrimCache.Text = "Trim Cached Files";
            this.btnTrimCache.Click += new System.EventHandler(this.btnTrimCache_Click);
            this.numTrimCache.Location = new System.Drawing.Point(304, 248);
            this.numTrimCache.Name = "numTrimCache";
            this.numTrimCache.Size = new System.Drawing.Size(40, 20);
            this.numTrimCache.TabIndex = 30;
            this.numTrimCache.Value = new decimal(new int[] {
            7,
            0,
            0,
            0});
            this.label9.Location = new System.Drawing.Point(192, 256);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(120, 23);
            this.label9.TabIndex = 31;
            this.label9.Text = "Delete tiles more than";
            this.label10.Location = new System.Drawing.Point(344, 256);
            this.label10.Name = "label10";
            this.label10.Size = new System.Drawing.Size(48, 23);
            this.label10.TabIndex = 32;
            this.label10.Text = "days old";
            this.btnRemovePushPins.Location = new System.Drawing.Point(176, 208);
            this.btnRemovePushPins.Name = "btnRemovePushPins";
            this.btnRemovePushPins.Size = new System.Drawing.Size(112, 23);
            this.btnRemovePushPins.TabIndex = 33;
            this.btnRemovePushPins.Text = "Remove PushPins";
            this.btnRemovePushPins.Click += new System.EventHandler(this.btnRemovePushPins_Click);
            this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
            this.ClientSize = new System.Drawing.Size(402, 352);
            this.Controls.Add(this.btnRemovePushPins);
            this.Controls.Add(this.btnTrimCache);
            this.Controls.Add(this.numTrimCache);
            this.Controls.Add(this.label10);
            this.Controls.Add(this.label9);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.btnFindBusiness);
            this.Controls.Add(this.label7);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.btnFindAddress);
            this.Controls.Add(this.txtAddress);
            this.Controls.Add(this.txtCityState);
            this.Controls.Add(this.txtNameCategory);
            this.Controls.Add(this.cbTerrain);
            this.Controls.Add(this.cbLayerIsOn);
            this.Controls.Add(this.lblZoomLevel);
            this.Controls.Add(this.tbZoomLevel);
            this.Controls.Add(this.btnLocalLive);
            this.Controls.Add(this.lnkLocalLive);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.lnkBnb);
            this.Controls.Add(this.btnLocateMe);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.label8);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "VirtualEarthForm";
            this.Text = "VirtualEarth Plugin v1.0";
            this.Load += new System.EventHandler(this.VirtualEarthForm_Load);
            ((System.ComponentModel.ISupportInitialize)(this.tbZoomLevel)).EndInit();
            this.groupBox1.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.numTrimCache)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();
        }
        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                if (components != null)
                {
                    components.Dispose();
                }
            }
            lock (m_WorldWindow.CurrentWorld.RenderableObjects.ChildObjects.SyncRoot)
            {
                veLayer.IsOn = false;
                foreach (WorldWind.Renderable.RenderableObject ro in m_WorldWindow.CurrentWorld.RenderableObjects.ChildObjects)
                {
                    if (ro is WorldWind.Renderable.RenderableObjectList && ro.Name.IndexOf("Images") >= 0)
                    {
                        WorldWind.Renderable.RenderableObjectList imagesList = ro as WorldWind.Renderable.RenderableObjectList;
                        imagesList.ChildObjects.Remove(veLayer);
                        break;
                    }
                }
                veLayer.Dispose();
            }
            string settingspath = Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath) + "\\Plugins\\VirtualEarth\\Settings.xml";
            VESettings.SaveSettingsToFile(settingspath, settings);
            base.Dispose(disposing);
        }
        private WorldWind.WorldWindow m_WorldWindow = null;
        public WorldWind.WorldWindow WorldWindow
        {
            get { return m_WorldWindow; }
        }
        public bool IsDebug
        {
            get
            {
                return rbDebug.Checked;
            }
        }
        public int StartZoomLevel
        {
            get
            {
                return settings.ZoomLevel;
            }
        }
        private VeReprojectTilesLayer veLayer;
        public VeReprojectTilesLayer VeLayer
        {
            get { return veLayer; }
        }
        private string cacheDirectory;
        private string pushPinTexture;
        private VESettings settings = new VESettings();
        public VirtualEarthForm(MainApplication parentApplication)
        {
            InitializeComponent();
            try
            {
                m_WorldWindow = parentApplication.WorldWindow;
                veLayer = new VeReprojectTilesLayer("VirtualEarth Tiles", parentApplication, this);
                lock (m_WorldWindow.CurrentWorld.RenderableObjects.ChildObjects.SyncRoot)
                {
                    foreach (WorldWind.Renderable.RenderableObject ro in m_WorldWindow.CurrentWorld.RenderableObjects.ChildObjects)
                    {
                        if (ro is WorldWind.Renderable.RenderableObjectList && ro.Name.IndexOf("Images") >= 0)
                        {
                            WorldWind.Renderable.RenderableObjectList imagesList = ro as WorldWind.Renderable.RenderableObjectList;
                            imagesList.ChildObjects.Insert(imagesList.ChildObjects.Count - 1, veLayer);
                            break;
                        }
                    }
                }
                cacheDirectory = String.Format("{0}\\Virtual Earth", m_WorldWindow.Cache.CacheDirectory);
                if (Directory.Exists(cacheDirectory) == true)
                {
                    DirectoryInfo diCache = new DirectoryInfo(cacheDirectory);
                }
                pushPinTexture = Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath) + "\\Plugins\\VirtualEarth\\VirtualEarthPushPin.png";
                if (File.Exists(pushPinTexture) == false)
                {
                    Utility.Log.Write(new Exception("pushPinTexture not found " + pushPinTexture));
                }
                string projDllPath = Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath) + "\\proj.dll";
                if (File.Exists(projDllPath) == false)
                {
                    veLayer.IsOn = false;
                    throw new Exception("'proj.dll' needs to be in the same directory where WorldWind.exe is installed");
                }
                string settingspath = Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath) + "\\Plugins\\VirtualEarth\\Settings.xml";
                if (File.Exists(settingspath))
                    settings = VESettings.LoadSettingsFromFile(settingspath);
                else
                    VESettings.SaveSettingsToFile(settingspath, settings);
                tbZoomLevel.Value = settings.ZoomLevel;
                cbLayerIsOn.Checked = settings.LayerOn;
                cbTerrain.Checked = settings.Terrain;
                rbRoad.Checked = settings.Road;
                rbAerial.Checked = settings.Aerial;
                rbHybrid.Checked = settings.Hybrid;
                rbDebug.Checked = settings.Debug;
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
                throw;
            }
        }
        private void VirtualEarthForm_Load(object sender, System.EventArgs e)
        {
        }
        public string GetDataSetName()
        {
            string dataSetName = "r";
            if (rbRoad.Checked == true)
            {
                dataSetName = "r";
            }
            else if (rbAerial.Checked == true)
            {
                dataSetName = "a";
            }
            else if (rbHybrid.Checked == true)
            {
                dataSetName = "h";
            }
            return dataSetName;
        }
        public string GetImageExtension()
        {
            string imageExtension = "jpeg";
            if (rbRoad.Checked == true)
            {
                imageExtension = "png";
            }
            return imageExtension;
        }
        protected override void OnClosing(CancelEventArgs e)
        {
            e.Cancel = true;
            this.Visible = false;
            base.OnClosing(e);
        }
        private string previousDataSetName = null;
        private void group_CheckedChanged(object sender, System.EventArgs e)
        {
            try
            {
                string curDataSetName = this.GetDataSetName();
                if (curDataSetName != previousDataSetName)
                {
                    veLayer.RemoveAllTiles();
                    veLayer.ForceRefresh();
                }
                this.settings.Road = rbRoad.Checked;
                this.settings.Hybrid = rbHybrid.Checked;
                this.settings.Aerial = rbAerial.Checked;
                this.settings.Debug = rbDebug.Checked;
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
            }
        }
        private void btnLocateMe_Click(object sender, System.EventArgs e)
        {
            HttpWebResponse hwRes = null;
            Stream s = null;
            StreamReader sr = null;
            try
            {
                string reqUrl = "http://virtualearth.msn.com/WiFiIPService/locate.ashx";
                HttpWebRequest hwReq = (HttpWebRequest)WebRequest.Create(reqUrl);
                hwReq.UserAgent = "NASA WorldWind 1.3.3.1";
                hwReq.Timeout = 10000;
                hwRes = (HttpWebResponse)hwReq.GetResponse();
                s = hwRes.GetResponseStream();
                sr = new StreamReader(s);
                string result = sr.ReadToEnd();
                int index = result.ToLower().IndexOf("setautolocateviewport");
                if (index != -1)
                {
                    int openParen = result.IndexOf("(");
                    if (openParen != -1)
                    {
                        result = result.Substring(openParen + 1, result.Length - openParen - 1);
                        string strSplitChar = ",";
                        string[] strVals = result.Split(strSplitChar.ToCharArray());
                        if (strVals.Length >= 2)
                        {
                            float lat = float.Parse(strVals[0]);
                            float lon = float.Parse(strVals[1]);
                            m_WorldWindow.GotoLatLon(lat, lon);
                        }
                    }
                }
                else
                {
                    MessageBox.Show("could not auto locate");
                }
            }
            catch (Exception ex)
            {
                string sex = ex.ToString();
                Utility.Log.Write(ex);
            }
            finally
            {
                if (sr != null)
                {
                    sr.Close();
                    sr = null;
                }
                if (s != null)
                {
                    s.Close();
                    s = null;
                }
                if (hwRes != null)
                {
                    hwRes.Close();
                    hwRes = null;
                }
            }
        }
        private void btnLocalLive_Click(object sender, System.EventArgs e)
        {
            try
            {
                string link = veLayer.GetLocalLiveLink();
                System.Diagnostics.Process.Start(link);
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
            }
        }
        private void lnkLocalLive_LinkClicked(object sender, System.Windows.Forms.LinkLabelLinkClickedEventArgs e)
        {
            System.Diagnostics.Process.Start("http://local.live.com/");
        }
        private void lnkBnb_LinkClicked(object sender, System.Windows.Forms.LinkLabelLinkClickedEventArgs e)
        {
            System.Diagnostics.Process.Start("http://www.brains-N-brawn.com/veWorldWind/");
        }
        private void tbZoomLevel_ValueChanged(object sender, System.EventArgs e)
        {
            lblZoomLevel.Text = "starting zoom level : " + tbZoomLevel.Value.ToString();
            this.settings.ZoomLevel = tbZoomLevel.Value;
        }
        private void cbLayerIsOn_CheckedChanged(object sender, System.EventArgs e)
        {
            if (cbLayerIsOn.Checked == true)
            {
                veLayer.IsOn = true;
            }
            else
            {
                veLayer.IsOn = false;
            }
            this.settings.LayerOn = cbLayerIsOn.Checked;
        }
        private void btnFindBusiness_Click(object sender, System.EventArgs e)
        {
            try
            {
                veLayer.PushPins = null;
                string nameCategory = txtNameCategory.Text.Trim();
                string cityState = txtCityState.Text.Trim();
                double lat1, lon1, lat2, lon2;
                double halfViewRange = m_WorldWindow.DrawArgs.WorldCamera.TrueViewRange.Degrees / 2;
                double lat = m_WorldWindow.DrawArgs.WorldCamera.Latitude.Degrees;
                double lon = m_WorldWindow.DrawArgs.WorldCamera.Longitude.Degrees;
                lat1 = lat + halfViewRange;
                lon1 = lon + halfViewRange;
                lat2 = lat - halfViewRange;
                lon2 = lon - halfViewRange;
                if (cityState.Length > 0)
                {
                    bool retVal = Search.SearchForAddress(cityState, out lat1, out lon1, out lat2, out lon2);
                }
                ArrayList alPushPins = Search.SearchForBusiness(nameCategory, lat1, lon1, lat2, lon2);
                if (alPushPins.Count <= 0)
                {
                    MessageBox.Show("no businesses found");
                }
                else if (alPushPins.Count == 1)
                {
                    veLayer.PushPins = alPushPins;
                    PushPin pp = (PushPin)alPushPins[0];
                    double altitude = 5000;
                    m_WorldWindow.GotoLatLonAltitude(pp.Latitude, pp.Longitude, altitude);
                }
                else
                {
                    veLayer.PushPins = alPushPins;
                    double ppLat = (lat1 + lat2) / 2;
                    double ppLon = (lon1 + lon2) / 2;
                    double perpViewRange = Math.Abs(lat1 - lat2);
                    m_WorldWindow.GotoLatLonViewRange(ppLat, ppLon, perpViewRange);
                }
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
            }
        }
        private void btnFindAddress_Click(object sender, System.EventArgs e)
        {
            try
            {
                veLayer.PushPins = null;
                string address = txtAddress.Text.Trim();
                double lat1, lon1, lat2, lon2;
                bool retVal = Search.SearchForAddress(address, out lat1, out lon1, out lat2, out lon2);
                if (retVal == true)
                {
                    double lat = (lat1 + lat2) / 2;
                    double lon = (lon1 + lon2) / 2;
                    PushPin pp = new PushPin();
                    pp.Address = address;
                    pp.Latitude = lat;
                    pp.Longitude = lon;
                    ArrayList alPushPins = new ArrayList();
                    alPushPins.Add(pp);
                    veLayer.PushPins = alPushPins;
                    double altitude = 5000;
                    m_WorldWindow.GotoLatLonAltitude(lat, lon, altitude);
                }
                else
                {
                    MessageBox.Show("address not found");
                }
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
            }
        }
        private void cbTerrain_CheckedChanged(object sender, System.EventArgs e)
        {
            try
            {
                veLayer.RemoveAllTiles();
                veLayer.ForceRefresh();
                this.settings.Terrain = cbTerrain.Checked;
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
            }
        }
        private void btnTrimCache_Click(object sender, System.EventArgs e)
        {
            try
            {
                if (Directory.Exists(cacheDirectory) == true)
                {
                    DirectoryInfo diCache = new DirectoryInfo(cacheDirectory);
                    int numDays = (int)numTrimCache.Value * -1;
                    DateTime cutOffDate = DateTime.Now.AddDays(numDays);
                    RecurseDeleteOldFiles(diCache, cutOffDate);
                }
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
            }
        }
        public void RecurseDeleteOldFiles(DirectoryInfo di, DateTime cutOffDate)
        {
            foreach (FileInfo fi in di.GetFiles("*.png"))
            {
                if (fi.CreationTime < cutOffDate)
                {
                    fi.Delete();
                }
            }
            foreach (FileInfo fi in di.GetFiles("*.jpeg"))
            {
                if (fi.CreationTime < cutOffDate)
                {
                    fi.Delete();
                }
            }
            foreach (DirectoryInfo tempDi in di.GetDirectories())
            {
                RecurseDeleteOldFiles(tempDi, cutOffDate);
            }
        }
        private void btnRemovePushPins_Click(object sender, System.EventArgs e)
        {
            try
            {
                veLayer.PushPins = null;
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
            }
        }
        public bool IsTerrainOn
        {
            get { return cbTerrain.Checked; }
        }
    }
    public class VirtualEarthPlugin : WorldWind.PluginEngine.Plugin
    {
        VirtualEarthForm m_Form = null;
        MenuItem m_MenuItem;
        WorldWind.WindowsControlMenuButton m_ToolbarItem;
        public override void Load()
        {
            try
            {
                if (ParentApplication.WorldWindow.CurrentWorld.IsEarth)
                {
                    m_Form = new VirtualEarthForm(ParentApplication);
                    m_Form.Owner = ParentApplication;
                    m_MenuItem = new MenuItem("VirtualEarth");
                    m_MenuItem.Click += new EventHandler(menuItemClicked);
                    ParentApplication.PluginsMenu.MenuItems.Add(m_MenuItem);
                    string imgPath = Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath) + "\\Plugins\\VirtualEarth\\VirtualEarthPlugin.png";
                    if (File.Exists(imgPath) == false)
                    {
                        Utility.Log.Write(new Exception("imgPath not found " + imgPath));
                    }
                    m_ToolbarItem = new WorldWind.WindowsControlMenuButton(
                        "VirtualEarth",
                        imgPath,
                        m_Form);
                    ParentApplication.WorldWindow.MenuBar.AddToolsMenuButton(m_ToolbarItem);
                    base.Load();
                }
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
                throw;
            }
        }
        public override void Unload()
        {
            try
            {
                if (m_Form != null)
                {
                    m_Form.Dispose();
                    m_Form = null;
                    ParentApplication.PluginsMenu.MenuItems.Remove(m_MenuItem);
                    ParentApplication.WorldWindow.MenuBar.RemoveToolsMenuButton(m_ToolbarItem);
                }
                base.Unload();
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
                throw;
            }
        }
        private void menuItemClicked(object sender, System.EventArgs e)
        {
            if (m_Form.Visible)
            {
                m_Form.Visible = false;
                m_MenuItem.Checked = false;
            }
            else
            {
                m_Form.Visible = true;
                m_MenuItem.Checked = true;
            }
        }
    }
    public class VeReprojectTilesLayer : RenderableObject
    {
        private Projection proj;
        private MainApplication parentApplication;
        private VirtualEarthForm veForm;
        private static double earthRadius;
        private static double earthCircum;
        private static double earthHalfCirc;
        private const int pixelsPerTile = 256;
        private int prevRow = -1;
        private int prevCol = -1;
        private int prevLvl = -1;
        private float prevVe = -1;
        private ArrayList veTiles = new ArrayList();
        public VeReprojectTilesLayer(string name, MainApplication parentApplication, VirtualEarthForm veForm)
            : base(name)
        {
            this.name = name;
            this.parentApplication = parentApplication;
            this.veForm = veForm;
        }
        private Sprite sprite;
        private Texture spriteTexture;
        float scaleWidth = .25f;
        float scaleHeight = .25f;
        int iconWidth = 128;
        int iconHeight = 128;
        Rectangle spriteSize;
        public override void Initialize(DrawArgs drawArgs)
        {
            try
            {
                if (this.isInitialized == true)
                {
                    return;
                }
                sprite = new Sprite(drawArgs.device);
                string spritePath = Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath) + "\\Plugins\\VirtualEarth\\VirtualEarthPushPin.png";
                if (File.Exists(spritePath) == false)
                {
                    Utility.Log.Write(new Exception("spritePath not found " + spritePath));
                }
                spriteSize = new Rectangle(0, 0, iconWidth, iconHeight);
                spriteTexture = TextureLoader.FromFile(drawArgs.device, spritePath);
                earthRadius = parentApplication.WorldWindow.CurrentWorld.EquatorialRadius;
                earthCircum = earthRadius * 2.0 * Math.PI;
                earthHalfCirc = earthCircum / 2;
                string[] projectionParameters = new string[] { "proj=merc", "ellps=sphere", "a=" + earthRadius.ToString(), "es=0.0", "no.defs" };
                proj = new Projection(projectionParameters);
                VeTile.Init(this.proj, parentApplication.WorldWindow.CurrentWorld.TerrainAccessor, parentApplication.WorldWindow.CurrentWorld.EquatorialRadius, veForm);
                prevVe = World.Settings.VerticalExaggeration;
                this.isInitialized = true;
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
                throw;
            }
        }
        public string GetLocalLiveLink()
        {
            string lat = parentApplication.WorldWindow.DrawArgs.WorldCamera.Latitude.Degrees.ToString("###.#####");
            string lon = parentApplication.WorldWindow.DrawArgs.WorldCamera.Longitude.Degrees.ToString("###.#####");
            string link = "http://local.live.com/default.aspx?v=2&cp=" + lat + "~" + lon + "&styles=" + veForm.GetDataSetName() + "&lvl=" + prevLvl.ToString();
            return link;
        }
        public void RemoveAllTiles()
        {
            lock (veTiles.SyncRoot)
            {
                for (int i = 0; i < veTiles.Count; i++)
                {
                    VeTile veTile = (VeTile)veTiles[i];
                    veTile.Dispose();
                    veTiles.RemoveAt(i);
                }
                veTiles.Clear();
            }
        }
        public void ForceRefresh()
        {
            prevRow = -1;
            prevCol = -1;
            prevLvl = -1;
        }
        public override void Update(DrawArgs drawArgs)
        {
            try
            {
                if (this.isOn == false)
                {
                    return;
                }
                if (this.isInitialized == false)
                {
                    this.Initialize(drawArgs);
                    return;
                }
                double lat = drawArgs.WorldCamera.Latitude.Degrees;
                double lon = drawArgs.WorldCamera.Longitude.Degrees;
                double alt = drawArgs.WorldCamera.Altitude;
                Angle tvr = drawArgs.WorldCamera.TrueViewRange;
                int zoomLevel = GetZoomLevelByTrueViewRange(tvr.Degrees);
                if (zoomLevel < veForm.StartZoomLevel)
                {
                    this.RemoveAllTiles();
                    return;
                }
                double metersY;
                double yMeters;
                int yMetersPerPixel;
                int row;
                double metersX = earthRadius * DegToRad(lon);
                double xMeters = earthHalfCirc + metersX;
                int xMetersPerPixel = (int)Math.Round(xMeters / MetersPerPixel(zoomLevel));
                int col = xMetersPerPixel / pixelsPerTile;
                UV uvCurrent = new UV(DegToRad(lon), DegToRad(lat));
                uvCurrent = proj.Forward(uvCurrent);
                metersY = uvCurrent.V;
                yMeters = earthHalfCirc - metersY;
                yMetersPerPixel = (int)Math.Round(yMeters / MetersPerPixel(zoomLevel));
                row = yMetersPerPixel / pixelsPerTile;
                if (prevVe != World.Settings.VerticalExaggeration)
                {
                    lock (veTiles.SyncRoot)
                    {
                        VeTile veTile;
                        for (int i = 0; i < veTiles.Count; i++)
                        {
                            veTile = (VeTile)veTiles[i];
                            if (veTile.VertEx != World.Settings.VerticalExaggeration)
                            {
                                veTile.CreateMesh(this.Opacity, World.Settings.VerticalExaggeration);
                            }
                        }
                    }
                }
                prevVe = World.Settings.VerticalExaggeration;
                if (row == prevRow && col == prevCol && zoomLevel == prevLvl)
                {
                    return;
                }
                lock (veTiles.SyncRoot)
                {
                    VeTile veTile;
                    for (int i = 0; i < veTiles.Count; i++)
                    {
                        veTile = (VeTile)veTiles[i];
                        veTile.IsNeeded = false;
                    }
                }
                ArrayList alMetadata = null;
                if (veForm.IsDebug == true)
                {
                    alMetadata = new ArrayList();
                    alMetadata.Add("yMeters " + yMeters.ToString());
                    alMetadata.Add("metersY " + metersY.ToString());
                    alMetadata.Add("yMeters2 " + yMeters.ToString());
                    alMetadata.Add("vLat " + uvCurrent.V.ToString());
                }
                AddVeTile(drawArgs, row, col, zoomLevel, alMetadata);
                AddNeighborTiles(drawArgs, row, col, zoomLevel, null, 1);
                AddNeighborTiles(drawArgs, row, col, zoomLevel, null, 2);
                AddNeighborTiles(drawArgs, row, col, zoomLevel, null, 3);
                lock (veTiles.SyncRoot)
                {
                    VeTile veTile;
                    for (int i = 0; i < veTiles.Count; i++)
                    {
                        veTile = (VeTile)veTiles[i];
                        if (veTile.IsNeeded == false)
                        {
                            veTile.Dispose();
                            veTiles.RemoveAt(i);
                        }
                    }
                }
                prevRow = row;
                prevCol = col;
                prevLvl = zoomLevel;
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
            }
        }
        private void AddNeighborTiles(DrawArgs drawArgs, int row, int col, int zoomLevel, ArrayList alMetadata, int range)
        {
            int minRow = row - range;
            int maxRow = row + range;
            int minCol = col - range;
            int maxCol = col + range;
            for (int i = minRow; i <= maxRow; i++)
            {
                for (int j = minCol; j <= maxCol; j++)
                {
                    if (i == minRow || i == maxRow || j == minCol || j == maxCol)
                    {
                        AddVeTile(drawArgs, i, j, zoomLevel, alMetadata);
                    }
                }
            }
        }
        private void AddVeTile(DrawArgs drawArgs, int row, int col, int zoomLevel, ArrayList alMetadata)
        {
            bool tileFound = false;
            lock (veTiles.SyncRoot)
            {
                foreach (VeTile veTile in veTiles)
                {
                    if (veTile.IsNeeded == true)
                    {
                        continue;
                    }
                    if (veTile.IsEqual(row, col, zoomLevel) == true)
                    {
                        veTile.IsNeeded = true;
                        tileFound = true;
                        break;
                    }
                }
            }
            if (tileFound == false)
            {
                int curZoomLevel = GetZoomLevelByTrueViewRange(drawArgs.WorldCamera.TrueViewRange.Degrees);
                if (curZoomLevel != zoomLevel)
                {
                    return;
                }
                VeTile newVeTile = CreateVeTile(drawArgs, row, col, zoomLevel, alMetadata);
                newVeTile.IsNeeded = true;
                lock (veTiles.SyncRoot)
                {
                    veTiles.Add(newVeTile);
                }
            }
        }
        private VeTile CreateVeTile(DrawArgs drawArgs, int row, int col, int zoomLevel, ArrayList alMetadata)
        {
            VeTile newVeTile = new VeTile(row, col, zoomLevel);
            if (alMetadata != null)
            {
                foreach (string metadata in alMetadata)
                {
                    newVeTile.AddMetaData(metadata);
                }
            }
            newVeTile.GetTexture(drawArgs, pixelsPerTile);
            double metersPerPixel = MetersPerPixel(zoomLevel);
            double totalTilesPerEdge = Math.Pow(2, zoomLevel);
            double totalMeters = totalTilesPerEdge * pixelsPerTile * metersPerPixel;
            double halfMeters = totalMeters / 2;
            double N = row * (pixelsPerTile * metersPerPixel);
            double W = col * (pixelsPerTile * metersPerPixel);
            N = halfMeters - N;
            W = W - halfMeters;
            double E = W + (pixelsPerTile * metersPerPixel);
            double S = N - (pixelsPerTile * metersPerPixel);
            newVeTile.UL = new UV(W, N);
            newVeTile.UR = new UV(E, N);
            newVeTile.LL = new UV(W, S);
            newVeTile.LR = new UV(E, S);
            byte opacity = this.Opacity;
            float verticalExaggeration = World.Settings.VerticalExaggeration;
            newVeTile.CreateMesh(opacity, verticalExaggeration);
            newVeTile.CreateDownloadRectangle(drawArgs, World.Settings.DownloadProgressColor.ToArgb());
            return newVeTile;
        }
        private static double MetersPerTile(int zoom)
        {
            return MetersPerPixel(zoom) * pixelsPerTile;
        }
        private static double MetersPerPixel(int zoom)
        {
            double arc;
            arc = earthCircum / ((1 << zoom) * pixelsPerTile);
            return arc;
        }
        private static double DegToRad(double d)
        {
            return d * Math.PI / 180.0;
        }
        private static double RadToDeg(double d)
        {
            return d * 180 / Math.PI;
        }
        public double GetLevelDegrees(int level)
        {
            double metersPerPixel = MetersPerPixel(level);
            double arcDistance = metersPerPixel * pixelsPerTile;
            double tileRange = (arcDistance / earthCircum) * 360;
            return tileRange;
        }
        public int GetZoomLevelByTrueViewRange(double trueViewRange)
        {
            int maxLevel = 3;
            int minLevel = 19;
            int numLevels = minLevel - maxLevel + 1;
            int retLevel = maxLevel;
            for (int i = 0; i < numLevels; i++)
            {
                retLevel = i + maxLevel;
                double viewAngle = 180;
                for (int j = 0; j < i; j++)
                {
                    viewAngle = viewAngle / 2.0;
                }
                if (trueViewRange >= viewAngle)
                {
                    break;
                }
            }
            return retLevel;
        }
        public int GetZoomLevelByArcDistance(double arcDistance)
        {
            int totalLevels = 24;
            int level = 0;
            for (level = 1; level <= totalLevels; level++)
            {
                double metersPerPixel = MetersPerPixel(level);
                double totalDistance = metersPerPixel * pixelsPerTile;
                if (arcDistance > totalDistance)
                {
                    break;
                }
            }
            return level - 1;
        }
        private int LatitudeToYAtZoom(double lat, int zoom)
        {
            int y;
            double arc = earthCircum / ((1 << zoom) * pixelsPerTile);
            double sinLat = Math.Sin(DegToRad(lat));
            double metersY = earthRadius / 2 * Math.Log((1 + sinLat) / (1 - sinLat));
            y = (int)Math.Round((earthHalfCirc - metersY) / arc);
            return y;
        }
        private int LongitudeToXAtZoom(double lon, int zoom)
        {
            int x;
            double arc = earthCircum / ((1 << zoom) * pixelsPerTile);
            double metersX = earthRadius * DegToRad(lon);
            x = (int)Math.Round((earthHalfCirc + metersX) / arc);
            return x;
        }
        public override void Render(DrawArgs drawArgs)
        {
            try
            {
                if (this.isOn == false)
                {
                    return;
                }
                if (this.isInitialized == false)
                {
                    return;
                }
                if (drawArgs.device == null)
                    return;
                if (veTiles != null && veTiles.Count > 0)
                {
                    bool disableZBuffer = false;
                    drawArgs.device.Transform.World = Matrix.Translation(
                           (float)-drawArgs.WorldCamera.ReferenceCenter.X,
                        (float)-drawArgs.WorldCamera.ReferenceCenter.Y,
                        (float)-drawArgs.WorldCamera.ReferenceCenter.Z
                    );
                    VeTile.Render(drawArgs, disableZBuffer, veTiles);
                    drawArgs.device.Transform.World = drawArgs.WorldCamera.WorldMatrix;
                }
                if (pushPins != null && pushPins.Count > 0)
                {
                    RenderPushPins(drawArgs);
                }
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
            }
        }
        public void GetViewPort(DrawArgs drawArgs, out double lat1, out double lon1, out double lat2, out double lon2)
        {
            double halfViewRange = drawArgs.WorldCamera.TrueViewRange.Degrees / 2;
            double lat = drawArgs.WorldCamera.Latitude.Degrees;
            double lon = drawArgs.WorldCamera.Longitude.Degrees;
            lat1 = lat + halfViewRange;
            lon1 = lon + halfViewRange;
            lat2 = lat - halfViewRange;
            lon2 = lon - halfViewRange;
        }
        private ArrayList pushPins = null;
        public ArrayList PushPins
        {
            get { return pushPins; }
            set { pushPins = value; }
        }
        public void RenderPushPins(DrawArgs drawArgs)
        {
            if (pushPins == null || pushPins.Count <= 0)
                return;
            double lat1, lon1, lat2, lon2;
            GetViewPort(drawArgs, out lat1, out lon1, out lat2, out lon2);
            Vector3 projectedPoint;
            lock (pushPins.SyncRoot)
            {
                foreach (PushPin p in pushPins)
                {
                    if (p.Latitude <= lat1 && p.Latitude >= lat2)
                    {
                        if (p.Longitude <= lon1 && p.Longitude >= lon2)
                        {
                            projectedPoint = MathEngine.SphericalToCartesian((float)p.Latitude, (float)p.Longitude, (float)earthRadius + 100);
                            projectedPoint.Project(drawArgs.device.Viewport, drawArgs.WorldCamera.ProjectionMatrix, drawArgs.WorldCamera.ViewMatrix, drawArgs.WorldCamera.WorldMatrix);
                            sprite.Begin(SpriteFlags.AlphaBlend);
                            sprite.Transform = Matrix.Transformation2D(new Vector2(0.0f, 0.0f),
                                0.0f, new Vector2(scaleWidth, scaleHeight),
                                new Vector2(0, 0), 0.0f, new Vector2(projectedPoint.X, projectedPoint.Y));
                            sprite.Draw(spriteTexture, spriteSize, new Vector3(.5f * iconWidth, .5f * iconHeight, 0), new Vector3(0, 0, 0), System.Drawing.Color.White);
                            sprite.End();
                        }
                    }
                }
            }
        }
        public override void Dispose()
        {
            RemoveAllTiles();
            if (sprite != null)
            {
                sprite.Dispose();
                sprite = null;
            }
        }
        public override bool PerformSelectionAction(DrawArgs drawArgs)
        {
            return false;
        }
    }
    [Serializable]
    public class VESettings
    {
        private int zoomlevel = 8;
        private bool layeron = true;
        private bool terrain = true;
        private bool road = true;
        private bool aerial = false;
        private bool hybrid = false;
        private bool debug = false;
        public int ZoomLevel
        {
            get
            {
                return zoomlevel;
            }
            set
            {
                zoomlevel = value;
            }
        }
        public bool LayerOn
        {
            get
            {
                return layeron;
            }
            set
            {
                layeron = value;
            }
        }
        public bool Terrain
        {
            get
            {
                return terrain;
            }
            set
            {
                terrain = value;
            }
        }
        public bool Road
        {
            get
            {
                return road;
            }
            set
            {
                road = value;
            }
        }
        public bool Aerial
        {
            get
            {
                return aerial;
            }
            set
            {
                aerial = value;
            }
        }
        public bool Hybrid
        {
            get
            {
                return hybrid;
            }
            set
            {
                hybrid = value;
            }
        }
        public bool Debug
        {
            get
            {
                return debug;
            }
            set
            {
                debug = value;
            }
        }
        public static VESettings LoadSettingsFromFile(string filename)
        {
            VESettings settings;
            XmlSerializer xs = new XmlSerializer(typeof(VESettings));
            if (File.Exists(filename))
            {
                FileStream fs = null;
                try
                {
                    fs = File.Open(filename, FileMode.Open, FileAccess.Read);
                }
                catch
                {
                    return new VESettings();
                }
                try
                {
                    settings = (VESettings)xs.Deserialize(fs);
                }
                catch
                {
                    settings = new VESettings();
                }
                finally
                {
                    fs.Close();
                }
            }
            else
            {
                settings = new VESettings();
            }
            return settings;
        }
        public static void SaveSettingsToFile(string file, VESettings settings)
        {
            FileStream fs = null;
            XmlSerializer xs = new XmlSerializer(typeof(VESettings));
            fs = File.Open(file, FileMode.Create, FileAccess.Write);
            try
            {
                xs.Serialize(fs, settings);
            }
            finally
            {
                fs.Close();
            }
        }
    }
    public class VeTile : IDisposable
    {
        UV m_ul, m_ur, m_ll, m_lr;
        public UV UL
        {
            get { return m_ul; }
            set { m_ul = value; }
        }
        public UV UR
        {
            get { return m_ur; }
            set { m_ur = value; }
        }
        public UV LL
        {
            get { return m_ll; }
            set { m_ll = value; }
        }
        public UV LR
        {
            get { return m_lr; }
            set { m_lr = value; }
        }
        private float vertEx;
        public float VertEx
        {
            get { return vertEx; }
        }
        private static Projection _proj;
        private static double _layerRadius;
        private static TerrainAccessor _terrainAccessor;
        private static System.Drawing.Font _font;
        private static Brush _brush;
        private static VirtualEarthForm _veForm;
        public static void Init(Projection proj, TerrainAccessor terrainAccessor, double layerRadius, VirtualEarthForm veForm)
        {
            _proj = proj;
            _terrainAccessor = terrainAccessor;
            _layerRadius = layerRadius;
            _veForm = veForm;
            _font = new System.Drawing.Font("Verdana", 15, FontStyle.Bold);
            _brush = new SolidBrush(Color.Green);
        }
        private bool isNeeded = true;
        public bool IsNeeded
        {
            get { return isNeeded; }
            set { isNeeded = value; }
        }
        public bool IsEqual(int row, int col, int level)
        {
            bool retVal = false;
            if (this.row == row && this.col == col && this.level == level)
            {
                retVal = true;
            }
            return retVal;
        }
        private int row;
        private int col;
        private int level;
        public VeTile(int row, int col, int level)
        {
            this.row = row;
            this.col = col;
            this.level = level;
        }
        private Texture texture = null;
        public Texture Texture
        {
            get { return texture; }
            set { texture = value; }
        }
        private ArrayList alMetaData = new ArrayList();
        private WebDownload download;
        public float ProgressPercent;
        private string textureName;
        private DrawArgs drawArgs;
        public void GetTexture(DrawArgs drawArgs, int pixelsPerTile)
        {
            this.drawArgs = drawArgs;
            string _datasetName = _veForm.GetDataSetName();
            string _imageExtension = _veForm.GetImageExtension();
            string _serverUri = ".ortho.tiles.virtualearth.net/tiles/";
            string quadKey = TileToQuadKey(col, row, level);
            string textureUrl = String.Concat(new object[] { "http://", _datasetName, quadKey[quadKey.Length - 1], _serverUri, _datasetName, quadKey, ".", _imageExtension, "?g=", 15 });
            if (_veForm.IsDebug == true)
            {
                MemoryStream ms;
                Bitmap b = new Bitmap(pixelsPerTile, pixelsPerTile);
                System.Drawing.Imaging.ImageFormat imageFormat;
                alMetaData.Add("ww rowXcol : " + row.ToString() + "x" + col.ToString());
                alMetaData.Add("veLevel : " + level.ToString());
                alMetaData.Add("quadKey " + quadKey.ToString());
                imageFormat = System.Drawing.Imaging.ImageFormat.Jpeg;
                b = DecorateBitmap(b, _font, _brush, alMetaData);
                ms = new MemoryStream();
                b.Save(ms, imageFormat);
                ms.Position = 0;
                this.texture = TextureLoader.FromStream(drawArgs.device, ms);
                ms.Close();
                ms = null;
                b.Dispose();
                b = null;
            }
            else
            {
                string levelDir = CreateLevelDir(level, _veForm.WorldWindow.Cache.CacheDirectory);
                string mapTypeDir = CreateMapTypeDir(levelDir, _datasetName);
                string rowDir = CreateRowDir(mapTypeDir, row);
                textureName = String.Empty;
                if (_datasetName == "r")
                {
                    textureName = GetTextureName(rowDir, row, col, "png");
                }
                else
                {
                    textureName = GetTextureName(rowDir, row, col, "jpeg");
                }
                if (File.Exists(textureName) == true)
                {
                    this.texture = TextureLoader.FromFile(drawArgs.device, textureName);
                }
                else
                {
                    download = new WebDownload(textureUrl);
                    download.DownloadType = DownloadType.Unspecified;
                    download.SavedFilePath = textureName + ".tmp";
                    download.ProgressCallback += new DownloadProgressHandler(UpdateProgress);
                    download.CompleteCallback += new DownloadCompleteHandler(DownloadComplete);
                    download.BackgroundDownloadFile();
                }
            }
        }
        void UpdateProgress(int pos, int total)
        {
            if (total == 0)
            {
                total = 50 * 1024;
            }
            pos = pos % (total + 1);
            ProgressPercent = (float)pos / total;
        }
        private void DownloadComplete(WebDownload downloadInfo)
        {
            try
            {
                downloadInfo.Verify();
                File.Delete(textureName);
                File.Move(downloadInfo.SavedFilePath, textureName);
                this.texture = TextureLoader.FromFile(drawArgs.device, textureName);
            }
            catch (System.Net.WebException caught)
            {
                System.Net.HttpWebResponse response = caught.Response as System.Net.HttpWebResponse;
                if (response != null && response.StatusCode == System.Net.HttpStatusCode.NotFound)
                {
                    using (File.Create(textureName + ".txt"))
                    { }
                    return;
                }
            }
            catch
            {
                using (File.Create(textureName + ".txt"))
                { }
                if (File.Exists(downloadInfo.SavedFilePath))
                    File.Delete(downloadInfo.SavedFilePath);
            }
            finally
            {
                download.IsComplete = true;
            }
        }
        public void AddMetaData(string metadata)
        {
            alMetaData.Add(metadata);
        }
        public Bitmap DecorateBitmap(Bitmap b, System.Drawing.Font font, Brush brush, ArrayList alMetadata)
        {
            if (alMetadata.Count > 0)
            {
                if (b.PixelFormat == System.Drawing.Imaging.PixelFormat.Format8bppIndexed)
                {
                    MemoryStream ms = new MemoryStream();
                    b.Save(ms, System.Drawing.Imaging.ImageFormat.Jpeg);
                    b.Dispose();
                    b = null;
                    b = new Bitmap(256, 256);
                    b = (Bitmap)Bitmap.FromStream(ms);
                    ms.Close();
                    ms = null;
                }
                Graphics g = Graphics.FromImage(b);
                g.Clear(Color.White);
                g.DrawLine(Pens.Red, 0, 0, b.Width, 0);
                g.DrawLine(Pens.Red, 0, 0, 0, b.Height);
                string s = (string)alMetadata[0];
                SizeF sizeF = g.MeasureString(s, font);
                for (int i = 0; i < alMetadata.Count; i++)
                {
                    s = (string)alMetadata[i];
                    int x = 0;
                    int y = (int)(sizeF.Height * (i + 0));
                    g.DrawString(s, font, brush, x, y);
                }
                g.Dispose();
            }
            return b;
        }
        private static string TileToQuadKey(int tx, int ty, int zl)
        {
            string quad;
            quad = "";
            for (int i = zl; i > 0; i--)
            {
                int mask = 1 << (i - 1);
                int cell = 0;
                if ((tx & mask) != 0)
                {
                    cell++;
                }
                if ((ty & mask) != 0)
                {
                    cell += 2;
                }
                quad += cell;
            }
            return quad;
        }
        public string CreateLevelDir(int level, string cacheDirectoryRoot)
        {
            string levelDir = null;
            string cacheDirectory = String.Format("{0}\\Virtual Earth", cacheDirectoryRoot);
            if (Directory.Exists(cacheDirectory) == false)
            {
                Directory.CreateDirectory(cacheDirectory);
            }
            levelDir = cacheDirectory + @"\" + level.ToString();
            if (Directory.Exists(levelDir) == false)
            {
                Directory.CreateDirectory(levelDir);
            }
            return levelDir;
        }
        public string CreateMapTypeDir(string levelDir, string mapType)
        {
            string mapTypeDir = levelDir + @"\" + mapType;
            if (Directory.Exists(mapTypeDir) == false)
            {
                Directory.CreateDirectory(mapTypeDir);
            }
            return mapTypeDir;
        }
        public string CreateRowDir(string mapTypeDir, int row)
        {
            string rowDir = mapTypeDir + @"\" + row.ToString("0000");
            if (Directory.Exists(rowDir) == false)
            {
                Directory.CreateDirectory(rowDir);
            }
            return rowDir;
        }
        public string GetTextureName(string rowDir, int row, int col, string textureExtension)
        {
            string textureName = rowDir + @"\" + row.ToString("0000") + "_" + col.ToString("0000") + "." + textureExtension;
            return textureName;
        }
        public void SaveBitmap(Bitmap b, string rowDir, int row, int col, string imageExtension, System.Drawing.Imaging.ImageFormat format)
        {
            string bmpName = rowDir + @"\" + row.ToString("0000") + "_" + col.ToString("0000") + "." + imageExtension;
            b.Save(bmpName, format);
        }
        public void Reproject()
        {
        }
        protected CustomVertex.PositionColoredTextured[] vertices;
        public CustomVertex.PositionColoredTextured[] Vertices
        {
            get { return vertices; }
        }
        protected short[] indices;
        public short[] Indices
        {
            get { return indices; }
        }
        protected int meshPointCount = 64;
        private double North;
        private double South;
        private double West;
        private double East;
        public void CreateMesh(byte opacity, float verticalExaggeration)
        {
            this.vertEx = verticalExaggeration;
            int opacityColor = System.Drawing.Color.FromArgb(opacity, 0, 0, 0).ToArgb();
            meshPointCount = 32;
            vertices = new CustomVertex.PositionColoredTextured[meshPointCount * meshPointCount];
            int upperBound = meshPointCount - 1;
            float scaleFactor = (float)1 / upperBound;
            double uStep = (UR.U - UL.U) / upperBound;
            double vStep = (UL.V - LL.V) / upperBound;
            UV curUnprojected = new UV(UL.U, UL.V);
            UV geoUL = _proj.Inverse(m_ul);
            UV geoLR = _proj.Inverse(m_lr);
            double latRange = (geoUL.U - geoLR.U) * 180 / Math.PI;
            North = geoUL.V * 180 / Math.PI;
            South = geoLR.V * 180 / Math.PI;
            West = geoUL.U * 180 / Math.PI;
            East = geoLR.U * 180 / Math.PI;
            float meshBaseRadius = (float)_layerRadius;
            float[,] heightData = null;
            if (_terrainAccessor != null && _veForm.IsTerrainOn == true)
            {
                TerrainTile tile = _terrainAccessor.GetElevationArray(North, South, West, East, meshPointCount);
                heightData = tile.ElevationData;
                tile.Dispose();
                tile = null;
            }
            UV geo;
            Vector3 pos;
            double height = 0;
            for (int i = 0; i < meshPointCount; i++)
            {
                for (int j = 0; j < meshPointCount; j++)
                {
                    geo = _proj.Inverse(curUnprojected);
                    geo.U *= 180 / Math.PI;
                    geo.V *= 180 / Math.PI;
                    if (_terrainAccessor != null)
                    {
                        if (_veForm.IsTerrainOn == true)
                        {
                            height = heightData[i, j] * verticalExaggeration;
                        }
                        else
                        {
                            height = verticalExaggeration * _terrainAccessor.GetElevationAt(geo.V, geo.U, upperBound / latRange);
                        }
                    }
                    pos = MathEngine.SphericalToCartesian(
                        geo.V,
                        geo.U,
                        _layerRadius + height);
                    vertices[i * meshPointCount + j].X = pos.X;
                    vertices[i * meshPointCount + j].Y = pos.Y;
                    vertices[i * meshPointCount + j].Z = pos.Z;
                    vertices[i * meshPointCount + j].Tu = j * scaleFactor;
                    vertices[i * meshPointCount + j].Tv = i * scaleFactor;
                    vertices[i * meshPointCount + j].Color = opacityColor;
                    curUnprojected.U += uStep;
                }
                curUnprojected.U = UL.U;
                curUnprojected.V -= vStep;
            }
            indices = new short[2 * upperBound * upperBound * 3];
            for (int i = 0; i < upperBound; i++)
            {
                for (int j = 0; j < upperBound; j++)
                {
                    indices[(2 * 3 * i * upperBound) + 6 * j] = (short)(i * meshPointCount + j);
                    indices[(2 * 3 * i * upperBound) + 6 * j + 1] = (short)((i + 1) * meshPointCount + j);
                    indices[(2 * 3 * i * upperBound) + 6 * j + 2] = (short)(i * meshPointCount + j + 1);
                    indices[(2 * 3 * i * upperBound) + 6 * j + 3] = (short)(i * meshPointCount + j + 1);
                    indices[(2 * 3 * i * upperBound) + 6 * j + 4] = (short)((i + 1) * meshPointCount + j);
                    indices[(2 * 3 * i * upperBound) + 6 * j + 5] = (short)((i + 1) * meshPointCount + j + 1);
                }
            }
        }
        public void Dispose()
        {
            if (texture != null)
            {
                texture.Dispose();
                texture = null;
            }
            if (download != null)
            {
                download.Dispose();
                download = null;
            }
            if (vertices != null)
            {
                vertices = null;
            }
            if (indices != null)
            {
                indices = null;
            }
            if (downloadRectangle != null)
            {
                downloadRectangle = null;
            }
            GC.SuppressFinalize(this);
        }
        CustomVertex.PositionColored[] downloadRectangle = new CustomVertex.PositionColored[5];
        public static void Render(DrawArgs drawArgs, bool disableZbuffer, ArrayList alVeTiles)
        {
            try
            {
                if (alVeTiles.Count <= 0)
                    return;
                lock (alVeTiles.SyncRoot)
                {
                    if (disableZbuffer)
                    {
                        if (drawArgs.device.RenderState.ZBufferEnable)
                            drawArgs.device.RenderState.ZBufferEnable = false;
                    }
                    else
                    {
                        if (!drawArgs.device.RenderState.ZBufferEnable)
                            drawArgs.device.RenderState.ZBufferEnable = true;
                    }
                    drawArgs.device.VertexFormat = CustomVertex.PositionColoredTextured.Format;
                    drawArgs.device.TextureState[0].AlphaOperation = TextureOperation.Modulate;
                    drawArgs.device.TextureState[0].ColorOperation = TextureOperation.Add;
                    drawArgs.device.TextureState[0].AlphaArgument1 = TextureArgument.TextureColor;
                    int notDownloadedIter = 0;
                    int[] notDownloaded = new int[alVeTiles.Count];
                    VeTile veTile;
                    for (int i = 0; i < alVeTiles.Count; i++)
                    {
                        veTile = (VeTile)alVeTiles[i];
                        if (veTile.Texture == null)
                        {
                            notDownloaded[notDownloadedIter] = i;
                            notDownloadedIter++;
                            continue;
                        }
                        else
                        {
                            drawArgs.device.Clear(ClearFlags.ZBuffer, 0, 1.0f, 0);
                            drawArgs.device.SetTexture(0, veTile.Texture);
                            drawArgs.device.DrawIndexedUserPrimitives(PrimitiveType.TriangleList, 0,
                                veTile.Vertices.Length, veTile.Indices.Length / 3, veTile.Indices, true, veTile.Vertices);
                        }
                    }
                    drawArgs.device.RenderState.ZBufferEnable = false;
                    drawArgs.device.VertexFormat = CustomVertex.PositionColored.Format;
                    drawArgs.device.TextureState[0].ColorOperation = TextureOperation.Disable;
                    int tileIndex;
                    for (int i = 0; i < notDownloadedIter; i++)
                    {
                        tileIndex = notDownloaded[i];
                        veTile = (VeTile)alVeTiles[tileIndex];
                        veTile.RenderDownloadRectangle(drawArgs);
                    }
                    drawArgs.device.TextureState[0].ColorOperation = TextureOperation.SelectArg1;
                    drawArgs.device.VertexFormat = CustomVertex.PositionTextured.Format;
                    drawArgs.device.RenderState.ZBufferEnable = true;
                }
            }
            catch (Exception ex)
            {
                string sex = ex.ToString();
                Utility.Log.Write(ex);
            }
            finally
            {
                if (disableZbuffer)
                    drawArgs.device.RenderState.ZBufferEnable = true;
            }
        }
        public void CreateDownloadRectangle(DrawArgs drawArgs, int color)
        {
            Vector3 northWestV = MathEngine.SphericalToCartesian((float)North, (float)West, _layerRadius);
            Vector3 southWestV = MathEngine.SphericalToCartesian((float)South, (float)West, _layerRadius);
            Vector3 northEastV = MathEngine.SphericalToCartesian((float)North, (float)East, _layerRadius);
            Vector3 southEastV = MathEngine.SphericalToCartesian((float)South, (float)East, _layerRadius);
            downloadRectangle[0].X = northWestV.X;
            downloadRectangle[0].Y = northWestV.Y;
            downloadRectangle[0].Z = northWestV.Z;
            downloadRectangle[0].Color = color;
            downloadRectangle[1].X = southWestV.X;
            downloadRectangle[1].Y = southWestV.Y;
            downloadRectangle[1].Z = southWestV.Z;
            downloadRectangle[1].Color = color;
            downloadRectangle[2].X = southEastV.X;
            downloadRectangle[2].Y = southEastV.Y;
            downloadRectangle[2].Z = southEastV.Z;
            downloadRectangle[2].Color = color;
            downloadRectangle[3].X = northEastV.X;
            downloadRectangle[3].Y = northEastV.Y;
            downloadRectangle[3].Z = northEastV.Z;
            downloadRectangle[3].Color = color;
            downloadRectangle[4].X = downloadRectangle[0].X;
            downloadRectangle[4].Y = downloadRectangle[0].Y;
            downloadRectangle[4].Z = downloadRectangle[0].Z;
            downloadRectangle[4].Color = color;
        }
        public void RenderDownloadRectangle(DrawArgs drawArgs)
        {
            drawArgs.device.Transform.World = Matrix.Translation(
                   (float)-drawArgs.WorldCamera.ReferenceCenter.X,
                (float)-drawArgs.WorldCamera.ReferenceCenter.Y,
                (float)-drawArgs.WorldCamera.ReferenceCenter.Z
            );
            drawArgs.device.DrawUserPrimitives(PrimitiveType.LineStrip, 4, downloadRectangle);
            drawArgs.device.Transform.World = drawArgs.WorldCamera.WorldMatrix;
        }
    }
    public class Search
    {
        private Search()
        {
        }
        private static string DoSearchRequest(string searchParams)
        {
            string text1 = string.Empty;
            HttpWebRequest request1 = (HttpWebRequest)WebRequest.Create("http:");
            request1.Method = "POST";
            request1.ContentType = "application/x-www-form-urlencoded";
            UTF8Encoding encoding1 = new UTF8Encoding();
            byte[] buffer1 = encoding1.GetBytes(searchParams);
            request1.ContentLength = buffer1.Length;
            try
            {
                Stream stream1 = request1.GetRequestStream();
                stream1.Write(buffer1, 0, buffer1.Length);
                stream1.Close();
                stream1 = null;
                text1 = Search.GetSearchResults(request1);
            }
            catch (WebException)
            {
            }
            return text1;
        }
        internal static string GetSearchResults(HttpWebRequest searchRequest)
        {
            string text1 = string.Empty;
            HttpWebResponse response1 = (HttpWebResponse)searchRequest.GetResponse();
            Cursor.Current = Cursors.WaitCursor;
            Stream stream1 = response1.GetResponseStream();
            Cursor.Current = Cursors.Default;
            Encoding encoding1 = Encoding.GetEncoding("utf-8");
            StreamReader reader1 = new StreamReader(stream1, encoding1);
            char[] chArray1 = new char[0x100];
            for (int num1 = reader1.Read(chArray1, 0, 0x100); num1 > 0; num1 = reader1.Read(chArray1, 0, 0x100))
            {
                string text2 = new string(chArray1, 0, num1);
                text1 = text1 + text2;
            }
            reader1.Close();
            reader1 = null;
            response1.Close();
            response1 = null;
            return text1;
        }
        public static bool SearchForAddress(string address, out double lat1, out double long1, out double lat2, out double long2)
        {
            double num1;
            long2 = num1 = 0;
            long1 = num1 = num1;
            lat2 = num1 = num1;
            lat1 = num1;
            string text1 = "a=&b=" + address + "&c=0.0&d=0.0&e=0.0&f=0.0&g=&i=&r=0";
            string text2 = Search.DoSearchRequest(text1);
            if ((text2 == null) || (text2 == string.Empty))
            {
                return false;
            }
            Regex regex1 = new Regex(@"SetViewport\((?<lat1>\S+),(?<long1>\S+),(?<lat2>\S+),(?<long2>\S+)\)");
            Match match1 = regex1.Match(text2);
            if (!match1.Success)
            {
                return false;
            }
            lat1 = double.Parse(match1.Groups["lat1"].Value);
            long1 = double.Parse(match1.Groups["long1"].Value);
            lat2 = double.Parse(match1.Groups["lat2"].Value);
            long2 = double.Parse(match1.Groups["long2"].Value);
            return true;
        }
        public static ArrayList SearchForBusiness(string business, double lat1, double lon1, double lat2, double lon2)
        {
            int num1 = 0;
            ArrayList list1 = new ArrayList();
            while (true)
            {
                bool flag1 = false;
                string text2 = string.Concat(new object[] { "a=", business.Trim(), "&b=&c=", lat1, "&d=", lon1, "&e=", lat2, "&f=", lon2, "&g=", num1, "&i=0&r=false" });
                string text1 = Search.DoSearchRequest(text2);
                if ((text1 != null) && (text1 != string.Empty))
                {
                    Regex regex1 = new Regex(@"VE_SearchResult\((?<id>[0-9]*),'(?<name>[^']*)','(?<address>[^']*)','(?<phone>[^']*)',(?<rating>[^,]*),'(?<type>[^']*)',(?<latitude>[^,]*),(?<longitude>[^,)]*)\)");
                    MatchCollection collection1 = regex1.Matches(text1);
                    foreach (Match match1 in collection1)
                    {
                        PushPin pushPin = new PushPin();
                        pushPin.Name = match1.Groups["name"].Value;
                        pushPin.Address = match1.Groups["address"].Value;
                        pushPin.Phone = match1.Groups["phone"].Value;
                        pushPin.Latitude = double.Parse(match1.Groups["latitude"].Value);
                        pushPin.Longitude = double.Parse(match1.Groups["longitude"].Value);
                        list1.Add(pushPin);
                    }
                    num1 += 10;
                    regex1 = new Regex(@"true,''\);$");
                    if (regex1.IsMatch(text1))
                    {
                        flag1 = true;
                    }
                }
                if (!flag1 || (list1.Count >= 50))
                {
                    return list1;
                }
            }
        }
    }
    public class PushPin
    {
        public string Name;
        public string Address;
        public string Phone;
        public double Latitude;
        public double Longitude;
    }
    [StructLayout(LayoutKind.Sequential)]
    public struct UV
    {
        public double U;
        public double V;
        public UV(double u, double v)
        {
            this.U = u;
            this.V = v;
        }
    }
    public class Projection : IDisposable
    {
        IntPtr projPJ;
        [DllImport("proj.dll")]
        static extern IntPtr pj_init(int argc, string[] args);
        [DllImport("proj.dll")]
        static extern string pj_free(IntPtr projPJ);
        [DllImport("proj.dll")]
        static extern UV pj_fwd(UV uv, IntPtr projPJ);
        [DllImport("proj.dll")]
        static extern UV pj_inv(UV uv, IntPtr projPJ);
        public Projection(string[] initParameters)
        {
            projPJ = pj_init(initParameters.Length, initParameters);
            if (projPJ == IntPtr.Zero)
                throw new ApplicationException("Projection initialization failed.");
        }
        public UV Forward(UV uv)
        {
            return pj_fwd(uv, projPJ);
        }
        public UV Inverse(UV uv)
        {
            return pj_inv(uv, projPJ);
        }
        public void Dispose()
        {
            if (projPJ != IntPtr.Zero)
            {
                pj_free(projPJ);
                projPJ = IntPtr.Zero;
            }
        }
    }
}
