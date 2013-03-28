using System;
using System.IO;
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
 }
 public interface IExecutionEnvironment
 {
  char DirectorySeparator { get; }
  bool IsRunningOnWindows { get; }
 }
}
