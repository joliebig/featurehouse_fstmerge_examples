using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.WebDashboard.Dashboard.Actions;
namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard.GenericPlugins
{
    [ReflectorType("htmlReportPlugin")]
    public class HtmlReportPlugin
        : ProjectConfigurableBuildPlugin
    {
        private readonly IActionInstantiator actionInstantiator;
        private string description = "no description set";
        private string actionName = "NoActionSet";
        public HtmlReportPlugin(IActionInstantiator actionInstantiator)
  {
   this.actionInstantiator = actionInstantiator;
        }
        [ReflectorProperty("description")]
        public string ConfiguredLinkDescription
        {
            get { return description; }
            set { description = value; }
        }
        public override string LinkDescription
        {
            get { return description; }
        }
        [ReflectorProperty("actionName")]
        public string ActionName
        {
            get { return actionName; }
            set { actionName = value; }
        }
        [ReflectorProperty("htmlFileName")]
        public string HtmlFileName { get; set; }
        public override INamedAction[] NamedActions
        {
            get
            {
                HtmlReportAction buildAction = (HtmlReportAction)actionInstantiator.InstantiateAction(typeof(HtmlReportAction));
                buildAction.HtmlFileName = HtmlFileName;
                return new INamedAction[] { new ImmutableNamedAction(ActionName, buildAction) };
            }
        }
    }
}
