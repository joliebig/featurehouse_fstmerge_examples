using System;
using System.Globalization;
using System.IO;
using System.Text;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 [ReflectorType("vss")]
 public class Vss : ProcessSourceControl
 {
  public const string DefaultProject = "$/";
  public const string SS_DIR_KEY = "SSDIR";
  public const string SS_REGISTRY_PATH = @"Software\\Microsoft\\SourceSafe";
  public const string SS_REGISTRY_KEY = "SCCServerPath";
  public const string SS_EXE = "ss.exe";
  private const string RecursiveCommandLineOption = "-R";
  private IRegistry registry;
  private string ssDir;
  private string executable;
  private string tempLabel;
  private IVssLocale locale;
  public Vss() : this(new VssLocale(CultureInfo.CurrentCulture))
  {}
  private Vss(IVssLocale locale) : this(locale, new VssHistoryParser(locale), new ProcessExecutor(), new Registry())
  {}
  public Vss(IVssLocale locale, IHistoryParser historyParser, ProcessExecutor executor, IRegistry registry) : base(historyParser, executor)
  {
   this.registry = registry;
   this.locale = locale;
  }
  [ReflectorProperty("executable", Required = false)]
  public string Executable
  {
   get
   {
    if (executable == null)
     executable = GetExecutableFromRegistry();
    return executable;
   }
   set { executable = value; }
  }
        [ReflectorProperty("project", Required = false)]
  public string Project = DefaultProject;
        [ReflectorProperty("username", Required = false)]
  public string Username;
        [ReflectorProperty("password", typeof(PrivateStringSerialiserFactory), Required = false)]
  public PrivateString Password;
        [ReflectorProperty("ssdir", Required = false)]
  public string SsDir
  {
   get { return ssDir; }
   set { ssDir = StringUtil.StripQuotes(value); }
  }
        [ReflectorProperty("applyLabel", Required = false)]
  public bool ApplyLabel = false;
        [ReflectorProperty("autoGetSource", Required = false)]
  public bool AutoGetSource = true;
        [ReflectorProperty("alwaysGetLatest", Required = false)]
  public bool AlwaysGetLatest = false;
        [ReflectorProperty("workingDirectory", Required = false)]
  public string WorkingDirectory;
        [ReflectorProperty("culture", Required = false)]
  public string Culture
  {
   get { return locale.ServerCulture; }
   set { locale.ServerCulture = value; }
  }
        [ReflectorProperty("cleanCopy", Required = false)]
  public bool CleanCopy = true;
     public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
            string tempOutputFileName = Path.GetTempFileName();
         return GetModifications(from_, to, tempOutputFileName);
        }
        public Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to, string tempOutputFileName)
        {
            try
            {
                Execute(CreateHistoryProcessInfo(from_, to, tempOutputFileName));
                TextReader outputReader = new StreamReader(tempOutputFileName, Encoding.Default);
                try
                {
                    Modification[] modifications = ParseModifications(outputReader, from_.StartTime, to.StartTime);
                    base.FillIssueUrl(modifications);
                    return modifications;
                }
                finally
                {
                    outputReader.Close();
                }
            }
            finally
            {
                File.Delete(tempOutputFileName);
            }
  }
  public override void LabelSourceControl(IIntegrationResult result)
  {
   if (! ApplyLabel) return;
   Execute(NewProcessInfoWith(LabelProcessInfoArgs(result), result));
   tempLabel = null;
  }
  public override void GetSource(IIntegrationResult result)
  {
            result.BuildProgressInformation.SignalStartRunTask("Getting source from_ VSS");
   CreateTemporaryLabel(result);
   if (! AutoGetSource) return;
   Log.Info("Getting source from_ VSS");
   Execute(NewProcessInfoWith(GetSourceArgs(result), result));
  }
        private PrivateArguments GetSourceArgs(IIntegrationResult result)
  {
            var builder = new PrivateArguments();
   builder.Add("get ", Project + "/*?*", true);
   builder.Add(RecursiveCommandLineOption);
   builder.AddIf(ApplyLabel, "-VL", tempLabel);
   builder.AddIf(!AlwaysGetLatest, "-Vd", locale.FormatCommandDate(result.StartTime));
   AppendUsernameAndPassword(builder);
   builder.Add("-I-N -W -GF- -GTM");
   builder.AddIf(CleanCopy, "-GWR");
   return builder;
  }
  private ProcessInfo CreateHistoryProcessInfo(IIntegrationResult from_, IIntegrationResult to, string tempOutputFileName)
  {
   return NewProcessInfoWith(HistoryProcessInfoArgs(from_.StartTime, to.StartTime, tempOutputFileName), to);
  }
        private PrivateArguments HistoryProcessInfoArgs(DateTime from_, DateTime to, string tempOutputFileName)
  {
   var builder = new PrivateArguments();
   builder.Add("history ", Project, true);
   builder.Add(RecursiveCommandLineOption);
   builder.Add(string.Format("-Vd{0}~{1}", locale.FormatCommandDate(to), locale.FormatCommandDate(from_)));
   AppendUsernameAndPassword(builder);
   builder.Add("-I-Y");
            builder.Add(null, "-O@" + tempOutputFileName, true);
            return builder;
  }
  private void CreateTemporaryLabel(IIntegrationResult result)
  {
   if (ApplyLabel)
   {
    tempLabel = CreateTemporaryLabelName(result.StartTime);
    LabelSourceControlWith(tempLabel, result);
   }
  }
  private void LabelSourceControlWith(string label, IIntegrationResult result)
  {
   Execute(NewProcessInfoWith(LabelProcessInfoArgs(label, null), result));
  }
        private PrivateArguments LabelProcessInfoArgs(IIntegrationResult result)
  {
   if (result.Succeeded)
   {
    return LabelProcessInfoArgs(result.Label, tempLabel);
   }
   else
   {
    return DeleteLabelProcessInfoArgs();
   }
  }
        private PrivateArguments DeleteLabelProcessInfoArgs()
  {
   return LabelProcessInfoArgs(string.Empty, tempLabel);
  }
  private PrivateArguments LabelProcessInfoArgs(string label, string oldLabel)
  {
            var builder = new PrivateArguments();
   builder.Add("label ", Project, true);
   builder.Add("-L", label);
            builder.AddIf(!string.IsNullOrEmpty(oldLabel), "-VL", oldLabel);
   AppendUsernameAndPassword(builder);
   builder.Add("-I-Y");
            return builder;
  }
  private string CreateTemporaryLabelName(DateTime time)
  {
   return "CCNETUNVERIFIED" + time.ToString("MMddyyyyHHmmss");
  }
  private string GetExecutableFromRegistry()
  {
   string comServerPath = registry.GetExpectedLocalMachineSubKeyValue(SS_REGISTRY_PATH, SS_REGISTRY_KEY);
   return Path.Combine(Path.GetDirectoryName(comServerPath), SS_EXE);
  }
  private ProcessInfo NewProcessInfoWith(PrivateArguments args, IIntegrationResult result)
  {
   string workingDirectory = result.BaseFromWorkingDirectory(WorkingDirectory);
   if (! Directory.Exists(workingDirectory)) Directory.CreateDirectory(workingDirectory);
   ProcessInfo processInfo = new ProcessInfo(Executable, args, workingDirectory);
   if (SsDir != null)
   {
    processInfo.EnvironmentVariables[SS_DIR_KEY] = SsDir;
   }
   return processInfo;
  }
        private void AppendUsernameAndPassword(PrivateArguments builder)
  {
            if (!string.IsNullOrEmpty(Username))
            {
                PrivateString userPlusPass = "\"-Y" + Username + "," + Password.PrivateValue + "\"";
                builder.Add(userPlusPass);
            }
  }
 }
}
