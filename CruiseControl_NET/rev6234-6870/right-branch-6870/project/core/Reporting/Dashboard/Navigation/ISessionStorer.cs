using System;
namespace ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation
{
    public interface ISessionStorer
    {
        string GenerateQueryToken();
        string SessionToken { get; set; }
    }
}
