using System;
using System.Security;
using System.Windows.Forms;
using WikiFunctions;
namespace AutoWikiBrowser
{
    internal static class Program
    {
        [STAThread]
        static void Main(string[] args)
        {
            try
            {
                System.Threading.Thread.CurrentThread.Name = "Main thread";
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                Application.ThreadException += ApplicationThreadException;
                if (Globals.UsingMono)
                {
                    MessageBox.Show("AWB is not currently supported by mono. You may use it for testing purposes, but functionality is not guaranteed.",
                        "Not supported",
                        MessageBoxButtons.OK, MessageBoxIcon.Warning);
                }
                AwbDirs.MigrateDefaultSettings();
                MainForm awb = new MainForm();
                AWB = awb;
                awb.ParseCommandLine(args);
                Article.SetAddListener(MyTrace.AddListener, MyTrace, "AWB");
                Application.Run(awb);
            }
            catch (Exception ex)
            {
                if (ex is SecurityException)
                    MessageBox.Show("AWB is unable to start up from the current location due to a lack of permissions.\r\nPlease try on a local drive or similar.", "Permissions Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                else
                    ErrorHandler.Handle(ex);
            }
        }
        private static void ApplicationThreadException(object sender, System.Threading.ThreadExceptionEventArgs e)
        {
            ErrorHandler.Handle(e.Exception);
        }
        internal static Version Version { get { return System.Reflection.Assembly.GetExecutingAssembly().GetName().Version; } }
        internal static string VersionString { get { return Version.ToString(); } }
        internal const string Name = "AutoWikiBrowser";
        internal static WikiFunctions.Plugin.IAutoWikiBrowser AWB;
        internal static readonly Logging.MyTrace MyTrace = new Logging.MyTrace();
    }
}
