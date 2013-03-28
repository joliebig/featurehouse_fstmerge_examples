using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
using System.IO;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("ncoverProfile")]
    public class NCoverProfileTask
        : BaseExecutableTask
    {
        private const string defaultExecutable = "NCover.Console";
        private string rootPath;
        public NCoverProfileTask()
            : this(new ProcessExecutor())
        {
        }
        public NCoverProfileTask(ProcessExecutor executor)
        {
            this.executor = executor;
            this.Publish = true;
            this.TimeOut = 600;
            this.LogLevel = NCoverLogLevel.Default;
        }
        [ReflectorProperty("program", Required = false)]
        public string ProgramToCover { get; set; }
        [ReflectorProperty("testProject", Required = false)]
        public string TestProject { get; set; }
        [ReflectorProperty("programParameters", Required = false)]
        public string ProgramParameters { get; set; }
        [ReflectorProperty("executable", Required = false)]
        public string Executable { get; set; }
        [ReflectorProperty("description", Required = false)]
        public string Description { get; set; }
        [ReflectorProperty("timeout", Required = false)]
        public int TimeOut { get; set; }
        [ReflectorProperty("baseDir", Required = false)]
        public string BaseDirectory { get; set; }
        [ReflectorProperty("workingDir", Required = false)]
        public string WorkingDirectory { get; set; }
        [ReflectorProperty("publish", Required = false)]
        public bool Publish { get; set; }
        [ReflectorProperty("logFile", Required = false)]
        public string LogFile { get; set; }
        [ReflectorProperty("logLevel", Required = false)]
        public NCoverLogLevel LogLevel { get; set; }
        [ReflectorProperty("projectName", Required = false)]
        public string ProjectName { get; set; }
        [ReflectorProperty("coverageFile", Required = false)]
        public string CoverageFile { get; set; }
        [ReflectorProperty("coverageMetric", Required = false)]
        public string CoverageMetric { get; set; }
        [ReflectorProperty("excludedAttributes", Required = false)]
        public string ExcludedAttributes { get; set; }
        [ReflectorProperty("excludedAssemblies", Required = false)]
        public string ExcludedAssemblies { get; set; }
        [ReflectorProperty("excludedFiles", Required = false)]
        public string ExcludedFiles { get; set; }
        [ReflectorProperty("excludedMethods", Required = false)]
        public string ExcludedMethods { get; set; }
        [ReflectorProperty("excludedTypes", Required = false)]
        public string ExcludedTypes { get; set; }
        [ReflectorProperty("includedAttributes", Required = false)]
        public string IncludedAttributes { get; set; }
        [ReflectorProperty("includedAssemblies", Required = false)]
        public string IncludedAssemblies { get; set; }
        [ReflectorProperty("includedFiles", Required = false)]
        public string IncludedFiles { get; set; }
        [ReflectorProperty("includedTypes", Required = false)]
        public string IncludedTypes { get; set; }
        [ReflectorProperty("disableAutoexclusion", Required = false)]
        public bool DisableAutoexclusion { get; set; }
        [ReflectorProperty("processModule", Required = false)]
        public string ProcessModule { get; set; }
        [ReflectorProperty("symbolSearch", Required = false)]
        public string SymbolSearch { get; set; }
        [ReflectorProperty("trendFile", Required = false)]
        public string TrendFile { get; set; }
        [ReflectorProperty("buildId", Required = false)]
        public string BuildId { get; set; }
        [ReflectorProperty("settingsFile", Required = false)]
        public string SettingsFile { get; set; }
        [ReflectorProperty("register", Required = false)]
        public bool Register { get; set; }
        [ReflectorProperty("applicationLoadWait", Required = false)]
        public int ApplicationLoadWait { get; set; }
        [ReflectorProperty("iis", Required = false)]
        public bool CoverIis { get; set; }
        [ReflectorProperty("serviceTimeout", Required = false)]
        public int ServiceTimeout { get; set; }
        [ReflectorProperty("windowsService", Required = false)]
        public string WindowsService { get; set; }
        public override void Run(IIntegrationResult result)
        {
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : "Running NCover profile");
            rootPath = BaseDirectory;
            if (string.IsNullOrEmpty(rootPath)) rootPath = result.WorkingDirectory;
            var processResult = TryToRun(CreateProcessInfo(result));
            result.AddTaskResult(new ProcessTaskResult(processResult));
            if (Publish)
            {
                var coverageFile = string.IsNullOrEmpty(CoverageFile) ? "coverage.xml" : CoverageFile;
                result.AddTaskResult(new FileTaskResult(RootPath(coverageFile, false)));
            }
        }
        protected override string GetProcessFilename()
        {
            string path;
            if (string.IsNullOrEmpty(Executable))
            {
                path = RootPath(defaultExecutable, true);
            }
            else
            {
                path = RootPath(Executable, true);
            }
            return path;
        }
        protected override string GetProcessBaseDirectory(IIntegrationResult result)
        {
            string path = string.IsNullOrEmpty(WorkingDirectory) ? RootPath(rootPath, true) : RootPath(WorkingDirectory, true);
            return path;
        }
        protected override int GetProcessTimeout()
        {
            return TimeOut * 1000;
        }
        protected override string GetProcessArguments(IIntegrationResult result)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.Append(RootPath(ProgramToCover, true));
            if (!string.IsNullOrEmpty(TestProject))
            {
                string testProject;
                if (!string.IsNullOrEmpty(WorkingDirectory))
                {
                    testProject = Path.Combine(RootPath(WorkingDirectory, false), TestProject);
                    testProject = StringUtil.AutoDoubleQuoteString(testProject);
                }
                else
                {
                    testProject = RootPath(TestProject, true);
                }
                buffer.AppendArgument(testProject);
            }
            buffer.AppendArgument(ProgramParameters);
            buffer.AppendIf(!string.IsNullOrEmpty(LogFile), "//l \"{0}\"", RootPath(LogFile, false));
            buffer.AppendIf(LogLevel != NCoverLogLevel.Default, "//ll {0}", LogLevel.ToString());
            buffer.AppendIf(!string.IsNullOrEmpty(ProjectName), "//p \"{0}\"", ProjectName);
            buffer.AppendIf(!string.IsNullOrEmpty(CoverageFile), "//x \"{0}\"", RootPath(CoverageFile, false));
            buffer.AppendIf(string.IsNullOrEmpty(CoverageFile), "//x \"{0}\"", RootPath("Coverage.xml", false));
            buffer.AppendIf(!string.IsNullOrEmpty(CoverageMetric), "//ct \"{0}\"", CoverageMetric);
            buffer.AppendIf(!string.IsNullOrEmpty(ExcludedAttributes), "//ea \"{0}\"", ExcludedAttributes);
            buffer.AppendIf(!string.IsNullOrEmpty(ExcludedAssemblies), "//eas \"{0}\"", ExcludedAssemblies);
            buffer.AppendIf(!string.IsNullOrEmpty(ExcludedFiles), "//ef \"{0}\"", ExcludedFiles);
            buffer.AppendIf(!string.IsNullOrEmpty(ExcludedMethods), "//em \"{0}\"", ExcludedMethods);
            buffer.AppendIf(!string.IsNullOrEmpty(ExcludedTypes), "//et \"{0}\"", ExcludedTypes);
            buffer.AppendIf(!string.IsNullOrEmpty(IncludedAttributes), "//ia \"{0}\"", IncludedAttributes);
            buffer.AppendIf(!string.IsNullOrEmpty(IncludedAssemblies), "//ias \"{0}\"", IncludedAssemblies);
            buffer.AppendIf(!string.IsNullOrEmpty(IncludedFiles), "//if \"{0}\"", IncludedFiles);
            buffer.AppendIf(!string.IsNullOrEmpty(IncludedTypes), "//it \"{0}\"", IncludedTypes);
            buffer.AppendIf(DisableAutoexclusion, "//na");
            buffer.AppendIf(!string.IsNullOrEmpty(ProcessModule), "//pm \"{0}\"", ProcessModule);
            buffer.AppendIf(!string.IsNullOrEmpty(SymbolSearch), "//ssp \"{0}\"", SymbolSearch);
            buffer.AppendIf(!string.IsNullOrEmpty(TrendFile), "//at \"{0}\"", RootPath(TrendFile, false));
            buffer.AppendArgument("//bi \"{0}\"", !string.IsNullOrEmpty(BuildId) ? BuildId : result.Label);
            buffer.AppendIf(!string.IsNullOrEmpty(SettingsFile), "//cr \"{0}\"", RootPath(SettingsFile, false));
            buffer.AppendIf(Register, "//reg");
            buffer.AppendIf(!string.IsNullOrEmpty(WorkingDirectory), "//w \"{0}\"", RootPath(WorkingDirectory, false));
            buffer.AppendIf(ApplicationLoadWait > 0, "//wal {0}", ApplicationLoadWait.ToString());
            buffer.AppendIf(CoverIis, "//iis");
            buffer.AppendIf(ServiceTimeout > 0, "//st {0}", ServiceTimeout.ToString());
            buffer.AppendIf(!string.IsNullOrEmpty(WindowsService), "//svc {0}", WindowsService);
            return buffer.ToString();
        }
        private string RootPath(string path, bool doubleQuote)
        {
            string actualPath;
            if (Path.IsPathRooted(path))
            {
                actualPath = path;
            }
            else
            {
                if (string.IsNullOrEmpty(path))
                {
                    actualPath = Path.Combine(rootPath, "NCoverResults");
                }
                else
                {
                    actualPath = Path.Combine(rootPath, path);
                }
            }
            if (doubleQuote) actualPath = StringUtil.AutoDoubleQuoteString(actualPath);
            return actualPath;
        }
        public enum NCoverLogLevel
        {
            Default,
            None,
            Normal,
            Verbose
        }
    }
}
