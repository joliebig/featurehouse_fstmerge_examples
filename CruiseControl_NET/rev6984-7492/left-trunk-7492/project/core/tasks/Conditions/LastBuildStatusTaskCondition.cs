using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Tasks.Conditions
{
    [ReflectorType("lastStatusCondition")]
    public class LastBuildStatusTaskCondition
        : ConditionBase
    {
        [ReflectorProperty("value", Required = true)]
        public IntegrationStatus Status { get; set; }
        protected override bool Evaluate(IIntegrationResult result)
        {
            LogDescriptionOrMessage("Checking last build status - matching to " + Status);
            if (result.IsInitial())
            {
                return false;
            }
            else
            {
                return Status == result.LastBuildStatus;
            }
        }
    }
}
