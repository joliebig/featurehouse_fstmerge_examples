namespace ThoughtWorks.CruiseControl.Core.Triggers.NCrontab
{
    using System;
    using System.Collections;
    using System.Globalization;
    using System.IO;
    [ Serializable ]
    public sealed class CrontabField : ICrontabField
    {
        private readonly BitArray _bits;
        private int _minValueSet;
        private int _maxValueSet;
        private readonly CrontabFieldImpl _impl;
        public static CrontabField Parse(CrontabFieldKind kind, string expression)
        {
            return new CrontabField(CrontabFieldImpl.FromKind(kind), expression);
        }
        public static CrontabField Minutes(string expression)
        {
            return new CrontabField(CrontabFieldImpl.Minute, expression);
        }
        public static CrontabField Hours(string expression)
        {
            return new CrontabField(CrontabFieldImpl.Hour, expression);
        }
        public static CrontabField Days(string expression)
        {
            return new CrontabField(CrontabFieldImpl.Day, expression);
        }
        public static CrontabField Months(string expression)
        {
            return new CrontabField(CrontabFieldImpl.Month, expression);
        }
        public static CrontabField DaysOfWeek(string expression)
        {
            return new CrontabField(CrontabFieldImpl.DayOfWeek, expression);
        }
        private CrontabField(CrontabFieldImpl impl, string expression)
        {
            if (impl == null)
                throw new ArgumentNullException("impl");
            _impl = impl;
            _bits = new BitArray(impl.ValueCount);
            _bits.SetAll(false);
            _minValueSet = int.MaxValue;
            _maxValueSet = -1;
            _impl.Parse(expression, Accumulate);
        }
        public int GetFirst()
        {
            return _minValueSet < int.MaxValue ? _minValueSet : -1;
        }
        public int Next(int start)
        {
            if (start < _minValueSet)
                return _minValueSet;
            var startIndex = ValueToIndex(start);
            var lastIndex = ValueToIndex(_maxValueSet);
            for (var i = startIndex; i <= lastIndex; i++)
            {
                if (_bits[i])
                    return IndexToValue(i);
            }
            return -1;
        }
        private int IndexToValue(int index)
        {
            return index + _impl.MinValue;
        }
        private int ValueToIndex(int value)
        {
            return value - _impl.MinValue;
        }
        public bool Contains(int value)
        {
            return _bits[ValueToIndex(value)];
        }
        private void Accumulate(int start, int end, int interval)
        {
            var minValue = _impl.MinValue;
            var maxValue = _impl.MaxValue;
            if (start == end)
            {
                if (start < 0)
                {
                    if (interval <= 1)
                    {
                        _minValueSet = minValue;
                        _maxValueSet = maxValue;
                        _bits.SetAll(true);
                        return;
                    }
                    start = minValue;
                    end = maxValue;
                }
                else
                {
                    if (start < minValue)
                    {
                        throw new CrontabException(string.Format(
                            "'{0} is lower than the minimum allowable value for this field. Value must be between {1} and {2} (all inclusive).",
                            start, _impl.MinValue, _impl.MaxValue));
                    }
                    if (start > maxValue)
                    {
                        throw new CrontabException(string.Format(
                            "'{0} is higher than the maximum allowable value for this field. Value must be between {1} and {2} (all inclusive).",
                            end, _impl.MinValue, _impl.MaxValue));
                    }
                }
            }
            else
            {
                if (start > end)
                {
                    end ^= start;
                    start ^= end;
                    end ^= start;
                }
                if (start < 0)
                {
                    start = minValue;
                }
                else if (start < minValue)
                {
                    throw new CrontabException(string.Format(
                        "'{0} is lower than the minimum allowable value for this field. Value must be between {1} and {2} (all inclusive).",
                        start, _impl.MinValue, _impl.MaxValue));
                }
                if (end < 0)
                {
                    end = maxValue;
                }
                else if (end > maxValue)
                {
                    throw new CrontabException(string.Format(
                        "'{0} is higher than the maximum allowable value for this field. Value must be between {1} and {2} (all inclusive).",
                        end, _impl.MinValue, _impl.MaxValue));
                }
            }
            if (interval < 1)
                interval = 1;
            int i;
            for (i = start - minValue; i <= (end - minValue); i += interval)
                _bits[i] = true;
            if (_minValueSet > start)
                _minValueSet = start;
            i += (minValue - interval);
            if (_maxValueSet < i)
                _maxValueSet = i;
        }
        public override string ToString()
        {
            return ToString(null);
        }
        public string ToString(string format)
        {
            var writer = new StringWriter(CultureInfo.InvariantCulture);
            switch (format)
            {
                case "G":
                case null:
                    Format(writer, true);
                    break;
                case "N":
                    Format(writer);
                    break;
                default:
                    throw new FormatException();
            }
            return writer.ToString();
        }
        public void Format(TextWriter writer)
        {
            Format(writer, false);
        }
        public void Format(TextWriter writer, bool noNames)
        {
            _impl.Format(this, writer, noNames);
        }
    }
}
