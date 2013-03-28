using System;
using System.ComponentModel;
namespace ThoughtWorks.CruiseControl.Remote
{
 [TypeConverter(typeof (ExpandableObjectConverter))]
 public interface ITrigger
 {
  void IntegrationCompleted();
  DateTime NextBuild { get; }
  IntegrationRequest Fire();
 }
}
