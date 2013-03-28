using System;
using System.Collections;
namespace ThoughtWorks.CruiseControl.Remote
{
 [Serializable]
 public class QueuedRequestSnapshotList : IEnumerable
 {
  private ArrayList queuedRequests;
  public QueuedRequestSnapshotList()
  {
   queuedRequests = new ArrayList();
  }
  public int Count
  {
   get { return queuedRequests.Count; }
  }
  public void Add(QueuedRequestSnapshot queuedRequestSnapshot)
  {
   queuedRequests.Add(queuedRequestSnapshot);
  }
  public QueuedRequestSnapshot this[int index]
  {
   get { return queuedRequests[index] as QueuedRequestSnapshot; }
  }
  IEnumerator IEnumerable.GetEnumerator()
  {
   return queuedRequests.GetEnumerator();
  }
 }
}
