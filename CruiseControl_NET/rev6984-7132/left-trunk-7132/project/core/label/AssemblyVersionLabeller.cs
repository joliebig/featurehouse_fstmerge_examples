namespace ThoughtWorks.CruiseControl.Core.Label
{
    using System;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
 [ReflectorType("assemblyVersionLabeller")]
 public class AssemblyVersionLabeller
        : LabellerBase
    {
        public AssemblyVersionLabeller()
        {
            this.Build = -1;
            this.Revision = -1;
        }
        [ReflectorProperty("major", Required = false)]
        public int Major { get; set; }
        [ReflectorProperty("minor", Required = false)]
        public int Minor { get; set; }
        [ReflectorProperty("build", Required = false)]
        public int Build { get; set; }
        [ReflectorProperty("revision", Required = false)]
        public int Revision { get; set; }
        [ReflectorProperty("incrementOnFailure", Required = false)]
        public bool IncrementOnFailure { get; set; }
  public override string Generate(IIntegrationResult integrationResult)
  {
   Version oldVersion;
   try
   {
                Log.Debug(string.Concat("[assemblyVersionLabeller] Old build label is: ", integrationResult.LastIntegration.Label));
    oldVersion = new Version(integrationResult.LastIntegration.Label);
   }
   catch (Exception)
   {
    oldVersion = new Version(0, 0, 0, 0);
   }
            Log.Debug(string.Concat("[assemblyVersionLabeller] Old version is: ", oldVersion.ToString()));
   int currentRevision = 0;
   if (Revision > -1)
   {
    currentRevision = Revision;
   }
   else
   {
                if (int.TryParse(integrationResult.LastChangeNumber, out currentRevision))
                {
                    Log.Debug(
                        string.Format("[assemblyVersionLabeller] LastChangeNumber retrieved: {0}",
                        currentRevision));
                }
                else
                {
                    Log.Debug("[assemblyVersionLabeller] LastChangeNumber of source control is '{0}', set revision number to '0'.",
                              string.IsNullOrEmpty(integrationResult.LastChangeNumber)
                                  ? "N/A"
                                  : integrationResult.LastChangeNumber);
                }
    if (currentRevision <= 0) currentRevision = oldVersion.Revision;
   }
   int currentBuild;
   if (Build > -1)
   {
    currentBuild = Build;
                Log.Debug("[assemblyVersionLabeller] Build number ist set to '{0}' via configuration.", Build);
   }
   else
   {
    currentBuild = oldVersion.Build;
                if (integrationResult.LastIntegration.Status == IntegrationStatus.Success || IncrementOnFailure)
    {
     currentBuild++;
    }
                else
    {
        Log.Debug(
            "[assemblyVersionLabeller] Not increasing build number because the integration is not succeeded and 'incrementOnFailure' property is set to 'false'.");
    }
   }
   Log.Debug(string.Format(System.Globalization.CultureInfo.InvariantCulture,
                                    "[assemblyVersionLabeller] Major: {0} Minor: {1} Build: {2} Revision: {3}", Major, Minor, currentBuild, currentRevision));
   Version newVersion = new Version(Major, Minor, currentBuild, currentRevision);
            Log.Debug(string.Concat("[assemblyVersionLabeller] New version is: ", newVersion.ToString()));
   return newVersion.ToString();
  }
 }
}
