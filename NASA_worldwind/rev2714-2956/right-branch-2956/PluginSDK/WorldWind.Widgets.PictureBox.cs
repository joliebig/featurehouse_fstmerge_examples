using System;
using System.Diagnostics;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
namespace WorldWind.Widgets
{
 public class PictureBox : IWidget, IInteractive
 {
  string m_Text = "";
  byte m_Opacity = 255;
  System.Drawing.Point m_Location = new System.Drawing.Point(0,0);
  System.Drawing.Size m_Size = new System.Drawing.Size(0,20);
  bool m_Visible = true;
  bool m_Enabled = true;
  IWidget m_ParentWidget = null;
  object m_Tag = null;
  string m_Name = "";
  string m_SaveFilePath = null;
  System.Drawing.Color m_ForeColor = System.Drawing.Color.White;
  double m_RefreshTime = 0;
  System.Timers.Timer m_RefreshTimer = new System.Timers.Timer();
  string m_ImageUri = null;
  string clickableUrl = null;
  public string ClickableUrl
  {
   get{ return clickableUrl; }
   set{ clickableUrl = value; }
  }
  public double RefreshTime
  {
   get
   {
    return m_RefreshTime;
   }
   set
   {
    m_RefreshTime = value;
    if(m_RefreshTime > 0)
     m_RefreshTimer.Interval = value;
   }
  }
  public byte Opacity
  {
   get
   {
    return m_Opacity;
   }
   set
   {
    m_Opacity = value;
   }
  }
  public PictureBox()
  {
  }
  public string SaveFilePath
  {
   get
   {
    return m_SaveFilePath;
   }
   set
   {
    m_SaveFilePath = value;
   }
  }
  public string ImageUri
  {
   get
   {
    return m_ImageUri;
   }
   set
   {
    m_ImageUri = value;
   }
  }
  public string Name
  {
   get
   {
    return m_Name;
   }
   set
   {
    m_Name = value;
   }
  }
  public System.Drawing.Color ForeColor
  {
   get
   {
    return m_ForeColor;
   }
   set
   {
    m_ForeColor = value;
   }
  }
  public string Text
  {
   get
   {
    return m_Text;
   }
   set
   {
    m_Text = value;
   }
  }
  public IWidget ParentWidget
  {
   get
   {
    return m_ParentWidget;
   }
   set
   {
    m_ParentWidget = value;
   }
  }
  public bool Visible
  {
   get
   {
    return m_Visible;
   }
   set
   {
    m_Visible = value;
   }
  }
  public object Tag
  {
   get
   {
    return m_Tag;
   }
   set
   {
    m_Tag = value;
   }
  }
  public IWidgetCollection ChildWidgets
  {
   get
   {
    return null;
   }
   set
   {
   }
  }
  public System.Drawing.Size ClientSize
  {
   get
   {
    return m_Size;
   }
   set
   {
    m_Size = value;
   }
  }
  public bool Enabled
  {
   get
   {
    return m_Enabled;
   }
   set
   {
    m_Enabled = value;
   }
  }
  public System.Drawing.Point ClientLocation
  {
   get
   {
    return m_Location;
   }
   set
   {
    m_Location = value;
   }
  }
  public System.Drawing.Point AbsoluteLocation
  {
   get
   {
    if(m_ParentWidget != null)
    {
     return new System.Drawing.Point(
      m_Location.X + m_ParentWidget.AbsoluteLocation.X,
      m_Location.Y + m_ParentWidget.AbsoluteLocation.Y);
    }
    else
    {
     return m_Location;
    }
   }
  }
  Texture m_ImageTexture = null;
  string displayText = null;
  bool isLoading = false;
  Sprite m_sprite = null;
  SurfaceDescription m_surfaceDescription;
  public bool IsLoaded = false;
  public void Render(DrawArgs drawArgs)
  {
   if(m_Visible)
   {
    if(m_ImageTexture == null)
    {
     if(!m_RefreshTimer.Enabled)
     {
      displayText = "Loading Image...";
      m_RefreshTimer.Elapsed += new System.Timers.ElapsedEventHandler(m_RefreshTimer_Elapsed);
      m_RefreshTimer.Start();
     }
    }
    if(displayText != null)
    {
     drawArgs.defaultDrawingFont.DrawText(
      null,
      displayText,
      new System.Drawing.Rectangle(AbsoluteLocation.X, AbsoluteLocation.Y, m_Size.Width, m_Size.Height),
      DrawTextFormat.None,
      m_ForeColor);
    }
    if(m_ImageTexture != null && !isLoading)
    {
     drawArgs.device.SetTexture(0, m_ImageTexture);
     drawArgs.device.RenderState.ZBufferEnable = false;
     System.Drawing.Point ul = new System.Drawing.Point(AbsoluteLocation.X, AbsoluteLocation.Y);
     System.Drawing.Point ur = new System.Drawing.Point(AbsoluteLocation.X + m_Size.Width, AbsoluteLocation.Y);
     System.Drawing.Point ll = new System.Drawing.Point(AbsoluteLocation.X, AbsoluteLocation.Y + m_Size.Height);
     System.Drawing.Point lr = new System.Drawing.Point(AbsoluteLocation.X + m_Size.Width, AbsoluteLocation.Y + m_Size.Height);
     if(m_sprite == null)
      m_sprite = new Sprite(drawArgs.device);
     m_sprite.Begin(SpriteFlags.AlphaBlend);
     float xscale = (float)(ur.X - ul.X) / (float)m_surfaceDescription.Width;
     float yscale = (float)(lr.Y - ur.Y) / (float)m_surfaceDescription.Height;
     m_sprite.Transform = Matrix.Scaling(xscale,yscale,0);
     m_sprite.Transform *= Matrix.Translation(0.5f * (ul.X + ur.X), 0.5f * (ur.Y + lr.Y), 0);
     m_sprite.Draw( m_ImageTexture,
      new Vector3(m_surfaceDescription.Width / 2, m_surfaceDescription.Height / 2,0),
      Vector3.Empty,
      System.Drawing.Color.FromArgb(m_Opacity, 255, 255, 255).ToArgb()
      );
     m_sprite.Transform = Matrix.Identity;
     m_sprite.End();
    }
   }
  }
  bool isUpdating = false;
  private void m_RefreshTimer_Elapsed(object sender, System.Timers.ElapsedEventArgs e)
  {
   if(isUpdating)
    return;
   isUpdating = true;
   if(m_ImageUri == null)
    return;
   if(m_ImageUri.ToLower().StartsWith("http://"))
   {
    if(m_SaveFilePath == null)
     return;
    try
    {
     WorldWind.Net.WebDownload webDownload = new WorldWind.Net.WebDownload(m_ImageUri);
     webDownload.DownloadType = WorldWind.Net.DownloadType.Unspecified;
     System.IO.FileInfo saveFile = new System.IO.FileInfo(m_SaveFilePath);
     if(!saveFile.Directory.Exists)
      saveFile.Directory.Create();
     webDownload.DownloadFile(m_SaveFilePath);
    }
    catch{}
   }
   else
   {
    m_SaveFilePath = m_ImageUri;
   }
   if(m_ImageTexture != null && !m_ImageTexture.Disposed)
   {
    m_ImageTexture.Dispose();
    m_ImageTexture = null;
   }
   if(!System.IO.File.Exists(m_SaveFilePath))
   {
    displayText = "Image Not Found";
    return;
   }
   m_ImageTexture = ImageHelper.LoadTexture(m_SaveFilePath);
   m_surfaceDescription = m_ImageTexture.GetLevelDescription(0);
   int width = ClientSize.Width;
   int height = ClientSize.Height;
   if(ClientSize.Width == 0)
   {
    width = m_surfaceDescription.Width;
   }
   if(ClientSize.Height == 0)
   {
    height = m_surfaceDescription.Height;
   }
   if(ParentWidget is Widgets.Form)
   {
    Widgets.Form parentForm = (Widgets.Form)ParentWidget;
    parentForm.ClientSize = new System.Drawing.Size(width, height + parentForm.HeaderHeight);
   }
   else
   {
    ParentWidget.ClientSize = new System.Drawing.Size(width, height);
   }
   ClientSize = new System.Drawing.Size(width, height);
   IsLoaded = true;
   isUpdating = false;
   displayText = null;
   if(m_RefreshTime == 0)
    m_RefreshTimer.Stop();
  }
  public bool OnKeyDown(System.Windows.Forms.KeyEventArgs e)
  {
   return false;
  }
  public bool OnKeyUp(System.Windows.Forms.KeyEventArgs e)
  {
   return false;
  }
  public bool OnKeyPress(System.Windows.Forms.KeyPressEventArgs e)
  {
   return false;
  }
  public bool OnMouseDown(System.Windows.Forms.MouseEventArgs e)
  {
   return false;
  }
  public bool OnMouseEnter(EventArgs e)
  {
   return false;
  }
  public bool OnMouseLeave(EventArgs e)
  {
   return false;
  }
  public bool OnMouseMove(System.Windows.Forms.MouseEventArgs e)
  {
   return false;
  }
  private int clickBuffer = 5;
  public bool OnMouseUp(System.Windows.Forms.MouseEventArgs e)
  {
   if(ClickableUrl != null && e.X > AbsoluteLocation.X + clickBuffer && e.X < AbsoluteLocation.X + ClientSize.Width - clickBuffer &&
    e.Y > AbsoluteLocation.Y + clickBuffer && e.Y < AbsoluteLocation.Y + ClientSize.Height - clickBuffer)
   {
    ProcessStartInfo psi = new ProcessStartInfo();
    psi.FileName = ClickableUrl;
    psi.Verb = "open";
    psi.UseShellExecute = true;
    psi.CreateNoWindow = true;
    Process.Start(psi);
   }
   return false;
  }
  public bool OnMouseWheel(System.Windows.Forms.MouseEventArgs e)
  {
   return false;
  }
 }
}
