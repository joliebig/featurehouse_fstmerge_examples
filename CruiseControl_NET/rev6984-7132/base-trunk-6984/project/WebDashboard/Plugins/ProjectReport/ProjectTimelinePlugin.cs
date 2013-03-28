namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.ProjectReport
{
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.WebDashboard.Dashboard;
    [ReflectorType("projectTimelinePlugin")]
    public class ProjectTimelinePlugin
        : IPlugin
    {
        private readonly IActionInstantiator actionInstantiator;
        public ProjectTimelinePlugin(IActionInstantiator actionInstantiator)
        {
            this.actionInstantiator = actionInstantiator;
        }
        public string LinkDescription
        {
            get { return "Project Timeline"; }
        }
        public INamedAction[] NamedActions
        {
            get
            {
                var action = actionInstantiator.InstantiateAction(typeof(ProjectTimelineAction));
                return new INamedAction[]
     {
      new ImmutableNamedAction(ProjectTimelineAction.TimelineActionName, action),
      new ImmutableNamedAction(ProjectTimelineAction.DataActionName, action)
     };
            }
        }
    }
}
