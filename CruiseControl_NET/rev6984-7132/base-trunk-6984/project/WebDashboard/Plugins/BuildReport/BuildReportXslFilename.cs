using System;
using System.Collections.Generic;
using System.Web;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.BuildReport
{
    public class BuildReportXslFilename
    {
        public BuildReportXslFilename()
        {
            this.IncludedProjects = new List<string>();
            this.ExcludedProjects = new List<string>();
        }
        public BuildReportXslFilename(string fileName)
            : this()
        {
            this.Filename = fileName;
        }
        public string Filename { get; set; }
        public ICollection<string> IncludedProjects { get; private set; }
        public ICollection<string> ExcludedProjects { get; private set; }
        public bool CheckProject(string projectName)
        {
            if (this.IncludedProjects.Count > 0)
            {
                return this.IncludedProjects.Contains(projectName);
            }
            else if (this.ExcludedProjects.Count > 0)
            {
                return !this.ExcludedProjects.Contains(projectName);
            }
            else
            {
                return true;
            }
        }
    }
}
