using Exortech.NetReflector;
using System;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    public abstract class TaskBase
        : IParamatisedTask
    {
        private IDynamicValue[] myDynamicValues = new IDynamicValue[0];
        [ReflectorProperty("dynamicValues", Required = false)]
        public IDynamicValue[] DynamicValues
        {
            get { return myDynamicValues; }
            set { myDynamicValues = value;}
        }
        public virtual void ApplyParameters(Dictionary<string, string> parameters)
        {
            if (myDynamicValues != null)
            {
                foreach (IDynamicValue value in myDynamicValues)
                {
                    value.ApplyTo(this, parameters);
                }
            }
        }
    }
}
