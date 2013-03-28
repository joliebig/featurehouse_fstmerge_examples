namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using Exortech.NetReflector;
    [ReflectorType("header")]
    public class HttpRequestHeader
    {
        [ReflectorProperty("name", Required = true)]
        public string Name { get; set; }
        [ReflectorProperty("value", Required = true)]
        public string Value { get; set; }
    }
}
