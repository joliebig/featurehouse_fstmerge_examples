namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Collections.Generic;
    using System.Text;
    public class TaskResultDetails
    {
        public TaskResultDetails(string taskName, string taskType, string fileName)
        {
            if (String.IsNullOrEmpty(taskName))
            {
                throw new ArgumentException("taskName is null or empty.", "taskName");
            }
            if (String.IsNullOrEmpty(taskType))
            {
                throw new ArgumentException("taskType is null or empty.", "taskType");
            }
            if (String.IsNullOrEmpty(fileName))
            {
                throw new ArgumentException("fileName is null or empty.", "fileName");
            }
            this.TaskName = taskName;
            this.TaskType = taskType;
            this.FileName = fileName;
        }
        public string TaskName { get; private set; }
        public string TaskType { get; private set; }
        public string FileName { get; private set; }
    }
}
