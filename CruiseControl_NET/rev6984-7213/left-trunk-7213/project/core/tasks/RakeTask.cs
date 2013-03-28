namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System.Diagnostics;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("rake")]
 public class RakeTask
        : BaseExecutableTask
 {
  public const int DefaultBuildTimeout = 600;
  public const string DefaultExecutable = @"rake";
        public const ProcessPriorityClass DefaultPriority = ProcessPriorityClass.Normal;
        public RakeTask()
            : this(new ProcessExecutor()) { }
        public RakeTask(ProcessExecutor executor)
        {
            this.executor = executor;
            this.BuildArgs = string.Empty;
            this.BaseDirectory = string.Empty;
            this.BuildTimeoutSeconds = RakeTask.DefaultBuildTimeout;
            this.Executable = RakeTask.DefaultExecutable;
            this.Priority = RakeTask.DefaultPriority;
            this.Rakefile = string.Empty;
            this.Targets = new string[0];
        }
        [ReflectorProperty("buildArgs", Required = false)]
        public string BuildArgs { get; set; }
        [ReflectorProperty("baseDirectory", Required = false)]
        public string BaseDirectory { get; set; }
        [ReflectorProperty("buildTimeoutSeconds", Required = false)]
        public int BuildTimeoutSeconds { get; set; }
        [ReflectorProperty("quiet", Required = false)]
        public bool Quiet { get; set; }
        [ReflectorProperty("executable", Required = false)]
        public string Executable { get; set; }
        [ReflectorProperty("priority", Required = false)]
        public ProcessPriorityClass Priority { get; set; }
        [ReflectorProperty("rakefile", Required = false)]
        public string Rakefile { get; set; }
        [ReflectorProperty("silent", Required = false)]
        public bool Silent { get; set; }
        [ReflectorProperty("targetList", Required = false)]
        public string[] Targets { get; set; }
        [ReflectorProperty("trace", Required = false)]
        public bool Trace { get; set; }
  protected override bool Execute(IIntegrationResult result)
  {
   ProcessInfo processInfo = CreateProcessInfo(result);
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : string.Format("Executing Rake: {0}", processInfo.PublicArguments));
   ProcessResult processResult = TryToRun(processInfo, result);
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
            return (!processResult.Failed);
  }
  protected override string GetProcessArguments(IIntegrationResult result)
  {
   ProcessArgumentBuilder args = new ProcessArgumentBuilder();
   args.AddArgument("--rakefile", Rakefile);
   if (Silent)
    args.AddArgument("--silent");
   else if (Quiet)
    args.AddArgument("--quiet");
   if (Trace)
    args.AddArgument("--trace");
   args.AppendArgument(BuildArgs);
   foreach (string t in Targets)
    args.AppendArgument(t);
   return args.ToString();
  }
  protected override string GetProcessBaseDirectory(IIntegrationResult result)
  {
   return result.BaseFromWorkingDirectory(BaseDirectory);
  }
  protected override int GetProcessTimeout()
  {
   return BuildTimeoutSeconds*1000;
  }
  protected override string GetProcessFilename()
  {
   return Executable;
  }
        protected override ProcessPriorityClass GetProcessPriorityClass()
        {
            return this.Priority;
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
