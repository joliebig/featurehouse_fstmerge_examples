using System.Web;
using ThoughtWorks.CruiseControl.WebDashboard.MVC;
namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard
{
    public class CookieSessionRetriever
        : ISessionRetriever
    {
        public string SessionToken { get; set; }
        public string RetrieveSessionToken(IRequest request)
        {
            var cookie = HttpContext.Current.Request.Cookies["CCNetSessionToken"];
            if (cookie != null)
            {
                SessionToken = cookie.Value;
            }
            else
            {
                SessionToken = string.Empty;
            }
            return SessionToken;
        }
    }
}
