using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using System.Xml;
using ThoughtWorks.CruiseControl.Core.Tasks;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    public abstract class SourceControlBase
        : ISourceControl, IParamatisedItem
    {
        private IDynamicValue[] myDynamicValues = new IDynamicValue[0];
        [ReflectorProperty("dynamicValues", Required = false)]
        public IDynamicValue[] DynamicValues
        {
            get { return myDynamicValues; }
            set { myDynamicValues = value; }
        }
        public abstract Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to);
        public abstract void LabelSourceControl(IIntegrationResult result);
        public abstract void GetSource(IIntegrationResult result);
        public abstract void Initialize(IProject project);
        public abstract void Purge(IProject project);
        public virtual void ApplyParameters(Dictionary<string, string> parameters, IEnumerable<ParameterBase> parameterDefinitions)
        {
            if (myDynamicValues != null)
            {
                foreach (IDynamicValue value in myDynamicValues)
                {
                    value.ApplyTo(this, parameters, parameterDefinitions);
                }
            }
        }
        [ReflectionPreprocessor]
        public virtual XmlNode PreprocessParameters(NetReflectorTypeTable typeTable, XmlNode inputNode)
        {
            return DynamicValueUtility.ConvertXmlToDynamicValues(typeTable, inputNode);
        }
    }
}
