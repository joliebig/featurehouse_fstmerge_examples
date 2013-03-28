using System;
using ThoughtWorks.CruiseControl.Remote;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Parameters;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public class RemotingCruiseProjectManager : ICruiseProjectManager
 {
  private readonly ICruiseManager manager;
  private readonly string projectName;
        public RemotingCruiseProjectManager(ICruiseManager manager, string projectName)
  {
   this.manager = manager;
   this.projectName = projectName;
  }
        public void ForceBuild(string sessionToken, Dictionary<string, string> parameters)
  {
   try
   {
    manager.Request(sessionToken, ProjectName, new IntegrationRequest(BuildCondition.ForceBuild, Environment.UserName));
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
                manager.SendMessage(sessionToken, ProjectName, new Message(string.Format("{0} is fixing the build.", Fixer)));
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
    manager.AbortBuild(sessionToken, ProjectName, Environment.UserName);
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
                manager.Stop(sessionToken, projectName);
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
                manager.Start(sessionToken, projectName);
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
                manager.CancelPendingRequest(sessionToken, ProjectName);
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
            ProjectStatusSnapshot snapshot = manager.TakeStatusSnapshot(projectName);
            return snapshot;
        }
        public virtual PackageDetails[] RetrievePackageList()
        {
            PackageDetails[] list = manager.RetrievePackageList(projectName);
            return list;
        }
        public virtual IFileTransfer RetrieveFileTransfer(string fileName)
        {
            RemotingFileTransfer fileTransfer = manager.RetrieveFileTransfer(projectName, fileName);
            return fileTransfer;
        }
        public virtual List<ParameterBase> ListBuildParameters()
        {
            return manager.ListBuildParameters(projectName);
        }
 }
}
