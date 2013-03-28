namespace ThoughtWorks.CruiseControl.Core.Config
{
    using System;
    public interface IConfigurationValidation
    {
        void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser);
    }
}
