using System;
namespace ThoughtWorks.CruiseControl.Core.State
{
    public interface IProjectStateManager
    {
        void RecordProjectAsStopped(string projectName);
        void RecordProjectAsStartable(string projectName);
        bool CheckIfProjectCanStart(string projectName);
    }
}
