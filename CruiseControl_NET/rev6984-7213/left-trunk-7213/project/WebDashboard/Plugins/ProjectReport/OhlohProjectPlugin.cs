namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.ProjectReport
{
    using System.Collections;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.WebDashboard.Dashboard;
    using ThoughtWorks.CruiseControl.WebDashboard.IO;
    using ThoughtWorks.CruiseControl.WebDashboard.MVC;
    using ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise;
    using ThoughtWorks.CruiseControl.WebDashboard.MVC.View;
    using ThoughtWorks.CruiseControl.WebDashboard.ServerConnection;
    [ReflectorType("ohlohProjectPlugin")]
    public class OhlohProjectPlugin
        : ICruiseAction, IPlugin
    {
        private const string ActionName = "ViewOhlohProjectStats";
        private readonly IFarmService farmService;
        private readonly IVelocityViewGenerator viewGenerator;
        public OhlohProjectPlugin(IFarmService farmService, IVelocityViewGenerator viewGenerator)
  {
   this.farmService = farmService;
   this.viewGenerator = viewGenerator;
        }
        public string LinkDescription
        {
            get { return "View Ohloh Stats"; }
        }
        public INamedAction[] NamedActions
        {
            get { return new INamedAction[] { new ImmutableNamedAction(ActionName, this) }; }
        }
        public IResponse Execute(ICruiseRequest request)
  {
            var ohloh = farmService.GetLinkedSiteId(request.ProjectSpecifier,
                request.RetrieveSessionToken(),
                "ohloh");
            if (string.IsNullOrEmpty(ohloh))
            {
                return new HtmlFragmentResponse("<div>This project has not been linked to a project in Ohloh</div>");
            }
            else
            {
                var velocityContext = new Hashtable();
                velocityContext["ohloh"] = ohloh;
                velocityContext["projectName"] = request.ProjectName;
                return viewGenerator.GenerateView(@"OhlohStats.vm", velocityContext);
            }
  }
    }
}
