namespace ThoughtWorks.CruiseControl.Core.Util
{
    public interface IWebFunctions
    {
        bool PingUrl(string address);
        bool PingAndValidateHeaderValue(string address, string header, string value);
    }
}
