using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("encryptedRequest")]
    [Serializable]
    public class EncryptedRequest
        : ServerRequest
    {
        private string encryptedData;
        public EncryptedRequest()
        {
        }
        public EncryptedRequest(string sessionToken)
            : base(sessionToken)
        {
        }
        public EncryptedRequest(string sessionToken, string encryptedData)
            : base(sessionToken)
        {
            this.encryptedData = encryptedData;
        }
        [XmlElement("data")]
        public string EncryptedData
        {
            get { return encryptedData; }
            set { encryptedData = value; }
        }
        [XmlElement("action")]
        public string Action { get; set; }
    }
}
