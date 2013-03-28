using System;
using System.IO;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind.Renderable;
using WorldWind;
using WorldWind.Configuration;
using WorldWind.Terrain;
namespace WorldWind
{
 public class World : RenderableObject
 {
  public static WorldSettings Settings = new WorldSettings();
  double equatorialRadius;
  const double flattening = 6378.135;
  const double SemiMajorAxis = 6378137.0;
  const double SemiMinorAxis = 6356752.31425;
  TerrainAccessor _terrainAccessor;
  RenderableObjectList _renderableObjects;
  private System.Collections.IList onScreenMessages;
  private DateTime lastElevationUpdate = System.DateTime.Now;
  WorldSurfaceRenderer m_WorldSurfaceRenderer = null;
  public System.Collections.IList OnScreenMessages
  {
   get
   {
    return this.onScreenMessages;
   }
   set
   {
    this.onScreenMessages = value;
   }
  }
  public WorldSurfaceRenderer WorldSurfaceRenderer
  {
   get
   {
    return m_WorldSurfaceRenderer;
   }
  }
  public bool IsEarth
  {
   get
   {
    return this.Name=="Earth";
   }
  }
  ProjectedVectorRenderer m_projectedVectorRenderer = null;
  public ProjectedVectorRenderer ProjectedVectorRenderer
  {
   get{ return m_projectedVectorRenderer; }
  }
  static World()
  {
  }
  public World(string name, Vector3 position, Quaternion orientation, double equatorialRadius,
   string cacheDirectory,
   TerrainAccessor terrainAccessor)
   : base(name, position, orientation)
  {
   this.equatorialRadius = equatorialRadius;
   this._terrainAccessor = terrainAccessor;
   this._renderableObjects = new RenderableObjectList(this.Name);
   this.MetaData.Add("CacheDirectory", cacheDirectory);
   this.m_projectedVectorRenderer = new ProjectedVectorRenderer(this);
  }
  public void SetLayerOpacity(string category, string name, float opacity)
  {
   this.setLayerOpacity(this._renderableObjects, category, name, opacity);
  }
  private static string getRenderablePathString(RenderableObject renderable)
  {
   if(renderable.ParentList == null)
   {
    return renderable.Name;
   }
   else
   {
    return getRenderablePathString(renderable.ParentList) + Path.DirectorySeparatorChar + renderable.Name;
   }
  }
  private void setLayerOpacity(RenderableObject ro, string category, string name, float opacity)
  {
   foreach(string key in ro.MetaData.Keys)
   {
    if(String.Compare(key, category, true) == 0)
    {
     if(ro.MetaData[key].GetType() == typeof(String))
     {
      string curValue = ro.MetaData[key] as string;
      if(String.Compare(curValue, name, true) == 0)
      {
       ro.Opacity = (byte)(255 * opacity);
      }
     }
     break;
    }
   }
   RenderableObjectList rol = ro as RenderableObjectList;
   if (rol != null)
   {
    foreach (RenderableObject childRo in rol.ChildObjects)
     setLayerOpacity(childRo, category, name, opacity);
   }
  }
  public static void LoadSettings()
  {
   try
   {
    Settings = (WorldSettings) SettingsBase.Load(Settings);
   }
   catch(Exception caught)
   {
    Utility.Log.Write(caught);
   }
  }
  public static void LoadSettings(string directory)
  {
   try
   {
    Settings = (WorldSettings) SettingsBase.LoadFromPath(Settings, directory);
   }
   catch(Exception caught)
   {
    Utility.Log.Write(caught);
   }
  }
  public TerrainAccessor TerrainAccessor
  {
   get
   {
    return this._terrainAccessor;
   }
   set
   {
    this._terrainAccessor = value;
   }
  }
  public double EquatorialRadius
  {
   get
   {
    return this.equatorialRadius;
   }
  }
  public RenderableObjectList RenderableObjects
  {
   get
   {
    return this._renderableObjects;
   }
   set
   {
    this._renderableObjects = value;
   }
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   try
   {
    if(this.isInitialized)
     return;
    this.RenderableObjects.Initialize(drawArgs);
   }
   catch(Exception caught)
   {
    Utility.Log.DebugWrite( caught );
   }
   finally
   {
    this.isInitialized = true;
   }
  }
  private void DrawAxis(DrawArgs drawArgs)
  {
   CustomVertex.PositionColored[] axis = new CustomVertex.PositionColored[2];
   Vector3 topV = MathEngine.SphericalToCartesian(90,0,this.EquatorialRadius + 0.15f*this.EquatorialRadius);
   axis[0].X = topV.X;
   axis[0].Y = topV.Y;
   axis[0].Z = topV.Z;
   axis[0].Color = System.Drawing.Color.Pink.ToArgb();
   Vector3 botV = MathEngine.SphericalToCartesian(-90,0,this.EquatorialRadius + 0.15f*this.EquatorialRadius);
   axis[1].X = botV.X;
   axis[1].Y = botV.Y;
   axis[1].Z = botV.Z;
   axis[1].Color = System.Drawing.Color.Pink.ToArgb();
   drawArgs.device.VertexFormat = CustomVertex.PositionColored.Format;
   drawArgs.device.TextureState[0].ColorOperation = TextureOperation.Disable;
   drawArgs.device.Transform.World = Matrix.Translation(
    (float)-drawArgs.WorldCamera.ReferenceCenter.X,
    (float)-drawArgs.WorldCamera.ReferenceCenter.Y,
    (float)-drawArgs.WorldCamera.ReferenceCenter.Z
    );
   drawArgs.device.DrawUserPrimitives(PrimitiveType.LineStrip, 1, axis);
   drawArgs.device.Transform.World = drawArgs.WorldCamera.WorldMatrix;
  }
  public override void Update(DrawArgs drawArgs)
  {
   if(!this.isInitialized)
   {
    this.Initialize(drawArgs);
   }
   if(this.RenderableObjects != null)
   {
    this.RenderableObjects.Update(drawArgs);
   }
   if(this.m_WorldSurfaceRenderer != null)
   {
    this.m_WorldSurfaceRenderer.Update(drawArgs);
   }
   if(this.m_projectedVectorRenderer != null)
   {
    this.m_projectedVectorRenderer.Update(drawArgs);
   }
   if(this.TerrainAccessor != null)
   {
    if(drawArgs.WorldCamera.Altitude < 300000)
    {
     if(System.DateTime.Now - this.lastElevationUpdate > TimeSpan.FromMilliseconds(500))
     {
      drawArgs.WorldCamera.TerrainElevation = (short)this.TerrainAccessor.GetElevationAt(drawArgs.WorldCamera.Latitude.Degrees, drawArgs.WorldCamera.Longitude.Degrees, 100.0 / drawArgs.WorldCamera.ViewRange.Degrees);
      this.lastElevationUpdate = System.DateTime.Now;
     }
    }
    else
     drawArgs.WorldCamera.TerrainElevation = 0;
   }
   else
   {
    drawArgs.WorldCamera.TerrainElevation = 0;
   }
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   return this._renderableObjects.PerformSelectionAction(drawArgs);
  }
  private void RenderSun(DrawArgs drawArgs)
  {
   Point3d sunPosition = -SunCalculator.GetGeocentricPosition(TimeKeeper.CurrentTimeUtc);
   Point3d sunSpherical = MathEngine.CartesianToSphericalD(sunPosition.X, sunPosition.Y, sunPosition.Z);
   sunPosition = MathEngine.SphericalToCartesianD(
    Angle.FromRadians(sunSpherical.Y),
    Angle.FromRadians(sunSpherical.Z),
    150000000000);
   Vector3 sunVector = new Vector3((float)sunPosition.X, (float)sunPosition.Y, (float)sunPosition.Z);
   Frustum viewFrustum = new Frustum();
   float aspectRatio = (float)drawArgs.WorldCamera.Viewport.Width / drawArgs.WorldCamera.Viewport.Height;
   Matrix projectionMatrix = Matrix.PerspectiveFovRH( (float)drawArgs.WorldCamera.Fov.Radians, aspectRatio, 1.0f, 300000000000);
   viewFrustum.Update(
    Matrix.Multiply(drawArgs.WorldCamera.AbsoluteWorldMatrix,
    Matrix.Multiply(drawArgs.WorldCamera.AbsoluteViewMatrix,
     projectionMatrix)));
   if(!viewFrustum.ContainsPoint(sunVector))
    return;
   Vector3 translationVector = new Vector3(
    (float)(sunPosition.X - drawArgs.WorldCamera.ReferenceCenter.X),
    (float)(sunPosition.Y - drawArgs.WorldCamera.ReferenceCenter.Y),
    (float)(sunPosition.Z - drawArgs.WorldCamera.ReferenceCenter.Z));
   Vector3 projectedPoint = drawArgs.WorldCamera.Project(translationVector);
   if(m_sunTexture == null)
   {
    m_sunTexture = ImageHelper.LoadTexture(Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath) + "\\Data\\sun.dds");
    m_sunSurfaceDescription = m_sunTexture.GetLevelDescription(0);
   }
   if(m_sprite == null)
   {
    m_sprite = new Sprite(drawArgs.device);
   }
   m_sprite.Begin(SpriteFlags.AlphaBlend);
   float xscale = (float)m_sunWidth / m_sunSurfaceDescription.Width;
   float yscale = (float)m_sunHeight / m_sunSurfaceDescription.Height;
   m_sprite.Transform = Matrix.Scaling(xscale,yscale,0);
   m_sprite.Transform *= Matrix.Translation(projectedPoint.X, projectedPoint.Y, 0);
   m_sprite.Draw( m_sunTexture,
    new Vector3(m_sunSurfaceDescription.Width>>1, m_sunSurfaceDescription.Height>>1,0),
    Vector3.Empty,
    System.Drawing.Color.FromArgb(253, 253, 200).ToArgb() );
   m_sprite.Transform = Matrix.Identity;
   m_sprite.End();
  }
  int m_sunWidth = 72;
  int m_sunHeight = 72;
  Sprite m_sprite = null;
  Texture m_sunTexture = null;
  SurfaceDescription m_sunSurfaceDescription;
  public override void Render(DrawArgs drawArgs)
  {
   if(m_WorldSurfaceRenderer != null && World.Settings.UseWorldSurfaceRenderer)
   {
    m_WorldSurfaceRenderer.RenderSurfaceImages(drawArgs);
   }
   RenderStars(drawArgs, RenderableObjects);
   if(World.Settings.EnableSunShading)
    RenderSun(drawArgs);
   Render(RenderableObjects, WorldWind.Renderable.RenderPriority.TerrainMappedImages, drawArgs);
   if(m_projectedVectorRenderer != null)
    m_projectedVectorRenderer.Render(drawArgs);
   Render(RenderableObjects, WorldWind.Renderable.RenderPriority.AtmosphericImages, drawArgs);
   Render(RenderableObjects, WorldWind.Renderable.RenderPriority.LinePaths, drawArgs);
   Render(RenderableObjects, WorldWind.Renderable.RenderPriority.Placenames, drawArgs);
   Render(RenderableObjects, WorldWind.Renderable.RenderPriority.Icons, drawArgs);
   Render(RenderableObjects, WorldWind.Renderable.RenderPriority.Custom, drawArgs);
   if(Settings.showPlanetAxis)
    this.DrawAxis(drawArgs);
  }
  private void RenderStars(DrawArgs drawArgs, WorldWind.Renderable.RenderableObject renderable)
  {
   if(renderable is RenderableObjectList)
   {
    RenderableObjectList rol = (RenderableObjectList)renderable;
    for(int i = 0; i < rol.ChildObjects.Count; i++)
    {
     RenderStars(drawArgs, (RenderableObject)rol.ChildObjects[i]);
    }
   }
   else if(renderable.Name != null && renderable.Name.Equals("Starfield"))
   {
    renderable.Render(drawArgs);
   }
  }
  private void Render(WorldWind.Renderable.RenderableObject renderable, WorldWind.Renderable.RenderPriority priority, DrawArgs drawArgs)
  {
   if(!renderable.IsOn || (renderable.Name != null && renderable.Name.Equals("Starfield")))
    return;
   if(priority == WorldWind.Renderable.RenderPriority.Icons && renderable is Icons)
   {
    renderable.Render(drawArgs);
   }
   else if(renderable is WorldWind.Renderable.RenderableObjectList)
   {
    WorldWind.Renderable.RenderableObjectList rol = (WorldWind.Renderable.RenderableObjectList)renderable;
    for(int i = 0; i < rol.ChildObjects.Count; i++)
    {
     Render((WorldWind.Renderable.RenderableObject)rol.ChildObjects[i], priority, drawArgs);
    }
   }
   else if(priority == WorldWind.Renderable.RenderPriority.TerrainMappedImages)
   {
    if(renderable.RenderPriority == WorldWind.Renderable.RenderPriority.SurfaceImages || renderable.RenderPriority == WorldWind.Renderable.RenderPriority.TerrainMappedImages)
    {
     renderable.Render(drawArgs);
    }
   }
   else if(renderable.RenderPriority == priority)
   {
    renderable.Render(drawArgs);
   }
  }
  private void saveRenderableState(RenderableObject ro)
  {
   string path = getRenderablePathString(ro);
   bool found = false;
   for(int i = 0; i < World.Settings.loadedLayers.Count; i++)
   {
    string s = (string)World.Settings.loadedLayers[i];
    if(s.Equals(path))
    {
     if(!ro.IsOn)
     {
      World.Settings.loadedLayers.RemoveAt(i);
      break;
     }
     else
     {
      found = true;
     }
    }
   }
   if(!found && ro.IsOn)
   {
    World.Settings.loadedLayers.Add(path);
   }
  }
  private void saveRenderableStates(RenderableObjectList rol)
  {
   saveRenderableState(rol);
   foreach(RenderableObject ro in rol.ChildObjects)
   {
    if(ro is RenderableObjectList)
    {
     RenderableObjectList childRol = (RenderableObjectList)ro;
     saveRenderableStates(childRol);
    }
    else
    {
     saveRenderableState(ro);
    }
   }
  }
  public override void Dispose()
  {
   saveRenderableStates(RenderableObjects);
   if(this.RenderableObjects!=null)
   {
    this.RenderableObjects.Dispose();
    this.RenderableObjects=null;
   }
   if(m_WorldSurfaceRenderer != null)
   {
    m_WorldSurfaceRenderer.Dispose();
   }
  }
  public static Angle ApproxAngularDistance(Angle latA, Angle lonA, Angle latB, Angle lonB )
  {
   Angle dlon = lonB - lonA;
   Angle dlat = latB - latA;
   double k = Math.Sin(dlat.Radians*0.5);
   double l = Math.Sin(dlon.Radians*0.5);
   double a = k*k + Math.Cos(latA.Radians) * Math.Cos(latB.Radians) * l*l;
   double c = 2 * Math.Asin(Math.Min(1,Math.Sqrt(a)));
   return Angle.FromRadians(c);
  }
  public double ApproxDistance(Angle latA, Angle lonA, Angle latB, Angle lonB )
  {
   double distance = equatorialRadius * ApproxAngularDistance(latA,lonA,latB,lonB).Radians;
   return distance;
  }
  public static void IntermediateGCPoint( float f, Angle lat1, Angle lon1, Angle lat2, Angle lon2, Angle d,
   out Angle lat, out Angle lon )
  {
   double sind = Math.Sin(d.Radians);
   double cosLat1 = Math.Cos(lat1.Radians);
   double cosLat2 = Math.Cos(lat2.Radians);
   double A=Math.Sin((1-f)*d.Radians)/sind;
   double B=Math.Sin(f*d.Radians)/sind;
   double x = A*cosLat1*Math.Cos(lon1.Radians) + B*cosLat2*Math.Cos(lon2.Radians);
   double y = A*cosLat1*Math.Sin(lon1.Radians) + B*cosLat2*Math.Sin(lon2.Radians);
   double z = A*Math.Sin(lat1.Radians) + B*Math.Sin(lat2.Radians);
   lat = Angle.FromRadians(Math.Atan2(z,Math.Sqrt(x*x+y*y)));
   lon = Angle.FromRadians(Math.Atan2(y,x));
  }
  public Vector3 IntermediateGCPoint( float f, Angle lat1, Angle lon1, Angle lat2, Angle lon2, Angle d )
  {
   double sind = Math.Sin(d.Radians);
   double cosLat1 = Math.Cos(lat1.Radians);
   double cosLat2 = Math.Cos(lat2.Radians);
   double A=Math.Sin((1-f)*d.Radians)/sind;
   double B=Math.Sin(f*d.Radians)/sind;
   double x = A*cosLat1*Math.Cos(lon1.Radians) + B*cosLat2*Math.Cos(lon2.Radians);
   double y = A*cosLat1*Math.Sin(lon1.Radians) + B*cosLat2*Math.Sin(lon2.Radians);
   double z = A*Math.Sin(lat1.Radians) + B*Math.Sin(lat2.Radians);
   Angle lat=Angle.FromRadians(Math.Atan2(z,Math.Sqrt(x*x+y*y)));
   Angle lon=Angle.FromRadians(Math.Atan2(y,x));
   Vector3 v = MathEngine.SphericalToCartesian(lat,lon,equatorialRadius);
   return v;
  }
 }
}
