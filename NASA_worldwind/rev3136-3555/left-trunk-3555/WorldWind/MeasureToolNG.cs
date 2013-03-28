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
  private System.Windows.Forms.Label measureLabel;
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MeasureWinResult));
            this.measureLabel = new System.Windows.Forms.Label();
            this.measureResult = new System.Windows.Forms.Label();
            this.SuspendLayout();
            this.measureLabel.AutoSize = true;
            this.measureLabel.Location = new System.Drawing.Point(13, 13);
            this.measureLabel.Name = "measureLabel";
            this.measureLabel.Size = new System.Drawing.Size(40, 13);
            this.measureLabel.TabIndex = 0;
            this.measureLabel.Text = "Length";
            this.measureResult.AutoSize = true;
            this.measureResult.Location = new System.Drawing.Point(103, 13);
            this.measureResult.Name = "measureResult";
            this.measureResult.Size = new System.Drawing.Size(22, 13);
            this.measureResult.TabIndex = 1;
            this.measureResult.Text = "0.0";
            this.ClientSize = new System.Drawing.Size(314, 47);
            this.Controls.Add(this.measureResult);
            this.Controls.Add(this.measureLabel);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "MeasureWinResult";
            this.Text = "Measurement Result";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.OnClosing);
            this.ResumeLayout(false);
            this.PerformLayout();
        }
        public MeasureWinResult()
        {
            InitializeComponent();
        }
        private void OnClosing(object sender, FormClosingEventArgs e)
        {
            e.Cancel = true;
            this.Visible = false;
        }
        public void LengthText(string text)
        {
            measureResult.Text = text;
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
            resultwindow = new MeasureWinResult();
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
                layer.IsOn = !layer.IsOn;
                menuItem.Checked = layer.IsOn;
                resultwindow.Visible = layer.IsOn;
                e.Handled = true;
            }
   if (e.KeyData == Keys.T)
   {
    int lastPointIndex = layer.Points.GetLength(0) - 1;
    ArrayList profile = layer.ElevationProfile(lastPointIndex);
    tg = new TerrainGraphForm(profile);
    tg.Show();
   }
        }
    }
    public delegate void LineAddHandler(string LengthText);
    class MeasureToolLayer : LineFeature
    {
        private enum MeasureState
        {
            Idle,
            Measuring,
            Complete
        }
        public LineAddHandler LineAdd;
        private Point mouseDownPoint;
        private MeasureState State = MeasureState.Idle;
        private DrawArgs m_drawArgs;
        private bool isPointGotoEnabled;
        private double length;
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
                    State = MeasureState.Idle;
                }
                else
                {
                    World.Settings.CameraIsPointGoto = isPointGotoEnabled;
                }
            }
        }
        public double Length
        {
            get
            {
                return length;
            }
            set
            {
                length = value;
            }
        }
        public MeasureToolLayer(World world,DrawArgs drawArgs):base("Measure Tool",world,new Point3d[0],Color.Red)
        {
            m_drawArgs = drawArgs;
            this.AltitudeMode = AltitudeMode.Absolute;
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
                if(Points.Length<2)
                {
                    AddPoint( Longitude.Degrees,Latitude.Degrees,1000);
                    LineAdd(ConvertUnits.GetDisplayString(CalcLength()));
                }
            }
            else if (World.Settings.MeasureMode == MeasureMode.Multi)
            {
                AddPoint(Longitude.Degrees, Latitude.Degrees, 1000);
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
            if (State != MeasureState.Idle && World.Settings.MeasureMode == MeasureMode.Multi)
            {
                switch (State)
                {
                    case MeasureState.Measuring:
                        State = MeasureState.Complete;
                        return;
                    case MeasureState.Complete:
                        State = MeasureState.Idle;
                        return;
                }
            }
            else
            {
                this.Points = new Point3d[0];
                this.NeedsUpdate = true;
                LineAdd(ConvertUnits.GetDisplayString(0.0));
            }
        }
        private void CalcIntermediates()
        {
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
            this.Length = accumulatedlength;
            return accumulatedlength;
        }
        private string FormatLength(double length,Units units)
        {
            string labelText = "Unsupported Units";
            if (units == Units.Metric)
            {
                labelText = length >= 10000 ?
                string.Format("{0:f1}km", length / 1000) :
                string.Format("{0:f1}m", length);
            }
            return labelText;
        }
  public ArrayList ElevationProfile(int segmentEndPointIndex)
  {
   World world = this.m_world;
   int samples = 100;
   double stepSize = 1.0 / (double)samples;
   Angle startLat, startLon, endLat, endLon = Angle.Zero;
   endLon = Angle.FromDegrees(this.Points[segmentEndPointIndex].X);
   endLat = Angle.FromDegrees(this.Points[segmentEndPointIndex].Y);
   startLon = Angle.FromDegrees(this.Points[segmentEndPointIndex - 1].X);
   startLat = Angle.FromDegrees(this.Points[segmentEndPointIndex - 1].Y);
   double elev0 = world.TerrainAccessor.GetElevationAt(startLat.Degrees, startLon.Degrees);
   double x0 = 0;
   Angle angularDistance = World.ApproxAngularDistance(startLat, startLon, endLat, endLon);
   double dist = angularDistance.Radians * (world.EquatorialRadius + elev0);
   double deltaX = dist / (samples - 1);
   WorldWind.Point2d initialPoint = new WorldWind.Point2d(x0, elev0);
   ArrayList terrainLine = new ArrayList();
   terrainLine.Add(initialPoint);
   for (int i = 1; i < samples; i++)
   {
    Angle lat, lon = Angle.Zero;
    double t = i * stepSize;
    World.IntermediateGCPoint((float)t, startLat, startLon, endLat, endLon, angularDistance, out lat, out lon);
    double elevi = (double)world.TerrainAccessor.GetElevationAt(lat.Degrees, lon.Degrees);
    double xi = deltaX * i + x0;
    WorldWind.Point2d pointI = new WorldWind.Point2d(xi, elevi);
    terrainLine.Add(pointI);
   }
   return terrainLine;
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
   myPane.Title.Text = "Terrain graph";
   myPane.XAxis.Title.Text = "Distance (m)";
   myPane.YAxis.Title.Text = "Elevation (m)";
   PointPairList list1 = new PointPairList();
   foreach (WorldWind.Point2d pt in elevProfile)
   {
    list1.Add(pt.X, pt.Y);
   }
   LineItem myCurve = myPane.AddCurve("Terrain", list1, Color.Red);
   zgc.AxisChange();
  }
 }
}
