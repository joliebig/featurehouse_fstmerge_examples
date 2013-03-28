using System.IO;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core;
using System;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.UnitTests.Core
{
 [TestFixture]
 public class ProjectBaseTest
 {
  private ProjectBase project;
  private class ConcreteProject : ProjectBase { }
  private IExecutionEnvironment executionEnvironment;
  [SetUp]
  public void Setup()
  {
   executionEnvironment = new ExecutionEnvironment();
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
   Assert.AreEqual(
    Path.Combine(executionEnvironment.GetDefaultProgramDataFolder(ApplicationType.Server),
                    Path.Combine("myProject", "WorkingDirectory")),
                project.WorkingDirectory);
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
   Assert.AreEqual(
    Path.Combine(executionEnvironment.GetDefaultProgramDataFolder(ApplicationType.Server),
                    Path.Combine("myProject", "Artifacts")),
                project.ArtifactDirectory);
  }
 }
}
