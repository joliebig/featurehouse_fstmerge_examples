using System;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class UserAuditFilter
        : AuditFilterBase
    {
        private string user;
        public UserAuditFilter(string userName)
            : this(userName, null) { }
        public UserAuditFilter(string userName, IAuditFilter innerFilter)
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
