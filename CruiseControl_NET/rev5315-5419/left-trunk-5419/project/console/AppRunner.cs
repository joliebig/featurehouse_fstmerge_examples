using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.IO;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Core.Util;
using System.Runtime.Remoting;
namespace ThoughtWorks.CruiseControl.Console
{
    public class AppRunner
        : MarshalByRefObject
    {
        private ConsoleRunner runner;
        private bool isStopping = false;
        private object lockObject = new object();
        public int Run(string[] args)
        {
            ArgumentParser parsedArgs = new ArgumentParser(args);
            try
            {
                runner = new ConsoleRunner(parsedArgs, new CruiseServerFactory());
                runner.Run();
                return 0;
            }
            catch (Exception ex)
            {
                Log.Error(ex);
                if (!parsedArgs.NoPauseOnError)
                {
                    System.Console.WriteLine("An unexpected error has caused the console to crash, please press any key to continue...");
                    System.Console.ReadKey();
                }
                return 1;
            }
            finally
            {
                runner = null;
            }
        }
        public void Stop(string reason)
        {
            bool stopRunner = false;
            lock (lockObject)
            {
                if (!isStopping)
                {
                    stopRunner = true;
                    isStopping = true;
                }
            }
            if (stopRunner)
            {
                Log.Info("Stopping console: " + reason);
                try
                {
                    runner.Stop();
                }
                catch (RemotingException)
                {
                }
            }
        }
    }
}
