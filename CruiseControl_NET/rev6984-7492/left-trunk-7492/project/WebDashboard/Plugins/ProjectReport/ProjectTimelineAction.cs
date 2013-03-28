namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.ProjectReport
{
    using System;
    using System.Collections;
    using System.Text;
    using System.Xml;
    using ThoughtWorks.CruiseControl.Core;
    using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
    using ThoughtWorks.CruiseControl.WebDashboard.IO;
    using ThoughtWorks.CruiseControl.WebDashboard.MVC;
    using ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise;
    using ThoughtWorks.CruiseControl.WebDashboard.MVC.View;
    using ThoughtWorks.CruiseControl.WebDashboard.Plugins.BuildReport;
    using ThoughtWorks.CruiseControl.WebDashboard.ServerConnection;
    public class ProjectTimelineAction
        : ICruiseAction
    {
        public const string TimelineActionName = "ProjectTimeline";
        public const string DataActionName = "ProjectTimelineData";
        private readonly IVelocityViewGenerator viewGenerator;
        private readonly IFarmService farmService;
        private readonly ICruiseUrlBuilder urlBuilder;
        public ProjectTimelineAction(IVelocityViewGenerator viewGenerator, IFarmService farmService, ICruiseUrlBuilder urlBuilder)
  {
            this.viewGenerator = viewGenerator;
            this.farmService = farmService;
            this.urlBuilder = urlBuilder;
        }
        public IResponse Execute(ICruiseRequest cruiseRequest)
        {
            IResponse response;
            if (string.Equals(cruiseRequest.Request.FileNameWithoutExtension, TimelineActionName, StringComparison.InvariantCultureIgnoreCase))
            {
                response = this.GenerateTimelinePage(cruiseRequest);
            }
            else if (string.Equals(cruiseRequest.Request.FileNameWithoutExtension, DataActionName, StringComparison.InvariantCultureIgnoreCase))
            {
                response = this.GenerateData(cruiseRequest);
            }
            else
            {
                throw new CruiseControlException("Unknown action: " + cruiseRequest.Request.FileNameWithoutExtension);
            }
            return response;
        }
        private IResponse GenerateTimelinePage(ICruiseRequest cruiseRequest)
        {
            var velocityContext = new Hashtable();
            velocityContext.Add("projectName", cruiseRequest.ProjectName);
            if (cruiseRequest.Request.ApplicationPath == "/")
            {
                velocityContext["applicationPath"] = string.Empty;
            }
            else
            {
                velocityContext["applicationPath"] = cruiseRequest.Request.ApplicationPath;
            }
            velocityContext["dataUrl"] = this.urlBuilder.BuildProjectUrl(DataActionName, cruiseRequest.ProjectSpecifier);
            return this.viewGenerator.GenerateView("ProjectTimeline.vm", velocityContext);
        }
        private IResponse GenerateData(ICruiseRequest cruiseRequest)
        {
            var builder = new StringBuilder();
            var settings = new XmlWriterSettings()
            {
                CheckCharacters = true,
                ConformanceLevel = ConformanceLevel.Document,
                Indent = false,
                NewLineHandling = NewLineHandling.None,
                NewLineOnAttributes = false,
                OmitXmlDeclaration = true
            };
            var basePath = (cruiseRequest.Request.ApplicationPath == "/" ? string.Empty : cruiseRequest.Request.ApplicationPath) +
                "/javascript/Timeline/images/";
            using (var xmlWriter = XmlWriter.Create(builder, settings))
            {
                xmlWriter.WriteStartElement("data");
                var builds = this.farmService.GetBuildSpecifiers(cruiseRequest.ProjectSpecifier, cruiseRequest.RetrieveSessionToken());
                foreach (var build in builds)
                {
                    this.AppendBuild(build, xmlWriter, basePath);
                }
                xmlWriter.WriteEndElement();
            }
            return new XmlFragmentResponse(builder.ToString());
        }
        private void AppendBuild(IBuildSpecifier build, XmlWriter xmlWriter, string basePath)
        {
            var logFile = new LogFile(build.BuildName);
            xmlWriter.WriteStartElement("event");
            xmlWriter.WriteAttributeString("start", logFile.Date.ToString("r"));
            xmlWriter.WriteAttributeString("title", logFile.Succeeded ? "Success (" + logFile.Label + ")" : "Failure");
            xmlWriter.WriteAttributeString("color", logFile.Succeeded ? "green" : "red");
            xmlWriter.WriteAttributeString("icon", basePath + "dark-" + (logFile.Succeeded ? "green" : "red") + "-circle.png");
            var buildUrl = this.urlBuilder.BuildBuildUrl(BuildReportBuildPlugin.ACTION_NAME, build);
            xmlWriter.WriteString("<a href=\"" + buildUrl + "\">View Build</a>");
            xmlWriter.WriteEndElement();
        }
    }
}
