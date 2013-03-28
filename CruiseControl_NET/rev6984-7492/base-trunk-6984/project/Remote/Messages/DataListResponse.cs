using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("dataListResponse")]
    [Serializable]
    public class DataListResponse
        : Response
    {
        private List<string> data = new List<string>();
        public DataListResponse()
            : base()
        {
        }
        public DataListResponse(ServerRequest request)
            : base(request)
        {
        }
        public DataListResponse(Response response)
            : base(response)
        {
        }
        [XmlElement("data")]
        public List<string> Data
        {
            get { return data; }
            set { data = value; }
        }
    }
}
