using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using RssBandit.WinGui.Utility;
namespace RssBandit.WinGui.Controls.CollapsiblePanels
{
 public enum PanelState
 {
  Expanded = 0,
  Collapsed = 1
 }
 public class CollapsiblePanel : Panel
 {
  public event PanelStateChangedEventHandler PanelStateChanged;
  public delegate void PanelStateChangedEventHandler(object sender, PanelEventArgs e);
  private const int IconBorder = 3;
  private const int ExpandBorder = 4;
  private System.Drawing.Imaging.ImageAttributes moImageTranspAttribute;
  private System.Drawing.Imaging.ImageAttributes moImageListTranspAttribute;
  private PanelState meCurrentState;
  private int miExpandedHeight;
  private int miMinTitleHeight;
  private bool mbFixedSize;
  private Point pt = new Point(0,0);
  private System.Drawing.Imaging.ColorMatrix grayMatrix;
  private System.Drawing.Imaging.ImageAttributes grayAttributes;
  private System.Drawing.Color mcStartColor = Color.White;
  private System.Drawing.Color mcEndColor = Color.FromArgb(199, 212, 247);
  private System.ComponentModel.IContainer components;
  private System.Windows.Forms.Label lblTitle;
  private System.Drawing.Image mxImage;
  private System.Windows.Forms.ToolTip MainToolTip;
  private System.Windows.Forms.Label lblSplitter;
  private System.Windows.Forms.ImageList mxImageList;
  private void InitializeComponent()
  {
   this.components = new System.ComponentModel.Container();
   System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(CollapsiblePanel));
   this.lblTitle = new System.Windows.Forms.Label();
   this.mxImageList = new System.Windows.Forms.ImageList(this.components);
   this.MainToolTip = new System.Windows.Forms.ToolTip(this.components);
   this.lblSplitter = new System.Windows.Forms.Label();
   this.SuspendLayout();
   this.lblTitle.BackColor = System.Drawing.SystemColors.Control;
   this.lblTitle.Cursor = System.Windows.Forms.Cursors.Default;
   this.lblTitle.Dock = System.Windows.Forms.DockStyle.Top;
   this.lblTitle.Font = new System.Drawing.Font("Tahoma", 9F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
   this.lblTitle.ForeColor = System.Drawing.Color.Navy;
   this.lblTitle.Location = new System.Drawing.Point(0, 0);
   this.lblTitle.Name = "lblTitle";
   this.lblTitle.Size = new System.Drawing.Size(200, 24);
   this.lblTitle.TabIndex = 0;
   this.lblTitle.Text = "Title";
   this.lblTitle.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
   this.lblTitle.Paint += new System.Windows.Forms.PaintEventHandler(this.labelTitle_Paint);
   this.lblTitle.MouseUp += new System.Windows.Forms.MouseEventHandler(this.labelTitle_MouseUp);
   this.lblTitle.MouseMove += new System.Windows.Forms.MouseEventHandler(this.labelTitle_MouseMove);
   this.mxImageList.ColorDepth = System.Windows.Forms.ColorDepth.Depth16Bit;
   this.mxImageList.ImageSize = new System.Drawing.Size(16, 16);
   this.mxImageList.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("mxImageList.ImageStream")));
   this.mxImageList.TransparentColor = System.Drawing.Color.Magenta;
   this.lblSplitter.BackColor = System.Drawing.SystemColors.Control;
   this.lblSplitter.Cursor = System.Windows.Forms.Cursors.HSplit;
   this.lblSplitter.Dock = System.Windows.Forms.DockStyle.Bottom;
   this.lblSplitter.Font = new System.Drawing.Font("Tahoma", 9F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
   this.lblSplitter.ForeColor = System.Drawing.Color.Navy;
   this.lblSplitter.Location = new System.Drawing.Point(311, 0);
   this.lblSplitter.Name = "lblSplitter";
   this.lblSplitter.Size = new System.Drawing.Size(200, 2);
   this.lblSplitter.TabIndex = 0;
   this.lblSplitter.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
   this.lblSplitter.MouseUp += new System.Windows.Forms.MouseEventHandler(this.lblSplitter_MouseUp);
   this.lblSplitter.MouseMove += new System.Windows.Forms.MouseEventHandler(this.lblSplitter_MouseMove);
   this.lblSplitter.MouseDown += new System.Windows.Forms.MouseEventHandler(this.lblSplitter_MouseDown);
   this.Controls.Add(this.lblTitle);
   this.SizeChanged += new System.EventHandler(this.CollapsiblePanel_SizeChanged);
   this.BackColorChanged += new System.EventHandler(this.CollapsiblePanel_BackColorChanged);
   this.ResumeLayout(false);
  }
  public CollapsiblePanel() : base()
  {
   this.components = new System.ComponentModel.Container();
   InitializeComponent();
   this.Controls.Add(this.lblSplitter);
   if (IsExpanded)
   {
    this.ExpandedHeight = this.Height;
   }
   MinTitleHeight = 22;
   this.BackColor = System.Drawing.SystemColors.Window;
   moImageTranspAttribute = new System.Drawing.Imaging.ImageAttributes();
   moImageTranspAttribute.SetColorKey(mxImageList.TransparentColor, mxImageList.TransparentColor);
   moImageListTranspAttribute = new System.Drawing.Imaging.ImageAttributes();
   ColorMap[] cm = new ColorMap[1];
   cm[0] = new ColorMap();
   cm[0].OldColor = mxImageList.TransparentColor;
   cm[0].NewColor = Color.Transparent;
   moImageListTranspAttribute.SetRemapTable(cm);
   this.grayMatrix = new ColorMatrix();
   this.grayMatrix.Matrix00 = 1/3f;
   this.grayMatrix.Matrix01 = 1/3f;
   this.grayMatrix.Matrix02 = 1/3f;
   this.grayMatrix.Matrix10 = 1/3f;
   this.grayMatrix.Matrix11 = 1/3f;
   this.grayMatrix.Matrix12 = 1/3f;
   this.grayMatrix.Matrix20 = 1/3f;
   this.grayMatrix.Matrix21 = 1/3f;
   this.grayMatrix.Matrix22 = 1/3f;
   this.grayAttributes = new ImageAttributes();
   this.grayAttributes.SetColorMatrix(this.grayMatrix, ColorMatrixFlag.Default,
    ColorAdjustType.Bitmap);
  }
  private void OnPanelStateChanged(PanelEventArgs e)
  {
   if(PanelStateChanged != null)
   {
    PanelStateChanged(this, e);
   }
  }
  public PanelState PanelState
  {
   get
   {
    return this.meCurrentState;
   }
   set
   {
    PanelState oldState = this.meCurrentState;
    this.meCurrentState = value;
    if(oldState != this.meCurrentState)
    {
     UpdatePanelState();
    }
   }
  }
  public int ExpandedHeight
  {
   get
   {
    return miExpandedHeight;
   }
   set
   {
    miExpandedHeight = value;
   }
  }
  public bool IsExpanded
  {
   get
   {
    return (meCurrentState == PanelState.Expanded);
   }
  }
  public bool FixedSize
  {
   get
   {
    return mbFixedSize;
   }
   set
   {
    mbFixedSize = value;
    lblSplitter.Visible = ! mbFixedSize;
   }
  }
  public string TitleText
  {
   get
   {
    return this.lblTitle.Text;
   }
   set
   {
    this.lblTitle.Text = value;
   }
  }
  public Color TitleFontColour
  {
   get
   {
    return this.lblTitle.ForeColor;
   }
   set
   {
    this.lblTitle.ForeColor = value;
   }
  }
  public Font TitleFont
  {
   get
   {
    return this.lblTitle.Font;
   }
   set
   {
    this.lblTitle.Font = value;
   }
  }
  public ImageList ImageList
  {
   get
   {
    return this.mxImageList;
   }
  }
  public Color StartColor
  {
   get
   {
    return this.mcStartColor;
   }
   set
   {
    this.mcStartColor = value;
    this.lblTitle.Invalidate();
   }
  }
  public Color EndColor
  {
   get
   {
    return this.mcEndColor;
   }
   set
   {
    this.mcEndColor = value;
    this.lblTitle.Invalidate();
   }
  }
  public int MinTitleHeight
  {
   get
   {
    return this.miMinTitleHeight;
   }
   set
   {
    this.miMinTitleHeight = value;
    SetTitleHeight();
    this.lblTitle.Invalidate();
   }
  }
  public Image Image
  {
   get
   {
    return this.mxImage;
   }
   set
   {
    this.mxImage = value;
    SetTitleHeight();
    this.lblTitle.Invalidate();
   }
  }
  private void SetTitleHeight()
  {
   if (this.Image != null)
   {
    if (this.mxImage.Height < miMinTitleHeight)
    {
     this.lblTitle.Height = miMinTitleHeight;
    }
    else
    {
     this.lblTitle.Height = this.mxImage.Height;
    }
   }
   else
   {
    this.lblTitle.Height = miMinTitleHeight;
   }
  }
  private bool IsOverTitle(int xPos, int yPos)
  {
   Rectangle rectTitle = this.lblTitle.Bounds;
   if(rectTitle.Contains(xPos, yPos))
   {
    return true;
   }
   return false;
  }
  private void UpdatePanelState()
  {
   switch(this.meCurrentState)
   {
    case PanelState.Collapsed :
     this.Height = lblTitle.Height;
     lblSplitter.Visible = false;
     break;
    case PanelState.Expanded :
     this.Height = this.miExpandedHeight;
     if (! mbFixedSize)
     {
      lblSplitter.Visible = true;
     }
     break;
    default :
     break;
   }
   this.lblTitle.Invalidate();
   OnPanelStateChanged(new PanelEventArgs(this));
   switch(this.meCurrentState)
   {
    case PanelState.Collapsed :
     MainToolTip.SetToolTip(lblTitle, Resource.Manager["RES_ControlCollapsiblePanelExpandTooltip"]);
     break;
    case PanelState.Expanded :
     MainToolTip.SetToolTip(lblTitle, Resource.Manager["RES_ControlCollapsiblePanelCollapseTooltip"]);
     break;
   }
  }
  private void labelTitle_Paint(object sender, System.Windows.Forms.PaintEventArgs e)
  {
   const int diameter = 14;
   int radius = diameter / 2;
   Rectangle bounds = lblTitle.Bounds;
   int offsetY = 0;
   if(null != this.mxImage)
   {
    offsetY = this.lblTitle.Height - miMinTitleHeight;
    if(offsetY < 0)
    {
     offsetY = 0;
    }
    bounds.Offset(0, offsetY);
    bounds.Height -= offsetY;
   }
   e.Graphics.Clear(this.Parent.BackColor);
   GraphicsPath path = new GraphicsPath();
   path.AddLine(bounds.Left + radius, bounds.Top, bounds.Right - diameter - 1, bounds.Top);
   path.AddArc(bounds.Right - diameter - 1, bounds.Top, diameter, diameter, 270, 90);
   path.AddLine(bounds.Right, bounds.Top + radius, bounds.Right, bounds.Bottom);
   path.AddLine(bounds.Right, bounds.Bottom, bounds.Left - 1, bounds.Bottom);
   path.AddArc(bounds.Left, bounds.Top, diameter, diameter, 180, 90);
   e.Graphics.SmoothingMode = SmoothingMode.AntiAlias;
   if(true == this.Enabled)
   {
    LinearGradientBrush brush = new LinearGradientBrush(
     bounds, this.mcStartColor, this.mcEndColor, LinearGradientMode.Horizontal);
    e.Graphics.FillPath(brush, path);
   }
   else
   {
    ColorEx grayStart = new ColorEx(this.mcStartColor);
    grayStart.Saturation = 0f;
    ColorEx grayEnd = new ColorEx(this.mcEndColor);
    grayEnd.Saturation = 0f;
    LinearGradientBrush brush = new LinearGradientBrush(
     bounds, grayStart.CurrentColor, grayEnd.CurrentColor,
     LinearGradientMode.Horizontal);
    e.Graphics.FillPath(brush, path);
   }
   System.Drawing.GraphicsUnit graphicsUnit = System.Drawing.GraphicsUnit.Display;
   int offsetX = CollapsiblePanel.IconBorder;
   if(null != this.mxImage)
   {
    offsetX += this.mxImage.Width + CollapsiblePanel.IconBorder;
    RectangleF srcRectF = this.mxImage.GetBounds(ref graphicsUnit);
    Rectangle destRect = new Rectangle(CollapsiblePanel.IconBorder,
     CollapsiblePanel.IconBorder, this.mxImage.Width, this.mxImage.Height);
    if(true == this.Enabled)
    {
     e.Graphics.DrawImage(this.mxImage, destRect, (int)srcRectF.Left, (int)srcRectF.Top,
      (int)srcRectF.Width, (int)srcRectF.Height, graphicsUnit, moImageTranspAttribute);
    }
    else
    {
     e.Graphics.DrawImage(this.mxImage, destRect, (int)srcRectF.Left, (int)srcRectF.Top,
      (int)srcRectF.Width, (int)srcRectF.Height, graphicsUnit, this.grayAttributes);
    }
   }
   SolidBrush textBrush = new SolidBrush(this.TitleFontColour);
   float left = (float)offsetX;
   float top = (float)offsetY + (float)CollapsiblePanel.ExpandBorder;
   float width = (float)this.lblTitle.Width - left - this.mxImageList.ImageSize.Width -
    CollapsiblePanel.ExpandBorder;
   float height = (float)miMinTitleHeight - ((float)CollapsiblePanel.ExpandBorder);
   RectangleF textRectF = new RectangleF(left, top, width, height);
   StringFormat format = new StringFormat();
   format.Trimming = StringTrimming.EllipsisWord;
   if(true == this.Enabled)
   {
    e.Graphics.DrawString(lblTitle.Text, lblTitle.Font, textBrush,
     textRectF, format);
   }
   else
   {
    Color disabled = SystemColors.GrayText;
    ControlPaint.DrawStringDisabled(e.Graphics, lblTitle.Text, lblTitle.Font,
     disabled, textRectF, format);
   }
   const int lineWidth = 1;
   SolidBrush lineBrush = new SolidBrush(Color.White);
   Pen linePen = new Pen(lineBrush, lineWidth);
   path.Reset();
   path.AddLine(bounds.Left, bounds.Bottom - lineWidth, bounds.Right,
    bounds.Bottom - lineWidth);
   e.Graphics.DrawPath(linePen, path);
   int xPos = bounds.Right - this.mxImageList.ImageSize.Width - CollapsiblePanel.IconBorder;
   int yPos = (bounds.Height - this.mxImageList.ImageSize.Height)/ 2;
   RectangleF srcIconRectF = this.mxImageList.Images[(int)this.meCurrentState].GetBounds(ref graphicsUnit);
   Rectangle destIconRect = new Rectangle(xPos, yPos,
    this.mxImageList.ImageSize.Width, this.mxImageList.ImageSize.Height);
   if(true == this.Enabled)
   {
    e.Graphics.DrawImage(this.mxImageList.Images[(int)this.meCurrentState], destIconRect,
     (int)srcIconRectF.Left, (int)srcIconRectF.Top, (int)srcIconRectF.Width,
     (int)srcIconRectF.Height, graphicsUnit, moImageListTranspAttribute);
   }
   else
   {
    e.Graphics.DrawImage(this.mxImageList.Images[(int)this.meCurrentState], destIconRect,
     (int)srcIconRectF.Left, (int)srcIconRectF.Top, (int)srcIconRectF.Width,
     (int)srcIconRectF.Height, graphicsUnit, this.grayAttributes);
   }
  }
  private void labelTitle_MouseUp(object sender, System.Windows.Forms.MouseEventArgs e)
  {
   if((e.Button == MouseButtons.Left) && (true == IsOverTitle(e.X, e.Y)))
   {
    if((null != this.mxImageList) && (this.mxImageList.Images.Count >=2))
    {
     if(this.meCurrentState == PanelState.Expanded)
     {
      this.meCurrentState = PanelState.Collapsed;
     }
     else
     {
      this.meCurrentState = PanelState.Expanded;
     }
     UpdatePanelState();
    }
   }
  }
  private void labelTitle_MouseMove(object sender, System.Windows.Forms.MouseEventArgs e)
  {
   if((e.Button == MouseButtons.None) && (true == IsOverTitle(e.X, e.Y)))
   {
    this.lblTitle.Cursor = Cursors.Hand;
   }
   else
   {
    this.lblTitle.Cursor = Cursors.Default;
   }
  }
  private void CollapsiblePanel_SizeChanged(object sender, System.EventArgs e)
  {
   if (DesignMode)
   {
    if (IsExpanded)
    {
     miExpandedHeight = this.Height;
    }
   }
  }
  private void lblSplitter_MouseDown(object sender, System.Windows.Forms.MouseEventArgs e)
  {
   if (lblSplitter.Visible)
   {
    lblSplitter.Capture = true;
    pt.X = e.X;
    pt.Y = e.Y;
   }
  }
  private void lblSplitter_MouseMove(object sender, System.Windows.Forms.MouseEventArgs e)
  {
   if ((lblSplitter.Visible ) && (e.Button == MouseButtons.Left))
   {
    this.Height = this.Height + e.Y - pt.Y;
    if (miExpandedHeight != this.Height)
    {
     miExpandedHeight = this.Height;
     OnPanelStateChanged(new PanelEventArgs(this));
    }
   }
  }
  private void lblSplitter_MouseUp(object sender, System.Windows.Forms.MouseEventArgs e)
  {
   if (lblSplitter.Visible)
   {
    lblSplitter.Capture = false;
    OnPanelStateChanged(new PanelEventArgs(this));
   }
  }
  private void CollapsiblePanel_BackColorChanged(object sender, System.EventArgs e)
  {
   lblSplitter.BackColor = this.BackColor;
  }
 }
 public class PanelEventArgs : System.EventArgs
 {
  private CollapsiblePanel panel;
  public PanelEventArgs(CollapsiblePanel sender)
  {
   this.panel = sender;
  }
  public CollapsiblePanel CollapsiblePanel
  {
   get
   {
    return this.panel;
   }
  }
  public PanelState PanelState
  {
   get
   {
    return this.panel.PanelState;
   }
  }
 }
}
