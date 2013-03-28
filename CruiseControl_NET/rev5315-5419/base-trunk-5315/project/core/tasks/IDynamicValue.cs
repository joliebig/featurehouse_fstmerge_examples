using System;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    public interface IDynamicValue
    {
        void ApplyTo(object value, Dictionary<string, string> parameters);
    }
}
