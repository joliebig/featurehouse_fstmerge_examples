using System.Xml.XPath;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Publishers.Statistics
{
    [ReflectorType("statistic")]
 public class Statistic
        : StatisticBase
 {
        public Statistic()
        {
        }
        public Statistic(string name, string xpath)
            : base(name, xpath)
        {
        }
 }
}
