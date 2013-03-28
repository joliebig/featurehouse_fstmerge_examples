using System;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class SecurityRightAuditFilter
        : AuditFilterBase
    {
        private SecurityRight right;
        public SecurityRightAuditFilter(SecurityRight securityRight)
            : this(securityRight, null) { }
        public SecurityRightAuditFilter(SecurityRight securityRight, IAuditFilter innerFilter)
            : base(innerFilter)
        {
            this.right = securityRight;
        }
        protected override bool DoCheckFilter(AuditRecord record)
        {
            bool include = (this.right == record.SecurityRight);
            return include;
        }
    }
}
