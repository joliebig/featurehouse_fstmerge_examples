using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Parameters
{
    [ReflectorType("numericParameter")]
    [Serializable]
    public class NumericParameter
        : ParameterBase
    {
        private double myMinValue = double.MinValue;
        private double myMaxValue = double.MaxValue;
        private bool myIsRequired = false;
        public NumericParameter()
            : base()
        {
        }
        public NumericParameter(string name)
            : base(name)
        {
        }
        [ReflectorProperty("minimum", Required = false)]
        [XmlAttribute("minimum")]
        [DefaultValue(double.MinValue)]
        public virtual double MinimumValue
        {
            get { return myMinValue; }
            set { myMinValue = value; }
        }
        [ReflectorProperty("maximum", Required = false)]
        [XmlAttribute("maximum")]
        [DefaultValue(double.MaxValue)]
        public virtual double MaximumValue
        {
            get { return myMaxValue; }
            set { myMaxValue = value; }
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
            get { return typeof(double); }
        }
        public override string[] AllowedValues
        {
            get { return null; }
        }
        public override Exception[] Validate(string value)
        {
            List<Exception> exceptions = new List<Exception>();
            double actualValue;
            if (string.IsNullOrEmpty(value))
            {
                if (IsRequired) exceptions.Add(GenerateException("Value of '{name}' is required"));
            }
            else
            {
                if (double.TryParse(value, out actualValue))
                {
                    if (actualValue < myMinValue)
                    {
                        exceptions.Add(
                            GenerateException("Value of '{name}' is less than the minimum allowed ({0})",
                                    myMinValue));
                    }
                    if (actualValue > myMaxValue)
                    {
                        exceptions.Add(
                            GenerateException("Value of '{name}' is more than the maximum allowed ({0})",
                                    myMaxValue));
                    }
                }
                else
                {
                    exceptions.Add(GenerateException("Value of '{name}' is not numeric"));
                }
            }
            return exceptions.ToArray();
        }
    }
}
