using System;
using System.Xml.Serialization;
using System.Collections.Generic;
using System.Runtime.Serialization;
namespace ThoughtWorks.CruiseControl.Remote
{
 [Serializable]
    [XmlRoot("queueSetSnapshot")]
 public class QueueSetSnapshot
 {
        private List<QueueSnapshot> snapshots = new List<QueueSnapshot>();
        private QueueSnapshotList queueSnapshots = null;
  public QueueSetSnapshot()
  {
  }
        [XmlElement("queue")]
        public List<QueueSnapshot> Queues
  {
   get { return snapshots; }
  }
        public QueueSnapshot FindByName(string queueName)
        {
            foreach (QueueSnapshot queueSnapshot in snapshots)
            {
                if (queueSnapshot.QueueName == queueName)
                {
                    return queueSnapshot;
                }
            }
            return null;
        }
        [OnDeserialized]
        private void DataReceived(StreamingContext context)
        {
            if (queueSnapshots != null)
            {
                snapshots = new List<QueueSnapshot>();
                foreach (var queue in queueSnapshots)
                {
                    snapshots.Add(queue as QueueSnapshot);
                }
            }
        }
    }
}
