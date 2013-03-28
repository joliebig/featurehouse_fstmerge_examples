namespace ThoughtWorks.CruiseControl.UnitTests.Core.Tasks
{
    using System;
    using System.IO;
    using NUnit.Framework;
    using Rhino.Mocks;
    using ThoughtWorks.CruiseControl.Core;
    using ThoughtWorks.CruiseControl.Core.Tasks;
    using ThoughtWorks.CruiseControl.Core.Util;
    using Constraints = Rhino.Mocks.Constraints;
    [TestFixture]
    public class TaskContextTests
    {
        private MockRepository mocks;
        [SetUp]
        public void Setup()
        {
            this.mocks = new MockRepository();
        }
        [Test]
        public void CreateResultStreamValidatesTaskNameIsNotNullOrEmpty()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            var context = new TaskContext(ioSystem, basePath);
            this.mocks.ReplayAll();
            var error1 = Assert.Throws<ArgumentException>(() => context.CreateResultStream(null, "type"));
            var error2 = Assert.Throws<ArgumentException>(() => context.CreateResultStream(string.Empty, "type"));
            this.mocks.VerifyAll();
            Assert.AreEqual("taskName", error1.ParamName);
            Assert.AreEqual("taskName", error2.ParamName);
        }
        [Test]
        public void CreateResultStreamValidatesTaskTypeIsNotNullOrEmpty()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            var context = new TaskContext(ioSystem, basePath);
            this.mocks.ReplayAll();
            var error1 = Assert.Throws<ArgumentException>(() => context.CreateResultStream("name", null)) as ArgumentException;
            var error2 = Assert.Throws<ArgumentException>(() => context.CreateResultStream("name", string.Empty)) as ArgumentException;
            this.mocks.VerifyAll();
            Assert.AreEqual("taskType", error1.ParamName);
            Assert.AreEqual("taskType", error2.ParamName);
        }
        [Test]
        public void CreateResultStreamOpensAStream()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            var filePath = Path.Combine(basePath, "Test.xml");
            Expect.Call(ioSystem.FileExists(filePath)).Return(false);
            using (var fileStream = new MemoryStream())
            {
                Expect.Call(ioSystem.OpenOutputStream(filePath)).Return(fileStream);
                var context = new TaskContext(ioSystem, basePath);
                this.mocks.ReplayAll();
                var stream = context.CreateResultStream("Test", "Type");
                this.mocks.VerifyAll();
                Assert.AreSame(fileStream, stream);
            }
        }
        [Test]
        public void CreateResultStreamOpensAStreamForDuplicatedName()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            var filePath1 = Path.Combine(basePath, "Test.xml");
            var filePath2 = Path.Combine(basePath, "Test-1.xml");
            Expect.Call(ioSystem.FileExists(filePath1)).Return(true);
            Expect.Call(ioSystem.FileExists(filePath2)).Return(false);
            using (var fileStream = new FileStream(filePath2, FileMode.Create))
            {
                Expect.Call(ioSystem.OpenOutputStream(filePath2)).Return(fileStream);
                var context = new TaskContext(ioSystem, basePath);
                this.mocks.ReplayAll();
                var stream = context.CreateResultStream("Test.txt", "Type");
                this.mocks.VerifyAll();
                Assert.AreSame(fileStream, stream);
            }
        }
        [Test]
        public void CreateResultStreamOpensAStreamWithOriginalExtension()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            var filePath = Path.Combine(basePath, "Test.txt");
            Expect.Call(ioSystem.FileExists(filePath)).Return(false);
            using (var fileStream = new MemoryStream())
            {
                Expect.Call(ioSystem.OpenOutputStream(filePath)).Return(fileStream);
                var context = new TaskContext(ioSystem, basePath);
                this.mocks.ReplayAll();
                var stream = context.CreateResultStream("Test.txt", "Type", true);
                this.mocks.VerifyAll();
                Assert.AreSame(fileStream, stream);
            }
        }
        [Test]
        public void StartChildContextStartANewContext()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            var likeCondition = basePath.Replace("\\", "\\\\") + "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
            Expect.Call(() => ioSystem.EnsureFolderExists(null))
                .Constraints(
                    new Constraints.Like(likeCondition));
            var context = new TaskContext(ioSystem, basePath);
            mocks.ReplayAll();
            var childContext = context.StartChildContext();
            mocks.VerifyAll();
            StringAssert.IsMatch(likeCondition, childContext.ArtifactFolder);
            Assert.IsFalse(childContext.IsFinialised);
        }
        [Test]
        public void MergeChildContextFinialisesTheChildContext()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            Expect.Call(() => ioSystem.EnsureFolderExists(null)).IgnoreArguments();
            var context = new TaskContext(ioSystem, basePath);
            mocks.ReplayAll();
            var childContext = context.StartChildContext();
            context.MergeChildContext(childContext);
            mocks.VerifyAll();
            Assert.IsTrue(childContext.IsFinialised);
        }
        [Test]
        public void MergeChildContextMergesChildTaskResults()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            Expect.Call(() => ioSystem.EnsureFolderExists(null)).IgnoreArguments();
            using (var fileStream = new MemoryStream())
            {
                var likeCondition = basePath.Replace("\\", "\\\\") + "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}\\\\test.xml";
                Expect.Call(ioSystem.FileExists(null))
                    .Return(false)
                    .Constraints(new Constraints.Like(likeCondition));
                Expect.Call(ioSystem.OpenOutputStream(null))
                    .Return(fileStream)
                    .Constraints(new Constraints.Like(likeCondition));
                Expect.Call(ioSystem.FileExists(Path.Combine(basePath, "test.xml"))).Return(false);
                Expect.Call(() => ioSystem.MoveFile(null, null))
                    .Constraints(new Constraints.Like(likeCondition), new Constraints.Equal(Path.Combine(basePath, "test.xml")));
                var context = new TaskContext(ioSystem, basePath);
                mocks.ReplayAll();
                var childContext = context.StartChildContext();
                var stream = childContext.CreateResultStream("test", "type");
                context.MergeChildContext(childContext);
                mocks.VerifyAll();
                Assert.IsTrue(childContext.IsFinialised);
            }
        }
        [Test]
        public void MergeChildContextValidatesChildContextArgument()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            var context = new TaskContext(ioSystem, basePath);
            var error = Assert.Throws<ArgumentNullException>(() =>
            {
                context.MergeChildContext(null);
            });
            Assert.AreEqual("childContext", error.ParamName);
        }
        [Test]
        public void MergeChildContextChckesTheContextMatches()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            var context1 = new TaskContext(ioSystem, basePath);
            var context2 = new TaskContext(ioSystem, basePath);
            var childContext = context1.StartChildContext();
            var error = Assert.Throws<ArgumentException>(() =>
            {
                context2.MergeChildContext(childContext);
            });
            Assert.AreEqual("childContext", error.ParamName);
        }
        [Test]
        public void FinialiseFinialisesTheContextAndGeneratesTheIndex()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            var filePath = Path.Combine(basePath, "Test.xml");
            var indexPath = Path.Combine(basePath, "ccnet-task-index.xml");
            Expect.Call(ioSystem.FileExists(filePath)).Return(false);
            string xmlIndex;
            using (var fileStream = new MemoryStream())
            {
                using (var memoryStream = new MemoryStream())
                {
                    Expect.Call(ioSystem.OpenOutputStream(filePath)).Return(fileStream);
                    Expect.Call(ioSystem.OpenOutputStream(indexPath)).Return(memoryStream);
                    var context = new TaskContext(ioSystem, basePath);
                    this.mocks.ReplayAll();
                    var stream = context.CreateResultStream("Test", "Type");
                    context.Finialise();
                    var error = Assert.Throws<ApplicationException>(() => context.CreateResultStream(null, null));
                    Assert.AreEqual("Context has been finialised - no further actions can be performed using it", error.Message);
                    this.mocks.VerifyAll();
                    Assert.IsTrue(context.IsFinialised);
                    using (var readerStream = new MemoryStream(memoryStream.GetBuffer()))
                    {
                        using (var reader = new StreamReader(readerStream))
                        {
                            xmlIndex = reader.ReadToEnd();
                        }
                    }
                }
            }
            var expectedIndex = "<task>" +
                    "<result file=\"" + filePath + "\" name=\"Test\" type=\"Type\" />" +
                "</task>";
            var endPos = xmlIndex.IndexOf('\x0');
            if (endPos > 0)
            {
                xmlIndex = xmlIndex.Substring(0, endPos);
            }
            Assert.AreEqual(expectedIndex, xmlIndex);
        }
        [Test]
        public void RunTaskValidatesTheTask()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            var context = new TaskContext(ioSystem, basePath);
            var error = Assert.Throws<ArgumentNullException>(() =>
            {
                context.RunTask(null, null);
            });
            Assert.AreEqual("task", error.ParamName);
        }
        [Test]
        public void RunTaskRunsTheTask()
        {
            var basePath = Path.GetTempPath();
            var ioSystem = this.mocks.StrictMock<IFileSystem>();
            var context = new TaskContext(ioSystem, basePath);
            var task = mocks.StrictMock<TaskBase>();
            var result = mocks.StrictMock<IIntegrationResult>();
            Expect.Call(() => ioSystem.EnsureFolderExists(null)).IgnoreArguments();
            Expect.Call(() => task.Run(result));
            mocks.ReplayAll();
            context.RunTask(task, result);
            mocks.VerifyAll();
        }
    }
}
