using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.IO;
    using System.Runtime;
    public class FileTaskResult
        : ITaskResult, ITemporaryResult
    {
        private readonly FileInfo dataSource;
        private bool deleteAfterMerge = true;
        private readonly IFileSystem fileSystem;
        public FileTaskResult(string filename) :
            this(filename, true)
        {
        }
        public FileTaskResult(string filename, bool deleteAfterMerge) :
            this(new FileInfo(filename), deleteAfterMerge)
        {
        }
        public FileTaskResult(FileInfo file) :
            this(file, true)
        {
        }
        public FileTaskResult(FileInfo file, bool deleteAfterMerge) :
            this(file, deleteAfterMerge, new SystemIoFileSystem())
        {
        }
        public FileTaskResult(FileInfo file, bool deleteAfterMerge, IFileSystem fileSystem)
        {
            this.deleteAfterMerge = deleteAfterMerge;
            this.dataSource = file;
            this.fileSystem = fileSystem;
            if (!fileSystem.FileExists(file.FullName))
            {
                throw new CruiseControlException("File not found: " + file.FullName);
            }
        }
        public bool WrapInCData { get; set; }
        public bool DeleteAfterMerge
        {
            get { return deleteAfterMerge; }
        }
        public FileInfo File
        {
            get { return dataSource; }
        }
        public string Data
        {
            get
            {
                var data = this.ReadFileContents();
                if (WrapInCData)
                {
                    return "<![CDATA[" + data + "]]>";
                }
                else
                {
                    return data;
                }
            }
        }
        public bool CheckIfSuccess()
        {
            return true;
        }
        public virtual void CleanUp()
        {
            if (this.deleteAfterMerge)
            {
                this.fileSystem.DeleteFile(this.dataSource.FullName);
            }
        }
        private string ReadFileContents()
        {
            try
            {
                if (this.dataSource.Length > 1048576)
                {
                    var fileSizeInMB = Convert.ToInt32(this.dataSource.Length / 524288);
                    try
                    {
                        using (new MemoryFailPoint(fileSizeInMB))
                        {
                        }
                    }
                    catch (InsufficientMemoryException error)
                    {
                        throw new CruiseControlException("Insufficient memory to import file results: " + error.Message, error);
                    }
                }
                using (StreamReader reader = this.dataSource.OpenText())
                {
                    return reader.ReadToEnd();
                }
            }
            catch (Exception ex)
            {
                throw new CruiseControlException("Unable to read the contents of the file: " + this.dataSource.FullName, ex);
            }
        }
    }
}
