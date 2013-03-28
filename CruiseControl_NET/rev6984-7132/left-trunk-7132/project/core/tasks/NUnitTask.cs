namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System.Diagnostics;
    using System.IO;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("nunit")]
 public class NUnitTask
        : TaskBase
 {
  public const string DefaultPath = @"nunit-console";
  public const int DefaultTimeout = 600;
  private const string DefaultOutputFile = "nunit-results.xml";
  private readonly ProcessExecutor executor;
  public NUnitTask() : this(new ProcessExecutor())
  {}
  public NUnitTask(ProcessExecutor exec)
  {
   executor = exec;
            this.Assemblies = new string[0];
            this.NUnitPath = DefaultPath;
            this.OutputFile = DefaultOutputFile;
            this.Timeout = DefaultTimeout;
            this.Priority = ProcessPriorityClass.Normal;
            this.ExcludedCategories = new string[0];
            this.IncludedCategories = new string[0];
        }
        [ReflectorProperty("assemblies")]
        public string[] Assemblies { get; set; }
        [ReflectorProperty("path", Required = false)]
        public string NUnitPath { get; set; }
        [ReflectorProperty("outputfile", Required = false)]
        public string OutputFile { get; set; }
        [ReflectorProperty("timeout", Required = false)]
        public int Timeout { get; set; }
        [ReflectorProperty("priority", Required = false)]
        public ProcessPriorityClass Priority { get; set; }
        [ReflectorProperty("excludedCategories", Required = false)]
        public string[] ExcludedCategories { get; set; }
        [ReflectorProperty("includedCategories", Required = false)]
        public string[] IncludedCategories { get; set; }
        protected override bool Execute(IIntegrationResult result)
  {
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : "Executing NUnit");
   string outputFile = result.BaseFromArtifactsDirectory(OutputFile);
   ProcessResult nunitResult = executor.Execute(NewProcessInfo(outputFile, result));
      result.AddTaskResult(new ProcessTaskResult(nunitResult, true));
   if (File.Exists(outputFile))
   {
    result.AddTaskResult(new FileTaskResult(outputFile));
   }
   else
   {
    Log.Warning(string.Format("NUnit test output file {0} was not created", outputFile));
   }
            return !nunitResult.Failed;
  }
  private ProcessInfo NewProcessInfo(string outputFile, IIntegrationResult result)
  {
            NUnitArgument nunitArgument = new NUnitArgument(Assemblies, outputFile);
            nunitArgument.ExcludedCategories = ExcludedCategories;
            nunitArgument.IncludedCategories = IncludedCategories;
            string args = nunitArgument.ToString();
   Log.Debug(string.Format("Running unit tests: {0} {1}", NUnitPath, args));
   ProcessInfo info = new ProcessInfo(NUnitPath, args, result.WorkingDirectory, Priority);
   info.TimeOut = Timeout * 1000;
   return info;
  }
 }
}
