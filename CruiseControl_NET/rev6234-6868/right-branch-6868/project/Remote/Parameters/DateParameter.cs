using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Xml.Serialization;
using System.Text.RegularExpressions;
namespace ThoughtWorks.CruiseControl.Remote.Parameters
{
    [ReflectorType("dateParameter")]
    [Serializable]
    public class DateParameter
        : ParameterBase
    {
        private static Regex dayOfWeekRegex = new Regex("(?<=dayofweek\\()[0-6](?=\\))", RegexOptions.IgnoreCase);
        private static Regex dayOfMonthRegex = new Regex("(?<=dayofmonth\\()[1-9][0-9]?(?=\\))", RegexOptions.IgnoreCase);
        private DateTime myMinValue = DateTime.MinValue;
        private DateTime myMaxValue = DateTime.MaxValue;
        private bool myIsRequired = false;
        private string myClientDefault;
        public DateParameter()
            : base()
        {
        }
        public DateParameter(string name)
            : base(name)
        {
        }
        [ReflectorProperty("minimum", Required = false)]
        [XmlAttribute("minimum")]
        public virtual DateTime MinimumValue
        {
            get { return myMinValue; }
            set { myMinValue = value; }
        }
        [ReflectorProperty("maximum", Required = false)]
        [XmlAttribute("maximum")]
        public virtual DateTime MaximumValue
        {
            get { return myMaxValue; }
            set { myMaxValue = value; }
        }
        [ReflectorProperty("required", Required = false)]
        [XmlAttribute("required")]
        [DefaultValue(false)]
        public virtual bool IsRequired
        {
            get { return myIsRequired; }
            set { myIsRequired = value; }
        }
        public override Type DataType
        {
            get { return typeof(DateTime); }
        }
        public override string[] AllowedValues
        {
            get { return null; }
        }
        [XmlElement("default")]
        public override string ClientDefaultValue
        {
            get { return myClientDefault; }
            set { myClientDefault = value; }
        }
        public override Exception[] Validate(string value)
        {
            List<Exception> exceptions = new List<Exception>();
            DateTime actualValue;
            if (string.IsNullOrEmpty(value))
            {
                if (IsRequired) exceptions.Add(GenerateException("Value of '{name}' is required"));
            }
            else
            {
                if (DateTime.TryParse(value, out actualValue))
                {
                    if (actualValue < myMinValue)
                    {
                        exceptions.Add(
                            GenerateException("Value of '{name}' is less than the minimum allowed ({0})",
                                    myMinValue));
                    }
                    if (actualValue > myMaxValue)
                    {
                        exceptions.Add(
                            GenerateException("Value of '{name}' is more than the maximum allowed ({0})",
                                    myMaxValue));
                    }
                }
                else
                {
                    exceptions.Add(GenerateException("Value of '{name}' is not date"));
                }
            }
            return exceptions.ToArray();
        }
        public override void GenerateClientDefault()
        {
            SetClientDefault();
        }
        public override object Convert(string value)
        {
            var actualValue = CalculateDate(value);
            return actualValue;
        }
        private void SetClientDefault()
        {
            myClientDefault = CalculateDate(DefaultValue).ToShortDateString();
        }
        private DateTime CalculateDate(string value)
        {
            DateTime date;
            if (DefaultValue.StartsWith("today", StringComparison.CurrentCultureIgnoreCase))
            {
                date = CalculateOperation(value.Substring(5), DateTime.Today);
            }
            else if (dayOfWeekRegex.IsMatch(value))
            {
                var day = dayOfWeekRegex.Match(value).Value;
                var diff = (int)DateTime.Today.DayOfWeek;
                date = DateTime.Today.AddDays(System.Convert.ToInt32(day) - diff);
                date = CalculateOperation(value.Substring(12), date);
            }
            else if (dayOfMonthRegex.IsMatch(value))
            {
                var day = dayOfMonthRegex.Match(value);
                date = new DateTime(DateTime.Now.Year, DateTime.Now.Month, System.Convert.ToInt32(day.Value));
                date = CalculateOperation(value.Substring(day.Value.Length + 12), date);
            }
            else
            {
                date = DateTime.Parse(value);
            }
            return date;
        }
        private DateTime CalculateOperation(string operation, DateTime baseDate)
        {
            DateTime date = baseDate;
            if (!string.IsNullOrEmpty(operation))
            {
                var number = System.Convert.ToInt32(operation.Substring(1));
                var op = operation.Substring(0, 1);
                switch (op)
                {
                    case "+":
                        date = date.AddDays(number);
                        break;
                    case "-":
                        date = date.AddDays(-number);
                        break;
                    default:
                        throw new InvalidOperationException("Unknown operation: '" + op + "'");
                }
            }
            return date;
        }
    }
}
