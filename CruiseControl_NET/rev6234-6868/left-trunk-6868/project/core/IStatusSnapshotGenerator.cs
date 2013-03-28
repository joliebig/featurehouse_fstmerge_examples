using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core
{
    public interface IStatusSnapshotGenerator
    {
        ItemStatus GenerateSnapshot();
    }
}
