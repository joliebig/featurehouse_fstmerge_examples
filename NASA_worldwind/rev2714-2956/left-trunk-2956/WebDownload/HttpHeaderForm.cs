using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
namespace WorldWind.Net.Monitor
{
 internal class ProgressDetailForm : System.Windows.Forms.Form
 {
  private System.Windows.Forms.TextBox description;
  private System.ComponentModel.Container components = null;
  public ProgressDetailForm( DebugItem item )
  {
   InitializeComponent();
   this.Text = item.Url;
   this.description.Text = item.ToString();
   this.description.SelectionLength = 0;
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
   this.description = new System.Windows.Forms.TextBox();
   this.SuspendLayout();
   this.description.Dock = System.Windows.Forms.DockStyle.Fill;
   this.description.Location = new System.Drawing.Point(0, 0);
   this.description.Multiline = true;
   this.description.Name = "description";
   this.description.ReadOnly = true;
   this.description.Size = new System.Drawing.Size(552, 277);
   this.description.TabIndex = 0;
   this.description.Text = "";
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.ClientSize = new System.Drawing.Size(552, 277);
   this.Controls.Add(this.description);
   this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.SizableToolWindow;
   this.KeyPreview = true;
   this.Name = "ProgressDetailForm";
   this.Text = "HTTP Headers";
   this.ResumeLayout(false);
  }
 }
}
