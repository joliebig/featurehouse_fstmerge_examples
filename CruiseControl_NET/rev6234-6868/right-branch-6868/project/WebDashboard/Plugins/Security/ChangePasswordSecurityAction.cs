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
    public class ChangePasswordSecurityAction
        : ICruiseAction
    {
        public const string ActionName = "ServerChangePassword";
        private readonly IFarmService farmService;
        private readonly IVelocityViewGenerator viewGenerator;
        private readonly ISessionStorer storer;
        public ChangePasswordSecurityAction(IFarmService farmService, IVelocityViewGenerator viewGenerator,
            ISessionStorer storer)
        {
            this.farmService = farmService;
            this.viewGenerator = viewGenerator;
            this.storer = storer;
        }
        public IResponse Execute(ICruiseRequest cruiseRequest)
        {
            Hashtable velocityContext = new Hashtable();
            velocityContext["message"] = string.Empty;
            velocityContext["error"] = string.Empty;
            string oldPassword = cruiseRequest.Request.GetText("oldPassword");
            string newPassword1 = cruiseRequest.Request.GetText("newPassword1");
            string newPassword2 = cruiseRequest.Request.GetText("newPassword2");
            if (!string.IsNullOrEmpty(oldPassword) &&
                !string.IsNullOrEmpty(newPassword1))
            {
                try
                {
                    if (newPassword1 != newPassword2) throw new Exception("New passwords do not match");
                    farmService.ChangePassword(cruiseRequest.ServerName, storer.SessionToken, oldPassword, newPassword1);
                    velocityContext["message"] = "Password has been changed";
                }
                catch (Exception error)
                {
                    velocityContext["error"] = error.Message;
                }
            }
            return viewGenerator.GenerateView("ChangePasswordAction.vm", velocityContext);
        }
    }
}
