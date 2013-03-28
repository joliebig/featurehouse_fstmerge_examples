using System;
using System.IO;
using System.Net;
using System.Xml;
using System.Drawing;
using System.Threading;
using System.Reflection;
using System.Collections;
using System.Diagnostics;
using System.Globalization;
using System.Windows.Forms;
using System.ComponentModel;
using System.Security.Cryptography;
using System.Text.RegularExpressions;
using ICSharpCode.SharpZipLib.Zip;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Net;
using WorldWind.Renderable;
using Utility;
namespace KMLPlugin
{
 public class KMLImporter : WorldWind.PluginEngine.Plugin
 {
  private const string version = "1.08";
  private const int IconSizeConstant = 32;
  private RIcons KMLIcons;
  private MenuItem tempMenu = new MenuItem();
  private MenuItem aboutMenuItem = new MenuItem();
  private MenuItem pluginMenuItem = new MenuItem();
  private MenuItem napalmMenuItem = new MenuItem();
  private MenuItem labelMenuItem = new MenuItem();
  private Hashtable iconStyles = new Hashtable();
  private Hashtable bitmapCache = new Hashtable();
  internal string KMLPath;
  private ArrayList networkLinks = new ArrayList();
  private World m_world;
  static string KmlDirectory = Path.Combine(Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath), "kml");
  public override void Load()
  {
   Settings.LoadSettings(Path.Combine(KmlDirectory, "KMLImporter.xml"));
   KMLIcons = new RIcons("KML Icons");
   KMLIcons.IsOn = false;
   m_Application.WorldWindow.DragEnter += new DragEventHandler(WorldWindow_DragEnter);
   m_Application.WorldWindow.DragDrop += new DragEventHandler(WorldWindow_DragDrop);
   MenuItem loadMenuItem = new MenuItem();
   loadMenuItem.Text = "Load KML/KMZ file...";
   loadMenuItem.Click += new EventHandler(loadMenu_Click);
   aboutMenuItem.Text = "About KMLImporter";
   aboutMenuItem.Click += new EventHandler(aboutMenu_Click);
   int mergeOrder = 0;
   foreach (MenuItem menuItem in m_Application.MainMenu.MenuItems)
   {
    if (menuItem.Text.Replace("&", "") == "File")
    {
     foreach (MenuItem subMenuItem in menuItem.MenuItems)
     {
      subMenuItem.MergeOrder = mergeOrder;
      if (subMenuItem.Text == "-")
       mergeOrder = 2;
     }
     tempMenu.Text = menuItem.Text;
     tempMenu.MergeOrder = 1;
     tempMenu.MenuItems.Add(loadMenuItem);
     tempMenu.MenuItems.Add(new MenuItem("-"));
     menuItem.MergeMenu(tempMenu);
    }
    if (menuItem.Text.Replace("&", "") == "Help")
     menuItem.MenuItems.Add(aboutMenuItem);
   }
   bool bEnabled = Napalm.NapalmIsEnabled(KmlDirectory);
   if (bEnabled)
    napalmMenuItem.Text = "Disable KMLImporter autoupdate";
   else
    napalmMenuItem.Text = "Enable KMLImporter autoupdate";
   napalmMenuItem.Click += new EventHandler(napalmMenu_Click);
   pluginMenuItem.MenuItems.Add(napalmMenuItem);
   labelMenuItem.Text = "Show all labels";
   labelMenuItem.Checked = Settings.ShowAllLabels;
   labelMenuItem.Click += new EventHandler(labelMenuItem_Click);
   pluginMenuItem.MenuItems.Add(labelMenuItem);
   pluginMenuItem.Text = "KMLImporter";
   m_Application.PluginsMenu.MenuItems.Add(pluginMenuItem);
   Type typecontroller = typeof(MainApplication);
   System.Reflection.PropertyInfo finfo = typecontroller.GetProperty("CmdArgs", BindingFlags.Static|BindingFlags.Public|BindingFlags.GetProperty);
   string[] temp = null;
   if(finfo != null)
   {
    temp = (string[])finfo.GetValue(null, null);
    if (temp != null)
    {
     foreach (string arg in temp)
     {
      if (!File.Exists(arg))
       continue;
      string fExt = Path.GetExtension(arg);
      if (fExt != ".kml" && fExt != ".kmz")
       continue;
      LoadDiskKM(arg);
      break;
     }
    }
   }
   m_Application.WorldWindow.CurrentWorld.RenderableObjects.Add(KMLIcons);
   m_world = m_Application.WorldWindow.CurrentWorld;
   base.Load();
  }
  public override void Unload()
  {
   Cleanup();
   Settings.SaveSettings(Path.Combine(KmlDirectory, "KMLImporter.xml"));
   m_Application.WorldWindow.CurrentWorld.RenderableObjects.Remove(KMLIcons);
   this.Application.WorldWindow.DragEnter -= new DragEventHandler(WorldWindow_DragEnter);
   this.Application.WorldWindow.DragDrop -= new DragEventHandler(WorldWindow_DragDrop);
   foreach (MenuItem menuItem in m_Application.MainMenu.MenuItems)
   {
    if (menuItem.Text.Replace("&", "") == "File")
    {
     foreach (MenuItem subMenuItem in menuItem.MenuItems)
     {
      if (subMenuItem.Text == tempMenu.MenuItems[0].Text)
      {
       menuItem.MenuItems.RemoveAt(subMenuItem.Index+1);
       menuItem.MenuItems.RemoveAt(subMenuItem.Index);
       break;
      }
     }
    }
    if (menuItem.Text.Replace("&", "") == "Help")
     menuItem.MenuItems.Remove(aboutMenuItem);
   }
   tempMenu.MenuItems.Clear();
            m_Application.PluginsMenu.MenuItems.Remove(pluginMenuItem);
   try
   {
    if (Directory.Exists(Path.Combine(KmlDirectory, "kmz")))
     Directory.Delete(Path.Combine(KmlDirectory, "kmz"), true);
    foreach (string kmlfile in Directory.GetFiles(KmlDirectory, "*.kml"))
    {
     try
     {
      File.Delete(kmlfile);
     }
     catch (System.IO.IOException)
     { }
    }
    foreach (string kmzfile in Directory.GetFiles(KmlDirectory, "*.kmz"))
    {
     try
     {
      File.Delete(kmzfile);
     }
     catch (System.IO.IOException)
     { }
    }
   }
   catch (Exception) {}
   base.Unload();
  }
  private void LoadDiskKM(string filename)
  {
   if (Path.GetExtension(filename) == ".kmz")
   {
    bool shouldReturn;
    string ExtractedKMLPath = ExtractKMZ(filename, out shouldReturn);
    if (shouldReturn)
     return;
    Spawn_LoadKML(ExtractedKMLPath);
   }
   else
   {
    Spawn_LoadKML(filename);
   }
   KMLIcons.IsOn = true;
  }
  private void Spawn_LoadKML(string path)
  {
   KMLPath = path;
   ThreadStart threadStart = new ThreadStart(LoadKMLFile);
   Thread kmlThread = new System.Threading.Thread(threadStart);
   kmlThread.Name = "KMLImporter worker thread";
   kmlThread.IsBackground = true;
   kmlThread.Start();
   Napalm.Update(KmlDirectory, version);
  }
  private void LoadKMLFile()
  {
   if (KMLPath == null || KMLIcons == null)
    return;
   Cleanup();
   WaitMessage waitMessage = new WaitMessage();
   KMLIcons.ChildObjects.Add(waitMessage);
   try
   {
    System.IO.StreamReader sr = new StreamReader(KMLPath);
    string kml = sr.ReadToEnd();
    try
    {
     LoadKML(kml, KMLIcons);
    }
    catch (Exception ex)
    {
     Log.Write(Log.Levels.Error, "KMLImporter: " + ex.ToString());
     MessageBox.Show(
      String.Format(CultureInfo.InvariantCulture, "Error loading KML file '{0}':\n\n{1}", KMLPath, ex.ToString()),
      "KMLImporter error",
      MessageBoxButtons.OK,
      MessageBoxIcon.Error,
      MessageBoxDefaultButton.Button1,
      base.Application.RightToLeft == RightToLeft.Yes ? MessageBoxOptions.RtlReading : MessageBoxOptions.ServiceNotification);
    }
    sr.Close();
   }
   catch(Exception ex)
   {
    Log.Write(Log.Levels.Error, "KMLImporter: " + ex.ToString());
    MessageBox.Show(
     String.Format(CultureInfo.InvariantCulture, "Error opening KML file '{0}':\n\n{1}", KMLPath, ex.ToString()),
     "KMLImporter error",
     MessageBoxButtons.OK,
     MessageBoxIcon.Error,
     MessageBoxDefaultButton.Button1,
     base.Application.RightToLeft == RightToLeft.Yes ? MessageBoxOptions.RtlReading : MessageBoxOptions.ServiceNotification);
   }
   KMLIcons.ChildObjects.Remove(waitMessage);
   KMLPath = null;
  }
  internal void LoadKML(string kml, RIcons layer)
  {
   kml = kml.Replace("xmlns=\"http://earth.google.com/kml/2.0\"", "");
   kml = kml.Replace("xmlns='http://earth.google.com/kml/2.0'", "");
   kml = kml.Replace("xmlns=\"http://earth.google.com/kml/2.1\"", "");
   kml = kml.Replace("xmlns='http://earth.google.com/kml/2.1'", "");
   XmlDocument doc = new XmlDocument();
   doc.LoadXml(kml);
   XmlNode node = doc.SelectSingleNode("//Document[name]/name");
   if(layer.Name == null || layer.Name.Length == 0 || layer.Name.Equals("KML Icons"))
   {
    if (node != null)
     layer.Name = node.InnerText;
   }
   ParseStyles(doc, KMLPath);
   XmlNode inNode = doc.SelectSingleNode("/kml/Document");
   if (inNode == null)
    inNode = doc.SelectSingleNode("/kml");
   if(inNode != null)
    ParseRenderables(inNode, layer, KMLPath);
  }
  private void ParseStyles(XmlDocument doc, string KmlPath)
  {
   XmlNodeList styles = doc.SelectNodes("//Style[@id]");
   foreach (XmlNode xstyle in styles)
   {
    string name = xstyle.Attributes.GetNamedItem("id").InnerText;
    if (iconStyles.ContainsKey(name))
     continue;
    Style style = GetStyle(xstyle, new Style(), KmlPath);
    if (style != null)
     iconStyles.Add(name, style);
   }
   XmlNodeList stylemaps = doc.SelectNodes("//StyleMap[@id]");
   foreach (XmlNode stylemap in stylemaps)
   {
    string name = stylemap.Attributes.GetNamedItem("id").InnerText;
    if (iconStyles.ContainsKey(name))
     continue;
    System.Xml.XmlNode stylemapNode = stylemap.SelectSingleNode("Pair[key=\"normal\"]/styleUrl");
    if (stylemapNode == null)
     continue;
    string normalName = stylemapNode.InnerText.Replace("#", "");
    XmlNode normalNode = doc.SelectSingleNode("//Style[@id='"+normalName+"']");
    Style style = GetStyle(normalNode, new Style(), KmlPath);
    if (style != null)
     iconStyles.Add(name, style);
   }
  }
  private void ParseRenderables(XmlNode inNode, RIcons layer, string KmlPath)
  {
   XmlNode visible = inNode.SelectSingleNode("visibility");
   if (visible != null)
   {
    if (visible.InnerText == "0")
     layer.IsOn = false;
   }
   ParseFolders(inNode, layer, KmlPath);
   ParseNetworkLinks(inNode, layer);
   ParseGroundOverlays(inNode, layer);
   ParseScreenOverlays(inNode, layer);
   ParsePlacemarks(inNode, layer, KmlPath);
   ParseLineStrings(inNode, layer);
   ParsePolygons(inNode, layer);
   ParseMultiGeometry(inNode, layer);
   layer.MetaData["Child count"] = layer.ChildObjects.Count;
  }
  private void ParseFolders(XmlNode inNode, RIcons layer, string KmlPath)
  {
   XmlNodeList folders = inNode.SelectNodes("Folder");
   foreach (XmlNode node in folders)
   {
    try
    {
     string foldername = "Folder";
     XmlNode nameNode = node.SelectSingleNode("name");
     if (nameNode != null)
      foldername = nameNode.InnerText;
     RIcons folder = null;
     foreach (RenderableObject ro in layer.ChildObjects)
     {
      RIcons ricons = ro as RIcons;
      if ((ricons != null) && (ro.Name == foldername))
      {
       folder = ricons;
      }
     }
     if (folder == null)
     {
      folder = new RIcons(foldername);
      layer.Add(folder);
     }
     XmlNode visibilityNode = node.SelectSingleNode("visibility");
     if(visibilityNode != null)
      layer.IsOn = (visibilityNode.InnerText == "1" ? true : false);
     ParseRenderables(node, folder, KmlPath);
    }
    catch (Exception ex)
                { Log.Write(Log.Levels.Error, "KMLImporter: " + ex.ToString()); }
   }
  }
  private void ParseNetworkLinks(XmlNode inNode, RIcons layer)
  {
   XmlNodeList networklinks = inNode.SelectNodes("NetworkLink");
   foreach (XmlNode node in networklinks)
   {
    try
    {
     string nlName = "NetworkLink";
     XmlNode nameNode = node.SelectSingleNode("name");
     if (nameNode != null)
      nlName = nameNode.InnerText;
     RIcons folder = null;
     foreach (RenderableObject ro in layer.ChildObjects)
     {
      RIcons ricons = ro as RIcons;
      if ((ricons != null) && (ro.Name == nlName))
      {
       folder = ricons;
      }
     }
     if (folder == null)
     {
      folder = new RIcons(nlName);
      layer.Add(folder);
     }
     XmlNode visibilityNode = node.SelectSingleNode("visibility");
     if(visibilityNode != null)
      folder.IsOn = (visibilityNode.InnerText == "1" ? true : false);
     string loadFile = null;
     XmlNode hrefNode = node.SelectSingleNode("Url/href");
     if ((hrefNode != null) && (hrefNode.InnerText.Length > 0))
      loadFile = hrefNode.InnerText;
     if (loadFile == null)
      continue;
     int tickSeconds = -1;
     int viewSeconds = -1;
     bool fired = false;
     if (node.SelectSingleNode("Url/refreshMode") != null)
     {
      if (node.SelectSingleNode("Url/refreshMode").InnerText == "onInterval")
      {
       string refreshText = node.SelectSingleNode("Url/refreshInterval").InnerText;
       tickSeconds = Convert.ToInt32(refreshText, CultureInfo.InvariantCulture);
      }
      if (node.SelectSingleNode("Url/refreshMode").InnerText == "once")
      {
       NetworkLink netLink = new NetworkLink(this, folder, loadFile, -1, -1);
       netLink.Fire();
       netLink.Dispose();
       fired = true;
      }
     }
     if ((node.SelectSingleNode("Url/viewRefreshMode") != null) && (node.SelectSingleNode("Url/viewRefreshMode").InnerText == "onStop"))
     {
      string refreshText = node.SelectSingleNode("Url/viewRefreshTime").InnerText;
      viewSeconds = Convert.ToInt32(refreshText, CultureInfo.InvariantCulture);
     }
     if (tickSeconds != -1 || viewSeconds != -1)
     {
      NetworkLink netLink = new NetworkLink(this, folder, loadFile, tickSeconds*1000, viewSeconds*1000);
      netLink.Fire();
      networkLinks.Add(netLink);
     }
     else if(!fired)
     {
      NetworkLink netLink = new NetworkLink(this, folder, loadFile, -1, -1);
      netLink.Fire();
      netLink.Dispose();
     }
    }
    catch (Exception ex)
                { Log.Write(Log.Levels.Error, "KMLImporter: " + ex.ToString()); }
   }
  }
  private void ParsePlacemarks(XmlNode inNode, RIcons layer, string KmlPath)
  {
   foreach (WorldWind.Renderable.RenderableObject ro in layer.ChildObjects)
   {
    RIcon ricon = ro as RIcon;
    if (ricon != null)
     ricon.HasBeenUpdated = false;
   }
   XmlNodeList placemarks = inNode.SelectNodes("Placemark[name and Point]");
   foreach (XmlNode node in placemarks)
   {
    try
    {
     string name = node.SelectSingleNode("name").InnerText;
     RIcon update = null;
     string loc = node.SelectSingleNode("Point/coordinates").InnerText.Trim();
     LLA lla = ParseCoordinate(loc);
     string desc = null;
     string uri = null;
     XmlNode xnode = node.SelectSingleNode("description");
     if (xnode != null)
     {
      string descRaw = xnode.InnerText;
      uri = SearchUri(descRaw);
                        desc = descRaw;
      if (desc.Length > 505)
       desc = desc.Substring(0, 500) + "...";
     }
     float rotation = 0;
     bool bRotated = false;
     string rotRaw = null;
     XmlNode rotNode1 = node.SelectSingleNode("Point/rotation");
     if (rotNode1 != null)
      rotRaw = rotNode1.InnerText;
     else
     {
      XmlNode rotNode2 = node.SelectSingleNode("IconStyle/heading");
      if (rotNode2 != null)
       rotRaw = rotNode2.InnerText;
      else
      {
       XmlNode rotNode3 = node.SelectSingleNode("Style/IconStyle/heading");
       if (rotNode3 != null)
        rotRaw = rotNode3.InnerText;
      }
     }
     if (rotRaw != null)
     {
      rotation = Convert.ToSingle(rotRaw, CultureInfo.InvariantCulture);
      bRotated = true;
     }
     Style style = LocateStyle(node, KmlPath);
     bool bExtrude = false;
     XmlNode extrudeNode = node.SelectSingleNode("Point/extrude");
     if (extrudeNode != null)
     {
      if (extrudeNode.InnerText == "1")
       bExtrude = true;
     }
     foreach (WorldWind.Renderable.RenderableObject ro in layer.ChildObjects)
     {
      RIcon ricon = ro as RIcon;
      if (ricon != null)
      {
       if ((ro.Name == name) && ((style == null) || ((ricon.NormalIcon == style.NormalIcon) && (!ricon.HasBeenUpdated))))
       {
        update = ricon;
        update.HasBeenUpdated = true;
        break;
       }
      }
     }
     if (update != null)
     {
      update.IsRotated = bRotated;
      if (bRotated)
      {
       update.Rotation = Angle.FromDegrees(rotation);
      }
                        if (style != null)
                        {
                            update.Height = Double.IsNaN(style.NormalScale) ? IconSizeConstant : (int)(style.NormalScale * Math.Min(((Bitmap)bitmapCache[style.NormalIcon]).Height, IconSizeConstant));
                            update.Width = Double.IsNaN(style.NormalScale) ? IconSizeConstant : (int)(style.NormalScale * Math.Min(((Bitmap)bitmapCache[style.NormalIcon]).Width, IconSizeConstant));
                            update.Description = desc;
                            update.SetPosition(lla.lat, lla.lon, lla.alt);
                        }
     }
     else
     {
      if (style != null)
      {
       CreateIcon(layer, name, desc, uri, lla.lat, lla.lon, lla.alt, style, bRotated, rotation, bExtrude);
      }
      else
      {
       string pal3Path = Path.Combine(KmlDirectory, "icons/palette-3.png");
       if (File.Exists(pal3Path))
       {
        if (!bitmapCache.Contains(pal3Path))
         bitmapCache.Add(pal3Path, (Bitmap)Bitmap.FromFile(pal3Path));
        Style pinStyle = new Style(GetSubImage(new Style(pal3Path), 448, 64, 64, 64));
        CreateIcon(layer, name, desc, uri, lla.lat, lla.lon, lla.alt, pinStyle, bRotated, rotation, bExtrude);
       }
      }
     }
    }
    catch (Exception ex)
                { Log.Write(Log.Levels.Error, "KMLImporter: " + ex.ToString()); }
   }
   RemoveUnusedIcons(layer);
  }
  private void ParseGroundOverlays(XmlNode inNode, RIcons layer)
  {
   XmlNodeList groundOverlays = inNode.SelectNodes("GroundOverlay[name and LatLonBox]");
   foreach (XmlNode node in groundOverlays)
   {
    XmlNode nameNode = node.SelectSingleNode("name");
    string name = nameNode.InnerText;
    XmlNode latLonBoxNode = node.SelectSingleNode("LatLonBox");
    if(latLonBoxNode != null)
    {
     XmlNode northNode = latLonBoxNode.SelectSingleNode("north");
     XmlNode southNode = latLonBoxNode.SelectSingleNode("south");
     XmlNode westNode = latLonBoxNode.SelectSingleNode("west");
     XmlNode eastNode = latLonBoxNode.SelectSingleNode("east");
     double north = ConfigurationLoader.ParseDouble(northNode.InnerText);
     double south = ConfigurationLoader.ParseDouble(southNode.InnerText);
     double west = ConfigurationLoader.ParseDouble(westNode.InnerText);
     double east = ConfigurationLoader.ParseDouble(eastNode.InnerText);
     WorldWind.Renderable.ImageLayer imageLayer = new ImageLayer(
      name,
      ParentApplication.WorldWindow.CurrentWorld,
      0,
      null,
      south,
      north,
      west,
      east,
      1.0,
      ParentApplication.WorldWindow.CurrentWorld.TerrainAccessor
      );
     imageLayer.DisableZBuffer = true;
     imageLayer.ImageUrl = node.SelectSingleNode("Icon/href").InnerText;
     XmlNode visibilityNode = node.SelectSingleNode("visibility");
     if(visibilityNode != null)
      imageLayer.IsOn = (visibilityNode.InnerText == "1" ? true : false);
     layer.Add(imageLayer);
    }
   }
  }
  private void ParseScreenOverlays(XmlNode inNode, RIcons layer)
  {
   XmlNodeList screenOverlays = inNode.SelectNodes("ScreenOverlay");
   if(screenOverlays!=null)
   {
    foreach(XmlNode screenOverlayNode in screenOverlays)
    {
     XmlNode nameNode = screenOverlayNode.SelectSingleNode("name");
     String name = "";
     if(nameNode != null)
      name = nameNode.InnerText;
                    XmlNode uriNode = screenOverlayNode.SelectSingleNode("Icon/href");
     String uri = "http://www.apogee.com.au/logo_topleft.gif";
                    if (uriNode != null)
                    {
                        uri = uriNode.InnerText;
                    }
                    float posX = 0;
                    float posY = 0;
                    ScreenUnits posXUnits = ScreenUnits.Pixels;
                    ScreenUnits posYUnits = ScreenUnits.Pixels;
                    XmlNode positionNode = screenOverlayNode.SelectSingleNode("screenXY");
                    if (positionNode != null)
                    {
                        if (positionNode.Attributes["x"] != null)
                        {
                            posX = float.Parse(positionNode.Attributes["x"].InnerText, CultureInfo.InvariantCulture);
                            if (positionNode.Attributes["xunits"].InnerText.ToLower() == "fraction")
                            {
                                posXUnits = ScreenUnits.Fraction;
                            }
                        }
                        if (positionNode.Attributes["y"] != null)
                        {
                            posY = float.Parse(positionNode.Attributes["y"].InnerText, CultureInfo.InvariantCulture);
                            if (positionNode.Attributes["yunits"].InnerText.ToLower() == "fraction")
                            {
                                posYUnits = ScreenUnits.Fraction;
                            }
                        }
                    }
                    ScreenOverlay scoverlay = new ScreenOverlay(name, posX, posY, uri);
                    scoverlay.PositionXUnits = posXUnits;
                    scoverlay.PositionYUnits = posYUnits;
                    scoverlay.ShowHeader = false;
                    XmlNode sizeNode = screenOverlayNode.SelectSingleNode("size");
                    if (sizeNode != null)
                    {
                        if (sizeNode.Attributes["x"] != null)
                        {
                            scoverlay.Width = float.Parse(sizeNode.Attributes["x"].InnerText, CultureInfo.InvariantCulture);
                            if(sizeNode.Attributes["xunits"].InnerText.ToLower() == "fraction")
                            {
                                scoverlay.SizeXUnits = ScreenUnits.Fraction;
                            }
                        }
                        if (sizeNode.Attributes["y"] != null)
                        {
                            scoverlay.Height = float.Parse(sizeNode.Attributes["y"].InnerText, CultureInfo.InvariantCulture);
                            if (sizeNode.Attributes["yunits"].InnerText.ToLower() == "fraction")
                            {
                                scoverlay.SizeYUnits = ScreenUnits.Fraction;
                            }
                        }
                    }
                    layer.Add(scoverlay);
    }
   }
  }
  private void ParseLineStrings(XmlNode inNode, RIcons layer)
  {
   XmlNodeList lineStrings = inNode.SelectNodes("Placemark[name and LineString]");
   foreach (XmlNode node in lineStrings)
   {
    XmlNode nameNode = node.SelectSingleNode("name");
    string name = nameNode.InnerText;
    Style style = null;
    XmlNode styleUrlNode = node.SelectSingleNode("styleUrl");
    if(styleUrlNode != null)
    {
     string styleUrlKey = styleUrlNode.InnerText.Trim();
     if(styleUrlKey.StartsWith("#"))
      styleUrlKey = styleUrlKey.Substring(1, styleUrlKey.Length - 1);
     style = (Style)iconStyles[styleUrlKey];
    }
    else
    {
     XmlNode styleNode = node.SelectSingleNode("Style");
     if(styleNode != null)
      style = GetStyle( styleNode, new Style(), "");
    }
    if(style == null)
     style = new Style();
    if(style.LineStyle == null)
     style.LineStyle = new LineStyle();
    if(style.PolyStyle == null)
     style.PolyStyle = new PolyStyle();
    bool extrude = false;
    XmlNode extrudeNode = node.SelectSingleNode("LineString/extrude");
    if (extrudeNode != null)
     extrude = Convert.ToBoolean(Convert.ToInt16(extrudeNode.InnerText));
    XmlNode outerRingNode = node.SelectSingleNode("LineString/coordinates");
    if(outerRingNode != null)
    {
     Point3d[] points = ParseCoordinates(outerRingNode);
                    LineFeature line = new LineFeature(name, m_world, points, System.Drawing.Color.FromArgb(style.LineStyle.Color.Color));
                    XmlNode altitudeModeNode = node.SelectSingleNode("LineString/altitudeMode");
                    line.AltitudeMode = GetAltitudeMode(altitudeModeNode);
                    line.LineWidth = (float)style.LineStyle.Width.Value;
                    if (extrude)
     {
      line.Extrude = true;
      if(style.PolyStyle.Color != null)
      {
       line.PolygonColor = System.Drawing.Color.FromArgb(style.PolyStyle.Color.Color);
      }
     }
     XmlNode visibilityNode = node.SelectSingleNode("visibility");
     if(visibilityNode != null)
      line.IsOn = (visibilityNode.InnerText == "1" ? true : false);
                    layer.Add(line);
    }
   }
  }
  private void ParseMultiGeometry(XmlNode inNode, RIcons layer)
  {
   XmlNodeList placemarkNodes = inNode.SelectNodes("Placemark[name and MultiGeometry]");
   Random rand = new Random((int)DateTime.Now.Ticks);
   foreach(XmlNode placemarkNode in placemarkNodes)
   {
    XmlNode nameNode = placemarkNode.SelectSingleNode("name");
    string name = nameNode.InnerText;
    RIcons multiGeometryList = new RIcons(name);
    Style style = null;
    XmlNode styleUrlNode = placemarkNode.SelectSingleNode("styleUrl");
    if(styleUrlNode != null)
    {
     string styleUrlKey = styleUrlNode.InnerText.Trim();
     if(styleUrlKey.StartsWith("#"))
      styleUrlKey = styleUrlKey.Substring(1, styleUrlKey.Length - 1);
     style = (Style)iconStyles[styleUrlKey];
    }
    else
    {
     XmlNode styleNode = placemarkNode.SelectSingleNode("Style");
     if(styleNode != null)
      style = GetStyle( styleNode, new Style(), "");
    }
    if(style == null)
     style = new Style();
    if(style.LineStyle == null)
     style.LineStyle = new LineStyle();
    if(style.PolyStyle == null)
     style.PolyStyle = new PolyStyle();
    XmlNodeList lineStringNodes = placemarkNode.SelectNodes("MultiGeometry/LineString");
    foreach(XmlNode lineStringNode in lineStringNodes)
    {
     bool extrude = false;
     XmlNode extrudeNode = lineStringNode.SelectSingleNode("extrude");
     if (extrudeNode != null)
      extrude = Convert.ToBoolean(Convert.ToInt16(extrudeNode.InnerText));
     XmlNode coordinateNode = lineStringNode.SelectSingleNode("coordinates");
     Point3d[] points = ParseCoordinates(coordinateNode);
     XmlNode altitudeModeNode = lineStringNode.SelectSingleNode("altitudeMode");
     AltitudeMode altitudeMode = GetAltitudeMode(altitudeModeNode);
     if(points != null && points.Length > 0)
     {
      LineFeature line = new LineFeature(
       name,
       m_world,
       points,
       System.Drawing.Color.FromArgb(style.LineStyle.Color.Color)
       );
      line.AltitudeMode = altitudeMode;
      if(style.PolyStyle.Color != null)
       line.PolygonColor = System.Drawing.Color.FromArgb(style.PolyStyle.Color.Color);
      line.LineWidth = (float)style.LineStyle.Width.Value;
      line.Extrude = extrude;
      multiGeometryList.Add(line);
     }
    }
    XmlNodeList polygonNodes = placemarkNode.SelectNodes("MultiGeometry/Polygon");
    foreach(XmlNode polygonNode in polygonNodes)
    {
     bool extrude = false;
     XmlNode extrudeNode = polygonNode.SelectSingleNode("extrude");
     if (extrudeNode != null)
      extrude = Convert.ToBoolean(Convert.ToInt16(extrudeNode.InnerText));
     XmlNode altitudeModeNode = polygonNode.SelectSingleNode("altitudeMode");
     AltitudeMode altitudeMode = GetAltitudeMode(altitudeModeNode);
     LinearRing outerRing = null;
     LinearRing[] innerRings = null;
     XmlNode outerRingNode = polygonNode.SelectSingleNode("outerBoundaryIs/LinearRing/coordinates");
     if (outerRingNode != null)
     {
      Point3d[] points = ParseCoordinates(outerRingNode);
      outerRing = new LinearRing();
      outerRing.Points = points;
     }
     XmlNodeList innerRingNodes = polygonNode.SelectNodes("innerBoundaryIs");
     if (innerRingNodes != null)
     {
      innerRings = new LinearRing[innerRingNodes.Count];
      for(int i = 0; i < innerRingNodes.Count; i++)
      {
       Point3d[] points = ParseCoordinates(innerRingNodes[i]);
       innerRings[i] = new LinearRing();
       innerRings[i].Points = points;
      }
     }
     if(outerRing != null)
     {
      PolygonFeature polygonFeature = new PolygonFeature(
       name,
       m_world,
       outerRing,
       innerRings,
       (style.PolyStyle.Color != null ? System.Drawing.Color.FromArgb(style.PolyStyle.Color.Color) : System.Drawing.Color.Yellow)
       );
      polygonFeature.Extrude = extrude;
      polygonFeature.AltitudeMode = altitudeMode;
      polygonFeature.Outline = style.PolyStyle.Outline;
      if(style.LineStyle.Color != null)
       polygonFeature.OutlineColor = System.Drawing.Color.FromArgb(style.LineStyle.Color.Color);
      multiGeometryList.Add(polygonFeature);
     }
    }
    XmlNode visibilityNode = placemarkNode.SelectSingleNode("visibility");
    if(visibilityNode != null)
     multiGeometryList.IsOn = (visibilityNode.InnerText == "1" ? true : false);
    layer.Add(multiGeometryList);
   }
  }
  private AltitudeMode GetAltitudeMode(XmlNode altitudeModeNode)
  {
   if(altitudeModeNode == null || altitudeModeNode.InnerText == null || altitudeModeNode.InnerText.Length == 0)
    return AltitudeMode.ClampedToGround;
   if(altitudeModeNode != null && altitudeModeNode.InnerText.Length > 0)
   {
    switch(altitudeModeNode.InnerText)
    {
     case "clampedToGround":
      return AltitudeMode.ClampedToGround;
     case "relativeToGround":
      return AltitudeMode.RelativeToGround;
     case "absolute":
      return AltitudeMode.Absolute;
    }
   }
   return AltitudeMode.ClampedToGround;
  }
  private void ParsePolygons(XmlNode inNode, RIcons layer)
  {
   XmlNodeList polygons = inNode.SelectNodes("Placemark[name and Polygon]");
   Random rand = new Random((int)DateTime.Now.Ticks);
   foreach (XmlNode node in polygons)
   {
    XmlNode nameNode = node.SelectSingleNode("name");
    string name = nameNode.InnerText;
    Style style = null;
    XmlNode styleUrlNode = node.SelectSingleNode("styleUrl");
    if(styleUrlNode != null)
    {
     string styleUrlKey = styleUrlNode.InnerText.Trim();
     if(styleUrlKey.StartsWith("#"))
      styleUrlKey = styleUrlKey.Substring(1, styleUrlKey.Length - 1);
     style = (Style)iconStyles[styleUrlKey];
    }
    else
    {
     XmlNode styleNode = node.SelectSingleNode("Style");
     if(styleNode != null)
      style = GetStyle( styleNode, new Style(), "");
    }
    if(style == null)
     style = new Style();
    if(style.LineStyle == null)
     style.LineStyle = new LineStyle();
    if(style.PolyStyle == null)
     style.PolyStyle = new PolyStyle();
    bool extrude = false;
    XmlNode extrudeNode = node.SelectSingleNode("Polygon/extrude");
    if (extrudeNode != null)
     extrude = Convert.ToBoolean(Convert.ToInt16(extrudeNode.InnerText));
    XmlNode altitudeModeNode = node.SelectSingleNode("Polygon/altitudeMode");
    AltitudeMode altitudeMode = GetAltitudeMode(altitudeModeNode);
    LinearRing outerRing = null;
    LinearRing[] innerRings = null;
    XmlNode outerRingNode = node.SelectSingleNode("Polygon/outerBoundaryIs/LinearRing/coordinates");
    if (outerRingNode != null)
    {
     Point3d[] points = ParseCoordinates(outerRingNode);
     Console.WriteLine(points.Length);
     outerRing = new LinearRing();
     outerRing.Points = points;
    }
    XmlNodeList innerRingNodes = node.SelectNodes("Polygon/innerBoundaryIs");
    if (innerRingNodes != null)
    {
     innerRings = new LinearRing[innerRingNodes.Count];
     for(int i = 0; i < innerRingNodes.Count; i++)
     {
      Point3d[] points = ParseCoordinates(innerRingNodes[i]);
      innerRings[i] = new LinearRing();
      innerRings[i].Points = points;
     }
    }
    if(outerRing != null)
    {
     PolygonFeature polygonFeature = new PolygonFeature(
      name, m_world,
      outerRing,
      innerRings,
      System.Drawing.Color.FromArgb(style.PolyStyle.Color.Color));
     polygonFeature.Extrude = extrude;
     polygonFeature.AltitudeMode = altitudeMode;
     polygonFeature.Outline = style.PolyStyle.Outline;
     if(style.LineStyle.Color != null)
      polygonFeature.OutlineColor = System.Drawing.Color.FromArgb(style.LineStyle.Color.Color);
     XmlNode visibilityNode = node.SelectSingleNode("visibility");
     if(visibilityNode != null)
      polygonFeature.IsOn = (visibilityNode.InnerText == "1" ? true : false);
     layer.Add(polygonFeature);
    }
   }
  }
  private Point3d[] ParseCoordinates(XmlNode coordinatesNode)
  {
   string coordlist = coordinatesNode.InnerText.Trim();
   char[] splitters = {'\n', ' ', '\t', ','};
   string[] lines = coordlist.Split(splitters);
   ArrayList tokenList = new ArrayList();
   ArrayList points = new ArrayList();
   int tokenCount = 0;
   for(int i = 0; i < lines.Length; i++)
   {
    string token = lines[i].Trim();
    if(token.Length == 0 || token == String.Empty)
     continue;
    tokenCount++;
    tokenList.Add(token);
    if(tokenCount == 3)
    {
     double lon = double.Parse((string)tokenList[tokenList.Count - 3], CultureInfo.InvariantCulture);
     double lat = double.Parse((string)tokenList[tokenList.Count - 2], CultureInfo.InvariantCulture);
     double alt = double.Parse((string)tokenList[tokenList.Count - 1], CultureInfo.InvariantCulture);
     points.Add(new Point3d(lon, lat, alt));
     tokenCount = 0;
    }
   }
   return (Point3d[])points.ToArray(typeof(Point3d));
  }
  private LLA ParseCoordinate(string loc)
  {
   if(loc.StartsWith(","))
    loc = loc.Substring(1, loc.Length - 1);
   if(loc.EndsWith(","))
    loc = loc.Substring(0, loc.Length - 1);
   string sLon="0", sLat="0", sAlt="0";
   if (loc.Split(',').Length == 3)
   {
    sLon = loc.Substring(0, loc.IndexOf(",")).Trim();
    sLat = loc.Substring(loc.IndexOf(",") + 1, loc.LastIndexOf(",") - loc.IndexOf(",") - 1).Trim();
    sAlt = loc.Substring(loc.LastIndexOf(",") + 1, loc.Length - loc.LastIndexOf(",") - 1).Trim();
   }
   else
   {
    sLon = loc.Substring(0, loc.IndexOf(",")).Trim();
    sLat = loc.Substring(loc.LastIndexOf(",") + 1, loc.Length - loc.LastIndexOf(",") - 1).Trim();
   }
   float lat = Convert.ToSingle(sLat, CultureInfo.InvariantCulture);
   float lon = Convert.ToSingle(sLon, CultureInfo.InvariantCulture);
   float alt = Convert.ToSingle(sAlt, CultureInfo.InvariantCulture);
   LLA lla = new LLA(lat, lon, alt);
   return lla;
  }
  private Style GetStyle(XmlNode style, Style oldStyle, string KmlPath)
  {
   try
   {
    Style overStyle = oldStyle;
    bool bPalette = false;
    XmlNode scaleNode = style.SelectSingleNode("IconStyle/scale");
    if (scaleNode != null)
     overStyle.NormalScale = Convert.ToDouble(scaleNode.InnerText, CultureInfo.InvariantCulture);
    XmlNode hrefNode = style.SelectSingleNode("IconStyle/Icon/href");
    if (hrefNode != null)
    {
     string filename = hrefNode.InnerText;
     if (filename.StartsWith("root://"))
     {
      filename = Path.Combine(KmlDirectory, filename.Remove(0, 7));
      if (File.Exists(filename))
      {
       bPalette = true;
       overStyle.NormalIcon = GetDiskImage(filename);
      }
     }
     else if (filename.StartsWith("http://"))
     {
      overStyle.NormalIcon = GetWebImage(filename);
     }
     else if (File.Exists(Path.Combine(Path.GetDirectoryName(KmlPath), filename)))
     {
      overStyle.NormalIcon = GetDiskImage(Path.Combine(Path.GetDirectoryName(KmlPath), filename));
     }
     else if (File.Exists(Path.Combine(KmlDirectory, filename)))
     {
      overStyle.NormalIcon = GetDiskImage(Path.Combine(KmlDirectory, filename));
     }
    }
    XmlNode wNode = style.SelectSingleNode("IconStyle/Icon/w");
    XmlNode hNode = style.SelectSingleNode("IconStyle/Icon/h");
    if (wNode != null && hNode != null)
    {
     int w = Convert.ToInt32(wNode.InnerText, CultureInfo.InvariantCulture);
     int h = Convert.ToInt32(hNode.InnerText, CultureInfo.InvariantCulture);
     int x = 0, y = 0;
     XmlNode xNode = style.SelectSingleNode("IconStyle/Icon/x");
     if (xNode != null)
      x = Convert.ToInt32(xNode.InnerText, CultureInfo.InvariantCulture);
     XmlNode yNode = style.SelectSingleNode("IconStyle/Icon/y");
     if (yNode != null)
      y = Convert.ToInt32(yNode.InnerText, CultureInfo.InvariantCulture);
     if (bPalette)
      overStyle.NormalIcon = GetSubImage(overStyle, x*2, y*2, w*2, h*2);
     else
      overStyle.NormalIcon = GetSubImage(overStyle, x, y, w, h);
    }
    XmlNode iconNode = style.SelectSingleNode("icon");
    if (iconNode != null)
    {
     string filename = iconNode.InnerText;
     if (!filename.StartsWith("http://"))
      return null;
     overStyle.NormalIcon = GetWebImage(filename);
    }
    XmlNode balloonStyleNode = style.SelectSingleNode("BalloonStyle");
    if(balloonStyleNode != null)
    {
     BalloonStyle balloonStyle = new BalloonStyle();
     XmlNode balloonTextNode = balloonStyleNode.SelectSingleNode("text");
     if(balloonTextNode != null)
     {
      TextElement textElement = new TextElement();
      textElement.Text = balloonTextNode.InnerText;
      XmlNode textNodeColor = balloonTextNode.SelectSingleNode("textColor");
      if(textNodeColor != null)
       textElement.TextColor = new ColorElement(ParseColor(textNodeColor.InnerText));
      balloonStyle.Text = textElement;
     }
     XmlNode balloonTextColorNode = balloonStyleNode.SelectSingleNode("textColor");
     if(balloonTextColorNode != null)
      balloonStyle.TextColor = new ColorElement(ParseColor(balloonTextColorNode.InnerText));
     XmlNode balloonColorNode = balloonStyleNode.SelectSingleNode("color");
     if(balloonColorNode != null)
      balloonStyle.Color = new ColorElement(ParseColor(balloonColorNode.InnerText));
     overStyle.BalloonStyle = balloonStyle;
    }
    XmlNode iconStyleNode = style.SelectSingleNode("IconStyle");
    if(iconStyleNode != null)
    {
     XmlNode iconElementNode = iconStyleNode.SelectSingleNode("Icon");
     IconElement iconElement = new IconElement();
     if(iconElementNode != null)
     {
      XmlNode iconElementHrefNode = iconElementNode.SelectSingleNode("href");
      if (iconElementHrefNode != null)
      {
       string filename = iconElementHrefNode.InnerText;
       if (filename.StartsWith("root://"))
       {
        filename = Path.Combine(KmlDirectory, filename.Remove(0, 7));
        if (File.Exists(filename))
        {
         bPalette = true;
         iconElement.href = GetDiskImage(filename);
        }
       }
       else if (filename.StartsWith("http://"))
       {
        iconElement.href = GetWebImage(filename);
       }
       else if (File.Exists(Path.Combine(Path.GetDirectoryName(KmlPath), filename)))
       {
        iconElement.href = GetDiskImage(Path.Combine(Path.GetDirectoryName(KmlPath), filename));
       }
       else if (File.Exists(Path.Combine(KmlDirectory, filename)))
       {
        iconElement.href = GetDiskImage(Path.Combine(KmlDirectory, filename));
       }
      }
      XmlNode iconElementWNode = iconElementNode.SelectSingleNode("w");
      XmlNode iconElementHNode = iconElementNode.SelectSingleNode("h");
      if (iconElementWNode != null && iconElementHNode != null)
      {
       int w = Convert.ToInt32(wNode.InnerText, CultureInfo.InvariantCulture);
       int h = Convert.ToInt32(hNode.InnerText, CultureInfo.InvariantCulture);
       int x = 0, y = 0;
       XmlNode xNode = iconElementNode.SelectSingleNode("x");
       if (xNode != null)
        x = Convert.ToInt32(xNode.InnerText, CultureInfo.InvariantCulture);
       XmlNode yNode = iconElementNode.SelectSingleNode("y");
       if (yNode != null)
        y = Convert.ToInt32(yNode.InnerText, CultureInfo.InvariantCulture);
       if (bPalette)
        iconElement.href = GetSubImage(overStyle, x*2, y*2, w*2, h*2);
       else
        iconElement.href = GetSubImage(overStyle, x, y, w, h);
      }
      IconStyle iconStyle = new IconStyle(iconElement);
      XmlNode iconStyleColorNode = iconStyleNode.SelectSingleNode("color");
      if(iconStyleColorNode != null)
       iconStyle.Color = new ColorElement(ParseColor(iconStyleColorNode.InnerText));
      XmlNode iconStyleColorModeNode = iconStyleNode.SelectSingleNode("colorMode");
      if(iconStyleColorModeNode != null)
      {
       iconStyle.ColorMode = (iconStyleColorModeNode.InnerText.ToLower() == "random" ? ColorMode.Random : ColorMode.Normal);
      }
      XmlNode iconStyleHeadingNode = iconStyleNode.SelectSingleNode("heading");
      if(iconStyleHeadingNode != null)
       iconStyle.Heading = new DecimalElement(double.Parse(iconStyleHeadingNode.InnerText, CultureInfo.InvariantCulture));
      XmlNode iconStyleScaleNode = iconStyleNode.SelectSingleNode("scale");
      if(iconStyleScaleNode != null)
       iconStyle.Scale = new DecimalElement(double.Parse(iconStyleScaleNode.InnerText, CultureInfo.InvariantCulture));
      overStyle.IconStyle = iconStyle;
     }
    }
    XmlNode labelStyleNode = style.SelectSingleNode("LabelStyle");
    if(labelStyleNode != null)
    {
     LabelStyle labelStyle = new LabelStyle();
     XmlNode labelColorNode = labelStyleNode.SelectSingleNode("color");
     if(labelColorNode != null)
      labelStyle.Color = new ColorElement(ParseColor(labelColorNode.InnerText));
     XmlNode labelColorModeNode = labelStyleNode.SelectSingleNode("colorMode");
     if(labelColorModeNode != null)
      labelStyle.ColorMode = (labelColorModeNode.InnerText.ToLower() == "random" ? ColorMode.Random : ColorMode.Normal);
     XmlNode labelScaleNode = labelStyleNode.SelectSingleNode("scale");
     if(labelScaleNode != null)
      labelStyle.Scale = new DecimalElement(double.Parse(labelScaleNode.InnerText, CultureInfo.InvariantCulture));
     overStyle.LabelStyle = labelStyle;
    }
    XmlNode lineStyleNode = style.SelectSingleNode("LineStyle");
    if(lineStyleNode != null)
    {
     LineStyle lineStyle = new LineStyle();
     XmlNode lineColorNode = lineStyleNode.SelectSingleNode("color");
     if(lineColorNode != null)
      lineStyle.Color = new ColorElement(ParseColor(lineColorNode.InnerText));
     XmlNode lineColorModeNode = lineStyleNode.SelectSingleNode("colorMode");
     if(lineColorModeNode != null)
      lineStyle.ColorMode = (lineColorModeNode.InnerText.ToLower() == "random" ? ColorMode.Random : ColorMode.Normal);
     XmlNode lineWidthNode = lineStyleNode.SelectSingleNode("width");
     if(lineWidthNode != null)
      lineStyle.Width = new DecimalElement(double.Parse(lineWidthNode.InnerText, CultureInfo.InvariantCulture));
     overStyle.LineStyle = lineStyle;
    }
    XmlNode polyStyleNode = style.SelectSingleNode("PolyStyle");
    if(polyStyleNode != null)
    {
     PolyStyle polyStyle = new PolyStyle();
     XmlNode polyColorNode = polyStyleNode.SelectSingleNode("color");
     if(polyColorNode != null)
      polyStyle.Color = new ColorElement(ParseColor(polyColorNode.InnerText));
     XmlNode polyColorModeNode = polyStyleNode.SelectSingleNode("colorMode");
     if(polyColorModeNode != null)
      polyStyle.ColorMode = (polyColorModeNode.InnerText.ToLower() == "random" ? ColorMode.Random : ColorMode.Normal);
     XmlNode polyFillNode = polyStyleNode.SelectSingleNode("fill");
     if(polyFillNode != null)
      polyStyle.Fill = (polyFillNode.InnerText == "1" ? true : false);
     XmlNode polyOutlineNode = polyStyleNode.SelectSingleNode("outline");
     if(polyOutlineNode != null)
      polyStyle.Outline = (polyOutlineNode.InnerText == "1" ? true : false);
     overStyle.PolyStyle = polyStyle;
    }
    return overStyle;
   }
   catch (System.Net.WebException ex)
   {
                Log.Write(Log.Levels.Error, "KMLImporter: " + ex.ToString());
   }
   return null;
  }
  private int ParseColor(string s)
  {
   string a = s.Substring(0,2);
   string b = s.Substring(2,2);
   string g = s.Substring(4,2);
   string r = s.Substring(6,2);
   return System.Drawing.Color.FromArgb(
    int.Parse(a, System.Globalization.NumberStyles.HexNumber),
    int.Parse(r, System.Globalization.NumberStyles.HexNumber),
    int.Parse(g, System.Globalization.NumberStyles.HexNumber),
    int.Parse(b, System.Globalization.NumberStyles.HexNumber)).ToArgb();
  }
  private string GetSubImage(Style style, int x, int y, int w, int h)
  {
   string key = style.NormalIcon+ "|"
    +x.ToString("D5", CultureInfo.InvariantCulture)
    +y.ToString("D5", CultureInfo.InvariantCulture)
    +w.ToString("D5", CultureInfo.InvariantCulture)
    +h.ToString("D5", CultureInfo.InvariantCulture);
   if (bitmapCache.ContainsKey(key))
    return key;
   Bitmap outImage = new Bitmap(w, h);
   Graphics graphics = Graphics.FromImage(outImage);
   RectangleF destinationRect = new RectangleF(0, 0, w, h);
            if (style.NormalIcon != null && bitmapCache.Contains(style.NormalIcon))
            {
                System.Drawing.Bitmap bit = ((Bitmap)bitmapCache[style.NormalIcon]);
                RectangleF sourceRect = new RectangleF(x, bit.Height - y - h, w, h);
                graphics.DrawImage((Bitmap)bitmapCache[style.NormalIcon], destinationRect, sourceRect, GraphicsUnit.Pixel);
                graphics.Flush();
                bitmapCache.Add(key, outImage);
                return key;
            }
            else
            {
                return null;
            }
  }
  private string GetDiskImage(string filename)
  {
   if (bitmapCache.ContainsKey(filename))
    return filename;
   Bitmap bit = (Bitmap)Bitmap.FromFile(filename);
   bitmapCache.Add(filename, bit);
   return filename;
  }
  private string GetWebImage(string filename)
  {
   if (bitmapCache.ContainsKey(filename))
    return filename;
   WebDownload myDownload = new WebDownload(filename);
   myDownload.DownloadMemory();
   Bitmap bit = (Bitmap)System.Drawing.Bitmap.FromStream(myDownload.ContentStream);
   myDownload.Dispose();
   bitmapCache.Add(filename, bit);
   return filename;
  }
  private Style LocateStyle(XmlNode node, string KmlPath)
  {
   Style style = null;
   XmlNode styleNode = node.SelectSingleNode("styleUrl");
   if (styleNode != null)
   {
    string styleUrl = styleNode.InnerText.Replace("#", "");
    style = (Style)iconStyles[styleUrl];
   }
   styleNode = node.SelectSingleNode("Style");
   if (styleNode != null)
   {
    if (style != null)
     style = GetStyle(styleNode, style, KmlPath);
    else
     style = GetStyle(styleNode, new Style(), KmlPath);
   }
   return style;
  }
  private void WorldWindow_DragEnter(object sender, DragEventArgs e)
  {
   if (DragDropIsValid(e))
    e.Effect = DragDropEffects.All;
  }
  private void WorldWindow_DragDrop(object sender, DragEventArgs e)
  {
   if (DragDropIsValid(e))
   {
    string[] files = (string[])e.Data.GetData(DataFormats.FileDrop);
    if (files.Length > 0 && File.Exists(files[0]))
    {
     LoadDiskKM(files[0]);
    }
   }
  }
  private static bool DragDropIsValid(DragEventArgs e)
  {
   if( e.Data.GetDataPresent(DataFormats.FileDrop, false))
   {
    if (((string[])e.Data.GetData(DataFormats.FileDrop)).Length == 1)
    {
     string extension = Path.GetExtension(((string[])e.Data.GetData(DataFormats.FileDrop))[0]).ToLower(CultureInfo.InvariantCulture);
     if ((extension == ".kml") || (extension == ".kmz"))
      return true;
    }
   }
   return false;
  }
  internal string ExtractKMZ(string filename, out bool bError)
  {
   bError = false;
   FileInfo fileInfo = new FileInfo(filename);
   string ExtractPath = Path.Combine(KmlDirectory, "kmz\\" + fileInfo.Name);
   if (!Directory.Exists(ExtractPath))
    Directory.CreateDirectory(ExtractPath);
   FastZip fz = new FastZip();
   fz.ExtractZip(filename, ExtractPath, "");
   string ExtractedKMLPath = null;
   if (File.Exists(Path.Combine(ExtractPath, "doc.kml")))
    ExtractedKMLPath = Path.Combine(ExtractPath, "doc.kml");
   else
   {
    ExtractedKMLPath = GetKMLFromDirectory(ExtractPath);
    if (ExtractedKMLPath == null)
     bError = true;
   }
   return ExtractedKMLPath;
  }
  private string GetKMLFromDirectory(string ExtractPath)
  {
   string[] folders = Directory.GetDirectories(ExtractPath);
   foreach (string folder in folders)
   {
    string tempPath = GetKMLFromDirectory(folder);
    if (tempPath != null)
     return tempPath;
   }
   string[] kmlfiles = Directory.GetFiles(ExtractPath, "*.kml");
   if (kmlfiles.Length > 0)
    return kmlfiles[0];
   else
    return null;
  }
  private void CreateIcon(RIcons layer, string Name, string Desc, string Uri, float Lat, float Lon, float Alt,
   Style style, bool bRotated, float rotation, bool bExtrude)
  {
   RIcon ic = new RIcon(
    Name,
    Lat,
    Lon,
    style.NormalIcon,
    Alt);
   if (Desc != null)
    ic.Description = Desc;
   if (Uri != null)
    ic.ClickableActionURL = Uri;
   if (bRotated)
   {
    ic.Rotation = Angle.FromDegrees(rotation);
    ic.IsRotated = true;
   }
   ic.m_drawGroundStick = bExtrude;
            if (style.NormalIcon != null && bitmapCache.Contains(style.NormalIcon))
            {
                ic.Image = (Bitmap)bitmapCache[style.NormalIcon];
                ic.Height = Double.IsNaN(style.NormalScale) ? IconSizeConstant : (int)(style.NormalScale * Math.Min(((Bitmap)bitmapCache[style.NormalIcon]).Height, IconSizeConstant));
                ic.Width = Double.IsNaN(style.NormalScale) ? IconSizeConstant : (int)(style.NormalScale * Math.Min(((Bitmap)bitmapCache[style.NormalIcon]).Width, IconSizeConstant));
            }
   layer.Add(ic);
  }
  private void loadMenu_Click(object sender, EventArgs e)
  {
   System.Windows.Forms.OpenFileDialog fileDialog = new OpenFileDialog();
   fileDialog.CheckFileExists = true;
   fileDialog.Filter = "KML/KMZ files (*.kml *.kmz)|*.kml;*.kmz";
   fileDialog.Multiselect = false;
   fileDialog.RestoreDirectory = true;
   DialogResult result = fileDialog.ShowDialog();
   if (result == DialogResult.OK)
   {
    LoadDiskKM(fileDialog.FileName);
   }
  }
  private void Cleanup()
  {
   foreach (RenderableObject ro in KMLIcons.ChildObjects)
   {
    ro.Dispose();
   }
   KMLIcons.ChildObjects.Clear();
   foreach (Object bit in bitmapCache)
   {
    Bitmap bitmap = bit as Bitmap;
    if (bitmap != null)
     bitmap.Dispose();
   }
   bitmapCache.Clear();
   foreach (NetworkLink netLink in networkLinks)
   {
    netLink.Dispose();
   }
   networkLinks.Clear();
   iconStyles.Clear();
  }
  private void aboutMenu_Click(object sender, EventArgs e)
  {
   AboutForm aboutForm = new AboutForm();
   aboutForm.ShowDialog();
  }
  private void napalmMenu_Click(object sender, EventArgs e)
  {
   bool bEnabled = Napalm.NapalmChangeStatus(KmlDirectory, napalmMenuItem.Text.StartsWith("Enable"));
   if (bEnabled)
    napalmMenuItem.Text = "Disable KMLImporter autoupdate";
   else
    napalmMenuItem.Text = "Enable KMLImporter autoupdate";
  }
  private void labelMenuItem_Click(object sender, EventArgs e)
  {
   labelMenuItem.Checked = Settings.ShowAllLabels = !labelMenuItem.Checked;
  }
  private static string SearchUri(string source)
  {
   int i = source.IndexOf("<a href");
   if (i != -1)
   {
    int start = source.Substring(i).IndexOf("\"") + i+1;
    int end = source.Substring(start+1).IndexOf("\"") + start+1;
    return source.Substring(start, end-start);
   }
   int start2 = source.IndexOf("http://");
   if (start2 != -1)
   {
    int end1 = source.Substring(start2+1).IndexOf("\n");
    int end2 = source.Substring(start2+1).IndexOf(" ");
    int end3 = source.Length -1;
    if (end1 == -1)
     end1 = Int32.MaxValue;
    else
     end1 += start2+1;
    if (end2 == -1)
     end2 = Int32.MaxValue;
    else
     end2 += start2+1;
    if (end3 == -1)
     end3 = Int32.MaxValue;
    int compareend1 = (end1 < end2) ? end1 : end2;
    int compareend2 = (end3 < compareend1) ? end3 : compareend1;
    string uri = source.Substring(start2, compareend2-start2);
    uri = uri.Replace(@"&amp;", @"&");
    uri = uri.Replace(@"&lt;", @"<");
    uri = uri.Replace(@"&gt;", @">");
    uri = uri.Replace(@"&apos;", @"'");
    uri = uri.Replace(@"&quot;", "\"");
    return uri;
   }
   return null;
  }
  private static string RemoveTags(string source)
  {
   while (true)
   {
    if ((source.IndexOf("<") == -1) || (source.IndexOf(">") == -1))
     break;
    int start = source.IndexOf("<");
    int stop = source.IndexOf(">") + 1;
    int count = stop - start;
    if (count < 0)
     break;
    source = source.Remove(start, count);
   }
   source = source.Replace("&nbsp;", "");
   return source.Trim();
  }
  private static void RemoveUnusedIcons(RIcons layer)
  {
   ArrayList VFD = new ArrayList();
   foreach (WorldWind.Renderable.RenderableObject ro in layer.ChildObjects)
   {
    RIcon ricon = ro as RIcon;
    if ((ricon != null) && (!ricon.HasBeenUpdated))
    {
     VFD.Add(ro);
    }
   }
   foreach (WorldWind.Renderable.RenderableObject ro in VFD)
   {
    layer.Remove(ro);
    ro.Dispose();
   }
  }
 }
 class Napalm
 {
  private const string plugName = "KMLImporter";
  private const string baseUrl = "http://worldwind.arc.nasa.gov";
  private delegate void UpdateDelegate (string PluginDirectory, string version);
  private Napalm()
  {
  }
  internal static void Update(string PluginDirectory, string version)
  {
   UpdateDelegate udel = new UpdateDelegate(WebUpdate);
   udel.BeginInvoke(PluginDirectory, version, null, null);
  }
  private static void WebUpdate(string PluginDirectory, string version)
  {
   CultureInfo icy = CultureInfo.InvariantCulture;
   Thread.CurrentThread.CurrentCulture = CultureInfo.InvariantCulture;
   try
   {
    if (!NapalmIsEnabled(PluginDirectory))
     return;
    if (!File.Exists(Path.Combine(PluginDirectory, plugName+".cs")))
     return;
    string URL = String.Format(icy, "{0}/{1}/{1}_ver.txt", baseUrl, plugName);
    WebClient verDownloader = new WebClient();
    Stream response = new MemoryStream(verDownloader.DownloadData(URL));
    System.IO.StreamReader sr = new StreamReader(response);
    string ver = sr.ReadLine();
    if (ver != version)
    {
     try
     {
      if (Convert.ToSingle(ver, CultureInfo.InvariantCulture) < Convert.ToSingle(version, CultureInfo.InvariantCulture))
       return;
     }
     catch (Exception)
     {
      return;
     }
     string CsURL = String.Format(icy, "{0}/{1}/{1}.cs", baseUrl, plugName);
     string CsPath = Path.Combine(PluginDirectory, String.Format(icy, "{0}.cs_", plugName));
     WebClient csDownloader = new WebClient();
     csDownloader.DownloadFile(CsURL, CsPath);
     try
     {
      System.IO.StreamReader streamreader = new StreamReader(CsPath);
      byte[] testStringBytes = GetHashBytes(streamreader.ReadToEnd());
      RSAParameters RSAKeyInfo = new RSAParameters();
      System.IO.StreamReader keyreader = new System.IO.StreamReader(Path.Combine(PluginDirectory, "key"));
      RSAKeyInfo.Modulus = Convert.FromBase64String(keyreader.ReadLine());
      RSAKeyInfo.Exponent = Convert.FromBase64String(keyreader.ReadLine());
      byte[] SignedHashValue = Convert.FromBase64String(sr.ReadLine());
      RSACryptoServiceProvider RSAdecr = new RSACryptoServiceProvider();
      RSAdecr.ImportParameters(RSAKeyInfo);
      RSAPKCS1SignatureDeformatter RSADeformatter = new RSAPKCS1SignatureDeformatter(RSAdecr);
      RSADeformatter.SetHashAlgorithm("SHA1");
      if (!RSADeformatter.VerifySignature(testStringBytes, SignedHashValue))
      {
                            Log.Write(Log.Levels.Error, String.Format(icy, "{0}: The file signature is not valid!", plugName));
       return;
      }
     }
     catch (Exception ex)
     {
                        Log.Write(Log.Levels.Error, "Signature checking error:\n" + ex);
      return;
     }
     if ((File.Exists(CsPath)) && (new FileInfo(CsPath).Length > 2))
     {
      string bcpPath = Path.Combine(PluginDirectory, plugName+"_v"+version+".cs_");
      if (File.Exists(bcpPath))
       File.Delete(bcpPath);
      string tempPath = Path.Combine(PluginDirectory, plugName+".cs_");
      string plugPath = Path.Combine(PluginDirectory, plugName+".cs");
      File.Move(plugPath, bcpPath);
      for (int i = 0; (i < 5) && (File.Exists(tempPath)); i++)
      {
       try
       {
        File.Move(tempPath, plugPath);
       }
       catch (Exception)
       {
        System.Threading.Thread.Sleep(800);
       }
      }
     }
     string message = String.Format(icy, "The {0} plugin has been updated.\n", plugName);
     message += "If you experience any problems it is recommended that you reload the plugin.\n";
     MessageBox.Show(message,
      plugName+" updated",
      MessageBoxButtons.OK,
      MessageBoxIcon.Information,
      MessageBoxDefaultButton.Button1,
      MessageBoxOptions.ServiceNotification);
    }
    else if (ver != version)
    {
     if (MessageBox.Show("A new version of the "+plugName+" plugin is available.\nWould you like to go to the website to download the latest version?",
      "PlaneTracker update available",
      MessageBoxButtons.YesNo,
      MessageBoxIcon.Information,
      MessageBoxDefaultButton.Button1,
      MessageBoxOptions.ServiceNotification) == DialogResult.Yes)
      System.Diagnostics.Process.Start("http://www.worldwindcentral.com/wiki/Add-on:KMLImporter");
    }
   }
   catch (Exception)
   { }
  }
  private static byte[] GetHashBytes(string s)
  {
   byte[] data = System.Text.Encoding.UTF8.GetBytes(s);
   byte[] key = Convert.FromBase64String("szLIWrCoPJ3DSWInZx5Ye7sRz0MKBG3JpmgP2KgzlcWGvuJMqNiD77DVQuIRFvgbc5UCEFRhS5Ii5khitfOXhg==");
   byte[] hash = new HMACSHA1(key).ComputeHash(data);
   return hash;
  }
  internal static bool NapalmIsEnabled(string PluginDirectory)
  {
   string keyPath = Path.Combine(PluginDirectory, "key");
   if (!File.Exists(keyPath))
    return false;
   StreamReader reader = new StreamReader(keyPath);
   string keyline1 = reader.ReadLine();
   string keyline2 = reader.ReadLine();
   string keyline3 = reader.ReadLine();
   reader.Close();
   if ((keyline1 != null) && (keyline2 != null) && (keyline3 != null) && (keyline3.Length > 0))
    return false;
   else
    return true;
  }
  internal static bool NapalmChangeStatus(string PluginDirectory, bool bEnabled)
  {
   string keyPath = Path.Combine(PluginDirectory, "key");
   if (!File.Exists(keyPath))
    return false;
   StreamReader reader = new StreamReader(keyPath);
   string keyline1 = reader.ReadLine();
   string keyline2 = reader.ReadLine();
   reader.Close();
   if ((keyline1 == null) || (keyline2 == null))
    return false;
   StreamWriter writer = new StreamWriter(keyPath);
   writer.WriteLine(keyline1);
   writer.WriteLine(keyline2);
   if (!bEnabled)
   {
    string[] possibleText = new string[] {
               "DisableNapalm",
               "You see; random characters",
               "WARNING: Do not try to read the above lines out loud",
               "\"Sharks with frickin' laser beams attached to their heads!\"",
               "\"Oh, my, yes.\"",
               "\"Windmills do not work that way! Good night!\"",
               "\"I am Holly, the ship's computer, with an IQ of 6000, the same IQ as 6000 PE teachers.\"",
               "\"Spoon!\"",
               "\"Contrary to popular opinion, cats cannot see in the dark. They just know where you are.\""};
    Random rand = new Random();
    string disabledString = possibleText[rand.Next(possibleText.Length-1)];
    writer.WriteLine(disabledString);
   }
   writer.Flush();
   writer.Close();
   return bEnabled;
  }
 }
 class Settings
 {
  internal static bool ShowAllLabels;
  private Settings()
  {
  }
  internal static void LoadSettings(string file)
  {
   try
   {
    XmlDocument xmldoc = new XmlDocument();
    xmldoc.Load(file);
    XmlNode node;
    node = xmldoc.SelectSingleNode("KMLImporter/ShowAllLabels");
    if (node != null)
    {
     ShowAllLabels = System.Convert.ToBoolean(node.InnerText, CultureInfo.InvariantCulture);
    }
   }
   catch (System.IO.IOException)
   { }
   catch (System.Xml.XmlException)
   { }
  }
  internal static void SaveSettings(string file)
  {
   try
   {
    System.Xml.XmlTextWriter xmlwriter = new System.Xml.XmlTextWriter(file, System.Text.Encoding.Default);
    xmlwriter.Formatting = System.Xml.Formatting.Indented;
    xmlwriter.WriteStartDocument();
    xmlwriter.WriteStartElement("KMLImporter");
    xmlwriter.WriteStartElement("ShowAllLabels");
    xmlwriter.WriteString(ShowAllLabels.ToString(CultureInfo.InvariantCulture));
    xmlwriter.WriteEndElement();
    xmlwriter.WriteEndElement();
    xmlwriter.WriteEndDocument();
    xmlwriter.Flush();
    xmlwriter.Close();
   }
   catch (System.IO.IOException)
   { }
   catch (System.Xml.XmlException)
   { }
  }
 }
 class NetworkLink
 {
  private string url;
  private RIcons layer;
  private System.Timers.Timer tickTimer = new System.Timers.Timer();
  private System.Timers.Timer viewTimer = new System.Timers.Timer();
  private KMLImporter owner;
  private bool bUpdating = false;
  bool m_firedStartup = false;
  private Matrix lastView = Matrix.Identity;
  private bool bViewStopped = false;
  internal NetworkLink(KMLImporter owner, RIcons layer, string url, int tickTime, int viewTime)
  {
   this.owner = owner;
   this.url = url;
   this.layer = layer;
   if (tickTime > 0)
   {
    tickTimer.Interval = (double)tickTime;
    tickTimer.Elapsed += new System.Timers.ElapsedEventHandler(timer_Elapsed);
    tickTimer.Start();
   }
   if (viewTime > 0)
   {
    viewTimer.Interval = (double)viewTime;
    viewTimer.Elapsed += new System.Timers.ElapsedEventHandler(timer_Elapsed);
    viewTimer.Start();
   }
  }
  private static string GetBBox()
  {
   CultureInfo ic = CultureInfo.InvariantCulture;
   Angle lat = DrawArgs.Camera.Latitude;
   Angle lon = DrawArgs.Camera.Longitude;
   Angle vr = DrawArgs.Camera.ViewRange;
   Angle North = lat + (0.5 * vr);
   Angle South = lat - (0.5 * vr);
   Angle East = lon + (0.5 * vr);
   Angle West = lon - (0.5 * vr);
   return "BBOX=" + West.Degrees.ToString(ic) +" "+ South.Degrees.ToString(ic) +" "+ East.Degrees.ToString(ic) +" "+ North.Degrees.ToString(ic);
  }
  internal void Fire()
  {
   if (viewTimer.Enabled)
    timer_Elapsed(viewTimer, null);
   else
    timer_Elapsed(null, null);
  }
  private void timer_Elapsed(object sender, System.Timers.ElapsedEventArgs e)
  {
   if (bUpdating)
    return;
   bUpdating = true;
   try
   {
    if (!m_firedStartup || (layer != null && layer.IsOn))
    {
     string fullurl = url;
     if (sender == viewTimer)
     {
      WorldWind.DrawArgs drawArgs = owner.ParentApplication.WorldWindow.DrawArgs;
      if (!bViewStopped)
      {
       if (drawArgs.WorldCamera.ViewMatrix != lastView)
       {
        lastView = drawArgs.WorldCamera.ViewMatrix;
        bUpdating = false;
        return;
       }
       bViewStopped = true;
      }
      else
      {
       if (drawArgs.WorldCamera.ViewMatrix != lastView)
       {
        lastView = drawArgs.WorldCamera.ViewMatrix;
        bViewStopped = false;
       }
       bUpdating = false;
       return;
      }
      fullurl += (fullurl.IndexOf('?') == -1 ? "?" : "&") + GetBBox();
     }
     string saveFile = Path.GetFileName(Uri.EscapeDataString(url));
     if(saveFile == null || saveFile.Length == 0)
      saveFile = "temp.kml";
     saveFile = Path.Combine(owner.PluginDirectory + "\\kml\\temp\\",saveFile);
                    FileInfo saveFileInfo = new FileInfo(saveFile);
                    if (!saveFileInfo.Directory.Exists)
                        saveFileInfo.Directory.Create();
     WebDownload myClient = new WebDownload(fullurl);
     myClient.DownloadFile(saveFile);
     string kmlFile = saveFile;
     if (Path.GetExtension(saveFile) == ".kmz")
     {
      bool bError = false;
      kmlFile = owner.ExtractKMZ(saveFile, out bError);
      if (bError)
      {
       return;
      }
     }
     owner.KMLPath = kmlFile;
     StreamReader sr = new StreamReader(kmlFile);
     string kml = sr.ReadToEnd();
     sr.Close();
     try
     {
      owner.LoadKML(kml, layer);
     }
     catch (Exception ex)
     {
                        Log.Write(Log.Levels.Error, "KMLImporter: " + ex.ToString());
      MessageBox.Show(
       String.Format(CultureInfo.InvariantCulture, "Error loading KML file '{0}':\n\n{1}", kmlFile, ex.ToString()),
       "KMLImporter error",
       MessageBoxButtons.OK,
       MessageBoxIcon.Error,
       MessageBoxDefaultButton.Button1,
       MessageBoxOptions.ServiceNotification);
     }
     m_firedStartup = true;
    }
   }
   catch (Exception ex)
   {
                Log.Write(Log.Levels.Error, "KMLImporter: " + ex.ToString());
   }
   bUpdating = false;
  }
  internal void Dispose()
  {
   tickTimer.Stop();
   viewTimer.Stop();
  }
 }
 class Style
 {
  public BalloonStyle BalloonStyle = null;
  public IconStyle IconStyle = null;
  public LabelStyle LabelStyle = null;
  public LineStyle LineStyle = null;
  public PolyStyle PolyStyle = null;
  internal string NormalIcon
  {
   get
   {
    return normalIcon;
   }
   set
   {
    normalIcon = value;
   }
  }
  internal double NormalScale
  {
   get
   {
    return normalScale;
   }
   set
   {
    normalScale = value;
   }
  }
  private string normalIcon;
  private double normalScale = Double.NaN;
  internal Style()
  {
   this.normalIcon = null;
  }
  internal Style(string normalIcon)
  {
   this.normalIcon = normalIcon;
  }
  public static Color HexToColor(string hexColor)
  {
   string hc = ExtractHexDigits(hexColor);
   if (hc.Length != 8)
   {
    return Color.White;
   }
   string a = hc.Substring(0, 2);
   string r = hc.Substring(2, 2);
   string g = hc.Substring(4, 2);
   string b = hc.Substring(6, 2);
   Color color = Color.White;
   try
   {
    int ai
     = Int32.Parse(a, System.Globalization.NumberStyles.HexNumber);
    int ri
     = Int32.Parse(r, System.Globalization.NumberStyles.HexNumber);
    int gi
     = Int32.Parse(g, System.Globalization.NumberStyles.HexNumber);
    int bi
     = Int32.Parse(b, System.Globalization.NumberStyles.HexNumber);
    color = Color.FromArgb(ri, gi, bi);
   }
   catch
   {
    return Color.White;
   }
   return color;
  }
  private static string ExtractHexDigits(string input)
  {
   Regex isHexDigit
    = new Regex("[abcdefABCDEF\\d]+", RegexOptions.Compiled);
   string newnum = "";
   foreach (char c in input)
   {
    if (isHexDigit.IsMatch(c.ToString()))
     newnum += c.ToString();
   }
   return newnum;
  }
 }
 class LLA
 {
  internal float lat;
  internal float lon;
  internal float alt;
  public LLA(float lat, float lon, float alt)
  {
   this.lat = lat;
   this.lon = lon;
   this.alt = alt;
  }
 }
 class BalloonStyle
 {
  public string id = null;
  public TextElement Text = null;
  public ColorElement TextColor = null;
  public ColorElement Color = null;
 }
 class IconStyle
 {
  public string id = null;
  public ColorElement Color = new ColorElement(System.Drawing.Color.White.ToArgb());
  public ColorMode ColorMode = ColorMode.Normal;
  public DecimalElement Heading = null;
  public DecimalElement Scale = new DecimalElement(1.0);
  public IconElement Icon = null;
  public IconStyle(IconElement icon)
  {
   Icon = icon;
  }
 }
 class LabelStyle
 {
  public string id = null;
  public ColorElement Color = new ColorElement(System.Drawing.Color.White.ToArgb());
  public ColorMode ColorMode = ColorMode.Normal;
  public DecimalElement Scale = new DecimalElement(1.0);
 }
 class LineStyle
 {
  public string id = null;
  public ColorElement Color = new ColorElement(System.Drawing.Color.Gray.ToArgb());
  public ColorMode ColorMode = ColorMode.Normal;
  public DecimalElement Width = new DecimalElement(1);
 }
 class PolyStyle
 {
  public string id = null;
  public ColorElement Color = new ColorElement(System.Drawing.Color.DarkGray.ToArgb());
  public ColorMode ColorMode = ColorMode.Normal;
  public bool Fill = true;
  public bool Outline = true;
 }
 class IconElement
 {
  public string href = null;
  public IntegerElement x = null;
  public IntegerElement y = null;
  public IntegerElement w = null;
  public IntegerElement h = null;
 }
 class TextElement
 {
  public string Text = null;
  public ColorElement TextColor = null;
 }
 class ColorElement
 {
  public int Color = System.Drawing.Color.Black.ToArgb();
  public ColorElement(int color)
  {
   Color = color;
  }
 }
 class IntegerElement
 {
  public int Value = 0;
  public IntegerElement(int v)
  {
   Value = v;
  }
 }
 class DecimalElement
 {
  public double Value = 0;
  public DecimalElement(double d)
  {
   Value = d;
  }
 }
 enum ColorMode
 {
  Normal,
  Random
 }
 class RIcons : Icons
 {
  private new Hashtable m_textures = new Hashtable();
  private new Sprite m_sprite;
  private Line m_groundStick;
  private Vector2[] groundStick = new Vector2[2];
  private new RIcon mouseOverIcon;
  private const int minIconZoomAltitude = 5000000;
  private static int hotColor = Color.White.ToArgb();
  private static int normalColor = Color.FromArgb(150,255,255,255).ToArgb();
  private static int descriptionColor = Color.White.ToArgb();
  private ArrayList labelRectangles = new ArrayList();
        private Form mainForm = MainApplication.ActiveForm;
  internal RIcons(string name) : base(name)
  {
   this.MetaData.Add("Child count", 0);
  }
  internal void AddIcon(RIcon icon)
  {
   icon.ParentList = this;
   Add(icon);
  }
  protected virtual void Render(DrawArgs drawArgs, RIcon icon, Vector3 projectedPoint)
  {
   if (!icon.isInitialized)
    icon.Initialize(drawArgs);
   if(!drawArgs.WorldCamera.ViewFrustum.ContainsPoint(icon.Position))
    return;
   Vector3 referenceCenter = new Vector3(
    (float)drawArgs.WorldCamera.ReferenceCenter.X,
    (float)drawArgs.WorldCamera.ReferenceCenter.Y,
    (float)drawArgs.WorldCamera.ReferenceCenter.Z);
   IconTexture iconTexture = GetTexture(icon);
   bool isMouseOver = icon == mouseOverIcon;
   if(isMouseOver)
   {
    isMouseOver = true;
    if(icon.isSelectable)
     DrawArgs.MouseCursor = CursorType.Hand;
    string description = icon.Description;
   }
   int color = isMouseOver ? hotColor : normalColor;
   if(iconTexture==null || isMouseOver || Settings.ShowAllLabels)
   {
    if(icon.Name != null)
    {
     const int labelWidth = 1000;
     if(iconTexture==null)
     {
      Rectangle realrect = drawArgs.defaultDrawingFont.MeasureString(m_sprite, icon.Name, DrawTextFormat.WordBreak, color);
      realrect.X = (int)projectedPoint.X - (labelWidth>>1);
      realrect.Y = (int)(projectedPoint.Y - (drawArgs.defaultDrawingFont.Description.Height >> 1));
      bool bDraw = true;
      foreach (Rectangle drawnrect in labelRectangles)
      {
       if (realrect.IntersectsWith(drawnrect))
       {
        bDraw = false;
        break;
       }
      }
      if (bDraw)
      {
       labelRectangles.Add(realrect);
       drawArgs.defaultDrawingFont.DrawText(m_sprite, icon.Name, realrect, DrawTextFormat.Center, color);
      }
     }
     else
     {
      int spacing = (int)(icon.Width * 0.3f);
      if(spacing>10)
       spacing = 10;
      int offsetForIcon = (icon.Width>>1) + spacing;
      Rectangle rightrect = drawArgs.defaultDrawingFont.MeasureString(m_sprite, icon.Name, DrawTextFormat.WordBreak, color);
      rightrect.X = (int)projectedPoint.X + offsetForIcon;
      rightrect.Y = (int)(projectedPoint.Y - (drawArgs.defaultDrawingFont.Description.Height >> 1));
      Rectangle leftrect = drawArgs.defaultDrawingFont.MeasureString(m_sprite, icon.Name, DrawTextFormat.WordBreak, color);
      leftrect.X = (int)projectedPoint.X - offsetForIcon - rightrect.Width;
      leftrect.Y = (int)(projectedPoint.Y - (drawArgs.defaultDrawingFont.Description.Height >> 1));
      bool bDrawRight = true;
      bool bDrawLeft = true;
      foreach (Rectangle drawnrect in labelRectangles)
      {
       if (rightrect.IntersectsWith(drawnrect))
       {
        bDrawRight = false;
       }
       if (leftrect.IntersectsWith(drawnrect))
       {
        bDrawLeft = false;
       }
       if (!bDrawRight && !bDrawLeft)
       {
        break;
       }
      }
      if (bDrawRight)
      {
       labelRectangles.Add(rightrect);
       drawArgs.defaultDrawingFont.DrawText(m_sprite, icon.Name, rightrect, DrawTextFormat.WordBreak, color);
      }
      else if (bDrawLeft)
      {
       labelRectangles.Add(leftrect);
       drawArgs.defaultDrawingFont.DrawText(m_sprite, icon.Name, leftrect, DrawTextFormat.WordBreak, color);
      }
     }
    }
   }
   if (icon.m_drawGroundStick)
   {
    Vector3 projectedGroundPoint = drawArgs.WorldCamera.Project(icon.m_groundPoint - referenceCenter);
    m_groundStick.Begin();
    groundStick[0].X = projectedPoint.X;
    groundStick[0].Y = projectedPoint.Y;
    groundStick[1].X = projectedGroundPoint.X;
    groundStick[1].Y = projectedGroundPoint.Y;
    m_groundStick.Draw(groundStick, Color.Red.ToArgb());
    m_groundStick.End();
   }
   if(iconTexture!=null)
   {
    float factor = 1;
    if (drawArgs.WorldCamera.Altitude > minIconZoomAltitude)
     factor -= (float)((drawArgs.WorldCamera.Altitude - minIconZoomAltitude) / drawArgs.WorldCamera.Altitude);
    float xscale = factor * ((float)icon.Width / iconTexture.Width);
    float yscale = factor * ((float)icon.Height / iconTexture.Height);
    m_sprite.Transform = Matrix.Scaling(xscale, yscale, 0);
    if (icon.IsRotated) m_sprite.Transform *= Matrix.RotationZ((float)icon.Rotation.Radians - (float) drawArgs.WorldCamera.Heading.Radians);
    m_sprite.Transform *= Matrix.Translation(projectedPoint.X, projectedPoint.Y, 0);
    m_sprite.Draw( iconTexture.Texture,
     new Vector3(iconTexture.Width>>1, iconTexture.Height>>1,0),
     Vector3.Empty,
     color);
    m_sprite.Transform = Matrix.Identity;
   }
  }
  private IconTexture GetTexture(RIcon icon)
  {
   object key = icon.Image;
   if(key==null)
    return null;
   IconTexture res = (IconTexture)m_textures[key];
   return res;
  }
  public override void Add(RenderableObject ro)
  {
   ro.ParentList = this;
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
   if(m_groundStick != null)
   {
    m_groundStick.Dispose();
    m_groundStick = null;
   }
   m_sprite = new Sprite(drawArgs.device);
   m_groundStick = new Line(drawArgs.device);
   foreach(RenderableObject ro in m_children)
   {
    RIcon icon = ro as RIcon;
    if(icon==null)
    {
     if(ro.IsOn)
      ro.Initialize(drawArgs);
     continue;
    }
    icon.Initialize(drawArgs);
    object key = null;
    IconTexture iconTexture = null;
    if(icon.Image != null)
    {
     iconTexture = (IconTexture)m_textures[icon.Image];
     if(iconTexture==null)
     {
      key = icon.Image;
      iconTexture = new IconTexture( drawArgs.device, icon.Image);
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
    RIcon icon = ro as RIcon;
    if(icon==null)
     continue;
    if(GetTexture(icon)==null)
    {
     icon.SelectionRectangle = drawArgs.defaultDrawingFont.MeasureString(null, icon.Name, DrawTextFormat.None, 0);
    }
    else
    {
     icon.SelectionRectangle = new Rectangle(0, 0, icon.Width, icon.Height);
    }
    icon.SelectionRectangle.Offset(-icon.SelectionRectangle.Width/2, -icon.SelectionRectangle.Height/2 );
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
   if (m_groundStick != null)
   {
    m_groundStick.Dispose();
    m_groundStick = null;
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
    RIcon icon = ro as RIcon;
    if(icon==null)
    {
     if (ro.PerformSelectionAction(drawArgs))
      return true;
     continue;
    }
    if(!drawArgs.WorldCamera.ViewFrustum.ContainsPoint(icon.Position))
     continue;
    Vector3 projectedPoint = drawArgs.WorldCamera.Project(icon.Position);
                if (!icon.SelectionRectangle.Contains(
                    (DrawArgs.LastMousePosition.X - (int)projectedPoint.X) / 5,
                    (DrawArgs.LastMousePosition.Y - (int)projectedPoint.Y) / 5)){
                    continue;
                }
    try
    {
                    foreach (RIcon r in m_children)
                    {
                        if (r.DescriptionBubble != null)
                        {
                            r.IsDescriptionVisible = false;
                            r.DescriptionBubble.isVisible = false;
                            r.DescriptionBubble.Dispose();
                        }
                    }
                    if (icon.DescriptionBubble != null)
                        icon.DescriptionBubble.Dispose();
                    icon.DescriptionBubble = new KMLDialog();
                    icon.DescriptionBubble.Owner = mainForm;
                    if (icon.IsDescriptionVisible == false)
                    {
                        icon.IsDescriptionVisible = true;
                    }
                    else
                    {
                        icon.DescriptionBubble.Dispose();
                        icon.IsDescriptionVisible = false;
                    }
     return true;
    }
                catch (Exception)
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
   Vector3 referenceCenter = new Vector3(
    (float)drawArgs.WorldCamera.ReferenceCenter.X,
    (float)drawArgs.WorldCamera.ReferenceCenter.Y,
    (float)drawArgs.WorldCamera.ReferenceCenter.Z);
   foreach(RenderableObject ro in m_children)
   {
    if(ro is RIcon)
     continue;
    if(!ro.IsOn)
     continue;
    ro.Render(drawArgs);
   }
   labelRectangles.Clear();
   int closestIconDistanceSquared = int.MaxValue;
   RIcon closestIcon = null;
   m_sprite.Begin(SpriteFlags.AlphaBlend);
   foreach(RenderableObject ro in m_children)
   {
    if(!ro.IsOn)
     continue;
    RIcon icon = ro as RIcon;
    if(icon == null)
     continue;
    Vector3 projectedPoint = drawArgs.WorldCamera.Project(icon.Position - referenceCenter);
                if (icon.IsDescriptionVisible == true)
                {
                    if (!drawArgs.WorldCamera.ViewFrustum.ContainsPoint(icon.Position))
                    {
                        icon.DescriptionBubble.Hide();
                        icon.IsDescriptionVisible = false;
                    }
                    else
                    {
                        if (icon.DescriptionBubble.isVisible == true)
                        {
                            icon.DescriptionBubble.Location = new Point((int)projectedPoint.X + (icon.Width/4), (int)projectedPoint.Y);
                            icon.DescriptionBubble.Show();
                            if(icon.DescriptionBubble.HTMLIsSet == false)
                                icon.DescriptionBubble.SetHTML(icon.Description);
                            icon.DescriptionBubble.BringToFront();
                        }
                    }
                }
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
   labelRectangles.Clear();
   if(mouseOverIcon != null)
    Render(drawArgs, mouseOverIcon, drawArgs.WorldCamera.Project(mouseOverIcon.Position - referenceCenter));
   mouseOverIcon = closestIcon;
   m_sprite.End();
  }
 }
 class RIcon : RenderableObject
 {
  internal new string Description;
  internal Bitmap Image;
  internal int Width;
  internal int Height;
  internal string ClickableActionURL
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
  internal Rectangle SelectionRectangle;
  internal Angle Rotation
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
  internal bool IsRotated
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
  internal bool m_drawGroundStick = true;
  internal Vector3 m_groundPoint = new Vector3();
  internal string NormalIcon;
  internal bool HasBeenUpdated = true;
        internal KMLDialog DescriptionBubble;
        internal bool IsDescriptionVisible = false;
  private Angle m_rotation;
  private bool m_isRotated;
  private string m_clickableActionURL;
  private float m_latitude;
  private float m_longitude;
  private float m_altitude;
  Matrix lastView = Matrix.Identity;
  internal RIcon(string name, float latitude, float longitude, string normalicon, float heightAboveSurface) : base( name )
  {
   m_latitude = latitude;
   m_longitude = longitude;
   NormalIcon = normalicon;
   this.m_altitude = heightAboveSurface;
  }
  internal void SetPosition(float latitude, float longitude, float altitude)
  {
   m_latitude = latitude;
   m_longitude = longitude;
   this.m_altitude = altitude;
   isInitialized = false;
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   float samplesPerDegree = 50.0f / ((float)drawArgs.WorldCamera.ViewRange.Degrees);
   float elevation = (float)drawArgs.CurrentWorld.TerrainAccessor.GetElevationAt(m_latitude, m_longitude, samplesPerDegree);
   float altitude = (float)(World.Settings.VerticalExaggeration * m_altitude + World.Settings.VerticalExaggeration * elevation);
   Position = MathEngine.SphericalToCartesian(m_latitude, m_longitude,
    altitude + drawArgs.WorldCamera.WorldRadius);
   if (m_drawGroundStick)
   {
    double gselevation = drawArgs.WorldCamera.WorldRadius;
    if((((WorldWindow)drawArgs.parentControl).CurrentWorld.TerrainAccessor != null) && (drawArgs.WorldCamera.Altitude < 300000))
    {
     gselevation += elevation * World.Settings.VerticalExaggeration;
    }
    m_groundPoint = MathEngine.SphericalToCartesian(m_latitude, m_longitude, gselevation);
   }
   isInitialized = true;
  }
  public override void Dispose()
  {
            if (this.DescriptionBubble != null)
                this.DescriptionBubble.Dispose();
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   return false;
  }
  public override void Update(DrawArgs drawArgs)
  {
   if(drawArgs.WorldCamera.ViewMatrix != lastView && drawArgs.CurrentWorld.TerrainAccessor != null && drawArgs.WorldCamera.Altitude < 300000)
   {
    float samplesPerDegree = 50.0f / ((float)drawArgs.WorldCamera.ViewRange.Degrees);
    float elevation = (float)drawArgs.CurrentWorld.TerrainAccessor.GetElevationAt(m_latitude, m_longitude, samplesPerDegree);
    float altitude = (float)(World.Settings.VerticalExaggeration * m_altitude + World.Settings.VerticalExaggeration * elevation);
    Position = MathEngine.SphericalToCartesian(m_latitude, m_longitude,
     altitude + drawArgs.WorldCamera.WorldRadius);
    lastView = drawArgs.WorldCamera.ViewMatrix;
   }
  }
  public override void Render(DrawArgs drawArgs)
  {
  }
 }
 class WaitMessage : RenderableObject
 {
  private string _Text = "Please wait, loading KML file.";
  private int color = Color.White.ToArgb();
  private int distanceFromCorner = 25;
  internal WaitMessage() : base("KML WaitMessage", Vector3.Empty, Quaternion.Identity)
  {
   this.RenderPriority = RenderPriority.Icons;
   this.IsOn = true;
  }
  public override void Render(DrawArgs drawArgs)
  {
   Rectangle bounds = drawArgs.defaultDrawingFont.MeasureString(null, _Text, DrawTextFormat.None, 0);
   drawArgs.defaultDrawingFont.DrawText(null, _Text,
    drawArgs.screenWidth-bounds.Width-distanceFromCorner, drawArgs.screenHeight-bounds.Height-distanceFromCorner,
    color );
  }
  public override void Initialize(DrawArgs drawArgs)
  {
  }
  public override void Update(DrawArgs drawArgs)
  {
  }
  public override void Dispose()
  {
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   return false;
  }
 }
 class AboutForm : System.Windows.Forms.Form
 {
  private System.Windows.Forms.Label label1;
  private System.Windows.Forms.Label label2;
  private System.Windows.Forms.LinkLabel linkLabel1;
  private System.Windows.Forms.Button button1;
  private System.Windows.Forms.Label label3;
  private System.ComponentModel.Container components = null;
  internal AboutForm()
  {
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
  private void button1_Click(object sender, EventArgs e)
  {
   this.Close();
  }
  private void linkLabel1_Click(object sender, EventArgs e)
  {
   try
   {
    System.Diagnostics.Process.Start("http://shockfire.blogspot.com/");
   }
   catch (Exception) {}
  }
  private void InitializeComponent()
  {
   this.label1 = new System.Windows.Forms.Label();
   this.label2 = new System.Windows.Forms.Label();
   this.linkLabel1 = new System.Windows.Forms.LinkLabel();
   this.button1 = new System.Windows.Forms.Button();
   this.label3 = new System.Windows.Forms.Label();
   this.SuspendLayout();
   this.label1.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
   this.label1.Location = new System.Drawing.Point(120, 8);
   this.label1.Name = "label1";
   this.label1.Size = new System.Drawing.Size(160, 48);
   this.label1.TabIndex = 0;
   this.label1.Text = "KMLImporter";
   this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
   this.label2.Location = new System.Drawing.Point(96, 144);
   this.label2.Name = "label2";
   this.label2.Size = new System.Drawing.Size(224, 24);
   this.label2.TabIndex = 1;
   this.label2.Text = "Created by Tim van den Hamer (ShockFire)";
   this.label2.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
   this.linkLabel1.Location = new System.Drawing.Point(96, 176);
   this.linkLabel1.Name = "linkLabel1";
   this.linkLabel1.Size = new System.Drawing.Size(224, 16);
   this.linkLabel1.TabIndex = 2;
   this.linkLabel1.TabStop = true;
   this.linkLabel1.Text = "http://shockfire.blogspot.com/";
   this.linkLabel1.Click += new EventHandler(linkLabel1_Click);
   this.linkLabel1.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
   this.button1.Location = new System.Drawing.Point(168, 216);
   this.button1.Name = "button1";
   this.button1.TabIndex = 3;
   this.button1.Text = "OK";
   this.button1.Click += new EventHandler(button1_Click);
   this.label3.Location = new System.Drawing.Point(96, 64);
   this.label3.Name = "label3";
   this.label3.Size = new System.Drawing.Size(224, 64);
   this.label3.TabIndex = 4;
   this.label3.Text = "KMLImporter - A placemark importer for NASA World Wind allowing easy importing of kml/kmz files.";
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.ClientSize = new System.Drawing.Size(416, 265);
   this.ControlBox = false;
   this.Controls.Add(this.label3);
   this.Controls.Add(this.button1);
   this.Controls.Add(this.linkLabel1);
   this.Controls.Add(this.label2);
   this.Controls.Add(this.label1);
   this.ShowInTaskbar = false;
   this.MaximizeBox = false;
   this.MinimizeBox = false;
   this.Name = "AboutForm";
   this.Text = "About KMLImporter";
   this.ResumeLayout(false);
  }
 }
}
