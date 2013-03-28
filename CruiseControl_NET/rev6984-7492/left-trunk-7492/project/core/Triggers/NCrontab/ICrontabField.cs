namespace ThoughtWorks.CruiseControl.Core.Triggers.NCrontab
{
    public interface ICrontabField
    {
        int GetFirst();
        int Next(int start);
        bool Contains(int value);
    }
}
