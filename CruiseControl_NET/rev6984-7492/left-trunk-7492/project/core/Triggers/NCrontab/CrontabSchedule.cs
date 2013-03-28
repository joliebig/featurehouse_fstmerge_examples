namespace ThoughtWorks.CruiseControl.Core.Triggers.NCrontab
{
 using System;
 using System.Collections.Generic;
 using System.Globalization;
 using System.IO;
 using Debug = System.Diagnostics.Debug;
    [ Serializable ]
    public sealed class CrontabSchedule
    {
        private readonly CrontabField _minutes;
        private readonly CrontabField _hours;
        private readonly CrontabField _days;
        private readonly CrontabField _months;
        private readonly CrontabField _daysOfWeek;
        private static readonly char[] _separators = new[] {' '};
        public static CrontabSchedule Parse(string expression)
        {
            if (expression == null)
                throw new ArgumentNullException("expression");
            return new CrontabSchedule(expression);
        }
        private CrontabSchedule(string expression)
        {
            Debug.Assert(expression != null);
            var fields = expression.Split(_separators, StringSplitOptions.RemoveEmptyEntries);
            if (fields.Length != 5)
            {
                throw new CrontabException(string.Format(
                    "'{0}' is not a valid crontab expression. It must contain at least 5 components of a schedule "
                    + "(in the sequence of minutes, hours, days, months, days of week).",
                    expression));
            }
            _minutes = CrontabField.Minutes(fields[0]);
            _hours = CrontabField.Hours(fields[1]);
            _days = CrontabField.Days(fields[2]);
            _months = CrontabField.Months(fields[3]);
            _daysOfWeek = CrontabField.DaysOfWeek(fields[4]);
        }
        public IEnumerable<DateTime> GetNextOccurrences(DateTime baseTime, DateTime endTime)
        {
            for (var occurrence = GetNextOccurrence(baseTime, endTime);
                 occurrence < endTime;
                 occurrence = GetNextOccurrence(occurrence, endTime))
            {
                yield return occurrence;
            }
        }
        public DateTime GetNextOccurrence(DateTime baseTime)
        {
            return GetNextOccurrence(baseTime, DateTime.MaxValue);
        }
        public DateTime GetNextOccurrence(DateTime baseTime, DateTime endTime)
        {
            const int nil = -1;
            var baseYear = baseTime.Year;
            var baseMonth = baseTime.Month;
            var baseDay = baseTime.Day;
            var baseHour = baseTime.Hour;
            var baseMinute = baseTime.Minute;
            var endYear = endTime.Year;
            var endMonth = endTime.Month;
            var endDay = endTime.Day;
            var year = baseYear;
            var month = baseMonth;
            var day = baseDay;
            var hour = baseHour;
            var minute = baseMinute + 1;
            minute = _minutes.Next(minute);
            if (minute == nil)
            {
                minute = _minutes.GetFirst();
                hour++;
            }
            hour = _hours.Next(hour);
            if (hour == nil)
            {
                minute = _minutes.GetFirst();
                hour = _hours.GetFirst();
                day++;
            }
            else if (hour > baseHour)
            {
                minute = _minutes.GetFirst();
            }
            day = _days.Next(day);
            RetryDayMonth:
            if (day == nil)
            {
                minute = _minutes.GetFirst();
                hour = _hours.GetFirst();
                day = _days.GetFirst();
                month++;
            }
            else if (day > baseDay)
            {
                minute = _minutes.GetFirst();
                hour = _hours.GetFirst();
            }
            month = _months.Next(month);
            if (month == nil)
            {
                minute = _minutes.GetFirst();
                hour = _hours.GetFirst();
                day = _days.GetFirst();
                month = _months.GetFirst();
                year++;
            }
            else if (month > baseMonth)
            {
                minute = _minutes.GetFirst();
                hour = _hours.GetFirst();
                day = _days.GetFirst();
            }
            var dateChanged = day != baseDay || month != baseMonth || year != baseYear;
            if (day > 28 && dateChanged && day > Calendar.GetDaysInMonth(year, month))
            {
                if (year >= endYear && month >= endMonth && day >= endDay)
                    return endTime;
                day = nil;
                goto RetryDayMonth;
            }
            var nextTime = new DateTime(year, month, day, hour, minute, 0, 0, baseTime.Kind);
            if (nextTime >= endTime)
                return endTime;
            if (_daysOfWeek.Contains((int) nextTime.DayOfWeek))
                return nextTime;
            return GetNextOccurrence(new DateTime(year, month, day, 23, 59, 0, 0, baseTime.Kind), endTime);
        }
        public override string ToString()
        {
            var writer = new StringWriter(CultureInfo.InvariantCulture);
            _minutes.Format(writer, true); writer.Write(' ');
            _hours.Format(writer, true); writer.Write(' ');
            _days.Format(writer, true); writer.Write(' ');
            _months.Format(writer, true); writer.Write(' ');
            _daysOfWeek.Format(writer, true);
            return writer.ToString();
        }
        private static Calendar Calendar
        {
            get { return CultureInfo.InvariantCulture.Calendar; }
        }
    }
}
