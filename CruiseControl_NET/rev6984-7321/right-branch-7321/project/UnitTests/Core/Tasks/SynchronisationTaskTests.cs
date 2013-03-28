namespace ThoughtWorks.CruiseControl.UnitTests.Core.Tasks
{
    using System;
    using System.Collections.Generic;
    using System.Text;
    using Rhino.Mocks;
    using NUnit.Framework;
    using ThoughtWorks.CruiseControl.Core.Tasks;
    using ThoughtWorks.CruiseControl.Core;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.Core.Util;
    using System.Threading;
    [TestFixture]
    public class SynchronisationTaskTests
    {
        private MockRepository mocks;
        [SetUp]
        public void Setup()
        {
            this.mocks = new MockRepository();
        }
        [Test]
        public void RunRunsTasksInSequence()
        {
            var buildInfo = mocks.DynamicMock<BuildProgressInformation>(string.Empty, string.Empty);
            var result = mocks.Stub<IIntegrationResult>();
            result.Status = IntegrationStatus.Success;
            SetupResult.For(result.Clone()).Return(result);
            SetupResult.For(result.BuildProgressInformation).Return(buildInfo);
            var logger = mocks.DynamicMock<ILogger>();
            var childTask1 = new SleepingTask() { SleepPeriod = 10, Result = IntegrationStatus.Success };
            var childTask2 = new SleepingTask() { SleepPeriod = 10, Result = IntegrationStatus.Success };
            var task = new SynchronisationTask()
            {
                Logger = logger,
                Tasks = new ITask[] {
                    childTask1,
                    childTask2
                }
            };
            this.mocks.ReplayAll();
            task.Run(result);
            this.mocks.VerifyAll();
            Assert.AreEqual(IntegrationStatus.Success, result.Status);
        }
        [Test]
        public void RunFailsIfTaskFails()
        {
            var buildInfo = mocks.DynamicMock<BuildProgressInformation>(string.Empty, string.Empty);
            var result = mocks.Stub<IIntegrationResult>();
            result.Status = IntegrationStatus.Success;
            SetupResult.For(result.Clone()).Return(result);
            SetupResult.For(result.BuildProgressInformation).Return(buildInfo);
            var logger = mocks.DynamicMock<ILogger>();
            var childTask1 = new SleepingTask() { SleepPeriod = 10, Result = IntegrationStatus.Success };
            var childTask2 = new SleepingTask() { SleepPeriod = 10, Result = IntegrationStatus.Failure };
            var task = new SynchronisationTask()
            {
                Logger = logger,
                Tasks = new ITask[] {
                    childTask1,
                    childTask2
                }
            };
            this.mocks.ReplayAll();
            task.Run(result);
            this.mocks.VerifyAll();
            Assert.AreEqual(IntegrationStatus.Failure, result.Status);
        }
        [Test]
        public void RunFailsIfTaskErrors()
        {
            var buildInfo = mocks.DynamicMock<BuildProgressInformation>(string.Empty, string.Empty);
            var result = mocks.Stub<IIntegrationResult>();
            result.Status = IntegrationStatus.Success;
            SetupResult.For(result.Clone()).Return(result);
            SetupResult.For(result.BuildProgressInformation).Return(buildInfo);
            var logger = mocks.DynamicMock<ILogger>();
            var childTask1 = new SleepingTask() { SleepPeriod = 10, Result = IntegrationStatus.Success };
            var childTask2 = new FailingTask();
            var task = new SynchronisationTask()
            {
                Logger = logger,
                Tasks = new ITask[] {
                    childTask1,
                    childTask2
                }
            };
            this.mocks.ReplayAll();
            task.Run(result);
            this.mocks.VerifyAll();
            Assert.AreEqual(IntegrationStatus.Failure, result.Status);
            Assert.IsNotNull(result.ExceptionResult);
            Assert.AreEqual("Task failed!", result.ExceptionResult.Message);
        }
        [Test]
        public void RunWorksWithCustomContextName()
        {
            var buildInfo = mocks.DynamicMock<BuildProgressInformation>(string.Empty, string.Empty);
            var result = mocks.Stub<IIntegrationResult>();
            result.Status = IntegrationStatus.Success;
            SetupResult.For(result.Clone()).Return(result);
            SetupResult.For(result.BuildProgressInformation).Return(buildInfo);
            var logger = mocks.DynamicMock<ILogger>();
            var childTask1 = new SleepingTask() { SleepPeriod = 10, Result = IntegrationStatus.Success };
            var childTask2 = new SleepingTask() { SleepPeriod = 10, Result = IntegrationStatus.Success };
            var task = new SynchronisationTask()
            {
                Logger = logger,
                Tasks = new ITask[] {
                    childTask1,
                    childTask2
                },
                ContextName = "customContext"
            };
            this.mocks.ReplayAll();
            task.Run(result);
            this.mocks.VerifyAll();
            Assert.AreEqual(IntegrationStatus.Success, result.Status);
        }
        [Test]
        public void RunAllowsTasksInSeparateContexts()
        {
            var logger = mocks.DynamicMock<ILogger>();
            var buildInfo1 = mocks.DynamicMock<BuildProgressInformation>(string.Empty, string.Empty);
            var result1 = mocks.Stub<IIntegrationResult>();
            result1.Status = IntegrationStatus.Success;
            SetupResult.For(result1.Clone()).Return(result1);
            SetupResult.For(result1.BuildProgressInformation).Return(buildInfo1);
            var childTask11 = new SleepingTask() { SleepPeriod = 1000, Result = IntegrationStatus.Success };
            var childTask12 = new SleepingTask() { SleepPeriod = 1000, Result = IntegrationStatus.Success };
            var task1 = new SynchronisationTask()
            {
                Logger = logger,
                Tasks = new ITask[] {
                    childTask11,
                    childTask12
                },
                ContextName = "customContext1"
            };
            var buildInfo2 = mocks.DynamicMock<BuildProgressInformation>(string.Empty, string.Empty);
            var result2 = mocks.Stub<IIntegrationResult>();
            result2.Status = IntegrationStatus.Success;
            SetupResult.For(result2.Clone()).Return(result2);
            SetupResult.For(result2.BuildProgressInformation).Return(buildInfo2);
            var childTask21 = new SleepingTask() { SleepPeriod = 1000, Result = IntegrationStatus.Success };
            var childTask22 = new SleepingTask() { SleepPeriod = 1000, Result = IntegrationStatus.Success };
            var task2 = new SynchronisationTask()
            {
                Logger = logger,
                Tasks = new ITask[] {
                    childTask21,
                    childTask22
                },
                ContextName = "customContext2"
            };
            var event1 = new ManualResetEvent(false);
            var event2 = new ManualResetEvent(false);
            this.mocks.ReplayAll();
            Assert.IsTrue(ThreadPool.QueueUserWorkItem(t1 =>
            {
                task1.Run(result1);
                event1.Set();
            }));
            Assert.IsTrue(ThreadPool.QueueUserWorkItem(t2 =>
            {
                task2.Run(result2);
                event2.Set();
            }));
            ManualResetEvent.WaitAll(new WaitHandle[] {
                event1,
                event2
            });
            this.mocks.VerifyAll();
            Assert.AreEqual(IntegrationStatus.Success, result1.Status);
            Assert.AreEqual(IntegrationStatus.Success, result2.Status);
        }
        [Test]
        public void RunTimesOutIfContextNotAvailable()
        {
            var logger = mocks.DynamicMock<ILogger>();
            var buildInfo1 = mocks.DynamicMock<BuildProgressInformation>(string.Empty, string.Empty);
            var result1 = mocks.Stub<IIntegrationResult>();
            result1.Status = IntegrationStatus.Success;
            SetupResult.For(result1.Clone()).Return(result1);
            SetupResult.For(result1.BuildProgressInformation).Return(buildInfo1);
            var childTask11 = new SleepingTask() { SleepPeriod = 2000, Result = IntegrationStatus.Success };
            var childTask12 = new SleepingTask() { SleepPeriod = 2000, Result = IntegrationStatus.Success };
            var task1 = new SynchronisationTask()
            {
                Logger = logger,
                Tasks = new ITask[] {
                    childTask11,
                    childTask12
                },
                TimeoutPeriod = 1
            };
            var buildInfo2 = mocks.DynamicMock<BuildProgressInformation>(string.Empty, string.Empty);
            var result2 = mocks.Stub<IIntegrationResult>();
            result2.Status = IntegrationStatus.Success;
            SetupResult.For(result2.Clone()).Return(result2);
            SetupResult.For(result2.BuildProgressInformation).Return(buildInfo2);
            var childTask21 = new SleepingTask() { SleepPeriod = 2000, Result = IntegrationStatus.Success };
            var childTask22 = new SleepingTask() { SleepPeriod = 2000, Result = IntegrationStatus.Success };
            var task2 = new SynchronisationTask()
            {
                Logger = logger,
                Tasks = new ITask[] {
                    childTask21,
                    childTask22
                },
                TimeoutPeriod = 1
            };
            var event1 = new ManualResetEvent(false);
            var event2 = new ManualResetEvent(false);
            this.mocks.ReplayAll();
            Assert.IsTrue(ThreadPool.QueueUserWorkItem(t1 =>
            {
                task1.Run(result1);
                event1.Set();
            }));
            Assert.IsTrue(ThreadPool.QueueUserWorkItem(t2 =>
            {
                task2.Run(result2);
                event2.Set();
            }));
            ManualResetEvent.WaitAll(new WaitHandle[] {
                event1,
                event2
            });
            this.mocks.VerifyAll();
            if (result2.Status == IntegrationStatus.Success)
            {
                Assert.AreEqual(IntegrationStatus.Failure, result1.Status);
            }
            else
            {
                Assert.AreEqual(IntegrationStatus.Failure, result2.Status);
            }
        }
        private class SleepingTask
            : ITask
        {
            public int? SleepPeriod { get;set;}
            public IntegrationStatus Result { get; set; }
            public void Run(IIntegrationResult result)
            {
                Thread.Sleep(SleepPeriod ?? 1);
                result.Status = Result;
            }
        }
        private class FailingTask
            : ITask
        {
            public void Run(IIntegrationResult result)
            {
                throw new Exception("Task failed!");
            }
        }
    }
}
