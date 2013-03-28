using System;
using System.IO;
using System.Xml;
using System.Xml.XPath;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Renderable;
namespace WorldWind
{
    public class WavingFlags : WorldWind.PluginEngine.Plugin
    {
        string DataFileUri = "http://issues.worldwind.arc.nasa.gov/confluence/download/attachments/425/cia2006data.txt";
        string FlagTextureDirectoryUri = "http://worldwind26.arc.nasa.gov/fotw/256x256";
        string FlagSuffix = "-lgflag.dds";
        string SavedFlagsDirectory = System.IO.Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath) + "\\Plugins\\FOTW\\Textures";
        string SavedFilePath = System.IO.Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath) + "\\Plugins\\FOTW\\fotw.txt";
        RenderableObjectList m_wavingFlagsList = null;
        public override void Load()
        {
            FileInfo savedFile = new FileInfo(SavedFilePath);
            if (!savedFile.Exists)
            {
                if (!savedFile.Directory.Exists)
                    savedFile.Directory.Create();
                try
                {
                    WorldWind.Net.WebDownload download = new WorldWind.Net.WebDownload(DataFileUri);
                    download.DownloadFile(savedFile.FullName);
                    download.Dispose();
                }
                catch { }
            }
            m_wavingFlagsList = new RenderableObjectList("Waving Flags");
            using (StreamReader reader = savedFile.OpenText())
            {
                string header = reader.ReadLine();
                string line = reader.ReadLine();
                while (line != null)
                {
                    string[] lineParts = line.Split('\t');
                    try
                    {
                        double latitude = double.Parse(lineParts[3], System.Globalization.CultureInfo.InvariantCulture);
                        double longitude = double.Parse(lineParts[4], System.Globalization.CultureInfo.InvariantCulture);
                        if (lineParts[1].Length == 2)
                        {
                            string flagFileUri = FlagTextureDirectoryUri + "/" + lineParts[1] + FlagSuffix;
                            FileInfo savedFlagFile = new FileInfo(SavedFlagsDirectory + "\\" + lineParts[1] + ".dds");
                            WavingFlagLayer flag = new WavingFlagLayer(
                                lineParts[0],
                                ParentApplication.WorldWindow.CurrentWorld,
                                latitude,
                                longitude,
                                flagFileUri);
                            flag.SavedImagePath = savedFlagFile.FullName;
                            m_wavingFlagsList.Add(flag);
                        }
                        else
                        {
                        }
                    }
                    catch
                    {
                    }
                    line = reader.ReadLine();
                }
            }
            ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(m_wavingFlagsList);
        }
        public override void Unload()
        {
            if (m_wavingFlagsList != null)
            {
                ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Remove(m_wavingFlagsList.Name);
                m_wavingFlagsList.Dispose();
                m_wavingFlagsList = null;
            }
        }
    }
    public class WavingFlagLayer : RenderableObject
    {
        double m_latitude = 0, m_longitude = 0;
        string m_imageUri = null;
        PolygonFeature m_polygonFeature = null;
        LineFeature m_lineFeature = null;
        public string SavedImagePath = null;
        public WavingFlagLayer(
            string name,
            World parentWorld,
            double latitude,
            double longitude,
            string imageUri
            )
            : base(name, parentWorld)
        {
            m_latitude = latitude;
            m_longitude = longitude;
            m_imageUri = imageUri;
        }
        public override void Initialize(DrawArgs drawArgs)
        {
            if (m_polygonFeature == null)
            {
                double offset = 0.02;
                Point3d[] points = new Point3d[4];
                points[0] = new Point3d(
                    m_longitude - offset,
                    m_latitude - offset,
                    200000);
                points[1] = new Point3d(
                    m_longitude - offset,
                    m_latitude + offset,
                    200000);
                points[2] = new Point3d(
                    m_longitude + offset,
                    m_latitude + offset,
                    200000);
                points[3] = new Point3d(
                    m_longitude + offset,
                    m_latitude - offset,
                    200000);
                LinearRing outerRing = new LinearRing();
                outerRing.Points = points;
                m_polygonFeature = new PolygonFeature(
                    name,
                    World,
                    outerRing,
                    null,
                    System.Drawing.Color.Chocolate);
                m_polygonFeature.AltitudeMode = AltitudeMode.Absolute;
                m_polygonFeature.Extrude = true;
                m_polygonFeature.Outline = true;
                m_polygonFeature.OutlineColor = System.Drawing.Color.Chocolate;
            }
            FileInfo savedFlagFile = new FileInfo(SavedImagePath);
            FileInfo placeHolderFile = new FileInfo(SavedImagePath + ".blk");
            if (!savedFlagFile.Exists && !placeHolderFile.Exists)
            {
                if (!savedFlagFile.Directory.Exists)
                    savedFlagFile.Directory.Create();
                try
                {
                    WorldWind.Net.WebDownload download = new WorldWind.Net.WebDownload(m_imageUri);
                    download.DownloadFile(savedFlagFile.FullName);
                    download.Dispose();
                    savedFlagFile.Refresh();
                }
                catch
                {
                    FileStream fs = placeHolderFile.Create();
                    fs.Close();
                    fs = null;
                    placeHolderFile.Refresh();
                }
            }
            if (savedFlagFile.Exists)
            {
                m_texture = ImageHelper.LoadTexture(
                    savedFlagFile.FullName,
                    System.Drawing.Color.Black.ToArgb());
                UpdateVertices();
            }
            isInitialized = true;
        }
        public override void Dispose()
        {
        }
        float angle = 0;
        private void UpdateVertices()
        {
            if (m_vertices != null)
                return;
            int vertexWidth = 32;
            int vertexHeight = 32;
            m_vertices = new CustomVertex.PositionNormalTextured[(vertexWidth + 1) * (vertexHeight + 1)];
            for (int y = 0; y <= vertexHeight; y++)
            {
                for (int x = 0; x <= vertexWidth; x++)
                {
                    m_vertices[y * (vertexWidth + 1) + x].X = (float)x / (float)vertexWidth;
                    m_vertices[y * (vertexWidth + 1) + x].Y = (float)y / (float)vertexHeight;
                    m_vertices[y * (vertexWidth + 1) + x].Z = 0;
                    m_vertices[y * (vertexWidth + 1) + x].Nx = 0;
                    m_vertices[y * (vertexWidth + 1) + x].Ny = 0;
                    m_vertices[y * (vertexWidth + 1) + x].Nz = 1;
                    m_vertices[y * (vertexWidth + 1) + x].Z *= m_vertices[y * (vertexWidth + 1) + x].Y * 0.09f;
                    m_vertices[y * (vertexWidth + 1) + x].Tu = m_vertices[y * (vertexWidth + 1) + x].Y;
                    m_vertices[y * (vertexWidth + 1) + x].Tv = 1.0f - m_vertices[y * (vertexWidth + 1) + x].X;
                }
            }
            m_indices = new short[2 * vertexWidth * vertexHeight * 3];
            int baseIndex = 0;
            for (int i = 0; i < vertexHeight; i++)
            {
                baseIndex = (2 * 3 * i * vertexWidth);
                for (int j = 0; j < vertexWidth; j++)
                {
                    m_indices[baseIndex] = (short)(i * (vertexWidth + 1) + j);
                    m_indices[baseIndex + 1] = (short)((i + 1) * (vertexWidth + 1) + j);
                    m_indices[baseIndex + 2] = (short)(i * (vertexWidth + 1) + j + 1);
                    m_indices[baseIndex + 3] = (short)(i * (vertexWidth + 1) + j + 1);
                    m_indices[baseIndex + 4] = (short)((i + 1) * (vertexWidth + 1) + j);
                    m_indices[baseIndex + 5] = (short)((i + 1) * (vertexWidth + 1) + j + 1);
                    baseIndex += 6;
                }
            }
        }
        Texture m_texture = null;
        static CustomVertex.PositionNormalTextured[] m_vertices = null;
        static VertexBuffer m_vertexBuffer = null;
        static IndexBuffer m_indexBuffer = null;
        static short[] m_indices = null;
        static Effect m_effect = null;
        static float Attentuation = 0.3f;
        public override void Update(DrawArgs drawArgs)
        {
            try
            {
                if (!isInitialized)
                    Initialize(drawArgs);
                if (m_polygonFeature != null)
                    m_polygonFeature.Update(drawArgs);
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
            }
        }
        public override void Render(DrawArgs drawArgs)
        {
            if (!isInitialized)
                return;
            if (m_polygonFeature == null || !drawArgs.WorldCamera.ViewFrustum.Intersects(m_polygonFeature.BoundingBox))
                return;
            try
            {
                m_polygonFeature.Render(drawArgs);
                if (m_effect == null)
                {
                    string outerrors = "";
                    m_effect = Effect.FromFile(
                        drawArgs.device,
                        "shaders\\flag.fx",
                        null,
                        null,
                        ShaderFlags.None,
                        null,
                        out outerrors);
                    if (outerrors != null && outerrors.Length > 0)
                        Utility.Log.Write(outerrors);
                    m_effect.Technique = "TVertexShaderOnly";
                }
                if (m_vertexBuffer == null)
                {
                    drawArgs.device.DeviceReset += new EventHandler(device_DeviceReset);
                    device_DeviceReset(drawArgs.device, null);
                }
                angle += .04f;
                if (angle > 360)
                    angle = 0;
                drawArgs.device.VertexFormat = CustomVertex.PositionNormalTextured.Format;
                Cull cull = drawArgs.device.RenderState.CullMode;
                drawArgs.device.RenderState.CullMode = Cull.None;
                drawArgs.device.RenderState.ZBufferEnable = true;
                drawArgs.device.TextureState[0].ColorOperation = TextureOperation.SelectArg1;
                drawArgs.device.TextureState[0].ColorArgument1 = TextureArgument.TextureColor;
                Vector3 pos =
                        MathEngine.SphericalToCartesian(m_latitude, m_longitude, World.EquatorialRadius + World.Settings.VerticalExaggeration * 100000);
                Vector3 rc = new Vector3(
                    (float)drawArgs.WorldCamera.ReferenceCenter.X,
                    (float)drawArgs.WorldCamera.ReferenceCenter.Y,
                    (float)drawArgs.WorldCamera.ReferenceCenter.Z
                    );
                drawArgs.device.Transform.World = Matrix.Scaling(World.Settings.VerticalExaggeration * 100000, World.Settings.VerticalExaggeration * 100000, World.Settings.VerticalExaggeration * 100000);
                drawArgs.device.Transform.World *= Matrix.RotationY((float)-MathEngine.DegreesToRadians(m_latitude));
                drawArgs.device.Transform.World *= Matrix.RotationZ((float)MathEngine.DegreesToRadians(m_longitude));
                drawArgs.device.Transform.World *= Matrix.Translation(pos - rc);
                Matrix worldViewProj = drawArgs.device.Transform.World * drawArgs.device.Transform.View * drawArgs.device.Transform.Projection;
                System.DateTime currentTime = TimeKeeper.CurrentTimeUtc;
                Point3d sunPosition = SunCalculator.GetGeocentricPosition(currentTime);
                Vector3 sunVector = new Vector3(
                    (float)-sunPosition.X,
                    (float)-sunPosition.Y,
                    (float)-sunPosition.Z);
                m_effect.SetValue("angle", (float)angle);
                m_effect.SetValue("attentuation", Attentuation);
                m_effect.SetValue("World", drawArgs.device.Transform.World);
                m_effect.SetValue("View", drawArgs.device.Transform.View);
                m_effect.SetValue("Projection", drawArgs.device.Transform.Projection);
                m_effect.SetValue("Tex0", m_texture);
                m_effect.SetValue("lightDir", new Vector4(sunVector.X, sunVector.Y, sunVector.Z, 0));
                drawArgs.device.Indices = m_indexBuffer;
                drawArgs.device.SetStreamSource(0, m_vertexBuffer, 0);
                int numPasses = m_effect.Begin(0);
                for (int i = 0; i < numPasses; i++)
                {
                    m_effect.BeginPass(i);
                    drawArgs.device.DrawIndexedPrimitives(
                        PrimitiveType.TriangleList,
                        0,
                        0,
                        m_vertices.Length,
                        0,
                        m_indices.Length / 3);
                    m_effect.EndPass();
                }
                m_effect.End();
                drawArgs.device.Indices = null;
                drawArgs.device.Transform.World = drawArgs.WorldCamera.WorldMatrix;
                drawArgs.device.Transform.View = drawArgs.WorldCamera.ViewMatrix;
                drawArgs.device.RenderState.CullMode = cull;
            }
            catch (Exception ex)
            {
                Utility.Log.Write(ex);
            }
        }
        public override bool PerformSelectionAction(DrawArgs drawArgs)
        {
            return false;
        }
        private void device_DeviceReset(object sender, EventArgs e)
        {
            Device device = (Device)sender;
            m_vertexBuffer = new VertexBuffer(
                typeof(CustomVertex.PositionNormalTextured),
                m_vertices.Length,
                device,
                0,
                CustomVertex.PositionNormalTextured.Format,
                Pool.Default);
            m_vertexBuffer.SetData(m_vertices, 0, LockFlags.NoOverwrite | LockFlags.Discard);
            m_indexBuffer = new IndexBuffer(
                typeof(short),
                m_indices.Length,
                device,
                0,
                Pool.Default);
            m_indexBuffer.SetData(m_indices, 0, LockFlags.NoOverwrite | LockFlags.Discard);
        }
    }
}
