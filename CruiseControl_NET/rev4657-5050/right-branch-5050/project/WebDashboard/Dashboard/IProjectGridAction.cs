using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
using ThoughtWorks.CruiseControl.WebDashboard.MVC;
using ThoughtWorks.CruiseControl.WebDashboard.IO;
namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard
{
    public interface IProjectGridAction
    {
        ProjectGridSortColumn DefaultSortColumn { get; set; }
        IResponse Execute(string actionName, ICruiseRequest request);
        IResponse Execute(string actionName, IServerSpecifier serverSpecifer, ICruiseRequest request);
    }
}
