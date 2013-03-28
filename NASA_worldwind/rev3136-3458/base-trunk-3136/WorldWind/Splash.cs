using System;
using System.IO;
using System.Threading;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Resources;
using System.Windows.Forms;
namespace WorldWind
{
 public class Splash : System.Windows.Forms.Form
 {
  private bool wasClicked;
  private bool hasError;
  private DateTime startTime = DateTime.Now;
  private TimeSpan timeOut = TimeSpan.FromSeconds(1);
        private int defaultHeight;
  private System.ComponentModel.Container components = null;
  public Splash()
  {
   InitializeComponent();
   defaultHeight = this.Height;
   FileInfo splashImageFile = new FileInfo(
    Path.GetDirectoryName(Application.ExecutablePath) + "\\Data\\Icons\\Interface\\splash.png");
   if(splashImageFile.Exists)
   {
   }
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
  public bool HasError
  {
   get
   {
    return hasError;
   }
   set
   {
    hasError = value;
    if(hasError)
    {
     Height = defaultHeight + 80;
    }
    else
    {
     Height = defaultHeight;
    }
   }
  }
  public void SetText(string message)
  {
   if(hasError)
    Wait();
   HasError = false;
   this.Invalidate();
   Application.DoEvents();
  }
  public void SetError(string message)
  {
   if(hasError)
    Wait();
   HasError = true;
   wasClicked = false;
   this.timeOut = TimeSpan.FromSeconds(30);
   this.Invalidate();
   Application.DoEvents();
  }
  public bool IsDone
  {
   get
   {
    Application.DoEvents();
    if (wasClicked)
     return true;
    TimeSpan timeElapsed = System.DateTime.Now - this.startTime;
    return (timeElapsed >= this.timeOut);
   }
  }
  protected void Wait()
  {
   while(!IsDone)
    Thread.Sleep(100);
  }
  protected override void OnKeyUp(KeyEventArgs e)
  {
   wasClicked = true;
   base.OnKeyUp(e);
  }
  private void Splash_MouseDown(object sender, System.Windows.Forms.MouseEventArgs e)
  {
   wasClicked = true;
  }
  public static Image GetStartupImage()
  {
   return Image.FromFile(Path.GetDirectoryName(Application.ExecutablePath) + "\\Data\\Icons\\Interface\\splash.png");
  }
  private static Font LoadFont( string familyName, float emSize, FontStyle newStyle )
  {
   try
   {
    return new Font(familyName, emSize, newStyle );
   }
   catch(ArgumentException)
   {
   }
   return new Font("", emSize, newStyle);
  }
  private void InitializeComponent()
  {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Splash));
            this.SuspendLayout();
            this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
            this.BackColor = System.Drawing.Color.Lime;
            this.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("$this.BackgroundImage")));
            this.ClientSize = new System.Drawing.Size(620, 300);
            this.ControlBox = false;
            this.ForeColor = System.Drawing.SystemColors.ControlText;
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.KeyPreview = true;
            this.MaximizeBox = false;
            this.Name = "Splash";
            this.ShowInTaskbar = false;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Splash";
            this.TransparencyKey = System.Drawing.Color.Lime;
            this.MouseDown += new System.Windows.Forms.MouseEventHandler(this.Splash_MouseDown);
            this.ResumeLayout(false);
  }
 }
}
