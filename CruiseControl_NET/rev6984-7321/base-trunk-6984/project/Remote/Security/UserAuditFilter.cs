using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class UserAuditFilter
        : AuditFilterBase
    {
        private string user;
        public UserAuditFilter()
        {
        }
        public UserAuditFilter(string userName)
            : this(userName, null) { }
        [XmlAttribute("user")]
        public string UserName
        {
            get { return user; }
            set { user = value; }
        }
        public UserAuditFilter(string userName, AuditFilterBase innerFilter)
            : base(innerFilter)
        {
            if (string.IsNullOrEmpty(userName)) throw new ArgumentNullException("userName");
            this.user = userName;
        }
        protected override bool DoCheckFilter(AuditRecord record)
        {
            bool include = string.Equals(this.user, record.UserName);
            return include;
        }
    }
}
