namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    using System.IO;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Tasks;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    [ReflectorType("xmllogger")]
    public class XmlLogPublisher
        : TaskBase
    {
        private readonly IFileSystem fileSystem;
        [ReflectorProperty("logDir", Required = false)]
        public string ConfiguredLogDirectory { get; set; }
        protected override bool Execute(IIntegrationResult result)
        {
            var fileSystem = this.fileSystem ?? new SystemIoFileSystem();
            if (result.Status == IntegrationStatus.Unknown)
            {
                return true;
            }
            var logLocation = this.Context.GenerateLogFolder(this.ConfiguredLogDirectory ?? this.Context.Project.LogFolder);
            var logName = this.Context.GenerateLogFilename();
            fileSystem.DeleteFile(logName);
            using (var writer = new StreamWriter(fileSystem.OpenOutputStream(logName)))
            {
                this.Context.WriteCurrentLog(writer);
            }
            return true;
        }
    }
}
