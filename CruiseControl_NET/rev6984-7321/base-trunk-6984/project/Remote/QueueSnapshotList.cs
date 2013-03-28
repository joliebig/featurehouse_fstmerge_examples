using System;
using System.Collections;
namespace ThoughtWorks.CruiseControl.Remote
{
 [Serializable]
 internal class QueueSnapshotList
        : IEnumerable
    {
        private ArrayList queueSnapshots;
  public QueueSnapshotList()
  {
   queueSnapshots = new ArrayList();
        }
        public IEnumerator GetEnumerator()
  {
   return queueSnapshots.GetEnumerator();
  }
    }
}
