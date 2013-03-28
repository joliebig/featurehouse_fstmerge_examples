namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using Exortech.NetReflector;
    [ReflectorType("nullTask")]
    public class NullTask : TaskBase
 {
        public NullTask()
        {
            this.SimulateFailure = false;
            this.SimulateFailureMessage = "Simulating Failure";
        }
        [ReflectorProperty("simulateFailure", Required = false)]
        public bool SimulateFailure { get; set; }
        [ReflectorProperty("simulateFailureMessage", Required = false)]
        public string SimulateFailureMessage { get; set; }
        protected override bool Execute(IIntegrationResult result)
        {
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : "Executing null task");
            System.Threading.Thread.Sleep(5000);
            if (SimulateFailure)
            {
                result.AddTaskResult(SimulateFailureMessage);
                throw new System.Exception(SimulateFailureMessage);
            }
            else
            {
                result.AddTaskResult("All OK for " + (!string.IsNullOrEmpty(Description) ? Description : "Null task"));
            }
            return !SimulateFailure;
        }
    }
}
