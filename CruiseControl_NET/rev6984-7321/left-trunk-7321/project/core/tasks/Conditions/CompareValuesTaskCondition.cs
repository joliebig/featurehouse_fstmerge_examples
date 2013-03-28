namespace ThoughtWorks.CruiseControl.Core.Tasks.Conditions
{
    using System;
    using System.Globalization;
    using Exortech.NetReflector;
    [ReflectorType("compareCondition")]
    public class CompareValuesTaskCondition
        : ConditionBase
    {
        [ReflectorProperty("value1", Required = true)]
        public string Value1 { get; set; }
        [ReflectorProperty("value2", Required = true)]
        public string Value2 { get; set; }
        [ReflectorProperty("evaluation", Required = true)]
        public Evaluation EvaluationType { get; set; }
        [ReflectorProperty("ignoreCase", Required = false)]
        public bool IgnoreCase { get; set; }
        protected override bool Evaluate(IIntegrationResult result)
        {
            this.LogDescriptionOrMessage("Checking value comparison condition - " + this.Value1 + " with " + this.Value2);
            var evaluation = false;
            switch (this.EvaluationType)
            {
                case Evaluation.Equal:
                    evaluation = string.Compare(this.Value1, this.Value2, this.IgnoreCase, CultureInfo.InvariantCulture) == 0;
                    break;
                case Evaluation.NotEqual:
                    evaluation = string.Compare(this.Value1, this.Value2, this.IgnoreCase, CultureInfo.InvariantCulture) != 0;
                    break;
                default:
                    throw new ArgumentOutOfRangeException("Unhandled evaluation type");
            }
            return evaluation;
        }
        public enum Evaluation
        {
            Equal,
            NotEqual,
        }
    }
}
