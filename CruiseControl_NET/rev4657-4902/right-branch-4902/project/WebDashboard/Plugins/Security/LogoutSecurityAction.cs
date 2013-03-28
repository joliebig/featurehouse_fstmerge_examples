using Objection;
using System;
using System.Collections;
using System.Text;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.WebDashboard.Dashboard;
using ThoughtWorks.CruiseControl.WebDashboard.IO;
using ThoughtWorks.CruiseControl.WebDashboard.MVC;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.View;
using ThoughtWorks.CruiseControl.WebDashboard.ServerConnection;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Security
{
    public class LogoutSecurityAction
        : ICruiseAction
    {
        public const string ActionName = "ServerLogout";
        private readonly IFarmService farmService;
        private readonly IVelocityViewGenerator viewGenerator;
        private readonly ISessionStorer storer;
        public LogoutSecurityAction(IFarmService farmService, IVelocityViewGenerator viewGenerator,
            ISessionStorer storer)
        {
            this.farmService = farmService;
            this.viewGenerator = viewGenerator;
            this.storer = storer;
        }
        public IResponse Execute(ICruiseRequest cruiseRequest)
        {
            Hashtable velocityContext = new Hashtable();
            if (!string.IsNullOrEmpty(storer.SessionToken)) farmService.Logout(cruiseRequest.ServerName, storer.SessionToken);
            storer.SessionToken = null;
            return viewGenerator.GenerateView("LoggedOut.vm", velocityContext);
        }
    }
}
