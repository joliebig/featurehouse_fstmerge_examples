namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.ProjectReport
{
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.WebDashboard.Dashboard;
    [ReflectorType("packageListPlugin")]
    public class PackageListPlugin
        : IPlugin
    {
        private readonly IActionInstantiator actionInstantiator;
        public PackageListPlugin(IActionInstantiator actionInstantiator)
        {
            this.actionInstantiator = actionInstantiator;
        }
        public string LinkDescription
        {
            get { return "Package List"; }
        }
        public INamedAction[] NamedActions
        {
            get
            {
                var action = actionInstantiator.InstantiateAction(typeof(PackageListAction));
                return new INamedAction[]
     {
      new ImmutableNamedAction(PackageListAction.ActionName, action)
     };
            }
        }
    }
}
