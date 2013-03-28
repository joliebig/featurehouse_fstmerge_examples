namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Text;
    using System.Xml;
    using ThoughtWorks.CruiseControl.Core.Util;
    public class TaskContext
    {
        private readonly IFileSystem fileSystem;
        private readonly List<TaskResultDetails> resultDetails = new List<TaskResultDetails>();
        private readonly Guid contextId;
        private readonly TaskContext parentContext;
        public TaskContext(IFileSystem fileSystem, string artifactFolder)
            : this(fileSystem, artifactFolder, null, Guid.NewGuid())
        {
        }
        private TaskContext(IFileSystem fileSystem, string artifactFolder, TaskContext parent, Guid contextId)
        {
            this.fileSystem = fileSystem;
            this.ArtifactFolder = artifactFolder;
            this.parentContext = parent;
            this.contextId = contextId;
        }
        public string ArtifactFolder { get; private set; }
        public bool IsFinialised { get; private set; }
        public delegate void MergeHandler(Stream outputStream, Stream[] inputStreams);
        public void MergeResultStreams(string taskName, string taskType, params Stream[] streamsToMerge)
        {
            this.MergeResultStreams(taskName, taskType, null, streamsToMerge);
        }
        public void MergeResultStreams(string taskName, string taskType, MergeHandler mergeHandler, params Stream[] streamsToMerge)
        {
            if (this.IsFinialised)
            {
                throw new ApplicationException("Context has been finialised - no further actions can be performed using it");
            }
            if (String.IsNullOrEmpty(taskName))
            {
                throw new ArgumentException("taskName is null or empty.", "taskName");
            }
            if (String.IsNullOrEmpty(taskType))
            {
                throw new ArgumentException("taskType is null or empty.", "taskType");
            }
            if (streamsToMerge.Length == 0)
            {
                throw new ArgumentException("There must be at least one stream to merge");
            }
            var actualHandler = mergeHandler ?? new MergeHandler((output, input) =>
            {
                var blockSize = 32768;
                var dataBlock = new byte[blockSize];
                int len;
                foreach (var stream in input)
                {
                    while ((len = stream.Read(dataBlock, 0, blockSize)) > 0)
                    {
                        output.Write(dataBlock, 0, len);
                    }
                }
            });
            var inputStreams = new List<Stream>();
            try
            {
                foreach (FileStream streamToMerge in streamsToMerge)
                {
                    inputStreams.Add(this.fileSystem.OpenInputStream(streamToMerge.Name));
                }
                using (var outputStream = this.CreateResultStream(taskName, taskType))
                {
                    actualHandler(outputStream, inputStreams.ToArray());
                }
            }
            finally
            {
                foreach (var inputStream in inputStreams)
                {
                    inputStream.Dispose();
                }
            }
            foreach (FileStream streamToMerge in streamsToMerge)
            {
                int position = -1;
                for (var loop = this.resultDetails.Count - 1; loop >= 0; loop--)
                {
                    if (this.resultDetails[loop].FileName == streamToMerge.Name)
                    {
                        position = loop;
                        break;
                    }
                }
                if (position >= 0)
                {
                    this.resultDetails.RemoveAt(position);
                }
            }
        }
        public virtual Stream CreateResultStream(string taskName, string taskType)
        {
            return this.CreateResultStream(taskName, taskType, false);
        }
        public virtual Stream CreateResultStream(string taskName, string taskType, bool ignoreExtension)
        {
            if (this.IsFinialised)
            {
                throw new ApplicationException("Context has been finialised - no further actions can be performed using it");
            }
            if (String.IsNullOrEmpty(taskName))
            {
                throw new ArgumentException("taskName is null or empty.", "taskName");
            }
            if (String.IsNullOrEmpty(taskType))
            {
                throw new ArgumentException("taskType is null or empty.", "taskType");
            }
            var extension = Path.GetExtension(taskName);
            if (!ignoreExtension || (extension.Length == 0))
            {
                extension = ".xml";
            }
            var fileName = this.GenerateUniqueFileName(
                Path.GetFileNameWithoutExtension(taskName) + extension);
            var details = new TaskResultDetails(taskName, taskType, fileName);
            this.resultDetails.Add(details);
            var stream = this.fileSystem.OpenOutputStream(fileName);
            return stream;
        }
        public TaskContext StartChildContext()
        {
            var childId = Guid.NewGuid();
            var childFolder = Path.Combine(this.ArtifactFolder, childId.ToString());
            this.fileSystem.EnsureFolderExists(Path.Combine(childFolder, "temp"));
            var child = new TaskContext(this.fileSystem, childFolder, this, childId);
            return child;
        }
        public void MergeChildContext(TaskContext childContext)
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
            foreach (var childResult in childContext.resultDetails)
            {
                var newFilePath = this.GenerateUniqueFileName(
                    Path.GetFileName(childResult.FileName));
                this.fileSystem.MoveFile(childResult.FileName, newFilePath);
                this.resultDetails.Add(
                    new TaskResultDetails(childResult.TaskName, childResult.TaskType, newFilePath));
            }
        }
        public void Finialise()
        {
            this.IsFinialised = true;
            var indexPath = Path.Combine(this.ArtifactFolder, "ccnet-task-index.xml");
            using (var indexStream = this.fileSystem.OpenOutputStream(indexPath))
            {
                var settings = new XmlWriterSettings()
                {
                    CheckCharacters = true,
                    CloseOutput = true,
                    ConformanceLevel = ConformanceLevel.Document,
                    Encoding = UTF8Encoding.UTF8,
                    Indent = false,
                    NewLineHandling = NewLineHandling.None,
                    NewLineOnAttributes = false,
                    OmitXmlDeclaration = true
                };
                using (var document = XmlWriter.Create(indexStream, settings))
                {
                    document.WriteStartElement("task");
                    foreach (var result in this.resultDetails)
                    {
                        document.WriteStartElement("result");
                        document.WriteAttributeString("file", result.FileName);
                        document.WriteAttributeString("name", result.TaskName);
                        document.WriteAttributeString("type", result.TaskType);
                        document.WriteEndElement();
                    }
                    document.WriteEndElement();
                    document.Flush();
                    document.Close();
                }
            }
        }
        public void RunTask(TaskBase task, IIntegrationResult result)
        {
            if (task == null)
            {
                throw new ArgumentNullException("task", "task is null.");
            }
            var child = this.StartChildContext();
            try
            {
                task.AssociateContext(this);
                task.Run(result);
            }
            finally
            {
                this.MergeChildContext(child);
            }
        }
        private string GenerateUniqueFileName(string fileName)
        {
            var baseFileName = Path.Combine(
                this.ArtifactFolder,
                Path.GetFileNameWithoutExtension(fileName));
            var extension = Path.GetExtension(fileName);
            var actualFileName = baseFileName + extension;
            var copy = 0;
            while (this.fileSystem.FileExists(actualFileName))
            {
                actualFileName = baseFileName + "-" + (++copy).ToString() + extension;
            }
            return actualFileName;
        }
    }
}
