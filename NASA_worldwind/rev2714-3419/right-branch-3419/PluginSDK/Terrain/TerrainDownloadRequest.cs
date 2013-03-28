using System;
using System.Windows.Forms;
using System.IO;
using System.Net;
using System.Diagnostics;
using System.Globalization;
using WorldWind.Configuration;
using WorldWind.Net;
using ICSharpCode.SharpZipLib.Zip;
namespace WorldWind.Terrain
{
 public class TerrainDownloadRequest : GeoSpatialDownloadRequest
 {
  public TerrainTile TerrainTile;
        const string ContentTypeZip = "application/zip";
  const string ContentType7z = "application/x-7z-compressed";
  const string ContentTypeXCompressed = "application/x-compressed";
  public TerrainDownloadRequest(TerrainTile tile, TerrainTileService owner, int row, int col, int targetLevel) : base(owner)
  {
   TerrainTile = tile;
   download.Url = String.Format(CultureInfo.InvariantCulture,
    "{0}?T={1}&L={2}&X={3}&Y={4}",
    owner.ServerUrl,
    owner.DataSet,
    targetLevel, col, row );
  }
  public override float West
  {
   get
   {
    return (float)TerrainTile.West;
   }
  }
  public override float East
  {
   get
   {
    return (float)TerrainTile.East;
   }
  }
  public override float North
  {
   get
   {
    return (float)TerrainTile.North;
   }
  }
  public override float South
  {
   get
   {
    return (float)TerrainTile.South;
   }
  }
  public override int Color
  {
   get
   {
    return World.Settings.DownloadTerrainRectangleColor.ToArgb();
   }
  }
  protected void ProcessFile()
  {
            if (download.ContentType == ContentTypeZip)
            {
                string compressedPath = download.SavedFilePath + ".zip";
                DirectoryInfo tempDirectory = new DirectoryInfo(Path.GetDirectoryName(compressedPath) + "\\" + System.DateTime.Now.Ticks.ToString());
                tempDirectory.Create();
                string tempFullPath = Path.Combine(tempDirectory.FullName, Path.GetFileNameWithoutExtension(download.SavedFilePath));
                if (File.Exists(compressedPath))
                    File.Delete(compressedPath);
                if (File.Exists(tempFullPath))
                    File.Delete(tempFullPath);
                if (File.Exists(SaveFilePath))
                    File.Delete(SaveFilePath);
                File.Move(download.SavedFilePath, compressedPath);
                FastZip fastZip = new FastZip();
                fastZip.ExtractZip(
                    compressedPath,
                    tempDirectory.FullName,
                    "");
                File.Move(tempFullPath, SaveFilePath);
                try
                {
                    File.Delete(compressedPath);
                    tempDirectory.Delete();
                }
                catch { }
            }
            else if (download.ContentType == ContentType7z || download.ContentType == ContentTypeXCompressed)
   {
    string compressedPath = download.SavedFilePath + ".7z";
    string tempDirectory = Path.GetDirectoryName(Path.GetDirectoryName(download.SavedFilePath));
    string tempFullPath = Path.Combine(tempDirectory, Path.GetFileNameWithoutExtension( download.SavedFilePath ) );
    if(File.Exists(compressedPath))
     File.Delete(compressedPath);
    if(File.Exists(tempFullPath))
     File.Delete(tempFullPath);
    File.Move(download.SavedFilePath, compressedPath);
    ProcessStartInfo psi = new ProcessStartInfo(Path.GetDirectoryName(Application.ExecutablePath) + @"\System\7za.exe");
    psi.UseShellExecute = false;
    psi.CreateNoWindow = true;
    psi.Arguments = String.Format(CultureInfo.InvariantCulture,
     " x -y -o\"{1}\" \"{0}\"", compressedPath, tempDirectory);
    using( Process p = Process.Start(psi) )
    {
     p.WaitForExit();
     if(p.ExitCode!=0)
      throw new ApplicationException(string.Format("7z decompression of file '{0}' failed.", compressedPath));
    }
    File.Delete(compressedPath);
    File.Move(tempFullPath, SaveFilePath);
   }
  }
  protected override void DownloadComplete()
  {
   try
   {
    download.Verify();
    ProcessFile();
   }
   catch(FileNotFoundException)
   {
    FlagBadTile();
   }
   catch(WebException caught)
   {
    HttpWebResponse response = caught.Response as HttpWebResponse;
    if(response!=null && response.StatusCode==HttpStatusCode.NotFound)
    {
     FlagBadTile();
    }
   }
   catch
   {
   }
  }
  public void DownloadInForeground()
  {
   try
   {
    download.DownloadFile(download.SavedFilePath);
    ProcessFile();
   }
   catch(FileNotFoundException)
   {
    FlagBadTile();
   }
   catch(WebException caught)
   {
    HttpWebResponse response = caught.Response as HttpWebResponse;
    if(response!=null && response.StatusCode==HttpStatusCode.NotFound)
    {
     FlagBadTile();
    }
   }
   catch
   {
   }
  }
  void FlagBadTile()
  {
   using( Stream flagFile = File.Create(SaveFilePath) )
   {
   }
  }
  public override float CalculateScore()
  {
   return 0;
  }
 }
}
