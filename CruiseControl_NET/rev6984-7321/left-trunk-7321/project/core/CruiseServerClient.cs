using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Xml;
using System.Xml.Serialization;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Messages;
using ThoughtWorks.CruiseControl.Core.Security;
using System.Security.Cryptography;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core
{
    public class CruiseServerClient
        : MarshalByRefObject, ICruiseServerClient
    {
        private readonly ICruiseServer cruiseServer;
        private readonly IChannelSecurity channelSecurity;
        private Dictionary<string, Type> messageTypes = null;
        private Dictionary<Type, XmlSerializer> messageSerialisers = new Dictionary<Type, XmlSerializer>();
        private Dictionary<string, SecureConnection> connections = new Dictionary<string, SecureConnection>();
        public CruiseServerClient(ICruiseServer cruiseServer)
        {
            this.cruiseServer = cruiseServer;
            var server = cruiseServer as CruiseServer;
            if ((server != null) &&
                (server.SecurityManager != null))
            {
                channelSecurity = server.SecurityManager.Channel;
            }
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
            var response = new Response();
            try
            {
                response = ExtractAndInvokeMessage(message, action, new RemotingChannelSecurityInformation());
            }
            catch (Exception error)
            {
                response.Result = ResponseResult.Failure;
                response.ErrorMessages.Add(
                    new ErrorMessage("Unable to process: " + error.Message));
            }
            var responseText = response.ToString();
            return responseText;
        }
        public virtual Response ProcessMessage(string action, ServerRequest message)
        {
            Response response = new Response();
            try
            {
                Type cruiseType = this.GetType();
                MethodInfo actionMethod = cruiseType.GetMethod(action,
                    BindingFlags.IgnoreCase | BindingFlags.Instance | BindingFlags.InvokeMethod | BindingFlags.Public);
                if (actionMethod == null)
                {
                    throw new CruiseControlException(
                        string.Format(
                            "Unable to locate action '{0}'",
                            action));
                }
                message.ChannelInformation = new RemotingChannelSecurityInformation();
                response = actionMethod.Invoke(this,
                    new object[] {
                        message
                    }) as Response;
            }
            catch (Exception error)
            {
                response.Result = ResponseResult.Failure;
                if ((error is TargetInvocationException) && (error.InnerException != null))
                {
                    error = error.InnerException;
                }
                response.ErrorMessages.Add(
                    new ErrorMessage("Unable to process: " + error.Message));
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
        public FileTransferResponse RetrieveFileTransfer(FileTransferRequest request)
        {
            return cruiseServer.RetrieveFileTransfer(request);
        }
        public virtual DataResponse GetLinkedSiteId(ProjectItemRequest request)
        {
            return cruiseServer.GetLinkedSiteId(request);
        }
        public Response ProcessSecureRequest(ServerRequest request)
        {
            var encryptedRequest = request as EncryptedRequest;
            if (encryptedRequest == null) throw new CruiseControlException("Incoming request is not an encrypted request");
            if (!connections.ContainsKey(request.SourceName)) throw new CruiseControlException("No secure connection for the source");
            var connection = connections[request.SourceName];
            var crypto = new RijndaelManaged();
            crypto.Key = connection.Key;
            crypto.IV = connection.IV;
            string data = DecryptMessage(crypto, encryptedRequest.EncryptedData);
            var response = ExtractAndInvokeMessage(data, encryptedRequest.Action,
                new RemotingChannelSecurityInformation(){ IsEncrypted = true });
            var encryptedResponse = new EncryptedResponse(request);
            encryptedResponse.EncryptedData = response.ToString();
            encryptedResponse.EncryptedData = EncryptMessage(crypto, encryptedResponse.EncryptedData);
            encryptedResponse.Result = ResponseResult.Success;
            return encryptedResponse;
        }
        public DataResponse RetrievePublicKey(ServerRequest request)
        {
            var response = new DataResponse(request);
            var cp = new CspParameters();
            cp.KeyContainerName = "CruiseControl.NET Server";
            var provider = new RSACryptoServiceProvider(cp);
            response.Data = provider.ToXmlString(false);
            response.Result = ResponseResult.Success;
            return response;
        }
        public Response InitialiseSecureConnection(LoginRequest request)
        {
            var cp = new CspParameters();
            cp.KeyContainerName = "CruiseControl.NET Server";
            var provider = new RSACryptoServiceProvider(cp);
            var originalKey = request.FindCredential(LoginRequest.UserNameCredential).Value;
            var decryptedKey = UTF8Encoding.UTF8.GetString(
                provider.Decrypt(Convert.FromBase64String(originalKey), false));
            var originalIv = request.FindCredential(LoginRequest.PasswordCredential).Value;
            var decryptedIv = UTF8Encoding.UTF8.GetString(
                provider.Decrypt(Convert.FromBase64String(originalIv), false));
            var connection = new SecureConnection()
            {
                Expiry = DateTime.Now.AddMinutes(15),
                IV = Convert.FromBase64String(decryptedIv),
                Key = Convert.FromBase64String(decryptedKey)
            };
            connections.Add(request.SourceName,
                connection);
            var response = new Response(request);
            response.Result = ResponseResult.Success;
            return response;
        }
        public Response TerminateSecureConnection(ServerRequest request)
        {
            if (connections.ContainsKey(request.SourceName))
            {
                connections.Remove(request.SourceName);
            }
            var response = new Response(request);
            response.Result = ResponseResult.Success;
            return response;
        }
        public DataListResponse ListServers(ServerRequest request)
        {
            return new DataListResponse()
            {
                Data = new List<string>(){
                    "local"
                }
            };
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
        private static string EncryptMessage(RijndaelManaged crypto, string message)
        {
            var encryptStream = new MemoryStream();
            var encrypt = new CryptoStream(encryptStream,
                crypto.CreateEncryptor(),
                CryptoStreamMode.Write);
            var dataToEncrypt = Encoding.UTF8.GetBytes(message);
            encrypt.Write(dataToEncrypt, 0, dataToEncrypt.Length);
            encrypt.FlushFinalBlock();
            encrypt.Close();
            var data = Convert.ToBase64String(encryptStream.ToArray());
            return data;
        }
        private static string DecryptMessage(RijndaelManaged crypto, string message)
        {
            var inputStream = new MemoryStream(Convert.FromBase64String(message));
            string data;
            using (var decryptionStream = new CryptoStream(inputStream,
                crypto.CreateDecryptor(),
                CryptoStreamMode.Read))
            {
                using (var reader = new StreamReader(decryptionStream))
                {
                    data = reader.ReadToEnd();
                }
            }
            return data;
        }
        private Response ExtractAndInvokeMessage(string message,
            string action,
            object channelInformation)
        {
            var messageXml = new XmlDocument();
            messageXml.LoadXml(message);
            var cruiseType = typeof(ICruiseServerClient);
            var actionMethod = cruiseType.GetMethod(action,
                BindingFlags.IgnoreCase | BindingFlags.Instance | BindingFlags.InvokeMethod | BindingFlags.Public);
            if (actionMethod == null)
            {
                throw new CruiseControlException(
                    string.Format(
                        "Unable to locate action '{0}'",
                        action));
            }
            var messageType = FindMessageType(messageXml.DocumentElement.Name);
            if (messageType == null)
            {
                throw new CruiseControlException(
                    string.Format(
                        "Unable to translate message: '{0}' is unknown",
                        messageXml.DocumentElement.Name));
            }
            var request = ConvertXmlToObject(messageType, message);
            var requestMessage = request as CommunicationsMessage;
            if (requestMessage != null) requestMessage.ChannelInformation = channelInformation;
            var response = actionMethod.Invoke(this,
                new object[] {
                        request
                    }) as Response;
            return response;
        }
        private class SecureConnection
        {
            public byte[] Key { get; set; }
            public byte[] IV { get; set; }
            public DateTime Expiry { get; set; }
        }
    }
}
