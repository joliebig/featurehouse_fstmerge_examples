using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
using ThoughtWorks.CruiseControl.WebDashboard.MVC;
namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard
{
 public interface IProjectGridAction
    {
        ProjectGridSortColumn DefaultSortColumn { get; set; }
        IResponse Execute(string actionName, IRequest request);
  IResponse Execute(string actionName, IServerSpecifier serverSpecifer, IRequest request);
 }
}
