using System; 
using System.Diagnostics; 
using System.Drawing; 
using System.Globalization; 
using System.IO; 
using System.Net; 
using System.Text; 
using System.Windows.Forms; 
using System.Xml; 
using Genghis.Windows.Forms; 
using NewsComponents; 
using NewsComponents.Net; 
using NewsComponents.Utils; 
using RssBandit.Common; 
using RssBandit.Common.Logging; 
using RssBandit.Resources; namespace  RssBandit.WinGui.Dialogs {
	
 internal class  FeedSourceDialog  : RssBandit.WinGui.Dialogs.DialogBase {
		
  private  string feedUrl;
 
  private  ICredentials feedCredentials;
 
  private  IWebProxy proxy;
 
  private  System.Windows.Forms.RichTextBox txtSource;
 
  private  System.Timers.Timer timer;
 
  private  System.ComponentModel.IContainer components = null;
 
  public  FeedSourceDialog(IWebProxy proxy, ICredentials feedCredentials, string sourceUrl, string title)
  {
   this.feedUrl = sourceUrl;
   this.feedCredentials = feedCredentials;
   this.proxy = proxy;
   InitializeComponent();
   this.Text = title;
   this.txtSource.Text = SR.GUIStatusLoadingChildItems;
   base.horizontalEdge.Visible = false;
   base.btnSubmit.Visible = false;
   base.btnCancel.Visible = false;
   this.KeyPreview = true;
  }
 
  private  Stream GetFeedSourceStream() {
   return AsyncWebRequest.GetSyncResponseStream(this.feedUrl, this.feedCredentials, RssBanditApplication.UserAgent, this.proxy);
  }
 
  private  void LoadAndFormatFeedSource() {
   try {
    using (new CursorChanger(Cursors.WaitCursor)) {
     using (StreamReader reader = new StreamReader(GetFeedSourceStream())) {
      XmlDocument doc = new XmlDocument();
      doc.Load(reader);
      StringBuilder sb = new StringBuilder();
      XmlTextWriter writer = new XmlTextWriter(new StringWriter(sb));
      writer.Formatting = Formatting.Indented;
      writer.Indentation = 4;
      writer.IndentChar = ' ';
      doc.Save(writer);
      this.txtSource.Text = sb.ToString();
      this.txtSource.ScrollBars = RichTextBoxScrollBars.ForcedBoth;
     }
    }
   } catch (Exception ex) {
    this.txtSource.Text = SR.ExceptionGeneral(ex.Message);
   }
  }
 
  string FontToString(Font font) {
   FontConverter oFontConv = new FontConverter();
   return oFontConv.ConvertToString(null,CultureInfo.InvariantCulture,font);
  }
 
  Font StringToFont(string name, Font defaultValue) {
   if (string.IsNullOrEmpty(name))
    return defaultValue;
   try {
     FontConverter oFontConv = new FontConverter();
     return oFontConv.ConvertFromString(null, CultureInfo.InvariantCulture, name) as Font;
   } catch {
    return defaultValue;
   }
  }
 
  protected override  void Dispose( bool disposing )
  {
   if( disposing )
   {
    if (components != null)
    {
     components.Dispose();
    }
   }
   base.Dispose( disposing );
  }
 
  private  void InitializeComponent()
  {
   this.txtSource = new System.Windows.Forms.RichTextBox();
   this.timer = new System.Timers.Timer();
   ((System.ComponentModel.ISupportInitialize)(this.timer)).BeginInit();
   this.SuspendLayout();
   this.btnCancel.Name = "btnCancel";
   this.btnCancel.Visible = false;
   this.horizontalEdge.Name = "horizontalEdge";
   this.horizontalEdge.Visible = false;
   this.btnSubmit.Name = "btnSubmit";
   this.btnSubmit.Visible = false;
   this.windowSerializer.LoadStateEvent += new RssBandit.WinGui.Controls.WindowSerializer.WindowSerializerDelegate(this.OnLoadState);
   this.windowSerializer.SaveStateEvent += new RssBandit.WinGui.Controls.WindowSerializer.WindowSerializerDelegate(this.OnSaveState);
   this.txtSource.Dock = System.Windows.Forms.DockStyle.Fill;
   this.txtSource.HideSelection = false;
   this.txtSource.Location = new System.Drawing.Point(0, 0);
   this.txtSource.Name = "txtSource";
   this.txtSource.ReadOnly = true;
   this.txtSource.ShowSelectionMargin = true;
   this.txtSource.Size = new System.Drawing.Size(387, 251);
   this.txtSource.TabIndex = 102;
   this.txtSource.Text = "";
   this.txtSource.WordWrap = false;
   this.txtSource.MouseDown += new System.Windows.Forms.MouseEventHandler(this.OnTxtMouseDown);
   this.txtSource.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.OnTxtKeyPress);
   this.txtSource.LinkClicked += new System.Windows.Forms.LinkClickedEventHandler(this.OnLinkClicked);
   this.timer.SynchronizingObject = this;
   this.timer.Elapsed += new System.Timers.ElapsedEventHandler(this.OnTimerElapsed);
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 14);
   this.ClientSize = new System.Drawing.Size(387, 251);
   this.Controls.Add(this.txtSource);
   this.KeyPreview = true;
   this.MaximizeBox = true;
   this.Name = "FeedSourceDialog";
   this.ShowInTaskbar = true;
   this.Load += new System.EventHandler(this.OnFormLoad);
   this.Controls.SetChildIndex(this.txtSource, 0);
   this.Controls.SetChildIndex(this.btnSubmit, 0);
   this.Controls.SetChildIndex(this.btnCancel, 0);
   this.Controls.SetChildIndex(this.horizontalEdge, 0);
   ((System.ComponentModel.ISupportInitialize)(this.timer)).EndInit();
   this.ResumeLayout(false);
  }
 
  private  void OnTimerElapsed(object sender, System.Timers.ElapsedEventArgs e) {
   this.timer.Enabled = false;
   LoadAndFormatFeedSource();
  }
 
  private  void OnFormLoad(object sender, System.EventArgs e) {
   this.timer.Enabled = true;
  }
 
  private  void OnLinkClicked(object sender, System.Windows.Forms.LinkClickedEventArgs e) {
   try {
    if (!string.IsNullOrEmpty(e.LinkText)) {
     Uri uri = new Uri(e.LinkText);
     Process.Start(uri.CanonicalizedUri());
    }
   } catch (Exception ex) {
    Log.Error("Cannot navigate to url '" + e.LinkText + "'", ex);
   }
  }
 
  private  void OnTxtMouseDown(object sender, System.Windows.Forms.MouseEventArgs e) {
   if (Control.MouseButtons == MouseButtons.Left) {
    try {
     if (Control.ModifierKeys == Keys.Alt) {
      using (FontDialog fntDialog = new FontDialog() ) {
       fntDialog.Font = this.txtSource.Font;
       if (DialogResult.OK == fntDialog.ShowDialog(this)) {
        this.txtSource.Font = fntDialog.Font;
       }
      }
     } else
     if (Control.ModifierKeys == Keys.Control) {
      this.txtSource.ZoomFactor += 0.25f;
     } else if (Control.ModifierKeys == (Keys.Control | Keys.Shift)) {
      this.txtSource.ZoomFactor -= 0.25f;
     }
    } catch (ArgumentException) {
    }
   }
  }
 
  private  void OnLoadState(object sender, Genghis.Preferences preferences) {
   this.txtSource.Font = StringToFont(preferences.GetString("fnt", null), this.txtSource.Font);
   this.txtSource.ZoomFactor = preferences.GetSingle("zoom", 1.0f);
  }
 
  private  void OnSaveState(object sender, Genghis.Preferences preferences) {
   preferences.SetProperty("fnt", FontToString(this.txtSource.Font));
   preferences.SetProperty("zoom", this.txtSource.ZoomFactor);
  }
 
  private  void OnTxtKeyPress(object sender, System.Windows.Forms.KeyPressEventArgs e) {
   if (Control.ModifierKeys == Keys.Control && (e.KeyChar == 'a' || e.KeyChar == 'A')) {
    this.txtSource.SelectAll();
    e.Handled = true;
   }
  }

	}

}
