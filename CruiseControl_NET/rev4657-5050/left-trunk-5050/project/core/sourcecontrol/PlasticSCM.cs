using System.Globalization;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 [ReflectorType("plasticscm")]
 public class PlasticSCM : ProcessSourceControl
 {
  public const string DefaultPlasticExecutable = "cm";
  public const char DELIMITER = '?';
  public const string DATEFORMAT = "dd/MM/yyyy HH:mm:ss";
  public static string FORMAT = DELIMITER + "{item}" + DELIMITER + "{owner}" + DELIMITER + "{date}" + DELIMITER + "{changeset}";
  public PlasticSCM() : this(new PlasticSCMHistoryParser(), new ProcessExecutor())
  {
  }
  public PlasticSCM(IHistoryParser parser, ProcessExecutor executor)
   : base(parser, executor)
  {
  }
        [ReflectorProperty("autoGetSource", Required = false)]
        public bool AutoGetSource = true;
        [ReflectorProperty("executable", Required=false)]
  public string Executable = DefaultPlasticExecutable;
  [ReflectorProperty("branch", Required=true)]
  public string Branch = string.Empty;
  [ReflectorProperty("repository", Required=false)]
  public string Repository = string.Empty;
        [ReflectorProperty("workingDirectory", Required = false)]
  public string WorkingDirectory = string.Empty;
        [ReflectorProperty("labelOnSuccess", Required = false)]
  public bool LabelOnSuccess = false;
  [ReflectorProperty("labelPrefix", Required=false)]
  public string LabelPrefix = "ccver-";
  [ReflectorProperty("forced", Required=false)]
  public bool Forced = false;
  public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   Execute(GoToBranchProcessInfo(from_));
            Modification[] modifications = GetModifications(CreateQueryProcessInfo(from_, to), from_.StartTime, to.StartTime);
            base.FillIssueUrl(modifications);
            return modifications;
  }
  public override void LabelSourceControl(IIntegrationResult result)
  {
   if (LabelOnSuccess && result.Succeeded)
   {
                Execute(CreateLabelProcessInfo(result));
                Execute(LabelProcessInfo(result));
   }
  }
  public override void GetSource(IIntegrationResult result)
  {
            result.BuildProgressInformation.SignalStartRunTask("Getting source from_ PlasticSCM");
            if (AutoGetSource)
            {
                Execute(GoToBranchProcessInfo(result));
                Execute(NewGetSourceProcessInfo(result));
            }
  }
  public ProcessInfo NewGetSourceProcessInfo(IIntegrationResult result)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
            builder.AppendArgument(string.Format("update {0}", result.BaseFromWorkingDirectory(WorkingDirectory)));
   if (Forced)
   {
    builder.AppendArgument("--forced");
   }
   return NewProcessInfoWithArgs(result, builder.ToString());
  }
  public ProcessInfo GoToBranchProcessInfo(IIntegrationResult result)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   builder.AppendArgument(string.Format("stb {0}", Branch));
   if (Repository != string.Empty)
   {
    builder.AppendArgument(string.Format("-repository={0}", Repository));
   }
   builder.AppendArgument("--noupdate");
   return NewProcessInfoWithArgs(result, builder.ToString());
  }
  public ProcessInfo CreateQueryProcessInfo(IIntegrationResult from_, IIntegrationResult to)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   builder.AppendArgument(
    string.Format("find revision where branch = '{0}' "+
         "and revno != 'CO' "+
         "and date between '{1}' and '{2}'",
    Branch, from_.StartTime.ToString(DATEFORMAT, CultureInfo.InvariantCulture), to.StartTime.ToString(DATEFORMAT, CultureInfo.InvariantCulture)));
   if (Repository != string.Empty)
   {
    builder.AppendArgument(string.Format("on repository '{0}'", Repository));
   }
   builder.AppendArgument(string.Format("--dateformat=\"{0}\"", DATEFORMAT));
   builder.AppendArgument(string.Format("--format=\"{0}\"", FORMAT));
   return NewProcessInfoWithArgs(from_, builder.ToString());
  }
  public ProcessInfo CreateLabelProcessInfo(IIntegrationResult result)
  {
   string labelName = LabelPrefix + result.Label;
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendArgument(string.Format("mklb {0}", labelName));
   return NewProcessInfoWithArgs(result, buffer.ToString());
  }
  public ProcessInfo LabelProcessInfo(IIntegrationResult result)
  {
   string labelName = LabelPrefix + result.Label;
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendArgument(string.Format("label -R lb:{0} .", labelName));
   return NewProcessInfoWithArgs(result, buffer.ToString());
  }
  private ProcessInfo NewProcessInfoWithArgs(IIntegrationResult result, string args)
  {
   return new ProcessInfo(Executable, args, result.BaseFromWorkingDirectory(WorkingDirectory));
  }
 }
}
