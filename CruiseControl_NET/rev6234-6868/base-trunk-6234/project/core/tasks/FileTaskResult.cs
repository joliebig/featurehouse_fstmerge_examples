namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.IO;
    public class FileTaskResult
        : ITaskResult
    {
        private readonly FileInfo dataSource;
        public FileTaskResult(string filename) :
            this(new FileInfo(filename))
        {
        }
        public FileTaskResult(FileInfo file)
        {
            if (!file.Exists)
            {
                throw new CruiseControlException("File not found: " + file.FullName);
            }
            this.dataSource = file;
        }
        public bool WrapInCData { get; set; }
        public string Data
        {
            get
            {
                var data = this.ReadFileContents();
                if (WrapInCData)
                {
                    return string.Format("<![CDATA[{0}]]>", data);
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
        private string ReadFileContents()
        {
            try
            {
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
