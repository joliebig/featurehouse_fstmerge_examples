using System;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class DateRangeAuditFilter
        : AuditFilterBase
    {
        private DateTime filterStartDate;
        private DateTime filterEndDate;
        public DateRangeAuditFilter(DateTime startDate, DateTime endDate)
            : this(startDate, endDate, null) { }
        public DateRangeAuditFilter(DateTime startDate, DateTime endDate, IAuditFilter innerFilter)
            : base(innerFilter)
        {
            if (startDate > endDate) throw new ArgumentOutOfRangeException("endDate cannot be before startDate");
            this.filterStartDate = startDate;
            this.filterEndDate = endDate;
        }
        protected override bool DoCheckFilter(AuditRecord record)
        {
            bool include = (record.TimeOfEvent >= filterStartDate) &&
                (record.TimeOfEvent <= filterEndDate);
            return include;
        }
    }
}
