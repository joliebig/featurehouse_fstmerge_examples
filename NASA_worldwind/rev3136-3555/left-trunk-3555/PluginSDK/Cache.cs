using System;
using System.Collections;
using System.IO;
using System.Threading;
using Utility;
namespace WorldWind
{
 public class Cache : IDisposable
 {
  public long CacheUpperLimit = 2L * 1024L * 1024L * 1024L;
  public long CacheLowerLimit = 1536L * 1024L * 1024L;
  public string CacheDirectory;
  public TimeSpan CleanupFrequency;
  Timer m_timer;
  public Cache(
   string cacheDirectory,
   TimeSpan cleanupFrequencyInterval,
   TimeSpan totalRunTime)
  {
   this.CleanupFrequency = cleanupFrequencyInterval;
   this.CacheDirectory = cacheDirectory;
   Directory.CreateDirectory(this.CacheDirectory);
   double firstDueSeconds = cleanupFrequencyInterval.TotalSeconds -
    totalRunTime.TotalSeconds % cleanupFrequencyInterval.TotalSeconds;
   m_timer = new Timer( new TimerCallback(OnTimer), null,
    (long)(firstDueSeconds*1000),
    (long)cleanupFrequencyInterval.TotalMilliseconds );
  }
  public Cache(
   string cacheDirectory,
   long cacheLowerLimit,
   long cacheUpperLimit,
   TimeSpan cleanupFrequencyInterval,
   TimeSpan totalRunTime)
   : this(cacheDirectory, cleanupFrequencyInterval, totalRunTime )
  {
   this.CacheLowerLimit = cacheLowerLimit;
   this.CacheUpperLimit = cacheUpperLimit;
  }
  private void OnTimer(object state)
  {
   try
   {
    Thread.CurrentThread.Priority = ThreadPriority.BelowNormal;
    long dirSize = GetDirectorySize(new DirectoryInfo(this.CacheDirectory));
    if(dirSize < this.CacheUpperLimit)
     return;
    ArrayList fileInfoList = GetDirectoryFileInfoList(new DirectoryInfo(this.CacheDirectory));
    while(dirSize > this.CacheLowerLimit)
    {
     if (fileInfoList.Count <= 100)
      break;
     FileInfo oldestFile = null;
     foreach(FileInfo curFile in fileInfoList)
     {
      if(oldestFile == null)
      {
       oldestFile = curFile;
       continue;
      }
      if(curFile.LastAccessTimeUtc < oldestFile.LastAccessTimeUtc)
      {
       oldestFile = curFile;
      }
     }
     fileInfoList.Remove(oldestFile);
     dirSize -= oldestFile.Length;
     try
     {
      File.Delete(oldestFile.FullName);
      string directory = oldestFile.Directory.FullName;
      while(Directory.GetFileSystemEntries(directory).Length==0)
      {
       Directory.Delete(directory);
       directory = Path.GetDirectoryName(directory);
      }
     }
     catch(IOException)
     {
     }
    }
   }
   catch(Exception caught)
   {
    Log.Write(Log.Levels.Error, "CACH", caught.Message);
   }
  }
  public static ArrayList GetDirectoryFileInfoList(DirectoryInfo inDir)
  {
   ArrayList returnList = new ArrayList();
   foreach(DirectoryInfo subDir in inDir.GetDirectories())
   {
    returnList.AddRange(GetDirectoryFileInfoList(subDir));
   }
   foreach(FileInfo fi in inDir.GetFiles())
   {
    returnList.Add(fi);
   }
   return returnList;
  }
  public static long GetDirectorySize(DirectoryInfo inDir)
  {
   long returnBytes = 0;
   foreach(DirectoryInfo subDir in inDir.GetDirectories())
   {
    returnBytes += GetDirectorySize(subDir);
   }
   foreach(FileInfo fi in inDir.GetFiles())
   {
    try
    {
     returnBytes += fi.Length;
    }
    catch(System.IO.IOException)
    {
    }
   }
   return returnBytes;
  }
  public override string ToString()
  {
   return CacheDirectory;
  }
  public void Dispose()
  {
   if(m_timer!=null)
   {
    m_timer.Dispose();
    m_timer = null;
   }
  }
 }
}
