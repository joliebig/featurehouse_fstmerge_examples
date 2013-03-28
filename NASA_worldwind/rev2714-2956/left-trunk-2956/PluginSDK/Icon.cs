using System;
using System.Collections;
using System.Diagnostics;
using System.Drawing;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
namespace WorldWind.Renderable
{
 public class IconTexture : IDisposable
 {
  public Texture Texture;
  public int Width;
  public int Height;
        public int ReferenceCount;
  public IconTexture(Device device, string textureFileName)
  {
   if(ImageHelper.IsGdiSupportedImageFormat(textureFileName))
   {
    using(Image image = ImageHelper.LoadImage(textureFileName))
     LoadImage(device, image);
   }
   else
   {
    Texture = ImageHelper.LoadIconTexture( textureFileName );
    using(Surface s = Texture.GetSurfaceLevel(0))
    {
     SurfaceDescription desc = s.Description;
     Width = desc.Width;
     Height = desc.Height;
    }
   }
  }
  public IconTexture(Device device, Bitmap image)
  {
   LoadImage(device, image);
  }
  protected void LoadImage(Device device, Image image)
  {
   Width = (int)Math.Round(Math.Pow(2, (int)(Math.Ceiling(Math.Log(image.Width)/Math.Log(2)))));
   if(Width>device.DeviceCaps.MaxTextureWidth)
    Width = device.DeviceCaps.MaxTextureWidth;
   Height = (int)Math.Round(Math.Pow(2, (int)(Math.Ceiling(Math.Log(image.Height)/Math.Log(2)))));
   if(Height>device.DeviceCaps.MaxTextureHeight)
    Height = device.DeviceCaps.MaxTextureHeight;
   using(Bitmap textureSource = new Bitmap(Width, Height))
   using(Graphics g = Graphics.FromImage(textureSource))
   {
    g.DrawImage(image, 0,0,Width,Height);
    if(Texture!=null)
     Texture.Dispose();
    Texture = new Texture(device, textureSource, Usage.None, Pool.Managed);
   }
  }
  public void Dispose()
  {
   if(Texture!=null)
   {
    Texture.Dispose();
    Texture = null;
   }
   GC.SuppressFinalize(this);
  }
 }
 public class Icons : RenderableObjectList
 {
  protected Hashtable m_textures = new Hashtable();
  protected Sprite m_sprite;
  static int hotColor = Color.White.ToArgb();
  static int normalColor = Color.FromArgb(150,255,255,255).ToArgb();
  static int nameColor = Color.White.ToArgb();
  static int descriptionColor = Color.White.ToArgb();
  System.Timers.Timer refreshTimer;
  protected Icon mouseOverIcon;
  public Icons(string name) : base(name)
  {
  }
  public Icons(string name,
   string dataSource,
   TimeSpan refreshInterval,
   World parentWorld,
   Cache cache) : base(name, dataSource, refreshInterval, parentWorld, cache)
  {
  }
  public void AddIcon(Icon icon)
  {
   Add(icon);
  }
  public override void Add(RenderableObject ro)
  {
   m_children.Add(ro);
   isInitialized = false;
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   if(!isOn)
    return;
   if(m_sprite != null)
   {
    m_sprite.Dispose();
    m_sprite = null;
   }
   m_sprite = new Sprite(drawArgs.device);
   System.TimeSpan smallestRefreshInterval = System.TimeSpan.MaxValue;
   foreach(RenderableObject ro in m_children)
   {
    Icon icon = ro as Icon;
    if(icon==null)
    {
     if(ro.IsOn)
      ro.Initialize(drawArgs);
     continue;
    }
    if(icon.RefreshInterval.TotalMilliseconds != 0 && icon.RefreshInterval != TimeSpan.MaxValue && icon.RefreshInterval < smallestRefreshInterval)
     smallestRefreshInterval = icon.RefreshInterval;
    icon.Initialize(drawArgs);
    object key = null;
    IconTexture iconTexture = null;
    if(icon.TextureFileName != null && icon.TextureFileName.Length > 0)
    {
     if(icon.TextureFileName.ToLower().StartsWith("http://") && icon.SaveFilePath != null)
     {
      try
      {
       WorldWind.Net.WebDownload webDownload = new WorldWind.Net.WebDownload(icon.TextureFileName);
       webDownload.DownloadType = WorldWind.Net.DownloadType.Unspecified;
       System.IO.FileInfo saveFile = new System.IO.FileInfo(icon.SaveFilePath);
       if(!saveFile.Directory.Exists)
        saveFile.Directory.Create();
       webDownload.DownloadFile(saveFile.FullName);
      }
      catch{}
      iconTexture = (IconTexture)m_textures[icon.SaveFilePath];
      if(iconTexture==null)
      {
       key = icon.SaveFilePath;
       iconTexture = new IconTexture( drawArgs.device, icon.SaveFilePath );
      }
     }
     else
     {
      iconTexture = (IconTexture)m_textures[icon.TextureFileName];
      if(iconTexture==null)
      {
       key = icon.TextureFileName;
       iconTexture = new IconTexture( drawArgs.device, icon.TextureFileName );
      }
     }
    }
    else
    {
     if(icon.Image != null)
     {
      iconTexture = (IconTexture)m_textures[icon.Image];
      if(iconTexture==null)
      {
       key = icon.Image;
       iconTexture = new IconTexture( drawArgs.device, icon.Image);
      }
     }
    }
    if(iconTexture==null)
     continue;
    if(key!=null)
    {
     m_textures.Add(key, iconTexture);
     if(icon.Width==0)
      icon.Width = iconTexture.Width;
     if(icon.Height==0)
      icon.Height = iconTexture.Height;
    }
   }
   foreach(RenderableObject ro in m_children)
   {
    Icon icon = ro as Icon;
    if(icon==null)
     continue;
    if(GetTexture(icon)==null)
    {
     icon.SelectionRectangle = drawArgs.defaultDrawingFont.MeasureString(null, icon.Name, DrawTextFormat.None, 0);
    }
    else
    {
     icon.SelectionRectangle = new Rectangle( 0,0,icon.Width, icon.Height );
    }
    icon.SelectionRectangle.Offset(-icon.SelectionRectangle.Width/2, -icon.SelectionRectangle.Height/2 );
   }
   if(refreshTimer == null && smallestRefreshInterval != TimeSpan.MaxValue)
   {
    refreshTimer = new System.Timers.Timer(smallestRefreshInterval.TotalMilliseconds);
    refreshTimer.Elapsed += new System.Timers.ElapsedEventHandler(refreshTimer_Elapsed);
    refreshTimer.Start();
   }
   isInitialized = true;
  }
  public override void Dispose()
  {
   base.Dispose();
   if(m_textures != null)
   {
    foreach(IconTexture iconTexture in m_textures.Values)
     iconTexture.Texture.Dispose();
    m_textures.Clear();
   }
   if(m_sprite != null)
   {
    m_sprite.Dispose();
    m_sprite = null;
   }
   if(refreshTimer != null)
   {
    refreshTimer.Stop();
    refreshTimer.Dispose();
    refreshTimer = null;
   }
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   foreach(RenderableObject ro in m_children)
   {
    if(!ro.IsOn)
     continue;
    if(!ro.isSelectable)
     continue;
    Icon icon = ro as Icon;
    if(icon==null)
    {
     if (ro.PerformSelectionAction(drawArgs))
      return true;
     continue;
    }
    if(!drawArgs.WorldCamera.ViewFrustum.ContainsPoint(icon.Position))
     continue;
    Vector3 projectedPoint = drawArgs.WorldCamera.Project(icon.Position);
    if(!icon.SelectionRectangle.Contains(
     DrawArgs.LastMousePosition.X - (int)projectedPoint.X,
     DrawArgs.LastMousePosition.Y - (int)projectedPoint.Y ) )
     continue;
    try
    {
     if(DrawArgs.IsLeftMouseButtonDown && !DrawArgs.IsRightMouseButtonDown)
     {
      if(icon.OnClickZoomAltitude != double.NaN || icon.OnClickZoomHeading != double.NaN || icon.OnClickZoomTilt != double.NaN)
      {
       drawArgs.WorldCamera.SetPosition(
        icon.Latitude,
        icon.Longitude,
        icon.OnClickZoomHeading,
        icon.OnClickZoomAltitude,
        icon.OnClickZoomTilt);
      }
      ProcessStartInfo psi = new ProcessStartInfo();
      psi.FileName = icon.ClickableActionURL;
      psi.Verb = "open";
      psi.UseShellExecute = true;
      psi.CreateNoWindow = true;
      Process.Start(psi);
     }
     else if(!DrawArgs.IsLeftMouseButtonDown && DrawArgs.IsRightMouseButtonDown)
     {
      ScreenOverlay[] overlays = icon.Overlays;
      if(overlays != null && overlays.Length > 0)
      {
       System.Windows.Forms.ContextMenu contextMenu = new System.Windows.Forms.ContextMenu();
       foreach(ScreenOverlay curOverlay in overlays)
       {
        contextMenu.MenuItems.Add(curOverlay.Name, new System.EventHandler(icon.OverlayOnOpen));
       }
       contextMenu.Show(DrawArgs.ParentControl, DrawArgs.LastMousePosition);
      }
     }
     return true;
    }
    catch
    {
    }
   }
   return false;
  }
  public override void Render(DrawArgs drawArgs)
  {
   if(!isOn)
    return;
   if(!isInitialized)
    return;
   foreach(RenderableObject ro in m_children)
   {
    if(!ro.IsOn)
     continue;
    ro.Render(drawArgs);
   }
   int closestIconDistanceSquared = int.MaxValue;
   Icon closestIcon = null;
   m_sprite.Begin(SpriteFlags.AlphaBlend);
   foreach(RenderableObject ro in m_children)
   {
    if(!ro.IsOn)
     continue;
    Icon icon = ro as Icon;
    if(icon==null)
     continue;
    Vector3 translationVector = new Vector3(
    (float)(icon.PositionD.X - drawArgs.WorldCamera.ReferenceCenter.X),
    (float)(icon.PositionD.Y - drawArgs.WorldCamera.ReferenceCenter.Y),
    (float)(icon.PositionD.Z - drawArgs.WorldCamera.ReferenceCenter.Z));
    Vector3 projectedPoint = drawArgs.WorldCamera.Project(translationVector);
    int dx = DrawArgs.LastMousePosition.X - (int)projectedPoint.X;
    int dy = DrawArgs.LastMousePosition.Y - (int)projectedPoint.Y;
    if( icon.SelectionRectangle.Contains( dx, dy ) )
    {
     int distanceSquared = dx*dx + dy*dy;
     if(distanceSquared < closestIconDistanceSquared)
     {
      closestIconDistanceSquared = distanceSquared;
      closestIcon = icon;
     }
    }
    if(icon != mouseOverIcon)
     Render(drawArgs, icon, projectedPoint);
   }
   if(mouseOverIcon != null)
   {
    Vector3 translationVector = new Vector3(
     (float)(mouseOverIcon.PositionD.X - drawArgs.WorldCamera.ReferenceCenter.X),
     (float)(mouseOverIcon.PositionD.Y - drawArgs.WorldCamera.ReferenceCenter.Y),
     (float)(mouseOverIcon.PositionD.Z - drawArgs.WorldCamera.ReferenceCenter.Z));
    Render(drawArgs, mouseOverIcon, drawArgs.WorldCamera.Project(translationVector));
   }
   mouseOverIcon = closestIcon;
   m_sprite.End();
  }
  protected virtual void Render(DrawArgs drawArgs, Icon icon, Vector3 projectedPoint)
  {
   if (!icon.isInitialized)
    icon.Initialize(drawArgs);
   if(!drawArgs.WorldCamera.ViewFrustum.ContainsPoint(icon.Position))
    return;
   double distanceToIcon = Vector3.Length(icon.Position - drawArgs.WorldCamera.Position);
   if(distanceToIcon > icon.MaximumDisplayDistance)
    return;
   if(distanceToIcon < icon.MinimumDisplayDistance)
    return;
   IconTexture iconTexture = GetTexture(icon);
   bool isMouseOver = icon == mouseOverIcon;
   if(isMouseOver)
   {
    isMouseOver = true;
    if(icon.isSelectable)
     DrawArgs.MouseCursor = CursorType.Hand;
    string description = icon.Description;
    if(description==null)
     description = icon.ClickableActionURL;
    if(description!=null)
    {
     DrawTextFormat format = DrawTextFormat.NoClip | DrawTextFormat.WordBreak | DrawTextFormat.Bottom;
     int left = 10;
     if(World.Settings.showLayerManager)
      left += World.Settings.layerManagerWidth;
     Rectangle rect = Rectangle.FromLTRB(left, 10, drawArgs.screenWidth - 10, drawArgs.screenHeight - 10 );
     drawArgs.defaultDrawingFont.DrawText(
      m_sprite, description,
      rect,
      format, 0xb0 << 24 );
     rect.Offset(2,0);
     drawArgs.defaultDrawingFont.DrawText(
      m_sprite, description,
      rect,
      format, 0xb0 << 24 );
     rect.Offset(0,2);
     drawArgs.defaultDrawingFont.DrawText(
      m_sprite, description,
      rect,
      format, 0xb0 << 24 );
     rect.Offset(-2,0);
     drawArgs.defaultDrawingFont.DrawText(
      m_sprite, description,
      rect,
      format, 0xb0 << 24 );
     rect.Offset(1,-1);
     drawArgs.defaultDrawingFont.DrawText(
      m_sprite, description,
      rect,
      format, descriptionColor );
    }
   }
   int color = isMouseOver ? hotColor : normalColor;
   if(iconTexture==null || isMouseOver || icon.NameAlwaysVisible)
   {
    if(icon.Name != null)
    {
     const int labelWidth = 1000;
     if(iconTexture==null)
     {
      Rectangle rect = new Rectangle(
       (int)projectedPoint.X - (labelWidth>>1),
       (int)(projectedPoint.Y - (drawArgs.defaultDrawingFont.Description.Height >> 1)),
       labelWidth,
       drawArgs.screenHeight );
      drawArgs.defaultDrawingFont.DrawText(m_sprite, icon.Name, rect, DrawTextFormat.Center, color);
     }
     else
     {
      int spacing = (int)(icon.Width * 0.3f);
      if(spacing>10)
       spacing = 10;
      int offsetForIcon = (icon.Width>>1) + spacing;
      Rectangle rect = new Rectangle(
       (int)projectedPoint.X + offsetForIcon,
       (int)(projectedPoint.Y - (drawArgs.defaultDrawingFont.Description.Height >> 1)),
       labelWidth,
       drawArgs.screenHeight );
      drawArgs.defaultDrawingFont.DrawText(m_sprite, icon.Name, rect, DrawTextFormat.WordBreak, color);
     }
    }
   }
   if(iconTexture!=null)
   {
    float xscale = (float)icon.Width / iconTexture.Width;
    float yscale = (float)icon.Height / iconTexture.Height;
    m_sprite.Transform = Matrix.Scaling(xscale,yscale,0);
    if(icon.IsRotated)
     m_sprite.Transform *= Matrix.RotationZ((float)icon.Rotation.Radians - (float)drawArgs.WorldCamera.Heading.Radians);
    m_sprite.Transform *= Matrix.Translation(projectedPoint.X, projectedPoint.Y, 0);
    m_sprite.Draw( iconTexture.Texture,
     new Vector3(iconTexture.Width>>1, iconTexture.Height>>1,0),
     Vector3.Empty,
     color );
    m_sprite.Transform = Matrix.Identity;
   }
  }
  protected IconTexture GetTexture(Icon icon)
  {
   object key = null;
   if(icon.Image == null)
   {
    key = (icon.TextureFileName.ToLower().StartsWith("http://") ? icon.SaveFilePath : icon.TextureFileName);
   }
   else
   {
    key = icon.Image;
   }
   if(key==null)
    return null;
   IconTexture res = (IconTexture)m_textures[key];
   return res;
  }
  bool isUpdating = false;
  private void refreshTimer_Elapsed(object sender, System.Timers.ElapsedEventArgs e)
  {
   if(isUpdating)
    return;
   isUpdating = true;
   try
   {
    for(int i = 0; i < this.ChildObjects.Count; i++)
    {
     RenderableObject ro = (RenderableObject)this.ChildObjects[i];
     if(ro != null && ro.IsOn && ro is Icon)
     {
      Icon icon = (Icon)ro;
      if(icon.RefreshInterval == TimeSpan.MaxValue || icon.LastRefresh > System.DateTime.Now - icon.RefreshInterval)
       continue;
      object key = null;
      IconTexture iconTexture = null;
      if(icon.TextureFileName != null && icon.TextureFileName.Length > 0)
      {
       if(icon.TextureFileName.ToLower().StartsWith("http://") && icon.SaveFilePath != null)
       {
        try
        {
         WorldWind.Net.WebDownload webDownload = new WorldWind.Net.WebDownload(icon.TextureFileName);
         webDownload.DownloadType = WorldWind.Net.DownloadType.Unspecified;
         System.IO.FileInfo saveFile = new System.IO.FileInfo(icon.SaveFilePath);
         if(!saveFile.Directory.Exists)
          saveFile.Directory.Create();
         webDownload.DownloadFile(saveFile.FullName);
        }
        catch{}
        iconTexture = (IconTexture)m_textures[icon.SaveFilePath];
        if(iconTexture != null)
        {
         IconTexture tempTexture = iconTexture;
         m_textures[icon.SaveFilePath] = new IconTexture( DrawArgs.Device, icon.SaveFilePath );
         tempTexture.Dispose();
        }
        else
        {
         key = icon.SaveFilePath;
         iconTexture = new IconTexture( DrawArgs.Device, icon.SaveFilePath );
         m_textures.Add(key, iconTexture);
         if(icon.Width==0)
          icon.Width = iconTexture.Width;
         if(icon.Height==0)
          icon.Height = iconTexture.Height;
        }
       }
       else
       {
        iconTexture = (IconTexture)m_textures[icon.TextureFileName];
        if(iconTexture != null)
        {
         IconTexture tempTexture = iconTexture;
         m_textures[icon.SaveFilePath] = new IconTexture( DrawArgs.Device, icon.TextureFileName );
         tempTexture.Dispose();
        }
        else
        {
         key = icon.SaveFilePath;
         iconTexture = new IconTexture( DrawArgs.Device, icon.TextureFileName );
         m_textures.Add(key, iconTexture);
         if(icon.Width==0)
          icon.Width = iconTexture.Width;
         if(icon.Height==0)
          icon.Height = iconTexture.Height;
        }
       }
      }
      else
      {
       if(icon.Image != null)
       {
        iconTexture = (IconTexture)m_textures[icon.Image];
        if(iconTexture != null)
        {
         IconTexture tempTexture = iconTexture;
         m_textures[icon.SaveFilePath] = new IconTexture( DrawArgs.Device, icon.Image );
         tempTexture.Dispose();
        }
        else
        {
         key = icon.SaveFilePath;
         iconTexture = new IconTexture( DrawArgs.Device, icon.Image );
         m_textures.Add(key, iconTexture);
         if(icon.Width==0)
          icon.Width = iconTexture.Width;
         if(icon.Height==0)
          icon.Height = iconTexture.Height;
        }
       }
      }
      icon.LastRefresh = System.DateTime.Now;
     }
    }
   }
   catch{}
   finally
   {
    isUpdating = false;
   }
  }
 }
 public class Icon : RenderableObject
 {
  public double OnClickZoomAltitude = double.NaN;
  public double OnClickZoomHeading = double.NaN;
  public double OnClickZoomTilt = double.NaN;
  public string SaveFilePath = null;
  public System.DateTime LastRefresh = System.DateTime.MinValue;
  public System.TimeSpan RefreshInterval = System.TimeSpan.MaxValue;
  private Angle m_rotation = Angle.Zero;
  private bool m_isRotated = false;
  private Point3d m_positionD = new Point3d();
  bool m_nameAlwaysVisible = false;
  public bool NameAlwaysVisible
  {
   get{ return m_nameAlwaysVisible; }
   set{ m_nameAlwaysVisible = value; }
  }
  public bool IsRotated
  {
   get
   {
    return m_isRotated;
   }
   set
   {
    m_isRotated = value;
   }
  }
  public Angle Rotation
  {
   get
   {
    return m_rotation;
   }
   set
   {
    m_rotation = value;
   }
  }
  System.Collections.ArrayList overlays = new ArrayList();
  public void OverlayOnOpen(object o, EventArgs e)
  {
   System.Windows.Forms.MenuItem mi = (System.Windows.Forms.MenuItem)o;
   foreach(ScreenOverlay overlay in overlays)
   {
    if(overlay == null)
     continue;
    if(overlay.Name.Equals(mi.Text))
    {
     if(!overlay.IsOn)
      overlay.IsOn = true;
    }
   }
  }
  public ScreenOverlay[] Overlays
  {
   get
   {
    if(overlays == null)
    {
     return null;
    }
    else
    {
     return (ScreenOverlay[])overlays.ToArray(typeof(ScreenOverlay));
    }
   }
  }
  public void AddOverlay(ScreenOverlay overlay)
  {
   if(overlay != null)
    overlays.Add(overlay);
  }
  public void RemoveOverlay(ScreenOverlay overlay)
  {
   for(int i = 0; i < overlays.Count; i++)
   {
    ScreenOverlay curOverlay = (ScreenOverlay)overlays[i];
    if(curOverlay.IconImagePath == overlay.IconImagePath && overlay.Name == curOverlay.Name)
    {
     overlays.RemoveAt(i);
    }
   }
  }
  protected string m_clickableActionURL;
  protected double m_latitude;
  protected double m_longitude;
  public double Altitude;
  public string TextureFileName;
  public Bitmap Image;
  public int Width;
  public int Height;
  public string ClickableActionURL
  {
   get
   {
    return m_clickableActionURL;
   }
   set
   {
    isSelectable = value != null;
    m_clickableActionURL = value;
   }
  }
  public Point3d PositionD
  {
   get{ return m_positionD; }
   set{ m_positionD = value; }
  }
  public double MaximumDisplayDistance = double.MaxValue;
  public double MinimumDisplayDistance;
  public Rectangle SelectionRectangle;
  public double Latitude
  {
   get { return m_latitude; }
  }
  public double Longitude
  {
   get { return m_longitude; }
  }
  public Icon(string name,
   double latitude,
   double longitude) : base( name )
  {
   m_latitude = latitude;
   m_longitude = longitude;
   this.RenderPriority = RenderPriority.Icons;
  }
  public Icon(string name,
   double latitude,
   double longitude,
   double heightAboveSurface) : base( name )
  {
   m_latitude = latitude;
   m_longitude = longitude;
   Altitude = heightAboveSurface;
   this.RenderPriority = RenderPriority.Icons;
  }
  [Obsolete]
  public Icon(string name,
   double latitude,
   double longitude,
   double heightAboveSurface,
   World parentWorld ) : base( name )
  {
   m_latitude = latitude;
   m_longitude = longitude;
   this.Altitude = heightAboveSurface;
   this.RenderPriority = RenderPriority.Icons;
  }
  [Obsolete]
  public Icon(string name,
   string description,
   double latitude,
   double longitude,
   double heightAboveSurface,
   World parentWorld,
   Bitmap image,
   int width,
   int height,
   string actionURL) : base( name )
  {
   this.Description = description;
   m_latitude = latitude;
   m_longitude = longitude;
   this.Altitude = heightAboveSurface;
   this.Image = image;
   this.Width = width;
   this.Height = height;
   ClickableActionURL = actionURL;
   this.RenderPriority = RenderPriority.Icons;
  }
  [Obsolete]
  public Icon(string name,
   string description,
   double latitude,
   double longitude,
   double heightAboveSurface,
   World parentWorld,
   string TextureFileName,
   int width,
   int height,
   string actionURL) : base( name )
  {
   this.Description = description;
   m_latitude = latitude;
   m_longitude = longitude;
   this.Altitude = heightAboveSurface;
   this.TextureFileName = TextureFileName;
   this.Width = width;
   this.Height = height;
   ClickableActionURL = actionURL;
   this.RenderPriority = RenderPriority.Icons;
  }
  public void SetPosition(double latitude, double longitude)
  {
   m_latitude = latitude;
   m_longitude = longitude;
   isInitialized = false;
  }
  public void SetPosition(double latitude, double longitude, double altitude)
  {
   m_latitude = latitude;
   m_longitude = longitude;
   Altitude = altitude;
   isInitialized = false;
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   double samplesPerDegree = 50.0 / (drawArgs.WorldCamera.ViewRange.Degrees);
   double elevation = drawArgs.CurrentWorld.TerrainAccessor.GetElevationAt(m_latitude, m_longitude, samplesPerDegree);
   double altitude = (World.Settings.VerticalExaggeration * Altitude + World.Settings.VerticalExaggeration * elevation);
   Position = MathEngine.SphericalToCartesian(m_latitude, m_longitude,
    altitude + drawArgs.WorldCamera.WorldRadius);
   m_positionD = MathEngine.SphericalToCartesianD(
    Angle.FromDegrees(m_latitude),
    Angle.FromDegrees(m_longitude),
    altitude + drawArgs.WorldCamera.WorldRadius);
   isInitialized = true;
  }
  public override void Dispose()
  {
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   return false;
  }
  Matrix lastView = Matrix.Identity;
  public override void Update(DrawArgs drawArgs)
  {
   if(drawArgs.WorldCamera.ViewMatrix != lastView && drawArgs.CurrentWorld.TerrainAccessor != null && drawArgs.WorldCamera.Altitude < 300000)
   {
    double samplesPerDegree = 50.0 / drawArgs.WorldCamera.ViewRange.Degrees;
    double elevation = drawArgs.CurrentWorld.TerrainAccessor.GetElevationAt(m_latitude, m_longitude, samplesPerDegree);
    double altitude = World.Settings.VerticalExaggeration * Altitude + World.Settings.VerticalExaggeration * elevation;
    Position = MathEngine.SphericalToCartesian(m_latitude, m_longitude,
     altitude + drawArgs.WorldCamera.WorldRadius);
    lastView = drawArgs.WorldCamera.ViewMatrix;
   }
   if(overlays != null)
   {
    for(int i = 0; i < overlays.Count; i++)
    {
     ScreenOverlay curOverlay = (ScreenOverlay)overlays[i];
     if(curOverlay != null)
     {
      curOverlay.Update(drawArgs);
     }
    }
   }
  }
  public override void Render(DrawArgs drawArgs)
  {
   if(overlays != null)
   {
    for(int i = 0; i < overlays.Count; i++)
    {
     ScreenOverlay curOverlay = (ScreenOverlay)overlays[i];
     if(curOverlay != null && curOverlay.IsOn)
     {
      curOverlay.Render(drawArgs);
     }
    }
   }
  }
  private void RefreshTimer_Elapsed(object sender, System.Timers.ElapsedEventArgs e)
  {
  }
 }
}
