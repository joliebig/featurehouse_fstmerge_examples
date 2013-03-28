using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System.Windows.Forms;
using WorldWind.Renderable;
using WorldWind.Camera;
using WorldWind;
using System.IO;
using System.Drawing;
using System;
using System.Globalization;
namespace Murris.Plugins
{
    public class PlanetaryRings : WorldWind.PluginEngine.Plugin
    {
        public static string LayerName = "Planetary Rings";
        public override void Load()
        {
            FileInfo SettingsFile = new FileInfo(Path.Combine(PluginDirectory, ParentApplication.WorldWindow.CurrentWorld.Name + ".ini"));
            if (SettingsFile.Exists)
            {
                PlanetaryRingsLayer layer = new PlanetaryRingsLayer(LayerName, PluginDirectory, ParentApplication.WorldWindow);
                ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(layer);
            }
        }
        public override void Unload()
        {
            ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Remove(LayerName);
        }
    }
    public class PlanetaryRingsLayer : RenderableObject
    {
        public string version = "1.2";
        public string pluginName;
        string settingsFileName;
        string pluginPath;
        World world;
        public DrawArgs drawArgs;
        Mesh layerMesh;
        Mesh occlusionMesh;
        Texture texture;
        Form pDialog;
        RenderableObject MoonLayer;
        bool CheckMoonLayer = true;
        public string textureFileName;
        public double inRadius;
        public double outRadius;
        public double inFactor = 1.2;
        public double outFactor = 2.5;
        bool ringsOn = true;
        bool shadowsOn = false;
        int shadowOpacity = 240;
        Mesh shadowsMesh;
        Mesh nightSideMesh;
        Mesh projMesh;
        Mesh glowMesh;
        Texture textureInverted;
        double sunLat = -24;
        double sunLon = 80;
        double sunLatBak;
        double sunLonBak;
        bool sunLocked = true;
        bool sunInvert = true;
        double shadowsLat = 999;
        bool showNightSide = true;
        bool showGlobeShadow = false;
        bool showRingsShadow = false;
        bool showUnlitSide = false;
        bool showGlow = false;
        public PlanetaryRingsLayer(string LayerName, string pluginPath, WorldWindow worldWindow)
            : base(LayerName)
        {
            this.pluginPath = pluginPath;
            this.pluginName = LayerName;
            this.world = worldWindow.CurrentWorld;
            this.settingsFileName = this.world.Name + ".ini";
            this.drawArgs = worldWindow.DrawArgs;
            this.RenderPriority = RenderPriority.AtmosphericImages;
            this.IsOn = false;
            ReadSettings();
        }
        public void FindMoonLayer()
        {
            string s = "";
            foreach (RenderableObject l in world.RenderableObjects.ChildObjects)
            {
                s += l.Name + ", ";
                if (l.Name.IndexOf("Moon") != -1) MoonLayer = l;
            }
            CheckMoonLayer = false;
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
            catch (Exception caught) { }
            if (line != "")
            {
                string[] settingsList = line.Split(';');
                string saveVersion = settingsList[0];
                if (settingsList[1] != null) textureFileName = settingsList[1];
                if (settingsList.Length >= 3) IsOn = (settingsList[2] == "False") ? false : true;
                if (settingsList.Length >= 4) shadowsOn = (settingsList[3] == "False") ? false : true;
                if (settingsList.Length >= 5) sunLocked = (settingsList[4] == "False") ? false : true;
                if (settingsList.Length >= 6) sunInvert = (settingsList[5] == "False") ? false : true;
                if (settingsList.Length >= 7) sunLat = double.Parse(settingsList[6], CultureInfo.InvariantCulture);
                if (settingsList.Length >= 8) sunLon = double.Parse(settingsList[7], CultureInfo.InvariantCulture);
                if (settingsList.Length >= 9) showNightSide = (settingsList[8] == "False") ? false : true;
                if (settingsList.Length >= 10) showGlobeShadow = (settingsList[9] == "False") ? false : true;
                if (settingsList.Length >= 11) showRingsShadow = (settingsList[10] == "False") ? false : true;
                if (settingsList.Length >= 12) showUnlitSide = (settingsList[11] == "False") ? false : true;
                if (settingsList.Length >= 13) showGlow = (settingsList[12] == "False") ? false : true;
                sunLatBak = sunLat; sunLonBak = sunLon;
            }
        }
        public void SaveSettings()
        {
            int lat = (int)sunLat;
            int lon = (int)sunLon;
            string line = version + ";"
                            + textureFileName + ";"
                            + IsOn.ToString() + ";"
                            + shadowsOn.ToString() + ";"
                            + sunLocked.ToString() + ";"
                            + sunInvert.ToString() + ";"
                            + lat.ToString() + ";"
                            + lon.ToString() + ";"
                            + showNightSide.ToString() + ";"
                            + showGlobeShadow.ToString() + ";"
                            + showRingsShadow.ToString() + ";"
                            + showUnlitSide.ToString() + ";"
                            + showGlow.ToString();
            try
            {
                StreamWriter sw = new StreamWriter(Path.Combine(pluginPath, settingsFileName));
                sw.Write(line);
                sw.Close();
            }
            catch (Exception caught) { }
        }
        public override void Render(DrawArgs drawArgs)
        {
            if (!isInitialized) return;
            if (CheckMoonLayer) FindMoonLayer();
            if (MoonLayer != null)
            {
                if (MoonLayer.IsOn) return;
            }
            CameraBase camera = drawArgs.WorldCamera;
            Device device = drawArgs.device;
            bool WWSunShading = World.Settings.EnableSunShading;
            Vector3 cameraPos = camera.Position;
            Vector3 cameraCoord = MathEngine.CartesianToSpherical(cameraPos.X, cameraPos.Y, cameraPos.Z);
            float camLat = cameraCoord.Y;
            inRadius = camera.WorldRadius * inFactor;
            outRadius = camera.WorldRadius * outFactor;
            if (layerMesh == null) layerMesh = TexturedRings(device, (float)inRadius, (float)outRadius, 128, 1);
            if (occlusionMesh == null) occlusionMesh = ColoredSphere(device, (float)camera.WorldRadius, 64, 32);
            if (!sunLocked)
            {
                sunLat = MathEngine.RadiansToDegrees(sunInvert ? -camLat : camLat);
                sunLatBak = sunLat;
            }
            else
            {
                if (WWSunShading)
                {
                    Point3d sunPosition = -SunCalculator.GetGeocentricPosition(TimeKeeper.CurrentTimeUtc);
                    Point3d sunSpherical = MathEngine.CartesianToSphericalD(sunPosition.X, sunPosition.Y, sunPosition.Z);
                    sunLat = MathEngine.RadiansToDegrees(sunSpherical.Y);
                    sunLon = MathEngine.RadiansToDegrees(sunSpherical.Z);
                }
                else
                {
                    sunLat = sunLatBak;
                    sunLon = sunLonBak;
                }
            }
            bool origFog = device.RenderState.FogEnable;
            device.RenderState.FogEnable = false;
            Matrix origProjection = device.Transform.Projection;
            Matrix origWorld = device.Transform.World;
            float aspectRatio = (float)device.Viewport.Width / device.Viewport.Height;
            float near = (float)(camera.WorldRadius + camera.Altitude - outRadius);
            float far = (float)(camera.WorldRadius + camera.Altitude + outRadius);
            if (near < 10000) near = 10000;
            device.Transform.Projection = Matrix.PerspectiveFovRH((float)camera.Fov.Radians, aspectRatio, near, far);
            device.RenderState.Lighting = false;
            device.RenderState.Ambient = Color.White;
            if ((shadowsOn || WWSunShading) && showGlow)
            {
                if (glowMesh == null || sunLat != shadowsLat)
                {
                    if (glowMesh != null) glowMesh.Dispose();
                    glowMesh = AtmoSphere(device, (float)(camera.WorldRadius + 100000), 64, 32);
                }
                device.Transform.World = Matrix.Identity;
                Recenter(drawArgs);
                device.RenderState.ZBufferEnable = false;
                device.SetTexture(0, null);
                device.RenderState.AlphaBlendEnable = true;
                device.RenderState.SourceBlend = Blend.SourceAlpha;
                device.RenderState.DestinationBlend = Blend.InvSourceAlpha;
                device.TextureState[0].ColorOperation = TextureOperation.SelectArg2;
                device.TextureState[0].ColorArgument2 = TextureArgument.Diffuse;
                device.TextureState[1].ColorOperation = TextureOperation.Disable;
                device.TextureState[0].AlphaOperation = TextureOperation.SelectArg2;
                device.TextureState[0].AlphaArgument2 = TextureArgument.Diffuse;
                device.TextureState[1].AlphaOperation = TextureOperation.Disable;
                glowMesh.DrawSubset(0);
                device.Transform.World = origWorld;
            }
            if ((shadowsOn || WWSunShading) && showRingsShadow)
            {
                if (projMesh == null || sunLat != shadowsLat)
                {
                    if (projMesh != null) projMesh.Dispose();
                    projMesh = ProjectedRings(device, (float)(camera.WorldRadius + 100000), 128, 64);
                }
                device.Transform.World = Matrix.Identity;
                if (sunLat > 0) device.Transform.World *= Matrix.RotationX((float)Math.PI);
                device.Transform.World *= Matrix.RotationY((float)MathEngine.DegreesToRadians(-sunLat));
                device.Transform.World *= Matrix.RotationZ((float)MathEngine.DegreesToRadians(sunLon));
                Recenter(drawArgs);
                device.RenderState.ZBufferEnable = false;
                device.TextureState[0].ColorOperation = TextureOperation.Add;
                device.TextureState[0].ColorArgument1 = TextureArgument.TextureColor;
                device.TextureState[0].ColorArgument2 = TextureArgument.Diffuse;
                device.TextureState[1].ColorOperation = TextureOperation.Disable;
                device.TextureState[0].AlphaOperation = TextureOperation.Modulate;
                device.TextureState[0].AlphaArgument1 = TextureArgument.TextureColor;
                device.TextureState[0].AlphaArgument2 = TextureArgument.Diffuse;
                device.TextureState[1].AlphaOperation = TextureOperation.Disable;
                device.SamplerState[0].MipFilter = TextureFilter.Linear;
                device.SamplerState[0].MinFilter = TextureFilter.Linear;
                device.SamplerState[0].MagFilter = TextureFilter.Linear;
                device.SetTexture(0, textureInverted);
                projMesh.DrawSubset(0);
                device.Transform.World = origWorld;
            }
            if ((shadowsOn || WWSunShading) && showNightSide)
            {
                if (nightSideMesh == null) nightSideMesh = NightSideShadow(device, (float)(camera.WorldRadius + 100000), 64, 64);
                device.Transform.World = Matrix.RotationY((float)MathEngine.DegreesToRadians(sunLat + 90));
                device.Transform.World *= Matrix.RotationZ((float)MathEngine.DegreesToRadians(sunLon + 180));
                Recenter(drawArgs);
                device.RenderState.ZBufferEnable = false;
                device.SetTexture(0, null);
                nightSideMesh.DrawSubset(0);
                device.Transform.World = origWorld;
            }
            device.RenderState.ZBufferEnable = true;
            device.Clear(ClearFlags.ZBuffer, Color.Black, 1.0f, 0);
            Recenter(drawArgs);
            device.RenderState.SourceBlend = Blend.Zero;
            device.RenderState.DestinationBlend = Blend.One;
            device.RenderState.AlphaBlendEnable = true;
            device.SetTexture(0, null);
            device.RenderState.CullMode = Cull.Clockwise;
            occlusionMesh.DrawSubset(0);
            device.RenderState.SourceBlend = Blend.SourceAlpha;
            device.RenderState.DestinationBlend = Blend.InvSourceAlpha;
            if (camLat > 0) device.RenderState.CullMode = Cull.CounterClockwise;
            device.TextureState[0].ColorOperation = TextureOperation.BlendCurrentAlpha;
            drawArgs.device.TextureState[0].ColorArgument1 = TextureArgument.TextureColor;
            drawArgs.device.TextureState[0].ColorArgument2 = TextureArgument.Diffuse;
            drawArgs.device.TextureState[0].AlphaOperation = TextureOperation.SelectArg1;
            drawArgs.device.TextureState[0].AlphaArgument1 = TextureArgument.TextureColor;
            device.SamplerState[0].MipFilter = TextureFilter.Linear;
            device.SamplerState[0].MinFilter = TextureFilter.Linear;
            device.SamplerState[0].MagFilter = TextureFilter.Linear;
            device.SetTexture(0, (camLat * sunLat < 0 && shadowsOn && showUnlitSide) ? textureInverted : texture);
            device.RenderState.ZBufferWriteEnable = false;
            layerMesh.DrawSubset(0);
            device.Transform.World = origWorld;
            if ((shadowsOn || WWSunShading) && showGlobeShadow)
            {
                if (shadowsMesh == null || sunLat != shadowsLat)
                {
                    if (shadowsMesh != null) shadowsMesh.Dispose();
                    shadowsMesh = RingShadow(drawArgs);
                    shadowsLat = sunLat;
                }
                device.Transform.World = Matrix.RotationZ((float)MathEngine.DegreesToRadians(sunLon + 180));
                Recenter(drawArgs);
                device.SetTexture(0, null);
                shadowsMesh.DrawSubset(0);
            }
            device.RenderState.CullMode = Cull.Clockwise;
            device.Transform.Projection = origProjection;
            device.Transform.World = origWorld;
            device.RenderState.ZBufferEnable = true;
            device.RenderState.ZBufferWriteEnable = true;
            device.RenderState.FogEnable = origFog;
            device.Clear(ClearFlags.ZBuffer, Color.Black, 1.0f, 0);
        }
        public void Recenter(DrawArgs drawArgs)
        {
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
                texture = TextureLoader.FromFile(drawArgs.device, Path.Combine(pluginPath, textureFileName));
                textureInverted = LoadtextureInverted(drawArgs.device, Path.Combine(pluginPath, textureFileName));
                isInitialized = true;
            }
            catch
            {
                isOn = false;
                MessageBox.Show("Error loading texture " + Path.Combine(pluginPath, textureFileName) + ".", "Layer initialization failed.", MessageBoxButtons.OK,
                        MessageBoxIcon.Error);
            }
        }
        public override void Update(DrawArgs drawArgs)
        {
            if (!isInitialized)
                Initialize(drawArgs);
        }
        public override void Dispose()
        {
            isInitialized = false;
            if (texture != null)
            {
                texture.Dispose();
                texture = null;
            }
            if (layerMesh != null)
            {
                layerMesh.Dispose();
                layerMesh = null;
            }
            if (occlusionMesh != null)
            {
                occlusionMesh.Dispose();
                occlusionMesh = null;
            }
            if (shadowsMesh != null)
            {
                shadowsMesh.Dispose();
                shadowsMesh = null;
            }
            if (nightSideMesh != null)
            {
                nightSideMesh.Dispose();
                nightSideMesh = null;
            }
            if (projMesh != null)
            {
                projMesh.Dispose();
                projMesh = null;
            }
            if (textureInverted != null)
            {
                textureInverted.Dispose();
                textureInverted = null;
            }
            if (glowMesh != null)
            {
                glowMesh.Dispose();
                glowMesh = null;
            }
        }
        public override bool PerformSelectionAction(DrawArgs drawArgs)
        {
            return false;
        }
        public override void BuildContextMenu(ContextMenu menu)
        {
            menu.MenuItems.Add("Properties", new System.EventHandler(OnPropertiesClick));
        }
        public void OnPropertiesClick(object sender, EventArgs e)
        {
            if (pDialog != null && !pDialog.IsDisposed)
                return;
            pDialog = new propertiesDialog(this);
            pDialog.Show();
        }
        public class propertiesDialog : System.Windows.Forms.Form
        {
            private System.Windows.Forms.Label lblTexture;
            private System.Windows.Forms.ComboBox cboTexture;
            private System.Windows.Forms.Label lblOn;
            private System.Windows.Forms.CheckBox chkOn;
            private System.Windows.Forms.Label lblShadows;
            private System.Windows.Forms.CheckBox chkShadows;
            private System.Windows.Forms.Label lblSunLock;
            private System.Windows.Forms.CheckBox chkSunLock;
            private System.Windows.Forms.CheckBox chkSunInvert;
            private System.Windows.Forms.CheckBox chkNightSide;
            private System.Windows.Forms.CheckBox chkGlobeShadow;
            private System.Windows.Forms.CheckBox chkRingsShadow;
            private System.Windows.Forms.CheckBox chkUnlitSide;
            private System.Windows.Forms.CheckBox chkGlow;
            private System.Windows.Forms.Button btnOK;
            private System.Windows.Forms.Button btnCancel;
            private PlanetaryRingsLayer layer;
            public propertiesDialog(PlanetaryRingsLayer layer)
            {
                this.layer = layer;
                InitializeComponent();
                DirectoryInfo di = new DirectoryInfo(layer.pluginPath);
                FileInfo[] imgFiles = di.GetFiles(layer.world.Name + "*.png");
                cboTexture.Items.AddRange(imgFiles);
                int i = cboTexture.FindString(layer.textureFileName);
                if (i != -1) cboTexture.SelectedIndex = i;
                chkOn.Checked = layer.IsOn;
                chkShadows.Checked = layer.shadowsOn;
                chkSunLock.Checked = layer.sunLocked;
                chkSunInvert.Checked = layer.sunInvert;
                chkNightSide.Checked = layer.showNightSide;
                chkGlobeShadow.Checked = layer.showGlobeShadow;
                chkRingsShadow.Checked = layer.showRingsShadow;
                chkUnlitSide.Checked = layer.showUnlitSide;
                chkGlow.Checked = layer.showGlow;
            }
            private void InitializeComponent()
            {
                this.btnCancel = new System.Windows.Forms.Button();
                this.btnOK = new System.Windows.Forms.Button();
                this.lblTexture = new System.Windows.Forms.Label();
                this.cboTexture = new System.Windows.Forms.ComboBox();
                this.lblOn = new System.Windows.Forms.Label();
                this.chkOn = new System.Windows.Forms.CheckBox();
                this.lblShadows = new System.Windows.Forms.Label();
                this.chkShadows = new System.Windows.Forms.CheckBox();
                this.lblSunLock = new System.Windows.Forms.Label();
                this.chkSunLock = new System.Windows.Forms.CheckBox();
                this.chkSunInvert = new System.Windows.Forms.CheckBox();
                this.chkNightSide = new System.Windows.Forms.CheckBox();
                this.chkGlobeShadow = new System.Windows.Forms.CheckBox();
                this.chkRingsShadow = new System.Windows.Forms.CheckBox();
                this.chkUnlitSide = new System.Windows.Forms.CheckBox();
                this.chkGlow = new System.Windows.Forms.CheckBox();
                this.SuspendLayout();
                this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
                this.btnCancel.Location = new System.Drawing.Point(311, 216);
                this.btnCancel.Name = "btnCancel";
                this.btnCancel.TabIndex = 0;
                this.btnCancel.Text = "Cancel";
                this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
                this.btnOK.Location = new System.Drawing.Point(224, 216);
                this.btnOK.Name = "btnOK";
                this.btnOK.TabIndex = 1;
                this.btnOK.Text = "OK";
                this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
                this.lblTexture.AutoSize = true;
                this.lblTexture.Location = new System.Drawing.Point(16, 28);
                this.lblTexture.Name = "lblTexture";
                this.lblTexture.Size = new System.Drawing.Size(82, 16);
                this.lblTexture.TabIndex = 2;
                this.lblTexture.Text = "Texture :";
                this.cboTexture.Location = new System.Drawing.Point(96, 25);
                this.cboTexture.Name = "cboTexture";
                this.cboTexture.Size = new System.Drawing.Size(296, 21);
                this.cboTexture.TabIndex = 3;
                this.cboTexture.Text = "Select texture file";
                this.cboTexture.DropDownStyle = ComboBoxStyle.DropDownList;
                this.cboTexture.MaxDropDownItems = 10;
                this.lblOn.AutoSize = true;
                this.lblOn.Location = new System.Drawing.Point(16, 59);
                this.lblOn.Name = "lblOn";
                this.lblOn.Size = new System.Drawing.Size(82, 16);
                this.lblOn.TabIndex = 4;
                this.lblOn.Text = "Show rings :";
                this.chkOn.Location = new System.Drawing.Point(96, 55);
                this.chkOn.Name = "chkOn";
                this.chkOn.TabIndex = 5;
                this.chkOn.Text = "Show";
                this.lblShadows.AutoSize = true;
                this.lblShadows.Location = new System.Drawing.Point(16, 90);
                this.lblShadows.Name = "lblShadows";
                this.lblShadows.Size = new System.Drawing.Size(82, 16);
                this.lblShadows.TabIndex = 6;
                this.lblShadows.Text = "Shadows :";
                this.chkShadows.Location = new System.Drawing.Point(96, 86);
                this.chkShadows.Name = "chkShadows";
                this.chkShadows.TabIndex = 7;
                this.chkShadows.Text = "Show";
                this.lblSunLock.AutoSize = true;
                this.lblSunLock.Location = new System.Drawing.Point(160, 90);
                this.lblSunLock.Name = "lblSunLock";
                this.lblSunLock.Size = new System.Drawing.Size(82, 16);
                this.lblSunLock.TabIndex = 8;
                this.lblSunLock.Text = "Sun :";
                this.chkSunLock.Location = new System.Drawing.Point(200, 86);
                this.chkSunLock.Name = "chkSunLock";
                this.chkSunLock.TabIndex = 9;
                this.chkSunLock.Text = "Fixed sun";
                this.chkSunInvert.Location = new System.Drawing.Point(320, 86);
                this.chkSunInvert.Name = "chkSunInvert";
                this.chkSunInvert.TabIndex = 10;
                this.chkSunInvert.Text = "Invert lat.";
                this.chkNightSide.Location = new System.Drawing.Point(200, 117);
                this.chkNightSide.Name = "chkNightSide";
                this.chkNightSide.TabIndex = 11;
                this.chkNightSide.Text = "Night side";
                this.chkGlobeShadow.Location = new System.Drawing.Point(320, 117);
                this.chkGlobeShadow.Name = "chkGlobeShadow";
                this.chkGlobeShadow.TabIndex = 12;
                this.chkGlobeShadow.Text = "Globe shad.";
                this.chkRingsShadow.Location = new System.Drawing.Point(200, 148);
                this.chkRingsShadow.Name = "chkRingsShadow";
                this.chkRingsShadow.TabIndex = 13;
                this.chkRingsShadow.Text = "Rings shadow";
                this.chkUnlitSide.Location = new System.Drawing.Point(320, 148);
                this.chkUnlitSide.Name = "chkUnlitSide";
                this.chkUnlitSide.TabIndex = 14;
                this.chkUnlitSide.Text = "Unlit side";
                this.chkGlow.Location = new System.Drawing.Point(200, 179);
                this.chkGlow.Name = "chkGlow";
                this.chkGlow.TabIndex = 15;
                this.chkGlow.Text = "Blue glow";
                this.AcceptButton = this.btnOK;
                this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
                this.CancelButton = this.btnCancel;
                this.ClientSize = new System.Drawing.Size(406, 255);
                this.ControlBox = false;
                this.Controls.Add(this.cboTexture);
                this.Controls.Add(this.lblTexture);
                this.Controls.Add(this.lblOn);
                this.Controls.Add(this.chkOn);
                this.Controls.Add(this.lblShadows);
                this.Controls.Add(this.chkShadows);
                this.Controls.Add(this.lblSunLock);
                this.Controls.Add(this.chkSunLock);
                this.Controls.Add(this.chkSunInvert);
                this.Controls.Add(this.chkNightSide);
                this.Controls.Add(this.chkGlobeShadow);
                this.Controls.Add(this.chkRingsShadow);
                this.Controls.Add(this.chkUnlitSide);
                this.Controls.Add(this.chkGlow);
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
                if (cboTexture.SelectedItem != null)
                {
                    layer.Dispose();
                    layer.textureFileName = cboTexture.SelectedItem.ToString();
                    layer.shadowsOn = chkShadows.Checked;
                    layer.sunLocked = chkSunLock.Checked;
                    layer.sunInvert = chkSunInvert.Checked;
                    layer.showNightSide = chkNightSide.Checked;
                    layer.showGlobeShadow = chkGlobeShadow.Checked;
                    layer.showRingsShadow = chkRingsShadow.Checked;
                    layer.showUnlitSide = chkUnlitSide.Checked;
                    layer.showGlow = chkGlow.Checked;
                    layer.Initialize(layer.drawArgs);
                    layer.IsOn = chkOn.Checked;
                    layer.SaveSettings();
                }
                this.Close();
            }
            private void btnCancel_Click(object sender, System.EventArgs e)
            {
                this.Close();
            }
        }
        private Mesh ColoredSphere(Microsoft.DirectX.Direct3D.Device device, float radius, int slices, int stacks)
        {
            int numVertices = (slices + 1) * (stacks + 1);
            int numFaces = slices * stacks * 2;
            int indexCount = numFaces * 3;
            Mesh mesh = new Mesh(numFaces, numVertices, MeshFlags.Managed, CustomVertex.PositionColored.Format, device);
            int[] ranks = new int[1];
            ranks[0] = mesh.NumberVertices;
            System.Array arr = mesh.VertexBuffer.Lock(0, typeof(CustomVertex.PositionColored), LockFlags.None, ranks);
            int vertIndex = 0;
            for (int stack = 0; stack <= stacks; stack++)
            {
                double latitude = -90 + ((float)stack / stacks * (float)180.0);
                for (int slice = 0; slice <= slices; slice++)
                {
                    CustomVertex.PositionColored pnt = new CustomVertex.PositionColored();
                    double longitude = 180 - ((float)slice / slices * (float)360);
                    Vector3 v = MathEngine.SphericalToCartesian(latitude, longitude, radius);
                    pnt.X = v.X;
                    pnt.Y = v.Y;
                    pnt.Z = v.Z;
                    pnt.Color = Color.FromArgb(0xff, 0xff, 0xff, 0xff).ToArgb();
                    arr.SetValue(pnt, vertIndex++);
                }
            }
            mesh.VertexBuffer.Unlock();
            ranks[0] = indexCount;
            arr = mesh.LockIndexBuffer(typeof(short), LockFlags.None, ranks);
            int i = 0;
            short bottomVertex = 0;
            short topVertex = 0;
            for (short x = 0; x < stacks; x++)
            {
                bottomVertex = (short)((slices + 1) * x);
                topVertex = (short)(bottomVertex + slices + 1);
                for (int y = 0; y < slices; y++)
                {
                    arr.SetValue(bottomVertex, i++);
                    arr.SetValue(topVertex, i++);
                    arr.SetValue((short)(topVertex + 1), i++);
                    arr.SetValue(bottomVertex, i++);
                    arr.SetValue((short)(topVertex + 1), i++);
                    arr.SetValue((short)(bottomVertex + 1), i++);
                    bottomVertex++;
                    topVertex++;
                }
            }
            mesh.IndexBuffer.SetData(arr, 0, LockFlags.None);
            return mesh;
        }
        private Mesh TexturedRings(Device device, float radius1, float radius2, int slices, int stacks)
        {
            int numVertices = (slices + 1) * (stacks + 1);
            int numFaces = slices * stacks * 2;
            int indexCount = numFaces * 3;
            float radiusStep = (radius2 - radius1) / stacks;
            Mesh mesh = new Mesh(numFaces, numVertices, MeshFlags.Managed, CustomVertex.PositionNormalTextured.Format, device);
            int[] ranks = new int[1];
            ranks[0] = mesh.NumberVertices;
            System.Array arr = mesh.VertexBuffer.Lock(0, typeof(CustomVertex.PositionNormalTextured), LockFlags.None, ranks);
            double latitude = 0;
            int vertIndex = 0;
            for (int stack = 0; stack <= stacks; stack++)
            {
                for (int slice = 0; slice <= slices; slice++)
                {
                    CustomVertex.PositionNormalTextured pnt = new CustomVertex.PositionNormalTextured();
                    double longitude = 180 - ((float)slice / slices * (float)360);
                    Vector3 v = MathEngine.SphericalToCartesian(latitude, longitude, radius1 + radiusStep * stack);
                    pnt.X = v.X;
                    pnt.Y = v.Y;
                    pnt.Z = v.Z;
                    pnt.Tu = (float)stack / stacks;
                    pnt.Tv = 0.0f;
                    arr.SetValue(pnt, vertIndex++);
                }
            }
            mesh.VertexBuffer.Unlock();
            ranks[0] = indexCount;
            arr = mesh.LockIndexBuffer(typeof(short), LockFlags.None, ranks);
            int i = 0;
            short bottomVertex = 0;
            short topVertex = 0;
            for (short x = 0; x < stacks; x++)
            {
                bottomVertex = (short)((slices + 1) * x);
                topVertex = (short)(bottomVertex + slices + 1);
                for (int y = 0; y < slices; y++)
                {
                    arr.SetValue(bottomVertex, i++);
                    arr.SetValue(topVertex, i++);
                    arr.SetValue((short)(topVertex + 1), i++);
                    arr.SetValue(bottomVertex, i++);
                    arr.SetValue((short)(topVertex + 1), i++);
                    arr.SetValue((short)(bottomVertex + 1), i++);
                    bottomVertex++;
                    topVertex++;
                }
            }
            mesh.IndexBuffer.SetData(arr, 0, LockFlags.None);
            mesh.ComputeNormals();
            return mesh;
        }
        private Mesh AtmoSphere(Microsoft.DirectX.Direct3D.Device device, float radius, int slices, int stacks)
        {
            int numVertices = (slices + 1) * (stacks + 1);
            int numFaces = slices * stacks * 2;
            int indexCount = numFaces * 3;
            Mesh mesh = new Mesh(numFaces, numVertices, MeshFlags.Managed, CustomVertex.PositionColored.Format, device);
            int[] ranks = new int[1];
            ranks[0] = mesh.NumberVertices;
            System.Array arr = mesh.VertexBuffer.Lock(0, typeof(CustomVertex.PositionColored), LockFlags.None, ranks);
            int vertIndex = 0;
            for (int stack = 0; stack <= stacks; stack++)
            {
                double latitude = -90 + ((float)stack / stacks * (float)180.0);
                for (int slice = 0; slice <= slices; slice++)
                {
                    CustomVertex.PositionColored pnt = new CustomVertex.PositionColored();
                    double longitude = 180 - ((float)slice / slices * (float)360);
                    Vector3 v = MathEngine.SphericalToCartesian(latitude, longitude, radius);
                    pnt.X = v.X;
                    pnt.Y = v.Y;
                    pnt.Z = v.Z;
                    double sunAngle = MathEngine.SphericalDistanceDegrees(latitude, longitude, sunLat, sunLon);
                    double alphaWinter = 0;
                    if (latitude * sunLat < 0)
                    {
                        double sinLat = Math.Sin(MathEngine.DegreesToRadians(Math.Abs(latitude)));
                        double sinSun = Math.Sin(MathEngine.DegreesToRadians(Math.Abs(sunLat)));
                        double coefLat = Math.Abs(latitude / 90);
                        alphaWinter = 255;
                        alphaWinter *= sinLat;
                        alphaWinter *= sinSun * 2;
                        if (alphaWinter > 255) alphaWinter = 255;
                        if (alphaWinter < 0) alphaWinter = 0;
                        alphaWinter *= 0.7;
                    }
                    double alphaTrans = 0;
                    alphaTrans = 100 * (1 - Math.Cos(MathEngine.DegreesToRadians(sunAngle)));
                    double alpha = Math.Max(alphaWinter, alphaTrans);
                    pnt.Color = Color.FromArgb((int)alpha, 0x10, 0x30, 0x90).ToArgb();
                    arr.SetValue(pnt, vertIndex++);
                }
            }
            mesh.VertexBuffer.Unlock();
            ranks[0] = indexCount;
            arr = mesh.LockIndexBuffer(typeof(short), LockFlags.None, ranks);
            int i = 0;
            short bottomVertex = 0;
            short topVertex = 0;
            for (short x = 0; x < stacks; x++)
            {
                bottomVertex = (short)((slices + 1) * x);
                topVertex = (short)(bottomVertex + slices + 1);
                for (int y = 0; y < slices; y++)
                {
                    arr.SetValue(bottomVertex, i++);
                    arr.SetValue(topVertex, i++);
                    arr.SetValue((short)(topVertex + 1), i++);
                    arr.SetValue(bottomVertex, i++);
                    arr.SetValue((short)(topVertex + 1), i++);
                    arr.SetValue((short)(bottomVertex + 1), i++);
                    bottomVertex++;
                    topVertex++;
                }
            }
            mesh.IndexBuffer.SetData(arr, 0, LockFlags.None);
            return mesh;
        }
        private Mesh NightSideShadow(Microsoft.DirectX.Direct3D.Device device, float radius, int slices, int stacks)
        {
            int numVertices = (slices + 1) * (stacks + 1);
            int numFaces = slices * stacks * 2;
            int indexCount = numFaces * 3;
            Mesh mesh = new Mesh(numFaces, numVertices, MeshFlags.Managed, CustomVertex.PositionColored.Format, device);
            int[] ranks = new int[1];
            ranks[0] = mesh.NumberVertices;
            System.Array arr = mesh.VertexBuffer.Lock(0, typeof(CustomVertex.PositionColored), LockFlags.None, ranks);
            int vertIndex = 0;
            float op = (float)shadowOpacity;
            for (int stack = 0; stack <= stacks; stack++)
            {
                double latitude = -10 + ((float)stack / stacks * (float)100.0);
                int alpha = shadowOpacity;
                if (stack == 0) alpha = 0;
                if (stack == 1) alpha = shadowOpacity / 6 * 1;
                if (stack == 2) alpha = shadowOpacity / 6 * 2;
                if (stack == 3) alpha = shadowOpacity / 6 * 3;
                if (stack == 4) alpha = shadowOpacity / 6 * 4;
                if (stack == 5) alpha = shadowOpacity / 6 * 5;
                if (stack == 6) alpha = (int)(op * .92);
                for (int slice = 0; slice <= slices; slice++)
                {
                    CustomVertex.PositionColored pnt = new CustomVertex.PositionColored();
                    double longitude = 180 - ((float)slice / slices * (float)360);
                    Vector3 v = MathEngine.SphericalToCartesian(latitude, longitude, radius);
                    pnt.X = v.X;
                    pnt.Y = v.Y;
                    pnt.Z = v.Z;
                    pnt.Color = Color.FromArgb(alpha, 0x00, 0x00, 0x00).ToArgb();
                    arr.SetValue(pnt, vertIndex++);
                }
            }
            mesh.VertexBuffer.Unlock();
            ranks[0] = indexCount;
            arr = mesh.LockIndexBuffer(typeof(short), LockFlags.None, ranks);
            int i = 0;
            short bottomVertex = 0;
            short topVertex = 0;
            for (short x = 0; x < stacks; x++)
            {
                bottomVertex = (short)((slices + 1) * x);
                topVertex = (short)(bottomVertex + slices + 1);
                for (int y = 0; y < slices; y++)
                {
                    arr.SetValue(bottomVertex, i++);
                    arr.SetValue(topVertex, i++);
                    arr.SetValue((short)(topVertex + 1), i++);
                    arr.SetValue(bottomVertex, i++);
                    arr.SetValue((short)(topVertex + 1), i++);
                    arr.SetValue((short)(bottomVertex + 1), i++);
                    bottomVertex++;
                    topVertex++;
                }
            }
            mesh.IndexBuffer.SetData(arr, 0, LockFlags.None);
            return mesh;
        }
        private Mesh RingShadow(DrawArgs drawArgs)
        {
            CameraBase camera = drawArgs.WorldCamera;
            Device device = drawArgs.device;
            double equatorialRadius = camera.WorldRadius;
            double polarRadius = equatorialRadius * 1.0;
            double shadowRadius = equatorialRadius * 10;
            if (sunLat != 0) shadowRadius = polarRadius / Math.Tan(MathEngine.DegreesToRadians(sunLat));
            if (shadowRadius < 0) shadowRadius *= -1;
            int stacks = 128;
            int slices = 12;
            int numVertices, numFaces;
            if (shadowRadius < outRadius)
            {
                numVertices = (slices + 1) + (stacks + 1) * 2 + 1;
                numFaces = slices + stacks * 2;
            }
            else
            {
                numVertices = (slices + 1) * 2 + (stacks + 1) * 2 + 1;
                numFaces = (slices + stacks) * 2;
            }
            int indexCount = numFaces * 3;
            Mesh mesh = new Mesh(numFaces, numVertices, MeshFlags.Managed, CustomVertex.PositionColored.Format, device);
            int[] ranks = new int[1];
            ranks[0] = mesh.NumberVertices;
            System.Array arr = mesh.VertexBuffer.Lock(0, typeof(CustomVertex.PositionColored), LockFlags.None, ranks);
            int vertIndex = 0;
            CustomVertex.PositionColored pnt;
            int shadowColor = Color.FromArgb(shadowOpacity, 0x00, 0x00, 0x00).ToArgb();
            pnt = new CustomVertex.PositionColored();
            pnt.X = (float)(inRadius + ((outRadius - inRadius) * 0.9));
            if (shadowRadius < outRadius) pnt.X = (float)(inRadius + ((shadowRadius - inRadius) * 0.9));
            pnt.Y = 0.0f;
            pnt.Z = 0.0f;
            pnt.Color = shadowColor;
            arr.SetValue(pnt, vertIndex++);
            double er2 = equatorialRadius * equatorialRadius;
            double sr2 = shadowRadius * shadowRadius;
            double inr2 = inRadius * inRadius;
            double outr2 = outRadius * outRadius;
            double p1y2 = er2 * (1 - inr2 / sr2) / (1 - er2 / sr2);
            double p1y = Math.Sqrt(p1y2);
            double p1x = Math.Sqrt(inr2 - p1y2);
            double p2x, p2y;
            if (shadowRadius < outRadius)
            {
                p2y = 0;
                p2x = shadowRadius;
            }
            else
            {
                double p2y2 = er2 * (1 - outr2 / sr2) / (1 - er2 / sr2);
                p2y = Math.Sqrt(p2y2);
                p2x = Math.Sqrt(outr2 - p2y2);
            }
            double stepX = (p2x - p1x) / stacks;
            for (int stack = 0; stack <= stacks; stack++)
            {
                double px = p1x + stepX * stack;
                double px2 = px * px;
                double py = Math.Sqrt(er2 * (1 - px2 / sr2));
                pnt = new CustomVertex.PositionColored();
                pnt.X = (float)px;
                pnt.Y = (float)py;
                pnt.Z = 0.0f;
                pnt.Color = shadowColor;
                arr.SetValue(pnt, vertIndex++);
                pnt = new CustomVertex.PositionColored();
                pnt.X = (float)px;
                pnt.Y = (float)-py;
                pnt.Z = 0.0f;
                pnt.Color = shadowColor;
                arr.SetValue(pnt, vertIndex++);
            }
            Vector3 v = MathEngine.CartesianToSpherical((float)p1x, (float)p1y, 0f);
            double p1Lon = MathEngine.RadiansToDegrees(v.Z);
            double stepLon = -p1Lon * 2 / slices;
            for (int slice = 1; slice < slices; slice++)
            {
                v = MathEngine.SphericalToCartesian(0, p1Lon + slice * stepLon, inRadius);
                pnt = new CustomVertex.PositionColored();
                pnt.X = v.X;
                pnt.Y = v.Y;
                pnt.Z = v.Z;
                pnt.Color = shadowColor;
                arr.SetValue(pnt, vertIndex++);
            }
            if (shadowRadius > outRadius)
            {
                v = MathEngine.CartesianToSpherical((float)p2x, (float)p2y, 0f);
                double p2Lon = MathEngine.RadiansToDegrees(v.Z);
                stepLon = -p2Lon * 2 / slices;
                for (int slice = 1; slice < slices; slice++)
                {
                    v = MathEngine.SphericalToCartesian(0, p2Lon + slice * stepLon, outRadius);
                    pnt = new CustomVertex.PositionColored();
                    pnt.X = v.X;
                    pnt.Y = v.Y;
                    pnt.Z = v.Z;
                    pnt.Color = shadowColor;
                    arr.SetValue(pnt, vertIndex++);
                }
            }
            mesh.VertexBuffer.Unlock();
            ranks[0] = indexCount;
            arr = mesh.LockIndexBuffer(typeof(short), LockFlags.None, ranks);
            int i = 0;
            for (short x = 0; x < stacks; x++)
            {
                arr.SetValue((short)0, i++);
                arr.SetValue((short)(x * 2 + 1), i++);
                arr.SetValue((short)(x * 2 + 3), i++);
                arr.SetValue((short)(x * 2 + 2), i++);
                arr.SetValue((short)0, i++);
                arr.SetValue((short)(x * 2 + 4), i++);
            }
            short vert = (short)((stacks - 1) * 2 + 4 + 1);
            arr.SetValue((short)0, i++);
            arr.SetValue((short)vert, i++);
            arr.SetValue((short)1, i++);
            for (short x = 1; x < slices - 1; x++)
            {
                arr.SetValue((short)0, i++);
                arr.SetValue((short)(vert + 1), i++);
                arr.SetValue((short)(vert), i++);
                vert++;
            }
            arr.SetValue((short)0, i++);
            arr.SetValue((short)2, i++);
            arr.SetValue((short)vert, i++);
            vert++;
            if (shadowRadius > outRadius)
            {
                arr.SetValue((short)0, i++);
                arr.SetValue((short)(stacks * 2 + 1), i++);
                arr.SetValue((short)vert, i++);
                for (short x = 1; x < slices - 1; x++)
                {
                    arr.SetValue((short)0, i++);
                    arr.SetValue((short)(vert), i++);
                    arr.SetValue((short)(vert + 1), i++);
                    vert++;
                }
                arr.SetValue((short)0, i++);
                arr.SetValue((short)vert, i++);
                arr.SetValue((short)(stacks * 2 + 2), i++);
            }
            mesh.IndexBuffer.SetData(arr, 0, LockFlags.None);
            return mesh;
        }
        private Mesh ProjectedRings(Device device, float radius, int slices, int stacks)
        {
            int numVertices = (slices + 1) * (stacks + 1);
            int numFaces = slices * stacks * 2;
            int indexCount = numFaces * 3;
            Mesh mesh = new Mesh(numFaces, numVertices, MeshFlags.Managed, CustomVertex.PositionColoredTextured.Format, device);
            int[] ranks = new int[1];
            ranks[0] = mesh.NumberVertices;
            System.Array arr = mesh.VertexBuffer.Lock(0, typeof(CustomVertex.PositionColoredTextured), LockFlags.None, ranks);
            int vertIndex = 0;
            for (int stack = 0; stack <= stacks; stack++)
            {
                double latitude = ((float)stack / stacks * (float)90.0);
                for (int slice = 0; slice <= slices; slice++)
                {
                    CustomVertex.PositionColoredTextured pnt = new CustomVertex.PositionColoredTextured();
                    double longitude = 180 - ((float)slice / slices * (float)360);
                    Vector3 v = MathEngine.SphericalToCartesian(latitude, longitude, radius);
                    pnt.X = v.X;
                    pnt.Y = v.Y;
                    pnt.Z = v.Z;
                    pnt.Tu = ProjectedRingsTu(v.Y, v.Z);
                    pnt.Tv = 0.0f;
                    int alpha = shadowOpacity - 40;
                    if (Math.Abs(longitude) > 90 && latitude < 80) alpha = 0;
                    if (pnt.Tu < -0.1f || pnt.Tu > 1.2) alpha = 0;
                    pnt.Color = Color.FromArgb(alpha, 0x00, 0x00, 0x00).ToArgb();
                    arr.SetValue(pnt, vertIndex++);
                }
            }
            mesh.VertexBuffer.Unlock();
            ranks[0] = indexCount;
            arr = mesh.LockIndexBuffer(typeof(short), LockFlags.None, ranks);
            int i = 0;
            short bottomVertex = 0;
            short topVertex = 0;
            for (short x = 0; x < stacks; x++)
            {
                bottomVertex = (short)((slices + 1) * x);
                topVertex = (short)(bottomVertex + slices + 1);
                for (int y = 0; y < slices; y++)
                {
                    arr.SetValue(bottomVertex, i++);
                    arr.SetValue(topVertex, i++);
                    arr.SetValue((short)(topVertex + 1), i++);
                    arr.SetValue(bottomVertex, i++);
                    arr.SetValue((short)(topVertex + 1), i++);
                    arr.SetValue((short)(bottomVertex + 1), i++);
                    bottomVertex++;
                    topVertex++;
                }
            }
            mesh.IndexBuffer.SetData(arr, 0, LockFlags.None);
            return mesh;
        }
        float ProjectedRingsTu(float y, float z)
        {
            double d = Math.Sqrt(y * y + z * z);
            double sinSunLat = Math.Sin(Math.Abs(MathEngine.DegreesToRadians(sunLat)));
            double ssl2 = sinSunLat * sinSunLat;
            double y1, z1, y2, z2;
            if (y != 0)
            {
                double k2 = (z * z) / (y * y);
                if (k2 == 0 || ssl2 == 0) return -1f;
                y1 = Math.Sqrt((inRadius * inRadius) / (1 + k2 / ssl2));
                z1 = Math.Sqrt((inRadius * inRadius) / (1 / k2 + 1 / ssl2));
                y2 = Math.Sqrt((outRadius * outRadius) / (1 + k2 / ssl2));
                z2 = Math.Sqrt((outRadius * outRadius) / (1 / k2 + 1 / ssl2));
            }
            else
            {
                y1 = 0;
                z1 = inRadius * sinSunLat;
                y2 = 0;
                z2 = outRadius * sinSunLat;
            }
            double d1 = Math.Sqrt(y1 * y1 + z1 * z1);
            double d2 = Math.Sqrt(y2 * y2 + z2 * z2);
            return (float)((d - d1) / (d2 - d1));
        }
        private Texture LoadtextureInverted(Device device, string filePath)
        {
            Bitmap b1;
            int x, y;
            b1 = new Bitmap(filePath);
            for (x = 0; x < b1.Width; x++)
            {
                for (y = 0; y < b1.Height; y++)
                {
                    Color p = b1.GetPixel(x, y);
                    b1.SetPixel(x, y, Color.FromArgb(p.A, 255 - p.R, 255 - p.G, 255 - p.B));
                }
            }
            MemoryStream stream = new MemoryStream();
            b1.Save(stream, System.Drawing.Imaging.ImageFormat.Png);
            stream.Seek(0, 0);
            Texture texture = TextureLoader.FromStream(device, stream);
            stream.Close();
            b1.Dispose();
            b1 = null;
            return texture;
        }
    }
}
