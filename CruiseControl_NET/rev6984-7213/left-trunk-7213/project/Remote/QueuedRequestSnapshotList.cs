using System;
using System.Collections;
namespace ThoughtWorks.CruiseControl.Remote
{
    [Serializable]
 public class QueuedRequestSnapshotList
        : IEnumerable
    {
        private ArrayList queuedRequests;
  public QueuedRequestSnapshotList()
  {
   queuedRequests = new ArrayList();
        }
        public IEnumerator GetEnumerator()
        {
            return queuedRequests.GetEnumerator();
        }
    }
}
