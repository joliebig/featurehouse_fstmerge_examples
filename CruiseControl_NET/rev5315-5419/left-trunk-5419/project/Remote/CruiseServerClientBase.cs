using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Remote
{
    public abstract class CruiseServerClientBase
    {
        private string sessionToken;
        public abstract string TargetServer { get; set; }
        public virtual string SessionToken
        {
            get { return sessionToken; }
            set { sessionToken = value; }
        }
        public abstract bool IsBusy { get; }
        public virtual ProjectStatus[] GetProjectStatus()
        {
            throw new NotImplementedException();
        }
        public virtual void ForceBuild(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual void ForceBuild(string projectName, List<NameValuePair> parameters)
        {
            throw new NotImplementedException();
        }
        public virtual void AbortBuild(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual void Request(string projectName, IntegrationRequest integrationRequest)
        {
            throw new NotImplementedException();
        }
        public virtual void StartProject(string project)
        {
            throw new NotImplementedException();
        }
        public virtual void StopProject(string project)
        {
            throw new NotImplementedException();
        }
        public virtual void SendMessage(string projectName, Message message)
        {
            throw new NotImplementedException();
        }
        public virtual void WaitForExit(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual void CancelPendingRequest(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual CruiseServerSnapshot GetCruiseServerSnapshot()
        {
            throw new NotImplementedException();
        }
        public virtual string GetLatestBuildName(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual string[] GetBuildNames(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual string[] GetMostRecentBuildNames(string projectName, int buildCount)
        {
            throw new NotImplementedException();
        }
        public virtual string GetLog(string projectName, string buildName)
        {
            throw new NotImplementedException();
        }
        public virtual string GetServerLog()
        {
            throw new NotImplementedException();
        }
        public virtual string GetServerLog(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual string GetServerVersion()
        {
            throw new NotImplementedException();
        }
        public virtual void AddProject(string serializedProject)
        {
            throw new NotImplementedException();
        }
        public virtual void DeleteProject(string projectName, bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment)
        {
            throw new NotImplementedException();
        }
        public virtual string GetProject(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual void UpdateProject(string projectName, string serializedProject)
        {
            throw new NotImplementedException();
        }
        public virtual ExternalLink[] GetExternalLinks(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual string GetArtifactDirectory(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual string GetStatisticsDocument(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual string GetModificationHistoryDocument(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual string GetRSSFeed(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual bool Login(List<NameValuePair> Credentials)
        {
            throw new NotImplementedException();
        }
        public virtual void Logout()
        {
            throw new NotImplementedException();
        }
        public virtual string GetSecurityConfiguration()
        {
            throw new NotImplementedException();
        }
        public virtual List<UserDetails> ListUsers()
        {
            throw new NotImplementedException();
        }
        public virtual List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(string userName, params string[] projects)
        {
            throw new NotImplementedException();
        }
        public virtual List<AuditRecord> ReadAuditRecords(int startRecord, int numberOfRecords)
        {
            throw new NotImplementedException();
        }
        public virtual List<AuditRecord> ReadAuditRecords(int startRecord, int numberOfRecords, AuditFilterBase filter)
        {
            throw new NotImplementedException();
        }
        public virtual List<ParameterBase> ListBuildParameters(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual void ChangePassword(string oldPassword, string newPassword)
        {
            throw new NotImplementedException();
        }
        public virtual void ResetPassword(string userName, string newPassword)
        {
            throw new NotImplementedException();
        }
        public virtual ProjectStatusSnapshot TakeStatusSnapshot(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual List<PackageDetails> RetrievePackageList(string projectName)
        {
            throw new NotImplementedException();
        }
        public virtual IFileTransfer RetrieveFileTransfer(string projectName, string fileName)
        {
            throw new NotImplementedException();
        }
    }
}
