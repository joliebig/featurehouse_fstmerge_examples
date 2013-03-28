using System.Collections.Specialized;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.Net;
using System.Threading;
using System.Windows.Forms;
using System.Xml;
using System.Text.RegularExpressions;
using System;
using WorldWind.Net;
using WorldWind;
using WorldWind.Renderable;
using WorldWind.VisualControl;
using Utility;
using CarbonTools.Core.Base;
using CarbonTools.Core.OGCCapabilities;
namespace WorldWind
{
    public class WMSBrowserNG : System.Windows.Forms.Form
    {
        string wms_config_filepath = Path.Combine(
            Path.Combine(MainApplication.Settings.ConfigPath, "Earth"),
            Path.Combine("Tools", "wms_server_list.xml"));
        string wms_skeleton_path = Path.Combine(
            Path.Combine(MainApplication.Settings.ConfigPath, "Earth"),
            Path.Combine("Tools", "wmsskeleton.xml"));
        string saved_dirpath = Path.Combine(MainApplication.DirectoryPath,
            Path.Combine(MainApplication.Settings.CachePath, "WMS Browser"));
        private WorldWindow worldWindow;
        private System.Drawing.Point mouseLocationProgressBarAnimation = new Point(0, 0);
        private System.Timers.Timer animationTimer = new System.Timers.Timer();
        private System.Collections.ArrayList animationFrames = new ArrayList();
        private System.Collections.Queue downloadQueue = new Queue();
        private System.Windows.Forms.Panel panelContents;
        private System.Windows.Forms.Splitter splitter1;
        private System.Windows.Forms.ContextMenu contextMenuLegendUrl;
        private System.Windows.Forms.ProgressBar progressBarStatus;
        private System.Windows.Forms.Label statusBarLabel;
        private System.Windows.Forms.Panel panelStatus;
        private System.Windows.Forms.Panel panelLower;
        private Label ConfLabel;
        private Label addwmslabel;
        private TextBox wmsUrltextBox;
        private Button gettreebutton;
        private Button wmsbutton;
        private Label label4;
        private TextBox wmsGetCapstextbox;
        private Label label1;
        private TextBox textBox2;
        private Button savewmsbutton;
        private ComboBox comboBox2;
        private Label label5;
        private CarbonTools.Controls.TreeViewOGCCapabilities treeOgcCaps;
        private PictureBox layerpreviewBox;
        public WMSBrowserNG(WorldWindow ww)
        {
            InitializeComponent();
            this.worldWindow = ww;
        }
        private void InitializeComponent()
        {
            this.progressBarStatus = new System.Windows.Forms.ProgressBar();
            this.contextMenuLegendUrl = new System.Windows.Forms.ContextMenu();
            this.panelContents = new System.Windows.Forms.Panel();
            this.treeOgcCaps = new CarbonTools.Controls.TreeViewOGCCapabilities();
            this.ConfLabel = new System.Windows.Forms.Label();
            this.splitter1 = new System.Windows.Forms.Splitter();
            this.statusBarLabel = new System.Windows.Forms.Label();
            this.panelStatus = new System.Windows.Forms.Panel();
            this.panelLower = new System.Windows.Forms.Panel();
            this.layerpreviewBox = new System.Windows.Forms.PictureBox();
            this.label5 = new System.Windows.Forms.Label();
            this.comboBox2 = new System.Windows.Forms.ComboBox();
            this.savewmsbutton = new System.Windows.Forms.Button();
            this.wmsbutton = new System.Windows.Forms.Button();
            this.label4 = new System.Windows.Forms.Label();
            this.gettreebutton = new System.Windows.Forms.Button();
            this.wmsGetCapstextbox = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.textBox2 = new System.Windows.Forms.TextBox();
            this.addwmslabel = new System.Windows.Forms.Label();
            this.wmsUrltextBox = new System.Windows.Forms.TextBox();
            this.panelContents.SuspendLayout();
            this.panelStatus.SuspendLayout();
            this.panelLower.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.layerpreviewBox)).BeginInit();
            this.SuspendLayout();
            this.progressBarStatus.Dock = System.Windows.Forms.DockStyle.Right;
            this.progressBarStatus.Location = new System.Drawing.Point(376, 0);
            this.progressBarStatus.Name = "progressBarStatus";
            this.progressBarStatus.Size = new System.Drawing.Size(136, 30);
            this.progressBarStatus.Step = 1;
            this.progressBarStatus.TabIndex = 1;
            this.panelContents.Controls.Add(this.treeOgcCaps);
            this.panelContents.Controls.Add(this.ConfLabel);
            this.panelContents.Controls.Add(this.splitter1);
            this.panelContents.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panelContents.Location = new System.Drawing.Point(0, 0);
            this.panelContents.Name = "panelContents";
            this.panelContents.Size = new System.Drawing.Size(512, 230);
            this.panelContents.TabIndex = 4;
            this.treeOgcCaps.Credentials = null;
            this.treeOgcCaps.Location = new System.Drawing.Point(0, 21);
            this.treeOgcCaps.Name = "treeOgcCaps";
            this.treeOgcCaps.Proxy = null;
            this.treeOgcCaps.Size = new System.Drawing.Size(512, 206);
            this.treeOgcCaps.TabIndex = 5;
            this.treeOgcCaps.URL = "";
            this.ConfLabel.AutoSize = true;
            this.ConfLabel.Location = new System.Drawing.Point(8, 4);
            this.ConfLabel.Name = "ConfLabel";
            this.ConfLabel.Size = new System.Drawing.Size(97, 13);
            this.ConfLabel.TabIndex = 4;
            this.ConfLabel.Text = "Configured Servers";
            this.splitter1.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.splitter1.Location = new System.Drawing.Point(0, 227);
            this.splitter1.Name = "splitter1";
            this.splitter1.Size = new System.Drawing.Size(512, 3);
            this.splitter1.TabIndex = 2;
            this.splitter1.TabStop = false;
            this.statusBarLabel.Dock = System.Windows.Forms.DockStyle.Fill;
            this.statusBarLabel.Location = new System.Drawing.Point(0, 0);
            this.statusBarLabel.Name = "statusBarLabel";
            this.statusBarLabel.Size = new System.Drawing.Size(512, 30);
            this.statusBarLabel.TabIndex = 0;
            this.statusBarLabel.TextAlign = System.Drawing.ContentAlignment.BottomLeft;
            this.panelStatus.Controls.Add(this.progressBarStatus);
            this.panelStatus.Controls.Add(this.statusBarLabel);
            this.panelStatus.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.panelStatus.Location = new System.Drawing.Point(0, 454);
            this.panelStatus.Name = "panelStatus";
            this.panelStatus.Size = new System.Drawing.Size(512, 30);
            this.panelStatus.TabIndex = 8;
            this.panelLower.Controls.Add(this.layerpreviewBox);
            this.panelLower.Controls.Add(this.label5);
            this.panelLower.Controls.Add(this.comboBox2);
            this.panelLower.Controls.Add(this.savewmsbutton);
            this.panelLower.Controls.Add(this.wmsbutton);
            this.panelLower.Controls.Add(this.label4);
            this.panelLower.Controls.Add(this.gettreebutton);
            this.panelLower.Controls.Add(this.wmsGetCapstextbox);
            this.panelLower.Controls.Add(this.label1);
            this.panelLower.Controls.Add(this.textBox2);
            this.panelLower.Controls.Add(this.addwmslabel);
            this.panelLower.Controls.Add(this.wmsUrltextBox);
            this.panelLower.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.panelLower.Location = new System.Drawing.Point(0, 230);
            this.panelLower.Name = "panelLower";
            this.panelLower.Size = new System.Drawing.Size(512, 224);
            this.panelLower.TabIndex = 0;
            this.layerpreviewBox.Location = new System.Drawing.Point(290, 105);
            this.layerpreviewBox.Name = "layerpreviewBox";
            this.layerpreviewBox.Size = new System.Drawing.Size(210, 113);
            this.layerpreviewBox.TabIndex = 8;
            this.layerpreviewBox.TabStop = false;
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(287, 67);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(71, 13);
            this.label5.TabIndex = 7;
            this.label5.Text = "Image Format";
            this.comboBox2.FormattingEnabled = true;
            this.comboBox2.Location = new System.Drawing.Point(376, 64);
            this.comboBox2.Name = "comboBox2";
            this.comboBox2.Size = new System.Drawing.Size(124, 21);
            this.comboBox2.TabIndex = 6;
            this.comboBox2.SelectedIndexChanged += new System.EventHandler(this.comboBox2_SelectedIndexChanged);
            this.savewmsbutton.Location = new System.Drawing.Point(117, 62);
            this.savewmsbutton.Name = "savewmsbutton";
            this.savewmsbutton.Size = new System.Drawing.Size(109, 27);
            this.savewmsbutton.TabIndex = 5;
            this.savewmsbutton.Text = "Save WMS Server";
            this.savewmsbutton.UseVisualStyleBackColor = true;
            this.savewmsbutton.Click += new System.EventHandler(this.savewmsbutton_Click);
            this.wmsbutton.Location = new System.Drawing.Point(11, 62);
            this.wmsbutton.Name = "wmsbutton";
            this.wmsbutton.Size = new System.Drawing.Size(96, 27);
            this.wmsbutton.TabIndex = 1;
            this.wmsbutton.Text = "Get WMS Tree";
            this.wmsbutton.UseVisualStyleBackColor = true;
            this.wmsbutton.Click += new System.EventHandler(this.button1_Click);
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(10, 8);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(81, 13);
            this.label4.TabIndex = 4;
            this.label4.Text = "Add WMS URL";
            this.gettreebutton.Location = new System.Drawing.Point(10, 62);
            this.gettreebutton.Name = "gettreebutton";
            this.gettreebutton.Size = new System.Drawing.Size(96, 25);
            this.gettreebutton.TabIndex = 2;
            this.gettreebutton.Text = "Get WMS Tree";
            this.gettreebutton.UseVisualStyleBackColor = true;
            this.wmsGetCapstextbox.Location = new System.Drawing.Point(12, 36);
            this.wmsGetCapstextbox.Name = "wmsGetCapstextbox";
            this.wmsGetCapstextbox.Size = new System.Drawing.Size(499, 20);
            this.wmsGetCapstextbox.TabIndex = 0;
            this.wmsGetCapstextbox.Text = "http://";
            this.wmsGetCapstextbox.TextChanged += new System.EventHandler(this.textBox3_TextChanged);
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(8, 8);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(81, 13);
            this.label1.TabIndex = 1;
            this.label1.Text = "Add WMS URL";
            this.textBox2.Location = new System.Drawing.Point(11, 36);
            this.textBox2.Name = "textBox2";
            this.textBox2.Size = new System.Drawing.Size(499, 20);
            this.textBox2.TabIndex = 0;
            this.textBox2.Text = "http://";
            this.addwmslabel.AutoSize = true;
            this.addwmslabel.Location = new System.Drawing.Point(7, 8);
            this.addwmslabel.Name = "addwmslabel";
            this.addwmslabel.Size = new System.Drawing.Size(81, 13);
            this.addwmslabel.TabIndex = 1;
            this.addwmslabel.Text = "Add WMS URL";
            this.wmsUrltextBox.Location = new System.Drawing.Point(10, 36);
            this.wmsUrltextBox.Name = "wmsUrltextBox";
            this.wmsUrltextBox.Size = new System.Drawing.Size(499, 20);
            this.wmsUrltextBox.TabIndex = 0;
            this.wmsUrltextBox.Text = "http://";
            this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
            this.ClientSize = new System.Drawing.Size(512, 484);
            this.Controls.Add(this.panelContents);
            this.Controls.Add(this.panelLower);
            this.Controls.Add(this.panelStatus);
            this.KeyPreview = true;
            this.MinimumSize = new System.Drawing.Size(504, 483);
            this.Name = "WMSBrowserNG";
            this.Text = "Web Mapping Server Browser";
            this.panelContents.ResumeLayout(false);
            this.panelContents.PerformLayout();
            this.panelStatus.ResumeLayout(false);
            this.panelLower.ResumeLayout(false);
            this.panelLower.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.layerpreviewBox)).EndInit();
            this.ResumeLayout(false);
        }
        private void textBox3_TextChanged(object sender, EventArgs e)
        {
            if(wmsGetCapstextbox.Text.StartsWith("http://"))
                Console.WriteLine("WMS GETCAPS URL VALIDATED");
        }
        private void button1_Click(object sender, EventArgs e)
        {
            WorldWind.Net.WebDownload download = new WebDownload(wmsGetCapstextbox.Text);
            string wmscachedir = Path.Combine( worldWindow.Cache.CacheDirectory, "WMS Browser" );
            download.DownloadFile(Path.Combine(wmscachedir,"getcaps.xml"));
            treeOgcCaps.URL = wmsGetCapstextbox.Text;
            treeOgcCaps.GetCapabilities();
        }
        private void comboBox2_SelectedIndexChanged(object sender, EventArgs e)
        {
        }
        private void savewmsbutton_Click(object sender, EventArgs e)
        {
            SourceOGCCapabilities source = new SourceOGCCapabilities();
            source.Address = new Uri(wmsGetCapstextbox.Text);
            source.ServiceType = OGCServiceTypes.WMS;
            HandlerOGCCapabilities handler = new HandlerOGCCapabilities(source);
            handler.GetCapabilities();
            DataOGCCapabilities data = handler.Data as DataOGCCapabilities;
            string wmslayerset = "<LayerSet Name=\"WMS Server\" ShowOnlyOneLayer=\"false\" ShowAtStartup=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"LayerSet.xsd\">\n";
            foreach (LayerItem item in data.LayerItems)
            {
                if (item.Parent != null)
                {
                    string converted = ConvertLayerToWMS(item);
                    wmslayerset += converted;
                }
            }
            wmslayerset += "</LayerSet>";
            Console.WriteLine(wmslayerset);
            XmlDocument doc = new XmlDocument();
            doc.LoadXml(wmslayerset);
            string wmsSave = Path.Combine(
            Path.Combine(MainApplication.Settings.ConfigPath, "Earth"),
            "WMS.xml");
            doc.Save(wmsSave);
        }
        private string ConvertLayerToWMS(LayerItem layer)
        {
            string skeleton;
            string FILE_NAME = this.wms_skeleton_path;
            skeleton = ReadSkeleton(FILE_NAME);
            skeleton = skeleton.Replace(@"$NAME", ConvertToXMLEntities(layer.Title));
            skeleton = skeleton.Replace(@"$NBB", layer.LLBoundingBox.MaxY.ToString());
            skeleton = skeleton.Replace(@"$SBB", layer.LLBoundingBox.MinY.ToString());
            skeleton = skeleton.Replace(@"$EBB", layer.LLBoundingBox.MaxX.ToString());
            skeleton = skeleton.Replace(@"$WBB", layer.LLBoundingBox.MinX.ToString());
            string wmsGetMap = wmsGetCapstextbox.Text.ToLowerInvariant().Replace("request=getcapabilities", "request=getmap");
            skeleton = skeleton.Replace(@"$SERVER", ConvertToXMLEntities(wmsGetMap));
            skeleton = skeleton.Replace(@"$LAYERNAME", layer.Name + @"&amp;TRANSPARENT=TRUE&amp;BGCOLOR=0xFF00FF");
            return skeleton;
        }
        private static string ReadSkeleton(string FILE_NAME)
        {
            string skeleton = null;
            if (!File.Exists(FILE_NAME))
            {
                MessageBox.Show(FILE_NAME + " does not exist");
                return skeleton;
            }
            String input;
            using (StreamReader sr = File.OpenText(FILE_NAME))
            {
                while ((input = sr.ReadLine()) != null)
                {
                    skeleton += input + new string((Char)13, 1);
                }
                sr.Close();
            }
            return skeleton;
        }
        private string ConvertToXMLEntities(string text)
        {
            text = Regex.Replace(text, "&", "&amp;");
            text = Regex.Replace(text, "<", "&lt;");
            text = Regex.Replace(text, ">", "&gt;");
            text = Regex.Replace(text, "\"", "&quot;");
            return text;
        }
    }
}
