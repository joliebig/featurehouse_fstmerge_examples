using System;
namespace ThoughtWorks.CruiseControl.Remote
{
 [Serializable]
 public class QueuedRequestSnapshot
 {
  private string projectName;
     private ProjectActivity activity;
        private DateTime requestTime;
  public QueuedRequestSnapshot(string projectName, ProjectActivity activity)
            : this(projectName, activity, DateTime.MinValue) { }
        public QueuedRequestSnapshot(string projectName, ProjectActivity activity, DateTime requestTime)
  {
   this.projectName = projectName;
            this.activity = activity;
            this.requestTime = requestTime;
  }
  public string ProjectName
  {
   get { return projectName; }
  }
     public ProjectActivity Activity
     {
         get { return activity; }
     }
        public DateTime RequestTime
        {
            get { return requestTime; }
            set { requestTime = value; }
        }
 }
}
