using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
using System.IO;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("ndepend")]
    public class NDependTask
        : BaseExecutableTask
    {
        private const string defaultExecutable = "NDepend.Console";
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
            TimeOut = 600;
            Publish = true;
        }
        [ReflectorProperty("project")]
        public string ProjectFile { get; set; }
        [ReflectorProperty("executable", Required = false)]
        public string Executable { get; set; }
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
            var isSuccessful = false;
            using (var stdOut = this.Context.CreateResultStream("stdout", "data/xml"))
            {
                using (var stdErr = this.Context.CreateResultStream("stderr", "data/xml"))
                {
                    var info = CreateProcessInfo(result);
                    var processResult = TryToRun(info, result, stdOut, stdErr, true);
                    result.AddTaskResult(new ProcessTaskResult(processResult));
                    if (processResult.TimedOut)
                    {
                        throw new BuilderException(this, "NDepend timed out (after " + this.TimeOut + " seconds)");
                    }
                    isSuccessful = !processResult.Failed;
                }
            }
            if (Publish && isSuccessful)
            {
                var newFiles = ListFileDifferences(oldFiles, outputDirectory);
                if (newFiles.Length > 0)
                {
                    logger.Debug("Copying {0} new file(s)", newFiles.Length);
                    foreach (var newFile in newFiles)
                    {
                        this.Context.ImportResultFile(newFile, Path.GetFileName(newFile), "data/unknown", true);
                    }
                }
            }
            return isSuccessful;
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
            string path = StringUtil.AutoDoubleQuoteString(rootPath);
            return path;
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
