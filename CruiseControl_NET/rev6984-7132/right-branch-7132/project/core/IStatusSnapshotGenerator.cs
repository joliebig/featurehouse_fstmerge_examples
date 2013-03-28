namespace ThoughtWorks.CruiseControl.Core
{
    using ThoughtWorks.CruiseControl.Remote;
    public interface IStatusSnapshotGenerator
    {
        ItemStatus GenerateSnapshot();
    }
}
