namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.ProjectReport
{
    using System;
    using System.Linq;
    using System.Text;
    using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.WebDashboard.IO;
    using ThoughtWorks.CruiseControl.WebDashboard.MVC;
    using ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise;
    using ThoughtWorks.CruiseControl.WebDashboard.ServerConnection;
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
            var projectSpecifier = cruiseRequest.ProjectSpecifier;
            var snapshot = farmServer.TakeStatusSnapshot(projectSpecifier, cruiseRequest.RetrieveSessionToken());
            IResponse output = null;
            var outputType = (cruiseRequest.Request.GetText("view") ?? string.Empty).ToLower();
            switch (outputType)
            {
                case "json":
                    var json = this.ConvertStatusToJson(snapshot);
                    output = new JsonFragmentResponse(json);
                    break;
                default:
                    var xml = snapshot.ToString();
                    output = new XmlFragmentResponse(xml);
                    break;
            }
            return output;
        }
        private string ConvertStatusToJson(ProjectStatusSnapshot status)
        {
            var jsonText = new StringBuilder();
            jsonText.Append("{");
            jsonText.AppendFormat("time:{0},", ToJsonDate(status.TimeOfSnapshot));
            AppendStatusDetails(status, jsonText);
            jsonText.Append("}");
            return jsonText.ToString();
        }
        private string ConvertStatusToJson(ItemStatus status)
        {
            var jsonText = new StringBuilder();
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
                var children = from child in status.ChildItems
                               select this.ConvertStatusToJson(child);
                builder.Append(string.Join(",", children.ToArray()));
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
