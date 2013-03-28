namespace ThoughtWorks.CruiseControl.Core.Tasks.Conditions
{
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Config;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("urlHeaderValueCondition")]
    public class UrlHeaderValueTaskCondition
        : ConditionBase, IConfigurationValidation
    {
        [ReflectorProperty("url", Required = true)]
        public string Url { get; set; }
        [ReflectorProperty("key", Required = true)]
        public string HeaderKey { get; set; }
        [ReflectorProperty("value", Required = true)]
        public string HeaderValue { get; set; }
        public IWebFunctions WebFunctions { get; set; }
        public void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            if (string.IsNullOrEmpty(this.Url))
            {
                errorProcesser.ProcessError("URL cannot be empty");
            }
            if (string.IsNullOrEmpty(this.HeaderKey))
            {
                errorProcesser.ProcessError("Header Key cannot be empty");
            }
        }
        protected override bool Evaluate(IIntegrationResult result)
        {
            this.LogDescriptionOrMessage(
                "Pinging URL '" + this.Url +
                "' and checking the value for header '" + this.HeaderKey + "'");
            var functions = this.WebFunctions ?? new DefaultWebFunctions();
            var exists = functions.PingAndValidateHeaderValue(this.Url, this.HeaderKey, this.HeaderValue);
            return exists;
        }
    }
}
