namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.IO;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("ndepend")]
    public class NDependTask
        : BaseExecutableTask
    {
        private const string defaultExecutable = "NDepend.Console";
        private const ProcessPriorityClass DefaultPriority = ProcessPriorityClass.Normal;
        private string rootPath;
        private IFileSystem fileSystem;
        private ILogger logger;
        public NDependTask()
            : this(new ProcessExecutor(), new SystemIoFileSystem(), new DefaultLogger())
        {
        }
        public NDependTask(ProcessExecutor executor, IFileSystem fileSystem, ILogger logger)
        {
            this.executor = executor;
            this.fileSystem = fileSystem;
            this.logger = logger;
            this.TimeOut = 600;
            this.Publish = true;
            this.Priority = ProcessPriorityClass.Normal;
        }
        [ReflectorProperty("project")]
        public string ProjectFile { get; set; }
        [ReflectorProperty("executable", Required = false)]
        public string Executable { get; set; }
        [ReflectorProperty("priority", Required = false)]
        public ProcessPriorityClass Priority { get; set; }
        [ReflectorProperty("emitXml", Required = false)]
        public bool EmitXml { get; set; }
        [ReflectorProperty("outputDir", Required = false)]
        public string OutputDir { get; set; }
        [ReflectorProperty("inputDirs", Required = false)]
        public string[] InputDirs { get; set; }
        [ReflectorProperty("silent", Required = false)]
        public bool Silent { get; set; }
        [ReflectorProperty("reportXslt", Required = false)]
        public string ReportXslt { get; set; }
        [ReflectorProperty("timeout", Required = false)]
        public int TimeOut { get; set; }
        [ReflectorProperty("baseDir", Required = false)]
        public string BaseDirectory { get; set; }
        [ReflectorProperty("publish", Required = false)]
        public bool Publish { get; set; }
        public IFileSystem FileSystem
        {
            get { return fileSystem; }
        }
        public ILogger Logger
        {
            get { return logger; }
        }
        protected override bool Execute(IIntegrationResult result)
        {
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : "Executing NDepend");
            rootPath = BaseDirectory;
            if (string.IsNullOrEmpty(rootPath)) rootPath = result.WorkingDirectory;
            var outputDirectory = RootPath(OutputDir, false);
            var oldFiles = GenerateOriginalFileList(outputDirectory);
   var processResult = TryToRun(CreateProcessInfo(result), result);
            result.AddTaskResult(new ProcessTaskResult(processResult, true));
            if (Publish && !processResult.Failed)
            {
                var newFiles = ListFileDifferences(oldFiles, outputDirectory);
                if (newFiles.Length > 0)
                {
                    logger.Debug("Copying {0} new file(s)", newFiles.Length);
                    var publishDir = Path.Combine(result.BaseFromArtifactsDirectory(result.Label), "NDepend");
                    fileSystem.EnsureFolderExists(publishDir);
                    foreach (var newFile in newFiles)
                    {
                        fileSystem.Copy(newFile,
                            Path.Combine(publishDir,
                                Path.GetFileName(newFile)));
                        if (Path.GetExtension(newFile) == ".xml")
                        {
                            result.AddTaskResult(fileSystem.GenerateTaskResultFromFile(newFile));
                        }
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
                path = RootPath(defaultExecutable, false);
            }
            else
            {
                path = RootPath(Executable, false);
            }
            return path;
        }
        protected override string GetProcessBaseDirectory(IIntegrationResult result)
        {
            return rootPath;
        }
        protected override int GetProcessTimeout()
        {
            return TimeOut * 1000;
        }
        protected override string GetProcessArguments(IIntegrationResult result)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.Append(RootPath(ProjectFile, true));
            buffer.AppendIf(Silent, "/Silent");
            buffer.AppendIf(EmitXml, "/EmitVisualNDependBinXml ");
            if ((InputDirs != null) && (InputDirs.Length > 0))
            {
                List<string> dirs = new List<string>();
                foreach (string dir in InputDirs)
                {
                    dirs.Add(RootPath(dir, true));
                }
                buffer.AppendArgument("/InDirs {0}", string.Join(" ", dirs.ToArray()));
            }
            buffer.AppendArgument("/OutDir {0}", RootPath(OutputDir, true));
            if (!string.IsNullOrEmpty(ReportXslt))
            {
                buffer.AppendArgument("/XslForReport  {0}", RootPath(ReportXslt, true));
            }
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
                    actualPath = Path.Combine(rootPath, "NDependResults");
                }
                else
                {
                    actualPath = Path.Combine(rootPath, path);
                }
            }
            if (doubleQuote) actualPath = StringUtil.AutoDoubleQuoteString(actualPath);
            return actualPath;
        }
        private string[] ListFileDifferences(Dictionary<string, DateTime> originalList, string outputDirectory)
        {
            string[] newList = {};
            if (fileSystem.DirectoryExists(outputDirectory)) newList = fileSystem.GetFilesInDirectory(outputDirectory);
            var differenceList = new List<string>();
            foreach (var newFile in newList)
            {
                if (originalList.ContainsKey(newFile))
                {
                    if (originalList[newFile] != fileSystem.GetLastWriteTime(newFile))
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
        private Dictionary<string, DateTime> GenerateOriginalFileList(string outputDirectory)
        {
            var originalFiles = new Dictionary<string, DateTime>();
            if (fileSystem.DirectoryExists(outputDirectory))
            {
                var oldFiles = fileSystem.GetFilesInDirectory(outputDirectory);
                foreach (var oldFile in oldFiles)
                {
                    originalFiles.Add(oldFile, fileSystem.GetLastWriteTime(oldFile));
                }
            }
            return originalFiles;
        }
    }
}
