using System;
using ThoughtWorks.CruiseControl.Remote;
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
  public void ForceBuild()
  {
   try
   {
    manager.Request(ProjectName, new IntegrationRequest(BuildCondition.ForceBuild, Environment.UserName));
   }
   catch (System.Net.Sockets.SocketException)
   {
   }
   catch (System.Runtime.Remoting.RemotingException)
   {
   }
  }
  public void FixBuild(string fixingUserName)
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
    manager.SendMessage(ProjectName, new Message(string.Format("{0} is fixing the build.", Fixer)));
   }
   catch (System.Net.Sockets.SocketException)
   {
   }
   catch (System.Runtime.Remoting.RemotingException)
   {
   }
  }
  public void AbortBuild()
  {
   try
   {
    manager.AbortBuild(ProjectName, Environment.UserName);
   }
   catch (System.Net.Sockets.SocketException)
   {
   }
   catch (System.Runtime.Remoting.RemotingException)
   {
   }
  }
  public void StopProject()
  {
   try
   {
    manager.Stop(projectName);
   }
   catch (System.Net.Sockets.SocketException)
   {
   }
   catch (System.Runtime.Remoting.RemotingException)
   {
   }
  }
  public void StartProject()
  {
   try
   {
    manager.Start(projectName);
   }
   catch (System.Net.Sockets.SocketException)
   {
   }
   catch (System.Runtime.Remoting.RemotingException)
   {
   }
  }
  public void CancelPendingRequest()
  {
   try
   {
    manager.CancelPendingRequest(ProjectName);
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
 }
}
