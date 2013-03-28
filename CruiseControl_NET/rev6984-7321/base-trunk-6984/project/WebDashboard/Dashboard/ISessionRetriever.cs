using System;
using ThoughtWorks.CruiseControl.WebDashboard.MVC;
namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard
{
    public interface ISessionRetriever
    {
        string RetrieveSessionToken(IRequest request);
        string SessionToken { get; set; }
    }
}
