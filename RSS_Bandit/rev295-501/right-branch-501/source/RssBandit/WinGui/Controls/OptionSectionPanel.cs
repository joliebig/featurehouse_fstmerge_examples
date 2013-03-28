using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;
namespace RssBandit.WinGui.Controls
{
 [DefaultProperty("Text")]
 public class OptionSectionPanel : System.Windows.Forms.Panel
 {
  private Color _highlight = Color.FromKnownColor( KnownColor.ControlLightLight );
  private Color _shadow = Color.FromKnownColor( KnownColor.ControlDark );
  private Image _image = null;
  private Point _imageLocation;
  private System.ComponentModel.IContainer components = null;
  public OptionSectionPanel()
  {
   _imageLocation = new Point(0, 20);
   SetStyle(ControlStyles.DoubleBuffer, true);
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
  protected override void OnPaintBackground(PaintEventArgs pe) {
   base.OnPaintBackground (pe);
   StringFormat sf = new StringFormat(StringFormatFlags.MeasureTrailingSpaces|StringFormatFlags.NoWrap);
   Graphics g = pe.Graphics;
   SizeF lt = g.MeasureString(this.Text + " ", this.Font, this.Width, sf);
   float lth = lt.Height / 2;
   Brush h = new SolidBrush( _highlight );
   Pen ph = new Pen(h, 1.0f);
   Brush s = new SolidBrush( _shadow );
   Pen ps = new Pen(s, 1.0f);
   g.TextRenderingHint = System.Drawing.Text.TextRenderingHint.SystemDefault;
   g.DrawString(this.Text, this.Font, SystemBrushes.FromSystemColor(this.ForeColor) , new PointF(0,0), sf);
   g.DrawLine( ps, lt.Width, lth, Width, lth );
   g.DrawLine( ph, lt.Width, lth + 1.0f, Width, lth + 1.0f);
   if (this._image != null)
    g.DrawImage(this._image, _imageLocation.X, _imageLocation.Y, this._image.Width, this._image.Height);
   sf.Dispose();
   ph.Dispose();
   ps.Dispose();
   h.Dispose();
   s.Dispose();
  }
  private void InitializeComponent()
  {
   this.Size = new System.Drawing.Size(220, 150);
  }
  [
  Browsable(true), DesignerSerializationVisibility(DesignerSerializationVisibility.Visible),
  DefaultValue(null),
  Category("Appearance")
  ]
  public Image Image {
   get { return this._image; }
   set {
    this._image = value;
    this.Invalidate();
   }
  }
  [
  Browsable(true), Localizable(true), DesignerSerializationVisibility(DesignerSerializationVisibility.Visible),
  DefaultValue(null),
  Category("Appearance")
  ]
  public override string Text {
   get {
    return base.Text;
   }
   set {
    base.Text = value;
    this.Invalidate();
   }
  }
  [
  Browsable(true), DesignerSerializationVisibility(DesignerSerializationVisibility.Visible),
  Category("Layout")
  ]
  public Point ImageLocation {
   get {
    return _imageLocation;
   }
   set {
    _imageLocation = value;
    this.Invalidate();
   }
  }
 }
}
