using System;
namespace ThoughtWorks.CruiseControl.Core.Distribution
{
    public interface IBuildMachine
    {
        string Name { get; }
        void Initialise();
        void Terminate();
        bool CanBuild(IProject project);
        RemoteBuildRequest StartBuild(
            IProject project,
            IIntegrationResult result,
            Action<RemoteBuildRequest> buildCompleted);
        void CancelBuild(string identifier);
    }
}
