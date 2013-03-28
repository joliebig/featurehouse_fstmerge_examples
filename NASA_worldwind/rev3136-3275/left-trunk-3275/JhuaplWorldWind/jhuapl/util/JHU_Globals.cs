using System;
using System.Collections;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Renderable;
namespace jhuapl.util
{
 public class JHU_Globals
 {
  protected static bool m_initialized;
  protected static JHU_Globals m_instance;
  public static JHU_Globals getInstance()
  {
   lock (typeof(JHU_Globals))
   {
    if (m_instance == null)
    {
     m_instance = new JHU_Globals();
    }
   }
   return m_instance;
  }
  protected static WorldWindow m_worldWindow;
  public WorldWindow WorldWindow
  {
   get { return m_worldWindow; }
  }
  protected static Hashtable m_textures;
  public Hashtable Textures
  {
   get { return m_textures; }
  }
  protected static JHU_RootWidget m_rootWidget;
  public JHU_RootWidget RootWidget
  {
   get { return m_rootWidget; }
  }
  protected static JHU_FormWidget m_infoForm;
  public JHU_FormWidget InfoForm
  {
   get { return m_infoForm; }
  }
  protected static JHU_SimpleTreeNodeWidget m_infoTree;
  public JHU_SimpleTreeNodeWidget InfoTree
  {
   get { return m_infoTree; }
  }
  protected static JHU_SimpleTreeNodeWidget m_generalInfoTreeNode;
  protected static JHU_SimpleTreeNodeWidget m_detailedInfoTreeNode;
  protected static JHU_SimpleTreeNodeWidget m_descriptionTreeNode;
  protected static JHU_LabelWidget m_generalInfoLabel;
  public JHU_LabelWidget GeneralInfoLabel
  {
   get { return m_generalInfoLabel; }
  }
  protected static JHU_LabelWidget m_detailedInfoLabel;
  public JHU_LabelWidget DetailedInfoLabel
  {
   get { return m_detailedInfoLabel; }
  }
  protected static JHU_LabelWidget m_descriptionLabel;
  public JHU_LabelWidget DescriptionLabel
  {
   get { return m_descriptionLabel; }
  }
  protected static JHU_FormWidget m_controlForm;
  protected static JHU_ControlWidget m_overviewWidget;
  protected static JHU_CompassWidget m_compassWidget;
  protected static JHU_ButtonWidget m_zoomInWidget;
  protected static JHU_ButtonWidget m_zoomOutWidget;
  protected static JHU_ButtonWidget m_zoomWorldWidget;
  protected static JHU_ButtonWidget m_zoomCountryWidget;
  protected static JHU_ButtonWidget m_zoomStateWidget;
  protected static JHU_ButtonWidget m_zoomCityWidget;
  protected static JHU_ButtonWidget m_zoomBldgWidget;
  protected static JHU_ButtonWidget m_resetWidget;
  public JHU_FormWidget NavigatorForm
  {
   get { return m_controlForm; }
  }
  protected static string m_basePath = ".";
  public string BasePath
  {
   get { return m_basePath; }
   set { m_basePath = value; }
  }
  protected JHU_Globals()
  {
   m_textures = new Hashtable();
   m_initialized = false;
  }
  protected static void Dispose()
  {
   foreach(IconTexture iconTexture in m_textures.Values)
    iconTexture.Texture.Dispose();
   m_textures.Clear();
   m_textures = null;
   m_instance = null;
   m_initialized = false;
  }
  public static void Initialize(WorldWindow worldWindow)
  {
   lock (typeof(JHU_Globals))
   {
    if (m_instance == null)
    {
     m_instance = new JHU_Globals();
    }
    m_worldWindow = worldWindow;
    if (m_rootWidget == null)
    {
     m_rootWidget = new JHU_RootWidget(m_worldWindow);
     m_rootWidget.Visible = true;
     m_rootWidget.Enabled = true;
    }
    if (m_infoForm == null)
    {
     m_infoForm = new JHU_FormWidget("Info");
     m_infoForm.WidgetSize = new System.Drawing.Size(200, 250);
     m_infoForm.Location = new System.Drawing.Point(m_rootWidget.ClientSize.Width - 201, m_rootWidget.ClientSize.Height - 271);
     m_infoForm.Anchor = JHU_Enums.AnchorStyles.Right;
     m_infoTree = new JHU_SimpleTreeNodeWidget("Info");
     m_infoTree.IsRadioButton = true;
     m_infoTree.Expanded = true;
     m_infoTree.EnableCheck = false;
     m_generalInfoLabel = new JHU_LabelWidget("");
     m_generalInfoLabel.ClearOnRender = true;
     m_generalInfoLabel.Format = DrawTextFormat.NoClip | DrawTextFormat.WordBreak ;
     m_generalInfoLabel.Location = new System.Drawing.Point(0, 0);
     m_generalInfoLabel.AutoSize = true;
     m_generalInfoLabel.UseParentWidth = false;
     m_generalInfoTreeNode = new JHU_SimpleTreeNodeWidget("General");
     m_generalInfoTreeNode.IsRadioButton = true;
     m_generalInfoTreeNode.Expanded = true;
     m_generalInfoTreeNode.EnableCheck = false;
     m_generalInfoTreeNode.Add(m_generalInfoLabel);
     m_infoTree.Add(m_generalInfoTreeNode);
     m_detailedInfoTreeNode = new JHU_SimpleTreeNodeWidget("Detailed");
     m_detailedInfoTreeNode.IsRadioButton = true;
     m_detailedInfoTreeNode.Expanded = false;
     m_detailedInfoTreeNode.EnableCheck = false;
     m_detailedInfoLabel = new JHU_LabelWidget("");
     m_detailedInfoLabel.ClearOnRender = true;
     m_detailedInfoLabel.Format = DrawTextFormat.NoClip | DrawTextFormat.WordBreak ;
     m_detailedInfoLabel.Location = new System.Drawing.Point(0, 0);
     m_detailedInfoLabel.AutoSize = true;
     m_detailedInfoLabel.UseParentWidth = false;
     m_detailedInfoTreeNode.Add(m_detailedInfoLabel);
     m_infoTree.Add(m_detailedInfoTreeNode);
     m_descriptionTreeNode = new JHU_SimpleTreeNodeWidget("Description");
     m_descriptionTreeNode.IsRadioButton = true;
     m_descriptionTreeNode.Expanded = false;
     m_descriptionTreeNode.EnableCheck = false;
     m_descriptionLabel = new JHU_LabelWidget("");
     m_descriptionLabel.ClearOnRender = true;
     m_descriptionLabel.Format = DrawTextFormat.NoClip | DrawTextFormat.WordBreak ;
     m_descriptionLabel.Location = new System.Drawing.Point(0, 0);
     m_descriptionLabel.AutoSize = true;
     m_descriptionLabel.UseParentWidth = true;
     m_descriptionTreeNode.Add(m_descriptionLabel);
     m_infoTree.Add(m_descriptionTreeNode);
     m_infoForm.Add(m_infoTree);
     m_rootWidget.Add(m_infoForm);
    }
    if (m_controlForm == null)
    {
     m_controlForm = new JHU_FormWidget("Navigator");
     m_controlForm.Location = new System.Drawing.Point(m_rootWidget.ClientSize.Width - 201, 0);
     m_controlForm.WidgetSize = new System.Drawing.Size(200, 242);
     m_controlForm.HorizontalScrollbarEnabled = false;
     m_controlForm.HorizontalResizeEnabled = false;
     m_controlForm.Anchor = JHU_Enums.AnchorStyles.Right;
     m_overviewWidget = new JHU_ControlWidget();
     m_controlForm.Add(m_overviewWidget);
     m_compassWidget = new JHU_CompassWidget();
     m_controlForm.Add(m_compassWidget);
     m_zoomInWidget = new JHU_ButtonWidget();
     m_zoomInWidget.Location = new System.Drawing.Point(84,100);
     m_zoomInWidget.LeftClickAction = new MouseClickAction(JHU_Globals.PerformZoomIn);
     m_controlForm.Add(m_zoomInWidget);
     m_zoomOutWidget = new JHU_ButtonWidget();
     m_zoomOutWidget.ImageName = "button_out.png";
     m_zoomOutWidget.Location = new System.Drawing.Point(84,137);
     m_zoomOutWidget.LeftClickAction = new MouseClickAction(JHU_Globals.PerformZoomOut);
     m_controlForm.Add(m_zoomOutWidget);
     m_zoomWorldWidget = new JHU_ButtonWidget();
     m_zoomWorldWidget.ImageName = "button_world.png";
     m_zoomWorldWidget.Location = new System.Drawing.Point(10,174);
     m_zoomWorldWidget.CountHeight = true;
     m_zoomWorldWidget.LeftClickAction = new MouseClickAction(JHU_Globals.PerformWorldZoom);
     m_controlForm.Add(m_zoomWorldWidget);
     m_zoomCountryWidget = new JHU_ButtonWidget();
     m_zoomCountryWidget.ImageName = "button_country.png";
     m_zoomCountryWidget.Location = new System.Drawing.Point(47,174);
     m_zoomCountryWidget.LeftClickAction = new MouseClickAction(JHU_Globals.PerformCountryZoom);
     m_controlForm.Add(m_zoomCountryWidget);
     m_zoomStateWidget = new JHU_ButtonWidget();
     m_zoomStateWidget.ImageName = "button_state.png";
     m_zoomStateWidget.Location = new System.Drawing.Point(84,174);
     m_zoomStateWidget.LeftClickAction = new MouseClickAction(JHU_Globals.PerformStateZoom);
     m_controlForm.Add(m_zoomStateWidget);
     m_zoomCityWidget = new JHU_ButtonWidget();
     m_zoomCityWidget.ImageName = "button_city.png";
     m_zoomCityWidget.Location = new System.Drawing.Point(121,174);
     m_zoomCityWidget.LeftClickAction = new MouseClickAction(JHU_Globals.PerformCityZoom);
     m_controlForm.Add(m_zoomCityWidget);
     m_zoomBldgWidget = new JHU_ButtonWidget();
     m_zoomBldgWidget.ImageName = "button_building.png";
     m_zoomBldgWidget.Location = new System.Drawing.Point(158,174);
     m_zoomBldgWidget.LeftClickAction = new MouseClickAction(JHU_Globals.PerformBuildingZoom);
     m_controlForm.Add(m_zoomBldgWidget);
     m_resetWidget = new JHU_ButtonWidget();
     m_resetWidget.ImageName = "button_reset.png";
     m_resetWidget.Location = new System.Drawing.Point(158,100);
     m_resetWidget.LeftClickAction = new MouseClickAction(JHU_Globals.PerformReset);
     m_controlForm.Add(m_resetWidget);
     m_rootWidget.Add(m_controlForm);
    }
    m_initialized = true;
   }
  }
  public static void PerformZoomOut(System.Windows.Forms.MouseEventArgs e)
  {
   DrawArgs drawArgs = JHU_Globals.getInstance().WorldWindow.DrawArgs;
   double alt = System.Math.Round(drawArgs.WorldCamera.Altitude);
   alt = alt * 1.2;
   if (alt <= 0)
    return;
   drawArgs.WorldCamera.Altitude = alt;
   JHU_Log.Write(1, "NAV", drawArgs.WorldCamera.Latitude.Degrees, drawArgs.WorldCamera.Longitude.Degrees, alt, "", "Zoom Out Button Pressed");
  }
  public static void PerformZoomIn(System.Windows.Forms.MouseEventArgs e)
  {
   DrawArgs drawArgs = JHU_Globals.getInstance().WorldWindow.DrawArgs;
   double alt = System.Math.Round(drawArgs.WorldCamera.Altitude);
   alt = alt * 0.8;
   if (alt <= 0)
    return;
   drawArgs.WorldCamera.Altitude = alt;
   JHU_Log.Write(1, "NAV", drawArgs.WorldCamera.Latitude.Degrees, drawArgs.WorldCamera.Longitude.Degrees, alt, "", "Zoom In Button Pressed");
  }
  public static void PerformWorldZoom(System.Windows.Forms.MouseEventArgs e)
  {
   JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Altitude = 12500000;
   JHU_Log.Write(1, "NAV", JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Latitude.Degrees, JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Longitude.Degrees, JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Altitude, "", "World Zoom Button Pressed");
  }
  public static void PerformCountryZoom(System.Windows.Forms.MouseEventArgs e)
  {
   JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Altitude = 3500000;
   JHU_Log.Write(1, "NAV", JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Latitude.Degrees, JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Longitude.Degrees, JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Altitude, "", "Country Zoom Button Pressed");
  }
  public static void PerformStateZoom(System.Windows.Forms.MouseEventArgs e)
  {
   JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Altitude = 300000;
   JHU_Log.Write(1, "NAV", JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Latitude.Degrees, JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Longitude.Degrees, JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Altitude, "", "State Zoom Button Pressed");
  }
  public static void PerformCityZoom(System.Windows.Forms.MouseEventArgs e)
  {
   JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Altitude = 30000;
   JHU_Log.Write(1, "NAV", JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Latitude.Degrees, JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Longitude.Degrees, JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Altitude, "", "City Zoom Button Pressed");
  }
  public static void PerformBuildingZoom(System.Windows.Forms.MouseEventArgs e)
  {
   JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Altitude = 1000;
   JHU_Log.Write(1, "NAV", JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Latitude.Degrees, JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Longitude.Degrees, JHU_Globals.getInstance().WorldWindow.DrawArgs.WorldCamera.Altitude, "", "Building Zoom Button Pressed");
  }
  public static void PerformReset(System.Windows.Forms.MouseEventArgs e)
  {
   DrawArgs drawArgs = JHU_Globals.getInstance().WorldWindow.DrawArgs;
   double lat = drawArgs.WorldCamera.Latitude.Degrees;
   double lon = drawArgs.WorldCamera.Longitude.Degrees;
   double alt = drawArgs.WorldCamera.Altitude;
   double fov = drawArgs.WorldCamera.ViewRange.Degrees;
   JHU_Globals.getInstance().WorldWindow.GotoLatLon(lat, lon, 0, alt, fov, 0);
   JHU_Log.Write(1, "NAV", drawArgs.WorldCamera.Latitude.Degrees, drawArgs.WorldCamera.Longitude.Degrees, drawArgs.WorldCamera.Altitude, "", "Reset Button Pressed");
  }
 }
}
