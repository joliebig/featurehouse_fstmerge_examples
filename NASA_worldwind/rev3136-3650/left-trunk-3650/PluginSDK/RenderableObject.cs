using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.IO;
using System.Windows.Forms;
using System.Xml;
using Microsoft.DirectX;
using WorldWind.Menu;
using WorldWind.VisualControl;
namespace WorldWind.Renderable
{
 public abstract class RenderableObject : IRenderable, IComparable
 {
  public bool isInitialized;
  public bool isSelectable;
  public RenderableObjectList ParentList;
  public string dbfPath = "";
  public bool dbfIsInZip = false;
  protected string name;
  protected string m_description = null;
  protected Hashtable _metaData = new Hashtable();
  protected Vector3 position;
  protected Quaternion orientation;
  protected bool isOn = true;
  protected byte m_opacity = 255;
  protected RenderPriority m_renderPriority = RenderPriority.SurfaceImages;
  protected Form m_propertyBrowser;
  protected Image m_thumbnailImage;
  protected string m_iconImagePath;
  protected Image m_iconImage;
  protected World m_world;
  string m_thumbnail;
  public string Description
  {
   get{ return m_description; }
   set{ m_description = value; }
  }
  protected RenderableObject(string name)
  {
   this.name = name;
  }
  protected RenderableObject(string name, World parentWorld)
  {
   this.name = name;
   this.m_world = parentWorld;
  }
  protected RenderableObject(string name, Vector3 position, Quaternion orientation)
  {
   this.name = name;
   this.position = position;
   this.orientation = orientation;
  }
  public abstract void Initialize(DrawArgs drawArgs);
  public abstract void Update(DrawArgs drawArgs);
  public abstract void Render(DrawArgs drawArgs);
  public virtual bool Initialized
  {
   get
   {
    return isInitialized;
   }
  }
  [TypeConverter(typeof(ExpandableObjectConverter))]
  public virtual World World
  {
   get
   {
    return m_world;
   }
  }
  public virtual string Thumbnail
  {
   get
   {
    return m_thumbnail;
   }
   set
   {
    m_thumbnail = ImageHelper.FindResource(value);
   }
  }
  public virtual Image ThumbnailImage
  {
   get
   {
    if(m_thumbnailImage==null)
    {
     if(m_thumbnail==null)
      return null;
     try
     {
      if(File.Exists(m_thumbnail))
       m_thumbnailImage = ImageHelper.LoadImage(m_thumbnail);
     }
     catch {}
    }
    return m_thumbnailImage;
   }
  }
  public string IconImagePath
  {
   get
   {
    return m_iconImagePath;
   }
   set
   {
    m_iconImagePath = value;
   }
  }
  public Image IconImage
  {
   get
   {
    if(m_iconImage==null)
    {
     if(m_iconImagePath==null)
      return null;
     try
     {
      if(File.Exists(m_iconImagePath))
       m_iconImage = ImageHelper.LoadImage(m_iconImagePath);
     }
     catch {}
    }
    return m_iconImage;
   }
  }
  public abstract void Dispose();
  public abstract bool PerformSelectionAction(DrawArgs drawArgs);
  public int CompareTo(object obj)
  {
   RenderableObject robj = obj as RenderableObject;
   if(obj == null)
    return 1;
   return this.m_renderPriority.CompareTo(robj.RenderPriority);
  }
  public virtual void Delete()
  {
   RenderableObjectList list = this.ParentList;
   string xmlConfigFile = (string)this.MetaData["XmlSource"];
   if (this.ParentList.Name == "Earth" & xmlConfigFile != null)
   {
    string message = "Permanently delete layer '" + this.Name + "' and rename its .xml config file to .bak?";
    if (DialogResult.Yes != MessageBox.Show(message, "Delete layer", MessageBoxButtons.YesNo, MessageBoxIcon.Warning,
     MessageBoxDefaultButton.Button2))
     return;
    if (xmlConfigFile.Contains("http"))
     throw new Exception("Can't delete network layers.");
    if (File.Exists(xmlConfigFile.Replace(".xml", ".bak")))
    {
     File.Delete(xmlConfigFile.Replace(".xml", ".bak"));
    }
    File.Move(xmlConfigFile, xmlConfigFile.Replace(".xml", ".bak"));
    this.ParentList.Remove(this);
   }
   else if (xmlConfigFile == null)
   {
    string message = "Delete plugin layer '" + this.Name + "'?\n\nThis may cause problems for a running plugin that expects the layer to be\nthere.  Restart the plugin in question to replace the layer after deleting.";
    if (DialogResult.Yes != MessageBox.Show(message, "Delete layer", MessageBoxButtons.YesNo, MessageBoxIcon.Warning,
     MessageBoxDefaultButton.Button2))
     return;
    this.ParentList.Remove(this);
   }
   else
   {
    throw new Exception("Can't delete this sub-item from the layer manager.  Try deleting the top-level entry for this layer.");
   }
  }
  public virtual void BuildContextMenu( ContextMenu menu )
  {
   menu.MenuItems.Add("Goto", new EventHandler(OnGotoClick));
   menu.MenuItems.Add("Properties...", new EventHandler(OnPropertiesClick));
   menu.MenuItems.Add("Info...", new EventHandler(OnInfoClick));
   menu.MenuItems.Add("Delete...", new EventHandler(OnDeleteClick));
   if(dbfPath != "")
    menu.MenuItems.Add("Dbf Info", new EventHandler(OnDbfInfo));
  }
  public override string ToString()
  {
   return name;
  }
  [Description("The object's render priority determining in what order it will be rendered compared to the other objects.")]
  public virtual RenderPriority RenderPriority
  {
   get
   {
    return this.m_renderPriority;
   }
   set
   {
    this.m_renderPriority = value;
    if(ParentList != null)
     ParentList.SortChildren();
   }
  }
  [Description("Controls the amount of light allowed to pass through this object. (0=invisible, 255=opaque).")]
  public virtual byte Opacity
  {
   get
   {
    return this.m_opacity;
   }
   set
   {
    this.m_opacity = value;
    if(value == 0)
    {
     if(this.isOn)
      this.IsOn = false;
    }
    else
    {
     if(!this.isOn)
      this.IsOn = true;
    }
   }
  }
  [Browsable(false)]
  public virtual Hashtable MetaData
  {
   get
   {
    return this._metaData;
   }
  }
  [Description("This layer's enabled status.")]
  public virtual bool IsOn
  {
   get
   {
    return this.isOn;
   }
   set
   {
    if(isOn && !value)
     this.Dispose();
    this.isOn = value;
   }
  }
  [Description("This layer's name.")]
  public virtual string Name
  {
   get
   {
    return this.name;
   }
   set
   {
    this.name = value;
   }
  }
  [Browsable(false)]
  public virtual Vector3 Position
  {
   get
   {
    return this.position;
   }
   set
   {
    this.position = value;
   }
  }
  [Browsable(false)]
  public virtual Quaternion Orientation
  {
   get
   {
    return this.orientation;
   }
   set
   {
    this.orientation = value;
   }
  }
  protected virtual void OnDbfInfo(object sender, EventArgs e)
  {
   ShapeFileInfoDlg sfid = new ShapeFileInfoDlg(dbfPath, dbfIsInZip);
   sfid.Show();
  }
  protected virtual void OnGotoClick(object sender, EventArgs e)
  {
   lock(this.ParentList.ChildObjects.SyncRoot)
   {
    for(int i = 0; i < this.ParentList.ChildObjects.Count; i++)
    {
     RenderableObject ro = (RenderableObject)this.ParentList.ChildObjects[i];
     if(ro.Name.Equals(name))
     {
                        if (ro is QuadTileSet)
                        {
                            QuadTileSet qts = (QuadTileSet)ro;
                            DrawArgs.Camera.SetPosition((qts.North + qts.South) / 2, (qts.East + qts.West) / 2);
                            double perpendicularViewRange = (qts.North - qts.South > qts.East - qts.West ? qts.North - qts.South : qts.East - qts.West);
                            double altitude = qts.LayerRadius * Math.Sin(MathEngine.DegreesToRadians(perpendicularViewRange * 0.5));
                            DrawArgs.Camera.Altitude = altitude;
                            break;
                        }
                        if (ro is Icon)
      {
       Icon ico = (Icon)ro;
       DrawArgs.Camera.SetPosition(ico.Latitude,ico.Longitude);
       DrawArgs.Camera.Altitude/=2;
       break;
      }
                        if (ro is ShapeFileLayer)
                        {
                            ShapeFileLayer slayer = (ShapeFileLayer)ro;
                            DrawArgs.Camera.SetPosition((slayer.North + slayer.South) / 2, (slayer.East + slayer.West) / 2);
                            double perpendicularViewRange = (slayer.North - slayer.South > slayer.East - slayer.West ? slayer.North - slayer.South : slayer.East - slayer.West);
                            double altitude = slayer.MaxAltitude;
                            DrawArgs.Camera.Altitude = altitude;
                            break;
                        }
     }
    }
   }
  }
  protected virtual void OnInfoClick(object sender, EventArgs e)
  {
   LayerManagerItemInfo lmii = new LayerManagerItemInfo(MetaData);
   lmii.ShowDialog();
  }
  protected virtual void OnPropertiesClick(object sender, EventArgs e)
  {
   if(m_propertyBrowser!=null)
    m_propertyBrowser.Dispose();
   m_propertyBrowser = new PropertyBrowser(this);
   m_propertyBrowser.Show();
  }
  protected virtual void OnDeleteClick(object sender, EventArgs e)
  {
   try
   {
    this.Delete();
   }
   catch (Exception ex)
   {
    MessageBox.Show(ex.Message,"Layer Delete");
   }
  }
 }
 public enum RenderPriority
 {
  SurfaceImages = 0,
  TerrainMappedImages = 100,
  AtmosphericImages = 200,
  LinePaths = 300,
  Icons = 400,
  Placenames = 500,
  Custom = 600
 }
}
