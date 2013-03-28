using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("listPackagesResponse")]
    [Serializable]
    public class ListPackagesResponse
        : Response
    {
        private List<PackageDetails> packages = new List<PackageDetails>();
        public ListPackagesResponse()
            : base()
        {
        }
        public ListPackagesResponse(ServerRequest request)
            : base(request)
        {
        }
        public ListPackagesResponse(Response response)
            : base(response)
        {
        }
        [XmlElement("packages")]
        public List<PackageDetails> Packages
        {
            get { return packages; }
            set { packages = value; }
        }
    }
}
