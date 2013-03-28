using NMock;
using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core.Publishers;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Core;
using System.IO;
using System.Xml;
namespace ThoughtWorks.CruiseControl.UnitTests.Core.Publishers
{
    [TestFixture]
    public class PackagePublisherTests
    {
        private string dataFilePath;
        [TestFixtureSetUp]
        public void Setup()
        {
            string projectPackageList = Path.Combine(Path.GetTempPath(), "Test project-packages.xml");
            string buildFolder = Path.Combine(Path.GetTempPath(), "A Label");
            if (File.Exists(projectPackageList)) File.Delete(projectPackageList);
            if (Directory.Exists(buildFolder)) Directory.Delete(buildFolder, true);
            dataFilePath = Path.Combine(Path.GetTempPath(), "datafile.txt");
            if (File.Exists(dataFilePath)) File.Delete(dataFilePath);
            File.WriteAllText(dataFilePath, "This is a test file for the packaging publisher");
        }
        [TestFixtureTearDown]
        public void CleanUp()
        {
            if (File.Exists(dataFilePath)) File.Delete(dataFilePath);
        }
        [Test]
        public void CheckAllProperties()
        {
            PackagePublisher publisher = new PackagePublisher();
            publisher.AlwaysPackage = true;
            Assert.AreEqual(true, publisher.AlwaysPackage);
            publisher.BaseDirectory = "The BASE!";
            Assert.AreEqual("The BASE!", publisher.BaseDirectory);
            publisher.CompressionLevel = 9;
            Assert.AreEqual(9, publisher.CompressionLevel);
            publisher.Flatten = true;
            Assert.AreEqual(true, publisher.Flatten);
            ManifestGenerator generator = new ManifestGenerator();
            publisher.ManifestGenerator = generator;
            Assert.AreSame(generator, publisher.ManifestGenerator);
            publisher.PackageName = "Package name";
            Assert.AreEqual("Package name", publisher.PackageName);
            publisher.SingleInstance = true;
            Assert.AreEqual(true, publisher.SingleInstance);
            publisher.Files = new string[] { "filename" };
            Assert.AreEqual(1, publisher.Files.Length);
            Assert.AreEqual("filename", publisher.Files[0]);
        }
        [Test]
        public void CompressionLevelOnlyAllowedBetweenZeroAndNine()
        {
            PackagePublisher publisher = new PackagePublisher();
            bool exceptionThrown = false;
            try
            {
                publisher.CompressionLevel = -1;
            }
            catch (ArgumentOutOfRangeException)
            {
                exceptionThrown = true;
            }
            Assert.IsTrue(exceptionThrown, "CompressionLevel allowed a number less than zero");
            exceptionThrown = false;
            try
            {
                publisher.CompressionLevel = 10;
            }
            catch (ArgumentOutOfRangeException)
            {
                exceptionThrown = true;
            }
            Assert.IsTrue(exceptionThrown, "CompressionLevel allowed a number greater than nine");
            for (int loop = 0; loop <= 9; loop++)
            {
                publisher.CompressionLevel = loop;
            }
        }
        [Test]
        public void MinimalRun()
        {
            IntegrationRequest request = new IntegrationRequest(BuildCondition.ForceBuild, "Somewhere", null);
            IntegrationSummary summary = new IntegrationSummary(IntegrationStatus.Success, "A Label", "Another Label", new DateTime(2009, 1, 1));
            IntegrationResult result = new IntegrationResult("Test project", "Working directory", "Artifact directory", request, summary);
            Modification modification1 = GenerateModification("first file", "Add");
            Modification modification2 = GenerateModification("second file", "Modify");
            result.Modifications = new Modification[] { modification1, modification2 };
            result.Status = IntegrationStatus.Success;
            result.ArtifactDirectory = Path.GetTempPath();
            string packageLocation = Path.Combine(Path.GetTempPath(), "Test Package-1");
            string packageName = packageLocation + ".zip";
            if (File.Exists(packageName)) File.Delete(packageName);
            PackagePublisher publisher = new PackagePublisher();
            publisher.PackageName = packageLocation;
            publisher.Files = new string[] { dataFilePath };
            publisher.Run(result);
            Assert.IsTrue(File.Exists(packageName), "Package not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "Test project-packages.xml")), "Project package list not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "A Label\\Test project-packages.xml")), "Build package list not generated");
        }
        [Test]
        public void RunWithFlatten()
        {
            IntegrationRequest request = new IntegrationRequest(BuildCondition.ForceBuild, "Somewhere", null);
            IntegrationSummary summary = new IntegrationSummary(IntegrationStatus.Success, "A Label", "Another Label", new DateTime(2009, 1, 1));
            IntegrationResult result = new IntegrationResult("Test project", "Working directory", "Artifact directory", request, summary);
            Modification modification1 = GenerateModification("first file", "Add");
            Modification modification2 = GenerateModification("second file", "Modify");
            result.Modifications = new Modification[] { modification1, modification2 };
            result.Status = IntegrationStatus.Success;
            result.ArtifactDirectory = Path.GetTempPath();
            string packageLocation = Path.Combine(Path.GetTempPath(), "Test Package-1");
            string packageName = packageLocation + ".zip";
            if (File.Exists(packageName)) File.Delete(packageName);
            PackagePublisher publisher = new PackagePublisher();
            publisher.PackageName = packageLocation;
            publisher.Flatten = true;
            publisher.Files = new string[] { dataFilePath };
            publisher.Run(result);
            Assert.IsTrue(File.Exists(packageName), "Package not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "Test project-packages.xml")), "Project package list not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "A Label\\Test project-packages.xml")), "Build package list not generated");
        }
        [Test]
        public void RunWithRelativeFileAndBaseFolder()
        {
            IntegrationRequest request = new IntegrationRequest(BuildCondition.ForceBuild, "Somewhere", null);
            IntegrationSummary summary = new IntegrationSummary(IntegrationStatus.Success, "A Label", "Another Label", new DateTime(2009, 1, 1));
            IntegrationResult result = new IntegrationResult("Test project", "Working directory", "Artifact directory", request, summary);
            Modification modification1 = GenerateModification("first file", "Add");
            Modification modification2 = GenerateModification("second file", "Modify");
            result.Modifications = new Modification[] { modification1, modification2 };
            result.Status = IntegrationStatus.Success;
            result.ArtifactDirectory = Path.GetTempPath();
            string packageLocation = Path.Combine(Path.GetTempPath(), "Test Package-1");
            string packageName = packageLocation + ".zip";
            if (File.Exists(packageName)) File.Delete(packageName);
            PackagePublisher publisher = new PackagePublisher();
            publisher.PackageName = packageLocation;
            publisher.Flatten = true;
            publisher.BaseDirectory = Path.GetTempPath();
            publisher.Files = new string[] { "datafile.txt" };
            publisher.Run(result);
            Assert.IsTrue(File.Exists(packageName), "Package not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "Test project-packages.xml")), "Project package list not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "A Label\\Test project-packages.xml")), "Build package list not generated");
        }
        [Test]
        public void RunForNonExistantFiles()
        {
            IntegrationRequest request = new IntegrationRequest(BuildCondition.ForceBuild, "Somewhere", null);
            IntegrationSummary summary = new IntegrationSummary(IntegrationStatus.Success, "A Label", "Another Label", new DateTime(2009, 1, 1));
            IntegrationResult result = new IntegrationResult("Test project", "Working directory", "Artifact directory", request, summary);
            Modification modification1 = GenerateModification("first file", "Add");
            Modification modification2 = GenerateModification("second file", "Modify");
            result.Modifications = new Modification[] { modification1, modification2 };
            result.Status = IntegrationStatus.Success;
            result.ArtifactDirectory = Path.GetTempPath();
            string packageLocation = Path.Combine(Path.GetTempPath(), "Test Package-1");
            string packageName = packageLocation + ".zip";
            if (File.Exists(packageName)) File.Delete(packageName);
            PackagePublisher publisher = new PackagePublisher();
            publisher.PackageName = packageLocation;
            publisher.Files = new string[] { Path.GetTempFileName() };
            publisher.Run(result);
            Assert.IsTrue(File.Exists(packageName), "Package not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "Test project-packages.xml")), "Project package list not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "A Label\\Test project-packages.xml")), "Build package list not generated");
        }
        [Test]
        public void RunForWildCard()
        {
            IntegrationRequest request = new IntegrationRequest(BuildCondition.ForceBuild, "Somewhere", null);
            IntegrationSummary summary = new IntegrationSummary(IntegrationStatus.Success, "A Label", "Another Label", new DateTime(2009, 1, 1));
            IntegrationResult result = new IntegrationResult("Test project", "Working directory", "Artifact directory", request, summary);
            Modification modification1 = GenerateModification("first file", "Add");
            Modification modification2 = GenerateModification("second file", "Modify");
            result.Modifications = new Modification[] { modification1, modification2 };
            result.Status = IntegrationStatus.Success;
            result.ArtifactDirectory = Path.GetTempPath();
            string tempPath = Path.Combine(Path.GetTempPath(), "CCNetTest");
            if (Directory.Exists(tempPath)) Directory.Delete(tempPath, true);
            Directory.CreateDirectory(tempPath);
            File.WriteAllText(Path.Combine(tempPath, "test.txt"), "Some text data");
            File.WriteAllText(Path.Combine(tempPath, "test.tst"), "Some text data");
            File.WriteAllText(Path.Combine(tempPath, "test2.txt"), "Some text data");
            File.WriteAllText(Path.Combine(tempPath, "test2.tst"), "Some text data");
            string packageLocation = Path.Combine(Path.GetTempPath(), "Test Package-1");
            string packageName = packageLocation + ".zip";
            if (File.Exists(packageName)) File.Delete(packageName);
            PackagePublisher publisher = new PackagePublisher();
            publisher.PackageName = packageLocation;
            publisher.Files = new string[] { Path.Combine(tempPath, "*.txt") };
            publisher.Run(result);
            Assert.IsTrue(File.Exists(packageName), "Package not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "Test project-packages.xml")), "Project package list not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "A Label\\Test project-packages.xml")), "Build package list not generated");
        }
        [Test]
        public void RunForDirectoryWildCard()
        {
            IntegrationRequest request = new IntegrationRequest(BuildCondition.ForceBuild, "Somewhere", null);
            IntegrationSummary summary = new IntegrationSummary(IntegrationStatus.Success, "A Label", "Another Label", new DateTime(2009, 1, 1));
            IntegrationResult result = new IntegrationResult("Test project", "Working directory", "Artifact directory", request, summary);
            Modification modification1 = GenerateModification("first file", "Add");
            Modification modification2 = GenerateModification("second file", "Modify");
            result.Modifications = new Modification[] { modification1, modification2 };
            result.Status = IntegrationStatus.Success;
            result.ArtifactDirectory = Path.GetTempPath();
            string packageLocation = Path.Combine(Path.GetTempPath(), "Test Package-1");
            string packageName = packageLocation + ".zip";
            if (File.Exists(packageName)) File.Delete(packageName);
            PackagePublisher publisher = new PackagePublisher();
            publisher.PackageName = packageLocation;
            publisher.Files = new string[] { Path.Combine(Path.GetTempPath(), "**\\datafile.txt") };
            publisher.Run(result);
            Assert.IsTrue(File.Exists(packageName), "Package not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "Test project-packages.xml")), "Project package list not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "A Label\\Test project-packages.xml")), "Build package list not generated");
        }
        [Test]
        public void IncludeManifestInPackage()
        {
            IntegrationRequest request = new IntegrationRequest(BuildCondition.ForceBuild, "Somewhere", null);
            IntegrationSummary summary = new IntegrationSummary(IntegrationStatus.Success, "A Label", "Another Label", new DateTime(2009, 1, 1));
            IntegrationResult result = new IntegrationResult("Test project", "Working directory", "Artifact directory", request, summary);
            Modification modification1 = GenerateModification("first file", "Add");
            Modification modification2 = GenerateModification("second file", "Modify");
            result.Modifications = new Modification[] { modification1, modification2 };
            result.Status = IntegrationStatus.Success;
            result.ArtifactDirectory = Path.GetTempPath();
            XmlDocument manifest = new XmlDocument();
            manifest.AppendChild(manifest.CreateElement("manifest"));
            DynamicMock generatorMock = new DynamicMock(typeof(IManifestGenerator));
            List<string> files = new List<string>();
            files.Add(dataFilePath);
            generatorMock.ExpectAndReturn("Generate", manifest, result, files.ToArray());
            string packageLocation = Path.Combine(Path.GetTempPath(), "Test Package-1");
            string packageName = packageLocation + ".zip";
            if (File.Exists(packageName)) File.Delete(packageName);
            PackagePublisher publisher = new PackagePublisher();
            publisher.PackageName = packageLocation;
            publisher.ManifestGenerator = generatorMock.MockInstance as IManifestGenerator;
            publisher.Files = new string[] { dataFilePath };
            publisher.Run(result);
            Assert.IsTrue(File.Exists(packageName), "Package not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "Test project-packages.xml")), "Project package list not generated");
            Assert.IsTrue(File.Exists(Path.Combine(Path.GetTempPath(), "A Label\\Test project-packages.xml")), "Build package list not generated");
            generatorMock.Verify();
        }
        private Modification GenerateModification(string name, string type)
        {
            Modification modification = new Modification();
            modification.ChangeNumber = "1";
            modification.Comment = "A comment";
            modification.EmailAddress = "email@somewhere.com";
            modification.FileName = name;
            modification.ModifiedTime = new DateTime(2009, 1, 1);
            modification.Type = type;
            modification.UserName = "johnDoe";
            modification.Version = "1.1.1.1";
            return modification;
        }
    }
}
