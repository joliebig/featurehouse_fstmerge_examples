using System.ComponentModel;
namespace ThoughtWorks.CruiseControl.Core.State
{
 [TypeConverter(typeof(ExpandableObjectConverter))]
 public interface IStateManager
 {
  IIntegrationResult LoadState(string project);
  void SaveState(IIntegrationResult result);
  bool HasPreviousState(string project);
 }
}
