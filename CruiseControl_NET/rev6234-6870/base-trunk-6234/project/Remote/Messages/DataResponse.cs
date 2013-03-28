using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("dataResponse")]
    [Serializable]
    public class DataResponse
        : Response
    {
        private string data;
        public DataResponse()
            : base()
        {
        }
        public DataResponse(ServerRequest request)
            : base(request)
        {
        }
        public DataResponse(Response response)
            : base(response)
        {
        }
        [XmlElement("data")]
        public string Data
        {
            get { return data; }
            set { data = value; }
        }
    }
}
