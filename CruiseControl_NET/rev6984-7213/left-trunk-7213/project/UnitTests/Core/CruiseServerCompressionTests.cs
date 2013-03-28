namespace ThoughtWorks.CruiseControl.UnitTests.Core
{
    using System;
    using System.Collections.Generic;
    using System.Text;
    using NUnit.Framework;
    using Rhino.Mocks;
    using ThoughtWorks.CruiseControl.Core;
    using ThoughtWorks.CruiseControl.Remote.Messages;
    using ThoughtWorks.CruiseControl.Core.Config;
    using System.Diagnostics;
    using ThoughtWorks.CruiseControl.Core.Queues;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Core.Security;
    [TestFixture]
    public class CruiseServerCompressionTests
    {
        private const string testProjectName = "Test Project";
        private const string testBuildName = "Test Build";
        [TearDown]
        public void Teardown()
        {
            IntegrationQueueManagerFactory.ResetFactory();
        }
        [Test(Description = "GetLog() should compress the log data.")]
        public void GetLogCompressesData()
        {
            var mocks = new MockRepository();
            var data = "This is some test - line 1, line 2, line 3 - this is some test data";
            var server = InitialiseServer(mocks, testBuildName, data);
            var request = new BuildRequest(null, testProjectName);
            request.BuildName = testBuildName;
            request.CompressData = true;
            var response = server.GetLog(request);
            mocks.VerifyAll();
            Assert.AreEqual(ResponseResult.Success, response.Result);
            Assert.AreNotEqual(data, response.Data);
        }
        private static CruiseServer InitialiseServer(MockRepository mocks, string buildName, string buildLog)
        {
            var projects = new ProjectList();
            var project = new Project()
            {
                Name = testProjectName
            };
            projects.Add(project);
            var configuration = mocks.StrictMock<IConfiguration>();
            SetupResult.For(configuration.Projects)
                .Return(projects);
            SetupResult.For(configuration.SecurityManager)
                .Return(new NullSecurityManager());
            var configService = mocks.StrictMock<IConfigurationService>();
            SetupResult.For(configService.Load())
                .Return(configuration);
            Expect.Call(() => { configService.AddConfigurationUpdateHandler(null); })
                .IgnoreArguments();
            var repository = mocks.StrictMock<IIntegrationRepository>();
            SetupResult.For(repository.GetBuildLog(buildName))
                .Return(buildLog);
            var projectIntegrator = mocks.StrictMock<IProjectIntegrator>();
            SetupResult.For(projectIntegrator.Project)
                .Return(project);
            SetupResult.For(projectIntegrator.IntegrationRepository)
                .Return(repository);
            var queueManager = mocks.StrictMock<IQueueManager>();
            Expect.Call(() => { queueManager.AssociateIntegrationEvents(null, null); })
                .IgnoreArguments();
            SetupResult.For(queueManager.GetIntegrator(testProjectName))
                .Return(projectIntegrator);
            var queueManagerFactory = mocks.StrictMock<IQueueManagerFactory>();
            SetupResult.For(queueManagerFactory.Create(null, configuration, null))
                .Return(queueManager);
            IntegrationQueueManagerFactory.OverrideFactory(queueManagerFactory);
            var execEnviron = mocks.StrictMock<IExecutionEnvironment>();
            SetupResult.For(execEnviron.GetDefaultProgramDataFolder(ApplicationType.Server))
                .Return(string.Empty);
            mocks.ReplayAll();
            var server = new CruiseServer(
                configService,
                null,
                null,
                null,
                null,
                execEnviron,
                null);
            return server;
        }
    }
}
