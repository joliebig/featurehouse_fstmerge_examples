using System;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.Telelogic
{
 [ReflectorType("synergyProject")]
 public class SynergyProjectInfo
 {
  public const int DefaultTaskFolder = 0;
  public const string DefaultPurpose = "Integration Testing";
  [ReflectorProperty("release")]
  public string Release;
  [ReflectorProperty("projectSpecification")]
  public string ProjectSpecification;
  public string ObjectName;
  public string WorkAreaPath;
  [ReflectorProperty("taskFolder", Required=false)]
  public int TaskFolder = DefaultTaskFolder;
  [ReflectorProperty("baseline", Required=false)]
  public bool BaseliningEnabled = false;
  [ReflectorProperty("template", Required=false)]
  public bool TemplateEnabled = false;
  [ReflectorArray("reconcile", Required=false)]
  public string[] ReconcilePaths;
  [ReflectorProperty("purpose", Required=false)]
  public string Purpose = DefaultPurpose;
  public DateTime LastReconfigureTime = DateTime.MinValue;
 }
}
