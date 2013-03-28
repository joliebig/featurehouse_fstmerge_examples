using System;
using System.Collections.Generic;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Core.Tasks;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 [ReflectorType("filtered")]
 public class FilteredSourceControl
        : SourceControlBase
 {
  private ISourceControl _realScProvider;
        private IModificationFilter[] _inclusionFilters = new IModificationFilter[0];
        private IModificationFilter[] _exclusionFilters = new IModificationFilter[0];
  [ReflectorProperty("sourceControlProvider", Required=true, InstanceTypeKey="type")]
  public ISourceControl SourceControlProvider
  {
   get { return _realScProvider; }
   set { _realScProvider = value; }
  }
        [ReflectorProperty("exclusionFilters", Required = false)]
        public IModificationFilter[] ExclusionFilters
        {
   get { return _exclusionFilters; }
   set { _exclusionFilters = value; }
  }
        [ReflectorProperty("inclusionFilters", Required = false)]
        public IModificationFilter[] InclusionFilters
        {
            get { return _inclusionFilters; }
            set { _inclusionFilters = value; }
        }
        public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   Modification[] allModifications = _realScProvider.GetModifications(from_, to);
            var acceptedModifications = new List<Modification>();
   foreach (Modification modification in allModifications)
   {
                if (IsAcceptedByInclusionFilters(modification) &&
                    (!IsAcceptedByExclusionFilters(modification)))
                {
                    Log.Debug(String.Format("Modification {0} was accepted by the filter specification.",
                        modification));
                    acceptedModifications.Add(modification);
                }
                else
                    Log.Debug(String.Format("Modification {0} was not accepted by the filter specification.",
                        modification));
            }
   return acceptedModifications.ToArray();
  }
        public override void LabelSourceControl(IIntegrationResult result)
  {
   _realScProvider.LabelSourceControl(result);
  }
        public override void GetSource(IIntegrationResult result)
  {
   _realScProvider.GetSource(result);
  }
        public override void Initialize(IProject project)
  {
            _realScProvider.Initialize(project);
  }
        public override void Purge(IProject project)
  {
             _realScProvider.Purge(project);
  }
  private bool IsAcceptedByInclusionFilters(Modification m)
  {
   if (_inclusionFilters.Length == 0)
    return true;
   foreach (IModificationFilter mf in _inclusionFilters)
   {
    if (mf.Accept(m))
                {
                    Log.Debug(String.Format("Modification {0} was included by filter {1}.", m, mf));
                    return true;
                }
            }
   return false;
  }
  private bool IsAcceptedByExclusionFilters(Modification m)
  {
            if (_exclusionFilters.Length == 0)
    return false;
   foreach (IModificationFilter mf in _exclusionFilters)
   {
                if (mf.Accept(m))
                {
                    Log.Debug(String.Format("Modification {0} was excluded by filter {1}.", m, mf));
                    return true;
                }
   }
   return false;
  }
        public override void ApplyParameters(Dictionary<string, string> parameters, IEnumerable<ParameterBase> parameterDefinitions)
        {
            base.ApplyParameters(parameters, parameterDefinitions);
            var dynamicChild = _realScProvider as IParamatisedItem;
            if (dynamicChild != null) dynamicChild.ApplyParameters(parameters, parameterDefinitions);
        }
    }
}
