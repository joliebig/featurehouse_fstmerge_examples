using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("statusSnapshotResponse")]
    [Serializable]
    public class StatusSnapshotResponse
        : Response
    {
        private ProjectStatusSnapshot snapshot;
        public StatusSnapshotResponse()
            : base()
        {
        }
        public StatusSnapshotResponse(ServerRequest request)
            : base(request)
        {
        }
        public StatusSnapshotResponse(Response response)
            : base(response)
        {
        }
        [XmlElement("snapshot")]
        public ProjectStatusSnapshot Snapshot
        {
            get { return snapshot; }
            set { snapshot = value; }
        }
    }
}
