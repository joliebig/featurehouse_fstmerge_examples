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
using System.Web;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Security
{
    public class UserNameSecurityAction
        : ICruiseAction
    {
        private readonly IFarmService farmService;
        private readonly IVelocityViewGenerator viewGenerator;
        private readonly ISessionStorer storer;
        private bool hidePassword;
        public UserNameSecurityAction(IFarmService farmService, IVelocityViewGenerator viewGenerator,
            ISessionStorer storer, bool hidePassword)
        {
            this.farmService = farmService;
            this.viewGenerator = viewGenerator;
            this.storer = storer;
            this.hidePassword = hidePassword;
        }
        public IResponse Execute(ICruiseRequest cruiseRequest)
        {
            Hashtable velocityContext = new Hashtable();
            string userName = cruiseRequest.Request.GetText("userName");
            string template = @"UserNameLogin.vm";
            if (!string.IsNullOrEmpty(userName))
            {
                try
                {
                    LoginRequest credentials = new LoginRequest(userName);
                    string password = cruiseRequest.Request.GetText("password");
                    if (!string.IsNullOrEmpty(password)) credentials.AddCredential(LoginRequest.PasswordCredential, password);
                    string sessionToken = farmService.Login(cruiseRequest.ServerName, credentials);
                    if (string.IsNullOrEmpty(sessionToken)) throw new Exception("Login failed!");
                    storer.SessionToken = sessionToken;
                    AddSessionCookie(sessionToken);
                    template = "LoggedIn.vm";
                }
                catch (Exception error)
                {
                    velocityContext["errorMessage"] = error.Message;
                }
            }
            velocityContext["hidePassword"] = hidePassword;
            return viewGenerator.GenerateView(template, velocityContext);
        }
        private void AddSessionCookie(string sessionToken)
        {
            var newCookie = new HttpCookie("CCNetSessionToken");
            newCookie.Value = sessionToken;
            newCookie.HttpOnly = true;
            newCookie.Expires = DateTime.Now.AddMinutes(15);
            HttpContext.Current.Response.Cookies.Add(newCookie);
        }
    }
}
