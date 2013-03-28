using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("buildParametersResponse")]
    [Serializable]
    public class BuildParametersResponse
        : Response
    {
        private List<ParameterBase> parameters = new List<ParameterBase>();
        public BuildParametersResponse()
            : base()
        {
        }
        public BuildParametersResponse(ServerRequest request)
            : base(request)
        {
        }
        public BuildParametersResponse(Response response)
            : base(response)
        {
        }
        [XmlElement("parameter")]
        public List<ParameterBase> Parameters
        {
            get { return parameters; }
            set { parameters = value; }
        }
    }
}
