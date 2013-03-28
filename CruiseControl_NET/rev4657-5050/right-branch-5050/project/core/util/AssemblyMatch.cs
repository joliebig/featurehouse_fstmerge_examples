using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Util
{
 [ReflectorType("assemblyMatch")]
 public class AssemblyMatch
 {
  [ReflectorProperty("expr", Required = true)]
  public string Expression = string.Empty;
 }
}
