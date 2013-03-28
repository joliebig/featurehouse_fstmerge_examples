using System;
namespace ThoughtWorks.CruiseControl.Remote
{
 [Serializable]
 public class QueuedRequestSnapshot
 {
  private string projectName;
     private ProjectActivity activity;
  public QueuedRequestSnapshot(string projectName, ProjectActivity activity)
  {
   this.projectName = projectName;
            this.activity = activity;
  }
  public string ProjectName
  {
   get { return projectName; }
  }
     public ProjectActivity Activity
     {
         get { return activity; }
     }
 }
}
