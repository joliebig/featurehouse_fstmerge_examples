using System.IO;
using System.Reflection;
using NMock.Constraints;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Core.Queues;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.UnitTests.UnitTestUtils;
namespace ThoughtWorks.CruiseControl.UnitTests.Core
{
 [TestFixture]
 public class ProjectIntegratorTest : IntegrationFixture
 {
  private const string TestQueueName = "projectQueue";
     private static readonly string tempDir = Path.GetTempPath() + Assembly.GetExecutingAssembly().FullName + "\\";
  private LatchMock integrationTriggerMock;
  private LatchMock projectMock;
  private ProjectIntegrator integrator;
  private IntegrationQueueSet integrationQueues;
  private IIntegrationQueue integrationQueue;
        private readonly string tempWorkingDir1 = tempDir + "tempWorkingDir1";
        private readonly string tempArtifactDir1 = tempDir + "tempArtifactDir1";
  [SetUp]
  public void SetUp()
  {
   integrationTriggerMock = new LatchMock(typeof (ITrigger));
   integrationTriggerMock.Strict = true;
   projectMock = new LatchMock(typeof (IProject));
   projectMock.Strict = true;
   projectMock.SetupResult("Name", "project");
   projectMock.SetupResult("QueueName", TestQueueName);
   projectMock.SetupResult("QueuePriority", 0);
   projectMock.SetupResult("Triggers", integrationTriggerMock.MockInstance);
            projectMock.SetupResult("WorkingDirectory", tempWorkingDir1);
            projectMock.SetupResult("ArtifactDirectory", tempArtifactDir1);
   integrationQueues = new IntegrationQueueSet();
            integrationQueues.Add(TestQueueName, new DefaultQueueConfiguration(TestQueueName));
   integrationQueue = integrationQueues[TestQueueName];
   integrator = new ProjectIntegrator((IProject) projectMock.MockInstance, integrationQueue);
  }
  [TearDown]
  public void TearDown()
  {
   if (integrator != null)
   {
    integrator.Stop();
    integrator.WaitForExit();
   }
            if (Directory.Exists(tempWorkingDir1))
                Directory.Delete(tempWorkingDir1, true);
            if (Directory.Exists(tempArtifactDir1))
                Directory.Delete(tempArtifactDir1, true);
        }
  private void VerifyAll()
  {
   integrationTriggerMock.Verify();
   projectMock.Verify();
  }
  [Test]
  public void ShouldContinueRunningIfNotToldToStop()
  {
   integrationTriggerMock.SetupResultAndSignal("Fire", null);
   projectMock.ExpectNoCall("Integrate", typeof (IntegrationRequest));
   integrationTriggerMock.ExpectNoCall("IntegrationCompleted");
   integrator.Start();
   integrationTriggerMock.WaitForSignal();
   Assert.AreEqual(ProjectIntegratorState.Running, integrator.State);
   VerifyAll();
  }
  [Test]
  public void ShouldStopWhenStoppedExternally()
  {
   integrationTriggerMock.SetupResultAndSignal("Fire", null);
   projectMock.ExpectNoCall("NotifyPendingState");
   projectMock.ExpectNoCall("Integrate", typeof (IntegrationRequest));
   projectMock.ExpectNoCall("NotifySleepingState");
   integrationTriggerMock.ExpectNoCall("IntegrationCompleted");
   integrator.Start();
   integrationTriggerMock.WaitForSignal();
   Assert.AreEqual(ProjectIntegratorState.Running, integrator.State);
   integrator.Stop();
   integrator.WaitForExit();
   Assert.AreEqual(ProjectIntegratorState.Stopped, integrator.State);
   VerifyAll();
  }
  [Test]
  public void StartMultipleTimes()
  {
   integrationTriggerMock.SetupResultAndSignal("Fire", null);
   projectMock.ExpectNoCall("NotifyPendingState");
   projectMock.ExpectNoCall("Integrate", typeof (IntegrationRequest));
   projectMock.ExpectNoCall("NotifySleepingState");
   integrationTriggerMock.ExpectNoCall("IntegrationCompleted");
   integrator.Start();
   integrator.Start();
   integrator.Start();
   integrationTriggerMock.WaitForSignal();
   Assert.AreEqual(ProjectIntegratorState.Running, integrator.State);
   integrator.Stop();
   integrator.WaitForExit();
   Assert.AreEqual(ProjectIntegratorState.Stopped, integrator.State);
   VerifyAll();
  }
  [Test]
  public void RestartIntegrator()
  {
   integrationTriggerMock.SetupResultAndSignal("Fire", null);
   projectMock.ExpectNoCall("NotifyPendingState");
   projectMock.ExpectNoCall("Integrate", typeof (IntegrationRequest));
   projectMock.ExpectNoCall("NotifySleepingState");
   integrationTriggerMock.ExpectNoCall("IntegrationCompleted");
   integrator.Start();
   integrationTriggerMock.WaitForSignal();
   integrator.Stop();
   integrator.WaitForExit();
   integrationTriggerMock.ResetLatch();
   integrator.Start();
   integrationTriggerMock.WaitForSignal();
   integrator.Stop();
   integrator.WaitForExit();
   VerifyAll();
  }
  [Test]
  public void StopUnstartedIntegrator()
  {
   integrationTriggerMock.ExpectNoCall("Fire");
   projectMock.ExpectNoCall("NotifyPendingState");
   projectMock.ExpectNoCall("Integrate", typeof (IntegrationRequest));
   projectMock.ExpectNoCall("NotifySleepingState");
   integrationTriggerMock.ExpectNoCall("IntegrationCompleted");
   integrator.Stop();
   Assert.AreEqual(ProjectIntegratorState.Stopped, integrator.State);
   VerifyAll();
  }
  [Test]
  public void VerifyStateAfterException()
  {
   string exceptionMessage = "Intentional exception";
   integrationTriggerMock.ExpectAndReturn("Fire", ForceBuildRequest());
   projectMock.Expect("NotifyPendingState");
   projectMock.ExpectAndThrow("Integrate", new CruiseControlException(exceptionMessage), new HasForceBuildCondition());
   projectMock.ExpectAndSignal("NotifySleepingState");
            projectMock.ExpectAndReturn("MaxSourceControlRetries", 5);
            projectMock.SetupResult("SourceControlErrorHandling", ThoughtWorks.CruiseControl.Core.Sourcecontrol.Common.SourceControlErrorHandlingPolicy.ReportEveryFailure);
            integrationTriggerMock.Expect("IntegrationCompleted");
   integrator.Start();
   projectMock.WaitForSignal();
   Assert.AreEqual(ProjectIntegratorState.Running, integrator.State);
   integrator.Stop();
   integrator.WaitForExit();
   Assert.AreEqual(ProjectIntegratorState.Stopped, integrator.State);
   VerifyAll();
  }
  [Test]
  public void Abort()
  {
   integrationTriggerMock.SetupResultAndSignal("Fire", null);
   projectMock.ExpectNoCall("NotifyPendingState");
   projectMock.ExpectNoCall("Integrate", typeof (IntegrationRequest));
   projectMock.ExpectNoCall("NotifySleepingState");
   integrationTriggerMock.ExpectNoCall("IntegrationCompleted");
   integrator.Start();
   integrationTriggerMock.WaitForSignal();
   Assert.AreEqual(ProjectIntegratorState.Running, integrator.State);
   integrator.Abort();
   integrator.WaitForExit();
   Assert.AreEqual(ProjectIntegratorState.Stopped, integrator.State);
   VerifyAll();
  }
  [Test]
  public void TerminateWhenProjectIsntStarted()
  {
   integrationTriggerMock.SetupResultAndSignal("Fire", null);
   projectMock.ExpectNoCall("NotifyPendingState");
   projectMock.ExpectNoCall("Integrate", typeof (IntegrationRequest));
   projectMock.ExpectNoCall("NotifySleepingState");
   integrationTriggerMock.ExpectNoCall("IntegrationCompleted");
   integrator.Abort();
   Assert.AreEqual(ProjectIntegratorState.Stopped, integrator.State);
   VerifyAll();
  }
  [Test]
  public void TerminateCalledTwice()
  {
   integrationTriggerMock.SetupResultAndSignal("Fire", null);
   projectMock.ExpectNoCall("NotifyPendingState");
   projectMock.ExpectNoCall("Integrate", typeof (IntegrationRequest));
   projectMock.ExpectNoCall("NotifySleepingState");
   integrationTriggerMock.ExpectNoCall("IntegrationCompleted");
   integrator.Start();
   integrationTriggerMock.WaitForSignal();
   Assert.AreEqual(ProjectIntegratorState.Running, integrator.State);
   integrator.Abort();
   integrator.Abort();
   VerifyAll();
  }
  [Test]
  public void ForceBuild()
  {
   integrationTriggerMock.ExpectNoCall("Fire");
   projectMock.Expect("Integrate", new HasForceBuildCondition());
   projectMock.Expect("NotifyPendingState");
   projectMock.ExpectAndSignal("NotifySleepingState");
   projectMock.ExpectNoCall("Integrate", typeof (IntegrationRequest));
            projectMock.SetupResult("MaxSourceControlRetries", 5);
            projectMock.SetupResult("SourceControlErrorHandling", ThoughtWorks.CruiseControl.Core.Sourcecontrol.Common.SourceControlErrorHandlingPolicy.ReportEveryFailure);
   integrationTriggerMock.ExpectAndSignal("IntegrationCompleted");
            integrator.ForceBuild("BuildForcer");
   integrationTriggerMock.WaitForSignal();
   projectMock.WaitForSignal();
   VerifyAll();
  }
  [Test]
  public void RequestIntegration()
  {
   IntegrationRequest request = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.Expect("NotifyPendingState");
   projectMock.Expect("Integrate", request);
   projectMock.ExpectAndSignal("NotifySleepingState");
            projectMock.SetupResult("MaxSourceControlRetries", 5);
            projectMock.SetupResult("SourceControlErrorHandling", ThoughtWorks.CruiseControl.Core.Sourcecontrol.Common.SourceControlErrorHandlingPolicy.ReportEveryFailure);
            integrationTriggerMock.ExpectAndSignal("IntegrationCompleted");
   integrator.Request(request);
   integrationTriggerMock.WaitForSignal();
   projectMock.WaitForSignal();
   Assert.AreEqual(ProjectIntegratorState.Running, integrator.State);
   VerifyAll();
  }
  [Test]
  public void ShouldClearRequestQueueAsSoonAsRequestIsProcessed()
  {
   IntegrationRequest request = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.Expect("NotifyPendingState");
   projectMock.Expect("Integrate", request);
   projectMock.ExpectAndSignal("NotifySleepingState");
            projectMock.SetupResult("MaxSourceControlRetries", 5);
            projectMock.SetupResult("SourceControlErrorHandling", ThoughtWorks.CruiseControl.Core.Sourcecontrol.Common.SourceControlErrorHandlingPolicy.ReportEveryFailure);
            integrationTriggerMock.Expect("IntegrationCompleted");
   integrationTriggerMock.ExpectAndReturnAndSignal("Fire", null);
   integrator.Request(request);
   projectMock.WaitForSignal();
   integrationTriggerMock.WaitForSignal();
   VerifyAll();
  }
  [Test]
  public void CancelPendingRequestDoesNothingForNoPendingItems()
  {
   int queuedItemCount = integrationQueue.GetQueuedIntegrations().Length;
   integrator.CancelPendingRequest();
   Assert.AreEqual(queuedItemCount, integrationQueue.GetQueuedIntegrations().Length);
   VerifyAll();
  }
  [Test]
  public void CancelPendingRequestRemovesPendingItems()
  {
   IProject project = (IProject) projectMock.MockInstance;
   IntegrationRequest request1 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.Expect("NotifyPendingState");
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request1, integrator));
   IntegrationRequest request2 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.ExpectNoCall("NotifyPendingState");
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request2, integrator));
   int queuedItemCount = integrationQueue.GetQueuedIntegrations().Length;
   Assert.AreEqual(2, queuedItemCount);
   integrationTriggerMock.Expect("IntegrationCompleted");
   integrator.CancelPendingRequest();
   queuedItemCount = integrationQueue.GetQueuedIntegrations().Length;
   Assert.AreEqual(1, queuedItemCount);
   VerifyAll();
  }
  [Test]
  public void FirstBuildOfProjectShouldSetToPending()
  {
   IProject project = (IProject) projectMock.MockInstance;
   IntegrationRequest request1 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.Expect("NotifyPendingState");
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request1, integrator));
   VerifyAll();
  }
  [Test]
  public void SecondBuildOfProjectShouldNotSetToPendingWhenQueued()
  {
   IProject project = (IProject) projectMock.MockInstance;
   IntegrationRequest request1 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.Expect("NotifyPendingState");
   IntegrationRequest request2 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.ExpectNoCall("NotifyPendingState");
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request1, integrator));
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request2, integrator));
   VerifyAll();
  }
  [Test]
  public void CompletingOnlyQueueBuildGoesToSleepingState()
  {
   IProject project = (IProject) projectMock.MockInstance;
   IntegrationRequest request1 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.Expect("NotifyPendingState");
   integrationTriggerMock.Expect("IntegrationCompleted");
   projectMock.Expect("NotifySleepingState");
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request1, integrator));
   integrationQueue.Dequeue();
   VerifyAll();
  }
  [Test]
  public void CompletingWithPendingQueueBuildGoesToPendingState()
  {
   IProject project = (IProject) projectMock.MockInstance;
   IntegrationRequest request1 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.Expect("NotifyPendingState");
   IntegrationRequest request2 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   integrationTriggerMock.Expect("IntegrationCompleted");
   projectMock.Expect("NotifyPendingState");
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request1, integrator));
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request2, integrator));
   integrationQueue.Dequeue();
   VerifyAll();
  }
  [Test]
  public void CompletingAllPendingQueueBuildsGoesToPendingState()
  {
   IProject project = (IProject) projectMock.MockInstance;
   IntegrationRequest request1 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.Expect("NotifyPendingState");
   IntegrationRequest request2 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   integrationTriggerMock.Expect("IntegrationCompleted");
   projectMock.Expect("NotifyPendingState");
   integrationTriggerMock.Expect("IntegrationCompleted");
   projectMock.Expect("NotifySleepingState");
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request1, integrator));
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request2, integrator));
   integrationQueue.Dequeue();
   integrationQueue.Dequeue();
   VerifyAll();
  }
  [Test]
  public void CancellingAPendingRequestWhileBuildingIgnoresState()
  {
   IProject project = (IProject) projectMock.MockInstance;
   IntegrationRequest request1 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.Expect("NotifyPendingState");
   IntegrationRequest request2 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.ExpectNoCall("NotifyPendingState");
   projectMock.ExpectNoCall("NotifySleepingState");
   integrationTriggerMock.Expect("IntegrationCompleted");
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request1, integrator));
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request2, integrator));
   integrator.CancelPendingRequest();
   VerifyAll();
  }
  [Test]
  public void CancellingAPendingRequestWhileNotBuildingGoesToSleeping()
  {
   LatchMock otherProjectMock = new LatchMock(typeof (IProject));
   otherProjectMock.Strict = true;
   otherProjectMock.SetupResult("Name", "otherProject");
   otherProjectMock.SetupResult("QueueName", TestQueueName);
   otherProjectMock.SetupResult("QueuePriority", 0);
   otherProjectMock.SetupResult("Triggers", integrationTriggerMock.MockInstance);
            otherProjectMock.SetupResult("WorkingDirectory", tempDir + "tempWorkingDir2");
            otherProjectMock.SetupResult("ArtifactDirectory", tempDir + "tempArtifactDir2");
   IProject otherProject = (IProject) otherProjectMock.MockInstance;
   IProject project = (IProject) projectMock.MockInstance;
   ProjectIntegrator otherIntegrator = new ProjectIntegrator(otherProject, integrationQueue);
   IntegrationRequest otherProjectRequest = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   otherProjectMock.Expect("NotifyPendingState");
   IntegrationRequest request2 = new IntegrationRequest(BuildCondition.IfModificationExists, "intervalTrigger");
   projectMock.Expect("NotifyPendingState");
   projectMock.Expect("NotifySleepingState");
   integrationTriggerMock.Expect("IntegrationCompleted");
   integrationQueue.Enqueue(new IntegrationQueueItem(otherProject, otherProjectRequest, otherIntegrator));
   integrationQueue.Enqueue(new IntegrationQueueItem(project, request2, integrator));
   integrator.CancelPendingRequest();
   otherProjectMock.Verify();
   VerifyAll();
  }
 }
 public class HasForceBuildCondition : BaseConstraint
 {
  public override bool Eval(object val)
  {
   return ((IntegrationRequest) val).BuildCondition == BuildCondition.ForceBuild;
  }
  public override string Message
  {
   get { return "IntegrationRequest is not ForceBuild."; }
  }
 }
}
