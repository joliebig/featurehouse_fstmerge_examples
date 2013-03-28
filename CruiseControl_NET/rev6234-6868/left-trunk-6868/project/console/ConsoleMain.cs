using System;
using System.IO;
using System.Reflection;
using System.Threading;
using System.Runtime.Remoting;
using System.Configuration;
namespace ThoughtWorks.CruiseControl.Console
{
 public class ConsoleMain
 {
        private static object lockObject = new object();
  [STAThread]
  internal static int Main(string[] args)
  {
            bool restart = true;
            int result = 0;
            DateTime restartTime = DateTime.MinValue;
            using (FileSystemWatcher watcher = new FileSystemWatcher(AppDomain.CurrentDomain.BaseDirectory, "*.dll"))
            {
                AppRunner runner = null;
                watcher.Changed += delegate(object sender, FileSystemEventArgs e)
                {
                    if (!restart)
                    {
                        lock (lockObject)
                        {
                            try
                            {
                                runner.Stop("One or more DLLs have changed");
                            }
                            catch (RemotingException)
                            {
                            }
                        }
                    }
                    restart = true;
                    restartTime = DateTime.Now.AddSeconds(10);
                };
                watcher.NotifyFilter = NotifyFilters.CreationTime | NotifyFilters.LastWrite | NotifyFilters.Size;
                watcher.EnableRaisingEvents = true;
                while (restart)
                {
                    restart = false;
                    var setting = ConfigurationManager.AppSettings["ShadowCopy"] ?? string.Empty;
                    var useShadowCopying = !(string.Equals(setting, "off", StringComparison.InvariantCultureIgnoreCase) ||
                        string.Equals(setting, "false", StringComparison.InvariantCultureIgnoreCase));
                    AppDomain runnerDomain;
                    try
                    {
                        runnerDomain = CreateNewDomain(useShadowCopying);
                    }
                    catch (FileLoadException)
                    {
                        useShadowCopying = false;
                        runnerDomain = CreateNewDomain(useShadowCopying);
                    }
                    runner = runnerDomain.CreateInstanceFromAndUnwrap(Assembly.GetExecutingAssembly().Location,
                        typeof(AppRunner).FullName) as AppRunner;
                    result = runner.Run(args, useShadowCopying);
                    AppDomain.Unload(runnerDomain);
                    while (DateTime.Now < restartTime)
                    {
                        Thread.Sleep(500);
                    }
                }
            }
            return result;
        }
        private static AppDomain CreateNewDomain(bool useShadowCopying)
        {
            return AppDomain.CreateDomain(
                "CC.Net",
                null,
                AppDomain.CurrentDomain.BaseDirectory,
                AppDomain.CurrentDomain.RelativeSearchPath,
                useShadowCopying);
        }
    }
}
