using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System.Windows.Forms;
using WorldWind.Renderable;
using WorldWind.Camera;
using WorldWind;
using System.IO;
using System.Drawing;
using System.Xml;
using System.Collections;
using System.Globalization;
using System;
namespace Murris.Plugins
{
 public class Compass : WorldWind.PluginEngine.Plugin
 {
  private WorldWind.WindowsControlMenuButton m_ToolbarItem;
  private Control control = new Control();
  private EventHandler evhand;
  private CompassLayer layer;
  public static string LayerName = "Compass rose";
  public override void Load()
  {
   control.Visible = true;
   evhand = new EventHandler(control_VisibleChanged);
   control.VisibleChanged += evhand;
   m_ToolbarItem = new WorldWind.WindowsControlMenuButton("Compass", Path.Combine(this.PluginDirectory, @"Plugins\Compass\toolbar\tbcompass.png"), control);
   m_Application.WorldWindow.MenuBar.AddToolsMenuButton(m_ToolbarItem);
   layer = new CompassLayer(LayerName, PluginDirectory, ParentApplication.WorldWindow);
   layer.IsOn = World.Settings.ShowCompass;
   ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(layer);
   m_ToolbarItem.SetPushed(World.Settings.ShowCompass);
  }
  public override void Unload()
  {
   control.VisibleChanged -= evhand;
   control.Dispose();
   if (m_ToolbarItem != null)
    m_Application.WorldWindow.MenuBar.RemoveToolsMenuButton(m_ToolbarItem);
   ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Remove(LayerName);
  }
  private void control_VisibleChanged(object sender, EventArgs e)
  {
   if (control.Visible)
    layer.IsOn = true;
   else
    layer.IsOn = false;
  }
 }
 public class CompassLayer : RenderableObject
 {
  static string version = "0.5";
  string settingsFileName = "Compass.ini";
  string pluginPath;
  World world;
  public DrawArgs drawArgs;
  CustomVertex.PositionTextured[] borderVertices = new CustomVertex.PositionTextured[4];
  Texture texture;
  Rectangle spriteSize;
  Point spriteOffset;
  bool tilt = true;
  string spritePos = "Bottom-Left";
  ArrayList svgList;
  Form pDialog;
  bool isInitializing = false;
  public string textureFileName = "Compass_Rose_Classic.png";
  public CompassLayer(string LayerName, string pluginPath, WorldWindow worldWindow) : base(LayerName)
  {
   this.pluginPath = Path.Combine(pluginPath, @"Plugins\Compass\");
   this.world = worldWindow.CurrentWorld;
   this.drawArgs = worldWindow.DrawArgs;
   this.RenderPriority = RenderPriority.Custom;
   ReadSettings();
  }
  public void ReadSettings()
  {
   string line = "";
   try
   {
    TextReader tr = File.OpenText(Path.Combine(pluginPath, settingsFileName));
    line = tr.ReadLine();
    tr.Close();
   }
   catch(Exception) {}
   if(line != "")
   {
    string[] settingsList = line.Split(';');
    string saveVersion = settingsList[1];
    if(settingsList[1] != null) textureFileName = settingsList[1];
    if(settingsList.Length >= 3) tilt = (settingsList[2] == "False") ? false : true;
    if(settingsList.Length >= 4) spritePos = settingsList[3];
   }
  }
  public void SaveSettings()
  {
   string line = version + ";" + textureFileName + ";" + tilt.ToString() + ";" + spritePos;
   try
   {
    StreamWriter sw = new StreamWriter(Path.Combine(pluginPath, settingsFileName));
    sw.Write(line);
    sw.Close();
   }
   catch(Exception) {}
  }
  public override void Render(DrawArgs drawArgs)
  {
   if(!isInitialized)
    return;
   CameraBase camera = drawArgs.WorldCamera;
   Device device = drawArgs.device;
   bool origFog = device.RenderState.FogEnable;
   device.RenderState.FogEnable = false;
   if(drawArgs.device.RenderState.Lighting)
   {
    drawArgs.device.RenderState.Lighting = false;
    drawArgs.device.RenderState.Ambient = World.Settings.StandardAmbientColor;
   }
   Matrix origProjection = device.Transform.Projection;
   Matrix origView = device.Transform.View;
   Matrix origWorld = device.Transform.World;
   device.Transform.Projection = Matrix.OrthoRH((float)device.Viewport.Width, (float)device.Viewport.Height, -(float)4e6, (float)4e6 );
   float offsetY = 0;
   float offsetZ = 0;
   switch (spritePos)
   {
    case "Bottom-Left" :
     offsetY = (float)device.Viewport.Width/2 - spriteSize.Width/2 - 10;
     offsetZ = (float)device.Viewport.Height/2 - spriteSize.Height/2 - 10;
     break;
    case "Bottom-Center" :
     offsetY = 0;
     offsetZ = (float)device.Viewport.Height/2 - spriteSize.Height/2 - 10;
     break;
    case "Screen-Center" :
     offsetY = 0;
     offsetZ = 0;
     break;
    case "Top-Right" :
     offsetY = -((float)device.Viewport.Width/2 - spriteSize.Width/2 - 10);
     offsetZ = -((float)device.Viewport.Height/2 - spriteSize.Height/2 - 10);
     if(World.Settings.ShowToolbar) offsetZ += 50;
     if(World.Settings.ShowPosition) offsetZ += 140;
     break;
    case "Top-Center" :
     offsetY = 0;
     offsetZ = -((float)device.Viewport.Height/2 - spriteSize.Height/2 - 10);
     if(World.Settings.ShowToolbar) offsetZ += 50;
     break;
    case "Top-Left" :
     offsetY = ((float)device.Viewport.Width/2 - spriteSize.Width/2 - 6);
     offsetZ = -((float)device.Viewport.Height/2 - spriteSize.Height/2 - 4);
     if(World.Settings.ShowToolbar) offsetZ += 40;
     break;
   }
   device.Transform.View = Matrix.LookAtRH(
    new Vector3((float)1e3, offsetY, offsetZ),
    new Vector3(0, offsetY, offsetZ),
    new Vector3(0, 0, 1));
   Matrix trans = Matrix.Translation(0, -(float)spriteOffset.X, (float)spriteOffset.Y);
   trans *= Matrix.RotationX((float)camera.Heading.Radians);
   if(tilt) trans *= Matrix.RotationY((float)camera.Tilt.Radians);
   device.Transform.World = trans;
   if(svgList != null)
   {
    for(int i = 0; i < svgList.Count; i++)
    {
     SvgShape s = (SvgShape)svgList[i];
     s.Render(drawArgs);
    }
   }
   if(texture != null)
   {
    float y = (float)spriteSize.Width/2;
    float z = (float)spriteSize.Height/2;
    borderVertices[0].X = 0;
    borderVertices[0].Y = -y;
    borderVertices[0].Z = -z;
    borderVertices[0].Tu = 0;
    borderVertices[0].Tv = 1;
    borderVertices[1].X = 0;
    borderVertices[1].Y = y;
    borderVertices[1].Z = -z;
    borderVertices[1].Tu = 1;
    borderVertices[1].Tv = 1;
    borderVertices[2].X = 0;
    borderVertices[2].Y = -y;
    borderVertices[2].Z = z;
    borderVertices[2].Tu = 0;
    borderVertices[2].Tv = 0;
    borderVertices[3].X = 0;
    borderVertices[3].Y = y;
    borderVertices[3].Z = z;
    borderVertices[3].Tu = 1;
    borderVertices[3].Tv = 0;
    device.VertexFormat = CustomVertex.PositionTextured.Format;
    device.SetTexture(0,texture);
    device.TextureState[0].ColorOperation = TextureOperation.SelectArg1;
    device.TextureState[0].ColorArgument1 = TextureArgument.TextureColor;
                device.TextureState[0].AlphaOperation = TextureOperation.SelectArg1;
                device.TextureState[0].AlphaArgument1 = TextureArgument.TextureColor;
    device.DrawUserPrimitives(PrimitiveType.TriangleStrip, 2, borderVertices);
   }
   device.Transform.World = origWorld;
   device.Transform.Projection = origProjection;
   device.Transform.View = origView;
   device.RenderState.ZBufferEnable = true;
   device.RenderState.FogEnable = origFog;
  }
  public override bool IsOn
  {
   get
   {
    return base.IsOn;
   }
   set
   {
    World.Settings.ShowCompass = value;
    base.IsOn = value;
   }
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   if(isInitializing) return;
   isInitializing = true;
   if(textureFileName.IndexOf(".svg") != -1)
   {
    try
    {
     string debugMsg = LoadSVG(Path.Combine(pluginPath, textureFileName));
     isInitialized = true;
    }
    catch
    {
     isOn = false;
     MessageBox.Show("Error loading SVG file " + Path.Combine(pluginPath, textureFileName) + ".","Layer initialization failed.", MessageBoxButtons.OK,
      MessageBoxIcon.Error );
    }
   }
   else
   {
    try
    {
     texture = TextureLoader.FromFile(drawArgs.device, Path.Combine(pluginPath, textureFileName), 0, 0, 1,Usage.None,
      Format.Dxt5, Pool.Managed, Filter.Box, Filter.Box, 0 );
     using(Surface s = texture.GetSurfaceLevel(0))
     {
      SurfaceDescription desc = s.Description;
      spriteSize = new Rectangle(0,0, desc.Width, desc.Height);
     }
     spriteOffset = new Point(0, 0);
     isInitialized = true;
    }
    catch
    {
     isOn = false;
     MessageBox.Show("Error loading texture " + Path.Combine(pluginPath, textureFileName) + ".","Layer initialization failed.", MessageBoxButtons.OK,
      MessageBoxIcon.Error );
    }
   }
   isInitializing = false;
  }
  private string LoadSVG(string filePath)
  {
   XmlTextReader reader = null;
   string debugMsg = "";
   Point min = new System.Drawing.Point(int.MaxValue,int.MaxValue);
   Point max = new System.Drawing.Point(int.MinValue,int.MinValue);
   reader = new XmlTextReader(filePath);
   reader.WhitespaceHandling = WhitespaceHandling.None;
   svgList = new ArrayList();
   while (reader.Read())
   {
    switch (reader.NodeType)
    {
     case XmlNodeType.Element:
     switch(reader.Name)
     {
      case "rect" :
      case "circle" :
      case "line" :
      case "polyline" :
      case "polygon" :
       SvgShape s = new SvgShape(reader);
       svgList.Add(s);
       min.X = Math.Min(min.X, s.min.X);
       min.Y = Math.Min(min.Y, s.min.Y);
       max.X = Math.Max(max.X, s.max.X);
       max.Y = Math.Max(max.Y, s.max.Y);
       debugMsg += s.debugMsg + " ";
       break;
     }
      break;
    }
   }
   reader.Close();
   int maxDimension = Math.Max(max.X - min.X, max.Y - min.Y);
   spriteSize = new Rectangle(0, 0, maxDimension, maxDimension);
   spriteOffset = new Point((max.X - min.X) / 2 + min.X, (max.Y - min.Y) / 2 + min.Y);
   return debugMsg;
  }
  public override void Update(DrawArgs drawArgs)
  {
   if(!isInitialized)
    Initialize(drawArgs);
  }
  public override void Dispose()
  {
   isInitialized = false;
   if(texture!=null)
   {
    texture.Dispose();
    texture = null;
   }
   if(svgList != null)
   {
    svgList = null;
   }
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   return false;
  }
  public override void BuildContextMenu( ContextMenu menu )
  {
   menu.MenuItems.Add("Properties", new System.EventHandler(OnPropertiesClick));
  }
  public new void OnPropertiesClick(object sender, EventArgs e)
  {
   if(pDialog != null && ! pDialog.IsDisposed)
    return;
   pDialog = new propertiesDialog(this);
   pDialog.Show();
  }
  public class propertiesDialog : System.Windows.Forms.Form
  {
   private System.Windows.Forms.Label lblTexture;
   private System.Windows.Forms.ComboBox cboTexture;
   private System.Windows.Forms.Label lblTilt;
   private System.Windows.Forms.CheckBox chkTilt;
   private System.Windows.Forms.Label lblPosition;
   private System.Windows.Forms.ComboBox cboPosition;
   private System.Windows.Forms.Button btnOK;
   private System.Windows.Forms.Button btnCancel;
   private CompassLayer layer;
   public propertiesDialog( CompassLayer layer )
   {
    InitializeComponent();
    this.layer = layer;
    DirectoryInfo di = new DirectoryInfo(layer.pluginPath);
    FileInfo[] imgFiles = di.GetFiles("*.png");
    cboTexture.Items.AddRange(imgFiles);
    imgFiles = di.GetFiles("*.svg");
    cboTexture.Items.AddRange(imgFiles);
    int i = cboTexture.FindString(layer.textureFileName);
    if(i != -1) cboTexture.SelectedIndex = i;
    chkTilt.Checked = layer.tilt;
    cboPosition.Items.Add("Top-Left");
    cboPosition.Items.Add("Top-Center");
    cboPosition.Items.Add("Top-Right");
    cboPosition.Items.Add("Bottom-Left");
    cboPosition.Items.Add("Bottom-Center");
    cboPosition.Items.Add("Screen-Center");
    i = cboPosition.FindString(layer.spritePos);
    if(i != -1) cboPosition.SelectedIndex = i;
   }
   private void InitializeComponent()
   {
    this.btnCancel = new System.Windows.Forms.Button();
    this.btnOK = new System.Windows.Forms.Button();
    this.lblTexture = new System.Windows.Forms.Label();
    this.cboTexture = new System.Windows.Forms.ComboBox();
    this.lblTilt = new System.Windows.Forms.Label();
    this.chkTilt = new System.Windows.Forms.CheckBox();
    this.lblPosition = new System.Windows.Forms.Label();
    this.cboPosition = new System.Windows.Forms.ComboBox();
    this.SuspendLayout();
    this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
    this.btnCancel.Location = new System.Drawing.Point(311, 109);
    this.btnCancel.Name = "btnCancel";
    this.btnCancel.TabIndex = 0;
    this.btnCancel.Text = "Cancel";
    this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
    this.btnOK.Location = new System.Drawing.Point(224, 109);
    this.btnOK.Name = "btnOK";
    this.btnOK.TabIndex = 1;
    this.btnOK.Text = "OK";
    this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
    this.lblTexture.AutoSize = true;
    this.lblTexture.Location = new System.Drawing.Point(16, 28);
    this.lblTexture.Name = "lblTexture";
    this.lblTexture.Size = new System.Drawing.Size(82, 16);
    this.lblTexture.TabIndex = 2;
    this.lblTexture.Text = "Compass :";
    this.cboTexture.Location = new System.Drawing.Point(96, 25);
    this.cboTexture.Name = "cboTexture";
    this.cboTexture.Size = new System.Drawing.Size(296, 21);
    this.cboTexture.TabIndex = 3;
    this.cboTexture.Text = "Select image file";
    this.cboTexture.DropDownStyle = ComboBoxStyle.DropDownList;
    this.cboTexture.MaxDropDownItems = 10;
    this.lblTilt.AutoSize = true;
    this.lblTilt.Location = new System.Drawing.Point(16, 59);
    this.lblTilt.Name = "lblTilt";
    this.lblTilt.Size = new System.Drawing.Size(82, 16);
    this.lblTilt.TabIndex = 4;
    this.lblTilt.Text = "Tilt :";
    this.chkTilt.Location = new System.Drawing.Point(96, 55);
    this.chkTilt.Name = "chkTilt";
    this.chkTilt.TabIndex = 5;
    this.chkTilt.Text = "Allow tilt";
    this.lblPosition.AutoSize = true;
    this.lblPosition.Location = new System.Drawing.Point(220, 59);
    this.lblPosition.Name = "lblPosition";
    this.lblPosition.Size = new System.Drawing.Size(82, 16);
    this.lblPosition.TabIndex = 6;
    this.lblPosition.Text = "Placement :";
    this.cboPosition.Location = new System.Drawing.Point(292, 56);
    this.cboPosition.Name = "cboPosition";
    this.cboPosition.Size = new System.Drawing.Size(100, 21);
    this.cboPosition.TabIndex = 7;
    this.cboPosition.Text = "Select placement";
    this.cboPosition.DropDownStyle = ComboBoxStyle.DropDownList;
    this.cboPosition.MaxDropDownItems = 10;
    this.AcceptButton = this.btnOK;
    this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
    this.CancelButton = this.btnCancel;
    this.ClientSize = new System.Drawing.Size(406, 144);
    this.ControlBox = false;
    this.Controls.Add(this.cboTexture);
    this.Controls.Add(this.lblTexture);
    this.Controls.Add(this.lblTilt);
    this.Controls.Add(this.chkTilt);
    this.Controls.Add(this.cboPosition);
    this.Controls.Add(this.lblPosition);
    this.Controls.Add(this.btnOK);
    this.Controls.Add(this.btnCancel);
    this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
    this.MaximizeBox = false;
    this.MinimizeBox = false;
    this.Name = "pDialog";
    this.ShowInTaskbar = false;
    this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
    this.Text = "Compass properties";
    this.TopMost = true;
    this.ResumeLayout(false);
   }
   private void btnOK_Click(object sender, System.EventArgs e)
   {
    if(cboTexture.SelectedItem != null)
    {
     layer.Dispose();
     layer.textureFileName = cboTexture.SelectedItem.ToString();
     layer.Initialize(layer.drawArgs);
     layer.tilt = chkTilt.Checked;
     layer.spritePos = cboPosition.SelectedItem.ToString();
     layer.SaveSettings();
    }
    this.Close();
   }
   private void btnCancel_Click(object sender, System.EventArgs e)
   {
    this.Close();
   }
  }
  private class SvgShape
  {
   CustomVertex.PositionColored[] lineVertices;
   int verticeCount = 0;
   int color = Color.White.ToArgb();
   string[] points;
   float x1, y1, x2, y2;
   string DecSep = CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator;
   public string debugMsg = "";
   public Point min = new System.Drawing.Point(int.MaxValue,int.MaxValue);
   public Point max = new System.Drawing.Point(int.MinValue,int.MinValue);
   public SvgShape(XmlTextReader reader)
   {
    if(reader["stroke"] != null)
    {
     if(reader["stroke"].StartsWith("#"))
     {
     }
     else
     {
      try {color = Color.FromName(reader["stroke"]).ToArgb();}
      catch {}
     }
    }
    lineVertices = new CustomVertex.PositionColored[100];
    switch(reader.Name)
    {
     case "rect":
      debugMsg += reader.Name + ":";
      x1 = Convert.ToSingle(reader["x"], CultureInfo.InvariantCulture);
      y1 = Convert.ToSingle(reader["y"], CultureInfo.InvariantCulture);
      x2 = x1 + Convert.ToSingle(reader["width"], CultureInfo.InvariantCulture);
      y2 = y2 + Convert.ToSingle(reader["height"], CultureInfo.InvariantCulture);
      min.X = Math.Min((int)x1, min.X);
      min.Y = Math.Min((int)y1, min.Y);
      max.X = Math.Max((int)x1, max.X);
      max.Y = Math.Max((int)y1, max.Y);
      min.X = Math.Min((int)x2, min.X);
      min.Y = Math.Min((int)y2, min.Y);
      max.X = Math.Max((int)x2, max.X);
      max.Y = Math.Max((int)y2, max.Y);
      this.lineVertices[this.verticeCount].X = 0;
      this.lineVertices[this.verticeCount].Y = x1;
      this.lineVertices[this.verticeCount].Z = -y1;
      this.lineVertices[this.verticeCount].Color = this.color;
      this.verticeCount++;
      this.lineVertices[this.verticeCount].X = 0;
      this.lineVertices[this.verticeCount].Y = x2;
      this.lineVertices[this.verticeCount].Z = -y1;
      this.lineVertices[this.verticeCount].Color = this.color;
      this.verticeCount++;
      this.lineVertices[this.verticeCount].X = 0;
      this.lineVertices[this.verticeCount].Y = x2;
      this.lineVertices[this.verticeCount].Z = -y2;
      this.lineVertices[this.verticeCount].Color = this.color;
      this.verticeCount++;
      this.lineVertices[this.verticeCount].X = 0;
      this.lineVertices[this.verticeCount].Y = x1;
      this.lineVertices[this.verticeCount].Z = -y2;
      this.lineVertices[this.verticeCount].Color = this.color;
      this.verticeCount++;
      this.lineVertices[this.verticeCount].X = 0;
      this.lineVertices[this.verticeCount].Y = x1;
      this.lineVertices[this.verticeCount].Z = -y1;
      this.lineVertices[this.verticeCount].Color = this.color;
      this.verticeCount++;
      break;
     case "circle" :
      debugMsg += "circle:";
      x1 = Convert.ToSingle(reader["cx"], CultureInfo.InvariantCulture);
      y1 = Convert.ToSingle(reader["cy"], CultureInfo.InvariantCulture);
      float r = Convert.ToSingle(reader["r"], CultureInfo.InvariantCulture);
      double twoPI = Math.PI * 2;
      double step = twoPI/32;
      for(double a = 0; a < twoPI; a += step)
      {
       float x = (float)(x1 + Math.Sin(a) * r);
       float y = (float)(y1 + Math.Cos(a) * r);
       this.lineVertices[this.verticeCount].X = 0;
       this.lineVertices[this.verticeCount].Y = x;
       this.lineVertices[this.verticeCount].Z = -y;
       this.lineVertices[this.verticeCount].Color = this.color;
       this.verticeCount++;
      }
      this.lineVertices[this.verticeCount].X = this.lineVertices[0].X;
      this.lineVertices[this.verticeCount].Y = this.lineVertices[0].Y;
      this.lineVertices[this.verticeCount].Z = this.lineVertices[0].Z;
      this.lineVertices[this.verticeCount].Color = this.color;
      this.verticeCount++;
      min.X = Math.Min((int)(x1-r), min.X);
      min.Y = Math.Min((int)(y1-r), min.Y);
      max.X = Math.Max((int)(x1+r), max.X);
      max.Y = Math.Max((int)(y1+r), max.Y);
      break;
     case "line":
      debugMsg += reader.Name + ":";
      x1 = Convert.ToSingle(reader["x1"], CultureInfo.InvariantCulture);
      y1 = Convert.ToSingle(reader["y1"], CultureInfo.InvariantCulture);
      min.X = Math.Min((int)x1, min.X);
      min.Y = Math.Min((int)y1, min.Y);
      max.X = Math.Max((int)x1, max.X);
      max.Y = Math.Max((int)y1, max.Y);
      this.lineVertices[this.verticeCount].X = 0;
      this.lineVertices[this.verticeCount].Y = x1;
      this.lineVertices[this.verticeCount].Z = -y1;
      this.lineVertices[this.verticeCount].Color = this.color;
      this.verticeCount++;
      x2 = Convert.ToSingle(reader["x2"], CultureInfo.InvariantCulture);
      y2 = Convert.ToSingle(reader["y2"], CultureInfo.InvariantCulture);
      min.X = Math.Min((int)x2, min.X);
      min.Y = Math.Min((int)y2, min.Y);
      max.X = Math.Max((int)x2, max.X);
      max.Y = Math.Max((int)y2, max.Y);
      this.lineVertices[this.verticeCount].X = 0;
      this.lineVertices[this.verticeCount].Y = x2;
      this.lineVertices[this.verticeCount].Z = -y2;
      this.lineVertices[this.verticeCount].Color = this.color;
      this.verticeCount++;
      break;
     case "polyline" :
      debugMsg += reader.Name + ":";
      points = reader["points"].Split(' ');
      for(int i = 0; i < points.Length; i++)
      {
       if(points[i].IndexOf(",") != -1)
       {
        points[i].Replace("\n", "");
        points[i].Replace("\r", "");
        string[] pair = points[i].Split(',');
        debugMsg += "(" + pair[0] + "-" + pair[1] + ")";
        x1 = Convert.ToSingle(pair[0], CultureInfo.InvariantCulture);
        y1 = Convert.ToSingle(pair[1], CultureInfo.InvariantCulture);
        min.X = Math.Min((int)x1, min.X);
        min.Y = Math.Min((int)y1, min.Y);
        max.X = Math.Max((int)x1, max.X);
        max.Y = Math.Max((int)y1, max.Y);
        this.lineVertices[this.verticeCount].X = 0;
        this.lineVertices[this.verticeCount].Y = x1;
        this.lineVertices[this.verticeCount].Z = -y1;
        this.lineVertices[this.verticeCount].Color = this.color;
        this.verticeCount++;
       }
      }
      break;
     case "polygon" :
      debugMsg += reader.Name + ":";
      points = reader["points"].Split(' ');
      for(int i = 0; i < points.Length; i++)
      {
       if(points[i].IndexOf(",") != -1)
       {
        points[i].Replace("\n", "");
        points[i].Replace("\r", "");
        string[] pair = points[i].Split(',');
        debugMsg += "(" + pair[0] + "-" + pair[1] + ")";
        x1 = Convert.ToSingle(pair[0], CultureInfo.InvariantCulture);
        y1 = Convert.ToSingle(pair[1], CultureInfo.InvariantCulture);
        min.X = Math.Min((int)x1, min.X);
        min.Y = Math.Min((int)y1, min.Y);
        max.X = Math.Max((int)x1, max.X);
        max.Y = Math.Max((int)y1, max.Y);
        this.lineVertices[this.verticeCount].X = 0;
        this.lineVertices[this.verticeCount].Y = x1;
        this.lineVertices[this.verticeCount].Z = -y1;
        this.lineVertices[this.verticeCount].Color = this.color;
        this.verticeCount++;
       }
      }
      this.lineVertices[this.verticeCount].X = this.lineVertices[0].X;
      this.lineVertices[this.verticeCount].Y = this.lineVertices[0].Y;
      this.lineVertices[this.verticeCount].Z = this.lineVertices[0].Z;
      this.lineVertices[this.verticeCount].Color = this.color;
      this.verticeCount++;
      break;
    }
   }
   public void Render(DrawArgs drawArgs)
   {
    drawArgs.device.TextureState[0].ColorOperation = TextureOperation.Disable;
    drawArgs.device.VertexFormat = CustomVertex.PositionColored.Format;
    drawArgs.device.DrawUserPrimitives(PrimitiveType.LineStrip, verticeCount-1, lineVertices);
   }
  }
 }
}
