namespace ThoughtWorks.CruiseControl.Core.Label
{
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.State;
 [ReflectorType("stateFileLabeller")]
 public class StateFileLabeller
        : LabellerBase
 {
  private readonly IStateManager stateManager;
  public StateFileLabeller() : this(new FileStateManager())
  {}
  public StateFileLabeller(IStateManager stateManager)
  {
   this.stateManager = stateManager;
  }
        [ReflectorProperty("project")]
        public string Project { get; set; }
  public override string Generate(IIntegrationResult integrationResult)
  {
   return stateManager.LoadState(Project).LastSuccessfulIntegrationLabel;
  }
 }
}
