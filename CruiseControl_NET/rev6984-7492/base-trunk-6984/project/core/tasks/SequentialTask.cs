using System;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("sequential")]
    public class SequentialTask
        : TaskContainerBase
    {
        [ReflectorProperty("tasks")]
        public override ITask[] Tasks
        {
            get { return base.Tasks; }
            set { base.Tasks = value; }
        }
        [ReflectorProperty("continueOnFailure", Required = false)]
        public bool ContinueOnFailure { get; set; }
        public ILogger Logger { get; set; }
        protected override bool Execute(IIntegrationResult result)
        {
            var logger = Logger ?? new DefaultLogger();
            var numberOfTasks = Tasks.Length;
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description)
                ? Description
                : string.Format("Running sequential tasks ({0} task(s))", numberOfTasks));
            logger.Info("Starting sequential task with {0} sub-task(s)", numberOfTasks);
            var successCount = 0;
            var failureCount = 0;
            for (var loop = 0; loop < numberOfTasks; loop++)
            {
                var taskName = string.Format("{0} [{1}]", Tasks[loop].GetType().Name, loop);
                logger.Debug("Starting task '{0}'", taskName);
                try
                {
                    var taskResult = result.Clone();
                    var task = Tasks[loop];
                    RunTask(task, taskResult);
                    result.Merge(taskResult);
                }
                catch (Exception error)
                {
                    result.ExceptionResult = error;
                    result.Status = IntegrationStatus.Failure;
                    logger.Warning("Task '{0}' failed!", taskName);
                }
                if (result.Status == IntegrationStatus.Success)
                {
                    successCount++;
                }
                else
                {
                    failureCount++;
                    if (!ContinueOnFailure) break;
                }
            }
            logger.Info("Sequential task completed: {0} successful, {1} failed", successCount, failureCount);
            return true;
        }
    }
}
