using System;
using System.Collections;
using System.IO;
using System.Diagnostics;
using System.Drawing;
using System.Xml.Serialization;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System.Windows.Forms;
using WorldWind;
using WorldWind.Renderable;
using WorldWind.Net;
using System.Xml;
using MapTools;
using ZedGraph;
namespace MeasureToolNewgen.Plugins
{
    class MeasureWinResult : System.Windows.Forms.Form
    {
  private System.Windows.Forms.Label measureResult;
        private System.ComponentModel.IContainer components;
  private ToolStrip toolStrip1;
  private ToolStripButton btnClearLines;
  private System.Windows.Forms.Label measureLabel;
  private ToolStripButton btnPlotTerrain;
  private ToolStripButton btnLineMode;
  private ToolStripSeparator toolStripSeparator1;
  private ToolStripButton btnStop;
  private MeasureToolNG plugin;
  private string multiIconPath = Application.StartupPath + "\\Plugins\\Measure\\linemode_multi_button.png";
  private string singleIconPath = Application.StartupPath + "\\Plugins\\Measure\\linemode_single_button.png";
        private void InitializeComponent()
        {
   System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MeasureWinResult));
   this.measureLabel = new System.Windows.Forms.Label();
   this.measureResult = new System.Windows.Forms.Label();
   this.toolStrip1 = new System.Windows.Forms.ToolStrip();
   this.btnLineMode = new System.Windows.Forms.ToolStripButton();
   this.btnStop = new System.Windows.Forms.ToolStripButton();
   this.btnClearLines = new System.Windows.Forms.ToolStripButton();
   this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
   this.btnPlotTerrain = new System.Windows.Forms.ToolStripButton();
   this.toolStrip1.SuspendLayout();
   this.SuspendLayout();
   this.measureLabel.AutoSize = true;
   this.measureLabel.Location = new System.Drawing.Point(13, 36);
   this.measureLabel.Name = "measureLabel";
   this.measureLabel.Size = new System.Drawing.Size(40, 13);
   this.measureLabel.TabIndex = 0;
   this.measureLabel.Text = "Length";
   this.measureLabel.Click += new System.EventHandler(this.measureLabel_Click);
   this.measureResult.AutoSize = true;
   this.measureResult.Location = new System.Drawing.Point(79, 36);
   this.measureResult.Name = "measureResult";
   this.measureResult.Size = new System.Drawing.Size(22, 13);
   this.measureResult.TabIndex = 1;
   this.measureResult.Text = "0.0";
   this.measureResult.Click += new System.EventHandler(this.measureResult_Click);
   this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnLineMode,
            this.btnStop,
            this.btnClearLines,
            this.toolStripSeparator1,
            this.btnPlotTerrain});
   this.toolStrip1.Location = new System.Drawing.Point(0, 0);
   this.toolStrip1.Name = "toolStrip1";
   this.toolStrip1.Size = new System.Drawing.Size(181, 25);
   this.toolStrip1.TabIndex = 3;
   this.toolStrip1.Text = "toolStrip1";
   this.btnLineMode.CheckOnClick = true;
   this.btnLineMode.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
   this.btnLineMode.Image = ((System.Drawing.Image)(resources.GetObject("btnLineMode.Image")));
   this.btnLineMode.ImageTransparentColor = System.Drawing.Color.Magenta;
   this.btnLineMode.Name = "btnLineMode";
   this.btnLineMode.Size = new System.Drawing.Size(23, 22);
   this.btnLineMode.Text = "Toggle Line Mode";
   this.btnLineMode.Click += new System.EventHandler(this.btnLineMode_Click);
   this.btnStop.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
   this.btnStop.Enabled = false;
   this.btnStop.Image = ((System.Drawing.Image)(resources.GetObject("btnStop.Image")));
   this.btnStop.ImageTransparentColor = System.Drawing.Color.Magenta;
   this.btnStop.Name = "btnStop";
   this.btnStop.Size = new System.Drawing.Size(23, 22);
   this.btnStop.Text = "toolStripButton1";
   this.btnClearLines.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
   this.btnClearLines.Image = ((System.Drawing.Image)(resources.GetObject("btnClearLines.Image")));
   this.btnClearLines.ImageTransparentColor = System.Drawing.Color.Magenta;
   this.btnClearLines.Name = "btnClearLines";
   this.btnClearLines.Size = new System.Drawing.Size(23, 22);
   this.btnClearLines.Text = "Clear all lines";
   this.btnClearLines.Click += new System.EventHandler(this.clearAllLinesButton_Click);
   this.toolStripSeparator1.Name = "toolStripSeparator1";
   this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
   this.btnPlotTerrain.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
   this.btnPlotTerrain.Image = ((System.Drawing.Image)(resources.GetObject("btnPlotTerrain.Image")));
   this.btnPlotTerrain.ImageTransparentColor = System.Drawing.Color.Magenta;
   this.btnPlotTerrain.Name = "btnPlotTerrain";
   this.btnPlotTerrain.Size = new System.Drawing.Size(23, 22);
   this.btnPlotTerrain.Text = "Plot terrain profile";
   this.btnPlotTerrain.Click += new System.EventHandler(this.btnPlotTerrain_Click);
   this.ClientSize = new System.Drawing.Size(181, 67);
   this.Controls.Add(this.toolStrip1);
   this.Controls.Add(this.measureResult);
   this.Controls.Add(this.measureLabel);
   this.MaximizeBox = false;
   this.MinimizeBox = false;
   this.Name = "MeasureWinResult";
   this.ShowIcon = false;
   this.ShowInTaskbar = false;
   this.Text = "MeasureTool Control";
   this.TopMost = true;
   this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.OnClosing);
   this.toolStrip1.ResumeLayout(false);
   this.toolStrip1.PerformLayout();
   this.ResumeLayout(false);
   this.PerformLayout();
        }
  public MeasureWinResult(MeasureToolNG plugin)
        {
   this.plugin = plugin;
            InitializeComponent();
   if (World.Settings.MeasureMode == MeasureMode.Multi)
   {
    this.btnLineMode.Checked = true;
    this.btnLineMode.Image = new Bitmap(multiIconPath);
   }
        }
        private void OnClosing(object sender, FormClosingEventArgs e)
        {
            e.Cancel = true;
   plugin.ChangePluginStatus();
        }
        public void LengthText(string text)
        {
            measureResult.Text = text;
        }
  private void measureLabel_Click(object sender, EventArgs e)
  {
  }
  private void measureResult_Click(object sender, EventArgs e)
  {
  }
  private void clearAllLinesButton_Click(object sender, EventArgs e)
  {
   plugin.ClearLines();
  }
  private void btnPlotTerrain_Click(object sender, EventArgs e)
  {
   plugin.PlotTerrain();
  }
  private void btnLineMode_Click(object sender, EventArgs e)
  {
   if (btnLineMode.Checked == true)
   {
    World.Settings.MeasureMode = MeasureMode.Multi;
    this.btnLineMode.Image = new Bitmap(multiIconPath);
   }
   else
   {
    World.Settings.MeasureMode = MeasureMode.Single;
    this.btnLineMode.Image = new Bitmap(singleIconPath);
    this.btnStop.Enabled = false;
   }
  }
    }
    public class MeasureToolNG : WorldWind.PluginEngine.Plugin
    {
        MenuItem menuItem;
        MeasureToolLayer layer;
        MeasureWinResult resultwindow;
  TerrainGraphForm tg;
        public override void Load()
        {
            layer = new MeasureToolLayer(
                ParentApplication.WorldWindow.CurrentWorld,ParentApplication.WorldWindow.DrawArgs);
            resultwindow = new MeasureWinResult(this);
            ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(layer);
            layer.LineAdd += resultwindow.LengthText;
            menuItem = new MenuItem("Measure\tM");
            menuItem.Click += new EventHandler(menuItemClicked);
            ParentApplication.ToolsMenu.MenuItems.Add(menuItem);
            ParentApplication.WorldWindow.MouseMove += new MouseEventHandler(layer.MouseMove);
            ParentApplication.WorldWindow.MouseDown += new MouseEventHandler(layer.MouseDown);
            ParentApplication.WorldWindow.MouseUp += new MouseEventHandler(layer.MouseUp);
            ParentApplication.WorldWindow.KeyUp += new KeyEventHandler(this.KeyUp);
            layer.IsOn = false;
            menuItem.Checked = layer.IsOn;
            resultwindow.Visible = layer.IsOn;
        }
        public override void Unload()
        {
            if (menuItem != null)
            {
                ParentApplication.ToolsMenu.MenuItems.Remove(menuItem);
                menuItem.Dispose();
                menuItem = null;
            }
            ParentApplication.WorldWindow.MouseMove -= new MouseEventHandler(layer.MouseMove);
            ParentApplication.WorldWindow.MouseDown -= new MouseEventHandler(layer.MouseDown);
            ParentApplication.WorldWindow.MouseUp -= new MouseEventHandler(layer.MouseUp);
            ParentApplication.WorldWindow.KeyUp -= new KeyEventHandler(this.KeyUp);
            ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Remove(layer);
        }
  public void ClearLines()
  {
   layer.Points = new Point3d[0];
   layer.NeedsUpdate = true;
   layer.LineAdd(ConvertUnits.GetDisplayString(0.0));
   layer.Nodes.Clear();
  }
        private void menuItemClicked(object sender, EventArgs e)
        {
            layer.IsOn = !layer.IsOn;
            menuItem.Checked = layer.IsOn;
            resultwindow.Visible = layer.IsOn;
        }
        private void KeyUp(object sender, KeyEventArgs e)
        {
            if (e.KeyData == Keys.M)
            {
    ChangePluginStatus();
                e.Handled = true;
            }
   if (e.KeyData == Keys.T)
   {
    if (layer.IsOn)
     PlotTerrain();
   }
        }
  public void PlotTerrain()
  {
   ArrayList profile = layer.ElevationProfile();
   if (profile != null)
   {
    tg = new TerrainGraphForm(profile);
    tg.Show();
   }
   else
    MessageBox.Show("No line exists.","Measuretool Error");
  }
  public void ChangePluginStatus()
  {
   layer.IsOn = !layer.IsOn;
   menuItem.Checked = layer.IsOn;
   resultwindow.Visible = layer.IsOn;
  }
    }
    public delegate void LineAddHandler(string LengthText);
    class MeasureToolLayer : LineFeature
    {
  public enum MeasureState
  {
   Idle,
   Measuring,
   Complete
  }
        public LineAddHandler LineAdd;
        private Point mouseDownPoint;
        private DrawArgs m_drawArgs;
        private bool isPointGotoEnabled;
        private double gclength;
  private MeasureState state = MeasureState.Idle;
  private ArrayList m_nodes;
        public override bool IsOn
        {
            get
            {
                return base.IsOn;
            }
            set
            {
                if (value == isOn)
                    return;
                base.IsOn = value;
                if (isOn)
                {
                    isPointGotoEnabled = World.Settings.CameraIsPointGoto;
                    World.Settings.CameraIsPointGoto = false;
                    state = MeasureState.Idle;
                }
                else
                {
                    World.Settings.CameraIsPointGoto = isPointGotoEnabled;
                }
            }
        }
        public double GCLength
        {
            get
            {
                return gclength;
            }
            set
            {
                gclength = value;
            }
        }
  public double TerrainLength
  {
   get { return GCLength; }
  }
  public ArrayList Nodes
  {
   get
   { return m_nodes; }
   set
   { m_nodes = value; }
  }
        public MeasureToolLayer(World world,DrawArgs drawArgs):base("Measure Tool",world,new Point3d[0],Color.Red)
        {
            m_drawArgs = drawArgs;
            this.AltitudeMode = AltitudeMode.RelativeToGround;
   m_nodes = new ArrayList();
   this.LineWidth = 5f;
        }
  public override void Render(DrawArgs drawArgs)
  {
   if (DrawArgs.MouseCursor == CursorType.Arrow)
    DrawArgs.MouseCursor = CursorType.Measure;
   base.Render(drawArgs);
  }
        public void MouseMove(object sender, MouseEventArgs e)
        {
        }
        public void MouseDown(object sender, MouseEventArgs e)
        {
            if (!isOn)
                return;
            mouseDownPoint = DrawArgs.LastMousePosition;
        }
        public void MouseUp(object sender, MouseEventArgs e)
        {
            if (!isOn)
                return;
            if (MouseDragged())
                return;
            if (e.Button == MouseButtons.Right)
            {
                MouseRightClick();
                return;
            }
            if (e.Button != MouseButtons.Left)
                return;
            Angle Latitude,Longitude;
            m_drawArgs.WorldCamera.PickingRayIntersection(
    e.X,
    e.Y,
    out Latitude,
    out Longitude);
            if (World.Settings.MeasureMode == MeasureMode.Single)
            {
                if(Nodes.Count<2)
                {
     Point3d p = new Point3d(Longitude.Degrees, Latitude.Degrees, 0);
     Nodes.Add(p);
     AddPoint(p);
     LineAdd(ConvertUnits.GetDisplayString(CalcLength()));
                }
            }
            else if (World.Settings.MeasureMode == MeasureMode.Multi)
            {
    Point3d p = new Point3d(Longitude.Degrees, Latitude.Degrees, 0);
    Nodes.Add(p);
    AddPoint(p);
                LineAdd(ConvertUnits.GetDisplayString(CalcLength()));
            }
        }
        private bool MouseDragged()
        {
            int dx = DrawArgs.LastMousePosition.X - mouseDownPoint.X;
            int dy = DrawArgs.LastMousePosition.Y - mouseDownPoint.Y;
            if (dx * dx + dy * dy > 3 * 3)
                return true;
            else
                return false;
        }
        private void MouseRightClick()
        {
            if (state != MeasureState.Idle && World.Settings.MeasureMode == MeasureMode.Multi)
            {
                switch (state)
                {
                    case MeasureState.Measuring:
                        state = MeasureState.Complete;
                        return;
                    case MeasureState.Complete:
                        state = MeasureState.Idle;
                        return;
                }
            }
            else
            {
                this.Points = new Point3d[0];
    this.Nodes.Clear();
                this.NeedsUpdate = true;
                LineAdd(ConvertUnits.GetDisplayString(0.0));
            }
        }
        private double CalcLength()
        {
            double accumulatedlength = 0.0;
            for (int i = 1; i < Points.Length; i++)
            {
                Point3d previous = Points[i - 1];
                Point3d current = Points[i];
                Angle angularDistance = World.ApproxAngularDistance
                    (Angle.FromDegrees(previous.X),
                    Angle.FromDegrees(previous.Y),
                    Angle.FromDegrees(current.X),
                    Angle.FromDegrees(current.Y));
                accumulatedlength += angularDistance.Radians * m_world.EquatorialRadius;
            }
            this.GCLength = accumulatedlength;
            return accumulatedlength;
        }
  public ArrayList ElevationProfile()
  {
   World world = this.m_world;
   int maxSamples = 200;
   Angle startLat, startLon, endLat, endLon = Angle.Zero;
   ArrayList terrainLine = new ArrayList();
            if (Nodes.Count > 1)
            {
    double totalArcLengthDegrees = 0;
    for (int node = 1; node < Nodes.Count; node++)
    {
     Point3d endPoint = (Point3d)Nodes[node];
     Point3d startPoint = (Point3d)Nodes[node - 1];
     endLon = Angle.FromDegrees(endPoint.X);
     endLat = Angle.FromDegrees(endPoint.Y);
     startLon = Angle.FromDegrees(startPoint.X);
     startLat = Angle.FromDegrees(startPoint.Y);
     Angle angularDistance = World.ApproxAngularDistance(startLat, startLon, endLat, endLon);
     totalArcLengthDegrees += angularDistance.Degrees;
    }
    MessageBox.Show("Total length: " + totalArcLengthDegrees.ToString() + " deg");
    for (int node = 1; node < Nodes.Count; node++)
    {
     Point3d endPoint = (Point3d)Nodes[node];
     Point3d startPoint = (Point3d)Nodes[node - 1];
     endLon = Angle.FromDegrees(endPoint.X);
     endLat = Angle.FromDegrees(endPoint.Y);
     startLon = Angle.FromDegrees(startPoint.X);
     startLat = Angle.FromDegrees(startPoint.Y);
     Angle angularDistance = World.ApproxAngularDistance(startLat, startLon, endLat, endLon);
     int samples = (int)(maxSamples*angularDistance.Degrees/totalArcLengthDegrees);
     double sampsPerDegree = samples / angularDistance.Degrees;
     double elev0 = world.TerrainAccessor.GetElevationAt(startLat.Degrees, startLon.Degrees, sampsPerDegree);
     double x0 = 0;
     if (node != 1)
      x0 = ((Point2d)terrainLine[terrainLine.Count-1]).X;
     double dist = angularDistance.Radians * (world.EquatorialRadius + elev0);
     double deltaX = dist / (samples - 1);
     WorldWind.Point2d initialPoint = new WorldWind.Point2d(x0, elev0);
     if (node == 1)
      terrainLine.Add(initialPoint);
     double stepSize = 1.0 / (double)samples;
     for (int i = 1; i < samples; i++)
     {
      Angle lat, lon = Angle.Zero;
      double t = i * stepSize;
      World.IntermediateGCPoint((float)t, startLat, startLon, endLat, endLon, angularDistance, out lat, out lon);
      double elevi = (double)world.TerrainAccessor.GetElevationAt(lat.Degrees, lon.Degrees, sampsPerDegree);
      double xi = deltaX * i + x0;
      WorldWind.Point2d pointI = new WorldWind.Point2d(xi, elevi);
      terrainLine.Add(pointI);
     }
    }
    return terrainLine;
            }
            return null;
  }
    }
    class MeasureResult : WorldWind.NewWidgets.FormWidget
    {
        private WorldWind.NewWidgets.TextLabel measureLabel = null;
        private WorldWind.NewWidgets.TextLabel measureResult = null;
        public MeasureResult():base("Measurement Results")
        {
            Initialize();
        }
        private void Initialize()
        {
            this.Name = "Measurement Results";
            this.ClientSize = new System.Drawing.Size(500, 183);
            this.ParentWidget = DrawArgs.NewRootWidget;
            measureLabel = new WorldWind.NewWidgets.TextLabel();
            measureLabel.Name = "Measurement Type";
            measureLabel.Text = "Length";
            measureLabel.Visible = true;
            measureLabel.ParentWidget = this;
            measureLabel.CountHeight = false;
            measureLabel.CountWidth = true;
            measureLabel.ClientLocation = new System.Drawing.Point(40, 65);
            measureResult = new WorldWind.NewWidgets.TextLabel();
            measureResult.Name = "Measurement Result";
            measureResult.Text = "0.0";
            measureResult.Visible = true;
            measureResult.ParentWidget = this;
            measureResult.CountHeight = false;
            measureResult.CountWidth = true;
            measureResult.ClientLocation = new System.Drawing.Point(140, 65);
            this.ChildWidgets.Add(measureLabel);
        }
        public void LengthText(string text)
        {
            measureResult.Text = text;
        }
    }
 internal class TerrainGraphForm : Form
 {
  private System.ComponentModel.IContainer components = null;
  protected override void Dispose(bool disposing)
  {
   if (disposing && (components != null))
   {
    components.Dispose();
   }
   base.Dispose(disposing);
  }
  private void InitializeComponent()
  {
   this.components = new System.ComponentModel.Container();
   this.zedGraphControl1 = new ZedGraph.ZedGraphControl();
   this.SuspendLayout();
   this.zedGraphControl1.EditButtons = System.Windows.Forms.MouseButtons.Left;
   this.zedGraphControl1.EditModifierKeys = ((System.Windows.Forms.Keys)((System.Windows.Forms.Keys.Alt | System.Windows.Forms.Keys.None)));
   this.zedGraphControl1.IsAutoScrollRange = false;
   this.zedGraphControl1.IsEnableHEdit = false;
   this.zedGraphControl1.IsEnableHPan = true;
   this.zedGraphControl1.IsEnableHZoom = true;
   this.zedGraphControl1.IsEnableVEdit = false;
   this.zedGraphControl1.IsEnableVPan = true;
   this.zedGraphControl1.IsEnableVZoom = true;
   this.zedGraphControl1.IsPrintFillPage = true;
   this.zedGraphControl1.IsPrintKeepAspectRatio = true;
   this.zedGraphControl1.IsScrollY2 = false;
   this.zedGraphControl1.IsShowContextMenu = true;
   this.zedGraphControl1.IsShowCopyMessage = true;
   this.zedGraphControl1.IsShowCursorValues = false;
   this.zedGraphControl1.IsShowHScrollBar = false;
   this.zedGraphControl1.IsShowPointValues = false;
   this.zedGraphControl1.IsShowVScrollBar = false;
   this.zedGraphControl1.IsSynchronizeXAxes = false;
   this.zedGraphControl1.IsSynchronizeYAxes = false;
   this.zedGraphControl1.IsZoomOnMouseCenter = false;
   this.zedGraphControl1.LinkButtons = System.Windows.Forms.MouseButtons.Left;
   this.zedGraphControl1.LinkModifierKeys = ((System.Windows.Forms.Keys)((System.Windows.Forms.Keys.Alt | System.Windows.Forms.Keys.None)));
   this.zedGraphControl1.Location = new System.Drawing.Point(12, 12);
   this.zedGraphControl1.Name = "zedGraphControl1";
   this.zedGraphControl1.PanButtons = System.Windows.Forms.MouseButtons.Left;
   this.zedGraphControl1.PanButtons2 = System.Windows.Forms.MouseButtons.Middle;
   this.zedGraphControl1.PanModifierKeys = ((System.Windows.Forms.Keys)((System.Windows.Forms.Keys.Shift | System.Windows.Forms.Keys.None)));
   this.zedGraphControl1.PanModifierKeys2 = System.Windows.Forms.Keys.None;
   this.zedGraphControl1.PointDateFormat = "g";
   this.zedGraphControl1.PointValueFormat = "G";
   this.zedGraphControl1.ScrollMaxX = 0;
   this.zedGraphControl1.ScrollMaxY = 0;
   this.zedGraphControl1.ScrollMaxY2 = 0;
   this.zedGraphControl1.ScrollMinX = 0;
   this.zedGraphControl1.ScrollMinY = 0;
   this.zedGraphControl1.ScrollMinY2 = 0;
   this.zedGraphControl1.Size = new System.Drawing.Size(597, 247);
   this.zedGraphControl1.TabIndex = 0;
   this.zedGraphControl1.ZoomButtons = System.Windows.Forms.MouseButtons.Left;
   this.zedGraphControl1.ZoomButtons2 = System.Windows.Forms.MouseButtons.None;
   this.zedGraphControl1.ZoomModifierKeys = System.Windows.Forms.Keys.None;
   this.zedGraphControl1.ZoomModifierKeys2 = System.Windows.Forms.Keys.None;
   this.zedGraphControl1.ZoomStepFraction = 0.1;
   this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
   this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
   this.ClientSize = new System.Drawing.Size(621, 282);
   this.Controls.Add(this.zedGraphControl1);
   this.Name = "TerrainGraph";
   this.Text = "TerrainGraph";
   this.Resize += new System.EventHandler(this.TerrainGraph_Resize);
   this.Load += new System.EventHandler(this.TerrainGraph_Load);
   this.ResumeLayout(false);
  }
  private ZedGraph.ZedGraphControl zedGraphControl1;
  private ArrayList elevProfile;
  public TerrainGraphForm(ArrayList profile)
  {
   this.elevProfile = profile;
   InitializeComponent();
  }
  private void TerrainGraph_Load(object sender, EventArgs e)
  {
   CreateGraph(zedGraphControl1);
   SetSize();
  }
  private void SetSize()
  {
   zedGraphControl1.Location = new Point(10, 10);
   zedGraphControl1.Size = new Size(ClientRectangle.Width - 20,
         ClientRectangle.Height - 20);
  }
  private void TerrainGraph_Resize(object sender, EventArgs e)
  {
   SetSize();
  }
  private void CreateGraph(ZedGraphControl zgc)
  {
   GraphPane myPane = zgc.GraphPane;
   myPane.XAxis.Scale.MaxGrace = 0.05;
   myPane.Title.IsVisible = false;
   myPane.XAxis.Title.Text = "Distance (m)";
   myPane.YAxis.Title.Text = "Elevation (m)";
   PointPairList list1 = new PointPairList();
   foreach (WorldWind.Point2d pt in elevProfile)
   {
    list1.Add(pt.X, pt.Y);
   }
   LineItem myCurve = myPane.AddCurve("Terrain", list1, Color.Red, SymbolType.None);
   zgc.AxisChange();
  }
 }
}
