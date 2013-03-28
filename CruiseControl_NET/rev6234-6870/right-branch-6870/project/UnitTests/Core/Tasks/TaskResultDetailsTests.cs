namespace ThoughtWorks.CruiseControl.UnitTests.Core.Tasks
{
    using System;
    using NUnit.Framework;
    using Rhino.Mocks;
    using ThoughtWorks.CruiseControl.Core.Tasks;
    [TestFixture]
    public class TaskResultDetailsTests
    {
        private MockRepository mocks;
        [SetUp]
        public void Setup()
        {
            this.mocks = new MockRepository();
        }
        [Test]
        public void ConstructorValidatesAllArguments()
        {
            Assert.AreEqual(
                "taskName",
                Assert.Throws<ArgumentException>(() => new TaskResultDetails(null, "type", "file")).ParamName);
            Assert.AreEqual(
                "taskName",
                Assert.Throws<ArgumentException>(() => new TaskResultDetails(string.Empty, "type", "file")).ParamName);
            Assert.AreEqual(
                "taskType",
                Assert.Throws<ArgumentException>(() => new TaskResultDetails("name", null, "file")).ParamName);
            Assert.AreEqual(
                "taskType",
                Assert.Throws<ArgumentException>(() => new TaskResultDetails("name", string.Empty, "file")).ParamName);
            Assert.AreEqual(
                "fileName",
                Assert.Throws<ArgumentException>(() => new TaskResultDetails("name", "type", null)).ParamName);
            Assert.AreEqual(
                "fileName",
                Assert.Throws<ArgumentException>(() => new TaskResultDetails("name", "type", string.Empty)).ParamName);
        }
        [Test]
        public void ConstructorSetProperties()
        {
            var taskName = "Test Task";
            var taskType = "Test Type";
            var fileName = "Name of file";
            var results = new TaskResultDetails(taskName, taskType, fileName);
            Assert.AreEqual(taskName, results.TaskName);
            Assert.AreEqual(taskType, results.TaskType);
            Assert.AreEqual(fileName, results.FileName);
        }
    }
}
