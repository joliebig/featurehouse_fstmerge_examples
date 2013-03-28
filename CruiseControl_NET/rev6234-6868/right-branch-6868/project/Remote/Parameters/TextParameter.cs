using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.Xml.Serialization;
using System.ComponentModel;
namespace ThoughtWorks.CruiseControl.Remote.Parameters
{
    [ReflectorType("textParameter")]
    [XmlRoot("textParameter")]
    [Serializable]
    public class TextParameter
        : ParameterBase
    {
        private int myMinLength = 0;
        private int myMaxLength = int.MaxValue;
        private bool myIsRequired = false;
        public TextParameter()
            : base()
        {
        }
        public TextParameter(string name)
            : base(name)
        {
        }
        [ReflectorProperty("minimum", Required = false)]
        [XmlAttribute("minimum")]
        [DefaultValue(0)]
        public virtual int MinimumLength
        {
            get { return myMinLength; }
            set { myMinLength = value; }
        }
        [ReflectorProperty("maximum", Required = false)]
        [XmlAttribute("maximum")]
        [DefaultValue(int.MaxValue)]
        public virtual int MaximumLength
        {
            get { return myMaxLength; }
            set { myMaxLength = value; }
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
        public override string[] AllowedValues
        {
            get { return null; }
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
                if (value.Length < myMinLength)
                {
                    exceptions.Add(
                        GenerateException("Value of '{name}' is less than the minimum length ({0})",
                                myMinLength));
                }
                if (value.Length > myMaxLength)
                {
                    exceptions.Add(
                        GenerateException("Value of '{name}' is more than the maximum length ({0})",
                                myMaxLength));
                }
            }
            return exceptions.ToArray();
        }
    }
}
