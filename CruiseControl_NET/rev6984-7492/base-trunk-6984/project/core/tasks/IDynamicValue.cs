using System;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    public interface IDynamicValue
    {
        void ApplyTo(object value, Dictionary<string, string> parameters, IEnumerable<ParameterBase> parameterDefinitions);
    }
}
