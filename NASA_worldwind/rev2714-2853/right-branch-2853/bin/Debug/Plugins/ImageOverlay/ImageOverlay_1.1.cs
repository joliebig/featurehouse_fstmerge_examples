using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System;
using System.Globalization;
using System.Diagnostics;
using System.IO;
using System.Windows.Forms;
using System.Drawing;
using WorldWind;
using WorldWind.Renderable;
using System.Collections;
using System.ComponentModel;
namespace Mashiharu.PluginSample
{
 public class ImageOverlayPlugin : WorldWind.PluginEngine.Plugin
 {
  public string Name = "Image Overlay 4";
  ImageOverlayList overlay;
  MenuItem menuOverlay;
  public override void Load()
  {
   overlay = new ImageOverlayList(this);
   Application.WorldWindow.CurrentWorld.RenderableObjects.Add(overlay);
   menuOverlay = new MenuItem(Name);
   Application.PluginsMenu.MenuItems.Add(menuOverlay);
   MenuItem menuAdd = new MenuItem("Add", new EventHandler(overlay.AddImage));
   menuOverlay.MenuItems.Add(menuAdd);
  }
  public override void Unload()
  {
   Application.WorldWindow.CurrentWorld.RenderableObjects.Remove(overlay.Name);
   Application.PluginsMenu.MenuItems.Remove(menuOverlay);
  }
 }
 public enum Corner
 {
  None,
  UL,
  UR,
  LL,
  LR,
  MM,
  MR
 }
 public class ImageOverlayList : RenderableObjectList
 {
  public static MainApplication ParentApplication;
  public static float Radius;
  public ImageOverlayPlugin Plugin;
  internal ImageOverlay m_selectedOverlay;
  internal Texture CornerTexture;
  internal Sprite Sprite;
  internal Corner DragCorner;
  ImageOverlayDialog properties;
  ContextMenu menu;
  string defaultImage;
  int mouseDownX;
  int mouseDownY;
  DrawArgs drawArgs;
  int showMenu;
  public ImageOverlayList(ImageOverlayPlugin plugin) : base(plugin.Name)
  {
   this.Plugin = plugin;
   ParentApplication = plugin.ParentApplication;
   Radius = (float)ParentApplication.WorldWindow.CurrentWorld.EquatorialRadius;
   ImageOverlay.Parent = this;
   Load();
   IsOn = true;
   ParentApplication.WorldWindow.MouseUp += new MouseEventHandler(WorldWindow_MouseUp);
   ParentApplication.WorldWindow.MouseDown += new MouseEventHandler(WorldWindow_MouseDown);
   ParentApplication.WorldWindow.MouseMove += new MouseEventHandler(WorldWindow_MouseMove);
   defaultImage = Path.Combine( plugin.PluginDirectory, "default.png" );
  }
  public ImageOverlay SelectedOverlay
  {
   get
   {
    return m_selectedOverlay;
   }
   set
   {
    m_selectedOverlay = value;
    if(properties!=null)
     properties.SelectedOverlay = value;
   }
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   Dispose();
   this.drawArgs = drawArgs;
   Sprite = new Sprite(drawArgs.device);
   menu = new ContextMenu();
   menu.MenuItems.Add( new MenuItem("&Remove", new EventHandler(Menu_Remove)));
   menu.MenuItems.Add( new MenuItem("&Properties", new EventHandler(Menu_Properties)));
   if(CornerTexture == null)
   {
    try
    {
     string texturePath = Path.Combine( Plugin.PluginDirectory, "corner.png");
     CornerTexture = ImageHelper.LoadTexture(texturePath);
    }
    catch( Exception caught )
    {
     MessageBox.Show(caught.Message, name);
     isOn = false;
     return;
    }
   }
   isInitialized = true;
  }
  public override void Render(DrawArgs drawArgs)
  {
   if(!IsOn)
    return;
   if(!isInitialized)
    return;
   base.Render(drawArgs);
   if(showMenu>0)
   {
    showMenu++;
    if(showMenu > 2)
    {
     menu.Show(ParentApplication.WorldWindow, new Point(
      DrawArgs.LastMousePosition.X,
      DrawArgs.LastMousePosition.Y ));
     showMenu = 0;
    }
   }
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   ImageOverlay previousSelected = SelectedOverlay;
   if(SelectedOverlay != null)
   {
    if(SelectedOverlay.PerformSelectionAction(drawArgs))
     return true;
   }
   foreach(RenderableObject ro in ChildObjects)
   {
    if(!ro.IsOn || !ro.isSelectable)
     continue;
    if(ro==previousSelected)
     continue;
    if (ro.PerformSelectionAction(drawArgs))
     return true;
   }
   return false;
  }
  public override void Dispose()
  {
   isInitialized = false;
   Save();
   base.Dispose();
   if(CornerTexture!=null)
   {
    CornerTexture.Dispose();
    CornerTexture = null;
   }
   if(Sprite!=null)
   {
    Sprite.Dispose();
    Sprite = null;
   }
   if(menu!=null)
   {
    menu.Dispose();
    menu = null;
   }
  }
  public void AddImage( object sender, EventArgs e )
  {
   ImageOverlay io = new ImageOverlay();
   io.Name = "New Image";
   io.Url = defaultImage;
  io.FitToScreen = true;
   Add(io);
   SelectedOverlay = io;
   Menu_Properties(this, EventArgs.Empty);
  }
  string SaveFilePath
  {
   get
   {
    return Path.Combine(Plugin.PluginDirectory, "overlays.txt");
   }
  }
  void Load()
  {
   if(!File.Exists(SaveFilePath))
    return;
   using(StreamReader sr = File.OpenText(SaveFilePath))
   {
    while(true)
    {
     string line = sr.ReadLine();
     if(line==null)
      break;
     ImageOverlay io = ImageOverlay.FromString(line);
     if(io != null)
      Add(io);
    }
   }
  }
  void Save()
  {
   using(StreamWriter sw = File.CreateText(SaveFilePath))
   {
    foreach(ImageOverlay o in ChildObjects)
     sw.WriteLine( o.ToString() );
   }
  }
  private void WorldWindow_MouseDown(object sender, MouseEventArgs e)
  {
   mouseDownX = e.X;
   mouseDownY = e.Y;
  }
  private void WorldWindow_MouseUp(object sender, MouseEventArgs e)
  {
   if(e.Button != MouseButtons.Right)
    return;
   int dx = e.X-mouseDownX;
   int dy = e.Y-mouseDownY;
   double dist = Math.Sqrt(dx*dx + dy*dy);
   if(dist > 3)
    return;
   if(SelectedOverlay != null)
   {
    if(SelectedOverlay.IsMouseInside)
    {
     showMenu++;
     return;
    }
   }
   PerformSelectionAction(drawArgs);
   if(SelectedOverlay == null)
    return;
   if(SelectedOverlay.IsMouseInside)
   {
    showMenu++;
    return;
   }
  }
  private void WorldWindow_MouseMove(object sender, MouseEventArgs e)
  {
   if(DragCorner==Corner.None)
    return;
   if(SelectedOverlay==null)
    return;
   SelectedOverlay.MouseMove(e);
  }
  public void ShowProperties( ImageOverlay ro)
  {
   if(properties!=null)
    properties.Dispose();
   properties = new ImageOverlayDialog(ro);
   properties.Icon = ParentApplication.Icon;
   properties.Show();
  }
  private void Menu_Remove(object sender, EventArgs e)
  {
   if(SelectedOverlay==null)
    return;
   string msg = "Are you sure you want to remove " + SelectedOverlay.Name+"?";
   if(MessageBox.Show(msg, "Remove image",
    MessageBoxButtons.YesNo, MessageBoxIcon.Question)!=DialogResult.Yes)
    return;
   ChildObjects.Remove(SelectedOverlay);
   SelectedOverlay.Dispose();
   SelectedOverlay = null;
  }
  private void Menu_Properties(object sender, EventArgs e)
  {
   if(SelectedOverlay==null)
    return;
   ShowProperties(SelectedOverlay);
  }
 }
 public class ImageOverlay : ImageLayer
 {
  DrawArgs drawArgs;
  Point offset = new Point(4,4);
  float closestDistanceSquared;
  Corner closestPointcorner;
  Vector3 ul;
  Vector3 ur;
  Vector3 ll;
  Vector3 lr;
  Vector3 mm;
  Vector3 mr;
  double[,] pos_p ;
  double[] a ;
  double[] b ;
  double angle ;
  bool isMouseOverDragHandle;
  public static ImageOverlayList Parent;
  public bool FitToScreen;
  public ImageOverlay() : base("",
   ImageOverlayList.Radius)
  {
   pos_p = new double[6,2] ;
   a = new double[6];
   b = new double[6];
   MinLat = -10;
   MaxLat = 10;
   MinLon = -10;
   MaxLon = 10;
   pos_p[0,0] = 10 ;
   pos_p[0,1] = -10 ;
   pos_p[1,0] = 10 ;
   pos_p[1,1] = 10 ;
   pos_p[2,0] = -10 ;
   pos_p[2,1] = 10 ;
   pos_p[3,0] = -10 ;
   pos_p[3,1] = 10 ;
   pos_p[4,0] = 0 ;
   pos_p[4,1] = 0 ;
   pos_p[5,0] = 10 ;
   pos_p[5,1] = 0 ;
   Opacity = 128;
   DisableZBuffer = true;
   isSelectable = true;
   this._terrainAccessor=ImageOverlayList.ParentApplication.WorldWindow.CurrentWorld.TerrainAccessor;
   IsOn = true;
   meshPointCount = 32;
  }
  public string Url
  {
   get
   {
    if(ImagePath!=null)
     return ImagePath;
    return ImageUrl;
   }
   set
   {
    if(value == null)
    {
     ImageUrl = null;
     ImagePath = null;
     return;
    }
    if(value.ToLower().StartsWith("http://"))
    {
     if(ImageUrl==value)
      return;
     ImagePath = null;
     ImageUrl = value;
    }
    else
    {
     if(!Path.IsPathRooted(value))
      value = Path.Combine(Parent.Plugin.PluginDirectory, value);
     ImageUrl = null;
     ImagePath = value;
    }
    Dispose();
   }
  }
     public void UpdateAngle(double percent)
  {
   this.angle = percent;
   this.isInitialized = false;
   CreateMesh();
   this.isInitialized = true;
  }
  public void get_ab ()
    {
    double[,] A_moins1 = {{ 1.00000, -2.00000, -1.00000, 1.00000, 0.00000, 1.00000 },
    { 0.00000, 2.00000, -2.00000, -1.00000, 2.00000, -1.00000},
    { 0.00000, -1.00000, 1.00000, 1.00000, 0.00000, -1.00000 },
    { 0.00000, 1.00000, -2.00000, -1.00000, 2.00000, 1.00000 },
    { 0.00000, 4.00000, 0.00000, -4.00000, 0.00000, 0.00000 },
    { -0.00000, -4.00000, 4.00000, 4.00000, -4.00000, -0.00000 }};
     a[0] = A_moins1[0,0] * pos_p[0,0] + A_moins1[1,0] * pos_p[1,0] + A_moins1[2,0] * pos_p[2,0] +
            A_moins1[3,0] * pos_p[3,0] + A_moins1[4,0] * pos_p[4,0] + A_moins1[5,0] * pos_p[5,0] ;
     a[1] = A_moins1[0,1] * pos_p[0,0] + A_moins1[1,1] * pos_p[1,0] + A_moins1[2,1] * pos_p[2,0] +
            A_moins1[3,1] * pos_p[3,0] + A_moins1[4,1] * pos_p[4,0] + A_moins1[5,1] * pos_p[5,0] ;
     a[2] = A_moins1[0,2] * pos_p[0,0] + A_moins1[1,2] * pos_p[1,0] + A_moins1[2,2] * pos_p[2,0] +
            A_moins1[3,2] * pos_p[3,0] + A_moins1[4,2] * pos_p[4,0] + A_moins1[5,2] * pos_p[5,0] ;
     a[3] = A_moins1[0,3] * pos_p[0,0] + A_moins1[1,3] * pos_p[1,0] + A_moins1[2,3] * pos_p[2,0] +
            A_moins1[3,3] * pos_p[3,0] + A_moins1[4,3] * pos_p[4,0] + A_moins1[5,3] * pos_p[5,0] ;
     a[4] = A_moins1[0,4] * pos_p[0,0] + A_moins1[1,4] * pos_p[1,0] + A_moins1[2,4] * pos_p[2,0] +
            A_moins1[3,4] * pos_p[3,0] + A_moins1[4,4] * pos_p[4,0] + A_moins1[5,4] * pos_p[5,0] ;
     a[5] = A_moins1[0,5] * pos_p[0,0] + A_moins1[1,5] * pos_p[1,0] + A_moins1[2,5] * pos_p[2,0] +
            A_moins1[3,5] * pos_p[3,0] + A_moins1[4,5] * pos_p[4,0] + A_moins1[5,5] * pos_p[5,0] ;
     b[0] = A_moins1[0,0] * pos_p[0,1] + A_moins1[1,0] * pos_p[1,1] + A_moins1[2,0] * pos_p[2,1] +
            A_moins1[3,0] * pos_p[3,1] + A_moins1[4,0] * pos_p[4,1] + A_moins1[5,0] * pos_p[5,1] ;
     b[1] = A_moins1[0,1] * pos_p[0,1] + A_moins1[1,1] * pos_p[1,1] + A_moins1[2,1] * pos_p[2,1] +
            A_moins1[3,1] * pos_p[3,1] + A_moins1[4,1] * pos_p[4,1] + A_moins1[5,1] * pos_p[5,1] ;
     b[2] = A_moins1[0,2] * pos_p[0,1] + A_moins1[1,2] * pos_p[1,1] + A_moins1[2,2] * pos_p[2,1] +
            A_moins1[3,2] * pos_p[3,1] + A_moins1[4,2] * pos_p[4,1] + A_moins1[5,2] * pos_p[5,1] ;
     b[3] = A_moins1[0,3] * pos_p[0,1] + A_moins1[1,3] * pos_p[1,1] + A_moins1[2,3] * pos_p[2,1] +
            A_moins1[3,3] * pos_p[3,1] + A_moins1[4,3] * pos_p[4,1] + A_moins1[5,3] * pos_p[5,1] ;
     b[4] = A_moins1[0,4] * pos_p[0,1] + A_moins1[1,4] * pos_p[1,1] + A_moins1[2,4] * pos_p[2,1] +
            A_moins1[3,4] * pos_p[3,1] + A_moins1[4,4] * pos_p[4,1] + A_moins1[5,4] * pos_p[5,1] ;
     b[5] = A_moins1[0,5] * pos_p[0,1] + A_moins1[1,5] * pos_p[1,1] + A_moins1[2,5] * pos_p[2,1] +
            A_moins1[3,5] * pos_p[3,1] + A_moins1[4,5] * pos_p[4,1] + A_moins1[5,5] * pos_p[5,1] ;
    }
   public double get_lat2 ( double Tu,double Tv)
     {
     return (a[0] + a[1]*Tu + a[2]*Tv + a[3]*Tu*Tu + a[4]*Tv*Tv + a[5]*Tu*Tv) ;
     }
   public double get_lon2 ( double Tu,double Tv)
     {
     return (b[0] + b[1]*Tu + b[2]*Tv + b[3]*Tu*Tu + b[4]*Tv*Tv + b[5]*Tu*Tv) ;
     }
   protected override void CreateMesh()
  {
   int upperBound = meshPointCount - 1;
   double scale2 = (double)1/(upperBound*upperBound);
   float scaleFactor = (float)1/upperBound;
   float latrange = (float)Math.Abs(maxLat - minLat);
   float lonrange;
   double lat;
   double lon;
   if(minLon < maxLon)
    lonrange = (float)(maxLon - minLon);
   else
    lonrange = (float)(360.0f + maxLon - minLon);
   int opacityColor = System.Drawing.Color.FromArgb(this.Opacity,0,0,0).ToArgb();
   int opacityColor2 = System.Drawing.Color.FromArgb(250,0,0,0).ToArgb();
   vertices = new CustomVertex.PositionColoredTextured[meshPointCount * meshPointCount];
   get_ab();
   for(int i = 0; i < meshPointCount; i++)
   {
    for(int j = 0; j < meshPointCount; j++)
    {
     double height = 0;
    lat = get_lat2 ( j*scaleFactor , i*scaleFactor);
    lon = get_lon2 ( j*scaleFactor , i*scaleFactor);
     if(this._terrainAccessor != null)
      height = this.verticalExaggeration * this._terrainAccessor.GetElevationAt(
     lat,
     lon,
       (double)upperBound / latrange);
     Vector3 pos = MathEngine.SphericalToCartesian(
      lat,
      lon,
      layerRadius + height);
     vertices[i*meshPointCount + j].X = pos.X;
     vertices[i*meshPointCount + j].Y = pos.Y;
     vertices[i*meshPointCount + j].Z = pos.Z;
     vertices[i*meshPointCount + j].Tu = j*scaleFactor;
     vertices[i*meshPointCount + j].Tv = i*scaleFactor;
     vertices[i*meshPointCount + j].Color = opacityColor;
    }
   }
  for(int i = 0; i < upperBound; i++)
    {
    vertices[i*meshPointCount + upperBound].Color = opacityColor2;
    vertices[i*meshPointCount + 0].Color = opacityColor2;
    vertices[i*meshPointCount + upperBound-1].Color = opacityColor2;
    vertices[i*meshPointCount + 1].Color = opacityColor2;
    vertices[0*meshPointCount + i].Color = opacityColor2;
    vertices[1*meshPointCount + i].Color = opacityColor2;
    vertices[(upperBound-1)*meshPointCount + i].Color = opacityColor2;
    vertices[upperBound*meshPointCount + i].Color = opacityColor2;
    }
   indices = new short[2 * upperBound * upperBound * 3];
   for(int i = 0; i < upperBound; i++)
   {
    for(int j = 0; j < upperBound; j++)
    {
     indices[(2*3*i*upperBound) + 6*j] = (short)(i*meshPointCount + j);
     indices[(2*3*i*upperBound) + 6*j + 1] = (short)((i+1)*meshPointCount + j);
     indices[(2*3*i*upperBound) + 6*j + 2] = (short)(i*meshPointCount + j+1);
     indices[(2*3*i*upperBound) + 6*j + 3] = (short)(i*meshPointCount + j+1);
     indices[(2*3*i*upperBound) + 6*j + 4] = (short)((i+1)*meshPointCount + j);
     indices[(2*3*i*upperBound) + 6*j + 5] = (short)((i+1)*meshPointCount + j+1);
    }
   }
  }
  public override void Render(DrawArgs drawArgs)
  {
   try
   {
    if(!isInitialized)
     return;
    if(FitToScreen)
    {
     MakeVisible();
     FitToScreen = false;
    }
    Vector3 referenceCenter = new Vector3(
     (float)drawArgs.WorldCamera.ReferenceCenter.X,
     (float)drawArgs.WorldCamera.ReferenceCenter.Y,
     (float)drawArgs.WorldCamera.ReferenceCenter.Z
     );
    closestDistanceSquared = float.MaxValue;
    if(Parent.SelectedOverlay == this)
    {
     Parent.Sprite.Begin(SpriteFlags.None);
     DoCorner(drawArgs, Corner.UL, ul - referenceCenter);
     DoCorner(drawArgs, Corner.UR, ur - referenceCenter);
     DoCorner(drawArgs, Corner.LL, ll - referenceCenter);
     DoCorner(drawArgs, Corner.LR, lr - referenceCenter);
     DoCorner(drawArgs, Corner.MM, mm - referenceCenter);
     DoCorner(drawArgs, Corner.MR, mr - referenceCenter);
     Parent.Sprite.End();
     if(Parent.DragCorner != Corner.None)
     {
      DrawArgs.MouseCursor = CursorType.Cross;
      return;
     }
     if(closestDistanceSquared < 30)
     {
      DrawArgs.MouseCursor = CursorType.Cross;
      isMouseOverDragHandle = true;
     }
     else
      isMouseOverDragHandle = false;
    }
   }
   finally
   {
    base.Render(drawArgs);
   }
  }
  void DoCorner(DrawArgs drawArgs, Corner corner, Vector3 worldPoint )
  {
   if(!drawArgs.WorldCamera.ViewFrustum.ContainsPoint(worldPoint))
    return;
   Vector3 pointScreenXy = drawArgs.WorldCamera.Project(worldPoint);
   float dx = pointScreenXy.X - DrawArgs.LastMousePosition.X;
   float dy = pointScreenXy.Y - DrawArgs.LastMousePosition.Y;
   float distSq = dx*dx + dy*dy;
   if(distSq < closestDistanceSquared)
   {
    closestDistanceSquared = distSq;
    closestPointcorner = corner;
   }
   Parent.Sprite.Draw2D(Parent.CornerTexture, offset, 0, new Point((int)pointScreenXy.X, (int)pointScreenXy.Y), -1);
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   this.drawArgs = drawArgs;
   base.Initialize(drawArgs);
   UpdateCorners();
  }
  public override void Dispose()
  {
   base.Dispose();
  }
  public double min(double f1,double f2)
    {
    if (f1<f2) return f1;
          else return f2;
    }
  public double max(double f1,double f2)
    {
    if (f1>f2) return f1;
          else return f2;
    }
  public void GetMinMax()
    {
    minLat = (float)min (min(pos_p[0,0],pos_p[1,0]),min(pos_p[2,0],pos_p[3,0]));
    maxLat = (float)max (max(pos_p[0,0],pos_p[1,0]),max(pos_p[2,0],pos_p[3,0]));
    minLon = (float)min(min(pos_p[0,1],pos_p[1,1]),min(pos_p[2,1],pos_p[3,1]) ) ;
    maxLon = (float)max(max(pos_p[0,1],pos_p[1,1]),max(pos_p[2,1],pos_p[3,1]) ) ;
    }
  public bool IsMouseInside
  {
   get
   {
    Angle latitude, longitude;
    drawArgs.WorldCamera.PickingRayIntersection(
     DrawArgs.LastMousePosition.X,
     DrawArgs.LastMousePosition.Y,
     out latitude,
     out longitude );
    if(Angle.IsNaN(latitude))
     return false;
    if(Angle.IsNaN(longitude))
     return false;
    bool clickedInside = true;
    if(latitude.Degrees < minLat)
     clickedInside = false;
    else if(latitude.Degrees > maxLat)
     clickedInside = false;
    else if(longitude.Degrees < minLon)
     clickedInside = false;
    else if(longitude.Degrees > maxLon)
     clickedInside = false;
    return clickedInside;
   }
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   if(!isInitialized)
    return false;
   if(Parent.DragCorner != Corner.None)
   {
    Parent.DragCorner = Corner.None;
    return true;
   }
   if(Parent.SelectedOverlay==null && IsMouseInside)
   {
    Parent.SelectedOverlay = this;
    return true;
   }
   if(Parent.SelectedOverlay == this)
   {
    if(!isMouseOverDragHandle)
    {
     Parent.SelectedOverlay = null;
     return false;
    }
   }
   if(!isMouseOverDragHandle)
    return false;
   Parent.DragCorner = closestPointcorner;
   return true;
  }
  public override void BuildContextMenu(ContextMenu menu)
  {
   base.BuildContextMenu(menu);
   menu.MenuItems.Add("Overlay Properties", new EventHandler(OnProperties));
  }
  void OnProperties(object sender, EventArgs e)
  {
   Parent.ShowProperties(this);
  }
  void UpdateCorners()
  {
   if(minLon > maxLon)
   {
    float tmp = (float)minLon;
    minLon = maxLon;
    maxLon = tmp;
   }
   if(minLat > maxLat)
   {
    float tmp = (float)minLat;
    minLat = maxLat;
    maxLat = tmp;
   }
   float dlat = (float)(minLat-maxLat);
   float dlon = (float)(maxLon - minLon);
  float theHeightUL = this._terrainAccessor.GetElevationAt(pos_p[0,0],pos_p[0,1],meshPointCount);
  float theHeightUR = this._terrainAccessor.GetElevationAt(pos_p[1,0],pos_p[1,1],meshPointCount);
  float theHeightLL = this._terrainAccessor.GetElevationAt(pos_p[2,0],pos_p[2,1],meshPointCount);
  float theHeightLR = this._terrainAccessor.GetElevationAt(pos_p[3,0],pos_p[3,1],meshPointCount);
  float theHeightMM = this._terrainAccessor.GetElevationAt(pos_p[4,0],pos_p[4,1],meshPointCount);
  float theHeightMR = this._terrainAccessor.GetElevationAt(pos_p[5,0],pos_p[5,1],meshPointCount);
   ul = MathEngine.SphericalToCartesian(pos_p[0,0],pos_p[0,1], layerRadius+10+(theHeightUL*verticalExaggeration));
   ur = MathEngine.SphericalToCartesian(pos_p[1,0],pos_p[1,1], layerRadius+10+(theHeightUR*verticalExaggeration));
   ll = MathEngine.SphericalToCartesian(pos_p[2,0],pos_p[2,1], layerRadius+10+(theHeightLL*verticalExaggeration));
   lr = MathEngine.SphericalToCartesian(pos_p[3,0],pos_p[3,1], layerRadius+10+(theHeightUR*verticalExaggeration));
   mm = MathEngine.SphericalToCartesian(pos_p[4,0],pos_p[4,1], layerRadius+10+(theHeightLL*verticalExaggeration));
   mr = MathEngine.SphericalToCartesian(pos_p[5,0],pos_p[5,1], layerRadius+10+(theHeightUR*verticalExaggeration));
  }
  void MakeVisible()
  {
   try
   {
    int centerX = drawArgs.screenWidth / 2;
    int centerY = drawArgs.screenHeight / 2;
    int margin = 50;
    this.maxLat = (float)drawArgs.WorldCamera.Latitude.Degrees + 20f;
    this.minLat = (float)drawArgs.WorldCamera.Latitude.Degrees - 20f;
    this.minLon = (float)drawArgs.WorldCamera.Longitude.Degrees - 20f;
    this.maxLon = (float)drawArgs.WorldCamera.Longitude.Degrees + 20f;
    Angle latitude, longitude;
    drawArgs.WorldCamera.PickingRayIntersection(centerX, margin, out latitude, out longitude);
    if(Angle.IsNaN(latitude))
     return;
    maxLat = (float)latitude.Degrees;
    drawArgs.WorldCamera.PickingRayIntersection(centerX, drawArgs.screenHeight - margin, out latitude, out longitude);
    if(Angle.IsNaN(latitude))
     return;
    minLat = (float)latitude.Degrees;
    drawArgs.WorldCamera.PickingRayIntersection(margin, centerY, out latitude, out longitude);
    if(Angle.IsNaN(longitude))
     return;
    minLon = (float)longitude.Degrees;
    drawArgs.WorldCamera.PickingRayIntersection(drawArgs.screenWidth - margin, centerY, out latitude, out longitude);
    if(Angle.IsNaN(longitude))
     return;
    maxLon = (float)longitude.Degrees;
    drawArgs.WorldCamera.PickingRayIntersection(margin, margin, out latitude, out longitude);
    pos_p[0,1] = (double)longitude.Degrees;
    pos_p[0,0] = (double)latitude.Degrees;
    drawArgs.WorldCamera.PickingRayIntersection(drawArgs.screenWidth - margin, margin, out latitude, out longitude);
    pos_p[1,1] = (double)longitude.Degrees;
    pos_p[1,0] = (double)latitude.Degrees;
    drawArgs.WorldCamera.PickingRayIntersection(drawArgs.screenWidth - margin, drawArgs.screenHeight - margin, out latitude, out longitude);
    pos_p[3,1] = (double)longitude.Degrees;
    pos_p[3,0] = (double)latitude.Degrees;
    drawArgs.WorldCamera.PickingRayIntersection(margin, drawArgs.screenHeight - margin, out latitude, out longitude);
    pos_p[2,1] = (double)longitude.Degrees;
    pos_p[2,0] = (double)latitude.Degrees;
    drawArgs.WorldCamera.PickingRayIntersection(drawArgs.screenWidth/2, drawArgs.screenHeight /2, out latitude, out longitude);
    pos_p[4,1] = (double)longitude.Degrees;
    pos_p[4,0] = (double)latitude.Degrees;
    drawArgs.WorldCamera.PickingRayIntersection(drawArgs.screenWidth-margin, drawArgs.screenHeight /2, out latitude, out longitude);
    pos_p[5,1] = (double)longitude.Degrees;
    pos_p[5,0] = (double)latitude.Degrees;
   }
   finally
   {
    UpdateCorners();
    CreateMesh();
   }
  }
  public void MouseMove(MouseEventArgs e)
  {
   Angle latitude, longitude;
   drawArgs.WorldCamera.PickingRayIntersection(e.X, e.Y, out latitude, out longitude);
   switch(Parent.DragCorner)
   {
    case Corner.UL:
     pos_p[0,1] = (double)longitude.Degrees;
     pos_p[0,0] = (double)latitude.Degrees;
     break;
    case Corner.UR:
     pos_p[1,1] = (double)longitude.Degrees;
     pos_p[1,0] = (double)latitude.Degrees;
     break;
    case Corner.LL:
     pos_p[2,1] = (double)longitude.Degrees;
     pos_p[2,0] = (double)latitude.Degrees;
     break;
    case Corner.LR:
     pos_p[3,1] = (double)longitude.Degrees;
     pos_p[3,0] = (double)latitude.Degrees;
     break;
    case Corner.MM:
     pos_p[4,1] = (double)longitude.Degrees;
     pos_p[4,0] = (double)latitude.Degrees;
     break;
    case Corner.MR:
     pos_p[5,1] = (double)longitude.Degrees;
     pos_p[5,0] = (double)latitude.Degrees;
     break;
   }
   UpdateCorners();
   CreateMesh();
  }
  public override void Delete()
  {
   Parent.ChildObjects.Remove(this);
   Dispose();
   Parent.SelectedOverlay = null;
  }
  public static ImageOverlay FromString(string line)
  {
   string[] fields = line.Split('\t');
   ImageOverlay io = new ImageOverlay();
   io.name = fields[0];
   io.Url = fields[1];
   io.pos_p[0,0] = double.Parse( fields[2], CultureInfo.InvariantCulture);
   io.pos_p[0,1] = double.Parse( fields[3], CultureInfo.InvariantCulture);
   io.pos_p[1,0] = double.Parse( fields[4], CultureInfo.InvariantCulture);
   io.pos_p[1,1] = double.Parse( fields[5], CultureInfo.InvariantCulture);
   io.pos_p[2,0] = double.Parse( fields[6], CultureInfo.InvariantCulture);
   io.pos_p[2,1] = double.Parse( fields[7], CultureInfo.InvariantCulture);
   io.pos_p[3,0] = double.Parse( fields[8], CultureInfo.InvariantCulture);
   io.pos_p[3,1] = double.Parse( fields[9], CultureInfo.InvariantCulture);
   io.pos_p[4,0] = double.Parse( fields[10], CultureInfo.InvariantCulture);
   io.pos_p[4,1] = double.Parse( fields[11], CultureInfo.InvariantCulture);
   io.pos_p[5,0] = double.Parse( fields[12], CultureInfo.InvariantCulture);
   io.pos_p[5,1] = double.Parse( fields[13], CultureInfo.InvariantCulture);
   io.Opacity = byte.Parse( fields[14], CultureInfo.InvariantCulture);
   io.GetMinMax();
   return io;
  }
  public override string ToString()
  {
   string url = Url == null ? "" : Url;
   url = url.Replace(Parent.Plugin.PluginDirectory,"");
   if(url.StartsWith(@"\"))
    url = url.Substring(1);
   string res = string.Format(CultureInfo.InvariantCulture,
    "{0}\t{1}\t{2}\t{3}\t{4}\t{5}\t{6}\t{7}\t{8}\t{9}\t{10}\t{11}\t{12}\t{13}\t{14}",
    name,
    url,
    pos_p[0,0],
    pos_p[0,1],
    pos_p[1,0],
    pos_p[1,1],
    pos_p[2,0],
    pos_p[2,1],
    pos_p[3,0],
    pos_p[3,1],
    pos_p[4,0],
    pos_p[4,1],
    pos_p[5,0],
    pos_p[5,1],
    Opacity );
   return res;
  }
 }
 public class ImageOverlayDialog : System.Windows.Forms.Form
 {
  private System.Windows.Forms.TrackBar opacity;
  private System.Windows.Forms.Label label2;
  private System.Windows.Forms.Label label3;
  private System.Windows.Forms.GroupBox groupBox1;
  public ImageOverlay m_selectedOverlay;
  private System.Windows.Forms.TextBox url;
  private System.Windows.Forms.TextBox name;
  private System.Windows.Forms.Button buttonBrowse;
  private System.Windows.Forms.Label label1;
  private System.Windows.Forms.Label label4;
  private System.Windows.Forms.Button buttonFitScreen;
  private System.Windows.Forms.Button buttonClose;
  private System.ComponentModel.Container components = null;
  public ImageOverlayDialog( ImageOverlay overlay )
  {
   InitializeComponent();
   SelectedOverlay = overlay;
  }
  public ImageOverlay SelectedOverlay
  {
   get
   {
    return m_selectedOverlay;
   }
   set
   {
    m_selectedOverlay = value;
    bool enabled = m_selectedOverlay != null;
    opacity.Enabled = enabled;
    name.Enabled = enabled;
    url.Enabled = enabled;
    if(m_selectedOverlay == null)
     return;
    int opacityValue = (int)(100*SelectedOverlay.OpacityPercent);
    opacity.Value = Math.Max(opacityValue, opacity.Minimum);
    name.Text = SelectedOverlay.Name;
    url.Text = SelectedOverlay.Url;
   }
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
   this.opacity = new System.Windows.Forms.TrackBar();
   this.label2 = new System.Windows.Forms.Label();
   this.label3 = new System.Windows.Forms.Label();
   this.groupBox1 = new System.Windows.Forms.GroupBox();
   this.url = new System.Windows.Forms.TextBox();
   this.name = new System.Windows.Forms.TextBox();
   this.buttonBrowse = new System.Windows.Forms.Button();
   this.label1 = new System.Windows.Forms.Label();
   this.label4 = new System.Windows.Forms.Label();
   this.buttonFitScreen = new System.Windows.Forms.Button();
   this.buttonClose = new System.Windows.Forms.Button();
   ((System.ComponentModel.ISupportInitialize)(this.opacity)).BeginInit();
   this.groupBox1.SuspendLayout();
   this.SuspendLayout();
   this.opacity.Location = new System.Drawing.Point(5, 20);
   this.opacity.Maximum = 100;
   this.opacity.Minimum = 7;
   this.opacity.Name = "opacity";
   this.opacity.Size = new System.Drawing.Size(273, 45);
   this.opacity.TabIndex = 0;
   this.opacity.TickFrequency = 8;
   this.opacity.Value = 100;
   this.opacity.Scroll += new System.EventHandler(this.opacity_Scroll);
   this.label2.Location = new System.Drawing.Point(8, 56);
   this.label2.Name = "label2";
   this.label2.Size = new System.Drawing.Size(48, 18);
   this.label2.TabIndex = 1;
   this.label2.Text = "&Clear";
   this.label3.Location = new System.Drawing.Point(229, 56);
   this.label3.Name = "label3";
   this.label3.Size = new System.Drawing.Size(48, 18);
   this.label3.TabIndex = 2;
   this.label3.Text = "&Opaque";
   this.groupBox1.Controls.Add(this.label2);
   this.groupBox1.Controls.Add(this.label3);
   this.groupBox1.Controls.Add(this.opacity);
   this.groupBox1.Location = new System.Drawing.Point(5, 119);
   this.groupBox1.Name = "groupBox1";
   this.groupBox1.Size = new System.Drawing.Size(282, 81);
   this.groupBox1.TabIndex = 5;
   this.groupBox1.TabStop = false;
   this.groupBox1.Text = "Transparency";
   this.url.Location = new System.Drawing.Point(16, 87);
   this.url.Name = "url";
   this.url.Size = new System.Drawing.Size(198, 20);
   this.url.TabIndex = 3;
   this.url.Text = "";
   this.url.Leave += new System.EventHandler(this.url_Leave);
   this.url.TextChanged += new System.EventHandler(this.url_Change);
   this.name.Location = new System.Drawing.Point(17, 31);
   this.name.Name = "name";
   this.name.Size = new System.Drawing.Size(266, 20);
   this.name.TabIndex = 1;
   this.name.Text = "";
   this.name.TextChanged += new System.EventHandler(this.name_TextChanged);
   this.buttonBrowse.Location = new System.Drawing.Point(217, 85);
   this.buttonBrowse.Name = "buttonBrowse";
   this.buttonBrowse.Size = new System.Drawing.Size(67, 23);
   this.buttonBrowse.TabIndex = 4;
   this.buttonBrowse.Text = "&Browse...";
   this.buttonBrowse.Click += new System.EventHandler(this.buttonBrowse_Click);
   this.label1.Location = new System.Drawing.Point(12, 71);
   this.label1.Name = "label1";
   this.label1.Size = new System.Drawing.Size(100, 16);
   this.label1.TabIndex = 2;
   this.label1.Text = "Filename or URL";
   this.label4.Location = new System.Drawing.Point(11, 15);
   this.label4.Name = "label4";
   this.label4.Size = new System.Drawing.Size(100, 15);
   this.label4.TabIndex = 0;
   this.label4.Text = "Name";
   this.buttonFitScreen.Location = new System.Drawing.Point(9, 214);
   this.buttonFitScreen.Name = "buttonFitScreen";
   this.buttonFitScreen.Size = new System.Drawing.Size(99, 27);
   this.buttonFitScreen.TabIndex = 6;
   this.buttonFitScreen.Text = "&Fit to screen";
   this.buttonFitScreen.Click += new System.EventHandler(this.buttonFitScreen_Click);
   this.buttonClose.Location = new System.Drawing.Point(186, 214);
   this.buttonClose.Name = "buttonClose";
   this.buttonClose.Size = new System.Drawing.Size(99, 27);
   this.buttonClose.TabIndex = 7;
   this.buttonClose.Text = "&Close";
   this.buttonClose.Click += new System.EventHandler(this.buttonClose_Click);
   this.AcceptButton = this.buttonClose;
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.ClientSize = new System.Drawing.Size(292, 249);
   this.Controls.Add(this.buttonClose);
   this.Controls.Add(this.buttonFitScreen);
   this.Controls.Add(this.label4);
   this.Controls.Add(this.label1);
   this.Controls.Add(this.buttonBrowse);
   this.Controls.Add(this.name);
   this.Controls.Add(this.url);
   this.Controls.Add(this.groupBox1);
   this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
   this.Name = "ImageOverlayDialog";
   this.Text = "Image Overlay Properties";
   ((System.ComponentModel.ISupportInitialize)(this.opacity)).EndInit();
   this.groupBox1.ResumeLayout(false);
   this.ResumeLayout(false);
  }
  private void opacity_Scroll(object sender, System.EventArgs e)
  {
   if(m_selectedOverlay == null)
    return;
   m_selectedOverlay.UpdateOpacity(opacity.Value / 100f);
  }
  private void buttonBrowse_Click(object sender, System.EventArgs e)
  {
   float minLat;
   float minLon;
   float maxLat;
   float maxLon;
   Image MyImage;
   bool goodImage=true;
   if(m_selectedOverlay == null)
    return;
   OpenFileDialog openFileDialog = new OpenFileDialog();
   if(!url.Text.ToLower().StartsWith("http:"))
    openFileDialog.FileName = url.Text;
   openFileDialog.RestoreDirectory = true;
   if(openFileDialog.ShowDialog()==DialogResult.OK)
   {
    url.Text = openFileDialog.FileName;
    MyImage=new Bitmap(url.Text);
    String fileNameNoExt = url.Text.Remove(url.Text.Length-4,4);
    string baseName="";
    int lastBackSlash = fileNameNoExt.LastIndexOf("\\");
    if (lastBackSlash!=-1)
    {
     int fileNameLength = fileNameNoExt.Length-lastBackSlash;
     baseName=fileNameNoExt.Substring(lastBackSlash+1,fileNameLength-1);
    }
    else
    {
     int lastForwardSlash = fileNameNoExt.LastIndexOf("//");
     int fileNameLength = fileNameNoExt.Length-lastForwardSlash;
     baseName=fileNameNoExt.Substring(lastForwardSlash+1,fileNameLength-1);
    }
    this.name.Text=baseName;
    String worldFileExt="";
    if (url.Text.EndsWith(".jpg"))
    {
                    worldFileExt = ".jgw";
    }
    else if (url.Text.EndsWith(".png"))
    {
                    worldFileExt = ".pgw";
    }
    else if (url.Text.EndsWith(".gif"))
    {
                     worldFileExt = ".gfw";
    }
    else if (url.Text.EndsWith(".tif"))
    {
      worldFileExt = ".tfw";
    }
    else
    {
     goodImage=false;
    }
    if (goodImage!=false)
    {
     if (File.Exists(fileNameNoExt+worldFileExt))
     {
      System.IO.StreamReader worldFile = new System.IO.StreamReader(fileNameNoExt+worldFileExt);
      String pixelSize = worldFile.ReadLine();
      worldFile.ReadLine();
      worldFile.ReadLine();
      worldFile.ReadLine();
      minLon=float.Parse(worldFile.ReadLine());
      maxLat=float.Parse(worldFile.ReadLine());
      maxLon=minLon + ((float.Parse(pixelSize)) * (MyImage.Width));
      minLat=maxLat - ((float.Parse(pixelSize)) * (MyImage.Height));
      m_selectedOverlay.MinLon=minLon;
      m_selectedOverlay.MaxLat=maxLat;
      m_selectedOverlay.MaxLon=maxLon;
      m_selectedOverlay.MinLat=minLat;
     }
    }
    else
    {
                    MessageBox.Show("This plugin only accepts png/jpg/gif/tif");
    }
   }
   m_selectedOverlay.UpdateTexture(url.Text);
   m_selectedOverlay.Url = url.Text;
  }
  private void url_Change(object sender, System.EventArgs e)
  {
  }
  private void url_Leave(object sender, System.EventArgs e)
  {
   if(m_selectedOverlay == null)
    return;
   if(m_selectedOverlay.Url == url.Text)
    return;
   float minLat;
   float minLon;
   float maxLat;
   float maxLon;
   Image MyImage;
   bool goodImage=true;
   string bitmapPath="";
   if (url.Text.ToLower().StartsWith("http://"))
   {
    bitmapPath=url.Text.Substring(5);
   }
   else
   {
    bitmapPath=url.Text;
   }
   MyImage=new Bitmap(bitmapPath);
   String fileNameNoExt = bitmapPath.Remove(bitmapPath.Length-4,4);
   String worldFileExt="";
   if (bitmapPath.EndsWith(".jpg"))
   {
    worldFileExt = ".jgw";
   }
   else if (bitmapPath.EndsWith(".png"))
   {
    worldFileExt = ".pgw";
   }
   else if (bitmapPath.EndsWith(".gif"))
   {
    worldFileExt = ".gfw";
   }
   else if (bitmapPath.EndsWith(".tif"))
   {
    worldFileExt = ".tfw";
   }
   else
   {
    goodImage=false;
   }
   if (goodImage!=false)
   {
    if (File.Exists(fileNameNoExt+worldFileExt))
    {
     System.IO.StreamReader worldFile = new System.IO.StreamReader(fileNameNoExt+worldFileExt);
     String pixelSize = worldFile.ReadLine();
     worldFile.ReadLine();
     worldFile.ReadLine();
     worldFile.ReadLine();
     minLon=float.Parse(worldFile.ReadLine());
     maxLat=float.Parse(worldFile.ReadLine());
     maxLon=minLon + ((float.Parse(pixelSize)) * (MyImage.Width));
     minLat=maxLat - ((float.Parse(pixelSize)) * (MyImage.Height));
     m_selectedOverlay.MinLon=minLon;
     m_selectedOverlay.MaxLat=maxLat;
     m_selectedOverlay.MaxLon=maxLon;
     m_selectedOverlay.MinLat=minLat;
    }
   }
   else
   {
    MessageBox.Show("This plugin only accepts png/jpg/gif/tif");
   }
   m_selectedOverlay.Url = bitmapPath;
  }
  private void name_TextChanged(object sender, System.EventArgs e)
  {
   if(m_selectedOverlay == null)
    return;
   m_selectedOverlay.Name = name.Text;
  }
  private void buttonFitScreen_Click(object sender, System.EventArgs e)
  {
   if(m_selectedOverlay == null)
    return;
   m_selectedOverlay.FitToScreen = true;
  }
  private void buttonClose_Click(object sender, System.EventArgs e)
  {
   Close();
  }
 }
 public class Win32Form2 : System.Windows.Forms.Form
 {
  private System.ComponentModel.Container components;
  public System.Windows.Forms.ProgressBar progressBar1;
  public Win32Form2()
  {
   InitializeComponent();
  }
  protected override void Dispose( bool disposing )
  {
   if( disposing )
   {
    if (components != null)
    {
     components.Dispose();
    }
   }
   base.Dispose( disposing );
  }
  private void InitializeComponent()
  {
   this.progressBar1 = new System.Windows.Forms.ProgressBar();
   this.SuspendLayout();
   this.progressBar1.Location = new System.Drawing.Point(72, 24);
   this.progressBar1.Maximum = 10;
   this.progressBar1.Name = "progressBar1";
   this.progressBar1.Size = new System.Drawing.Size(312, 40);
   this.progressBar1.Step = 1;
   this.progressBar1.TabIndex = 0;
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.ClientSize = new System.Drawing.Size(456, 85);
   this.Controls.Add(this.progressBar1);
   this.Name = "Win32Form2";
   this.Text = "Loading...";
   this.Click += new System.EventHandler(this.Win32Form2_Click);
   this.ResumeLayout(false);
  }
  protected void Win32Form2_Click(object sender, System.EventArgs e)
  {
  }
 }
}
