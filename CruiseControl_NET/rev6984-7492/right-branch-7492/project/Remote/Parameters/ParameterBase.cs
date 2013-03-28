using Exortech.NetReflector;
using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Parameters
{
    [Serializable]
    [XmlInclude(typeof(TextParameter))]
    [XmlInclude(typeof(SelectParameter))]
    [XmlInclude(typeof(NumericParameter))]
    [XmlInclude(typeof(DateParameter))]
    [XmlInclude(typeof(BooleanParameter))]
    public abstract class ParameterBase
    {
        private string myName;
        private string myDisplayName = null;
        private string myDescription = null;
        private string myDefault = null;
        public ParameterBase()
        {
        }
        public ParameterBase(string name)
        {
            myName = name;
        }
        [ReflectorProperty("name", Required = true)]
        [XmlAttribute("name")]
        public virtual string Name
        {
            get { return myName; }
            set { myName = value; }
        }
        [ReflectorProperty("display", Required = false)]
        [XmlAttribute("display")]
        public virtual string DisplayName
        {
            get { return myDisplayName ?? myName; }
            set { myDisplayName = value; }
        }
        [ReflectorProperty("description", Required = false)]
        [XmlElement("description")]
        public virtual string Description
        {
            get { return myDescription; }
            set { myDescription = value; }
        }
        [ReflectorProperty("default", Required = false)]
        [XmlIgnore]
        public virtual string DefaultValue
        {
            get { return myDefault; }
            set { myDefault = value; }
        }
        [XmlElement("default")]
        public virtual string ClientDefaultValue
        {
            get { return myDefault; }
            set { myDefault = value; }
        }
        public abstract Type DataType { get; }
        [XmlElement("allowedValue")]
        public abstract string[] AllowedValues { get; }
        public abstract Exception[] Validate(string value);
        public virtual object Convert(string value)
        {
            object actualValue = value;
            if (DataType != typeof(string))
            {
                actualValue = System.Convert.ChangeType(value, DataType);
            }
            return actualValue;
        }
        public virtual void GenerateClientDefault()
        {
        }
        protected virtual Exception GenerateException(string message, params object[] values)
        {
            string actualMessage = message.Replace("{name}", DisplayName);
            Exception exception = new Exception(
                string.Format(actualMessage,
                values));
            return exception;
        }
    }
}
