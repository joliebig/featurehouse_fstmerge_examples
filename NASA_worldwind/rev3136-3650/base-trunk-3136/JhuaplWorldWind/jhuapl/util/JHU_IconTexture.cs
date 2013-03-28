using System;
using System.Collections;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Renderable;
using WorldWind.Menu;
using System.Windows.Forms;
namespace jhuapl.util
{
 public class JHU_IconTexture : IDisposable
 {
  public Texture Texture;
  public int Width;
  public int Height;
  public int ReferenceCount;
  public JHU_IconTexture(Device device, string textureFileName)
  {
   if(JHU_ImageHelper.IsGdiSupportedImageFormat(textureFileName))
   {
    using(Image image = JHU_ImageHelper.LoadImage(textureFileName))
     LoadImage(device, image);
   }
   else
   {
    Texture = JHU_ImageHelper.LoadIconTexture( device, textureFileName );
    using(Surface s = Texture.GetSurfaceLevel(0))
    {
     SurfaceDescription desc = s.Description;
     Width = desc.Width;
     Height = desc.Height;
    }
   }
  }
  public JHU_IconTexture(Device device, Bitmap image)
  {
   LoadImage(device, image);
  }
  protected void LoadImage(Device device, Image image)
  {
   Width = (int)Math.Round(Math.Pow(2, (int)(Math.Ceiling(Math.Log(image.Width)/Math.Log(2)))));
   if(Width>device.DeviceCaps.MaxTextureWidth)
    Width = device.DeviceCaps.MaxTextureWidth;
   Height = (int)Math.Round(Math.Pow(2, (int)(Math.Ceiling(Math.Log(image.Height)/Math.Log(2)))));
   if(Height>device.DeviceCaps.MaxTextureHeight)
    Height = device.DeviceCaps.MaxTextureHeight;
   using(Bitmap textureSource = new Bitmap(Width, Height))
   using(Graphics g = Graphics.FromImage(textureSource))
   {
    g.DrawImage(image, 0,0,Width,Height);
    if(Texture!=null)
     Texture.Dispose();
    Texture = new Texture(device, textureSource, Usage.None, Pool.Managed);
   }
  }
  public void Dispose()
  {
   if(Texture!=null)
   {
    Texture.Dispose();
    Texture = null;
   }
   GC.SuppressFinalize(this);
  }
 }
}
