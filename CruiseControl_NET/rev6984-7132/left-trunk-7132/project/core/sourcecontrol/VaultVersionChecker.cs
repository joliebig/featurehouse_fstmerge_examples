namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    using System.Diagnostics;
    using System.Reflection;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("vault")]
 public class VaultVersionChecker
        : SourceControlBase
 {
  private Timeout timeout = Timeout.DefaultTimeout;
  public const string DefaultExecutable = @"C:\Program Files\SourceGear\Vault Client\vault.exe";
  public const string DefaultHistoryArgs = "-excludeactions label,obliterate -rowlimit 0";
  public const string DefaultFolder = "$";
  public const string DefaultFileTime = "checkin";
  public const int DefaultPollRetryWait = 5;
  public const int DefaultPollRetryAttempts = 5;
  public enum EForcedVaultVersion
  {
   None,
   Vault3,
   Vault317
  }
  private Vault3 _vaultSourceControl = null;
  private EForcedVaultVersion _forcedVaultVersion = EForcedVaultVersion.None;
        [ReflectorProperty("username", Required = false)]
        public string Username { get; set; }
        [ReflectorProperty("password", typeof(PrivateStringSerialiserFactory), Required = false)]
        public PrivateString Password { get; set; }
        [ReflectorProperty("host", Required = false)]
        public string Host { get; set; }
        [ReflectorProperty("repository", Required = false)]
        public string Repository { get; set; }
        [ReflectorProperty("folder", Required = false)]
        public string Folder { get; set; }
        [ReflectorProperty("executable", Required = false)]
        public string Executable { get; set; }
        [ReflectorProperty("ssl", Required = false)]
        public bool Ssl { get; set; }
        [ReflectorProperty("autoGetSource", Required = false)]
        public bool AutoGetSource { get; set; }
        [ReflectorProperty("applyLabel", Required = false)]
        public bool ApplyLabel { get; set; }
        [ReflectorProperty("historyArgs", Required = false)]
        public string HistoryArgs { get; set; }
        [ReflectorProperty("timeout", typeof(TimeoutSerializerFactory), Required = false)]
  public Timeout Timeout
  {
   get
   {
    return timeout;
   }
   set
   {
    if (value==null)
     timeout = Timeout.DefaultTimeout;
    else
     timeout = value;
   }
  }
        [ReflectorProperty("useWorkingDirectory", Required = false)]
        public bool UseVaultWorkingDirectory { get; set; }
        [ReflectorProperty("workingDirectory", Required = false)]
        public string WorkingDirectory { get; set; }
        [ReflectorProperty("setFileTime", Required = false)]
        public string setFileTime { get; set; }
        [ReflectorProperty("cleanCopy", Required = false)]
        public bool CleanCopy { get; set; }
        [ReflectorProperty("proxyServer", Required = false)]
        public string proxyServer { get; set; }
        [ReflectorProperty("proxyPort", Required = false)]
        public string proxyPort { get; set; }
        [ReflectorProperty("proxyUser", Required = false)]
        public string proxyUser { get; set; }
        [ReflectorProperty("proxyPassword", Required = false)]
        public string proxyPassword { get; set; }
        [ReflectorProperty("proxyDomain", Required = false)]
        public string proxyDomain { get; set; }
        [ReflectorProperty("otherVaultArguments", Required = false)]
        public string otherVaultArguments { get; set; }
        [ReflectorProperty("pollRetryWait", Required = false)]
        public int pollRetryWait { get; set; }
        [ReflectorProperty("pollRetryAttempts", Required = false)]
        public int pollRetryAttempts { get; set; }
  public Vault3 VaultSourceControl
  {
   get { return _vaultSourceControl; }
  }
        public VaultVersionChecker()
        {
            this.InitialiseDefaults();
        }
  public VaultVersionChecker(IHistoryParser historyParser, ProcessExecutor executor, EForcedVaultVersion forceVersion)
  {
            this.InitialiseDefaults();
            _forcedVaultVersion = forceVersion;
   switch ( forceVersion )
   {
    case EForcedVaultVersion.Vault3:
     _vaultSourceControl = new Vault3(this, historyParser, executor);
     break;
    case EForcedVaultVersion.Vault317:
     _vaultSourceControl = new Vault317(this, historyParser, executor);
     break;
    default:
     Debug.Fail("You have to force a version of Vault from_ the unit tests.");
     break;
   }
        }
        private void InitialiseDefaults()
        {
            this.Folder = DefaultFolder;
            this.Executable = DefaultExecutable;
            this.Ssl = false;
            this.AutoGetSource = true;
            this.ApplyLabel = false;
            this.HistoryArgs = DefaultHistoryArgs;
            this.UseVaultWorkingDirectory = true;
            this.setFileTime = DefaultFileTime;
            this.CleanCopy = false;
            this.pollRetryWait = DefaultPollRetryWait;
            this.pollRetryAttempts = DefaultPollRetryAttempts;
        }
        public override void Initialize(IProject project)
  {
   GetCorrectVaultInstance();
   VaultSourceControl.Initialize(project);
  }
        public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   GetCorrectVaultInstance();
   return VaultSourceControl.GetModifications(from_, to);
  }
        public override void LabelSourceControl(IIntegrationResult result)
  {
   GetCorrectVaultInstance();
   VaultSourceControl.LabelSourceControl(result);
  }
        public override void GetSource(IIntegrationResult result)
  {
   GetCorrectVaultInstance();
   VaultSourceControl.GetSource(result);
  }
        public override void Purge(IProject project)
  {
   GetCorrectVaultInstance();
   VaultSourceControl.Purge(project);
  }
  private void GetCorrectVaultInstance()
  {
   if ( VaultSourceControl != null )
    return;
   if ( VaultVersionIs317OrBetter() )
   {
    Log.Debug("Vault CLC is at least version 3.1.7.");
    _vaultSourceControl = new Vault317(this);
   }
   else
   {
    Log.Debug("Vault CLC is older than version 3.1.7.");
    _vaultSourceControl = new Vault3(this);
   }
  }
  private bool VaultVersionIs317OrBetter()
  {
   switch ( _forcedVaultVersion )
   {
    case EForcedVaultVersion.Vault3:
     Log.Debug("Vault version 3 forced.");
     return false;
    case EForcedVaultVersion.Vault317:
     Log.Debug("Vault version 3.1.7 forced");
     return true;
   }
   Assembly vaultExe = Assembly.LoadFile(Executable);
   AssemblyName vaultExeName = vaultExe.GetName();
   Log.Debug(Executable + " is version " + vaultExeName.Version.ToString());
   if ( vaultExeName.Version.Major > 3 )
    return true;
   if ( vaultExeName.Version.Major == 3 )
   {
    if ( vaultExeName.Version.Minor > 1 )
     return true;
    if ( vaultExeName.Version.Minor == 1 && vaultExeName.Version.Build >= 7 )
     return true;
   }
   return false;
  }
 }
}
