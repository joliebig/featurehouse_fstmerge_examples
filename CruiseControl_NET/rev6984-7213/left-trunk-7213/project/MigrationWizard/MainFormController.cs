using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.IO;
namespace ThoughtWorks.CruiseControl.MigrationWizard
{
    public class MainFormController
    {
        private MainForm mainForm;
        private List<MigrationEventArgs> migrationEvents = new List<MigrationEventArgs>();
        public MainFormController()
        {
            MigrationOptions = new MigrationOptions()
            {
                CurrentVersion = "1.4.4",
                BackupConfiguration = true,
                BackupServerConfiguration = true,
                ConfigurationLocation = Path.Combine(
                    Environment.GetFolderPath(Environment.SpecialFolder.ProgramFiles),
                    Path.Combine("CruiseControl.NET",
                        Path.Combine("Server", "ccnet.config"))),
                CurrentServerLocation = Path.Combine(
                    Environment.GetFolderPath(Environment.SpecialFolder.ProgramFiles),
                    Path.Combine("CruiseControl.NET", "Server")),
                CurrentWebDashboardLocation = Path.Combine(
                    Environment.GetFolderPath(Environment.SpecialFolder.ProgramFiles),
                    Path.Combine("CruiseControl.NET", "webdashboard")),
                MigrateConfiguration = true,
                MigrateServer = true,
                MigrateWebDashboard = true,
                NewServerLocation = Path.Combine(
                    Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData),
                    Path.Combine("CruiseControl.NET", "server")),
                NewWebDashboardLocation = Path.Combine(
                    Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData),
                    Path.Combine("CruiseControl.NET", "webdashboard"))
            };
            MigrationEngine = new MigrationEngine()
            {
                MigrationOptions = MigrationOptions
            };
            MigrationEngine.Message += (o, e) => migrationEvents.Add(e);
        }
        public MigrationOptions MigrationOptions { get; set; }
        public MigrationEngine MigrationEngine { get; set; }
        public List<MigrationEventArgs> MigrationEvents
        {
            get { return migrationEvents; }
        }
        public void ShowUI()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            mainForm = new MainForm(this);
            mainForm.Start();
            Application.Run(mainForm);
        }
        public void Close()
        {
            if (mainForm != null)
            {
                mainForm.Close();
                mainForm = null;
            }
        }
        public void ViewLog(Form owner)
        {
            var form = new LogForm();
            form.LoadMessages(MigrationEvents);
            if (owner != null)
            {
                form.ShowDialog(owner);
            }
            else
            {
                form.ShowDialog();
            }
        }
    }
}
