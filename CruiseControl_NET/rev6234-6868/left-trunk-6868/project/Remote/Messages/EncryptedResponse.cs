using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("encryptedResponse")]
    [Serializable]
    public class EncryptedResponse
        : Response
    {
        private string encryptedData;
        public EncryptedResponse()
            : base()
        {
        }
        public EncryptedResponse(ServerRequest request)
            : base(request)
        {
        }
        public EncryptedResponse(Response response)
            : base(response)
        {
        }
        [XmlElement("data")]
        public string EncryptedData
        {
            get { return encryptedData; }
            set { encryptedData = value; }
        }
    }
}
