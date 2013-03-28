using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Xml.Serialization;
using System.IO;
using System.Text.RegularExpressions;
namespace ThoughtWorks.CruiseControl.Remote.Parameters
{
    [ReflectorType("selectParameter")]
    [Serializable]
    public class SelectParameter
        : ParameterBase
    {
        private bool myIsRequired = false;
        private NameValuePair[] myAllowedValues = { };
        private string myClientDefault;
        public SelectParameter()
            : base()
        {
        }
        public SelectParameter(string name)
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
        [ReflectorProperty("sourceFile", Required = false)]
        [XmlIgnore]
        public virtual string SourceFile { get; set; }
        [ReflectorProperty("allowedValues", typeof(NameValuePairListSerialiserFactory), Required = false)]
        [XmlIgnore]
        public virtual NameValuePair[] DataValues
        {
            get { return myAllowedValues; }
            set
            {
                myAllowedValues = value;
                SetClientDefault();
            }
        }
        [ReflectorProperty("default", Required = false)]
        [XmlIgnore]
        public override string DefaultValue
        {
            get { return base.DefaultValue; }
            set
            {
                base.DefaultValue = value;
                SetClientDefault();
            }
        }
        [XmlElement("allowedValue")]
        public override string[] AllowedValues
        {
            get
            {
                var values = new List<string>();
                foreach (var value in myAllowedValues)
                {
                    if (string.IsNullOrEmpty(value.Name))
                    {
                        values.Add(value.Value);
                    }
                    else
                    {
                        values.Add(value.Name);
                    }
                }
                return values.ToArray();
            }
        }
        [XmlElement("default")]
        public override string ClientDefaultValue
        {
            get { return myClientDefault; }
            set { myClientDefault = value; }
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
                foreach (var allowedValue in myAllowedValues)
                {
                    if ((string.IsNullOrEmpty(allowedValue.Name) && (allowedValue.Value == value)) ||
                        (!string.IsNullOrEmpty(allowedValue.Name) && (allowedValue.Name == value)))
                    {
                        isAllowed = true;
                        break;
                    }
                }
                if (!isAllowed) exceptions.Add(GenerateException("Value of '{name}' is not an allowed value"));
            }
            return exceptions.ToArray();
        }
        public override object Convert(string value)
        {
            var testValue = value;
            var actualValue = value;
            foreach (var valueToCheck in myAllowedValues)
            {
                if ((testValue == valueToCheck.Name) ||
                    (string.IsNullOrEmpty(valueToCheck.Name) && (testValue == valueToCheck.Value)))
                {
                    actualValue = valueToCheck.Value;
                    break;
                }
            }
            return actualValue;
        }
        public override void GenerateClientDefault()
        {
            if (!string.IsNullOrEmpty(SourceFile))
            {
                using (var reader = File.OpenText(SourceFile))
                {
                    var currentLine = reader.ReadLine();
                    var values = new List<NameValuePair>();
                    while (currentLine != null)
                    {
                        currentLine = currentLine.Trim();
                        if (currentLine.Length > 0) values.Add(new NameValuePair(null, currentLine));
                        currentLine = reader.ReadLine();
                    }
                    myAllowedValues = values.ToArray();
                }
            }
        }
        private void SetClientDefault()
        {
            myClientDefault = DefaultValue;
            foreach (var value in myAllowedValues)
            {
                if (!string.IsNullOrEmpty(value.Name) && (DefaultValue == value.Value))
                {
                    myClientDefault = value.Name;
                    break;
                }
            }
        }
    }
}
