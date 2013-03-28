using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.Xml;
using System.Collections.Specialized;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class CruiseServerHttpClient
        : CruiseServerClientBase
    {
        private readonly string serverUri;
        private string targetServer;
        private WebClient client;
        public CruiseServerHttpClient(string serverUri)
            : this(serverUri, new WebClient())
        {
        }
        public CruiseServerHttpClient(string serverUri, WebClient client)
        {
            this.serverUri = serverUri.EndsWith("/") ? serverUri.Substring(0, serverUri.Length - 1) : serverUri;
            this.client = client;
        }
        public override string TargetServer
        {
            get { return string.IsNullOrEmpty(targetServer) ? "local" : targetServer; }
            set { targetServer = value; }
        }
        public override bool IsBusy
        {
            get { return false; }
        }
        public override string Address
        {
            get { return serverUri; }
        }
        public override ProjectStatus[] GetProjectStatus()
        {
            try
            {
                var url = GenerateUrl("XmlStatusReport.aspx");
                var response = client.DownloadString(url);
                if (string.IsNullOrEmpty(response)) throw new CommunicationsException("No data retrieved");
                var document = new XmlDocument();
                document.LoadXml(response);
                var projects = ParseProjects(document.SelectNodes("/Projects/Project"));
                return projects.ToArray();
            }
            catch (Exception error)
            {
                throw new CommunicationsException("Unable to retrieve project status from_ the remote server", error);
            }
        }
        public override void ForceBuild(string projectName)
        {
            SendButtonPush("ForceBuild", projectName);
        }
        public override void ForceBuild(string projectName, List<NameValuePair> parameters)
        {
            ForceBuild(projectName);
        }
        public override void AbortBuild(string projectName)
        {
            SendButtonPush("AbortBuild", projectName);
        }
        public override void Request(string projectName, IntegrationRequest integrationRequest)
        {
            ForceBuild(projectName);
        }
        public override void StartProject(string project)
        {
            SendButtonPush("StartBuild", project);
        }
        public override void StopProject(string project)
        {
            SendButtonPush("StopBuild", project);
        }
        public override CruiseServerSnapshot GetCruiseServerSnapshot()
        {
            try
            {
                string response;
                try
                {
                    var url = GenerateUrl("XmlServerReport.aspx");
                    response = client.DownloadString(url);
                }
                catch (Exception)
                {
                    var url = GenerateUrl("XmlStatusReport.aspx");
                    response = client.DownloadString(url);
                }
                if (string.IsNullOrEmpty(response)) throw new CommunicationsException("No data retrieved");
                var document = new XmlDocument();
                document.LoadXml(response);
                var snapshot = new CruiseServerSnapshot();
                if (document.DocumentElement.Name == "CruiseControl")
                {
                    snapshot.ProjectStatuses = ParseProjects(document.SelectNodes("/CruiseControl/Projects/Project")).ToArray();
                    ParseQueues(document, snapshot);
                }
                else
                {
                    snapshot.ProjectStatuses = ParseProjects(document.SelectNodes("/Projects/Project")).ToArray();
                }
                return snapshot;
            }
            catch (Exception error)
            {
                throw new CommunicationsException("Unable to retrieve project status from_ the remote server", error);
            }
        }
        public override string GetServerVersion()
        {
            GetProjectStatus();
            return "0.0.0.0";
        }
        private string GenerateUrl(string pageUrl)
        {
            var lastSlash = serverUri.LastIndexOf('/');
            if (serverUri.IndexOf('.', lastSlash) > 0)
            {
                return serverUri;
            }
            else
            {
                return string.Format("{0}/{1}", serverUri, pageUrl);
            }
        }
        private List<ProjectStatus> ParseProjects(XmlNodeList projectNodes)
        {
            var projects = new List<ProjectStatus>();
            foreach (XmlElement node in projectNodes)
            {
                var project = new ProjectStatus(){
                    Activity = new ProjectActivity(RetrieveAttributeValue(node, "activity", "Unknown")),
                    BuildStage = node.GetAttribute("BuildStage"),
                    BuildStatus = RetrieveAttributeValue(node, "lastBuildStatus", IntegrationStatus.Unknown),
                    Category = node.GetAttribute("category"),
                    LastBuildDate = RetrieveAttributeValue(node, "lastBuildTime", DateTime.MinValue),
                    LastBuildLabel = node.GetAttribute("lastBuildLabel"),
                    Name = node.GetAttribute("name"),
                    NextBuildTime = RetrieveAttributeValue(node, "nextBuildTime", DateTime.MaxValue),
                    ServerName = node.GetAttribute("serverName"),
                    Status = RetrieveAttributeValue(node, "status", ProjectIntegratorState.Unknown),
                    WebURL = node.GetAttribute("webUrl")
                };
                projects.Add(project);
            }
            return projects;
        }
        private void ParseQueues(XmlDocument document, CruiseServerSnapshot snapshot)
        {
            foreach (XmlElement queueSnapshotEl in document.SelectNodes("/CruiseControl/Queues/Queue"))
            {
                var queueSnapshot = new QueueSnapshot()
                {
                    QueueName = RetrieveAttributeValue(queueSnapshotEl, "name", string.Empty)
                };
                snapshot.QueueSetSnapshot.Queues.Add(queueSnapshot);
                foreach (XmlElement requestEl in queueSnapshotEl.SelectNodes("Request"))
                {
                    var request = new QueuedRequestSnapshot()
                    {
                        Activity = new ProjectActivity(RetrieveAttributeValue(requestEl, "activity", "Unknown")),
                        ProjectName = RetrieveAttributeValue(requestEl, "projectName", string.Empty)
                    };
                    queueSnapshot.Requests.Add(request);
                }
            }
        }
        private string RetrieveAttributeValue(XmlElement element, string attributeName, string defaultValue)
        {
            var value = element.GetAttribute(attributeName);
            if (string.IsNullOrEmpty(value)) value = defaultValue;
            return value;
        }
        private DateTime RetrieveAttributeValue(XmlElement element, string attributeName, DateTime defaultValue)
        {
            var value = element.GetAttribute(attributeName);
            var dateValue = string.IsNullOrEmpty(value)
                ? defaultValue
                : DateTime.Parse(value);
            return dateValue;
        }
        private TEnum RetrieveAttributeValue<TEnum>(XmlElement element, string attributeName, TEnum defaultValue)
        {
            var value = element.GetAttribute(attributeName);
            var enumValue = string.IsNullOrEmpty(value)
                ? defaultValue
                : (TEnum)Enum.Parse(typeof(TEnum), value);
            return enumValue;
        }
        private void SendButtonPush(string command, string project)
        {
            var url = GenerateUrl("ViewFarmReport.aspx");
            var values = new NameValueCollection();
            values.Add(command, "true");
            values.Add("projectName", project);
            values.Add("serverName", TargetServer);
            try
            {
                client.UploadValues(url, values);
            }
            catch (Exception error)
            {
                throw new CommunicationsException(
                    string.Format("{0} failed: {1}", command, error.Message),
                    error);
            }
        }
    }
}
