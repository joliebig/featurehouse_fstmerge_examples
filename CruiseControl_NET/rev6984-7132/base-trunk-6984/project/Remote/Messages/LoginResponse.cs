using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("loginResponse")]
    [Serializable]
    public class LoginResponse
        : Response
    {
        private string sessionToken;
        public LoginResponse()
            : base()
        {
        }
        public LoginResponse(ServerRequest request)
            : base(request)
        {
        }
        public LoginResponse(Response response)
            : base(response)
        {
        }
        [XmlAttribute("sessionToken")]
        public string SessionToken
        {
            get { return sessionToken; }
            set { sessionToken = value; }
        }
    }
}
