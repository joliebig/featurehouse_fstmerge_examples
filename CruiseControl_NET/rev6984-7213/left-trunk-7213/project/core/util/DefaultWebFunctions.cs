namespace ThoughtWorks.CruiseControl.Core.Util
{
    using System.Net;
    public class DefaultWebFunctions
        : IWebFunctions
    {
        public bool PingUrl(string address)
        {
            try
            {
                var pinger = new WebClient();
                pinger.DownloadString(address);
                return true;
            }
            catch
            {
                return false;
            }
        }
        public bool PingAndValidateHeaderValue(string address, string header, string value)
        {
            try
            {
                var pinger = new WebClient();
                pinger.DownloadString(address);
                return (string.Compare(pinger.ResponseHeaders[header], value, true) == 0);
            }
            catch
            {
                return false;
            }
        }
    }
}
