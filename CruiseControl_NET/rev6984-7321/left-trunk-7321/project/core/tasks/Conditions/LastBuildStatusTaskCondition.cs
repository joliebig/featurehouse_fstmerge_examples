namespace ThoughtWorks.CruiseControl.Core.Tasks.Conditions
{
    using System.Globalization;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Remote;
    [ReflectorType("lastStatusCondition")]
    public class LastBuildStatusTaskCondition
        : ConditionBase
    {
        [ReflectorProperty("value", Required = true)]
        public IntegrationStatus Status { get; set; }
        protected override bool Evaluate(IIntegrationResult result)
        {
            this.LogDescriptionOrMessage(
                "Checking last build status - matching to " +
                this.Status.ToString(CultureInfo.InvariantCulture));
            if (result.IsInitial())
            {
                return false;
            }
            else
            {
                return this.Status == result.LastBuildStatus;
            }
        }
    }
}
