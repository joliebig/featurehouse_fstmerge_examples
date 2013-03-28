namespace ThoughtWorks.CruiseControl.Core.Config
{
    public sealed class ConfigurationTrace
    {
        public ConfigurationTrace(object value, ConfigurationTrace parent)
        {
            this.Value = value;
            this.Parent = parent;
        }
        public object Value { get; private set; }
        public ConfigurationTrace Parent { get; private set; }
        public TType GetAncestorValue<TType>()
            where TType : class
        {
            var ancestor = this.FindAncestor<TType>();
            if (ancestor != null)
            {
                return (TType)ancestor.Value;
            }
            else
            {
                return null;
            }
        }
        public ConfigurationTrace FindAncestor<TType>()
        {
            if ((this.Value != null) && (this.Value.GetType() == typeof(TType)))
            {
                return this;
            }
            else
            {
                return null;
            }
        }
        public ConfigurationTrace Wrap(object value)
        {
            var trace = new ConfigurationTrace(value, this);
            return trace;
        }
        public static ConfigurationTrace Start(object value)
        {
            var trace = new ConfigurationTrace(value, null);
            return trace;
        }
    }
}
