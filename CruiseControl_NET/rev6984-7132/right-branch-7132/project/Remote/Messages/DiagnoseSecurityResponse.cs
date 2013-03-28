using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("diagnoseSecurityResponse")]
    [Serializable]
    public class DiagnoseSecurityResponse
        : Response
    {
        private List<SecurityCheckDiagnostics> diagnostics = new List<SecurityCheckDiagnostics>();
        public DiagnoseSecurityResponse()
            : base()
        {
        }
        public DiagnoseSecurityResponse(ServerRequest request)
            : base(request)
        {
        }
        public DiagnoseSecurityResponse(Response response)
            : base(response)
        {
        }
        [XmlElement("diagnosis")]
        public List<SecurityCheckDiagnostics> Diagnostics
        {
            get { return diagnostics; }
            set { diagnostics = value; }
        }
    }
}
