using System;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Core
{
    public interface IParamatisedProject
    {
        void Prebuild(IIntegrationResult result, Dictionary<string, string> parameterValues);
        void Run(IIntegrationResult result, Dictionary<string, string> parameterValues);
        void PublishResults(IIntegrationResult result, Dictionary<string, string> parameterValues);
        List<ParameterBase> ListBuildParameters();
        void ValidateParameters(Dictionary<string, string> parameters);
    }
}
