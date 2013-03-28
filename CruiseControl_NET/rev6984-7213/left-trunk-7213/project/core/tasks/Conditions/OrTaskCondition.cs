namespace ThoughtWorks.CruiseControl.Core.Tasks.Conditions
{
    using System.Globalization;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Config;
    [ReflectorType("orCondition")]
    public class OrTaskCondition
        : ConditionBase, IConfigurationValidation
    {
        [ReflectorProperty("conditions", Required = true)]
        public ITaskCondition[] Conditions { get; set; }
        protected override bool Evaluate(IIntegrationResult result)
        {
            this.LogDescriptionOrMessage("Performing OR check - " +
                this.Conditions.Length.ToString(CultureInfo.InvariantCulture) +
                " conditions to check");
            var evaluationResult = false;
            foreach (var condition in this.Conditions)
            {
                evaluationResult = condition.Eval(result);
                if (evaluationResult)
                {
                    break;
                }
            }
            return evaluationResult;
        }
        public void Validate(IConfiguration configuration,
            ConfigurationTrace parent,
            IConfigurationErrorProcesser errorProcesser)
        {
            if (this.Conditions.Length == 0)
            {
                errorProcesser
                    .ProcessError("Validation failed for orCondition - at least one child condition must be supplied");
            }
        }
    }
}
