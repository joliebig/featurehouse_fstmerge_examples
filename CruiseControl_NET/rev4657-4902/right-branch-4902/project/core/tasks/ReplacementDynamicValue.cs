using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("replacementValue")]
    public class ReplacementDynamicValue
        : IDynamicValue
    {
        private string propertyName;
        private NameValuePair[] parameterValues;
        private string formatValue;
        public ReplacementDynamicValue() { }
        public ReplacementDynamicValue(string format, string property, params NameValuePair[] parameters)
        {
            this.formatValue = format;
            this.propertyName = property;
            this.parameterValues = parameters;
        }
        [ReflectorProperty("property")]
        public string PropertyName
        {
            get { return propertyName; }
            set { propertyName = value; }
        }
        [ReflectorArray("parameters")]
        public NameValuePair[] Parameters
        {
            get { return parameterValues; }
            set { parameterValues = value; }
        }
        [ReflectorProperty("format")]
        public string FormatValue
        {
            get { return formatValue; }
            set { formatValue = value; }
        }
        public virtual void ApplyTo(object value, Dictionary<string, string> parameters)
        {
            DynamicValueUtility.PropertyValue property = DynamicValueUtility.FindProperty(value, propertyName);
            if (property != null)
            {
                List<string> actualParameters = new List<string>();
                foreach (NameValuePair parameterName in parameterValues)
                {
                    if (parameters.ContainsKey(parameterName.Name))
                    {
                        actualParameters.Add(parameters[parameterName.Name]);
                    }
                    else
                    {
                        actualParameters.Add(parameterName.Value);
                    }
                }
                string parameterValue = string.Format(this.formatValue,
                    actualParameters.ToArray());
                property.ChangeProperty(parameterValue);
            }
        }
    }
}
