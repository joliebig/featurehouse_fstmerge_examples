using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System.Windows.Forms;
using WorldWind.Renderable;
using WorldWind.Camera;
using WorldWind;
using System.IO;
using System.Drawing;
using System;
namespace Murris.Plugins
{
       public class SkyGradient : WorldWind.PluginEngine.Plugin
       {
               public static string LayerName = "Sky Gradient";
               public override void Load()
               {
                       int i = FindLayerIndice("Images");
                       FileInfo SettingsFile = new FileInfo(Path.Combine(PluginDirectory, ParentApplication.WorldWindow.CurrentWorld.Name + ".ini"));
                       if(SettingsFile.Exists) {
                               SkyGradientLayer layer = new SkyGradientLayer(LayerName, PluginDirectory, ParentApplication.WorldWindow);
                               ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.ChildObjects.Insert((i != -1) ? i : 1, layer);
                       }
               }
               public override void Unload()
               {
                       ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Remove(LayerName);
               }
               public int FindLayerIndice(string name) {
                       string s = "";
                       for (int i = 0; i < ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.ChildObjects.Count; i++) {
                               RenderableObject l = (RenderableObject)ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.ChildObjects[i];
                               s += l.Name + ", ";
                               if(l.Name.IndexOf(name) != -1) return i;
                       }
                       return -1;
               }
       }
       public class SkyGradientLayer : RenderableObject
       {
               public string version = "0.5b";
               public string pluginName;
               string settingsFileName;
               string pluginPath;
               public World world;
               public DrawArgs drawArgs;
               Mesh mesh;
               double lastAltitude;
               Color savedSkyColor;
               Form pDialog;
               double thickness = 60e3;
               Color horizonColor = Color.FromArgb(0xff, 0xff, 0xff, 0xff);
               Color zenithColor = Color.FromArgb(0xff, 0x10, 0x40, 0xb0);
               bool useFog = false;
               float nearFactor = 2.0f;
               float farFactor = 1.1f;
               public string presetFileName;
               public SkyGradientLayer(string LayerName, string pluginPath, WorldWindow worldWindow) : base(LayerName)
               {
                       this.pluginPath = pluginPath;
                       this.pluginName = LayerName;
                       this.world = worldWindow.CurrentWorld;
                       this.settingsFileName = this.world.Name + ".ini";
                       this.drawArgs = worldWindow.DrawArgs;
                       this.RenderPriority = RenderPriority.SurfaceImages;
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
                       catch(Exception caught) {}
                       if(line != "")
                       {
                               string[] settingsList = line.Split(';');
                               string saveVersion = settingsList[0];
                               if(settingsList[1] != null) presetFileName = settingsList[1];
                               if(settingsList.Length >= 3) IsOn = (settingsList[2] == "False") ? false : true;
                               if(settingsList.Length >= 4) useFog = (settingsList[3] == "False") ? false : true;
                       }
               }
               public void ReadPresets()
               {
                       string line = "";
                       try
                       {
                               TextReader tr = File.OpenText(Path.Combine(pluginPath, presetFileName));
                               line = tr.ReadLine();
                               tr.Close();
                       }
                       catch(Exception caught)
                       {
                               MessageBox.Show("Error reading " + Path.Combine(pluginPath, presetFileName), "Presets loading failed.", MessageBoxButtons.OK, MessageBoxIcon.Error );
                       }
                       if(line != "")
                       {
                               string[] settingsList = line.Split(';');
                               string saveVersion = settingsList[0];
                               if(settingsList[1] != null) thickness = double.Parse(settingsList[1]);
                               if(settingsList[2] != null) zenithColor = ColorFromPreset(settingsList[2]);
                               if(settingsList[3] != null) horizonColor = ColorFromPreset(settingsList[3]);
                       }
               }
               public Color ColorFromPreset(string s)
               {
                       string[] values = s.Split(' ');
                       return Color.FromArgb(int.Parse(values[0]), int.Parse(values[1]), int.Parse(values[2]));
               }
               public void SaveSettings()
               {
                       string line = version + ";" + presetFileName + ";" + IsOn.ToString() + ";" + useFog.ToString();
                       try
                       {
                               StreamWriter sw = new StreamWriter(Path.Combine(pluginPath, settingsFileName));
                               sw.Write(line);
                               sw.Close();
                       }
                       catch(Exception caught) {}
               }
               public void SavePresets()
               {
                       string line = version + ";"
                               + thickness.ToString() + ";"
                               + zenithColor.R.ToString() + " " + zenithColor.G.ToString() + " " + zenithColor.B.ToString() + ";"
                               + horizonColor.R.ToString() + " " + horizonColor.G.ToString() + " " + horizonColor.B.ToString();
                       try
                       {
                               StreamWriter sw = new StreamWriter(Path.Combine(pluginPath, presetFileName));
                               sw.Write(line);
                               sw.Close();
                       }
                       catch(Exception caught) {}
               }
               public override void Render(DrawArgs drawArgs)
               {
                       if(!isInitialized || (this.world.Name == "Earth" && World.Settings.EnableAtmosphericScattering))
                               return;
                       CameraBase camera = drawArgs.WorldCamera;
                       Device device = drawArgs.device;
                       Vector3 cameraPos = camera.Position;
                       double distToCenterOfPlanet = (camera.Altitude + camera.WorldRadius);
                       if(mesh == null || lastAltitude != camera.Altitude)
                       {
                               double tangentalDistance = Math.Sqrt( distToCenterOfPlanet*distToCenterOfPlanet - camera.WorldRadius*camera.WorldRadius);
                               double domeRadius = tangentalDistance;
                               double horizonLat = (-Math.PI / 2 + Math.Acos(tangentalDistance / distToCenterOfPlanet)) * 180 / Math.PI;
                               double zenithLat = 90;
                               if(camera.Altitude >= thickness)
                               {
                                       double tangentalDistanceZenith = Math.Sqrt( distToCenterOfPlanet*distToCenterOfPlanet - (camera.WorldRadius + thickness)*(camera.WorldRadius + thickness));
                                       zenithLat = (-Math.PI / 2 + Math.Acos(tangentalDistanceZenith / distToCenterOfPlanet)) * 180 / Math.PI;
                               }
                               if(camera.Altitude < thickness && camera.Altitude > thickness * 0.8)
                               {
                                       zenithLat = (thickness - camera.Altitude) / (thickness - thickness * 0.8) * 90;
                               }
                               if(mesh != null) mesh.Dispose();
                               mesh = ColoredSphere(device, (float)domeRadius, horizonLat, zenithLat, 128, 24);
                               lastAltitude = camera.Altitude;
                       }
                       device.SetTexture(0,null);
                       device.TextureState[0].ColorOperation = TextureOperation.BlendCurrentAlpha;
                       device.VertexFormat = CustomVertex.PositionColored.Format;
                       Matrix origWorld = device.Transform.World;
                       Matrix origProjection = device.Transform.Projection;
                       Matrix SkyGradientTrans;
                       Vector3 cameraCoord = MathEngine.CartesianToSpherical(cameraPos.X, cameraPos.Y, cameraPos.Z);
                       float camLat = cameraCoord.Y;
                       float camLon = cameraCoord.Z;
                       SkyGradientTrans = Matrix.Translation(0,0,(float)distToCenterOfPlanet);
                       SkyGradientTrans = Matrix.Multiply(SkyGradientTrans, Matrix.RotationY(-camLat+(float)Math.PI/2));
                       SkyGradientTrans = Matrix.Multiply(SkyGradientTrans, Matrix.RotationZ(camLon));
                       device.Transform.World = SkyGradientTrans;
                       Recenter(drawArgs);
                       bool origFog = device.RenderState.FogEnable;
                       device.RenderState.FogEnable = false;
                       float aspectRatio = (float)device.Viewport.Width / device.Viewport.Height;
                       device.Transform.Projection = Matrix.PerspectiveFovRH((float)camera.Fov.Radians, aspectRatio, 1, float.MaxValue );
                       mesh.DrawSubset(0);
                       device.RenderState.FillMode = FillMode.Solid;
                       device.Transform.World = origWorld;
                       device.Transform.Projection = origProjection;
                       device.RenderState.FogEnable = origFog;
                       device.RenderState.ZBufferEnable = true;
                       if(useFog)
                       {
                               double tangentalDistance = Math.Sqrt( distToCenterOfPlanet*distToCenterOfPlanet - camera.WorldRadius*camera.WorldRadius);
                               float a = (float)camera.Altitude;
                               device.RenderState.FogEnable = true;
                               device.RenderState.FogColor = horizonColor;
                               device.RenderState.FogTableMode = FogMode.Linear;
                               device.RenderState.FogStart = a * nearFactor;
                               device.RenderState.FogEnd = (float)tangentalDistance * farFactor;
                       }
               }
               public void Recenter(DrawArgs drawArgs) {
                       drawArgs.device.Transform.World *= Matrix.Translation(
                               (float)-drawArgs.WorldCamera.ReferenceCenter.X,
                               (float)-drawArgs.WorldCamera.ReferenceCenter.Y,
                               (float)-drawArgs.WorldCamera.ReferenceCenter.Z
                               );
               }
               public override void Initialize(DrawArgs drawArgs)
               {
                       try
                       {
                               savedSkyColor = World.Settings.SkyColor;
                               World.Settings.SkyColor = Color.Black;
                               ReadPresets();
                               isInitialized = true;
                       }
                       catch
                       {
                               isOn = false;
                               MessageBox.Show("Error initializing", "Layer initialization failed.", MessageBoxButtons.OK, MessageBoxIcon.Error );
                       }
               }
               public override void Update(DrawArgs drawArgs)
               {
                       if(!isInitialized)
                               Initialize(drawArgs);
               }
               public override void Dispose()
               {
                       isInitialized = false;
                       World.Settings.SkyColor = savedSkyColor;
                       if(mesh!=null)
                       {
                               mesh.Dispose();
                               mesh = null;
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
               public void OnPropertiesClick(object sender, EventArgs e)
               {
                       if(pDialog != null && ! pDialog.IsDisposed)
                               return;
                       pDialog = new propertiesDialog(this);
                       pDialog.Show();
               }
               public class propertiesDialog : System.Windows.Forms.Form
               {
                       private System.Windows.Forms.Label lblTexture;
                       private System.Windows.Forms.ComboBox cboFiles;
                       private System.Windows.Forms.Button btnOK;
                       private System.Windows.Forms.Button btnCancel;
                       private SkyGradientLayer layer;
                       public propertiesDialog( SkyGradientLayer layer )
                       {
                               this.layer = layer;
                               InitializeComponent();
                               DirectoryInfo di = new DirectoryInfo(layer.pluginPath);
                               FileInfo[] imgFiles = di.GetFiles(layer.world.Name + "*.csv");
                               cboFiles.Items.AddRange(imgFiles);
                               int i = cboFiles.FindString(layer.presetFileName);
                               if(i != -1) cboFiles.SelectedIndex = i;
                       }
                       private void InitializeComponent()
                       {
                               this.btnCancel = new System.Windows.Forms.Button();
                               this.btnOK = new System.Windows.Forms.Button();
                               this.lblTexture = new System.Windows.Forms.Label();
                               this.cboFiles = new System.Windows.Forms.ComboBox();
                               this.SuspendLayout();
                               this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
                               this.btnCancel.Location = new System.Drawing.Point(311, 59);
                               this.btnCancel.Name = "btnCancel";
                               this.btnCancel.TabIndex = 0;
                               this.btnCancel.Text = "Cancel";
                               this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
                               this.btnOK.Location = new System.Drawing.Point(224, 59);
                               this.btnOK.Name = "btnOK";
                               this.btnOK.TabIndex = 1;
                               this.btnOK.Text = "OK";
                               this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
                               this.lblTexture.AutoSize = true;
                               this.lblTexture.Location = new System.Drawing.Point(16, 28);
                               this.lblTexture.Name = "lblTexture";
                               this.lblTexture.Size = new System.Drawing.Size(82, 16);
                               this.lblTexture.TabIndex = 2;
                               this.lblTexture.Text = "Preset:";
                               this.cboFiles.Location = new System.Drawing.Point(96, 25);
                               this.cboFiles.Name = "cboFiles";
                               this.cboFiles.Size = new System.Drawing.Size(296, 21);
                               this.cboFiles.TabIndex = 3;
                               this.cboFiles.Text = "Select file";
                               this.cboFiles.DropDownStyle = ComboBoxStyle.DropDownList;
                               this.cboFiles.MaxDropDownItems = 10;
                               this.AcceptButton = this.btnOK;
                               this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
                               this.CancelButton = this.btnCancel;
                               this.ClientSize = new System.Drawing.Size(406, 94);
                               this.ControlBox = false;
                               this.Controls.Add(this.cboFiles);
                               this.Controls.Add(this.lblTexture);
                               this.Controls.Add(this.btnOK);
                               this.Controls.Add(this.btnCancel);
                               this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
                               this.MaximizeBox = false;
                               this.MinimizeBox = false;
                               this.Name = "pDialog";
                               this.ShowInTaskbar = false;
                               this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
                               this.Text = layer.pluginName + " " + layer.version + " properties for " + layer.world.Name;
                               this.TopMost = true;
                               this.ResumeLayout(false);
                       }
                       private void btnOK_Click(object sender, System.EventArgs e)
                       {
                               if(cboFiles.SelectedItem != null)
                               {
                                       layer.Dispose();
                                       layer.presetFileName = cboFiles.SelectedItem.ToString();
                                       layer.Initialize(layer.drawArgs);
                                       layer.SaveSettings();
                               }
                               this.Close();
                       }
                       private void btnCancel_Click(object sender, System.EventArgs e)
                       {
                               this.Close();
                       }
               }
               private Mesh ColoredSphere(Microsoft.DirectX.Direct3D.Device device, float radius, double startLat, double endLat, int slices, int stacks)
               {
                       int numVertices = (slices+1)*(stacks+1);
                       int numFaces = slices*stacks*2;
                       int indexCount = numFaces * 3;
                       Mesh mesh = new Mesh(numFaces,numVertices,MeshFlags.Managed,CustomVertex.PositionColored.Format,device);
                       int [] ranks = new int[1];
                       ranks[0] = mesh.NumberVertices;
                       System.Array arr = mesh.VertexBuffer.Lock(0,typeof(CustomVertex.PositionColored),LockFlags.None,ranks);
                       int vertIndex=0;
                       CustomVertex.PositionColored pnt;
                       Vector3 v;
                       double latitude = startLat - ((endLat - startLat) / 4);
                       if(latitude < startLat - 1) latitude = startLat - 1;
                       for(int slice = 0; slice <= slices; slice++)
                       {
                               pnt = new CustomVertex.PositionColored();
                               double longitude = 180 - ((float)slice/slices*(float)360);
                               v = MathEngine.SphericalToCartesian( latitude, longitude, radius);
                               pnt.X = v.X;
                               pnt.Y = v.Y;
                               pnt.Z = v.Z;
                               pnt.Color = Color.FromArgb(255, horizonColor.R, horizonColor.G, horizonColor.B).ToArgb();
                               arr.SetValue(pnt,vertIndex++);
                       }
                       for(int stack = 1; stack < stacks; stack++)
                       {
                               double linear = (float)(stack-1)/(stacks-1f);
                               double k = 1 - Math.Cos(linear * Math.PI/2);
                               latitude = startLat + (k*(float)(endLat - startLat));
                               double colorFactorZ = linear;
                               double colorFactorH = 1 - colorFactorZ;
                               double alphaFactor = 1 - (linear * linear * linear);
                               if(alphaFactor > 1) alphaFactor = 1f;
                               for(int slice = 0; slice <= slices; slice++)
                               {
                                       pnt = new CustomVertex.PositionColored();
                                       double longitude = 180 - ((float)slice/slices*(float)360);
                                       v = MathEngine.SphericalToCartesian( latitude, longitude, radius);
                                       pnt.X = v.X;
                                       pnt.Y = v.Y;
                                       pnt.Z = v.Z;
                                       pnt.Color = Color.FromArgb(
                                               (int)(255f * alphaFactor),
                                               (int)(horizonColor.R * colorFactorH + zenithColor.R * colorFactorZ),
                                               (int)(horizonColor.G * colorFactorH + zenithColor.G * colorFactorZ),
                                               (int)(horizonColor.B * colorFactorH + zenithColor.B * colorFactorZ)).ToArgb();
                                       arr.SetValue(pnt,vertIndex++);
                               }
                       }
                       latitude = endLat + ((endLat - startLat) / 10);
                       for(int slice = 0; slice <= slices; slice++)
                       {
                               pnt = new CustomVertex.PositionColored();
                               double longitude = 180 - ((float)slice/slices*(float)360);
                               v = MathEngine.SphericalToCartesian( latitude, longitude, radius);
                               pnt.X = v.X;
                               pnt.Y = v.Y;
                               pnt.Z = v.Z;
                               pnt.Color = Color.FromArgb(0, zenithColor.R, zenithColor.G, zenithColor.B).ToArgb();
                               arr.SetValue(pnt,vertIndex++);
                       }
                       mesh.VertexBuffer.Unlock();
                       ranks[0]=indexCount;
                       arr = mesh.LockIndexBuffer(typeof(short),LockFlags.None,ranks);
                       int i=0;
                       short bottomVertex = 0;
                       short topVertex = 0;
                       for(short x = 0; x < stacks; x++)
                       {
                               bottomVertex = (short)((slices+1) * x);
                               topVertex = (short)(bottomVertex + slices + 1);
                               for(int y = 0; y < slices; y++)
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
}
