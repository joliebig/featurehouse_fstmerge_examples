namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.Telelogic
{
    using System;
    using Exortech.NetReflector;
    [ReflectorType("synergyProject")]
 public class SynergyProjectInfo
 {
  public const int DefaultTaskFolder = 0;
  public const string DefaultPurpose = "Integration Testing";
        public SynergyProjectInfo()
        {
            this.TaskFolder = DefaultTaskFolder;
            this.BaseliningEnabled = false;
            this.TemplateEnabled = false;
            this.Purpose = DefaultPurpose;
        }
        [ReflectorProperty("release")]
        public string Release { get; set; }
        [ReflectorProperty("projectSpecification")]
        public string ProjectSpecification { get; set; }
  public string ObjectName;
  public string WorkAreaPath;
        [ReflectorProperty("taskFolder", Required = false)]
        public int TaskFolder { get; set; }
        [ReflectorProperty("baseline", Required = false)]
        public bool BaseliningEnabled { get; set; }
        [ReflectorProperty("template", Required = false)]
        public bool TemplateEnabled { get; set; }
        [ReflectorProperty("reconcile", Required = false)]
        public string[] ReconcilePaths { get; set; }
        [ReflectorProperty("purpose", Required = false)]
        public string Purpose { get; set; }
  public DateTime LastReconfigureTime = DateTime.MinValue;
 }
}
