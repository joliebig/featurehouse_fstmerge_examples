namespace ThoughtWorks.CruiseControl.Core.Label
{
    using System;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
 [ReflectorType("dateLabeller")]
 public class DateLabeller
        : LabellerBase
 {
  private readonly DateTimeProvider dateTimeProvider;
        public DateLabeller()
            : this(new DateTimeProvider())
        {
        }
        public DateLabeller(DateTimeProvider dateTimeProvider)
        {
            this.dateTimeProvider = dateTimeProvider;
            this.YearFormat = "0000";
            this.MonthFormat = "00";
            this.DayFormat = "00";
            RevisionFormat = "000";
        }
        [ReflectorProperty("yearFormat", Required = false)]
        public string YearFormat { get; set; }
        [ReflectorProperty("monthFormat", Required = false)]
        public string MonthFormat { get; set; }
        [ReflectorProperty("dayFormat", Required = false)]
        public string DayFormat { get; set; }
        [ReflectorProperty("revisionFormat", Required = false)]
        public string RevisionFormat { get; set; }
  public override string Generate(IIntegrationResult integrationResult)
  {
   DateTime now = dateTimeProvider.Now;
   Version version = ParseVersion(now, integrationResult.LastIntegration);
   int revision = version.Revision;
   if (now.Year == version.Major && now.Month == version.Minor && now.Day == version.Build)
   {
    revision += 1;
   }
   else
   {
    revision = 1;
   }
            return string.Format("{0}.{1}.{2}.{3}",
                   now.Year.ToString(YearFormat), now.Month.ToString(MonthFormat), now.Day.ToString(DayFormat), revision.ToString(RevisionFormat));
  }
  private Version ParseVersion(DateTime date, IntegrationSummary lastIntegrationSummary)
  {
   try
   {
    return new Version(lastIntegrationSummary.LastSuccessfulIntegrationLabel);
   }
   catch (SystemException)
   {
    return new Version(date.Year, date.Month, date.Day, 0);
   }
  }
 }
}
