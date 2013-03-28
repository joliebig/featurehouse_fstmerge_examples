using System;
using ThoughtWorks.CruiseControl.Remote;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public class RemotingCruiseProjectManager : ICruiseProjectManager
 {
        private readonly CruiseServerClientBase manager;
  private readonly string projectName;
        public RemotingCruiseProjectManager(CruiseServerClientBase manager, string projectName)
  {
   this.manager = manager;
   this.projectName = projectName;
  }
        public void ForceBuild(string sessionToken, Dictionary<string, string> parameters)
  {
   try
   {
                manager.SessionToken = sessionToken;
                if (parameters != null)
                {
                    var buildValues = NameValuePair.FromDictionary(parameters);
                    manager.ForceBuild(projectName, buildValues);
                }
                else
                {
                    manager.ForceBuild(projectName);
                }
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
                manager.SessionToken = sessionToken;
                manager.SendMessage(projectName, new Message(message, Message.MessageKind.Fixer));
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
                manager.SessionToken = sessionToken;
                manager.AbortBuild(projectName);
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
                manager.SessionToken = sessionToken;
                manager.StopProject(projectName);
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
                manager.SessionToken = sessionToken;
                manager.StartProject(projectName);
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
                manager.SessionToken = sessionToken;
                manager.CancelPendingRequest(projectName);
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
            var snapshot = manager.TakeStatusSnapshot(projectName);
            return snapshot;
        }
        public virtual PackageDetails[] RetrievePackageList()
        {
            var list = manager.RetrievePackageList(projectName);
            return list.ToArray();
        }
        public virtual IFileTransfer RetrieveFileTransfer(string fileName)
        {
            var fileTransfer = manager.RetrieveFileTransfer(projectName, fileName);
            return fileTransfer;
        }
        public virtual List<ParameterBase> ListBuildParameters()
        {
            return manager.ListBuildParameters(projectName);
        }
 }
}
