using System;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class ProjectAuditFilter
        : AuditFilterBase
    {
        private string project;
        public ProjectAuditFilter(string projectName)
            : this(projectName, null) { }
        public ProjectAuditFilter(string projectName, IAuditFilter innerFilter)
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
