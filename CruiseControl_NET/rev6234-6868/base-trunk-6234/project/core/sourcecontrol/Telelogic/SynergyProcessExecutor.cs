using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.Telelogic
{
 public class SynergyProcessExecutor : ProcessExecutor
 {
  public override ProcessResult Execute(ProcessInfo processInfo)
  {
   char bell = (char) 0x07;
   char empty = ' ';
   ProcessResult retVal = base.Execute(processInfo);
   string standardOutput = retVal.StandardOutput.Replace(bell, empty);
   string standardError = retVal.StandardError.Replace(bell, empty);
   return new ProcessResult(standardOutput, standardError, retVal.ExitCode, retVal.TimedOut);
  }
 }
}
