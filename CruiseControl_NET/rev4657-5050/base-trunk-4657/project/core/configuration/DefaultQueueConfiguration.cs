using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Config
{
    [ReflectorType("queue")]
    public class DefaultQueueConfiguration
        : IQueueConfiguration
    {
        private string _name;
        private QueueDuplicateHandlingMode _handlingMode = QueueDuplicateHandlingMode.UseFirst;
        private string _lockQueueNames;
        public DefaultQueueConfiguration() { }
        public DefaultQueueConfiguration(string name)
        {
            _name = name;
        }
        [ReflectorProperty("name", Required = true)]
        public virtual string Name
        {
            get { return _name; }
            set { _name = value.Trim(); }
        }
        [ReflectorProperty("duplicates", Required = false)]
        public virtual QueueDuplicateHandlingMode HandlingMode
        {
            get { return _handlingMode; }
            set { _handlingMode = value; }
        }
        [ReflectorProperty("lockqueues", Required = false)]
        public virtual string LockQueueNames
        {
            get { return _lockQueueNames; }
            set { _lockQueueNames = value; }
        }
    }
}
