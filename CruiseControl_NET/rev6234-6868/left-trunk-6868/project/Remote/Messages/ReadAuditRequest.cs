using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using ThoughtWorks.CruiseControl.Remote.Security;
using System.ComponentModel;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("readAuditMessage")]
    [Serializable]
    public class ReadAuditRequest
        : ServerRequest
    {
        private int startRecord = 0;
        private int numberOfRecords = int.MaxValue;
        private AuditFilterBase filter;
        [XmlAttribute("start")]
        [DefaultValue(0)]
        public int StartRecord
        {
            get { return startRecord; }
            set { startRecord = value; }
        }
        [XmlAttribute("number")]
        [DefaultValue(int.MaxValue)]
        public int NumberOfRecords
        {
            get { return numberOfRecords; }
            set { numberOfRecords = value; }
        }
        [XmlElement("filter")]
        public AuditFilterBase Filter
        {
            get { return filter; }
            set { filter = value; }
        }
    }
}
