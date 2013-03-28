using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise;
using ThoughtWorks.CruiseControl.WebDashboard.Dashboard;
using ThoughtWorks.CruiseControl.WebDashboard.MVC;
using ThoughtWorks.CruiseControl.WebDashboard.IO;
using ThoughtWorks.CruiseControl.WebDashboard.ServerConnection;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.ServerReport
{
    public class MessageHandlerPlugin
        : ICruiseAction, IPlugin
    {
        public const string ActionName = "RawXmlMessage";
        private readonly IFarmService farmService;
        public MessageHandlerPlugin(IFarmService farmService)
        {
            this.farmService = farmService;
        }
        public INamedAction[] NamedActions
        {
            get
            {
                return new INamedAction[] {
                    new ImmutableNamedActionWithoutSiteTemplate(ActionName, this)
                };
            }
        }
        public string LinkDescription
        {
            get { return "Process XML message"; }
        }
        public IResponse Execute(ICruiseRequest cruiseRequest)
        {
            string action = cruiseRequest.Request.GetText("action");
            string message = cruiseRequest.Request.GetText("message");
            string response = farmService.ProcessMessage(cruiseRequest.ServerSpecifier,
                action,
                message);
            return new XmlFragmentResponse(response);
        }
    }
}
