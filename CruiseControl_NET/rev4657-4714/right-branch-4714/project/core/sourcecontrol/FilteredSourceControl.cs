using System;
using System.Collections;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 [ReflectorType("filtered")]
 public class FilteredSourceControl : ISourceControl
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
  public Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   Modification[] allModifications = _realScProvider.GetModifications(from_, to);
   ArrayList acceptedModifications = new ArrayList();
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
   return (Modification[]) acceptedModifications.ToArray(typeof (Modification));
  }
  public void LabelSourceControl(IIntegrationResult result)
  {
   _realScProvider.LabelSourceControl(result);
  }
  public void GetSource(IIntegrationResult result)
  {
   _realScProvider.GetSource(result);
  }
  public void Initialize(IProject project)
  {
            _realScProvider.Initialize(project);
  }
  public void Purge(IProject project)
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
 }
}
