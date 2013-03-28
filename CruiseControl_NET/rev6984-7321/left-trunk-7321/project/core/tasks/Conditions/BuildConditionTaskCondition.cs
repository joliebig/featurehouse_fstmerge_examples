namespace ThoughtWorks.CruiseControl.Core.Tasks.Conditions
{
    using System.Globalization;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Remote;
    [ReflectorType("buildCondition")]
    public class BuildConditionTaskCondition
        : ConditionBase
    {
        [ReflectorProperty("value", Required = true)]
        public BuildCondition BuildCondition { get; set; }
        protected override bool Evaluate(IIntegrationResult result)
        {
            this.LogDescriptionOrMessage("Checking build condition - matching to " + this.BuildCondition.ToString());
            return this.BuildCondition == result.BuildCondition;
        }
    }
}
