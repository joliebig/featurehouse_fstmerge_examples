using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("nullTask")]
    public class NullTask : TaskBase
 {
        [ReflectorProperty("simulateFailure", Required = false)]
        public bool SimulateFailure = false;
        [ReflectorProperty("simulateFailureMessage", Required = false)]
        public string SimulateFailureMessage = "Simulating Failure";
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
