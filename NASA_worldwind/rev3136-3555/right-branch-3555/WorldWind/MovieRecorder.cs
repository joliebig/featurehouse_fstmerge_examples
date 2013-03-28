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
using Utility;
using AviFile;
namespace Apogee.MovieCreator
{
    public class MovieRecorderDialog : System.Windows.Forms.Form
    {
        private AviManager aviManager;
        private VideoStream aviStream;
        private DirectoryInfo pngDirInfo;
        public bool doMovie = false;
        private bool fileChosen = false;
        public string movieFileName;
        public string imageFileNames = "_{0:0000}.png";
        private System.Windows.Forms.Button buttonBrowseScript;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Button buttonPlay;
        private System.Windows.Forms.Button buttonRecord;
        private System.Windows.Forms.Button buttonStop;
        private System.ComponentModel.Container components = null;
        PathCamera camera;
        private System.Windows.Forms.TextBox scriptFileTextBox;
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
        private System.Windows.Forms.Label outputPngLabel;
        private System.Windows.Forms.TextBox outputPngTextBox;
        private System.Windows.Forms.Button buttonEdit;
        private Button buttonBrowsePngOutput;
        private Button buttonConvert;
        private Button buttonBrowseMovieOutput;
        private Label outputMovieLabel;
        private TextBox outputMovieTextBox;
        private Label frameRateLabel;
        private TextBox frameRateTextBox;
        private Label perSecLabel;
        private System.Windows.Forms.NumericUpDown frameHeight;
        public MovieRecorderDialog(Plugin plugin)
        {
            InitializeComponent();
            this.plugin = plugin;
            this.worldWind = plugin.Application;
            this.worldWindow = worldWind.WorldWindow;
        }
        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                if (components != null)
                {
                    components.Dispose();
                }
            }
            base.Dispose(disposing);
        }
        private void InitializeComponent()
        {
            this.scriptFileTextBox = new System.Windows.Forms.TextBox();
            this.buttonBrowseScript = new System.Windows.Forms.Button();
            this.frameWaitTime = new System.Windows.Forms.NumericUpDown();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.buttonRecord = new System.Windows.Forms.Button();
            this.buttonPlay = new System.Windows.Forms.Button();
            this.buttonStop = new System.Windows.Forms.Button();
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
            this.outputPngLabel = new System.Windows.Forms.Label();
            this.outputPngTextBox = new System.Windows.Forms.TextBox();
            this.buttonEdit = new System.Windows.Forms.Button();
            this.buttonBrowsePngOutput = new System.Windows.Forms.Button();
            this.buttonConvert = new System.Windows.Forms.Button();
            this.buttonBrowseMovieOutput = new System.Windows.Forms.Button();
            this.outputMovieLabel = new System.Windows.Forms.Label();
            this.outputMovieTextBox = new System.Windows.Forms.TextBox();
            this.frameRateLabel = new System.Windows.Forms.Label();
            this.frameRateTextBox = new System.Windows.Forms.TextBox();
            this.perSecLabel = new System.Windows.Forms.Label();
            ((System.ComponentModel.ISupportInitialize)(this.frameWaitTime)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.frameWidth)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.frameHeight)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.frameEnd)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.frameStart)).BeginInit();
            this.SuspendLayout();
            this.scriptFileTextBox.Enabled = false;
            this.scriptFileTextBox.Location = new System.Drawing.Point(12, 34);
            this.scriptFileTextBox.Name = "scriptFileTextBox";
            this.scriptFileTextBox.Size = new System.Drawing.Size(185, 20);
            this.scriptFileTextBox.TabIndex = 1;
            this.scriptFileTextBox.Text = "MovieScript.sc";
            this.buttonBrowseScript.Location = new System.Drawing.Point(203, 34);
            this.buttonBrowseScript.Name = "buttonBrowseScript";
            this.buttonBrowseScript.Size = new System.Drawing.Size(75, 21);
            this.buttonBrowseScript.TabIndex = 2;
            this.buttonBrowseScript.Text = "&Browse";
            this.buttonBrowseScript.Click += new System.EventHandler(this.buttonBrowseScript_Click);
            this.frameWaitTime.Increment = new decimal(new int[] {
            100,
            0,
            0,
            0});
            this.frameWaitTime.Location = new System.Drawing.Point(88, 84);
            this.frameWaitTime.Maximum = new decimal(new int[] {
            1000000,
            0,
            0,
            0});
            this.frameWaitTime.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.frameWaitTime.Name = "frameWaitTime";
            this.frameWaitTime.Size = new System.Drawing.Size(64, 20);
            this.frameWaitTime.TabIndex = 4;
            this.frameWaitTime.Value = new decimal(new int[] {
            100,
            0,
            0,
            0});
            this.label2.Location = new System.Drawing.Point(12, 86);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(70, 16);
            this.label2.TabIndex = 3;
            this.label2.Text = "Pre-roll time:";
            this.label3.Location = new System.Drawing.Point(155, 87);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(32, 16);
            this.label3.TabIndex = 5;
            this.label3.Text = "ms";
            this.buttonRecord.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.buttonRecord.Enabled = false;
            this.buttonRecord.Location = new System.Drawing.Point(109, 225);
            this.buttonRecord.Name = "buttonRecord";
            this.buttonRecord.Size = new System.Drawing.Size(75, 23);
            this.buttonRecord.TabIndex = 11;
            this.buttonRecord.Text = "&Record";
            this.buttonRecord.Click += new System.EventHandler(this.buttonRecord_Click);
            this.buttonPlay.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.buttonPlay.Enabled = false;
            this.buttonPlay.Location = new System.Drawing.Point(12, 225);
            this.buttonPlay.Name = "buttonPlay";
            this.buttonPlay.Size = new System.Drawing.Size(75, 23);
            this.buttonPlay.TabIndex = 10;
            this.buttonPlay.Text = "&Play";
            this.buttonPlay.Click += new System.EventHandler(this.buttonPlay_Click);
            this.buttonStop.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.buttonStop.Enabled = false;
            this.buttonStop.Location = new System.Drawing.Point(203, 225);
            this.buttonStop.Name = "buttonStop";
            this.buttonStop.Size = new System.Drawing.Size(75, 23);
            this.buttonStop.TabIndex = 12;
            this.buttonStop.Text = "&Stop";
            this.buttonStop.Click += new System.EventHandler(this.buttonStop_Click);
            this.label4.Location = new System.Drawing.Point(12, 15);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(64, 16);
            this.label4.TabIndex = 0;
            this.label4.Text = "Script file:";
            this.frameWidth.Increment = new decimal(new int[] {
            16,
            0,
            0,
            0});
            this.frameWidth.Location = new System.Drawing.Point(117, 153);
            this.frameWidth.Maximum = new decimal(new int[] {
            1000000,
            0,
            0,
            0});
            this.frameWidth.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.frameWidth.Name = "frameWidth";
            this.frameWidth.Size = new System.Drawing.Size(56, 20);
            this.frameWidth.TabIndex = 7;
            this.frameWidth.Value = new decimal(new int[] {
            640,
            0,
            0,
            0});
            this.frameHeight.Increment = new decimal(new int[] {
            16,
            0,
            0,
            0});
            this.frameHeight.Location = new System.Drawing.Point(201, 153);
            this.frameHeight.Maximum = new decimal(new int[] {
            1000000,
            0,
            0,
            0});
            this.frameHeight.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.frameHeight.Name = "frameHeight";
            this.frameHeight.Size = new System.Drawing.Size(56, 20);
            this.frameHeight.TabIndex = 9;
            this.frameHeight.Value = new decimal(new int[] {
            480,
            0,
            0,
            0});
            this.label5.Location = new System.Drawing.Point(179, 155);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(16, 16);
            this.label5.TabIndex = 8;
            this.label5.Text = "X";
            this.label6.Location = new System.Drawing.Point(12, 153);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(108, 18);
            this.label6.TabIndex = 6;
            this.label6.Text = "Frame dimensions:";
            this.label6.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.openFileDialog.Filter = "Script files|*.sc";
            this.openFileDialog.RestoreDirectory = true;
            this.label1.Location = new System.Drawing.Point(12, 118);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(72, 16);
            this.label1.TabIndex = 13;
            this.label1.Text = "Frame range:";
            this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.label7.Location = new System.Drawing.Point(152, 118);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(16, 16);
            this.label7.TabIndex = 15;
            this.label7.Text = "-";
            this.frameEnd.Increment = new decimal(new int[] {
            16,
            0,
            0,
            0});
            this.frameEnd.Location = new System.Drawing.Point(174, 118);
            this.frameEnd.Maximum = new decimal(new int[] {
            1000000,
            0,
            0,
            0});
            this.frameEnd.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.frameEnd.Name = "frameEnd";
            this.frameEnd.Size = new System.Drawing.Size(56, 20);
            this.frameEnd.TabIndex = 16;
            this.frameEnd.Value = new decimal(new int[] {
            9999,
            0,
            0,
            0});
            this.frameStart.Increment = new decimal(new int[] {
            16,
            0,
            0,
            0});
            this.frameStart.Location = new System.Drawing.Point(90, 118);
            this.frameStart.Maximum = new decimal(new int[] {
            1000000,
            0,
            0,
            0});
            this.frameStart.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.frameStart.Name = "frameStart";
            this.frameStart.Size = new System.Drawing.Size(56, 20);
            this.frameStart.TabIndex = 14;
            this.frameStart.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.outputPngLabel.Location = new System.Drawing.Point(12, 180);
            this.outputPngLabel.Name = "outputPngLabel";
            this.outputPngLabel.Size = new System.Drawing.Size(95, 16);
            this.outputPngLabel.TabIndex = 17;
            this.outputPngLabel.Text = "Output PNG files:";
            this.outputPngTextBox.Location = new System.Drawing.Point(12, 199);
            this.outputPngTextBox.Name = "outputPngTextBox";
            this.outputPngTextBox.Size = new System.Drawing.Size(185, 20);
            this.outputPngTextBox.TabIndex = 18;
            this.outputPngTextBox.Text = "movie_{0:0000}.png";
            this.buttonEdit.Enabled = false;
            this.buttonEdit.Location = new System.Drawing.Point(203, 61);
            this.buttonEdit.Name = "buttonEdit";
            this.buttonEdit.Size = new System.Drawing.Size(75, 21);
            this.buttonEdit.TabIndex = 19;
            this.buttonEdit.Text = "&Edit";
            this.buttonEdit.Click += new System.EventHandler(this.buttonEdit_Click);
            this.buttonBrowsePngOutput.Location = new System.Drawing.Point(203, 198);
            this.buttonBrowsePngOutput.Name = "buttonBrowsePngOutput";
            this.buttonBrowsePngOutput.Size = new System.Drawing.Size(75, 21);
            this.buttonBrowsePngOutput.TabIndex = 23;
            this.buttonBrowsePngOutput.Text = "&Browse";
            this.buttonBrowsePngOutput.Click += new System.EventHandler(this.buttonBrowseOutput_Click);
            this.buttonConvert.Enabled = false;
            this.buttonConvert.Location = new System.Drawing.Point(15, 328);
            this.buttonConvert.Name = "buttonConvert";
            this.buttonConvert.Size = new System.Drawing.Size(262, 23);
            this.buttonConvert.TabIndex = 24;
            this.buttonConvert.Text = "Convert to movie";
            this.buttonConvert.UseVisualStyleBackColor = true;
            this.buttonConvert.Click += new System.EventHandler(this.buttonConvert_Click);
            this.buttonBrowseMovieOutput.Location = new System.Drawing.Point(203, 272);
            this.buttonBrowseMovieOutput.Name = "buttonBrowseMovieOutput";
            this.buttonBrowseMovieOutput.Size = new System.Drawing.Size(75, 21);
            this.buttonBrowseMovieOutput.TabIndex = 27;
            this.buttonBrowseMovieOutput.Text = "&Browse";
            this.buttonBrowseMovieOutput.Click += new System.EventHandler(this.buttonBrowseMovieOutput_Click);
            this.outputMovieLabel.Location = new System.Drawing.Point(12, 254);
            this.outputMovieLabel.Name = "outputMovieLabel";
            this.outputMovieLabel.Size = new System.Drawing.Size(108, 16);
            this.outputMovieLabel.TabIndex = 25;
            this.outputMovieLabel.Text = "Output movie file:";
            this.outputMovieTextBox.Enabled = false;
            this.outputMovieTextBox.Location = new System.Drawing.Point(12, 273);
            this.outputMovieTextBox.Name = "outputMovieTextBox";
            this.outputMovieTextBox.Size = new System.Drawing.Size(185, 20);
            this.outputMovieTextBox.TabIndex = 26;
            this.outputMovieTextBox.Text = "movie.avi";
            this.frameRateLabel.AutoSize = true;
            this.frameRateLabel.Location = new System.Drawing.Point(12, 305);
            this.frameRateLabel.Name = "frameRateLabel";
            this.frameRateLabel.Size = new System.Drawing.Size(65, 13);
            this.frameRateLabel.TabIndex = 28;
            this.frameRateLabel.Text = "Frame Rate:";
            this.frameRateTextBox.Location = new System.Drawing.Point(83, 302);
            this.frameRateTextBox.Name = "frameRateTextBox";
            this.frameRateTextBox.Size = new System.Drawing.Size(24, 20);
            this.frameRateTextBox.TabIndex = 29;
            this.frameRateTextBox.Text = "2";
            this.perSecLabel.AutoSize = true;
            this.perSecLabel.Location = new System.Drawing.Point(113, 305);
            this.perSecLabel.Name = "perSecLabel";
            this.perSecLabel.Size = new System.Drawing.Size(94, 13);
            this.perSecLabel.TabIndex = 30;
            this.perSecLabel.Text = "frames per second";
            this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
            this.ClientSize = new System.Drawing.Size(289, 358);
            this.Controls.Add(this.perSecLabel);
            this.Controls.Add(this.frameRateTextBox);
            this.Controls.Add(this.frameRateLabel);
            this.Controls.Add(this.buttonBrowseMovieOutput);
            this.Controls.Add(this.outputMovieLabel);
            this.Controls.Add(this.outputMovieTextBox);
            this.Controls.Add(this.buttonConvert);
            this.Controls.Add(this.frameWidth);
            this.Controls.Add(this.buttonBrowsePngOutput);
            this.Controls.Add(this.buttonEdit);
            this.Controls.Add(this.outputPngLabel);
            this.Controls.Add(this.outputPngTextBox);
            this.Controls.Add(this.scriptFileTextBox);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.label7);
            this.Controls.Add(this.frameEnd);
            this.Controls.Add(this.frameStart);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.frameHeight);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.buttonStop);
            this.Controls.Add(this.buttonPlay);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.frameWaitTime);
            this.Controls.Add(this.buttonBrowseScript);
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
            this.PerformLayout();
        }
        void InstallPathCamera()
        {
            camera = new PathCamera(worldWindow, this);
            string scriptPath = scriptFileTextBox.Text;
            if (!Path.IsPathRooted(scriptPath))
                scriptPath = Path.Combine(plugin.PluginDirectory, scriptPath);
            camera.LoadScript(scriptPath);
            camera.PreRollTime = TimeSpan.FromMilliseconds((int)frameWaitTime.Value);
            camera.StartFrame = (int)frameStart.Value;
            camera.EndFrame = (int)frameEnd.Value;
            worldWindow.DrawArgs.WorldCamera = camera;
            if (worldWind.WindowState != FormWindowState.Normal)
                worldWind.WindowState = FormWindowState.Normal;
            worldWind.ClientSize = new Size((int)frameWidth.Value, (int)frameHeight.Value);
        }
        private void buttonPlay_Click(object sender, System.EventArgs e)
        {
            this.doMovie = false;
            InstallPathCamera();
            enableControlButtons(false);
        }
        private void buttonRecord_Click(object sender, System.EventArgs e)
        {
            InstallPathCamera();
            camera.IsRecording = true;
            if (!doMovie)
                camera.OutputFilePattern = outputPngTextBox.Text;
            else
                camera.OutputFilePattern = outputPngTextBox.Text.Split(new char[] { '.' })[0] + imageFileNames;
            enableControlButtons(false);
        }
        private void buttonStop_Click(object sender, System.EventArgs e)
        {
            enableControlButtons(true);
            if (worldWindow.DrawArgs.WorldCamera != camera)
                return;
            camera.InstallDefaultCamera();
            camera = null;
        }
        private void buttonBrowseScript_Click(object sender, System.EventArgs e)
        {
            openFileDialog.FileName = scriptFileTextBox.Text;
            if (openFileDialog.ShowDialog() == DialogResult.OK)
                scriptFileTextBox.Text = openFileDialog.FileName;
            buttonPlay.Enabled = true;
            buttonEdit.Enabled = true;
        }
        private void MovieRecorderDialog_Activated(object sender, System.EventArgs e)
        {
            frameWidth.Value = worldWindow.DrawArgs.screenWidth;
            frameHeight.Value = worldWindow.DrawArgs.screenHeight;
        }
        private void buttonEdit_Click(object sender, System.EventArgs e)
        {
            System.Diagnostics.Process.Start("notepad.exe", scriptFileTextBox.Text);
        }
        private void pngRadioButton_CheckedChanged(object sender, EventArgs e)
        {
            outputPngTextBox.Text = "movie_{0:0000}.png";
            this.doMovie = false;
        }
        private void movieRadioButton_CheckedChanged(object sender, EventArgs e)
        {
            outputPngTextBox.Text = "movie.avi";
            this.doMovie = true;
        }
        private void enableControlButtons(bool b)
        {
            buttonStop.Enabled = !b;
            buttonPlay.Enabled = b;
            if (this.fileChosen)
                buttonRecord.Enabled = b;
        }
        private void buttonBrowseOutput_Click(object sender, EventArgs e)
        {
            String location = "";
            string pngFileName = outputPngTextBox.Text;
            FolderBrowserDialog chooser = new FolderBrowserDialog();
            chooser.Description = "Save Files in Directory";
            if (chooser.ShowDialog(MainApplication.ActiveForm) == DialogResult.OK)
                fileChosen = true;
            location = chooser.SelectedPath + "\\movie" + imageFileNames;
            if (fileChosen)
            {
                buttonEdit.Enabled = true;
                buttonPlay.Enabled = true;
                buttonRecord.Enabled = true;
                outputPngTextBox.Text = location;
                pngDirInfo = new DirectoryInfo(chooser.SelectedPath);
            }
        }
        private void buttonBrowseMovieOutput_Click(object sender, EventArgs e)
        {
            SaveFileDialog chooser = new SaveFileDialog();
            chooser.Filter = "AVI files (*.avi)|*.avi|All files (*.*)|*.*";
            chooser.FilterIndex = 1;
            chooser.RestoreDirectory = true;
            chooser.Title = "Save Movie";
            bool chosen = false;
            if (chooser.ShowDialog(MainApplication.ActiveForm) == DialogResult.OK)
            {
                chosen = true;
                buttonConvert.Enabled = true;
            }
            if (chosen)
            {
                outputMovieTextBox.Text = chooser.FileName;
                outputMovieTextBox.Enabled = true;
            }
        }
        private void buttonConvert_Click(object sender, EventArgs e)
        {
            if (pngDirInfo == null)
            {
                FileInfo f = new FileInfo(outputMovieTextBox.Text);
                pngDirInfo = f.Directory;
            }
            enableControlButtons(false);
            buttonConvert.Enabled = false;
            aviManager = new AviManager(outputMovieTextBox.Text, false);
            FileInfo[] fi = pngDirInfo.GetFiles();
            bool firstFrame = true;
            foreach (FileInfo f in fi)
            {
                if (f.Extension == ".png")
                {
                    Bitmap bitmap = (Bitmap)Image.FromFile(Path.Combine(f.DirectoryName,f.Name));
                    int frameRate = Convert.ToInt16(frameRateTextBox.Text);
                    if (firstFrame == true)
                        aviStream = aviManager.AddVideoStream(true, frameRate, bitmap);
                    else
                        aviStream.AddFrame(bitmap);
                    bitmap.Dispose();
                    firstFrame = false;
                }
            }
            aviManager.Close();
            enableControlButtons(true);
            buttonConvert.Enabled = true;
        }
        public void Stop()
        {
            buttonStop_Click(null,null);
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
            if (camera is PathCamera)
                ((PathCamera)camera).InstallDefaultCamera();
        }
        public void menuItemRecorder_Click(object sender, EventArgs e)
        {
            if (dialog != null && !dialog.IsDisposed)
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
        MovieRecorderDialog dialog;
        WorldWind.WorldWindow worldWindow;
        ArrayList keyFrames = new ArrayList();
        KeyFrame q0, q1, q2, q3;
        int currentFrameNumber = int.MinValue;
        int currentKeyNumber;
        DateTime frameStart = DateTime.MinValue;
        bool isPreRollComplete;
        bool isFrameRecorded = true;
        public PathCamera(WorldWind.WorldWindow worldWindow, MovieRecorderDialog dialog)
            : base(worldWindow.CurrentWorld.Position, worldWindow.CurrentWorld.EquatorialRadius)
        {
            this.worldWindow = worldWindow;
            this.dialog = dialog;
        }
        public override void Update(Device device)
        {
            if (currentFrameNumber == int.MinValue)
                currentFrameNumber = StartFrame - 1;
            try
            {
                isPreRollComplete = DateTime.Now.Subtract(frameStart) > PreRollTime;
                if (isPreRollComplete && isFrameRecorded)
                {
                    isPreRollComplete = false;
                    isFrameRecorded = false;
                    frameStart = DateTime.Now;
                    currentFrameNumber++;
                    if (currentFrameNumber > EndFrame)
                    {
                        q1 = null;
                        currentKeyNumber = keyFrames.Count;
                    }
                    while (q1 == null || currentFrameNumber > q2.FrameNumber)
                    {
                        if (currentKeyNumber + 3 < keyFrames.Count)
                        {
                            q0 = (KeyFrame)keyFrames[currentKeyNumber];
                            q1 = (KeyFrame)keyFrames[currentKeyNumber + 1];
                            q2 = (KeyFrame)keyFrames[currentKeyNumber + 2];
                            q3 = (KeyFrame)keyFrames[currentKeyNumber + 3];
                            currentKeyNumber++;
                        }
                        else
                        {
                            InstallDefaultCamera();
                            return;
                        }
                    }
                    float t = (float)(currentFrameNumber - q1.FrameNumber) / (q2.FrameNumber - q1.FrameNumber);
                    Altitude = InterpolateLog(t, q1.Altitude, q2.Altitude);
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
                    orientation = Quaternion.Squad(q1.Orientation, a, b, c, t);
                    Quaternion.SquadSetup(ref a, ref b, ref c,
                        q0.CameraOrientation, q1.CameraOrientation, q2.CameraOrientation, q3.CameraOrientation);
                    Vector3 cr = MathEngine.QuaternionToEuler(Quaternion.Squad(q1.CameraOrientation, a, b, c, t));
                    m_Orientation = new Quaternion4d(orientation.X, orientation.Y, orientation.Z, orientation.W);
                    _tilt.Radians = cr.X;
                    _bank.Radians = cr.Y;
                }
                base.Update(device);
                if (isPreRollComplete)
                {
                    if (IsRecording)
                    {
                        string frameName = string.Format(OutputFilePattern, currentFrameNumber);
                        worldWindow.SaveScreenshot(frameName);
                    }
                    isFrameRecorded = true;
                }
            }
            catch (Exception caught)
            {
                Log.Write(caught);
                q1 = null;
                currentKeyNumber = keyFrames.Count;
            }
        }
        public void LoadScript(string scriptFile)
        {
            using (TextReader tr = File.OpenText(scriptFile))
            {
                int lineNumber = 0;
                while (true)
                {
                    string line = tr.ReadLine();
                    if (line == null)
                        break;
                    lineNumber++;
                    if (line.Trim().Length <= 0)
                        continue;
                    try
                    {
                        keyFrames.Add(KeyFrame.FromString(line));
                    }
                    catch (Exception caught)
                    {
                        string msg = string.Format(
                            "Error in {0}, line {1}: {2}",
                            scriptFile, lineNumber, caught.Message);
                        throw new ApplicationException(msg);
                    }
                }
                if (keyFrames.Count <= 0)
                    throw new ArgumentException("No key frames found in movie script.");
                keyFrames.Insert(0, keyFrames[0]);
                keyFrames.Add(keyFrames[keyFrames.Count - 1]);
            }
        }
        float InterpolateLog(float t, double start, double end)
        {
            double logStart = Math.Log10(start);
            double logEnd = Math.Log10(end);
            double res = Math.Pow(10, t * (logEnd - logStart) + logStart);
            return (float)res;
        }
        public void InstallDefaultCamera()
        {
            worldWindow.DrawArgs.WorldCamera = new MomentumCamera(worldWindow.CurrentWorld.Position, worldWindow.CurrentWorld.EquatorialRadius);
            worldWindow.DrawArgs.WorldCamera.Update(worldWindow.DrawArgs.device);
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
            string[] fields = scriptLine.Split(new char[] { FieldSeparator }, 2);
            WorldWindUri wu = WorldWindUri.Parse(fields[1]);
            KeyFrame kf = new KeyFrame();
            kf.Altitude = wu.Altitude;
            if (double.IsNaN(kf.Altitude)) kf.Altitude = 10000;
            kf.Bank = wu.Bank;
            if (Angle.IsNaN(kf.Bank)) kf.Bank = Angle.Zero;
            kf.Direction = wu.Direction;
            if (Angle.IsNaN(kf.Direction)) kf.Direction = Angle.Zero;
            kf.Latitude = wu.Latitude;
            if (Angle.IsNaN(kf.Latitude)) kf.Latitude = Angle.Zero;
            kf.Longitude = wu.Longitude;
            if (Angle.IsNaN(kf.Longitude)) kf.Longitude = Angle.Zero;
            kf.Tilt = wu.Tilt;
            if (Angle.IsNaN(kf.Tilt)) kf.Tilt = Angle.Zero;
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
