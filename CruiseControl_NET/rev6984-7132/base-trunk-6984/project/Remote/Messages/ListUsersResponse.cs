using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [XmlRoot("listUsersResponse")]
    [Serializable]
    public class ListUsersResponse
        : Response
    {
        private List<UserDetails> users = new List<UserDetails>();
        public ListUsersResponse()
            : base()
        {
        }
        public ListUsersResponse(ServerRequest request)
            : base(request)
        {
        }
        public ListUsersResponse(Response response)
            : base(response)
        {
        }
        [XmlElement("user")]
        public List<UserDetails> Users
        {
            get { return users; }
            set { users = value; }
        }
    }
}
