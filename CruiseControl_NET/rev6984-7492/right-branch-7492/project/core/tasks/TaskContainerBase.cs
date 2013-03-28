using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using ThoughtWorks.CruiseControl.Remote;
using System;
using ThoughtWorks.CruiseControl.Core.Config;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    public abstract class TaskContainerBase
        : TaskBase, IConfigurationValidation
    {
        private Dictionary<string, string> parameters;
        private IEnumerable<ParameterBase> parameterDefinitions;
        private Dictionary<ITask, ItemStatus> taskStatuses = new Dictionary<ITask, ItemStatus>();
        public virtual ITask[] Tasks { get; set; }
        public override void ApplyParameters(Dictionary<string, string> parameters, IEnumerable<ParameterBase> parameterDefinitions)
        {
            this.parameters = parameters;
            this.parameterDefinitions = parameterDefinitions;
            base.ApplyParameters(parameters, parameterDefinitions);
        }
        public virtual void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            if (Tasks != null)
            {
                foreach (var task in Tasks)
                {
                    var validatorTask = task as IConfigurationValidation;
                    if (validatorTask != null)
                    {
                        validatorTask.Validate(configuration, parent.Wrap(this), errorProcesser);
                    }
                }
            }
        }
        public override void InitialiseStatus(ItemBuildStatus newStatus)
        {
            if ((this.CurrentStatus == null) || (this.CurrentStatus.Status != newStatus))
            {
                taskStatuses.Clear();
                base.InitialiseStatus(newStatus);
                if (Tasks != null)
                {
                    foreach (ITask task in Tasks)
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
                            CurrentStatus.AddChild(taskItem);
                            taskStatuses.Add(task, taskItem);
                        }
                    }
                }
            }
        }
        protected virtual void RunTask(ITask task, IIntegrationResult result)
        {
            if (task is IParamatisedItem)
            {
                (task as IParamatisedItem).ApplyParameters(parameters, parameterDefinitions);
            }
            task.Run(result);
        }
        protected void CancelTasks()
        {
            foreach (var status in this.taskStatuses)
            {
                var task = status.Key as IStatusItem;
                if (task != null)
                {
                    task.CancelStatus();
                }
                else if (status.Value.Status == ItemBuildStatus.Pending)
                {
                    status.Value.Status = ItemBuildStatus.Cancelled;
                }
            }
        }
    }
}
