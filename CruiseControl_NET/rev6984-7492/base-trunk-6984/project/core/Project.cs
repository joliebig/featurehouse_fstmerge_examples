using System;
using System.Collections;
using System.ComponentModel;
using System.IO;
using System.Xml;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Core.Label;
using ThoughtWorks.CruiseControl.Core.Publishers;
using ThoughtWorks.CruiseControl.Core.Publishers.Statistics;
using ThoughtWorks.CruiseControl.Core.Security;
using ThoughtWorks.CruiseControl.Core.Sourcecontrol;
using ThoughtWorks.CruiseControl.Core.State;
using ThoughtWorks.CruiseControl.Core.Tasks;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Parameters;
using System.Text;
using System.Runtime;
using ThoughtWorks.CruiseControl.Core.Triggers;
namespace ThoughtWorks.CruiseControl.Core
{
    [ReflectorType("project")]
    public class Project : ProjectBase, IProject, IIntegrationRunnerTarget, IIntegrationRepository,
        IConfigurationValidation, IStatusSnapshotGenerator, IParamatisedProject
    {
        private string webUrl = DefaultUrl();
        private string queueName = string.Empty;
        private int queuePriority = 0;
        private ISourceControl sourceControl = new NullSourceControl();
        private ILabeller labeller = new DefaultLabeller();
        private ITask[] tasks = new ITask[] { new NullTask() };
        private ITask[] publishers = new ITask[] { new XmlLogPublisher() };
        private ProjectActivity currentActivity = ProjectActivity.Sleeping;
        private IStateManager state = new FileStateManager();
        private IIntegrationResultManager integrationResultManager;
        private IIntegratable integratable;
        private QuietPeriod quietPeriod = new QuietPeriod(new DateTimeProvider());
        private ArrayList messages = new ArrayList();
        private int maxSourceControlRetries = 5;
        private IProjectAuthorisation security = new InheritedProjectAuthorisation();
        private ParameterBase[] parameters = new ParameterBase[0];
        private ProjectInitialState initialState = ProjectInitialState.Started;
        private ProjectStartupMode startupMode = ProjectStartupMode.UseLastState;
        private bool StopProjectOnReachingMaxSourceControlRetries = false;
        private Sourcecontrol.Common.SourceControlErrorHandlingPolicy sourceControlErrorHandling = Common.SourceControlErrorHandlingPolicy.ReportEveryFailure;
        private ProjectStatusSnapshot currentProjectStatus;
        private Dictionary<ITask, ItemStatus> currentProjectItems = new Dictionary<ITask, ItemStatus>();
        private Dictionary<SourceControlOperation, ItemStatus> sourceControlOperations = new Dictionary<SourceControlOperation, ItemStatus>();
        [ReflectorProperty("prebuild", Required = false)]
        public ITask[] PrebuildTasks = new ITask[0];
        public Project()
        {
            integrationResultManager = new IntegrationResultManager(this);
            integratable = new IntegrationRunner(integrationResultManager, this, quietPeriod);
            currentProjectStatus = new ProjectStatusSnapshot();
            PropertyChanged += delegate(object sender, PropertyChangedEventArgs e)
            {
                switch (e.PropertyName)
                {
                    case "Name":
                        lock (currentProjectStatus)
                        {
                            currentProjectStatus.Name = Name;
                        }
                        break;
                    case "Description":
                        lock (currentProjectStatus)
                        {
                            currentProjectStatus.Description = Description;
                        }
                        break;
                }
            };
        }
        public Project(IIntegratable integratable)
            : this()
        {
            this.integratable = integratable;
        }
        [ReflectorProperty("security", InstanceTypeKey = "type", Required = false)]
        public IProjectAuthorisation Security
        {
            get { return security; }
            set { security = value; }
        }
        [ReflectorProperty("parameters", Required = false)]
        public ParameterBase[] Parameters
        {
            get { return parameters; }
            set { parameters = value; }
        }
        [ReflectorProperty("linkedSites", Required = false)]
        public NameValuePair[] LinkedSites { get; set; }
        [ReflectorProperty("state", InstanceTypeKey = "type", Required = false), Description("State")]
        public IStateManager StateManager
        {
            get { return state; }
            set { state = value; }
        }
        [ReflectorProperty("webURL", Required = false)]
        public string WebURL
        {
            get { return webUrl; }
            set { webUrl = value; }
        }
        [ReflectorProperty("impersonation", InstanceType = typeof(ImpersonationDetails), Required = false)]
        public ImpersonationDetails Impersonation { get; set; }
        [ReflectorProperty("maxSourceControlRetries", Required = false)]
        public int MaxSourceControlRetries
        {
            get { return maxSourceControlRetries; }
            set { maxSourceControlRetries = value < 0 ? 0 : value; }
        }
        [ReflectorProperty("stopProjectOnReachingMaxSourceControlRetries", Required = false)]
        public bool stopProjectOnReachingMaxSourceControlRetries
        {
            get { return StopProjectOnReachingMaxSourceControlRetries; }
            set { StopProjectOnReachingMaxSourceControlRetries = value; }
        }
        [ReflectorProperty("sourceControlErrorHandling", Required = false)]
        public Common.SourceControlErrorHandlingPolicy SourceControlErrorHandling
        {
            get { return sourceControlErrorHandling; }
            set { sourceControlErrorHandling = value; }
        }
        [ReflectorProperty("queue", Required = false)]
        public string QueueName
        {
            get
            {
                if (string.IsNullOrEmpty(queueName)) return Name;
                return queueName;
            }
            set { queueName = value.Trim(); }
        }
        [ReflectorProperty("queuePriority", Required = false)]
        public int QueuePriority
        {
            get { return queuePriority; }
            set { queuePriority = value; }
        }
        [ReflectorProperty("sourcecontrol", InstanceTypeKey = "type", Required = false)]
        public ISourceControl SourceControl
        {
            get { return sourceControl; }
            set { sourceControl = value; }
        }
        [ReflectorArray("publishers", Required = false)]
        public ITask[] Publishers
        {
            get { return publishers; }
            set { publishers = value; }
        }
        [ReflectorProperty("modificationDelaySeconds", Required = false)]
        public double ModificationDelaySeconds
        {
            get { return quietPeriod.ModificationDelaySeconds; }
            set { quietPeriod.ModificationDelaySeconds = value; }
        }
        [ReflectorProperty("labeller", InstanceTypeKey = "type", Required = false)]
        public ILabeller Labeller
        {
            get { return labeller; }
            set { labeller = value; }
        }
        [ReflectorArray("tasks", Required = false)]
        public ITask[] Tasks
        {
            get { return tasks; }
            set { tasks = value; }
        }
        public ProjectActivity Activity
        {
            get { return currentActivity; }
            set { currentActivity = value; }
        }
        public ProjectActivity CurrentActivity
        {
            get { return currentActivity; }
        }
        public IIntegrationResult CurrentResult
        {
            get { return integrationResultManager.CurrentIntegration; }
        }
        public IIntegrationResult Integrate(IntegrationRequest request)
        {
            Log.Trace("Integrating {0}", Name);
            lock (currentProjectStatus)
            {
                currentProjectItems.Clear();
                sourceControlOperations.Clear();
                currentProjectStatus.Status = ItemBuildStatus.Running;
                currentProjectStatus.ChildItems.Clear();
                currentProjectStatus.TimeCompleted = null;
                currentProjectStatus.TimeOfEstimatedCompletion = null;
                currentProjectStatus.TimeStarted = DateTime.Now;
                GenerateSourceControlOperation(SourceControlOperation.CheckForModifications);
                GenerateTaskStatuses("Pre-build tasks", PrebuildTasks);
                GenerateSourceControlOperation(SourceControlOperation.GetSource);
                GenerateTaskStatuses("Build tasks", Tasks);
                GenerateTaskStatuses("Publisher tasks", Publishers);
            }
            IIntegrationResult result = null;
            IDisposable impersonation = null;
            var hasError = false;
            try
            {
                if (Impersonation != null) impersonation = Impersonation.Impersonate();
                var dynamicSourceControl = sourceControl as IParamatisedItem;
                if (dynamicSourceControl != null)
                {
                    dynamicSourceControl.ApplyParameters(request.BuildValues, parameters);
                }
                result = integratable.Integrate(request);
            }
            catch (Exception error)
            {
                Log.Error(error);
                hasError = true;
                throw;
            }
            finally
            {
                if (impersonation != null) impersonation.Dispose();
                lock (currentProjectStatus)
                {
                    CancelAllOutstandingItems(currentProjectStatus);
                    currentProjectStatus.TimeCompleted = DateTime.Now;
                    IntegrationStatus resultStatus = result == null ?
                        (hasError ? IntegrationStatus.Exception : IntegrationStatus.Unknown) :
                        result.Status;
                    switch (resultStatus)
                    {
                        case IntegrationStatus.Success:
                            currentProjectStatus.Status = ItemBuildStatus.CompletedSuccess;
                            break;
                        case IntegrationStatus.Unknown:
                            currentProjectStatus.Status = ItemBuildStatus.Cancelled;
                            break;
                        default:
                            currentProjectStatus.Status = ItemBuildStatus.CompletedFailed;
                            break;
                    }
                }
            }
            return result;
        }
        private void ClearMessages(Message.MessageKind kind)
        {
            for (Int32 i = messages.Count - 1; i >= 0; i--)
            {
                Message m = (Message)messages[i];
                if (m.Kind == kind)
                {
                    messages.RemoveAt(i);
                }
            }
        }
        private void CancelAllOutstandingItems(ItemStatus value)
        {
            if ((value.Status == ItemBuildStatus.Running) ||
                (value.Status == ItemBuildStatus.Pending))
            {
                ItemBuildStatus status = ItemBuildStatus.Cancelled;
                foreach (ItemStatus item in value.ChildItems)
                {
                    CancelAllOutstandingItems(item);
                    if (item.Status == ItemBuildStatus.CompletedFailed)
                    {
                        status = ItemBuildStatus.CompletedFailed;
                    }
                    else if ((item.Status == ItemBuildStatus.CompletedSuccess) &&
                        (status == ItemBuildStatus.Cancelled))
                    {
                        status = ItemBuildStatus.CompletedSuccess;
                    }
                }
                value.Status = status;
            }
        }
        private void GenerateSourceControlOperation(SourceControlOperation operation)
        {
            ItemStatus sourceControlStatus = null;
            if (SourceControl is IStatusSnapshotGenerator)
            {
                sourceControlStatus = (SourceControl as IStatusSnapshotGenerator).GenerateSnapshot();
            }
            else
            {
                sourceControlStatus = new ItemStatus(
                    string.Format("{0}: {1}",
                        SourceControl.GetType().Name,
                        operation));
                sourceControlStatus.Status = ItemBuildStatus.Pending;
            }
            if (sourceControlStatus != null)
            {
                currentProjectStatus.AddChild(sourceControlStatus);
                sourceControlOperations.Add(operation, sourceControlStatus);
            }
        }
        private void GenerateTaskStatuses(string name, IList tasks)
        {
            ItemStatus groupItem = new ItemStatus(name);
            groupItem.Status = ItemBuildStatus.Pending;
            foreach (ITask task in tasks)
            {
                ItemStatus taskItem = null;
                if (task is TaskBase)
                {
                    (task as TaskBase).InitialiseStatus();
                }
                if (task is IStatusSnapshotGenerator)
                {
                    taskItem = (task as IStatusSnapshotGenerator).GenerateSnapshot();
                }
                else
                {
                    taskItem = new ItemStatus(task.GetType().Name);
                    taskItem.Status = ItemBuildStatus.Pending;
                }
                if (taskItem != null)
                {
                    groupItem.AddChild(taskItem);
                    currentProjectItems.Add(task, taskItem);
                }
            }
            if (groupItem.ChildItems.Count > 0) currentProjectStatus.AddChild(groupItem);
        }
        public void NotifyPendingState()
        {
            currentActivity = ProjectActivity.Pending;
        }
        public void NotifySleepingState()
        {
            currentActivity = ProjectActivity.Sleeping;
        }
        public void Prebuild(IIntegrationResult result)
        {
            var parameters = new Dictionary<string, string>();
            if (result.Parameters != null) parameters = NameValuePair.ToDictionary(result.Parameters);
            Prebuild(result, parameters);
        }
        public void Prebuild(IIntegrationResult result, Dictionary<string, string> parameterValues)
  {
            RunTasks(result, PrebuildTasks, parameterValues);
        }
        public virtual void ValidateParameters(Dictionary<string, string> parameterValues)
        {
            Log.Debug("Validating parameters");
            if (parameters != null)
            {
                List<Exception> results = new List<Exception>();
                foreach (ParameterBase parameter in parameters)
                {
                    string value = null;
                    if (parameterValues.ContainsKey(parameter.Name)) value = parameterValues[parameter.Name];
                    results.AddRange(parameter.Validate(value));
                }
                if (results.Count > 0)
                {
                    var error = new StringBuilder();
                    error.Append("The following errors were found in the parameters:");
                    foreach (Exception err in results)
                    {
                        error.Append(Environment.NewLine + err.Message);
                    }
                    Exception exception = new Exception(error.ToString());
                    Log.Warning(exception);
                    throw exception;
                }
            }
        }
        public void Run(IIntegrationResult result)
        {
            var parameters = new Dictionary<string, string>();
            if (result.Parameters != null) parameters = NameValuePair.ToDictionary(result.Parameters);
            Run(result, parameters);
        }
        public void Run(IIntegrationResult result, Dictionary<string, string> parameterValues)
  {
            RunTasks(result, tasks, parameterValues);
        }
        private void RunTasks(IIntegrationResult result, IList tasksToRun, Dictionary<string, string> parameterValues)
        {
            foreach (ITask task in tasksToRun)
            {
                if (task is IParamatisedItem)
                {
                    (task as IParamatisedItem).ApplyParameters(parameterValues, parameters);
                }
                RunTask(task, result,false);
                if (result.Failed) break;
            }
            CancelTasks(tasksToRun);
        }
        public void AbortRunningBuild()
        {
            ProcessExecutor.KillProcessCurrentlyRunningForProject(Name);
        }
        public void PublishResults(IIntegrationResult result)
        {
            var parameters = new Dictionary<string, string>();
            if (result.Parameters != null) parameters = NameValuePair.ToDictionary(result.Parameters);
            PublishResults(result, parameters);
        }
  public void PublishResults(IIntegrationResult result, Dictionary<string, string> parameterValues)
  {
            CancelTasks(PrebuildTasks);
            CancelTasks(Tasks);
            var merged = false;
            var mergeFailed = false;
            foreach (ITask publisher in publishers)
            {
                var isMergeTask = publisher is IMergeTask;
                try
                {
                    merged |= isMergeTask;
                    if (publisher is IParamatisedItem)
                    {
                        (publisher as IParamatisedItem).ApplyParameters(parameterValues, parameters);
                    }
                    RunTask(publisher, result, true);
                }
                catch (Exception e)
                {
                    mergeFailed |= isMergeTask;
                    Log.Error("Publisher threw exception: " + e);
                }
            }
            if (result.Succeeded)
            {
                messages.Clear();
            }
            else
            {
                AddBreakersToMessages(result);
                AddFailedTaskToMessages();
            }
            this.ClearMessages(Message.MessageKind.BuildStatus);
            if (merged && !mergeFailed)
            {
                foreach (var taskResult in result.TaskResults)
                {
                    var temporaryResult = taskResult as ITemporaryResult;
                    if (temporaryResult != null)
                    {
                        temporaryResult.CleanUp();
                    }
                }
            }
            else
            {
                var hasTemporaryFiles = false;
                foreach (var taskResult in result.TaskResults)
                {
                    var temporaryResult = taskResult as ITemporaryResult;
                    if (temporaryResult != null)
                    {
                        hasTemporaryFiles = true;
                        break;
                    }
                }
                if (hasTemporaryFiles)
                {
                    if (mergeFailed)
                    {
                        Log.Warning("One or more merge publishers failed - temporary files have not been deleted!");
                    }
                    else
                    {
                        Log.Warning("There are no merge publishers - temporary files have not been deleted!");
                    }
                }
            }
        }
        private void RunTask(ITask task, IIntegrationResult result, bool isPublisher)
        {
            ItemStatus status = null;
            if (currentProjectItems.ContainsKey(task)) status = currentProjectItems[task];
            var baseTask = task as TaskBase;
            if (status != null)
            {
                if (baseTask != null)
                {
                    status.TimeOfEstimatedCompletion = baseTask.CalculateEstimatedTime();
                    isPublisher = false;
                }
                status.TimeStarted = DateTime.Now;
                status.Status = ItemBuildStatus.Running;
                if ((status.Parent != null) && (status.Parent.Status == ItemBuildStatus.Pending))
                {
                    status.Parent.TimeStarted = status.TimeStarted;
                    status.Parent.Status = ItemBuildStatus.Running;
                }
            }
            try
            {
                task.Run(result);
                if (status != null && !isPublisher)
                {
                    var wasSuccessful = !result.Failed;
                    if (baseTask != null)
                    {
                        wasSuccessful = baseTask.WasSuccessful;
                    }
                    switch (status.Status)
                    {
                        case ItemBuildStatus.Pending:
                        case ItemBuildStatus.Running:
                        case ItemBuildStatus.Unknown:
                            if (wasSuccessful)
                            {
                                status.Status = ItemBuildStatus.CompletedSuccess;
                            }
                            else
                            {
                                status.Status = ItemBuildStatus.CompletedFailed;
                            }
                            break;
                    }
                }
            }
            catch
            {
                if (status != null) status.Status = ItemBuildStatus.CompletedFailed;
                throw;
            }
            finally
            {
                if (status != null) status.TimeCompleted = DateTime.Now;
            }
        }
        private void CancelTasks(IList tasks)
        {
            ItemBuildStatus overallStatus = ItemBuildStatus.Cancelled;
            List<ItemStatus> statuses = new List<ItemStatus>();
            foreach (ITask task in tasks)
            {
                if (currentProjectItems.ContainsKey(task))
                {
                    ItemStatus status = currentProjectItems[task];
                    if ((status.Parent != null) && !statuses.Contains(status.Parent))
                    {
                        statuses.Add(status.Parent);
                    }
                    if (status.Status == ItemBuildStatus.Pending) status.Status = ItemBuildStatus.Cancelled;
                    if (status.Status == ItemBuildStatus.CompletedFailed)
                    {
                        overallStatus = ItemBuildStatus.CompletedFailed;
                    }
                    else if ((status.Status == ItemBuildStatus.CompletedSuccess) && (overallStatus == ItemBuildStatus.Cancelled))
                    {
                        overallStatus = ItemBuildStatus.CompletedSuccess;
                    }
                }
            }
            foreach (ItemStatus status in statuses)
            {
                status.Status = overallStatus;
                if ((overallStatus == ItemBuildStatus.CompletedFailed) ||
                    (overallStatus == ItemBuildStatus.CompletedSuccess))
                {
                    status.TimeCompleted = DateTime.Now;
                }
            }
        }
        private void AddBreakersToMessages(IIntegrationResult result)
        {
            List<string> breakers = new List<string>();
            string breakingusers = string.Empty;
            foreach (Modification mod in result.Modifications)
            {
                if (!breakers.Contains(mod.UserName))
                {
                    breakers.Add(mod.UserName);
                }
            }
            foreach (string UserName in result.FailureUsers)
            {
                if (!breakers.Contains(UserName))
                {
                    breakers.Add(UserName);
                }
            }
            if (breakers.Count > 0)
            {
                breakingusers = string.Empty;
                foreach (string user in breakers)
                {
                    breakingusers += user + ", ";
                }
                breakingusers = breakingusers.Remove(breakingusers.Length - 2, 2);
            }
            AddMessage(new Message(breakingusers, Message.MessageKind.Breakers));
        }
        private void AddFailedTaskToMessages()
        {
            var failedTasks = new List<string>();
            FindFailedTasks(currentProjectStatus, failedTasks);
            if (failedTasks.Count > 0)
            {
                AddMessage(
                    new Message(
                         string.Join(
                            ", ",
                            failedTasks.ToArray()), Message.MessageKind.FailingTasks) );
            }
        }
        private void FindFailedTasks(ItemStatus item, List<string> failedTasks)
        {
            if (item.ChildItems.Count > 0)
            {
                foreach (var childItem in item.ChildItems)
                {
                    FindFailedTasks(childItem, failedTasks);
                }
            }
            else
            {
                if (item.Status == ItemBuildStatus.CompletedFailed)
                {
                    if (string.IsNullOrEmpty(item.Description))
                    { failedTasks.Add(item.Name); }
                    else
                    { failedTasks.Add(item.Description); }
                }
            }
        }
        public void Initialize()
        {
            Log.Info(string.Format("Initializing Project [{0}]", Name));
            SourceControl.Initialize(this);
        }
        public void Purge(bool purgeWorkingDirectory, bool purgeArtifactDirectory, bool purgeSourceControlEnvironment)
        {
            Log.Info(string.Format("Purging Project [{0}]", Name));
            if (purgeSourceControlEnvironment)
            {
                SourceControl.Purge(this);
            }
            if (purgeWorkingDirectory && Directory.Exists(WorkingDirectory))
            {
                new IoService().DeleteIncludingReadOnlyObjects(WorkingDirectory);
            }
            if (purgeArtifactDirectory && Directory.Exists(ArtifactDirectory))
            {
                new IoService().DeleteIncludingReadOnlyObjects(ArtifactDirectory);
            }
        }
        public string Statistics
        {
            get { return StatisticsPublisher.LoadStatistics(ArtifactDirectory); }
        }
        public string ModificationHistory
        {
            get { return ModificationHistoryPublisher.LoadHistory(ArtifactDirectory); }
        }
        public string RSSFeed
        {
            get { return RssPublisher.LoadRSSDataDocument(ArtifactDirectory); }
        }
        public IIntegrationRepository IntegrationRepository
        {
            get { return this; }
        }
        public static string DefaultUrl()
        {
            return string.Format("http://{0}/ccnet", Environment.MachineName);
        }
        public ProjectStatus CreateProjectStatus(IProjectIntegrator integrator)
        {
            var lastIntegration = this.LastIntegration;
            ProjectStatus status = new ProjectStatus(
                this.Name,
                this.Category,
                this.CurrentActivity,
                lastIntegration.Status,
                integrator.State,
                this.WebURL,
                lastIntegration.StartTime,
                lastIntegration.Label,
                lastIntegration.LastSuccessfulIntegrationLabel,
                this.Triggers.NextBuild,
                this.CurrentBuildStage(),
                this.QueueName,
                this.QueuePriority);
            status.Description = this.Description;
            status.Messages = (Message[])this.messages.ToArray(typeof(Message));
            return status;
        }
        private string CurrentBuildStage()
        {
            if (CurrentActivity == ProjectActivity.Building ||
    CurrentActivity == ProjectActivity.CheckingModifications)
    return integrationResultManager.CurrentIntegration.BuildProgressInformation.GetBuildProgressInformation();
            else
    return string.Empty;
        }
        private IntegrationSummary LastIntegration
        {
            get { return integrationResultManager.LastIntegration; }
        }
        public void AddMessage(Message message)
        {
            messages.Add(message);
        }
        public string GetBuildLog(string buildName)
        {
            string logDirectory = GetLogDirectory();
            if (string.IsNullOrEmpty(logDirectory)) return string.Empty;
            var filepath = Path.Combine(logDirectory, buildName);
            var fileInfo = new FileInfo(filepath);
            if (fileInfo.Length > 1048576)
            {
                var fileSizeInMB = Convert.ToInt32(fileInfo.Length / 524288);
                try
                {
                    using (new MemoryFailPoint(fileSizeInMB))
                    {
                    }
                }
                catch (InsufficientMemoryException error)
                {
                    throw new CruiseControlException("Insufficient memory to retrieve log: " + error.Message, error);
                }
            }
            using (StreamReader sr = new StreamReader(filepath))
            {
                return sr.ReadToEnd();
            }
        }
        public string[] GetBuildNames()
        {
            string logDirectory = GetLogDirectory();
            if (string.IsNullOrEmpty(logDirectory)) return new string[0];
            try
            {
                string[] logFileNames = LogFileUtil.GetLogFileNames(logDirectory);
                Array.Sort(logFileNames);
                Array.Reverse(logFileNames);
                return logFileNames;
            }
            catch (DirectoryNotFoundException error)
            {
                throw new CruiseControlException("Log directory does not exist. This normally occurs because there are no builds for this project.", error);
            }
        }
        public string[] GetMostRecentBuildNames(int buildCount)
        {
            string[] buildNames = GetBuildNames();
            ArrayList buildNamesToReturn = new ArrayList();
            for (int i = 0; i < ((buildCount < buildNames.Length) ? buildCount : buildNames.Length); i++)
            {
                buildNamesToReturn.Add(buildNames[i]);
            }
            return (string[])buildNamesToReturn.ToArray(typeof(string));
        }
        public string GetLatestBuildName()
        {
            string[] buildNames = GetBuildNames();
            if (buildNames.Length > 0)
            {
                return buildNames[0];
            }
            else
            {
                return string.Empty;
            }
        }
        private string GetLogDirectory()
        {
            XmlLogPublisher publisher = GetLogPublisher();
            string logDirectory = publisher.LogDirectory(ArtifactDirectory);
            if (!Directory.Exists(logDirectory))
            {
                Log.Warning("Log Directory [ " + logDirectory + " ] does not exist. Are you sure any builds have completed?");
            }
            return logDirectory;
        }
        private XmlLogPublisher GetLogPublisher()
        {
            foreach (ITask publisher in Publishers)
            {
                if (publisher is XmlLogPublisher)
                {
                    return (XmlLogPublisher)publisher;
                }
            }
            throw new CruiseControlException("Unable to find Log Publisher for project so can't find log file");
        }
        public void CreateLabel(IIntegrationResult result)
        {
            if (Labeller is IParamatisedItem)
            {
                (Labeller as IParamatisedItem).ApplyParameters(result.IntegrationRequest.BuildValues,
                    parameters);
            }
            result.Label = Labeller.Generate(result);
        }
        [ReflectorProperty("initialState", Required = false)]
        public ProjectInitialState InitialState
        {
            get { return initialState; }
            set { initialState = value; }
        }
        [ReflectorProperty("startupMode", Required = false)]
        public ProjectStartupMode StartupMode
        {
            get { return startupMode; }
            set { startupMode = value; }
        }
        public virtual void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            if (security.RequiresServerSecurity &&
                (configuration.SecurityManager is NullSecurityManager))
            {
                errorProcesser.ProcessError(
                    new ConfigurationException(
                        string.Format("Security is defined for project '{0}', but not defined at the server", this.Name)));
            }
            this.ValidateProject(errorProcesser);
            this.ValidateItem(sourceControl, configuration, parent, errorProcesser);
            this.ValidateItem(labeller, configuration, parent, errorProcesser);
            this.ValidateItems(PrebuildTasks, configuration, parent, errorProcesser);
            this.ValidateItems(tasks, configuration, parent, errorProcesser);
            this.ValidateItems(publishers, configuration, parent, errorProcesser);
            this.ValidateItem(state, configuration, parent, errorProcesser);
            this.ValidateItem(security, configuration, parent, errorProcesser);
            var mt = (MultipleTrigger)this.Triggers;
            if (mt != null)
            {
                this.ValidateItems(mt.Triggers, configuration, parent, errorProcesser);
            }
        }
        private void ValidateProject(IConfigurationErrorProcesser errorProcesser)
        {
            if (ContainsInvalidChars(this.Name))
            {
                errorProcesser.ProcessWarning(
                    string.Format("Project name '{0}' contains some chars that could cause problems, better use only numbers and letters",
                        Name));
            }
        }
        private bool ContainsInvalidChars(string item)
        {
            bool result = false;
            for (Int32 i = 0; i < item.Length; i++)
            {
                if (!char.IsLetterOrDigit(item, i) &&
                    item[i] != '.' &&
                    item[i] != ' ' &&
                    item[i] != '-' &&
                    item[i] != '_')
                {
                    result = true;
                    break;
                }
            }
            return result;
        }
        private void ValidateItem(object item, IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            if ((item != null) && (item is IConfigurationValidation))
            {
                (item as IConfigurationValidation).Validate(configuration, parent.Wrap(this), errorProcesser);
            }
        }
        private void ValidateItems(IEnumerable items, IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            if (items != null)
            {
                foreach (object item in items)
                {
                    this.ValidateItem(item, configuration, parent, errorProcesser);
                }
            }
        }
        public virtual ItemStatus GenerateSnapshot()
        {
            lock (currentProjectStatus)
            {
                if (currentProjectStatus.Status == ItemBuildStatus.Unknown)
                {
                    switch (LastIntegration.Status)
                    {
                        case IntegrationStatus.Success:
                            currentProjectStatus.Status = ItemBuildStatus.CompletedSuccess;
                            break;
                        case IntegrationStatus.Failure:
                        case IntegrationStatus.Exception:
                            currentProjectStatus.Status = ItemBuildStatus.CompletedFailed;
                            break;
                    }
                }
                return currentProjectStatus.Clone();
            }
        }
        public virtual void RecordSourceControlOperation(SourceControlOperation operation, ItemBuildStatus status)
        {
            if (sourceControlOperations.ContainsKey(operation))
            {
                sourceControlOperations[operation].Status = status;
                switch (status)
                {
                    case ItemBuildStatus.Running:
                        sourceControlOperations[operation].TimeStarted = DateTime.Now;
                        break;
                    case ItemBuildStatus.CompletedFailed:
                    case ItemBuildStatus.CompletedSuccess:
                        sourceControlOperations[operation].TimeCompleted = DateTime.Now;
                        break;
                }
            }
        }
        public virtual List<PackageDetails> RetrievePackageList()
        {
            var lastBuild = this.GetLatestBuildName();
            if (!string.IsNullOrEmpty(lastBuild))
            {
                var logDetails = new LogFile(lastBuild);
                var listFile = Path.Combine(logDetails.Label, Name + "-packages.xml");
                listFile = Path.Combine(ArtifactDirectory, listFile);
                var packages = LoadPackageList(listFile);
                return packages;
            }
            else
            {
                return new List<PackageDetails>();
            }
        }
        public virtual List<PackageDetails> RetrievePackageList(string buildLabel)
        {
            var listFile = Path.Combine(buildLabel, Name + "-packages.xml");
            listFile = Path.Combine(ArtifactDirectory, listFile);
            if (File.Exists(listFile))
            {
                var packages = LoadPackageList(listFile);
                return packages;
            }
            else
            {
                return new List<PackageDetails>();
            }
        }
        private List<PackageDetails> LoadPackageList(string fileName)
        {
            List<PackageDetails> packages = new List<PackageDetails>();
            if (File.Exists(fileName))
            {
                XmlDocument packageList = new XmlDocument();
                packageList.Load(fileName);
                foreach (XmlElement packageElement in packageList.SelectNodes("/packages/package"))
                {
                    string packageFileName = packageElement.GetAttribute("file");
                    packageFileName = packageFileName.Replace(ArtifactDirectory, string.Empty);
                    if (packageFileName.StartsWith("\\")) packageFileName = packageFileName.Substring(1);
                    PackageDetails details = new PackageDetails(packageFileName);
                    details.Name = packageElement.GetAttribute("name");
                    details.BuildLabel = packageElement.GetAttribute("label");
                    details.DateTime = DateTime.Parse(packageElement.GetAttribute("time"));
                    details.NumberOfFiles = Convert.ToInt32(packageElement.GetAttribute("files"));
                    details.Size = Convert.ToInt64(packageElement.GetAttribute("size"));
                    packages.Add(details);
                }
            }
            return packages;
        }
        public virtual List<ParameterBase> ListBuildParameters()
        {
            var parameterList = new List<ParameterBase>();
            if (AskForForceBuildReason != DisplayLevel.None)
            {
                var reasonParameter = new TextParameter()
                {
                    Description = "What is the reason for this force build?",
                    DisplayName = "Reason",
                    Name = "CCNetForceBuildReason",
                    IsRequired = (AskForForceBuildReason == DisplayLevel.Required)
                };
                parameterList.Add(reasonParameter);
            }
            if (parameters != null)
            {
                foreach (var parameter in parameters)
                {
                    parameter.GenerateClientDefault();
                }
                parameterList.AddRange(parameters);
            }
            return parameterList;
        }
        public void ClearNotNeededMessages()
        {
            ClearMessages(Message.MessageKind.Breakers);
            ClearMessages(Message.MessageKind.FailingTasks);
        }
        public void InitialiseForBuild(IntegrationRequest request)
        {
            this.ClearMessages(Message.MessageKind.BuildStatus);
            this.AddMessage(new Message(request.ToString(), Message.MessageKind.BuildStatus));
        }
    }
}
