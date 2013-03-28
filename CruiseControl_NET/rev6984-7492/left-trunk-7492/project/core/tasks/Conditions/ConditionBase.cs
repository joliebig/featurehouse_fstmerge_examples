namespace ThoughtWorks.CruiseControl.Core.Tasks.Conditions
{
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Tasks;
    using ThoughtWorks.CruiseControl.Core.Util;
    public abstract class ConditionBase
        : ITaskCondition
    {
        [ReflectorProperty("description", Required = false)]
        public string Description { get; set; }
        public ILogger Logger { get; set; }
        public virtual bool Eval(IIntegrationResult result)
        {
            var evaluation = this.Evaluate(result);
            return evaluation;
        }
        protected abstract bool Evaluate(IIntegrationResult result);
        protected ILogger RetrieveLogger()
        {
            if (this.Logger == null)
            {
                this.Logger = new DefaultLogger();
            }
            return this.Logger;
        }
        protected void LogDescriptionOrMessage(string message)
        {
            var logger = this.RetrieveLogger();
            if (string.IsNullOrEmpty(this.Description))
            {
                logger.Debug(message);
            }
            else
            {
                logger.Info(this.Description);
            }
        }
    }
}
