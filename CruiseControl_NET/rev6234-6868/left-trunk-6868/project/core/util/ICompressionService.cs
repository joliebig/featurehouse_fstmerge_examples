namespace ThoughtWorks.CruiseControl.Core.Util
{
    public interface ICompressionService
    {
        string CompressString(string value);
        string ExpandString(string value);
    }
}
