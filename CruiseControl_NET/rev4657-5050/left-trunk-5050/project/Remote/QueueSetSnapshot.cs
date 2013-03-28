using System;
namespace ThoughtWorks.CruiseControl.Remote
{
 [Serializable]
 public class QueueSetSnapshot
 {
  private QueueSnapshotList queueSnapshots;
  public QueueSetSnapshot()
  {
   queueSnapshots = new QueueSnapshotList();
  }
  public QueueSnapshotList Queues
  {
   get { return queueSnapshots; }
  }
 }
}
