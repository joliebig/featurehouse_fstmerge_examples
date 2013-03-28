namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System.Diagnostics;
    using System.IO;
    using System.Xml;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("codeItRight")]
    public class CodeItRightTask
        : BaseExecutableTask
    {
        private const string DefaultExecutable = "SubMain.CodeItRight.Cmd";
        public CodeItRightTask()
            : this(new ProcessExecutor())
        {
        }
        public CodeItRightTask(ProcessExecutor executor)
        {
            this.executor = executor;
            this.Executable = CodeItRightTask.DefaultExecutable;
            this.TimeOut = 600;
            this.ReportingThreshold = Severity.None;
            this.FailureThreshold = Severity.None;
            this.Priority = ProcessPriorityClass.Normal;
        }
        [ReflectorProperty("executable", Required = false)]
        public string Executable { get; set; }
        [ReflectorProperty("solution", Required = false)]
        public string Solution { get; set; }
        [ReflectorProperty("project", Required = false)]
        public string Project { get; set; }
        [ReflectorProperty("xsl", Required = false)]
        public string Xsl { get; set; }
        [ReflectorProperty("crData", Required = false)]
        public string CRData { get; set; }
        [ReflectorProperty("profile", Required = false)]
        public string Profile { get; set; }
        [ReflectorProperty("reportingThreshold", Required = false)]
        public Severity ReportingThreshold { get; set; }
        [ReflectorProperty("failureThreshold", Required = false)]
        public Severity FailureThreshold { get; set; }
        [ReflectorProperty("timeout", Required = false)]
        public int TimeOut { get; set; }
        [ReflectorProperty("priority", Required = false)]
        public ProcessPriorityClass Priority { get; set; }
        protected override bool Execute(IIntegrationResult result)
        {
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : "Running CodeItRight analysis");
   var processResult = TryToRun(CreateProcessInfo(result), result);
            processResult = new ProcessResult(
                processResult.StandardOutput,
                processResult.StandardError,
                processResult.ExitCode,
                processResult.TimedOut,
                processResult.ExitCode < 0);
            result.AddTaskResult(new ProcessTaskResult(processResult));
            if (!processResult.Failed)
            {
                var xmlFile = result.BaseFromWorkingDirectory("codeitright.xml");
                result.AddTaskResult(
                    new FileTaskResult(xmlFile, true));
            }
            var failed = processResult.Failed;
            if (!failed && (this.FailureThreshold != Severity.None))
            {
                var xmlFile = result.BaseFromWorkingDirectory("codeitright.xml");
                var document = new XmlDocument();
                if (File.Exists(xmlFile))
                {
                    document.Load(xmlFile);
                    for (var level = (int)Severity.CriticalError; level >= (int)this.FailureThreshold; level--)
                    {
                        failed = CodeItRightTask.CheckReportForSeverity(document, (Severity)level);
                        if (failed)
                        {
                            break;
                        }
                    }
                }
            }
            return !failed;
        }
        protected override string GetProcessFilename()
        {
            return this.Executable;
        }
        protected override string GetProcessBaseDirectory(IIntegrationResult result)
        {
            string path = result.WorkingDirectory;
            return path;
        }
        protected override int GetProcessTimeout()
        {
            return this.TimeOut * 1000;
        }
        protected override string GetProcessArguments(IIntegrationResult result)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.AddArgument("/quiet");
            buffer.AddArgument("/severityThreshold:\"" + this.ReportingThreshold.ToString() + "\"");
            buffer.AddArgument("/out:\"" + result.BaseFromWorkingDirectory("codeitright.xml") + "\"");
            if (!string.IsNullOrEmpty(this.Solution))
            {
                buffer.AddArgument("/Solution:\"" + this.EnsurePathIsRooted(result, this.Solution) + "\"");
            }
            else if (!string.IsNullOrEmpty(this.Project))
            {
                buffer.AddArgument("/Project:\"" + this.EnsurePathIsRooted(result, this.Project) + "\"");
            }
            else
            {
                throw new CruiseControlException("Either a solution or a project must be specified for analysis.");
            }
            if (!string.IsNullOrEmpty(this.Xsl))
            {
                buffer.AddArgument("/outxsl:\"" + this.EnsurePathIsRooted(result, this.Xsl) + "\"");
            }
            if (!string.IsNullOrEmpty(this.CRData))
            {
                buffer.AddArgument("/crdata:\"" + this.EnsurePathIsRooted(result, this.CRData) + "\"");
            }
            if (!string.IsNullOrEmpty(this.Profile))
            {
                buffer.AddArgument("/profile:\"" + this.Profile + "\"");
            }
            return buffer.ToString();
        }
        protected override ProcessPriorityClass GetProcessPriorityClass()
        {
            return this.Priority;
        }
        private static bool CheckReportForSeverity(XmlDocument document, Severity value)
        {
            var nodes = document.SelectNodes("/CodeItRightReport/Violations/Violation[Severity='" + value.ToString() + "']");
            return nodes.Count > 0;
        }
        private string EnsurePathIsRooted(IIntegrationResult result, string path)
        {
            if (!Path.IsPathRooted(path))
            {
                return result.BaseFromWorkingDirectory(path);
            }
            else
            {
                return path;
            }
        }
        public enum Severity
        {
            CriticalError = 5,
            Error = 4,
            CriticalWarning = 3,
            Warning = 2,
            Information = 1,
            None = 0,
        }
    }
}
