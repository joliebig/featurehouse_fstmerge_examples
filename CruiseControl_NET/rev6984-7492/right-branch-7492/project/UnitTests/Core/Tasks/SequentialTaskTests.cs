using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Core.Tasks;
using Rhino.Mocks;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.UnitTests.Core.Tasks
{
    [TestFixture]
    public class SequentialTaskTests
    {
        private MockRepository mocks = new MockRepository();
        [Test]
        public void ExecuteRunsMultipleSuccessfulTasks()
        {
            var subTasks = new List<SequentialTestTask>();
            for (var loop = 1; loop <= 5; loop++)
            {
                subTasks.Add(new SequentialTestTask() { TaskNumber = loop, Result = IntegrationStatus.Success });
            }
            var task = new SequentialTask()
            {
                Tasks = subTasks.ToArray()
            };
            var logger = mocks.DynamicMock<ILogger>();
            var result = GenerateResultMock(5);
            mocks.ReplayAll();
            task.Run(result);
            mocks.VerifyAll();
            Assert.AreEqual(IntegrationStatus.Success, result.Status, "Status does not match");
        }
        [Test]
        public void ExecuteStopsOnFirstFailure()
        {
            var subTasks = new List<SequentialTestTask>();
            for (var loop = 1; loop <= 5; loop++)
            {
                subTasks.Add(new SequentialTestTask() { TaskNumber = loop, Result = loop > 3 ? IntegrationStatus.Failure : IntegrationStatus.Success });
            }
            var task = new SequentialTask()
            {
                Tasks = subTasks.ToArray()
            };
            var logger = mocks.DynamicMock<ILogger>();
            var result = GenerateResultMock(4);
            mocks.ReplayAll();
            task.Run(result);
            mocks.VerifyAll();
            Assert.AreEqual(IntegrationStatus.Failure, result.Status, "Status does not match");
        }
        [Test]
        public void ExecuteIgnoresFailures()
        {
            var subTasks = new List<SequentialTestTask>();
            for (var loop = 1; loop <= 5; loop++)
            {
                subTasks.Add(new SequentialTestTask() { TaskNumber = loop, Result = loop > 3 ? IntegrationStatus.Failure : IntegrationStatus.Success });
            }
            var task = new SequentialTask()
            {
                Tasks = subTasks.ToArray(),
                ContinueOnFailure = true
            };
            var logger = mocks.DynamicMock<ILogger>();
            var result = GenerateResultMock(5);
            mocks.ReplayAll();
            task.Run(result);
            mocks.VerifyAll();
            Assert.AreEqual(IntegrationStatus.Failure, result.Status, "Status does not match");
        }
        [Test]
        public void ExecuteHandlesAnExceptionInATask()
        {
            var task = new SequentialTask()
            {
                Tasks = new ITask[]
                {
                    new ExceptionTestTask()
                }
            };
            var logger = mocks.DynamicMock<ILogger>();
            var result = GenerateResultMock(0);
            Expect.Call(result.ExceptionResult).PropertyBehavior();
            mocks.ReplayAll();
            task.Run(result);
            mocks.VerifyAll();
            Assert.AreEqual(IntegrationStatus.Failure, result.Status, "Status does not match");
        }
        private IIntegrationResult GenerateResultMock(int runCount)
        {
            var buildInfo = mocks.DynamicMock<BuildProgressInformation>(string.Empty, string.Empty);
            var result = mocks.StrictMock<IIntegrationResult>();
            SetupResult.For(result.BuildProgressInformation).Return(buildInfo);
            for (var loop = 1; loop <= runCount; loop++)
            {
                Expect.Call(() => { result.AddTaskResult(string.Format("Task #{0} has run", loop)); });
            }
            Expect.Call(result.Status).PropertyBehavior();
            Expect.Call(result.Clone()).Return(result).Repeat.Times(runCount == 0 ? 1 : runCount);
            if (runCount > 0) Expect.Call(() => { result.Merge(result); }).Repeat.Times(runCount);
            return result;
        }
        private class SequentialTestTask
            : ITask
        {
            public IntegrationStatus Result { get; set; }
            public int TaskNumber { get; set; }
            public void Run(IIntegrationResult result)
            {
                result.AddTaskResult(string.Format("Task #{0} has run", TaskNumber));
                result.Status = Result;
            }
        }
        private class ExceptionTestTask
            : ITask
        {
            public void Run(IIntegrationResult result)
            {
                throw new Exception();
            }
        }
    }
}
