using System;
using System.IO;
using System.Reflection;
using System.Threading;
using System.Runtime.Remoting;
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
                    AppDomain newDomain = AppDomain.CreateDomain("CC.Net",
                        null,
                        AppDomain.CurrentDomain.BaseDirectory,
                        AppDomain.CurrentDomain.RelativeSearchPath,
                        true);
                    runner = newDomain.CreateInstanceFromAndUnwrap(Assembly.GetExecutingAssembly().Location,
                        typeof(AppRunner).FullName) as AppRunner;
                    result = runner.Run(args);
                    AppDomain.Unload(newDomain);
                    while (DateTime.Now < restartTime)
                    {
                        Thread.Sleep(500);
                    }
                }
            }
            return result;
        }
 }
}
