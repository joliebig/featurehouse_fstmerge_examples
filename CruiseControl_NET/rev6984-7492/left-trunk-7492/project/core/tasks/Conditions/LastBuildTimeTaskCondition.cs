namespace ThoughtWorks.CruiseControl.Core.Tasks.Conditions
{
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.Core.Util;
    using System;
    [ReflectorType("lastBuildTimeCondition")]
    public class LastBuildTimeTaskCondition
        : ConditionBase
    {
        [ReflectorProperty("time", typeof(TimeoutSerializerFactory), Required = true)]
        public Timeout Time { get; set; }
        protected override bool Evaluate(IIntegrationResult result)
        {
            this.LogDescriptionOrMessage(
                "Checking last build time - checking build was at least " +
                (this.Time.Millis / 1000) + "s ago");
            if (result.IsInitial())
            {
                return true;
            }
            else
            {
                var checkTime = DateTime.Now.AddMilliseconds(-this.Time.Millis);
                var isTrue = checkTime > result.LastIntegration.StartTime;
                return isTrue;
            }
        }
    }
}
