using System;
using System.Collections;
using System.Diagnostics;
using System.Drawing;
using System.Text;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Renderable;
using WorldWind.Menu;
using System.Windows.Forms;
namespace jhuapl.util
{
 public class JHU_Icon : RenderableObject
 {
  protected double m_latitude = 0.0F;
  protected double m_longitude = 0.0F;
  protected double m_altitude = 0.0F;
  protected string m_url;
  protected Hashtable m_textures;
  protected JHU_FormWidget m_hookForm = null;
  protected JHU_SimpleTreeNodeWidget m_hookTreeNode = null;
  protected JHU_SimpleTreeNodeWidget m_hookGeneralTreeNode = null;
  protected JHU_SimpleTreeNodeWidget m_hookDetailTreeNode = null;
  protected JHU_SimpleTreeNodeWidget m_hookDescTreeNode = null;
  protected JHU_LabelWidget m_hookGeneralLabel = null;
  protected JHU_LabelWidget m_hookDetailLabel = null;
  protected JHU_LabelWidget m_hookDescLabel = null;
  protected ContextMenu m_contextMenu = null;
  protected Vector3 m_groundPoint;
  protected Line m_groundStick;
  protected System.Drawing.Color m_groundStickColor = System.Drawing.Color.FromArgb(
   192,
   255,
   255,
   255);
  protected bool m_isUpdated = false;
  protected static JHU_Globals m_globals;
  protected Point3d m_positionD = new Point3d();
  public double Latitude
  {
   get { return m_latitude; }
   set
   {
    m_latitude = (float)value;
    m_isUpdated = false;
   }
  }
  public double Longitude
  {
   get { return m_longitude; }
   set
   {
    m_longitude = (float)value;
    m_isUpdated = false;
   }
  }
  public double Altitude
  {
   get { return m_altitude; }
   set
   {
    m_altitude = (float)value;
    m_isUpdated = false;
   }
  }
  public Point3d PositionD
  {
   get{ return m_positionD; }
   set{ m_positionD = value; }
  }
  public string TextureFileName;
  public JHU_IconTexture m_iconTexture;
  protected bool m_iconTexture2Show = false;
  protected string m_iconTexture2Name;
  protected JHU_IconTexture m_iconTexture2 = null;
  protected bool m_iconTexture3Show = false;
  protected string m_iconTexture3Name;
  protected JHU_IconTexture m_iconTexture3 = null;
  public Bitmap Image;
  public int Width;
  public int Height;
  public float XScale;
  public float YScale;
  public string URL
  {
   get { return m_url; }
   set
   {
    isSelectable = value != null;
    m_url = value;
   }
  }
  public float MaximumDisplayDistance = float.MaxValue;
  public float MinimumDisplayDistance;
  public Rectangle SelectionRectangle;
  static int hotColor = Color.White.ToArgb();
  static int normalColor = Color.FromArgb(200,255,255,255).ToArgb();
  static int nameColor = Color.White.ToArgb();
  static int descriptionColor = Color.White.ToArgb();
  const int labelWidth = 1000;
  protected Angle m_rotation = Angle.Zero;
  public Angle Rotation
  {
   get { return m_rotation; }
   set
   {
    m_rotation = value;
    if (value == Angle.Zero)
    {
     m_isRotated = true;
    }
    else
    {
     m_isRotated = false;
    }
   }
  }
  protected bool m_isRotated;
  public bool IsRotated
  {
   get { return m_isRotated; }
   set
   {
    m_isRotated = value;
    if (!m_isRotated)
     m_rotation = Angle.Zero;
   }
  }
  protected bool m_drawGroundStick;
  public bool DrawGroundStick
  {
   get { return m_drawGroundStick; }
   set
   {
    m_drawGroundStick = value;
    m_isUpdated = false;
   }
  }
  public JHU_Icon(string name,
   double latitude,
   double longitude) : base( name )
  {
   m_latitude = (float) latitude;
   m_longitude = (float) longitude;
   m_globals = JHU_Globals.getInstance();
   m_textures = m_globals.Textures;
  }
  public JHU_Icon(string name,
   double latitude,
   double longitude,
   double heightAboveSurface) : this( name, latitude, longitude )
  {
   m_altitude = (float) heightAboveSurface;
  }
  public JHU_Icon(string name,
   string description,
   double latitude,
   double longitude,
   double heightAboveSurface,
   Bitmap image,
   int width,
   int height,
   string actionURL) : this( name, latitude, longitude, heightAboveSurface )
  {
   this.Description = description;
   this.Image = image;
   this.Width = width;
   this.Height = height;
   m_url = actionURL;
  }
  public JHU_Icon(string name,
   string description,
   double latitude,
   double longitude,
   double heightAboveSurface,
   string TextureFileName,
   int width,
   int height,
   string actionURL) : this( name, latitude, longitude, heightAboveSurface )
  {
   this.Description = description;
   this.TextureFileName = TextureFileName;
   this.Width = width;
   this.Height = height;
   m_url = actionURL;
  }
  public void SetPosition(double latitude, double longitude)
  {
   m_latitude = (float) latitude;
   m_longitude = (float) longitude;
   m_isUpdated = false;
  }
  public void SetPosition(double latitude, double longitude, double altitude)
  {
   m_latitude = (float) latitude;
   m_longitude = (float) longitude;
   this.Altitude = (float) altitude;
   m_isUpdated = false;
  }
  protected void UpdatePosition(DrawArgs drawArgs)
  {
   double elevation = drawArgs.WorldCamera.WorldRadius;
   if((m_globals.WorldWindow.CurrentWorld.TerrainAccessor != null) && (drawArgs.WorldCamera.Altitude < 300000))
   {
    float distanceToIcon = Vector3.Length(this.Position - drawArgs.WorldCamera.Position);
    if (distanceToIcon < 300000)
    {
     elevation += m_globals.WorldWindow.CurrentWorld.TerrainAccessor.GetElevationAt(
      Latitude, Longitude, 100.0) * World.Settings.VerticalExaggeration;
    }
    else
    {
     elevation += m_globals.WorldWindow.CurrentWorld.TerrainAccessor.GetElevationAt(
      Latitude, Longitude) * World.Settings.VerticalExaggeration;
    }
   }
   Position = MathEngine.SphericalToCartesian(Latitude, Longitude,
    Altitude + elevation);
   m_positionD = MathEngine.SphericalToCartesianD(
    Angle.FromDegrees(m_latitude),
    Angle.FromDegrees(m_longitude),
    m_altitude + drawArgs.WorldCamera.WorldRadius);
   if (m_drawGroundStick)
    m_groundPoint = MathEngine.SphericalToCartesian(Latitude, Longitude, elevation);
   m_isUpdated = true;
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   UpdatePosition(drawArgs);
   object key = null;
   m_iconTexture = null;
   if(TextureFileName.Trim() != String.Empty)
   {
    m_iconTexture = (JHU_IconTexture)m_textures[TextureFileName];
    if(m_iconTexture==null)
    {
     key = TextureFileName;
     m_iconTexture = new JHU_IconTexture( drawArgs.device, TextureFileName );
    }
    if (m_iconTexture2Show && m_iconTexture2Name.Trim() != String.Empty)
    {
     m_iconTexture2 = (JHU_IconTexture)m_textures[m_iconTexture2Name];
     if (m_iconTexture2 == null)
     {
      m_iconTexture2 = new JHU_IconTexture( drawArgs.device, m_iconTexture2Name );
      m_textures.Add(m_iconTexture2Name, m_iconTexture2);
     }
     m_iconTexture2.ReferenceCount++;
    }
    if (m_iconTexture3Show && m_iconTexture3Name.Trim() != String.Empty)
    {
     m_iconTexture3 = (JHU_IconTexture)m_textures[m_iconTexture3Name];
     if (m_iconTexture3 == null)
     {
      m_iconTexture3 = new JHU_IconTexture( drawArgs.device, m_iconTexture3Name );
      m_textures.Add(m_iconTexture3Name, m_iconTexture3);
     }
     m_iconTexture3.ReferenceCount++;
    }
   }
   else
   {
    if(this.Image != null)
    {
     m_iconTexture = (JHU_IconTexture)m_textures[this.Image];
     if(m_iconTexture==null)
     {
      key = this.Image;
      m_iconTexture = new JHU_IconTexture( drawArgs.device, this.Image);
     }
    }
   }
   if(m_iconTexture!=null)
   {
    m_iconTexture.ReferenceCount++;
    if(key!=null)
    {
     m_textures.Add(key, m_iconTexture);
    }
    if(this.Width==0)
     this.Width = m_iconTexture.Width;
    if(this.Height==0)
     this.Height = m_iconTexture.Height;
   }
   if(m_iconTexture == null)
   {
    this.SelectionRectangle = drawArgs.defaultDrawingFont.MeasureString(null, this.Name, DrawTextFormat.None, 0);
   }
   else
   {
    this.SelectionRectangle = new Rectangle( 0, 0, this.Width, this.Height );
   }
   this.SelectionRectangle.Offset(-this.SelectionRectangle.Width/2, -this.SelectionRectangle.Height/2 );
   if (m_iconTexture != null)
   {
    this.XScale = (float)this.Width / m_iconTexture.Width;
    this.YScale = (float)this.Height / m_iconTexture.Height;
   }
   else
   {
    this.XScale = 1.0f;
    this.YScale = 1.0f;
   }
   if (m_groundStick == null)
    m_groundStick = new Line(drawArgs.device);
   isInitialized = true;
  }
  public override void Dispose()
  {
   if (m_contextMenu != null)
   {
    m_contextMenu.Dispose();
    m_contextMenu = null;
   }
   if (m_hookForm != null)
   {
    m_hookForm.Dispose();
    m_hookForm = null;
   }
   m_iconTexture.ReferenceCount--;
   this.isInitialized = false;
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   if(drawArgs.WorldCamera.ViewFrustum.ContainsPoint(this.Position))
   {
    Vector3 projectedPoint = drawArgs.WorldCamera.Project(this.Position);
    if(this.SelectionRectangle.Contains(
     DrawArgs.LastMousePosition.X - (int)projectedPoint.X,
     DrawArgs.LastMousePosition.Y - (int)projectedPoint.Y ) )
    {
     try
     {
      if ((m_url != null) && (m_url.Length > 0))
      {
       Process.Start(m_url);
       return true;
      }
     }
     catch
     {
     }
    }
   }
   return false;
  }
  public bool PerformRMBAction(MouseEventArgs e)
  {
   if (m_contextMenu == null)
   {
    m_contextMenu = new ContextMenu();
    this.BuildContextMenu(m_contextMenu);
   }
   m_contextMenu.Show(m_globals.WorldWindow, new System.Drawing.Point(e.X, e.Y));
   return true;
  }
  public override void Update(DrawArgs drawArgs)
  {
  }
  public override void Render(DrawArgs drawArgs)
  {
   if(!isOn)
    return;
   if (!this.isInitialized)
    this.Initialize(drawArgs);
   if(!drawArgs.WorldCamera.ViewFrustum.ContainsPoint(this.Position))
    return;
   if ((!this.m_isUpdated) || (drawArgs.WorldCamera.Altitude < 300000))
   {
    this.UpdatePosition(drawArgs);
   }
            Vector3 translationVector = new Vector3(
                (float)(this.Position.X - drawArgs.WorldCamera.ReferenceCenter.X),
                (float)(this.Position.Y - drawArgs.WorldCamera.ReferenceCenter.Y),
                (float)(this.Position.Z - drawArgs.WorldCamera.ReferenceCenter.Z));
   Vector3 projectedPoint = drawArgs.WorldCamera.Project(translationVector);
   float distanceToIcon = Vector3.Length(this.Position - drawArgs.WorldCamera.Position);
   if(distanceToIcon > this.MaximumDisplayDistance)
    return;
   if(distanceToIcon < this.MinimumDisplayDistance)
    return;
   FastRender(drawArgs, null, projectedPoint);
   UpdateHookForm();
  }
  public void FastRender(DrawArgs drawArgs, Sprite sprite, Vector3 projectedPoint)
  {
   double distanceToIcon = Vector3.Length(this.Position - drawArgs.WorldCamera.Position);
   if(distanceToIcon > this.MaximumDisplayDistance)
    return;
   if(distanceToIcon < this.MinimumDisplayDistance)
    return;
   if (!this.isInitialized)
    this.Initialize(drawArgs);
   if ((!this.m_isUpdated) || (drawArgs.WorldCamera.Altitude < 300000))
   {
    this.UpdatePosition(drawArgs);
   }
   JHU_IconTexture iconTexture = this.GetTexture();
   if(iconTexture==null)
   {
    if(this.Name != null)
    {
     Rectangle rect = new Rectangle(
      (int)projectedPoint.X - (labelWidth>>1),
      (int)(projectedPoint.Y - (drawArgs.defaultDrawingFont.Description.Height >> 1)),
      labelWidth,
      drawArgs.screenHeight );
     drawArgs.defaultDrawingFont.DrawText(sprite, this.Name, rect, DrawTextFormat.Center, normalColor);
    }
   }
   else
   {
    sprite.Transform = Matrix.Scaling(this.XScale,this.YScale,0);
    if (m_isRotated)
     sprite.Transform *= Matrix.RotationZ((float)m_rotation.Radians - (float) drawArgs.WorldCamera.Heading.Radians);
    sprite.Transform *= Matrix.Translation(projectedPoint.X, projectedPoint.Y, 0);
    sprite.Draw( iconTexture.Texture,
     new Vector3(iconTexture.Width>>1, iconTexture.Height>>1,0),
     Vector3.Empty,
     normalColor );
    if (m_iconTexture2Show)
    {
     sprite.Draw ( m_iconTexture2.Texture,
      new Vector3(m_iconTexture2.Width>>1, m_iconTexture2.Height>>1, 0),
      Vector3.Empty,
      normalColor );
    }
    if (m_iconTexture3Show)
    {
     sprite.Draw ( m_iconTexture3.Texture,
      new Vector3(m_iconTexture3.Width>>1, m_iconTexture3.Height>>1, 0),
      Vector3.Empty,
      normalColor );
    }
    sprite.Transform = Matrix.Identity;
   }
   if (m_drawGroundStick)
   {
    Vector2[] groundStick = new Vector2[2];
    Vector3 referenceCenter = new Vector3(
     (float)drawArgs.WorldCamera.ReferenceCenter.X,
     (float)drawArgs.WorldCamera.ReferenceCenter.Y,
     (float)drawArgs.WorldCamera.ReferenceCenter.Z);
    Vector3 projectedGroundPoint = drawArgs.WorldCamera.Project(m_groundPoint - referenceCenter);
    m_groundStick.Begin();
    groundStick[0].X = (float) (projectedPoint.X);
    groundStick[0].Y = (float) (projectedPoint.Y);
    groundStick[1].X = (float) (projectedGroundPoint.X);
    groundStick[1].Y = (float) (projectedGroundPoint.Y);
    m_groundStick.Draw(groundStick, m_groundStickColor);
    m_groundStick.End();
   }
  }
  public void MouseOverRender(DrawArgs drawArgs, Sprite sprite, Vector3 projectedPoint)
  {
   JHU_IconTexture iconTexture = this.GetTexture();
   if(this.isSelectable)
    DrawArgs.MouseCursor = CursorType.Hand;
   if (!this.isInitialized)
    this.Initialize(drawArgs);
   if ((!this.m_isUpdated) || (drawArgs.WorldCamera.Altitude < 300000))
   {
    this.UpdatePosition(drawArgs);
   }
   m_globals.GeneralInfoLabel.Text = GeneralInfo();
   m_globals.DetailedInfoLabel.Text = DetailedInfo();
   m_globals.DescriptionLabel.Text = DescriptionInfo();
   if(iconTexture==null)
   {
    if(this.Name != null)
    {
     Rectangle rect = new Rectangle(
      (int)projectedPoint.X - (labelWidth>>1),
      (int)(projectedPoint.Y - (drawArgs.defaultDrawingFont.Description.Height >> 1)),
      labelWidth,
      drawArgs.screenHeight );
     drawArgs.defaultDrawingFont.DrawText(sprite, this.Name, rect, DrawTextFormat.Center, hotColor);
    }
   }
   else
   {
    if(this.Name != null)
    {
     int spacing = (int)(this.Width * 0.3f);
     if(spacing>10)
      spacing = 10;
     int offsetForIcon = (this.Width>>1) + spacing;
     Rectangle rect = new Rectangle(
      (int)projectedPoint.X + offsetForIcon,
      (int)(projectedPoint.Y - (drawArgs.defaultDrawingFont.Description.Height >> 1)),
      labelWidth,
      drawArgs.screenHeight );
     drawArgs.defaultDrawingFont.DrawText(sprite, this.Name, rect, DrawTextFormat.WordBreak, hotColor);
    }
    sprite.Transform = Matrix.Scaling(this.XScale,this.YScale,0);
    if (m_isRotated)
     sprite.Transform *= Matrix.RotationZ((float)m_rotation.Radians - (float) drawArgs.WorldCamera.Heading.Radians);
    sprite.Transform *= Matrix.Translation(projectedPoint.X, projectedPoint.Y, 0);
    sprite.Draw( iconTexture.Texture,
     new Vector3(iconTexture.Width>>1, iconTexture.Height>>1,0),
     Vector3.Empty,
     hotColor );
    if (m_iconTexture2Show)
    {
     sprite.Draw ( m_iconTexture2.Texture,
      new Vector3(m_iconTexture2.Width>>1, m_iconTexture2.Height>>1, 0),
      Vector3.Empty,
      hotColor );
    }
    if (m_iconTexture3Show)
    {
     sprite.Draw ( m_iconTexture3.Texture,
      new Vector3(m_iconTexture3.Width>>1, m_iconTexture3.Height>>1, 0),
      Vector3.Empty,
      hotColor );
    }
    sprite.Transform = Matrix.Identity;
   }
   if (m_drawGroundStick)
   {
    Vector2[] groundStick = new Vector2[2];
    Vector3 referenceCenter = new Vector3(
     (float)drawArgs.WorldCamera.ReferenceCenter.X,
     (float)drawArgs.WorldCamera.ReferenceCenter.Y,
     (float)drawArgs.WorldCamera.ReferenceCenter.Z);
    Vector3 projectedGroundPoint = drawArgs.WorldCamera.Project(m_groundPoint - referenceCenter);
    m_groundStick.Begin();
    groundStick[0].X = projectedPoint.X;
    groundStick[0].Y = projectedPoint.Y;
    groundStick[1].X = projectedGroundPoint.X;
    groundStick[1].Y = projectedGroundPoint.Y;
    m_groundStick.Draw(groundStick, m_groundStickColor);
    m_groundStick.End();
   }
  }
  public virtual string GeneralInfo()
  {
   StringBuilder outString = new StringBuilder();
   outString.AppendFormat("{0:-10} {1}\n","Name:", Name);
   outString.AppendFormat("{0:-10} {1:00.00000}\n","Lat:", Latitude);
   outString.AppendFormat("{0:-10} {1:000.00000}\n","Lon:", Longitude);
   outString.AppendFormat("{0:-10} {1:F0}\n","Alt:", this.Altitude);
   return outString.ToString();
  }
  public virtual string DetailedInfo()
  {
   StringBuilder outString = new StringBuilder();
   outString.AppendFormat("{0:-10} {1}\n","URL:", m_url);
   return outString.ToString();
  }
  public virtual string DescriptionInfo()
  {
   return this.Description;
  }
  public JHU_IconTexture GetTexture()
  {
   return m_iconTexture;
  }
  public void UpdateHookForm()
  {
   if (m_hookForm != null)
   {
    if (m_hookForm.Enabled)
    {
     m_hookForm.Text = this.DMS();
     m_hookGeneralLabel.Text = this.GeneralInfo();
     m_hookDetailLabel.Text = this.DetailedInfo();
     m_hookDescLabel.Text = this.DescriptionInfo();
    }
    else
    {
     m_hookForm.Dispose();
     m_hookTreeNode = null;
     m_hookGeneralTreeNode = null;
     m_hookDetailTreeNode = null;
     m_hookDescTreeNode = null;
     m_hookGeneralLabel = null;
     m_hookDetailLabel = null;
     m_hookDescLabel = null;
     m_hookForm = null;
    }
   }
  }
  public string Degrees()
  {
   StringBuilder retStr = new StringBuilder();
   retStr.AppendFormat("Lat: {0:00.00000}", this.Latitude);
   retStr.AppendFormat(" Lon: {0:000.00000}", this.Longitude);
   retStr.AppendFormat(" Alt: {0:F0}", this.Altitude);
   return retStr.ToString();
  }
  public string DMS()
  {
   StringBuilder retStr = new StringBuilder();
   retStr.AppendFormat("Lat: {0}", JHU_Utilities.Degrees2DMS(this.Latitude, 'N', 'S'));
   retStr.AppendFormat(" Lon: {0}", JHU_Utilities.Degrees2DMS(this.Longitude, 'E', 'W'));
   retStr.AppendFormat(" Alt: {0:F0}", this.Altitude);
   return retStr.ToString();
  }
  public void GoTo()
  {
   m_globals.WorldWindow.GotoLatLon(m_latitude, m_longitude);
  }
  public override void BuildContextMenu(ContextMenu menu)
  {
   MenuItem gotoMenuItem = new MenuItem("Goto Location", new EventHandler(IconGotoMenuItem_Click));
   MenuItem hookMenuItem = new MenuItem("Hook " + name, new EventHandler(IconHookMenuItem_Click));
   MenuItem urlMenuItem = new MenuItem ("Open URL", new EventHandler (IconURLMenuItem_Click));
   if ((m_url == null) || (m_url.Length <= 0))
   {
    urlMenuItem.Enabled = false;
   }
   menu.MenuItems.Add(gotoMenuItem);
   menu.MenuItems.Add(hookMenuItem);
   menu.MenuItems.Add(urlMenuItem);
  }
  public void AddContextMenuItem(MenuItem newItem)
  {
   if (m_contextMenu == null)
   {
    m_contextMenu = new ContextMenu();
    this.BuildContextMenu(m_contextMenu);
   }
   m_contextMenu.MenuItems.Add(newItem);
  }
  void IconGotoMenuItem_Click(object sender, EventArgs s)
  {
   JHU_Log.Write(1, "NAV", this.Latitude, this.Longitude, this.Altitude, this.Name, "Icon Goto called for icon " + this.Name);
   this.GoTo();
  }
  void IconHookMenuItem_Click(object sender, EventArgs s)
  {
   JHU_Log.Write(1, "ICON", this.Latitude, this.Longitude, this.Altitude, this.Name, "Icon Hook called for icon " + this.Name);
   if (m_hookForm == null)
   {
    m_hookForm = new jhuapl.util.JHU_FormWidget(" " + this.Name);
    m_hookForm.WidgetSize = new System.Drawing.Size(200, 250);
    m_hookForm.Location = new System.Drawing.Point(200,120);
    m_hookForm.DestroyOnClose = true;
    m_hookTreeNode = new JHU_SimpleTreeNodeWidget("Info");
    m_hookTreeNode.IsRadioButton = true;
    m_hookTreeNode.Expanded = true;
    m_hookTreeNode.EnableCheck = false;
    m_hookGeneralLabel = new JHU_LabelWidget("");
    m_hookGeneralLabel.ClearOnRender = true;
    m_hookGeneralLabel.Format = DrawTextFormat.WordBreak ;
    m_hookGeneralLabel.Location = new System.Drawing.Point(0, 0);
    m_hookGeneralLabel.AutoSize = true;
    m_hookGeneralLabel.UseParentWidth = false;
    m_hookGeneralTreeNode = new JHU_SimpleTreeNodeWidget("General");
    m_hookGeneralTreeNode.IsRadioButton = true;
    m_hookGeneralTreeNode.Expanded = true;
    m_hookGeneralTreeNode.EnableCheck = false;
    m_hookGeneralTreeNode.Add(m_hookGeneralLabel);
    m_hookTreeNode.Add(m_hookGeneralTreeNode);
    m_hookDetailLabel = new JHU_LabelWidget("");
    m_hookDetailLabel.ClearOnRender = true;
    m_hookDetailLabel.Format = DrawTextFormat.WordBreak ;
    m_hookDetailLabel.Location = new System.Drawing.Point(0, 0);
    m_hookDetailLabel.AutoSize = true;
    m_hookDetailLabel.UseParentWidth = false;
    m_hookDetailTreeNode = new JHU_SimpleTreeNodeWidget("Detail");
    m_hookDetailTreeNode.IsRadioButton = true;
    m_hookDetailTreeNode.Expanded = true;
    m_hookDetailTreeNode.EnableCheck = false;
    m_hookDetailTreeNode.Add(m_hookDetailLabel);
    m_hookTreeNode.Add(m_hookDetailTreeNode);
    m_hookDescTreeNode = new JHU_SimpleTreeNodeWidget("Description");
    m_hookDescTreeNode.IsRadioButton = true;
    m_hookDescTreeNode.Expanded = false;
    m_hookDescTreeNode.EnableCheck = false;
    m_hookDescLabel = new JHU_LabelWidget("");
    m_hookDescLabel.ClearOnRender = true;
    m_hookDescLabel.Format = DrawTextFormat.WordBreak ;
    m_hookDescLabel.Location = new System.Drawing.Point(0, 0);
    m_hookDescLabel.AutoSize = true;
    m_hookDescLabel.UseParentWidth = true;
    m_hookDescTreeNode.Add(m_hookDescLabel);
    m_hookTreeNode.Add(m_hookDescTreeNode);
    m_hookForm.Add(m_hookTreeNode);
    m_globals.RootWidget.Add(m_hookForm);
   }
   UpdateHookForm();
   m_hookForm.Enabled = true;
   m_hookForm.Visible = true;
  }
  void IconURLMenuItem_Click(object sender, EventArgs s)
  {
   try
   {
    JHU_Log.Write(1, "ICON", this.Latitude, this.Longitude, this.Altitude, this.Name, "Icon URL called for icon " + this.Name +". URL = " + m_url);
    if ((m_url != null) && (m_url.Length > 0))
    {
     Process.Start(m_url);
    }
   }
   catch
   {
   }
  }
  void IconADOCSMenuItem_Click(object sender, EventArgs s)
  {
   try
   {
    if ((m_url != null) && (m_url.Length > 0))
    {
     Process.Start("C:\\BIN\\adocs.exe");
    }
   }
   catch
   {
   }
  }
 }
}
