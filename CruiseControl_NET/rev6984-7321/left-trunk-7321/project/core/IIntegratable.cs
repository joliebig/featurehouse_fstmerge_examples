using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core
{
    public interface IIntegratable
    {
        IIntegrationResult StartNewIntegration(IntegrationRequest request);
        IIntegrationResult Integrate(IntegrationRequest request);
    }
}
