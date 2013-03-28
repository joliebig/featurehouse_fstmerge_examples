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
    public class GlobeIcon : WorldWind.PluginEngine.Plugin
    {
        public static string LayerName = "Globe Overview";
        WorldWind.WindowsControlMenuButton m_ToolbarItem;
        private Control control = new Control();
        EventHandler evhand;
        GlobeIconLayer layer;
        public override void Load()
        {
            layer = new GlobeIconLayer(LayerName, PluginDirectory, ParentApplication.WorldWindow);
            control.Visible = true;
            evhand = new EventHandler(control_VisibleChanged);
            control.VisibleChanged += evhand;
            string imgPath = Path.Combine(PluginDirectory, "GlobeIcon.png");
            if (File.Exists(imgPath) == false)
            {
                Utility.Log.Write(new Exception("imgPath not found " + imgPath));
            }
            m_ToolbarItem = new WorldWind.WindowsControlMenuButton(
                "Globe Overview",
                imgPath,
                control);
            ParentApplication.WorldWindow.MenuBar.AddToolsMenuButton(m_ToolbarItem);
            ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(layer);
            m_ToolbarItem.SetPushed(layer.IsOn);
        }
        public override void Unload()
        {
  layer.SaveSettings();
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
    public class GlobeIconLayer : RenderableObject
    {
        string version = "0.5";
        string pluginName;
        string settingsFileName = "GlobeIcon.ini";
        string pluginPath;
        public World world;
        public DrawArgs drawArgs;
        float offsetY, offsetZ;
        Texture texture;
        Mesh spriteMesh;
        Rectangle spriteSize, insetSize;
        Point spriteOffset, insetOffset;
        string spritePos = "Bottom-Right";
        string insetPos = "Bottom-Left";
        int globeRadius = 40;
        int insetWidth = 110;
        int insetHeight = 80;
        bool showGlobe = true;
        bool showInset = true;
        Form pDialog;
        public string textureFileName = "world.topo.bathy.200407.jpg";
        public string texturePath;
        public GlobeIconLayer(string LayerName, string pluginPath, WorldWindow worldWindow)
            : base(LayerName)
        {
            this.pluginPath = pluginPath;
            this.pluginName = LayerName;
            this.world = worldWindow.CurrentWorld;
            this.drawArgs = worldWindow.DrawArgs;
            this.RenderPriority = RenderPriority.Custom;
            this.texturePath = Path.Combine(MainApplication.DirectoryPath, "Data/Earth/BmngBathy");
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
            catch (Exception caught) { }
            if (line != "")
            {
                string[] settingsList = line.Split(';');
                string saveVersion = settingsList[1];
                if (settingsList[1] != null) textureFileName = settingsList[1];
                if (settingsList.Length >= 3) spritePos = settingsList[2];
                if (settingsList.Length >= 4) showGlobe = (settingsList[3] == "False") ? false : true;
                if (settingsList.Length >= 5) globeRadius = int.Parse(settingsList[4], CultureInfo.InvariantCulture);
                if (settingsList.Length >= 6) insetPos = settingsList[5];
                if (settingsList.Length >= 7) showInset = (settingsList[6] == "False") ? false : true;
                if (settingsList.Length >= 8) insetWidth = int.Parse(settingsList[7], CultureInfo.InvariantCulture);
                if (settingsList.Length >= 9) insetHeight = int.Parse(settingsList[8], CultureInfo.InvariantCulture);
    if (settingsList.Length >= 10) IsOn = bool.Parse(settingsList[9]);
            }
        }
        public void SaveSettings()
        {
            string line = version + ";"
                + textureFileName + ";"
                + spritePos + ";"
                + showGlobe.ToString() + ";"
                + globeRadius.ToString(CultureInfo.InvariantCulture) + ";"
                + insetPos + ";"
                + showInset.ToString() + ";"
                + insetWidth.ToString(CultureInfo.InvariantCulture) + ";"
                + insetHeight.ToString(CultureInfo.InvariantCulture) + ";"
    + IsOn.ToString();
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
            if (world.Name != "Earth") return;
            if (showGlobe) RenderGlobe(drawArgs);
            if (showInset) RenderInset(drawArgs);
        }
        public void RenderGlobe(DrawArgs drawArgs)
        {
            CameraBase camera = drawArgs.WorldCamera;
            Device device = drawArgs.device;
            bool origFog = device.RenderState.FogEnable;
            device.RenderState.FogEnable = false;
            if (drawArgs.device.RenderState.Lighting)
            {
                drawArgs.device.RenderState.Lighting = false;
                drawArgs.device.RenderState.Ambient = World.Settings.StandardAmbientColor;
            }
            Matrix origProjection = device.Transform.Projection;
            device.Transform.Projection = Matrix.OrthoRH((float)device.Viewport.Width, (float)device.Viewport.Height, -(float)4e6, (float)4e6);
            switch (spritePos)
            {
                case "Bottom-Left":
                    offsetY = (float)device.Viewport.Width / 2 - spriteSize.Width / 2 - 10;
                    offsetZ = (float)device.Viewport.Height / 2 - spriteSize.Height / 2 - 10;
                    break;
                case "Bottom-Center":
                    offsetY = 0;
                    offsetZ = (float)device.Viewport.Height / 2 - spriteSize.Height / 2 - 10;
                    break;
                case "Bottom-Right":
                    offsetY = -((float)device.Viewport.Width / 2 - spriteSize.Width / 2 - 60);
                    offsetZ = (float)device.Viewport.Height / 2 - spriteSize.Height / 2 - 10;
                    break;
                case "Screen-Center":
                    offsetY = 0;
                    offsetZ = 0;
                    break;
                case "Top-Right":
                    offsetY = -((float)device.Viewport.Width / 2 - spriteSize.Width / 2 - 10);
                    offsetZ = -((float)device.Viewport.Height / 2 - spriteSize.Height / 2 - 10);
                    if (World.Settings.ShowToolbar) offsetZ += 50;
                    if (World.Settings.ShowPosition) offsetZ += 140;
                    break;
                case "Top-Center":
                    offsetY = 0;
                    offsetZ = -((float)device.Viewport.Height / 2 - spriteSize.Height / 2 - 10);
                    if (World.Settings.ShowToolbar) offsetZ += 50;
                    break;
                case "Top-Left":
                    offsetY = ((float)device.Viewport.Width / 2 - spriteSize.Width / 2 - 6);
                    offsetZ = -((float)device.Viewport.Height / 2 - spriteSize.Height / 2 - 6);
                    if (World.Settings.ShowToolbar) offsetZ += 42;
                    break;
            }
            Matrix origView = device.Transform.View;
            device.Transform.View = Matrix.LookAtRH(
                new Vector3((float)1e3, offsetY, offsetZ),
                new Vector3(0, offsetY, offsetZ),
                new Vector3(0, 0, 1));
            Matrix origWorld = device.Transform.World;
            Matrix trans = Matrix.Translation(0, -(float)spriteOffset.X, (float)spriteOffset.Y);
            trans *= Matrix.RotationZ((float)camera.Longitude.Radians + (float)Math.PI);
            trans *= Matrix.RotationY(-(float)camera.Latitude.Radians);
            device.Transform.World = trans;
            if (texture != null)
            {
                if (spriteMesh == null) spriteMesh = TexturedSphere(device, (float)globeRadius, 24, 24);
                device.SetTexture(0, texture);
                device.TextureState[0].ColorOperation = TextureOperation.SelectArg1;
                device.TextureState[0].ColorArgument1 = TextureArgument.TextureColor;
                device.TextureState[0].AlphaOperation = TextureOperation.SelectArg1;
                device.TextureState[0].AlphaArgument1 = TextureArgument.TextureColor;
                spriteMesh.DrawSubset(0);
                DrawCrossHairs(drawArgs, (float)device.Viewport.Width / 2 - offsetY, (float)device.Viewport.Height / 2 + offsetZ);
            }
            device.Transform.World = origWorld;
            device.Transform.Projection = origProjection;
            device.Transform.View = origView;
            device.RenderState.ZBufferEnable = true;
            device.RenderState.FogEnable = origFog;
        }
        public void RenderInset(DrawArgs drawArgs)
        {
            CameraBase camera = drawArgs.WorldCamera;
            Device device = drawArgs.device;
            bool origFog = device.RenderState.FogEnable;
            device.RenderState.FogEnable = false;
            if (drawArgs.device.RenderState.Lighting)
            {
                drawArgs.device.RenderState.Lighting = false;
                drawArgs.device.RenderState.Ambient = World.Settings.StandardAmbientColor;
            }
            switch (insetPos)
            {
                case "Bottom-Left":
                    offsetY = (float)device.Viewport.Width / 2 - insetSize.Width / 2 - 10;
                    offsetZ = (float)device.Viewport.Height / 2 - insetSize.Height / 2 - 10;
                    break;
                case "Bottom-Center":
                    offsetY = 0;
                    offsetZ = (float)device.Viewport.Height / 2 - insetSize.Height / 2 - 10;
                    break;
                case "Bottom-Right":
                    offsetY = -((float)device.Viewport.Width / 2 - insetSize.Width / 2 - 60);
                    offsetZ = (float)device.Viewport.Height / 2 - insetSize.Height / 2 - 10;
                    break;
                case "Screen-Center":
                    offsetY = 0;
                    offsetZ = 0;
                    break;
                case "Top-Right":
                    offsetY = -((float)device.Viewport.Width / 2 - insetSize.Width / 2 - 10);
                    offsetZ = -((float)device.Viewport.Height / 2 - insetSize.Height / 2 - 10);
                    if (World.Settings.ShowToolbar) offsetZ += 50;
                    if (World.Settings.ShowPosition) offsetZ += 140;
                    break;
                case "Top-Center":
                    offsetY = 0;
                    offsetZ = -((float)device.Viewport.Height / 2 - insetSize.Height / 2 - 10);
                    if (World.Settings.ShowToolbar) offsetZ += 50;
                    break;
                case "Top-Left":
                    offsetY = ((float)device.Viewport.Width / 2 - insetSize.Width / 2 - 6);
                    offsetZ = -((float)device.Viewport.Height / 2 - insetSize.Height / 2 - 4);
                    if (World.Settings.ShowToolbar) offsetZ += 44;
                    break;
            }
            Matrix origView = device.Transform.View;
            device.Transform.View = Matrix.LookAtRH(
                new Vector3((float)1e3, 0, 0),
                new Vector3(0, 0, 0),
                new Vector3(0, 0, 1));
            Matrix origWorld = device.Transform.World;
            Matrix trans = Matrix.Translation(0, -(float)insetOffset.X, (float)insetOffset.Y);
            trans *= Matrix.RotationZ((float)camera.Longitude.Radians + (float)Math.PI);
            trans *= Matrix.RotationY(-(float)camera.Latitude.Radians);
            device.Transform.World = trans;
            Viewport origVp = device.Viewport;
            Viewport viewPort = new Viewport();
            viewPort.Width = insetSize.Width;
            viewPort.Height = insetSize.Height;
            viewPort.X = device.Viewport.Width / 2 - (int)offsetY - viewPort.Width / 2;
            viewPort.Y = device.Viewport.Height / 2 + (int)offsetZ - viewPort.Height / 2;
            device.Viewport = viewPort;
            float topAlt = 4000e3f; float topFactor = 4f;
            float botAlt = 200e3f; float botFactor = 14f;
            float zoomFactor = botFactor + ((topFactor - botFactor) / (topAlt - botAlt) * ((float)camera.Altitude - botAlt));
            if (zoomFactor > botFactor) zoomFactor = botFactor;
            Matrix origProjection = device.Transform.Projection;
            device.Transform.Projection = Matrix.OrthoRH((float)device.Viewport.Width / zoomFactor, (float)device.Viewport.Height / zoomFactor, -(float)4e6, (float)4e6);
            if (texture != null && zoomFactor > topFactor)
            {
                device.RenderState.ZBufferEnable = false;
                if (spriteMesh == null) spriteMesh = TexturedSphere(device, (float)globeRadius, 24, 24);
                device.SetTexture(0, texture);
                device.TextureState[0].ColorOperation = TextureOperation.SelectArg1;
                device.TextureState[0].ColorArgument1 = TextureArgument.TextureColor;
                device.TextureState[0].AlphaOperation = TextureOperation.SelectArg1;
                device.TextureState[0].AlphaArgument1 = TextureArgument.TextureColor;
                spriteMesh.DrawSubset(0);
            }
            device.Transform.World = origWorld;
            device.Transform.Projection = origProjection;
            device.Transform.View = origView;
            device.RenderState.ZBufferEnable = true;
            device.RenderState.FogEnable = origFog;
            device.Viewport = origVp;
            if (texture != null && zoomFactor > topFactor)
            {
                DrawCrossHairs(drawArgs, (float)device.Viewport.Width / 2 - offsetY, (float)device.Viewport.Height / 2 + offsetZ);
                DrawRectangle(drawArgs, (float)viewPort.X, (float)viewPort.Y, (float)viewPort.Width, (float)viewPort.Height);
            }
        }
        int crossHairColor = Color.GhostWhite.ToArgb();
        CustomVertex.TransformedColored[] vertical = new CustomVertex.TransformedColored[4];
        CustomVertex.TransformedColored[] horizontal = new CustomVertex.TransformedColored[4];
        public void DrawCrossHairs(DrawArgs drawArgs, float X, float Y)
        {
            Device device = drawArgs.device;
            int crossHairSize = 10;
            int crossHairGap = 3;
            horizontal[0].X = X - crossHairSize;
            horizontal[0].Y = Y;
            horizontal[0].Z = 0.5f;
            horizontal[0].Color = crossHairColor;
            horizontal[1].X = X - crossHairGap;
            horizontal[1].Y = Y;
            horizontal[1].Z = 0.5f;
            horizontal[1].Color = crossHairColor;
            horizontal[2].X = X + crossHairGap;
            horizontal[2].Y = Y;
            horizontal[2].Z = 0.5f;
            horizontal[2].Color = crossHairColor;
            horizontal[3].X = X + crossHairSize;
            horizontal[3].Y = Y;
            horizontal[3].Z = 0.5f;
            horizontal[3].Color = crossHairColor;
            vertical[0].X = X;
            vertical[0].Y = Y - crossHairSize;
            vertical[0].Z = 0.5f;
            vertical[0].Color = crossHairColor;
            vertical[1].X = X;
            vertical[1].Y = Y - crossHairGap;
            vertical[1].Z = 0.5f;
            vertical[1].Color = crossHairColor;
            vertical[2].X = X;
            vertical[2].Y = Y + crossHairGap;
            vertical[2].Z = 0.5f;
            vertical[2].Color = crossHairColor;
            vertical[3].X = X;
            vertical[3].Y = Y + crossHairSize;
            vertical[3].Z = 0.5f;
            vertical[3].Color = crossHairColor;
            drawArgs.device.VertexFormat = CustomVertex.TransformedColored.Format;
            drawArgs.device.TextureState[0].ColorOperation = TextureOperation.Disable;
            drawArgs.device.DrawUserPrimitives(PrimitiveType.LineList, 4, horizontal);
            drawArgs.device.DrawUserPrimitives(PrimitiveType.LineList, 4, vertical);
        }
        int rectangleColor = Color.GhostWhite.ToArgb();
        CustomVertex.TransformedColored[] rectangleVertices = new CustomVertex.TransformedColored[5];
        public void DrawRectangle(DrawArgs drawArgs, float X, float Y, float Width, float Height)
        {
            Device device = drawArgs.device;
            rectangleVertices[0].X = X;
            rectangleVertices[0].Y = Y;
            rectangleVertices[0].Z = 0.5f;
            rectangleVertices[0].Color = rectangleColor;
            rectangleVertices[1].X = X + Width;
            rectangleVertices[1].Y = Y;
            rectangleVertices[1].Z = 0.5f;
            rectangleVertices[1].Color = rectangleColor;
            rectangleVertices[2].X = X + Width;
            rectangleVertices[2].Y = Y + Height;
            rectangleVertices[2].Z = 0.5f;
            rectangleVertices[2].Color = rectangleColor;
            rectangleVertices[3].X = X;
            rectangleVertices[3].Y = Y + Height;
            rectangleVertices[3].Z = 0.5f;
            rectangleVertices[3].Color = rectangleColor;
            rectangleVertices[4].X = X;
            rectangleVertices[4].Y = Y;
            rectangleVertices[4].Z = 0.5f;
            rectangleVertices[4].Color = rectangleColor;
            drawArgs.device.VertexFormat = CustomVertex.TransformedColored.Format;
            drawArgs.device.TextureState[0].ColorOperation = TextureOperation.Disable;
            drawArgs.device.DrawUserPrimitives(PrimitiveType.LineStrip, rectangleVertices.Length - 1, rectangleVertices);
        }
        public override void Initialize(DrawArgs drawArgs)
        {
            Device device = drawArgs.device;
            try
            {
                texture = TextureLoader.FromFile(device, Path.Combine(texturePath, textureFileName));
                isInitialized = true;
            }
            catch
            {
                isOn = false;
                MessageBox.Show("Error loading texture " + Path.Combine(texturePath, textureFileName) + ".", "Layer initialization failed.", MessageBoxButtons.OK,
                    MessageBoxIcon.Error);
            }
            spriteSize = new Rectangle(0, 0, globeRadius * 2, globeRadius * 2);
            spriteOffset = new Point(0, 0);
            insetSize = new Rectangle(0, 0, insetWidth, insetHeight);
            insetOffset = new Point(0, 0);
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
            if (spriteMesh != null)
            {
                spriteMesh.Dispose();
                spriteMesh = null;
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
        public new void OnPropertiesClick(object sender, EventArgs e)
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
            private System.Windows.Forms.Label lblGlobe;
            private System.Windows.Forms.CheckBox chkGlobe;
            private System.Windows.Forms.Label lblPosition;
            private System.Windows.Forms.ComboBox cboPosition;
            private System.Windows.Forms.Label lblSize;
            private System.Windows.Forms.ComboBox cboSize;
            private System.Windows.Forms.Label lblInset;
            private System.Windows.Forms.CheckBox chkInset;
            private System.Windows.Forms.Label lblPosition2;
            private System.Windows.Forms.ComboBox cboPosition2;
            private System.Windows.Forms.Label lblSize2;
            private System.Windows.Forms.ComboBox cboSize2;
            private System.Windows.Forms.Button btnOK;
            private System.Windows.Forms.Button btnCancel;
            private GlobeIconLayer layer;
            public propertiesDialog(GlobeIconLayer layer)
            {
                InitializeComponent();
                this.layer = layer;
                this.Text = layer.pluginName + " " + layer.version + " properties";
                DirectoryInfo di = new DirectoryInfo(layer.texturePath);
                FileInfo[] imgFiles = di.GetFiles("*.jpg");
                cboTexture.Items.AddRange(imgFiles);
                imgFiles = di.GetFiles("*.png");
                cboTexture.Items.AddRange(imgFiles);
                int i = cboTexture.FindString(layer.textureFileName);
                if (i != -1) cboTexture.SelectedIndex = i;
                chkGlobe.Checked = layer.showGlobe;
                cboPosition.Items.Add("Top-Left");
                cboPosition.Items.Add("Top-Center");
                cboPosition.Items.Add("Top-Right");
                cboPosition.Items.Add("Bottom-Left");
                cboPosition.Items.Add("Bottom-Center");
                cboPosition.Items.Add("Bottom-Right");
                cboPosition.Items.Add("Screen-Center");
                i = cboPosition.FindString(layer.spritePos);
                if (i != -1) cboPosition.SelectedIndex = i;
                cboSize.Items.Add("64x64");
                cboSize.Items.Add("80x80");
                cboSize.Items.Add("100x100");
                cboSize.Items.Add("128x128");
                i = cboSize.FindString((layer.globeRadius * 2).ToString() + "x" + (layer.globeRadius * 2).ToString());
                if (i != -1) cboSize.SelectedIndex = i;
                chkInset.Checked = layer.showInset;
                cboPosition2.Items.Add("Top-Left");
                cboPosition2.Items.Add("Top-Center");
                cboPosition2.Items.Add("Top-Right");
                cboPosition2.Items.Add("Bottom-Left");
                cboPosition2.Items.Add("Bottom-Center");
                cboPosition2.Items.Add("Bottom-Right");
                cboPosition2.Items.Add("Screen-Center");
                i = cboPosition2.FindString(layer.insetPos);
                if (i != -1) cboPosition2.SelectedIndex = i;
                cboSize2.Items.Add("64x64");
                cboSize2.Items.Add("100x64");
                cboSize2.Items.Add("64x100");
                cboSize2.Items.Add("80x80");
                cboSize2.Items.Add("110x80");
                cboSize2.Items.Add("80x110");
                cboSize2.Items.Add("100x100");
                cboSize2.Items.Add("133x100");
                cboSize2.Items.Add("100x133");
                cboSize2.Items.Add("128x128");
                cboSize2.Items.Add("160x128");
                cboSize2.Items.Add("128x160");
                i = cboSize2.FindString(layer.insetWidth.ToString() + "x" + layer.insetHeight.ToString());
                if (i != -1) cboSize2.SelectedIndex = i;
            }
            private void InitializeComponent()
            {
                this.btnCancel = new System.Windows.Forms.Button();
                this.btnOK = new System.Windows.Forms.Button();
                this.lblTexture = new System.Windows.Forms.Label();
                this.cboTexture = new System.Windows.Forms.ComboBox();
                this.lblGlobe = new System.Windows.Forms.Label();
                this.chkGlobe = new System.Windows.Forms.CheckBox();
                this.lblPosition = new System.Windows.Forms.Label();
                this.cboPosition = new System.Windows.Forms.ComboBox();
                this.lblSize = new System.Windows.Forms.Label();
                this.cboSize = new System.Windows.Forms.ComboBox();
                this.lblInset = new System.Windows.Forms.Label();
                this.chkInset = new System.Windows.Forms.CheckBox();
                this.lblPosition2 = new System.Windows.Forms.Label();
                this.cboPosition2 = new System.Windows.Forms.ComboBox();
                this.lblSize2 = new System.Windows.Forms.Label();
                this.cboSize2 = new System.Windows.Forms.ComboBox();
                this.SuspendLayout();
                this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
                this.btnCancel.Location = new System.Drawing.Point(311, 199);
                this.btnCancel.Name = "btnCancel";
                this.btnCancel.TabIndex = 0;
                this.btnCancel.Text = "Cancel";
                this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
                this.btnOK.Location = new System.Drawing.Point(224, 199);
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
                this.cboTexture.Text = "Select image file";
                this.cboTexture.DropDownStyle = ComboBoxStyle.DropDownList;
                this.cboTexture.MaxDropDownItems = 10;
                this.lblGlobe.AutoSize = true;
                this.lblGlobe.Location = new System.Drawing.Point(16, 59);
                this.lblGlobe.Name = "lblGlobe";
                this.lblGlobe.Size = new System.Drawing.Size(82, 16);
                this.lblGlobe.TabIndex = 4;
                this.lblGlobe.Text = "Globe :";
                this.chkGlobe.Location = new System.Drawing.Point(96, 55);
                this.chkGlobe.Name = "chkGlobe";
                this.chkGlobe.TabIndex = 5;
                this.chkGlobe.Text = "Display";
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
                this.lblSize.AutoSize = true;
                this.lblSize.Location = new System.Drawing.Point(220, 90);
                this.lblSize.Name = "lblSize";
                this.lblSize.Size = new System.Drawing.Size(82, 16);
                this.lblSize.TabIndex = 8;
                this.lblSize.Text = "Size :";
                this.cboSize.Location = new System.Drawing.Point(292, 87);
                this.cboSize.Name = "cboSize";
                this.cboSize.Size = new System.Drawing.Size(100, 21);
                this.cboSize.TabIndex = 9;
                this.cboSize.Text = "Select size";
                this.cboSize.DropDownStyle = ComboBoxStyle.DropDownList;
                this.cboSize.MaxDropDownItems = 10;
                this.lblInset.AutoSize = true;
                this.lblInset.Location = new System.Drawing.Point(16, 121);
                this.lblInset.Name = "lblInset";
                this.lblInset.Size = new System.Drawing.Size(82, 16);
                this.lblInset.TabIndex = 10;
                this.lblInset.Text = "Top view :";
                this.chkInset.Location = new System.Drawing.Point(96, 117);
                this.chkInset.Name = "chkInset";
                this.chkInset.TabIndex = 11;
                this.chkInset.Text = "Display";
                this.lblPosition2.AutoSize = true;
                this.lblPosition2.Location = new System.Drawing.Point(220, 121);
                this.lblPosition2.Name = "lblPosition2";
                this.lblPosition2.Size = new System.Drawing.Size(82, 16);
                this.lblPosition2.TabIndex = 12;
                this.lblPosition2.Text = "Placement :";
                this.cboPosition2.Location = new System.Drawing.Point(292, 118);
                this.cboPosition2.Name = "cboPosition2";
                this.cboPosition2.Size = new System.Drawing.Size(100, 21);
                this.cboPosition2.TabIndex = 13;
                this.cboPosition2.Text = "Select placement";
                this.cboPosition2.DropDownStyle = ComboBoxStyle.DropDownList;
                this.cboPosition2.MaxDropDownItems = 10;
                this.lblSize2.AutoSize = true;
                this.lblSize2.Location = new System.Drawing.Point(220, 152);
                this.lblSize2.Name = "lblSize2";
                this.lblSize2.Size = new System.Drawing.Size(82, 16);
                this.lblSize2.TabIndex = 14;
                this.lblSize2.Text = "Size :";
                this.cboSize2.Location = new System.Drawing.Point(292, 149);
                this.cboSize2.Name = "cboSize2";
                this.cboSize2.Size = new System.Drawing.Size(100, 21);
                this.cboSize2.TabIndex = 15;
                this.cboSize2.Text = "Select size";
                this.cboSize2.DropDownStyle = ComboBoxStyle.DropDownList;
                this.cboSize2.MaxDropDownItems = 10;
                this.AcceptButton = this.btnOK;
                this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
                this.CancelButton = this.btnCancel;
                this.ClientSize = new System.Drawing.Size(406, 236);
                this.ControlBox = false;
                this.Controls.Add(this.cboTexture);
                this.Controls.Add(this.lblTexture);
                this.Controls.Add(this.lblGlobe);
                this.Controls.Add(this.chkGlobe);
                this.Controls.Add(this.cboPosition);
                this.Controls.Add(this.lblPosition);
                this.Controls.Add(this.cboSize);
                this.Controls.Add(this.lblSize);
                this.Controls.Add(this.lblInset);
                this.Controls.Add(this.chkInset);
                this.Controls.Add(this.cboPosition2);
                this.Controls.Add(this.lblPosition2);
                this.Controls.Add(this.cboSize2);
                this.Controls.Add(this.lblSize2);
                this.Controls.Add(this.btnOK);
                this.Controls.Add(this.btnCancel);
                this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
                this.MaximizeBox = false;
                this.MinimizeBox = false;
                this.Name = "pDialog";
                this.ShowInTaskbar = false;
                this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
                this.Text = "Layer properties";
                this.TopMost = true;
                this.ResumeLayout(false);
            }
            private void btnOK_Click(object sender, System.EventArgs e)
            {
                if (cboTexture.SelectedItem != null)
                {
                    string[] size;
                    layer.Dispose();
                    layer.textureFileName = cboTexture.SelectedItem.ToString();
                    layer.showGlobe = chkGlobe.Checked;
                    layer.spritePos = cboPosition.SelectedItem.ToString();
                    size = cboSize.SelectedItem.ToString().Split('x');
                    layer.globeRadius = int.Parse(size[0]) / 2;
                    layer.showInset = chkInset.Checked;
                    layer.insetPos = cboPosition2.SelectedItem.ToString();
                    size = cboSize2.SelectedItem.ToString().Split('x');
                    layer.insetWidth = int.Parse(size[0]);
                    layer.insetHeight = int.Parse(size[1]);
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
        private Mesh TexturedSphere(Device device, float radius, int slices, int stacks)
        {
            int numVertices = (slices + 1) * (stacks + 1);
            int numFaces = slices * stacks * 2;
            int indexCount = numFaces * 3;
            Mesh mesh = new Mesh(numFaces, numVertices, MeshFlags.Managed, CustomVertex.PositionNormalTextured.Format, device);
            int[] ranks = new int[1];
            ranks[0] = mesh.NumberVertices;
            System.Array arr = mesh.VertexBuffer.Lock(0, typeof(CustomVertex.PositionNormalTextured), LockFlags.None, ranks);
            int vertIndex = 0;
            for (int stack = 0; stack <= stacks; stack++)
            {
                double latitude = -90 + ((float)stack / stacks * (float)180.0);
                for (int slice = 0; slice <= slices; slice++)
                {
                    CustomVertex.PositionNormalTextured pnt = new CustomVertex.PositionNormalTextured();
                    double longitude = 180 - ((float)slice / slices * (float)360);
                    Vector3 v = MathEngine.SphericalToCartesian(latitude, longitude, radius);
                    pnt.X = v.X;
                    pnt.Y = v.Y;
                    pnt.Z = v.Z;
                    pnt.Tu = (float)slice / slices;
                    pnt.Tv = 1.0f - (float)stack / stacks;
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
                    arr.SetValue((short)(topVertex + 1), i++);
                    arr.SetValue(topVertex, i++);
                    arr.SetValue(bottomVertex, i++);
                    arr.SetValue((short)(bottomVertex + 1), i++);
                    arr.SetValue((short)(topVertex + 1), i++);
                    bottomVertex++;
                    topVertex++;
                }
            }
            mesh.IndexBuffer.SetData(arr, 0, LockFlags.None);
            mesh.ComputeNormals();
            return mesh;
        }
    }
}
