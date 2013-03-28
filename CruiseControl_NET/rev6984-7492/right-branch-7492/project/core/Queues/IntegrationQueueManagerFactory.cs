using System;
using ThoughtWorks.CruiseControl.Core.State;
namespace ThoughtWorks.CruiseControl.Core.Queues
{
    public class IntegrationQueueManagerFactory :
        IQueueManagerFactory
    {
        private static IQueueManagerFactory managerFactory = new IntegrationQueueManagerFactory();
        public virtual IQueueManager Create(IProjectIntegratorListFactory projectIntegratorListFactory,
            IConfiguration configuration,
            IProjectStateManager stateManager)
        {
            IQueueManager integrationQueueManager = new IntegrationQueueManager(projectIntegratorListFactory, configuration, stateManager);
            return integrationQueueManager;
        }
        public static void OverrideFactory(IQueueManagerFactory newFactory)
        {
            managerFactory = newFactory;
        }
        public static void ResetFactory()
        {
            managerFactory = new IntegrationQueueManagerFactory();
        }
        public static IQueueManager CreateManager(IProjectIntegratorListFactory projectIntegratorListFactory,
            IConfiguration configuration,
            IProjectStateManager stateManager)
        {
            return managerFactory.Create(projectIntegratorListFactory, configuration, stateManager);
        }
    }
}
