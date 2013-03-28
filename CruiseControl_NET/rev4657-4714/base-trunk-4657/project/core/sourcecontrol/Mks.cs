using System;
using System.Collections;
using System.IO;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 [ReflectorType("mks")]
 public class Mks : ProcessSourceControl
 {
  public const string DefaultExecutable = "si.exe";
  public const int DefaultPort = 8722;
  public const bool DefaultAutoGetSource = true;
  public Mks() : this(new MksHistoryParser(), new ProcessExecutor())
  {
  }
  public Mks(IHistoryParser parser, ProcessExecutor executor) : base(parser, executor)
  {
  }
  [ReflectorProperty("executable")]
  public string Executable = DefaultExecutable;
  [ReflectorProperty("user", Required = false)]
  public string User;
  [ReflectorProperty("password", Required = false)]
  public string Password;
  [ReflectorProperty("checkpointOnSuccess", Required = false)]
  public bool CheckpointOnSuccess;
  [ReflectorProperty("autoGetSource", Required=false)]
  public bool AutoGetSource = DefaultAutoGetSource;
  [ReflectorProperty("hostname")]
  public string Hostname;
  [ReflectorProperty("port", Required=false)]
  public int Port = DefaultPort;
  [ReflectorProperty("sandboxroot")]
  public string SandboxRoot;
  [ReflectorProperty("sandboxfile")]
  public string SandboxFile;
  public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   ProcessInfo info = NewProcessInfoWithArgs(BuildModsCommand());
   Log.Info(string.Format("Getting Modifications: {0} {1}", info.FileName, info.Arguments));
   Modification[] modifications = GetModifications(info, from_.StartTime, to.StartTime);
   AddMemberInfoToModifiedOrAddedModifications(modifications);
            base.FillIssueUrl(modifications);
   return ValidModifications(modifications, from_.StartTime, to.StartTime);
  }
  private Modification[] ValidModifications(Modification[] modifications, DateTime from_, DateTime to)
  {
   if (CheckpointOnSuccess) return modifications;
   ArrayList validModifications = new ArrayList();
   for (int i = 0; i < modifications.Length; i++)
   {
    if (from_ <= modifications[i].ModifiedTime && to >= modifications[i].ModifiedTime)
    {
     validModifications.Add(modifications[i]);
    }
   }
   return (Modification[]) validModifications.ToArray(typeof (Modification));
  }
  public override void LabelSourceControl(IIntegrationResult result)
  {
   if (CheckpointOnSuccess && result.Succeeded)
   {
    ProcessInfo checkpointProcess = NewProcessInfoWithArgs(BuildCheckpointCommand(result.Label));
    ExecuteWithLogging(checkpointProcess, "Adding Checkpoint");
   }
  }
  public override void GetSource(IIntegrationResult result)
  {
            result.BuildProgressInformation.SignalStartRunTask("Getting source from_ MKS");
   if (AutoGetSource)
   {
    ProcessInfo resynchProcess = NewProcessInfoWithArgs(BuildResyncCommand());
    ExecuteWithLogging(resynchProcess, "Resynchronising source");
    RemoveReadOnlyAttribute();
   }
  }
  private void AddMemberInfoToModifiedOrAddedModifications(Modification[] modifications)
  {
   for (int index = 0; index < modifications.Length; index++)
   {
    Modification modification = modifications[index];
    if ("Deleted" != modification.Type)
    {
     AddMemberInfo(modification);
    }
   }
  }
  private void AddMemberInfo(Modification modification)
  {
   ProcessInfo memberInfoProcess = NewProcessInfoWithArgs(BuildMemberInfoCommand(modification));
   ProcessResult result = Execute(memberInfoProcess);
   ((MksHistoryParser) historyParser).ParseMemberInfoAndAddToModification(modification, new StringReader(result.StandardOutput));
  }
  private void ExecuteWithLogging(ProcessInfo processInfo, string comment)
  {
   Log.Info(string.Format(comment + " : {0} {1}", processInfo.FileName, processInfo.Arguments));
   Execute(processInfo);
  }
  private string BuildResyncCommand()
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendArgument("resync");
   buffer.AppendArgument("--overwriteChanged");
   buffer.AppendArgument("--restoreTimestamp");
   AppendCommonArguments(buffer, true);
   return buffer.ToString();
  }
  private string BuildCheckpointCommand(string label)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendArgument("checkpoint");
   buffer.AppendArgument("-d \"Cruise Control.Net Build - {0}\"", label);
   buffer.AppendArgument("-L \"Build - {0}\"", label);
   AppendCommonArguments(buffer, true);
   return buffer.ToString();
  }
  private string BuildModsCommand()
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendArgument("mods");
   AppendCommonArguments(buffer, true);
   return buffer.ToString();
  }
  private string BuildMemberInfoCommand(Modification modification)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendArgument("memberinfo");
   AppendCommonArguments(buffer, false);
   string modificationPath = (modification.FolderName == null) ? SandboxRoot : Path.Combine(SandboxRoot, modification.FolderName);
   buffer.AddArgument(Path.Combine(modificationPath, modification.FileName));
   return buffer.ToString();
  }
  private void AppendCommonArguments(ProcessArgumentBuilder buffer, bool recurse)
  {
   if (recurse)
   {
    buffer.AppendArgument("-R");
   }
   buffer.AddArgument("-S", Path.Combine(SandboxRoot, SandboxFile));
   buffer.AppendArgument("--user={0}", User);
   buffer.AppendArgument("--password={0}", Password);
   buffer.AppendArgument("--quiet");
  }
  private void RemoveReadOnlyAttribute()
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("-R");
   buffer.AddArgument("/s", SandboxRoot + "\\*");
   Execute(new ProcessInfo("attrib", buffer.ToString()));
  }
  private ProcessInfo NewProcessInfoWithArgs(string args)
  {
   return new ProcessInfo(Executable, args);
  }
 }
}
