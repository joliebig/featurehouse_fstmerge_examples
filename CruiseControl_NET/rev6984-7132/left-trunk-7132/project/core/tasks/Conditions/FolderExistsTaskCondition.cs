namespace ThoughtWorks.CruiseControl.Core.Tasks.Conditions
{
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    [ReflectorType("folderExistsCondition")]
    public class FolderExistsTaskCondition
        : ConditionBase
    {
        [ReflectorProperty("folder", Required = true)]
        public string FolderName { get; set; }
        public IFileSystem FileSystem { get; set; }
        protected override bool Evaluate(IIntegrationResult result)
        {
            var folderName = result.BaseFromWorkingDirectory(this.FolderName);
            this.LogDescriptionOrMessage("Checking for folder '" + folderName + "'");
            var fileSystem = this.FileSystem ?? new SystemIoFileSystem();
            var exists = fileSystem.DirectoryExists(folderName);
            return exists;
        }
    }
}
