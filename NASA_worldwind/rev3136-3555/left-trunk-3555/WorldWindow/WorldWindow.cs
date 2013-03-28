using Microsoft.DirectX.Direct3D;
using Microsoft.DirectX;
using System.Collections;
using System.ComponentModel;
using System.Diagnostics;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.Net;
using System.Security.Permissions;
using System.Threading;
using System.Windows.Forms;
using System;
using WorldWind.Camera;
using WorldWind.Menu;
using WorldWind;
using WorldWind.DataSource;
using WorldWind.Net;
using WorldWind.Net.Wms;
using WorldWind.Interop;
using WorldWind.VisualControl;
using Utility;
namespace WorldWind
{
 public class WorldWindow : Control, IGlobe
 {
  private Device m_Device3d;
  private PresentParameters m_presentParams;
  private DrawArgs drawArgs;
  private World m_World;
  private Cache m_Cache;
  private Thread m_WorkerThread;
  private bool showDiagnosticInfo;
  private string _caption = "";
  private long lastFpsUpdateTime;
  private int frameCounter;
  private float fps;
  private string saveScreenShotFilePath;
  private ImageFileFormat saveScreenShotImageFileFormat = ImageFileFormat.Bmp;
  private bool m_WorkerThreadRunning;
  private LayerManagerButton layerManagerButton;
  private MenuBar _menuBar = new MenuBar(MenuAnchor.Top, 90);
  private bool m_isRenderDisabled;
  private bool isMouseDragging;
  private Point mouseDownStartPosition = Point.Empty;
  private bool renderWireFrame;
  private System.Timers.Timer m_FpsTimer = new System.Timers.Timer(250);
  public WorldWindow()
  {
   this.SetStyle(ControlStyles.AllPaintingInWmPaint | ControlStyles.Opaque, true);
   this.Size = new Size(1,1);
   try
   {
    if(!IsInDesignMode())
     this.InitializeGraphics();
    this.drawArgs = new DrawArgs(m_Device3d, this );
    this.m_RootWidget = new WorldWind.Widgets.RootWidget(this);
                this.m_NewRootWidget = new WorldWind.NewWidgets.RootWidget(this);
    DrawArgs.RootWidget = this.m_RootWidget;
                DrawArgs.NewRootWidget = this.m_NewRootWidget;
    m_FpsTimer.Elapsed += new System.Timers.ElapsedEventHandler(m_FpsTimer_Elapsed);
    m_FpsTimer.Start();
    TimeKeeper.Start();
   }
   catch (InvalidCallException caught)
   {
    throw new InvalidCallException(
     "Unable to locate a compatible graphics adapter. Make sure you are running the latest version of DirectX.", caught );
   }
   catch (NotAvailableException caught)
   {
    throw new NotAvailableException(
     "Unable to locate a compatible graphics adapter. Make sure you are running the latest version of DirectX.", caught );
   }
  }
  public World CurrentWorld
  {
   get
   {
    return m_World;
   }
   set
   {
    m_World = value;
    if(m_World != null)
    {
     MomentumCamera camera = new MomentumCamera(m_World.Position, m_World.EquatorialRadius );
     if(!World.Settings.CameraResetsAtStartup)
     {
      camera.SetPosition(
       World.Settings.CameraLatitude.Degrees,
       World.Settings.CameraLongitude.Degrees,
       World.Settings.CameraHeading.Degrees,
       World.Settings.CameraAltitude,
       World.Settings.CameraTilt.Degrees,
       0
       );
     }
     this.drawArgs.WorldCamera = camera;
     this.drawArgs.CurrentWorld = value;
     this.layerManagerButton = new LayerManagerButton(
      Path.Combine(Path.GetDirectoryName(Application.ExecutablePath), @"Data\Icons\Interface\layer-manager2.png"),
      m_World);
     this._menuBar.AddToolsMenuButton( this.layerManagerButton, 0 );
     this._menuBar.AddToolsMenuButton( new PositionMenuButton( Path.GetDirectoryName(Application.ExecutablePath) + "\\Data\\Icons\\Interface\\coordinates.png"), 1);
     this._menuBar.AddToolsMenuButton( new LatLonMenuButton( Path.GetDirectoryName(Application.ExecutablePath) + "\\Data\\Icons\\Interface\\lat-long.png", m_World), 2);
     this.layerManagerButton.SetPushed(World.Settings.ShowLayerManager);
     m_World.RenderableObjects.Add(new Renderable.LatLongGrid(m_World));
    }
   }
  }
  public string Caption
  {
   get
   {
    return this._caption;
   }
   set
   {
    this._caption = value;
   }
  }
  public DrawArgs DrawArgs
  {
   get { return this.drawArgs; }
  }
  public MenuBar MenuBar
  {
   get
   {
    return this._menuBar;
   }
  }
  public bool ShowLayerManager
  {
   get
   {
    if(this.layerManagerButton != null)
     return this.layerManagerButton.IsPushed();
    else
     return false;
   }
   set
   {
    if(this.layerManagerButton != null)
     this.layerManagerButton.SetPushed(value);
   }
  }
  public Cache Cache
  {
   get
   {
    return m_Cache;
   }
   set
   {
    m_Cache = value;
   }
  }
  public bool IsRenderDisabled
  {
   get
   {
    return m_isRenderDisabled;
   }
   set
   {
    m_isRenderDisabled = value;
   }
  }
  public void Goto( WorldWind.Net.WorldWindUri uri )
  {
   GotoLatLon(
    uri.Latitude.Degrees, uri.Longitude.Degrees, uri.Direction.Degrees,
    uri.Altitude, uri.ViewRange.Degrees, uri.Tilt.Degrees);
   drawArgs.WorldCamera.Bank = uri.Bank;
   drawArgs.UpperLeftCornerText = uri.ToString();
   CurrentWorld.RenderableObjects.Enable( uri.Layer );
  }
  public void GotoLatLon(double latitude, double longitude, double heading, double altitude, double perpendicularViewRange, double tilt)
  {
   if(!double.IsNaN(perpendicularViewRange))
    altitude = m_World.EquatorialRadius * Math.Sin(MathEngine.DegreesToRadians(perpendicularViewRange * 0.5));
   if (altitude<1)
    altitude = 1;
   this.drawArgs.WorldCamera.SetPosition(latitude, longitude, heading, altitude, tilt);
  }
  public void GotoLatLon(double latitude, double longitude)
  {
   this.drawArgs.WorldCamera.SetPosition(latitude, longitude,
    this.drawArgs.WorldCamera.Heading.Degrees,
    this.drawArgs.WorldCamera.Altitude,
    this.drawArgs.WorldCamera.Tilt.Degrees);
  }
  public void GotoLatLonAltitude(double latitude, double longitude, double altitude)
  {
   this.drawArgs.WorldCamera.SetPosition(latitude, longitude,
    this.drawArgs.WorldCamera.Heading.Degrees,
    altitude,
    this.drawArgs.WorldCamera.Tilt.Degrees);
  }
  public void GotoLatLonHeadingViewRange(double latitude, double longitude, double heading, double perpendicularViewRange)
  {
   double altitude = m_World.EquatorialRadius * Math.Sin(MathEngine.DegreesToRadians(perpendicularViewRange * 0.5));
   this.GotoLatLonHeadingAltitude(latitude, longitude, heading, altitude);
  }
  public void GotoLatLonViewRange(double latitude, double longitude, double perpendicularViewRange)
  {
   double altitude = m_World.EquatorialRadius * Math.Sin(MathEngine.DegreesToRadians(perpendicularViewRange * 0.5));
   this.GotoLatLonHeadingAltitude(latitude, longitude, this.drawArgs.WorldCamera.Heading.Degrees, altitude);
  }
  public void GotoLatLonHeadingAltitude(double latitude, double longitude, double heading, double altitude)
  {
   this.drawArgs.WorldCamera.SetPosition(latitude, longitude,
    heading,
    altitude,
    this.drawArgs.WorldCamera.Tilt.Degrees);
  }
  public void SaveScreenshot(string filePath)
  {
   if(m_Device3d == null)
    return;
   FileInfo saveFileInfo = new FileInfo(filePath);
   string ext = saveFileInfo.Extension.Replace(".","");
   try
   {
    this.saveScreenShotImageFileFormat = (ImageFileFormat) Enum.Parse(typeof(ImageFileFormat), ext, true );
   }
   catch(ArgumentException)
   {
    throw new ApplicationException("Unknown file type/file extension for file '"+filePath+"'.  Unable to save.");
   }
   if(!saveFileInfo.Directory.Exists)
    saveFileInfo.Directory.Create();
   this.saveScreenShotFilePath = filePath;
  }
  public void OnApplicationIdle(object sender, EventArgs e)
  {
   const float SleepOverHeadSeconds = 2e-3f;
   const float PresentOverheadSeconds = 0;
   try
   {
                if (Parent.Focused && !Focused)
                    Focus();
    while (IsAppStillIdle)
    {
     if (!World.Settings.AlwaysRenderWindow && m_isRenderDisabled && !World.Settings.CameraHasMomentum)
      return;
     Render();
     if (World.Settings.ThrottleFpsHz > 0)
     {
      float frameSeconds = 1.0f / World.Settings.ThrottleFpsHz - PresentOverheadSeconds;
      float sleepSeconds = frameSeconds - SleepOverHeadSeconds - DrawArgs.SecondsSinceLastFrame;
      if(sleepSeconds > 0)
      {
       Thread.Sleep((int) (1000 * sleepSeconds));
       while(DrawArgs.SecondsSinceLastFrame < frameSeconds)
       {
       }
      }
     }
     drawArgs.Present();
    }
   }
   catch(DeviceLostException)
   {
    AttemptRecovery();
   }
   catch(Exception caught)
   {
    Log.Write(caught);
   }
  }
  private static bool IsAppStillIdle
  {
   get
   {
    NativeMethods.Message msg;
    return !NativeMethods.PeekMessage(out msg, IntPtr.Zero, 0, 0, 0);
   }
  }
  protected override void OnPaint(PaintEventArgs e)
  {
   try
   {
    if(m_Device3d==null)
    {
     e.Graphics.Clear(SystemColors.Control);
     return;
    }
                Render();
    m_Device3d.Present();
   }
   catch(DeviceLostException)
   {
    try
    {
     AttemptRecovery();
     Render();
     m_Device3d.Present();
    }
    catch(DirectXException)
    {
    }
   }
  }
  System.Collections.ArrayList m_FrameTimes = new ArrayList();
  WorldWind.Widgets.RootWidget m_RootWidget = null;
        WorldWind.NewWidgets.RootWidget m_NewRootWidget = null;
  public void Render()
  {
   long startTicks = 0;
   PerformanceTimer.QueryPerformanceCounter(ref startTicks);
            try
            {
                this.drawArgs.BeginRender();
                System.Drawing.Color backgroundColor = System.Drawing.Color.Black;
                m_Device3d.Clear(ClearFlags.Target | ClearFlags.ZBuffer, backgroundColor, 1.0f, 0);
                if (m_World == null)
                {
                    m_Device3d.BeginScene();
                    m_Device3d.EndScene();
                    m_Device3d.Present();
                    Thread.Sleep(25);
                    return;
                }
                if (m_WorkerThread == null)
                {
                    m_WorkerThreadRunning = true;
                    m_WorkerThread = new Thread(new ThreadStart(WorkerThreadFunc));
                    m_WorkerThread.Name = "WorldWindow.WorkerThreadFunc";
                    m_WorkerThread.IsBackground = true;
                    if (World.Settings.UseBelowNormalPriorityUpdateThread)
                    {
                        m_WorkerThread.Priority = ThreadPriority.BelowNormal;
                    }
                    else
                    {
                        m_WorkerThread.Priority = ThreadPriority.Normal;
                    }
                    m_WorkerThread.Start();
                }
                this.drawArgs.WorldCamera.Update(m_Device3d);
                m_Device3d.BeginScene();
                if (renderWireFrame)
                    m_Device3d.RenderState.FillMode = FillMode.WireFrame;
                else
                    m_Device3d.RenderState.FillMode = FillMode.Solid;
                drawArgs.RenderWireFrame = renderWireFrame;
                m_World.Render(this.drawArgs);
                if (World.Settings.ShowCrosshairs)
                    this.DrawCrossHairs();
                frameCounter++;
                if (frameCounter == 30)
                {
                    fps = frameCounter / (float)(DrawArgs.CurrentFrameStartTicks - lastFpsUpdateTime) * PerformanceTimer.TicksPerSecond;
                    frameCounter = 0;
                    lastFpsUpdateTime = DrawArgs.CurrentFrameStartTicks;
                }
                m_RootWidget.Render(drawArgs);
                m_NewRootWidget.Render(drawArgs);
                if (saveScreenShotFilePath != null)
                    SaveScreenShot();
                drawArgs.device.RenderState.ZBufferEnable = false;
                if (renderWireFrame)
                    m_Device3d.RenderState.FillMode = FillMode.Solid;
                m_Device3d.RenderState.FogEnable = false;
                RenderPositionInfo();
                _menuBar.Render(drawArgs);
                m_FpsGraph.Render(drawArgs);
                if (m_World.OnScreenMessages != null)
                {
                    try
                    {
                        foreach (OnScreenMessage dm in m_World.OnScreenMessages)
                        {
                            int xPos = (int)Math.Round(dm.X * this.Width);
                            int yPos = (int)Math.Round(dm.Y * this.Height);
                            Rectangle posRect =
                                new Rectangle(xPos, yPos, this.Width, this.Height);
                            this.drawArgs.defaultDrawingFont.DrawText(null,
                                dm.Message, posRect,
                                DrawTextFormat.NoClip | DrawTextFormat.WordBreak,
                                Color.White);
                        }
                    }
                    catch (Exception)
                    {
                    }
                }
                m_Device3d.EndScene();
            }
            catch (Exception ex)
            {
                Log.Write(ex);
            }
   finally
   {
    if(World.Settings.ShowFpsGraph)
    {
     long endTicks = 0;
     PerformanceTimer.QueryPerformanceCounter(ref endTicks);
     float elapsedMilliSeconds = 1000.0f / (1000.0f*(float)(endTicks - startTicks)/PerformanceTimer.TicksPerSecond);
     m_FrameTimes.Add(elapsedMilliSeconds);
    }
    this.drawArgs.EndRender();
   }
   drawArgs.UpdateMouseCursor(this);
  }
  private LineGraph m_FpsGraph = new LineGraph();
  public void ResetToolbar()
  {
   lock(this._menuBar.LayersMenuButtons.SyncRoot)
   {
    foreach(IMenu m in this._menuBar.LayersMenuButtons)
    {
     m.Dispose();
    }
    this._menuBar.LayersMenuButtons.Clear();
   }
   lock(this._menuBar.ToolsMenuButtons.SyncRoot)
   {
     for(int i = 0; i < this._menuBar.ToolsMenuButtons.Count; i++)
     {
      IMenu m = (IMenu)this._menuBar.ToolsMenuButtons[i];
      if(m != null)
      {
       m.Dispose();
      }
     }
     this._menuBar.ToolsMenuButtons.Clear();
   }
  }
  private const int positionAlphaStep = 20;
  private int positionAlpha = 255;
  private int positionAlphaMin = 40;
  private int positionAlphaMax = 205;
  protected void RenderPositionInfo()
  {
   string captionText = _caption;
   captionText += "\n" + this.drawArgs.UpperLeftCornerText;
   if(World.Settings.ShowPosition)
   {
                double feetPerMeter = 3.2808399;
                double feetPerMile = 5280;
    string alt = null;
    float agl = (float)this.drawArgs.WorldCamera.AltitudeAboveTerrain;
                string dist = null;
                float dgl = (float)this.drawArgs.WorldCamera.Distance;
                if (World.Settings.DisplayUnits == Units.Metric)
                {
                    if (agl >= 1000)
                        alt = string.Format("{0:,.0} km", agl / 1000);
                    else
                        alt = string.Format("{0:f0} m", agl);
                    if (dgl > 100000)
                        dist = string.Format("{0:f2} km", dgl / 1000);
                    else
                        dist = string.Format("{0:f0} m", dgl);
                }
                else
                {
                    agl *= (float)feetPerMeter;
                    dgl *= (float)feetPerMeter;
                    if (agl >= feetPerMile)
                        alt = string.Format("{0:,.0} miles", agl / feetPerMile);
                    else
                        alt = string.Format("{0:f0} ft", agl);
                    if (dgl > 100000)
                        dist = string.Format("{0:f2} miles", dgl / feetPerMile);
                    else
                        dist = string.Format("{0:f0} ft", dgl);
                }
    double heading = this.drawArgs.WorldCamera.Heading.Degrees;
    if(heading<0)
     heading+=360;
    captionText += String.Format("Latitude: {0}\nLongitude: {1}\nHeading: {2:f2}\nTilt: {3}\nAltitude: {4}\nDistance: {5}\nFOV: {6}",
     this.drawArgs.WorldCamera.Latitude,
     this.drawArgs.WorldCamera.Longitude,
     heading,
     this.drawArgs.WorldCamera.Tilt,
     alt,
     dist,
     this.drawArgs.WorldCamera.Fov );
    if(drawArgs.WorldCamera.AltitudeAboveTerrain < 300000)
    {
                    double terrainElevation = drawArgs.WorldCamera.TerrainElevation;
                    if (World.Settings.DisplayUnits == Units.Metric)
                    {
                        captionText += String.Format("\nTerrain Elevation: {0:n} meters\n", terrainElevation);
                    }
                    else
                    {
                        captionText += String.Format("\nTerrain Elevation: {0:n} feet\n", terrainElevation * feetPerMeter);
                    }
    }
   }
            if (this.showDiagnosticInfo)
            {
                string bytesTransferred;
                float bytes = DataRequest.TotalBytes;
                if (bytes > 1024)
                {
                    bytes /= 1024;
                    if (bytes > 1024)
                    {
                        bytes /= 1024;
                        if (bytes > 1024)
                        {
                            bytes /= 1024;
                            bytesTransferred = bytes.ToString("0.#") + "G";
                        }
                        else
                            bytesTransferred = bytes.ToString("0.#") + "M";
                    }
                    else
                        bytesTransferred = bytes.ToString("0.#") + "K";
                }
                else
                    bytesTransferred = bytes.ToString();
                captionText +=
                    "\nAvailable Texture Memory: " + (m_Device3d.AvailableTextureMemory / 1024).ToString("N0") + " kB" +
                    "\nBoundary Points: " + this.drawArgs.numBoundaryPointsRendered.ToString() + " / " + this.drawArgs.numBoundaryPointsTotal.ToString() + " : " + this.drawArgs.numBoundariesDrawn.ToString() +
                    "\nTiles Drawn: " + (this.drawArgs.numberTilesDrawn * 0.25f).ToString() +
                    "\n" + this.drawArgs.WorldCamera +
                    "\nFPS: " + this.fps.ToString("f1") +
                    "\nRO: " + m_World.RenderableObjects.Count.ToString("f0") +
                    "\nmLat: " + this.cLat.Degrees.ToString() +
                    "\nmLon: " + this.cLon.Degrees.ToString() +
                    "\nTotal Data Requests: " + DataRequest.TotalRequests + ", " + DataRequest.CacheHits + " cache hits (" + string.Format("{0:f2}", 100.0 * DataRequest.CacheHits / DataRequest.TotalRequests) + " %)" +
                    "\nCurrent Data Requests: " + DataStore.ActiveRequestCount +" active, " + DataStore.PendingRequestCount + " pending." +
                    "\nBytes Transferred: " + bytesTransferred +
                    "\n" + TimeKeeper.CurrentTimeUtc.ToLocalTime().ToLongTimeString();
            }
   captionText = captionText.Trim();
   DrawTextFormat dtf = DrawTextFormat.NoClip | DrawTextFormat.WordBreak | DrawTextFormat.Right;
   int x = 7;
   int y = _menuBar!=null && World.Settings.ShowToolbar ? 65 : 7;
   Rectangle textRect = Rectangle.FromLTRB(x,y, this.Width-8, this.Height-8 );
   if (_menuBar.IsActive)
   {
    positionAlpha -= positionAlphaStep;
    if (positionAlpha<positionAlphaMin)
    {
     positionAlpha=positionAlphaMin;
    }
   }
   else
   {
    positionAlpha += positionAlphaStep;
    if(positionAlpha>positionAlphaMax)
     positionAlpha = positionAlphaMax;
   }
   int positionBackColor = positionAlpha << 24;
   int positionForeColor = (int)((uint)(positionAlpha << 24) + 0xffffffu);
   this.drawArgs.defaultDrawingFont.DrawText( null, captionText, textRect, dtf, positionBackColor);
   textRect.Offset(-1,-1);
   this.drawArgs.defaultDrawingFont.DrawText( null, captionText, textRect, dtf, positionForeColor);
  }
  Line crossHairs;
  int crossHairColor = Color.GhostWhite.ToArgb();
  protected void DrawCrossHairs()
  {
   int crossHairSize = 10;
   if(this.crossHairs == null)
   {
    crossHairs = new Line(m_Device3d);
   }
   Vector2[] vertical = new Vector2[2];
   Vector2[] horizontal = new Vector2[2];
   horizontal[0].X = this.Width / 2 - crossHairSize;
   horizontal[0].Y = this.Height / 2;
   horizontal[1].X = this.Width / 2 + crossHairSize;
   horizontal[1].Y = this.Height / 2;
   vertical[0].X = this.Width / 2;
   vertical[0].Y = this.Height / 2 - crossHairSize;
   vertical[1].X = this.Width / 2;
   vertical[1].Y = this.Height / 2 + crossHairSize;
   crossHairs.Begin();
   crossHairs.Draw(horizontal, crossHairColor);
   crossHairs.Draw(vertical, crossHairColor);
   crossHairs.End();
  }
  protected void AttemptRecovery()
  {
   try
   {
    m_Device3d.TestCooperativeLevel();
   }
   catch (DeviceLostException)
   {
   }
   catch (DeviceNotResetException)
   {
    try
    {
     m_Device3d.Reset(m_presentParams);
    }
    catch (DeviceLostException)
    {
    }
   }
  }
        public void HandleMouseWheel(MouseEventArgs e)
        {
            OnMouseWheel(e);
        }
  protected override void OnMouseWheel(MouseEventArgs e)
        {
            try
   {
                bool handled = false;
                if (m_NewRootWidget != null)
                {
                    try
                    {
                        handled = m_NewRootWidget.OnMouseWheel(e);
                    }
                    finally
                    {
                    }
                }
    if(!handled && this._menuBar.OnMouseWheel(e))
     return;
                if (!handled)
                {
                    this.drawArgs.WorldCamera.ZoomStepped(e.Delta / 120.0f);
                }
   }
   finally
   {
    base.OnMouseWheel(e);
   }
  }
  protected override void OnKeyDown(KeyEventArgs e)
  {
   try
   {
    e.Handled = HandleKeyDown(e);
    base.OnKeyDown(e);
   }
   catch (Exception caught)
   {
    MessageBox.Show( caught.Message, "Operation failed", MessageBoxButtons.OK, MessageBoxIcon.Error );
   }
  }
  protected override void OnKeyUp(KeyEventArgs e)
  {
   try
   {
    e.Handled = HandleKeyUp(e);
    base.OnKeyUp(e);
   }
   catch (Exception caught)
   {
    MessageBox.Show( caught.Message, "Operation failed", MessageBoxButtons.OK, MessageBoxIcon.Error );
   }
  }
  protected override void OnKeyPress(KeyPressEventArgs e)
  {
            if (m_RootWidget != null)
            {
                bool handled = m_RootWidget.OnKeyPress(e);
                e.Handled = handled;
            }
            if (m_NewRootWidget != null)
            {
                bool handled = m_NewRootWidget.OnKeyPress(e);
                e.Handled = handled;
            }
            base.OnKeyPress(e);
  }
  [SecurityPermission(SecurityAction.LinkDemand, UnmanagedCode=true), SecurityPermission(SecurityAction.InheritanceDemand, UnmanagedCode=true)]
  public override bool PreProcessMessage(ref Message msg)
  {
   const int WM_KEYDOWN = 0x0100;
   if (msg.Msg == WM_KEYDOWN)
   {
    Keys key = (Keys)msg.WParam.ToInt32();
    switch (key)
    {
     case Keys.Left:
     case Keys.Up:
     case Keys.Right:
     case Keys.Down:
      OnKeyDown(new KeyEventArgs(key));
      msg.Result = (IntPtr) 1;
      return true;
    }
   }
   return base.PreProcessMessage (ref msg);
  }
  public bool HandleKeyDown(KeyEventArgs e)
  {
   bool handled = this.m_RootWidget.OnKeyDown(e);
   if(handled)
    return handled;
            handled = this.m_NewRootWidget.OnKeyDown(e);
            if (handled)
                return handled;
   if (e.Alt)
   {
    switch (e.KeyCode)
    {
     case Keys.C:
      World.Settings.ShowCrosshairs = !World.Settings.ShowCrosshairs;
      return true;
     case Keys.Add:
     case Keys.Oemplus:
     case Keys.Home:
     case Keys.NumPad7:
      this.drawArgs.WorldCamera.Fov -= Angle.FromDegrees( 5 );
      return true;
     case Keys.Subtract:
     case Keys.OemMinus:
     case Keys.End:
     case Keys.NumPad1:
      this.drawArgs.WorldCamera.Fov += Angle.FromDegrees( 5 );
      return true;
    }
   }
   else if (e.Control)
   {
   }
   else
   {
    switch (e.KeyCode)
    {
     case Keys.A:
      Angle rotateClockwise = Angle.FromRadians(0.01f);
      this.drawArgs.WorldCamera.Heading += rotateClockwise;
      this.drawArgs.WorldCamera.RotationYawPitchRoll(Angle.Zero, Angle.Zero, rotateClockwise);
      return true;
     case Keys.D:
      Angle rotateCounterclockwise = Angle.FromRadians(-0.01f);
      this.drawArgs.WorldCamera.Heading += rotateCounterclockwise;
      this.drawArgs.WorldCamera.RotationYawPitchRoll(Angle.Zero, Angle.Zero, rotateCounterclockwise);
      return true;
     case Keys.W:
      this.drawArgs.WorldCamera.Tilt += Angle.FromDegrees( -1.0f );
      return true;
     case Keys.S:
      this.drawArgs.WorldCamera.Tilt+= Angle.FromDegrees( 1.0f );
      return true;
     case Keys.Left:
     case Keys.H:
     case Keys.NumPad4:
      Angle panLeft = Angle.FromRadians((float)-1 * (this.drawArgs.WorldCamera.Altitude) * (1 / (300 * this.CurrentWorld.EquatorialRadius)));
      this.drawArgs.WorldCamera.RotationYawPitchRoll( panLeft, Angle.Zero, Angle.Zero);
      return true;
     case Keys.Down:
     case Keys.J:
     case Keys.NumPad2:
      Angle panDown = Angle.FromRadians((float)-1 * (this.drawArgs.WorldCamera.Altitude) * (1 / (300 * this.CurrentWorld.EquatorialRadius)));
      this.drawArgs.WorldCamera.RotationYawPitchRoll(Angle.Zero, panDown, Angle.Zero);
      return true;
     case Keys.Right:
     case Keys.K:
     case Keys.NumPad6:
      Angle panRight = Angle.FromRadians((float)1 * (this.drawArgs.WorldCamera.Altitude) * (1 / (300 * this.CurrentWorld.EquatorialRadius)));
      this.drawArgs.WorldCamera.RotationYawPitchRoll(panRight, Angle.Zero, Angle.Zero);
      return true;
     case Keys.Up:
     case Keys.U:
     case Keys.NumPad8:
      Angle panUp = Angle.FromRadians((float)1 * (this.drawArgs.WorldCamera.Altitude) * (1 / (300 * this.CurrentWorld.EquatorialRadius)));
      this.drawArgs.WorldCamera.RotationYawPitchRoll(Angle.Zero, panUp, Angle.Zero);
      return true;
     case Keys.Add:
     case Keys.Oemplus:
     case Keys.Home:
     case Keys.NumPad7:
      this.drawArgs.WorldCamera.ZoomStepped( World.Settings.CameraZoomStepKeyboard);
      return true;
     case Keys.Subtract:
     case Keys.OemMinus:
     case Keys.End:
     case Keys.NumPad1:
      this.drawArgs.WorldCamera.ZoomStepped( -World.Settings.CameraZoomStepKeyboard );
      return true;
    }
   }
   return false;
  }
  public bool HandleKeyUp(KeyEventArgs e)
  {
   bool handled = m_RootWidget.OnKeyUp(e);
   if(handled)
   {
    e.Handled = handled;
    return handled;
   }
            handled = m_NewRootWidget.OnKeyUp(e);
            if (handled)
            {
                e.Handled = handled;
                return handled;
            }
   if (e.Alt)
   {
   }
   else if (e.Control)
   {
    switch (e.KeyCode)
    {
     case Keys.D:
      this.showDiagnosticInfo = !this.showDiagnosticInfo;
      return true;
     case Keys.W:
      renderWireFrame = !renderWireFrame;
      return true;
    }
   }
   else
   {
    switch (e.KeyCode)
    {
     case Keys.Space:
     case Keys.Clear:
      this.drawArgs.WorldCamera.Reset();
      return true;
    }
   }
   return false;
  }
  protected override void OnMouseDown(MouseEventArgs e)
  {
   DrawArgs.LastMousePosition.X = e.X;
   DrawArgs.LastMousePosition.Y = e.Y;
   mouseDownStartPosition.X = e.X;
   mouseDownStartPosition.Y = e.Y;
   try
   {
    bool handled = false;
    handled = m_RootWidget.OnMouseDown(e);
                if (!handled)
                {
                    handled = m_NewRootWidget.OnMouseDown(e);
                }
    if(!handled)
    {
     if(!this._menuBar.OnMouseDown(e))
     {
     }
    }
   }
   finally
   {
    if(e.Button == MouseButtons.Left)
     DrawArgs.IsLeftMouseButtonDown = true;
    if(e.Button == MouseButtons.Right)
     DrawArgs.IsRightMouseButtonDown = true;
    base.OnMouseDown(e);
   }
  }
        bool isDoubleClick = false;
        protected override void OnMouseDoubleClick(MouseEventArgs e)
        {
            isDoubleClick = true;
            base.OnMouseDoubleClick(e);
        }
  protected override void OnMouseUp(MouseEventArgs e)
        {
            DrawArgs.LastMousePosition.X = e.X;
   DrawArgs.LastMousePosition.Y = e.Y;
   try
   {
                bool handled = false;
    handled = m_RootWidget.OnMouseUp(e);
                if (!handled)
                {
                    handled = m_NewRootWidget.OnMouseUp(e);
                }
    if(!handled)
    {
     if(mouseDownStartPosition==Point.Empty)
      return;
     mouseDownStartPosition = Point.Empty;
     if(!this.isMouseDragging)
     {
      if(this._menuBar.OnMouseUp(e))
       return;
     }
     if(m_World == null)
      return;
                    if (isDoubleClick)
                    {
                        isDoubleClick = false;
                        if (e.Button == MouseButtons.Left)
                        {
                            drawArgs.WorldCamera.Zoom(World.Settings.CameraDoubleClickZoomFactor);
                        }
                        else if (e.Button == MouseButtons.Right)
                        {
                            drawArgs.WorldCamera.Zoom(-World.Settings.CameraDoubleClickZoomFactor);
                        }
                    }
                    else
                    {
                        if (e.Button == MouseButtons.Left)
                        {
                            if (this.isMouseDragging)
                            {
                                this.isMouseDragging = false;
                            }
                            else
                            {
                                if (!m_World.PerformSelectionAction(this.drawArgs))
                                {
                                    Angle targetLatitude;
                                    Angle targetLongitude;
                                    this.drawArgs.WorldCamera.PickingRayIntersection(
                                        DrawArgs.LastMousePosition.X,
                                        DrawArgs.LastMousePosition.Y,
                                        out targetLatitude,
                                        out targetLongitude);
                                    if (!Angle.IsNaN(targetLatitude))
                                        this.drawArgs.WorldCamera.PointGoto(targetLatitude, targetLongitude);
                                }
                            }
                        }
                        else if (e.Button == MouseButtons.Right)
                        {
                            if (this.isMouseDragging)
                                this.isMouseDragging = false;
                            else
                            {
                                if (!m_World.PerformSelectionAction(this.drawArgs))
                                {
                                }
                            }
                        }
                    }
    }
   }
   finally
   {
    if(e.Button == MouseButtons.Left)
     DrawArgs.IsLeftMouseButtonDown = false;
    if(e.Button == MouseButtons.Right)
     DrawArgs.IsRightMouseButtonDown = false;
    base.OnMouseUp(e);
   }
  }
  protected override void OnMouseMove(MouseEventArgs e)
  {
   DrawArgs.MouseCursor = CursorType.Arrow;
   try
   {
    bool handled = false;
                if (!isMouseDragging)
                {
                    handled = m_RootWidget.OnMouseMove(e);
                    if (!handled)
                    {
                        handled = m_NewRootWidget.OnMouseMove(e);
                    }
                }
    if(!handled)
    {
     int deltaX = e.X - DrawArgs.LastMousePosition.X;
     int deltaY = e.Y - DrawArgs.LastMousePosition.Y;
     float deltaXNormalized = (float)deltaX/drawArgs.screenWidth;
     float deltaYNormalized = (float)deltaY/drawArgs.screenHeight;
     if(!this.isMouseDragging)
     {
      if(this._menuBar.OnMouseMove(e))
      {
       base.OnMouseMove(e);
       return;
      }
     }
     if(mouseDownStartPosition == Point.Empty)
      return;
     bool isMouseLeftButtonDown = ((int)e.Button & (int)MouseButtons.Left) != 0;
     bool isMouseRightButtonDown = ((int)e.Button & (int)MouseButtons.Right) != 0;
     if (isMouseLeftButtonDown || isMouseRightButtonDown)
     {
      int dx = this.mouseDownStartPosition.X - e.X;
      int dy = this.mouseDownStartPosition.Y - e.Y;
      int distanceSquared = dx*dx + dy*dy;
      if (distanceSquared > 3*3)
       this.isMouseDragging = true;
     }
     if (isMouseLeftButtonDown && !isMouseRightButtonDown)
     {
      Angle prevLat, prevLon;
      this.drawArgs.WorldCamera.PickingRayIntersection(
       DrawArgs.LastMousePosition.X,
       DrawArgs.LastMousePosition.Y,
       out prevLat,
       out prevLon);
      Angle curLat, curLon;
      this.drawArgs.WorldCamera.PickingRayIntersection(
       e.X,
       e.Y,
       out curLat,
       out curLon);
      if(World.Settings.CameraTwistLock)
      {
       if(Angle.IsNaN(curLat)||Angle.IsNaN(prevLat))
       {
        Angle deltaLat = Angle.FromRadians((double)deltaY * (this.drawArgs.WorldCamera.Altitude) / (800 * this.CurrentWorld.EquatorialRadius));
        Angle deltaLon = Angle.FromRadians((double)-deltaX * (this.drawArgs.WorldCamera.Altitude) / (800 * this.CurrentWorld.EquatorialRadius));
        this.drawArgs.WorldCamera.Pan(deltaLat, deltaLon);
       }
       else
       {
        Angle lat = prevLat-curLat;
        Angle lon = prevLon-curLon;
        this.drawArgs.WorldCamera.Pan( lat, lon );
       }
      }
      else
      {
       double factor = (this.drawArgs.WorldCamera.Altitude) / (1500 * this.CurrentWorld.EquatorialRadius);
       drawArgs.WorldCamera.RotationYawPitchRoll(
        Angle.FromRadians(DrawArgs.LastMousePosition.X-e.X)*factor,
        Angle.FromRadians(e.Y-DrawArgs.LastMousePosition.Y)*factor,
        Angle.Zero);
      }
     }
     else if (!isMouseLeftButtonDown && isMouseRightButtonDown)
     {
      Angle deltaEyeDirection = Angle.FromRadians( -deltaXNormalized * World.Settings.CameraRotationSpeed);
      this.drawArgs.WorldCamera.RotationYawPitchRoll( Angle.Zero, Angle.Zero, deltaEyeDirection );
      this.drawArgs.WorldCamera.Tilt += Angle.FromRadians(deltaYNormalized * World.Settings.CameraRotationSpeed );
     }
     else if (isMouseLeftButtonDown && isMouseRightButtonDown)
     {
      if(Math.Abs(deltaYNormalized) > float.Epsilon)
       this.drawArgs.WorldCamera.Zoom( -deltaYNormalized*World.Settings.CameraZoomAnalogFactor );
      if (!World.Settings.CameraBankLock)
       this.drawArgs.WorldCamera.Bank -= Angle.FromRadians( deltaXNormalized * World.Settings.CameraRotationSpeed );
     }
    }
   }
   catch
   {
   }
   finally
   {
    this.drawArgs.WorldCamera.PickingRayIntersection(
     e.X,
     e.Y,
     out cLat,
     out cLon);
    DrawArgs.LastMousePosition.X = e.X;
    DrawArgs.LastMousePosition.Y = e.Y;
    base.OnMouseMove(e);
   }
  }
  Angle cLat, cLon;
  protected override void OnMouseLeave(EventArgs e)
  {
   if(_menuBar!=null)
    _menuBar.OnMouseMove(new MouseEventArgs(MouseButtons.None, 0,-1,-1,0));
   base.OnMouseLeave(e);
  }
  protected void SaveScreenShot()
  {
   try
   {
    using( Surface backbuffer = m_Device3d.GetBackBuffer(0, 0, BackBufferType.Mono) )
     SurfaceLoader.Save(saveScreenShotFilePath, saveScreenShotImageFileFormat, backbuffer);
    saveScreenShotFilePath = null;
   }
   catch(InvalidCallException caught)
   {
    MessageBox.Show(caught.Message, "Screenshot save failed.", MessageBoxButtons.OK, MessageBoxIcon.Error );
   }
  }
  protected override void Dispose( bool disposing )
  {
   if( disposing )
   {
    if(m_WorkerThread != null && m_WorkerThread.IsAlive)
    {
     m_WorkerThreadRunning = false;
     m_WorkerThread.Abort();
    }
    m_FpsTimer.Stop();
    if(m_World != null)
    {
     m_World.Dispose();
     m_World = null;
    }
    if(this.drawArgs != null)
    {
     this.drawArgs.Dispose();
     this.drawArgs = null;
    }
    if(this._menuBar!=null)
    {
     this._menuBar.Dispose();
     this._menuBar = null;
    }
    m_Device3d.Dispose();
   }
   base.Dispose( disposing );
   GC.SuppressFinalize(this);
  }
  private void m_Device3d_DeviceResizing(object sender, CancelEventArgs e)
  {
   if(this.Size.Width == 0 || this.Size.Height == 0)
   {
    e.Cancel = true;
    return;
   }
   this.drawArgs.screenHeight = this.Height;
   this.drawArgs.screenWidth = this.Width;
  }
  private static bool IsInDesignMode()
  {
   return Application.ExecutablePath.ToUpper(CultureInfo.InvariantCulture).EndsWith("DEVENV.EXE");
  }
  private void InitializeGraphics()
  {
   m_presentParams = new PresentParameters();
   m_presentParams.Windowed = true;
   m_presentParams.SwapEffect = SwapEffect.Discard;
   m_presentParams.AutoDepthStencilFormat = DepthFormat.D16;
   m_presentParams.EnableAutoDepthStencil = true;
   if(!World.Settings.VSync)
    m_presentParams.PresentationInterval = PresentInterval.Immediate;
   int adapterOrdinal = 0;
   try
   {
    adapterOrdinal = Manager.Adapters.Default.Adapter;
   }
   catch
   {
    throw new NotAvailableException();
   }
   DeviceType dType = DeviceType.Hardware;
   foreach(AdapterInformation ai in Manager.Adapters)
   {
    if(ai.Information.Description.IndexOf("NVPerfHUD") >= 0)
    {
     adapterOrdinal = ai.Adapter;
     dType = DeviceType.Reference;
    }
   }
   CreateFlags flags = CreateFlags.SoftwareVertexProcessing;
   Caps caps = Manager.GetDeviceCaps(adapterOrdinal, DeviceType.Hardware);
   if(caps.DeviceCaps.SupportsHardwareTransformAndLight)
    flags = CreateFlags.HardwareVertexProcessing;
   flags |= CreateFlags.MultiThreaded | CreateFlags.FpuPreserve;
   try
   {
    m_Device3d = new Device(adapterOrdinal, dType, this, flags, m_presentParams);
   }
   catch( Microsoft.DirectX.DirectXException )
   {
    throw new NotSupportedException("Unable to create the Direct3D m_Device3d.");
   }
   m_Device3d.DeviceReset += new EventHandler(OnDeviceReset);
   m_Device3d.DeviceResizing += new CancelEventHandler(m_Device3d_DeviceResizing);
   OnDeviceReset(m_Device3d, null);
  }
  private void OnDeviceReset(object sender, EventArgs e)
  {
   if( m_Device3d.DeviceCaps.TextureFilterCaps.SupportsMinifyAnisotropic)
   {
    m_Device3d.SamplerState[0].MinFilter = TextureFilter.Anisotropic;
   }
   else if( m_Device3d.DeviceCaps.TextureFilterCaps.SupportsMinifyLinear)
   {
    m_Device3d.SamplerState[0].MinFilter = TextureFilter.Linear;
   }
   if( m_Device3d.DeviceCaps.TextureFilterCaps.SupportsMagnifyAnisotropic )
   {
    m_Device3d.SamplerState[0].MagFilter = TextureFilter.Anisotropic;
   }
   else if( m_Device3d.DeviceCaps.TextureFilterCaps.SupportsMagnifyLinear )
   {
    m_Device3d.SamplerState[0].MagFilter = TextureFilter.Linear;
   }
   m_Device3d.SamplerState[0].AddressU = TextureAddress.Clamp;
   m_Device3d.SamplerState[0].AddressV = TextureAddress.Clamp;
   m_Device3d.RenderState.Clipping = true;
   m_Device3d.RenderState.CullMode = Cull.Clockwise;
   m_Device3d.RenderState.Lighting = false;
   m_Device3d.RenderState.Ambient = World.Settings.StandardAmbientColor;
   m_Device3d.RenderState.ZBufferEnable = true;
   m_Device3d.RenderState.AlphaBlendEnable = true;
   m_Device3d.RenderState.SourceBlend = Blend.SourceAlpha;
   m_Device3d.RenderState.DestinationBlend = Blend.InvSourceAlpha;
  }
  private void WorkerThreadFunc()
  {
   const int refreshIntervalMs = 150;
   while(m_WorkerThreadRunning)
   {
    try
    {
     if(World.Settings.UseBelowNormalPriorityUpdateThread && m_WorkerThread.Priority == System.Threading.ThreadPriority.Normal)
     {
      m_WorkerThread.Priority = System.Threading.ThreadPriority.BelowNormal;
     }
     else if(!World.Settings.UseBelowNormalPriorityUpdateThread && m_WorkerThread.Priority == System.Threading.ThreadPriority.BelowNormal)
     {
      m_WorkerThread.Priority = System.Threading.ThreadPriority.Normal;
     }
     long startTicks = 0;
     PerformanceTimer.QueryPerformanceCounter(ref startTicks);
                    DataStore.Update();
     m_World.Update(this.drawArgs);
     long endTicks = 0;
     PerformanceTimer.QueryPerformanceCounter(ref endTicks);
     float elapsedMilliSeconds = 1000*(float)(endTicks - startTicks)/PerformanceTimer.TicksPerSecond;
     float remaining = refreshIntervalMs - elapsedMilliSeconds;
     if(remaining > 0)
      Thread.Sleep((int)remaining);
    }
    catch(Exception caught)
    {
     Log.Write(caught);
    }
   }
  }
  public void SetDisplayMessages(IList messages)
  {
   m_World.OnScreenMessages = messages;
  }
  public void SetLatLonGridShow(bool show)
  {
   World.Settings.ShowLatLonLines = show;
  }
  public void SetLayers(IList layers)
  {
   if (layers != null)
   {
    foreach (LayerDescriptor ld in layers)
    {
     this.CurrentWorld.SetLayerOpacity(ld.Category, ld.Name, (float)ld.Opacity * 0.01f);
    }
   }
  }
  public void SetVerticalExaggeration(double exageration)
  {
   World.Settings.VerticalExaggeration = (float)exageration;
  }
  public void SetViewDirection(String type, double horiz, double vert, double elev)
  {
   this.drawArgs.WorldCamera.SetPosition(this.drawArgs.WorldCamera.Latitude.Degrees, this.drawArgs.WorldCamera.Longitude.Degrees, horiz,
    this.drawArgs.WorldCamera.Altitude, vert);
  }
  public void SetViewPosition(double degreesLatitude, double degreesLongitude,
   double metersElevation)
  {
   this.drawArgs.WorldCamera.SetPosition(degreesLatitude, degreesLongitude, this.drawArgs.WorldCamera.Heading.Degrees,
    metersElevation, this.drawArgs.WorldCamera.Tilt.Degrees);
  }
  public void SetWmsImage(WmsDescriptor imageA, WmsDescriptor imageB, double alpha)
  {
   if (imageA != null)
   {
    System.Console.Write(imageA.Url.ToString() + " ");
    System.Console.WriteLine(imageA.Opacity);
   }
   if (imageB != null)
   {
    System.Console.Write(imageB.Url.ToString() + " ");
    System.Console.Write(imageB.Opacity);
    System.Console.Write(" alpha = ");
    System.Console.WriteLine(alpha);
   }
  }
  bool m_FpsUpdate = false;
  private void m_FpsTimer_Elapsed(object sender, System.Timers.ElapsedEventArgs e)
  {
   if(m_FpsUpdate)
    return;
   m_FpsUpdate = true;
   try
   {
    if(World.Settings.ShowFpsGraph)
    {
     if(!m_FpsGraph.Visible)
     {
      m_FpsGraph.Visible = true;
     }
     if(m_FrameTimes.Count > World.Settings.FpsFrameCount)
     {
      m_FrameTimes.RemoveRange(0, m_FrameTimes.Count - World.Settings.FpsFrameCount);
     }
     m_FpsGraph.Size = new Size((int)(Width * .5), (int)(Height * .1));
     m_FpsGraph.Location = new Point((int)(Width * .35), (int)(Height * .895));
     m_FpsGraph.Values = (float[])m_FrameTimes.ToArray(typeof(float));
    }
    else
    {
     if(m_FpsGraph.Visible)
     {
      m_FpsGraph.Visible = false;
     }
    }
   }
   catch(Exception ex)
   {
    Log.Write(ex);
   }
   m_FpsUpdate = false;
  }
 }
}
