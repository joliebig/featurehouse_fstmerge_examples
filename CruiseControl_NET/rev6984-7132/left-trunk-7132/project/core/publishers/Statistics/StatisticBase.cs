namespace ThoughtWorks.CruiseControl.Core.Publishers.Statistics
{
    using System;
    using System.Collections.Generic;
    using System.Text;
    using Exortech.NetReflector;
    using System.Xml.XPath;
    public abstract class StatisticBase
    {
        protected string name;
        protected string xpath;
        private bool generateGraph;
        private bool include = true;
        public StatisticBase()
        {
        }
        public StatisticBase(string name, string xpath)
        {
            this.name = name;
            this.xpath = xpath;
        }
        [ReflectorProperty("xpath")]
        public string Xpath
        {
            get { return xpath; }
            set { xpath = value; }
        }
        [ReflectorProperty("name")]
        public string Name
        {
            get { return name; }
            set { name = value; }
        }
        [ReflectorProperty("generateGraph", Required = false)]
        public bool GenerateGraph
        {
            get { return generateGraph; }
            set { generateGraph = value; }
        }
        [ReflectorProperty("include", Required = false)]
        public bool Include
        {
            get { return include; }
            set { include = value; }
        }
        public StatisticResult Apply(XPathNavigator nav)
        {
            object value = Evaluate(nav);
            return new StatisticResult(name, value);
        }
        protected virtual object Evaluate(XPathNavigator nav)
        {
            return nav.Evaluate(xpath);
        }
        public override bool Equals(object obj)
        {
            var o = (StatisticBase)obj;
            return name.Equals(o.Name);
        }
        public override int GetHashCode()
        {
            return name.GetHashCode();
        }
    }
}
