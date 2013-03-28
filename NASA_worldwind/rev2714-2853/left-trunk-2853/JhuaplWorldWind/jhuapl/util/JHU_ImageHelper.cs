using Microsoft.DirectX.Direct3D;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Diagnostics;
using System;
using System.IO;
using System.Windows.Forms;
using WorldWind;
namespace jhuapl.util
{
 public sealed class JHU_ImageHelper
 {
  private JHU_ImageHelper()
  {
  }
  public static bool IsGdiSupportedImageFormat(string imageFileName)
  {
   string extension = Path.GetExtension(imageFileName).ToLower();
   const string GdiSupportedExtensions = ".bmp.gif.jpg.jpeg.png.gif.tif";
   return GdiSupportedExtensions.IndexOf(extension)>=0;
  }
  public static Texture LoadTexture( Device device, string textureFileName )
  {
   return LoadTexture(device, textureFileName, 0);
  }
  public static Texture LoadTexture( Device device, string textureFileName, int transparentColor )
  {
   try
   {
    using(Stream imageStream = File.OpenRead(textureFileName))
     return LoadTexture(device, imageStream, transparentColor);
   }
   catch
   {
    throw new Microsoft.DirectX.Direct3D.InvalidDataException("Error reading image file '" + textureFileName +"'.");
   }
  }
  public static Texture LoadTexture( Device device, Stream textureStream )
  {
   return LoadTexture(device, textureStream, 0);
  }
  public static Texture LoadTexture( Device device, Stream textureStream, int transparentColor )
  {
   try
   {
    Texture texture = TextureLoader.FromStream(device, textureStream, 0, 0,
     1, Usage.None, World.Settings.TextureFormat, Pool.Managed, Filter.Box, Filter.Box, transparentColor);
    return texture;
   }
            catch (Microsoft.DirectX.Direct3D.InvalidDataException)
   {
   }
   try
   {
    using(Bitmap image = (Bitmap)Image.FromStream(textureStream))
    {
     Texture texture = new Texture(device, image, Usage.None, Pool.Managed);
     return texture;
    }
   }
   catch
   {
                throw new Microsoft.DirectX.Direct3D.InvalidDataException("Error reading image stream.");
   }
  }
  public static Image LoadImage( string bitmapFileName )
  {
   try
   {
    return Image.FromFile(bitmapFileName);
   }
   catch
   {
    Utility.Log.Write("IMAG", "Error loading image '" + bitmapFileName + "'.");
    return CreateDefaultImage();
   }
  }
  public static Cursor LoadCursor( string relativePath )
  {
   string fullPath = Path.Combine("Data\\Icons\\Interface", relativePath );
   try
   {
    Cursor res = new Cursor(fullPath);
    return res;
   }
   catch(Exception caught)
   {
    Utility.Log.Write("IMAG", "Unable to load cursor '"+relativePath+"': " + caught.Message);
    return Cursors.Default;
   }
  }
  public static Texture LoadIconTexture( Device device, string relativePath )
  {
   try
   {
    if(!Path.IsPathRooted(relativePath))
     relativePath = Path.Combine("Data\\Icons", relativePath );
    if(File.Exists(relativePath))
     return TextureLoader.FromFile(device, relativePath, 0, 0, 1,Usage.None,
      Format.Dxt5, Pool.Managed, Filter.Box, Filter.Box, 0 );
   }
   catch
   {
    Utility.Log.Write("IMAG", "Error loading texture '" + relativePath + "'.");
   }
   using(Bitmap bitmap = CreateDefaultImage())
    return new Texture(device, bitmap, 0, Pool.Managed);
  }
  public static void ConvertToDxt1(string originalImagePath, string outputDdsPath, Device device, bool eraseOriginal)
  {
   ConvertToDds(originalImagePath, outputDdsPath, Format.Dxt1, device, eraseOriginal);
  }
  public static void ConvertToDxt1(Stream originalImageStream, string outputDdsPath, Device device)
  {
   ConvertToDds(originalImageStream,outputDdsPath,Format.Dxt1,device);
  }
  public static void ConvertToDxt3(string originalImagePath, string outputDdsPath, Device device, bool eraseOriginal)
  {
   ConvertToDds(originalImagePath, outputDdsPath, Format.Dxt3, device, eraseOriginal);
  }
  public static void ConvertToDxt3(Stream originalImageStream, string outputDdsPath, Device device)
  {
   ConvertToDds(originalImageStream,outputDdsPath,Format.Dxt3,device);
  }
  public static void ConvertToDds(string originalImagePath, string outputDdsPath, Format format, Device device, bool eraseOriginal)
  {
   try
   {
    using( Texture t = TextureLoader.FromFile(
        device,
        originalImagePath,
        0, 0,
        1,0, format, Pool.Scratch,
        Filter.Box | Filter.DitherDiffusion, Filter.None, 0))
     TextureLoader.Save(outputDdsPath,ImageFileFormat.Dds,t);
    if(eraseOriginal)
     File.Delete(originalImagePath);
   }
            catch (Microsoft.DirectX.Direct3D.InvalidDataException)
   {
    throw new ApplicationException("Failed to load image data from " + originalImagePath +".");
   }
  }
  public static void ConvertToDds(Stream originalImageStream, string outputDdsPath, Format format, Device device)
  {
   try
   {
    originalImageStream.Seek(0, SeekOrigin.Begin);
    using( Texture t = TextureLoader.FromStream(
         device,
         originalImageStream,
         0, 0,
         1, 0, format, Pool.Scratch,
         Filter.Box | Filter.DitherDiffusion, Filter.None, 0))
     TextureLoader.Save(outputDdsPath,ImageFileFormat.Dds,t);
   }
            catch (Microsoft.DirectX.Direct3D.InvalidDataException)
   {
    throw new ApplicationException("Failed to load image data from stream.");
   }
  }
  private static Bitmap CreateDefaultImage()
  {
   Bitmap b = new Bitmap(32,32);
   using( Graphics g = Graphics.FromImage(b))
   {
    g.Clear(Color.FromArgb(88,255,255,255));
    g.DrawLine(Pens.Red, 0,0,b.Width, b.Height);
    g.DrawLine(Pens.Red, 0,b.Height,b.Width,0);
   }
   return b;
  }
 }
}
