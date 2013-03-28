using System;
using System.IO;
using System.Runtime.InteropServices;
namespace ThoughtWorks.CruiseControl.Core.Util
{
 public sealed class ExecutionEnvironment : IExecutionEnvironment
 {
  private static bool? isRunningOnWindows;
  public char DirectorySeparator
  {
   get { return Path.DirectorySeparatorChar; }
  }
  public bool IsRunningOnWindows
  {
   get
   {
    if (isRunningOnWindows.HasValue)
     return isRunningOnWindows.Value;
    int platform = (int) Environment.OSVersion.Platform;
    isRunningOnWindows = ((platform != 4)
     && (platform != 6)
     && (platform != 128));
    return isRunningOnWindows.Value;
   }
  }
  public string RuntimeDirectory
  {
   get
   {
    return RuntimeEnvironment.GetRuntimeDirectory();
   }
  }
  public string GetDefaultProgramDataFolder(ApplicationType application)
  {
            if (application == ApplicationType.Unknown)
            {
                throw new ArgumentOutOfRangeException("application");
            }
            var pgfPath = AppDomain.CurrentDomain.BaseDirectory;
            return pgfPath;
  }
  public string EnsurePathIsRooted(string path)
  {
   if (!Path.IsPathRooted(path))
   {
    path = Path.Combine(
     GetDefaultProgramDataFolder(ApplicationType.Server),
     path);
   }
   return path;
  }
 }
 public interface IExecutionEnvironment
 {
  char DirectorySeparator { get; }
  bool IsRunningOnWindows { get; }
  string RuntimeDirectory { get; }
  string GetDefaultProgramDataFolder(ApplicationType application);
  string EnsurePathIsRooted(string path);
 }
 public enum ApplicationType
 {
  Unknown = 0,
  Server = 1,
  WebDashboard = 2
 }
}
