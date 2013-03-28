using System;
using System.Collections;
using System.IO;
using System.Xml;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Net;
using Utility;
namespace WorldWind.Renderable
{
 public class TiledWFSPlacenameSet : RenderableObject
 {
  protected World m_parentWorld;
  protected double m_minimumDistanceSq;
  protected double m_maximumDistanceSq;
  protected string m_placenameBaseUrl;
  protected string m_iconFilePath;
  protected Sprite m_sprite;
  protected Font m_drawingFont;
  protected int m_color;
  protected ArrayList m_placenameFileList = new ArrayList();
  protected Hashtable m_placenameFiles = new Hashtable();
  protected Hashtable m_renderablePlacenames = new Hashtable();
  protected WorldWindPlacename[] m_placeNames;
  protected double m_altitude;
  protected Texture m_iconTexture;
  protected System.Drawing.Rectangle m_spriteSize;
  protected FontDescription m_fontDescription;
  protected DrawTextFormat m_textFormat = DrawTextFormat.None;
  protected static int IconWidth = 48;
  protected static int IconHeight = 48;
  public WorldWindPlacename[] PlaceNames
  {
   get{ return m_placeNames; }
  }
  public int Color
  {
   get{ return m_color; }
  }
  public FontDescription FontDescription
  {
   get{ return m_fontDescription; }
  }
  public TiledWFSPlacenameSet(
   string name,
   World parentWorld,
   double altitude,
   double maximumDisplayAltitude,
   double minimumDisplayAltitude,
   string placenameBaseUrl,
   FontDescription fontDescription,
   System.Drawing.Color color,
   string iconFilePath
   ) : base(name, parentWorld.Position, Quaternion.RotationYawPitchRoll(0,0,0))
  {
   m_parentWorld = parentWorld;
   m_altitude = altitude;
   m_maximumDistanceSq = maximumDisplayAltitude*maximumDisplayAltitude;
   m_minimumDistanceSq = minimumDisplayAltitude*minimumDisplayAltitude;
   m_placenameBaseUrl = placenameBaseUrl;
   m_fontDescription = fontDescription;
   m_color = color.ToArgb();
   m_iconFilePath = iconFilePath;
   m_renderPriority = RenderPriority.Placenames;
  }
  public override bool IsOn
  {
   get
   {
    return isOn;
   }
   set
   {
    if(isOn && !value)
     Dispose();
    isOn = value;
    if(Name=="Placenames")
     World.Settings.showPlacenames = value;
   }
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   this.isInitialized = true;
   m_drawingFont = drawArgs.CreateFont(m_fontDescription);
            WorldWindWFSPlacenameFile root_file = new WorldWindWFSPlacenameFile(m_placenameBaseUrl);
            m_placenameFileList = new ArrayList(root_file.SplitPlacenameFiles());
   if(m_iconFilePath!=null)
   {
    m_iconTexture = ImageHelper.LoadIconTexture( m_iconFilePath );
    using(Surface s = m_iconTexture.GetSurfaceLevel(0))
    {
     SurfaceDescription desc = s.Description;
     m_spriteSize = new System.Drawing.Rectangle(0,0, desc.Width, desc.Height);
    }
    m_sprite = new Sprite(drawArgs.device);
   }
  }
  public override void Dispose()
  {
   this.isInitialized = false;
   if(m_placenameFileList != null)
   {
    lock(m_placenameFileList.SyncRoot)
    {
     m_placenameFileList.Clear();
    }
   }
   if(m_placenameFiles != null)
   {
    lock(m_placenameFiles.SyncRoot)
    {
     m_placenameFiles.Clear();
    }
   }
   if(m_placeNames != null)
   {
    lock(this)
     m_placeNames = null;
   }
   if(m_iconTexture != null)
   {
    m_iconTexture.Dispose();
    m_iconTexture = null;
   }
   if(m_sprite != null)
   {
    m_sprite.Dispose();
    m_sprite = null;
   }
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   return false;
  }
  int curPlaceNameIndex;
  Matrix lastView = Matrix.Identity;
  public override void Update(DrawArgs drawArgs)
  {
   try
   {
    if(!this.isInitialized)
     this.Initialize(drawArgs);
    if(lastView != drawArgs.WorldCamera.ViewMatrix)
    {
     ArrayList tempPlacenames = new ArrayList();
     if((m_minimumDistanceSq == 0 && m_maximumDistanceSq == 0) ||
      drawArgs.WorldCamera.Altitude*drawArgs.WorldCamera.Altitude <= m_maximumDistanceSq)
     {
      curPlaceNameIndex=0;
      foreach(WorldWindWFSPlacenameFile placenameFileDescriptor in m_placenameFileList)
      {
       UpdateNames(placenameFileDescriptor, tempPlacenames, drawArgs);
      }
     }
     lock(this)
     {
                        if (tempPlacenames.Count > 0)
                            Console.WriteLine("SomePlace");
                        m_placeNames = new WorldWindPlacename[tempPlacenames.Count];
      tempPlacenames.CopyTo(m_placeNames);
     }
     lastView = drawArgs.WorldCamera.ViewMatrix;
    }
   }
   catch
   {
   }
  }
  void UpdateNames(WorldWindWFSPlacenameFile placenameFileDescriptor, ArrayList tempPlacenames, DrawArgs drawArgs)
  {
   double viewRange = drawArgs.WorldCamera.TrueViewRange.Degrees;
   double north = drawArgs.WorldCamera.Latitude.Degrees + viewRange;
   double south = drawArgs.WorldCamera.Latitude.Degrees - viewRange;
   double west = drawArgs.WorldCamera.Longitude.Degrees - viewRange;
   double east = drawArgs.WorldCamera.Longitude.Degrees + viewRange;
   if(placenameFileDescriptor.north < south)
    return;
   if(placenameFileDescriptor.south > north)
    return;
   if(placenameFileDescriptor.east < west)
    return;
   if(placenameFileDescriptor.west > east)
    return;
            WorldWindPlacename[] tilednames = placenameFileDescriptor.PlaceNames;
            tempPlacenames.Capacity = tempPlacenames.Count + tilednames.Length;
            WorldWindPlacename curPlace = new WorldWindPlacename();
            for (int i = 0; i < tilednames.Length; i++)
            {
                if (m_placeNames != null && curPlaceNameIndex < m_placeNames.Length)
                    curPlace = m_placeNames[curPlaceNameIndex];
                WorldWindPlacename pn = tilednames[i];
                float lat = pn.Lat;
                float lon = pn.Lon;
                float lonRanged = lon;
                if (lonRanged < west)
                    lonRanged += 360;
                if (lat > north || lat < south || lonRanged > east || lonRanged < west)
                    continue;
                float elevation = 0;
                if (m_parentWorld.TerrainAccessor != null && drawArgs.WorldCamera.Altitude < 300000)
                    elevation = (float)m_parentWorld.TerrainAccessor.GetElevationAt(lat, lon);
                float altitude = (float)(m_parentWorld.EquatorialRadius + World.Settings.VerticalExaggeration * m_altitude + World.Settings.VerticalExaggeration * elevation);
                pn.cartesianPoint = MathEngine.SphericalToCartesian(lat, lon, altitude);
                float distanceSq = Vector3.LengthSq(pn.cartesianPoint - drawArgs.WorldCamera.Position);
                if (distanceSq > m_maximumDistanceSq)
                    continue;
                if (distanceSq < m_minimumDistanceSq)
                    continue;
                if (!drawArgs.WorldCamera.ViewFrustum.ContainsPoint(pn.cartesianPoint))
                    continue;
                tempPlacenames.Add(pn);
            }
  }
  public override void Render(DrawArgs drawArgs)
  {
   try
   {
    lock(this)
    {
     Vector3 cameraPosition = drawArgs.WorldCamera.Position;
     if(m_placeNames==null)
      return;
     int outlineColor = unchecked((int)0x80ffffff);
     int brightness = (m_color & 0xff) +
      ((m_color >> 8) & 0xff) +
      ((m_color >> 16) & 0xff);
     if(brightness > 255*3/2)
      outlineColor = unchecked((int)0x80000000);
     if(m_sprite != null)
      m_sprite.Begin(SpriteFlags.AlphaBlend);
     int count = 0;
     Vector3 referenceCenter = new Vector3(
      (float)drawArgs.WorldCamera.ReferenceCenter.X,
      (float)drawArgs.WorldCamera.ReferenceCenter.Y,
      (float)drawArgs.WorldCamera.ReferenceCenter.Z);
     for(int index=0; index<m_placeNames.Length; index++)
     {
      Vector3 v = m_placeNames[index].cartesianPoint;
      float distanceSquared = Vector3.LengthSq(v-cameraPosition);
      if(distanceSquared > m_maximumDistanceSq)
       continue;
      if(distanceSquared < m_minimumDistanceSq)
       continue;
      if(!drawArgs.WorldCamera.ViewFrustum.ContainsPoint(v))
       continue;
      Vector3 pv = drawArgs.WorldCamera.Project(v - referenceCenter);
      string label = m_placeNames[index].Name;
      System.Drawing.Rectangle rect = m_drawingFont.MeasureString(null, label, m_textFormat, m_color );
      pv.Y -= rect.Height/2;
      if(m_sprite==null)
       pv.X -= rect.Width/2;
      rect.Inflate(3,1);
      int x = (int)Math.Round(pv.X);
      int y = (int)Math.Round(pv.Y);
      rect.Offset(x,y);
      if(World.Settings.outlineText)
      {
       m_drawingFont.DrawText(null,label, x-1, y-1, outlineColor );
       m_drawingFont.DrawText(null,label, x-1, y+1, outlineColor );
       m_drawingFont.DrawText(null,label, x+1, y-1, outlineColor );
       m_drawingFont.DrawText(null,label, x+1, y+1, outlineColor );
      }
      m_drawingFont.DrawText(null,label, x, y, m_color );
      count++;
      if(count>30)
       break;
     }
     if(m_sprite != null)
      m_sprite.End();
    }
   }
   catch(Exception caught)
   {
    Log.Write( caught );
   }
  }
 }
 public class WorldWindWFSPlacenameFile
 {
  public string wfsURL;
  public float north = 90.0f;
  public float south = -90.0f;
  public float west = -180.0f;
  public float east = 180.0f;
  protected WorldWindPlacename[] m_placeNames = null;
        public string wfsBaseUrl;
  public WorldWindWFSPlacenameFile(String wfsBaseUrl)
  {
            this.wfsBaseUrl = wfsBaseUrl;
  }
        public WorldWindPlacename[] PlaceNames
        {
            get {
                if (m_placeNames == null)
                {
                    DownloadParsePlacenames();
                }
                return m_placeNames;
            }
        }
  public WorldWindWFSPlacenameFile[] SplitPlacenameFiles()
  {
   WorldWindWFSPlacenameFile northWest = new WorldWindWFSPlacenameFile(this.wfsBaseUrl);
   northWest.north = this.north;
   northWest.south = 0.5f * (this.north + this.south);
   northWest.west = this.west;
   northWest.east = 0.5f * (this.west + this.east);
            northWest.wfsURL = northWest.wfsBaseUrl + "&BBOX=" + northWest.west + "," + northWest.east + "," + northWest.south + ","+northWest.north;
            WorldWindWFSPlacenameFile northEast = new WorldWindWFSPlacenameFile(this.wfsBaseUrl);
   northEast.north = this.north;
   northEast.south = 0.5f * (this.north + this.south);
   northEast.west = 0.5f * (this.west + this.east);
   northEast.east = this.east;
            northEast.wfsURL = northEast.wfsBaseUrl + "&BBOX=" + northEast.west + "," + northEast.east + "," + northEast.south + "," + northEast.north;
            WorldWindWFSPlacenameFile southWest = new WorldWindWFSPlacenameFile(this.wfsBaseUrl);
   southWest.north = 0.5f * (this.north + this.south);
   southWest.south = this.south;
   southWest.west = this.west;
   southWest.east = 0.5f * (this.west + this.east);
            southWest.wfsURL = southWest.wfsBaseUrl + "&BBOX=" + southWest.west + "," + southWest.east + "," + southWest.south + "," + southWest.north;
            WorldWindWFSPlacenameFile southEast = new WorldWindWFSPlacenameFile(this.wfsBaseUrl);
   southEast.north = 0.5f * (this.north + this.south);
   southEast.south = this.south;
   southEast.west = 0.5f * (this.west + this.east);
   southEast.east = this.east;
            southEast.wfsURL = southEast.wfsBaseUrl + "&BBOX=" + southEast.west + "," + southEast.east + "," + southEast.south + "," + southEast.north;
   WorldWindWFSPlacenameFile[] returnArray = new WorldWindWFSPlacenameFile[] {northWest, northEast, southWest, southEast};
   return returnArray;
  }
        private void DownloadParsePlacenames()
        {
            string cachefilename = "Cache//WFS//"+(this.east+this.west)/2+
                "_"+
                (this.north+this.south)/2+".xml";
            if (!File.Exists(cachefilename))
            {
                WebDownload wfsdl = new WebDownload(this.wfsURL);
                wfsdl.DownloadFile(cachefilename);
            }
            XmlDocument gmldoc = new XmlDocument();
            gmldoc.Load(cachefilename);
            XmlNamespaceManager xmlnsManager = new XmlNamespaceManager(gmldoc.NameTable);
            xmlnsManager.AddNamespace("gml", "http://www.opengis.net/gml");
            XmlNodeList rootNode = gmldoc.SelectNodes("//gml:featureMember",xmlnsManager);
            if(rootNode!=null){
                if(rootNode.Count > 0)
                {
                    Console.WriteLine(rootNode.Count);
                    m_placeNames = new WorldWindPlacename[rootNode.Count];
                    for (int i = 0; i < rootNode.Count; i++)
                    {
                        XmlNode coordinateNode = rootNode[i].SelectSingleNode("//gml:Point/gml:coordinates", xmlnsManager);
                        if (coordinateNode != null)
                        {
                            string coordinateNodeText = coordinateNode.InnerText;
                            string[] coords = coordinateNodeText.Split(',');
                            WorldWindPlacename pn = new WorldWindPlacename();
                            pn.Lon = Convert.ToSingle(coords[0]);
                            pn.Lat = Convert.ToSingle(coords[1]);
                            pn.Name = "Somename";
                            m_placeNames[i] = pn;
                        }
                    }
                }
            }
        }
 }
}
