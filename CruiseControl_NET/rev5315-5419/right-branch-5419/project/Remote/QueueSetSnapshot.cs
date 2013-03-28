using System;
using System.Xml.Serialization;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Remote
{
 [Serializable]
    [XmlRoot("queueSetSnapshot")]
 public class QueueSetSnapshot
 {
  private List<QueueSnapshot> queueSnapshots;
  public QueueSetSnapshot()
  {
            queueSnapshots = new List<QueueSnapshot>();
  }
        [XmlElement("queue")]
        public List<QueueSnapshot> Queues
  {
   get { return queueSnapshots; }
  }
        public QueueSnapshot FindByName(string queueName)
        {
            foreach (QueueSnapshot queueSnapshot in queueSnapshots)
            {
                if (queueSnapshot.QueueName == queueName)
                {
                    return queueSnapshot;
                }
            }
            return null;
        }
 }
}
