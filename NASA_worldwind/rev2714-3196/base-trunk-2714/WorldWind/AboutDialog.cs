using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using WorldWind;
namespace WorldWind
{
 public class AboutDialog : System.Windows.Forms.Form
 {
  private System.Windows.Forms.Button buttonClose;
  private System.Windows.Forms.Label labelVersion;
  private System.Windows.Forms.Label labelVersionNumber;
  private System.Windows.Forms.Label label1;
  private System.Windows.Forms.Label labelWorldWindowVersionNumber;
  private System.Windows.Forms.PictureBox pictureBox;
  private System.Windows.Forms.Button button1;
  private System.Windows.Forms.Label labelProductVersion;
  public AboutDialog(WorldWindow ww)
  {
   InitializeComponent();
   this.labelVersionNumber.Text = Application.ProductVersion;
   this.labelWorldWindowVersionNumber.Text = ww.ProductVersion;
   this.pictureBox.Image = Splash.GetStartupImage();
  }
  private void InitializeComponent()
  {
   this.buttonClose = new System.Windows.Forms.Button();
   this.pictureBox = new System.Windows.Forms.PictureBox();
   this.labelVersion = new System.Windows.Forms.Label();
   this.labelVersionNumber = new System.Windows.Forms.Label();
   this.label1 = new System.Windows.Forms.Label();
   this.labelWorldWindowVersionNumber = new System.Windows.Forms.Label();
   this.labelProductVersion = new System.Windows.Forms.Label();
   this.button1 = new System.Windows.Forms.Button();
   this.SuspendLayout();
   this.buttonClose.DialogResult = System.Windows.Forms.DialogResult.Cancel;
   this.buttonClose.Location = new System.Drawing.Point(120, 328);
   this.buttonClose.Name = "buttonClose";
   this.buttonClose.Size = new System.Drawing.Size(96, 32);
   this.buttonClose.TabIndex = 0;
   this.buttonClose.Text = "OK";
   this.pictureBox.Cursor = System.Windows.Forms.Cursors.Hand;
   this.pictureBox.Location = new System.Drawing.Point(13, 14);
   this.pictureBox.Name = "pictureBox";
   this.pictureBox.Size = new System.Drawing.Size(512, 256);
   this.pictureBox.TabIndex = 1;
   this.pictureBox.TabStop = false;
   this.pictureBox.Click += new System.EventHandler(this.pictureBox_Click);
   this.labelVersion.Location = new System.Drawing.Point(15, 278);
   this.labelVersion.Name = "labelVersion";
   this.labelVersion.Size = new System.Drawing.Size(112, 23);
   this.labelVersion.TabIndex = 2;
   this.labelVersion.Text = "World Wind Version:";
   this.labelVersionNumber.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
   this.labelVersionNumber.Location = new System.Drawing.Point(151, 278);
   this.labelVersionNumber.Name = "labelVersionNumber";
   this.labelVersionNumber.Size = new System.Drawing.Size(168, 18);
   this.labelVersionNumber.TabIndex = 3;
   this.label1.Location = new System.Drawing.Point(15, 301);
   this.label1.Name = "label1";
   this.label1.Size = new System.Drawing.Size(129, 23);
   this.label1.TabIndex = 4;
   this.label1.Text = "World Window Version:";
   this.labelWorldWindowVersionNumber.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
   this.labelWorldWindowVersionNumber.Location = new System.Drawing.Point(151, 301);
   this.labelWorldWindowVersionNumber.Name = "labelWorldWindowVersionNumber";
   this.labelWorldWindowVersionNumber.Size = new System.Drawing.Size(176, 18);
   this.labelWorldWindowVersionNumber.TabIndex = 5;
   this.labelProductVersion.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
   this.labelProductVersion.Location = new System.Drawing.Point(424, 278);
   this.labelProductVersion.Name = "labelProductVersion";
   this.labelProductVersion.Size = new System.Drawing.Size(96, 24);
   this.labelProductVersion.TabIndex = 6;
   this.labelProductVersion.TextAlign = System.Drawing.ContentAlignment.TopRight;
   this.button1.Location = new System.Drawing.Point(320, 328);
   this.button1.Name = "button1";
   this.button1.Size = new System.Drawing.Size(96, 32);
   this.button1.TabIndex = 1;
   this.button1.Text = "Credits";
   this.button1.Click += new System.EventHandler(this.button1_Click);
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.ClientSize = new System.Drawing.Size(540, 369);
   this.Controls.Add(this.button1);
   this.Controls.Add(this.labelProductVersion);
   this.Controls.Add(this.labelWorldWindowVersionNumber);
   this.Controls.Add(this.label1);
   this.Controls.Add(this.labelVersionNumber);
   this.Controls.Add(this.labelVersion);
   this.Controls.Add(this.pictureBox);
   this.Controls.Add(this.buttonClose);
   this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.Fixed3D;
   this.KeyPreview = true;
   this.MaximizeBox = false;
   this.MinimizeBox = false;
   this.Name = "AboutDialog";
   this.ShowInTaskbar = false;
   this.Text = "About World Wind";
   this.ResumeLayout(false);
  }
  protected override void OnKeyUp(System.Windows.Forms.KeyEventArgs e)
  {
   switch(e.KeyCode)
   {
    case Keys.Escape:
     Close();
     e.Handled = true;
     break;
    case Keys.F4:
     if(e.Modifiers==Keys.Control)
     {
      Close();
      e.Handled = true;
     }
     break;
   }
   base.OnKeyUp(e);
  }
  private void pictureBox_Click(object sender, System.EventArgs e)
  {
   MainApplication.BrowseTo( MainApplication.WebsiteUrl );
  }
  private void button1_Click(object sender, System.EventArgs e)
  {
  MainApplication.BrowseTo( MainApplication.CreditsWebsiteUrl );
  }
 }
}
