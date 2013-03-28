using System;
using System.Windows.Forms;
using Infragistics.Win;
using Infragistics.Win.Misc;
namespace RssBandit.WinGui.Controls
{
 public class VerticalHeaderLabel : UltraLabel
 {
  public event EventHandler ImageClick;
  private Cursor _saveCursorImageArea;
  private System.ComponentModel.Container components = null;
  public VerticalHeaderLabel()
  {
   InitializeComponent();
   this._saveCursorImageArea = null;
   this.DrawFilter = new SmoothLabelDrawFilter(this, true);
   this.MouseEnterElement += new Infragistics.Win.UIElementEventHandler(VerticalHeaderLabel_MouseEnterElement);
   this.MouseLeaveElement += new Infragistics.Win.UIElementEventHandler(VerticalHeaderLabel_MouseLeaveElement);
   this.Click += new System.EventHandler(VerticalHeaderLabel_Click);
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
   this.Name = "VerticalHeaderLabel";
   this.Size = new System.Drawing.Size(25, 245);
  }
  private void VerticalHeaderLabel_MouseEnterElement(object sender, Infragistics.Win.UIElementEventArgs e) {
   if (this.Appearance.Image != null &&
    e.Element is ImageAndTextUIElement.ImageAndTextDependentImageUIElement) {
    this._saveCursorImageArea = this.Cursor;
    this.Cursor = Cursors.Hand;
   }
  }
  private void VerticalHeaderLabel_MouseLeaveElement(object sender, Infragistics.Win.UIElementEventArgs e) {
   if (this.Appearance.Image != null &&
    e.Element is ImageAndTextUIElement.ImageAndTextDependentImageUIElement) {
    if (this._saveCursorImageArea != null)
     this.Cursor = this._saveCursorImageArea;
    else
     this.Cursor = Cursors.Default;
    this._saveCursorImageArea = null;
   }
  }
  private void VerticalHeaderLabel_Click(object sender, System.EventArgs e) {
   if (this._saveCursorImageArea != null)
    OnImageClick();
  }
  protected void OnImageClick() {
   if (ImageClick != null)
    ImageClick(this, EventArgs.Empty);
  }
 }
}
