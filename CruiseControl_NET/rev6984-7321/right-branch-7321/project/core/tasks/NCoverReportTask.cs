using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Text;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("ncoverReport")]
    public class NCoverReportTask
        : BaseExecutableTask
    {
        private const string defaultExecutable = "NCover.Reporting";
        private string rootPath;
        public NCoverReportTask()
            : this(new ProcessExecutor())
        {
        }
        public NCoverReportTask(ProcessExecutor executor)
        {
            this.executor = executor;
            this.XmlReportFilter = NCoverReportFilter.Default;
            this.TimeOut = 600;
            this.NumberToReport = -1;
            this.SortBy = NCoverSortBy.None;
            this.MergeMode = NCoverMergeMode.Default;
        }
        [ReflectorProperty("executable", Required = false)]
        public string Executable { get; set; }
        [ReflectorProperty("timeout", Required = false)]
        public int TimeOut { get; set; }
        [ReflectorProperty("baseDir", Required = false)]
        public string BaseDirectory { get; set; }
        [ReflectorProperty("workingDir", Required = false)]
        public string WorkingDirectory { get; set; }
        [ReflectorProperty("priority", Required = false)]
        public ProcessPriorityClass Priority = ProcessPriorityClass.Normal;
        [ReflectorProperty("coverageFile", Required = false)]
        public string CoverageFile { get; set; }
        [ReflectorProperty("clearFilters", Required = false)]
        public bool ClearCoverageFilters { get; set; }
        [ReflectorProperty("filters", Required = false)]
        public CoverageFilter[] CoverageFilters { get; set; }
        [ReflectorProperty("minimumThresholds", Required = false)]
        public CoverageThreshold[] MinimumThresholds { get; set; }
        [ReflectorProperty("minimumCoverage", Required = false)]
        public bool UseMinimumCoverage { get; set; }
        [ReflectorProperty("xmlReportFilter", Required = false)]
        public NCoverReportFilter XmlReportFilter { get; set; }
        [ReflectorProperty("satisfactory", Required = false)]
        public CoverageThreshold[] SatisfactoryThresholds { get; set; }
        [ReflectorProperty("numberToReport", Required = false)]
        public int NumberToReport { get; set; }
        [ReflectorProperty("trendOutput", Required = false)]
        public string TrendOutputFile { get; set; }
        [ReflectorProperty("trendInput", Required = false)]
        public string TrendInputFile { get; set; }
        [ReflectorProperty("buildId", Required = false)]
        public string BuildId { get; set; }
        [ReflectorProperty("hide", Required = false)]
        public string HideElements { get; set; }
        [ReflectorProperty("outputDir", Required = false)]
        public string OutputDir { get; set; }
        [ReflectorProperty("reports", Required = false)]
        public NCoverReportType[] Reports { get; set; }
        [ReflectorProperty("projectName", Required = false)]
        public string ProjectName { get; set; }
        [ReflectorProperty("sortBy", Required = false)]
        public NCoverSortBy SortBy { get; set; }
        [ReflectorProperty("uncoveredAmount", Required = false)]
        public int TopUncoveredAmount { get; set; }
        [ReflectorProperty("mergeMode", Required = false)]
        public NCoverMergeMode MergeMode { get; set; }
        [ReflectorProperty("mergeFile", Required = false)]
        public string MergeFile { get; set; }
        protected override bool Execute(IIntegrationResult result)
        {
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : "Running NCover reporting");
            rootPath = BaseDirectory;
            if (string.IsNullOrEmpty(rootPath)) rootPath = result.WorkingDirectory;
            var outputDirectory = new DirectoryInfo(RootPath(OutputDir, false));
            var oldFiles = GenerateOriginalFileList(outputDirectory);
   var processResult = TryToRun(CreateProcessInfo(result), result);
            result.AddTaskResult(new ProcessTaskResult(processResult));
            if (!processResult.Failed)
            {
                outputDirectory.Refresh();
                var newFiles = ListFileDifferences(oldFiles, outputDirectory);
                if (newFiles.Length > 0)
                {
                    var publishDir = Path.Combine(result.BaseFromArtifactsDirectory(result.Label), "NCover");
                    Log.Debug(string.Format("Copying {0} files to {1}", newFiles.Length, publishDir));
                    var index = outputDirectory.FullName.Length + 1;
                    foreach (FileInfo newFile in newFiles)
                    {
                        var fileInfo = new FileInfo(Path.Combine(publishDir, newFile.FullName.Substring(index)));
                        if (!fileInfo.Directory.Exists) fileInfo.Directory.Create();
                        newFile.CopyTo(fileInfo.FullName, true);
                    }
                }
            }
            return !processResult.Failed;
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
            var coverageFile = string.IsNullOrEmpty(CoverageFile) ? "coverage.xml" : CoverageFile;
            buffer.AppendArgument(RootPath(coverageFile, true));
            buffer.AppendIf(ClearCoverageFilters, "//ccf");
            foreach (var filter in CoverageFilters ?? new CoverageFilter[0])
            {
                buffer.AppendArgument("//cf {0}", filter.ToParamString());
            }
            foreach (var threshold in MinimumThresholds ?? new CoverageThreshold[0])
            {
                buffer.AppendArgument("//mc {0}", threshold.ToParamString());
            }
            buffer.AppendIf(UseMinimumCoverage, "//mcsc");
            buffer.AppendIf(XmlReportFilter != NCoverReportFilter.Default, "//rdf {0}", XmlReportFilter.ToString());
            foreach (var threshold in SatisfactoryThresholds ?? new CoverageThreshold[0])
            {
                buffer.AppendArgument("//sct {0}", threshold.ToParamString());
            }
            buffer.AppendIf(NumberToReport > 0, "//smf {0}", NumberToReport.ToString());
            buffer.AppendIf(!string.IsNullOrEmpty(TrendOutputFile), "//at \"{0}\"", RootPath(TrendOutputFile, false));
            buffer.AppendArgument("//bi \"{0}\"", string.IsNullOrEmpty(BuildId) ? result.Label : BuildId);
            buffer.AppendIf(!string.IsNullOrEmpty(HideElements), "//hi \"{0}\"", HideElements);
            buffer.AppendIf(!string.IsNullOrEmpty(TrendInputFile), "//lt \"{0}\"", RootPath(TrendInputFile, false));
            GenerateReportList(buffer);
            buffer.AppendIf(!string.IsNullOrEmpty(ProjectName), "//p \"{0}\"", ProjectName);
            buffer.AppendIf(SortBy != NCoverSortBy.None, "//so \"{0}\"", SortBy.ToString());
            buffer.AppendIf(TopUncoveredAmount > 0, "//tu \"{0}\"", TopUncoveredAmount.ToString());
            buffer.AppendIf(MergeMode != NCoverMergeMode.Default, "//mfm \"{0}\"", MergeMode.ToString());
            buffer.AppendIf(!string.IsNullOrEmpty(MergeFile), "//s \"{0}\"", RootPath(MergeFile, false));
            buffer.AppendIf(!string.IsNullOrEmpty(WorkingDirectory), "//w \"{0}\"", RootPath(WorkingDirectory, false));
            return buffer.ToString();
        }
        protected override ProcessPriorityClass GetProcessPriorityClass()
        {
            return this.Priority;
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
        private FileInfo[] ListFileDifferences(Dictionary<string, DateTime> originalList, DirectoryInfo outputDirectory)
        {
            FileInfo[] newList = { };
            var index = 0;
            if (outputDirectory.Exists)
            {
                index = outputDirectory.FullName.Length;
                newList = outputDirectory.GetFiles("*.*", SearchOption.AllDirectories);
            }
            List<FileInfo> differenceList = new List<FileInfo>();
            foreach (FileInfo newFile in newList)
            {
                var filename = newFile.FullName.Substring(index);
                if (originalList.ContainsKey(filename))
                {
                    if (originalList[filename] != newFile.LastWriteTime)
                    {
                        differenceList.Add(newFile);
                    }
                }
                else
                {
                    differenceList.Add(newFile);
                }
            }
            return differenceList.ToArray();
        }
        private Dictionary<string, DateTime> GenerateOriginalFileList(DirectoryInfo outputDirectory)
        {
            Dictionary<string, DateTime> originalFiles = new Dictionary<string, DateTime>();
            if (outputDirectory.Exists)
            {
                FileInfo[] oldFiles = outputDirectory.GetFiles("*.*", SearchOption.AllDirectories);
                var index = outputDirectory.FullName.Length;
                foreach (FileInfo oldFile in oldFiles)
                {
                    var filename = oldFile.FullName.Substring(index);
                    originalFiles.Add(filename, oldFile.LastWriteTime);
                }
            }
            return originalFiles;
        }
        private void GenerateReportList(ProcessArgumentBuilder buffer)
        {
            var reportList = new List<NCoverReportType>();
            if ((Reports != null) && (Reports.Length > 0))
            {
                reportList.AddRange(Reports);
            }
            else
            {
                reportList.Add(NCoverReportType.FullCoverageReport);
            }
            foreach (var report in reportList)
            {
                var path = OutputDir;
                if (report == NCoverReportType.FullCoverageReport)
                {
                    path = RootPath(path, false);
                }
                else
                {
                    path = RootPath(string.Format("{0}.html", report), false);
                }
                buffer.AppendArgument("//or \"{0}\"", string.Format("{0}:Html:{1}", report, path));
            }
        }
        public enum NCoverReportFilter
        {
            Default,
            Assembly,
            Namespace
        }
        public enum NCoverReportType
        {
            FullCoverageReport,
            Summary,
            UncoveredCodeSections,
            SymbolSourceCode,
            SymbolSourceCodeClass,
            SymbolSourceCodeClassMethod,
            MethodSourceCode,
            MethodSourceCodeClass,
            MethodSourceCodeClassMethod,
            SymbolModule,
            SymbolModuleNamespace,
            SymbolModuleNamespaceClass,
            SymbolModuleNamespaceClassMethod,
            SymbolCCModuleClassFailedCoverageTop,
            SymbolCCModuleClassCoverageTop,
            MethodModuleNamespaceClass,
            MethodModuleNamespaceClassMethod,
            MethodCCModuleClassFailedCoverageTop,
            MethodCCModuleClassCoverageTop
        }
        public enum NCoverSortBy
        {
            None,
            Name,
            ClassLine,
            CoveragePercentageAscending,
            CoveragePercentageDescending,
            UnvisitedSequencePointsAscending,
            UnvisitedSequencePointsDescending,
            VisitCountAscending,
            VisitCountDescending,
            FunctionCoverageAscending,
            FunctionCoverageDescending
        }
        public enum NCoverMergeMode
        {
            Default,
            KeepSourceFilters,
            Destructive,
            AppendFilters
        }
    }
}
