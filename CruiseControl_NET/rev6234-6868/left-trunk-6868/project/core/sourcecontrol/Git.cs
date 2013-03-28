using System.Diagnostics;
using System.IO;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 [ReflectorType("git")]
 public class Git : ProcessSourceControl
 {
  private const string historyFormat = "Commit:%H%nTime:%ci%nAuthor:%an%nE-Mail:%ae%nMessage:%s%n%n%b%nChanges:";
  private readonly IFileSystem _fileSystem;
  private readonly IFileDirectoryDeleter _fileDirectoryDeleter;
  private BuildProgressInformation _buildProgressInformation;
  [ReflectorProperty("autoGetSource", Required = false)]
  public bool AutoGetSource = true;
        [ReflectorProperty("executable", Required = false)]
  public string Executable = "git";
        [ReflectorProperty("repository", Required = true)]
  public string Repository;
        [ReflectorProperty("branch", Required = false)]
  public string Branch = "master";
        [ReflectorProperty("tagCommitMessage", Required = false)]
  public string TagCommitMessage = "CCNet Build {0}";
        [ReflectorProperty("tagNameFormat", Required = false)]
  public string TagNameFormat = "CCNet-Build-{0}";
        [ReflectorProperty("tagOnSuccess", Required = false)]
  public bool TagOnSuccess;
        [ReflectorProperty("commitBuildModifications", Required = false)]
  public bool CommitBuildModifications;
        [ReflectorProperty("commitUntrackedFiles", Required = false)]
  public bool CommitUntrackedFiles;
        [ReflectorProperty("committerName", Required = false)]
  public string CommitterName;
        [ReflectorProperty("committerEMail", Required = false)]
  public string CommitterEMail;
        [ReflectorProperty("workingDirectory", Required = false)]
  public string WorkingDirectory;
  public Git() : this(new GitHistoryParser(), new ProcessExecutor(), new SystemIoFileSystem(), new IoService()) { }
  public Git(IHistoryParser historyParser, ProcessExecutor executor, IFileSystem fileSystem, IFileDirectoryDeleter fileDirectoryDeleter)
   : base(historyParser, executor)
  {
   _fileSystem = fileSystem;
   _fileDirectoryDeleter = fileDirectoryDeleter;
  }
  public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   RepositoryAction result = CreateUpateLocalRepository(to);
   string originHeadHash = GitLogOriginHash(Branch, to);
   if (result == RepositoryAction.Updated && (originHeadHash == GitLogLocalHash(to)))
   {
    Log.Debug(string.Concat("[Git] Local and origin hash of branch '", Branch,
          "' matches, no modifications found. Current hash is '", originHeadHash, "'"));
    return new Modification[0];
   }
   return ParseModifications(GitLogHistory(Branch, from_, to), from_.StartTime, to.StartTime);
  }
  public override void GetSource(IIntegrationResult result)
  {
   if (!AutoGetSource)
    return;
   GitCheckoutRemoteBranch(Branch, result);
   GitClean(result);
  }
  public override void LabelSourceControl(IIntegrationResult result)
  {
   if (!TagOnSuccess || result.Failed)
    return;
   string tagName = string.Format(TagNameFormat, result.Label);
   string commitMessage = string.Format(TagCommitMessage, result.Label);
   if (CommitBuildModifications)
   {
    if (CommitUntrackedFiles)
     GitAddAll(result);
    GitCommitAll(commitMessage, result);
   }
   GitCreateTag(tagName, commitMessage, result);
   GitPushTag(tagName, result);
  }
  private string BaseWorkingDirectory(IIntegrationResult result)
  {
   return Path.GetFullPath(result.BaseFromWorkingDirectory(WorkingDirectory));
  }
  private BuildProgressInformation GetBuildProgressInformation(IIntegrationResult result)
  {
   if (_buildProgressInformation == null)
                _buildProgressInformation = result.BuildProgressInformation;
   return _buildProgressInformation;
  }
  private void ProcessExecutor_ProcessOutput(object sender, ProcessOutputEventArgs e)
  {
   if (_buildProgressInformation == null)
    return;
   if (e.OutputType == ProcessOutputType.ErrorOutput)
    return;
   _buildProgressInformation.AddTaskInformation(e.Data);
  }
  private RepositoryAction CreateUpateLocalRepository(IIntegrationResult result)
  {
   string workingDirectory = BaseWorkingDirectory(result);
   string gitRepositoryDirectory = Path.Combine(workingDirectory, ".git");
   if (!_fileSystem.DirectoryExists(workingDirectory))
   {
    Log.Debug(string.Concat("[Git] Working directory '", workingDirectory, "' does not exist."));
    GitClone(result);
    SetupLocalRepository(result);
    return RepositoryAction.Created;
   }
   if (!_fileSystem.DirectoryExists(gitRepositoryDirectory))
   {
    Log.Debug(string.Concat("[Git] Working directory '", workingDirectory,
          "' already exists, but it is not a git repository. Try deleting it and starting again."));
    _fileDirectoryDeleter.DeleteIncludingReadOnlyObjects(workingDirectory);
    return CreateUpateLocalRepository(result);
   }
   GitFetch(result);
   return RepositoryAction.Updated;
  }
  private void SetupLocalRepository(IIntegrationResult result)
  {
   if (!string.IsNullOrEmpty(CommitterName) && !string.IsNullOrEmpty(CommitterEMail))
   {
    GitConfigSet("user.name", CommitterName, result);
    GitConfigSet("user.email", CommitterEMail, result);
   }
   else if (string.IsNullOrEmpty(GitConfigGet("user.name", result)) || string.IsNullOrEmpty(GitConfigGet("user.email", result)))
   {
    Log.Warning("[Git] Properties 'committerName' and 'committerEMail' are not provided. They're required to use the 'TagOnSuccess' feature.");
   }
  }
  private ProcessInfo NewProcessInfo(string args, IIntegrationResult result)
  {
      return NewProcessInfo(args, result, ProcessPriorityClass.Normal, new int[] {0});
  }
        private ProcessInfo NewProcessInfo(string args, IIntegrationResult result, ProcessPriorityClass priority, int[] successExitCodes)
        {
            Log.Info(string.Concat("[Git] Calling git ", args));
            var processInfo = new ProcessInfo(Executable, args, BaseWorkingDirectory(result), priority,
                                                      successExitCodes);
            return processInfo;
        }
  private string GitLogOriginHash(string branchName, IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("log");
   buffer.AddArgument(string.Concat("origin/", branchName));
   buffer.AddArgument("--date-order");
   buffer.AddArgument("-1");
   buffer.AddArgument("--pretty=format:\"%H\"");
   return Execute(NewProcessInfo(buffer.ToString(), result)).StandardOutput.Trim();
  }
  private string GitLogLocalHash(IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("log");
   buffer.AddArgument("--date-order");
   buffer.AddArgument("-1");
   buffer.AddArgument("--pretty=format:\"%H\"");
   string hash = null;
   try
   {
    hash = Execute(NewProcessInfo(buffer.ToString(), result)).StandardOutput.Trim();
   }
   catch (CruiseControlException ex)
   {
    if (!ex.Message.Contains("fatal: bad default revision 'HEAD'"))
     throw;
   }
   return hash;
  }
  private ProcessResult GitLogHistory(string branchName, IIntegrationResult from_, IIntegrationResult to)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("log");
   buffer.AddArgument(string.Concat("origin/", branchName));
   buffer.AddArgument("--date-order");
   buffer.AddArgument("--name-status");
   buffer.AddArgument(string.Concat("--after=", from_.StartTime.ToUniversalTime().ToString("R")));
   buffer.AddArgument(string.Concat("--before=", to.StartTime.ToUniversalTime().ToString("R")));
   buffer.AddArgument(string.Concat("--pretty=format:", '"', historyFormat, '"'));
   return Execute(NewProcessInfo(buffer.ToString(), to));
  }
  private void GitClone(IIntegrationResult result)
  {
   string wd = BaseWorkingDirectory(result);
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("clone");
   buffer.AddArgument(Repository);
   buffer.AddArgument(wd);
   var bpi = GetBuildProgressInformation(result);
   bpi.SignalStartRunTask(string.Concat("git ", buffer.ToString()));
   ProcessExecutor.ProcessOutput += ProcessExecutor_ProcessOutput;
   ProcessInfo pi = NewProcessInfo(buffer.ToString(), result);
   pi.WorkingDirectory = Path.GetDirectoryName(wd.Trim().TrimEnd(Path.DirectorySeparatorChar));
   Execute(pi);
   ProcessExecutor.ProcessOutput -= ProcessExecutor_ProcessOutput;
  }
  private void GitConfigSet(string name, string value, IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("config");
   buffer.AddArgument(name);
   buffer.AddArgument(value);
   Execute(NewProcessInfo(buffer.ToString(), result));
  }
        private string GitConfigGet(string name, IIntegrationResult result)
  {
      ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
      buffer.AddArgument("config");
      buffer.AddArgument("--get");
      buffer.AddArgument(name);
      return
          Execute(NewProcessInfo(buffer.ToString(), result, ProcessPriorityClass.Normal, new int[] {0, 1, 2})).
              StandardOutput.Trim();
  }
  private void GitFetch(IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("fetch");
   buffer.AddArgument("origin");
   var bpi = GetBuildProgressInformation(result);
   bpi.SignalStartRunTask(string.Concat("git ", buffer.ToString()));
   ProcessExecutor.ProcessOutput += ProcessExecutor_ProcessOutput;
   Execute(NewProcessInfo(buffer.ToString(), result));
   ProcessExecutor.ProcessOutput -= ProcessExecutor_ProcessOutput;
  }
  private void GitCheckoutRemoteBranch(string branchName, IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("checkout");
   buffer.AddArgument("-q");
   buffer.AddArgument("-f");
   buffer.AddArgument(string.Concat("origin/", branchName));
   var bpi = GetBuildProgressInformation(result);
   bpi.SignalStartRunTask(string.Concat("git ", buffer.ToString()));
   ProcessExecutor.ProcessOutput += ProcessExecutor_ProcessOutput;
   Execute(NewProcessInfo(buffer.ToString(), result));
   ProcessExecutor.ProcessOutput -= ProcessExecutor_ProcessOutput;
  }
  private void GitClean(IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("clean");
   buffer.AddArgument("-d");
   buffer.AddArgument("-f");
   buffer.AddArgument("-x");
   Execute(NewProcessInfo(buffer.ToString(), result));
  }
  private void GitCommitAll(string commitMessage, IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("commit");
   buffer.AddArgument("--all");
   buffer.AddArgument("--allow-empty");
   buffer.AddArgument("-m", commitMessage);
   Execute(NewProcessInfo(buffer.ToString(), result));
  }
  private void GitAddAll(IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("add");
   buffer.AddArgument("--all");
   Execute(NewProcessInfo(buffer.ToString(), result));
  }
  private void GitCreateTag(string tagName, string tagMessage, IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("tag");
   buffer.AddArgument("-a");
   buffer.AddArgument("-m", tagMessage);
   buffer.AddArgument(tagName);
   Execute(NewProcessInfo(buffer.ToString(), result));
  }
  private void GitPushTag(string tagName, IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   buffer.AddArgument("push");
   buffer.AddArgument("origin");
   buffer.AddArgument("tag");
   buffer.AddArgument(tagName);
   var bpi = GetBuildProgressInformation(result);
   bpi.SignalStartRunTask(string.Concat("git ", buffer.ToString()));
   ProcessExecutor.ProcessOutput += ProcessExecutor_ProcessOutput;
   Execute(NewProcessInfo(buffer.ToString(), result));
   ProcessExecutor.ProcessOutput -= ProcessExecutor_ProcessOutput;
  }
  private enum RepositoryAction
  {
   Unknown = 0,
   Created = 1,
   Updated = 2
  }
 }
}
