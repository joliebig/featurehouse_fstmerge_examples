using Exortech.NetReflector;
using System;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("directValue")]
    public class DirectDynamicValue
        : IDynamicValue
    {
        private string propertyName;
        private string parameterName;
        private string defaultValue;
        public DirectDynamicValue() { }
        public DirectDynamicValue(string parameter, string property)
        {
            propertyName = property;
            parameterName = parameter;
        }
        [ReflectorProperty("property")]
        public string PropertyName
        {
            get { return propertyName; }
            set { propertyName = value; }
        }
        [ReflectorProperty("parameter")]
        public string ParameterName
        {
            get { return parameterName; }
            set { parameterName = value; }
        }
        [ReflectorProperty("default", Required = false)]
        public string DefaultValue
        {
            get { return defaultValue; }
            set { defaultValue = value; }
        }
        public virtual void ApplyTo(object value, Dictionary<string, string> parameters, IEnumerable<ParameterBase> parameterDefinitions)
        {
            DynamicValueUtility.PropertyValue property = DynamicValueUtility.FindProperty(value, propertyName);
            if (property != null)
            {
                string parameterValue = defaultValue;
                if (parameters.ContainsKey(parameterName))
                {
                    parameterValue = parameters[parameterName];
                }
                var actualValue = DynamicValueUtility.ConvertValue(parameterName, parameterValue, parameterDefinitions);
                property.ChangeProperty(actualValue);
            }
        }
    }
}
