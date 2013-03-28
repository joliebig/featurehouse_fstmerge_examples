using System;
using System.IO;
using System.Threading;
using ThoughtWorks.CruiseControl.Core.Queues;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core
{
 public class ProjectIntegrator : IProjectIntegrator, IDisposable, IIntegrationQueueNotifier
 {
  private readonly ITrigger trigger;
  private readonly IProject project;
  private readonly IIntegrationQueue integrationQueue;
  private Thread thread;
  private ProjectIntegratorState state = ProjectIntegratorState.Stopped;
        private int AmountOfSourceControlExceptions = 0;
  public ProjectIntegrator(IProject project, IIntegrationQueue integrationQueue)
  {
   trigger = project.Triggers;
   this.project = project;
   this.integrationQueue = integrationQueue;
            if (!Directory.Exists(project.WorkingDirectory))
                Directory.CreateDirectory(project.WorkingDirectory);
            if (!Directory.Exists(project.ArtifactDirectory))
                Directory.CreateDirectory(project.ArtifactDirectory);
        }
  public string Name
  {
   get { return project.Name; }
  }
  public IProject Project
  {
   get { return project; }
  }
  public ProjectIntegratorState State
  {
   get { return state; }
  }
  public IIntegrationRepository IntegrationRepository
  {
   get { return project.IntegrationRepository; }
  }
  public void Start()
  {
   lock (this)
   {
    if (IsRunning)
     return;
    state = ProjectIntegratorState.Running;
   }
   if (thread == null || thread.ThreadState == ThreadState.Stopped)
   {
    thread = new Thread(Run);
    thread.Name = project.Name;
   }
   if (thread.ThreadState != ThreadState.Running)
   {
    thread.Start();
   }
  }
  public void ForceBuild(string enforcerName)
  {
   Log.Info(string.Format("{0} forced Build for project: {1}", enforcerName, project.Name));
   AddToQueue(new IntegrationRequest(BuildCondition.ForceBuild, enforcerName));
   Start();
  }
  public void AbortBuild(string enforcerName)
  {
   Log.Info(string.Format("{0} aborted the running Build for project: {1}", enforcerName, project.Name));
   project.AbortRunningBuild();
  }
  public void Request(IntegrationRequest request)
  {
   AddToQueue(request);
   Start();
  }
  public void CancelPendingRequest()
  {
   integrationQueue.RemovePendingRequest(project);
  }
  private void Run()
  {
   Log.Info("Starting integrator for project: " + project.Name);
   try
   {
    while (IsRunning)
    {
     try
     {
      Integrate();
     }
     catch (Exception ex)
     {
      Log.Error(ex);
     }
     Thread.Sleep(100);
    }
   }
   catch (ThreadAbortException)
   {
    Thread.ResetAbort();
   }
   finally
   {
    Stopped();
   }
  }
  private void Integrate()
  {
            while (integrationQueue.IsLocked)
            {
                Thread.Sleep(200);
            }
            IntegrationRequest ir = integrationQueue.GetNextRequest(project);
   if (ir != null)
   {
                integrationQueue.ToggleQueueLocks(true);
                Log.Info(string.Format("Project: '{0}' is first in queue: '{1}' and shall start integration.",
                           project.Name, project.QueueName));
                IIntegrationResult result = new IntegrationResult();
    try
    {
     result = project.Integrate(ir);
    }
    finally
    {
     RemoveCompletedRequestFromQueue();
                    integrationQueue.ToggleQueueLocks(false);
                    if (result.SourceControlErrorOccured)
                    { AmountOfSourceControlExceptions++; }
                    else
                    { AmountOfSourceControlExceptions = 0; }
                    if (AmountOfSourceControlExceptions > project.MaxSourceControlRetries)
                    {
                        Stopped();
                    }
                }
   }
   else
   {
    PollTriggers();
    while (IsRunning && integrationQueue.HasItemPendingOnQueue(project) && !integrationQueue.IsLocked)
    {
     Thread.Sleep(200);
    }
   }
  }
  private void PollTriggers()
  {
   IntegrationRequest triggeredRequest = trigger.Fire();
   if (triggeredRequest != null)
   {
    AddToQueue(triggeredRequest);
   }
  }
  private void AddToQueue(IntegrationRequest request)
  {
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request, this));
  }
  private void RemoveCompletedRequestFromQueue()
  {
   integrationQueue.Dequeue();
  }
  private void Stopped()
  {
   state = ProjectIntegratorState.Stopped;
   thread = null;
   integrationQueue.RemoveProject(project);
   Log.Info("Integrator for project: " + project.Name + " is now stopped.");
  }
  public bool IsRunning
  {
   get { return state == ProjectIntegratorState.Running; }
  }
  public void Stop()
  {
   if (IsRunning)
   {
    Log.Info("Stopping integrator for project: " + project.Name);
    state = ProjectIntegratorState.Stopping;
   }
  }
  public void Abort()
  {
   if (thread != null)
   {
    Log.Info("Aborting integrator for project: " + project.Name);
    thread.Abort();
   }
  }
  public void WaitForExit()
  {
   if (thread != null && thread.IsAlive)
   {
    thread.Join();
   }
  }
  void IDisposable.Dispose()
  {
   Abort();
  }
  public void NotifyEnteringIntegrationQueue()
  {
   if (!integrationQueue.HasItemOnQueue(project))
   {
    project.NotifyPendingState();
   }
  }
  public void NotifyExitingIntegrationQueue(bool isPendingItemCancelled)
  {
   if (isPendingItemCancelled)
   {
    if (integrationQueue.GetNextRequest(project) == null)
    {
     project.NotifySleepingState();
    }
    else
    {
    }
   }
   else
   {
    if (!integrationQueue.HasItemPendingOnQueue(project))
    {
     project.NotifySleepingState();
    }
    else
    {
     project.NotifyPendingState();
    }
   }
   trigger.IntegrationCompleted();
  }
 }
}
