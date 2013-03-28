using System.IO;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core;
namespace ThoughtWorks.CruiseControl.UnitTests.Core
{
 [TestFixture]
 public class ProjectBaseTest
 {
  private ProjectBase project;
  private class ConcreteProject : ProjectBase { }
  [SetUp]
  public void Setup()
  {
   project = new ConcreteProject();
  }
  [Test]
  public void ShouldReturnConfiguredWorkingDirectoryIfOneIsSet()
  {
   project.ConfiguredWorkingDirectory = @"C:\my\working\directory";
   Assert.AreEqual(@"C:\my\working\directory", project.WorkingDirectory);
  }
  [Test]
  public void ShouldReturnCalculatedWorkingDirectoryIfOneIsNotSet()
  {
   project.Name = "myProject";
   Assert.AreEqual(new DirectoryInfo(@"myProject\WorkingDirectory").FullName, project.WorkingDirectory);
  }
  [Test]
  public void ShouldReturnConfiguredArtifactDirectoryIfOneIsSet()
  {
   project.ConfiguredArtifactDirectory = @"C:\my\artifacts";
   Assert.AreEqual(@"C:\my\artifacts", project.ArtifactDirectory);
  }
  [Test]
  public void ShouldReturnCalculatedArtifactDirectoryIfOneIsNotSet()
  {
   project.Name = "myProject";
   Assert.AreEqual(new DirectoryInfo(@"myProject\Artifacts").FullName, project.ArtifactDirectory);
  }
 }
}
