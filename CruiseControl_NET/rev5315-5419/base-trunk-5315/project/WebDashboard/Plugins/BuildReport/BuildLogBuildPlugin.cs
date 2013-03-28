using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.WebDashboard.Dashboard;
using ThoughtWorks.CruiseControl.WebDashboard.Dashboard.GenericPlugins;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.BuildReport
{
 [ReflectorType("buildLogBuildPlugin")]
 public class BuildLogBuildPlugin : ProjectConfigurableBuildPlugin
 {
  private readonly IActionInstantiator actionInstantiator;
  public BuildLogBuildPlugin(IActionInstantiator actionInstantiator)
  {
   this.actionInstantiator = actionInstantiator;
  }
  public override string LinkDescription
  {
   get { return "View Build Log"; }
  }
  public override INamedAction[] NamedActions
  {
   get
   {
    return new INamedAction[]
     {
      new ImmutableNamedAction(HtmlBuildLogAction.ACTION_NAME, actionInstantiator.InstantiateAction(typeof (HtmlBuildLogAction)))
     };
   }
  }
 }
}
