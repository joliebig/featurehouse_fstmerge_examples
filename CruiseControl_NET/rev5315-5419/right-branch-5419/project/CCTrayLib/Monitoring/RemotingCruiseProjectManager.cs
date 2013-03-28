using System;
using ThoughtWorks.CruiseControl.Remote;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using ThoughtWorks.CruiseControl.Remote.Messages;
using ThoughtWorks.CruiseControl.Core;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public class RemotingCruiseProjectManager : ICruiseProjectManager
 {
        private readonly ICruiseServerClient manager;
  private readonly string projectName;
        public RemotingCruiseProjectManager(ICruiseServerClient manager, string projectName)
  {
   this.manager = manager;
   this.projectName = projectName;
  }
        public void ForceBuild(string sessionToken, Dictionary<string, string> parameters)
  {
   try
   {
                BuildIntegrationRequest request = PopulateRequest(new BuildIntegrationRequest(), sessionToken);
                request.BuildValues = NameValuePair.FromDictionary(parameters);
                ValidateResponse(manager.ForceBuild(request));
   }
   catch (System.Net.Sockets.SocketException)
   {
   }
   catch (System.Runtime.Remoting.RemotingException)
   {
   }
  }
  public void FixBuild(string sessionToken, string fixingUserName)
  {
   string Fixer;
   if (fixingUserName.Trim().Length == 0)
   {
    Fixer = Environment.UserName;
   }
   else
   {
    Fixer = fixingUserName;
   }
   try
   {
                string message = string.Format("{0} is fixing the build.", Fixer);
                MessageRequest request = PopulateRequest(new MessageRequest(), sessionToken);
                request.Message = message;
                ValidateResponse(manager.SendMessage(request));
   }
   catch (System.Net.Sockets.SocketException)
   {
   }
   catch (System.Runtime.Remoting.RemotingException)
   {
   }
  }
        public void AbortBuild(string sessionToken)
  {
   try
   {
    ValidateResponse(manager.AbortBuild(GenerateProjectRequest(sessionToken)));
   }
   catch (System.Net.Sockets.SocketException)
   {
   }
   catch (System.Runtime.Remoting.RemotingException)
   {
   }
  }
        public void StopProject(string sessionToken)
  {
   try
   {
                ProjectRequest request = GenerateProjectRequest(sessionToken);
                ValidateResponse(manager.Stop(request));
   }
   catch (System.Net.Sockets.SocketException)
   {
   }
   catch (System.Runtime.Remoting.RemotingException)
   {
   }
  }
        public void StartProject(string sessionToken)
  {
   try
   {
                ProjectRequest request = GenerateProjectRequest(sessionToken);
                ValidateResponse(manager.Start(request));
   }
   catch (System.Net.Sockets.SocketException)
   {
   }
   catch (System.Runtime.Remoting.RemotingException)
   {
   }
  }
        public void CancelPendingRequest(string sessionToken)
  {
   try
   {
                ProjectRequest request = GenerateProjectRequest(sessionToken);
                ValidateResponse(manager.CancelPendingRequest(request));
   }
   catch (System.Net.Sockets.SocketException)
   {
   }
   catch (System.Runtime.Remoting.RemotingException)
   {
   }
  }
  public string ProjectName
  {
   get { return projectName; }
  }
        public virtual ProjectStatusSnapshot RetrieveSnapshot()
        {
            var request = GenerateProjectRequest(null);
            var response = manager.TakeStatusSnapshot(request);
            ValidateResponse(response);
            ProjectStatusSnapshot snapshot = response.Snapshot;
            return snapshot;
        }
        public virtual PackageDetails[] RetrievePackageList()
        {
            var request = GenerateProjectRequest(null);
            var response = manager.RetrievePackageList(request);
            ValidateResponse(response);
            PackageDetails[] list = response.Packages.ToArray();
            return list;
        }
        public virtual IFileTransfer RetrieveFileTransfer(string fileName)
        {
            RemotingFileTransfer fileTransfer = manager.RetrieveFileTransfer(projectName, fileName);
            return fileTransfer;
        }
        public virtual List<ParameterBase> ListBuildParameters()
        {
            return manager.ListBuildParameters(GenerateProjectRequest(null)).Parameters;
        }
        private ProjectRequest GenerateProjectRequest(string sessionToken)
        {
            ProjectRequest request = PopulateRequest(new ProjectRequest(), sessionToken);
            return request;
        }
        private TRequest PopulateRequest<TRequest>(TRequest request, string sessionToken)
            where TRequest : ProjectRequest
        {
            request.SessionToken = sessionToken;
            request.ProjectName = projectName;
            return request;
        }
        private void ValidateResponse(Response value)
        {
            if (value.Result == ResponseResult.Failure)
            {
                string message = "Request request has failed on the remote server:" + Environment.NewLine +
                    value.ConcatenateErrors();
                throw new CruiseControlException(message);
            }
        }
 }
}
