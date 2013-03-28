using System;
using System.Diagnostics;
using System.Globalization;
using System.Net;
using System.IO;
using System.Threading;
using System.Xml;
using Utility;
namespace WorldWind.Net
{
 public delegate void DownloadProgressHandler(int bytesRead, int totalBytes);
 public delegate void DownloadCompleteHandler(WebDownload wd);
 public enum DownloadType
 {
  Unspecified,
  Wms
 }
 public class WebDownload : IDisposable
 {
  static public bool Log404Errors = false;
  static public bool useWindowsDefaultProxy = true;
  static public string proxyUrl = "";
  static public bool useDynamicProxy;
  static public string proxyUserName = "";
  static public string proxyPassword = "";
  public static string UserAgent = String.Format(
   CultureInfo.InvariantCulture,
   "World Wind v{0} ({1}, {2})",
   System.Windows.Forms.Application.ProductVersion,
   Environment.OSVersion.ToString(),
   CultureInfo.CurrentCulture.Name);
  public string Url;
  public Stream ContentStream;
  public string SavedFilePath;
  public bool IsComplete;
  public DownloadProgressHandler ProgressCallback;
  public static DownloadCompleteHandler DebugCallback;
  public static DownloadCompleteHandler DownloadEnded;
  public DownloadCompleteHandler CompleteCallback;
  public DownloadType DownloadType = DownloadType.Unspecified;
  public string ContentType;
  public int BytesProcessed;
  public int ContentLength;
  public DateTime DownloadStartTime = DateTime.MinValue;
  internal HttpWebRequest request;
  internal HttpWebResponse response;
  protected Exception downloadException;
  protected bool isMemoryDownload;
        private bool stopFlag = false;
        protected Thread dlThread;
  public WebDownload( string url )
  {
   this.Url = url;
  }
  public WebDownload()
  {
  }
  public bool IsDownloadInProgress
  {
   get
   {
    return dlThread != null && dlThread.IsAlive;
   }
  }
  public Exception Exception
  {
   get
   {
    return downloadException;
   }
  }
  public void BackgroundDownloadFile()
  {
   if (CompleteCallback==null)
    throw new ArgumentException("No download complete callback specified.");
   dlThread = new Thread(new ThreadStart(Download));
   dlThread.Name = "WebDownload.dlThread";
   dlThread.IsBackground = true;
   dlThread.CurrentUICulture = Thread.CurrentThread.CurrentUICulture;
   dlThread.Start();
  }
  public void BackgroundDownloadFile( DownloadCompleteHandler completeCallback )
  {
   CompleteCallback += completeCallback;
   BackgroundDownloadFile();
  }
  public void BackgroundDownloadFile( DownloadType dlType )
  {
   DownloadType = dlType;
   BackgroundDownloadFile();
  }
  public void BackgroundDownloadMemory()
  {
   if (CompleteCallback==null)
    throw new ArgumentException("No download complete callback specified.");
   isMemoryDownload = true;
   dlThread = new Thread(new ThreadStart(Download));
   dlThread.Name = "WebDownload.dlThread(2)";
   dlThread.IsBackground = true;
   dlThread.CurrentUICulture = Thread.CurrentThread.CurrentUICulture;
   dlThread.Start();
  }
  public void BackgroundDownloadMemory( DownloadCompleteHandler completeCallback )
  {
   CompleteCallback += completeCallback;
   BackgroundDownloadMemory();
  }
  public void BackgroundDownloadMemory( DownloadType dlType )
  {
   DownloadType = dlType;
   BackgroundDownloadMemory();
  }
  public void DownloadMemory()
  {
   isMemoryDownload = true;
   Download();
  }
  public void DownloadMemory( DownloadType dlType )
  {
   DownloadType = dlType;
   DownloadMemory();
  }
  public void DownloadMemory( DownloadProgressHandler progressCallback )
  {
   ProgressCallback += progressCallback;
   DownloadMemory();
  }
  public void DownloadFile( string destinationFile )
  {
   SavedFilePath = destinationFile;
   Download();
  }
  public void DownloadFile( string destinationFile, DownloadType dlType )
  {
   DownloadType = dlType;
   DownloadFile(destinationFile);
  }
  public void SaveMemoryDownloadToFile(string destinationFilePath )
  {
   if(ContentStream==null)
    throw new InvalidOperationException("No data available.");
   ContentStream.Seek(0,SeekOrigin.Begin);
   using(Stream fileStream = File.Create(destinationFilePath))
   {
    if(ContentStream is MemoryStream)
    {
     MemoryStream ms = (MemoryStream)ContentStream;
     fileStream.Write(ms.GetBuffer(), 0, (int)ms.Length);
    }
    else
    {
     byte[] buffer = new byte[4096];
     while(true)
     {
      int numRead = ContentStream.Read(buffer, 0, buffer.Length);
      if(numRead<=0)
       break;
      fileStream.Write(buffer,0,numRead);
     }
    }
   }
   ContentStream.Seek(0,SeekOrigin.Begin);
  }
  public void Cancel()
  {
   CompleteCallback = null;
   ProgressCallback = null;
   if (dlThread!=null && dlThread != Thread.CurrentThread)
   {
                if (dlThread.IsAlive)
                {
                    Log.Write(Log.Levels.Verbose, "WebDownload.Cancel() : stopping download thread...");
                    stopFlag = true;
                    if (!dlThread.Join(500))
                    {
                        Log.Write(Log.Levels.Warning, "WebDownload.Cancel() : download thread refuses to die, forcing Abort()");
                        dlThread.Abort();
                    }
                }
    dlThread = null;
   }
  }
  private void OnProgressCallback(int bytesRead, int totalBytes)
  {
   if (ProgressCallback != null)
   {
    ProgressCallback(bytesRead, totalBytes);
   }
  }
  private static void OnDebugCallback(WebDownload wd)
  {
   if (DebugCallback != null)
   {
    DebugCallback(wd);
   }
  }
  private static void OnDownloadEnded(WebDownload wd)
  {
   if (DownloadEnded != null)
   {
    DownloadEnded(wd);
   }
  }
  protected void Download()
  {
            Log.Write(Log.Levels.Debug, "Starting download thread...");
            Debug.Assert(Url.StartsWith("http://"));
   DownloadStartTime = DateTime.Now;
   try
   {
    try
    {
     OnProgressCallback(0, 1);
     OnDebugCallback(this);
                    if (stopFlag)
                    {
                        IsComplete = true;
                        return;
                    }
     if (isMemoryDownload && ContentStream == null)
     {
      ContentStream = new MemoryStream();
     }
     else
     {
      string targetDirectory = Path.GetDirectoryName(SavedFilePath);
      if(targetDirectory.Length > 0)
       Directory.CreateDirectory(targetDirectory);
      ContentStream = new FileStream(SavedFilePath, FileMode.Create);
     }
     request = (HttpWebRequest) WebRequest.Create(Url);
     request.UserAgent = UserAgent;
                    if (stopFlag)
                    {
                        IsComplete = true;
                        return;
                    }
     request.Proxy = ProxyHelper.DetermineProxyForUrl(
      Url,
      useWindowsDefaultProxy,
      useDynamicProxy,
      proxyUrl,
      proxyUserName,
      proxyPassword);
     using (response = request.GetResponse() as HttpWebResponse)
     {
      if (response.StatusCode == HttpStatusCode.OK)
      {
       ContentType = response.ContentType;
       string strContentLength = response.Headers["Content-Length"];
       if (strContentLength != null)
       {
        ContentLength = int.Parse(strContentLength, CultureInfo.InvariantCulture);
       }
       byte[] readBuffer = new byte[1500];
       using (Stream responseStream = response.GetResponseStream())
       {
        while (true)
                                {
                                    if (stopFlag)
                                    {
                                        IsComplete = true;
                                        return;
                                    }
         int bytesRead = responseStream.Read(readBuffer, 0, readBuffer.Length);
         if (bytesRead <= 0)
          break;
         ContentStream.Write(readBuffer, 0, bytesRead);
         BytesProcessed += bytesRead;
         OnProgressCallback(BytesProcessed, ContentLength);
         OnDebugCallback(this);
        }
       }
      }
     }
     HandleErrors();
    }
                catch (ThreadAbortException)
                {
                    Log.Write(Log.Levels.Verbose, "Re-throwing ThreadAbortException.");
                    throw;
                }
    catch (System.Configuration.ConfigurationException)
    {
     throw;
    }
    catch (Exception caught)
    {
     try
     {
      if (ContentStream != null)
      {
       ContentStream.Close();
       ContentStream = null;
      }
      if (SavedFilePath != null && SavedFilePath.Length > 0)
      {
       File.Delete(SavedFilePath);
      }
     }
     catch(Exception)
     {
     }
     SaveException(caught);
    }
                if (stopFlag)
                {
                    IsComplete = true;
                    return;
                }
    if (ContentLength == 0)
    {
     ContentLength = BytesProcessed;
     OnProgressCallback(BytesProcessed, ContentLength);
    }
    if (ContentStream is MemoryStream)
    {
     ContentStream.Seek(0, SeekOrigin.Begin);
    }
    else if (ContentStream != null)
    {
     ContentStream.Close();
     ContentStream = null;
    }
    OnDebugCallback(this);
    if (CompleteCallback == null)
    {
     Verify();
    }
    else
    {
     CompleteCallback(this);
    }
   }
   catch (ThreadAbortException)
   {
                Log.Write(Log.Levels.Verbose, "Download aborted.");
            }
   finally
   {
    IsComplete = true;
   }
   OnDownloadEnded(this);
  }
  private void HandleErrors()
  {
   if(ContentStream.Length == 15)
   {
    Exception ex = new FileNotFoundException("The remote server returned an error: (404) Not Found.", SavedFilePath );
    SaveException(ex);
   }
   if (DownloadType == DownloadType.Wms && (
    ContentType.StartsWith("text/xml") ||
    ContentType.StartsWith("application/vnd.ogc.se")))
   {
    SetMapServerError();
   }
  }
  public void Verify()
  {
   if(Exception!=null)
    throw Exception;
  }
  private void SaveException( Exception exception )
  {
   downloadException = exception;
   if(Exception is ThreadAbortException)
    return;
   if(Log404Errors)
   {
    Log.Write(Log.Levels.Error, "HTTP", "Error: " + Url );
    Log.Write(Log.Levels.Error+1, "HTTP", "     : " + exception.Message );
   }
  }
  private void SetMapServerError()
  {
   try
   {
    XmlDocument errorDoc = new XmlDocument();
    ContentStream.Seek(0,SeekOrigin.Begin);
    errorDoc.Load(ContentStream);
    string msg = "";
    foreach( XmlNode node in errorDoc.GetElementsByTagName("ServiceException"))
     msg += node.InnerText.Trim()+Environment.NewLine;
    SaveException( new WebException(msg.Trim()) );
   }
   catch(XmlException)
   {
    SaveException( new WebException("An error occurred while trying to download " + request.RequestUri.ToString()+".") );
   }
  }
  public void Dispose()
  {
   if (dlThread!=null && dlThread != Thread.CurrentThread)
   {
                if (dlThread.IsAlive)
                {
                    Log.Write(Log.Levels.Verbose, "WebDownload.Dispose() : stopping download thread...");
                    stopFlag = true;
                    if (!dlThread.Join(500))
                    {
                        Log.Write(Log.Levels.Warning, "WebDownload.Dispose() : download thread refuses to die, forcing Abort()");
                        dlThread.Abort();
                    }
                }
    dlThread = null;
   }
   if(request!=null)
   {
    request.Abort();
    request = null;
   }
   if (ContentStream != null)
   {
    ContentStream.Close();
    ContentStream=null;
   }
   if(DownloadStartTime != DateTime.MinValue)
    OnDebugCallback(this);
   GC.SuppressFinalize(this);
  }
 }
}
