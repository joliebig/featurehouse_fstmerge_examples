using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Util
{
 [ReflectorType("assemblyMatch")]
 public class AssemblyMatch
 {
        public AssemblyMatch()
        {
            this.Expression = string.Empty;
        }
        [ReflectorProperty("expr", Required = true)]
        public string Expression { get; set; }
 }
}
