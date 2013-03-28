using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("snapshotResponse")]
    [Serializable]
    public class SnapshotResponse
        : Response
    {
        private CruiseServerSnapshot snapshot;
        public SnapshotResponse()
            : base()
        {
        }
        public SnapshotResponse(ServerRequest request)
            : base(request)
        {
        }
        public SnapshotResponse(Response response)
            : base(response)
        {
        }
        [XmlElement("snapshot")]
        public CruiseServerSnapshot Snapshot
        {
            get { return snapshot; }
            set { snapshot = value; }
        }
    }
}
