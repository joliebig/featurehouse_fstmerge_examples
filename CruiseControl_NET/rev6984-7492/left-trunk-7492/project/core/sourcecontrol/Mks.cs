namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("mks")]
 public class Mks : ProcessSourceControl
 {
  public const string DefaultExecutable = "si.exe";
  public const int DefaultPort = 8722;
  public const bool DefaultAutoGetSource = true;
  public const bool DefaultAutoDisconnect = false;
  private static int usageCount = 0;
  private static object usageCountLock = new object();
  public Mks() : this(new MksHistoryParser(), new ProcessExecutor())
  {
  }
  public Mks(IHistoryParser parser, ProcessExecutor executor) : base(parser, executor)
  {
            this.Executable = DefaultExecutable;
            this.AutoGetSource = DefaultAutoGetSource;
            this.AutoDisconnect = DefaultAutoDisconnect;
            this.Port = DefaultPort;
  }
        [ReflectorProperty("executable")]
        public string Executable { get; set; }
        [ReflectorProperty("user", Required = false)]
        public string User { get; set; }
        [ReflectorProperty("password", Required = false)]
        public string Password { get; set; }
        [ReflectorProperty("checkpointOnSuccess", Required = false)]
        public bool CheckpointOnSuccess { get; set; }
        [ReflectorProperty("autoGetSource", Required = false)]
        public bool AutoGetSource { get; set; }
        [ReflectorProperty("autoDisconnect", Required = false)]
        public bool AutoDisconnect { get; set; }
        [ReflectorProperty("hostname")]
        public string Hostname { get; set; }
        [ReflectorProperty("port", Required = false)]
        public int Port { get; set; }
        [ReflectorProperty("sandboxroot")]
        public string SandboxRoot { get; set; }
        [ReflectorProperty("sandboxfile")]
        public string SandboxFile { get; set; }
  public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   IncreaseUsageCount();
            ProcessInfo info = NewProcessInfoWithArgs(BuildSandboxModsCommand());
            Log.Info(string.Format("Getting Modifications (mods): {0} {1}", info.FileName, info.Arguments));
            Modification[] modifications = GetModifications(info, from_.StartTime, to.StartTime);
            AddMemberInfoToModifiedOrAddedModifications(modifications);
            if (!this.CheckpointOnSuccess)
            {
                modifications = FilterOnTimeframe(modifications, from_.StartTime, to.StartTime);
            }
            DecreaseUsageCount();
      return modifications;
  }
  public override void LabelSourceControl(IIntegrationResult result)
  {
   IncreaseUsageCount();
   if (CheckpointOnSuccess && result.Succeeded)
   {
    ProcessInfo checkpointProcess = NewProcessInfoWithArgs(BuildCheckpointCommand(result.Label));
    ExecuteWithLogging(checkpointProcess, "Adding Checkpoint");
   }
   DecreaseUsageCount();
  }
  public override void GetSource(IIntegrationResult result)
  {
   IncreaseUsageCount();
   if (AutoGetSource)
   {
    ProcessInfo resynchProcess = NewProcessInfoWithArgs(BuildResyncCommand());
    ExecuteWithLogging(resynchProcess, "Resynchronizing source");
    RemoveReadOnlyAttribute();
   }
   DecreaseUsageCount();
  }
  private void AddMemberInfoToModifiedOrAddedModifications(Modification[] modifications)
  {
   for (int index = 0; index < modifications.Length; index++)
   {
    Modification modification = modifications[index];
    if ("deleted" != modification.Type)
    {
     AddMemberInfo(modification);
    }
   }
  }
        private Modification[] FilterOnTimeframe(Modification[] modifications, DateTime from_, DateTime to)
        {
            List<Modification> mods = new List<Modification>();
            for (int index = 0; index < modifications.Length; index++)
            {
                Modification modification = modifications[index];
                if ( modification.ModifiedTime >= from_ && modification.ModifiedTime <= to )
                {
                    mods.Add(modification);
                }
                else if (modification.Type == "deleted")
                {
                    mods.Add(modification);
                }
            }
            return mods.ToArray();
        }
  private void AddMemberInfo(Modification modification)
  {
            ProcessInfo memberInfoProcess = NewProcessInfoWithArgs(BuildMemberInfoCommandXml(modification));
   ProcessResult result = Execute(memberInfoProcess);
   ((MksHistoryParser) historyParser).ParseMemberInfoAndAddToModification(modification, new StringReader(result.StandardOutput));
  }
  private void ExecuteWithLogging(ProcessInfo processInfo, string comment)
  {
   Log.Info(string.Format(comment + " : {0} {1}", processInfo.FileName, processInfo.PublicArguments));
   Execute(processInfo);
  }
  private string BuildResyncCommand()
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendArgument("resync");
   buffer.AppendArgument("--overwriteChanged");
   buffer.AppendArgument("--restoreTimestamp");
            buffer.AppendArgument("--forceConfirm=yes");
            buffer.AppendArgument("--includeDropped");
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
        private string BuildSandboxModsCommand()
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.AppendArgument("viewsandbox --nopersist --filter=changed:all --xmlapi");
            AppendCommonArguments(buffer, true);
            return buffer.ToString();
        }
        private string BuildMemberInfoCommandXml(Modification modification)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.AppendArgument("memberinfo --xmlapi");
            AppendCommonArguments(buffer, false, true);
            string modificationPath = (modification.FolderName == null) ? SandboxRoot : Path.Combine(SandboxRoot, modification.FolderName);
            buffer.AddArgument(Path.Combine(modificationPath, modification.FileName));
            return buffer.ToString();
        }
  private void AppendCommonArguments(ProcessArgumentBuilder buffer, bool recurse)
  {
            AppendCommonArguments(buffer, recurse, false);
  }
        private void AppendCommonArguments(ProcessArgumentBuilder buffer, bool recurse, bool omitSandbox)
        {
            if (recurse)
            {
                buffer.AppendArgument("-R");
            }
            if (!omitSandbox)
            {
                buffer.AddArgument("-S", Path.Combine(SandboxRoot, SandboxFile));
            }
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
  private void IncreaseUsageCount()
  {
   lock(usageCountLock)
   {
    usageCount++;
   }
  }
  private void DecreaseUsageCount()
  {
   lock(usageCountLock)
   {
    usageCount--;
    if(AutoDisconnect && (usageCount == 0))
    {
      ProcessInfo info = NewProcessInfoWithArgs(BuildDisconnectCommand());
      ExecuteWithLogging(info, "Disconnecting from_ server");
    }
   }
  }
  private string BuildDisconnectCommand()
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AppendArgument("disconnect");
   buffer.AppendArgument("--user={0}", User);
   buffer.AppendArgument("--password={0}", Password);
   buffer.AppendArgument("--quiet");
   buffer.AppendArgument("--forceConfirm=yes");
   return buffer.ToString();
  }
 }
}
