using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class ProjectAuditFilter
        : AuditFilterBase
    {
        private string project;
        public ProjectAuditFilter()
        {
        }
        public ProjectAuditFilter(string projectName)
            : this(projectName, null) { }
        [XmlAttribute("project")]
        public string ProjectName
        {
            get { return project; }
            set { project = value; }
        }
        public ProjectAuditFilter(string projectName, AuditFilterBase innerFilter)
            : base(innerFilter)
        {
            if (string.IsNullOrEmpty(projectName)) throw new ArgumentNullException("projectName");
            this.project = projectName;
        }
        protected override bool DoCheckFilter(AuditRecord record)
        {
            bool include = string.Equals(this.project, record.ProjectName);
            return include;
        }
    }
}
