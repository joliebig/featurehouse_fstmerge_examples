using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("projectStatusResponse")]
    [Serializable]
    public class ProjectStatusResponse
        : Response
    {
        private List<ProjectStatus> projects = new List<ProjectStatus>();
        public ProjectStatusResponse()
            : base()
        {
        }
        public ProjectStatusResponse(ServerRequest request)
            : base(request)
        {
        }
        public ProjectStatusResponse(Response response)
            : base(response)
        {
        }
        [XmlElement("project")]
        public List<ProjectStatus> Projects
        {
            get { return projects; }
            set { projects = value; }
        }
    }
}
