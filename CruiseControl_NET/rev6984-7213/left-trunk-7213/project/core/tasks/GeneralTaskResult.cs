namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    public class GeneralTaskResult
        : ITaskResult
    {
        private readonly string data;
        private readonly bool succeeded;
        public GeneralTaskResult(bool succeeded, string data)
        {
            this.succeeded = succeeded;
            this.data = data;
        }
        public string Data
        {
            get { return this.data; }
        }
        public bool CheckIfSuccess()
        {
            return this.succeeded;
        }
    }
}
