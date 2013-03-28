using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard
{
    [ReflectorType("cookieStore")]
    public class CookieSessionStore
        : ISessionStore
    {
        public ISessionStorer RetrieveStorer()
        {
            var storer = new CookieSessionStorer();
            return storer;
        }
        public ISessionRetriever RetrieveRetriever()
        {
            var retriever = new CookieSessionRetriever();
            return retriever;
        }
    }
}
