namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Collections.Generic;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core;
    using ThoughtWorks.CruiseControl.Core.Config;
    using ThoughtWorks.CruiseControl.Core.Tasks.Conditions;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.Remote.Parameters;
    [ReflectorType("conditional")]
    public class ConditionalTask
        : TaskBase, IConfigurationValidation
    {
        private Dictionary<string, string> parameters;
        private IEnumerable<ParameterBase> parameterDefinitions;
        private ItemStatus mainStatus;
        private ItemStatus elseStatus;
        private Dictionary<ITask, ItemStatus> taskStatuses = new Dictionary<ITask, ItemStatus>();
        private Dictionary<ITask, ItemStatus> elseTaskStatuses = new Dictionary<ITask, ItemStatus>();
        public ConditionalTask()
        {
            this.Tasks = new ITask[0];
            this.ElseTasks = new ITask[0];
        }
        [ReflectorProperty("conditions", Required = true)]
        public ITaskCondition[] TaskConditions { get; set; }
        [ReflectorProperty("tasks", Required = false)]
        public ITask[] Tasks { get; set; }
        [ReflectorProperty("elseTasks", Required = false)]
        public ITask[] ElseTasks { get; set; }
        public ILogger Logger { get; set; }
        public override void ApplyParameters(Dictionary<string, string> parameters,
            IEnumerable<ParameterBase> parameterDefinitions)
        {
            this.parameters = parameters;
            this.parameterDefinitions = parameterDefinitions;
            base.ApplyParameters(parameters, parameterDefinitions);
        }
        public virtual void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            var trace = parent.Wrap(this);
            foreach (var condition in this.TaskConditions)
            {
                var validation = condition as IConfigurationValidation;
                if (validation != null)
                {
                    validation.Validate(
                        configuration,
                        trace,
                        errorProcesser);
                }
            }
            this.ValidateTasks(this.Tasks, configuration, trace, errorProcesser);
            this.ValidateTasks(this.ElseTasks, configuration, trace, errorProcesser);
        }
        public override void InitialiseStatus(ItemBuildStatus newStatus)
        {
            if ((this.CurrentStatus == null) || (this.CurrentStatus.Status != newStatus))
            {
                this.taskStatuses.Clear();
                this.elseTaskStatuses.Clear();
                base.InitialiseStatus(newStatus);
                this.mainStatus = this.InitialiseTaskStatuses(newStatus, this.Tasks, this.taskStatuses, "Tasks (Main)");
                this.elseStatus = this.InitialiseTaskStatuses(newStatus, this.ElseTasks, this.elseTaskStatuses, "Tasks (Else)");
            }
        }
        protected override bool Execute(IIntegrationResult result)
        {
            result.BuildProgressInformation
                .SignalStartRunTask(string.IsNullOrEmpty(this.Description) ? "Running conditional task" : this.Description);
            var logger = this.Logger ?? new DefaultLogger();
            logger.Debug("Checking conditions");
            var conditionsPassed = this.EvaluateConditions(logger, result);
            var successful = true;
            if (conditionsPassed)
            {
                logger.Info("Conditions passed - running tasks");
                this.elseStatus.Status = ItemBuildStatus.Cancelled;
                this.CancelTasks(this.elseTaskStatuses);
                successful = this.RunTasks(this.Tasks, logger, result);
                this.CancelTasks(this.taskStatuses);
                this.mainStatus.Status = successful ? ItemBuildStatus.CompletedSuccess : ItemBuildStatus.CompletedFailed;
            }
            else
            {
                logger.Info("Conditions did not pass - running else tasks");
                this.mainStatus.Status = ItemBuildStatus.Cancelled;
                this.CancelTasks(this.taskStatuses);
                successful = this.RunTasks(this.ElseTasks, logger, result);
                this.CancelTasks(this.elseTaskStatuses);
                this.elseStatus.Status = successful ? ItemBuildStatus.CompletedSuccess : ItemBuildStatus.CompletedFailed;
            }
            return successful;
        }
        private void ValidateTasks(ITask[] tasks,
            IConfiguration configuration,
            ConfigurationTrace parent,
            IConfigurationErrorProcesser errorProcesser)
        {
            if (tasks != null)
            {
                foreach (var task in tasks)
                {
                    var validatorTask = task as IConfigurationValidation;
                    if (validatorTask != null)
                    {
                        validatorTask.Validate(configuration, parent, errorProcesser);
                    }
                }
            }
        }
        private bool EvaluateConditions(ILogger logger, IIntegrationResult result)
        {
            if (this.TaskConditions == null)
            {
                throw new ArgumentNullException();
            }
            var passed = true;
            foreach (ITaskCondition condition in TaskConditions)
            {
                var commonCondition = condition as ConditionBase;
                if (commonCondition != null)
                {
                    commonCondition.Logger = logger;
                }
                passed = condition.Eval(result);
                if (!passed)
                {
                    break;
                }
            }
            return passed;
        }
        private ItemStatus InitialiseTaskStatuses(
            ItemBuildStatus newStatus,
            ITask[] tasks,
            Dictionary<ITask, ItemStatus> taskStatuses,
            string title)
        {
            var groupStatus = new ItemStatus()
            {
                Name = title,
                Status = newStatus,
                TimeCompleted = null,
                TimeOfEstimatedCompletion = null,
                TimeStarted = null
            };
            this.CurrentStatus.AddChild(groupStatus);
            if (tasks != null)
            {
                foreach (ITask task in tasks)
                {
                    ItemStatus taskItem = null;
                    if (task is TaskBase)
                    {
                        (task as TaskBase).InitialiseStatus(newStatus);
                    }
                    if (task is IStatusSnapshotGenerator)
                    {
                        taskItem = (task as IStatusSnapshotGenerator).GenerateSnapshot();
                    }
                    else
                    {
                        taskItem = new ItemStatus(task.GetType().Name);
                        taskItem.Status = newStatus;
                    }
                    if (taskItem != null)
                    {
                        groupStatus.AddChild(taskItem);
                        taskStatuses.Add(task, taskItem);
                    }
                }
            }
            return groupStatus;
        }
        private void CancelTasks(Dictionary<ITask, ItemStatus> taskStatuses)
        {
            foreach (var status in taskStatuses)
            {
                if (status.Key is IStatusItem)
                {
                    var item = status.Key as IStatusItem;
                    item.CancelStatus();
                }
                else if (status.Value.Status == ItemBuildStatus.Pending)
                {
                    status.Value.Status = ItemBuildStatus.Cancelled;
                }
            }
        }
        private void RunTask(ITask task, IIntegrationResult result)
        {
            if (task is IParamatisedItem)
            {
                (task as IParamatisedItem).ApplyParameters(parameters, parameterDefinitions);
            }
            task.Run(result);
        }
        private bool RunTasks(ITask[] tasks, ILogger logger, IIntegrationResult result)
        {
            var successCount = 0;
            var failureCount = 0;
            for (var loop = 0; loop < tasks.Length; loop++)
            {
                var taskName = string.Format("{0} [{1}]", tasks[loop].GetType().Name, loop);
                logger.Debug("Starting task '{0}'", taskName);
                try
                {
                    var taskResult = result.Clone();
                    var task = tasks[loop];
                    this.RunTask(task, taskResult);
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
                }
            }
            logger.Info("Tasks completed: {0} successful, {1} failed", successCount, failureCount);
            return failureCount == 0;
        }
    }
}
