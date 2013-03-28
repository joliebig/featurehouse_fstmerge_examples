using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
namespace NASA.Plugins
{
 public class BmngAboutDialog : System.Windows.Forms.Form
 {
  private System.Windows.Forms.PictureBox pictureBox1;
  private System.Windows.Forms.Label label1;
  private System.Windows.Forms.Label label2;
  private System.Windows.Forms.Label label3;
  private System.Windows.Forms.LinkLabel linkLabel1;
  private System.Windows.Forms.LinkLabel linkLabel2;
  private System.Windows.Forms.Label label4;
  private System.Windows.Forms.Label label5;
  private System.Windows.Forms.LinkLabel linkLabel3;
  private System.ComponentModel.Container components = null;
  public BmngAboutDialog()
  {
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
   System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(BmngAboutDialog));
   this.pictureBox1 = new System.Windows.Forms.PictureBox();
   this.label1 = new System.Windows.Forms.Label();
   this.label2 = new System.Windows.Forms.Label();
   this.label3 = new System.Windows.Forms.Label();
   this.linkLabel1 = new System.Windows.Forms.LinkLabel();
   this.linkLabel2 = new System.Windows.Forms.LinkLabel();
   this.label4 = new System.Windows.Forms.Label();
   this.label5 = new System.Windows.Forms.Label();
   this.linkLabel3 = new System.Windows.Forms.LinkLabel();
   this.SuspendLayout();
   this.pictureBox1.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox1.Image")));
   this.pictureBox1.Location = new System.Drawing.Point(8, 8);
   this.pictureBox1.Name = "pictureBox1";
   this.pictureBox1.Size = new System.Drawing.Size(128, 128);
   this.pictureBox1.TabIndex = 0;
   this.pictureBox1.TabStop = false;
   this.label1.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
   this.label1.Location = new System.Drawing.Point(144, 8);
   this.label1.Name = "label1";
   this.label1.Size = new System.Drawing.Size(216, 23);
   this.label1.TabIndex = 1;
   this.label1.Text = "Blue Marble Next-Generation Plugin";
   this.label2.Location = new System.Drawing.Point(144, 32);
   this.label2.Name = "label2";
   this.label2.Size = new System.Drawing.Size(216, 23);
   this.label2.TabIndex = 2;
   this.label2.Text = "Source Imagery Prepared By:";
   this.label3.Location = new System.Drawing.Point(144, 80);
   this.label3.Name = "label3";
   this.label3.Size = new System.Drawing.Size(208, 23);
   this.label3.TabIndex = 3;
   this.label3.Text = "Plugin and Web Services Provided By:";
   this.linkLabel1.Location = new System.Drawing.Point(144, 56);
   this.linkLabel1.Name = "linkLabel1";
   this.linkLabel1.Size = new System.Drawing.Size(216, 23);
   this.linkLabel1.TabIndex = 4;
   this.linkLabel1.TabStop = true;
   this.linkLabel1.Text = "http://bluemarble.nasa.gov";
   this.linkLabel2.Location = new System.Drawing.Point(144, 104);
   this.linkLabel2.Name = "linkLabel2";
   this.linkLabel2.Size = new System.Drawing.Size(200, 23);
   this.linkLabel2.TabIndex = 5;
   this.linkLabel2.TabStop = true;
   this.linkLabel2.Text = "http://worldwind.arc.nasa.gov";
   this.label4.Location = new System.Drawing.Point(8, 144);
   this.label4.Name = "label4";
   this.label4.Size = new System.Drawing.Size(232, 24);
   this.label4.TabIndex = 6;
   this.label4.Text = "For Questions or Comments, Please Contact:";
   this.label5.Location = new System.Drawing.Point(232, 136);
   this.label5.Name = "label5";
   this.label5.Size = new System.Drawing.Size(80, 24);
   this.label5.TabIndex = 7;
   this.label5.Text = "Chris Maxwell";
   this.linkLabel3.Location = new System.Drawing.Point(232, 160);
   this.linkLabel3.Name = "linkLabel3";
   this.linkLabel3.Size = new System.Drawing.Size(128, 23);
   this.linkLabel3.TabIndex = 8;
   this.linkLabel3.TabStop = true;
   this.linkLabel3.Text = "cmaxwell@arc.nasa.gov";
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.ClientSize = new System.Drawing.Size(362, 184);
   this.Controls.Add(this.linkLabel3);
   this.Controls.Add(this.label5);
   this.Controls.Add(this.label4);
   this.Controls.Add(this.linkLabel2);
   this.Controls.Add(this.linkLabel1);
   this.Controls.Add(this.label3);
   this.Controls.Add(this.label2);
   this.Controls.Add(this.label1);
   this.Controls.Add(this.pictureBox1);
   this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
   this.Name = "BmngAboutDialog";
   this.Text = "Blue Marble Next-Generation About Dialog";
   this.ResumeLayout(false);
  }
 }
}
