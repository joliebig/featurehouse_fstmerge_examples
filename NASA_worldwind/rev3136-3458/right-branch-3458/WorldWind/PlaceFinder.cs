using System;
using System.Diagnostics;
using System.Text;
using System.IO;
using System.Net;
using System.Xml;
using System.Xml.XPath;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Globalization;
using System.Web;
using WorldWind.Renderable;
using WorldWind.Net;
namespace WorldWind
{
 public class PlaceFinder : System.Windows.Forms.Form
 {
  private System.Windows.Forms.GroupBox groupBoxResults;
  private System.Windows.Forms.GroupBox groupBoxYahooSimple;
  private System.Windows.Forms.GroupBox groupBoxYahooDetailed;
  private System.Windows.Forms.GroupBox groupBoxWWSearch;
  private System.Windows.Forms.TextBox textBoxYahooSimple;
  private System.Windows.Forms.TextBox textBoxYahooStreet;
  private System.Windows.Forms.TextBox textBoxYahooCity;
  private System.Windows.Forms.TextBox textBoxYahooState;
  private System.Windows.Forms.TextBox textBoxYahooZip;
  private System.Windows.Forms.Label label1;
  private System.Windows.Forms.Button buttonYahooSimpleSearch;
  private System.Windows.Forms.Button buttonYahooDetailedSearch;
  private System.Windows.Forms.Button buttonWWSearch;
  private System.Windows.Forms.TextBox textBoxLatitude;
  private System.Windows.Forms.TextBox textBoxLongitude;
  private System.Windows.Forms.Label label2;
  private System.Windows.Forms.Label label3;
  private System.Windows.Forms.Button buttonGo;
  private System.Windows.Forms.ListView listViewResults;
  private System.Windows.Forms.StatusBar statusBar;
  const string YahooUri = "http://api.local.yahoo.com/MapsService/V1/geocode";
  const string TerraUri = "http://www.apogee.com.au/geocode/terrapagegeocode.php";
  const string YahooAppId = "nasaworldwind";
  WorldWind.WorldWindow m_WorldWindow = null;
  private EnterTextBox textBoxWWFeature;
  private System.Windows.Forms.TabControl tabControl1;
  private System.Windows.Forms.ComboBox comboBox1;
  private System.Windows.Forms.Label label4;
  private System.Windows.Forms.NumericUpDown numericUpDownAltitude;
  private System.Windows.Forms.GroupBox groupBox1;
  private System.Windows.Forms.Button terraSearch;
  private System.Windows.Forms.TextBox terraPost;
  private System.Windows.Forms.TextBox terraState;
  private System.Windows.Forms.TextBox terraSub;
  private System.Windows.Forms.TextBox terraSt;
  private System.Windows.Forms.TextBox terraStNo;
  private System.Windows.Forms.TabPage tabPageYahoo;
  private System.Windows.Forms.TabPage tabPageTerraPage;
  private System.Windows.Forms.TabPage tabPageWWFeature;
  private System.Windows.Forms.PictureBox pictureBox1;
  private System.Windows.Forms.PictureBox pictureBoxYahoo;
  private Label label7;
  private Label label6;
  private Label label5;
  private PictureBox pictureBoxWW;
  private System.ComponentModel.Container components = null;
  public PlaceFinder(WorldWind.WorldWindow ww)
  {
   InitializeComponent();
   try
   {
    Image yahooImage = Image.FromFile(
     Path.Combine(Path.GetDirectoryName(Application.ExecutablePath), "Plugins\\PlaceFinder\\yahoowebservices.gif"));
    pictureBoxYahoo.Size = new Size(yahooImage.Width, yahooImage.Height);
    pictureBoxYahoo.Image = yahooImage;
    Image wwImage = Image.FromFile(
     Path.Combine(Path.GetDirectoryName(Application.ExecutablePath), "Plugins\\PlaceFinder\\fef2.png"));
    pictureBoxWW.Size = new Size(wwImage.Width, wwImage.Height);
    pictureBoxWW.Image = wwImage;
   }
   catch{}
   m_WorldWindow = ww;
  }
  protected override void Dispose( bool disposing )
  {
   if( disposing )
   {
    if(components != null)
    {
     components.Dispose();
    }
   }
   base.Dispose( disposing );
  }
  private void InitializeComponent()
  {
   System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(PlaceFinder));
   this.listViewResults = new System.Windows.Forms.ListView();
   this.groupBoxResults = new System.Windows.Forms.GroupBox();
   this.numericUpDownAltitude = new System.Windows.Forms.NumericUpDown();
   this.label4 = new System.Windows.Forms.Label();
   this.buttonGo = new System.Windows.Forms.Button();
   this.label3 = new System.Windows.Forms.Label();
   this.label2 = new System.Windows.Forms.Label();
   this.textBoxLongitude = new System.Windows.Forms.TextBox();
   this.textBoxLatitude = new System.Windows.Forms.TextBox();
   this.comboBox1 = new System.Windows.Forms.ComboBox();
   this.groupBoxYahooSimple = new System.Windows.Forms.GroupBox();
   this.buttonYahooSimpleSearch = new System.Windows.Forms.Button();
   this.textBoxYahooSimple = new System.Windows.Forms.TextBox();
   this.groupBoxYahooDetailed = new System.Windows.Forms.GroupBox();
   this.buttonYahooDetailedSearch = new System.Windows.Forms.Button();
   this.textBoxYahooZip = new System.Windows.Forms.TextBox();
   this.textBoxYahooState = new System.Windows.Forms.TextBox();
   this.textBoxYahooCity = new System.Windows.Forms.TextBox();
   this.textBoxYahooStreet = new System.Windows.Forms.TextBox();
   this.groupBoxWWSearch = new System.Windows.Forms.GroupBox();
   this.buttonWWSearch = new System.Windows.Forms.Button();
   this.label1 = new System.Windows.Forms.Label();
   this.textBoxWWFeature = new EnterTextBox();
   this.statusBar = new System.Windows.Forms.StatusBar();
   this.tabControl1 = new System.Windows.Forms.TabControl();
   this.tabPageWWFeature = new System.Windows.Forms.TabPage();
   this.label7 = new System.Windows.Forms.Label();
   this.label6 = new System.Windows.Forms.Label();
   this.label5 = new System.Windows.Forms.Label();
   this.tabPageYahoo = new System.Windows.Forms.TabPage();
   this.pictureBoxYahoo = new System.Windows.Forms.PictureBox();
   this.tabPageTerraPage = new System.Windows.Forms.TabPage();
   this.pictureBox1 = new System.Windows.Forms.PictureBox();
   this.groupBox1 = new System.Windows.Forms.GroupBox();
   this.terraStNo = new System.Windows.Forms.TextBox();
   this.terraSearch = new System.Windows.Forms.Button();
   this.terraPost = new System.Windows.Forms.TextBox();
   this.terraState = new System.Windows.Forms.TextBox();
   this.terraSub = new System.Windows.Forms.TextBox();
   this.terraSt = new System.Windows.Forms.TextBox();
   this.pictureBoxWW = new System.Windows.Forms.PictureBox();
   this.groupBoxResults.SuspendLayout();
   ((System.ComponentModel.ISupportInitialize)(this.numericUpDownAltitude)).BeginInit();
   this.groupBoxYahooSimple.SuspendLayout();
   this.groupBoxYahooDetailed.SuspendLayout();
   this.groupBoxWWSearch.SuspendLayout();
   this.tabControl1.SuspendLayout();
   this.tabPageWWFeature.SuspendLayout();
   this.tabPageYahoo.SuspendLayout();
   ((System.ComponentModel.ISupportInitialize)(this.pictureBoxYahoo)).BeginInit();
   this.tabPageTerraPage.SuspendLayout();
   ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
   this.groupBox1.SuspendLayout();
   ((System.ComponentModel.ISupportInitialize)(this.pictureBoxWW)).BeginInit();
   this.SuspendLayout();
   this.listViewResults.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
      | System.Windows.Forms.AnchorStyles.Right)));
   this.listViewResults.FullRowSelect = true;
   this.listViewResults.HeaderStyle = System.Windows.Forms.ColumnHeaderStyle.Nonclickable;
   this.listViewResults.HideSelection = false;
   this.listViewResults.Location = new System.Drawing.Point(8, 16);
   this.listViewResults.MultiSelect = false;
   this.listViewResults.Name = "listViewResults";
   this.listViewResults.Size = new System.Drawing.Size(536, 120);
   this.listViewResults.TabIndex = 1;
   this.listViewResults.UseCompatibleStateImageBehavior = false;
   this.listViewResults.View = System.Windows.Forms.View.Details;
   this.listViewResults.SelectedIndexChanged += new System.EventHandler(this.listViewResults_SelectedIndexChanged);
   this.groupBoxResults.Controls.Add(this.numericUpDownAltitude);
   this.groupBoxResults.Controls.Add(this.label4);
   this.groupBoxResults.Controls.Add(this.buttonGo);
   this.groupBoxResults.Controls.Add(this.label3);
   this.groupBoxResults.Controls.Add(this.label2);
   this.groupBoxResults.Controls.Add(this.textBoxLongitude);
   this.groupBoxResults.Controls.Add(this.textBoxLatitude);
   this.groupBoxResults.Controls.Add(this.listViewResults);
   this.groupBoxResults.Controls.Add(this.comboBox1);
   this.groupBoxResults.Location = new System.Drawing.Point(16, 216);
   this.groupBoxResults.Name = "groupBoxResults";
   this.groupBoxResults.Size = new System.Drawing.Size(552, 240);
   this.groupBoxResults.TabIndex = 2;
   this.groupBoxResults.TabStop = false;
   this.groupBoxResults.Text = "Results";
   this.numericUpDownAltitude.Location = new System.Drawing.Point(208, 208);
   this.numericUpDownAltitude.Maximum = new decimal(new int[] {
            100000000,
            0,
            0,
            0});
   this.numericUpDownAltitude.Name = "numericUpDownAltitude";
   this.numericUpDownAltitude.Size = new System.Drawing.Size(192, 20);
   this.numericUpDownAltitude.TabIndex = 10;
   this.numericUpDownAltitude.Value = new decimal(new int[] {
            50000,
            0,
            0,
            0});
   this.label4.Location = new System.Drawing.Point(136, 208);
   this.label4.Name = "label4";
   this.label4.Size = new System.Drawing.Size(64, 23);
   this.label4.TabIndex = 9;
   this.label4.Text = "Altitude (m)";
   this.buttonGo.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
   this.buttonGo.Location = new System.Drawing.Point(456, 152);
   this.buttonGo.Name = "buttonGo";
   this.buttonGo.Size = new System.Drawing.Size(88, 72);
   this.buttonGo.TabIndex = 6;
   this.buttonGo.Text = "Go";
   this.buttonGo.Click += new System.EventHandler(this.buttonGo_Click);
   this.label3.Location = new System.Drawing.Point(136, 176);
   this.label3.Name = "label3";
   this.label3.Size = new System.Drawing.Size(64, 23);
   this.label3.TabIndex = 5;
   this.label3.Text = "Longitude";
   this.label2.Location = new System.Drawing.Point(144, 144);
   this.label2.Name = "label2";
   this.label2.Size = new System.Drawing.Size(56, 23);
   this.label2.TabIndex = 4;
   this.label2.Text = "Latitude";
   this.textBoxLongitude.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
      | System.Windows.Forms.AnchorStyles.Right)));
   this.textBoxLongitude.Location = new System.Drawing.Point(208, 176);
   this.textBoxLongitude.Name = "textBoxLongitude";
   this.textBoxLongitude.Size = new System.Drawing.Size(240, 20);
   this.textBoxLongitude.TabIndex = 3;
   this.textBoxLatitude.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
      | System.Windows.Forms.AnchorStyles.Right)));
   this.textBoxLatitude.Location = new System.Drawing.Point(208, 144);
   this.textBoxLatitude.Name = "textBoxLatitude";
   this.textBoxLatitude.Size = new System.Drawing.Size(240, 20);
   this.textBoxLatitude.TabIndex = 2;
   this.comboBox1.Items.AddRange(new object[] {
            "Decimal Degrees",
            "Degrees / Seconds"});
   this.comboBox1.Location = new System.Drawing.Point(8, 144);
   this.comboBox1.Name = "comboBox1";
   this.comboBox1.Size = new System.Drawing.Size(121, 21);
   this.comboBox1.TabIndex = 7;
   this.comboBox1.Text = "Decimal Degrees";
   this.groupBoxYahooSimple.Controls.Add(this.buttonYahooSimpleSearch);
   this.groupBoxYahooSimple.Controls.Add(this.textBoxYahooSimple);
   this.groupBoxYahooSimple.Location = new System.Drawing.Point(8, 16);
   this.groupBoxYahooSimple.Name = "groupBoxYahooSimple";
   this.groupBoxYahooSimple.Size = new System.Drawing.Size(224, 88);
   this.groupBoxYahooSimple.TabIndex = 3;
   this.groupBoxYahooSimple.TabStop = false;
   this.groupBoxYahooSimple.Text = "Yahoo! Simple Search (USA)";
   this.buttonYahooSimpleSearch.Location = new System.Drawing.Point(136, 56);
   this.buttonYahooSimpleSearch.Name = "buttonYahooSimpleSearch";
   this.buttonYahooSimpleSearch.Size = new System.Drawing.Size(75, 23);
   this.buttonYahooSimpleSearch.TabIndex = 1;
   this.buttonYahooSimpleSearch.Text = "Search";
   this.buttonYahooSimpleSearch.Click += new System.EventHandler(this.buttonYahooSimpleSearch_Click);
   this.textBoxYahooSimple.Location = new System.Drawing.Point(16, 24);
   this.textBoxYahooSimple.Name = "textBoxYahooSimple";
   this.textBoxYahooSimple.Size = new System.Drawing.Size(200, 20);
   this.textBoxYahooSimple.TabIndex = 0;
   this.textBoxYahooSimple.Text = "Address or Keyword(s)";
   this.textBoxYahooSimple.MouseUp += new System.Windows.Forms.MouseEventHandler(this.textBoxYahooSimple_MouseUp);
   this.groupBoxYahooDetailed.Controls.Add(this.buttonYahooDetailedSearch);
   this.groupBoxYahooDetailed.Controls.Add(this.textBoxYahooZip);
   this.groupBoxYahooDetailed.Controls.Add(this.textBoxYahooState);
   this.groupBoxYahooDetailed.Controls.Add(this.textBoxYahooCity);
   this.groupBoxYahooDetailed.Controls.Add(this.textBoxYahooStreet);
   this.groupBoxYahooDetailed.Location = new System.Drawing.Point(240, 16);
   this.groupBoxYahooDetailed.Name = "groupBoxYahooDetailed";
   this.groupBoxYahooDetailed.Size = new System.Drawing.Size(296, 120);
   this.groupBoxYahooDetailed.TabIndex = 4;
   this.groupBoxYahooDetailed.TabStop = false;
   this.groupBoxYahooDetailed.Text = "Yahoo! Detailed Search (USA)";
   this.buttonYahooDetailedSearch.Location = new System.Drawing.Point(208, 88);
   this.buttonYahooDetailedSearch.Name = "buttonYahooDetailedSearch";
   this.buttonYahooDetailedSearch.Size = new System.Drawing.Size(75, 23);
   this.buttonYahooDetailedSearch.TabIndex = 5;
   this.buttonYahooDetailedSearch.Text = "Search";
   this.buttonYahooDetailedSearch.Click += new System.EventHandler(this.buttonYahooDetailedSearch_Click);
   this.textBoxYahooZip.Location = new System.Drawing.Point(216, 56);
   this.textBoxYahooZip.Name = "textBoxYahooZip";
   this.textBoxYahooZip.Size = new System.Drawing.Size(64, 20);
   this.textBoxYahooZip.TabIndex = 4;
   this.textBoxYahooZip.Text = "Zip Code";
   this.textBoxYahooState.Location = new System.Drawing.Point(168, 56);
   this.textBoxYahooState.Name = "textBoxYahooState";
   this.textBoxYahooState.Size = new System.Drawing.Size(40, 20);
   this.textBoxYahooState.TabIndex = 3;
   this.textBoxYahooState.Text = "State";
   this.textBoxYahooCity.Location = new System.Drawing.Point(16, 56);
   this.textBoxYahooCity.Name = "textBoxYahooCity";
   this.textBoxYahooCity.Size = new System.Drawing.Size(144, 20);
   this.textBoxYahooCity.TabIndex = 2;
   this.textBoxYahooCity.Text = "City";
   this.textBoxYahooStreet.Location = new System.Drawing.Point(16, 24);
   this.textBoxYahooStreet.Name = "textBoxYahooStreet";
   this.textBoxYahooStreet.Size = new System.Drawing.Size(264, 20);
   this.textBoxYahooStreet.TabIndex = 1;
   this.textBoxYahooStreet.Text = "Street";
   this.groupBoxWWSearch.Controls.Add(this.buttonWWSearch);
   this.groupBoxWWSearch.Controls.Add(this.label1);
   this.groupBoxWWSearch.Controls.Add(this.textBoxWWFeature);
   this.groupBoxWWSearch.Location = new System.Drawing.Point(157, 14);
   this.groupBoxWWSearch.Name = "groupBoxWWSearch";
   this.groupBoxWWSearch.Size = new System.Drawing.Size(296, 80);
   this.groupBoxWWSearch.TabIndex = 4;
   this.groupBoxWWSearch.TabStop = false;
   this.groupBoxWWSearch.Text = "World Wind Feature Search (Global)";
   this.buttonWWSearch.Location = new System.Drawing.Point(208, 48);
   this.buttonWWSearch.Name = "buttonWWSearch";
   this.buttonWWSearch.Size = new System.Drawing.Size(75, 23);
   this.buttonWWSearch.TabIndex = 3;
   this.buttonWWSearch.Text = "Search";
   this.buttonWWSearch.Click += new System.EventHandler(this.buttonWWSearch_Click);
   this.label1.Location = new System.Drawing.Point(16, 48);
   this.label1.Name = "label1";
   this.label1.Size = new System.Drawing.Size(192, 23);
   this.label1.TabIndex = 2;
   this.label1.Text = "(Cities, geographical features, etc.)";
   this.textBoxWWFeature.Location = new System.Drawing.Point(16, 24);
   this.textBoxWWFeature.Name = "textBoxWWFeature";
   this.textBoxWWFeature.Size = new System.Drawing.Size(264, 20);
   this.textBoxWWFeature.TabIndex = 1;
   this.textBoxWWFeature.Text = "Keyword(s)";
   this.textBoxWWFeature.KeyUp += new System.Windows.Forms.KeyEventHandler(this.textBoxWWFeature_KeyUp);
   this.statusBar.Location = new System.Drawing.Point(0, 472);
   this.statusBar.Name = "statusBar";
   this.statusBar.Size = new System.Drawing.Size(584, 22);
   this.statusBar.TabIndex = 5;
   this.tabControl1.Alignment = System.Windows.Forms.TabAlignment.Bottom;
   this.tabControl1.Controls.Add(this.tabPageWWFeature);
   this.tabControl1.Controls.Add(this.tabPageYahoo);
   this.tabControl1.Controls.Add(this.tabPageTerraPage);
   this.tabControl1.Location = new System.Drawing.Point(16, 16);
   this.tabControl1.Multiline = true;
   this.tabControl1.Name = "tabControl1";
   this.tabControl1.SelectedIndex = 0;
   this.tabControl1.Size = new System.Drawing.Size(552, 192);
   this.tabControl1.TabIndex = 6;
   this.tabPageWWFeature.Controls.Add(this.pictureBoxWW);
   this.tabPageWWFeature.Controls.Add(this.label7);
   this.tabPageWWFeature.Controls.Add(this.label6);
   this.tabPageWWFeature.Controls.Add(this.label5);
   this.tabPageWWFeature.Controls.Add(this.groupBoxWWSearch);
   this.tabPageWWFeature.Location = new System.Drawing.Point(4, 4);
   this.tabPageWWFeature.Name = "tabPageWWFeature";
   this.tabPageWWFeature.Size = new System.Drawing.Size(544, 166);
   this.tabPageWWFeature.TabIndex = 2;
   this.tabPageWWFeature.Text = "World Wind Feature Search (Global)";
   this.tabPageWWFeature.UseVisualStyleBackColor = true;
   this.label7.AutoSize = true;
   this.label7.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Underline, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
   this.label7.Location = new System.Drawing.Point(154, 106);
   this.label7.Name = "label7";
   this.label7.Size = new System.Drawing.Size(69, 13);
   this.label7.TabIndex = 7;
   this.label7.Text = "Search Help:";
   this.label6.AutoSize = true;
   this.label6.Location = new System.Drawing.Point(154, 136);
   this.label6.Name = "label6";
   this.label6.Size = new System.Drawing.Size(362, 13);
   this.label6.TabIndex = 6;
   this.label6.Text = " Enclosing multiple words in quotes will search for the exact phrase entered";
   this.label5.AutoSize = true;
   this.label5.Location = new System.Drawing.Point(154, 123);
   this.label5.Name = "label5";
   this.label5.Size = new System.Drawing.Size(368, 13);
   this.label5.TabIndex = 5;
   this.label5.Text = " Searching for multiple words will find result thats contain every word entered";
   this.tabPageYahoo.Controls.Add(this.pictureBoxYahoo);
   this.tabPageYahoo.Controls.Add(this.groupBoxYahooSimple);
   this.tabPageYahoo.Controls.Add(this.groupBoxYahooDetailed);
   this.tabPageYahoo.Location = new System.Drawing.Point(4, 4);
   this.tabPageYahoo.Name = "tabPageYahoo";
   this.tabPageYahoo.Size = new System.Drawing.Size(544, 166);
   this.tabPageYahoo.TabIndex = 0;
   this.tabPageYahoo.Text = "Yahoo! Search (USA)";
   this.tabPageYahoo.UseVisualStyleBackColor = true;
   this.pictureBoxYahoo.Location = new System.Drawing.Point(72, 112);
   this.pictureBoxYahoo.Name = "pictureBoxYahoo";
   this.pictureBoxYahoo.Size = new System.Drawing.Size(88, 31);
   this.pictureBoxYahoo.TabIndex = 5;
   this.pictureBoxYahoo.TabStop = false;
   this.pictureBoxYahoo.Click += new System.EventHandler(this.pictureBoxYahoo_Click);
   this.tabPageTerraPage.Controls.Add(this.pictureBox1);
   this.tabPageTerraPage.Controls.Add(this.groupBox1);
   this.tabPageTerraPage.Location = new System.Drawing.Point(4, 4);
   this.tabPageTerraPage.Name = "tabPageTerraPage";
   this.tabPageTerraPage.Size = new System.Drawing.Size(544, 166);
   this.tabPageTerraPage.TabIndex = 3;
   this.tabPageTerraPage.Text = "Terra Page Detailed Search (Australia)";
   this.tabPageTerraPage.UseVisualStyleBackColor = true;
   this.pictureBox1.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox1.Image")));
   this.pictureBox1.Location = new System.Drawing.Point(432, 112);
   this.pictureBox1.Name = "pictureBox1";
   this.pictureBox1.Size = new System.Drawing.Size(104, 40);
   this.pictureBox1.TabIndex = 5;
   this.pictureBox1.TabStop = false;
   this.groupBox1.Controls.Add(this.terraStNo);
   this.groupBox1.Controls.Add(this.terraSearch);
   this.groupBox1.Controls.Add(this.terraPost);
   this.groupBox1.Controls.Add(this.terraState);
   this.groupBox1.Controls.Add(this.terraSub);
   this.groupBox1.Controls.Add(this.terraSt);
   this.groupBox1.Location = new System.Drawing.Point(120, 8);
   this.groupBox1.Name = "groupBox1";
   this.groupBox1.Size = new System.Drawing.Size(296, 120);
   this.groupBox1.TabIndex = 4;
   this.groupBox1.TabStop = false;
   this.groupBox1.Text = "TerraPage Detailed Search (Australia)";
   this.terraStNo.Location = new System.Drawing.Point(16, 24);
   this.terraStNo.Name = "terraStNo";
   this.terraStNo.Size = new System.Drawing.Size(44, 20);
   this.terraStNo.TabIndex = 6;
   this.terraStNo.Text = "No";
   this.terraSearch.Location = new System.Drawing.Point(208, 88);
   this.terraSearch.Name = "terraSearch";
   this.terraSearch.Size = new System.Drawing.Size(75, 23);
   this.terraSearch.TabIndex = 5;
   this.terraSearch.Text = "Search";
   this.terraSearch.Click += new System.EventHandler(this.terraSearch_Click);
   this.terraPost.Location = new System.Drawing.Point(216, 56);
   this.terraPost.Name = "terraPost";
   this.terraPost.Size = new System.Drawing.Size(64, 20);
   this.terraPost.TabIndex = 4;
   this.terraPost.Text = "Post Code";
   this.terraState.Location = new System.Drawing.Point(168, 56);
   this.terraState.Name = "terraState";
   this.terraState.Size = new System.Drawing.Size(40, 20);
   this.terraState.TabIndex = 3;
   this.terraState.Text = "State";
   this.terraSub.Location = new System.Drawing.Point(16, 56);
   this.terraSub.Name = "terraSub";
   this.terraSub.Size = new System.Drawing.Size(144, 20);
   this.terraSub.TabIndex = 2;
   this.terraSub.Text = "Suburb";
   this.terraSt.Location = new System.Drawing.Point(72, 24);
   this.terraSt.Name = "terraSt";
   this.terraSt.Size = new System.Drawing.Size(208, 20);
   this.terraSt.TabIndex = 1;
   this.terraSt.Text = "Street";
   this.pictureBoxWW.Location = new System.Drawing.Point(22, 43);
   this.pictureBoxWW.Name = "pictureBoxWW";
   this.pictureBoxWW.Size = new System.Drawing.Size(96, 80);
   this.pictureBoxWW.TabIndex = 8;
   this.pictureBoxWW.TabStop = false;
   this.pictureBoxWW.Click += new System.EventHandler(this.pictureBoxWW_Click);
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.ClientSize = new System.Drawing.Size(584, 494);
   this.Controls.Add(this.tabControl1);
   this.Controls.Add(this.statusBar);
   this.Controls.Add(this.groupBoxResults);
   this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
   this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
   this.Name = "PlaceFinder";
   this.Text = "Place Finder";
   this.Closing += new System.ComponentModel.CancelEventHandler(this.OnClosing);
   this.groupBoxResults.ResumeLayout(false);
   this.groupBoxResults.PerformLayout();
   ((System.ComponentModel.ISupportInitialize)(this.numericUpDownAltitude)).EndInit();
   this.groupBoxYahooSimple.ResumeLayout(false);
   this.groupBoxYahooSimple.PerformLayout();
   this.groupBoxYahooDetailed.ResumeLayout(false);
   this.groupBoxYahooDetailed.PerformLayout();
   this.groupBoxWWSearch.ResumeLayout(false);
   this.groupBoxWWSearch.PerformLayout();
   this.tabControl1.ResumeLayout(false);
   this.tabPageWWFeature.ResumeLayout(false);
   this.tabPageWWFeature.PerformLayout();
   this.tabPageYahoo.ResumeLayout(false);
   ((System.ComponentModel.ISupportInitialize)(this.pictureBoxYahoo)).EndInit();
   this.tabPageTerraPage.ResumeLayout(false);
   ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
   this.groupBox1.ResumeLayout(false);
   this.groupBox1.PerformLayout();
   ((System.ComponentModel.ISupportInitialize)(this.pictureBoxWW)).EndInit();
   this.ResumeLayout(false);
  }
  string m_CurrentSearchUri = null;
  private void buttonYahooSimpleSearch_Click(object sender, System.EventArgs e)
  {
   m_CurrentSearchUri = string.Format(
    "{0}?appid={1}&location={2}", YahooUri, YahooAppId, textBoxYahooSimple.Text);
   this.listViewResults.Items.Clear();
   this.listViewResults.Columns.Clear();
   this.listViewResults.Columns.Add("Address", 120, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("City", 80, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("State", 30, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("Zip", 50, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("Country", 80, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("Latitude", 80, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("Longitude", 80, HorizontalAlignment.Center);
   try
   {
    System.Threading.Thread t = new System.Threading.Thread(new System.Threading.ThreadStart(SearchFunction));
    t.IsBackground = true;
    t.Start();
   }
   catch
   {
   }
  }
  private void SearchFunction()
  {
   try
   {
    if(m_CurrentSearchUri != null)
    {
     using(WebDownload client = new WebDownload(m_CurrentSearchUri))
     {
      client.DownloadMemory();
      XmlTextReader reader = new XmlTextReader(client.ContentStream);
      ArrayList resultList = new ArrayList();
      Hashtable curResult = new Hashtable();
      while(reader.Read())
      {
       if(reader.NodeType == XmlNodeType.Element)
       {
        if(reader.Name != "Result" && reader.Name != "ResultSet")
        {
         curResult.Add(reader.Name, reader.ReadString());
        }
       }
       else if(reader.NodeType == XmlNodeType.EndElement)
       {
        if(reader.Name.Equals("Result"))
        {
         resultList.Add(curResult);
         curResult = new Hashtable();
        }
       }
      }
      foreach(Hashtable r in resultList)
      {
       string[] parts = new string[this.listViewResults.Columns.Count];
       for(int i = 0; i < this.listViewResults.Columns.Count; i++)
       {
        parts[i] = (string)r[this.listViewResults.Columns[i].Text];
       }
       this.listViewResults.Items.Add(new ListViewItem(parts));
      }
     }
    }
    this.statusBar.BeginInvoke(new SetStatusMessageDelegate(SetStatusMessage), new object[] {this.listViewResults.Items.Count.ToString() + " items found."});
   }
   catch{}
  }
  static string getInnerTextFromFirstChild(XPathNodeIterator iter)
  {
   if(iter.Count == 0)
   {
    return null;
   }
   else
   {
    iter.MoveNext();
    return iter.Current.Value;
   }
  }
  private void buttonYahooDetailedSearch_Click(object sender, System.EventArgs e)
  {
   m_CurrentSearchUri = string.Format(
    "{0}?appid={1}&street={2}&city={3}&state={4}&zip={5}", YahooUri, YahooAppId,
    this.textBoxYahooStreet.Text,
    this.textBoxYahooCity.Text,
    this.textBoxYahooState.Text,
    this.textBoxYahooZip.Text);
   this.listViewResults.Items.Clear();
   this.listViewResults.Columns.Clear();
   this.listViewResults.Columns.Add("Address", 120, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("City", 80, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("State", 30, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("Zip", 50, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("Country", 80, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("Latitude", 80, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("Longitude", 80, HorizontalAlignment.Center);
   try
   {
    System.Threading.Thread t = new System.Threading.Thread(new System.Threading.ThreadStart(SearchFunction));
    t.IsBackground = true;
    t.Start();
   }
   catch
   {
   }
  }
  private void SearchOnline()
  {
   this.listViewResults.Items.Clear();
   this.listViewResults.Columns.Clear();
   this.listViewResults.Columns.Add("Name", 150, HorizontalAlignment.Left);
   this.listViewResults.Columns.Add("Country", 120, HorizontalAlignment.Left);
   this.listViewResults.Columns.Add("Region", 80, HorizontalAlignment.Left);
   this.listViewResults.Columns.Add("Type", 90, HorizontalAlignment.Left);
   this.listViewResults.Columns.Add("Latitude", 60, HorizontalAlignment.Left);
   this.listViewResults.Columns.Add("Longitude", 60, HorizontalAlignment.Left);
   this.statusBar.BeginInvoke(new SetStatusMessageDelegate(SetStatusMessage), new object[] { "Searching..." });
   try
   {
    XmlTextReader reader = null;
    string query = this.textBoxWWFeature.Text;
    if (query.Contains(" "))
    {
     query = "+" + query.Replace(" ", " +");
    }
    string urlQuery = HttpUtility.UrlEncode(query);
    try
    {
     string searchUriString = "http://s0.tileservice.worldwindcentral.org/queryGeoNames?q=" + urlQuery + "&columns=className,countryName,priAdmName,latitude,longitude,name,origName";
     reader = new XmlTextReader(searchUriString);
    }
    catch (System.Net.WebException ex)
    {
     MessageBox.Show(ex.Message);
    }
    int counter = 0;
    while (reader.Read())
    {
     if (reader.Name == "Row")
     {
      string name = null;
      if (reader.GetAttribute("origName") == "")
      {
       name = reader.GetAttribute("name");
      }
      else
      {
       name = reader.GetAttribute("origName");
      }
      string sLon = reader.GetAttribute("longitude");
      string sLat = reader.GetAttribute("latitude");
      string country = reader.GetAttribute("countryName");
      string region = reader.GetAttribute("priAdmName");
      string type = reader.GetAttribute("className");
      double lon = double.Parse(sLon, CultureInfo.InvariantCulture);
      double lat = double.Parse(sLat, CultureInfo.InvariantCulture);
      ListViewItem item = new ListViewItem(new string[] { name, country, region, type, lat.ToString(CultureInfo.CurrentCulture), lon.ToString(CultureInfo.CurrentCulture) });
      listViewResults.Items.Add(item);
      counter++;
     }
    }
    if (counter != 0)
    {
     string status = "Found " + counter + " results.";
     this.statusBar.BeginInvoke(new SetStatusMessageDelegate(SetStatusMessage), new object[] { status });
    }
    else
    {
     this.statusBar.BeginInvoke(new SetStatusMessageDelegate(SetStatusMessage), new object[] { "Found no results." });
    }
   }
   catch (XmlException xmle)
   {
    MessageBox.Show(xmle.Message);
   }
  }
  private void buttonWWSearch_Click(object sender, System.EventArgs e)
  {
   this.startWWSearch();
  }
  private void startWWSearch()
  {
   if (!World.Settings.UseOfflineSearch && m_WorldWindow.CurrentWorld.IsEarth)
   {
    SearchOnline();
   }
   else
   {
    this.listViewResults.Items.Clear();
    this.listViewResults.Columns.Clear();
    this.listViewResults.Columns.Add("Name", 120, HorizontalAlignment.Center);
    this.listViewResults.Columns.Add("Country", 80, HorizontalAlignment.Center);
    this.listViewResults.Columns.Add("Latitude", 80, HorizontalAlignment.Center);
    this.listViewResults.Columns.Add("Longitude", 80, HorizontalAlignment.Center);
    System.Threading.Thread t = new System.Threading.Thread(new System.Threading.ThreadStart(SearchWWData));
    t.IsBackground = true;
    t.Start();
   }
  }
  private void SearchWWData()
  {
   this.statusBar.BeginInvoke(new SetStatusMessageDelegate(SetStatusMessage), new object[] {"Searching..."});
   string[] searchtokens = textBoxWWFeature.Text.Split(' ');
   string[] wplFiles = getWplFiles(m_WorldWindow.CurrentWorld.RenderableObjects);
   if(wplFiles != null)
   {
    foreach(string file in wplFiles)
    {
     PlaceNameSetFullSearch(searchtokens, file);
    }
   }
   this.statusBar.BeginInvoke(new SetStatusMessageDelegate(SetStatusMessage), new object[] {this.listViewResults.Items.Count.ToString() + " items found."});
  }
  private delegate void SetStatusMessageDelegate(string msg);
  private void SetStatusMessage(string msg)
  {
   this.statusBar.Text = msg;
  }
  bool PlaceNameSetFullSearch(string [] searchTokens, string placenameDataFile)
  {
   DirectoryInfo dir = new DirectoryInfo(Path.GetDirectoryName(
    Path.Combine(
    Path.GetDirectoryName(Application.ExecutablePath),
    placenameDataFile)));
   if(!dir.Exists) return true;
   foreach(FileInfo placenameFile in dir.GetFiles("*.wwp"))
   {
    using(BinaryReader reader = new BinaryReader(new BufferedStream(placenameFile.OpenRead(), 10000000)) )
    {
     int placenameCount = reader.ReadInt32();
     for(int i = 0; i < placenameCount; i++)
     {
      if(CheckStopRequested()) return false;
      WorldWindPlacename pn = new WorldWindPlacename();
      WplIndex.ReadPlaceName(reader, ref pn, WplIndex.MetaDataAction.Store);
      if(isPlaceMatched(searchTokens, pn))
      {
       if(CheckMaxResults()) return false;
       listViewResults.Invoke(new addPlaceDelegate(addPlace), new object[] { pn });
      }
     }
    }
   }
   return true;
  }
  private delegate void addPlaceDelegate(WorldWindPlacename pn);
  private void addPlace(WorldWindPlacename pn)
  {
   ListViewItem item = new ListViewItem(
    new string[] { pn.Name, (string)pn.metaData["Country"], pn.Lat.ToString(), pn.Lon.ToString() }
    );
   item.Tag = pn;
   listViewResults.Items.Add(item);
  }
  bool cancelSearch = false;
  void SetStopped( string statusMessage )
  {
   this.cancelSearch = false;
  }
  void SetStopped()
  {
   SetStopped("Stopped.");
  }
  bool CheckStopRequested()
  {
   if(this.cancelSearch)
   {
    SetStopped();
    return true;
   }
   return false;
  }
  bool CheckMaxResults()
  {
   return false;
  }
  static bool isPlaceMatched(string[] searchTokens, WorldWindPlacename pn)
  {
   char[] delimiters = new char[] {' ','(',')',','};
   string targetString;
   if(pn.metaData != null)
   {
    StringBuilder sb = new StringBuilder(pn.Name);
    foreach(string str in pn.metaData.Values)
    {
     sb.Append(' ');
     sb.Append(str);
    }
    targetString = sb.ToString();
   }
   else
   {
    targetString = pn.Name;
   }
   string[] targetTokens = targetString.Split(delimiters);
   foreach(string curSearchToken in searchTokens)
   {
    bool found = false;
    foreach(string curTargetToken in targetTokens)
    {
     if(String.Compare(curSearchToken, curTargetToken, true) == 0)
     {
      found = true;
      break;
     }
    }
    if(!found)
     return false;
   }
   return true;
  }
  private string[] getWplFiles(WorldWind.Renderable.RenderableObject ro)
  {
   if(ro is WorldWind.Renderable.TiledPlacenameSet)
   {
    if(ro.MetaData.Contains("PlacenameDataFile"))
    {
     return (string[])new string[] {(string)ro.MetaData["PlacenameDataFile"]};
    }
   }
   else if(ro is WorldWind.Renderable.RenderableObjectList)
   {
    WorldWind.Renderable.RenderableObjectList rol = (WorldWind.Renderable.RenderableObjectList)ro;
    ArrayList wplFiles = new ArrayList();
    foreach(WorldWind.Renderable.RenderableObject childRo in rol.ChildObjects)
    {
     string[] childStrings = getWplFiles(childRo);
     if(childStrings != null)
      foreach(string childString in childStrings)
       wplFiles.Add(childString);
    }
    if(wplFiles.Count > 0)
     return (string[])wplFiles.ToArray(typeof(string));
   }
   return null;
  }
  private void buttonGo_Click(object sender, System.EventArgs e)
  {
   try
   {
    double lat = double.Parse(this.textBoxLatitude.Text);
    double lon = double.Parse(this.textBoxLongitude.Text);
    double alt = (double)this.numericUpDownAltitude.Value;
    m_WorldWindow.GotoLatLonAltitude(lat, lon, alt);
   }
   catch{}
  }
  private void textBoxYahooSimple_MouseUp(object sender, System.Windows.Forms.MouseEventArgs e)
  {
   if(this.textBoxYahooSimple.Text == "Address or Keyword(s)")
   {
    this.textBoxYahooSimple.Text = "";
   }
  }
  private void listViewResults_SelectedIndexChanged(object sender, System.EventArgs e)
  {
   if(this.listViewResults.SelectedItems.Count > 0)
   {
    string latString = null;
    string lonString = null;
    string addressString = null;
    string cityString = null;
    string countryString = null;
    for(int i = 0; i < this.listViewResults.Columns.Count; i++)
    {
     if(this.listViewResults.Columns[i].Text == "Latitude")
     {
      latString = this.listViewResults.SelectedItems[0].SubItems[i].Text;
     }
     else if(this.listViewResults.Columns[i].Text == "Longitude")
     {
      lonString = this.listViewResults.SelectedItems[0].SubItems[i].Text;
     }
     else if(this.listViewResults.Columns[i].Text == "Address")
     {
      addressString = this.listViewResults.SelectedItems[0].SubItems[i].Text;
     }
     else if(this.listViewResults.Columns[i].Text == "City")
     {
      cityString = this.listViewResults.SelectedItems[0].SubItems[i].Text;
     }
     else if(this.listViewResults.Columns[i].Text == "Country")
     {
      countryString = this.listViewResults.SelectedItems[0].SubItems[i].Text;
     }
    }
    if(addressString != null && addressString.Length > 0)
    {
     this.numericUpDownAltitude.Value = 2500;
    }
    else if(cityString != null && cityString.Length > 0)
    {
     this.numericUpDownAltitude.Value = 50000;
    }
    else if(countryString != null && countryString.Length > 0)
    {
     this.numericUpDownAltitude.Value = 500000;
    }
    if(latString != null && lonString != null)
    {
     this.textBoxLatitude.Text = latString;
     this.textBoxLongitude.Text = lonString;
     double lat = double.Parse(latString);
     double lon = double.Parse(lonString);
     m_WorldWindow.GotoLatLon(lat, lon);
    }
   }
  }
  private void terraSearch_Click(object sender, System.EventArgs e)
  {
   m_CurrentSearchUri = string.Format(
    "{0}?appid={1}&street={2}&city={3}&state={4}&zip={5}&streetno={6}", TerraUri, YahooAppId,
    this.terraSt.Text,
    this.terraSub.Text,
    this.terraState.Text,
    this.terraPost.Text,
    this.terraStNo.Text);
   this.listViewResults.Items.Clear();
   this.listViewResults.Columns.Clear();
   this.listViewResults.Columns.Add("Address", 120, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("City", 80, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("State", 30, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("Zip", 50, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("Country", 80, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("Latitude", 80, HorizontalAlignment.Center);
   this.listViewResults.Columns.Add("Longitude", 80, HorizontalAlignment.Center);
   try
   {
    SearchFunction();
   }
   catch
   {
   }
  }
  private void OnClosing(object sender, System.ComponentModel.CancelEventArgs e)
  {
   e.Cancel = true;
   this.Visible = false;
  }
  private void pictureBoxYahoo_Click(object sender, System.EventArgs e)
  {
   ProcessStartInfo psi = new ProcessStartInfo();
   psi.FileName = "http://developer.yahoo.net/about";
   psi.Verb = "open";
   psi.UseShellExecute = true;
   psi.CreateNoWindow = true;
   Process.Start(psi);
  }
  private void pictureBoxWW_Click(object sender, System.EventArgs e)
  {
   ProcessStartInfo psi = new ProcessStartInfo();
   psi.FileName = "http://www.fef.org/";
   psi.Verb = "open";
   psi.UseShellExecute = true;
   psi.CreateNoWindow = true;
   Process.Start(psi);
  }
  void textBoxWWFeature_KeyUp(object sender, KeyEventArgs e)
  {
   if (e.KeyCode == Keys.Enter)
   {
    this.startWWSearch();
    e.Handled = true;
   }
  }
 }
 public class PlaceFinderLoader : WorldWind.PluginEngine.Plugin
 {
  MenuItem m_MenuItem;
  PlaceFinder m_Form = null;
  WorldWind.WindowsControlMenuButton m_ToolbarItem = null;
  public override void Load()
  {
   if(ParentApplication.WorldWindow.CurrentWorld != null && ParentApplication.WorldWindow.CurrentWorld.Name.IndexOf("Earth") >= 0)
   {
    m_MenuItem = new MenuItem("Place Finder");
    m_MenuItem.Click += new EventHandler(menuItemClicked);
    foreach (MenuItem menuItem in m_Application.MainMenu.MenuItems)
    {
     if (menuItem.Text.Replace("&", "") == "Edit")
     {
      menuItem.MenuItems.Add( m_MenuItem );
      break;
     }
    }
    m_Form = new PlaceFinder(ParentApplication.WorldWindow);
    m_Form.Closing += new CancelEventHandler(m_Form_Closing);
    m_Form.Owner = ParentApplication;
    m_ToolbarItem = new WorldWind.WindowsControlMenuButton(
     "PlaceFinder",
     Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath) + "\\Data\\Icons\\Interface\\search.png",
     m_Form);
    ParentApplication.WorldWindow.MenuBar.AddToolsMenuButton(m_ToolbarItem);
   }
  }
  public override void Unload()
  {
   if(m_MenuItem!=null)
   {
    foreach (MenuItem menuItem in m_Application.MainMenu.MenuItems)
    {
     if (menuItem.Text.Replace("&", "") == "Edit")
     {
      menuItem.MenuItems.Remove( m_MenuItem );
      break;
     }
    }
    m_MenuItem.Dispose();
    m_MenuItem = null;
   }
   if(m_ToolbarItem != null)
   {
    ParentApplication.WorldWindow.MenuBar.RemoveToolsMenuButton(m_ToolbarItem);
    m_ToolbarItem.Dispose();
    m_ToolbarItem = null;
   }
   if(m_Form != null)
   {
    m_Form.Dispose();
    m_Form = null;
   }
  }
  void menuItemClicked(object sender, EventArgs e)
  {
   if(m_Form.Visible)
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
  private void m_Form_Closing(object sender, CancelEventArgs e)
  {
   m_MenuItem.Checked = false;
  }
 }
}
