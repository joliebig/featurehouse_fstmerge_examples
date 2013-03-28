namespace ThoughtWorks.CruiseControl.Core.Distribution
{
    public interface IBuildAgent
    {
        INetReflectorConfigurationReader ConfigurationReader { get; set; }
        void Initialise();
        void Terminate();
    }
}
