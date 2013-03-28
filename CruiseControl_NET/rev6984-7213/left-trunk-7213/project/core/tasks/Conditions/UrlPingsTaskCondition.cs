namespace ThoughtWorks.CruiseControl.Core.Tasks.Conditions
{
    using System.Globalization;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Config;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("urlPingCondition")]
    public class UrlPingsTaskCondition
        : ConditionBase, IConfigurationValidation
    {
        [ReflectorProperty("url", Required = true)]
        public string Url { get; set; }
        public IWebFunctions WebFunctions { get; set; }
        public void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            if (string.IsNullOrEmpty(this.Url))
            {
                errorProcesser.ProcessError("URL cannot be empty");
            }
        }
        protected override bool Evaluate(IIntegrationResult result)
        {
            this.LogDescriptionOrMessage("Pinging URL '" + this.Url + "'");
            var functions = this.WebFunctions ?? new DefaultWebFunctions();
            var exists = functions.PingUrl(this.Url);
            return exists;
        }
    }
}
