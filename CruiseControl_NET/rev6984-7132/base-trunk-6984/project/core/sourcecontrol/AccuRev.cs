using System;
using System.Collections.Specialized;
using System.Globalization;
using System.IO;
using System.Text.RegularExpressions;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 [ReflectorType("accurev")]
 public class AccuRev : ProcessSourceControl
    {
  [ReflectorProperty("autoGetSource", Required = false)]
  public bool AutoGetSource = false;
        [ReflectorProperty("executable", Required = false)]
  public string Executable = "accurev.exe";
        [ReflectorProperty("homeDir", Required = false)]
  public string AccuRevHomeDir = null;
        [ReflectorProperty("labelOnSuccess", Required = false)]
  public bool LabelOnSuccess = false;
        [ReflectorProperty("login", Required = false)]
  public bool LogIn = false;
        [ReflectorProperty("password", typeof(PrivateStringSerialiserFactory), Required = false)]
        public PrivateString AccuRevPassword = null;
        [ReflectorProperty("principal", Required = false)]
  public string AccuRevPrincipal = null;
        [ReflectorProperty("workspace", Required = false)]
  public string Workspace = string.Empty;
        internal Modification[] mods = new Modification[0];
  public AccuRev() : this(new AccuRevHistoryParser(), new ProcessExecutor())
  {
  }
  public AccuRev(ProcessExecutor executor) : this(new AccuRevHistoryParser(), executor)
  {
  }
  public AccuRev(IHistoryParser parser, ProcessExecutor executor) : base(parser, executor)
  {
   if (LogIn & ((AccuRevPrincipal == null) || (AccuRevPassword == null)))
   {
    Log.Error("login=true requires principal= and password= to be specified.");
   }
  }
  private static string FormatCommandDate(DateTime date)
  {
   return date.ToString("yyyy\\/MM\\/dd HH\\:mm\\:ss", CultureInfo.InvariantCulture);
  }
  private string GetBasisStreamName(IIntegrationResult result)
  {
   string line;
   Regex findBasisRegex = new Regex(@"^\s*Basis:\s+(.+)$");
   PossiblyLogIn(result);
   ProcessResult cmdResults = RunCommand("info", result);
   StringReader infoStdOut = new StringReader(cmdResults.StandardOutput);
   while ((line = infoStdOut.ReadLine()) != null)
   {
    Match parsed = findBasisRegex.Match(line);
    if (parsed.Success)
     return parsed.Groups[1].ToString().Trim();
   }
   Log.Error(string.Format("No \"Basis:\" line found in output from_ AccuRev \"accurev info\": {0}", cmdResults.StandardOutput));
   return string.Empty;
  }
  public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   PossiblyLogIn(from_);
   string args = string.Format("hist -a -s \"{0}\" -t \"{1}-{2}\"",
    GetBasisStreamName(to),
    FormatCommandDate(to.StartTime),
    FormatCommandDate(from_.StartTime));
   ProcessInfo histCommand = PrepCommand(args, from_);
            Modification[] mods = base.GetModifications(histCommand, from_.StartTime, to.StartTime);
            base.FillIssueUrl(mods);
            return mods;
  }
  public override void GetSource(IIntegrationResult result)
  {
            result.BuildProgressInformation.SignalStartRunTask("Getting source from_ AccuRev");
   if (AutoGetSource)
   {
       string command = "update";
    PossiblyLogIn(result);
                var lastChangeNumber = Modification.GetLastChangeNumber(mods);
       int lastChange = int.Parse(lastChangeNumber ?? "0");
                if (lastChange != 0) command = command + " -t " + lastChange;
                RunCommand(command, result);
   }
  }
  public override void LabelSourceControl(IIntegrationResult result)
  {
   if (LabelOnSuccess && result.Succeeded && (result.Label != string.Empty))
   {
    PossiblyLogIn(result);
    string args = string.Format("mksnap -s \"{0}\" -b \"{1}\" -t \"{2}\"",
     result.Label,
     GetBasisStreamName(result),
     FormatCommandDate(result.StartTime));
    RunCommand(args, result);
   }
  }
        private void PossiblyLogIn(IIntegrationResult result)
  {
   if (!LogIn)
    return;
   if ((AccuRevPrincipal == null) || (AccuRevPassword == null))
   {
    Log.Error("login=true requires principal= and password= to be specified.");
    return;
   }
            RunCommand(new PrivateArguments("login", AccuRevPrincipal, AccuRevPassword), result);
  }
        private ProcessInfo PrepCommand(PrivateArguments args, IIntegrationResult result)
  {
   Log.Debug(string.Format("Preparing to run AccuRev command: {0} {1}", Executable, args));
   ProcessInfo command = new ProcessInfo(Executable, args, result.BaseFromWorkingDirectory(Workspace));
   SetEnvironmentVariables(command.EnvironmentVariables, result);
   return command;
  }
  private ProcessResult RunCommand(PrivateArguments args, IIntegrationResult result)
  {
   ProcessInfo command = PrepCommand(args, result);
   ProcessResult cmdResults = Execute(command);
   if (cmdResults.Failed)
   {
    Log.Error(string.Format("AccuRev command \"{0} {1}\" failed with RC={2}",
     Executable, args, cmdResults.ExitCode));
    if ((cmdResults.StandardError != null) && (cmdResults.StandardError != string.Empty))
     Log.Error(string.Format("\tError output: {0}", cmdResults.StandardError));
   }
   return cmdResults;
  }
  private void SetEnvironmentVariables(StringDictionary environmentVariables, IIntegrationResult result)
  {
            if (!string.IsNullOrEmpty(AccuRevHomeDir))
    environmentVariables["ACCUREV_HOME"] = result.BaseFromArtifactsDirectory(AccuRevHomeDir);
            if (!string.IsNullOrEmpty(AccuRevPrincipal))
    environmentVariables["ACCUREV_PRINCIPAL"] = AccuRevPrincipal;
  }
 }
}
