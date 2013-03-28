using Exortech.NetReflector;
using System;
namespace ThoughtWorks.CruiseControl.Core.Config
{
    [ReflectorType("queue")]
    public class DefaultQueueConfiguration
        : IQueueConfiguration, IConfigurationValidation
    {
        private string name;
        private QueueDuplicateHandlingMode handlingMode = QueueDuplicateHandlingMode.UseFirst;
        private string lockQueueNames;
        public DefaultQueueConfiguration() { }
        public DefaultQueueConfiguration(string name)
        {
            this.name = name;
        }
        [ReflectorProperty("name", Required = true)]
        public virtual string Name
        {
            get { return name; }
            set { name = value; }
        }
        [ReflectorProperty("duplicates", Required = false)]
        public virtual QueueDuplicateHandlingMode HandlingMode
        {
            get { return handlingMode; }
            set { handlingMode = value; }
        }
        [ReflectorProperty("lockqueues", Required = false)]
        public virtual string LockQueueNames
        {
            get { return lockQueueNames; }
            set { lockQueueNames = value; }
        }
        public virtual void Validate(IConfiguration configuration, object parent)
        {
            bool queueFound = false;
            foreach (IProject projectDef in configuration.Projects)
            {
                if (string.Equals(this.Name, projectDef.QueueName, StringComparison.InvariantCulture))
                {
                    queueFound = true;
                    break;
                }
            }
            if (!queueFound)
            {
                throw new ConfigurationException(
                    string.Format("An unused queue definition has been found: name '{0}'", this.Name));
            }
        }
    }
}
