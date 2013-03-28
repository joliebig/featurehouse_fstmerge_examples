using System;
using System.Drawing;
using System.Threading;
using System.Windows.Forms;
namespace RssBandit.WinGui.Forms
{
 public class SplashScreen : System.Windows.Forms.Form
 {
  private string statusInfo = String.Empty;
  private string versionInfo = String.Empty;
  private RectangleF rectStatus, rectVersion = RectangleF.Empty;
  private readonly Font statusFont = new Font("Tahoma", 8, FontStyle.Regular);
  private System.Windows.Forms.Label labelSlogan;
  private readonly Font versionFont = new Font("Tahoma", 8, FontStyle.Bold);
  public string StatusInfo {
   set { statusInfo = value; ApplyChanges(); }
   get { return statusInfo; }
  }
  public string VersionInfo {
   set { versionInfo = value; ApplyChanges(); }
   get { return versionInfo; }
  }
  private void ApplyChanges() {
   try {
    if (this.InvokeRequired) {
     this.Invoke(new MethodInvoker(this.Invalidate));
     return;
    }
    this.Invalidate();
   }
   catch {
   }
  }
  private System.ComponentModel.Container components = null;
  public SplashScreen()
  {
   InitializeComponent();
   InitDrawingRectangles();
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
   System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(SplashScreen));
   this.labelSlogan = new System.Windows.Forms.Label();
   this.SuspendLayout();
   this.labelSlogan.AccessibleDescription = resources.GetString("labelSlogan.AccessibleDescription");
   this.labelSlogan.AccessibleName = resources.GetString("labelSlogan.AccessibleName");
   this.labelSlogan.Anchor = ((System.Windows.Forms.AnchorStyles)(resources.GetObject("labelSlogan.Anchor")));
   this.labelSlogan.AutoSize = ((bool)(resources.GetObject("labelSlogan.AutoSize")));
   this.labelSlogan.BackColor = System.Drawing.Color.Transparent;
   this.labelSlogan.Dock = ((System.Windows.Forms.DockStyle)(resources.GetObject("labelSlogan.Dock")));
   this.labelSlogan.Enabled = ((bool)(resources.GetObject("labelSlogan.Enabled")));
   this.labelSlogan.Font = ((System.Drawing.Font)(resources.GetObject("labelSlogan.Font")));
   this.labelSlogan.ForeColor = System.Drawing.SystemColors.WindowText;
   this.labelSlogan.Image = ((System.Drawing.Image)(resources.GetObject("labelSlogan.Image")));
   this.labelSlogan.ImageAlign = ((System.Drawing.ContentAlignment)(resources.GetObject("labelSlogan.ImageAlign")));
   this.labelSlogan.ImageIndex = ((int)(resources.GetObject("labelSlogan.ImageIndex")));
   this.labelSlogan.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("labelSlogan.ImeMode")));
   this.labelSlogan.Location = ((System.Drawing.Point)(resources.GetObject("labelSlogan.Location")));
   this.labelSlogan.Name = "labelSlogan";
   this.labelSlogan.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("labelSlogan.RightToLeft")));
   this.labelSlogan.Size = ((System.Drawing.Size)(resources.GetObject("labelSlogan.Size")));
   this.labelSlogan.TabIndex = ((int)(resources.GetObject("labelSlogan.TabIndex")));
   this.labelSlogan.Text = resources.GetString("labelSlogan.Text");
   this.labelSlogan.TextAlign = ((System.Drawing.ContentAlignment)(resources.GetObject("labelSlogan.TextAlign")));
   this.labelSlogan.UseMnemonic = false;
   this.labelSlogan.Visible = ((bool)(resources.GetObject("labelSlogan.Visible")));
   this.AccessibleDescription = resources.GetString("$this.AccessibleDescription");
   this.AccessibleName = resources.GetString("$this.AccessibleName");
   this.AutoScaleBaseSize = ((System.Drawing.Size)(resources.GetObject("$this.AutoScaleBaseSize")));
   this.AutoScroll = ((bool)(resources.GetObject("$this.AutoScroll")));
   this.AutoScrollMargin = ((System.Drawing.Size)(resources.GetObject("$this.AutoScrollMargin")));
   this.AutoScrollMinSize = ((System.Drawing.Size)(resources.GetObject("$this.AutoScrollMinSize")));
   this.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("$this.BackgroundImage")));
   this.ClientSize = ((System.Drawing.Size)(resources.GetObject("$this.ClientSize")));
   this.Controls.Add(this.labelSlogan);
   this.Enabled = ((bool)(resources.GetObject("$this.Enabled")));
   this.Font = ((System.Drawing.Font)(resources.GetObject("$this.Font")));
   this.ForeColor = System.Drawing.SystemColors.WindowText;
   this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
   this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
   this.ImeMode = ((System.Windows.Forms.ImeMode)(resources.GetObject("$this.ImeMode")));
   this.Location = ((System.Drawing.Point)(resources.GetObject("$this.Location")));
   this.MaximumSize = ((System.Drawing.Size)(resources.GetObject("$this.MaximumSize")));
   this.MinimumSize = ((System.Drawing.Size)(resources.GetObject("$this.MinimumSize")));
   this.Name = "SplashScreen";
   this.RightToLeft = ((System.Windows.Forms.RightToLeft)(resources.GetObject("$this.RightToLeft")));
   this.ShowInTaskbar = false;
   this.StartPosition = ((System.Windows.Forms.FormStartPosition)(resources.GetObject("$this.StartPosition")));
   this.Text = resources.GetString("$this.Text");
   this.TransparencyKey = System.Drawing.Color.Magenta;
   this.Paint += new System.Windows.Forms.PaintEventHandler(this.OnFormPaint);
   this.ResumeLayout(false);
  }
  private void OnFormPaint(object sender, System.Windows.Forms.PaintEventArgs e) {
   Graphics g = e.Graphics;
   StringFormat f = new StringFormat();
   if (Win32.IsOSAtLeastWindowsXP) {
    g.TextRenderingHint = System.Drawing.Text.TextRenderingHint.SystemDefault;
   } else {
    g.TextRenderingHint = System.Drawing.Text.TextRenderingHint.AntiAliasGridFit;
   }
   f.Alignment = StringAlignment.Far;
   f.LineAlignment = StringAlignment.Near;
   g.DrawString(versionInfo, versionFont, new SolidBrush(this.ForeColor), rectVersion , f);
   f.Alignment = StringAlignment.Near;
   f.LineAlignment = StringAlignment.Near;
   g.DrawString(statusInfo, statusFont, new SolidBrush(this.ForeColor), rectStatus , f);
  }
  private void InitDrawingRectangles() {
   int boxWidth = this.ClientSize.Width-20;
   rectVersion = new RectangleF(new Point(10, 190),
    new Size(boxWidth, Convert.ToInt32(versionFont.Size) + 20));
   rectStatus = new RectangleF(new Point(10, 10),
    new Size(boxWidth, Convert.ToInt32(statusFont.Size * 2) + 25));
  }
 }
 public class Splash {
  static SplashScreen MySplashForm = null;
  static Thread MySplashThread = null;
  static void ShowThread() {
            MySplashForm = new SplashScreen();
   Application.Run(MySplashForm);
  }
  static public void Show() {
   if (MySplashThread != null)
    return;
   MySplashThread = new Thread(Splash.ShowThread);
   MySplashThread.CurrentCulture = Thread.CurrentThread.CurrentCulture;
   MySplashThread.CurrentUICulture = Thread.CurrentThread.CurrentUICulture;
   MySplashThread.IsBackground = true;
   MySplashThread.SetApartmentState(ApartmentState.STA);
   MySplashThread.Start();
   while (MySplashForm == null) Thread.Sleep(new TimeSpan(100));
  }
  static public void Close() {
   if (MySplashThread == null) return;
   if (MySplashForm == null) return;
   try {
    MySplashForm.Invoke(new MethodInvoker(MySplashForm.Close));
   }
   catch (Exception) {
   }
   MySplashThread = null;
   MySplashForm = null;
  }
  static public string Status {
   set {
    if (MySplashForm == null) {
     return;
    }
    MySplashForm.StatusInfo = value;
   }
   get {
    if (MySplashForm == null) {
     throw new InvalidOperationException("Splash Form not on screen");
    }
    return MySplashForm.StatusInfo;
   }
  }
  static public string Version {
   set {
    if (MySplashForm == null) {
     return;
    }
    MySplashForm.VersionInfo = value;
   }
   get {
    if (MySplashForm == null) {
     throw new InvalidOperationException("Splash Form not on screen");
    }
    return MySplashForm.VersionInfo;
   }
  }
 }
}
