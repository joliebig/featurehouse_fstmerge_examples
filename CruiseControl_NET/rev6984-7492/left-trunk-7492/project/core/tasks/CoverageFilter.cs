using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("coverageFilter")]
    public class CoverageFilter
    {
        public CoverageFilter()
        {
            ItemType = NCoverItemType.Default;
        }
        [ReflectorProperty("data")]
        public string Data { get; set; }
        [ReflectorProperty("type", Required = false)]
        public NCoverItemType ItemType { get; set; }
        [ReflectorProperty("regex", Required = false)]
        public bool IsRegex { get; set; }
        [ReflectorProperty("include", Required = false)]
        public bool IsInclude { get; set; }
        public string ToParamString()
        {
            var builder = new StringBuilder();
            builder.Append(Data);
            if (ItemType != NCoverItemType.Default)
            {
                builder.AppendFormat(":{0}", ItemType);
                if (IsRegex || IsInclude)
                {
                    builder.AppendFormat(":{0}", IsRegex ? "true" : "false");
                    builder.AppendFormat(":{0}", IsInclude ? "true" : "false");
                }
            }
            return builder.ToString();
        }
        public enum NCoverItemType
        {
            Default,
            Assembly,
            Namespace,
            Class,
            Method,
            Document
        }
    }
}
