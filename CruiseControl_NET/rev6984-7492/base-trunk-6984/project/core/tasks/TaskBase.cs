using System;
using System.Collections.Generic;
using System.Xml;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    public abstract class TaskBase
        : IParamatisedItem, IStatusSnapshotGenerator, ITask
    {
        private IDynamicValue[] myDynamicValues = new IDynamicValue[0];
        private ItemStatus currentStatus;
        private List<TimeSpan> elapsedTimes = new List<TimeSpan>();
        [ReflectorProperty("dynamicValues", Required = false)]
        public IDynamicValue[] DynamicValues
        {
            get { return myDynamicValues; }
            set { myDynamicValues = value;}
        }
        public virtual string Name
        {
            get { return GetType().Name; }
        }
        [ReflectorProperty("description", Required = false)]
        public string Description { get; set; }
        public ItemStatus CurrentStatus
        {
            get { return currentStatus; }
        }
        public bool WasSuccessful { get; private set; }
        public virtual void Run(IIntegrationResult result)
        {
            this.WasSuccessful = false;
            InitialiseStatus();
            currentStatus.Status = ItemBuildStatus.Running;
            currentStatus.TimeOfEstimatedCompletion = CalculateEstimatedTime();
            currentStatus.TimeStarted = DateTime.Now;
            try
            {
                this.WasSuccessful = Execute(result);
            }
            catch (Exception error)
            {
                currentStatus.Error = error.Message;
                result.Status = IntegrationStatus.Exception;
                throw;
            }
            finally
            {
                currentStatus.Status = (this.WasSuccessful) ? ItemBuildStatus.CompletedSuccess : ItemBuildStatus.CompletedFailed;
                currentStatus.TimeCompleted = DateTime.Now;
                switch (result.Status)
                {
                    case IntegrationStatus.Unknown:
                    case IntegrationStatus.Success:
                        result.Status = this.WasSuccessful ? IntegrationStatus.Success : IntegrationStatus.Failure;
                        break;
                }
            }
        }
        public virtual DateTime? CalculateEstimatedTime()
        {
            double seconds = 0;
            if (elapsedTimes.Count > 0)
            {
                for (var loop = 0; loop < elapsedTimes.Count; loop++)
                {
                    seconds += elapsedTimes[loop].TotalSeconds;
                }
                seconds /= elapsedTimes.Count;
            }
            return seconds > 0 ? (DateTime?)DateTime.Now.AddSeconds(seconds) : null;
        }
        public virtual ItemStatus GenerateSnapshot()
        {
            if (currentStatus == null) InitialiseStatus();
            return currentStatus;
        }
        public virtual void ApplyParameters(Dictionary<string, string> parameters, IEnumerable<ParameterBase> parameterDefinitions)
        {
            if (myDynamicValues != null)
            {
                foreach (IDynamicValue value in myDynamicValues)
                {
                    value.ApplyTo(this, parameters, parameterDefinitions);
                }
            }
        }
        [ReflectionPreprocessor]
        public virtual XmlNode PreprocessParameters(NetReflectorTypeTable typeTable, XmlNode inputNode)
        {
            return DynamicValueUtility.ConvertXmlToDynamicValues(typeTable, inputNode);
        }
        public virtual string RetrieveDescriptionOrName()
        {
            var value = string.IsNullOrEmpty(Description) ? Name : Description;
            return value;
        }
        public virtual void InitialiseStatus()
        {
            if (currentStatus != null)
            {
                var elapsedTime = currentStatus.TimeCompleted - currentStatus.TimeStarted;
                if (elapsedTime.HasValue)
                {
                    if (elapsedTimes.Count >= 8)
                    {
                        elapsedTimes.RemoveAt(7);
                    }
                    elapsedTimes.Insert(0, elapsedTime.Value);
                }
            }
            currentStatus = new ItemStatus()
            {
                Name = Name,
                Description = Description,
                Status = ItemBuildStatus.Pending,
                TimeCompleted = null,
                TimeOfEstimatedCompletion = null,
                TimeStarted = null
            };
        }
        protected abstract bool Execute(IIntegrationResult result);
    }
}
