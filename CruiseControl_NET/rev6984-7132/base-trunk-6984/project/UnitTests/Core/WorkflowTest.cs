using System;
using Exortech.NetReflector;
using NMock;
using NMock.Constraints;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.UnitTests.Core
{
 [TestFixture]
 public class WorkflowTest : IntegrationFixture
 {
  [Test]
  public void LoadConfiguration()
  {
   string xml = @"
<workflow name=""foo"">
 <tasks>
  <nullTask/>
  <nullTask/>
 </tasks>
</workflow>";
   object obj = NetReflector.Read(xml);
   Assert.IsNotNull(obj);
   Assert.IsTrue(obj is Workflow);
   Workflow project = (Workflow) obj;
   Assert.AreEqual("foo", project.Name);
   Assert.AreEqual(2, project.Tasks.Count);
   Assert.IsTrue(project.Tasks[0] is ITask);
   Assert.IsTrue(project.Tasks[1] is ITask);
  }
  [Test]
  public void Run()
  {
   Workflow project = new Workflow();
   IMock taskMock1 = new DynamicMock(typeof (ITask));
   taskMock1.Expect("Run", new NotNull());
   IMock taskMock2 = new DynamicMock(typeof (ITask));
   taskMock2.Expect("Run", new NotNull());
   IMock taskMock3 = new DynamicMock(typeof (ITask));
   taskMock3.Expect("Run", new NotNull());
   project.Tasks.Add(taskMock1.MockInstance);
   project.Tasks.Add(taskMock2.MockInstance);
   project.Integrate(ModificationExistRequest());
   Assert.IsNotNull(project.CurrentIntegration);
   Assert.IsNotNull(project.CurrentIntegration.StartTime);
   Assert.IsNotNull(project.CurrentIntegration.EndTime);
   Assert.IsNotNull(project.CurrentIntegration.EndTime > project.CurrentIntegration.StartTime);
   Assert.AreEqual(project.CurrentIntegration.Status, project.LatestBuildStatus);
   taskMock1.Verify();
   taskMock2.Verify();
  }
  [Test]
  public void RunForceBuild()
  {
   Workflow project = new Workflow();
   IMock taskMock1 = new DynamicMock(typeof (ITask));
   taskMock1.Expect("Run", new NotNull());
   IMock taskMock2 = new DynamicMock(typeof (ITask));
   taskMock2.Expect("Run", new NotNull());
   project.Tasks.Add(taskMock1.MockInstance);
   project.Tasks.Add(taskMock2.MockInstance);
   project.Integrate(ForceBuildRequest());
   taskMock1.Verify();
   taskMock2.Verify();
  }
  [Test]
  public void RunWhereFirstTaskThrowsException()
  {
   Workflow project = new Workflow();
   Exception ex = new CruiseControlException("foo");
   IMock taskMock1 = new DynamicMock(typeof (ITask));
   taskMock1.ExpectAndThrow("Run", ex, new NotNull());
   IMock taskMock2 = new DynamicMock(typeof (ITask));
   taskMock2.Expect("Run", new NotNull());
   project.Tasks.Add(taskMock1.MockInstance);
   project.Tasks.Add(taskMock2.MockInstance);
   IIntegrationResult result = project.Integrate(ModificationExistRequest());
   taskMock1.Verify();
   taskMock2.Verify();
   Assert.AreEqual(IntegrationStatus.Exception, result.Status);
   Assert.AreEqual(ex, result.ExceptionResult);
  }
 }
}
