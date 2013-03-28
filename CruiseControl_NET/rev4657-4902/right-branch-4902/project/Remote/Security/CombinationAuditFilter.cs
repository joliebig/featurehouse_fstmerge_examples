using System;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class CombinationAuditFilter
        : AuditFilterBase
    {
        private IAuditFilter[] combinedFilters;
        public CombinationAuditFilter(params IAuditFilter[] filters)
            : this(filters, null) { }
        public CombinationAuditFilter(IAuditFilter[] filters, IAuditFilter innerFilter)
            : base(innerFilter)
        {
            this.combinedFilters = filters;
        }
        protected override bool DoCheckFilter(AuditRecord record)
        {
            bool include = false;
            foreach (IAuditFilter filter in combinedFilters)
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
