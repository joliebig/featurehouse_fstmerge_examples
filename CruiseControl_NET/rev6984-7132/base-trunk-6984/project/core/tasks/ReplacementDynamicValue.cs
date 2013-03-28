using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Parameters;
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
        public virtual void ApplyTo(object value, Dictionary<string, string> parameters, IEnumerable<ParameterBase> parameterDefinitions)
        {
            DynamicValueUtility.PropertyValue property = DynamicValueUtility.FindProperty(value, propertyName);
            if (property != null)
            {
                var actualParameters = new List<object>();
                foreach (NameValuePair parameterName in parameterValues)
                {
                    object actualValue;
                    if (parameters.ContainsKey(parameterName.Name))
                    {
                        var inputValue = parameters[parameterName.Name];
                        actualValue = DynamicValueUtility.ConvertValue(parameterName.Name, inputValue, parameterDefinitions);
                    }
                    else
                    {
                        actualValue = DynamicValueUtility.ConvertValue(parameterName.Name, parameterName.Value, parameterDefinitions);
                    }
                    actualParameters.Add(actualValue);
                }
                string parameterValue = string.Format(this.formatValue,
                    actualParameters.ToArray());
                property.ChangeProperty(parameterValue);
            }
        }
    }
}
