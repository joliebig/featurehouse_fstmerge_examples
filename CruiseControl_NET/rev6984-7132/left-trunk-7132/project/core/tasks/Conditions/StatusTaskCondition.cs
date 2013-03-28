namespace ThoughtWorks.CruiseControl.Core.Tasks.Conditions
{
    using System.Globalization;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Remote;
    [ReflectorType("statusCondition")]
    public class StatusTaskCondition
        : ConditionBase
    {
        [ReflectorProperty("value", Required = true)]
        public IntegrationStatus Status { get; set; }
        protected override bool Evaluate(IIntegrationResult result)
        {
            this.LogDescriptionOrMessage("Checking status - matching to " + this.Status.ToString());
            return this.Status == result.Status;
        }
    }
}
