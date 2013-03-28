namespace ThoughtWorks.CruiseControl.WebDashboard.IO
{
    using System.Configuration;
    using System.IO;
    using ThoughtWorks.CruiseControl.Core.Util;
    public static class ProgramDataFolder
    {
        private static readonly IFileSystem fileSystem = new SystemIoFileSystem();
        private static readonly IExecutionEnvironment executionEnvironment = new ExecutionEnvironment();
        private static string location;
        static ProgramDataFolder()
        {
            location = ConfigurationManager.AppSettings["CruiseControlDataLocation"];
        }
        public static string Location
        {
            get
            {
                if (string.IsNullOrEmpty(location))
                {
                    location = executionEnvironment.GetDefaultProgramDataFolder(ApplicationType.WebDashboard);
                    Log.Debug(string.Concat("Initialising data folder: '", location, "'."));
                    fileSystem.EnsureFolderExists(location);
                }
                return location;
            }
            set
            {
                Log.Debug(string.Concat("Data folder set to: '", value, "'."));
                fileSystem.EnsureFolderExists(value);
                location = value;
            }
        }
        public static string MapPath(string path)
        {
            var fullPath = new DirectoryInfo(Path.Combine(Location, path)).FullName;
            return fullPath;
        }
    }
}
