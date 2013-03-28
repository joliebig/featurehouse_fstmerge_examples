namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    public interface ITaskCondition
    {
        bool Eval(IIntegrationResult result);
    }
}
