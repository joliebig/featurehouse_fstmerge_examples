namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Collections.Generic;
    using System.Text.RegularExpressions;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.Core.Config;
    [ReflectorType("cruiseServerControl")]
    public class CruiseServerControlTask
        : TaskBase, IConfigurationValidation
    {
        private List<string> cachedProjects = new List<string>();
        [ReflectorProperty("server", Required = false)]
        public string Server { get; set; }
        [ReflectorProperty("actions", Required = true)]
        public CruiseServerControlTaskAction[] Actions { get; set; }
        public ICruiseServerClientFactory ClientFactory { get; set; }
        public ILogger Logger { get; set; }
        public virtual void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            if ((this.Actions == null) || (this.Actions.Length == 0))
            {
                errorProcesser.ProcessWarning("This task will not do anything - no actions specified");
            }
        }
        protected override bool Execute(IIntegrationResult result)
        {
            var logger = this.Logger ?? new DefaultLogger();
            var factory = this.ClientFactory ?? new CruiseServerClientFactory();
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description)
                ? Description
                : "Performing server actions");
            logger.Info("Performing server actions");
            logger.Debug("Initialising client");
            var client = factory.GenerateClient(Server ?? "tcp://localhost:21234");
            this.CacheProjectNames(logger, client);
            var count = 0;
            foreach (var action in Actions ?? new CruiseServerControlTaskAction[0])
            {
                var projects = this.ListProjects(action.Project);
                logger.Info("Found " + projects.Count + " project(s) for pattern '" + action.Project + "'");
                Action<string> projectAction = RetrieveAction(logger, client, action);
                if (projectAction != null)
                {
                    foreach (var project in projects)
                    {
                        logger.Debug("Sending action to " + project);
                        count++;
                        projectAction(project);
                    }
                }
                else
                {
                    throw new CruiseControlException("Unknown action specified: " + action.Type.ToString());
                }
            }
            logger.Info("Server actions completed: " + count + " command(s) sent");
            return true;
        }
        private void CacheProjectNames(ILogger logger, CruiseServerClientBase client)
        {
            logger.Info("Retrieving projects from_ server");
            this.cachedProjects.Clear();
            var serverProjects = client.GetProjectStatus();
            foreach (var serverProject in serverProjects)
            {
                this.cachedProjects.Add(serverProject.Name);
            }
            logger.Debug(this.cachedProjects.Count + " project(s) retrieved");
        }
        private static Action<string> RetrieveAction(ILogger logger, CruiseServerClientBase client, CruiseServerControlTaskAction action)
        {
            Action<string> projectAction = null;
            switch (action.Type)
            {
                case CruiseServerControlTaskActionType.StartProject:
                    logger.Info("Performing start project action");
                    projectAction = p =>
                    {
                        client.StartProject(p);
                    };
                    break;
                case CruiseServerControlTaskActionType.StopProject:
                    logger.Info("Performing stop project action");
                    projectAction = p =>
                    {
                        client.StopProject(p);
                    };
                    break;
            }
            return projectAction;
        }
        private IList<string> ListProjects(string projectPattern)
        {
            var list = new List<string>();
            if (projectPattern.Contains("*") || projectPattern.Contains("?"))
            {
                var pattern = projectPattern
                    .Replace("*", ".*")
                    .Replace("?", ".");
                var regex = new Regex(pattern, RegexOptions.Compiled | RegexOptions.IgnoreCase);
                foreach (var project in this.cachedProjects)
                {
                    if (regex.IsMatch(project))
                    {
                        list.Add(project);
                    }
                }
            }
            else
            {
                list.Add(projectPattern);
            }
            return list;
        }
    }
}
