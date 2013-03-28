using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class SecurityRightAuditFilter
        : AuditFilterBase
    {
        private SecurityRight right;
        public SecurityRightAuditFilter()
        {
        }
        public SecurityRightAuditFilter(SecurityRight securityRight)
            : this(securityRight, null) { }
        public SecurityRightAuditFilter(SecurityRight securityRight, AuditFilterBase innerFilter)
            : base(innerFilter)
        {
            this.right = securityRight;
        }
        [XmlAttribute("right")]
        public SecurityRight SecurityRight
        {
            get { return right; }
            set { right = value; }
        }
        protected override bool DoCheckFilter(AuditRecord record)
        {
            bool include = (this.right == record.SecurityRight);
            return include;
        }
    }
}
