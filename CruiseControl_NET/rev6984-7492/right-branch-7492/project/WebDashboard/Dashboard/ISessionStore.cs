using System;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard
{
    public interface ISessionStore
    {
        ISessionStorer RetrieveStorer();
        ISessionRetriever RetrieveRetriever();
    }
}
