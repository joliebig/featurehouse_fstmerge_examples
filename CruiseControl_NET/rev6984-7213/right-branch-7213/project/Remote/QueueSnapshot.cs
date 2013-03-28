using System;
using System.Xml.Serialization;
using System.Collections.Generic;
using System.Runtime.Serialization;
namespace ThoughtWorks.CruiseControl.Remote
{
 [Serializable]
    [XmlRoot("queueSnapshot")]
 public class QueueSnapshot
 {
  private string queueName;
        private List<QueuedRequestSnapshot> queueRequests = new List<QueuedRequestSnapshot>();
        private QueuedRequestSnapshotList _requests;
        public QueueSnapshot()
        {
        }
  public QueueSnapshot(string queueName)
  {
   this.queueName = queueName;
  }
        [XmlAttribute("name")]
  public string QueueName
  {
   get { return queueName; }
            set { queueName = value; }
  }
        [XmlElement("queueRequest")]
        public List<QueuedRequestSnapshot> Requests
  {
   get { return queueRequests; }
  }
        [XmlIgnore]
        public bool IsEmpty
        {
            get { return queueRequests.Count == 0; }
        }
        [OnDeserialized]
        private void DataReceived(StreamingContext context)
        {
            if (_requests != null)
            {
                queueRequests = new List<QueuedRequestSnapshot>();
                foreach (var queue in _requests)
                {
                    queueRequests.Add(queue as QueuedRequestSnapshot);
                }
            }
        }
    }
}
