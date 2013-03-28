using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote;
using System.Xml.Serialization;
using ThoughtWorks.CruiseControl.Remote.Messages;
using System.Xml;
using System.Reflection;
using System.IO;
namespace ThoughtWorks.CruiseControl.Core
{
    public class CruiseServerClient
        : MarshalByRefObject, ICruiseServerClient
    {
  private readonly ICruiseServer cruiseServer;
        private Dictionary<string, Type> messageTypes = null;
        private Dictionary<Type, XmlSerializer> messageSerialisers = new Dictionary<Type, XmlSerializer>();
        public CruiseServerClient(ICruiseServer cruiseServer)
  {
   this.cruiseServer = cruiseServer;
        }
        public virtual ProjectStatusResponse GetProjectStatus(ServerRequest request)
        {
            return cruiseServer.GetProjectStatus(request);
        }
        public Response Start(ProjectRequest request)
        {
            return cruiseServer.Start(request);
        }
        public Response Stop(ProjectRequest request)
        {
            return cruiseServer.Stop(request);
        }
        public Response ForceBuild(ProjectRequest request)
        {
            return cruiseServer.ForceBuild(request);
        }
        public Response AbortBuild(ProjectRequest request)
        {
            return cruiseServer.AbortBuild(request);
        }
        public Response CancelPendingRequest(ProjectRequest request)
        {
            return cruiseServer.CancelPendingRequest(request);
        }
        public Response SendMessage(MessageRequest request)
        {
            return cruiseServer.SendMessage(request);
        }
        public virtual Response WaitForExit(ProjectRequest request)
        {
            return cruiseServer.WaitForExit(request);
        }
        public virtual SnapshotResponse GetCruiseServerSnapshot(ServerRequest request)
        {
            return cruiseServer.GetCruiseServerSnapshot(request);
        }
        public virtual DataResponse GetLatestBuildName(ProjectRequest request)
        {
            return cruiseServer.GetLatestBuildName(request);
        }
        public virtual DataListResponse GetBuildNames(ProjectRequest request)
        {
            return cruiseServer.GetBuildNames(request);
        }
        public virtual DataListResponse GetMostRecentBuildNames(BuildListRequest request)
        {
            return cruiseServer.GetMostRecentBuildNames(request);
        }
        public virtual DataResponse GetLog(BuildRequest request)
        {
            return cruiseServer.GetLog(request);
        }
        public virtual DataResponse GetServerLog(ServerRequest request)
        {
            return cruiseServer.GetServerLog(request);
        }
        public virtual Response AddProject(ChangeConfigurationRequest request)
        {
            return cruiseServer.AddProject(request);
        }
        public virtual Response DeleteProject(ChangeConfigurationRequest request)
        {
            return cruiseServer.DeleteProject(request);
        }
        public virtual Response UpdateProject(ChangeConfigurationRequest request)
        {
            return cruiseServer.UpdateProject(request);
        }
        public virtual DataResponse GetProject(ProjectRequest request)
        {
            return cruiseServer.GetProject(request);
        }
        public virtual ExternalLinksListResponse GetExternalLinks(ProjectRequest request)
        {
            return cruiseServer.GetExternalLinks(request);
        }
        public virtual DataResponse GetArtifactDirectory(ProjectRequest request)
        {
            return cruiseServer.GetArtifactDirectory(request);
        }
        public virtual DataResponse GetStatisticsDocument(ProjectRequest request)
        {
            return cruiseServer.GetStatisticsDocument(request);
        }
        public virtual DataResponse GetModificationHistoryDocument(ProjectRequest request)
        {
            return cruiseServer.GetModificationHistoryDocument(request);
        }
        public virtual DataResponse GetRSSFeed(ProjectRequest request)
        {
            return cruiseServer.GetRSSFeed(request);
        }
        public virtual DataResponse GetServerVersion(ServerRequest request)
        {
            return cruiseServer.GetServerVersion(request);
        }
        public LoginResponse Login(LoginRequest request)
        {
            return cruiseServer.Login(request);
        }
        public Response Logout(ServerRequest request)
        {
            return cruiseServer.Logout(request);
        }
        public DataResponse GetSecurityConfiguration(ServerRequest request)
        {
            return cruiseServer.GetSecurityConfiguration(request);
        }
        public ListUsersResponse ListUsers(ServerRequest request)
        {
            return cruiseServer.ListUsers(request);
        }
        public DiagnoseSecurityResponse DiagnoseSecurityPermissions(DiagnoseSecurityRequest request)
        {
            return cruiseServer.DiagnoseSecurityPermissions(request);
        }
        public ReadAuditResponse ReadAuditRecords(ReadAuditRequest request)
        {
            return cruiseServer.ReadAuditRecords(request);
        }
        public BuildParametersResponse ListBuildParameters(ProjectRequest request)
        {
            return cruiseServer.ListBuildParameters(request);
        }
        public Response ChangePassword(ChangePasswordRequest request)
        {
            return cruiseServer.ChangePassword(request);
        }
        public Response ResetPassword(ChangePasswordRequest request)
        {
            return cruiseServer.ResetPassword(request);
        }
        public virtual string ProcessMessage(string action, string message)
        {
            Response response = new Response();
            try
            {
                XmlDocument messageXml = new XmlDocument();
                messageXml.LoadXml(message);
                Type messageType = FindMessageType(messageXml.DocumentElement.Name);
                if (messageType == null)
                {
                    throw new CruiseControlException(
                        string.Format(
                            "Unable to translate message: '{0}' is unknown",
                            messageXml.DocumentElement.Name));
                }
                Type cruiseType = typeof(ICruiseServerClient);
                MethodInfo actionMethod = cruiseType.GetMethod(action,
                    BindingFlags.IgnoreCase | BindingFlags.Instance | BindingFlags.InvokeMethod | BindingFlags.Public);
                if (actionMethod == null)
                {
                    throw new CruiseControlException(
                        string.Format(
                            "Unable to locate action '{0}'",
                            action));
                }
                object request = ConvertXmlToObject(messageType, message);
                response = actionMethod.Invoke(this,
                    new object[] {
                        request
                    }) as Response;
            }
            catch (Exception error)
            {
                response.Result = ResponseResult.Failure;
                response.ErrorMessages.Add(
                    new ErrorMessage("Unable to process error: " + error.Message));
            }
            return response.ToString();
        }
        public virtual Response ProcessMessage(string action, ServerRequest message)
        {
            Response response = new Response();
            try
            {
                Type cruiseType = typeof(ICruiseServerClient);
                MethodInfo actionMethod = cruiseType.GetMethod(action,
                    BindingFlags.IgnoreCase | BindingFlags.Instance | BindingFlags.InvokeMethod | BindingFlags.Public);
                if (actionMethod == null)
                {
                    throw new CruiseControlException(
                        string.Format(
                            "Unable to locate action '{0}'",
                            action));
                }
                response = actionMethod.Invoke(this,
                    new object[] {
                        message
                    }) as Response;
            }
            catch (Exception error)
            {
                response.Result = ResponseResult.Failure;
                response.ErrorMessages.Add(
                    new ErrorMessage("Unable to process error: " + error.Message));
            }
            return response;
        }
        public override object InitializeLifetimeService()
        {
            return null;
        }
        public virtual DataResponse GetFreeDiskSpace(ServerRequest request)
        {
            return cruiseServer.GetFreeDiskSpace(request);
        }
        public virtual StatusSnapshotResponse TakeStatusSnapshot(ProjectRequest request)
        {
            return cruiseServer.TakeStatusSnapshot(request);
        }
        public virtual ListPackagesResponse RetrievePackageList(ProjectRequest request)
        {
            return cruiseServer.RetrievePackageList(request);
        }
        public RemotingFileTransfer RetrieveFileTransfer(string project, string fileName)
        {
            return cruiseServer.RetrieveFileTransfer(project, fileName);
        }
        private Type FindMessageType(string messageName)
        {
            Type messageType = null;
            if (messageTypes == null)
            {
                messageTypes = new Dictionary<string, Type>();
                Assembly remotingLibrary = typeof(ICruiseServerClient).Assembly;
                foreach (Type remotingType in remotingLibrary.GetExportedTypes())
                {
                    XmlRootAttribute[] attributes = remotingType.GetCustomAttributes(
                        typeof(XmlRootAttribute), false) as XmlRootAttribute[];
                    foreach (XmlRootAttribute attribute in attributes)
                    {
                        messageTypes.Add(attribute.ElementName, remotingType);
                    }
                }
            }
            if (messageTypes.ContainsKey(messageName))
            {
                messageType = messageTypes[messageName];
            }
            return messageType;
        }
        private object ConvertXmlToObject(Type messageType, string message)
        {
            object messageObj = null;
            if (!messageSerialisers.ContainsKey(messageType))
            {
                messageSerialisers[messageType] = new XmlSerializer(messageType);
            }
            using (StringReader reader = new StringReader(message))
            {
                messageObj = messageSerialisers[messageType].Deserialize(reader);
            }
            return messageObj;
        }
    }
}
