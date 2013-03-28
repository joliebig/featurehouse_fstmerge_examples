using System.Collections.Generic;
using System.Xml;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Tasks;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Core.Label
{
    public abstract class LabellerBase
        : ILabeller, IParamatisedItem
    {
        [ReflectorProperty("dynamicValues", Required = false)]
        public IDynamicValue[] DynamicValues { get; set; }
        public abstract string Generate(IIntegrationResult integrationResult);
        public virtual void Run(IIntegrationResult result)
        {
            result.Label = Generate(result);
        }
        public virtual void ApplyParameters(Dictionary<string, string> parameters, IEnumerable<ParameterBase> parameterDefinitions)
        {
            if (DynamicValues != null)
            {
                foreach (var value in DynamicValues)
                {
                    value.ApplyTo(this, parameters, parameterDefinitions);
                }
            }
        }
        [ReflectionPreprocessor]
        public virtual XmlNode PreprocessParameters(XmlNode inputNode)
        {
            return DynamicValueUtility.ConvertXmlToDynamicValues(inputNode);
        }
    }
}
