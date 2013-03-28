using Exortech.NetReflector;
using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Parameters
{
    [Serializable]
    [XmlInclude(typeof(TextParameter))]
    [XmlInclude(typeof(RangeParameter))]
    [XmlInclude(typeof(NumericParameter))]
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
        [XmlElement("default")]
        public virtual string DefaultValue
        {
            get { return myDefault; }
            set { myDefault = value; }
        }
        public abstract Type DataType { get; }
        public abstract string[] AllowedValues { get; }
        public abstract Exception[] Validate(string value);
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
