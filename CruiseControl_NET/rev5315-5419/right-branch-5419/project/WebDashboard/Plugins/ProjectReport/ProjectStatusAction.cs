using System.Collections;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.WebDashboard.Dashboard;
using ThoughtWorks.CruiseControl.WebDashboard.IO;
using ThoughtWorks.CruiseControl.WebDashboard.MVC;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.View;
using ThoughtWorks.CruiseControl.WebDashboard.ServerConnection;
using System.Xml;
using System.Text;
using System;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.ProjectReport
{
    public class ProjectStatusAction : ICruiseAction
    {
        public const string ActionName = "ProjectStatus";
        private readonly IFarmService farmServer;
        public ProjectStatusAction(IFarmService farmServer)
        {
            this.farmServer = farmServer;
        }
        public IResponse Execute(ICruiseRequest cruiseRequest)
        {
            IProjectSpecifier projectSpecifier = cruiseRequest.ProjectSpecifier;
            ProjectStatusSnapshot snapshot = farmServer.TakeStatusSnapshot(projectSpecifier);
            IResponse output = null;
            string outputType = (cruiseRequest.Request.GetText("view") ?? string.Empty).ToLower();
            switch (outputType)
            {
                case "json":
                    string json = ConvertStatusToJson(snapshot);
                    output = new JsonFragmentResponse(json);
                    break;
                default:
                    string xml = snapshot.ToString();
                    output = new XmlFragmentResponse(xml);
                    break;
            }
            return output;
        }
        private string ConvertStatusToJson(ProjectStatusSnapshot status)
        {
            StringBuilder jsonText = new StringBuilder();
            jsonText.Append("{");
            jsonText.AppendFormat("time:{0},", ToJsonDate(status.TimeOfSnapshot));
            AppendStatusDetails(status, jsonText);
            jsonText.Append("}");
            return jsonText.ToString();
        }
        private string ConvertStatusToJson(ItemStatus status)
        {
            StringBuilder jsonText = new StringBuilder();
            jsonText.Append("{");
            AppendStatusDetails(status, jsonText);
            jsonText.Append("}");
            return jsonText.ToString();
        }
        private void AppendStatusDetails(ItemStatus status, StringBuilder builder)
        {
            builder.AppendFormat("id:'{0}'", status.Identifier);
            builder.AppendFormat(",name:'{0}'", ToJsonString(status.Name));
            builder.AppendFormat(",status:'{0}'", status.Status);
            if (!string.IsNullOrEmpty(status.Description)) builder.AppendFormat(",description:'{0}'", ToJsonString(status.Description));
            if (status.TimeStarted.HasValue) builder.AppendFormat(",started:{0}", ToJsonDate(status.TimeStarted.Value));
            if (status.TimeCompleted.HasValue) builder.AppendFormat(",completed:{0}", ToJsonDate(status.TimeCompleted.Value));
            if (status.TimeOfEstimatedCompletion.HasValue) builder.AppendFormat(",estimated:{0}", ToJsonDate(status.TimeOfEstimatedCompletion.Value));
            if (status.ChildItems.Count > 0)
            {
                builder.Append(",children:[");
                int count = 0;
                foreach (ItemStatus child in status.ChildItems)
                {
                    if (count++ > 0) builder.Append(",");
                    builder.Append(ConvertStatusToJson(child));
                }
                builder.Append("]");
            }
        }
        private string ToJsonString(string value)
        {
            string json = value.Replace("\'", "\'\'");
            return json;
        }
        private string ToJsonDate(DateTime value)
        {
            string json = string.Format("new Date({0}, {1}, {2}, {3}, {4}, {5})",
                value.Year,
                value.Month - 1,
                value.Day,
                value.Hour,
                value.Minute,
                value.Second);
            return json;
        }
    }
}
