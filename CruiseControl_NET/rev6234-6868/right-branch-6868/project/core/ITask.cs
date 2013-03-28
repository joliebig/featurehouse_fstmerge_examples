using System.ComponentModel;
namespace ThoughtWorks.CruiseControl.Core
{
 [TypeConverter(typeof(ExpandableObjectConverter))]
 public interface ITask
 {
  void Run(IIntegrationResult result);
 }
}
