using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("loginMessage")]
    [Serializable]
    public class LoginRequest
        : ServerRequest
    {
        public const string UserNameCredential = "userName";
        public const string PasswordCredential = "password";
        public const string TypeCredential = "type";
        public const string DomainCredential = "domain";
        private List<NameValuePair> credentials = new List<NameValuePair>();
        public LoginRequest()
        {
        }
        public LoginRequest(string userName)
        {
            credentials.Add(new NameValuePair(LoginRequest.UserNameCredential, userName));
        }
        [XmlElement("credential")]
        public List<NameValuePair> Credentials
        {
            get { return credentials; }
            set { credentials = value; }
        }
        public NameValuePair AddCredential(string name, string value)
        {
            NameValuePair credential = new NameValuePair(name, value);
            credentials.Add(credential);
            return credential;
        }
    }
}
