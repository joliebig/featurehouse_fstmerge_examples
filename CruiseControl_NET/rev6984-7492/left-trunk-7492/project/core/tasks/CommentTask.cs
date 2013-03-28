namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("commentTask")]
    public class CommentTask
        : TaskBase
    {
        [ReflectorProperty("message", Required = true)]
        public string Message { get; set; }
        [ReflectorProperty("failure", Required = false)]
        public bool FailTask { get; set; }
        public ILogger Logger { get; set; }
        protected override bool Execute(IIntegrationResult result)
        {
            result.BuildProgressInformation
                .SignalStartRunTask("Adding a comment to the log");
            (this.Logger ?? new DefaultLogger())
                .Debug("Logging " + (this.FailTask ? "error " : string.Empty) + "message: " + this.Message);
            result.AddTaskResult(
                new GeneralTaskResult(!this.FailTask, Message));
            return true;
        }
    }
}
