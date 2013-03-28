using System;
using System.Configuration;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Runtime.InteropServices;
using System.ServiceProcess;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Service
{
 public class CCService : ServiceBase
 {
  public const string DefaultServiceName = "CCService";
  private const string DefaultConfigFileName = "ccnet.config";
  private readonly string DefaultDirectory = AppDomain.CurrentDomain.BaseDirectory;
  private ICruiseServer server;
  public CCService()
  {
   ServiceName = LookupServiceName();
  }
  private string ConfigFilename
  {
   get
   {
    string configFilename = ConfigurationManager.AppSettings["ccnet.config"];
    return StringUtil.IsBlank(configFilename) ? DefaultConfigFilePath() : configFilename;
   }
  }
  private static string Remoting
  {
   get { return ConfigurationManager.AppSettings["remoting"]; }
  }
  protected override void OnStart(string[] args)
  {
   Directory.SetCurrentDirectory(DefaultDirectory);
            Log.Info(string.Format("CruiseControl.NET Server {0} -- .NET Continuous Integration Server", Assembly.GetExecutingAssembly().GetName().Version));
            AssemblyCopyrightAttribute[] copyrightAttributes = (AssemblyCopyrightAttribute[])Assembly.GetExecutingAssembly().GetCustomAttributes(typeof(AssemblyCopyrightAttribute), false);
            if (copyrightAttributes.Length > 0)
            {
                Log.Info(string.Format("{0}  All Rights Reserved.", copyrightAttributes[0].Copyright));
            }
            Log.Info(string.Format(".NET Runtime Version: {0}{2}\tImage Runtime Version: {1}", Environment.Version, Assembly.GetExecutingAssembly().ImageRuntimeVersion, GetRuntime()));
            Log.Info(string.Format("OS Version: {0}\tServer locale: {1}", Environment.OSVersion, CultureInfo.CurrentCulture));
            VerifyConfigFileExists();
            CreateAndStartCruiseServer();
        }
  private string DefaultConfigFilePath()
  {
   return Path.Combine(DefaultDirectory, DefaultConfigFileName);
  }
  private void VerifyConfigFileExists()
  {
   FileInfo configFileInfo = new FileInfo(ConfigFilename);
   if (!configFileInfo.Exists)
   {
    throw new Exception(string.Format("CruiseControl.NET configuration file {0} does not exist.", configFileInfo.FullName));
   }
  }
  private void CreateAndStartCruiseServer()
  {
   server = new CruiseServerFactory().Create(UseRemoting(), ConfigFilename);
   server.Start();
  }
  private static bool UseRemoting()
  {
   return (Remoting != null && Remoting.Trim().ToLower() == "on");
  }
  protected override void OnStop()
  {
   server.Abort();
  }
  protected override void OnPause()
  {
   server.Stop();
  }
  protected override void OnContinue()
  {
   server.Start();
  }
  private static string LookupServiceName()
  {
   string serviceName = ConfigurationManager.AppSettings["service.name"];
   return StringUtil.IsBlank(serviceName) ? DefaultServiceName : serviceName;
  }
  private static void Main()
  {
   AllocateWin32Console();
   Run(new ServiceBase[] {new CCService()});
  }
  private static void AllocateWin32Console()
  {
   if (new ExecutionEnvironment().IsRunningOnWindows)
    AllocConsole();
  }
  [DllImport("kernel32.dll", SetLastError = true)]
  private static extern bool AllocConsole();
        private static string GetRuntime()
        {
            if (Type.GetType("Mono.Runtime") != null)
                return " [Mono]";
            return string.Empty;
        }
 }
}
