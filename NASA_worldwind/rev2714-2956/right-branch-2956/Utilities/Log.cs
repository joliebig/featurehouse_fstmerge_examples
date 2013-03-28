using System;
using System.IO;
using System.Diagnostics;
using System.Windows.Forms;
namespace Utility
{
 public sealed class Log
 {
  static StreamWriter logWriter;
  static string logPath;
  static string logFilePath;
  private Log()
  {}
  static Log()
  {
   try
   {
    logPath = DefaultSettingsDirectory();
    Directory.CreateDirectory(logPath);
    logFilePath = Path.Combine( logPath, "WorldWind.log" );
    logWriter = new StreamWriter(logFilePath, true);
    logWriter.AutoFlush = true;
   }
   catch (Exception caught)
   {
    throw new System.ApplicationException(String.Format("Unexpected logfile error: {0}", logFilePath), caught);
   }
  }
  public static string DefaultSettingsDirectory()
  {
   return Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + DefaultSettingsDirectorySuffix();
  }
  public static string DefaultSettingsDirectorySuffix()
  {
   Version ver = new Version(Application.ProductVersion);
   return string.Format(@"\{0}\{1}\{2}.{3}.{4}.{5}", Application.CompanyName, Application.ProductName, ver.Major, ver.Minor, ver.Build, ver.Revision);
  }
  public static void Write( string category, string message )
  {
   try
   {
    lock(logWriter)
    {
     string logLine = string.Format("{0} {1} {2}",
      DateTime.Now.ToString("u"),
      category.PadRight(4,' ').Substring(0,4),
      message );
     logWriter.WriteLine( logLine );
    }
   }
   catch (Exception caught)
   {
    throw new System.ApplicationException(String.Format("Unexpected logging error on write(1)"), caught);
   }
  }
  [Conditional("DEBUG")]
  public static void DebugWrite( string category, string message )
  {
   Debug.Write( category, message );
  }
  public static void Write( string message )
  {
   Write( "", message );
  }
  [Conditional("DEBUG")]
  public static void DebugWrite( string message )
  {
   Write( "", message );
  }
  public static void Write( Exception caught )
  {
   try
   {
    if (caught is System.Threading.ThreadAbortException)
     return;
    string functionName = "Unknown";
    if(caught.StackTrace != null)
    {
     string firstStackTraceLine = caught.StackTrace.Split('\n')[0];
     functionName = firstStackTraceLine.Trim().Split(" (".ToCharArray())[1];
    }
    string logFileName = string.Format("DEBUG_{0}.txt", DateTime.Now.ToString("yyyy-MM-dd-HH-mm-ss") );
    string logFullPath = Path.Combine(logPath, logFileName);
    using (StreamWriter sw = new StreamWriter(logFullPath, false))
    {
     sw.WriteLine(caught.ToString());
    }
   }
   catch (Exception caught2)
   {
    throw new System.ApplicationException(String.Format("{0}\nUnexpected logging error on write(2)", caught.Message), caught2);
   }
  }
  [Conditional("DEBUG")]
  public static void DebugWrite( Exception caught )
  {
   try
   {
    if (caught is System.Threading.ThreadAbortException)
     return;
    string functionName = "Unknown";
    if(caught.StackTrace != null)
    {
     string firstStackTraceLine = caught.StackTrace.Split('\n')[0];
     functionName = firstStackTraceLine.Trim().Split(" (".ToCharArray())[1];
    }
    string logFileName = string.Format("DEBUG_{0}.txt", DateTime.Now.ToString("yyyy-MM-dd-HH-mm-ss") );
    string logFullPath = Path.Combine(logPath, logFileName);
    using (StreamWriter sw = new StreamWriter(logFullPath, false))
    {
     sw.WriteLine(caught.ToString());
    }
   }
   catch (Exception caught2)
   {
    throw new System.ApplicationException(String.Format("{0}\nUnexpected logging error on write(3)", caught.Message), caught2);
   }
  }
 }
}
