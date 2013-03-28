using Microsoft.DirectX.Direct3D;
using Microsoft.DirectX;
using OrbitTools;
using System.Collections;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.Windows.Forms;
using System;
using Utility;
using WorldWind.Net;
using WorldWind.PluginEngine;
using WorldWind.Renderable;
using WorldWind;
using System.ComponentModel;
using System.Drawing.Design;
using System.Windows.Forms.Design;
namespace WorldWind.SatelliteTracker
{
 public struct Ray
 {
  public Vector3 origin;
  public Vector3 destination;
  public Vector3 direction;
 }
 public struct BoundingSphere
 {
  public Vector3 center;
  public float radius;
 }
 public class SatelliteTrackerPlugin : Plugin
 {
  private string[] tleList =
            {
              @"http://science.nasa.gov/realtime/JTrack/spacecraftvectors.txt",
             @"http://www.celestrak.com/NORAD/elements/tle-new.txt",
             @"http://www.celestrak.com/NORAD/elements/stations.txt",
             @"http://www.celestrak.com/NORAD/elements/weather.txt",
             @"http://www.celestrak.com/NORAD/elements/noaa.txt",
             @"http://www.celestrak.com/NORAD/elements/goes.txt",
             @"http://www.celestrak.com/NORAD/elements/resource.txt",
             @"http://www.celestrak.com/NORAD/elements/sarsat.txt",
             @"http://www.celestrak.com/NORAD/elements/tdrss.txt",
             @"http://www.celestrak.com/NORAD/elements/geo.txt",
             @"http://www.celestrak.com/NORAD/elements/intelsat.txt",
             @"http://www.celestrak.com/NORAD/elements/iridium.txt",
             @"http://www.celestrak.com/NORAD/elements/globalstar.txt",
             @"http://www.celestrak.com/NORAD/elements/gps-ops.txt",
             @"http://www.celestrak.com/NORAD/elements/science.txt",
             @"http://www.celestrak.com/NORAD/elements/engineering.txt",
             @"http://www.celestrak.com/NORAD/elements/orbcomm.txt"};
  private WorldWind.Renderable.RenderableObjectList[] m_SatElementsList;
  private WorldWind.Renderable.RenderableObjectList m_BaseList;
  public override void Load()
  {
   m_BaseList = new RenderableObjectList("Satellite Tracker");
   m_SatElementsList = new WorldWind.Renderable.RenderableObjectList[tleList.Length - 1];
   string LayerName = "";
   for(int x = 0; x < tleList.Length-1; x++)
   {
    int start = tleList[x].LastIndexOf("/")+1;
    int end = tleList[x].LastIndexOf(@".");
    LayerName = tleList[x].Substring(start,end-start);
    m_SatElementsList[x] = new SatelliteTrackerOverlay(this, LayerName.ToUpper(), tleList[x]);
    m_BaseList.Add(m_SatElementsList[x]);
   }
   m_BaseList.IsOn = true;
   Application.WorldWindow.CurrentWorld.RenderableObjects.Add(m_BaseList);
  }
  public override void Unload()
  {
   Application.WorldWindow.CurrentWorld.RenderableObjects.Remove(m_BaseList);
  }
  public void resetChildCameraFollows()
  {
   foreach(SatelliteTrackerOverlay st in this.m_BaseList.ChildObjects )
   {
    st.resetChildCameraFollows();
   }
  }
 }
 public class SatelliteTrackerOverlay : RenderableObjectList
 {
  public static MainApplication ParentApplication;
  public DrawArgs drawArgs;
  public SatelliteTrackerPlugin Plugin;
  public string tlepath = "";
  private bool m_showTracks = false;
  public Mesh mesh;
  public ExtendedMaterial[] materials;
  public Texture[] meshTextures;
  public Material[] meshMaterials;
  public Vector3 meshBoundingBoxMinValue;
  public Vector3 meshBoundingBoxMaxValue;
  private float m_scale = 200;
  [Editor(typeof(OpacityEditor),
    typeof(System.Drawing.Design.UITypeEditor))]
  [Description("Controls the amount of light allowed to pass through this object. (0=invisible, 255=opaque).")]
  public new int Opacity
  {
   get{return base.Opacity;}
   set{base.Opacity=(byte)value;}
  }
  public SatelliteTrackerOverlay(SatelliteTrackerPlugin plugin, string overlayname, string tleURL): base(overlayname)
  {
   this.tlepath = tleURL;
   this.Plugin = plugin;
   ParentApplication = plugin.ParentApplication;
   IsOn = false;
  }
  [TypeConverter(typeof(SatelliteTrackConverter))]
  public string CurrentFocus
  {
   get
   {
    foreach(SatelliteTrack st in this.ChildObjects)
    {
     if (st.CameraFollows )
     {
      return st.Name;
     }
    }
    return "none";
   }
   set
   {
    resetChildCameraFollows();
    foreach(SatelliteTrack st in this.ChildObjects)
    {
     if (st.Name.Equals(value))
     {
      st.OnFocusClick(this, new System.EventArgs());
     }
    }
   }
  }
  public bool ShowTracks
  {
   get { return m_showTracks; }
   set
   {
    SetChildrenShowTracks(value);
    m_showTracks = value;
   }
  }
  public float ModelScale
  {
   get { return m_scale; }
   set
   {
    m_scale = value;
    SetChildrenModelScale(m_scale);}
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   Dispose();
   ArrayList SatelliteList;
   SatelliteList = GetFromWeb(this.tlepath);
   SatelliteList.Sort();
   LoadMesh(drawArgs);
   try
   {
    for(int x = 0; x < SatelliteList.Count;x++)
    {
     Tle twoLineElement = (Tle) SatelliteList[x];
     SatelliteTrack iss = new SatelliteTrack(this.Plugin.PluginDirectory ,twoLineElement );
     this.Add(iss);
    }
    SetChildrenModelScale(m_scale);
   }
   catch(Exception ex)
   {
    System.Windows.Forms.MessageBox.Show(ex.Message);
    this.IsOn=false;
   }
   this.drawArgs = drawArgs;
   isInitialized = true;
  }
  public ArrayList GetFromWeb(string url )
  {
   string line1=null,line2=null;
   bool useLocalFile = false;
   ArrayList satTles = new ArrayList();
   string saveto =Path.Combine( this.Plugin.PluginDirectory,this.Name +".txt");
   using( WebDownload dl = new WebDownload(url) )
   {
    try
    {
     dl.DownloadMemory();
     dl.SaveMemoryDownloadToFile(saveto);
    }
    catch(System.Net.WebException)
    {
     useLocalFile = true;
    }
    if(useLocalFile)
     using(TextReader tr = new StreamReader( saveto ))
     {
      string[] line = tr.ReadToEnd().Split('\n');
      string NumFormat = GetNumberFormat(line);
      for(int i=0;i<line.Length-1-2;i = i + 3)
      {
       if(line[i].Trim().Length == 0)
        break;
       line1 = line[i+1].Trim();
       line2 = line[i+2].Trim();
       Tle tle = new Tle(FormatTrailingNumber(line[i].Trim(), NumFormat), line1, line2);
       satTles.Add(tle);
      }
     }
    else
     using(TextReader tr = new StreamReader( dl.ContentStream ))
     {
      string[] line = tr.ReadToEnd().Split('\n');
      string NumFormat = GetNumberFormat(line);
      for(int i=0;i<line.Length-1-2;i = i + 3)
      {
       if(line[i].Trim().Length == 0)
        break;
       line1 = line[i+1].Trim();
       line2 = line[i+2].Trim();
       Tle tle = new Tle(FormatTrailingNumber(line[i].Trim(), NumFormat), line1, line2);
       satTles.Add(tle);
      }
     }
   }
   return satTles;
  }
  private bool LoadMesh(DrawArgs drawArgs)
  {
   string LayerMeshFile =Path.Combine( this.Plugin.PluginDirectory,this.Name +".x");
   if(!System.IO.File.Exists(LayerMeshFile))
   {
    LayerMeshFile = Path.Combine( this.Plugin.PluginDirectory,"default.x");
   }
   GraphicsStream adj;
   try
   {
    mesh = Mesh.FromFile(LayerMeshFile, MeshFlags.Managed, drawArgs.device, out adj, out materials );
   }
   catch(Exception caught)
   {
    Utility.Log.Write( caught );
    string msg = "Failed to read mesh from " + LayerMeshFile;
    System.Windows.Forms.MessageBox.Show( msg, "ISS failed to load.", MessageBoxButtons.OK, MessageBoxIcon.Error);
    this.ParentList.IsOn = false;
    return false;
   }
   string textureFilePath = "";
   try
   {
    meshTextures = new Texture[materials.Length];
    meshMaterials = new Material[materials.Length];
    string xFilePath = Path.GetDirectoryName(LayerMeshFile);
    for(int i = 0; i < materials.Length; i++)
    {
     meshMaterials[i] = materials[i].Material3D;
     meshMaterials[i].Ambient = meshMaterials[i].Diffuse;
     if(materials[i].TextureFilename!=null)
     {
      textureFilePath = Path.Combine(xFilePath, materials[i].TextureFilename);
      meshTextures[i] = TextureLoader.FromFile(drawArgs.device, textureFilePath);
     }
    }
    isInitialized = true;
    GraphicsStream vertexData;
    VertexBufferDescription description =
     mesh.VertexBuffer.Description;
    vertexData = mesh.VertexBuffer.Lock
     (0, 0, LockFlags.ReadOnly);
    Geometry.ComputeBoundingBox(vertexData,
     mesh.NumberVertices,description.VertexFormat,
     out meshBoundingBoxMinValue,
     out meshBoundingBoxMaxValue);
    mesh.VertexBuffer.Unlock();
   }
   catch(Exception caught)
   {
    Utility.Log.Write( caught );
    string msg = "Failed to read Texture from " + textureFilePath;
    throw new Exception( msg);
   }
   return true;
  }
  private string GetNumberFormat(string[] strArr)
  {
   int maxDigits = 0;
   for(int i=0;i<strArr.Length-1-2;i = i + 3)
   {
    try
    {
     string s = strArr[i].Substring(strArr[i].Trim().LastIndexOf(" ")).Trim();
     int x = int.Parse(s);
     if(x.ToString().Length > 3)
      x = x;
     if(maxDigits < x.ToString().Length )
      maxDigits = x.ToString().Length;
    }
    catch
    {
    }
   }
   string sOutputFormat = "";
   for(int x = 0; x < maxDigits; x++)
    sOutputFormat +="0";
   return sOutputFormat;
  }
  private string FormatTrailingNumber(string s, string Format)
  {
   try
   {
    s = s.Substring(0, s.LastIndexOf(" ")) + " " + Convert.ToInt32(s.Substring(s.LastIndexOf(" "))).ToString(Format);
   }
   catch{}
   return s;
  }
  public override void Render(DrawArgs drawArgs)
  {
   if(!IsOn)
    return;
   if(!isInitialized)
    return;
   base.Render(drawArgs);
  }
  public override void Dispose()
  {
   isInitialized = false;
   RemoveAllOverlay();
   if(mesh!=null)
    mesh.Dispose();
   if(meshTextures!=null)
    foreach(Texture t in meshTextures)
     if(t!=null)
      t.Dispose();
   base.Dispose();
  }
  public override void Update(DrawArgs drawArgs)
  {
   try
   {
    if(!this.IsOn)
     return;
    if(!this.isInitialized)
     this.Initialize(drawArgs);
    foreach(RenderableObject ro in this.m_children)
    {
     if(ro.IsOn)
     {
      ro.Update(drawArgs);
     }
    }
   }
   catch(Exception ex)
   {
    System.Windows.Forms.MessageBox.Show(ex.Message);
    this.IsOn=false;
    throw ex;
   }
   base.Update (drawArgs);
  }
  public void RemoveAllOverlay()
  {
   base.ChildObjects.Clear();
  }
  public void resetChildCameraFollows()
  {
   foreach(SatelliteTrack st in this.ChildObjects)
   {
    st.CameraFollows=false;
   }
  }
  public void SetChildrenShowTracks(bool show)
  {
   foreach(SatelliteTrack st in this.ChildObjects)
   {
    st.ShowTrack=show;
   }
  }
  public void SetChildrenModelScale(float multiplier)
  {
   foreach(SatelliteTrack st in this.ChildObjects)
   {
    st.ModelScale=multiplier;
   }
  }
  public override void BuildContextMenu(ContextMenu menu)
  {
   menu.MenuItems.Add("Focus Off", new System.EventHandler(OnFocusOffClick));
   base.BuildContextMenu (menu);
  }
  protected virtual void OnFocusOffClick(object sender, System.EventArgs e)
  {
   ((SatelliteTrackerPlugin)this.Plugin).resetChildCameraFollows();
  }
  public class SatelliteTrackConverter: StringConverter
  {
   string[] m_arraylist;
   public override bool GetStandardValuesSupported(ITypeDescriptorContext context)
   {
    return true;
   }
   public override StandardValuesCollection GetStandardValues(ITypeDescriptorContext context)
   {
    SatelliteTrackerOverlay stol = ((SatelliteTrackerOverlay)context.Instance);
    m_arraylist = new string[stol.ChildObjects.Count];
    m_arraylist[0] = "none";
    for (int i = 1; i < stol.ChildObjects.Count;i++)
     m_arraylist[i] = stol.ChildObjects[i-1].ToString();
    return new StandardValuesCollection(m_arraylist);
   }
  }
 }
 public class SatelliteTrack : RenderableObject
 {
  private string LayerName = "";
  float m_scale = 1;
  bool m_cameraFollows = false;
  bool m_showTrack = false;
  double m_Lat;
  double m_Lon;
  double m_Alt;
  int m_trackorbits = 1;
  private Mesh mesh;
  string meshFilePath;
  private ExtendedMaterial[] materials;
  private Texture[] meshTextures;
  private Material[] meshMaterials;
  private Vector3 meshBoundingBoxMinValue;
  private Vector3 meshBoundingBoxMaxValue;
  private bool localmeshloaded = false;
  bool isFirstRun;
  int periodMin;
  CustomVertex.PositionColored[] track = new CustomVertex.PositionColored[12];
  int m_orbitColor;
  private Color toolbarColor = SystemColors.Control;
  Orbit orbit;
  String mStr_TrackData = "";
  double m_elapsedMinutes;
  Tle TwoLineElement = null;
  System.Timers.Timer timer;
  bool UpdateTrack = true;
  [CategoryAttribute("Satellite")]
  [Description("Altitidue in meters")]
  public double Alt
  {
   get {return m_Alt;}
  }
  public bool CameraFollows
  {
   get { return m_cameraFollows; }
   set
   {
    if(value)
     ((SatelliteTrackerPlugin)((SatelliteTrackerOverlay)this.ParentList).Plugin).resetChildCameraFollows();
    m_cameraFollows = value;
   }
  }
  [CategoryAttribute("Satellite")]
  [Description("Latitidue")]
  public string Lat
  {
   get {return ToDDMMSS(m_Lat);}
  }
  [CategoryAttribute("Satellite")]
  [Description("Longitidue")]
  public string Lon
  {
   get {return ToDDMMSS(m_Lon);}
  }
  [CategoryAttribute("Satellite")]
  [Description("Details of the satellite orbit")]
  public Orbit OrbitData
  {
   get { return orbit; }
  }
  [CategoryAttribute("Satellite")]
  [Description("Controls the scale of the satellite model")]
  public float ModelScale
  {
   get { return m_scale; }
   set { m_scale = value; }
  }
  [CategoryAttribute("Track Display")]
  [Description("Boolean to determine if track is shown")]
  public bool ShowTrack
  {
   get { return m_showTrack; }
   set
   {
    if(value &&!m_showTrack)
     UpdateTrack = true;
    m_showTrack = value; }
  }
  [CategoryAttribute("Track Display")]
  [Description("Color for orbit")]
  public Color TrackColor
  {
   get
   {
    toolbarColor = Color.FromArgb(m_orbitColor);
    return toolbarColor;
   }
   set
   {
    toolbarColor = value;
    m_orbitColor = toolbarColor.ToArgb();
   }
  }
  [CategoryAttribute("Track Display")]
  [Description("Number of seconds between track updates")]
  public double TrackUpdateInterval
  {
   get {return timer.Interval/1000;}
   set {timer.Interval = value *1000;}
  }
  [CategoryAttribute("Track Display")]
  [Description("Number of orbits to display")]
  public int NumberOfOrbitsToTrack
  {
   get {return m_trackorbits;}
   set {m_trackorbits=value;}
  }
  [Editor(typeof(OpacityEditor),
    typeof(System.Drawing.Design.UITypeEditor))]
  [CategoryAttribute("Satellite")]
  [Description("Controls the amount of light allowed to pass through this object. (0=invisible, 255=opaque).")]
  public new int Opacity
  {
   get{return base.Opacity;}
   set{base.Opacity=(byte)value;}
  }
  public SatelliteTrack( string meshFilePath, OrbitTools.Tle tle ) : base(tle.Name)
  {
   this.meshFilePath = meshFilePath;
   this.RenderPriority = RenderPriority.Icons;
   this.TwoLineElement = tle;
   LayerName = this.TwoLineElement.Name;
   m_orbitColor = Color.FromArgb(m_opacity,255,255,22).ToArgb();
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   if (isInitialized)
    return;
   try
   {
    orbit = new Orbit(this.TwoLineElement);
    timer = new System.Timers.Timer(10000);
    UpdateTrackPositions(drawArgs);
    timer.Elapsed+=new System.Timers.ElapsedEventHandler(timer_Elapsed);
    timer.Start();
   }
   catch(Exception caught)
   {
    Utility.Log.Write( caught );
    string msg = "Failed to read TLE from " + this.TwoLineElement.Name;
    throw new Exception( msg);
   }
   this.IsOn = LoadMesh(drawArgs);
   isInitialized = true;
  }
  public override void Dispose()
  {
   isInitialized = false;
   if(localmeshloaded)
   {
    if(mesh!=null)
     mesh.Dispose();
    if(meshTextures!=null)
     foreach(Texture t in meshTextures)
      if(t!=null)
       t.Dispose();
   }
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   return false;
  }
  public override void Update(DrawArgs drawArgs)
  {
   if(!isInitialized)
    Initialize(drawArgs);
  }
  public override void Render(DrawArgs drawArgs)
  {
   try
   {
    if(!isInitialized || mesh==null)
     return;
    m_elapsedMinutes = orbit.TPlusEpoch()/60;
    if(m_showTrack)
    {
     DrawTrack(drawArgs);
    }
    Eci eci = orbit.getPosition(m_elapsedMinutes);
    CoordGeo cog = eci.toGeo();
    if(isFirstRun)
    {
     isFirstRun=false;
    }
    m_Lat = cog.Latitude ;
    m_Lon = cog.Longitude ;
    m_Alt = cog.Altitude ;
    if(m_cameraFollows)
     drawArgs.WorldCamera.SetPosition(Angle.FromRadians(cog.Latitude ).Degrees, Angle.FromRadians(cog.Longitude ).Degrees, double.NaN, double.NaN, double.NaN);
    drawArgs.device.RenderState.CullMode = Cull.None;
    drawArgs.device.RenderState.Lighting = true;
    drawArgs.device.RenderState.Ambient = Color.Black;
    drawArgs.device.RenderState.NormalizeNormals = true;
    drawArgs.device.Lights[0].Diffuse = Color.FromArgb(255, 255, 215);
    drawArgs.device.Lights[0].Type = LightType.Directional;
    drawArgs.device.Lights[0].Direction = new Vector3(1f,1f,1f);
    drawArgs.device.Lights[0].Enabled = true;
    Vector3 sv = MathEngine.SphericalToCartesian(Angle.FromRadians(cog.Latitude), Angle.FromRadians(cog.Longitude ), drawArgs.WorldCamera.WorldRadius + cog.Altitude *1000 );
    Vector3 xyzPosition = sv;
    drawArgs.device.Lights[0].Position = new Vector3(
     (float)sv.X*2,
     (float)sv.Y*2,
     (float)sv.Z);
    drawArgs.device.RenderState.AlphaBlendEnable = true;
    drawArgs.device.TextureState[0].ColorArgument1 = TextureArgument.TextureColor;
    drawArgs.device.TextureState[0].ColorOperation = TextureOperation.SelectArg1;
    drawArgs.device.SamplerState[0].MipFilter = TextureFilter.Anisotropic;
    drawArgs.device.SamplerState[0].MipMapLevelOfDetailBias = -1;
    drawArgs.device.SamplerState[0].AddressU = TextureAddress.Mirror;
    drawArgs.device.SamplerState[0].AddressV = TextureAddress.Mirror;
    Matrix currentWorld = drawArgs.device.Transform.World;
    drawArgs.device.Transform.World = Matrix.Identity;
    drawArgs.device.Transform.World *= Matrix.Scaling(m_scale, m_scale, m_scale);
    double time = TimeKeeper.CurrentTimeUtc.TimeOfDay.TotalSeconds/60;
    double fraction = time-Math.Round(time);
    drawArgs.device.Transform.World *= Matrix.RotationX((float)(fraction*Math.PI*2));
    drawArgs.device.Transform.World *= Matrix.Translation(
     (float)sv.X,
     (float)sv.Y,
     (float)sv.Z);
    drawArgs.device.Transform.World *= Matrix.Translation(
     (float)-drawArgs.WorldCamera.ReferenceCenter.X,
     (float)-drawArgs.WorldCamera.ReferenceCenter.Y,
     (float)-drawArgs.WorldCamera.ReferenceCenter.Z
     );
    drawArgs.device.RenderState.ZBufferEnable = true;
    for( int i = 0; i < meshMaterials.Length; i++ )
    {
     drawArgs.device.Material = meshMaterials[i];
     Texture texture = meshTextures[i];
     drawArgs.device.TextureState[0].ColorArgument1 = TextureArgument.TextureColor;
     drawArgs.device.SetTexture(0, texture);
     mesh.DrawSubset(i);
    }
    drawArgs.device.Transform.World = currentWorld;
    drawArgs.device.RenderState.Lighting = false;
    drawArgs.device.RenderState.CullMode = Cull.Clockwise;
    Vector3 rc = new Vector3(
     (float)drawArgs.WorldCamera.ReferenceCenter.X,
     (float)drawArgs.WorldCamera.ReferenceCenter.Y,
     (float)drawArgs.WorldCamera.ReferenceCenter.Z
     );
    Vector3 projectedPoint = drawArgs.WorldCamera.Project(xyzPosition - rc);
    Ray foundRay;
    int mouseBuffer = 15;
    if(projectedPoint.X >= DrawArgs.LastMousePosition.X - mouseBuffer &&
     projectedPoint.X <= DrawArgs.LastMousePosition.X + mouseBuffer &&
     projectedPoint.Y >= DrawArgs.LastMousePosition.Y - mouseBuffer &&
     projectedPoint.Y <= DrawArgs.LastMousePosition.Y + mouseBuffer)
    {
     m_orbitColor = Color.FromArgb(m_opacity, Color.Purple).ToArgb();
     ShowLabelData(drawArgs, projectedPoint);
     DrawTrack(drawArgs);
    }
    else
    {
     m_orbitColor = Color.FromArgb(m_opacity,255,255,22).ToArgb();
    }
   }
   catch(Exception ex)
   {
    throw ex;
   }
  }
  public override void BuildContextMenu(ContextMenu menu)
  {
   menu.MenuItems.Add("Focus", new System.EventHandler(OnFocusClick));
   base.BuildContextMenu (menu);
  }
  private bool LoadMesh(DrawArgs drawArgs)
  {
   string LayerMeshFile =Path.Combine( meshFilePath,this.Name +".x");
   if(System.IO.File.Exists(LayerMeshFile))
   {
    GraphicsStream adj;
    try
    {
     mesh = Mesh.FromFile(LayerMeshFile, MeshFlags.Managed, drawArgs.device, out adj, out materials );
    }
    catch(Exception caught)
    {
     Utility.Log.Write( caught );
     string msg = "Failed to read mesh from " + meshFilePath;
     System.Windows.Forms.MessageBox.Show( msg, this.name +" failed to load.", MessageBoxButtons.OK, MessageBoxIcon.Error);
     this.ParentList.IsOn = false;
     return false;
    }
    string textureFilePath = "";
    try
    {
     meshTextures = new Texture[materials.Length];
     meshMaterials = new Material[materials.Length];
     string xFilePath = Path.GetDirectoryName(LayerMeshFile);
     for(int i = 0; i < materials.Length; i++)
     {
      meshMaterials[i] = materials[i].Material3D;
      meshMaterials[i].Ambient = meshMaterials[i].Diffuse;
      if(materials[i].TextureFilename!=null)
      {
       textureFilePath = Path.Combine(xFilePath, materials[i].TextureFilename);
       meshTextures[i] = TextureLoader.FromFile(drawArgs.device, textureFilePath);
      }
     }
     isInitialized = true;
     isFirstRun = true;
     GraphicsStream vertexData;
     VertexBufferDescription description =
      mesh.VertexBuffer.Description;
     vertexData = mesh.VertexBuffer.Lock
      (0, 0, LockFlags.ReadOnly);
     Geometry.ComputeBoundingBox(vertexData,
      mesh.NumberVertices,description.VertexFormat,
      out meshBoundingBoxMinValue,
      out meshBoundingBoxMaxValue);
     mesh.VertexBuffer.Unlock();
     localmeshloaded = true;
    }
    catch(Exception caught)
    {
     Utility.Log.Write( caught );
     string msg = "Failed to read Texture from " + textureFilePath;
     System.Windows.Forms.MessageBox.Show( msg, this.name +" failed to load.", MessageBoxButtons.OK, MessageBoxIcon.Error);
     return false;
    }
   }
   else
   {
    this.meshBoundingBoxMinValue = ((SatelliteTrackerOverlay)this.ParentList).meshBoundingBoxMinValue;
    this.meshBoundingBoxMaxValue = ((SatelliteTrackerOverlay)this.ParentList).meshBoundingBoxMaxValue;
    this.meshTextures = ((SatelliteTrackerOverlay)this.ParentList).meshTextures;
    this.meshMaterials = ((SatelliteTrackerOverlay)this.ParentList).meshMaterials;
    this.mesh = ((SatelliteTrackerOverlay)this.ParentList).mesh;
    localmeshloaded = false;
   }
   return true;
  }
  public bool GetMeshIntersect(out Ray foundRay, double WorldRadius,
   Viewport viewPort,
   Matrix pMatrix,
   Matrix vMatrix,
   Matrix wMatrix, int MouseX, int MouseY)
  {
   bool bIntersects = false;
   Vector3 vDirection;
   Vector3 vNear = new Vector3(MouseX, MouseY, 0);
   Vector3 vFar = new Vector3(MouseX, MouseY, 1);
   Vector3 sv = MathEngine.SphericalToCartesian(Angle.FromRadians(m_Lat),
    Angle.FromRadians(m_Lon), WorldRadius + m_Alt*1000 );
   vNear.Unproject(viewPort,
    pMatrix,
    vMatrix,
    Matrix.Translation(sv));
   vFar.Unproject(viewPort,
    pMatrix,
    vMatrix,
    Matrix.Translation(sv));
   foundRay = new Ray();
   vDirection= Vector3.Subtract(vFar,vNear);
   foundRay.origin = vNear;
   foundRay.destination = vFar;
   foundRay.direction =vDirection;
   if (Geometry.BoxBoundProbe(meshBoundingBoxMinValue *m_scale,
    meshBoundingBoxMaxValue*m_scale, vNear, vFar))
   {
    bIntersects = true;
   }
   return bIntersects;
  }
  private void UpdateTrackPositions(DrawArgs drawArgs)
  {
   if(UpdateTrack)
   {
    periodMin = (int) orbit.Period()/60;
    track = new CustomVertex.PositionColored[periodMin*m_trackorbits];
    for(int i=0;i<track.Length;i++)
    {
     try
     {
      Eci e = orbit.getPosition(i+m_elapsedMinutes);
      CoordGeo cg = e.toGeo();
      Vector3 s = MathEngine.SphericalToCartesian(Angle.FromRadians(cg.Latitude ), Angle.FromRadians(cg.Longitude ),
       drawArgs.WorldCamera.WorldRadius + cg.Altitude *1000 );
      track[i].X = s.X;
      track[i].Y = s.Y;
      track[i].Z = s.Z;
      track[i].Color = m_orbitColor;
     }
     catch(Exception ex)
     {
      if(!ex.Message.Equals("Satellite orbit may have decayed"))
       throw ex;
     }
    }
    UpdateTrack = false;
   }
  }
  private void DrawTrack(DrawArgs drawArgs)
  {
   UpdateTrackPositions(drawArgs);
   drawArgs.device.Transform.World = Matrix.Translation(
    (float)-drawArgs.WorldCamera.ReferenceCenter.X,
    (float)-drawArgs.WorldCamera.ReferenceCenter.Y,
    (float)-drawArgs.WorldCamera.ReferenceCenter.Z
    );
   drawArgs.device.RenderState.ZBufferEnable = false;
   drawArgs.device.TextureState[0].ColorOperation = TextureOperation.Disable;
   drawArgs.device.VertexFormat = CustomVertex.PositionColored.Format;
   drawArgs.device.DrawUserPrimitives(PrimitiveType.LineStrip, track.Length-1, track);
   drawArgs.device.RenderState.ZBufferEnable = true;
   drawArgs.device.Transform.World = drawArgs.WorldCamera.WorldMatrix;
  }
  private void ShowLabelData(DrawArgs drawArgs, Vector3 projectedPoint)
  {
   try
   {
    String[] lblString = new String[15];
    System.Globalization.NumberFormatInfo nfi = new CultureInfo( "en-US", false ).NumberFormat;
    nfi.NumberDecimalDigits = 4;
    bool Use2525B = false;
    switch(mStr_TrackData)
    {
     case "Labels":
      lblString[0] = "W";
      lblString[1] = "B/C/D/AC";
      lblString[2] = "E/F";
      lblString[3] = "X/Y";
      lblString[4] = "";
      lblString[5] = "G";
      lblString[6] = "V";
      lblString[7] = "AA";
      lblString[8] = "H";
      lblString[9] = "T";
      lblString[10] = "";
      lblString[11] = "M";
      lblString[12] = "Z";
      lblString[13] = "R/AG";
      lblString[14] = "JJ/KKKKK/L/N/PPPPP";
      Use2525B = true;
      break;
     default:
      lblString[0] = DateTime.Now.ToUniversalTime().ToString("ddHHmmssZMMMyy");
      lblString[1] = "";
      lblString[2] = "";
      lblString[3] = (m_Alt).ToString("0000") + "km/" + ToDDMMSS(Angle.FromRadians(m_Lat).Degrees)+","+ToDDMMSS(Angle.FromRadians(m_Lon).Degrees);
      lblString[4] = "";
      lblString[5] = "";
      lblString[6] = "Elapsed Min:" + m_elapsedMinutes.ToString("000000.00");
      lblString[7] = "";
      lblString[8] = "";
      lblString[9] = orbit.SatName();
      lblString[10] = "";
      lblString[11] = "";
      lblString[12] = "Period:" + periodMin.ToString() + " min";
      lblString[13] = "";
      lblString[14] = "";
      break;
    }
    Rectangle[] lblRects = new Rectangle[15];
    for (int i = 0; i < 15; i++)
    {
     lblRects[i] = drawArgs.defaultDrawingFont.MeasureString(null, lblString[i], DrawTextFormat.None, 0);
    }
    Rectangle rectMax = new Rectangle((int) projectedPoint.X, (int) projectedPoint.Y, 1, 1);
    for (int i = 0; i < 15; i++)
    {
     rectMax.Width = System.Math.Max(rectMax.Width, lblRects[i].Width);
     rectMax.Height = System.Math.Max(rectMax.Height , lblRects[i].Height );
    }
    double XoffsetFactor = 1.5;
    if(!Use2525B)
     XoffsetFactor =1;
    rectMax.X = rectMax.X - (int) (rectMax.Width * XoffsetFactor);
    rectMax.Y = rectMax.Y - (int) (rectMax.Height * 2.5);
    int col, row = 0;
    Rectangle rectDraw = new Rectangle(rectMax.Location, rectMax.Size);
    Color color = Color.FromArgb(125,Color.Gray);
    for (int i = 0; i < 15; i++)
    {
     col = i % 3;
     row = i / 3;
     rectDraw.X = (int)rectMax.X + (rectMax.Width*col);
     rectDraw.Y = (int)rectMax.Y+ (rectMax.Height *row);
     if(lblString[i].Length!=0)
      DrawBox(rectDraw.X,rectDraw.Y, rectMax.Size.Width,rectMax.Size.Height, 0.00f,color.ToArgb(),drawArgs.device);
     drawArgs.defaultDrawingFont.DrawText(null, lblString[i],rectDraw, DrawTextFormat.Right - col,Color.Yellow.ToArgb() );
    }
   }
   catch(Exception ex)
   {
    string s = ex.Message;
   }
  }
  string ToDDMMSS(double deg)
  {
   int DD, MM, SS =0;
   double mm, ss;
   if( deg / 360 >1) deg = deg % 360;
   if(deg > 180) deg = deg - 360;
   DD = (int)deg /1;
   string sign = DD < 0 ? "-" : " ";
   deg *= deg < 0 ? -1 : 1;
   mm = (deg % 1);
   MM = (int)(mm *60)/1;
   ss = (mm *60) % 1;
   SS = (int)((ss)*60)/1;
   return DD.ToString("000 ")+MM.ToString("00 ")+SS.ToString("00");
  }
  public static void DrawBox(int ulx, int uly, int width, int height, float z, int color, Device device)
  {
   CustomVertex.TransformedColored[] verts = new CustomVertex.TransformedColored[4];
   verts[0].X = (float)ulx;
   verts[0].Y = (float)uly;
   verts[0].Z = z;
   verts[0].Color = color;
   verts[1].X = (float)ulx;
   verts[1].Y = (float)uly + height;
   verts[1].Z = z;
   verts[1].Color = color;
   verts[2].X = (float)ulx + width;
   verts[2].Y = (float)uly;
   verts[2].Z = z;
   verts[2].Color = color;
   verts[3].X = (float)ulx + width;
   verts[3].Y = (float)uly + height;
   verts[3].Z = z;
   verts[3].Color = color;
   device.VertexFormat = CustomVertex.TransformedColored.Format;
   device.TextureState[0].ColorOperation = TextureOperation.Disable;
   device.DrawUserPrimitives(PrimitiveType.TriangleStrip, verts.Length - 2, verts);
  }
  private void timer_Elapsed(object sender, System.Timers.ElapsedEventArgs e)
  {
   UpdateTrack = true;;
  }
  public virtual void OnFocusClick(object sender, System.EventArgs e)
  {
   SatelliteTrackerOverlay.ParentApplication.WorldWindow.SetViewPosition(Angle.FromRadians(m_Lat).Degrees ,
    Angle.FromRadians(m_Lon).Degrees , ((m_Alt+100) *1000 ));
   this.CameraFollows=true;
  }
 }
 public class OpacityEditor : UITypeEditor
 {
  public override UITypeEditorEditStyle GetEditStyle(
   ITypeDescriptorContext context)
  {
   return UITypeEditorEditStyle.DropDown;
  }
  public override object EditValue(ITypeDescriptorContext context,
   IServiceProvider provider, object value)
  {
   IWindowsFormsEditorService wfes = provider.GetService(
    typeof(IWindowsFormsEditorService)) as
    IWindowsFormsEditorService;
   if (wfes != null)
   {
    frmOpacity _frmOpacity = new frmOpacity();
    _frmOpacity.trackBar1.Value = (int) value;
    _frmOpacity.BarValue = _frmOpacity.trackBar1.Value;
    _frmOpacity._wfes = wfes;
    wfes.DropDownControl(_frmOpacity);
    value = _frmOpacity.BarValue;
   }
   return value;
  }
 }
 public class frmOpacity : System.Windows.Forms.Form
 {
  public int BarValue;
  public IWindowsFormsEditorService _wfes;
  public System.Windows.Forms.TrackBar trackBar1;
  private System.Windows.Forms.Button btnTrack;
  private System.ComponentModel.Container components = null;
  public frmOpacity()
  {
   InitializeComponent();
   TopLevel = false;
   btnTrack.Text = BarValue.ToString();
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
   this.trackBar1 = new System.Windows.Forms.TrackBar();
   this.btnTrack = new System.Windows.Forms.Button();
   ((System.ComponentModel.ISupportInitialize)(this.trackBar1)).BeginInit();
   this.SuspendLayout();
   this.trackBar1.LargeChange = 15;
   this.trackBar1.Location = new System.Drawing.Point(0, 8);
   this.trackBar1.Maximum = 255;
   this.trackBar1.Name = "trackBar1";
   this.trackBar1.Size = new System.Drawing.Size(152, 45);
   this.trackBar1.TabIndex = 0;
   this.trackBar1.TickFrequency = 10;
   this.trackBar1.TickStyle = System.Windows.Forms.TickStyle.Both;
   this.trackBar1.ValueChanged += new System.EventHandler(this.trackBar1_ValueChanged);
   this.btnTrack.DialogResult = System.Windows.Forms.DialogResult.OK;
   this.btnTrack.Location = new System.Drawing.Point(155, 16);
   this.btnTrack.Name = "btnTrack";
   this.btnTrack.Size = new System.Drawing.Size(35, 23);
   this.btnTrack.TabIndex = 2;
   this.btnTrack.Text = "45";
   this.btnTrack.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
   this.btnTrack.Click += new System.EventHandler(this.btnTrack_Click);
   this.AcceptButton = this.btnTrack;
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.ClientSize = new System.Drawing.Size(192, 70);
   this.ControlBox = false;
   this.Controls.Add(this.btnTrack);
   this.Controls.Add(this.trackBar1);
   this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
   this.MaximizeBox = false;
   this.MinimizeBox = false;
   this.Name = "frmContrast";
   this.ShowInTaskbar = false;
   this.Closed += new System.EventHandler(this.frmOpacity_Closed);
   ((System.ComponentModel.ISupportInitialize)(this.trackBar1)).EndInit();
   this.ResumeLayout(false);
  }
  private void trackBar1_ValueChanged(object sender, System.EventArgs e)
  {
   BarValue = trackBar1.Value;
   btnTrack.Text = BarValue.ToString();
  }
  private void btnTrack_Click(object sender, System.EventArgs e)
  {
   Close();
  }
  private void frmOpacity_Closed(object sender, System.EventArgs e)
  {
   _wfes.CloseDropDown();
  }
 }
}
namespace OrbitTools
{
 abstract public class Globals
 {
  public const double PI = 3.141592653589793;
  public const double TWOPI = 2.0 * Globals.PI;
  public const double RADS_PER_DEG = Globals.PI / 180.0;
  public const double GM = 398601.2;
  public const double GEOSYNC_ALT = 42241.892;
  public const double EARTH_RAD = 6370.0;
  public const double EARTH_DIA = 12800.0;
  public const double DAY_SIDERAL = (23 * 3600) + (56 * 60) + 4.09;
  public const double DAY_24HR = (24 * 3600);
  public const double AE = 1.0;
  public const double AU = 149597870.0;
  public const double SR = 696000.0;
  public const double TWOTHRD = 2.0 / 3.0;
  public const double XKMPER = 6378.135;
  public const double F = 1.0 / 298.26;
  public const double GE = 398600.8;
  public const double J2 = 1.0826158E-3;
  public const double J3 = -2.53881E-6;
  public const double J4 = -1.65597E-6;
  public const double CK2 = J2 / 2.0;
  public const double CK4 = -3.0 * J4 / 8.0;
  public const double XJ3 = J3;
  public const double E6A = 1.0e-06;
  public const double QO = Globals.AE + 120.0 / Globals.XKMPER;
  public const double S = Globals.AE + 78.0 / Globals.XKMPER;
  public const double MIN_PER_DAY = 1440.0;
  public const double SEC_PER_DAY = 86400.0;
  public const double OMEGA_E = 1.00273790934;
  public static double XKE = Math.Sqrt(3600.0 * GE /
   (Globals.XKMPER * Globals.XKMPER * Globals.XKMPER));
  public static double QOMS2T = Math.Pow((QO - Globals.S), 4);
  public static double Sqr(double x)
  {
   return (x * x);
  }
  public static double Fmod2p(double arg)
  {
   double modu = (arg % TWOPI);
   if (modu < 0.0)
    modu += TWOPI;
   return modu;
  }
  public static double AcTan(double sinx, double cosx)
  {
   double ret;
   if (cosx == 0.0)
   {
    if (sinx > 0.0)
     ret = PI / 2.0;
    else
     ret = 3.0 * PI / 2.0;
   }
   else
   {
    if (cosx > 0.0)
     ret = Math.Atan(sinx / cosx);
    else
     ret = PI + Math.Atan(sinx / cosx);
   }
   return ret;
  }
  public static double Rad2Deg(double r)
  {
   const double DEG_PER_RAD = 180.0 / PI;
   return r * DEG_PER_RAD;
  }
  public static double Deg2Rad(double d)
  {
   const double RAD_PER_DEG = PI / 180.0;
   return d * RAD_PER_DEG;
  }
 }
 public class JulianConverter:ExpandableObjectConverter
 {
  public override bool CanConvertTo(ITypeDescriptorContext context,
   System.Type destinationType)
  {
   if (destinationType == typeof(Julian))
    return true;
   return base.CanConvertTo(context, destinationType);
  }
  public override object ConvertTo(ITypeDescriptorContext context,
   CultureInfo culture,
   object value,
   System.Type destinationType)
  {
   if (destinationType == typeof(System.String) &&
    value is Orbit)
   {
    Julian so = (Julian)value;
    return "Date:" + so.Date;
   }
   return base.ConvertTo(context, culture, value, destinationType);
  }
  public override bool CanConvertFrom(ITypeDescriptorContext context,
   System.Type sourceType)
  {
   if (sourceType == typeof(string))
    return true;
   return base.CanConvertFrom(context, sourceType);
  }
 }
 [TypeConverterAttribute(typeof(JulianConverter))]
 public class Julian
 {
  private const double EPOCH_JAN1_00H_1900 = 2415020.5;
  private const double EPOCH_JAN1_12H_1900 = 2415021.0;
  private const double EPOCH_JAN0_12H_1900 = 2415020.0;
  private const double EPOCH_JAN1_12H_2000 = 2451545.0;
  private double m_Date;
  private int m_Year;
  private double m_Day;
  public Julian(DateTime dt)
  {
   double day =
    (dt.DayOfYear) +
    (dt.Hour +
    ((dt.Minute +
    ((dt.Second + (dt.Millisecond / 1000.0)) / 60.0)) / 60.0)) / 24.0;
   Initialize(dt.Year, day);
  }
  public Julian(int year, double day)
  {
   Initialize(year, day);
  }
  public double Date { get { return m_Date; } }
  public double FromJan1_00h_1900() { return m_Date - EPOCH_JAN1_00H_1900; }
  public double FromJan1_12h_1900() { return m_Date - EPOCH_JAN1_12H_1900; }
  public double FromJan0_12h_1900() { return m_Date - EPOCH_JAN0_12H_1900; }
  public double FromJan1_12h_2000() { return m_Date - EPOCH_JAN1_12H_2000; }
  public TimeSpan Diff(Julian date)
  {
   const double TICKS_PER_DAY = 8.64e11;
   return new TimeSpan((long)((m_Date - date.m_Date) * TICKS_PER_DAY));
  }
  protected void Initialize(int year, double day)
  {
   if ((year <= 1582) || (year > 3000) ||
    (day < 0.0) || (day > 366.5))
   {
    throw new Exception("Date out of range");
   }
   m_Year = year;
   m_Day = day;
   year--;
   int A = (year / 100);
   int B = 2 - A + (A / 4);
   double NewYears = (int)(365.25 * year) +
    (int)(30.6001 * 14) +
    1720994.5 + B;
   m_Date = NewYears + day;
  }
  public double toGMST()
  {
   double UT = (m_Date + 0.5) % 1.0;
   double TU = (FromJan1_12h_2000() - UT) / 36525.0;
   double GMST = 24110.54841 + TU *
    (8640184.812866 + TU * (0.093104 - TU * 6.2e-06));
   GMST = (GMST + Globals.SEC_PER_DAY * Globals.OMEGA_E * UT) % Globals.SEC_PER_DAY;
   if (GMST < 0.0)
    GMST += Globals.SEC_PER_DAY;
   return (Globals.TWOPI * (GMST / Globals.SEC_PER_DAY));
  }
  public double toLMST(double lon)
  {
   return (toGMST() + lon) % Globals.TWOPI;
  }
  public DateTime toTime()
  {
   DateTime dt = new DateTime(m_Year, 1, 1);
   dt = dt.AddDays(m_Day - 1.0);
   return dt;
  }
 }
 public class CoordGeo
 {
  private double m_Latitude;
  private double m_Longitude;
  private double m_Altitude;
  public CoordGeo()
  {
   m_Latitude = 0.0;
   m_Longitude = 0.0;
   m_Altitude = 0.0;
  }
  public CoordGeo(double lat, double lon, double alt)
  {
   m_Latitude = lat;
   m_Longitude = lon;
   m_Altitude = alt;
  }
  public double Latitude
  {
   get { return m_Latitude; }
   set { m_Latitude = value; }
  }
  public double Longitude
  {
   get { return m_Longitude; }
   set { m_Longitude = value; }
  }
  public double Altitude
  {
   get { return m_Altitude; }
   set { m_Altitude = value; }
  }
 }
 public class CoordTopo
 {
  private double m_Azimuth;
  private double m_Elevation;
  private double m_Range;
  private double m_RangeRate;
  public CoordTopo()
  {
   m_Azimuth = 0.0;
   m_Elevation = 0.0;
   m_Range = 0.0;
   m_RangeRate = 0.0;
  }
  public CoordTopo(double az, double el, double rng, double rate)
  {
   m_Azimuth = az;
   m_Elevation = el;
   m_Range = rng;
   m_RangeRate = rate;
  }
  public double Azimuth
  {
   get { return m_Azimuth; }
   set { m_Azimuth = value; }
  }
  public double Elevation
  {
   get { return m_Elevation; }
   set { m_Elevation = value; }
  }
  public double Range
  {
   get { return m_Range; }
   set { m_Range = value; }
  }
  public double RangeRate
  {
   get { return m_RangeRate; }
   set { m_RangeRate = value; }
  }
 }
 public class Eci
 {
  private Vector m_Position;
  private Vector m_Velocity;
  private Julian m_Date;
  private VectorUnits m_VectorUnits;
  protected enum VectorUnits
  {
   None,
   Ae,
   Km,
  };
  public Eci()
  {
   m_VectorUnits = VectorUnits.None;
  }
  public Eci(Vector pos, Vector vel, Julian date, bool IsAeUnits)
  {
   m_Position = pos;
   m_Velocity = vel;
   m_Date = date;
   m_VectorUnits = (IsAeUnits ? VectorUnits.Ae : VectorUnits.None);
  }
  public Vector Position { get { return m_Position; } }
  public Vector Velocity { get { return m_Velocity; } }
  public Julian Date { get { return m_Date; } }
  protected VectorUnits Units
  {
   get { return m_VectorUnits; }
   set { m_VectorUnits = value; }
  }
  public void SetUnitsAe() { Units = VectorUnits.Ae; }
  public void SetUnitsKm() { Units = VectorUnits.Km; }
  public bool UnitsAreAe() { return Units == VectorUnits.Ae; }
  public bool UnitsAreKm() { return Units == VectorUnits.Km; }
  public Eci(CoordGeo geo, Julian date)
  {
   m_VectorUnits = VectorUnits.Km;
   double mfactor = Globals.TWOPI * (Globals.OMEGA_E / Globals.SEC_PER_DAY);
   double lat = geo.Latitude;
   double lon = geo.Longitude;
   double alt = geo.Altitude;
   double theta = date.toLMST(lon);
   double c = 1.0 / Math.Sqrt(1.0 + Globals.F * (Globals.F - 2.0) * Globals.Sqr(Math.Sin(lat)));
   double s = Globals.Sqr(1.0 - Globals.F) * c;
   double achcp = (Globals.XKMPER * c + alt) * Math.Cos(lat);
   m_Date = date;
   m_Position = new Vector();
   m_Position.X = achcp * Math.Cos(theta);
   m_Position.Y = achcp * Math.Sin(theta);
   m_Position.Z = (Globals.XKMPER * s + alt) * Math.Sin(lat);
   m_Position.W = Math.Sqrt(Globals.Sqr(m_Position.X) +
    Globals.Sqr(m_Position.Y) +
    Globals.Sqr(m_Position.Z));
   m_Velocity = new Vector();
   m_Velocity.X = -mfactor * m_Position.Y;
   m_Velocity.Y = mfactor * m_Position.X;
   m_Velocity.Z = 0.0;
   m_Velocity.W = Math.Sqrt(Globals.Sqr(m_Velocity.X) +
    Globals.Sqr(m_Velocity.Y));
  }
  public CoordGeo toGeo()
  {
   ae2km();
   double theta = Globals.AcTan(m_Position.Y, m_Position.X);
   double lon = (theta - m_Date.toGMST()) % Globals.TWOPI;
   if (lon < 0.0)
    lon += Globals.TWOPI;
   double r = Math.Sqrt(Globals.Sqr(m_Position.X) + Globals.Sqr(m_Position.Y));
   double e2 = Globals.F * (2.0 - Globals.F);
   double lat = Globals.AcTan(m_Position.Z, r);
   const double DELTA = 1.0e-07;
   double phi;
   double c;
   do
   {
    phi = lat;
    c = 1.0 / Math.Sqrt(1.0 - e2 * Globals.Sqr(Math.Sin(phi)));
    lat = Globals.AcTan(m_Position.Z + Globals.XKMPER * c * e2 * Math.Sin(phi), r);
   }
   while (Math.Abs(lat - phi) > DELTA);
   double alt = r / Math.Cos(lat) - Globals.XKMPER * c;
   return new CoordGeo(lat, lon, alt);
  }
  public void ae2km()
  {
   if (UnitsAreAe())
   {
    MulPos(Globals.XKMPER / Globals.AE);
    MulVel((Globals.XKMPER / Globals.AE) * (Globals.MIN_PER_DAY / 86400));
    m_VectorUnits = VectorUnits.Km;
   }
  }
  protected void MulPos(double factor)
  {
   m_Position.Mul(factor);
  }
  protected void MulVel(double factor)
  {
   m_Velocity.Mul(factor);
  }
 }
 abstract public class NoradBase
 {
  protected double m_satInc;
  protected double m_satEcc;
  protected double m_cosio; protected double m_theta2; protected double m_x3thm1; protected double m_eosq;
  protected double m_betao2; protected double m_betao; protected double m_aodp; protected double m_xnodp;
  protected double m_s4; protected double m_qoms24; protected double m_perigee; protected double m_tsi;
  protected double m_eta; protected double m_etasq; protected double m_eeta; protected double m_coef;
  protected double m_coef1; protected double m_c1; protected double m_c2; protected double m_c3;
  protected double m_c4; protected double m_sinio; protected double m_a3ovk2; protected double m_x1mth2;
  protected double m_xmdot; protected double m_omgdot; protected double m_xhdot1; protected double m_xnodot;
  protected double m_xnodcf; protected double m_t2cof; protected double m_xlcof; protected double m_aycof;
  protected double m_x7thm1;
  public abstract Eci getPosition(double tsince);
  protected Orbit m_Orbit;
  public NoradBase(Orbit orbit)
  {
   m_Orbit = orbit;
   Initialize();
  }
  private void Initialize()
  {
   m_satInc = m_Orbit.Inclination;
   m_satEcc = m_Orbit.Eccentricity;
   m_cosio = Math.Cos(m_satInc);
   m_theta2 = m_cosio * m_cosio;
   m_x3thm1 = 3.0 * m_theta2 - 1.0;
   m_eosq = m_satEcc * m_satEcc;
   m_betao2 = 1.0 - m_eosq;
   m_betao = Math.Sqrt(m_betao2);
   m_aodp = m_Orbit.SemiMinor;
   m_xnodp = m_Orbit.mnMotionRec;
   m_perigee = Globals.XKMPER * (m_aodp * (1.0 - m_satEcc) - Globals.AE);
   m_s4 = Globals.S;
   m_qoms24 = Globals.QOMS2T;
   if (m_perigee < 156.0)
   {
    m_s4 = m_perigee - 78.0;
    if (m_perigee <= 98.0)
    {
     m_s4 = 20.0;
    }
    m_qoms24 = Math.Pow((120.0 - m_s4) * Globals.AE / Globals.XKMPER, 4.0);
    m_s4 = m_s4 / Globals.XKMPER + Globals.AE;
   }
   double pinvsq = 1.0 / (m_aodp * m_aodp * m_betao2 * m_betao2);
   m_tsi = 1.0 / (m_aodp - m_s4);
   m_eta = m_aodp * m_satEcc * m_tsi;
   m_etasq = m_eta * m_eta;
   m_eeta = m_satEcc * m_eta;
   double psisq = Math.Abs(1.0 - m_etasq);
   m_coef = m_qoms24 * Math.Pow(m_tsi,4.0);
   m_coef1 = m_coef / Math.Pow(psisq,3.5);
   double c2 = m_coef1 * m_xnodp *
    (m_aodp * (1.0 + 1.5 * m_etasq + m_eeta * (4.0 + m_etasq)) +
    0.75 * Globals.CK2 * m_tsi / psisq * m_x3thm1 *
    (8.0 + 3.0 * m_etasq * (8.0 + m_etasq)));
   m_c1 = m_Orbit.BStar * c2;
   m_sinio = Math.Sin(m_satInc);
   double a3ovk2 = -Globals.XJ3 / Globals.CK2 * Math.Pow(Globals.AE,3.0);
   m_c3 = m_coef * m_tsi * a3ovk2 * m_xnodp * Globals.AE * m_sinio / m_satEcc;
   m_x1mth2 = 1.0 - m_theta2;
   m_c4 = 2.0 * m_xnodp * m_coef1 * m_aodp * m_betao2 *
    (m_eta * (2.0 + 0.5 * m_etasq) +
    m_satEcc * (0.5 + 2.0 * m_etasq) -
    2.0 * Globals.CK2 * m_tsi / (m_aodp * psisq) *
    (-3.0 * m_x3thm1 * (1.0 - 2.0 * m_eeta + m_etasq * (1.5 - 0.5 * m_eeta)) +
    0.75 * m_x1mth2 *
    (2.0 * m_etasq - m_eeta * (1.0 + m_etasq)) *
    Math.Cos(2.0 * m_Orbit.ArgPerigee)));
   double theta4 = m_theta2 * m_theta2;
   double temp1 = 3.0 * Globals.CK2 * pinvsq * m_xnodp;
   double temp2 = temp1 * Globals.CK2 * pinvsq;
   double temp3 = 1.25 * Globals.CK4 * pinvsq * pinvsq * m_xnodp;
   m_xmdot = m_xnodp + 0.5 * temp1 * m_betao * m_x3thm1 +
    0.0625 * temp2 * m_betao *
    (13.0 - 78.0 * m_theta2 + 137.0 * theta4);
   double x1m5th = 1.0 - 5.0 * m_theta2;
   m_omgdot = -0.5 * temp1 * x1m5th + 0.0625 * temp2 *
    (7.0 - 114.0 * m_theta2 + 395.0 * theta4) +
    temp3 * (3.0 - 36.0 * m_theta2 + 49.0 * theta4);
   double xhdot1 = -temp1 * m_cosio;
   m_xnodot = xhdot1 + (0.5 * temp2 * (4.0 - 19.0 * m_theta2) +
    2.0 * temp3 * (3.0 - 7.0 * m_theta2)) * m_cosio;
   m_xnodcf = 3.5 * m_betao2 * xhdot1 * m_c1;
   m_t2cof = 1.5 * m_c1;
   m_xlcof = 0.125 * a3ovk2 * m_sinio *
    (3.0 + 5.0 * m_cosio) / (1.0 + m_cosio);
   m_aycof = 0.25 * a3ovk2 * m_sinio;
   m_x7thm1 = 7.0 * m_theta2 - 1.0;
  }
  protected Eci FinalPosition(double incl, double omega, double e,
   double a, double xl, double xnode,
   double xn, double tsince)
  {
   if ((e * e) > 1.0)
   {
    throw new Exception("Error in satellite data");
   }
   double beta = Math.Sqrt(1.0 - e * e);
   double axn = e * Math.Cos(omega);
   double temp = 1.0 / (a * beta * beta);
   double xll = temp * m_xlcof * axn;
   double aynl = temp * m_aycof;
   double xlt = xl + xll;
   double ayn = e * Math.Sin(omega) + aynl;
   double capu = Globals.Fmod2p(xlt - xnode);
   double temp2 = capu;
   double temp3 = 0.0;
   double temp4 = 0.0;
   double temp5 = 0.0;
   double temp6 = 0.0;
   double sinepw = 0.0;
   double cosepw = 0.0;
   bool fDone = false;
   for (int i = 1; (i <= 10) && !fDone; i++)
   {
    sinepw = Math.Sin(temp2);
    cosepw = Math.Cos(temp2);
    temp3 = axn * sinepw;
    temp4 = ayn * cosepw;
    temp5 = axn * cosepw;
    temp6 = ayn * sinepw;
    double epw = (capu - temp4 + temp3 - temp2) /
     (1.0 - temp5 - temp6) + temp2;
    if (Math.Abs(epw - temp2) <= Globals.E6A)
     fDone = true;
    else
     temp2 = epw;
   }
   double ecose = temp5 + temp6;
   double esine = temp3 - temp4;
   double elsq = axn * axn + ayn * ayn;
   temp = 1.0 - elsq;
   double pl = a * temp;
   double r = a * (1.0 - ecose);
   double temp1 = 1.0 / r;
   double rdot = Globals.XKE * Math.Sqrt(a) * esine * temp1;
   double rfdot = Globals.XKE * Math.Sqrt(pl) * temp1;
   temp2 = a * temp1;
   double betal = Math.Sqrt(temp);
   temp3 = 1.0 / (1.0 + betal);
   double cosu = temp2 * (cosepw - axn + ayn * esine * temp3);
   double sinu = temp2 * (sinepw - ayn - axn * esine * temp3);
   double u = Globals.AcTan(sinu, cosu);
   double sin2u = 2.0 * sinu * cosu;
   double cos2u = 2.0 * cosu * cosu - 1.0;
   temp = 1.0 / pl;
   temp1 = Globals.CK2 * temp;
   temp2 = temp1 * temp;
   double rk = r * (1.0 - 1.5 * temp2 * betal * m_x3thm1) +
    0.5 * temp1 * m_x1mth2 * cos2u;
   double uk = u - 0.25 * temp2 * m_x7thm1 * sin2u;
   double xnodek = xnode + 1.5 * temp2 * m_cosio * sin2u;
   double xinck = incl + 1.5 * temp2 * m_cosio * m_sinio * cos2u;
   double rdotk = rdot - xn * temp1 * m_x1mth2 * sin2u;
   double rfdotk = rfdot + xn * temp1 * (m_x1mth2 * cos2u + 1.5 * m_x3thm1);
   double sinuk = Math.Sin(uk);
   double cosuk = Math.Cos(uk);
   double sinik = Math.Sin(xinck);
   double cosik = Math.Cos(xinck);
   double sinnok = Math.Sin(xnodek);
   double cosnok = Math.Cos(xnodek);
   double xmx = -sinnok * cosik;
   double xmy = cosnok * cosik;
   double ux = xmx * sinuk + cosnok * cosuk;
   double uy = xmy * sinuk + sinnok * cosuk;
   double uz = sinik * sinuk;
   double vx = xmx * cosuk - cosnok * sinuk;
   double vy = xmy * cosuk - sinnok * sinuk;
   double vz = sinik * cosuk;
   double x = rk * ux;
   double y = rk * uy;
   double z = rk * uz;
   Vector vecPos = new Vector(x, y, z);
   double altKm = (vecPos.Magnitude() * (Globals.XKMPER / Globals.AE));
   if ((altKm < Globals.EARTH_RAD) || (altKm > (2 * Globals.GEOSYNC_ALT)))
   {
    throw new Exception("Satellite orbit may have decayed");
   }
   double xdot = rdotk * ux + rfdotk * vx;
   double ydot = rdotk * uy + rfdotk * vy;
   double zdot = rdotk * uz + rfdotk * vz;
   Vector vecVel = new Vector(xdot, ydot, zdot);
   DateTime gmt = m_Orbit.EpochTime;
   gmt = gmt.AddMinutes(tsince);
   return new Eci(vecPos, vecVel, new Julian(gmt), true);
  }
 }
 public class NoradSDP4 : NoradBase
 {
  const double zns = 1.19459E-5; const double c1ss = 2.9864797E-6;
  const double zes = 0.01675; const double znl = 1.5835218E-4;
  const double c1l = 4.7968065E-7; const double zel = 0.05490;
  const double zcosis = 0.91744867; const double zsinis = 0.39785416;
  const double zsings = -0.98088458; const double zcosgs = 0.1945905;
  const double q22 = 1.7891679E-6; const double q31 = 2.1460748E-6;
  const double q33 = 2.2123015E-7; const double g22 = 5.7686396;
  const double g32 = 0.95240898; const double g44 = 1.8014998;
  const double g52 = 1.0508330; const double g54 = 4.4108898;
  const double root22 = 1.7891679E-6; const double root32 = 3.7393792E-7;
  const double root44 = 7.3636953E-9; const double root52 = 1.1428639E-7;
  const double root54 = 2.1765803E-9; const double thdt = 4.3752691E-3;
  double m_sing;
  double m_cosg;
  double eqsq; double siniq; double cosiq; double rteqsq; double ao;
  double cosq2; double sinomo; double cosomo; double bsq; double xlldot;
  double omgdt; double xnodot;
  double xll; double omgasm; double xnodes; double _em;
  double xinc; double xn; double t;
  double dp_e3; double dp_ee2; double dp_savtsn; double dp_se2;
  double dp_se3; double dp_sgh2; double dp_sgh3; double dp_sgh4;
  double dp_sh2; double dp_sh3; double dp_si2;
  double dp_si3; double dp_sl2; double dp_sl3; double dp_sl4;
  double dp_xgh2; double dp_xgh3; double dp_xgh4; double dp_xh2;
  double dp_xh3; double dp_xi2; double dp_xi3; double dp_xl2;
  double dp_xl3; double dp_xl4; double dp_xqncl; double dp_zmol;
  double dp_zmos;
  double dp_atime; double dp_d2201; double dp_d2211; double dp_d3210;
  double dp_d3222; double dp_d4410; double dp_d4422; double dp_d5220;
  double dp_d5232; double dp_d5421; double dp_d5433; double dp_del1;
  double dp_del2; double dp_del3; double dp_fasx2; double dp_fasx4;
  double dp_fasx6; double dp_omegaq; double dp_sse; double dp_ssg;
  double dp_ssh; double dp_ssi; double dp_ssl; double dp_step2;
  double dp_stepn; double dp_stepp; double dp_thgr; double dp_xfact;
  double dp_xlamo; double dp_xli; double dp_xni;
  bool dp_iresfl;
  bool dp_isynfl;
  double dpi_c; double dpi_ctem; double dpi_day; double dpi_gam;
  double dpi_stem; double dpi_xnodce; double dpi_zcosgl; double dpi_zcoshl;
  double dpi_zcosil; double dpi_zsingl; double dpi_zsinhl; double dpi_zsinil;
  double dpi_zx; double dpi_zy;
  public NoradSDP4(Orbit orbit) :
   base(orbit)
  {
   m_sing = Math.Sin(m_Orbit.ArgPerigee);
   m_cosg = Math.Cos(m_Orbit.ArgPerigee);
   dp_savtsn = 0.0; dp_zmos = 0.0; dp_se2 = 0.0; dp_se3 = 0.0;
   dp_si2 = 0.0; dp_si3 = 0.0; dp_sl2 = 0.0; dp_sl3 = 0.0;
   dp_sl4 = 0.0; dp_sgh2 = 0.0; dp_sgh3 = 0.0;
   dp_sgh4 = 0.0; dp_sh2 = 0.0; dp_sh3 = 0.0; dp_zmol = 0.0;
   dp_ee2 = 0.0; dp_e3 = 0.0; dp_xi2 = 0.0; dp_xi3 = 0.0;
   dp_xl2 = 0.0; dp_xl3 = 0.0; dp_xl4 = 0.0; dp_xgh2 = 0.0;
   dp_xgh3 = 0.0; dp_xgh4 = 0.0; dp_xh2 = 0.0; dp_xh3 = 0.0;
   dp_xqncl = 0.0;
   dp_thgr = 0.0; dp_omegaq = 0.0; dp_sse = 0.0; dp_ssi = 0.0;
   dp_ssl = 0.0; dp_ssh = 0.0; dp_ssg = 0.0; dp_d2201 = 0.0;
   dp_d2211 = 0.0; dp_d3210 = 0.0; dp_d3222 = 0.0; dp_d4410 = 0.0;
   dp_d4422 = 0.0; dp_d5220 = 0.0; dp_d5232 = 0.0; dp_d5421 = 0.0;
   dp_d5433 = 0.0; dp_xlamo = 0.0; dp_del1 = 0.0; dp_del2 = 0.0;
   dp_del3 = 0.0; dp_fasx2 = 0.0; dp_fasx4 = 0.0; dp_fasx6 = 0.0;
   dp_xfact = 0.0; dp_xli = 0.0; dp_xni = 0.0; dp_atime = 0.0;
   dp_stepp = 0.0; dp_stepn = 0.0; dp_step2 = 0.0;
   dp_iresfl = false;
   dp_isynfl = false;
  }
  private bool DeepInit(ref double eosq, ref double sinio, ref double cosio,
   ref double betao, ref double aodp, ref double theta2,
   ref double sing, ref double cosg, ref double betao2,
   ref double xmdot, ref double omgdot, ref double xnodott)
  {
   eqsq = eosq;
   siniq = sinio;
   cosiq = cosio;
   rteqsq = betao;
   ao = aodp;
   cosq2 = theta2;
   sinomo = sing;
   cosomo = cosg;
   bsq = betao2;
   xlldot = xmdot;
   omgdt = omgdot;
   xnodot = xnodott;
   Julian jd = m_Orbit.Epoch;
   dp_thgr = jd.toGMST();
   double eq = m_Orbit.Eccentricity;
   double aqnv = 1.0 / ao;
   dp_xqncl = m_Orbit.Inclination;
   double xmao = m_Orbit.mnAnomaly();
   double xpidot = omgdt + xnodot;
   double sinq = Math.Sin(m_Orbit.RAAN);
   double cosq = Math.Cos(m_Orbit.RAAN);
   dp_omegaq = m_Orbit.ArgPerigee;
   double day = jd.FromJan0_12h_1900();
   if (day != dpi_day)
   {
    dpi_day = day;
    dpi_xnodce = 4.5236020 - 9.2422029E-4 * day;
    dpi_stem = Math.Sin(dpi_xnodce);
    dpi_ctem = Math.Cos(dpi_xnodce);
    dpi_zcosil = 0.91375164 - 0.03568096 * dpi_ctem;
    dpi_zsinil = Math.Sqrt(1.0 - dpi_zcosil * dpi_zcosil);
    dpi_zsinhl = 0.089683511 *dpi_stem / dpi_zsinil;
    dpi_zcoshl = Math.Sqrt(1.0 - dpi_zsinhl * dpi_zsinhl);
    dpi_c = 4.7199672 + 0.22997150 * day;
    dpi_gam = 5.8351514 + 0.0019443680 * day;
    dp_zmol = Globals.Fmod2p(dpi_c - dpi_gam);
    dpi_zx = 0.39785416 * dpi_stem / dpi_zsinil;
    dpi_zy = dpi_zcoshl * dpi_ctem + 0.91744867 * dpi_zsinhl * dpi_stem;
    dpi_zx = Globals.AcTan(dpi_zx,dpi_zy) + dpi_gam - dpi_xnodce;
    dpi_zcosgl = Math.Cos(dpi_zx);
    dpi_zsingl = Math.Sin(dpi_zx);
    dp_zmos = 6.2565837 + 0.017201977 * day;
    dp_zmos = Globals.Fmod2p(dp_zmos);
   }
   dp_savtsn = 1.0e20;
   double zcosg = zcosgs;
   double zsing = zsings;
   double zcosi = zcosis;
   double zsini = zsinis;
   double zcosh = cosq;
   double zsinh = sinq;
   double cc = c1ss;
   double zn = zns;
   double ze = zes;
   double zmo = dp_zmos;
   double xnoi = 1.0 / m_xnodp;
   double a1; double a3; double a7; double a8; double a9; double a10;
   double a2; double a4; double a5; double a6; double x1; double x2;
   double x3; double x4; double x5; double x6; double x7; double x8;
   double z31; double z32; double z33; double z1; double z2; double z3;
   double z11; double z12; double z13; double z21; double z22; double z23;
   double s3; double s2; double s4; double s1; double s5; double s6;
   double s7;
   double se = 0.0; double si = 0.0; double sl = 0.0;
   double sgh = 0.0; double sh = 0.0;
   for (int pass = 1; pass <= 2; pass++)
   {
    a1 = zcosg * zcosh + zsing * zcosi * zsinh;
    a3 = -zsing * zcosh + zcosg * zcosi * zsinh;
    a7 = -zcosg * zsinh + zsing * zcosi * zcosh;
    a8 = zsing * zsini;
    a9 = zsing * zsinh + zcosg * zcosi * zcosh;
    a10 = zcosg * zsini;
    a2 = cosiq * a7 + siniq * a8;
    a4 = cosiq * a9 + siniq * a10;
    a5 = -siniq * a7 + cosiq * a8;
    a6 = -siniq * a9 + cosiq * a10;
    x1 = a1 * cosomo + a2 * sinomo;
    x2 = a3 * cosomo + a4 * sinomo;
    x3 = -a1 * sinomo + a2 * cosomo;
    x4 = -a3 * sinomo + a4 * cosomo;
    x5 = a5 * sinomo;
    x6 = a6 * sinomo;
    x7 = a5 * cosomo;
    x8 = a6 * cosomo;
    z31 = 12.0 * x1 * x1 - 3.0 * x3 * x3;
    z32 = 24.0 * x1 * x2 - 6.0 * x3 * x4;
    z33 = 12.0 * x2 * x2 - 3.0 * x4 * x4;
    z1 = 3.0 * (a1 * a1 + a2 * a2) + z31 * eqsq;
    z2 = 6.0 * (a1 * a3 + a2 * a4) + z32 * eqsq;
    z3 = 3.0 * (a3 * a3 + a4 * a4) + z33 * eqsq;
    z11 = -6.0 * a1 * a5 + eqsq*(-24.0 * x1 * x7 - 6.0 * x3 * x5);
    z12 = -6.0 * (a1 * a6 + a3 * a5) +
     eqsq * (-24.0 * (x2 * x7 + x1 * x8) - 6.0 * (x3 * x6 + x4 * x5));
    z13 = -6.0 * a3 * a6 + eqsq * (-24.0 * x2 * x8 - 6.0 * x4 * x6);
    z21 = 6.0 * a2 * a5 + eqsq * (24.0 * x1 * x5 - 6.0 * x3 * x7);
    z22 = 6.0*(a4 * a5 + a2 * a6) +
     eqsq * (24.0 * (x2 * x5 + x1 * x6) - 6.0 * (x4 * x7 + x3 * x8));
    z23 = 6.0 * a4 * a6 + eqsq*(24.0 * x2 * x6 - 6.0 * x4 * x8);
    z1 = z1 + z1 + bsq * z31;
    z2 = z2 + z2 + bsq * z32;
    z3 = z3 + z3 + bsq * z33;
    s3 = cc * xnoi;
    s2 = -0.5 * s3/rteqsq;
    s4 = s3 * rteqsq;
    s1 = -15.0 * eq * s4;
    s5 = x1 * x3 + x2 * x4;
    s6 = x2 * x3 + x1 * x4;
    s7 = x2 * x4 - x1 * x3;
    se = s1 * zn * s5;
    si = s2 * zn * (z11 + z13);
    sl = -zn * s3 * (z1 + z3 - 14.0 - 6.0 * eqsq);
    sgh = s4 * zn * (z31 + z33 - 6.0);
    sh = -zn * s2 * (z21 + z23);
    if (dp_xqncl < 5.2359877E-2)
     sh = 0.0;
    dp_ee2 = 2.0 * s1 * s6;
    dp_e3 = 2.0 * s1 * s7;
    dp_xi2 = 2.0 * s2 * z12;
    dp_xi3 = 2.0 * s2 * (z13 - z11);
    dp_xl2 = -2.0 * s3 * z2;
    dp_xl3 = -2.0 * s3 * (z3 - z1);
    dp_xl4 = -2.0 * s3 * (-21.0 - 9.0 * eqsq) * ze;
    dp_xgh2 = 2.0 * s4 * z32;
    dp_xgh3 = 2.0 * s4 * (z33 - z31);
    dp_xgh4 = -18.0 * s4 * ze;
    dp_xh2 = -2.0 * s2 * z22;
    dp_xh3 = -2.0 * s2 * (z23 - z21);
    if (pass == 1)
    {
     dp_sse = se;
     dp_ssi = si;
     dp_ssl = sl;
     dp_ssh = sh / siniq;
     dp_ssg = sgh - cosiq * dp_ssh;
     dp_se2 = dp_ee2;
     dp_si2 = dp_xi2;
     dp_sl2 = dp_xl2;
     dp_sgh2 = dp_xgh2;
     dp_sh2 = dp_xh2;
     dp_se3 = dp_e3;
     dp_si3 = dp_xi3;
     dp_sl3 = dp_xl3;
     dp_sgh3 = dp_xgh3;
     dp_sh3 = dp_xh3;
     dp_sl4 = dp_xl4;
     dp_sgh4 = dp_xgh4;
     zcosg = dpi_zcosgl;
     zsing = dpi_zsingl;
     zcosi = dpi_zcosil;
     zsini = dpi_zsinil;
     zcosh = dpi_zcoshl * cosq + dpi_zsinhl * sinq;
     zsinh = sinq * dpi_zcoshl - cosq * dpi_zsinhl;
     zn = znl;
     cc = c1l;
     ze = zel;
     zmo = dp_zmol;
    }
   }
   dp_sse = dp_sse + se;
   dp_ssi = dp_ssi + si;
   dp_ssl = dp_ssl + sl;
   dp_ssg = dp_ssg + sgh - cosiq / siniq * sh;
   dp_ssh = dp_ssh + sh / siniq;
   dp_iresfl = false;
   dp_isynfl = false;
   bool bInitOnExit = true;
   double g310;
   double f220;
   double bfact = 0.0;
   if ((m_xnodp >= 0.0052359877) || (m_xnodp <= 0.0034906585))
   {
    if ((m_xnodp < 8.26E-3) || (m_xnodp > 9.24E-3) || (eq < 0.5))
    {
     bInitOnExit = false;
    }
    else
    {
     dp_iresfl = true;
     double eoc = eq * eqsq;
     double g201 = -0.306 - (eq - 0.64) * 0.440;
     double g211; double g322;
     double g410; double g422;
     double g520;
     if (eq <= 0.65)
     {
      g211 = 3.616 - 13.247 * eq + 16.290 * eqsq;
      g310 = -19.302 + 117.390 * eq - 228.419 * eqsq + 156.591 * eoc;
      g322 = -18.9068 + 109.7927 * eq - 214.6334 * eqsq + 146.5816 * eoc;
      g410 = -41.122 + 242.694 * eq - 471.094 * eqsq + 313.953 * eoc;
      g422 = -146.407 + 841.880 * eq - 1629.014 * eqsq + 1083.435 * eoc;
      g520 = -532.114 + 3017.977 * eq - 5740.0 * eqsq + 3708.276 * eoc;
     }
     else
     {
      g211 = -72.099 + 331.819 * eq - 508.738 * eqsq + 266.724 * eoc;
      g310 = -346.844 + 1582.851 * eq - 2415.925 * eqsq + 1246.113 * eoc;
      g322 = -342.585 + 1554.908 * eq - 2366.899 * eqsq + 1215.972 * eoc;
      g410 = -1052.797 + 4758.686 * eq - 7193.992 * eqsq + 3651.957 * eoc;
      g422 = -3581.69 + 16178.11 * eq - 24462.77 * eqsq + 12422.52 * eoc;
      if (eq <= 0.715)
       g520 = 1464.74 - 4664.75 * eq + 3763.64 * eqsq;
      else
       g520 = -5149.66 + 29936.92 * eq - 54087.36 * eqsq + 31324.56 * eoc;
     }
     double g533;
     double g521;
     double g532;
     if (eq < 0.7)
     {
      g533 = -919.2277 + 4988.61 * eq - 9064.77 * eqsq + 5542.21 * eoc;
      g521 = -822.71072 + 4568.6173 * eq - 8491.4146 * eqsq + 5337.524 * eoc;
      g532 = -853.666 + 4690.25 * eq - 8624.77 * eqsq + 5341.4 * eoc;
     }
     else
     {
      g533 = -37995.78 + 161616.52 * eq - 229838.2 * eqsq + 109377.94 * eoc;
      g521 = -51752.104 + 218913.95 * eq - 309468.16 * eqsq + 146349.42 * eoc;
      g532 = -40023.88 + 170470.89 * eq - 242699.48 * eqsq + 115605.82 * eoc;
     }
     double sini2 = siniq * siniq;
     f220 = 0.75*(1.0 + 2.0 * cosiq + cosq2);
     double f221 = 1.5 * sini2;
     double f321 = 1.875 * siniq*(1.0 - 2.0 * cosiq - 3.0 * cosq2);
     double f322 = -1.875 * siniq*(1.0 + 2.0 * cosiq - 3.0 * cosq2);
     double f441 = 35.0 * sini2 * f220;
     double f442 = 39.3750 * sini2 * sini2;
     double f522 = 9.84375 * siniq*(sini2*(1.0 - 2.0 * cosiq - 5.0 * cosq2) +
      0.33333333*(-2.0 + 4.0 * cosiq + 6.0 * cosq2));
     double f523 = siniq*(4.92187512 * sini2*(-2.0 - 4.0 * cosiq + 10.0 * cosq2) +
      6.56250012 * (1.0 + 2.0 * cosiq - 3.0 * cosq2));
     double f542 = 29.53125 * siniq*(2.0 - 8.0 * cosiq + cosq2 * (-12.0 + 8.0 * cosiq + 10.0 * cosq2));
     double f543 = 29.53125 * siniq*(-2.0 - 8.0 * cosiq + cosq2 * (12.0 + 8.0 * cosiq - 10.0 * cosq2));
     double xno2 = m_xnodp * m_xnodp;
     double ainv2 = aqnv * aqnv;
     double temp1 = 3.0 * xno2 * ainv2;
     double temp = temp1 * root22;
     dp_d2201 = temp * f220 * g201;
     dp_d2211 = temp * f221 * g211;
     temp1 = temp1 * aqnv;
     temp = temp1 * root32;
     dp_d3210 = temp * f321 * g310;
     dp_d3222 = temp * f322 * g322;
     temp1 = temp1 * aqnv;
     temp = 2.0 * temp1 * root44;
     dp_d4410 = temp * f441 * g410;
     dp_d4422 = temp * f442 * g422;
     temp1 = temp1 * aqnv;
     temp = temp1 * root52;
     dp_d5220 = temp * f522 * g520;
     dp_d5232 = temp * f523 * g532;
     temp = 2.0 * temp1 * root54;
     dp_d5421 = temp * f542 * g521;
     dp_d5433 = temp * f543 * g533;
     dp_xlamo = xmao + m_Orbit.RAAN + m_Orbit.RAAN - dp_thgr - dp_thgr;
     bfact = xlldot + xnodot + xnodot - thdt - thdt;
     bfact = bfact + dp_ssl + dp_ssh + dp_ssh;
    }
   }
   else
   {
    dp_iresfl = true;
    dp_isynfl = true;
    double g200 = 1.0 + eqsq * (-2.5 + 0.8125 * eqsq);
    g310 = 1.0 + 2.0 * eqsq;
    double g300 = 1.0 + eqsq * (-6.0 + 6.60937 * eqsq);
    f220 = 0.75 * (1.0 + cosiq) * (1.0 + cosiq);
    double f311 = 0.9375 * siniq * siniq * (1.0 + 3 * cosiq) - 0.75 * (1.0 + cosiq);
    double f330 = 1.0 + cosiq;
    f330 = 1.875 * f330 * f330 * f330;
    dp_del1 = 3.0 * m_xnodp * m_xnodp * aqnv * aqnv;
    dp_del2 = 2.0 * dp_del1 * f220 * g200 * q22;
    dp_del3 = 3.0 * dp_del1 * f330 * g300 * q33 * aqnv;
    dp_del1 = dp_del1 * f311 * g310 * q31 * aqnv;
    dp_fasx2 = 0.13130908;
    dp_fasx4 = 2.8843198;
    dp_fasx6 = 0.37448087;
    dp_xlamo = xmao + m_Orbit.RAAN + m_Orbit.ArgPerigee - dp_thgr;
    bfact = xlldot + xpidot - thdt;
    bfact = bfact + dp_ssl + dp_ssg + dp_ssh;
   }
   if (bInitOnExit)
   {
    dp_xfact = bfact - m_xnodp;
    dp_xli = dp_xlamo;
    dp_xni = m_xnodp;
    dp_atime = 0.0;
    dp_stepp = 720.0;
    dp_stepn = -720.0;
    dp_step2 = 259200.0;
   }
   eosq = eqsq;
   sinio = siniq;
   cosio = cosiq;
   betao = rteqsq;
   aodp = ao;
   theta2 = cosq2;
   sing = sinomo;
   cosg = cosomo;
   betao2 = bsq;
   xmdot = xlldot;
   omgdot = omgdt;
   xnodott = xnodot;
   return true;
  }
  private bool DeepCalcDotTerms(ref double pxndot, ref double pxnddt, ref double pxldot)
  {
   if (dp_isynfl)
   {
    pxndot = dp_del1 * Math.Sin(dp_xli - dp_fasx2) +
     dp_del2 * Math.Sin(2.0 * (dp_xli - dp_fasx4)) +
     dp_del3 * Math.Sin(3.0 * (dp_xli - dp_fasx6));
    pxnddt = dp_del1 * Math.Cos(dp_xli - dp_fasx2) +
     2.0 * dp_del2 * Math.Cos(2.0 * (dp_xli - dp_fasx4)) +
     3.0 * dp_del3 * Math.Cos(3.0 * (dp_xli - dp_fasx6));
   }
   else
   {
    double xomi = dp_omegaq + omgdt * dp_atime;
    double x2omi = xomi + xomi;
    double x2li = dp_xli + dp_xli;
    pxndot = dp_d2201 * Math.Sin(x2omi + dp_xli - g22) +
     dp_d2211 * Math.Sin(dp_xli - g22) +
     dp_d3210 * Math.Sin(xomi + dp_xli - g32) +
     dp_d3222 * Math.Sin(-xomi + dp_xli - g32) +
     dp_d4410 * Math.Sin(x2omi + x2li - g44) +
     dp_d4422 * Math.Sin(x2li - g44) +
     dp_d5220 * Math.Sin(xomi + dp_xli - g52) +
     dp_d5232 * Math.Sin(-xomi + dp_xli - g52) +
     dp_d5421 * Math.Sin(xomi + x2li - g54) +
     dp_d5433 * Math.Sin(-xomi + x2li - g54);
    pxnddt = dp_d2201 * Math.Cos(x2omi + dp_xli - g22) +
     dp_d2211 * Math.Cos(dp_xli - g22) +
     dp_d3210 * Math.Cos(xomi + dp_xli - g32) +
     dp_d3222 * Math.Cos(-xomi + dp_xli - g32) +
     dp_d5220 * Math.Cos(xomi + dp_xli - g52) +
     dp_d5232 * Math.Cos(-xomi + dp_xli - g52) +
     2.0 * (dp_d4410 * Math.Cos(x2omi + x2li - g44) +
     dp_d4422 * Math.Cos(x2li - g44) +
     dp_d5421 * Math.Cos(xomi + x2li - g54) +
     dp_d5433 * Math.Cos(-xomi + x2li - g54));
   }
   pxldot = dp_xni + dp_xfact;
   pxnddt = pxnddt * pxldot;
   return true;
  }
  private void DeepCalcIntegrator(ref double pxndot, ref double pxnddt,
   ref double pxldot, double delt)
  {
   DeepCalcDotTerms(ref pxndot, ref pxnddt, ref pxldot);
   dp_xli = dp_xli + (pxldot) * delt + (pxndot) * dp_step2;
   dp_xni = dp_xni + (pxndot) * delt + (pxnddt) * dp_step2;
   dp_atime = dp_atime + delt;
  }
  private bool DeepSecular(ref double xmdf, ref double omgadf, ref double xnode,
   ref double emm, ref double xincc, ref double xnn,
   ref double tsince)
  {
   xll = xmdf;
   omgasm = omgadf;
   xnodes = xnode;
   xn = xnn;
   t = tsince;
   xll = xll + dp_ssl * t;
   omgasm = omgasm + dp_ssg * t;
   xnodes = xnodes + dp_ssh * t;
   _em = m_Orbit.Eccentricity + dp_sse * t;
   xinc = m_Orbit.Inclination + dp_ssi * t;
   if (xinc < 0.0)
   {
    xinc = -xinc;
    xnodes = xnodes + Globals.PI;
    omgasm = omgasm - Globals.PI;
   }
   double xnddt = 0.0;
   double xndot = 0.0;
   double xldot = 0.0;
   double ft = 0.0;
   double delt = 0.0;
   bool fDone = false;
   if (dp_iresfl)
   {
    while (!fDone)
    {
     if ((dp_atime == 0.0) ||
      ((t >= 0.0) && (dp_atime < 0.0)) ||
      ((t < 0.0) && (dp_atime >= 0.0)))
     {
      if (t < 0)
       delt = dp_stepn;
      else
       delt = dp_stepp;
      dp_atime = 0.0;
      dp_xni = m_xnodp;
      dp_xli = dp_xlamo;
      fDone = true;
     }
     else
     {
      if (Math.Abs(t) < Math.Abs(dp_atime))
      {
       delt = dp_stepp;
       if (t >= 0.0)
        delt = dp_stepn;
       DeepCalcIntegrator(ref xndot, ref xnddt, ref xldot, delt);
      }
      else
      {
       delt = dp_stepn;
       if (t > 0.0)
        delt = dp_stepp;
       fDone = true;
      }
     }
    }
    while (Math.Abs(t - dp_atime) >= dp_stepp)
    {
     DeepCalcIntegrator(ref xndot, ref xnddt, ref xldot, delt);
    }
    ft = t - dp_atime;
    DeepCalcDotTerms(ref xndot, ref xnddt, ref xldot);
    xn = dp_xni + xndot * ft + xnddt * ft * ft * 0.5;
    double xl = dp_xli + xldot * ft + xndot * ft * ft * 0.5;
    double temp = -xnodes + dp_thgr + t * thdt;
    xll = xl - omgasm + temp;
    if (!dp_isynfl)
     xll = xl + temp + temp;
   }
   xmdf = xll;
   omgadf = omgasm;
   xnode = xnodes;
   emm = _em;
   xincc = xinc;
   xnn = xn;
   tsince = t;
   return true;
  }
  private bool DeepPeriodics(ref double e, ref double xincc,
   ref double omgadf, ref double xnode,
   ref double xmam)
  {
   _em = e;
   xinc = xincc;
   omgasm = omgadf;
   xnodes = xnode;
   xll = xmam;
   double sinis = Math.Sin(xinc);
   double cosis = Math.Cos(xinc);
   double sghs = 0.0;
   double shs = 0.0;
   double sh1 = 0.0;
   double pe = 0.0;
   double pinc = 0.0;
   double pl = 0.0;
   double sghl = 0.0;
   if (Math.Abs(dp_savtsn - t) >= 30.0)
   {
    dp_savtsn = t;
    double zm = dp_zmos + zns * t;
    double zf = zm + 2.0 * zes * Math.Sin(zm);
    double sinzf = Math.Sin(zf);
    double f2 = 0.5 * sinzf * sinzf - 0.25;
    double f3 = -0.5 * sinzf * Math.Cos(zf);
    double ses = dp_se2 * f2 + dp_se3 * f3;
    double sis = dp_si2 * f2 + dp_si3 * f3;
    double sls = dp_sl2 * f2 + dp_sl3 * f3 + dp_sl4 * sinzf;
    sghs = dp_sgh2 * f2 + dp_sgh3 * f3 + dp_sgh4 * sinzf;
    shs = dp_sh2 * f2 + dp_sh3 * f3;
    zm = dp_zmol + znl * t;
    zf = zm + 2.0 * zel * Math.Sin(zm);
    sinzf = Math.Sin(zf);
    f2 = 0.5 * sinzf * sinzf - 0.25;
    f3 = -0.5 * sinzf * Math.Cos(zf);
    double sel = dp_ee2 * f2 + dp_e3 * f3;
    double sil = dp_xi2 * f2 + dp_xi3 * f3;
    double sll = dp_xl2 * f2 + dp_xl3 * f3 + dp_xl4 * sinzf;
    sghl = dp_xgh2 * f2 + dp_xgh3 * f3 + dp_xgh4 * sinzf;
    sh1 = dp_xh2 * f2 + dp_xh3 * f3;
    pe = ses + sel;
    pinc = sis + sil;
    pl = sls + sll;
   }
   double pgh = sghs + sghl;
   double ph = shs + sh1;
   xinc = xinc + pinc;
   _em = _em + pe;
   if (dp_xqncl >= 0.2)
   {
    ph = ph / siniq;
    pgh = pgh - cosiq * ph;
    omgasm = omgasm + pgh;
    xnodes = xnodes + ph;
    xll = xll + pl;
   }
   else
   {
    double sinok = Math.Sin(xnodes);
    double cosok = Math.Cos(xnodes);
    double alfdp = sinis * sinok;
    double betdp = sinis * cosok;
    double dalf = ph * cosok + pinc * cosis * sinok;
    double dbet = -ph * sinok + pinc * cosis * cosok;
    alfdp = alfdp + dalf;
    betdp = betdp + dbet;
    double xls = xll + omgasm + cosis * xnodes;
    double dls = pl + pgh - pinc * xnodes * sinis;
    xls = xls + dls;
    xnodes = Globals.AcTan(alfdp, betdp);
    xll = xll + pl;
    omgasm = xls - xll - Math.Cos(xinc) * xnodes;
   }
   e = _em;
   xincc = xinc;
   omgadf = omgasm;
   xnode = xnodes;
   xmam = xll;
   return true;
  }
  public override Eci getPosition(double tsince)
  {
   DeepInit(ref m_eosq, ref m_sinio, ref m_cosio, ref m_betao, ref m_aodp, ref m_theta2,
    ref m_sing, ref m_cosg, ref m_betao2, ref m_xmdot, ref m_omgdot, ref m_xnodot);
   double xmdf = m_Orbit.mnAnomaly() + m_xmdot * tsince;
   double omgadf = m_Orbit.ArgPerigee + m_omgdot * tsince;
   double xnoddf = m_Orbit.RAAN + m_xnodot * tsince;
   double tsq = tsince * tsince;
   double xnode = xnoddf + m_xnodcf * tsq;
   double tempa = 1.0 - m_c1 * tsince;
   double tempe = m_Orbit.BStar * m_c4 * tsince;
   double templ = m_t2cof * tsq;
   double xn = m_xnodp;
   double em = 0.0;
   double xinc = 0.0;
   DeepSecular(ref xmdf, ref omgadf, ref xnode, ref em, ref xinc, ref xn, ref tsince);
   double a = Math.Pow(Globals.XKE / xn, Globals.TWOTHRD) * Globals.Sqr(tempa);
   double e = em - tempe;
   double xmam = xmdf + m_xnodp * templ;
   DeepPeriodics(ref e, ref xinc, ref omgadf, ref xnode, ref xmam);
   double xl = xmam + omgadf + xnode;
   xn = Globals.XKE / Math.Pow(a, 1.5);
   return FinalPosition(xinc, omgadf, e, a, xl, xnode, xn, tsince);
  }
 }
 public class NoradSGP4 : NoradBase
 {
  private double m_c5;
  private double m_omgcof;
  private double m_xmcof;
  private double m_delmo;
  private double m_sinmo;
  public NoradSGP4(Orbit orbit) :
   base(orbit)
  {
   m_c5 = 2.0 * m_coef1 * m_aodp * m_betao2 *
    (1.0 + 2.75 * (m_etasq + m_eeta) + m_eeta * m_etasq);
   m_omgcof = m_Orbit.BStar * m_c3 * Math.Cos(m_Orbit.ArgPerigee);
   m_xmcof = -Globals.TWOTHRD * m_coef * m_Orbit.BStar * Globals.AE / m_eeta;
   m_delmo = Math.Pow(1.0 + m_eta * Math.Cos(m_Orbit.mnAnomaly()), 3.0);
   m_sinmo = Math.Sin(m_Orbit.mnAnomaly());
  }
  public override Eci getPosition(double tsince)
  {
   bool isimp = false;
   if ((m_aodp * (1.0 - m_satEcc) / Globals.AE) < (220.0 / Globals.XKMPER + Globals.AE))
   {
    isimp = true;
   }
   double d2 = 0.0;
   double d3 = 0.0;
   double d4 = 0.0;
   double t3cof = 0.0;
   double t4cof = 0.0;
   double t5cof = 0.0;
   if (!isimp)
   {
    double c1sq = m_c1 * m_c1;
    d2 = 4.0 * m_aodp * m_tsi * c1sq;
    double temp = d2 * m_tsi * m_c1 / 3.0;
    d3 = (17.0 * m_aodp + m_s4) * temp;
    d4 = 0.5 * temp * m_aodp * m_tsi *
     (221.0 * m_aodp + 31.0 * m_s4) * m_c1;
    t3cof = d2 + 2.0 * c1sq;
    t4cof = 0.25 * (3.0 * d3 + m_c1 * (12.0 * d2 + 10.0 * c1sq));
    t5cof = 0.2 * (3.0 * d4 + 12.0 * m_c1 * d3 + 6.0 *
     d2 * d2 + 15.0 * c1sq * (2.0 * d2 + c1sq));
   }
   double xmdf = m_Orbit.mnAnomaly() + m_xmdot * tsince;
   double omgadf = m_Orbit.ArgPerigee + m_omgdot * tsince;
   double xnoddf = m_Orbit.RAAN + m_xnodot * tsince;
   double omega = omgadf;
   double xmp = xmdf;
   double tsq = tsince * tsince;
   double xnode = xnoddf + m_xnodcf * tsq;
   double tempa = 1.0 - m_c1 * tsince;
   double tempe = m_Orbit.BStar * m_c4 * tsince;
   double templ = m_t2cof * tsq;
   if (!isimp)
   {
    double delomg = m_omgcof * tsince;
    double delm = m_xmcof * (Math.Pow(1.0 + m_eta * Math.Cos(xmdf), 3.0) - m_delmo);
    double temp = delomg + delm;
    xmp = xmdf + temp;
    omega = omgadf - temp;
    double tcube = tsq * tsince;
    double tfour = tsince * tcube;
    tempa = tempa - d2 * tsq - d3 * tcube - d4 * tfour;
    tempe = tempe + m_Orbit.BStar * m_c5 * (Math.Sin(xmp) - m_sinmo);
    templ = templ + t3cof * tcube + tfour * (t4cof + tsince * t5cof);
   }
   double a = m_aodp * Globals.Sqr(tempa);
   double e = m_satEcc - tempe;
   double xl = xmp + omega + xnode + m_xnodp * templ;
   double xn = Globals.XKE / Math.Pow(a, 1.5);
   return FinalPosition(m_satInc, omgadf, e, a, xl, xnode, xn, tsince);
  }
 }
 public class OrbitConverter:ExpandableObjectConverter
 {
  public override bool CanConvertTo(ITypeDescriptorContext context,
   System.Type destinationType)
  {
   if (destinationType == typeof(Orbit))
    return true;
   return base.CanConvertTo(context, destinationType);
  }
  public override object ConvertTo(ITypeDescriptorContext context,
   CultureInfo culture,
   object value,
   System.Type destinationType)
  {
   if (destinationType == typeof(System.String) &&
    value is Orbit)
   {
    Orbit so = (Orbit)value;
    return "Apogee:" + so.Apogee +
     ", ArgPerigee: " + so.ArgPerigee +
     ", BStar:" +so.BStar +
     ", Drag:" +so.Drag +
     ", Eccentricity:" +so.Eccentricity +
     ", Epoch:" +so.Epoch +
     ", EpochTime:" +so.EpochTime +
     ", Inclination:" +so.Inclination +
     ", Major:" +so.Major +
     ", Minor:" +so.Minor +
     ", mnMotion:" +so.mnMotion +
     ", mnMotionRec:" +so.mnMotionRec +
     ", Perigee:" +so.Perigee +
     ", RAAN:" +so.RAAN +
     ", SemiMajor:" +so.SemiMajor +
     ", SemiMinor:" +so.SemiMinor ;
   }
   return base.ConvertTo(context, culture, value, destinationType);
  }
  public override bool CanConvertFrom(ITypeDescriptorContext context,
   System.Type sourceType)
  {
   if (sourceType == typeof(string))
    return true;
   return base.CanConvertFrom(context, sourceType);
  }
 }
 [TypeConverterAttribute(typeof(OrbitConverter))]
 public class Orbit
 {
  private Tle m_tle;
  private Julian m_jdEpoch;
  private NoradBase m_NoradModel;
  private double m_secPeriod;
  private double m_aeAxisSemiMinorRec;
  private double m_aeAxisSemiMajorRec;
  private double m_mnMotionRec;
  private double m_kmPerigeeRec;
  private double m_kmApogeeRec;
  public Julian Epoch { get { return m_jdEpoch; }}
  public DateTime EpochTime { get { return m_jdEpoch.toTime(); }}
  public double SemiMajor { get { return m_aeAxisSemiMajorRec; }}
  public double SemiMinor { get { return m_aeAxisSemiMinorRec; }}
  public double mnMotionRec { get { return m_mnMotionRec; }}
  public double Major { get { return 2.0 * SemiMajor; }}
  public double Minor { get { return 2.0 * SemiMinor; }}
  public double Perigee { get { return m_kmPerigeeRec; }}
  public double Apogee { get { return m_kmApogeeRec; }}
  public double Inclination { get { return radGet(Tle.Field.Inclination); }}
  public double Eccentricity { get { return m_tle.getField(Tle.Field.Eccentricity); }}
  public double RAAN { get { return radGet(Tle.Field.Raan); }}
  public double ArgPerigee { get { return radGet(Tle.Field.ArgPerigee); }}
  public double BStar { get { return m_tle.getField(Tle.Field.BStarDrag) / Globals.AE;}}
  public double Drag { get { return m_tle.getField(Tle.Field.MeanMotionDt); }}
  public double mnMotion { get { return m_tle.getField(Tle.Field.MeanMotion); }}
  public Orbit(Tle tle)
  {
   m_NoradModel = null;
   m_tle = tle;
   m_jdEpoch = m_tle.EpochJulian;
   m_secPeriod = -1.0;
   double mm = mnMotion;
   double rpmin = mm * 2 * Globals.PI / Globals.MIN_PER_DAY;
   double a1 = Math.Pow(Globals.XKE / rpmin, Globals.TWOTHRD);
   double e = Eccentricity;
   double i = Inclination;
   double temp = (1.5 * Globals.CK2 * (3.0 * Globals.Sqr(Math.Cos(i)) - 1.0) /
    Math.Pow(1.0 - e * e, 1.5));
   double delta1 = temp / (a1 * a1);
   double a0 = a1 *
    (1.0 - delta1 *
    ((1.0 / 3.0) + delta1 *
    (1.0 + 134.0 / 81.0 * delta1)));
   double delta0 = temp / (a0 * a0);
   m_mnMotionRec = rpmin / (1.0 + delta0);
   m_aeAxisSemiMinorRec = a0 / (1.0 - delta0);
   m_aeAxisSemiMajorRec = m_aeAxisSemiMinorRec / Math.Sqrt(1.0 - (e * e));
   m_kmPerigeeRec = Globals.XKMPER * (m_aeAxisSemiMajorRec * (1.0 - e) - Globals.AE);
   m_kmApogeeRec = Globals.XKMPER * (m_aeAxisSemiMajorRec * (1.0 + e) - Globals.AE);
   if (2.0 * Globals.PI / m_mnMotionRec >= 225.0)
   {
    m_NoradModel = new NoradSDP4(this);
   }
   else
   {
    m_NoradModel = new NoradSGP4(this);
   }
  }
  public double mnAnomaly()
  {
   return radGet(Tle.Field.MeanAnomaly);
  }
  public double mnAnomaly(DateTime gmt)
  {
   double span = TPlusEpoch(gmt);
   double P = Period();
   return (mnAnomaly() + (Globals.TWOPI * (span / P))) % Globals.TWOPI;
  }
  public double TPlusEpoch(DateTime gmt)
  {
   TimeSpan ts = gmt - EpochTime;
   return ts.TotalSeconds;
  }
  public double TPlusEpoch()
  {
   return TPlusEpoch(DateTime.Now.ToUniversalTime());
  }
  public double Period()
  {
   if (m_secPeriod < 0.0)
   {
    if (m_mnMotionRec == 0)
     m_secPeriod = 0.0;
    else
     m_secPeriod = (2 * Globals.PI) / m_mnMotionRec * 60.0;
   }
   return m_secPeriod;
  }
  public Eci getPosition(double tsince)
  {
   Eci eci = m_NoradModel.getPosition(tsince);
   eci.ae2km();
   return eci;
  }
  public string SatName()
  {
   return SatName(false);
  }
  public string SatName(bool fAppendId)
  {
   string str = m_tle.Name;
   if (fAppendId)
   {
    str = str + " #" + m_tle.NoradNumber;
   }
   return str;
  }
  protected double radGet(Tle.Field fld)
  {
   return m_tle.getField(fld, Tle.Unit.Radians);
  }
  protected double degGet(Tle.Field fld)
  {
   return m_tle.getField(fld, Tle.Unit.Degrees);
  }
 }
 public class Site
 {
  private CoordGeo m_geo;
  public Site(CoordGeo geo)
  {
   m_geo = geo;
  }
  public Site(double degLat, double degLon, double kmAlt)
  {
   m_geo =
    new CoordGeo(Globals.Deg2Rad(degLat), Globals.Deg2Rad(degLon), kmAlt);
  }
  public CoordGeo Geo
  {
   get { return m_geo; }
   set { m_geo = value; }
  }
  public double Latitude { get { return m_geo.Latitude; } }
  public double Longitude { get { return m_geo.Longitude; } }
  public double Altitude { get { return m_geo.Altitude; } }
  public Eci getPosition(Julian date)
  {
   return new Eci(m_geo, date);
  }
  public CoordTopo getLookAngle(Eci eci)
  {
   Julian date = eci.Date;
   Eci eciSite = new Eci(m_geo, date);
   if (!eci.UnitsAreKm())
    throw new Exception("ECI units must be kilometer-based");
   Vector vecRgRate = new Vector(eci.Velocity.X - eciSite.Velocity.X,
    eci.Velocity.Y - eciSite.Velocity.Y,
    eci.Velocity.Z - eciSite.Velocity.Z);
   double x = eci.Position.X - eciSite.Position.X;
   double y = eci.Position.Y - eciSite.Position.Y;
   double z = eci.Position.Z - eciSite.Position.Z;
   double w = Math.Sqrt(Globals.Sqr(x) + Globals.Sqr(y) + Globals.Sqr(z));
   Vector vecRange = new Vector(x, y, z, w);
   double theta = date.toLMST(Longitude);
   double sin_lat = Math.Sin(Latitude);
   double cos_lat = Math.Cos(Latitude);
   double sin_theta = Math.Sin(theta);
   double cos_theta = Math.Cos(theta);
   double top_s = sin_lat * cos_theta * vecRange.X +
    sin_lat * sin_theta * vecRange.Y -
    cos_lat * vecRange.Z;
   double top_e = -sin_theta * vecRange.X +
    cos_theta * vecRange.Y;
   double top_z = cos_lat * cos_theta * vecRange.X +
    cos_lat * sin_theta * vecRange.Y +
    sin_lat * vecRange.Z;
   double az = Math.Atan(-top_e / top_s);
   if (top_s > 0.0)
    az += Globals.PI;
   if (az < 0.0)
    az += 2.0 * Globals.PI;
   double el = Math.Asin(top_z / vecRange.W);
   double rate = (vecRange.X * vecRgRate.X +
    vecRange.Y * vecRgRate.Y +
    vecRange.Z * vecRgRate.Z) / vecRange.W;
   CoordTopo topo = new CoordTopo(az,
    el,
    vecRange.W,
    rate);
   return topo;
  }
  public string toString()
  {
   bool LatNorth = true;
   bool LonEast = true;
   if (m_geo.Latitude < 0.0)
   {
    LatNorth = false;
   }
   if (m_geo.Longitude < 0.0)
   {
    LonEast = false;
   }
   string str = Math.Abs(Globals.Rad2Deg(m_geo.Latitude)).ToString("{0,6:f3} ");
   str += (LatNorth ? 'N' : 'S');
   str += Math.Abs(Globals.Rad2Deg(m_geo.Longitude)).ToString("{0,6:f3}");
   str += (LonEast ? 'E' : 'W');
   str += (m_geo.Altitude * 1000.0).ToString();
   str += "m";
   return str;
  }
 }
 public class Tle : IComparable
 {
  public enum Line
  {
   Zero = 0,
   One,
   Two
  };
  public enum Field
  {
   NoradNum,
   IntlDesc,
   SetNumber,
   EpochYear,
   EpochDay,
   OrbitAtEpoch,
   Inclination,
   Raan,
   Eccentricity,
   ArgPerigee,
   MeanAnomaly,
   MeanMotion,
   MeanMotionDt,
   MeanMotionDt2,
   BStarDrag
  }
  public enum Unit
  {
   Radians,
   Degrees,
   Native
  }
  private string m_strName;
  private string m_strLine1;
  private string m_strLine2;
  private Hashtable m_Field;
  private Hashtable m_Cache;
  private int Key(Unit u, Field f)
  {
   return ((int)u * 100) + (int)f;
  }
  private const int TLE_LEN_LINE_DATA = 69; private const int TLE_LEN_LINE_NAME = 22;
  private const int TLE1_COL_SATNUM = 2; private const int TLE1_LEN_SATNUM = 5;
  private const int TLE1_COL_INTLDESC_A = 9; private const int TLE1_LEN_INTLDESC_A = 2;
  private const int TLE1_COL_INTLDESC_B = 11; private const int TLE1_LEN_INTLDESC_B = 3;
  private const int TLE1_COL_INTLDESC_C = 14; private const int TLE1_LEN_INTLDESC_C = 3;
  private const int TLE1_COL_EPOCH_A = 18; private const int TLE1_LEN_EPOCH_A = 2;
  private const int TLE1_COL_EPOCH_B = 20; private const int TLE1_LEN_EPOCH_B = 12;
  private const int TLE1_COL_MEANMOTIONDT = 33; private const int TLE1_LEN_MEANMOTIONDT = 10;
  private const int TLE1_COL_MEANMOTIONDT2 = 44; private const int TLE1_LEN_MEANMOTIONDT2 = 8;
  private const int TLE1_COL_BSTAR = 53; private const int TLE1_LEN_BSTAR = 8;
  private const int TLE1_COL_EPHEMTYPE = 62; private const int TLE1_LEN_EPHEMTYPE = 1;
  private const int TLE1_COL_ELNUM = 64; private const int TLE1_LEN_ELNUM = 4;
  private const int TLE2_COL_SATNUM = 2; private const int TLE2_LEN_SATNUM = 5;
  private const int TLE2_COL_INCLINATION = 8; private const int TLE2_LEN_INCLINATION = 8;
  private const int TLE2_COL_RAASCENDNODE = 17; private const int TLE2_LEN_RAASCENDNODE = 8;
  private const int TLE2_COL_ECCENTRICITY = 26; private const int TLE2_LEN_ECCENTRICITY = 7;
  private const int TLE2_COL_ARGPERIGEE = 34; private const int TLE2_LEN_ARGPERIGEE = 8;
  private const int TLE2_COL_MEANANOMALY = 43; private const int TLE2_LEN_MEANANOMALY = 8;
  private const int TLE2_COL_MEANMOTION = 52; private const int TLE2_LEN_MEANMOTION = 11;
  private const int TLE2_COL_REVATEPOCH = 63; private const int TLE2_LEN_REVATEPOCH = 5;
  public Tle(string strName, string strLine1, string strLine2)
  {
   m_strName = strName;
   m_strLine1 = strLine1;
   m_strLine2 = strLine2;
   Initialize();
  }
  public Tle(Tle tle) :
   this(tle.Name, tle.Line1, tle.Line2)
  {
  }
  private void Initialize()
  {
   m_Field = new Hashtable();
   m_Cache = new Hashtable();
   m_Field[Field.NoradNum] = m_strLine1.Substring(TLE1_COL_SATNUM, TLE1_LEN_SATNUM);
   m_Field[Field.IntlDesc] = m_strLine1.Substring(TLE1_COL_INTLDESC_A,
    TLE1_LEN_INTLDESC_A +
    TLE1_LEN_INTLDESC_B +
    TLE1_LEN_INTLDESC_C);
   m_Field[Field.EpochYear] =
    m_strLine1.Substring(TLE1_COL_EPOCH_A, TLE1_LEN_EPOCH_A);
   m_Field[Field.EpochDay] =
    m_strLine1.Substring(TLE1_COL_EPOCH_B, TLE1_LEN_EPOCH_B);
   if (m_strLine1[TLE1_COL_MEANMOTIONDT] == '-')
   {
    m_Field[Field.MeanMotionDt] = "-0";
   }
   else
    m_Field[Field.MeanMotionDt] = "0";
   m_Field[Field.MeanMotionDt] += m_strLine1.Substring(TLE1_COL_MEANMOTIONDT + 1,
    TLE1_LEN_MEANMOTIONDT);
   m_Field[Field.MeanMotionDt2] =
    ExpToDecimal(m_strLine1.Substring(TLE1_COL_MEANMOTIONDT2,
    TLE1_LEN_MEANMOTIONDT2));
   m_Field[Field.BStarDrag] =
    ExpToDecimal(m_strLine1.Substring(TLE1_COL_BSTAR, TLE1_LEN_BSTAR));
   m_Field[Field.SetNumber] =
    m_strLine1.Substring(TLE1_COL_ELNUM, TLE1_LEN_ELNUM).TrimStart();
   m_Field[Field.Inclination] =
    m_strLine2.Substring(TLE2_COL_INCLINATION, TLE2_LEN_INCLINATION).TrimStart();
   m_Field[Field.Raan] =
    m_strLine2.Substring(TLE2_COL_RAASCENDNODE, TLE2_LEN_RAASCENDNODE).TrimStart();
   m_Field[Field.Eccentricity] = "0." + m_strLine2.Substring(TLE2_COL_ECCENTRICITY,
    TLE2_LEN_ECCENTRICITY);
   m_Field[Field.ArgPerigee] =
    m_strLine2.Substring(TLE2_COL_ARGPERIGEE, TLE2_LEN_ARGPERIGEE).TrimStart();
   m_Field[Field.MeanAnomaly] =
    m_strLine2.Substring(TLE2_COL_MEANANOMALY, TLE2_LEN_MEANANOMALY).TrimStart();
   m_Field[Field.MeanMotion] =
    m_strLine2.Substring(TLE2_COL_MEANMOTION, TLE2_LEN_MEANMOTION).TrimStart();
   m_Field[Field.OrbitAtEpoch] =
    m_strLine2.Substring(TLE2_COL_REVATEPOCH, TLE2_LEN_REVATEPOCH).TrimStart();
  }
  private const string CatTleData = "TLE Data";
  private const string CatElements = "Elements";
  [Category(CatTleData)]
  public string Name
  {
   get { return m_strName; }
  }
  [Category(CatTleData)]
  public string Line1
  {
   get { return m_strLine1; }
  }
  [Category(CatTleData)]
  public string Line2
  {
   get { return m_strLine2; }
  }
  [Category(CatElements)]
  public string NoradNumber
  {
   get { return getField(Field.NoradNum, false); }
  }
  [Category(CatElements)]
  public string Eccentricity
  {
   get { return getField(Field.Eccentricity, false); }
  }
  [Category(CatElements)]
  public string Inclination
  {
   get { return getField(Field.Inclination, true); }
  }
  [Category(CatElements)]
  public string Epoch
  {
   get
   {
    return getField(Field.EpochYear).ToString() +
     getField(Field.EpochDay).ToString();
   }
  }
  [Category(CatElements)]
  public string IntlDescription
  {
   get { return getField(Field.IntlDesc, false); }
  }
  [Category(CatElements)]
  public string SetNumber
  {
   get { return getField(Field.SetNumber, false); }
  }
  [Category(CatElements)]
  public string OrbitAtEpoch
  {
   get { return getField(Field.OrbitAtEpoch, false); }
  }
  [Category(CatElements)]
  public string RAAscendingNode
  {
   get { return getField(Field.Raan, true); }
  }
  [Category(CatElements)]
  public string ArgPerigee
  {
   get { return getField(Field.ArgPerigee, true); }
  }
  [Category(CatElements)]
  public string MeanAnomaly
  {
   get { return getField(Field.MeanAnomaly, true); }
  }
  [Category(CatElements)]
  public string MeanMotion
  {
   get { return getField(Field.MeanMotion, true); }
  }
  [Category(CatElements)]
  public string MeanMotionDt
  {
   get { return getField(Field.MeanMotionDt, false); }
  }
  [Category(CatElements)]
  public string MeanMotionDt2
  {
   get { return getField(Field.MeanMotionDt2, false); }
  }
  [Category(CatElements)]
  public string BStarDrag
  {
   get { return getField(Field.BStarDrag, false); }
  }
  public Julian EpochJulian
  {
   get
   {
    int epochYear = (int)getField(Tle.Field.EpochYear);
    double epochDay = getField(Tle.Field.EpochDay );
    if (epochYear < 57)
     epochYear += 2000;
    else
     epochYear += 1900;
    return new Julian(epochYear, epochDay);
   }
  }
  public double getField(Field fld)
  {
   return getField(fld, Unit.Native);
  }
  public double getField(Field fld, Unit units)
  {
   int key = Key(units, fld);
   if (m_Cache.ContainsKey(key))
   {
    return (double)m_Cache[key];
   }
   else
   {
    double valNative = Double.Parse(m_Field[fld].ToString());
    double valConv = ConvertUnits(valNative, fld, units);
    m_Cache[key] = valConv;
    return valConv;
   }
  }
  public string getField(Field fld, bool AppendUnits)
  {
   string str = m_Field[fld].ToString();
   if (AppendUnits)
   {
    str += getUnits(fld);
   }
   return str;
  }
  protected double ConvertUnits(double valNative,
   Field fld,
   Unit units)
  {
   if (fld == Field.Inclination ||
    fld == Field.Raan ||
    fld == Field.ArgPerigee ||
    fld == Field.MeanAnomaly)
   {
    if (units == Unit.Radians)
    {
     return valNative * Globals.RADS_PER_DEG;
    }
   }
   return valNative;
  }
  protected string getUnits(Field fld)
  {
   const string strDegrees = " degrees";
   const string strRevsPerDay = " revs / day";
   switch (fld)
   {
    case Field.Inclination:
    case Field.Raan:
    case Field.ArgPerigee:
    case Field.MeanAnomaly:
     return strDegrees;
    case Field.MeanMotion:
     return strRevsPerDay;
    default:
     return string.Empty;
   }
  }
  protected static string ExpToDecimal(string str)
  {
   const int COL_SIGN = 0;
   const int LEN_SIGN = 1;
   const int COL_MANTISSA = 1;
   const int LEN_MANTISSA = 5;
   const int COL_EXPONENT = 6;
   const int LEN_EXPONENT = 2;
   string Sign = str.Substring(COL_SIGN, LEN_SIGN);
   string Mantissa = str.Substring(COL_MANTISSA, LEN_MANTISSA);
   string Exponent = str.Substring(COL_EXPONENT, LEN_EXPONENT);
   double val = Double.Parse(Sign +"0." + Mantissa + "e" + Exponent);
   return val.ToString("f9");
  }
  public static bool IsValidLine(string str, Line line)
  {
   str.TrimStart();
   str.TrimEnd();
   int nLen = str.Length;
   if (nLen != TLE_LEN_LINE_DATA)
    return false;
   if ((str[0] - '0') != (int)line)
    return false;
   if (str[1] != ' ')
    return false;
   return true;
  }
  static int CheckSum(string str)
  {
   int len = str.Length - 1;
   int xsum = 0;
   for (int i = 0; i < len; i++)
   {
    char ch = str[i];
    if (Char.IsDigit(ch))
     xsum += (ch - '0');
    else if (ch == '-')
     xsum++;
   }
   return (xsum % 10);
  }
  public int CompareTo(object obj)
  {
   return (new CaseInsensitiveComparer()).Compare( this.Name, ((Tle)obj).Name ) ;
  }
 }
 public class Vector
 {
  private double m_x;
  private double m_y;
  private double m_z;
  private double m_w;
  public Vector()
  {
   m_x = 0.0;
   m_y = 0.0;
   m_z = 0.0;
   m_z = 0.0;
  }
  public Vector(double x, double y, double z)
  {
   m_x = x;
   m_y = y;
   m_z = z;
   m_w = 0.0;
  }
  public Vector(double x, double y, double z, double w)
  {
   m_x = x;
   m_y = y;
   m_z = z;
   m_w = w;
  }
  public double X { get { return m_x; } set { m_x = value;} }
  public double Y { get { return m_y; } set { m_y = value;} }
  public double Z { get { return m_z; } set { m_z = value;} }
  public double W { get { return m_w; } set { m_w = value;} }
  public void Mul(double factor)
  {
   m_x *= factor;
   m_y *= factor;
   m_z *= factor;
   m_w *= Math.Abs(factor);
  }
  public void Sub(Vector vec)
  {
   m_x -= vec.m_x;
   m_y -= vec.m_y;
   m_z -= vec.m_z;
   m_w -= vec.m_w;
  }
  public double Angle(Vector vec)
  {
   return Math.Acos(Dot(vec) / (Magnitude() * vec.Magnitude()));
  }
  public double Magnitude()
  {
   return Math.Sqrt((m_x * m_x) + (m_y * m_y) + (m_z * m_z));
  }
  public double Dot(Vector vec)
  {
   return (m_x * vec.m_x) + (m_y * vec.m_y) + (m_z * vec.m_z);
  }
 }
}
