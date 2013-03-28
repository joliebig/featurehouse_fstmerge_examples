using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
    public interface IProjectStatusRetriever
    {
        ProjectStatus GetProjectStatus(string projectName);
    }
}
