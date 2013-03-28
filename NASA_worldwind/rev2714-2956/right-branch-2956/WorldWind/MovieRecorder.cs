using Microsoft.DirectX.Direct3D;
using Microsoft.DirectX;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.IO;
using System.Threading;
using System.Windows.Forms;
using System;
using WorldWind.Camera;
using WorldWind;
using WorldWind.Net;
using WorldWind.PluginEngine;
namespace Mashiharu.Sample
{
 public class MovieRecorderDialog : System.Windows.Forms.Form
 {
  private System.Windows.Forms.Button buttonBrowse;
  private System.Windows.Forms.Label label2;
  private System.Windows.Forms.Label label3;
  private System.Windows.Forms.Button buttonPlay;
  private System.Windows.Forms.Button buttonRecord;
  private System.Windows.Forms.Button buttonCancel;
  private System.ComponentModel.Container components = null;
  PathCamera camera;
  private System.Windows.Forms.TextBox scriptFile;
  private System.Windows.Forms.Label label4;
  WorldWind.WorldWindow worldWindow;
  MainApplication worldWind;
  Plugin plugin;
  private System.Windows.Forms.Label label6;
  private System.Windows.Forms.OpenFileDialog openFileDialog;
  private System.Windows.Forms.Label label5;
  private System.Windows.Forms.NumericUpDown frameWidth;
  private System.Windows.Forms.NumericUpDown frameWaitTime;
  private System.Windows.Forms.Label label1;
  private System.Windows.Forms.Label label7;
  private System.Windows.Forms.NumericUpDown frameEnd;
  private System.Windows.Forms.NumericUpDown frameStart;
  private System.Windows.Forms.Label label8;
  private System.Windows.Forms.TextBox outputFiles;
  private System.Windows.Forms.Button buttonEdit;
  private System.Windows.Forms.NumericUpDown frameHeight;
  public MovieRecorderDialog(Plugin plugin)
  {
   InitializeComponent();
   this.plugin = plugin;
   this.worldWind = plugin.Application;
   this.worldWindow = worldWind.WorldWindow;
  }
  protected override void Dispose( bool disposing )
  {
   if( disposing )
   {
    if(components != null)
    {
     components.Dispose();
    }
   }
   base.Dispose( disposing );
  }
  private void InitializeComponent()
  {
   this.scriptFile = new System.Windows.Forms.TextBox();
   this.buttonBrowse = new System.Windows.Forms.Button();
   this.frameWaitTime = new System.Windows.Forms.NumericUpDown();
   this.label2 = new System.Windows.Forms.Label();
   this.label3 = new System.Windows.Forms.Label();
   this.buttonRecord = new System.Windows.Forms.Button();
   this.buttonPlay = new System.Windows.Forms.Button();
   this.buttonCancel = new System.Windows.Forms.Button();
   this.label4 = new System.Windows.Forms.Label();
   this.frameWidth = new System.Windows.Forms.NumericUpDown();
   this.frameHeight = new System.Windows.Forms.NumericUpDown();
   this.label5 = new System.Windows.Forms.Label();
   this.label6 = new System.Windows.Forms.Label();
   this.openFileDialog = new System.Windows.Forms.OpenFileDialog();
   this.label1 = new System.Windows.Forms.Label();
   this.label7 = new System.Windows.Forms.Label();
   this.frameEnd = new System.Windows.Forms.NumericUpDown();
   this.frameStart = new System.Windows.Forms.NumericUpDown();
   this.label8 = new System.Windows.Forms.Label();
   this.outputFiles = new System.Windows.Forms.TextBox();
   this.buttonEdit = new System.Windows.Forms.Button();
   ((System.ComponentModel.ISupportInitialize)(this.frameWaitTime)).BeginInit();
   ((System.ComponentModel.ISupportInitialize)(this.frameWidth)).BeginInit();
   ((System.ComponentModel.ISupportInitialize)(this.frameHeight)).BeginInit();
   ((System.ComponentModel.ISupportInitialize)(this.frameEnd)).BeginInit();
   ((System.ComponentModel.ISupportInitialize)(this.frameStart)).BeginInit();
   this.SuspendLayout();
   this.scriptFile.Location = new System.Drawing.Point(16, 34);
   this.scriptFile.Name = "scriptFile";
   this.scriptFile.Size = new System.Drawing.Size(187, 20);
   this.scriptFile.TabIndex = 1;
   this.scriptFile.Text = "MovieRecorder.txt";
   this.buttonBrowse.Location = new System.Drawing.Point(206, 33);
   this.buttonBrowse.Name = "buttonBrowse";
   this.buttonBrowse.Size = new System.Drawing.Size(24, 20);
   this.buttonBrowse.TabIndex = 2;
   this.buttonBrowse.Text = "&...";
   this.buttonBrowse.Click += new System.EventHandler(this.buttonBrowse_Click);
   this.frameWaitTime.Increment = new System.Decimal(new int[] {
                                  100,
                                  0,
                                  0,
                                  0});
   this.frameWaitTime.Location = new System.Drawing.Point(88, 69);
   this.frameWaitTime.Maximum = new System.Decimal(new int[] {
                                 1000000,
                                 0,
                                 0,
                                 0});
   this.frameWaitTime.Minimum = new System.Decimal(new int[] {
                                 1,
                                 0,
                                 0,
                                 0});
   this.frameWaitTime.Name = "frameWaitTime";
   this.frameWaitTime.Size = new System.Drawing.Size(64, 20);
   this.frameWaitTime.TabIndex = 4;
   this.frameWaitTime.Value = new System.Decimal(new int[] {
                                100,
                                0,
                                0,
                                0});
   this.label2.Location = new System.Drawing.Point(14, 72);
   this.label2.Name = "label2";
   this.label2.Size = new System.Drawing.Size(70, 16);
   this.label2.TabIndex = 3;
   this.label2.Text = "Pre-roll time:";
   this.label3.Location = new System.Drawing.Point(152, 72);
   this.label3.Name = "label3";
   this.label3.Size = new System.Drawing.Size(32, 16);
   this.label3.TabIndex = 5;
   this.label3.Text = "ms";
   this.buttonRecord.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
   this.buttonRecord.Location = new System.Drawing.Point(104, 232);
   this.buttonRecord.Name = "buttonRecord";
   this.buttonRecord.TabIndex = 11;
   this.buttonRecord.Text = "&Record";
   this.buttonRecord.Click += new System.EventHandler(this.buttonRecord_Click);
   this.buttonPlay.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
   this.buttonPlay.Location = new System.Drawing.Point(16, 232);
   this.buttonPlay.Name = "buttonPlay";
   this.buttonPlay.TabIndex = 10;
   this.buttonPlay.Text = "&Play";
   this.buttonPlay.Click += new System.EventHandler(this.buttonPlay_Click);
   this.buttonCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
   this.buttonCancel.Location = new System.Drawing.Point(192, 232);
   this.buttonCancel.Name = "buttonCancel";
   this.buttonCancel.TabIndex = 12;
   this.buttonCancel.Text = "&Stop";
   this.buttonCancel.Click += new System.EventHandler(this.buttonCancel_Click);
   this.label4.Location = new System.Drawing.Point(16, 16);
   this.label4.Name = "label4";
   this.label4.Size = new System.Drawing.Size(64, 16);
   this.label4.TabIndex = 0;
   this.label4.Text = "Script file:";
   this.frameWidth.Increment = new System.Decimal(new int[] {
                                 16,
                                 0,
                                 0,
                                 0});
   this.frameWidth.Location = new System.Drawing.Point(114, 144);
   this.frameWidth.Maximum = new System.Decimal(new int[] {
                                1000000,
                                0,
                                0,
                                0});
   this.frameWidth.Minimum = new System.Decimal(new int[] {
                                1,
                                0,
                                0,
                                0});
   this.frameWidth.Name = "frameWidth";
   this.frameWidth.Size = new System.Drawing.Size(56, 20);
   this.frameWidth.TabIndex = 7;
   this.frameWidth.Value = new System.Decimal(new int[] {
                               640,
                               0,
                               0,
                               0});
   this.frameHeight.Increment = new System.Decimal(new int[] {
                                 16,
                                 0,
                                 0,
                                 0});
   this.frameHeight.Location = new System.Drawing.Point(197, 144);
   this.frameHeight.Maximum = new System.Decimal(new int[] {
                                1000000,
                                0,
                                0,
                                0});
   this.frameHeight.Minimum = new System.Decimal(new int[] {
                                1,
                                0,
                                0,
                                0});
   this.frameHeight.Name = "frameHeight";
   this.frameHeight.Size = new System.Drawing.Size(56, 20);
   this.frameHeight.TabIndex = 9;
   this.frameHeight.Value = new System.Decimal(new int[] {
                               480,
                               0,
                               0,
                               0});
   this.label5.Location = new System.Drawing.Point(175, 148);
   this.label5.Name = "label5";
   this.label5.Size = new System.Drawing.Size(16, 16);
   this.label5.TabIndex = 8;
   this.label5.Text = "X";
   this.label6.Location = new System.Drawing.Point(2, 147);
   this.label6.Name = "label6";
   this.label6.Size = new System.Drawing.Size(104, 16);
   this.label6.TabIndex = 6;
   this.label6.Text = "Frame dimensions:";
   this.label6.TextAlign = System.Drawing.ContentAlignment.TopRight;
   this.openFileDialog.Filter = "Script files|*.sc";
   this.openFileDialog.RestoreDirectory = true;
   this.label1.Location = new System.Drawing.Point(8, 105);
   this.label1.Name = "label1";
   this.label1.Size = new System.Drawing.Size(72, 16);
   this.label1.TabIndex = 13;
   this.label1.Text = "Frame range:";
   this.label1.TextAlign = System.Drawing.ContentAlignment.TopRight;
   this.label7.Location = new System.Drawing.Point(144, 106);
   this.label7.Name = "label7";
   this.label7.Size = new System.Drawing.Size(16, 16);
   this.label7.TabIndex = 15;
   this.label7.Text = "-";
   this.frameEnd.Increment = new System.Decimal(new int[] {
                                16,
                                0,
                                0,
                                0});
   this.frameEnd.Location = new System.Drawing.Point(161, 105);
   this.frameEnd.Maximum = new System.Decimal(new int[] {
                               1000000,
                               0,
                               0,
                               0});
   this.frameEnd.Minimum = new System.Decimal(new int[] {
                               1,
                               0,
                               0,
                               0});
   this.frameEnd.Name = "frameEnd";
   this.frameEnd.Size = new System.Drawing.Size(56, 20);
   this.frameEnd.TabIndex = 16;
   this.frameEnd.Value = new System.Decimal(new int[] {
                              9999,
                              0,
                              0,
                              0});
   this.frameStart.Increment = new System.Decimal(new int[] {
                                 16,
                                 0,
                                 0,
                                 0});
   this.frameStart.Location = new System.Drawing.Point(85, 105);
   this.frameStart.Maximum = new System.Decimal(new int[] {
                                1000000,
                                0,
                                0,
                                0});
   this.frameStart.Minimum = new System.Decimal(new int[] {
                                1,
                                0,
                                0,
                                0});
   this.frameStart.Name = "frameStart";
   this.frameStart.Size = new System.Drawing.Size(56, 20);
   this.frameStart.TabIndex = 14;
   this.frameStart.Value = new System.Decimal(new int[] {
                               1,
                               0,
                               0,
                               0});
   this.label8.Location = new System.Drawing.Point(16, 175);
   this.label8.Name = "label8";
   this.label8.Size = new System.Drawing.Size(80, 16);
   this.label8.TabIndex = 17;
   this.label8.Text = "Output files:";
   this.outputFiles.Location = new System.Drawing.Point(16, 191);
   this.outputFiles.Name = "outputFiles";
   this.outputFiles.Size = new System.Drawing.Size(240, 20);
   this.outputFiles.TabIndex = 18;
   this.outputFiles.Text = "movie_{0:0000}.png";
   this.buttonEdit.Location = new System.Drawing.Point(237, 33);
   this.buttonEdit.Name = "buttonEdit";
   this.buttonEdit.Size = new System.Drawing.Size(41, 20);
   this.buttonEdit.TabIndex = 19;
   this.buttonEdit.Text = "&Edit";
   this.buttonEdit.Click += new System.EventHandler(this.buttonEdit_Click);
   this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
   this.ClientSize = new System.Drawing.Size(288, 264);
   this.Controls.Add(this.buttonEdit);
   this.Controls.Add(this.label8);
   this.Controls.Add(this.outputFiles);
   this.Controls.Add(this.scriptFile);
   this.Controls.Add(this.label1);
   this.Controls.Add(this.label7);
   this.Controls.Add(this.frameEnd);
   this.Controls.Add(this.frameStart);
   this.Controls.Add(this.label6);
   this.Controls.Add(this.label5);
   this.Controls.Add(this.frameHeight);
   this.Controls.Add(this.frameWidth);
   this.Controls.Add(this.label4);
   this.Controls.Add(this.buttonCancel);
   this.Controls.Add(this.buttonPlay);
   this.Controls.Add(this.label3);
   this.Controls.Add(this.label2);
   this.Controls.Add(this.frameWaitTime);
   this.Controls.Add(this.buttonBrowse);
   this.Controls.Add(this.buttonRecord);
   this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
   this.KeyPreview = true;
   this.Name = "MovieRecorderDialog";
   this.Text = "Movie Recorder";
   this.Activated += new System.EventHandler(this.MovieRecorderDialog_Activated);
   ((System.ComponentModel.ISupportInitialize)(this.frameWaitTime)).EndInit();
   ((System.ComponentModel.ISupportInitialize)(this.frameWidth)).EndInit();
   ((System.ComponentModel.ISupportInitialize)(this.frameHeight)).EndInit();
   ((System.ComponentModel.ISupportInitialize)(this.frameEnd)).EndInit();
   ((System.ComponentModel.ISupportInitialize)(this.frameStart)).EndInit();
   this.ResumeLayout(false);
  }
  void InstallPathCamera()
  {
   camera = new PathCamera(worldWindow);
   string scriptPath = scriptFile.Text;
   if(!Path.IsPathRooted(scriptPath))
    scriptPath = Path.Combine(plugin.PluginDirectory, scriptPath);
   camera.LoadScript( scriptPath );
   camera.PreRollTime = TimeSpan.FromMilliseconds((int)frameWaitTime.Value);
   camera.StartFrame = (int)frameStart.Value;
   camera.EndFrame = (int)frameEnd.Value;
   worldWindow.DrawArgs.WorldCamera = camera;
   if(worldWind.WindowState!=FormWindowState.Normal)
    worldWind.WindowState=FormWindowState.Normal;
   worldWind.ClientSize = new Size((int)frameWidth.Value,(int)frameHeight.Value);
  }
  private void buttonPlay_Click(object sender, System.EventArgs e)
  {
   InstallPathCamera();
  }
  private void buttonRecord_Click(object sender, System.EventArgs e)
  {
   InstallPathCamera();
   camera.IsRecording = true;
   camera.OutputFilePattern = outputFiles.Text;
  }
  private void buttonCancel_Click(object sender, System.EventArgs e)
  {
   if(worldWindow.DrawArgs.WorldCamera != camera)
    return;
   camera.InstallDefaultCamera();
   camera = null;
  }
  private void buttonBrowse_Click(object sender, System.EventArgs e)
  {
   openFileDialog.FileName = scriptFile.Text;
   if(openFileDialog.ShowDialog()==DialogResult.OK)
    scriptFile.Text = openFileDialog.FileName;
  }
  private void MovieRecorderDialog_Activated(object sender, System.EventArgs e)
  {
   frameWidth.Value = worldWindow.DrawArgs.screenWidth;
   frameHeight.Value = worldWindow.DrawArgs.screenHeight;
  }
  private void buttonEdit_Click(object sender, System.EventArgs e)
  {
   System.Diagnostics.Process.Start("notepad.exe", scriptFile.Text);
  }
 }
 public class MovieRecorder : Plugin
 {
  System.Windows.Forms.MenuItem menuItemRecorder;
  MovieRecorderDialog dialog;
  public override void Load()
  {
   menuItemRecorder = new System.Windows.Forms.MenuItem();
   menuItemRecorder.Text = "Movie &Recorder";
   menuItemRecorder.Click += new System.EventHandler(menuItemRecorder_Click);
   Application.PluginsMenu.MenuItems.Add(menuItemRecorder);
  }
  public override void Unload()
  {
   Application.PluginsMenu.MenuItems.Remove(menuItemRecorder);
   CameraBase camera = Application.WorldWindow.DrawArgs.WorldCamera;
   if(camera is PathCamera)
    ((PathCamera)camera).InstallDefaultCamera();
  }
  public void menuItemRecorder_Click(object sender, EventArgs e)
  {
   if(dialog!=null && !dialog.IsDisposed)
    return;
   dialog = new MovieRecorderDialog(this);
   dialog.Show();
  }
 }
 public class PathCamera : CameraBase
 {
  public bool IsRecording;
  public TimeSpan PreRollTime;
  public int StartFrame;
  public int EndFrame = int.MaxValue;
  public string OutputFilePattern = @"movie_{0:0000}.png";
  WorldWind.WorldWindow worldWindow;
  ArrayList keyFrames = new ArrayList();
  KeyFrame q0, q1, q2, q3;
  int currentFrameNumber=int.MinValue;
  int currentKeyNumber;
  DateTime frameStart = DateTime.MinValue;
  bool isPreRollComplete;
  bool isFrameRecorded = true;
  public PathCamera(WorldWind.WorldWindow worldWindow)
   : base(worldWindow.CurrentWorld.Position, worldWindow.CurrentWorld.EquatorialRadius)
  {
   this.worldWindow = worldWindow;
  }
  public override void Update(Device device)
  {
   if(currentFrameNumber==int.MinValue)
    currentFrameNumber = StartFrame-1;
   try
   {
    isPreRollComplete = DateTime.Now.Subtract(frameStart)>PreRollTime;
    if(isPreRollComplete && isFrameRecorded)
    {
     isPreRollComplete = false;
     isFrameRecorded = false;
     frameStart = DateTime.Now;
     currentFrameNumber++;
     if(currentFrameNumber > EndFrame)
     {
      q1 = null;
      currentKeyNumber = keyFrames.Count;
     }
     while(q1==null || currentFrameNumber > q2.FrameNumber)
     {
      if(currentKeyNumber+3 < keyFrames.Count)
      {
       q0 = (KeyFrame)keyFrames[currentKeyNumber];
       q1 = (KeyFrame)keyFrames[currentKeyNumber+1];
       q2 = (KeyFrame)keyFrames[currentKeyNumber+2];
       q3 = (KeyFrame)keyFrames[currentKeyNumber+3];
       currentKeyNumber++;
      }
      else
      {
       InstallDefaultCamera();
       return;
      }
     }
     float t = (float)(currentFrameNumber - q1.FrameNumber) / (q2.FrameNumber - q1.FrameNumber);
     Altitude = InterpolateLog( t, q1.Altitude, q2.Altitude );
     Quaternion a = Quaternion.Zero;
     Quaternion b = Quaternion.Zero;
     Quaternion c = Quaternion.Zero;
     Quaternion orientation = new Quaternion(
      (float)m_Orientation.X,
      (float)m_Orientation.Y,
      (float)m_Orientation.Z,
      (float)m_Orientation.W);
     Quaternion.SquadSetup(ref a, ref b, ref c,
      q0.Orientation, q1.Orientation, q2.Orientation, q3.Orientation);
     orientation = Quaternion.Squad(q1.Orientation,a,b,c,t);
     Quaternion.SquadSetup(ref a, ref b, ref c,
      q0.CameraOrientation, q1.CameraOrientation, q2.CameraOrientation, q3.CameraOrientation);
     Vector3 cr = MathEngine.QuaternionToEuler( Quaternion.Squad(q1.CameraOrientation,a,b,c,t) );
     m_Orientation = new Quaternion4d(orientation.X, orientation.Y, orientation.Z, orientation.W);
     _tilt.Radians = cr.X;
     _bank.Radians = cr.Y;
    }
    base.Update(device);
    if(isPreRollComplete)
    {
     if(IsRecording)
     {
      string frameName = string.Format(OutputFilePattern, currentFrameNumber);
      worldWindow.SaveScreenshot(frameName);
     }
     isFrameRecorded = true;
    }
   }
   catch(Exception caught)
   {
    Utility.Log.Write(caught);
    q1 = null;
    currentKeyNumber = keyFrames.Count;
   }
  }
  public void LoadScript( string scriptFile )
  {
   using(TextReader tr = File.OpenText(scriptFile))
   {
    int lineNumber = 0;
    while(true)
    {
     string line = tr.ReadLine();
     if(line==null)
      break;
     lineNumber++;
     if(line.Trim().Length <= 0)
      continue;
     try
     {
      keyFrames.Add(KeyFrame.FromString(line));
     }
     catch(Exception caught)
     {
      string msg = string.Format(
       "Error in {0}, line {1}: {2}",
       scriptFile, lineNumber, caught.Message);
      throw new ApplicationException(msg);
     }
    }
    if(keyFrames.Count <=0)
     throw new ArgumentException("No key frames found in movie script.");
    keyFrames.Insert(0, keyFrames[0]);
    keyFrames.Add(keyFrames[keyFrames.Count-1]);
   }
  }
  float InterpolateLog( float t, double start, double end )
  {
   double logStart = Math.Log10(start);
   double logEnd = Math.Log10(end);
   double res = Math.Pow(10,t * (logEnd-logStart) + logStart);
   return (float)res;
  }
  public void InstallDefaultCamera()
  {
   worldWindow.DrawArgs.WorldCamera = new MomentumCamera(worldWindow.CurrentWorld.Position, worldWindow.CurrentWorld.EquatorialRadius);
   worldWindow.DrawArgs.WorldCamera.Update( worldWindow.DrawArgs.device );
  }
 }
 public class KeyFrame
 {
  public double Altitude;
  public Angle Latitude;
  public Angle Longitude;
  public Angle Bank;
  public Angle Direction;
  public Angle Tilt;
  public int FrameNumber;
  public static char FieldSeparator = ';';
  public static KeyFrame FromString(string scriptLine)
  {
   string[] fields = scriptLine.Split(new char[]{FieldSeparator},2);
   WorldWindUri wu = WorldWindUri.Parse(fields[1]);
   KeyFrame kf = new KeyFrame();
   kf.Altitude = wu.Altitude;
   if(double.IsNaN(kf.Altitude)) kf.Altitude = 10000;
   kf.Bank = wu.Bank;
   if(Angle.IsNaN(kf.Bank)) kf.Bank = Angle.Zero;
   kf.Direction = wu.Direction;
   if(Angle.IsNaN(kf.Direction)) kf.Direction = Angle.Zero;
   kf.Latitude = wu.Latitude;
   if(Angle.IsNaN(kf.Latitude)) kf.Latitude = Angle.Zero;
   kf.Longitude = wu.Longitude;
   if(Angle.IsNaN(kf.Longitude)) kf.Longitude = Angle.Zero;
   kf.Tilt = wu.Tilt;
   if(Angle.IsNaN(kf.Tilt)) kf.Tilt = Angle.Zero;
   kf.FrameNumber = int.Parse(fields[0]);
   return kf;
  }
  public Quaternion Orientation
  {
   get
   {
    return MathEngine.EulerToQuaternion(Longitude.Radians, Latitude.Radians, Direction.Radians);
   }
  }
  public Quaternion CameraOrientation
  {
   get
   {
    return MathEngine.EulerToQuaternion(Tilt.Radians, Bank.Radians, 0);
   }
  }
 }
}
