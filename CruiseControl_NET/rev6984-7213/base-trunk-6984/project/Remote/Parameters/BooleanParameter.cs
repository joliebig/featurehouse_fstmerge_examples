using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Xml.Serialization;
using System.Text.RegularExpressions;
namespace ThoughtWorks.CruiseControl.Remote.Parameters
{
    [ReflectorType("booleanParameter")]
    [Serializable]
    public class BooleanParameter
        : ParameterBase
    {
        private bool myIsRequired = false;
        private NameValuePair trueValue;
        private NameValuePair falseValue;
        public BooleanParameter()
            : base()
        {
        }
        public BooleanParameter(string name)
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
        [ReflectorProperty("true", typeof(NameValuePairSerialiserFactory))]
        [XmlElement("true")]
        public virtual NameValuePair TrueValue
        {
            get { return trueValue; }
            set { trueValue = value; }
        }
        [ReflectorProperty("false", typeof(NameValuePairSerialiserFactory))]
        [XmlElement("false")]
        public virtual NameValuePair FalseValue
        {
            get { return falseValue; }
            set { falseValue = value; }
        }
        public override Type DataType
        {
            get { return typeof(string); }
        }
        [XmlElement("allowedValue")]
        public override string[] AllowedValues
        {
            get
            {
                return new string[] {
                        string.IsNullOrEmpty(TrueValue.Name) ? TrueValue.Value : TrueValue.Name,
                        string.IsNullOrEmpty(FalseValue.Name) ? FalseValue.Value : FalseValue.Name
                    };
            }
        }
        public override Exception[] Validate(string value)
        {
            List<Exception> exceptions = new List<Exception>();
            if (string.IsNullOrEmpty(value))
            {
                if (IsRequired) exceptions.Add(GenerateException("Value of '{name}' is required"));
            }
            return exceptions.ToArray();
        }
        public override object Convert(string value)
        {
            var testValue = value;
            var actualValue = value;
            if ((testValue == TrueValue.Name) || (testValue == TrueValue.Value)) actualValue = TrueValue.Value;
            if ((testValue == FalseValue.Name) || (testValue == FalseValue.Value)) actualValue = FalseValue.Value;
            return actualValue;
        }
    }
}
