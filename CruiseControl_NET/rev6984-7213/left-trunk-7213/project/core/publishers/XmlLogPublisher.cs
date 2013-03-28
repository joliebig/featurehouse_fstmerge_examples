namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    using System.IO;
    using System.Xml;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.Core.Tasks;
    [ReflectorType("xmllogger")]
    public class XmlLogPublisher
        : TaskBase, IMergeTask
    {
  public static readonly string DEFAULT_LOG_SUBDIRECTORY = "buildlogs";
        [ReflectorProperty("logDir", Required = false)]
        public string ConfiguredLogDirectory { get; set; }
  public string LogDirectory(string artifactDirectory)
  {
            if (string.IsNullOrEmpty(ConfiguredLogDirectory))
   {
    return Path.Combine(artifactDirectory, DEFAULT_LOG_SUBDIRECTORY);
   }
   else if (Path.IsPathRooted(ConfiguredLogDirectory))
   {
    return ConfiguredLogDirectory;
   }
   else
   {
    return Path.Combine(artifactDirectory, ConfiguredLogDirectory);
   }
  }
        protected override bool Execute(IIntegrationResult result)
        {
            if (result.Status == IntegrationStatus.Unknown)
                return true;
            using (XmlIntegrationResultWriter integrationWriter = new XmlIntegrationResultWriter(CreateWriter(LogDirectory(result.ArtifactDirectory), GetFilename(result))))
            {
    integrationWriter.Formatting = Formatting.Indented;
    integrationWriter.Write(result);
            }
            result.BuildLogDirectory = LogDirectory(result.ArtifactDirectory);
            return true;
        }
        private TextWriter CreateWriter(string dirname, string filename)
        {
            if (!Directory.Exists(dirname))
                Directory.CreateDirectory(dirname);
            string path = Path.Combine(dirname, filename);
   return new StreamWriter(path);
        }
        private string GetFilename(IIntegrationResult result)
        {
            return Util.StringUtil.RemoveInvalidCharactersFromFileName(new LogFile(result).Filename);
        }
    }
}
