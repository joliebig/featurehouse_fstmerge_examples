using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("coverageThreshold")]
    public class CoverageThreshold
    {
        public CoverageThreshold()
        {
            ItemType = NCoverItemType.Default;
        }
        [ReflectorProperty("metric")]
        public NCoverMetric Metric { get; set; }
        [ReflectorProperty("value", Required = false)]
        public int MinValue { get; set; }
        [ReflectorProperty("type", Required = false)]
        public NCoverItemType ItemType { get; set; }
        [ReflectorProperty("pattern", Required = false)]
        public string Pattern { get; set; }
        public string ToParamString()
        {
            var builder = new StringBuilder();
            builder.Append(Metric);
            if (MinValue >= 0)
            {
                builder.AppendFormat(":{0}", MinValue);
                if ((ItemType != NCoverItemType.Default) || !string.IsNullOrEmpty(Pattern))
                {
                    builder.AppendFormat(":{0}", ItemType == NCoverItemType.Default ? NCoverItemType.Default : ItemType);
                    if (!string.IsNullOrEmpty(Pattern)) builder.AppendFormat(":{0}", Pattern);
                }
            }
            return builder.ToString();
        }
        public enum NCoverMetric
        {
            SymbolCoverage,
            BranchCoverage,
            MethodCoverage,
            CyclomaticComplexity
        }
        public enum NCoverItemType
        {
            Default,
            View,
            Module,
            Namespace,
            Class
        }
    }
}
