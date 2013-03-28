namespace ThoughtWorks.CruiseControl.Core
{
 public interface ILabeller : ITask
 {
  string Generate(IIntegrationResult integrationResult);
 }
}
