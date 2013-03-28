using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Core.Security;
namespace ThoughtWorks.CruiseControl.Core
{
 public interface IConfiguration
 {
  IProjectList Projects { get; }
        List<IQueueConfiguration> QueueConfigurations { get; }
        IQueueConfiguration FindQueueConfiguration(string name);
        ISecurityManager SecurityManager { get; }
  void AddProject(IProject project);
  void DeleteProject(string name);
 }
}
