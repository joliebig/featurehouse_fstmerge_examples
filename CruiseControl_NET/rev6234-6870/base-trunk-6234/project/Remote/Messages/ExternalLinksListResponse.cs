using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("externalLinksResponse")]
    [Serializable]
    public class ExternalLinksListResponse
        : Response
    {
        private List<ExternalLink> data = new List<ExternalLink>();
        public ExternalLinksListResponse()
            : base()
        {
        }
        public ExternalLinksListResponse(ServerRequest request)
            : base(request)
        {
        }
        public ExternalLinksListResponse(Response response)
            : base(response)
        {
        }
        [XmlElement("link")]
        public List<ExternalLink> ExternalLinks
        {
            get { return data; }
            set { data = value; }
        }
    }
}
