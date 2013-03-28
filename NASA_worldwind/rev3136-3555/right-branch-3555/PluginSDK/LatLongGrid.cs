using System;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
namespace WorldWind.Renderable
{
    public class LatLongGrid : RenderableObject
    {
        public double WorldRadius;
        protected double radius;
        public bool IsEarth;
        public int MinVisibleLongitude;
        public int MaxVisibleLongitude;
        public int MinVisibleLatitude;
        public int MaxVisibleLatitude;
        public int LongitudeInterval;
        public int LatitudeInterval;
        public int LongitudePointCount;
        public int LatitudePointCount;
        protected CustomVertex.PositionColored[] lineVertices;
        protected bool useZBuffer;
        public LatLongGrid(World world)
            : base("Grid lines")
        {
            WorldRadius = world.EquatorialRadius;
            IsEarth = world.Name == "Earth";
            m_renderPriority = RenderPriority.LinePaths;
        }
        public override void Render(DrawArgs drawArgs)
        {
            if (!World.Settings.showLatLonLines)
                return;
            ComputeGridValues(drawArgs);
            float offsetDegrees = (float)drawArgs.WorldCamera.TrueViewRange.Degrees / 6;
            drawArgs.device.RenderState.ZBufferEnable = useZBuffer;
            drawArgs.device.TextureState[0].ColorOperation = TextureOperation.Disable;
            drawArgs.device.VertexFormat = CustomVertex.PositionColored.Format;
            drawArgs.device.Transform.World = Matrix.Translation(
                    (float)-drawArgs.WorldCamera.ReferenceCenter.X,
                    (float)-drawArgs.WorldCamera.ReferenceCenter.Y,
                    (float)-drawArgs.WorldCamera.ReferenceCenter.Z
                    );
            Vector3 referenceCenter = new Vector3(
                    (float)drawArgs.WorldCamera.ReferenceCenter.X,
                    (float)drawArgs.WorldCamera.ReferenceCenter.Y,
                    (float)drawArgs.WorldCamera.ReferenceCenter.Z);
            if (World.Settings.EnableSunShading) drawArgs.device.RenderState.Lighting = false;
            for (float longitude = MinVisibleLongitude; longitude < MaxVisibleLongitude; longitude += LongitudeInterval)
            {
                int vertexIndex = 0;
                for (float latitude = MinVisibleLatitude; latitude <= MaxVisibleLatitude; latitude += LatitudeInterval)
                {
                    Vector3 pointXyz = MathEngine.SphericalToCartesian(latitude, longitude, radius);
                    lineVertices[vertexIndex].X = pointXyz.X;
                    lineVertices[vertexIndex].Y = pointXyz.Y;
                    lineVertices[vertexIndex].Z = pointXyz.Z;
                    lineVertices[vertexIndex].Color = World.Settings.latLonLinesColor;
                    vertexIndex++;
                }
                drawArgs.device.DrawUserPrimitives(PrimitiveType.LineStrip, LatitudePointCount - 1, lineVertices);
                float lat = (float)(drawArgs.WorldCamera.Latitude).Degrees;
                if (lat > 70)
                    lat = 70;
                Vector3 v = MathEngine.SphericalToCartesian(lat, (float)longitude, radius);
                if (drawArgs.WorldCamera.ViewFrustum.ContainsPoint(v))
                {
                    int longitudeRanged = (int)longitude;
                    if (longitudeRanged <= -180)
                        longitudeRanged += 360;
                    else if (longitudeRanged > 180)
                        longitudeRanged -= 360;
                    string s = Math.Abs(longitudeRanged).ToString();
                    if (longitudeRanged < 0)
                        s += "W";
                    else if (longitudeRanged > 0 && longitudeRanged < 180)
                        s += "E";
                    v = drawArgs.WorldCamera.Project(v - referenceCenter);
                    System.Drawing.Rectangle rect = new System.Drawing.Rectangle((int)v.X + 2, (int)v.Y, 10, 10);
                    drawArgs.defaultDrawingFont.DrawText(null, s, rect.Left, rect.Top, World.Settings.latLonLinesColor);
                }
            }
            for (float latitude = MinVisibleLatitude; latitude <= MaxVisibleLatitude; latitude += LatitudeInterval)
            {
                float longitude = (float)(drawArgs.WorldCamera.Longitude).Degrees + offsetDegrees;
                Vector3 v = MathEngine.SphericalToCartesian(latitude, longitude, radius);
                if (drawArgs.WorldCamera.ViewFrustum.ContainsPoint(v))
                {
                    v = drawArgs.WorldCamera.Project(v - referenceCenter);
                    float latLabel = latitude;
                    if (latLabel > 90)
                        latLabel = 180 - latLabel;
                    else if (latLabel < -90)
                        latLabel = -180 - latLabel;
                    string s = ((int)Math.Abs(latLabel)).ToString();
                    if (latLabel > 0)
                        s += "N";
                    else if (latLabel < 0)
                        s += "S";
                    System.Drawing.Rectangle rect = new System.Drawing.Rectangle((int)v.X, (int)v.Y, 10, 10);
                    drawArgs.defaultDrawingFont.DrawText(null, s, rect.Left, rect.Top, World.Settings.latLonLinesColor);
                }
                int vertexIndex = 0;
                for (longitude = MinVisibleLongitude; longitude <= MaxVisibleLongitude; longitude += LongitudeInterval)
                {
                    Vector3 pointXyz = MathEngine.SphericalToCartesian(latitude, longitude, radius);
                    lineVertices[vertexIndex].X = pointXyz.X;
                    lineVertices[vertexIndex].Y = pointXyz.Y;
                    lineVertices[vertexIndex].Z = pointXyz.Z;
                    if (latitude == 0)
                        lineVertices[vertexIndex].Color = World.Settings.equatorLineColor;
                    else
                        lineVertices[vertexIndex].Color = World.Settings.latLonLinesColor;
                    vertexIndex++;
                }
                drawArgs.device.DrawUserPrimitives(PrimitiveType.LineStrip, LongitudePointCount - 1, lineVertices);
            }
            if (World.Settings.showTropicLines && IsEarth)
                RenderTropicLines(drawArgs);
            drawArgs.device.Transform.World = drawArgs.WorldCamera.WorldMatrix;
            if (!useZBuffer)
                drawArgs.device.RenderState.ZBufferEnable = true;
            if (World.Settings.EnableSunShading) drawArgs.device.RenderState.Lighting = true;
        }
        public override void Initialize(DrawArgs drawArgs)
        {
            this.isInitialized = true;
        }
        public override void Dispose()
        {
        }
        public override bool PerformSelectionAction(DrawArgs drawArgs)
        {
            return false;
        }
        public override void Update(DrawArgs drawArgs)
        {
        }
        public override bool IsOn
        {
            get
            {
                return World.Settings.showLatLonLines;
            }
            set
            {
                World.Settings.showLatLonLines = value;
            }
        }
        void RenderTropicLines(DrawArgs drawArgs)
        {
            RenderTropicLine(drawArgs, 23.439444f, "Tropic Of Cancer");
            RenderTropicLine(drawArgs, -23.439444f, "Tropic Of Capricorn");
            RenderTropicLine(drawArgs, 66.560556f, "Arctic Circle");
            RenderTropicLine(drawArgs, -66.560556f, "Antarctic Circle");
        }
        void RenderTropicLine(DrawArgs drawArgs, float latitude, string label)
        {
            int vertexIndex = 0;
            Vector3 referenceCenter = new Vector3(
                    (float)drawArgs.WorldCamera.ReferenceCenter.X,
                    (float)drawArgs.WorldCamera.ReferenceCenter.Y,
                    (float)drawArgs.WorldCamera.ReferenceCenter.Z);
            for (float longitude = MinVisibleLongitude; longitude <= MaxVisibleLongitude; longitude = longitude + LongitudeInterval)
            {
                Vector3 pointXyz = MathEngine.SphericalToCartesian(latitude, longitude, radius);
                lineVertices[vertexIndex].X = pointXyz.X;
                lineVertices[vertexIndex].Y = pointXyz.Y;
                lineVertices[vertexIndex].Z = pointXyz.Z;
                lineVertices[vertexIndex].Color = World.Settings.tropicLinesColor;
                vertexIndex++;
            }
            drawArgs.device.DrawUserPrimitives(PrimitiveType.LineStrip, LongitudePointCount - 1, lineVertices);
            Vector3 t1 = MathEngine.SphericalToCartesian(Angle.FromDegrees(latitude),
                    drawArgs.WorldCamera.Longitude - drawArgs.WorldCamera.TrueViewRange * 0.3f * 0.5f, radius);
            if (drawArgs.WorldCamera.ViewFrustum.ContainsPoint(t1))
            {
                t1 = drawArgs.WorldCamera.Project(t1 - referenceCenter);
                drawArgs.defaultDrawingFont.DrawText(null, label, new System.Drawing.Rectangle((int)t1.X, (int)t1.Y, drawArgs.screenWidth, drawArgs.screenHeight), DrawTextFormat.NoClip, World.Settings.tropicLinesColor);
            }
        }
        public void ComputeGridValues(DrawArgs drawArgs)
        {
            double vr = drawArgs.WorldCamera.TrueViewRange.Radians;
            vr *= 1 + Math.Abs(Math.Sin(drawArgs.WorldCamera.Latitude.Radians));
            if (vr < 0.17)
                LatitudeInterval = 1;
            else if (vr < 0.6)
                LatitudeInterval = 2;
            else if (vr < 1.0)
                LatitudeInterval = 5;
            else
                LatitudeInterval = 10;
            LongitudeInterval = LatitudeInterval;
            if (drawArgs.WorldCamera.ViewFrustum.ContainsPoint(MathEngine.SphericalToCartesian(90, 0, radius)) ||
                    drawArgs.WorldCamera.ViewFrustum.ContainsPoint(MathEngine.SphericalToCartesian(-90, 0, radius)))
            {
                LongitudeInterval = 10;
            }
            MinVisibleLongitude = LongitudeInterval >= 10 ? -180 : (int)drawArgs.WorldCamera.Longitude.Degrees / LongitudeInterval * LongitudeInterval - 18 * LongitudeInterval;
            MaxVisibleLongitude = LongitudeInterval >= 10 ? 180 : (int)drawArgs.WorldCamera.Longitude.Degrees / LongitudeInterval * LongitudeInterval + 18 * LongitudeInterval;
            MinVisibleLatitude = (int)drawArgs.WorldCamera.Latitude.Degrees / LatitudeInterval * LatitudeInterval - 9 * LatitudeInterval;
            MaxVisibleLatitude = (int)drawArgs.WorldCamera.Latitude.Degrees / LatitudeInterval * LatitudeInterval + 9 * LatitudeInterval;
            if (MaxVisibleLatitude - MinVisibleLatitude >= 180 || LongitudeInterval == 10)
            {
                MinVisibleLatitude = -90;
                MaxVisibleLatitude = 90;
            }
            LongitudePointCount = (MaxVisibleLongitude - MinVisibleLongitude) / LongitudeInterval + 1;
            LatitudePointCount = (MaxVisibleLatitude - MinVisibleLatitude) / LatitudeInterval + 1;
            int vertexPointCount = Math.Max(LatitudePointCount, LongitudePointCount);
            if (lineVertices == null || vertexPointCount > lineVertices.Length)
                lineVertices = new CustomVertex.PositionColored[Math.Max(LatitudePointCount, LongitudePointCount)];
            radius = WorldRadius;
            if (drawArgs.WorldCamera.Altitude < 0.10f * WorldRadius)
                useZBuffer = false;
            else
            {
                useZBuffer = true;
                double bRadius = WorldRadius * 1.01f;
                double nRadius = WorldRadius + 0.015f * drawArgs.WorldCamera.Altitude;
                radius = Math.Min(nRadius, bRadius);
            }
        }
    }
}
