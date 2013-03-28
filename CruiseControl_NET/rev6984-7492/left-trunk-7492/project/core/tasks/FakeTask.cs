using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("fake")]
    public class FakeTask : BaseExecutableTask
    {
        public const string defaultExecutable = "FAKE.exe";
        public const int DefaultBuildTimeout = 600;
        public const string logFilename = "fake-results-{0}.xml";
        public readonly Guid LogFileId = Guid.NewGuid();
        public const ProcessPriorityClass DefaultPriority = ProcessPriorityClass.Normal;
        private readonly IFileDirectoryDeleter fileDirectoryDeleter = new IoService();
        [ReflectorProperty("executable", Required = false)]
        public string Executable { get; set; }
        [ReflectorProperty("baseDirectory", Required = false)]
        public string ConfiguredBaseDirectory { get; set; }
        [ReflectorProperty("priority", Required = false)]
        public ProcessPriorityClass Priority { get; set; }
        [ReflectorProperty("buildTimeoutSeconds", Required = false)]
        public int BuildTimeoutSeconds { get; set; }
        [ReflectorProperty("buildFile", Required = false)]
        public string BuildFile { get; set; }
        public FakeTask():
   this(new ProcessExecutor()){}
        public FakeTask(ProcessExecutor executor)
  {
   this.executor = executor;
            Executable = defaultExecutable;
            ConfiguredBaseDirectory = string.Empty;
            Priority = DefaultPriority;
            BuildTimeoutSeconds = DefaultBuildTimeout;
            BuildFile = string.Empty;
        }
        protected override bool Execute(IIntegrationResult result)
        {
            var fakeOutputFile = GetFakeOutputFile(result);
            fileDirectoryDeleter.DeleteIncludingReadOnlyObjects(fakeOutputFile);
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description :
                string.Format("Executing FAKE - {0}", ToString()));
            var processResult = TryToRun(CreateProcessInfo(result), result);
            if (File.Exists(fakeOutputFile))
                result.AddTaskResult(new FileTaskResult(fakeOutputFile));
            result.AddTaskResult(new ProcessTaskResult(processResult, true));
            if (processResult.TimedOut)
                throw new BuilderException(this, string.Concat("FAKE process timed out (after ", BuildTimeoutSeconds, " seconds)"));
            return !processResult.Failed;
        }
        protected override string GetProcessFilename()
        {
            return Executable;
        }
        protected override string GetProcessArguments(IIntegrationResult result)
        {
            var buffer = new ProcessArgumentBuilder();
            buffer.AppendArgument(StringUtil.AutoDoubleQuoteString(BuildFile));
            buffer.AppendArgument("logfile={0}", StringUtil.AutoDoubleQuoteString(GetFakeOutputFile(result)));
            AppendIntegrationResultProperties(buffer, result);
            return buffer.ToString();
        }
        protected override string GetProcessBaseDirectory(IIntegrationResult result)
        {
            return result.BaseFromWorkingDirectory(ConfiguredBaseDirectory);
        }
        protected override ProcessPriorityClass GetProcessPriorityClass()
        {
            return Priority;
        }
        protected override int GetProcessTimeout()
        {
            return BuildTimeoutSeconds * 1000;
        }
        private static void AppendIntegrationResultProperties(ProcessArgumentBuilder buffer, IIntegrationResult result)
        {
            IDictionary properties = result.IntegrationProperties;
            foreach (string key in properties.Keys)
            {
                object value = result.IntegrationProperties[key];
                if (value != null)
                    buffer.AppendArgument(string.Format("{0}={1}", key, StringUtil.AutoDoubleQuoteString(StringUtil.RemoveTrailingPathDelimeter(StringUtil.IntegrationPropertyToString(value)))));
            }
        }
        public override string ToString()
        {
            string baseDirectory = ConfiguredBaseDirectory ?? string.Empty;
            return string.Format(@" BaseDirectory: {0}, Executable: {1}, BuildFile: {2}", baseDirectory, Executable, BuildFile);
        }
        private string GetFakeOutputFile(IIntegrationResult result)
        {
            return Path.Combine(result.ArtifactDirectory, string.Format(logFilename, LogFileId));
        }
    }
}
