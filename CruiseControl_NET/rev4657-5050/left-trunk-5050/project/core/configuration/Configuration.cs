using System;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Core.Config
{
 public class Configuration : IConfiguration
 {
  private ProjectList projects = new ProjectList();
        private List<IQueueConfiguration> queueConfigurations = new List<IQueueConfiguration>();
        public virtual List<IQueueConfiguration> QueueConfigurations
        {
            get { return queueConfigurations; }
        }
  public void AddProject(IProject project)
  {
   projects.Add(project);
  }
        public virtual IQueueConfiguration FindQueueConfiguration(string name)
        {
            IQueueConfiguration actualConfig = null;
            foreach (IQueueConfiguration config in queueConfigurations)
            {
                if (string.Equals(config.Name, name, StringComparison.InvariantCultureIgnoreCase))
                {
                    actualConfig = config;
                    break;
                }
            }
            if (actualConfig == null) actualConfig = new DefaultQueueConfiguration(name);
            return actualConfig;
        }
  public void DeleteProject(string name)
  {
   projects.Delete(name);
  }
  public IProjectList Projects
  {
   get { return projects; }
  }
 }
}
