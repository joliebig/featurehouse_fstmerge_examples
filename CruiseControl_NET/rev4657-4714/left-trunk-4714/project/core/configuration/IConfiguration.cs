using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Core.Config;
namespace ThoughtWorks.CruiseControl.Core
{
 public interface IConfiguration
 {
  IProjectList Projects { get; }
        List<IQueueConfiguration> QueueConfigurations { get; }
        IQueueConfiguration FindQueueConfiguration(string name);
  void AddProject(IProject project);
  void DeleteProject(string name);
 }
}
