using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Core.State;
namespace ThoughtWorks.CruiseControl.Core.Queues
{
    public interface IQueueManagerFactory
    {
        IQueueManager Create(IProjectIntegratorListFactory projectIntegratorListFactory,
            IConfiguration configuration,
            IProjectStateManager stateManager);
    }
}
