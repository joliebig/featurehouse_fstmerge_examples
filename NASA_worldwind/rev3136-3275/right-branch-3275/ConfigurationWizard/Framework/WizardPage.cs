using System;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;
namespace ConfigurationWizard
{
 public class WizardPage : System.Windows.Forms.UserControl
 {
  private System.Windows.Forms.Panel panel1;
  private string _title;
  private string _subTitle;
  private Font _titleFont = new Font("Arial", 10, FontStyle.Bold);
  protected Wizard wizard;
  private System.ComponentModel.Container components = null;
  public WizardPage()
  {
   InitializeComponent();
  }
  [Browsable(true)]
  public string Title
  {
   get { return _title; }
   set { _title = value; }
  }
  [Browsable(true)]
  public string SubTitle
  {
   get {return _subTitle;}
   set {_subTitle = value;}
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
   if(_titleFont!=null)
   {
    _titleFont.Dispose();
    _titleFont=null;
   }
   base.Dispose( disposing );
  }
  private void InitializeComponent()
  {
   this.panel1 = new System.Windows.Forms.Panel();
   this.SuspendLayout();
   this.panel1.BackColor = System.Drawing.Color.White;
   this.panel1.Location = new System.Drawing.Point(0, 0);
   this.panel1.Name = "panel1";
   this.panel1.Size = new System.Drawing.Size(541, 60);
   this.panel1.TabIndex = 0;
   this.panel1.Paint += new System.Windows.Forms.PaintEventHandler(this.panel1_Paint);
   this.BackColor = System.Drawing.SystemColors.Control;
   this.Controls.Add(this.panel1);
   this.Name = "WizardPage";
   this.Size = new System.Drawing.Size(541, 363);
   this.SizeChanged += new System.EventHandler(this.WizardPage_SizeChanged);
   this.ResumeLayout(false);
  }
  private void WizardPage_SizeChanged(object sender, System.EventArgs e)
  {
   this.Size = new System.Drawing.Size(541, 363);
  }
  private void panel1_Paint(object sender, System.Windows.Forms.PaintEventArgs e)
  {
   ControlPaint.DrawBorder3D(e.Graphics, 0, panel1.Height-2, panel1.Width, 4,Border3DStyle.Sunken);
   e.Graphics.DrawString(_title, _titleFont, Brushes.Black, 17,9);
   e.Graphics.DrawString(_subTitle, Font, Brushes.Black, 38,25);
  }
 }
}
