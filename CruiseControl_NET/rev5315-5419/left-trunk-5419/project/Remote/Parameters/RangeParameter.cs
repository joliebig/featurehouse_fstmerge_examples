using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Parameters
{
    [ReflectorType("rangeParameter")]
    [Serializable]
    public class RangeParameter
        : ParameterBase
    {
        private bool myIsRequired = false;
        private string[] myAllowedValues = new string[0];
        public RangeParameter()
            : base()
        {
        }
        public RangeParameter(string name)
            : base(name)
        {
        }
        [ReflectorProperty("required", Required = false)]
        [XmlAttribute("required")]
        [DefaultValue(false)]
        public virtual bool IsRequired
        {
            get { return myIsRequired; }
            set { myIsRequired = value; }
        }
        public override Type DataType
        {
            get { return typeof(string); }
        }
        [ReflectorArray("allowedValues")]
        [XmlElement("value")]
        public virtual string[] DataValues
        {
            get { return myAllowedValues; }
            set { myAllowedValues = value; }
        }
        [XmlIgnore]
        public override string[] AllowedValues
        {
            get { return myAllowedValues; }
        }
        public override Exception[] Validate(string value)
        {
            List<Exception> exceptions = new List<Exception>();
            if (string.IsNullOrEmpty(value))
            {
                if (IsRequired) exceptions.Add(GenerateException("Value of '{name}' is required"));
            }
            else
            {
                bool isAllowed = false;
                foreach (string allowedValue in myAllowedValues)
                {
                    if (allowedValue == value)
                    {
                        isAllowed = true;
                        break;
                    }
                }
                if (!isAllowed) exceptions.Add(GenerateException("Value of '{name}' is not an allowed value"));
            }
            return exceptions.ToArray();
        }
    }
}
