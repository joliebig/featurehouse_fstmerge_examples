using System;
using System.Xml.Serialization;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class CombinationAuditFilter
        : AuditFilterBase
    {
        private List<AuditFilterBase> combinedFilters = new List<AuditFilterBase>();
        public CombinationAuditFilter()
        {
        }
        public CombinationAuditFilter(params AuditFilterBase[] filters)
            : this(filters, null) { }
        public CombinationAuditFilter(AuditFilterBase[] filters, AuditFilterBase innerFilter)
            : base(innerFilter)
        {
            this.combinedFilters = new List<AuditFilterBase>(filters);
        }
        [XmlElement("filter")]
        public List<AuditFilterBase> Filters
        {
            get { return combinedFilters; }
            set { combinedFilters = value; }
        }
        protected override bool DoCheckFilter(AuditRecord record)
        {
            bool include = false;
            foreach (AuditFilterBase filter in combinedFilters)
            {
                if (filter.CheckFilter(record))
                {
                    include = true;
                    break;
                }
            }
            return include;
        }
    }
}
