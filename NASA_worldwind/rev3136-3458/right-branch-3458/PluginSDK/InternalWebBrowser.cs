using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using WorldWind.NewWidgets;
namespace WorldWind
{
 public class InternalWebBrowserPanel : Panel
 {
  private WebBrowser webBrowser;
  private ToolStrip webBrowserToolStrip;
  private ToolStripButton webBrowserBack;
  private ToolStripButton webBrowserForward;
  private ToolStripTextBox webBrowserURL;
  private ToolStripButton webBrowserGo;
  private ToolStripButton webBrowserStop;
  private Splitter splitter;
  public InternalWebBrowserPanel()
  {
   InitializeComponent();
  }
  public InternalWebBrowserPanel(string startUrl)
  {
   InitializeComponent();
   this.NavigateTo(startUrl);
  }
  public void NavigateTo(string targetUrl)
  {
   webBrowser.Navigate(targetUrl);
  }
  public bool IsTyping()
  {
   return webBrowserURL.Focused;
  }
  private void InitializeComponent()
  {
   this.webBrowser = new System.Windows.Forms.WebBrowser();
   this.webBrowserToolStrip = new System.Windows.Forms.ToolStrip();
   this.webBrowserBack = new System.Windows.Forms.ToolStripButton();
   this.webBrowserForward = new System.Windows.Forms.ToolStripButton();
   this.webBrowserURL = new System.Windows.Forms.ToolStripTextBox();
   this.webBrowserGo = new System.Windows.Forms.ToolStripButton();
   this.webBrowserStop = new System.Windows.Forms.ToolStripButton();
   this.splitter = new System.Windows.Forms.Splitter();
   this.webBrowserToolStrip.SuspendLayout();
   this.SuspendLayout();
   this.SizeChanged += new System.EventHandler(this.Resized);
   this.webBrowser.Location = new System.Drawing.Point(0, 26);
   this.webBrowser.Margin = new System.Windows.Forms.Padding(5);
   this.webBrowser.Name = "webBrowser";
   this.webBrowser.Height = this.Height - this.webBrowserToolStrip.Height;
   this.webBrowser.Width = this.Width;
   this.webBrowser.ScriptErrorsSuppressed = true;
   this.webBrowser.TabIndex = 3;
   this.webBrowser.Url = new System.Uri("http://worldwind.arc.nasa.gov/", System.UriKind.Absolute);
   this.webBrowser.DocumentCompleted += new System.Windows.Forms.WebBrowserDocumentCompletedEventHandler(this.webBrowser_DocumentCompleted);
   this.webBrowserToolStrip.ImageScalingSize = new System.Drawing.Size(18, 18);
   this.webBrowserToolStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
    this.webBrowserBack,
    this.webBrowserForward,
    this.webBrowserStop,
    this.webBrowserURL,
    this.webBrowserGo});
   this.webBrowserToolStrip.Dock = DockStyle.Top;
   this.webBrowserToolStrip.LayoutStyle = System.Windows.Forms.ToolStripLayoutStyle.Flow;
   this.webBrowserToolStrip.Location = new System.Drawing.Point(0, 0);
   this.webBrowserToolStrip.Name = "webBrowserToolStrip";
   this.webBrowserToolStrip.Padding = new System.Windows.Forms.Padding(0, 0, 1, 1);
   this.webBrowserToolStrip.Height = 26;
   this.webBrowserToolStrip.TabIndex = 4;
   this.webBrowserToolStrip.Text = "toolStrip1";
   this.webBrowserBack.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
   this.webBrowserBack.Image = global::WorldWind.Properties.Resources.back;
   this.webBrowserBack.ImageTransparentColor = System.Drawing.Color.Magenta;
   this.webBrowserBack.Name = "webBrowserBack";
   this.webBrowserBack.Size = new System.Drawing.Size(23, 22);
   this.webBrowserBack.Text = "Back";
   this.webBrowserBack.Click += new System.EventHandler(this.webBrowserBack_Click);
   this.webBrowserForward.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
   this.webBrowserForward.Image = global::WorldWind.Properties.Resources.forward;
   this.webBrowserForward.ImageTransparentColor = System.Drawing.Color.Magenta;
   this.webBrowserForward.Name = "webBrowserForward";
   this.webBrowserForward.Size = new System.Drawing.Size(23, 22);
   this.webBrowserForward.Text = "Forward";
   this.webBrowserForward.Click += new System.EventHandler(this.webBrowserForward_Click);
   this.webBrowserURL.AcceptsReturn = true;
   this.webBrowserURL.Name = "webBrowserURL";
   this.webBrowserURL.Size = new System.Drawing.Size(200, 25);
   this.webBrowserURL.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.webBrowserURL_KeyPress);
   this.webBrowserGo.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
   this.webBrowserGo.Image = global::WorldWind.Properties.Resources.go;
   this.webBrowserGo.ImageTransparentColor = System.Drawing.Color.Magenta;
   this.webBrowserGo.Name = "webBrowserGo";
   this.webBrowserGo.Size = new System.Drawing.Size(23, 22);
   this.webBrowserGo.Text = "Go";
   this.webBrowserGo.Click += new System.EventHandler(this.webBrowserGo_Click);
   this.webBrowserStop.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
   this.webBrowserStop.Image = global::WorldWind.Properties.Resources.stop;
   this.webBrowserStop.ImageTransparentColor = System.Drawing.Color.Magenta;
   this.webBrowserStop.Name = "webBrowserStop";
   this.webBrowserStop.Size = new System.Drawing.Size(23, 22);
   this.webBrowserStop.Text = "Stop";
   this.webBrowserStop.Click += new System.EventHandler(this.webBrowserStop_Click);
   this.Controls.Add(this.webBrowserToolStrip);
   this.Controls.Add(this.webBrowser);
   this.webBrowserToolStrip.ResumeLayout(false);
   this.webBrowserToolStrip.PerformLayout();
   this.ResumeLayout(false);
  }
  private void webBrowserBack_Click(object sender, EventArgs e)
  {
   webBrowser.GoBack();
  }
  private void webBrowserForward_Click(object sender, EventArgs e)
  {
   webBrowser.GoForward();
  }
  private void webBrowserGo_Click(object sender, EventArgs e)
  {
   webBrowser.Navigate(webBrowserURL.Text);
  }
  private void webBrowserStop_Click(object sender, EventArgs e)
  {
   webBrowser.Stop();
  }
  private void webBrowser_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
  {
   webBrowserURL.Text = webBrowser.Url.ToString();
  }
  private void webBrowserURL_KeyPress(object sender, KeyPressEventArgs e)
  {
   if (e.KeyChar == (char)13)
   {
    webBrowser.Navigate(webBrowserURL.Text);
   }
  }
  private void Resized(object sender, EventArgs e)
  {
   this.webBrowser.Width = this.Width;
   this.webBrowser.Height = this.Height - this.webBrowserToolStrip.Height;
  }
 }
}
