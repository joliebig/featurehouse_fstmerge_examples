using System.Diagnostics;
using System.IO;
using System.Text;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
 [ReflectorType("gendarme")]
 public class GendarmeTask : BaseExecutableTask
 {
  public const string defaultExecutable = "gendarme";
  public const string logFilename = "gendarme-results.xml";
  public const int defaultLimit = -1;
  public const bool defaultQuiet = false;
  public const bool defaultVerbose = false;
  public const bool defaultFailBuildOnFoundDefects = false;
  public const int defaultVerifyTimeout = 0;
        public const ProcessPriorityClass defaultPriority = ProcessPriorityClass.Normal;
        private readonly IFileDirectoryDeleter fileDirectoryDeleter = new IoService();
  public GendarmeTask():
   this(new ProcessExecutor()){}
  public GendarmeTask(ProcessExecutor executor)
  {
   this.executor = executor;
            this.Assemblies = new AssemblyMatch[0];
            this.AssemblyListFile = string.Empty;
            this.VerifyTimeoutSeconds = defaultVerifyTimeout;
            this.Executable = defaultExecutable;
            this.ConfiguredBaseDirectory = string.Empty;
            this.Priority = defaultPriority;
            this.ConfigFile = string.Empty;
            this.RuleSet = string.Empty;
            this.IgnoreFile = string.Empty;
            this.Limit = defaultLimit;
            this.Severity = string.Empty;
            this.Confidence = string.Empty;
            this.Quiet = defaultQuiet;
            this.Verbose = defaultVerbose;
            this.FailBuildOnFoundDefects = defaultFailBuildOnFoundDefects;
  }
        [ReflectorProperty("executable", Required = false)]
        public string Executable { get; set; }
        [ReflectorProperty("baseDirectory", Required = false)]
        public string ConfiguredBaseDirectory { get; set; }
        [ReflectorProperty("priority", Required = false)]
        public ProcessPriorityClass Priority { get; set; }
        [ReflectorProperty("configFile", Required = false)]
        public string ConfigFile { get; set; }
        [ReflectorProperty("ruleSet", Required = false)]
        public string RuleSet { get; set; }
        [ReflectorProperty("ignoreFile", Required = false)]
        public string IgnoreFile { get; set; }
        [ReflectorProperty("limit", Required = false)]
        public int Limit { get; set; }
        [ReflectorProperty("severity", Required = false)]
        public string Severity { get; set; }
        [ReflectorProperty("confidence", Required = false)]
        public string Confidence { get; set; }
        [ReflectorProperty("quiet", Required = false)]
        public bool Quiet { get; set; }
        [ReflectorProperty("verbose", Required = false)]
        public bool Verbose { get; set; }
        [ReflectorProperty("failBuildOnFoundDefects", Required = false)]
        public bool FailBuildOnFoundDefects { get; set; }
        [ReflectorProperty("assemblies", Required = false)]
        public AssemblyMatch[] Assemblies { get; set; }
        [ReflectorProperty("assemblyListFile", Required = false)]
        public string AssemblyListFile { get; set; }
        [ReflectorProperty("verifyTimeoutSeconds", Required = false)]
        public int VerifyTimeoutSeconds { get; set; }
  protected override string GetProcessFilename()
  {
   return Executable;
  }
  protected override string GetProcessArguments(IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendIf(!string.IsNullOrEmpty(ConfigFile), "--config {0}", StringUtil.AutoDoubleQuoteString(ConfigFile));
   buffer.AppendIf(!string.IsNullOrEmpty(RuleSet), "--set {0}", RuleSet);
   buffer.AppendIf(!string.IsNullOrEmpty(IgnoreFile), "--ignore {0}", StringUtil.AutoDoubleQuoteString(IgnoreFile));
   buffer.AppendIf(Limit > 0, "--limit {0}", Limit.ToString());
   buffer.AppendIf(!string.IsNullOrEmpty(Severity), "--severity {0}", Severity);
   buffer.AppendIf(!string.IsNullOrEmpty(Confidence), "--confidence {0}", Confidence);
   buffer.AppendIf(Quiet, "--quiet");
   buffer.AppendIf(Verbose, "--verbose");
   buffer.AppendArgument("--xml {0}", StringUtil.AutoDoubleQuoteString(GetGendarmeOutputFile(result)));
   CreateAssemblyList(buffer);
   return buffer.ToString();
  }
  protected override string GetProcessBaseDirectory(IIntegrationResult result)
  {
   return result.BaseFromWorkingDirectory(ConfiguredBaseDirectory);
  }
  protected override int GetProcessTimeout()
  {
   return VerifyTimeoutSeconds * 1000;
  }
        protected override ProcessPriorityClass GetProcessPriorityClass()
        {
            return this.Priority;
        }
        protected override bool Execute(IIntegrationResult result)
  {
            string gendarmeOutputFile = GetGendarmeOutputFile(result);
            fileDirectoryDeleter.DeleteIncludingReadOnlyObjects(gendarmeOutputFile);
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description :
    "Executing Gendarme to verifiy assemblies.");
   ProcessResult processResult = TryToRun(CreateProcessInfo(result), result);
            if (File.Exists(gendarmeOutputFile))
            {
                result.AddTaskResult(new FileTaskResult(gendarmeOutputFile));
            }
            result.AddTaskResult(new ProcessTaskResult(processResult, true));
   if (processResult.TimedOut)
    throw new BuilderException(this, string.Concat("Gendarme process timed out (after ", VerifyTimeoutSeconds, " seconds)"));
            return !processResult.Failed;
  }
  protected override int[] GetProcessSuccessCodes()
  {
   if (FailBuildOnFoundDefects)
    return new int[] {0};
   return new int[] {0, 1};
  }
  private static string GetGendarmeOutputFile(IIntegrationResult result)
  {
   return Path.Combine(result.ArtifactDirectory, logFilename);
  }
  private void CreateAssemblyList(ProcessArgumentBuilder buffer)
  {
   if (string.IsNullOrEmpty(AssemblyListFile) && (Assemblies == null || Assemblies.Length == 0))
    throw new ConfigurationException("[GendarmeTask] Neither 'assemblyListFile' nor 'assemblies' are specified. Please specify one of them.");
   if (!string.IsNullOrEmpty(AssemblyListFile))
    buffer.AppendArgument(string.Concat("@", StringUtil.AutoDoubleQuoteString(AssemblyListFile)));
   foreach (AssemblyMatch asm in Assemblies)
    buffer.AppendArgument(asm.Expression);
  }
 }
}
