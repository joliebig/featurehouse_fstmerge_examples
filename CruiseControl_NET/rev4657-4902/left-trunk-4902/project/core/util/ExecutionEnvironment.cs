using System;
using System.IO;
namespace ThoughtWorks.CruiseControl.Core.Util
{
 public sealed class ExecutionEnvironment : IExecutionEnvironment
 {
  public char DirectorySeparator
  {
   get { return Path.DirectorySeparatorChar; }
  }
  public bool IsRunningOnWindows
  {
   get
   {
    int platform = (int) Environment.OSVersion.Platform;
    return ((platform != 4) && (platform != 128));
   }
  }
 }
 public interface IExecutionEnvironment
 {
  char DirectorySeparator { get; }
  bool IsRunningOnWindows { get; }
 }
}
