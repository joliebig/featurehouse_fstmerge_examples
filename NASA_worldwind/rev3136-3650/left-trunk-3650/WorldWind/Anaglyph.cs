using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Drawing;
using System.Windows.Forms;
using WorldWind;
using WorldWind.Camera;
using WorldWind.Renderable;
namespace Mashi.Stereo
{
    public class AnaglyphStereo : WorldWind.PluginEngine.Plugin
    {
        public static string LayerName = "Anaglyph Stereo 3D";
        System.Windows.Forms.MenuItem menuItem;
        StereoLayer layer;
        public override void Load()
        {
            menuItem = new System.Windows.Forms.MenuItem();
            menuItem.Text = "Anaglyph 3D View Mode";
            menuItem.Click += new System.EventHandler(menuItem_Click);
            ParentApplication.ViewMenu.MenuItems.Add(menuItem);
            Caps caps = Application.WorldWindow.DrawArgs.device.DeviceCaps;
            if (!caps.DestinationBlendCaps.SupportsBlendFactor ||
                    !caps.SourceBlendCaps.SupportsBlendFactor)
            {
                throw new ApplicationException("The graphics adapter is not compatible, no blend factor support.");
            }
            layer = new StereoLayer(LayerName, Application.WorldWindow);
            Application.WorldWindow.CurrentWorld.RenderableObjects.Add(layer);
            layer.IsOn = false;
        }
        public override void Unload()
        {
            if (menuItem != null)
            {
                ParentApplication.ViewMenu.MenuItems.Remove(menuItem);
                menuItem.Dispose();
                menuItem = null;
            }
            if (layer != null)
            {
                Application.WorldWindow.CurrentWorld.RenderableObjects.Remove(layer);
                layer.Dispose();
                layer = null;
            }
        }
        void menuItem_Click(object sender, EventArgs e)
        {
            layer.IsOn = !layer.IsOn;
            menuItem.Checked = layer.IsOn;
        }
    }
    public class StereoLayer : RenderableObject
    {
        WorldWindow worldWin;
        CustomVertex.TransformedTextured[] windowQuad = new CustomVertex.TransformedTextured[4];
        Texture m_rightTexture;
        float m_interocularDistance = 1f;
        float m_focusAngle = 0.6f;
        bool isRendering;
        public StereoLayer(string LayerName, WorldWindow worldWindow)
            : base(LayerName)
        {
            this.RenderPriority = RenderPriority.Icons;
            this.worldWin = worldWindow;
            worldWindow.DrawArgs.device.DeviceLost += new EventHandler(OnDeviceLost);
        }
        [Browsable(false)]
        public override byte Opacity
        {
            get
            {
                return base.Opacity;
            }
            set
            {
                base.Opacity = value;
            }
        }
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
                    MessageBox.Show("Prolonged use of 3D glasses may lead to problems such as headaches, dizziness, or disorientation.\nTo avoid symptoms, take rest periods to allow your eyes to recover between uses.", "Warning!");
                }
            }
        }
        [Description("The relative distance separating the two eyes.")]
        public float InterocularDistance
        {
            get { return m_interocularDistance; }
            set { m_interocularDistance = value; }
        }
        [Description("The relative distance separating the two eyes.")]
        public float FocusAngle
        {
            get { return m_focusAngle; }
            set { m_focusAngle = value; }
        }
        public override void Render(DrawArgs drawArgs)
        {
            if (!isInitialized)
                Initialize(drawArgs);
            if (!isInitialized)
                return;
            if (isRendering)
                return;
            isRendering = true;
            try
            {
                Device device = drawArgs.device;
                CameraBase camera = drawArgs.WorldCamera;
                Matrix View = camera.ViewMatrix;
                float eyeDist = m_interocularDistance / 100 * (float)camera.Distance;
                device.Transform.View *= Matrix.Translation(eyeDist, 0f, 0f);
                device.Transform.View *= Matrix.RotationY((float)MathEngine.DegreesToRadians(m_focusAngle));
                RenderRightEye(drawArgs, m_rightTexture, camera);
                device.RenderState.ZBufferEnable = false;
                device.VertexFormat = CustomVertex.TransformedTextured.Format;
                device.TextureState[0].ColorOperation = TextureOperation.SelectArg1;
                device.TextureState[0].ColorArgument1 = TextureArgument.TextureColor;
                device.RenderState.ColorWriteEnable = ColorWriteEnable.Green | ColorWriteEnable.Blue;
                device.SetTexture(0, m_rightTexture);
                device.DrawUserPrimitives(PrimitiveType.TriangleStrip, 2, windowQuad);
                device.SetTexture(0, null);
                device.RenderState.ColorWriteEnable = ColorWriteEnable.RedGreenBlueAlpha;
            }
            finally
            {
                isRendering = false;
            }
        }
        void RenderRightEye(DrawArgs drawArgs, Texture t, CameraBase camera)
        {
            Device device = drawArgs.device;
            using (Surface outputSurface = t.GetSurfaceLevel(0))
            {
                Surface backBuffer = device.GetRenderTarget(0);
                device.SetRenderTarget(0, outputSurface);
                System.Drawing.Color backgroundColor = System.Drawing.Color.Black;
                device.Clear(ClearFlags.Target | ClearFlags.ZBuffer, backgroundColor, 1.0f, 0);
                worldWin.CurrentWorld.Render(drawArgs);
                device.SetRenderTarget(0, backBuffer);
            }
        }
        public override void Initialize(DrawArgs drawArgs)
        {
            Device device = drawArgs.device;
            try
            {
                SurfaceDescription backBufferDescription;
                using (Surface backBuffer = device.GetBackBuffer(0, 0, BackBufferType.Mono))
                    backBufferDescription = backBuffer.Description;
                windowQuad[0].Y = backBufferDescription.Height;
                windowQuad[0].Tv = 1;
                windowQuad[1].X = backBufferDescription.Width;
                windowQuad[1].Y = backBufferDescription.Height;
                windowQuad[1].Tu = 1;
                windowQuad[1].Tv = 1;
                windowQuad[3].X = backBufferDescription.Width;
                windowQuad[3].Tu = 1;
                if (m_rightTexture == null || m_rightTexture.Disposed)
                    m_rightTexture = new Texture(drawArgs.device,
                            backBufferDescription.Width, backBufferDescription.Height,
                            1, Usage.RenderTarget, backBufferDescription.Format, Pool.Default);
                isInitialized = true;
            }
            catch (DirectXException)
            {
                Dispose();
            }
        }
        public override void Update(DrawArgs drawArgs)
        {
        }
        public override void Dispose()
        {
            isInitialized = false;
            if (m_rightTexture != null)
            {
                m_rightTexture.Dispose();
                m_rightTexture = null;
            }
        }
        public override bool PerformSelectionAction(DrawArgs drawArgs)
        {
            return false;
        }
        public void OnDeviceLost(object sender, EventArgs e)
        {
            Dispose();
        }
    }
}
