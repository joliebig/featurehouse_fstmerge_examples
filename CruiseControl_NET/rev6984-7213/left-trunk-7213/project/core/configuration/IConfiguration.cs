namespace ThoughtWorks.CruiseControl.Core
{
    using System.Collections.Generic;
    using ThoughtWorks.CruiseControl.Core.Config;
    using ThoughtWorks.CruiseControl.Core.Distribution;
    using ThoughtWorks.CruiseControl.Core.Security;
    public interface IConfiguration
 {
  IProjectList Projects { get; }
        List<IQueueConfiguration> QueueConfigurations { get; }
        IQueueConfiguration FindQueueConfiguration(string name);
        ISecurityManager SecurityManager { get; }
        IList<IBuildMachine> BuildMachines { get; }
        IList<IBuildAgent> BuildAgents { get; }
        void AddProject(IProject project);
  void DeleteProject(string name);
 }
}
