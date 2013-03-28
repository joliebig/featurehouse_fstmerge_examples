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
  private System.Windows.Forms.Label label;
  private System.Windows.Forms.PictureBox pictureBox;
  private System.ComponentModel.Container components = null;
  public Splash()
  {
   InitializeComponent();
   defaultHeight = this.Height;
   FileInfo splashImageFile = new FileInfo(
    Path.GetDirectoryName(Application.ExecutablePath) + "\\Data\\Icons\\Interface\\splash.png");
   if(splashImageFile.Exists)
   {
    pictureBox.Image = Image.FromFile(splashImageFile.FullName);
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
     label.TextAlign = ContentAlignment.MiddleCenter;
     label.BorderStyle = BorderStyle.Fixed3D;
    }
    else
    {
     Height = defaultHeight;
     label.TextAlign = ContentAlignment.MiddleLeft;
     label.BorderStyle = BorderStyle.None;
    }
   }
  }
  public void SetText(string message)
  {
   if(hasError)
    Wait();
   HasError = false;
   this.label.Text = message;
   this.label.ForeColor = Color.Black;
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
   this.label.Text = message + "\n\nPress any key or click to continue.";
   this.label.ForeColor = Color.Red;
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
   this.label = new System.Windows.Forms.Label();
   this.pictureBox = new System.Windows.Forms.PictureBox();
   this.SuspendLayout();
   this.label.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
    | System.Windows.Forms.AnchorStyles.Left)
    | System.Windows.Forms.AnchorStyles.Right)));
   this.label.Location = new System.Drawing.Point(8, 261);
   this.label.Name = "label";
   this.label.Size = new System.Drawing.Size(504, 27);
   this.label.TabIndex = 3;
   this.label.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
   this.label.MouseDown += new System.Windows.Forms.MouseEventHandler(this.Splash_MouseDown);
   this.pictureBox.BackColor = System.Drawing.Color.White;
   this.pictureBox.Location = new System.Drawing.Point(3, 3);
   this.pictureBox.Name = "pictureBox";
   this.pictureBox.Size = new System.Drawing.Size(512, 253);
   this.pictureBox.TabIndex = 2;
   this.pictureBox.TabStop = false;
   this.pictureBox.MouseDown += new System.Windows.Forms.MouseEventHandler(this.Splash_MouseDown);
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.BackColor = System.Drawing.Color.White;
   this.ClientSize = new System.Drawing.Size(520, 296);
   this.ControlBox = false;
   this.Controls.Add(this.label);
   this.Controls.Add(this.pictureBox);
   this.ForeColor = System.Drawing.SystemColors.ControlText;
   this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
   this.KeyPreview = true;
   this.MaximizeBox = false;
   this.Name = "Splash";
   this.ShowInTaskbar = false;
   this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
   this.Text = "Splash";
   this.TransparencyKey = System.Drawing.Color.FromArgb(((System.Byte)(192)), ((System.Byte)(0)), ((System.Byte)(0)));
   this.MouseDown += new System.Windows.Forms.MouseEventHandler(this.Splash_MouseDown);
   this.ResumeLayout(false);
  }
  protected override void OnPaint(PaintEventArgs e)
  {
   base.OnPaint(e);
   ControlPaint.DrawBorder3D(e.Graphics, 0,0,(int)e.Graphics.VisibleClipBounds.Width, (int)e.Graphics.VisibleClipBounds.Height, Border3DStyle.Raised);
  }
 }
}
