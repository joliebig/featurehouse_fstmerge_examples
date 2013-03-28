using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System.Windows.Forms;
using WorldWind.Renderable;
using WorldWind.Camera;
using WorldWind.Net;
using WorldWind.VisualControl;
using WorldWind;
using System.IO;
using System.Drawing;
using System.Net;
using System.Collections;
using System;
namespace Murris.Plugins
{
 public class GlobalClouds : WorldWind.PluginEngine.Plugin
 {
        private WorldWind.WindowsControlMenuButton m_ToolbarItem;
        private Control control = new Control();
        private EventHandler evhand;
        private GlobalCloudsLayer layer;
  public static string LayerName = "GlobalClouds";
        public override void Load()
        {
            if (ParentApplication.WorldWindow.CurrentWorld != null && ParentApplication.WorldWindow.CurrentWorld.Name.IndexOf("Earth") >= 0)
            {
                control.Visible = true;
                evhand = new EventHandler(control_VisibleChanged);
                control.VisibleChanged += evhand;
                m_ToolbarItem = new WorldWind.WindowsControlMenuButton("Global Clouds", Path.Combine(Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath), @"Data\Icons\Interface\earth-eastern.png"), control);
                m_Application.WorldWindow.MenuBar.AddToolsMenuButton(m_ToolbarItem);
                layer = new GlobalCloudsLayer(LayerName, PluginDirectory, ParentApplication.WorldWindow);
    layer.IsOn = World.Settings.ShowClouds;
                ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(layer);
    m_ToolbarItem.SetPushed(World.Settings.ShowClouds);
            }
        }
  public override void Unload()
  {
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
 public class GlobalCloudsLayer : RenderableObject
 {
  static string version = "0.6";
  string settingsFileName = "GlobalClouds.ini";
  string serverListFileName = "GlobalCloudsServers.txt";
  string pluginPath;
  public World world;
  public DrawArgs drawArgs;
  Mesh layerMesh;
  Texture texture = null;
  Form pDialog;
  public string textureFileName = "";
  public string latestFileName = "";
  public DateTime latestTime = DateTime.MinValue;
  public int historyDays = 10;
  public int refreshHours = 3;
  public GlobalCloudsLayer(string LayerName, string pluginPath, WorldWindow worldWindow) : base(LayerName)
  {
   this.pluginPath = pluginPath;
   this.world = worldWindow.CurrentWorld;
   this.drawArgs = worldWindow.DrawArgs;
   this.RenderPriority = RenderPriority.AtmosphericImages;
   CleanupJpg();
   FindLatest();
   ReadSettings();
   if(textureFileName == "" && latestFileName != "") textureFileName = latestFileName;
  }
  public void FindLatest()
  {
   DirectoryInfo di = new DirectoryInfo(pluginPath);
   FileInfo[] imgFiles = di.GetFiles("clouds*.png");
   for(int i = 0; i < imgFiles.Length; i++)
   {
    if(imgFiles[i].LastWriteTime > latestTime)
    {
     latestFileName = imgFiles[i].Name;
     latestTime = imgFiles[i].LastWriteTime;
    }
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
    World.Settings.ShowClouds = value;
    base.IsOn = value;
   }
  }
  public void CleanupHistory()
  {
   DateTime oldest = DateTime.Now.AddDays(-historyDays);
   DirectoryInfo di = new DirectoryInfo(pluginPath);
   FileInfo[] imgFiles = di.GetFiles("clouds*.png");
   for(int i = 0; i < imgFiles.Length; i++)
   {
    if(imgFiles[i].LastWriteTime < oldest)
    {
     File.Delete(Path.Combine(pluginPath, imgFiles[i].Name));
    }
   }
  }
  public void CleanupJpg()
  {
   DirectoryInfo di = new DirectoryInfo(pluginPath);
   FileInfo[] imgFiles = di.GetFiles("clouds*.jpg");
   for(int i = 0; i < imgFiles.Length; i++)
   {
    File.Delete(Path.Combine(pluginPath, imgFiles[i].Name));
   }
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
    if(settingsList[1] != null) textureFileName = settingsList[1];
   }
  }
  public void SaveSettings()
  {
   string line = version + ";" + textureFileName;
   try
   {
    StreamWriter sw = new StreamWriter(Path.Combine(pluginPath, settingsFileName));
    sw.Write(line);
    sw.Close();
   }
   catch(Exception caught) {}
  }
  public override void Render(DrawArgs drawArgs)
  {
   if(!isInitialized) return;
   if(world.Name != "Earth") return;
   if(!isDownloading && DateTime.Now > latestTime.AddHours(refreshHours))
   {
    if(retryCount < maxRetry && DateTime.Now > lastDownloadTime.AddSeconds(retryDelaySeconds))
    {
     StartDownload(pluginPath, "clouds_" + DateTimeStamp(DateTime.Now) + ".jpg");
    }
   }
   CameraBase camera = drawArgs.WorldCamera;
   if(texture != null)
   {
    double cloudAlt = 20e3;
    if(camera.Altitude < 4000e3) return;
    double sphereRadius = camera.WorldRadius + cloudAlt;
    if(layerMesh == null)
     layerMesh = TexturedSphere(drawArgs.device, (float)sphereRadius, 64, 64);
    drawArgs.device.SetTexture(0,texture);
                drawArgs.device.TextureState[0].ColorOperation = TextureOperation.Modulate;
                drawArgs.device.TextureState[0].ColorArgument1 = TextureArgument.TextureColor;
                drawArgs.device.TextureState[0].ColorArgument2 = TextureArgument.Diffuse;
                drawArgs.device.TextureState[0].AlphaOperation = TextureOperation.SelectArg1;
                drawArgs.device.TextureState[0].AlphaArgument1 = TextureArgument.TextureColor;
    drawArgs.device.VertexFormat = CustomVertex.PositionNormalTextured.Format;
    Matrix origWorld = drawArgs.device.Transform.World;
    Matrix origProjection = drawArgs.device.Transform.Projection;
    bool origFog = drawArgs.device.RenderState.FogEnable;
    drawArgs.device.RenderState.FogEnable = false;
    float aspectRatio = (float)drawArgs.device.Viewport.Width / drawArgs.device.Viewport.Height;
    drawArgs.device.Transform.Projection = Matrix.PerspectiveFovRH((float)camera.Fov.Radians, aspectRatio, 1, float.MaxValue );
    drawArgs.device.Transform.World = Matrix.Translation(
     (float)-drawArgs.WorldCamera.ReferenceCenter.X,
     (float)-drawArgs.WorldCamera.ReferenceCenter.Y,
     (float)-drawArgs.WorldCamera.ReferenceCenter.Z
     );
    drawArgs.device.RenderState.ZBufferEnable = false;
    layerMesh.DrawSubset(0);
    drawArgs.device.Transform.World = origWorld;
    drawArgs.device.Transform.Projection = origProjection;
    drawArgs.device.RenderState.FogEnable = origFog;
    drawArgs.device.RenderState.ZBufferEnable = true;
   }
   if(isDownloading)
   {
    if(progressBar == null) progressBar = new WorldWind.VisualControl.ProgressBar(40,4);
    progressBar.Draw(drawArgs, drawArgs.screenWidth - 34, drawArgs.screenHeight - 10, ProgressPercent, downloadProgressColor);
    drawArgs.device.RenderState.ZBufferEnable = true;
   }
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   if(textureFileName != "")
   {
    try
    {
     texture = TextureLoader.FromFile(drawArgs.device, Path.Combine(pluginPath, textureFileName));
     isInitialized = true;
    }
    catch
    {
     textureFileName = "";
    }
   }
   else
   {
    StartDownload(pluginPath, "clouds_" + DateTimeStamp(DateTime.Now) + ".jpg");
    isInitialized = true;
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
   if(texture!=null)
   {
    texture.Dispose();
    texture = null;
   }
   if(layerMesh != null)
   {
    layerMesh.Dispose();
    layerMesh = null;
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
   public new void OnPropertiesClick(object sender, EventArgs e)
   {
   if(pDialog != null && ! pDialog.IsDisposed)
    return;
   pDialog = new propertiesDialog(this);
   pDialog.Show();
   }
  public class propertiesDialog : System.Windows.Forms.Form
  {
   private System.Windows.Forms.Label lblTexture;
   private System.Windows.Forms.ComboBox cboTexture;
   private System.Windows.Forms.Button btnOK;
   private System.Windows.Forms.Button btnCancel;
   private System.Windows.Forms.Button btnPrevious;
   private System.Windows.Forms.Button btnNext;
   private GlobalCloudsLayer layer;
   private string savedTextureFileName;
   public propertiesDialog( GlobalCloudsLayer layer )
   {
    this.layer = layer;
    InitializeComponent();
    DirectoryInfo di = new DirectoryInfo(layer.pluginPath);
    FileInfo[] imgFiles = di.GetFiles("*.png");
    cboTexture.Items.AddRange(imgFiles);
    int i = cboTexture.FindString(layer.textureFileName);
    if(i != -1) cboTexture.SelectedIndex = i;
    savedTextureFileName = layer.textureFileName;
   }
   private void InitializeComponent()
   {
    this.btnCancel = new System.Windows.Forms.Button();
    this.btnOK = new System.Windows.Forms.Button();
    this.btnPrevious = new System.Windows.Forms.Button();
    this.btnNext = new System.Windows.Forms.Button();
    this.lblTexture = new System.Windows.Forms.Label();
    this.cboTexture = new System.Windows.Forms.ComboBox();
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
    this.btnPrevious.Location = new System.Drawing.Point(20, 59);
    this.btnPrevious.Name = "btnPrevious";
    this.btnPrevious.TabIndex = 2;
    this.btnPrevious.Text = "Previous";
    this.btnPrevious.Click += new System.EventHandler(this.btnPrevious_Click);
    this.btnNext.Location = new System.Drawing.Point(106, 59);
    this.btnNext.Name = "btnNext";
    this.btnNext.TabIndex = 3;
    this.btnNext.Text = "Next";
    this.btnNext.Click += new System.EventHandler(this.btnNext_Click);
    this.lblTexture.AutoSize = true;
    this.lblTexture.Location = new System.Drawing.Point(16, 28);
    this.lblTexture.Name = "lblTexture";
    this.lblTexture.Size = new System.Drawing.Size(82, 16);
    this.lblTexture.TabIndex = 4;
    this.lblTexture.Text = "Clouds map :";
    this.cboTexture.Location = new System.Drawing.Point(96, 25);
    this.cboTexture.Name = "cboTexture";
    this.cboTexture.Size = new System.Drawing.Size(296, 21);
    this.cboTexture.TabIndex = 5;
    this.cboTexture.Text = "Select texture file";
    this.cboTexture.DropDownStyle = ComboBoxStyle.DropDownList;
    this.cboTexture.MaxDropDownItems = 10;
    this.AcceptButton = this.btnOK;
    this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
    this.CancelButton = this.btnCancel;
    this.ClientSize = new System.Drawing.Size(406, 94);
    this.ControlBox = false;
    this.Controls.Add(this.cboTexture);
    this.Controls.Add(this.lblTexture);
    this.Controls.Add(this.btnOK);
    this.Controls.Add(this.btnCancel);
    this.Controls.Add(this.btnPrevious);
    this.Controls.Add(this.btnNext);
    this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
    this.MaximizeBox = false;
    this.MinimizeBox = false;
    this.Name = "pDialog";
    this.ShowInTaskbar = false;
    this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
    this.Text = "GlobalClouds properties ";
    this.TopMost = true;
    this.ResumeLayout(false);
   }
   private void btnOK_Click(object sender, System.EventArgs e)
   {
    if(cboTexture.SelectedItem != null)
    {
     if(cboTexture.SelectedItem.ToString().IndexOf(".png") != -1)
     {
      layer.Dispose();
      layer.textureFileName = cboTexture.SelectedItem.ToString();
      layer.Initialize(layer.drawArgs);
      layer.SaveSettings();
     }
    }
    this.Close();
   }
   private void btnCancel_Click(object sender, System.EventArgs e)
   {
    if(layer.textureFileName != savedTextureFileName)
    {
     layer.Dispose();
     layer.textureFileName = savedTextureFileName;
     layer.Initialize(layer.drawArgs);
    }
    this.Close();
   }
   private void btnPrevious_Click(object sender, System.EventArgs e)
   {
    layer.GotoPrevious();
    int i = cboTexture.FindString(layer.textureFileName);
    if(i != -1) cboTexture.SelectedIndex = i;
   }
   private void btnNext_Click(object sender, System.EventArgs e)
   {
    layer.GotoNext();
    int i = cboTexture.FindString(layer.textureFileName);
    if(i != -1) cboTexture.SelectedIndex = i;
   }
  }
  public ArrayList historyList;
  public void BuildHistoryList()
  {
   historyList = new ArrayList();
   DirectoryInfo di = new DirectoryInfo(pluginPath);
   FileInfo[] imgFiles = di.GetFiles("clouds*.png");
   for(int i = 0; i < imgFiles.Length; i++)
   {
    historyList.Add(imgFiles[i].Name);
   }
   historyList.Sort();
  }
  public void GotoPrevious()
  {
   if(historyList == null) BuildHistoryList();
   int i = historyList.IndexOf(textureFileName);
   if(i != -1)
   {
    if(i > 0) i--; else i = historyList.Count - 1;
    textureFileName = (string)historyList[i];
    Dispose();
    Initialize(drawArgs);
   }
  }
  public void GotoNext()
  {
   if(historyList == null) BuildHistoryList();
   int i = historyList.IndexOf(textureFileName);
   if(i != -1)
   {
    if(i < historyList.Count - 1) i++; else i = 0;
    textureFileName = (string)historyList[i];
    Dispose();
    Initialize(drawArgs);
   }
  }
  public bool isDownloading = false;
  public float ProgressPercent;
  WebDownload download;
  WorldWind.VisualControl.ProgressBar progressBar;
  int downloadProgressColor = Color.FromArgb(180,200,200,200).ToArgb();
  int maxRetry = 3;
  int retryCount = 0;
  int retryDelaySeconds = 60;
  DateTime lastDownloadTime = DateTime.MinValue;
  private void DownloadCloudMap(string filePath, string fileName)
  {
   string url = GetServerUrl();
   System.Net.WebClient wc = new System.Net.WebClient();
   wc.DownloadFile(url, Path.Combine(filePath, fileName));
   wc.Dispose();
  }
  public virtual void StartDownload(string filePath, string fileName)
  {
   string url = GetServerUrl();
   download = new WebDownload(url);
   download.SavedFilePath = Path.Combine(filePath, fileName);
   download.ProgressCallback += new DownloadProgressHandler(UpdateProgress);
   download.CompleteCallback += new DownloadCompleteHandler(DownloadComplete);
   download.BackgroundDownloadFile();
   isDownloading = true;
  }
  public string GetServerUrl()
  {
   Random r = new Random();
   ArrayList serverList = new ArrayList();
   string line;
   TextReader tr = File.OpenText(Path.Combine(pluginPath, serverListFileName));
   while ((line = tr.ReadLine()) != null)
   {
    if(line.StartsWith("http://")) serverList.Add(line);
   }
   tr.Close();
   string Url = (string)serverList[r.Next(serverList.Count - 1)];
   return Url;
  }
  void UpdateProgress( int pos, int total )
  {
   if(total==0)
    total = 50*1024;
   pos = pos % (total+1);
   ProgressPercent = (float)pos/total;
  }
  private void DownloadComplete(WebDownload downloadInfo)
  {
   try
   {
    MakeAlphaPng(downloadInfo.SavedFilePath);
    CleanupHistory();
    FindLatest();
    Dispose();
    textureFileName = latestFileName;
    Initialize(drawArgs);
    SaveSettings();
    retryCount = 0;
   }
   catch(System.Net.WebException caught)
   {
    System.Net.HttpWebResponse response = caught.Response as System.Net.HttpWebResponse;
    if(response!=null)
    {
     Utility.Log.Write(Utility.Log.Levels.Warning, "Error downloading cloud map from " + downloadInfo.Url + " (" + response.StatusDescription + ").");
    }
    retryCount++;
   }
   catch(Exception caugth)
   {
    Utility.Log.Write(Utility.Log.Levels.Warning, "Error downloading cloud map from " + downloadInfo.Url + " (" + caugth.Message + ").");
    if(File.Exists(downloadInfo.SavedFilePath))
     File.Delete(downloadInfo.SavedFilePath);
    retryCount++;
   }
   finally
   {
    download.IsComplete = true;
    isDownloading = false;
    lastDownloadTime = DateTime.Now;
   }
  }
  public string DateTimeStamp(DateTime d)
  {
   return d.Year.ToString() + d.Month.ToString("d2") + d.Day.ToString("d2") + "-" + d.Hour.ToString("d2") + d.Minute.ToString("d2");
  }
  private void MakeAlphaPng(string filePath)
  {
   Bitmap b1, b2;
   int x, y;
   b1 = new Bitmap(filePath);
   b2 = new Bitmap(b1.Width, b1.Height, System.Drawing.Imaging.PixelFormat.Format32bppArgb);
   for(x = 0; x < b1.Width; x++)
   {
    for(y = 0; y < b1.Height; y++)
    {
     Color p = b1.GetPixel(x,y);
     b2.SetPixel(x, y, Color.FromArgb(p.R, p.R, p.G, p.B));
    }
   }
   b2.Save(filePath.Replace(".jpg", ".png"), System.Drawing.Imaging.ImageFormat.Png);
   b1.Dispose();
   b2.Dispose();
   b1 = null;
   b2 = null;
  }
  private Mesh TexturedSphere(Device device, float radius, int slices, int stacks)
  {
   int numVertices = (slices+1)*(stacks+1);
   int numFaces = slices*stacks*2;
   int indexCount = numFaces * 3;
   Mesh mesh = new Mesh(numFaces,numVertices,MeshFlags.Managed,CustomVertex.PositionNormalTextured.Format,device);
   int [] ranks = new int[1];
   ranks[0] = mesh.NumberVertices;
   System.Array arr = mesh.VertexBuffer.Lock(0,typeof(CustomVertex.PositionNormalTextured),LockFlags.None,ranks);
   int vertIndex=0;
   for(int stack=0;stack<=stacks;stack++)
   {
    double latitude = -90 + ((float)stack/stacks*(float)180.0);
    for(int slice=0;slice<=slices;slice++)
    {
     CustomVertex.PositionNormalTextured pnt = new CustomVertex.PositionNormalTextured();
     double longitude = 180 - ((float)slice/slices*(float)360);
     Vector3 v = MathEngine.SphericalToCartesian( latitude, longitude, radius);
     pnt.X = v.X;
     pnt.Y = v.Y;
     pnt.Z = v.Z;
     pnt.Tu = 1.0f-(float)slice/slices;
     pnt.Tv = 1.0f-(float)stack/stacks;
     arr.SetValue(pnt,vertIndex++);
    }
   }
   mesh.VertexBuffer.Unlock();
   ranks[0]=indexCount;
   arr = mesh.LockIndexBuffer(typeof(short),LockFlags.None,ranks);
   int i=0;
   short bottomVertex = 0;
   short topVertex = 0;
   for(short x=0;x<stacks;x++)
   {
    bottomVertex = (short)((slices+1)*x);
    topVertex = (short)(bottomVertex + slices + 1);
    for(int y=0;y<slices;y++)
    {
     arr.SetValue(bottomVertex,i++);
     arr.SetValue(topVertex,i++);
     arr.SetValue((short)(topVertex+1),i++);
     arr.SetValue(bottomVertex,i++);
     arr.SetValue((short)(topVertex+1),i++);
     arr.SetValue((short)(bottomVertex+1),i++);
     bottomVertex++;
     topVertex++;
    }
   }
   mesh.IndexBuffer.SetData(arr,0,LockFlags.None);
            mesh.IndexBuffer.Unlock();
   mesh.ComputeNormals();
   return mesh;
  }
 }
}
