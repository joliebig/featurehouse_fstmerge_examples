using System;
using System.Globalization;
using System.IO;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.Telelogic
{
 [ReflectorType("synergyConnection")]
 public class SynergyConnectionInfo
 {
  private string username;
  private string password;
  private string homeDirectory;
  private string clientDatabaseDirectory;
  private string executable;
  private string workingDirectory;
  public SynergyConnectionInfo()
  {
   Executable = "ccm.exe";
   Timeout = 3600;
   Host = "localhost";
   Database = null;
   Username = "%USERNAME%";
   Password = String.Empty;
   Role = "build_mgr";
   HomeDirectory = @"%SystemDrive%\cmsynergy\%USERNAME%";
   ClientDatabaseDirectory = @"%SystemDrive%\cmsynergy\u0000";
   WorkingDirectory = @"%ProgramFiles%\Telelogic\CM Synergy 6.3\bin";
   Reset();
  }
  public string SessionId;
        [ReflectorProperty("executable")]
  public string Executable
  {
   get { return executable; }
   set { executable = Environment.ExpandEnvironmentVariables(value); }
  }
        [ReflectorProperty("workingDirectory", Required = false)]
  public string WorkingDirectory
  {
   get { return workingDirectory; }
   set { workingDirectory = Environment.ExpandEnvironmentVariables(value); }
  }
        [ReflectorProperty("host")]
  public string Host;
        [ReflectorProperty("database")]
  public string Database;
  public char Delimiter;
  public string DatabaseName
  {
   get
   {
    string databaseName;
    databaseName = Path.GetFileName(Database);
    if (databaseName.Length == 0)
    {
     databaseName = Directory.GetParent(Database).Name;
    }
    return (databaseName);
   }
  }
        [ReflectorProperty("polling", Required = false)]
  public bool PollingEnabled;
        [ReflectorProperty("username", Required = false)]
  public string Username
  {
   get { return username; }
   set { username = Environment.ExpandEnvironmentVariables(value); }
  }
        [ReflectorProperty("password", Required = false)]
  public string Password
  {
   get { return password; }
   set { password = Environment.ExpandEnvironmentVariables(value); }
  }
        [ReflectorProperty("role", Required = false)]
  public string Role;
        [ReflectorProperty("homeDirectory", Required = false)]
  public string HomeDirectory
  {
   get { return homeDirectory; }
   set { homeDirectory = Environment.ExpandEnvironmentVariables(value); }
  }
        [ReflectorProperty("clientDatabaseDirectory", Required = false)]
  public string ClientDatabaseDirectory
  {
   get { return clientDatabaseDirectory; }
   set { clientDatabaseDirectory = Environment.ExpandEnvironmentVariables(value); }
  }
        [ReflectorProperty("timeout", Required = false)]
  public int Timeout;
  public IFormatProvider FormatProvider = CultureInfo.CurrentCulture;
  public void Reset()
  {
   SessionId = null;
   Delimiter = '-';
  }
 }
}
