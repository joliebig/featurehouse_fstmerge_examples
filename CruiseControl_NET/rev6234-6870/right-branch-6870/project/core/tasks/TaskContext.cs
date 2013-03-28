namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Text;
    using System.Threading;
    using System.Xml;
    using ThoughtWorks.CruiseControl.Core.Publishers;
    using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
    public class TaskContext
    {
        private readonly IFileSystem fileSystem;
        private readonly TaskContext parentContext;
        private readonly Guid contextId = Guid.NewGuid();
        private readonly IIntegrationResult result;
        private readonly object snapshotLock = new object();
        private TaskResult associatedResult;
        private string buildFolder;
        private TaskContext(ProjectConfiguration project, IFileSystem fileSystem, IIntegrationResult result)
            : this(project, fileSystem, result, null)
        {
        }
        private TaskContext(ProjectConfiguration project, IFileSystem fileSystem, IIntegrationResult result, TaskContext parent)
        {
            this.Project = project;
            this.fileSystem = fileSystem;
            this.result = result;
            this.parentContext = parent;
            this.associatedResult = new TaskResult("project", project.Name);
        }
        public ProjectConfiguration Project { get; private set; }
        public bool IsFinialised { get; private set; }
        public string BuildFolder
        {
            get
            {
                if (this.buildFolder == null)
                {
                    this.buildFolder = this.result.BaseFromArtifactsDirectory(this.result.Label);
                }
                return this.buildFolder;
            }
        }
        public static TaskContext FromProject(Project project, IIntegrationResult result)
        {
            var context = new TaskContext(ProjectConfiguration.FromProject(project), new SystemIoFileSystem(), result);
            return context;
        }
        public static TaskContext FromProject(ProjectConfiguration project, IIntegrationResult result)
        {
            var context = new TaskContext(project, new SystemIoFileSystem(), result);
            return context;
        }
        public string GetStreamName(Stream remoteStream)
        {
            var fileStream = remoteStream as FileStream;
            if (fileStream != null)
            {
                return fileStream.Name;
            }
            else
            {
                return string.Empty;
            }
        }
        public void ImportResult(TaskResult resultToImport)
        {
            this.associatedResult.Children.Add(resultToImport);
        }
        public void ImportResultFile(string filename, string resultName, string dataType, bool deleteSourceFile)
        {
            if (this.IsFinialised)
            {
                throw new ApplicationException("Context has been finialised - no further actions can be performed using it");
            }
            if (String.IsNullOrEmpty(filename))
            {
                throw new ArgumentException("filename is null or empty.", "filename");
            }
            if (String.IsNullOrEmpty(resultName))
            {
                throw new ArgumentException("resultName is null or empty.", "resultName");
            }
            if (String.IsNullOrEmpty(dataType))
            {
                throw new ArgumentException("dataType is null or empty.", "dataType");
            }
            var actualFilename = this.GenerateDataFilename();
            this.fileSystem.EnsureFolderExists(actualFilename);
            var details = new TaskOutput(actualFilename, resultName, dataType);
            this.LockResults(
                t =>
                {
                    this.associatedResult.Output.Add(details);
                },
                "Unable to add new output - index is locked");
            if (deleteSourceFile)
            {
                File.Move(filename, actualFilename);
            }
            else
            {
                File.Copy(filename, actualFilename);
            }
        }
        public void ImportResultFile(Stream inputStream, string resultName, string dataType)
        {
            if (this.IsFinialised)
            {
                throw new ApplicationException("Context has been finialised - no further actions can be performed using it");
            }
            if (inputStream == null)
            {
                throw new ArgumentNullException("inputStream");
            }
            if (String.IsNullOrEmpty(resultName))
            {
                throw new ArgumentException("resultName is null or empty.", "resultName");
            }
            if (String.IsNullOrEmpty(dataType))
            {
                throw new ArgumentException("dataType is null or empty.", "dataType");
            }
            using (var newStream = this.CreateResultStream(resultName, dataType))
            {
                using (var stream = this.fileSystem.ResetStreamForReading(inputStream))
                {
                    var buffer = new byte[4096];
                    var length = 1;
                    while ((length = stream.Read(buffer, 0, buffer.Length)) > 0)
                    {
                        newStream.Write(buffer, 0, length);
                    }
                }
            }
        }
        public virtual Stream CreateResultStream(string resultName, string dataType)
        {
            if (this.IsFinialised)
            {
                throw new ApplicationException("Context has been finialised - no further actions can be performed using it");
            }
            if (String.IsNullOrEmpty(resultName))
            {
                throw new ArgumentException("resultName is null or empty.", "resultName");
            }
            if (String.IsNullOrEmpty(dataType))
            {
                throw new ArgumentException("dataType is null or empty.", "dataType");
            }
            var fileName = this.GenerateDataFilename();
            this.fileSystem.EnsureFolderExists(fileName);
            var details = new TaskOutput(fileName, resultName, dataType);
            this.LockResults(
                t =>
                {
                    this.associatedResult.Output.Add(details);
                },
                "Unable to add new result - index is locked");
            var stream = this.fileSystem.OpenOutputStream(fileName);
            return stream;
        }
        public TaskContext StartChildContext()
        {
            var child = new TaskContext(this.Project, this.fileSystem, this.result, this);
            return child;
        }
        public void InitialiseResult(string taskType, string identifier)
        {
            this.associatedResult = new TaskResult(taskType, identifier);
        }
        public void MergeChildContext(TaskContext childContext, ItemBuildStatus status)
        {
            if (childContext == null)
            {
                throw new ArgumentNullException("childContext", "childContext is null.");
            }
            else if (childContext.parentContext.contextId != this.contextId)
            {
                throw new ArgumentException("Unable to merge - child does not belong to this context", "childContext");
            }
            childContext.IsFinialised = true;
            childContext.associatedResult.TaskOutcome = status;
            this.LockResults(
                t =>
                {
                    this.associatedResult.Children.Add(childContext.associatedResult);
                },
                "Unable to merge results - index is locked");
        }
        public string GenerateLogFolder(string logFolder)
        {
            var folder = logFolder ?? "buildlogs";
            if (!Path.IsPathRooted(folder))
            {
                folder = this.result.BaseFromArtifactsDirectory(folder);
            }
            this.fileSystem.EnsureFolderExists(folder, false);
            return folder;
        }
        public string GenerateLogFilename()
        {
            var baseName = Util.StringUtil.RemoveInvalidCharactersFromFileName(new LogFile(this.result).Filename);
            var fullName = Path.Combine(this.result.BuildLogDirectory, baseName);
            return fullName;
        }
        public void Finialise(ItemBuildStatus status)
        {
            this.IsFinialised = true;
            this.associatedResult.TaskOutcome = status;
            var logLocation = this.GenerateLogFolder(this.Project.LogFolder);
            this.result.BuildLogDirectory = logLocation;
            var logName = this.GenerateLogFilename();
            this.fileSystem.DeleteFile(logName);
            using (var writer = new StreamWriter(this.fileSystem.OpenOutputStream(logName)))
            {
                this.WriteCurrentLog(writer);
            }
        }
        public void WriteCurrentLog(TextWriter writer)
        {
            using (var integrationWriter = new XmlIntegrationResultWriter(writer, this.GenerateResultsSnapshot()))
            {
                integrationWriter.Formatting = Formatting.Indented;
                integrationWriter.Write(this.result);
            }
        }
        public TaskResult GenerateResultsSnapshot()
        {
            TaskResult snapshot = null;
            if (this.parentContext != null)
            {
                snapshot = this.parentContext.GenerateResultsSnapshot();
            }
            else
            {
                this.LockResults(
                    t =>
                    {
                        snapshot = this.associatedResult;
                    },
                    "Unable to generate snapshot - unable to retrieve lock");
            }
            return snapshot;
        }
        public void RunTask(ITask task)
        {
            if (task == null)
            {
                throw new ArgumentNullException("task", "task is null.");
            }
            var child = this.StartChildContext();
            try
            {
                if (task is TaskBase)
                {
                    (task as TaskBase).AssociateContext(child);
                }
                task.Run(this.result);
            }
            finally
            {
                if (task is TaskBase)
                {
                    this.MergeChildContext(child, (task as TaskBase).CurrentStatus.Status);
                }
                else
                {
                    this.MergeChildContext(child, ItemBuildStatus.Unknown);
                }
            }
        }
        private void LockResults(Action<bool> action, string errorMessage)
        {
            if (Monitor.TryEnter(this.snapshotLock, 30000))
            {
                try
                {
                    action(true);
                }
                finally
                {
                    Monitor.Exit(this.snapshotLock);
                }
            }
            else
            {
                throw new CruiseControlException(errorMessage);
            }
        }
        private string GenerateDataFilename()
        {
            return Path.Combine(this.BuildFolder, Guid.NewGuid().ToString() + ".data");
        }
    }
}
