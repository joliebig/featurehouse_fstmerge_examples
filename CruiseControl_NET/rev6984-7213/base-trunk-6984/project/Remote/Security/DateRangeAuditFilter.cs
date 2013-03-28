using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class DateRangeAuditFilter
        : AuditFilterBase
    {
        private DateTime filterStartDate;
        private DateTime filterEndDate;
        public DateRangeAuditFilter()
        {
        }
        public DateRangeAuditFilter(DateTime startDate, DateTime endDate)
            : this(startDate, endDate, null) { }
        public DateRangeAuditFilter(DateTime startDate, DateTime endDate, AuditFilterBase innerFilter)
            : base(innerFilter)
        {
            if (startDate > endDate) throw new ArgumentOutOfRangeException("endDate cannot be before startDate");
            this.filterStartDate = startDate;
            this.filterEndDate = endDate;
        }
        [XmlAttribute("startDate")]
        public DateTime StartDate
        {
            get { return filterStartDate; }
            set { filterStartDate = value; }
        }
        [XmlAttribute("endDate")]
        public DateTime EndDate
        {
            get { return filterEndDate; }
            set { filterEndDate = value; }
        }
        protected override bool DoCheckFilter(AuditRecord record)
        {
            bool include = (record.TimeOfEvent >= filterStartDate) &&
                (record.TimeOfEvent <= filterEndDate);
            return include;
        }
    }
}
