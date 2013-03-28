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
   string workingDir = Path.GetFullPath(Path.Combine(".", "workingdir"));
   project.ConfiguredWorkingDirectory = workingDir;
   Assert.AreEqual(workingDir, project.WorkingDirectory);
  }
  [Test]
  public void ShouldReturnCalculatedWorkingDirectoryIfOneIsNotSet()
  {
   project.Name = "myProject";
   Assert.AreEqual(new DirectoryInfo(Path.Combine("myProject", "WorkingDirectory")).FullName, project.WorkingDirectory);
  }
  [Test]
  public void ShouldReturnConfiguredArtifactDirectoryIfOneIsSet()
  {
   string artifactDir = Path.GetFullPath(Path.Combine(".", "artifacts"));
   project.ConfiguredArtifactDirectory = artifactDir;
   Assert.AreEqual(artifactDir, project.ArtifactDirectory);
  }
  [Test]
  public void ShouldReturnCalculatedArtifactDirectoryIfOneIsNotSet()
  {
   project.Name = "myProject";
   Assert.AreEqual(new DirectoryInfo(Path.Combine("myProject", "Artifacts")).FullName, project.ArtifactDirectory);
  }
 }
}
