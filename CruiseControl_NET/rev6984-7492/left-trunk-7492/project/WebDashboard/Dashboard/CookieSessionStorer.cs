using System;
using System.Web;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard
{
    public class CookieSessionStorer
        : ISessionStorer
    {
        public string SessionToken { get; set; }
        public string GenerateQueryToken()
        {
            return string.Empty;
        }
    }
}
