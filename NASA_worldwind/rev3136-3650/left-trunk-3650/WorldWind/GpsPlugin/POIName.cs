using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
namespace GpsTrackerPlugin
{
 public class POIName : System.Windows.Forms.Form
 {
  GPSTrackerOverlay m_gpsOverlay;
  private System.Windows.Forms.Label label1;
  private System.Windows.Forms.Button buttonOK;
  private System.Windows.Forms.Label label2;
  private System.Windows.Forms.TextBox textBoxPOIName;
  private System.ComponentModel.Container components = null;
  public POIName(GPSTrackerOverlay gpsOverlay)
  {
   m_gpsOverlay = gpsOverlay;
   InitializeComponent();
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
   this.label1 = new System.Windows.Forms.Label();
   this.textBoxPOIName = new System.Windows.Forms.TextBox();
   this.buttonOK = new System.Windows.Forms.Button();
   this.label2 = new System.Windows.Forms.Label();
   this.SuspendLayout();
   this.label1.Location = new System.Drawing.Point(0, 8);
   this.label1.Name = "label1";
   this.label1.Size = new System.Drawing.Size(128, 16);
   this.label1.TabIndex = 0;
   this.label1.Text = "Please enter POI Name:";
   this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
   this.textBoxPOIName.Location = new System.Drawing.Point(128, 8);
   this.textBoxPOIName.Name = "textBoxPOIName";
   this.textBoxPOIName.Size = new System.Drawing.Size(136, 20);
   this.textBoxPOIName.TabIndex = 1;
   this.textBoxPOIName.Text = "";
   this.buttonOK.Location = new System.Drawing.Point(272, 8);
   this.buttonOK.Name = "buttonOK";
   this.buttonOK.Size = new System.Drawing.Size(32, 20);
   this.buttonOK.TabIndex = 2;
   this.buttonOK.Text = "OK";
   this.buttonOK.Click += new System.EventHandler(this.buttonOK_Click);
   this.label2.Location = new System.Drawing.Point(16, 32);
   this.label2.Name = "label2";
   this.label2.Size = new System.Drawing.Size(248, 16);
   this.label2.TabIndex = 3;
   this.label2.Text = " (Leave blank for a GPSTracker assigned name)";
   this.label2.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.ClientSize = new System.Drawing.Size(314, 58);
   this.ControlBox = false;
   this.Controls.Add(this.label2);
   this.Controls.Add(this.buttonOK);
   this.Controls.Add(this.textBoxPOIName);
   this.Controls.Add(this.label1);
   this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
   this.KeyPreview = true;
   this.MaximizeBox = false;
   this.MinimizeBox = false;
   this.Name = "POIName";
   this.ShowInTaskbar = false;
   this.Text = "GPSTracker :: New POI Name";
   this.TopMost = true;
   this.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.POIName_KeyPress);
   this.Load += new System.EventHandler(this.POIName_Load);
   this.ResumeLayout(false);
  }
  private void buttonOK_Click(object sender, System.EventArgs e)
  {
   m_gpsOverlay.m_sPOIName = textBoxPOIName.Text;
   Close();
  }
  private void POIName_KeyPress(object sender, System.Windows.Forms.KeyPressEventArgs e)
  {
   if (e.KeyChar==27)
    Close();
   else
   if (e.KeyChar=='\n' || e.KeyChar=='\r')
   {
    m_gpsOverlay.m_sPOIName = textBoxPOIName.Text;
    Close();
   }
  }
  private void POIName_Load(object sender, System.EventArgs e)
  {
   this.Top=Control.MousePosition.Y;
   this.Left=Control.MousePosition.X;
   if (this.Top + this.Height > Screen.PrimaryScreen.WorkingArea.Bottom)
    this.Top=Screen.PrimaryScreen.WorkingArea.Bottom-this.Height-5;
   if (this.Left + this.Width > Screen.PrimaryScreen.WorkingArea.Right)
    this.Left=Screen.PrimaryScreen.WorkingArea.Right-this.Width-5;
  }
 }
}
