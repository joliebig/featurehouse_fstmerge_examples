using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System.Windows.Forms;
using WorldWind.Renderable;
using WorldWind.Camera;
using WorldWind;
using System.IO;
using System;
using System.Drawing;
using System.Globalization;
namespace Withak.Plugin
{
 public class FloodLevelPlugin : WorldWind.PluginEngine.Plugin
 {
  public static string LayerName = "Flood level";
  public override void Load()
  {
   if(ParentApplication.WorldWindow.CurrentWorld != null && ParentApplication.WorldWindow.CurrentWorld.Name.IndexOf("SDSS") == -1)
   {
    FloodLevel layer = new FloodLevel(LayerName, ParentApplication.WorldWindow);
    ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(layer);
   }
  }
  public override void Unload()
  {
   ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Remove(LayerName);
  }
 }
 public class FloodLevel : RenderableObject
 {
  public World world;
  public DrawArgs drawArgs;
  Mesh floodMesh;
  public double FloodElevation;
  public Color FloodColor;
  FloodLevelForm propsFrm;
  public FloodLevel(string LayerName, WorldWind.WorldWindow worldWindow) : base(LayerName)
  {
   this.world = worldWindow.CurrentWorld;
   this.drawArgs = worldWindow.DrawArgs;
   this.FloodColor = Color.DarkBlue;
   this.FloodElevation = 0;
  }
  public override void Render(DrawArgs drawArgs)
  {
   if(!isInitialized)
    return;
   CameraBase camera = drawArgs.WorldCamera;
   Device device = drawArgs.device;
   if(camera.Altitude > 1000e3) return;
   double halfView = 90D - MathEngine.RadiansToDegrees(Math.Asin(camera.WorldRadius/(camera.Altitude + camera.WorldRadius)));
   double west = camera.Longitude.Degrees - halfView;
   double east = camera.Longitude.Degrees + halfView;
   double north = camera.Latitude.Degrees + halfView;
   double south = camera.Latitude.Degrees - halfView;
   float radius = (float)(camera.WorldRadius + FloodElevation * World.Settings.VerticalExaggeration);
   this.Name = "Flood level: " + ConvertUnits.GetDisplayString(FloodElevation);
   if(floodMesh != null) floodMesh.Dispose();
   floodMesh = MakeFloodMesh(device, radius,west,north,east,south,FloodColor);
   SetupLights(drawArgs);
   Matrix origWorld = device.Transform.World;
   Matrix origProjection = device.Transform.Projection;
   bool origFog = device.RenderState.FogEnable;
   device.RenderState.FogEnable = false;
   drawArgs.device.Transform.World = Matrix.Translation(
    (float)-drawArgs.WorldCamera.ReferenceCenter.X,
    (float)-drawArgs.WorldCamera.ReferenceCenter.Y,
    (float)-drawArgs.WorldCamera.ReferenceCenter.Z
    );
   device.SetTexture(0,null);
   device.VertexFormat = CustomVertex.PositionNormalColored.Format;
              device.TextureState[0].AlphaOperation = TextureOperation.SelectArg1;
              device.TextureState[0].AlphaArgument1 = TextureArgument.Diffuse;
              device.TextureState[0].ColorOperation = TextureOperation.SelectArg1;
              device.TextureState[0].ColorArgument1 = TextureArgument.Diffuse;
   floodMesh.DrawSubset(0);
   device.Transform.World = origWorld;
   device.Transform.Projection = origProjection;
   device.RenderState.FogEnable = origFog;
  }
  public void SetupLights(DrawArgs drawArgs)
  {
   Device device = drawArgs.device;
   CameraBase camera = drawArgs.WorldCamera;
   if(World.Settings.EnableSunShading) {
    Material material = new Material();
    material.Diffuse = Color.White;
    material.Ambient = Color.White;
    material.Specular = Color.White;
    material.SpecularSharpness = 50.0f;
    device.Material = material;
    device.Lights[0].Diffuse = Color.White;
    device.Lights[0].Specular = Color.FromArgb(0x80, 0x80, 0x80);
    device.Lights[0].Type = LightType.Directional;
    Point3d sunPosition = SunCalculator.GetGeocentricPosition(TimeKeeper.CurrentTimeUtc);
    device.Lights[0].Direction = new Vector3((float)sunPosition.X, (float)sunPosition.Y, (float)sunPosition.Z);
    device.Lights[0].Update();
    device.Lights[0].Enabled = true;
    device.RenderState.SpecularEnable = true;
   } else {
    device.RenderState.Lighting = false;
    device.RenderState.Ambient = World.Settings.StandardAmbientColor;
    device.RenderState.SpecularEnable = false;
   }
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   isInitialized = true;
  }
  public override void Update(DrawArgs drawArgs)
  {
   if(!isInitialized)
    Initialize(drawArgs);
  }
  public override void Dispose()
  {
   if(floodMesh != null) floodMesh.Dispose();
   if (propsFrm != null && !propsFrm.IsDisposed) propsFrm.Dispose();
   isInitialized = false;
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
   if (propsFrm != null && !propsFrm.IsDisposed)
    return;
   propsFrm = new FloodLevelForm(this);
   propsFrm.Show();
  }
  private Mesh MakeFloodMesh(Device device, float radius, double west, double north, double east, double south, Color color)
  {
   int slices = 128;
   int stacks = 128;
   int numVertices = (slices+1)*(stacks+1);
   int numFaces = slices*stacks*2;
   int indexCount = numFaces * 3;
   double lonSpacing = (east - west)/slices;
   double latSpacing = (north - south)/stacks;
   int alpha = 200;
   color = Color.FromArgb(alpha, color.R, color.G, color.B);
   Mesh mesh = new Mesh(numFaces,numVertices,MeshFlags.Managed,CustomVertex.PositionNormalColored.Format,device);
   int [] ranks = new int[1];
   ranks[0] = mesh.NumberVertices;
   System.Array arr = mesh.VertexBuffer.Lock(0,typeof(CustomVertex.PositionNormalColored),LockFlags.None,ranks);
   int vertIndex=0;
   double latitude = south;
   double longitude = west;
   for(int stack=0;stack<=stacks;stack++)
   {
    for(int slice=0;slice<=slices;slice++)
    {
     CustomVertex.PositionNormalColored pnt = new CustomVertex.PositionNormalColored();
     Vector3 v = MathEngine.SphericalToCartesian( latitude, longitude, radius);
     pnt.X = v.X;
     pnt.Y = v.Y;
     pnt.Z = v.Z;
     pnt.Color = color.ToArgb();
     v.Normalize();
     pnt.Normal = v;
     arr.SetValue(pnt,vertIndex++);
     longitude += lonSpacing;
    }
    latitude += latSpacing;
    longitude = west;
   }
   latitude -= latSpacing;
   longitude -= lonSpacing;
   Console.Write("Final edges (east,north): " + longitude.ToString() + ", " + latitude.ToString() + "\n\n");
   mesh.VertexBuffer.Unlock();
   ranks[0]=indexCount;
   arr = mesh.LockIndexBuffer(typeof(short),LockFlags.None,ranks);
   int i=0;
   short bottomVertex = 0;
   short topVertex = 0;
   for(short x=0;x<stacks;x++)
   {
    bottomVertex = (short)((slices+1)*x);
    topVertex = (short)(bottomVertex + slices + 1);
    for(int y=0;y<slices;y++)
    {
     arr.SetValue(bottomVertex,i++);
     arr.SetValue((short)(topVertex+1),i++);
     arr.SetValue(topVertex,i++);
     arr.SetValue(bottomVertex,i++);
     arr.SetValue((short)(bottomVertex+1),i++);
     arr.SetValue((short)(topVertex+1),i++);
     bottomVertex++;
     topVertex++;
    }
   }
   mesh.IndexBuffer.SetData(arr,0,LockFlags.None);
   return mesh;
  }
 }
 public class FloodLevelForm : Form
 {
  private System.Windows.Forms.NumericUpDown numFloodLevel;
  private System.Windows.Forms.TrackBar sliderFloodLevel;
  private System.Windows.Forms.NumericUpDown numSliderMax;
  private System.Windows.Forms.NumericUpDown numSliderMin;
  private System.Windows.Forms.Label label1;
  private System.Windows.Forms.ColorDialog colorDialog1;
  private System.Windows.Forms.Button buttonColorPicker;
  private FloodLevel fl;
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
   this.numFloodLevel = new System.Windows.Forms.NumericUpDown();
   this.sliderFloodLevel = new System.Windows.Forms.TrackBar();
   this.numSliderMax = new System.Windows.Forms.NumericUpDown();
   this.numSliderMin = new System.Windows.Forms.NumericUpDown();
   this.label1 = new System.Windows.Forms.Label();
   this.colorDialog1 = new System.Windows.Forms.ColorDialog();
   this.buttonColorPicker = new System.Windows.Forms.Button();
   ((System.ComponentModel.ISupportInitialize)(this.numFloodLevel)).BeginInit();
   ((System.ComponentModel.ISupportInitialize)(this.sliderFloodLevel)).BeginInit();
   ((System.ComponentModel.ISupportInitialize)(this.numSliderMax)).BeginInit();
   ((System.ComponentModel.ISupportInitialize)(this.numSliderMin)).BeginInit();
   this.SuspendLayout();
   this.numFloodLevel.Location = new System.Drawing.Point(93, 89);
   this.numFloodLevel.Maximum = new decimal(new int[] {
            9000,
            0,
            0,
            0});
   this.numFloodLevel.Minimum = new decimal(new int[] {
            11000,
            0,
            0,
            -2147483648});
   this.numFloodLevel.Name = "numFloodLevel";
   this.numFloodLevel.Size = new System.Drawing.Size(65, 20);
   this.numFloodLevel.TabIndex = 1;
   this.numFloodLevel.ValueChanged += new System.EventHandler(this.numFloodLevel_ValueChanged);
   this.sliderFloodLevel.LargeChange = 1;
   this.sliderFloodLevel.Location = new System.Drawing.Point(0, 12);
   this.sliderFloodLevel.Maximum = 200;
   this.sliderFloodLevel.Minimum = -200;
   this.sliderFloodLevel.Name = "sliderFloodLevel";
   this.sliderFloodLevel.Orientation = System.Windows.Forms.Orientation.Vertical;
   this.sliderFloodLevel.Size = new System.Drawing.Size(37, 196);
   this.sliderFloodLevel.TabIndex = 0;
   this.sliderFloodLevel.TickFrequency = 2;
   this.sliderFloodLevel.TickStyle = System.Windows.Forms.TickStyle.TopLeft;
   this.sliderFloodLevel.ValueChanged += new System.EventHandler(this.sliderFloodLevel_ValueChanged);
   this.numSliderMax.Location = new System.Drawing.Point(32, 12);
   this.numSliderMax.Maximum = new decimal(new int[] {
            9000,
            0,
            0,
            0});
   this.numSliderMax.Minimum = new decimal(new int[] {
            11000,
            0,
            0,
            -2147483648});
   this.numSliderMax.Name = "numSliderMax";
   this.numSliderMax.Size = new System.Drawing.Size(65, 20);
   this.numSliderMax.TabIndex = 3;
   this.numSliderMax.Value = new decimal(new int[] {
            200,
            0,
            0,
            0});
   this.numSliderMax.ValueChanged += new System.EventHandler(this.numSliderMax_ValueChanged);
   this.numSliderMin.Location = new System.Drawing.Point(32, 188);
   this.numSliderMin.Maximum = new decimal(new int[] {
            9000,
            0,
            0,
            0});
   this.numSliderMin.Minimum = new decimal(new int[] {
            11000,
            0,
            0,
            -2147483648});
   this.numSliderMin.Name = "numSliderMin";
   this.numSliderMin.Size = new System.Drawing.Size(65, 20);
   this.numSliderMin.TabIndex = 4;
   this.numSliderMin.Value = new decimal(new int[] {
            200,
            0,
            0,
            -2147483648});
   this.numSliderMin.ValueChanged += new System.EventHandler(this.numSliderMin_ValueChanged);
   this.label1.AutoSize = true;
   this.label1.Location = new System.Drawing.Point(80, 73);
   this.label1.Name = "label1";
   this.label1.Size = new System.Drawing.Size(99, 13);
   this.label1.TabIndex = 3;
   this.label1.Text = "Flood elevation (m):";
   this.buttonColorPicker.Location = new System.Drawing.Point(83, 124);
   this.buttonColorPicker.Name = "buttonColorPicker";
   this.buttonColorPicker.Size = new System.Drawing.Size(75, 23);
   this.buttonColorPicker.TabIndex = 2;
   this.buttonColorPicker.Text = "Flood Color";
   this.buttonColorPicker.UseVisualStyleBackColor = true;
   this.buttonColorPicker.Click += new System.EventHandler(this.buttonColorPicker_Click);
   this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
   this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
   this.ClientSize = new System.Drawing.Size(182, 220);
   this.Controls.Add(this.buttonColorPicker);
   this.Controls.Add(this.label1);
   this.Controls.Add(this.numSliderMin);
   this.Controls.Add(this.numSliderMax);
   this.Controls.Add(this.sliderFloodLevel);
   this.Controls.Add(this.numFloodLevel);
   this.MaximizeBox = false;
   this.Name = "FloodLevelForm";
   this.SizeGripStyle = System.Windows.Forms.SizeGripStyle.Hide;
   this.Text = "Flood Level";
   ((System.ComponentModel.ISupportInitialize)(this.numFloodLevel)).EndInit();
   ((System.ComponentModel.ISupportInitialize)(this.sliderFloodLevel)).EndInit();
   ((System.ComponentModel.ISupportInitialize)(this.numSliderMax)).EndInit();
   ((System.ComponentModel.ISupportInitialize)(this.numSliderMin)).EndInit();
   this.ResumeLayout(false);
   this.PerformLayout();
  }
  public FloodLevelForm(FloodLevel mesh)
  {
   InitializeComponent();
   this.fl = mesh;
   buttonColorPicker.BackColor = fl.FloodColor;
   numFloodLevel.Value = (decimal)fl.FloodElevation;
  }
  private void buttonColorPicker_Click(object sender, EventArgs e)
  {
   if (colorDialog1.ShowDialog() == DialogResult.OK)
   {
    fl.FloodColor = colorDialog1.Color;
    buttonColorPicker.BackColor = fl.FloodColor;
   }
  }
  private void numFloodLevel_ValueChanged(object sender, EventArgs e)
  {
   if (numFloodLevel.Value < sliderFloodLevel.Minimum)
    sliderFloodLevel.Minimum = (int)numFloodLevel.Value;
   if (numFloodLevel.Value > sliderFloodLevel.Maximum)
    sliderFloodLevel.Maximum = (int)numFloodLevel.Value;
   sliderFloodLevel.Value = (int)numFloodLevel.Value;
   fl.FloodElevation = sliderFloodLevel.Value;
  }
  private void sliderFloodLevel_ValueChanged(object sender, EventArgs e)
  {
   numFloodLevel.Value = sliderFloodLevel.Value;
  }
  private void numSliderMax_ValueChanged(object sender, EventArgs e)
  {
   if (sliderFloodLevel.Value > numSliderMax.Value)
    sliderFloodLevel.Value = (int)numSliderMax.Value;
   sliderFloodLevel.Maximum = (int)numSliderMax.Value;
   sliderFloodLevel.TickFrequency = (int)((sliderFloodLevel.Maximum - sliderFloodLevel.Minimum) / 100);
  }
  private void numSliderMin_ValueChanged(object sender, EventArgs e)
  {
   if (sliderFloodLevel.Value < numSliderMin.Value)
    sliderFloodLevel.Value = (int)numSliderMin.Value;
   sliderFloodLevel.Minimum = (int)numSliderMin.Value;
   sliderFloodLevel.TickFrequency = (int)((sliderFloodLevel.Maximum - sliderFloodLevel.Minimum) / 100);
  }
 }
}
