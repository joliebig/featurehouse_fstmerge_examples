namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
    public class WebDashboardUrl
    {
        private string serverUrl;
        private string serverAlias = "local";
        public WebDashboardUrl(string serverUrl)
        {
            this.serverUrl = serverUrl.TrimEnd('/');
        }
        public WebDashboardUrl(string serverUrl, string serverAlias) : this(serverUrl)
        {
            this.serverAlias = serverAlias.Trim('/');
        }
        public string XmlServerReport
        {
            get { return string.Format("{0}/XmlServerReport.aspx", serverUrl); }
        }
        public string ViewFarmReport
        {
            get { return string.Format("{0}/server/{1}/ViewFarmReport.aspx", serverUrl, serverAlias); }
        }
    }
}
