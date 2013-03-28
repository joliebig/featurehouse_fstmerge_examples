using System;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.Core
{
 public class CruiseManager : MarshalByRefObject, ICruiseManager
 {
  private readonly ICruiseServer cruiseServer;
  public CruiseManager(ICruiseServer cruiseServer)
  {
   this.cruiseServer = cruiseServer;
  }
  public ProjectStatus[] GetProjectStatus()
  {
   return cruiseServer.GetProjectStatus();
  }
        public void ForceBuild(string project, string enforcerName)
        {
            cruiseServer.ForceBuild(null, project, enforcerName, new Dictionary<string,string>());
        }
        public void ForceBuild(string project, string enforcerName, Dictionary<string, string> parameters)
        {
            cruiseServer.ForceBuild(null, project, enforcerName, parameters);
        }
        public void AbortBuild(string project, string enforcerName)
  {
            cruiseServer.AbortBuild(null, project, enforcerName);
  }
  public void Request(string projectName, IntegrationRequest integrationRequest)
  {
            cruiseServer.Request(null, projectName, integrationRequest);
  }
  public void Start(string project)
  {
            cruiseServer.Start(null, project);
  }
  public void Stop(string project)
  {
            cruiseServer.Stop(null, project);
  }
  public void SendMessage(string projectName, Message message)
  {
            cruiseServer.SendMessage(null, projectName, message);
        }
        public void ForceBuild(string sessionToken, string projectName, string enforcerName)
        {
            cruiseServer.ForceBuild(sessionToken, projectName, enforcerName, new Dictionary<string,string>());
        }
        public void ForceBuild(string sessionToken, string projectName, string enforcerName, Dictionary<string, string> parameters)
        {
            cruiseServer.ForceBuild(sessionToken, projectName, enforcerName, parameters);
        }
        public void AbortBuild(string sessionToken, string projectName, string enforcerName)
        {
            cruiseServer.AbortBuild(sessionToken, projectName, enforcerName);
        }
        public void Request(string sessionToken, string projectName, IntegrationRequest integrationRequest)
        {
            cruiseServer.Request(sessionToken, projectName, integrationRequest);
        }
        public void Start(string sessionToken, string project)
        {
            cruiseServer.Start(sessionToken, project);
        }
        public void Stop(string sessionToken, string project)
        {
            cruiseServer.Stop(sessionToken, project);
  }
        public void SendMessage(string sessionToken, string projectName, Message message)
        {
            cruiseServer.SendMessage(sessionToken, projectName, message);
        }
        public void CancelPendingRequest(string sessionToken, string projectName)
        {
            cruiseServer.CancelPendingRequest(sessionToken, projectName);
        }
  public void WaitForExit(string project)
  {
   cruiseServer.WaitForExit(project);
  }
  public void CancelPendingRequest(string projectName)
  {
            cruiseServer.CancelPendingRequest(null, projectName);
  }
        public CruiseServerSnapshot GetCruiseServerSnapshot()
  {
   return cruiseServer.GetCruiseServerSnapshot();
  }
  public string GetLatestBuildName(string projectName)
  {
   return cruiseServer.GetLatestBuildName(projectName);
  }
  public string[] GetBuildNames(string projectName)
  {
   return cruiseServer.GetBuildNames(projectName);
  }
  public string[] GetMostRecentBuildNames(string projectName, int buildCount)
  {
   try
   {
    return cruiseServer.GetMostRecentBuildNames(projectName, buildCount);
   }
   catch (Exception e)
   {
    Log.Error(e);
    throw new CruiseControlException("Unexpected exception caught on server", e);
   }
  }
  public string GetLog(string projectName, string buildName)
  {
   return cruiseServer.GetLog(projectName, buildName);
  }
  public string GetServerLog()
  {
   return cruiseServer.GetServerLog();
  }
  public string GetServerLog(string projectName)
  {
   return cruiseServer.GetServerLog(projectName);
  }
  public void AddProject(string serializedProject)
  {
   cruiseServer.AddProject(serializedProject);
  }
  public void DeleteProject(string projectName, bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment)
  {
   cruiseServer.DeleteProject(projectName, purgeWorkingDirectory, purgeArtifactDirectory, purgeSourceControlEnvironment);
  }
  public string GetProject(string projectName)
  {
   return cruiseServer.GetProject(projectName);
  }
  public void UpdateProject(string projectName, string serializedProject)
  {
   cruiseServer.UpdateProject(projectName, serializedProject);
  }
  public ExternalLink[] GetExternalLinks(string projectName)
  {
   return cruiseServer.GetExternalLinks(projectName);
  }
  public string GetArtifactDirectory(string projectName)
  {
   return cruiseServer.GetArtifactDirectory(projectName);
  }
  public string GetStatisticsDocument(string projectName)
  {
   return cruiseServer.GetStatisticsDocument(projectName);
  }
        public string GetModificationHistoryDocument(string projectName)
        {
            return cruiseServer.GetModificationHistoryDocument(projectName);
        }
        public string GetRSSFeed(string projectName)
        {
            return cruiseServer.GetRSSFeed(projectName);
        }
  public override object InitializeLifetimeService()
  {
   return null;
  }
  public string GetServerVersion()
  {
   return cruiseServer.GetVersion();
  }
        public long GetFreeDiskSpace()
        {
            return cruiseServer.GetFreeDiskSpace();
        }
        public virtual ProjectStatusSnapshot TakeStatusSnapshot(string projectName)
        {
            return cruiseServer.TakeStatusSnapshot(projectName);
        }
        public virtual PackageDetails[] RetrievePackageList(string projectName)
        {
            return cruiseServer.RetrievePackageList(projectName);
        }
        public virtual PackageDetails[] RetrievePackageList(string projectName, string buildLabel)
        {
            return cruiseServer.RetrievePackageList(projectName, buildLabel);
        }
        public virtual RemotingFileTransfer RetrieveFileTransfer(string project, string fileName)
        {
            return cruiseServer.RetrieveFileTransfer(project, fileName);
        }
        public string Login(ISecurityCredentials credentials)
        {
            string sessionToken = cruiseServer.Login(credentials);
            return sessionToken;
        }
        public void Logout(string sesionToken)
        {
            cruiseServer.Logout(sesionToken);
        }
        public bool ValidateSession(string sessionToken)
        {
            return true;
        }
        public virtual string GetSecurityConfiguration(string sessionToken)
        {
            return cruiseServer.GetSecurityConfiguration(sessionToken);
        }
        public virtual List<UserDetails> ListAllUsers(string sessionToken)
        {
            return cruiseServer.ListAllUsers(sessionToken);
        }
        public virtual List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(string sessionToken, string userName, params string[] projectNames)
        {
            return cruiseServer.DiagnoseSecurityPermissions(sessionToken, userName, projectNames);
        }
        public virtual List<AuditRecord> ReadAuditRecords(string sessionToken, int startPosition, int numberOfRecords)
        {
            return cruiseServer.ReadAuditRecords(sessionToken, startPosition, numberOfRecords);
        }
        public virtual List<AuditRecord> ReadAuditRecords(string sessionToken, int startPosition, int numberOfRecords, IAuditFilter filter)
        {
            return cruiseServer.ReadAuditRecords(sessionToken, startPosition, numberOfRecords, filter);
        }
        public virtual void ChangePassword(string sessionToken, string oldPassword, string newPassword)
        {
            cruiseServer.ChangePassword(sessionToken, oldPassword, newPassword);
        }
        public virtual void ResetPassword(string sessionToken, string userName, string newPassword)
        {
            cruiseServer.ResetPassword(sessionToken, userName, newPassword);
        }
        public virtual List<ParameterBase> ListBuildParameters(string projectName)
        {
            return cruiseServer.ListBuildParameters(projectName);
        }
    }
}
