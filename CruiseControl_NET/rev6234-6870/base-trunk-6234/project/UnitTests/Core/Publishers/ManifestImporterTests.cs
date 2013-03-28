using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core.Publishers;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Core;
using System.Xml;
using System.IO;
namespace ThoughtWorks.CruiseControl.UnitTests.Core.Publishers
{
    [TestFixture]
    public class ManifestImporterTests
    {
        [Test]
        public void CheckAllProperties()
        {
            ManifestImporter generator = new ManifestImporter();
            generator.FileName = "File name";
            Assert.AreEqual("File name", generator.FileName);
        }
        [Test]
        public void ImportAbsoluteBasedManifest()
        {
            string sourceFile = Path.Combine(Path.GetTempPath(), "ImportManifest.xml");
            string expectedManifest = "<manifest>" +
                    "from_ a file" +
                "</manifest>";
            if (File.Exists(sourceFile)) File.Delete(sourceFile);
            File.WriteAllText(sourceFile, expectedManifest);
            ManifestImporter generator = new ManifestImporter();
            generator.FileName = sourceFile;
            IntegrationRequest request = new IntegrationRequest(BuildCondition.ForceBuild, "Somewhere", null);
            IntegrationSummary summary = new IntegrationSummary(IntegrationStatus.Success, "A Label", "Another Label", new DateTime(2009, 1, 1));
            IntegrationResult result = new IntegrationResult("Test project", "Working directory", "Artifact directory", request, summary);
            List<string> files = new List<string>();
            XmlDocument manifest = generator.Generate(result, files.ToArray());
            Assert.IsNotNull(manifest);
            string actualManifest = manifest.OuterXml;
            Assert.AreEqual(expectedManifest, actualManifest);
        }
        [Test]
        public void ImportRelativeBasedManifest()
        {
            string sourceFile = Path.Combine(Path.GetTempPath(), "ImportManifest.xml");
            string expectedManifest = "<manifest>" +
                    "from_ a file" +
                "</manifest>";
            if (File.Exists(sourceFile)) File.Delete(sourceFile);
            File.WriteAllText(sourceFile, expectedManifest);
            ManifestImporter generator = new ManifestImporter();
            generator.FileName = "ImportManifest.xml";
            IntegrationRequest request = new IntegrationRequest(BuildCondition.ForceBuild, "Somewhere", null);
            IntegrationSummary summary = new IntegrationSummary(IntegrationStatus.Success, "A Label", "Another Label", new DateTime(2009, 1, 1));
            IntegrationResult result = new IntegrationResult("Test project", "Working directory", "Artifact directory", request, summary);
            List<string> files = new List<string>();
            result.WorkingDirectory = Path.GetTempPath();
            XmlDocument manifest = generator.Generate(result, files.ToArray());
            Assert.IsNotNull(manifest);
            string actualManifest = manifest.OuterXml;
            Assert.AreEqual(expectedManifest, actualManifest);
        }
        [Test]
        public void ImportWithoutAFilename()
        {
            ManifestImporter generator = new ManifestImporter();
            Assert.That(delegate { generator.Generate(null, null); },
                        Throws.TypeOf<ArgumentOutOfRangeException>().With.Property("ParamName").EqualTo("FileName"));
        }
    }
}
