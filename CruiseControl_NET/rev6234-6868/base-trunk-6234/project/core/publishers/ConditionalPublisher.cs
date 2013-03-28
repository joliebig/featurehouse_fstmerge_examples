using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Core.Tasks;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    [ReflectorType("conditionalPublisher")]
    public class ConditionalPublisher
        : TaskContainerBase
    {
        [ReflectorProperty("publishers")]
        public override ITask[] Tasks
        {
            get { return base.Tasks; }
            set { base.Tasks = value; }
        }
        [ReflectorProperty("conditions", Required = true)]
        public IntegrationStatus[] Conditions { get; set; }
        public ILogger Logger { get; set; }
        protected override bool Execute(IIntegrationResult result)
        {
            var logger = Logger ?? new DefaultLogger();
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description)
                ? Description
                : "Checking conditional publishers");
            logger.Info("Checking conditional publishers");
            var runPublishers = false;
            foreach (var condition in Conditions ?? new IntegrationStatus[0])
            {
                runPublishers |= (condition == result.Status);
            }
            if (runPublishers)
            {
                logger.Info("Conditions met - running publishers");
                for (var loop = 0; loop < Conditions.Length; loop++)
                {
                    var publisher = Tasks[loop];
                    logger.Debug("Running publisher #{0}", loop);
                    try
                    {
                        RunTask(publisher, result);
                    }
                    catch (Exception e)
                    {
                        logger.Error("Publisher threw exception: {0}", e.Message);
                    }
                }
            }
            else
            {
                logger.Info("Conditions not met - publishers not run");
            }
            return true;
        }
    }
}
