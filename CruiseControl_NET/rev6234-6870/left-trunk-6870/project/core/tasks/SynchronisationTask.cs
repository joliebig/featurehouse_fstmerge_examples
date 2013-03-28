namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Collections.Generic;
    using System.Threading;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Config;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    [ReflectorType("synchronised")]
    public class SynchronisationTask
        : TaskContainerBase
    {
        private static Dictionary<string, object> contexts = new Dictionary<string, object>();
        private static object lockObject = new object();
        [ReflectorProperty("tasks")]
        public override ITask[] Tasks
        {
            get { return base.Tasks; }
            set { base.Tasks = value; }
        }
        [ReflectorProperty("continueOnFailure", Required = false)]
        public bool ContinueOnFailure { get; set; }
        public ILogger Logger { get; set; }
        [ReflectorProperty("context", Required = false)]
        public string ContextName { get; set; }
        [ReflectorProperty("timeout", Required = false)]
        public int? TimeoutPeriod { get; set; }
        protected override bool Execute(IIntegrationResult result)
        {
            var logger = Logger ?? new DefaultLogger();
            var numberOfTasks = Tasks.Length;
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description)
                ? Description
                : string.Format("Running tasks in synchronisation context({0} task(s))", numberOfTasks));
            logger.Info("Starting synchronisation task with {0} sub-task(s)", numberOfTasks);
            var contextToUse = this.ContextName ?? "DefaultSynchronisationContext";
            lock (lockObject)
            {
                if (!contexts.ContainsKey(contextToUse))
                {
                    contexts.Add(contextToUse, new object());
                }
            }
            if (Monitor.TryEnter(contexts[contextToUse], TimeoutPeriod.GetValueOrDefault(300) * 1000))
            {
                try
                {
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
                    logger.Info("Parallel task completed: {0} successful, {1} failed", successCount, failureCount);
                }
                finally
                {
                    Monitor.Exit(contexts[contextToUse]);
                }
            }
            else
            {
                logger.Warning("Unable to enter synchonisation context!");
                result.Status = IntegrationStatus.Failure;
            }
            return (result.Status == IntegrationStatus.Success);
        }
    }
}
