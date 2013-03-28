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
namespace MeasureToolNewgen.Plugins
{
    class MeasureWinResult : System.Windows.Forms.Form
    {
        private Label measureResult;
        private Label measureLabel;
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
}
