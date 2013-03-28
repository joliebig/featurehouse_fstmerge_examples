using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Camera;
using WorldWind.Configuration;
using WorldWind.Net;
using WorldWind.Terrain;
using WorldWind.VisualControl;
using System;
using System.IO;
using System.ComponentModel;
using System.Collections;
using System.Drawing;
using Utility;
namespace WorldWind.Renderable
{
    public class QuadTileSet : RenderableObject
    {
        bool m_RenderStruts = true;
        protected string m_ServerLogoFilePath;
        protected Image m_ServerLogoImage;
        protected Hashtable m_topmostTiles = new Hashtable();
        protected double m_north;
        protected double m_south;
        protected double m_west;
        protected double m_east;
        bool renderFileNames = false;
        protected Texture m_iconTexture;
        protected Sprite sprite;
        protected Rectangle m_spriteSize;
        protected ProgressBar progressBar;
        protected Blend m_sourceBlend = Blend.BlendFactor;
        protected Blend m_destinationBlend = Blend.InvBlendFactor;
        protected static long lastRenderTime;
        protected double m_layerRadius;
        protected bool m_alwaysRenderBaseTiles;
        protected float m_tileDrawSpread;
        protected float m_tileDrawDistance;
        protected bool m_isDownloadingElevation;
        protected int m_numberRetries;
        protected Hashtable m_downloadRequests = new Hashtable();
        protected int m_maxQueueSize = 400;
        protected bool m_terrainMapped;
        protected ImageStore[] m_imageStores;
        protected Camera.CameraBase m_camera;
        protected GeoSpatialDownloadRequest[] m_activeDownloads = new GeoSpatialDownloadRequest[20];
        protected DateTime[] m_downloadStarted = new DateTime[20];
        protected TimeSpan m_connectionWaitTime = TimeSpan.FromMinutes(2);
        protected DateTime m_connectionWaitStart;
        protected bool m_isConnectionWaiting;
        protected bool m_enableColorKeying;
        protected Effect m_effect = null;
        protected string m_effectPath = null;
        protected string m_effectTechnique = null;
        static protected EffectPool m_effectPool = new EffectPool();
  protected TimeSpan m_cacheExpirationTime = TimeSpan.MaxValue;
        public static Texture DownloadInProgressTexture;
        public static Texture DownloadQueuedTexture;
        public static Texture DownloadTerrainTexture;
        public int ColorKey;
        public int ColorKeyMax;
        bool m_renderGrayscale = false;
        float m_grayscaleBrightness = 0.0f;
        public float GrayscaleBrightness
        {
            get { return m_grayscaleBrightness; }
            set { m_grayscaleBrightness = value; }
        }
        public bool RenderGrayscale
        {
            get { return m_renderGrayscale; }
            set { m_renderGrayscale = value; }
        }
        public bool RenderStruts
        {
            get { return m_RenderStruts; }
            set { m_RenderStruts = value; }
        }
        public QuadTileSet(
                string name,
                World parentWorld,
                double distanceAboveSurface,
                double north,
                double south,
                double west,
                double east,
                bool terrainMapped,
                                        ImageStore[] imageStores)
            : base(name, parentWorld)
        {
            float layerRadius = (float)(parentWorld.EquatorialRadius + distanceAboveSurface);
            m_north = north;
            m_south = south;
            m_west = west;
            m_east = east;
            Position = MathEngine.SphericalToCartesian(
                    (north + south) * 0.5f,
                    (west + east) * 0.5f,
                    layerRadius);
            m_layerRadius = layerRadius;
            m_tileDrawDistance = 3.5f;
            m_tileDrawSpread = 2.9f;
            m_imageStores = imageStores;
            m_terrainMapped = terrainMapped;
            if (terrainMapped)
                m_renderPriority = RenderPriority.TerrainMappedImages;
        }
  public TimeSpan CacheExpirationTime
  {
   get
   {
    return this.m_cacheExpirationTime;
   }
   set
   {
    this.m_cacheExpirationTime = value;
   }
  }
        public virtual string ServerLogoFilePath
        {
            get
            {
                return m_ServerLogoFilePath;
            }
            set
            {
                m_ServerLogoFilePath = value;
            }
        }
        public bool RenderFileNames
        {
            get
            {
                return renderFileNames;
            }
            set
            {
                renderFileNames = value;
            }
        }
        public virtual Image ServerLogoImage
        {
            get
            {
                if (m_ServerLogoImage == null)
                {
                    if (m_ServerLogoFilePath == null)
                        return null;
                    try
                    {
                        if (File.Exists(m_ServerLogoFilePath))
                            m_ServerLogoImage = ImageHelper.LoadImage(m_ServerLogoFilePath);
                    }
                    catch { }
                }
                return m_ServerLogoImage;
            }
        }
        public override Image ThumbnailImage
        {
            get
            {
                if (base.ThumbnailImage != null)
                    return base.ThumbnailImage;
                return ServerLogoImage;
            }
        }
        public virtual bool HasTransparentRange
        {
            get
            {
                return (ColorKeyMax != 0);
            }
        }
        public Blend SourceBlend
        {
            get
            {
                return m_sourceBlend;
            }
            set
            {
                m_sourceBlend = value;
            }
        }
        public Blend DestinationBlend
        {
            get
            {
                return m_destinationBlend;
            }
            set
            {
                m_destinationBlend = value;
            }
        }
        public double North
        {
            get
            {
                return m_north;
            }
        }
        public double West
        {
            get
            {
                return m_west;
            }
        }
        public double South
        {
            get
            {
                return m_south;
            }
        }
        public double East
        {
            get
            {
                return m_east;
            }
        }
        public bool EnableColorKeying
        {
            get
            {
                return m_enableColorKeying;
            }
            set
            {
                m_enableColorKeying = value;
            }
        }
        public DateTime ConnectionWaitStart
        {
            get
            {
                return m_connectionWaitStart;
            }
        }
        public bool IsConnectionWaiting
        {
            get
            {
                return m_isConnectionWaiting;
            }
        }
        public double LayerRadius
        {
            get
            {
                return m_layerRadius;
            }
            set
            {
                m_layerRadius = value;
            }
        }
        public bool AlwaysRenderBaseTiles
        {
            get
            {
                return m_alwaysRenderBaseTiles;
            }
            set
            {
                m_alwaysRenderBaseTiles = value;
            }
        }
        public float TileDrawSpread
        {
            get
            {
                return m_tileDrawSpread;
            }
            set
            {
                m_tileDrawSpread = value;
            }
        }
        public float TileDrawDistance
        {
            get
            {
                return m_tileDrawDistance;
            }
            set
            {
                m_tileDrawDistance = value;
            }
        }
        public bool IsDownloadingElevation
        {
            get
            {
                return m_isDownloadingElevation;
            }
            set
            {
                m_isDownloadingElevation = value;
            }
        }
        public int NumberRetries
        {
            get
            {
                return m_numberRetries;
            }
            set
            {
                m_numberRetries = value;
            }
        }
        public bool TerrainMapped
        {
            get { return m_terrainMapped; }
            set { m_terrainMapped = value; }
        }
        public ImageStore[] ImageStores
        {
            get
            {
                return m_imageStores;
            }
        }
        public Hashtable DownloadRequests
        {
            get
            {
                return m_downloadRequests;
            }
        }
        public CameraBase Camera
        {
            get
            {
                return m_camera;
            }
            set
            {
                m_camera = value;
            }
        }
        public string EffectPath
        {
            get
            {
                return m_effectPath;
            }
            set
            {
                m_effectPath = value;
                m_effect = null;
            }
        }
        public Effect Effect
        {
            get
            {
                return m_effect;
            }
        }
        override public void Initialize(DrawArgs drawArgs)
        {
            Camera = DrawArgs.Camera;
            if (DownloadInProgressTexture == null)
                DownloadInProgressTexture = CreateDownloadRectangle(
                        DrawArgs.Device, World.Settings.DownloadProgressColor, 0);
            if (DownloadQueuedTexture == null)
                DownloadQueuedTexture = CreateDownloadRectangle(
                        DrawArgs.Device, World.Settings.DownloadQueuedColor, 0);
            if (DownloadTerrainTexture == null)
                DownloadTerrainTexture = CreateDownloadRectangle(
                        DrawArgs.Device, World.Settings.DownloadTerrainRectangleColor, 0);
            try
            {
                lock (m_topmostTiles.SyncRoot)
                {
                    foreach (QuadTile qt in m_topmostTiles.Values)
                        qt.Initialize();
                }
            }
            catch
            {
            }
            isInitialized = true;
            if (MetaData.ContainsKey("EffectPath"))
            {
                m_effectPath = MetaData["EffectPath"] as string;
            }
            else
            {
                m_effectPath = null;
            }
            m_effect = null;
        }
        public override bool PerformSelectionAction(DrawArgs drawArgs)
        {
            return false;
        }
        public override void Update(DrawArgs drawArgs)
        {
            if (!isInitialized)
                Initialize(drawArgs);
            if (m_effectPath != null && m_effect == null)
            {
                string errs = string.Empty;
                m_effect = Effect.FromFile(DrawArgs.Device, m_effectPath, null, "", ShaderFlags.None, m_effectPool, out errs);
                if (errs != null && errs != string.Empty)
                {
                    Log.Write(Log.Levels.Warning, "Could not load effect " + m_effectPath + ": " + errs);
                    Log.Write(Log.Levels.Warning, "Effect has been disabled.");
                    m_effectPath = null;
                    m_effect = null;
                }
            }
            if (ImageStores[0].LevelZeroTileSizeDegrees < 180)
            {
                double vrd = DrawArgs.Camera.ViewRange.Degrees;
                double latitudeMax = DrawArgs.Camera.Latitude.Degrees + vrd;
                double latitudeMin = DrawArgs.Camera.Latitude.Degrees - vrd;
                double longitudeMax = DrawArgs.Camera.Longitude.Degrees + vrd;
                double longitudeMin = DrawArgs.Camera.Longitude.Degrees - vrd;
                if (latitudeMax < m_south || latitudeMin > m_north || longitudeMax < m_west || longitudeMin > m_east)
                    return;
            }
            if (DrawArgs.Camera.ViewRange * 0.5f >
                    Angle.FromDegrees(TileDrawDistance * ImageStores[0].LevelZeroTileSizeDegrees))
            {
                lock (m_topmostTiles.SyncRoot)
                {
                    foreach (QuadTile qt in m_topmostTiles.Values)
                        qt.Dispose();
                    m_topmostTiles.Clear();
                    ClearDownloadRequests();
                }
                return;
            }
            RemoveInvisibleTiles(DrawArgs.Camera);
            try
            {
                int middleRow = MathEngine.GetRowFromLatitude(DrawArgs.Camera.Latitude, ImageStores[0].LevelZeroTileSizeDegrees);
                int middleCol = MathEngine.GetColFromLongitude(DrawArgs.Camera.Longitude, ImageStores[0].LevelZeroTileSizeDegrees);
                double middleSouth = -90.0f + middleRow * ImageStores[0].LevelZeroTileSizeDegrees;
                double middleNorth = -90.0f + middleRow * ImageStores[0].LevelZeroTileSizeDegrees + ImageStores[0].LevelZeroTileSizeDegrees;
                double middleWest = -180.0f + middleCol * ImageStores[0].LevelZeroTileSizeDegrees;
                double middleEast = -180.0f + middleCol * ImageStores[0].LevelZeroTileSizeDegrees + ImageStores[0].LevelZeroTileSizeDegrees;
                double middleCenterLat = 0.5f * (middleNorth + middleSouth);
                double middleCenterLon = 0.5f * (middleWest + middleEast);
                int tileSpread = 4;
                for (int i = 0; i < tileSpread; i++)
                {
                    for (double j = middleCenterLat - i * ImageStores[0].LevelZeroTileSizeDegrees; j < middleCenterLat + i * ImageStores[0].LevelZeroTileSizeDegrees; j += ImageStores[0].LevelZeroTileSizeDegrees)
                    {
                        for (double k = middleCenterLon - i * ImageStores[0].LevelZeroTileSizeDegrees; k < middleCenterLon + i * ImageStores[0].LevelZeroTileSizeDegrees; k += ImageStores[0].LevelZeroTileSizeDegrees)
                        {
                            int curRow = MathEngine.GetRowFromLatitude(Angle.FromDegrees(j), ImageStores[0].LevelZeroTileSizeDegrees);
                            int curCol = MathEngine.GetColFromLongitude(Angle.FromDegrees(k), ImageStores[0].LevelZeroTileSizeDegrees);
                            long key = ((long)curRow << 32) + curCol;
                            QuadTile qt = (QuadTile)m_topmostTiles[key];
                            if (qt != null)
                            {
                                qt.Update(drawArgs);
                                continue;
                            }
                            double west = -180.0f + curCol * ImageStores[0].LevelZeroTileSizeDegrees;
                            if (west > m_east)
                                continue;
                            double east = west + ImageStores[0].LevelZeroTileSizeDegrees;
                            if (east < m_west)
                                continue;
                            double south = -90.0f + curRow * ImageStores[0].LevelZeroTileSizeDegrees;
                            if (south > m_north)
                                continue;
                            double north = south + ImageStores[0].LevelZeroTileSizeDegrees;
                            if (north < m_south)
                                continue;
                            qt = new QuadTile(south, north, west, east, 0, this);
                            if (DrawArgs.Camera.ViewFrustum.Intersects(qt.BoundingBox))
                            {
                                lock (m_topmostTiles.SyncRoot)
                                    m_topmostTiles.Add(key, qt);
                                qt.Update(drawArgs);
                            }
                        }
                    }
                }
            }
            catch (System.Threading.ThreadAbortException)
            {
            }
            catch (Exception caught)
            {
                Log.Write(caught);
            }
        }
        protected void RemoveInvisibleTiles(CameraBase camera)
        {
            ArrayList deletionList = new ArrayList();
            lock (m_topmostTiles.SyncRoot)
            {
                foreach (long key in m_topmostTiles.Keys)
                {
                    QuadTile qt = (QuadTile)m_topmostTiles[key];
                    if (!camera.ViewFrustum.Intersects(qt.BoundingBox))
                        deletionList.Add(key);
                }
                foreach (long deleteThis in deletionList)
                {
                    QuadTile qt = (QuadTile)m_topmostTiles[deleteThis];
                    if (qt != null)
                    {
                        m_topmostTiles.Remove(deleteThis);
                        qt.Dispose();
                    }
                }
            }
        }
        public override void Render(DrawArgs drawArgs)
        {
            try
            {
                lock (m_topmostTiles.SyncRoot)
                {
                    if (m_topmostTiles.Count <= 0)
                    {
                        return;
                    }
                    Device device = DrawArgs.Device;
                    device.Clear(ClearFlags.ZBuffer, 0, 1.0f, 0);
                    device.RenderState.ZBufferEnable = true;
                    lastRenderTime = DrawArgs.CurrentFrameStartTicks;
                    if (!World.Settings.EnableSunShading)
                    {
                        device.VertexFormat = CustomVertex.PositionNormalTextured.Format;
                        device.SetTextureStageState(0, TextureStageStates.ColorOperation, (int)TextureOperation.SelectArg1);
                        device.SetTextureStageState(0, TextureStageStates.ColorArgument1, (int)TextureArgument.TextureColor);
                        device.SetTextureStageState(0, TextureStageStates.AlphaArgument1, (int)TextureArgument.TextureColor);
                        device.SetTextureStageState(0, TextureStageStates.AlphaOperation, (int)TextureOperation.SelectArg1);
                        device.SetTextureStageState(1, TextureStageStates.ColorArgument2, (int)TextureArgument.Current);
                        device.SetTextureStageState(1, TextureStageStates.ColorArgument1, (int)TextureArgument.TextureColor);
                        device.SetTextureStageState(1, TextureStageStates.TextureCoordinateIndex, 0);
                    }
                    device.VertexFormat = CustomVertex.PositionNormalTextured.Format;
                    foreach (QuadTile qt in m_topmostTiles.Values)
                        qt.Render(drawArgs);
                    device.SetTextureStageState(1, TextureStageStates.TextureCoordinateIndex, 1);
                    if (m_renderPriority < RenderPriority.TerrainMappedImages)
                        device.RenderState.ZBufferEnable = true;
                }
            }
            catch
            {
            }
            finally
            {
                if (IsConnectionWaiting)
                {
                    if (DateTime.Now.Subtract(TimeSpan.FromSeconds(15)) < ConnectionWaitStart)
                    {
                        string s = "Problem connecting to server... Trying again in 2 minutes.\n";
                        drawArgs.UpperLeftCornerText += s;
                    }
                }
                int i = 0;
                foreach (GeoSpatialDownloadRequest request in m_activeDownloads)
                {
                    if (request != null && !request.IsComplete && i < 10)
                    {
                        RenderDownloadProgress(drawArgs, request, i++);
                    }
                }
            }
        }
        public void RenderDownloadProgress(DrawArgs drawArgs, GeoSpatialDownloadRequest request, int offset)
        {
            int halfIconHeight = 24;
            int halfIconWidth = 24;
            Vector3 projectedPoint = new Vector3(DrawArgs.ParentControl.Width - halfIconWidth - 10, DrawArgs.ParentControl.Height - 34 - 4 * offset, 0.5f);
            if (progressBar == null)
                progressBar = new ProgressBar(40, 4);
            progressBar.Draw(drawArgs, projectedPoint.X, projectedPoint.Y + 24, request.ProgressPercent, World.Settings.DownloadProgressColor.ToArgb());
            DrawArgs.Device.RenderState.ZBufferEnable = true;
            if (ServerLogoFilePath == null)
                return;
            if (m_iconTexture == null)
                m_iconTexture = ImageHelper.LoadIconTexture(ServerLogoFilePath);
            if (sprite == null)
            {
                using (Surface s = m_iconTexture.GetSurfaceLevel(0))
                {
                    SurfaceDescription desc = s.Description;
                    m_spriteSize = new Rectangle(0, 0, desc.Width, desc.Height);
                }
                this.sprite = new Sprite(DrawArgs.Device);
            }
            float scaleWidth = (float)2.0f * halfIconWidth / m_spriteSize.Width;
            float scaleHeight = (float)2.0f * halfIconHeight / m_spriteSize.Height;
            this.sprite.Begin(SpriteFlags.AlphaBlend);
            this.sprite.Transform = Matrix.Transformation2D(new Vector2(0.0f, 0.0f), 0.0f, new Vector2(scaleWidth, scaleHeight),
                    new Vector2(0, 0),
                    0.0f, new Vector2(projectedPoint.X, projectedPoint.Y));
            this.sprite.Draw(m_iconTexture, m_spriteSize,
                    new Vector3(1.32f * 48, 1.32f * 48, 0), new Vector3(0, 0, 0),
                    World.Settings.DownloadLogoColor);
            this.sprite.End();
        }
        public override void Dispose()
        {
            isInitialized = false;
            for (int i = 0; i < World.Settings.MaxSimultaneousDownloads; i++)
            {
                if (m_activeDownloads[i] != null)
                {
                    m_activeDownloads[i].Dispose();
                    m_activeDownloads[i] = null;
                }
            }
            foreach (QuadTile qt in m_topmostTiles.Values)
                qt.Dispose();
            if (m_iconTexture != null)
            {
                m_iconTexture.Dispose();
                m_iconTexture = null;
            }
            if (this.sprite != null)
            {
                this.sprite.Dispose();
                this.sprite = null;
            }
        }
        public virtual void ResetCacheForCurrentView(WorldWind.Camera.CameraBase camera)
        {
            ArrayList deletionList = new ArrayList();
            lock (m_topmostTiles.SyncRoot)
            {
                foreach (long key in m_topmostTiles.Keys)
                {
                    QuadTile qt = (QuadTile)m_topmostTiles[key];
                    if (camera.ViewFrustum.Intersects(qt.BoundingBox))
                    {
                        qt.ResetCache();
                        deletionList.Add(key);
                    }
                }
                foreach (long deletionKey in deletionList)
                    m_topmostTiles.Remove(deletionKey);
            }
        }
        public void ClearDownloadRequests()
        {
            lock (m_downloadRequests.SyncRoot)
            {
                m_downloadRequests.Clear();
            }
        }
        public virtual void AddToDownloadQueue(CameraBase camera, GeoSpatialDownloadRequest newRequest)
        {
            QuadTile key = newRequest.QuadTile;
            key.WaitingForDownload = true;
            lock (m_downloadRequests.SyncRoot)
            {
                if (m_downloadRequests.Contains(key))
                    return;
                m_downloadRequests.Add(key, newRequest);
                if (m_downloadRequests.Count >= m_maxQueueSize)
                {
                    GeoSpatialDownloadRequest farthestRequest = null;
                    Angle curDistance = Angle.Zero;
                    Angle farthestDistance = Angle.Zero;
                    foreach (GeoSpatialDownloadRequest curRequest in m_downloadRequests.Values)
                    {
                        curDistance = MathEngine.SphericalDistance(
                                        curRequest.QuadTile.CenterLatitude,
                                        curRequest.QuadTile.CenterLongitude,
                                        camera.Latitude,
                                        camera.Longitude);
                        if (curDistance > farthestDistance)
                        {
                            farthestRequest = curRequest;
                            farthestDistance = curDistance;
                        }
                    }
                    farthestRequest.Dispose();
                    farthestRequest.QuadTile.DownloadRequest = null;
                    m_downloadRequests.Remove(farthestRequest.QuadTile);
                }
            }
            ServiceDownloadQueue();
        }
        public virtual void RemoveFromDownloadQueue(GeoSpatialDownloadRequest removeRequest)
        {
            lock (m_downloadRequests.SyncRoot)
            {
                QuadTile key = removeRequest.QuadTile;
                GeoSpatialDownloadRequest request = (GeoSpatialDownloadRequest)m_downloadRequests[key];
                if (request != null)
                {
                    m_downloadRequests.Remove(key);
                    request.QuadTile.DownloadRequest = null;
                }
            }
        }
        public virtual void ServiceDownloadQueue()
        {
            Log.Write(Log.Levels.Verbose, "QTS", "ServiceDownloadQueue: " + m_downloadRequests.Count + " requests waiting");
            lock (m_downloadRequests.SyncRoot)
            {
                for (int i = 0; i < World.Settings.MaxSimultaneousDownloads; i++)
                {
                    if (m_activeDownloads[i] == null)
                        continue;
                    if (!m_activeDownloads[i].IsComplete)
                        continue;
                    m_activeDownloads[i].Cancel();
                    m_activeDownloads[i].Dispose();
                    m_activeDownloads[i] = null;
                }
                if (NumberRetries >= 5 || m_isConnectionWaiting)
                {
                    if (!m_isConnectionWaiting)
                    {
                        m_connectionWaitStart = DateTime.Now;
                        m_isConnectionWaiting = true;
                    }
                    if (DateTime.Now.Subtract(m_connectionWaitTime) > m_connectionWaitStart)
                    {
                        NumberRetries = 0;
                        m_isConnectionWaiting = false;
                    }
                    return;
                }
                for (int i = 0; i < World.Settings.MaxSimultaneousDownloads; i++)
                {
                    if (m_activeDownloads[i] != null)
                        continue;
                    if (m_downloadRequests.Count <= 0)
                        continue;
                    m_activeDownloads[i] = GetClosestDownloadRequest();
                    if (m_activeDownloads[i] != null)
                    {
                        m_downloadStarted[i] = DateTime.Now;
                        m_activeDownloads[i].StartDownload();
                    }
                }
            }
        }
        public virtual GeoSpatialDownloadRequest GetClosestDownloadRequest()
        {
            GeoSpatialDownloadRequest closestRequest = null;
            float largestArea = float.MinValue;
            lock (m_downloadRequests.SyncRoot)
            {
                foreach (GeoSpatialDownloadRequest curRequest in m_downloadRequests.Values)
                {
                    if (curRequest.IsDownloading)
                        continue;
                    QuadTile qt = curRequest.QuadTile;
                    if (!m_camera.ViewFrustum.Intersects(qt.BoundingBox))
                        continue;
                    float screenArea = qt.BoundingBox.CalcRelativeScreenArea(m_camera);
                    if (screenArea > largestArea)
                    {
                        largestArea = screenArea;
                        closestRequest = curRequest;
                    }
                }
            }
            return closestRequest;
        }
        static protected Texture CreateDownloadRectangle(Device device, Color color, int padding)
        {
            int mid = 128;
            using (Bitmap i = new Bitmap(2 * mid, 2 * mid))
            using (Graphics g = Graphics.FromImage(i))
            using (Pen pen = new Pen(color))
            {
                int width = mid - 1 - 2 * padding;
                g.DrawRectangle(pen, padding, padding, width, width);
                g.DrawRectangle(pen, mid + padding, padding, width, width);
                g.DrawRectangle(pen, padding, mid + padding, width, width);
                g.DrawRectangle(pen, mid + padding, mid + padding, width, width);
                Texture texture = new Texture(device, i, Usage.None, Pool.Managed);
                return texture;
            }
        }
    }
}
