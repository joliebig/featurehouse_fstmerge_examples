using System;
using System.IO;
using System.Text;
using Utility;
namespace jhuapl.util
{
 public class JHU_Log
 {
  static StreamWriter m_logWriter;
  static string m_logFileName = "";
  static string m_logPath = "";
  static string m_logFilePath = "";
  static int m_logLevel = 0;
  static bool m_autoIndent = true;
  public enum Severity
  {
   UNK,
   SUCC,
   INFO,
   WARN,
   ERR,
   FAIL
  }
  public static int LogLevel
  {
   get { return m_logLevel; }
   set { m_logLevel = value; }
  }
  public static string LogPath
  {
   get
   {
    if (m_logPath.Trim() == "")
     return JHU_Globals.getInstance().BasePath + "\\log";
    else
     return m_logPath;
   }
   set { m_logPath = value; }
  }
  public static bool AutoIndent
  {
   get { return m_autoIndent; }
   set { m_autoIndent = value; }
  }
  private JHU_Log()
  {}
  static JHU_Log()
  {
  }
  public static void Close()
  {
   if (m_logWriter != null)
   {
    m_logWriter.Close();
    m_logWriter = null;
   }
  }
  public static bool Open()
  {
   bool status = true;
   lock (typeof(JHU_Log))
   {
    try
    {
     Close();
     Directory.CreateDirectory(LogPath);
     if (m_logFileName.Trim() == "")
      m_logFileName = "CollabSpace";
     string name = m_logFileName;
     name += "-" + DateTime.UtcNow.ToString("yyyy-MM-dd-HH-mm-ss") + ".csv";
     m_logFilePath = Path.Combine( LogPath, name );
     m_logWriter = new StreamWriter(m_logFilePath, true);
     m_logWriter.AutoFlush = true;
     string logLine = string.Format("{0},{1},{2},{3},{4},{5},{6},{7}{8}",
      "Timestamp (UTC)",
      "Category",
      "Level",
      "Lat",
      "Lon",
      "Alt",
      "Name",
      "[Optional Indenting] ",
      "Message");
     m_logWriter.WriteLine( logLine );
    }
    catch (Exception caught)
    {
     Log.Write(caught);
     status = false;
    }
   }
   return status;
  }
  public static bool Open(string name)
  {
   m_logFileName = name;
   return Open();
  }
  public static void Write(int level, string category, double lat, double lon, double alt, string name, string message )
  {
   if (level <= m_logLevel)
   {
    if (m_logWriter == null)
    {
     Open();
    }
    try
    {
     lock(m_logWriter)
     {
      StringBuilder indent = new StringBuilder("");
      if (m_autoIndent)
      {
       for (int i = 0; i < level; i++)
       {
        indent.Append("\t");
       }
      }
      string logLine = string.Format("{0},{1},{2:00},{3:r},{4:r},{5:r},{6},{7}{8}",
       DateTime.UtcNow.ToString("u"),
       category.PadRight(4,' ').Substring(0,4),
       level,
       lat,
       lon,
       alt,
       name,
       indent.ToString(),
       message );
      m_logWriter.WriteLine( logLine );
     }
    }
    catch (Exception caught)
    {
     Log.Write(caught);
    }
   }
  }
  public static void Write( string message )
  {
   Write( 0, "UNK", 0.0, 0.0, 0.0, "UNK", message );
  }
  public static void Write( int level, string category, string name, string message )
  {
   Write( level, category, 0.0, 0.0, 0.0, name, message );
  }
 }
}
