using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    [ReflectorType("variable")]
    public class EnvironmentVariable
    {
        private string my_value = null;
        [ReflectorProperty("name", Required = true)]
        public string name { get; set; }
        [ReflectorProperty("value", Required = false)]
        public string value
        {
            get { return (my_value == null) ?string.Empty : my_value; }
            set { my_value = value; }
        }
    }
}
