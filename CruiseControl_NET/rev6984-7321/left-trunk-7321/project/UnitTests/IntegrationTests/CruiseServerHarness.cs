namespace ThoughtWorks.CruiseControl.UnitTests.IntegrationTests
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Linq;
    using System.Threading;
    using System.Xml;
    using ThoughtWorks.CruiseControl.Core;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.Remote.Events;
    using ThoughtWorks.CruiseControl.Remote.Messages;
    public class CruiseServerHarness
        : IDisposable
    {
        private readonly string configFile;
        private readonly List<string> projects;
        private readonly string[] stateFiles;
        private readonly string[] buildFolders;
        private readonly CruiseServerFactory factory;
        private readonly ICruiseServer server;
        private readonly ManualResetEvent[] completionEvents;
        public CruiseServerHarness(XmlDocument configuration, params string[] projects)
        {
            this.TimeoutLength = new TimeSpan(0, 5, 0);
            var workingFolder = Environment.CurrentDirectory;
            this.configFile = Path.Combine(workingFolder, "ScenarioTests.xml");
            configuration.Save(configFile);
            this.projects = new List<string>(projects);
            this.stateFiles = projects.Select(p => Path.Combine(workingFolder, p + ".state")).ToArray();
            this.buildFolders = projects.Select(p => Path.Combine(workingFolder, Path.Combine("ScenarioTests", p))).ToArray();
            this.factory = new CruiseServerFactory();
            this.server = this.factory.Create(true, this.configFile);
            this.completionEvents = new ManualResetEvent[projects.Length];
            for (var loop = 0; loop < this.completionEvents.Length; loop++)
            {
                this.completionEvents[loop] = new ManualResetEvent(false);
            }
            this.server.IntegrationCompleted += (o, e) =>
            {
                if (this.IntegrationCompleted != null)
                {
                    this.IntegrationCompleted(o, e);
                }
                this.FindCompletionEvent(e.ProjectName).Set();
            };
        }
        public ICruiseServer Server
        {
            get { return this.server; }
        }
        public ManualResetEvent[] CompletionEvents
        {
            get { return this.completionEvents; }
        }
        public TimeSpan TimeoutLength { get; set; }
        public ProjectStatus TriggerBuildAndWait(string projectName, params NameValuePair[] parameters)
        {
            var completionEvent = this.FindCompletionEvent(projectName);
            var request = new BuildIntegrationRequest()
            {
                ProjectName = projectName,
                BuildValues = new List<NameValuePair>(parameters)
            };
            ValidateResponse(this.server.ForceBuild(request));
            if (!completionEvent.WaitOne(this.TimeoutLength, false))
            {
                ValidateResponse(this.server.AbortBuild(request));
                throw new HarnessException("Build did not complete within the time-out period");
            }
            var status = ValidateResponse(this.server.GetProjectStatus(request));
            return status.Projects.Single(p => p.Name == projectName);
        }
        public ManualResetEvent FindCompletionEvent(string projectName)
        {
            var projectIndex = this.projects.IndexOf(projectName);
            return this.completionEvents[projectIndex];
        }
        public void Dispose()
        {
            foreach (var state in this.stateFiles.Where(s => File.Exists(s)))
            {
                File.Delete(state);
            }
            foreach (var build in this.buildFolders.Where(f => Directory.Exists(f)))
            {
                Directory.Delete(build, true);
            }
            if (File.Exists(this.configFile))
            {
                File.Delete(this.configFile);
            }
            this.server.Stop();
            this.server.WaitForExit();
            this.server.Dispose();
        }
        public event EventHandler<IntegrationCompletedEventArgs> IntegrationCompleted;
        private static TResponse ValidateResponse<TResponse>(TResponse response)
            where TResponse : Response
        {
            if (response.Result != ResponseResult.Success)
            {
                throw new HarnessException("Unable to trigger build");
            }
            return response;
        }
    }
}
