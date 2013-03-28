namespace ThoughtWorks.CruiseControl.Core
{
    public interface IStatusItem
        : IStatusSnapshotGenerator
    {
        void InitialiseStatus();
        void CancelStatus();
    }
}
