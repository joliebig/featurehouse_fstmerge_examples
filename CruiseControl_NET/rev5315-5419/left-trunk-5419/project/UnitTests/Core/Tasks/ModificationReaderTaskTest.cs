using Exortech.NetReflector;
using NMock;
using NMock.Constraints;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Core.Tasks;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.UnitTests.Core.Sourcecontrol;
using ThoughtWorks.CruiseControl.UnitTests.Core.Util;
using System;
namespace ThoughtWorks.CruiseControl.UnitTests.Core.Tasks
{
    [TestFixture]
    public class ModificationReaderTaskTest
    {
        private ModificationWriterTask writerTask;
        private ModificationReaderTask readerTask;
        private IntegrationResult result;
        private Modification[] modifications;
        [SetUp]
        public void SetUp()
        {
            writerTask = new ModificationWriterTask();
            readerTask = new ModificationReaderTask();
            writerTask.AppendTimeStamp = true;
            ClearExistingModificationFiles();
            modifications = new Modification[]
    {
     ModificationMother.CreateModification("foo.txt", @"c\src"),
     ModificationMother.CreateModification("bar.txt", @"c\src")
    };
        }
        [TearDown]
        public void TearDown()
        {
            if (System.IO.Directory.Exists(readerTask.OutputPath))
            {
                System.IO.Directory.Delete(readerTask.OutputPath, true);
            }
        }
        [Test]
        public void ShouldReadModificationFile()
        {
            result = CreateSuccessfulWithModifications(DateTime.Now);
            writerTask.Run(result);
            result = CreateSuccessful(DateTime.Now.AddHours(1));
            Assert.AreEqual(0, result.Modifications.Length);
            readerTask.Run(result);
            Assert.AreEqual(2, result.Modifications.Length);
        }
        [Test]
        public void ShouldReadMultipleModificationFile()
        {
            result = CreateSuccessfulWithModifications(DateTime.Now);
            writerTask.Run(result);
            result = CreateSuccessfulWithModifications(DateTime.Now.AddHours(1));
            writerTask.Run(result);
            result = CreateSuccessful(DateTime.Now.AddHours(2));
            Assert.AreEqual(0, result.Modifications.Length);
            readerTask.Run(result);
            Assert.AreEqual(4, result.Modifications.Length);
        }
        [Test]
        public void ShouldAddReadModificationsToExistingOnes()
        {
            result = CreateSuccessfulWithModifications(DateTime.Now);
            writerTask.Run(result);
            result = CreateSuccessfulWithModifications(DateTime.Now.AddHours(1));
            Assert.AreEqual(2, result.Modifications.Length);
            readerTask.Run(result);
            Assert.AreEqual(4, result.Modifications.Length);
        }
        private void ClearExistingModificationFiles()
        {
            result = CreateSuccessful(DateTime.Now);
            writerTask.OutputPath = result.BaseFromArtifactsDirectory("ReaderTest");
            readerTask.OutputPath = writerTask.OutputPath;
            if (System.IO.Directory.Exists(readerTask.OutputPath))
            {
                System.IO.Directory.Delete(readerTask.OutputPath, true);
            }
            System.IO.Directory.CreateDirectory(readerTask.OutputPath);
        }
        private IntegrationResult CreateSuccessful(DateTime integrationTime)
        {
            return IntegrationResultMother.CreateSuccessful(integrationTime);
        }
        private IntegrationResult CreateSuccessfulWithModifications(DateTime integrationTime)
        {
            IntegrationResult temp = IntegrationResultMother.CreateSuccessful(integrationTime);
            temp.Modifications = modifications;
            return temp;
        }
    }
}
