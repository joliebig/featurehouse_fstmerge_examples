using System;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    public interface IParamatisedTask
    {
        void ApplyParameters(Dictionary<string, string> parameters);
    }
}
