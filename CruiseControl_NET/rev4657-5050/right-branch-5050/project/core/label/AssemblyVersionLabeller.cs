using System;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Label
{
 [ReflectorType("assemblyVersionLabeller")]
 public class AssemblyVersionLabeller : ILabeller
 {
  [ReflectorProperty("major", Required = false)]
  public int Major;
  [ReflectorProperty("minor", Required = false)]
  public int Minor;
  [ReflectorProperty("build", Required = false)]
  public int Build = -1;
  [ReflectorProperty("revision", Required = false)]
  public int Revision = -1;
  [ReflectorProperty("incrementOnFailure", Required = false)]
  public bool IncrementOnFailure;
  public string Generate(IIntegrationResult integrationResult)
  {
   Version oldVersion;
   try
   {
    Log.Debug(string.Concat("Old build label is: ", integrationResult.LastIntegration.Label));
    oldVersion = new Version(integrationResult.LastIntegration.Label);
   }
   catch (Exception)
   {
    oldVersion = new Version(0, 0, 0, 0);
   }
   Log.Debug(string.Concat("Old version is: ", oldVersion.ToString()));
   int currentRevision;
   if (Revision > -1)
   {
    currentRevision = Revision;
   }
   else
   {
    currentRevision = integrationResult.LastChangeNumber;
    if (currentRevision == 0 && integrationResult.BuildCondition == BuildCondition.ForceBuild)
     currentRevision = oldVersion.Revision;
   }
   int currentBuild;
   if (Build > -1)
   {
    currentBuild = Build;
   }
   else
   {
    currentBuild = oldVersion.Build;
    if ((Major != oldVersion.Major ||
     Minor != oldVersion.Minor ||
     currentRevision != oldVersion.Revision ||
     integrationResult.BuildCondition == BuildCondition.ForceBuild) &&
     (integrationResult.LastIntegrationStatus == IntegrationStatus.Success || IncrementOnFailure))
    {
     currentBuild++;
    }
   }
   Log.Debug(string.Format(System.Globalization.CultureInfo.InvariantCulture,
         "Major: {0} Minor: {1} Build: {2} Revision: {3}", Major, Minor, currentBuild, currentRevision));
   Version newVersion = new Version(Major, Minor, currentBuild, currentRevision);
   Log.Debug(string.Concat("New version is: ", newVersion.ToString()));
   return newVersion.ToString();
  }
  public void Run(IIntegrationResult result)
  {
   result.Label = Generate(result);
  }
 }
}
