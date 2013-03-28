namespace ThoughtWorks.CruiseControl.Core.Tasks.Conditions
{
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    [ReflectorType("fileExistsCondition")]
    public class FileExistsTaskCondition
        : ConditionBase
    {
        [ReflectorProperty("file", Required = true)]
        public string FileName { get; set; }
        public IFileSystem FileSystem { get; set; }
        protected override bool Evaluate(IIntegrationResult result)
        {
            var fileName = result.BaseFromWorkingDirectory(this.FileName);
            this.LogDescriptionOrMessage("Checking for file '" + fileName + "'");
            var fileSystem = this.FileSystem ?? new SystemIoFileSystem();
            var exists = fileSystem.FileExists(fileName);
            return exists;
        }
    }
}
