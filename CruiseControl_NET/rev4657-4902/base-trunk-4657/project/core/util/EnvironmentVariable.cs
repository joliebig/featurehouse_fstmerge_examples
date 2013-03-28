using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    [ReflectorType("variable")]
    public class EnvironmentVariable
    {
        [ReflectorProperty("name", Required = true)]
        public string name;
        private string my_value = null;
        [ReflectorProperty("value", Required = false)]
        public string value
        {
            get { return (my_value == null) ? "" : my_value; }
            set { my_value = value; }
        }
    }
}
