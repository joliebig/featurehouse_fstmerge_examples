using System.IO;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.BitKeeper
{
 [ReflectorType("bitkeeper")]
 public class BitKeeper : ProcessSourceControl
 {
  public const string DefaultExecutable = @"C:\Program Files\BitKeeper\bk.exe";
  public BitKeeper(IHistoryParser parser, ProcessExecutor executor) : base(parser, executor)
  {}
  public BitKeeper() : base(new BitKeeperHistoryParser())
  {}
  [ReflectorProperty("executable", Required=false)]
  public string Executable = DefaultExecutable;
  [ReflectorProperty("workingDirectory", Required=true)]
  public string WorkingDirectory = string.Empty;
  [ReflectorProperty("tagOnSuccess", Required = false)]
  public bool TagOnSuccess = false;
  [ReflectorProperty("autoGetSource", Required = false)]
  public bool AutoGetSource = true;
  [ReflectorProperty("fileHistory", Required = false)]
  public bool FileHistory = false;
  [ReflectorProperty("cloneTo", Required = false)]
  public string CloneTo = string.Empty;
  public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   ProcessResult result = Execute(NewProcessInfo(BuildHistoryProcessArgs(), to));
   Modification[] modifications = ParseModifications(result, from_.StartTime, to.StartTime);
            base.FillIssueUrl(modifications);
            return modifications;
  }
  public override void LabelSourceControl(IIntegrationResult result)
  {
   if (TagOnSuccess && result.Succeeded)
   {
    Execute(NewLabelProcessInfo(result));
    Execute(NewProcessInfo(BuildPushProcessArgs(), result));
   }
  }
  public override void GetSource(IIntegrationResult result)
  {
            result.BuildProgressInformation.SignalStartRunTask("Getting source from_ BitKeeper");
   if (!AutoGetSource)
    return;
   ProcessInfo info = NewProcessInfo(BuildGetSourceArguments(), result);
   Log.Info(string.Format("Getting source from_ BitKeeper: {0} {1}", info.FileName, info.Arguments));
   Execute(info);
   Execute(NewProcessInfo(BuildPushProcessArgs(), result));
   if (CloneTo != string.Empty) CloneSource(result);
  }
  private void CloneSource(IIntegrationResult result)
  {
   string clonePath = CloneTo;
   if (!Path.IsPathRooted(clonePath))
    clonePath = Path.Combine(WorkingDirectory, CloneTo);
   clonePath = Path.GetFullPath(clonePath);
   DirectoryInfo di = new DirectoryInfo(clonePath);
   try
   {
    if (di.Exists)
     new IoService().DeleteIncludingReadOnlyObjects(clonePath);
   }
   catch
   {}
   ProcessInfo ctInfo = NewProcessInfo(BuildCloneToArguments(), result);
   Log.Info(string.Format("Cloning source to: {0}", clonePath));
   Execute(ctInfo);
  }
  private ProcessInfo NewLabelProcessInfo(IIntegrationResult result)
  {
   return NewProcessInfo(BuildTagProcessArgs(result.Label), result);
  }
  private string BuildTagProcessArgs(string label)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendArgument("tag");
   buffer.AppendArgument(label);
   return buffer.ToString();
  }
  private string BuildPushProcessArgs()
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendArgument("push");
   return buffer.ToString();
  }
  private string BuildHistoryProcessArgs()
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendArgument("changes");
   buffer.AppendArgument("-R");
   if (FileHistory)
    buffer.AppendArgument("-v");
   return buffer.ToString();
  }
  private string BuildGetSourceArguments()
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.Append("pull");
   return buffer.ToString();
  }
  private string BuildCloneToArguments()
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendArgument("clone");
   buffer.AppendArgument(".");
   buffer.AppendArgument(CloneTo);
   return buffer.ToString();
  }
  private ProcessInfo NewProcessInfo(string args, IIntegrationResult result)
  {
   ProcessInfo pi = new ProcessInfo(Executable, args, result.BaseFromWorkingDirectory(WorkingDirectory));
   pi.EnvironmentVariables.Add("PAGER", "cat");
   return pi;
  }
 }
}
