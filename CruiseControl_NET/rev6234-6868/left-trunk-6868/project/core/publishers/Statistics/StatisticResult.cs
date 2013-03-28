using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Publishers.Statistics
{
    public class StatisticResult
    {
        private readonly string statName;
        private readonly object value;
        public StatisticResult(string statName, object value)
        {
            this.statName = statName;
            this.value = value;
        }
        public string StatName
        {
            get { return statName; }
        }
        public object Value
        {
            get { return value; }
        }
    }
}
