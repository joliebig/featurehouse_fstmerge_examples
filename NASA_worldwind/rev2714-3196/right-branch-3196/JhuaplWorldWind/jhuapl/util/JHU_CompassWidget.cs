using System;
using System.Drawing;
using System.Collections;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
namespace jhuapl.util
{
 public class JHU_CompassWidget : jhuapl.util.IWidget
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
  protected Sprite m_sprite;
  protected JHU_IconTexture m_iconTexture;
  protected Hashtable m_textures;
  protected string m_imageName = "compass.png";
  protected static int m_normalColor = Color.White.ToArgb();
  protected float XScale;
  protected float YScale;
  public JHU_CompassWidget()
  {
   m_textures = JHU_Globals.getInstance().Textures;
   m_location.X = 5;
   m_location.Y = 100;
   m_size.Height = 79;
   m_size.Width = 79;
   m_isInitialized = false;
  }
  public string ImageName
  {
   get { return m_imageName; }
   set
   {
    m_imageName = value;
    m_isInitialized = false;
   }
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
   m_imageName = JHU_Globals.getInstance().BasePath + @"\Plugins\Navigator\compass.png";
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
   m_isInitialized = true;
  }
  public void Render(DrawArgs drawArgs)
  {
   if (!this.m_isInitialized)
    this.Initialize(drawArgs);
   if ((m_visible) && (m_enabled))
   {
    m_sprite.Begin(SpriteFlags.AlphaBlend);
    m_sprite.Transform = Matrix.Scaling(this.XScale,this.YScale,0);
    m_sprite.Transform *= Matrix.RotationZ((float)drawArgs.WorldCamera.Heading.Radians*-1);
    m_sprite.Transform *= Matrix.Translation(AbsoluteLocation.X+m_size.Width/2, AbsoluteLocation.Y+m_size.Height/2, 0);
    m_sprite.Draw( m_iconTexture.Texture,
     new Vector3(m_iconTexture.Width>>1, m_iconTexture.Height>>1,0),
     Vector3.Empty,
     m_normalColor );
    m_sprite.Transform = Matrix.Identity;
    m_sprite.End();
   }
  }
 }
}
