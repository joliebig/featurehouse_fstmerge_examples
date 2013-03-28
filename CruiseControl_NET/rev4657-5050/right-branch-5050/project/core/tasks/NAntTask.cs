using System.Collections;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
 [ReflectorType("nant")]
 public class NAntTask : BaseExecutableTask
 {
  public const int DefaultBuildTimeout = 600;
  public const string defaultExecutable = "nant";
  public const string DefaultLogger = "NAnt.Core.XmlLogger";
  public const bool DefaultNoLogo = true;
  public NAntTask():
   this(new ProcessExecutor()){}
  public NAntTask(ProcessExecutor executor)
  {
   this.executor = executor;
  }
  [ReflectorArray("targetList", Required = false)]
  public string[] Targets = new string[0];
  [ReflectorProperty("executable", Required = false)]
  public string Executable = defaultExecutable;
  [ReflectorProperty("buildFile", Required = false)]
  public string BuildFile = string.Empty;
  [ReflectorProperty("baseDirectory", Required = false)]
  public string ConfiguredBaseDirectory = string.Empty;
  [ReflectorProperty("buildArgs", Required = false)]
  public string BuildArgs = string.Empty;
  [ReflectorProperty("logger", Required = false)]
  public string Logger = DefaultLogger;
  [ReflectorProperty("nologo", Required = false)]
  public bool NoLogo = DefaultNoLogo;
  [ReflectorProperty("buildTimeoutSeconds", Required = false)]
  public int BuildTimeoutSeconds = DefaultBuildTimeout;
        [ReflectorProperty("description", Required = false)]
        public string Description = string.Empty;
  public override void Run(IIntegrationResult result)
  {
            result.BuildProgressInformation.SignalStartRunTask(Description != string.Empty ? Description :
                string.Format("Executing Nant :BuildFile: {0} Targets: {1} ", BuildFile, string.Join(", ", Targets)));
            ProcessResult processResult = TryToRun(CreateProcessInfo(result));
   result.AddTaskResult(new ProcessTaskResult(processResult));
   if (processResult.TimedOut)
    throw new BuilderException(this, "NAnt process timed out (after " + BuildTimeoutSeconds + " seconds)");
  }
  protected override string GetProcessFilename()
  {
   return Executable;
  }
  protected override int GetProcessTimeout()
  {
   return BuildTimeoutSeconds * 1000;
  }
  protected override string GetProcessArguments(IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendIf(NoLogo, "-nologo");
   buffer.AppendArgument(@"-buildfile:{0}", StringUtil.AutoDoubleQuoteString(BuildFile));
   buffer.AppendArgument("-logger:{0}", Logger);
   buffer.AppendArgument(BuildArgs);
   AppendIntegrationResultProperties(buffer, result);
   AppendTargets(buffer);
   return buffer.ToString();
  }
  protected override string GetProcessBaseDirectory(IIntegrationResult result)
  {
   return result.BaseFromWorkingDirectory(ConfiguredBaseDirectory);
  }
  private static void AppendIntegrationResultProperties(ProcessArgumentBuilder buffer, IIntegrationResult result)
  {
   IDictionary properties = result.IntegrationProperties;
   foreach (string key in properties.Keys)
   {
    object value = result.IntegrationProperties[key];
    if (value != null)
     buffer.AppendArgument(string.Format("-D:{0}={1}", key, StringUtil.AutoDoubleQuoteString(StringUtil.RemoveTrailingPathDelimeter(StringUtil.IntegrationPropertyToString(value)))));
   }
  }
  private void AppendTargets(ProcessArgumentBuilder buffer)
  {
   foreach(string t in Targets)
   {
    buffer.AppendArgument(t);
   }
  }
  public override string ToString()
  {
   string baseDirectory = ConfiguredBaseDirectory ?? "";
   return string.Format(@" BaseDirectory: {0}, Targets: {1}, Executable: {2}, BuildFile: {3}", baseDirectory, string.Join(", ", Targets), Executable, BuildFile);
  }
  public string TargetsForPresentation
  {
   get
   {
    return StringUtil.ArrayToNewLineSeparatedString(Targets);
   }
   set
   {
    Targets = StringUtil.NewLineSeparatedStringToArray(value);
   }
  }
 }
}
