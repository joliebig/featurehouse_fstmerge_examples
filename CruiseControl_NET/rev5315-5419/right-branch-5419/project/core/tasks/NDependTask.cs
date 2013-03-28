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
        private string projectFile;
        private string description;
        private string executable;
        private bool emitXml;
        private string[] inputDirs;
        private string outputDir;
        private bool silent;
        private string reportXslt;
        private int timeOut = 600;
        private string baseDirectory;
        private string rootPath;
        private bool publish = true;
        public NDependTask()
            : this(new ProcessExecutor())
        {
        }
        public NDependTask(ProcessExecutor executor)
        {
            this.executor = executor;
        }
        [ReflectorProperty("project")]
        public string ProjectFile
        {
            get { return projectFile; }
            set { projectFile = value; }
        }
        [ReflectorProperty("executable", Required = false)]
        public string Executable
        {
            get { return executable; }
            set { executable = value; }
        }
        [ReflectorProperty("description", Required = false)]
        public string Description
        {
            get { return description; }
            set { description = value; }
        }
        [ReflectorProperty("emitXml", Required = false)]
        public bool EmitXml
        {
            get { return emitXml; }
            set { emitXml = value; }
        }
        [ReflectorProperty("outputDir", Required = false)]
        public string OutputDir
        {
            get { return outputDir; }
            set { outputDir = value; }
        }
        [ReflectorProperty("inputDirs", Required = false)]
        public string[] InputDirs
        {
            get { return inputDirs; }
            set { inputDirs = value; }
        }
        [ReflectorProperty("silent", Required = false)]
        public bool Silent
        {
            get { return silent; }
            set { silent = value; }
        }
        [ReflectorProperty("reportXslt", Required = false)]
        public string ReportXslt
        {
            get { return reportXslt; }
            set { reportXslt = value; }
        }
        [ReflectorProperty("timeout", Required = false)]
        public int TimeOut
        {
            get { return timeOut; }
            set { timeOut = value; }
        }
        [ReflectorProperty("baseDir", Required = false)]
        public string BaseDirectory
        {
            get { return baseDirectory; }
            set { baseDirectory = value; }
        }
        [ReflectorProperty("publish", Required = false)]
        public bool Publish
        {
            get { return publish; }
            set { publish = value; }
        }
        public override void Run(IIntegrationResult result)
        {
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : "Executing NDepend");
            rootPath = baseDirectory;
            if (string.IsNullOrEmpty(rootPath)) rootPath = result.WorkingDirectory;
            DirectoryInfo outputDirectory = new DirectoryInfo(RootPath(outputDir, false));
            Dictionary<string, DateTime> oldFiles = GenerateOriginalFileList(outputDirectory);
            ProcessResult processResult = TryToRun(CreateProcessInfo(result));
            result.AddTaskResult(new ProcessTaskResult(processResult));
            if (publish)
            {
                FileInfo[] newFiles = ListFileDifferences(oldFiles, outputDirectory);
                if (newFiles.Length > 0)
                {
                    string publishDir = Path.Combine(result.BaseFromArtifactsDirectory(result.Label), "NDepend");
                    if (!Directory.Exists(publishDir)) Directory.CreateDirectory(publishDir);
                    foreach (FileInfo newFile in newFiles)
                    {
                        newFile.CopyTo(Path.Combine(publishDir, newFile.Name));
                        if (newFile.Extension == ".xml")
                        {
                            result.AddTaskResult((new FileTaskResult(newFile)));
                        }
                    }
                }
            }
        }
        protected override string GetProcessFilename()
        {
            string path;
            if (string.IsNullOrEmpty(executable))
            {
                path = RootPath(defaultExecutable, true);
            }
            else
            {
                path = RootPath(executable, true);
            }
            return path;
        }
        protected override string GetProcessBaseDirectory(IIntegrationResult result)
        {
            string path = RootPath(rootPath, true);
            return path;
        }
        protected override int GetProcessTimeout()
        {
            return timeOut * 1000;
        }
        protected override string GetProcessArguments(IIntegrationResult result)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.Append(RootPath(projectFile, true));
            buffer.AppendIf(silent, "/Silent");
            buffer.AppendIf(emitXml, "/EmitVisualNDependBinXml ");
            if ((inputDirs != null) && (inputDirs.Length > 0))
            {
                List<string> dirs = new List<string>();
                foreach (string dir in inputDirs)
                {
                    dirs.Add(RootPath(dir, true));
                }
                buffer.AppendArgument("/InDirs {0}", string.Join(" ", dirs.ToArray()));
            }
            buffer.AppendArgument("/OutDir {0}", RootPath(outputDir, true));
            if (!string.IsNullOrEmpty(reportXslt))
            {
                buffer.AppendArgument("/XslForReport  {0}", RootPath(reportXslt, true));
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
        private FileInfo[] ListFileDifferences(Dictionary<string, DateTime> originalList, DirectoryInfo outputDirectory)
        {
            FileInfo[] newList = {};
            if (outputDirectory.Exists) newList = outputDirectory.GetFiles();
            List<FileInfo> differenceList = new List<FileInfo>();
            foreach (FileInfo newFile in newList)
            {
                if (originalList.ContainsKey(newFile.Name))
                {
                    if (originalList[newFile.Name] != newFile.LastWriteTime)
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
                FileInfo[] oldFiles = outputDirectory.GetFiles();
                foreach (FileInfo oldFile in oldFiles)
                {
                    originalFiles.Add(oldFile.Name, oldFile.LastWriteTime);
                }
            }
            return originalFiles;
        }
    }
}
