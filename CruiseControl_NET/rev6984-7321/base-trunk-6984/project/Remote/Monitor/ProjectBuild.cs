using System;
using System.Collections.Generic;
using System.Text;
using System.Globalization;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public class ProjectBuild
    {
        private readonly CruiseServerClientBase client;
        private readonly Project project;
        private readonly string name;
        private string log;
        public ProjectBuild(string buildName, Project project, CruiseServerClientBase client)
        {
            this.name = buildName;
            this.project = project;
            this.client = client;
            BuildDate = DateTime.ParseExact(name.Substring(3, 14), "yyyyMMddHHmmss", CultureInfo.InvariantCulture);
            IsSuccessful = (name.Substring(17, 1) == "L");
            if (IsSuccessful)
            {
                var startPos = name.IndexOf("build.", StringComparison.InvariantCultureIgnoreCase) + 6;
                var endPos = name.LastIndexOf('.');
                Label = name.Substring(startPos, endPos - startPos);
            }
        }
        public string Name
        {
            get { return name; }
        }
        public string Log
        {
            get
            {
                if (string.IsNullOrEmpty(log))
                {
                    client.ProcessSingleAction<object>(o =>
                    {
                        log = client.GetLog(project.Name, name);
                    }, null);
                }
                return log;
            }
        }
        public DateTime BuildDate { get; private set; }
        public string Label { get; private set; }
        public bool IsSuccessful { get; private set; }
    }
}
