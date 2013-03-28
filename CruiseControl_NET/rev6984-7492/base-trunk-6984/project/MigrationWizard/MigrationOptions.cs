using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.MigrationWizard
{
    public class MigrationOptions
    {
        public static string[] AllowedVersions = {
                                                    "1.4.4",
                                                    "1.4.3",
                                                    "1.4.2",
                                                    "1.4.1",
                                                    "1.4.0"
                                                };
        public bool MigrateServer { get; set; }
        public string CurrentServerLocation { get; set; }
        public string NewServerLocation { get; set; }
        public bool BackupServerConfiguration { get; set; }
        public bool MigrateConfiguration { get; set; }
        public bool BackupConfiguration { get; set; }
        public string ConfigurationLocation { get; set; }
        public bool MigrateWebDashboard { get; set; }
        public string CurrentWebDashboardLocation { get; set; }
        public string NewWebDashboardLocation { get; set; }
        public string CurrentVersion { get; set; }
    }
}
