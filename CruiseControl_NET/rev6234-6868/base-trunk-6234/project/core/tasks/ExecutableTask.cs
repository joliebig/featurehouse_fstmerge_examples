using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
using System.Diagnostics;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
 [ReflectorType("exec")]
 public class ExecutableTask
        : BaseExecutableTask
 {
  public const int DEFAULT_BUILD_TIMEOUT = 600;
  public ExecutableTask() : this(new ProcessExecutor())
  {}
  public ExecutableTask(ProcessExecutor executor)
  {
   this.executor = executor;
  }
  [ReflectorProperty("executable", Required = true)]
  public string Executable = string.Empty;
  [ReflectorProperty("baseDirectory", Required = false)]
  public string ConfiguredBaseDirectory = string.Empty;
  [ReflectorProperty("buildArgs", Required = false)]
  public string BuildArgs = string.Empty;
  [ReflectorArray("environment", Required = false)]
  public EnvironmentVariable[] EnvironmentVariables = new EnvironmentVariable[0];
  private int[] successExitCodes;
  [ReflectorProperty("successExitCodes", Required = false)]
  public string SuccessExitCodes
  {
   get
            {
                string result =string.Empty;
                if (successExitCodes != null)
                {
                    foreach (int code in successExitCodes)
                    {
                        if (result !=string.Empty)
                            result = result + ",";
                        result = result + code;
                    }
                }
                return result;
            }
   set
   {
    string[] codes = value.Split(',');
    if (codes.Length == 0)
    {
     successExitCodes = null;
     return;
    }
    successExitCodes = new int[codes.Length];
    for (int i = 0; i < codes.Length; ++i)
    {
     successExitCodes[i] = Int32.Parse(codes[i]);
    }
   }
  }
  [ReflectorProperty("buildTimeoutSeconds", Required = false)]
  public int BuildTimeoutSeconds = DEFAULT_BUILD_TIMEOUT;
        protected override bool Execute(IIntegrationResult result)
  {
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : string.Format("Executing {0}", Executable));
   ProcessInfo info = CreateProcessInfo(result);
   SetConfiguredEnvironmentVariables(info.EnvironmentVariables, EnvironmentVariables);
   ProcessResult processResult = TryToRun(info, result);
            if (!StringUtil.IsWhitespace(processResult.StandardOutput) || !StringUtil.IsWhitespace(processResult.StandardError))
            {
                ProcessResult newResult = new ProcessResult(
                    StringUtil.MakeBuildResult(processResult.StandardOutput,string.Empty),
     StringUtil.MakeBuildResult(processResult.StandardError, "Error"),
                    processResult.ExitCode,
                    processResult.TimedOut,
     processResult.Failed);
                processResult = newResult;
            }
            result.AddTaskResult(new ProcessTaskResult(processResult));
         if (processResult.TimedOut)
          throw new BuilderException(this, "Command Line Build timed out (after " + BuildTimeoutSeconds + " seconds)");
            return !processResult.Failed;
  }
  protected override string GetProcessFilename()
  {
   return Executable;
  }
  protected override string GetProcessArguments(IIntegrationResult result)
  {
   return BuildArgs;
  }
  protected override string GetProcessBaseDirectory(IIntegrationResult result)
  {
   return result.BaseFromWorkingDirectory(ConfiguredBaseDirectory);
  }
  protected override int[] GetProcessSuccessCodes()
  {
   return successExitCodes;
  }
  protected override int GetProcessTimeout()
  {
   return BuildTimeoutSeconds*1000;
  }
  public override string ToString()
  {
   return string.Format(@" BaseDirectory: {0}, Executable: {1}", ConfiguredBaseDirectory, Executable);
  }
        private static void SetConfiguredEnvironmentVariables(StringDictionary variablePool, IEnumerable<EnvironmentVariable> varsToSet)
        {
            foreach (EnvironmentVariable item in varsToSet)
                variablePool[item.name] = item.value;
        }
    }
}
