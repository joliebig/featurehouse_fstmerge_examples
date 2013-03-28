namespace ThoughtWorks.CruiseControl.Core.Triggers.NCrontab
{
    using System;
    [ Serializable ]
    public enum CrontabFieldKind
    {
        Minute,
        Hour,
        Day,
        Month,
        DayOfWeek
    }
}
