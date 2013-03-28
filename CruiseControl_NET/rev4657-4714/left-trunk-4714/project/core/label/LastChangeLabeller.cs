using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Label
{
 [ReflectorType("lastChangeLabeller")]
 public class LastChangeLabeller : ILabeller
 {
  [ReflectorProperty("prefix", Required=false)]
  public string LabelPrefix = string.Empty;
  public virtual string Generate(IIntegrationResult resultFromThisBuild)
  {
   int changeNumber = resultFromThisBuild.LastChangeNumber;
   Log.Debug(string.Format("Last change number is \"{0}\"", changeNumber));
   if (changeNumber != 0)
    return LabelPrefix + changeNumber;
   else
    return LabelPrefix + "unknown";
  }
  public void Run(IIntegrationResult result)
  {
   result.Label = Generate(result);
  }
 }
}
