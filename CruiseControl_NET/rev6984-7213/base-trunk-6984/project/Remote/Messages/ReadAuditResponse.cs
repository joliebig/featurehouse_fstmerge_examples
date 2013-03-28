using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("readAuditResponse")]
    [Serializable]
    public class ReadAuditResponse
        : Response
    {
        private List<AuditRecord> records = new List<AuditRecord>();
        public ReadAuditResponse()
            : base()
        {
        }
        public ReadAuditResponse(ServerRequest request)
            : base(request)
        {
        }
        public ReadAuditResponse(Response response)
            : base(response)
        {
        }
        [XmlElement("record")]
        public List<AuditRecord> Records
        {
            get { return records; }
            set { records = value; }
        }
    }
}
