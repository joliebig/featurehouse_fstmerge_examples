using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core
{
 public interface IIntegratable
 {
  IIntegrationResult Integrate(IntegrationRequest request);
 }
}
