using System;
using System.Drawing;
using System.Collections;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
namespace jhuapl.util
{
 public class JHU_ControlWidget : jhuapl.util.IWidget, jhuapl.util.IInteractive
 {
  protected string m_name = "";
  protected System.Drawing.Point m_location = new System.Drawing.Point(0,0);
  protected System.Drawing.Size m_size = new System.Drawing.Size(0,0);
  protected bool m_visible = true;
  protected bool m_enabled = true;
  protected bool m_countHeight = true;
  protected bool m_countWidth = false;
  protected jhuapl.util.IWidget m_parentWidget = null;
  protected object m_tag = null;
  protected bool m_isInitialized = false;
  protected MouseClickAction m_leftClickAction = null;
  protected MouseClickAction m_rightClickAction = null;
  protected Sprite m_sprite;
  protected JHU_IconTexture m_iconTexture;
  protected Hashtable m_textures;
  protected string m_imageName = "world.png";
  protected Line m_crossHairs;
  static int m_normalColor = Color.White.ToArgb();
  System.Drawing.Color m_BackgroundColor = System.Drawing.Color.FromArgb(
   128,
   255,
   255,
   255);
  protected float XScale;
  protected float YScale;
  public string ImageName
  {
   get { return m_imageName; }
   set
   {
    m_imageName = value;
    m_isInitialized = false;
   }
  }
  public JHU_ControlWidget()
  {
   m_textures = JHU_Globals.getInstance().Textures;
   m_location.X = 10;
   m_location.Y = 5;
   m_size.Height = 90;
   m_size.Width = 180;
   m_isInitialized = false;
   LeftClickAction = new MouseClickAction(this.PerformLMBAction);
  }
  private void UpdateCrosshair(DrawArgs drawArgs)
  {
   float lat = (float) drawArgs.WorldCamera.Latitude.Degrees;
   float lon = (float) drawArgs.WorldCamera.Longitude.Degrees;
   lat = lat * -1;
   lat = lat / 2;
   lon = lon / 2;
   System.Drawing.Color crossHairColor = System.Drawing.Color.FromArgb(
    192,
    255,
    0,
    0);
   Vector2[] vertical = new Vector2[2];
   Vector2[] horizontal = new Vector2[2];
   vertical[0].X = Location.X + m_size.Width/2 + lon;
   vertical[0].Y = Location.Y + 0;
   vertical[1].X = Location.X + m_size.Width/2 + lon;
   vertical[1].Y = Location.Y + m_size.Height;
   horizontal[0].X = Location.X + 0;
   horizontal[0].Y = Location.Y + m_size.Height/2 + lat;
   horizontal[1].X = Location.X + m_size.Width;
   horizontal[1].Y = Location.Y + m_size.Height/2 + lat;
   m_crossHairs.Begin();
   m_crossHairs.Draw(vertical, crossHairColor);
   m_crossHairs.Draw(horizontal, crossHairColor);
   m_crossHairs.End();
  }
  public void PerformLMBAction(System.Windows.Forms.MouseEventArgs e)
  {
   double lon = (e.X - this.AbsoluteLocation.X)*2 - 180;
   double lat = (e.Y - this.AbsoluteLocation.Y)*2 - 90;
   if (lat > 0)
    lat = -lat;
   else
    lat = System.Math.Abs(lat);
   JHU_Globals.getInstance().WorldWindow.GotoLatLon(lat, lon);
   JHU_Log.Write(1, "NAV", lat, lon, 0, this.Name, "Control Widget Goto Point Called.");
  }
  public string Name
  {
   get { return m_name; }
   set { m_name = value; }
  }
  public System.Drawing.Point Location
  {
   get { return m_location; }
   set { m_location = value; }
  }
  public System.Drawing.Point AbsoluteLocation
  {
   get
   {
    if(m_parentWidget != null)
    {
     return new System.Drawing.Point(
      m_location.X + m_parentWidget.ClientLocation.X,
      m_location.Y + m_parentWidget.ClientLocation.Y);
    }
    else
    {
     return m_location;
    }
   }
  }
  public System.Drawing.Point ClientLocation
  {
   get { return this.AbsoluteLocation; }
  }
  public System.Drawing.Size WidgetSize
  {
   get { return m_size; }
   set { m_size = value; }
  }
  public System.Drawing.Size ClientSize
  {
   get { return m_size; }
   set { m_size = value; }
  }
  public bool Enabled
  {
   get { return m_enabled; }
   set { m_enabled = value; }
  }
  public bool Visible
  {
   get { return m_visible; }
   set { m_visible = value; }
  }
  public bool CountHeight
  {
   get { return m_countHeight; }
   set { m_countHeight = value; }
  }
  public bool CountWidth
  {
   get { return m_countWidth; }
   set { m_countWidth = value; }
  }
  public jhuapl.util.IWidget ParentWidget
  {
   get { return m_parentWidget; }
   set { m_parentWidget = value; }
  }
  public jhuapl.util.IWidgetCollection ChildWidgets
  {
   get { return null; }
   set { }
  }
  public object Tag
  {
   get { return m_tag; }
   set { m_tag = value; }
  }
  public void Initialize (DrawArgs drawArgs)
  {
   object key = null;
   m_imageName = JHU_Globals.getInstance().BasePath + @"\Plugins\Navigator\world.png";
   m_iconTexture = (JHU_IconTexture)m_textures[m_imageName];
   if(m_iconTexture==null)
   {
    key = m_imageName;
    m_iconTexture = new JHU_IconTexture( drawArgs.device, m_imageName );
   }
   if(m_iconTexture!=null)
   {
    m_iconTexture.ReferenceCount++;
    if(key!=null)
    {
     m_textures.Add(key, m_iconTexture);
    }
    if (m_size.Width == 0)
     m_size.Width = m_iconTexture.Width;
    if (m_size.Height == 0)
     m_size.Height = m_iconTexture.Height;
    this.XScale = (float)m_size.Width / m_iconTexture.Width;
    this.YScale = (float)m_size.Height / m_iconTexture.Height;
   }
   if (m_sprite == null)
    m_sprite = new Sprite(drawArgs.device);
   if (m_crossHairs == null)
    m_crossHairs = new Line(drawArgs.device);
   m_isInitialized = true;
  }
  public void Render(DrawArgs drawArgs)
  {
   if (!this.m_isInitialized)
    this.Initialize(drawArgs);
   if ((m_visible) && (m_enabled))
   {
    JHU_Utilities.DrawBox(
     this.AbsoluteLocation.X,
     this.AbsoluteLocation.Y,
     m_size.Width,
     m_size.Height,
     0.0f,
     m_BackgroundColor.ToArgb(),
     drawArgs.device);
    m_sprite.Begin(SpriteFlags.AlphaBlend);
    m_sprite.Transform = Matrix.Scaling(this.XScale,this.YScale,0);
    m_sprite.Transform *= Matrix.Translation(AbsoluteLocation.X+m_size.Width/2, AbsoluteLocation.Y+m_size.Height/2, 0);
    m_sprite.Draw( m_iconTexture.Texture,
     new Vector3(m_iconTexture.Width>>1, m_iconTexture.Height>>1,0),
     Vector3.Empty,
     m_normalColor );
    m_sprite.Transform = Matrix.Identity;
    m_sprite.End();
    UpdateCrosshair(drawArgs);
   }
  }
  public MouseClickAction LeftClickAction
  {
   get { return m_leftClickAction; }
   set { m_leftClickAction = value; }
  }
  public MouseClickAction RightClickAction
  {
   get { return m_rightClickAction; }
   set { m_rightClickAction = value; }
  }
  public bool OnMouseDown(System.Windows.Forms.MouseEventArgs e)
  {
   if ((m_visible) && (m_enabled))
   {
    if(e.X >= this.AbsoluteLocation.X &&
     e.X <= this.AbsoluteLocation.X + m_size.Width &&
     e.Y >= this.AbsoluteLocation.Y &&
     e.Y <= this.AbsoluteLocation.Y + m_size.Height)
    {
     if (e.Button == System.Windows.Forms.MouseButtons.Left &&
      m_leftClickAction != null)
     {
      m_leftClickAction(e);
      return true;
     }
     if (e.Button == System.Windows.Forms.MouseButtons.Right &&
      m_rightClickAction != null)
     {
      m_rightClickAction(e);
      return true;
     }
    }
   }
   return false;
  }
  public bool OnMouseUp(System.Windows.Forms.MouseEventArgs e)
  {
   return false;
  }
  public bool OnMouseMove(System.Windows.Forms.MouseEventArgs e)
  {
   return false;
  }
  public bool OnMouseWheel(System.Windows.Forms.MouseEventArgs e)
  {
   return false;
  }
  public bool OnMouseEnter(EventArgs e)
  {
   return false;
  }
  public bool OnMouseLeave(EventArgs e)
  {
   return false;
  }
  public bool OnKeyDown(System.Windows.Forms.KeyEventArgs e)
  {
   return false;
  }
  public bool OnKeyUp(System.Windows.Forms.KeyEventArgs e)
  {
   return false;
  }
 }
}
